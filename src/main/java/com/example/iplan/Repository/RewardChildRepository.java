package com.example.iplan.Repository;

import com.example.iplan.Domain.Reward;
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
public class RewardRepository extends DefaultFirebaseDBRepository<Reward> {

    public RewardRepository() {
        setEntityClass(Reward.class);
        setCollectionName("rewards");
    }

    /**
     * 특정 사용자 ID와 일치하는 보상 목록을 반환
     * @param userId 사용자 ID
     * @return 해당 사용자의 보상 목록
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<Reward> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection("rewards");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", userId)
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        List<Reward> rewards = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            rewards.add(document.toObject(Reward.class));
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
    public List<Reward> findByDate(String userId, String targetDate) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection("rewards");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", userId)
                .whereEqualTo("postDate", targetDate)
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        List<Reward> rewards = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            rewards.add(document.toObject(Reward.class));
        }

        return rewards;
    }
}
