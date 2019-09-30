package com.aseemsavio.reactivespring.playground;

public class AseemException extends Throwable {

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public AseemException(Throwable exception) {
        this.message = exception.getMessage();
    }


}
