# Extract: 第八组项目详细设计.docx
paragraphs=295 tables=17 sections=1

## Paragraphs
002 [Normal] 详细设计规范
003 [Normal] 费控云差旅平台
008 [Normal] 开发须知
009 [Fließtext] 详细设计是在充分理解需求的基础上，进行设计文档的编写，设计文档应充分说明复杂功能、核心功能关键实现方法、步骤、路径，应体现设计者对于需求的理解，以及知道如何实现，
010 [Fließtext] 设计文档也是指导实现者如何实现的参考指南。详细设计不仅要关注功能性需求的设计，同时也应该包含非功能性需求的设计，而我们往往容易忽略非功能性需求的设计，因此，这里特意予以强调。
011 [Heading 2] 1.非功能性要求
012 [Heading 3] 可靠性
013 [Fließtext] 可靠性指软件在异常情况下或在被非法、非常规使用时维持自身功能的能力。主要体现在容错和健壮性这两个方面。
014 [Fließtext] 容错指软件发生故障时仍保持正常运行的能力。它保证软件能在异常情况下正常运行，并在内部完成故障的修复工作。修复完成后，软件需要继续或从头开始执行异常位置的操作。
015 [Fließtext] 健壮性是保护软件不受非正常使用方式或非法输入影响的能力。具备该能力后，不论怎样的使用方式，软件都能准确迁移至系统定义的状态。
016 [Fließtext] 例如：
017 [Fließtext] ● 分布式系统在发生通信异常时会先暂时切断连接，等问题修复完成后再重新连接，恢复软件的运行； / 　　● 系统缺陷率每1,000小时最多发生1次故障； / 　　● 因软件系统的失效而造成无法完成业务的概率要小于5‰。
019 [Heading 3] 高性能
020 [Fließtext] 性能是系统或组件在给定的限制条件（如速度、精度或内存使用）内完成其指定功能的程度。性能表现是衡量软件质量的重要指标，在需求分析和系统设计阶段就必须充分考虑性能因素。性能指标主要包括响应时间、并发数、资源使用率等。简单地说，性能需求体现了系统如何“多快好省”地实现客户的功能需求。
021 [Fließtext] 例如：
022 [Fließtext] ● 响应时间：在95％的情况下，一般时段响应时间不超过1.5秒，高峰时段不超过4秒；在网络畅通时，电子地图刷新时间不超过10秒； / 　　● 并发数：系统可以同时满足10,000个用户请求； / 　　● 资源使用率：CPU占用率<=50%，内存占用率<=50%。
023 [Heading 3] 可维护性
024 [Fließtext] 功能在应对需求变更时，实现业务扩展时，能否比较方便地满足复杂多变的需求，一般要在设计上，要进行灵活的设计，以实现业务上的持续迭代，保证一定的可维护性。
025 [Fließtext] 例如：
026 [Fließtext] ● 从接到修改请求后，对于普通修改应在1~2天内完成； / 　　● 对于评估后为重大需求或设计修改应在1周内完成； / 　　● 90%的BUG修改时间不超过1个工作日，其他不超过2个工作日。
027 [Heading 3] 安全性
028 [Fließtext] 安全性指产品消除潜在风险的能力和对风险的承受能力。包括保密性、可靠性和完整性三个子特性。保密性指数据不能被授权用户以外的任何人访问的能力。可靠性指授权用户可以不受阻止的访问数据、与其它软件的兼容的能力和产品的强壮度。完整性指按预期目标完成任务的能力。
029 [Fließtext] 一般分为程序安全、系统安全、数据安全。程序安全指开发的程序是否是安全的，程序上有没有安全的漏洞，例如Web开发中服务器代码没有对输入的参数进行验证，从而导致客户端机器人轻易的获取数据。系统安全指系统整体的安全，例如安全的粒度，未经授权的用户是否可以轻易的访问非法的数据等。数据安全是对数据的保护，数据库中数据有没有做审核，用户之间是否会共享数据等。
030 [Normal] 例如：
031 [Fließtext] ● 严格权限访问控制，用户在经过身份认证后，只能访问其权限范围内的数据，只能进行其权限范围内的操作；
032 [Fließtext] ● 提供运行日志管理及安全审计功能，可追踪系统的历史使用情况；
033 [Fließtext] ● 能经受来自互联网的一般性恶意攻击。如病毒（包括木马）攻击、口令猜测攻击、黑客入侵等；
038 [Heading 1] 术语定义
040 [Normal] 来自于产品或实施的需求描述，只谈需求，不谈任何设计实现。
041 [Heading 2] 2. 功能性需求描述
042 [Heading 2] 2.1需求概述
043 [Normal] 费控云差旅平台是为企业提供差旅费用管控的Web应用系统，核心功能包括差旅报销单的全生命周期管理。系统包含以下四个核心需求：
044 [Heading 5] 需求1：用户登录与身份认证
045 [Normal] 系统用户通过用户名和密码进行登录认证，登录成功后获得JWT令牌，用于后续所有API请求的身份验证。令牌有效期480分钟，超时后需重新登录。
046 [Heading 5] 需求2：报销单全生命周期管理
047 [Normal] 支持报销单的创建（草稿）、编辑、提交审批、审批通过、撤回、作废、复制和删除等全流程操作。每个报销单包含：基本信息、出行行程、补助明细和费用分摊四个部分。
049 [Heading 5] 需求3：主数据管理
050 [Normal] 提供公司、部门、员工、业务类型（三级树形结构）、城市、项目等基础数据的查询接口，供前端下拉选择使用。业务类型支持级联选择（如：员工差旅活动 → 境内出差 → 项目出差）。
051 [Heading 5] 需求4：补助自动计算
052 [Normal] 根据行程天数和目的地城市类型自动计算餐费补助、交通补助和通讯补助的标准金额。城市类型分为三类：一类城市100元/天，二类城市80元/天，三类城市50元/天。交通和通讯补助统一为40元/天。
054 [Heading 2] 2.2业务全景图
057 [Heading 1] 3功能详细设计
058 [Heading 2] 3.1用户登录（AuthController）
059 [Heading 3] 3.1.1功能内容
060 [List Paragraph] 前端登录页面收集用户名和密码，调用 POST /api/auth/login。
061 [List Paragraph] 后端AuthService接收LoginDTO，查询sys_user表验证用户名和密码。
062 [List Paragraph] 密码验证使用PasswordHasher进行PBKDF2-SHA256哈希比对。
063 [List Paragraph] 验证通过后JwtTokenService生成JWT令牌，返回给前端。
064 [List Paragraph] 前端将Token存入localStorage，后续请求通过Axios拦截器自动附加Authorization头。
066 [Heading 3] 3.1.2实现逻辑
067 [Normal] (1) 前端提交POST /api/auth/login，携带username和password。
068 [Normal] (2) AuthController委托AuthService.login()处理。
069 [Normal] (3) AuthService通过UserMapper查询sys_user表，验证用户存在且enabled=1。
070 [Normal] (4) PasswordHasher解析存储的哈希格式（pbkdf2_sha256$iterations$salt$hash），使用JCE的PBKDF2WithHmacSHA256重新计算并比对。
071 [Normal] (5) JwtTokenService创建JWT：Header(alg:HS256, typ:JWT) + Payload(sub, uid, eid, roles, iat, exp)，使用HMAC-SHA256签名。
072 [Normal] (6) 返回LoginVO：token + tokenType("Bearer") + expiresInMinutes(480)。
073 [Normal] (7) SecurityConfig中/api/auth/login路径permitAll()，其余请求需认证。
074 [Normal] (8) JwtAuthenticationFilter从请求头解析Bearer Token，还原CurrentUser并注入SecurityContext。
075 [Heading 3] 3.1.3异常处置
076 [List Paragraph] 用户名不存在或密码不匹配：抛出BusinessException(401, "用户名或密码错误")。
077 [List Paragraph] Token过期：JwtTokenService检测exp字段，抛出BusinessException(401)，前端清除Token并跳转登录页。
078 [List Paragraph] Token签名无效：抛出BusinessException(401, "令牌签名无效")。
083 [Heading 2] 3.2报销单列表查询
084 [Heading 3] 3.2.1功能内容
085 [List Paragraph] 报销单列表页展示当前用户的报销单分页数据，每页默认10条。
086 [List Paragraph] 支持按单号、标题、事由、公司、部门、报销人、业务类型进行多条件组合筛选。
088 [Heading 3] 3.2.2实现逻辑
089 [Normal] (1) 前端GET /api/reimbursements?current=1&size=10&...。
090 [Normal] (2) ReimbursementController.page()接收分页参数和筛选条件，通过@AuthenticationPrincipal注入CurrentUser。
091 [Normal] (3) ReimbursementService.page()使用MyBatis-Plus Page + LambdaQueryWrapper构建动态查询。
092 [Normal] (4) 非管理员用户自动过滤owner_user_id=当前用户ID，实现数据隔离。
093 [Normal] (5) 结果按creation_time降序排列，返回PageResult<ReimbursementListVO>。
095 [Heading 3] 3.2.3异常处置
096 [List Paragraph] 分页参数异常：Service层对page/size做钳位（current最小1，size最小1最大100）。
097 [List Paragraph] 数据库异常：GlobalExceptionHandler捕获，返回500+通用错误消息。
101 [Heading 2] 3.3报销单草稿创建与编辑
102 [Heading 3] 3.3.1功能内容
103 [List Paragraph] 创建草稿：POST /api/reimbursements/drafts，首次创建自动生成单号。
104 [List Paragraph] 编辑草稿：PUT /api/reimbursements/{id}/draft，仅草稿状态可编辑。
105 [List Paragraph] 包含四个数据块：基本信息、行程列表、补助明细、费用分摊。
106 [List Paragraph] 补助金额根据城市类型自动计算标准：一类100元、二类80元、三类50元，交通和通讯各40元。
108 [Heading 3] 3.3.1实现逻辑
109 [Normal] (1) Service层使用@Transactional保证原子性。
110 [Normal] (2) 编辑时采用"先删后插"模式（replaceChildren）：先级联删除旧子数据，再插入新子数据。
111 [Normal] (3) 行程验证：出行人/城市存在性、到达日期≥出发日期、到达日期≤当前日期、同一出行人行程日期不重叠。
112 [Normal] (4) 补助验证：每日补助日期在行程范围内、同行程日期不可重复、金额在0到标准之间。
113 [Normal] (5) 费用分摊验证：公司存在性、分摊比例0~1、分摊金额非负。
114 [Normal] (6) 汇总计算：subsidy_total/meal/transportation/phone等汇总值回写主表。
116 [Heading 3] 3.3.3异常处置
117 [List Paragraph] 非草稿状态编辑：抛出BusinessException("仅草稿报销单可编辑")。
118 [List Paragraph] 行程日期重叠：同一出行人行程日期区间有交集时抛出BusinessException。
119 [List Paragraph] 分摊验证失败：分摊比例合计≠100%或金额合计≠补助总额时拒绝提交。
122 [Heading 2] 3.4报销单状态流转
123 [Heading 3] 3.4.1功能内容
124 [Normal] 状态枚举与流转规则：
126 [Heading 3] 3.4.2实现逻辑
127 [Normal] 所有状态变更方法均使用@Transactional。核心校验逻辑：
128 [List Paragraph] submit()：验证必填字段 + 行程/分摊完整性后，状态0→3。
129 [List Paragraph] approve()：验证状态=3，状态3→1。
130 [List Paragraph] withdraw()：验证状态=3，状态3→0。
131 [List Paragraph] voidByOwner()：验证状态≠2，任意非作废→2。
132 [List Paragraph] copy()：读取详情，复制基本信息+行程+补助+分摊生成新草稿。
133 [List Paragraph] deleteDraft()：验证状态=0且为本人数据，级联删除。
134 [Heading 3] 3.4.3异常处置
135 [List Paragraph] 状态校验：每个操作前严格校验当前状态，不合法则抛出BusinessException说明原因。
136 [List Paragraph] 权限校验：requireOwned()验证owner_user_id，非本人数据抛出BusinessException(403)。
137 [List Paragraph] 数据库外键设置ON DELETE CASCADE：删除主表自动清理trip/subsidy/subsidy_day/allocation。
145 [Heading 2] 3.5主数据查询
146 [Heading 3] 3.5.1功能内容
147 [List Paragraph] 提供6个主数据GET接口：公司、部门、员工、城市、项目、业务类型树。
148 [List Paragraph] 全部返回全量数据（无分页），供前端下拉框和级联选择器使用。
149 [List Paragraph] 业务类型以递归树形结构返回，parent_id构建父子关系，leaf_flag标识叶子节点。
151 [Heading 3] 3.5.2实现逻辑
152 [Normal] (1) MasterDataController路由前缀/api/master，委托MasterDataService处理。
153 [Normal] (2) MasterDataService使用Wrappers.lambdaQuery()全表查询。
154 [Normal] (3) 业务类型树构建：查询全部→按parent_id分组→从parent_id=null的根节点递归构建树。
155 [Normal] (4) 所有VO使用Java record类，不可变且自动生成访问器。
160 [Heading 1] 4技术实现设计
161 [Heading 2] 4.1系统结构设计
162 [Normal] 后端技术栈：
163 [List Paragraph] 运行环境：Java 17 + Spring Boot 4.0.6 + Tomcat 11（嵌入式）
164 [List Paragraph] 安全框架：Spring Security 7 + 自实现JWT（HMAC-SHA256签名）
165 [List Paragraph] ORM框架：MyBatis-Plus 3.5.15 + MySQL 8.0 + HikariCP 7.0.2
166 [List Paragraph] 校验：Jakarta Validation（Hibernate Validator 9）
167 [List Paragraph] JSON序列化：Jackson 3.1.2
169 [Normal] 前端技术栈：
170 [List Paragraph] 运行环境：Node.js 22 + Vite 8
171 [List Paragraph] UI框架：Vue 3.5 + TypeScript 6.0 + Element Plus 2.11
172 [List Paragraph] 状态管理：Pinia 3.0 + 路由：Vue Router 5.0
173 [List Paragraph] HTTP客户端：Axios 1.13
174 [Heading 2] 4.1.1后端模块划分
176 [Heading 2] 4.2接口及核心类设计
177 [Heading 3] 4.2.1核心Service类
178 [Normal] ReimbursementService
179 [Normal] 报销单核心服务（803行），管理报销单全生命周期。核心方法：page()分页查询、detail()详情、createDraft()/saveDraft()草稿CRUD、submit()/approve()/withdraw()/voidByOwner()状态流转、copy()复制、deleteDraft()删除。内部类Summary使用累加器模式汇总补助金额。
181 [Normal] JwtTokenService
182 [Normal] 自实现JWT服务，不依赖第三方JWT库。createToken()构建三段式JWT并用HMAC-SHA256签名；parseToken()验证签名+过期时间后还原CurrentUser。
184 [Normal] MasterDataService
185 [Normal] 主数据查询服务，提供6种基础数据的全量查询。businessTypeTree()使用递归算法从扁平数据构建三级树形结构。
186 [Heading 3] 核心实体关系（ER）
187 [Normal] 报销主表（fk_reim_main）为聚合根，关联关系如下：
188 [List Paragraph] fk_reim_main → fk_reim_trip（1:N）：一个报销单可包含多条行程
189 [List Paragraph] fk_reim_trip → fk_reim_subsidy（1:1）：每条行程对应一条补助汇总
190 [List Paragraph] fk_reim_subsidy → fk_reim_subsidy_day（1:N）：每条补助包含多天每日明细
191 [List Paragraph] fk_reim_main → fk_reim_allocation（1:N）：一个报销单可有多条费用分摊
192 [List Paragraph] fk_reim_main → sys_user（N:1）：关联数据所有者（owner_user_id）
193 [List Paragraph] 所有子表外键设置ON DELETE CASCADE，删除主表自动级联清理。
195 [Heading 2] 与前端的交互
196 [Normal] 前后端通过RESTful API交互，路径前缀/api，JSON格式。前端通过Vite代理（/api → localhost:8088）转发。
199 [Heading 2] 与第三方的交互
200 [Normal] 当前V1.0版本为独立部署的单体应用，不涉及第三方系统交互。所有功能在系统内部闭环完成。未来扩展点：企业微信审批流对接、财务系统数据同步（预留Feign/HTTP客户端扩展接口）。
203 [Heading 1] 5关键技术点
204 [Heading 1] 5.1 并发编程
205 [Normal] 当前V1.0版本为单用户操作场景，报销单操作通过数据库更新实现基本并发控制。后续如引入高并发场景建议：使用@Version乐观锁防并发覆盖；报销单号使用数据库自增ID避免并发冲突；如需异步处理引入Spring @Async + 线程池。
207 [Heading 2] 5.2 事务控制
208 [Heading 3] 5.2.1数据库局部事务
209 [Normal] 核心写操作均使用Spring声明式事务（@Transactional）：
210 [List Paragraph] createDraft()/saveDraft()：保证主表+子表（行程/补助/分摊）的原子写入。
211 [List Paragraph] replaceChildren()：先删后插模式，在事务内完成子表数据全量替换。
212 [List Paragraph] 所有状态变更方法（submit/withdraw/approve/void）均使用@Transactional。
213 [List Paragraph] 事务传播行为使用默认REQUIRED，确保嵌套调用在同一事务中。
215 [Heading 3] 5.2.2分布式事务
216 [Normal] 当前V1.0为单体应用，不涉及分布式事务。后续如拆分为微服务，需引入Seata或基于MQ的最终一致性方案。
218 [Heading 2] 5.3Job使用
219 [Normal] 当前V1.0版本无定时任务。后续可能涉及：报销数据定期归档Job（清理超期的已审批/已作废数据）；JWT为无状态设计，无需服务端清理Job。
221 [Heading 2] 5.4权限控制
222 [Normal] 系统实现基于角色的访问控制（RBAC）：
223 [List Paragraph] CurrentUser record携带userId/employeeId/username/roles，通过@AuthenticationPrincipal注入。
224 [List Paragraph] 数据级权限：isAdmin()判断，非管理员自动过滤owner_user_id。Service层requireOwned()校验归属。
225 [List Paragraph] 操作级权限：每个状态流转方法校验当前状态是否允许目标操作。
226 [List Paragraph] 前端路由守卫：beforeEach检查Token，未登录自动跳转/login。
228 [Heading 2] 5.5 Redis使用
229 [Normal] 当前版本未使用Redis（JWT为无状态设计，无需Session存储）。后续引入建议：
230 [List Paragraph] Key前缀规范：travel:{模块}:{标识}（如travel:cache:company:list）。
231 [List Paragraph] 缓存主数据（公司/部门/城市等低变更数据），TTL 30分钟。
233 [Heading 2] 5.6 敏感信息处理
234 [List Paragraph] 用户密码：PBKDF2-SHA256 + 随机盐哈希存储，不可逆。
235 [List Paragraph] JWT Secret：通过配置属性注入，生产环境以环境变量TRAVEL_JWT_SECRET覆盖。
236 [List Paragraph] 数据库密码：通过环境变量TRAVEL_DB_PASSWORD覆盖，配置文件中不写明文。
237 [List Paragraph] 前端Token：存储于localStorage，建议生产环境升级为HttpOnly Cookie。
238 [Heading 2] 5.7 错误码使用
239 [List Paragraph] BusinessException携带错误码和中文描述，GlobalExceptionHandler统一转换为Result<T>响应。
241 [Heading 2] 5.8 异动日志
242 [Normal] 系统通过以下字段记录关键操作：
243 [List Paragraph] created_at：数据创建时间（数据库默认CURRENT_TIMESTAMP）。
244 [List Paragraph] update_time：数据最后更新时间（Service层显式设置LocalDateTime.now()）。
245 [List Paragraph] owner_user_id：数据所有者，创建时写入且不可修改。
246 [List Paragraph] 关键状态变更（提交/审批/撤回/作废）同时更新update_time，可追踪完整操作时间线。
248 [Heading 2] 5.9 大数据量问题
249 [Normal] V1.0数据量预测：fk_reim_main年增量约10万条，5年累计50万条。当前已采取的措施：
250 [List Paragraph] 分页查询使用MyBatis-Plus Page插件 + 索引（status+creation_time复合索引）。
251 [List Paragraph] 子表查询按main_id精确匹配 + 主键索引，避免全表扫描。
252 [List Paragraph] 后续数据量增长方案：按月分区、1年以上已审批数据归档至历史表。
254 [Heading 2] 5.10 缓存/MQ/重试机制
255 [Normal] 当前V1.0版本均未使用缓存、MQ和重试机制。这些技术点将在业务规模扩大后按需引入：缓存层使用Spring Cache + Redis优化主数据读取；MQ使用RocketMQ/RabbitMQ处理审批通知等异步任务（需保证消息幂等性）；重试机制使用Spring Retry处理外部接口调用失败场景。
257 [Heading 1] 6数据库设计
258 [Heading 2] 6.1 数据库表设计
259 [Normal] 数据库名：travel，字符集：utf8mb4，排序规则：utf8mb4_0900_ai_ci。共12张表，分为三类：
261 [Heading 3] 6.1.1 系统基础数据表（sys_*）
263 [Heading 3] 6.1.2 业务基础数据表（biz_*）
265 [Heading 3] 6.1.3 报销业务数据表（fk_reim_*）
267 [Heading 2] 6.2 数据库访问模块设计
268 [List Paragraph] Entity类使用Lombok @Data + @TableName注解，字段自动映射（下划线转驼峰）。
269 [List Paragraph] Mapper接口继承BaseMapper<T>，自动获得CRUD方法，无需编写XML映射文件。
270 [List Paragraph] Service层使用Wrappers.lambdaQuery()构建类型安全的动态查询条件。
271 [List Paragraph] 分页使用Page<T> + selectPage()方法。
272 [List Paragraph] 全局配置：map-underscore-to-camel-case=true + id-type=auto（数据库自增主键）。
275 [Heading 1] 7.费控云差旅平台开发项目 WBS
277 [Heading 1] 8.附录1  接口定义明细
278 [Heading 2] 附录1.1 认证接口
280 [Heading 2] 附录1.2 报销单接口
281 [Normal] (1) 报销单列表查询：
283 [Normal] (2) 报销单详情：
285 [Normal] (3) 创建/编辑草稿：
287 [Normal] (4) 状态流转接口：
289 [Heading 2] 附录1.3 主数据接口
291 [Heading 2] 附录1.4 全局响应格式
293 [Heading 2] 所有接口遵循此统一格式。HTTP状态码与业务错误码独立：HTTP 200可携带业务错误（如code=401表示业务层认证失败）。

