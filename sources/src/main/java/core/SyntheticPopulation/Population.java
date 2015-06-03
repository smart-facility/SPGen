package core.SyntheticPopulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import core.TextFileHandler;
import core.HardcodedData.*;
import core.ArrayHandler;
import core.HardcodedData;
import core.ImportData;
import core.SyntheticPopulation.Household;
import core.SyntheticPopulation.Population;

public class Population {
	private static HashMap<Integer,Household> hhPool;
	private static HashMap<Integer,Individual> indivPool;
	
	/**
	 * 
	 * @param outputCSV
	 */
	public static void outputPopulationRecords(String outputCSV) {
		ArrayList<String[]> outputPop = new ArrayList<String[]>();
		String[] header = new String[] {"hhID", "indivID", "age", "gender", "hhRel", "hhType", "zoneID", "zoneDescription"};
//		outputPop.add(header);

		for (Integer hhID : hhPool.keySet()) {
			ArrayList<Integer> residentsID = hhPool.get(hhID).getResidentsID();
			
			for (Integer indivID : residentsID) {
				String[] details = new String[9];
				details[0] = Integer.toString(hhID);
				details[1] = Integer.toString(indivID);
				details[2] = Integer.toString(indivPool.get(indivID).getAge());
				details[3] = indivPool.get(indivID).getGender().toString();
				details[4] = indivPool.get(indivID).getHhRel().toString();
				details[5] = hhPool.get(hhID).getHhType().toString();
				details[6] = hhPool.get(hhID).getZoneName();
				details[7] = hhPool.get(hhID).getZoneDescription();
				outputPop.add(details);
				
			}
		}
		
		TextFileHandler.writeToCSV(outputCSV, header, outputPop);
	}
	
	
	/**
	 * 
	 * @param nYears
	 */
	public static void startEvolulation(int nYears) {
		// reads in evolution rates
		ImportData.importData(HardcodedData.inputTablesPath + "rates/birth_rates.csv", 
				HardcodedData.inputTablesPath + "rates/death_rates_female.csv",
				HardcodedData.inputTablesPath + "rates/death_rates_male.csv", 
				HardcodedData.inputTablesPath + "rates/divorce_rates_female.csv", 
				HardcodedData.inputTablesPath + "rates/divorce_rates_male.csv", 
				HardcodedData.inputTablesPath + "rates/marriage_rates_female.csv", 
				HardcodedData.inputTablesPath + "rates/marriage_rates_male.csv");
		System.out.println("\nimporting data finished.");
		
		Population.outputPopulation(HardcodedData.outputTablesPath + "year0_SP.csv");
		
		for (int iYear=1; iYear<=nYears; iYear++) {
			Population.evolvePopulation();
			System.out.println("\nevolving SP year " + iYear + " finished.");

			outputPopulation(HardcodedData.outputTablesPath + "year" + Integer.toString(iYear) + "_SP.csv");
			System.out.println("writing year" + iYear + "SP.csv finished.");
		}
	}
	
