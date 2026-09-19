# ZhuaTech AIPS｜AI生产排程优化系统

> 把订单需求、产能、物料、换型、维护与交期约束转化为可执行生产计划

ZhuaTech AIPS 是知华科技（上海如静知华信息科技有限公司）发布的企业级源码项目，面向“需求池、主生产计划、有限产能、物料齐套、瓶颈、换型、维护窗口、排程仿真与下达”提供管理端与响应式业务端。工程采用前后端分离架构，所有示例数据均为虚构数据。

[知华科技官网](https://www.zhuatech.cn/) · [架构说明](docs/ARCHITECTURE.md) · [API 文档](docs/API.md) · [企业能力](docs/ENTERPRISE.md) · [测试说明](docs/TESTING.md)

![AI生产排程优化系统产品界面示意](docs/images/product-overview.svg)

## 业务模块

| 模块 | 核心能力 |
| --- | --- |
| 需求池 | 归集销售订单、预测、安全库存和优先级 |
| 有限产能 | 维护设备、班次、人员、模具和日历产能 |
| 物料齐套 | 计算在库、在途、替代料和短缺时间 |
| 约束建模 | 配置前后置、批量、换型、冻结窗和维护约束 |
| AI排程优化 | 按交期、在制品、换型和成本生成候选方案 |
| 情景仿真 | 比较插单、设备停机、加班和外协情景 |
| 瓶颈管理 | 识别超负荷工作中心并给出处置动作 |
| 计划下达 | 经计划员确认后下达 MES 并冻结版本 |
| 执行反馈 | 跟踪达成率、延误、重排和模型偏差 |

![AI生产排程优化系统业务闭环](docs/images/workflow.svg)

## 企业级控制

- 新增有限产能排程引擎：按优先级、交期和设备适配分配工作中心，计算利用率、延期分钟与未排产原因；
- ADMIN / OPERATOR 角色边界和管理员接口隔离；
- 服务端字段、模块、唯一编号和状态迁移校验；
- 组织、期间、责任人、风险等级、到期日和 SLA 统计；
- 幂等创建、JPA 乐观锁、重复提交保护和职责分离；
- 附件 SHA-256 元数据、业务凭证完整性与全流程审计；
- 组合检索、分页、逾期筛选、UTF-8 CSV 导出和协作时间线；
- 外部系统仅预留适配器，使用方自行配置地址与凭据；
- prod profile 拒绝默认密码、弱数据库口令和本地跨域来源。

## 技术架构

- 后端：Java 21、Spring Boot、Spring Security、JPA、Bean Validation、Actuator
- 前端：Vue 3、Vite、Axios，支持桌面端与移动端响应式布局
- 数据库：MySQL 8；自动化测试使用 H2
- 交付：Docker Compose、Nginx、环境变量、GitHub Actions
- Java 包名：`cn.zhuatech.aiproductionscheduler`

## 启动与测试

```bash
cd backend && mvn test
cd ../frontend && npm install && npm run build
cd .. && cp .env.example .env && docker compose up --build
```

开发演示账号：`admin / admin123`、`operator / operator123`。生产环境必须通过环境变量替换全部默认凭据。

## 许可与商业授权

Copyright © 2026 上海如静知华信息科技有限公司。

本工程仅允许个人学习、研究和非商业技术交流，**不得用于商业用途**。企业内部使用、生产部署、SaaS运营、项目交付、品牌替换、收费培训、咨询实施或再分发，均须事先获得上海如静知华信息科技有限公司书面授权，详见 [LICENSE](LICENSE)。

深度开发、私有化部署、系统集成与企业数字化咨询，请访问[知华科技官网](https://www.zhuatech.cn/)或扫码联系：

| 微信咨询一 | 微信咨询二 |
| --- | --- |
| ![微信咨询二维码一](docs/images/zhuatech-wechat-consulting.png) | ![微信咨询二维码二](docs/images/zhuatech-wechat-consulting-2.png) |

SEO：AI生产排程优化系统、AIPS系统源码、企业数字化、Java企业系统、Vue管理系统、知华科技、上海如静知华信息科技有限公司。
