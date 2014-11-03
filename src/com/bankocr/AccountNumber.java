package com.bankocr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class AccountNumber {
	/**
	 * The account number under consideration. The digits of this string will be substituted with the possible
	 * digits for that position to form valid account number.
	 */
	private StringBuilder accountNumber = null;
	/**
	 * This string helps in determining the final valid account numbers.
	 */
	private String receivedAccountNumber = null;
	/**
	 * Contains the map of positions of the illegal characters to the list of two or more guessed digits for that position.
	 */
	private Map<Integer, List<Character>> errorPosMap = null;
	/**
	 * Contains the map of position to the character guessed for the illegal character at that position. The size of the
	 * list is always 1.
	 */
	private Map<Integer, List<Character>> guessedDigitsMap = null;
	
	/**
	 * List of possible numbers formed by replacing the illegal characters with the guessed digits for that 
	 * position OR by replacing the digits with the possible digits(When number contains all legal characters) for 
	 * that position so that the resulting number is a valid number.
	 */
	private List<String> possibleNumbersList = null;
	private Map<Integer, List<Character>> storedPairsMap = null;
	/**
	 * This map contains valid account numbers sorted according to how much similar they are to the 
	 * received account number(if account number does not contains any illegal characters) OR the account number 
	 * formed by replacing the illegal character with possible digit for that character.
	 */
	private Map<Integer, ArrayList<String>> validAccountNumbersMap = new TreeMap<Integer, ArrayList<String>>();
	
	
	public AccountNumber(StringBuilder parsedAccountNumber, Map<Integer, List<Character>> errorPosMap, Map<Integer, List<Character>> guessedDigitsMap) {
		this.accountNumber = parsedAccountNumber;
		this.errorPosMap = errorPosMap;
		this.receivedAccountNumber = parsedAccountNumber.toString();
		this.guessedDigitsMap = guessedDigitsMap;
		System.out.println("Account number received : " + this.accountNumber);
	}

	/**
	 * Checks if the account number is valid
	 * @param accountNumber the account number
	 * @return <code>true</code> if the account number is valid, <code>false</code> otherwise
	 */
	public static boolean isValid(String accountNumber){
		char[] numbers = accountNumber.toCharArray();
		int sum = 0;
		for(int i=numbers.length, j=0; i>0; i--, j++){
			int num = Character.getNumericValue(numbers[j]);
			sum += i * num;
		}
		return sum % 11 == 0;
	}
	
	public boolean containsValidCharacters(){
		if(receivedAccountNumber == null)
			return false;
		if(receivedAccountNumber.length() != 9){
			System.out.println("Account number must only contain nine characters");
			return false;
		}
		// If the account number has no illegal characters
		if(errorPosMap.size() == 0){
			if(!receivedAccountNumber.matches("^[0-9]*$")){
				System.out.println("Account number should only contain digits between 0-9");
				return false;
			}
		}else{
			// Account number has illegal characters
			if(!receivedAccountNumber.matches("^[0-9?]*$")){
				System.out.println("Account number should only contain digits between 0-9 and ? for illegal character if any");
				return false;
			}
		}
		return true;
	}

	public String resolveAccountNumbers() {
		if(!containsValidCharacters()){
			return String.format("%s ILL", this.receivedAccountNumber);
		}
		if(errorPosMap.size() == 0){
			// Account number had no illegal characters.
			permuteWithPossibleDigits();
			return getResolvedAccountNumbers();
		}else{
			// This function will calculate all the resulting combinations from the pairs of guessed digits,
			// and will try to insert the combination at their respective position.
			// E.g. For the number '?2345678?', the guessed digits for position 1 and 9 are [1, 4] & [3, 5]
			// so 13, 15, 43, 45 are the combinations, and replacing the characters at position 1 & 9, we get
			// 123456783, 123456785, 423456783, 423456785
			getCombinations(errorPosMap);
			if(validAccountNumbersMap.size() > 0 && validAccountNumbersMap.get(1) != null){
				System.out.println("we have valid numbers : " + validAccountNumbersMap.toString());
				// We have valid numbers.
				return getResolvedAccountNumbers();
			}else{
				System.out.println("possible numbers  : " + possibleNumbersList.size());
				int validNumbersCount = 0;
				List<String> possibleNumbersToValidNumberStrings = new ArrayList<String>();
				for(String s : possibleNumbersList){
					System.out.println("trying " + s);
					receivedAccountNumber = s;
					accountNumber.replace(0, 9, s);
					permuteWithPossibleDigits();
					int currentValidNumbersCount = validAccountNumbersMap.get(1).size();
					// Check if permuting the characters of the current possible number contributed in
					// finding the valid account number
					if(currentValidNumbersCount > validNumbersCount){
						validNumbersCount = currentValidNumbersCount;
						System.out.println("number contributing to the valid account number : " + s);
						possibleNumbersToValidNumberStrings.addAll(possibleNumbersList);
					}else{
						//The current possible number did not contribute to the valid account numbers.
					}
					System.out.println("possible numbers for: " + s  + " are "+ possibleNumbersList.toString());
					System.out.println("valid numbers for possible number : " + s + " are " + validAccountNumbersMap.size() + "  " + validAccountNumbersMap.toString());
				}
				
				ArrayList<String> nosArrayList = validAccountNumbersMap.get(1);
				POSSIBLE_NUMBERS_LOOP:
				for(String pString : possibleNumbersToValidNumberStrings){
					for(String vString : nosArrayList){
						int diff = OCRDigit.getHammingDistance(pString, vString);
						if(diff == 1){
							receivedAccountNumber = pString;
							break POSSIBLE_NUMBERS_LOOP;
						}
						System.out.println(String.format("difference between pString: %s and vString: %s is %d", pString, vString, diff));
					}
				}
				
				return getResolvedAccountNumbers();
			}
		}
	}

	private String getResolvedAccountNumbers() {
		ArrayList<String> nearestNumbers = validAccountNumbersMap.get(1);
		if(validAccountNumbersMap.size() == 0 || nearestNumbers == null){
			return String.format("%s ILL", receivedAccountNumber);
		}else{
			if(nearestNumbers.size() == 1){
				return nearestNumbers.get(0);
			}else{
				StringBuilder sb = new StringBuilder();
				for(int i=0; i<nearestNumbers.size(); i++){
					sb.append(String.format("'%s'%s", nearestNumbers.get(i), (i < nearestNumbers.size() - 1 ? ", " : "")));
				}
				return String.format("%s AMB [%s]", receivedAccountNumber, sb.toString());
			}
		}
	}

	private void permuteWithPossibleDigits(){
		Map<Integer, List<Character>> substitutionPairs = new LinkedHashMap<Integer, List<Character>>();
		for(int i=0; i<accountNumber.length(); i++){
			if(errorPosMap.containsKey(i) || guessedDigitsMap.containsKey(i))
				continue;
			final char c = accountNumber.charAt(i);
			String ocrCharString = OCRDigit.getOCRDigitString(c);
			final List<Character> possibleCharacters = OCRDigit.getPossibleDigits(ocrCharString);
			if(!possibleCharacters.isEmpty()){
				//insert the current character at the beginning
				substitutionPairs.put(i, new ArrayList<Character>(){{
					add(c);
					addAll(possibleCharacters);
					}});
			}
			System.out.println("possible char for " + c + " are " + possibleCharacters.toString() + " at position : " + i);
		}
		getCombinations(substitutionPairs);
	}
	
	private void getCombinations(Map<Integer, List<Character>> pairs){
		possibleNumbersList = new ArrayList<String>();
		int totalPairs = pairs.size();
		char[] tempResult = new char[totalPairs];
		storedPairsMap = new HashMap<Integer, List<Character>>(totalPairs);
		combinePairs(pairs, 0, tempResult);
	}

	private void combinePairs(Map<Integer, List<Character>> pairs, int index,	char[] tempResult) {
		if (index == pairs.size()) {
			int i=0;
			for(Integer integer : pairs.keySet()){
				//System.out.println("setting char " + tempResult[i] + " at " + integer);
				accountNumber.setCharAt(integer, tempResult[i++]);
			}
		
			if(isValid(accountNumber.toString())){
				int difference = OCRDigit.getHammingDistance(receivedAccountNumber, accountNumber.toString());
				if(validAccountNumbersMap.containsKey(difference)){
					ArrayList<String> acNoList = validAccountNumbersMap.get(difference);
					acNoList.add(accountNumber.toString());
					validAccountNumbersMap.put(difference, acNoList);
				}else{
					validAccountNumbersMap.put(difference, new ArrayList<String>(){{ add(accountNumber.toString()); }});
				}
			}else{
				possibleNumbersList.add(accountNumber.toString());
			}
	        return;
	    }
		
		List<Character> current = storedPairsMap.get(index);
		if(current == null){
			int i=0;
			for(Entry<Integer, List<Character>> entry : pairs.entrySet()){
				if(i == index){
					current = entry.getValue();
					storedPairsMap.put(index, current);
					System.out.println(String.format("n=%d, pairs.get(n)=%s ", index, current.toString() ));
				}
				i++;
			}
		}
		
		for(char c : current){
			tempResult[index] = c;
			combinePairs(pairs, index + 1, tempResult);
		}
	}
}