	/**
	 * evolves the population
	 */
	public static void evolvePopulation() {
		// ages and dies
		ageAndPassAway();
		System.out.println("\n\tageAndPassAway() finished...");
		
		// divorces
		divorce();
		System.out.println("\n\tdivorce() finished...");
		
		// marries
		marry();
		System.out.println("\n\tmarry() finished...");
		
		// gives birth
		giveBirth();
		System.out.println("\n\tgiveBirth() finished...");
	}
	
	
	/**
	 * 
	 */
	private static void giveBirth() {
		Iterator<Entry<Integer, Household>> itHhold = Population.getHhPool().entrySet().iterator();
		// for each household in the hhPool
		while (itHhold.hasNext()) {
			Map.Entry<Integer, Household> entry = itHhold.next();
			Integer hhID = entry.getKey();
			
			ArrayList<Individual> newbornsThisHhold = new ArrayList<Individual>();
			
			Iterator<Integer> itIndiv = hhPool.get(hhID).getResidentsID().iterator();
			while (itIndiv.hasNext()) {
				Integer indID = itIndiv.next();
				// if this household has Married female
				if (indivPool.get(indID).getHhRel().equals(HholdRelSP.Married) && indivPool.get(indID).getGender().equals(Genders._female)) {
					ArrayList<Integer> childIDs = hhPool.get(hhID).getIndivIDOfHHRel(HholdRelSP.U15Child);
					childIDs = hhPool.get(hhID).getIndivIDOfHHRel(HholdRelSP.Student, childIDs);
					childIDs = hhPool.get(hhID).getIndivIDOfHHRel(HholdRelSP.O15Child, childIDs);
					int nCrntChildren = childIDs.size();
					if (indivPool.get(indID).isHavingBaby(nCrntChildren)) {
						// constructs a new individual with age 0, relationship U15Child, random gender,
						Individual newborn = new Individual(calculateNewIndivID(), 0, 
								Genders.getGenderByValue(HardcodedData.random.nextInt(Genders.values().length)+1), HholdRelSP.U15Child);
						// if this newborn survives
						if (!newborn.isDead()) {
							// adds him or her to indivPool
							addIndivToPopulation(newborn);
							// add him or her to the list of newborns to be added to this household later
							newbornsThisHhold.add(newborn);
						}
						
					}
				}
			}
			
			// for each newborn who survives
			for (Individual newborn : newbornsThisHhold) {
				// adds him or her to the resident list of this household
				addIndivToHhold(hhID, newborn.getId());
			}
			
			// reclassifies the type of this household
			hhPool.get(hhID).assignAggreHholdType();
		}
	}
	
	
	/**
	 * adds a new individual indiv to indivPool. If ID of the new individual indiv already exists in indivPool, indiv is not added (i.e the existing individual is not replaced by indiv).
	 * @param indiv
	 */
	public static void addIndivToPopulation(Individual indiv) {
		if (!indivPool.containsKey((Integer)indiv.getId())) {
			indivPool.put((Integer)indiv.getId(), indiv);
		}
	}
	
	
	/**
	 * adds a new resident to an existing household in hhPool. If ID of the new resident (newResidentID) already exists, this resident is not added to this household.
	 * @param hhID ID of the household to which new resident will be added.
	 * @param newResidentID ID of the new resident.
	 */
	public static void addIndivToHhold(int hhID, int newResidentID) {
		Household hhold = hhPool.get((Integer)hhID);
		ArrayList<Integer> crnResidents = hhold.getResidentsID();
		if (!crnResidents.contains((Integer)newResidentID)) {
			crnResidents.add((Integer)newResidentID);
			hhold.setResidentsID(crnResidents);
		}
		
	}
	
	/**
	 * 
	 */
	private static void marry() {
		// gets a list of males and females ready to get married in each household
		HashMap<Integer,ArrayList<Integer>> marryIndivsByHhID = new HashMap<Integer,ArrayList<Integer>>();
		ArrayList<Integer> marryingHhIDs = new ArrayList<Integer>();
		for (Integer hhID : hhPool.keySet()) {
			for (Integer indivID : hhPool.get(hhID).getResidentsID()) {
				if (indivPool.get(indivID).isMarried()) {
					ArrayList<Integer> marryIndivs = new ArrayList<Integer>();
					if (marryIndivsByHhID.containsKey(hhID)) {
						marryIndivs = marryIndivsByHhID.get(hhID);
					}
					marryIndivs.add(indivID);
					marryIndivsByHhID.put(hhID, marryIndivs);
					if (!marryingHhIDs.contains(hhID)) {
						marryingHhIDs.add(hhID);
					}
				}
			}
		}
		
		while (marryIndivsByHhID.size()>0) {
			// randomly picks 2 households from marryIndivsByHhID.keySet()
			if (marryIndivsByHhID.size()>=2) {
				marryIndivsFrom2Hholds(marryIndivsByHhID);
			} else if (marryIndivsByHhID.size()==1) {
				marryIndivsFromSameHhold(marryIndivsByHhID);
			}
			
			// removes hhIDs with empty marry individual ID from marryIndivsByHhID
			Iterator<Entry<Integer, ArrayList<Integer>>> it = marryIndivsByHhID.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, ArrayList<Integer>> entry = it.next();
				ArrayList<Integer> marryIDs = entry.getValue();
				if (marryIDs==null || marryIDs.size()==0) {
					it.remove();
				}
			}
		}
		