## Tables

### Table 1 rows=5 cols=2
001 | 文件状态： 草稿 修改 正式发布 | 所属项目编号：8
002 | 文件状态： 草稿 修改 正式发布 | 版 本：1
003 | 文件状态： 草稿 修改 正式发布 | 撰 写 人:徐文卓，徐鹤文，陈昱桦
004 | 文件状态： 草稿 修改 正式发布 | 完成日期：2026-5-25
005 | 文件状态： 草稿 修改 正式发布 | 发布日期：2026-5-26

### Table 2 rows=10 cols=3
001 | 缩写/术语 | 全称 | 说明
002 | JWT | JSON Web Token | 无状态身份认证令牌，基于HMAC-SHA256签名
003 | MyBatis-Plus | MyBatis增强工具 | 提供Lambda查询构造器和分页插件的ORM框架
004 | PBKDF2 | Password-Based Key Derivation Function 2 | 基于口令的密钥派生算法，用于密码哈希存储
005 | DTO/VO | Data Transfer Object / View Object | Java record类，分别用于入参和出参的数据封装
006 | WBS | Work Breakdown Structure | 工作分解结构，用于项目任务划分和进度管理
007 | Element Plus | Vue 3 UI组件库 | 提供表格、表单、对话框等企业级UI组件
008 | Pinia | Vue状态管理库 | Vue 3官方推荐的状态管理方案
009 | HikariCP | 高性能JDBC连接池 | Spring Boot默认连接池，提供极速连接获取
010 | SY | 报销单号前缀 | 报销单号格式为"SY" + 10位数字（如SY0000000001）

