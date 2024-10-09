package com.example.iplan.Service;

import com.example.iplan.Domain.RewardChild;
import com.example.iplan.Domain.RewardParent;
import com.example.iplan.Repository.RewardChildRepository;
import com.example.iplan.Repository.RewardParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class RewardParentService {

    private final RewardParentRepository rewardParentRepository;
    private final RewardChildRepository rewardChildRepository;

    @Autowired
    public RewardParentService(RewardParentRepository rewardParentRepository, RewardChildRepository rewardRepository) {
        this.rewardParentRepository = rewardParentRepository;
        this.rewardChildRepository = rewardRepository;
    }

    /**
     * 부모님의 보상 코멘트와 별점, 보상 지급 여부를 저장하는 기능
     * @param rewardParent 저장할 RewardParent 객체
     * @param rewardId 관련된 Reward의 ID
     * @return 저장 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> saveRewardParent(RewardParent rewardParent, String rewardId) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. RewardChild 엔티티에서 planId 추출
            RewardChild reward = rewardChildRepository.findById(rewardId);
            if (reward == null) {
                response.put("success", false);
                response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 2. 추출한 planId를 RewardParent에 설정
            rewardParent.setPlan_id(reward.getPlan_id());

            // 3. RewardParent 저장
            rewardParent.set_rewarded(true);  // RewardParent의 isRewarded를 true로 설정
            rewardParentRepository.save(rewardParent);

            // 4. Reward의 isRewarded를 true로 설정하고 저장
            reward.set_rewarded(true);
            rewardChildRepository.update(reward);

            response.put("success", true);
            response.put("message", "부모님의 코멘트와 별점이 정상적으로 저장되었고, 보상 지급 상태가 업데이트되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "저장에 실패했습니다. Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 부모님 코멘트와 별점을 조회하는 기능
     * @param id 조회할 RewardParent의 ID
     * @return 조회된 RewardParent 객체
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public RewardParent getRewardParent(String id) throws ExecutionException, InterruptedException {
        try {
            return rewardParentRepository.findById(id);
        } catch (Exception e) {
            throw new ExecutionException("보상 조회에 실패했습니다. Error: " + e.getMessage(), e);
        }
    }

    /**
     * 부모님의 보상 코멘트와 별점 수정 기능
     * @param rewardParent 수정할 RewardParent 객체
     * @return 수정 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> updateRewardParent(RewardParent rewardParent) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
            RewardParent existingRewardParent = rewardParentRepository.findById(rewardParent.getId());

            if (existingRewardParent == null) {
                response.put("success", false);
                response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 필드 업데이트
            if (rewardParent.getComment() != null) {
                existingRewardParent.setComment(rewardParent.getComment());
            }
            if (rewardParent.getGrade() != 0) {
                existingRewardParent.setGrade(rewardParent.getGrade());
            }
            existingRewardParent.set_rewarded(rewardParent.is_rewarded());

            rewardParentRepository.update(existingRewardParent);

            response.put("success", true);
            response.put("message", "부모님의 코멘트와 별점이 정상적으로 수정되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "수정에 실패했습니다. Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
