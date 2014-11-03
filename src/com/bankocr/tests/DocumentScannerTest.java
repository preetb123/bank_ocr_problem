package com.bankocr.tests;

import static org.junit.Assert.*;

import junit.framework.Assert;

import org.junit.Test;

import com.bankocr.DocumentScanner;

public class DocumentScannerTest {
	
	@Test
	public void testInvalidEntryIfLinesNotEqualToFour() throws Exception{
		String[] lines = {"asdas", "sfdfs", "sdf"};
		assertFalse("Lines not equal to 4", DocumentScanner.isValidEntry(lines));
	}
	
	@Test
	public void testInvalidEntryIfLineDoesNotHave27Characters() throws Exception {
		String[] lines = {"asdas", "sfdfs", "sdf", "fdsfdfs"};
		assertFalse("Line is not 27 characters long", DocumentScanner.isValidEntry(lines));
	}
	
	@Test
	public void testInvalidEntryIfLineContainsInvalidCharacters() throws Exception {
		// Test if lines contains characters other than space, pipe and underscore.
		String[] lines = {"asdassjh uihgtft | hjiyhujn", "sfdfslmnbvcxzasdfghjkjhgflk", 
							"sdflkjhgtyuhgtyhbnvgftyhnbg", "mnbvcxzaqwertygfdsaxcvbhnj"};
		assertFalse("Line contains characters other than space, pipe or underscore", DocumentScanner.isValidEntry(lines));
	}
	
	@Test
	public void testInvalidEntryIfFourthLineIsNotEmpty() throws Exception {
		String[] lines = {"asdassjh uihgtft | hjiyhujn", "sfdfslmnbvcxzasdfghjkjhgflk", 
				"sdflkjhgtyuhgtyhbnvgftyhnbg", "mnbvcxzaqwertygfdsaxcvbhnj"};
		assertFalse("Fourth line is not empty", DocumentScanner.isValidEntry(lines));
	}
	
	@Test
	public void testIsValidEntry() throws Exception {
		String[] lines = {" _  _  _  _  _  _  _  _  _ ",
						  "|_||_||_||_||_||_||_||_||_|",
						  "  ||_  _|  | _||_|  ||_| _ ",
						  "                           "};
		assertTrue(DocumentScanner.isValidEntry(lines));
	}
	
	@Test
	public void testInvalidAccountNumber() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(" _  _  _  _  _  _  _  _  _ ").append(DocumentScanner.LINE_SEPARATOR);
		sb.append(" _| _| _| _| _| _| _| _| _|").append(DocumentScanner.LINE_SEPARATOR);
		sb.append("|_ |_ |_ |_ |_ |_ |_ |_ |_ ").append(DocumentScanner.LINE_SEPARATOR);
		sb.append("                           ").append(DocumentScanner.LINE_SEPARATOR);
		
		assertEquals("222222222 ILL", DocumentScanner.getScannedAccountNumber(sb));
	}
	
	@Test
	public void testScannedAccountNumberWithAllZeros() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(" _  _  _  _  _  _  _  _  _ ").append(DocumentScanner.LINE_SEPARATOR);
		sb.append("| || || || || || || || || |").append(DocumentScanner.LINE_SEPARATOR);
		sb.append("|_||_||_||_||_||_||_||_||_|").append(DocumentScanner.LINE_SEPARATOR);
		sb.append("                           ").append(DocumentScanner.LINE_SEPARATOR);
		
		assertEquals("000000000", DocumentScanner.getScannedAccountNumber(sb));
	}
	
	public void testGetScannedAccountNumber() throws Exception {
		 StringBuilder sb  = new StringBuilder();
		 sb.append(" _     _  _  _  _  _  _    ").append(DocumentScanner.LINE_SEPARATOR);
		 sb.append("| || || || || || || ||_   |").append(DocumentScanner.LINE_SEPARATOR);
		 sb.append("|_||_||_||_||_||_||_| _|  |").append(DocumentScanner.LINE_SEPARATOR);
		 sb.append("                           ").append(DocumentScanner.LINE_SEPARATOR);
		// Here only one digit guessed, and replaced in place
		Assert.assertEquals("000000051", DocumentScanner.getScannedAccountNumber(sb));
	}
}
