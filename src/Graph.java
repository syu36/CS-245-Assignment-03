import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Graph implementation specifically for Assignment 3, 6 degrees of Kevin Bacon
 *
 */
public class Graph {
	public HashMap<String, HashSet<String>> adjList;

	/**
	 * Initializes adjacency list.
	 */
	public Graph() {
		adjList = new HashMap<>();
	}

	/**
	 * Adds a connection from the first actor to the second actor, then from the second to the first.
	 * @param actor the first actor
	 * @param otherActor the second actor
	 */
	public void addConnection(String actor, String otherActor) {
		adjList.putIfAbsent(actor, new HashSet<>());
		adjList.get(actor).add(otherActor);
		adjList.putIfAbsent(otherActor, new HashSet<>());
		adjList.get(otherActor).add(actor);
	}

	/**
	 * Returns the list of neighbors
	 * @param name the actor
	 * @return the list of neighbors of the actors
	 */
	public Set<String> getNeighbor(String name) {
		return adjList.get(name);
	}
}
