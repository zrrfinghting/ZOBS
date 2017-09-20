package com.zero.logic.dao;

import com.zero.logic.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;


/**
 * 图书类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
public interface BookDao extends CrudRepository<Book,Integer> {

    /**
     * 模糊查询分页获取图书
     * @param keyWord
     * @param pageable
     * @return books
     */
    @Query("select t from Book t where t.bookName like %?1% or t.typeId like %?1% or t.bookId like %?1% or t.author like %?1% or t.bookDesc like %?1% ")
    Page<Book> findByBookName(@Param("keyWord")String keyWord, Pageable pageable);

    /**
     * 模糊查询图书数量
     * @param keyWord
     * @return 图书数量
     */
    @Query("select count (*)from Book t where t.bookName like %?1% or t.typeId like %?1%  or t.bookId like %?1% or t.author like %?1% or t.bookDesc like %?1% ")
    public long count(@Param("keyWord")String keyWord);

    /**
     * 根据分类ID分页获取books，
     * @param typeId
     * @param pageable
     * @return books
     */
    @Query("select  t from Book t where t.typeId like %?1%")
    Page<Book> findBookByTypeId(@Param("typeId") String typeId, Pageable pageable);

    /**
     * 根据分类ID获取图书数量
     * @param typeId
     * @return 图书数量
     */
    @Query("select  count(*) from Book t where t.typeId like %?1%")
    public long countAllByTypeId(@Param("typeId") String typeId);
    /**
     * 根据图书编号获取图书
     * @param bookId
     * @return
     */
    @Query("select t from Book t where t.bookId=:bookId")
    public Book getBookByBookId(@Param("bookId")String bookId);

    /**
     * 根据图书分类编号获取图书
     * @param typeId
     * @return 图书
     */
    @Query("select t from Book t where t.typeId=:typeId")
    public List<Book> getBookByTypeId(@Param("typeId") String typeId);

    /**
     * 分页获取折扣货物，按照打折力度进行排序
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Query(value = "select * from sys_book t where 0<t.discount and t.discount<1 ORDER BY t.discount DESC limit ?1,?2",nativeQuery = true)
    public List<Book> getBooksBy(int pageNum,int pageSize);

    /**
     * 获取打折的货物数量
     * @return 打折的货物数量
     */
    @Query(value = "select count(*) from sys_book t where 0<t.discount and t.discount<1",nativeQuery = true)
    public long countByDiscount();


    /**
     * 以分类ID和discount作为条件分页模糊查询折扣货物
     * @param pageNum
     * @param pageSize
     * @param typeId
     * @return 属于该分类的折扣货物
     */
    @Query(value = "select * from sys_book t where t.typeId like %?3% and 0<t.discount and t.discount<1 ORDER BY t.discount DESC limit ?1,?2",nativeQuery = true)
    public List<Book> getByDiscountAndTypeId(int pageNum,int pageSize,String typeId);
    @Query("select count (*) from Book t where t.typeId like %?1% and 0<t.discount and t.discount<1 ")
    public long countBookByDiscountAndTypeId(String typeId);

    /**
     * 根据state上架下架分页获取book
     * @param state
     * @param pageable
     * @return book
     */
    @Query("select t from  Book t where t.state=:state")
    public Page<Book> findByState(@Param("state") int state,Pageable pageable);
    @Query("select count(*) from Book t where t.state=:state")
    public long countByState(@Param("state") int state);


    /**
     * 分类和价格作为条件模糊查询图书
     * @param keyWord
     * @param typeId
     * @param
     * @return 图书
     */
    //升序
    @Query(value = "select * from sys_book t where t.typeId like %?2% and t.price*t.discount>=?3 and t.price*t.discount <=?4 and (t.bookName like %?1% or t.author like %?1% or t.bookDesc like %?1%) ORDER BY t.price*t.discount ASC limit ?5,?6",nativeQuery = true)
    List<Book> findBooksByTypeIdASC(String keyWord,String typeId,double beginPrice,double finishPrice,int pageNum,int pageSize);
    //降序
    @Query(value = "select * from sys_book t where t.typeId like %?2% and t.price*t.discount>=?3 and t.price*t.discount <=?4 and (t.bookName like %?1% or t.author like %?1% or t.bookDesc like %?1%) ORDER BY t.price*t.discount DESC limit ?5,?6",nativeQuery = true)
    List<Book> findBooksByTypeIdDESC(String keyWord,String typeId,double beginPrice,double finishPrice,int pageNum,int pageSize);
    @Query("select count (*) from Book t where  t.typeId like %?2% and t.price*t.discount>=?3 and t.price*t.discount <=?4 and (t.bookName like %?1% or t.author like %?1% or t.bookDesc like %?1%)")

    public long countByTypeId(@Param("keyWord")String keyWord,@Param("typeId")String typeId,@Param("beginPrice")double beginPrice,@Param("finishPrice")double finishPrice);

}
