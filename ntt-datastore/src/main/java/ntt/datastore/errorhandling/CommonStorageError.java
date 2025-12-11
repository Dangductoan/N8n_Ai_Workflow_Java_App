package ntt.datastore.errorhandling;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CommonStorageError {

    private HttpStatus status;
    private String message;

    public CommonStorageError() {
        super();
    }

    public CommonStorageError(final HttpStatus status, final String message) {
        super();
        this.status = status;
        this.message = message;
    }

}