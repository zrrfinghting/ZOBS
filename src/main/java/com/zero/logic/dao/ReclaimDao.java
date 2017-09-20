package com.zero.logic.dao;

import com.zero.logic.domain.Reclaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * 在线回收实体接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/8/2
 */
public interface ReclaimDao extends CrudRepository<Reclaim, Integer> {
    /**
     * 根据回收单编号获取回收单信息
     * @param reclaimId
     * @return
     */
    public Reclaim queryByReclaimId(String reclaimId);

    /**
     * 根据 关键字和者回收单状态 分页获取回收单信息
     * @param keyword
     * @param userCode
     * @param state
     * @param pageable
     * @return
     */
    @Query("select t from Reclaim t where t.createUser=?2 and t.state=?3 and (t.reclaimId like %?1% or t.createUser like %?1% or t.sellerName like %?1% or t.sellerPhone like %?1% or t.goodsDsc like %?1%)")
    public Page<Reclaim> findByReclaimIdAndState(@Param("keyword")String keyword, @Param("userCode")String userCode, @Param("state")int state, Pageable pageable);

    @Query("select count(*) from Reclaim t where t.createUser=?2 and t.state=?3 and (t.reclaimId like %?1% or t.createUser like %?1% or t.sellerName like %?1% or t.sellerPhone like %?1% or t.goodsDsc like %?1%)")
    public long countByReclaimIdAndState(@Param("keyword")String keyword, @Param("userCode")String userCode, @Param("state")int state);


    /**
     * 根据关键字  分页获取回收信息
     * @param keyword
     * @param userCode
     * @param pageable
     * @return
     */
    @Query("select t from Reclaim t where t.createUser=?2  and (t.reclaimId like %?1% or t.createUser like %?1% or t.sellerName like %?1% or t.sellerPhone like %?1% or t.goodsDsc like %?1%)")
    public Page<Reclaim> findByReclaimId(@Param("keyword")String keyword,@Param("userCode")String userCode, Pageable pageable);
    @Query("select count(*) from Reclaim t where t.createUser=?2  and (t.reclaimId like %?1% or t.createUser like %?1% or t.sellerName like %?1% or t.sellerPhone like %?1% or t.goodsDsc like %?1%)")
    public long countByReclaimId(@Param("keyword")String keyword, @Param("userCode")String userCode);


    /**
     * 根据关键字 分页获取回收单信息（后端）
     * @param keyword
     * @param pageable
     * @return
     */
    @Query("select t from Reclaim t where t.reclaimId like %?1% or t.createUser like %?1% or t.sellerName like %?1% or t.sellerPhone like %?1% or t.goodsDsc like %?1%")
    public Page<Reclaim> findAllByKeyword(String keyword, Pageable pageable);

    @Query("select count(*) from Reclaim t where t.reclaimId like %?1% or t.createUser like %?1% or t.sellerName like %?1% or t.sellerPhone like %?1% or t.goodsDsc like %?1%")
    public long countAllByKeyword(String keyword);


    /**
     * 根据关键字分页获取回收单 (后端)
     * @param keyword
     * @param state
     * @param pageable
     * @return
     */
    @Query("select t from Reclaim t where t.state=?2 and (t.reclaimId like %?1% or t.createUser like %?1% or t.sellerName like %?1% or t.sellerPhone like %?1% or t.goodsDsc like %?1%)")
    public Page<Reclaim> findAllByStateAndKeyword(String keyword,int state, Pageable pageable);

    @Query("select count(*) from Reclaim t where  t.state=?2 and (t.reclaimId like %?1% or t.createUser like %?1% or t.sellerName like %?1% or t.sellerPhone like %?1% or t.goodsDsc like %?1%)")
    public long countAllByStateAndKeyword(String keyword,int state);
}
