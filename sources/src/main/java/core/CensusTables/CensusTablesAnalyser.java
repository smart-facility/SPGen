package core.CensusTables;

import java.util.ArrayList;

import core.ArrayHandler;
import core.HardcodedData;
import core.HardcodedData.AgeGroups;
import core.HardcodedData.B24Cols;
import core.HardcodedData.B25Cols;
import core.HardcodedData.B30Cols;
import core.HardcodedData.B30Rows;
import core.HardcodedData.Genders;
import core.HardcodedData.HholdRelSP;
import core.HardcodedData.HholdTypes;

public class CensusTablesAnalyser {
	
	/**
	 * checks if the value corresponding to hhRel and gender and ageGroup in census table b22 is non-negative.
	 * If yes, the value is modifiable.
	 * @param hhRel
	 * @param gender
	 * @param ageGroup
	 * @return
	 */
	public static boolean isRelAndAgeModifiable(HholdRelSP hhRel, Genders gender, AgeGroups ageGroup) {
		boolean isRelAndAgeModifiable = false;
//		ArrayList<Integer> b22Indices = hhRel.getB22ColIndices();
//		if (gender.equals(Genders._female)) {
//			for (Integer b22index : b22Indices) {
//				if (CensusTables.getHhRelFemale()[ageGroup.getIndex()][b22index]>=0) {
//					isRelAndAgeModifiable = true;
//					break;
//				}
//			}
//		} else if (gender.equals(Genders._male)) {
//			for (Integer b22index : b22Indices) {
//				if (CensusTables.getHhRelMale()[ageGroup.getIndex()][b22index]>=0) {
//					isRelAndAgeModifiable = true;
//					break;
//				}
//			}
//		}
		
		if (gender.equals(Genders._female)) {
			if (CensusTables.getSpLikeB22Female()[ageGroup.getIndex()][hhRel.getIndex()]>=0)
				isRelAndAgeModifiable = true;
		} else if (gender.equals(Genders._male)) {
			if (CensusTables.getSpLikeB22Male()[ageGroup.getIndex()][hhRel.getIndex()]>=0)
				isRelAndAgeModifiable = true;
		}
		
		return isRelAndAgeModifiable;
	}
	
	/**
	 * 
	 * @param hhType
	 * @return
	 */
	public static double[] getPercentGenderInHholdType(HholdTypes hhType) {
		int[] countOfGendersInHhType = new int[Genders.values().length];
		
		countOfGendersInHhType[Genders._male.getValue()] = CensusTables.getSpLikeB25Male()[hhType.getIndex()];
		countOfGendersInHhType[Genders._female.getValue()] = CensusTables.getSpLikeB25Female()[hhType.getIndex()];
		
//		countOfGendersInHhType[Genders._male.getValue()] = CensusTables.getFamilyCompBySex()[hhType.getb24b25Row().getIndex()][Genders._male.getB25Col().getIndex()];
//		countOfGendersInHhType[Genders._female.getValue()] = CensusTables.getFamilyCompBySex()[hhType.getb24b25Row().getIndex()][Genders._female.getB25Col().getIndex()];
		
		return ArrayHandler.normaliseArray(countOfGendersInHhType);
	}
	
