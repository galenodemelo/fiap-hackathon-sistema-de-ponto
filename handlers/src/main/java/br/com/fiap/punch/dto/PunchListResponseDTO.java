package br.com.fiap.punch.dto;

import java.util.List;

public record PunchListResponseDTO(List<PunchDateResponseDTO> dates) {
}