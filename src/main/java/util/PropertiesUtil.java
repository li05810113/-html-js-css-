package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author 李中伟
 * @Date 2021/10/27
 */
public class PropertiesUtil {
    public static Properties properties = null;
    static {
        properties = new Properties();
        getConfig();
    }
        public static void getConfig() {
        try {
            String confPath = System.getProperty("user.dir");
            confPath = confPath + File.separator + "my.properties";
            File propFile = new File(confPath);
            System.out.println("查找配置文件：" + confPath);
            InputStream in = null;
            if(propFile.exists()){
                in = new FileInputStream(propFile);
            } else{
                System.out.println("未查找到配置文件，使用jar包默认配置");
                in = PathUtil.class.getClassLoader().getResourceAsStream("my.properties");
            }
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
