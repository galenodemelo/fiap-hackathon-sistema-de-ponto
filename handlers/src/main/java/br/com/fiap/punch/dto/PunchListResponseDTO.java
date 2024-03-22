package br.com.fiap.punch.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public class PunchListResponseDTO {

    List<PunchDateResponseDTO> dates;

    public PunchListResponseDTO(List<PunchDateResponseDTO> dates) {
        this.dates = dates;
    }

    public List<PunchDateResponseDTO> getDates() {
        return dates;
    }

    public void setDates(List<PunchDateResponseDTO> dates) {
        this.dates = dates;
    }
}