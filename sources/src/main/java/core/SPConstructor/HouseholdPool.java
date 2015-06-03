package core.SPConstructor;
import java.util.ArrayList;
import java.util.HashMap;

import core.ArrayHandler;
import core.CensusTables.CensusTables;
import core.HardcodedData.B24Cols;
import core.HardcodedData.B24B25Rows;
import core.HardcodedData.Genders;
import core.HardcodedData.HholdAttribs;
import core.HardcodedData.B30Cols;
import core.HardcodedData.B30Rows;
import core.HardcodedData.HholdRelSP;
import core.HardcodedData.HholdTypes;
import core.HardcodedData.IndivAttribs;
import core.HardcodedData.MinResidentCols;


public class HouseholdPool {
	private static HashMap<Integer, int[]> hholdPool;
	private static HashMap<Integer, ArrayList<Integer>> hholdsAllocated;
	
	public static HashMap<Integer, ArrayList<Integer>> getHholdsAllocated() {
		return hholdsAllocated;
	}

	public static void setHholdsAllocated(HashMap<Integer, ArrayList<Integer>> newHholdsAllocated) {
		hholdsAllocated = newHholdsAllocated;
	}
	
	public static HashMap<Integer, int[]> getPool() {
		return hholdPool;
	}

	
	/**
	 * 
	 */
	public static void makeHholdPool() {
		int hholdCount = 0;
		hholdPool = new HashMap<Integer, int[]>();
		int[] hholdIndices = ArrayHandler.toInt(hholdPool.keySet());
		if (ArrayHandler.getLength(hholdIndices)>0) {
			hholdCount = ArrayHandler.getLength(hholdIndices)+1;
		}
		
		// generates records for family households using table of family composition (familyComp)
		HholdTypes[] hfs = HholdTypes.getValidHFTypes();
		for (HholdTypes hf : hfs) {
			int nhholds = CensusTables.getSpLikeB24()[hf.getIndex()];
			for (int nhh=1; nhh<=nhholds; nhh++) {
				hholdCount += 1;
				int[] hholdAttrib = makeAttribsOfNewHhold(HholdTypes.getHholdTypeFromString(hf.toString()));
				hholdPool.put(hholdCount, hholdAttrib);
			}
		}
		
//		for (B24B25Rows hf : B24B25Rows.values()) {
//			int nhholds = CensusTables.getFamilyComp()[hf.getIndex()][B24Cols.families.getIndex()];
//			for (int nhh=1; nhh<=nhholds; nhh++) {
//				hholdCount += 1;
//				int[] hholdAttrib = makeAttribsOfNewHhold(HholdTypes.getHholdTypeFromString(hf.toString()));
//				hholdPool.put(hholdCount, hholdAttrib);
//			}
//		}
		
		// generates records for non family households using table of household composition (hhComp)
		for (B30Rows row : B30Rows.values()) {
			int nhholds = CensusTables.getSpLikeB30NF()[row.getIndex()];
			//int nhholds = CensusTables.getHhComp()[row.getIndex()][B30Cols.nonFamilyHholds.getIndex()];
			for (int nhh=1; nhh<=nhholds; nhh++) {
				hholdCount += 1;
				int[] hholdAttrib = makeAttribsOfNewHhold(HholdTypes.NF);
				hholdPool.put(hholdCount, hholdAttrib);
			}
		}
	}
	
	
	/**
	 * 
	 * @param hhTypes
	 * @param nResidents
	 * @return
	 */
	private static int[] makeAttribsOfNewHhold(HholdTypes hhTypes) {
		int[] hholdAttrib = new int[HholdAttribs.values().length];
		
		hholdAttrib[HholdAttribs.hhType.getIndex()] = hhTypes.getIndex();
		
		return hholdAttrib;
	}
	
	
	/**
	 * 
	 * @param hholdID
	 * @param indivsID
	 */
	public static void addResidentsToHousehold(int hholdID, ArrayList<Integer> indivsID) {
		if (hholdsAllocated==null)
			 hholdsAllocated = new HashMap<Integer, ArrayList<Integer>>();
		
		ArrayList<Integer> existingIndivsID = new ArrayList<Integer>();
		if (hholdsAllocated.containsKey((Integer)hholdID)) {
			existingIndivsID = hholdsAllocated.get((Integer)hholdID);
		}
		for (Integer id : indivsID) {
			if (id<0) continue;
			if (!existingIndivsID.contains(id)) 
				existingIndivsID.add(id);
		}
		
		hholdsAllocated.put((Integer)hholdID,existingIndivsID); 
	}

	
	/**
	 * 
	 * @param hholdID
	 * @param residentID
	 */
	public static void addResidentsToHousehold(int hholdID, int residentID) {
		if (hholdsAllocated==null)
			 hholdsAllocated = new HashMap<Integer, ArrayList<Integer>>();
		
		ArrayList<Integer> existingIndivsID = new ArrayList<Integer>();
		if (hholdsAllocated.containsKey((Integer)hholdID)) {
			existingIndivsID = hholdsAllocated.get((Integer)hholdID);
		}
		
		if (!existingIndivsID.contains((Integer)residentID)) {
			existingIndivsID.add((Integer)residentID);
		}
		
		hholdsAllocated.put((Integer)hholdID,existingIndivsID); 
	}
	
	
	/**
	 * looks for residents with queryHhRel in HouseholdPool.getHholdsAllocated().
	 * @param hhID
	 * @param queryHhRel
	 * @return
	 */
	public static ArrayList<Integer> getResidentsByHhRel(int hhID, HholdRelSP queryHhRel) {
		ArrayList<Integer> residentsID = new ArrayList<Integer>();
		for (Integer indivID : getHholdsAllocated().get((Integer)hhID)) {
			int[] indivAttribs = IndividualPool.getPool().get(indivID);
			if (queryHhRel.getIndex()==indivAttribs[IndivAttribs.hhRel.getIndex()]) {
				residentsID.add(indivID);
			}
		}
		
		return residentsID;
	}
	
	
}
