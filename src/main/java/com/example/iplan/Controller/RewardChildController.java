package com.example.iplan.Controller;

import com.example.iplan.DTO.RewardChildDTO;
import com.example.iplan.Domain.RewardChild;
import com.example.iplan.Service.RewardChildService;
import com.google.firebase.database.annotations.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/reward-child")
public class RewardChildController {

    private final RewardChildService rewardChildService;

    /**
     * 보상을 추가(저장)
     * @param reward Reward 객체
     * @return 성공 여부 및 오류 메시지
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addReward(@RequestBody @NotNull RewardChildDTO reward) throws ExecutionException, InterruptedException {
        return rewardChildService.saveReward(reward);
    }

    /**
     * 보상 세부사항을 가져옴
     * @param documentId 보상 ID
     * @return 보상 객체
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/{documentId}")
    @ResponseBody
    public ResponseEntity<RewardChild> getReward(@PathVariable String documentId) throws ExecutionException, InterruptedException {
        RewardChild reward = rewardChildService.getReward(documentId);
        return ResponseEntity.ok(reward);
    }

    /**
     * 보상을 수정
     * @param reward 수정할 Reward 객체
     * @return 성공 여부 및 오류 메시지
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PatchMapping()
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateReward(@AuthenticationPrincipal String user_id, @RequestBody @NotNull RewardChildDTO reward) throws ExecutionException, InterruptedException {
        return rewardChildService.updateReward(user_id, reward);
    }

    /**
     * 보상을 삭제
     * @param documentID 보상 ID
     * @return 성공 여부 및 오류 메시지
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @DeleteMapping("/{documentID}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteReward(@PathVariable String documentID) throws ExecutionException, InterruptedException {
        return rewardChildService.deleteReward(documentID);
    }

    /**
     * 한 달간 달성한 보상 수를 계산
     * @param user_id 사용자 ID
     * @param year 해당 연도
     * @param month 해당 월 (1월은 1, 12월은 12)
     * @return 한 달간 달성한 보상 수
     */
    @GetMapping("/monthly-achievement")
    public ResponseEntity<Map<String, Object>> countMonthlyRewards(
            @AuthenticationPrincipal String user_id,
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> response = new HashMap<>();
        try {
            int rewardCount = rewardChildService.countMonthlyRewards(user_id, year, month);
            response.put("success", true);
            response.put("rewardCount", rewardCount);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            response.put("success", false);
            response.put("message", "보상 계산에 실패했습니다. Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}
