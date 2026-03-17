# Avoiding "Robodebt-in-Miniature": What Richmond Must Learn from Failed Government Chatbots and Digital Service Tools

## Executive Summary

- **Automating eligibility/compliance without human review reliably produces massive false positives and life-altering harm:** Michigan's MiDAS auto-fraud system falsely flagged approximately 40,000 people, with a 93% error rate in a 22,000-case audit, leading to a $20 million class-action settlement after the state garnished wages and intercepted tax returns without due process [1] [2] [3]. **Mitigation:** Never auto-adjudicate resident eligibility or compliance; require human-in-the-loop for any adverse outcome and put a hard block on automated collections until a human review is complete.
- **"One-stop" benefits finders collapse when underlying data are wrong or stale:** The federal government's $11 million redesigned Medicare Plan Finder delivered inaccurate drug costs in 2019, prompting Nebraska to temporarily shut down a network of about 350 volunteer Medicare advisers for a day [4]. Similarly, a 2025 Medicare Advantage provider directory frequently produced erroneous in-network information, forcing CMS to create a special enrollment fix [5]. **Mitigation:** Treat data freshness as a first-class feature with visible data timestamps and dual-confirm prompts.
- **Biometric shortcuts erode trust and widen access gaps faster than they block fraud:** Following a firestorm of criticism from privacy advocates and lawmakers, the IRS abandoned its plan to require mandatory ID.me facial recognition in 2022, a technology that was used by 30 states and 10 federal agencies [6] [7]. **Mitigation:** Default to multiple, non-biometric verification paths with clear privacy disclosures and publish vendor standards.
- **Governance failures—not just tech—sink launches:** HHS ignored 18 written warnings ahead of HealthCare.gov's disastrous 2013 crash [8]. Similarly, Australia's Robodebt automated unlawful income averaging, wrongfully recovering AUS $746 million from 381,000 individuals [9]. A Royal Commission found "no meaningful human intervention" in the debt calculations [10]. **Mitigation:** Institute a pre-launch "red team" and legal check for any automated decision, and set a go/no-go gate tied to legal sign-off.
- **Hype without plumbing leads to abandonment and reputational drag:** Enfield Council's much-publicized "Amelia" AI chatbot (launched in 2016) promised to handle frontline service queries but faded without clear sustained use, illustrating the gap between conversational UI and deep service integration [11] [12]. **Mitigation:** Scope tightly to high-volume, low-complexity requests with end-to-end workflow integration before public fanfare.
- **"Black box" rules and coercive UX manufacture consent and compound harm:** MiDAS used extremely misleading questionnaires pre-filled with responses that would flag the applicant for fraud, and sent notices only to an online portal [2]. Robodebt reversed the burden of proof onto the accused [10]. **Mitigation:** Mandate plain-language flows, mailed and digital notices, and independent usability tests for due-process steps.

## Why These Failures Matter for Richmond

The same patterns that sank national systems appear in local pilots when scope, legality, governance, and data quality are underplayed. Government digital service failures rarely stem from a chatbot becoming "sentient"; they fail because agencies embed flawed legal logic into code, obscure uncertainty behind confident user interfaces, and remove human oversight from high-stakes decisions. For Richmond, understanding these failure modes is the difference between launching a helpful civic tool and triggering a localized crisis that harms vulnerable residents and exposes the municipality to liability.

### Richmond Context and Risk Appetite

To avoid these pitfalls, Richmond must establish absolute clarity on what the prototype will and won't decide. The pilot outcomes must be strictly defined: the tool should focus on information triage and routing, not definitive eligibility determinations or automated filings. Richmond must pre-commit to a foundational rule: "no adverse decisions without humans." By capping the system's authority, the city can test conversational interfaces safely while preserving due process.

## Failure Case Compendium

Failures in government digital services cluster around five themes: confidently wrong answers, trust erosion, abandonment, scope creep, and data staleness. The table below outlines the most prominent documented failures and the specific mitigations Richmond must adopt.

