package com.zero.logic.dao;
import com.zero.logic.domain.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * 日志类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/9
 */
public interface LogDao extends CrudRepository<Log,Integer>{

    /**
     * 分页获取日志
     * @param keyWord
     * @param pageable
     * @return
     */
    @Query("select t from Log t where t.type=?2 and (t.logId like %?1% or t.logContent like %?1% or t.userCode like %?1%)" )
    public Page<Log> getByPage(String keyWord,int type, Pageable pageable);

    /**
     * 获取模糊查询记录数
     * @param keyWord
     * @return
     */
    @Query("select count(*) from Log t where t.type=?2 and (t.logId like %?1% or t.logContent like %?1% or t.userCode like %?1%)" )
    public long count(@Param("keyWord")String keyWord,@Param("type") int type);
}
