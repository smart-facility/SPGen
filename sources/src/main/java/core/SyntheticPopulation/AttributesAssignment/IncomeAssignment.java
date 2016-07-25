package core.SyntheticPopulation.AttributesAssignment;

import java.util.ArrayList;
import java.util.HashMap;

import core.ArrayHandler;
import core.HardcodedData;
import core.TargetArea;
import core.HardcodedData.Genders;
import core.TextFileHandler;
import core.SyntheticPopulation.Household;
import core.SyntheticPopulation.Population;

public class IncomeAssignment {
	
	private static HashMap<String,int[]> ageGroupBounds;
	private static HashMap<String,int[]> incomeBounds;
	private static HashMap<String,Genders> genders; 
	private static HashMap<Integer,ArrayList<Integer>> indivIDByZoneID;
	private static HashMap<Genders,int[][]> incomeData;
	
	public static HashMap<Genders, int[][]> getIncomeData() {
		return incomeData;
	}

	public static void setIncomeData(HashMap<Genders, int[][]> incomeData) {
		IncomeAssignment.incomeData = incomeData;
	}


	private enum ColumnsIncByAgeBySex {
		Sex(0),
		AgeGroup(1),
		IncomeBracket(2),
		CD(3),
		count(4);
		
		private int colIndex;
		
		private ColumnsIncByAgeBySex(int newIndex) {
			colIndex = newIndex;
		}
		
		public int getColIndex() {
			return colIndex;
		}
	}
	
	public static void assignGenderMapping() {
		genders = new HashMap<String, HardcodedData.Genders>();
		genders.put("Male",Genders._male);
		genders.put("Female",Genders._female);
	}
	
	
	/*
	public static void assignAgeGroupBounds() {
		ageGroupBounds = new HashMap<String,int[]>();
		
		ageGroupBounds.put("0-4 years", new int[] {0,0,4});
		ageGroupBounds.put("5-9 years", new int[] {1,5,9});
		ageGroupBounds.put("10-14 years", new int[] {2,10,14});
		ageGroupBounds.put("15-19 years", new int[] {3,15,19});
		ageGroupBounds.put("20-24 years", new int[] {4,20,24});
		ageGroupBounds.put("25-29 years", new int[] {5,25,29});
		ageGroupBounds.put("30-34 years", new int[] {6,30,34});
		ageGroupBounds.put("35-39 years", new int[] {7,35,39});
		ageGroupBounds.put("40-44 years", new int[] {8,40,44});
		ageGroupBounds.put("45-49 years", new int[] {9,45,49});
		ageGroupBounds.put("50-54 years", new int[] {10,50,54});
		ageGroupBounds.put("55-59 years", new int[] {11,55,59});
		ageGroupBounds.put("60-64 years", new int[] {12,60,64});
		ageGroupBounds.put("65-69 years", new int[] {13,65,69});
		ageGroupBounds.put("70-74 years", new int[] {14,70,74});
		ageGroupBounds.put("75-79 years", new int[] {15,75,79});
		ageGroupBounds.put("80-84 years", new int[] {16,80,84});
		ageGroupBounds.put("85-89 years", new int[] {17,85,89});
		ageGroupBounds.put("90-94 years", new int[] {18,90,94});
		ageGroupBounds.put("95-99 years", new int[] {19,95,99});
		ageGroupBounds.put("100 years and over", new int[] {20,100,110});
	}
	*/
	
	public static void assignAgeGroupBounds() {
		ageGroupBounds = new HashMap<String,int[]>();
		
		//ageGroupBounds.put("0-14", new int[] {0,0,14});
		ageGroupBounds.put("15-19", new int[] {0,15,19});
		ageGroupBounds.put("20-24", new int[] {1,20,24});
		ageGroupBounds.put("25-34", new int[] {2,25,34});
		ageGroupBounds.put("35-44", new int[] {3,35,44});
		ageGroupBounds.put("45-54", new int[] {4,45,54});
		ageGroupBounds.put("55-64", new int[] {5,55,64});
		ageGroupBounds.put("65-74", new int[] {6,65,74});
		ageGroupBounds.put("75-84", new int[] {7,75,84});
		ageGroupBounds.put("85 over", new int[] {8,85,110});
	}
	
