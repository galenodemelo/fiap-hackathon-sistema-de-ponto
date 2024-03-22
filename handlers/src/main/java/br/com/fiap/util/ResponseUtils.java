package br.com.fiap.util;

import br.com.fiap.handler.dto.ResponseDTO;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ResponseUtils {

    public static APIGatewayProxyResponseEvent ok(String message) throws JsonProcessingException {
        return respond(true, message, 200);
    }

    public static APIGatewayProxyResponseEvent badRequest(String message) throws JsonProcessingException {
        return respond(false, message, 400);
    }

    private static APIGatewayProxyResponseEvent respond(Boolean success, String message, Integer statusCode) throws JsonProcessingException {
        String body = new ResponseDTO(message, success).toJson();

        return new APIGatewayProxyResponseEvent()
            .withBody(body)
            .withStatusCode(statusCode);
    }
}
