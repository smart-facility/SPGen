package core;

import java.util.ArrayList;
import java.util.HashMap;

import core.HardcodedData.*;
import core.ArrayHandler;
import core.SyntheticPopulation.Household;
import core.SyntheticPopulation.Individual;
import core.SyntheticPopulation.Population;


public class ImportData {
	
	/**
	 * 
	 * @param synPopCSV
	 * @param birthRatesCSV
	 * @param deathRatesFemaleCSV
	 * @param deathRatesMaleCSV
	 * @param divorceRatesFemaleCSV
	 * @param divorceRatesMaleCSV
	 * @param marriageRatesFemaleCSV
	 * @param marriageRatesMaleCSV
	 */
	public static void importData(String birthRatesCSV, 
			String deathRatesFemaleCSV, String deathRatesMaleCSV, String divorceRatesFemaleCSV, String divorceRatesMaleCSV,
			String marriageRatesFemaleCSV, String marriageRatesMaleCSV) {
		
		Rates.setBirthRates(readInBirthRates(birthRatesCSV));
		Rates.setDeathRatesFemale(readInRates(deathRatesFemaleCSV));
		Rates.setDeathRatesMale(readInRates(deathRatesMaleCSV));
		Rates.setDivorceRatesFemale(readInRates(divorceRatesFemaleCSV));
		Rates.setDivorceRatesMale(readInRates(divorceRatesMaleCSV));
		Rates.setMarriageRatesFemale(readInRates(marriageRatesFemaleCSV));
		Rates.setMarriageRatesMale(readInRates(marriageRatesMaleCSV));
	}
	
	
	/**
	 * 
	 * @param birthRatesCSV
	 * @return
	 */
	private static HashMap<Integer,double[]> readInBirthRates(String birthRatesCSV) {
		ArrayList<ArrayList<String>> rawCSV = TextFileHandler.readCSV(birthRatesCSV);
		HashMap<Integer,double[]> birthRates = new HashMap<Integer,double[]>();
		
		for (ArrayList<String> row : rawCSV) {
			String ageStr = row.get(InputBirthRatesColumn.age.getIndex());
			String child1 = row.get(InputBirthRatesColumn.firstChild.getIndex());
			String child2 = row.get(InputBirthRatesColumn.secondChild.getIndex());
			String child3 = row.get(InputBirthRatesColumn.thirdChild.getIndex());
			String child4 = row.get(InputBirthRatesColumn.fourthChild.getIndex());
			String child5 = row.get(InputBirthRatesColumn.fifthChild.getIndex());
			String child6 = row.get(InputBirthRatesColumn.sixthChildMore.getIndex());
			
			int age;
			double prob1Child;
			double prob2Child;
			double prob3Child;
			double prob4Child;
			double prob5Child;
			double prob6Child;
			try {
				age = Integer.parseInt(ageStr);
				prob1Child = Double.parseDouble(child1);
				prob2Child = Double.parseDouble(child2);
				prob3Child = Double.parseDouble(child3);
				prob4Child = Double.parseDouble(child4);
				prob5Child = Double.parseDouble(child5);
				prob6Child = Double.parseDouble(child6);
			} catch (Exception e) {
				continue;
			}
			
			birthRates.put(age, new double[] {prob1Child, prob2Child, prob3Child, prob4Child, prob5Child, prob6Child});
		}
		
		return birthRates;
	}
	
	
	/**
	 * 
	 * @param ratesCSV
	 * @return
	 */
	private static HashMap<Integer,Double> readInRates(String ratesCSV) {
		ArrayList<ArrayList<String>> rawCSV = TextFileHandler.readCSV(ratesCSV);
		HashMap<Integer,Double> rates = new HashMap<Integer,Double>();
		
		for (ArrayList<String> row : rawCSV) {
			String ageStr = row.get(InputRatesColumn.age.getIndex());
			String probStr = row.get(InputRatesColumn.probability.getIndex());
			
			int age;
			double prob;
			try {
				age = Integer.parseInt(ageStr);
				prob = Double.parseDouble(probStr);
			} catch (Exception e) {
				continue;
			}
			
			rates.put(age, prob);
		}
		
		return rates;
	}
}
