package core.SPConstructor;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;

import core.ArrayHandler;
import core.HardcodedData;
import core.CensusTables.CensusTables;
import core.CensusTables.CensusTablesAnalyser;
import core.HardcodedData.AgeGroups;
import core.HardcodedData.Genders;
import core.HardcodedData.B22Cols;
import core.HardcodedData.HholdAttribs;
import core.HardcodedData.HholdRelSP;
import core.HardcodedData.HholdTypes;
import core.HardcodedData.IndivAttribs;
import core.HardcodedData.MinResidentCols;

public class IndividualPool {
	private static HashMap<Integer, int[]> indivPool;
	
	private static int[] availMarriedMalesID;
	private static int[] availMarriedFemalesID;
	private static int[][] dAgeAvailMarried; // the row of this matrix represents Married males, the column of this matrix represents Married females
	private static int[] availLoneParentMalesID;
	private static int[] availLoneParentFemalesID;
	private static int[] availU15MalesID;
	private static int[] availU15FemalesID;
	private static int[] availStuMalesID;
	private static int[] availStuFemalesID;
	private static int[] availO15MalesID;
	private static int[] availO15FemalesID;
	private static int[] availRelMalesID;
	private static int[] availRelFemalesID;
	private static int[] availLonePersonID;
	private static int[] availGrHholdID;
	
	private static boolean havingAvailMarriedOppositeSex;
	private static boolean havingAvailMarriedMaleOnly;
	private static boolean havingAvailMarriedFemaleOnly;
	private static boolean notHavingAvailMarried;
	
	
	/**
	 * 
	 */
	public static void makeIndivPool() {
		int indivCount = 0;
		indivPool = new HashMap<Integer, int[]>();

		int[] indivIndices = ArrayHandler.toInt(indivPool.keySet());
		if (ArrayHandler.getLength(indivIndices)>0) {
			indivCount = ArrayHandler.getLength(indivIndices)+1;
		}
		
//		for (AgeGroups ageGroup : AgeGroups.values()) {
//			for (B22Cols hhRelCol : B22Cols.values()) {
//				if (hhRelCol.equals(B22Cols.id) || hhRelCol.equals(B22Cols.year_1) || 
//						hhRelCol.equals(B22Cols.year_2) || hhRelCol.equals(B22Cols.Visitor) || hhRelCol.equals(B22Cols.note)) {
//					continue;
//				}
//				
//				int nMales = CensusTables.getHhRelMale()[ageGroup.getIndex()][hhRelCol.getIndex()];
//				for (int iMale=1; iMale<=nMales; iMale++) {
//					indivCount = indivCount + 1;
//					int[] indAttrib = makeAttribsOfNewIndiv(ageGroup.getAge(), Genders._male, hhRelCol.getHhRelSP());
//					indivPool.put(indivCount, indAttrib);
//				}
//				
//				int nFemales = CensusTables.getHhRelFemale()[ageGroup.getIndex()][hhRelCol.getIndex()];
//				for (int iFemale = 1; iFemale<=nFemales; iFemale++) {
//					indivCount = indivCount + 1;
//					int[] indAttrib = makeAttribsOfNewIndiv(ageGroup.getAge(), Genders._female, hhRelCol.getHhRelSP());
//					indivPool.put(indivCount, indAttrib);
//				}
//			}
//		}
		
		for (AgeGroups ageGroup : AgeGroups.values()) {
			for (HholdRelSP hhRel : HholdRelSP.values()) {
				int nMales = CensusTables.getSpLikeB22Male()[ageGroup.getIndex()][hhRel.getIndex()];
				for (int iMale=1; iMale<=nMales; iMale++) {
					indivCount = indivCount + 1;
					int[] indAttrib = makeAttribsOfNewIndiv(ageGroup.getAge(), Genders._male, hhRel);
					indivPool.put(indivCount, indAttrib);
				}
				
				int nFemales = CensusTables.getSpLikeB22Female()[ageGroup.getIndex()][hhRel.getIndex()];
				for (int iFemale=1; iFemale<=nFemales; iFemale++) {
					indivCount = indivCount + 1;
					int[] indAttrib = makeAttribsOfNewIndiv(ageGroup.getAge(), Genders._female, hhRel);
					indivPool.put(indivCount, indAttrib);
				}
			}
		}
		
	}
	

