package core.SPConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import core.*;
import core.CensusTables.*;
import core.CensusTables.Preprocessing.CDDescriptionPreprocessor;
import core.CensusTables.Preprocessing.SLAFamiCompByTypePreprocessor;
import core.CensusTables.Preprocessing.SLAHhRelByAgeBySexPreprocessor;
import core.CensusTables.Preprocessing.SLAHholdCompBySizePreprocessor;
import core.HardcodedData.*;
import core.SyntheticPopulation.*;

public class SPConstructor {
	
	private static String getTimeStamp() {
		return (new Timestamp(new java.util.Date().getTime())).toString();
	}
	
	
	/**
	 * goes through each household in HouseholdPool.hholdsAllocated and transfered population in these households into Population
	 */
	public static void translateZonalSP2GlobalSP(String zoneName) {
		
		int crnHholdCountsInPopulation = 0;
		int[] hholdIDArray = ArrayHandler.toInt(Population.getHhPool().keySet());
		if (hholdIDArray.length>0) 
			crnHholdCountsInPopulation = ArrayHandler.max(hholdIDArray);
		
		int crnIndivCountsInPopulation = 0;
		int[] indivIDArray = ArrayHandler.toInt(Population.getIndivPool().keySet());
		if (indivIDArray.length>0) 
			crnIndivCountsInPopulation = ArrayHandler.max(indivIDArray);
		
		for (Integer hhID : HouseholdPool.getHholdsAllocated().keySet()) {
			ArrayList<Integer> indivIDs = HouseholdPool.getHholdsAllocated().get(hhID);
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(HouseholdPool.getPool().get(hhID)[HholdAttribs.hhType.getIndex()]);
			
			ArrayList<Integer> newResidents = new ArrayList<Integer>();
			
			for (Integer indivID : indivIDs) {
				int newIndivID = (int)indivID + crnIndivCountsInPopulation;
				int indivAge = IndividualPool.getPool().get(indivID)[IndivAttribs.age.getIndex()];
				Genders indivGender = Genders.getGenderByValue(IndividualPool.getPool().get(indivID)[IndivAttribs.gender.getIndex()]);
				HholdRelSP indivHhRel = HholdRelSP.getHholdRelSPByIndex(IndividualPool.getPool().get(indivID)[IndivAttribs.hhRel.getIndex()]);

				if (Population.getIndivPool().containsKey((Integer)newIndivID)) {
					System.out.println("Individual ID " + newIndivID + " already exists in Population.indivPool. New indiv ID constructed.");
					newIndivID = ArrayHandler.max(ArrayHandler.toInt(Population.getIndivPool().keySet())) + 1;
				}
				
				Population.getIndivPool().put((Integer)newIndivID, new Individual(newIndivID, indivAge, indivGender, indivHhRel));
				
				newResidents.add((Integer)newIndivID);
			}
			
			int newHholdID = (int)hhID + crnHholdCountsInPopulation;
			if (Population.getHhPool().containsKey((Integer)newHholdID)) {
				System.out.println("Household ID " + newHholdID + " already exists in Population.hhPool. New hhold ID constructed.");
				newHholdID = ArrayHandler.max(ArrayHandler.toInt(Population.getHhPool().keySet())) + 1;
			}
			Population.getHhPool().put((Integer)newHholdID, new Household(newHholdID, hhType, newResidents, zoneName, HardcodedData.getZonesNameDescription().get(zoneName)));
		}
		
	}
	
	
	/**
	 * 
	 * @param indivDB
	 * @param hholdDB
	 * @param zoneName
	 */
	public static void constructZonalSP(String zoneName) {
		
		HardcodedData.setOutputTablesPath(zoneName);
		CensusTables.outputSPlikeCensusTables();
		
		// creates indivPool and hholdPool
		makePools(); 
		System.out.println("\t\t ...makePools done! " + getTimeStamp());
		
		// allocates individuals into households
		allocateMinIndivsToHholds();
		System.out.println("\t\t ...allocateMinIndivsToHholds done! " + getTimeStamp());
		
//		System.out.println("\n\nRemaining individuals...");
//		SPOutputHandler.dispRemainingIndivs(IndividualPool.getAvailU15MalesID(), "U15MalesID");
//		SPOutputHandler.dispRemainingIndivs(IndividualPool.getAvailU15FemalesID(), "U15FemalesID");
//		SPOutputHandler.dispRemainingIndivs(IndividualPool.getAvailStuMalesID(), "StuMalesID");
//		SPOutputHandler.dispRemainingIndivs(IndividualPool.getAvailStuFemalesID(), "StuFemalesID");
//		SPOutputHandler.dispRemainingIndivs(IndividualPool.getAvailO15MalesID(), "O15MalesID");
//		SPOutputHandler.dispRemainingIndivs(IndividualPool.getAvailO15FemalesID(), "O15FemalesID");
//		SPOutputHandler.dispRemainingIndivs(IndividualPool.getAvailRelMalesID(), "RelMalesID");
//		SPOutputHandler.dispRemainingIndivs(IndividualPool.getAvailRelFemalesID(), "RelFemalesID");
		
		HashMap<Integer, Double> rmsHhTypeFemale = new HashMap<Integer, Double>(); // <count of female allocated, rmsHhTypeFemale>
		HashMap<Integer, Double> rmsHhTypeMale = new HashMap<Integer, Double>(); // <count of male allocated, rmsHhTypeFemale>
		HashMap<Integer, Double> rmsHhSize = new HashMap<Integer, Double>();
		HashMap<Integer,Double> rmsHhTypeIndiv = new HashMap<Integer,Double>();
		
		HashMap<Integer,Double> rmsHhTypeFemaleCount = new HashMap<Integer, Double>();
		HashMap<Integer,Double> rmsHhTypeMaleCount = new HashMap<Integer, Double>();
		HashMap<Integer,Double> rmsHhSizeCount = new HashMap<Integer, Double>(); 
		HashMap<Integer,Double> rmsHhTypeIndivCount = new HashMap<Integer, Double>();
		
		allocateRemainingChildrenToFamilyHholds(rmsHhTypeFemale, rmsHhTypeMale, rmsHhSize, rmsHhTypeIndiv,
				rmsHhTypeFemaleCount, rmsHhTypeMaleCount, rmsHhSizeCount, rmsHhTypeIndivCount);
		System.out.println("\t\t ...allocateRemainingChildrenToFamilyHholds done! " + getTimeStamp());
		
		allocateRemainingRelativeToFamilyHholds(rmsHhTypeFemale, rmsHhTypeMale, rmsHhSize, rmsHhTypeIndiv,
				rmsHhTypeFemaleCount, rmsHhTypeMaleCount, rmsHhSizeCount, rmsHhTypeIndivCount);
		System.out.println("\t\t ...allocateRemainingRelativeToFamilyHholds done! " + getTimeStamp());
		
		TextFileHandler.writeToCSVIntegerDouble(HardcodedData.zonalOutputPath + "rmsHhTypeFemale.csv", null, rmsHhTypeFemale, false);
		TextFileHandler.writeToCSVIntegerDouble(HardcodedData.zonalOutputPath + "rmsHhTypeMale.csv", null, rmsHhTypeMale, false);
		TextFileHandler.writeToCSVIntegerDouble(HardcodedData.zonalOutputPath + "rmsHhSize.csv", null, rmsHhSize, false);
		TextFileHandler.writeToCSVIntegerDouble(HardcodedData.zonalOutputPath + "rmsHhTypeIndiv.csv", null, rmsHhTypeIndiv, false);
		
		TextFileHandler.writeToCSVIntegerDouble(HardcodedData.zonalOutputPath + "rmsHhTypeFemaleCount.csv", null, rmsHhTypeFemaleCount, false);
		TextFileHandler.writeToCSVIntegerDouble(HardcodedData.zonalOutputPath + "rmsHhTypeMaleCount.csv", null, rmsHhTypeMaleCount, false);
		TextFileHandler.writeToCSVIntegerDouble(HardcodedData.zonalOutputPath + "rmsHhSizeCount.csv", null, rmsHhSizeCount, false);
		TextFileHandler.writeToCSVIntegerDouble(HardcodedData.zonalOutputPath + "rmsHhTypeIndivCount.csv", null, rmsHhTypeIndivCount, false);
		
		ZonalSPOutputHandler.outputZonalSPAnalyserTables();
		System.out.println("\t\t ...outputZonalSPAnalyserTables() done! " + getTimeStamp());
		
	}
	
	
	/**
	 * 
	 */
	private static void allocateMinIndivsToHholds() {
		// allocates the required number of parents to households
		HashMap<Integer,int[]> hhIDParentsIDMap = new HashMap<Integer,int[]>();
		hhIDParentsIDMap = assignParentsToHholds(hhIDParentsIDMap);
		System.out.println("\t\t\t ...assignParentsToHholds done! " + getTimeStamp());
		
		// sorts households in hhIDParentsIDMap by ascending youngest parent age
		int[] minAgeArray = new int[hhIDParentsIDMap.keySet().size()];
		int[] hhIDArray = new int[hhIDParentsIDMap.keySet().size()];
		int hhCount = -1;
		for (Integer hhID : hhIDParentsIDMap.keySet()) {
			hhCount += 1;
			minAgeArray[hhCount] = IndividualPool.getAgeYoungerParent(hhIDParentsIDMap.get(hhID));
			hhIDArray[hhCount] = hhID;
		}
		int[] sortedIndices = null;
		sortedIndices = ArrayHandler.sortedIndices(minAgeArray);
		
		// assigning minimum required children to households in hhIDPArentsIDMap 
		// starting with households having eldest minAgeParent
		for (int i=sortedIndices.length-1; i>=0; i--) {
			int hhID = hhIDArray[sortedIndices[i]];
			int minAgeParent = minAgeArray[sortedIndices[i]];
			//int[] parentsID = hhIDParentsIDMap.get(hhID);
			
			int[] hhAttribs = HouseholdPool.getPool().get(hhID);
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(hhAttribs[HholdAttribs.hhType.getIndex()]);
			int minU15 = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_u15.getIndex()];
			int minStu = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_students.getIndex()];
			int minO15 = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_o15.getIndex()];
			
			if (minU15>=1) {
				assignU15Child(hhID, minAgeParent, hhType);
			}
			
			if (minStu>=1) {
				assignStuChild(hhID, minAgeParent, hhType);
			}
			
			if (minO15>=1) {
				assignO15Child(hhID, minAgeParent, hhType);
			}
		}
		System.out.println("\t\t\t ...assignMinChildrenToHholds done! " + getTimeStamp());
		
		assignRelativesToHholds();
		System.out.println("\t\t\t ...assignRelativesToHholds done! " + getTimeStamp());
		
		assignIndivsToNFHholds();
		System.out.println("\t\t\t ...assignIndivsToNFHholds done! " + getTimeStamp());
	}
	
	
	/**
	 * 
	 */
	private static void allocateRemainingRelativeToFamilyHholds(
			HashMap<Integer, Double> rmsHhTypeFemale, HashMap<Integer, Double> rmsHhTypeMale, HashMap<Integer, Double> rmsHhSize, HashMap<Integer,Double> rmsHhTypeIndiv,
			HashMap<Integer,Double> rmsHhTypeFemaleCount, HashMap<Integer,Double> rmsHhTypeMaleCount, HashMap<Integer,Double> rmsHhSizeCount, HashMap<Integer,Double> rmsHhTypeIndivCount) {
		
		int countIndivAlloc = 0;
		int[] countChildAllocArray = ArrayHandler.toInt(rmsHhSize.keySet());
		if (ArrayHandler.getLength(countChildAllocArray)>0)
			countIndivAlloc = ArrayHandler.max(countChildAllocArray);
				
		int countMaleAlloc = 0;
		int[] countMaleAllocArray = ArrayHandler.toInt(rmsHhTypeMale.keySet());
		if (ArrayHandler.getLength(countMaleAllocArray)>0)
			countMaleAlloc = ArrayHandler.max(countMaleAllocArray);
		
		int countFemaleAlloc = 0;
		int[] countFemaleAllocArray = ArrayHandler.toInt(rmsHhTypeFemale.keySet());
		if (ArrayHandler.getLength(countFemaleAllocArray)>0)
			countFemaleAlloc = ArrayHandler.max(countFemaleAllocArray);
		
		/*
		 * prepares arrays from census tables for the calculations of RMS during allocting individuals into households.
		 */
		int[] censusHFBySize = CensusTables.getSpLikeB30HF();
		int[] censusFemaleByHFs = SPAnalyser.extractValuesForFamilyHholds(CensusTables.getSpLikeB25Female());
		int[] censusMaleByHFs = SPAnalyser.extractValuesForFamilyHholds(CensusTables.getSpLikeB25Male());
		int[] censusPeopleByHFs = new int[censusMaleByHFs.length];
		for (int i=0; i<=censusPeopleByHFs.length-1; i++) {
			censusPeopleByHFs[i] = censusFemaleByHFs[i] + censusMaleByHFs[i];
		}
		// gets the percentage of individual counts of both genders for each family household type from census 
		double[] censusPercentIndivsByHhType = CensusTablesAnalyser.getPercentIndivsByHhTypes();
		/*
		 * ends preparing arrays
		 */
		
		while ((IndividualPool.getAvailRelMalesID()!=null && IndividualPool.getAvailRelMalesID().length>0) ||
				(IndividualPool.getAvailRelFemalesID()!=null && IndividualPool.getAvailRelFemalesID().length>0)) {
			
			HashMap<Integer,String> hhTypeSizeGenderMap = new HashMap<Integer,String>();
			HashMap<Integer,double[]> rmsDataPointsMap = new HashMap<Integer,double[]>();
			HashMap<Integer,ArrayList<Integer[]>> indivHholdIDsMap = new HashMap<Integer,ArrayList<Integer[]>>();
			
			constructMapsOfDataPoints(IndividualPool.getAvailRelMalesID(), HholdRelSP.Relative, hhTypeSizeGenderMap, rmsDataPointsMap, indivHholdIDsMap);
			constructMapsOfDataPoints(IndividualPool.getAvailRelFemalesID(), HholdRelSP.Relative, hhTypeSizeGenderMap, rmsDataPointsMap, indivHholdIDsMap);
			
			if (rmsDataPointsMap.size()==0) break;
			
			// prepares data for ArrayHandler.getParetoFront(double[][])
			double[][] dataPoints = new double[rmsDataPointsMap.size()][2];
			for (Integer caseKey : rmsDataPointsMap.keySet()) {
				dataPoints[(int)caseKey][0] = rmsDataPointsMap.get(caseKey)[0];
				dataPoints[(int)caseKey][1] = rmsDataPointsMap.get(caseKey)[1];
			}
			// extracts the indices corresponding to data points along the pareto front
			int[] paretoFrontIndices = ArrayHandler.getParetoFrontIndices(dataPoints);
			
			// notes that these indices are sorted in the ascending order dataPoints[i][0] and descending order of dataPoints[i][1]
			// picks the point in the middle of this front
			int pickedIndex = HardcodedData.random.nextInt(paretoFrontIndices.length);
			//int pickedIndex = paretoFrontIndices.length/(int)2;
			Integer pickedCaseKey = paretoFrontIndices[pickedIndex];
			
			// gets the list of individual ID - household ID pairs corresponding to this case key
			ArrayList<Integer[]> selectedIndivHholdPairs = indivHholdIDsMap.get(pickedCaseKey);
			
			// randomly picks a pair out of selectedIndivHholdPairs
			int randomIndex = HardcodedData.random.nextInt(selectedIndivHholdPairs.size());
			Integer[] selectedPair = selectedIndivHholdPairs.get(randomIndex);
			double[] rmsValues = rmsDataPointsMap.get(pickedCaseKey);
			
			Integer indivID = selectedPair[0];
			Integer hhID = selectedPair[1];
			
			if (indivID<0)
				System.out.println("indivID<0 in SPConstructor.allocateRemainingRelativeToFamilyHholds.");
			
			HouseholdPool.addResidentsToHousehold((int)hhID, (int)indivID);
			
			if (IndividualPool.getPool().get((Integer)indivID)[IndivAttribs.gender.getIndex()]==Genders._male.getValue()) { // removes from IndividualPool.getAvailRelMalesID()
				IndividualPool.setAvailRelMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailRelMalesID(), (int)indivID));
			} else { // removes from IndividualPool.getAvailRelFemalesID()
				IndividualPool.setAvailRelFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailRelFemalesID(), (int)indivID));
			}
			
			
			/*
			 * prepares output for testing
			 */
			int[] allocHFBySize = SPAnalyser.countHFAllocByHholdSize();
			int[] allocFemaleByHFs = SPAnalyser.extractValuesForFamilyHholds(SPAnalyser.countIndivAllocByHhType(Genders._female));
			int[] allocMaleByHFs = SPAnalyser.extractValuesForFamilyHholds(SPAnalyser.countIndivAllocByHhType(Genders._male));
			int[] allocPeopleByHFs = new int[allocMaleByHFs.length];
			for (int i=0; i<=allocPeopleByHFs.length-1; i++) {
				allocPeopleByHFs[i] = allocMaleByHFs[i] + allocFemaleByHFs[i];
			}
			
			// stores RMS of proportion of HF counts by size
			countIndivAlloc += 1;
			rmsHhSize.put((Integer)countIndivAlloc, rmsValues[1]);
			// and stores RMS of HF counts by size
			double rmsHFCounts = ArrayHandler.calculateRMS(censusHFBySize, allocHFBySize);
			rmsHhSizeCount.put((Integer)countIndivAlloc, rmsHFCounts);
			
			// stores RMS of proportion of people counts (both genders) by HF type.
			double rmsPeopleByHhType = ArrayHandler.calculateRMS(censusPercentIndivsByHhType, ArrayHandler.normaliseArray(allocPeopleByHFs));
			rmsHhTypeIndiv.put((Integer)countIndivAlloc,rmsPeopleByHhType);
			// and stores RMS of people counts (both genders) by HF type.
			double rmsPeopleCounts = ArrayHandler.calculateRMS(censusPeopleByHFs, allocPeopleByHFs);
			rmsHhTypeIndivCount.put((Integer)countIndivAlloc,rmsPeopleCounts);
			
			// stores RMS of proportion of male and female by HF type.
			// and stores RMS of count of male and female by HF type.
			if (IndividualPool.getPool().get(indivID)[IndivAttribs.gender.getIndex()]==Genders._female.getValue()) {
				countFemaleAlloc += 1;
				rmsHhTypeFemale.put((Integer)countFemaleAlloc, rmsValues[0]);
				double rmsFemaleCounts = ArrayHandler.calculateRMS(censusFemaleByHFs, allocFemaleByHFs);
				rmsHhTypeFemaleCount.put((Integer)countFemaleAlloc,rmsFemaleCounts);
			} else if (IndividualPool.getPool().get(indivID)[IndivAttribs.gender.getIndex()]==Genders._male.getValue()) {
				countMaleAlloc += 1;
				rmsHhTypeMale.put((Integer)countMaleAlloc, rmsValues[0]);
				double rmsMaleCounts = ArrayHandler.calculateRMS(censusMaleByHFs, allocMaleByHFs);
				rmsHhTypeMaleCount.put((Integer)countMaleAlloc, rmsMaleCounts);
			}
			/*
			 * ends preparing output for testing
			 */
			
		}
		
		
	}
	
	
	/**
	 * for each individual of types U15Child, O15Child, Student, Relative, male or female, 
	 * determines possible households he/she can be allocated to (based on the individual's gender, age, relationship and the household's type and age of the younger parent.)
	 * calculates RMS of nMale (of nFemale) by hhType and RMS of number households for each household size.
	 */
	private static void allocateRemainingChildrenToFamilyHholds(
			HashMap<Integer, Double> rmsHhTypeFemale, HashMap<Integer, Double> rmsHhTypeMale, HashMap<Integer, Double> rmsHhSize, HashMap<Integer,Double> rmsHhTypeIndiv, 
			HashMap<Integer,Double> rmsHhTypeFemaleCount, HashMap<Integer,Double> rmsHhTypeMaleCount, HashMap<Integer,Double> rmsHhSizeCount, HashMap<Integer,Double> rmsHhTypeIndivCount) {
		
		int countChildAlloc = 0;
		int[] countChildAllocArray = ArrayHandler.toInt(rmsHhSize.keySet());
		if (ArrayHandler.getLength(countChildAllocArray)>0)
			countChildAlloc = ArrayHandler.max(countChildAllocArray);
		
		int countMaleAlloc = 0;
		int[] countMaleAllocArray = ArrayHandler.toInt(rmsHhTypeMale.keySet());
		if (ArrayHandler.getLength(countMaleAllocArray)>0)
			countMaleAlloc = ArrayHandler.max(countMaleAllocArray);
		
		int countFemaleAlloc = 0;
		int[] countFemaleAllocArray = ArrayHandler.toInt(rmsHhTypeFemale.keySet());
		if (ArrayHandler.getLength(countFemaleAllocArray)>0)
			countFemaleAlloc = ArrayHandler.max(countFemaleAllocArray);
		
		int sumChildRem = ArrayHandler.getLength(IndividualPool.getAvailU15MalesID()) + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()) + 
				ArrayHandler.getLength(IndividualPool.getAvailStuMalesID()) + ArrayHandler.getLength(IndividualPool.getAvailStuFemalesID()) +
				ArrayHandler.getLength(IndividualPool.getAvailO15MalesID()) + ArrayHandler.getLength(IndividualPool.getAvailO15FemalesID());
		System.out.println("\t\t start allocating " + sumChildRem + " remaining children to HFs... " + getTimeStamp());
		
		
		/*
		 * prepares arrays from census tables for the calculations of RMS during allocting individuals into households.
		 */
		int[] censusHFBySize = CensusTables.getSpLikeB30HF();
		int[] censusFemaleByHFs = SPAnalyser.extractValuesForFamilyHholds(CensusTables.getSpLikeB25Female());
		int[] censusMaleByHFs = SPAnalyser.extractValuesForFamilyHholds(CensusTables.getSpLikeB25Male());
		int[] censusPeopleByHFs = new int[censusMaleByHFs.length];
		for (int i=0; i<=censusPeopleByHFs.length-1; i++) {
			censusPeopleByHFs[i] = censusFemaleByHFs[i] + censusMaleByHFs[i];
		}
		
		// gets the percentage of individual counts of both genders for each family household type from census 
		double[] censusPercentIndivsByHhType = CensusTablesAnalyser.getPercentIndivsByHhTypes();
		
		/*
		 * ends preparing arrays
		 */
		
		while ((IndividualPool.getAvailU15MalesID()!=null && IndividualPool.getAvailU15MalesID().length>0) ||
				(IndividualPool.getAvailU15FemalesID()!=null && IndividualPool.getAvailU15FemalesID().length>0) ||
				(IndividualPool.getAvailStuMalesID()!=null && IndividualPool.getAvailStuMalesID().length>0) ||
				(IndividualPool.getAvailStuFemalesID()!=null && IndividualPool.getAvailStuFemalesID().length>0) ||
				(IndividualPool.getAvailO15MalesID()!=null && IndividualPool.getAvailO15MalesID().length>0) ||
				(IndividualPool.getAvailO15FemalesID()!=null && IndividualPool.getAvailO15FemalesID().length>0)) {
			
			Integer childID = -1;
			Integer hhID = -1;
			int minParentAge = -1;
			
			HashMap<Integer,String> hhTypeSizeGenderMap = new HashMap<Integer,String>();
			HashMap<Integer,double[]> rmsDataPointsMap = new HashMap<Integer,double[]>();
			HashMap<Integer,ArrayList<Integer[]>> indivHholdIDsMap = new HashMap<Integer,ArrayList<Integer[]>>();
			
			
			// constructs pairs of data points, 
			// each pair corresponds to RMSHhType and RMSHhsize as a result of a possible allocation of an individual to a household. 
			constructMapsOfDataPoints(IndividualPool.getAvailU15MalesID(), HholdRelSP.U15Child, hhTypeSizeGenderMap, rmsDataPointsMap, indivHholdIDsMap);
			constructMapsOfDataPoints(IndividualPool.getAvailU15FemalesID(), HholdRelSP.U15Child, hhTypeSizeGenderMap, rmsDataPointsMap, indivHholdIDsMap);
			constructMapsOfDataPoints(IndividualPool.getAvailStuMalesID(), HholdRelSP.Student, hhTypeSizeGenderMap, rmsDataPointsMap, indivHholdIDsMap);
			constructMapsOfDataPoints(IndividualPool.getAvailStuFemalesID(), HholdRelSP.Student, hhTypeSizeGenderMap, rmsDataPointsMap, indivHholdIDsMap);
			constructMapsOfDataPoints(IndividualPool.getAvailO15MalesID(), HholdRelSP.O15Child, hhTypeSizeGenderMap, rmsDataPointsMap, indivHholdIDsMap);
			constructMapsOfDataPoints(IndividualPool.getAvailO15FemalesID(), HholdRelSP.O15Child, hhTypeSizeGenderMap, rmsDataPointsMap, indivHholdIDsMap);
//			System.out.println("\t\t\t ...(" + countChildAlloc + ") finish constructMapsOfDataPoints! " + getTimeStamp());
			
			if (rmsDataPointsMap.size()==0) break;
			
			// prepares data for ArrayHandler.getParetoFront(double[][])
			double[][] dataPoints = new double[rmsDataPointsMap.size()][2];
			for (Integer caseKey : rmsDataPointsMap.keySet()) {
				dataPoints[(int)caseKey][0] = rmsDataPointsMap.get(caseKey)[0];
				dataPoints[(int)caseKey][1] = rmsDataPointsMap.get(caseKey)[1];
			}

			// extracts the indices corresponding to data points along the pareto front
			int[] paretoFrontIndices = ArrayHandler.getParetoFrontIndices(dataPoints);
//			System.out.println("\t\t\t ...(" + countChildAlloc + ") finish getParetoFrontIndices! " + getTimeStamp());
			
			// notes that these indices are sorted in the ascending order dataPoints[i][0] and descending order of dataPoints[i][1]
			// (randomly) picks a point from the pareto front that has both RMS values 
			int pickedIndex = HardcodedData.random.nextInt(paretoFrontIndices.length);
			// int pickedIndex = paretoFrontIndices.length/(int)2;
			Integer pickedCaseKey = (Integer)paretoFrontIndices[pickedIndex];
			
			// gets the list of individual ID - household ID pairs corresponding to this case key
			ArrayList<Integer[]> selectedIndivHholdPairs = indivHholdIDsMap.get(pickedCaseKey);
			double[] rmsValues = rmsDataPointsMap.get(pickedCaseKey);
//			System.out.println("\t\t\t ...(" + countChildAlloc + ") finish get list indivID-hhID! " + getTimeStamp());
			
			// out of these pairs, pick the first one that satisfies the conditions between child age and younger parent age
			boolean foundGoodPair = false;
			int[] dAgeAfterCorrections = new int[selectedIndivHholdPairs.size()];
			for (int iPair=0; iPair<=selectedIndivHholdPairs.size()-1; iPair++) {
				childID = selectedIndivHholdPairs.get(iPair)[0];
				hhID = selectedIndivHholdPairs.get(iPair)[1];
				int childAge = IndividualPool.getPool().get(childID)[IndivAttribs.age.getIndex()];
				minParentAge = IndividualPool.getAgeYoungerParent((int)hhID);
				
				if (IndividualPool.isChildAgeAcceptable(childAge, minParentAge)) {
					putOneRemainingChildToHhold((int)hhID, (int)childID);
					foundGoodPair = true;
					break;
				} else {
					dAgeAfterCorrections[iPair] = (int)IndividualPool.correctAgeAndCalculateDiffToChildAgeBound(childAge, minParentAge);
				}
			}
//			System.out.println("\t\t\t ...(" + countChildAlloc + ") finish picking first pair! " + getTimeStamp());
			
			// if there are no pairs satisfying the conditions between child age and younger parent age
			if (!foundGoodPair) {
				// picks the pair that corresponds to the smallest value in dAgeAfterCorrections
				int indexOfMin = ArrayHandler.getIndexOfMin(dAgeAfterCorrections);
				childID = selectedIndivHholdPairs.get(indexOfMin)[0];
				hhID = selectedIndivHholdPairs.get(indexOfMin)[1];
				
				minParentAge = IndividualPool.getAgeYoungerParent((int)hhID);
				IndividualPool.correctChildAgeToBestMatchParents((int)childID, minParentAge);
				
				putOneRemainingChildToHhold((int)hhID, (int)childID);
				
//				System.out.println("\t\t\t ...(" + countChildAlloc + ") finish !foundGoodPair! " + getTimeStamp());
			}
			
			/*
			 * prepares output for testing
			 */
			int[] allocHFBySize = SPAnalyser.countHFAllocByHholdSize();
			int[] allocFemaleByHFs = SPAnalyser.extractValuesForFamilyHholds(SPAnalyser.countIndivAllocByHhType(Genders._female));
			int[] allocMaleByHFs = SPAnalyser.extractValuesForFamilyHholds(SPAnalyser.countIndivAllocByHhType(Genders._male));
			int[] allocPeopleByHFs = new int[allocMaleByHFs.length];
			for (int i=0; i<=allocPeopleByHFs.length-1; i++) {
				allocPeopleByHFs[i] = allocMaleByHFs[i] + allocFemaleByHFs[i];
			}
			double[] normAllocPeopleByHFs = ArrayHandler.normaliseArray(allocPeopleByHFs);
			
			
			// stores RMS of proportion of HF counts by size
			countChildAlloc += 1;
			rmsHhSize.put((Integer)countChildAlloc, rmsValues[1]);
			// and stores RMS of HF counts by size
			double rmsHFCounts = ArrayHandler.calculateRMS(censusHFBySize, allocHFBySize);
			rmsHhSizeCount.put((Integer)countChildAlloc, rmsHFCounts);
			
			// stores RMS of proportion of people counts (both genders) by HF type.
			double rmsPeopleByHhType = ArrayHandler.calculateRMS(censusPercentIndivsByHhType, normAllocPeopleByHFs);
			rmsHhTypeIndiv.put((Integer)countChildAlloc,rmsPeopleByHhType);
			// and stores RMS of people counts (both genders) by HF type.
			double rmsPeopleCounts = ArrayHandler.calculateRMS(censusPeopleByHFs, allocPeopleByHFs);
			rmsHhTypeIndivCount.put((Integer)countChildAlloc,rmsPeopleCounts);
			
			// stores RMS of proportion of male and female by HF type.
			// and stores RMS of count of male and female by HF type.
			if (IndividualPool.getPool().get(childID)[IndivAttribs.gender.getIndex()]==Genders._female.getValue()) {
				countFemaleAlloc += 1;
				rmsHhTypeFemale.put((Integer)countFemaleAlloc, rmsValues[0]);
				double rmsFemaleCounts = ArrayHandler.calculateRMS(censusFemaleByHFs, allocFemaleByHFs);
				rmsHhTypeFemaleCount.put((Integer)countFemaleAlloc,rmsFemaleCounts);
			} else if (IndividualPool.getPool().get(childID)[IndivAttribs.gender.getIndex()]==Genders._male.getValue()) {
				countMaleAlloc += 1;
				rmsHhTypeMale.put((Integer)countMaleAlloc, rmsValues[0]);
				double rmsMaleCounts = ArrayHandler.calculateRMS(censusMaleByHFs, allocMaleByHFs);
				rmsHhTypeMaleCount.put((Integer)countMaleAlloc, rmsMaleCounts);
			}
			/*
			 * ends preparing output for testing
			 */
			
			if ((countChildAlloc%50)==0) {
				System.out.println("\t\t\t ..." + countChildAlloc + " children allocated! " + getTimeStamp());
			}
		}
	}
	
	
	
	/**
	 * 
	 * @param hhID
	 * @param childID
	 */
	private static void putOneRemainingChildToHhold(int hhID, int childID) {
		if (childID<0)
			System.out.println("childID<0 in SPCOnstructor.putOneRemainingChildToHhold");
		HouseholdPool.addResidentsToHousehold((int)hhID, (int)childID);
		
		// if this child is U15Child 
		if (IndividualPool.getPool().get((Integer)childID)[IndivAttribs.hhRel.getIndex()]==HholdRelSP.U15Child.getIndex()) { 
			if (IndividualPool.getPool().get((Integer)childID)[IndivAttribs.gender.getIndex()]==Genders._male.getValue()) { // removes from IndividualPool.getAvailU15MalesID()
				IndividualPool.setAvailU15MalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailU15MalesID(), childID));
			} else { // removes from IndividualPool.getAvailU15FemalesID()
				IndividualPool.setAvailU15FemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailU15FemalesID(), childID));
			}
		}  
		// if this child is Student
		else if (IndividualPool.getPool().get((Integer)childID)[IndivAttribs.hhRel.getIndex()]==HholdRelSP.Student.getIndex()) {
			if (IndividualPool.getPool().get((Integer)childID)[IndivAttribs.gender.getIndex()]==Genders._male.getValue()) { // removes from IndividualPool.getAvailStuMalesID()
				IndividualPool.setAvailStuMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailStuMalesID(), childID));
			} else { // removes from IndividualPool.getAvailStuFemalesID()
				IndividualPool.setAvailStuFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailStuFemalesID(), childID));
			}
		}
		// if this child is O15Child
		else if (IndividualPool.getPool().get((Integer)childID)[IndivAttribs.hhRel.getIndex()]==HholdRelSP.O15Child.getIndex()) {
			if (IndividualPool.getPool().get((Integer)childID)[IndivAttribs.gender.getIndex()]==Genders._male.getValue()) { // removes from IndividualPool.getAvailO15MalesID()
				IndividualPool.setAvailO15MalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailO15MalesID(), childID));
			} else { // removes from IndividualPool.getAvailO15FemalesID()
				IndividualPool.setAvailO15FemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailO15FemalesID(), childID));
			}
		}
	}
	
	/**
	 * 
	 * @param indivIDList
	 * @param hhTypeSizeGenderMap
	 * @param rmsDataPointsMap
	 * @param indivHholdIDsMap
	 */
	private static void constructMapsOfDataPoints(int[] indivIDList, HholdRelSP hhRel,
			HashMap<Integer,String> hhTypeSizeGenderMap, HashMap<Integer,double[]> rmsDataPointsMap, HashMap<Integer,ArrayList<Integer[]>> indivHholdIDsMap) {

		if (indivIDList==null || indivIDList.length==0)
			return;
		
		for (int indivID : indivIDList) {
			int[] indivAttribs = IndividualPool.getPool().get((Integer)indivID);
			Genders indivGender = Genders.getGenderByValue(indivAttribs[IndivAttribs.gender.getIndex()]);

			for (Integer hhID : HouseholdPool.getPool().keySet()) {
				int[] hhAttribs = HouseholdPool.getPool().get(hhID);
				HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(hhAttribs[HholdAttribs.hhType.getIndex()]);
				
				int minIndiv = -1;
				if (hhRel.equals(HholdRelSP.U15Child)) {
					minIndiv = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_u15.getIndex()];
				} else if (hhRel.equals(HholdRelSP.Student)) {
					minIndiv = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_students.getIndex()];
				} else if (hhRel.equals(HholdRelSP.O15Child)) {
					minIndiv = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_o15.getIndex()];
				} else if (hhRel.equals(HholdRelSP.Relative)) {
					minIndiv = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_relatives.getIndex()];
				}
				
				// if this household must not have any U15Child individuals, move on to the next household
				if (minIndiv<0) 
					continue;
				
				// gets the size of the current household
				int hhSize = HouseholdPool.getHholdsAllocated().get((Integer)hhID).size();
				// if this individual is to be added to this household, the actual size will be hhSize + 1
				int newHhSize = hhSize + 1;
				
				String strHhTypeSizeGender = hhType.toString() + "_" + Integer.toString(newHhSize) + "_" + indivGender.toString();
				
				if (hhTypeSizeGenderMap.containsValue(strHhTypeSizeGender)) {
					Integer caseKey = -1;
					for (Integer tmpKey : hhTypeSizeGenderMap.keySet()) {
						if (hhTypeSizeGenderMap.get(tmpKey).equals(strHhTypeSizeGender)) {
							caseKey = tmpKey;
							break;
						}
					}
					ArrayList<Integer[]> indHholdIDs = new ArrayList<Integer[]>();
					if (indivHholdIDsMap.containsKey(caseKey)) {
						indHholdIDs = indivHholdIDsMap.get(caseKey);
					}
					indHholdIDs.add(new Integer[] {(Integer)indivID, hhID});
					indivHholdIDsMap.put(caseKey,indHholdIDs);
					
					continue;
				}

				Integer caseCount=0;
				if (hhTypeSizeGenderMap.size()!=0) {
					caseCount = (Integer)(ArrayHandler.max(ArrayHandler.toInt(hhTypeSizeGenderMap.keySet())) + 1);
				}
				hhTypeSizeGenderMap.put(caseCount, strHhTypeSizeGender);
				
				// increases the current gender count of the given hhType by 1 and 
				// calculates the new rms compared to census distribution of this gender count by household type.
				double rmsHHType = SPAnalyser.calculateRMSIndivCountsByHhTypePlus1(hhType, indivGender);
				// calculates the RMS between the new distribution of HF counts by size and the distribution in census
				double rmsHHSize = SPAnalyser.calculateRMSHholdCountsBySizePlus1(newHhSize);
				
				rmsDataPointsMap.put(caseCount, new double[] {rmsHHType, rmsHHSize});
				
				ArrayList<Integer[]> indHholdIDs = new ArrayList<Integer[]>();
				if (indivHholdIDsMap.containsKey(caseCount)) {
					indHholdIDs = indivHholdIDsMap.get(caseCount);
				}
				indHholdIDs.add(new Integer[] {(Integer)indivID, hhID});
				indivHholdIDsMap.put(caseCount,indHholdIDs);
			}
		}
	}
	
	
	/**
	 * 
	 */
	private static void assignIndivsToNFHholds() {
		Iterator<Entry<Integer, int[]>> itMap = HouseholdPool.getPool().entrySet().iterator();
		
		ArrayList<Integer> nf1List = new ArrayList<Integer>();
		ArrayList<Integer> nfxList = new ArrayList<Integer>();
		
		//int nNF1 = CensusTables.getHhComp()[B30Rows.one.getIndex()][B30Cols.nonFamilyHholds.getIndex()];
		int nNF1 = CensusTables.getSpLikeB30NF()[B30Rows.one.getIndex()];
		
		while (itMap.hasNext()) {
			Entry<Integer, int[]> hhEntry = itMap.next();
			int hhID = hhEntry.getKey();
			int[] hhAttribs = hhEntry.getValue();
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(hhAttribs[HholdAttribs.hhType.getIndex()]);
			int minNFpp = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_non_family.getIndex()];
			
			if (minNFpp>0) {
				if (nf1List.size()<nNF1)
					nf1List.add(hhID);
				else {
					nfxList.add(hhID);
				}
			}
		}
		
		putLonePersonIntoNF(nf1List);
		// removes any remaining LonePerson individuals
		IndividualPool.setAvailLonePersonID(IndividualPool.removesIndivsFromIndivPool(IndividualPool.getAvailLonePersonID()));
		
		putGrHholdIndivIntoNF(nfxList);
		// removes any remaining GrHhold individuals
		IndividualPool.setAvailGrHholdID(IndividualPool.removesIndivsFromIndivPool(IndividualPool.getAvailGrHholdID()));
	}
	
	
	/**
	 * 
	 * @param nf1List
	 */
	private static void putLonePersonIntoNF(ArrayList<Integer> nf1List) {
		// assigns LonePerson individuals to NF households having 1 resident
		for (Integer nf1ID : nf1List) {
			int lonePersonID = -1;

			if (IndividualPool.getAvailLonePersonID()!=null && IndividualPool.getAvailLonePersonID().length!=0) {
				// picks randomly a LonePerson individual, either male or female
				lonePersonID = ArrayHandler.pickRandomFromArray(IndividualPool.getAvailLonePersonID(), null, 1, HardcodedData.random)[0];
				IndividualPool.setAvailLonePersonID(ArrayHandler.removeValueInArray(IndividualPool.getAvailLonePersonID(), lonePersonID));
			} else {
				lonePersonID = IndividualPool.constructOneNewIndividual(HholdRelSP.LonePerson, AgeGroups._15_24.getMinAge(), AgeGroups._85_99.getMaxAge());
			}

			// adds this individual to this household
			if (lonePersonID<0) 
				System.out.println("lonePersonID<0 in SPConstructor.putLonePersonIntoNF");
			HouseholdPool.addResidentsToHousehold(nf1ID, lonePersonID);
		}
	}
	
	
	/**
	 * 
	 * @param nfxList
	 */
	private static void putGrHholdIndivIntoNF(ArrayList<Integer> nfxList) {
		for (B30Rows b30Row : B30Rows.values()) {
			if (b30Row.equals(B30Rows.one)) continue;
			
			int nResidents = b30Row.getValue();
			//int nNFHholds = CensusTables.getHhComp()[b30Row.getIndex()][B30Cols.nonFamilyHholds.getIndex()];
			int nNFHholds = CensusTables.getSpLikeB30NF()[b30Row.getIndex()];
			
			for (int ihh=1; ihh<=nNFHholds; ihh++) {
				if (nfxList!=null && nfxList.size()!=0) {
					int hhID = nfxList.get(0);
					for (int ipp=1; ipp<=nResidents; ipp++) {
						int grHholdID = -1;
						
						if (IndividualPool.getAvailGrHholdID()!=null && IndividualPool.getAvailGrHholdID().length!=0) {
							// picks randomly a GroupHhold individual, regardless male or female
							grHholdID = ArrayHandler.pickRandomFromArray(IndividualPool.getAvailGrHholdID(), null, 1, HardcodedData.random)[0];
							IndividualPool.setAvailGrHholdID(ArrayHandler.removeValueInArray(IndividualPool.getAvailGrHholdID(), grHholdID));
						} else {
							// constructs a new GroupHhold individual
							grHholdID = IndividualPool.constructOneNewIndividual(HholdRelSP.GroupHhold, AgeGroups._15_24.getMinAge(), AgeGroups._85_99.getMaxAge());
						}
						
						// adds this individual to this household
						if (grHholdID<0)
							System.out.println("grHholdID<0 in SPConstructor.putGrHholdIndivIntoNF");
						HouseholdPool.addResidentsToHousehold(hhID, grHholdID);
					}
					nfxList.remove(0);
				}
			}
		}
		
	}
	
	
	/**
	 * 
	 */
	private static void assignRelativesToHholds() {
		Iterator<Entry<Integer, int[]>> itMap = HouseholdPool.getPool().entrySet().iterator();
		
		while (itMap.hasNext()) {
			Entry<Integer, int[]> hhEntry = itMap.next();
			int hhID = (int)hhEntry.getKey();
			int[] hhAttribs = hhEntry.getValue();
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(hhAttribs[HholdAttribs.hhType.getIndex()]);
			int minRel = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_relatives.getIndex()];
			
			if (minRel>=2) {
				for (int iRel=1; iRel<=minRel; iRel++) {
					int relIDPicked = -1;
					
					// if both Relative males and females are available
					if (IndividualPool.getAvailRelFemalesID()!=null && IndividualPool.getAvailRelFemalesID().length>0 &&
							IndividualPool.getAvailRelMalesID()!=null && IndividualPool.getAvailRelMalesID().length>0) {
						// determines if adding a relative male or a relative female best reserves the distribution of males and females of this household type.
						double[] percentGenders = CensusTablesAnalyser.getPercentGenderInHholdType(hhType);
						int[] femaleCountsByHhTypes = SPAnalyser.countIndivAllocByHhType(Genders._female);
						int[] maleCountsByHhTypes = SPAnalyser.countIndivAllocByHhType(Genders._male);
						int[] crnCountGenders = new int[] {femaleCountsByHhTypes[hhType.getIndex()], maleCountsByHhTypes[hhType.getIndex()]};
						
						int genderIdx = ArrayHandler.getIndexOfLowestRMS(percentGenders, crnCountGenders);
						// adding a female better reserves the distribution of males and females of that household type
						if (genderIdx==Genders._female.getValue()) {
							relIDPicked = ArrayHandler.pickRandomFromArray(IndividualPool.getAvailRelFemalesID(), null, 1, HardcodedData.random)[0];
							IndividualPool.setAvailRelFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailRelFemalesID(), relIDPicked));
						}
						// adding a male better reserves the distribution of males and females of that household type
						else if (genderIdx==Genders._male.getValue()) {
							relIDPicked = ArrayHandler.pickRandomFromArray(IndividualPool.getAvailRelMalesID(), null, 1, HardcodedData.random)[0];
							IndividualPool.setAvailRelMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailRelMalesID(), relIDPicked));
						}
					}
					// if only female Relative are available
					else if (IndividualPool.getAvailRelFemalesID()!=null && IndividualPool.getAvailRelFemalesID().length>0 &&
							(IndividualPool.getAvailRelMalesID()==null || IndividualPool.getAvailRelMalesID().length==0)) {
						relIDPicked = ArrayHandler.pickRandomFromArray(IndividualPool.getAvailRelFemalesID(), null, 1, HardcodedData.random)[0];
						IndividualPool.setAvailRelFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailRelFemalesID(), relIDPicked));
					}
					// if only male Relative are available
					else if ((IndividualPool.getAvailRelFemalesID()==null || IndividualPool.getAvailRelFemalesID().length==0) &&
							(IndividualPool.getAvailRelMalesID()!=null && IndividualPool.getAvailRelMalesID().length!=0)) {
						relIDPicked = ArrayHandler.pickRandomFromArray(IndividualPool.getAvailRelMalesID(), null, 1, HardcodedData.random)[0];
						IndividualPool.setAvailRelMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailRelMalesID(), relIDPicked));
					}
					
					// if for some reasons the above selection processes failed to pick a valid Relative
					if (relIDPicked==-1) {
						// creates a new Relative individual whose gender best reserves the distribution of males and females of that household type
						// the age of the new Relative also needs to best reserves the distribution of household relationship by age
						//relIDPicked = constructNewRelativeIndiv(hhType);
						relIDPicked = IndividualPool.constructOneNewIndividual(HholdRelSP.Relative, AgeGroups._15_24.getMinAge(), AgeGroups._85_99.getMaxAge());
					}
					
					// adds this individual to this household
					if (relIDPicked<0)
						System.out.println("relIDPicked<0 in SPConstructor.assignRelativesToHholds()");
					HouseholdPool.addResidentsToHousehold(hhID, relIDPicked);
				}
			}
		}
		
	}
	
	
	/**
	 * 
	 * @param hhID
	 * @param ageYoungerParent
	 * @param hhType
	 */
	private static void assignO15Child(int hhID, int ageYoungerParent, HholdTypes hhType) {
		int[] childAgeBounds = IndividualPool.calculateChildAgeBounds(ageYoungerParent);
		int minValidAge = childAgeBounds[0];
		int maxValidAge = childAgeBounds[1];
		
		int o15IDPicked = -1;
		
		// if both O15Child males and females are available 
		if (IndividualPool.getAvailO15FemalesID()!=null && IndividualPool.getAvailO15FemalesID().length>0 &&
				IndividualPool.getAvailO15MalesID()!=null && IndividualPool.getAvailO15MalesID().length>0) {
			// with the type of this household, determines if adding a male or female will better reserves the distribution of males and females of that household type
			double[] percentGenders = CensusTablesAnalyser.getPercentGenderInHholdType(hhType);
			
			int[] femaleCountsByHhTypes = SPAnalyser.countIndivAllocByHhType(Genders._female);
			int[] maleCountsByHhTypes = SPAnalyser.countIndivAllocByHhType(Genders._male);
			int[] crnCountGenders = new int[] {femaleCountsByHhTypes[hhType.getIndex()], maleCountsByHhTypes[hhType.getIndex()]};
			
			int genderIdx = ArrayHandler.getIndexOfLowestRMS(percentGenders, crnCountGenders);
			// adding a female better reserves the distribution of males and females of that household type
			if (genderIdx==Genders._female.getValue()) {
				o15IDPicked = pickOneO15Child(minValidAge, maxValidAge, IndividualPool.getAvailO15FemalesID());
				IndividualPool.setAvailO15FemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailO15FemalesID(), o15IDPicked));
			}
			// adding a male better reserves the distribution of males and females of that household type
			else if (genderIdx==Genders._male.getValue()) {
				o15IDPicked = pickOneO15Child(minValidAge, maxValidAge, IndividualPool.getAvailO15MalesID());
				IndividualPool.setAvailO15MalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailO15MalesID(), o15IDPicked));
			}
		}
		// if only O15Child females available
		else if (IndividualPool.getAvailO15FemalesID()!=null && IndividualPool.getAvailO15FemalesID().length>0 &&
				(IndividualPool.getAvailO15MalesID()==null || IndividualPool.getAvailO15MalesID().length==0)) {
			o15IDPicked = pickOneO15Child(minValidAge, maxValidAge, IndividualPool.getAvailO15FemalesID());
			IndividualPool.setAvailO15FemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailO15FemalesID(), o15IDPicked));
		}
		// if only O15Child males available
		else if ((IndividualPool.getAvailO15FemalesID()==null || IndividualPool.getAvailO15FemalesID().length==0) &&
				(IndividualPool.getAvailO15MalesID()!=null && IndividualPool.getAvailO15MalesID().length!=0)) {
			o15IDPicked = pickOneO15Child(minValidAge, maxValidAge, IndividualPool.getAvailO15MalesID());
			IndividualPool.setAvailO15MalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailO15MalesID(), o15IDPicked));
		}
		
		// if for some reasons, the selection process couldn't pick up a valid O15Child
		if (o15IDPicked==-1) {
			// creates a new O15Child individual with gender and age that best reserves the distribution of males and females of that household type
			o15IDPicked = IndividualPool.constructOneNewIndividual(HholdRelSP.O15Child, minValidAge, maxValidAge);
		}
		
		// adds this individual to this household
		if (o15IDPicked<0)
			System.out.println("o15IDPicked<0 in SPConstructor.assignO15Child.");
		HouseholdPool.addResidentsToHousehold(hhID, o15IDPicked);
	}
	
	
	/**
	 * 
	 * @param hhID
	 * @param ageYoungerParent
	 * @param hhType
	 */
	private static void assignStuChild(int hhID, int ageYoungerParent, HholdTypes hhType) {
		int[] childAgeBounds = IndividualPool.calculateChildAgeBounds(ageYoungerParent);
		int minValidAge = childAgeBounds[0];
		int maxValidAge = childAgeBounds[1];
		
		int stuIDPicked = -1;
		
		// if both Student males and females are available 
		if (IndividualPool.getAvailStuFemalesID()!=null && IndividualPool.getAvailStuFemalesID().length>0 &&
				IndividualPool.getAvailStuMalesID()!=null && IndividualPool.getAvailStuMalesID().length>0) {
			// with the type of this household, determines if adding a male or female will better reserves the distribution of males and females of that household type
			double[] percentGenders = CensusTablesAnalyser.getPercentGenderInHholdType(hhType);
			
			int[] femaleCountsByHhTypes = SPAnalyser.countIndivAllocByHhType(Genders._female);
			int[] maleCountsByHhTypes = SPAnalyser.countIndivAllocByHhType(Genders._male);
			int[] crnCountGenders = new int[] {femaleCountsByHhTypes[hhType.getIndex()], maleCountsByHhTypes[hhType.getIndex()]};
			
			int genderIdx = ArrayHandler.getIndexOfLowestRMS(percentGenders, crnCountGenders);
			// adding a female better reserves the distribution of males and females of that household type
			if (genderIdx==Genders._female.getValue()) {
				stuIDPicked = pickOneStuChild(minValidAge, maxValidAge, IndividualPool.getAvailStuFemalesID());
				IndividualPool.setAvailStuFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailStuFemalesID(), stuIDPicked));
			}
			// adding a male better reserves the distribution of males and females of that household type
			else if (genderIdx==Genders._male.getValue()) {
				stuIDPicked = pickOneStuChild(minValidAge, maxValidAge, IndividualPool.getAvailStuMalesID());
				IndividualPool.setAvailStuMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailStuMalesID(), stuIDPicked));
			}
			
		} 
		// if only Student females available
		else if ((IndividualPool.getAvailStuFemalesID()!=null && IndividualPool.getAvailStuFemalesID().length>0) &&
				(IndividualPool.getAvailStuMalesID()==null || IndividualPool.getAvailStuMalesID().length==0)) {
			stuIDPicked = pickOneStuChild(minValidAge, maxValidAge, IndividualPool.getAvailStuFemalesID());
			IndividualPool.setAvailStuFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailStuFemalesID(), stuIDPicked));
		}
		// if only Student males available
		else if ((IndividualPool.getAvailStuFemalesID()==null || IndividualPool.getAvailStuFemalesID().length==0) &&
				(IndividualPool.getAvailStuMalesID()!=null && IndividualPool.getAvailStuMalesID().length>0)) {
			stuIDPicked = pickOneStuChild(minValidAge, maxValidAge, IndividualPool.getAvailStuMalesID());
			IndividualPool.setAvailStuMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailStuMalesID(), stuIDPicked));
		} 
		
		// if for some reasons, the selection process couldn't pick up a valid Student
		if (stuIDPicked==-1) {
			// creates a new Student individual with gender and age that best reserves the distribution of males and females of that household type
			//stuIDPicked = constructNewStudentIndiv(hhType, minValidAge, maxValidAge);
			stuIDPicked = IndividualPool.constructOneNewIndividual(HholdRelSP.Student, minValidAge, maxValidAge);
		}
		
		// adds this individual to this household
		if (stuIDPicked<0)
			System.out.println("stuIDPicked=" + stuIDPicked + " in SPConstructor.assignStuChild. hhID " + hhID + ", hhType " + hhType.toString());
		HouseholdPool.addResidentsToHousehold(hhID, stuIDPicked);
		
	}
	
	
	/**
	 * 
	 * @param hhID
	 * @param ageYoungerParent
	 * @param hhType
	 */
	private static void assignU15Child(int hhID, int ageYoungerParent, HholdTypes hhType) {
//		System.out.println("\nhhID " + hhID + ", ageYoungerParent " + ageYoungerParent + ", hhType " + hhType.toString());
		
		int[] childAgeBounds = IndividualPool.calculateChildAgeBounds(ageYoungerParent);
		int minValidAge = childAgeBounds[0];
		int maxValidAge = childAgeBounds[1];
		
		int u15IDPicked = -1;
		
		// if both U15 males and females are available
		if (IndividualPool.getAvailU15FemalesID()!=null && IndividualPool.getAvailU15FemalesID().length>0 &&
				IndividualPool.getAvailU15MalesID()!=null && IndividualPool.getAvailU15MalesID().length>0) {
			// with the type of this household, determines if adding a male or female will better reserves the distribution of males and females of that household type
//			System.out.println("Picking U15 female or male. ");
			double[] percentGenders = CensusTablesAnalyser.getPercentGenderInHholdType(hhType);
			
			int[] femaleCountsByHhTypes = SPAnalyser.countIndivAllocByHhType(Genders._female);
			int[] maleCountsByHhTypes = SPAnalyser.countIndivAllocByHhType(Genders._male);
			int[] crnCountGenders = new int[] {femaleCountsByHhTypes[hhType.getIndex()], maleCountsByHhTypes[hhType.getIndex()]};
			
			int genderIdx = ArrayHandler.getIndexOfLowestRMS(percentGenders, crnCountGenders);
			if (genderIdx==Genders._female.getValue()) {
				u15IDPicked = pickOneU15Child(minValidAge, maxValidAge, IndividualPool.getAvailU15FemalesID());
//				System.out.println("Age " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()] + ", " 
//						+ "Gender " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.gender.getIndex()]);
//				System.out.println("Before nU15Males/nU15Females " + ArrayHandler.getLength(IndividualPool.getAvailU15MalesID())
//						+ "/" + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()));
				
				IndividualPool.setAvailU15FemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailU15FemalesID(), u15IDPicked));
				
//				System.out.println("After nU15Males/nU15Females " + ArrayHandler.getLength(IndividualPool.getAvailU15MalesID())
//						+ "/" + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()));
			}
			else if (genderIdx==Genders._male.getValue()) {
				u15IDPicked = pickOneU15Child(minValidAge, maxValidAge, IndividualPool.getAvailU15MalesID());
//				System.out.println("Age " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()] + ", " 
//						+ "Gender " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.gender.getIndex()]);
//				System.out.println("Before nU15Males/nU15Females " + ArrayHandler.getLength(IndividualPool.getAvailU15MalesID())
//						+ "/" + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()));
				
				IndividualPool.setAvailU15MalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailU15MalesID(), u15IDPicked));
				
