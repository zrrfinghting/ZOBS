package com.zero.logic.controller;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.zero.basic.filter.BasicFilter;
import com.zero.logic.dao.LogDao;
import com.zero.logic.dao.PurviewDao;
import com.zero.logic.dao.RoleDao;
import com.zero.logic.dao.UserDao;
import com.zero.logic.domain.Log;
import com.zero.logic.domain.Purview;
import com.zero.logic.domain.Role;
import com.zero.logic.domain.User;
import com.zero.logic.util.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import sun.misc.resources.Messages_pt_BR;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private LogDao logDao;
    @Autowired
    private PurviewDao purviewDao;

    @RequestMapping(value = "getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取用户",notes = "分页获取用户")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "userCode");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<User> users=null;
            long total =0;
            if ("启用".equals(keyWord)){
                users = userDao.findUsersByState(1,pageable);
                total = userDao.countByState(1);//获取查询总数
            }else if ("停用".equals(keyWord)){
                users = userDao.findUsersByState(0,pageable);
                total = userDao.countByState(0);;//获取查询总数
            }else {
                users = userDao.findByUserName(keyWord,pageable);
                total = userDao.count(keyWord);//获取查询总数
            }
            List<Object> list = new ArrayList<>();
            for(User user:users){
                list.add(user);
            }
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取用户失败");
        }

    }

    @RequestMapping(value = "/getUserByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "获取用户",notes = "根据用户编号获取所有用户")
    public String getUserByUserCode(
            @ApiParam(required=true,name="userCode", value="用户编号")
            @RequestParam("userCode")String userCode) throws Exception {
        try {
            User user = userDao.getUserByUserCode(userCode);
            return JsonUtil.fromObject(user);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取用户失败");
        }

    }

    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @ApiOperation(value = "新增用户",notes = "新增用户信息")
    public String addUser(@RequestBody User user,@RequestParam String [] roles){
                try {
                    for (String roleId:roles){
                        Role role = roleDao.getRoleByRoleId(roleId);
                        if (null!=role && role.getState()!=0 ){
                            user.getRoles().add(role);
                        }
                    }
                    user.setUserPsw(MD5Util.getMd5(user.getUserCode(),user.getUserPsw()));
                    user.setCreateDate(new Date());
                    user.setUpdateDate(new Date());
                    userDao.save(user);
                    //记录日志
                    logDao.save(new Log(new Date(),new Date(),"新增用户"+user.getUserName()+"成功",0,BasicFilter.user_id));
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "新增用户信息成功");
                }catch (Exception e){
                    e.printStackTrace();
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "新增用户信息失败");
                }
    }
    @RequestMapping(value = "/registerUser",method = RequestMethod.POST)
    @ApiOperation(value = "注册用户",notes = "注册用户")
    public String registerUser(@RequestBody User user){
        try {
            user.setUserPsw(MD5Util.getMd5(user.getUserCode(),user.getUserPsw()));
            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            userDao.save(user);
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"注册成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"注册失败");
        }
    }

    @RequestMapping(value = "/editUser",method = RequestMethod.POST)
    @ApiOperation(value = "修改用户",notes = "根据用户编号修改用户")
    public String editUser(@RequestBody User user,@RequestParam String [] roleIds,HttpServletRequest req) throws ParseException {
        try {
            User oldUser = userDao.getUserByUserCode(user.getUserCode());
            if(null!=oldUser){
                //保存用户角色
                for (int i=0;i<roleIds.length;i++){
                    String roleId = roleIds[i];
                    Role role = roleDao.getRoleByRoleId(roleId);
                    if(null!=role && role.getState()!=0){
                        user.getRoles().add(role);
                    }
                }
                if (!oldUser.getUserPsw().equals(user.getUserPsw())){
                    user.setUserPsw(MD5Util.getMd5(user.getUserCode(),user.getUserPsw()));
                }else {
                    user.setUserPsw(oldUser.getUserPsw());
                }
                user.setUpdateDate(new Date());//修改时间
                user.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,user.getCreateDate()));
                userDao.save(user);
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"修改用户"+user.getUserCode()+"成功",0,BasicFilter.user_id));
                return  JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"用户修改成功");
           }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户修改失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户修改失败");
        }
    }

    @RequestMapping(value = "/deleteUsers",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户",notes = "根据用户编号删除用户")
    public String deleteUsers(@RequestParam String []userCodes) {
        try {
            String deleteId ="";
            String unDeleteId="";
            for (int i=0;i<userCodes.length;i++){
                String userCode = userCodes[i];
                User oldUser = userDao.getUserByUserCode(userCode);
                if (oldUser.getState()==0){//只能删除停用的用户
                    userDao.delete(oldUser);
                    deleteId +=userCode+"，";
                }else if (oldUser.getState()==1){
                    unDeleteId +=userCode;
                }
            }
            if ("".equals(unDeleteId)){
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"删除用户"+deleteId+"成功",0,BasicFilter.user_id));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除用户成功");
            }else {
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"删除用户"+deleteId+"成功",0,BasicFilter.user_id));
                if (userCodes[0].length()==unDeleteId.length()){//单个用户
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"" + unDeleteId + "用户未停用，删除失败");
                }else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"" + unDeleteId + "用户未停用，其余用户删除成功");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除用户失败");
        }
    }

    @RequestMapping(value = "/changeUserState",method = RequestMethod.GET)
    @ApiOperation(value = "修改用户状态",notes = "修改单个或多个用户账号状态")
    public String changeUserState(
            @RequestParam("userCodes") String [] userCodes,
            @RequestParam("state")int state){
        try {
            String userState ="";
            String unUserState="";
            String ownState="";
            for (int i=0;i<userCodes.length;i++){
                String userCode = userCodes[i];
                User oldUser = userDao.getUserByUserCode(userCode);
                if (null!=oldUser){
                    if (userCode.equals(BasicFilter.user_id)){//禁止用户自己修改自己的状态
                        ownState+=userCode;
                        unUserState +=userCode;
                    }else {
                        oldUser.setState(state);
                        oldUser.setUpdateDate(new Date());
                        oldUser.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldUser.getCreateDate()));
                        userDao.save(oldUser);
                        userState +=userCode+",";
                    }
                }else {
                    unUserState +=userCode;
                }
            }
            if ("".equals(unUserState)){
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"修改用户"+userState+"状态为："+state+"成功",0,BasicFilter.user_id));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"用户状态修改成功");
            }else {
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"修改用户"+userState+"状态为："+state+"成功",0,BasicFilter.user_id));
                if (userCodes[0].length()==unUserState.length()){
                    if (userCodes[0].length()==ownState.length()){
                        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,""+ownState+"禁止修改自身的状态");
                    }
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,""+unUserState+"用户状态修改失败");
                }else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"除了"+unUserState+"用户状态修改失败，其余修改成功");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"用户状态修改失败");
        }
    }

    @RequestMapping(value = "/changeUserPsw",method = RequestMethod.POST)
    @ApiOperation(value = "修改用户密码",notes = "修改用户密码")
    public String changeUserPsw(@RequestBody Object obj){
        try {
            String userCode =JsonUtil.getString("userCode",obj);
            String userPsw = JsonUtil.getString("userPsw",obj);
            String oldUserPsw = JsonUtil.getString("oldUserPsw",obj);
            oldUserPsw = MD5Util.getMd5(userCode,oldUserPsw);//校验用户旧密码
            User oldUser = userDao.getUserByUserCode(userCode);
            if (oldUserPsw.equals(oldUser.getUserPsw())){
                oldUser.setUserPsw(MD5Util.getMd5(userCode,userPsw));//设置新密码
                oldUser.setUpdateDate(new Date());
                userDao.save(oldUser);
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"修改用户"+userCode+"密码成功",0,BasicFilter.user_id));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"密码修改成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"旧密码不正确");
            }
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"密码修改失败");
        }
    }


    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ApiOperation(value = "登录系统",notes = "登录系统")
    public String login(@RequestBody Object object){
        try {
            String userCode = JsonUtil.getString("userCode",object);
            String userPsw = JsonUtil.getString("userPsw",object);
            //String codeDate = new String(Base64Decode.decode(userCode));//解密用户名
            //String pswDate = new String(Base64Decode.decode(userPsw));//解密用户密码
            Map<String, Object> map = new HashMap<>();
            User oldUser = userDao.getUserByUserCode(userCode);
            if(null!=oldUser){
                if (oldUser.getState()==0){
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户为停用状态,不能登录系统");
                }else {
                    userPsw = MD5Util.getMd5(userCode,userPsw);
                    if(oldUser.getUserPsw().equals(userPsw)){
                        //记录日志
                        logDao.save(new Log(new Date(),new Date(),"用户"+userCode+"登录成功",0,userCode));
                        //用户登录成功后返回一个user对象给前端时(不返回密码)
                        User newUser = new User();
                        newUser.setUserCode(oldUser.getUserCode());
                        newUser.setUserName(oldUser.getUserName());
                        newUser.setState(oldUser.getState());
                        newUser.setPhone(oldUser.getPhone());
                        newUser.setEmail(oldUser.getEmail());
                        newUser.setAddress(oldUser.getAddress());
                        newUser.setCreateUser(oldUser.getCreateUser());

                        String roleStr = JsonUtil.getString("roles",JsonUtil.fromObject(oldUser));
                        List roles = JsonUtil.getList(roleStr);
                        //遍历用户角色
                        Map map_purview = new IdentityHashMap();//key值是可以重复的map
                        List roleId_list = new ArrayList();
                        for (int i=0;i<roles.size();i++){
                            Map map_roleIds = new HashMap();
                            String roleId =JsonUtil.getString("roleId", roles.get(i));
                            map_roleIds.put("roleId",roleId);
                            roleId_list.add(map_roleIds);
                            //根据roleId获取权限
                            List<Object> purviews = purviewDao.getQurview(roleId);
                            for (Object obj:purviews){
                                Purview oldPurview = purviewDao.getPurviewByPurviewId(obj.toString());
                                String purviewRule = oldPurview.getPurviewRule();
                                map_purview.put(String.valueOf(purviewRule.charAt(0)),purviewRule.charAt(2));
                            }
                        }
                        Map purview = new HashMap();

                        purview.put("roles",roleId_list);
                        purview.put("purview",map_purview);
                        String userStr = JsonUtil.makeJsonBeanByKey(newUser,purview);
                        Map map_token = new HashMap();
                        //生成token保存到redis缓存，返回token给前端
                        TokenUtil.createAndSaveToken(userCode);
                        map_token.put("token",TokenUtil.getToken(userCode));
                        userStr = JsonUtil.makeJsonBeanByKey(userStr,map_token);
                        map.put("user",userStr);
                        //生成token保存到redis缓存，返回token给前端
                       // TokenUtil.createAndSaveToken(userCode);
                        //map.put("token",TokenUtil.getToken(userCode));
                        String result = JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"登录成功");
                        //清除map里记录的连续输错密码的次数
                         PswErrorNumUtil.map.remove(userCode);
                        return JsonUtil.makeJsonBeanByKey(result,map);
                    }else {
                        PswErrorNumUtil.setErrorNum(userCode);
                        int errorNum = Integer.parseInt(PswErrorNumUtil.map.get(userCode).toString());
                        if (errorNum>6){//如果连续输入6次初五就停用账户
                         User user =  userDao.getUserByUserCode(userCode);
                         user.setState(0);
                         userDao.save(user);

                        }
                        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"密码不正确");
                    }
                }
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户不存在");
            }
        }catch(Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"登录失败");
        }
    }

    @RequestMapping(value = "resetPsw",method = RequestMethod.POST)
    @ApiOperation(value = "重置用户密码",notes = "重置用户密码")
    public String resetPsw(@RequestBody Object object){
        try {
            String userCode = JsonUtil.getString("userCode",object);
            String userPsw = JsonUtil.getString("userPsw",object);
            String verifyCode = JsonUtil.getString("verifyCode",object);

            User oldUser = userDao.getUserByUserCode(userCode);
            if (null!=oldUser){
                //校验验证码否过期
                //Date outDate = DateUtil.parse(DateUtil.FORMAT2,oldUser.getOutDate());
               // Date currentDate = DateUtil.parse(DateUtil.FORMAT2,DateUtil.formatDate(DateUtil.FORMAT2,new Date()));
                //if (outDate.getTime()<currentDate.getTime()){
                //    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"验证码已经过期，请重申请找回密码！");
                //}

                if (redisTemplate.opsForValue().get(userCode)!=null){
                    String oldVerifyCode = redisTemplate.opsForValue().get(userCode).toString();
                    if (verifyCode.equals(oldVerifyCode)){
                        userPsw = MD5Util.getMd5(userCode,userPsw);
                        oldUser.setUserPsw(userPsw);
                        userDao.save(oldUser);
                        //记录日志
                        logDao.save(new Log(new Date(),new Date(),"用户重置密码成功",0, BasicFilter.user_id));
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"密码设置成功");
                    }else {
                        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"验证码不正确");
                    }
                }else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"验证码过期");
                }
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"重置密码失败！");
        }
    }

    @RequestMapping(value = "existsUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "用户注册时校验用户名是否已经被他人注册",notes = "用户注册是校验用户名是否已经被他人注册")
    public String existsUserCode(@RequestParam("userCode") String userCode){
        User user = userDao.getUserByUserCode(userCode);
        if (null==user){
            return "false";
        }else {
            return "true";
        }
    }
    /*@RequestMapping(value = "getUserByState",method = RequestMethod.GET)exists
    @ApiOperation(value = "根据用户状态分页获取用户",notes = "根据用户状态分页获取用户")
    public String  getUserByState(
            @RequestParam("state")int state,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "userCode");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<User> users = userDao.findUsersByState(state,pageable);
            List<Object> list = new ArrayList<>();
            for(User user:users){
                list.add(user);
            }
            long total = userDao.countByState(state);//获取查询总数
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return  JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取失败");
        }
    }*/

    //增加一个账号锁定功能和账号解锁功能
}
