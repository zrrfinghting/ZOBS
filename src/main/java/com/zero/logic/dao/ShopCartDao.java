package com.zero.logic.dao;

import com.zero.logic.domain.ShopCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/7
 */
public interface ShopCartDao extends CrudRepository<ShopCart,Integer>{

    /**
     * 根据createUser分页获取购物车信息（createUser对应用户表的userCode）
     * @param userCode
     * @param pageable
     * @return
     */
    @Query("select t from ShopCart t where t.userCode=:userCode")
    public Page<ShopCart> getShopCartsByCreateUser(@Param("userCode") String userCode, Pageable pageable);

    @Query("select count(*) from ShopCart t where t.userCode=:userCode")
    public long countByCreateUser(@Param("userCode") String userCode);

    /**
     * 根据用户编号Id清空购物车里的货物信息
     * @param userCode
     * @return
     */
    @Transactional
    @Modifying
    @Query("delete from ShopCart t where t.userCode=:userCode")
    public void deleteByUsercode(@Param("userCode") String userCode);

    /**
     * 根据用户编号获取购物车里属于该用户的货物信息
     * @param userCode
     * @return 购物车里的货物信息
     */
    @Query("select t  from ShopCart t where t.userCode=:userCode and t.bookId=:bookId")
    public ShopCart getShopCartsByUsercodeandAndBookId(@Param("userCode") String userCode,@Param("bookId") String bookId);

}
