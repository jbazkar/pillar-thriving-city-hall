
# Richmond procurement tools: VPPA risks, failure modes, and safe-by-design controls

## Executive Summary

Building a procurement tool for Richmond City staff requires navigating strict legal constraints where software errors can trigger severe legal and financial consequences. The Virginia Public Procurement Act (VPPA) and Richmond City Code Chapter 21 govern all local public contracts [1] [2]. Violations of these rules can result in contracts being declared void, rapid 10-day protest windows, and even Class 1 misdemeanor charges for ethics breaches [3]. 

To mitigate these risks, the tool must be strictly designed as an "advisory-only" system. It cannot make official procurement determinations, which are legally reserved for authorized purchasing agents and officials [3]. High-risk data such as contract dates, debarment status, and pricing must be gated behind mandatory human verification workflows. By encoding VPPA and City Code guardrails directly into the user experience, the tool can accelerate staff workflows while maintaining the rigorous auditability required to defend against vendor protests.

## Legal Framework and Constraints

The legal architecture governing Richmond's procurement is a combination of state law and local ordinances. These rules are binding design constraints for any procurement software.

### VPPA Articles 5 and 6: Remedies and Ethics drive risk posture
The Virginia Public Procurement Act (Title 2.2, Chapter 43) establishes the baseline rules for local public bodies [1] [3]. 
* **Remedies (Article 5):** Vendors have a strict 10-day window to protest an award or a decision to award (§ 2.2-4360) [4]. During a timely protest, the award may be stayed (§ 2.2-4362) [5]. If a contract is awarded in violation of the law, a public body may declare it void, and the contractor is only entitled to the cost of performance, with no lost profits [3].
* **Ethics (Article 6):** The VPPA strictly prohibits misrepresentations (§ 2.2-4376) and requires certifications of compliance (§ 2.2-4375) [6] [7]. It also restricts contributions and gifts during the procurement process (§ 2.2-4376.1, § 2.2-4376.2) [8] [9]. A willful violation of these ethics provisions is a Class 1 misdemeanor, and upon conviction, a public employee forfeits their employment (§ 2.2-4377) [10].

### Richmond City Code Chapter 21: Local implementation details
Richmond implements the VPPA through City Code Chapter 21 (Public Procurement) [2]. Key sections include:
* **Article II:** Contract Formation and Methods of Source Selection [2].
* **Article IV:** Debarment [11].
* **Article V:** Appeals and Remedies for Protests [2].
* **Recent Ordinances:** Richmond recently added § 21-74 concerning the payment of prevailing wages for certain construction contracts (adopted 2024-10-15) [12], and § 21-75 concerning reverse auctioning (adopted 2025-06-23) [13].

### Roles and authorities under Virginia law and City practice
Under Virginia law, a designated "purchasing agent" exercises day-to-day responsibility for procurement, including preparing determinations and solicitations [3]. Furthermore, before a contract is approved, a qualified attorney must approve the form of the contract document, and a financial officer must certify that funds are available and appropriated [3].

## Decision Support vs. Procurement Determination

A critical legal boundary is the difference between providing data to support a decision and making the determination itself. The tool must never cross into making determinations.

### Activities matrix: Advisory vs. Official Determination

| Procurement Activity | Tool May (Advisory) | Tool Must Not (Determination) | Required Human Action |
| :--- | :--- | :--- | :--- |
| **Sole Source / Emergency** | Draft justification memos based on user inputs; flag missing fields. | Auto-approve or finalize the determination. | Written determination signed by authorized purchasing agent [3]. |
| **Contract Award** | Score proposals based on math; aggregate evaluator notes. | Declare a "winner" or issue an award notice. | Official award decision and posting by procurement officer. |
| **Responsibility Check** | Surface licensing data and debarment lists for review. | Disqualify a vendor automatically. | Written determination of nonresponsibility (§ 2.2-4359) [14]. |
| **Legal/Financial Review** | Route documents to legal/finance; check for standard clauses. | Bypass legal review or certify funds availability. | Attorney approval of form; Financial officer certification of funds [3]. |

### UX language policy
The tool's interface must avoid authoritative verbs that imply the software is making a legal decision. Words like "approve," "determine," "award," "certify," and "shall/must" are strictly prohibited. Instead, the UI should use advisory language such as "flag," "suggest," "evidence indicates," and "needs review."

## Risk Scenario Deep-Dive

Software errors in procurement tools can directly cause statutory violations. The following scenarios outline high-impact failure modes.

### Scenario risk register

