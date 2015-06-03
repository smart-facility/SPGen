package core.SPConstructor;

import java.util.ArrayList;
import java.util.HashMap;

import core.ArrayHandler;
import core.HardcodedData;
import core.HardcodedData.AgeGroups;
import core.HardcodedData.B30Rows;
import core.HardcodedData.Genders;
import core.HardcodedData.HholdAttribs;
import core.HardcodedData.HholdRelSP;
import core.HardcodedData.HholdTypes;
import core.HardcodedData.IndivAttribs;
import core.CensusTables.CensusTables;
import core.CensusTables.CensusTablesAnalyser;

public class SPAnalyser {
	
	/**
	 * 
	 * @param oppositeGenders true if only couples of opposite genders are considered, false if otherwise.
	 * @return counts of households having 2 married individuals by the age difference between them. If oppositeGenders is true, the age difference is calculated as the age of the married male minus the age difference of the married female. If oppositeGenders is false, the age difference is the absolute value of age difference between the two married individuals.
	 */
	public static HashMap<Integer,Integer> calculatedAgeCouples(boolean oppositeGenders) {
		HashMap<Integer,Integer> dAgeMap = new HashMap<Integer,Integer>();
		
		for (Integer hhID : HouseholdPool.getHholdsAllocated().keySet()) {
			ArrayList<Integer> residentIDs = HouseholdPool.getHholdsAllocated().get(hhID);
			ArrayList<Integer> marriedIDs = new ArrayList<Integer>();
			for (Integer indivID : residentIDs) {
				if (IndividualPool.getPool().get(indivID)[IndivAttribs.hhRel.getIndex()]==HholdRelSP.Married.getIndex()) {
					marriedIDs.add(indivID); 
				}
			}
			
			if (marriedIDs.size()!=0) {
				if (marriedIDs.size()!=2) {
					System.out.println("hhID " + hhID + " has other than 2 married individuals.");
					continue;
				}
				
				if (oppositeGenders) {// if 2 married individuals have different genders
					if (IndividualPool.getPool().get(marriedIDs.get(0))[IndivAttribs.gender.getIndex()]!=
							IndividualPool.getPool().get(marriedIDs.get(1))[IndivAttribs.gender.getIndex()]) {
						int dAge = IndividualPool.getPool().get(marriedIDs.get(0))[IndivAttribs.age.getIndex()] - 
								IndividualPool.getPool().get(marriedIDs.get(1))[IndivAttribs.age.getIndex()];
						if (IndividualPool.getPool().get(marriedIDs.get(1))[IndivAttribs.gender.getIndex()]==Genders._male.getValue()) {
							dAge = IndividualPool.getPool().get(marriedIDs.get(1))[IndivAttribs.age.getIndex()] - 
									IndividualPool.getPool().get(marriedIDs.get(0))[IndivAttribs.age.getIndex()];
						}
						
						int countdAge = 0;
						if (dAgeMap.containsKey(dAge)) {
							countdAge = dAgeMap.get(dAge);
						}
						countdAge += 1;
						dAgeMap.put(dAge,countdAge);
					}
				} else {
					if (IndividualPool.getPool().get(marriedIDs.get(0))[IndivAttribs.gender.getIndex()]==
							IndividualPool.getPool().get(marriedIDs.get(1))[IndivAttribs.gender.getIndex()]) {
						int dAge = Math.abs(IndividualPool.getPool().get(marriedIDs.get(0))[IndivAttribs.age.getIndex()] - 
								IndividualPool.getPool().get(marriedIDs.get(1))[IndivAttribs.age.getIndex()]);
						int countdAge = 0;
						if (dAgeMap.containsKey(dAge)) {
							countdAge = dAgeMap.get(dAge);
						}
						countdAge += 1;
						dAgeMap.put(dAge,countdAge);
					}
					
				}

			}
			
		}
		
		return dAgeMap;
	}
	
	
	/**
	 * 
	 */
	public static HashMap<Integer,Integer> calcDistribMotherChildAgeGap() {
		HashMap<Integer,Integer> dAgeMap = new HashMap<Integer,Integer>();
		for (Integer hhID : HouseholdPool.getHholdsAllocated().keySet()) {
			int ageYoungerParent = IndividualPool.getAgeYoungerParent((int)hhID);
			
			// there is no valid parent in this household, moves on to the next household
			if (ageYoungerParent==-1) continue; 
			
			ArrayList<Integer> residentIDs = HouseholdPool.getHholdsAllocated().get(hhID);
			for (Integer indivID : residentIDs) {
				if (IndividualPool.getPool().get(indivID)[IndivAttribs.hhRel.getIndex()]==HholdRelSP.U15Child.getIndex() ||
						IndividualPool.getPool().get(indivID)[IndivAttribs.hhRel.getIndex()]==HholdRelSP.Student.getIndex() || 
						IndividualPool.getPool().get(indivID)[IndivAttribs.hhRel.getIndex()]==HholdRelSP.O15Child.getIndex()) {
					
					int dAge = ageYoungerParent - IndividualPool.getPool().get(indivID)[IndivAttribs.age.getIndex()];
					
					Integer countdAge = 0;
					if (dAgeMap.containsKey((Integer)dAge)) {
						countdAge = dAgeMap.get((Integer)dAge);
					}
					countdAge += 1;
					dAgeMap.put((Integer)dAge, countdAge);
				}
			}
			
		}
		return dAgeMap;
	}
	
	
	/**
	 * 
	 * @param hhRelSP
	 * @param gender
	 * @return
	 */
	public static int[] countIndivAllocByAge(HholdRelSP hhRelSP, Genders gender) {
		int[][] hhRel = countIndivAllocByHhRelByAge(gender);
				
		int[] indivCounts = new int[AgeGroups.values().length];
		for (int iAge=0; iAge<=indivCounts.length-1; iAge++) {
			indivCounts[iAge] = hhRel[iAge][hhRelSP.getIndex()];
		}
		
		return indivCounts;
	}
	
	
	/**
	 * 
	 * @param gender
	 * @return int[HholdRelSP.values().length]
	 */
	public static int[] countIndivAllocByHhRel(Genders gender) {
		
		int[] hhRelCounts = new int[HholdRelSP.values().length];
		
		int[][] hhRel = countIndivAllocByHhRelByAge(gender);
		
		for (int iRel=0; iRel<=hhRelCounts.length-1; iRel++) {
			for (int iAge=0; iAge<=hhRel.length-1; iAge++) {
				if (hhRel[iAge][iRel]>=0)
					hhRelCounts[iRel] = hhRelCounts[iRel] + hhRel[iAge][iRel];
			}
		}
		
		return hhRelCounts;
	}
	
	
	/**
	 * 
	 * @param ageGroup
	 * @param gender
	 * @return int[HholdRelSP.values().length]
	 */
	public static int[] countIndivAllocByHhRel(AgeGroups ageGroup, Genders gender) {
		int[] hhRelCounts = new int[HholdRelSP.values().length];
		
		int[][] hhRel = countIndivAllocByHhRelByAge(gender);
		
		for (int iRel=0; iRel<=hhRelCounts.length-1; iRel++) {
			hhRelCounts[iRel] = hhRel[ageGroup.getIndex()][iRel];
		}
			
		return hhRelCounts;
	}
	
