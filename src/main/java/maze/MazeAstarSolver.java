package maze;

import java.util.*;

public class MazeAstarSolver {

    private Maze maze;
    private ArrayList<Node> nodeList;

    private PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(Node::getFinalDist));

    private ArrayList<Node> closedList= new ArrayList<>();

    private ArrayList<Node> path = null;

    private Node sourceNode, targetNode;

    public MazeAstarSolver(Maze maze) {
        this.maze = maze;
        nodeList = new ArrayList<>(maze.getNodesList().size());
        sourceNode = maze.getSourceNode();
        targetNode = maze.getTargetNode();
    }

    public ArrayList<Node> solve() {
        openList.add(sourceNode);

        sourceNode.setDistFromStart(0);
        sourceNode.calcHeuristicDist(targetNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            closedList.add(current);

            if(current.equals(targetNode)) {
                path = reconstructPath();
                return path;
            }

            for(var negihbor : current.getNeighbors().entrySet()) {
                Node node = negihbor.getKey();
                int weight = negihbor.getValue();
                node.calcHeuristicDist(targetNode);
                if(closedList.contains(node)) continue;     //this node was already evaluated

                if(!openList.contains(node)) openList.add(node);        //found a new node

                double score = current.getDistFromStart() + weight;

                if(score < node.getDistFromStart()) {
                    node.setParentNode(current);
                    node.setDistFromStart(score);
                }
            }
        }
        path = null;
        return null;

    }

    private ArrayList<Node> reconstructPath() {
        var nodeStack = new Stack<Node>();
        var currNode = targetNode;
        nodeStack.add(currNode);
        do  {
            currNode = currNode.getParentNode();
            nodeStack.add(currNode);
        } while (currNode != sourceNode);

        var list = new ArrayList<Node>(nodeStack.size());

        while(nodeStack.size() > 0)
            list.add(nodeStack.pop());
        return list;
    }


    public String getStringSolution() {
        String str = maze.stringFormat();
        String[] rows = str.split("\n");

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < rows.length; ++i) {
            StringBuilder row = new StringBuilder(rows[i]);
            for(var node : path) {
                if(node.getY() == i && !node.isSourceNode() && !node.isTargetNode()) {
                    row.setCharAt(node.getX() , '*');
                }
            }
            builder.append(row).append("\n");
        }

        return builder.toString();
    }

    public String getDirections() {
        StringBuilder builder = new StringBuilder(path.size());
        for(var node : path) {
            if(node == sourceNode) continue;                //skip the source node, we are already there
            if(node.getParentNode().getY() < node.getY())
                builder.append('d');                                //parent node is above this node
            else if(node.getParentNode().getY() > node.getY())
                builder.append('u');                                //parent node is below this
            else if(node.getParentNode().getX() < node.getX())
                builder.append('r');                                //parent node is on the left side of this
            else if(node.getParentNode().getX() > node.getX())
                builder.append('l');                                //parent node is on the right side of this
        }

        return builder.toString();
    }

}