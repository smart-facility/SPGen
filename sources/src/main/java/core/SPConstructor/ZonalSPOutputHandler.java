package core.SPConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.jmx.Agent;

import core.ArrayHandler;
import core.HardcodedData;
import core.TextFileHandler;
import core.CensusTables.CensusTablesAnalyser;
import core.HardcodedData.*;

public class ZonalSPOutputHandler {
	private static final String[] headerRelForZones = new String[] {"zoneName", "MarriedABS", "MarriedSP", "LoneParentABS", "LoneParentSP", 
			"U15ChildABS", "U15ChildSP", "StudentABS", "StudentSP", "O15ChildABS", "O15ChildSP", "RelativeABS", "RelativeSP", 
			"GroupHholdMembersABS", "GroupHholdMembersSP", "LonePersonABS", "LonePersonSP", "UnknownABS", "UnknownSP"};
	
	private static final String[] headerGenderByHhTypeForZones = new String[] { "zoneName", "NF_ABS", "NF_SP", "HF1_ABS", "HF1_SP", 
			"HF2_ABS", "HF2_SP", "HF3_ABS", "HF3_SP", "HF4_ABS", "HF4_SP", "HF5_ABS", "HF5_SP", "HF6_ABS", "HF6_SP", "HF7_ABS", "HF7_SP", "HF8_ABS", "HF8_SP", 
			"HF9_ABS", "HF9_SP", "HF10_ABS", "HF10_SP", "HF11_ABS", "HF11_SP", "HF12_ABS", "HF12_SP", "HF13_ABS", "HF13_SP", "HF14_ABS", "HF14_SP", "HF15_ABS", "HF15_SP", 
			"HF16_ABS", "HF16_SP", "Unknown_ABS", "Unknown_SP"};
	
	private static final String[] headerCountHholdByTypeForZones = new String[] {"zoneName", "NF_ABS", "NF_SP", "HF1_ABS", "HF1_SP", 
			"HF2_ABS", "HF2_SP", "HF3_ABS", "HF3_SP", "HF4_ABS", "HF4_SP", "HF5_ABS", "HF5_SP", "HF6_ABS", "HF6_SP", "HF7_ABS", "HF7_SP", "HF8_ABS", "HF8_SP", 
			"HF9_ABS", "HF9_SP", "HF10_ABS", "HF10_SP", "HF11_ABS", "HF11_SP", "HF12_ABS", "HF12_SP", "HF13_ABS", "HF13_SP", "HF14_ABS", "HF14_SP", "HF15_ABS", "HF15_SP", 
			"HF16_ABS", "HF16_SP", "Unknown_ABS", "Unknown_SP"};
	
	private static final String[] headerCountHholdBySizeForZones = new String[] {
			"zoneName", "1_ABS", "1_SP", "2_ABS", "2_SP", "3_ABS", "3_SP", "4_ABS", "4_SP", "5_ABS", "5_SP", ">=6_ABS", ">=6_SP"};
	
	private static ArrayList<String[]> countMaleByRelForZones;
	private static ArrayList<String[]> percentMaleByRelForZones;
	private static ArrayList<String[]> countFemaleByRelForZones;
	private static ArrayList<String[]> percentFemaleByRelForZones;
	
	private static ArrayList<String[]> countMaleByHhTypeForZones;
	private static ArrayList<String[]> percentMaleByHhTypeForZones;
	private static ArrayList<String[]> countFemaleByHhTypeForZones;
	private static ArrayList<String[]> percentFemaleByHhTypeForZones;
	
	private static ArrayList<String[]> countHholdByTypeForZones; 
	private static ArrayList<String[]> percentHholdByTypeForZones;
	
	private static ArrayList<String[]> countHFBySizeForZones;
	private static ArrayList<String[]> percentHFBySizeForZones;
	private static ArrayList<String[]> countNFBySizeForZones;
	private static ArrayList<String[]> percentNFBySizeForZones;
	

