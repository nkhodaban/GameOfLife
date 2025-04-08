package GameOfLife;

import java.awt.BorderLayout;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class ApplicationFrame extends JFrame {

    private static final int GAP = 4;
    private final CellPanel view;
    private final Random rand = new Random();
    private final Thread[][] threads;

    private CyclicBarrier barrier;


    public ApplicationFrame(boolean[][] grid, CellThread[][] cells, int row, int col, int size){

        // Trying to change look of GUI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        setLayout(new BorderLayout(GAP, GAP));

        threads = new Thread[row][col];

        // Adding few panels on screen
        view = new CellPanel(grid, row, col, size);
        add(view, BorderLayout.NORTH);
        addButtons(grid, cells);

        // Options for frame, just ignore
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addButtons(boolean[][] grid, CellThread[][] cells){

        JPanel bottom = new JPanel();
        JButton start = new JButton("Start");
        JButton run = new JButton("Run");
        JButton stop = new JButton("Stop");
        JButton step = new JButton("Step");
        JButton end = new JButton("End");
        JButton clear = new JButton("Clear");
        JButton randomize = new JButton("Random Cells");

        bottom.add(start);
        bottom.add(run);
        bottom.add(stop);
        bottom.add(step);
        bottom.add(end);
        bottom.add(clear);
        bottom.add(randomize);
        add(bottom, BorderLayout.SOUTH);

        // Action Listener for start button
        start.addActionListener(_ -> {

            this.barrier = new CyclicBarrier(cells.length * cells[0].length, this::repaint);

            // Creating and start new Threads for each cell
            for(int i = 0; i < cells.length; i++){
                for(int j = 0; j < cells[0].length; j++){
                    cells[i][j] = new CellThread(grid, i, j, this.barrier);
                    threads[i][j] = new Thread(cells[i][j]);
                    threads[i][j].start();
                }
            }
            
            start.setEnabled(false);
            run.setEnabled(false);
            stop.setEnabled(true);
            step.setEnabled(false);
            end.setEnabled(true);
            clear.setEnabled(false);
            randomize.setEnabled(false);
        });

        // Action listener for run button
        run.addActionListener(_ -> {
            // Re-start Threads for each cell
            for(CellThread[] cellArray : cells){
                for(CellThread cell : cellArray){
                    synchronized (cell) {
                        cell.setRunning(true);
                        cell.notify();
                    }
                }
            }
            
            start.setEnabled(false);
            run.setEnabled(false);
            stop.setEnabled(true);
            step.setEnabled(false);
            end.setEnabled(true);
            clear.setEnabled(false);
            randomize.setEnabled(false);
        });

        // Action listener for stop button
        stop.addActionListener(_ -> {
            // Pause all threads
            for (CellThread[] cellArray : cells) {
                for (CellThread cell : cellArray) {
                    cell.setRunning(false);
                }
            }

            start.setEnabled(false);
            run.setEnabled(true);
            stop.setEnabled(false);
            step.setEnabled(true);
            end.setEnabled(true);
            clear.setEnabled(false);
            randomize.setEnabled(true);
        });

        // Action listener for step button
        step.addActionListener(_ -> {
            // Just one step of simulation
            for(CellThread[] cellArray : cells){
                for(CellThread cell : cellArray){
                    synchronized (cell) {
                        cell.setRunning(true);
                        cell.notify();
                    }
                }
            }
            for (CellThread[] cellArray : cells) {
                for (CellThread cell : cellArray) {
                    cell.setRunning(false);
                }
            }
            
            start.setEnabled(false);
            run.setEnabled(true);
            stop.setEnabled(false);
            step.setEnabled(true);
            end.setEnabled(true);
            clear.setEnabled(false);
            randomize.setEnabled(false);

        });

        // Action listener for end button
        end.addActionListener(_ ->{
            // Just interrupting all threads
            for(int i = 0; i < cells.length; i++){
                for(int j = 0; j < cells[0].length; j++){
                    threads[i][j].interrupt();
                }
            }
            
            start.setEnabled(true);
            run.setEnabled(false);
            stop.setEnabled(false);
            step.setEnabled(false);
            end.setEnabled(false);
            clear.setEnabled(true);
            randomize.setEnabled(true);
        });

        // Action listener for clear button
        clear.addActionListener(_ ->{
            // Give false value to each position in grid
            for(int i = 0; i < grid.length; i++){
                for(int j = 0; j < grid[0].length; j++){
                    grid[i][j] = false;
                }
            }
            repaint();
        });

        // Action listener for randomize button
        randomize.addActionListener(_ ->{
            // Give random value to each position in grid
            for(int i = 0; i < grid.length; i++){
                for(int j = 0; j < grid[0].length; j++){
                    grid[i][j] = rand.nextBoolean();
                }
            }
            repaint();
        });
    }
}