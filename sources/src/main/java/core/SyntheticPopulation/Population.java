package core.SyntheticPopulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import core.TextFileHandler;
import core.SyntheticPopulation.Household;
import core.SyntheticPopulation.Population;

public class Population {
	private static HashMap<Integer,Household> hhPool;
	private static HashMap<Integer,Individual> indivPool;
	
	/**
	 * 
	 * @param outputCSV
	 */
	public static void outputPopulationRecords(String outputCSV) {
		ArrayList<String[]> outputPop = new ArrayList<String[]>();
		String[] header = new String[] {"hhID", "indivID", "age", "gender", "hhRel", "hhType", "zoneID", "zoneDescription"};
//		outputPop.add(header);

		for (Integer hhID : hhPool.keySet()) {
			ArrayList<Integer> residentsID = hhPool.get(hhID).getResidentsID();
			
			for (Integer indivID : residentsID) {
				String[] details = new String[9];
				details[0] = Integer.toString(hhID);
				details[1] = Integer.toString(indivID);
				details[2] = Integer.toString(indivPool.get(indivID).getAge());
				details[3] = indivPool.get(indivID).getGender().toString();
				details[4] = indivPool.get(indivID).getHhRel().toString();
				details[5] = hhPool.get(hhID).getHhType().toString();
				details[6] = Integer.toString(hhPool.get(hhID).getAreaID());
				details[7] = hhPool.get(hhID).getAreaDescription();
				outputPop.add(details);
				
			}
		}
		
		TextFileHandler.writeToCSV(outputCSV, header, outputPop);
	}
	
	
	/**
	 * 
	 * @param outputCSV
	 */
	public static void outputPopulationRecordsWithIncome(String outputCSV) {
		ArrayList<String[]> outputPop = new ArrayList<String[]>();
		String[] header = new String[] {"hhID", "indivID", "age", "gender", "hhRel", "hhType", "zoneID", "zoneDescription", "income"};
//		outputPop.add(header);

		for (Integer hhID : hhPool.keySet()) {
			ArrayList<Integer> residentsID = hhPool.get(hhID).getResidentsID();
			
			for (Integer indivID : residentsID) {
				String[] details = new String[header.length];
				details[0] = Integer.toString(hhID);
				details[1] = Integer.toString(indivID);
				details[2] = Integer.toString(indivPool.get(indivID).getAge());
				details[3] = indivPool.get(indivID).getGender().toString();
				details[4] = indivPool.get(indivID).getHhRel().toString();
				details[5] = hhPool.get(hhID).getHhType().toString();
				details[6] = Integer.toString(hhPool.get(hhID).getAreaID());
				details[7] = hhPool.get(hhID).getAreaDescription();
				details[8] = Integer.toString(indivPool.get(indivID).getIncomeWkly());
				outputPop.add(details);
				
			}
		}
		
		TextFileHandler.writeToCSV(outputCSV, header, outputPop);
	}
	
	
	
	/**
	 * adds a new individual indiv to indivPool. If ID of the new individual indiv already exists in indivPool, indiv is not added (i.e the existing individual is not replaced by indiv).
	 * @param indiv
	 */
	public static void addIndivToPopulation(Individual indiv) {
		if (!indivPool.containsKey((Integer)indiv.getId())) {
			indivPool.put((Integer)indiv.getId(), indiv);
		}
	}
	
	
	/**
	 * adds a new resident to an existing household in hhPool. If ID of the new resident (newResidentID) already exists, this resident is not added to this household.
	 * @param hhID ID of the household to which new resident will be added.
	 * @param newResidentID ID of the new resident.
	 */
	public static void addIndivToHhold(int hhID, int newResidentID) {
		Household hhold = hhPool.get((Integer)hhID);
		ArrayList<Integer> crnResidents = hhold.getResidentsID();
		if (!crnResidents.contains((Integer)newResidentID)) {
			crnResidents.add((Integer)newResidentID);
			hhold.setResidentsID(crnResidents);
		}
		
	}
	
	
	
	/**
	 * checks and removes households in the population that have no residents.
	 * If there is at least one resident in a household, reassesses and reassigns household type.
	 */
	public static void cleanUpHholdPool() {
		Iterator<Entry<Integer, Household>> itHhold = Population.getHhPool().entrySet().iterator();
		// for each household in the hhPool
		while (itHhold.hasNext()) {
			Map.Entry<Integer, Household> entry = itHhold.next();
			Integer hhID = entry.getKey();
			if (hhPool.get(hhID).getResidentsID()==null || hhPool.get(hhID).getResidentsID().size()==0) {
				itHhold.remove();
			} else {
				hhPool.get(hhID).assignAggreHholdType();
			}
		}
	}
	
	/**
	 * adds a new household hhold to hhPool. If ID of the new household hhold already exists in hhPool, hhold is not added (i.e the existing household is not replaced by hhold).
	 * @param hhold
	 */
	public static void addHholdToPopulation(Household hhold) {
		if (!hhPool.containsKey(hhold.getId())) {
			hhPool.put((Integer)hhold.getId(),hhold);
		}
	}
	

	/**
	 * 
	 * @param outputCSV
	 */
	public static void outputPopulation(String outputCSV) {
		ArrayList<String[]> outputPop = new ArrayList<String[]>();
		String[] header = new String[] {"hhID", "indivID", "age", "gender", "hhRel", "hhType", "zoneDescription", "zoneName"};
		//outputPop.add(header);

		for (Integer hhID : hhPool.keySet()) {
			ArrayList<Integer> residentsID = hhPool.get(hhID).getResidentsID();
			
			for (Integer indivID : residentsID) {
				String[] details = new String[8];
				details[0] = Integer.toString(hhID);
				details[1] = Integer.toString(indivID);
				details[2] = Integer.toString(indivPool.get(indivID).getAge());
				details[3] = indivPool.get(indivID).getGender().toString();
				details[4] = indivPool.get(indivID).getHhRel().toString();
				details[5] = hhPool.get(hhID).getHhType().toString();
				details[6] = hhPool.get(hhID).getAreaDescription();
				details[7] = Integer.toString(hhPool.get(hhID).getAreaID());
				outputPop.add(details);
			}
		}
		TextFileHandler.writeToCSV(outputCSV, header, outputPop);
	}

	
	public static HashMap<Integer, Household> getHhPool() {
		return hhPool;
	}
	public static void setHhPool(HashMap<Integer, Household> hhPool) {
		Population.hhPool = hhPool;
	}
	public static HashMap<Integer, Individual> getIndivPool() {
		return indivPool;
	}
	public static void setIndivPool(HashMap<Integer, Individual> indivPool) {
		Population.indivPool = indivPool;
	}
}
