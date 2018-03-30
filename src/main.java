import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class main {
	// 48 MHz nombres d'instructions par secondes.
	public static void main(String[] args) throws IOException {
		
		
		Task t0 = new Task((2813 / 48), 100, 100);
		Task t1 = new Task((2801 / 48), 300, 300);
		Task t2 = new Task((2999 / 48), 200, 200);

		
		Scheduler scheduler = new Scheduler();
		scheduler.addTask(t0);
		scheduler.addTask(t1);
		scheduler.addTask(t2);
		
		//scheduler.setHyperperiod(600);
		
		boolean[][] result;
		String inputPath;
		File file1;
		File file2;
		BufferedWriter bw1;
		BufferedWriter bw2;
		File file3;
		File file4;
		BufferedWriter bw3;
		BufferedWriter bw4;
		
		System.out.println("RM preemptive");
		result = scheduler.scheduleRMPreemptive();		
		System.out.println(scheduler.toString(result));		
		inputPath = "svg/RMPreemptive.svg";
		file1 = new File(inputPath);
		file1.createNewFile();
		bw1 = new BufferedWriter(new FileWriter(inputPath));
		bw1.write(scheduler.toSVG(result) + '\n');
		bw1.close();
		
		
		System.out.println("RM no preemptive");
		result = scheduler.scheduleRMNoPreemptive();
		System.out.println(scheduler.toString(result));		
		inputPath = "svg/RMNoPreemptive.svg";
		file2 = new File(inputPath);
		file2.createNewFile();
		bw2 = new BufferedWriter(new FileWriter(inputPath));
		bw2.write(scheduler.toSVG(result) + '\n');
		bw2.close();
		
		
		System.out.println("EDF preemptive");
		result = scheduler.scheduleEDFPreemptive();					
		System.out.println(scheduler.toString(result));		
		inputPath = "svg/EDFPreemptive.svg";
		file3 = new File(inputPath);
		file3.createNewFile();
		bw3 = new BufferedWriter(new FileWriter(inputPath));
		bw3.write(scheduler.toSVG(result) + '\n');
		bw3.close();
		
		
		System.out.println("EDF no preemptive");		
		result = scheduler.scheduleEDFNoPreemptive();
		System.out.println(scheduler.toString(result));		
		inputPath = "svg/EDFNoPreemptive.svg";
		file4 = new File(inputPath);
		file4.createNewFile();
		bw4 = new BufferedWriter(new FileWriter(inputPath));		
		bw4.write(scheduler.toSVG(result) + '\n');
		bw4.close();
		
		
		
	}

}
