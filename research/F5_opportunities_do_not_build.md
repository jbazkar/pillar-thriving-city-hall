# Hackathon Red Flags: Six City Hall Builds to Avoid—and What to Ship Instead

## Executive Summary

When building civic technology for "A Thriving City Hall," the line between a high-impact prototype and a harmful liability is often defined by legal constraints, data access, and the limits of automation. This report outlines six specific project directions that hackathon teams must avoid, validated by recent public-sector failures, legal frameworks, and infrastructure realities. 

The most critical insights driving these restrictions include:
* **No public RVA311 API exists:** Richmond's official channels direct residents to call 3-1-1 or 804-646-7000 [1] [2]. The city is not listed among the endpoints scanned by Open311 Status [3]. Any tool claiming "full integration" is misrepresenting its capabilities and risks mishandling resident data.
* **Unsupervised government chatbots cause legal and reputational harm:** New York City's MyCity chatbot, which cost nearly $600,000 to build, actively encouraged illegal business practices and was ultimately taken down in February 2026 after sustained public backlash [4] [5].
* **Automated procurement decisions invite legal protests:** The Government Accountability Office (GAO) routinely sustains bid protests when agencies fail to adequately document their evaluation rationale [6]. Furthermore, the OECD warns that AI in procurement faces severe transparency and bias risks that can lead to unfair decisions and legal challenges from unsuccessful bidders [7].
* **Copycat portals erode public trust:** Unofficial sites that mimic government services frequently confuse residents. The UK's Driver and Vehicle Licensing Agency (DVLA) was contacted over 1,200 times regarding unaffiliated websites that charged premiums for services that are free on official government portals [8].

To ensure hackathon teams deliver viable, safe, and impactful solutions, organizers must redirect participants away from decisive automation and unauthorized integrations, guiding them instead toward staff-assistive tools, policy navigators, and wayfinding utilities.

## Why These Six Directions Are Off-Limits—And How to Pivot

Each of the six "do-not-build" directions is restricted due to missing infrastructure, severe legal risk, or documented prior public-sector failures. However, every restricted concept has a scoped, safe, and high-impact alternative that teams can successfully prototype over a weekend.

### Snapshot Table: Six Red Flags, Harms, and Redirects

| Direction to Avoid | Validation & Evidence | Specific Harm Mechanism | Safer Redirect Pattern |
| :--- | :--- | :--- | :--- |
| **"Full RVA311 integration"** | No public API/Open311 endpoint; Richmond routes to phone [1] [3] [2]. | Misrepresentation; failed submissions; PII mishandling; DoS on forms. | Deep-link navigator + call/text pre-fill; explicit disclaimers; City engagement for future data. |
| **"AI replaces 311 agents"** | NYC MyCity bot gave illegal advice; cost ~$600k; taken down Feb 2026 [4] [5]. | Residents break laws; fines/evictions; severe brand damage. | Agent-assist RAG for staff; confidence thresholds; citation-only answers; mandatory human handoff. |
| **"Automated procurement compliance checker"** | OECD flags transparency risks [7]; GAO sustains protests over undocumented decisions [6]. | False "compliant" advice leads to illegal solicitations, protests, and unauthorized practice of law. | Policy navigator + clause finder with citations; checklists; "not legal advice" disclaimers. |
| **"Procurement award assistant"** | OECD warns skewed data favors certain bidders, inviting legal challenges [7]. | Discrimination; sustained bid protests; lawsuits; FOIA burdens. | Market scan and vendor discovery; risk flags to humans; audit trails; no automated recommendations. |
| **"Citywide service optimization"** | No cross-department data access; Code for America advises strict least-privilege access [9]. | Vaporware; non-demoable prototypes; undermines credibility. | One-department pilot on public data; clear user; measurable micro-outcome. |
| **"Portal replacement for rva.gov/RVA311"** | DVLA logged 1,210 contacts regarding copycat sites charging for free services [8]. | PII loss; fees for free services; trust erosion; resident confusion. | Wayfinding front door that deep-links; "unofficial" branding; zero PII capture; exit notices. |

