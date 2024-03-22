package br.com.fiap;

import br.com.fiap.queue.SqsManager;
import br.com.fiap.report.dto.AskForReportRequestDTO;
import br.com.fiap.util.LogUtils;
import br.com.fiap.util.ResponseUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;

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

            final String queueName = "report-q";
            SqsManager sqsManager = new SqsManager(queueName);
            sqsManager.sendMessage(mapper.writeValueAsString(askForReportRequestDTO));

            return ResponseUtils.ok("Relatório será enviado por e-mail");
        } catch (Exception exception) {
            LogUtils.logException(exception);
            return ResponseUtils.internalServerError("Erro ao solicitar relatório");
        }
    }
}