	public static void initialiseOutputArraysForZones() {
		countMaleByRelForZones = new ArrayList<String[]>();
		percentMaleByRelForZones = new ArrayList<String[]>();
		countFemaleByRelForZones = new ArrayList<String[]>();
		percentFemaleByRelForZones = new ArrayList<String[]>();
		
		countMaleByHhTypeForZones = new ArrayList<String[]>();
		percentMaleByHhTypeForZones = new ArrayList<String[]>();
		countFemaleByHhTypeForZones = new ArrayList<String[]>();
		percentFemaleByHhTypeForZones = new ArrayList<String[]>();
		
		countHholdByTypeForZones = new ArrayList<String[]>();
		percentHholdByTypeForZones = new ArrayList<String[]>();
		
		countHFBySizeForZones = new ArrayList<String[]>();
		percentHFBySizeForZones = new ArrayList<String[]>();
		countNFBySizeForZones = new ArrayList<String[]>();
		percentNFBySizeForZones = new ArrayList<String[]>();
	}
	
	
	/**
	 * 
	 * @param zoneName
	 */
	public static void recordStatForZones(String zoneName) {
		
		SPAnalyser.recordHhRelGenderForZones(zoneName, countMaleByRelForZones, percentMaleByRelForZones, Genders._male);
		SPAnalyser.recordHhRelGenderForZones(zoneName, countFemaleByRelForZones, percentFemaleByRelForZones, Genders._female);
		
		SPAnalyser.recordGenderByHhTypeForZones(zoneName, countMaleByHhTypeForZones, percentMaleByHhTypeForZones, Genders._male);
		SPAnalyser.recordGenderByHhTypeForZones(zoneName, countFemaleByHhTypeForZones, percentFemaleByHhTypeForZones, Genders._female);
		
		SPAnalyser.recordHhCountByTypeForZones(zoneName, countHholdByTypeForZones, percentHholdByTypeForZones);
		
		SPAnalyser.recordHFCountBySizeForZones(zoneName, countHFBySizeForZones, percentHFBySizeForZones);
		SPAnalyser.recordNFCountBySizeForZones(zoneName, countNFBySizeForZones, percentNFBySizeForZones);

	}

	
	/**
	 * 
	 * @param indivPool
	 * @param filename
	 */
	public static void writeIndivPool2CSV(String filename) {
		String[] header = new String[] {"indivID","age","gender","hhRel","hhType"};
		List<String[]> dataout = new ArrayList<String[]>();
		
		for (Integer indivID : IndividualPool.getPool().keySet()) {
			dataout.add(new String[] {String.valueOf(indivID), 
									String.valueOf(IndividualPool.getPool().get(indivID)[IndivAttribs.age.getIndex()]),
									String.valueOf(IndividualPool.getPool().get(indivID)[IndivAttribs.gender.getIndex()]),
									String.valueOf(IndividualPool.getPool().get(indivID)[IndivAttribs.hhRel.getIndex()])});
		}
		
		TextFileHandler.writeToCSV(filename, header, dataout);
	}
	
	
	/**
	 * 
	 * @param hholdPool
	 */
	public static void writeHholdPool2CSV(String filename) {
		String[] header = new String[] {"hhID","hhType"};
		List<String[]> dataout = new ArrayList<String[]>();
		
		for (Integer hhID : HouseholdPool.getPool().keySet()) {
			dataout.add(new String[] {String.valueOf(hhID),
									String.valueOf(HouseholdPool.getPool().get(hhID)[HholdAttribs.hhType.getIndex()])});
		}
		
		TextFileHandler.writeToCSV(filename, header, dataout);
		
	}

	
	
