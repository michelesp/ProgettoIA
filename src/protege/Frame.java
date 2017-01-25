package protege;

import java.util.ArrayList;

public class Frame {
	
	private String term,type,category,medic_cohesion;
	private ArrayList<Info> extracted_info;
	private int recurrency;
	
	public Frame(String term,String type,String category,String med_coh,ArrayList<Info> ext,int recurrency){
		this.term=term;
		this.type=type;
		this.category=category;
		medic_cohesion=med_coh;
		extracted_info=ext;
		this.recurrency=recurrency;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setMedic_cohesion(String medic_cohesion) {
		this.medic_cohesion = medic_cohesion;
	}

	public void setExtracted_info(ArrayList<Info> extracted_info) {
		this.extracted_info = extracted_info;
	}

	public void setRecurrency(int recurrency) {
		this.recurrency = recurrency;
	}

	public String getTerm() {
		return term;
	}
	
	public String getType() {
		return type;
	}

	public String getCategory() {
		return category;
	}

	public String getMedic_cohesion() {
		return medic_cohesion;
	}

	public ArrayList<Info> getExtracted_info() {
		return extracted_info;
	}

	public int getRecurrency() {
		return recurrency;
	}
	
	

}
