package pl.volleyflow.apputils;

public class StringUtils {

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}
