# 航空订票平台 - 后端 (Flight API)

## 项目概述

航空订票平台后端(Flight API)是一个基于 Spring Boot 的 RESTful API 服务，为航空订票平台提供核心业务功能支持。该系统允许用户搜索航班、比较票价、选择航班、下单并完成预订。系统支持用户注册、登录、航班查询、订单管理等功能，为前端应用提供完整的数据服务和业务逻辑处理。

### 核心价值

- **高效航班搜索**：提供快速、精准的航班搜索功能
- **安全用户认证**：基于 JWT 的安全认证机制
- **完整订单流程**：支持从航班选择到订单确认的完整业务流程
- **RESTful API 设计**：符合 REST 规范的 API 设计，便于前端集成
- **可扩展架构**：模块化设计，便于功能扩展和维护

## 项目架构

### 系统架构图

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│                 │      │                 │      │                 │
│  前端应用        │ ──── │  Flight API     │ ──── │  MySQL数据库    │
│  (React)        │      │  (Spring Boot)  │      │                 │
│                 │      │                 │      │                 │
└─────────────────┘      └─────────────────┘      └─────────────────┘
                                  │
                                  │
                         ┌────────┴────────┐
                         │                 │
                         │  AWS云服务       │
                         │  (部署环境)      │
                         │                 │
                         └─────────────────┘
```

### 业务流程图

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│         │     │         │     │         │     │         │     │         │
│ 航班搜索 │ ──> │ 航班列表│ ──> │ 航班选择 │ ──> │ 订单确认│ ──> │ 订单完成 │
│         │     │         │     │         │     │         │     │         │
└─────────┘     └─────────┘     └─────────┘     └─────────┘     └─────────┘
```

### 系统组件图

```
┌───────────────────────────────────────────────────────────────┐
│                        Flight API                             │
│                                                               │
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐          │
│  │             │   │             │   │             │          │
│  │ Controllers │──>│  Services   │──>│ Repositories│──>┐      │
│  │             │   │             │   │             │   │      │
│  └─────────────┘   └─────────────┘   └─────────────┘   │      │
│         │                                              │      │
│         │          ┌─────────────┐                     │      │
│         └─────────>│    DTOs     │                     │      │
│                    │             │                     │      │
│                    └─────────────┘                     │      │
│                                                        ▼      │
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────────────┐  │
│  │             │   │             │   │                     │  │
│  │  Security   │   │  Exception  │   │      Database       │  │
│  │             │   │  Handling   │   │                     │  │
│  └─────────────┘   └─────────────┘   └─────────────────────┘  │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

## 技术栈详解

### 后端框架

- **Spring Boot 3.3.13**：提供快速开发框架，简化配置
  - 选择理由：成熟稳定、开发效率高、社区活跃
- **Spring Security**：提供认证和授权功能
  - 选择理由：与 Spring Boot 无缝集成、安全性高
- **Spring Data JPA**：简化数据库访问
  - 选择理由：减少样板代码、提高开发效率

### 数据库

- **MySQL 8.x**：关系型数据库
  - 选择理由：稳定可靠、性能优良、广泛应用

### 认证与安全

- **JWT (JSON Web Token)**：基于令牌的认证机制
  - 选择理由：无状态、可扩展、适合微服务架构
- **CORS 配置**：支持跨域资源共享
  - 选择理由：允许前端安全访问 API

### 开发工具与库

- **Lombok**：减少样板代码
  - 选择理由：提高开发效率、代码简洁
- **MapStruct 1.5.5**：对象映射工具
  - 选择理由：类型安全、高性能、编译时检查
- **Swagger/OpenAPI 2.5.0**：API 文档生成
  - 选择理由：自动生成文档、交互式测试界面

### 监控与管理

- **Spring Boot Actuator**：应用监控和管理
  - 选择理由：提供健康检查、指标收集、运行时信息

### 前端框架（集成）

- **React**：用户界面库
  - 选择理由：组件化开发、虚拟 DOM、性能优良

## 环境要求

### 开发环境

- **JDK 21**：Java 开发工具包
- **Maven 3.8+**：项目管理工具
- **MySQL 8.x**：数据库服务
- **IDE**：VS Code
- **Git**：版本控制

### 运行环境

- **操作系统**：支持 Windows、Linux、macOS
- **JRE 21**：Java 运行环境
- **内存**：最低 4GB，推荐 8GB 以上
- **存储**：最低 500MB 可用空间
- **网络**：支持 HTTP/HTTPS 协议

### 依赖配置

所有依赖已在`pom.xml`中配置，主要包括：

- Spring Boot Starter (Web, Data JPA, Security)
- MySQL Connector
- JWT 相关库
- Lombok
- MapStruct
- Swagger/OpenAPI
- Validation API

## 快速开始

### 环境准备

1. 安装 JDK 21

   ```bash
   # 检查Java版本
   java -version
   ```

2. 安装 Maven

   ```bash
   # 检查Maven版本
   mvn -version
   ```

3. 安装 MySQL 并创建数据库
   ```sql
   CREATE DATABASE flight_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

