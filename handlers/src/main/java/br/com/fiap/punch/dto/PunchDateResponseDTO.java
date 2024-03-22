package br.com.fiap.punch.dto;

import java.util.Date;
import java.util.List;

public record PunchDateResponseDTO(Date date, List<PunchResponseDTO> punches, Double total) {
}