	/**
	 * 
	 * @param age
	 * @param gender
	 * @param hhRelSP
	 * @return
	 */
	public static int[] makeAttribsOfNewIndiv(int age, Genders gender, HholdRelSP hhRelSP) {
		int[] indAttrib = new int[IndivAttribs.values().length];
		
		indAttrib[IndivAttribs.gender.getIndex()] = gender.getValue();
		indAttrib[IndivAttribs.hhRel.getIndex()] = hhRelSP.getIndex();
		indAttrib[IndivAttribs.age.getIndex()] = age;
		
		if (hhRelSP.equals(HholdRelSP.LoneParent) && indAttrib[IndivAttribs.age.getIndex()]<HardcodedData.ageOfConsent)
			indAttrib[IndivAttribs.age.getIndex()] = HardcodedData.ageOfConsent;
		if (hhRelSP.equals(HholdRelSP.Married) && indAttrib[IndivAttribs.age.getIndex()]<HardcodedData.minAgeMarried)
			indAttrib[IndivAttribs.age.getIndex()] = HardcodedData.minAgeMarried;
		
		return indAttrib;
	}
	
	
	/**
	 * adds a new individual with attributes indivAttrib to indivPool.
	 * ID of the new individual is the maximum ID available indivPool plus 1.
	 * @param indivAttrib
	 * @return ID of the new individual in indivPool
	 */
	public static int addIndivToPool(int[] indivAttrib) {
		int[] indivIDs = ArrayHandler.toInt(indivPool.keySet());
		
		int newIndivID = 1;
		if (indivIDs!=null && indivIDs.length>0) 
			newIndivID = indivIDs[ArrayHandler.getIndexOfMax(indivIDs)]+1;
		
		indivPool.put(newIndivID,indivAttrib);
		
		return newIndivID;
	}
	
	
	/**
	 * 
	 * @param indivID
	 * @param newIndivAttrib
	 */
	public static void updateIndivInPool(int indivID, int[] newIndivAttrib) {
		indivPool.put(indivID, newIndivAttrib);
	}
	
	
	/**
	 * 
	 * @param hhRel
	 * @return
	 */
	public static int[] extractIndivsID(HashMap<Integer,int[]> pool, HholdRelSP hhRel) {
		ArrayList<Integer> listIDs = new ArrayList<Integer>();
		for (Integer indivID : pool.keySet()) {
			if (pool.get(indivID)[IndivAttribs.hhRel.getIndex()]==hhRel.getIndex()) {
				listIDs.add(indivID);
			}
		}
		return ArrayHandler.toInt(listIDs);
	}
	
	
	/**
	 * 
	 * @param hhRel
	 * @param gender
	 * @return
	 */
	public static int[] extractIndivsID(HashMap<Integer,int[]> pool, HholdRelSP hhRel, Genders gender) {
		ArrayList<Integer> listIDs = new ArrayList<Integer>();
		for (Integer indivID : pool.keySet()) {
			if (pool.get(indivID)[IndivAttribs.hhRel.getIndex()]==hhRel.getIndex() &&
					pool.get(indivID)[IndivAttribs.gender.getIndex()]==gender.getValue()) {
				listIDs.add(indivID);
			}
		}
		return ArrayHandler.toInt(listIDs);
	}
	
	
	/**
	 * 
	 */
	public static void constructdAgeAvailMarried() {
		havingAvailMarriedMaleOnly = false;
		havingAvailMarriedFemaleOnly = false;
		havingAvailMarriedOppositeSex = false;
		notHavingAvailMarried = false;
		
		if (ArrayHandler.getLength(availMarriedMalesID)!=0 && ArrayHandler.getLength(availMarriedFemalesID)==0) { // only 'Married' males available in indivPool, i.e. male couples only
			havingAvailMarriedMaleOnly = true;
			
			dAgeAvailMarried = new int[availMarriedMalesID.length][availMarriedMalesID.length];
			
			for (int iMale=0; iMale<=availMarriedMalesID.length-1; iMale++) {
				int ageiMale = indivPool.get(availMarriedMalesID[iMale])[IndivAttribs.age.getIndex()];
				for (int jMale=iMale+1; jMale<=availMarriedMalesID.length-1; jMale++) {
					int agejMale = indivPool.get(availMarriedMalesID[jMale])[IndivAttribs.age.getIndex()];
					dAgeAvailMarried[iMale][jMale] = Math.abs(ageiMale - agejMale);
					dAgeAvailMarried[jMale][iMale] = Math.abs(ageiMale - agejMale);
				}
			}
			
		} else if (ArrayHandler.getLength(availMarriedMalesID)==0 && ArrayHandler.getLength(availMarriedFemalesID)!=0) { // only 'Married' females available in indivPool, i.e. female couples only
			havingAvailMarriedFemaleOnly = true;
			
			dAgeAvailMarried = new int[availMarriedFemalesID.length][availMarriedFemalesID.length];
			
			for (int iFem=0; iFem<=availMarriedFemalesID.length-1; iFem++) {
				int ageiFem = indivPool.get(availMarriedFemalesID[iFem])[IndivAttribs.age.getIndex()];
				for (int jFem=iFem+1; jFem<=availMarriedFemalesID.length-1; jFem++) {
					int agejFem = indivPool.get(availMarriedFemalesID[jFem])[IndivAttribs.age.getIndex()];
					dAgeAvailMarried[iFem][jFem] = Math.abs(ageiFem - agejFem);
					dAgeAvailMarried[jFem][iFem] = Math.abs(ageiFem - agejFem);
				}
			}
			
		} else if(ArrayHandler.getLength(availMarriedMalesID)!=0 && ArrayHandler.getLength(availMarriedFemalesID)!=0) { // both 'Married' males and females available in indivPool
			havingAvailMarriedOppositeSex = true;
			
			dAgeAvailMarried = new int[availMarriedMalesID.length][availMarriedFemalesID.length];
			
			for (int iMale=0; iMale<=availMarriedMalesID.length-1; iMale++) {
				int ageMale = indivPool.get(availMarriedMalesID[iMale])[IndivAttribs.age.getIndex()];
				for (int iFem=0; iFem<=availMarriedFemalesID.length-1; iFem++) {
					int ageFemale = indivPool.get(availMarriedFemalesID[iFem])[IndivAttribs.age.getIndex()];
					dAgeAvailMarried[iMale][iFem] = ageMale - ageFemale;
				}
			}
		} else { // no 'Married' individuals at all in indivPool
			notHavingAvailMarried = true;
		}
	}
	