### Table 3 rows=5 cols=3
001 | 状态 | 编码 | 允许的流转操作
002 | 草稿 | 0 | → 提交审批(→3)；→ 删除
003 | 审批通过 | 1 | → 作废(→2)
004 | 已作废 | 2 | （终态，不可流转）
005 | 审批中 | 3 | → 审批通过(→1)；→ 撤回(→0)；→ 作废(→2)

### Table 4 rows=17 cols=4
001 | 一级模块 | 二级模块 | 三级模块 | 职责说明
002 | config | security | SecurityConfig | Spring Security安全配置，定义过滤链
003 | config | security | JwtTokenService | JWT令牌创建(HMAC-SHA256)与解析验证
004 | config | security | JwtAuthFilter | OncePerRequestFilter，解析Bearer Token
005 | config | security | CurrentUser | Java record，用户身份信息载体
006 | config | exception | BusinessException | 业务异常类，携带错误码和消息
007 | config | exception | GlobalExceptionHandler | @RestControllerAdvice全局异常处理
008 | config | mybatis | MybatisPlusConfig | 分页插件配置
009 | controller | - | AuthController | 认证接口：POST /api/auth/login
010 | controller | - | ReimbursementController | 报销单CRUD全生命周期接口(11个)
011 | controller | - | MasterDataController | 主数据查询接口(6个GET接口)
012 | service | - | AuthService | 登录验证逻辑 + JWT签发
013 | service | - | ReimbursementService | 报销单核心业务(803行)，含状态流转/补助计算
014 | service | - | MasterDataService | 基础数据查询 + 业务类型树构建
015 | service | - | PasswordHasher | PBKDF2-SHA256密码哈希验证组件
016 | mapper | - | 12个Mapper接口 | 继承BaseMapper<T>，无需XML配置
017 | entity/dto/vo | - | 数据对象包 | 12个Entity + 5个DTO record + 10个VO record

