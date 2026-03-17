# Red Flags for A Thriving City Hall — Preventing Harmful AI Pitfalls in RVA311 Prototypes

## Executive Summary

As Richmond prepares for the Hack for RVA event in March 2026, participants tackling the "A Thriving City Hall" pillar will likely leverage generative AI to build civic tech prototypes [1]. While AI offers immense potential to streamline government services, deploying chatbots and service navigators without strict guardrails introduces severe risks. Recent high-profile failures in government AI demonstrate that overconfident, hallucinating, or legally inaccurate bots do not just cause frustration—they create real-world harm, erode public trust, and generate legal liability. 

This report identifies the five most serious risk domains for RVA311-adjacent prototypes: over-claiming routing accuracy, hallucinating service categories, providing wrong eligibility information, falsely claiming official status, and relying on stale data. To prevent these pitfalls, developers must shift from a paradigm of "answering every question" to "calibrated abstention"—where the AI is designed to safely say "I don't know" and route users to official human channels. By implementing closed taxonomies, strict citation requirements, and prominent prototype disclaimers, hackathon teams can build tools that genuinely support a thriving, trustworthy City Hall.

## Why These Red Flags Matter — Preventing Resident Harm and Preserving Trust

Small AI mistakes can quickly cascade into significant civic failures. When a resident follows incorrect routing advice or acts on false eligibility information, the downstream effects include delayed emergency responses, escalated staff burdens to fix misrouted tickets, and a profound loss of trust in local government. 

### Richmond Context: A Thriving City Hall and RVA311’s Mission

The City of Richmond's RVA311 system is the central nervous system for non-emergency civic services. In 2024, RVA311 provided assistance or supported City partners in responding to over 83,000 requests [2]. The system partners with over 30 different departments, agencies, and teams, handling everything from pothole repairs and streetlight maintenance to tax billing and social services programs [2]. Because RVA311 is a highly structured routing mechanism, any prototype built to interface with it must respect its exact categories and channels. A tool that misroutes a request or invents a department undermines the efficiency that RVA311 was built to achieve.

### What Goes Wrong Elsewhere: Lessons from NYC, Air Canada, and the GSA

Publicized failures of AI chatbots highlight the severe legal, reputational, and operational risks of deploying unconstrained AI in public-facing roles:
* **New York City's MyCity Chatbot**: Launched to help business owners, this official AI chatbot confidently provided illegal advice. It told landlords they did not have to accept Section 8 housing vouchers (which is illegal discrimination) and told restaurant owners they could take a cut of their workers' tips (which is wage theft) [3]. 
* **Air Canada Liability**: In *Moffatt v. Air Canada* (2024), a Canadian tribunal held the airline liable for negligent misrepresentation after its chatbot hallucinated a non-existent bereavement fare policy [4] [5]. The tribunal rejected the defense that the chatbot was a separate entity, establishing that organizations own what their AI says [4] [5].
* **GSA Internal AI Evaluation**: A 2025 evaluation of the U.S. General Services Administration's internal GenAI tool found that 33% of users cited inaccurate content and 33% cited poor output quality as top barriers to increased usage [6]. 

## Risk Domain Deep Dives — What’s Really Happening, Why It Matters, What To Do

The following table outlines the five critical risk domains, potential failure scenarios, and required mitigation strategies for RVA311 prototypes.

| Risk Domain | Specific Risk Description | Example Failure Scenario | Mitigation Strategy |
| :--- | :--- | :--- | :--- |
| **1. Over-claiming routing accuracy** | The tool confidently routes ambiguous queries, presenting guesses as facts. Inaccurate content is a top barrier to AI adoption [6]. | A resident reports a "downed line." The bot confidently routes it to "Public Works - Tree," but it is a live utility line requiring DPU or 911. Result: delayed hazard response. | **Calibrated abstention**: If confidence is below a threshold or hazard keywords are present, the bot must state "I can't be sure — please call 3-1-1" and offer multiple candidate categories. |
| **2. Hallucinating service categories** | The LLM invents RVA311 categories, department names, or service types that do not exist in the actual system. | The bot suggests routing a ticket to the "Richmond Sanitation Authority – Graffiti Unit" (a fabricated entity). The resident believes the request is filed, but nothing happens. | **Closed-world taxonomy**: Only allow categories that exist in the live RVA311 catalog. Implement a "no source, no output" rule. If no match is found, surface "Not an RVA311 service." |
| **3. Wrong eligibility/compliance info** | The tool makes legal eligibility or compliance determinations, crossing into unauthorized legal advice and creating liability [3] [7]. | The bot tells a small business "you qualify for this tax relief program," which is later proven wrong. The resident misses deadlines or faces financial penalties. | **Prohibit determinations**: Never state eligibility or compliance. Require authoritative citations for any rule-like statement. Otherwise, defer to official pages or human agents. |
| **4. Claiming official status** | The prototype implies it is an official City channel. Users believe submissions are actively being routed, damaging trust when they fail. | The demo UI uses the City seal and a "Submit" button. Residents assume their requests are officially filed. Failures reflect poorly on City legitimacy. | **Distinct branding & phase banners**: Use prominent "Prototype — Not a City service" banners. Avoid official government identifiers. Always provide the official 3-1-1 phone number and rva311.com link [2]. |
| **5. Data staleness** | The bot relies on outdated rva.gov content or retired 311 categories, providing obsolete routing advice. | A City department merges or a category is retired. The bot continues recommending the old path, causing dead ends and staff escalations. | **Freshness signaling**: Show per-result source and last-checked timestamps. Utilize metadata fields like `valid_from` and `valid_to` [8]. Warn users when data exceeds SLA thresholds. |

