package core.CensusTables.Preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import core.TextFileHandler;

public class CensusTablesPreprocessor {
	
	public static void readInSLACensusTables(String fileNameFamilyCompByType, String fileNameHhRelByAgeBySex, String fileNameHholdCompBySize) {
		SLAFamiCompByTypePreprocessor.readInCensusTables(fileNameFamilyCompByType);
		SLAHhRelByAgeBySexPreprocessor.readInCensusTables(fileNameHhRelByAgeBySex);
		SLAHholdCompBySizePreprocessor.readInCensusTables(fileNameHholdCompBySize);
	}
//	
//	public static void readInCDCensusTables(String fileNameFamilyCompByType, 
//			String fileNameHhRelByAgeBySex, String fileNameHholdCompBySize) {
//		CDFamiCompByTypePreprocessor.readInCensusTables(fileNameFamilyCompByType);
//		CDHhRelByAgeBySexPreprocessor.readInCensusTables(fileNameHhRelByAgeBySex);
//		CDHholdCompBySizePreprocessor.readInCensusTables(fileNameHholdCompBySize);
//		
//	}
}
