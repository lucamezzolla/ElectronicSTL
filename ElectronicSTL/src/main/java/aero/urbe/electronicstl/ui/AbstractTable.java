package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.MyClasses.MyUtilities;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.teemu.VaadinIcons;

/**
 *
 * @author Luca Mezzolla
 */
abstract class AbstractTable extends CustomComponent implements FieldEvents.TextChangeListener, ItemClickEvent.ItemClickListener {
    
    Table table;
    Button addItemButton;
    TextField searchField;
    ComboBox advancedCombo;
    final VerticalLayout layout;
    HorizontalLayout hl;

    AbstractTable() {
        layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeFull();
        super.setCompositionRoot(layout);
        buildUI();
    }
    
    private void buildUI() {
        layout.removeAllComponents();
        table = new Table();
        table.setImmediate(true);
        table.setSelectable(true);
        table.addItemClickListener(this);
        searchField = new TextField();
        searchField.setImmediate(true);
        searchField.setWidth("100%");
        searchField.addTextChangeListener(this);
        addItemButton = new Button(VaadinIcons.PLUS);
        addItemButton.setWidth("100%");
        advancedCombo = MyUtilities.buildComboBox(null);
        table.setSizeFull();       
        hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.setSpacing(true);
        layout.addComponents(hl, table);
        layout.setExpandRatio(table, 1f);
    }
    
    abstract void edit(Integer itemId);
    abstract void search(String text);

    @Override
    public void textChange(FieldEvents.TextChangeEvent event) {
        search(event.getText());
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        if(event.isDoubleClick()) {
            edit((Integer)event.getItemId());
        }
    }
    
}