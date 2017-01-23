package sourcedata;
import java.time.LocalTime;

public class Exam extends TableStringItem {
	private LocalTime localTime;
	
	public Exam(LocalTime localTime, String[] data) {
		super(data);
		this.localTime = localTime;
	}
	
	public LocalTime getLocalTime() {
		return localTime;
	}
	
}
