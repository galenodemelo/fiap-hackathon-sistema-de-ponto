package br.com.fiap.handler.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record ResponseDTO(String message, boolean success) {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }

}
