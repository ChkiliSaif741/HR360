package controllers;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Properties;

public class ServiceMail {

    public void envoyerEmailAvecLogo(String destinataire, String sujet, String contenuHtml, String logoPath) throws MessagingException {
        final String username = "kousaynajar147@gmail.com"; // Remplacez par votre adresse Gmail
        final String password = "criw ewpi tsvh vjis"; // Remplacez par votre mot de passe d'application

        // Configuration des propriétés SMTP pour Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Activation de TLS
        props.put("mail.smtp.host", "smtp.gmail.com"); // Hôte SMTP de Gmail
        props.put("mail.smtp.port", "587"); // Port SMTP de Gmail
        props.put("mail.debug", "true"); // Activation des logs SMTP pour le débogage

        // Création de la session avec authentification
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Expéditeur
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire)); // Destinataire
            message.setSubject(sujet);

            // Création du contenu HTML avec le logo incorporé
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(contenuHtml, "text/html");

            // Création de la partie pour le logo
            MimeBodyPart imagePart = new MimeBodyPart();
            DataSource fds = new FileDataSource(logoPath);
            imagePart.setDataHandler(new DataHandler(fds));
            imagePart.setHeader("Content-ID", "<logo>"); // Identifiant du logo dans le HTML

            // Assemblage des parties du message
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(imagePart);

            // Définir le contenu du message
            message.setContent(multipart);

            // Envoi du message
            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + destinataire);
        } catch (AuthenticationFailedException e) {
            System.out.println("Erreur d'authentification : Vérifiez vos identifiants.");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi de l'email.");
            e.printStackTrace();
        }
    }
}