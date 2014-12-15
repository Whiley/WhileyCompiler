package wyopcl.util;

import java.util.Arrays;
import java.util.HashMap;

import wyil.lang.Code.Block;
import wyil.lang.Constant;

public class StackFrame {
	private final Block block;
	private String name;
	private int line;				
	private int return_reg;	
	private HashMap<String, Integer> loop_index = new HashMap<String, Integer>();
	private int depth;
	private Constant[] registers  = new Constant[0];
	
	public StackFrame(int depth, Block block, int line,
			String name, int return_reg){
		this.depth = depth;
		this.block = block;
		this.name = name;
		this.line = line;			
		this.return_reg = return_reg;
	}

	public int getLine() {
		return line;
	}
	
	public void setLine(int line){
		this.line = line;
	}		
	
	public Block getBlock(){
		return block;
	}
	
	public void setReturn_reg(int return_reg) {
		this.return_reg = return_reg;
	}
	
	public int getReturn_reg() {
		return return_reg;
	}				
	
	public String getName() {
		return name;
	}
	
	public Constant getRegister(int reg){
		if(reg>=registers.length || reg < 0){
			return null;
		}
		
		return registers[reg];
	}
	
	public void setRegister(int reg, Constant constant){
		if(reg>=registers.length){
			//Expand the array.
			registers = Arrays.copyOf(registers, reg+1);
		}
		
		registers[reg] = constant;
	}
	
	
	public int getRegisterLength(){
		return registers.length;
	}

	@Override
	public String toString() {			
		return "Block:"+block.hashCode()+" Line:"+line;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getLoop_index(String label) {
		if(this.loop_index.containsKey(label)){
			return this.loop_index.get(label);
		}else{
			//If the loop index is not found, then return -1.
			this.loop_index.put(label, -1);
			return -1;
		}
	}

	public void setLoop_index(String label, int loop_index) {
		this.loop_index.put(label, loop_index);
	}
			
}