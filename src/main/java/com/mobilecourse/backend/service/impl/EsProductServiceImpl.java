package com.mobilecourse.backend.service.impl;

import com.mobilecourse.backend.dao.EsDao;
import com.mobilecourse.backend.nosql.elasticsearch.document.EsProduct;
import com.mobilecourse.backend.nosql.elasticsearch.repository.EsProductRepository;
import com.mobilecourse.backend.service.EsProductService;
import com.sun.jna.Library;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 搜索管理Service实现类
 */
@Service
public class EsProductServiceImpl implements EsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsProductServiceImpl.class);
    @Autowired
    private EsDao productDao;
    @Autowired
    private EsProductRepository productRepository;

    @Override
    public EsProduct get(int id) {
        EsProduct esp=productRepository.queryProductById(id);
        return esp;
    }

    @Override
    public void delete(Integer id) {
        productRepository.deleteById(id);
    }


    @Override
    public EsProduct create(EsProduct esp) {
        EsProduct result = null;
        result = productRepository.save(esp);
        return result;
    }


    @Override
    public Iterable<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        QueryStringQueryBuilder builder = new QueryStringQueryBuilder(keyword);
        Iterable<EsProduct> search = productRepository.search(builder);
        return search;
//        Pageable pageable = PageRequest.of(pageNum, pageSize);
//        return productRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
    }

}
