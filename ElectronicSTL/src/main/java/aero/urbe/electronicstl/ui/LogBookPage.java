package aero.urbe.electronicstl.ui;

import aero.urbe.electronicstl.Constants.Queries;
import aero.urbe.electronicstl.MyClasses.DefectItem;
import aero.urbe.electronicstl.MyClasses.LogBookPageItem;
import aero.urbe.electronicstl.jdb;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Luca Mezzolla
 */
public class LogBookPage extends Window {

    private final VerticalLayout vl;
    private final jdb db;
    private final LogBookPageItem item;
    
    public LogBookPage(jdb db, LogBookPageItem item) {
        this.db = db;
        this.item = item;
        super.setWidth("80%");
        super.setHeight("80%");
        super.setModal(true);
        super.setCaption("Logbook Page (ID: "+String.valueOf(item.getId())+")");
        vl = new VerticalLayout();
        //prendi Periodical Tests fatti durante la sessione se ci sono
        String periodicalTests = "";
        ArrayList<Map<String, String>> testsArray = Queries.SELECT_PERIODICAL_TESTS_IN_PAGE(db, item.getId());
        if(testsArray.size() > 0) {
            for(Map<String, String> foo : testsArray) {
                periodicalTests += foo.get("name").toString()+ " " + foo.get("number") + " ("+foo.get("year")+"); ";
            }
            periodicalTests = periodicalTests.substring(0, periodicalTests.length() - 2);
        } else {
            periodicalTests = "NIL";
        }
        //prendi Difetti fatti durante la sessione se ci sono
        String defects = "";
        ArrayList<DefectItem> defectsArray = Queries.SELECT_DEFECTS_IN_PAGE(db, item.getSimulatorId(), item.getId());
        if(defectsArray.size() > 0) {
            for(DefectItem di : defectsArray) {
                defects += di.getDescription()+" ("+di.getDefectType()+"); ";
            }
            defects = defects.substring(0, defects.length() - 2);
        } else {
            defects = "NIL";
        }
        String sessionContent = !item.getSessionContent().equals("") ? item.getSessionContent() : "NIL";
        String students = !item.getStudents().equals("") ? item.getStudents() : "NIL";
        String obs = !item.getObservers().equals("") ? item.getObservers() : "NIL";
        String remarks = !item.getRemarks().equals("") ? item.getRemarks() : "NIL";
        ThemeResource themeRes = new ThemeResource("logbook_page.html?"
                + "page_id="+item.getId() //page_id!
                + "&date_page="+item.getDate()
                + "&simulator="+item.getSimulator()
                + "&sched_datetime_start="+item.getSchedStart()
                + "&sched_datetime_end="+item.getSchedEnd()
                + "&sched_datetime_total="+item.getSchedTotal()
                + "&actual_datetime_start="+item.getActualStart()
                + "&actual_datetime_end="+item.getActualEnd()
                + "&actual_datetime_total="+item.getActualTotal()
                + "&ttl_start="+item.getTTLStart()
                + "&ttl_end="+item.getTTLEnd()
                + "&ttl_total="+item.getTTLTotal()
                + "&customer="+item.getCustomer()
                + "&training_type="+item.getTrainingType()
                + "&session_content="+sessionContent
                + "&maintenance_type="+item.getMaintenanceType()
                + "&device_users="+item.getDeviceUsers()
                + "&students="+students
                + "&observers="+obs
                + "&maintenance_called="+item.getMaintenanceCalled()
                + "&training_completed="+item.getTrainingCompleted()
                + "&interruptions="+item.getInterruptions()
                + "&lost_training_time="+item.getLostTime()
                + "&device_performance="+item.getDevicePerforance()
                + "&maintenance_called="+item.getMaintenanceCalled()
                + "&periodicalTests="+periodicalTests
                + "&defects="+defects
                + "&remarks="+remarks
                + "&author="+item.getAuthor()
        );
        BrowserFrame bf = new BrowserFrame(null, themeRes); 
        bf.setSizeFull();
        vl.setSizeFull();
        vl.addComponent(bf);
        super.setContent(vl);
        super.center();
    }

}