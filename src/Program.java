import java.io.File;
import java.util.HashMap;

public class Program {
	private int id;
	private File code;
	private State state;
	private int nextInstruction;
	private HashMap<String, String> vars;
	
	public Program(int id, String codePath) {
		this.id = id;
		this.code = new File(codePath);
		this.state = State.READY;
		this.nextInstruction = 0;
		this.vars = new HashMap<>();
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
	
	public String getVariable(String requestedVarIdentifier){
		return vars.get(requestedVarIdentifier);
	}
	
	public void addVariable(String identifier, String value) {
		vars.put(identifier, value);
	}
}
