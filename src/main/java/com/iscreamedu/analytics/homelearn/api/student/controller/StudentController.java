package com.iscreamedu.analytics.homelearn.api.student.controller;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stud")
public class StudentController {
	
	
	@Autowired
	StudLrnTypeService studLrnTypeService;

    private HttpHeaders headers;
    private LinkedHashMap body;

    public StudentController() {
        headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
    }

    /**
     * 학습유형 요약 (STUD-LT-001)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnTypeSummary")
    public ResponseEntity getLrnTypeSummary(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnTypeService.getLrnTypeSummary(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습유형 안내 (STUD-LT-002)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnTypeInfo")
    public ResponseEntity getLrnTypeInfo(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnTypeService.getLrnTypeInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습유형 상세 (STUD-LT-003)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnTypeDetail")
    public ResponseEntity getLrnTypeDetail(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnTypeService.getLrnTypeDetail(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습유형 내역 (STUD-LT-004)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnTypeHistory")
    public ResponseEntity getLrnTypeHistory(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnTypeService.getLrnTypeHistory(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

}
