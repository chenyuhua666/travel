import fs from "node:fs/promises";
import { FileBlob, SpreadsheetFile } from "@oai/artifact-tool";

const templatePath = "E:/qq下载路径/项目文档资料/费控云XXX开发项目wbs模板.xlsx";
const outputPath = "E:/qq下载路径/差旅报销系统_WBS报告.xlsx";
const tasksPath = "E:/codelocation/IDEA/travel/tools/wbs_tasks.json";

const input = await FileBlob.load(templatePath);
const workbook = await SpreadsheetFile.importXlsx(input);
const tasks = JSON.parse(await fs.readFile(tasksPath, "utf8"));

function asDate(value) {
  return value ? new Date(`${value}T00:00:00`) : null;
}

function clear(sheet, range) {
  sheet.getRange(range).unmerge();
  sheet.getRange(range).clear({ applyTo: "all" });
}

function styleHeader(range) {
  range.format = {
    fill: "#2E74B5",
    font: { bold: true, color: "#FFFFFF" },
    wrapText: true,
  };
}

function styleSubHeader(range) {
  range.format = {
    fill: "#E8EEF5",
    font: { bold: true, color: "#1F4D78" },
    wrapText: true,
  };
}

function setWidths(sheet) {
  const widths = [80, 120, 110, 240, 360, 100, 120, 105, 105, 80, 95, 220, 160];
  for (let i = 0; i < widths.length; i += 1) {
    sheet.getRangeByIndexes(0, i, 1, 1).format.columnWidthPx = widths[i];
  }
}

const wbs = workbook.worksheets.getItem("WBS");
for (const table of wbs.tables.items ?? []) {
  table.delete();
}
clear(wbs, "A1:M220");
wbs.showGridLines = false;
wbs.getRange("A1:M1").values = [["差旅报销系统开发项目WBS", "", "", "", "", "", "", "", "", "", "", "", ""]];
wbs.getRange("A1:M1").merge();
styleHeader(wbs.getRange("A1:M1"));
wbs.getRange("A1:M1").format.font = { bold: true, color: "#FFFFFF", size: 16 };
wbs.getRange("A1:M1").format.rowHeightPx = 34;
wbs.getRange("A2:M2").values = [[
  "序号",
  "计划阶段",
  "板块",
  "任务名称",
  "任务说明",
  "责任人",
  "协助人",
  "开始时间",
  "结束时间",
  "进度",
  "完成状态",
  "产出要求",
  "备注",
]];
styleSubHeader(wbs.getRange("A2:M2"));
wbs.getRange("A2:M2").format.rowHeightPx = 30;
wbs.getRange(`A3:M${tasks.length + 2}`).values = tasks.map((task) => [
  task.code,
  task.phase,
  task.block,
  task.taskName,
  task.taskDesc,
  task.owner,
  task.support,
  asDate(task.start),
  asDate(task.end),
  task.progress,
  task.status,
  task.output,
  task.notes,
]);
wbs.getRange(`A3:M${tasks.length + 2}`).format.wrapText = true;
wbs.getRange(`A3:M${tasks.length + 2}`).format.rowHeightPx = 52;
wbs.getRange(`H3:I${tasks.length + 2}`).format.numberFormat = "yyyy-mm-dd";
wbs.getRange(`J3:J${tasks.length + 2}`).format.numberFormat = "0%";
wbs.getRange(`K3:K${tasks.length + 2}`).dataValidation = {
  rule: { type: "list", values: ["未开始", "进行中", "已完成", "待执行"] },
};
wbs.freezePanes.freezeRows(2);
setWidths(wbs);

const objectives = workbook.worksheets.getItem("一期目标");
for (const table of objectives.tables.items ?? []) {
  table.delete();
}
clear(objectives, "A1:I220");
objectives.showGridLines = false;
objectives.getRange("A1:I1").values = [[
  "一级模块编号",
  "一级模块名称",
  "二级模块编号",
  "二级模块名称",
  "三级模块名称",
  "排期",
  "责任组",
  "备注",
  "完成情况",
]];
styleSubHeader(objectives.getRange("A1:I1"));
objectives.getRange("A2:I8").values = [
  ["1001", "差旅报销", "100101", "登录与权限", "JWT登录、Token拦截、本人数据权限", "一期", "后端/前端", "已实现", "已完成"],
  ["1001", "差旅报销", "100102", "基础数据", "公司、部门、员工、业务类型、城市、项目", "一期", "后端/前端", "已实现", "已完成"],
  ["1001", "差旅报销", "100103", "报销列表", "查询、分页、新增、编辑、审批、复制、删除", "一期", "前端/后端", "已实现", "已完成"],
  ["1001", "差旅报销", "100104", "报销表单", "基础信息、行程、补助日历、费用合计、分摊、备注", "一期", "前端/后端", "已实现", "已完成"],
  ["1001", "差旅报销", "100105", "状态流转", "草稿、审批中、审批通过、已作废", "一期", "后端", "已实现", "已完成"],
  ["1001", "差旅报销", "100106", "测试联调", "后端测试、前端构建、接口联调", "一期", "测试", "前端和接口联调待最终回归", "进行中"],
  ["1001", "差旅报销", "100107", "上线准备", "生产配置、数据库初始化、部署说明", "二期/上线前", "运维/DBA", "后续阶段", "未开始"],
];
objectives.getRange("A1:I8").format.wrapText = true;

