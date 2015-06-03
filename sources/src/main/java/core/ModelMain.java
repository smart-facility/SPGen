package core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import core.SPConstructor.IndividualPool;
import core.SPConstructor.SPAnalyser;
import core.SPConstructor.SPConstructor;
import core.SPConstructor.ZonalSPOutputHandler;
import core.HardcodedData;
import core.CensusTables.CensusTables;
import core.CensusTables.Preprocessing.CDFamiCompByTypePreprocessor;
import core.CensusTables.Preprocessing.CDHhRelByAgeBySexPreprocessor;
import core.CensusTables.Preprocessing.CDHholdCompBySizePreprocessor;
import core.CensusTables.Preprocessing.CensusTablesPreprocessor;
import core.CensusTables.Preprocessing.SLAFamiCompByTypePreprocessor;
import core.CensusTables.Preprocessing.SLAHhRelByAgeBySexPreprocessor;
import core.CensusTables.Preprocessing.SLAHholdCompBySizePreprocessor;
import core.HardcodedData.*;
import core.SPConstructor.*;
import core.SyntheticPopulation.*;

public class ModelMain {

	public static void main (String[] args) {
		HardcodedData.setSeed(Integer.parseInt(args[0]));
		//HardcodedData.setSeed(0);
		
		constructSP(args[1]);
		//constructSP("input tables/rawData_cd/2006_cd_subset.csv");
		
		//evolveSP(20);
		
		System.out.println("\nFinished!");
	}
	
	private static void constructSP(String arg) {
//		SPConstructorMain.generateSPUsingDBTables();
		
//		SPConstructorMain.generateSPUsingStoredCSVTables("input tables/rawData/2006_sla_family_composition_subset.csv", 
//				"input tables/rawData/2006_sla_household_composition_by_sex_by_age_subset.csv",
//				"input tables/rawData/2006_sla_household_composition_by_size_subset.csv");

//		System.out.println("Start constructing SP for " + args[0]);
//		SPConstructorMain.generatingZonalSPByReadingInCSVTable(args[0]);
//		SPConstructorMain.generatingZonalSPByReadingInCSVTable("105057201");
//		SPConstructorMain.generatingZonalSPByReadingInCSVTable("1010101");
		
		//SPConstructorMain.generatingSPByReadingInCSVTable("input tables/rawData_cd_Illawarra/2006_cd_Illawarra.csv");
		SPConstructorMain.generatingSPByReadingInCSVTable(arg);
	}
	
	
	/**
	 * evolves the population for a number of years, nYears.
	 * Note that the SP must have been generated, e.g. by using SPConstructorMain.generatingSPByReadingInCSVTable, before executing this.
	 * @param nYears
	 */
	private static void evolveSP(int nYears) {
		Population.startEvolulation(nYears);
	}
	
	
	private static void evolveSP(String initSPFile, int nYears) {
		Population.startEvolulation(nYears);
	}
	
	
	private static Timestamp getTimeStampVal() {
		return (new Timestamp(new java.util.Date().getTime()));
	}
	
	
	
	
	private static void generateInputTables() {
		SLAFamiCompByTypePreprocessor.readInCensusTables("input tables/rawData_sla/2006_sla_family_composition.csv");
		SLAHhRelByAgeBySexPreprocessor.readInCensusTables("input tables/rawData_sla/2006_sla_household_composition_by_sex_by_age.csv");
		SLAHholdCompBySizePreprocessor.readInCensusTables("input tables/rawData_sla/2006_sla_household_composition_by_size.csv");
		
		CDFamiCompByTypePreprocessor.readInCensusTables("input tables/rawData_cd/2006_cd_family_composition.csv");
		CDHhRelByAgeBySexPreprocessor.readInCensusTables("input tables/rawData_cd/2006_cd_household_composition_by_sex_by_age.csv");
		CDHholdCompBySizePreprocessor.readInCensusTables("input tables/rawData_cd/2006_cd_household_composition_by_size.csv");
	}
	
	
	
}

