package aero.urbe.electronicstl.MyClasses;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

/**
 *
 * @author Luca Mezzolla
 */
public class ConfirmDialog extends Window implements Button.ClickListener {

    private final VerticalLayout vl = new VerticalLayout();
    private Button submit;
    private Button cancel;
    private HorizontalLayout hl;
    private ConfirmDialogInterface listener;
    private Label extraInfo;

    public ConfirmDialog() {
        super.setCaption("Confirm");
        super.setWidth("300px");
        super.setModal(true);
        super.setResizable(false);
        super.setContent(vl);
        super.center();
        extraInfo = new Label();
        buildUI();
    }

    public void setListener(ConfirmDialogInterface listener) {
        this.listener = listener;
    }
    
    private void buildUI() {
        vl.removeAllComponents();
        vl.setMargin(true);
        vl.setSpacing(true);
        submit = new Button("Yes", this);
        cancel = new Button("No", this);
        cancel.setStyleName(Runo.BUTTON_DEFAULT);
        submit.setWidth("100%");
        cancel.setWidth("100%");
        submit.setClickShortcut(KeyCode.ENTER);
        hl = new HorizontalLayout(cancel, submit);
        hl.setWidth("100%");
        hl.setSpacing(true);
        vl.addComponents(extraInfo, new Label("Are you sure?"), hl);
        vl.setComponentAlignment(hl, Alignment.MIDDLE_RIGHT);
    }

    public void setExtraInfo(String text) {
        if(text.length() > 0) {
            extraInfo.setValue(text);
        }
    }
    
    @Override
    public void buttonClick(Button.ClickEvent event) {
        Button button = event.getButton();
        if(listener != null) {
            if(button == submit) {
                listener.submit();
            } else if(button == cancel) {
                listener.cancel();
            }
        }
        super.close();
    }
    
}