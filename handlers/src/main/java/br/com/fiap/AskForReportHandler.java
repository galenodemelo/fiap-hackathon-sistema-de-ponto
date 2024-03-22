package br.com.fiap;

import br.com.fiap.queue.SqsManager;
import br.com.fiap.report.dto.AskForReportRequestDTO;
import br.com.fiap.report.dto.ReportQueueDTO;
import br.com.fiap.user.User;
import br.com.fiap.util.LogUtils;
import br.com.fiap.util.ResponseUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.Map;

public class AskForReportHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            AskForReportRequestDTO askForReportRequestDTO = mapper.readValue(input.getBody(), AskForReportRequestDTO.class);
            if (askForReportRequestDTO.startDate().after(new Date())) {
                return ResponseUtils.badRequest("Data de início não pode ser maior que a data atual");
            }

            if (askForReportRequestDTO.startDate().after(askForReportRequestDTO.endDate())) {
                return ResponseUtils.badRequest("Data de início não pode ser maior que a data final");
            }

            Map<String, Object> authorizer = input.getRequestContext().getAuthorizer();
            Map<String, String> lambda = (Map) authorizer.get("lambda");
            String principalId = lambda.get("principalId");

            User currentUser = User.findById(Long.valueOf(principalId));
            if (currentUser == null) {
                return ResponseUtils.badRequest("Usuário não encontrado");
            }

            ReportQueueDTO reportQueueDTO = new ReportQueueDTO(
                currentUser.getEmail(),
                askForReportRequestDTO.startDate(),
                askForReportRequestDTO.endDate()
            );

            final String queueName = "report-q";
            SqsManager sqsManager = new SqsManager(queueName);
            sqsManager.sendMessage(mapper.writeValueAsString(reportQueueDTO));

            return ResponseUtils.ok("Relatório será enviado por e-mail");
        } catch (Exception exception) {
            LogUtils.logException(exception);
            return ResponseUtils.internalServerError("Erro ao solicitar relatório");
        }
    }
}
