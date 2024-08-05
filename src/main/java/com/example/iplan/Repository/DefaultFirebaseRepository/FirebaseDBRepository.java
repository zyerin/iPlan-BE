package com.example.iplan.Repository.DefaultFirebaseRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FirebaseDBRepository<T, ID> {
    void save(T entity) throws ExecutionException, InterruptedException;
    void update(T entity) throws ExecutionException, InterruptedException;
    void delete(ID id) throws ExecutionException, InterruptedException;
    T findById(ID id) throws ExecutionException, InterruptedException;
    List<T> findAll() throws ExecutionException, InterruptedException;
}
