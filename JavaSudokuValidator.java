package com.project.first;
import java.util.Scanner; 
public class JavaSudokuValidator {
	
	// Global constant for number of threads
		private static final int TOTAL_NUM_THREADS = 27;
				
		private static  int[][] sudoku = new int[9][9];
		
		
		// Array that worker threads will update
		private static boolean[] isValid;
		

		
		// General object that will be extended by worker thread objects, only contains
		// the row and column relevant to the thread
		public static class RowColumnObject {
			int row;
			int col;
			RowColumnObject(int row, int column) {
				this.row = row;
				this.col = column;
			}
		}
		
		// Runnable object that determines if numbers 1-9 only appear once in a row
		public static class IsRowisValid extends RowColumnObject implements Runnable {		
			IsRowisValid(int row, int column) {
				super(row, column); 
			}

			@Override
			public void run() {
				if (col != 0 || row > 8) {
					System.out.println("InisValid row or column for row subsection!");				
					return;
				}
				
				// Check if numbers 1-9 only appear once in the row
				boolean[] isValidityArray = new boolean[9];
				int i;
				for (i = 0; i < 9; i++) {
					// If the corresponding index for the number is set to 1, and the number is encountered again,
					// the isValid array will not be updated and the thread will exit.
					int num = sudoku[row][i];
					if (num < 1 || num > 9 || isValidityArray[num - 1]) {
						return;
					} else if (!isValidityArray[num - 1]) {
						isValidityArray[num - 1] = true;
					}
				}
				// If reached this point, row subsection is isValid.
				int index=row+9;
				isValid[index] = true;
			}

		}
		
		// Runnable object that determines if numbers 1-9 only appear once in a column
		public static class IsColumnisValid extends RowColumnObject implements Runnable {
			IsColumnisValid(int row, int column) {
				super(row, column); 
			}

			@Override
			public void run() 
			{
				if (row != 0 || col > 8) 
				{
					System.out.println("Invalid row or column for col subsection!");				
					return;
				}
				
				// Check if numbers 1-9 only appear once in the column
				boolean[] isValidityArray = new boolean[9];
				int i;
				for (i = 0; i < 9; i++) 
				{
					// If the corresponding index for the number is set to 1, and the number is encountered again,
					// the isValid array will not be updated and the thread will exit.
					int num = sudoku[i][col];
					if (num < 1 || num > 9 || isValidityArray[num - 1]) 
					{
						return;
					} 
					else if (!isValidityArray[num - 1]) 
					{
						isValidityArray[num - 1] = true;
					}
				}
				// If reached this point, column subsection is isValid.
				int index= 18+col;
				isValid[index] = true;			
			}		
		}
		
		// Runnable object that determines if numbers 1-9 only appear once in a 3x3 subsection
		public static class Is3x3isValid extends RowColumnObject implements Runnable {
			Is3x3isValid(int row, int column) 
			{
				super(row, column); 
			}

			@Override
			public void run() 
			{
				// Confirm isValid parameters
				if (row > 6 || row % 3 != 0 || col > 6 || col % 3 != 0) 
				{
					System.out.println("InValid row or column for subsection!");
					return;
				}
				
				// Check if numbers 1-9 only appear once in 3x3 subsection
				boolean[] isValidityArray = new boolean[9];			
				for (int i = row; i < row + 3; i++) 
				{
					for (int j = col; j < col + 3; j++) 
					{
						int num = sudoku[i][j];
						if (num < 1 || num > 9 || isValidityArray[num - 1]) 
						{
							return;
						} 
						else 
						{
							isValidityArray[num - 1] = true;		
						}
					}
				}
				// If reached this point, 3x3 subsection is isValid.
				int index= row + col/3;
				isValid[index] = true; // Maps the subsection to an index in the first 8 indices of the isValid array			
			}
			
		}

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		for(int i=0; i<9; i++) {
			System.out.println("Write 9 values for "+(i+1)+"th row:");
			for(int j=0; j<9; j++) {
				sudoku[i][j]=sc.nextInt();
			}
		}
		
		
				isValid = new boolean[TOTAL_NUM_THREADS];		
				Thread[] threads = new Thread[TOTAL_NUM_THREADS];
				
				int threadIndex = 0;
				
				for (int i = 0; i < 9; i++) 
				{
					for (int j = 0; j < 9; j++) 
					{						
						if (i%3 == 0 && j%3 == 0) 
						{
							//9 threads to check for 3*3 columns
							threads[threadIndex] = new Thread(new Is3x3isValid(i, j));	
							threadIndex++;
						}
						if (i == 0) 
						{		
							//9 threads to check for each column
							threads[threadIndex] = new Thread(new IsColumnisValid(i, j));
							threadIndex++;
						}
						if (j == 0) 
						{
							//9 threads to check for each row
							threads[threadIndex] = new Thread(new IsRowisValid(i, j));	
							threadIndex++;
						}
					}
				}
				
				// Start all threads
				for (int i = 0; i < threads.length; i++) 
				{
					threads[i].start();
				}
				
				// Wait for all threads to finish
				for (int i = 0; i < threads.length; i++) 
				{
					try 
					{
						threads[i].join();
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				
				// If any of the entries in the isValid array are 0, then the sudoku solution is inisValid
				for (int i = 0; i < isValid.length; i++) 
				{
					if (isValid[i]==false) 
					{
						System.out.println("Sudoku solution is not Valid!");
						return;
					}
					
				}
				System.out.println("Sudoku solution is Valid!");
				return;		

	}

}

/*
 Write 9 values for 1th row:
6 2 4 5 3 9 1 8 7
Write 9 values for 2th row:
5 1 9 7 2 8 6 3 4
Write 9 values for 3th row:
8 3 7 6 1 4 2 9 5
Write 9 values for 4th row:
1 4 3 8 6 5 7 2 9
Write 9 values for 5th row:
9 5 8 2 4 7 3 6 1
Write 9 values for 6th row:
7 6 2 3 9 1 4 5 8
Write 9 values for 7th row:
3 7 1 9 5 6 8 4 2
Write 9 values for 8th row:
4 9 6 1 8 2 5 7 3
Write 9 values for 9th row:
2 8 5 4 7 3 9 1 6
 */
