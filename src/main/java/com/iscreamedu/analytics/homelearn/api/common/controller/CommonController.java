package com.iscreamedu.analytics.homelearn.api.common.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iscreamedu.analytics.homelearn.api.common.service.CacheService;
import com.iscreamedu.analytics.homelearn.api.common.util.ValidationCode;

/**
 * Common controller
 * @author hy
 * @since 2019.09.05
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일      		수정자		수정내용
 *  ----------  --------    ---------------------------
 *  2019.09.05	hy			초기생성 
 *  </pre>
 */
@RestController
public class CommonController implements ErrorController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonController.class);
	
	@Autowired
	private CacheService cacheService;
	
	/**
	 * 에러 공통처리, 장애시 처리
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/error")
    public ResponseEntity handleError(HttpServletRequest request) throws Exception{
		// 헤더설정
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
		
        LOGGER.warn("===== SYSTEM ERROR_REQUEST_URI : "+ request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        LOGGER.warn("===== SYSTEM STATUS_CODE : "+ request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        LOGGER.warn("===== SYSTEM ERROR_EXCEPTION : "+ request.getAttribute(RequestDispatcher.ERROR_EXCEPTION));
		
		// 메시지
		LinkedHashMap msg = new LinkedHashMap();
		msg.put("resultCode", ValidationCode.SYSTEM_ERROR.getCode());
		
		LinkedHashMap body = new LinkedHashMap();
		body.put("msg", msg);
		
        return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	@Override
	public String getErrorPath() {
		return "/error";
	}
	
	/**
	 * 메시지 캐시삭제
	 * @param commandMap
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/removeCache")
	public ResponseEntity updateEhcache(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		cacheService.clearCache();
		
		// 헤더설정
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        //headers.setAccessControlAllowOrigin("*");
        //headers.setAccessControlAllowCredentials(true);
        
		// 메시지
		LinkedHashMap msg = new LinkedHashMap();
		msg.put("resultCode", ValidationCode.SUCCESS);
		msg.put("result", "removeCache");
		
		LinkedHashMap body = new LinkedHashMap();
		body.put("msg", msg);
		
        return new ResponseEntity(body, headers, HttpStatus.OK);		
	}
}
