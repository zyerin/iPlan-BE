package com.example.iplan.Repository;

import com.example.iplan.Domain.ScreenTime;
import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class SetScreenTimeRepository extends DefaultFirebaseDBRepository<ScreenTime> {

    public SetScreenTimeRepository(Firestore firestore){
        setEntityClass(ScreenTime.class);
        setCollectionName("ScreenTime");
    }

    public ScreenTime findByDate(String user_id, String targetDate) throws ExecutionException, InterruptedException{
        Firestore firestore = FirestoreClient.getFirestore();

        CollectionReference collection = firestore.collection("ScreenTime");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", user_id)
                .whereEqualTo("date", targetDate)
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        if(!querySnapshot.isEmpty()){
            return querySnapshot.getDocuments().get(0).toObject(ScreenTime.class);
        }else{
            return null;
        }
    }

    public List<ScreenTime> findByUserIdAndMonth(String userId, LocalDate startDate, LocalDate endDate) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        // Firestore의 Collection 참조
        CollectionReference collection = firestore.collection("ScreenTime");

        // 날짜를 문자열로 변환 (Firestore에서 문자열 기반 비교)
        String startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 쿼리 구성
        Query query = collection
                .whereEqualTo("user_id", userId) // 사용자 ID 필터
                .whereGreaterThanOrEqualTo("date", startDateString) // 시작 날짜 필터
                .whereLessThanOrEqualTo("date", endDateString); // 종료 날짜 필터

        // Firestore에서 데이터 가져오기
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        return querySnapshot.get().toObjects(ScreenTime.class);
    }

}