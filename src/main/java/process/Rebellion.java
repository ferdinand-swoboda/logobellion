package process;
import java.util.HashMap;

import actor.Agent;
import actor.Cop;
import actor.Person;
import world.IWorld;
import world.World;

public class Rebellion {

	public static double threshold = 0.1;
	public static double k = 2.3;

	private HashMap<Integer, Cop> cops;

	private HashMap<Integer, Agent> freeAgents;

	private HashMap<Integer, Agent> imprisonedAgents;

	private IWorld<Person> world;

	public Rebellion() {
		world = new World<Person>(50);
	}

	public static void main(String[] args) {
		
		world.initialise(10);
		
		for(int i = 0; i < 3; i++) {
			Cop temp = new Cop();
			cops.add(temp);
			world.enter(temp);
		}
		
		for(int i = 0; i < 50, i++) {
			Agent temp = new Agent();
			freeAgents.add(temp);
			world.enter(temp);
		}
		
		for(int j = 0; j < 10; j++) {
			tick();
		}

	}

	private void tick() {
		
		// movement rule
		if(AGENT_MOVEMENT) {
			for (Agent a : freeAgents) {
				a.move();
			}
		}
		
		for (Cop c : cops) {
			c.move();
		}
		
		// agent rule
		for (Agent a : freeAgents) {
			a.act()
		}
		
		// cop rule
		for (Cop c : cops) {
			c.act();
		}
		
		// check imprisoned agents
		for (Agent a : imprisonedAgents) {
			a.decreaseJailTime();
			if (a.jailTime == 0) {
				imprisonedAgents.remove(a);
				freeAgents.add(a);
				world.move(a);
			}
		}
		
	}

}
