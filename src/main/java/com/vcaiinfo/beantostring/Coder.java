package com.vcaiinfo.beantostring;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import org.apache.commons.io.IOUtils;

/**
 * Created by vcaiTech on 2017/6/29.
 */
public class Coder {

    public static void main(String[] args) {
        //step1 读取指定目录下的文件
        String path = getProjectDirectory() + "com/vcaiinfo/beantostring";
        System.out.println("path = " + path);
        File directory = new File(path);
        if (directory.exists()&& directory.isDirectory()){
            File[] beans = directory.listFiles();
            for(File bean:beans)
                if (!bean.isHidden()) {
                    String beanName = bean.getName().replace(".class", "");
                    try {
                        if (!beanName.equalsIgnoreCase("Coder")) {
                            Class clazz = Class.forName("com.vcaiinfo.beantostring." + beanName);//根据类名获得其对应的Class对象 写上你想要的类名就是了 注意是全名 如果有包的话要加上 比如java.Lang.String
                            Field[] fields = clazz.getDeclaredFields();//根据Class对象获得属性 私有的也可以获得
                            String head = "class " + clazz.getSimpleName() + ":BaseItem{";
                            for (Field f : fields) {
                                head += "\n";
                                head += ("    var " + f.getName() + ":" + getSwiftClassType(f.getType().getSimpleName()));
                                head += "\n";
                            }
                            head += "}";
                            saveClassString(clazz.getSimpleName(), head);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }

    }

    private static void saveClassString(String className,String classBody){
        String filePath = "/Users/vcaiTech/BeanToString/target/" + "Beans/"+className+ ".swift";
        File sFile = new  File(filePath);
        if(sFile.exists()) sFile.delete();
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"));
            IOUtils.write(classBody, writer);
            IOUtils.write( IOUtils.LINE_SEPARATOR, writer);
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSwiftClassType(String javaType){
        switch (javaType) {
            case "String":
                return "String?";
            default:
                return "Any?";
        }
    }

    /*
    *  获取项目jar所处的文件路径
    */
    private static String getProjectDirectory(){
        URL url = Coder.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null ;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath != null && filePath.endsWith(".jar"))
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        return filePath;
    }
}
