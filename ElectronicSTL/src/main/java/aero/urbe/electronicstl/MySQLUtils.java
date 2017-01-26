package aero.urbe.electronicstl;

import java.sql.ResultSet;

/***
 * 
 * @author Luca Mezzolla
 */
public class MySQLUtils {

    /**
     * Escape string to protected against SQL Injection
     *
     * You must add a single quote ' around the result of this function for data,
     * or a backtick ` around table and row identifiers. 
     * If this function returns null than the result should be changed
     * to "NULL" without any quote or backtick.
     *
     **/
    public static String mysql_real_escape_string(jdb db, String str) throws Exception {
        if(str == null) {
            return "NULL";
        }
        if(str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","").length() < 1) {
            return str;
        }
        String clean_string = str;
        clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
        clean_string = clean_string.replaceAll("\\n","\\\\n");
        clean_string = clean_string.replaceAll("\\r", "\\\\r");
        clean_string = clean_string.replaceAll("\\t", "\\\\t");
        clean_string = clean_string.replaceAll("\\00", "\\\\0");
        clean_string = clean_string.replaceAll("'", "\\\\'");
        clean_string = clean_string.replaceAll("\\\"", "\\\\\"");
        if (clean_string.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/?\\\\\"' ]","").length() < 1) {
            return clean_string;
        }
        ResultSet rs = db.query("SELECT QUOTE('"+clean_string+"')");
        rs.first();
        String r = rs.getString(1);
        return r.substring(1,r.length() - 1);       
    }

    /**
     * Escape data to protected against SQL Injection
     *
     */
    public static String quote(jdb db, String str) throws Exception {
        if (str == null) {
            return "NULL";
        }
        return "'"+mysql_real_escape_string(db, str)+"'";
    }

    /**
     * Escape identifier to protected against SQL Injection
     *
     */
    public static String nameQuote(jdb db, String str) throws Exception {
       if(str == null) {
           return "NULL";
       }
       return "`"+mysql_real_escape_string(db, str)+"`";
    }

}