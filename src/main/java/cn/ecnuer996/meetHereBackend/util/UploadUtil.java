package cn.ecnuer996.meetHereBackend.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author LuoChengLing
 */
public class UploadUtil {

    /**
     * 更新文件（图片）
     * @param newFile 新文件的流
     * @param dest 新文件的保存路径
     * @param toDelete 要更新文件的路径
     * @return 更新文件成功则返回true
     */
    public boolean updateFile(MultipartFile newFile,String dest,String toDelete){
        File originalFile=new File(toDelete);
        if (originalFile.exists()){
            // fail to delete original file
            if(!originalFile.getAbsoluteFile().delete()){
                return false;
            }
        }
        File destFile=new File(dest);
        try{
            newFile.transferTo(destFile);
        }catch (IOException i){
            i.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 上传文件
     * @param newFile 文件流
     * @param dest 文件保存路径
     * @return
     */
    public boolean upload(MultipartFile newFile,String dest){
        File destFile=new File(dest);
        try{
            newFile.transferTo(destFile);
        }catch (IOException i){
            i.printStackTrace();
            return false;
        }
        return true;
    }
}
