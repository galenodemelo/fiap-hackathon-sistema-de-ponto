package br.com.fiap.report.dto;

import java.util.Date;

public record AskForReportRequestDTO(Date startDate, Date endDate) {
}
