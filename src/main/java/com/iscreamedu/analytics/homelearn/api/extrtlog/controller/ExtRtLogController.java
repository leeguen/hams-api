package com.iscreamedu.analytics.homelearn.api.extrtlog.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iscreamedu.analytics.homelearn.api.challenge.service.ChallengeService;
import com.iscreamedu.analytics.homelearn.api.extrtlog.service.ExtRtLogService;

@RestController
@RequestMapping("/extRtLog")	
public class ExtRtLogController {

	@Autowired
	private ExtRtLogService extRtLogService;

	@Autowired
	private ChallengeService challengeService;
	
    private HttpHeaders headers;
    private LinkedHashMap body;

	public ExtRtLogController() {
		headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
        headers.setCacheControl(CacheControl.noCache());
        headers.setCacheControl(CacheControl.noStore().mustRevalidate());
        headers.setExpires(0);
	}
	
	@PostMapping(value="/regCompleteMission", consumes = MediaType.APPLICATION_JSON_VALUE,  produces = "application/json; charset=utf8")
	@ResponseBody
	public ResponseEntity setRealTimeCompleteMission(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)extRtLogService.setRealTimeCompleteMission(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	@PostMapping(value="/setChMissonStatusChange", produces = "application/json; charset=utf8")
    @ResponseBody
    public ResponseEntity setRealTimeMissonStatusChange(@RequestBody Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws Exception {
		body = (LinkedHashMap)extRtLogService.setRealTimeMissonStatusChange(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
}
