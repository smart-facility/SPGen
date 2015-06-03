package core.CensusTables.Preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import core.HardcodedData.B30Rows;
import core.HardcodedData;
import core.TextFileHandler;

public class SLAHholdCompBySizePreprocessor {
	
	private static final String CHAR_Family_household = "Family household";
	private static final String CHAR_Non_Family_household = "Non Family household";
	
	private static HashMap<String,String> slaIDNameMapHholdComp;
	private static HashMap<String,int[]> slaIDHFCompBySizeMap;
	private static HashMap<String,int[]> slaIDNFCompBySizeMap;
	
	public static HashMap<String, String> getSlaIDNameMapHholdComp() {
		return slaIDNameMapHholdComp;
	}


	public static HashMap<String, int[]> getSlaIDHFCompBySizeMap() {
		return slaIDHFCompBySizeMap;
	}


	public static HashMap<String, int[]> getSlaIDNFCompBySizeMap() {
		return slaIDNFCompBySizeMap;
	}

	private enum HholdCompBySizeCol {
		census_year(0),
		sla_code(1),
		sla_name(2),
		number_of_persons(3),
		household_type(4),
		household_count(5);
		
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
		hhSizeTextMap.put("1", B30Rows.one);
		hhSizeTextMap.put("2", B30Rows.two);
		hhSizeTextMap.put("3", B30Rows.three);
		hhSizeTextMap.put("4", B30Rows.four);
		hhSizeTextMap.put("5", B30Rows.five);
		hhSizeTextMap.put("6 or more", B30Rows.six);
		
	}
	
	public static void readInCensusTables(String fileNameHholdCompBySize) {
		/*
		 * reads in household composition by household size
		 */
		slaIDNameMapHholdComp = new HashMap<String, String>();
		slaIDHFCompBySizeMap = new HashMap<String, int[]>();
		slaIDNFCompBySizeMap = new HashMap<String, int[]>();
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
			Integer slaIDInt = null;
			Integer count = null;
			
			try {
				slaIDInt = Integer.parseInt(line.get(HholdCompBySizeCol.sla_code.getColIndex()));
				count = Integer.parseInt(line.get(HholdCompBySizeCol.household_count.getColIndex()));
			} catch (Exception e) {
				continue;
			}
			String numPersons = line.get(HholdCompBySizeCol.number_of_persons.getColIndex());
			String slaID = Integer.toString(slaIDInt);
			String slaName = line.get(HholdCompBySizeCol.sla_name.getColIndex());
			String hhType = line.get(HholdCompBySizeCol.household_type.getColIndex());
			Integer nPersons = hhSizeTextMap.get(numPersons).getValue();
			
			if (hhType.equals(CHAR_Family_household)) {
				int[] hfComp = new int[hhSizeTextMap.size()];
				if (slaIDHFCompBySizeMap.containsKey(slaID)) {
					hfComp = slaIDHFCompBySizeMap.get(slaID);
				}
				hfComp[nPersons-1] = count;
				slaIDHFCompBySizeMap.put(slaID,hfComp);
			} else if (hhType.equals(CHAR_Non_Family_household)) {
				int[] nfComp = new int[hhSizeTextMap.size()];
				if (slaIDNFCompBySizeMap.containsKey(slaID)) {
					nfComp = slaIDNFCompBySizeMap.get(slaID);
				}
				nfComp[nPersons-1] = count;
				slaIDNFCompBySizeMap.put(slaID,nfComp);
			}
			
			if (!slaIDNameMapHholdComp.containsKey(slaID)) {
				slaIDNameMapHholdComp.put(slaID, slaName);
			}
		}
	}
	
	/**
	 * 
	 */
	private static void writeHholdCompBySizeToCSV() {
		try {
			FileUtils.forceMkdir(new File(HardcodedData.slaInputPathHFCompBySize));
			FileUtils.forceMkdir(new File(HardcodedData.slaInputPathNFCompBySize));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String slaID : slaIDHFCompBySizeMap.keySet()) {
			int[] hfComp = slaIDHFCompBySizeMap.get(slaID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhSize","count"});
			for (int i=0; i<=hfComp.length-1; i++) {
				strOut.add(new String[] {Integer.toString((i+1)), Integer.toString(hfComp[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.slaInputPathHFCompBySize + slaID + "_" + HardcodedData.charHFCompBySize + ".csv", null, strOut);
		}
		
		for (String slaID : slaIDNFCompBySizeMap.keySet()) {
			int[]nfComp = slaIDNFCompBySizeMap.get(slaID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhSize","count"});
			for (int i=0; i<=nfComp.length-1; i++) {
				strOut.add(new String[] {Integer.toString((i+1)), Integer.toString(nfComp[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.slaInputPathNFCompBySize + slaID + "_" + HardcodedData.charNFCompBySize + ".csv", null, strOut);
		}
	}
}
