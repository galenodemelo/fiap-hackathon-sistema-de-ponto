package br.com.fiap.queue;

public class ReportQueueSqsManager extends SqsManager {

    private final static String QUEUE_NAME = "report-q";

    public ReportQueueSqsManager() {
        super(QUEUE_NAME);
    }
}