const risks = workbook.worksheets.getItem("项目风险登记");
for (const table of risks.tables.items ?? []) {
  table.delete();
}
clear(risks, "A1:I80");
risks.showGridLines = false;
risks.getRange("A1:I1").values = [[
  "登记日期",
  "登记人",
  "风险描述",
  "风险责任组",
  "风险责任人",
  "状态",
  "风险应对措施",
  "风险解决人",
  "风险解决日期",
]];
styleSubHeader(risks.getRange("A1:I1"));
risks.getRange("A2:I6").values = [
  [asDate("2026-05-25"), "Codex", "审批接口已有实现，但未接入真实BPM审批流或审批角色表。", "产品/后端", "待定", "开放", "后续引入审批角色、流程实例、审批意见和操作日志。", "", null],
  [asDate("2026-05-25"), "Codex", "草稿保存采用替换子表策略，并发编辑时后提交可能覆盖先提交。", "后端", "待定", "开放", "增加version字段或更新时间并发校验。", "", null],
  [asDate("2026-05-25"), "Codex", "JWT secret存在默认值，生产环境若未覆盖存在安全风险。", "运维/后端", "待定", "开放", "部署时强制环境变量覆盖并定期轮换。", "", null],
  [asDate("2026-05-25"), "Codex", "基础数据暂无维护页面，字典变更依赖脚本。", "产品/后端", "待定", "开放", "补充后台维护或导入机制。", "", null],
  [asDate("2026-05-25"), "Codex", "前端构建和接口联调仍需最终回归确认。", "测试/前端", "待定", "跟踪中", "执行构建、冒烟和接口联调检查。", "", null],
];
risks.getRange("A2:A6").format.numberFormat = "yyyy-mm-dd";
risks.getRange("I2:I6").format.numberFormat = "yyyy-mm-dd";
risks.getRange("A1:I6").format.wrapText = true;

const issues = workbook.worksheets.getItem("项目问题日志");
for (const table of issues.tables.items ?? []) {
  table.delete();
}
clear(issues, "A1:G80");
issues.showGridLines = false;
issues.getRange("A1:G1").values = [[
  "登记日期",
  "登记人",
  "问题类型",
  "问题说明",
  "责任组",
  "责任人",
  "解决方案",
]];
styleSubHeader(issues.getRange("A1:G1"));
issues.getRange("A2:G4").values = [
  [asDate("2026-05-25"), "Codex", "文档问题", "原WBS为DOCX报告，需改为按模板生成XLSX。", "文档", "Codex", "使用费控云WBS模板生成差旅报销系统_WBS报告.xlsx。"],
  [asDate("2026-05-25"), "Codex", "文档问题", "详细设计报告需要页脚页码和带页码目录。", "文档", "Codex", "插入Word页码域和目录域，并调用Word更新字段。"],
  [asDate("2026-05-25"), "Codex", "一致性问题", "详细报告与WBS文件任务可能不一致。", "文档", "Codex", "抽取tools/wbs_tasks.json作为共享任务数据源。"],
];
issues.getRange("A2:A4").format.numberFormat = "yyyy-mm-dd";
issues.getRange("A1:G4").format.wrapText = true;

const meetings = workbook.worksheets.getItem("会议记录");
for (const table of meetings.tables.items ?? []) {
  table.delete();
}
clear(meetings, "A1:F50");
meetings.showGridLines = false;
meetings.getRange("A1:F1").values = [["复盘日期", "复盘类型", "复盘人员", "复盘会议纪要", "所属项目", "项目经理"]];
styleSubHeader(meetings.getRange("A1:F1"));
meetings.getRange("A2:F2").values = [[
  asDate("2026-05-25"),
  "文档交付复盘",
  "产品、后端、前端、测试、Codex",
  "确认WBS使用Excel模板输出；详细设计报告补充目录页码和页脚页码；WBS任务统一从共享JSON生成。",
  "费控云差旅报销模块",
  "项目负责人",
]];
meetings.getRange("A2:A2").format.numberFormat = "yyyy-mm-dd";
meetings.getRange("A1:F2").format.wrapText = true;

setWidths(wbs);

const inspect = await workbook.inspect({
  kind: "region",
  sheetId: "WBS",
  range: `A1:M${tasks.length + 2}`,
  maxChars: 9000,
  tableMaxRows: 10,
  tableMaxCols: 13,
});
console.log(inspect.ndjson);

const errors = await workbook.inspect({
  kind: "match",
  searchTerm: "#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A",
  options: { useRegex: true, maxResults: 100 },
  summary: "formula error scan",
});
console.log(errors.ndjson);

for (const sheetName of ["WBS", "一期目标", "项目风险登记", "项目问题日志", "会议记录"]) {
  const preview = await workbook.render({ sheetName, autoCrop: "all", scale: 1, format: "png" });
  const bytes = new Uint8Array(await preview.arrayBuffer());
  await fs.writeFile(`E:/codelocation/IDEA/travel/outputs/xlsx-builder/preview-${sheetName}.png`, bytes);
}

const output = await SpreadsheetFile.exportXlsx(workbook);
await output.save(outputPath);
console.log(outputPath);
