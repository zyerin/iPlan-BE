package com.example.iplan.Repository;

import com.example.iplan.Domain.ScreenTimeOCRResult;
import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@Repository
public class GetScreenTimeOCRRepository extends DefaultFirebaseDBRepository<ScreenTimeOCRResult> {

    public GetScreenTimeOCRRepository(){
        setEntityClass(ScreenTimeOCRResult.class);
        setCollectionName("ScreenTimeOCRResult");
    }

    public ScreenTimeOCRResult findByDate(String user_id, LocalDate targetDate) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();

        CollectionReference collection = firestore.collection("ScreenTimeOCRResult");

        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", user_id)
                .whereEqualTo("date", targetDate)
                .get();

        QuerySnapshot querySnapshot = apiFutureList.get();

        if(!querySnapshot.isEmpty()){
            return querySnapshot.getDocuments().get(0).toObject(ScreenTimeOCRResult.class);
        }else{
            return null;
        }
    }
}
