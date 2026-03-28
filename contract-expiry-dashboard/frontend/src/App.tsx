import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Alert,
  AppBar,
  Box,
  Button,
  Container,
  Dialog,
  DialogContent,
  DialogTitle,
  FormControl,
  InputLabel,
  LinearProgress,
  MenuItem,
  Paper,
  Select,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TableSortLabel,
  Toolbar,
  Typography,
} from "@mui/material";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import type { RenewalBucket, EndDateScope, ContractRow, ContractDetail, Metadata } from "./api";
import {
  fetchByDepartment,
  fetchByRenewalBucket,
  fetchContracts,
  fetchContract,
  fetchMetadata,
} from "./api";

const BUCKET_ORDER: RenewalBucket[] = [
  "DAYS_0_30",
  "DAYS_31_60",
  "DAYS_61_90",
  "DAYS_91_180",
  "DAYS_181_PLUS",
  "UNKNOWN_END",
];

const BUCKET_LABEL: Record<RenewalBucket, string> = {
  DAYS_0_30: "0–30 days",
  DAYS_31_60: "31–60 days",
  DAYS_61_90: "61–90 days",
  DAYS_91_180: "91–180 days",
  DAYS_181_PLUS: "181+ days",
  UNKNOWN_END: "Unknown end",
};

const PIE_COLORS = [
  "#1a4d6d",
  "#2e6f95",
  "#458eb8",
  "#5fadcb",
  "#7ec8cf",
  "#c75b39",
  "#e8936a",
  "#8e6bb3",
  "#5c4b8a",
];

/** Stable color per department so selection/filter refetches do not reshuffle slice hues (index-based colors jump when sort order changes). */
function fillForDepartment(department: string): string {
  let h = 0;
  for (let i = 0; i < department.length; i++) {
    h = Math.imul(31, h) + department.charCodeAt(i);
    h |= 0;
  }
  return PIE_COLORS[Math.abs(h) % PIE_COLORS.length];
}

/** Full annulus: SVG cannot render a 360° arc as one `A` (start = end → degenerate). Two semicircles + evenodd inner hole. */
function donutFullRingPath(cx: number, cy: number, rInner: number, rOuter: number): string {
  const outer = [
    `M ${cx} ${cy - rOuter}`,
    `A ${rOuter} ${rOuter} 0 1 1 ${cx} ${cy + rOuter}`,
    `A ${rOuter} ${rOuter} 0 1 1 ${cx} ${cy - rOuter}`,
    "Z",
  ].join(" ");
  const inner = [
    `M ${cx} ${cy - rInner}`,
    `A ${rInner} ${rInner} 0 1 0 ${cx} ${cy + rInner}`,
    `A ${rInner} ${rInner} 0 1 0 ${cx} ${cy - rInner}`,
    "Z",
  ].join(" ");
  return `${outer} ${inner}`;
}

/** SVG arc path for one donut slice (no chart library = no in-chart legend). */
function donutSegmentPath(
  cx: number,
  cy: number,
  rInner: number,
  rOuter: number,
  a0: number,
  a1: number,
): string {
  if (a1 - a0 < 1e-6) return "";
  const x0o = cx + rOuter * Math.cos(a0);
  const y0o = cy + rOuter * Math.sin(a0);
  const x1o = cx + rOuter * Math.cos(a1);
  const y1o = cy + rOuter * Math.sin(a1);
  const x0i = cx + rInner * Math.cos(a0);
  const y0i = cy + rInner * Math.sin(a0);
  const x1i = cx + rInner * Math.cos(a1);
  const y1i = cy + rInner * Math.sin(a1);
  const large = a1 - a0 > Math.PI ? 1 : 0;
  return [
    `M ${x0o} ${y0o}`,
    `A ${rOuter} ${rOuter} 0 ${large} 1 ${x1o} ${y1o}`,
    `L ${x1i} ${y1i}`,
    `A ${rInner} ${rInner} 0 ${large} 0 ${x0i} ${y0i}`,
    "Z",
  ].join(" ");
}

