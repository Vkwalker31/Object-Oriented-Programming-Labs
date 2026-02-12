package models;

import java.util.ArrayList;
import java.util.List;

public class University {
    private String name;
    private String address;
    private int foundedYear;
    private List<Faculty> faculties;

    public University(String name, String address, int foundedYear) {
        validateName(name);
        validateAddress(address);
        validateYear(foundedYear);

        this.name = name;
        this.address = address;
        this.foundedYear = foundedYear;
        this.faculties = new ArrayList<>();
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("University name cannot be empty");
        }
    }

    private void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
    }

    private void validateYear(int year) {
        if (year < 1500 || year > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Invalid founding year");
        }
    }

    public void addFaculty(Faculty faculty) {
        if (faculty == null) {
            throw new IllegalArgumentException("Faculty cannot be null");
        }
        if (!faculties.contains(faculty)) {
            faculties.add(faculty);
        }
    }

    public int getYearsOfOperation() {
        return java.time.Year.now().getValue() - foundedYear;
    }

    public int getFacultyCount() {
        return faculties.size();
    }

    public int getTotalDepartments() {
        int total = 0;
        for (Faculty faculty : faculties) {
            total += faculty.getDepartmentCount();
        }
        return total;
    }

    public int getTotalTeachers() {
        int total = 0;
        for (Faculty faculty : faculties) {
            total += faculty.getTotalTeachers();
        }
        return total;
    }

    public int getTotalCourses() {
        int total = 0;
        for (Faculty faculty : faculties) {
            total += faculty.getTotalCourses();
        }
        return total;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getFoundedYear() {
        return foundedYear;
    }

    public List<Faculty> getFaculties() {
        return new ArrayList<>(faculties);
    }

    @Override
    public String toString() {
        return "University{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", founded=" + foundedYear +
                ", years=" + getYearsOfOperation() +
                ", faculties=" + faculties.size() +
                ", departments=" + getTotalDepartments() +
                ", teachers=" + getTotalTeachers() +
                ", courses=" + getTotalCourses() +
                '}';
    }
}