	/**
	 * removes individuals with ID in indivIDs from indivPool, updates SPAnalyser.hhRelMale and hhRelFemale accordingly.
	 * 
	 * @param indivIDs
	 * @return the remaining of indivIDs (which should normally be null).
	 */
	public static int[] removesIndivsFromIndivPool(int[] indivIDs) {
		if (indivIDs==null) return null;
		
		int[] returnIDs = new int[indivIDs.length];
		for (int i=0; i<=indivIDs.length-1; i++) {
			returnIDs[i] = indivIDs[i];
		}
		
		for (int i=0; i<=indivIDs.length-1; i++) {
			Genders gender = Genders.getGenderByValue(indivPool.get(indivIDs[i])[IndivAttribs.gender.getIndex()]);
			AgeGroups ageGroup = AgeGroups.getAgeGroup(indivPool.get(indivIDs[i])[IndivAttribs.age.getIndex()]);
			HholdRelSP hhRel = HholdRelSP.getHholdRelSPByIndex(indivPool.get(indivIDs[i])[IndivAttribs.hhRel.getIndex()]);
			
			// checks if hhRel of selectedGender of ageGroup is valid
			boolean isModifiable = CensusTablesAnalyser.isRelAndAgeModifiable(hhRel, gender, ageGroup);
			
			if (isModifiable) {
				indivPool.remove(indivIDs[i]);
				returnIDs = ArrayHandler.removeValueInArray(returnIDs, indivIDs[i]);
			}
		}
		
		return returnIDs;
	}
	
	
	/**
	 * determines the age group of a new individual of a given household relationship (hhRel) 
	 * and a given gender for minimal deviation of both row distribution and column distribution in census table b22.
	 * 
	 * @param hhRel household relationship of the new individual 
	 * @param gender gender of the new individual
	 * @return age group of the new individual
	 */
	private static AgeGroups determineAgeGroupOfNewIndiv(HholdRelSP hhRel, Genders gender) {
		int[] countsByAge = SPAnalyser.countIndivAllocByAge(hhRel, gender);
		double[] percentByAge = CensusTablesAnalyser.getPercentbyAgeGroups(hhRel, gender);
		
		double[] largerRMS = new double[countsByAge.length];
		
		for (int iAge=0; iAge<=countsByAge.length-1; iAge++) {
			// if the hhRel (i.e. Relative, Married, etc.) is not allowed in this age group, moves on to the next age group
			if (percentByAge[iAge]<0) {
				largerRMS[iAge] = -1;
				continue;
			}
			
			// increases the count in this age group by 1 and calculates the RMS of the new countsByAge and percentByAge (from census)
			countsByAge[iAge] += 1;
			double rmsCol = ArrayHandler.calculateRMS(percentByAge, ArrayHandler.normaliseArray(countsByAge));
			// then puts countsByAge[iAge] back to the normal/current value
			countsByAge[iAge] -= 1;
			
			// gets the current counts of individuals by HholdType of this age group
			int[] countsByHhRel = SPAnalyser.countIndivAllocByHhRel(AgeGroups.getAgeGroupByIndex(iAge), gender);
			// gets the census distribution of hhold relationship of this age group
			double[] percentByHhRel = CensusTablesAnalyser.getPercentByHhRels(AgeGroups.getAgeGroupByIndex(iAge), gender);
			
			// increases the counts of the hhRel being studied by 1 and calculates the RMS of the new countsByHhRel and percentByHhRel
			countsByHhRel[hhRel.getIndex()] += 1;
			double rmsRow = ArrayHandler.calculateRMS(percentByHhRel, ArrayHandler.normaliseArray(countsByHhRel));
			// then puts countsByHhRel[HholdRelSP.Relative.getIndex()] back to normal/previous value
			countsByHhRel[hhRel.getIndex()] -= 1;
			
			largerRMS[iAge] = Math.max(rmsCol, rmsRow);
		}
		
		int iAgeMin = ArrayHandler.getIndexOfPositiveMin(largerRMS);
		
		if (iAgeMin==-1) {
			return null;
		} else {
			return AgeGroups.getAgeGroupByIndex(iAgeMin);
		}
	}
	
	
	/**
	 * 
	 * @param hhRel
	 * @param gender
	 * @param minAge
	 * @param maxAge
	 * @return
	 */
	private static AgeGroups determineAgeGroupOfNewIndiv(HholdRelSP hhRel, Genders gender, int minAge, int maxAge) {
		AgeGroups minAgeGroup = AgeGroups.getAgeGroup(minAge);
		AgeGroups maxAgeGroup = AgeGroups.getAgeGroup(maxAge);
		if (maxAgeGroup.getMaxAge()<minAgeGroup.getMinAge()) return null;
		
		int[] countsByAge = SPAnalyser.countIndivAllocByAge(hhRel, gender);
		double[] percentByAge = CensusTablesAnalyser.getPercentbyAgeGroups(hhRel, gender);
		double[] largerRMS = new double[maxAgeGroup.getIndex() - minAgeGroup.getIndex() + 1];
		
		for (int iAge=minAgeGroup.getIndex(); iAge<=maxAgeGroup.getIndex(); iAge++) {
			// if the hhRel (i.e. Relative, Married, etc.) is not allowed in this age group, moves on to the next age group
			if (percentByAge[iAge]<0) {
				largerRMS[iAge-minAgeGroup.getIndex()] = -1;
				continue;
			}
			
			// increases the count in this age group by 1 and calculates the RMS of the new countsByAge and percentByAge (from census)
			countsByAge[iAge] += 1;
			double rmsCol = ArrayHandler.calculateRMS(percentByAge, ArrayHandler.normaliseArray(countsByAge));
			// then puts countsByAge[iAge] back to the normal/current value
			countsByAge[iAge] -= 1;
			
			// gets the current counts of individuals by HholdType of this age group
			int[] countsByHhRel = SPAnalyser.countIndivAllocByHhRel(AgeGroups.getAgeGroupByIndex(iAge), gender);
			// gets the census distribution of hhold relationship of this age group
			double[] percentByHhRel = CensusTablesAnalyser.getPercentByHhRels(AgeGroups.getAgeGroupByIndex(iAge), gender);
			
			// increases the counts of the hhRel being studied by 1 and calculates the RMS of the new countsByHhRel and percentByHhRel
			countsByHhRel[hhRel.getIndex()] += 1;
			double rmsRow = ArrayHandler.calculateRMS(percentByHhRel, ArrayHandler.normaliseArray(countsByHhRel));
			// then puts countsByHhRel[HholdRelSP.Relative.getIndex()] back to normal/previous value
			countsByHhRel[hhRel.getIndex()] -= 1;
			
			largerRMS[iAge-minAgeGroup.getIndex()] = Math.max(rmsCol, rmsRow);
		}
		
		int iAgeMin = ArrayHandler.getIndexOfPositiveMin(largerRMS);
		
		if (iAgeMin==-1) {
			return null;
		} else {
			return AgeGroups.getAgeGroupByIndex(iAgeMin+minAgeGroup.getIndex());
		}
	}
	
	
	/**
	 * determines if adding a new male or female of a given household relationship (hhRel) will better reserves the distribution of total individual counts (of each gender) by household relationship
	 * @param hhRel
	 * @return
	 */
	private static Genders determineGenderOfNewIndiv(HholdRelSP hhRel) {
		double[] percentCensusMalesByRels = CensusTablesAnalyser.getPercentByHhRels(Genders._male);
		double[] percentCensusFemalesByRels = CensusTablesAnalyser.getPercentByHhRels(Genders._female);

		int[] countSPMalesByRels = SPAnalyser.countIndivAllocByHhRel(Genders._male);
		countSPMalesByRels[hhRel.getIndex()] = countSPMalesByRels[hhRel.getIndex()] + 1;
		int[] countSPFemalesByRels = SPAnalyser.countIndivAllocByHhRel(Genders._female);
		countSPFemalesByRels[hhRel.getIndex()] = countSPFemalesByRels[hhRel.getIndex()] + 1;

		double[] percentSPMalesByRels = ArrayHandler.normaliseArray(countSPMalesByRels);
		double[] percentSPFemalesByRels = ArrayHandler.normaliseArray(countSPFemalesByRels);

		double rmsMalesByRels = ArrayHandler.calculateRMS(percentSPMalesByRels, percentCensusMalesByRels);
		double rmsFemalesByRels = ArrayHandler.calculateRMS(percentSPFemalesByRels, percentCensusFemalesByRels);
		
		Genders selectedGender = null;
		
		if (rmsMalesByRels<rmsFemalesByRels) { // adding a male to individual pool causes less deviation to the distribution of relationship in census data
			selectedGender = Genders._male;
		} else { // adding a female to individual pool causes less deviation to the distribution of relationship in census data
			selectedGender = Genders._female;
		}
		
		return selectedGender;
	}
	
	
	/**
	 * constructs one individual of given household relationship (hhRel).
	 * The gender and age of the new individual is determined so that they minimise the RMS of distributions of household relationship by age of each gender (Table b22).
	 * adds the new individual to IndividualPool.indivPool, and updates SPAnalyser.hhRelMale or hhRelFemale accordingly.
	 * 
	 * @param hhRel
	 * @return id of the new individual, -1 if the new individual cannot be constructed.
	 */
	public static int constructOneNewIndividual(HholdRelSP hhRel) {
		int newIndivID = -1;
		
		// adds another individual with relationship hhRel to indivPool
		// the gender and age of new individual is determined so that 
		// they minimise the RMS of distributions of household relationship by age of each gender(Table b22).
		
		Genders selectedGender = determineGenderOfNewIndiv(hhRel);
		
		AgeGroups ageGroup = determineAgeGroupOfNewIndiv(hhRel, selectedGender);
		// checks if hhRel of selectedGender of ageGroup is valid
		boolean addingSuccessful = CensusTablesAnalyser.isRelAndAgeModifiable(hhRel, selectedGender, ageGroup);
		if (addingSuccessful) {
			// adds the new individual with given hhRel, ageGroup, and gender to indivPool
			int[] indivAttrib = IndividualPool.makeAttribsOfNewIndiv(ageGroup.getAge(), selectedGender, hhRel);
			newIndivID = IndividualPool.addIndivToPool(indivAttrib);
		}
		
		return newIndivID;
	}
	
	
	/**
	 * constructs one individual of given household relationship (hhRel).
	 * The gender and age of the new individual is determined so that they minimise the RMS of distributions of household relationship by age of each gender (Table b22).
	 * the age is also bounded by minAge and maxAge.
	 * adds the new individual to IndividualPool.indivPool, and updates SPAnalyser.hhRelMale or hhRelFemale accordingly.
	 * 
	 * @param hhRel
	 * @param minAge
	 * @param maxAge
	 * @return id of the new individual, -1 if new indvidual cannot be constructed.
	 */
	public static int constructOneNewIndividual(HholdRelSP hhRel, int minAge, int maxAge) {
		int newIndivID = -1;
		
		// adds another individual with relationship hhRel to indivPool
		// the gender and age of new individual is determined so that 
		// they minimise the RMS of distributions of household relationship by age of each gender(Table b22).
		Genders selectedGender = determineGenderOfNewIndiv(hhRel);
		
		AgeGroups ageGroup = null;
		if (hhRel.equals(HholdRelSP.U15Child)) {
			ageGroup = AgeGroups._0_14;
		} else if (hhRel.equals(HholdRelSP.Student)) {
			ageGroup = AgeGroups._15_24;
		} else {
			if (minAge<HardcodedData.minAgeO15)
				minAge = HardcodedData.minAgeO15;
			if (maxAge<HardcodedData.minAgeO15)
				maxAge = HardcodedData.minAgeO15;
				
			ageGroup = determineAgeGroupOfNewIndiv(hhRel, selectedGender, minAge, maxAge);
		}
		
		if (ageGroup==null) 
			return newIndivID;
		// checks if hhRel of selectedGender of ageGroup is valid
		boolean addingSuccessful = CensusTablesAnalyser.isRelAndAgeModifiable(hhRel, selectedGender, ageGroup);
		if (addingSuccessful) {
			int age;
			if (maxAge<ageGroup.getMinAge()) {
				age = ageGroup.getMinAge();
			} else if (minAge>ageGroup.getMaxAge()) {
				age = ageGroup.getMaxAge();
			} else {
				age = ArrayHandler.generateRandomInt(Math.max(minAge,ageGroup.getMinAge()), Math.min(maxAge,ageGroup.getMaxAge()), HardcodedData.random);
			}
			// adds the new individual with given hhRel, ageGroup, and gender to indivPool
			int[] indivAttrib = IndividualPool.makeAttribsOfNewIndiv(age, selectedGender, hhRel);
			newIndivID = IndividualPool.addIndivToPool(indivAttrib);
			
//			System.out.println("newIndivConstructed, " + newIndivID + ", " + hhRel.toString() + ", " + 
//															age + " (" + minAge + "," + maxAge + ")" + ", " + selectedGender.toString());
		}
		
		
		return newIndivID;
	}
	
	
	/**
	 * 
	 * @param idList
	 * @return
	 */
	private static int[] sortIndivIDsByAscendingAge(int[] idList) {
		if (idList==null) return null;
		
		int[] ageList = new int[idList.length];
		for (int i=0; i<=idList.length-1; i++) {
			ageList[i] = indivPool.get(idList[i])[IndivAttribs.age.getIndex()];
		}
		
		int[] sortedAgeIndex = ArrayHandler.sortedIndices(ageList);
		
		int[] sortedIDList = new int[idList.length];
		for (int i=0; i<=sortedAgeIndex.length-1; i++) {
			sortedIDList[i] = idList[sortedAgeIndex[i]];
		}
		
		return sortedIDList;
	}
	
	
	
