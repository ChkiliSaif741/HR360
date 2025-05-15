package entities;


import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Mailpass {
    public void envoyerEmail(String destinataire, String sujet, String contenu) throws MessagingException {
        final String username = "farhanihamza563@gmail.com"; // Remplacez par votre adresse Gmail
        final String password = "mopj qili ssgz ifym"; // Remplacez par votre mot de passe d'application

        // Configuration des propriétés SMTP pour Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.debug", "true");


        // Création de la sessions avec authentification
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
            message.setText(contenu);


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