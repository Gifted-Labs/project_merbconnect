package com.merbsconnect.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating QR codes for event registrations.
 */
@Slf4j
@Service
public class QrCodeService {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final String IMAGE_FORMAT = "PNG";

    /**
     * Generates a QR code as a Base64 encoded PNG image.
     *
     * @param content The content to encode in the QR code
     * @return Base64 encoded PNG image string
     */
    public String generateQrCodeBase64(String content) {
        return generateQrCodeBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Generates a QR code as a Base64 encoded PNG image with custom dimensions.
     *
     * @param content The content to encode in the QR code
     * @param width   Width of the QR code image in pixels
     * @param height  Height of the QR code image in pixels
     * @return Base64 encoded PNG image string
     */
    public String generateQrCodeBase64(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_FORMAT, outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            log.debug("Generated QR code for content: {} (length: {} chars)",
                    content.substring(0, Math.min(20, content.length())), content.length());

            return "data:image/png;base64," + base64Image;

        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code for content: {}", content, e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Generates a registration QR code using the registration token.
     *
     * @param registrationToken Unique token for the registration
     * @param baseUrl           Base URL for the check-in endpoint
     * @return Base64 encoded PNG image string
     */
    public String generateRegistrationQrCode(String registrationToken, String baseUrl) {
        String checkInUrl = baseUrl + "/check-in?token=" + registrationToken;
        return generateQrCodeBase64(checkInUrl);
    }

    /**
     * Generates a simple QR code with just the registration token.
     *
     * @param registrationToken Unique token for the registration
     * @return Base64 encoded PNG image string
     */
    public String generateTokenQrCode(String registrationToken) {
        return generateQrCodeBase64(registrationToken);
    }
}
