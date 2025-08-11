package org.example.model;

public class ValidationResponse {
    private boolean valid;
    private String errMsg;

    public ValidationResponse(boolean valid){
        this.valid = valid;
    }

    public ValidationResponse(String errMsg){
        this.errMsg = errMsg;
        this.valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errMsg;
    }
}