	/*
	public static void assignIncomeBounds() {
		incomeBounds = new HashMap<String, int[]>();
		
		incomeBounds.put("$1-$149", new int[] {0,1,149});
		incomeBounds.put("$150-$249", new int[] {1,150,249});
		incomeBounds.put("$250-$399", new int[] {2,250,399});
		incomeBounds.put("$400-$599", new int[] {3,400,599});
		incomeBounds.put("$600-$799", new int[] {4,600,799});
		incomeBounds.put("$800-$999", new int[] {5,800,999});
		incomeBounds.put("$1000-$1299", new int[] {6,1000,1299});
		incomeBounds.put("$1300-$1599", new int[] {7,1300,1599});
		incomeBounds.put("$1600-$1999", new int[] {8,1600,1999});
		incomeBounds.put("$2000 or more", new int[] {9,2000,10000});
		incomeBounds.put("Nil income", new int[] {10,0,0});
	}
	*/
	
	public static void assignIncomeBounds() {
		incomeBounds = new HashMap<String, int[]>();
		
		incomeBounds.put("1-199", new int[] {0,1,199});
		incomeBounds.put("200-299", new int[] {1,200,299});
		incomeBounds.put("300-399", new int[] {2,300,399});
		incomeBounds.put("400-599", new int[] {3,400,599});
		incomeBounds.put("600-799", new int[] {4,600,799});
		incomeBounds.put("800-999", new int[] {5,800,999});
		incomeBounds.put("1000-1249", new int[] {6,1000,1249});
		incomeBounds.put("1250-1499", new int[] {7,1250,1499});
		incomeBounds.put("1500-1999", new int[] {8,1500,1999});
		incomeBounds.put("2000 more", new int[] {9,2000,10000});
		incomeBounds.put("Neg-Nil", new int[] {10,0,0});
		incomeBounds.put("unknown", new int[] {11,0,0});
	}
	
	
	public static void groupIndivIDByZoneID() {
		indivIDByZoneID = new HashMap<Integer, ArrayList<Integer>>();
		for (Household hhold : Population.getHhPool().values()) {
			Integer zoneID = new Integer(hhold.getAreaID());
			ArrayList<Integer> residentsID = hhold.getResidentsID();
			
			ArrayList<Integer> indivList = new ArrayList<Integer>();
			if (indivIDByZoneID.containsKey(zoneID)) {
				indivList = indivIDByZoneID.get(zoneID);
			}
			
			indivList.addAll(residentsID);
			indivIDByZoneID.put(zoneID, indivList);
		}
	}
	
	
	public static boolean readInIncome(String fileNameIncome) {
		ArrayList<ArrayList<String>> rawData = TextFileHandler.readCSV(fileNameIncome);
		if (rawData==null || rawData.size()==0) {
			return false;
		}
		
		incomeData = new HashMap<HardcodedData.Genders, int[][]>();
		
		for (ArrayList<String> row : rawData) {
			// checks if this row contains valid data
			String rawGender = row.get(ColumnsIncByAgeBySex.Sex.getColIndex());
			String rawAgeGroup = row.get(ColumnsIncByAgeBySex.AgeGroup.getColIndex());
			String rawIncome = row.get(ColumnsIncByAgeBySex.IncomeBracket.getColIndex());
			String rawCount = row.get(ColumnsIncByAgeBySex.count.getColIndex());
			
			if (!genders.containsKey(rawGender)) {
				System.out.println("not recognise " + rawGender);
				continue;
			}
			
			if (!ageGroupBounds.containsKey(rawAgeGroup)) {
				System.out.println("not recognise " + rawAgeGroup);
				continue;
			}
			
			if (!incomeBounds.containsKey(rawIncome)) {
				System.out.println("not recognise " + rawIncome);
				continue;
			}
			
			int count;
			try {
				count = Integer.parseInt(rawCount);
			} catch (NumberFormatException e) {
				continue;
			}
			
			Genders crnGender = genders.get(rawGender);
			int ageGroupIndex = ageGroupBounds.get(rawAgeGroup)[0];
			int incomeIndex = incomeBounds.get(rawIncome)[0];
			
			int[][] data = new int[ageGroupBounds.size()][incomeBounds.size()];
			if (incomeData.containsKey(crnGender)) {
				data = incomeData.get(crnGender);
			}
			data[ageGroupIndex][incomeIndex] = count;
			
			incomeData.put(crnGender, data);
		}
		
		return true;
	}
	
	
	public static void assignIncome() {
		groupIndivIDByZoneID();
		assignGenderMapping();
		assignAgeGroupBounds();
		assignIncomeBounds();
		
		System.out.println("income init phases done!");
		
		for (String zoneID : TargetArea.getTargetAreaDesc().keySet()) {
			if (!indivIDByZoneID.containsKey(zoneID)) {
				System.out.println("No individuals in zone " + zoneID + ". Abort IncomeAssignment.assignIncome()!");
				continue;
			}
			
			String fileNameIncome = HardcodedData.commonInputPathIncByAgeBySex + zoneID + "_2011.csv";
			boolean readSuccess = readInIncome(fileNameIncome);
			if (!readSuccess) {
				System.out.println("Fail to retrieve file " + fileNameIncome + ". Abort IncomeAssignment.assignIncome()!");
			}
			
			System.out.println("read income file ok!");
			
			ArrayList<Integer> indivIDs = indivIDByZoneID.get(zoneID);
			
			for (Integer indID : indivIDs) {
				int newIncome = 0;
				if (Population.getIndivPool().get(indID).getAge()>HardcodedData.maxAgeU15) {
					Genders crnGender = Population.getIndivPool().get(indID).getGender();
					int ageGrpIndex = getAgeGroupIndex(Population.getIndivPool().get(indID).getAge());
					if (ageGrpIndex<0) {
						System.out.println("age group index not found for individual ID " + indID + ", aged " + Population.getIndivPool().get(indID).getAge());
					}
					int[] ageGrpIncome = incomeData.get(crnGender)[ageGrpIndex];
					
					//System.out.println("Getting income for age " + Population.getIndivPool().get(indID).getAge() + ", gender " + crnGender.toString());
					newIncome = generateIncome(ageGrpIncome);
				}
				
				Population.getIndivPool().get(indID).setIncomeWkly(newIncome);
			}
		}
		
	}
	
	
	