	/**
	 * 
	 * @param hhRel
	 * @param filename
	 */
	public static void writeHhRelByAge2CSV(int[][]hhRel, String filename) {
		String[] header = new String[hhRel[0].length+2];
		header[0] = "year1";
		header[1] = "year2";
		for (int j=0; j<=hhRel[0].length-1; j++) {
			header[j+2] = HholdRelSP.getHholdRelSPByIndex(j).toString();
		}
		List<String[]> dataout = new ArrayList<String[]>();
		
		for (int i=0; i<=hhRel.length-1; i++) {
			String[] crnLine = new String[hhRel[i].length+2];
			crnLine[0] = String.valueOf(AgeGroups.getAgeGroupByIndex(i).getMinAge());
			crnLine[1] = String.valueOf(AgeGroups.getAgeGroupByIndex(i).getMaxAge());
			for (int j=0; j<=hhRel[i].length-1; j++) {
				crnLine[j+2] = String.valueOf(hhRel[i][j]);
			}
			dataout.add(crnLine);
		}
		
		TextFileHandler.writeToCSV(filename, header, dataout);
	}

	
	
	/**
	 * 
	 * @param b24
	 * @param filename
	 */
	public static void writeHholdCountsByTypes2CSV(int[] b24, String filename) {
		String[] header = new String[] {"hhType","counts"};
		List<String[]> dataout = new ArrayList<String[]>();
		for (int i=0; i<=b24.length-1; i++) {
			String[] crnLine = new String[] {HholdTypes.getHholdTypeFromIndex(i).toString(), String.valueOf(b24[i])};
			dataout.add(crnLine);
		}
		TextFileHandler.writeToCSV(filename, header, dataout);
	}

	
	
