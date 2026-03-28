export type RenewalBucket =
  | "DAYS_0_30"
  | "DAYS_31_60"
  | "DAYS_61_90"
  | "DAYS_91_180"
  | "DAYS_181_PLUS"
  | "UNKNOWN_END";

export type EndDateScope = "ALL" | "PAST" | "UPCOMING" | "UNKNOWN";

export interface CountByDepartment {
  department: string;
  count: number;
}

export interface CountByBucket {
  bucket: string;
  count: number;
}

export interface ContractRow {
  id: number;
  agencyDepartment: string;
  contractNumber: string;
  contractValue: number | null;
  supplier: string;
  procurementType: string;
  effectiveFrom: string | null;
  effectiveTo: string | null;
}

export interface ContractDetail extends ContractRow {
  contractValueRaw: string | null;
  description: string | null;
  typeOfSolicitation: string | null;
  effectiveFromRaw: string | null;
  effectiveToRaw: string | null;
  contractSummary: string | null;
  loadedAt: string | null;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface Metadata {
  sourceUrl: string | null;
  loadedAt: string | null;
  rowCount: number | null;
  provenanceUrl: string | null;
  referenceTimezone: string | null;
  referenceDate: string | null;
}

function qs(params: Record<string, string | number | undefined | null>): string {
  const e = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== null && v !== "") {
      e.set(k, String(v));
    }
  }
  const s = e.toString();
  return s ? `?${s}` : "";
}

export async function fetchByDepartment(
  department: string | null,
  renewalBucket: RenewalBucket | null,
): Promise<CountByDepartment[]> {
  const q = qs({
    department: department ?? undefined,
    renewalBucket: renewalBucket ?? undefined,
  });
  const r = await fetch(`/api/v1/dashboard/by-department${q}`);
  if (!r.ok) throw new Error(await r.text());
  return r.json();
}

export async function fetchByRenewalBucket(
  department: string | null,
  renewalBucket: RenewalBucket | null,
): Promise<CountByBucket[]> {
  const q = qs({
    department: department ?? undefined,
    renewalBucket: renewalBucket ?? undefined,
  });
  const r = await fetch(`/api/v1/dashboard/by-renewal-bucket${q}`);
  if (!r.ok) throw new Error(await r.text());
  return r.json();
}

export async function fetchContracts(params: {
  department: string | null;
  renewalBucket: RenewalBucket | null;
  endDateScope: EndDateScope;
  page: number;
  size: number;
  sort: string;
}): Promise<PageResponse<ContractRow>> {
  const q = qs({
    department: params.department ?? undefined,
    renewalBucket: params.renewalBucket ?? undefined,
    endDateScope: params.endDateScope,
    page: params.page,
    size: params.size,
    sort: params.sort,
  });
  const r = await fetch(`/api/v1/contracts${q}`);
  if (!r.ok) throw new Error(await r.text());
  return r.json();
}

export async function fetchContract(id: number): Promise<ContractDetail> {
  const r = await fetch(`/api/v1/contracts/${id}`);
  if (!r.ok) throw new Error(await r.text());
  return r.json();
}

export async function fetchMetadata(): Promise<Metadata> {
  const r = await fetch(`/api/v1/metadata`);
  if (!r.ok) throw new Error(await r.text());
  return r.json();
}

export type ExtractFieldKey =
  | "agencyDepartment"
  | "contractNumber"
  | "contractValue"
  | "supplier"
  | "procurementType"
  | "description"
  | "typeOfSolicitation"
  | "effectiveFrom"
  | "effectiveTo"
  | "synopsis";

export interface FieldWithConfidence {
  value: string | null;
  confidence: number;
}

export interface ContractExtractResponse {
  fields: Record<ExtractFieldKey, FieldWithConfidence>;
  meta: {
    model: string;
    warnings?: string[];
  };
}

export async function extractContractPdf(file: File): Promise<ContractExtractResponse> {
  const fd = new FormData();
  fd.append("file", file);
  const r = await fetch("/api/v1/extract/contract-pdf", {
    method: "POST",
    body: fd,
  });
  if (!r.ok) {
    let msg = await r.text();
    try {
      const j = JSON.parse(msg) as { error?: string };
      if (j.error) msg = j.error;
    } catch {
      /* use raw body */
    }
    throw new Error(msg || `Request failed (${r.status})`);
  }
  return r.json() as Promise<ContractExtractResponse>;
}
