package com.bankocr.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.bankocr.OCRDigit;

public class BankOcrTest {
	// The code in this function is used to generate sample input. 
	// This code will generate 500 entries for the sample input.
	public void generateEntriesForInput() {
		List<Character> charSet = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
		StringBuilder sb = null;
		List<String> nums = new ArrayList<String>();
		for(int i=0; i<500; i++){
			sb = new StringBuilder();
			Collections.shuffle(charSet);
			for(int j=0; j<charSet.size() - 1; j++){
				char c = charSet.get(j);
				sb.append(c);
			}
			nums.add(sb.toString());
		}
		List<String> ocrCharList = null;
		List<List<String>> acnoList = new ArrayList<List<String>>();; 
		//for each string in the list
		for(String string : nums){
			ocrCharList = new ArrayList<>(9);
			char[] arr = string.toCharArray();
			// for each char in string
			for(char c : arr){
				// get the ocr char
				String ocrChar = OCRDigit.getOCRDigitString(c);
				ocrCharList.add(ocrChar);
			}
			acnoList.add(ocrCharList);
		}
		
		List<String> list = new ArrayList<String>();
		StringBuilder sb1, sb2, sb3;
		for(List<String> ocrno : acnoList){
			sb1 = new StringBuilder();
			sb2 = new StringBuilder();
			sb3 = new StringBuilder();
			for(String s : ocrno){
				sb1.append(s.substring(0, 3));
				sb2.append(s.substring(3, 6));
				sb3.append(s.substring(6, 9));
			}
			list.add(String.format("%s%n%s%n%s%n%27s%n", sb1, sb2, sb3, " "));
		}
		
		for(String string : list){
			System.out.print(string);
		}
	}
}
