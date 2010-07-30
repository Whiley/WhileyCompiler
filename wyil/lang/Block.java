package wyil.lang;

import java.util.*;

public final class Block extends ArrayList<Code> {
	public Block() {
		super();
	}
	public Block(Collection<Code> codes) {
		super(codes);
	}
	public Block(Code... codes) {
		super(Arrays.asList(codes));
	}
}
