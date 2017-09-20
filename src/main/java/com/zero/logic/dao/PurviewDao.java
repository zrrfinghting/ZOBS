package com.zero.logic.dao;

import com.zero.logic.domain.Purview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 权限接口
 * @auther Deram Zhao
 * @creatTime 2017/6/9
 */
public interface PurviewDao extends CrudRepository<Purview,Integer> {
    /**
     * 根据权限编号获取权限信息
     * @param purviewId
     * @return Purview
     */
    public Purview getPurviewByPurviewId(String purviewId);

    /**
     * 角色模糊分页查询
     * @param keyWord
     * @param pageable
     * @return
     */
    @Query("select t from Purview t where t.purviewName like %?1% or t.purviewId like %?1% or t.purviewDesc like %?1%")
    public Page<Purview> findByPurviewName(@Param("keyWord") String keyWord, Pageable pageable);

    /**
     * 获取模糊查询记录数
     * @param keyWord
     * @return
     */
    @Query("select count(*)from Purview t where t.purviewName like %?1% or t.purviewId like %?1% or t.purviewDesc like %?1%")
    public long count(@Param("keyWord")String keyWord);

    /**
     * 根据权限ID获取权限对应的角色
     * @param purview_id
     * @return 拥有该权限的角色
     */
    @Query(value = "select * from sys_role_purview t where t.purview_id=:purview_id",nativeQuery = true)
    public List<Object> getObj(@Param("purview_id") String purview_id);

    /**
     * 根据角色ID获取权限
     * @param role_id
     * @return 改角色拥有的权限
     */
    @Query(value = "select purview_id from sys_role_purview t where  t.role_id=:role_id",nativeQuery = true)
    public List<Object> getQurview(@Param("role_id") String role_id);

    /**
     * 根据state分页获取权限
     * @param state
     * @param pageable
     * @return 权限信息
     */
    @Query("select t from Purview t where t.state=:state")
    public Page<Purview> findByState(@Param("state") int state,Pageable pageable);
    @Query("select count(*)from Purview t where t.state=:state")
    public long countByState(@Param("state") int state);
}
