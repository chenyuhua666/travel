INSERT INTO sys_company (id, company_no, company_name) VALUES
    (1, '0407', '胜意科技北京分公司'),
    (2, '0408', '胜意科技上海分公司'),
    (3, '0409', '胜意科技武汉分公司'),
    (4, '0410', '胜意科技杭州分公司'),
    (5, '0411', '胜意科技荆州分公司');

INSERT INTO sys_department (id, department_no, department_name) VALUES
    (1, '072001', '客户成功事业部'),
    (2, '072002', '企业消费事业部'),
    (3, '072003', '企业费控事业部'),
    (4, '072004', '集采事业部'),
    (5, '072005', '航旅事业部'),
    (6, '072006', '运营事业部'),
    (7, '072007', '营销事业部');

INSERT INTO sys_employee (id, employee_no, employee_name, department_id, company_id) VALUES
    (1, '74541', '徐年年', 1, 3),
    (2, '74008', '郑雨雪', 2, 1),
    (3, '21552', '邹薇', 3, 2),
    (4, '80681', '王成军', 4, 4),
    (5, '89899', '潘展飞', 5, 5),
    (6, '10503', '姜林', 6, 3);

INSERT INTO sys_user (id, username, password_hash, employee_id, roles, enabled) VALUES
    (1, 'demo', 'pbkdf2_sha256$120000$dHJhdmVsLWRlbW8tc2FsdA$BzGndNJU5PsaMrZCSxRwfdQmgpHt-yiql6-hNHFK5eQ', 1, 'EMPLOYEE', 1);

INSERT INTO biz_business_type (id, business_type_no, business_type_name, parent_id, leaf_flag) VALUES
    (1, '1001001', '员工差旅活动', NULL, 0),
    (2, '100100101', '境内出差', 1, 0),
    (3, '10010010101', '项目出差', 2, 1),
    (4, '10010010102', '市场拓展出差', 2, 1),
    (5, '100100102', '境外出差', 1, 0),
    (6, '10010010201', '国外考察', 5, 1),
    (7, '10010010202', '售后维护出差', 5, 1),
    (8, '1001002', '人力资源', NULL, 0),
    (9, '100100201', '个人团队培训', 8, 1),
    (10, '100100202', '招聘会', 8, 1),
    (11, '1001003', '员工福利', NULL, 0),
    (12, '100100301', '员工旅游', 11, 1),
    (13, '100100302', '员工团建', 11, 1),
    (14, '100100303', '员工体检', 11, 1);

INSERT INTO biz_city (id, city_no, city_name, city_type) VALUES
    (1, '10119', '北京', 1),
    (2, '10621', '上海', 1),
    (3, '10458', '武汉', 2),
    (4, '10216', '杭州', 2),
    (5, '10455', '荆州', 3);

INSERT INTO biz_project (id, project_no, project_name) VALUES
    (1, 'nonProjectRelated', '非项目类费用归集'),
    (2, 'centralChina', '华中客户定制化项目'),
    (3, 'southChina', '华南客户定制化项目'),
    (4, 'northChina', '华北客户定制化项目'),
    (5, 'eastChina', '华东客户定制化项目'),
    (6, 'southWest', '西南客户定制化项目'),
    (7, 'northWest', '西北客户定制化项目'),
    (8, 'northEast', '东北客户定制化项目');
