package aero.urbe.electronicstl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Luca Mezzolla
 */
public class jdb {

    public static final int DBTYPE_MYSQL = 1;
    public static final int DBTYPE_MSSQL = 2;
    public static final int DBTYPE_SQLITE = 3;

    private int currenttype = 0;
    private String drivername;
    private String connstring;
    private String username;
    private String password;

    public jdb(String address, String databasename, String username, String password) throws ClassNotFoundException, SQLException {
        /*Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://"+address+"/"+databasename;
        dbcon = DriverManager.getConnection(url, username, password);*/
        currenttype = DBTYPE_MYSQL;
        makeConnection("com.mysql.jdbc.Driver", "jdbc:mysql://"+address+"/"+databasename+"?autoReconnect=true&failOverReadOnly=false", username, password);
    }

    public jdb(int dbtype, String address, String databasename, String username, String password) throws ClassNotFoundException, SQLException {
        switch (dbtype) {
            case DBTYPE_MYSQL:
                makeConnection("com.mysql.jdbc.Driver",
                               "jdbc:mysql://"+address+"/"+databasename+"?autoReconnect=true&failOverReadOnly=false",
                               username,
                               password);
                break;
            case DBTYPE_MSSQL:
                makeConnection("com.microsoft.sqlserver.jdbc.SQLServerDriver",
                               "jdbc:sqlserver://"+address+";databaseName="+databasename+";",
                               username,
                               password);
                break;
            case DBTYPE_SQLITE:
                makeConnection("org.sqlite.JDBC", "jdbc:sqlite:"+databasename, null, null);
                break;
        }
        currenttype = dbtype;
    }

    private void makeConnection(String drivername, String connstring, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName(drivername);

        this.drivername = drivername;
        this.connstring = connstring;
        this.username = username;
        this.password = password;
        
        String url = connstring;
        dbconn = DriverManager.getConnection(url, username, password);
    }

    public static String getConnectionString(int dbtype, String address, String databasename) {
        switch (dbtype) {
            case DBTYPE_MYSQL:
                return "jdbc:mysql://" + address + "/" + databasename+"?autoReconnect=true&failOverReadOnly=false";
            case DBTYPE_MSSQL:
                return "jdbc:microsoft:sqlserver://"+address+";databaseName="+databasename+";";
        }
        return "";
    }
    
    public void reconnect() throws ClassNotFoundException, SQLException {
        makeConnection(drivername, connstring, username, password);
    }

    public ResultSet query(String sqlquery) throws SQLException {
        Statement st;
        if (currenttype != DBTYPE_SQLITE)
            st = dbconn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        else
            st = dbconn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        return st.executeQuery(sqlquery);
    }

    public int update(String sqlupdate) throws SQLException {
        Statement st = dbconn.createStatement();
        return st.executeUpdate(sqlupdate);
    }
    
    public PreparedStatement prepareStatement(String statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) dbconn.prepareStatement(statement);
        return ps;
    }
    
    public PreparedStatement prepareStatement(String statement, int i) throws SQLException {
        PreparedStatement ps = (PreparedStatement) dbconn.prepareStatement(statement, i);
        return ps;
    }

    public CallableStatement prepareCall(String statement) throws SQLException {
        return dbconn.prepareCall(statement);
    }  
    
    public Connection getConnection() {
        return dbconn;
    }

    private Connection dbconn;

}