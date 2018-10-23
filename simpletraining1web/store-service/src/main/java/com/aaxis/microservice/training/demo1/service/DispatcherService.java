package com.aaxis.microservice.training.demo1.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DispatcherService {

    @Autowired
    private RestTemplateBuilder mRestTemplateBuilder;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return mRestTemplateBuilder.build();
    }

    private Logger logger = LoggerFactory.getLogger(DispatcherService.class);

    @HystrixCommand(fallbackMethod = "fallBackPrice",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "30000"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
    }, threadPoolKey = "priceThreadPool", threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "3")
    })
    public double getPriceFromService(String pProductId){
        Double price = (Double) ((Map) restTemplate.getForObject("http://PRICE-SERVER/price/" + pProductId, Map.class)).get("price");
        return price;
    }

    @HystrixCommand(fallbackMethod = "fallBackInventory",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "30000"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
    }, threadPoolKey = "inventoryThreadPool", threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "3")
    })
    public int getInventoryFromService(String pProductId){
        Integer stock = (Integer) ((Map) restTemplate.getForObject("http://INVENTORY-SERVER/inventory/" + pProductId, Map.class)).get("stock");
        return stock;
    }

    public double fallBackPrice(String pProductId) {
        logger.warn("Price service down, provide fall back service.");
        return 0.00;
    }

    public int fallBackInventory(String pProductId) {
        logger.warn("Inventory service down, provide fall back service.");
        return 0;
    }
}
