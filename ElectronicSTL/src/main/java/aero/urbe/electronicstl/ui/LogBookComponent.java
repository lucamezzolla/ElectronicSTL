package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Constants;
import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.MyClasses.ConfirmDialog;
import aero.urbe.electronicstl.MyClasses.ConfirmDialogInterface;
import aero.urbe.electronicstl.MyClasses.DefectItem;
import aero.urbe.electronicstl.MyClasses.LogBookItem;
import aero.urbe.electronicstl.MyClasses.LogBookPageItem;
import aero.urbe.electronicstl.MyClasses.MyItem;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.MyClasses.MyUtilities;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.jdb;
import com.vaadin.data.Property;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;
import eu.maxschuster.vaadin.autocompletetextfield.provider.CollectionSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.provider.MatchMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.vaadin.teemu.VaadinIcons;

/**
 *
 * @author Luca Mezzolla
 */
public class LogBookComponent extends CustomComponent implements Button.ClickListener, ItemClickEvent.ItemClickListener, LogBookTable {
    
    private final jdb db;
    private final User user;
    private final PopupDateField from;
    private final PopupDateField to;
    private final ComboBox selectSim;
    private final SimpleDateFormat formatDB;
    private final Button searchButton;
    private final Table table;
    private final Button addItemButton;
    private final VerticalLayout layout;
    
    public LogBookComponent(final jdb db, User user) {
        this.db = db;
        this.user = user;
        layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeFull();
        super.setCompositionRoot(layout);
        formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        table = new Table();
        table.setSizeFull(); 
        table.setImmediate(true);
        table.setSelectable(true);
        table.addItemClickListener(this);
        table.addContainerProperty("Page ID", String.class, null);
        table.addContainerProperty("Date", String.class, null);
        table.addContainerProperty("Author", String.class, null);
        table.addContainerProperty("Start (actual)", String.class, null);
        table.addContainerProperty("End (actual)", String.class, null);
        table.addContainerProperty("Total Time (actual)", String.class, null);
        table.addContainerProperty("Device Users", String.class, null);
        table.addContainerProperty("Students", String.class, null);
        table.setVisible(false);
        table.setColumnWidth("Page ID", 90);
        table.setColumnAlignment("Page ID", Table.Align.CENTER);
        addItemButton = new Button(VaadinIcons.PLUS);
        addItemButton.setWidth("100%");
        addItemButton.addClickListener(this);
        from = MyUtilities.buildPopupDateField(Messages.FROM, false, null);
        to = MyUtilities.buildPopupDateField(Messages.TO, true,null);
        selectSim = MyUtilities.buildComboBox(Messages.SIMULATOR);
        from.setDateFormat("dd-MMM-yyyy"); to.setDateFormat("dd-MMM-yyyy");
        from.setLocale(Locale.UK); to.setLocale(Locale.UK);
        searchButton = new Button(Messages.SEARCH, this);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.addComponents(addItemButton, from, to, selectSim, searchButton);
        hl.setComponentAlignment(addItemButton, Alignment.BOTTOM_LEFT);
        hl.setComponentAlignment(searchButton, Alignment.BOTTOM_LEFT);
        selectSim.removeAllItems();
        ArrayList<MyItem> sims = Queries.SELECT_SIMULATORS1(db);
        for(MyItem foo : sims) {
            selectSim.addItem(foo);
        }
        selectSim.addFocusListener(new FieldEvents.FocusListener() {
            @Override
            public void focus(FieldEvents.FocusEvent event) {
                selectSim.removeAllItems();
                ArrayList<MyItem> sims = Queries.SELECT_SIMULATORS1(db);
                for(MyItem foo : sims) {
                    selectSim.addItem(foo);
                }
                if(selectSim.size() > 0) {
                    selectSim.select(selectSim.getItemIds().iterator().next());
                    searchButton.setEnabled(true);
                } else {
                    searchButton.setEnabled(false);
                }
            }
        });
        if(selectSim.size() > 0) {
            selectSim.select(selectSim.getItemIds().iterator().next());
            searchButton.setEnabled(true);
        } else {
            searchButton.setEnabled(false);
        }
        layout.addComponents(hl, table);
        layout.setExpandRatio(table, 1f);
        setPrivileges();
        if(!selectSim.isEmpty()) {
            searchButton.click();
        }
    }
    
