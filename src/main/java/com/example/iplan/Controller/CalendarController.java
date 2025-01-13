package com.example.iplan.Controller;

import com.example.iplan.Service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    /**
     * (달력 탭) 해당 년월의 도장(성공)상황 모두 가져오기
     * @param yearMonth
     * @param userId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/{yearMonth}")
    public ResponseEntity<Map<String, Object>> getMonthCalendarData(@PathVariable String yearMonth, @AuthenticationPrincipal String userId) throws ExecutionException, InterruptedException {
        return calendarService.getAllCalendarData(yearMonth, userId);
    }

    /**
     * (달력 탭) 특정 날짜 클릭시 해당 날짜의 데이터 모두 가져오기
     * @param yearMonthDate
     * @param userId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/showTargetDateData/{yearMonthDate}")
    public ResponseEntity<Map<String, Object>> getTargetDateData(@PathVariable String yearMonthDate, @AuthenticationPrincipal String userId) throws ExecutionException, InterruptedException {
        return calendarService.getTargetDateData(yearMonthDate, userId);
    }
}
