package com.example.iplan.Controller;

import com.example.iplan.DTO.PlanChildDTO;
import com.example.iplan.Domain.PlanChild;
import com.example.iplan.Domain.ScreenTime;
import com.example.iplan.Service.PlanChildService;
import com.google.firebase.database.annotations.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/plan")
public class PlanChildController {

    private final PlanChildService planChildService;

    /**
     * 계획을 추가한다
     * @param request PlanChildDto
     * @param user_id 유저 아이디
     * @return 성공 여부 및 오류 메세지
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping("/addition")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> additionPlan(@RequestBody @NotNull PlanChildDTO request, String user_id)
            throws ExecutionException, InterruptedException {
        return planChildService.postChildNewPlan(request, user_id);
    }

    /**
     * 추가된 계획 리스트를 전부 보여준다.
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/List")
    public List<PlanChildDTO> showPlanList(@AuthenticationPrincipal String user_id, String targetDate) throws ExecutionException, InterruptedException {
        return planChildService.findAllPlanList(user_id, targetDate);
    }

    @GetMapping("/detail/{documentID}")
    public PlanChild showPlanDetail(@PathVariable String documentID) throws ExecutionException, InterruptedException {
        return planChildService.findByPlanID(documentID);
    }

    @PatchMapping("/update-plan")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePlan(@RequestBody @NotNull PlanChildDTO request, @AuthenticationPrincipal String user_id) throws ExecutionException, InterruptedException {
        return planChildService.updateOriginalPlan(request, user_id);
    }

    @DeleteMapping("/delete-plan/{documentID}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePlan(@PathVariable String documentID) throws ExecutionException, InterruptedException {
        return planChildService.DeletePlan(documentID);
    }

    @PostMapping("/screen-time-set")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> setScreenTime(@RequestBody ScreenTime screenTime){
        return planChildService.SetScreenTime(screenTime);
    }
}
