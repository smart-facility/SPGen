package core.CensusTables.Preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import core.HardcodedData;
import core.HardcodedData.HholdTypes;
import core.TextFileHandler;

public class SLAFamiCompByTypePreprocessor {
	private static final String CHAR_BLANK = ""; 
	private static final String CHAR_SPACE = " ";
	private static final String CHAR_female = "female";
	private static final String CHAR_male = "male";

	private static HashMap<String,String> slaIDNameMapFamiComp;
	private static HashMap<String,int[]> slaIDMaleByHhTypeMap;
	private static HashMap<String,int[]> slaIDFemaleByHhTypeMap;
	private static HashMap<String,int[]> slaIDHholdByTypeMap;
	
	public static HashMap<String, String> getSlaIDNameMapFamiComp() {
		return slaIDNameMapFamiComp;
	}

	public static HashMap<String, int[]> getSlaIDMaleByHhTypeMap() {
		return slaIDMaleByHhTypeMap;
	}

	public static HashMap<String, int[]> getSlaIDFemaleByHhTypeMap() {
		return slaIDFemaleByHhTypeMap;
	}

	public static HashMap<String, int[]> getSlaIDHholdByTypeMap() {
		return slaIDHholdByTypeMap;
	}

	private enum FamilyCompByTypeCol {
		census_year(0),
		sla_code(1),
		sla_name(2),
		full_variable_name(3),
		family_composition(4),
		family_or_persons(5),
		fp_count(6);
		
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
		slaIDNameMapFamiComp = new HashMap<String,String>();
		slaIDMaleByHhTypeMap = new HashMap<String,int[]>();
		slaIDFemaleByHhTypeMap = new HashMap<String,int[]>();
		slaIDHholdByTypeMap = new HashMap<String,int[]>();
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
			Integer slaIDInt = null;
			Integer count = null;
			try {
				slaIDInt = Integer.parseInt(line.get(FamilyCompByTypeCol.sla_code.getColIndex()));
				count = Integer.parseInt(line.get(FamilyCompByTypeCol.fp_count.getColIndex()));
			} catch (Exception e) {
				continue;
			}
			String slaID = Integer.toString(slaIDInt);
			String slaName = line.get(FamilyCompByTypeCol.sla_name.getColIndex());
			String fullVariableName = line.get(FamilyCompByTypeCol.full_variable_name.getColIndex());
			String familyComp = line.get(FamilyCompByTypeCol.family_composition.getColIndex());
			if (!hhTypeTextMap.containsKey(familyComp)) {
				continue;
			}
			int hhTypeIndex = hhTypeTextMap.get(familyComp).getIndex();
			
			if (fullVariableName.equals(CHAR_BLANK)) {
				int[] hhByType = new int[HholdTypes.values().length];
				if (slaIDHholdByTypeMap.containsKey(slaID))
					hhByType = slaIDHholdByTypeMap.get(slaID);
				hhByType[hhTypeIndex] = (int)count;
				slaIDHholdByTypeMap.put(slaID, hhByType);
			} else {
				String[] fullVarNameParts = fullVariableName.split(CHAR_SPACE);
				String finWord = fullVarNameParts[fullVarNameParts.length-1];
				
				if (finWord.equals(CHAR_female)) {
					int[] femaleByHhType = new int[HholdTypes.values().length];
					if (slaIDFemaleByHhTypeMap.containsKey(slaID)) 
						femaleByHhType = slaIDFemaleByHhTypeMap.get(slaID);
					femaleByHhType[hhTypeIndex] = (int)count;
					slaIDFemaleByHhTypeMap.put(slaID, femaleByHhType);
				} else if (finWord.equals(CHAR_male)) {
					int[] maleByHhType = new int[HholdTypes.values().length];
					if (slaIDMaleByHhTypeMap.containsKey(slaID)) 
						maleByHhType = slaIDMaleByHhTypeMap.get(slaID);
					maleByHhType[hhTypeIndex] = (int)count;
					slaIDMaleByHhTypeMap.put(slaID,maleByHhType);
				}
			}
			
			if (!slaIDNameMapFamiComp.containsKey(slaID)) {
				slaIDNameMapFamiComp.put(slaID, slaName);
			}
		}
	}
	
	/**
	 * 
	 */
	private static void writeFamilyCompByTypeToCSV() {
		try {
			FileUtils.forceMkdir(new File(HardcodedData.slaInputPathMaleByHhType));
			FileUtils.forceMkdir(new File(HardcodedData.slaInputPathFemaleByHhType));
			FileUtils.forceMkdir(new File(HardcodedData.slaInputPathHholdByHhType));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String slaID : slaIDMaleByHhTypeMap.keySet()) {
			int[] maleByHhType = slaIDMaleByHhTypeMap.get(slaID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhType","count"});
			for (int i=0; i<=maleByHhType.length-1; i++) {
				strOut.add(new String[] {HholdTypes.getHholdTypeFromIndex(i).toString(), Integer.toString(maleByHhType[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.slaInputPathMaleByHhType + slaID + "_" + HardcodedData.charMaleByHhType + ".csv", null, strOut);
		}
		
		for (String slaID : slaIDFemaleByHhTypeMap.keySet()) {
			int[] femaleByHhType = slaIDFemaleByHhTypeMap.get(slaID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhType","count"});
			for (int i=0; i<=femaleByHhType.length-1; i++) {
				strOut.add(new String[] {HholdTypes.getHholdTypeFromIndex(i).toString(), Integer.toString(femaleByHhType[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.slaInputPathFemaleByHhType + slaID + "_" + HardcodedData.charFemaleByHhType +".csv", null, strOut);
		}
		
		for (String slaID : slaIDHholdByTypeMap.keySet()) {
			int[] hhByType = slaIDHholdByTypeMap.get(slaID);
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			strOut.add(new String[] {"hhType","count"});
			for (int i=0; i<=hhByType.length-1; i++) {
				strOut.add(new String[] {HholdTypes.getHholdTypeFromIndex(i).toString(), Integer.toString(hhByType[i])});
			}
			TextFileHandler.writeToCSV(HardcodedData.slaInputPathHholdByHhType + slaID + "_" + HardcodedData.charHholdByHhType + ".csv", null, strOut);
		}
	}
}
