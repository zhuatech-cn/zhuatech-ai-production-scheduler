/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiproductionscheduler.domain;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class DomainCatalog {
    private final Map<String, WorkflowAction> actions = new LinkedHashMap<>();
    public DomainCatalog() {
        actions.put("OPTIMIZE", new WorkflowAction("OPTIMIZE", "生成候选排程", List.of("草稿"), "待确认", "OPERATOR"));
        actions.put("APPROVE", new WorkflowAction("APPROVE", "批准生产计划", List.of("待确认"), "待下达", "ADMIN"));
        actions.put("RELEASE", new WorkflowAction("RELEASE", "下达生产计划", List.of("待下达"), "已下达", "ADMIN"));
    }
    public String systemName() { return "知华科技AI生产排程优化系统"; }
    public String scene() { return "需求池、主生产计划、有限产能、物料齐套、瓶颈、换型、维护窗口、排程仿真与下达"; }
    public String initialStatus() { return "草稿"; }
    public String partyLabel() { return "工厂/产线/订单"; }
    public String amountLabel() { return "生产计划价值"; }
    public String quantityLabel() { return "计划生产数量"; }
    public String dueLabel() { return "订单交期"; }
    public List<ModuleDefinition> modules() { return List.of(
            new ModuleDefinition("DEMAND", "需求池", "归集销售订单、预测、安全库存和优先级"),
            new ModuleDefinition("CAPACITY", "有限产能", "维护设备、班次、人员、模具和日历产能"),
            new ModuleDefinition("MATERIAL", "物料齐套", "计算在库、在途、替代料和短缺时间"),
            new ModuleDefinition("CONSTRAINT", "约束建模", "配置前后置、批量、换型、冻结窗和维护约束"),
            new ModuleDefinition("OPTIMIZER", "AI排程优化", "按交期、在制品、换型和成本生成候选方案"),
            new ModuleDefinition("SIMULATION", "情景仿真", "比较插单、设备停机、加班和外协情景"),
            new ModuleDefinition("BOTTLENECK", "瓶颈管理", "识别超负荷工作中心并给出处置动作"),
            new ModuleDefinition("RELEASE", "计划下达", "经计划员确认后下达 MES 并冻结版本"),
            new ModuleDefinition("PERFORMANCE", "执行反馈", "跟踪达成率、延误、重排和模型偏差")
        ); }
    public Map<String, WorkflowAction> actions() { return Collections.unmodifiableMap(actions); }
    public record ModuleDefinition(String code,String name,String description) {}
    public record WorkflowAction(String code,String label,List<String> from,String to,String requiredRole) {}
}
