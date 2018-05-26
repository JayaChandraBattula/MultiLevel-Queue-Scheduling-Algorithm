package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

public class MultiLevelQueue {

	int[] priority= new int[500], burstTime=new int[500],completionTime=new int[500], waitingTime=new int[500], turnaroundTime=new int[500];
	static int n,quantum=4,arrivalTime=0;
	static int[] p=new int[500];
	static float totalwt,totaltat;	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		long startTime = System.currentTimeMillis();
		MultiLevelQueue multilevelqueue = new MultiLevelQueue();
		Object[] returnArrayObjects=multilevelqueue.excelReader("C:\\Users\\Chandra\\Desktop\\UC CS\\EECE 6038 Operating Systems\\Project\\Inputtestdata1.xls");
		@SuppressWarnings("unused")
		int[] burstTime=(int[])returnArrayObjects[0];
		@SuppressWarnings("unused")
		int[] priority=(int[])returnArrayObjects[1];
		multilevelqueue.scheduling();
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total time taken for execution of the code is "+totalTime+" millisec");

	}

	public void scheduling () {

		ArrayList<Integer> topPriorityList = new ArrayList<Integer>();
		ArrayList<Integer> lowPriorityProcessList = new ArrayList<Integer>();
		int z = 0;

		// High priority queue execution with FCFS scheduling algorithm

		for (int i = 0;i <n; i++) {
			p[i]=i;                                                       //Iteration of process for printing (0,1,2....)
			if (priority[i]==1)
			{
				topPriorityList.add(priority[i]);
				completionTime[i]=completionTime[z]+burstTime[i];
				z=i;
				turnaroundTime[i]=completionTime[i]-arrivalTime;
				waitingTime[i]=turnaroundTime[i]-burstTime[i];
			}
		}

		// Low Priority queue execution with Round Robin scheduling algorithm

		for (int i = 0;i <n; i++) {
			if (priority[i]==0)
			{
				lowPriorityProcessList.add(i);                            //  lowPriorityProcessList contains all the process (like p2,p3,p4) which are in low priority
			}
		}

		int[] lowPriorityProcessArray = new int[50];  
		for (int i = 0;i <lowPriorityProcessList.size(); i++) {
			lowPriorityProcessArray[i]=lowPriorityProcessList.get(i);     //Copying the process from lowPriorityProcessList to lowPriorityProcessArray
		}

		// Make a copy of burst times burstTime[] to store remaining burst times.

		int rem_bt[]= new int[lowPriorityProcessList.size()];
		for (int i = 0 ; i < lowPriorityProcessList.size() ; i++)
		{
			rem_bt[i] =  burstTime[lowPriorityProcessArray[i]];           // Burst time of all the process with low priority are copied into rem_bt array
		}

		int t = completionTime[z]; // Current time in RR

		// Keep traversing processes in round robin manner until all of them are not done.

		while (true)
		{
			boolean done = true;

			// Traverse all processes one by one repeatedly
			for (int i = 0 ; i < lowPriorityProcessList.size(); i++)
			{
				// If burst time of a process is greater than 0 then only need to process further
				if (rem_bt[i] > 0)
				{
					done = false;                                           // There is a pending process

					if (rem_bt[i] > quantum)
					{
						// Increase the value of t i.e. shows how much time a process has been processed
						t += quantum;

						// Decrease the burst_time of current process by quantum
						rem_bt[i] -= quantum;
					}

					// If burst time is smaller than or equal to quantum. Last cycle for this process
					else
					{
						// Increase the value of t i.e. shows how much time a process has been processed
						t = t + rem_bt[i];
						completionTime[lowPriorityProcessArray[i]]=t;

						// As the process gets fully executed make its remaining burst time = 0
						rem_bt[i] = 0;
					}
				}
			}

			// If all processes are done
			if (done == true)
				break;
		}

		// Calculating waiting time and turn around time for all the process in low priority queue
		for (int j = 0; j < lowPriorityProcessList.size(); j++) {
			turnaroundTime[lowPriorityProcessArray[j]]=completionTime[lowPriorityProcessArray[j]];
			waitingTime[lowPriorityProcessArray[j]]=turnaroundTime[lowPriorityProcessArray[j]]-burstTime[lowPriorityProcessArray[j]];

		}


		System.out.println("\nPROCESS\t\t PRIORITY \tBURST TIME \tCOMPLETION TIME \tWAITING TIME \tTURNAROUNDTIME");
		for(int i = 0; i < n ; i++)
		{
			System.out.println("\n"+p[i]+"\t\t\t"+priority[i]+"\t\t"+burstTime[i]+"\t\t"+completionTime[i]+"\t\t"+waitingTime[i]+"\t\t"+turnaroundTime[i]);
		}

		for(int i = 0; i < n ; i++)
		{
			totalwt+=waitingTime[i];
			totaltat+=turnaroundTime[i];
		}

		System.out.println("\n"+"Average Waiting Time is: "+totalwt/n);
		System.out.println("Average Turnaround Time is : "+totaltat/n);

	}


	public Object[] excelReader(String filepath) throws IOException {

		FileInputStream fis = new FileInputStream(new File(filepath));

		//create a workbook instance that refers to .xls file
		HSSFWorkbook wb = new HSSFWorkbook(fis);

		//create a sheet object to retrieve the sheet
		HSSFSheet dataSheet = wb.getSheetAt(0);

		//to print total number of process
		n=dataSheet.getLastRowNum();
		System.out.println("Total number of Process "+n);

		Object[] arrayObjects = new Object[2];


		// Storing burst time
		for (int r = 1; r <= n; r++) {
			Row row=dataSheet.getRow(r);
			String cell=(new DataFormatter().formatCellValue(row.getCell(1)));
			burstTime[r-1]=Integer.parseInt(cell);
			arrayObjects[0]=burstTime;
		}

		// Storing priority
		for (int r = 1; r <= n; r++) {
			Row row=dataSheet.getRow(r);
			String cell=(new DataFormatter().formatCellValue(row.getCell(2)));
			priority[r-1]=Integer.parseInt(cell);
			arrayObjects[1]=priority;
		}

		wb.close();
		fis.close();

		return arrayObjects;

	}

}
