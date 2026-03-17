# RVA311 routing reality: cutting misroutes, boosting first-time right

## Executive Summary
Online intake is amplifying routing pressure for 311 call centers nationwide. In cities like Los Angeles, 80% of 311-operated service requests now arrive via mobile apps and websites, yet 40% of answered calls still require transfers due to misclassification or specialized needs [1]. For Richmond's RVA311 system, which handles approximately 208,216 requests annually using AvePoint Citizen Services on Dynamics 365, this digital shift presents both a risk of increased manual re-routing and an opportunity for intelligent triage. 

"Right-first-time" routing is measurable and achievable. Industry benchmarks show average First Call Resolution (FCR) rates around 71% [2], with top-performing cities like Tempe hitting 86% Single Point of Contact resolution [3]. Historically, without strong triage, misroutes are costly; pre-311 studies in Minneapolis found 20–30% of calls were misrouted by departments, with up to 60–85% of police-related calls misdirected [4]. By leveraging Richmond's existing Dynamics 365 stack to pilot a plain-language service navigator—starting with highly ambiguous categories like sanitation—RVA311 can significantly reduce avoidable rework, improve Service Level Agreement (SLA) compliance, and boost agent trust.

## Why this report matters — RVA311 can cut avoidable rework and improve resident outcomes now

Digital intake is rising, misroutes are measurable and reducible, and existing systems can support "right-first-time" routing with focused configuration and Quality Assurance (QA). 

### Richmond baseline and goals
With a known volume of ~208,216 annual requests and a modern technology stack (AvePoint on Dynamics 365), RVA311 is well-positioned to instrument routing accuracy quickly. By establishing baseline metrics for reclassification rates, the city can guide a targeted pilot for a resident service navigation tool championed by 311 Director Pete Briel.

## Staff persona and workflow — "A day in the life" of a RVA311 routing agent

Agents juggle triage, classification, and rework daily. Miscategorized online requests and subtle policy nuances drive avoidable effort and frustration.

### Intake touchpoints and queues
Agents manage a multichannel environment, handling requests via phone, web portals, and mobile apps. While phone calls allow for real-time probing and clarification, digital submissions often arrive in auto-intake workstreams with missing context, requiring agents to manually review and validate the resident's intent.

### Triage steps
The standard triage workflow requires agents to validate the location, read resident text descriptions, review uploaded photos, search the Knowledge Base (KB) for policy rules, select the appropriate service code, and assign the ticket to the correct department to trigger the SLA.

### Re-routing loop
When a digital request is misclassified by a resident, agents enter a re-routing loop. They must detect the error, re-code the ticket, notify the resident and the new department, and ideally annotate the reason for the change and the time spent correcting it.

### Pain points
Agents face significant friction from ambiguous categories (e.g., distinguishing between sanitation, code enforcement, and street maintenance), duplicate submissions, seasonal policy shifts, KB gaps, and inconsistent SLAs across departments. 

### Tools in the flow
The primary tools in the agent's workflow include Microsoft Dynamics 365, AvePoint Citizen Services, KB articles, GIS/geo-location tools, performance dashboards, and QA recording systems.

## System capabilities and gaps — AvePoint/Dynamics features vs RVA311 needs

Intelligent routing, knowledge integration, and dashboards already exist in Richmond's stack; taxonomy alignment and analytics on reclassification are the primary gaps.

### What AvePoint offers
AvePoint Citizen Services, built on Microsoft Cloud technologies, provides dynamic routing, integrated geo-location services, SLA monitoring, and automated escalation [5]. It allows agencies to route service requests dynamically based on user input and workload, and enables citizens to search a knowledge base integrated with Dynamics 365 [5]. The system also supports easy editing and updating of existing service requests [5].

### What to configure now
To maximize these capabilities, RVA311 needs a taxonomy review sprint to align service codes and synonyms. The system should be configured to require a "Reason for Reclassify" field when tickets are altered, and dashboards must be updated to surface "reclassified after intake" as a primary KPI.

