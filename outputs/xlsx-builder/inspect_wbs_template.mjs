import { FileBlob, SpreadsheetFile } from "@oai/artifact-tool";

const inputPath = "E:/qq下载路径/项目文档资料/费控云XXX开发项目wbs模板.xlsx";
const input = await FileBlob.load(inputPath);
const workbook = await SpreadsheetFile.importXlsx(input);

const summary = await workbook.inspect({
  kind: "sheet",
  include: "id,name,index,range",
  maxChars: 4000,
});
console.log(summary.ndjson);

const wbsRegion = await workbook.inspect({
  kind: "region",
  sheetId: "WBS",
  range: "A1:M35",
  maxChars: 12000,
  tableMaxRows: 35,
  tableMaxCols: 13,
  tableMaxCellChars: 120,
});
console.log(wbsRegion.ndjson);

for (const name of ["一期目标", "项目风险登记", "项目问题日志", "会议记录"]) {
  const region = await workbook.inspect({
    kind: "region",
    sheetId: name,
    range: "A1:I12",
    maxChars: 7000,
    tableMaxRows: 12,
    tableMaxCols: 9,
    tableMaxCellChars: 120,
  });
  console.log(region.ndjson);
}

const errors = await workbook.inspect({
  kind: "match",
  searchTerm: "#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A",
  options: { useRegex: true, maxResults: 50 },
  summary: "formula errors",
});
console.log(errors.ndjson);
