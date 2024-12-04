package com.example.iplan.Repository;

import com.example.iplan.Domain.ScreenTime;
import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class SetScreenTimeRepository extends DefaultFirebaseDBRepository<ScreenTime> {

    public SetScreenTimeRepository(Firestore firestore){
        super(firestore);
        setEntityClass(ScreenTime.class);
        setCollectionName("ScreenTime");
    }

    public ScreenTime findByDate(String user_id, String targetDate) throws ExecutionException, InterruptedException{
        /*Firestore firestore = FirestoreClient.getFirestore();

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
        }*/
        Map<String, Object> filters = Map.of(
                "user_id", user_id,
                "date", targetDate
        );
        return findByFields(filters);
    }
}
