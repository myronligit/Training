package com.aaxis.microservice.training.demo1.controller;

import com.aaxis.microservice.training.demo1.domain.ESProduct;
import com.aaxis.microservice.training.demo1.service.ESProductService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/es")
public class ESProductController {

    @Autowired
    private ESProductService esProductService;


    @GetMapping("/initData")
    public String initData() {
        esProductService.initData();
        return "redirect:/login";
    }

    @GetMapping("/searchPage")
    public String loadsSearchPage(){
        return "/es_search_page";
    }

    @RequestMapping("/search")
    public String search(HttpServletRequest request){
        //int pageNumber = request.getParameter("page") == null ? 1 : Integer.valueOf(request.getParameter("page"));
        String name = request.getParameter("name");
        List<ESProduct> result = esProductService.search(name);
        request.setAttribute("productResult", result);
        return "es_search";
    }
}
