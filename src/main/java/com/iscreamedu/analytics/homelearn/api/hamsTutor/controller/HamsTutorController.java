package com.iscreamedu.analytics.homelearn.api.hamsTutor.controller;

import com.iscreamedu.analytics.homelearn.api.common.security.CipherUtil;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorExService;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorService;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.HamsTutorVrService;
import com.iscreamedu.analytics.homelearn.api.hamsTutor.service.impl.HamsTutorServiceImpl;
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
@RequestMapping("/tutor")
public class HamsTutorController {

    @Autowired
    private HamsTutorService hamsTutorService;

    @Autowired
    private HamsTutorVrService hamsTutorVrService;
    
    @Autowired
    private HamsTutorExService hamsTutorExService;

    private HttpHeaders headers;
    private LinkedHashMap body;

    public HamsTutorController() {
        headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
    }

    /**
     * 재연장 예측 (HAMS-T-EX-001)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getSettleInfoPredictionStt")
    public ResponseEntity getSettleInfoPredictionStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorExService.getSettleInfoPredictionStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * AI진단 결과 (HAMS-T-EX-002)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAiDiagnosisRst")
    public ResponseEntity getAiDiagnosisRst(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorExService.getAiDiagnosisRst(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * AI학습처방 취약단원 지도 (HAMS-T-EX-003)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAiWeakChapterGuide")
    public ResponseEntity getAiWeakChapterGuide(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorExService.getAiWeakChapterGuide(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * AI학습처방 추천 문제 (HAMS-T-EX-004)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAiRecommendQuestion")
    public ResponseEntity getAiRecommendQuestion(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorExService.getAiRecommendQuestion(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * AI 추천 코스 (HAMS-T-EX-005)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAiRecommendCourse")
    public ResponseEntity getAiRecommendCourse(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorExService.getAiRecommendCourse(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * AI 추천 코스 - 코스 확인 정보 등록 (HAMS-T-EX-006)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAiRecommendCourseConfirm")
    public ResponseEntity getAiRecommendCourseConfirm(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorExService.getAiRecommendCourseConfirm(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * AI 추천 코스 - 코스 적용 정보 등록 (HAMS-T-EX-007)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAiRecommendCourseApply")
    public ResponseEntity getAiRecommendCourseApply(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorExService.getAiRecommendCourseApply(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 비전리포트 NFT 발행 목록 (HAMS-T-NF-001)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionReportNftList")
    public ResponseEntity getVisionReportNftList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorExService.getVisionReportNftList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
     * 비전리포트 NFT 상세 (HAMS-T-NF-002)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionReportNft")
    public ResponseEntity getVisionReportNft(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorExService.getVisionReportNft(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 기본정보 (HAMS-T-LA-001)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnBasicInfo")
    public ResponseEntity getLrnBasicInfo(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getLrnBasicInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 성장지표 (HAMS-T-LA-002)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnGrowthStt")
    public ResponseEntity getLrnGrowthStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getLrnGrowthStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 학습 현황 (HAMS-T-LA-003)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnExStt")
    public ResponseEntity getLrnExStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getLrnExStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 학습 차트 (HAMS-T-LA-004)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnExChart")
    public ResponseEntity getLrnExChart(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getLrnExChart(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 타임라인 목록 (HAMS-T-LA-005)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getLrnTimeLineList")
    public ResponseEntity getLrnTimeLineList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getLrnTimeLineList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 평가 현황 (HAMS-T-LA-006)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getExamStt")
    public ResponseEntity getExamStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getExamStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 평가 현황 (HAMS-T-LA-007)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getExamChart")
    public ResponseEntity getExamChart(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getExamChart(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 평가 목록 (HAMS-T-LA-008)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getExamList")
    public ResponseEntity getExamList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getExamList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 출석 현황 (HAMS-T-LA-009)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getAttStt")
    public ResponseEntity getAttStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getAttStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 비전리포트 발행 정보 (HAMS-T-VR-001)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionReportPublishedInfo")
    public ResponseEntity getVisionReportPublishedInfo(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionReportPublishedInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 비전리포트 기본정보 (HAMS-T-VR-002)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionBasicInfo")
    public ResponseEntity getVisionBasicInfo(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionBasicInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 비전리포트 성장지표 (HAMS-T-VR-003)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionGrowthStt")
    public ResponseEntity getVisionGrowthStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionGrowthStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 비전리포트 평가 현황 (HAMS-T-VR-004)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionExamStt")
    public ResponseEntity getVisionExamStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionExamStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 비전리포트 평가 단원 현황 (HAMS-T-VR-005)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionExamChapterStt")
    public ResponseEntity getVisionExamChapterStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionExamChapterStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 비전리포트 평가 단원 학습 (HAMS-T-VR-006)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionExamChapterLrn")
    public ResponseEntity getVisionExamChapterLrn(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionExamChapterLrn(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 	비전리포트 평가 영역 현황 (HAMS-T-VR-007)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionExamFieldStt")
    public ResponseEntity getVisionExamFieldStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionExamFieldStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 	비전리포트 평가 목록 (HAMS-T-VR-008)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionExamList")
    public ResponseEntity getVisionExamList(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionExamList(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 	비전리포트 출석패턴 (HAMS-T-VR-009)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionAttPtn")
    public ResponseEntity getVisionAttPtn(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionAttPtn(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 	비전리포트 학습패턴 (HAMS-T-VR-010)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionLrnPtn")
    public ResponseEntity getVisionLrnPtn(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionLrnPtn(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 	비전리포트 출력 기본정보 (HAMS-T-VR-011)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionPrintBasicInfo")
    public ResponseEntity getVisionPrintBasicInfo(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionPrintBasicInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 	비전리포트 출력 학습현황 (HAMS-T-VR-012)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionPrintLrnStt")
    public ResponseEntity getVisionPrintLrnStt(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionPrintLrnStt(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 	비전리포트 출력 커리큘럼 (HAMS-T-VR-013)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionPrintLrnDiagnosisRst")
    public ResponseEntity getVisionPrintLrnDiagnosisRst(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionPrintLrnDiagnosisRst(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 	비전리포트 출력 추천 학습 (HAMS-T-VR-014)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getVisionPrintAiRecommendLrn")
    public ResponseEntity getVisionPrintAiRecommendLrn(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getVisionPrintAiRecommendLrn(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    /**
	 * 비전리포트 출력 피드백 수정 (HAMS-T-VR-015)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/getVisionPrintFeedbackUpdatePopup")
	@ResponseBody
	public ResponseEntity getVisionPrintFeedbackUpdatePopup(@RequestBody Map<String, Object> params) throws Exception {
		body = (LinkedHashMap<String, Object>)hamsTutorVrService.getVisionPrintFeedbackUpdatePopup(params);
		return new ResponseEntity(body, headers, HttpStatus.OK);
	}
	
	/**
     * 	회원 정보 (HAMS-T-VR-016)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getStudInfo")
    public ResponseEntity getStudInfo(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorVrService.getStudInfo(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }

    /**
     * 학습분석 메세지 코드 모음 (HAMS-T-LA-010)
     * @param params
     * @return
     * @throws Exception
     */
    @GetMapping("/getCommMsgCd")
    @ResponseBody
    public ResponseEntity getCommMsgCd(@RequestParam Map<String,Object> params) throws Exception {
        body = (LinkedHashMap<String, Object>) hamsTutorService.getCommMsgCd(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
    
    
    /**
	 * 과목 코드 정보 (HAMS-T-LA-011)
     * @param params
     * @return
     * @throws Exception
	 */
    @GetMapping("/getSubjCd")
    @ResponseBody
    public ResponseEntity getSubjCd(@RequestParam Map<String,Object> params) throws Exception {
    	body = (LinkedHashMap<String, Object>) hamsTutorService.getSubjCd(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
	}






}