### 1) Over-Claiming Routing Accuracy — Calibrate Confidence, Not Swagger

A 311 helper that confidently routes every query, even ambiguous ones, is dangerous. When an AI guesses rather than abstaining, it causes misfiling, delays critical services, and creates rework for City staff. The GSA's evaluation of its GenAI chat tool revealed that inaccurate content was a primary barrier to adoption for 33% of users [6]. 

To mitigate this, prototypes must utilize calibrated abstention. If a query is ambiguous or contains hazard keywords (e.g., "sparks," "gas," "downed"), the bot should default to a safe deflection: "I'm not sure — here is how to reach 3-1-1." Tracking and improving the quality of these safe deflections should be a primary KPI, rather than simply measuring the raw answer rate.

### 2) Hallucinated Service Categories — Close the World and Cite Sources

LLMs are prediction engines that optimize for plausibility, not truth [7]. When asked to match vague intents, they frequently invent plausible-sounding but entirely fake entities. In the context of RVA311, an AI might confabulate a department name or a specific service category that does not exist in the City's actual catalog.

Prototypes must enforce a closed-world taxonomy. The bot should only be allowed to present categories that exist in the live RVA311 system. By implementing a "no source = no output" architecture, the AI is blocked from generating any answer it cannot trace to a specific, current section of the official catalog [7]. If there is no exact match, the UX should clearly state, "This does not appear in RVA311," and provide the 3-1-1 phone number.

### 3) Wrong Eligibility/Compliance Info — Avoid Legal Advice Traps

Providing incorrect eligibility or compliance information is the most legally perilous risk domain. The NYC MyCity chatbot disaster demonstrated how an official-looking bot can confidently dispense illegal advice regarding housing discrimination and wage laws [3]. Furthermore, the *Moffatt v. Air Canada* case established that organizations are liable for the negligent misrepresentations made by their chatbots, regardless of generic website disclaimers [4] [5]. 

Civic tech tools must be hard-coded to ban eligibility and compliance determinations. If a user asks if they qualify for a program, the bot must not answer "yes" or "no." Instead, it must retrieve and cite the exact statutory or official source text, provide a link to the official page, and offer a "Talk to a person" call-to-action. 

### 4) Claiming Official Status — Design Out Confusion

Users naturally infer authority from government branding, seals, and.gov look-and-feel elements. If a prototype looks official, users will assume their submissions are being routed through official channels. When NYC's chatbot began dispensing illegal advice, the city quietly updated the site to describe it as a "beta product" that may provide inaccurate responses—but this post-hoc fix was too late to prevent the initial harm and negative press [9].

To prevent confusion, prototypes must use overt non-official status indicators. The GOV.UK Design System mandates the use of a "Phase banner" (e.g., Alpha or Beta) to explicitly show users that a service is new and still being worked on [10]. Conversely, the U.S. Web Design System (USWDS) uses specific banners to identify *official* websites of government organizations [11]. Prototypes should adopt the GOV.UK phase banner approach ("Prototype — Not an official City service") and strictly avoid USWDS official identifiers or City of Richmond seals.

### 5) Data Staleness — Freshness Signaling and Fallbacks

City websites change, departments reorganize, and 311 categories are updated. A tool trained on stale content will give outdated routing advice, leading to dead ends. 

Best practices for content freshness in human services data involve explicit metadata. The Open Referral Human Services Data Specification (HSDS) utilizes `valid_from` and `valid_to` fields to indicate the exact period for which data should be considered valid, ensuring older data can be identified [8]. Prototypes should display a "Checked against RVA311 as of [time]" stamp on every result. If the source age exceeds a specific threshold (e.g., 30 days), the UI should flag that the content may be outdated and defer to a human referral.

## Non-Negotiable Guardrails and Technical Controls

To ensure safety and build trust, any "A Thriving City Hall" prototype must implement the following three non-negotiable guardrails:

1. **No legal, eligibility, or compliance determinations**: The bot must never state whether a user is eligible for a service or compliant with a rule. It must require a citation to the exact RVA311 or rva.gov source for any rule-like statement; otherwise, it must abstain and escalate to 3-1-1.
2. **Closed taxonomy and "No Source = No Output"**: The bot may only present live RVA311 categories with exact names and links. If a category is not found or confidence is low, it must show "I'm not sure" and offer human contact.
3. **Prominent "Not an official City service" status**: Every screen and response must feature a phase/experimental banner. The UI must include explicit routing disclaimers stating that submissions are not sent to City systems and direct users to rva311.com or 804-646-7000 for official service [2].

