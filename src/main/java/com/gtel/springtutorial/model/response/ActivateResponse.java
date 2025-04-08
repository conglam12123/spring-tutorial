package com.gtel.springtutorial.model.response;

import lombok.Data;

@Data
public class ActivateResponse {
    private String transactionId;

    private Integer failedAttempt;


}
