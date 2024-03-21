package br.com.fiap;

import br.com.fiap.punch.dto.PunchRequestDTO;
import br.com.fiap.util.DatabaseConnection;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class RegisterPunchHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PunchRequestDTO punchRequestDTO = objectMapper.readValue(apiGatewayProxyRequestEvent.getBody(), PunchRequestDTO.class);

            if (punchRequestDTO == null || punchRequestDTO.event() == null) {
                return response.withBody("{message: Evento de registro não informado}").withStatusCode(400);
            }

            Connection connection = DatabaseConnection.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("insert into punch (user_id, event, punch_date) values (?, ?, ?)");
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, punchRequestDTO.event());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))));
            preparedStatement.execute();

            return response.withBody("{success: true}").withStatusCode(200);
        } catch (Exception exception) {
            return response.withStatusCode(500);
        }
    }
}