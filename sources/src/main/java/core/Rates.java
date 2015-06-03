package core;

import java.util.HashMap;

public class Rates {
	private static HashMap<Integer,double[]> birthRates;
	private static HashMap<Integer,Double> deathRatesFemale;
	private static HashMap<Integer,Double> deathRatesMale;
	private static HashMap<Integer,Double> divorceRatesFemale;
	private static HashMap<Integer,Double> divorceRatesMale;
	private static HashMap<Integer,Double> marriageRatesFemale;
	private static HashMap<Integer,Double> marriageRatesMale;
	
	public static HashMap<Integer, double[]> getBirthRates() {
		return birthRates;
	}
	public static void setBirthRates(HashMap<Integer, double[]> birthRates) {
		Rates.birthRates = birthRates;
	}
	public static HashMap<Integer, Double> getDeathRatesFemale() {
		return deathRatesFemale;
	}
	public static void setDeathRatesFemale(HashMap<Integer, Double> deathRatesFemale) {
		Rates.deathRatesFemale = deathRatesFemale;
	}
	public static HashMap<Integer, Double> getDeathRatesMale() {
		return deathRatesMale;
	}
	public static void setDeathRatesMale(HashMap<Integer, Double> deathRatesMale) {
		Rates.deathRatesMale = deathRatesMale;
	}
	public static HashMap<Integer, Double> getDivorceRatesFemale() {
		return divorceRatesFemale;
	}
	public static void setDivorceRatesFemale(
			HashMap<Integer, Double> divorceRatesFemale) {
		Rates.divorceRatesFemale = divorceRatesFemale;
	}
	public static HashMap<Integer, Double> getDivorceRatesMale() {
		return divorceRatesMale;
	}
	public static void setDivorceRatesMale(HashMap<Integer, Double> divorceRatesMale) {
		Rates.divorceRatesMale = divorceRatesMale;
	}
	public static HashMap<Integer, Double> getMarriageRatesFemale() {
		return marriageRatesFemale;
	}
	public static void setMarriageRatesFemale(
			HashMap<Integer, Double> marriageRatesFemale) {
		Rates.marriageRatesFemale = marriageRatesFemale;
	}
	public static HashMap<Integer, Double> getMarriageRatesMale() {
		return marriageRatesMale;
	}
	public static void setMarriageRatesMale(
			HashMap<Integer, Double> marriageRatesMale) {
		Rates.marriageRatesMale = marriageRatesMale;
	}
}