| Scenario | Likelihood | Severity | Risk Chain & Legal Impact | Primary Controls |
| :--- | :--- | :--- | :--- | :--- |
| **1. "Expired" vs informally extended contract** | High | Medium | Mislabel → lapse → unauthorized spend. | Show executed amendments only; require approver attestation before flagging as expired. |
| **2. LLM-extracted term off by one year** | Medium | High | Missed renewal → emergency procurement → protest exposure. | Dual-verify high-risk fields; provide clause-level citations; set confidence thresholds. |
| **3. Debarment false negative** | Low | High | Award to ineligible vendor → voidable contract, protests [3]. | Multi-source checks (City, State, Federal); day-of-award recheck; stale-data hard stop. |
| **4. "Better value" state contract with undisclosed pricing** | Medium | High | Improper cooperative use → noncompliant award. | Cooperative eligibility checklist; pricing parity confirmation; prevailing wage check (§ 21-74) [12]. |

## High-Signal Compliance Red Flags

To prevent statutory violations, the tool must monitor for and elevate the following high-signal red flags:

* **Single-quote purchases near the $50,000 threshold:** Richmond's single-quote limit is $50,000, which exposes the City to bid-splitting risks where purchases are artificially divided to avoid competitive bidding [15].
* **Debarment checks older than 24 hours:** Vendor status can change rapidly; failing to verify SAM.gov, eVA, or City Article IV exclusions immediately prior to award risks contracting with a suspended entity [11] [16].
* **Missing written justification for sole source/emergency:** The VPPA requires a formal written determination and prompt public notice on eVA for non-competitive awards; absence of this documentation is a severe audit finding [17].
* **Improper cooperative procurement (piggybacking):** Using another public body's contract without verifying that the original solicitation contained the required cooperative language violates VPPA § 2.2-4304 [18].
* **Failure to verify contractor license on bid:** Virginia law requires the awarding authority to collect the bidder's state license number prior to considering a bid; missing this invalidates the bid [19] [20].

## Disclaimers and Advisory Safeguards

Disclaimers are necessary but insufficient on their own; they must be paired with hard workflow gates. 

### Required disclaimer elements
All recommendation views and exported reports must include clear disclaimers: "Advisory tool only; not an official procurement determination," "User remains responsible for compliance under VPPA and Richmond City Code," and "Verify all data against original sources." 

### Placement and enforcement
These disclaimers must be visible on all dashboards, included in pre-action confirmation modals, and permanently stamped onto PDF exports and audit logs.

## Verification Workflow and Approvals

To prevent unauthorized actions, the tool must enforce a standard verification checklist before any staff member can act on the tool's output.

### Pre-award verification steps by procurement method

| Procurement Method | Required Verification Steps | Statutory/Code Driver |
| :--- | :--- | :--- |
| **Competitive Sealed Bidding** | Verify contractor license number on bid [20]; Check debarment status; Confirm prevailing wage applicability [12]. | Code of Virginia § 54.1-1111 [19]; City Code § 21-74 [12]. |
| **Cooperative Procurement** | Verify cooperative membership; Confirm scope and pricing visibility; Route for legal/finance sign-off. | VPPA requirements; Attorney/Finance approval [3]. |
| **Sole Source / Emergency** | Draft written determination; Collect ethics certifications [6]; Obtain purchasing agent signature. | VPPA § 2.2-4375; Purchasing agent authority [3]. |

## Data Freshness, Provenance, and Stop-Ship Logic

Displaying stale data is a severe legal risk. The tool must use timestamps and traffic-light UI to drive safe behavior.

### Data domains and freshness SLAs

| Data Domain | Source of Truth | Update Cadence | Action on Stale Data |
| :--- | :--- | :--- | :--- |
| **Contract Terms/Dates** | Executed contract documents | Real-time on upload | Warn: Require manual verification of source doc. |
| **Debarment Status** | City Article IV list, eVA, SAM.gov [16] | Daily | Block: Hard stop on award workflows until refreshed. |
| **Vendor Licensing** | State licensing boards | Weekly | Block: Cannot consider bid without valid license [20]. |

## Debarment, Responsibility, and Licensing Checks

Ensuring a vendor is eligible is a strict legal responsibility. A public body may deny prequalification if a contractor is currently debarred [3]. Furthermore, an awarding authority must require a contractor to submit their state license number prior to considering a bid [20]. The tool must auto-collect license numbers and integrate multi-source exclusion checks (Richmond Article IV, state eVA, federal SAM.gov). If issues are found, the tool should generate a draft written determination of nonresponsibility (§ 2.2-4359) for human review [14].

## Remedies, Protests, and Defense Playbook

Because vendors have only 10 days to protest an award (§ 2.2-4360) [4], the city must be able to produce a defensible record immediately. The tool must default to immutable, time-stamped logs of data provenance, user decisions, and approvals. Upon award, it should auto-generate a "protest packet" containing the decision rationale, source documents, ethics certifications, and timestamped debarment checks to defend against potential stays of award (§ 2.2-4362) [5].

