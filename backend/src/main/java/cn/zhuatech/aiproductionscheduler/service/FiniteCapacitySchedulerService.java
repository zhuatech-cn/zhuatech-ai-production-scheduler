/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiproductionscheduler.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class FiniteCapacitySchedulerService {
    public ScheduleResult schedule(@Valid ScheduleRequest request) {
        Map<String, WorkCenter> centers = new LinkedHashMap<>();
        Map<String, LocalDateTime> cursor = new HashMap<>();
        for (WorkCenter center : request.workCenters()) {
            if (center.availableUntil().isBefore(center.availableFrom()) || center.availableUntil().equals(center.availableFrom()))
                throw new IllegalArgumentException("工作中心可用结束时间必须晚于开始时间: " + center.code());
            if (centers.put(center.code(), center) != null) throw new IllegalArgumentException("工作中心编码不能重复: " + center.code());
            cursor.put(center.code(), center.availableFrom());
        }
        Set<String> jobIds = new HashSet<>();
        for (Job job : request.jobs()) if (!jobIds.add(job.jobNo())) throw new IllegalArgumentException("工单号不能重复: " + job.jobNo());
        List<Job> jobs = request.jobs().stream().sorted(Comparator.comparingInt(Job::priority).reversed()
            .thenComparing(Job::dueAt).thenComparing(Job::jobNo)).toList();
        List<ScheduleLine> lines = new ArrayList<>();
        List<UnscheduledJob> unscheduled = new ArrayList<>();
        Map<String, Long> assignedMinutes = new LinkedHashMap<>();
        centers.keySet().forEach(code -> assignedMinutes.put(code, 0L));
        for (Job job : jobs) {
            long durationMinutes = Math.round(job.durationHours() * 60);
            String bestCenter = null; LocalDateTime bestStart = null; LocalDateTime bestEnd = null;
            for (String code : job.eligibleWorkCenters()) {
                WorkCenter center = centers.get(code);
                if (center == null) continue;
                LocalDateTime start = cursor.get(code).isAfter(center.availableFrom()) ? cursor.get(code) : center.availableFrom();
                LocalDateTime end = start.plusMinutes(durationMinutes);
                if (end.isAfter(center.availableUntil())) continue;
                if (bestEnd == null || end.isBefore(bestEnd) || end.equals(bestEnd) && code.compareTo(bestCenter) < 0) {
                    bestCenter = code; bestStart = start; bestEnd = end;
                }
            }
            if (bestCenter == null) {
                unscheduled.add(new UnscheduledJob(job.jobNo(), "符合条件的工作中心剩余产能不足"));
                continue;
            }
            cursor.put(bestCenter, bestEnd);
            assignedMinutes.compute(bestCenter, (key, value) -> value + durationMinutes);
            long lateMinutes = Math.max(0, Duration.between(job.dueAt(), bestEnd).toMinutes());
            lines.add(new ScheduleLine(job.jobNo(), bestCenter, bestStart, bestEnd, lateMinutes,
                lateMinutes == 0 ? "ON_TIME" : "LATE"));
        }
        Map<String, Integer> utilization = new LinkedHashMap<>();
        for (WorkCenter center : centers.values()) {
            long capacity = Duration.between(center.availableFrom(), center.availableUntil()).toMinutes();
            utilization.put(center.code(), (int)Math.min(100, Math.round(assignedMinutes.get(center.code()) * 100.0 / capacity)));
        }
        long lateJobs = lines.stream().filter(line -> line.lateMinutes() > 0).count();
        return new ScheduleResult(lines, unscheduled, utilization, lines.size(), lateJobs, unscheduled.size());
    }

    public record ScheduleRequest(@NotEmpty List<@Valid WorkCenter> workCenters, @NotEmpty List<@Valid Job> jobs) {}
    public record WorkCenter(@NotBlank String code, @NotNull LocalDateTime availableFrom, @NotNull LocalDateTime availableUntil) {}
    public record Job(@NotBlank String jobNo, @DecimalMin("0.01") double durationHours, @NotNull LocalDateTime dueAt,
                      @Min(1) @Max(100) int priority, @NotEmpty List<@NotBlank String> eligibleWorkCenters) {}
    public record ScheduleLine(String jobNo, String workCenter, LocalDateTime startAt, LocalDateTime endAt,
                               long lateMinutes, String deliveryStatus) {}
    public record UnscheduledJob(String jobNo, String reason) {}
    public record ScheduleResult(List<ScheduleLine> schedule, List<UnscheduledJob> unscheduled,
                                 Map<String,Integer> utilizationPercent, int scheduledJobs, long lateJobs, long unscheduledJobs) {}
}
