package entities;

import java.util.Map;

public class QuizQuestion {
    private int id;
    private String question;
    private String description;
    private Map<String, String> answers;
    private Map<String, String> correct_answers;
    private String explanation;
    private String tip;
    private String category;
    private String difficulty;

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }

    public Map<String, String> getCorrect_answers() {
        return correct_answers;
    }

    public void setCorrect_answers(Map<String, String> correct_answers) {
        this.correct_answers = correct_answers;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", description='" + description + '\'' +
                ", answers=" + answers +
                ", correct_answers=" + correct_answers +
                ", explanation='" + explanation + '\'' +
                ", tip='" + tip + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}