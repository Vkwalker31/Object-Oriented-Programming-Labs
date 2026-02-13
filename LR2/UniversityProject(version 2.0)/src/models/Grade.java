package models;

import java.time.LocalDate;

public class Grade {
    private double value;
    private String letter;
    private LocalDate dateAwarded;
    private String comments;

    public Grade(double value) {
        validateValue(value);
        this.value = value;
        this.letter = calculateLetterGrade(value);
        this.dateAwarded = LocalDate.now();
        this.comments = "";
    }

    private void validateValue(double value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Grade value must be between 0 and 100");
        }
    }

    private String calculateLetterGrade(double value) {
        if (value >= 90) return "A";
        if (value >= 80) return "B";
        if (value >= 70) return "C";
        if (value >= 60) return "D";
        return "F";
    }

    public boolean isPassing() {
        return value >= 60;
    }

    public double getValue() {
        return value;
    }

    public String getLetter() {
        return letter;
    }

    public LocalDate getDateAwarded() {
        return dateAwarded;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments != null ? comments : "";
    }

    @Override
    public String toString() {
        return "Grade{" +
                "value=" + value +
                ", letter='" + letter + '\'' +
                ", passing=" + isPassing() +
                '}';
    }
}