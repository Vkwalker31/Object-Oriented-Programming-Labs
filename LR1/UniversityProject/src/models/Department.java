package models;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private String name;
    private String code;
    private Teacher head;
    private List<Teacher> teachers;
    private List<Course> courses;

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

    public void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (!courses.contains(course)) {
            courses.add(course);
        }
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

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
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
}