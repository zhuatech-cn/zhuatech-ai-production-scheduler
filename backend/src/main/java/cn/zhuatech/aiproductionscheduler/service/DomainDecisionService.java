/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiproductionscheduler.service;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;
import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class DomainDecisionService {
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public DecisionResult assess(DecisionRequest request) { int score=100;List<String>actions=new ArrayList<>();int shortage=Math.max(0,request.demandQuantity()-request.availableCapacity());if(shortage>0){score-=45;actions.add("扩充产能、调整批次或安排外协");}if(request.materialCoverageRate()<95){score-=35;actions.add("解决关键物料短缺后再下达");}if(request.bottleneckUtilization()>100){score-=35;actions.add("重排瓶颈工作中心负荷");}if(request.lateOrderCount()>0){score-=Math.min(30,request.lateOrderCount()*5);actions.add("处理预计延期订单并通知承诺变更");}if(request.maintenanceConflict()){score-=50;actions.add("消除设备维护窗口冲突");}if(!request.plannerApproved()){score-=30;actions.add("由生产计划员确认候选方案");}return result(score,actions,"SCHEDULE_READY","OPTIMIZE","BLOCKED",Map.of("capacityShortage",shortage,"materialCoverageRate",request.materialCoverageRate(),"bottleneckUtilization",request.bottleneckUtilization(),"setupChangeCount",request.setupChangeCount(),"lateOrderCount",request.lateOrderCount())); }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private DecisionResult result(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=80?good:score>=50?warn:bad;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private DecisionResult riskResult(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=70?bad:score>=40?warn:good;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record DecisionRequest(
        @NotBlank String planNo,
        @Positive int demandQuantity,
        @PositiveOrZero int availableCapacity,
        @DecimalMin("0") @DecimalMax("100") double materialCoverageRate,
        @DecimalMin("0") double bottleneckUtilization,
        @PositiveOrZero int lateOrderCount,
        @PositiveOrZero int setupChangeCount,
        boolean maintenanceConflict,
        boolean plannerApproved) {}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record DecisionResult(String decision,int score,Map<String,Object> metrics,List<String> actions) {}
}
