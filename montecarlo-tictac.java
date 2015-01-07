import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {
	
	public static Random RAND = new Random();
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		String playerString = in.nextLine();
		int [] squareLocation = new int[2];
		squareLocation[0] = in.nextInt();
		squareLocation[1] = in.nextInt();
		in.nextLine();
		String [] board = new String [9];
		for (int i = 0 ; i < 9; i++ )
		{
			board[i] = in.nextLine();
		}

		MonteCarloTS (board, squareLocation , playerString.charAt(0));

		
	}

	
	public static void MonteCarloTS(String[] b, int[] squareLocation,
			char player) {

		// System.out.println("Square: "+squareLocation[0]+" "+squareLocation[1]);
		
		//board char array
				char [][] board = new char[9][9];
				for (int i=0; i <9;i++)
					for (int j=0; j<9;j++)
						board[i][j] = b[i].charAt(j);
				
		//initializing squareWinner matrix. each value indicates the winner of the corresponding square. '-' means the square is not won.
		char[][] squareWinner = new char [3][3];
		for (int i = 0; i<3;i++)
			for (int j =0; j<3; j++)
			{
				int [] s = {i,j};
				squareWinner[i][j] = checkSquareWinner(board,s);
			}
		
		
		ArrayList<int[]> validMoves = new ArrayList<int[]>();
		
		// square owned by some player.
		if(squareLocation[0] ==-1 || squareWinner[squareLocation[0]][squareLocation[1]]!='-')
		{

		validMoves = getGlobalValidMoves(squareWinner, board);
		
	}
		else{
		validMoves = getValidMoves(squareLocation, board);
		}
		
		// for(int i =0; i<validMoves.size();i++)
		// System.out.println("Valid: "+validMoves.get(i)[0]+" "+validMoves.get(i)[1]);
		int maxRemainingMoves = getRemainingMoves(b);
		ArrayList<int[]> moveResults = new ArrayList<int[]>(validMoves.size());
		for (int i = 0; i < validMoves.size(); i++){
			moveResults.add(new int[5]);
		}
		/* 
		 * ties = 0
		 * losses = 1
		 * wins = 2
		 * weightedLosses = 3
		 * weightedWins = 4
		 * 
		*/
		
		long time = System.currentTimeMillis();
		long currentTime = System.currentTimeMillis();
		while(currentTime-time <3000)
		{
		for (int i = 0; i < validMoves.size(); i++) {

			String[] result = simulateGame(validMoves.get(i), squareLocation,
					getBoardCopy(board), player, getSquareWinnerCopy(squareWinner));
			int weight = (10*maxRemainingMoves - Integer.parseInt(result[1]));
		
		
			if (result[0].equals("-")) {
				//tie
				(moveResults.get(i))[0]++;
			} else {
				if (result[0].equals(Character.toString(player))) {
					//win	
					(moveResults.get(i))[2]++;
					(moveResults.get(i))[4]+= weight;
				} else {
					//loss
					(moveResults.get(i))[1]++;
					(moveResults.get(i))[3]+= weight;
				}
			}
		}
		
		currentTime = System.currentTimeMillis();
		}
		
		 int bestScore = Integer.MIN_VALUE;
	        int[] bestMove = new int [2];
	        for ( int i=0; i< validMoves.size(); i++) {
	          
	            int score = ( moveResults.get(i)[4]- moveResults.get(i)[3]*10)/
	            			(moveResults.get(i)[0]+   moveResults.get(i)[1]+   moveResults.get(i)[2]);
	         
	            if(score > bestScore) {
	                bestScore = score;
	                bestMove = validMoves.get(i);
	            }
	        }

	        
	        //Printing best Move
		      System.out.println( (bestMove[0]/3)+" "+ (bestMove[1]/3) + " " + (bestMove[0]%3)+ " "+ (bestMove[1]%3) );
	}
	
	private static int getRemainingMoves(String[] board)
	{
		int remainingMoves =0;
		for(int i = 0;i<9; i++)
			for (int j=0; j<9;j++)
			{
				if(board[i].charAt(j)=='-')
				remainingMoves++;
			}
		return remainingMoves;
	}

	public static String[] simulateGame(int[] move, int[] squareLocation,
			char[][] b, char player, char[][] s) {
		// TODO Auto-generated method stub
		int counter = 1;
		char winner = '-';

		char[][] squareWinner = s.clone();
		char[][] board = b.clone();

		// playing input move
		board[move[0]][move[1]] = player;

		// check winner
		winner = checkGameWinner(squareWinner);
		if (winner != '-') {
			String[] out = { Character.toString(winner) , Integer.toString(counter) };
			return out;
		}

		// //pre-processing random numbers
		// int[] lookupTable = new int[81];
		// for(int i = 0;i<81;i++)
		// lookupTable[i] = (int) (Math.random()*1000);
		//
		//
		char enemyPlayer = 'O';
		if (player == 'O')
			enemyPlayer = 'X';

		char currentPlayer = enemyPlayer;
		int[] currentSquareLocation = new int[2];
		currentSquareLocation[0] = move[0] % 3;
		currentSquareLocation[0] = move[1] % 3;
		while ((checkGameWinner(board) != 'X' || checkGameWinner(board) != 'O')
				&& !gameOver(squareWinner)) {
			ArrayList<int[]> validMoves = new ArrayList<int[]>();
			if (squareWinner[currentSquareLocation[0]][currentSquareLocation[1]] != '-') {

				validMoves = getGlobalValidMoves(squareWinner, board);

			} else {
				validMoves = getValidMoves(currentSquareLocation, board);
			}
			counter++;

			// odd equals AI agent
			if (counter % 2 == 0)
				currentPlayer = enemyPlayer;
			else
				currentPlayer = player;

			// choose valid random move
			int[] currentMove = validMoves
					.get((RAND.nextInt(Integer.MAX_VALUE) % validMoves.size()));
			// play random move
			// System.out.println(currentMove[0]+" "+currentMove[1]);
			board[currentMove[0]][currentMove[1]] = currentPlayer;
			currentSquareLocation[0] = currentMove[0] / 3;
			currentSquareLocation[1] = currentMove[1] / 3;
			squareWinner[currentSquareLocation[0]][currentSquareLocation[1]] = checkSquareWinner(
					board, currentSquareLocation);
			// squareWinner-ception :P
			winner = checkGameWinner(squareWinner);

			if (winner != '-')

			{
				String[] out = { Character.toString(winner) , Integer.toString(counter) };
				return out;
			}

			currentSquareLocation[0] = currentMove[0] % 3;
			currentSquareLocation[1] = currentMove[1] % 3;

		}

		String[] out = { Character.toString(winner) , Integer.toString(counter) };
		return out;
	}


	private static ArrayList<int[]> getGlobalValidMoves(char[][] squareWinner,
			char[][] board) {
		
		
		ArrayList<int[]> validMoves = new ArrayList<int[]>();
		for (int l=0;l<3;l++)
		for (int k=0;k<3;k++)
			if (squareWinner[l][k]=='-')
			{
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++) {
					if (board[i + 3 * l][j + 3* k] == '-')
				{
					int[] position = { i + 3 *l, j + 3 * k};
					validMoves.add(position);
				}
				}
			}
			
		return  validMoves;
		
	}


	public static boolean gameOver(char[][] board) {
		for (int i = 0; i <3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == '-')
					return false;
			}
		}
		return true;
	}

	public static char checkSquareWinner(char [][]board, int[] squareLocation )
	{
		
        for(int i = 0; i < 3; i++)
        {
            char win = checkRow(i,board, squareLocation);
            if (win == '-')
            {
            	win = checkcolumn(i, board, squareLocation);
            	if(win!= '-')
            		return win;
            }
            else 
            	return win;
        }
        
        //Check diagonal top left to bottom right
    	int rowOffset = 3 * squareLocation[0];
		int columnOffset =  3* squareLocation[1];
        if(board[rowOffset][columnOffset] != '-')
        {
            if(board[rowOffset][columnOffset] == board[1+rowOffset][1+columnOffset] &&
            		board[1+rowOffset][1+columnOffset] == board[2+rowOffset][2+columnOffset])
            {
                return board[rowOffset][columnOffset];
            }
        }
        //Check diagonal top right to bottom left
         if(board[rowOffset][2+columnOffset] != '-')
        {
            if(board[rowOffset][2+columnOffset] == board[1+rowOffset][1+columnOffset] &&
            		board[1+rowOffset][1+columnOffset] == board[2+rowOffset][columnOffset])
            {
               return  board[rowOffset][2+columnOffset];
            }
        }
        
        if(checkSquareTie(board,squareLocation))
		{//	System.out.println("squarelocation"+ squareLocation[0]+""+squareLocation[1]);
			return '!';
		}
        return '-';

	}
	private static boolean checkSquareTie(char[][] board, int[] squareLocation) {

		for (int i =0; i <3; i++){
			for (int j = 0 ; j <3;j++)
			{
				if (board[i+ 3 * squareLocation[0]][j+  + 3 * squareLocation[1]]== '-')
					return false;
			}
		}
		return true;
	}


	public static char checkRow(int row, char[][] board, int[] squareLocation)
    {
		int rowOffset = 3 * squareLocation[0];
		int columnOffset =  3* squareLocation[1];
        if(board[row+rowOffset][columnOffset] == '-')
        {
            return '-';
        }
        if(board[row+rowOffset][columnOffset] == board[row+rowOffset][1+columnOffset]&&
        		board[row+rowOffset][1+columnOffset] == board[row+rowOffset][2+columnOffset])
        {
            return board[row+rowOffset][columnOffset];
        }
        return '-';
    }
	
	public static char checkcolumn(int column, char[][] board, int[] squareLocation)
    {
		int rowOffset = 3 * squareLocation[0];
		int columnOffset =  3* squareLocation[1];
        if(board[rowOffset][column+columnOffset] == '-')
        {
            return '-';
        }
        if(board[rowOffset][column+columnOffset]== board[1+rowOffset][column+columnOffset]&&
        		board[1+rowOffset][column+columnOffset] == board[2+rowOffset][column+columnOffset])
        {
            return board[rowOffset][column+columnOffset];
        }
        return '-';
    }
	public static char checkGameWinner(char [][]board)
	{

        for(int i = 0; i < 3; i++)
        {
            char win = checkRow(i,board);
            if (win == '-' || win == '!')
            {
            	win = checkcolumn(i, board);
            	if(win!='-' && win!= '!')
            		return win;
            }
            else 
            	return win;
        }
        
        //Check diagonal top left to bottom right
    	if(board[0][0] != '-' && board[0][0] !='!' )
        {
            if(board[0][0]== board[1][1] &&
            		 board[1][1]==  board[2][2])
            {
                return  board[0][0];
            }
        }
        //Check diagonal top right to bottom left
         if( board[1][2] != '-' && board[1][2] != '!')
        {
            if( board[0][2]==  board[1][1]&&
            		 board[1][1]==  board[2][0])
            {
               return   board[0][2];
            }
        }
        
        return '-';

	}
	public static char[][] getBoardCopy(char[][] board)
	{
		char [][] out = new char[9][9];
				for (int i=0;i<9;i++)
					for (int j=0;j<9;j++)
						out[i][j] = board[i][j];
				return out;
	}
	public static char[][] getSquareWinnerCopy(char[][] squareWinner)
	{
		char [][] out = new char[3][3];
				for (int i=0;i<3;i++)
					for (int j=0;j<3;j++)
						out[i][j] = squareWinner[i][j];
				return out;
	}
	public static char checkRow(int row, char[][] board)
    {
		 if(board[row][0] == '-' || board[row][0] == '!')
        {
            return '-';
        }
        if(board[row][0]== board[row][1]&&
        		board[row][1] == board[row][2])
        {
            return board[row][0];
        }
        return '-';
    }
	
	public static char checkcolumn(int column, char[][] board)
    {
		{
			 if(board[0][column] == '-' ||board[0][column]=='!' )
	        {
	            return '-';
	        }
	        if(board[0][column]== board[1][column]&&
	        		board[1][column] == board[2][column])
	        {
	            return board[0][column];
	        }
	        return '-';
	    }
		
    }
		
