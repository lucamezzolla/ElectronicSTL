package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.jdb;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Runo;
import java.util.HashMap;

/**
 *
 * @author Luca Mezzolla
 */
public class ProfileWindow extends Window implements Button.ClickListener {
    
    private final jdb db;
    private User user;
    private VerticalLayout vl;
    private TextField name;
    private TextField lastname;
    private TextField email;
    private TextField phone;
    private final Page.Styles styles = Page.getCurrent().getStyles();
    private Button submit;
    private Button changePass;
    private Button cancel;

    public ProfileWindow(jdb db, User user) {
        this.db = db;
        this.user = user;
        styles.add("#disabled { pointer-events: none; background-image: none; background-color: #dbdbdb }");
        vl = vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setWidth("100%");
        super.setCaption("Profile");
        super.setModal(true);
        super.setWidth("40%");
        super.setResizable(false);
        super.setContent(vl);
        super.center();
        buildUI();
    }

    private void buildUI() {
        vl.removeAllComponents();
        name = buildTextField(Messages.NAME);
        lastname = buildTextField(Messages.LASTNAME);
        email = buildTextField(Messages.EMAIL);
        phone = buildTextField(Messages.PHONE);
        name.setValue(user.getName());
        lastname.setValue(user.getLastname());
        email.setValue(user.getEmail());
        phone.setValue(user.getPhone());
        name.setId("disabled");
        lastname.setId("disabled");
        email.setId("disabled");
        submit = new Button(Messages.SUBMIT, this);
        changePass = new Button("Change Password", this);
        cancel = new Button(Messages.CANCEL, this);
        cancel.setStyleName(Runo.BUTTON_DEFAULT);
        submit.setClickShortcut(KeyCode.ENTER);
        phone.focus();
        HorizontalLayout hlButtons = new HorizontalLayout(cancel, changePass, submit);
        hlButtons.setSpacing(true);
        vl.addComponents(name, lastname, email, phone, hlButtons);
        vl.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
    }
    
    private TextField buildTextField(String caption) {
        TextField foo = new TextField(caption);
        foo.setWidth("100%");
        foo.setImmediate(true);
        return foo;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == submit) {
            if(phone.getValue().length() > 0) {
                try {
                    HashMap<String, String> array = new HashMap<>();
                    array.put("phone", phone.getValue());
                    array.put("level", String.valueOf(user.getLevelId()));
                    user.setPhone(phone.getValue());
                    Queries.UPDATE_USER(db, array, user.getId());
                    MyNotification.SHOW(Messages.DB_UPDATED, Type.HUMANIZED_MESSAGE);
                    super.close();
                } catch (Exception ex) {
                    MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
                }
            } else {
                MyNotification.SHOW(Messages.ERROR, Messages.ERROR_EMPTY_FIELDS, Type.ERROR_MESSAGE);
            }
        }
        if(event.getButton() == changePass) {
            ChangePasswordWindow win = new ChangePasswordWindow(db, user.getId());
            UI.getCurrent().addWindow(win);
        }
        if(event.getButton() == cancel) {
            super.close();
        }
    }
    
    
    
}