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
	
	public static String commonInputPathFemaleByHhType = "input tables/inputTables/femaleByHhType/"; 
	public static String commonInputPathMaleByHhType = "input tables/inputTables/maleByHhType/";
	public static String commonInputPathHholdByHhType = "input tables/inputTables/hholdByHhType/";
	public static String commonInputPathHhRelFemale = "input tables/inputTables/hhRelFemale/";
	public static String commonInputPathHhRelMale = "input tables/inputTables/hhRelMale/";
	public static String commonInputPathNFCompBySize = "input tables/inputTables/nfCompBySize/";
	public static String commonInputPathHFCompBySize = "input tables/inputTables/hfCompBySize/";
	
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
	
	public static String inputTablesPath = "input tables/";
	public static String outputTablesPath = "post processing/output SP evolution/";
	
	public static String charFemaleByHhType = "femaleByHhType"; 
	public static String charMaleByHhType = "maleByHhType";
	public static String charHholdByHhType = "hholdByHhType";
	public static String charHhRelFemale = "hhRelFemale";
	public static String charHhRelMale = "hhRelMale";
	public static String charNFCompBySize = "nfCompBySize";
	public static String charHFCompBySize = "hfCompBySize";
	
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
	
	
	private static HashMap<String,String> zonesNameDescription;
	
	public static HashMap<String,String> getZonesNameDescription() {
		return zonesNameDescription;
	}
	
	public static void setZonesNameDescription(HashMap<String,String> newZonesNameDescription) {
		zonesNameDescription = newZonesNameDescription;
	}
	
	public static void setZonesNameDescription() {
		zonesNameDescription = new HashMap<String,String>();
		zonesNameDescription.put("gs_1400508", "Green Square");
		zonesNameDescription.put("gs_1400509", "Green Square");
		zonesNameDescription.put("gs_1400512", "Green Square");
		zonesNameDescription.put("gs_1400606", "Green Square");
		zonesNameDescription.put("gs_1400607", "Green Square");
		zonesNameDescription.put("gs_1400608", "Green Square");
		zonesNameDescription.put("gs_1400611", "Green Square");
		zonesNameDescription.put("gs_1400612", "Green Square");
		zonesNameDescription.put("gs_1400615", "Green Square");
		zonesNameDescription.put("gs_1400616", "Green Square");
		zonesNameDescription.put("gs_1400617", "Green Square"); 
		zonesNameDescription.put("gs_1400704", "Green Square"); 
		zonesNameDescription.put("gs_1400705", "Green Square"); 
		zonesNameDescription.put("gs_1400706", "Green Square"); 
		zonesNameDescription.put("gs_1400707", "Green Square"); 
		zonesNameDescription.put("gs_1400708", "Green Square"); 
		zonesNameDescription.put("gs_1400709", "Green Square"); 
		zonesNameDescription.put("gs_1400710", "Green Square"); 
		zonesNameDescription.put("gs_1400711", "Green Square"); 
		zonesNameDescription.put("gs_1400712", "Green Square"); 
		zonesNameDescription.put("gs_1400713", "Green Square"); 
		zonesNameDescription.put("gs_1400714", "Green Square"); 
		zonesNameDescription.put("gs_1400715", "Green Square"); 
		zonesNameDescription.put("gs_1400716", "Green Square"); 
		zonesNameDescription.put("gs_1400717", "Green Square"); 
		zonesNameDescription.put("gs_1400718", "Green Square"); 
		zonesNameDescription.put("gs_1400719", "Green Square"); 
		zonesNameDescription.put("gs_1400720", "Green Square"); 
		zonesNameDescription.put("gs_1400721", "Green Square"); 
		zonesNameDescription.put("gs_1400722", "Green Square"); 
		zonesNameDescription.put("gs_1400723", "Green Square"); 
		zonesNameDescription.put("gs_1400812", "Green Square"); 
		zonesNameDescription.put("gs_1400813", "Green Square"); 
		zonesNameDescription.put("gs_1400815", "Green Square");  
		
		zonesNameDescription.put("rw_1430102", "Randwick");
		zonesNameDescription.put("rw_1430103", "Randwick"); 
		zonesNameDescription.put("rw_1430104", "Randwick"); 
		zonesNameDescription.put("rw_1430105", "Randwick"); 
		zonesNameDescription.put("rw_1430106", "Randwick"); 
		zonesNameDescription.put("rw_1430107", "Randwick"); 
		zonesNameDescription.put("rw_1430108", "Randwick"); 
		zonesNameDescription.put("rw_1430109", "Randwick"); 
		zonesNameDescription.put("rw_1430110", "Randwick"); 
		zonesNameDescription.put("rw_1430111", "Randwick"); 
		zonesNameDescription.put("rw_1430201", "Randwick"); 
		zonesNameDescription.put("rw_1430202", "Randwick"); 
		zonesNameDescription.put("rw_1430203", "Randwick"); 
		zonesNameDescription.put("rw_1430204", "Randwick"); 
		zonesNameDescription.put("rw_1430205", "Randwick"); 
		zonesNameDescription.put("rw_1430206", "Randwick"); 
		zonesNameDescription.put("rw_1430207", "Randwick"); 
		zonesNameDescription.put("rw_1430208", "Randwick"); 
		zonesNameDescription.put("rw_1430209", "Randwick"); 
		zonesNameDescription.put("rw_1430210", "Randwick"); 
		zonesNameDescription.put("rw_1430211", "Randwick"); 
		zonesNameDescription.put("rw_1430212", "Randwick"); 
		zonesNameDescription.put("rw_1430213", "Randwick"); 
		zonesNameDescription.put("rw_1430301", "Randwick"); 
		zonesNameDescription.put("rw_1430302", "Randwick"); 
		zonesNameDescription.put("rw_1430303", "Randwick"); 
		zonesNameDescription.put("rw_1430304", "Randwick"); 
		zonesNameDescription.put("rw_1430305", "Randwick"); 
		zonesNameDescription.put("rw_1430306", "Randwick"); 
		zonesNameDescription.put("rw_1430307", "Randwick"); 
		zonesNameDescription.put("rw_1430308", "Randwick"); 
		zonesNameDescription.put("rw_1430309", "Randwick"); 
		zonesNameDescription.put("rw_1430310", "Randwick"); 
		zonesNameDescription.put("rw_1430311", "Randwick"); 
		zonesNameDescription.put("rw_1430312", "Randwick"); 
		zonesNameDescription.put("rw_1430401", "Randwick"); 
		zonesNameDescription.put("rw_1430402", "Randwick"); 
		zonesNameDescription.put("rw_1430403", "Randwick"); 
		zonesNameDescription.put("rw_1430404", "Randwick"); 
		zonesNameDescription.put("rw_1430405", "Randwick"); 
		zonesNameDescription.put("rw_1430406", "Randwick"); 
		zonesNameDescription.put("rw_1430407", "Randwick"); 
		zonesNameDescription.put("rw_1430408", "Randwick"); 
		zonesNameDescription.put("rw_1430409", "Randwick"); 
		zonesNameDescription.put("rw_1430410", "Randwick"); 
		zonesNameDescription.put("rw_1430411", "Randwick"); 
		zonesNameDescription.put("rw_1430412", "Randwick"); 
		zonesNameDescription.put("rw_1430501", "Randwick"); 
		zonesNameDescription.put("rw_1430502", "Randwick"); 
		zonesNameDescription.put("rw_1430503", "Randwick"); 
		zonesNameDescription.put("rw_1430504", "Randwick"); 
		zonesNameDescription.put("rw_1430505", "Randwick"); 
		zonesNameDescription.put("rw_1430506", "Randwick"); 
		zonesNameDescription.put("rw_1430507", "Randwick"); 
		zonesNameDescription.put("rw_1430508", "Randwick"); 
		zonesNameDescription.put("rw_1430509", "Randwick"); 
		zonesNameDescription.put("rw_1430510", "Randwick"); 
		zonesNameDescription.put("rw_1430511", "Randwick"); 
		zonesNameDescription.put("rw_1430512", "Randwick"); 
		zonesNameDescription.put("rw_1430513", "Randwick"); 
		zonesNameDescription.put("rw_1430601", "Randwick"); 
		zonesNameDescription.put("rw_1430602", "Randwick"); 
		zonesNameDescription.put("rw_1430603", "Randwick"); 
		zonesNameDescription.put("rw_1430604", "Randwick"); 
		zonesNameDescription.put("rw_1430605", "Randwick"); 
		zonesNameDescription.put("rw_1430606", "Randwick"); 
		zonesNameDescription.put("rw_1430607", "Randwick"); 
		zonesNameDescription.put("rw_1430608", "Randwick"); 
		zonesNameDescription.put("rw_1430609", "Randwick"); 
		zonesNameDescription.put("rw_1430610", "Randwick"); 
		zonesNameDescription.put("rw_1430611", "Randwick"); 
		zonesNameDescription.put("rw_1430612", "Randwick"); 
		zonesNameDescription.put("rw_1430613", "Randwick"); 
		zonesNameDescription.put("rw_1430701", "Randwick"); 
		zonesNameDescription.put("rw_1430702", "Randwick"); 
		zonesNameDescription.put("rw_1430703", "Randwick"); 
		zonesNameDescription.put("rw_1430704", "Randwick"); 
		zonesNameDescription.put("rw_1430705", "Randwick"); 
		zonesNameDescription.put("rw_1430706", "Randwick"); 
		zonesNameDescription.put("rw_1430707", "Randwick"); 
		zonesNameDescription.put("rw_1430708", "Randwick"); 
		zonesNameDescription.put("rw_1430709", "Randwick"); 
		zonesNameDescription.put("rw_1430710", "Randwick"); 
		zonesNameDescription.put("rw_1430711", "Randwick"); 
		zonesNameDescription.put("rw_1430712", "Randwick"); 
		zonesNameDescription.put("rw_1430801", "Randwick"); 
		zonesNameDescription.put("rw_1430802", "Randwick"); 
		zonesNameDescription.put("rw_1430803", "Randwick"); 
		zonesNameDescription.put("rw_1430804", "Randwick"); 
		zonesNameDescription.put("rw_1430805", "Randwick"); 
		zonesNameDescription.put("rw_1430806", "Randwick"); 
		zonesNameDescription.put("rw_1430807", "Randwick"); 
		zonesNameDescription.put("rw_1430808", "Randwick"); 
		zonesNameDescription.put("rw_1430809", "Randwick"); 
		zonesNameDescription.put("rw_1430810", "Randwick"); 
		zonesNameDescription.put("rw_1430811", "Randwick"); 
		zonesNameDescription.put("rw_1430812", "Randwick"); 
		zonesNameDescription.put("rw_1430901", "Randwick"); 
		zonesNameDescription.put("rw_1430902", "Randwick"); 
		zonesNameDescription.put("rw_1430903", "Randwick"); 
		zonesNameDescription.put("rw_1430904", "Randwick"); 
		zonesNameDescription.put("rw_1430905", "Randwick"); 
		zonesNameDescription.put("rw_1430906", "Randwick"); 
		zonesNameDescription.put("rw_1430907", "Randwick"); 
		zonesNameDescription.put("rw_1430908", "Randwick"); 
		zonesNameDescription.put("rw_1430909", "Randwick"); 
		zonesNameDescription.put("rw_1430910", "Randwick"); 
		zonesNameDescription.put("rw_1430911", "Randwick"); 
		zonesNameDescription.put("rw_1430912", "Randwick"); 
		zonesNameDescription.put("rw_1431001", "Randwick"); 
		zonesNameDescription.put("rw_1431002", "Randwick"); 
		zonesNameDescription.put("rw_1431003", "Randwick"); 
		zonesNameDescription.put("rw_1431004", "Randwick"); 
		zonesNameDescription.put("rw_1431005", "Randwick"); 
		zonesNameDescription.put("rw_1431006", "Randwick"); 
		zonesNameDescription.put("rw_1431007", "Randwick"); 
		zonesNameDescription.put("rw_1431008", "Randwick"); 
		zonesNameDescription.put("rw_1431009", "Randwick"); 
		zonesNameDescription.put("rw_1431010", "Randwick"); 
		zonesNameDescription.put("rw_1431011", "Randwick"); 
		zonesNameDescription.put("rw_1431012", "Randwick"); 
		zonesNameDescription.put("rw_1431101", "Randwick"); 
		zonesNameDescription.put("rw_1431102", "Randwick"); 
		zonesNameDescription.put("rw_1431103", "Randwick"); 
		zonesNameDescription.put("rw_1431104", "Randwick"); 
		zonesNameDescription.put("rw_1431105", "Randwick"); 
		zonesNameDescription.put("rw_1431106", "Randwick"); 
		zonesNameDescription.put("rw_1431107", "Randwick"); 
		zonesNameDescription.put("rw_1431108", "Randwick"); 
		zonesNameDescription.put("rw_1431109", "Randwick"); 
		zonesNameDescription.put("rw_1431110", "Randwick"); 
		zonesNameDescription.put("rw_1431111", "Randwick"); 
		zonesNameDescription.put("rw_1431112", "Randwick"); 
		zonesNameDescription.put("rw_1431113", "Randwick"); 
		zonesNameDescription.put("rw_1431114", "Randwick"); 
		zonesNameDescription.put("rw_1431201", "Randwick"); 
		zonesNameDescription.put("rw_1431202", "Randwick"); 
		zonesNameDescription.put("rw_1431203", "Randwick"); 
		zonesNameDescription.put("rw_1431204", "Randwick"); 
		zonesNameDescription.put("rw_1431205", "Randwick"); 
		zonesNameDescription.put("rw_1431206", "Randwick"); 
		zonesNameDescription.put("rw_1431207", "Randwick"); 
		zonesNameDescription.put("rw_1431208", "Randwick"); 
		zonesNameDescription.put("rw_1431209", "Randwick"); 
		zonesNameDescription.put("rw_1431210", "Randwick"); 
		zonesNameDescription.put("rw_1431211", "Randwick"); 
		zonesNameDescription.put("rw_1431301", "Randwick"); 
		zonesNameDescription.put("rw_1431302", "Randwick"); 
		zonesNameDescription.put("rw_1431303", "Randwick"); 
		zonesNameDescription.put("rw_1431304", "Randwick"); 
		zonesNameDescription.put("rw_1431305", "Randwick"); 
		zonesNameDescription.put("rw_1431306", "Randwick"); 
		zonesNameDescription.put("rw_1431307", "Randwick"); 
		zonesNameDescription.put("rw_1431308", "Randwick"); 
		zonesNameDescription.put("rw_1431309", "Randwick"); 
		zonesNameDescription.put("rw_1431310", "Randwick"); 
		zonesNameDescription.put("rw_1431311", "Randwick"); 
		zonesNameDescription.put("rw_1431312", "Randwick"); 
		zonesNameDescription.put("rw_1431401", "Randwick"); 
		zonesNameDescription.put("rw_1431402", "Randwick"); 
		zonesNameDescription.put("rw_1431403", "Randwick"); 
		zonesNameDescription.put("rw_1431404", "Randwick"); 
		zonesNameDescription.put("rw_1431405", "Randwick"); 
		zonesNameDescription.put("rw_1431406", "Randwick"); 
		zonesNameDescription.put("rw_1431407", "Randwick"); 
		zonesNameDescription.put("rw_1431408", "Randwick"); 
		zonesNameDescription.put("rw_1431409", "Randwick"); 
		zonesNameDescription.put("rw_1431410", "Randwick"); 
		zonesNameDescription.put("rw_1431411", "Randwick"); 
		zonesNameDescription.put("rw_1431412", "Randwick"); 
		zonesNameDescription.put("rw_1431413", "Randwick"); 
		zonesNameDescription.put("rw_1431501", "Randwick"); 
		zonesNameDescription.put("rw_1431502", "Randwick"); 
		zonesNameDescription.put("rw_1431503", "Randwick"); 
		zonesNameDescription.put("rw_1431504", "Randwick"); 
		zonesNameDescription.put("rw_1431505", "Randwick"); 
		zonesNameDescription.put("rw_1431506", "Randwick"); 
		zonesNameDescription.put("rw_1431507", "Randwick"); 
		zonesNameDescription.put("rw_1431508", "Randwick"); 
		zonesNameDescription.put("rw_1431509", "Randwick"); 
		zonesNameDescription.put("rw_1431510", "Randwick"); 
		zonesNameDescription.put("rw_1431511", "Randwick"); 
		zonesNameDescription.put("rw_1431512", "Randwick"); 
		zonesNameDescription.put("rw_1431513", "Randwick"); 
		zonesNameDescription.put("rw_1431514", "Randwick"); 
		zonesNameDescription.put("rw_1431515", "Randwick"); 
		zonesNameDescription.put("rw_1431601", "Randwick"); 
		zonesNameDescription.put("rw_1431602", "Randwick"); 
		zonesNameDescription.put("rw_1431603", "Randwick"); 
		zonesNameDescription.put("rw_1431604", "Randwick"); 
		zonesNameDescription.put("rw_1431605", "Randwick"); 
		zonesNameDescription.put("rw_1431606", "Randwick"); 
		zonesNameDescription.put("rw_1431607", "Randwick"); 
		zonesNameDescription.put("rw_1431608", "Randwick"); 
		zonesNameDescription.put("rw_1431609", "Randwick"); 
		zonesNameDescription.put("rw_1431610", "Randwick"); 
		zonesNameDescription.put("rw_1431611", "Randwick"); 
		zonesNameDescription.put("rw_1431612", "Randwick"); 
		zonesNameDescription.put("rw_1431701", "Randwick"); 
		zonesNameDescription.put("rw_1431702", "Randwick"); 
		zonesNameDescription.put("rw_1431703", "Randwick"); 
		zonesNameDescription.put("rw_1431704", "Randwick"); 
		zonesNameDescription.put("rw_1431705", "Randwick"); 
		zonesNameDescription.put("rw_1431706", "Randwick"); 
		zonesNameDescription.put("rw_1431707", "Randwick"); 
		zonesNameDescription.put("rw_1431708", "Randwick"); 
		zonesNameDescription.put("rw_1431709", "Randwick"); 
		zonesNameDescription.put("rw_1431710", "Randwick"); 
		zonesNameDescription.put("rw_1431711", "Randwick"); 
		zonesNameDescription.put("rw_1431712", "Randwick"); 
		zonesNameDescription.put("rw_1431801", "Randwick"); 
		zonesNameDescription.put("rw_1431802", "Randwick"); 
		zonesNameDescription.put("rw_1431803", "Randwick"); 
		zonesNameDescription.put("rw_1431804", "Randwick"); 
		zonesNameDescription.put("rw_1431806", "Randwick"); 
		zonesNameDescription.put("rw_1431807", "Randwick"); 
		zonesNameDescription.put("rw_1431808", "Randwick"); 
		zonesNameDescription.put("rw_1431809", "Randwick"); 
		zonesNameDescription.put("rw_1431810", "Randwick"); 
		zonesNameDescription.put("rw_1431811", "Randwick"); 
		zonesNameDescription.put("rw_1431812", "Randwick"); 
		zonesNameDescription.put("rw_1431814", "Randwick"); 
		zonesNameDescription.put("rw_1430101", "Randwick");
	}
	
