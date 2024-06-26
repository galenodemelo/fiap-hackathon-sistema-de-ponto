package br.com.fiap.queue;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

abstract public class SqsManager {

    private final String queueName;

    public SqsManager(String queueName) {
        this.queueName = queueName;
    }

    public void sendMessage(String message) {
        SqsClient sqsClient = buildClient();

        try (sqsClient) {
            sqsClient.sendMessage(to -> to.queueUrl(this.queueName).messageBody(message));
        }
    }

    public void deleteMessage(String receiptHandle) {
        SqsClient sqsClient = buildClient();

        try (sqsClient) {
            sqsClient.deleteMessage(to -> to.queueUrl(this.queueName).receiptHandle(receiptHandle));
        }
    }

    private SqsClient buildClient() {
        return SqsClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .build();
    }
}
