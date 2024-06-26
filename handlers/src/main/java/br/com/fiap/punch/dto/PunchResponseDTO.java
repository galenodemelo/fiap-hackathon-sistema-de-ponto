package br.com.fiap.punch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.sql.Timestamp;

@JsonDeserialize
public class PunchResponseDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="HH:mm")
    private Timestamp punch;
    private String event;

    public PunchResponseDTO(Timestamp punch, String event) {
        this.punch = punch;
        this.event = event;
    }

    public Timestamp getPunch() {
        return punch;
    }

    public String getEvent() {
        return event;
    }

    public void setPunch(Timestamp punch) {
        this.punch = punch;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}