# Perplexity Runner (Scaffold)

This folder provides a lightweight scaffold to run the prompts in `05_prompts/research` against the Perplexity API and save results under `05_prompts/research-output`.

You will paste your Perplexity client boilerplate into `px_client.py` (or install your preferred SDK) when ready.

Contents
- `config.yaml` — central run configuration (model, preset, instructions, search params)
- `px_client.py` — placeholder client; paste your boilerplate here
- `run_one.py` — run a single prompt file and save outputs
- `run_all.py` — run all prompts in a directory
- `Makefile` — convenience targets
- `docs/` — Perplexity prompt/use guidance (copied to keep usage close to code)

Usage (after adding client code)
1) Copy `.env.example` to `.env` and set `PERPLEXITY_API_KEY`.
2) Paste your client boilerplate into `px_client.py` (replace the stub).
3) Option A: run one prompt
   - `python 05_prompts/perplexity_runner/run_one.py 05_prompts/research/A1_problem_landscape_mbe_contracting.txt`
4) Option B: run all prompts in a folder
   - `python 05_prompts/perplexity_runner/run_all.py 05_prompts/research`

Outputs
- Markdown response: `05_prompts/research-output/<prompt_basename>.md`
- JSON metadata (including search_results if available): `05_prompts/research-output/<prompt_basename>.json`

Notes
- Do not ask the model to include URLs in the text. Parse accurate URLs from the API response metadata (see docs in `docs/`).
- Prefer using built-in API search parameters (e.g., `search_domain_filter`) instead of instruction-based filtering.
- Keep prompts Richmond-specific and label Findings as Verified (with sources), Inference, or Unknown.

Per-prompt configuration
- You can override `config.yaml` per prompt using either:
  1) YAML front matter at the top of the prompt file:
     ---
     model: sonar-pro
     preset: pro-search
     instructions: |
       You are a Richmond-focused research assistant...
     search_domain_filter: ["mvendor.cgieva.com", "rva.gov"]
     web_search_options:
       search_context_size: high
     ---
     <prompt text continues here>

  2) A sibling YAML file with the same basename, e.g.:
     `A1_problem_landscape_mbe_contracting.yaml`
     with any subset of the same keys as `config.yaml`.

Precedence
- Base `config.yaml` <- per-prompt YAML <- prompt front matter (highest).
