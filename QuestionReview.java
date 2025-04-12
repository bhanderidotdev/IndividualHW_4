package application;

/**
 * QuestionReview represents a review for a question.
 */
public class QuestionReview {
    private int id;
    private String text;
    private String reviewer;
    private int questionId;
    
    /**
     * Constructs a QuestionReview.
     * @param id the review identifier
     * @param text the review text
     * @param reviewer the reviewer's username
     * @param questionId the associated question ID
     */
    public QuestionReview(int id, String text, String reviewer, int questionId) {
        this.id = id;
        this.text = text;
        this.reviewer = reviewer;
        this.questionId = questionId;
    }
    
    public int getId() {
        return id;
    }
    
    public String getText() {
        return text;
    }
    
    public String getReviewer() {
        return reviewer;
    }
    
    public int getQuestionId() {
        return questionId;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Returns a string representation of the review.
     */
    public String toString() {
        return "ID: " + id + " | " + text + " (" + reviewer + ")";
    }
}
