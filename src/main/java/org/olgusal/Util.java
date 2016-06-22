package org.olgusal;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.olgusal.pojo.Term;

public class Util {
	public static int getHash(String message) {

		StringBuilder builder = new StringBuilder();
		builder.append(message);
		// builder.append("1");
		int result = builder.toString().hashCode();
		System.out.println("result : " + result);
		return result;
	}

	public static String getMD5(String message) throws UnsupportedEncodingException, NoSuchAlgorithmException {

		// MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		// messageDigest.update(word.getBytes());
		// String hashedWord = new String(messageDigest.digest());
		// System.out.println("Hashed " + hashedWord);

		String digest = null;
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hash = md.digest(message.getBytes("UTF-8"));

		// converting byte array to Hexadecimal String
		StringBuilder sb = new StringBuilder(2 * hash.length);
		for (byte b : hash) {
			sb.append(String.format("%02x", b & 0xff));
		}

		digest = sb.toString();

		System.out.println("hashed: " + digest);
		return digest;
	}

	public static Map sortByComparator(Map unsortMap) {

		List list = new LinkedList(unsortMap.entrySet());

		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o2, Object o1) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// put sorted list into map again
		// LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static void printMap(Map<Integer, String> sortedMap) {
		for (Map.Entry entry : sortedMap.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}

	public static void printTermMap(Map<String, Term> sortedMap) {
		for (Map.Entry<String, Term> entry : sortedMap.entrySet()) {
			System.out.println("###");

			System.out.print("Key:"+entry.getKey() + ", Root:" + entry.getValue().getRoot()+", TotalCount:"+entry.getValue().getTotalCount()+", OrigCount:"+entry.getValue().getOriginalSet().size()+", Orig:"+entry.getValue().getOriginalSet());

			System.out.println();
			for (Map.Entry<String, Integer> docMap : entry.getValue().getDocumentTermCountMap().entrySet()) {
				System.out.println("Doc:"+docMap.getKey()+", Count:"+docMap.getValue());
			}
			System.out.println("###");
		}
	}

	
}
