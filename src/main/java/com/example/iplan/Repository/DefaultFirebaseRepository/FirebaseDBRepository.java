package com.example.iplan.Repository.DefaultFirebaseRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

// 데이터베이스 작업의 기본 인터페이스를 정의
public interface FirebaseDBRepository<T, ID> {
    void save(T entity) throws ExecutionException, InterruptedException;
    void update(T entity) throws ExecutionException, InterruptedException;
    void delete(ID id) throws ExecutionException, InterruptedException;
    T findById(ID id) throws ExecutionException, InterruptedException;

    // 특정 ID를 기반으로 모든 엔티티를 조회하는 메서드
    List<T> findAll(ID id) throws ExecutionException, InterruptedException;
}
