package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Department {
    private String name;
    private String code;
    private Teacher head;
    private List<Teacher> teachers;
    private List<AbstractCourse> courses;

    public Department(String name, String code) {
        validateName(name);
        validateCode(code);

        this.name = name;
        this.code = code;
        this.teachers = new ArrayList<>();
        this.courses = new ArrayList<>();
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty");
        }
    }

    private void validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Department code cannot be empty");
        }
    }

    public void addTeacher(Teacher teacher) {
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher cannot be null");
        }
        if (!teachers.contains(teacher)) {
            teachers.add(teacher);
        }
    }

    public void removeTeacher(Teacher teacher) {
        teachers.remove(teacher);
        if (head != null && head.equals(teacher)) {
            head = null;
        }
    }

    public void addCourse(AbstractCourse course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    public void removeCourse(AbstractCourse course) {
        courses.remove(course);
    }

    public void setHead(Teacher head) {
        if (head != null && !teachers.contains(head)) {
            addTeacher(head);
        }
        this.head = head;
    }

    public int getTeacherCount() {
        return teachers.size();
    }

    public int getCourseCount() {
        return courses.size();
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Teacher getHead() {
        return head;
    }

    public List<Teacher> getTeachers() {
        return new ArrayList<>(teachers);
    }

    public List<AbstractCourse> getCourses() {
        return new ArrayList<>(courses);
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
        return "Department{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", head=" + (head != null ? head.getFullName() : "Not assigned") +
                ", teachers=" + teachers.size() +
                ", courses=" + courses.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
