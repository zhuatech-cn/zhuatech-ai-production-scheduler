/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiproductionscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc class FiniteCapacitySchedulerApiTests {
 @Autowired MockMvc mvc;
 private static final String BODY="""
  {"workCenters":[{"code":"WC-A","availableFrom":"2026-09-20T08:00:00","availableUntil":"2026-09-20T16:00:00"},
                   {"code":"WC-B","availableFrom":"2026-09-20T08:00:00","availableUntil":"2026-09-20T12:00:00"}],
   "jobs":[{"jobNo":"MO-1","durationHours":5,"dueAt":"2026-09-20T15:00:00","priority":100,"eligibleWorkCenters":["WC-A"]},
            {"jobNo":"MO-2","durationHours":4,"dueAt":"2026-09-20T12:00:00","priority":80,"eligibleWorkCenters":["WC-A","WC-B"]},
            {"jobNo":"MO-3","durationHours":6,"dueAt":"2026-09-20T18:00:00","priority":20,"eligibleWorkCenters":["WC-B"]}]}
  """;
 @Test void producesCapacityConstrainedScheduleAndExceptions() throws Exception {
  mvc.perform(post("/api/domain/schedule").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(BODY))
   .andExpect(status().isOk()).andExpect(jsonPath("$.data.scheduledJobs").value(2))
   .andExpect(jsonPath("$.data.unscheduledJobs").value(1)).andExpect(jsonPath("$.data.schedule[0].jobNo").value("MO-1"))
   .andExpect(jsonPath("$.data.utilizationPercent.WC-A").value(63));
 }
 @Test void rejectsInvalidCapacityWindow() throws Exception {
  String invalid=BODY.replace("2026-09-20T16:00:00","2026-09-20T07:00:00");
  mvc.perform(post("/api/domain/schedule").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(invalid))
   .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("工作中心可用结束时间必须晚于开始时间: WC-A"));
 }
 @Test void scheduleRequiresAuthentication() throws Exception {mvc.perform(post("/api/domain/schedule").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized());}
}