	/**
	 * 
	 * @param gender
	 * @return returns the percentage of gender in each household type (based on census table B25). Household types are from HF1 to HF16.
	 */
	public static double[] getPercentIndivsByHhTypes(Genders gender) {
		HholdTypes[] hfs = HholdTypes.getValidHFTypes();
		int[] indivCountsByHhTypes = new int[hfs.length];
		if (gender.equals(Genders._female)) {
			for (HholdTypes hf : hfs) {
				indivCountsByHhTypes[hf.getb24b25Row().getIndex()] = CensusTables.getSpLikeB25Female()[hf.getIndex()];
			}
		} else if (gender.equals(Genders._male)) {
			for (HholdTypes hf : hfs) {
				indivCountsByHhTypes[hf.getb24b25Row().getIndex()] = CensusTables.getSpLikeB25Male()[hf.getIndex()];
			}
		} else 
			return null;
		return ArrayHandler.normaliseArray(indivCountsByHhTypes);
		
//		int[] indivCountsByHhTypes = new int[CensusTables.getFamilyCompBySex().length];
//		if (gender.equals(Genders._female)) {
//			for (int i=0; i<=indivCountsByHhTypes.length-1; i++) {
//				indivCountsByHhTypes[i] = CensusTables.getFamilyCompBySex()[i][B25Cols.females.getIndex()];
//			}
//		} else {
//			for (int i=0; i<=indivCountsByHhTypes.length-1; i++) {
//				indivCountsByHhTypes[i] = CensusTables.getFamilyCompBySex()[i][B25Cols.males.getIndex()];
//			}
//		}
//		return ArrayHandler.normaliseArray(indivCountsByHhTypes);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static double[] getPercentIndivsByHhTypes() {
		HholdTypes[] hfs = HholdTypes.getValidHFTypes();
		
		int[] indivCountsByHhTypes = new int[hfs.length];
		
		for (HholdTypes hf : hfs) {
			indivCountsByHhTypes[hf.getb24b25Row().getIndex()] = CensusTables.getSpLikeB25Male()[hf.getIndex()] + CensusTables.getSpLikeB25Female()[hf.getIndex()];
		}
		
		
		return ArrayHandler.normaliseArray(indivCountsByHhTypes);
	}
	