		// corrects household relationship of remaining residents in households that have individuals just got married.
		// if any of these households have no residents, remove them from the household pool.
		for (Integer marryingHhID : marryingHhIDs) {
			if (hhPool.get(marryingHhID).getResidentsID()==null || hhPool.get(marryingHhID).getResidentsID().size()==0) {
				hhPool.remove(marryingHhID);
			} else {
				hhPool.get(marryingHhID).correctRelationshipInHhold();
				hhPool.get(marryingHhID).assignAggreHholdType();
			}
		}
	}
	
	
	/**
	 * 
	 * @param marryIndivsByHhID
	 */
	private static void marryIndivsFromSameHhold(HashMap<Integer,ArrayList<Integer>> marryIndivsByHhID) {
		Integer hhID = ArrayHandler.toInt(marryIndivsByHhID.keySet())[0];
		ArrayList<Integer> marryIDs = marryIndivsByHhID.get(hhID);
		
		// if there is only 1 available marrying individual in this household
		if (marryIDs.size()<=1) {
			// empty the list of available marrying individuals of this household and updates marryIndivsByHhID.
			marryIndivsByHhID.put(hhID, new ArrayList<Integer>());
			return;
		}
		
		Integer marry1 = marryIDs.get(0);
		Integer marry2 = null;
		
		if (indivPool.get(marry1).getHhRel().equals(HholdRelSP.Married) || indivPool.get(marry1).getHhRel().equals(HholdRelSP.LoneParent) ||
				indivPool.get(marry1).getHhRel().equals(HholdRelSP.U15Child) || indivPool.get(marry1).getHhRel().equals(HholdRelSP.Student) || 
				indivPool.get(marry1).getHhRel().equals(HholdRelSP.O15Child) || indivPool.get(marry1).getHhRel().equals(HholdRelSP.Relative)) {
			for (Integer id : marryIDs) {
				if (id==marry1) continue;
				if (!indivPool.get(id).getHhRel().equals(HholdRelSP.Married) && !indivPool.get(id).getHhRel().equals(HholdRelSP.LoneParent) &&
						!indivPool.get(id).getHhRel().equals(HholdRelSP.U15Child) && !indivPool.get(id).getHhRel().equals(HholdRelSP.Student) && 
						!indivPool.get(id).getHhRel().equals(HholdRelSP.O15Child) && !indivPool.get(id).getHhRel().equals(HholdRelSP.Relative)) {
					marry2 = id;
					break;
				}
			}
			// if all individuals available for marriage in this household are relative to each other
			if (marry2==null) {
				// empty the list of available marrying individuals of this household and updates marryIndivsByHhID.
				marryIndivsByHhID.put(hhID, new ArrayList<Integer>());
				return;
			} 
		} else {
			marry2 = marryIDs.get(1);
		}
		
		// updates list of marrying individuals in marryIndivsByHhID
		marryIDs.remove(marry1);
		marryIDs.remove(marry2);
		marryIndivsByHhID.put(hhID, marryIDs);
		
		marryIndividuals(marry1, marry2, hhID, marryIndivsByHhID);
	}
	
	
	/**
	 * 
	 * @param indiv1ID
	 * @param indiv2ID
	 * @param hhID
	 * @param marryIDsByHhold
	 */
	private static void marryIndividuals(Integer indiv1ID, Integer indiv2ID, Integer hhID, HashMap<Integer,ArrayList<Integer>> marryIDsByHhold) {
		// creates an ArrayList of ID of individuals in the new household, starting with indiv1ID and indiv2ID
		ArrayList<Integer> newResidents = new ArrayList<Integer>();
		newResidents.add(indiv1ID);
		newResidents.add(indiv2ID);
		
		// if either indiv1ID or indiv2ID is a lone parent
		if (indivPool.get(indiv1ID).getHhRel().equals(HholdRelSP.LoneParent) || indivPool.get(indiv2ID).getHhRel().equals(HholdRelSP.LoneParent)) {
			// adds any children (U15Child, Student, O15Child) in hhID that will move to the new household AND removes these individuals from hhID.
			// Note that the relationship of these individuals don't change in the new household.
			Iterator<Integer> itIndiv = hhPool.get(hhID).getResidentsID().iterator();
			while (itIndiv.hasNext()) {
				Integer indID = itIndiv.next();
				
				if (indID.equals(indiv1ID) || indID.equals(indiv2ID)) continue;
				
				if (indivPool.get(indID).getHhRel().equals(HholdRelSP.U15Child)) {
					newResidents.add(indID);
					// removes indID from hhID
					itIndiv.remove();
				} else if (indivPool.get(indID).getHhRel().equals(HholdRelSP.Student) || indivPool.get(indID).getHhRel().equals(HholdRelSP.O15Child)) {
					if (marryIDsByHhold.get(hhID)!=null && !marryIDsByHhold.get(hhID).contains(indID)) {
						newResidents.add(indID);
						// removes indID from hhID
						itIndiv.remove();
					}
				}
			}
			
		}
		
		// removes indiv1ID and indiv2ID from hhID
		hhPool.get(hhID).getResidentsID().remove(indiv1ID);
		hhPool.get(hhID).getResidentsID().remove(indiv2ID);
//		// if hhID has no residents left, removes it from hhPool
//		if (hhPool.get(hhID).getResidentsID()==null || hhPool.get(hhID).getResidentsID().size()==0) {
//			hhPool.remove(hhID);
//		} else { // else, cleans it and reclassifies it
//			hhPool.get(hhID).assignHholdTypes();
//		}
		
		// constructs a new household
		Household hhold = new Household(calculateNewHholdID(), HholdTypes.Unknown, newResidents, HardcodedData.unknown, HardcodedData.unknown);
		hhold.assignAggreHholdType();
		// adds this household to hhPool
		addHholdToPopulation(hhold);
	}
	
	
	/**
	 * 
	 * @param marryIndivsByHhID
	 */
	private static void marryIndivsFrom2Hholds(HashMap<Integer,ArrayList<Integer>> marryIndivsByHhID) {
		// randomly picks 2 households from the collection of households that have individuals who want to marry (i.e. from marryIndivsByHhID) 
		int[] hhIDs = ArrayHandler.pickRandomFromArray(ArrayHandler.toInt(marryIndivsByHhID.keySet()), null, 2, HardcodedData.random);
		Integer h1ID = hhIDs[0];
		Integer h2ID = hhIDs[1];
		// picks a male from marryInH1
		Integer maleH1 = pickIndivFromList(marryIndivsByHhID.get(h1ID), Genders._male);
		// picks a female from marryInH1
		Integer femaleH1 = pickIndivFromList(marryIndivsByHhID.get(h1ID), Genders._female);
		
		// picks a male from marryInH2
		Integer maleH2 = pickIndivFromList(marryIndivsByHhID.get(h2ID), Genders._male);
		// picks a female from marryInH2
		Integer femaleH2 = pickIndivFromList(marryIndivsByHhID.get(h2ID), Genders._female);
		
		// matches and marries them
		if (maleH1!=null && femaleH2!=null) {
			// removes maleH1 from marryInH1 and femaleH2 from marryInH2 and updates marryIndivsByHhID
			removeMarryIndivFromAvailList(maleH1, femaleH2, h1ID, h2ID, marryIndivsByHhID);
			// marries them by removing them from their current households and putting them into a new household
			marryIndividuals(maleH1, femaleH2, h1ID, h2ID, marryIndivsByHhID);
		} 
		else if (femaleH1!=null && maleH2!=null) {
			// removes femaleH1 from marryInH1 and maleH2 from marryInH2 and updates marryIndivsByHhID
			removeMarryIndivFromAvailList(femaleH1, maleH2, h1ID, h2ID, marryIndivsByHhID);
			// marries them by removing them from their current households and putting them into a new household
			marryIndividuals(femaleH1, maleH2, h1ID, h2ID, marryIndivsByHhID);
		} 
		else if (femaleH1!=null && femaleH2!=null) {
			// removes femaleH1 from marryInH1 and femaleH2 from marryInH2 and updates marryIndivsByHhID
			removeMarryIndivFromAvailList(femaleH1, femaleH2, h1ID, h2ID, marryIndivsByHhID);
			// marries them by removing them from their current households and putting them into a new household
			marryIndividuals(femaleH1, femaleH2, h1ID, h2ID, marryIndivsByHhID);
		} 
		else if (maleH1!=null && maleH2!=null) {
			// removes maleH1 from marryInH1 and maleH2 from marryInH2 and updates marryIndivsByHhID
			removeMarryIndivFromAvailList(maleH1, maleH2, h1ID, h2ID, marryIndivsByHhID);
			// marries them by removing them from their current households and putting them into a new household
			marryIndividuals(maleH1, maleH2, h1ID, h2ID, marryIndivsByHhID);
		}
	}
	
	
	
	/**
	 * marries individuals indiv1ID and indiv2ID by removing them from their current households and putting them into a new household.
	 * if they are LoneParent, all children not available for marrying (i.e. not in marryIDsivsByHhold) must accompany them to the new household.
	 * @param indiv1ID
	 * @param indiv2ID
	 * @param h1ID
	 * @param h2ID
	 * @param marryIDsByHhold
	 */
	private static void marryIndividuals(Integer indiv1ID, Integer indiv2ID, Integer h1ID, Integer h2ID, HashMap<Integer,ArrayList<Integer>> marryIDsByHhold) {
		
		// creates an ArrayList of ID of individuals in the new household, starting with indiv1ID and indiv2ID
		ArrayList<Integer> newResidents = new ArrayList<Integer>();
		newResidents.add(indiv1ID);
		newResidents.add(indiv2ID);
		
		// if indiv1ID is a lone parent
		if (indivPool.get(indiv1ID).getHhRel().equals(HholdRelSP.LoneParent)) {
			// adds any children (U15Child, Student, O15Child) in h1ID that will follow indiv1ID to the new household AND removes these individuals from h1ID.
			// Note that the relationship of these individuals don't change in the new household.
			System.out.println("marryIndividuals: Hhold " + h1ID + " has LoneParent, hhSize " + hhPool.get(h1ID).getResidentsID().size());
			Iterator<Integer> itIndiv = hhPool.get(h1ID).getResidentsID().iterator();
			while (itIndiv.hasNext()) {
				Integer indID = itIndiv.next();
				
				if (indID.equals(indiv1ID)) continue;
				
				if (indivPool.get(indID).getHhRel().equals(HholdRelSP.U15Child)) {
					newResidents.add(indID);
					// removes indID from h1ID
					itIndiv.remove();
					System.out.println("\tRemoved 1 resident from hhold " + h1ID + ", hhSize " + hhPool.get(h1ID).getResidentsID().size());
				} else if (indivPool.get(indID).getHhRel().equals(HholdRelSP.Student) || indivPool.get(indID).getHhRel().equals(HholdRelSP.O15Child)) {
					if (marryIDsByHhold.get(h1ID)!=null && !marryIDsByHhold.get(h1ID).contains(indID)) {
						newResidents.add(indID);
						// removes indID from h1ID
						itIndiv.remove();
						System.out.println("\tRemoved 1 resident from hhold " + h1ID + ", hhSize " + hhPool.get(h1ID).getResidentsID().size());
					}
				}
			}
		}
		
		// if indiv2ID is a lone parent
		if (indivPool.get(indiv2ID).getHhRel().equals(HholdRelSP.LoneParent)) {
			// adds any individuals in h2ID that will follow indiv2ID to the new household AND removes these individuals from h2ID.
			// Note that the relationship of these individuals don't change in the new household.
			System.out.println("marryIndividuals: Hhold " + h2ID + " has LoneParent, hhSize " + hhPool.get(h2ID).getResidentsID().size());
			Iterator<Integer> itIndiv = hhPool.get(h2ID).getResidentsID().iterator();
			while (itIndiv.hasNext()) {
				Integer indID = itIndiv.next();
				
				if (indID.equals(indiv2ID)) continue;
				
				if (indivPool.get(indID).getHhRel().equals(HholdRelSP.U15Child)) {
					newResidents.add(indID);
					// removes indID from h2ID
					itIndiv.remove();
					System.out.println("\tRemoved 1 resident from hhold " + h2ID + ", hhSize " + hhPool.get(h2ID).getResidentsID().size());
				} else if (indivPool.get(indID).getHhRel().equals(HholdRelSP.Student) || indivPool.get(indID).getHhRel().equals(HholdRelSP.O15Child)) {
					if (marryIDsByHhold.get(h2ID)!=null && !marryIDsByHhold.get(h2ID).contains(indID)) {
						newResidents.add(indID);
						// removes indID from h2ID
						itIndiv.remove();
						System.out.println("\tRemoved 1 resident from hhold " + h2ID + ", hhSize " + hhPool.get(h2ID).getResidentsID().size());
					}
				}
			}
		}
		
		// removes indiv1ID from h1ID
		hhPool.get(h1ID).getResidentsID().remove(indiv1ID);
		// changes relationship of indiv1ID to Married
		indivPool.get(indiv1ID).setHhRel(HholdRelSP.Married);
//		System.out.println("\n\tnewly wed " + indiv1ID + ", " + indivPool.get(indiv1ID).getHhRel().toString());

		// removes indiv2ID from h2ID
		hhPool.get(h2ID).getResidentsID().remove(indiv2ID);
		// changes relationship of indiv2ID to Married
		indivPool.get(indiv2ID).setHhRel(HholdRelSP.Married);
//		System.out.println("\tnewly wed " + indiv2ID + ", " + indivPool.get(indiv2ID).getHhRel().toString());
		
		
//		// if h1ID has no residents, removes it from hhPool
//		if (hhPool.get(h1ID).getResidentsID()==null || hhPool.get(h1ID).getResidentsID().size()==0) {
//			hhPool.remove(h1ID);
//		} else { // else, cleans it and reassigns its hhType
//			hhPool.get(h1ID).assignAggreHholdType();
//		}
//		
//		// if h2ID has no residents, removes it from hhPool
//		if (hhPool.get(h2ID).getResidentsID()==null || hhPool.get(h2ID).getResidentsID().size()==0) {
//			hhPool.remove(h2ID);
//		} else { // else, cleans it and reassigns its hhType
//			hhPool.get(h2ID).assignAggreHholdType();
//		}
		
		// constructs a new household
		Household newHhold = new Household(calculateNewHholdID(), HholdTypes.Unknown, newResidents, HardcodedData.unknown, HardcodedData.unknown);
		newHhold.assignAggreHholdType();

		// adds this household to hhPool
		addHholdToPopulation(newHhold);
	}
	
	
	/**
	 * 
	 * @param marryIDH1
	 * @param marryIDH2
	 * @param h1ID
	 * @param h2ID
	 * @param availMarryByHh
	 */
	private static void removeMarryIndivFromAvailList(Integer marryIDH1, Integer marryIDH2, Integer h1ID, Integer h2ID, HashMap<Integer,ArrayList<Integer>> availMarryByHh) {
		availMarryByHh.get(h1ID).remove(marryIDH1);
		availMarryByHh.get(h2ID).remove(marryIDH2);
	}
	
	/**
	 * picks the ID of the first individual with the given gender in the given indivIDList.
	 * @param indivIDList
	 * @param gender
	 * @return
	 */
	private static Integer pickIndivFromList(ArrayList<Integer> indivIDList, Genders gender) {
		Integer idPicked = null;
		for (Integer indivID : indivIDList) {
			if (indivPool.get(indivID).getGender().equals(gender)) {
				idPicked = indivID;
				break;
			}
		}
		return idPicked;
	}
	
	
	/**
	 * 
	 */
	private static void divorce() {
		ArrayList<Integer> newDevorcees = new ArrayList<Integer>();
		
		Iterator<Entry<Integer, Household>> itHhold = Population.getHhPool().entrySet().iterator();
		// for each household in the hhPool
		while (itHhold.hasNext()) {
			Map.Entry<Integer, Household> entry = itHhold.next();
			
			Integer hhID = entry.getKey();
			
			ArrayList<Integer> marriedIDs = hhPool.get(hhID).getIndivIDOfHHRel(HholdRelSP.Married);
			
			// if this household has 2 married individuals and at least one of them wants to divorce
			if (marriedIDs.size()==2 && 
					(indivPool.get(marriedIDs.get(0)).isDivorced() || indivPool.get(marriedIDs.get(1)).isDivorced())) {
				
				// determines Id of the individual leaving the household
				Integer leavingIndivID = determineIDLeavingDevorcedIndiv(marriedIDs);

				newDevorcees.add(leavingIndivID);
				
				// removes the ID of the leaving individual from the list of residents in the existing household
				hhPool.get(hhID).getResidentsID().remove(leavingIndivID);
				// changes the household relationship of the remaining married individual to LoneParent.
				Integer remMarriedIndivID = marriedIDs.get(0);
				if (remMarriedIndivID.equals(leavingIndivID)) 
					remMarriedIndivID = marriedIDs.get(1);
				indivPool.get(remMarriedIndivID).setHhRel(HholdRelSP.LoneParent);
				
				// cleans and reclassifies the type of the existing household
				hhPool.get(hhID).assignAggreHholdType();
			}
		}
		
		// allocates each of the people who got divorced into one new household
		for (Integer indivID : newDevorcees) {
			makeNewHholdForDevorcee(indivID);
		}
	}
	
	
	/**
	 * decides the person leaving the household of a divorced couple.
	 * The leaving person is a male if the couple is different sex. If the couple is same sex, the leaving person is any of the 2.
	 * 
	 * @param marriedIDs
	 * @return
	 */
	private static Integer determineIDLeavingDevorcedIndiv(ArrayList<Integer> marriedIDs) {
		Integer leavingIndivID = marriedIDs.get(1);
		// if these two married individuals have different gender
		if (!indivPool.get(marriedIDs.get(0)).getGender().equals(indivPool.get(marriedIDs.get(1)).getGender())) {
			// the leaving individual is the male
			if (indivPool.get(marriedIDs.get(0)).getGender().equals(Genders._male))
				leavingIndivID = marriedIDs.get(0);
			else 
				leavingIndivID = marriedIDs.get(1);
		}
		return leavingIndivID;
	}
	
	
	/**
	 * creates a new household for each devorcee in the population.
	 * The household type will be NF. The relationship of the devorcee is set to LonePerson
	 * @param leavingIndivID
	 */
	private static void makeNewHholdForDevorcee(Integer leavingIndivID) {
		// sets the relationship of the leaving individual to NonFamilyMember
		indivPool.get(leavingIndivID).setHhRel(HholdRelSP.LonePerson);
		
		// creates a new household for the leaving individual
		int newhhID = calculateNewHholdID();
		ArrayList<Integer> newResidentIDs = new ArrayList<Integer>();
		newResidentIDs.add(leavingIndivID);
		
		Household newHhold = new Household(newhhID, HholdTypes.NF, newResidentIDs, HardcodedData.unknown, HardcodedData.unknown);
		addHholdToPopulation(newHhold);
	}
	
	/**
	 * 
	 */
	private static void ageAndPassAway() {
		Iterator<Entry<Integer, Household>> itHhold = Population.getHhPool().entrySet().iterator();
		// for each household in the hhPool
		while (itHhold.hasNext()) {
			Map.Entry<Integer, Household> entry = itHhold.next();
			
			Integer hhID = entry.getKey();
			// for each resident in this household
			Iterator<Integer> itIndiv = hhPool.get(hhID).getResidentsID().iterator();
			while (itIndiv.hasNext()) {
				Integer indivID = itIndiv.next();
				// ages and updates relationship of this individual if necessary
				if (indivPool.get(indivID)==null) {
					System.out.println("\nWARNING indivPool.get(indivID)==null, indivID = " + indivID + ", hhID = " + hhID);
				}
				
				indivPool.get(indivID).age();
				// is this individual dead
				if (indivPool.get(indivID).isDead()) {
					indivPool.remove(indivID);
					itIndiv.remove();
				}
			}

			// if there is no residents left in this household, remove it from hhPool
			if (hhPool.get(hhID).getResidentsID()==null || hhPool.get(hhID).getResidentsID().size()==0) {
				itHhold.remove();
			} else {
				// updates the type of this household to correctly reflect the new composition of residents
				hhPool.get(hhID).assignAggreHholdType();
			}
		}
	}
	
	
	
	
	/**
	 * checks and removes households in the population that have no residents.
	 * If there is at least one resident in a household, reassesses and reassigns household type.
	 */
	public static void cleanUpHholdPool() {
		Iterator<Entry<Integer, Household>> itHhold = Population.getHhPool().entrySet().iterator();
		// for each household in the hhPool
		while (itHhold.hasNext()) {
			Map.Entry<Integer, Household> entry = itHhold.next();
			Integer hhID = entry.getKey();
			if (hhPool.get(hhID).getResidentsID()==null || hhPool.get(hhID).getResidentsID().size()==0) {
				itHhold.remove();
			} else {
				hhPool.get(hhID).assignAggreHholdType();
			}
		}
	}
	
	/**
	 * adds a new household hhold to hhPool. If ID of the new household hhold already exists in hhPool, hhold is not added (i.e the existing household is not replaced by hhold).
	 * @param hhold
	 */
	public static void addHholdToPopulation(Household hhold) {
		if (!hhPool.containsKey(hhold.getId())) {
			hhPool.put((Integer)hhold.getId(),hhold);
		}
	}
	
	
	/**
	 * 
	 * @return an ID of a new household, which is calculated by adding 1 to the max ID in the household pool.
	 */
	private static int calculateNewHholdID() {
		int[] existingHhIDs = ArrayHandler.toInt(hhPool.keySet());
		return (existingHhIDs[ArrayHandler.getIndexOfMax(existingHhIDs)] + 1);
	}
	
	/**
	 * 
	 * @return an ID of a new individual, which is calculated by adding 1 to the max ID in the individual pool.
	 */
	private static int calculateNewIndivID() {
		int[] existingIndivIDs = ArrayHandler.toInt(indivPool.keySet());
		return (existingIndivIDs[ArrayHandler.getIndexOfMax(existingIndivIDs)] + 1);
	}
	
	
	/**
	 * 
	 * @param outputCSV
	 */
	public static void outputPopulation(String outputCSV) {
		ArrayList<String[]> outputPop = new ArrayList<String[]>();
		String[] header = new String[] {"hhID", "indivID", "age", "gender", "hhRel", "hhType", "zoneDescription", "zoneName"};
		//outputPop.add(header);

		for (Integer hhID : hhPool.keySet()) {
			ArrayList<Integer> residentsID = hhPool.get(hhID).getResidentsID();
			
			for (Integer indivID : residentsID) {
				String[] details = new String[8];
				details[0] = Integer.toString(hhID);
				details[1] = Integer.toString(indivID);
				details[2] = Integer.toString(indivPool.get(indivID).getAge());
				details[3] = indivPool.get(indivID).getGender().toString();
				details[4] = indivPool.get(indivID).getHhRel().toString();
				details[5] = hhPool.get(hhID).getHhType().toString();
				details[6] = hhPool.get(hhID).getZoneDescription();
				details[7] = hhPool.get(hhID).getZoneName();
				outputPop.add(details);
				
			}
		}
		
		TextFileHandler.writeToCSV(outputCSV, header, outputPop);
	}
	
	public static HashMap<Integer, Household> getHhPool() {
		return hhPool;
	}
	public static void setHhPool(HashMap<Integer, Household> hhPool) {
		Population.hhPool = hhPool;
	}
	public static HashMap<Integer, Individual> getIndivPool() {
		return indivPool;
	}
	public static void setIndivPool(HashMap<Integer, Individual> indivPool) {
		Population.indivPool = indivPool;
	}
}
