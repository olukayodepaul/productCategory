package dart.productCatelogMicroservice.product_category.utilities;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class RunTimeException  extends RuntimeException {

    private final HttpStatus status;
    private final ErrorHandler responseHandler;

    public RunTimeException(ErrorHandler responseHandler, HttpStatus status) {
        super(responseHandler.getMessage());
        this.status = status;
        this.responseHandler = responseHandler;
    }

}
