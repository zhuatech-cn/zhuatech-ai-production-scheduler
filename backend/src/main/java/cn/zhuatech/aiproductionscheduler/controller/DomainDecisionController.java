/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiproductionscheduler.controller;
import cn.zhuatech.aiproductionscheduler.common.ApiResponse;
import cn.zhuatech.aiproductionscheduler.service.DomainDecisionService;
import cn.zhuatech.aiproductionscheduler.service.FiniteCapacitySchedulerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/domain") public class DomainDecisionController {
 private final DomainDecisionService service; private final FiniteCapacitySchedulerService schedulerService;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public DomainDecisionController(DomainDecisionService service, FiniteCapacitySchedulerService schedulerService){this.service=service;this.schedulerService=schedulerService;}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @PostMapping("/decision") public ApiResponse<DomainDecisionService.DecisionResult> assess(@Valid @RequestBody DomainDecisionService.DecisionRequest request){return ApiResponse.ok(service.assess(request));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @PostMapping("/schedule") public ApiResponse<FiniteCapacitySchedulerService.ScheduleResult> schedule(@Valid @RequestBody FiniteCapacitySchedulerService.ScheduleRequest request){return ApiResponse.ok(schedulerService.schedule(request));}
}