//	public enum Zones {
//		gs_1400508, gs_1400509, gs_1400512, gs_1400606, gs_1400607, gs_1400608, gs_1400611, gs_1400612, gs_1400615, gs_1400616, 
//		gs_1400617, gs_1400704, gs_1400705, gs_1400706, gs_1400707, gs_1400708, gs_1400709, gs_1400710, gs_1400711, gs_1400712, 
//		gs_1400713, gs_1400714, gs_1400715, gs_1400716, gs_1400717, gs_1400718, gs_1400719, gs_1400720, gs_1400721, gs_1400722, 
//		gs_1400723, gs_1400812, gs_1400813, gs_1400815, 
//		rw_1430102, rw_1430103, rw_1430104, rw_1430105, rw_1430106, rw_1430107, rw_1430108, rw_1430109, rw_1430110, rw_1430111, 
//		rw_1430201, rw_1430202, rw_1430203, rw_1430204, rw_1430205, rw_1430206, rw_1430207, rw_1430208, rw_1430209, rw_1430210, 
//		rw_1430211, rw_1430212, rw_1430213, rw_1430301, rw_1430302, rw_1430303, rw_1430304, rw_1430305, rw_1430306, rw_1430307, 
//		rw_1430308, rw_1430309, rw_1430310, rw_1430311, rw_1430312, rw_1430401, rw_1430402, rw_1430403, rw_1430404, rw_1430405, 
//		rw_1430406, rw_1430407, rw_1430408, rw_1430409, rw_1430410, rw_1430411, rw_1430412, rw_1430501, rw_1430502, rw_1430503, 
//		rw_1430504, rw_1430505, rw_1430506, rw_1430507, rw_1430508, rw_1430509, rw_1430510, rw_1430511, rw_1430512, rw_1430513, 
//		rw_1430601, rw_1430602, rw_1430603, rw_1430604, rw_1430605, rw_1430606, rw_1430607, rw_1430608, rw_1430609, rw_1430610, 
//		rw_1430611, rw_1430612, rw_1430613, rw_1430701, rw_1430702, rw_1430703, rw_1430704, rw_1430705, rw_1430706, rw_1430707, 
//		rw_1430708, rw_1430709, rw_1430710, rw_1430711, rw_1430712, rw_1430801, rw_1430802, rw_1430803, rw_1430804, rw_1430805, 
//		rw_1430806, rw_1430807, rw_1430808, rw_1430809, rw_1430810, rw_1430811, rw_1430812, rw_1430901, rw_1430902, rw_1430903, 
//		rw_1430904, rw_1430905, rw_1430906, rw_1430907, rw_1430908, rw_1430909, rw_1430910, rw_1430911, rw_1430912, rw_1431001, 
//		rw_1431002, rw_1431003, rw_1431004, rw_1431005, rw_1431006, rw_1431007, rw_1431008, rw_1431009, rw_1431010, rw_1431011, 
//		rw_1431012, rw_1431101, rw_1431102, rw_1431103, rw_1431104, rw_1431105, rw_1431106, rw_1431107, rw_1431108, rw_1431109, 
//		rw_1431110, rw_1431111, rw_1431112, rw_1431113, rw_1431114, rw_1431201, rw_1431202, rw_1431203, rw_1431204, rw_1431205, 
//		rw_1431206, rw_1431207, rw_1431208, rw_1431209, rw_1431210, rw_1431211, rw_1431301, rw_1431302, rw_1431303, rw_1431304, 
//		rw_1431305, rw_1431306, rw_1431307, rw_1431308, rw_1431309, rw_1431310, rw_1431311, rw_1431312, rw_1431401, rw_1431402, 
//		rw_1431403, rw_1431404, rw_1431405, rw_1431406, rw_1431407, rw_1431408, rw_1431409, rw_1431410, rw_1431411, rw_1431412, 
//		rw_1431413, rw_1431501, rw_1431502, rw_1431503, rw_1431504, rw_1431505, rw_1431506, rw_1431507, rw_1431508, rw_1431509, 
//		rw_1431510, rw_1431511, rw_1431512, rw_1431513, rw_1431514, rw_1431515, rw_1431601, rw_1431602, rw_1431603, rw_1431604, 
//		rw_1431605, rw_1431606, rw_1431607, rw_1431608, rw_1431609, rw_1431610, rw_1431611, rw_1431612, rw_1431701, rw_1431702, 
//		rw_1431703, rw_1431704, rw_1431705, rw_1431706, rw_1431707, rw_1431708, rw_1431709, rw_1431710, rw_1431711, rw_1431712, 
//		rw_1431801, rw_1431802, rw_1431803, rw_1431804, rw_1431806, rw_1431807, rw_1431808, rw_1431809, rw_1431810, rw_1431811, 
//		rw_1431812, rw_1431814, rw_1430101
//	}

}
