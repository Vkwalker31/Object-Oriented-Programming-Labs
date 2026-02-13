package models;

public class LectureCourse extends AbstractCourse {
    private String lectureHall;

    public LectureCourse(String courseId, String name, int credits, int maxStudents, String lectureHall) {
        super(courseId, name, credits, maxStudents);
        validateLectureHall(lectureHall);
        this.lectureHall = lectureHall;
    }

    private void validateLectureHall(String lectureHall) {
        if (lectureHall == null || lectureHall.trim().isEmpty()) {
            throw new IllegalArgumentException("Lecture hall cannot be empty for a lecture course.");
        }
    }

    @Override
    public String getCourseType() {
        return "Lecture";
    }

    public String getLectureHall() {
        return lectureHall;
    }

    public void setLectureHall(String lectureHall) {
        validateLectureHall(lectureHall);
        this.lectureHall = lectureHall;
    }

    @Override
    public String toString() {
        return "LectureCourse{" +
                "id='" + courseId + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", teacher=" + (teacher != null ? teacher.getFullName() : "Not assigned") +
                ", enrolled=" + getCurrentOccupancy() + "/" + getMaxCapacity() +
                ", lectureHall='" + lectureHall + '\'' +
                ", schedule=" + (schedule != null ? schedule.toString() : "Not set") +
                '}';
    }
}
