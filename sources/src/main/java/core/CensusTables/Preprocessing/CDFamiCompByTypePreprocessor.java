package core.CensusTables.Preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import core.HardcodedData;
import core.TextFileHandler;
import core.HardcodedData.HholdTypes;

public class CDFamiCompByTypePreprocessor {
	private static final String CHAR_family = "family";
	private static final String CHAR_female = "female";
	private static final String CHAR_male = "male";

	private static HashMap<String,int[]> ccdIDMaleByHhTypeMap;
	private static HashMap<String,int[]> ccdIDFemaleByHhTypeMap;
	private static HashMap<String,int[]> ccdIDHholdByTypeMap;
	
	public static HashMap<String, int[]> getCcdIDMaleByHhTypeMap() {
		return ccdIDMaleByHhTypeMap;
	}

	public static HashMap<String, int[]> getCcdIDFemaleByHhTypeMap() {
		return ccdIDFemaleByHhTypeMap;
	}

	public static HashMap<String, int[]> getCcdIDHholdByTypeMap() {
		return ccdIDHholdByTypeMap;
	}
	
	private enum FamilyCompByTypeCol {
		census_year(0),
		ccd_code(1),
		family_composition(2),
		family_or_persons(3),
		fp_count(4);
		
		private int colIndex;
		
		private FamilyCompByTypeCol(int newIndex) {
			colIndex = newIndex;
		}
		
		public int getColIndex() {
			return colIndex;
		}
	}
	
	private static HashMap<String,HholdTypes> hhTypeTextMap;
	private static void setHhTypeTextMap() {
		hhTypeTextMap = new HashMap<String, HholdTypes>();
		hhTypeTextMap.put("Couple family with no children", HholdTypes.HF1);
		hhTypeTextMap.put("Couple family with children under 15 and dependent students and non dependent children", HholdTypes.HF2);
		hhTypeTextMap.put("Couple family with children under 15 and dependent students and no non dependent children", HholdTypes.HF3);
		hhTypeTextMap.put("Couple family with children under 15 and no dependent students and non dependent children", HholdTypes.HF4);
		hhTypeTextMap.put("Couple family with children under 15 and no dependent students and no non dependent children", HholdTypes.HF5);
		hhTypeTextMap.put("Couple family with no children under 15 and dependent students and non dependent children", HholdTypes.HF6);
		hhTypeTextMap.put("Couple family with no children under 15 and dependent students and no non dependent children", HholdTypes.HF7);
		hhTypeTextMap.put("Couple family with no children under 15 and no dependent students and non dependent children", HholdTypes.HF8);
		hhTypeTextMap.put("One parent family with children under 15 and dependent students and non dependent children", HholdTypes.HF9);
		hhTypeTextMap.put("One parent family with children under 15 and dependent students and no non dependent children", HholdTypes.HF10);
		hhTypeTextMap.put("One parent family with children under 15 and no dependent students and non dependent children", HholdTypes.HF11);
		hhTypeTextMap.put("One parent family with children under 15 and no dependent students and no non dependent children", HholdTypes.HF12);
		hhTypeTextMap.put("One parent family with no children under 15 and dependent students and non dependent children", HholdTypes.HF13);
		hhTypeTextMap.put("One parent family with no children under 15 and dependent students and no non dependent children", HholdTypes.HF14);
		hhTypeTextMap.put("One parent family with no children under 15 and no dependent students and non dependent children", HholdTypes.HF15);
		hhTypeTextMap.put("Other family", HholdTypes.HF16);
	}
	
	
	public static void readInCensusTables(String fileNameFamilyCompByType) {
		
		/*
		 * reads in family composition by household type
		 */
		ccdIDMaleByHhTypeMap = new HashMap<String,int[]>();
		ccdIDFemaleByHhTypeMap = new HashMap<String,int[]>();
		ccdIDHholdByTypeMap = new HashMap<String,int[]>();
		setHhTypeTextMap();
		
		readInFamilyCompositionByType(fileNameFamilyCompByType);
		writeFamilyCompByTypeToCSV();
	}
	
