package sourcedata;
import java.time.LocalDate;
import java.time.LocalTime;

public class DailyClinicDiaryItem extends Exam {
	private LocalDate localDate;

	public DailyClinicDiaryItem(LocalDate localDate, LocalTime localTime, String[] data) {
		super(localTime, data);
		this.localDate = localDate;
	}
	
	public LocalDate getLocalDate(){
		return localDate;
	}
	
}
