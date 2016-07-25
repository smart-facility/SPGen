package core.SyntheticPopulation;

import core.ArrayHandler;
import core.HardcodedData;
import core.HardcodedData.*;
import core.Rates;


public class Individual {
	private int id;
	private int age;
	private Genders gender;
	private HholdRelSP hhRel;
	private int incomeWkly;
	private transient int[][] travelDiariesWeekdays;
	
	/**
	 * 
	 * @param newId
	 * @param newAge
	 * @param newGender
	 * @param newHhRel
	 * @param newIncome
	 */
	public Individual(int newId, int newAge, Genders newGender, HholdRelSP newHhRel, int newIncome) {
		this.setId(newId);
		this.setAge(newAge);
		this.setGender(newGender);
		this.setHhRel(newHhRel);
		this.setIncomeWkly(newIncome);
	}
	
	/**
	 * 
	 * @param newId
	 * @param newAge
	 * @param newGender
	 * @param newHhRel
	 */
	public Individual(int newId, int newAge, Genders newGender, HholdRelSP newHhRel) {
		this.setId(newId);
		this.setAge(newAge);
		this.setGender(newGender);
		this.setHhRel(newHhRel);
	}

	/**
	 * increases the age of this individual by 1.
	 * changes his/her relationship to O15Child if he/she is an U15Child and age>HardcodedData.maxAgeU15.
	 * changes his/her relationship to O15Child if he/she is an Student and age>HardcodedData.maxAgeStu.
	 */
	public void age() {
		this.setAge(this.getAge()+1);
		if (this.getHhRel().equals(HholdRelSP.U15Child) && this.getAge()>HardcodedData.maxAgeU15) {
			this.setHhRel(HholdRelSP.O15Child);
		} else if (this.getHhRel().equals(HholdRelSP.Student) && this.getAge()>HardcodedData.maxAgeStu) {
			this.setHhRel(HholdRelSP.O15Child);
		}
	}
	
	
	/**
	 * determines if this individual is dead by comparing a random double number with the death probability of his/her age and gender.
	 * @return
	 */
	public boolean isDead() {
		double deathProb;
		if (this.getGender().equals(Genders._female)) {
			int queryAge = (Integer)this.getAge();
			int maxAgeInRates = ArrayHandler.max(ArrayHandler.toInt(Rates.getDeathRatesFemale().keySet()));
			if (queryAge>maxAgeInRates) 
				queryAge = (Integer)maxAgeInRates;
			
			if (!Rates.getDeathRatesFemale().containsKey((Integer)queryAge)) 
				return false;
			deathProb = Rates.getDeathRatesFemale().get((Integer)queryAge);
		} 
		else {
			int queryAge = (Integer)this.getAge();
			int maxAgeInRates = ArrayHandler.max(ArrayHandler.toInt(Rates.getDeathRatesMale().keySet()));
			if (queryAge>maxAgeInRates) 
				queryAge = (Integer)maxAgeInRates;
			
			if (!Rates.getDeathRatesMale().containsKey((Integer)queryAge)) 
				return false;
			deathProb = Rates.getDeathRatesMale().get((Integer)queryAge);
		}
		
		double randDouble = HardcodedData.random.nextDouble();
		if (randDouble<=deathProb) return true;
		else return false;
	}
	
	
	/**
	 * determines if this individual is divorced by comparing a random double number with the divorce probability of his/her age and gender.
	 * @return
	 */
	public boolean isDivorced() {
		double divorceProb;
		if (this.getGender().equals(Genders._female)) {
			int queryAge = (Integer)this.getAge();
			int maxAgeInRates = ArrayHandler.max(ArrayHandler.toInt(Rates.getDivorceRatesFemale().keySet()));
			if (queryAge>maxAgeInRates) 
				queryAge = (Integer)maxAgeInRates;
			
			if (!Rates.getDivorceRatesFemale().containsKey((Integer)queryAge))
				return false;
			divorceProb = Rates.getDivorceRatesFemale().get((Integer)queryAge);
		} else {
			int queryAge = (Integer)this.getAge();
			int maxAgeInRates = ArrayHandler.max(ArrayHandler.toInt(Rates.getDivorceRatesMale().keySet()));
			if (queryAge>maxAgeInRates) 
				queryAge = (Integer)maxAgeInRates;
			
			if (!Rates.getDivorceRatesMale().containsKey((Integer)queryAge))
				return false;
			divorceProb = Rates.getDivorceRatesMale().get((Integer)queryAge);
		}
		
		double randDouble = HardcodedData.random.nextDouble();
		if (randDouble<=divorceProb) return true;
		else return false;
	}
	
	
	/**
	 * determines if this individual is getting married by comparing a random double number with the marriage probability of his/her age and gender.
	 * if this individual is already married or is an u15Child, return false.
	 * @return
	 */
	public boolean isMarried() {
		if (this.getHhRel().equals(HholdRelSP.Married) || this.getHhRel().equals(HholdRelSP.U15Child)) 
			return false;
		
		double marryProb;
		if (this.getGender().equals(Genders._female)) {
			int queryAge = (Integer)this.getAge();
			int maxAgeInRates = ArrayHandler.max(ArrayHandler.toInt(Rates.getMarriageRatesFemale().keySet()));
			if (queryAge>maxAgeInRates) 
				queryAge = (Integer)maxAgeInRates;
			
			if (!Rates.getMarriageRatesFemale().containsKey((Integer)queryAge))
				return false;
			marryProb = Rates.getMarriageRatesFemale().get((Integer)queryAge);
		} else {
			int queryAge = (Integer)this.getAge();
			int maxAgeInRates = ArrayHandler.max(ArrayHandler.toInt(Rates.getMarriageRatesMale().keySet()));
			if (queryAge>maxAgeInRates) 
				queryAge = (Integer)maxAgeInRates;
			
			if (!Rates.getMarriageRatesMale().containsKey((Integer)queryAge))
				return false;
			marryProb = Rates.getMarriageRatesMale().get((Integer)queryAge);
		}
		
		double randDouble = HardcodedData.random.nextDouble();
		if (randDouble<=marryProb) return true;
		else return false;
	}
	
	
	/**
	 * 
	 * @param nCrntChildren
	 * @return
	 */
	public boolean isHavingBaby(int nCrntChildren) {
		
		if (this.getHhRel().equals(HholdRelSP.Married) && this.getGender().equals(Genders._female)) {
			
			int queryAge = (Integer)this.getAge();
			int maxAgeInRates = ArrayHandler.max(ArrayHandler.toInt(Rates.getBirthRates().keySet()));
			if (queryAge>maxAgeInRates) 
				queryAge = (Integer)maxAgeInRates;
			
			if (!Rates.getBirthRates().containsKey((Integer)queryAge)) {
				return false;
			}
			
			int orderNewBaby = nCrntChildren + 1; // if nCrntChildren is 0, then this female is having 1st baby (orderNewBaby=1), and so on. 
			
			int childColumn = orderNewBaby-1;
			if (orderNewBaby>HardcodedData.InputBirthRatesColumn.sixthChildMore.getIndex()) {
				childColumn = HardcodedData.InputBirthRatesColumn.sixthChildMore.getIndex()-1;
			}
			double havingBabyProb = Rates.getBirthRates().get((Integer)queryAge)[childColumn];
			
			double randDouble = HardcodedData.random.nextDouble();
			if (randDouble<=havingBabyProb) return true;
			else return false;
		} else {
			return false;
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Genders getGender() {
		return gender;
	}

	public void setGender(Genders gender) {
		this.gender = gender;
	}

	public HholdRelSP getHhRel() {
		return hhRel;
	}

	public void setHhRel(HholdRelSP hhRel) {
		this.hhRel = hhRel;
	}
	
	public int getIncomeWkly() {
		return incomeWkly;
	}

	public void setIncomeWkly(int newIncome) {
		this.incomeWkly = newIncome;
	}
	
	public int[][] getTravelDiariesWeekdays() {
		return this.travelDiariesWeekdays;
	}

	public void setTravelDiariesWeekdays(int[][] travelDiariesWeekdays) {
		if (travelDiariesWeekdays != null) {
			int[][] inputTravelDiariesWeekdays = travelDiariesWeekdays.clone();
			this.travelDiariesWeekdays = new int[inputTravelDiariesWeekdays.length][inputTravelDiariesWeekdays[0].length];
			System.arraycopy(inputTravelDiariesWeekdays, 0, this.travelDiariesWeekdays, 0, inputTravelDiariesWeekdays.length);
		}
	}
}
