# OA 员工管理系统

后端使用 Spring Boot 3.4.5、Java 21、MyBatis 和 MySQL；前端是 Vue 3 + Vite，默认由 Vite 将 `/api` 请求代理到后端。

## 本地启动

1. 创建数据库：在 MySQL 8 中执行项目根目录的 `oa.sql`。脚本可重复执行，不会删除已有数据。演示账号为 `admin / 123456` 和 `zhangsan / 123456`。
2. 启动后端：在项目根目录执行 `mvnw.cmd spring-boot:run`。默认监听 `8083`，开发数据库默认使用 `root / 123456`；可通过 `DB_URL`、`DB_USERNAME` 和 `DB_PASSWORD` 覆盖。
3. 配置真实 AI（可选）：设置 `DASHSCOPE_ENABLED=true`、`DASHSCOPE_API_KEY` 和 `SPRING_AI_MODEL_CHAT=dashscope`。模型使用通义千问 `qwen-plus`；未启用 DashScope 或模型调用失败时，AI 对话会退回本地规则回复。
4. 启动前端：从[前端仓库](https://github.com/qaq666-u/oa-ai-employee-management-frontend)获取源码后，在前端项目目录执行 `npm install` 和 `npm run dev`，访问 `http://localhost:8082`。登录态通过 Session Cookie 保存。

头像上传默认保存在运行目录下的 `uploads` 文件夹。可以用 `OA_UPLOAD_DIR` 指定外部目录，用 `OA_BASE_URL` 指定图片访问地址。

## 接口

- 用户：`POST /user/login`、`POST /user/logout`、`GET /user/info`、`PUT /user/password`、`POST /user/avatar`
- 员工：分页查询、详情、新增、修改、单条/批量删除和状态更新，路径前缀为 `/emp`
- 部门：分页查询、列表、新增、修改和删除，路径前缀为 `/dept`
- 职位：分页查询、列表、新增、修改和删除，路径前缀为 `/job`
- AI：`POST /ai/chat`、`GET /ai/history`、`DELETE /ai/history`

业务错误以统一 `Result` 返回。登录校验失败使用 HTTP 401，普通员工访问管理接口使用 HTTP 403。

## 打包部署

使用 JDK 21 执行 `mvnw.cmd -DskipTests package`，然后用 `java -jar target/oa-back-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod` 启动。生产环境应设置 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`、`OA_UPLOAD_DIR` 和 `OA_BASE_URL`；若启用 AI，再设置 `DASHSCOPE_ENABLED=true`、`DASHSCOPE_API_KEY` 和 `SPRING_AI_MODEL_CHAT=dashscope`。日志文件位置可通过 `OA_LOG_FILE` 配置。
