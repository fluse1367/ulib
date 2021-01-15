package eu.software4you.smtp;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * @deprecated needs further development
 * will be improved in future
 */
@Deprecated
public class Mail {
    private String host;
    private boolean auth = true;
    private String sender = null;
    private String senderName = null;
    private String receiver = null;
    private String subject = null;
    private String text = null;
    private String header1 = null;
    private String header2 = null;
    private final String transportProtocol = "smtps";

    private String smtpUser = null;
    private String smtpPassword = null;

    public void setHost(String host) {
        this.host = host;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public String send() {
        try {
            Properties props = System.getProperties();
            props.put("mail.smtps.host", host);
            props.put("mail.smtps.auth", auth);
            Session session = Session.getInstance(props, null);
            Message msg = new MimeMessage(session);

            InternetAddress inetAdr = senderName == null ? new InternetAddress(sender) : new InternetAddress(sender, senderName);

            msg.setFrom(inetAdr);

            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(receiver, false));
            msg.setSubject(subject);

            //msg.setText(text);
            msg.setContent(text, "text/html; charset=ISO-8859-1");

            if (header1 != null && header2 != null)
                msg.setHeader(header1, header2);

            msg.setSentDate(new Date());
            SMTPTransport t =
                    (SMTPTransport) session.getTransport(transportProtocol);
            t.connect(host, smtpUser, smtpPassword);
            t.sendMessage(msg, msg.getAllRecipients());
            String res = t.getLastServerResponse();
            t.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
