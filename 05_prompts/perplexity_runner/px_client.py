"""
Perplexity client wired for the prompt runner.

Notes
- Uses the Perplexity REST API (Sonar endpoint) via `requests`.
- Reads API key from `PERPLEXITY_API_KEY` (see `.env.example`).
- Supports models like `sonar-pro` and `sonar-deep-research`.
- Returns a normalized dict: { output_text, search_results, model, meta }.
"""

from __future__ import annotations

import os
import requests
from typing import Any, Dict, List


class PerplexityClient:
    def __init__(self, api_key: str | None = None, base_url: str | None = None, timeout: int = 60):
        self.api_key = api_key or os.getenv("PERPLEXITY_API_KEY")
        if not self.api_key:
            raise RuntimeError("PERPLEXITY_API_KEY not set. Add it to .env or environment.
Set PERPLEXITY_API_KEY before running the Perplexity runner.")

        # Default to the Sonar REST endpoint, matching user-provided boilerplate
        self.base_url = base_url or os.getenv("PERPLEXITY_BASE_URL", "https://api.perplexity.ai/v1/sonar")
        self.timeout = timeout

    def create_response(
        self,
        *,
        preset: str,
        input_text: str,
        instructions: str | None = None,
        model: str | None = None,
        **params: Any,
    ) -> Dict[str, Any]:
        """Call Perplexity and return a normalized response dict.

        Parameters
        - preset: Perplexity preset (e.g., "pro-search"). Included in meta; some endpoints ignore it.
        - input_text: The user query / prompt body.
        - instructions: Optional system instructions.
        - model: Perplexity model (e.g., "sonar-pro", "sonar-deep-research").
        - params: May include `search_domain_filter`, `web_search_options`, etc.
        """

        eff_model = model or os.getenv("PERPLEXITY_MODEL", "sonar-pro")

        messages: List[Dict[str, str]] = []
        if instructions:
            messages.append({"role": "system", "content": instructions})
        messages.append({"role": "user", "content": input_text})

        payload: Dict[str, Any] = {
            "model": eff_model,
            "messages": messages,
        }

        # Best-effort pass-through of search options if provided in config/front matter
        if params.get("search_domain_filter"):
            payload["search_domain_filter"] = params["search_domain_filter"]
        if params.get("web_search_options"):
            payload["web_search_options"] = params["web_search_options"]
        # Preserve preset in meta; some APIs may not accept it as a request field
        if preset:
            payload["preset"] = preset

        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
        }

        resp = requests.post(self.base_url, json=payload, headers=headers, timeout=self.timeout)
        try:
            resp.raise_for_status()
        except requests.HTTPError as e:
            # Attach server-provided error details if any
            detail = None
            try:
                detail = resp.json()
            except Exception:
                pass
            raise RuntimeError(f"Perplexity API error: {e} | details={detail}") from e

        raw = resp.json()

        # Extract assistant text – handle common shapes defensively
        output_text = (
            (raw.get("choices") or [{}])[0].get("message", {}).get("content")
            if isinstance(raw.get("choices"), list)
            else raw.get("output_text")
        ) or ""

        # Extract search results/citations in a normalized form
        search_results: List[Dict[str, Any]] = []

        if isinstance(raw.get("search_results"), list):
            # Already in desired shape or close to it
            for item in raw["search_results"]:
                if isinstance(item, dict):
                    # Keep as-is; ensure keys exist
                    search_results.append({
                        "title": item.get("title"),
                        "url": item.get("url"),
                        "date": item.get("date"),
                    })
                elif isinstance(item, str):
                    search_results.append({"title": None, "url": item, "date": None})
        else:
            # Some responses include citations as a list of URLs within the message
            choice0 = (raw.get("choices") or [{}])[0]
            message = choice0.get("message") or {}
            citations = message.get("citations") or choice0.get("citations") or raw.get("citations")
            if isinstance(citations, list):
                for c in citations:
                    if isinstance(c, dict):
                        search_results.append({
                            "title": c.get("title"),
                            "url": c.get("url") or c.get("source") or c.get("link"),
                            "date": c.get("date"),
                        })
                    elif isinstance(c, str):
                        search_results.append({"title": None, "url": c, "date": None})

        return {
            "output_text": output_text,
            "search_results": search_results,
            "model": raw.get("model", eff_model),
            "meta": {
                "preset": preset,
                "request_endpoint": self.base_url,
                # Omit API key from meta for safety
                "request_params": {
                    k: v for k, v in payload.items() if k not in {"messages"}
                },
                "raw": raw,
            },
        }
