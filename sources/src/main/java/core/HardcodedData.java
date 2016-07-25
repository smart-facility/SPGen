package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.io.FileUtils;


public class HardcodedData {
	public static String zonalOutputPath;
	public static String globalOutputPath; // = "post processing/output tables/";
	public static String globalOutputPathCountForZones; // = "post processing/output tables/countForZones/";
	public static String globalOutputPathPercentForZones; // = "post processing/output tables/percentForZones/";
	public static String globalOutputPathAccumulative; // = "post processing/output tables/accumulative/";
	
	public static String inputTablesPathSPGen = "input tables/inputTables/";
	public static String inputTablesPathSPEvo = "input tables/inputTables/rates - SP evolution/";
	public static String outputTablesPath = "post processing/output SP evolution/";
	
	public static String charFemaleByHhType = "femaleByHhType"; 
	public static String charMaleByHhType = "maleByHhType";
	public static String charHholdByHhType = "hholdByHhType";
	public static String charHhRelFemale = "hhRelFemale";
	public static String charHhRelMale = "hhRelMale";
	public static String charNFCompBySize = "nfCompBySize";
	public static String charHFCompBySize = "hfCompBySize";
	public static String charIncByAgeBySex = "incomeByAgeBySex";
	
	public static String commonInputPathFemaleByHhType = inputTablesPathSPGen + charFemaleByHhType + "/"; 
	public static String commonInputPathMaleByHhType = inputTablesPathSPGen + charMaleByHhType + "/";
	public static String commonInputPathHholdByHhType = inputTablesPathSPGen + charHholdByHhType + "/";
	public static String commonInputPathHhRelFemale = inputTablesPathSPGen + charHhRelFemale + "/";
	public static String commonInputPathHhRelMale = inputTablesPathSPGen + charHhRelMale + "/";
	public static String commonInputPathNFCompBySize = inputTablesPathSPGen + charNFCompBySize + "/";
	public static String commonInputPathHFCompBySize = inputTablesPathSPGen + charHFCompBySize + "/";
	public static String commonInputPathIncByAgeBySex = inputTablesPathSPGen + charIncByAgeBySex + "/";
	
	public static String slaInputPathFemaleByHhType = "input tables/slaInputTables/femaleByHhType/"; 
	public static String slaInputPathMaleByHhType = "input tables/slaInputTables/maleByHhType/";
	public static String slaInputPathHholdByHhType = "input tables/slaInputTables/hholdByHhType/";
	public static String slaInputPathHhRelFemale = "input tables/slaInputTables/hhRelFemale/";
	public static String slaInputPathHhRelMale = "input tables/slaInputTables/hhRelMale/";
	public static String slaInputPathNFCompBySize = "input tables/slaInputTables/nfCompBySize/";
	public static String slaInputPathHFCompBySize = "input tables/slaInputTables/hfCompBySize/";
	
	public static String ccdInputPathFemaleByHhType = "input tables/ccdInputTables/femaleByHhType/"; 
	public static String ccdInputPathMaleByHhType = "input tables/ccdInputTables/maleByHhType/";
	public static String ccdInputPathHholdByHhType = "input tables/ccdInputTables/hholdByHhType/";
	public static String ccdInputPathHhRelFemale = "input tables/ccdInputTables/hhRelFemale/";
	public static String ccdInputPathHhRelMale = "input tables/ccdInputTables/hhRelMale/";
	public static String ccdInputPathNFCompBySize = "input tables/ccdInputTables/nfCompBySize/";
	public static String ccdInputPathHFCompBySize = "input tables/ccdInputTables/hfCompBySize/";
	
	
	public static final int minusOne = -1;
	public static final String unknown = "Unknown";
	
