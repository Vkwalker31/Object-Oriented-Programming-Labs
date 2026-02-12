package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person {
    private String studentId;
    private int enrollmentYear;
    private int currentYear;
    private double gpa;
    private List<Enrollment> enrollments;

    public Student(String firstName, String lastName, LocalDate dateOfBirth,
                   String email, String phoneNumber, String studentId,
                   int enrollmentYear) {
        super(firstName, lastName, dateOfBirth, email, phoneNumber);
        validateStudentId(studentId);

        this.studentId = studentId;
        this.enrollmentYear = enrollmentYear;
        this.currentYear = 1;
        this.gpa = 0.0;
        this.enrollments = new ArrayList<>();
    }

    private void validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        }
    }

    public void enrollInCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        for (Enrollment e : enrollments) {
            if (e.getCourse().equals(course)) {
                throw new IllegalStateException("Student already enrolled in this course");
            }
        }

        Enrollment enrollment = new Enrollment(this, course);
        enrollments.add(enrollment);
        course.addEnrollment(enrollment);
    }

    public void updateGpa() {
        if (enrollments.isEmpty()) {
            this.gpa = 0.0;
            return;
        }

        double sum = 0.0;
        int count = 0;

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getGrade() != null) {
                sum += enrollment.getGrade().getValue();
                count++;
            }
        }

        this.gpa = count > 0 ? sum / count : 0.0;
    }

    public List<Course> getActiveCourses() {
        List<Course> activeCourses = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if ("ACTIVE".equals(enrollment.getStatus())) {
                activeCourses.add(enrollment.getCourse());
            }
        }
        return activeCourses;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public double getGpa() {
        return gpa;
    }

    public List<Enrollment> getEnrollments() {
        return new ArrayList<>(enrollments); // Защита от изменения
    }

    public void setCurrentYear(int currentYear) {
        if (currentYear < 1 || currentYear > 6) {
            throw new IllegalArgumentException("Current year must be between 1 and 6");
        }
        this.currentYear = currentYear;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + studentId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", year=" + currentYear +
                ", GPA=" + String.format("%.2f", gpa) +
                ", courses=" + enrollments.size() +
                '}';
    }
}