### Table 5 rows=18 cols=4
001 | 功能 | 方法 | 接口路径 | 前端调用位置
002 | 用户登录 | POST | /api/auth/login | authStore.login()
003 | 报销列表 | GET | /api/reimbursements | getReimbursements()
004 | 报销详情 | GET | /api/reimbursements/{id} | getReimbursement()
005 | 创建草稿 | POST | /api/reimbursements/drafts | createDraft()
006 | 保存草稿 | PUT | /api/reimbursements/{id}/draft | saveDraft()
007 | 提交审批 | POST | /api/reimbursements/{id}/submit | submitReimbursement()
008 | 审批通过 | POST | /api/reimbursements/{id}/approve | approveReimbursement()
009 | 撤回 | POST | /api/reimbursements/{id}/withdraw | withdrawReimbursement()
010 | 作废 | POST | /api/reimbursements/{id}/void | voidReimbursement()
011 | 复制 | POST | /api/reimbursements/{id}/copy | copyReimbursement()
012 | 删除草稿 | DELETE | /api/reimbursements/{id} | deleteReimbursement()
013 | 公司列表 | GET | /api/master/companies | getCompanies()
014 | 部门列表 | GET | /api/master/departments | getDepartments()
015 | 员工列表 | GET | /api/master/employees | getEmployees()
016 | 城市列表 | GET | /api/master/cities | getCities()
017 | 项目列表 | GET | /api/master/projects | getProjects()
018 | 业务类型 | GET | /api/master/business-types/tree | getBusinessTypes()