	/**
	 * if age of childID is outside the acceptable age range (determined based on minAgeParent), 
	 * reassigns the age to upper bound or lower bound of the child age group. 
	 * @param childID
	 * @param minAgeParent
	 */
	public static void correctChildAgeToBestMatchParents(int childID, int minAgeParent) {
		// determines if age of this individual is acceptable to the age of the younger parent
		int[] childAgeBounds = calculateChildAgeBounds(minAgeParent);
		int childAge = indivPool.get((Integer)childID)[IndivAttribs.age.getIndex()];
		// if the age of childID is younger than lower bound of the acceptable age range
		// reassigns the age of this child to the upper bound of his/her age group (randomly minus 0 or 1)
		if (childAge<childAgeBounds[0]) {
			indivPool.get((Integer)childID)[IndivAttribs.age.getIndex()] = AgeGroups.getAgeGroup(childAge).getMaxAge() - HardcodedData.random.nextInt(2);
		} 
		// if the age of childID is older than the upper bound of the acceptable age range
		// re assigns the age of this child to lower bound of his/her age group (randomly plus 0 or 1)
		else if (childAge>childAgeBounds[1]) {
			indivPool.get((Integer)childID)[IndivAttribs.age.getIndex()] = AgeGroups.getAgeGroup(childAge).getMinAge() + HardcodedData.random.nextInt(2);
		}
	}
	
	
	/**
	 * determines if childAge is within the bound calculated based on minAgeParent (using calculateChildAgeBounds(int minAgeParent)).
	 * if childAge is smaller than lower bound, returns the age difference between the lower bound and the upper limit of his/her age group.
	 * if childAge is larger than upper bound returns the age difference between the upper bound and the lower limit of his/her age group.
	 * if childAge in within bound, return null;
	 * 
	 * @param childAge
	 * @param minAgeParent
	 * @return
	 */
	public static Integer correctAgeAndCalculateDiffToChildAgeBound(int childAge, int minAgeParent) {
		// determines if age of this individual is acceptable to the age of the younger parent
		int[] childAgeBounds = calculateChildAgeBounds(minAgeParent);
		
		// if the age of childID is younger than lower bound of the acceptable age range
		if (childAge<childAgeBounds[0]) {
			return (Integer)(childAgeBounds[0] - AgeGroups.getAgeGroup(childAge).getMaxAge());
		}
		// if the age of childID is older than the upper bound of the acceptable age range
		else if (childAge>childAgeBounds[1]) {
			return (Integer)(AgeGroups.getAgeGroup(childAge).getMinAge() - childAgeBounds[1]);
		}
		
		// if childAge in within bound
		return null;
	}
	
	
	
