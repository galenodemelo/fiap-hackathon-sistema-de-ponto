package br.com.fiap.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtils {

    public static java.sql.Date toDatabaseDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static String toDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }
}