### Table 6 rows=7 cols=4
001 | 错误码 | HTTP状态 | 说明 | 典型场景
002 | 0 | 200 | 成功 | 正常返回业务数据
003 | 400 | 400 | 参数/业务校验失败 | @Valid校验不通过、业务规则不满足
004 | 401 | 401 | 认证失败 | 用户名密码错误、Token无效/过期
005 | 403 | 403 | 无权限 | 操作他人数据或越权操作
006 | 404 | 404 | 资源不存在 | 报销单不存在
007 | 500 | 500 | 系统异常 | 未预期的运行时异常

### Table 7 rows=5 cols=4
001 | 表名 | 说明 | 主键/唯一键 | 关联关系
002 | sys_company | 公司信息表 | id PK / company_no UK | -
003 | sys_department | 部门信息表 | id PK / department_no UK | -
004 | sys_employee | 员工信息表 | id PK / employee_no UK | FK→sys_company, sys_department
005 | sys_user | 系统用户表 | id PK / username UK | FK→sys_employee；roles存储角色字符串

### Table 8 rows=4 cols=4
001 | 表名 | 说明 | 关键字段 | 特殊设计
002 | biz_business_type | 业务类型（树形） | business_type_no/name, parent_id | parent_id自关联，leaf_flag标识叶子
003 | biz_city | 城市信息表 | city_no/name, city_type | city_type: 1一类/2二类/3三类城市
004 | biz_project | 项目信息表 | project_no/name | 含"非项目类费用归集"兜底记录