	private static int generateIncome(int[] ageGrpIncome) {
		double randDouble = HardcodedData.random.nextDouble();
		
		double[] incomeDistrib = ArrayHandler.normaliseArray(ageGrpIncome);
		
		int selectedIncGroup = -1;
		int newIncome = 0;
		
		double[] accuIncomeDistrib = new double[incomeDistrib.length+1];
		accuIncomeDistrib[0] = 0;
		for (int i=1; i<=accuIncomeDistrib.length-1; i++) {
			accuIncomeDistrib[i] = incomeDistrib[i-1] + accuIncomeDistrib[i-1];
			if (randDouble>accuIncomeDistrib[i-1] && randDouble<=accuIncomeDistrib[i]) {
				selectedIncGroup = i-1;
				break;
			}
		}
		
//		if (selectedIncGroup==-1) {
//			System.out.println("selectedIncGroup==-1, randomDouble = " + randDouble);
//			
//			System.out.println("ageGroupIncome");
//			for (int i=0; i<=ageGrpIncome.length-1; i++) {
//				System.out.println(ageGrpIncome[i]);
//			}
//			
//			System.out.println("\nincome distributions");
//			for (int i=0; i<=incomeDistrib.length-1; i++) {
//				System.out.println(incomeDistrib[i] + ", " + accuIncomeDistrib[i+1]);
//			}
//			
//			System.exit(0);
//		}
		
		for (String incGroup : incomeBounds.keySet()) {
			int[] values = incomeBounds.get(incGroup);
			if (values[0]==selectedIncGroup) {
				int minIncome = values[1];
				int maxIncome = values[2];
				newIncome = HardcodedData.random.nextInt(maxIncome-minIncome+1) + minIncome;
				break;
			}
		}
		
		return newIncome;
	}
	
	
	
	private static int getAgeGroupIndex(int indivAge) {
		for (String ageGroup : ageGroupBounds.keySet()) {
			int[] values = ageGroupBounds.get(ageGroup);
			if (indivAge>=values[1] && indivAge<=values[2]) {
				return values[0];
			}
		}
		return -1;
	}
}