    @Override
    public void itemClick(ItemClickEvent event) {
        if(table.getValue() != null && table.getValue() instanceof LogBookItem) {
            LogBookItem item = (LogBookItem) table.getValue();
            try {
                LogBookPageItem pageItem = Queries.SELECT_PAGE(db, item.getId());
                LogBookPage page = new LogBookPage(db, pageItem);
                UI.getCurrent().addWindow(page);
            } catch (ParseException ex) {
                MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == addItemButton) {
            //Check all item in db
            if(!Queries.CHECK_ALL_DB_ITEM(db)) {
                Notification.show(Messages.ERROR, Messages.ERROR_ITEM_MISSING, Notification.Type.ERROR_MESSAGE);
                return;
            }
            //Select Simulator with Warning and start...
            SelectSim selectSim = new SelectSim(db, user);
            selectSim.setListener(this);
            UI.getCurrent().addWindow(selectSim);
        }
        if(event.getButton() == searchButton) {
            try {
                MyItem myItem = (MyItem)selectSim.getValue();
                int simulatorId = myItem.getId();
                Date start = from.getValue();
                Date end = to.getValue();
                start.setHours(0); start.setMinutes(0); start.setSeconds(0);
                end.setHours(23); end.setMinutes(59); end.setSeconds(59);
                if(start != null || end != null) {
                    if(end.getTime() <= start.getTime()) {
                        MyNotification.SHOW(Messages.ERROR, Messages.ERROR_DATES, Notification.Type.ERROR_MESSAGE);
                    } else {
                        ArrayList<LogBookItem> items = Queries.SELECT_PAGES(db, simulatorId, formatDB.format(start), formatDB.format(end));
                        if(items.size() > 0) {
                            table.removeAllItems();
                            for(LogBookItem item : items) {
                                table.addItem(new Object[] { String.valueOf(item.getId()), item.getDatePage(), item.getAuthor(),
                                    item.getActualStart(), item.getActualEnd(), item.getActualTotal(), item.getDeviceUsers(), item.getStudents() }, item);
                            }
                            table.setVisible(true);
                        } else {
                            table.setVisible(false);
                            MyNotification.SHOW(Messages.WARNING_NO_PAGE_FOUND, Notification.Type.WARNING_MESSAGE);
                        }
                    }
                }
            } catch (ParseException ex) {
                MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void updateTable(int simulatorId) {
        Iterator iterator = selectSim.getItemIds().iterator();
        while(iterator.hasNext()) {
            MyItem foo = (MyItem)iterator.next();
            if(foo.getId() == simulatorId) {
                selectSim.select(foo);
            }
        }
        searchButton.click();
    }

    private void setPrivileges() {
        if(user.getLevelId() == Constants.USER) {
            addItemButton.setVisible(false);
        }
        if(user.getLevelId() == Constants.DEVICE_USER) {
            addItemButton.setVisible(true);
        }
        if(user.getLevelId() == Constants.TECHNICAL) {
            addItemButton.setVisible(true);
        }
        if(user.getLevelId() == Constants.ADMIN) {
            addItemButton.setVisible(true);
        }
        if(user.getLevelId() == Constants.SUPER_ADMIN) {
            addItemButton.setVisible(true);
        }
    }

}

class SelectSim extends Window implements Button.ClickListener, ConfirmDialogInterface {
    
    private Label title;
    private CheckBox cb1;
    private CheckBox cb2;
    private ComboBox combo;
    private Button submit;
    private VerticalLayout vl;
    private final jdb db;
    private final User user;
    private LogBookTable listener;
    private CheckBox cb3;

    public SelectSim(jdb db, User user) {
        this.db = db;
        this.user = user;
        super.setResizable(false);
        super.setModal(true);
        super.setCaption(Messages.NEW_STL_TITLE1);
        super.center();
        vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        buildUI();
    }

    public void setListener(LogBookTable listener) {
        this.listener = listener;
    }
    
    private void buildUI() {
        vl.removeAllComponents();
        //TITLE
        title = new Label("<div style='text-align: center'>"+Messages.NEW_STL_TITLE2+"</div>", ContentMode.HTML);
        title.setStyleName(Runo.LABEL_H2);
        //CB
        cb1 = new CheckBox(Messages.NEW_STL_CB1, false);
        cb2 = new CheckBox(Messages.NEW_STL_CB2, false);
        cb3 = new CheckBox(Messages.NEW_STL_CB3, false);
        //COMBO
        combo = new ComboBox(Messages.NEW_STL_SELECT_SIM);
        combo.setWidth("100%");
        combo.setNullSelectionAllowed(false);
        combo.setTextInputAllowed(false);
        ArrayList<MyItem> sims = Queries.SELECT_SIMULATORS1(db);
        if(sims.size() > 0) {
            for(int i = 0; i < sims.size(); i++) {
                combo.addItem(sims.get(i));
            }
            combo.select(combo.getItemIds().iterator().next());
        }
        //SUBMIT
        submit = new Button(Messages.SUBMIT, this);
        submit.setClickShortcut(KeyCode.ENTER);
        //Add
        vl.addComponents(title, cb1, cb2, cb3, combo, submit);
        vl.setComponentAlignment(submit, Alignment.BOTTOM_RIGHT);
        super.setContent(vl);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(cb1.getValue() && cb2.getValue() && cb3.getValue()) {
            String name = ((MyItem)combo.getValue()).getName();
            ConfirmDialog conf = new ConfirmDialog();
            conf.setListener(this);
            conf.setExtraInfo(Messages.NEW_STL_WARNING + " " + name);
            UI.getCurrent().addWindow(conf);
        } else {
            MyNotification.SHOW(Messages.NEW_STL_WARNING_DISCLAIMER, Notification.Type.WARNING_MESSAGE);
        }
    }

    @Override
    public void submit() {
        super.close();
        Integer id = ((MyItem)combo.getValue()).getId();
        String name = ((MyItem)combo.getValue()).getName();
        NewPage newPage = new NewPage(db, id, name, user);
        newPage.setListener(listener);
        UI.getCurrent().addWindow(newPage);
    }

    @Override
    public void cancel() {
        super.close();
    }

}

interface LogBookTable {
    void updateTable(int simulatorId);
}

class NewPage extends Window implements Property.ValueChangeListener, Button.ClickListener, FieldEvents.TextChangeListener {
    
    private final Integer id;
    private final String name;
    private final VerticalLayout vl;
    private final jdb db;
    private LogBookTable listener;
    private Integer ttl = 0;
    private String actualTtl = "0.0";
    private ComboBox customer;
    private ComboBox trainingType;
    private ComboBox maintenanceType;
    private ComboBox maintCalled;
    private ComboBox trainingCompleted;
    private ComboBox interruptions;
    private ComboBox devicePerf;
    private ComboBox lostTrainingTime;
    private ComboBox startScheduledHours;
    private ComboBox startScheduledMin;
    private ComboBox endScheduledHours;
    private ComboBox endScheduledMin;
    private ComboBox startActualHours;
    private ComboBox startActualMin;
    private ComboBox endActualHours;
    private ComboBox endActualMin;
    private TextField scheduledTotal;
    private TextField actualTotal;
    private PopupDateField startScheduled;
    private PopupDateField endScheduled;
    private PopupDateField startActual;
    private PopupDateField endActual;
    private TextField ttlEnd;
    private TextField ttlStart;
    private TextField ttlTotal;
    private final PeriodicalTest pt;
    private Button addDefectsButton;
    private final User user;
    private final AddDefects ad;
    private Button submit;
    private Button cancel;
    private AutocompleteTextField dev1;
    private AutocompleteTextField dev2;
    private AutocompleteTextField stu1;
    private AutocompleteTextField stu2;
    private AutocompleteTextField obs1;
    private AutocompleteTextField obs2;
    private Button addPeriodicalTests;
    private HashMap<Object, Object> stl_page;
    private Date nowDate;
    private String dateDB;
    private SimpleDateFormat formatDB;
    private TextField sessionContent;
    private TextArea remarks;
    
    //ID of the simulator (simulator_id)
    public NewPage(jdb db, Integer id, String name, User user) {
        this.id = id;
        this.name = name;
        this.db = db;
        this.user = user;
        super.setResizable(false);
        super.setModal(true);
        super.setWidth("80%");
        super.center();
        vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setStyleName(Runo.LAYOUT_DARKER);
        //WINDOW OPEN
        pt = new PeriodicalTest();
        ad = new AddDefects(id, db);
        UI.getCurrent().addWindow(pt);
        UI.getCurrent().addWindow(ad);
        buildUI();
        fillCombos();
        
    }
    
    private void closeWin() {
        super.close();
    }

    public void setListener(LogBookTable listener) {
        this.listener = listener;
    }
    
    private void buildUI() {
        Styles styles = Page.getCurrent().getStyles();
        styles.add("#disabledField { pointer-events: none }");
        //GET INFO SIMULATOR: TTL and ACTUAL TTL
        ArrayList<Object> array = Queries.SELECT_SIMULATOR(db, id);
        ttl = (Integer) array.get(2);
        actualTtl = array.get(3).toString();
        vl.removeAllComponents();
        //Date page
        SimpleDateFormat formatTitle = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatTitle.setTimeZone(TimeZone.getTimeZone("UTC"));
        formatDB.setTimeZone(TimeZone.getTimeZone("UTC"));
        nowDate = new Date();
        String dateTitle = formatTitle.format(nowDate);
        dateDB = formatDB.format(nowDate);
        //Title
        super.setCaption(name + " ("+dateTitle+")");
        //DATE AND TTL
        HorizontalLayout hl0 = buildHorizontLayout();
        VerticalLayout vl0_1 = buildVerticalLayout(Messages.SCHEDULED);
        VerticalLayout vl0_2 = buildVerticalLayout(Messages.ACTUAL);
        VerticalLayout vl0_3 = buildVerticalLayout(Messages.TTL);
        scheduledTotal = buildTextField(Messages.TOTAL);
        actualTotal = buildTextField(Messages.TOTAL);
        scheduledTotal.setValue(Messages.ERROR);
        actualTotal.setValue(Messages.ERROR);
        startScheduled = buildPopupDateField(Messages.START, "scheduledTotal");
        endScheduled = buildPopupDateField(Messages.END, "scheduledTotal");
        startActual = buildPopupDateField(Messages.START, "actualTotal");
        endActual = buildPopupDateField(Messages.END, "actualTotal");
        startScheduled.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                startActual.setValue((Date)event.getProperty().getValue());
            }
        });
        endScheduled.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                endActual.setValue((Date)event.getProperty().getValue());
            }
        });
        ttlStart = buildTextField(Messages.START);
        ttlStart.setHeight("24px");
        ttlEnd = new TextField();
        ttlEnd.setCaption(Messages.END+" *");
        ttlEnd.setWidth("100%");
        ttlTotal = buildTextField(Messages.ACTUAL_TTL_AFTER_SESSION+" *");
        ttlTotal.setValue("");
        ttlStart.setValue(actualTtl);
        scheduledTotal.setId("scheduledTotal");
        actualTotal.setId("actualTotal");
        scheduledTotal.setId("disabledField");
        actualTotal.setId("disabledField");
        vl0_1.addComponents(startScheduled, endScheduled, scheduledTotal);
        vl0_2.addComponents(startActual, endActual, actualTotal);
        vl0_3.addComponents(ttlStart, ttlEnd, ttlTotal);
        if(ttl == 0) {
            vl0_3.setEnabled(false);
            vl0_3.setVisible(false);
        }
        hl0.addComponents(vl0_1, vl0_2, vl0_3);
        //CUSTOMER - TRAINING - SESSION CONTENT - MAINTENANCE TYPE - ADD PERIODICAL TEST
        HorizontalLayout hl1 = buildHorizontLayout();
        customer = buildCombo(Messages.CUSTOMER, false);
        trainingType = buildCombo(Messages.TRAINING_TYPE, false);
        trainingType.addValueChangeListener(this);
        trainingType.addContextClickListener(new ContextClickEvent.ContextClickListener() {
            @Override
            public void contextClick(ContextClickEvent event) {
                Notification.show("ok");
            }
        });
        sessionContent = buildTextField(Messages.SESSION_CONTENT);
        maintenanceType = buildCombo(Messages.MAINT_TYPE, false);
        addPeriodicalTests = new Button(Messages.ADD_PERIODICAL_TESTS, this);
        addPeriodicalTests.setWidth("100%");
        addPeriodicalTests.setStyleName(Runo.BUTTON_DEFAULT);
        addPeriodicalTests.setEnabled(false);
        hl1.addComponents(customer,trainingType,sessionContent,maintenanceType, addPeriodicalTests);
        hl1.setComponentAlignment(addPeriodicalTests, Alignment.BOTTOM_CENTER);
        hl1.setExpandRatio(customer, 0.2f);
        hl1.setExpandRatio(trainingType, 0.2f);
        hl1.setExpandRatio(sessionContent, 0.2f);
        hl1.setExpandRatio(maintenanceType, 0.2f);
        hl1.setExpandRatio(addPeriodicalTests, 0.2f);
        //MAINTENANCE CALLED - TRAINING COMPLETED - INTERRUPTIONS - LOST TRAINING TIME - DEVICE P - ADD DEFECTS
        HorizontalLayout hl2 = buildHorizontLayout();
        maintCalled = buildCombo(Messages.MAINT_CALLED,false);
        trainingCompleted = buildCombo(Messages.TRAINING_COMPLETED,false);
        interruptions = buildCombo(Messages.INTERRUPTIONS,false);
        lostTrainingTime = buildCombo(Messages.LOST_TRAINING_TIME,false);
        devicePerf = buildCombo(Messages.DEVICE_PERF,false);
        addDefectsButton = new Button(Messages.ADD_DEFECTS, this);
        addDefectsButton.setWidth("100%");
        addDefectsButton.setStyleName(Runo.BUTTON_DEFAULT);
        hl2.addComponents(maintCalled,trainingCompleted,interruptions,lostTrainingTime,devicePerf, addDefectsButton);
        hl2.setComponentAlignment(addDefectsButton, Alignment.BOTTOM_CENTER);
        hl2.setExpandRatio(maintCalled, 0.15f);
        hl2.setExpandRatio(trainingCompleted, 0.15f);
        hl2.setExpandRatio(interruptions, 0.15f);
        hl2.setExpandRatio(lostTrainingTime, 0.15f);
        hl2.setExpandRatio(devicePerf, 0.2f);
        hl2.setExpandRatio(addDefectsButton, 0.2f);
        //DEVICE USERS - STUDENTS - OBSERVERS
        HorizontalLayout hl3 = buildHorizontLayout();
        VerticalLayout vl1_1 = buildVerticalLayout(Messages.DEVICE_USERS_CAPTION+" *");
        VerticalLayout vl1_2 = buildVerticalLayout(Messages.STUDENTS);
        VerticalLayout vl1_3 = buildVerticalLayout(Messages.OBSERVERS);
        Collection<String> frequentUsers = Queries.SELECT_FREQUENT_USERS(db);
        AutocompleteSuggestionProvider suggestionProvider = new CollectionSuggestionProvider(frequentUsers, MatchMode.CONTAINS, true, Locale.US);
        dev1 = new AutocompleteTextField(); dev1.setWidth("100%"); dev1.setImmediate(true); dev1.setMinChars(1); dev1.setSuggestionProvider(suggestionProvider);
        dev2 = new AutocompleteTextField(); dev2.setWidth("100%"); dev2.setImmediate(true); dev2.setMinChars(1); dev2.setSuggestionProvider(suggestionProvider);
        stu1 = new AutocompleteTextField(); stu1.setWidth("100%"); stu1.setImmediate(true); stu1.setMinChars(1); stu1.setSuggestionProvider(suggestionProvider);
        stu2 = new AutocompleteTextField(); stu2.setWidth("100%"); stu2.setImmediate(true); stu2.setMinChars(1); stu2.setSuggestionProvider(suggestionProvider);
        obs1 = new AutocompleteTextField(); obs1.setWidth("100%"); obs1.setImmediate(true); obs1.setMinChars(1); obs1.setSuggestionProvider(suggestionProvider);
        obs2 = new AutocompleteTextField(); obs2.setWidth("100%"); obs2.setImmediate(true); obs2.setMinChars(1); obs2.setSuggestionProvider(suggestionProvider);
        dev1.setInputPrompt(Messages.DEVICE_USERS+" 1");
        dev2.setInputPrompt(Messages.DEVICE_USERS+" 2");
        stu1.setInputPrompt(Messages.STUDENTS+" 1");
        stu2.setInputPrompt(Messages.STUDENTS+" 2");
        obs1.setInputPrompt(Messages.OBSERVERS+" 1");
        obs2.setInputPrompt(Messages.OBSERVERS+" 2");
        vl1_1.addComponents(dev1, dev2);
        vl1_2.addComponents(stu1, stu2);
        vl1_3.addComponents(obs1, obs2);
        hl3.addComponents(vl1_1, vl1_2, vl1_3);
        //REMARKS
        HorizontalLayout hl4 = buildHorizontLayout();
        remarks = new TextArea(Messages.REMARKS);
        remarks.setInputPrompt("If NIL, leave empty this field");
        remarks.setWidth("100%");
        remarks.setHeight("50px");
        hl4.addComponent(remarks);
        //BUTTONS
        HorizontalLayout hl5 = buildHorizontLayout();
        submit = new Button(Messages.SUBMIT, this);
        cancel = new Button(Messages.CANCEL, this);
        cancel.setStyleName(Runo.BUTTON_DEFAULT);
        hl5.addComponents(new Label("* Required Fields"),cancel, submit);
        hl5.setSizeUndefined();
        //ADD INTO VL
        vl.addComponents( 
                hl0, new Label("<hr style='height: 5px' />", ContentMode.HTML), 
                hl1, new Label("<hr style='height: 5px' />", ContentMode.HTML),
                hl2, new Label("<hr style='height: 5px' />", ContentMode.HTML), 
                hl3, new Label("<hr style='height: 5px' />", ContentMode.HTML), 
                hl4, hl5);
        vl.setComponentAlignment(hl5, Alignment.MIDDLE_RIGHT);
        super.setContent(vl);
    }
    
    private ComboBox buildCombo(String caption, boolean allowed) {
        ComboBox combo = new ComboBox(caption);
        combo.setWidth("100%");
        combo.setNullSelectionAllowed(allowed);
        combo.setTextInputAllowed(allowed);
        return combo;
    }
    
    private TextField buildTextField(String caption) {
        TextField text = new TextField();    
        if(caption.length() > 0) {
            text.setCaption(caption);
        }
        text.setWidth("100%");
        return text;
    }
    
    private PopupDateField buildPopupDateField(String caption, final String targetId) {
        Date fooDate = new Date();
        PopupDateField pop = new PopupDateField(caption);
        pop.setImmediate(true);
        pop.setLocale(Locale.UK);
        pop.setDateFormat("dd/MM/yyyy HH:mm");
        pop.addValueChangeListener(this);
        pop.setTextFieldEnabled(false);
        pop.setResolution(Resolution.MINUTE);
        pop.setValue(fooDate);
        pop.setWidth("100%");
        return pop;
    }
    
    private HorizontalLayout buildHorizontLayout() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setSizeFull();
        return hl;
    }
    
    private VerticalLayout buildVerticalLayout(String caption) {
        VerticalLayout vl = new VerticalLayout();
        vl.setCaption(caption);
        vl.setSpacing(true);
        vl.setSizeFull();
        return vl;
    }

    private void fillCombos() {
        //Customers
        ArrayList<MyItem> customers = Queries.SELECT_TABLE(db, "stl_customers");
        for(MyItem item : customers) {
            customer.addItem(item);
        }
        customer.select(customer.getItemIds().iterator().next());
        //TRAINING TYPE
        ArrayList<MyItem> trainingTypes = Queries.SELECT_TABLE(db, "stl_training_type");
        for(MyItem item : trainingTypes) {
            trainingType.addItem(item);
        }
        trainingType.select(trainingType.getItemIds().iterator().next());
        //MAINTENANCE TYPE
        ArrayList<MyItem> maintTypes = Queries.SELECT_TABLE(db, "stl_maintenance_type");
        for(MyItem item : maintTypes) {
            maintenanceType.addItem(item);
        }
        maintenanceType.select(maintenanceType.getItemIds().iterator().next());
        maintenanceType.setEnabled(false);
        //MAINTENANCE CALLED
        maintCalled.addItem(new MyItem(0, Messages.NO));
        maintCalled.addItem(new MyItem(1, Messages.YES));
        maintCalled.select(maintCalled.getItemIds().iterator().next());
        //Training Completed
        trainingCompleted.addItem(new MyItem(1, Messages.YES));
        trainingCompleted.addItem(new MyItem(0, Messages.NO));
        trainingCompleted.select(trainingCompleted.getItemIds().iterator().next());
        //INTERRUPTIONS
        interruptions.setPageLength(0);
        for(int i = 0; i < 21; i++) {
            interruptions.addItem(i);
        }
        interruptions.select(interruptions.getItemIds().iterator().next());
        //LOST TIME
        lostTrainingTime.setPageLength(0);
        for(int i = 0; i < 401; i++) {
            lostTrainingTime.addItem(i);
        }
        lostTrainingTime.select(lostTrainingTime.getItemIds().iterator().next());
        //DEVICE PERFORMANCE
        ArrayList<MyItem> devPerfs = Queries.SELECT_TABLE(db, "stl_device_performance");
        for(MyItem item : devPerfs) {
            devicePerf.addItem(item);
        }
        Iterator iterator = devicePerf.getItemIds().iterator();
        while(iterator.hasNext()) {
            MyItem item = (MyItem) iterator.next();
            if(item.getId() == 5) {
                devicePerf.select(item);
            }
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        Property property = event.getProperty();
        if(property.equals(startScheduled) || property.equals(endScheduled)) {
            long start = startScheduled.getValue().getTime();
            long end = endScheduled.getValue().getTime();
            if(end <= start) {
                scheduledTotal.setValue(Messages.ERROR);
            } else {
                String h = ""; String m = "";
                long minute = Math.abs((start - end) / 60000);
                int Hours = (int) minute / 60;
                int Minutes = (int)minute % 60;
                if(Hours < 10) h = "0"+String.valueOf(Hours);
                else h = String.valueOf(Hours);
                if(Minutes < 10) m = "0"+String.valueOf(Minutes);
                else m = String.valueOf(Minutes);
                String total = h + ":" + m;
                if(total.equals("00:00")) scheduledTotal.setValue(Messages.ERROR);
                else scheduledTotal.setValue(total); 
            }
        }
        if(property.equals(startActual) || property.equals(endActual)) {
            long start = startActual.getValue().getTime();
            long end = endActual.getValue().getTime();
            if(end <= start) {
                actualTotal.setValue(Messages.ERROR);
            } else {
                String h = ""; String m = "";
                long minute = Math.abs((start - end) / 60000);
                int Hours = (int) minute / 60;
                int Minutes = (int)minute % 60;
                if(Hours < 10) h = "0"+String.valueOf(Hours);
                else h = String.valueOf(Hours);
                if(Minutes < 10) m = "0"+String.valueOf(Minutes);
                else m = String.valueOf(Minutes);
                String total = h + ":" + m;
                if(total.equals("00:00")) actualTotal.setValue(Messages.ERROR);
                else actualTotal.setValue(total); 
            }
        }
        if(property.equals(trainingType)) {
            /*
            +----+-----------------------+
            | id | name                  |
            +----+-----------------------+
            |  1 | Training              |
            |  2 | Check                 |
            |  3 | Engineering           |
            |  4 | Maintenance           |
            |  5 | Regualar Authority    |
            |  6 | Other (ex. Demo)      |
            |  7 | Compliance Monitoring |
            |  8 | Safety Monitoring     |
            |  9 | Periodical Test       |
            +----+-----------------------+
            */
            MyItem item = (MyItem) property.getValue();
            if(item.getId() == 4) {
                addPeriodicalTests.setEnabled(false);
                maintenanceType.setEnabled(true);
            } else if(item.getId() == 9) {
                addPeriodicalTests.setEnabled(true);
                maintenanceType.setEnabled(false);
            } else {
                addPeriodicalTests.setEnabled(false);
                maintenanceType.setEnabled(false);
            }
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == addDefectsButton) {
            ad.setVisible(true);
        }
        if(event.getButton() == addPeriodicalTests) {
            pt.setVisible(true);
        }
        if(event.getButton() == cancel) {
            closeWin();
        }
        if(event.getButton() == submit) {
            try {
                //CHECK VALUES FOR INSERT
                if(scheduledTotal.getValue().equals(Messages.ERROR) || actualTotal.getValue().equals(Messages.ERROR) || dev1.getValue().equals("")) {
                    MyNotification.SHOW(Messages.ERROR_EMPTY_FIELDS, Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if(ttl > 0) {
                    if(ttlEnd.getValue().equals("") || ttlTotal.getValue().equals("")) {
                        MyNotification.SHOW(Messages.ERROR_EMPTY_FIELDS, Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                }
                //Periodical Test
                if(((MyItem) trainingType.getValue()).getId() == 9) { //) = Periodical Test
                    if(!pt.isSetted()) {
                        MyNotification.SHOW(Messages.ERROR_EMPTY_FIELDS, Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                }
                //INSERT FREQUENT USERS BEFORE
                Queries.INSERT_FREQUENT_USER(db, dev1.getValue());
                Queries.INSERT_FREQUENT_USER(db, dev2.getValue());
                Queries.INSERT_FREQUENT_USER(db, stu1.getValue());
                Queries.INSERT_FREQUENT_USER(db, stu2.getValue());
                Queries.INSERT_FREQUENT_USER(db, obs1.getValue());
                Queries.INSERT_FREQUENT_USER(db, obs2.getValue());
                //PREPARE VALUES
                //stl_pages
                Map<String, String> stlPage = new HashMap<>();
                stlPage.put("user_id", String.valueOf(user.getId()));
                stlPage.put("simulator_id", String.valueOf(this.id));
                stlPage.put("date_page", dateDB);
                stlPage.put("sched_datetime_start", formatDB.format(startScheduled.getValue()));
                stlPage.put("sched_datetime_end", formatDB.format(endScheduled.getValue()));
                stlPage.put("sched_datetime_total", scheduledTotal.getValue());
                stlPage.put("actual_datetime_start", formatDB.format(startActual.getValue()));
                stlPage.put("actual_datetime_end", formatDB.format(endActual.getValue()));
                stlPage.put("actual_datetime_total", actualTotal.getValue());
                stlPage.put("ttl_start", ttl > 0 ? ttlStart.getValue() : "0.0");
                stlPage.put("ttl_end", ttl > 0 ? ttlEnd.getValue() : "0.0");
                stlPage.put("ttl_total", ttl > 0 ? ttlTotal.getValue() : "0.0");
                stlPage.put("customer_id", String.valueOf(((MyItem)customer.getValue()).getId()));
                stlPage.put("training_type_id", String.valueOf(((MyItem)trainingType.getValue()).getId()));
                stlPage.put("session_content", sessionContent.getValue());
                stlPage.put("maintenance_type_id", String.valueOf(((MyItem)maintenanceType.getValue()).getId()));
                stlPage.put("device_users", dev1.getValue()+"###"+dev2.getValue());
                stlPage.put("students", stu1.getValue()+"###"+stu2.getValue());
                stlPage.put("observers", obs1.getValue()+"###"+obs2.getValue());
                stlPage.put("maintenance_called", String.valueOf(((MyItem)maintCalled.getValue()).getId()));
                stlPage.put("training_completed", String.valueOf(((MyItem)trainingCompleted.getValue()).getId()));
                stlPage.put("interruptions", interruptions.getValue().toString());
                stlPage.put("lost_training_time", lostTrainingTime.getValue().toString());
                stlPage.put("device_performance_id", String.valueOf(((MyItem)devicePerf.getValue()).getId()));
                stlPage.put("remarks", remarks.getValue());
                //Insert Page
                int pageId = Queries.INSERT_TABLE_MAP(db, "stl_pages", stlPage);
                //Upload Simulator TTL
                Queries.UPDATE_SIMULATOR_TTL(db, ttl > 0 ? ttlTotal.getValue() : "0", id);
                //stl_periodical_test_values
                if(((MyItem) trainingType.getValue()).getId() == 9) { //) = Periodical Test
                    if(pt.getQtg().getValue()) {
                        Map<String, String> stlPeriodicalTest = new HashMap<>();
                        stlPeriodicalTest.put("page_id", String.valueOf(pageId));
                        stlPeriodicalTest.put("periodical_test_id", "1");
                        stlPeriodicalTest.put("number", pt.getQtgNumber());
                        stlPeriodicalTest.put("year", pt.getQtgYear());
                        Queries.INSERT_TABLE_MAP(db, "stl_periodical_test_values", stlPeriodicalTest);
                    }
                    if(pt.getFlyout().getValue()) {
                        Map<String, String> stlPeriodicalTest = new HashMap<>();
                        stlPeriodicalTest.put("page_id", String.valueOf(pageId));
                        stlPeriodicalTest.put("periodical_test_id", "2");
                        stlPeriodicalTest.put("number", pt.getFlyoutNumber());
                        stlPeriodicalTest.put("year", pt.getFlyoutYear());
                        Queries.INSERT_TABLE_MAP(db, "stl_periodical_test_values", stlPeriodicalTest);
                    }
                    if(pt.getNavData().getValue()) {
                        Map<String, String> stlPeriodicalTest = new HashMap<>();
                        stlPeriodicalTest.put("page_id", String.valueOf(pageId));
                        stlPeriodicalTest.put("periodical_test_id", "3");
                        stlPeriodicalTest.put("number", pt.getNavDataNumber());
                        stlPeriodicalTest.put("year", pt.getNavDataYear());
                        Queries.INSERT_TABLE_MAP(db, "stl_periodical_test_values", stlPeriodicalTest);
                    }
                    if(pt.getIosData().getValue()) {
                        Map<String, String> stlPeriodicalTest = new HashMap<>();
                        stlPeriodicalTest.put("page_id", String.valueOf(pageId));
                        stlPeriodicalTest.put("periodical_test_id", "4");
                        stlPeriodicalTest.put("number", pt.getIosDataNumber());
                        stlPeriodicalTest.put("year", pt.getIosDataYear());
                        Queries.INSERT_TABLE_MAP(db, "stl_periodical_test_values", stlPeriodicalTest);
                    }
                    if(pt.getVideCalibration().getValue()) {
                        Map<String, String> stlPeriodicalTest = new HashMap<>();
                        stlPeriodicalTest.put("page_id", String.valueOf(pageId));
                        stlPeriodicalTest.put("periodical_test_id", "5");
                        stlPeriodicalTest.put("number", pt.getVideCalibrationNumber());
                        stlPeriodicalTest.put("year", pt.getVideCalibrationYear());
                        Queries.INSERT_TABLE_MAP(db, "stl_periodical_test_values", stlPeriodicalTest);
                    }
                    if(pt.getTransportDelay().getValue()) {
                        Map<String, String> stlPeriodicalTest = new HashMap<>();
                        stlPeriodicalTest.put("page_id", String.valueOf(pageId));
                        stlPeriodicalTest.put("periodical_test_id", "6");
                        stlPeriodicalTest.put("number", pt.getTransportDelaygNumber());
                        stlPeriodicalTest.put("year", pt.getTransportDelayYear());
                        Queries.INSERT_TABLE_MAP(db, "stl_periodical_test_values", stlPeriodicalTest);
                    }      
                }
                //stl_defects
                if(ad.getCb1().getValue()) {
                    Map<String, String> stlDefects = new HashMap<>();
                    stlDefects.put("simulator_id", String.valueOf(this.id));
                    stlDefects.put("page_id", String.valueOf(pageId));
                    stlDefects.put("defect_type_id", String.valueOf(ad.getDefectType1()));
                    stlDefects.put("description", ad.getDescription1());
                    stlDefects.put("datetime_start", dateDB);
                    Queries.INSERT_TABLE_MAP(db, "stl_defects", stlDefects);
                }
                if(ad.getCb2().getValue()) {
                    Map<String, String> stlDefects = new HashMap<>();
                    stlDefects.put("simulator_id", String.valueOf(this.id));
                    stlDefects.put("page_id", String.valueOf(pageId));
                    stlDefects.put("defect_type_id", String.valueOf(ad.getDefectType2()));
                    stlDefects.put("description", ad.getDescription2());
                    stlDefects.put("datetime_start", dateDB);
                    Queries.INSERT_TABLE_MAP(db, "stl_defects", stlDefects);
                }
                if(ad.getCb3().getValue()) {
                    Map<String, String> stlDefects = new HashMap<>();
                    stlDefects.put("simulator_id", String.valueOf(this.id));
                    stlDefects.put("page_id", String.valueOf(pageId));
                    stlDefects.put("defect_type_id", String.valueOf(ad.getDefectType3()));
                    stlDefects.put("description", ad.getDescription3());
                    stlDefects.put("datetime_start", dateDB);
                    Queries.INSERT_TABLE_MAP(db, "stl_defects", stlDefects);
                }
                vl.removeAllComponents();
                super.setWidthUndefined();
                Label title = new Label(Messages.SUCCESS);
                Label title1 = new Label("ID CODE: "+pageId);
                Label title2 = new Label(Messages.SUBMIT_PAGE_OK);
                title.setStyleName(Runo.LABEL_H1);
                title1.setStyleName(Runo.LABEL_H2);
                title2.setStyleName(Runo.LABEL_H2);
                Button close = new Button(Messages.CLOSE, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        closeWin();
                    }
                });
                vl.addComponents(title, title1, title2, close);
                vl.setComponentAlignment(close, Alignment.BOTTOM_RIGHT);
                super.center();
                if(listener != null) {
                    listener.updateTable(this.id);
                }
            } catch (Exception ex) {
                MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void textChange(FieldEvents.TextChangeEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

class PeriodicalTest extends Window implements Button.ClickListener {

    private final VerticalLayout vl;
    private Table table;
    private Button hide;
    private ArrayList<CheckBox> checkBoxAr;
    private CheckBox qtg;
    private CheckBox flyout;
    private CheckBox navData;
    private CheckBox iosData;
    private CheckBox videCalibration;
    private CheckBox transportDelay;
    private ComboBox qtgYear;
    private ComboBox flyoutYear;
    private ComboBox navDataYear;
    private ComboBox iosDataYear;
    private ComboBox videCalibrationYear;
    private ComboBox transportDelayYear;
    private TextField qtgNumber;
    private TextField flyoutNumber;
    private TextField navDataNumber;
    private TextField iosDataNumber;
    private TextField videCalibrationNumber;
    private TextField transportDelaygNumber;
    
    public PeriodicalTest() {
        vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        checkBoxAr= new ArrayList<>();
        super.setCaption(Messages.PERIODICAL_TESTS);
        super.setVisible(false);
        super.setModal(true);
        super.setResizable(false);
        super.setClosable(false);
        super.setContent(vl);
        super.center();
        buildUI();        
    }

    private void buildUI() {
        /*
        +----+-------------------+
        | id | name              |
        +----+-------------------+
        |  1 | QTG               |
        |  2 | Flyout            |
        |  3 | NAV DATA Update   |
        |  4 | IOS DATA Update   |
        |  5 | Video Calibration |
        |  6 | Transport Delay   |
        +----+-------------------+
        */
        vl.removeAllComponents();
        table = new Table();
        table.setWidth("100%");
        table.addContainerProperty(Messages.PERIODICAL_TEST, CheckBox.class, null);
        table.addContainerProperty(Messages.YEAR, ComboBox.class, null);
        table.addContainerProperty(Messages.NUMBER, TextField.class, null);
        qtg = new CheckBox(Messages.QTG, false);
        flyout = new CheckBox(Messages.FLYOUT, false);
        navData = new CheckBox(Messages.NAV_DATA_UPDATE, false);
        iosData = new CheckBox(Messages.IOS_DATA_UPDATE, false);
        videCalibration = new CheckBox(Messages.VIDEO_CALIBRATION, false);
        transportDelay = new CheckBox(Messages.TRANSPORT_DELAY, false);
        checkBoxAr.add(qtg);
        checkBoxAr.add(flyout);
        checkBoxAr.add(navData);
        checkBoxAr.add(iosData);
        checkBoxAr.add(videCalibration);
        checkBoxAr.add(transportDelay);
        qtgYear = buildComboBox();
        flyoutYear = buildComboBox();
        navDataYear = buildComboBox();
        iosDataYear = buildComboBox();
        videCalibrationYear = buildComboBox();
        transportDelayYear = buildComboBox();
        qtgNumber = buildTextField();
        flyoutNumber = buildTextField();
        navDataNumber = buildTextField();
        iosDataNumber = buildTextField();
        videCalibrationNumber = buildTextField();
        transportDelaygNumber = buildTextField();
        table.addItem(new Object[] { qtg, qtgYear, qtgNumber  }, 1);
        table.addItem(new Object[] { flyout, flyoutYear, flyoutNumber }, 2);
        table.addItem(new Object[] { navData, navDataYear, navDataNumber}, 3);
        table.addItem(new Object[] { iosData, iosDataYear, iosDataNumber }, 4);
        table.addItem(new Object[] { videCalibration, videCalibrationYear, videCalibrationNumber }, 5);
        table.addItem(new Object[] { transportDelay, transportDelayYear, transportDelaygNumber }, 6);
        table.setPageLength(6);
        hide = new Button(Messages.HIDE_WINDOW, this);
        vl.addComponents(table, hide);
        vl.setComponentAlignment(hide, Alignment.BOTTOM_RIGHT);
    }
    
    public boolean isSetted() {
        for(CheckBox foo : checkBoxAr) {
            if(foo.getValue()) {
                return true;
            }
        }
        return false;
    }

    public CheckBox getQtg() {
        return qtg;
    }

    public CheckBox getFlyout() {
        return flyout;
    }

    public CheckBox getNavData() {
        return navData;
    }

    public CheckBox getIosData() {
        return iosData;
    }

    public CheckBox getVideCalibration() {
        return videCalibration;
    }

    public CheckBox getTransportDelay() {
        return transportDelay;
    }

    public String getQtgYear() {
        return (String)qtgYear.getValue();
    }

    public String getFlyoutYear() {
        return (String)flyoutYear.getValue();
    }

    public String getNavDataYear() {
        return (String)navDataYear.getValue();
    }

    public String getIosDataYear() {
        return (String)iosDataYear.getValue();
    }

    public String getVideCalibrationYear() {
        return (String)videCalibrationYear.getValue();
    }

    public String getTransportDelayYear() {
        return (String)transportDelayYear.getValue();
    }

    public String getQtgNumber() {
        return qtgNumber.getValue();
    }

    public String getFlyoutNumber() {
        return flyoutNumber.getValue();
    }

    public String getNavDataNumber() {
        return navDataNumber.getValue();
    }

    public String getIosDataNumber() {
        return iosDataNumber.getValue();
    }

    public String getVideCalibrationNumber() {
        return videCalibrationNumber.getValue();
    }

    public String getTransportDelaygNumber() {
        return transportDelaygNumber.getValue();
    }
    
    private ComboBox buildComboBox() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        ComboBox foo = new ComboBox();
        foo.setWidth("100%");
        foo.setNullSelectionAllowed(false);
        foo.setTextInputAllowed(false);
        foo.addItem(String.valueOf(year - 1));
        foo.addItem(String.valueOf(year));
        foo.addItem(String.valueOf(year + 1));
        foo.select(String.valueOf(year));
        return foo;
    }
    
    private TextField buildTextField() {
        TextField foo = new TextField();
        foo.setWidth("100%");
        return foo;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        super.setVisible(false);
    }
    
}

class AddDefects extends Window implements Button.ClickListener, Property.ValueChangeListener {
    
    private final jdb db;
    private final Integer id;
    private final VerticalLayout vl;
    public CheckBox cb1, cb2, cb3;
    public ComboBox defectType1, defectType2, defectType3;
    public TextField description1, description2, description3;
    private Table table;
    private Button hide;
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    
    //ID of the simulator (simulator_id)
    public AddDefects(Integer id, jdb db) {
        this.id = id;
        this.db = db;
        vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setWidth("100%");
        super.setWidth("50%");
        super.setClosable(false);
        super.setModal(true);
        super.setVisible(false);
        super.setResizable(false);
        super.setCaption(Messages.ADD_DEFECTS);
        super.setContent(vl);
        buildUI();
        fill();
    }

    private void buildUI() {
        vl.removeAllComponents();
        cb1 = new CheckBox(); cb1.addValueChangeListener(this);
        cb2 = new CheckBox(); cb2.addValueChangeListener(this);
        cb3 = new CheckBox(); cb3.addValueChangeListener(this);
        defectType1 = MyUtilities.buildComboBox(Messages.DEFECT_TYPE); defectType1.setEnabled(false);
        defectType2 = MyUtilities.buildComboBox(Messages.DEFECT_TYPE); defectType2.setEnabled(false);
        defectType3 = MyUtilities.buildComboBox(Messages.DEFECT_TYPE); defectType3.setEnabled(false);
        description1 = MyUtilities.buildTextField(Messages.DESCRIPTION); description1.setEnabled(false);
        description2 = MyUtilities.buildTextField(Messages.DESCRIPTION); description2.setEnabled(false);
        description3 = MyUtilities.buildTextField(Messages.DESCRIPTION); description3.setEnabled(false);
        HorizontalLayout hl1 = new HorizontalLayout(cb1, defectType1, description1);
        HorizontalLayout hl2 = new HorizontalLayout(cb2, defectType2, description2);
        HorizontalLayout hl3 = new HorizontalLayout(cb3, defectType3, description3);
        hl1.setSpacing(true); hl1.setWidth("100%"); hl1.setExpandRatio(cb1, 0.1f); hl1.setExpandRatio(defectType1, 0.45f); hl1.setExpandRatio(description1, 0.45f); 
        hl2.setSpacing(true); hl2.setWidth("100%"); hl2.setExpandRatio(cb2, 0.1f); hl2.setExpandRatio(defectType2, 0.45f); hl2.setExpandRatio(description2, 0.45f); 
        hl3.setSpacing(true); hl3.setWidth("100%"); hl3.setExpandRatio(cb3, 0.1f); hl3.setExpandRatio(defectType3, 0.45f); hl3.setExpandRatio(description3, 0.45f); 
        hl1.setComponentAlignment(cb1, Alignment.BOTTOM_CENTER);
        hl2.setComponentAlignment(cb2, Alignment.BOTTOM_CENTER);
        hl3.setComponentAlignment(cb3, Alignment.BOTTOM_CENTER);
        table = new Table(Messages.LIST_OF_OPEN_DEFECTS);
        table.addContainerProperty("Date", String.class, null);
        table.addContainerProperty("Description", String.class, null);
        table.setWidth("100%");
        table.setSelectable(false);
        table.setPageLength(10);
        table.setColumnWidth("Date", 140);
        hide = new Button(Messages.HIDE_WINDOW, this);
        vl.addComponents(hl1, hl2, hl3, table, hide);
        vl.setComponentAlignment(hide, Alignment.BOTTOM_RIGHT);
    }

    public CheckBox getCb1() {
        return cb1;
    }

    public CheckBox getCb2() {
        return cb2;
    }

    public CheckBox getCb3() {
        return cb3;
    }

    public int getDefectType1() {
        return ((MyItem)defectType1.getValue()).getId();
    }

    public int getDefectType2() {
        return ((MyItem)defectType2.getValue()).getId();
    }

    public int getDefectType3() {
        return ((MyItem)defectType3.getValue()).getId();
    }

    public String getDescription1() {
        return description1.getValue();
    }

    public String getDescription2() {
        return description2.getValue();
    }

    public String getDescription3() {
        return description3.getValue();
    }
    
    private void fill() {
        ArrayList<MyItem> items1 = Queries.SELECT_TABLE(db, "stl_defect_type");
        ArrayList<MyItem> items2 = Queries.SELECT_TABLE(db, "stl_defect_type");
        ArrayList<MyItem> items3 = Queries.SELECT_TABLE(db, "stl_defect_type");
        for(MyItem foo : items1) {
            defectType1.addItem(foo);
        }
        for(MyItem foo : items2) {
            defectType2.addItem(foo);
        }
        for(MyItem foo : items3) {
            defectType3.addItem(foo);
        }
        defectType1.select(defectType1.getItemIds().iterator().next());
        defectType2.select(defectType2.getItemIds().iterator().next());
        defectType3.select(defectType3.getItemIds().iterator().next());
        ArrayList<DefectItem> openDefectsArray = Queries.SELECT_OPEN_DEFECT(db, id);
        if(openDefectsArray.size() > 0) {
            for(DefectItem foo : openDefectsArray) {
                table.addItem(new Object[] { format.format(foo.getDatetimeStart()), foo.getDescription()+" ("+foo.getDefectType()+")"}, foo);
            }
        } else {
            table.setVisible(false);
        }
    }
    
    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getButton() == hide) {
            super.setVisible(false);
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if(event.getProperty().equals(cb1)) {
            defectType1.setEnabled(cb1.getValue()); description1.setEnabled(cb1.getValue());
            if(!cb1.getValue()) {
                defectType1.select(defectType1.getItemIds().iterator().next());
                description1.setValue("");
            }
        }
        if(event.getProperty().equals(cb2)) {
            defectType2.setEnabled(cb2.getValue()); description2.setEnabled(cb2.getValue());
            if(!cb2.getValue()) {
                defectType2.select(defectType2.getItemIds().iterator().next());
                description2.setValue("");
            }
        }
        if(event.getProperty().equals(cb3)) {
            defectType3.setEnabled(cb3.getValue()); description3.setEnabled(cb3.getValue());
            if(!cb3.getValue()) {
                defectType3.select(defectType3.getItemIds().iterator().next());
                description3.setValue("");
            }
        }
    }
    
}