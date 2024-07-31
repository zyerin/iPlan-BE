package com.example.iplan.Repository.DefaultFirebaseRepository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

@Component
@Repository
public class DefaultFirebaseDBRepository<T> implements FirebaseDBRepository<T, String> {

    private Class<T> entityClass;
    private String collectionName;

    public void setEntityClass(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    public void setCollectionName(String collectionName){
        this.collectionName = collectionName;
    }

    @Override
    public void save(T entity) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        //Firestore 인스턴스에서 지정한 collectionName을 참조하는 CollectionReference객체를 가져온다
        //이 객체는 지정된 컬렉션의 문서에 접근하거나 조작할 수 있는 메서드 제공
        CollectionReference collection = firestore.collection(collectionName);
        ApiFuture<WriteResult> result = collection.document(getDocumentId(entity)).set(entity);
        result.get(); // 작성이 완료될때까지 Block
    }

    @Override
    public void update(T entity) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection(collectionName);
        ApiFuture<WriteResult> result = collection.document(getDocumentId(entity)).set(entity);
        result.get();
    }

    @Override
    public void delete(String id) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collection = firestore.collection(collectionName);
        ApiFuture<WriteResult> result = collection.document(id).delete();
        result.get();
    }

    @Override
    public T findById(String id) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        //어떤 컬렉션인지 객체 가져옴
        CollectionReference collection = firestore.collection(collectionName);
        //id에 해당하는 문서(데이터)를 가져온다
        ApiFuture<DocumentSnapshot> apiFuture = collection.document(id).get();
        DocumentSnapshot documentSnapshot = apiFuture.get();

        //문서(데이터)가 있다면 entity 객체로 가져온다.
        if(documentSnapshot.exists()){
            return documentSnapshot.toObject(entityClass);
        }

        return null;
    }

    /**
     * entity 객체에서 문서 ID를 추출한다.
     * entity가 DocumentId를 포함하는 필드가 있다면 해당 필드에서 ID를 가져온다.
     * @param entity 추출하고 싶은 ID의 객체
     * @return ID값
     */
    private String getDocumentId(T entity) {
        try{
            Method getIdMethod = entity.getClass().getMethod("getId");
            return (String) getIdMethod.invoke(entity);
        } catch(Exception e){
            throw new RuntimeException("Failed to get document ID", e);
        }
    }
}