//				System.out.println("After nU15Males/nU15Females " + ArrayHandler.getLength(IndividualPool.getAvailU15MalesID())
//						+ "/" + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()));
			}
			
		} 
		// if only U15 females available
		else if ((IndividualPool.getAvailU15FemalesID()!=null && IndividualPool.getAvailU15FemalesID().length>0) && 
				(IndividualPool.getAvailU15MalesID()==null || IndividualPool.getAvailU15MalesID().length==0)) {
			u15IDPicked = pickOneU15Child(minValidAge, maxValidAge, IndividualPool.getAvailU15FemalesID());
//			System.out.println("Picking U15 female. "
//					+ "Age " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()] + ", " 
//					+ "Gender " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.gender.getIndex()]);
//			System.out.println("Before nU15Males/nU15Females " + ArrayHandler.getLength(IndividualPool.getAvailU15MalesID())
//					+ "/" + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()));
			
			IndividualPool.setAvailU15FemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailU15FemalesID(), u15IDPicked));
			
//			System.out.println("After nU15Males/nU15Females " + ArrayHandler.getLength(IndividualPool.getAvailU15MalesID())
//					+ "/" + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()));
		}  
		// if only U15 males available
		else if ((IndividualPool.getAvailU15FemalesID()==null || IndividualPool.getAvailU15FemalesID().length==0) &&
				(IndividualPool.getAvailU15MalesID()!=null && IndividualPool.getAvailU15MalesID().length>0)) {
			u15IDPicked = pickOneU15Child(minValidAge, maxValidAge, IndividualPool.getAvailU15MalesID());
//			System.out.println("Picking U15 male. "
//					+ "Age " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()] + ", " 
//					+ "Gender " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.gender.getIndex()]);
//			System.out.println("Before nU15Males/nU15Females " + ArrayHandler.getLength(IndividualPool.getAvailU15MalesID())
//					+ "/" + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()));
			
			IndividualPool.setAvailU15MalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailU15MalesID(), u15IDPicked));
			
