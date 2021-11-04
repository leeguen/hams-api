package com.iscreamedu.analytics.homelearn.api.common.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 공통 암호화 클래스
 * @author hy
 * @since 2019.04.29
 * @version 1.0
 * @see
 *  
 * <pre>
 * << 개정이력(Modification Information) >>
 * 
 *   수정일      수정자          수정내용
 *  -------    --------    ---------------------------
 *  2019.04.29   hy        최초 생성 
 *  </pre>
 */
public class CipherUtil {
	
	/** log */
	private static final Logger LOGGER = LoggerFactory.getLogger(CipherUtil.class);

	private static volatile CipherUtil INSTANCE;

	final static String secretKey = "K2y4N9bRm01Ih4oA6aE9HOMELEARNxHy"; // 32bit
	static String IV = ""; // 16bit

	public static CipherUtil getInstance() {
		if (INSTANCE == null) {
			synchronized (CipherUtil.class) {
				if (INSTANCE == null)
					INSTANCE = new CipherUtil();
			}
		}
		return INSTANCE;
	}

	private CipherUtil() {
		IV = secretKey.substring(0, 16);
	}
	
	// 암호화
	public static String AES_Encode(String str) throws UnsupportedEncodingException,NoSuchAlgorithmException, NoSuchPaddingException,
														InvalidKeyException, InvalidAlgorithmParameterException,
														IllegalBlockSizeException, BadPaddingException {
		byte[] keyData = secretKey.getBytes();
		SecretKey secureKey = new SecretKeySpec(keyData, "AES");
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));
		byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
		String enStr = new String(Base64.encodeBase64(encrypted));

		return enStr;
	}

	// 복호화
	public static String AES_Decode(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
														InvalidKeyException, InvalidAlgorithmParameterException,
														IllegalBlockSizeException, BadPaddingException {
		if(str != null && !str.equals("")){
			str = str.replace("%2F", "/").replace("%2B", "+").replace("%3D", "=").replace(" ", "+").replace("%20", "+");
			byte[] keyData = secretKey.getBytes();
			SecretKey secureKey = new SecretKeySpec(keyData, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes("UTF-8")));
			byte[] byteStr = Base64.decodeBase64(str.getBytes());
			return new String(c.doFinal(byteStr), "UTF-8");
		}
		
		return null;
	}
		
	public static void main(String[] args) throws Exception{
		CipherUtil cp = CipherUtil.getInstance();
		String encodedStr = cp.AES_Encode("1518810&1806565"); //TCHR_ID&STUD_ID
		LOGGER.debug("***************** encodingTemp : " + encodedStr);
		encodedStr = "dAWtTb5qz7Cen6srfb+IUw==";
		String decodedStr = cp.AES_Decode(encodedStr);
		LOGGER.debug("***************** decodingTemp : " + decodedStr);
	}
}
