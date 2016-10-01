package digitalpoint;

import java.util.UUID;

public class Post {
	
	private UUID id;
	private boolean firstPostFlag;
	private String category;
	private String title;
	private String text;

	public Post(boolean firstPostFlag, String category, String title, String text) {
		
		this.id = UUID.randomUUID();
		this.setFirstPostFlag(firstPostFlag);
		this.setCategory(category);
		this.setTitle(title);
		this.setText(text);
		
	}
	
	public String stringifyCSV() {
		return this.id + "\t" + this.category + "\t" + this.title + "\t" + this.text;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isFirstPostFlag() {
		return firstPostFlag;
	}

	public void setFirstPostFlag(boolean firstPostFlag) {
		this.firstPostFlag = firstPostFlag;
	}

	

}
