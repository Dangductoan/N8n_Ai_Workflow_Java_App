package ntt.user.management.errorhandling;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CommonConfigurationError {

    private HttpStatus status;
    private String message;

    public CommonConfigurationError() {
        super();
    }

    public CommonConfigurationError(final HttpStatus status, final String message) {
        super();
        this.status = status;
        this.message = message;
    }

}