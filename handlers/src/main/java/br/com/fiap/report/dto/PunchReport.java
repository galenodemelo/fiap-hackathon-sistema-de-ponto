package br.com.fiap.report.dto;

import java.util.List;
import java.util.Map;

public class PunchReport {

    private final static String DELIMITER = ";";

    private final static List<String> HEADER_LIST = List.of("ID", "Data", "Tipo");
    private final List<String> rowList = new java.util.ArrayList<>();

    public PunchReport addRow(Map<String, String> rowData) {
        List<String> rowDataList = List.of(
            rowData.get("id"),
            rowData.get("punchDate"),
            rowData.get("event")
        );

        this.rowList.add(String.join(DELIMITER, rowDataList));
        return this;
    }

    public String buildCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append(String.join(DELIMITER, HEADER_LIST));
        csv.append("\n");

        for (String row : this.rowList) {
            csv.append(row);
            csv.append("\n");
        }

        return csv.toString();
    }
}
