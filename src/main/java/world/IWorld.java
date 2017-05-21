package world;

import java.util.List;

/**
 * A world represents a two dimensional square matrix of patches that wraps
 * around. A world can be used to store and move entities within it.
 * 
 * @author Ferdinand
 *
 * @param <T>
 *            the type of entities to be managed by this world
 */
public interface IWorld<T extends IEntity> {

	/**
	 * Assigns the given entities to random locations within the world. The
	 * number of given entities should be less than the number of available
	 * locations in the world.
	 * 
	 * @param entities
	 *            the entities to be added to the world
	 */
	void enter(List<? extends T> entities);

	/**
	 * Resets the world to an empty state
	 */
	void clear();

	/**
	 * Moves the given entity to another random, free location in the world
	 * within the given scope. If no such location is available, the entity
	 * stays in its current location.
	 * 
	 * @param entity
	 *            the given entity
	 * @param scope
	 *            the given scope
	 */
	void move(T entity, int scope);

	/**
	 * Moves the given entity to a target entity's location
	 * 
	 * @param entity
	 *            the given entity to be moved
	 * @param target
	 *            the target entity to whose location to move to
	 */
	void moveTo(T entity, T target);

	/**
	 * Returns the neighbourhood of the given entity. The neighbourhood is
	 * comprised of all entities within the given scope of the given entity.
	 * 
	 * @param entity
	 *            the given entity
	 * @param scope
	 *            the given scope
	 * @return the list of nearby entities
	 */
	List<? extends T> neighbourhoodOf(T entity, int scope);

	/**
	 * Returns the scale of the two-dimensional square world
	 * 
	 * @return the scale
	 */
	int getScale();

}