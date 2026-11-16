package com.apoffice.backend.dto;

import java.math.BigDecimal;
import java.util.Map;

public class VerifyTugasRequest {

    private String status;
    private Map<Long, BigDecimal> nominalByAssignee;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<Long, BigDecimal> getNominalByAssignee() {
        return nominalByAssignee;
    }

    public void setNominalByAssignee(Map<Long, BigDecimal> nominalByAssignee) {
        this.nominalByAssignee = nominalByAssignee;
    }
}
