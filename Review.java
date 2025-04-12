package application;

public class Review {
	private int id;
	private String text;
	private String author;
	private int answerId;
	
	public Review(int id, String text, String author, int answerId) {
		this.id = id;
		this.text = text;
		this.author = author;
		this.answerId = answerId;
	}
	
	public void setText(String text) {
        if (text != null && !text.trim().isEmpty() && text.length() <= 500) {
            this.text = text;
        }
    }
	
	public String toString() {
        return "ID: " + id + " | " + text + " (" + author + ")";
    }
	
	public int getId() { return id; }
    public String getText() { return text; }
    public String getAuthor() { return author; }
    public int getQuestionId() { return answerId; }
    
    public void setId(int id) { this.id = id; }
}