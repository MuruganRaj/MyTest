package iniyan.com.facebookintegrate.model;

public class JoinAddResponse {
    private int code;
    private JoinAddResponseMessage[] message;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JoinAddResponseMessage[] getMessage() {
        return this.message;
    }

    public void setMessage(JoinAddResponseMessage[] message) {
        this.message = message;
    }
}
