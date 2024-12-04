package com.example.iplan.Repository;

import com.example.iplan.DTO.PlanChildDTO;
import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import com.example.iplan.Domain.PlanChild;
import com.example.iplan.Service.PlanChildService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class PlanChildRepository extends DefaultFirebaseDBRepository<PlanChild> {

    public PlanChildRepository() {
        setEntityClass(PlanChild.class);
        setCollectionName("PlanChild");
    }

    public List<PlanChild> findByDate(String user_id, LocalDate targetDate) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        //어떤 컬렉션인지 객체 가져옴
        CollectionReference collection = firestore.collection("PlanChild");

        // 특정 날짜와 일치하는 문서들에 대해 쿼리
        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", user_id) // 사용자의 user_id로 필터링
                .whereEqualTo("postDate", targetDate) // 특정 날짜로 필터링, targetDate는 "YYYY-MM-DD" 형식의 문자열이라고 가정
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        List<PlanChild> plans = new ArrayList<>();

        // 쿼리 결과의 모든 문서를 PlanChild 객체로 변환하여 리스트에 추가
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            plans.add(document.toObject(PlanChild.class));
        }

        return plans; // 일치하는 모든 PlanChild 문서를 포함하는 리스트 반환
    }

    public List<PlanChildDTO> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection("PlanChild");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", userId)
                .whereGreaterThanOrEqualTo("postDate", startDate.toString())
                .whereLessThanOrEqualTo("postDate", endDate.toString())
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();
        List<PlanChildDTO> plans = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            PlanChildDTO plan = document.toObject(PlanChildDTO.class);
            plans.add(plan);
        }

        return plans;
    }

}
