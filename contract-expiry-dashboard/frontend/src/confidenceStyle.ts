/** Model-reported confidence → UI band (TDD: ≥90% green, ≥80% yellow, &lt;80% amber). */
export type ConfidenceBand = "high" | "mid" | "low";

export function confidenceBand(conf: number): ConfidenceBand {
  if (conf >= 0.9) return "high";
  if (conf >= 0.8) return "mid";
  return "low";
}

export function confidenceRowSx(band: ConfidenceBand): Record<string, unknown> {
  switch (band) {
    case "high":
      return {
        bgcolor: "rgba(46, 125, 50, 0.12)",
        borderLeft: 4,
        borderColor: "success.main",
      };
    case "mid":
      return {
        bgcolor: "rgba(251, 192, 45, 0.2)",
        borderLeft: 4,
        borderColor: "#fbc02d",
      };
    default:
      return {
        bgcolor: "rgba(237, 108, 2, 0.15)",
        borderLeft: 4,
        borderColor: "warning.main",
      };
  }
}
