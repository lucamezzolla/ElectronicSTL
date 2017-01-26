package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.MyClasses.DefectItem;
import aero.urbe.electronicstl.MyClasses.MyItem;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.MyClasses.MyUtilities;
import aero.urbe.electronicstl.jdb;
import com.vaadin.data.Property;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import org.vaadin.teemu.VaadinIcons;
import org.vaadin.teemu.switchui.Switch;

/**
 *
 * @author Luca Mezzolla
 */
public class DefectSolutionWindow extends Window implements Button.ClickListener {
    
    private final DefectItem item;
    private TextField pageId, simulator, dateStart, defectTypeAssigned, ataSysCode, ataSubCode, ataDescription;
    private TextArea description, correctiveAction;
    private Switch solved;
    private Button submit, close, infoAta;
    private final VerticalLayout mainVL;
    private final VerticalLayout vl1;
    private final VerticalLayout vl2;
    private final HorizontalLayout hl;
    private final Styles styles;
    private ComboBox defectTypeCorrected;
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy (HH:mm)");
    private final SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final jdb db;
    private TechicalAreaTableInterface listener;

    public DefectSolutionWindow(jdb db, DefectItem item) {
        this.db = db;
        this.item = item;
        styles = Page.getCurrent().getStyles();
        styles.add("#disabled { pointer-events: none; background-image: none; background-color: #dbdbdb }");
        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        formatDB.setTimeZone(gmtTime);
        hl = new HorizontalLayout();
        mainVL = new VerticalLayout();
        vl1 = new VerticalLayout();
        vl2 = new VerticalLayout();
        hl.setSpacing(true);
        hl.setMargin(false);
        vl1.setMargin(true);
        vl2.setMargin(true);
        vl1.setSpacing(true);
        vl2.setSpacing(true);
        hl.setWidth("100%");
        mainVL.setWidth("100%");
        mainVL.setMargin(true);
        mainVL.setSpacing(true);
        vl1.setWidth("100%");
        vl2.setWidth("100%");
        super.setCaption(Messages.TAB_TECNHICAL_AREA);
        super.setWidth("60%");
        super.setModal(true);
        super.setResizable(false);
        super.center();
        super.setContent(buildUI());
    }

    public void setListener(TechicalAreaTableInterface listener) {
        this.listener = listener;
    }
    
    public interface TechicalAreaTableInterface {
        void updateTable();
    }

