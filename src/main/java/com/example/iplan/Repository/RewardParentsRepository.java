package com.example.iplan.Repository;

import com.example.iplan.Domain.RewardParents;
import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class RewardParentsRepository extends DefaultFirebaseDBRepository<RewardParents> {

    public RewardParentsRepository() {
        setEntityClass(RewardParents.class);
        setCollectionName("RewardParents"); // Firestore에서 저장할 컬렉션 이름 설정
    }


     // 특정 사용자 ID와 일치하는 보상 부모 목록을 반환
    public List<RewardParents> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection("RewardParents");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", userId)  // 필드 이름을 정확히 설정해야 함
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        List<RewardParents> rewardParents = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            rewardParents.add(document.toObject(RewardParents.class));
        }

        return rewardParents;
    }


     // 특정 계획 ID와 일치하는 보상 부모 목록을 반환
    public List<RewardParents> findByPlanId(String planId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection("RewardParents");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("plan_id", planId)  // planId 필드를 기준으로 필터링
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        List<RewardParents> rewardParents = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            rewardParents.add(document.toObject(RewardParents.class));
        }

        return rewardParents;
    }






}