	/**
	 * 
	 * @param minAgeParent
	 * @return
	 */
	public static int[] calculateChildAgeBounds(int minAgeParent) {
		int minPossibleChildAge = minAgeParent - HardcodedData.maxdAgeParentChild;
		int maxPossibleChildAge = minAgeParent - HardcodedData.ageOfConsent;
		if (minPossibleChildAge<0) minPossibleChildAge = 0;
		if (maxPossibleChildAge<0) maxPossibleChildAge = 0;
		return new int[] {minPossibleChildAge, maxPossibleChildAge};
	}
	
	/**
	 * 
	 * @param hhID
	 * @return
	 */
	public static int getAgeYoungerParent(int hhID) {
		int[] hhAttribs = HouseholdPool.getPool().get((Integer)hhID);
		HholdTypes hhType = HholdTypes.getHholdTypeFromIndex(hhAttribs[HholdAttribs.hhType.getIndex()]);
		int minParents = HardcodedData.minResidents[hhType.getIndex()][MinResidentCols.min_parents.getIndex()];
		
		int minAgeParent = -1;
		if (minParents==2) {
			// determines the age of younger parent in this household
			ArrayList<Integer> parentsID = HouseholdPool.getResidentsByHhRel(hhID, HholdRelSP.Married);
			minAgeParent = getAgeYoungerParent(ArrayHandler.toInt(parentsID));
		} else if (minParents==1) {
			ArrayList<Integer> parentsID = HouseholdPool.getResidentsByHhRel(hhID, HholdRelSP.LoneParent);
			minAgeParent = IndividualPool.getPool().get(parentsID.get(0))[IndivAttribs.age.getIndex()];
		}
		
		return minAgeParent;
	}
	
	
	/**
	 * returns the younger age of parent(s) in the household.
	 * if there is only 1 parent, the returned age is the age of the parent.
	 * if there are 2 same sex parents, the returned age is the age of the younger one.
	 * if there are 2 opposite sex parents, the returned age is the age of the female parent.
	 * @param parentsID
	 * @return
	 */
	public static int getAgeYoungerParent(int[] parentsID) {
		int youngerAge = -1;
		int p1ID = parentsID[0];
		int p2ID = parentsID[1];
		
		if (p1ID!=-1 && p2ID!=-1) { // if two parents exist
			int[] p1Attrib = IndividualPool.getPool().get((Integer)p1ID);
			int[] p2Attrib = IndividualPool.getPool().get((Integer)p2ID);
			Genders p1Gender = Genders.getGenderByValue(p1Attrib[IndivAttribs.gender.getIndex()]);
			Genders p2Gender = Genders.getGenderByValue(p2Attrib[IndivAttribs.gender.getIndex()]);
			int p1Age = p1Attrib[IndivAttribs.age.getIndex()];
			int p2Age = p2Attrib[IndivAttribs.age.getIndex()];
			
			if (p1Gender.equals(p2Gender)) {
				if (p1Age<=p2Age) {
					youngerAge = p1Age;
				} else {
					youngerAge = p2Age;
				}
			} else {
				if (p1Gender.equals(Genders._female)) {
					youngerAge = p1Age;
				} else {
					youngerAge = p2Age;
				}
			}
		} else if (p1ID!=-1 && p2ID==-1) { // if only 1 parent is valid
			int[] p1Attrib = IndividualPool.getPool().get((Integer)p1ID);
			youngerAge = p1Attrib[IndivAttribs.age.getIndex()];
		} else if (p1ID==-1 && p2ID!=-1) { // if only one parent is valid
			int[] p2Attrib = IndividualPool.getPool().get((Integer)p2ID);
			youngerAge = p2Attrib[IndivAttribs.age.getIndex()];
		}
		
		return youngerAge;
	}
	
