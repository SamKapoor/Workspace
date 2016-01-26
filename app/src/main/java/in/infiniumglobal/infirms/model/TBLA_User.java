package in.infiniumglobal.infirms.model;

/**
 * Created by admin on 1/26/2016.
 */
public class TBLA_User {

    private String UserID;
    private String UserName;
    private String UserPassword;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }
}
