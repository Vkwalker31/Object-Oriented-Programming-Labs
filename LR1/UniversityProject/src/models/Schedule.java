package models;

import java.time.Duration;
import java.time.LocalTime;

public class Schedule {
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Classroom classroom;

    public Schedule(String dayOfWeek, LocalTime startTime, LocalTime endTime, Classroom classroom) {
        validateDayOfWeek(dayOfWeek);
        validateTimes(startTime, endTime);

        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroom = classroom;
    }

    private void validateDayOfWeek(String day) {
        String[] validDays = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        for (String validDay : validDays) {
            if (validDay.equals(day)) {
                return;
            }
        }
        throw new IllegalArgumentException("Invalid day of week");
    }

    private void validateTimes(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Times cannot be null");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    public long getDuration() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime + "-" + endTime +
                " (" + getDuration() + " min) в " +
                (classroom != null ? classroom.getFullName() : "No room");
    }
}