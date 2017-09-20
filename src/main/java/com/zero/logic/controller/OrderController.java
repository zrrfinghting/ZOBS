package com.zero.logic.controller;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.zero.basic.filter.BasicFilter;
import com.zero.logic.dao.*;
import com.zero.logic.domain.*;
import com.zero.logic.util.DateUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.*;

/**
 * 订控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/19
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderBookDao orderBookDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private LogDao logDao;
    @Autowired
    private SellerIfoDao sellerIfoDao;

    @RequestMapping(value = "/addOrder",method = RequestMethod.POST)
    @ApiOperation(value = "生成订单",notes = "生成订单信息")
    @Transactional
    public String addOrder(@RequestBody Object obj){
        try {
            String orderId = String.valueOf(System.currentTimeMillis());
            int state = Integer.parseInt(JsonUtil.getString("state",obj));//生成订单时订单状态是未付款状态
            int payMode = Integer.parseInt(JsonUtil.getString("payMode",obj));//支付方式 0--货到付款，1--在线支付
            String address = JsonUtil.getString("address",obj);
            String receiver = JsonUtil.getString("receiver",obj);
            String phone = JsonUtil.getString("phone",obj);
            String delivery = JsonUtil.getString("delivery",obj);
            String deliveryDate = JsonUtil.getString("deliveryDate",obj);
            String receiverDate = JsonUtil.getString("receiverDate",obj);
            Date createDate = new Date();
            String createUser = BasicFilter.user_id;
            //保存订单里的货物信息
            String orderBooks = JsonUtil.getString("orderBookList",obj);
            List<Object> orderBookList =JsonUtil.getList(orderBooks);
            List<OrderBook> list = new ArrayList<>();
            for (Object object:orderBookList){

                String bookId = JsonUtil.getString("bookId",object);
                String bookNum = JsonUtil.getString("bookNum",object);
                int orderBookNum = Integer.parseInt(bookNum);
                Book oldBook = bookDao.getBookByBookId(bookId);
                oldBook.setBookNum(oldBook.getBookNum()-orderBookNum); //库存货物数量 - 订单货物数量
                bookDao.save(oldBook);

                OrderBook newOrderBook = new OrderBook();
                newOrderBook.setOrderId(orderId);
                newOrderBook.setBookId(bookId);
                newOrderBook.setBookNum(orderBookNum);
                newOrderBook.setState(state);
                newOrderBook.setCreateDate(new Date());
                newOrderBook.setUpdateDate(new Date());
                list.add(newOrderBook);
            }
            if (list.size()<1){
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"请选择你要购买的东西，不能提交空订单");
            }
            orderBookDao.save(list);
            //保存订单信息
            Order order = new Order();
            order.setOrderId(orderId);
            order.setState(state);
            order.setPayMode(payMode);
            order.setAddress(address);
            order.setReceiver(receiver);
            order.setPhone(phone);
            order.setDelivery(delivery);
            order.setDeliveryDate(DateUtil.parse(DateUtil.FORMAT2,deliveryDate));
            order.setReceiverDate(DateUtil.parse(DateUtil.FORMAT2,receiverDate));
            order.setCreateDate(createDate);
            order.setCreateUser(BasicFilter.user_id);
            orderDao.save(order);

            //记录日志
            logDao.save(new Log(new Date(),new Date(),"新增订单"+orderId+"成功",0,BasicFilter.user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增订单成功");
        }catch (Exception e){
            e.printStackTrace();
            //因为sping 默认trycatch是不进行事务回滚的，可以在手动设置事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"新增订单失败");
        }
    }

    /**
     * 一、订单流程  结算--》填写或次该收货地址--》提交订单--》付款
     *待付款：可以修改收货地址和取消订单
     * 订单状态 （未付款的时候可以取消订单且做物理删除）取消订单-1，交易成功0，待付款1，待商家发货2，商家已经发货3，申请退款待商家确认4,退款成功5，
     * 申请退货待商家确认6，退货成功7，完成交易8，关闭交易9，
     *
     */
    @RequestMapping(value = "/editOrder",method = RequestMethod.POST)
    @ApiOperation(value = "修改订单",notes = "修改订单信息")
    public String editOrder(@RequestBody Order order){
        try {
            Order oldOrder = orderDao.findOrderByOrderId(order.getOrderId());
            order.setUpdateDate(new Date());
            order.setUpdateUser(BasicFilter.user_id);
            order.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldOrder.getCreateDate()));
            orderDao.save(order);
            //同步订单的状态到属于这条订单的货物
            List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(order.getOrderId());
            for (OrderBook orderBook:orderBooks){
                if (orderBook.getState()==2){
                    orderBook.setState(order.getState());
                }else if (orderBook.getState()==1 && oldOrder.getPayMode()==0 ){
                    orderBook.setState(order.getState());
                }
                orderBookDao.save(orderBook);
            }
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"修改订单"+oldOrder.getOrderId()+"成功",0,BasicFilter.user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改订单成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改订单失败");
        }
    }
    @RequestMapping(value = "/deleteOrder",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除订单",notes = "根据订单ID删除订单信息")
    public String deleteOrder(@RequestParam String [] orderIds){
        try {
            String unDeletes="";
            String deletes="";
            for (int i=0;i<orderIds.length;i++){
                Order oldOrder = orderDao.findOrderByOrderId(orderIds[i]);

                if (null!=oldOrder && oldOrder.getState()==1){//只能删除状态为待付款的订单
                    orderDao.delete(oldOrder);
                    orderBookDao.deleteByOrderId(oldOrder.getOrderId());
                    deletes +=oldOrder.getOrderId()+",";
                }else {
                    unDeletes +=orderIds[i]+",";
                }
            }
            if ("".equals(unDeletes)){
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"删除订单"+deletes+"成功",0,BasicFilter.user_id));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除订单成功");
            }else {
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"删除订单"+deletes+"成功",0,BasicFilter.user_id));
                String str[] = unDeletes.split(",");
                if (str.length==1){
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"除了订单"+unDeletes+"删除失败");
                }else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"除了订单"+unDeletes+"未能删除，其余订单删除成功");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除订单失败");
        }
    }

    @RequestMapping(value = "getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取订单",notes = "分页获取订单信息")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "orderId");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<Order> orders = orderDao.getByOrderId(keyWord,pageable);
            List<Object> list = new ArrayList<>();
            for (Order order:orders){
                Map map = new HashMap();
                String orderStr = JsonUtil.fromObject(order);
                //遍历该条订单的所有货物信息计算出该条订单的金额
                List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(order.getOrderId());
                double price =0;//订单金额

                List<Object> books = new ArrayList<>();
                for (OrderBook orderBook:orderBooks){
                    Book book = bookDao.getBookByBookId(orderBook.getBookId());
                    Map map_book = new HashMap();
                    map_book.put("bookId",book.getBookId());
                    map_book.put("state",orderBook.getState());
                    int orderNum = orderBook.getBookNum();
                    if (null!=book) {
                        double bookPrice =book.getPrice();
                        price += orderNum*bookPrice;
                    }
                    books.add(map_book);
                }
                map.put("price",price);
                map.put("books",books);
                String orderMap = JsonUtil.makeJsonBeanByKey(orderStr,map);

                //String orderBooksstr = JsonUtil.fromArray(orderBooks);
                //map.put("detail",orderBooksstr);
                //String str = JsonUtil.makeJsonBeanByKey(orderMap,map);
                list.add(orderMap);
            }
            long total = orderDao.count(keyWord);//获取查询总数
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"查询订单失败");
        }
    }

    @RequestMapping(value = "/getOrderByOrderId",method = RequestMethod.GET)
    @ApiOperation(value = "根据订单ID获取订单信息",notes = "根据订单ID获取订单信息")
    public String getOrderByOrderId(@RequestParam("orderId") String orderId){
        try {
            Order oldOrder = orderDao.findOrderByOrderId(orderId);
            Map map = new HashMap();
            String orderStr = JsonUtil.fromObject(oldOrder);
            //遍历该条订单的所有货物信息计算出该条订单的金额
            List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(orderId);
            List<Object> books = new ArrayList<>();
            for (OrderBook orderBook:orderBooks){
                String bookId = orderBook.getBookId();
                Book book = bookDao.getBookByBookId(bookId);
                Map book_map = new HashMap();
                book_map.put("img_l",book.getImage_l());
                book_map.put("bookId",book.getBookId());
                book_map.put("bookName",book.getBookName());
                book_map.put("price",book.getPrice());
                book_map.put("discount",book.getDiscount());
                book_map.put("bookNum",orderBook.getBookNum());
                book_map.put("author",book.getAuthor());
                book_map.put("state",orderBook.getState());//这里的state是指该货物在订单里的状态
                books.add(book_map);
            }
            map.put("books",books);
            //添加商家信息
            SellerInfo sellerInfo = sellerIfoDao.getBySellerCode("sellerCode");
            map.put("sellerInfo",sellerInfo);
            String orderMap = JsonUtil.makeJsonBeanByKey(orderStr,map);
            return  orderMap;
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取订单信息失败");
        }
    }

    @RequestMapping(value = "getDetailByOrderId",method = RequestMethod.GET)
    @ApiOperation(value = "根据订单ID获取订单详情",notes = "根据订单ID获取订单详情信息")
    public String getDetailByOrderId(
            @RequestParam("orderId")String orderId,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "orderId");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<OrderBook> orderBooks = orderBookDao.findOrderBooksByOrderId(orderId,pageable);
            List<Object> orderList = new ArrayList<>();
            for (OrderBook orderBook:orderBooks){
                Book book = bookDao.getBookByBookId(orderBook.getBookId());
                Map map = new HashMap<>();
                map.put("bookName",book.getBookName());
                map.put("storeHouse",book.getStorehouse());
                map.put("price",book.getPrice());
                map.put("discount",book.getDiscount());
                String strList = JsonUtil.fromObject(orderBook);
                strList = JsonUtil.makeJsonBeanByKey(strList,map);
                orderList.add(strList);
            }
            long total = orderBookDao.count(orderId);//获取查询总数
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(orderList,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取订单详情失败");
        }
    }

    /**
     *待付款：可以修改收货地址和取消订单
     * 订单状态 （未付款的时候可以取消订单且做物理删除）取消订单-1，完成交易0，待付款1，待商家发货2，商家已经发货3，申请退款待商家确认4,退款成功5，
     * 申请退货待商家确认6，退货成功7，关闭交易8，
     *
     *
     */
    @RequestMapping(value = "changeOrderState",method = RequestMethod.POST)
    @ApiOperation(value = "根据订单ID改变订单状态",notes = "根据订单ID改变订单状态情信息")
    public String changeOrderState(@RequestBody Object object){
        try {
            String orderId = JsonUtil.getString("orderId",object);
            String bookId = JsonUtil.getString("bookId",object);
            int state = Integer.parseInt(JsonUtil.getString("state",object));
            Order oldOrder = orderDao.findOrderByOrderId(orderId);
            //买家操作：代付款：可取消订单；付款后：商家未发货可申请退款；商家已经发货只能申请退货
            if (state==4){
                if (oldOrder.getState()==2){
                    //如果bookId是空那么标识是整条订单进行申请退款
                    if("".equals(bookId)){
                        saveOrder(oldOrder,state);//保存整条订单状态
                        List<OrderBook> orderBooks = orderBookDao.findOrderBooksByOrderId(orderId);
                        for (OrderBook orderBook:orderBooks){
                            orderBook.setState(state);//orderBook表的和这一条订单相关的书籍也要设置状态和订单保持一致
                            orderBookDao.save(orderBook);
                        }
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"您的订单申请退款操作成功，请您耐心等待商家确认");
                    }else {
                        OrderBook orderBook = orderBookDao.getByOrderIdAndBookId(orderId,bookId);
                        orderBook.setState(state);//该货物状态为退款申请
                        orderBookDao.save(orderBook);
                        //如果这一条订单的所有货物都为退款申请了，那么该条订单的状态就改为退货申请状态，相当于整条订单申请退货
                        List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(orderId,4);
                        if (orderBooks.size()==0){
                            saveOrder(oldOrder,state);
                        }else {
                            saveOrder(oldOrder,oldOrder.getState());//订单还是已经发货状态，只是改变要退货的货物状态而已
                        }
                    }
                }else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"您的订单状态为"+oldOrder.getState()+"不能申请退款");
                }//退货
            }else if (state==6){
                if (oldOrder.getState()==3){
                    if ("".equals(bookId)){
                        saveOrder(oldOrder,state);//保存整条订单状态
                        List<OrderBook> orderBooks = orderBookDao.findOrderBooksByOrderId(orderId);
                        for (OrderBook orderBook:orderBooks){
                            orderBook.setState(state);//orderBook表的和这一条订单相关的书籍也要设置状态和订单保持一致
                            orderBookDao.save(orderBook);
                        }
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"您的订单申请退款操作成功，请您耐心等待商家确认");
                    }else {
                        OrderBook orderBook = orderBookDao.getByOrderIdAndBookId(orderId,bookId);
                        orderBook.setState(state);//该货物状态为退货申请
                        orderBookDao.save(orderBook);
                        //如果这一条订单的所有货物都为退款申请了，那么该条订单的状态就改为退货申请状态，相当于整条订单申请退货
                        List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(orderId,6);
                        if (orderBooks.size()==0){
                            saveOrder(oldOrder,state);
                        }else {
                            saveOrder(oldOrder,oldOrder.getState());//订单还是已经发货状态，只是改变要退货的货物状态而已
                        }
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"您的订单申请退货操作成功，请您耐心等待商家确认");
                    }

                }else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"您的订单状态为"+oldOrder.getState()+"不能申请退货");
                }//取消订单
            }else if (state==-1){
                if (oldOrder.getState()==1){
                    if ("".equals(bookId)){
                        saveOrder(oldOrder,state);;//保存整条订单状态
                        List<OrderBook> orderBooks = orderBookDao.findOrderBooksByOrderId(orderId);
                        for (OrderBook orderBook:orderBooks){
                            orderBook.setState(state);//orderBook表的和这一条订单相关的书籍也要设置状态和订单保持一致
                            orderBookDao.save(orderBook);
                        }
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"取消订单作成功");
                    }else {
                        OrderBook orderBook = orderBookDao.getByOrderIdAndBookId(orderId,bookId);
                        orderBook.setState(state);//该货物状态为取消订单
                        orderBookDao.save(orderBook);
                        //如果这一条订单的所有货物都为取消订单了，那么该条订单的状态就改为去取消订单状态，相当于取消整条订单
                        List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(orderId,-1);
                        if (orderBooks.size()==0){
                            saveOrder(oldOrder,state);
                        }else {
                            saveOrder(oldOrder,oldOrder.getState());//订单还是待付款状态，只是改变要取消的货物状态而已
                        }
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"您的订单取消成功");
                    }
                }else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"您的订单状态为"+oldOrder.getState()+"不能取消订单");
                }
            }else if (state==0){
                if ("".equals(bookId)){
                    oldOrder.setReceiverDate(new Date());
                    saveOrder(oldOrder,state);;//保存整条订单状态
                    List<OrderBook> orderBooks = orderBookDao.findOrderBooksByOrderId(orderId);
                    for (OrderBook orderBook:orderBooks){
                        if(orderBook.getState()==3){//如果这条订单下的货物状态是3--是商家已经发货就可以将其改为0----完成交
                            orderBook.setState(state);//orderBook表的和这一条订单相关的书籍也要设置状态和订单保持一致
                            orderBookDao.save(orderBook);
                        }
                    }
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"交易完成！");
                }else {
                    OrderBook orderBook = orderBookDao.getByOrderIdAndBookId(orderId,bookId);
                    orderBook.setState(state);//该货物状态为交易完成
                    orderBookDao.save(orderBook);
                    //如果这一条订单的所有货物都为完成交易了，那么该条订单的状态就改为完成交易状态，相当于整条订单完成交易
                   // List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(orderId,-1);
                   // if (orderBooks.size()==0){
                        oldOrder.setReceiverDate(new Date());//收货时间
                        saveOrder(oldOrder,state);
                    //}else {
                   //     saveOrder(oldOrder,oldOrder.getState());//订单还是发货状态，只是改变已经完成交易货物状态而已
                   // }
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"您的订单已经完成交易");
                }

            }

            //商家操作
            if (state==5){
                if (oldOrder.getState()==4){
                    if ("".equals(bookId)){
                        saveOrder(oldOrder,state);//保存整条订单状态
                        List<OrderBook> orderBooks = orderBookDao.findOrderBooksByOrderId(orderId);
                        for (OrderBook orderBook:orderBooks){
                            orderBook.setState(state);//orderBook表的和这一条订单相关的书籍也要设置状态和订单保持一致
                            orderBookDao.save(orderBook);
                        }
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"退款成功");
                    }else {
                        String str = "";
                        OrderBook orderBook = orderBookDao.getByOrderIdAndBookId(orderId,bookId);
                        orderBook.setState(state);//该货物状态为退款申请
                        orderBookDao.save(orderBook);
                        //如果这一条订单的所有货物都为退款申请了，那么该条订单的状态就改为退货申请状态，相当于整条订单申请退货
                        List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(orderId,5);
                        if (orderBooks.size()==0){
                            saveOrder(oldOrder,state);
                        }else {
                            saveOrder(oldOrder,oldOrder.getState());//订单还是已经发货状态，只是改变要退货的货物状态而已
                        }
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"退款成功");
                    }
                }
            }
            if (state==7){//确认退货
                OrderBook orderBook = orderBookDao.getByOrderIdAndBookId(orderId,bookId);
                if (orderBook.getState()==6){
                    if ("".equals(bookId)){
                        orderBook.setState(state);
                        //如果一条订单里的货物全部退货成功了就将这条订单设置为  关闭交易
                        List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(orderId);
                        boolean flag = false;
                        for (OrderBook orderBook1:orderBooks){
                            if (orderBook1.getState()!=7){
                                flag=true;
                            }
                        }
                        if (!flag){
                            saveOrder(oldOrder,8);//关闭交易8
                            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"退货成功");
                        }
                    }else {
                        orderBook.setState(state);//该货物状态为退货申请
                        orderBookDao.save(orderBook);
                        //如果这一条订单的所有货物都为退款申请了，那么该条订单的状态就改为退货申请状态，相当于整条订单申请退货
                        List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(orderId,7);
                        if (orderBooks.size()==0){
                            saveOrder(oldOrder,state);
                        }else {
                            saveOrder(oldOrder,oldOrder.getState());//申请退货待商家确认6，只是改变要退货的货物状态而已
                        }
                    }
                }
            }
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"将订单："+orderId+"状态"+getStateStr(oldOrder.getState())+"修改为："+getStateStr(state)+"成功",0, BasicFilter.user_id));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改订单状态成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改订单状态失败");
        }
    }

    /**
     * 保存买家对订单的操作结果
     * @param oldOrder
     * @param state
     */
    private void saveOrder(Order oldOrder,int state){
        oldOrder.setState(state);
        oldOrder.setUpdateDate(new Date());
        oldOrder.setUpdateUser(BasicFilter.user_id);
        orderDao.save(oldOrder);
    }

    /**
     * 将state用数字转换为对应中文名称
     * @param state
     * @return 对应中文名称
     */
    private String getStateStr(int state){
        String str = "";
        switch (state){
            case -1:str = "取消订单";
            break;
            case 0:str = "完成交易";
            break;
            case 1:str = "待付款";
                break;
            case 2:str = "待商家发货";
                break;
            case 3:str = "商家已经发货";
                break;
            case 4:str = "申请退款待商家确认";
                break;
            case 5:str = "退款成功";
                break;
            case 6:str = "申请退货待商家确认";
                break;
            case 7:str = "退货成功";
                break;
            case 8:str = "关闭交易";
            break;
            default:break;
        }
        return str;
    }
    /*@RequestMapping(value = "getAllByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "根据userCode获取所有订单",notes = "根据用户ID获取所有订单信息")
    public String getAllByUserCode(
            @RequestParam("userCode")String userCode,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "updateDate");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<Order> orders = orderDao.getByCreateUser(userCode,pageable);
            List<Object> list = new ArrayList<>();
            for (Order order:orders){
                double price =0;//订单金额
                List<Book> books = new ArrayList<>();
                List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(order.getOrderId());
                for (OrderBook orderBook:orderBooks){
                    Book book = bookDao.getBookByBookId(orderBook.getBookId());
                    int orderNum = orderBook.getBookNum();
                    if (null!=book) {
                        double bookPrice =book.getPrice()*book.getDiscount();//价格*折扣;
                        book.setBookNum(orderNum);
                        books.add(book);
                        price += orderNum*bookPrice;
                    }
                }
                Map map = new HashMap();
                String orderStr = JsonUtil.fromObject(order);
                map.put("books",books);
                map.put("price",price);
                String orderMap = JsonUtil.makeJsonBeanByKey(orderStr,map);
                list.add(orderMap);
            }
            long total = orderDao.countByCreateUser(userCode);//获取查询总数
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return  JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取订单信息失败");
        }
    }*/

    @RequestMapping(value = "getAllByUserCodeAndState",method = RequestMethod.GET)
    @ApiOperation(value = "根据userCode和state获取用户的所有订单",notes = "根根据userCode和state获取所有订单信息")
    public String getAllByUserCodeAndState(
            @RequestParam("userCode")String userCode,
            @RequestParam("state") int state,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "createDate");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<Order> orders = null;
            List<Order> order_list = new ArrayList<>();
            long total=0;//获取查询总数
            if (state==-2){//如果state==-2，就根据userCode查询全部
                 orders = orderDao.getByCreateUser(userCode,pageable);
                 for (Order order:orders){
                     order_list.add(order);
                 }
                 total = orderDao.countByCreateUser(userCode);//获取查询总数
            }else if (state==4 || state ==6){
                orders = orderDao.getByCreateUser(userCode,pageable);
                for (Order order:orders){
                    List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(order.getOrderId());
                    boolean flag = false;
                    for (OrderBook orderBook:orderBooks){
                        if (state ==4 && (orderBook.getState()==4 || orderBook.getState()==5)){
                           flag=true;
                        }else if (state ==6 && (orderBook.getState()==6 || orderBook.getState()==7)){
                            flag=true;
                        }
                    }
                    if (flag){
                        order_list.add(order);
                    }
                }
                total =order_list.size();//获取查询总数
            }else {
                orders = orderDao.getByCreateUseraAndState(userCode,state,pageable);
                for (Order order:orders){
                    order_list.add(order);
                }
                total = orderDao.countByCreateUserAndState(userCode,state);//获取查询总数
            }
            List<Object> list = new ArrayList<>();
            for (Order order:order_list){
                double price =0;//订单金额
                List<String> books = new ArrayList<>();
                List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(order.getOrderId());
                List<OrderBook> orderBooks2 = new ArrayList<>();
               if (state ==4){
                   for (OrderBook orderBook:orderBooks){
                       if (orderBook.getState()==4 || orderBook.getState()==5){
                           orderBooks2.add(orderBook);
                       }
                   }
                   orderBooks = orderBooks2;
               }else if (state ==6){
                   for (OrderBook orderBook:orderBooks){
                       if (orderBook.getState()==6 || orderBook.getState()==7){
                           orderBooks2.add(orderBook);
                       }
                   }
                   orderBooks = orderBooks2;
               }
                for (OrderBook orderBook:orderBooks){
                    int orderNum = orderBook.getBookNum();
                    Book book = bookDao.getBookByBookId(orderBook.getBookId());
                    //根据orderId和bookId拿到货物在订单中的状态（因为订单中可能有的货物进行退款--5/退货--7）
                   OrderBook obook = orderBookDao.getByOrderIdAndBookId(orderBook.getOrderId(),book.getBookId());
                    if (null!=book) {
                        book.setBookNum(orderNum);
                        book.setState(obook.getState());//取orderBook表里的state
                        books.add(JsonUtil.fromObject(book));
                        double bookPrice =book.getPrice()*book.getDiscount();//价格*折扣;
                        price += orderNum*bookPrice;
                    }
                }
                Map map = new HashMap();
                String orderStr = JsonUtil.fromObject(order);
                map.put("price",price);
                map.put("books",books);
                list.add(JsonUtil.makeJsonBeanByKey(orderStr,map));
            }

            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return  JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取订单信息失败");
        }
    }

    @RequestMapping(value = "getTopSell",method = RequestMethod.GET)
    @ApiOperation(value = "获取热销书籍",notes = "获取热销书籍")
    public String getTopSell(
            @RequestParam("typeId") String typeId,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
              try {
                  //
                  List<Object> orderBooks = orderBookDao.getByBookNum(pageNum-1,pageSize);
                  List<Object> list = new ArrayList<>();
                  for (Object object:orderBooks){
                      JSONArray array = null;
                      array = JSONArray.fromObject(object);
                      Map map = new HashMap();
                      for (int i=0;i<array.size();i++){
                          String bookNum = array.get(i).toString();
                          i++;
                          String bookId = array.get(i).toString();
                          Book oldBook = bookDao.getBookByBookId(bookId);
                          if ("".equals(typeId)){
                             oldBook.setBookNum(Integer.parseInt(bookNum));
                              list.add(oldBook);
                          }else {
                              if (oldBook.getTypeId().equals(typeId) || oldBook.getTypeId().startsWith(typeId)){
                                  oldBook.setBookNum(Integer.parseInt(bookNum));
                                  list.add(oldBook);
                              }
                          }
                      }
                  }
                  long total = list.size();
                  long  totalPage = total/pageSize==0? total/pageSize:total/pageSize+1;
                  return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
              }catch (Exception e){
                  e.printStackTrace();
                  return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取热销书失败");
              }
    }
}