## 1) Full RVA311 Integration — Validate the Constraint, Don't Fake It

There is no public integration path for Richmond's 311 system today. Faking an integration creates user harm, data security risks, and a loss of credibility.

**Validation and Precedents**
The official City of Richmond website directs residents to dial 3-1-1 or 804-646-7000 to submit service requests [1] [2]. Richmond does not expose an Open311 or AvePoint API, and the city is absent from the Open311 Status monitor, which scans 25 active endpoints globally [3]. While civic tech teams often resort to scraping when APIs are unavailable, this approach is highly brittle. For example, BetaNYC has utilized scraping for research dashboards, but noted that gathering and preparing such data is extremely time-consuming and requires constant maintenance [10].

**Harm Mechanisms**
If a tool claims to submit 311 requests without a backend API, users will believe a critical issue (e.g., a downed power line or broken water main) is "filed" when it is not, leaving latent safety hazards unresolved. Furthermore, scraping submission forms can cause automated Denial of Service (DoS) issues on city servers and route sensitive Personally Identifiable Information (PII) through insecure, non-compliant third-party services.

**The Redirect**
* Build a wayfinding or triage utility: Create an address lookup or category suggestion tool that provides pre-filled call/text/email handoffs to official channels.
* Include explicit notices: Prominently display "This tool does not submit requests to the City."
* Advocate for open data: Open a formal data-sharing request with the City to explore a future read-only feed.

## 2) AI Replaces 311 Agents — Don't Repeat NYC's Mistakes

Deploying a broad, resident-facing Large Language Model (LLM) chatbot without a human fallback is fundamentally unsafe. The public inherently trusts the "voice" of government, making AI hallucinations dangerous.

**Validation and Precedents**
New York City's MyCity chatbot serves as a primary cautionary tale. Launched in October 2023 to help business owners, the Microsoft-powered bot confidently provided "dangerously inaccurate" advice [4]. It falsely told landlords they did not have to accept Section 8 housing vouchers (which is illegal discrimination in NYC) and told employers they could take a cut of their workers' tips [4]. Despite adding disclaimers, the bot continued to provide false information [11]. Building the bot's foundations reportedly cost nearly $600,000, and the new mayoral administration ultimately took the bot offline on February 4, 2026, labeling it "functionally unusable" [5]. 

**Harm Mechanisms**
Confidently wrong legal information leads residents and business owners to commit violations, resulting in fines, evictions, or lawsuits [4]. Furthermore, inconsistent answers across different languages create severe equity impacts.

**The Redirect**
* Build staff-facing retrieval assistants: Create an "agent-assist" tool that searches vetted rva.gov content to help human 311 operators answer questions faster.
* Implement strict guardrails: Require citations for every claim, establish confidence gating, and mandate human review before any answer is given to a resident.
* For resident-facing tools: Build a search-first FAQ navigator that always links directly to official source pages and offers a "Call 311 now" escalation button.

## 3) Automated Procurement Compliance Checker — Advice Without a Law License

Compliance determinations require licensed human judgment and deep contextual understanding. Automated prototypes in this space risk the unauthorized practice of law and invite severe legal scrutiny.

**Validation and Precedents**
The OECD warns that AI in public procurement faces significant risks regarding algorithmic transparency and legal ambiguity [7]. Many jurisdictions lack formal regulations on AI use, leading to potential challenges from unsuccessful bidders who question the fairness of automated processes [7]. Furthermore, the Government Accountability Office (GAO) routinely sustains bid protests when agencies fail to adequately document their evaluations. In a recent 2025 decision, the GAO sustained a protest because the agency's evaluation was "conclusory and failed to identify any substantive support for its findings" [6]. A black-box AI tool that simply outputs "compliant" cannot survive this level of audit.

**Harm Mechanisms**
False "compliance" green lights can lead to illegal solicitations, improper contract awards, vendor litigation, and sustained bid protests that halt city operations.

