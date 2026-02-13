package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Enrollment {
    private Student student;
    private AbstractCourse course; // Теперь AbstractCourse
    private LocalDate enrollmentDate;
    private String status; // ACTIVE, COMPLETED, DROPPED
    private Grade grade;

    public Enrollment(Student student, AbstractCourse course) {
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
        course.removeEnrollment(this);
        student.updateGpa();
    }

    public Student getStudent() {
        return student;
    }

    public AbstractCourse getCourse() {
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
        if (status.equals("COMPLETED") || status.equals("DROPPED")) {
            student.updateGpa();
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return student.equals(that.student) && course.equals(that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, course);
    }
}
