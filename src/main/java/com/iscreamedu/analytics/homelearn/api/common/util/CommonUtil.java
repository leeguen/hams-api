package com.iscreamedu.analytics.homelearn.api.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtil {

	
    
    
    /***
     * 해당월의 마지막날짜 추출
     * @param curDate
     * @param transFormat
     * @return 오류발생시 0 리턴
     */
	public static int getCalendarLastDay(String curDate, DateFormat transFormat) {
		try {
			Calendar dt = Calendar.getInstance();
			dt.setTime(transFormat.parse(curDate));
			return dt.getActualMaximum(Calendar.DATE);
		} catch(ParseException pe) {
			return 0;
		}
	}
	
	/***
	 * 날짜차 추출
	 * @param date1
	 * @param date2
	 * @param diffUnit : 비교 단위
	 * @return
	 */
	public static long getCalendarDiff(String date1, String date2, String diffUnit) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
			
	        Date firstDate = format.parse(date1);
	        Date secondDate = format.parse(date2);
	        
	        long diffInMillies  = firstDate.getTime() - secondDate.getTime(); 
	        if(diffUnit.toUpperCase().equals("YEAR")) {
	           	return Math.abs(diffInMillies / (365*24*60*60*1000)); 
	        } else if(diffUnit.toUpperCase().equals("DAY")) {
	        	return Math.abs(diffInMillies / (24*60*60*1000)); 
	        } else if(diffUnit.toUpperCase().equals("HOUR")) { 
	        	return Math.abs(diffInMillies / (60*60*1000)); 
	        } else if(diffUnit.toUpperCase().equals("MIN")) { 
	        	return Math.abs(diffInMillies / (60*1000)); 
	        } else {
	        	return Math.abs(diffInMillies);
	        }
	        
		} catch(ParseException pe) {
			return 0;
		}
	}
}