| Case | Primary Failure Category | What Failed (with evidence) | Consequences | Root Cause Pattern | Richmond Mitigation |
|---|---|---|---|---|---|
| **Medicare Plan Finder (2019)** | Confidently wrong answers | Inaccurate drug prices/coverage; Nebraska paused 350 advisors; brokers warned off tool; $2,700/month drug price differences [4]. | Mis-enrollment; irreversible plan choices for seniors. | Data quality gaps; UI hid uncertainty behind confident design. | Data freshness SLAs; show timestamps; source links; "verify with plan" prompts. |
| **Medicare Provider Directory (2025)** | Data staleness | Frequent provider in-network errors; CMS created a special enrollment period to fix the issue [5]. | Surprise bills, confusion, potential out-of-pocket costs. | New data integration issues; late plan uploads. | Time-boxed caches; "not for billing decisions" banners; reversible choices. |
| **MiDAS (MI UI, 2013–2015)** | Trust erosion; harm | 93% false fraud rate in 22k audit; ~40k falsely accused; $20M settlement [1] [2] [3]. | Financial harm; bankruptcies; wage garnishments; long litigation. | Auto-adjudication; coercive UX; portal-only notices. | Human review for adverse actions; mailed + digital notices; legal review of rules. |
| **Robodebt (AUS)** | Scope creep; illegality | Unlawful income averaging; 381,000 refunds; AUS $746M repaid; Royal Commission condemned lack of human intervention [10] [9]. | Severe distress, suicides; massive refunds; loss of public trust. | Illegal logic embedded in code; reversed burden of proof; opacity. | No automation without legal sign-off; publish algorithmic rules; clear appeal rights. |
| **IRS/ID.me (2022)** | Trust erosion; abandonment | IRS dropped mandatory face scans after backlash; GAO urged stronger oversight [6] [13] [7]. | Equity/privacy concerns; sudden policy reversal. | Single-path identity proofing; thin transparency regarding biometric data. | Multi-path verification; publish metrics; privacy impact assessments. |
| **HealthCare.gov (2013)** | Abandonment risk | 18 written warnings ignored before launch; early crash [8]. | Lost trust; expensive fixes; political fallout. | Ignored risk signals; big-bang release without adequate testing. | Phased rollout; load tests; strict go/no-go gates. |
| **Enfield "Amelia" (2016–2017)** | Abandonment | Hype over AI chatbot; unclear sustained deployment or benefits [11] [12]. | Perception of gimmickry; failure to deliver on "digital customer service" promises. | Chat UI without deep back-office service plumbing; overscoping. | Start with low-complexity, integrated workflows; define kill metrics. |

*Table 1: Cross-case comparison of government digital service failures and Richmond mitigations.*

## Deep Dives by Failure Category

Five recurring failure modes can be actively engineered out of the Richmond prototype if their root causes are understood.

### Confidently Wrong Answers

Hallucinations in government tools often occur by integration, not just by AI generation. When the $11 million redesigned Medicare Plan Finder launched, it presented highly confident but entirely inaccurate cost estimates. In one instance, a consultant found a $2,700 a month difference for a high blood pressure medication depending on which version of the drug was selected, but the tool's summary page confidently stated the medication was covered [4]. Similarly, the 2025 Medicare Advantage provider directory frequently produced erroneous and conflicting information about which providers were in-network [5]. 
**Guardrails:** Show data provenance, last-updated timestamps, and confidence scores. Limit the bot to quoting maintained source content and require user confirmation for high-impact steps.

### Trust Erosion

Trust is destroyed when tools bypass due process or force users into uncomfortable privacy trade-offs. The IRS faced a firestorm of criticism and had to abandon its plan to require millions of Americans to submit to a facial recognition check through ID.me [7]. In Michigan, the MiDAS system eroded trust by sending fraud notices only to an online portal that former claimants had no reason to check, and then aggressively garnishing wages and intercepting tax returns without holding a hearing [2] [3]. 
**Guardrails:** Implement multi-path identity verification, conduct fairness reviews, ensure mailed notices for critical alerts, and maintain public documentation.

### Abandonment

When staff or residents cannot rely on a tool, it becomes a costly ghost town. HealthCare.gov crashed within two hours of its launch because officials ignored 18 written warnings that the mammoth project was mismanaged and off course [8]. On a municipal level, Enfield Council launched the "Amelia" AI chatbot in 2016 in a blaze of publicity, touting it as a groundbreaking solution for customer service [12]. However, the project required a steep learning curve and massive human intervention, ultimately failing to transform the digital divide as promised [11] [12]. 
**Guardrails:** Mandate staff co-design, comprehensive training, and clear escalation paths. Ensure production-grade integrations exist before public relations pushes, and establish service-level KPIs.

