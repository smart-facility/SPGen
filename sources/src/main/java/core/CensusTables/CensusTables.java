package core.CensusTables;

import core.ArrayHandler;
import core.HardcodedData;
import core.PostgresHandler;
import core.CensusTables.Preprocessing.ZonalSPLikeCensusTablesReader;
import core.HardcodedData.*;
import core.SPConstructor.ZonalSPOutputHandler;

public class CensusTables {
	
	private static int[][] hhRelMale;
	private static int[][] hhRelFemale;
	private static int[][] familyComp;
	private static int[][] familyCompBySex;
	private static int[][] hhComp;
//	private static int[][] minResidents; 

	private static int[][] spLikeB22Male;
	private static int[][] spLikeB22Female;
	private static int[] spLikeB24;
	private static int[] spLikeB25Male;
	private static int[] spLikeB25Female;
	private static int[] spLikeB30HF;
	private static int[] spLikeB30NF;
	
	private static int[][] spLikeB22MaleAccum;
	private static int[][] spLikeB22FemaleAccum;
	private static int[] spLikeB24Accum;
	private static int[] spLikeB25MaleAccum;
	private static int[] spLikeB25FemaleAccum;
	private static int[] spLikeB30HFAccum;
	private static int[] spLikeB30NFAccum;
	
	public static int[][] getSpLikeB22Male() {
		return spLikeB22Male;
	}

	public static int[][] getSpLikeB22Female() {
		return spLikeB22Female;
	}

	public static int[] getSpLikeB24() {
		return spLikeB24;
	}

	public static int[] getSpLikeB25Male() {
		return spLikeB25Male;
	}

	public static int[] getSpLikeB25Female() {
		return spLikeB25Female;
	}

	public static int[] getSpLikeB30HF() {
		return spLikeB30HF;
	}

	public static int[] getSpLikeB30NF() {
		return spLikeB30NF;
	}

	public static int[][] getSpLikeB22MaleAccum() {
		return spLikeB22MaleAccum;
	}

	public static int[][] getSpLikeB22FemaleAccum() {
		return spLikeB22FemaleAccum;
	}

	public static int[] getSpLikeB24Accum() {
		return spLikeB24Accum;
	}

	public static int[] getSpLikeB25MaleAccum() {
		return spLikeB25MaleAccum;
	}

	public static int[] getSpLikeB25FemaleAccum() {
		return spLikeB25FemaleAccum;
	}

	public static int[] getSpLikeB30HFAccum() {
		return spLikeB30HFAccum;
	}

	public static int[] getSpLikeB30NFAccum() {
		return spLikeB30NFAccum;
	}

	public static int[][] getHhRelMale() {
		return hhRelMale;
	}

	public static int[][] getHhRelFemale() {
		return hhRelFemale;
	}
	
	public static int[][] getFamilyComp() {
		return familyComp;
	}

	public static int[][] getFamilyCompBySex() {
		return familyCompBySex;
	}

	public static int[][] getHhComp() {
		return hhComp;
	}

	
	
