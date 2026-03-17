# Verification TODO — A Thriving City Hall

Top 10 claims to verify before the hackathon (March 27, 2026). Assign each to a team member and log the result in `evidence_log.md`.

---

## Priority Verifications

### 1. rva.gov sitemap availability
**Claim:** rva.gov publishes a sitemap.xml that can be used to discover all department and service pages.
**How to verify:** Check https://www.rva.gov/sitemap.xml — does it exist? If so, how many pages does it index?
**Impact if false:** Teams building a rva.gov search layer must crawl manually from department landing pages rather than using a sitemap.
**Evidence log ID:** Create E-016

### 2. Drupal Search API facet parameters
**Claim:** The search URL pattern https://www.rva.gov/search?search={query}&page={n}&f[0]=business_unit:{id} is correct and returns JSON-parseable results.
**How to verify:** Test the URL with a known query and a known department slug; inspect the response format.
**Impact if false:** Search integration approach may need to change.
**Evidence log ID:** Update E-006

### 3. RVA311 2025 volume (208,216 requests)
**Claim:** RVA311 received 208,216 total service requests in 2025.
**How to verify:** Find the source document (City annual report, press release, Council presentation). The rubric document cites this figure but doesn't give a URL.
**Impact if false:** Demo credibility if this number is wrong or cannot be sourced.
**Evidence log ID:** Update E-004

### 4. City Contracts dataset column bug confirmation
**Claim:** The City Contracts Socrata API (xqn7-jvv2) returns only 8 of 9 columns. The CSV download returns all 9.
**How to verify:** Query both the SODA API and download the CSV; compare column counts and identify the missing column.
**Impact if false:** Teams may not need to use CSV workaround.
**Evidence log ID:** Update E-008 and E-009

### 5. SAM.gov API free key availability and rate limits
**Claim:** SAM.gov provides free API access with no cost, and a key can be obtained via sam.gov registration.
**How to verify:** Visit https://open.gsa.gov/api/entity-api/ and confirm key registration process and rate limits.
**Impact if false:** Cross-source contract finder may require paid API access or have tighter rate limits than expected.
**Evidence log ID:** Update E-014

### 6. AvePoint Citizen Services platform — no public API confirmation
**Claim:** AvePoint Citizen Services (Richmond's RVA311 platform) does not expose a public API for service requests.
**How to verify:** Check AvePoint product documentation at https://www.avepoint.com/products/citizen-services. Look for API documentation.
**Impact if false:** (Unlikely to be wrong, but confirming eliminates a potential project blocker.)
**Evidence log ID:** Update E-002

### 7. rva.gov department slug list
**Claim:** There are over 40 department slugs accessible via the business_unit facet parameter in rva.gov search.
**How to verify:** Inspect rva.gov search page HTML for facet values; or query the search API without a business_unit filter and examine the facet response.
**Impact if false:** The number of departments affects the scope of a category taxonomy project.
**Evidence log ID:** Create E-017

### 8. Historical RVA311 Socrata data date range
**Claim:** The historical SeeClickFix Socrata dataset (vgg4-hjn8) contains data from January 2014 through August 2015 only.
**How to verify:** Query the SODA API: https://data.richmondgov.com/resource/vgg4-hjn8.json?$select=min(created_at),max(created_at)
**Impact if false:** May affect how useful the historical data is for understanding current category patterns.
**Evidence log ID:** Update E-001

### 9. eVA Virginia procurement dataset URL on data.virginia.gov
**Claim:** Virginia eVA procurement data is available as a CSV download on data.virginia.gov.
**How to verify:** Search https://data.virginia.gov for "eVA" or "procurement" and find the current dataset URL.
**Impact if false:** Cross-source finder approach may need adjustment.
**Evidence log ID:** Create E-018

### 10. Pete Briel as Director of 311 / formal champion status
**Claim:** Pete Briel is the Director of 311 at Richmond City government and is a candidate for post-hackathon champion.
**How to verify:** Check City of Richmond staff directory or press coverage.
**Impact if false:** The continuation pathway for the service navigation problem needs to be re-identified.
**Evidence log ID:** Update E-011