public static  ArrayList<int[]> getValidMoves(int[] squareLocation,
			String[] square) {
		int squareBound = 3;
		if (squareLocation[0] == -1) {
			squareBound = 9;
			squareLocation[0] = 0;
			squareLocation[1] = 0;
		}
		ArrayList<int[]> validMoves = new ArrayList<int[]>();
		for (int i = 0; i < squareBound; i++)
			for (int j = 0; j < squareBound; j++) {
				if (square[i + 3 * squareLocation[0]].charAt(j + 3
						* squareLocation[1]) == '-')
				{
					int[] position = { i + 3 * squareLocation[0],
							j + 3 * squareLocation[1] };
					validMoves.add(position);
				}
			}
		return  validMoves;

	}

public static  ArrayList<int[]> getValidMoves(int[] squareLocation,
		char[][] square) {
	int squareBound = 3;
	if (squareLocation[0] == -1) {
		squareBound = 9;
		squareLocation[0] = 0;
		squareLocation[1] = 0;
	}
	ArrayList<int[]> validMoves = new ArrayList<int[]>();
	for (int i = 0; i < squareBound; i++)
		for (int j = 0; j < squareBound; j++) {
			if (square[i + 3 * squareLocation[0]][j + 3
					* squareLocation[1]] == '-')
			{
				int[] position = { i + 3 * squareLocation[0],
						j + 3 * squareLocation[1] };
				validMoves.add(position);
			}
		}
	return  validMoves;

}
}
