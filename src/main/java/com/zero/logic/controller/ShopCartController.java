package com.zero.logic.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.zero.basic.filter.BasicFilter;
import com.zero.logic.dao.BookDao;
import com.zero.logic.dao.LogDao;
import com.zero.logic.dao.ShopCartDao;
import com.zero.logic.domain.Book;
import com.zero.logic.domain.Log;
import com.zero.logic.domain.ShopCart;
import com.zero.logic.util.CodeGeneratorUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/7
 */
@RestController
@RequestMapping("shopCart")
public class ShopCartController {
    @Autowired
    private ShopCartDao shopCartDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private LogDao logDao;

    @RequestMapping(value = "/addToShopCart",method = RequestMethod.POST)
    @ApiOperation(value = "添加货物到购物车",notes = "添加货物到购物车")
    public String addToShopCart(@RequestBody ShopCart shopCart){
        try {
            String user_id = BasicFilter.user_id;
            ShopCart oldCart = shopCartDao.getShopCartsByUsercodeandAndBookId(user_id,shopCart.getBookId());
            if(null==oldCart){//该用户未在购物车里添加该货物，直接保存进购物车
                shopCart.setUserCode(user_id);
                shopCart.setCreateUser(user_id);
                shopCart.setCreateDate(new Date());
                shopCart.setUpdateDate(new Date());
                shopCartDao.save(shopCart);
            } else {//该用户已经在购物和里添加过该货物，改变货物数量即可
                oldCart.setBookNum(shopCart.getBookNum()+oldCart.getBookNum());
                shopCartDao.save(oldCart);
            }
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"编号为"+user_id+"的用户向购物车添加"+shopCart.getBookId()+"编号货物成功",0,user_id ));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"添加货物成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"添加货物失败");
        }
    }

    @RequestMapping(value = "/editShopCart",method = RequestMethod.POST)
    @ApiOperation(value = "修改购物车里的某条货物信息（某条货物数量）",notes = "修改购物车信息")
    public String editShopCart(@RequestParam String bookId,@RequestParam int bookNum){
        try {
            String user_id = BasicFilter.user_id;
            ShopCart oldShopCart = shopCartDao.getShopCartsByUsercodeandAndBookId(user_id,bookId);
            oldShopCart.setBookNum(bookNum);
            shopCartDao.save(oldShopCart);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"编号为："+user_id+"的用户修改购物车里的货物编号是："+bookId+"的货物数量从"+oldShopCart.getBookNum()+"到"+bookNum+"",0,user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改失败");
        }
    }
    @RequestMapping(value = "/deleteShopCart",method = RequestMethod.DELETE)
    @ApiOperation(value = "根据货物编号批量删除购物车里的货物",notes = "删除物车里的一条或多条货物")
    public String deleteShopCart(@RequestParam String []bookIds){
        try {
            String user_id = BasicFilter.user_id;
            String deleteBookIds = "";
            for (int i=0;i<bookIds.length;i++){
                deleteBookIds +=bookIds[i]+"，";
                ShopCart shopCart = shopCartDao.getShopCartsByUsercodeandAndBookId(BasicFilter.user_id,bookIds[i]);
                if(null==shopCart){
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除购物车的货物信息失败");
                }
                shopCartDao.delete(shopCart);
            }
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"删除用户编号为"+user_id+"购物车里书籍编号是"+deleteBookIds+"的书籍成功",0,user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除购物车的货物信息成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除购物车的货物信息失败");
        }
    }
    @RequestMapping(value = "/emptyShopCart",method = RequestMethod.DELETE)
    @ApiOperation(value = "清空购物车",notes = "清空购物车")
    public String emptyShopCart(){
        try {
            String user_id = BasicFilter.user_id;
            shopCartDao.deleteByUsercode(user_id);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"编号为"+user_id+"的用的购物车清空成功",0,user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"清空购物车成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"清空购物车失败");
        }
    }
    @RequestMapping(value = "/getCartByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "根据userCode获取购物车信息",notes = "根据userCode分页获取购物车信息")
    public String getCartByUserCode(
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            String user_id = BasicFilter.user_id;
            Sort sort = new Sort(Sort.Direction.DESC, "updateDate");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<ShopCart> shopCarts = shopCartDao.getShopCartsByCreateUser(user_id,pageable);
            List<String> list = new ArrayList<>();
            for (ShopCart shopCart:shopCarts){
                Book oldBook = bookDao.getBookByBookId(shopCart.getBookId());
                Map map = new HashMap();
                map.put("bookName",oldBook.getBookName());
                map.put("price",oldBook.getPrice());
                map.put("discount",oldBook.getDiscount());
                map.put("author",oldBook.getAuthor());
                map.put("storeNumber",oldBook.getBookNum());//库存量
                map.put("image_l",oldBook.getImage_l());//货物图片
                String mapStr = JsonUtil.makeJsonBeanByKey(shopCart,map);
                list.add(mapStr);
            }
            long total = shopCartDao.countByCreateUser(user_id);
            long totalPage = total%pageSize==0?total/pageSize:total/pageSize+1;
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取购物车信息失败");
        }
        }
}
