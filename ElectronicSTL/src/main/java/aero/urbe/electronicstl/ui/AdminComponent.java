package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Constants;
import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.MyClasses.MyItem;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.MyClasses.MyUtilities;
import aero.urbe.electronicstl.MyClasses.SimulatorStatusItem;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.jdb;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.teemu.VaadinIcons;

/**
 *
 * @author Luca Mezzolla
 */
public class AdminComponent extends CustomComponent implements Button.ClickListener, AdminInterface, Property.ValueChangeListener, ItemClickEvent.ItemClickListener {
    
    private final jdb db;
    private final User user;
    private Table simsStatusTable;
    private final VerticalLayout layout;
    private Table table;
    private ComboBox advancedCombo;
    private Button addItemButton;
    private HorizontalLayout hl;
    private ItemClickEvent.ItemClickListener simStatusTableItemListener;
    
    public AdminComponent(jdb db, User user) {
        this.db = db;
        this.user = user;
        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add("#centered { vertical-align: middle }");
        layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeFull();
        super.setCompositionRoot(layout);
        buildUI();
        if(MyUtilities.CHECK_LEVEL(user.getLevelId(), Constants.ADMIN)) {
            addItemButton.setVisible(true);
        } else {
            addItemButton.setVisible(false);
            simsStatusTable.removeItemClickListener(simStatusTableItemListener);
        }
    }
    
