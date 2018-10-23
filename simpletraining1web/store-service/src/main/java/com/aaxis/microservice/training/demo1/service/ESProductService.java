package com.aaxis.microservice.training.demo1.service;

import com.aaxis.microservice.training.demo1.dao.ESProductDao;
import com.aaxis.microservice.training.demo1.domain.ESProduct;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ESProductService {

    @Autowired
    private ESProductDao esProductDao;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ESProductService.class);

    public void initData() {
        logger.info("Start to init ES data...");
        for (int i = 0; i < 1000; i++){
            ESProduct esProduct = new ESProduct();
            esProduct.setId("es_" + i);
            esProduct.setName(RandomStringUtils.randomAlphanumeric(32));
            esProduct.setPriority(new Random().nextInt(100));
            esProduct.setCreatedDate(randomDate("2010-01-01","2018-01-01"));
            esProduct.setPrice(new Random().nextDouble());
            esProduct.setStock(new Random().nextInt(600));
            esProductDao.save(esProduct);
        }
        logger.info("End to init ES data...");
    }

    public void saveProduct(ESProduct esProduct){
        esProductDao.save(esProduct);
    }

    public AggregatedPage<ESProduct> search(int pageNumber, String sort, String searchTerm){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("name", "*" + searchTerm + "*"));
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("training")
                .withTypes("product")
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(pageNumber, 10))
                .withSort(SortBuilders.fieldSort(sort))
                .build();
        return elasticsearchTemplate.queryForPage(searchQuery, ESProduct.class);
    }

    public List search(String searchTerm){
        Criteria c = new Criteria("name").contains(searchTerm);
        return elasticsearchTemplate.queryForList(new CriteriaQuery(c), ESProduct.class);
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }

    private static Date randomDate(String beginDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
