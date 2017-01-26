package aero.urbe.electronicstl.MyClasses;

import java.io.InputStream;

/**
 *
 * @author Luca Mezzolla
 */
public class FileItem {
    
    private String name;
    private InputStream is;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }
    
}