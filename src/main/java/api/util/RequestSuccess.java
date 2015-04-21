package api.util;

/**
 * Created by Dev on 21/04/2015.
 */
public class RequestSuccess {
    private boolean status;
    private String message;

    public RequestSuccess(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
