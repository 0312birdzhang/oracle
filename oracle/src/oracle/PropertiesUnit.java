/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

/**
 *
 * @author Administrator
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUnit {

    public static String getValue(String filename, String key) {
        File file = new File(filename);
        String value = null;
        try {
            FileInputStream in = new FileInputStream(file);
            Properties p = new Properties();
            p.load(in);
            value = p.getProperty(key);
            in.close();
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block

        //    e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block

           // e.printStackTrace();
        }
        return value;
    }
}
