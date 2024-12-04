package com.example.iplan.Service;

import com.google.api.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CalendarService {

    public ResponseEntity<Map<String, Object>> getAllCalendarData(String yearMonth, String user_id){
        Map<String, Object> response = new HashMap<>();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