### Table 9 rows=6 cols=4
001 | 表名 | 说明 | 核心字段 | 外键/级联
002 | fk_reim_main | 报销主表 | reim_no, 状态/金额/人员/部门/公司/业务类型 | FK→sys_user(owner)
003 | fk_reim_trip | 行程明细 | 出行人/出发城市/到达城市/日期/说明 | FK→main(CASCADE)
004 | fk_reim_subsidy | 补助汇总 | 天数/申请金额/实补/餐费/交通/通讯 | FK→main, trip(CASCADE)
005 | fk_reim_subsidy_day | 每日补助 | 日期/城市/标准/选中标记/实际金额 | FK→subsidy(CASCADE)
006 | fk_reim_allocation | 费用分摊 | 公司/项目/比例(DECIMAL 8,6)/金额 | FK→main(CASCADE)

### Table 10 rows=16 cols=13
001 | 序号 | 计划阶段 | 模块 | 任务名称 | 任务说明 | 负责人 | 协助人 | 开始时间 | 结束时间 | 工期(天) | 当前状态 | 质量要求 | 备注
002 | 1 | 后端开发 | 数据库 | 数据库设计与建库脚本 | 设计12张表，编写建库脚本及示例数据 | 陈昱桦 |  | 2026-05-23 | 2026-05-23 | 0.5 | 已完成 |  | 
003 | 2 | 后端开发 | 安全认证 | JWT安全框架与登录接口 | Spring Security + JWT + PBKDF2密码验证 | 陈昱桦 |  | 2026-05-23 | 2026-05-24 | 1 | 已完成 |  | 
004 | 3 | 后端开发 | 报销单 | 报销单数据结构设计 | Entity、DTO、VO定义及嵌套结构 | 陈昱桦 |  | 2026-05-24 | 2026-05-24 | 0.5 | 已完成 |  | 
005 | 4 | 后端开发 | 报销单 | 报销单草稿创建与编辑 | 草稿的创建、保存，含主表+行程+补助+分摊的原子写入 | 陈昱桦 |  | 2026-05-24 | 2026-05-25 | 1 | 已完成 |  | 
006 | 5 | 后端开发 | 报销单 | 补助计算与费用分摊 | 按城市类型逐日计算补助，分摊比例校验 | 陈昱桦 |  | 2026-05-25 | 2026-05-25 | 1 | 已完成 |  | 
007 | 6 | 后端开发 | 报销单 | 报销单状态流转 | 提交、审批、撤回、作废、复制、删除6种状态变更 | 陈昱桦 |  | 2026-05-25 | 2026-05-25 | 0.5 | 已完成 |  | 
008 | 7 | 后端开发 | 项目基础 | 项目初始化与环境配置 | 前后端项目骨架搭建，数据库初始化 | 徐文卓 |  | 2026-05-23 | 2026-05-23 | 0.5 | 已完成 |  | 
009 | 8 | 后端开发 | 主数据 | 主数据查询接口 | 公司/部门/员工/城市/项目/业务类型6个查询接口 | 徐文卓 |  | 2026-05-23 | 2026-05-24 | 1 | 已完成 |  | 
010 | 9 | 后端开发 | 公共组件 | 统一异常处理 | 全局异常拦截、业务异常定义、参数校验 | 徐文卓 |  | 2026-05-24 | 2026-05-24 | 0.5 | 已完成 |  | 
011 | 10 | 测试联调 | 联调 | 前后端联调测试 | 接口联调、流程测试、Bug修复 | 徐文卓 |  | 2026-05-25 | 2026-05-26 | 0.5 | 已完成 |  | 全员配合
012 | 11 | 文档 | 文档 | 接口与设计文档 | API文档、数据库文档、部署说明 | 徐文卓 |  | 2026-05-26 | 2026-05-26 | 0.5 | 已完成 |  | 
013 | 12 | 前端开发 | 项目基础 | 前端项目初始化 | Vue3项目骨架、路由、Axios封装、Store | 徐鹤文 |  | 2026-05-23 | 2026-05-23 | 0.5 | 已完成 |  | 
014 | 13 | 前端开发 | 登录 | 登录页与主布局 | 登录表单、Token管理、AppShell框架布局 | 徐鹤文 |  | 2026-05-23 | 2026-05-24 | 0.5 | 已完成 |  | 
015 | 14 | 前端开发 | 报销单 | 报销单列表页 | 表格展示、分页、多条件筛选 | 徐鹤文 |  | 2026-05-24 | 2026-05-25 | 1 | 已完成 |  | 
016 | 15 | 前端开发 | 报销单 | 报销单表单页 | 创建/编辑表单，含行程、补助、分摊子组件 | 徐鹤文 |  | 2026-05-25 | 2026-05-26 | 1 | 已完成 |  | 

