package aero.urbe.electronicstl.Constants;

import aero.urbe.electronicstl.MyClasses.MyItem;

/**
 *
 * @author Luca Mezzolla
 */
public class Constants {
    
    public final static int SEARCH_MIN_CHARS = 2;
    public final static int USER = 1;
    public final static int DEVICE_USER = 2;
    public final static int TECHNICAL = 3;
    public final static int ADMIN = 4;
    public final static int SUPER_ADMIN = 5;
    
    public final static MyItem CUSTOMERS_ITEM = new MyItem(0, "Customers");
    public final static MyItem SIMULATORS_ITEM = new MyItem(1, "Simulators");
    public final static MyItem SIMULATORS_STATUS_ITEM = new MyItem(2, "Simulators Status");
    
    
}