package com.example.iplan.Controller;

import com.example.iplan.DTO.PlanCategoryDTO;
import com.example.iplan.Service.PlanCategoryService;
import com.google.firebase.database.annotations.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
@RequiredArgsConstructor
public class PlanCategoryController {

    private final PlanCategoryService planCategoryService;

    @PostMapping("plan-category/addition")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> additionPlanCategory(@RequestBody @NotNull PlanCategoryDTO request, String user_id){
        return planCategoryService.addCategory(user_id, request);
    }

    @GetMapping("plan-category/categoryList")
    @ResponseBody
    public List<PlanCategoryDTO> findAllUserCategory(String user_id) throws ExecutionException, InterruptedException {
        return planCategoryService.findAllPlanCategory(user_id);
    }

    @PatchMapping("plan-category/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePlanCategory(String user_id, PlanCategoryDTO planCategoryDTO) throws ExecutionException, InterruptedException {
        return planCategoryService.updatePlanCategory(user_id, planCategoryDTO);
    }

    @DeleteMapping("plan-category/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePlanCategory(String documentID) throws ExecutionException, InterruptedException {
        return planCategoryService.deletePlanCategory(documentID);
    }
}