//			System.out.println("After nU15Males/nU15Females " + ArrayHandler.getLength(IndividualPool.getAvailU15MalesID())
//					+ "/" + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()));
			
		}
		
		// if for some reasons, e.g. neither U15 males nor females available, or selection processes couldn't picked up a valid U15, etc.
		if (u15IDPicked==-1) {
			// creates a new U15 individual with gender and age that best reserves the distribution of males and females of that household type
			//u15IDPicked = constructNewU15Indiv(hhType, minValidAge, maxValidAge);
			u15IDPicked = IndividualPool.constructOneNewIndividual(HholdRelSP.U15Child, minValidAge, maxValidAge);
//			System.out.println("constructed new U15 Individual. "
//					+ "Age " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()] + ", " 
//					+ "Gender " + IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.gender.getIndex()] + ". "
//					+ "nU15Males = " + ArrayHandler.getLength(IndividualPool.getAvailU15MalesID())
//					+ "nU15Females = " + ArrayHandler.getLength(IndividualPool.getAvailU15FemalesID()));
		}
		
		// adds this individual to this household
		if (u15IDPicked<0)
			System.out.println("u15IDPicked<0 in SPConstructor.assignU15Child");
		HouseholdPool.addResidentsToHousehold(hhID, u15IDPicked);
	}
	
	
	/**
	 * 
	 * @param minValidAge youngest possible age of the U15 child.
	 * @param maxValidAge eldest possible age of the U15 child.
	 * @param availableU15ChildIDs list of ID of available U15 child (normally all males or all females) sorted by ascending age.
	 * @return ID of the U15Child whose age is between minValidAge and maxValidAge
	 */
	private static int pickOneU15Child(int minValidAge, int maxValidAge, int[] availableU15ChildIDs) {
		int u15IDPicked = -1;
		
		if (maxValidAge<HardcodedData.minAgeU15) {
			// picks the youngest U15 child, i.e. the first ID in availableU15ChildIDs
			u15IDPicked = availableU15ChildIDs[0];
			// sets the age of this U15 child to minAgeU15, so that the age gap between of youngest parent and the child is a little larger.
			IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()] = HardcodedData.minAgeU15;
		} else if (minValidAge>HardcodedData.maxAgeU15) {
			// picks the eldest U15 child, i.e the last ID in availableU15ChildIDs
			u15IDPicked = availableU15ChildIDs[availableU15ChildIDs.length-1];
			// sets the age of this U15 child to maxAgeU15, so that the age gap between the youngest parent and the child is a little smaller.
			IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()] = HardcodedData.maxAgeU15;
		} else {
			int lowerBoundAge = Math.max(minValidAge, HardcodedData.minAgeU15);
			int upperBoundAge = Math.min(maxValidAge, HardcodedData.maxAgeU15);
			u15IDPicked = pickIndivYoungerThanMaxAgeFromSpecList(availableU15ChildIDs, upperBoundAge);
			if (u15IDPicked==-1) { // if there is no u15 child younger than upperBoundAge
				// picks the youngest u15 child
				u15IDPicked = availableU15ChildIDs[0];
				// changes the age of this u15 child to upperBoundAge
				IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()] = upperBoundAge;
			} 
			// if a valid u15 child is found but the age is younger than lowerBoundAge, set age of this u15 child to lowerBoundAge
			else if (IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()]<lowerBoundAge) {
				IndividualPool.getPool().get(u15IDPicked)[IndivAttribs.age.getIndex()] = lowerBoundAge;
			}
		}
		
		return u15IDPicked;
	}
	
	
	/**
	 * 
	 * @param minValidAge youngest possible age of the student child.
	 * @param maxValidAge eldest possible age of the student child.
	 * @param availableStuChildIDs list of ID of available Student child (normally all males or all females) sorted by ascending age.
	 * @return ID of the Student whose age is between minValidAge and maxValidAge
	 */
	private static int pickOneStuChild(int minValidAge, int maxValidAge, int[] availableStuChildIDs) {
		int stuIDPicked = -1;
		
		if (maxValidAge<HardcodedData.minAgeStu) {
			// picks the youngest Student child, i.e. the first ID in availableStuChildIDs
			stuIDPicked = availableStuChildIDs[0];
			// sets the age of this Student child to minAgeStu, so that the age gap between of youngest parent and the child is a little larger.
			IndividualPool.getPool().get(stuIDPicked)[IndivAttribs.age.getIndex()] = HardcodedData.minAgeStu;
		} else if (minValidAge>HardcodedData.maxAgeStu) {
			// picks the eldest Stu child, i.e the last ID in availableStuChildIDs
			stuIDPicked = availableStuChildIDs[availableStuChildIDs.length-1];
			// sets the age of this Stu child to maxAgeStu, so that the age gap between the youngest parent and the child is a little smaller.
			IndividualPool.getPool().get(stuIDPicked)[IndivAttribs.age.getIndex()] = HardcodedData.maxAgeStu;
		} else {
			int lowerBoundAge = Math.max(minValidAge, HardcodedData.minAgeStu);
			int upperBoundAge = Math.min(maxValidAge, HardcodedData.maxAgeStu);
			stuIDPicked = pickIndivYoungerThanMaxAgeFromSpecList(availableStuChildIDs, upperBoundAge);
			if (stuIDPicked==-1) { // if there is no student child younger than upperBoundAge
				// picks the youngest student child
				stuIDPicked = availableStuChildIDs[0];
				// changes the age of this student child to upperBoundAge
				IndividualPool.getPool().get(stuIDPicked)[IndivAttribs.age.getIndex()] = upperBoundAge;
			} 
			// if a valid student child is found but the age is younger than lowerBoundAge, set age of this student child to lowerBoundAge
			else if (IndividualPool.getPool().get(stuIDPicked)[IndivAttribs.age.getIndex()]<lowerBoundAge) {
				IndividualPool.getPool().get(stuIDPicked)[IndivAttribs.age.getIndex()] = lowerBoundAge;
			}
		}
		
		return stuIDPicked;
	}
	
	
	/**
	 * 
	 * @param minValidAge youngest possible age of the o15 child (in relation to the age of younger parent in the household).
	 * @param maxValidAge eldest possible age of the o15 child (in relation to the age of younger parent in the household).
	 * @param availableO15ChildIDs list of ID of available O15 children (normally all males or all females) sorted by ascending age
	 * @return ID of the O15Child whose age is between minValidAge and maxValidAge
	 */
	private static int pickOneO15Child(int minValidAge, int maxValidAge, int[] availableO15ChildIDs) {
		int o15IDPicked = -1;
		
		if (maxValidAge<HardcodedData.minAgeO15) {
			// picks the youngest O15 child, i.e. the first ID in the availableO15ChildIDs
			o15IDPicked = availableO15ChildIDs[0];
			// change the age of this individual to the smallest age of his age group, so that the age gap between the youngest parent and the child is a little larger.
			int crntAge = IndividualPool.getPool().get(o15IDPicked)[IndivAttribs.age.getIndex()];
			IndividualPool.getPool().get(o15IDPicked)[IndivAttribs.age.getIndex()] = AgeGroups.getAgeGroup(crntAge).getMinAge();
		}
		else {
			int lowerBoundAge = Math.max(minValidAge, HardcodedData.minAgeO15);
			int upperBoundAge = maxValidAge;
			o15IDPicked = pickIndivYoungerThanMaxAgeFromSpecList(availableO15ChildIDs, upperBoundAge);
			if (o15IDPicked==-1) {// if there is no O15 child younger than upperBoundAge
				// picks the youngest o15 child (without changing the age of this individual to uppBoundAge, because doing this will change the distribution of O15Child by age)
				o15IDPicked = availableO15ChildIDs[0];
				// gets the youngest age in the age group of this individual
				int minOfAgeGroup = AgeGroups.getAgeGroup(IndividualPool.getPool().get(o15IDPicked)[IndivAttribs.age.getIndex()]).getMinAge();
				// assigns a new age to this O15Child individual so that the new age is within the current age group as well as within (lowerBoundAge upperBoundAge) range.
				if (minOfAgeGroup>upperBoundAge) {
					IndividualPool.getPool().get(o15IDPicked)[IndivAttribs.age.getIndex()] = minOfAgeGroup;
				} else {
					int tmpMinAge = Math.max(minOfAgeGroup, lowerBoundAge);
					int[] possibleAges = ArrayHandler.makeIncrementalIntArray(tmpMinAge, upperBoundAge, 1);
					IndividualPool.getPool().get(o15IDPicked)[IndivAttribs.age.getIndex()] = ArrayHandler.pickRandomFromArray(possibleAges, null, 1, HardcodedData.random)[0];
				}
			}
			// if a valid O15Child is found but the age is younger than lowerBoundAge
			else if (IndividualPool.getPool().get(o15IDPicked)[IndivAttribs.age.getIndex()]<lowerBoundAge) {
				// gets of max age of the age group
				int maxOfAgeGroup = AgeGroups.getAgeGroup(IndividualPool.getPool().get(o15IDPicked)[IndivAttribs.age.getIndex()]).getMaxAge();
				if (maxOfAgeGroup<lowerBoundAge) {
					IndividualPool.getPool().get(o15IDPicked)[IndivAttribs.age.getIndex()] = maxOfAgeGroup;
				} else {
					int tmpMaxAge = Math.min(maxOfAgeGroup, upperBoundAge);
					int[] possibleAges = ArrayHandler.makeIncrementalIntArray(lowerBoundAge, tmpMaxAge, 1);
					IndividualPool.getPool().get(o15IDPicked)[IndivAttribs.age.getIndex()] = ArrayHandler.pickRandomFromArray(possibleAges, null, 1, HardcodedData.random)[0];
				}
			}
		}
		return o15IDPicked;
	}
	
	
	/**
	 * 
	 * @param existingMap
	 * @return
	 */
	private static HashMap<Integer,int[]> assignParentsToHholds(HashMap<Integer,int[]> existingMap) {
		// assigns Married individuals to households that need 2 parents
		existingMap = assignMarriedIndivsToHholds(existingMap);
		System.out.println("\t\t\t\t ...assignMarriedIndivsToHholds done! " + getTimeStamp());
		
		// removes any remaining Married individuals
		IndividualPool.setAvailMarriedFemalesID(IndividualPool.removesIndivsFromIndivPool(IndividualPool.getAvailMarriedFemalesID()));
		IndividualPool.setAvailMarriedMalesID(IndividualPool.removesIndivsFromIndivPool(IndividualPool.getAvailMarriedMalesID()));
		
		// assigns LoneParent individuals to households that need 1 parent
		// if no LoneParent is available for a household, constructs a new LoneParent individual for that household.
		// if for any reason the new LoneParent cannot be constructed, remove this household.
		existingMap = assignLoneParentIndivsToHholds(existingMap);
		System.out.println("\t\t\t\t ...assignLoneParentIndivsToHholds done! " + getTimeStamp());
		
		// removes any remaining LoneParent individuals
		IndividualPool.setAvailLoneParentFemalesID(IndividualPool.removesIndivsFromIndivPool(IndividualPool.getAvailLoneParentFemalesID()));
		IndividualPool.setAvailLoneParentMalesID(IndividualPool.removesIndivsFromIndivPool(IndividualPool.getAvailLoneParentMalesID()));
		
		return existingMap;
	}
	
	
	/**
	 * 
	 * @param existingMap
	 * @return
	 */
	private static HashMap<Integer, int[]> assignLoneParentIndivsToHholds(HashMap<Integer,int[]> hhParentsMap) {
		Iterator<Entry<Integer, int[]>> itMap = HouseholdPool.getPool().entrySet().iterator();
		
		ArrayList<Integer> hhIDWithU15Only = new ArrayList<Integer>();
		ArrayList<Integer> hhIDWithU15O15 = new ArrayList<Integer>();
		ArrayList<Integer> hhIDWithO15Only = new ArrayList<Integer>();
		ArrayList<Integer> hhIDOther = new ArrayList<Integer>(); // this should be empty
		
		while (itMap.hasNext()) {
			Entry<Integer, int[]> hhEntry = itMap.next();
			int hhID = hhEntry.getKey();
			int[] hhAttribs = hhEntry.getValue();
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(hhAttribs[HholdAttribs.hhType.getIndex()]);
			int minPar = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_parents.getIndex()];
			int minU15 = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_u15.getIndex()];
			int minStu = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_students.getIndex()];
			int minO15 = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_o15.getIndex()];
			
			if (minPar==1) {
				if (minU15>0) {
					if (minStu>0 || minO15>0) {
						hhIDWithU15O15.add(hhID);
					} else {
						hhIDWithU15Only.add(hhID);
					}
				} else {
					if (minStu>0 || minO15>0) {
						hhIDWithO15Only.add(hhID);
					} else {
						hhIDOther.add(hhID); // hhIDOther should be empty
					}
				}
			}
		}
		
		
