import argparse
import os
from pathlib import Path
import json
import yaml
from dotenv import load_dotenv

from px_client import PerplexityClient


def load_config(cfg_path: Path) -> dict:
    with cfg_path.open("r", encoding="utf-8") as f:
        return yaml.safe_load(f)


def deep_merge(a: dict, b: dict) -> dict:
    """Recursively merge dict b into a and return a copy."""
    out = dict(a or {})
    for k, v in (b or {}).items():
        if (
            isinstance(out.get(k), dict)
            and isinstance(v, dict)
        ):
            out[k] = deep_merge(out[k], v)
        else:
            out[k] = v
    return out


def parse_front_matter(text: str) -> tuple[dict, str]:
    """If text starts with YAML front matter (---), parse and return (meta, body)."""
    if text.startswith("---\n"):
        end = text.find("\n---\n", 4)
        if end != -1:
            fm_text = text[4:end]
            body = text[end + 5 :]
            try:
                meta = yaml.safe_load(fm_text) or {}
            except Exception:
                meta = {}
            return meta, body.strip()
    return {}, text


def main():
    parser = argparse.ArgumentParser(description="Run one research prompt with Perplexity")
    parser.add_argument("prompt_path", type=str, help="Path to a .txt prompt file")
    parser.add_argument("--config", type=str, default=str(Path(__file__).parent / "config.yaml"))
    args = parser.parse_args()

    load_dotenv(Path(__file__).parent / ".env")

    cfg = load_config(Path(args.config))
    prompt_file = Path(args.prompt_path)
    if not prompt_file.exists():
        raise SystemExit(f"Prompt not found: {prompt_file}")

    raw_text = prompt_file.read_text(encoding="utf-8").strip()
    fm_meta, input_text = parse_front_matter(raw_text)

    # Per-prompt YAML override: <prompt>.yaml
    per_prompt_yaml = prompt_file.with_suffix(".yaml")
    per_cfg = {}
    if per_prompt_yaml.exists():
        per_cfg = load_config(per_prompt_yaml)

    # Merge order: base cfg <- per-prompt YAML <- front matter meta
    eff_cfg = deep_merge(cfg, per_cfg)
    eff_cfg = deep_merge(eff_cfg, fm_meta)

    client = PerplexityClient()
    response = client.create_response(
        preset=eff_cfg.get("preset", "pro-search"),
        input_text=input_text,
        instructions=eff_cfg.get("instructions"),
        model=eff_cfg.get("model", "sonar-pro"),
        search_domain_filter=eff_cfg.get("search_domain_filter") or None,
        web_search_options=eff_cfg.get("web_search_options") or None,
    )

    out_dir = Path(eff_cfg.get("io", {}).get("output_dir", "05_prompts/research-output"))
    out_dir.mkdir(parents=True, exist_ok=True)

    base = prompt_file.stem
    md_path = out_dir / f"{base}.md"
    json_path = out_dir / f"{base}.json"

    if (md_path.exists() or json_path.exists()) and not eff_cfg.get("io", {}).get("overwrite", False):
        raise SystemExit(f"Output exists. Set io.overwrite=true or delete: {md_path} {json_path}")

    output_text = response.get("output_text", "")
    md_path.write_text(output_text, encoding="utf-8")
    json_path.write_text(json.dumps(response, indent=2), encoding="utf-8")

    print(f"Saved: {md_path}\nSaved: {json_path}")


if __name__ == "__main__":
    main()