	/**
	 * 
	 * @param indivsByHhTypes
	 * @param filename
	 */
	public static void writeIndivCountsByHhTypes2CSV(int[] indivsByHhTypes, String filename) {
		String[] header = new String[] {"hhType","indivCounts"};
		List<String[]> dataout = new ArrayList<String[]>();
		for (int i=0; i<=indivsByHhTypes.length-1; i++) {
			String[] crnLine = new String[] {HholdTypes.getHholdTypeFromIndex(i).toString(), String.valueOf(indivsByHhTypes[i])};
			dataout.add(crnLine);
		}
		TextFileHandler.writeToCSV(filename, header, dataout);
	}

	
	/**
	 * 
	 * @param hfCounts
	 * @param filename
	 */
	public static void writeHFCountsByUsualResidents2CSV(int[] hfCounts, String filename) {
		String[] header = new String[] {"nResidents","hhCounts"};
		List<String[]> dataout = new ArrayList<String[]>();
		for (int i=0; i<=hfCounts.length-1; i++) {
			String[] crnLine = new String[] {B30Rows.getRowFromIndex(i).toString(), String.valueOf(hfCounts[i])};
			dataout.add(crnLine);
		}
		TextFileHandler.writeToCSV(filename, header, dataout);
	}
	
	
	/**
	 * 
	 * @param nfCounts
	 * @param filename
	 */
	public static void writeNFCountsByUsualResidents2CSV(int[] nfCounts, String filename) {
		String[] header = new String[] {"nResidents","hhCounts"};
		List<String[]> dataout = new ArrayList<String[]>();
		for (int i=0; i<=nfCounts.length-1; i++) {
			String[] crnLine = new String[] {B30Rows.getRowFromIndex(i).toString(), String.valueOf(nfCounts[i])};
			dataout.add(crnLine);
		}
		TextFileHandler.writeToCSV(filename, header, dataout);
	}
	
	
	/**
	 * 
	 * @param filename
	 */
	public static void writeSPRecords(String filename) {
		String[] header = new String[] {"hhID","indivID","age","gender","hhRel","hhType"};
		List<String[]> dataout = new ArrayList<String[]>();
		for (Integer hhID : HouseholdPool.getHholdsAllocated().keySet()) {
			ArrayList<Integer> indivIDs = HouseholdPool.getHholdsAllocated().get(hhID);
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(HouseholdPool.getPool().get(hhID)[HholdAttribs.hhType.getIndex()]);
			
			for (Integer indivID : indivIDs) {
				int[] indivAttribs = IndividualPool.getPool().get(indivID);
				int age = indivAttribs[IndivAttribs.age.getIndex()];
				int genderIdx = indivAttribs[IndivAttribs.gender.getIndex()];
				HholdRelSP hhRel = HholdRelSP.getHholdRelSPByIndex(indivAttribs[IndivAttribs.hhRel.getIndex()]);
				
				String[] crnLine = new String[] {String.valueOf(hhID), String.valueOf(indivID), 
						String.valueOf(age), String.valueOf(genderIdx), hhRel.toString(), hhType.toString()};
				
				dataout.add(crnLine);
			}
		}
		TextFileHandler.writeToCSV(filename, header, dataout);
	}
	
	
	/**
	 * 
	 */
	public static void outputInitSPAnalyserTables() {
		ZonalSPOutputHandler.writeHhRelByAge2CSV(SPAnalyser.countIndivInPoolByHhRelByAge(Genders._male), HardcodedData.zonalOutputPath + "init_hhRelMale.csv");
		ZonalSPOutputHandler.writeHhRelByAge2CSV(SPAnalyser.countIndivInPoolByHhRelByAge(Genders._female), HardcodedData.zonalOutputPath + "init_hhRelFemale.csv");
		ZonalSPOutputHandler.writeHholdCountsByTypes2CSV(SPAnalyser.countHholdInPoolByType(), HardcodedData.zonalOutputPath + "init_hhCountsByHhTypes.csv");
	}
	
	
	/**
	 * 
	 */
	public static void outputZonalSPAnalyserTables() {
		// output values from individual pool and household pool
		// these outputs are mostly useful for debugging purposes only, so can be commented out 
//		int[][] hhRelMalePool = SPAnalyser.countIndivInPoolByHhRelByAge(Genders._male);
//		int[][] hhRelFemalePool = SPAnalyser.countIndivInPoolByHhRelByAge(Genders._female);
//		int[] hhCountByTypesPool = SPAnalyser.countHholdInPoolByType();
//		SPOutputHandler.writeHhRelByAge2CSV(hhRelMalePool, HardcodedData.outputTablesPath + "chkPool_hhRelMale.csv");
//		SPOutputHandler.writeHhRelByAge2CSV(hhRelFemalePool, HardcodedData.outputTablesPath + "chkPool_hhRelFemale.csv");
//		SPOutputHandler.writeHholdCountsByTypes2CSV(hhCountByTypesPool, HardcodedData.outputTablesPath + "chkPool_hhCountsByHhTypes.csv");
		
		// output values from allocated individuals and households, ie. the final SP.
		// these values can and should be compared against census tables.
		int[][] hhRelMaleAlloc = SPAnalyser.countIndivAllocByHhRelByAge(Genders._male);
		int[][] hhRelFemaleAlloc = SPAnalyser.countIndivAllocByHhRelByAge(Genders._female);
		int[] hhCountByTypesAlloc = SPAnalyser.countHholdAllocByType();
		int[] maleCountByHhTypesAlloc = SPAnalyser.countIndivAllocByHhType(Genders._male);
		int[] femaleCountByHhTypesAlloc = SPAnalyser.countIndivAllocByHhType(Genders._female);
		int[] hfCountsByResidents = SPAnalyser.countHFAllocByHholdSize();
		int[] nfCountsByResidents = SPAnalyser.countNFAllocByHholdSize();
		
		ZonalSPOutputHandler.writeHhRelByAge2CSV(hhRelMaleAlloc, HardcodedData.zonalOutputPath + "alloc_hhRelMale.csv");
		ZonalSPOutputHandler.writeHhRelByAge2CSV(hhRelFemaleAlloc, HardcodedData.zonalOutputPath + "alloc_hhRelFemale.csv");
		ZonalSPOutputHandler.writeHholdCountsByTypes2CSV(hhCountByTypesAlloc, HardcodedData.zonalOutputPath + "alloc_hhCountsByHhTypes.csv");
		ZonalSPOutputHandler.writeIndivCountsByHhTypes2CSV(maleCountByHhTypesAlloc, HardcodedData.zonalOutputPath + "alloc_maleCountsByHhTypes.csv");
		ZonalSPOutputHandler.writeIndivCountsByHhTypes2CSV(femaleCountByHhTypesAlloc, HardcodedData.zonalOutputPath + "alloc_femaleCountsByHhTypes.csv");
		ZonalSPOutputHandler.writeHFCountsByUsualResidents2CSV(hfCountsByResidents, HardcodedData.zonalOutputPath + "alloc_hfComp.csv");
		ZonalSPOutputHandler.writeNFCountsByUsualResidents2CSV(nfCountsByResidents, HardcodedData.zonalOutputPath + "alloc_nfComp.csv");
		
		// output other stuff
		ZonalSPOutputHandler.writeSPRecords(HardcodedData.zonalOutputPath + "spRecords.csv");
		TextFileHandler.writeToCSV(HardcodedData.zonalOutputPath + "dAgeOppositeSex.csv", new String[] {"dAge","counts"}, SPAnalyser.calculatedAgeCouples(true));
		TextFileHandler.writeToCSV(HardcodedData.zonalOutputPath + "dAgeSameSex.csv", new String[] {"dAge","counts"}, SPAnalyser.calculatedAgeCouples(false));
		TextFileHandler.writeToCSV(HardcodedData.zonalOutputPath + "dAgeYoungerParentChild.csv", new String[] {"dAge","counts"}, SPAnalyser.calcDistribMotherChildAgeGap());
	}
	
