package models;

import java.time.LocalDate;

public class Enrollment {
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String status;
    private Grade grade;

    public Enrollment(Student student, Course course) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        this.student = student;
        this.course = course;
        this.enrollmentDate = LocalDate.now();
        this.status = "ACTIVE";
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
        if (grade != null) {
            this.status = "COMPLETED";
            student.updateGpa();
        }
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public void drop() {
        this.status = "DROPPED";
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setStatus(String status) {
        if (!status.equals("ACTIVE") && !status.equals("COMPLETED") && !status.equals("DROPPED")) {
            throw new IllegalArgumentException("Invalid status");
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "student=" + student.getFullName() +
                ", course=" + course.getName() +
                ", status='" + status + '\'' +
                ", grade=" + (grade != null ? grade.getLetter() : "Not graded") +
                '}';
    }
}
