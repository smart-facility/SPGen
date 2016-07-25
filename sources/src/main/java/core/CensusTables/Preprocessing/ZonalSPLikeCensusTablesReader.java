package core.CensusTables.Preprocessing;

import java.util.ArrayList;

import core.HardcodedData.AgeGroups;
import core.HardcodedData.B30Rows;
import core.HardcodedData.HholdRelSP;
import core.HardcodedData.HholdTypes;
import core.TextFileHandler;

public class ZonalSPLikeCensusTablesReader {
	/**
	 * 
	 * @param fileNameZonalCountByHhType
	 * @return
	 */
	public static int[] readCountByHhType(String fileNameZonalCountByHhType) {
		ArrayList<ArrayList<String>> rawData = TextFileHandler.readCSV(fileNameZonalCountByHhType);
		
		if (rawData.size()==0)
			return null;
		
		int[] spLikeCountByHhType = new int[HholdTypes.values().length];
		
		for (int iRow=0; iRow<=rawData.size()-1; iRow++) {
			ArrayList<String> line = rawData.get(iRow);
			for (int iCol=0; iCol<=line.size()-1; iCol++) {
				Integer count=null;
				try {
					count = Integer.parseInt(line.get(iCol));
				} catch (Exception e) {
					continue;
				}
				spLikeCountByHhType[iRow-1] = count;
			}
		}
		
		return spLikeCountByHhType;
	}
	
	
	/**
	 * 
	 * @param fileNameZonalHhRel
	 * @return
	 */
	public static int[][] readHhRel(String fileNameZonalHhRel) {
		ArrayList<ArrayList<String>> rawData = TextFileHandler.readCSV(fileNameZonalHhRel);
		
		if (rawData.size()==0)
			return null;
		
		int[][] spLikehhRel = new int[AgeGroups.values().length][HholdRelSP.values().length];
		
		for (int iRow=1; iRow<=rawData.size()-1; iRow++) {
			ArrayList<String> line = rawData.get(iRow);
			for (int iCol=1; iCol<=line.size()-1; iCol++) {
				Integer count=null;
				try {
					count = Integer.parseInt(line.get(iCol));
				} catch (Exception e) {
					continue;
				}
				spLikehhRel[iRow-1][iCol-1] = count;
			}
		}
		
		return spLikehhRel;
	}
	
	
	/**
	 * 
	 * @param fileNameZonalHhCountByType
	 */
	public static int[] readHhCountBySize(String fileNameZonalHhCountByType) {
		ArrayList<ArrayList<String>> rawData = TextFileHandler.readCSV(fileNameZonalHhCountByType);
		
		if (rawData.size()==0)
			return null;
		
		int[] spLikeHhBySize = new int[B30Rows.values().length];
		
		for (int iRow=0; iRow<=rawData.size()-1; iRow++) {
			ArrayList<String> line = rawData.get(iRow);
			for (int iCol=0; iCol<=line.size()-1; iCol++) {
				Integer count=null;
				try {
					count = Integer.parseInt(line.get(iCol));
				} catch (Exception e) {
					continue;
				}
				spLikeHhBySize[iRow-1] = count;
			}
		}
		return spLikeHhBySize;
	}
}
