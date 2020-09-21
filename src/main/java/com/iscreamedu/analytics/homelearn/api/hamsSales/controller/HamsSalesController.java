package com.iscreamedu.analytics.homelearn.api.hamsSales.controller;

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.iscreamedu.analytics.homelearn.api.hamsSales.service.HamsSalesService;

/**
 * HAMS Sales API Controller
 * @author shoshu
 * @since 2020.09.21
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일      		수정자		수정내용
 *  ----------  --------    --------------------------
 *  2020.09.21	shoshu		초기생성 
 *  </pre>
 */
public class HamsSalesController {
	private static final Logger LOGGER = LoggerFactory.getLogger(HamsSalesController.class);
	
	@Autowired
	private HamsSalesService hamsSalesService;
	public HttpHeaders headers;
	public LinkedHashMap body;
	
	public HamsSalesController() {
		headers = new HttpHeaders();
		headers.setContentType(new MediaType("application","json"));
		headers.setAccessControlAllowOrigin("*");
		headers.setAccessControlAllowCredentials(true);
	}
}
