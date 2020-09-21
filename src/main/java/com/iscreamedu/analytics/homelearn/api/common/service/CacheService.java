package com.iscreamedu.analytics.homelearn.api.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;

import com.iscreamedu.analytics.homelearn.api.common.mapper.CommonMapper;

@Service("cacheService")
public class CacheService {
	
	@Autowired
	private EhCacheCacheManager ehCache;
	
	@Autowired
	CommonMapper commonMapper;
	
	private HttpSession session;
	private String CACHE_NAME = "commonCache";
	
	/**
	 * 메시지 목록 가져오기
	 * @param examSeq
	 * @return
	 * @throws Exception
	 */
	public HashMap getMessageList(String lang) throws Exception {
		// Cache Name을 가지고 cache 찾기 - ehcache.xml 참조
		Cache cc = ehCache.getCache(CACHE_NAME);
		String key = null;
		
		//찾는 캐시 데이터가 없으면 DB에서 조회
		if(cc.get(key) == null){
			Map param = new HashMap();
			param.put("lang", lang);
			
			List list = commonMapper.getList(param, "Common.selectCommMsgList");
			
			int size = list.size();
			Map msgMap = new HashMap();
			for(int i=0; i<size; i++) {
				Map data = (HashMap)list.get(i);
				msgMap.put( data.get("msgCd"), data.get("msg") );
			}
			
			cc.put(key, msgMap);
		}
		
		ValueWrapper obj = cc.get(key);

		return (HashMap)obj.get();
	}
	
	public String getMessage(String lang, String key) throws Exception {
		HashMap msgMap = this.getMessageList(lang);		
		String msg = "";
		
		if(msgMap.containsKey(key)){
			msg = (String)msgMap.get(key);
		} else {
			msg = key;
		}
		
		return msg;
	}
	
	public void removeCache(String key) throws Exception {
		Cache cc = ehCache.getCache(CACHE_NAME);
		cc.evict(key);
	}
	
	public void clearCache() throws Exception {
		Cache cc = ehCache.getCache(CACHE_NAME);
		cc.clear();
	}
}
