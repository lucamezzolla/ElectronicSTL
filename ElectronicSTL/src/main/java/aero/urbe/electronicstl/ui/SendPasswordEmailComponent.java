package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.jdb;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author Luca Mezzolla
 */
public class SendPasswordEmailComponent extends Window implements Button.ClickListener {
    
    private final VerticalLayout vl;
    private TextField emailField;
    private HorizontalLayout hl;
    private Button cancel, submit;
    private final jdb db;

    public SendPasswordEmailComponent(jdb db) {
        this.db = db;
        vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.setMargin(true);
        vl.setWidth("100%");
        buildUI();
        super.setCaption("Send new password...");
        super.setWidth("25%");
        super.setModal(true);
        super.setResizable(false);
        super.setContent(vl);
        super.center();
    }

    private void buildUI() {
        vl.removeAllComponents();
        emailField = new TextField("Email:");
        emailField.setWidth("100%");
        emailField.focus();
        submit = new Button(Messages.SUBMIT, this);
        submit.setClickShortcut(KeyCode.ENTER);
        cancel = new Button(Messages.CANCEL, this);
        cancel.setStyleName(Runo.BUTTON_DEFAULT);
        hl = new HorizontalLayout(cancel, submit);
        hl.setSpacing(true);
        vl.addComponents(emailField, hl);
        vl.setComponentAlignment(hl, Alignment.BOTTOM_RIGHT);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == cancel) {
            super.close();
        } else {
            if(emailField.getValue().length() > 0) {
                try {
                    Date date = new Date();
                    String newPass = String.valueOf(date.getTime());
                    Queries.UPDATE_PASSWORD(db, newPass,emailField.getValue());                    
                    String from = "lucamezzolla@gmail.com";
                    String pass = "Trustno1.";
                    String[] to = { emailField.getValue() }; // list of recipient email addresses
                    String subject = "Electronic STL (Reset Password)";
                    String body = "Your password is: "+newPass;
                    sendFromGMail(from, pass, to, subject, body);
                    super.close();
                } catch (Exception ex) {
                    MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
                }
            } else {
                MyNotification.SHOW(Messages.ERROR, Messages.ERROR_EMPTY_FIELDS, Notification.Type.ERROR_MESSAGE);
            }
        }
    }
    
    private static void sendFromGMail(String from, String pass, String[] to, String subject, String body) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];
            // To get the array of addresses
            for( int i = 0; i < to.length; i++ ) {
                toAddress[i] = new InternetAddress(to[i]);
            }
            for( int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            MyNotification.SHOW("Success!", "New password sent! Check your incoming mail or spam!", Notification.Type.HUMANIZED_MESSAGE);
        }
        catch (Exception ex) { }
    }
    
}