package com.aaxis.microservice.training.demo1.service;

import com.aaxis.microservice.training.demo1.dao.CategoryDao;
import com.aaxis.microservice.training.demo1.dao.ProductDao;
import com.aaxis.microservice.training.demo1.domain.Category;
import com.aaxis.microservice.training.demo1.domain.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    private CategoryDao mCategoryDao;

    @Autowired
    private ProductDao mProductDao;

    @Autowired
    private RestTemplateBuilder mRestTemplateBuilder;

    @Autowired
    private Environment env;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DispatcherService dispatcherService;

    private static final int PRODUCT_BATCH_SIZE = 1000;

    private static final String PRICE_CACHE_KEY_PREFIX = "price_";

    private static final String INVENTORY_CACHE_KEY_PREFIX = "inventory_";

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public void initData() {
        logger.info("Start to initial data...");
        List<Category> categories = mCategoryDao.findAll();

        if (categories == null) {
            logger.warn("Category list is null.");
            return;
        }
        int maxProductCoundInCategory = Integer.parseInt(env.getProperty("maxProductCoundInCategory"));

        String checkProductExistBeforeAdding = env.getProperty("checkProductExistBeforeAdding");

        for (Category category : categories) {

            int randomProductSize = new Random().nextInt(maxProductCoundInCategory / 2) + maxProductCoundInCategory / 2;

            List<Product> productist = new ArrayList<>(PRODUCT_BATCH_SIZE);

            for (int i = 1; i <= randomProductSize; i++) {
                String productId = category.getId() + "_" + i;
                String productName = RandomStringUtils.randomAlphanumeric(32);
                if ("true".equalsIgnoreCase(checkProductExistBeforeAdding) && mProductDao.findById(productId).isPresent()) {
                    break;
                }
                Product product = new Product();
                product.setId(productId);
                product.setName(productName);
                product.setPriority(new Random().nextInt(100));
                Date date = randomDate("2010-01-01","2018-01-01");
                product.setCreatedDate(date);
//                product.setPrice(new BigDecimal(new Random().nextDouble() * 1000).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue());
                product.setCategory(category);
//                mProductDao.save(product);
                productist.add(product);

                if(productist.size() % PRODUCT_BATCH_SIZE == 0){
                    logger.debug("Saving the product data...");
                    mProductDao.saveAll(productist);
                    productist.clear();
                }
            }

            if(!productist.isEmpty()){
                logger.debug("Saving the product data...");
                mProductDao.saveAll(productist);
                productist.clear();
            }
            logger.info("End to initial data...");
        }




        restTemplate.getForObject("http://localhost:8081/price/initData", Map.class);
        restTemplate.getForObject("http://localhost:8082/inventory/initData", Map.class);
    }

    public List<Product> findProductsByCategoryId(String categoryId) {
        return mProductDao.findProductsByCategory_Id(categoryId);
    }

    public Page<Product> findProductsInPLP(String categoryId, int page, String sortName, String sortValue) {
        long startTime = System.currentTimeMillis();
        logger.debug("Searching the products under category {} in page {} order by {} with {}", new Object[]{categoryId, page, sortName, sortValue});

        Specification<Product> spec = (Root<Product> r, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            Path<Category> name = r.get("category");
            Predicate p = cb.equal(name.as(Category.class), mCategoryDao.findById(categoryId).get());
            return p;
        };

        Pageable pageable = null;

        if (sortName != null) {
            Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue) ? QSort.Direction.ASC : QSort.Direction.DESC, sortName);
            pageable = new PageRequest(page-1, 20, sort);
        } else {
            pageable = new PageRequest(page-1, 20);
        }

        Page<Product> pageResult = mProductDao.findAll(spec, pageable);
        addPriceAndInventory(pageResult.getContent());
        long cost = System.currentTimeMillis()-startTime;
        logger.debug("The search cost: " + cost);
        return pageResult;
    }

    public Page<Product> searchProducts(int page, String productId, String name, String sortName, String sortValue) {
        long startTime = System.currentTimeMillis();
        logger.debug("Searching the products by productId {} and name {} in page {} order by {} with {}", new Object[]{productId, name, page, sortName, sortValue});

        Specification<Product> spec = (Root<Product> r, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            if (StringUtils.isNotBlank(productId)) {
                Predicate p = cb.like(r.get("id"), "%" + productId + "%");
                predicateList.add(p);
            }
            if (StringUtils.isNotBlank(name)) {
                Predicate p = cb.like(r.get("name"), "%" + name + "%");
                predicateList.add(p);
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };

        Pageable pageable = null;

        if (sortName != null) {
            Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue) ? QSort.Direction.ASC : QSort.Direction.DESC, sortName);
            pageable = new PageRequest(page-1, 20, sort);
        } else {
            pageable = new PageRequest(page-1, 20);
        }

        Page<Product> pageResult = mProductDao.findAll(spec, pageable);
        addPriceAndInventory(pageResult.getContent());
        long cost = System.currentTimeMillis()-startTime;
        logger.debug("The search cost: " + cost);
        return pageResult;
    }

    public void addPriceAndInventory(List<Product> products) {
        logger.debug("Add price and inventory to product");
        products.forEach(product -> {
            product.setPrice(getProductPrice(product.getId()));
            product.setStock(getProductInventory(product.getId()));
        });
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return mRestTemplateBuilder.build();
    }

    public double getProductPrice(String pProductId) {
        Double price = dispatcherService.getPriceFromService(pProductId);
        return price;
    }

    public int getProductInventory(String pProductId) {
        Integer stock = dispatcherService.getInventoryFromService(pProductId);
        return stock;
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

