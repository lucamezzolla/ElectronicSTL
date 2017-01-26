package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.MyClasses.MyNotification;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

/**
 *
 * @author Luca Mezzolla
 */
public class InfoComponent extends CustomComponent implements Button.ClickListener {

    private final VerticalLayout vl;
    private final HorizontalLayout hl;
    private final Button manualButton;
    private final Button videoButton;
    private final Label title;
    private final Label version;
    private final Label author;
    private final Button closeButton;
    private final Window win;
    
    public InfoComponent(Window win) {
        this.win = win;
        vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setWidth("100%");
        
        title = new Label("ELECTRONIC STL");
        version = new Label("Version 0.1.0");
        author = new Label("Developed by Luca Mezzolla");
        
        title.setStyleName(Runo.LABEL_H1);
        version.setStyleName(Runo.LABEL_H2);
        author.setStyleName(Runo.LABEL_SMALL);
        
        manualButton = new Button("Manual", this);
        videoButton = new Button("Video", this);
        closeButton = new Button("Close", this);
        
        closeButton.setStyleName(Runo.BUTTON_DEFAULT);
        
        hl = new HorizontalLayout(manualButton, videoButton, closeButton);
        hl.setSpacing(true);
        
        vl.addComponents(title, version, author, hl);
        vl.setComponentAlignment(hl, Alignment.BOTTOM_RIGHT);
        
        super.setCompositionRoot(vl);
        
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        Button b = event.getButton();
        if(b == closeButton) {
            win.close();
        } else {
            MyNotification.SHOW("Under Construction!", Notification.Type.WARNING_MESSAGE);
        }
    }
    
    
    
}