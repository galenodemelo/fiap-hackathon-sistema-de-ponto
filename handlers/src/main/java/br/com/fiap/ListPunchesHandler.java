package br.com.fiap;

import br.com.fiap.util.DatabaseConnection;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class ListPunchesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        try {
            ResultSet resultSet = getPunchResultSet();

            while (resultSet.next()) {
                
            }


            return response.withStatusCode(200).withBody("");
        } catch (Exception exception) {
            exception.printStackTrace();
            return response.withStatusCode(500);
        }
    }

    private ResultSet getPunchResultSet() {
        try {
            final Integer retroactiveNumberOfDays = 30;
            LocalDateTime finalDate = LocalDate.now().atStartOfDay().plusDays(1);
            LocalDateTime initialDate = finalDate.minusDays(retroactiveNumberOfDays);

            Connection connection = DatabaseConnection.getConnection();

            final String query = "select * from punch where punch_date between ? and ? order by punch_date";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(initialDate));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(finalDate));

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet;
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}