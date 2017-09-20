package com.zero.logic.controller;

import com.zero.logic.dao.*;
import com.zero.logic.domain.*;
import com.zero.logic.util.CodeGeneratorUtil;
import com.zero.logic.util.DateUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 回收单接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/27
 */
@RestController
@RequestMapping("retrieve")
public class RetrieveController {

  /*  @Autowired
    private RetrieveDao retrieveDao;
    @Autowired
    private RetrieveBookDao retrieveBookDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private OrderBookDao orderBookDao;
    @Autowired
    private LogDao logDao;

    @RequestMapping(value = "/addRetrieve",method = RequestMethod.POST)
    @ApiOperation(value = "新增回收单",notes = "新增回收单信息")
    @Transactional
    public String addRetrieve(@RequestBody Object object){
        try {
            String STR = JsonUtil.fromArray(object);
            Retrieve retrieve = new Retrieve();
            String retrieveId = String.valueOf(System.currentTimeMillis());
            String orderId = JsonUtil.getString("orderId",object);
            int state=0;
            if (!"".equals(JsonUtil.getString("state",object))){
                state = Integer.parseInt(JsonUtil.getString("state",object));
            }

            retrieve.setRetrieveId(retrieveId);
            retrieve.setRetriever(JsonUtil.getString("retriever",object));
            retrieve.setRetrieverPhone(JsonUtil.getString("retrieverPhone",object));
            retrieve.setRetrieverAddress(JsonUtil.getString("retrieverAddress",object));
            retrieve.setRetrieveDate(DateUtil.parse(DateUtil.FORMAT2,JsonUtil.getString("retrieveDate",object)));
            retrieve.setState(state);
            retrieve.setOrderId(orderId);
            retrieve.setCreateDate(new Date());

            retrieveDao.save(retrieve);//保存回收单

            List<Book> books = new ArrayList<>();
            List<RetrieveBook> retrieveBooks = new ArrayList<>();
            String str = JsonUtil.getString("books",object);
            List<Object> list = JsonUtil.getList(str);
            for (Object obj:list){
                String bookId = JsonUtil.getString("bookId",obj);
                String bookName = JsonUtil.getString("bookName",obj);
                int bookNum = Integer.parseInt(JsonUtil.getString("bookNum",obj));
                String price = JsonUtil.getString("price",obj);
                double retrievePrice = Double.parseDouble(price);

                RetrieveBook retrieveBook = new RetrieveBook();
                retrieveBook.setRetrieveId(retrieveId);
                retrieveBook.setBookId(bookId);
                retrieveBook.setBookNum(bookNum);
                retrieveBook.setPrice(retrievePrice);


                if (state==1){//1主动回收，做新书入库处理且状态为未上架（不能进行售卖）
                    Book book = new Book();
                    String newBookId = CodeGeneratorUtil.getBookCode();
                    retrieveBook.setBookId(newBookId);
                    book.setBookId(newBookId);
                    book.setBookName(bookName);
                    book.setState(0);//状态为未上架
                    book.setBookNum(Integer.parseInt(JsonUtil.getString("bookNum",obj)));
                    book.setPublishTime(DateUtil.parse(DateUtil.FORMAT2,JsonUtil.getString("publishTime",obj)));
                    book.setPrinttime(DateUtil.parse(DateUtil.FORMAT2,JsonUtil.getString("printtime",obj)));
                    book.setAuthor(JsonUtil.getString("author",obj));
                    book.setBookDesc(JsonUtil.getString("bookDesc",obj));
                    book.setTypeId(JsonUtil.getString("typeId",obj));
                    book.setImage_l(JsonUtil.getString("image_l",obj));
                   // book.setImage_s(JsonUtil.getString("image_s",obj));//图书的详情图片 只有在书图书的时候添加
                    String bookPrice = JsonUtil.getString("bookSize",obj);
                    String pageNum = JsonUtil.getString("pageNum",obj);
                    String edition = JsonUtil.getString("edition",obj);
                    String orderBy = JsonUtil.getString("orderBy",obj);
                    if (!"".equals(bookPrice)){
                        book.setBookSize(Integer.parseInt(bookPrice));
                    }
                    if (!"".equals(pageNum)){
                        book.setPageNum(Integer.parseInt(pageNum));
                    }
                   if (!"".equals(edition)){
                       book.setEdition(Integer.parseInt(edition));
                   }
                   if (!"".equals(orderBy)){
                       book.setOrderBy(Integer.parseInt(orderBy));
                   }
                    books.add(book);
                }else if (state==0){//退货回收
                    Book oldBook = bookDao.getBookByBookId(bookId);
                    int storeNum = oldBook.getBookNum()+bookNum;
                    oldBook.setBookNum(storeNum);
                    books.add(oldBook);
                }

                retrieveBooks.add(retrieveBook);
            }
            retrieveBookDao.save(retrieveBooks);//保存订单图书
            bookDao.save(books);//保存图书
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增回收单成功");
        }catch (Exception e){
            e.printStackTrace();
            //因为sping 默认trycatch是不进行事务回滚的，可以在手动设置事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"新增回收单失败");
        }
    }

    @RequestMapping(value = "/getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取回收单",notes = "分页获取回收单信息")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "retrieveId");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<Retrieve> retrieves = retrieveDao.findRetrievesByRetrieveId(keyWord,pageable);
            List<Retrieve> list = new ArrayList<>();
            for (Retrieve retrieve:retrieves){
                list.add(retrieve);
            }
            long total = retrieveDao.count(keyWord);//获取查询总数
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取回收单信息失败");
        }
    }
    @RequestMapping(value = "/getRetrieveByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "根据userCode分页获取回收单",notes = "根据userCode分页获取回收单信息")
    public String getRetrieveByUserCode(
            @RequestParam("userCode")String userCode,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC,"updateDate");
            Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
            Page<Retrieve> retrieves = retrieveDao.findRetrievesByCreateUser(userCode,pageable);
            List<String> list = new ArrayList<>();
            for (Retrieve retrieve:retrieves){
                double price =0;//订单金额
                List<RetrieveBook> retrieveBooks = retrieveBookDao.getRetrieveBooksByRetrieveId(retrieve.getRetrieveId());
                for (RetrieveBook retrieveBook:retrieveBooks){
                    int bookNum = retrieveBook.getBookNum();
                    Book book = bookDao.getBookByBookId(retrieveBook.getBookId());
                    if (null!=book) {
                        double bookPrice =book.getPrice();
                        price += bookNum*bookPrice;
                    }
                }
                Map map = new HashMap();
                map.put("price",price);
                list.add(JsonUtil.makeJsonBeanByKey(retrieve,map));
            }
            long total = retrieveDao.countByCreateUser(userCode);
            long totalPage = total%pageSize==0?total/pageSize:total/pageSize+1;
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取回收单失败");
        }
    }
    @RequestMapping(value = "/getRetrieveById",method = RequestMethod.GET)
    @ApiOperation(value = "根据回收单ID获取回收单信息",notes = "根据回收单ID获取回收单信息")
    public  String getRetrieveById(@RequestParam String retrieveId){
        try {
            List<Book> books = new ArrayList<>();
            Retrieve oldRetrieve  = retrieveDao.getRetrieveByRetrieveId(retrieveId);
            List<RetrieveBook> retrieveBooks = retrieveBookDao.getRetrieveBooksByRetrieveId(retrieveId);
            Map map = new HashMap();
            for (RetrieveBook retrieveBook:retrieveBooks){
                Book oldBook = bookDao.getBookByBookId(retrieveBook.getBookId());
                if (null!=oldBook){
                    oldBook.setBookNum(retrieveBook.getBookNum());
                    books.add(oldBook);
                }
            }
            map.put("books",books);
            return JsonUtil.makeJsonBeanByKey(oldRetrieve,map);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取回收单信息失败");
        }
    }
    @RequestMapping(value = "/getDetailById",method = RequestMethod.GET)
    @ApiOperation(value = "根据回收单ID获取回收单详情信息",notes = "根据回收单ID获取回收单详情信息")
    public String getDetailById(@RequestParam String retrieveId){
        try {
            //Retrieve oldRetrieve  = retrieveDao.getRetrieveByRetrieveId(retrieveId);
            List<RetrieveBook> list = retrieveBookDao.getRetrieveBooksByRetrieveId(retrieveId);
            return JsonUtil.fromArray(list);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取回收单详情失败");
        }
    }
    @RequestMapping(value = "/editRetrieve",method = RequestMethod.POST)
    @ApiOperation(value = "修改回收单信息",notes = "修改回收单信息")
    @Transactional
    public  String editRetrieve(@RequestBody Object object){
        try {
            int state = Integer.parseInt(JsonUtil.getString("state",object));//回收单里的回收图书有两种：1--主动回收，0--退货回收
            String retrieveId =JsonUtil.getString("retrieveId",object);
            String strBooks = JsonUtil.getString("books",object);
            List<Object> books = JsonUtil.getList(strBooks);
            List<RetrieveBook> retrieveBooks = new ArrayList<>();
            List<RetrieveBook> oldretrieveBooks = retrieveBookDao.getRetrieveBooksByRetrieveId(retrieveId);
            for (Object obj:books){
                RetrieveBook reBook = new RetrieveBook();
                String bookId = JsonUtil.getString("bookId",obj);
                int retrieveNum = Integer.parseInt(JsonUtil.getString("bookNum",obj));
                reBook.setRetrieveId(retrieveId);
                reBook.setBookId(bookId);
                reBook.setBookNum(retrieveNum);
                reBook.setPrice(Double.parseDouble(JsonUtil.getString("price",obj)));
                retrieveBooks.add(reBook);

                RetrieveBook  oldRetrieveBook = retrieveBookDao.getRetrieveBookByBookId(bookId);
                int bookNum = oldRetrieveBook.getBookNum();//回收单的数量
                Book oldBook = bookDao.getBookByBookId(bookId);
                int storeNum = oldBook.getBookNum();//修改之前的数量
                if (retrieveNum-bookNum>0){
                    storeNum = storeNum+(retrieveNum-bookNum);
                }else if (retrieveNum-bookNum <0){
                    storeNum = storeNum -(bookNum-retrieveNum);
                }
                oldBook.setBookNum(storeNum);
                oldBook.setUpdateDate(new Date());
                bookDao.save(oldBook);

                *//*if (state==1){//1--主动回收
                    Book oldBook = bookDao.getBookByBookId(bookId);
                    if (null!=oldBook){
                        RetrieveBook  oldRetrieveBook = retrieveBookDao.getRetrieveBookByBookId(bookId);
                        int bookNum = oldRetrieveBook.getBookNum();//回收单的数量
                        int storeNum = oldBook.getBookNum();//修改之前的数量
                        if (retrieveNum-bookNum>0){
                            storeNum = storeNum+(retrieveNum-bookNum);
                        }else if (retrieveNum-bookNum <0){
                            storeNum = storeNum -(bookNum-retrieveNum);
                        }
                        oldBook.setBookNum(storeNum);
                        oldBook.setUpdateDate(new Date());
                        bookDao.save(oldBook);

                    }else {
                        Book book = new Book();
                        JSONObject json=JSONObject.fromObject(obj);
                        book = (Book) JSONObject.toBean(json,Book.class);
                        book.setState(0);//0--未上架，1--已经上架
                        book.setCreateDate(new Date());
                        book.setUpdateDate(new Date());
                        bookDao.save(book);
                    }
                }else if (state==0){//0--退货回收
                    RetrieveBook  oldRetrieveBook = retrieveBookDao.getRetrieveBookByBookId(bookId);
                    int bookNum = oldRetrieveBook.getBookNum();//回收单的数量
                    Book oldBook = bookDao.getBookByBookId(bookId);
                    int storeNum = oldBook.getBookNum();//修改之前的数量
                    if (retrieveNum-bookNum>0){
                        storeNum = storeNum+(retrieveNum-bookNum);
                    }else if (retrieveNum-bookNum <0){
                        storeNum = storeNum -(bookNum-retrieveNum);
                    }
                    oldBook.setBookNum(storeNum);
                    oldBook.setUpdateDate(new Date());
                    bookDao.save(oldBook);
                }*//*
            }
            for (RetrieveBook oldRetrieveBook:oldretrieveBooks){
                boolean flag = false;
                for (RetrieveBook retrieveBook:retrieveBooks){
                    if (oldRetrieveBook.getRetrieveId().equals(retrieveBook.getRetrieveId())){
                        flag = true;
                    }
                }
                if (!flag){
                    retrieveBookDao.delete(oldRetrieveBook);
                }
            }
            retrieveBookDao.save(retrieveBooks); //修改回收单

            JSONObject json=JSONObject.fromObject(object);
            json.remove("books");//Retrieve对象没有books字段，books是前端拼接的Book对象
            Retrieve oldRetrieve = retrieveDao.getRetrieveByRetrieveId(retrieveId);
            if (null!=oldRetrieve){
                String createDate = oldRetrieve.getCreateDate();
                //String retrieveDate = oldRetrieve.getRetrieveDate();
                oldRetrieve = (Retrieve) JSONObject.toBean(json,Retrieve.class);
                oldRetrieve.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,createDate));
                oldRetrieve.setUpdateDate(new Date());
                //oldRetrieve.setRetrieveDate(DateUtil.parse(DateUtil.FORMAT2,retrieveDate));
                retrieveDao.save(oldRetrieve);//修改回收单
            }
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改回收单成功");
        }catch (Exception e){
            e.printStackTrace();
            //因为sping 默认trycatch是不进行事务回滚的，手动设置事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改回收单失败");
        }
    }
    @RequestMapping(value = "/deleteRetrieve",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除回收单信息",notes = "根据回收单ID删除回收单信息")
    public String deleteRetrieve(@RequestParam("retrieveIds") String []retrieveIds){
        try {
            String unRetrieveIds = "";
            List<Retrieve> retrieves = new ArrayList<>();
            List<Book> books = new ArrayList<>();
            for (int i=0;i<retrieveIds.length;i++){
                try {
                    String retrieveId = retrieveIds[i];
                    Retrieve oldRetrieve = retrieveDao.getRetrieveByRetrieveId(retrieveId);
                    if (null!=oldRetrieve){
                        List<RetrieveBook> oldRetrieveBooks = retrieveBookDao.getRetrieveBooksByRetrieveId(retrieveId);
                        for (RetrieveBook retrieveBook:oldRetrieveBooks){
                            Book oldBook = bookDao.getBookByBookId(retrieveBook.getBookId());
                            if (null!=oldBook){
                                books.add(oldBook);
                            }
                        }
                        bookDao.delete(books);
                        retrieveBookDao.delete(oldRetrieveBooks);
                        retrieves.add(oldRetrieve);
                    }else {
                        unRetrieveIds +=retrieveId+",";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    //因为sping 默认trycatch是不进行事务回滚的，手动设置事务回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }

            }
            retrieveDao.delete(retrieves);
            if ("".equals(unRetrieveIds)){
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除回收单成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"回收单"+unRetrieveIds+"删除失败，其他的删除回收单成功");
            }

        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除回收单失败");
        }
    }*/
}
