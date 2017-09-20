package com.zero.logic.dao;

import com.zero.logic.domain.Order;
import com.zero.logic.domain.OrderBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 订单类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/19
 */
public interface OrderDao extends CrudRepository<Order, Integer> {

    /**
     * 根据orderID获取定的信息
     *
     * @param orderId
     * @return 订单信息
     */
    @Query("select t from Order t where t.orderId=:orderId")
    public Order findOrderByOrderId(@Param("orderId") String orderId);

    /**
     * 根据关键字分页获取订单
     *
     * @param keyWord
     * @param pageable
     * @return 订单信息
     */
    @Query("select t from Order t where t.orderId like %?1% or t.address like %?1% or t.receiver like %?1% or t.phone like %?1% or t.delivery like %?1%")
    public Page<Order> getByOrderId(@Param("keyWord") String keyWord, Pageable pageable);

    /**
     * 获取关键字查询订单分页记录数
     *
     * @param keyWord
     * @return 订单记录数
     */
    @Query("select count(*) from Order t where t.orderId like %?1% or t.address like %?1% or t.receiver like %?1% or t.phone like %?1% or t.delivery like %?1%")
    public long count(@Param("keyWord") String keyWord);


    /**
     * 根据用户ID获取订单信息
     *
     * @param userCode
     * @param pageable
     * @return 订单信息
     */
    @Query("select t from Order t where t.createUser=:userCode")
    public Page<Order> getByCreateUser(@Param("userCode") String userCode, Pageable pageable);

    @Query("select count (*) from Order t where t.createUser=:userCode")
    public long countByCreateUser(@Param("userCode") String userCode);


    /**
     * 根据用户ID和订单state获取订单信息
     *
     * @param userCode
     * @param state
     * @param pageable
     * @return 订单信息
     */
    @Query("select t from Order t where t.createUser=:userCode and t.state=:state")
    public Page<Order> getByCreateUseraAndState(@Param("userCode") String userCode, @Param("state") int state, Pageable pageable);

    @Query("select count (*) from Order t where t.createUser=:userCode and t.state=:state")
    public long countByCreateUserAndState(@Param("userCode") String userCode, @Param("state") int state);


}
