package ntt.workflow.auth.controller;

import ntt.workflow.auth.utils.OtpUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms-media-api/security")
public class SecurityController {

    @ResponseBody
    @GetMapping("/qrcode/{secret}/{email}/{issuer}")
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable(value="issuer") String issuer,
            @PathVariable(value="secret") String secret,
            @PathVariable(value="email") String email
    ) {
        byte[] byteOTP = OtpUtils.generateQRCode(issuer, secret, email);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_PNG)
                .body(byteOTP);
    }

}
