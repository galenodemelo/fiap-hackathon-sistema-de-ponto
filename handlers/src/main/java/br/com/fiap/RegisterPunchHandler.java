package br.com.fiap;

import br.com.fiap.punch.PunchEvent;
import br.com.fiap.punch.dto.PunchRequestDTO;
import br.com.fiap.user.User;
import br.com.fiap.util.DatabaseConnection;
import br.com.fiap.util.LogUtils;
import br.com.fiap.util.ResponseUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
            Map<String, Object> authorizer = apiGatewayProxyRequestEvent.getRequestContext().getAuthorizer();
            Map<String, Object> lambda = (Map) authorizer.get("lambda");
            String principalId = lambda.get("principalId").toString();

            User currentUser = User.findById(Long.valueOf(principalId));
            if (currentUser == null) {
                return ResponseUtils.badRequest("Usuário não encontrado");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            PunchRequestDTO punchRequestDTO = objectMapper.readValue(apiGatewayProxyRequestEvent.getBody(), PunchRequestDTO.class);

            if (punchRequestDTO == null || punchRequestDTO.event() == null) {
                return response.withBody("{message: Evento de registro não informado}").withStatusCode(400);
            }

            savePunch(punchRequestDTO.event(), currentUser.getId());

            return response.withBody("{success: true}").withStatusCode(200);
        } catch (Exception exception) {
            LogUtils.logException(exception);
            return ResponseUtils.internalServerError(exception.getMessage());
        }
    }

    private void savePunch(PunchEvent event, Long userId) {
        try {
            Connection connection = DatabaseConnection.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("insert into punch (user_id, event, punch_date) values (?, ?, ?)");
            preparedStatement.setInt(1, userId.intValue());
            preparedStatement.setString(2, event.toString());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))));
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}