**Required Technical Controls**:
* **Citation Enforcement**: Implement constrained decoding or retrieval-augmented generation (RAG) that blocks generation if a valid source citation is missing [7].
* **Hazard Keyword Overrides**: Hardcode triggers for emergency keywords (e.g., "fire", "wire", "accident") that immediately bypass the LLM and display 911/emergency routing instructions.
* **Incident Kill Switch**: Define incident thresholds and maintain a deactivation plan to immediately pause the bot if harmful error patterns emerge.

## UX Language and Status Patterns that Reduce Risk

Clear status indicators, humility in AI responses, and precise linking are far more effective than generic, passive disclaimers. 

**Language Patterns to USE**:
* "Prototype — Not an official City service."
* "I'm not sure. For official assistance, please call 3-1-1."
* "According to [Exact Source Name], typically..." followed by a deep link.
* "If this is an emergency, call 911. For non-emergencies, call 3-1-1."

**Language Patterns to AVOID**:
* *"You are eligible/approved/compliant."* (Replaces with: "Here are the official criteria" + link).
* *"We submitted your request" or "Your ticket is filed."* (Replace with: "This prototype cannot file requests. Submit at rva311.com").
* *"Official," "endorsed by," or "in partnership with the City."* (Use neutral prototype branding).
* *"Always/never/must"* when summarizing complex rules.
* Fabricated entity names (e.g., "Richmond Graffiti Authority").

## Evaluation, Telemetry, and Governance — Prove Safety, Improve Fast

Hackathon judges and City stakeholders will look for prototypes that measure safety, not just capability. Teams should track and optimize for abstention quality. 

Key metrics should include the **abstention rate** (how often the bot safely says "I don't know"), **safe-deflection success** (how often users click the provided 3-1-1 link after an abstention), and **hallucination incidents** (which should be strictly zero). Teams should maintain an audit trail logging every query, the exact retrieval chunks used, and the citation provided [7]. This forensic trail proves that the system is grounded in actual City data rather than guessing.

## Demo Strategy for Judges — Show Safety-by-Design

When presenting to the Hack for RVA judges, teams should actively demonstrate their safety guardrails live. 

**Recommended Demo Script**:
1. **Ambiguous Request**: Type a vague query. Show the bot safely abstaining and offering the 3-1-1 phone number.
2. **Non-Existent Service**: Ask for a fabricated service. Show the bot explicitly stating "This is not in RVA311" rather than hallucinating a department.
3. **Valid Category**: Ask a standard 311 question (e.g., pothole). Show the bot returning the exact RVA311 category name, a deep link, and a freshness timestamp.
4. **Edge Eligibility Query**: Ask if a business qualifies for a specific tax break. Show the bot declining to make a determination, citing the official rva.gov page, and offering a "Talk to a person" CTA. 

By emphasizing the kill switch, strict sourcing, and audit logging, teams will prove they understand that in civic tech, reliability and trust are the ultimate metrics of success.

## References

1. *City of Richmond to Partner in City’s First-Ever Civic Hack-a-thon | Richmond*. https://www.rva.gov/press-releases-and-announcements/news/city-richmond-partner-citys-first-ever-civic-hack-thon
2. *About RVA 311 | Richmond*. https://www.rva.gov/citizen-service-and-response/about-rva-311
3. *NYC’s AI Chatbot Tells Businesses to Break the Law – The Markup*. https://themarkup.org/artificial-intelligence/2024/03/29/nycs-ai-chatbot-tells-businesses-to-break-the-law
4. *Airline held liable for its chatbot giving passenger bad advice - what this means for travellers*. https://www.bbc.com/travel/article/20240222-air-canada-chatbot-misinformation-what-travellers-should-know
5. *BC Tribunal Confirms Companies Remain Liable for Information Provided by AI Chatbot*. https://www.americanbar.org/groups/business_law/resources/business-law-today/2024-february/bc-tribunal-confirms-companies-remain-liable-information-provided-ai-chatbot/
6. *Evaluating Generative Artificial Intelligence (GenAI) chat tools | Office of Evaluation Sciences*. https://oes.gsa.gov/projects/2533-evaluating-gen-ai-chat-tools/
7. *NYC AI Chatbot Gave Illegal Advice: Government AI Liability*. https://veriprajna.com/insights/government-ai-chatbot-liability
8. *Field and Object Guidance — Open Referral Data Specifications 3.0.1 documentation*. https://docs.openreferral.org/en/latest/hsds/field_guidance.html
9. *Malfunctioning NYC AI Chatbot Still Active Despite Widespread Evidence It’s Encouraging Illegal Behavior | THE CITY — NYC News*. https://www.thecity.nyc/2024/04/02/malfunctioning-nyc-ai-chatbot-still-active-false-information/
10. *Phase banner – GOV.UK Design System*. https://design-system.service.gov.uk/components/phase-banner/
11. *Banner | U.S. Web Design System (USWDS)*. https://designsystem.digital.gov/components/banner/