	/**
	 * constructs table of counts of people by household relationship by age for the specified gender using IndividualPool.indivPool.
	 * 
	 * @param gender
	 * @return
	 */
	public static int[][] countIndivInPoolByHhRelByAge(Genders gender) {
		int[][] b22SP = new int[AgeGroups.values().length][HholdRelSP.values().length];
		for (Integer indivID : IndividualPool.getPool().keySet()) {
			int[] indivAttribs = IndividualPool.getPool().get(indivID);
			if (gender.getValue()!=indivAttribs[IndivAttribs.gender.getIndex()]) {
				continue;
			}
			
			AgeGroups ageGroup = AgeGroups.getAgeGroup(indivAttribs[IndivAttribs.age.getIndex()]);
			int hhRelIdx = indivAttribs[IndivAttribs.hhRel.getIndex()];
			b22SP[ageGroup.getIndex()][hhRelIdx] += 1;
		}
		
		return b22SP;
	}
	
	
	/**
	 *  constructs table of counts of people by household relationship by age for the specified gender using HouseholdPool.getHholdsAllocated().
	 * @param gender
	 * @return int[AgeGroups.values().length][HholdRelSP.values().length]
	 */
	public static int[][] countIndivAllocByHhRelByAge(Genders gender) {
		int[][] b22SP = new int[AgeGroups.values().length][HholdRelSP.values().length];
		for (Integer hhID : HouseholdPool.getHholdsAllocated().keySet()) {
			ArrayList<Integer> residentsID = HouseholdPool.getHholdsAllocated().get(hhID);
			for (Integer indivID : residentsID) {
				int[] indivAttribs = IndividualPool.getPool().get(indivID);
				if (gender.getValue()!=indivAttribs[IndivAttribs.gender.getIndex()]) {
					continue;
				}
				AgeGroups ageGroup = AgeGroups.getAgeGroup(indivAttribs[IndivAttribs.age.getIndex()]);
				int hhRelIdx = indivAttribs[IndivAttribs.hhRel.getIndex()];
				b22SP[ageGroup.getIndex()][hhRelIdx] += 1;
			}
		}
		return b22SP;
	}
	
	
	/**
	 * constructs counts of households by household type using HouseholdPool.hholdPool.
	 * 
	 * @return
	 */
	public static int[] countHholdInPoolByType() {
		int[] hhCountsByTypes = new int[HholdTypes.values().length];
		
		for (Integer hhID : HouseholdPool.getPool().keySet()) {
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(HouseholdPool.getPool().get(hhID)[HholdAttribs.hhType.getIndex()]);
			hhCountsByTypes[hhType.getIndex()] += 1;
		}
		
		return hhCountsByTypes;
	}
	
	
	/**
	 * constructs counts of household by household type using HouseholdPool.hholdsAllocated.
	 * 
	 * @return int[HholdTypes.values().length]
	 */
	public static int[] countHholdAllocByType() {
		int[] hhCountsByTypes = new int[HholdTypes.values().length];
		for (Integer hhID : HouseholdPool.getHholdsAllocated().keySet()) {
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(HouseholdPool.getPool().get(hhID)[HholdAttribs.hhType.getIndex()]);
			hhCountsByTypes[hhType.getIndex()] += 1;
		}
		return hhCountsByTypes;
	}
	
	
	/**
	 * constructs counts of individual of the specified gender by household type using HouseholdPool.hholdsAllocated.
	 * 
	 * @param gender
	 * @return counts of individuals of given gender allocated to household types. the size of the returned array is HholdTypes.values().length ('NF' to 'Unknown'). 
	 */
	public static int[] countIndivAllocByHhType(Genders gender) {
		int[] indivsByHFs = new int[HholdTypes.values().length];
		
		if (HouseholdPool.getHholdsAllocated()==null) {
			return indivsByHFs;
		}
		
		for (Integer hhID : HouseholdPool.getHholdsAllocated().keySet()) {
			ArrayList<Integer> indivIDs = HouseholdPool.getHholdsAllocated().get(hhID);
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(HouseholdPool.getPool().get(hhID)[HholdAttribs.hhType.getIndex()]);
			for (Integer indivID : indivIDs) {
				if (!IndividualPool.getPool().containsKey(indivID)) {
					System.out.println(indivID + " is not in IndividualPool. IndividualPool.getPool().size()=" + IndividualPool.getPool().size() + 
							", hhType is " + hhType.toString() + ", hhID " + hhID);
				}
				if (gender.getValue()==IndividualPool.getPool().get(indivID)[IndivAttribs.gender.getIndex()]) {
					indivsByHFs[hhType.getIndex()] += 1; 
				}
			}
		}
		
		return indivsByHFs;
	}
	
