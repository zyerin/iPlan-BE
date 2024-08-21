package com.example.iplan.DTO;

import com.google.firebase.database.annotations.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class PlanChildDTO {

    private String id;
    private String user_id;

    @NotNull
    private String title;
    private boolean alarm;
    private List<String> category_id;
    private String memo;
    private LocalDate post_date;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private boolean is_completed;
}
