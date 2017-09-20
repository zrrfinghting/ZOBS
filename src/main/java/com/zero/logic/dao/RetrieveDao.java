package com.zero.logic.dao;

import com.zero.logic.domain.Retrieve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * 回收单类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/27
 */
public interface RetrieveDao extends CrudRepository<Retrieve,Integer>{

    /**
     * 分页获回收单信息
     * @param keyWord
     * @param pageable
     * @return 回收单信息
     */
    @Query("select t from Retrieve t where t.retrieverPhone like %?1% or t.retrieveId like %?1% or t.retriever like %?1% or t.retrieverAddress like %?1% or t.orderId like %?1%")
    public Page<Retrieve> findRetrievesByRetrieveId(@Param("keyWord")String keyWord, Pageable pageable);

    /**
     * 分页获取回收单记录数
     * @param keyWord
     * @return 记录数
     */
    @Query("select count(*) from Retrieve t where t.retrieverPhone like %?1% or t.retrieveId like %?1% or t.retriever like %?1% or t.retrieverAddress like %?1% or t.orderId like %?1%")
    public long count(@Param("keyWord")String keyWord);

    /**
     * 根据回收单ID获取回收单信息
     * @param retrieveId
     * @return
     */
    public Retrieve getRetrieveByRetrieveId(String retrieveId);


    /**
     * 根据userCode获取回收单信息
     * @param createUser
     * @param pageable
     * @return 回收单信息
     */
    @Query("select t from Retrieve t where t.createUser=:createUser")
    public Page<Retrieve> findRetrievesByCreateUser(@Param("createUser")String createUser, Pageable pageable);

    /**
     * 根据userCode回去回收单记录
     * @param createUser
     * @return 回收单数量
     */
    @Query("select count(*) from Retrieve t where t.createUser=:createUser")
    public long countByCreateUser(@Param("createUser")String createUser);
}
