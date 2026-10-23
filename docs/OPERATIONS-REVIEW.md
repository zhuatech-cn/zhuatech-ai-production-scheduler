# 良率与换型损耗产能测算

上海如静知华信息科技有限公司 · https://www.zhuatech.cn/

商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。

## 功能与实际边界

在有限产能排程之外，按良率向上折算投产量，加入换型时间并计算产能缺口。

沿用现有Java/Vue架构和认证。新增类位于`cn.zhuatech.aiproductionscheduler.operations`，只读算法与接口分层。核心逐条算法在`OperationsReviewService.evaluate`，字段契约在`src/main/resources/operations/schema.json`，业务边界算例在`src/test/resources/operations/cases.json`。没有新增演示账号、固定新密码、业务表或Flyway修改；不触发模型、网络和业务写入。

## 操作

进入`/operations.html`（首页有入口），输入现有企业账号密码加载工作台；生产环境仅通过HTTPS传输凭据。载入的样例是虚构输入，不是数据库演示数据。可新增/删除记录、修改核对日期、执行核算并导出CSV或完整JSON。页面最多100条，结果分页50条；后端可接收500条。

所有字段必填。数字字段以十进制字符串提交，不能使用科学计数法、负数或千分位；JSON数字也可由后端精确解析。输入范围与小数位见下表。所有金额统一为人民币元，不进行币种转换；数量与计量单位不可跨口径混合。日期严格为1900–2200年的ISO日期。编号80位以内，本批次不区分大小写去重。

| 字段 | 名称/单位 | 类型 | 范围 |
| --- | --- | --- | --- |
| id | 记录编号 | ID | 必填 |
| quantity | 合格需求量 | NUMBER | 0–1000000；小数位≤0 |
| cycleMinutes | 单件加工分钟 | NUMBER | 0–1000000000000；小数位≤6 |
| setupMinutes | 换型分钟 | NUMBER | 0–1000000000000；小数位≤6 |
| availableMinutes | 可用分钟 | NUMBER | 0.000001–1000000000000；小数位≤6 |
| yieldPct | 预计良率% | NUMBER | 0–100；小数位≤4 |
| dueDate | 需求日期 | DATE | 必填 |

核算结果按`HOLD`（暂不满足）、`REVIEW`（需复核）、`PASS`（条件满足）及编号稳定排序。每条结果包含明确核对依据。**PASS不是批准，不是实际执行授权**。任何一条输入不合法都会返回400并拒绝整批，不跳过问题行。报告不可缓存；浏览器内容不新增持久存储，改输入后旧报告立即失效。

## 接口

- `GET /api/operations-review/schema`：获取本项目字段、算法说明、输出列和虚构输入样例，必须认证。
- `POST /api/operations-review/evaluate`：必须认证，复用原Bearer/Basic体系，请求仅允许`asOf`、`records`。最大256KiB、500条、层级8、15000节点。字段和节点上限可能先于记录数命中。

```json
{
  "asOf": "2026-10-23",
  "records": [
    {
      "id": "ITEM-001",
      "quantity": "100",
      "cycleMinutes": "2",
      "setupMinutes": "30",
      "availableMinutes": "300",
      "yieldPct": "100",
      "dueDate": "2026-11-01"
    }
  ]
}
```

响应`data`含`recordCount/holdCount/reviewCount/passCount/results/automaticWrite`，其中`automaticWrite=false`。每条结果都有`id/decision/reason`及专项指标，数值结果以字符串返回以避免JavaScript浮点误差。缺少或失效身份401；结构、字段、数量关系或资源限制错误400；成功核算200，业务条件不满足也返回200和HOLD而非悄悄略过。

## 安全和交付注意

输入必须由调用方合法取得；接口不读取其他用户或供应商的数据，也不替代原数据权限。重要执行操作必须继续在原业务服务内做身份、角色、数据范围和并发事务校验；此工具不授予放行权限，不新增业务变更审计。费用、证据和审批状态是调用方输入声明，不是本工具已从数据库核实的事实。原系统其他模块及默认示例配置仍需单独评估，不能将本次升级解释为整套系统完成企业级验收。

前端通过textContent呈现文本；导出全部CSV字段做公式前缀及引号转义，JSON保留原数值文本。不存储新增凭据；JWT页临时登录只将令牌留在页面内存，数据库会话页每次调用原Auth验证。响应设置Cache-Control:no-store。

## 测试

后端`mvn test`覆盖原回归、新算法算例、非法边界和真实HTTP认证；前端`node --test --test-reporter=tap test/*.test.*`及`npm run build`。业务测试使用本项目现有测试环境，不等于逐一完成真实MySQL生产部署验收；本轮实际执行结果见工作区交付报告。规则分支和精度边界有具体算例，未使用固定成功响应替代算法。
