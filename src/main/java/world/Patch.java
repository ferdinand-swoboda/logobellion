package world;

import java.util.LinkedList;

public class Patch<T extends IEntity> {

	private int xCoordinate;

	private int yCoordinate;

	private LinkedList<T> occupants;

	public Patch(int xCoordinate, int yCoordinate) {
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.occupants = new LinkedList<T>();
	}

	public Patch(int xCoordinate, int yCoordinate, LinkedList<T> occupants) {
		this(xCoordinate, yCoordinate);
		this.occupants = occupants;
	}

	public LinkedList<? extends T> getOccupants() {
		return occupants;
	}

	public void addOccupant(T occupant) {
		this.occupants.add(occupant);
	}

	public T removeOccupant(T occupant) {
		int index = this.occupants.indexOf(occupant);
		return (index < 0) ? null : this.occupants.remove(index);
	}

	public boolean containsActive() {
		return !this.occupants.stream().anyMatch((T entity) -> entity.isActive());
	}

	public int getyCoordinate() {
		return yCoordinate;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public void clearOccupants() {
		occupants.clear();
	}

	public boolean isOccupied() {
		return !this.occupants.isEmpty();
	}

}