	/**
	 * extracts the values for family households (HF1 to HF16) from an array of values for all household types, i.e. those in HholdTypes.values() ('NF' to 'Unknown')
	 * @param valuesAllHholdTypes array of values for all household types, i.e. those in HholdTypes.values() ('NF' to 'Unknown')
	 * @return array of values for family households only, i.e. starting from HF1 to HF16.
	 */
	public static int[] extractValuesForFamilyHholds(int[] valuesAllHholdTypes) {
		int[] hfValues = new int[valuesAllHholdTypes.length-2];
		for (int i=0; i<=hfValues.length-1; i++) {
			hfValues[i] = valuesAllHholdTypes[i+1];
		}
		return hfValues;
	}
	
	/**
	 * constructs counts of family household by the number of residents using HouseholdPool.hholdsAllocated.
	 * 
	 * @return int[B30Rows.values().length]
	 */
	public static int[] countHFAllocByHholdSize() {
		int[] hfCounts = new int[B30Rows.values().length];
		
		if (HouseholdPool.getHholdsAllocated()==null) {
			return hfCounts;
		}
		
		for (Integer hhID : HouseholdPool.getHholdsAllocated().keySet()) {
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(HouseholdPool.getPool().get(hhID)[HholdAttribs.hhType.getIndex()]);
			if (hhType.equals(HholdTypes.NF) || hhType.equals(HholdTypes.Unknown)) {
				continue;
			}
			int nResidents = HouseholdPool.getHholdsAllocated().get(hhID).size();
			if (nResidents>0) {
				if (nResidents<=B30Rows.six.getValue()) { // if the number of residents is less than or equal to 6
					hfCounts[B30Rows.getRowFromValue(nResidents).getIndex()] += 1;
				} else { // if the number of residents is greater than 6
					hfCounts[B30Rows.six.getIndex()] += 1;
				}
			}
		}
		
		return hfCounts;
	}


