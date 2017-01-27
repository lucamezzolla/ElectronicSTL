package aero.urbe.electronicstl.Constants;

import aero.urbe.electronicstl.MyClasses.DefectItem;
import aero.urbe.electronicstl.MyClasses.FileItem;
import aero.urbe.electronicstl.MyClasses.LogBookItem;
import aero.urbe.electronicstl.MyClasses.LogBookPageItem;
import aero.urbe.electronicstl.MyClasses.MyItem;
import aero.urbe.electronicstl.MyClasses.MyNotification;
import aero.urbe.electronicstl.MyClasses.SimulatorStatusItem;
import aero.urbe.electronicstl.MyClasses.TechnicalItem;
import aero.urbe.electronicstl.MyClasses.User;
import aero.urbe.electronicstl.MySQLUtils;
import aero.urbe.electronicstl.jdb;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author Luca Mezzolla
 */
public class Queries {
    
    public final static String DBNAME = "stldb_test.";
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //INSERT
    public static void INSERT_USER(jdb db, HashMap<String, String> values) throws Exception {
        String val1 = MySQLUtils.mysql_real_escape_string(db, values.get("name"));
        String val2 = MySQLUtils.mysql_real_escape_string(db, values.get("lastname"));
        String val3 = MySQLUtils.mysql_real_escape_string(db, values.get("phone"));
        String val4 = MySQLUtils.mysql_real_escape_string(db, values.get("email"));
        String val5 = MySQLUtils.mysql_real_escape_string(db, values.get("password"));
        String val6 = values.get("level");
        String query = "insert into "+DBNAME+"stl_users (name, lastname, phone, email, password, level_id) values ("
            +"'"+val1+"', '"+val2+"', '"+val3+"', '"+val4+"', md5('"+val5+"'), '"+val6+"')";
        db.update(query);
    }
    public static void INSERT_TABLE(jdb db, String table, String name) throws SQLException {
        String query = "insert into "+DBNAME+table+" (name) values ('"+name+"')";
        db.update(query);
    }
    public static void INSERT_FILE(jdb db, String name, InputStream is, int type) throws SQLException {
        String query = "insert into "+DBNAME+"stl_technical_items (technical_item_id, name, value) values (?,?,?)";
        PreparedStatement statement = db.prepareStatement(query);
        statement.setInt(1, type);
        statement.setString(2, name);
        statement.setBlob(3, is);
        statement.executeUpdate();
        statement.close();
        db.update(query);
    }
    /***
     * 
     * @param db
     * @param table
     * @param stlPage
     * @return int Last ID generated
     * @throws SQLException 
     */
    public static int INSERT_TABLE_MAP(jdb db, String table, Map<String, String> stlPage) throws SQLException, Exception {
        int id = -1;
        String query = "insert into "+DBNAME+table+" set ";
        for (Map.Entry entry : stlPage.entrySet()) {
            String val = MySQLUtils.mysql_real_escape_string(db, (String) entry.getValue());
            query += entry.getKey() + " = '" +val+"',";
        }
        query = query.substring(0, query.length() - 1);
        db.update(query);
        query = "select max(id) from "+DBNAME+table;
        ResultSet rs = db.query(query);
        if(rs.next()) {
            id = rs.getInt(1);
            rs.close();
        }
        return id;
    }
    public static void INSERT_SIMULATOR(jdb db, String name, int ttl, String ttlStart) throws Exception {
        String nameStr = MySQLUtils.mysql_real_escape_string(db, name);
        SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatDB.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        String dateStr = formatDB.format(date);
        String query1 = "insert into "+DBNAME+"stl_simulators (name, ttl, actual_ttl) values ('"+nameStr+"', '"+ttl+"', '"+ttlStart+"')";
        db.update(query1);
        ResultSet rs = db.query("select max(id) from "+DBNAME+"stl_simulators");
        if(rs.next()) {
            int simulatorId = rs.getInt(1);
            String query2 = "insert into "+DBNAME+"stl_simulator_status_values (simulator_id, simulator_status_id, datetime_start) "
                + "values ('"+simulatorId+"', '3', '"+dateStr+"')";
            db.update(query2);
            rs.close();
        }
    }
    public static void INSERT_SIMULATOR_STATUS(jdb db, int simulatorId, int statusId) {
        try {
            SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatDB.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = new Date();
            String dateStr = formatDB.format(date);
            String query = "insert into "+DBNAME+"stl_simulator_status_values (simulator_id, simulator_status_id, datetime_start) "
                    + "values ('"+simulatorId+"', '"+statusId+"', '"+dateStr+"')";
            db.update(query);
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //SELECT
    public static boolean CHECK_ALL_DB_ITEM(jdb db) {
        ArrayList<String> tables = new ArrayList<>();
        tables.add("stl_levels");
        tables.add("stl_defect_type");
        tables.add("stl_training_type");
        tables.add("stl_maintenance_type");
        tables.add("stl_device_performance");
        tables.add("stl_customers");
        tables.add("stl_simulators");
        tables.add("stl_periodical_test");
        for(String table : tables) {
            try {
                ResultSet rs = db.query("select * from "+DBNAME+table);
                if(rs.next()) {
                    rs.close();
                } else {
                    return false; 
                }
            } catch (SQLException ex) {
                return false;
            }
        }
        return true;
    }
    
    public static User SELECT_LOGIN(jdb db, String email, String password) {
        User user = new User();
        try {
            String query = "select id, name, lastname, phone, email, password, level_id, "
                    + "(select name from "+DBNAME+"stl_levels where id = level_id) as levelName from "+DBNAME+"stl_users "
                    + "where email = '"+email+"' and password = md5('"+password+"')";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                user.setId(rs.getInt(1));
                user.setName(rs.getString("name"));
                user.setLastname(rs.getString("lastname"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setLevelId(rs.getInt("level_id"));
                user.setLevelName(rs.getString("levelName"));
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return user;
    }
    
    public static SimulatorStatusItem SELECT_LAST_STATUS_SIMULATOR(jdb db, int simulatorId) {
        SimulatorStatusItem item = new SimulatorStatusItem();
        try {
            String query = "select id, simulator_id as simulatorId, simulator_status_id as simulatorStatusId, "
                    + "(select name from "+DBNAME+"stl_simulators where id = simulatorId) as simulator, "
                    + "(select name from "+DBNAME+"stl_simulator_status where id = simulatorStatusId) as simulatorStatus "
                    + "from "+DBNAME+"stl_simulator_status_values where simulator_id = '"+simulatorId+"' order by id desc limit 0, 1";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                item.setId(rs.getInt(1));
                item.setSimulatorId(rs.getInt(2));
                item.setSimulatorStatusId(rs.getInt(3));
                item.setSimulator(rs.getString(4));
                item.setSimulatorStatus(rs.getString(5));
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return item;
    }
    
    
    public static void SELECT_USERS(jdb db, String text, Table table, int myId) {
        try {
            String query = "select id, name, lastname, phone, email, password, level_id, "
                    + "(select name from "+DBNAME+"stl_levels where id = level_id) as levelName from "+DBNAME+"stl_users "
                    + "where id != '"+myId+"' and (name like '"+text+"%' or lastname like '"+text+"%') order by name, lastname";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                table.removeAllItems();
                rs.beforeFirst();
                while(rs.next()) {
                    Object foo[] = { rs.getString("name"), rs.getString("lastname"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("levelName") };
                    table.addItem(foo, rs.getInt(1));
                }
                rs.close();
                table.setVisible(true);
            } else {
                table.setVisible(false);
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
    }
    public static ArrayList<Object> SELECT_USER(jdb db, Integer id) {
        ArrayList<Object> array = new ArrayList<>();
        try {
            String query = "select id, name, lastname, phone, email, password, level_id, "
                    + "(select name from "+DBNAME+"stl_levels where id = level_id) as levelName from "+DBNAME+"stl_users "
                    + "where id = '"+id+"'";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                array.add(rs.getString("name"));
                array.add(rs.getString("lastname"));
                array.add(rs.getString("phone"));
                array.add(rs.getString("email"));
                array.add(rs.getInt("level_id"));
                array.add(rs.getString("levelName"));
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    public static ArrayList<DefectItem> SELECT_OPEN_DEFECT(jdb db, int simulatorId ) {
        ArrayList<DefectItem> array = new ArrayList<>();
        try {
            String query = "select id, page_id, defect_type_id, "
                    + "(select name from "+DBNAME+"stl_simulators where id = simulator_id) as simulator, "
                    + "(select name from "+DBNAME+"stl_defect_type where id = defect_type_id) as defect, "
                    + "description, ata_syscode, ata_subcode, ata_description, corrective_action, datetime_start, datetime_end, solved "
                    + "from "+DBNAME+"stl_defects where simulator_id = '"+simulatorId+"' and solved = '0' order by datetime_start desc";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    DefectItem di = new DefectItem();
                    di.setId(rs.getInt("id"));
                    di.setSimulator(rs.getString("simulator"));
                    di.setPageId(rs.getInt("page_id"));
                    di.setDefectTypeId(rs.getInt("defect_type_id"));
                    di.setDefectType(rs.getString("defect"));
                    di.setDescription(rs.getString("description"));
                    di.setDatetimeStart(rs.getTimestamp("datetime_start"));
                    di.setSolved(rs.getInt("solved"));
                    array.add(di);
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    public static FileItem SELECT_FILE(jdb db, TechnicalItem technicalItem) {
        FileItem item = null;
        try {
            String query = "select name, value from "+DBNAME+"stl_technical_items where id = "+technicalItem.getId();
            ResultSet rs = db.query(query);
            if(rs.next()) {
                item = new FileItem();
                item.setName(rs.getString(1));
                item.setIs(rs.getBinaryStream(2));
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return item;
    }
    public static ArrayList<TechnicalItem> SELECT_TECHNICAL_ITEMS(jdb db, int techId) {
        ArrayList<TechnicalItem> array = new ArrayList<>();
        try {
            String query = "select id, technical_item_id, name, value from "+DBNAME+"stl_technical_items where technical_item_id = '"+techId+"'";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    TechnicalItem foo = new TechnicalItem();
                    foo.setId(rs.getInt(1));
                    foo.setTechnicalItemId(rs.getInt(2));
                    foo.setName(rs.getString(3));                 
                    foo.setBlob(rs.getBlob(4));
                    array.add(foo);
                }
                rs.close();
            }
            return array;
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    
    public static LogBookPageItem SELECT_PAGE(jdb db, int id) throws ParseException {
        LogBookPageItem item = new LogBookPageItem();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy (HH:mm)");
        SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String query = "select id, date_page, (select concat(name,' ',lastname) from "+DBNAME+"stl_users where id = user_id) as author, "
                    + "(select name from "+DBNAME+"stl_simulators where id = simulator_id) as simulator, simulator_id, "
                    + "sched_datetime_start, sched_datetime_end, sched_datetime_total, "
                    + "actual_datetime_start, actual_datetime_end, actual_datetime_total, ttl_start, ttl_end, ttl_total, training_type_id, "
                    + "(select name from "+DBNAME+"stl_customers where id = customer_id) as customer, "
                    + "(select name from "+DBNAME+"stl_training_type where id = training_type_id) as training_type, "
                    + "(select name from "+DBNAME+"stl_maintenance_type where id = maintenance_type_id) as maintenance_type, "
                    + "(select name from "+DBNAME+"stl_device_performance where id = device_performance_id) as device_performance, "
                    + "session_content, device_users, students, observers,"
                    + "maintenance_called, training_completed, interruptions, lost_training_time, remarks "
                    + "from "+DBNAME+"stl_pages "
                    + "where id = '"+id+"' "
                    + "order by date_page desc";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                String[] devusers = rs.getString("device_users").split("###");
                String[] students = rs.getString("students").split("###");
                String[] obs = rs.getString("observers").split("###");
                String devStr = ""; String stuStr = ""; String obsStr = "";
                for(int i = 0; i < devusers.length; i++) {
                    devStr += devusers[i].equals("") ? "" : devusers[i]+", ";
                }
                for(int i = 0; i < students.length; i++) {
                    stuStr += students[i].equals("") ? "" : students[i]+", ";
                }
                for(int i = 0; i < obs.length; i++) {
                    obsStr += obs[i].equals("") ? "" : obs[i]+", ";
                }
                devStr = devStr.length() > 0 ? devStr.substring(0, devStr.length() - 2) : devStr;
                stuStr = stuStr.length() > 0 ? stuStr.substring(0, stuStr.length() - 2) : stuStr;
                obsStr = obsStr.length() > 0 ? obsStr.substring(0, obsStr.length() - 2) : obsStr;
                String maintenance_type = "";
                if(rs.getInt("training_type_id") != 4) {
                    maintenance_type = rs.getString("maintenance_type");
                }
                item.setId(rs.getInt("id"));
                item.setDate(format.format(rs.getTimestamp("date_page")).toUpperCase());
                item.setSimulatorId(rs.getInt("simulator_id"));
                item.setSimulator(rs.getString("simulator").toUpperCase());
                item.setTTLStart(rs.getString("ttl_start"));
                item.setTTLEnd(rs.getString("ttl_end"));
                item.setTTLTotal(rs.getString("ttl_total"));
                item.setSchedStart(format.format(formatDB.parse(rs.getString("sched_datetime_start"))).toUpperCase());
                item.setSchedEnd(format.format(formatDB.parse(rs.getString("sched_datetime_end"))).toUpperCase());
                item.setSchedTotal(rs.getString("sched_datetime_total").toUpperCase());
                item.setActualStart(format.format(formatDB.parse(rs.getString("actual_datetime_start"))).toUpperCase());
                item.setActualEnd(format.format(formatDB.parse(rs.getString("actual_datetime_end"))).toUpperCase());
                item.setActualTotal(rs.getString("actual_datetime_total").toUpperCase());
                item.setCustomer(rs.getString("customer").toUpperCase());
                item.setTrainingType(rs.getString("training_type").toUpperCase());
                item.setSessionContent(rs.getString("session_content").toUpperCase());
                item.setMaintenanceType(maintenance_type.toUpperCase());
                item.setDeviceUsers(devStr.toUpperCase());
                item.setStudents(stuStr.toUpperCase());
                item.setObservers(obsStr.toUpperCase());
                item.setMaintenanceCalled(rs.getInt("maintenance_called") == 1 ? "YES" : "NO");
                item.setTrainingCompleted(rs.getInt("training_completed") == 1 ? "YES" : "NO");
                item.setInterruptions(String.valueOf(rs.getInt("interruptions")));
                item.setLostTime(String.valueOf(rs.getInt("lost_training_time")));
                item.setDevicePerforance(rs.getString("device_performance").toUpperCase());
                item.setRemarks(rs.getString("remarks").toUpperCase());
                item.setAuthor(rs.getString("author").toUpperCase());
                rs.close();
            }
        } catch(SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return item;
    }
    public static ArrayList<LogBookItem> SELECT_PAGES(jdb db, int simulatorId, String from, String to) throws ParseException {//, int simulatorId ) {
        ArrayList<LogBookItem> array = new ArrayList<>();
        SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy (HH:mm)");
        try {
            String query = "select id, date_page, "
                    + "actual_datetime_start, actual_datetime_end, actual_datetime_total, "
                    + "device_users, students, (select concat(name,' ', lastname) from "+DBNAME+"stl_users where id = user_id) as author "
                    + "from "+DBNAME+"stl_pages "
                    + "where (date_page between '"+from+"' and '"+to+"') and (simulator_id = '"+simulatorId+"') "
                    + "order by date_page desc";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    Date actualStart = formatDB.parse(rs.getString(3));
                    Date actualEnd = formatDB.parse(rs.getString(4));
                    String[] devusers = rs.getString(6).split("###");
                    String[] students = rs.getString(7).split("###");
                    String devStr = ""; String stuStr = "";
                    for(int i = 0; i < devusers.length; i++) {
                        devStr += devusers[i].equals("") ? "" : devusers[i]+", ";
                    }
                    for(int i = 0; i < students.length; i++) {
                        stuStr += students[i].equals("") ? "" : students[i]+", ";
                    }
                    devStr = devStr.length() > 0 ? devStr.substring(0, devStr.length() - 2) : devStr;
                    stuStr = stuStr.length() > 0 ? stuStr.substring(0, stuStr.length() - 2) : stuStr;
                    LogBookItem item = new LogBookItem();
                    item.setId(rs.getInt(1));
                    item.setDatePage(format.format(rs.getTimestamp(2)));
                    item.setActualStart(format.format(actualStart));
                    item.setActualEnd(format.format(actualEnd));
                    item.setActualTotal(rs.getString(5));
                    item.setDeviceUsers(devStr);
                    item.setStudents(stuStr);
                    item.setAuthor(rs.getString(8));
                    array.add(item);
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    
    public static ArrayList<Map<String, String>> SELECT_PERIODICAL_TESTS_IN_PAGE(jdb db, int pageId) {
        ArrayList<Map<String, String>> array = new ArrayList<>();
        try {
            String query = "select "
                    + "(select name from "+DBNAME+"stl_periodical_test where id = periodical_test_id) as periodical_test, "
                    + "number, year from "+DBNAME+"stl_periodical_test_values where page_id = '"+pageId+"' order by id";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    Map<String, String> ar = new HashMap<>();
                    ar.put("name", rs.getString(1)); //name of the periodical test
                    ar.put("number", rs.getString(2)); //number of the periodical test
                    ar.put("year", rs.getString(3)); //year of the periodical test
                    array.add(ar);
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    
    public static ArrayList<DefectItem> SELECT_DEFECTS_IN_PAGE(jdb db, int simulatorId, int pageId) {
        ArrayList<DefectItem> array = new ArrayList<>();
        try {
            String query = "select id, page_id, defect_type_id, "
                    + "(select name from "+DBNAME+"stl_simulators where id = simulator_id) as simulator, "
                    + "(select name from "+DBNAME+"stl_defect_type where id = defect_type_id) as defect, "
                    + "description, ata_syscode, ata_subcode, ata_description, corrective_action, datetime_start, datetime_end, solved "
                    + "from "+DBNAME+"stl_defects where simulator_id = '"+simulatorId+"' and page_id = '"+pageId+"' order by datetime_start desc";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    DefectItem di = new DefectItem();
                    di.setId(rs.getInt("id"));
                    di.setSimulator(rs.getString("simulator"));
                    di.setPageId(rs.getInt("page_id"));
                    di.setDefectTypeId(rs.getInt("defect_type_id"));
                    di.setDefectType(rs.getString("defect"));
                    di.setDescription(rs.getString("description"));
                    di.setAtaSysCode(String.valueOf(rs.getInt("ata_syscode")));
                    di.setAtaSubCode(String.valueOf(rs.getInt("ata_subcode")));
                    di.setAtaDescription(rs.getString("ata_description"));
                    di.setCorrectiveAction(rs.getString("corrective_action"));
                    di.setDatetimeStart(rs.getTimestamp("datetime_start"));
                    di.setDatetimeEnd(rs.getTimestamp("datetime_end"));
                    di.setSolved(rs.getInt("solved"));
                    array.add(di);
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    
    public static ArrayList<DefectItem> SELECT_DEFECTS(jdb db, int simulatorId) {
        ArrayList<DefectItem> array = new ArrayList<>();
        try {
            String query = "select id, page_id, defect_type_id, "
                    + "(select name from "+DBNAME+"stl_simulators where id = simulator_id) as simulator, "
                    + "(select name from "+DBNAME+"stl_defect_type where id = defect_type_id) as defect, "
                    + "description, ata_syscode, ata_subcode, ata_description, corrective_action, datetime_start, datetime_end, solved "
                    + "from "+DBNAME+"stl_defects where simulator_id = '"+simulatorId+"' order by datetime_start desc";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    DefectItem di = new DefectItem();
                    di.setId(rs.getInt("id"));
                    di.setSimulator(rs.getString("simulator"));
                    di.setPageId(rs.getInt("page_id"));
                    di.setDefectTypeId(rs.getInt("defect_type_id"));
                    di.setDefectType(rs.getString("defect"));
                    di.setDescription(rs.getString("description"));
                    di.setAtaSysCode(String.valueOf(rs.getInt("ata_syscode")));
                    di.setAtaSubCode(String.valueOf(rs.getInt("ata_subcode")));
                    di.setAtaDescription(rs.getString("ata_description"));
                    di.setCorrectiveAction(rs.getString("corrective_action"));
                    di.setDatetimeStart(rs.getTimestamp("datetime_start"));
                    di.setDatetimeEnd(rs.getTimestamp("datetime_end"));
                    di.setSolved(rs.getInt("solved"));
                    array.add(di);
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    
    public static ArrayList<MyItem> SELECT_TABLE(jdb db, String table) {
        ArrayList<MyItem> array = new ArrayList<>();
        try {
            String query = "select id, name from "+DBNAME+table+" order by id";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    array.add(new MyItem(rs.getInt("id"), rs.getString("name")));
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    public static ArrayList<MyItem> SELECT_SIMULATORS1(jdb db) {
        ArrayList<MyItem> array = new ArrayList<>();
        try {
            String query = "select id, name, ttl, actual_ttl from "+DBNAME+"stl_simulators";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    MyItem item = new MyItem(rs.getInt("id"), rs.getString("name"));
                    array.add(item);
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    public static ArrayList<Map<String, String>> SELECT_SIMULATORS2(jdb db) {
        ArrayList<Map<String, String>> array = new ArrayList<>();
        try {
            String query = "select id, name, ttl, actual_ttl from "+DBNAME+"stl_simulators";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    Map<String, String> ar = new HashMap<>();
                    ar.put("id", String.valueOf(rs.getInt("id")));
                    ar.put("name", rs.getString("name"));
                    ar.put("actual_ttl", rs.getString("actual_ttl"));
                    array.add(ar);
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    public static int SELECT_COUNT_SESSION(jdb db, String from, String to, Integer simulatorId) {
        int count = -1;
        try {
            String query = "select count(id) from "+DBNAME+"stl_pages where simulator_id = 1 and date_page between '"+from+"' and '"+to+"'";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                count = rs.getInt(1);    
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return count;
    }
    public static ArrayList<Object> SELECT_SIMULATOR(jdb db, Integer id) {
        ArrayList<Object> array = new ArrayList<>();
        try {
            String query = "select id, name, ttl, actual_ttl from "+DBNAME+"stl_simulators where id = '"+id+"'";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                array.add(rs.getInt("id"));                 //INTEGER 
                array.add(rs.getString("name"));            //STRING    
                array.add(rs.getInt("ttl"));                //TINYINY
                array.add(rs.getString("actual_ttl"));      //STRING              
                rs.close();
            }
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    public final static String SELECT_STL_LEVELS = "select id, name from "+DBNAME+"stl_levels where id < 5 order by id";
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //UPDATE
    public static void UPDATE_SIMULATOR_TTL(jdb db, String value, Integer id) throws Exception {
        String valueStr = MySQLUtils.mysql_real_escape_string(db, value);
        try {
            String query = "update "+DBNAME+"stl_simulators set actual_ttl = '"+valueStr+"' where id = '"+id+"'";
            db.update(query);
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
    }
    public static void UPDATE_USER(jdb db, HashMap<String, String> values, Integer id) throws Exception {
        String phone = MySQLUtils.mysql_real_escape_string(db, (String)values.get("phone"));
        try {
            String query = "update "+DBNAME+"stl_users "
                    + "set phone = '"+phone+"', level_id = '"+values.get("level")+"' "
                    + "where id = '"+id+"'";
            db.update(query);
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
    }
    public static void UPDATE_PASSWORD(jdb db, String password, Integer id) throws Exception {
        String passStr = MySQLUtils.mysql_real_escape_string(db, password);
        try {
            String query = "update "+DBNAME+"stl_users set password = md5('"+passStr+"') where id = '"+id+"'";
            db.update(query);
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
    }
    public static void UPDATE_PASSWORD(jdb db, String password, String email) throws Exception {
        String passStr = MySQLUtils.mysql_real_escape_string(db, password);
        try {
            String query = "update "+DBNAME+"stl_users set password = md5('"+passStr+"') where email = '"+email+"'";
            db.update(query);
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
    }
    public static void UPDATE_DEFECT(jdb db, int id, int defectTypeId, String sysCode, String subCode, String ataDesc, String correction, String dateEndStr, int solvedValue) throws Exception {
        String sysCodeStr = MySQLUtils.mysql_real_escape_string(db, sysCode);
        String subCodeStr = MySQLUtils.mysql_real_escape_string(db, subCode);
        String ataDescStr = MySQLUtils.mysql_real_escape_string(db, ataDesc);
        String correctionStr = MySQLUtils.mysql_real_escape_string(db, correction);
        try {
            String query = "update "+DBNAME+"stl_defects "
                + "set defect_type_id = '"+defectTypeId+"', "
                + "ata_syscode = '"+sysCodeStr+"', "
                + "ata_subcode = '"+subCodeStr+"', "
                + "ata_description = '"+ataDescStr+"', "
                + "corrective_action = '"+correctionStr+"', ";
            if(solvedValue == 1) {
                query += "datetime_end = '"+dateEndStr+"', ";
            } else {
                query += "datetime_end = NULL, ";
            }
            query += "solved = '"+solvedValue+"' where id = '"+id+"'";
            db.update(query);
            MyNotification.SHOW(Messages.SUCCESS, Messages.DB_UPDATED, Type.HUMANIZED_MESSAGE, 3000);
        } catch (SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //STATISTICS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //STAT1: Total Sessions
    public static Map<String, String> STAT1(jdb db, String start, String end) {
        Map<String, String> array = new HashMap<>();
        ArrayList<Integer> simIds = new ArrayList<>();
        try {
            String query = "select id from "+DBNAME+"stl_simulators order by id";
            ResultSet rs = db.query(query);
            if(rs.next()) {
                rs.beforeFirst();
                while(rs.next()) {
                    simIds.add(rs.getInt(1));
                }
                rs.close();
            }
            if(simIds.size() > 0) {
                Iterator iterator = simIds.iterator();
                while(iterator.hasNext()) {
                    Integer id = (Integer)iterator.next();
                    query = "select count(id) from "+DBNAME+"stl_pages "
                            + "where (simulator_id = "+id+") and (date_page between '"+start+"' and '"+end+"')";
                    rs = db.query(query);
                    if(rs.next()) {
                        rs.beforeFirst();
                        while(rs.next()) {
                            //inserisce come stringhe il simulator id e il conteggio delle sessione
                            array.put(String.valueOf(id), String.valueOf(rs.getInt(1)));
                        }
                        rs.close();
                    }
                }
            }
        } catch(SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    //STAT2: Total Sessions
    public static ArrayList<Map<String, String>> STAT2(jdb db, String start, String end, int simulatorId) {
        ArrayList<Map<String, String>> array = new ArrayList<>();
        try {
            for(int i = 1; i <= 9; i++) {
                String query = "SELECT count(training_type_id) FROM "+DBNAME+"stl_pages "
                        + "where (simulator_id = '"+simulatorId+"') and (training_type_id = '"+i+"') and (date_page between '"+start+"' and '"+end+"')";
                ResultSet rs = db.query(query);
                if(rs.next()) {
                    rs.beforeFirst();
                    while(rs.next()) {
                        Map<String, String> ar = new HashMap<>();
                        ar.put("val"+i, String.valueOf(rs.getInt(1)));
                        array.add(ar);
                    }
                    rs.close();
                } else {
                    Map<String, String> ar = new HashMap<>();
                    ar.put("val"+i, "0");
                    array.add(ar);
                }
            }
        } catch(SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    //STAT5: Defects
    public static ArrayList<Map<String, String>> STAT5(jdb db, String start, String end, int simulatorId) {
        ArrayList<Map<String, String>> array = new ArrayList<>();
        try {
            for(int i = 1; i <= 3; i++) {
                String query = "SELECT count(defect_type_id) FROM "+DBNAME+"stl_defects "
                        + "where (simulator_id = '"+simulatorId+"' and defect_type_id = '"+i+"') and (datetime_start between '"+start+"' and '"+end+"')";
                ResultSet rs = db.query(query);
                if(rs.next()) {
                    rs.beforeFirst();
                    while(rs.next()) {
                        Map<String, String> ar = new HashMap<>();
                        ar.put("val"+i, String.valueOf(rs.getInt(1)));
                        array.add(ar);
                    }
                    rs.close();
                } else {
                    Map<String, String> ar = new HashMap<>();
                    ar.put("val"+i, "0");
                    array.add(ar);
                }
            }
        } catch(SQLException ex) {
            Notification.show(Messages.ERROR, Messages.ERROR_GENERIC, Type.ERROR_MESSAGE);
        }
        return array;
    }
    
}