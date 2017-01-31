package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Constants;
import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.MyClasses.DefectItem;
import aero.urbe.electronicstl.MyClasses.FileItem;
import aero.urbe.electronicstl.MyClasses.MyItem;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.MyClasses.MyUtilities;
import aero.urbe.electronicstl.MyClasses.TechnicalItem;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.jdb;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.vaadin.teemu.VaadinIcons;

/**
 *
 * @author Luca Mezzolla
 */
public class TechnicalComponent extends CustomComponent implements Button.ClickListener, Property.ValueChangeListener {

    private final jdb db;
    private final User user;
    private final MyItem deferred, mitigation, defects;
    private final VerticalLayout layout;
    private final ComboBox advancedCombo, selectSim;
    private final HorizontalLayout hl;
    private final Button submit;
    private final Table defectsTable;
    private final SimpleDateFormat format;
    private final ItemClickEvent.ItemClickListener defectsTableItemClickListner;
    private final Table technicalItemsTable;
    private final Upload upload;
    private final Uploader receiver;

    public TechnicalComponent(final jdb db, User user) {
        this.db = db;
        this.user = user;
        Styles styles = Page.getCurrent().getStyles();
        styles.add("#centered { vertical-align: middle }");
        layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeFull();
        format = new SimpleDateFormat("dd-MM-yyyy (HH:mm)");
        defects = Constants.DEFECT_ITEM;
        deferred = Constants.DEFERRED_ITEMS_ITEM;
        mitigation = Constants.MITIGATION_PROCEDURE_ITEM;
        advancedCombo = MyUtilities.buildComboBox(Messages.TAB_TECNHICAL_AREA);
        advancedCombo.addItem(defects);
        advancedCombo.addItem(deferred);
        advancedCombo.addItem(mitigation);
        advancedCombo.addValueChangeListener(this);
        advancedCombo.setImmediate(true);
        selectSim = MyUtilities.buildComboBox(Messages.SIMULATOR);
        selectSim.removeAllItems();
        submit = new Button(Messages.SEARCH, this);
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
        selectSim.setImmediate(true);
        receiver = new Uploader();
        receiver.setDb(db);
        receiver.setListener(new UploadInterface() {
            @Override
            public void uploadTable() {
                submit.click();
            }
        });
        upload = new Upload();
        upload.setReceiver(receiver);
        upload.addSucceededListener(receiver);
        advancedCombo.setWidthUndefined();
        selectSim.setWidthUndefined();
        hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidth("100%");
        hl.addComponents(advancedCombo, selectSim, submit, upload);
        upload.setEnabled(false);
        hl.setExpandRatio(advancedCombo, 0.1f);
        hl.setExpandRatio(selectSim, 0.1f);
        hl.setExpandRatio(submit, 0.1f);
        hl.setExpandRatio(upload, 0.7f);
        hl.setComponentAlignment(upload, Alignment.BOTTOM_RIGHT);
        hl.setComponentAlignment(submit, Alignment.BOTTOM_LEFT);
        //----------------------------------------------------------
        defectsTableItemClickListner = new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if(event.isDoubleClick()) {
                    if(event.getItemId() != null) {
                        DefectItem di = (DefectItem) event.getItemId();
                        DefectSolutionWindow win = new DefectSolutionWindow(db, di);
                        win.setListener(new DefectSolutionWindow.TechicalAreaTableInterface() {
                            @Override
                            public void updateTable() {
                                submit.click();
                            }
                        });
                        UI.getCurrent().addWindow(win);
                    }
                }
            }
        };
        //----------------------------------------------------------
        defectsTable = buildDefectsTable();
        defectsTable.setVisible(false);
        defectsTable.addItemClickListener(defectsTableItemClickListner);
        technicalItemsTable = buildTechicalItemsTable();
        technicalItemsTable.setVisible(false);
        technicalItemsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if(event.isDoubleClick()) {
                    if(event.getItemId() != null) {
                        try {
                            TechnicalItem foo = (TechnicalItem) event.getItemId();
                            FileItem fileItem = Queries.SELECT_FILE(db, foo);
                            if(fileItem != null) {
                                FileOutputStream output = null;
                                String fileName = fileItem.getName();
                                InputStream input = fileItem.getIs();
                                File file = new File("/tmp/"+fileName);
                                output = new FileOutputStream(file);
                                IOUtils.copy(input,output);
                                input.close();
                                output.close();
                                final Window win = new Window(fileName);
                                win.setWidth("20%");
                                win.setModal(true);
                                win.setResizable(false);
                                Button downloadButton = new Button("Download");
                                Button closeButton = new Button("Close", new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(Button.ClickEvent event) {
                                        win.close();
                                    }
                                });
                                downloadButton.setWidth("100%");
                                closeButton.setWidth("100%");
                                FileDownloader fileDownloader = new FileDownloader(new FileResource(file));
                                fileDownloader.extend(downloadButton);
                                VerticalLayout vl = new VerticalLayout(downloadButton, closeButton);
                                vl.setSpacing(true);
                                vl.setMargin(true);
                                win.setContent(vl);
                                UI.getCurrent().addWindow(win);
                            } else {
                                MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
                            }
                        } catch(Exception ex) {
                            MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        advancedCombo.select(defects);
        layout.addComponents(hl, defectsTable, technicalItemsTable);
        layout.setExpandRatio(defectsTable, 9);
        layout.setExpandRatio(technicalItemsTable, 9);
        super.setCompositionRoot(layout);
        setPrivileges();
    }
    
    private void setPrivileges() {
        if(user.getLevelId() == Constants.USER) {
            advancedCombo.removeItem(deferred);
            advancedCombo.removeItem(mitigation);
            advancedCombo.select(defects);
            defectsTable.removeItemClickListener(defectsTableItemClickListner);
        }
        if(user.getLevelId() == Constants.DEVICE_USER) {
            defectsTable.removeItemClickListener(defectsTableItemClickListner);
        }
        if(user.getLevelId() == Constants.TECHNICAL) {
            advancedCombo.removeItem(deferred);
            advancedCombo.removeItem(mitigation);
            advancedCombo.select(defects);
        }
        if(user.getLevelId() == Constants.ADMIN) {
            //
        }
        if(user.getLevelId() == Constants.SUPER_ADMIN) {
            //
        }
    }
    
    private Table buildTechicalItemsTable() {
        Table foo = new Table();
        foo.setImmediate(true);
        foo.setSizeFull(); 
        foo.setImmediate(true);
        foo.setSelectable(true);
        foo.addContainerProperty("Remove", Button.class, null);
        foo.addContainerProperty("File", String.class, null);
        foo.setColumnWidth("Remove", 70);
        foo.setColumnAlignment("Remove", Table.Align.CENTER);
        return foo;
    }
    
    private Table buildDefectsTable() {
        Table foo = new Table();
        foo.setImmediate(true);
        foo.setSizeFull(); 
        foo.setImmediate(true);
        foo.setSelectable(true);
        foo.addContainerProperty("Solved", Image.class, null);
        foo.addContainerProperty("Page ID", String.class, null);
        foo.addContainerProperty("Date", String.class, null);
        foo.addContainerProperty("Type", String.class, null);
        foo.addContainerProperty("Description", String.class, null);
        foo.addContainerProperty("Corrective Action", String.class, null);
        foo.setColumnWidth("Solved", 70);
        foo.setColumnWidth("Page ID", 90);
        foo.setColumnWidth("Date", 170);
        foo.setColumnWidth("Type", 170);
        foo.setColumnAlignment("Solved", Table.Align.CENTER);
        foo.setColumnAlignment("Page ID", Table.Align.CENTER);
        return foo;
    }
    
    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(((MyItem) advancedCombo.getValue()).equals(defects)) {
            upload.setEnabled(false);
            MyItem simulatorItem = (MyItem) selectSim.getValue();
            ArrayList<DefectItem> defectsArray = Queries.SELECT_DEFECTS(db, simulatorItem.getId());
            if(defectsArray.size() > 0) {
                defectsTable.removeAllItems();
                Iterator iterator = defectsArray.iterator();
                while(iterator.hasNext()) {
                    DefectItem foo = (DefectItem) iterator.next();
                    Image image = new Image();
                    String correction = "";
                    if(foo.getSolved() == 1) {
                        image.setSource(new ThemeResource("green.png"));
                        correction = foo.getCorrectiveAction();
                    } else {
                        image.setSource(new ThemeResource("red.png"));
                    }
                    image.setWidth("40px");
                    image.setHeight("15px");
                    image.setId("centered");
                    defectsTable.addItem(new Object[] { image, String.valueOf(foo.getPageId()), 
                        format.format(foo.getDatetimeStart()), foo.getDefectType(), 
                        foo.getDescription(), correction }, foo);
                }
                defectsTable.setVisible(true);
            } else {
                defectsTable.setVisible(false);
                MyNotification.SHOW("No defect found!", Notification.Type.WARNING_MESSAGE);
            }
        }
        if(((MyItem) advancedCombo.getValue()).equals(deferred)) {
            upload.setEnabled(true);
            receiver.setTypeId(2);
            receiver.setSimulatorId(((MyItem)selectSim.getValue()).getId());
            defectsTable.setVisible(false);
            technicalItemsTable.removeAllItems();
            ArrayList<TechnicalItem> items = Queries.SELECT_TECHNICAL_ITEMS(db, 2, ((MyItem)selectSim.getValue()).getId());
            if(items.size() > 0) {
                for(int i = 0; i < items.size(); i++) {
                    final TechnicalItem foo = items.get(i);
                    Button trash = new Button(VaadinIcons.MINUS);
                    trash.setWidth("100%");
                    trash.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            Queries.REMOVE_ITEM(db, foo.getId());
                            submit.click();
                        }
                    });
                    trash.setStyleName(Runo.BUTTON_SMALL);
                    trash.setId(String.valueOf(foo.getId()));
                    technicalItemsTable.addItem(new Object[] { trash, foo.getName() }, foo);
                }
                technicalItemsTable.setVisible(true);
            } else {
                technicalItemsTable.setVisible(false);
            }
        }
        if(((MyItem) advancedCombo.getValue()).equals(mitigation)) {
            upload.setEnabled(true);
            receiver.setTypeId(3);
            receiver.setSimulatorId(((MyItem)selectSim.getValue()).getId());
            defectsTable.setVisible(false);
            technicalItemsTable.removeAllItems();
            ArrayList<TechnicalItem> items = Queries.SELECT_TECHNICAL_ITEMS(db, 3, ((MyItem)selectSim.getValue()).getId());
            if(items.size() > 0) {
                for(int i = 0; i < items.size(); i++) {
                    final TechnicalItem foo = items.get(i);
                    Button trash = new Button(VaadinIcons.MINUS);
                    trash.setWidth("100%");
                    trash.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            Queries.REMOVE_ITEM(db, foo.getId());
                            submit.click();
                        }
                    });
                    trash.setStyleName(Runo.BUTTON_SMALL);
                    trash.setId(String.valueOf(foo.getId()));
                    technicalItemsTable.addItem(new Object[] { trash, foo.getName() }, foo);
                }
                technicalItemsTable.setVisible(true);
            } else {
                technicalItemsTable.setVisible(false);
            }
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        //SIMULATOR STATUS
        if(((MyItem)event.getProperty().getValue()).getId() == 1) {
            technicalItemsTable.setVisible(false);
            upload.setEnabled(false);
        }
        //DEFERRED ITEM
        if(((MyItem)event.getProperty().getValue()).getId() == 2) {
            selectSim.setVisible(true);
            submit.setVisible(true);
        }
        //MITIGATION PROCEDURE
        if(((MyItem)event.getProperty().getValue()).getId() == 3) {
            selectSim.setVisible(true);
            submit.setVisible(true);
        }
    }

}

