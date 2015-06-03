package core.CensusTables.Preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import core.HardcodedData;
import core.TextFileHandler;
import core.HardcodedData.*;


public class SLAHhRelByAgeBySexPreprocessor {
	private static final String CHAR_female = "female";
	private static final String CHAR_male = "male";
	//private static final int nValidHhRel = 8;
	
	private static HashMap<String,String> slaIDNameMapHhRel;
	private static HashMap<String,int[][]> slaIDhhRelMaleMap;
	private static HashMap<String,int[][]> slaIDhhRelFemaleMap;
	
	public static HashMap<String, String> getSlaIDNameMapHhRel() {
		return slaIDNameMapHhRel;
	}


	public static HashMap<String, int[][]> getSlaIDhhRelMaleMap() {
		return slaIDhhRelMaleMap;
	}


	public static HashMap<String, int[][]> getSlaIDhhRelFemaleMap() {
		return slaIDhhRelFemaleMap;
	}


	private enum HhRelByAgeBySexCol {
		census_year(0),	
		sla_code(1),
		sla_name(2),
		gender(3),
		age_band(4),
		household_relationship(5),
		person_count(6);
		
		private int colIndex;
		
		private HhRelByAgeBySexCol(int newColIndex) {
			colIndex = newColIndex;
		}
		
		public int getColIndex() {
			return colIndex;
		}

	}
	
	private static HashMap<String,AgeGroups> ageGroupTxtIndexMap;
	private static void setAgeGroupTxtIndexMap() {
		ageGroupTxtIndexMap = new HashMap<String, AgeGroups>();
		ageGroupTxtIndexMap.put("0-14",AgeGroups._0_14);
		ageGroupTxtIndexMap.put("15-24",AgeGroups._15_24);
		ageGroupTxtIndexMap.put("25-34",AgeGroups._25_34);
		ageGroupTxtIndexMap.put("35-44",AgeGroups._35_44);
		ageGroupTxtIndexMap.put("45-54",AgeGroups._45_54);
		ageGroupTxtIndexMap.put("55-64",AgeGroups._55_64);
		ageGroupTxtIndexMap.put("65-74",AgeGroups._65_74);
		ageGroupTxtIndexMap.put("75-84",AgeGroups._75_84);
		ageGroupTxtIndexMap.put("85-over",AgeGroups._85_99);
	}
	
