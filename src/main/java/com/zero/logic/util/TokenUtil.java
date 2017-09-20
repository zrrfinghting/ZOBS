package com.zero.logic.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token的生成，验证，销毁
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/12
 */
@Configuration
public class TokenUtil {
    private static int TOKEN_EXPIRES_MINUTES =60; //token保存时间默认为60分钟
    @Autowired
    private static RedisTemplate redisTemplate;
    @Autowired
    public void setRedis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        //泛型设置成String后必须更改对应的序列化方案
        redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
    }

    /**
     * 生成token并且以<userCode,token>形式保存到redis
     * @param userCode
     * @return
     */
    public static void createAndSaveToken(String userCode){

        Properties propes = ReadProperties.getPropes("/application.properties");
        if ("".equals(propes.getProperty("TOKEN_EXPIRES_MINUTES")));
         TOKEN_EXPIRES_MINUTES = Integer.parseInt(propes.getProperty("TOKEN_EXPIRES_MINUTES"));
        String token = UUID.randomUUID().toString(); //原token
        redisTemplate.opsForValue().set(userCode, token,TOKEN_EXPIRES_MINUTES,TimeUnit.MINUTES);//将token以<key,value>的形式缓存到redis

    }

    /**
     * 根据userCode获取token
     * @param userCode
     * @return
     */
    public static String getToken(String userCode){
        String token ="";
        if (null==userCode ||"".equals(userCode)){
            return token;
        }
        if (redisTemplate.opsForValue().get(userCode)!=null){
            token = redisTemplate.opsForValue().get(userCode).toString();
        }
        return token;
    }
    /**
     * 校验token
     * @param userode
     * @param token
     * @return true/false
     */
    public static boolean checkToken(String userode,String token){
        if (null==userode){
            return false;
        }
        if (redisTemplate.opsForValue().get(userode)==null){
            return false;
        }
        if (token.equals(redisTemplate.opsForValue().get(userode).toString())){
            //redisTemplate.boundValueOps(userode).expire(TOKEN_EXPIRES_MINUTES,TimeUnit.MINUTES); //校验成功延长token过期时间
            redisTemplate.expire(userode,TOKEN_EXPIRES_MINUTES,TimeUnit.MINUTES);//校验成功延长token过期时间
            return true;
        }else {
            return false;
        }
    }

    /**
     * 根据userCode删除redis里的token
     * @param userCode
     */
    public static void deleteToken(String userCode){
        redisTemplate.delete(userCode);
    }

    /***
     * 将error与userCode拼接作为 key值，错误次数作为value值  记录用户登录错误次数
     * @param key
     */
    public static void saveErrorNum(String key){
        int errorNum=0;
        if (null!=redisTemplate.opsForValue().get(key)){
            errorNum=Integer.parseInt(redisTemplate.opsForValue().get(key).toString());
        }
        redisTemplate.opsForValue().set(key,errorNum+1);
    }
}
