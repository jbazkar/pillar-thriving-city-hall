#!/usr/bin/env python3
"""
Batch-run Perplexity deep research on all prompts under 05_prompts/research.

Notes
- No external deps: uses urllib and a simple .env loader.
- Uses model `sonar-deep-research` by default.
- Skips prompts whose outputs already exist unless --overwrite.
"""

from __future__ import annotations

import argparse
import json
import os
import sys
from pathlib import Path
from time import sleep
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError


def load_env_file(path: Path) -> None:
    if not path.exists():
        return
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        if "=" in line:
            k, v = line.split("=", 1)
            if k and k.strip() not in os.environ:
                os.environ[k.strip()] = v.strip()


def call_perplexity(api_key: str, content: str, model: str, timeout: int = 300) -> dict:
    url = os.getenv("PERPLEXITY_BASE_URL", "https://api.perplexity.ai/v1/sonar")
    payload = {
        "model": model,
        "messages": [{"role": "user", "content": content}],
    }
    data = json.dumps(payload).encode("utf-8")
    req = Request(url, data=data, method="POST")
    req.add_header("Authorization", f"Bearer {api_key}")
    req.add_header("Content-Type", "application/json")
    with urlopen(req, timeout=timeout) as resp:
        return json.loads(resp.read().decode("utf-8"))


def extract_output_text(raw: dict) -> str:
    try:
        if isinstance(raw.get("choices"), list) and raw["choices"]:
            return raw["choices"][0].get("message", {}).get("content") or ""
        if isinstance(raw.get("output_text"), str):
            return raw["output_text"]
    except Exception:
        pass
    return ""


def run_batch(prompts: list[Path], overwrite: bool = False, delay: float = 1.0, model: str = "sonar-deep-research") -> list[tuple[Path, str]]:
    out_dir = Path("05_prompts/research-output")
    out_dir.mkdir(parents=True, exist_ok=True)
    api_key = os.getenv("PERPLEXITY_API_KEY")
    if not api_key:
        raise SystemExit("PERPLEXITY_API_KEY not set. Add it to 05_prompts/perplexity_runner/.env or export it.")

    results: list[tuple[Path, str]] = []
    for p in prompts:
        base = p.stem
        md_path = out_dir / f"{base}.md"
        json_path = out_dir / f"{base}.json"
        if not overwrite and (md_path.exists() or json_path.exists()):
            results.append((p, "skipped (exists)"))
            continue

        content = p.read_text(encoding="utf-8").strip()
        try:
            raw = call_perplexity(api_key, content, model)
            output_text = extract_output_text(raw)
            json_path.write_text(json.dumps(raw, indent=2), encoding="utf-8")
            md_path.write_text(output_text, encoding="utf-8")
            results.append((p, "ok"))
        except HTTPError as e:
            body = None
            try:
                body = e.read().decode("utf-8")
            except Exception:
                pass
            results.append((p, f"http_error {e.code}: {e.reason} {body!r}"))
        except URLError as e:
            results.append((p, f"network_error: {e}"))
        except Exception as e:
            results.append((p, f"error: {e}"))

        # Gentle delay to reduce rate-limit risk
        if delay > 0:
            sleep(delay)

    return results


def main() -> int:
    parser = argparse.ArgumentParser(description="Run deep research on all prompts in a folder")
    parser.add_argument("prompts_dir", nargs="?", default="05_prompts/research")
    parser.add_argument("--overwrite", action="store_true", help="Overwrite existing outputs")
    parser.add_argument("--limit", type=int, default=0, help="Limit number of prompts (0 = all)")
    parser.add_argument("--delay", type=float, default=1.0, help="Delay between calls in seconds")
    parser.add_argument("--model", type=str, default=os.getenv("PERPLEXITY_MODEL", "sonar-deep-research"))
    args = parser.parse_args()

    # Load API key from runner .env if present
    load_env_file(Path("05_prompts/perplexity_runner/.env"))

    prompts_path = Path(args.prompts_dir)
    if not prompts_path.exists() or not prompts_path.is_dir():
        print(f"Not a directory: {prompts_path}", file=sys.stderr)
        return 2

    all_prompts = sorted(p for p in prompts_path.iterdir() if p.suffix == ".txt")
    # Prefer prompts without outputs when limiting
    out_dir = Path("05_prompts/research-output")
    def has_outputs(p: Path) -> bool:
        base = p.stem
        return (out_dir / f"{base}.md").exists() or (out_dir / f"{base}.json").exists()

    if args.limit and args.limit > 0:
        pending = [p for p in all_prompts if not has_outputs(p)]
        if not pending:
            to_run = []
        else:
            to_run = pending[: args.limit]
    else:
        to_run = all_prompts

    results = run_batch(to_run, overwrite=args.overwrite, delay=args.delay, model=args.model)
    for p, status in results:
        print(f"{p.name}: {status}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
