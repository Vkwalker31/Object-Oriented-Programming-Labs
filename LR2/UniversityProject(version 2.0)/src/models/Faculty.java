package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Faculty {
    private String name;
    private String code;
    private Teacher dean;
    private List<Department> departments;

    public Faculty(String name, String code) {
        validateName(name);
        validateCode(code);

        this.name = name;
        this.code = code;
        this.departments = new ArrayList<>();
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty name cannot be empty");
        }
    }

    private void validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty code cannot be empty");
        }
    }

    public void addDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
        if (!departments.contains(department)) {
            departments.add(department);
        }
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    public void setDean(Teacher dean) {
        this.dean = dean;
    }

    public int getDepartmentCount() {
        return departments.size();
    }

    public int getTotalTeachers() {
        int total = 0;
        for (Department dept : departments) {
            total += dept.getTeacherCount();
        }
        return total;
    }

    public int getTotalCourses() {
        int total = 0;
        for (Department dept : departments) {
            total += dept.getCourseCount();
        }
        return total;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Teacher getDean() {
        return dean;
    }

    public List<Department> getDepartments() {
        return new ArrayList<>(departments);
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setCode(String code) {
        validateCode(code);
        this.code = code;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", dean=" + (dean != null ? dean.getFullName() : "Not assigned") +
                ", departments=" + departments.size() +
                ", totalTeachers=" + getTotalTeachers() +
                ", totalCourses=" + getTotalCourses() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return code.equals(faculty.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}

