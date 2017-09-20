package com.zero.logic.dao;

import com.zero.logic.domain.SellerInfo;
import org.springframework.data.repository.CrudRepository;

/**
 * 商家信息实体接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/31
 */
public interface SellerIfoDao extends CrudRepository<SellerInfo,Integer> {

    public SellerInfo getBySellerCode(String sellerCode);
}
