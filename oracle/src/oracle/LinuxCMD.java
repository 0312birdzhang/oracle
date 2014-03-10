/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class LinuxCMD {

    public static String CMD(String order) {
        StringBuffer sb = new StringBuffer();
        BufferedReader in = null;
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        String code = "UTF-8";
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                p = rt.exec("cmd.exe /c " + order);
                code = "GBK";
            } else {
                p = rt.exec("sh -c" + order);
            }

            in = new BufferedReader(new InputStreamReader(p.getInputStream(), code));
            String str = null;
            String[] strArray = null;
            while ((str = in.readLine()) != null) {
                if (str.trim().length() == 0) {
                    continue;
                }
                sb.append(str).append("\n");
            }
        } catch (IOException ex) {
        } finally {
            try {
                in.close();
                p.destroy();
            } catch (IOException ex) {
            }
        }
        String values = null;
        try {
            String iso = new String(sb.toString().getBytes("UTF-8"), "ISO-8859-1");
            values = new String(iso.getBytes("ISO-8859-1"), "UTF-8");
            //System.out.println(values);
        } catch (UnsupportedEncodingException ex) {
        }
        return values;
    }

    public static long getFileSize(File f)//取得文件夹大小
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }
    
    public static void  writeFile(String f,String key) throws IOException{
        BufferedWriter bw = null; 
        String outFile =PropertiesUnit.getValue(PathInfo.getPath(OracleCon.class) + "db.properties", "outfile"+key);
        File file = new File(outFile);
        if(key.equals("sql")||key.equals("plan")||key.equals("active")){
        	FileInputStream fis = new FileInputStream(file);
        	//如果是获取topsql内容，且文件大小操作1G，就重写文件
        	if((double)fis.available()/1024/1024/1024>1){
        		bw = new BufferedWriter(new FileWriter(new File(outFile),false));
        	}else{
        		bw = new BufferedWriter(new FileWriter(new File(outFile),true));
        	}
        }else{
        	bw = new BufferedWriter(new FileWriter(file,true)); 
        }
        bw.write(f);
        bw.close();
    }

    public static String FormetFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.##");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static void main(String[] args) throws Exception {

        String hh = "LOCATION=e:/archive";
        String val[] = hh.split("=");
        // System.out.println(val[1]);
        System.out.println(FormetFileSize(getFileSize(new File(val[1]))));
    }
    
    
}