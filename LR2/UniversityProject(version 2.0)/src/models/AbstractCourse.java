package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractCourse implements Attendable {
    protected String courseId;
    protected String name;
    protected int credits;
    protected Teacher teacher;
    protected int maxStudents;
    protected List<Enrollment> enrollments;
    protected Schedule schedule;

    public AbstractCourse(String courseId, String name, int credits, int maxStudents) {
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
        if (teacher != null && !teacher.getCourses().contains(this)) {
            teacher.assignCourse(this);
        }
    }

    public void addEnrollment(Enrollment enrollment) {
        if (isFull()) {
            throw new IllegalStateException("Course is full");
        }
        if (!enrollments.contains(enrollment)) {
            enrollments.add(enrollment);
        }
    }

    public boolean removeEnrollment(Enrollment enrollment) {
        return enrollments.remove(enrollment);
    }

    public boolean isFull() {
        return enrollments.size() >= maxStudents;
    }

    @Override
    public int getCurrentOccupancy() {
        return enrollments.size();
    }

    @Override
    public void addOccupant() {
        if (isFull()) {
            throw new IllegalStateException("Cannot add occupant, course is full.");
        }
        System.out.println("DEBUG: Occupant added to " + getName());
    }

    @Override
    public void removeOccupant() {
        if (getCurrentOccupancy() <= 0) {
            throw new IllegalStateException("Cannot remove occupant, course is empty.");
        }
        System.out.println("DEBUG: Occupant removed from " + getName());
    }

    @Override
    public String getId() {
        return courseId;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    @Override
    public int getMaxCapacity() {
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

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setCredits(int credits) {
        validateCredits(credits);
        this.credits = credits;
    }

    public void setMaxStudents(int maxStudents) {
        validateMaxStudents(maxStudents);
        this.maxStudents = maxStudents;
    }

    public abstract String getCourseType();

    @Override
    public String toString() {
        return "AbstractCourse{" +
                "id='" + courseId + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", type=" + getCourseType() +
                ", teacher=" + (teacher != null ? teacher.getFullName() : "Not assigned") +
                ", enrolled=" + getCurrentOccupancy() + "/" + maxStudents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCourse that = (AbstractCourse) o;
        return courseId.equals(that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}