### Table 11 rows=7 cols=2
001 | 项目 | 说明
002 | 接口名称 | 用户登录
003 | 请求方式 | POST /api/auth/login
004 | 认证要求 | 无需认证（SecurityConfig中permitAll）
005 | 请求参数 | JSON: {"username":"demo", "password":"Travel@123"}
006 | 成功响应 | {"code":0, "data":{"token":"eyJ...", "tokenType":"Bearer", "expiresInMinutes":480}}
007 | 失败响应 | {"code":401, "message":"用户名或密码错误"}

### Table 12 rows=4 cols=2
001 | 项目 | 说明
002 | 请求方式 | GET /api/reimbursements
003 | 请求参数 | current(页码), size(每页条数), reimNo, title, reason, companyId, departmentId, reimburserId, businessTypeId（均可选）
004 | 成功响应 | {"code":0, "data":{"total":14, "current":1, "size":10, "records":[...]}}

### Table 13 rows=3 cols=2
001 | 项目 | 说明
002 | 请求方式 | GET /api/reimbursements/{id}
003 | 成功响应 | {"code":0, "data":{...完整报销单（含行程/补助/分摊嵌套结构）}}

### Table 14 rows=4 cols=2
001 | 项目 | 说明
002 | 创建草稿 | POST /api/reimbursements/drafts
003 | 编辑草稿 | PUT /api/reimbursements/{id}/draft
004 | 请求体 | ReimbursementDraftSaveDTO:{reimbursementTitle, reimburserId, reimDepartmentId, reimCompanyId, businessTypeId, businessTripReason, remarks, trips:[{travelerId, departCityId, arriveCityId, departDate, arriveDate, tripDescription, subsidyDays:[{subsidyDate, mealSelected, mealAmount, ...}]}], allocations:[{companyId, projectId, allocationRatio, allocationAmount}]}

