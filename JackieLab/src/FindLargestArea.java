import java.util.HashSet;
import java.util.Vector;

/**
 * @author Thachapong
 *
 */
public class FindLargestArea {
	
//	static int [][] matrix = {
//					{0, 0, 0, 2, 2},
//					{1, 1, 7, 2, 2},
//					{2, 2, 7, 2, 1},
//					{2, 1, 7, 4, 4},
//					{2, 7, 7, 4, 4},
//					{4, 6, 6, 0, 4},
//					{4, 4, 6, 4, 4},
//					{4, 4, 6, 4, 4}	};
	
	static int [][] matrix = {
					{0, 0, 0, 2, 2},
					{1, 1, 7, 2, 2},
					{2, 2, 7, 2, 1},
					{2, 1, 7, 4, 4},
					{2, 7, 7, 4, 4},
					{4, 6, 4, 4, 4},
					{4, 4, 6, 4, 4},
					{4, 4, 6, 4, 4}	};
	
	public static void main(String[] args) {
		
		System.out.println(getSizeOfLargestArea(matrix));
		
	}
	
	static int iArea = 1;
	static int largestArea = 1;
	
	/**
	 * @author Thachapong
	 * @param matrix
	 * Matrix as 2D Array of integer.
	 * @return
	 * Longest area of the matrix.
	 */
	public static int getSizeOfLargestArea(int [][] matrix) {
		FindLargestArea aTable = new FindLargestArea();
		Node nextNode;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				nextNode = aTable.new Node(i, j);
				if(nextNode.isVisited()) continue;
				dfs(nextNode);
				if(iArea > largestArea) largestArea = iArea;
				iArea = 1;
			}
		}
		return largestArea;
	}
	
	/**
	 *  Store the index of visited nodes (i_j)
	 *  Example value: "0_3"
	 */
	static HashSet<String> visited = new HashSet<String>();
	
	
	/**
	 * @param iNode
	 * DFS, traversing to all child till the last.
	 */
	public static void dfs(Node iNode) {
		if(iNode == null) return;
		visited.add(iNode.i_j);
		for (Node node : iNode.populateAllChild()) {
			if ( !node.isVisited() ){
				iArea++;
				dfs(node);
			}
		}
	}
	
	/**
	 * @author Thachapong
	 * 
	 * Inner class propose for define node item in matrix.
	 * 
	 */
	class Node{
		// Propose for make unique key to add to visited Set when node visited.
		String i_j;
		// index of Node.
		int i, j;
		// List of all children.
		Vector<Node> children = new Vector<Node>();
		
		public Node(int i, int j) {
			i_j = i+"_"+j;
			this.i = i;
			this.j = j;
		}
		public boolean isVisited() {
			// Using benefit from HashSet feature.
			return visited.contains(i_j);
		}
		public int getMatrixValue() throws IndexOutOfBoundsException {
			return matrix[i][j];
		}
		public boolean isMatrixValueEquals(Node node) {
			try {
				return this.getMatrixValue() == node.getMatrixValue();
			} catch (Exception e) {
				// Just skip it.
			}
			return false;
		}
		private Vector<Node> populateAllChild() {
			Node [] child = new Node[4];
			child[0] = new Node(i-1, j);
			child[1] = new Node(i+1, j);
			child[2] = new Node(i, j-1);
			child[3] = new Node(i, j+1);
			for (int i = 0; i < child.length; i++) {
				// Consider only the equal of matrix value to be the member.
				// And skip the visited item to avoid redundant process.
				if(this.isMatrixValueEquals(child[i]) && !child[i].isVisited()){
					children.addElement(child[i]);
				}
			}
			return children;
		}
	}

}
