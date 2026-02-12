package models;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseId;
    private String name;
    private int credits;
    private Teacher teacher;
    private int maxStudents;
    private List<Enrollment> enrollments;
    private Schedule schedule;

    public Course(String courseId, String name, int credits, int maxStudents) {
        validateCourseId(courseId);
        validateName(name);
        validateCredits(credits);
        validateMaxStudents(maxStudents);

        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.maxStudents = maxStudents;
        this.enrollments = new ArrayList<>();
    }

    private void validateCourseId(String courseId) {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Course ID cannot be empty");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
    }

    private void validateCredits(int credits) {
        if (credits < 1 || credits > 10) {
            throw new IllegalArgumentException("Credits must be between 1 and 10");
        }
    }

    private void validateMaxStudents(int maxStudents) {
        if (maxStudents < 1) {
            throw new IllegalArgumentException("Max students must be positive");
        }
    }

    public void assignTeacher(Teacher teacher) {
        if (this.teacher != null && !this.teacher.equals(teacher)) {
            this.teacher.removeCourse(this);
        }
        this.teacher = teacher;
    }

    public void addEnrollment(Enrollment enrollment) {
        if (isFull()) {
            throw new IllegalStateException("Course is full");
        }

        if (!enrollments.contains(enrollment)) {
            enrollments.add(enrollment);
        }
    }

    public boolean isFull() {
        return enrollments.size() >= maxStudents;
    }

    public int getEnrolledCount() {
        return enrollments.size();
    }

    public int getAvailableSeats() {
        return maxStudents - enrollments.size();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public List<Enrollment> getEnrollments() {
        return new ArrayList<>(enrollments);
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + courseId + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", teacher=" + (teacher != null ? teacher.getFullName() : "Not assigned") +
                ", enrolled=" + getEnrolledCount() + "/" + maxStudents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseId.equals(course.courseId);
    }

    @Override
    public int hashCode() {
        return courseId.hashCode();
    }
}