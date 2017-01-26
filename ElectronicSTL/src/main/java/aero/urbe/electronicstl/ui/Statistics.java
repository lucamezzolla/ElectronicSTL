package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.MyClasses.MyItem;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.MyClasses.MyUtilities;
import aero.urbe.electronicstl.jdb;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Luca Mezzolla
 */
public class Statistics extends CustomComponent implements Button.ClickListener, Property.ValueChangeListener {

    private final VerticalLayout vl;
    private HorizontalLayout hl;
    private PopupDateField from;
    private PopupDateField to;
    private ComboBox statistics;
    private final MyItem stat2 = new MyItem(2, "Type of Session"); 
    private final MyItem stat3 = new MyItem(4, "Device Availability");
    private final MyItem stat4 = new MyItem(5, "Average Discrepancy Turn Around Time");
    private final MyItem stat5 = new MyItem(6, "Defects");
    private final MyItem stat6 = new MyItem(9, "Average Quality Rating");
    private Button submit;
    private final jdb db;
    private SimpleDateFormat formatDB;
    private SimpleDateFormat format;
    private ComboBox selectSim;
    
    
    public Statistics(jdb db) {
        this.db = db;
        vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setSizeFull();
        super.setCompositionRoot(vl);
        buildUI();
    }

    private void buildUI() {
        from = MyUtilities.buildPopupDateField(Messages.FROM, false,null);
        to = MyUtilities.buildPopupDateField(Messages.TO, true,null);
        formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format = new SimpleDateFormat("dd-MMM-yyyy");
        from.setDateFormat("dd-MMM-yyyy"); 
        to.setDateFormat("dd-MMM-yyyy");
        statistics = MyUtilities.buildComboBox("Statistics");
        statistics.addItem(stat2); //tipo delle sessioni
        statistics.addItem(stat3); //il tempo del periodo indicato dall’utente meno il tempo di “fuori uso” (STD Down).
        statistics.addItem(stat4); //una tabella con queste colonne: mese, discrepanze aperte, interruzioni aperte, discrepanze risolte, interruzioni risolte.
        statistics.addItem(stat5); //valore numerico dei difetti
        statistics.addItem(stat6); //device performance
        statistics.select(stat2);
        statistics.addValueChangeListener(this);
        submit = new Button("Submit", this);
        //**************************************
        selectSim = MyUtilities.buildComboBox(Messages.SIMULATOR);
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
                    submit.setEnabled(true);
                } else {
                    submit.setEnabled(false);
                }
            }
        });
        if(selectSim.size() > 0) {
            selectSim.select(selectSim.getItemIds().iterator().next());
            submit.setEnabled(true);
        } else {
            submit.setEnabled(false);
        }
        //**************************************
        hl = new HorizontalLayout(from, to, statistics, selectSim, submit);
        hl.setComponentAlignment(submit, Alignment.BOTTOM_LEFT);
        hl.setSpacing(true);
        vl.addComponents(hl, new Image(null, new ThemeResource("charts.jpg")));
        vl.setExpandRatio(vl.getComponent(1), 9);
        vl.setComponentAlignment(vl.getComponent(1), Alignment.MIDDLE_CENTER);
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
//        if(((MyItem)event.getProperty().getValue()).equals(.....)) {
//           
//        } else {
//            
//        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        Date start = from.getValue();
        Date end = to.getValue();
        start.setHours(0); start.setMinutes(0); start.setSeconds(0);
        end.setHours(0); end.setMinutes(0); end.setSeconds(0);
        if(start != null || end != null) {
            if(end.getTime() <= start.getTime()) {
                MyNotification.SHOW(Messages.ERROR, Messages.ERROR_DATES, Notification.Type.ERROR_MESSAGE);
            } else {
                ThemeResource resource = null;
                String getVars = "";
                String startStr = formatDB.format(start);
                String endStr = formatDB.format(end);
                //////////////////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////
                //STAT 2 tipo delle sessioni
                //////////////////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////
                if(statistics.getValue().equals(stat2)) {
                    MyItem sim = (MyItem) selectSim.getValue();
                    getVars = "?start="+format.format(start)+"&end="+format.format(end)+"&sim="+sim.getName();
                    ArrayList<Map<String, String>> array = Queries.STAT2(db, startStr, endStr, sim.getId());
                    for(int i = 1; i <= 9; i++) {
                        Map<String, String> foo = array.get(i-1);
                        getVars += "&val"+i+"="+foo.get("val"+i);
                    }
                    int pcount = Queries.SELECT_COUNT_SESSION(db, startStr, endStr, sim.getId());
                    getVars += "&period_count="+pcount;
                    resource = new ThemeResource("stats2.html"+getVars);
                }
                if(statistics.getValue().equals(stat3)) {
                    resource = new ThemeResource("stats3.html");
                }
                if(statistics.getValue().equals(stat4)) {
                    resource = new ThemeResource("stats4.html");
                }
                if(statistics.getValue().equals(stat5)) {
                    MyItem sim = (MyItem) selectSim.getValue();
                    getVars = "?start="+format.format(start)+"&end="+format.format(end)+"&sim="+sim.getName();
                    ArrayList<Map<String, String>> array = Queries.STAT5(db, startStr, endStr, sim.getId());
                    for(int i = 1; i <= 3; i++) {
                        Map<String, String> foo = array.get(i-1);
                        getVars += "&val"+i+"="+foo.get("val"+i);
                    }
                    int pcount = Queries.SELECT_COUNT_SESSION(db, startStr, endStr, sim.getId());
                    getVars += "&period_count="+pcount;
                    resource = new ThemeResource("stats5.html"+getVars);
                }
                if(statistics.getValue().equals(stat6)) {
                    resource = new ThemeResource("stats6.html");
                }
                if(resource != null) {
                    BrowserFrame frame = new BrowserFrame(null, resource);
                    frame.setSizeFull();
                    Window win = new Window("Statistics");
                    win.setWidth("900px");
                    win.setHeight("80%");
                    win.setModal(true);
                    win.center();
                    win.setContent(frame);
                    UI.getCurrent().addWindow(win);
                }
            }
        }
    }
    
}