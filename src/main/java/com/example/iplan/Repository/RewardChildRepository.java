package com.example.iplan.Repository;

import com.example.iplan.Domain.RewardChild;
import com.example.iplan.DTO.RewardChildDTO;
import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class RewardChildRepository extends DefaultFirebaseDBRepository<RewardChild> {

    public RewardChildRepository() {
        setEntityClass(RewardChild.class);
        setCollectionName("RewardChild");
    }

    /**
     * 특정 사용자 ID와 일치하는 보상 목록을 반환
     * @param userId 사용자 ID
     * @return 해당 사용자의 보상 목록
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<RewardChildDTO> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection("RewardChild");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", userId)
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        List<RewardChildDTO> rewards = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            RewardChild rewardChild = document.toObject(RewardChild.class);
            RewardChildDTO rewardChildDTO = convertToDTO(rewardChild);
            rewards.add(rewardChildDTO);
        }

        return rewards;
    }

    /**
     * 특정 날짜와 일치하는 보상 목록을 반환
     * @param targetDate 특정 날짜
     * @return 해당 날짜의 보상 목록
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<RewardChildDTO> findByDate(String userId, String targetDate) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection("RewardChild");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", userId)
                .whereEqualTo("date", targetDate)
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        List<RewardChildDTO> rewards = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            RewardChild rewardChild = document.toObject(RewardChild.class);
            RewardChildDTO rewardChildDTO = convertToDTO(rewardChild);
            rewards.add(rewardChildDTO);
        }

        return rewards;
    }

    /**
     * RewardChild 엔티티를 RewardChildDTO 로 변환
     * @param rewardChild RewardChild 엔티티
     * @return RewardChildDTO
     */
    private RewardChildDTO convertToDTO(RewardChild rewardChild) {
        return RewardChildDTO.builder()
                .id(rewardChild.getId())
                .user_id(rewardChild.getUser_id())
                .content(rewardChild.getContent())
                .date(rewardChild.getDate())
                .is_rewarded(rewardChild.is_rewarded())
                .build();
    }
}