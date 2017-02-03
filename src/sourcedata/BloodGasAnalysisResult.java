package sourcedata;

public class BloodGasAnalysisResult extends TableDataItem {
	private String exam;
	private String parameter;
	private String unitsOfMisure;
	private boolean derived;
	
	public BloodGasAnalysisResult(String exam, String parameter, String unitsOfMisure, boolean derived) {
		super();
		this.exam = exam;
		this.parameter = parameter;
		this.unitsOfMisure = unitsOfMisure;
		this.derived = derived;
	}

	public String getExam() {
		return exam;
	}

	public String getParameter() {
		return parameter;
	}

	public String getUnitsOfMisure() {
		return unitsOfMisure;
	}
	
	public boolean isDerived() {
		return derived;
	}
}
