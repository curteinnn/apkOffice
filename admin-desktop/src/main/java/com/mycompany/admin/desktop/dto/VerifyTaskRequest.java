package com.mycompany.admin.desktop.dto;

import java.math.BigDecimal;
import java.util.Map;

public record VerifyTaskRequest(String status, Map<Long, BigDecimal> nominalByAssignee) {}
