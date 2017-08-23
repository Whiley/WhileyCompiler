package wyil.util.interpreter;

import wybs.util.AbstractCompilationUnit.Identifier;
import wyil.util.interpreter.Interpreter.CallStack;

public abstract class LValue {
	abstract public RValue read(CallStack frame);
	abstract public void write(CallStack frame, RValue rhs);

	public static final class Variable extends LValue {
		private final Identifier name;

		public Variable(Identifier name) {
			this.name = name;
		}

		@Override
		public RValue read(CallStack frame) {
			return frame.getLocal(name);
		}

		@Override
		public void write(CallStack frame, RValue rhs) {
			frame.putLocal(name, rhs);
		}
	}

	public static class Array extends LValue {
		private final LValue src;
		private final RValue.Int index;

		public Array(LValue src, RValue.Int index) {
			this.src = src;
			this.index = index;
		}

		@Override
		public RValue read(CallStack frame) {
			RValue.Array src = Interpreter.checkType(this.src.read(frame), null, RValue.Array.class);
			return src.read(index);
		}

		@Override
		public void write(CallStack frame, RValue value) {
			RValue.Array arr = Interpreter.checkType(this.src.read(frame), null, RValue.Array.class);
			src.write(frame, arr.write(index, value));
		}
	}

	public static class Record extends LValue {
		private final LValue src;
		private final Identifier field;

		public Record(LValue src, Identifier field) {
			this.src = src;
			this.field = field;
		}

		@Override
		public RValue read(CallStack frame) {
			RValue.Record src = Interpreter.checkType(this.src.read(frame), null, RValue.Record.class);
			return src.read(field);
		}

		@Override
		public void write(CallStack frame, RValue value) {
			RValue.Record rec = Interpreter.checkType(this.src.read(frame), null, RValue.Record.class);
			src.write(frame, rec.write(field, value));
		}
	}

	public static class Dereference extends LValue {
		private final LValue src;

		public Dereference(LValue src) {
			this.src = src;
		}

		@Override
		public RValue read(CallStack frame) {
			RValue.Cell cell = Interpreter.checkType(src.read(frame), null, RValue.Cell.class);
			return cell.read();
		}

		@Override
		public void write(CallStack frame, RValue rhs) {
			RValue.Cell cell = Interpreter.checkType(src.read(frame), null, RValue.Cell.class);
			cell.write(rhs);
		}
	}
}