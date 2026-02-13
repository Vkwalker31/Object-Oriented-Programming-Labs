package models;

import models.AbstractCourse;
import models.Grade;
import models.Person;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person {
    private String employeeId;
    private String position;
    private LocalDate hireDate;
    private double salary;
    private List<AbstractCourse> courses; // Теперь список AbstractCourse

    public Teacher(String firstName, String lastName, LocalDate dateOfBirth,
                   String email, String phoneNumber, String employeeId,
                   String position, LocalDate hireDate, double salary) {
        super(firstName, lastName, dateOfBirth, email, phoneNumber);
        validateEmployeeId(employeeId);
        validatePosition(position);
        validateHireDate(hireDate);
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

    private void validateHireDate(LocalDate hireDate) {
        if (hireDate == null) {
            throw new IllegalArgumentException("Hire date cannot be null");
        }
        if (hireDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be in the future");
        }
    }

    private void validateSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }

    public void assignCourse(AbstractCourse course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        if (!courses.contains(course)) {
            courses.add(course);
            if (course.getTeacher() != this) {
                course.assignTeacher(this);
            }
        }
    }

    public void removeCourse(AbstractCourse course) {
        if (courses.remove(course)) {
            // Если курс успешно удален, убедиться, что у курса нет этого преподавателя
            if (course.getTeacher() == this) {
                course.assignTeacher(null); // Убираем преподавателя с курса
            }
        }
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

    public List<AbstractCourse> getCourses() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Teacher teacher = (Teacher) o;
        return employeeId.equals(teacher.employeeId);
    }

    @Override
    public int hashCode() {
        return employeeId.hashCode();
    }
}
