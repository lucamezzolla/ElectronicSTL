package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.MyClasses.MyUtilities;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.jdb;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Runo;
import org.vaadin.teemu.VaadinIcons;

/**
 *
 * @author Luca Mezzolla
 */
public class SuperAdminComponent extends CustomComponent {
    
    private final jdb db;
    private final User user;
    private final VerticalLayout vl;
    private Button reset, resetLogbook, resetUsers; 

    public SuperAdminComponent(jdb db, User user) {
        this.db = db;
        this.user = user;
        vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setWidth("100%");
        super.setCompositionRoot(vl);
        buildUI();
    }

    private void buildUI() {
        vl.removeAllComponents();
        reset = MyUtilities.buildButton("Reset database", Runo.BUTTON_BIG, VaadinIcons.DATABASE);
        resetUsers = MyUtilities.buildButton("Delete Users", Runo.BUTTON_BIG, VaadinIcons.USERS);
        resetLogbook = MyUtilities.buildButton("Delete Logbook and Defects", Runo.BUTTON_BIG, VaadinIcons.BOOK);
        reset.setWidth("250px");
        resetUsers.setWidth("250px");
        resetLogbook.setWidth("250px");
        vl.addComponents(new Label("<div style='height: 40px'></div>", ContentMode.HTML),reset, resetUsers, resetLogbook);
        vl.setComponentAlignment(reset, Alignment.MIDDLE_CENTER);
        vl.setComponentAlignment(resetLogbook, Alignment.MIDDLE_CENTER);
        vl.setComponentAlignment(resetUsers, Alignment.MIDDLE_CENTER);
    }
    
    
    
}