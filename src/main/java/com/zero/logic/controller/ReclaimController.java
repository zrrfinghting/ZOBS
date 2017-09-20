package com.zero.logic.controller;

import com.zero.basic.filter.BasicFilter;
import com.zero.logic.dao.LogDao;
import com.zero.logic.dao.ReclaimDao;
import com.zero.logic.domain.Log;
import com.zero.logic.domain.Reclaim;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 回收单类处理控制rest接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/8/2
 */
@RestController
@RequestMapping("/reclaim")
public class ReclaimController {
    @Autowired
    private ReclaimDao reclaimDao;
    @Autowired
    private LogDao logDao;
    @RequestMapping(value = "/saveRetrieve",method = RequestMethod.POST)
    @ApiOperation(value = "保存回收单",notes = "保存回收单信息")
    public String saveRetrieve(@RequestBody Reclaim reclaim){
        try {
            reclaim.setReclaimId(String.valueOf(System.currentTimeMillis()));//自动生成回收单编号
            reclaim.setCreateDate(new Date());
            reclaim.setCreateUser(BasicFilter.user_id);
            reclaimDao.save(reclaim);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"新增回收单："+reclaim.getReclaimId()+"成功",0,BasicFilter.user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增回收单成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增回收单成功");
        }
    }

    @RequestMapping(value = "/editReclaim",method = RequestMethod.POST)
    @ApiOperation(value = "修改回收单",notes = "修改回收单")
    public String editReclaim(@RequestBody Reclaim reclaim){
        try {
            reclaim.setUpdateDate(new Date());
            reclaim.setUpdateUser(BasicFilter.user_id);
            reclaimDao.save(reclaim);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"修改回收单："+reclaim.getReclaimId()+"成功",0,BasicFilter.user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改回收单成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改回收单失败");
        }
    }

    @RequestMapping(value = "/queryRetrieveById",method = RequestMethod.GET)
    @ApiOperation(value = "通过回收单查编号询回收单信息",notes = "通过回收单查编号询回收单信息")
    public String queryRetrieveById(@RequestParam String reclaimId){
        try {
            Reclaim reclaim = reclaimDao.queryByReclaimId(reclaimId);
            return JsonUtil.fromObject(reclaim);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取回收单信息失败");
        }
    }

    /**
     * 前台回收单查询
     * @param keyWord
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "通过关键字or状态分页获取回收单信息",notes = "通过关键字or状态分页获取回收单信息")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam("state")int state,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            String userCode = BasicFilter.user_id;
            Sort sort = new Sort(Sort.Direction.DESC,"createDate");//按照最新时间进行排序
            Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
            Page<Reclaim> reclaims = null;
            long total = 0;
            //前台的回收单查询
            if (state==-2){//state=-2查询该用户的所有状态
                reclaims = reclaimDao.findByReclaimId(keyWord,userCode,pageable);
                total = reclaimDao.countByReclaimId(keyWord,userCode);
            }else {
                reclaims = reclaimDao.findByReclaimIdAndState(keyWord,userCode,state,pageable);
                total = reclaimDao.countByReclaimIdAndState(keyWord,userCode,state);
            }
            List<Reclaim> list = new ArrayList<>();
            for (Reclaim reclaim:reclaims){
                list.add(reclaim);
            }
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"分页获取回收单失败");
        }
    }

    @RequestMapping(value = "/getByPageHouDuan",method = RequestMethod.GET)
    @ApiOperation(value = "通过关键字or状态分页获取回收单信息",notes = "通过关键字or状态分页获取回收单信息")
    public String getByPageHouDuan(
            @RequestParam("keyWord")String keyWord,
            @RequestParam("state")int state,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
                try {
                    Sort sort = new Sort(Sort.Direction.DESC,"createDate");//按照最新时间进行排序
                    Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
                    Page<Reclaim> reclaims = null;
                    long total = 0;

                    //后台查询
                    if (state==-2){//state=-2查询该用户的所有状态
                        reclaims = reclaimDao.findAllByKeyword(keyWord,pageable);
                        total = reclaimDao.countAllByKeyword(keyWord);
                    }else {
                        reclaims = reclaimDao.findAllByStateAndKeyword(keyWord,state,pageable);
                        total = reclaimDao.countAllByStateAndKeyword(keyWord,state);
                    }
                    List<Reclaim> list = new ArrayList<>();
                    for (Reclaim reclaim:reclaims){
                        list.add(reclaim);
                    }
                    long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
                    return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
                }catch (Exception e){
                    e.printStackTrace();
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"分页获取回收单失败");
                }
    }


    @RequestMapping(value = "/changeReclaimState",method = RequestMethod.GET)
    @ApiOperation(value = "改变回收单状态",notes = "改变回收单状态")
    public String changeReclaimState(@RequestParam String reclaimId,@RequestParam int state){
        try {

            Reclaim reclaim = reclaimDao.queryByReclaimId(reclaimId);
            reclaim.setState(state);
            reclaim.setUpdateDate(new Date());
            reclaimDao.save(reclaim);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"改变回收单"+reclaim.getReclaimId()+"状态从"+reclaim.getState()+"到"+state+"成功",0,BasicFilter.user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"改变回收单状态成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"改变回收单状态失败");
        }
    }
    @RequestMapping(value = "/deleteByReclaimId",method = RequestMethod.DELETE)
    @ApiOperation(value = "根据reclaimId删除单个或多个回收单信息",notes = "根据reclaimId删除单个或多个回收单信息")
    public String deleteByReclaimId(@RequestParam String []reclaimIds){
        String deleteId="";
        try {
            for (int i=0;i<reclaimIds.length;i++){
                Reclaim reclaim = reclaimDao.queryByReclaimId(reclaimIds[i]);
                reclaimDao.delete(reclaim);
                deleteId = reclaimIds[i];
            }
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除回收单信息成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除"+deleteId+"回收单信息失败");
        }
    }
}
