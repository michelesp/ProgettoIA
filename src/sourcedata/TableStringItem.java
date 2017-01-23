package sourcedata;

public abstract class TableStringItem extends TableDataItem {
	private String[] data;

	public TableStringItem(String[] data) {
		this.data = data;
	}

	public String[] getData() {
		return data;
	}
}
