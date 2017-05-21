package actor;

import world.IEntity;
import world.IWorld;

/**
 * A NetLogo turtle that moves around and performs actions.
 * 
 * @author Ferdinand
 *
 */
public abstract class Turtle implements IEntity {

	/**
	 * The world the turtle operates in
	 */
	protected IWorld<Turtle> world;

	/**
	 * Represents the scope of environment the turtle can perceive
	 */
	protected int vision;

	/**
	 * Creates a turtle with the given world and vision
	 * 
	 * @param world
	 *            the world
	 * @param vision
	 *            the vision
	 */
	public Turtle(IWorld<Turtle> world, int vision) {
		this.world = world;
		this.vision = vision;
	}

	/**
	 * Moves the turtle to another random, unoccupied location in the world
	 * within its vision. If no such location is available, the turtle stays in
	 * its current location.
	 */
	public void move() {
		world.move(this, vision);
	}

	/**
	 * Performs an implementation-dependent action
	 */
	public abstract void act();

}
