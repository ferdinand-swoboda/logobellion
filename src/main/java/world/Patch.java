package world;

import java.util.LinkedList;

/**
 * A patch contains a list of occupying entities and is assigned spatial
 * information, i.e. two-dimensional coordinates.
 * 
 * @author Ferdinand
 *
 * @param <T>
 *            the type of occupying entities
 */
public class Patch<T extends IEntity> {

	/**
	 * the patch's x coordinate
	 */
	private int xCoordinate;

	/**
	 * the patch's y coordinate
	 */
	private int yCoordinate;

	/**
	 * the patch's occupying entities
	 */
	private LinkedList<T> occupants;

	/**
	 * Constructs a patch with no occupants from the given coordinates
	 * 
	 * @param xCoordinate
	 * @param yCoordinate
	 */
	public Patch(int xCoordinate, int yCoordinate) {
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.occupants = new LinkedList<T>();
	}

	/**
	 * Constructs a patch with the given coordinates and the given occupants
	 * 
	 * @param xCoordinate
	 * @param yCoordinate
	 * @param occupants
	 */
	public Patch(int xCoordinate, int yCoordinate, LinkedList<T> occupants) {
		this(xCoordinate, yCoordinate);
		this.occupants = occupants;
	}

	/**
	 * Returns the occuyping entities
	 * 
	 * @return the occupants
	 */
	public LinkedList<? extends T> getOccupants() {
		return occupants;
	}

	/**
	 * Adds the given entity to the occupants
	 * 
	 * @param occupant
	 *            the given entity
	 */
	public void addOccupant(T occupant) {
		this.occupants.add(occupant);
	}

	/**
	 * Removes the given entity from the occupants
	 * 
	 * @param occupant
	 *            the given entity
	 * @return the entity removed
	 */
	public T removeOccupant(T occupant) {
		int index = this.occupants.indexOf(occupant);
		return (index < 0) ? null : this.occupants.remove(index);
	}

	/**
	 * Returns whether any of the occupying entities is active
	 * 
	 * @return whether any of the occupying entities is active
	 */
	public boolean containsActive() {
		return !this.occupants.stream().anyMatch((T entity) -> entity.isActive());
	}

	/**
	 * Returns the y coordinate
	 * 
	 * @return the y coordinate
	 */
	public int getyCoordinate() {
		return yCoordinate;
	}

	/**
	 * Returns the x coordinate
	 * 
	 * @return the x coordinate
	 */
	public int getxCoordinate() {
		return xCoordinate;
	}

	/**
	 * Removes all occupying entities
	 */
	public void clearOccupants() {
		occupants.clear();
	}

	/**
	 * Returns whether the patch is occupied or not
	 * 
	 * @return true if and only if the list of occupying entities is non-empty,
	 *         false otherwise
	 */
	public boolean isOccupied() {
		return !this.occupants.isEmpty();
	}

}
