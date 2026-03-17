# Building One City, Not Two: De-risking Richmond’s Digital Services for LEP, Low-Broadband, and Accessibility Needs

## Executive Summary

As Richmond accelerates its digital transformation through hackathons and rapid prototyping, there is a critical risk of inadvertently creating a two-tier service experience. Prototypes optimized for high-speed internet, English fluency, and advanced digital literacy will systematically exclude the residents who rely on city services the most. 

**Key Strategic Insights:**
* **Broadband gap creates a built-in exclusion tier:** With 86.2% of Richmond households having a broadband subscription [1], approximately 13.8% remain without at-home broadband. Prototypes cannot rely on online-only flows; they must include phone/311 fallbacks and in-person handoffs.
* **Poverty amplifies under-connection risk:** Richmond Public Schools (RPS) report 100% of students fall under Free and Reduced Meal eligibility [2], signaling widespread economic constraints that likely correlate with mobile-only internet dependence and data caps.
* **Machine translation is the default, but the City acknowledges its limits:** The official rva.gov website uses Google Translate but explicitly disclaims it as a "generic translation," affirming residents' rights to professional translation via the City's Language Access Plan and directing them to 311 [3] [4].
* **LEP demand is material and rising:** RPS English Learner metrics show only a 6% proficiency rate and a 47% annual progress rate [2]. Furthermore, the number of recently arrived English Language Learners exempted from state reading assessments fluctuated from 20 (2022-2023) to 56 (2023-2024) and 30 (2024-2025) [2].
* **"Wrong answer" risk is real:** The City already walls off liability for data inaccuracies, such as in its GIS mapping [5]. A service navigator that misroutes residents for critical deadlines (e.g., eviction defense) can cause material harm.
* **Complexity correlates with drop-off:** Dense text and multi-step logic will compound abandonment, especially for Limited English Proficiency (LEP) families. 

## Problem Framing — Richmond’s digital two-tier risk is avoidable with explicit guardrails

The enthusiasm of a hackathon often biases development toward cutting-edge, resource-heavy web applications. However, without explicit design guardrails for LEP, low-broadband, and assistive technology users, these prototypes will work flawlessly for the digitally fluent while failing those who need the most help. If a resident uses a service navigator and it provides inaccessible or incorrect information regarding legal rights or benefits, they could be worse off than if the tool never existed. 

## Digital Divide: Connectivity, Devices, and Cost Constraints in Richmond

### 86.2% broadband vs 13.8% offline—what that means for eligibility and deadlines

According to U.S. Census Bureau QuickFacts (2020-2024), 94.4% of Richmond city households have a computer, but only 86.2% have a broadband internet subscription [1]. This 13.8% gap represents tens of thousands of residents who may rely on public Wi-Fi, cellular data, or no internet at all. For time-sensitive government services, assuming a stable broadband connection can result in missed deadlines and denied eligibility.

### Poverty-linked under-connection: 100% FRM coverage at RPS as an affordability proxy

Richmond Public Schools report that 100% of their students are eligible for Free and Reduced Meals [2]. This universal eligibility is a strong proxy for widespread economic constraints among Richmond families. Financial limitations directly impact digital access, often resulting in reliance on mobile-only internet with strict data caps. 

### What we don’t know yet—and how to learn it during prototyping

While we know the overarching broadband statistics, granular data on cellular-only households at the neighborhood level requires further validation. Hackathon prototypes must be instrumented to collect device heuristics, bandwidth throttling tests, and offline retries to build a clearer picture of the mobile-only user base.

## Language Access: Demand, Translation Quality, and Legal Signals

### EL proficiency is just 6% with 47% progress—expect comprehension barriers

The language barrier in Richmond is significant. RPS data reveals that out of 4,220 English Learners, only 240 (6%) have attained proficiency, while 47% are making progress [2]. This indicates that a large portion of the adult population in these households likely faces severe comprehension barriers when navigating complex government terminology in English.

### City’s machine translation disclaimer + Language Access Plan = minimum obligations

The City of Richmond recognizes the limitations of automated translation. Across rva.gov, a disclaimer states that the site uses Google Translate to provide a "generic translation" and that this "does not substitute your right to obtain a professional translation provided by the City of Richmond as part of the Language Access Plan" [3] [4]. Residents are instructed to call 804-646-7000 or 3-1-1 for assistance [3] [6]. Prototypes must adhere to this standard by offering clear pathways to human interpreters.