function DepartmentDonut({
  data,
  selectedDepartment,
  onSegmentClick,
}: {
  data: { department: string; count: number }[];
  selectedDepartment: string | null;
  onSegmentClick: (department: string) => void;
}) {
  const vb = 200;
  const cx = vb / 2;
  const cy = vb / 2;
  const rOuter = 78;
  const rInner = 32;
  const total = useMemo(() => data.reduce((s, d) => s + d.count, 0), [data]);

  const segments = useMemo(() => {
    if (total <= 0) return [];
    let a = -Math.PI / 2;
    return data.map((d, i) => {
      const sweep = (d.count / total) * 2 * Math.PI;
      const a0 = a;
      const a1 = a + sweep;
      a = a1;
      const fullRing = sweep >= 2 * Math.PI - 1e-4;
      const path = fullRing
        ? donutFullRingPath(cx, cy, rInner, rOuter)
        : donutSegmentPath(cx, cy, rInner, rOuter, a0, a1);
      const fill = fillForDepartment(d.department);
      return { path, fill, department: d.department, count: d.count, i, fullRing };
    });
  }, [data, total, cx, cy, rInner, rOuter]);

  if (total <= 0) return null;

  const tip = (name: string, count: number) => `${name}: ${count.toLocaleString()} contracts`;

  return (
    <svg
      viewBox={`0 0 ${vb} ${vb}`}
      width="100%"
      height="100%"
      role="img"
      aria-label="Contracts by department, counts as a donut chart"
      preserveAspectRatio="xMidYMid meet"
    >
      {segments.map((s) => {
        const selected = selectedDepartment === s.department;
        const fill = s.fill;
        const stroke = selected ? "#0a1f2e" : fill;
        const strokeWidth = selected ? 2 : 1;
        return (
          <path
            key={`${s.i}-${s.department}`}
            d={s.path}
            fill={fill}
            fillRule={s.fullRing ? "evenodd" : "nonzero"}
            stroke={stroke}
            strokeWidth={strokeWidth}
            cursor="pointer"
            onClick={() => onSegmentClick(s.department)}
            aria-pressed={selected}
          >
            <title>{tip(s.department, s.count)}</title>
          </path>
        );
      })}
    </svg>
  );
}

function formatMoney(n: number | null): string {
  if (n == null) return "—";
  return new Intl.NumberFormat(undefined, { style: "currency", currency: "USD", maximumFractionDigits: 0 }).format(n);
}

function formatDate(s: string | null): string {
  if (!s) return "—";
  return s;
}

