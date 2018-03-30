
public class Task{
	private int id;
	private int wcet;	
	private int deadline;
	private int period;
	
	// priority increase (2 > 1)
	private int priority;
	private int toExecute;
	
	
	public Task( int wcet, int deadline, int period) {
		this.id = -1;
		this.wcet = wcet;
		this.deadline = deadline;
		this.period = period;
		this.toExecute = 0;
		this.priority = -1;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPeriod() {
		return period;
	}

	public int getDeadline() {
		return deadline;
	}

	public int getWcet() {
		return wcet;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	
	
	public int getToExecute() {
		return toExecute;
	}
	
	public void executed(){
		toExecute --;
	}
	
	public void execute(){
		toExecute += wcet;
	}
	
	public void done(){
		toExecute = 0;
	}


	
	
}
