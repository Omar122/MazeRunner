package com.omar.maze;

import java.io.File;
import java.util.*;

public class Main {
    static Maze maze = new Maze();

    public static void main(String[] args) {
        displayMenu();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextInt()) {
            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        generateMaze();
                        displayMenu();
                        break;
                    case 2:
                        loadMaze();
                        displayMenu();
                        break;
                    case 3:
                        saveMaze();
                        displayMenu();
                        break;
                    case 4:
                        displayMaze();
                        displayMenu();
                        break;
                    case 5:
                        findEscape();
                        displayMenu();
                        break;
                    case 0:
                        exit();
                    default:
                        System.out.println("Incorrect option. Please try again");
                }
            } catch (InputMismatchException e) {
                System.out.println("Incorrect option. Please try again");
                exit();
            }

        }
    }

    private static void findEscape() {
        maze.solveMaze();
    }

    private static void displayMaze() {
        if (maze.maze == null) {
            System.out.println("Incorrect option. Please try again");
        } else {
            maze.printMaze();
        }
    }

    private static void loadMaze() {
        Scanner scanner = new Scanner(System.in);
        String filename = scanner.nextLine();
        File file = new File(filename);
        maze.loadMaze(file);
    }

    private static void saveMaze() {
        if (maze.maze == null) {
            System.out.println("Incorrect option. Please try again");
        } else {
            String filename = maze.saveMaze();
            System.out.println(filename);

        }
    }

    private static void displayMenu() {
        if (maze.maze == null) {
            System.out.println("""
                    
                    === Menu ===\s
                    1.  Generate a new maze.
                    2.  Load a maze.
                    0  .  Exit."""
            );
        } else {
            System.out.println("""
                    
                    === Menu ===\s
                    1.  Generate a new maze.
                    2.  Load a maze.
                    3.  Save the maze.
                    4.  Display the maze.
                    5.  Find the escape
                    0.  Exit."""
            );
        }
   }

    private static void generateMaze() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the size of a new maze");
        int x = scanner.nextInt();
        maze.generateMaze(x, x);
        maze.printMaze();
    }

    public static void exit() {
        System.out.println("Bye");
        System.exit(0);
    }
}