## Ethics, Gifts, and Certifications

The VPPA's ethics provisions (Article 6) carry criminal penalties (Class 1 misdemeanor) [10]. The tool must mediate these requirements by integrating context-aware attestations at key workflow steps. It must prompt users to complete required certifications of compliance (§ 2.2-4375) [6] and acknowledge prohibitions on misrepresentations (§ 2.2-4376) [7] and gifts (§ 2.2-4376.1) [9]. The tool must *never* auto-populate these certifications; it must require explicit human affirmation and store it in the audit trail.

## City-Specific Features

Richmond's local ordinances frequently update procurement logic. For example, the recent additions of prevailing wage requirements (§ 21-74) [12] and reverse auctioning (§ 21-75) [13] alter how contracts are formed. The tool must implement jurisdiction-aware rule packs with clear versioning, showing users which version of the City Code is driving the current logic, and forcing re-validation when ordinances change.

## Language Pattern Library

To maintain the tool's advisory status, specific language patterns must be enforced in the UI.

| Avoid (Implies Authority) | Use Instead (Advisory) |
| :--- | :--- |
| "Determine" / "Make Determination" | "Suggest" / "Evidence indicates" |
| "Approve" / "Reject" | "Recommended for review" / "Flagged for issues" |
| "Award Contract" | "Draft award notice" |
| "Certify" | "Awaiting user attestation" |
| "Must" / "Shall" | "Appears required by [Code Section]" |

## Governance, Training, and Rollout

Technical controls will fail without proper adoption. The rollout should include a shadow-period where tool outputs are manually compared against traditional processes. Quarterly compliance audits and post-mortems of any vendor protests should be used to refine the tool's logic. Clear escalation paths must be defined involving Procurement, Legal, and Finance teams to handle edge cases and exceptions.

## References

1. *Code of Virginia Code - Chapter 43. Virginia Public Procurement Act*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/
2. *ARTICLE I. - IN GENERAL | Code of Ordinances | Richmond, VA | Municode Library*. https://mcclibraryweb.azurewebsites.us/va/richmond/codes/code_of_ordinances?nodeId=PTIICICO_CH21PUPR_ARTIINGE_SS21-10--21-36RE
3. *The Virginia Public Procurement Act*. https://www.vml.org/wp-content/uploads/pdf/14VaPublicProcurementAct.pdf
4. *§ 2.2-4360. Protest of award or decision to award*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4360/
5. *§ 2.2-4362. Stay of award during protest*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4362/
6. *§ 2.2-4375. Certification of compliance required; penalty for false statements*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4375/
7. *§ 2.2-4376. Misrepresentations prohibited*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4376/
8. *§ 2.2-4376.2. (Effective until July 1, 2027) Disclosure of contributions and gifts during procurement process; civil penalty*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4376.2/
9. *§ 2.2-4376.1. Contributions and gifts; prohibition during procurement process*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4376.1/
10. *§ 2.2-4377. Penalty for violation*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4377/
11. *ARTICLE IV. - DEBARMENT | Code of Ordinances | Richmond, VA | Municode Library*. https://mcclibraryweb.azurewebsites.us/va/richmond/codes/code_of_ordinances?nodeId=PTIICICO_CH21PUPR_ARTIVDE
12. *City of Richmond - Ordinance No. 2024-186*. https://richmondva.legistar.com/LegislationDetail.aspx?ID=6736412&GUID=E756F112-CF1E-4FBA-AB28-FC7C66697B97
13. *Ordinance No. 2025-137 (Sec. 21-75 Reverse Auctioning) — Adopted Ordinances Not Yet Codified | Municode*. https://mcclibraryweb.azurewebsites.us/va/richmond/ordinances/code_of_ordinances?nodeId=1384374
14. *§ 2.2-4359. Determination of nonresponsibility*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4359/
15. *Office of the City Auditor (OCA) — Annual Report FY 2025*. https://rva.gov/sites/default/files/2025-08/OCA%20Annual%20Report%20-%20FY%202025.pdf
16. *How to Search for Exclusions on SAM.gov | U.S. Department of the Interior*. https://www.doi.gov/pam/suspension-debarment/search-exclusions
17. *§ 2.2-4303. Methods of procurement*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4303/
18. *§ 2.2-4304. Joint and cooperative procurement*. https://law.lis.virginia.gov/vacode/title2.2/chapter43/section2.2-4304/
19. *§ 54.1-1111. Prerequisites to obtaining business license; building, etc., permit*. https://law.lis.virginia.gov/vacode/title54.1/chapter11/section54.1-1111/
20. *Code of Virginia Code - Chapter 11. Contractors*. https://law.lis.virginia.gov/vacodefull/title54.1/chapter11/