//		System.out.println("\nLoneparent households with U15 only");
		System.out.println("\t\t\t\t\t start locating lone parent for " + hhIDWithU15Only.size() + " households with U15 only... " + getTimeStamp());
		int minAgeParentWithU15Only = HardcodedData.ageOfConsent;
		hhParentsMap = assignLoneParentToSpecifiedHholdList(hhIDWithU15Only, minAgeParentWithU15Only, hhParentsMap);
		
//		System.out.println("\nLoneParent households with U15 and O15");
		System.out.println("\t\t\t\t\t start locating lone parent for " + hhIDWithU15O15.size() + " households with U15 and O15... " + getTimeStamp());
		int minAgeParentWithO15 = HardcodedData.minAgeO15 + HardcodedData.ageOfConsent;
		hhParentsMap = assignLoneParentToSpecifiedHholdList(hhIDWithU15O15, minAgeParentWithO15, hhParentsMap);
		
//		System.out.println("\nLoneparent households with O15 only");
		System.out.println("\t\t\t\t\t start locating lone parent for " + hhIDWithO15Only.size() + " households with O15 only... " + getTimeStamp());
		hhParentsMap = assignLoneParentToSpecifiedHholdList(hhIDWithO15Only, minAgeParentWithO15, hhParentsMap);
		