    private void buildUI() {
        layout.removeAllComponents();
        hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.setSpacing(true);
        hl.setWidthUndefined();
        addItemButton = new Button(VaadinIcons.PLUS);
        addItemButton.addClickListener(this);
        advancedCombo = MyUtilities.buildComboBox(null);
        advancedCombo.addItem(Constants.CUSTOMERS_ITEM);
        advancedCombo.addItem(Constants.FREQUENT_USERS_ITEM);
        advancedCombo.addItem(Constants.SIMULATORS_ITEM);
        advancedCombo.addItem(Constants.SIMULATORS_STATUS_ITEM);
        advancedCombo.addValueChangeListener(this);
        advancedCombo.setWidthUndefined();
        hl.addComponents(addItemButton, advancedCombo);
        //-----------------------------------------------------------------------
        table = new Table();
        table.setImmediate(true);
        table.setSelectable(true);
        table.addItemClickListener(this);
        table.setSizeFull();       
        table.addContainerProperty(Messages.NAME, String.class, null);
        table.addContainerProperty(Messages.ACTUAL+" "+Messages.TTL, String.class, null);
        table.setColumnExpandRatio(Messages.NAME, 0.1f);
        table.setColumnExpandRatio(Messages.ACTUAL+" "+Messages.TTL, 0.9f);
        table.setId(Messages.CUSTOMERS);
        table.setColumnCollapsingAllowed(true);
        table.setColumnCollapsed(Messages.ACTUAL+" "+Messages.TTL, true);
        //--------------------------- TABLE OF SIM STATUS
        simsStatusTable = new Table();
        simsStatusTable.addContainerProperty("Status", Image.class, null);
        simsStatusTable.addContainerProperty("Name", String.class, null);
        simsStatusTable.setSelectable(true);
        simsStatusTable.setColumnWidth("Status", 70);
        simsStatusTable.setColumnAlignment("Status", Table.Align.CENTER);
        //-----------------------------------------------------------------------
        simStatusTableItemListener = new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if(event.isDoubleClick()) {
                    if(event.getItemId() != null) {
                        final SimulatorStatusItem foo = (SimulatorStatusItem) event.getItemId();
                        final Window win = new Window(Messages.SIMULATOR_STATUS);
                        win.setModal(true);
                        win.setWidth("300px");
                        win.setResizable(false);
                        VerticalLayout vl = new VerticalLayout();
                        vl.setSpacing(true);
                        vl.setMargin(true);
                        vl.setWidth("100%");
                        final OptionGroup og = new OptionGroup();
                        MyItem red = new MyItem(1, Messages.RED);
                        MyItem yellow = new MyItem(2, Messages.YELLOW);
                        MyItem green = new MyItem(3, Messages.GREEN);
                        og.addItem(red);
                        og.addItem(yellow);
                        og.addItem(green);
                        if(foo.getSimulatorStatusId() == 1) {
                            og.select(red);
                        }
                        if(foo.getSimulatorStatusId() == 2) {
                            og.select(yellow);
                        }
                        if(foo.getSimulatorStatusId() == 3) {
                            og.select(green);
                        }
                        Button submit = new Button(Messages.SUBMIT, new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                MyItem ogItem = (MyItem) og.getValue();
                                if(ogItem.getId() != foo.getSimulatorStatusId()) {
                                    Queries.INSERT_SIMULATOR_STATUS(db, foo.getSimulatorId(), ogItem.getId());
                                }
                                updateTable(Messages.SIMULATOR_STATUS);
                                win.close();
                            }
                        });
                        Button cancel = new Button(Messages.CANCEL, new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                win.close();
                            }
                        });
                        cancel.setStyleName(Runo.BUTTON_DEFAULT);
                        HorizontalLayout hl = new HorizontalLayout(cancel, submit);
                        hl.setSpacing(true);
                        vl.addComponents(og, hl);
                        vl.setComponentAlignment(hl, Alignment.BOTTOM_RIGHT);
                        win.setContent(vl);
                        win.center();
                        UI.getCurrent().addWindow(win);
                    }
                }
            }
        };
        simsStatusTable.addItemClickListener(simStatusTableItemListener);
        simsStatusTable.setSizeFull();
        //-----------------------------------------------------------------------
        table.setVisible(false);
        layout.addComponents(hl, table, simsStatusTable);
        layout.setExpandRatio(table, 9);
        layout.setExpandRatio(simsStatusTable, 9);
        advancedCombo.select(Constants.SIMULATORS_ITEM);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == addItemButton) {
            String id = table.getId();
            AddItem win = new AddItem(db, table.getId());
            win.setId("AdminItemWindow");
            win.setListener(this);
            UI.getCurrent().addWindow(win);
        }
    }

    @Override
    public void updateTable(String tableId) {
        simsStatusTable.setVisible(false);
        table.setCaption("");
        table.removeAllItems();
        if(tableId.equals(Messages.CUSTOMERS)) {
            table.setCaption(Messages.CUSTOMERS);
            ArrayList<MyItem> array = null;
            table.setId(Messages.CUSTOMERS);
            table.setColumnCollapsed(Messages.ACTUAL+" "+Messages.TTL, true);
            array = Queries.SELECT_TABLE(db, "stl_customers");
            if(array.size() > 0) {
                for(MyItem foo : array) {
                    table.addItem(new Object[] { foo.getName(), "" }, foo.getId());
                }
                table.setVisible(true);
            } else {
                table.setVisible(false);
            }
        }
        if(tableId.equals(Messages.FREQUENT_USERS)) {
            table.setCaption(Messages.FREQUENT_USERS+" (Double click to edit)");
            table.setId(Messages.FREQUENT_USERS);
            table.setColumnCollapsed(Messages.ACTUAL+" "+Messages.TTL, true);
            ArrayList<MyItem> frequentUsers = Queries.SELECT_TABLE(db, "stl_frequent_users");
            if(frequentUsers.size() > 0) {
                table.setVisible(true);
                for(MyItem foo : frequentUsers) {
                    table.addItem(new Object[] { foo.getName(), "" }, foo);
                }
            } else {
                table.setVisible(false);
            }
        }
        if(tableId.equals(Messages.SIMULATORS)) {
            table.setCaption(Messages.SIMULATORS);
            simsStatusTable.setVisible(false);
            ArrayList<Map<String, String>> array = null;
            table.setId(Messages.SIMULATORS); 
            table.setColumnCollapsed(Messages.ACTUAL+" "+Messages.TTL, false);
            array = Queries.SELECT_SIMULATORS2(db);
            if(array.size() > 0) {
                for(Map<String, String> foo : array) {
                    String ttlValue = "error";
                    if(foo.get("ttl").equals("0")) {
                        ttlValue = "No TTL";
                    } else {
                        ttlValue = foo.get("actual_ttl");
                    }
                    table.addItem(new Object[] { foo.get("name"), ttlValue }, foo.get("id"));
                }
                table.setVisible(true);
            } else {
                table.setVisible(false);
            }
        }
        if(tableId.equals(Messages.SIMULATOR_STATUS)) {
            table.setVisible(false);
            simsStatusTable.setCaption(Messages.SIMULATOR_STATUS+" (Double click to edit)");
            simsStatusTable.removeAllItems();
            ArrayList<MyItem> sims = Queries.SELECT_SIMULATORS1(db);
            if(sims.size() > 0) {
                Iterator iterator = sims.iterator();
                while(iterator.hasNext()) {
                    MyItem sim = (MyItem) iterator.next();
                    SimulatorStatusItem simStatusItem = Queries.SELECT_LAST_STATUS_SIMULATOR(db, sim.getId());
                    Image image = null;
                    if(simStatusItem.getSimulatorStatusId() == 1) {
                        image = new Image(null, new ThemeResource("red.png"));
                    }
                    if(simStatusItem.getSimulatorStatusId() == 2) {
                        image = new Image(null, new ThemeResource("yellow.png"));
                    }
                    if(simStatusItem.getSimulatorStatusId() == 3) {
                        image = new Image(null, new ThemeResource("green.png"));
                    }
                    image.setWidth("40px");
                    image.setHeight("15px");
                    image.setId("centered");
                    simsStatusTable.addItem(new Object[] { image, simStatusItem.getSimulator() + " ("+simStatusItem.getSimulatorStatus()+")" }, simStatusItem);
                }
                simsStatusTable.setVisible(true);
            } else {
                simsStatusTable.setVisible(false);
            }
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if(event.getProperty().getValue().equals(Constants.CUSTOMERS_ITEM)) {
            if(MyUtilities.CHECK_LEVEL(user.getLevelId(), Constants.ADMIN)) {
                addItemButton.setVisible(true);
            }
            table.setId(Messages.CUSTOMERS);
            updateTable(Messages.CUSTOMERS);
        }
        if(event.getProperty().getValue().equals(Constants.FREQUENT_USERS_ITEM)) {
            if(MyUtilities.CHECK_LEVEL(user.getLevelId(), Constants.ADMIN)) {
                addItemButton.setVisible(true);
            }
            table.setId(Messages.FREQUENT_USERS);
            updateTable(Messages.FREQUENT_USERS);
        }
        if(event.getProperty().getValue().equals(Constants.SIMULATORS_ITEM)) {
            if(MyUtilities.CHECK_LEVEL(user.getLevelId(), Constants.ADMIN)) {
                addItemButton.setVisible(true);
            }
            table.setId(Messages.SIMULATORS);
            updateTable(Messages.SIMULATORS);            
        }
        if(event.getProperty().getValue().equals(Constants.SIMULATORS_STATUS_ITEM)) {
            addItemButton.setVisible(false);
            updateTable(Messages.SIMULATOR_STATUS);
        }
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        if(event.isDoubleClick()) {
            //edit((Integer)event.getItemId());
            if(table.getId().equals(Messages.FREQUENT_USERS)) {
                final MyItem foo = (MyItem) event.getItemId();
                final Window win = new Window("Edit...");
                win.setWidth("30%");
                win.setResizable(false);
                win.setModal(true);
                VerticalLayout vl = new VerticalLayout(); vl.setMargin(true); vl.setSpacing(true); vl.setWidth("100%");
                final TextField name = new TextField(); name.setValue(foo.getName()); name.setWidth("100%"); name.focus();
                Button submit = new Button(Messages.SUBMIT, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        if(name.getValue().length() > 0) {
                            Queries.UPLOAD_TABLE(db, "stl_frequent_users", foo.getId(), name.getValue());
                            win.close();
                            updateTable(Messages.FREQUENT_USERS);
                            Notification.show("Updated!");
                        } else {
                            Notification.show(Messages.ERROR, Messages.ERROR_EMPTY_FIELDS, Type.ERROR_MESSAGE);
                        }
                    }
                });
                submit.setClickShortcut(KeyCode.ENTER);
                vl.addComponents(name, submit);
                vl.setComponentAlignment(submit, Alignment.BOTTOM_RIGHT);
                win.setContent(vl);
                UI.getCurrent().addWindow(win);
            } else {
                MyNotification.SHOW(Messages.WARNING_NOT_EDITABLE, Notification.Type.WARNING_MESSAGE);
            }
        }
    }

}