	/**
	 * constructs counts of non-family household by the number of residents using HouseholdPool.hholdsAllocated.
	 * 
	 * @return int[B30Rows.values().length]
	 */
	public static int[] countNFAllocByHholdSize() {
		int[] nfCounts = new int[B30Rows.values().length];
		
		if (HouseholdPool.getHholdsAllocated()==null) {
			return nfCounts;
		}
		
		for (Integer hhID : HouseholdPool.getHholdsAllocated().keySet()) {
			HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(HouseholdPool.getPool().get(hhID)[HholdAttribs.hhType.getIndex()]);
			if (hhType.equals(HholdTypes.NF)) {
				int nResidents = HouseholdPool.getHholdsAllocated().get(hhID).size();
				if (nResidents>0) {
					if (nResidents<=B30Rows.six.getValue()) { // if the number of residents is less than or equal to 6
						nfCounts[B30Rows.getRowFromValue(nResidents).getIndex()] += 1;
					} else { // if the number of residents is greater than 6
						nfCounts[B30Rows.six.getIndex()] += 1;
					}
				}
			}
		}
		
		return nfCounts;
	}
	
	
	/**
	 * increases the current gender count of the given hhType by 1 and calculates the new rms compared to census distribution of this gender count by household type.
	 * 
	 * @param hhType
	 * @param gender
	 * @return
	 */
	public static double calculateRMSIndivCountsByHhTypePlus1(HholdTypes hhType, Genders gender) {
		// gets the counts of allocated individuals (for the gender of this individual) by all household types, including 'NF' and 'Unknown'. 
		int[] countsOfIndivsByHhType = SPAnalyser.countIndivAllocByHhType(gender);
		// increases the counts of individuals in hhType by 1
		countsOfIndivsByHhType[hhType.getIndex()] += 1;
		// extracts values for family households only
		int[] countsOfIndivsByHFs = SPAnalyser.extractValuesForFamilyHholds(countsOfIndivsByHhType);
		// gets the percentage of individual counts of the given gender (indivGender) for each family household type from census 
		double[] censusPercentIndivsByHhType = CensusTablesAnalyser.getPercentIndivsByHhTypes(gender);
		// calculates rms between the new individual counts by family types and the census percentage 
		double rmsHHType = ArrayHandler.calculateRMS(censusPercentIndivsByHhType, ArrayHandler.normaliseArray(countsOfIndivsByHFs));
		
		return rmsHHType;
	}
	
	
	/**
	 * 
	 *  
	 * @param hhID
	 * @return
	 */
	public static double calculateRMSHholdCountsBySizePlus1(int newHhSize) {
		// gets the current counts of family households by size
		int[] hfCountsBySize = SPAnalyser.countHFAllocByHholdSize();
		// increases family counts of size newHhSize by 1
		hfCountsBySize[B30Rows.getRowFromValue(newHhSize).getIndex()] += 1;
		// gets the percentage of family counts by size from census data
		double[] censusPercentHFBySize = CensusTablesAnalyser.getPercentHFByUsualResidents();
		// calculates rms of the new family counts with the census percentage
		double rmsHHSize = ArrayHandler.calculateRMS(censusPercentHFBySize, ArrayHandler.normaliseArray(hfCountsBySize));
		
		return rmsHHSize;
	}
	
	
	/**
	 * 
	 * @param zoneName
	 * @param countRelForZones
	 * @param percentRelForZones
	 * @param gender
	 */
	public static void recordHhRelGenderForZones(String zoneName, 
			ArrayList<String[]> countRelForZones, ArrayList<String[]> percentRelForZones, Genders gender) {
		
		int[] genderCensusByRel = CensusTablesAnalyser.getIndivCensusByHhRel(gender);
		int[] genderAllocByRel = SPAnalyser.countIndivAllocByHhRel(gender);
		
		String[] crnLineCountRelGender = new String[HholdRelSP.values().length*2+1];
		crnLineCountRelGender[0] = zoneName;
		for (int i=0; i<=genderAllocByRel.length-1; i++) {
			crnLineCountRelGender[i*2+1] = Integer.toString(genderCensusByRel[i]);
			crnLineCountRelGender[i*2+2] = Integer.toString(genderAllocByRel[i]);
		}
		countRelForZones.add(crnLineCountRelGender);
		
		if (gender.equals(Genders._female)) {
			GlobalSPOutputHandler.accumulateFemaleCensusByRel(genderCensusByRel);
			GlobalSPOutputHandler.accumulateFemaleAllocByRel(genderAllocByRel);
		} else {
			GlobalSPOutputHandler.accumulateMaleCensusByRel(genderCensusByRel);
			GlobalSPOutputHandler.accumulateMaleAllocByRel(genderAllocByRel);
		}
		
		double[] percentGenderCensusByRel = ArrayHandler.normaliseArray(genderCensusByRel);
		double[] percentGenderAllocByRel = ArrayHandler.normaliseArray(genderAllocByRel);
		
		String[] crnLinePercentRelGender = new String[HholdRelSP.values().length*2+1];
		crnLinePercentRelGender[0] = zoneName;
		for (int i=0; i<=percentGenderAllocByRel.length-1; i++) {
			crnLinePercentRelGender[i*2+1] = Double.toString(percentGenderCensusByRel[i]);
			crnLinePercentRelGender[i*2+2] = Double.toString(percentGenderAllocByRel[i]);
		}
		percentRelForZones.add(crnLinePercentRelGender);
	}
	
	
	/**
	 * 
	 * @param zoneName
	 * @param countGenderByHhType4Zones
	 * @param percentGenderByHhType4Zones
	 * @param gender
	 */
	public static void recordGenderByHhTypeForZones(String zoneName, ArrayList<String[]> countGenderByHhType4Zones, 
			ArrayList<String[]> percentGenderByHhType4Zones, Genders gender) {
		
		int[] genderAllocByHhType = countIndivAllocByHhType(gender);
		// int[] genderCensusByHhType = CensusTablesAnalyser.getSPlikeIndivCountsFromCensusB25(gender);
		int[] genderCensusByHhType = null;
		if (gender.equals(Genders._female))
			genderCensusByHhType = CensusTables.getSpLikeB25Female();
		else if (gender.equals(Genders._male))
			genderCensusByHhType = CensusTables.getSpLikeB25Male();
		// replaces the value for NF by the count LonePerson and GroupHhold individuals of this gender (from table B22)
		int[] genderCensusByRel = CensusTablesAnalyser.getIndivCensusByHhRel(gender);
		genderCensusByHhType[HholdTypes.NF.getIndex()] = genderCensusByRel[HholdRelSP.LonePerson.getIndex()] + genderCensusByRel[HholdRelSP.GroupHhold.getIndex()];
		
		String[] crnLineGenderByHhType = new String[HholdTypes.values().length*2+1];
		crnLineGenderByHhType[0] = zoneName;
		for (int i=0; i<=genderAllocByHhType.length-1; i++) {
			crnLineGenderByHhType[i*2+1] = Integer.toString(genderCensusByHhType[i]);
			crnLineGenderByHhType[i*2+2] = Integer.toString(genderAllocByHhType[i]);
		}
		countGenderByHhType4Zones.add(crnLineGenderByHhType);
		
		if (gender.equals(Genders._female)) {
			GlobalSPOutputHandler.accumulateFemaleCensusByHhType(genderCensusByHhType);
			GlobalSPOutputHandler.accumulateFemaleAllocByHhType(genderAllocByHhType);
		} else {
			GlobalSPOutputHandler.accumulateMaleCensusByHhType(genderCensusByHhType);
			GlobalSPOutputHandler.accumulateMaleAllocByHhType(genderAllocByHhType);
		}
		
		double[] percentGenderCensusByHhType = ArrayHandler.normaliseArray(genderCensusByHhType);
		double[] percentGenderAllocByHhType = ArrayHandler.normaliseArray(genderAllocByHhType);
		
		String[] crnLinePercentGenderByHhType = new String[HholdTypes.values().length*2+1];
		crnLinePercentGenderByHhType[0] = zoneName;
		for (int i=0; i<=percentGenderAllocByHhType.length-1; i++) {
			crnLinePercentGenderByHhType[i*2+1] = Double.toString(percentGenderCensusByHhType[i]);
			crnLinePercentGenderByHhType[i*2+2] = Double.toString(percentGenderAllocByHhType[i]);
		}
		percentGenderByHhType4Zones.add(crnLinePercentGenderByHhType);
	}
	
	
	/**
	 * 
	 * @param zoneName
	 * @param hhCountByTypeForZones
	 * @param percentHhCountByTypeForZones
	 */
	public static void recordHhCountByTypeForZones(String zoneName, ArrayList<String[]> hhCountByTypeForZones, ArrayList<String[]> percentHhCountByTypeForZones) {
		
		int[] hhAllocByType = countHholdAllocByType();
		//int[] hhCensusByType = CensusTablesAnalyser.getSPlikeHhCountsTableFromCensusB24();
		int[] hhCensusByType = CensusTables.getSpLikeB24();
		// replaces the value for NF by the count of NF households from table B30.
		//hhCensusByType[HholdTypes.NF.getIndex()] = ArrayHandler.sumPositiveInArray(CensusTablesAnalyser.getSPlikeNFCountsFromCensusB30());
		hhCensusByType[HholdTypes.NF.getIndex()] = ArrayHandler.sumPositiveInArray(CensusTables.getSpLikeB30NF());
		
		String[] crnLineHhCountByType = new String[HholdTypes.values().length*2+1];
		crnLineHhCountByType[0] = zoneName;
		for (int i=0; i<=hhAllocByType.length-1; i++) {
			crnLineHhCountByType[i*2+1] = Integer.toString(hhCensusByType[i]);
			crnLineHhCountByType[i*2+2] = Integer.toString(hhAllocByType[i]);
		}
		hhCountByTypeForZones.add(crnLineHhCountByType);
		
		GlobalSPOutputHandler.accumulateHholdCensusByType(hhCensusByType);
		GlobalSPOutputHandler.accumulateHholdAllocByType(hhAllocByType);
		
		double[] percentHhAllocByType = ArrayHandler.normaliseArray(hhAllocByType);
		double[] percentHhCensusByType = ArrayHandler.normaliseArray(hhCensusByType);
		
		String[] crnLinePercentHhCountByType = new String[HholdTypes.values().length*2+1];
		crnLinePercentHhCountByType[0] = zoneName;
		for (int i=0; i<=hhAllocByType.length-1; i++) {
			crnLinePercentHhCountByType[i*2+1] = Double.toString(percentHhCensusByType[i]);
			crnLinePercentHhCountByType[i*2+2] = Double.toString(percentHhAllocByType[i]);
		}
		percentHhCountByTypeForZones.add(crnLinePercentHhCountByType);
	}
	
	
	/**
	 * 
	 * @param zoneName
	 * @param countHFBySizeForZones
	 * @param percentHFBySizeForZones
	 */
	public static void recordHFCountBySizeForZones(String zoneName, ArrayList<String[]> countHFBySizeForZones, ArrayList<String[]> percentHFBySizeForZones) {
		//int[] hfCensusBySize = CensusTablesAnalyser.getSPlikeHFCountsFromCensusB30();
		int[] hfCensusBySize = CensusTables.getSpLikeB30HF();
		int[] hfAllocBySize = countHFAllocByHholdSize();
		// replaces the value in hfCensusBySize corresponding to 1 resident to 0, instead of -1.
		hfCensusBySize[B30Rows.one.getIndex()] = 0;
		
		String[] crnLineHFCountBySize = new String[B30Rows.values().length*2+1];
		crnLineHFCountBySize[0] = zoneName;
		for (int i=0; i<=hfAllocBySize.length-1; i++) {
			crnLineHFCountBySize[i*2+1] = Integer.toString(hfCensusBySize[i]);
			crnLineHFCountBySize[i*2+2] = Integer.toString(hfAllocBySize[i]);
		}
		countHFBySizeForZones.add(crnLineHFCountBySize);
		
		GlobalSPOutputHandler.accumulateHFCensusBySize(hfCensusBySize);
		GlobalSPOutputHandler.accumulateHFAllocBySize(hfAllocBySize);
		
		double[] percentHFCensusBySize = ArrayHandler.normaliseArray(hfCensusBySize);
		double[] percentHFAllocBySize = ArrayHandler.normaliseArray(hfAllocBySize);
		
		String[] crnLinePercentHFCountBySize = new String[B30Rows.values().length*2+1];
		crnLinePercentHFCountBySize[0] = zoneName;
		for (int i=0; i<=percentHFAllocBySize.length-1; i++) {
			crnLinePercentHFCountBySize[i*2+1] = Double.toString(percentHFCensusBySize[i]);
			crnLinePercentHFCountBySize[i*2+2] = Double.toString(percentHFAllocBySize[i]);
		}
		percentHFBySizeForZones.add(crnLinePercentHFCountBySize);
	}
	
	
	/**
	 * 
	 * @param zoneName
	 * @param countNFBySizeForZones
	 * @param percentNFBySizeForZones
	 */
	public static void recordNFCountBySizeForZones(String zoneName, ArrayList<String[]> countNFBySizeForZones, ArrayList<String[]> percentNFBySizeForZones) {
		//int[] nfCensusBySize = CensusTablesAnalyser.getSPlikeNFCountsFromCensusB30();
		int[] nfCensusBySize = CensusTables.getSpLikeB30NF();
		int[] nfAllocBySize = countNFAllocByHholdSize();
		
		String[] crnLineNFCountBySize = new String[B30Rows.values().length*2+1];
		crnLineNFCountBySize[0] = zoneName;
		for (int i=0; i<=nfAllocBySize.length-1; i++) {
			crnLineNFCountBySize[i*2+1] = Integer.toString(nfCensusBySize[i]);
			crnLineNFCountBySize[i*2+2] = Integer.toString(nfAllocBySize[i]);
		}
		countNFBySizeForZones.add(crnLineNFCountBySize);
		
		GlobalSPOutputHandler.accumulateNFCensusBySize(nfCensusBySize);
		GlobalSPOutputHandler.accumulateNFAllocBySize(nfAllocBySize);
		
		double[] percentNFCensusBySize = ArrayHandler.normaliseArray(nfCensusBySize);
		double[] percentNFAllocBySize = ArrayHandler.normaliseArray(nfAllocBySize);
		
		String[] crnLinePercentNFCountBySize = new String[B30Rows.values().length*2+1];
		crnLinePercentNFCountBySize[0] = zoneName;
		for (int i=0; i<=percentNFAllocBySize.length-1; i++) {
			crnLinePercentNFCountBySize[i*2+1] = Double.toString(percentNFCensusBySize[i]);
			crnLinePercentNFCountBySize[i*2+2] = Double.toString(percentNFAllocBySize[i]);
		}
		percentNFBySizeForZones.add(crnLinePercentNFCountBySize);
	}
}