### Data to capture
Critical data points to instrument include a reclassification flag, time spent per re-route, transfer reasons, the presence of photo attachments, and location confidence scores.

## National benchmarks — Routing accuracy proxies and re-routing burden

Cities use First Call Resolution (FCR) and transfer rates as stand-ins for routing accuracy. Performance ranges from ~70% to the mid-80s for FCR, with 5–20% transfer rates among peer cities [1] [4] [2].

### Minneapolis: The cost of misroutes
Before implementing its 311 system, Minneapolis found that 20 to 30 percent of calls were misrouted, and more than 60 percent of calls to police were misdirects [4]. After launching 311, the city achieved an FCR rate of approximately 70% in its first year of operations [4].

### Los Angeles: The digital intake challenge
In Los Angeles, the mobile app and website account for 80% of service requests received through 311-operated channels [1]. Despite this digital adoption, 311 acts as a first-tier call center for issues it cannot resolve, forcing agents to transfer over 40% of answered calls to other departments [1]. This contrasts sharply with peer cities that only transfer between 5% and 20% of calls [1]. These transfers compound wait times, which averaged 3.3 minutes in LA compared to 10 seconds to 2.8 minutes for peers [1].

### Tempe and Industry Averages
Tempe tracks a "Single Point of Contact Resolution Rate," reporting 86.15% in a recent dashboard [3]. Their stated goal is to achieve a resolution rate greater than or equal to the mean average determined by U.S. Contact Center benchmarking [6]. Nationally, SQM Group's 2021 benchmarking shows the call center industry FCR average is 71%, meaning 29% of customers have to call back to resolve their issue [2].

| City / Source | Metric | Value | Relevance to RVA311 |
| :--- | :--- | :--- | :--- |
| **Minneapolis** [4] | First Call Resolution (FCR) | ~70% (Year 1) | Demonstrates baseline achievable accuracy post-consolidation. |
| **Los Angeles** [1] | Transfer Rate | 40% | Highlights the risk of acting as a switchboard without proper triage tools; peers average 5-20%. |
| **Tempe** [3] | Single Point of Contact Resolution | 86.15% | Represents a high-performing benchmark for resolving issues without transfers. |
| **SQM Group (Industry)** [2] | Average FCR | 71% | Provides a cross-industry baseline for first-time right interactions. |

*Takeaway: RVA311 should aim for an initial "Right First Type" target of ≥80%, aligning with high-performing municipal benchmarks while avoiding the high transfer rates seen in fragmented systems like LA.*

## Misclassification hotspots — Where plain language prevents rework

Sanitation requests frequently hinge on subtle policy and location rules that plain-language prompts can clarify, preventing downstream rework.

### Illegal dumping vs bulk vs missed collection
Residents often confuse sanitation categories. For example, NYC 311 has distinct rules for "Illegal Dumping" (which can include past dumping or chronic dumping from a vehicle, with fines up to $18,000) [7] versus "Missed Collection" [8]. In Columbus, bulk items like furniture and carpet will not be collected unless specifically scheduled online or via 311 [9]. 

### Location and eligibility rules
Policy context must be live. NYC suspends missed collection complaints during full winter snowstorm operations [8]. Furthermore, NYC's Department of Sanitation does not remove items from private property or small amounts of trash from public sidewalks (which is the property owner's responsibility) [7]. 

| Category | Common Confusion | Required Qualifier | Example Prompt |
| :--- | :--- | :--- | :--- |
| **Missed Collection** | Confused with weather delays or holiday schedules. | Operations state (e.g., snow emergency). | "Is there a current weather delay? (Note: Complaints suspended during snow)." |
| **Illegal Dumping** | Confused with private property litter. | Location (Public vs. Private). | "Is the debris on a public street/sidewalk or private property?" |
| **Bulk Pickup** | Confused with regular trash. | Item type and scheduling requirement. | "Are you disposing of furniture or appliances? This requires a scheduled pickup." |

