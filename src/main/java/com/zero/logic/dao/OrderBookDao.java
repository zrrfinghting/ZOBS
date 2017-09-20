package com.zero.logic.dao;

import com.zero.logic.domain.OrderBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单图书类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/19
 */
public interface OrderBookDao extends CrudRepository<OrderBook,Integer>{

    /**
     * 根据图书ID获取订单图书信息
     * @param bookId
     * @return 订单图书信息
     */
    public OrderBook getOrderBookByBookId(String bookId);

    /**
     * 根据订单ID获取订单图书信息
     * @param orderId
     * @return list 订单图书信息
     */
    public List<OrderBook> getOrderBooksByOrderId(String orderId);

    /**
     * 根据订单ID分页获取订单图书信息
     * @param orderId
     * @param pageable
     * @return 订单图书信息
     */
    @Query("select t from OrderBook t where t.orderId=:orderId")
    public Page<OrderBook> findOrderBooksByOrderId(@Param("orderId")String orderId, Pageable pageable);

    /**
     * 根据订单ID分页获取订单图书信息记录
     * @param orderId
     * @return 记录数量
     */
    @Query("select count(*) from OrderBook t where t.orderId=:orderId")
    public long count(@Param("orderId")String orderId);


    /**
     * 根据已经完场的订单来统计货物销量排行榜
     * @param pagaNum
     * @param pageSize
     * @return
     */
    @Query(value = "SELECT booknum,bookId from sys_order o,sys_order_book ob where o.state=0 AND ob.orderId=o.orderid ORDER BY ob.booknum DESC limit ?1,?2",nativeQuery = true)
    public List<Object> getByBookNum(int pagaNum,int pageSize);

    /**
     * 根据 orderId和bookId获取orderBook
     * @param orderId
     * @param bookId
     * @return orderBook
     */
    @Query("select t from OrderBook t where t.orderId=:orderId and t.bookId=:bookId")
    public OrderBook getByOrderIdAndBookId(@Param("orderId") String orderId,@Param("bookId") String bookId);

    /**
     * 根据orderID获取 orderBook
     * @param orderId
     * @return orderBook
     */
    @Query("select t from OrderBook t where t.orderId=:orderId")
    public List<OrderBook> findOrderBooksByOrderId(@Param("orderId")String orderId);


    /**
     * 根据订单ID和订单状态查询书籍是否存在
     * @param orderId
     * @param state
     * @return
     */
    @Query("select t from OrderBook t where t.orderId=:orderId and t.state<>:state")
    public List<OrderBook> getOrderBooksByOrderId(@Param("orderId")String orderId,@Param("state")int state);


    /**
     * 根据orderId删除 orderBook里的信息
     * @param orderId
     */
    @Modifying
    @Transactional
    public void deleteByOrderId(String orderId);

}
