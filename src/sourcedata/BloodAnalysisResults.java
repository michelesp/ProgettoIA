package sourcedata;

public class BloodAnalysisResults extends TableDataItem {
	private String exam;
	private String result;
	private String unitsOfMisure;
	private String referenceRange;
	
	public BloodAnalysisResults(String exam, String result, String unitsOfMisure, String referenceRange) {
		super();
		this.exam = exam;
		this.result = result;
		this.unitsOfMisure = unitsOfMisure;
		this.referenceRange = referenceRange;
	}

	public String getExam() {
		return exam;
	}

	public String getResult() {
		return result;
	}

	public String getUnitsOfMisure() {
		return unitsOfMisure;
	}

	public String getReferenceRange() {
		return referenceRange;
	}
	
}
