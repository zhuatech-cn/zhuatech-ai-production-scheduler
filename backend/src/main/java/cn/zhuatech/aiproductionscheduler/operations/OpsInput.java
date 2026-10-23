/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiproductionscheduler.operations;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import tools.jackson.databind.json.JsonMapper;

/**
 * 有界、精确的只读业务核算输入。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
final class OpsInput {
  private static final JsonMapper JSON =
      JsonMapper.builder()
          .enable(
              tools.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS,
              tools.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
          .enable(tools.jackson.core.StreamReadFeature.STRICT_DUPLICATE_DETECTION)
          .build();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private OpsInput() {}

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @SuppressWarnings("unchecked")
  static Map<String, Object> parse(String raw) {
    if (raw == null || raw.getBytes(StandardCharsets.UTF_8).length > 262144)
      throw new IllegalArgumentException("请求最多256 KiB");
    Map<String, Object> value;
    try {
      value = JSON.readValue(raw, Map.class);
    } catch (tools.jackson.core.JacksonException ex) {
      throw new IllegalArgumentException("JSON格式错误、重复字段或尾随数据");
    }
    if (value == null) throw new IllegalArgumentException("请求须为JSON对象");
    inspect(value, 0, new int[] {0, 0});
    return value;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static void inspect(Object value, int depth, int[] budget) {
    if (depth > 8 || ++budget[0] > 15000) throw new IllegalArgumentException("请求层级或节点过多");
    if (value instanceof Map<?, ?> m) {
      if (m.size() > 32) throw new IllegalArgumentException("对象字段过多");
      for (var e : m.entrySet()) {
        inspect(e.getKey(), depth + 1, budget);
        inspect(e.getValue(), depth + 1, budget);
      }
    } else if (value instanceof List<?> l) {
      if (l.size() > 500) throw new IllegalArgumentException("最多500项");
      for (var e : l) inspect(e, depth + 1, budget);
    } else if (value instanceof String s) {
      budget[1] += s.length();
      if (s.length() > 200000 || budget[1] > 220000) throw new IllegalArgumentException("文本过长");
    } else if (value instanceof Number n) {
      BigDecimal d = decimal(n);
      if (d.precision() > 30 || Math.abs((long) d.scale()) > 12)
        throw new IllegalArgumentException("数值精度最多30位、小数/指数尺度最多12");
    } else if (value != null && !(value instanceof Boolean))
      throw new IllegalArgumentException("不支持的值类型");
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static BigDecimal decimal(Object v) {
    if (!(v instanceof Number) && !(v instanceof String))
      throw new IllegalArgumentException("数值须为十进制数或数字文本");
    if (v instanceof String s && (!s.matches("[0-9]+(?:\\.[0-9]+)?") || s.length() > 32))
      throw new IllegalArgumentException("数字文本不接受指数、负数、千分位或过长内容");
    try {
      var n = new BigDecimal(v.toString());
      if (n.precision() > 30 || Math.abs((long) n.scale()) > 12)
        throw new IllegalArgumentException("数值精度超限");
      return n;
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("数值须为有限十进制数");
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static String text(Object v) {
    if (!(v instanceof String s)
        || s.length() > 200
        || s.isBlank()
        || java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFKC).isBlank()
        || s.chars().anyMatch(Character::isISOControl))
      throw new IllegalArgumentException("文本须为200字以内非空且不含控制字符");
    return s.strip();
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static String id(Object v) {
    String s = text(v);
    if (!s.matches("[A-Za-z0-9][A-Za-z0-9_.:-]{0,79}"))
      throw new IllegalArgumentException("编号格式错误，最多80位");
    return s;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static LocalDate date(Object v) {
    String s = text(v);
    if (!s.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))
      throw new IllegalArgumentException("日期须为YYYY-MM-DD");
    try {
      var d = LocalDate.parse(s);
      if (d.getYear() < 1900 || d.getYear() > 2200)
        throw new IllegalArgumentException("日期年份范围1900至2200");
      return d;
    } catch (DateTimeException ex) {
      throw new IllegalArgumentException("日期不存在");
    }
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static void keys(Map<?, ?> m, Collection<String> allowed) {
    if (m.size() != allowed.size() || !m.keySet().containsAll(allowed))
      throw new IllegalArgumentException("字段缺失或包含未声明字段");
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  static Object safe(Object value) {
    if (value instanceof BigDecimal n) return n.toPlainString();
    if (value instanceof Map<?, ?> m) {
      Map<String, Object> out = new LinkedHashMap<>();
      m.forEach((k, v) -> out.put(k.toString(), safe(v)));
      return out;
    }
    if (value instanceof List<?> l) return l.stream().map(OpsInput::safe).toList();
    return value;
  }
}
