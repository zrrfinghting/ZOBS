package com.zero.logic.dao;

import com.zero.logic.domain.Role;
import com.zero.logic.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * 角色类接口
 * @auther Deram Zhao
 * @creatTime 2017/6/8
 */
public interface RoleDao extends CrudRepository<Role,Integer> {
    /**
     * 根据角色编号获取角色信息
     * @param roleId
     * @return
     */
    public Role getRoleByRoleId(String roleId);

    /**
     * 角色模糊分页查询
     * @param keyWord
     * @param pageable
     * @return
     */
   @Query("select t from Role t where t.roleName like %?1% or t.roleId like %?1% or t.roleDesc like %?1% or t.createUser like %?1%")
   public Page<Role> findByRoleName(@Param("keyWord") String keyWord, Pageable pageable);

    /**
     * 获取模糊查询记录数
     * @param keyWord
     * @return
     */
    @Query("select count(*) from Role t where t.roleName like %?1% or t.roleId like %?1% or t.roleDesc like %?1% or t.createUser like %?1%" )
    public long count(@Param("keyWord")String keyWord);


    /**
     * 根据角色ID查找引用改该角色的用户
     * @param role_id
     * @return 用户与角色对应中间表结果集
     */
    @Query(value = "select * from sys_user_role t where t.role_id=:role_id",nativeQuery = true)
    public List<Object> getObj(@Param("role_id") String role_id);

    /**
     * 根据state分页获取角色
     * @param state
     * @return
     */
    @Query("select t from Role t where t.state=:state")
    public Page<Role> findByState(@Param("state") int state,Pageable pageable);
    @Query("select count(*) from Role t where t.state=:state")
    public long countByState(@Param("state") int state);
}