### 获取代码

```bash
# 克隆代码仓库
git clone https://github.com/luozhigang2012/flight-api.git
cd flight-api
```

### 配置应用

1. 修改`src/main/resources/application.yml`中的数据库配置：

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/flight_db?useSSL=false&serverTimezone=UTC
       username: your_username
       password: your_password
   ```

2. 配置 JWT 密钥（生产环境必须修改）：
   ```yaml
   jwt:
     secret: your_secure_jwt_secret_key_here
   ```

### 构建与运行

```bash
# 使用Maven构建项目
mvn clean package

# 运行应用
java -jar target/flight-api-0.0.1-SNAPSHOT.jar
```

或者使用 Maven 直接运行：

```bash
mvn spring-boot:run
```

### 验证安装

访问以下 URL 确认应用正常运行：

- API 基础 URL: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- 健康检查: http://localhost:8080/actuator/health

## API 文档

完整的 API 文档可通过 Swagger UI 访问：http://localhost:8080/swagger-ui/index.html

### 主要 API 端点

#### 认证相关

| 方法 | URL                | 描述         | 参数                          | 返回值              | 状态码        |
| ---- | ------------------ | ------------ | ----------------------------- | ------------------- | ------------- |
| POST | /api/auth/register | 用户注册     | RegisterRequestDTO (body)     | AuthResponseDTO     | 201, 400      |
| POST | /api/auth/login    | 用户登录     | LoginRequestDTO (body)        | AuthResponseDTO     | 200, 401      |
| GET  | /api/auth/check    | 检查登录状态 | Authorization (header)        | {loggedIn: boolean} | 200           |
| POST | /api/auth/logout   | 用户注销     | Authorization (header)        | void                | 200, 401      |
| POST | /api/auth/refresh  | 刷新令牌     | {refreshToken: string} (body) | AuthResponseDTO     | 200, 400, 401 |

#### 航班相关

| 方法 | URL               | 描述         | 参数                               | 返回值                                | 状态码   |
| ---- | ----------------- | ------------ | ---------------------------------- | ------------------------------------- | -------- |
| GET  | /api/flights      | 搜索航班     | from, to, date, page, size (query) | PagedResponseDTO\<FlightResponseDTO\> | 200, 404 |
| GET  | /api/flights/{id} | 获取航班详情 | id (path)                          | FlightResponseDTO                     | 200, 404 |

#### 机场相关

| 方法 | URL           | 描述         | 参数 | 返回值                     | 状态码 |
| ---- | ------------- | ------------ | ---- | -------------------------- | ------ |
| GET  | /api/airports | 获取所有机场 | 无   | List\<AirportResponseDTO\> | 200    |

#### 订单相关

| 方法 | URL                | 描述             | 参数                                                                 | 返回值                                 | 状态码        |
| ---- | ------------------ | ---------------- | -------------------------------------------------------------------- | -------------------------------------- | ------------- |
| POST | /api/bookings      | 创建订单         | BookingRequestDTO (body), Authorization (header)                     | BookingResponseDTO                     | 201, 400, 401 |
| GET  | /api/bookings      | 获取用户订单列表 | status (query, optional), page, size (query), Authorization (header) | PagedResponseDTO\<BookingResponseDTO\> | 200, 401, 404 |
| GET  | /api/bookings/{id} | 获取订单详情     | id (path), Authorization (header)                                    | BookingResponseDTO                     | 200, 401, 404 |

### 请求/响应示例

#### 用户注册请求

```json
POST /api/auth/register
{
    "email": "test@example.com",
    "password": "Password123",
    "firstName": "Test",
    "lastName": "User",
    "country": "CN",
    "phone": "1234567890"
}
```

#### 用户注册响应

```json
{
  "success": true,
  "code": 201,
  "message": "Registration successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "accessTokenExpiresIn": 1800000,
    "userInfo": {
      "id": 1,
      "email": "test@example.com",
      "firstName": "Test",
      "lastName": "User"
    }
  }
}
```

#### 航班搜索请求

```
GET /api/flights?from=PEK&to=PVG&date=2025-06-16
```

#### 航班搜索响应

```json
{
  "success": true,
  "code": 200,
  "message": "Flights retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "flightNumber": "CA1501",
        "departure": "PEK - Beijing Capital International Airport, Beijing",
        "destination": "PVG - Shanghai Pudong International Airport, Shanghai",
        "departureDate": "2025-06-16",
        "departureTime": "08:30:00",
        "price": "1350.00"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

## 数据模型

### 实体关系图

```
┌─────────┐       ┌─────────┐       ┌─────────┐
│         │       │         │       │         │
│  User   │──┐    │ Flight  │<──────│ Airport │
│         │  │    │         │       │         │
└─────────┘  │    └─────────┘       └─────────┘
             │        │
             │        │
             │        │
             │    ┌───▼─────┐
             └───>│         │
                  │ Booking │
                  │         │
                  └─────────┘
                      │
                      │
                      │
                  ┌───▼─────┐
                  │         │
                  │Passenger│
                  │         │
                  └─────────┘
```

## 部署指南

### Docker 部署

#### 准备 Dockerfile

项目根目录下 Dockerfile：

```dockerfile
FROM openjdk:21-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 构建 Docker 镜像

```bash
# 构建应用
mvn clean package

# 构建Docker镜像
docker build -t flight-api:latest .
```

#### 本地运行 Docker 容器

```bash
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/flight_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=123456 \
  -e JWT_SECRET=your_secure_jwt_secret \
  --name flight-api flight-api:latest
```

### AWS 部署流程

#### 1. 准备 AWS 环境

- 创建 AWS 账户并设置 IAM 权限
- 安装并配置 AWS CLI

#### 2. 创建 ECR 仓库

```bash
# 创建ECR仓库
aws ecr create-repository --repository-name flight-api

# 登录ECR
aws ecr get-login-password --region <your-region> | docker login --username AWS --password-stdin <your-account-id>.dkr.ecr.<your-region>.amazonaws.com
```

#### 3. 推送镜像到 ECR

```bash
# 标记镜像
docker tag flight-api:latest <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/flight-api:latest

# 推送镜像
docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/flight-api:latest
```

#### 4. 创建 RDS 数据库

1. 在 AWS 控制台创建 MySQL RDS 实例
2. 配置安全组允许 ECS 服务访问
3. 创建数据库和用户

#### 5. 配置 ECS 服务

1. 创建任务定义：

   - 使用 ECR 镜像
   - 配置环境变量（数据库连接、JWT 密钥等）
   - 设置内存和 CPU 限制

2. 创建 ECS 集群和服务：
   - 选择 Fargate 或 EC2 启动类型
   - 配置负载均衡器
   - 设置自动扩展策略

#### 6. 配置负载均衡器

1. 创建 Application Load Balancer
2. 配置目标组和健康检查
3. 设置 HTTPS 证书（推荐）

#### 7. 设置 CI/CD 流水线

使用 GitHub Actions 自动化部署流程：

```yaml
name: Flight API CI/CD

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: "21"
          distribution: "temurin"
          cache: maven

      - name: Build with Maven
        run: mvn -B package --file pom.xml

      - name: Run tests
        run: mvn test

      - name: Build and push Docker image
        if: github.event_name == 'push' && github.ref == 'refs/heads/main'
        env:
          AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
          AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          AWS_REGION: ${{ secrets.AWS_REGION }}
          ECR_REPOSITORY: flight-api
        run: |
          aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin ${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.$AWS_REGION.amazonaws.com
          docker build -t ${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:${{ github.sha }} .
          docker push ${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:${{ github.sha }}

      - name: Deploy to ECS
        if: github.event_name == 'push' && github.ref == 'refs/heads/main'
        env:
          AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
          AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          AWS_REGION: ${{ secrets.AWS_REGION }}
        run: |
          aws ecs update-service --cluster flight-api-cluster --service flight-api-service --force-new-deployment
```

## 贡献指南

### 代码规范

- **Java 代码风格**：遵循 Google Java Style Guide
- **提交信息格式**：

  ```
  <type>(<scope>): <subject>

  <body>

  <footer>
  ```

  其中 type 可以是：feat, fix, docs, style, refactor, test, chore

- **分支命名规范**：
  - `feature/xxx`：新功能
  - `bugfix/xxx`：Bug 修复
  - `hotfix/xxx`：紧急修复
  - `release/xxx`：发布准备

### 开发流程

1. Fork 项目仓库
2. 创建功能分支
3. 提交代码变更
4. 确保测试通过
5. 提交 Pull Request

### PR 流程

1. PR 标题清晰描述变更内容
2. 详细描述变更的目的和实现方式
3. 关联相关 Issue
4. 等待代码审查
5. 根据反馈进行修改
6. 合并到主分支

### 代码审查标准

- 代码质量：遵循最佳实践，无明显缺陷
- 测试覆盖：新功能必须有单元测试
- 文档完整：必要的注释和文档更新
- 性能考虑：不引入明显的性能问题

## 许可证信息

本项目采用 MIT 许可证。详情请参阅[LICENSE](LICENSE)文件。

```
MIT License

Copyright (c) 2025 Your Organization

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
