package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Constants;
import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.Interfaces.LoginInterface;
import aero.urbe.electronicstl.MyClasses.MyUtilities;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.jdb;
import com.vaadin.annotations.Push;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.vaadin.teemu.VaadinIcons;

/**
 *
 * @author Luca Mezzolla
 */
public class MainPage extends CustomComponent implements LoginInterface {

    private final jdb db;
    private User user;
    private final AbsoluteLayout al;
    private final Styles styles;
    private final static int SUPER_ADMIN_COMPONENT = 10;
    private final static int ADMIN_COMPONENT = 1;
    private final static int STATS_COMPONENT = 2;
    private final static int LOGBOOK_COMPONENT = 3;
    private final static int TECHNICAL_COMPONENT = 4;
    private final static int USERS_COMPONENT = 5;
    private Label utcTimeLabel;
    private Date utcDate;
    private Timer timer;
    private TimerTask timerTask;
    
    public MainPage(jdb db) {
        this.db = db;
        al = new AbsoluteLayout();
        al.setSizeFull();
        utcDate = new Date();
        styles = UI.getCurrent().getPage().getStyles();
        styles.add(".v-generated-body .v-absolutelayout { background-color: white }");
        super.setCompositionRoot(al);
        doLogin();
    }

    private void doLogin() {
        Window win = new Window(Messages.LOGIN);
        LoginComponent login = new LoginComponent(db);
        login.setListener(this);
        win.setContent(login);
        win.setResizable(false);
        win.setClosable(false);
        win.setWidth("25%");
        win.center();
        UI.getCurrent().addWindow(win);
    }

    @Override
    public void onLogin(User user) {
        this.user = user;
        Collection<Window> windows = UI.getCurrent().getWindows();
        while(windows.iterator().hasNext()) {
            windows.iterator().next().close();
        }
        buildUI();
        clock();
    }

    private void buildUI() {
        al.removeAllComponents();
        Image logo = new Image(null, new ThemeResource("pics/logoda.jpg"));
        logo.setDescription(Messages.COMPANY_TITLE);
        Button profileButton = new Button(user.getName()+" "+user.getLastname(), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ProfileWindow win = new ProfileWindow(db, user);
                UI.getCurrent().addWindow(win);
            }
        });
        profileButton.setStyleName(Runo.BUTTON_LINK);
        //Label welcomeLabel = new Label("<div style='text-align: right'>"+user.getName()+" "+user.getLastname()+"</div>", ContentMode.HTML);
        Label userLevelLabel = new Label("<div style='text-align: right; margin-top: 2px'>Level: "+user.getLevelId()+" - "+user.getLevelName()+"</div>", ContentMode.HTML);
        utcTimeLabel = new Label("<div style='text-align: right; margin-top: 2px; margin-bottom: 2px'>"+utcDate.toGMTString()+"</div>");
        utcTimeLabel.setContentMode(ContentMode.HTML);
        utcTimeLabel.setImmediate(true);
        Button logoutButton = new Button("Logout", VaadinIcons.POWER_OFF);
        logoutButton.setStyleName(Runo.BUTTON_SMALL);
        logoutButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    db.getConnection().close();
                } catch (SQLException ex) { }
                timerTask.cancel();
                timer.cancel();
                timer.purge();
                UI.getCurrent().getSession().close();
                Page.getCurrent().reload();
            }
        });
        Label testVersionLabel = new Label("<p style='text-align: center; font-size: 30px; background-color: yellow'>TEST VERSION</p>", ContentMode.HTML);
        VerticalLayout vl = new VerticalLayout(profileButton, userLevelLabel, utcTimeLabel, logoutButton);
        vl.setComponentAlignment(profileButton, Alignment.TOP_RIGHT);
        HorizontalLayout hl = new HorizontalLayout(logo, testVersionLabel,vl);
        hl.setMargin(true);
        hl.setWidth("100%");
        vl.setWidth("100%");
        vl.setSpacing(false);
        vl.setComponentAlignment(logoutButton, Alignment.TOP_RIGHT);
        final TabSheet tabs = new TabSheet();
        tabs.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                Collection<Window> windows = UI.getCurrent().getWindows();
                if(windows.size() > 0) {
                    Iterator iterator = windows.iterator();
                    while(iterator.hasNext()) {
                        Window win = (Window) iterator.next();
                        if(win.getCaption().equals(Messages.ADVANCED_SEARCH)) {
                            win.setVisible(false);
                        }
                    }
                }
            }
        });
        tabs.setSizeFull();
        //LOGBOOK TAB
        tabs.addTab(buildTab(LOGBOOK_COMPONENT), Messages.TAB_STL_PAGES);
        //TECNICAL AREA TAB
        tabs.addTab(buildTab(TECHNICAL_COMPONENT), Messages.TAB_TECNHICAL_AREA);
        //USER TAB
        tabs.addTab(buildTab(USERS_COMPONENT), Messages.TAB_USERS);
        //STATISTICS TAB
        tabs.addTab(buildTab(STATS_COMPONENT), Messages.TAB_STATS);
        //ADMINISTRATION TAB
        tabs.addTab(buildTab(ADMIN_COMPONENT), Messages.TAB_ADMIN);
        //SUPER ADMINISTRATION TAB
        if(MyUtilities.CHECK_LEVEL(user.getLevelId(), Constants.SUPER_ADMIN)) {
            tabs.addTab(buildTab(SUPER_ADMIN_COMPONENT), Messages.TAB_SUPER_ADMIN);
        }
        //INFO TAB
        VerticalLayout infoVl = new VerticalLayout();
        infoVl.setId("info");
        tabs.addTab(infoVl, Messages.TAB_INFO);
        tabs.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                if(event.getTabSheet().getSelectedTab().getId().equals("info")) {
                    Window win = new Window(Messages.TAB_INFO);
                    InfoComponent infoC = new InfoComponent(win);
                    win.setContent(infoC);
                    win.setWidth("35%");
                    win.setResizable(false);
                    win.setModal(true);
                    UI.getCurrent().addWindow(win);
                    event.getTabSheet().setSelectedTab(0);
                }
            }
        });
        //
        al.addComponent(hl, "top: 0px; left: 0px");
        al.addComponent(tabs, "top: 95px; left: 15px; bottom: 15px; right: 15px");
    }
    
    private CustomComponent buildTab(int type) {
        CustomComponent component = null;
        switch(type) {
            case STATS_COMPONENT:
                component = new Statistics(db);
                break; 
            case LOGBOOK_COMPONENT:
                component = new LogBookComponent(db, user);
                break; 
            case TECHNICAL_COMPONENT:
                component = new TechnicalComponent(db, user);
                break; 
            case USERS_COMPONENT:
                component = new UsersComponent(db, user);
                break;
            case ADMIN_COMPONENT:
                component = new AdminComponent(db, user);
                break; 
            case SUPER_ADMIN_COMPONENT:
                component = new SuperAdminComponent(db, user);
                break;   
        }
        component.setId("");
        component.setSizeFull();
        return component;
    }

    private void clock() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                utcDate.setTime(utcDate.getTime()+1000);
                utcTimeLabel.setValue("<div style='text-align: right; margin-top: 2px; margin-bottom: 2px'>"+utcDate.toGMTString()+"</div>");
                //UI.getCurrent().push();
            }
        };
        timer.schedule(timerTask, 0, 1000);
        timerTask.run();
    }
    
}