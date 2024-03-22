package br.com.fiap.punch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@JsonDeserialize
public class PunchDateResponseDTO {

    @JsonFormat(pattern="dd/MM/yyyy")
    private Date date;
    private List<PunchResponseDTO> punches;
//    private Double total;

    public PunchDateResponseDTO(Date date, List<PunchResponseDTO> punches) {
        this.date = date;
        this.punches = punches;
//        this.total = calculateTotalHours();
    }

    private Double calculateTotalHours() {
        Double total = 0D;

        Timestamp initialDate = null;
        Timestamp finalDate = null;

        for (PunchResponseDTO punchResponseDTO : this.punches) {
            if (initialDate == null) {
                initialDate = punchResponseDTO.getPunch();
                continue;
            }

            finalDate = punchResponseDTO.getPunch();

            long difference = finalDate.getTime() - initialDate.getTime();

            total += difference * 1000 * 60 * 60;
        }

        return total;
    }
}