package com.example.iplan.Service;

import com.example.iplan.DTO.RewardChildDTO;
import com.example.iplan.Domain.RewardChild;
import com.example.iplan.Domain.RewardParents;
import com.example.iplan.Repository.RewardChildRepository;
import com.example.iplan.Repository.RewardParentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
public class RewardChildService {

    private final RewardChildRepository rewardRepository;
    private final RewardParentsRepository rewardParentsRepository;

    /**
     * 새로운 보상을 저장하는 기능
     * @param rewardDto 저장할 보상 객체의 DTO
     * @return 저장 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> saveReward(RewardChildDTO rewardDto) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        // 빌더 패턴을 사용하여 RewardChild 객체 생성
        RewardChild reward = RewardChild.builder()
                .user_id(rewardDto.getUser_id())
                .content(rewardDto.getContent())
                .date(rewardDto.getDate())
                .plan_id(rewardDto.getPlan_id())
                .is_rewarded(rewardDto.is_rewarded())
                .build();

        try {
            rewardRepository.save(reward);
            response.put("success", true);
            response.put("message", "보상이 정상적으로 저장되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "보상 저장에 실패했습니다. Error: " + e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 보상을 ID로 조회하는 기능
     * @param id 검색할 보상의 ID
     * @return 검색된 보상 객체
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public RewardChild getReward(String id) throws ExecutionException, InterruptedException {
        try {
            return rewardRepository.findEntityByDocumentId(id);
        } catch (Exception e) {
            throw new ExecutionException("보상 조회에 실패했습니다. Error: " + e, e);
        }
    }

    /**
     * 보상을 ID로 삭제하는 기능
     * @param documentID 삭제할 보상의 ID
     * @return 삭제 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> deleteReward(String documentID) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
            // 해당 ID의 보상을 조회
            RewardChild reward = rewardRepository.findEntityByDocumentId(documentID);
            if (reward == null) {
                response.put("success", false);
                response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 보상이 이미 지급된 경우 (is_rewarded 가 true) 삭제를 허용하지 않음
            if (reward.is_rewarded()) {
                response.put("success", false);
                response.put("message", "해당 보상은 이미 지급되어 삭제할 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // 지급되지 않은 보상만 삭제 허용
            RewardChild rewardChild = rewardRepository.findEntityByDocumentId(documentID);
            rewardRepository.delete(rewardChild);
            response.put("success", true);
            response.put("message", "보상이 정상적으로 삭제되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "보상 삭제에 실패했습니다. Error: " + e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 기존 보상을 수정하는 기능
     * @param rewardDto 수정할 보상 객체의 DTO
     * @return 수정 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> updateReward(String user_id, RewardChildDTO rewardDto) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        RewardChild existingReward = rewardRepository.findEntityByDocumentId(rewardDto.getId());

        if (existingReward == null) {
            response.put("success", false);
            response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // RewardParents에서 같은 plan_id를 가진 문서를 찾는다.
        List<RewardParents> rewardParentsList = rewardParentsRepository.findByPlanId(user_id, rewardDto.getPlan_id());

        // 같은 plan_id를 가진 RewardParents 중에서 is_rewarded가 true인 경우 수정할 수 없다.
        boolean isAnyRewarded = rewardParentsList.stream().anyMatch(RewardParents::is_rewarded);
        if (isAnyRewarded) {
            response.put("success", false);
            response.put("message", "해당 계획에 대한 부모님의 보상이 이미 지급되어 수정할 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // 빌더 패턴을 사용하여 새롭게 RewardChild 객체를 생성하고 업데이트
        RewardChild updatedReward = RewardChild.builder()
                .id(existingReward.getId()) // 기존 ID 유지
                .user_id(rewardDto.getUser_id() != null ? rewardDto.getUser_id() : existingReward.getUser_id())
                .content(rewardDto.getContent() != null ? rewardDto.getContent() : existingReward.getContent())
                .date(rewardDto.getDate() != null ? rewardDto.getDate() : existingReward.getDate())
                .plan_id(rewardDto.getPlan_id() != null ? rewardDto.getPlan_id() : existingReward.getPlan_id())
                .is_rewarded(rewardDto.is_rewarded())
                .build();

        try {
            rewardRepository.update(updatedReward);
            response.put("success", true);
            response.put("message", "보상이 정상적으로 수정되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "보상 수정에 실패했습니다. Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 한 달간 달성한 보상 수를 계산 -> is_rewarded 가 true 인 것만 계산
     * @param userId 사용자 ID
     * @param year 해당 연도
     * @param month 해당 월 (1월은 1, 12월은 12)
     * @return 한 달간 달성한 보상 수
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public int countMonthlyRewards(String userId, int year, int month) throws ExecutionException, InterruptedException {
        // 해당 달의 첫 번째 날짜와 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 모든 보상 가져오기
        List<RewardChildDTO> rewards = rewardRepository.findRewardChildDtoByUserId(userId);

        // 보상 중에서 해당 기간에 달성된 보상만 필터링하여 계산
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return (int) rewards.stream()
                .filter(reward -> {
                    LocalDate rewardDate = LocalDate.parse(reward.getDate(), formatter);
                    return !rewardDate.isBefore(startDate) && !rewardDate.isAfter(endDate) && reward.is_rewarded();
                })
                .count();
    }

    /**
     * 제네릭 함수를 정의하여 필드 업데이트 처리
     * @param newValue 새로 들어오는 값이 null이 아니라면(수정된 값이라면)
     * @param setter 필드 값을 설정하는 Consumer 함수
     * @param <T> 필드의 데이터 타입
     */
    private <T> void updateIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }
}
