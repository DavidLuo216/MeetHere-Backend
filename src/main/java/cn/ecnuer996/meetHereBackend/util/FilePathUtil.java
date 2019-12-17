package cn.ecnuer996.meetHereBackend.util;

import org.springframework.beans.factory.annotation.Value;

/**
 * 记录各种路径的信息
 * @author LuoChengLing
 */
public class FilePathUtil {

    private static String uploadFolder="/var/www/html/images/";

    public static final String URL_VENUE_COVER_PREFIX ="https://ecnuer996.cn/images/venue-images/";
    public static final String LOCAL_VENUE_COVER_PREFIX=uploadFolder+"venue-images/";
    public static final String URL_SITE_IMAGE_PREFIX ="https://ecnuer996.cn/images/site-images/";
    public static final String LOCAL_SITE_IMAGE_PREFIX=uploadFolder+"site-images/";
    public static final String URL_USER_AVATAR_PREFIX ="https://ecnuer996.cn/images/user_avatars/";
    public static final String LOCAL_USER_AVATAR_PREFIX=uploadFolder+"user_avatars/";
    public static final String URL_MANAGER_AVATAR_PREFIX ="https://ecnuer996.cn/images/user_avatars/";
    public static final String LOCAL_MANAGER_AVATAR_PREFIX=uploadFolder+"user_avatars/";

    public static void main(String[] args){
        System.out.println(FilePathUtil.LOCAL_SITE_IMAGE_PREFIX);
    }
}
