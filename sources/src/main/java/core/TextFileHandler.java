package core;

import java.io.*;
import java.util.*;

import au.com.bytecode.opencsv.CSVWriter;

public class TextFileHandler {
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static ArrayList<ArrayList<String>> readCSV (String fileName) {
		ArrayList<ArrayList<String>> sheetData = new ArrayList<ArrayList<String>>();
		BufferedReader br = null;
		
		try {
			 // create BufferedReader to read csv file containing data
            br = new BufferedReader(new FileReader(fileName));
            String strLine = "";

            StringTokenizer st = null;

            // read comma separated file line by line
            while ((strLine = br.readLine()) != null) {

                // break comma separated line using ","
                st = new StringTokenizer(strLine, ",");

                ArrayList<String> data = new ArrayList<String>();

                while (st.hasMoreTokens()) {
                    // display csv file
                    String editedValue = String.valueOf(st.nextToken())
                            .replace("\"", "").trim();

                    data.add(editedValue);
                }

                sheetData.add(data);
            }
            br.close();
		} catch (Exception e) {
			//logger.error("Exception while reading csv file: ", e);
		}
		
		return sheetData;
	}
	
	
	/**
	 * 
	 * @param filename
	 * @param header
	 * @param dataout
	 */
	public static void writeToCSV(String filename, String[] header, List<String[]> dataout) {
		CSVWriter csvWriter = null;
		try {
			Writer fw = new BufferedWriter(new FileWriter(filename));
			csvWriter = new CSVWriter(fw, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
			
			csvWriter.writeNext(header);
			csvWriter.writeAll(dataout);

			csvWriter.flush();
		} catch (IOException e) {
			System.out.println("Failed to write content to file:" + filename);
		} finally {
			if (csvWriter != null) {
				try {
					csvWriter.close();
				} catch (IOException e) {
					System.out.println("Failed to close file after writing: ");
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @param filename
	 * @param header
	 * @param dataout
	 */
	public static void writeToCSV(String filename, String[] header, List<String[]> dataout, boolean append) {
		CSVWriter csvWriter = null;
		try {
			Writer fw = new BufferedWriter(new FileWriter(filename, append));
			csvWriter = new CSVWriter(fw, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
			
			csvWriter.writeNext(header);
			csvWriter.writeAll(dataout);

			csvWriter.flush();
		} catch (IOException e) {
			System.out.println("Failed to write content to file:" + filename);
		} finally {
			if (csvWriter != null) {
				try {
					csvWriter.close();
				} catch (IOException e) {
					System.out.println("Failed to close file after writing: ");
				}
			}
		}
	}
	
	
	public static void writeToCSV(String filename, String[] header, double[][] dataout) {
		ArrayList<String[]> strOut = new ArrayList<String[]>();
		for (int i=0; i<=dataout.length-1; i++) {
			String[] dataRow = new String[dataout[i].length];
			for (int j=0; j<=dataRow.length-1; j++) {
				dataRow[j] = Double.toString(dataout[i][j]);
			}
			strOut.add(dataRow);
		}
		 writeToCSV(filename, header, strOut);
	}
	
	
	/**
	 * 
	 * @param filename
	 * @param header
	 * @param dataout
	 */
	public static void writeToCSV(String filename, String[] header, HashMap<Integer,Integer> dataout) {
		ArrayList<String[]> strOut = new ArrayList<String[]>();
		for (Integer key : dataout.keySet()) {
			String[] str = new String[] {String.valueOf(key), String.valueOf(dataout.get(key))};
			strOut.add(str);
		}
		writeToCSV(filename, header, strOut);
	}
	
	
	/**
	 * 
	 * @param filename
	 * @param header
	 * @param dataout
	 */
	public static void writeToCSVIntegerDouble(String filename, String[] header, HashMap<Integer,Double> dataout, boolean append) {
		ArrayList<String[]> strOut = new ArrayList<String[]>();
		for (Integer key : dataout.keySet()) {
			String[] str = new String[] {String.valueOf(key), String.valueOf(dataout.get(key))};
			strOut.add(str);
		}
		writeToCSV(filename, header, strOut, append);
	}
}