//		System.out.println("\nLoneparent households Other (should be empty)");
		System.out.println("\t\t\t\t\t start locating lone parent for " + hhIDOther.size() + " other households... " + getTimeStamp());
		hhParentsMap = assignLoneParentToSpecifiedHholdList(hhIDOther, HardcodedData.ageOfConsent, hhParentsMap);
		
		return hhParentsMap;
	}
	
	
	/**
	 * 
	 * @param hhIDList
	 * @param minAgeParent
	 * @param existingMap
	 * @return
	 */
	private static HashMap<Integer,int[]> assignLoneParentToSpecifiedHholdList(
			ArrayList<Integer> hhIDList, int minAgeParent, HashMap<Integer,int[]>existingMap) {
		
		int count = 0;
		
		for (Integer hhID : hhIDList) {
			int[] hhAttribs = HouseholdPool.getPool().get(hhID);
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(hhAttribs[HholdAttribs.hhType.getIndex()]);
			
			int loneParentID = -1;
			
			// if there are both female and male LoneParent available
			if (IndividualPool.getAvailLoneParentFemalesID()!=null && IndividualPool.getAvailLoneParentFemalesID().length!=0 &&
					IndividualPool.getAvailLoneParentMalesID()!=null && IndividualPool.getAvailLoneParentMalesID().length!=0) {
				
				// searches for youngest lone parent female that is older than minAgeParent 
				int loneParentIDFemale = pickIndivOlderThanMinAgeFromSpecList(IndividualPool.getAvailLoneParentFemalesID(), minAgeParent);
				// if no individual in specifiedList is older than minAge, return the eldest individual in this list.
				if (loneParentIDFemale==-1) { 
					loneParentIDFemale = IndividualPool.getAvailLoneParentFemalesID()[IndividualPool.getAvailLoneParentFemalesID().length-1];
				}
				
				// searches for youngest lone parent male older than minAgeParent
				int loneParentIDMale = pickIndivOlderThanMinAgeFromSpecList(IndividualPool.getAvailLoneParentMalesID(), minAgeParent);
				if (loneParentIDMale==-1) {
					loneParentIDMale = IndividualPool.getAvailLoneParentMalesID()[IndividualPool.getAvailLoneParentMalesID().length-1];
				}
				
				// out of this male and female LoneParents, chooses the younger one
				if (IndividualPool.getPool().get(loneParentIDFemale)[IndivAttribs.age.getIndex()]<=
						IndividualPool.getPool().get(loneParentIDMale)[IndivAttribs.age.getIndex()]) {
					loneParentID = loneParentIDFemale;
//					System.out.println(hhType.toString() + ", " + minAgeParent + ", female, min age picked " + IndividualPool.getPool().get(loneParentID)[IndivAttribs.age.getIndex()]);
					
					// remove loneParentID from IndividualPool.availLoneParentFemalesID
					IndividualPool.setAvailLoneParentFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailLoneParentFemalesID(), loneParentID));
				} else {
					loneParentID = loneParentIDMale;
//					System.out.println(hhType.toString() + ", " + minAgeParent + ", male, min age picked " + IndividualPool.getPool().get(loneParentID)[IndivAttribs.age.getIndex()]);
					
					// remove loneParentID from IndividualPool.availLoneParentMalesID
					IndividualPool.setAvailLoneParentMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailLoneParentMalesID(), loneParentID));
				}
			}
			// if there are only female 'LoneParent' available
			else if (IndividualPool.getAvailLoneParentFemalesID()!=null && IndividualPool.getAvailLoneParentFemalesID().length!=0 &&
					(IndividualPool.getAvailLoneParentMalesID()==null || IndividualPool.getAvailLoneParentMalesID().length==0)) { 
				// searches for youngest lone parent female that is older than minAgeParent 
				loneParentID = pickIndivOlderThanMinAgeFromSpecList(IndividualPool.getAvailLoneParentFemalesID(), minAgeParent);
				// if no individual in specifiedList is older than minAge, return the eldest individual in this list.
				if (loneParentID==-1) { 
					loneParentID = IndividualPool.getAvailLoneParentFemalesID()[IndividualPool.getAvailLoneParentFemalesID().length-1];
				}
//				System.out.println(hhType.toString() + ", " + minAgeParent + ", female, min age picked " + IndividualPool.getPool().get(loneParentID)[IndivAttribs.age.getIndex()]);
				
				// remove loneParentID from IndividualPool.availLoneParentFemalesID
				IndividualPool.setAvailLoneParentFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailLoneParentFemalesID(), loneParentID));
			} 
			// else if there are only male 'LoneParent' available
			else if ((IndividualPool.getAvailLoneParentFemalesID()==null || IndividualPool.getAvailLoneParentFemalesID().length==0) &&
					IndividualPool.getAvailLoneParentMalesID()!=null && IndividualPool.getAvailLoneParentMalesID().length!=0) {
				// searches for youngest lone parent male older than minAgeParent
				loneParentID = pickIndivOlderThanMinAgeFromSpecList(IndividualPool.getAvailLoneParentMalesID(), minAgeParent);
				if (loneParentID==-1) {
					loneParentID = IndividualPool.getAvailLoneParentMalesID()[IndividualPool.getAvailLoneParentMalesID().length-1];
				}
//				System.out.println(hhType.toString() + ", " + minAgeParent + ", male, min age picked " + IndividualPool.getPool().get(loneParentID)[IndivAttribs.age.getIndex()]);
				
				// remove loneParentID from IndividualPool.availLoneParentMalesID
				IndividualPool.setAvailLoneParentMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailLoneParentMalesID(), loneParentID));
			} 
			// no 'LoneParent' available
			else {
				// construct new LoneParents
				loneParentID = IndividualPool.constructOneNewIndividual(HholdRelSP.LoneParent, minAgeParent, HardcodedData.largeInt);
			}

			// if no valid LoneParent individual is found, remove this household.
			if (loneParentID==-1) {
				//SPAnalyser.remove1Hhold(hhType);
				HouseholdPool.getPool().remove(hhID);
				continue;
			}

			if (loneParentID<0)
				System.out.println("loneParentID<0 in SPConstructor.assignLoneParentToSpecifiedHholdList.");
			
			HouseholdPool.addResidentsToHousehold(hhID, loneParentID);

			existingMap.put(hhID, new int[] {loneParentID, -1});
		
			count += 1;
			if ((count%100)==0)
				System.out.println("\t\t\t\t\t\t ..." + count + " households located!" + getTimeStamp());
		}
		
		return existingMap;
	}
	
		
	/**
	 * 
	 * 
	 * @param specifiedList this is a list of individual IDs sorted by ascending age.
	 * @param minAge the selected individual must be older than or equal to this age.
	 * @return ID of youngest individual from specifiedList that is older than or equal to minAge. 
	 */
	private static int pickIndivOlderThanMinAgeFromSpecList(int[] specifiedList, int minAge) {
		if (specifiedList==null || specifiedList.length==0) return -1;
		// searches for youngest individual who is older than minAge
		for (int i=0; i<=specifiedList.length-1; i++) {
			int indivID = specifiedList[i];
			if (IndividualPool.getPool().get(indivID)[IndivAttribs.age.getIndex()]>=minAge) {
				return indivID;
			}
		}
		return -1;
	}
	
	
	/**
	 * 
	 * @param specifiedList this is a list of individual IDs sorted by ascending age.
	 * @param maxAge the selected individual must be younger than or equal to this age.
	 * @return ID of the eldest individual from specifiedList that is younger than or equal to maxAge. If no individual in specifiedList is younger than maxAge, return -1;
	 */
	private static int pickIndivYoungerThanMaxAgeFromSpecList(int[] specifiedList, int maxAge) {
		if (specifiedList==null || specifiedList.length==0) return -1;
		// searches for youngest individual who is older than minAge
		for (int i=specifiedList.length-1; i>=0; i--) {
			int indivID = specifiedList[i];
			if (IndividualPool.getPool().get(indivID)[IndivAttribs.age.getIndex()]<=maxAge) {
				return indivID;
			}
		}
		return -1;
	}
	

	/**
	 * assigns 2 Married individuals to each household that needs 2 parents.
	 * A new Married individual is added to the individual pool if only 1 Married individual remaining available in individual pool.
	 * If no Married individuals available in the individual pool, the household is removed from household pool.
	 * 
	 * @param existingMap
	 * @return
	 */
	private static HashMap<Integer, int[]> assignMarriedIndivsToHholds(HashMap<Integer,int[]> existingMap) {
		Iterator<Entry<Integer, int[]>> itMap = HouseholdPool.getPool().entrySet().iterator();
		
		ArrayList<Integer> hhIDWithU15Only = new ArrayList<Integer>();
		ArrayList<Integer> hhIDWithU15O15 = new ArrayList<Integer>();
		ArrayList<Integer> hhIDWithO15Only = new ArrayList<Integer>();
		ArrayList<Integer> hhIDOther = new ArrayList<Integer>();
		
		System.out.println("\t\t\t\t\t start classifying " + HouseholdPool.getPool().size() + " households. " + getTimeStamp());
		while (itMap.hasNext()) {
			Entry<Integer, int[]> hhEntry = itMap.next();
			int hhID = hhEntry.getKey();
			int[] hhAttribs = hhEntry.getValue();
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(hhAttribs[HholdAttribs.hhType.getIndex()]);
			int minPar = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_parents.getIndex()];
			int minU15 = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_u15.getIndex()];
			int minStu = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_students.getIndex()];
			int minO15 = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_o15.getIndex()];
			
			if (minPar>1) {
				if (minU15>0) {
					if (minStu>0 || minO15>0) {
						hhIDWithU15O15.add(hhID);
					} else {
						hhIDWithU15Only.add(hhID);
					}
				} else {
					if (minStu>0 || minO15>0) {
						hhIDWithO15Only.add(hhID);
					} else {
						hhIDOther.add(hhID); // these are HF1 households
					}
				}
				
			}
		}
		System.out.println("\t\t\t\t\t ...classifying households done! " + getTimeStamp());
		
