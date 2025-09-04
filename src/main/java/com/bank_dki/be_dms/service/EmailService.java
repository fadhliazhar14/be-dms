package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Year;
import java.util.Map;

/**
 * Email Service untuk menangani semua operasi pengiriman email
 * Menggunakan template HTML dan plain text untuk kompatibilitas
 * 
 * @author Bank Jakarta DMS Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.email.sender}")
    private String emailSender;
    
    @Value("${app.email.sender-name}")
    private String emailSenderName;
    
    /**
     * Sends password reset email with professional template
     * @param user User requesting password reset
     * @param resetToken Reset token
     * @param resetUrl Complete reset URL
     */
    public void sendPasswordResetEmail(User user, String resetToken, String resetUrl) {
        try {
            // Load templates
            String htmlTemplate = loadEmailTemplate("reset-password-email.html");
            String textTemplate = loadEmailTemplate("reset-password-email.txt");
            
            // Prepare template variables
            Map<String, String> templateVars = Map.of(
                "{{username}}", user.getUserName() != null ? user.getUserName() : "User",
                "{{resetToken}}", resetToken,
                "{{resetUrl}}", resetUrl,
                "{{expirationTime}}", "60", // 1 hour in minutes
                "{{currentYear}}", String.valueOf(Year.now().getValue())
            );
            
            // Replace placeholders
            String htmlContent = replacePlaceholders(htmlTemplate, templateVars);
            String textContent = replacePlaceholders(textTemplate, templateVars);
            
            // Send email
            sendHtmlEmail(
                user.getUserEmail(),
                "🔐 Reset Password - Bank Jakarta DMS",
                htmlContent,
                textContent
            );
            
            log.info("Password reset email sent successfully to: {}", user.getUserEmail());
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getUserEmail(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    /**
     * Generic method untuk mengirim HTML email dengan text fallback
     * @param to Recipient email
     * @param subject Email subject
     * @param htmlContent HTML content
     * @param textContent Plain text content (fallback)
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent, String textContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(textContent, htmlContent); // text as fallback, html as primary
            helper.setFrom(emailSender, emailSenderName);
            
            mailSender.send(message);
            log.debug("Email sent successfully to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    /**
     * Load email template from classpath resources
     * @param templateName Template filename (e.g., "reset-password-email.html")
     * @return Template content as string
     */
    private String loadEmailTemplate(String templateName) {
        try {
            var resource = ResourceUtils.getFile("classpath:templates/" + templateName);
            return Files.readString(resource.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load email template: {}", templateName, e);
            // Return fallback template based on type
            if (templateName.endsWith(".html")) {
                return getDefaultHtmlTemplate();
            } else {
                return getDefaultTextTemplate();
            }
        }
    }
    
    /**
     * Replace all placeholders in template with provided values
     * @param template Template content with placeholders
     * @param templateVars Map of placeholder -> value pairs
     * @return Template with replaced values
     */
    private String replacePlaceholders(String template, Map<String, String> templateVars) {
        String result = template;
        for (Map.Entry<String, String> entry : templateVars.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        
        // Handle logo with fallback mechanism
        LogoData logoData = loadBankLogoWithFallback();
        result = result.replace("{{bankLogo}}", logoData.base64Data())
                      .replace("{{logoDisplay}}", logoData.logoDisplay())
                      .replace("{{fallbackDisplay}}", logoData.fallbackDisplay());
        
        return result;
    }
    
    /**
     * Load Bank Jakarta logo with smart fallback
     * @return LogoData containing base64 data and display settings
     */
    private LogoData loadBankLogoWithFallback() {
        try {
            var logoResource = ResourceUtils.getFile("classpath:assets/bank-dki.png");
            byte[] logoBytes = Files.readAllBytes(logoResource.toPath());
            String base64Data = java.util.Base64.getEncoder().encodeToString(logoBytes);
            
            log.debug("Bank Jakarta logo loaded successfully from assets");
            return new LogoData(base64Data, "inline-block", "none");
            
        } catch (Exception e) {
            log.warn("Failed to load Bank Jakarta logo from assets: {}", e.getMessage());
            log.info("Using emoji fallback for Bank Jakarta logo");
            
            // Return transparent pixel as base64 but show fallback
            String transparentPixel = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
            return new LogoData(transparentPixel, "none", "inline-block");
        }
    }
    
    /**
     * Record for logo data and display settings
     */
    private record LogoData(String base64Data, String logoDisplay, String fallbackDisplay) {}
    
    /**
     * Default HTML template sebagai fallback
     * Menggunakan design system Bank Jakarta dengan warna merah konsisten
     */
    private String getDefaultHtmlTemplate() {
        return """
            <!DOCTYPE html>
            <html lang="id">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reset Password - Bank Jakarta DMS</title>
            </head>
            <body style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; line-height: 1.6; color: #333; background-color: #f5f6fa; margin: 0; padding: 20px 0;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08); overflow: hidden;">
                    <!-- Header -->
                    <div style="background: linear-gradient(135deg, #DE2929 0%, #B91C1C 100%); padding: 40px 30px; text-align: center; color: white;">
                        <h1 style="margin: 0 0 10px 0; font-size: 28px; font-weight: bold;">🏛️ Bank Jakarta</h1>
                        <h2 style="margin: 0; font-size: 20px; font-weight: 500;">Reset Password</h2>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 40px 30px;">
                        <h3 style="color: #DE2929; font-size: 20px; margin-bottom: 20px;">Selamat datang, {{username}}!</h3>
                        
                        <p style="margin-bottom: 25px; line-height: 1.7; color: #4b5563;">
                            Kami menerima permintaan untuk mereset password akun Anda di sistem DMS Bank Jakarta.
                        </p>
                        
                        <!-- Reset Button -->
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="{{resetUrl}}" style="background: linear-gradient(135deg, #DE2929 0%, #B91C1C 100%); color: white; text-decoration: none; padding: 16px 32px; border-radius: 8px; font-size: 16px; font-weight: 600; display: inline-block; box-shadow: 0 4px 12px rgba(222, 41, 41, 0.3);">
                                🔐 Reset Password Sekarang
                            </a>
                        </div>
                        
                        <!-- Token Display -->
                        <div style="background: linear-gradient(135deg, #fef2f2 0%, #fecaca 100%); border: 2px dashed #DE2929; border-radius: 8px; padding: 20px; margin: 25px 0; text-align: center;">
                            <p style="margin-bottom: 10px; font-size: 14px; color: #6b7280;">Atau gunakan kode verifikasi:</p>
                            <p style="font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, 'Courier New', monospace; font-size: 24px; font-weight: bold; color: #DE2929; letter-spacing: 4px; margin: 15px 0;">{{resetToken}}</p>
                        </div>
                        
                        <!-- Security Notice -->
                        <div style="background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 20px; margin: 25px 0; border-radius: 0 8px 8px 0;">
                            <h4 style="color: #92400e; margin-bottom: 10px; font-size: 16px;">🛡️ Pemberitahuan Keamanan</h4>
                            <ul style="color: #92400e; font-size: 14px; margin: 0; padding-left: 20px;">
                                <li>Link dan kode berlaku selama {{expirationTime}} menit</li>
                                <li>Jangan bagikan kode atau link ini kepada siapapun</li>
                                <li>Hubungi administrator jika Anda tidak melakukan permintaan ini</li>
                            </ul>
                        </div>
                        
                        <!-- Alternative Link -->
                        <div style="background-color: #f8fafc; border-radius: 8px; padding: 20px; margin: 25px 0; border: 1px solid #e2e8f0;">
                            <p style="font-size: 14px; color: #64748b; margin-bottom: 10px;"><strong>Jika tombol tidak berfungsi:</strong></p>
                            <p style="font-size: 14px; color: #64748b; margin-bottom: 10px;">Salin dan tempel URL berikut:</p>
                            <div style="word-break: break-all; font-family: 'SF Mono', Monaco, monospace; background-color: white; padding: 12px; border-radius: 6px; font-size: 12px; color: #475569; border: 1px solid #d1d5db;">{{resetUrl}}</div>
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%); padding: 30px; text-align: center; border-top: 1px solid #e2e8f0;">
                        <p style="color: #DE2929; font-weight: 600; margin-bottom: 10px;">Bank Jakarta - Document Management System</p>
                        <p style="color: #64748b; font-size: 14px; margin-bottom: 15px;">Sistem keamanan tingkat enterprise untuk dokumen penting.</p>
                        <div style="color: #94a3b8; font-size: 12px;">
                            <p style="margin: 5px 0;">© {{currentYear}} Bank Jakarta. Seluruh hak cipta dilindungi undang-undang.</p>
                            <p style="margin: 5px 0;">Email otomatis - Mohon jangan membalas email ini</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    /**
     * Default plain text template sebagai fallback
     */
    private String getDefaultTextTemplate() {
        return """
            ================================================================================
            BANK JAKARTA - DOCUMENT MANAGEMENT SYSTEM
            RESET PASSWORD
            ================================================================================
            
            Selamat datang, {{username}}!
            
            Kami menerima permintaan untuk mereset password akun Anda di sistem DMS Bank Jakarta.
            
            KODE RESET PASSWORD
            -------------------
            {{resetToken}}
            
            LINK RESET PASSWORD
            ------------------
            {{resetUrl}}
            
            PETUNJUK:
            1. Klik link di atas, atau salin dan tempel ke browser Anda
            2. Masukkan kode reset: {{resetToken}}
            3. Buat password baru yang kuat
            4. Simpan password di tempat yang aman
            
            PEMBERITAHUAN KEAMANAN:
            • Link dan kode berlaku selama {{expirationTime}} menit dari sekarang
            • Jika Anda tidak melakukan permintaan ini, segera hubungi administrator
            • Jangan bagikan kode atau link ini kepada siapapun
            • Gunakan password yang kuat dengan kombinasi huruf, angka, dan simbol
            
            BANTUAN:
            Jika Anda mengalami kesulitan, silakan hubungi:
            - Tim IT Bank Jakarta
            - Administrator sistem DMS
            
            ================================================================================
            © {{currentYear}} Bank Jakarta. Seluruh hak cipta dilindungi undang-undang.
            Email otomatis - Mohon jangan membalas email ini
            ================================================================================
            """;
    }
}