interface UploadInterface {
    void uploadTable();
}

class Uploader implements Receiver, SucceededListener {
    
    public File file;
    private int typeId, simulatorId;
    private jdb db;
    private UploadInterface listener;

    public void setListener(UploadInterface listener) {
        this.listener = listener;
    }
    
    public OutputStream receiveUpload(String filename, String mimeType) {
        FileOutputStream fos = null; // Stream to write to
        try {
            file = new File("/tmp/" + filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            MyNotification.SHOW("Warning", "No file selected!", Notification.Type.WARNING_MESSAGE);
        }
        return fos;
    }
    
    public void setTypeId(int type) {
        typeId = type;
    }

    public void setSimulatorId(int simulatorId) {
        this.simulatorId = simulatorId;
    }
    
    public void setDb(jdb db1) {
        db = db1;
    }

    public void uploadSucceeded(SucceededEvent event) {
        if(file != null) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                Queries.INSERT_FILE(db, file.getName(), inputStream, typeId, simulatorId);
                MyNotification.SHOW("Added!", Type.HUMANIZED_MESSAGE);
                inputStream.close();
                file.delete();
                if(listener != null) {
                    listener.uploadTable();
                }
            } catch(Exception ex) {
                MyNotification.SHOW(Messages.ERROR, Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
            }
        } else {
            MyNotification.SHOW("No selected file", Notification.Type.WARNING_MESSAGE);
        }
    }
    
};