//		System.out.println("\nhouseholds with U15 only");
		System.out.println("\t\t\t\t\t start locating parents for " + hhIDWithU15Only.size() + " households with U15 only... " + getTimeStamp());
		int minAgeParentWithU15Only = HardcodedData.ageOfConsent;
		existingMap = assignMarriedCouplesToSpecifiedHholdList(hhIDWithU15Only, minAgeParentWithU15Only, existingMap);
		
		
//		System.out.println("\nhouseholds with U15 and O15");
		System.out.println("\t\t\t\t\t start locating parents for " + hhIDWithU15O15.size() + " households with U15 and O15... " + getTimeStamp());
		int minAgeParentWithO15 = HardcodedData.minAgeO15 + HardcodedData.ageOfConsent;
		existingMap = assignMarriedCouplesToSpecifiedHholdList(hhIDWithU15O15, minAgeParentWithO15, existingMap);
		
		
//		System.out.println("\nhouseholds with O15 only");
		System.out.println("\t\t\t\t\t start locating parents for " + hhIDWithO15Only.size() + " households with O15 only... " + getTimeStamp());
		existingMap = assignMarriedCouplesToSpecifiedHholdList(hhIDWithO15Only, minAgeParentWithO15, existingMap);
		
		
//		System.out.println("\nhouseholds other (HF1)");
		System.out.println("\t\t\t\t\t start locating parents for " + hhIDOther.size() + " other households... " + getTimeStamp());
		existingMap = assignMarriedCouplesToSpecifiedHholdList(hhIDOther, HardcodedData.ageOfConsent, existingMap);
		
		
		return existingMap;
	}
	
	
	/**
	 * 
	 * @param hhIDList
	 * @param minAgeParent
	 * @param existingMap
	 * @return
	 */
	private static HashMap<Integer, int[]> assignMarriedCouplesToSpecifiedHholdList(ArrayList<Integer> hhIDList, int minAgeParent, HashMap<Integer,int[]> existingMap) {
		int count = 0;
		for (Integer hhID : hhIDList) {
			
			// generates a preferred age gap for couple in this household following Gaussian distribution
			double ageGapDouble = HardcodedData.random.nextGaussian()*HardcodedData.stddAgeMarriedCouple + HardcodedData.meandAgeMarriedCouple;
			int ageGapInt = (int)(ageGapDouble+0.5); 
			//int ageGapInt = HardcodedData.meandAgeMarriedCouple;
			
			int[] hhAttribs = HouseholdPool.getPool().get(hhID);
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(hhAttribs[HholdAttribs.hhType.getIndex()]);
			if (IndividualPool.isNotHavingAvailMarried()) {
				// removes this household from householdPool
				//SPAnalyser.remove1Hhold(hhType); 
				HouseholdPool.getPool().remove(hhID);
				continue;
			}
			else if (IndividualPool.isHavingAvailMarriedOppositeSex()) {
				int[] parentsID = pickMarriedOppositeSexCouple(hhID, minAgeParent, hhType, ageGapInt);
				IndividualPool.constructdAgeAvailMarried();
				existingMap.put(hhID, parentsID);
//				System.out.println(hhType.toString() + ", " + minAgeParent + ", opposite sex, min age picked " + 
//							Math.min(IndividualPool.getPool().get(parentsID[0])[IndivAttribs.age.getIndex()], IndividualPool.getPool().get(parentsID[1])[IndivAttribs.age.getIndex()]));
			}
			else if (IndividualPool.isHavingAvailMarriedMaleOnly()) { 
				int[] parentsID = pickMarriedMaleCouple(hhType, hhID, minAgeParent, ageGapInt);
				if (parentsID[0]>=0 && parentsID[1]>=0) {
					IndividualPool.constructdAgeAvailMarried();
					existingMap.put(hhID, parentsID);
//					System.out.println(hhType.toString() + ", " + minAgeParent + ", males, min age picked " + 
//							Math.min(IndividualPool.getPool().get(parentsID[0])[IndivAttribs.age.getIndex()], IndividualPool.getPool().get(parentsID[1])[IndivAttribs.age.getIndex()]));
				} else {
					//SPAnalyser.remove1Hhold(hhType);
					HouseholdPool.getPool().remove(hhID);
					continue;
				}
			}
			else if (IndividualPool.isHavingAvailMarriedFemaleOnly()) {
				int[] parentsID = pickMarriedFemaleCouple(hhType, hhID, minAgeParent, ageGapInt);
				if (parentsID[0]>=0 && parentsID[1]>=0) {
					IndividualPool.constructdAgeAvailMarried();
					existingMap.put(hhID, parentsID);
//					System.out.println(hhType.toString() + ", " + minAgeParent + ", female, min age picked " + 
//							Math.min(IndividualPool.getPool().get(parentsID[0])[IndivAttribs.age.getIndex()], IndividualPool.getPool().get(parentsID[1])[IndivAttribs.age.getIndex()]));
				} else {
					//SPAnalyser.remove1Hhold(hhType);
					HouseholdPool.getPool().remove(hhID);
					continue;
				}
			}
			count += 1;
			if ((count%100)==0) {
				System.out.println("\t\t\t\t\t\t ..." + count + " households located! " + getTimeStamp());
			}
		}
		
		return existingMap;
	}
	
	
	/**
	 * 
	 * @param hhType
	 * @param hhID
	 * @param minAgeParent
	 * @return
	 */
	private static int[] pickMarriedMaleCouple(HholdTypes hhType, int hhID, int minAgeParent, int ageGap) {
		
		int parent1ID = -1;
		int parent2ID = -1;
		
		if (IndividualPool.getAvailMarriedMalesID().length==1) { // if there is only 1 'Married' male left
			// constructs a new Married male that best reserves the distribution of household relationship (table b22a), regardless of the required minimum age of parent.
			//int newMarriedIndivID = constructNewMarriedIndiv();
			int newMarriedIndivID = IndividualPool.constructOneNewIndividual(HholdRelSP.Married, minAgeParent, HardcodedData.largeInt);
			
			if (newMarriedIndivID>=0) { // if a new Married male is successfully constructed
				parent1ID = newMarriedIndivID;
				parent2ID = IndividualPool.getAvailMarriedMalesID()[0];
				
				ArrayList<Integer> newParents = new ArrayList<Integer>();
				newParents.add(parent1ID);
				newParents.add(parent2ID);
				
				if (parent1ID==-1)
					System.out.println("parent1ID==-1 in SPConstructor.pickMarriedMaleCouple (1 male left)");
				if (parent2ID==-1)
					System.out.println("parent2ID==-1 in SPConstructor.pickMarriedMaleCouple (1 male left)");
				
				HouseholdPool.addResidentsToHousehold(hhID, newParents);
				
				// updates IndividualPool.availMarriedMalesID and IndividualPool.availMarriedFemalesID, and reconstruct IndividualPool.getdAgeMarriedMatrix
				IndividualPool.setAvailMarriedMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailMarriedMalesID(), parent2ID));
			}
			
		} else { // there are at least 2 Married males left
			// initially assumes that the youngest available male is the second eldest
			int idxAvailMale = IndividualPool.getAvailMarriedMalesID().length-2;
			// searches only until the second last ID in IndividualPool.availMarriedMalesID 
			// because the last ID sure belongs to an older individual and we need 2 parents. 
			for (int iMale=0; iMale<=IndividualPool.getAvailMarriedMalesID().length-2; iMale++) {
				if (IndividualPool.getPool().get(IndividualPool.getAvailMarriedMalesID()[iMale])[IndivAttribs.age.getIndex()]>=minAgeParent) {
					idxAvailMale = iMale;
					break;
				}
			}
			
			int finRow = idxAvailMale;
			int finCol = idxAvailMale+1;
			for (int iMale=idxAvailMale; iMale<=IndividualPool.getAvailMarriedMalesID().length-2; iMale++) {
				for (int jMale=iMale+1; jMale<=IndividualPool.getAvailMarriedMalesID().length-1; jMale++) {
					if (Math.abs(IndividualPool.getdAgeAvailMarried()[iMale][jMale]-ageGap) < 
							Math.abs(IndividualPool.getdAgeAvailMarried()[finRow][finCol]-ageGap)) {
//					if (Math.abs(IndividualPool.getdAgeAvailMarried()[iMale][jMale]-HardcodedData.meandAgeMarriedCouple) < 
//							Math.abs(IndividualPool.getdAgeAvailMarried()[finRow][finCol]-HardcodedData.meandAgeMarriedCouple)) {
						finRow = iMale;
						finCol = jMale;
					}
				}
			}
			
			parent1ID = IndividualPool.getAvailMarriedMalesID()[finRow];
			parent2ID = IndividualPool.getAvailMarriedMalesID()[finCol];
			
			ArrayList<Integer> newParents = new ArrayList<Integer>();
			newParents.add(parent1ID);
			newParents.add(parent2ID);
			
			if (parent1ID==-1)
				System.out.println("parent1ID==-1 in SPConstructor.pickMarriedMaleCouple (>1 male left)");
			if (parent2ID==-1)
				System.out.println("parent2ID==-1 in SPConstructor.pickMarriedMaleCouple (>1 male left)");
			
			HouseholdPool.addResidentsToHousehold(hhID, newParents);
			
			// updates IndividualPool.availMarriedMalesID and IndividualPool.availMarriedFemalesID, and reconstruct IndividualPool.getdAgeMarriedMatrix
			IndividualPool.setAvailMarriedMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailMarriedMalesID(), parent1ID));
			IndividualPool.setAvailMarriedMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailMarriedMalesID(), parent2ID));
		}
		
		return new int[] {parent1ID, parent2ID};
	}

	
	/**
	 * 
	 * @param hhType
	 * @param hhID
	 * @param minAgeParent
	 * @return
	 */
	private static int[] pickMarriedFemaleCouple(HholdTypes hhType, int hhID, int minAgeParent, int ageGap) {
		int parent1ID = -1;
		int parent2ID = -1;
		
		if (IndividualPool.getAvailMarriedFemalesID().length==1) { // if there is only 1 'Married' female left
			// constructs a new Married male or female that best reserves the distribution of household relationship (table b22a), regardless of the required minimum age of parent.
			//int newMarriedIndivID = constructNewMarriedIndiv();
			int newMarriedIndivID = IndividualPool.constructOneNewIndividual(HholdRelSP.Married, minAgeParent, HardcodedData.largeInt);
			
			if (newMarriedIndivID!=-1) { // if a new Married female is successfully constructed
				parent1ID = newMarriedIndivID;
				parent2ID = IndividualPool.getAvailMarriedFemalesID()[0];
				
				ArrayList<Integer> newParents = new ArrayList<Integer>();
				newParents.add(parent1ID);
				newParents.add(parent2ID);
				
				if (parent1ID==-1)
					System.out.println("parent1ID==-1 in SPConstructor.pickMarriedFemaleCouple (1 female left)");
				if (parent2ID==-1)
					System.out.println("parent2ID==-1 in SPConstructor.pickMarriedFemaleCouple (1 female left)");
				
				HouseholdPool.addResidentsToHousehold(hhID, newParents);
				
				// updates IndividualPool.availMarriedMalesID and IndividualPool.availMarriedFemalesID, and reconstruct IndividualPool.getdAgeMarriedMatrix.
				IndividualPool.setAvailMarriedFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailMarriedFemalesID(), parent2ID));
			}
			
		} else { // there are at least 2 Married females left
			// initially assumes that the youngest available female is the second eldest
			int idxAvailFemale = IndividualPool.getAvailMarriedFemalesID().length-2;
			// searches only until the second last ID in IndividualPool.availMarriedFemalesID 
			// because the last ID sure belongs to an older individual and we need 2 parents. 
			for (int iFemale=0; iFemale<=IndividualPool.getAvailMarriedFemalesID().length-2; iFemale++) {
				if (IndividualPool.getPool().get(IndividualPool.getAvailMarriedFemalesID()[iFemale])[IndivAttribs.age.getIndex()]>=minAgeParent) {
					idxAvailFemale = iFemale;
					break;
				}
			}
			
			int finRow = idxAvailFemale;
			int finCol = idxAvailFemale+1;
			for (int iFemale=idxAvailFemale; iFemale<=IndividualPool.getAvailMarriedFemalesID().length-2; iFemale++) {
				for (int jFemale=iFemale+1; jFemale<=IndividualPool.getAvailMarriedFemalesID().length-1; jFemale++) {
					if (Math.abs(IndividualPool.getdAgeAvailMarried()[iFemale][jFemale]-ageGap) < 
							Math.abs(IndividualPool.getdAgeAvailMarried()[finRow][finCol]-ageGap)) {
//					if (Math.abs(IndividualPool.getdAgeAvailMarried()[iFemale][jFemale]-HardcodedData.meandAgeMarriedCouple) < 
//							Math.abs(IndividualPool.getdAgeAvailMarried()[finRow][finCol]-HardcodedData.meandAgeMarriedCouple)) {
						finRow = iFemale;
						finCol = jFemale;
					}
				}
			}
			
			parent1ID = IndividualPool.getAvailMarriedFemalesID()[finRow];
			parent2ID = IndividualPool.getAvailMarriedFemalesID()[finCol];
			
			ArrayList<Integer> newParents = new ArrayList<Integer>();
			newParents.add(parent1ID);
			newParents.add(parent2ID);
			
			if (parent1ID==-1)
				System.out.println("parent1ID==-1 in SPConstructor.pickMarriedFemaleCouple (>1 female left)");
			if (parent2ID==-1)
				System.out.println("parent2ID==-1 in SPConstructor.pickMarriedFemaleCouple (>1 female left)");
			
			HouseholdPool.addResidentsToHousehold(hhID, newParents);
			
			// updates IndividualPool.availMarriedMalesID and IndividualPool.availMarriedFemalesID, and reconstruct IndividualPool.getdAgeMarriedMatrix
			IndividualPool.setAvailMarriedFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailMarriedFemalesID(), parent1ID));
			IndividualPool.setAvailMarriedFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailMarriedFemalesID(), parent2ID));
		}
		
		return new int[] {parent1ID, parent2ID};
	}
	

	/**
	 * 
	 * @param hhID
	 * @param minParentAge
	 * @return
	 */
	private static int[] pickMarriedOppositeSexCouple(int hhID, int minParentAge, HholdTypes hhType, int ageGap) {
		// initially assumes that the available valid female is the eldest among the available Married females.
		// notes that IDs in IndividualPool.availMarriedFemalesID is sorted so that age of the corresponding individuals is ascending.
		int idxValidFemale = IndividualPool.getAvailMarriedFemalesID().length-1;
		for (int iFem=0; iFem<=IndividualPool.getAvailMarriedFemalesID().length-1; iFem++) {
			if (IndividualPool.getPool().get(IndividualPool.getAvailMarriedFemalesID()[iFem])[IndivAttribs.age.getIndex()]>=minParentAge) {
				idxValidFemale = iFem;
				break;
			}
		}
		
		int finRow = 0;
		int finCol = idxValidFemale;
		for (int iMale=0; iMale<=IndividualPool.getAvailMarriedMalesID().length-1; iMale++) {
			for (int iFem=idxValidFemale; iFem<=IndividualPool.getAvailMarriedFemalesID().length-1; iFem++) {
				if (Math.abs(IndividualPool.getdAgeAvailMarried()[iMale][iFem]-ageGap) < 
						Math.abs(IndividualPool.getdAgeAvailMarried()[finRow][finCol]-ageGap)) {
//				if (Math.abs(IndividualPool.getdAgeAvailMarried()[iMale][iFem]-HardcodedData.meandAgeMarriedCouple) < 
//						Math.abs(IndividualPool.getdAgeAvailMarried()[finRow][finCol]-HardcodedData.meandAgeMarriedCouple)) {
					finRow = iMale;
					finCol = iFem;
				}
			}
		}
		
		int maleID = IndividualPool.getAvailMarriedMalesID()[finRow];
		int femaleID = IndividualPool.getAvailMarriedFemalesID()[finCol];
		
		ArrayList<Integer> newParents = new ArrayList<Integer>();
		newParents.add(maleID);
		newParents.add(femaleID);
		
		if (maleID==-1)
			System.out.println("maleID==-1 in SPConstructor.pickMarriedOppositeSexCouple");
		if (femaleID==-1)
			System.out.println("femaleID==-1 in SPConstructor.pickMarriedOppositeSexCouple");
		
		HouseholdPool.addResidentsToHousehold(hhID, newParents);
		
		// updates IndividualPool.availMarriedMalesID and IndividualPool.availMarriedFemalesID, and reconstruct IndividualPool.getdAgeMarriedMatrix
		IndividualPool.setAvailMarriedMalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailMarriedMalesID(), maleID));
		IndividualPool.setAvailMarriedFemalesID(ArrayHandler.removeValueInArray(IndividualPool.getAvailMarriedFemalesID(), femaleID));
		
		return new int[] {maleID, femaleID};
	}

	
	/**
	 * 
	 */
	private static void makePools() {
//		// reads tables from databases and assigns them to hhRelMale, hhRelFemale, nHF, nppInHF, and hhComp
//		CensusTables.readTablesFromDBs(dbIndiv, dbHhold, zoneName);
//		System.out.println("\tfinish CensusTables.readTablesFromDBs");
		
//		CensusTables.displayCensusB22();
//		CensusTables.displayCensusB24();
//		CensusTables.displayCensusB25();
//		CensusTables.displayCensusB30();
//		CensusTables.displayMinResidents();

		// generates individual pool from hhRelMale and hhRelFemale
		IndividualPool.makeIndivPool();

		// generates household pool from familyComp and hhComp
		HouseholdPool.makeHholdPool();
		HouseholdPool.setHholdsAllocated(new HashMap<Integer, ArrayList<Integer>>());

		// constructs initial arrays of ID of available 'Married' males and females in indivPool
		IndividualPool.setAvailMarriedMalesID(null);
		IndividualPool.setAvailMarriedFemalesID(null);
		IndividualPool.setAvailMarriedMalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.Married, Genders._male));
		IndividualPool.setAvailMarriedFemalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.Married, Genders._female));
