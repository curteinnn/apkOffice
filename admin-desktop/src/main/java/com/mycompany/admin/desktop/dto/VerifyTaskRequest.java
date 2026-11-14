package com.mycompany.admin.desktop.dto;

import java.math.BigDecimal;

public record VerifyTaskRequest(String status, BigDecimal nominal) {}
