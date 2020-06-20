package com.mobilecourse.backend.nosql.elasticsearch.repository;

import com.mobilecourse.backend.nosql.elasticsearch.document.EsProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ES操作类
 */
public interface EsProductRepository extends ElasticsearchRepository<EsProduct, Integer> {
    /**
     * 搜索查询
     *
     * @param name
     * @param subTitle
     * @param keywords
     * @param page              分页信息
     * @return
     */
    Page<EsProduct> findByNameOrSubTitleOrKeywords(String name, String subTitle, String keywords, Pageable page);

    EsProduct queryProductById(Integer id);


}
