package com.example.iplan.Service;

import com.example.iplan.DTO.PlanChildDTO;
import com.example.iplan.Domain.PlanChild;
import com.example.iplan.Domain.ScreenTime;
import com.example.iplan.Repository.PlanChildRepository;
import com.example.iplan.Repository.SetScreenTimeRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PlanChildService {

    private final PlanChildRepository planChildRepository;
    private final SetScreenTimeRepository setScreenTimeRepository;

    /**
     * 새로운 계획을 추가하는 기능
     * repository에 값이 저장되면 add()를 통해 DocumentId가 자동생성된다.
     * @param planPostDto
     * @param user_id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> postChildNewPlan(PlanChildDTO planPostDto, String user_id) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();
        String[] dateArr = planPostDto.getPost_date().split("-");

        PlanChild planPost = PlanChild.builder()
                .user_id(user_id)
                .alarm(planPostDto.isAlarm())
                .memo(planPostDto.getMemo())
                .category_id(planPostDto.getCategory_id())
                .title(planPostDto.getTitle())
                .post_year(dateArr[0])
                .post_month(dateArr[1])
                .post_date(dateArr[2])
                .is_completed(planPostDto.is_completed())
                .build();

        if(planPost.getUser_id() != null && !planPost.getUser_id().isEmpty()){
            planChildRepository.save(planPost);
            response.put("success", true);
            response.put("message", "계획이 정상적으로 추가되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else
        {
            response.put("success", false);
            response.put("message", "유저 아이디가 올바르지 않습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 해당 날짜의 추가한 계획 리스트들을 전부 보여주는 기능
     * 엔티티가 아닌 Dto로 넘겨준다.
     * 목표 리스트에서 보여주고 싶은 정보만 넘겨주기 위해서
     * @param targetDate 해당 날짜 "yyyy-MM-dd" 형식
     * @return 해당 날짜 계획 리스트
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<PlanChildDTO> findAllPlanList(String user_id, @JsonFormat(pattern = "yyyy-MM-dd") String targetDate) throws ExecutionException, InterruptedException {
        List<PlanChild> planEntityList = planChildRepository.findByDate(user_id, targetDate);

        ArrayList<PlanChildDTO> planDtoList = new ArrayList<>();

        for(PlanChild plan : planEntityList){
            PlanChildDTO planDto = PlanChildDTO.builder()
                    .id(plan.getId())
                    .title(plan.getTitle())
                    .is_completed(plan.is_completed())
                    .build();

            planDtoList.add(planDto);
        }

        return planDtoList;
    }

    public PlanChild findByPlanID(String documentID) throws ExecutionException, InterruptedException {

        return planChildRepository.findEntityByDocumentId(documentID);
    }

    /**
     * 기존의 계획을 수정한다
     * @param planChildDTO 수정된 계획의 DTO
     * @param user_id 해당 계획 소유자 id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ResponseEntity<Map<String, Object>> updateOriginalPlan(PlanChildDTO planChildDTO, String user_id) throws ExecutionException, InterruptedException {

        Map<String, Object> response = new HashMap<>();

        PlanChild originalPlan = planChildRepository.findEntityByDocumentId(planChildDTO.getId());

        if(!Objects.equals(originalPlan.getUser_id(), user_id))
        {
            response.put("success", false);
            response.put("message", "해당 계획과 사용자가 일치하지 않습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        updateIfNotNull(planChildDTO.getTitle(), originalPlan::setTitle);
        updateIfNotNull(planChildDTO.getMemo(), originalPlan::setMemo);
        updateIfNotNull(planChildDTO.getCategory_id(), originalPlan::setCategory_id);
        updateIfNotNull(planChildDTO.isAlarm(), originalPlan::setAlarm);
        updateIfNotNull(planChildDTO.is_completed(), originalPlan::set_completed);

        try{
            planChildRepository.update(originalPlan);
        }
        catch (Exception e){
            response.put("success", false);
            response.put("message", "계획 업데이트에 실패했습니다. Error: "+ e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("success", true);
        response.put("message", "계획이 정상적으로 업데이트 되었습니다");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> DeletePlan(String document_id) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        try{
            PlanChild plan = planChildRepository.findEntityByDocumentId(document_id);
            planChildRepository.delete(plan);
        }
        catch (Exception e){
            response.put("success", false);
            response.put("message", "계획 삭제에 실패했습니다. Error: "+ e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("success", true);
        response.put("message", "계획이 정상적으로 삭제 되었습니다");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * 사용자가 목표 스크린타임을 설정한다
     * @param screenTime
     * @return
     */
    public ResponseEntity<Map<String, Object>> SetScreenTime(ScreenTime screenTime){
        Map<String, Object> response = new HashMap<>();

        try{
            setScreenTimeRepository.save(screenTime);
        }
        catch(Exception e){
            response.put("success", false);
            response.put("message", "스크린 타임 설정에 실패했습니다. Error: "+ e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("success", true);
        response.put("message", "스크린 타임 정상적으로 설정 되었습니다");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 제네릭 함수를 정의하여 필드 업데이트 처리
     * @param newValue 새로 들어오는 값이 null이 아니라면(수정된 값이라면)
     * @param setter
     * @param <T>
     */
    private <T> void updateIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }
}