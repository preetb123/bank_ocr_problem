package com.bankocr.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.bankocr.AccountNumber;

public class AccountNumberTest {
	
	private Map<Integer, List<Character>> errorPosMap = null;
	private Map<Integer, List<Character>> guessedDigitsMap = null;
	private StringBuilder parsedAccountNumberString = null;
	private AccountNumber accountNumber = null;

	@Test
	public void testAccountNumberIsInvalidIfDoesNotContainNineDigits() {
		parsedAccountNumberString = new StringBuilder("12345678");
		errorPosMap = new LinkedHashMap<Integer, List<Character>>();
		guessedDigitsMap = new LinkedHashMap<Integer, List<Character>>();
		accountNumber = new AccountNumber(parsedAccountNumberString, errorPosMap, guessedDigitsMap);
		assertFalse(accountNumber.containsValidCharacters());
		
		parsedAccountNumberString = new StringBuilder("123456787676723");
		accountNumber = new AccountNumber(parsedAccountNumberString, errorPosMap, guessedDigitsMap);
		assertFalse(accountNumber.containsValidCharacters());
	}
	
	@Test
	public void testAccountNumberContainsDigitsBetweenZeroToNine() throws Exception {
		
		parsedAccountNumberString = new StringBuilder("123456789");
		errorPosMap = new LinkedHashMap<Integer, List<Character>>();
		guessedDigitsMap = new LinkedHashMap<Integer, List<Character>>();
		
		accountNumber = new AccountNumber(parsedAccountNumberString, errorPosMap, guessedDigitsMap);
		assertTrue(accountNumber.containsValidCharacters());
		
		parsedAccountNumberString = new StringBuilder("000051180");
		accountNumber = new AccountNumber(parsedAccountNumberString, errorPosMap, guessedDigitsMap);
		assertTrue(accountNumber.containsValidCharacters());
		
		parsedAccountNumberString = new StringBuilder("00005118?");
		errorPosMap.put(8, new ArrayList<Character>(){{ add('3'); add('5'); }});
		accountNumber = new AccountNumber(parsedAccountNumberString, errorPosMap, guessedDigitsMap);
		assertTrue(accountNumber.containsValidCharacters());
	}
	
	@Test
	public void testAccountNumberIsInvalidIfContainsNonDigitCharacters() {
		parsedAccountNumberString = new StringBuilder("12ad5678@");
		errorPosMap = new LinkedHashMap<Integer, List<Character>>();
		guessedDigitsMap = new LinkedHashMap<Integer, List<Character>>();
		
		accountNumber = new AccountNumber(parsedAccountNumberString, errorPosMap, guessedDigitsMap);
		assertFalse(accountNumber.containsValidCharacters());
	}
	
	@Test
	public void testIfAccountNumberIsValidBasedOnChecksum() throws Exception {
		assertTrue("000000000 is a valid account number.", AccountNumber.isValid("000000000"));
		assertFalse("111111111 is an invalid account number." , AccountNumber.isValid("111111111"));
		assertFalse("222222222 is an invalid account number." , AccountNumber.isValid("222222222"));
		assertFalse("333333333 is an invalid account number." , AccountNumber.isValid("333333333"));
		assertTrue("123456789 is a valid account number." , AccountNumber.isValid("123456789"));
		assertFalse("490067715 is an invalid account number." , AccountNumber.isValid("490067715"));
		assertFalse("429498989 is an invalid account number." , AccountNumber.isValid("429498989"));
		assertTrue("429498969 is a valid account number." , AccountNumber.isValid("429498969"));
		assertTrue("985998989 is a valid account number." , AccountNumber.isValid("985998989"));
		assertFalse("123456784 is an invalid account number." , AccountNumber.isValid("123456784"));		
	}
	
	public void testResolveAccountNumbers() throws Exception {
		parsedAccountNumberString = new StringBuilder("12345678?");
		errorPosMap = new LinkedHashMap<Integer, List<Character>>(){{ 
									put(8, new ArrayList<Character>(){{ add('4'); add('9'); }} ); 
									}};
		guessedDigitsMap = new LinkedHashMap<Integer, List<Character>>();
		accountNumber = new AccountNumber(parsedAccountNumberString, errorPosMap, guessedDigitsMap);
		
		// 12345678? has two possible numbers, 123456784 and 123456789, of which only the later one is valid.
		Assert.assertEquals("123456789", accountNumber.resolveAccountNumbers());
		
		parsedAccountNumberString = new StringBuilder("?2345678?");
		errorPosMap = new LinkedHashMap<Integer, List<Character>>(){{ 
			put(0, new ArrayList<Character>(){{ add('1'); add('4'); }} );
			put(8, new ArrayList<Character>(){{ add('3'); add('5'); }} );
			}};
		guessedDigitsMap = new LinkedHashMap<Integer, List<Character>>();
		accountNumber = new AccountNumber(parsedAccountNumberString, errorPosMap, guessedDigitsMap);
		
		// ?2345678? has two four possible numbers, but none of them qualifies to be a valid number, so we try to guess the
		// other digits for which the number becomes a valid digit. 123456783 is the first possible but not valid number
		// which is nearest to become valid with single substitution
		Assert.assertEquals("123456783 AMB ['123456703', '123456185']", accountNumber.resolveAccountNumbers());
	}
}
