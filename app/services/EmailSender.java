package services;

import play.Logger;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    private final static Logger.ALogger appLogger = Logger.of("app");

    public static void welcomeEmail(String receiver){
        appLogger.info("Montando e-mail de boas vindas");
        String title = "Bem vindx - Sala de Aula Virtual";
        String body = "Bem vindx à Sala de Aula Virtual!\\nEsperamos que você aprenda bastante e que este desejo pelo novo nunca cesse!!\\nAproveite.";

        send(receiver, title, body);
    }

    public static void recoveryEmail(String receiver, String temppassword){
        appLogger.info("Montando e-mail de recuperacao de senha");
        String title = "Recuperação de Senha - Sala de Aula Virtual";
        String body = "Foi recebida uma solicitação para resetar a senha da conta vinculada a este e-mail.\nSenha temporária para reset: "+temppassword+"\n\nCaso você não tenha solicitado, altere sua senha por uma mais difícil por precaução!";

        send(receiver, title, body);
    }

    private static void send(String receiver, String title, String body){
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication("saladeaulavr@gmail.com", "SalaDeAula123");
                    }
                });
        session.setDebug(true);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("saladeaulavr@gmail.com"));

            Address[] toUser = InternetAddress.parse(receiver);

            message.setRecipients(Message.RecipientType.TO, toUser);
            message.setSubject(title);
            message.setText(body);

            appLogger.info("Enviando e-mail");
            Transport.send(message);
            appLogger.info("E-mail enviado");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
