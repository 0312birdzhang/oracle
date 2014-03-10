/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleDriver;
import oracle.sql.CLOB;  

public class OracleCon implements Serializable {

    private static Connection conn = null;//连接对象
    private static Statement stmt = null;//语句对象
    private static ResultSet rs = null;
    private static StringBuffer sb = new StringBuffer("\"");
    private static String serverip;
    private static String dbport;
    private static String dbpasswd;
    private static String dbname;
    private static String dbusername;
    private static int tnsping = 1;

    public static void openConn() {
        try {
            serverip = PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "db.properties", "serverip");
            dbport = PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "db.properties", "dbport");
            dbpasswd = PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "db.properties", "dbpasswd");
            dbname = PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "db.properties", "dbname");
            dbusername = PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "db.properties", "dbusername");
            //System.out.print(PathInfo.getPath(OracleCon.class));
            DriverManager.registerDriver(new OracleDriver());//注册驱动程序
            //建立到数据库的连接
            //  System.out.println(PathInfo.getPath(OracleCon.class));
            conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@" + serverip + ":"
                    + dbport + ":"
                    + dbname, dbusername, dbpasswd);
        } catch (SQLException ex) {
            tnsping = 0;
        }
    }

    public static void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // e.printStackTrace();
            }
        }
    }

    public static void getValue(String key) {
        String order = null;
        try {
            if (conn == null || conn.isClosed()) {

                openConn();
                if (key.equalsIgnoreCase("tnsping")) {
                    System.out.println(tnsping);
                    if (tnsping == 0) {
                        return;
                    }
                }
            }
        } catch (SQLException ex) {
            //  ex.printStackTrace();
        }
        //result = new StringBuffer();
        stmt = null;//语句对象
        rs = null;
        //  sb = new StringBuffer();
        try {
            stmt = conn.createStatement();//通过连接对象获得语句对象
            //String sql = PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "oraclesql.properties", key);
            String sql="Select '数据库'||name||' 用户 '|| username||' 将在 '|| trunc(expiry_date - sysdate) ||' 内过期，请处理！'   from dba_users,v$database";
            if (sql == null || sql.trim().equals("")) {
                return;
            }
            boolean bool = stmt.execute(sql);
            if (bool) {//如果布尔值为true，就意味着执行的是查询语句
                rs = stmt.getResultSet();
                while (rs.next()) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        Object res = rs.getObject(i);
                        
                        if ((!key.equals("tbs") && !key.equals("archivesystem"))||System.getProperty("os.name").startsWith("AIX")) {
                            if (res == null) {
                                System.out.print(" " + " ");
                            } else {
                                System.out.println(res.toString() + " ");
                            }
                        }
                        if (res == null) {
                            sb.append(" ").append(" ");
                        } else {
                            sb.append(res.toString()).append(" ");
                        }

                    }
                    sb.append(" ");
                    if (!key.equals("tbs") && !key.equals("archivesystem")) {
                        System.out.println("");
                    }

                }
                if (key.equals("tbs")&&!System.getProperty("os.name").startsWith("AIX")) {
                    String send = PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "db.properties", "diramp");
                    String ampserver = PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "db.properties", "ampserver");
                    String hostName = LinuxCMD.CMD("hostname").trim();
                    StringBuffer ssb = new StringBuffer(sb.toString().trim());
                    //String ssb="/smp/sncmon/bin/zabbix_sender -z 192.168.2.9 -s localhost.localdomain -k oracle[tbs] -o \"SYSAUX,870,94.52,UNDOTBS1,110,13.86\"";
                    ssb.append("\"");
                    order = send + " -z " + ampserver + " -s " + hostName + " -k oracle[tbs] -o " + ssb.toString();
                    System.out.println(order);
                    LinuxCMD.CMD(order);
                }
                if (key.equals("archivesystem")) {
                    //System.out.println(sb.toString().trim());
                    String[] val = sb.toString().trim().split("=");
                    //  System.out.println(val[1]);
                    System.out.println(LinuxCMD.FormetFileSize(LinuxCMD.getFileSize(new File(val[1]))));
                }
            } else {//如果布尔值为FALSE时，就意味着执行的不是查询语句
            }
        } catch (SQLException e) {
            //  e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(OracleCon.class.getName()).log(Level.SEVERE, null, ex);
        } finally {//收尾工作.
            //关闭所有对象，注意：后得到的对象先关闭，先得到的对象后关闭。
            if (rs != null) {
                try {

                    rs.close();
                } catch (SQLException e) {
                    // e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    // e.printStackTrace();
                }
            }

        }
        close();
    }
}
