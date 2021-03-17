package com.tower.variable;

public final class RedisVariable {
    /**
     * 手机号发送验证码
     **/
    public final static String SMS_PHONE = "SMS_PHONE";
    /**
     * 手机号验证码验证次数
     **/
    public final static String SMS_PHONE_LIMIT = "SMS_PHONE_LIMIT";
    /**
     * 轮播图
     **/
    public final static String SLIDESHOW_ALL = "SLIDESHOW_ALL";
    /**
     * 地址信息
     **/
    public final static String ADDR_INFO = "ADDR_INFO";
    /**
     * 浏览次数
     **/
    public final static String BROWSE = "BROWSE";
    /**
     * 点赞次数
     **/
    public final static String PRAISE = "PRAISE";
    /**
     * 玩家token
     **/
    public final static String USER_TOKEN = "USER_TOKEN";
    /**
     * 玩家信息
     **/
    public final static String USER_INFO = "USER_INFO";
    /**
     * 图片验证码token
     **/
    public final static String IMAGE_CODE_TOKEN = "IMAGE_CODE_TOKEN";

    /**
     * 保存用户点赞数据的key
     **/
    public static final String MAP_KEY_USER_LIKED = "MAP_USER_LIKED";
    /**
     * 拼接被点赞的用户id和点赞的人的id作为key。格式 222222::333333
     * @param likedUserId 被点赞的文章id
     * @param likedPostId 点赞的人的id
     * @return
     */
    public static String getLikedKey(int likedUserId, int likedPostId){
        StringBuilder builder = new StringBuilder();
        builder.append(likedUserId);
        builder.append("::");
        builder.append(likedPostId);
        return builder.toString();
    }


}
