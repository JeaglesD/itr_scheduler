
import java.io.ObjectOutputStream.PutField;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scheduler {

	private List<Task> tasks;
	private int hyperperiod;

	public Scheduler() {
		this.tasks = new ArrayList<Task>();
		this.hyperperiod = 0;
	}

	public Scheduler(Scheduler scheduler) {
		this.tasks = scheduler.tasks;
		this.hyperperiod = scheduler.hyperperiod;
	}

	public void addTask(Task task) {		
			this.tasks.add(task);
			task.setId(this.tasks.size() - 1);
			calculateHyperperiod();
	}

	public int getHyperperiod() {
		return hyperperiod;
	}
	
	public void setHyperperiod(int hyperperiod) {
		this.hyperperiod = hyperperiod;
	}
	
	private void init() {
		for(Task task: tasks){
			task.done();
		}		
	}
	
	private void calculateHyperperiod()
	{		
	    int result = this.tasks.get(0).getPeriod();
	    for(int i = 1; i < this.tasks.size(); i++) result = (int) lcm(result, this.tasks.get(i).getPeriod());
	    this.hyperperiod = result;
	}
		
	private static long lcm(long a, long b)
	{
	    return a * (b / gcd(a, b));
	}
	
	private static long gcd(long a, long b)
	{
	    while (b > 0)
	    {
	        long temp = b;
	        b = a % b; // % is remainder
	        a = temp;
	    }
	    return a;
	}
	
	private Task getFirstTask() {
		Task firstTask = new Task(-1, -1, -1);
		firstTask.setPriority(-1);
		for (int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).getPriority() > firstTask.getPriority()
					&& tasks.get(i).getToExecute() > 0) {
				firstTask = tasks.get(i);
			}
		}
		return firstTask;
	}
	
	private void calculatePrioritiesRM() {
		
		for (Task task : tasks) {
			int priority = 0;
			for (Task otherTask : tasks) {
				if (task.getId() != otherTask.getId()) {
					if (task.getPeriod() < otherTask.getPeriod()) {
						priority++;
					}else if(task.getPeriod() == otherTask.getPeriod() &&
							task.getId() < otherTask.getId()){
						priority ++;
					}
				}
			}
			task.setPriority(priority);
		}
	}

	private void calculatePrioritiesEDF(int time){
		int[] gaps = new int[tasks.size()];
		for(int i = 0; i < tasks.size(); i++){
			gaps[i] = tasks.get(i).getDeadline() - (time % tasks.get(i).getPeriod());
		}
		Task task;
		Task otherTask;
		for (int i = 0; i < tasks.size(); i++) {
			int priority = 0;
			task =  tasks.get(i);
			for (int j = 0; j < tasks.size(); j++){
				otherTask = tasks.get(j);
				if (task.getId() != otherTask.getId()) {
					if (gaps[i] < gaps[j]) {
						priority++;
					}else if(gaps[i] == gaps[j] &&
							task.getId() < otherTask.getId()){
						priority ++;
					}
				}
			}
			task.setPriority(priority);
		}
	}
	
	public boolean[][] scheduleRMPreemptive() {
		boolean[][] result = new boolean[tasks.size()][hyperperiod];
		calculateHyperperiod();
		init() ;
		calculatePrioritiesRM();
		Task currentTask;
		for (int i = 0; i < hyperperiod; i++) {
			for (Task task : tasks) {
				if (i % task.getPeriod() == 0) {
					task.execute();
				}
			}
			currentTask = getFirstTask();
			if (currentTask.getId() >= 0) {
				result[currentTask.getId()][i] = true;
				currentTask.executed();
			}
		}
		
		return result;
	}
	
	public boolean[][] scheduleRMNoPreemptive() {
		boolean[][] result = new boolean[tasks.size()][hyperperiod];
		calculateHyperperiod();		
		Task currentTask;
		int i = 0;
		int startNoPre;
		init();
		calculatePrioritiesRM();
		for(Task task: tasks){
			if(i % task.getPeriod() == 0){
				task.execute();							
			}
		}
		currentTask = getFirstTask();
		
		while (i < hyperperiod){
			startNoPre = i;
			if(currentTask.getToExecute() > 0){
				startNoPre = i + 1;				
				while(currentTask.getToExecute() > 0 && i < hyperperiod){					
					result[currentTask.getId()][i] = true;
					currentTask.executed();						
					i++;					
				}				
			}else{				
				i++;
			}
			// execute the tasks missed during the no preemption
			for( int j = startNoPre; j < i; j ++){
				for(Task task: tasks){
					if(j % task.getPeriod() == 0){
						task.execute();							
					}
				}
			}
			currentTask = getFirstTask();
		}
		return result;
	}
	
	public  boolean[][] scheduleEDFPreemptive(){
		boolean[][] result = new boolean[tasks.size()][hyperperiod];		
		Task currentTask;
		calculateHyperperiod();	
		init() ;
		for (int i = 0; i < hyperperiod; i++) {
			for (Task task : tasks) {
				if (i % task.getPeriod() == 0) {
					task.execute();
				}				
			}
			calculatePrioritiesEDF(i);
			currentTask = getFirstTask();
			if (currentTask.getId() >= 0) {
				result[currentTask.getId()][i] = true;
				currentTask.executed();
			}

		}
		
		
		return result;
	}
	
	public  boolean[][] scheduleEDFNoPreemptive(){
		boolean[][] result = new boolean[tasks.size()][hyperperiod];
		calculateHyperperiod();
		Task currentTask;
		int i = 0;
		int startNoPre;
		init();
		calculatePrioritiesEDF(i);
		for(Task task: tasks){
			if(i % task.getPeriod() == 0){
				task.execute();							
			}
		}
		currentTask = getFirstTask();
		
		while (i < hyperperiod) {
			startNoPre = i;
			if(currentTask.getToExecute() > 0){
				startNoPre = i +1;				
				while(currentTask.getToExecute() > 0 && i < hyperperiod){					
					result[currentTask.getId()][i] = true;
					currentTask.executed();						
					i++;					
				}				
			}else{				
				i++;
			}
			// execute the tasks missed during the no preemption
			for( int j = startNoPre; j < i; j ++){
				for(Task task: tasks){
					if(j % task.getPeriod() == 0){
						task.execute();							
					}
				}
			}
			calculatePrioritiesEDF(i);
			currentTask = getFirstTask();
		}	
		
		return result;
	}
		
	public String toString(boolean[][] array) {
		String result = "";
		for (int i = 0; i < tasks.size(); i++) {
			result += "Task" + i + ": ";
			for (int j = 0; j < hyperperiod; j++) {
				if (array[i][j]) {
					result += "X";
				} else {
					result += "_";
				}
			}
			result += "\n";
		}
		return result;
	}
	
	public String toSVG(boolean[][] array){
		String result = "";
		String scale = "";
		int step = 25;
		int margeLeft = 20;
		int widthImage;		
		int yLine;
		int xScale;
		int yScale;
		int xRect;
		int yRect;
		int xStick;
		widthImage = 40 + hyperperiod;
		result = "<svg width=\"" + widthImage + "\"";
		result +=" height=\"" + tasks.size() * 20 + "\">\n";
		for (int i = 0; i < tasks.size(); i++) {
			yLine = 10 + i * 20;
			result += "<text x=\"0\" y=\"" + yLine +"\">";
			result += "T" + i + "</text>\n";
			for (int j = 0; j < hyperperiod; j++) {
				// add scale number
				if((j % step) == 0){
					xScale = margeLeft + j ;
					yScale = 15 + i * 20;	
					scale += "<text x=\"" + xScale +"\" ";
					scale += "y=\"" + yScale + "\" ";
					scale += "fill=\"black\" ";
					scale += "font-family=\"Verdana\" ";
					scale += "font-size=\"5\"";
					scale += ">";
					scale += j + "</text>";
				}
				// add rect element 
				if (array[i][j]) {
					xRect = margeLeft + j;
					yRect = 5 + i *20;
					result += "<rect x=\"" + xRect + "\" ";
					result += "y=\"" + yRect +"\" ";
					result += "width=\"1\" ";
					result += "height=\"5\" ";
					result += "style=\"fill:rgb(0,0,255);stroke-width:3\"/>\n";					
				}
				// add start element
				if((j % tasks.get(i).getPeriod()) == 0){
					xStick = margeLeft + j;
					yLine = 10 + i *20;
					result += "<line x1=\"" + xStick +"\" y1=\"" + yLine + "\" ";
					result += " x2=\"" + xStick + "\" y2=\"" + (yLine - 7) + "\" ";
					result += " style=\"stroke:rgb(0, 255, 0);stroke-width:1\"/>\n";
				}
				// add deadline element
				if((j % tasks.get(i).getPeriod()) == 
						tasks.get(i).getDeadline()){
					xStick = margeLeft + j;
					result += "<line x1=\"" + xStick +"\" y1=\"" + yLine + "\" ";
					result += " x2=\"" + xStick + "\" y2=\"" + (yLine - 7) + "\" ";
					result += " style=\"stroke:rgb(255, 0, 0);stroke-width:1\"/>\n";
				}				
			}
			if ((hyperperiod % step) == 0) {
				// last number
				xScale = margeLeft + hyperperiod;
				yScale = 15 + i * 20;
				scale += "<text x=\"" + xScale + "\" ";
				scale += "y=\"" + yScale + "\" ";
				scale += "fill=\"black\" ";
				scale += "font-family=\"Verdana\" ";
				scale += "font-size=\"5\"";
				scale += ">";
				scale += hyperperiod + "</text>";
			}
			result += "<line x1=\"" +  margeLeft+ "\" y1=\"" + yLine +"\" ";
			result += "x2=\""+ (hyperperiod + margeLeft) + "\" y2=\""+ yLine + "\" ";
			result += "style=\"stroke:rgb(0,0,0);stroke-width:1\"/>\n";	
			result += scale;
			
		}
		result += "</svg>";
		return result;
	}
	
	
}