	/**
	 * 
	 * @param fileNameFamilyCompByType
	 */
	private static void readInFamilyCompositionByType(String fileNameFamilyCompByType) {
		ArrayList<ArrayList<String>> rawData = TextFileHandler.readCSV(fileNameFamilyCompByType);
		
		for (ArrayList<String> line : rawData) {
			Integer ccdIDInt = null;
			Integer count = null;
			try {
				ccdIDInt = Integer.parseInt(line.get(FamilyCompByTypeCol.ccd_code.getColIndex()));
				count = Integer.parseInt(line.get(FamilyCompByTypeCol.fp_count.getColIndex()));
			} catch (Exception e) {
				continue;
			}
			String ccdID = Integer.toString(ccdIDInt);
			String familyComp = line.get(FamilyCompByTypeCol.family_composition.getColIndex());
			String familyOrPersons = line.get(FamilyCompByTypeCol.family_or_persons.getColIndex());
			int hhTypeIndex = hhTypeTextMap.get(familyComp).getIndex();
			
			if (familyOrPersons.equals(CHAR_family)) {
				int[] hhByType = new int[HholdTypes.values().length];
				if (ccdIDHholdByTypeMap.containsKey(ccdID))
					hhByType = ccdIDHholdByTypeMap.get(ccdID);
				hhByType[hhTypeIndex] = (int)count;
				ccdIDHholdByTypeMap.put(ccdID, hhByType);
			} else if (familyOrPersons.equals(CHAR_female)) {
				int[] femaleByHhType = new int[HholdTypes.values().length];
				if (ccdIDFemaleByHhTypeMap.containsKey(ccdID)) 
					femaleByHhType = ccdIDFemaleByHhTypeMap.get(ccdID);
				femaleByHhType[hhTypeIndex] = (int)count;
				ccdIDFemaleByHhTypeMap.put(ccdID, femaleByHhType);
			} else if (familyOrPersons.equals(CHAR_male)) {
				int[] maleByHhType = new int[HholdTypes.values().length];
				if (ccdIDMaleByHhTypeMap.containsKey(ccdID)) 
					maleByHhType = ccdIDMaleByHhTypeMap.get(ccdID);
				maleByHhType[hhTypeIndex] = (int)count;
				ccdIDMaleByHhTypeMap.put(ccdID,maleByHhType);
			}
		}
	}
	
	
	/**
	 * 
	 */
	private static void writeFamilyCompByTypeToCSV() {
		try {
			FileUtils.forceMkdir(new File(HardcodedData.ccdInputPathMaleByHhType));
			FileUtils.forceMkdir(new File(HardcodedData.ccdInputPathFemaleByHhType));
			FileUtils.forceMkdir(new File(HardcodedData.ccdInputPathHholdByHhType));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String ccdID : ccdIDMaleByHhTypeMap.keySet()) {
			int[] maleByHhType = ccdIDMaleByHhTypeMap.get(ccdID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhType","count"});
			for (int i=0; i<=maleByHhType.length-1; i++) {
				strOut.add(new String[] {HholdTypes.getHholdTypeFromIndex(i).toString(), Integer.toString(maleByHhType[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.ccdInputPathMaleByHhType + ccdID + "_" + HardcodedData.charMaleByHhType + ".csv", null, strOut);
		}
		
		for (String ccdID : ccdIDFemaleByHhTypeMap.keySet()) {
			int[] femaleByHhType = ccdIDFemaleByHhTypeMap.get(ccdID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhType","count"});
			for (int i=0; i<=femaleByHhType.length-1; i++) {
				strOut.add(new String[] {HholdTypes.getHholdTypeFromIndex(i).toString(), Integer.toString(femaleByHhType[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.ccdInputPathFemaleByHhType + ccdID + "_" + HardcodedData.charFemaleByHhType +".csv", null, strOut);
		}
		
		for (String ccdID : ccdIDHholdByTypeMap.keySet()) {
			int[] hhByType = ccdIDHholdByTypeMap.get(ccdID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhType","count"});
			for (int i=0; i<=hhByType.length-1; i++) {
				strOut.add(new String[] {HholdTypes.getHholdTypeFromIndex(i).toString(), Integer.toString(hhByType[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.ccdInputPathHholdByHhType + ccdID + "_" + HardcodedData.charHholdByHhType + ".csv", null, strOut);
		}
	}
}
