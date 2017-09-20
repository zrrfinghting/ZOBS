package com.zero.logic.controller;

import com.zero.logic.dao.UserDao;
import com.zero.logic.domain.User;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.ReadProperties;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.security.auth.message.MessageInfo;
import java.io.IOException;
import java.security.Security;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 发送验证码类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/22
 */
@RestController
@RequestMapping("email")
public class SendVerifyCodeController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisTemplate redisTemplate;
    //未加密的邮件使用的是25端口  阿里云服务器的25端口被禁掉了  所以使用下面的ssl邮件发送方式  他使用的是465端口
  /*  @RequestMapping(value = "/sendVerifyCode",method = RequestMethod.GET)
    @ApiOperation(value = "生成验证码",notes = "随机生成验证码发送到邮箱")
    public String sendVerifyCode222(@RequestParam String userCode,@RequestParam String emailAddress){
        try {
            //获取验证码有效时间
            int CAPTCHA_EXPIRES_MINUTES=5;//验证码过期时间 默认五分钟，//如果配置文件有设置则使用配置文件的
            if (!"".equals(ReadProperties.getProperties().getProperty("CAPTCHA_EXPIRES_MINUTES")))
            CAPTCHA_EXPIRES_MINUTES= Integer.parseInt(ReadProperties.getProperties().getProperty("CAPTCHA_EXPIRES_MINUTES"));
            //获取邮件模板配置信息
            Properties properties = ReadProperties.getPropes("/application.properties");
            String fromAddr = properties.getProperty("message.setFrom");//发件地址
            String verifyCode = String.valueOf((int)((Math.random()*9+1)*1000));//生成随机四位验证码
            SimpleMailMessage message = new SimpleMailMessage();
            //用户密码找回
            if ("".equals(emailAddress )|| emailAddress.length()<1){
                User oldUser = userDao.getUserByUserCode(userCode);
               // Timestamp outDate = new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000);// 5分钟后过期
                //oldUser.setOutDate(outDate);
                //oldUser.setVerifyCode(verifyCode);

                //使用redis缓存以<key,value>形式存储 用户-验证码 信息
                redisTemplate.opsForValue().set(userCode, verifyCode,CAPTCHA_EXPIRES_MINUTES,TimeUnit.MINUTES);

                emailAddress = oldUser.getEmail();
                message.setSubject("主题：密码重置");
                message.setText("您的验证码是 "+verifyCode+"请勿泄露给他人");

                userDao.save(oldUser);

            }else {//用户注册校验邮箱
                //使用redis缓存以<key,value>形式存储 用户-验证码 信息
                redisTemplate.opsForValue().set(userCode, verifyCode,CAPTCHA_EXPIRES_MINUTES,TimeUnit.MINUTES);
                message.setSubject("主题：用户注册");
                message.setText("您的验证码是 "+verifyCode+"请勿泄露给他人");
            }

            Map map = new HashMap();
            map.put("verifyCode",verifyCode);
            message.setFrom(fromAddr);//发件箱
            message.setTo(emailAddress);//收件箱
            mailSender.send(message);
            String str = JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"验证码发送成功，注意查收邮件");
            return JsonUtil.makeJsonBeanByKey(str,map);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"发送失败");
        }
    }*/


    @RequestMapping(value = "/sendVerifyCode",method = RequestMethod.GET)
    @ApiOperation(value = "生成验证码",notes = "随机生成验证码发送到邮箱")
    public String sendVerifyCode(@RequestParam String userCode,@RequestParam String emailAddress) throws AddressException, MessagingException, IOException {
        try {

            String verifyCode = String.valueOf((int)((Math.random()*9+1)*1000));//生成随机四位验证码
            int CAPTCHA_EXPIRES_MINUTES=5;//验证码过期时间默认五分钟
            if (!"".equals(ReadProperties.getProperties().getProperty("CAPTCHA_EXPIRES_MINUTES")))
                CAPTCHA_EXPIRES_MINUTES= Integer.parseInt(ReadProperties.getProperties().getProperty("CAPTCHA_EXPIRES_MINUTES"));

            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

            //从配置文件读取邮件用户名和密码，服务器主机地址 端口
            Properties properties = ReadProperties.getPropes("/application.properties");
            final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            final String username = properties.getProperty("spring.mail.username");//用户名
            final String password = properties.getProperty("spring.mail.password");//密码
            String fromAddr = properties.getProperty("message.setFrom");//发件地址
            Address toAddr = null;  // 收件地址

            // Get a Properties object
            Properties props = new Properties();
            props.setProperty("mail.smtp.host",properties.getProperty("spring.mail.host"));//邮件服务器地址
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", properties.getProperty("spring.mail.port"));//服务器端口
            props.setProperty("mail.smtp.socketFactory.port",properties.getProperty("spring.mail.socketFactory.port"));
            props.put("mail.smtp.auth", properties.getProperty("spring.mail.auth"));

            Session session = Session.getDefaultInstance(props, new Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }});

            Message msg = new MimeMessage(session);

            if ("".equals(emailAddress) || emailAddress.length()<1){//用户密码找回,使用用户之前注册的邮箱作为收件箱
                User oldUser = userDao.getUserByUserCode(userCode);
                redisTemplate.opsForValue().set(userCode, verifyCode,CAPTCHA_EXPIRES_MINUTES,TimeUnit.MINUTES);//使用redis缓存以<key,value>形式存储 用户-验证码 信息
                msg.setSubject("主题：密码重置");
                emailAddress = oldUser.getEmail();
                //toAddr =  new InternetAddress(emailAddress); // 收件地址
            }else {
                //用户注册校验邮箱
                //使用redis缓存以<key,value>形式存储 用户-验证码 信息
                redisTemplate.opsForValue().set(userCode, verifyCode,CAPTCHA_EXPIRES_MINUTES,TimeUnit.MINUTES);
                msg.setSubject("主题：用户注册");
               // toAddr =  new InternetAddress(emailAddress); // 收件地址
            }

            msg.setFrom(new InternetAddress(fromAddr));//设置发件
            msg.setRecipient(Message.RecipientType.TO,new InternetAddress(emailAddress));//设置收件
            msg.setText("您的验证码是 "+verifyCode+"请勿泄露给他人");
            msg.setSentDate(new Date());
            Transport transport = session.getTransport();
            transport.send(msg);
            transport.close();
            Map map = new HashMap();
            map.put("verifyCode",verifyCode);
            String str = JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"验证码发送成功，注意查收邮件");
            return JsonUtil.makeJsonBeanByKey(str,map);

        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"验证码发送失败！");
        }

    }
}
