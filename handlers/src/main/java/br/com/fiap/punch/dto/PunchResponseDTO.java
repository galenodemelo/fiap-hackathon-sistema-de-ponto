package br.com.fiap.punch.dto;

import br.com.fiap.punch.PunchEvent;

import java.sql.Timestamp;

public record PunchResponseDTO(Timestamp punch, PunchEvent event) {
}