package com.aaxis.microservice.training.demo1.service;

import com.aaxis.microservice.training.demo1.dao.ItemPriceDAO;
import com.aaxis.microservice.training.demo1.domain.ItemPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ItemPriceService {

    @Autowired
    private ItemPriceDAO mItemPriceDAO;

    private static final Logger logger = LoggerFactory.getLogger(ItemPriceService.class);

    public ItemPrice findItemPriceById(String pProductId){
        logger.debug("Query price by product id: {}", pProductId);
        Optional<ItemPrice> optionalItemPrice = mItemPriceDAO.findById(pProductId);
        if(optionalItemPrice.isPresent()){
            return optionalItemPrice.get();
        }
        logger.debug("Get no result for query price by product id: {}", pProductId);
        return null;
    }

    public void initData(){
        mItemPriceDAO.insertItemPrice();
    }

}
