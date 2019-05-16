import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * Assignment 3 for CS 245. Adapts BFS for finding the shortest path between actors. Six Degrees of Kevin Bacon.
 *
 */
public class A3 {
	private Graph graph;

	/**
	 * Initialize and create the graph, then find the shortest path between two actors that the user inputs.
	 * @param file the csv file of actors
	 */
	public A3(String file) {
		graph = new Graph();
		createGraph(file);
		shortestPath();
	}

	/**
	 * Creates a graph by isolating the cast list using regex and then uses JSONParser from JSONSimple to parse the JSON into cast member objects.
	 * @param file the csv file of actors
	 */
	private void createGraph(String file) {
		JSONParser parser = new JSONParser();

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file))) {
			// ignore first line since it is just headings
			reader.readLine();
			String line = reader.readLine();

			// regex for the json lists
			Pattern p = Pattern.compile("\\[\\{.+?\\}\\]");

			while (line != null) {
				Matcher m = p.matcher(line);

				if (m.find()) {
					String cast = m.group();
					// replace the consecutive quotation marks with singular double quotes
					cast = cast.replace("\"\"", "\"");

					JSONArray o = (JSONArray) parser.parse(cast);

					// Add all actors in lowercase to the graph for case-insensitive
					for (Object obj1 : o) {
						JSONObject actor = (JSONObject) obj1;
						for (Object obj2 : o) {
							JSONObject otherActor = (JSONObject) obj2;
							graph.addConnection(actor.get("name").toString().toLowerCase(), otherActor.get("name").toString().toLowerCase());
						}
					}

				}

				line = reader.readLine();
			}

		} catch (IOException e) {
			System.err.println("An error occured opening the file to read.");
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Finds the shortest path between the actors given by user input.
	 */
	private void shortestPath() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Actor 1 name: ");
		String actor1 = scanner.nextLine().toLowerCase();
		System.out.print("Actor 2 name: ");
		String actor2 = scanner.nextLine().toLowerCase();

		if (actor1.equals(actor2)) {
			System.out.println("This is the same actor.");
		} else if (!graph.adjList.containsKey(actor1) || !graph.adjList.containsKey(actor2)) {
			System.out.println("One or both actor names are invalid.");
		}
		else {
			shortestPath(actor1, actor2);
		}

		scanner.close();
	}

	/**
	 * Finds the shortest path between the actors given by user input.
	 * @param actor1 the first actor
	 * @param actor2 the second actor
	 */
	private void shortestPath(String actor1, String actor2) {
		HashSet<String> visited = new HashSet<>();

		// v2, v1 that leads to v2
		HashMap<String, String> before = new HashMap<>();

		// actor 1 is the start of the path and the first visited, so there is nothing before
		visited.add(actor1);
		before.put(actor1, null);

		// list of actors who we still need to check their neighbors
		LinkedList<String> finishVisit = new LinkedList<>();
		finishVisit.add(actor1);

		while (!finishVisit.isEmpty()) {
			String currentActor = finishVisit.poll();

			// If the current actor is the last actor, we know we've found the shortest path. Print it and break.
			if (currentActor.equals(actor2)) {
				System.out.println(finishedPath(before, currentActor));
				break;
			}

			Iterator<String> adjActors = graph.getNeighbor(currentActor).iterator();
			while (adjActors.hasNext()) {
				String adjActor = adjActors.next();

				if (!visited.contains(adjActor)) {
					visited.add(adjActor);
					finishVisit.add(adjActor);
					before.put(adjActor, currentActor);
				}
			}
		}


	}

	/**
	 * Returns the shortest path of actors between the first and second actor.
	 * @param before the map of actors to the actors that led to them
	 * @param lastActor the last actor
	 * @return the string of shortest path
	 */
	private String finishedPath(HashMap<String, String> before, String lastActor) {
		String prev = before.get(lastActor);
		if (prev == null) {
			return lastActor;
		}
		return finishedPath(before, prev) + " --> " +  lastActor;
	}


	public static void main(String args[]) {
		new A3(args[0]);
	}
}
