package com.mobilecourse.backend.service;

import com.mobilecourse.backend.nosql.elasticsearch.document.EsProduct;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 搜索管理Service
 * Created by macro on 2018/6/19.
 */
public interface EsProductService {
//    /**
//     * 从数据库中导入所有到ES
//     */
//    int importAll();

//    /**
//     * 根据id删除
//     */
//    void delete(int id);

    /**
     * 根据id创建
     */
    EsProduct create(EsProduct esp);

//    /**
//     * 批量删除
//     */
//    void delete(List<Integer> ids);

    /**
     * 根据关键字搜索名称或者副标题
     */
    Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize);

}