package planner;

public class Session {
    private static String token;
    private static String username;

    public static void set(String user, String jwt) {
        username = user;
        token = jwt;
    }

    public static String getToken() {
        return token;
    }

    public static String getUsername() {
        return username;
    }

    public static boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public static void clear() {
        token = null;
        username = null;
    }

    
}
