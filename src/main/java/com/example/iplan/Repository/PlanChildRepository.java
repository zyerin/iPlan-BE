package com.example.iplan.Repository;

import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import com.example.iplan.Domain.PlanChild;
import org.springframework.stereotype.Repository;

@Repository
public class PlanChildRepository extends DefaultFirebaseDBRepository<PlanChild> {

    public PlanChildRepository(Class<PlanChild> entityClass, String collectionName) {
        super(entityClass, collectionName);
    }
}
