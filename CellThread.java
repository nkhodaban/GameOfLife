package GameOfLife;

import java.util.concurrent.CyclicBarrier;

public class CellThread implements Runnable {
    
    private CyclicBarrier cyclicBarrier;
    private boolean[][] grid;
    private int i, j;
    private volatile boolean running = true;

    public CellThread(boolean[][] grid, int i, int j, CyclicBarrier cyclicBarrier){
        this.grid = grid;
        this.i = i;
        this.j = j;
        this.cyclicBarrier = cyclicBarrier;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean getNextState(){

        int count = 0;

        for(int x = i - 1; x <= i + 1; x++){
            for(int y = j - 1; y <= j + 1; y++){

                if(x == i && y == j) continue;

                if(x >= 0 && x < grid.length && y >= 0 && y < grid[0].length){
                    if(grid[x][y]) count++;
                }
            }
        }

        if(grid[i][j]){
            return count == 2 || count == 3;
        }else{
            return count == 3;
        }
    }

    @Override
    public void run() {

        while (true){

            synchronized (this) { 
                while (!running) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }

            boolean nextState = getNextState();

            try{
                cyclicBarrier.await();
            }catch (Exception e){ break; }

            grid[i][j] = nextState;

            try{
                cyclicBarrier.await();
            }catch (Exception e){ break; }
        }
    }

}