### Table 15 rows=7 cols=3
001 | 操作 | 请求 | 前置状态→目标状态
002 | 提交审批 | POST /api/reimbursements/{id}/submit | 草稿(0) → 审批中(3)
003 | 审批通过 | POST /api/reimbursements/{id}/approve | 审批中(3) → 审批通过(1)
004 | 撤回 | POST /api/reimbursements/{id}/withdraw | 审批中(3) → 草稿(0)
005 | 作废 | POST /api/reimbursements/{id}/void | 非作废(≠2) → 已作废(2)
006 | 复制 | POST /api/reimbursements/{id}/copy | 任意状态 → 新草稿(0)
007 | 删除 | DELETE /api/reimbursements/{id} | 草稿(0) → 删除

### Table 16 rows=7 cols=3
001 | 接口 | 路径 | 返回示例
002 | 公司列表 | GET /api/master/companies | [{id, companyNo, companyName}]
003 | 部门列表 | GET /api/master/departments | [{id, departmentNo, departmentName}]
004 | 员工列表 | GET /api/master/employees | [{id, employeeNo, employeeName, deptId, companyId}]
005 | 城市列表 | GET /api/master/cities | [{id, cityNo, cityName, cityType}]
006 | 项目列表 | GET /api/master/projects | [{id, projectNo, projectName}]
007 | 业务类型树 | GET /api/master/business-types/tree | [{id, no, name, parentId, leafFlag, children:[...]}]

### Table 17 rows=4 cols=3
001 | 字段 | 类型 | 说明
002 | code | int | 业务状态码：0=成功，其他见错误码表
003 | message | string | 提示信息，成功时为"success"
004 | data | T（泛型） | 响应数据体，失败时为null