*Takeaway: Embedding policy-aware guardrails and eligibility gates directly into the digital intake flow prevents residents from submitting invalid or miscategorized tickets.*

## Richmond landscape — What's public, what's missing, and where to look

While the technology stack (AvePoint/D365) supports rapid internal measurement, public routing-accuracy data for RVA311 is currently limited.

### Known items
RVA311 utilizes AvePoint Citizen Services on Dynamics 365 and processes approximately 208,216 requests annually. Pete Briel serves as the Director of 311 and is a key stakeholder for improving resident service navigation.

### Gaps
This research scan did not surface public reporting on RVA311 staffing levels, specific routing accuracy percentages, or transfer metrics on the rva.gov or RVA311 portals. 

### Sources to verify
To establish a true baseline, internal validation is required. Key sources to investigate include internal budget/performance documents, vendor configuration logs, and direct stakeholder interviews with Pete Briel to confirm specific pain points and pilot scope.

## Define the metric — "Right First Type" and companion KPIs

Make "Right First Type" (RFT) the north-star metric for routing quality, supported by transfer rates and time-per-re-route.

### RFT definition
RFT is defined as the percentage of service requests that require no category change, reclassification, or departmental transfer post-intake until closure.

### Supporting KPIs
Supporting metrics should include the online reclassification rate, average minutes spent per re-route, avoidable transfer rate, FCR, and SLA compliance deltas comparing reclassified tickets versus clean tickets.

### Instrumentation
RVA311 must implement required fields in Dynamics/AvePoint, specifically a "Reason for Reclassify" picklist, and build weekly dashboard reports to track these metrics.

## Navigator impact on workflow — Gains, risks, and guardrails

A human-in-the-loop, explainable navigator reduces rework and coaching time; unmanaged automation risks SLA breaches and trust erosion.

### Gains
A plain-language navigator would provide faster triage, fewer reclassifications, consistent questioning for residents, better onboarding support for new agents, and automated detection of KB content gaps.

### Risks
If the tool is inaccurate, staff face the risk of misroutes at scale, a fragile taxonomy that breaks during seasonal shifts, and a perceived deskilling or surveillance of the workforce.

### Guardrails
To build staff trust, the tool must include confidence thresholds, display the rationale for its category recommendations, show the top-3 alternative categories, and allow one-click human override with audit logs. It must also integrate dynamic policy flags (e.g., weather events) to adjust intake rules in real-time.

## Pilot plan — Start narrow, measure hard, expand fast

A 60–90 day pilot focused on three high-friction sanitation categories can validate value, quantify rework savings, and de-risk scale-up.

### Scope
The pilot should focus exclusively on Missed Collection, Bulk Pickup, and Illegal Dumping.

### Phases
* **Weeks 1–2:** Baseline measurement (instrumenting Dynamics 365).
* **Weeks 3–4:** Recommendation-only mode (agents see suggestions but manually select).
* **Weeks 5–8:** Tuned recommendations based on agent feedback.
* **Weeks 9–12:** Optional auto-route enabled for high-confidence digital submissions.

| KPI | Baseline Method | Target | Reporting Cadence |
| :--- | :--- | :--- | :--- |
| **Reclassification Rate** | Dynamics 365 flag | -30% reduction | Weekly |
| **Minutes per Re-route** | Time-and-motion study | -25% reduction | Bi-weekly |
| **Avoidable Transfers** | Call center logs | -20% reduction | Weekly |
| **Agent Trust Score** | Staff survey | >4.0 / 5.0 | End of Phase 2 & 4 |

*Takeaway: Strict phase-gating ensures the tool proves its accuracy to agents before any automated routing is turned loose on the public.*

## Governance, QA, and reporting — Make accuracy a habit

Formal QA, SLAs, and a monthly routing report institutionalize gains and drive continuous content fixes.

