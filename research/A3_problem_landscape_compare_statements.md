# Prototype where the data lives: Richmond 311 vs. Procurement

## Executive Summary

When evaluating the two targeted problem statements for Richmond's "A Thriving City Hall" pillar, hackathon teams face a classic product management trade-off: a well-defined user with a closed backend (Statement 1) versus an undefined user with rich, accessible open data (Statement 2). 

While Statement 1 (Helping Residents Find the Right City Service) scores higher on the initial rubric (27/32) and boasts a clear champion in RVA311 Director Peter Breil, it suffers from a fatal technical constraint for a 48-hour build: the lack of a public API. Conversely, Statement 2 (Helping City Staff Review Procurement Risks) scores lower (22/32) and lacks a named champion, but it is supported by a fully accessible, monthly-updated Socrata dataset and strong political momentum from the newly launched Procurement Transparency Dashboard.

**Recommendation for a typical hackathon team:**
Choose **Statement 2 (Procurement risks/opportunities)**. It provides high-quality, fully accessible public data, enabling a highly verifiable and reproducible demo in 48 hours. It carries a lower risk of civic harm if the prototype produces errors (provided proper disclaimers are used) and aligns perfectly with the city’s recent ordinance-driven transparency push. A credible, data-backed prototype is more likely to score well with judges and spark a post-hackathon conversation with Procurement Services.

**Conditions under which a team might reasonably choose the lower-scored problem (Statement 2):**
* The team needs a fully verifiable, API-backed build with reproducible endpoints that judges can click and re-run.
* The team’s strengths lie in data engineering, analytics, and front-end dashboards rather than service design and content curation.
* There is no pre-event access to 311 leadership or the intake taxonomy, creating a high risk of shipping an LLM-based triage tool without authoritative ground truth.
* The judging rubric heavily rewards the "use of open data" and "demo reproducibility."
* The team can align their scope to the new Procurement Transparency Dashboard to create a clear post-hackathon pathway.
* The team can implement conservative, transparent risk heuristics (e.g., "expiring within N days," "single vendor concentration") with clear labels and caveats.

## Decision Snapshot — For a weekend build, procurement delivers a stronger, verifiable demo; 311 wins only with co-design and scoped triage

The following table breaks down the comparative advantages and constraints of both problem statements across six critical dimensions.

| Dimension | Statement 1 — Help Residents Find the Right City Service/Next Step (Score 27/32) | Statement 2 — Help Staff Review Procurement Risks/Opportunities (Score 22/32) | Verdict |
| :--- | :--- | :--- | :--- |
| **1) Data availability** | No confirmed public RVA311 API post-2018. The channel relies on a web portal, mobile apps, and a call center. Open data portal shows no 311 request feed; 311 appears to run on a vendor Service Request Management (SRM) system [1]. | City Contracts dataset (Socrata xqn7-jvv2) is fully accessible via OData/CSV/JSON. It is updated monthly (last updated March 16, 2026) and includes agency, contract number, amount, supplier, and dates [2]. | **Statement 2** |
| **2) User clarity** | Very clear end user: Richmond residents needing non-emergency services. High volume signal: 83,000+ requests supported in 2024 [3]. Clear champion: Director Peter Breil [4]. | Intended user is internal: procurement analysts/managers. No named champion; user needs (risk flags, expirations, vendor concentration) must be inferred. | **Statement 1** |
| **3) Demo credibility** | Without an API/taxonomy, the demo becomes a content triage wizard linking to rva.gov or a manual mapping of service categories—hard to verify correctness beyond "we link to this page." | Highly verifiable: charts and flags computed directly from public Socrata data. Judges can reproduce results via API URLs. Aligns with the city’s newly launched transparency dashboard [5]. | **Statement 2** |
| **4) Risk profile (harm if wrong)** | Higher. Misrouting can delay fixes and generate duplicate tickets. Local reporting shows misrouting delayed a leaking water meter repair for months [4]. | Lower direct civic harm if the tool is clearly "for staff" and labeled exploratory. False positives could create noise, mitigated with disclaimers. | **Statement 1 has higher harm** |
| **5) Continuation pathway** | Stronger. A named champion (311 director) and an existing high-volume operation make it easier to pilot if co-designed [4]. | Weaker. No champion, though the Transparency Dashboard and ordinance momentum create an opening to engage Procurement Services [5]. | **Statement 1** |
| **6) Technical feasibility** | Tighter. No programmatic feed; teams must scrape/collate service content and hand-curate a routing taxonomy. LLM prompting alone risks hallucinations. | Strong. One clean, well-documented dataset plus OpenGov links makes it straightforward to build SoQL-powered analytics and expirations radars [2] [6]. | **Statement 2** |

**Key Takeaway:** Statement 2 offers a highly feasible, data-rich environment perfect for a 48-hour sprint, whereas Statement 1 requires significant manual curation and carries higher risks of user friction without direct API access.

### Why this choice works for judges — Public APIs + reproducibility beat speculative UX

