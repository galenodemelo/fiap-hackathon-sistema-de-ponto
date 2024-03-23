package br.com.fiap.punch;

public enum PunchEvent {

    ENTRY("Entrada"),
    INTERVAL_BEGIN("Início do intervalo"),
    INTERVAL_END("Fim do intervalo"),
    EXIT("Saída");

    private final String label;

    PunchEvent(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