	/**
	 * 
	 * @param dbIndiv
	 * @param dbHhold
	 * @param cdName
	 */
	public static void readTablesFromDBs(PostgresHandler dbIndiv, PostgresHandler dbHhold, String cdName) {
		// get table B22a for Male
		hhRelMale = null;
		hhRelFemale = null;
		familyComp = null;
		familyCompBySex = null;
		hhComp = null;
//		minResidents = null;
		
		hhRelMale = dbIndiv.getIntArrayFromRows(
				IndivSchema.household_relationship.toString(),
				cdName,
				Genders._male.toString(),
				B22Cols.note.toString(),
				ArrayHandler.makeIncrementalIntArray(1, B22Cols.values().length - 1, 1));

		// get table B22a for Female
		hhRelFemale = dbIndiv.getIntArrayFromRows(
				IndivSchema.household_relationship.toString(),
				cdName,
				Genders._female.toString(),
				B22Cols.note.toString(),
				ArrayHandler.makeIncrementalIntArray(1, B22Cols.values().length - 1, 1));

		// get table B24 and B25
		for (B24B25Rows fcn : B24B25Rows.values()) {
			familyComp = ArrayHandler.concateMatrices(familyComp, dbHhold.getIntArrayFromRows(
					HholdSchema.family_composition.toString(),
					cdName,
					fcn.toString(),
					B24Cols.note.toString(),
					ArrayHandler.makeIncrementalIntArray(1, B24Cols.values().length - 1, 1)));
			familyCompBySex = ArrayHandler.concateMatrices(familyCompBySex,	dbHhold.getIntArrayFromRows(
																			HholdSchema.family_composition_bysex.toString(), 
																			cdName, 
																			fcn.toString(),
																			B25Cols.note.toString(), 
																			ArrayHandler.makeIncrementalIntArray(1,B25Cols.values().length-1, 1)));
		}

		// get table B30
		hhComp = dbHhold.getIntArrayFromAllRows(
				HholdSchema.household_composition.toString(),
				cdName, 
				new int[] {B30Cols.id.getIndex() + 1, 
					B30Cols.familyHholds.getIndex() + 1, 
					B30Cols.nonFamilyHholds.getIndex() + 1 });
		
//		// get table S1
//		minResidents = dbHhold.getIntArrayFromAllRows(
//				HholdSchema.min_individuals.toString(),
//				HholdSchema.min_individuals.toString(), new int[] {
//					MinResidentCols.index.getIndex() + 1,
//					MinResidentCols.min_parents.getIndex() + 1,
//					MinResidentCols.min_u15.getIndex() + 1,
//					MinResidentCols.min_students.getIndex() + 1,
//					MinResidentCols.min_o15.getIndex() + 1,
//					MinResidentCols.min_relatives.getIndex() + 1,
//					MinResidentCols.min_non_family.getIndex() + 1,
//					MinResidentCols.min_residents.getIndex() + 1 });
		
		prepareSPLikeTables();
		
//		accummulateCensusTables();
	}
	
