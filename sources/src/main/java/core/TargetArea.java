package core;

import java.util.ArrayList;
import java.util.HashMap;

public class TargetArea {
	
	private static HashMap<String,String> targetAreaDesc;
	private static HashMap<Integer,Integer> jtwAreaByTargetArea;
	private static HashMap<Integer,ArrayList<Integer>> targetAreasByJTWArea;
	private static HashMap<Integer,double[]> targetAreaCoords;
	private static ArrayList<Integer> jtwAreas;
	
	public static ArrayList<Integer> getJTWAreas() {
		return jtwAreas;
	}
	
	public static HashMap<Integer, double[]> getTargetAreaCoords() {
		return targetAreaCoords;
	}
	
	public static HashMap<Integer, Integer> getJTWAreaByTargetArea() {
		return jtwAreaByTargetArea;
	}

	public static HashMap<String,String> getTargetAreaDesc() {
		return targetAreaDesc;
	}
	
	public static HashMap<Integer,ArrayList<Integer>> getTargetAreasByJTWArea() {
		return targetAreasByJTWArea;
	}
	
	private enum TargetAreaDescCol {
		targetArea_code(0),	
		jtwArea_code(1),
		description(2),
		xCentroid(3),
		yCentroid(4);
		
		private int colIndex;
		
		private TargetAreaDescCol(int newColIndex) {
			colIndex = newColIndex;
		}
		
		public int getColIndex() {
			return colIndex;
		}

	}
	
	public static void initialise(String fileName) {
		targetAreaDesc = new HashMap<String, String>();
		jtwAreaByTargetArea = new HashMap<Integer,Integer>();
		targetAreasByJTWArea = new HashMap<Integer,ArrayList<Integer>>();
		targetAreaCoords = new HashMap<Integer,double[]>();
		jtwAreas = new ArrayList<Integer>();
		readTable(fileName);
	}
	
	private static void readTable(String fileName) {
		ArrayList<ArrayList<String>> rawData = TextFileHandler.readCSV(fileName);
		
		for (ArrayList<String> line : rawData) {
			String tAreaIDString = line.get(TargetAreaDescCol.targetArea_code.getColIndex());
			String jtwAreaIDString = line.get(TargetAreaDescCol.jtwArea_code.getColIndex());
			String areaDescription = line.get(TargetAreaDescCol.description.getColIndex());
			String xCentroidString = line.get(TargetAreaDescCol.xCentroid.getColIndex());
			String yCentroidString = line.get(TargetAreaDescCol.yCentroid.getColIndex());
			
			Integer tAreaIDInt = null;
			Integer jtwAreaIDInt = null;
			Double xCentroid = null;
			Double yCentroid = null;
			
			try {
				tAreaIDInt = Integer.parseInt(tAreaIDString);
				jtwAreaIDInt = Integer.parseInt(jtwAreaIDString);
				xCentroid = Double.parseDouble(xCentroidString);
				yCentroid = Double.parseDouble(yCentroidString);
				
				if (!targetAreaDesc.containsKey(tAreaIDString)) {
					targetAreaDesc.put(tAreaIDString, areaDescription);
				}
				
				if (!jtwAreaByTargetArea.containsKey(tAreaIDInt)) {
					jtwAreaByTargetArea.put(tAreaIDInt, jtwAreaIDInt);
				}
				
				ArrayList<Integer> tAreasThisJTWArea = new ArrayList<Integer>();
				if (targetAreasByJTWArea.containsKey(jtwAreaIDInt)) {
					tAreasThisJTWArea = targetAreasByJTWArea.get(jtwAreaIDInt);
				}
				tAreasThisJTWArea.add(tAreaIDInt);
				targetAreasByJTWArea.put(jtwAreaIDInt, tAreasThisJTWArea);
				
				if (!targetAreaCoords.containsKey(tAreaIDInt)) {
					targetAreaCoords.put(tAreaIDInt, new double[] {xCentroid, yCentroid});
				}
				
				if (!jtwAreas.contains(jtwAreaIDInt)) {
					jtwAreas.add(jtwAreaIDInt);
				}
				
			} catch (Exception e) {
				continue;
			}
			
		}
		
	}
	
	
}
