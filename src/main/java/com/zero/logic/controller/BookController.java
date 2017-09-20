package com.zero.logic.controller;

import com.zero.basic.filter.BasicFilter;
import com.zero.logic.dao.BookDao;
import com.zero.logic.dao.LogDao;
import com.zero.logic.domain.Book;
import com.zero.logic.domain.Log;
import com.zero.logic.util.*;
import io.swagger.annotations.ApiOperation;
import javassist.compiler.ast.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图书控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
@RestController
@RequestMapping("book")
public class BookController {
    @Autowired
    private BookDao bookDao;
    @Autowired
    private LogDao logDao;
    @RequestMapping(value = "/getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取图书",notes = "分页获取图书")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC,"createDate");//按照最新时间进行排序
            Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
            Page<Book> books=null;
            long total=0;
            if ("上架".equals(keyWord)){
                books = bookDao.findByState(1,pageable);
                total = bookDao.countByState(1);
            }else if ("下架".equals(keyWord)){
                books = bookDao.findByState(0,pageable);
                total = bookDao.countByState(0);
            }else {
                books = bookDao.findByBookName(keyWord,pageable);
                total = bookDao.count(keyWord);
            }
            List<Object> list = new ArrayList<>();
            for (Book book:books){
                list.add(book);
            }
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取图书失败");
        }
    }

    @RequestMapping(value = "/queryByTypeIdAndKeyword",method = RequestMethod.GET)
    @ApiOperation(value = "分类中模糊查询图书",notes = "分类中模糊查询图书")
    public String queryByTypeIdAndKeyword
            (@RequestParam("keyWord")String keyWord,
             @RequestParam("typeId")String typeId,
             @RequestParam("sort")String sort,
             @RequestParam("startPrice")String startPrice,
             @RequestParam("endPrice")String endPrice,
             @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
             @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            List<Object> list = new ArrayList<>();
            long total = 0;
            long totalPage=0;

            if ("".equals(typeId) && "".equals(startPrice) && "".equals(endPrice)){//分类ID为空则根据keyword模糊查询
                String reslut = sortByPrice(keyWord,sort,pageNum,pageSize);
                return reslut;
            }else {
                double beginPrice = 0;
                double finishPrice = 0;
                Sort sortStr = null;
                if (!"".equals(startPrice) && !"".equals(endPrice)){
                    beginPrice = Double.parseDouble(startPrice);
                    finishPrice = Double.parseDouble(endPrice);
                }else {
                    beginPrice = 0;//如果价格没传就默认0开始，10001结束
                    finishPrice = 10001;
                }

                List<Book> books = null;
                if ("".equals(sort) || "DESC".equals(sort)){
                    books = bookDao.findBooksByTypeIdDESC(keyWord,typeId,beginPrice,finishPrice,pageNum-1,pageSize);
                }else if ("ASC".equals(sort)){
                    books = bookDao.findBooksByTypeIdASC(keyWord,typeId,beginPrice,finishPrice,pageNum-1,pageSize);
                }
                for (Book book:books){
                    list.add(book);
                }
                total = bookDao.countByTypeId(keyWord,typeId,beginPrice,finishPrice);
                totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            }
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"在分类中通过关键字查询图书失败");
        }
    }
    public String sortByPrice(String keyWord,String sort, Integer pageNum,Integer pageSize){
        try {
            Sort sortStr=null;
            if ("ASC".equals(sort)){
                 sortStr = new Sort(Sort.Direction.ASC,"createDate");//按照最新时间进行排序
            }
            if ("DESC".equals(sort)){
                 sortStr = new Sort(Sort.Direction.DESC,"createDate");//按照最新时间进行排序
            }else {
                sortStr = new Sort(Sort.Direction.DESC,"createDate");//按时间排序
            }
            Pageable pageable = new PageRequest(pageNum-1,pageSize,sortStr);
            Page<Book> books=null;
            long total=0;
            books = bookDao.findByBookName(keyWord,pageable);
            total = bookDao.count(keyWord);
            List<Object> list = new ArrayList<>();
            for (Book book:books){
                list.add(book);
            }
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取图书失败");
        }
    }




    @RequestMapping(value = "/getBookByBookId",method = RequestMethod.GET)
    @ApiOperation(value = "获取图书",notes = "根据图书ID获取图书")
    public String getBookByBookId(@RequestParam("bookId")String bookId){
        try {
            Book book = bookDao.getBookByBookId(bookId);
            return JsonUtil.fromObject(book);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取图书失败");
        }
    }

    @RequestMapping(value = "/getBooksByTypeId",method = RequestMethod.GET)
    @ApiOperation(value = "根据分类ID获取图书",notes = "根据分类ID获取图书")
    public String getBooksByTypeId(
           @RequestParam("typeId")String typeId,
           @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
           @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC,"typeId");
            Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
            Page<Book> books =bookDao.findBookByTypeId(typeId,pageable);
            List<Object> list = new ArrayList<>();
            for (Book book:books){
                list.add(book);
            }
            long total = bookDao.countAllByTypeId(typeId);
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"根据分类ID获取图书失败");
        }
    }

    @RequestMapping(value = "/addBook",method = RequestMethod.POST)
    @ApiOperation(value = "新增图书",notes = "新增图书")
    public String addBook(@RequestBody Book book){
        try {
            book.setCreateDate(new Date());
            book.setUpdateDate(new Date());
            book.setCreateUser(BasicFilter.user_id);
            book.setBookId(CodeGeneratorUtil.getBookCode());
            book.setOrderBy((int) (book.getPrice()*book.getDiscount()));
            bookDao.save(book);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"新增"+book.getBookId()+"图书成功",0,BasicFilter.user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增图书成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"新增图书失败");
        }
    }
    @RequestMapping(value = "/editBook",method = RequestMethod.POST)
    @ApiOperation(value = "修改图书",notes = "修改图书")
    public String editBook(@RequestBody Book book){
        try {
            Book oldBook = bookDao.getBookByBookId(book.getBookId());
            if(null!=oldBook){
                bookDao.save(book);
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"修改"+book.getBookId()+"图书成功",0,BasicFilter.user_id));
            }
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改图书成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改图书失败");
        }
    }

    @RequestMapping(value = "/deleteBooks",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除图书",notes = "根据图书编号删除图书")
    public String deleteBook(@RequestParam("bookId") String []bookIds){
        try {
            String filePath = UploadFilePathUtil.uploadFilePath;//图书保存文件夹路径
            String books = "";
            String noBookId="";
            for (int i=0;i<bookIds.length;i++){
                String bookId = bookIds[i];
                Book oldBook = bookDao.getBookByBookId(bookId);
                if (null!=oldBook){
                    bookDao.delete(oldBook);
                    books +=bookId+",";
                    String image_l = oldBook.getImage_l();//图书大图路径
                    String image_s = oldBook.getImage_s();//详情图路径
                    if(!"".equals(image_l)){new File(filePath+image_l).delete();}
                    if (!"".equals(image_s)){new File(filePath+image_s).delete();}
                }else {
                    noBookId +=bookId+"、";
                }
            }
            if ("".equals(noBookId)){
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"删除"+books+"图书成功",0,BasicFilter.user_id));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除图书成功");
            }else {
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"删除"+books+"图书成功",0,BasicFilter.user_id));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"图书"+noBookId+"不存在，其余的图书删除成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除图书失败");
        }
    }
    @RequestMapping(value = "/changeState",method = RequestMethod.POST)
    @ApiOperation(value = "修改图书状态",notes = "修改图书状态")
    public String changeState(@RequestBody Object object){
        try {
            String bookId = JsonUtil.getString("bookId",object);
            int state = Integer.parseInt(JsonUtil.getString("state",object));
            Book oldBook = bookDao.getBookByBookId(bookId);
            oldBook.setState(state);
            bookDao.save(oldBook);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"修改图书"+bookId+"状态"+oldBook.getState()+"为："+state+"成功",0, BasicFilter.user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改状态成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改状态失败");
        }
    }
    @RequestMapping(value = "/getDiscount",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取特价货物",notes = "分页获取特价货物")
    public String getDiscount(
            @RequestParam("typeId")String typeId,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            //只查改分类的折扣货物
            if (!"".equals(typeId)){
                List<Book> books = bookDao.getByDiscountAndTypeId(pageNum-1,pageSize,typeId);
                long total = bookDao.countBookByDiscountAndTypeId(typeId);
                long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
                return TableUtil.createTableDate(books,total,pageNum,totalPage,pageSize);
            }
            //查所有的折扣货物
            List<Book> books = bookDao.getBooksBy(pageNum-1,pageSize);
            long total = bookDao.countByDiscount();
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(books,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取打折货物失败");
        }
    }
}












