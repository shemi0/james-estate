package com.kingjamesestate.realestate.service;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class EmailService {

    private static final String HERO_IMAGE =
            "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=1200&h=420&q=80";

    private final String fromEmail;
    private final String password;
    private final String publicUrl;

    public EmailService(@Value("${spring.mail.username}") String fromEmail,
                         @Value("${spring.mail.password}") String password,
                         @Value("${app.public-url}") String publicUrl) {
        this.fromEmail = fromEmail;
        this.password = password;
        this.publicUrl = publicUrl;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        String link = publicUrl + "/verify-otp.html?email="
                + URLEncoder.encode(toEmail, StandardCharsets.UTF_8)
                + "&code=" + otp;
        Exception lastError = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                sendOnce(toEmail, otp, link);
                return;
            } catch (Exception e) {
                lastError = e;
                if (attempt == 1) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        throw new IllegalStateException("Could not send verification email", lastError);
    }

    private void sendOnce(String toEmail, String otp, String link) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.connectiontimeout", "12000");
        props.put("mail.smtp.timeout", "12000");
        props.put("mail.smtp.writetimeout", "12000");

        Session session = Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail, "James Estate"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Verify your James Estate agent account");
        message.setContent(html(otp, link), "text/html; charset=UTF-8");
        message.setHeader("X-Alternative", plainText(otp, link));

        try (Transport transport = session.getTransport("smtp")) {
            transport.connect("smtp.gmail.com", 465, fromEmail, password);
            transport.sendMessage(message, message.getAllRecipients());
        }
    }

    private String plainText(String otp, String link) {
        return """
                James Estate

                This code confirms that you own this email address. James Estate asks for it before an agent can sign in and publish a property listing. It expires in 15 minutes.

                Open this link to verify your account:
                %s

                Or enter this code on the verification page: %s

                If you did not create a James Estate account, you can ignore this email.
                """.formatted(link, otp);
    }

    private String html(String otp, String link) {
        String href = link.replace("&", "&amp;");
        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0;padding:0;background:#f3f4f6;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f3f4f6;padding:28px 12px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="560" cellpadding="0" cellspacing="0" style="width:560px;max-width:560px;background:#ffffff;border-radius:16px;overflow:hidden;">
                          <tr>
                            <td style="background:#111111;">
                              <img src="%s" width="560" alt="James Estate" style="display:block;width:100%%;height:auto;border:0;">
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:28px 36px 0;font-family:Arial,Helvetica,sans-serif;font-size:13px;letter-spacing:1.5px;font-weight:bold;color:#c41e3a;">
                              JAMES ESTATE
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:10px 36px 0;font-family:Arial,Helvetica,sans-serif;font-size:24px;line-height:1.3;font-weight:bold;color:#111111;">
                              Verify your agent account
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:12px 36px 0;font-family:Arial,Helvetica,sans-serif;font-size:15px;line-height:1.6;color:#4b5563;">
                              This code confirms that you own this email address. James Estate asks for it before an agent can sign in and publish a property listing. It expires in 15 minutes.
                            </td>
                          </tr>
                          <tr>
                            <td align="center" style="padding:24px 36px 8px;">
                              <a href="%s" style="display:inline-block;background:#c41e3a;color:#ffffff;text-decoration:none;font-family:Arial,Helvetica,sans-serif;font-size:16px;font-weight:bold;padding:14px 28px;border-radius:8px;">
                                Verify my email
                              </a>
                            </td>
                          </tr>
                          <tr>
                            <td align="center" style="padding:8px 36px 0;font-family:Arial,Helvetica,sans-serif;font-size:13px;color:#6b7280;">
                              Or enter this code on the verification page
                            </td>
                          </tr>
                          <tr>
                            <td align="center" style="padding:12px 36px 8px;">
                              <div style="font-family:Arial,Helvetica,sans-serif;font-size:34px;letter-spacing:10px;font-weight:bold;color:#111111;background:#f9fafb;border:1px solid #e5e7eb;border-radius:12px;padding:18px 28px;">
                                %s
                              </div>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:8px 36px 28px;font-family:Arial,Helvetica,sans-serif;font-size:13px;line-height:1.6;color:#6b7280;">
                              If you did not create a James Estate account, you can ignore this email.
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#1a1a2e;padding:18px 36px;font-family:Arial,Helvetica,sans-serif;font-size:12px;line-height:1.6;color:#d1d5db;">
                              James Estate<br>Buy, rent, and short stay across Nigeria
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(HERO_IMAGE, href, otp);
    }
}
