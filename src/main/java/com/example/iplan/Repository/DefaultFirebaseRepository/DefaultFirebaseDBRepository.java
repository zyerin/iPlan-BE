package com.example.iplan.Repository.DefaultFirebaseRepository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Repository
@RequiredArgsConstructor
public class DefaultFirebaseDBRepository<T> implements FirebaseDBRepository<T, String> {

    private Class<T> entityClass;
    private String collectionName;

    protected final Firestore firestore;

    // Entity 클래스 설정 메서드
    public void setEntityClass(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    // 컬렉션 이름 설정 메서드
    public void setCollectionName(String collectionName){
        this.collectionName = collectionName;
    }

    // Entity 저장 메서드
    @Override
    public void save(T entity) throws ExecutionException, InterruptedException {
        CollectionReference collection = firestore.collection(collectionName);
        ApiFuture<DocumentReference> result = collection.add(entity);
        result.get(); // 작성이 완료될때까지 Block
    }

    // Entity 업데이트 메서드
    @Override
    public void update(T entity) throws ExecutionException, InterruptedException {
        CollectionReference collection = firestore.collection(collectionName);
        ApiFuture<WriteResult> result = collection.document(getDocumentId(entity)).set(entity);
        result.get();
    }

    // Entity 삭제 메서드
    @Override
    public void delete(T entity) throws ExecutionException, InterruptedException {
        CollectionReference collection = firestore.collection(collectionName);
        ApiFuture<WriteResult> result = collection.document(getDocumentId(entity)).delete();
        result.get();
    }

    // ID로 Entity 검색 메서드
    @Override
    public T findEntityByDocumentId(String document_id) throws ExecutionException, InterruptedException {
        CollectionReference collection = firestore.collection(collectionName);
        ApiFuture<DocumentSnapshot> apiFuture = collection.document(document_id).get();
        DocumentSnapshot documentSnapshot = apiFuture.get();

        if(documentSnapshot.exists()){
            return documentSnapshot.toObject(entityClass);
        }

        return null;
    }

    // 특정 사용자 ID로 모든 Entity 검색 메서드
    @Override
    public List<T> findEntityAll(String user_id) throws ExecutionException, InterruptedException{
        CollectionReference collection = firestore.collection(collectionName);
        ApiFuture<QuerySnapshot> apiFutureList = collection
                .whereEqualTo("user_id", user_id)
                .get();
        QuerySnapshot querySnapshot = apiFutureList.get();

        if(querySnapshot != null){
            return querySnapshot.toObjects(entityClass);
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
        try {
            Method getIdMethod = entity.getClass().getMethod("getId");
            return (String) getIdMethod.invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get document ID", e);
        }
    }
}
