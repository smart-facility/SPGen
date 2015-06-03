package core.CensusTables.Preprocessing;

import java.util.ArrayList;
import java.util.HashMap;

import core.TextFileHandler;

public class CDDescriptionPreprocessor {
	
	private static HashMap<String,String> ccdIDDescriptionMap;
	
	public static HashMap<String,String> getCcdIDDescriptionMap() {
		return ccdIDDescriptionMap;
	}
	
	private enum CDDescriptionCol {
		cd_code(0),	
		sla_code(1),
		sla_name(2);
		
		private int colIndex;
		
		private CDDescriptionCol(int newColIndex) {
			colIndex = newColIndex;
		}
		
		public int getColIndex() {
			return colIndex;
		}

	}
	
	 
	
	public static void readInDescriptionTable(String fileName) {
		ccdIDDescriptionMap = new HashMap<String, String>();
		readTable(fileName);
	}
	
	private static void readTable(String fileName) {
		ArrayList<ArrayList<String>> rawData = TextFileHandler.readCSV(fileName);
		
		for (ArrayList<String> line : rawData) {
			Integer ccdIDInt = null;
			try {
				ccdIDInt = Integer.parseInt(line.get(CDDescriptionCol.cd_code.getColIndex()));
			} catch (Exception e) {
				continue;
			}
			String ccdIDString = Integer.toString(ccdIDInt);
			String slaCode = line.get(CDDescriptionCol.sla_code.getColIndex());
			String slaName = line.get(CDDescriptionCol.sla_name.getColIndex());
			String ccdDescription = slaName + "_" + slaCode;
			
			if (!ccdIDDescriptionMap.containsKey(ccdIDString)) {
				ccdIDDescriptionMap.put(ccdIDString, ccdDescription);
			}
		}
	}
}