    private VerticalLayout buildUI() {
        mainVL.removeAllComponents();
        //vl1
        Panel p1 = new Panel("Defect");
        p1.setWidth("100%");
        dateStart = new TextField("Date"); dateStart.setWidth("100%"); dateStart.setId("disabled");
        pageId = new TextField("Page ID"); pageId.setWidth("100%"); pageId.setId("disabled");
        simulator = new TextField("Simulator"); simulator.setWidth("100%"); simulator.setId("disabled");
        defectTypeAssigned = new TextField(Messages.DEFECT_TYPE); defectTypeAssigned.setWidth("100%"); defectTypeAssigned.setId("disabled");
        description = new TextArea("Description"); description.setWidth("100%"); description.setId("disabled");
        vl1.addComponents(pageId, dateStart, simulator, defectTypeAssigned, description);
        p1.setContent(vl1);
        //vl2
        Panel p2 = new Panel("Solution");
        p2.setWidth("100%");
        defectTypeCorrected = MyUtilities.buildComboBox(Messages.DEFECT_TYPE);
        ArrayList<MyItem> defectComboItems = Queries.SELECT_TABLE(db, "stl_defect_type");
        for(MyItem foo : defectComboItems) {
            defectTypeCorrected.addItem(foo);
        }
        Iterator iterator = defectTypeCorrected.getItemIds().iterator();
        while(iterator.hasNext()) {
            MyItem foo = (MyItem) iterator.next();
            if(foo.getId() == item.getDefectTypeId()) {
                defectTypeCorrected.select(foo);
            }
        }
        ataSysCode = new TextField("ATA System Code"); ataSysCode.setWidth("100%");
        ataSubCode = new TextField("ATA Sub Code"); ataSubCode.setWidth("100%");
        ataDescription = new TextField("ATA Description"); ataDescription.setWidth("100%");
        infoAta = new Button(VaadinIcons.QUESTION);
        infoAta.setWidth("100%");
        infoAta.setStyleName(Runo.BUTTON_SMALL);
        infoAta.addClickListener(this);
        HorizontalLayout hlInfo = new HorizontalLayout(ataDescription, infoAta);
        hlInfo.setSpacing(true);
        hlInfo.setWidth("100%");
        hlInfo.setExpandRatio(ataDescription, 0.8f);
        hlInfo.setExpandRatio(infoAta, 0.2f);
        hlInfo.setComponentAlignment(infoAta, Alignment.BOTTOM_LEFT);
        correctiveAction = new TextArea("Corrective Action"); correctiveAction.setWidth("100%");
        submit = new Button("Save", this);
        close = new Button("Close without save", this);
        close.setStyleName(Runo.BUTTON_DEFAULT);
        vl2.addComponents(defectTypeCorrected, ataSysCode, ataSubCode, hlInfo, correctiveAction);
        p2.setContent(vl2);
        //hl
        hl.addComponents(p1, p2);
        //*******************************************************************
        solved = new Switch();
        solved.setAnimationEnabled(true);
        solved.setImmediate(true);
        solved.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if(!(Boolean)event.getProperty().getValue()) {
                    resetSolution(false);
                } else {
                    resetSolution(true);
                }
            }
        });
        HorizontalLayout hlButtons = new HorizontalLayout(new Label("<div style='text-align: right'>Solved?</div>", ContentMode.HTML), solved, close, submit);
        hlButtons.setSpacing(true);
        //********************************************************************
        mainVL.addComponents(hl, hlButtons);
        mainVL.setComponentAlignment(hlButtons, Alignment.MIDDLE_RIGHT);
        //fill components
        pageId.setValue(String.valueOf(item.getId()));
        dateStart.setValue(format.format(item.getDatetimeStart()));
        simulator.setValue(item.getSimulator());
        defectTypeAssigned.setValue(item.getDefectType());
        description.setValue(item.getDescription());
        if(item.getSolved() == 1) {
            solved.setValue(Boolean.TRUE);
            resetSolution(true);
        } else {
            solved.setValue(Boolean.FALSE);
            resetSolution(false);
        }
        //
        return mainVL;
    }
    
    private void resetSolution(boolean value) {
        if(!value) {
            defectTypeCorrected.select(defectTypeCorrected.getItemIds().iterator().next()); defectTypeCorrected.setEnabled(false);
            ataSysCode.setValue(""); ataSysCode.setEnabled(false);
            ataSubCode.setValue(""); ataSubCode.setEnabled(false);
            ataDescription.setValue(""); ataDescription.setEnabled(false);
            correctiveAction.setValue(""); correctiveAction.setEnabled(false);
            infoAta.setEnabled(false);
        } else {
            defectTypeCorrected.setEnabled(true);
            ataSysCode.setEnabled(true);
            ataSubCode.setEnabled(true);
            ataDescription.setEnabled(true);
            correctiveAction.setEnabled(true);
            Iterator iterator = defectTypeCorrected.getItemIds().iterator();
            while(iterator.hasNext()) {
                MyItem foo = (MyItem) iterator.next();
                if(foo.getId() == item.getDefectTypeId()) {
                    defectTypeCorrected.select(foo);
                }
            }
            ataSysCode.setValue(item.getAtaSysCode());
            ataSubCode.setValue(item.getAtaSubCode());
            ataDescription.setValue(item.getAtaDescription());
            infoAta.setEnabled(true);
            correctiveAction.setValue(item.getCorrectiveAction());
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        Button foo = event.getButton();
        if(foo == infoAta) {
            ThemeResource resource = new ThemeResource("ata_codes.pdf");
            BrowserFrame bf = new BrowserFrame(null, resource);
            bf.setSizeFull();
            Window win = new Window("ATA Codes");
            win.setWidth("80%");
            win.setHeight("80%");
            win.setModal(true);
            win.setContent(bf);
            UI.getCurrent().addWindow(win);
        }
        if(foo == submit) {
            int id = item.getId();
            int defectTypeId = ((MyItem)defectTypeCorrected.getValue()).getId();
            String ataSysCodeVal = ataSysCode.getValue();
            String ataSubCodeVal = ataSubCode.getValue();
            String ataDescriptionVal = ataDescription.getValue();
            String correction = correctiveAction.getValue();
            Date dateEnd = new Date();
            String dateEndStr = formatDB.format(dateEnd);
            int solvedValue = solved.getValue() ? 1 : 0;
            if(solvedValue == 1) {
                if(ataSysCodeVal.length() > 0 && ataSubCodeVal.length() > 0 && ataDescriptionVal.length() > 0 && correction.length() > 0) {
                    try {
                        Queries.UPDATE_DEFECT(db, id, defectTypeId, ataSysCodeVal, ataSubCodeVal, ataDescriptionVal, correction, dateEndStr, solvedValue);
                        if(listener != null) {
                            listener.updateTable();
                        }
                        super.close();
                    } catch (Exception ex) {
                        MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
                    }
                } else {
                    MyNotification.SHOW(Messages.ERROR, Messages.ERROR_EMPTY_FIELDS, Notification.Type.ERROR_MESSAGE);
                }
            } else {
                try {
                    Queries.UPDATE_DEFECT(db, id, item.getDefectTypeId(), "0", "0", "", correction, "", solvedValue);
                    if(listener != null) {
                        listener.updateTable();
                    }
                    super.close();
                } catch (Exception ex) {
                    MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
                }
            }
        }
        if(foo == close) {
            super.close();
        }
    }
    
    
}