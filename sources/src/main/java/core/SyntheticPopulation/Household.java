package core.SyntheticPopulation;

import java.util.ArrayList;

import core.HardcodedData.*;

public class Household {
	private int id;
	private HholdTypes hhType;
	private AggreHholdTypes aggreHhType;
	private ArrayList<Integer> residentsID;
	private String zoneName;
	private String zoneDescription;
	
	public Household(int newId, HholdTypes newHhType, ArrayList<Integer> newResidents, String newZone, String newZoneDescription) {
		this.setId(newId);
		this.setHhType(newHhType);
		this.setResidentsID(newResidents);
		this.setZoneName(newZone);
		this.setZoneDescription(newZoneDescription);
		this.assignAggreHholdType();
	}
	
	
	/**
	 * assigns initial AggreHholdType to households output from SP construction.
	 * The method assumes that the composition of relationships in a household is correct (e.g. a household should not have a Married individual and a LoneParent individual).
	 * It therefore doesn't check and correct this composition.
	 */
	public void assignAggreHholdType() {
		if (this.getResidentsID()==null || this.getResidentsID().size()==0) return;
		
		ArrayList<Integer> marriedID = this.getIndivIDOfHHRel(HholdRelSP.Married);
		ArrayList<Integer> loneParentID = this.getIndivIDOfHHRel(HholdRelSP.LoneParent);
		ArrayList<Integer> u15ChildID = this.getIndivIDOfHHRel(HholdRelSP.U15Child);
		ArrayList<Integer> o15ChildStudentID = this.getIndivIDOfHHRel(HholdRelSP.Student);
		o15ChildStudentID = this.getIndivIDOfHHRel(HholdRelSP.O15Child, o15ChildStudentID);
		
		if (marriedID.size()==2) {
			if (u15ChildID.size()>0) {
				if (o15ChildStudentID.size()>0) {
					this.setAggreHhType(AggreHholdTypes.coupleU15O15);
				} else {
					this.setAggreHhType(AggreHholdTypes.coupleU15);
				}
			} else {
				if (o15ChildStudentID.size()>0) {
					this.setAggreHhType(AggreHholdTypes.coupleO15);
				} else {
					this.setAggreHhType(AggreHholdTypes.couple);
				}
			}
		} else {
			if (loneParentID.size()==1) {
				if (u15ChildID.size()>0) {
					if (o15ChildStudentID.size()>0) {
						this.setAggreHhType(AggreHholdTypes.loneParentU15O15);
					} else {
						this.setAggreHhType(AggreHholdTypes.loneParentU15);
					}
				} else {
					if (o15ChildStudentID.size()>0) {
						this.setAggreHhType(AggreHholdTypes.loneParentO15);
					} else {
						this.setAggreHhType(AggreHholdTypes.Other);
					}
				}
			} else {
				this.setAggreHhType(AggreHholdTypes.Other);
			}
		}
	}
	
	
	/**
	 * 
	 */
//	public void assignAggreHholdTypes() {
//		if (this.getResidentsID()==null || this.getResidentsID().size()==0) return;
//
////		this.correctRelationshipInHhold();
//
//		ArrayList<Integer> marriedID = this.getIndivIDOfHHRel(HholdRelSP.Married);
//		ArrayList<Integer> loneParentID = this.getIndivIDOfHHRel(HholdRelSP.LoneParent);
//		ArrayList<Integer> u15ChildID = this.getIndivIDOfHHRel(HholdRelSP.U15Child);
//		ArrayList<Integer> o15ChildStudentID = this.getIndivIDOfHHRel(HholdRelSP.Student);
//		o15ChildStudentID = this.getIndivIDOfHHRel(HholdRelSP.O15Child, o15ChildStudentID);
//
//		if (marriedID.size()==2) {
//			if (u15ChildID.size()>0) {
//				if (o15ChildStudentID.size()>0) {
//					this.setAggreHhType(AggreHholdTypes.coupleU15O15);
//				} else {
//					this.setAggreHhType(AggreHholdTypes.coupleU15);
//				}
//			} else {
//				if (o15ChildStudentID.size()>0) {
//					this.setAggreHhType(AggreHholdTypes.coupleO15);
//				} else {
//					this.setAggreHhType(AggreHholdTypes.couple);
//				}
//			}
//		} else {
//			if (loneParentID.size()==1) {
//				if (u15ChildID.size()>0) {
//					if (o15ChildStudentID.size()>0) {
//						this.setAggreHhType(AggreHholdTypes.loneParentU15O15);
//					} else {
//						this.setAggreHhType(AggreHholdTypes.loneParentU15);
//					}
//				} else {
//					if (o15ChildStudentID.size()>0) {
//						this.setAggreHhType(AggreHholdTypes.loneParentO15);
//					} else {
//						this.setAggreHhType(AggreHholdTypes.Other);
//					}
//				}
//			} else {
//				this.setAggreHhType(AggreHholdTypes.Other);
//			}
//		}
//	}
	
	
	/**
	 * 
	 */
	public void correctRelationshipInHhold() {
		if (this.getResidentsID()==null || this.getResidentsID().size()==0) return;
		
		if (this.getResidentsID().size()==1) {
			Population.getIndivPool().get(this.getResidentsID().get(0)).setHhRel(HholdRelSP.LonePerson);
			return;
		}
		
		ArrayList<Integer> marriedID = this.getIndivIDOfHHRel(HholdRelSP.Married);
		ArrayList<Integer> loneParentID = this.getIndivIDOfHHRel(HholdRelSP.LoneParent);
		ArrayList<Integer> u15ChildID = this.getIndivIDOfHHRel(HholdRelSP.U15Child);
		ArrayList<Integer> o15ChildStudentID = this.getIndivIDOfHHRel(HholdRelSP.Student);
		o15ChildStudentID = this.getIndivIDOfHHRel(HholdRelSP.O15Child, o15ChildStudentID);
		
		ArrayList<Integer> relativeID = this.getIndivIDOfHHRel(HholdRelSP.Relative);
		
		ArrayList<Integer> nfPeople = this.getIndivIDOfHHRel(HholdRelSP.LonePerson);
		nfPeople = this.getIndivIDOfHHRel(HholdRelSP.GroupHhold,nfPeople);
		
		
		if (marriedID.size()>=2) {
			// randomly changes (married.size()-2) individuals to Relative;
			for (int i=2; i<=marriedID.size()-1; i++) {
				Population.getIndivPool().get(marriedID.get(i)).setHhRel(HholdRelSP.Relative);
			}
			// change any individual in loneParentID to Relative;
			for (Integer id : loneParentID) {
				Population.getIndivPool().get(id).setHhRel(HholdRelSP.Relative);
			}
			// change any individual in nfPeople to Relative;
			for (Integer id : nfPeople) {
				Population.getIndivPool().get(id).setHhRel(HholdRelSP.Relative);
			}
		} else if (marriedID.size()==1) {
			if (loneParentID.size()>=1) {// if there is at least 1 LoneParent
				// changes the married individual to Relative
				Population.getIndivPool().get(marriedID.get(0)).setHhRel(HholdRelSP.Relative);
				// changes all loneparent (except the 1st one) to Relative
				for (int i=1; i<=loneParentID.size()-1; i++) {
					Population.getIndivPool().get(loneParentID.get(i)).setHhRel(HholdRelSP.Relative);
				}
				// changes all nfPeople (if any) to Relative
				for (Integer id : nfPeople) {
					Population.getIndivPool().get(id).setHhRel(HholdRelSP.Relative);
				}
				if (u15ChildID.size()==0 && o15ChildStudentID.size()==0) { // there is no children in this household
					// changes the remaining LoneParent to Relative;
					Population.getIndivPool().get(loneParentID.get(0)).setHhRel(HholdRelSP.Relative);
				}
			} else { // there is no lone parent
				if (u15ChildID.size()>0 || o15ChildStudentID.size()>0) { // if there's at least 1 child
					// changes married individual to LoneParent
					Population.getIndivPool().get(marriedID.get(0)).setHhRel(HholdRelSP.LoneParent);
					// changes all nfPeople (if any) to Relative
					for (Integer id : nfPeople) {
						Population.getIndivPool().get(id).setHhRel(HholdRelSP.Relative);
					}
				} else { // no children and there are more than 1 person in the household
					// converts all of them to Relative
					for (Integer residentID : this.getResidentsID()) {
						Population.getIndivPool().get(residentID).setHhRel(HholdRelSP.Relative);
					}
				}
			}
		} else { // no married individuals in this household
			if (loneParentID.size()>=1) { // if there is at least 1 LoneParent
				if (u15ChildID.size()>0 || o15ChildStudentID.size()>0) { // if there's at least 1 child
					// changes all loneparent (except the 1st one) to Relative
					for (int i=1; i<=loneParentID.size()-1; i++) {
						Population.getIndivPool().get(loneParentID.get(i)).setHhRel(HholdRelSP.Relative);
					}
					// changes all nfPeople (if any) to Relative
					for (Integer id : nfPeople) {
						Population.getIndivPool().get(id).setHhRel(HholdRelSP.Relative);
					}
				} else { //there are no children in this household and there are more than 1 person in this household
					// converts all of them to Relative
					for (Integer residentID : this.getResidentsID()) {
						Population.getIndivPool().get(residentID).setHhRel(HholdRelSP.Relative);
					}
				}
			} else { // no LoneParent in this household
				if (u15ChildID.size()>0 || o15ChildStudentID.size()>0 || relativeID.size()>0) { // if there's at least 1 child or 1 relative
					// converts all residents to Relative, including any non-family members
					for (Integer residentID : this.getResidentsID()) {
						Population.getIndivPool().get(residentID).setHhRel(HholdRelSP.Relative);
					}
				} else { // there are no family members in this household, only group household member
					// assigns GrHholdMember to residents 
					for (Integer residentID : this.getResidentsID()) {
						Population.getIndivPool().get(residentID).setHhRel(HholdRelSP.GroupHhold);
					}
				}
			}
		}
	}

	
	public ArrayList<Integer> getIndivIDOfHHRel(HholdRelSP hhRel) {
		ArrayList<Integer> indivIDList = new ArrayList<Integer>();
		for (Integer indivID : this.getResidentsID()) {
			if (Population.getIndivPool().get(indivID).getHhRel().equals(hhRel)) {
				indivIDList.add(indivID);
			}
		}
		return indivIDList;
	}
	
	
	public ArrayList<Integer> getIndivIDOfHHRel(HholdRelSP hhRel, ArrayList<Integer> existingList) {
		for (Integer indivID : this.getResidentsID()) {
			if (Population.getIndivPool().get(indivID).getHhRel().equals(hhRel)) {
				existingList.add(indivID);
			}
		}
		return existingList;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public HholdTypes getHhType() {
		return hhType;
	}

	public void setHhType(HholdTypes hhType) {
		this.hhType = hhType;
	}

	public ArrayList<Integer> getResidentsID() {
		return residentsID;
	}

	public void setResidentsID(ArrayList<Integer> residentsID) {
		this.residentsID = residentsID;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	
	public String getZoneDescription() {
		return zoneDescription;
	}

	public void setZoneDescription(String zoneDescription) {
		this.zoneDescription = zoneDescription;
	}

	public AggreHholdTypes getAggreHhType() {
		return aggreHhType;
	}

	public void setAggreHhType(AggreHholdTypes aggreHhType) {
		this.aggreHhType = aggreHhType;
	}
}