	/**
	 * 
	 * @return the percentage of family households that have 1, 2, 3, 4, 5, 6+ residents (using census table B30).
	 */
	public static double[] getPercentHFByUsualResidents() {
		int[] hhCounts = CensusTables.getSpLikeB30HF();
		
		
//		int[] hhCounts = new int[CensusTables.getHhComp().length];
//		for (int i=0; i<=hhCounts.length-1; i++) {
//			hhCounts[i] = CensusTables.getHhComp()[i][B30Cols.familyHholds.getIndex()];
//		}
		return ArrayHandler.normaliseArray(hhCounts);
	}
	
	
	/**
	 * 
	 * @return the percentage of non-family households that have 1, 2, 3, 4, ,5, 6+ residents (using census table B30).
	 */
	public static double[] getPercentNFByUsualResidents() {
		int[] hhCounts = CensusTables.getSpLikeB30NF();
//		int[] hhCounts = new int[CensusTables.getHhComp().length];
//		for (int i=0; i<=hhCounts.length-1; i++) {
//			hhCounts[i] = CensusTables.getHhComp()[i][B30Cols.nonFamilyHholds.getIndex()];
//		}
		return ArrayHandler.normaliseArray(hhCounts);
	}
	
	
	/**
	 * 
	 * @param hhRelSP
	 * @param hhRel
	 * @return the percentage of hhRelSP and gender in census table B22. The length of the returned array equals to HardcodedData.AgeGroups.
	 */
	public static double[] getPercentbyAgeGroups(HholdRelSP hhRelSP, Genders gender) {
//		int[] countsByAge = new int[AgeGroups.values().length];
//
//		ArrayList<Integer> cols = hhRelSP.getB22ColIndices();
//
//		for (AgeGroups age : AgeGroups.values()) {
//			int ageIndex = age.getIndex();
//			for (Integer col : cols) {
//				if (gender.equals(Genders._male)) {
//					if (CensusTables.getHhRelMale()[ageIndex][col]<0) {
//						countsByAge[ageIndex] = -1;
//					} else {
//						if (countsByAge[ageIndex]<0) {
//							countsByAge[ageIndex] = 0;
//						}
//						countsByAge[ageIndex] = countsByAge[ageIndex] + CensusTables.getHhRelMale()[ageIndex][col];
//					}
//				} else if (gender.equals(Genders._female)) {
//					if (CensusTables.getHhRelFemale()[ageIndex][col]<0) {
//						countsByAge[ageIndex] = -1;
//					} else {
//						if (countsByAge[ageIndex]<0) {
//							countsByAge[ageIndex] = 0;
//						}
//						countsByAge[ageIndex] = countsByAge[ageIndex] + CensusTables.getHhRelFemale()[ageIndex][col];
//					}
//				}
//				
//			}
//		}
//
//		double[] percentByAge = ArrayHandler.normaliseArray(countsByAge);

		int[] countsByAge = new int[AgeGroups.values().length];
		
		if (gender.equals(Genders._female)) {
			for (int i=0; i<=countsByAge.length-1; i++) {
				countsByAge[i] = CensusTables.getSpLikeB22Female()[i][hhRelSP.getIndex()];
			}
		} else if (gender.equals(Genders._male)) {
			for (int i=0; i<=countsByAge.length-1; i++) {
				countsByAge[i] = CensusTables.getSpLikeB22Male()[i][hhRelSP.getIndex()];
			}
		} else {
			return null;
		}
		
		return ArrayHandler.normaliseArray(countsByAge);
	}

	
	/**
	 * 
	 * @param hhRel
	 * @return returns the percentage of gender across all age groups in census table B22. The length of the returned array equals to HardcodedData.HholdRelSP.
	 */
	public static double[] getPercentByHhRels(Genders gender) {
		int[] countsHhRels = new int[HholdRelSP.values().length];

//		for (HholdRelSP hhRelSP : HholdRelSP.values()) {
//			int hhRelSPIdx = hhRelSP.getIndex();
//			ArrayList<Integer> cols = hhRelSP.getB22ColIndices();
//			for (Integer col : cols) {
//				for (AgeGroups ageGroup : AgeGroups.values()) {
//					if (gender.equals(Genders._male)) {
//						if (CensusTables.getHhRelMale()[ageGroup.getIndex()][col]<0) {
//							countsHhRels[hhRelSPIdx] = -1;
//						} else {
//							if (countsHhRels[hhRelSPIdx]<0) countsHhRels[hhRelSPIdx] = 0;
//							countsHhRels[hhRelSPIdx] = countsHhRels[hhRelSPIdx] + CensusTables.getHhRelMale()[ageGroup.getIndex()][col];
//						}
//					} else if (gender.equals(Genders._female)) {
//						if (CensusTables.getHhRelFemale()[ageGroup.getIndex()][col]<0) {
//							countsHhRels[hhRelSPIdx] = -1;
//						} else {
//							if (countsHhRels[hhRelSPIdx]<0) countsHhRels[hhRelSPIdx] = 0;
//							countsHhRels[hhRelSPIdx] = countsHhRels[hhRelSPIdx] + CensusTables.getHhRelFemale()[ageGroup.getIndex()][col];
//						}
//					}
//					
//						
//				}	
//			}
//		}

		for (int i=0; i<=countsHhRels.length-1; i++) {
			countsHhRels[i] = -1;
		}
		
		if (gender.equals(Genders._female)) {
			for (int i=0; i<=countsHhRels.length-1; i++) {
				for (AgeGroups ageGroup : AgeGroups.values()) {
					int ageGroupIndex = ageGroup.getIndex();
					if (CensusTables.getSpLikeB22Female()[ageGroupIndex][i]>=0) {
						if (countsHhRels[i]>=0)
							countsHhRels[i] = countsHhRels[i] + CensusTables.getSpLikeB22Female()[ageGroupIndex][i];
						else 
							countsHhRels[i] = CensusTables.getSpLikeB22Female()[ageGroupIndex][i];
					}
				}
			}
		} else if (gender.equals(Genders._male)) {
			for (int i=0; i<=countsHhRels.length-1; i++) {
				for (AgeGroups ageGroup : AgeGroups.values()) {
					int ageGroupIndex = ageGroup.getIndex();
					if (CensusTables.getSpLikeB22Male()[ageGroupIndex][i]>=0) {
						if (countsHhRels[i]>=0)
							countsHhRels[i] = countsHhRels[i] + CensusTables.getSpLikeB22Male()[ageGroupIndex][i];
						else 
							countsHhRels[i] = CensusTables.getSpLikeB22Male()[ageGroupIndex][i];
					}
				}
			}
		} else 
			return null;
		
		return ArrayHandler.normaliseArray(countsHhRels);

	}
	
	
	/**
	 * 
	 * @param ageGroup
	 * @param gender
	 * @return
	 */
	public static double[] getPercentByHhRels(AgeGroups ageGroup, Genders gender) {
		int[] countsHhRels = new int[HholdRelSP.values().length];
		
//		for (HholdRelSP hhRelSP : HholdRelSP.values()) {
//			int hhRelSPIdx = hhRelSP.getIndex();
//			ArrayList<Integer> cols = hhRelSP.getB22ColIndices();
//			for (Integer col : cols) {
//				if (gender.equals(Genders._male)) {
//					if (CensusTables.getHhRelMale()[ageGroup.getIndex()][col]<0) {
//						countsHhRels[hhRelSPIdx] = -1;
//					} else {
//						if (countsHhRels[hhRelSPIdx]<0) countsHhRels[hhRelSPIdx] = 0;
//						countsHhRels[hhRelSPIdx] = countsHhRels[hhRelSPIdx] + CensusTables.getHhRelMale()[ageGroup.getIndex()][col];
//					}
//				} else if (gender.equals(Genders._female)) {
//					if (CensusTables.getHhRelFemale()[ageGroup.getIndex()][col]<0) {
//						countsHhRels[hhRelSPIdx] = -1;
//					} else {
//						if (countsHhRels[hhRelSPIdx]<0) countsHhRels[hhRelSPIdx] = 0;
//						countsHhRels[hhRelSPIdx] = countsHhRels[hhRelSPIdx] + CensusTables.getHhRelFemale()[ageGroup.getIndex()][col];
//					}
//				}
//			}
//		}
		
		int ageGroupIndex = ageGroup.getIndex();
		if (gender.equals(Genders._female))
			for (int i=0; i<=countsHhRels.length-1; i++)
				countsHhRels[i] = CensusTables.getSpLikeB22Female()[ageGroupIndex][i];
		else if (gender.equals(Genders._male))
			for (int i=0; i<=countsHhRels.length-1; i++)
				countsHhRels[i] = CensusTables.getSpLikeB22Male()[ageGroupIndex][i];
		else {
			return null;
		}
		
		return ArrayHandler.normaliseArray(countsHhRels);
	}
	
	
	/**
	 * 
	 * @param gender
	 * @return table of counts of individuals by household relationship by age group (from census table B22) of a given gender. 
	 * The number of rows is HardcodedData.AgeGroups.values().length. The number of columns is HardcodedData.HholdRelSP.values().length.
	 */
	public static int[][] getSPlikeTableFromCensusB22(Genders gender) {
		int[][] b22SP = new int[HardcodedData.AgeGroups.values().length][HardcodedData.HholdRelSP.values().length];
		
		for (AgeGroups ageGroup : AgeGroups.values()) {
			int ageGrIdx = ageGroup.getIndex();
			for (HholdRelSP hhRelSP : HholdRelSP.values()) {
				int hhRelSPIdx = hhRelSP.getIndex();
				ArrayList<Integer> b22Idx = hhRelSP.getB22ColIndices();
				for (Integer ib22 : b22Idx) {
					if (gender.equals(Genders._male)) {
						if (CensusTables.getHhRelMale()[ageGrIdx][ib22]<0) {
							b22SP[ageGrIdx][hhRelSPIdx] = -1;
						}
						else {
							if (b22SP[ageGrIdx][hhRelSPIdx]==-1) b22SP[ageGrIdx][hhRelSPIdx] = 0;
							b22SP[ageGrIdx][hhRelSPIdx] += CensusTables.getHhRelMale()[ageGrIdx][ib22];
						}
					} else if (gender.equals(Genders._female)) {
						if (CensusTables.getHhRelFemale()[ageGrIdx][ib22]<0) {
							b22SP[ageGrIdx][hhRelSPIdx] = -1;
						}
						else {
							if (b22SP[ageGrIdx][hhRelSPIdx]==-1) b22SP[ageGrIdx][hhRelSPIdx] = 0;
							b22SP[ageGrIdx][hhRelSPIdx] += CensusTables.getHhRelFemale()[ageGrIdx][ib22];
						}
					}
				}
			}
		}
		
		return b22SP;
	}
	
	
	/**
	 * 
	 * @param gender
	 * @return int[HholdRelSP.values().length]
	 */
	public static int[] getIndivCensusByHhRel(Genders gender) {
		int[] hhRelCounts = new int[HholdRelSP.values().length];
		
		//int[][] hhRel = getSPlikeTableFromCensusB22(gender);
		
		if (gender.equals(Genders._female)) {
			for (int iRel=0; iRel<=hhRelCounts.length-1; iRel++) {
				for (int iAge=0; iAge<=CensusTables.getSpLikeB22Female().length-1; iAge++) {
					if (CensusTables.getSpLikeB22Female()[iAge][iRel]>=0)
						hhRelCounts[iRel] = hhRelCounts[iRel] + CensusTables.getSpLikeB22Female()[iAge][iRel];
				}
			}
		} else if (gender.equals(Genders._male)) {
			for (int iRel=0; iRel<=hhRelCounts.length-1; iRel++) {
				for (int iAge=0; iAge<=CensusTables.getSpLikeB22Male().length-1; iAge++) {
					if (CensusTables.getSpLikeB22Male()[iAge][iRel]>=0)
						hhRelCounts[iRel] = hhRelCounts[iRel] + CensusTables.getSpLikeB22Male()[iAge][iRel];
				}
			}
		} else 
			return null;
		
		return hhRelCounts;
	}
	
		
	/**
	 * 
	 * @return count of family households (from census table B24). The length of the returned array equals HholdTypes.values().length.
	 */
	public static int[] getSPlikeHhCountsTableFromCensusB24() {
		int[] b24SP = new int[HholdTypes.values().length];
		
		for (HholdTypes hhType : HholdTypes.values()) {
			if (hhType.equals(HholdTypes.NF)) {
				int nnf = 0;
				for (B30Rows row : B30Rows.values()) {
					if (CensusTables.getSpLikeB30NF()[row.getIndex()]>0) {
						nnf = nnf + CensusTables.getSpLikeB30NF()[row.getIndex()];
					}
//					if (CensusTables.getHhComp()[row.getIndex()][B30Cols.nonFamilyHholds.getIndex()]>=0) {
//						nnf = nnf + CensusTables.getHhComp()[row.getIndex()][B30Cols.nonFamilyHholds.getIndex()];
//					}
				}
				b24SP[hhType.getIndex()] = nnf;
			} else if (!hhType.equals(HholdTypes.Unknown)) {
				b24SP[hhType.getIndex()] = CensusTables.getFamilyComp()[hhType.getb24b25Row().getIndex()][B24Cols.families.getIndex()];
			}
		}
		
		return b24SP;
	}
	
	
	/**
	 * 
	 * @param gender
	 * @return counts of the number of individuals of a given gender in each household type (from census table B24). The length of the returned array equals HholdTypes.values().length.
	 */
	public static int[] getSPlikeIndivCountsFromCensusB25(Genders gender) {
		int[] b25SP = new int[HholdTypes.values().length];
		
		for (HholdTypes hhType : HholdTypes.values()) {
			if (hhType.equals(HholdTypes.NF) || hhType.equals(HholdTypes.Unknown)) {
				continue;
			}
			if (gender.equals(Genders._male)) 
				b25SP[hhType.getIndex()] = CensusTables.getFamilyCompBySex()[hhType.getb24b25Row().getIndex()][B25Cols.males.getIndex()];
			else if (gender.equals(Genders._female))
				b25SP[hhType.getIndex()] = CensusTables.getFamilyCompBySex()[hhType.getb24b25Row().getIndex()][B25Cols.females.getIndex()];
		}
		
		return b25SP;
	}
	
	
	/**
	 * 
	 * @return counts of the number family households that have 1,2,3,4,5,6+ residents (from census table B30). Size of the returned table equals B30Rows.values().length.
	 */
	public static int[] getSPlikeHFCountsFromCensusB30() {
		int[] hfComp = new int[B30Rows.values().length]; 
		
		for (B30Rows row : B30Rows.values()) {
			hfComp[row.getIndex()] = CensusTables.getHhComp()[row.getIndex()][B30Cols.familyHholds.getIndex()];
		}
		
		// family households must not have 1 resident
		hfComp[0] = -1;
		
		return hfComp;
	}
	
	
	/**
	 * 
	 * @return counts of the number non-family households that have 1,2,3,4,5,6+ residents (from census table B30). Size of the returned table equals B30Rows.values().length.
	 */
	public static int[] getSPlikeNFCountsFromCensusB30() {
		int[] nfComp = new int[B30Rows.values().length]; 
		
		for (B30Rows row : B30Rows.values()) {
			nfComp[row.getIndex()] = CensusTables.getHhComp()[row.getIndex()][B30Cols.nonFamilyHholds.getIndex()];
		}
		
		return nfComp;
	}
}
