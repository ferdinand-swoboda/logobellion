package process;

/**
 * This class contains all external parameters that influence the programs
 * execution. They can be specified as parameters on the command console.
 * 
 * @author Ferdinand
 *
 */
public class Parameters {

	/**
	 * the initial cop density in the world
	 */
	public static double INITIAL_COP_DENSITY = 0.04;
	/**
	 * the initial agent density in the world
	 */
	public static double INITIAL_AGENT_DENSITY = 0.7;
	/**
	 * a turtle's vision
	 */
	public static int VISION = 7;
	/**
	 * the initial government legitimacy perceived by every agent
	 */
	public static double GOVERNMENT_LEGITIMACY = 0.82;
	/**
	 * The maximum jail time, a rebelling agent will be sentenced to
	 */
	public static int MAX_JAIL_TERM = 30;
	/**
	 * whether agents can move or not
	 */
	public static boolean MOVEMENT = true;
	/**
	 * EXTENSION whether the perceived government legitimacy is calculated for
	 * each agent individually or not
	 */
	public static boolean INDIVIDUAL_LEGITIMACY = false;
	/**
	 * the scale of the two-dimensional square world
	 */
	public static int SCALE = 50;
	/**
	 * the number of ticks to run this simulation for
	 */
	public static int TICKS = 20;

}
