package models;

public class Classroom {
    private String roomNumber;
    private String building;
    private int capacity;
    private boolean hasProjector;
    private boolean hasComputers;

    public Classroom(String roomNumber, String building, int capacity) {
        validateRoomNumber(roomNumber);
        validateBuilding(building);
        validateCapacity(capacity);

        this.roomNumber = roomNumber;
        this.building = building;
        this.capacity = capacity;
        this.hasProjector = false;
        this.hasComputers = false;
    }

    private void validateRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
    }

    private void validateBuilding(String building) {
        if (building == null || building.trim().isEmpty()) {
            throw new IllegalArgumentException("Building cannot be empty");
        }
    }

    private void validateCapacity(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
    }

    public String getFullName() {
        return building + "-" + roomNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getBuilding() {
        return building;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isHasProjector() {
        return hasProjector;
    }

    public boolean isHasComputers() {
        return hasComputers;
    }

    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public void setHasComputers(boolean hasComputers) {
        this.hasComputers = hasComputers;
    }

    public String getEquipment() {
        StringBuilder sb = new StringBuilder();
        if (hasProjector) sb.append("Projector ");
        if (hasComputers) sb.append("Computers ");
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "name='" + getFullName() + '\'' +
                ", capacity=" + capacity +
                ", equipment=" + (getEquipment().isEmpty() ? "None" : getEquipment()) +
                '}';
    }
}


