# Credible City Hall Demos: Proving Real Data, Real Limits, Real Trust

## Executive Summary

In the evolving landscape of civic tech hackathons, judges have grown highly sophisticated, actively penalizing "integration theater" and overly polished prototypes that hide their failure modes. The 2026 standard for credibility requires teams to operationalize transparency. Federal guidance now explicitly treats disclosed uncertainty as a positive competency; the 18F De-risking Government Technology Guide lists highlighting areas of uncertainty as a positive sign and flags misidentifying core technologies as a critical red flag [1]. Similarly, the NIST AI Risk Management Framework (AI RMF 1.0) centers transparency, information integrity, and uncertainty communication across its core functions [2]. 

To win trust, teams must build an explicit "uncertainty lane" into their demos, showing abstentions, confidence bands, and fallback flows. Real Richmond data is the credibility backbone: teams must name exact systems like AvePoint Citizen Services on Dynamics 365, Granicus Legistar, and RVA.gov on Acquia/Drupal, driving the demo with live pulls or time-stamped cached snapshots [3] [4] [5] [6]. By attaching click-through provenance chips to every recommendation and demonstrating safe failure modes on stage, teams can transform the "what doesn't it do?" question from a vulnerability into their strongest pitch for feasibility and maturity.

## 1) Context: The 2026 Credibility Bar for Civic Tech Demos

Hackathon judges are increasingly wary of "tech for tech's sake" and projects that prioritize aesthetics over actual usefulness [1] [7]. A well-defined problem statement often reveals more maturity than the code itself, and judges actively look for solutions tied to meaningful user needs rather than flashy buzzwords [7]. 

Federal playbooks and frameworks have established a new baseline where transparency and documented limits are rewarded. The 18F De-risking Guide emphasizes that since vendors cannot know if a proposed approach will be effective until development begins, they should be candid about their uncertainty [1]. Furthermore, the NIST AI RMF 1.0 requires organizations to account for inherent limitations and uncertainties in their models to improve overall system trustworthiness [2]. In a 48-hour hackathon, credibility is evaluated in minutes through the demonstration of live data, clear provenance, calibrated uncertainty, and the honest handling of failure modes.

## 2) Richmond Systems Reality Map

Citing Richmond's actual technology stack and surfacing verifiable endpoints during the demo anchors the project in reality and proves the team understands the specific civic environment.

| System | Function | Platform/Tech | Public Touchpoint | Notable Details |
| :--- | :--- | :--- | :--- | :--- |
| RVA311 | Non-emergency service requests | AvePoint Citizen Services on Microsoft Dynamics 365 | rva311.com | Handled over 83,000 requests in 2024; features public status states (unprocessed, assigned, in progress, on hold, completed) [3] [6]. |
| City Council/Boards | Agendas, meetings, legislation | Granicus Legistar | richmondva.legistar.com | Live calendar, department pages; common agenda/minutes objects [5] [8]. |
| RVA.gov | City website | Drupal on Acquia | rva.gov | City press release confirms Acquia hosting and Drupal-based platform [4]. |

**Takeaway:** Accurately naming these systems prevents the red flag of "misidentifying core technologies" [1] and demonstrates a clear grasp of the agency's operational reality.

## 3) Standards That Reward Admitting Limits

The fastest path to trust is showing exactly how the tool handles uncertainty, aligning with established federal and technical standards.

| Guidance Framework | What It Rewards | Demo Evidence to Show |
| :--- | :--- | :--- |
| NIST AI RMF 1.0 | Transparency, uncertainty measures, error communication, human override across GOVERN, MAP, MEASURE, and MANAGE functions [2]. | Confidence bands, abstain thresholds, incident logs, explainability panels, and clear human override paths. |
| NIST GAI Profile (AI 600-1) | Information integrity, provenance, and mitigation of confabulation risks [9]. | Source chips with URLs/timestamps; simple red-team logs showing hallucination prevention. |
| 18F De-risking Guide | Candid uncertainty, correct tech naming, and prioritizing usefulness over aesthetics [1]. | "Scope box" slide detailing what the tool does and doesn't do; utility-first workflows. |
| USDS Digital Services Playbook | Defaulting to open, open source code/data, and providing mechanisms to report bugs [10] [11] [12]. | Public repository links, data dictionaries, and a visible "report an issue" button in the UI. |

**Takeaway:** Translating these high-level frameworks into visible UI elements—like confidence scores and source links—proves that the prototype is built on a foundation of responsible engineering.

