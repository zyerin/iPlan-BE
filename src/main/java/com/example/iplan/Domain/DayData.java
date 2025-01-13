package com.example.iplan.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.firebase.database.annotations.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 하루의 데이터
 */
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayData {

    @DocumentId
    private String id;

    @NotNull
    private String user_id;

    @JsonFormat(pattern = "yyyy")
    private String year;

    @JsonFormat(pattern = "MM")
    private String month;

    @JsonFormat(pattern = "dd")
    private String date;

    private boolean isReachGoal;

    private List<String> plan_idList;

    private String screenTime_id;

    private String reward_id;
}
