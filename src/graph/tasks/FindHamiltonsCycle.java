package graph.tasks;

import graph.Arc;
import graph.Graph;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FindHamiltonsCycle implements GraphTasks{

    Graph graph;

    int [][] matrix;
    int [][] cycles;

    public FindHamiltonsCycle(Graph graph){
        this.graph = graph;

        matrix = new int[graph.setOfVertexes.size()][graph.setOfVertexes.size()];


        for(int []row: matrix){
            Arrays.fill(row,0);
        }

        for (int i=0;i<graph.setOfArcs.size();++i){
            Arc arc = graph.setOfArcs.get(i);

            int sourceIndex = findIndexOfVertex(arc.sourcePoint);
            int targetIndex = findIndexOfVertex(arc.targetPoint);

            boolean isDirected = arc.isDirected;

            matrix[sourceIndex][targetIndex] = 1;
            if (!isDirected) {
                matrix[targetIndex][targetIndex] = 1;
            }
        }
    }

    private int findIndexOfVertex(Point point){
        for (int i = 0; i < graph.setOfVertexes.size(); ++i) {
            if(graph.setOfVertexes.get(i).point == point)
                return i;
        }
        return -1;
    }

    @Override
    public void solveTask() {
        ArrayList<Integer> visited = new ArrayList<>();

        for(int i=0;i<matrix.length;++i){
            visited.add(i);
            depthSearch(visited,i,i);
            visited.clear();
        }

    }

    private void depthSearch(ArrayList<Integer> visited, int currentVertex,int sourceVertex){
        for(int i=0;i<matrix.length;++i){
            if(matrix[currentVertex][i]!=0 && !contains(visited,i)){
                visited.add(i);
                depthSearch(visited,i,sourceVertex);
                visited.remove(i);
            } else if(matrix[currentVertex][i]!=0 && contains(visited,i) && visited.size()==matrix.length){
                // cycle found
                // TODO store cycles somewhere
            }
        }
    }

    private boolean contains(ArrayList<Integer> visited,int currentVertex){
        for (int j : visited) {
            if (j == currentVertex) {
                return true;
            }
        }
        return false;
    }

    private void printMatrix(){
        for(int i=0;i<graph.setOfVertexes.size();++i){
            for(int j=0;j<graph.setOfVertexes.size();++j){
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println("");
        }
    }

}
