package aero.urbe.electronicstl;

import aero.urbe.electronicstl.Constants.Messages;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.ui.MainPage;
import com.vaadin.annotations.Push;
import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Push(PushMode.AUTOMATIC)
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        try {
            VaadinSession.getCurrent().getSession().setMaxInactiveInterval(18000); //5 hours
            UI.getCurrent().getSession().getLockInstance();
            //Set DB
            jdb db = new jdb("127.0.0.1", "stldb_test", "webadmin", "fgE36y%qkB*@_WYx$mbM");
            //Start
            MainPage mp = new MainPage(db);
            mp.setSizeFull();
            setContent(mp);
        } catch (Exception ex) {
            MyNotification.SHOW("Error", Messages.ERROR_GENERIC, Notification.Type.ERROR_MESSAGE);
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
    
}