package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.Interfaces.LoginInterface;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.jdb;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;
import org.vaadin.teemu.VaadinIcons;

/**
 *
 * @author Luca Mezzolla
 */
public class LoginComponent extends CustomComponent implements Button.ClickListener {
    
    private final VerticalLayout vl;
    private TextField userTextField;
    private PasswordField secretPasswordField;
    private Button recoverButton, submitButton;
    private LoginInterface listener;
    private jdb db;

    public void setListener(LoginInterface listener) {
        this.listener = listener;
    }
    
    public LoginComponent(jdb db) {
        this.db = db;
        vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setWidth("100%");
        super.setCompositionRoot(vl);
        buildUI();
    }
    
    private void buildUI() {
        vl.removeAllComponents();
        userTextField = new TextField(Messages.EMAIL);
        userTextField.setWidth("100%");
        userTextField.setIcon(VaadinIcons.USER);
        userTextField.focus();
        secretPasswordField = new PasswordField(Messages.PASSWORD1);
        secretPasswordField.setWidth("100%");
        secretPasswordField.setIcon(VaadinIcons.PASSWORD);
        submitButton = new Button(Messages.SUBMIT, this);
        submitButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        recoverButton = new Button(Messages.RECOVER_PASSWORD, this);
        recoverButton.setStyleName(Runo.BUTTON_DEFAULT);
        HorizontalLayout hlButtons = new HorizontalLayout(recoverButton, submitButton);
        hlButtons.setSpacing(true);
        vl.addComponents(userTextField, secretPasswordField, hlButtons);
        vl.setComponentAlignment(hlButtons, Alignment.MIDDLE_RIGHT);
//        userTextField.setValue("luca.mezzolla@urbe.aero");
//        secretPasswordField.setValue("trustno1");
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        Button foo = event.getButton();
        if(foo != null && foo == submitButton) {
            if(userTextField.getValue().length() > 0 && secretPasswordField.getValue().length() > 0) {
                User user = Queries.SELECT_LOGIN(db, userTextField.getValue(), secretPasswordField.getValue());
                if(listener != null && user.getId() > 0) {
                    listener.onLogin(user);
                } else {
                    Notification.show(Messages.ERROR, Messages.LOGIN_ERROR, Notification.Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show(Messages.ERROR, Messages.ERROR_EMPTY_FIELDS, Notification.Type.WARNING_MESSAGE);
            }
        }
        if(foo != null && foo == recoverButton) {
            SendPasswordEmailComponent win = new SendPasswordEmailComponent(db);
            UI.getCurrent().addWindow(win);
        }
    }
    
}