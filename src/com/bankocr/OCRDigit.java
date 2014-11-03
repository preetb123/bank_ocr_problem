package com.bankocr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class contains static functions for parsing the OCR characters into digits, or digits the OCR character 
 * can possibly be.   
 */
public final class OCRDigit {
	/**
	 * Contains mapping of the OCR scanned string to the character that it represents.
	 */
	public static final Map<String, Character> ocrCharToDigitMap = new LinkedHashMap<String, Character>(10);
	/**
	 * Contains the map of characters(including OCR scanned that are legal or illegal) to the characters they possibly 
	 * can be.
	 */
	public static final Map<String, List<Character>> possibleDigitsMap = new HashMap<String, List<Character>>();
	
 	public static final String ZERO = " _ " +
									    "| |" +
									    "|_|";
	public static final String ONE = "   " +
									    "  |" +
									    "  |";
	public static final String TWO = " _ " +
									    " _|" +
									    "|_ ";
	public static final String THREE = " _ " +
									     " _|" +
									     " _|";
	public static final String FOUR = "   " +
									    "|_|" +
									    "  |";
	public static final String FIVE = " _ " +
									    "|_ " +
									    " _|";
	public static final String SIX = " _ " +
									    "|_ " +
									    "|_|";
	public static final String SEVEN = " _ " +
									      "  |" +
									      "  |";
	public static final String EIGHT = " _ " +
									     "|_|" +
									     "|_|";
	public static final String NINE = " _ " +
									    "|_|" +
									    " _|";

	static{
		ocrCharToDigitMap.put(ZERO, '0');
		ocrCharToDigitMap.put(ONE, '1');
		ocrCharToDigitMap.put(TWO, '2');
		ocrCharToDigitMap.put(THREE, '3');
		ocrCharToDigitMap.put(FOUR, '4');
		ocrCharToDigitMap.put(FIVE, '5');
		ocrCharToDigitMap.put(SIX, '6');
		ocrCharToDigitMap.put(SEVEN, '7');
		ocrCharToDigitMap.put(EIGHT, '8');
		ocrCharToDigitMap.put(NINE, '9');
	}
	
	/**
	 * Returns a list of digits by parsing the scanned character. If the scanned character is identifiable, 
	 * the list contains only one digit OR the list contains all the guessed digits for the scanned character.   
	 * @param ocrChar the character scanned by the scanner. We receive this character as a string formed with 
	 * spaces, underscores and pipe. 
	 * @return the identified digit for the scanned character OR the list of guessed digits. The return value is wrapped
	 * into a {@link ParsedDigit} to know if the digits/digit are guessed OR we just found them in the map containing
	 * the OCR char and the digit it represents  
	 */
	public static ParsedDigit getParsedDigit(String ocrChar){
		System.out.println("getParsedDigit: \n" + represent(ocrChar));
		ParsedDigit parsedDigit;
		List<Character> digits = new ArrayList<Character>();
		if(ocrCharToDigitMap.get(ocrChar) != null){
			digits.add(ocrCharToDigitMap.get(ocrChar));
			parsedDigit = new ParsedDigit(false, digits);
		}else{
			digits.addAll(getPossibleDigits(ocrChar));
			parsedDigit = new ParsedDigit(true, digits);
		}
		System.out.println("digits.size(): " + digits.size());
		return parsedDigit;
	}
	
	/**
	 * Returns the list of characters that can be formed by adding or removing JUST ONE pipe or underscore to the
	 * identifiable digit.
	 * @param ocrChar the OCR character string  
	 * @return a list of characters that are only one character different from the received OCR character
	 */
	public static List<Character> getPossibleDigits(String ocrChar) {
		if(possibleDigitsMap.containsKey(ocrChar)){
			System.out.println("Possible digits for \n" + represent(ocrChar) + " \n\n are ");
			List<Character> s = possibleDigitsMap.get(ocrChar);
			System.out.print(s.toString()+"\n");
			return s;
		}
		List<Character> possibleDigits = new ArrayList<>();
		for(Entry<String, Character> ocrCharEntry : ocrCharToDigitMap.entrySet()){
			String key = ocrCharEntry.getKey();
			int distance = getHammingDistance(ocrChar, key);
			if(distance == 1){
				possibleDigits.add(ocrCharEntry.getValue());
			}
			System.out.println(String.format("Distance: %d, for ocrChar and key %n %s %n %s", distance, represent(ocrChar), represent(key)));
		}
		possibleDigitsMap.put(ocrChar, possibleDigits);
		return possibleDigits;
	}
	
	/**
	 * Return the difference between two strings. I.e. number of positions at which the corresponding symbols are different.
	 * @param s1 the first string
	 * @param s2 the second string
	 * @return the difference > 0, 0 if two strings are equal  
	 */
	public static int getHammingDistance(String s1, String s2){
		//minimum number of substitutions required to change one string into the other, 
		//or the minimum number of errors that could have transformed one string into the other.
		int distance = 0;
		for(int i=0; i<s1.length(); i++){
			if(s1.charAt(i) != s2.charAt(i)){
				++distance;
			}
		}
		return distance;
	}	
	
	/**
	 * Format the string to represent it as an OCR digit
	 * @param s the string to be formatted
	 * @return the formatted string
	 */
	public static String represent(String s){
		return String.format("%s%n%s%n%s", s.substring(0, 3), s.substring(3, 6), s.substring(6, 9));
	}

	/**
	 * Returns the OCR character string for this digit.
	 * @param c the character
	 * @return the OCR representation of the character
	 */
	public static String getOCRDigitString(char c) {
		for(Entry<String, Character> entry : ocrCharToDigitMap.entrySet()){
			if(entry.getValue().equals(c)){
				return entry.getKey();
			}
		}
		return "";
	}
	
	public static class ParsedDigit {
		private boolean isGuessed;
		private List<Character> digits;
		
		public ParsedDigit(boolean isGuessed, List<Character> digits) {
			this.isGuessed = isGuessed;
			this.digits = digits;
		}

		public boolean isGuessed() {
			return this.isGuessed;
		}
		
		public List<Character> getGuessedDigits() {
			return this.digits;
		}	
	}
}
