package core;

import core.HardcodedData;
import core.SPConstructor.*;


public class ModelMain {
	
	/**
	 * sample jar execution (from current folder) 
	 * java -jar spGen.jar 1 "input tables/target areas/2006_cd_subset.csv"
	 * @param args
	 */
	public static void main (String[] args) {
		
		String seedValStr = args[0]; //"0"; 
		String listOfTargetAreas = args[1]; //"input tables/target areas/2006_cd_subset.csv";
		
		try {
			int seedInt = Integer.parseInt(seedValStr);
			HardcodedData.setSeed(seedInt);
			HardcodedData.initOutputPaths();
			
			TargetArea.initialise(listOfTargetAreas);
			SPConstructorMain.generatingSPByReadingInCSVTable(listOfTargetAreas);
			
			System.out.println("\nFinished!");
		} catch (Exception e) {
			System.out.println(seedValStr + " is invalid seed value. Please input an integer.");
			System.out.println("Abort SP construction.");
		}
	}
	
	
	/**
	 * example initExistingSPFromCSV("input tables/target areas/dac_allSA1s.csv", "input tables/year20_SP.csv");
	 * @param existSPFilename
	 */
	private static void initExistingSPFromCSV(String targetAreasFilename, String existSPFilename) {
		TargetArea.initialise(targetAreasFilename);
		SPImporter.initialisePopulationFromCSV(existSPFilename);
	}
}

