package util;

import org.apache.commons.lang3.StringUtils;
import vo.FileVo;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author 李中伟
 * @Date 2021/10/26
 */
public class WriteUtil {

    // 按指定模式在字符串查找
    static final String pattern = "(.*)(\\.js|\\.css)+?(\\s*\")(.*)";
    // 创建 Pattern 对象
    static Pattern HTML_PATTERN = Pattern.compile(pattern);
    // 按指定模式在字符串查找
    static final String JS_HTML = "(.*)(\\.html)(\\s*)([^(]*)$";
    // 创建 Pattern 对象
    static Pattern JS_PATTERN = Pattern.compile(JS_HTML);

    public static void copyFiles(String oldPath, String newPath, List<FileVo> files, String version) {
        File newDir = new File(newPath);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        List<FileVo> dirs = files.stream().filter(x -> !x.isFile()).collect(Collectors.toList());
        for (FileVo fileVo : dirs) {
            if (!fileVo.isFile()) {
                String path = fileVo.getPath();
                if (!path.contains(oldPath)) {
                    System.out.println("文件路径异常！");
                    System.exit(1);
                }
                path = path.replace(oldPath, newPath);
                File dir = new File(path + File.separator + fileVo.getName());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
        }
        files.removeAll(dirs);
        files.stream().parallel().forEach(fileVo -> {
            String path = fileVo.getPath();
            String oldFile = path + File.separator + fileVo.getName();
            path = path.replace(oldPath, newPath);
            String newFile = path + File.separator + fileVo.getName();
            if (fileVo.isDealFile()) {
                addVersion(oldFile, newFile, version);
                return;
            }
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(oldFile));
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newFile))) {
                byte[] bs = new byte[1024];
                int l = -1;
                while ((l = in.read(bs)) != -1) {
                    out.write(bs, 0, l);
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("复制文件异常！");
                System.exit(1);
            }
        });
    }

    static void addVersion(String sourceFile, String targerFile, String version) {
        System.out.println("处理文件：" + sourceFile);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), "UTF-8"));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targerFile), "UTF-8"))) {
            String str = null;
            while ((str = reader.readLine()) != null) {
                str = str.trim();
                if (StringUtils.isBlank(str)) {
                    continue;
                }

                if(sourceFile.endsWith(".html")){

                    Matcher matcher = HTML_PATTERN.matcher(str);
                    if (matcher.find()) {
                        StringBuffer sb = new StringBuffer(matcher.group(1));
                        sb.append(matcher.group(2))
                                .append("?v=" + version)
                                .append("\"")
                                .append(matcher.group(4));
                        str = sb.toString();

                    }
                } else if(sourceFile.endsWith(".js")){
                    if(!str.startsWith("//")){
                        str = addJsHtmlVersion(str, version);
                    }
                }
                writer.write(str);
                writer.newLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("复制文件异常！");
            System.exit(1);
        }
    }

    private static String addJsHtmlVersion(String line, String version){
        if(!line.contains("/")){
            return line;
        }
        Matcher matcher = JS_PATTERN.matcher(line);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer(matcher.group(1));
            sb.append(matcher.group(2));
            String queryStr = matcher.group(4);
            if(queryStr.contains("?")){
                queryStr = queryStr.replace("?", "?v="+ version + "&");
            } else {
                queryStr =  "?v=" + version + queryStr;
            }
            sb.append(queryStr);
            line = sb.toString();
        }
        return line;
    }

    /**
     * @param zipFile   zip文件名
     * @param zipSource 待压缩文件
     * @param unzipDir  解压文件根目录
     */
    public static void zipFiles(String zipFile, String zipSource, String unzipDir) {
        ZipOutputStream zipOut = null;
        try {
            zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zip(zipOut, new File(zipSource), unzipDir);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                zipOut.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("压缩文件异常！");
            }
        }
    }

    private static void zip(ZipOutputStream zipOut, File f, String path) {
        if (f.isDirectory()) {
            try {
                //根目录创建
                zipOut.putNextEntry(new ZipEntry(path + File.separator));
                File[] files = f.listFiles();
                for (File childFile : files) {
                    zip(zipOut, childFile.getAbsoluteFile(), path + File.separator + childFile.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                zipOut.putNextEntry(new ZipEntry(path));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("压缩文件异常！");
                System.exit(1);
            }
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(f))) {
                byte[] bs = new byte[1024];
                int l = -1;
                while ((l = in.read(bs)) != -1) {
                    zipOut.write(bs, 0, l);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("压缩文件异常！");
                System.exit(1);
            }
        }
    }
}
