package process;

import java.util.HashMap;
import java.util.LinkedList;

import actor.Agent;
import actor.Cop;
import actor.Person;
import world.IWorld;
import world.World;

public class Rebellion {

	public static double threshold = 0.1;
	public static double k = 2.3;

	private HashMap<Integer, Integer> numQuietAgents;

	private HashMap<Integer, Integer> numJailedAgents;

	private HashMap<Integer, Integer> numRebels;

	// all agent types are disjunct
	private LinkedList<Agent> quietAgents;

	private LinkedList<Agent> jailedAgents;

	private LinkedList<Agent> rebels;

	private LinkedList<Cop> cops;

	private IWorld<Person> world;

	public Rebellion(int worldDimension) {
		numQuietAgents = new HashMap<Integer, Integer>();
		numJailedAgents = new HashMap<Integer, Integer>();
		numRebels = new HashMap<Integer, Integer>();
		quietAgents = new LinkedList<Agent>();
		jailedAgents = new LinkedList<Agent>();
		rebels = new LinkedList<Agent>();
		world = new World<Person>(worldDimension);
		cops = new LinkedList<Cop>();
	}

	public static void main(String[] args) {

		// TODO make it parameterized
		Rebellion rebellion = new Rebellion(Parameters.DIMENSION);
		rebellion.readParameters(args);
		rebellion.setup();
		rebellion.go(Parameters.TICKS);
		rebellion.saveResults();

	}

	public void setup() {
		int numberCops = (int) Math.floor(Parameters.INITIAL_COP_DENSITY * world.getDimension() * world.getDimension());

		for (int i = 0; i < numberCops; i++) {
			Cop cop = new Cop(world, Parameters.VISION, Parameters.MAX_JAIL_TERM);
			cops.add(cop);
			world.add(cop);
		}

		int numberAgents = (int) Math
				.floor(Parameters.INITIAL_AGENT_DENSITY * world.getDimension() * world.getDimension());

		for (int i = 0; i < numberAgents; i++) {
			Agent agent = new Agent(world, Parameters.VISION, Parameters.INDIVIDUAL_LEGITIMACY,
					Parameters.GOVERNMENT_LEGITIMACY);
			quietAgents.add(agent);
			world.add(agent);
		}

	}

	public void go(int ticks) {
		for (int i = 0; i < ticks; i++) {
			tick();
		}
	}

	public void saveResults() {
		// TODO store results in a NetLogo-compatible csv file
	}

	private void tick() {

		// TODO update lists after each tick
		// movement rule
		if (Parameters.MOVEMENT) {
			for (Agent agent : quietAgents) {
				agent.move();
			}

			for (Agent rebel : rebels) {
				rebel.move();
			}
		}

		for (Cop cop : cops) {
			cop.move();
		}

		// agent rule
		for (Agent agent : quietAgents) {
			agent.act();
		}

		for (Agent rebel : rebels) {
			rebel.act();
		}

		// cop rule
		for (Cop c : cops) {
			c.act();
		}

		// reduce jail time
		for (Agent agent : jailedAgents) {
			agent.decreaseJailTerm();
		}

	}

	// parameters have to be declared in the form
	// -<parameter.name>=<parameter.value> ...
	private void readParameters(String[] args) {
		for (int i = 0; i < args.length; i++) {

			String[] parts = args[i].split("=");
			String parameterName = parts[0].substring(1);

			switch (parameterName) {
			case "initial_cop_density":
				Parameters.INITIAL_COP_DENSITY = Double.parseDouble(parts[1]);
				break;
			case "initial_agent_density":
				Parameters.INITIAL_AGENT_DENSITY = Double.parseDouble(parts[1]);
				break;
			case "vision":
				Parameters.VISION = Integer.parseInt(parts[1]);
				break;
			case "government_legitimacy":
				Parameters.GOVERNMENT_LEGITIMACY = Double.parseDouble(parts[1]);
				break;
			case "max_jail_term":
				Parameters.MAX_JAIL_TERM = Integer.parseInt(parts[1]);
				break;
			case "movement":
				Parameters.MOVEMENT = Boolean.parseBoolean(parts[1]);
				break;
			case "individual_legitimacy":
				Parameters.INDIVIDUAL_LEGITIMACY = Boolean.parseBoolean(parts[1]);
				break;
			case "ticks":
				Parameters.TICKS = Integer.parseInt(parts[1]);
				break;
			case "dimension":
				Parameters.DIMENSION = Integer.parseInt(parts[1]);
			default:
				System.out.println("The parameter " + parameterName + " you entered is invalid");
			}
		}
	}

}
