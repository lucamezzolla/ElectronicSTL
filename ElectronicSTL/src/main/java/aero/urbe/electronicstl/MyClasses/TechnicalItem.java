package aero.urbe.electronicstl.MyClasses;

import java.sql.Blob;

/**
 *
 * @author Luca Mezzolla
 */
public class TechnicalItem {
    
    private int id = -1;
    private int technicalItemId;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTechnicalItemId() {
        return technicalItemId;
    }

    public void setTechnicalItemId(int technicalItemId) {
        this.technicalItemId = technicalItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}