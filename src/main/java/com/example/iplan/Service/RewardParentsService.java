package com.example.iplan.Service;

import com.example.iplan.DTO.RewardChildDTO;
import com.example.iplan.DTO.RewardParentsDTO;
import com.example.iplan.Domain.RewardChild;
import com.example.iplan.Domain.RewardParents;
import com.example.iplan.Repository.RewardChildRepository;
import com.example.iplan.Repository.RewardParentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class RewardParentsService {

    private final RewardParentsRepository rewardParentsRepository;
    private final RewardChildRepository rewardChildRepository;

    @Autowired
    public RewardParentsService(RewardParentsRepository rewardParentsRepository, RewardChildRepository rewardChildRepository) {
        this.rewardParentsRepository = rewardParentsRepository;
        this.rewardChildRepository = rewardChildRepository;
    }

    // 부모님의 보상 코멘트와 별점, 보상 지급 여부를 저장하는 기능 - 1) 보상을 지급 or 2) 보상을 보류
    public ResponseEntity<Map<String, Object>> saveRewardParents(RewardParentsDTO rewardParentsDTO, String rewardId) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. RewardChild 엔티티에서 planId 추출
            RewardChild reward = rewardChildRepository.findById(rewardId);
            if (reward == null) {
                response.put("success", false);
                response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 2. 빌더 패턴을 사용하여 RewardParents 객체 생성 및 설정
            RewardParents newRewardParents = RewardParents.builder()
                    .user_id(rewardParentsDTO.getUser_id())
                    .plan_id(reward.getPlan_id())
                    .reward_id(rewardId)
                    .comment(rewardParentsDTO.getComment())
                    .grade(rewardParentsDTO.getGrade())
                    .rewarded(true) // 항상 true로 설정
                    .success(rewardParentsDTO.isSuccess()) // is_success 값을 받아 그대로 저장
                    .build();

            // 3. RewardParents 저장
            rewardParentsRepository.save(newRewardParents);

            // 4. RewardChild의 보상 지급 상태와 여부를 업데이트하고 저장
            reward.setRewarded(true);
            reward.setSuccess(rewardParentsDTO.isSuccess());
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

    // 부모님 코멘트와 별점을 조회하는 기능
    public RewardParents getRewardParents(String id) throws ExecutionException, InterruptedException {
        try {
            return rewardParentsRepository.findById(id);
        } catch (Exception e) {
            throw new ExecutionException("보상 조회에 실패했습니다. Error: " + e.getMessage(), e);
        }
    }

    // 부모님의 보상 코멘트와 별점 수정 기능
    public ResponseEntity<Map<String, Object>> updateRewardParents(RewardParentsDTO rewardParentsDTO) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
            RewardParents existingRewardParents = rewardParentsRepository.findById(rewardParentsDTO.getId());

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
                    .reward_id(existingRewardParents.getReward_id())
                    .comment(rewardParentsDTO.getComment() != null ? rewardParentsDTO.getComment() : existingRewardParents.getComment())
                    .grade(rewardParentsDTO.getGrade() != 0 ? rewardParentsDTO.getGrade() : existingRewardParents.getGrade())
                    .rewarded(true) // 항상 보상 지급 상태를 true로 설정
                    .success(rewardParentsDTO.isSuccess())  // 보상을 회수하는 경우도 가능
                    .build();


            rewardParentsRepository.update(updatedRewardParents);

            String rewardId = existingRewardParents.getReward_id();
            RewardChild reward = rewardChildRepository.findById(rewardId);
            if (reward == null) {
                response.put("success", false);
                response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            reward.setRewarded(true);
            reward.setSuccess(rewardParentsDTO.isSuccess());
            rewardChildRepository.update(reward);

            response.put("success", true);
            response.put("message", "부모님의 코멘트와 별점이 정상적으로 수정되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "수정에 실패했습니다. Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 한 달간 첨삭이 완료되지 않은 보상 수를 계산 -> rewarded 가 false 인 것만 계산
    public int countMonthlyNotRewarded(String linked_id, int year, int month) throws ExecutionException, InterruptedException {
        // 해당 달의 첫 번째 날짜와 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 모든 보상 가져오기
        List<RewardChildDTO> rewards = rewardChildRepository.findByUserId(linked_id);

        // 보상 중에서 해당 기간에 rewarded 가 true 인 달성된 보상만 필터링하여 계산
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return (int) rewards.stream()
                .filter(reward -> {
                    LocalDate rewardDate = LocalDate.parse(reward.getDate(), formatter);
                    return !rewardDate.isBefore(startDate) && !rewardDate.isAfter(endDate) && !reward.isRewarded();
                })
                .count();
    }

    // 한 달 동안의 아직 첨삭되지 않은 보상 목록을 조회 -> rewarded 가 false 인 것만 조회
    public List<RewardChildDTO> listMonthlyNotRewarded(String linked_id, int year, int month) throws ExecutionException, InterruptedException {
        // 해당 달의 첫 번째 날짜와 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 모든 보상 가져오기
        List<RewardChildDTO> rewards = rewardChildRepository.findByUserId(linked_id);

        // 보상 중에서 해당 기간에 포함되는 rewarded 가 false 인 보상만 필터링하여 리스트 반환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return rewards.stream().filter(reward -> {
                    LocalDate rewardDate = LocalDate.parse(reward.getDate(), formatter);
                    return !rewardDate.isBefore(startDate) && !rewardDate.isAfter(endDate) && !reward.isRewarded();
                })
                .toList();

    }
}
