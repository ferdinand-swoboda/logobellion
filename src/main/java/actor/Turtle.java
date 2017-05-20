package actor;

import world.IEntity;
import world.IWorld;

public abstract class Turtle implements IEntity {

	protected IWorld<Turtle> world;

	protected int vision;

	public Turtle(IWorld<Turtle> world, int vision) {
		this.world = world;
		this.vision = vision;
	}

	public void move() {
		world.move(this, vision);
	}

	public abstract void act();

}