Hackathon judges consistently reward prototypes that work over those that merely look good. Statement 2 aligns perfectly with open-data, reproducible judging criteria. Because the Socrata dataset is live and structured, teams can embed actual OData queries into their code. Judges can verify the math, click the links, and see real Richmond city contracts. Statement 1, while solving a highly visible resident pain point, forces teams to build speculative UX layers over static web content, making the underlying technology harder to validate.

## Data Landscape and Constraints — Open Socrata contracts vs. closed 311 backend

The fundamental divergence between these two problem statements lies in where and how the data is stored. 

For Statement 2, the data landscape is robust and accessible. The "City Contracts" dataset on the Open Data portal provides a comprehensive list of open and closed city contracts for the past five years [2]. This dataset is updated monthly, with the most recent update occurring on March 16, 2026 [2]. It includes highly structured fields critical for risk analysis: agency name, contract number, contract dollar amount, supplier name, procurement type, contract description, solicitation type, and effective start and end dates [2]. Furthermore, the city has consolidated procurement data via the new Procurement Transparency Dashboard, which links to OpenGov for bid details and the Minority Business Development Vendor Registry [6] [5].

Conversely, Statement 1 suffers from a severe data bottleneck. While RVA311 is a highly active service, there is no public Open311 API endpoint available for developers to pull live request taxonomies or status updates. The city's contract data reveals that RVA311 operates on a proprietary "Service Request Management System (SRM)" [1]. This vendor lock-in means that any hackathon prototype aimed at 311 triage will not be able to programmatically submit tickets or pull live category codes. Teams will be forced to scrape rva.gov service pages and manually map out a routing taxonomy.

## User Access and Champions — Residents clear; procurement users unclear but policy momentum helps

Statement 1 benefits from absolute clarity regarding its end-users and stakeholders. The target audience is Richmond residents and visitors who need non-emergency services. The demand is proven: in 2024, RVA311 provided assistance or supported City partners in responding to over 83,000 requests [3]. Furthermore, the problem has a highly visible champion in Peter Breil, the Director of Citizen Service and Response [4] [7]. Having a named executive who actively discusses the system's intake challenges provides a clear target for post-hackathon adoption.

Statement 2 lacks a specific named champion within Procurement Services, meaning teams must infer the exact needs of internal procurement analysts. However, this gap is offset by massive, highly visible executive and policy momentum. The City of Richmond recently launched the Procurement Transparency Dashboard to offer clear insights into the stewardship of public funds [5]. This initiative was driven by Councilmember Kenya Gibson's ORD. 2025-211, which requires increased public access to procurement data [5]. Mayor Danny Avula has publicly championed this effort, stating, "This is about building accountability and trust" [5]. Teams can leverage this political tailwind to position their prototype as a natural extension of the city's current strategic goals.

## Demo Design Paths — Build plans both ways with what judges can verify

To succeed in a 48-hour window, teams must design demos that highlight their technical execution while masking their data constraints. 

**Statement 2 Exemplar Features (Procurement):**
Because the data is structured, teams can build three clean, checkable views that judges can verify instantly:
* **Expiring Contracts Radar:** A dashboard flagging contracts expiring in the next 30, 60, or 90 days to help staff prepare for renewals or new bids.
* **Supplier Concentration View:** An analysis showing the top 10 vendors by total award amount, highlighting potential single-vendor dependencies.
* **Methodology Breakdown:** Visualizations of awards by procurement method and solicitation type over time.
* **Verification:** The UI should include live SoQL/OData URLs so judges can click and see the raw JSON/CSV feed powering the charts.

**Statement 1 Exemplar Features (RVA311):**
Without an API, teams must rely on curated content and robust fail-safes:
* **Top 25 Resident Intents Wizard:** A guided, mobile-friendly questionnaire mapping the most common issues (e.g., potholes, streetlights, tax billing) to the correct rva.gov pages or 311 guidance [3].
* **Duplicate Check Prompt:** A location-aware heuristic that asks, "Has this been reported?" before guiding the user further, aligning with the city's plea for residents to avoid submitting duplicate requests [3].
* **Fail-safe Escalation:** A persistent "Call 311" fallback when the user's intent is uncertain, ensuring no resident is left stranded.

## Risk and Mitigations — Minimize civic harm while maximizing usefulness

The risk profiles of these two statements differ drastically in terms of potential civic harm. 

Statement 1 carries a high risk of direct resident impact. RVA311 acts as a wrapper around heavily siloed city services; as Director Peter Breil noted, "We are the intake... The departments have teams that respond" [4]. Misrouting a request can have severe consequences. For example, local reporting highlighted a case where a leaking water meter pooled water for months because three separate RVA311 requests were not routed to the correct repair crews [4]. Furthermore, the city explicitly notes that using 311 reduces non-emergency calls to 911 [3]. If a hackathon prototype gives wrong advice or creates friction, it could inadvertently push frustrated residents to call emergency lines. Mitigations must include conservative prompts, linking only to authoritative rva.gov pages, and never inferring the status of a ticket.

