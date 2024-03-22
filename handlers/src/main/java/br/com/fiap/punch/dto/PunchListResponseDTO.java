package br.com.fiap.punch.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public class PunchListResponseDTO {

    List<PunchDateResponseDTO> dates;

    public PunchListResponseDTO(List<PunchDateResponseDTO> dates) {
        this.dates = dates;
    }
}