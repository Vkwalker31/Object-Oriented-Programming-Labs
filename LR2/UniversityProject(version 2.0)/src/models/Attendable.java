package models;

public interface Attendable {
    String getId();
    String getName();
    int getMaxCapacity();
    int getCurrentOccupancy();

    default boolean hasAvailableSpots() {
        return getCurrentOccupancy() < getMaxCapacity();
    }
    
    void addOccupant();

    void removeOccupant();
}