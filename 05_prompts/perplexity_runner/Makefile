PROMPTS_DIR?=05_prompts/research
RUNNER=05_prompts/perplexity_runner

run-one:
	python $(RUNNER)/run_one.py $(PROMPT) --config $(RUNNER)/config.yaml

run-all:
	python $(RUNNER)/run_all.py $(PROMPTS_DIR) --config $(RUNNER)/config.yaml

list:
	ls -1 $(PROMPTS_DIR)/*.txt || true

clean:
	rm -f 05_prompts/research-output/*

