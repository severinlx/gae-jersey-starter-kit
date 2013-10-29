package name.vysoky.example.util;

import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Universal parser used in whole application.
 *
 * @author Jiri Vysoky
 */
public class Parser {

    private static final Locale LOCALE = new Locale("cs", "CZ");
    private static final SimpleDateFormat DF = new SimpleDateFormat("d.M.YYYY", LOCALE);
    private static final NumberFormat CF = DecimalFormat.getCurrencyInstance(LOCALE);
    private static final NumberFormat NF = DecimalFormat.getInstance(LOCALE);

    {
        NF.setMinimumFractionDigits(2);
        NF.setMaximumFractionDigits(2);
    }

    public static String formatShortDate(Date d) {
        if (d == null) return "";
        else return DF.format(d);
    }

    public static String formatCurrency(BigDecimal d) {
        if (d == null) return "";
        else return CF.format(d);
    }

    public static String formatString(String s) {
        if (s == null) return "";
        else return StringEscapeUtils.escapeXml(s);
    }

    public static String parseString(String s) {
        if (s == null || s.isEmpty()) return null;
        else return s;
    }

    public static Long parseLong(String s) {
        if (s == null || s.isEmpty()) return null;
        else return Long.parseLong(s);
    }

    public static String formatLong(Long l) {
        if (l == null) return "";
        else return Long.toString(l);
    }

    public static String formatDouble(Double d) {
        if (d == null) return "";
        else return NF.format(d);
    }

    public static  Double parseDouble(String s) {
        if (s == null || s.isEmpty()) return null;
        else return Double.parseDouble(s);
    }

}
