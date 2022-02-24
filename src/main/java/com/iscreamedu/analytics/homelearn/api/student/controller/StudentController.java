package com.iscreamedu.analytics.homelearn.api.student.controller;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnAnalService;
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
	
	@Autowired
	StudLrnAnalService studLrnAnalService;

    private HttpHeaders headers;
    private LinkedHashMap body;

    public StudentController() {
        headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
    }
    
    /**
     * 현재 주차 정보 (STUD-CM-001)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getYymmwk")
    public ResponseEntity getYymmwk(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getYymmwk(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 현재 월 정보 (STUD-CM-002)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getYymm")
    public ResponseEntity getYymm(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getYymm(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
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
    
    /**
     * 리포트 리스트 (STUD-LA-001)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getReportList")
    public ResponseEntity getReportList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getReportList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 학습분석 요약 (STUD-LA-002)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getHomeSummary")
    public ResponseEntity getHomeSummary(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getHomeSummary(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 학습현황 (STUD-LA-003)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnStt")
    public ResponseEntity getLrnStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getLrnStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 수행률 (STUD-LA-004)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnExRt")
    public ResponseEntity getLrnExRt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getLrnExRt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 수행 습관 (STUD-LA-005)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnHabit")
    public ResponseEntity getLrnHabit(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getLrnHabit(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 집중도 (STUD-LA-006)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getConcn")
    public ResponseEntity getConcn(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getConcn(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 홈런 타임 (STUD-LA-007)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnTm")
    public ResponseEntity getLrnTm(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getLrnTm(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 스스로학습 (STUD-LA-008)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAlrn")
    public ResponseEntity getAlrn(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getAlrn(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 평가점수 (STUD-LA-009)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getExamScore")
    public ResponseEntity getExamScore(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getExamScore(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 오답노트 현황 (STUD-LA-010)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getIncrtNoteStt")
    public ResponseEntity getIncrtNoteStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getIncrtNoteStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 문제풀이 습관 (STUD-LA-011)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getSlvHabit")
    public ResponseEntity getSlvHabit(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getSlvHabit(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 리포트 소감 조회 (STUD-LA-012)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getReportImpression")
    public ResponseEntity getReportImpression(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getReportImpression(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 리포트 소감 등록 (STUD-LA-013)
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/insertReportImpression")
	@ResponseBody
	public ResponseEntity insertReportImpression(@RequestBody Map<String, Object> params) throws Exception {
		body = (LinkedHashMap<String, Object>)studLrnAnalService.insertReportImpression(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}

}
