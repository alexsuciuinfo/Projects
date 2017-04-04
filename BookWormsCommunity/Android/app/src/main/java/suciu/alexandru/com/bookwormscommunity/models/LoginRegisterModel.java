package suciu.alexandru.com.bookwormscommunity.models;

/**
 * Created by Alexandru on 11.05.2016.
 */
public class LoginRegisterModel {
    private boolean error;
    private String message;
    private String apikey;
    private String userId;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
