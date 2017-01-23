package sourcedata;
import java.time.LocalDate;
import java.time.LocalTime;

public class DailyClinicDiary extends Exam {
	private LocalDate localDate;

	public DailyClinicDiary(LocalDate localDate, LocalTime localTime, String[] data) {
		super(localTime, data);
		this.localDate = localDate;
	}
	
	public LocalDate getLocalDate(){
		return localDate;
	}
	
}
