/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ntt.workflow.auth.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class OtpUtils {
	public static String generateSecretKey(String issuer, String issuerSecret, String email) {
		String secretKey = issuer + "_" + issuerSecret + "_" + email;
		Base32 base32 = new Base32();
		return base32.encodeToString(secretKey.getBytes());
	}

	public static String getTOTPCode(String secretKey) {
		Base32 base32 = new Base32();
		byte[] bytes = base32.decode(secretKey);
		String hexKey = Hex.encodeHexString(bytes);
		return TOTP.getOTP(hexKey);
	}

	public static String getGoogleAuthenticatorBarCode(String issuer, String secretKey, String account) {
		try {
			return "otpauth://totp/"
					+ URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
					+ "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
					+ "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static byte[] generateQRCode(String issuerId, String issuerSecret, String email)  {
		try {
			String secretKey = generateSecretKey(issuerId, issuerSecret, email);
			String barCodeUrl = getGoogleAuthenticatorBarCode(issuerId, secretKey, email);
			byte[] barCode = createQRCode(barCodeUrl, 300, 300);
			return barCode;
		}catch (Exception e){
			return null;
		}
	}

	public static void createQRCode(String barCodeData, String filePath, int height, int width)
			throws WriterException, IOException {
		BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
		try (FileOutputStream out = new FileOutputStream(filePath)) {
			MatrixToImageWriter.writeToStream(matrix, "png", out);
		}
	}

	public static byte[] createQRCode(String barCodeData, int height, int width)
			throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
		try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
			MatrixToImageWriter.writeToStream(bitMatrix,"PNG",pngOutputStream);
			byte[] pngData = pngOutputStream.toByteArray();
			return pngData;
		}
	}

	public static boolean verifyOTP(String secretKey, String otp){
        return otp.equals(getTOTPCode(secretKey));
    }

}
