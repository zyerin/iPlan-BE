package com.example.iplan.Controller;

import com.example.iplan.DTO.PlanChildDTO;
import com.example.iplan.Domain.PlanChild;
import com.example.iplan.Domain.ScreenTime;
import com.example.iplan.Service.PlanChildService;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.database.annotations.NotNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 특정 날짜의 단일 계획들에 대한 컨트롤러
 */
@Tag(name = "Plan CRUD", description = "아이 화면에서 계획을 추가하고, 확인하고, 수정하고, 삭제합니다.")
@RequiredArgsConstructor
@Controller
@RequestMapping("/plan")
public class PlanChildController {

    private final PlanChildService planChildService;

    /**
     * (목표 탭에서 계획 추가하기 버튼 클릭시) 해당 날짜에 단일 계획을 추가한다
     * @param request PlanChildDto
     * @param token 유저 아이디
     * @return 성공 여부 및 오류 메세지
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Operation(summary = "계획 추가", description = "당일의 계획을 추가한다.")
    @PostMapping("/addition")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> additionPlan(@RequestBody @NotNull PlanChildDTO request, @AuthenticationPrincipal FirebaseToken token)
            throws ExecutionException, InterruptedException {
        String user_id = token.getUid();

        return planChildService.postChildNewPlan(request, user_id);
    }

    /**
     * (목표 탭 클릭시)추가된 계획 리스트를 전부 보여준다.(계획 제목만)
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/dayPlanList")
    public List<PlanChildDTO> showPlanList(@AuthenticationPrincipal FirebaseToken token, String targetDate) throws ExecutionException, InterruptedException {
        String user_id = token.getUid();

        return planChildService.findAllPlanList(user_id, targetDate);
    }

    /**
     * (목표 탭에서 해당 날짜에서 특정 계획 클릭시)특정 계획의 세부사항을 확인한다.
     * @param documentID
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/detail/{documentID}")
    public PlanChild showPlanDetail(@PathVariable String documentID) throws ExecutionException, InterruptedException {
        return planChildService.findByPlanID(documentID);
    }

    /**
     * 특정 계획 수정
     * @param request
     * @param token
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PatchMapping("/update-plan")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePlan(@RequestBody @NotNull PlanChildDTO request, @AuthenticationPrincipal FirebaseToken token) throws ExecutionException, InterruptedException {
        String user_id = token.getUid();

        return planChildService.updateOriginalPlan(request, user_id);
    }

    /**
     * 특정 계획 삭제 버튼 클릭시
     * @param documentID
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @DeleteMapping("/delete-plan/{documentID}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePlan(@PathVariable String documentID) throws ExecutionException, InterruptedException {
        return planChildService.DeletePlan(documentID);
    }

    /**
     * 목표 탭에서 스크린 타임 측정 클릭시 목표 시간 설정
     * @param screenTime
     * @return
     */
    @PostMapping("/screen-time-set")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> setScreenTime(@RequestBody ScreenTime screenTime){
        return planChildService.SetScreenTime(screenTime);
    }
}
