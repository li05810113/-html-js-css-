import util.PathUtil;
import util.PropertiesUtil;
import util.WriteUtil;
import vo.FileVo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void main(String[] args) {
        PropertiesUtil.properties.list(System.out);
        String basePath = PropertiesUtil.properties.getProperty("base_path");
//        basePath = "F:\\project\\hsv2_web";
        String path = basePath;
        path = path.replace("/", File.separator);
        File webPath = new File(path);
        if (webPath.exists()) {
            System.out.println("当前工程路径：" + webPath.getAbsolutePath());
        } else {
            System.out.println("当前工程路径不存在：" + webPath.getAbsolutePath());
        }
        try {
            //获取工程绝对路径
            webPath = webPath.getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String version = dateFormat.format(new Date());
        //临时文件 如：C:\hs_web20211011101000
        String tmpVersionDir = webPath.getAbsolutePath() + version;
        System.out.println("创建临时目录:" + tmpVersionDir);
        File tmpDir = new File(tmpVersionDir);
        tmpDir.mkdir();
        List<FileVo> fileVos = PathUtil.listFiles(path);
        WriteUtil.copyFiles(webPath.getAbsolutePath(), tmpVersionDir, fileVos, version);
        System.out.println("生成压缩文件:" + tmpDir + ".zip");
        WriteUtil.zipFiles(tmpDir + ".zip", tmpVersionDir, webPath.getName());
        System.out.println("删除临时目录:" + tmpDir);
        PathUtil.deleteForCmd(tmpVersionDir);

    }


}
