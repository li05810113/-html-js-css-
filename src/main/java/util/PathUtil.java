package util;

import vo.FileVo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 李中伟
 * @Date 2021/10/25
 */
public class PathUtil {

    static String ignorePath = PropertiesUtil.properties.getProperty("ignore_path");

    public static List<FileVo> listFiles(String path) {
        File webPath = new File(path);
        if (webPath.isFile()) {
            FileVo fileVo = new FileVo();
            fileVo.setName(webPath.getName());
            fileVo.setPath(webPath.getPath());
            fileVo.setFile(true);
            if (webPath.getName().endsWith(".html")) {
                fileVo.setDealFile(true);
            }
            return Collections.singletonList(fileVo);
        }
        String[] paths = webPath.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.matches(ignorePath)) {
                    System.out.println("忽略文件：" + name);
                    return false;
                }
                return true;
            }
        });
        List<FileVo> fileVos = new ArrayList();
        for (String p : paths) {
            FileVo fileVo = new FileVo();
            fileVos.add(fileVo);
            String childerFile = webPath.getAbsolutePath() + File.separator + p;
            File f = new File(childerFile);
            String name = f.getName();
            fileVo.setName(name);
            fileVo.setPath(webPath.getAbsolutePath());
            if (f.isFile()) {
                fileVo.setFile(true);
                if (name.endsWith(".html")) {
                    fileVo.setDealFile(true);
                }
            } else {
                fileVos.addAll(listFiles(childerFile));
            }
        }
        return fileVos;
    }

    public static void deleteForCmd(String path){
        String delCmd = "cmd /C RD /S /Q " + path;
        System.out.println("删除文件:"+delCmd);
        try {
            Process p = Runtime.getRuntime().exec(delCmd);
            p.waitFor();
        } catch (Exception e) {
            System.out.println("删除文件失败:"+path);
            e.printStackTrace();
        }
    }
}
