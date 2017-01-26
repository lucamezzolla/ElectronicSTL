package aero.urbe.electronicstl.MyClasses;

/**
 *
 * @author Luca Mezzolla
 */
public class LogBookItem {
    
    private int id;
    private String datePage;
    private String actualStart;
    private String actualEnd;
    private String actualTotal;
    private String deviceUsers;
    private String students;
    private String author;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDatePage() {
        return datePage;
    }

    public void setDatePage(String datePage) {
        this.datePage = datePage;
    }

    public String getActualStart() {
        return actualStart;
    }

    public void setActualStart(String actualStart) {
        this.actualStart = actualStart;
    }

    public String getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(String actualEnd) {
        this.actualEnd = actualEnd;
    }

    public String getActualTotal() {
        return actualTotal;
    }

    public void setActualTotal(String actualTotal) {
        this.actualTotal = actualTotal;
    }

    public String getDeviceUsers() {
        return deviceUsers;
    }

    public void setDeviceUsers(String deviceUsers) {
        this.deviceUsers = deviceUsers;
    }

    public String getStudents() {
        return students;
    }

    public void setStudents(String students) {
        this.students = students;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    
}
