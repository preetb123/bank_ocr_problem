package com.bankocr.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;

import com.bankocr.OCRDigit;
import com.bankocr.OCRDigit.ParsedDigit;

public class OCRDigitTest {

	@Test
	public void testParsedDigitForLegibleCharacterFive() {
		String ocrFiveString = " _ " +
							   "|_ " +
							   " _|";
		ParsedDigit parsedDigit = OCRDigit.getParsedDigit(ocrFiveString);
		assertThat(Arrays.asList('5'), is(parsedDigit.getGuessedDigits()));
	}
	
	@Test
	public void testParsedDigitForIllegibleCharactersFive() {
		String ocrFiveString = " _ " +
							   " _ " +
							   " _|";
		assertEquals(2, OCRDigit.getParsedDigit(ocrFiveString).getGuessedDigits().size());
		assertThat(OCRDigit.getParsedDigit(ocrFiveString).getGuessedDigits(), IsCollectionContaining.hasItems('3', '5'));
	}

	@Test
	public void testParsedDigitForLegibleCharacterNine() {
		String ocrNineString = " _ " +
							   "|_|" +
							   " _|";
		
		assertThat(Arrays.asList('9'), is(OCRDigit.getParsedDigit(ocrNineString).getGuessedDigits()));
	}
	
	@Test
	public void testParsedDigitForIllegibleCharacterNine(){
		String ocrNineString = " _ " +
							   "|_|" +
							   " _ ";
		
		assertEquals(1, OCRDigit.getParsedDigit(ocrNineString).getGuessedDigits().size());
		assertThat(OCRDigit.getParsedDigit(ocrNineString).getGuessedDigits(), IsCollectionContaining.hasItem('9'));
	}
	
	@Test
	public void testParsedDigitForIllegibleCharFourOrOne() throws Exception {
		String ocrFiveString = "   " +
							   " _|" +
							   "  |";
		assertEquals(2, OCRDigit.getParsedDigit(ocrFiveString).getGuessedDigits().size());
		assertThat(OCRDigit.getParsedDigit(ocrFiveString).getGuessedDigits(), IsCollectionContaining.hasItems('1', '4'));
	}
	
	@Test
	public void testPossibleDigitsForOne() {
		String ocrNineString = "   " +
				   			   "  |" +
				   			   "  |";
		assertEquals(1, OCRDigit.getPossibleDigits(ocrNineString).size());
		assertEquals(Arrays.asList('7'), OCRDigit.getPossibleDigits(ocrNineString));
	}

	@Test
	public void testPossibleDigitsForTwo() {
		String ocrChar = " _ " +
			    	     " _|" +
			    	     "|_ ";
		assertEquals(0, OCRDigit.getPossibleDigits(ocrChar).size());
		assertEquals(Arrays.asList(), OCRDigit.getPossibleDigits(ocrChar));
	}
	
	@Test
	public void testPossibleDigitsForThree() {
		String ocrChar = " _ " +
			    	     " _|" +
			    	     " _|";
		assertEquals(1, OCRDigit.getPossibleDigits(ocrChar).size());
		assertEquals(Arrays.asList('9'), OCRDigit.getPossibleDigits(ocrChar));
	}
	
	@Test
	public void testPossibleDigitsForEight() {
		String ocrChar = " _ " +
			    	     "|_|" +
			    	     "|_|";
		assertEquals(3, OCRDigit.getPossibleDigits(ocrChar).size());
		assertThat(OCRDigit.getPossibleDigits(ocrChar), IsCollectionContaining.hasItems('9', '0', '6'));
	}
	
	@Test
	public void testGetHammingDistanceOnOcrDigits() {
		String charOneString = "   "+
							   "  |"+
							   "  |";
		String charSevenString = " _ "+
								 "  |"+
								 "  |";
		assertEquals(1, OCRDigit.getHammingDistance(charOneString, charSevenString));
	}
	
	@Test
	public void testGetHammingDistanceOnString(){
		String accountNumber1 = "123456789";
		String accountNumber2 = "123456780";
		assertEquals(1, OCRDigit.getHammingDistance(accountNumber1, accountNumber2));
	}
	
	public void testHammingDistanceForEqualStringsIsZero(){
		String accountNumber1 = "123456789";
		String accountNumber2 = "123456789";
		assertEquals(0, OCRDigit.getHammingDistance(accountNumber1, accountNumber2));
	}

	@Test
	public void testGetOCRDigitString() {
		String ocrNineString = " _ " +
							   "|_|" +
							   " _|";
		assertEquals(ocrNineString, OCRDigit.getOCRDigitString('9'));
	}
}
