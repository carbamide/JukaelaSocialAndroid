package com.jukaela.Jukaela;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GravatarHelper {

	public static String getURL (String emailAddress, int size)
	{
		if (emailAddress == null) {
			return null;
		}
		
		String curatedEmail = emailAddress.replaceAll("\\s","").toLowerCase();
		String md5email = MD5_Hash(curatedEmail);
		String tempEndPoint = String.format("http://www.gravatar.com/avatar/%s?s=%d&d=mm", md5email, size);
		
		return tempEndPoint;
	}
	
	public static String MD5_Hash(String s) {
		MessageDigest m = null;

		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		m.update(s.getBytes(),0,s.length());
		String hash = new BigInteger(1, m.digest()).toString(16);
		return hash;
	}
}
