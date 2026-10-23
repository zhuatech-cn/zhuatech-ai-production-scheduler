/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiproductionscheduler.operations;

import java.util.*;
import org.springframework.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 对请求者提交的批次执行只读核算，不查询他人业务数据。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/operations-review")
public class OperationsController {
  private final OperationsReviewService service;

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public OperationsController(OperationsReviewService service) {
    this.service = service;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private void authorize(Authentication principal) {
    if (principal == null
        || !principal.isAuthenticated()
        || principal instanceof AnonymousAuthenticationToken)
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "请先登录");
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @GetMapping("/schema")
  public ResponseEntity<?> schema(Authentication principal) {
    authorize(principal);
    return ResponseEntity.ok()
        .cacheControl(CacheControl.noStore())
        .body(Map.of("success", true, "data", service.schema()));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @PostMapping("/evaluate")
  public ResponseEntity<?> evaluate(@RequestBody String raw, Authentication principal) {
    authorize(principal);
    try {
      return ResponseEntity.ok()
          .cacheControl(CacheControl.noStore())
          .body(
              Map.of(
                  "success",
                  true,
                  "message",
                  "核算完成；未变更业务记录",
                  "data",
                  OpsInput.safe(service.request(raw))));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest()
          .cacheControl(CacheControl.noStore())
          .body(Map.of("success", false, "message", ex.getMessage()));
    }
  }
}
