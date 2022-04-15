import java.io.File;
import java.util.LinkedList;

public class Program {
	private int id;
	private File code;
	private State state;
	private int nextInstruction;
	private LinkedList<Variable<?>> vars;
	
	public Program(int id, String codePath) {
		this.id = id;
		this.code = new File(codePath);
		this.state = State.READY;
		this.nextInstruction = 0;
		this.vars = new LinkedList<>();
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setState(State newState) {
		this.state = newState;
	}
	
	public State getState() {
		return this.state;
	}
	
	public void block() {
		this.state = State.BLOCKED;
	}
	
	public void release() {
		this.state = State.READY;
	}
	
	public void execute() {
		// TODO complete method
	}
	
	public Object getVariable(String varIdentifier){
		for(Variable<?> var : vars) {
			if(varIdentifier == var.getIdentifier()) return var.getValue();
		}
		return null; // TODO Throw error
	}
	
	public void addVariable(Variable<?> var) {
		vars.add(var);
	}
}