//		System.out.println("\tfinish setAvailMarriedFemalesID");
		
		// constructs initial arrays of ID of available 'LoneParent' males and females in indivPool
		IndividualPool.setAvailLoneParentMalesID(null);
		IndividualPool.setAvailLoneParentFemalesID(null);
		IndividualPool.setAvailLoneParentMalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.LoneParent, Genders._male));
		IndividualPool.setAvailLoneParentFemalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.LoneParent, Genders._female));
//		System.out.println("\tfinish setAvailLoneParentFemalesID");
		
		// constructs initial arrays of ID of available 'U15Child' males and females in indivPool
		IndividualPool.setAvailU15MalesID(null);
		IndividualPool.setAvailU15FemalesID(null);
		IndividualPool.setAvailU15MalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.U15Child, Genders._male));
		IndividualPool.setAvailU15FemalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.U15Child, Genders._female));
//		System.out.println("\tfinish setAvailU15FemalesID");
		
		// constructs initial arrays of ID of available 'Student' males and females in indivPool
		IndividualPool.setAvailStuMalesID(null);
		IndividualPool.setAvailStuFemalesID(null);
		IndividualPool.setAvailStuMalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.Student, Genders._male));
		IndividualPool.setAvailStuFemalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.Student, Genders._female));
//		System.out.println("\tfinish setAvailStuFemalesID");
		
		// constructs initial arrays of ID of available 'O15Child' males and females in indivPool
		IndividualPool.setAvailO15MalesID(null);
		IndividualPool.setAvailO15FemalesID(null);
		IndividualPool.setAvailO15MalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.O15Child, Genders._male));
		IndividualPool.setAvailO15FemalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.O15Child, Genders._female));			
//		System.out.println("\tfinish setAvailO15FemalesID");
		
		// constructs initial arrays of ID of available 'Relative' males and females in indivPool
		IndividualPool.setAvailRelMalesID(null);
		IndividualPool.setAvailRelFemalesID(null);
		IndividualPool.setAvailRelMalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.Relative, Genders._male));
		IndividualPool.setAvailRelFemalesID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.Relative, Genders._female));
//		System.out.println("\tfinish setAvailRelFemalesID");
		
		IndividualPool.setAvailLonePersonID(null);
		IndividualPool.setAvailGrHholdID(null);
		IndividualPool.setAvailLonePersonID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.LonePerson));
		IndividualPool.setAvailGrHholdID(IndividualPool.extractIndivsID(IndividualPool.getPool(), HholdRelSP.GroupHhold));
//		System.out.println("\tfinish setAvailGrHholdID");
		
		// constructs initial dAgeMarriedMatrix
		IndividualPool.constructdAgeAvailMarried();
//		System.out.println("\tfinish constructdAgeAvailMarried()");
	}
}
