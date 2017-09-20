package com.zero.logic.dao;
import com.zero.logic.domain.BookType;
import org.hibernate.sql.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 图书分类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/15
 */
public interface BookTypeDao extends CrudRepository<BookType,Integer> {

    /**
     * 根据图书分类id获取图书分类
     * @param typeId
     * @return
     */
    public BookType getBookTypeByTypeId(String typeId);

    /**
     * 根据分类父ID获取分类
     * @param parent 父类Id
     * @return 分类信息
     */
    public BookType getBookTypeByParent(String parent);

    /**
     * 根据关键字分页查询分类信息
     * @param keyWord
     * @param pageable
     * @return 分类信息
     */
    @Query("select t from BookType t where t.typeId like %?1% or t.typeName like %?1% or t.createUser like %?1% or t.typeDesc like %?1%")
    public Page<BookType> findBookTypesByTypeName(@Param("keyWord")String keyWord, Pageable pageable);

    @Query("select count(*) from BookType t where t.typeId like %?1% or t.typeName like %?1% or t.createUser like %?1% or t.typeDesc like %?1%")
    public long countByTypeName(@Param("keyWord") String keyWord);

}
