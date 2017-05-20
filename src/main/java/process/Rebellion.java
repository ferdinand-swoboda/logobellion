package process;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import actor.Agent;
import actor.Cop;
import actor.Turtle;
import world.IWorld;
import world.World;

public class Rebellion {

	public static double threshold = 0.1;
	public static double k = 2.3;

	private HashMap<Integer, Integer> numQuietAgents;

	private HashMap<Integer, Integer> numJailedAgents;

	private HashMap<Integer, Integer> numRebels;

	private LinkedList<Agent> agents;

	private LinkedList<Cop> cops;

	private IWorld<Turtle> world;

	public Rebellion(int worldDimension) {
		numQuietAgents = new HashMap<Integer, Integer>();
		numJailedAgents = new HashMap<Integer, Integer>();
		numRebels = new HashMap<Integer, Integer>();
		agents = new LinkedList<Agent>();
		world = new World<Turtle>(worldDimension);
		cops = new LinkedList<Cop>();
	}

	public static void main(String[] args) {

		readParameters(args);
		Rebellion rebellion = new Rebellion(Parameters.DIMENSION);
		rebellion.setup();
		rebellion.go(Parameters.TICKS);
		rebellion.storeTimeSeries();

	}

	public void setup() {
		int numberCops = (int) Math.floor(Parameters.INITIAL_COP_DENSITY * world.getDimension() * world.getDimension());

		for (int i = 0; i < numberCops; i++) {
			Cop cop = new Cop(world, Parameters.VISION, Parameters.MAX_JAIL_TERM);
			cops.add(cop);
		}

		int numberAgents = (int) Math
				.floor(Parameters.INITIAL_AGENT_DENSITY * world.getDimension() * world.getDimension());

		for (int i = 0; i < numberAgents; i++) {
			Agent agent = new Agent(world, Parameters.VISION, Parameters.INDIVIDUAL_LEGITIMACY,
					Parameters.GOVERNMENT_LEGITIMACY);
			agents.add(agent);
		}

		List<Turtle> turtles = Stream.concat(cops.stream(), agents.stream()).collect(Collectors.toList());
		world.enter(turtles);

	}

	public void go(int ticks) {
		save(0);
		for (int i = 1; i <= ticks; i++) {
			tick();
			save(i);
		}
	}

	public void storeTimeSeries() {
		// TODO store results in a NetLogo-compatible csv file
	}

	// parameters have to be declared in the form
	// -<parameter.name>=<parameter.value> ...
	private static void readParameters(String[] args) {
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

	private void tick() {

		List<Turtle> turtles = Stream.concat(agents.stream(), cops.stream()).collect(Collectors.toList());
		Collections.shuffle(turtles);

		// randomly go through all persons
		for (Turtle turtle : turtles) {
			// movement rule
			turtle.move();

			// agent rule and cop rule
			turtle.act();
		}

		// reduce jail time of (jailed) agents
		agents.stream().forEach(Agent::decreaseJailTerm);

	}

	// save current state in time series
	private void save(int tick) {
		int numQuietAgents = 0;
		int numJailedAgents = 0;
		int numRebels = 0;

		// different agent types are disjunct
		for (Agent agent : agents) {
			if (agent.isRebel()) {
				numRebels++;
			} else if (agent.isActive()) {
				numQuietAgents++;
			} else {
				numJailedAgents++;
			}
		}

		this.numQuietAgents.put(tick, numQuietAgents);
		this.numJailedAgents.put(tick, numJailedAgents);
		this.numRebels.put(tick, numRebels);
	}

}
