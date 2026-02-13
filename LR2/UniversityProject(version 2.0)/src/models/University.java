package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class University {
    private String name;
    private String address;
    private int foundedYear;
    private List<Faculty> faculties;

    public University(String name, String address, int foundedYear) {
        validateName(name);
        validateAddress(address);
        validateFoundedYear(foundedYear);

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

    private void validateFoundedYear(int year) {
        if (year < 1000 || year > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Invalid founded year: " + year);
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

    public void removeFaculty(Faculty faculty) {
        faculties.remove(faculty);
    }

    public int getYearsOfOperation() {
        return LocalDate.now().getYear() - foundedYear;
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

    // ===== Геттеры =====
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

    // ===== Сеттеры =====
    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setAddress(String address) {
        validateAddress(address);
        this.address = address;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        University that = (University) o;
        return name.equals(that.name) && address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }
}
