package ConwaysLifeGame;

public class Conway {

    private static boolean[][] grid;
    private static final int rows = 20;
    private static final int cols = 50;

    static void main(String[] args) throws InterruptedException {
        grid = new boolean[rows][cols];
        grid[10][5] = true;
        grid[10][6] = true;
        grid[11][4] = true;
        grid[11][5] = true;
        grid[12][5] = true;

        while (true){
            print();
            generateNewCells();
            Thread.sleep(400);
        }
    }

    static int checkNeighbours(boolean[][] grid, int x, int y){
        int count = 0;
        for (int r = -1; r <= 1; r++){
            for (int c = -1; c <= 1; c++){
                if (r == 0 && c == 0) continue;

                int nx = (r + x + rows) % rows;
                int ny = (c + y + cols) % cols;

                if (grid[nx][ny]) count++;
            }
        }
        return count;
    }

    static void generateNewCells(){
        boolean[][] newCells = new boolean[rows][cols];
        for (int x = 0; x < rows; x++){
            for (int y = 0; y < cols; y++){
                int count = checkNeighbours(grid, x , y);
                if (grid[x][y]){
                    newCells[x][y] = (count == 2 || count == 3);
                } else {
                    newCells[x][y] = (count == 3);
                }
            }
        }
        grid = newCells;
    }

    static void print(){
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                System.out.print(grid[i][j] ? "o" : " ");
            }
            System.out.println();
        }
    }

}