	private static HashMap<String,HholdRelSP> hhRelTxtIndexMap;
	private static void setHhRelTxtIndexMap() {
		hhRelTxtIndexMap = new HashMap<String, HholdRelSP>();
		hhRelTxtIndexMap.put("Husband in a registered marriage", HholdRelSP.Married);
		hhRelTxtIndexMap.put("Wife in a registered marriage", HholdRelSP.Married);
		hhRelTxtIndexMap.put("Partner in de facto marriage", HholdRelSP.Married);
		hhRelTxtIndexMap.put("Lone parent", HholdRelSP.LoneParent);
		hhRelTxtIndexMap.put("Child under 15", HholdRelSP.U15Child);
		hhRelTxtIndexMap.put("Dependent student Aged 15 24 years", HholdRelSP.Student);
		hhRelTxtIndexMap.put("Non dependent child", HholdRelSP.O15Child);
		hhRelTxtIndexMap.put("Other related individual", HholdRelSP.Relative);
		hhRelTxtIndexMap.put("Unrelated individual living in family household", HholdRelSP.Relative);
		hhRelTxtIndexMap.put("Group household member", HholdRelSP.GroupHhold);
		hhRelTxtIndexMap.put("Lone person", HholdRelSP.LonePerson);
	}
	
	
	public static void readInCensusTables(String fileNameHhRelByAgeBySex) {
		
		/*
		 * reads in household relationship by age by sex
		 */
		setAgeGroupTxtIndexMap();
		setHhRelTxtIndexMap();
		slaIDNameMapHhRel = new HashMap<String,String>();
		slaIDhhRelMaleMap = new HashMap<String, int[][]>();
		slaIDhhRelFemaleMap = new HashMap<String, int[][]>();
		
		readInHhRelByAgeBySex(fileNameHhRelByAgeBySex);
		writeHhRelByAgeBySexToCSV();
	}
	
	
	/**
	 * 
	 * @param fileNameHhRelByAgeBySex
	 */
	private static void readInHhRelByAgeBySex(String fileNameHhRelByAgeBySex) {
		ArrayList<ArrayList<String>> rawData = TextFileHandler.readCSV(fileNameHhRelByAgeBySex);
		for (ArrayList<String> line : rawData) {
			Integer slaIDInt = null;
			Integer count = null;
			try{
				slaIDInt = Integer.parseInt(line.get(HhRelByAgeBySexCol.sla_code.getColIndex()));
				count = Integer.parseInt(line.get(HhRelByAgeBySexCol.person_count.getColIndex()));
			} catch (Exception e) {
				continue;
			}
			String slaID = Integer.toString(slaIDInt);
			String slaName = line.get(HhRelByAgeBySexCol.sla_name.getColIndex());
			String gender = line.get(HhRelByAgeBySexCol.gender.getColIndex());
			String ageBand = line.get(HhRelByAgeBySexCol.age_band.getColIndex());
			String hhRel = line.get(HhRelByAgeBySexCol.household_relationship.getColIndex());
			if (!ageGroupTxtIndexMap.containsKey(ageBand) || !hhRelTxtIndexMap.containsKey(hhRel)) {
				continue;
			}
			int ageBandIdx = ageGroupTxtIndexMap.get(ageBand).getIndex();
			int hhRelIdx = hhRelTxtIndexMap.get(hhRel).getIndex();
			
			int[][] hhRelGender = new int[AgeGroups.values().length][HholdRelSP.values().length];
			for (int i=0; i<=hhRelGender.length-1; i++) 
				for (int j=0; j<=hhRelGender[0].length-1; j++)
					hhRelGender[i][j] = -1;
			
			if (gender.equals(CHAR_female)) {
				if (slaIDhhRelFemaleMap.containsKey(slaID)) {
					hhRelGender = slaIDhhRelFemaleMap.get(slaID);
				}
				if (count>=0) {
					if (hhRelGender[ageBandIdx][hhRelIdx]>=0) 
						hhRelGender[ageBandIdx][hhRelIdx] = hhRelGender[ageBandIdx][hhRelIdx] + count;
					else 
						hhRelGender[ageBandIdx][hhRelIdx] = count;
				}
				slaIDhhRelFemaleMap.put(slaID, hhRelGender);
			} 
			else if (gender.equals(CHAR_male)) {
				if (slaIDhhRelMaleMap.containsKey(slaID)) {
					hhRelGender = slaIDhhRelMaleMap.get(slaID);
				}
				if (count>=0) {
					if (hhRelGender[ageBandIdx][hhRelIdx]>=0) 
						hhRelGender[ageBandIdx][hhRelIdx] = hhRelGender[ageBandIdx][hhRelIdx] + count;
					else 
						hhRelGender[ageBandIdx][hhRelIdx] = count;
				}
				slaIDhhRelMaleMap.put(slaID, hhRelGender);
			}
			
			if (!slaIDNameMapHhRel.containsKey(slaID)) {
				slaIDNameMapHhRel.put(slaID, slaName);
			}
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	private static String[] makeHeader() {
		String[] header = new String[HholdRelSP.values().length+1];
		header[0] = "ageBandIndex";
		for (int i=1; i<=header.length-1; i++) {
			header[i] = HholdRelSP.getHholdRelSPByIndex(i-1).toString();
		}
		
		return header;
	}
	
	
	/**
	 * 
	 */
	private static void writeHhRelByAgeBySexToCSV() {
		try {
			FileUtils.forceMkdir(new File(HardcodedData.slaInputPathHhRelMale));
			FileUtils.forceMkdir(new File(HardcodedData.slaInputPathHhRelFemale));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String slaID : slaIDhhRelMaleMap.keySet()) {
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			//strOut.add(new String[] {"ageBandIndex","Married","LoneParent","U15Child","Student","O15Child","Relative","GroupHholdMember","LonePerson","Unknown"});
			strOut.add(makeHeader());
			
			int[][] hhRelMale = slaIDhhRelMaleMap.get(slaID);
			for (int i=0; i<=hhRelMale.length-1; i++) {
				String[] crnRow = new String[hhRelMale[0].length+1];
				//crnRow[0] = Integer.toString(i);
				crnRow[0] = AgeGroups.getAgeGroupByIndex(i).toString();
				for (int j=0; j<=hhRelMale[0].length-1; j++) {
					crnRow[j+1] = Integer.toString(hhRelMale[i][j]);
				}
				strOut.add(crnRow);
			}
			TextFileHandler.writeToCSV(HardcodedData.slaInputPathHhRelMale + slaID + "_" + HardcodedData.charHhRelMale + ".csv", null, strOut);
		}
		
		// repeats the above for loop for slaIDhhRelFemaleMap
		for (String slaID : slaIDhhRelFemaleMap.keySet()) {
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			//strOut.add(new String[] {"ageBandIndex","Married","LoneParent","U15Child","Student","O15Child","Relative","GroupHholdMember","LonePerson","Unknown"});
			strOut.add(makeHeader());
			
			int[][] hhRelFemale = slaIDhhRelFemaleMap.get(slaID);
			for (int i=0; i<=hhRelFemale.length-1; i++) {
				String[] crnRow = new String[hhRelFemale[0].length+1];
				//crnRow[0] = Integer.toString(i);
				crnRow[0] = AgeGroups.getAgeGroupByIndex(i).toString();
				for (int j=0; j<=hhRelFemale[0].length-1; j++) {
					crnRow[j+1] = Integer.toString(hhRelFemale[i][j]);
				}
				strOut.add(crnRow);
			}
			TextFileHandler.writeToCSV(HardcodedData.slaInputPathHhRelFemale + slaID + "_" + HardcodedData.charHhRelFemale + ".csv", null, strOut);
		}
	}
}
