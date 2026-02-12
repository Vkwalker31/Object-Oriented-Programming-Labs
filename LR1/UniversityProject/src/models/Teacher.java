package models;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person {
    private String employeeId;
    private String position;
    private LocalDate hireDate;
    private double salary;
    private List<Course> courses;

    public Teacher(String firstName, String lastName, LocalDate dateOfBirth,
                   String email, String phoneNumber, String employeeId,
                   String position, LocalDate hireDate, double salary) {
        super(firstName, lastName, dateOfBirth, email, phoneNumber);
        validateEmployeeId(employeeId);
        validatePosition(position);
        validateSalary(salary);

        this.employeeId = employeeId;
        this.position = position;
        this.hireDate = hireDate;
        this.salary = salary;
        this.courses = new ArrayList<>();
    }

    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be empty");
        }
    }

    private void validatePosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be empty");
        }
    }

    private void validateSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }

    public void assignCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        if (!courses.contains(course)) {
            courses.add(course);
            course.assignTeacher(this);
        }
    }

    public void removeCourse(Course course) {
        courses.remove(course);
    }

    public int getYearsOfService() {
        if (hireDate == null) {
            return 0;
        }
        return Period.between(hireDate, LocalDate.now()).getYears();
    }

    public int getCourseCount() {
        return courses.size();
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getPosition() {
        return position;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public void setPosition(String position) {
        validatePosition(position);
        this.position = position;
    }

    public void setSalary(double salary) {
        validateSalary(salary);
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + employeeId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", position='" + position + '\'' +
                ", courses=" + courses.size() +
                ", experience=" + getYearsOfService() + " years" +
                '}';
    }
}