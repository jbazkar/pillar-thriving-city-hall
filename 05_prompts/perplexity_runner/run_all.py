import argparse
from pathlib import Path
import subprocess
import sys


def main():
    parser = argparse.ArgumentParser(description="Run all prompts in a folder with Perplexity")
    parser.add_argument("prompts_dir", type=str, help="Directory containing .txt prompts")
    parser.add_argument("--config", type=str, default=str(Path(__file__).parent / "config.yaml"))
    args = parser.parse_args()

    prompts_path = Path(args.prompts_dir)
    if not prompts_path.exists() or not prompts_path.is_dir():
        raise SystemExit(f"Not a directory: {prompts_path}")

    prompt_files = sorted(p for p in prompts_path.iterdir() if p.suffix == ".txt")
    if not prompt_files:
        print("No .txt prompts found.")
        sys.exit(0)

    for p in prompt_files:
        print(f"\n=== Running {p.name} ===")
        subprocess.check_call([
            sys.executable,
            str(Path(__file__).parent / "run_one.py"),
            str(p),
            "--config",
            args.config,
        ])


if __name__ == "__main__":
    main()

