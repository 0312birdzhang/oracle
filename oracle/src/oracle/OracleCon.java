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

    private static Connection conn = null;//���Ӷ���
    private static Statement stmt = null;//������
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
            DriverManager.registerDriver(new OracleDriver());//ע����������
            //���������ݿ������
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
        stmt = null;//������
        rs = null;
        //  sb = new StringBuffer();
        try {
            stmt = conn.createStatement();//ͨ�����Ӷ�����������
            //String sql = PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "oraclesql.properties", key);
            String sql="Select '���ݿ�'||name||' �û� '|| username||' ���� '|| trunc(expiry_date - sysdate) ||' �ڹ��ڣ��봦��'   from dba_users,v$database";
            if (sql == null || sql.trim().equals("")) {
                return;
            }
            boolean bool = stmt.execute(sql);
            if (bool) {//�������ֵΪtrue������ζ��ִ�е��ǲ�ѯ���
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
            } else {//�������ֵΪFALSEʱ������ζ��ִ�еĲ��ǲ�ѯ���
            }
        } catch (SQLException e) {
            //  e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(OracleCon.class.getName()).log(Level.SEVERE, null, ex);
        } finally {//��β����.
            //�ر����ж���ע�⣺��õ��Ķ����ȹرգ��ȵõ��Ķ����رա�
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
