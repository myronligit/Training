package com.aaxis.microservice.training.demo1.dao;

import com.aaxis.microservice.training.demo1.domain.ESProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface ESProductDao extends ElasticsearchRepository<ESProduct, String> {
}
