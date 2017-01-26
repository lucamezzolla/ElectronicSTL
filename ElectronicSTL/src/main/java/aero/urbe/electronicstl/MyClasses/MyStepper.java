package aero.urbe.electronicstl.MyClasses;

import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

/**
 *
 * @author Luca Mezzolla
 */
public class MyStepper extends CustomComponent implements Button.ClickListener {
    
    private final HorizontalLayout mainLayout;
    private TextField valueTextField;
    private Button up, down;
    private Double value = 0.0;
    private MyStepperInterface listener;
    private Styles styles = Page.getCurrent().getStyles();

    public MyStepper() {
        mainLayout = new HorizontalLayout();
        mainLayout.setWidth("100%");
        styles.add("#disabled { pointer-events: none; background-image: none; background-color: #dbdbdb }");
        super.setCompositionRoot(mainLayout);
        buildUI();
    }

    public void setListener(MyStepperInterface listener) {
        this.listener = listener;
    }
    
    private void buildUI() {
        mainLayout.removeAllComponents();
        valueTextField = new TextField();
        valueTextField.setId("disabled");
        valueTextField.setWidth("100%");
        valueTextField.setHeight("24px");
        up = new Button(new ThemeResource("pics/up.png"));
        down = new Button(null, new ThemeResource("pics/down.png"));
        up.setWidth("100%");
        down.setWidth("100%");
        up.addClickListener(this);
        down.addClickListener(this);
        mainLayout.addComponents(valueTextField, down, up);
        mainLayout.setComponentAlignment(down, Alignment.BOTTOM_CENTER);
        mainLayout.setComponentAlignment(up, Alignment.BOTTOM_CENTER);
        mainLayout.setExpandRatio(valueTextField, 0.7f);
        mainLayout.setExpandRatio(down, 0.15f);
        mainLayout.setExpandRatio(up, 0.15f);
        setValue(value);
    }
    
    public void setCaption(String text) {
        valueTextField.setCaption(text);
    }
    
    public String getValue() {
        return valueTextField.getValue();
    }
    
    public void setValue(Double value) {
        this.value = value;
        String result = String.format("%.1f", value);
        valueTextField.setValue(result);
        if(listener != null) {
            listener.change();
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if(event.getSource() == up) {
            value = value + 0.1;
            setValue(value);
        } else {
            value = value - 0.1;
            setValue(value);
        }
    }   
    
    
    
}
