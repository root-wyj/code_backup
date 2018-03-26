package com.shixi.web.tools;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 加密/签名
 * @author heyong
 * @date 2018年1月23日
 */
public class Signature {
	
	// 签名类型
	public static final String MD5_WITH_RSA = "MD5withRSA";
	public static final String SHA1_WITH_RSA = "SHA1withRSA";
	public static final String SHA256_WITH_RSA = "SHA256withRSA";
	
	/**
	 * 生成RSA公钥/密钥
	 * @param keysize
	 * @return 返回的Map集合通过"public_key"(公钥)和"private_key"(密钥)取值。
	 * @author heyong
	 */
	public static Map<String, String> generateRSAKey(int keysize)  {
		KeyPairGenerator keyPairGenerator;
		
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(keysize);

			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

			byte[] publicKeyData = publicKey.getEncoded();
			byte[] privateKeyData = privateKey.getEncoded();
			
			Map<String, String> keyMap = new HashMap<String, String>();
			keyMap.put("public_key", Base64.getEncoder().encodeToString(publicKeyData));
			keyMap.put("private_key", Base64.getEncoder().encodeToString(privateKeyData));
			return keyMap;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 签名
	 * 
	 * @param signType 签名类型
	 * @param privateKey 私钥
	 * @param data 原文
	 * @return 返回base64编码后的字符串
	 * @author heyong
	 */
	public static String signData(String signType, String privateKey, String data) throws NoSuchAlgorithmException,
			InvalidKeyException, InvalidKeySpecException, SignatureException, UnsupportedEncodingException {
		java.security.Signature signature = java.security.Signature.getInstance(signType);
		signature.initSign(Signature.toRSAPrivateKey(privateKey));
		signature.update(data.getBytes("UTF-8"));
		byte[] result = signature.sign();

		return Base64.getEncoder().encodeToString(result);
	}

	/**
	 * 验证
	 * 
	 * @param signType 签名类型
	 * @param publicKey 公钥
	 * @param data 原文
	 * @param signData 密文（签名）
	 * @return
	 * @author heyong
	 */
	public static boolean verifyData(String signType, String publicKey, String data, String signData)
			throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, SignatureException,
			UnsupportedEncodingException {
		java.security.Signature signature = java.security.Signature.getInstance(signType);
		signature.initVerify(Signature.toRSAPublicKey(publicKey));
		signature.update(data.getBytes("UTF-8"));
		return signature.verify(Base64.getDecoder().decode(signData));
	}

	private static PrivateKey toRSAPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.getDecoder().decode(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}
	
	private static PublicKey toRSAPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.getDecoder().decode(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}
}
