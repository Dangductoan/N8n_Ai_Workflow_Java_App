//package cms.common.api.media.exceptions;
//
//import cms.common.api.media.model.ImageResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.multipart.MaxUploadSizeExceededException;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//@ControllerAdvice
//public class RestExceptionHandler extends ResponseEntityExceptionHandler {
//
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public ResponseEntity<ImageResponse> handleMaxSizeException(MaxUploadSizeExceededException exc) {
//        ImageResponse response = new ImageResponse();
//        response.setMessage("Unable to upload. File is too large!");
//        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
//    }
//}