### Scope Creep

Tools designed for simple tasks become dangerous when they silently expand into complex legal determinations. Australia's Robodebt scheme was touted to save billions by using algorithms to identify welfare overpayments, but it relied on a flawed and illegal method of "income averaging" that divided annual income evenly across fortnights [10] [9]. Michigan's MiDAS system similarly expanded its scope to automatically flag any data discrepancy as fraud, no matter how trivial [2]. 
**Guardrails:** Create strict scope cards for each feature. Require legal pre-clearance for any decision logic and use feature flags to control rollouts.

### Data Staleness

Data staleness is the silent killer of digital service tools. The 2025 Medicare Advantage provider directory errors were largely attributed to new data integration issues and the fact that participating plans were given until January 1 to upload their network information—almost a month after the end of open enrollment [5]. When underlying content isn't maintained, the chatbot confidently serves expired truth.
**Guardrails:** Enforce strict update windows, monitor data drift, and program the bot to degrade gracefully to a "contact a human" prompt when data is stale.

## Root Causes Synthesis

The common failure pattern across these case studies is not "AI gone rogue," but rather weak content and policy governance. Across the Medicare Plan Finder (stale/erroneous plan data), MiDAS and Robodebt (invalid legal logic embedded directly into code), and ID.me (policy decisions outpacing privacy risk assessments), the root cause is missing stewardship. 

Agencies frequently launch tools without accountable owners who maintain truth, legality, and equity. They rely on over-centralized, big-bang launches without phased validation. To survive, Richmond must stand up a Content and Policy Governance Board with strict SLAs for updates, data provenance tracking, bias/impact reviews, and sunset rules when source systems change.

## Design Principles Derived from Failures

To convert these lessons into practice, Richmond should adopt the following ten design principles for its prototype:

1. **No adverse decisions without humans:** Never allow the system to automatically deny benefits, issue fines, or trigger collections.
2. **Data provenance first:** Every answer must cite and link to maintained sources with visible timestamps.
3. **Graceful degradation:** If data is stale or confidence is low, the bot must stop guessing and route the user to a human.
4. **Multi-path access and identity:** Never block residents behind a single vendor or biometric requirement.
5. **Plain-language + mailed notices:** Any communication regarding rights or obligations must be sent via mail and written in plain language, not just portal alerts.
6. **Published scope and limitations:** Clearly state what the bot can and cannot do at the entry point.
7. **Observability:** Log all prompts, decisions, and handoffs, and conduct weekly reviews.
8. **Reversibility:** Build provisional actions and structured remediation paths for when the tool gets it wrong.
9. **Legal and ethics gates:** Require pre-launch sign-offs and quarterly audits.
10. **Co-design and kill metrics:** Build with frontline staff and residents, and define strict kill/keep metrics for the pilot.

## Language and Architecture Patterns that Drove Failures

Specific anti-patterns repeatedly triggered harm in the documented case studies. Richmond must actively replace these with safer architectural and linguistic choices.

| Anti-pattern (Language/Architecture) | Seen In | Why Harmful | Replace With |
|---|---|---|---|
| **"You are eligible for X"** (without source/date) | Medicare Plan Finder | Over-confidence hides data errors and leads to irreversible choices. | "Based on [source], last updated [date], you may be eligible; confirm with [link] or speak to an agent." |
| **Income "averaging" shortcuts in rules** | Robodebt | Resulted in unlawful determinations and massive false debts. | Statutory-compliant logic; mandatory legal sign-off on all algorithmic rules. |
| **Auto-adjudication and collections** | MiDAS | Caused a 93% false positive rate and severe due-process violations [1] [3]. | Human review before any adverse action; mailed notices; clear appeal path. |
| **Single-path biometric ID** | IRS/ID.me | Triggered equity and privacy backlash [7]. | Multi-path ID verification; publish metrics; offer staffed alternatives. |
| **Big-bang launch ignoring risk gates** | HealthCare.gov | Led to an early crash and massive loss of public trust [8]. | Phased rollouts; load testing; strict go/no-go criteria. |
| **Chat UI without back-office integration** | Enfield Amelia | Resulted in low containment and ultimate abandonment [11]. | Build end-to-end flows for a few specific tasks, then expand. |
| **Hidden uncertainty in UI** | Medicare Plan Finder | Users over-rely on bad data, leading to financial harm [4]. | Confidence labels; "how I got this" links; dual-confirm prompts. |

