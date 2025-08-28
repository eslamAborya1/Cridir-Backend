package com.NTG.Cridir.service;

import com.NTG.Cridir.model.User;
import com.NTG.Cridir.repository.UserRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public EmailService(JavaMailSender mailSender, JwtService jwtService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Value("${app.server.base-url}")
    private String appBaseUrl;

    public void sendActivationEmail(User user, String token) {
        String activationLink = appBaseUrl + "/auth/activate?token=" + token;

        String subject = "Activate your Cridir account";
        String body = "<p>Hello " + user.getName() + ",</p>"
                + "<p>Please click the link below to activate your account:</p>"
                + "<p><a href=\"" + activationLink + "\">Activate Account</a></p>";

        sendEmail(user.getEmail(), subject, body);
    }

//    public void sendEmail(String to, String subject, String htmlContent) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setFrom("member@cridir.com");
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//            System.out.println(" Email sent to " + to);
//        } catch (MessagingException e) {
//            throw new RuntimeException(" Failed to send email", e);
//        }
//    }
@Value("${SENDGRID_API_KEY}")
private String sendGridApiKey;

    public void sendEmail(String to, String subject, String htmlContent) {
        Email from = new Email("okashaaa14@gmail.com"); // لازم Verified Sender في SendGrid
        Email recipient = new Email(to);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Email status: " + response.getStatusCode());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to send email", ex);
        }
    }








//    @Transactional
//    public void sendResetPasswordEmail(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//
//
//        String resetToken = jwtService.generateToken(user);
//
//        String resetLink = appBaseUrl + "/reset-password?token=" + resetToken;
//
//        String subject = "Reset your Cridir password";
//        String body = "<p>Hello " + user.getName() + ",</p>"
//                + "<p>We received a request to reset your password.</p>"
//                + "<p><a href=\"" + resetLink + "\">Click here to reset your password</a></p>"
//                + "<p>If you didn’t request this, you can safely ignore this email.</p>";
//
//        sendEmail(user.getEmail(), subject, body);
//    }
//public void resetPassword(ResetPasswordRequest request) {
//    User user = userRepository.findByEmail(request.email())
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    if (!request.code().equals(user.getResetCode()))
//        throw new RuntimeException("Invalid reset code");
//
//    if (passwordEncoder.matches(request.newPassword(), user.getPassword()))
//        throw new RuntimeException("New password cannot be the same as the old one");
//
//    user.setPassword(passwordEncoder.encode(request.newPassword()));
//    user.setResetCode(null); // امسح الكود بعد الاستخدام
//    userRepository.save(user);
//}


    @Transactional
    public void sendResetPasswordCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // generate 5-digit code
        String code = String.format("%05d", (int)(Math.random() * 100000));
        user.setResetCode(code);
        user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(10)); // expire in 2 min
        userRepository.save(user);

        String subject = "Your Cridir Password Reset Code";
        String body = "<p>Hello " + user.getName() + ",</p>"
                + "<p>Your password reset code is: <b>" + code + "</b></p>"
                + "<p>This code will expire in 10 minutes.</p>";

        sendEmail(user.getEmail(), subject, body);
    }

}