### Translation quality in government contexts: when Google Translate fails

Google Translate frequently struggles with government service terminology, legal notices, and benefit names. Relying solely on machine translation for critical services can lead to disastrous misunderstandings. 

### Candidate priority languages for Richmond prototypes

While Spanish is the most prominent non-English language, recent data shows that Korean is the third most commonly spoken language in Virginia, with Chinese, Vietnamese, and Arabic rounding out the top five [7] [8].

| Language | Estimated State Rank | Priority Tier | Channel Strategy | Rationale |
| :--- | :--- | :--- | :--- | :--- |
| Spanish | 2nd | Tier 1 | Human-reviewed UI + 311 | Largest LEP population; highest daily service demand. |
| Korean | 3rd [7] | Tier 2 | Machine Translation + 311 | Growing demographic in VA; requires robust fallback to human interpreters. |
| Chinese | Top 5 [8] | Tier 2 | Machine Translation + 311 | Significant presence; complex character rendering requires UI testing. |
| Vietnamese | Top 5 [8] | Tier 2 | Machine Translation + 311 | High community presence; distinct linguistic structure from English. |
| Arabic | Top 5 [8] | Tier 2 | Machine Translation + 311 | Requires Right-to-Left (RTL) UI support in prototypes. |

*Takeaway: Prototypes must natively support Spanish with human-reviewed text, while providing accessible 311 routing for other top languages where machine translation may fail.*

## Accessibility: WCAG 2.1 AA pitfalls in chatbots and dynamic UIs

### High-risk violations to preempt in hackathon builds

Dynamic web applications and chatbots frequently violate WCAG 2.1 AA standards by trapping keyboard focus, failing to announce status changes to screen readers, and utilizing poor color contrast. Fixing these issues during the hackathon is exponentially cheaper than retrofitting them later.

### Seniors-first accessibility: large targets, high contrast, predictable navigation

For Richmond's senior population, accessibility means more than just screen reader support. It requires large tap targets, high-contrast text, the elimination of tiny modals, and predictable back/next navigation to accommodate declining vision and motor control.

### Screen reader compatibility in SPAs: dynamic content that "talks" correctly

Single Page Applications (SPAs) often fail to alert screen readers when new content loads. Proper ARIA live regions and focus management are required to ensure visually impaired users are not left behind.

### Top 10 WCAG 2.1 AA checks for hackathon bots/navigators

| Violation Area | User Impact | Quick Test | Code Fix / Strategy |
| :--- | :--- | :--- | :--- |
| Keyboard Traps | Motor-impaired users cannot navigate | Tab through the entire app without a mouse | Ensure `tabindex` is logical; provide visible focus states. |
| Color Contrast | Low-vision users cannot read text | Use a contrast checker tool | Ensure text-to-background ratio is at least 4.5:1. |
| Missing Form Labels | Screen readers cannot identify inputs | Inspect form fields for `<label>` | Use `aria-label` or explicit `<label for="id">`. |
| Dynamic Status | Screen readers miss new chat messages | Trigger an error/message with VoiceOver on | Use `aria-live="polite"` for new content. |
| Tiny Tap Targets | Mobile/senior users misclick | Test on mobile device | Ensure interactive elements are at least 44x44 CSS pixels. |

*Takeaway: Implementing these basic checks ensures that prototypes do not immediately alienate users relying on assistive technologies.*

## Inclusion Risk Inventory — Concrete failure scenarios to test and prevent

### Scenarios and Guardrails

| Scenario | Likely Users Affected | Evidence | Prototype Guardrail | Test Method |
| :--- | :--- | :--- | :--- | :--- |
| Machine-translated eviction deadline rendered incorrectly | LEP residents | rva.gov disclaimer [3] | Human-review critical dates; add 311 escalation. | Bilingual user testing on core flows. |
| Navigator assumes broadband and forces 10MB PDF upload | Low-income, mobile-only users | 13.8% lack broadband [1] | Defer uploads; allow camera-to-text or in-person drop-off. | Throttle network to 3G during QA. |
| Chatbot hides "request interpreter" behind English menu | LEP residents | 6% EL proficiency [2] | Sticky, icon-based 311/Interpreter button on all screens. | Task completion test with non-English speakers. |
| Dynamic step loses keyboard focus | Screen reader users | Standard SPA pitfalls | Manage focus on route changes; use ARIA live regions. | Navigate entirely via keyboard/VoiceOver. |

