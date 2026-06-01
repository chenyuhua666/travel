import fs from "node:fs/promises";
import { FileBlob, SpreadsheetFile } from "@oai/artifact-tool";

const input = await FileBlob.load("E:/qq下载路径/差旅报销系统_WBS报告.xlsx");
const workbook = await SpreadsheetFile.importXlsx(input);

const sheets = await workbook.inspect({
  kind: "sheet",
  include: "name,range",
  maxChars: 3000,
});
console.log(sheets.ndjson);

const wbs = await workbook.inspect({
  kind: "region",
  sheetId: "WBS",
  range: "A1:M22",
  maxChars: 9000,
  tableMaxRows: 10,
  tableMaxCols: 13,
});
console.log(wbs.ndjson);

const errors = await workbook.inspect({
  kind: "match",
  searchTerm: "#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A",
  options: { useRegex: true, maxResults: 100 },
  summary: "formula error scan",
});
console.log(errors.ndjson);

for (const sheetName of ["WBS", "一期目标", "项目风险登记", "项目问题日志", "会议记录"]) {
  const preview = await workbook.render({ sheetName, autoCrop: "all", scale: 1, format: "png" });
  await fs.writeFile(`E:/codelocation/IDEA/travel/outputs/xlsx-builder/final-preview-${sheetName}.png`, new Uint8Array(await preview.arrayBuffer()));
}