	/**
	 * 
	 */
	public static void writeStatForZonesToCSV() {
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathCountForZones + "countMaleByRelForZones.csv", headerRelForZones, countMaleByRelForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathPercentForZones + "percentMaleByRelForZones.csv", headerRelForZones, percentMaleByRelForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathCountForZones + "countFemaleByRelForZones.csv", headerRelForZones, countFemaleByRelForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathPercentForZones + "percentFemaleByRelForZones.csv", headerRelForZones, percentFemaleByRelForZones);
		
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathCountForZones + "countMaleByHhType4Zones.csv", headerGenderByHhTypeForZones, countMaleByHhTypeForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathPercentForZones + "percentMaleByHhType4Zones.csv", headerGenderByHhTypeForZones, percentMaleByHhTypeForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathCountForZones + "countFemaleByHhType4Zones.csv", headerGenderByHhTypeForZones, countFemaleByHhTypeForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathPercentForZones + "percentFemaleByHhType4Zones.csv", headerGenderByHhTypeForZones, percentFemaleByHhTypeForZones);
		
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathCountForZones + "countHholdByTypeForZones.csv", headerCountHholdByTypeForZones, countHholdByTypeForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathPercentForZones + "percentCountHholdByTypeForZones.csv", headerCountHholdByTypeForZones, percentHholdByTypeForZones);
		
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathCountForZones + "countHFBySizeForZones.csv", headerCountHholdBySizeForZones, countHFBySizeForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathPercentForZones + "percentHFBySizeForZones.csv", headerCountHholdBySizeForZones, percentHFBySizeForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathCountForZones + "countNFBySizeForZones.csv", headerCountHholdBySizeForZones, countNFBySizeForZones);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathPercentForZones + "percentNFBySizeForZones.csv", headerCountHholdBySizeForZones, percentNFBySizeForZones);
	}
	
	
	
	
	/**
	 * 
	 * @param indivIDArray
	 * @param arrayName
	 */
	public static void dispRemainingIndivs(int[] indivIDArray, String arrayName) {
		if (indivIDArray==null || indivIDArray.length==0) {
			System.out.println(arrayName + ", 0");
		} else {
			System.out.println(arrayName + ", " + indivIDArray.length);
		}
	}
}
