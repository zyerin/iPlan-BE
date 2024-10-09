package com.example.iplan.Repository;

import com.example.iplan.Domain.RewardParent;
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
public class RewardParentRepository extends DefaultFirebaseDBRepository<RewardParent> {

    public RewardParentRepository() {
        setEntityClass(RewardParent.class);
        setCollectionName("RewardParent"); // Firestore에서 저장할 컬렉션 이름 설정
    }

    /**
     * 특정 사용자 ID와 일치하는 보상 부모 목록을 반환
     * @param userId 사용자 ID
     * @return 해당 사용자의 보상 부모 목록
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<RewardParent> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection("RewardParent");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", userId)  // 필드 이름을 정확히 설정해야 함
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        List<RewardParent> rewardParents = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            rewardParents.add(document.toObject(RewardParent.class));
        }

        return rewardParents;
    }

    /**
     * 특정 계획 ID와 일치하는 보상 부모 목록을 반환
     * 특정 planId와 일치하는 모든 RewardParent 문서를 Firestore에서 조회하여 반환
     * @param planId 계획 ID
     * @return 해당 계획에 대한 보상 부모 목록
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<RewardParent> findByPlanId(String planId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection("RewardParent");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("plan_id", planId)  // planId 필드를 기준으로 필터링
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        List<RewardParent> rewardParents = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            rewardParents.add(document.toObject(RewardParent.class));
        }

        return rewardParents;
    }
}
