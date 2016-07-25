package core.SPConstructor;

import java.util.ArrayList;
import java.util.HashMap;

import core.TextFileHandler;
import core.HardcodedData.Genders;
import core.HardcodedData.HholdRelSP;
import core.HardcodedData.HholdTypes;
import core.TargetArea;
import core.SyntheticPopulation.Household;
import core.SyntheticPopulation.Individual;
import core.SyntheticPopulation.Population;

public class SPImporter {
	
	public enum InitSPColumns {
		hhID(0), indivID(1), age(2), gender(3), hhRel(4), hhType(5), zoneID(6), zoneDescription(7), incomeWkly(8);
		
		private int index;
		
		private InitSPColumns (int newIndex) {
			index = newIndex;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	/**
	 * 
	 * @param csvFile
	 */
	public static void initialisePopulationFromCSV(String csvFile) {
		ArrayList<ArrayList<String>> rawSP = TextFileHandler.readCSV(csvFile);
		
		if (rawSP==null) {
			return;
		}
		
		Population.setHhPool(new HashMap<Integer,Household>());
		Population.setIndivPool(new HashMap<Integer,Individual>());
		
		for (ArrayList<String> indRec : rawSP) {
			int hholdID, indivID, age, income, targetAreaID;
			
			try {
				hholdID = Integer.parseInt(indRec.get(InitSPColumns.hhID.getIndex()));
				indivID = Integer.parseInt(indRec.get(InitSPColumns.indivID.getIndex()));
				age = Integer.parseInt(indRec.get(InitSPColumns.age.getIndex()));
				income = Integer.parseInt(indRec.get(InitSPColumns.incomeWkly.getIndex()));
				Genders gender = Genders.valueOf(indRec.get(InitSPColumns.gender.getIndex()));
				HholdRelSP hhRel = HholdRelSP.valueOf(indRec.get(InitSPColumns.hhRel.getIndex()));
				HholdTypes hhType = HholdTypes.valueOf(indRec.get(InitSPColumns.hhType.getIndex()));
				targetAreaID = Integer.parseInt(indRec.get(InitSPColumns.zoneID.getIndex()));
				
				// only importing this individual is he/she is in the TargetArea
				if (!TargetArea.getTargetAreaDesc().containsKey(targetAreaID)) {
					// adds this new individual to Population.indivPool
					Individual newIndiv = new Individual(indivID, age, gender, hhRel, income);
					Population.getIndivPool().put((Integer)indivID, newIndiv);
					
					// adds this new individual to household. 
					// If the household doesn't exist, create a new one and add it to Population.hhPool
					ArrayList<Integer> residentsID = new ArrayList<Integer>();
					Household hhold;
					if (Population.getHhPool().containsKey((Integer)hholdID)) {
						hhold = Population.getHhPool().get((Integer)hholdID);
						residentsID = Population.getHhPool().get((Integer)hholdID).getResidentsID();
					} else {
						hhold = new Household(hholdID, hhType, residentsID, targetAreaID, TargetArea.getTargetAreaDesc().get(targetAreaID));
					}
					residentsID.add((Integer)indivID);
					hhold.setResidentsID(residentsID);
					Population.getHhPool().put((Integer)hholdID, hhold);
				}
			} catch (NumberFormatException e) {
				continue;
			}
		}

		// free the memory
		rawSP.clear();
	}

}
