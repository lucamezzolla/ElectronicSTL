package aero.urbe.electronicstl.MyClasses;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author Luca Mezzolla
 */
public class MyUtilities {
    
    public final static boolean CHECK_LEVEL(int userLevelId, int requestedLevel) {
        return userLevelId >= requestedLevel;
    }
    
    public final static Button buildButton(String caption, String style, Resource icon) {
        Button foo = new Button();
        if(caption != null) foo.setCaption(caption);
        if(style != null) foo.setStyleName(style);
        if(icon != null) foo.setIcon(icon);
        foo.setWidth("100%");
        return foo;
    }
    
    public final static PopupDateField buildPopupDateField(String caption, boolean to, Resolution resolution ) {
        PopupDateField foo = new PopupDateField();
        foo.setLocale(Locale.UK);
        if(caption != null) foo.setCaption(caption);
        if(resolution != null) foo.setResolution(resolution);
        Calendar cal = Calendar.getInstance();
        //Date date =  cal.getTime();
        if(to) {
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));            
        } else {
            cal.set(Calendar.DAY_OF_MONTH, 1);            
        }
        foo.setValue(cal.getTime());
        foo.setTextFieldEnabled(false);
        foo.setWidth("100%");
        return foo;
    }
    
    public final static TextField buildTextField(String caption) {
        TextField foo = new TextField();
        if(caption != null) foo.setCaption(caption);
        foo.setWidth("100%");
        return foo;
    }
    
    public final static ComboBox buildComboBox(String caption) {
        ComboBox foo = new ComboBox();
        if(caption != null) foo.setCaption(caption);
        foo.setWidth("100%");
        foo.setImmediate(true);
        foo.setNullSelectionAllowed(false);
        foo.setTextInputAllowed(false);
        return foo;
    }
    
}