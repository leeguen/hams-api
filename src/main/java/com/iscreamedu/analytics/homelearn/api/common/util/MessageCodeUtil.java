package com.iscreamedu.analytics.homelearn.api.common.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.iscreamedu.analytics.homelearn.api.common.service.CacheService;

/**
 * 메시지 처리 유틸 클래스
 * @author hy
 * @since 2019.09.10
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *   수정일      수정자          수정내용
 *  ----------  --------    ---------------------------
 *  2019.09.10  hy          최초 생성 
 *  </pre>
 */
public class MessageCodeUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageCodeUtil.class);

	private HttpSession session;
	private CacheService cacheService;
	
	private String separator = "|"; //기본 구분자

	private String msgCd = ""; //메시지 코드
	private String args = ""; //메시지 코드에 들어갈 값들 

    public String convertMsgToStr(Object objMsg, String type) {
    	
		String output = "";
		
		if(objMsg != null) {
			String msg = objMsg.toString();
		
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
				
				//긴 메시지
				if("LONG".equals(type)) {
					
					String[] temp = StringUtils.delimitedListToStringArray( msg, "^" ); //총 메시지 값
			    	msgCd = temp[0];
			    	if(temp.length > 1) {
			    		args = temp[1];
			    	}
			    	
			    	// 1번째 메시지
			    	if(msgCd != null && !"".equals(msgCd)) {
			    		//1.메시지를 받아옴
			    		output = cacheService.getMessage(lang, msgCd);
			    		
			    		//3.2차 치환 문자들을 치환함
			    		if(args != null) {
			    			String[] strArr = StringUtils.delimitedListToStringArray( args, separator );
			    			int arrLen = strArr.length;
			    			
			    			for(int i=0; i<arrLen; i++) {
			    				
			    				String flag = "";
			    				if(!"".equals(strArr[i])) {
			    					flag = strArr[i].substring(0, 1); //!시간, @코드 값으로 넘어오면
			    				} 
			    				
			    				if("!".equals(flag)) {
			    					//시간값 변경
			    					strArr[i] = strArr[i].substring(1);
			    					
			    					// 값이 콤마(,)로 구분된 경우, 치환영역 1군데에 콤마로 구분된 문자열을 다 넣어준다.
			    					String[] strArr2 = StringUtils.delimitedListToStringArray( strArr[i], "," );
			    					int arrLen2 = strArr2.length;
			    					
			    					StringBuffer sb2 = new StringBuffer();
			    					for(int j=0; j<arrLen2; j++) {
			    						
			    						String ret = "0"+ss;
			    						if (!"0".equals(strArr2[j])) {
			    							int h,m,s = 0;
			    							int t = Integer.parseInt(strArr2[j]);
			    							
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
			    						sb2.append(",").append( ret.trim() ); 
			    					}
			    					strArr[i] = sb2.toString().substring(1);
			    					
			    				} else if("@".equals(flag)) {
			    					//코드값 변경
			    					strArr[i] = strArr[i].substring(1);
			    					
			    					// 값이 콤마(,)로 구분된 경우, 치환영역 1군데에 콤마로 구분된 문자열을 다 넣어준다.
			    					String[] strArr2 = StringUtils.delimitedListToStringArray( strArr[i], "," );
			    					int arrLen2 = strArr2.length;
			    					
			    					if(arrLen2 > 1) {
			    						StringBuffer sb2 = new StringBuffer();
			    						for(int j=0; j<arrLen2; j++) {
			    							sb2.append(",").append( cacheService.getMessage(lang, strArr2[j]) ); 
			    						}
			    						strArr[i] = sb2.toString().substring(1);
			    					} else {
			    						strArr[i] = cacheService.getMessage(lang, strArr[i]);
			    					}
			    				}
			    				
			    				output = output.replaceAll( "\\{"+i+"\\}", "<strong>"+strArr[i]+"</strong>" );
			    			}
			    		}
			    	}
			    	
				} else if("SHORT".equals(type)) {
					
					msgCd = msg;
					
					// 메시지
					if(msgCd != null && !"".equals(msgCd)) {
						String[] strArr = StringUtils.delimitedListToStringArray( msgCd, separator );
						int arrLen = strArr.length;
						
						StringBuffer sb = new StringBuffer();
						for(int i=0; i<arrLen; i++) {
							// 값이 콤마(,)로 구분된 경우, 치환영역 1군데에 콤마로 구분된 문자열을 다 넣어준다.
							String[] strArr2 = StringUtils.delimitedListToStringArray( strArr[i], "," );
							int arrLen2 = strArr2.length;
							
							if(arrLen2 > 1) {
								StringBuffer sb2 = new StringBuffer();
								for(int j=0; j<arrLen2; j++) {
									sb2.append(",").append( cacheService.getMessage(lang, strArr2[j]) ); 
								}
								sb.append(separator).append( sb2.toString().substring(1) );
							}else {
								sb.append(separator).append( cacheService.getMessage(lang, strArr[i]) );
							}
						}
						
						output = sb.toString().substring(1);
					}
					
				} else if("TIME".equals(type)) {
					
					msgCd = msg;
					
					// 메시지
					if(msgCd != null && !"".equals(msgCd)) {
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
								sb2.append(",").append( ret.trim() ); 
							}
							sb.append(separator).append( sb2.toString().substring(1) );
						}
						
						output = sb.toString().substring(1);
					}
					
				}
				
			} catch (Exception e) {
				LOGGER.warn( "===== Convert Failed : " + msg);
			}
		}
        return output; 
    }
}
