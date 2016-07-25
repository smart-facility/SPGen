package core.SPConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import core.HardcodedData;
import core.TargetArea;
import core.TextFileHandler;
import core.CensusTables.CensusTables;
import core.SyntheticPopulation.Household;
import core.SyntheticPopulation.Individual;
import core.SyntheticPopulation.Population;


public class SPConstructorMain {
	/**
	 * 
	 * @param fileNameFamilyCompByType
	 * @param fileNameHhRelByAgeBySex
	 * @param fileNameHholdCompBySize
	 * @param fileNameZoneDescription
	 */
	public static void generatingSPByReadingInCSVTable(String fileNameZoneDescription) {
		HardcodedData.initOutputPaths();
		ZonalSPOutputHandler.initialiseOutputArraysForZones();
		
		Population.setHhPool(new HashMap<Integer, Household>());
		Population.setIndivPool(new HashMap<Integer, Individual>());
		
		ArrayList<String[]> procTimeLog = new ArrayList<String[]>();
		
		for (String zoneID : TargetArea.getTargetAreaDesc().keySet()) {
			Timestamp startTimeStamp = getTimeStampVal();
			System.out.println("\nzone " + zoneID + ", " + TargetArea.getTargetAreaDesc().get(zoneID) + ", " + startTimeStamp.toString());
			
			// sets census tables
			// correct CensusTables.setCensusTablesFromReadingCSV so that if not all tables are available, return false
			boolean tablesReadSuccessful = CensusTables.setCensusTablesFromReadingCSV(zoneID);
			if (!tablesReadSuccessful) { 
				System.out.println("Not all input tables for zone " + zoneID + " are available or read in successfully! Abort constructing SP for zone " + zoneID + "!");
				continue;
			}
			
			Timestamp endTimeStamp = getTimeStampVal();
			
			long dt = endTimeStamp.getTime() - startTimeStamp.getTime();
			procTimeLog.add(new String[] {zoneID, startTimeStamp.toString(), endTimeStamp.toString(), String.valueOf(dt)});
			
			// starts constructing SP for this zone
			System.out.println("\tstart constructZonalSP... " + endTimeStamp.toString());
			SPConstructor.constructZonalSP(zoneID);
			
			System.out.println("\tstart recordStatForZones... " + getTimeStamp());
			ZonalSPOutputHandler.recordStatForZones(zoneID);

			// integrates SP of this zone into the global SP
			System.out.println("\tstart translateZonalSP2GlobalSP... " + getTimeStamp());
			SPConstructor.translateZonalSP2GlobalSP(zoneID);
		}
		
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathCountForZones + "zonalGenTimeLog.csv", new String[] {"zoneID", "startTime", "endTime", "dt"}, procTimeLog);
		
		System.out.println("start outputPopulationRecords... " + getTimeStamp());
		Population.outputPopulationRecords(HardcodedData.globalOutputPath + "finSP.csv");
		
		//IncomeAssignment.assignIncome();
		//Population.outputPopulationRecordsWithIncome(HardcodedData.globalOutputPath + "finSP.csv");
		
		System.out.println("start writeStatForZonesToCSV... " + getTimeStamp());
		ZonalSPOutputHandler.writeStatForZonesToCSV();
		
		System.out.println("start writeAccumTablesToCSV... " + getTimeStamp());
		GlobalSPOutputHandler.writeAccumTablesToCSV();
	}
	
	
	private static String getTimeStamp() {
		return (new Timestamp(new java.util.Date().getTime())).toString();
	}
	
	private static Timestamp getTimeStampVal() {
		return (new Timestamp(new java.util.Date().getTime()));
	}
}
