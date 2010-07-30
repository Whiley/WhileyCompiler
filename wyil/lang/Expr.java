package wyil.lang;

public interface Expr {
	public static class Variable implements Expr {
		public final String name;
		public Variable(String name) {
			this.name = name;
		}
		public int hashCode() {
			return name.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Variable) {
				return name.equals(((Variable)o).name);
			}
			return false;
		}
		public String toString() {
			return name;
		}
	}
}
