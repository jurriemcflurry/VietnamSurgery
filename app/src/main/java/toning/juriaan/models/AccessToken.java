package toning.juriaan.models;

public class AccessToken {
    private static String access_token;
    private static String userName;
    private static String userrole;

    public static void setAccess_token(String access_token) {
        AccessToken.access_token = access_token;
    }

    public static String getAccess_token() {
        return access_token;
    }

    public static void setUserName(String userName) {
        AccessToken.userName = userName;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserrole(String userrole) {
        AccessToken.userrole = userrole;
    }

    public static String getUserrole() {
        return userrole;
    }
}
