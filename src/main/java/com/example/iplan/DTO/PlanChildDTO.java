package com.example.iplan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String post_date;

    private boolean is_completed;
}
