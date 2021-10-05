package com.iscreamedu.analytics.homelearn.api.group.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iscreamedu.analytics.homelearn.api.group.service.GroupService;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    private HttpHeaders headers;
    private LinkedHashMap body;

    public GroupController() {
        headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json"));
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowCredentials(true);
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
        body = (LinkedHashMap<String, Object>) groupService.getCommMsgCd(params);
        return new ResponseEntity(body,headers, HttpStatus.OK);
    }
}
