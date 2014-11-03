package com.bankocr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class BankOcr {
	
	public static final boolean DEBUG = false;
	
	public static void main(String[] args) {
		FileInputStream fis = null;
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			
			fis = new FileInputStream("test.input");
			reader = new BufferedReader(new InputStreamReader(fis));
			File outputFile = new File("output.txt");
			if(outputFile.exists() && outputFile.length() > 0){
				outputFile.delete();
			}
			writer = new PrintWriter(new File("output.txt"));
			
			StringBuilder builder = new StringBuilder();
			String line = reader.readLine();
			int lineCount = 0;
			while(line != null){
				System.out.println(line);
				builder.append(line).append(DocumentScanner.LINE_SEPARATOR);
				++lineCount;
				line = reader.readLine();
				if(lineCount == 4){
					System.out.println("*******************************************************************************");
					String accountNumberString = DocumentScanner.getScannedAccountNumber(builder);
					writer.println(String.format("%s=> %s", builder.toString(), accountNumberString));
					System.out.println("Account Number : " + accountNumberString);
					lineCount = 0;
					builder.setLength(0);
				}
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("Input file unavailable. Please Verify the name and path of the file.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				fis.close();
				reader.close();
				writer.close();				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
