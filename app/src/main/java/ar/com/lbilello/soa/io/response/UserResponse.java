package ar.com.lbilello.soa.io.response;

public class UserResponse {
    private boolean success;
    private String env = "";
    private String token = "";
    private String token_refresh = "";
    private String msg = "";

    public Boolean getSuccess() {
        return success;
    }

    public String getEnv() {
        return env;
    }

    public String getToken() {
        return token;
    }

    public String getToken_refresh() {
        return token_refresh;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "success=" + success +
                ", env='" + env + '\'' +
                ", token='" + token + '\'' +
                ", token_refresh='" + token_refresh + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