*Takeaway: Pre-mortems expose how residents can be harmed by poorly designed tools; every identified risk must have a corresponding technical or operational guardrail.*

## Minimum Bar for Prototypes — Accessibility and Language Access Requirements

### Accessibility (WCAG 2.1 AA mini-spec for hackathon)
Prototypes must pass a keyboard-only navigation test, meet AA color contrast ratios, include visible focus indicators, and utilize ARIA-live regions for dynamic status updates. Timeouts must be greater than 20 minutes to accommodate slower readers.

### Language access
Core flows must be available in English and Spanish, with the Spanish translation human-reviewed. Every page must feature an interpreter link routing to 311, mirroring the City's current web strategy [3]. 

### Mobile and bandwidth
The initial load size must be under 1 MB to accommodate data caps. The critical path must function without requiring PDF downloads, and the system should offer SMS fallbacks for saving case IDs or links.

## Design Patterns That Reduce Inclusion Risk

### Pattern 1: Confidence + source disclosure per answer
Show a confidence label, the authoritative source, and a last-updated timestamp for every answer. For legal or benefits topics, default to human handoff if confidence is low. The City already disclaims liability for GIS data inaccuracies [5]; prototypes must similarly manage expectations.

### Pattern 2: Plain language + structured choices over free text
Replace open-ended text boxes with guided, pre-translated choices. This reduces cognitive load and minimizes the risk of machine translation failing on user-inputted slang or idioms.

### Pattern 3: "Escalate to a human" always visible
Maintain a sticky "Call 311" button with a language choice option. This aligns with the City's existing Language Access Plan routing [4].

### Pattern 4: Step-by-step flows with progress and estimated time
Break complex forms into progressive disclosures. Show a step counter and allow users to save and return without creating an account.

### Pattern 5: Mobile-first, offline-tolerant interactions
Design for the smallest screen first. Allow users to take photos of documents rather than uploading PDFs, and provide SMS receipts with case summaries.

## Measurement, Guardrails, and Go/No-Go Criteria

### KPIs to instrument during hack
Teams must measure step completion rates, drop-off per step (a >20% drop at any step should trigger an immediate redesign), language selection rates, and human handoff acceptance.

### Safety interlocks
If a prototype provides information on legal rights or benefits and the system's confidence is anything less than "high," it must require a human escalation. If low bandwidth is detected, the system should immediately offer an SMS or phone path.

## Pilot Scope and Operationalization with 311

### Where to pilot
Pilots should focus on services with high harm potential from misinformation, such as utility assistance or housing support. 

### 311 enablement
Prototypes should generate a "handoff package" (language preference, answers already provided, and next steps) that can be securely viewed by 311 operators. This prevents residents from having to repeat their stories, bridging the gap between digital self-service and human support.

## Evidence Appendix — What we know now vs. what we’ll validate

### Confirmed local signals
* **Broadband/Computer:** 86.2% of Richmond households have broadband; 94.4% have a computer [1].
* **Language Access Demand:** RPS English Learners show 6% proficiency and 47% progress, with recent arrival exemptions fluctuating between 20 and 56 students [2].
* **City Policy:** rva.gov explicitly disclaims Google Translate and routes users to 311 for professional translation under the Language Access Plan [3] [4].

### To validate during/after hack
* Richmond-specific cellular-only household percentages.
* Step-level drop-off rates for target municipal services.
* Real-world screen reader compatibility through direct user testing.

## References

1. *U.S. Census Bureau QuickFacts: Richmond city, Virginia*. https://www.census.gov/quickfacts/fact/table/richmondcityvirginia/PST045224
2. *Richmond City Public Schools - Virginia School Quality Profiles*. https://schoolquality.virginia.gov/divisions/richmond-city-public-schools
3. *| Richmond*. https://www.rva.gov/
4. *RVA Stay | Richmond*. https://www.rva.gov/rvastay
5. *GIS/Mapping | Richmond*. https://www.rva.gov/assessor-real-estate/gismapping
6. *Citizen Service and Response | Richmond*. https://rva.gov/citizen-service-and-response
7. *How Korean became Virginia's third most-spoken language*. https://www.axios.com/local/richmond/2025/06/10/most-popular-spoken-languages-virginia
8. *Linguistic Profile of Virginia's Non-English Speakers*. https://storymaps.arcgis.com/stories/31723fbdcb0342c0b1cc30d0913a8d2c