	public static void initOutputPaths() {
		globalOutputPath = "post processing/output tables" + "_" + Long.toString(randomSeed) + "/";
		globalOutputPathCountForZones = globalOutputPath + "countForZones/";
		globalOutputPathPercentForZones = globalOutputPath + "percentForZones/";
		globalOutputPathAccumulative = globalOutputPath + "accumulative/";
		
		try {
			 
			FileUtils.forceMkdir(new File(globalOutputPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			FileUtils.forceMkdir(new File(globalOutputPathCountForZones));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			FileUtils.forceMkdir(new File(globalOutputPathPercentForZones));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			FileUtils.forceMkdir(new File(globalOutputPathAccumulative));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setOutputTablesPath(String zoneName) {
		zonalOutputPath = globalOutputPath + zoneName + "/";
		
		try {
			FileUtils.forceMkdir(new File(zonalOutputPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Random random = new Random();
	public static long randomSeed;
	
	public static final int largeInt = 1000000000;
	public static final double largeDouble = 1000000000.0;
		
	public static void setSeed(long seed) {
		randomSeed = seed;
		random.setSeed(seed);
	}
	
	public static final int minAgeMarried = 18;
	public static final int ageOfConsent = 16; // mindAgeParentChild
	public static final int maxdAgeParentChild = 50;
	public static final int meandAgeMarriedCouple = 2;
	public static final int stddAgeMarriedCouple = 2;
	public static final int minAgeU15 = 0;
	public static final int maxAgeU15 = 14;
	public static final int minAgeStu = 15;
	public static final int maxAgeStu = 24;
	public static final int minAgeO15 = 15;
	
	public static final String My_POSTGRESURL = "jdbc:postgresql://localhost:5432/";
	public static final String My_USERNAME = "postgres";
	public static final String My_PASSWORD = "password";
	
	
	
	public static enum IndivAttribs {
		age(0), gender(1), hhRel(2);
		
		private int index;
		
		private IndivAttribs(int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		} 
	}
	
	
	public static enum HholdAttribs {
		hhType(0);
		
		private int index;
		
		private HholdAttribs(int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	
	public enum HholdRelSP {
		Married(0),LoneParent(1),U15Child(2),Student(3),O15Child(4),Relative(5),GroupHhold(6),LonePerson(7),Unknown(8);
		
		private int index;
		
		private HholdRelSP (int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static int getNumberOfValidElements() {
			return (HholdRelSP.values().length - 1); // ignoring type 'Unknown';
		}
		
		public static HholdRelSP getHholdRelSPByIndex(int queryIndex) {
			for (HholdRelSP hhRelSP : HholdRelSP.values()) {
				if (hhRelSP.index==queryIndex) {
					return hhRelSP;
				}
			}
			return null;
		}
		
		public ArrayList<Integer> getB22ColIndices() {
			ArrayList<Integer> cols = new ArrayList<Integer>();
			for (B22Cols col : B22Cols.values()) {
				if (col.getHhRelSP()==this) {
					if (!cols.contains(col.getIndex())) {
						cols.add(col.getIndex());
					}
				}
			}
			return cols;
		}
		
		public static HholdRelSP[] getValidHholdRelSP() {
			HholdRelSP[] validHhRels = {HholdRelSP.Married, HholdRelSP.LoneParent, HholdRelSP.U15Child, HholdRelSP.Student, HholdRelSP.O15Child, HholdRelSP.Relative,
					HholdRelSP.GroupHhold, HholdRelSP.LonePerson};
			return validHhRels;
		}
	}
	
	
	public enum HholdTypes {
		NF(0), HF1(1), HF2(2), HF3(3), HF4(4), HF5(5), HF6(6), HF7(7), HF8(8), HF9(9), HF10(10), HF11(11), HF12(12), HF13(13), HF14(14), HF15(15), HF16(16), 
		Unknown(17);
		
		private int index;
		
		private HholdTypes (int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		} 
		
		public static int getIndexFromString(String hhType) {
			int hhIndex = -1;
			
			for (HholdTypes hh : HholdTypes.values()) {
				if (hh.toString()==hhType) {
					return hh.getIndex();
				}
			}
			
			return hhIndex;
		}
		
		public static HholdTypes getHholdTypeFromIndex(int newIndex) {
			for (HholdTypes hh : HholdTypes.values()) {
				if (hh.getIndex()==newIndex) {
					return hh;
				}
			}
			return null;
		}
		
		public static HholdTypes getHholdTypeFromString(String hhType) {
			for (HholdTypes hh : HholdTypes.values()) {
				if (hh.toString()==hhType) {
					return hh;
				}
			}
			return null;
		}
		
		public B24B25Rows getb24b25Row() {
			for (B24B25Rows b2425Row : B24B25Rows.values()) {
				if (this.toString().equals(b2425Row.toString())) {
					return b2425Row;
				}
			}
			return null;
		}
		
		public static HholdTypes[] getValidHFTypes() {
			return new HholdTypes[] {HF1, HF2, HF3, HF4, HF5, HF6, HF7, HF8, HF9, HF10, HF11, HF12, HF13, HF14, HF15, HF16};
		}
	}
	
	
	public enum AggreHholdTypes {
		couple(0), 
		coupleU15(1), coupleO15(2), coupleU15O15(3), 
		loneParentU15(4), loneParentO15(5), loneParentU15O15(6),  
		Other(7);

		private int index;

		private AggreHholdTypes (int newIndex) {
			index = newIndex;
		}
	}
		
	
	public enum DBNames {
		individuals, households
	};
	
	public enum DBVariableTypes {
		int4, text
	};
	
	public enum IndivSchema {
		highest_education, household_relationship, income, occupation, population, registered_marital_status, social_marital_status, work_travel_mode;
	};

	public enum HholdSchema {
		family_composition, family_composition_bysex, gross_family, gross_household, household_composition, min_individuals
	};

	
	
	public enum B22Cols {
		id(0, null), year_1(1, null), year_2(2, null), 
		Married(3, HholdRelSP.Married), DeFacto(4, HholdRelSP.Married), LoneParent(5, HholdRelSP.LoneParent), 
		U15Child(6, HholdRelSP.U15Child), Student(7, HholdRelSP.Student), O15Child(8, HholdRelSP.O15Child), 
		Relative(9, HholdRelSP.Relative), NonRelative(10, HholdRelSP.Relative), 
		GroupHhold(11, HholdRelSP.GroupHhold), LonePerson(12, HholdRelSP.LonePerson), 
		Visitor(13, null), note(-1, null);
		
		private int index;
		private HholdRelSP hhRelSP;
		
		private B22Cols (int newIndex, HholdRelSP relSP) {
			index = newIndex;
			hhRelSP = relSP;
		}
		
		public int getIndex() {
			return index;
		} 
		
		public HholdRelSP getHhRelSP() {
			return hhRelSP;
		}
	}
	
	/**
	 * values of Gender (i.e. "_male" and "_female") must be exactly the same the values in column 'note' of table B22.
	 * @author nhuynh
	 *
	 */
	public enum Genders {
		_female(0), _male(1);
		
		private int value;
		
		private Genders (int newIndex) {
			value = newIndex;
		}
		
		public int getValue() {
			return value;
		} 
		
		public static Genders getGenderByValue(int val) {
			if (val==0) return Genders._female;
			else return Genders._male;
		}
		
		public B25Cols getB25Col() {
			if (this.equals(_female))
				return B25Cols.females;
			else if (this.equals(_male))
				return B25Cols.males;
			else return null;
		}
	};
	
	public enum AgeGroups {
		_0_14(0, 14, 0),
		_15_24(15, 24, 1),
		_25_34(25, 34, 2),
		_35_44(35, 44, 3),
		_45_54(45, 54, 4),
		_55_64(55, 64, 5),
		_65_74(65, 74, 6),
		_75_84(75, 84, 7),
		_85_99(85, 99, 8);
		
		private int age;
		private int uBound;
		private int lBound;
		private int index;
		
		private AgeGroups(int lowerBound, int upperBound, int newIndex) {
			uBound = upperBound;
			lBound = lowerBound;
			index = newIndex;
		}
		
		public int getAge() {
			age = lBound + (int)((uBound-lBound)*random.nextDouble());
			return age;
		}
		
		public int getMaxAge() {
			return uBound;
		}
		
		public int getMinAge() {
			return lBound;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static AgeGroups getAgeGroup(int queryAge) {
			for (AgeGroups ageGroup : AgeGroups.values()) {
				if (ageGroup.lBound<=queryAge && queryAge<=ageGroup.uBound) {
					return ageGroup;
				}
				if (queryAge>_85_99.getMaxAge()) {
					return _85_99;
				}
				if (queryAge<_0_14.getMinAge()) {
					return _0_14;
				}
			}
			return null;
		}

		public static AgeGroups getAgeGroupByIndex(int queryIndex) {
			for (AgeGroups ageGroup : AgeGroups.values()) {
				if (ageGroup.index==queryIndex) {
					return ageGroup;
				}
			}
			return null;
		}
	}
	
	public enum B24Cols {
		id(0), families(1), note(-1);
		
		private int index;
		
		private B24Cols (int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		} 
	}
	
	public enum B25Cols {
		id(0), males(1), females(2), persons(3), note(-1);
		
		private int index;
		
		private B25Cols (int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		} 
	}

	
	public enum B24B25Rows {
		HF1(0), HF2(1), HF3(2), HF4(3), HF5(4), HF6(5), HF7(6), HF8(7), HF9(8), HF10(9), HF11(10), HF12(11), HF13(12), HF14(13), HF15(14), HF16(15);
		
		private int index;
		
		private B24B25Rows (int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		} 
	}
	

	
	public enum B30Cols {
		id(0), familyHholds(1), nonFamilyHholds(2), notes(-1);
		
		private int index;
		
		private B30Cols (int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	
	public enum B30Rows {
		one(0,1), two(1,2), three(2,3), four(3,4), five(4,5), six(5,6);
		
		private int index;
		private int value;
		
		private B30Rows (int newIndex, int newValue) {
			index = newIndex;
			value = newValue;
		}
		
		public int getIndex() {
			return index;
		}
		
		public int getValue() {
			return value;
		}
		
		public static B30Rows getRowFromValue(int newValue) {
			if (newValue>=6) return B30Rows.six;
			for (B30Rows row : B30Rows.values()) {
				if (row.value==newValue) {
					return row;
				}
			}
			return null;
		}
		
		public static B30Rows getRowFromIndex(int newIndex) {
			for (B30Rows row : B30Rows.values()) {
				if (row.index==newIndex)
					return row;
			}
			return null;
		}
	}
	
	public static final int[][] minResidents = {
		{1,-1,-1,-1,-1,-1,1,1},
		{2,2,-1,-1,-1,0,-1,2},
		{3,2,1,1,1,0,-1,5},
		{4,2,1,1,-1,0,-1,4},
		{5,2,1,-1,1,0,-1,4},
		{6,2,1,-1,-1,0,-1,3},
		{7,2,-1,1,1,0,-1,4},
		{8,2,-1,1,-1,0,-1,3},
		{9,2,-1,-1,1,0,-1,3},
		{10,1,1,1,1,0,-1,4},
		{11,1,1,1,-1,0,-1,3},
		{12,1,1,-1,1,0,-1,3},
		{13,1,1,-1,-1,0,-1,2},
		{14,1,-1,1,1,0,-1,3},
		{15,1,-1,1,-1,0,-1,2},
		{16,1,-1,-1,1,0,-1,2},
		{17,-1,-1,-1,-1,2,-1,2}};
	
	
	public enum MinResidentCols {
		index(0), min_parents(1), min_u15(2), min_students(3), min_o15(4), min_relatives(5), min_non_family(6), min_residents(7), note(-1);
		
		private int colIndex;
		
		private MinResidentCols(int newIndex) {
			colIndex = newIndex;
		}
		
		public int getIndex() {
			return colIndex;
		}
	};
	
	
	public enum InputBirthRatesColumn {
		age(0), firstChild(1), secondChild(2), thirdChild(3), fourthChild(4), fifthChild(5), sixthChildMore(6);
		
		private int index;
		
		private InputBirthRatesColumn(int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	public enum InputRatesColumn {
		age(0), probability(1);
		
		private int index;
		
		private InputRatesColumn(int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	/*
	private static HashMap<String,String> zonesNameDescription;
	
	public static HashMap<String,String> getZonesNameDescription() {
		return zonesNameDescription;
	}
	
	public static void setZonesNameDescription(HashMap<String,String> newZonesNameDescription) {
		zonesNameDescription = newZonesNameDescription;
	}
	*/
}
