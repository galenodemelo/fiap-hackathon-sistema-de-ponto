package br.com.fiap.punch.dto;

import br.com.fiap.util.HourSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@JsonDeserialize
public class PunchDateResponseDTO {

    @JsonFormat(pattern="dd/MM/yyyy")
    private Date date;

    private List<PunchResponseDTO> punches;

    @JsonSerialize(using = HourSerializer.class)
    private long total;

    public PunchDateResponseDTO(Date date, List<PunchResponseDTO> punches) {
        this.date = date;
        this.punches = punches;
        this.total = calculateTotalHours();
    }

    private long calculateTotalHours() {
        long total = 0;

        Timestamp initialDate = null;
        Timestamp finalDate = null;

        for (PunchResponseDTO punchResponseDTO : this.punches) {
            if (initialDate == null) {
                initialDate = punchResponseDTO.getPunch();
                continue;
            }

            finalDate = punchResponseDTO.getPunch();

            total += finalDate.getTime() - initialDate.getTime();

            initialDate = null;
            finalDate = null;
        }

        return total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<PunchResponseDTO> getPunches() {
        return punches;
    }

    public void setPunches(List<PunchResponseDTO> punches) {
        this.punches = punches;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}