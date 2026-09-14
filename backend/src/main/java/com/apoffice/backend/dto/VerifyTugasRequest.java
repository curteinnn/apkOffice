package com.apoffice.backend.dto;

import java.math.BigDecimal;

public class VerifyTugasRequest {

    private String status;
    private BigDecimal nominal;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getNominal() {
        return nominal;
    }

    public void setNominal(BigDecimal nominal) {
        this.nominal = nominal;
    }
}