*Table 2: Anti-patterns observed in failed government tools and recommended mitigations.*

## How to Communicate Limitations Without Undermining Credibility

Transparency increases trust when paired with concrete safety nets. The prototype must communicate its boundaries clearly without sounding useless. 

- **Entry banner:** "This assistant gives general guidance from [agencies]; it does not make final eligibility decisions. Last content update: [timestamp]."
- **Just-in-time guardrails:** "This answer is based on [policy], updated [date]. Before applying, confirm details with [link] or connect with an agent."
- **Error/revision script:** "We found an issue affecting [answers about X] between [dates]. We're sorry. Here's what we've changed, and how we'll make it right [remedy]."
- **Commitment statement:** "If our guidance led to a missed deadline or incorrect filing during [window], we'll help you correct it."

## Implementation Playbook for the Richmond Prototype

A safe launch is engineered, not assumed. Richmond should follow this phased approach:

### Phase 0: Governance Setup
Create dedicated roles: a Content/Data Steward, a Legal/Ethics Reviewer, and an Incident Lead. Define strict SLAs for content updates, review cadences, and identify the absolute data sources of record.

### Phase 1: Scope and Service Plumbing
Select 5 to 10 high-volume, low-complexity intents that have full back-office integration. Build hard "stop conditions" into the architecture so the bot halts and escalates when it encounters low confidence or stale content.

### Phase 2: Pre-launch Validation
Conduct rigorous hallucination and bias tests, alongside load tests. Execute a "red team" exercise involving legal, UX, and service staff. Run a dry-run with 50 to 100 residents to measure containment rates, escalation quality, and accuracy.

### Phase 3: Phased Rollout and Monitoring
Soft launch the prototype to 10% of web traffic. Ensure there is a highly visible "report an issue" button. Conduct weekly risk reviews and publish a public changelog and list of known issues.

## Risk Register with Triggers and Countermeasures

To ensure accountability, Richmond must make failure modes visible and assign clear ownership.

| Failure Mode | Leading Indicator | Countermeasure | Owner |
|---|---|---|---|
| **Confidently wrong routing/eligibility** | Spike in escalations or complaints on a specific topic. | Disable the topic; add warning banners; fast-track content fix. | Content Steward |
| **Data staleness** | Source update misses its defined SLA window. | Auto-deprecate related answers; show "call us" prompt. | Content Steward |
| **Equity/access barriers** | Drop-off in usage among certain demographics. | Add non-digital channels; simplify language; review ID requirements. | UX Lead |
| **Legal/process risk** | Bot begins suggesting obligations or definitive eligibility. | Replace with "informational only" disclaimers; trigger legal review. | Legal Reviewer |
| **Trust erosion post-incident** | Negative feedback sentiment or media scrutiny. | Publish a public post-mortem; offer immediate remediation. | Incident Lead |

*Table 3: Launch safeguards and risk register for the Richmond prototype.*

## What Civic Technologists Flag as Common Design Mistakes

The failures documented above align perfectly with the most common design mistakes flagged by civic technologists and federal playbooks (such as the OMB Digital Services Playbook and GAO oversight reports):

- **Building for policy intent, not legal text:** Skipping legal sign-off leads to unlawful systems, as seen when Robodebt automated income averaging without ensuring it met statutory requirements [10].
- **Treating content as "set and forget":** Failing to treat content as an operational product leads to the data staleness that plagued the Medicare Plan Finder [5].
- **Single-path identity:** Forcing users through a single vendor or biometric check limits accessibility and triggers backlash, as seen with ID.me [6].
- **Launching chat UIs without service integration:** Deploying a conversational interface without the back-office plumbing to actually resolve issues leads to abandonment, as seen with Enfield Council [11].
- **Hiding uncertainty:** Failing to provide appeal paths and obscuring the system's limitations manufactures consent and compounds harm, as seen in the MiDAS disaster [2] [3].

