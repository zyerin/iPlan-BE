package com.example.iplan.Repository;

import com.example.iplan.Domain.PlanCategory;
import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PlanCategoryRepository extends DefaultFirebaseDBRepository<PlanCategory> {

    public PlanCategoryRepository(){
        setEntityClass(PlanCategory.class);
        setCollectionName("PlanCategory");
    }
}