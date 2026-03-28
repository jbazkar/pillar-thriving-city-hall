import { describe, expect, it } from "vitest";
import { confidenceBand } from "./confidenceStyle";

describe("confidenceBand", () => {
  it("maps TDD thresholds (90% / 80%)", () => {
    expect(confidenceBand(0.95)).toBe("high");
    expect(confidenceBand(0.9)).toBe("high");
    expect(confidenceBand(0.89)).toBe("mid");
    expect(confidenceBand(0.8)).toBe("mid");
    expect(confidenceBand(0.79)).toBe("low");
    expect(confidenceBand(0)).toBe("low");
  });
});
