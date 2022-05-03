import java.util.*;

public class Puzzle {

    static MoveList moves() {
        return new MoveList();//Get All Possible moves
    }

    static void printBoard(Board b) {  // Printing board ex:- [14,[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1]]
        int boardSize = b.cells.length;
        System.out.print("(" + b.countPeg + ", [");
        for (int i = 0; i < boardSize; i++)
            System.out.print(i < boardSize - 1 ? b.cells[i] + ", " : b.cells[i] + "])");
        System.out.println();
    }

    static void solve(Board b, List<LinkedList<Move>> solutions, int count) {
        if (b.countPeg == 1) {
            solutions.add(new LinkedList<Move>());
            return;
        }

        for (Move m : moves()) {
            Board updatedBoard = b.move(m);
            if (updatedBoard == null)
                continue;

            List<LinkedList<Move>> tailSolutions = new ArrayList<LinkedList<Move>>();
            solve(updatedBoard, tailSolutions, count);//Recursive Call

            for (LinkedList<Move> solution : tailSolutions) {
                solution.add(0, m);
                solutions.add(solution);

                if (solutions.size() == count) // When size reaches count , end the call.
                    return;
            }
        }
    }

    static List<Move> firstSolution(Board b) {
        List<LinkedList<Move>> allSolutions = new ArrayList<LinkedList<Move>>();
        solve(b, allSolutions, 1); // We are just interested in the first solution so sending count as 1
        return allSolutions.get(0);
    }

    static void show(Board b) { 
    // prints the board in the visual form ex :    
    //      . 
    //     x x 
    //    x x x 
    //   x x x x 
    //  x x x x x    
        int[][] lines = { { 4, 0, 0 }, { 3, 1, 2 }, { 2, 3, 5 }, { 1, 6, 9 }, { 0, 10, 14 } };
        for (int i = 0; i < lines.length; i++) {
            int tab = lines[i][0];
            int start = lines[i][1];
            int end = lines[i][2];

            StringBuilder space = new StringBuilder();
            for (int j = 0; j < tab; j++) {
                space.append(" ");
            }
            System.out.print(space.toString());
            for (int k = start; k <= end; k++)
                System.out.print(b.cells[k] == 0 ? ". " : "x ");
            System.out.println();
        }

        System.out.println();
    }

    static void replay(List<Move> moves, Board b) {
        show(b);
        for (Move m : moves) {
            b = b.move(m);
            show(b);
        }
    }

    // prints out a terse view of solutions for each missing peg
    static void terse() {
        for (int i = 0; i < 15; i++) {
            Board b = new Board(i);
            printBoard(b); // Printing initial board
            List<Move> moves = firstSolution(b);
            for (Move m : moves) {
                System.out.println(m); // printing all moves of the first solution
                b = b.move(m);
            }
            printBoard(b); // printing final board
            System.out.println();
        }
    }

    // visualizes a solution for each first 5 positions
    // others look the same after 120 degrees rotations
    static void go() {
        for (int i = 0; i < 5; i++) {
            System.out.println("=== " + i + " ===");
            Board b = new Board(i);
            List<Move> moves = firstSolution(b);
            replay(moves, b);
            System.out.println();
        }
    }

    public static void main(String[] args) {
        go();
        terse();
    }
}

class Board {
    public int countPeg;
    public int[] cells; //Using Array data structure to represent board '0' in empty cells  and '1' in other cells

    public Board(int emptyCell) {
        cells = new int[15];
        Arrays.fill(cells, 1);
        cells[emptyCell] = 0;
        countPeg = 14;
    }

    public Board(int countPeg, int[] cells) {
        this.countPeg = countPeg;
        this.cells = cells.clone();
    }

    public Board move(Move m) { 
        if (cells[m.from] == 1 && cells[m.over] == 1 && cells[m.to] == 0) { // checking for valid move
            Board updatedBoard = new Board(countPeg - 1, cells.clone());
            updatedBoard.cells[m.from] = 0;
            updatedBoard.cells[m.over] = 0;
            updatedBoard.cells[m.to] = 1;

            return updatedBoard;
        } else {
            return null;
        }

    }
}

class Move {
    public int from;
    public int over;
    public int to;

    public Move(int from, int over, int to) {
        this.from = from;
        this.over = over;
        this.to = to;
    }

    public Move reverseMove() {
        return new Move(to, over, from);
    }

    @Override
    public String toString() {
        return "(" + from + ", " + over + ", " + to + ")";
    }
}

class MoveList implements Iterable<Move> {
    static final Move[] moves = new Move[] {
            new Move(0, 1, 3),
            new Move(0, 2, 5),
            new Move(1, 3, 6),
            new Move(1, 4, 8),
            new Move(2, 4, 7),
            new Move(2, 5, 9),
            new Move(3, 6, 10),
            new Move(3, 7, 12),
            new Move(4, 7, 11),
            new Move(4, 8, 13),
            new Move(5, 8, 12),
            new Move(5, 9, 14),
            new Move(3, 4, 5),
            new Move(6, 7, 8),
            new Move(7, 8, 9),
            new Move(10, 11, 12),
            new Move(11, 12, 13),
            new Move(12, 13, 14)
    };

    @Override
    public CustomMoveIterator iterator() {
        return new CustomMoveIterator(moves);
    }
}

class CustomMoveIterator implements Iterator<Move> {
    private Move[] moves;
    private int index;
    private Move reverseMove;

    public CustomMoveIterator(Move[] moves) {
        this.moves = moves;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < moves.length || (index == moves.length && reverseMove != null);
    }

    @Override
    public Move next() {
        if (reverseMove == null) {
            Move move = moves[index];
            reverseMove = move.reverseMove();
            index++;
            return move;
        } else {
            Move move = reverseMove;
            reverseMove = null;
            return move;
        }
    }
}