**The Redirect**
* Build a procurement policy navigator: Design a question flow with controlled vocabulary that links directly to the Virginia Public Procurement Act and city policy.
* Output checklists, not verdicts: The tool should generate a checklist of requirements rather than a definitive "compliant/non-compliant" ruling.
* Add a "Request legal review" workflow: Generate an exportable audit packet (questions asked, sources cited) for human legal counsel to review and sign off on.

## 4) Procurement Award Assistant — Recommenders Invite Bias Claims

Algorithmic award recommendations create massive exposure to fairness and bias claims. Procurement awards have strict legal and ethical dimensions that a weekend prototype cannot safely navigate.

**Validation and Precedents**
The OECD highlights that AI systems with inadequate or skewed training data can favor certain bidders, leading to unfair procurement decisions [7]. Because an AI system can review bids in seconds, if bias exists, the "speed and scale of harm will also exceed that of a human review" [7]. Without algorithmic transparency, contracting authorities lack the independent capability to maintain and monitor these systems, inviting legal challenges [7].

**Harm Mechanisms**
Automated recommendations can result in perceived or real discrimination against minority-owned or small businesses. This leads to sustained protests, reputational damage, and massive Freedom of Information Act (FOIA) burdens as vendors demand to see the algorithm's decision-making process.

**The Redirect**
* Deliver market scans and supplier discovery: Build tools that help staff find potential vendors without ranking them for a specific award.
* Surface risk flags: Create a tool that highlights missing certifications or missing documentation for human adjudicators to review.
* Generate side-by-side dossiers: Output source-cited comparisons of vendors, but strictly prohibit the system from outputting "Award to Vendor X."

## 5) Citywide Service Optimization — Scope Creep Kills Demos

A tool claiming to analyze and optimize service delivery across all city departments is impossible to build effectively in a weekend. Without cross-departmental data access and defined line-of-business users, this direction results in vaporware.

**Validation and Precedents**
Richmond does not have an Open311 API [3]. Furthermore, best practices for government data, such as those from Code for America, suggest strict access controls based on the principle of least privilege, meaning users should only have the absolute minimum level of access necessary [9]. A hackathon team will not have the clearance to access cross-departmental operational data.

**Harm Mechanisms**
Attempting this scope leads to non-demoable prototypes, stakeholder fatigue, and a wasted weekend for the development team. It undermines the credibility of civic tech initiatives by promising impossible results.

**The Redirect**
* Pick one service, one user, and one public dataset.
* Define a single metric (e.g., speed to answer a specific FAQ) and ship a measurable, working micro-insight or process map.

## 6) Official Portal Replacement — Avoid Copycat Dynamics

Any tool that implies residents should use it instead of rva.gov or RVA311 for official service interactions is dangerous. Unofficial portals can easily be mistaken for authoritative channels, leading to financial and privacy harms.

**Validation and Precedents**
The UK's DVLA has issued severe warnings regarding copycat websites. Since January 2020, the DVLA was contacted 1,210 times by members of the public regarding third-party websites that dress up like legitimate webpages [8]. These sites often charge excessive premiums for services that are completely free on the official government site, such as changing an address on a driving licence [8] [12]. 

**Harm Mechanisms**
Portal replacements cause resident confusion, capture sensitive PII without proper security protocols, and often result in users paying fees for free municipal tasks. This severely erodes public trust and places an added support burden on the City to clean up the confusion.

**The Redirect**
* Build a "front door" navigator: Create a wayfinding tool with clear, prominent "unofficial" labeling.
* Capture zero PII: The tool must not ask for names, addresses, or contact info.
* Use exit notices: When a user is ready to take action, display a "You are now leaving this site to visit the official City of Richmond portal" notice, ensuring all actual transactions occur on rva.gov or RVA311.

## Cross-Cutting Guardrails Every Team Should Adopt

If a hackathon project touches resident interactions or municipal rules, teams must apply these minimum safeguards:

* **Human-in-the-loop by default:** No decisive automation should be permitted for legal, procurement, or compliance topics.
* **Source-grounding and citations:** AI tools must only answer from vetted, official content and must display citations and "last-updated" dates.
* **Confidence thresholds and abstention:** Systems must be programmed to say "I don't know—please contact 311" when confidence is low.
* **FOIA-ready logs:** Systems must log inputs, outputs, sources, and human approvals to ensure auditability.
* **Privacy-by-design:** Do not collect PII unless strictly necessary, and implement immediate data retention limits.

## Successful Patterns to Emulate from Other Jurisdictions

Instead of the restricted builds, teams should look to successful, narrowly scoped implementations currently working in other governments:

* **Internal Procurement Q&A:** The North Carolina Department of Information Technology (NCDIT) introduced an AI-powered chatbot to assist state agency staff with IT procurement processes. Available 24/7, it provides instant answers on accessing forms and understanding timelines, significantly reducing wait times [7].
* **Oversight and Risk Analytics:** Brazil's Comptroller General's Office developed "Alice," an AI-powered system that analyzes bids and public notices to identify risks and irregularities in real time. It automates large-scale auditing to support public officials, but it informs humans rather than making final decisions [7].

## Implementation Checklist per Redirect

To turn these redirections into shippable weekend plans, hackathon organizers should require teams to define the following before writing code:

1. **Define the User:** Is this for a resident, a 311 agent, or a procurement officer? (Pick exactly one).
2. **Identify the Dataset:** What specific, publicly available dataset or official URL is powering this tool?
3. **Scope the Output:** What is the exact output? (e.g., a checklist, a deep-link, a risk flag).
4. **Establish Guardrails:** How does the tool handle edge cases, and what is the explicit disclaimer shown to the user?
5. **Plan the Handoff:** How does the user transition from this prototype to the official City of Richmond system to complete their task?

## References

1. *About RVA 311 | Richmond*. https://www.rva.gov/citizen-service-and-response/about-rva-311
2. *RVA 311 Helpful Links | Richmond*. https://www.rva.gov/citizen-service-and-response/rva-311-helpful-links
3. *Open311 Status - 311 API monitoring and analysis*. https://status.open311.org/
4. *NYC’s AI Chatbot Tells Businesses to Break the Law – The Markup*. https://themarkup.org/artificial-intelligence/2024/03/29/nycs-ai-chatbot-tells-businesses-to-break-the-law
5. *Mamdani to kill the NYC AI chatbot we caught telling businesses to break the law – The Markup*. https://themarkup.org/artificial-intelligence/2026/01/30/mamdani-to-kill-the-nyc-ai-chatbot-we-caught-telling-businesses-to-break-the-law
6. *GAO Checks the Math: An Agency’s Failure to Document OCI and Best-Value Decision Results in Sustained Protest - SmallGovCon*. https://smallgovcon.com/gaobidprotests/gao-checks-the-math-an-agencys-failure-to-document-oci-and-best-value-decision-results-in-sustained-protest/
7. *AI in public procurement: Governing with Artificial Intelligence | OECD*. https://www.oecd.org/en/publications/2025/06/governing-with-artificial-intelligence_398fa287/full-report/ai-in-public-procurement_2e095543.html
8. *Motorists warned of websites charging a premium for DVLA services free on GOV.UK - GOV.UK*. https://www.gov.uk/government/news/motorists-warned-of-websites-charging-a-premium-for-dvla-services-free-on-govuk
9. *Getting Your Data Ready for AI — Code for America*. https://codeforamerica.org/news/getting-your-data-ready-for-ai/
10. *Data Design Challenges and Opportunities for NYC Community Boards - BetaNYC*. https://www.beta.nyc/publications/data-design-challenges-and-opportunities-for-nyc-community-boards/
11. *Malfunctioning NYC AI Chatbot Still Active Despite Widespread Evidence It’s Encouraging Illegal Behavior | THE CITY — NYC News*. https://www.thecity.nyc/2024/04/02/malfunctioning-nyc-ai-chatbot-still-active-false-information/
12. *Trading Standards alert: Suspicious websites warning – London Borough of Bromley*. https://www.bromley.gov.uk/news/article/873/trading-standards-alert-suspicious-websites-warning