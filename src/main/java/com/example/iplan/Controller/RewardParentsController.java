package com.example.iplan.Controller;

import com.example.iplan.DTO.RewardChildDTO;
import com.example.iplan.DTO.RewardParentsDTO;
import com.example.iplan.Domain.RewardParents;
import com.example.iplan.Service.RewardParentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/reward-parents")
public class RewardParentsController {

    private final RewardParentsService rewardParentsService;

    @Autowired
    public RewardParentsController(RewardParentsService rewardParentsService) {
        this.rewardParentsService = rewardParentsService;
    }

    // 아이들이 설정한 보상 지급
    @PostMapping("/{rewardId}")
    public ResponseEntity<Map<String, Object>> addRewardParents(@RequestBody RewardParentsDTO rewardParents, @PathVariable String rewardId) throws ExecutionException, InterruptedException {
        return rewardParentsService.saveRewardParents(rewardParents, rewardId);
    }

    // RewardParents 에서 해당 문서의 id에 대한 세부사항 가져옴
    @GetMapping("/{id}")
    public ResponseEntity<RewardParents> getRewardParents(@PathVariable String id) throws ExecutionException, InterruptedException {
        RewardParents rewardParents = rewardParentsService.getRewardParents(id);
        return ResponseEntity.ok(rewardParents);
    }

    @PatchMapping
    public ResponseEntity<Map<String, Object>> updateRewardParents(@RequestBody RewardParentsDTO rewardParents) throws ExecutionException, InterruptedException {
        return rewardParentsService.updateRewardParents(rewardParents);
    }

    // 한 달간 아직 지급하지 않은 보상 개수
    @GetMapping("/monthly-not-rewarded-count")
    public ResponseEntity<Map<String, Object>> countMonthlyNotRewarded(
            @RequestParam String linked_id,
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> response = new HashMap<>();
        try {
            int rewardCount = rewardParentsService.countMonthlyNotRewarded(linked_id, year, month);
            response.put("success", true);
            response.put("rewardCount", rewardCount);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            response.put("success", false);
            response.put("message", "보상 계산에 실패했습니다. Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // 한 달간 아직 지급하지 않은 보상의 목록
    @GetMapping("/monthly-not-rewarded-lists")
    public ResponseEntity<Map<String, Object>> listMonthlyNotRewarded(
            @RequestParam String linked_id,
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<RewardChildDTO> rewards = rewardParentsService.listMonthlyNotRewarded(linked_id, year, month);

            if (rewards.isEmpty()) {
                response.put("success", false);
                response.put("message", "해당 월에 대한 보상이 모두 첨삭 완료되었습니다.");
            } else {
                response.put("success", true);
                response.put("rewards", rewards);
            }

            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            response.put("success", false);
            response.put("message", "보상 목록을 가져오는 데 실패했습니다. Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
