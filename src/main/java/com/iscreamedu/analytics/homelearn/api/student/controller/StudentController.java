package com.iscreamedu.analytics.homelearn.api.student.controller;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.student.service.StudHomeLogService;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnAnalService;
import com.iscreamedu.analytics.homelearn.api.student.service.StudLrnLogService;
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
	
	@Autowired
	StudLrnLogService studLrnLogService;
	
	@Autowired
	StudHomeLogService studHomeLogService;

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
     * 학생 정보 (STUD-CM-003)
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/getStudInfo")
    public ResponseEntity getStudInfo(@RequestBody Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getStudInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 학생 정보 (STUD-CM-003)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getStudInfo")
    public ResponseEntity getStudInfoForTchr(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getStudInfoForTchr(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 현재 월 정보 (STUD-CM-004)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getYymmwkList")
    public ResponseEntity getYymmwkList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getYymmwkList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 학습유형 요약 (STUD-LT-001)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnTypeCheck")
    public ResponseEntity getLrnTypeSummary(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnTypeService.getLrnTypeCheck(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습유형 안내 (STUD-LT-002)
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
     * 학습유형 상세 (STUD-LT-003)
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
     * 학습유형 경로 정보 (STUD-LT-004)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnTypePathInfo")
    public ResponseEntity getLrnTypePathInfo(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnTypeService.getLrnTypePathInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 학습유형 안내 (STUD-LT-005)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/admin/getLrnTypeDetail")
    public ResponseEntity getLrnTypeDetailForAdmin(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnTypeService.getLrnTypeDetailForAdmin(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습유형 상세 (STUD-LT-006)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/admin/getLrnTypeHistory")
    public ResponseEntity getLrnTypeHistoryForAdmin(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnTypeService.getLrnTypeHistoryForAdmin(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 학습유형 안내 (STUD-LT-005)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping({"/getStudLrnTypeInfo/{studId}", "/getStudLrnTypeInfo/{studId}.ai"})
    public ResponseEntity getStudLrnTypeInfo(@PathVariable("studId") String studId) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnTypeService.getStudLrnTypeInfo(studId);
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
     * 출석률 (STUD-LA-003)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAttRt")
    public ResponseEntity getAttRt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getAttRt(params);
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
     * 스스로학습 (STUD-LA-007)
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
     * 평가점수 (STUD-LA-008)
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
     * 오답노트 현황 (STUD-LA-009)
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
     * 문제풀이 습관 (STUD-LA-010)
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
     * 문제풀이 습관 (STUD-LA-010)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getSlvHabit2")
    public ResponseEntity getSlvHabit2(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.getSlvHabit2(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 리포트 소감 조회 (STUD-LA-011)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/insertReportCheck")
    public ResponseEntity insertReportCheck(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.insertReportCheck(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 리포트 소감 삭제 (STUD-LA-012)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/deleteReportCheck")
    public ResponseEntity deleteReportCheck(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnAnalService.deleteReportCheck(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 	학습 수행 이력 (STUD-LT-005)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping({"/getStudLrnExLog/{studId}"})
    public ResponseEntity getStudLrnExLog(@PathVariable("studId") String studId) throws Exception {
        body = (LinkedHashMap<String, Object>) studLrnLogService.getStudLrnExLog(studId);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 목록 - 관리자 페이지용 (STUD-HL-001)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getHlogList")
    public ResponseEntity getHlogList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.getHlogList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 수여 수 (STUD-HL-002)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getHlogCnt")
    public ResponseEntity getHlogCnt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.getHlogCnt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 목록 - 학습기용 (STUD-HL-003)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getHlogDetailList")
    public ResponseEntity getHlogDetailList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.getHlogDetailList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 썸네일 목록 (STUD-HL-004)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getHlogThnList")
    public ResponseEntity getHlogThnList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.getHlogThnList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 상세 정보 (STUD-HL-005)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getHlogDetail")
    public ResponseEntity getHlogDetail(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.getHlogDetail(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 정보 (STUD-HL-006)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getHlogInfo")
    public ResponseEntity getHlogInfo(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.getHlogInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 템플릿 정보 (STUD-HL-007)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getTempInfo")
    public ResponseEntity getTempInfo(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.getTempInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 템플릿 목록 (STUD-HL-008)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getHlogTempList")
    public ResponseEntity getHlogTempList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.getHlogTempList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 템플릿 삭제 (STUD-HL-009)
     * @param params
     * @return
     * @throws Exception
     */
    @DeleteMapping("/delHlogTemp")
    public ResponseEntity delHlogTemp(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.delHlogTemp(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 생성 (STUD-HL-010)
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/regHlog")
    public ResponseEntity regHlog(@RequestBody Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.regHlog(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 수여 (STUD-HL-011)
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/setHlog")
    public ResponseEntity setHlog(@RequestBody Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.setHlog(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 상장 삭제 (STUD-HL-012)
     * @param params
     * @return
     * @throws Exception
     */
    @DeleteMapping("/delHlog")
    public ResponseEntity delHlog(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) studHomeLogService.delHlog(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

}
