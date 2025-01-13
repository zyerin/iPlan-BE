package com.example.iplan.Service;

import com.example.iplan.DTO.RewardParentsDTO;
import com.example.iplan.Domain.RewardChild;
import com.example.iplan.Domain.RewardParents;
import com.example.iplan.Repository.RewardChildRepository;
import com.example.iplan.Repository.RewardParentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Service
public class RewardParentsService {

    private final RewardParentsRepository rewardParentsRepository;
    private final RewardChildRepository rewardChildRepository;

    /**
     * 부모님의 보상 코멘트와 별점, 보상 지급 여부를 저장하는 기능
     * @param rewardParentsDTO 저장할 RewardParents 객체
     * @param rewardId 관련된 Reward의 ID
     * @return 저장 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> saveRewardParents(RewardParentsDTO rewardParentsDTO, String rewardId) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. RewardChild 엔티티에서 planId 추출
            RewardChild reward = rewardChildRepository.findEntityByDocumentId(rewardId);
            if (reward == null) {
                response.put("success", false);
                response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 2. 빌더 패턴을 사용하여 RewardParents 객체 생성 및 설정
            RewardParents newRewardParents = RewardParents.builder()
                    .user_id(rewardParentsDTO.getUser_id())
                    .plan_id(reward.getPlan_id())
                    .comment(rewardParentsDTO.getComment())
                    .grade(rewardParentsDTO.getGrade())
                    .is_rewarded(true) // 항상 보상 지급 상태를 true로 설정
                    .build();

            // 3. RewardParents 저장
            rewardParentsRepository.save(newRewardParents);

            // 4. RewardChild의 보상 지급 상태를 업데이트하고 저장
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
     * @param id 조회할 RewardParents의 ID
     * @return 조회된 RewardParents 객체
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public RewardParents getRewardParents(String id) throws ExecutionException, InterruptedException {
        try {
            return rewardParentsRepository.findEntityByDocumentId(id);
        } catch (Exception e) {
            throw new ExecutionException("보상 조회에 실패했습니다. Error: " + e.getMessage(), e);
        }
    }

    /**
     * 부모님의 보상 코멘트와 별점 수정 기능
     * @param rewardParentsDTO 수정할 RewardParents 객체
     * @return 수정 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> updateRewardParents(RewardParentsDTO rewardParentsDTO) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
            RewardParents existingRewardParents = rewardParentsRepository.findEntityByDocumentId(rewardParentsDTO.getId());

            if (existingRewardParents == null) {
                response.put("success", false);
                response.put("message", "해당 ID의 지급된 보상을 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 빌더 패턴을 사용하여 RewardParents 객체를 새롭게 업데이트
            RewardParents updatedRewardParents = RewardParents.builder()
                    .id(existingRewardParents.getId()) // 기존 ID 유지
                    .user_id(existingRewardParents.getUser_id())
                    .plan_id(existingRewardParents.getPlan_id())
                    .comment(rewardParentsDTO.getComment() != null ? rewardParentsDTO.getComment() : existingRewardParents.getComment())
                    .grade(rewardParentsDTO.getGrade() != 0 ? rewardParentsDTO.getGrade() : existingRewardParents.getGrade())
                    .is_rewarded(true) // 항상 보상 지급 상태를 true로 설정
                    .build();

            rewardParentsRepository.update(updatedRewardParents);

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
