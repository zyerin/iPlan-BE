package com.example.iplan.DTO;

import com.google.firebase.database.annotations.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DayDataDTO {

    private String id;

    @NotNull
    private String user_id;

    private boolean reach_goal;

    private List<PlanChildDTO> planChildDTOList;

    private boolean screenTime_goal;

    private String reward_content;

    private boolean is_reward;
}