## 4) Credibility Checklist: Essential Demo Requirements

To convert judge skepticism into confidence, every City Hall prototype demo must demonstrate the following elements:

* **Real data proof:** Use live calls or time-stamped caches; display dataset URLs and "last updated" metrics.
* **Correct system naming:** Explicitly reference AvePoint/Dynamics 365, Granicus Legistar, and RVA.gov/Acquia [3] [4] [5] [6].
* **Scope box:** Provide an explicit "Can / Cannot / Didn't attempt" list to avoid implied integrations.
* **Provenance chips:** Include clickable sources on every recommendation to ensure information integrity [9].
* **Uncertainty display:** Show calibrated confidence, top-3 suggestions, abstain thresholds, and coverage statistics [2].
* **Failure modes:** Rehearse two failures, demonstrating the "I'm not sure" behavior and the operator fallback path [1] [2].
* **Logs panel:** Display request counts, response codes, and cache hits/misses.
* **Explainability:** Provide a one-sentence rationale per recommendation to satisfy interpretability requirements [2].
* **Accessibility basics:** Mention Section 508 compliance plans and demonstrate keyboard navigation [1].
* **Security/PII stance:** Clarify what data was excluded and guarantee no write access to live systems.
* **Sustainability signals:** Share a GitHub repository, open-source license, readme, and roadmap [13] [10].
* **Judge handout:** Provide a one-pager with all links, dataset names, and documented limitations.

## 5) Language Patterns That Signal Honesty

Small wording choices during the pitch reveal whether a team is overselling or acting responsibly. 

| Scenario | Do Say (Trusted & Responsible) | Don't Say (Red Flag) |
| :--- | :--- | :--- |
| Capability scope | "Read-only; cached on 03/16 21:04; no write to Dynamics 365." | "We integrated with RVA systems." |
| Confidence | "We're 62% confident; below 55% we abstain and route to human." | "We're 95%+ confident across the board." |
| Data lineage | "Recommendation based on Legistar item #2026-123 and 2 RVA311 categories; links below." | "Our model knows this from the data." |
| Limits | "Doesn't handle post-committee amendments; flagged as unknown." | "It can do everything end-to-end." |
| Failure | "This query triggers our 'I'm not sure' path; here's the log." | "It never fails on our tests." |

**Takeaway:** Using precise, constrained language aligns with the 18F guidance that warns against highly complex language that confuses rather than clarifies [1].

## 6) Pre-Answer the "What are the limitations?" Question

Owning the boundaries of the prototype before judges ask strengthens the pitch. Link each limitation to a specific mitigation or next step using this 90-second script structure:

* **What it does today:** "Pulls recent Legistar agendas and RVA311 categories; recommends 3 info links; logs abstentions."
* **What it doesn't do:** "No write access to Dynamics 365; doesn't parse scanned PDFs; excludes classified data and private request details."
* **Data coverage:** "Covers 30 days of meetings; caches every 15 minutes; shows last-updated timestamps. Older items require manual requests."
* **Quality/uncertainty:** "Abstains below a 0.55 confidence threshold; top-3 recall is 84% on our validation set; shows rationale and source chips."
* **Failure modes:** "Malformed agendas trigger an 'I'm not sure' state, routing to operator review in under 60 seconds."
* **Next 2 weeks vs. 90 days:** "In 2 weeks: OCR fallback. In 90 days: an agency pilot with audit trails and access controls."

## 7) Demo Design: Proving Real Data Under Hackathon Constraints

Running a live demo makes a presentation stand out, but failing at a live demo due to shared Wi-Fi can ruin credibility [13]. Teams must plan for network reality while proving their code works.

* **Live-first with cached fallback:** Build a "degraded mode" toggle that switches to cached data with visible timestamps if the network fails.
* **Pre-recorded screencasts:** Have a short screencast or animated GIFs ready as an alternative to show the app in action [13].
* **On-screen run IDs:** Display logs and reproducibility steps directly in the UI.
* **Announce the plan:** Call out the network mitigation strategy upfront to preempt skepticism and demonstrate engineering maturity.

## 8) Failure Cases and Red Flags

Demonstrating safe failure differentiates a credible prototype from a polished but misleading mockup.

