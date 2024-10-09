package com.example.iplan.Controller;

import com.example.iplan.DTO.RewardParentsDTO;
import com.example.iplan.Domain.RewardParents;
import com.example.iplan.Service.RewardParentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
