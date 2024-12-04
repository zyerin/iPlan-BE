package com.example.iplan.Controller;

import com.example.iplan.Service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.optional.qual.Present;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{yearMonth}")
    public ResponseEntity<Map<String, Object>> getMonthCalendarData(@PathVariable String yearMonth, @AuthenticationPrincipal String userId){
        return calendarService.getAllCalendarData(yearMonth, userId);
    }
}
