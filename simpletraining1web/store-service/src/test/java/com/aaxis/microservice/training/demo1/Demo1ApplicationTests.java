package com.aaxis.microservice.training.demo1;

import com.aaxis.microservice.training.demo1.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Demo1Application.class, MockServletContext.class})
@EnableWebMvc
public class Demo1ApplicationTests extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	private static final Logger logger = LoggerFactory.getLogger(Demo1ApplicationTests.class);

	@Before
	public void setup(){
		mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void contextLoads() {
	}

	/*@Test
	public void testLogin() throws Exception{
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/rest/doLogin")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("username","test5")
				.param("password", "123456"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		logger.info("login result : " + mvcResult.getResponse().getContentAsString());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testRegist() throws Exception{
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/rest/doRegist")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("username", "test5")
				.param("password", "123456"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		logger.info("regist result : " + mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testSearch() throws Exception{
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/product/search")
				.param("productId","A_111").param("name","")).andReturn();
		int status = mvcResult.getResponse().getStatus();
		Assert.assertEquals(200,status);
		logger.info("search result : " + mvcResult.getResponse().getContentAsString());
	}*/

}
