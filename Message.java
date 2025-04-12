package application;

public class Message{
	private int toUserId;
	private String text;
	private String fromAuthor;
	
	public Message(int id, String text, String author) {
		this.toUserId = id;
		this.text = text;
		this.fromAuthor = author;
	}
	
	public String getAuthor() {
		return fromAuthor;
	}
	public void setAuthor(String author) {
		this.fromAuthor = author;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		if (text != null && !text.trim().isEmpty() && text.length() <= 200) {
			this.text = text;
		}
	}
	public int getId() {
		return toUserId;
	}
	public void setId(int id) {
		this.toUserId = id;
	}
	
	
}