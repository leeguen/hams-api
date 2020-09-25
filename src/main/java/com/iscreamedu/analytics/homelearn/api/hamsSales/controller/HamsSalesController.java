package com.iscreamedu.analytics.homelearn.api.hamsSales.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/sales")
@RestController
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
	
	/**
	 * 학생 정보 (HAMS-S-C-001)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getStudInfo")
	public ResponseEntity getStudInfo(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getStudInfo(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 일일 학습 정보 (HAMS-S-TR-001)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getDailyLrnStt")
	public ResponseEntity getDailyLrnStt(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getDailyLrnStt(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
	 * 전환 예측 (HAMS-S-TR-002)
	 * @param params
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getSettleInfoPrediction")
	public ResponseEntity getSettleInfoPrediction(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)hamsSalesService.getSettleInfoPrediction(params);
		
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
}
