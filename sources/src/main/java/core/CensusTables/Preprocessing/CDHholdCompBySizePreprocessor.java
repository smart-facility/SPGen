package core.CensusTables.Preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import core.HardcodedData;
import core.TextFileHandler;
import core.HardcodedData.B30Rows;

public class CDHholdCompBySizePreprocessor {
	private static final String CHAR_Family_household = "family household";
	private static final String CHAR_Non_Family_household = "non family household";
	
	private static HashMap<String,int[]> ccdIDHFCompBySizeMap;
	private static HashMap<String,int[]> ccdIDNFCompBySizeMap;

	public static HashMap<String, int[]> getCcdIDHFCompBySizeMap() {
		return ccdIDHFCompBySizeMap;
	}

	public static HashMap<String, int[]> getCcdIDNFCompBySizeMap() {
		return ccdIDNFCompBySizeMap;
	}

	private enum HholdCompBySizeCol {
		census_year(0),
		ccd_code(1),
		number_of_persons(2),
		household_type(3),
		household_count(4);
		
		private int colIndex;
		
		private HholdCompBySizeCol(int newIndex) {
			colIndex = newIndex;
		}
		
		public int getColIndex() {
			return colIndex;
		}
	}
	
	private static HashMap<String,B30Rows> hhSizeTextMap;
	private static void setHhSizeTextMap() {
		hhSizeTextMap = new HashMap<String, B30Rows>();
		hhSizeTextMap.put("one", B30Rows.one);
		hhSizeTextMap.put("two", B30Rows.two);
		hhSizeTextMap.put("three", B30Rows.three);
		hhSizeTextMap.put("four", B30Rows.four);
		hhSizeTextMap.put("five", B30Rows.five);
		hhSizeTextMap.put("six or more", B30Rows.six);
		
	}
	
	public static void readInCensusTables(String fileNameHholdCompBySize) {
		/*
		 * reads in household composition by household size
		 */
		ccdIDHFCompBySizeMap = new HashMap<String, int[]>();
		ccdIDNFCompBySizeMap = new HashMap<String, int[]>();
		setHhSizeTextMap();
		
		readInHholdCompBySize(fileNameHholdCompBySize);
		writeHholdCompBySizeToCSV();
	}
	
	/**
	 * 
	 * @param fileNameFamilyCompByType
	 */
	private static void readInHholdCompBySize(String fileNameHholdCompBySize) {
		ArrayList<ArrayList<String>> rawData = TextFileHandler.readCSV(fileNameHholdCompBySize);
		
		for (ArrayList<String> line : rawData) {
			Integer ccdIDInt = null;
			Integer count = null;
			
			try {
				ccdIDInt = Integer.parseInt(line.get(HholdCompBySizeCol.ccd_code.getColIndex()));
				// if the number of elements in this line is 1 less than number of elements in other lines, 
				// it is because the value of last element (household_count) is not available.
				// this must be because the line is corresponding to household_type 'family household' and number_of_persons 'one'.
				// assigns value -1 to count for lines like this. 
				if (line.size()==HholdCompBySizeCol.values().length-1) {  
					count = -1;
				} else {
					count = Integer.parseInt(line.get(HholdCompBySizeCol.household_count.getColIndex()));
				}
				
			} catch (Exception e) {
				continue;
			}
			String numPersons = line.get(HholdCompBySizeCol.number_of_persons.getColIndex());
			String ccdID = Integer.toString(ccdIDInt);
			String hhType = line.get(HholdCompBySizeCol.household_type.getColIndex());
			Integer nPersons = hhSizeTextMap.get(numPersons).getValue();
			
			if (hhType.equals(CHAR_Family_household)) {
				int[] hfComp = new int[hhSizeTextMap.size()];
				if (ccdIDHFCompBySizeMap.containsKey(ccdID)) {
					hfComp = ccdIDHFCompBySizeMap.get(ccdID);
				}
				hfComp[nPersons-1] = count;
				ccdIDHFCompBySizeMap.put(ccdID,hfComp);
			} else if (hhType.equals(CHAR_Non_Family_household)) {
				int[] nfComp = new int[hhSizeTextMap.size()];
				if (ccdIDNFCompBySizeMap.containsKey(ccdID)) {
					nfComp = ccdIDNFCompBySizeMap.get(ccdID);
				}
				nfComp[nPersons-1] = count;
				ccdIDNFCompBySizeMap.put(ccdID,nfComp);
			}
		}
	}
	
	
	/**
	 * 
	 */
	private static void writeHholdCompBySizeToCSV() {
		try {
			FileUtils.forceMkdir(new File(HardcodedData.ccdInputPathHFCompBySize));
			FileUtils.forceMkdir(new File(HardcodedData.ccdInputPathNFCompBySize));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String ccdID : ccdIDHFCompBySizeMap.keySet()) {
			int[] hfComp = ccdIDHFCompBySizeMap.get(ccdID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhSize","count"});
			for (int i=0; i<=hfComp.length-1; i++) {
				strOut.add(new String[] {Integer.toString((i+1)), Integer.toString(hfComp[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.ccdInputPathHFCompBySize + ccdID + "_" + HardcodedData.charHFCompBySize + ".csv", null, strOut);
		}
		
		for (String ccdID : ccdIDNFCompBySizeMap.keySet()) {
			int[]nfComp = ccdIDNFCompBySizeMap.get(ccdID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhSize","count"});
			for (int i=0; i<=nfComp.length-1; i++) {
				strOut.add(new String[] {Integer.toString((i+1)), Integer.toString(nfComp[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.ccdInputPathNFCompBySize + ccdID + "_" + HardcodedData.charNFCompBySize + ".csv", null, strOut);
		}
	}
}
