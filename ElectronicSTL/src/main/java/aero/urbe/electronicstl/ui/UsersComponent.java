package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Constants;
import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.Interfaces.TableInterface;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.MyClasses.MyUtilities;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.jdb;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Runo;
import java.sql.SQLException;

/**
 *
 * @author Luca Mezzolla
 */
public class UsersComponent extends AbstractTable implements Button.ClickListener, TableInterface {

    private final jdb db;
    private final User user;
    
    public UsersComponent(jdb db, User user) {
        this.db = db;
        this.user = user;
        table.addContainerProperty("Name", String.class, null);
        table.addContainerProperty("Lastname", String.class, null);
        table.addContainerProperty("Phone", String.class, null);
        table.addContainerProperty("Email", String.class, null);
        table.addContainerProperty("Level", String.class, null);
        table.setColumnWidth(("Name"), 200);
        table.setColumnWidth(("Lastname"), 200);
        table.setColumnWidth(("Phone"), 200);
        table.setColumnWidth(("Email"), 200);
        addItemButton.addClickListener(this);
        addItemButton.setStyleName(Runo.BUTTON_SMALL);
        searchField.setInputPrompt(Messages.SEARCH_BY_NAME_LASTNAME);
        hl.addComponents(searchField, addItemButton);
        hl.setExpandRatio(addItemButton, 0.05f);
        hl.setExpandRatio(searchField, 0.95f);
        search("%%%");
        setPrivileges();
    }
    
    private void setPrivileges() {
        if(user.getLevelId() == Constants.USER) {
            addItemButton.setVisible(false);
            table.removeItemClickListener(this);
        }
        if(user.getLevelId() == Constants.DEVICE_USER) {
            addItemButton.setVisible(false);
            table.removeItemClickListener(this);
        }
        if(user.getLevelId() == Constants.TECHNICAL) {
            addItemButton.setVisible(false);
            table.removeItemClickListener(this);
        }
        if(user.getLevelId() == Constants.ADMIN) {
            addItemButton.setVisible(true);
        }
        if(user.getLevelId() == Constants.SUPER_ADMIN) {
            addItemButton.setVisible(true);
        }
    }
    
    private void buildUserWindow(jdb db, Integer id) {
        try {
            UserWindow win = new UserWindow(db, id);
            win.setListener(this);
            win.setId("UserWindow");
            UI.getCurrent().addWindow(win);
        } catch (SQLException ex) {
            MyNotification.SHOW("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }
    
    @Override
    void search(String text) {
        Queries.SELECT_USERS(db, text, table, user.getId());
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == addItemButton) {
            //ADD USER
            if(MyUtilities.CHECK_LEVEL(user.getLevelId(), Constants.ADMIN)) {
                buildUserWindow(db, -1);
            } else {
                Notification.show(Messages.ERROR, Messages.ERROR_LOW_LEVEL, Type.ERROR_MESSAGE);
            }   
        }
    }

    @Override
    void edit(Integer itemId) {
        //EDIT USER
        if(MyUtilities.CHECK_LEVEL(user.getLevelId(), Constants.ADMIN)) {
            buildUserWindow(db, (Integer) itemId);
        } else {
            Notification.show(Messages.ERROR, Messages.ERROR_LOW_LEVEL, Type.ERROR_MESSAGE);
        }
    }

    @Override
    public void updateTable(String tableId) {
        searchField.setValue("");
        search("%%%");
    }

}