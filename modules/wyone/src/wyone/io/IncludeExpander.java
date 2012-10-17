package wyone.io;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import wyone.core.*;

public class IncludeExpander {
	
	public void expand(SpecFile file) throws IOException {
		List<SpecFile.Decl> file_declarations = file.declarations;
		HashSet<String> included = new HashSet<String>();
		
		for(int i=0;i!=file_declarations.size();) {
			SpecFile.Decl d = file_declarations.get(i);
			if(d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl inc = (SpecFile.IncludeDecl) d;
				file_declarations.remove(i);
				if(!included.contains(inc.filename)) {
					SpecLexer lexer = new SpecLexer(inc.filename);
					SpecParser parser = new SpecParser(inc.filename, lexer.scan());
					SpecFile sf = parser.parse();
					file_declarations.addAll(i,sf.declarations);
					included.add(inc.filename);
				}
			} else {
				i = i + 1;
			}
		}
	}
}
