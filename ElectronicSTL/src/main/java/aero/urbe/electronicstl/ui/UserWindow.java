package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.Interfaces.TableInterface;
import aero.urbe.electronicstl.MyClasses.MyItem;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.jdb;
import com.vaadin.data.Validator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Runo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Luca Mezzolla
 */
public class UserWindow extends Window implements Button.ClickListener {

    private VerticalLayout vl;
    private final jdb db;
    private ComboBox level;
    private Button submit, cancel, changePass;
    private TextField name;
    private TextField lastname;
    private TextField email;
    private PasswordField pass1;
    private PasswordField pass2;
    private TextField phone;
    private final Integer id;
    private TableInterface listener;
    private VerticalLayout vl1;
    private VerticalLayout vl2;
    private final Page.Styles styles = Page.getCurrent().getStyles();
    
    public UserWindow(jdb db, Integer id) throws SQLException {
        this.db = db;
        this.id = id;
        vl = vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setWidth("100%");
        styles.add("#disabled { pointer-events: none; background-image: none; background-color: #dbdbdb }");
        super.setCaption(Messages.ADD_USER);
        super.setModal(true);
        super.setWidth("550px");
        super.setResizable(false);
        super.setContent(vl);
        super.center();
        buildUI();
        editUser();
    }

    public void setListener(TableInterface listener) {
        this.listener = listener;
    }
    
    private void buildUI() throws SQLException {
        vl.removeAllComponents();
        vl1 = new VerticalLayout();
        vl2 = new VerticalLayout();
        name = buildTextField(Messages.NAME);
        lastname = buildTextField(Messages.LASTNAME);
        email = buildTextField(Messages.EMAIL);
        pass1 = buildPassField(Messages.PASSWORD1);
        pass2 = buildPassField(Messages.PASSWORD2);
        phone = buildTextField(Messages.PHONE);
        level = new ComboBox(Messages.LEVEL);
        submit = new Button(Messages.SUBMIT, this);
        changePass = new Button("Change Password", this);
        changePass.setVisible(false);
        submit.setId("ADD");
        submit.setClickShortcut(KeyCode.ENTER);
        cancel = new Button(Messages.CANCEL, this);
        cancel.setStyleName(Runo.BUTTON_DEFAULT);
        vl1.setSpacing(true);
        vl2.setSpacing(true);
        vl1.setWidth("100%");
        vl2.setWidth("100%");
        level.setNullSelectionAllowed(false);
        level.setTextInputAllowed(false);
        level.setWidth("100%");
        name.focus();
        vl1.addComponents(name, lastname, phone, email, pass1, pass2, level);
        vl2.addComponents(new Image(null, new ThemeResource("pics/pilot.png")));
        vl2.setComponentAlignment(vl2.getComponent(0), Alignment.TOP_RIGHT);
        HorizontalLayout hl = new HorizontalLayout(vl1, vl2);
        HorizontalLayout hlButtons = new HorizontalLayout(cancel, changePass, submit);
        hlButtons.setSpacing(true);
        hl.setSpacing(true);
        hl.setWidth("100%");
        vl.addComponents(hl, hlButtons);
        vl.setComponentAlignment(hlButtons, Alignment.MIDDLE_RIGHT);
        setLevels();
    }
    
    private void editUser() {
        if(id > 0) {
            //TITLE
            super.setCaption(Messages.EDIT_USER);
            //UI
            ArrayList<Object> user = Queries.SELECT_USER(db, id);
            name.setValue((String)user.get(0));
            lastname.setValue((String)user.get(1));
            phone.setValue((String)user.get(2));
            email.setValue((String)user.get(3));
            Integer levelId = (Integer)user.get(4);
            if(levelId == 5) {
                TextField levelSuperAdmin = new TextField(Messages.LEVEL, "Super Administrator");
                levelSuperAdmin.setWidth("100%");
                levelSuperAdmin.setId("disabled");
                vl1.replaceComponent(level, levelSuperAdmin);
            } else {
                Iterator<?> iterator = level.getItemIds().iterator();
                while(iterator.hasNext()) {
                    MyItem next = (MyItem) iterator.next();
                    Integer nextid = next.getId();
                    if(nextid == levelId) {
                        level.select(next);
                    }
                }
            }
            name.setId("disabled");
            lastname.setId("disabled");
            email.setId("disabled");
            changePass.setId("disabled");
            phone.focus();
            vl1.removeComponent(pass1);
            vl1.removeComponent(pass2);
            //BUTTON
            submit.setId("EDIT");
        }
    }
    
