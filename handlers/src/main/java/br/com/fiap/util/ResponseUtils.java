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

    public static APIGatewayProxyResponseEvent internalServerError(String message) {
        try {
            return respond(false, message, 500);
        } catch (JsonProcessingException exception) {
            LogUtils.logException(exception);
            return null;
        }
    }

    private static APIGatewayProxyResponseEvent respond(Boolean success, String message, Integer statusCode) throws JsonProcessingException {
        String body = new ResponseDTO(message, success).toJson();

        return new APIGatewayProxyResponseEvent()
            .withBody(body)
            .withStatusCode(statusCode);
    }
}
