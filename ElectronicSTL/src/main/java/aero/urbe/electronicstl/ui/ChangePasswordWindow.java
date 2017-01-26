package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.jdb;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luca Mezzolla
 */
public class ChangePasswordWindow extends Window implements Button.ClickListener {
    
    private final jdb db;
    private User user;
    private VerticalLayout vl;
    private PasswordField pass1;
    private PasswordField pass2;
    private Button submit;
    private Button cancel;
    private final Integer id;

    public ChangePasswordWindow(jdb db, Integer id) {
        this.db = db;
        this.id = id;
        vl = vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setWidth("100%");
        super.setCaption("Change Password");
        super.setModal(true);
        super.setWidth("30%");
        super.setResizable(false);
        super.setContent(vl);
        super.center();
        buildUI();
    }

    private void buildUI() {
        vl.removeAllComponents();
        pass1 = buildPassField("Password:");
        pass2 = buildPassField("Confirm Password:");
        submit = new Button(Messages.SUBMIT, this);
        cancel = new Button(Messages.CANCEL, this);
        cancel.setStyleName(Runo.BUTTON_DEFAULT);
        submit.setClickShortcut(KeyCode.ENTER);
        pass1.focus();
        HorizontalLayout hlButtons = new HorizontalLayout(cancel, submit);
        hlButtons.setSpacing(true);
        vl.addComponents(pass1, pass2, hlButtons);
        vl.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
    }
    
    private PasswordField buildPassField(String caption) {
        PasswordField foo = new PasswordField(caption);
        foo.setWidth("100%");
        foo.setImmediate(true);
        return foo;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == submit) {
            if(pass1.getValue().length() > 0 && pass2.getValue().length() > 0) {
                if(pass1.getValue().equals(pass2.getValue())) {
                    try {
                        Queries.UPDATE_PASSWORD(db, pass1.getValue(), id);
                        MyNotification.SHOW(Messages.DB_UPDATED, Type.HUMANIZED_MESSAGE);
                        super.close();
                    } catch (Exception ex) {
                        MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
                    }
                } else {
                    MyNotification.SHOW("Password does not match the confirm password", Type.WARNING_MESSAGE);
                }
            } else {
                MyNotification.SHOW(Messages.ERROR, Messages.ERROR_EMPTY_FIELDS, Type.ERROR_MESSAGE);
            }
        }
        if(event.getButton() == cancel) {
            super.close();
        }
    }
    
}
