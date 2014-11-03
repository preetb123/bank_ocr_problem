package com.bankocr;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public final class DocumentScanner {
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	// The characters can only be space, pipe or underscore 
	public static final Pattern ocrCharPattern = Pattern.compile("^[\\s|_]*$");
	
	/**
	 * Scans the string containing the entry for one account number and parses OCR characters to the 
	 * digits they represent or can possibly represent. 
	 * @param builder the string containing the OCR entry for one account number  
	 * @return the resolved account number
	 */
	public static String getScannedAccountNumber(StringBuilder builder) {
		final int DIGIT_WIDTH = 3;
		final int ACCOUNT_NUMBER_LENGTH = 9;
		String[] lines = builder.toString().split(LINE_SEPARATOR);
		
		if(!isValidEntry(lines)){
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		Map<Integer, List<Character>> errorPosMap = new LinkedHashMap<Integer, List<Character>>();
		Map<Integer, List<Character>> guessedDigitsMap = new LinkedHashMap<Integer, List<Character>>();
		
		for(int i=0; i<ACCOUNT_NUMBER_LENGTH; i++){
			String line1 = lines[0].substring(i * DIGIT_WIDTH, i * DIGIT_WIDTH + DIGIT_WIDTH);
			String line2 = lines[1].substring(i * DIGIT_WIDTH, i * DIGIT_WIDTH + DIGIT_WIDTH);
			String line3 = lines[2].substring(i * DIGIT_WIDTH, i * DIGIT_WIDTH + DIGIT_WIDTH);
			OCRDigit.ParsedDigit parsedDigit = OCRDigit.getParsedDigit(line1 + line2 + line3);
			if(parsedDigit.getGuessedDigits().size() == 1){
				// Only one digit guessed
				sb.append(parsedDigit.getGuessedDigits().get(0));
				if(parsedDigit.isGuessed()){
					guessedDigitsMap.put(i, parsedDigit.getGuessedDigits());
				}
			}else {
				sb.append("?");
				// Multiple digits guessed, so we store all the guessed characters for the position
				errorPosMap.put(i, parsedDigit.getGuessedDigits());
			}
		}
		
		if(errorPosMap.size() == 0 && AccountNumber.isValid(sb.toString())){
			return sb.toString();
		}else{
			AccountNumber accountNumber = new AccountNumber(sb, errorPosMap, guessedDigitsMap);
			return accountNumber.resolveAccountNumbers();
		}
	}

	public static boolean isValidEntry(String[] lines) {
		if(lines.length != 4){
			System.out.println("Entry has to be 4 lines long.");
			return false;
		}
		for(int i=0; i<lines.length; i++){
			String line = lines[i];
			if(line.length() != 27){
				System.out.println("Each line should contain 27 characters.");
				return false;
			}
			if(i==3 && !line.matches("^\\s*$")){
				System.out.println("Fourth line should be emty.");
				return false;
			}else if(!ocrCharPattern.matcher(line).find()){
				System.out.println("Invalid characters found. Only space, pipe or underscore expected.");
				return false;
			}
		}
		return true;
	}
}
