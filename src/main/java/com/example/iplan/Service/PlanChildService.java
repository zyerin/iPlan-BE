package com.example.iplan.Service;

import com.example.iplan.Domain.PlanChild;
import com.example.iplan.Repository.PlanChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class PlanChildService {
    @Autowired
    private PlanChildRepository planChildRepository;

    public void postChildNewPlan(PlanChild planPost) throws ExecutionException, InterruptedException {
        planChildRepository.save(planPost);
    }
}
