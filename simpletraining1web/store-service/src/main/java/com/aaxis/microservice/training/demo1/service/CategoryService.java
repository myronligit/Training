package com.aaxis.microservice.training.demo1.service;

import com.aaxis.microservice.training.demo1.dao.CategoryDao;
import com.aaxis.microservice.training.demo1.dao.UserDao;
import com.aaxis.microservice.training.demo1.domain.Category;
import com.aaxis.microservice.training.demo1.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryDao pCategoryDao;

    @Autowired
    private Environment env;

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public void initData(){
        logger.info("Start to initial category data...");
        String[] categoryIds = env.getProperty("categoryIds").split(",");
        logger.debug("The all category ids are: ", categoryIds);

        for(String categoryId : categoryIds){
            if(pCategoryDao.findById(categoryId).isPresent()){
                continue;
            }
            Category category = new Category();
            category.setId(categoryId);
            category.setName("Category_"+categoryId);
            pCategoryDao.save(category);
        }
        logger.info("End to initial category data...");
    }

    public List<Category> findAllCategories(){
        return pCategoryDao.findAll();
    }
}
