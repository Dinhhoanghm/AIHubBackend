package ongoing.backend.config.exception;

import lombok.Getter;

@Getter
public class DBException extends Exception {
    static final long serialVersionUID = 1651914954614L;
    private int code = 404;

    public DBException() {
    }

    public DBException(String message) {
        super(message);
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBException(Throwable cause) {
        super(cause);
    }

    protected DBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
