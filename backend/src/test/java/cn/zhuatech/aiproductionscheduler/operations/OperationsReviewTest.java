/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiproductionscheduler.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.json.JsonMapper;

/**
 * 业务算例、资源限制、十进制和全批拒绝回归。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class OperationsReviewTest {
  private final OperationsReviewService service = new OperationsReviewService();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private Map<String, Object> sample() {
    return OpsInput.parse(new JsonMapper().writeValueAsString(service.schema().get("sample")));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private Map<String, Object> first(Map<String, Object> request) {
    return (Map<String, Object>) ((List<?>) request.get("records")).getFirst();
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  private Map<String, Object> output(Map<String, Object> request) {
    return (Map<String, Object>) ((List<?>) service.execute(request).get("results")).getFirst();
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  static Stream<Map<String, Object>> businessCases() throws Exception {
    try (var in = OperationsReviewTest.class.getResourceAsStream("/operations/cases.json")) {
      return ((List<Map<String, Object>>) new JsonMapper().readValue(in, List.class)).stream();
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @ParameterizedTest
  @MethodSource("businessCases")
  @SuppressWarnings("unchecked")
  void domainCalculationMatchesBusinessCase(Map<String, Object> test) {
    var r = sample();
    first(r).putAll((Map<String, Object>) test.get("patch"));
    if (Boolean.TRUE.equals(test.get("error"))) {
      assertThrows(IllegalArgumentException.class, () -> service.execute(r), test.toString());
      return;
    }
    var out = output(r);
    for (var expected : ((Map<String, Object>) test.get("expected")).entrySet()) {
      Object actual = out.get(expected.getKey());
      assertNotNull(actual, test.toString());
      String value = expected.getValue().toString();
      if (value.startsWith("#"))
        assertEquals(
            0,
            new BigDecimal(value.substring(1)).compareTo(new BigDecimal(actual.toString())),
            test.toString());
      else assertEquals(value, actual.toString(), test.toString());
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void resultIsReadOnlyAndCountsBalance() {
    var r = service.execute(sample());
    assertEquals(false, r.get("automaticWrite"));
    assertEquals(1, r.get("recordCount"));
    assertEquals(
        1,
        ((Number) r.get("passCount")).intValue()
            + ((Number) r.get("reviewCount")).intValue()
            + ((Number) r.get("holdCount")).intValue());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  @SuppressWarnings("unchecked")
  void everyPublishedMetricIsPresent() {
    var out = output(sample());
    for (var column : (List<Map<String, Object>>) service.schema().get("columns"))
      assertTrue(out.containsKey(column.get("key")), column.toString());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rootMustBeObject() {
    for (String value : List.of("null", "[]", "1", "\"x\""))
      assertThrows(IllegalArgumentException.class, () -> service.request(value));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void duplicateJsonKeysRejected() {
    var ex =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                service.request(
                    "{\"asOf\":\"2026-10-23\",\"asOf\":\"2026-10-23\",\"records\":[]}"));
    assertTrue(ex.getMessage().contains("JSON"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void trailingJsonRejected() {
    assertThrows(IllegalArgumentException.class, () -> service.request("{} {}"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void exactNumericJsonSurvivesParsing() {
    assertEquals("0.123456789012", OpsInput.parse("{\"x\":0.123456789012}").get("x").toString());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void underflowCannotBecomeZero() {
    var ex =
        assertThrows(IllegalArgumentException.class, () -> OpsInput.parse("{\"x\":1e-1000000}"));
    assertTrue(ex.getMessage().contains("精度"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void bodyByteBudgetUsesUtf8() {
    assertThrows(
        IllegalArgumentException.class,
        () -> OpsInput.parse("{\"x\":\"" + "中".repeat(90000) + "\"}"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void decimalStringDoesNotLoseCents() {
    assertEquals("9007199254740993.01", OpsInput.decimal("9007199254740993.01").toPlainString());
    assertEquals("9007199254740993.01", OpsInput.safe(new BigDecimal("9007199254740993.01")));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void invalidDecimalStringsRejected() {
    for (String s : List.of("1e3", "NaN", "Infinity", "1,000", ".01", "-1"))
      assertThrows(IllegalArgumentException.class, () -> OpsInput.decimal(s), s);
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void allNumbersCanUseExactStrings() {
    assertNotNull(output(sample()));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void recordsCannotBeEmpty() {
    var r = sample();
    r.put("records", List.of());
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void recordsCannotExceed500() {
    var r = sample();
    r.put("records", Collections.nCopies(501, first(r)));
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void missingRootFieldRejected() {
    var r = sample();
    r.remove("asOf");
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void unknownRootFieldRejected() {
    var r = sample();
    r.put("automaticApprove", true);
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void unknownRowFieldRejected() {
    var r = sample();
    first(r).put("administrator", true);
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void missingRowFieldRejected() {
    var r = sample();
    first(r).remove("id");
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void nonObjectRowRejected() {
    var r = sample();
    r.put("records", List.of("invalid"));
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void duplicateIdsRejectedAsWholeBatch() {
    var r = sample();
    r.put("records", List.of(first(r), new HashMap<>(first(r))));
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void idsAreCaseInsensitiveWithinBatch() {
    var r = sample();
    var other = new HashMap<>(first(r));
    other.put("id", "item-001");
    r.put("records", List.of(first(r), other));
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void invalidIdCannotBeMarkup() {
    var r = sample();
    first(r).put("id", "<img src=x>");
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void datesAreStrict() {
    for (String s : List.of("2026-02-30", "2026-2-01", "1899-01-01", "2201-01-01")) {
      var r = sample();
      r.put("asOf", s);
      assertThrows(IllegalArgumentException.class, () -> service.execute(r));
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void unicodeBlankRejected() {
    assertThrows(IllegalArgumentException.class, () -> OpsInput.text("\u00A0"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void controlsRejected() {
    assertThrows(IllegalArgumentException.class, () -> OpsInput.text("value\u0000"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  @SuppressWarnings("unchecked")
  void everyFieldTypeAndRangeIsValidated() {
    for (var f : (List<Map<String, Object>>) service.schema().get("fields")) {
      var r = sample();
      first(r)
          .put(
              f.get("key").toString(),
              f.get("type").equals("NUMBER") ? "1000000000000000000000000000000" : "");
      assertThrows(IllegalArgumentException.class, () -> service.execute(r), f.toString());
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  @SuppressWarnings("unchecked")
  void stableBatchOrderingAndAllRowsPreserved() {
    var r = sample();
    var b = new HashMap<>(first(r));
    b.put("id", "B");
    var a = new HashMap<>(first(r));
    a.put("id", "A");
    r.put("records", List.of(b, a));
    var out = service.execute(r);
    var rows = (List<Map<String, Object>>) out.get("results");
    assertEquals(2, rows.size());
    assertEquals("A", rows.get(0).get("id"));
    assertEquals("B", rows.get(1).get("id"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void directAndJsonExecutionAgree() {
    var r = sample();
    assertEquals(service.execute(r), service.request(new JsonMapper().writeValueAsString(r)));
  }
}
