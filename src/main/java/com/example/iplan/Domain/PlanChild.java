package com.example.iplan.Domain;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
public class PlanChild {

    @DocumentId
    private String id; // Firestore 문서의 ID

    private String title;
    private Date postDate;

}
