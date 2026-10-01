-- Initial schema and demo data for the OA employee management system.
-- Safe to re-run: existing tables and records are not dropped or overwritten.
CREATE DATABASE IF NOT EXISTS oa_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE oa_db;

CREATE TABLE IF NOT EXISTS dept (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(255) DEFAULT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Departments';

CREATE TABLE IF NOT EXISTS job (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  sort INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Job titles';

CREATE TABLE IF NOT EXISTS emp (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  emp_no VARCHAR(20) NOT NULL,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(64) NOT NULL,
  name VARCHAR(50) NOT NULL,
  gender TINYINT NOT NULL DEFAULT 1 COMMENT '1 male, 0 female',
  phone VARCHAR(20) DEFAULT NULL,
  email VARCHAR(100) DEFAULT NULL,
  avatar VARCHAR(500) DEFAULT NULL,
  dept_id BIGINT DEFAULT NULL,
  job_id BIGINT DEFAULT NULL,
  hire_date DATE DEFAULT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'employee',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 active, 0 disabled',
  UNIQUE KEY uk_emp_no (emp_no),
  UNIQUE KEY uk_emp_username (username),
  KEY idx_emp_name (name),
  KEY idx_emp_dept (dept_id),
  KEY idx_emp_job (job_id),
  CONSTRAINT fk_emp_dept FOREIGN KEY (dept_id) REFERENCES dept(id),
  CONSTRAINT fk_emp_job FOREIGN KEY (job_id) REFERENCES job(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Employees';

INSERT IGNORE INTO dept (name, description) VALUES
  ('技术部', '负责产品研发与技术支持'),
  ('财务部', '负责财务核算与资金管理'),
  ('运营部', '负责产品运营与客户服务'),
  ('人事部', '负责招聘、考勤与员工关系'),
  ('市场部', '负责市场推广与品牌运营'),
  ('行政部', '负责行政后勤与办公支持');

INSERT IGNORE INTO job (name, sort) VALUES
  ('前端工程师', 1), ('后端工程师', 2), ('测试工程师', 3),
  ('产品经理', 4), ('UI设计师', 5), ('人事专员', 6),
  ('财务专员', 7), ('市场专员', 8);

-- Demo administrator and staff accounts all use the initial password 123456.
-- Its stored MD5 digest matches Md5Util.md5("123456").
INSERT IGNORE INTO emp
  (emp_no, username, password, name, gender, phone, email, dept_id, job_id, hire_date, role, status)
VALUES
  ('EMP001', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 1, '13800000001', 'admin@oa.com',
   (SELECT id FROM dept WHERE name='技术部'), (SELECT id FROM job WHERE name='后端工程师'), '2022-01-01', 'admin', 1),
  ('EMP002', 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 1, '13800000002', 'zhangsan@oa.com',
   (SELECT id FROM dept WHERE name='技术部'), (SELECT id FROM job WHERE name='前端工程师'), '2023-07-01', 'employee', 1),
  ('EMP003', 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李娜', 0, '13800000003', 'lina@oa.com',
   (SELECT id FROM dept WHERE name='财务部'), (SELECT id FROM job WHERE name='财务专员'), '2023-04-12', 'employee', 1);
