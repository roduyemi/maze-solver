package maze;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Maze {

	public Maze() {
		
	}
	
	private static int x, y, startX, startY, endX, endY;
	private static String [][] map = null;
	private static final String WALL = "1", SPACE = "0", NOT_VISITED = "nv", VISITING = "vg", VISITED = "vd";
	
	/**
	 * Reads map from external input file and maps to multi-dimensional array
	 * @return map
	 * @throws IOException
	 */
	private static boolean loadMap() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("medium_input.txt"));
		
		boolean validMap = true;
		
		String XY = br.readLine();
		String startXY = br.readLine();
		String endXY = br.readLine();
		
		String [] XYLine = XY.split(" ");
		String [] startXYLine = startXY.split(" ");
		String [] endXYLine = endXY.split(" ");
		
		x = Integer.parseInt(XYLine[0]);					// width of map
		y  = Integer.parseInt(XYLine[1]);					// height of map
		
		startX  = Integer.parseInt(startXYLine[0]);			// source x co-ordinate
		startY  = Integer.parseInt(startXYLine[1]);			// source y co-ordinate
		
		endX  = Integer.parseInt(endXYLine[0]);				// destination x co-ordinate
		endY  = Integer.parseInt(endXYLine[1]);				// destination y co-ordinate	
		
		// Create multi-dimensional array with dimensions width x height
		map = new String [x][y];
		
		// map line number count
		int count = 0;
		
		// Populate map with data from input file
		while (true) {

			String line = br.readLine();
			
			// no more lines to read
			if (line == null) {
				br.close();
				break;
			}
			String [] s = line.split(" ");
			for(int i=0; i < s.length; i++) {
				map[i][count] = s[i];
			}
			
			count++;
		}
		
		// Set source and destination if not a wall
		if(!map[startX][startY].equals(WALL)) {
			map[startX][startY] = "S";
		}
		else {
			System.out.println("Invalid source co-ordinates");
			validMap = false;
		}
		if(!map[endX][endY].equals(WALL)) {
			map[endX][endY] = "E";
		}
		else {
			System.out.println("Invalid destination co-ordinates");
			validMap = false;
		}
		     
	    return validMap;
	}
	/**
	 * Loads map via local function loadMap
	 * Finds shortest path to solution via local function BFS
	 * If valid solution exists, prints maze solution to external file
	 * @throws IOException
	 */
	public static void solveMaze() throws IOException {
		
		boolean printMaze = false;
		boolean validMap = false;
		
		try {
			validMap = loadMap();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(validMap) {
			printMaze = BFS();
		}
		
		if(printMaze) {

		    PrintWriter pw = new PrintWriter(new FileWriter("maze_solution.txt"));
		     
		    for (int i=0; i < x; i++) {
		    	
		    	for (int j=0; j < y; j++) {
		    		
		    		if(map[i][j].equals(WALL)) {
		    			pw.write("#");
		    		}
		    		else if(map[i][j].equals(SPACE)) {
		    			pw.write(" ");
		    		}
		    		else {
		    			pw.write(map[i][j]);
		    		}
		    		
		    	}
		    	pw.write(System.getProperty("line.separator"));
		    }
		    pw.close();
		}
		else {
			System.err.println("No solution for maze exists");
		}
	}
	/**
	 * Performs breadth first search to find path leading from source to destination
	 * Finds shortest path to destination via local function findPath
	 * @return true if solution exists, false otherwise
	 */
	public static boolean BFS() {
		
		Queue<int []> q = new LinkedList<int []>();
		HashMap<int [], int[]> parent =  new HashMap<int[], int[]>();
		String state [][] = new String [x][y];
		int [] start = {startX, startY};
		int i, j;
		int [] u;
		
		boolean found = false;						// set to true if path to destination found
		
		for (i = 0; i < x; i++) {
			for(j = 0; j < y; j++) {
				state[i][j] = NOT_VISITED;
			}
		}
		state[startX][startY] = VISITING;
		
		q.add(start);
		
		while (!q.isEmpty()) {
			u = q.peek();
			
			int nodeX = u[0];
			int nodeY = u[1];
			
			// Check NORTH node
			if(nodeX > 0 &&
			   state[nodeX-1][nodeY].equals(NOT_VISITED) &&
			   !map[nodeX-1][nodeY].equals(WALL)) {
			   state[nodeX-1][nodeY] = VISITING;
			   int [] currNode = {nodeX-1, nodeY};
			   q.add(currNode);
			   parent.put(currNode, u);
			}
			//Check WEST node
			if(nodeY > 0 &&
			   state[nodeX][nodeY-1].equals(NOT_VISITED) &&
			   !map[nodeX][nodeY-1].equals(WALL)) {
			   state[nodeX][nodeY-1] = VISITING;
			   int [] currNode = {nodeX, nodeY-1};
			   q.add(currNode);
			   parent.put(currNode, u);
			}
			//Check EAST node
			if(y-nodeY > 1 &&
			   state[nodeX][nodeY+1].equals(NOT_VISITED) &&
			   !map[nodeX][nodeY+1].equals(WALL)) {
			   state[nodeX][nodeY+1] = VISITING;
			   int [] currNode = {nodeX, nodeY+1};
			   q.add(currNode);
			   parent.put(currNode, u);
			}
			//Check SOUTH node
			if(x-nodeX > 1 &&
			   state[nodeX+1][nodeY].equals(NOT_VISITED) &&
			   !map[nodeX+1][nodeY].equals(WALL)) {
			   state[nodeX+1][nodeY] = VISITING;
			   int [] currNode = {nodeX+1, nodeY};
			   q.add(currNode);
			   parent.put(currNode, u);
			}		
			
			state[nodeX][nodeY] = VISITED;

			q.poll();
			
			// found source, set found to true
			if(map[nodeX][nodeY].equals("E")) {
				found = true;
				break;
			}
		}
		// Call findPath to map out shortest path on maze
		if(found) {
			findPath(parent);
		}
		return found;
		
	}
	/**
	 * Starting from destination, trace back through parent nodes until source
	 * is reached
	 * @param parent - key: node, value: parent node
	 */
	public static void findPath (HashMap<int [], int[]> parent) {
		
		Iterator<Map.Entry<int[], int[]>> it = parent.entrySet().iterator();
		int [] currNode = {endX, endY};
		
		while(it.hasNext()) {
			
			Map.Entry<int[], int[]> me = (Map.Entry<int[], int[]>)it.next();
			if(Arrays.equals(currNode, (int[]) me.getKey())) {
				currNode = (int[]) me.getValue();
				it = parent.entrySet().iterator();
				if(!map[currNode[0]][currNode[1]].equals("S")) {
					map[currNode[0]][currNode[1]] = "X";
				}
				
				if(currNode[0] == startX && currNode[1] == startY) {
					break;
				}
			}
		}		
		
	}
	public static void main (String [] args) {
		
		try {
			solveMaze();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	
		
}