### Governance and QA program
Minneapolis 311 developed an internal QA process that reviews a monthly sample of phone records and screen captures to document how agents move through the knowledge base, resulting in a quality assurance index [10]. RVA311 should launch a similar monthly "Routing QA huddle" to sample tickets, score RFT, and assign KB updates.

### SLAs and Reporting
As noted in Los Angeles, providing estimated completion times is an essential component of a city's service commitment and serves as a benchmark for measuring timeliness [1]. RVA311 should define strict handoff expectations between 311 and departments, and publish an internal dashboard tracking these SLAs.

## Risks and mitigations — Anticipate "but what about…"

Data quality, seasonality, and labor concerns are manageable with staged rollouts, transparency, and strong overrides.

### Data quality and Seasonality
Taxonomy drift can be mitigated through scheduled reviews and A/B testing. Seasonal events (like snowstorms impacting trash pickup) require operations-state flags tied directly to the City operations desk to prevent invalid ticket creation.

### Workforce adoption
Agents must be involved early in the co-design process. Training should utilize real, complex cases, and leadership should celebrate wins when the tool successfully deflects avoidable rework, ensuring agents view the navigator as an assistive tool rather than a replacement.

## Source appendix
* **LA Controller 311 audit:** controller.lacity.gov/audits/311 [1]
* **Minneapolis 311 case study:** portal.cops.usdoj.gov/resourcecenter/content.ashx/cops-w0488-pub.pdf [4]
* **ICMA 311 guidance:** icma.org/sites/default/files/09-082%20311%20Final%20Report%2012-2-08.pdf [10]
* **AvePoint Citizen Services brochure:** cdn.avepoint.com/pdfs/en/AvePoint_Citizen_Services_Product_brochure.pdf [5]
* **Tempe 311 FCR:** arcgis.com/apps/dashboards/66e146cb50f74688bfbfc00c191e78d4 [3]; data-academy.tempe.gov/pages/441cd14ae84a4119a155cddcc64fe0e9 [6]
* **SQM Group industry FCR:** sqmgroup.com/resources/library/blog/FCR-benchmarking-industry-results-2021 [2]
* **NYC311 sanitation examples:** on.nyc.gov/MissedTrash [8]; portal.311.nyc.gov/article/?kanumber=KA-02097 [7]
* **Columbus bulk collection:** columbus.gov/Services/Trash-Recycling-Bulk-Collection/Bulk-Collection [9]

## References

1. *The 411 on 311: Calling for a Customer-First Approach*. https://controller.lacity.gov/audits/311
2. *First Call Resolution Benchmarking by Industry Results for 2021*. https://www.sqmgroup.com/resources/library/blog/FCR-benchmarking-industry-results-2021
3. *2.03 311 First-Call Resolution Rate (dashboard)*. https://www.arcgis.com/apps/dashboards/66e146cb50f74688bfbfc00c191e78d4
4. *[PDF] Building a 311 System: A Case Study of the City of Minneapolis*. https://portal.cops.usdoj.gov/resourcecenter/content.ashx/cops-w0488-pub.pdf
5. *Product Brochure_AvePoint Citizen Services*. https://cdn.avepoint.com/pdfs/en/AvePoint_Citizen_Services_Product_brochure.pdf
6. *2.03 311 First-Call Resolution Rate | Data Academy - Tempe*. https://data-academy.tempe.gov/pages/441cd14ae84a4119a155cddcc64fe0e9
7. *
	
		
		








  
Illegal Dumping · NYC311
*. https://portal.311.nyc.gov/article/?kanumber=KA-02097
8. *
	
		
		








  
Missed Trash, Recycling, or Compost Collection · NYC311
*. https://on.nyc.gov/MissedTrash
9. *Bulk Collection - City of Columbus, Ohio*. https://www.columbus.gov/Services/Trash-Recycling-Bulk-Collection/Bulk-Collection
10. *Call 311:*. https://icma.org/sites/default/files/09-082%20311%20Final%20Report%2012-2-08.pdf