Statement 2 carries a much lower risk of direct civic harm because it is an internal, staff-facing analytics tool. The primary risk is generating false positives (e.g., flagging a standard contract as a "risk"). This could create reputational or political noise. To mitigate this, teams must clearly label all heuristics, display the formulas used to calculate risk, show data freshness timestamps, and limit the scope to descriptive analytics rather than prescriptive judgments.

## 48-Hour Build Feasibility — Concrete milestones and stack

Feasibility dictates hackathon success. Statement 2 is essentially plug-and-play, while Statement 1 requires significant content operations.

| Milestone | Statement 2 (Procurement Analytics) | Statement 1 (RVA311 Triage) |
| :--- | :--- | :--- |
| **Hour 4** | Establish data model and write SoQL queries against the Socrata endpoint. | Curate the "Top 25 intents" by scraping rva.gov service pages. |
| **Hour 12** | Build three core dashboards (expirations, vendors, methods). | Develop the routing wizard MVP with basic location input. |
| **Hour 20** | Implement risk flag heuristics and data export functionality. | Add duplicate-check prompts and refine accessibility. |
| **Hour 36** | Polish UI and embed verification links for judges. | Script user flows and implement the "Call 311" fallback UX. |
| **Hour 44** | Finalize the 1-page narrative and handoff notes. | Conduct usability testing with 2-3 residents (if available). |

**Key Takeaway:** The Procurement build allows teams to spend the majority of their time on data engineering and UI polish. The 311 build forces teams to spend critical hours manually mapping content before they can even begin coding the core logic.

## Adoption Pathways and Handoff — Who owns it Monday morning

A successful hackathon project must have a clear owner when the weekend ends. 

For Statement 1, the pathway is clearer but requires proactive engagement. Teams should attempt to request a brief taxonomy review with Director Peter Breil or his team. The handoff should include a coverage map of the top intents addressed by the prototype and a proposed pilot plan for integrating the triage wizard as a lightweight front-end to the existing RVA311 portal.

For Statement 2, the strategy is to align with the team managing the new Procurement Transparency Dashboard. Teams should prepare a 1-pager for Procurement Services that explicitly ties the prototype's features to the goals of ORD. 2025-211 [5]. The handoff must include an open-source repository and a clear data dictionary explaining how the Socrata data was transformed into the risk dashboards.

## Success and Failure Cases — Learn from what worked and what didn't

Understanding the current operational realities of Richmond's services provides vital context for prototype design. 

RVA311 has demonstrated massive capacity when requests are routed correctly. Between January 2022 and March 2023, 1,888 potholes were reported, and crews successfully closed 1,824 of those requests [8]. However, the system fails when triage breaks down, as evidenced by the months-long delay in fixing a leaking water meter due to misrouted tickets [4]. 

On the procurement side, the recent launch of the Transparency Dashboard proves that the city administration is actively investing resources into making financial data accessible and actionable [5]. Prototypes that build on this momentum are highly likely to be well-received by city leadership.

## Judge-Facing Evidence Pack — Make verification effortless

To maximize scoring, teams must make it effortless for judges to verify their work. 

For Statement 2, every chart and metric must feature a "View Source Data" button that links directly to the corresponding SoQL/OData query. The UI should prominently display the data freshness (e.g., "Last Updated: March 16, 2026") [2] and explicitly state the formulas used for risk flags. 

For Statement 1, every suggestion generated by the triage wizard must link directly to its authoritative rva.gov page or display the official phone number. Teams must clearly document the known limitations of their prototype, specifically acknowledging the lack of a live API and the reliance on curated heuristics.

## References

1. *https://data.richmondgov.com/api/views/xqn7-jvv2/r...*. https://data.richmondgov.com/api/views/xqn7-jvv2/rows.csv?accessType=DOWNLOAD&bom=true&format=true
2. *City Contracts | Open Data Portal | City of Richmond, Virginia*. https://data.richmondgov.com/Well-Managed-Government/City-Contracts/xqn7-jvv2
3. *About RVA 311 | Richmond*. https://www.rva.gov/citizen-service-and-response/about-rva-311
4. *To some callers, RVA311 is the wrong number*. https://www.richmonder.org/to-some-callers-rva311-is-the-wrong-number/
5. *City Administration Expands Transparency, Releases Procurement Transparency Dashboard | Richmond*. https://rva.gov/press-releases-and-announcements/news/city-administration-expands-transparency-releases-procurement
6. *Procurement Transparency Dashboard | Richmond*. https://rva.gov/procurement-services/procurement-transparency-dashboard
7. *For the first meeting of her term, Abubaker invited Peter Briel ...*. https://www.instagram.com/p/DFusxUBzClR/
8. *Pothole problems showing progress in Richmond, city officials say | WRIC ABC 8News*. https://www.wric.com/news/local-news/richmond/pothole-problems-showing-progress-in-richmond-city-officials-say/