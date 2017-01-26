package protege;


public class Info {

	private String info, date;
	
	public Info(String i,String d){
		info=i;
		date=d;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	public String toString()
	{
		return "Info:"+info+",date:"+date;
	}
	
}
