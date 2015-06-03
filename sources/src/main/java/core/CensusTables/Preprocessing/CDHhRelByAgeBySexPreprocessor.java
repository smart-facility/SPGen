package core.CensusTables.Preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import core.HardcodedData;
import core.TextFileHandler;
import core.HardcodedData.AgeGroups;
import core.HardcodedData.HholdRelSP;

public class CDHhRelByAgeBySexPreprocessor {
	private static final String CHAR_female1 = "female";
	private static final String CHAR_female2 = ".female";
	private static final String CHAR_male1 = "male";
	private static final String CHAR_male2 = ".male";
	
	private static HashMap<String,int[][]> ccdIDhhRelMaleMap;
	private static HashMap<String,int[][]> ccdIDhhRelFemaleMap;
	
	
	public static HashMap<String, int[][]> getCcdIDhhRelMaleMap() {
		return ccdIDhhRelMaleMap;
	}


	public static HashMap<String, int[][]> getCcdIDhhRelFemaleMap() {
		return ccdIDhhRelFemaleMap;
	}
	
	
	private enum HhRelByAgeBySexCol {
		census_year(0),	
		ccd_code(1),
		gender(2),
		age_band(3),
		household_relationship(4),
		person_count(5);
		
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
		ageGroupTxtIndexMap.put("0-14 years",AgeGroups._0_14);
		ageGroupTxtIndexMap.put("15-24 years",AgeGroups._15_24);
		ageGroupTxtIndexMap.put("25-34 years",AgeGroups._25_34);
		ageGroupTxtIndexMap.put("35-44 years",AgeGroups._35_44);
		ageGroupTxtIndexMap.put("45-54 years",AgeGroups._45_54);
		ageGroupTxtIndexMap.put("55-64 years",AgeGroups._55_64);
		ageGroupTxtIndexMap.put("65-74 years",AgeGroups._65_74);
		ageGroupTxtIndexMap.put("75-84 years",AgeGroups._75_84);
		ageGroupTxtIndexMap.put("85 years old or over",AgeGroups._85_99);
	}

	
	private static HashMap<String,HholdRelSP> hhRelTxtIndexMap;
	private static void setHhRelTxtIndexMap() {
		hhRelTxtIndexMap = new HashMap<String, HholdRelSP>();
		hhRelTxtIndexMap.put("Husband.in.a.registered.marriage", HholdRelSP.Married);
		hhRelTxtIndexMap.put("Wife.in.a.registered.marriage", HholdRelSP.Married);
		hhRelTxtIndexMap.put("Partner.in.de.facto.marriage.b", HholdRelSP.Married);
		hhRelTxtIndexMap.put("Lone.parent", HholdRelSP.LoneParent);
		hhRelTxtIndexMap.put("Child.under.15", HholdRelSP.U15Child);
		hhRelTxtIndexMap.put("Dependent.student aged.15.24.years", HholdRelSP.Student);
		hhRelTxtIndexMap.put("Non.dependent.child", HholdRelSP.O15Child);
		hhRelTxtIndexMap.put("Other.related.individual", HholdRelSP.Relative);
		hhRelTxtIndexMap.put("Unrelated.individual.living.in.family.household", HholdRelSP.Relative);
		hhRelTxtIndexMap.put("Group.household.member", HholdRelSP.GroupHhold);
		hhRelTxtIndexMap.put("Lone.person", HholdRelSP.LonePerson);
	}
	
	
	public static void readInCensusTables(String fileNameHhRelByAgeBySex) {
		
		/*
		 * reads in household relationship by age by sex
		 */
		setAgeGroupTxtIndexMap();
		setHhRelTxtIndexMap();
		ccdIDhhRelMaleMap = new HashMap<String, int[][]>();
		ccdIDhhRelFemaleMap = new HashMap<String, int[][]>();
		
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
			
			Integer ccdIDInt = null;
			Integer count = null;
			try{
				ccdIDInt = Integer.parseInt(line.get(HhRelByAgeBySexCol.ccd_code.getColIndex()));
				// if the last column of this line is empty, this line has 1 less element compared to other lines
				if (line.size()==HhRelByAgeBySexCol.values().length-1) 
					count = -1;
				else {
					count = Integer.parseInt(line.get(HhRelByAgeBySexCol.person_count.getColIndex()));
				}
			} catch (Exception e) {
				continue;
			}
			
			String slaID = Integer.toString(ccdIDInt);
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
			
			if (gender.equals(CHAR_female1) || gender.equals(CHAR_female2)) {
				if (ccdIDhhRelFemaleMap.containsKey(slaID)) {
					hhRelGender = ccdIDhhRelFemaleMap.get(slaID);
				}
				if (count>=0) {
					if (hhRelGender[ageBandIdx][hhRelIdx]>=0) 
						hhRelGender[ageBandIdx][hhRelIdx] = hhRelGender[ageBandIdx][hhRelIdx] + count;
					else 
						hhRelGender[ageBandIdx][hhRelIdx] = count;
				}
				ccdIDhhRelFemaleMap.put(slaID, hhRelGender);
			} 
			else if (gender.equals(CHAR_male1) || gender.equals(CHAR_male2)) {
				if (ccdIDhhRelMaleMap.containsKey(slaID)) {
					hhRelGender = ccdIDhhRelMaleMap.get(slaID);
				}
				if (count>=0) {
					if (hhRelGender[ageBandIdx][hhRelIdx]>=0) 
						hhRelGender[ageBandIdx][hhRelIdx] = hhRelGender[ageBandIdx][hhRelIdx] + count;
					else 
						hhRelGender[ageBandIdx][hhRelIdx] = count;
				}
				ccdIDhhRelMaleMap.put(slaID, hhRelGender);
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
			FileUtils.forceMkdir(new File(HardcodedData.ccdInputPathHhRelMale));
			FileUtils.forceMkdir(new File(HardcodedData.ccdInputPathHhRelFemale));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String ccdID : ccdIDhhRelMaleMap.keySet()) {
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			//strOut.add(new String[] {"ageBandIndex","Married","LoneParent","U15Child","Student","O15Child","Relative","GroupHholdMember","LonePerson","Unknown"});
			strOut.add(makeHeader());
			
			int[][] hhRelMale = ccdIDhhRelMaleMap.get(ccdID);
			for (int i=0; i<=hhRelMale.length-1; i++) {
				String[] crnRow = new String[hhRelMale[0].length+1];
				//crnRow[0] = Integer.toString(i);
				crnRow[0] = AgeGroups.getAgeGroupByIndex(i).toString();
				for (int j=0; j<=hhRelMale[0].length-1; j++) {
					crnRow[j+1] = Integer.toString(hhRelMale[i][j]);
				}
				strOut.add(crnRow);
			}
			TextFileHandler.writeToCSV(HardcodedData.ccdInputPathHhRelMale + ccdID + "_" + HardcodedData.charHhRelMale + ".csv", null, strOut);
		}
		
		// repeats the above for loop for slaIDhhRelFemaleMap
		for (String ccdID : ccdIDhhRelFemaleMap.keySet()) {
			ArrayList<String[]> strOut = new ArrayList<String[]>();
			//strOut.add(new String[] {"ageBandIndex","Married","LoneParent","U15Child","Student","O15Child","Relative","GroupHholdMember","LonePerson","Unknown"});
			strOut.add(makeHeader());
			
			int[][] hhRelFemale = ccdIDhhRelFemaleMap.get(ccdID);
			for (int i=0; i<=hhRelFemale.length-1; i++) {
				String[] crnRow = new String[hhRelFemale[0].length+1];
				//crnRow[0] = Integer.toString(i);
				crnRow[0] = AgeGroups.getAgeGroupByIndex(i).toString();
				for (int j=0; j<=hhRelFemale[0].length-1; j++) {
					crnRow[j+1] = Integer.toString(hhRelFemale[i][j]);
				}
				strOut.add(crnRow);
			}
			TextFileHandler.writeToCSV(HardcodedData.ccdInputPathHhRelFemale + ccdID + "_" + HardcodedData.charHhRelFemale + ".csv", null, strOut);
		}
	}
}
