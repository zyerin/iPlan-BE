package com.example.iplan.Service;

import com.example.iplan.DTO.RewardChildDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Service
public class RewardChildService {

    private final RewardChildRepository rewardRepository;
    private final RewardParentsRepository rewardParentsRepository;

    @Autowired
    public RewardChildService(RewardChildRepository rewardRepository, RewardParentsRepository rewardParentsRepository) {
        this.rewardRepository = rewardRepository;
        this.rewardParentsRepository = rewardParentsRepository;
    }

    // 새로운 보상을 저장하는 기능
    public ResponseEntity<Map<String, Object>> saveReward(RewardChildDTO rewardDto) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        // 빌더 패턴을 사용하여 RewardChild 객체 생성
        RewardChild reward = RewardChild.builder()
                .user_id(rewardDto.getUser_id())
                .content(rewardDto.getContent())
                .date(rewardDto.getDate())
                .plan_id(rewardDto.getPlan_id())
                .rewarded(false)
                .success(rewardDto.isSuccess())
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

    // 보상을 ID로 조회하는 기능
    public RewardChild getReward(String id) throws ExecutionException, InterruptedException {
        try {
            return rewardRepository.findById(id);
        } catch (Exception e) {
            throw new ExecutionException("보상 조회에 실패했습니다. Error: " + e, e);
        }
    }

    // 보상을 ID로 삭제하는 기능
    public ResponseEntity<Map<String, Object>> deleteReward(String id) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
            // 해당 ID의 보상을 조회
            RewardChild reward = rewardRepository.findById(id);
            if (reward == null) {
                response.put("success", false);
                response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 보상이 이미 지급된 경우 (rewarded 가 true) 삭제를 허용하지 않음
            if (reward.isRewarded()) {
                response.put("success", false);
                response.put("message", "해당 보상은 이미 지급되어 삭제할 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // 지급되지 않은 보상만 삭제 허용
            rewardRepository.delete(id);
            response.put("success", true);
            response.put("message", "보상이 정상적으로 삭제되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "보상 삭제에 실패했습니다. Error: " + e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 기존 보상을 수정하는 기능
    public ResponseEntity<Map<String, Object>> updateReward(RewardChildDTO rewardDto) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        RewardChild existingReward = rewardRepository.findById(rewardDto.getId());

        if (existingReward == null) {
            response.put("success", false);
            response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // RewardParents에서 같은 plan_id를 가진 문서를 찾는다.
        List<RewardParents> rewardParentsList = rewardParentsRepository.findByPlanId(rewardDto.getPlan_id());

        // 같은 plan_id를 가진 RewardParents 중에서 rewarded가 true인 경우 수정할 수 없다.
        if (existingReward.isRewarded()) {
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
                .rewarded(false)
                .success(false)
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

    // 한 달간 작성한 모든 보상 수
    public int countMonthlyTotalRewarded(String userId, int year, int month) throws ExecutionException, InterruptedException {
        // 해당 달의 첫 번째 날짜와 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 모든 보상 가져오기
        List<RewardChildDTO> rewards = rewardRepository.findByUserId(userId);

        // 보상 중에서 해당 기간에 달성된 보상만 필터링하여 계산
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return (int) rewards.stream()
                .filter(reward -> {
                    LocalDate rewardDate = LocalDate.parse(reward.getDate(), formatter);
                    return !rewardDate.isBefore(startDate) && !rewardDate.isAfter(endDate);
                })
                .count();
    }

    // 한 달간 달성한 보상 수를 계산 -> success 가 true 인 것만 계산
    public int countMonthlySuccessRewarded(String userId, int year, int month) throws ExecutionException, InterruptedException {
        // 해당 달의 첫 번째 날짜와 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 모든 보상 가져오기
        List<RewardChildDTO> rewards = rewardRepository.findByUserId(userId);

        // 보상 중에서 해당 기간에 success 가 true 인 달성된 보상만 필터링하여 계산
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return (int) rewards.stream()
                .filter(reward -> {
                    LocalDate rewardDate = LocalDate.parse(reward.getDate(), formatter);
                    return !rewardDate.isBefore(startDate) && !rewardDate.isAfter(endDate) && reward.isSuccess();
                })
                .count();
    }

    // 한달 간 첨삭이 완료된 보상 수를 계산 -> rewarded 가 true 인 것만 계산
    public int countMonthlyRewarded(String userId, int year, int month) throws ExecutionException, InterruptedException {
        // 해당 달의 첫 번째 날짜와 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 모든 보상 가져오기
        List<RewardChildDTO> rewards = rewardRepository.findByUserId(userId);

        // 보상 중에서 해당 기간에 rewarded 가 true 인 달성된 보상만 필터링하여 계산
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return (int) rewards.stream()
                .filter(reward -> {
                    LocalDate rewardDate = LocalDate.parse(reward.getDate(), formatter);
                    return !rewardDate.isBefore(startDate) && !rewardDate.isAfter(endDate) && reward.isRewarded();
                })
                .count();
    }

    // 첨삭이 완료되지 않은 보상 수를 계산 -> rewarded 가 false 인 것만 계산
    public int countMonthlyNotRewarded(String userId, int year, int month) throws ExecutionException, InterruptedException {
        // 해당 달의 첫 번째 날짜와 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 모든 보상 가져오기
        List<RewardChildDTO> rewards = rewardRepository.findByUserId(userId);

        // 보상 중에서 해당 기간에 rewarded 가 true 인 달성된 보상만 필터링하여 계산
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return (int) rewards.stream()
                .filter(reward -> {
                    LocalDate rewardDate = LocalDate.parse(reward.getDate(), formatter);
                    return !rewardDate.isBefore(startDate) && !rewardDate.isAfter(endDate) && !reward.isRewarded();
                })
                .count();
    }



    // 한 달 동안의 첨삭된 보상 목록을 조회하는 기능 -> rewarded 가 true 인 것만 조회
    public List<RewardChildDTO> listMonthlyRewarded(String userId, int year, int month) throws ExecutionException, InterruptedException {
        // 해당 달의 첫 번째 날짜와 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 모든 보상 가져오기
        List<RewardChildDTO> rewards = rewardRepository.findByUserId(userId);

        // 보상 중에서 해당 기간에 포함되는 rewarded 가 true 인 보상만 필터링하여 리스트 반환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return rewards.stream().filter(reward -> {
                    LocalDate rewardDate = LocalDate.parse(reward.getDate(), formatter);
                    return !rewardDate.isBefore(startDate) && !rewardDate.isAfter(endDate) && reward.isRewarded();
                })
                .toList();

    }

    // 한 달 동안의 아직 첨삭되지 않은 보상 목록을 조회 -> rewarded 가 false 인 것만 조회
    public List<RewardChildDTO> listMonthlyNotRewarded(String userId, int year, int month) throws ExecutionException, InterruptedException {
        // 해당 달의 첫 번째 날짜와 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 모든 보상 가져오기
        List<RewardChildDTO> rewards = rewardRepository.findByUserId(userId);

        // 보상 중에서 해당 기간에 포함되는 rewarded 가 false 인 보상만 필터링하여 리스트 반환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return rewards.stream().filter(reward -> {
                    LocalDate rewardDate = LocalDate.parse(reward.getDate(), formatter);
                    return !rewardDate.isBefore(startDate) && !rewardDate.isAfter(endDate) && !reward.isRewarded();
                })
                .toList();

    }

}
