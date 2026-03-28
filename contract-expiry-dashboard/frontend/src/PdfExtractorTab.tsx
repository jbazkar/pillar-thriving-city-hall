import { useEffect, useRef, useState } from "react";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Container,
  Dialog,
  DialogContent,
  DialogTitle,
  LinearProgress,
  Link,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableRow,
  Tooltip,
  Typography,
} from "@mui/material";
import type { ContractExtractResponse, ExtractFieldKey } from "./api";
import { extractContractPdf } from "./api";
import { confidenceBand, confidenceRowSx } from "./confidenceStyle";

const FIELD_ORDER: { key: ExtractFieldKey; label: string }[] = [
  { key: "agencyDepartment", label: "Agency/Department" },
  { key: "contractNumber", label: "Contract Number" },
  { key: "contractValue", label: "Contract Value" },
  { key: "supplier", label: "Supplier" },
  { key: "procurementType", label: "Procurement Type" },
  { key: "description", label: "Description" },
  { key: "typeOfSolicitation", label: "Type of Solicitation" },
  { key: "effectiveFrom", label: "Effective From" },
  { key: "effectiveTo", label: "Effective To" },
  { key: "synopsis", label: "Synopsis of the contract" },
];

const SYNOPSIS_PREVIEW = 100;

function synopsisPreview(text: string | null): string {
  if (!text) return "";
  if (text.length <= SYNOPSIS_PREVIEW) return text;
  return text.slice(0, SYNOPSIS_PREVIEW) + "…";
}

