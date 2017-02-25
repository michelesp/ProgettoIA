package protege;


import java.time.LocalDateTime;
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
	public Frame(String term, String type, String category)
	{
		this.term = term;
		this.type = type;
		this.category = category;
		extracted_info = new ArrayList<Info>();
	}
	public void addInfo(String info, LocalDateTime datetime)
	{	
		if(info!=null || info.length()<=0)
		{
			Info toAdd = new Info(info,(datetime!=null?datetime.toString():""));
		//	for(Info i : extracted_info)
		//		if(i.equals(toAdd))
		//			return;
			
			extracted_info.add(toAdd);
		}
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

	public void addRecurrency() {
		recurrency++;
	}

	public String toString()
	{
		return "Term:"+term+",type:"+type+",category:"+category+",cohesion:"+medic_cohesion+extracted_info.toString();
	}
	public boolean equals(Frame another)
	{
		return another.term.equals(this.term);
	}


}