export default function App() {
  const [department, setDepartment] = useState<string | null>(null);
  const [renewalBucket, setRenewalBucket] = useState<RenewalBucket | null>(null);
  const [endDateScope, setEndDateScope] = useState<EndDateScope>("ALL");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [sortDesc, setSortDesc] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [pieRows, setPieRows] = useState<{ department: string; count: number }[]>([]);
  const [barRows, setBarRows] = useState<{ bucket: RenewalBucket; label: string; count: number }[]>([]);
  const [tableRows, setTableRows] = useState<ContractRow[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [metadata, setMetadata] = useState<Metadata | null>(null);
  const [detailOpen, setDetailOpen] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detail, setDetail] = useState<ContractDetail | null>(null);

  const sortParam = useMemo(() => `effectiveTo,${sortDesc ? "desc" : "asc"}`, [sortDesc]);

  const pieDataSorted = useMemo(
    () =>
      [...pieRows].sort(
        (a, b) => b.count - a.count || a.department.localeCompare(b.department),
      ),
    [pieRows],
  );

  const loadDashboard = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [pie, bar, meta, contracts] = await Promise.all([
        fetchByDepartment(department, renewalBucket),
        fetchByRenewalBucket(department, renewalBucket),
        fetchMetadata(),
        fetchContracts({
          department,
          renewalBucket,
          endDateScope,
          page,
          size: rowsPerPage,
          sort: sortParam,
        }),
      ]);
      setPieRows(
        pie
          .filter((r) => r.department)
          .map((r) => ({ department: r.department, count: Number(r.count) })),
      );
      const barMap = new Map(bar.map((b) => [b.bucket as RenewalBucket, Number(b.count)]));
      setBarRows(
        BUCKET_ORDER.map((bucket) => ({
          bucket,
          label: BUCKET_LABEL[bucket],
          count: barMap.get(bucket) ?? 0,
        })),
      );
      setMetadata(meta);
      setTableRows(contracts.content);
      setTotalElements(contracts.totalElements);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to load");
    } finally {
      setLoading(false);
    }
  }, [department, renewalBucket, endDateScope, page, rowsPerPage, sortParam]);

  useEffect(() => {
    void loadDashboard();
  }, [loadDashboard]);

  const onBarClick = (entry: { bucket: RenewalBucket }) => {
    const b = entry.bucket;
    setRenewalBucket((prev) => (prev === b ? null : b));
    setPage(0);
  };

  const onPieSegmentClick = (dept: string) => {
    setDepartment((prev) => (prev === dept ? null : dept));
    setPage(0);
  };

  const resetDashboard = () => {
    setDepartment(null);
    setRenewalBucket(null);
    setEndDateScope("ALL");
    setSortDesc(true);
    setPage(0);
  };

  const openDetail = async (row: ContractRow) => {
    setDetailOpen(true);
    setDetailLoading(true);
    setDetail(null);
    try {
      const d = await fetchContract(row.id);
      setDetail(d);
    } catch (e) {
      setDetail(null);
    } finally {
      setDetailLoading(false);
    }
  };

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "background.default" }}>
      <AppBar position="static" color="transparent" elevation={0} sx={{ borderBottom: 1, borderColor: "divider" }}>
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Contract expiry dashboard
          </Typography>
          <Button variant="outlined" color="primary" onClick={resetDashboard}>
            Clear all filters
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ py: 3 }}>
        <Stack spacing={2}>
          <Alert severity="info" variant="outlined">
            Advisory only — verify all contract dates and values in official City systems before taking action.
          </Alert>

          {metadata && (
            <Typography variant="body2" color="text.secondary">
              Data source:{" "}
              {metadata.provenanceUrl ? (
                <a href={metadata.provenanceUrl} target="_blank" rel="noreferrer">
                  Richmond open data (City Contracts)
                </a>
              ) : (
                "Richmond open data (City Contracts)"
              )}
              . Last load: {metadata.loadedAt ?? "—"}
              {metadata.rowCount != null ? ` · ${metadata.rowCount} rows` : ""}. Reference date ({metadata.referenceTimezone ?? "server TZ"}):{" "}
              {metadata.referenceDate ?? "—"}.
            </Typography>
          )}

          {error && (
            <Alert severity="error">
              {error}
            </Alert>
          )}

          {loading && <LinearProgress />}

          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: { xs: "1fr", lg: "1fr 1fr" },
              gap: 2,
            }}
          >
            <Paper sx={{ p: 2, minHeight: 400 }}>
              <Typography variant="subtitle1" gutterBottom>
                Contracts by department
              </Typography>
              <Typography variant="caption" color="text.secondary" display="block" sx={{ mb: 1 }}>
                Click a segment to filter by department (click again to clear). Hover for totals; details in the list.
                When a renewal window is selected on the bar chart, counts here match that window.
              </Typography>
              {pieRows.length === 0 ? (
                <Box sx={{ height: 280, display: "flex", alignItems: "center", justifyContent: "center" }}>
                  <Typography color="text.secondary">No rows in the database. Run CSV bootstrap (see README).</Typography>
                </Box>
              ) : (
                <Box
                  sx={{
                    height: { xs: 240, md: 300 },
                    maxWidth: 420,
                    mx: "auto",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                  }}
                >
                  <DepartmentDonut
                    data={pieDataSorted}
                    selectedDepartment={department}
                    onSegmentClick={onPieSegmentClick}
                  />
                </Box>
              )}
            </Paper>

            <Paper sx={{ p: 2, minHeight: 400 }}>
              <Typography variant="subtitle1" gutterBottom>
                Renewal windows (upcoming & unknown)
              </Typography>
              <Typography variant="caption" color="text.secondary" display="block" sx={{ mb: 1 }}>
                Past end dates are omitted here; use the table to review them. Click a bar to filter.
              </Typography>
              <ResponsiveContainer width="100%" height={280}>
                <BarChart data={barRows} margin={{ left: 8, right: 8, bottom: 40 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="label" angle={-25} textAnchor="end" interval={0} height={60} tick={{ fontSize: 11 }} />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Bar
                    dataKey="count"
                    radius={[4, 4, 0, 0]}
                    onClick={(item: { payload?: { bucket: RenewalBucket }; bucket?: RenewalBucket }) => {
                      const b = item?.payload?.bucket ?? item?.bucket;
                      if (b) onBarClick({ bucket: b });
                    }}
                  >
                    {barRows.map((r, i) => {
                      const fill = PIE_COLORS[i % PIE_COLORS.length];
                      const fillOpacity =
                        renewalBucket != null && r.bucket !== renewalBucket ? 0.35 : 1;
                      return (
                        <Cell
                          key={r.bucket}
                          fill={fill}
                          fillOpacity={fillOpacity}
                          cursor="pointer"
                        />
                      );
                    })}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </Paper>
          </Box>

          <Paper sx={{ p: 2 }}>
            <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems={{ sm: "center" }} sx={{ mb: 2 }}>
              <Typography variant="subtitle1">Contract register</Typography>
              <FormControl size="small" sx={{ minWidth: 220 }}>
                <InputLabel id="scope-label">End date</InputLabel>
                <Select
                  labelId="scope-label"
                  label="End date"
                  value={endDateScope}
                  onChange={(e) => {
                    setEndDateScope(e.target.value as EndDateScope);
                    setPage(0);
                  }}
                >
                  <MenuItem value="ALL">All</MenuItem>
                  <MenuItem value="PAST">Past end date only</MenuItem>
                  <MenuItem value="UPCOMING">Upcoming only</MenuItem>
                  <MenuItem value="UNKNOWN">Unknown end only</MenuItem>
                </Select>
              </FormControl>
              {(department || renewalBucket) && (
                <Typography variant="body2" color="text.secondary" component="span">
                  Active filter
                  {department ? `: Department: ${department}` : ""}
                  {department && renewalBucket ? " · " : ""}
                  {renewalBucket ? `Window: ${BUCKET_LABEL[renewalBucket]}` : ""}
                </Typography>
              )}
            </Stack>

            <TableContainer>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Agency/Department</TableCell>
                    <TableCell>Contract #</TableCell>
                    <TableCell align="right">Value</TableCell>
                    <TableCell>Supplier</TableCell>
                    <TableCell>Procurement type</TableCell>
                    <TableCell>Effective from</TableCell>
                    <TableCell sortDirection={sortDesc ? "desc" : "asc"}>
                      <TableSortLabel
                        active
                        direction={sortDesc ? "desc" : "asc"}
                        onClick={() => {
                          setSortDesc((d) => !d);
                          setPage(0);
                        }}
                      >
                        Effective to
                      </TableSortLabel>
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {tableRows.map((row) => (
                    <TableRow
                      key={row.id}
                      hover
                      sx={{ cursor: "pointer" }}
                      onClick={() => void openDetail(row)}
                    >
                      <TableCell>{row.agencyDepartment}</TableCell>
                      <TableCell>{row.contractNumber}</TableCell>
                      <TableCell align="right">{formatMoney(row.contractValue)}</TableCell>
                      <TableCell>{row.supplier}</TableCell>
                      <TableCell>{row.procurementType}</TableCell>
                      <TableCell>{formatDate(row.effectiveFrom)}</TableCell>
                      <TableCell>{formatDate(row.effectiveTo)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            <TablePagination
              component="div"
              count={totalElements}
              page={page}
              onPageChange={(_, p) => setPage(p)}
              rowsPerPage={rowsPerPage}
              onRowsPerPageChange={(e) => {
                setRowsPerPage(Number(e.target.value));
                setPage(0);
              }}
              rowsPerPageOptions={[10, 20, 50]}
            />
          </Paper>
        </Stack>
      </Container>

      <Dialog open={detailOpen} onClose={() => setDetailOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>Contract detail</DialogTitle>
        <DialogContent dividers>
          {detailLoading && <Typography>Loading…</Typography>}
          {!detailLoading && detail && (
            <Stack spacing={2}>
              <Box>
                <Typography variant="overline">Contract summary</Typography>
                <Paper variant="outlined" sx={{ p: 2, bgcolor: "grey.50" }}>
                  <Typography variant="body2">
                    {detail.contractSummary?.trim()
                      ? detail.contractSummary
                      : "No summary is stored for this contract in the MVP (future: SAM.gov / LLM enrichment)."}
                  </Typography>
                </Paper>
              </Box>
              <DetailField label="Agency/Department" value={detail.agencyDepartment} />
              <DetailField label="Contract number" value={detail.contractNumber} />
              <DetailField label="Contract value" value={formatMoney(detail.contractValue)} raw={detail.contractValueRaw} />
              <DetailField label="Supplier" value={detail.supplier} />
              <DetailField label="Procurement type" value={detail.procurementType} />
              <DetailField label="Description" value={detail.description} />
              <DetailField label="Type of solicitation" value={detail.typeOfSolicitation} />
              <DetailField label="Effective from" value={formatDate(detail.effectiveFrom)} raw={detail.effectiveFromRaw} />
              <DetailField label="Effective to" value={formatDate(detail.effectiveTo)} raw={detail.effectiveToRaw} />
            </Stack>
          )}
        </DialogContent>
      </Dialog>
    </Box>
  );
}

function DetailField({ label, value, raw }: { label: string; value: string | null; raw?: string | null }) {
  return (
    <Box>
      <Typography variant="caption" color="text.secondary">
        {label}
      </Typography>
      <Typography variant="body2">{value || "—"}</Typography>
      {raw && raw !== value && (
        <Typography variant="caption" color="text.secondary" display="block">
          Source: {raw}
        </Typography>
      )}
    </Box>
  );
}
