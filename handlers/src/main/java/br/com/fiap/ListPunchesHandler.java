package br.com.fiap;

import br.com.fiap.punch.dto.PunchDateResponseDTO;
import br.com.fiap.punch.dto.PunchListResponseDTO;
import br.com.fiap.punch.dto.PunchResponseDTO;
import br.com.fiap.user.User;
import br.com.fiap.util.DatabaseConnection;
import br.com.fiap.util.DateUtils;
import br.com.fiap.util.LogUtils;
import br.com.fiap.util.ResponseUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Handler for requests to Lambda function.
 */
public class ListPunchesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        try {
            Map<String, Object> authorizer = input.getRequestContext().getAuthorizer();
            Map<String, Object> lambda = (Map) authorizer.get("lambda");
            String principalId = lambda.get("principalId").toString();

            User currentUser = User.findById(Long.valueOf(principalId));
            if (currentUser == null) {
                return ResponseUtils.badRequest("Usuário não encontrado");
            }

            ResultSet resultSet = getPunchResultSet(currentUser.getId());
            Map<Date, List<PunchResponseDTO>> punches = new HashMap<>();

            while (resultSet.next()) {
                Timestamp punchDate = resultSet.getTimestamp("punch_date");
                String event = resultSet.getString("event");

                Date date = new Date(DateUtils.clearTime(punchDate));
                if (!punches.containsKey(date)) punches.put(date, new ArrayList<>());

                punches.get(date).add(new PunchResponseDTO(punchDate, event));
            }

            List<PunchDateResponseDTO> punchDates = new ArrayList<>();
            Iterator<Map.Entry<Date, List<PunchResponseDTO>>> iterator = punches.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Date, List<PunchResponseDTO>> entry = iterator.next();
                punchDates.add(new PunchDateResponseDTO(entry.getKey(), entry.getValue()));
            }

            Collections.sort(punchDates, new Comparator<PunchDateResponseDTO>() {
                @Override
                public int compare(PunchDateResponseDTO o1, PunchDateResponseDTO o2) {
                    return o1.getDate().getTime() < o2.getDate().getTime() ? -1 : 1;
                }
            });

            PunchListResponseDTO punchListResponseDTO = new PunchListResponseDTO(punchDates);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(punchListResponseDTO);

            return response.withStatusCode(200).withBody(json);
        } catch (Exception exception) {
            LogUtils.logException(exception);
            return ResponseUtils.internalServerError(exception.getMessage());
        }
    }

    private ResultSet getPunchResultSet(Long userId) {
        try {
            final Integer retroactiveNumberOfDays = 30;
            LocalDateTime finalDate = LocalDate.now().atStartOfDay().plusDays(1);
            LocalDateTime initialDate = finalDate.minusDays(retroactiveNumberOfDays);

            Connection connection = DatabaseConnection.getConnection();

            final String query = "select punch_date, event from punch where user_id = ? and punch_date >= ? and punch_date < ? order by punch_date";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId.intValue());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(initialDate));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(finalDate));

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet;
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}