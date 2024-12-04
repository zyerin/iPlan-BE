package com.example.iplan.Service;

import com.example.iplan.DTO.PlanChildDTO;
import com.example.iplan.Domain.ScreenTime;
import com.example.iplan.Repository.PlanChildRepository;
import com.example.iplan.Repository.SetScreenTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ReportParentsService {

    private final PlanChildRepository planChildRepository;
    private final SetScreenTimeRepository screenTimeRepository;

    @Autowired
    public ReportParentsService(PlanChildRepository planChildRepository, SetScreenTimeRepository screenTimeRepository) {
        this.planChildRepository = planChildRepository;
        this.screenTimeRepository = screenTimeRepository;
    }

    // 한 달 동안 아이들이 작성한 전체 계획, 달성한 계획, 달성하지 못한 계획 수를 조회
    public ResponseEntity<Map<String, Object>> getMonthlyPlanReport(String userId, int year, int month) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        // 해당 월의 첫 번째 날짜와 마지막 날짜 설정
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 아이의 모든 계획 가져오기
        List<PlanChildDTO> plans = planChildRepository.findByUserIdAndDateRange(userId, startDate, endDate);

        // 전체 계획 수, 달성한 계획 수, 달성하지 못한 계획 수 계산
        int totalPlans = plans.size();
        int completedPlans = (int) plans.stream().filter(PlanChildDTO::is_completed).count();
        int uncompletedPlans = totalPlans - completedPlans;

        // 성취도 계산
        double achievementRate = totalPlans > 0 ? (double) completedPlans / totalPlans * 100 : 0.0;

        // 응답 데이터에 통계 정보 추가
        response.put("success", true);
        response.put("totalPlans", totalPlans);
        response.put("completedPlans", completedPlans);
        response.put("uncompletedPlans", uncompletedPlans);
        response.put("achievementRate", achievementRate); // 성취도 퍼센트

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 목표 스크린타임, 오늘의 스크린타임, 한 달 평균 스크린타임 조회
    public ResponseEntity<Map<String, Object>> getScreenTimeReport(String userId, int year, int month) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        // 오늘 날짜 설정
        LocalDate today = LocalDate.now();
        ScreenTime todayScreenTime = screenTimeRepository.findByDate(userId, String.valueOf(today));

        // 오늘의 스크린타임 및 목표 스크린타임
        if (todayScreenTime != null) {
            response.put("daily_limit", todayScreenTime.getDaily_limit()); // 목표 스크린타임
            response.put("used_time_today", todayScreenTime.getUsed_time()); // 오늘의 스크린타임
        } else {
            response.put("daily_limit", "No data");
            response.put("used_time_today", "No data");
        }

        // 해당 월의 첫 번째 날짜와 마지막 날짜 설정
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 한 달간의 스크린타임 데이터 가져오기
        List<ScreenTime> monthlyScreenTimes = screenTimeRepository.findByUserIdAndMonth(userId, startDate, endDate);
        double averageScreenTime = monthlyScreenTimes.stream()
                .mapToDouble(ScreenTime::getUsed_time)
                .average()
                .orElse(0.0);

        // 응답 데이터에 통계 정보 추가
        response.put("average_screen_time", averageScreenTime); // 한 달 평균 스크린타임

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
