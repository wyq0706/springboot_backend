package com.mobilecourse.backend.service.impl;

import com.mobilecourse.backend.dao.EsDao;
import com.mobilecourse.backend.nosql.elasticsearch.document.EsProduct;
import com.mobilecourse.backend.nosql.elasticsearch.repository.EsProductRepository;
import com.mobilecourse.backend.service.EsProductService;
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

//    @Override
//    public int importAll() {
//        List<EsProduct> esProductList = productDao.getAllEsProductList(null);
//        Iterable<EsProduct> esProductIterable = productRepository.saveAll(esProductList);
//        Iterator<EsProduct> iterator = esProductIterable.iterator();
//        int result = 0;
//        while (iterator.hasNext()) {
//            result++;
//            iterator.next();
//        }
//        return result;
//    }

//    @Override
//    public void delete(int id) {
//        productRepository.deleteById(id);
//    }

    @Override
    public EsProduct create(EsProduct esp) {
        EsProduct result = null;
        result = productRepository.save(esp);
        return result;
    }

//    @Override
//    public void delete(List<Integer> ids) {
//        if (!CollectionUtils.isEmpty(ids)) {
//            List<EsProduct> esProductList = new ArrayList<>();
//            for (int id : ids) {
//                EsProduct esProduct = new EsProduct();
//                esProduct.setId(id);
//                esProductList.add(esProduct);
//            }
//            productRepository.deleteAll(esProductList);
//        }
//    }

    @Override
    public Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return productRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
    }

}