## Appendix: Evidence Excerpts and References

Every claim in this report traces directly to public reports, audits, or investigations:
- **Medicare Plan Finder:** ProPublica (2020) documented the $11 million tool giving wrong insurance information, including $2,700/month drug price differences [4]. AARP and The Washington Post (2025) documented the provider directory errors and CMS's special enrollment relief [5].
- **MiDAS (Michigan UI):** The Michigan Auditor General and court filings (2016–2024) documented the 93% false fraud rate, the 40,000 falsely accused residents, and the $20 million settlement [1] [2] [3] [14].
- **Robodebt (Australia):** The Royal Commission Report (2023) and subsequent reporting documented the unlawful income averaging, the lack of human intervention, and the AUS $746 million refunded to 381,000 individuals [10] [9].
- **HealthCare.gov:** HHS OIG and The Washington Post (2016) documented the 18 ignored written warnings prior to launch [8].
- **IRS/ID.me:** The Washington Post and GAO (2022–2025) documented the backlash and reversal of the mandatory facial recognition plan used across 30 states and 10 federal agencies [6] [13] [7].
- **Enfield Amelia:** Contemporary coverage and retrospectives illustrated the hype and subsequent abandonment risk of the 2016 chatbot launch [11] [12].

## References

1. *Case Over the Michigan Unemployment Insurance Agency's ...*. https://stpp.fordschool.umich.edu/sites/stpp/files/2024-08/stpp-midas-explainer.pdf
2. *Michigan Unemployment Insurance False Fraud Determinations | Benefits Tech Advocacy Hub*. https://btah.org/case-study/michigan-unemployment-insurance-false-fraud-determinations.html
3. *                Cahoo v. SAS Analytics Inc., No. 18-1296 (6th Cir. 2019) :: Justia    *. https://law.justia.com/cases/federal/appellate-courts/ca6/18-1296/18-1296-2019-01-03.html
4. *The $11 Million Dollar Medicare Tool That Gives Seniors the Wrong Insurance Information — ProPublica*. https://www.propublica.org/article/the-11-million-dollar-medicare-tool-that-gives-seniors-the-wrong-insurance-information
5. *Errors in Medicare Plan Finder Mislead Users on Coverage*. https://www.aarp.org/medicare/medicare-plan-finder-glitches/
6. *ID.me says its service will no longer require facial recognition - The Washington Post*. https://www.washingtonpost.com/technology/2022/02/09/irs-idme-facial-recognition-login/
7. *IRS abandons ID.me facial recognition plans - The Washington Post*. https://www.washingtonpost.com/technology/2022/02/07/irs-idme-face-scans/
8. *
		Investigators Find HHS Officials Missed Warnings About Healthcare.gov's Early Troubles - KFF Health News	*. https://kffhealthnews.org/morning-breakout/investigators-find-hhs-officials-missed-warnings-about-healthcare-govs-early-troubles/
9. *Australia’s Robodebt scheme: A tragic case of public policy failure | Blavatnik School of Government*. https://www.bsg.ox.ac.uk/blog/australias-robodebt-scheme-tragic-case-public-policy-failure
10. *The flawed algorithm at the heart of Robodebt   | Pursuit by the University of Melbourne*. https://pursuit.unimelb.edu.au/articles/the-flawed-algorithm-at-the-heart-of-robodebt
11. *Could AI chatbots be the new face of local gov? Enfield Council thinks so*. https://diginomica.com/ai-chat-bots-new-face-local-gov-enfield-council-thinks
12. *Whatever Happened to Amelia?. Hype v Reality in Public Sector AI | by Dr Jim Hamill | Medium*. https://medium.com/@jim.hamill_73113/whatever-happened-to-amelia-dcfcf4f5bc4d
13. *IRS Should Strengthen Oversight of Its Identity-Proofing ...*. https://www.gao.gov/assets/gao-25-107273.pdf
14. *Michigan to settle unemployment false fraud lawsuit for $20 million*. https://www.freep.com/story/news/local/michigan/2022/10/20/michiganunemployment-false-fraud-lawsuit/69577567007/