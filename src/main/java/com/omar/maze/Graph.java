package maze;

import java.util.*;

public class Graph {

    private final Map<Cell, Set<Cell>> adjVertices;

    public Graph() {
        adjVertices=new HashMap<>();
    }

    void addCell(Cell cell) {
        adjVertices.putIfAbsent(cell, new HashSet<>() {
        });
    }

    void removeCell(Cell cell) {
        adjVertices.values().stream().forEach(e -> e.remove(cell));
        adjVertices.remove(cell);
    }

    void addEdge(Cell cell1, Cell cell2) {
        adjVertices.get(cell1).add(cell2);
        adjVertices.get(cell2).add(cell1);

    }
    void addAndRremoveEdge(Cell cell1, Cell cell2) {
        if(adjVertices.get(cell1).isEmpty()){
            adjVertices.get(cell1).add(cell2);
            adjVertices.get(cell2).add(cell1);
        }else{
            adjVertices.get(cell1).removeAll(adjVertices.get(cell1));
            adjVertices.get(cell1).add(cell2);
            adjVertices.get(cell2).removeAll(adjVertices.get(cell2));
            adjVertices.get(cell2).add(cell1);
        }
    }
    int Count(){
        return adjVertices.size();
    }

    Set<Cell> getAdjVertices(Cell cell) {
        return adjVertices.get(cell);
    }

    void printGraph() {
      for(var key:adjVertices.entrySet()){
          System.out.println("Key: "+key.getKey());
          System.out.println("Value: "+key.getValue());
      }
    }

    Map<Cell, Set<Cell>> getAdjVerticesMap(){
        return adjVertices;
    }


}
