package com.example.iplan.Controller;

import com.example.iplan.Service.ReportParentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/report")
public class ReportParentsController {

    private final ReportParentsService reportParentsService;

    @Autowired
    public ReportParentsController(ReportParentsService reportParentsService) {
        this.reportParentsService = reportParentsService;
    }

    @GetMapping("/monthly-report")
    public ResponseEntity<Map<String, Object>> getMonthlyPlanReport(@RequestParam String userId, @RequestParam int year, @RequestParam int month) {
        try {
            return reportParentsService.getMonthlyPlanReport(userId, year, month);
        } catch (ExecutionException | InterruptedException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "보고서 조회에 실패했습니다. Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

