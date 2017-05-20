package actor;

import world.IEntity;
import world.IWorld;

public abstract class Person implements IEntity {

	protected IWorld<Person> world;

	protected int vision;

	public Person(IWorld<Person> world, int vision) {
		this.world = world;
		this.vision = vision;
	}

	public void move() {
		world.move(this, vision);
	}

	public abstract void act();

}
