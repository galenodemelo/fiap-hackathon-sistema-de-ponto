package br.com.fiap.report.dto;

import java.util.Date;

public record ReportQueueDTO(String email, Date startDate, Date endDate) {
}
