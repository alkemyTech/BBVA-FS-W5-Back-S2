package com.example.bbva.squad2.Wallet.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String template, Map<String, String> placeholders) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);

        // Cargar el contenido de la plantilla
        String htmlContent = loadTemplate(template);

        // Reemplazar los marcadores dinámicos
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        helper.setText(htmlContent, true);

        // Agregar imágenes comunes
        ClassPathResource imageLogo = new ClassPathResource("static/images/lynxlogo.png");
        helper.addInline("logoImage", imageLogo);

        ClassPathResource imageLynx = new ClassPathResource("static/images/lynxnombre2.png");
        helper.addInline("welcomeImage", imageLynx);

        mailSender.send(message);
    }

    private String loadTemplate(String templateName) {
        switch (templateName) {
            case "welcome":
                return """
                        <!DOCTYPE html>
                        <html>
                        <body>
                            <h1>Bienvenido {{firstName}} {{lastName}} a Lynx</h1>
                            <p>Ya podes ingresar tu dinero y utilizar tus cuentas.</p>
                            <img src="cid:logoImage" alt="Logo" style="width: 50%; max-width: 90px;">
                            <img src="cid:welcomeImage" alt="Bienvenido" style="width: 50%; max-width: 200px;">
                        </body>
                        </html>
                        """;
            case "sendTransaction":
                return """
                        <!DOCTYPE html>
                        <html>
                        <body>
                            <p>{{firstNameRemitente}} {{lastNameRemitente}} tu transferencia a {{firstNameReceptor}} {{lastNameReceptor}} fue enviada con éxito</p>
                            <p>Monto enviado ${{monto}}</p>
                            <img src="cid:logoImage" alt="Logo" style="width: 50%; max-width: 90px;">
                            <img src="cid:welcomeImage" alt="Bienvenido" style="width: 50%; max-width: 200px;">
                        </body>
                        </html>
                        """;
            case "receiveTransaction":
                return """
                        <!DOCTYPE html>
                        <html>
                        <body>
                            <p>{{firstNameReceptor}} {{lastNameReceptor}} recibiste una transferencia de {{firstNameRemitente}} {{lastNameRemitente}}</p>
                            <p>Monto recibido ${{monto}}</p>
                            <img src="cid:logoImage" alt="Logo" style="width: 50%; max-width: 90px;">
                            <img src="cid:welcomeImage" alt="Bienvenido" style="width: 50%; max-width: 200px;">
                        </body>
                        </html>
                        """;
            default:
                throw new IllegalArgumentException("Template no encontrado: " + templateName);
        }
    }
}