	/**
	 * returns false if
	 * - childAge is smaller than (minParentAge - HardcodedData.maxdAgeParentChild) or larger than (minParentAge - HardcodedData.ageOfConsent)
	 * 
	 * @param childAge
	 * @param minParentAge
	 * @return
	 */
	public static boolean isChildAgeAcceptable(int childAge, int minParentAge) {
		
		boolean childAgeAcceptable = false;
		// determines if age of this individual is acceptable to the age of the younger parent
		int[] childAgeBounds = IndividualPool.calculateChildAgeBounds(minParentAge);
		if (childAge>=childAgeBounds[0] && childAge<=childAgeBounds[1]) {
			childAgeAcceptable = true;
		}
		
		return childAgeAcceptable;
	}
	
	public static int[] getAvailLoneParentMalesID() {
		return availLoneParentMalesID;
	}


	public static void setAvailLoneParentMalesID(int[] availLoneParentMalesID) {
		IndividualPool.availLoneParentMalesID = sortIndivIDsByAscendingAge(availLoneParentMalesID);
	}

	
	public static int[] getAvailLoneParentFemalesID() {
		return availLoneParentFemalesID;
	}


	public static void setAvailLoneParentFemalesID(int[] availLoneParentFemalesID) {
		IndividualPool.availLoneParentFemalesID = sortIndivIDsByAscendingAge(availLoneParentFemalesID);
	}
	