export default function PdfExtractorTab({ active }: { active: boolean }) {
  const [file, setFile] = useState<File | null>(null);
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [result, setResult] = useState<ContractExtractResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [synopsisOpen, setSynopsisOpen] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (!file || !active) {
      setPdfUrl(null);
      return;
    }
    const url = URL.createObjectURL(file);
    setPdfUrl(url);
    return () => {
      URL.revokeObjectURL(url);
    };
  }, [active, file]);

  const pickPdf = (f: File | null) => {
    setError(null);
    setResult(null);
    if (!f) {
      setFile(null);
      return;
    }
    const extOk = f.name.toLowerCase().endsWith(".pdf");
    const typeOk =
      !f.type || f.type === "application/pdf" || f.type === "application/octet-stream";
    if (!extOk || !typeOk) {
      setError("Please choose a PDF file (.pdf, application/pdf).");
      setFile(null);
      return;
    }
    setFile(f);
  };

  const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const f = e.target.files?.[0] ?? null;
    pickPdf(f);
    e.target.value = "";
  };

  const onDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    const f = e.dataTransfer.files?.[0];
    if (f) pickPdf(f);
  };

  const onDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const submit = async () => {
    if (!file) return;
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const data = await extractContractPdf(file);
      setResult(data);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Extraction failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "background.default" }}>
      <Container maxWidth="xl" sx={{ py: 3 }}>
        <Stack spacing={2}>
          <Typography variant="body1">
            Upload a contract PDF to extract key fields using AI. Results are assisted suggestions only — verify against the
            original document. PDF files only.
          </Typography>

          <input
            ref={inputRef}
            type="file"
            accept="application/pdf,.pdf"
            hidden
            onChange={onFileChange}
          />

          <Paper
            variant="outlined"
            onDrop={onDrop}
            onDragOver={onDragOver}
            sx={{ p: 2, borderStyle: "dashed" }}
          >
            <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems="center">
              <Button variant="contained" onClick={() => inputRef.current?.click()} disabled={loading}>
                Choose PDF
              </Button>
              <Typography variant="body2" color="text.secondary">
                {file ? file.name : "Or drag and drop a PDF here"}
              </Typography>
              <Box sx={{ flexGrow: 1 }} />
              <Button variant="outlined" onClick={() => void submit()} disabled={!file || loading}>
                Extract fields
              </Button>
            </Stack>
          </Paper>

          {loading && <LinearProgress />}
          {error && (
            <Alert severity="error" onClose={() => setError(null)}>
              {error}
            </Alert>
          )}

          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: { xs: "1fr", lg: "1fr 1fr" },
              gap: 2,
              alignItems: "stretch",
            }}
          >
            <Paper sx={{ p: 2, minHeight: 320 }}>
              <Typography variant="subtitle1" gutterBottom>
                Extraction results
              </Typography>
              {!result && !loading && (
                <Typography color="text.secondary" variant="body2">
                  Choose a PDF and run extraction to see fields here.
                </Typography>
              )}
              {loading && (
                <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
                  <CircularProgress />
                </Box>
              )}
              {result && (
                <>
                  <TableContainer>
                    <Table size="small">
                      <TableBody>
                        {FIELD_ORDER.map(({ key, label }) => {
                          const f = result.fields[key];
                          const val = f?.value ?? null;
                          const conf = f?.confidence ?? 0;
                          const band = confidenceBand(conf);
                          const isSynopsis = key === "synopsis";
                          const displayVal =
                            isSynopsis && val
                              ? synopsisPreview(val)
                              : val;
                          return (
                            <TableRow key={key} sx={confidenceRowSx(band)}>
                              <TableCell sx={{ verticalAlign: "top", fontWeight: 600, width: "38%" }}>{label}</TableCell>
                              <TableCell sx={{ verticalAlign: "top" }}>
                                <Typography variant="body2" component="span">
                                  {displayVal ?? "—"}
                                </Typography>
                                {isSynopsis && val && val.length > SYNOPSIS_PREVIEW && (
                                  <>
                                    {" "}
                                    <Link
                                      component="button"
                                      type="button"
                                      variant="body2"
                                      onClick={() => setSynopsisOpen(true)}
                                      sx={{ verticalAlign: "baseline" }}
                                    >
                                      View full synopsis
                                    </Link>
                                  </>
                                )}
                              </TableCell>
                              <TableCell align="right" sx={{ whiteSpace: "nowrap", verticalAlign: "top" }}>
                                <Tooltip title="Model confidence estimate — not a statistical guarantee">
                                  <Typography variant="body2" component="span">
                                    {(conf * 100).toFixed(1)}%
                                  </Typography>
                                </Tooltip>
                              </TableCell>
                            </TableRow>
                          );
                        })}
                      </TableBody>
                    </Table>
                  </TableContainer>
                  {result.meta?.warnings && result.meta.warnings.length > 0 && (
                    <Alert severity="warning" sx={{ mt: 2 }}>
                      {result.meta.warnings.join(" ")}
                    </Alert>
                  )}
                </>
              )}
            </Paper>

            <Paper sx={{ p: 2, minHeight: 480, display: "flex", flexDirection: "column" }}>
              <Typography variant="subtitle1" gutterBottom>
                Document preview
              </Typography>
              {!pdfUrl && (
                <Typography color="text.secondary" variant="body2">
                  PDF preview appears after you select a file.
                </Typography>
              )}
              {pdfUrl && (
                <Box
                  component="iframe"
                  title="PDF preview"
                  src={pdfUrl}
                  sx={{ flex: 1, minHeight: 420, width: "100%", border: 0, bgcolor: "grey.100" }}
                />
              )}
            </Paper>
          </Box>

          <Alert severity="info" variant="outlined">
            AI-assisted extraction requires human verification before reliance. These outputs are not legal advice and are
            not guaranteed accurate.
          </Alert>
        </Stack>
      </Container>

      <Dialog open={synopsisOpen} onClose={() => setSynopsisOpen(false)} maxWidth="md" fullWidth scroll="paper">
        <DialogTitle>Full synopsis</DialogTitle>
        <DialogContent dividers>
          <Typography variant="body2" sx={{ whiteSpace: "pre-wrap" }}>
            {result?.fields.synopsis?.value ?? "—"}
          </Typography>
          <Alert severity="info" sx={{ mt: 2 }} variant="outlined">
            AI-assisted text — verify against the PDF before reliance.
          </Alert>
        </DialogContent>
      </Dialog>
    </Box>
  );
}
