package com.zero.logic.dao;

import com.zero.logic.domain.RetrieveBook;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 回收单图书接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/27
 */
public interface RetrieveBookDao extends CrudRepository<RetrieveBook,Integer>{

    /**
     * 根据回收单ID获取回收单图书信息
     * @param retrieveId
     * @return 回收单图书信息
     */
    public List<RetrieveBook> getRetrieveBooksByRetrieveId(String retrieveId);

    /**
     * 根据图书ID获取回收单图书信息
     * @param bookId
     * @return 回收单图书信息
     */
    public RetrieveBook getRetrieveBookByBookId(String bookId);

}