	public static int[] getAvailU15MalesID() {
		return availU15MalesID;
	}

	public static void setAvailU15MalesID(int[] availU15MalesID) {
		IndividualPool.availU15MalesID = sortIndivIDsByAscendingAge(availU15MalesID);
	}

	public static int[] getAvailU15FemalesID() {
		return availU15FemalesID;
	}

	public static void setAvailU15FemalesID(int[] availU15FemalesID) {
		IndividualPool.availU15FemalesID = sortIndivIDsByAscendingAge(availU15FemalesID);
	}

	public static int[] getAvailStuMalesID() {
		return availStuMalesID;
	}

	public static void setAvailStuMalesID(int[] availStuMalesID) {
		IndividualPool.availStuMalesID = sortIndivIDsByAscendingAge(availStuMalesID);
	}

	public static int[] getAvailStuFemalesID() {
		return availStuFemalesID;
	}

	public static void setAvailStuFemalesID(int[] availStuFemalesID) {
		IndividualPool.availStuFemalesID = sortIndivIDsByAscendingAge(availStuFemalesID);
	}

	public static int[] getAvailO15MalesID() {
		return availO15MalesID;
	}

	public static void setAvailO15MalesID(int[] availO15MalesID) {
		IndividualPool.availO15MalesID = sortIndivIDsByAscendingAge(availO15MalesID);
	}

