/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

import java.io.File;

/**
 *
 * @author Administrator
 */

public class PathInfo {

    /**
     * @param args the command line arguments
     */
    
//    public static void main(String[] args) {
//
//        System.out.println(getPath(PathInfo.class));
//
//    }

    public static String getPath(Class theclass) {
        String dirstrString = theclass.getProtectionDomain().getCodeSource().getLocation().toString();
        String linuxString = null;
        if (File.separatorChar == '/') {
            String[] paths = dirstrString.split("/");
            String pathString = "";
            for (int i = 0; i < paths.length; i++) {
                if (i != 0 && i != paths.length - 1) {
                    pathString += paths[i] + "/";
                }
            }
            // pathString += thepathString;
            linuxString = "/" + pathString;
        } else {
             String[] paths = dirstrString.split("/");
            String pathString = "";
            for (int i = 0; i < paths.length; i++) {
                if (i != 0 && i != paths.length - 1) {
                    pathString += paths[i] + "\\";
                }
            }
            // pathString += thepathString;
            linuxString = pathString;
        }
        return linuxString;
    }

  
}
