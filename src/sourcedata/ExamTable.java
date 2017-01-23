package sourcedata;
import java.util.ArrayList;

public class ExamTable extends ArrayList<TableDataItem> {
	private String medicalDepartment;
	
	public ExamTable() {
		super();
	}
	
	public void setMedicalDepartment(String medicalDepartment) {
		this.medicalDepartment = medicalDepartment;
	}
}
