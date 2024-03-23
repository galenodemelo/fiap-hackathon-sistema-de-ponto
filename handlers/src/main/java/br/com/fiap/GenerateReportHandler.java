package br.com.fiap;

import br.com.fiap.mail.MailSender;
import br.com.fiap.punch.PunchEvent;
import br.com.fiap.queue.ReportQueueSqsManager;
import br.com.fiap.report.dto.PunchReport;
import br.com.fiap.report.dto.ReportQueueDTO;
import br.com.fiap.storage.S3Manager;
import br.com.fiap.user.User;
import br.com.fiap.util.ConvertUtils;
import br.com.fiap.util.DatabaseConnection;
import br.com.fiap.util.LogUtils;
import br.com.fiap.util.ResponseUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Map;

public class GenerateReportHandler implements RequestHandler<SQSEvent, APIGatewayProxyResponseEvent> {

    private static String sendToS3(String csv) throws IOException {
        final String bucketName = "punch-reports";
        final String filename = Instant.now().getNano() + ".csv";

        S3Manager manager = new S3Manager();
        manager.put(bucketName, filename, csv, "text/csv");
        return manager.presignedUrl(bucketName, filename);
    }

    public APIGatewayProxyResponseEvent handleRequest(final SQSEvent input, final Context context) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            for (SQSEvent.SQSMessage message : input.getRecords()) {
                ReportQueueDTO reportQueueDTO = mapper.readValue(message.getBody(), ReportQueueDTO.class);

                User user = User.findByEmail(reportQueueDTO.email());
                if (user == null) throw new RuntimeException("Usuário não encontrado");

                ResultSet resultSet = listPunches(user, reportQueueDTO);

                PunchReport punchReport = new PunchReport();
                while (resultSet.next()) {
                    PunchEvent event = PunchEvent.valueOf(resultSet.getString("event"));

                    punchReport.addRow(
                            Map.of(
                                    "id", resultSet.getString("id"),
                                    "punchDate", ConvertUtils.toDateString(resultSet.getDate("punch_date")),
                                    "event", event.getLabel()
                            )
                    );
                }
                resultSet.close();

                String presignedUrl = sendToS3(punchReport.buildCsv());
                MailSender.sendMail(
                        user.getEmail(),
                        "Relatório de ponto",
                        "Seu relatório de ponto está pronto: " + presignedUrl
                );

                new ReportQueueSqsManager().deleteMessage(message.getReceiptHandle());
            }

            return ResponseUtils.ok("Relatório gerado com sucesso");
        } catch (Exception exception) {
            LogUtils.logException(exception);
            return ResponseUtils.internalServerError("Erro ao gerar relatório");
        }
    }

    private ResultSet listPunches(User user, ReportQueueDTO reportQueueDTO) throws SQLException {
        PreparedStatement preparedStatement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT * FROM punch WHERE user_id = ? AND punch_date BETWEEN ? AND ?"
        );

        preparedStatement.setLong(1, user.getId());
        preparedStatement.setDate(2, ConvertUtils.toDatabaseDate(reportQueueDTO.startDate()));
        preparedStatement.setDate(3, ConvertUtils.toDatabaseDate(reportQueueDTO.endDate()));

        return preparedStatement.executeQuery();
    }
}
