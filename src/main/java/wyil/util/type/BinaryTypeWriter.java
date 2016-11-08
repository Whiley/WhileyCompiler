package wyil.util.type;

import java.io.IOException;

import wyautl_old.io.BinaryAutomataWriter;
import wyautl_old.lang.Automaton;
import wybs.lang.NameID;
import wyfs.io.BinaryOutputStream;
import wyil.lang.Type;
import wyil.util.TypeSystem;

/**
 * <p>
 * A <code>BinaryWriter</code> will write types to a binary output stream.
 * The types should be read back from the stream using
 * <code>BinaryReader</code> .
 * </p>
 * <p>
 * <b>NOTE:</b> Under-the-hood, this class is essentially a wrapper for
 * <code>BinaryAutomataWriter</code>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class BinaryTypeWriter extends BinaryAutomataWriter {
	public BinaryTypeWriter(BinaryOutputStream writer) {
		super(writer);
	}
	@Override
	public void write(Automaton.State state) throws IOException {
		super.write(state);
		if (state.kind == TypeSystem.K_NOMINAL) {
			NameID name = (NameID) state.data;
			writeString(name.module().toString());
			writeString(name.name());
		} else if(state.kind == TypeSystem.K_RECORD) {
			TypeSystem.RecordState fields = (TypeSystem.RecordState) state.data;
			writer.write_bit(fields.isOpen);
			writer.write_uv(fields.size());
			for(String field : fields) {
				writeString(field);
			}
		} else if(state.kind == TypeSystem.K_REFERENCE) {
			if(state.data != null) {
				writeString((String)state.data);
			} else {
				writeString("");
			}
		} else if(state.kind == TypeSystem.K_FUNCTION) {
			TypeSystem.FunctionOrMethodState fms = (TypeSystem.FunctionOrMethodState) state.data;
			writer.write_uv(fms.numParams);
		} else if(state.kind == TypeSystem.K_METHOD) {
			TypeSystem.FunctionOrMethodState fms = (TypeSystem.FunctionOrMethodState) state.data;
			// FIXME: this is really a hack, but it's fine for now. This whole
			// method needs to be replaced!!
			writer.write_uv(fms.numParams);
			writer.write_uv(fms.contextLifetimes.size());
			for(String s : fms.contextLifetimes) {
				writeString(s);
			}
			writer.write_uv(fms.lifetimeParameters.size());
			for(String s : fms.lifetimeParameters) {
				writeString(s);
			}
		}
	}

	private void writeString(String str) throws IOException {
		writer.write_uv(str.length());
		for (int i = 0; i != str.length(); ++i) {
			writer.write_u16(str.charAt(i));
		}
	}
}