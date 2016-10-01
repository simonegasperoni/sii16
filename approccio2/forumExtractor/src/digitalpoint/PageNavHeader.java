package digitalpoint;

import java.util.StringTokenizer;

public class PageNavHeader {
	
	private String text;

	public PageNavHeader(String text) {
		
		this.text = text;
		
	}
	
	public int getCurrentPage() {		
		StringTokenizer st = new StringTokenizer(this.text);
		st.nextToken();
		return Integer.parseInt(st.nextToken().toString());
	}
	
	public int getTotalPages() {
		StringTokenizer st = new StringTokenizer(this.text);
		st.nextToken();
		st.nextToken();
		st.nextToken();
		return Integer.parseInt(st.nextToken().toString());
	}

}