interface AdminInterface {
    void updateTable(String tableId);
}

class AddItem extends Window implements Button.ClickListener {
    
    private final jdb db;
    private final String tableId;
    private final VerticalLayout vl;
    private Button submitButton;
    private Button cancelButton;
    private TextField name;
    private CheckBox ttl;
    private TextField ttlStart;
    private AdminInterface listener;

    public AddItem(jdb db, String tableId) {
        this.db = db;
        this.tableId = tableId;
        vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        super.setContent(vl);
        super.setWidth("30%");
        super.setResizable(false);
        super.setModal(true);
        super.center();
        buildUI();
    }

    public void setListener(AdminInterface listener) {
        this.listener = listener;
    }

    private void buildUI() {
        vl.removeAllComponents();
        name = new TextField(Messages.NAME);
        vl.addComponent(name);
        if(tableId.equals(Messages.CUSTOMERS)) {
            super.setCaption(Messages.ADD_CUSTOMERS);
        }
        if(tableId.equals(Messages.FREQUENT_USERS)) {
            super.setCaption(Messages.ADD_FREQUENT_USER);
        }
        if(tableId.equals(Messages.SIMULATORS)) {
            super.setCaption(Messages.ADD_SIM);
            ttl = new CheckBox(Messages.HAS_TTL, false);
            ttl.setImmediate(true);
            ttlStart = new TextField();
            ttlStart.setImmediate(true);
            ttlStart.setWidth("100%");
            ttlStart.setValue("0.0");
            vl.addComponents(ttl, ttlStart);
            ttlStart.setVisible(false);
            ttl.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    ttlStart.setVisible(ttl.getValue());
                }
            });
        }
        name.setWidth("100%");
        name.focus();
        submitButton = new Button(Messages.SUBMIT, this);
        cancelButton = new Button(Messages.CANCEL, this);
        cancelButton.setStyleName(Runo.BUTTON_DEFAULT);
        HorizontalLayout hlButtons = new HorizontalLayout(cancelButton, submitButton);
        hlButtons.setSpacing(true);
        submitButton.setClickShortcut(KeyCode.ENTER);
        vl.addComponent(hlButtons);
        vl.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == submitButton) {
            if(name.getValue().length() > 0) {
                try {
                    if(tableId.equals(Messages.CUSTOMERS)) {
                        Queries.INSERT_TABLE(db, "stl_customers", name.getValue());
                    }
                    if(tableId.equals(Messages.FREQUENT_USERS)) {
                        Queries.INSERT_TABLE(db, "stl_frequent_users", name.getValue());
                    }
                    if(tableId.equals(Messages.SIMULATORS)) {
                        int ttlValue = 0;
                        String ttlStartValue = ttlStart.getValue();
                        if(ttl.getValue())
                            ttlValue = 1;
                        Queries.INSERT_SIMULATOR(db, name.getValue(), ttlValue, ttlStartValue);
                    }
                    if(listener != null) {
                        listener.updateTable(tableId);
                    }
                    closeWindow();
                } catch (SQLException ex) {
                    MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
                } catch (Exception ex) {
                    Logger.getLogger(AddItem.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                MyNotification.SHOW(Messages.ERROR_EMPTY_FIELDS, Notification.Type.WARNING_MESSAGE);
            }
        }
        if(event.getButton() == cancelButton) {
            closeWindow();
        }
    }
    
    private void closeWindow() {
        Collection<Window> windows = UI.getCurrent().getWindows();
        Iterator<Window> iterator = windows.iterator();
        while(iterator.hasNext()) {
            Window foo = (Window) iterator.next();
            if(foo.getId() != null) {
                if(foo.getId().equals("AdminItemWindow")) {
                    foo.close();
                }
            }
        }
    }
    
}