	public static boolean setCensusTablesFromReadingCSV(String zoneName) {
		spLikeB22Male = null;
		spLikeB22Female = null;
		spLikeB24 = null;
		spLikeB25Male = null;
		spLikeB25Female = null;
		spLikeB30HF = null;
		spLikeB30NF = null;
		
		spLikeB22Male = ZonalSPLikeCensusTablesReader.readHhRel(HardcodedData.commonInputPathHhRelMale + zoneName + "_" + HardcodedData.charHhRelMale + ".csv");
		System.out.println(HardcodedData.commonInputPathHhRelMale + zoneName + "_" + HardcodedData.charHhRelMale + ".csv");
		
		spLikeB22Female = ZonalSPLikeCensusTablesReader.readHhRel(HardcodedData.commonInputPathHhRelFemale + zoneName + "_" + HardcodedData.charHhRelFemale + ".csv");
		System.out.println(HardcodedData.commonInputPathHhRelFemale + zoneName + "_" + HardcodedData.charHhRelFemale + ".csv");
		
		spLikeB24 = ZonalSPLikeCensusTablesReader.readCountByHhType(HardcodedData.commonInputPathHholdByHhType + zoneName + "_" + HardcodedData.charHholdByHhType + ".csv");
		System.out.println(HardcodedData.commonInputPathHholdByHhType + zoneName + "_" + HardcodedData.charHholdByHhType + ".csv");
		
		spLikeB25Male = ZonalSPLikeCensusTablesReader.readCountByHhType(HardcodedData.commonInputPathMaleByHhType + zoneName + "_" + HardcodedData.charMaleByHhType + ".csv");
		System.out.println(HardcodedData.commonInputPathMaleByHhType + zoneName + "_" + HardcodedData.charMaleByHhType + ".csv");
		
		spLikeB25Female = ZonalSPLikeCensusTablesReader.readCountByHhType(HardcodedData.commonInputPathFemaleByHhType + zoneName + "_" + HardcodedData.charFemaleByHhType + ".csv");
		System.out.println(HardcodedData.commonInputPathFemaleByHhType + zoneName + "_" + HardcodedData.charFemaleByHhType + ".csv");
		
		spLikeB30HF = ZonalSPLikeCensusTablesReader.readHhCountBySize(HardcodedData.commonInputPathHFCompBySize + zoneName + "_" + HardcodedData.charHFCompBySize + ".csv");
		System.out.println(HardcodedData.commonInputPathHFCompBySize + zoneName + "_" + HardcodedData.charHFCompBySize + ".csv");
		
		spLikeB30NF = ZonalSPLikeCensusTablesReader.readHhCountBySize(HardcodedData.commonInputPathNFCompBySize + zoneName + "_" + HardcodedData.charNFCompBySize + ".csv");
		System.out.println(HardcodedData.commonInputPathNFCompBySize + zoneName + "_" + HardcodedData.charNFCompBySize + ".csv");
		
		if (spLikeB22Male==null || spLikeB22Female==null || spLikeB24==null || spLikeB25Male==null || spLikeB25Female==null || spLikeB30HF==null || spLikeB30NF==null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 
	 */
	private static void prepareSPLikeTables() {
		spLikeB22Male = CensusTablesAnalyser.getSPlikeTableFromCensusB22(Genders._male);
		spLikeB22Female = CensusTablesAnalyser.getSPlikeTableFromCensusB22(Genders._female);
		spLikeB30HF = CensusTablesAnalyser.getSPlikeHFCountsFromCensusB30();
		spLikeB30NF = CensusTablesAnalyser.getSPlikeNFCountsFromCensusB30();
		spLikeB24 = CensusTablesAnalyser.getSPlikeHhCountsTableFromCensusB24();
		spLikeB25Male = CensusTablesAnalyser.getSPlikeIndivCountsFromCensusB25(Genders._male);
		spLikeB25Female = CensusTablesAnalyser.getSPlikeIndivCountsFromCensusB25(Genders._female);
	}
	
	
	private static void accummulateCensusTables() {
		if (spLikeB22MaleAccum==null)
			spLikeB22MaleAccum = new int[spLikeB22Male.length][spLikeB22Male[0].length];
		else {
			spLikeB22MaleAccum = ArrayHandler.addPositiveIn2DArrays(spLikeB22MaleAccum, spLikeB22Male);
		}
		
		if (spLikeB22FemaleAccum==null)
			spLikeB22FemaleAccum = new int[spLikeB22Female.length][spLikeB22Female[0].length];
		else {
			spLikeB22FemaleAccum = ArrayHandler.addPositiveIn2DArrays(spLikeB22FemaleAccum,spLikeB22Female);
		}
		
		if (spLikeB24Accum==null)
			spLikeB24Accum = new int[spLikeB24.length];
		else {
			spLikeB24Accum = ArrayHandler.addPositiveValuesInArrays(spLikeB24Accum, spLikeB24);
		}
		
		if (spLikeB25MaleAccum==null)
			spLikeB25MaleAccum = new int[spLikeB25Male.length];
		else {
			spLikeB25MaleAccum = ArrayHandler.addPositiveValuesInArrays(spLikeB25MaleAccum, spLikeB25Male);
		}
		
		if (spLikeB25FemaleAccum==null)
			spLikeB25FemaleAccum = new int[spLikeB25Female.length];
		else {
			spLikeB25FemaleAccum = ArrayHandler.addPositiveValuesInArrays(spLikeB25FemaleAccum, spLikeB25Female);
		}
		
		if (spLikeB30HFAccum==null)
			spLikeB30HFAccum = new int[spLikeB30HF.length];
		else {
			spLikeB30HFAccum = ArrayHandler.addPositiveValuesInArrays(spLikeB30HFAccum, spLikeB30HF);
		}
		
		if (spLikeB30NFAccum==null)
			spLikeB30NFAccum = new int[spLikeB30NF.length];
		else {
			spLikeB30NFAccum = ArrayHandler.addPositiveValuesInArrays(spLikeB30NFAccum, spLikeB30NF);
		}
	}
	
	/**
	 * 
	 * @param hhRelMale
	 * @param hhRelFemale
	 */
	public static void displayCensusB22() {
		System.out.println("\nCensusTables.hhRelMale, " + hhRelMale.length + "x" + hhRelMale[0].length);
		System.out.println("id,year1,year2,married,defacto,loneparent,U15,student,O15,reltives,nonrelatives,groupsHousehold,lonePerson,visitor");
		for (AgeGroups ageGroup : AgeGroups.values()) {
			System.out.println(hhRelMale[ageGroup.getIndex()][B22Cols.id.getIndex()] + ", " + 
								hhRelMale[ageGroup.getIndex()][B22Cols.year_1.getIndex()] + "(" + ageGroup.getMinAge() + "), " +
								hhRelMale[ageGroup.getIndex()][B22Cols.year_2.getIndex()] + "(" + ageGroup.getMaxAge() + "), " +
								hhRelMale[ageGroup.getIndex()][B22Cols.Married.getIndex()] + ", " + 
								hhRelMale[ageGroup.getIndex()][B22Cols.DeFacto.getIndex()] + ", " +
								hhRelMale[ageGroup.getIndex()][B22Cols.LoneParent.getIndex()] + ", " +
								hhRelMale[ageGroup.getIndex()][B22Cols.U15Child.getIndex()] + ", " +
								hhRelMale[ageGroup.getIndex()][B22Cols.Student.getIndex()] + ", " +
								hhRelMale[ageGroup.getIndex()][B22Cols.O15Child.getIndex()] + ", " +
								hhRelMale[ageGroup.getIndex()][B22Cols.Relative.getIndex()] + ", " +
								hhRelMale[ageGroup.getIndex()][B22Cols.NonRelative.getIndex()] + ", " +
								hhRelMale[ageGroup.getIndex()][B22Cols.GroupHhold.getIndex()] + ", " +
								hhRelMale[ageGroup.getIndex()][B22Cols.LonePerson.getIndex()] + ", " +
								hhRelMale[ageGroup.getIndex()][B22Cols.Visitor.getIndex()]);
		}

		System.out.println("\nCensusTables.hhRelFemale, " + hhRelFemale.length + "x" + hhRelFemale[0].length);
		System.out.println("id,year1,year2,married,defacto,loneparent,U15,student,O15,reltives,nonrelatives,groupsHousehold,lonePerson,visitor");
		for (AgeGroups ageGroup : AgeGroups.values()) {
			System.out.println(hhRelFemale[ageGroup.getIndex()][B22Cols.id.getIndex()] + ", " + 
								hhRelFemale[ageGroup.getIndex()][B22Cols.year_1.getIndex()] + "(" + ageGroup.getMinAge() + "), " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.year_2.getIndex()] + "(" + ageGroup.getMaxAge() + "), " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.Married.getIndex()] + ", " + 
								hhRelFemale[ageGroup.getIndex()][B22Cols.DeFacto.getIndex()] + ", " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.LoneParent.getIndex()] + ", " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.U15Child.getIndex()] + ", " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.Student.getIndex()] + ", " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.O15Child.getIndex()] + ", " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.Relative.getIndex()] + ", " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.NonRelative.getIndex()] + ", " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.GroupHhold.getIndex()] + ", " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.LonePerson.getIndex()] + ", " +
								hhRelFemale[ageGroup.getIndex()][B22Cols.Visitor.getIndex()]);
		}
	}

	
	/**
	 * 
	 * @param nHF
	 */
	public static void displayCensusB24() {
		System.out.println("\nFamily Composition (CensusTables.familyComp, " + familyComp.length + "x" + familyComp[0].length + "):");
		System.out.println("HHType,HHCounts");
		for (B24B25Rows fcn : B24B25Rows.values()) {
			System.out.println(fcn.toString() + "," + familyComp[fcn.getIndex()][B24Cols.families.getIndex()]);
		}

	}
	
	
	/**
	 * 
	 * @param nppInHF
	 */
	public static void displayCensusB25() {
		System.out.println("\nFamily Composition by Sex (CensusTables.familyCompBySex, " + familyCompBySex.length + "x" + familyCompBySex[0].length + "):");
		System.out.println("HHType,Males,Females");
		for (B24B25Rows fcn : B24B25Rows.values()) {
			System.out.println(fcn.toString() + "," + familyCompBySex[fcn.getIndex()][B25Cols.males.getIndex()] + "," + 
					familyCompBySex[fcn.getIndex()][B25Cols.females.getIndex()]);
		}
	}
	
	
	/**
	 * 
	 * @param hhComp
	 */
	public static void displayCensusB30() {
		System.out.println("\nHousehold composition by usual residents (CensusTables.hhComp, " + hhComp.length + "x" + hhComp[0].length + "):");
		System.out.println("nPeople,familyHouseholds,nonFamilyHouseholds");
		for (B30Rows row : B30Rows.values()) {
			System.out.println(row.toString() + "," + hhComp[row.getIndex()][B30Cols.familyHholds.getIndex()] + "," + 
														hhComp[row.getIndex()][B30Cols.nonFamilyHholds.getIndex()]);
		}
	}
	
	
	/**
	 * 
	 * @param minResidents
	 */
	public static void displayMinResidents() {
		System.out.println("\nMinimum residents (minResidents, " + HardcodedData.minResidents.length + "x" + HardcodedData.minResidents[0].length + "):");
		System.out.println("hhType,index,minParents,minU15,minStudents,minO15,minRelatives,minNonFamily,minResidents");
		for (HholdTypes row : HholdTypes.values()) {
			if (row==HholdTypes.Unknown) continue;
			System.out.println(row.toString() + "," + 
					HardcodedData.minResidents[row.getIndex()][MinResidentCols.index.getIndex()] + "," + 
					HardcodedData.minResidents[row.getIndex()][MinResidentCols.min_parents.getIndex()] + "," +
					HardcodedData.minResidents[row.getIndex()][MinResidentCols.min_u15.getIndex()] + "," +
					HardcodedData.minResidents[row.getIndex()][MinResidentCols.min_students.getIndex()] + "," +
					HardcodedData.minResidents[row.getIndex()][MinResidentCols.min_o15.getIndex()] + "," +
					HardcodedData.minResidents[row.getIndex()][MinResidentCols.min_relatives.getIndex()] + "," +
					HardcodedData.minResidents[row.getIndex()][MinResidentCols.min_non_family.getIndex()] + "," +
					HardcodedData.minResidents[row.getIndex()][MinResidentCols.min_residents.getIndex()]);
		
		}
	}
	
	
	/**
	 * 
	 */
	public static void outputSPlikeCensusTables() {
		ZonalSPOutputHandler.writeHhRelByAge2CSV(spLikeB22Male, HardcodedData.zonalOutputPath + "census_hhRelMale.csv");
		ZonalSPOutputHandler.writeHhRelByAge2CSV(spLikeB22Female, HardcodedData.zonalOutputPath + "census_hhRelFemale.csv");
		ZonalSPOutputHandler.writeHholdCountsByTypes2CSV(spLikeB24, HardcodedData.zonalOutputPath + "census_hhCountsByHhTypes.csv");
		ZonalSPOutputHandler.writeIndivCountsByHhTypes2CSV(spLikeB25Male, HardcodedData.zonalOutputPath + "census_maleCountsByHhTypes.csv");
		ZonalSPOutputHandler.writeIndivCountsByHhTypes2CSV(spLikeB25Female, HardcodedData.zonalOutputPath + "census_femaleCountsByHhTypes.csv");
		ZonalSPOutputHandler.writeHFCountsByUsualResidents2CSV(spLikeB30HF, HardcodedData.zonalOutputPath + "census_hfComp.csv");
		ZonalSPOutputHandler.writeNFCountsByUsualResidents2CSV(spLikeB30NF, HardcodedData.zonalOutputPath + "census_nfComp.csv");
	}
	

}
