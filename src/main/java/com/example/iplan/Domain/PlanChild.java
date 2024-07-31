package com.example.iplan.Domain;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Getter;

import java.util.Date;

public class PlanChild {

    @Getter
    @DocumentId
    private String id; // Firestore 문서의 ID

    private String title;
    private Date postDate;

}
