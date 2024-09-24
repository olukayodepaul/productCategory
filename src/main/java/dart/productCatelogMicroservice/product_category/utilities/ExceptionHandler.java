package dart.productCatelogMicroservice.product_category.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(RunTimeException.class)
    public ResponseEntity<ErrorHandler> handleCustomException(RunTimeException ex) {
        return new ResponseEntity<>(ex.getResponseHandler(), ex.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
                new ErrorHandler(false, "An error occurred: Required request data is missing "+ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}