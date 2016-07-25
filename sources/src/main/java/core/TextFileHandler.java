package core;

import java.io.*;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.StringValueTransformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import core.SyntheticPopulation.Household;
import core.SyntheticPopulation.Individual;
import core.SyntheticPopulation.Population;

public class TextFileHandler {
	private static final Logger logger = Logger.getLogger(TextFileHandler.class);
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static ArrayList<ArrayList<String>> readCSV (String fileName) {
		ArrayList<ArrayList<String>> sheetData = new ArrayList<ArrayList<String>>();
		BufferedReader br = null;
		
		try {
			 // create BufferedReader to read csv file containing data
            br = new BufferedReader(new FileReader(fileName));
            String strLine = "";

            StringTokenizer st = null;

            // read comma separated file line by line
            while ((strLine = br.readLine()) != null) {

                // break comma separated line using ","
                st = new StringTokenizer(strLine, ",");

                ArrayList<String> data = new ArrayList<String>();

                while (st.hasMoreTokens()) {
                    // display csv file
                    String editedValue = String.valueOf(st.nextToken())
                            .replace("\"", "").trim();

                    data.add(editedValue);
                }

                sheetData.add(data);
            }
            br.close();
		} catch (Exception e) {
			//logger.error("Exception while reading csv file: ", e);
		}
		
		return sheetData;
	}
	
	
	/**
	 * 
	 * @param filename
	 * @param header
	 * @param dataout
	 */
	public static void writeToCSV(String filename, String[] header, List<String[]> dataout) {
		CSVWriter csvWriter = null;
		try {
			Writer fw = new BufferedWriter(new FileWriter(filename));
			csvWriter = new CSVWriter(fw, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
			
			csvWriter.writeNext(header);
			csvWriter.writeAll(dataout);

			csvWriter.flush();
		} catch (IOException e) {
			System.out.println("Failed to write content to file:" + filename);
		} finally {
			if (csvWriter != null) {
				try {
					csvWriter.close();
				} catch (IOException e) {
					System.out.println("Failed to close file after writing: ");
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @param filename
	 * @param header
	 * @param dataout
	 */
	public static void writeToCSV(String filename, String[] header, List<String[]> dataout, boolean append) {
		CSVWriter csvWriter = null;
		try {
			Writer fw = new BufferedWriter(new FileWriter(filename, append));
			csvWriter = new CSVWriter(fw, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
			
			csvWriter.writeNext(header);
			csvWriter.writeAll(dataout);

			csvWriter.flush();
		} catch (IOException e) {
			System.out.println("Failed to write content to file:" + filename);
		} finally {
			if (csvWriter != null) {
				try {
					csvWriter.close();
				} catch (IOException e) {
					System.out.println("Failed to close file after writing: ");
				}
			}
		}
	}
	
	
	public static void writeToCSV(String filename, String[] header, double[][] dataout) {
		ArrayList<String[]> strOut = new ArrayList<String[]>();
		for (int i=0; i<=dataout.length-1; i++) {
			String[] dataRow = new String[dataout[i].length];
			for (int j=0; j<=dataRow.length-1; j++) {
				dataRow[j] = Double.toString(dataout[i][j]);
			}
			strOut.add(dataRow);
		}
		 writeToCSV(filename, header, strOut);
	}
	
	
	/**
	 * 
	 * @param filename
	 * @param header
	 * @param dataout
	 */
	public static void writeToCSV(String filename, String[] header, HashMap<Integer,Integer> dataout) {
		ArrayList<String[]> strOut = new ArrayList<String[]>();
		for (Integer key : dataout.keySet()) {
			String[] str = new String[] {String.valueOf(key), String.valueOf(dataout.get(key))};
			strOut.add(str);
		}
		writeToCSV(filename, header, strOut);
	}
	
	
	/**
	 * 
	 * @param filename
	 * @param header
	 * @param dataout
	 */
	public static void writeToCSVIntegerDouble(String filename, String[] header, HashMap<Integer,Double> dataout, boolean append) {
		ArrayList<String[]> strOut = new ArrayList<String[]>();
		for (Integer key : dataout.keySet()) {
			String[] str = new String[] {String.valueOf(key), String.valueOf(dataout.get(key))};
			strOut.add(str);
		}
		writeToCSV(filename, header, strOut, append);
	}
	
	
	/**
    *
    */
   public static void writeToText(String filename, String text, boolean append) {
       BufferedWriter writer = null;

       try {
           writer = new BufferedWriter(new FileWriter(filename, append));
           writer.append(text);
           writer.newLine();
           writer.flush();
       } catch (IOException e) {
           logger.error("Failed to write file: " + filename, e);
       } finally {
           if (writer != null) {
               try {
                   writer.close();
               } catch (IOException e) {
                   logger.error("Failed to close file after writing: " + filename, e);
               }
           }
       }
   }
   
   /**
	 * write Travel Diary into CSV file.
	 * 
	 * @param fileName
	 * 
	 * @author vlcao
	 */
	public static void writeTravelDiaryToCSV(String fileName) {

       final String[] header = {"Travel_ID", "Agent ID", "Hhold ID", "Age", "Gender", "Income",
               "Travel_Origin", "Travel_Destination", "Start_Time", "End_Time", "Duration",
               "Mode_of_Transport", "Purpose", "Vehicle_ID", "Trip_ID"};

		CSVWriter csvWriter = null;
		try {
           csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fileName)), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			csvWriter.writeNext(header);

			// Iterates the data and write it out to the .csv file
			for (Household hhold : Population.getHhPool().values()) {
				for (Integer indivID : hhold.getResidentsID()) {
					Individual individual = Population.getIndivPool().get(indivID);
					int[][] diaries = individual.getTravelDiariesWeekdays();
					if (diaries == null) {
						continue;
                   }
                   for (int[] diary : diaries) {
                	   
                	   /*
                       //NOT write to csv file for travel diary does NOT travel (stay at home)
                       if ((diary[6] == -1 //origin
                               && diary[7] == -1 //destination
                               && diary[8] == -1 //start_time
                               && diary[9] == -1 //end_time
                               && diary[10] == -1 //duration
                               && diary[11] == -1) //travel_mode
                               || (diary[6] == 0 //origin
                               && diary[7] == 0 //destination
                               && diary[8] == 0 //start_time
                               && diary[9] == 0 //end_time
                               && diary[10] == 0) //duration
                               ) {

                           continue;
                       }
						*/
                       Integer[] diaryAsObj = ArrayUtils.toObject(diary);
                       Collection<String> stringValues = CollectionUtils.collect(Arrays.asList(diaryAsObj), StringValueTransformer.getInstance());
                       csvWriter.writeNext(stringValues.toArray(new String[stringValues.size()]));
                   }
				}
			}

		} catch (IOException e) {
			logger.error("Failed to create Travel Diaries file" + fileName, e);
		} finally {
			try {
				if (csvWriter != null) {
                   csvWriter.close();
               }
			} catch (IOException e) {
				logger.error("Failed to close file: " + fileName, e);
			}
		}
	}
	
	/**
	 * 
	 * @param spTD
	 * @param fileName
	 */
	public static void writeSpTD2CSV(List<List<int[][]>> spTD, String fileName) {
        final String[] header = {"hholdID", "indivID", "tripID", "departure_time", "trip_time", "mode", "purpose", "origin", "destination"};

        CSVWriter writer = null;

		try {
			writer = new CSVWriter(new BufferedWriter(new FileWriter(fileName)), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			writer.writeNext(header);

            for (List<int[][]> spTDwOD : spTD) {
                for (int[][] tdHhold : spTDwOD) {
                    for (int[] aTdHhold : tdHhold) {
                        //NOT write to csv file for travel diary does NOT travel (stay at home)
                        /*
                    	if ((aTdHhold[3] == 0 //departure_time
                                && aTdHhold[4] == 0 //trip_time
                                && aTdHhold[7] == 0 //origin
                                && aTdHhold[8] == 0)//destination
                                || (aTdHhold[3] == -1 //departure_time
                                && aTdHhold[4] == -1 //trip_time
                                && aTdHhold[7] == -1 //origin
                                && aTdHhold[8] == -1)//destination
                                ) {

                            continue;
                        }
						*/
                        Collection<String> stringValues = CollectionUtils.collect(Arrays.asList(ArrayUtils.toObject(aTdHhold)), StringValueTransformer.getInstance());
                        writer.writeNext(stringValues.toArray(new String[stringValues.size()]));
                        writer.flush();
                    }
                }
            }
		} catch (IOException e) {
			logger.error("Failed to write to file: " + e, e);
		}  finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                logger.error("Failed to close file: " + fileName, e);
            }
        }
	}
}
