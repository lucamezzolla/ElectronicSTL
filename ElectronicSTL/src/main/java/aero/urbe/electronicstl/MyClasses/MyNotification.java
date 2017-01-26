package aero.urbe.electronicstl.MyClasses;

import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 *
 * @author Luca Mezzolla
 */
public class MyNotification {
    
    public static void SHOW(String message, Notification.Type type) {
        Notification note = new Notification(message, type);
        note.setPosition(Position.MIDDLE_CENTER);
        note.setDelayMsec(3000);
        note.show(UI.getCurrent().getPage());
    }
    
    public static void SHOW(String caption, String message, Notification.Type type) {
        Notification note = new Notification(caption, message, type);
        note.setPosition(Position.MIDDLE_CENTER);
        note.setDelayMsec(3000);
        note.show(UI.getCurrent().getPage());
    }
    
    public static void SHOW(String caption, String message, Notification.Type type, int delay) {
        Notification note = new Notification(caption, message, type);
        note.setPosition(Position.MIDDLE_CENTER);
        note.setDelayMsec(delay);
        note.show(UI.getCurrent().getPage());
    }
    
}