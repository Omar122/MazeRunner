package com.omar.maze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Maze {

    private static final String PASSAGE = "\s\s";
    private static final String WALL = "██";

    static BiPredicate<int[][], Cell> isBlocked = (maze, cell) -> maze[cell.x()][cell.y()] == 1;

    private Graph mazeGraph;

    public Maze() {
    }

    public static List<Cell> getNeighbor(Cell cell) {
        return new ArrayList<>(
                List.of(new Cell(cell.x() + 1, cell.y()),
                        new Cell(cell.x() - 1, cell.y()),
                        new Cell(cell.x(), cell.y() + 1),
                        new Cell(cell.x(), cell.y() - 1)));
    }

    public static List<Cell> getFrontierCells(Cell cell) {
        return new ArrayList<>(
                List.of(new Cell(cell.x() + 2, cell.y()),
                        new Cell(cell.x() - 2, cell.y()),
                        new Cell(cell.x(), cell.y() + 2),
                        new Cell(cell.x(), cell.y() - 2)));
    }

    public static Cell getInbetweenCell(Cell cell1, Cell cell2) {
        int x = (cell1.x() + cell2.x()) / 2;
        int y = (cell1.y() + cell2.y()) / 2;
        return new Cell(x, y);
    }

    int[][] maze;

    public void generateMaze(int x, int y) {
        maze = new int[x][y];
        //Start with blocked Maze.
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                this.maze[i][j] = 1;
            }
        }
        Random random=new Random();
        mazeGraph = new Graph();
        int middle = (maze.length - 1) / 2;
        int offset=4;
        int rootX=random.nextInt(offset * 2 + 1) + middle - offset;
        offset=3;
        int rootY=random.nextInt(offset * 2 + 1) + middle - offset;

        ArrayList<Cell> queue = new ArrayList<>();
        queue.add(new Cell(rootX, rootY));
        Cell nextCell = queue.getFirst();

        List<Cell> frontierCells = getFrontierCells(nextCell);
        maze[nextCell.x()][nextCell.y()] = 0;
        while (!frontierCells.isEmpty()) {
            //Collections.shuffle(frontierCells);
            AtomicInteger count= new AtomicInteger();
            Optional<Cell> frontierCell = frontierCells.stream()
                    .filter(e -> e.x() > 0 && e.y() > 0 && e.x() < maze.length - 1 && e.y() < maze[0].length - 1)
                    .filter(e -> isBlocked.test(maze, e))
                    .peek(e-> count.getAndIncrement())
                    .skip(random.nextInt(count.get()==0?1:count.get()))
                    .findFirst();
            if (frontierCell.isEmpty()) {
                queue.remove(nextCell);
                if (queue.isEmpty()) {
                    break;
                }
                Collections.shuffle(queue);
                nextCell = queue.getFirst();
                frontierCells = getFrontierCells(nextCell);
                continue;
            }
            Cell inbetweenCell = getInbetweenCell(nextCell, frontierCell.get());
            maze[inbetweenCell.x()][inbetweenCell.y()] = 0;
            maze[frontierCell.get().x()][frontierCell.get().y()] = 0;
            //Adding nodes Graph 
            mazeGraph.addCell(nextCell);
            mazeGraph.addCell(inbetweenCell);
            mazeGraph.addCell(frontierCell.get());
            mazeGraph.addEdge(nextCell, inbetweenCell);
            mazeGraph.addEdge(inbetweenCell, frontierCell.get());

            queue.add(frontierCell.get());
            //Collections.shuffle(queue);
            nextCell = queue.getFirst();
            frontierCells = getFrontierCells(nextCell);
        }

        assignEntry();
        assignExit();
    }

    private void assignEntry() {
        for (int i = 0; i < maze.length; i++) {
            if (!isBlocked.test(maze, new Cell(i, 1))) {
                mazeGraph.addCell(new Cell(i, 0));
                mazeGraph.addEdge(new Cell(i, 1), new Cell(i, 0));
                maze[i][0] = 0;
                break;
            } else if (!isBlocked.test(maze, new Cell(i, 2))) {
                maze[i][0] = 0;
                maze[i][1] = 0;
                mazeGraph.addCell(new Cell(i, 0));
                mazeGraph.addCell(new Cell(i, 1));
                mazeGraph.addEdge(new Cell(i, 1), new Cell(i, 0));
                mazeGraph.addEdge(new Cell(i, 1), new Cell(i, 2));
                break;
            }
        }
    }

    private void assignExit() {
        main:
        for (int i = maze.length - 1; i > 0; i--) {
            for (int j = maze[0].length - 1; j > 0; j--) {
                if (!isBlocked.test(maze, new Cell(i, j))) {
                    Cell lastCell = new Cell(i, j);
                    Cell exitCell = new Cell(i, maze[0].length - 1);
                    Cell inbetweenCell = getInbetweenCell(lastCell, exitCell);
                    mazeGraph.addCell(inbetweenCell);
                    mazeGraph.addCell(exitCell);
                    mazeGraph.addEdge(lastCell, inbetweenCell);
                    mazeGraph.addEdge(exitCell, inbetweenCell);
                    maze[inbetweenCell.x()][inbetweenCell.y()] = 0;
                    maze[exitCell.x()][exitCell.y()] = 0;
                    break main;
                }
            }
        }
    }

    public void printMaze() {
        for (int[] row : maze) {
            System.out.println();
            for (int j = 0; j < maze[0].length; j++) {
                if (row[j] == 0) {
                    System.out.print(PASSAGE);
                } else {
                    System.out.print(WALL);
                }
            }
        }
    }

    public String saveMaze() {
        File file = new File("test_maze.txt");
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (int[] row : maze) {
                printWriter.write("\n");
                for (int j = 0; j < maze[0].length; j++) {
                    if (row[j] == 0) {
                        printWriter.write(PASSAGE);
                    } else {
                        printWriter.write(WALL);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return file.getName();
    }

    public void loadMaze(File file) {
        try {
            String bytes = new String(Files.readAllBytes(file.toPath()));
            int size = bytes.lines().skip(1).findFirst().get().length() / 2;
            maze = new int[size][size];
            mazeGraph = new Graph();
            AtomicInteger rowCount = new AtomicInteger(0);
            bytes.lines().sequential().skip(1).map(e -> {
                e = e.replace(PASSAGE, " ");
                e = e.replace(WALL, "█");
                return e;
            }).forEachOrdered(e -> {
                for (int i = 0; i < e.length(); i++) {
                    if (e.charAt(i) == '█') {
                        maze[rowCount.get()][i] = 1;
                    } else {
                        maze[rowCount.get()][i] = 0;
                        mazeGraph.addCell(new Cell(rowCount.get(), i));

                    }
                }
                rowCount.incrementAndGet();
            });

            for (Cell cell : mazeGraph.getAdjVerticesMap().keySet()) {
                List<Cell> neighbors = getNeighbor(cell);
                neighbors.stream()
                        .filter(e -> e.x() > 0 && e.y() > 0 && e.x() < maze.length - 1 && e.y() < maze[0].length - 1)
                        .filter(Predicate.not(e -> isBlocked.test(maze, e)))
                        .filter(mazeGraph.getAdjVerticesMap()::containsKey)
                        .filter(e -> !mazeGraph.getAdjVertices(cell).contains(e))
                        .forEach(e -> mazeGraph.addEdge(cell, e));
            }
            mazeGraph.printGraph();

        } catch (IOException e) {
            System.out.println("The file ... does not exist");
        } catch (ArrayIndexOutOfBoundsException | NoSuchElementException ex) {
            System.out.println("Cannot load the maze. It has an invalid format");
        }
    }

    public void solveMaze() {
        Cell entryCell = new Cell(0, 0);
        Cell exitCell = null;
        //find entry and exi
        for (int i = 0; i < maze.length; i++) {
            if (maze[i][0] == 0) {
                entryCell = new Cell(i, 0);
                break;
            }
        }
        for (int i = maze.length - 1; i >= 0; i--) {
            if (maze[i][maze.length - 1] == 0) {
                exitCell = new Cell(i, maze.length - 1);
                break;
            }
        }

        Set<Cell> pathCells = dij(mazeGraph, exitCell, entryCell);
        printMazeSolution(pathCells);
    }

    private Set<Cell> dij(Graph solution, Cell exitCell, Cell entryCell) {
        Set<Cell> visited = new HashSet<>();
        Map<Cell, Integer> dist = new HashMap<>();
        Queue<Cell> q = new ArrayDeque<>();
        for (Cell cell : solution.getAdjVerticesMap().keySet()) {
            dist.put(cell, Integer.MAX_VALUE);
        }
        dist.put(entryCell, 0);
        q.offer(entryCell);
        while (!q.isEmpty()) {
            Cell cell = q.poll();
            q.addAll(solution.getAdjVertices(cell).stream().filter(Predicate.not(visited::contains)).toList());
            for (Cell neiCell : solution.getAdjVertices(cell)) {
                int alt = dist.get(cell) + 1;
                if (alt < dist.get(neiCell)) {
                    dist.put(neiCell, alt);
                    visited.add(neiCell);
                    if (neiCell.equals(exitCell)) {
                        break;
                    }
                }
            }
        }
        Set<Cell> path = new HashSet<>();
        q.offer(exitCell);
        while (!q.isEmpty()) {
            Cell cell = q.poll();
            if (cell.equals(entryCell)) {
                path.add(cell);
                break;
            }
            List<Cell> adjVertices = solution.getAdjVertices(cell).stream().distinct().toList();
            path.add(cell);
            if (adjVertices.size() == 1) {
                q.offer(adjVertices.stream().findFirst().get());
            } else {
                Optional<Map.Entry<Cell, Integer>> min = dist.entrySet().stream()
                        .filter(e -> adjVertices.contains(e.getKey()))
                        .filter(Predicate.not(e -> path.contains(e.getKey())))
                        .min(Map.Entry.comparingByValue()).stream().findFirst();
                q.offer(min.get().getKey());
            }

        }
        return path;
    }

    public void printMazeSolution(Set<Cell> visited) {
        for (int i = 0; i < maze.length; i++) {
            System.out.println();
            for (int j = 0; j < maze[0].length; j++) {
                if (maze[i][j] == 0) {
                    if (visited.contains(new Cell(i, j))) {
                        System.out.print("//");
                    } else {
                        System.out.print(PASSAGE);
                    }
                } else {
                    System.out.print(WALL);
                }
            }
        }
    }
}
