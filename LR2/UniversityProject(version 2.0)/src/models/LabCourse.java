package models;

public class LabCourse extends AbstractCourse {
    private int numberOfComputers; // Специфично для лабораторной работы
    private boolean requiresSpecialSoftware;

    public LabCourse(String courseId, String name, int credits, int maxStudents,
                     int numberOfComputers, boolean requiresSpecialSoftware) {
        super(courseId, name, credits, maxStudents);
        validateNumberOfComputers(numberOfComputers);
        this.numberOfComputers = numberOfComputers;
        this.requiresSpecialSoftware = requiresSpecialSoftware;
    }

    private void validateNumberOfComputers(int numberOfComputers) {
        if (numberOfComputers <= 0) {
            throw new IllegalArgumentException("Number of computers must be positive for a lab course.");
        }
    }

    @Override
    public String getCourseType() {
        return "Laboratory";
    }

    public int getNumberOfComputers() {
        return numberOfComputers;
    }

    public void setNumberOfComputers(int numberOfComputers) {
        validateNumberOfComputers(numberOfComputers);
        this.numberOfComputers = numberOfComputers;
    }

    public boolean requiresSpecialSoftware() {
        return requiresSpecialSoftware;
    }

    public void setRequiresSpecialSoftware(boolean requiresSpecialSoftware) {
        this.requiresSpecialSoftware = requiresSpecialSoftware;
    }

    @Override
    public String toString() {
        return "LabCourse{" +
                "id='" + courseId + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", teacher=" + (teacher != null ? teacher.getFullName() : "Not assigned") +
                ", enrolled=" + getCurrentOccupancy() + "/" + getMaxCapacity() +
                ", computers=" + numberOfComputers +
                ", specialSoftware=" + requiresSpecialSoftware +
                ", schedule=" + (schedule != null ? schedule.toString() : "Not set") +
                '}';
    }
}
