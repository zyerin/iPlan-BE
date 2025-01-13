package com.example.iplan.Service;

import com.example.iplan.DTO.DayDataDTO;
import com.example.iplan.DTO.PlanChildDTO;
import com.example.iplan.Domain.DayData;
import com.example.iplan.Domain.ScreenTimeOCRResult;
import com.example.iplan.Repository.DayDataRepository;
import com.example.iplan.Repository.GetScreenTimeOCRRepository;
import com.example.iplan.Repository.PlanChildRepository;
import com.google.api.Http;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final DayDataRepository dayDataRepository;
    private final PlanChildService planChildService;
    private final GetScreenTimeOCRRepository getScreenTimeOCRRepository;

    public ResponseEntity<Map<String, Object>> getAllCalendarData(String yearMonth, String user_id){
        Map<String, Object> response = new HashMap<>();
        List<DayDataDTO> dayDataDTOList;
        try{
            dayDataDTOList = dayDataRepository.findTargetMonthData(user_id, yearMonth);
        }catch(Exception e){
            CatchException(response, e);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("success", true);
        response.put("message", "해당 달의 데이터 가져오기에 성공하였습니다");
        response.put("entity", dayDataDTOList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> getTargetDateData(String yearMonthDate, String user_id) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();
        DayData dayData;

        try{
            dayData = dayDataRepository.findTargetDayData(user_id, yearMonthDate);
        }catch (Exception e){
            CatchException(response, e);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        String targetDate = dayData.getYear()+"-"+dayData.getMonth()+"-"+dayData.getDate();
        ScreenTimeOCRResult screenTimeOCRResult= getScreenTimeOCRRepository.findByDate(user_id, targetDate);
        List<PlanChildDTO> planChildDTOList = planChildService.findAllPlanList(user_id, targetDate);

        boolean allPlanSuccess = false;
        if(!planChildDTOList.isEmpty()){
            for(var dto : planChildDTOList){
                if (!dto.is_completed()) {
                    break;
                }
            }
            allPlanSuccess = true;
        }

        boolean screenTimeSuccess = false;
        if(screenTimeOCRResult != null){
            screenTimeSuccess = screenTimeOCRResult.isSuccess();
        }

        DayDataDTO dayDataDTO = DayDataDTO.builder()
                .id(dayData.getId())
                .planChildDTOList(planChildDTOList)
                .screenTime_goal(screenTimeSuccess)
                .reach_goal(allPlanSuccess)
                .build();

        response.put("success", true);
        response.put("entity", dayDataDTO);
        response.put("message", "특정 날짜 데이터 가져오기 성공");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void CatchException(Map<String, Object> response, Exception e){
        response.put("success", false);
        response.put("message", e.getMessage());
    }
}