| Red Flag Judges Notice | Credible Replacement to Show |
| :--- | :--- |
| Hardcoded perfect responses | Use a randomized test set and intentionally show 1 failure with an "I'm not sure" path. |
| Vague "integration with City systems" | Present an exact scope box with URLs, masked tokens, and a strict no-write guarantee. |
| Always-high confidence | Display a calibrated chart with abstention and coverage metrics. |
| No mention of limits | Deliver the 90-second limitations script and provide a system card. |
| Overly aesthetic UI | Focus on utility-first flows tied to agency tasks and demonstrate an operator override [1]. |

**Takeaway:** 18F explicitly flags prioritizing aesthetics over usability and misidentifying core technologies as major warning signs [1]. Addressing these head-on builds immediate trust.

## 9) Align to Judging Rubrics

Make it easy for judges to award points by mapping demo beats directly to standard civic hackathon criteria.

* **Clarity:** The Carolina Across 100 rubric rewards clearly defined topics and accurate data representation without misinformation [14]. Show provenance chips and the "System Reality" slide to score high here.
* **Usefulness:** Submissions must translate into new insights or expanded understanding [14]. Tie recommendations directly to operator steps and citizen-facing links.
* **Feasibility & Scalability:** Judges look for scrappy solutions that could realistically scale [7]. Live calls, visible logs, a public repository, and a 90-day pilot plan prove feasibility.
* **Presentation:** A passionate, coherent story often sways the room [7]. Open with constraints and end with a failure case handled gracefully.

## 10) Appendices: Ready-to-Use Artifacts

Hand judges everything they need to verify claims quickly.

**Sample Provenance Chip Spec:**
```json
{
 "source_url": "https://richmondva.legistar.com/LegislationDetail.aspx?ID=12345",
 "item_id": "ORD-2026-123",
 "fetched_at": "2026-03-16T21:04:00Z",
 "cache_ttl_minutes": 15,
 "confidence_score": 0.82
}
```

**Uncertainty Panel Template:**
Include a visual confidence band, an abstain threshold indicator, and a short rationale snippet (e.g., "Matched 3 prior council items and 2 RVA311 categories").

**Model/System Card Outline:**
Document capabilities, non-capabilities, data used/excluded, known failure modes, and an evaluation summary.

**Repository Structure:**
Ensure the GitHub repository includes a README, setup instructions, a data dictionary, an open-source license, the demo script, and sample logs [10] [11].

## References

1. *De-risking Government Technology Guide - 18F Guides*. https://guides.18f.org/assets/derisking-government-tech/dist/18f-derisking-guide.pdf
2. *Artificial Intelligence Risk Management Framework (AI RMF 1.0)*. https://nvlpubs.nist.gov/nistpubs/ai/nist.ai.100-1.pdf
3. *About RVA 311 | Richmond*. https://www.rva.gov/citizen-service-and-response/about-rva-311
4. *City launches RVA.gov, new web platform to replace richmondgov.com | Richmond*. https://www.rva.gov/press-releases-and-announcements-mayors-office/news/city-launches-rvagov-new-web-platform-replace
5. *
	City of Richmond - Planning Commission
*. https://richmondva.legistar.com/DepartmentDetail.aspx?GUID=CFDDD5D6-AE26-43ED-8747-A02A21FD9362&ID=24014
6. *RVA 311 | AvePoint*. https://www.avepoint.com/case-studies/rva311
7. *What Are the Criteria to Judge as a Hackathon Jury? | by Praveen Xavier | Medium*. https://praveenax.medium.com/what-are-the-criteria-to-judge-as-a-hackathon-jury-32e08046dd4b
8. *
	City of Richmond - Calendar
*. https://richmondva.legistar.com/
9. *Artificial Intelligence Risk Management Framework*. https://nvlpubs.nist.gov/nistpubs/ai/NIST.AI.600-1.pdf
10. *The Digital Services Playbook — from the U.S. DOGE Service*. https://playbook.usds.gov/
11. *U.S. Digital Services and Playbook: "Default to Open" | Opensource.com*. https://opensource.com/government/14/8/us-government-how-to-opens-source-software
12. *Applying the U.S. Digital Services Playbook for Strategic and Sustainable Success | Palantir.net*. https://www.palantir.net/blog/applying-us-digital-services-playbook-strategic-and-sustainable-success
13. *8 Tips to a Successful Hackathon Demo and Presentation | by Hack Upstate | Upstate Interactive | Medium*. https://medium.com/upstate-interactive/8-tips-to-a-successful-hackathon-demo-and-presentation-4d1ae83415ad
14. *The Carolina Across 100 Civic Hacking Competition*. https://carolinaacross100.unc.edu/wp-content/uploads/2022/03/Judging-Rubric-pdf.pdf