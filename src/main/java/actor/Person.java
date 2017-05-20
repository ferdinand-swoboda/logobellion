package actor;

import java.util.Random;

import world.IEntity;
import world.IWorld;

public abstract class Person implements IEntity {

	protected IWorld<Person> world;

	protected int vision;

	protected Random random;

	public Person(IWorld<Person> world, int vision) {
		this.world = world;
		this.vision = vision;
		this.random = new Random();
	}

	public void move() {
		world.move(this, vision);
	}

	public abstract void act();

}
