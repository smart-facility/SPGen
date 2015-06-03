package core.SPConstructor;

import java.util.ArrayList;

import core.ArrayHandler;
import core.HardcodedData;
import core.HardcodedData.HholdRelSP;
import core.HardcodedData.HholdTypes;
import core.TextFileHandler;

public class GlobalSPOutputHandler {
	private static int[] accumMaleAllocByRel;
	private static int[] accumFemaleAllocByRel;
	private static int[] accumMaleAllocByHhType;
	private static int[] accumFemaleAllocByHhType;
	private static int[] accumHholdAllocByType;
	private static int[] accumHFAllocBySize;
	private static int[] accumNFAllocBySize;
	
	private static int[] accumMaleCensusByRel;
	private static int[] accumFemaleCensusByRel;
	private static int[] accumMaleCensusByHhType;
	private static int[] accumFemaleCensusByHhType;
	private static int[] accumHholdCensusByType;
	private static int[] accumHFCensusBySize;
	private static int[] accumNFCensusBySize;
	
	public static void accumulateMaleAllocByRel(int[] zonalMaleAllocByRel) {
		if (accumMaleAllocByRel==null) {
			accumMaleAllocByRel = new int[zonalMaleAllocByRel.length];
		} 
		accumMaleAllocByRel = ArrayHandler.addPositiveValuesInArrays(accumMaleAllocByRel, zonalMaleAllocByRel);
	}
	
	public static void accumulateFemaleAllocByRel(int[] zonalFemaleAllocByRel) {
		if (accumFemaleAllocByRel==null) {
			accumFemaleAllocByRel = new int[zonalFemaleAllocByRel.length];
		}
		accumFemaleAllocByRel = ArrayHandler.addPositiveValuesInArrays(accumFemaleAllocByRel, zonalFemaleAllocByRel);
	}
	
	public static void accumulateMaleAllocByHhType(int[] zonalMaleAllocByHhType) {
		if (accumMaleAllocByHhType==null) {
			accumMaleAllocByHhType = new int[zonalMaleAllocByHhType.length];
		}
		accumMaleAllocByHhType = ArrayHandler.addPositiveValuesInArrays(accumMaleAllocByHhType, zonalMaleAllocByHhType);
	}
	
	public static void accumulateFemaleAllocByHhType(int[] zonalFemaleAllocByHhType) {
		if (accumFemaleAllocByHhType==null) {
			accumFemaleAllocByHhType = new int[zonalFemaleAllocByHhType.length];
		}
		accumFemaleAllocByHhType = ArrayHandler.addPositiveValuesInArrays(accumFemaleAllocByHhType, zonalFemaleAllocByHhType);
	}
	
	public static void accumulateHholdAllocByType(int[] zonalHholdAllocByType) {
		if (accumHholdAllocByType==null) {
			accumHholdAllocByType = new int[zonalHholdAllocByType.length];
		} else {
			accumHholdAllocByType = ArrayHandler.addPositiveValuesInArrays(accumHholdAllocByType, zonalHholdAllocByType);
		}
	}
	
	public static void accumulateHFAllocBySize(int[] zonalHFAllocBySize) {
		if (accumHFAllocBySize==null) {
			accumHFAllocBySize = new int[zonalHFAllocBySize.length];
		}
		accumHFAllocBySize = ArrayHandler.addPositiveValuesInArrays(accumHFAllocBySize, zonalHFAllocBySize);
	}
	
	public static void accumulateNFAllocBySize(int[] zonalNFAllocBySize) {
		if (accumNFAllocBySize==null) {
			accumNFAllocBySize = new int[zonalNFAllocBySize.length];
		}
		accumNFAllocBySize = ArrayHandler.addPositiveValuesInArrays(accumNFAllocBySize, zonalNFAllocBySize);
	}
	
	
	public static void accumulateMaleCensusByRel(int[] zonalMaleCensusByRel) {
		if (accumMaleCensusByRel==null) {
			accumMaleCensusByRel = new int[zonalMaleCensusByRel.length];
		}
		accumMaleCensusByRel = ArrayHandler.addPositiveValuesInArrays(accumMaleCensusByRel, zonalMaleCensusByRel);
	}
	
	public static void accumulateFemaleCensusByRel(int[] zonalFemaleCensusByRel) {
		if (accumFemaleCensusByRel==null) {
			accumFemaleCensusByRel = new int[zonalFemaleCensusByRel.length];
		} 
		accumFemaleCensusByRel = ArrayHandler.addPositiveValuesInArrays(accumFemaleCensusByRel, zonalFemaleCensusByRel);
	}
	
	public static void accumulateMaleCensusByHhType(int[] zonalMaleCensusByHhType) {
		if (accumMaleCensusByHhType==null) {
			accumMaleCensusByHhType = new int[zonalMaleCensusByHhType.length];
		}
		accumMaleCensusByHhType = ArrayHandler.addPositiveValuesInArrays(accumMaleCensusByHhType, zonalMaleCensusByHhType);
	}
	
	public static void accumulateFemaleCensusByHhType(int[] zonalFemaleCensusByHhType) {
		if (accumFemaleCensusByHhType==null) {
			accumFemaleCensusByHhType = new int[zonalFemaleCensusByHhType.length];
		}
		accumFemaleCensusByHhType = ArrayHandler.addPositiveValuesInArrays(accumFemaleCensusByHhType, zonalFemaleCensusByHhType);
	}
	
