package planner;

public class Session {
    private static String token;
    private static String username;
    private static String pendingEmail;  // email waiting for verification

    public static void set(String user, String jwt) {
        username = user;
        token = jwt;
    }

    public static void setPendingEmail(String email){
        pendingEmail = email;
    }

    public static String getPendingEmail(){
        return pendingEmail;
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