    private TextField buildTextField(String caption) {
        TextField foo = new TextField(caption);
        foo.setWidth("100%");
        foo.setImmediate(true);
        foo.addValidator(new MyValidator());
        return foo;
    }
    
    private PasswordField buildPassField(String caption) {
        PasswordField foo = new PasswordField(caption);
        foo.setWidth("100%");
        foo.setImmediate(true);
        foo.addValidator(new MyValidator());
        return foo;
    }
    
    private void setLevels() throws SQLException {
        ResultSet rs = db.query(Queries.SELECT_STL_LEVELS);
        while(rs.next()) {
            MyItem foo = new MyItem(rs.getInt("id"), rs.getString("name"));
            level.addItem(foo);
        }
        rs.close();
        level.select(level.getItemIds().iterator().next());
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == submit) {
            try {
                if(event.getButton().getId().equals("ADD")) {
                    if(name.isValid() && lastname.isValid() && email.isValid()
                            && phone.isValid() && pass1.isValid() && pass2.isValid()) {
                        if(pass1.getValue().equals(pass2.getValue())) {
                            HashMap<String, String> values = new HashMap<>();
                            values.put("name", name.getValue());
                            values.put("lastname", lastname.getValue());
                            values.put("phone", phone.getValue());
                            values.put("email", email.getValue());
                            values.put("password", pass1.getValue());
                            values.put("level", String.valueOf(((MyItem) level.getValue()).getId()));
                            Queries.INSERT_USER(db, values);
                            MyNotification.SHOW(Messages.DB_UPDATED, Notification.Type.HUMANIZED_MESSAGE);
                            closeWindow();
                        } else {
                            MyNotification.SHOW("Error", "Check Password Field", Notification.Type.ERROR_MESSAGE);
                        }
                    }
                } else if(event.getButton().getId().equals("EDIT")) {
                    if(phone.isValid()) {
                        HashMap<String, String> values = new HashMap<>();
                        values.put("phone", phone.getValue());
                        values.put("level", String.valueOf(((MyItem) level.getValue()).getId()));
                        Queries.UPDATE_USER(db, values, id);
                        MyNotification.SHOW(Messages.DB_UPDATED, Notification.Type.HUMANIZED_MESSAGE);
                        closeWindow();
                    }
                }
                if(listener != null) {
                    listener.updateTable(null);
                }
            } catch (Exception ex) {
                MyNotification.SHOW("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        }
        if(event.getButton() == changePass) {
            ChangePasswordWindow win = new ChangePasswordWindow(db, id);
            UI.getCurrent().addWindow(win);
        }
        if(event.getButton() == cancel) {
            closeWindow();
        }
    }
    
    private void closeWindow() {
        Collection<Window> windows = UI.getCurrent().getWindows();
        Iterator<Window> iterator = windows.iterator();
        while(iterator.hasNext()) {
            Window foo = (Window) iterator.next();
            if(foo.getId() != null) {
                if(foo.getId().equals("UserWindow")) {
                    foo.close();
                }
            }
        }
    }

    class MyValidator implements Validator {
        @Override
        public void validate(Object value) throws InvalidValueException {
            if (!(value instanceof String && ((String)value).length() > 0))
                throw new InvalidValueException("Empty Field!");
        }
    }
    
}