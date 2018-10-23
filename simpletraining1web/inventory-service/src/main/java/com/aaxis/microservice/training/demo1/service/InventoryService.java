package com.aaxis.microservice.training.demo1.service;


import com.aaxis.microservice.training.demo1.dao.InventoryDAO;
import com.aaxis.microservice.training.demo1.domain.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private InventoryDAO mInventoryDAO;

    public void initData(){
        mInventoryDAO.addItemInventory();
    }

    @Cacheable(value = "inventory", key = "'inventory'.concat(#pProductId)")
    public Inventory findInventoryById(String pProductId){
        logger.debug("Query inventory by product id: {}", pProductId);
        Optional<Inventory> optionalInventory = mInventoryDAO.findById(pProductId);
        if(optionalInventory.isPresent()){
            return optionalInventory.get();
        }
        logger.debug("Get no result when query inventory by product id: {}", pProductId);
        return null;
    }
}
