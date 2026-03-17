# Build-Ready Plan for a Thriving City Hall: Data, Dependencies, and Guardrails for Richmond's Service Navigation

## Executive Summary
For the Richmond Civic Hackathon team building under the "A Thriving City Hall" pillar, success depends on navigating Richmond's specific data structures and strict compliance guardrails. RVA311 handles over 83,000 requests annually across more than 30 departments [1], requiring a robust and adaptable taxonomy. Furthermore, Richmond's Language Access Plan (LAP) strictly prohibits the use of automated internet translation for service delivery [2], meaning any chatbot or navigation tool must prioritize human-translated vital flows and warm handoffs to live interpreters. 

While the City Contracts Socrata dataset offers a clean 9-column schema for immediate transparency wins [3], developers must account for Socrata API bugs that silently drop null fields [4]. Several critical unknowns—including the RVA311 public API, rva.gov sitemap structure, and federal/state procurement API access—directly impact hackathon feasibility and must be treated as immediate go/no-go gates for automation features.

## Hackathon Feasibility Gates & Top 10 Research Questions

Below are the answers to the 10 key questions, marked by verification status and flagged for hackathon feasibility impact.

### 1. RVA311 Request Types and Department Mappings
**Status:** Partially Verified / Inference
**URL:** https://www.rva.gov/citizen-service-and-response/about-rva-311
**Answer:** A single, exhaustive public list of all 50+ request types is not documented in one static location. However, RVA311 partners with over 30 different departments [1]. The taxonomy is dynamic; for example, in 2019, the city split "Parking Application" into "Request Handicap Parking Sign" and "Request Residential Parking Permit" to improve routing [5]. 

| Department Category | Example Request Types / Services |
| :--- | :--- |
| Public Works | Solid Waste, Bulk and Brush, Potholes, Sidewalks, Streetlights [1] |
| Public Utilities | Storm Water, Waste Water [1] |
| Social Services | Benefit Application, Child Protective Services, Homeless Services [1] |
| Finance and Revenue | Personal/Business Property Taxes, Business Licensing [1] |
| Planning and Development | Zoning and Permitting, Code Enforcement [1] |

### 2. rva.gov Sitemap Structure and Structured Data
**Status:** Unknown
**URL:** N/A
**Feasibility Flag:** **CRITICAL**. Without a verified sitemap or JSON-LD schema (BreadcrumbList, FAQ, Organization), building an automated search index or RAG (Retrieval-Augmented Generation) pipeline for city services will require manual scraping, severely limiting the scope of a hackathon project.

### 3. City Contracts Socrata Dataset (xqn7-jvv2) Columns and API Bug
**Status:** Verified
**URL:** https://data.richmondgov.com/Well-Managed-Government/City-Contracts/xqn7-jvv2
**Answer:** The dataset contains exactly 9 columns: `Agency/Department`, `Contract Number`, `Contract Value`, `Supplier`, `Procurement Type`, `Description`, `Type of Solicitation`, `Effective From`, and `Effective To` [3]. 
**The API Bug:** The Socrata API (SODA) skips empty or null fields entirely in its JSON response rather than returning an empty string [4]. This results in arrays/rows with missing keys, causing shorter rows that break naive table visualizations or data ingestion pipelines [4] [6].

### 4. SAM.gov API Registration and Rate Limits
**Status:** Unknown
**URL:** N/A
**Feasibility Flag:** **MODERATE**. Affects the ability to enrich local contract data with federal data. Teams should plan a fallback to static CSV exports if API keys cannot be provisioned during the hackathon.

### 5. eVA Virginia Procurement CSV Dataset
**Status:** Unknown
**URL:** N/A
**Feasibility Flag:** **MODERATE**. Affects state-level contract enrichment.

### 6. Comparable City 311 Taxonomies
**Status:** Unknown (within provided Richmond context)
**URL:** N/A
**Answer:** While specific data for Boston, NYC, Chicago, and SF was not retrieved in this research hop, Richmond's own evolution shows a move toward plain-language, highly specific intents (e.g., separating "Public Litter" from "Storm Debris") [5].

### 7. Failure Modes of Government Service Chatbots
**Status:** Inference
**URL:** https://rva.gov/sites/default/files/2022-05/LANGUAGE%20ACCESS%20PLAN%20FINAL-%20APR2020.pdf
**Answer:** The primary documented failure mode in Richmond's context is language misrouting. Richmond's Language Access Plan explicitly states: "Automated internet interpretation services will not be used even in emergency situations" [2]. A chatbot that relies on automated translation (like Google Translate) to route non-English service requests violates city policy and erodes trust with the 6,537 Spanish-speaking Limited English Proficiency (LEP) residents [2].

### 8. AvePoint Citizen Services Platform API
**Status:** Unknown
**URL:** N/A
**Feasibility Flag:** **CRITICAL**. If the AvePoint platform powering RVA311 lacks a public API for programmatic ticket creation, the hackathon team cannot build an end-to-end automated submission tool. The project must pivot to an "advisory" tool that guides residents to the correct official form or phone number.

### 9. Contract Transparency Tools on Socrata
**Status:** Unknown
**URL:** N/A
**Answer:** Specific external examples were not retrieved, but Richmond's 9-column schema [3] is sufficient to build a localized MVP dashboard filtering by `Supplier`, `Agency/Department`, and `Contract Value`.

### 10. Digital Equity and Accessibility Considerations
**Status:** Verified
**URL:** https://www.rva.gov/immigrant-engagement/language-access
**Answer:** Language access is a strict legal and operational mandate in Richmond. The city provides free interpretation and translation services [7]. 
* **Language:** Spanish makes up more than 96 percent of the calls to the language line [2]. 
* **Policy:** The city uses only qualified adults as interpreters and forbids the use of children under 18 [2]. 
* **Digital Guardrails:** RVA.gov features a disclaimer that Google Translate is not a substitute for professional translation provided under the LAP, directing residents to call 804-646-7000 or 3-1-1 for actual service [7]. Hackathon tools must include prominent, plain-language pathways to these phone numbers rather than relying solely on digital text.

## References

1. *About RVA 311 | Richmond*. https://www.rva.gov/citizen-service-and-response/about-rva-311
2. *1 Revised Version April 2020 LANGUAGE ACCESS PLAN ...*. https://rva.gov/sites/default/files/2022-05/LANGUAGE%20ACCESS%20PLAN%20FINAL-%20APR2020.pdf
3. *City Contracts | Open Data Portal | City of Richmond, Virginia*. https://data.richmondgov.com/Well-Managed-Government/City-Contracts/xqn7-jvv2
4. *Socrata API - How to replace empty data fields from query as empty strings in results array - Stack Overflow*. https://stackoverflow.com/questions/53235100/socrata-api-how-to-replace-empty-data-fields-from-query-as-empty-strings-in-re
5. *Improvements to RVA311 Request System, November 2019 | Richmond*. https://rva.gov/press-releases-and-announcements/news/improvements-rva311-request-system-november-2019
6. *Socrata API missing fields - Stack Overflow*. https://stackoverflow.com/questions/37419673/socrata-api-missing-fields
7. *Language Access | Richmond*. https://www.rva.gov/immigrant-engagement/language-access