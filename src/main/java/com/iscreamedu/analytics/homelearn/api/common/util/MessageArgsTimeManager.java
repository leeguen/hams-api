package com.iscreamedu.analytics.homelearn.api.common.util;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.iscreamedu.analytics.homelearn.api.common.service.CacheService;

/**
 * Message Args Time Manager
 * @author hyhy
 * @since 2018.08.08
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *  수정일		수정자		수정내용
 *  -------		--------    ---------------------------
 *  2018.08.08  hyhy        최초 생성 
 *  2019.09.26  5heeteak	총 학습 시간 구분 로직 추가
 *  </pre>
 */
public class MessageArgsTimeManager extends SimpleTagSupport {
	
	private HttpSession session;
	private CacheService cacheService;

	private String separator = "|"; //기본 구분자

	private String msgCd = ""; //메시지 코드

	private String rtnDefault = "-"; // 기본 반환
	
	private String args = "";
	
	public String getArgs() {
		return args;
	}
	public void setArgs(String args) {
		this.args = args;
	}

	public String getMsgCd() {
		return msgCd;
	}
	public void setMsgCd(String msgCd) {
		this.msgCd = msgCd;
	}

	@Override
    public void doTag() throws JspException, IOException {
		
		String output = "";
		
		try {
			//autowired가 안되기 때문에 spring bean에 등록된 cacheService를 가져옴
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			this.session = request.getSession();
			
			ServletContext conext = session.getServletContext();
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(conext);
			this.cacheService = (CacheService)ctx.getBean("cacheService");
			
			//언어코드
			String lang = "ko"; //기본 한국어
			if(session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME) != null) {
				lang = session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME).toString();
			}
			
			String hh = cacheService.getMessage(lang, "HOUR");
			String mi = cacheService.getMessage(lang, "MIN");
			String ss = cacheService.getMessage(lang, "SEC");
			boolean enFlag = "en".equals(lang) ? true : false; //영어이면 true
			
			// 메시지
			if(args != null && "T".equals(args)) {
				String[] strArr = StringUtils.delimitedListToStringArray( msgCd, separator );
				int arrLen = strArr.length;
				
				StringBuffer sb = new StringBuffer();
				for(int i=0; i<arrLen; i++) {
					// 값이 콤마(,)로 구분된 경우, 치환영역 1군데에 콤마로 구분된 문자열을 다 넣어준다.
					String[] strArr2 = StringUtils.delimitedListToStringArray( strArr[i], "," );
					int arrLen2 = strArr2.length;
					
					StringBuffer sb2 = new StringBuffer();
					for(int j=0; j<arrLen2; j++) {
						
						String ret = "0"+ss;
			            if (!"0".equals(strArr2[j])) {
			            	int h,m,s = 0;
			            	int t = Integer.parseInt(strArr2[j]);
			            	
			            	/*평가분석 > 평가 소요시간 -000초 -> -로 변환 (2019.04.19 AIRND-281 평가분석 > 평가지 분석 > 평가 소요시간 표시 오류 수정 - 오희택) START */
			            	if(t <= -1) {
			            		ret =  "-"; 
			            	} else {
		            		/*평가분석 > 평가 소요시간 -000초 -> -로 변환 (2019.04.19 AIRND-281 평가분석 > 평가지 분석 > 평가 소요시간 표시 오류 수정 - 오희택) END */
			            		if(t < 60) { //1분 미만인 경우 초단위 노출
				            		s = t;
				            		ret = ( s == 0 ? "" : s + ( enFlag && s > 1 ? ss+"s" : ss ) ); //영어일 경우 1보다 크면 뒤에 s를 붙여준다. 1hour -> 2hours
				            	}else {
				            		h = (t / 3600);
				            		m = (t % 3600 / 60);
				            		s = (t % 3600 % 60);
				            		
				            		ret = ( h == 0 ? "" : h + ( enFlag && h > 1 ? hh+"s" : hh ) )   
				            			+ ( m == 0 ? "" : " " + m + ( enFlag && m > 1 ? mi+"s" : mi ) ); 
				            			//+ ( s == 0 ? "" : s + ( enFlag && s > 1 ? ss+"s" : ss ) ); //영어일 경우 1보다 크면 뒤에 s를 붙여준다. 1hour -> 2hours
				            	}
			            	}
			            	
			            }
						sb2.append(",").append( ret ); 
					}
					sb.append(separator).append( sb2.toString().substring(1) );
				}
				
				output = sb.toString().substring(1);
			} else if(msgCd != null && !"".equals(msgCd)) {
				String[] strArr = StringUtils.delimitedListToStringArray( msgCd, separator );
				int arrLen = strArr.length;
				
				StringBuffer sb = new StringBuffer();
				for(int i=0; i<arrLen; i++) {
					// 값이 콤마(,)로 구분된 경우, 치환영역 1군데에 콤마로 구분된 문자열을 다 넣어준다.
					String[] strArr2 = StringUtils.delimitedListToStringArray( strArr[i], "," );
					int arrLen2 = strArr2.length;
					
					StringBuffer sb2 = new StringBuffer();
					for(int j=0; j<arrLen2; j++) {
						
						String ret = "0"+ss;
			            if (!"0".equals(strArr2[j])) {
			            	int h,m,s = 0;
			            	int t = Integer.parseInt(strArr2[j]);
			            	
			            	/*평가분석 > 평가 소요시간 -000초 -> -로 변환 (2019.04.19 AIRND-281 평가분석 > 평가지 분석 > 평가 소요시간 표시 오류 수정 - 오희택) START */
			            	if(t <= -1) {
			            		ret =  "-"; 
			            	} else {
		            		/*평가분석 > 평가 소요시간 -000초 -> -로 변환 (2019.04.19 AIRND-281 평가분석 > 평가지 분석 > 평가 소요시간 표시 오류 수정 - 오희택) END */
			            		if(t < 60) { //1분 미만인 경우 초단위 노출
				            		s = t;
				            		ret = ( s == 0 ? "" : s + ( enFlag && s > 1 ? ss+"s" : ss ) ); //영어일 경우 1보다 크면 뒤에 s를 붙여준다. 1hour -> 2hours
				            	}else {
				            		h = (t / 3600);
				            		m = (t % 3600 / 60);
				            		s = (t % 3600 % 60);
				            		
				            		ret = ( h == 0 ? "" : h + ( enFlag && h > 1 ? hh+"s" : hh + "<br/>") )   
				            			+ ( m == 0 ? "" : " " + m + ( enFlag && m > 1 ? mi+"s" : mi ) ); 
				            			//+ ( s == 0 ? "" : s + ( enFlag && s > 1 ? ss+"s" : ss ) ); //영어일 경우 1보다 크면 뒤에 s를 붙여준다. 1hour -> 2hours
				            	}
			            	}
			            	
			            }
						sb2.append(",").append( ret ); 
					}
					sb.append(separator).append( sb2.toString().substring(1) );
				}
				
				output = sb.toString().substring(1);
			} else {
				output = rtnDefault;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        getJspContext().getOut().print(output); 
    }
}