	public static int[] getAvailO15FemalesID() {
		return availO15FemalesID;
	}

	public static void setAvailO15FemalesID(int[] availO15FemalesID) {
		IndividualPool.availO15FemalesID = sortIndivIDsByAscendingAge(availO15FemalesID);
	}

	public static int[] getAvailRelMalesID() {
		return availRelMalesID;
	}

	public static void setAvailRelMalesID(int[] availRelMalesID) {
		IndividualPool.availRelMalesID = sortIndivIDsByAscendingAge(availRelMalesID);
	}

	public static int[] getAvailRelFemalesID() {
		return availRelFemalesID;
	}

	public static void setAvailRelFemalesID(int[] availRelFemalesID) {
		IndividualPool.availRelFemalesID = sortIndivIDsByAscendingAge(availRelFemalesID);
	}
	
	public static int[] getAvailMarriedMalesID() {
		return availMarriedMalesID;
	}

	public static void setAvailMarriedMalesID(int[] availMarriedMalesID) {
		IndividualPool.availMarriedMalesID = sortIndivIDsByAscendingAge(availMarriedMalesID);
	}
	
	public static int[] getAvailMarriedFemalesID() {
		return availMarriedFemalesID;
	}

	public static void setAvailMarriedFemalesID(int[] availMarriedFemalesID) {
		IndividualPool.availMarriedFemalesID = sortIndivIDsByAscendingAge(availMarriedFemalesID);
	}
	
	public static int[][] getdAgeAvailMarried() {
		return dAgeAvailMarried;
	}
	
	public static HashMap<Integer, int[]> getPool() {
		return indivPool;
	}

	public static boolean isHavingAvailMarriedOppositeSex() {
		return havingAvailMarriedOppositeSex;
	}

	public static boolean isHavingAvailMarriedMaleOnly() {
		return havingAvailMarriedMaleOnly;
	}

	public static boolean isHavingAvailMarriedFemaleOnly() {
		return havingAvailMarriedFemaleOnly;
	}

	public static boolean isNotHavingAvailMarried() {
		return notHavingAvailMarried;
	}


	public static int[] getAvailLonePersonID() {
		return availLonePersonID;
	}


	public static void setAvailLonePersonID(int[] availLonePersonID) {
		IndividualPool.availLonePersonID = availLonePersonID;
	}


	public static int[] getAvailGrHholdID() {
		return availGrHholdID;
	}


	public static void setAvailGrHholdID(int[] availGrHholdID) {
		IndividualPool.availGrHholdID = availGrHholdID;
	}


}
