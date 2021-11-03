package vo;

/**
 * @author 李中伟
 * @Date 2021/10/25
 */
public class FileVo {
    private String path;
    private String name;
    private boolean isFile = false;
    private boolean isDealFile = false;
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public boolean isDealFile() {
        return isDealFile;
    }

    public void setDealFile(boolean dealFile) {
        isDealFile = dealFile;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        if(isFile){
            s.append("文件:");
        } else {
            s.append("目录:");
        }
        s.append(path).append("/").append(name);
        return s.toString();
    }
}
