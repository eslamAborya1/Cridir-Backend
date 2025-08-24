package com.NTG.Cridir.DTOs;

import com.NTG.Cridir.model.Enum.Status;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

// Used when we (backend) send data back to client (returning request details to Customer or Provider).
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServiceRequestResponse(
        Long requestId,
        String customerName,
        String providerName,
        String issueType,
        String carType,
        Integer carModelYear,
        Status status,
        BigDecimal totalCost,
        OffsetDateTime requestTime,
        Double latitude,
        Double longitude,
        Long estimatedArrivalSeconds
) {}