	public static void accumulateHholdCensusByType(int[] zonalHholdCensusByType) {
		if (accumHholdCensusByType==null) {
			accumHholdCensusByType = new int[zonalHholdCensusByType.length];
		}
		accumHholdCensusByType = ArrayHandler.addPositiveValuesInArrays(accumHholdCensusByType, zonalHholdCensusByType);
	}
	
	public static void accumulateHFCensusBySize(int[] zonalHFCensusBySize) {
		if (accumHFCensusBySize==null) {
			accumHFCensusBySize = new int[zonalHFCensusBySize.length];
		}
		accumHFCensusBySize = ArrayHandler.addPositiveValuesInArrays(accumHFCensusBySize, zonalHFCensusBySize);
	}
	
	public static void accumulateNFCensusBySize(int[] zonalNFCensusBySize) {
		if (accumNFCensusBySize==null) {
			accumNFCensusBySize = new int[zonalNFCensusBySize.length];
		}
		accumNFCensusBySize = ArrayHandler.addPositiveValuesInArrays(accumNFCensusBySize, zonalNFCensusBySize);
	}
	
	
	public static void writeAccumTablesToCSV() {
		ArrayList<String[]> accumMaleByRel = new ArrayList<String[]>();
		accumMaleByRel.add(new String[] {"hhRel","countABS","countSP"});
		for (int i=0; i<=accumMaleAllocByRel.length-1; i++) {
			String[] crnLine = new String[] {
					HholdRelSP.getHholdRelSPByIndex(i).toString(), Integer.toString(accumMaleCensusByRel[i]), Integer.toString(accumMaleAllocByRel[i])};
			accumMaleByRel.add(crnLine);
		}
		
		ArrayList<String[]> accumFemaleByRel = new ArrayList<String[]>();
		accumFemaleByRel.add(new String[] {"hhRel","countABS","countSP"});
		for (int i=0; i<=accumFemaleAllocByRel.length-1; i++) {
			String[] crnLine = new String[] {
					HholdRelSP.getHholdRelSPByIndex(i).toString(), Integer.toString(accumFemaleCensusByRel[i]), Integer.toString(accumFemaleAllocByRel[i])};
			accumFemaleByRel.add(crnLine);
		}
		
		ArrayList<String[]> accumMaleByHhType = new ArrayList<String[]>();
		accumMaleByHhType.add(new String[] {"hhType", "countABS", "countSP"});
		for (int i=0; i<=accumMaleAllocByHhType.length-1; i++) {
			String[] crnLine = new String[] {
				HholdTypes.getHholdTypeFromIndex(i).toString(), Integer.toString(accumMaleCensusByHhType[i]), Integer.toString(accumMaleAllocByHhType[i])};
			accumMaleByHhType.add(crnLine);
		}
		
		ArrayList<String[]> accumFemaleByHhType = new ArrayList<String[]>();
		accumFemaleByHhType.add(new String[] {"hhType", "countABS", "countSP"});
		for (int i=0; i<=accumFemaleAllocByHhType.length-1; i++) {
			String[] crnLine = new String[] {
				HholdTypes.getHholdTypeFromIndex(i).toString(), Integer.toString(accumFemaleCensusByHhType[i]), Integer.toString(accumFemaleAllocByHhType[i])};
			accumFemaleByHhType.add(crnLine);
		}
		
		ArrayList<String[]> accumHholdByType = new ArrayList<String[]>();
		accumHholdByType.add(new String[] {"hhType", "countABS", "countSP"});
		for (int i=0; i<=accumHholdAllocByType.length-1; i++) {
			String[] crnLine = new String[] {
					HholdTypes.getHholdTypeFromIndex(i).toString(), Integer.toString(accumHholdCensusByType[i]), Integer.toString(accumHholdAllocByType[i])};
			accumHholdByType.add(crnLine);
		}
		
		ArrayList<String[]> accumHFBySize = new ArrayList<String[]>();
		accumHFBySize.add(new String[] {"hhSize", "countABS", "countSP"});
		for (int i=0; i<=accumHFAllocBySize.length-1; i++) {
			String[] crnLine = new String[] {
				Integer.toString((i+1)), Integer.toString(accumHFCensusBySize[i]), Integer.toString(accumHFAllocBySize[i])};
			accumHFBySize.add(crnLine);
		}
		
		ArrayList<String[]> accumNFBySize = new ArrayList<String[]>();
		accumNFBySize.add(new String[] {"hhSize", "countABS", "countSP"});
		for (int i=0; i<=accumNFAllocBySize.length-1; i++) {
			String[] crnLine = new String[] {
				Integer.toString((i+1)), Integer.toString(accumNFCensusBySize[i]), Integer.toString(accumNFAllocBySize[i])};
			accumNFBySize.add(crnLine);
		}
		
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathAccumulative + "accumMaleByRel.csv", null, accumMaleByRel);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathAccumulative + "accumFemaleByRel.csv", null, accumFemaleByRel);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathAccumulative + "accumMaleByHhType.csv", null, accumMaleByHhType);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathAccumulative + "accumFemaleByHhType.csv", null, accumFemaleByHhType);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathAccumulative + "accumHholdByType.csv", null, accumHholdByType);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathAccumulative + "accumHFBySize.csv", null, accumHFBySize);
		TextFileHandler.writeToCSV(HardcodedData.globalOutputPathAccumulative + "accumNFBySize.csv", null, accumNFBySize);
	}
}
