package br.com.fiap.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {

    public static void logException(Exception exception) {
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);

            String stackTrace = stringWriter.toString();
            System.out.println(stackTrace);

            stringWriter.close();
            printWriter.close();
        } catch (IOException ioException) {
            System.out.println("Falha ao logar exceção: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }
}
