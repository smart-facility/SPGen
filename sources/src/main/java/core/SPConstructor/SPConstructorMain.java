package core.SPConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import core.HardcodedData;
import core.PostgresHandler;
import core.TextFileHandler;
import core.CensusTables.CensusTables;
import core.CensusTables.Preprocessing.CDDescriptionPreprocessor;
import core.CensusTables.Preprocessing.SLAFamiCompByTypePreprocessor;
import core.CensusTables.Preprocessing.SLAHhRelByAgeBySexPreprocessor;
import core.CensusTables.Preprocessing.SLAHholdCompBySizePreprocessor;
import core.HardcodedData.DBNames;
import core.SyntheticPopulation.Household;
import core.SyntheticPopulation.Individual;
import core.SyntheticPopulation.Population;


public class SPConstructorMain {

	/**
	 * generates the synthetic population using census tables stored in databases.
	 * 
	 */
	public static void generateSPUsingDBTables() {
		
		HardcodedData.initOutputPaths();
		ZonalSPOutputHandler.initialiseOutputArraysForZones();
		
		Population.setHhPool(new HashMap<Integer, Household>());
		Population.setIndivPool(new HashMap<Integer, Individual>());
		
		HardcodedData.setZonesNameDescription();
		PostgresHandler indivDB = new PostgresHandler(HardcodedData.My_POSTGRESURL + DBNames.individuals, HardcodedData.My_USERNAME, HardcodedData.My_PASSWORD);
		PostgresHandler hholdDB = new PostgresHandler(HardcodedData.My_POSTGRESURL + DBNames.households, HardcodedData.My_USERNAME, HardcodedData.My_PASSWORD);
		
		for (String zoneID : HardcodedData.getZonesNameDescription().keySet()) {
			System.out.println("zone " + zoneID);
			
			// reads tables from databases and assigns them to hhRelMale, hhRelFemale, nHF, nppInHF, and hhComp
			CensusTables.readTablesFromDBs(indivDB, hholdDB, zoneID);
			
			// starts constructing SP for this zone
			SPConstructor.constructZonalSP(zoneID);
			ZonalSPOutputHandler.recordStatForZones(zoneID);

			// integrates SP of this zone into the global SP
			SPConstructor.translateZonalSP2GlobalSP(zoneID);
		}
		
		// closes connections
		indivDB.closeConnection();
		hholdDB.closeConnection();
		
		Population.outputPopulationRecords(HardcodedData.globalOutputPath + "finSP.csv");
		ZonalSPOutputHandler.writeStatForZonesToCSV();
		GlobalSPOutputHandler.writeAccumTablesToCSV();
	}
	
	
	/**
	 * generates the synthetic population using census tables stored in csv files.
	 */
	public static void generateSPUsingStoredCSVTables(String fileNameFamilyCompByType, String fileNameHhRelByAgeBySex, String fileNameHholdCompBySize) {
		HardcodedData.initOutputPaths();
		ZonalSPOutputHandler.initialiseOutputArraysForZones();
		
		Population.setHhPool(new HashMap<Integer, Household>());
		Population.setIndivPool(new HashMap<Integer, Individual>());
		
		// read in census tables for all zones from csv files
		SLAFamiCompByTypePreprocessor.readInCensusTables(fileNameFamilyCompByType);
		SLAHhRelByAgeBySexPreprocessor.readInCensusTables(fileNameHhRelByAgeBySex);
		SLAHholdCompBySizePreprocessor.readInCensusTables(fileNameHholdCompBySize);
		
		HardcodedData.setZonesNameDescription(SLAFamiCompByTypePreprocessor.getSlaIDNameMapFamiComp());
		
		for (String zoneID : HardcodedData.getZonesNameDescription().keySet()) {
			System.out.println("zone " + zoneID + ", " + HardcodedData.getZonesNameDescription().get(zoneID) + ", " + getTimeStamp());
			
			// sets census tables
			System.out.println("\tstart setCensusTablesFromStoredData... " + getTimeStamp());
			CensusTables.setCensusTablesFromStoredData(zoneID);
			
			// starts constructing SP for this zone
			System.out.println("\tstart constructZonalSP... " + getTimeStamp());
			SPConstructor.constructZonalSP(zoneID);
			
			System.out.println("\tstart recordStatForZones... " + getTimeStamp());
			ZonalSPOutputHandler.recordStatForZones(zoneID);

			// integrates SP of this zone into the global SP
			System.out.println("\tstart translateZonalSP2GlobalSP... " + getTimeStamp());
			SPConstructor.translateZonalSP2GlobalSP(zoneID);
		}
		
		System.out.println("start outputPopulationRecords... " + getTimeStamp());
		Population.outputPopulationRecords(HardcodedData.globalOutputPath + "finSP.csv");
		
		System.out.println("start writeStatForZonesToCSV... " + getTimeStamp());
		ZonalSPOutputHandler.writeStatForZonesToCSV();
		
		System.out.println("start writeAccumTablesToCSV... " + getTimeStamp());
		GlobalSPOutputHandler.writeAccumTablesToCSV();
	}
	
	
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
		
		CDDescriptionPreprocessor.readInDescriptionTable(fileNameZoneDescription);
		HardcodedData.setZonesNameDescription(CDDescriptionPreprocessor.getCcdIDDescriptionMap());
		
		ArrayList<String[]> procTimeLog = new ArrayList<String[]>();
		
		for (String zoneID : HardcodedData.getZonesNameDescription().keySet()) {
			Timestamp startTimeStamp = getTimeStampVal();
			System.out.println("\nzone " + zoneID + ", " + HardcodedData.getZonesNameDescription().get(zoneID) + ", " + startTimeStamp.toString());
			
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
		
		System.out.println("start writeStatForZonesToCSV... " + getTimeStamp());
		ZonalSPOutputHandler.writeStatForZonesToCSV();
		
		System.out.println("start writeAccumTablesToCSV... " + getTimeStamp());
		GlobalSPOutputHandler.writeAccumTablesToCSV();
	}
	
	/**
	 * 
	 * @param zoneID
	 */
	public static void generatingZonalSPByReadingInCSVTable(String zoneID) {
		HardcodedData.initOutputPaths();
		ZonalSPOutputHandler.initialiseOutputArraysForZones();
		
		Population.setHhPool(new HashMap<Integer, Household>());
		Population.setIndivPool(new HashMap<Integer, Individual>());
		
		// sets census tables
		boolean tablesReadSuccessful = CensusTables.setCensusTablesFromReadingCSV(zoneID);
		if (!tablesReadSuccessful) { 
			System.out.println("Not all input tables for zone " + zoneID + " are available or read in successfully! Abort constructing SP for zone " + zoneID + "!");
		} else {
			// starts constructing SP for this zone
			System.out.println("\tstart constructZonalSP... " + getTimeStamp());
			SPConstructor.constructZonalSP(zoneID);
			
//			System.out.println("\tstart recordStatForZones... " + getTimeStamp());
//			ZonalSPOutputHandler.recordStatForZones(zoneID);
		}
	}
	
	private static String getTimeStamp() {
		return (new Timestamp(new java.util.Date().getTime())).toString();
	}
	
	private static Timestamp getTimeStampVal() {
		return (new Timestamp(new java.util.Date().getTime()));
	}
}
