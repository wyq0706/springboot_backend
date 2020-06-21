package com.mobilecourse.backend.service;

import com.mobilecourse.backend.nosql.elasticsearch.document.EsProduct;
import org.elasticsearch.search.aggregations.metrics.percentiles.hdr.InternalHDRPercentiles;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 搜索管理Service
 * Created by macro on 2018/6/19.
 */
public interface EsProductService {

    /**
     * 根据id获取用户
     */
    EsProduct get(int id);

    /**
     * 创建
     */
    EsProduct create(EsProduct esp);

    /**
     * 根据id删除
     */
    void delete(Integer Id);

    /**
     * 根据关键字搜索名称或者副标题
     */
    Iterable<EsProduct> search(String keyword, Integer pageNum, Integer pageSize);


}