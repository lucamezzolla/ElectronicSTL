package aero.urbe.electronicstl.MyClasses;

import java.util.Date;

/**
 *
 * @author Luca Mezzolla
 */
public class DefectItem {
    
    private int id;
    private String simulator;
    private int pageId;
    private int defectTypeId;
    private String defectType;
    private String description;
    private String correctiveAction;
    private String ataSysCode, ataSubCode, ataDescription;
    private Date datetimeStart;
    private Date datetimeEnd;
    private int solved;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDefectTypeId() {
        return defectTypeId;
    }

    public void setDefectTypeId(int defectTypeId) {
        this.defectTypeId = defectTypeId;
    }
    
    public String getSimulator() {
        return simulator;
    }

    public void setSimulator(String simulator) {
        this.simulator = simulator;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getDefectType() {
        return defectType;
    }

    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(String correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    public Date getDatetimeStart() {
        return datetimeStart;
    }

    public void setDatetimeStart(Date datetimeStart) {
        this.datetimeStart = datetimeStart;
    }

    public Date getDatetimeEnd() {
        return datetimeEnd;
    }

    public void setDatetimeEnd(Date datetimeEnd) {
        this.datetimeEnd = datetimeEnd;
    }

    public int getSolved() {
        return solved;
    }

    public void setSolved(int solved) {
        this.solved = solved;
    }

    public String getAtaSysCode() {
        return ataSysCode;
    }

    public void setAtaSysCode(String ataSysCode) {
        this.ataSysCode = ataSysCode;
    }

    public String getAtaSubCode() {
        return ataSubCode;
    }

    public void setAtaSubCode(String ataSubCode) {
        this.ataSubCode = ataSubCode;
    }

    public String getAtaDescription() {
        return ataDescription;
    }

    public void setAtaDescription(String ataDescription) {
        this.ataDescription = ataDescription;
    }
    
}