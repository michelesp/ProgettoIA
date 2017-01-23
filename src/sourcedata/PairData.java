package sourcedata;

public class PairData extends StructuredData {
	private String name;
	private String value;
	
	public PairData(String name, String value) {
		this.name = name;
		this.value = value;
	}

	
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	public String toString() {
		return "("+name+","+value+")";
	}
	
}
