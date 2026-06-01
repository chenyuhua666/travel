CREATE TABLE IF NOT EXISTS sys_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_no VARCHAR(32) NOT NULL,
    company_name VARCHAR(128) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sys_company_no (company_no)
);

CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_no VARCHAR(32) NOT NULL,
    department_name VARCHAR(128) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sys_department_no (department_no)
);

CREATE TABLE IF NOT EXISTS sys_employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_no VARCHAR(32) NOT NULL,
    employee_name VARCHAR(64) NOT NULL,
    department_id BIGINT NULL,
    company_id BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sys_employee_no (employee_no),
    KEY idx_sys_employee_department (department_id),
    KEY idx_sys_employee_company (company_id),
    CONSTRAINT fk_sys_employee_department FOREIGN KEY (department_id) REFERENCES sys_department (id),
    CONSTRAINT fk_sys_employee_company FOREIGN KEY (company_id) REFERENCES sys_company (id)
);

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    employee_id BIGINT NOT NULL,
    roles VARCHAR(255) NOT NULL DEFAULT 'EMPLOYEE',
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_employee (employee_id),
    CONSTRAINT fk_sys_user_employee FOREIGN KEY (employee_id) REFERENCES sys_employee (id)
);

CREATE TABLE IF NOT EXISTS biz_business_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_type_no VARCHAR(32) NOT NULL,
    business_type_name VARCHAR(128) NOT NULL,
    parent_id BIGINT NULL,
    leaf_flag TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_biz_business_type_no (business_type_no),
    KEY idx_biz_business_type_parent (parent_id),
    CONSTRAINT fk_biz_business_type_parent FOREIGN KEY (parent_id) REFERENCES biz_business_type (id)
);

CREATE TABLE IF NOT EXISTS biz_city (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    city_no VARCHAR(32) NOT NULL,
    city_name VARCHAR(64) NOT NULL,
    city_type TINYINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_biz_city_no (city_no)
);

CREATE TABLE IF NOT EXISTS biz_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_no VARCHAR(64) NOT NULL,
    project_name VARCHAR(128) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_biz_project_no (project_no)
);

CREATE TABLE IF NOT EXISTS fk_reim_main (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reim_no VARCHAR(32) NULL,
    reimbursement_title VARCHAR(500) NULL,
    reimburser_id BIGINT NULL,
    reimburser_no VARCHAR(32) NULL,
    reimburser_name VARCHAR(64) NULL,
    reim_department_id BIGINT NULL,
    reim_department_no VARCHAR(32) NULL,
    reim_department_name VARCHAR(128) NULL,
    reim_company_id BIGINT NULL,
    reim_company_no VARCHAR(32) NULL,
    reim_company_name VARCHAR(128) NULL,
    business_type_id BIGINT NULL,
    business_type_no VARCHAR(32) NULL,
    business_type_name VARCHAR(128) NULL,
    business_trip_reason VARCHAR(500) NULL,
    subsidy_total DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    meal_allowance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    transportation_allowance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    phone_allowance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    remarks VARCHAR(1000) NULL,
    status TINYINT NOT NULL DEFAULT 0,
    owner_user_id BIGINT NOT NULL,
    version INT NOT NULL DEFAULT 0,
    creation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_fk_reim_main_no (reim_no),
    KEY idx_fk_reim_main_status_time (status, creation_time),
    KEY idx_fk_reim_main_reimburser (reimburser_id),
    KEY idx_fk_reim_main_company (reim_company_id),
    KEY idx_fk_reim_main_owner (owner_user_id),
    CONSTRAINT fk_fk_reim_main_owner FOREIGN KEY (owner_user_id) REFERENCES sys_user (id)
);

CREATE TABLE IF NOT EXISTS fk_reim_trip (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    main_id BIGINT NOT NULL,
    traveler_id BIGINT NOT NULL,
    traveler_no VARCHAR(32) NOT NULL,
    traveler_name VARCHAR(64) NOT NULL,
    depart_city_id BIGINT NOT NULL,
    depart_city_no VARCHAR(32) NOT NULL,
    depart_city_name VARCHAR(64) NOT NULL,
    arrive_city_id BIGINT NOT NULL,
    arrive_city_no VARCHAR(32) NOT NULL,
    arrive_city_name VARCHAR(64) NOT NULL,
    depart_date DATE NOT NULL,
    arrive_date DATE NOT NULL,
    trip_description VARCHAR(500) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_fk_reim_trip_main (main_id),
    KEY idx_fk_reim_trip_traveler_dates (traveler_id, depart_date, arrive_date),
    CONSTRAINT fk_fk_reim_trip_main FOREIGN KEY (main_id) REFERENCES fk_reim_main (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fk_reim_subsidy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    main_id BIGINT NOT NULL,
    trip_id BIGINT NOT NULL,
    subsidy_days INT NOT NULL DEFAULT 0,
    apply_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    subsidy_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    meal_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    transportation_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    phone_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_fk_reim_subsidy_trip (trip_id),
    KEY idx_fk_reim_subsidy_main (main_id),
    CONSTRAINT fk_fk_reim_subsidy_main FOREIGN KEY (main_id) REFERENCES fk_reim_main (id) ON DELETE CASCADE,
    CONSTRAINT fk_fk_reim_subsidy_trip FOREIGN KEY (trip_id) REFERENCES fk_reim_trip (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fk_reim_subsidy_day (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    subsidy_id BIGINT NOT NULL,
    subsidy_date DATE NOT NULL,
    weekday_name VARCHAR(16) NOT NULL,
    city_id BIGINT NOT NULL,
    city_name VARCHAR(64) NOT NULL,
    meal_standard_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    transportation_standard_amount DECIMAL(18,2) NOT NULL DEFAULT 40.00,
    phone_standard_amount DECIMAL(18,2) NOT NULL DEFAULT 40.00,
    meal_selected TINYINT NOT NULL DEFAULT 0,
    transportation_selected TINYINT NOT NULL DEFAULT 0,
    phone_selected TINYINT NOT NULL DEFAULT 0,
    meal_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    transportation_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    phone_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_fk_reim_subsidy_day_date (subsidy_id, subsidy_date),
    KEY idx_fk_reim_subsidy_day_city (city_id),
    CONSTRAINT fk_fk_reim_subsidy_day_subsidy FOREIGN KEY (subsidy_id) REFERENCES fk_reim_subsidy (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fk_reim_allocation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    main_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    company_no VARCHAR(32) NOT NULL,
    company_name VARCHAR(128) NOT NULL,
    project_id BIGINT NULL,
    project_no VARCHAR(64) NULL,
    project_name VARCHAR(128) NULL,
    allocation_ratio DECIMAL(8,6) NOT NULL DEFAULT 0.000000,
    allocation_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    row_order INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_fk_reim_allocation_main (main_id),
    CONSTRAINT fk_fk_reim_allocation_main FOREIGN KEY (main_id) REFERENCES fk_reim_main (id) ON DELETE CASCADE
);
