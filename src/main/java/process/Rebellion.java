package process;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import actor.Agent;
import actor.Cop;
import actor.ExtendedAgent;
import actor.Turtle;
import world.IWorld;
import world.World;

/**
 * A rebellion controls the dynamics of the social behaviour governed by this
 * simulation.
 * 
 * @author Ferdinand
 *
 */
public class Rebellion {

	/**
	 * constant specified in the NetLogo model
	 */
	public static double threshold = 0.1;
	/**
	 * constant specified in the NetLogo model
	 */
	public static double k = 2.3;

	/**
	 * time series of intermediate per-tick results as a results cache
	 */
	private StringBuilder results;

	/**
	 * the agents in a simulation
	 */
	private LinkedList<Agent> agents;

	/**
	 * the cops in a simulation
	 */
	private LinkedList<Cop> cops;

	/**
	 * the world used for a simulation
	 */
	private IWorld<Turtle> world;

	/**
	 * Creates a new rebellion with a world of the given scale
	 * 
	 * @param worldScale
	 *            the given scale
	 */
	public Rebellion(int worldScale) {
		results = new StringBuilder();
		agents = new LinkedList<Agent>();
		cops = new LinkedList<Cop>();
		world = new World<Turtle>(worldScale);
	}

	/**
	 * Main method and entry point of the logobellion program
	 * 
	 * @param args
	 *            command-line arguments used to specify simulation parameters
	 */
	public static void main(String[] args) {

		readParameters(args);
		Rebellion rebellion = new Rebellion(Parameters.SCALE);
		rebellion.setup();
		rebellion.go(Parameters.TICKS);
		try {
			rebellion.export("results.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Prepares a simulation run by creating cops and agents and adding them to
	 * the world
	 */
	public void setup() {
		agents.clear();
		cops.clear();
		world.clear();

		// prepare results file with simulation parameters
		results = new StringBuilder("TICKS,DIMENSION,VISION,MAX_JAIL_TERM,MOVEMENT,"
				+ "INITIAL_COP_DENSITY,INITIAL_AGENT_DENSITY,"
				+ "GOVERNMENT_LEGITIMACY,INDIVIDUAL_LEGITIMACY\n");
		results.append(Parameters.TICKS + "," + Parameters.SCALE + "," + Parameters.VISION + ","
				+ Parameters.MAX_JAIL_TERM + "," + Parameters.MOVEMENT + ","
				+ Parameters.INITIAL_COP_DENSITY + "," + Parameters.INITIAL_AGENT_DENSITY + ","
				+ Parameters.GOVERNMENT_LEGITIMACY + "," + Parameters.INDIVIDUAL_LEGITIMACY + "\n");

		results.append("tick,#QuietAgents,#JailedAgents,#ActiveAgents\n");

		// create cops
		int numberCops = (int) Math
				.floor(Parameters.INITIAL_COP_DENSITY * world.getScale() * world.getScale());
		for (int i = 0; i < numberCops; i++) {
			Cop cop = new Cop(world, Parameters.VISION, Parameters.MAX_JAIL_TERM);
			cops.add(cop);
		}

		// create agents
		int numberAgents = (int) Math
				.floor(Parameters.INITIAL_AGENT_DENSITY * world.getScale() * world.getScale());
		for (int i = 0; i < numberAgents; i++) {
			// enable/disable extended agent behaviour
			Agent agent = Parameters.INDIVIDUAL_LEGITIMACY
					? new ExtendedAgent(world, Parameters.VISION, Parameters.GOVERNMENT_LEGITIMACY,
							Parameters.MOVEMENT)
					: new Agent(world, Parameters.VISION, Parameters.GOVERNMENT_LEGITIMACY,
							Parameters.MOVEMENT);
			agents.add(agent);
		}

		// add turtles, i.e. cops and agents, to the world
		List<Turtle> turtles = Stream.concat(cops.stream(), agents.stream())
				.collect(Collectors.toList());
		world.enter(turtles);

	}

	/**
	 * Perform a simulation run with the given number of ticks
	 * 
	 * @param ticks
	 *            the given number of ticks
	 */
	public void go(int ticks) {
		// save the initial simulation state
		save(0);
		for (int i = 1; i <= ticks; i++) {
			tick();
			// save the simulation state after each tick
			save(i);
		}
	}

	/**
	 * Store simulation results in a csv file with the given filename
	 * 
	 * @param filename
	 *            the given filename
	 * @throws IOException
	 *             if errors occur loading the file
	 */
	private void export(String filename) throws IOException {
		FileWriter fw = new FileWriter(filename, true);
		fw.write(results.toString());
		fw.close();
	}

	/**
	 * Extracts simulation parameters from the given text. Parameters have to be
	 * declared in the form -<parameter.name>=<parameter.value>.
	 * 
	 * @param args
	 *            the given text
	 */
	private static void readParameters(String[] args) {
		// go through all words
		for (int i = 0; i < args.length; i++) {

			// separate each in parameter name and value
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
				Parameters.SCALE = Integer.parseInt(parts[1]);
			default:
				System.out.println("The parameter " + parameterName + " you entered is invalid");
			}
		}
	}

	/**
	 * Perform a simulation tick
	 */
	private void tick() {

		List<Turtle> turtles = Stream.concat(agents.stream(), cops.stream())
				.collect(Collectors.toList());
		Collections.shuffle(turtles);

		// randomly go through all turtles
		for (Turtle turtle : turtles) {
			// apply movement rule
			turtle.move();

			// apply agent rule or cop rule, respectively
			turtle.act();
		}

		// reduce jail time of (jailed) agents
		agents.stream().forEach(Agent::decreaseJailTerm);

	}

	/**
	 * Save current simulation state in results cache for the given tick
	 * 
	 * @param tick
	 *            the given tick
	 */
	private void save(int tick) {
		int numQuietAgents = 0;
		int numJailedAgents = 0;
		int numRebels = 0;

		// different agent types are disjunct
		// count the types of agents in the simulation state
		for (Agent agent : agents) {
			if (agent.isRebel()) {
				numRebels++;
			} else if (agent.isActive()) {
				numQuietAgents++;
			} else {
				numJailedAgents++;
			}
		}

		// write the values associated with the given tick to the results cache
		results.append(
				tick + "," + numQuietAgents + "," + numJailedAgents + "," + numRebels + "\n");
	}

}
