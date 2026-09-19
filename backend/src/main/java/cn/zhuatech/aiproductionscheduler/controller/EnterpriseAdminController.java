/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiproductionscheduler.controller;
import cn.zhuatech.aiproductionscheduler.common.ApiResponse;
import cn.zhuatech.aiproductionscheduler.model.EnterpriseControl;
import cn.zhuatech.aiproductionscheduler.service.EnterpriseControlService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/admin/enterprise/controls")
public class EnterpriseAdminController {
    private final EnterpriseControlService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public EnterpriseAdminController(EnterpriseControlService service){this.service=service;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/{id}/review") ApiResponse<EnterpriseControl> review(@PathVariable Long id,@Valid @RequestBody EnterpriseControlService.ReviewRequest request){return ApiResponse.ok(service.review(id,request));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/{id}/sync") ApiResponse<EnterpriseControl> sync(@PathVariable Long id,@Valid @RequestBody EnterpriseControlService.SyncRequest request){return ApiResponse.ok(service.sync(id,request));}
}
