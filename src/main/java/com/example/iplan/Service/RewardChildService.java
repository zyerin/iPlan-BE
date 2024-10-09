package com.example.iplan.Service;

import com.example.iplan.Domain.Reward;
import com.example.iplan.Repository.RewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;

    @Autowired
    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    /**
     * 새로운 보상을 저장하는 기능
     * @param reward 저장할 보상 객체
     * @return 저장 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> saveReward(Reward reward) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

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
     * 보상을 ID로 검색하는 기능
     * @param id 검색할 보상의 ID
     * @return 검색된 보상 객체
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Reward getReward(String id) throws ExecutionException, InterruptedException {
        try {
            return rewardRepository.findById(id);
        } catch (Exception e) {
            throw new ExecutionException("보상 조회에 실패했습니다. Error: " + e, e);
        }
    }

    /**
     * 보상을 ID로 삭제하는 기능
     * @param id 삭제할 보상의 ID
     * @return 삭제 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> deleteReward(String id) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
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

    /**
     * 기존 보상을 수정하는 기능
     * @param reward 수정할 보상 객체
     * @return 수정 결과
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> updateReward(Reward reward) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try {
            Reward existingReward = rewardRepository.findById(reward.getId());

            if (existingReward == null) {
                response.put("success", false);
                response.put("message", "해당 ID의 보상을 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            updateIfNotNull(reward.getUserId(), existingReward::setUserId);
            updateIfNotNull(reward.getContent(), existingReward::setContent);
            updateIfNotNull(reward.getDate(), existingReward::setDate);
            updateIfNotNull(reward.getPlanId(), existingReward::setPlanId);
            updateIfNotNull(reward.isRewarded(), existingReward::setRewarded);

            rewardRepository.update(existingReward);

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
