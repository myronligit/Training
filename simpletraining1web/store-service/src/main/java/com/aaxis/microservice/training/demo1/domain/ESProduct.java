package com.aaxis.microservice.training.demo1.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "training", type = "product")
public class ESProduct implements Serializable {

    private String id;
    private String name;
    private Integer priority;
    private Date createdDate;
    private double price;
    private int stock;

    public String getId() {
        return id;
    }

    public void setId(String pId) {
        id = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double pPrice) {
        price = pPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int pStock) {
        stock = pStock;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer pPriority) {
        priority = pPriority;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date pCreatedDate) {
        createdDate = pCreatedDate;
    }
}
