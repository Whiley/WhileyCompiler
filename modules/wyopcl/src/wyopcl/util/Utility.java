package wyopcl.util;

import static wycc.lang.SyntaxError.internalFailure;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.Code.Block;
import wyil.lang.Type.FunctionOrMethod;
import wyjc.runtime.WyList;
import wyjc.runtime.WyRat;

public final class Utility {

	private Utility(){
		//Utility class has the private constructor.
	}
	
	private static String constantToString(Constant.Map map, Class<?> paramType) {
		String r = "{";
		if (map.values.isEmpty()) {
			r = r + "=>";
		} else {
			boolean firstTime = true;
			ArrayList<String> keystr = new ArrayList<String>();
			HashMap<String, Constant> keymap = new HashMap<String, Constant>();
			for (Constant key : map.values.keySet()) {
				keystr.add(key.toString());
				keymap.put(key.toString(), key);
			}
			Collections.sort(keystr);
			for (String key : keystr) {
				if (!firstTime) {
					r += ", ";
				}
				firstTime = false;
				Constant k = keymap.get(key);
				r += k + "=>" + convertConstantToJavaObject(map.values.get(k), paramType);
			}
		}
		r += "}";
		return r.replaceAll("\"", "");
	}
	/**
	 * Returns a string of a sorted Constant.Set, using tree set.
	 * @param set the Constant Set 
	 * @param paramType the given type
	 * @return
	 */
	private static String constantToString(Constant.Set set, Class<?> paramType) {
		// Sort the elements in a set, using the tree set.
		TreeSet<Constant> sorted = new TreeSet<Constant>(set.values);
		String r = "{";
		boolean firstTime = true;
		for (Constant constant: sorted) {
			if (!firstTime) {
				r += ", ";
			}
			firstTime = false;
			if(constant instanceof Constant.Char){
				r += "'"+convertConstantToJavaObject(constant, paramType)+"'";
			}else{
				r += convertConstantToJavaObject(constant, paramType);
			}
			
		}
		r += "}";
		return r.replaceAll("\"", "");
	}

	private static String constantToString(Constant.List list, Class<?> paramType) {
		String r = "[";
		boolean firstTime = true;
		for (Constant elem : list.values) {
			if (!firstTime) {
				r += ", ";
			}
			firstTime = false;
			// Check if the elem is a list
			if (elem instanceof Constant.List || elem instanceof Constant.Record) {
				r += convertConstantToJavaObject(elem, paramType);
			} else {
				r += elem;
			}
		}
		r += "]";
		return r.replaceAll("\"", "");
	}

	private static String constantToString(Constant.Record record, Class<?> paramType) {
		String r = "{";
		boolean firstTime = true;
		ArrayList<String> keys = new ArrayList<String>(record.values.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			if (!firstTime) {
				r += ",";
			}
			firstTime = false;
			Constant next = record.values.get(key);
			if (next instanceof Constant.Record || next instanceof Constant.List) {
				// Convert the key record into a string
				r += key + ":" + convertConstantToJavaObject(next, paramType);
			} else {
				r += key + ":" + next;
			}

		}
		r += "}";
		return r;
		//return r.replaceAll("\"", "");
	}

	private static String constantToString(Constant.Tuple tuple, Class<?> paramType) {
		String r = "(";
		boolean firstTime = true;
		for (Constant constant : tuple.values) {
			if (!firstTime) {
				r += ",";
			}
			firstTime = false;
			r += convertConstantToJavaObject(constant, paramType);
		}
		r += ")";

		return r.replaceAll("\"", "");
	}

	/**
	 * Convert the Constant object to the Java Object of given type.
	 * 
	 * @param constant the Constant 
	 * @param paramType the given Java type
	 * @return the Java object
	 */
	@SuppressWarnings("unchecked")
	public static Object convertConstantToJavaObject(Constant constant, Class<?> paramType) {

		if (constant == null || constant instanceof Constant.Null) {
			return null;
		}
		
		if (constant instanceof Constant.Bool) {
			return ((Constant.Bool) constant).value;
		} 
		
		if (constant instanceof Constant.Char) {
			return ((Constant.Char) constant).value;
		}
		
		if (constant instanceof Constant.Strung) {
			//Add the double quotas to the return string.
			return ((Constant.Strung) constant).value;
		}
		
		if (constant instanceof Constant.Byte) {
			if (paramType.equals(BigInteger.class)) {
				return ((Constant.Byte) constant).value;
			} else if (paramType.equals(WyList.class)) {
				// Cast byte to char...too complicated....
				WyList wyList = new WyList();
				wyList.add(((Constant.Byte) constant).value);
				return wyList;
			} else {
				return ((Constant.Byte) constant).value;
			}
		}
		
		
		if (constant instanceof Constant.Decimal) {
			//Check if the returned type is WyRat
			if(paramType.equals(WyRat.class)){	
				return new WyRat(((Constant.Decimal)constant).value);
			}			
			return (Constant.Decimal) constant;			
		}
		
		if (constant instanceof DecimalFraction){
			//return the string of a DecimalFraction.
			return ((DecimalFraction)constant).toString();
		}
		
		if (constant instanceof Constant.Integer) {
			//Check if the returned type is WyRat
			if(paramType.equals(WyRat.class)){	
				return new WyRat(((Constant.Integer)constant).value);
			}			
			return ((Constant.Integer) constant).value;
		}
		
		if (constant instanceof Constant.List) {
			if (paramType.equals(WyList.class)) {
				WyList wylist = new WyList();
				for(Constant value: ((Constant.List) constant).values){
					wylist.add(((Constant.Byte)value).value);
				}
				return wylist;
			}else{
				return constantToString((Constant.List) constant, paramType);
			}
		}
		
		if (constant instanceof Constant.Map) {
			return constantToString((Constant.Map) constant, paramType);
		}
		
		if (constant instanceof Constant.Record) {
			return constantToString((Constant.Record) constant, paramType);
		}
		
		if (constant instanceof Constant.Set) {
			return constantToString((Constant.Set) constant, paramType);
		}		
		
		if (constant instanceof Constant.Tuple) {
			return constantToString((Constant.Tuple) constant, paramType);
		}
		
		internalFailure("Not implemented!", "Converter.java", null);
		return null;
	}

	public static Class<?> convertToClass(Type paramType) {
		if (paramType instanceof Type.Any) {
			return Object.class;
		} else if (paramType instanceof Type.Strung) {
			return String.class;
		} else if (paramType instanceof Type.Int) {
			return Integer.class;
		} else {
			internalFailure("Not implemented!", "Converter.java", null);
		}
		return null;

	}

	/**
	 * Copy and return the Constant object
	 * 
	 * @param constant
	 * @return
	 */
	public static Constant copyConstant(Constant constant) {
		if(constant == null || constant instanceof Constant.Null){
			return Constant.V_NULL;
		}
		
		if (constant instanceof Constant.Integer) {
			return Constant.V_INTEGER(((Constant.Integer) constant).value);
		} else if (constant instanceof Constant.List) {			
			ArrayList<Constant> values = ((Constant.List) constant).values;			
			return Constant.V_LIST(new ArrayList<Constant>(values));
		} else if (constant instanceof Constant.Record) {
			return Constant.V_RECORD(((Constant.Record) constant).values);
		} else if (constant instanceof Constant.Strung) {
			return Constant.V_STRING(((Constant.Strung) constant).value);
		} else if (constant instanceof Constant.Set) {
			return Constant.V_SET(((Constant.Set) constant).values);
		} else if (constant instanceof Constant.Type) {
			return Constant.V_TYPE(((Constant.Type) constant).type);
		} else if (constant instanceof Constant.Bool) {
			return Constant.V_BOOL(((Constant.Bool) constant).value);
		} else if (constant instanceof Constant.Map) {
			return Constant.V_MAP(((Constant.Map) constant).values);
		} else if (constant instanceof Constant.Byte) {
			return Constant.V_BYTE(((Constant.Byte) constant).value);
		} else if (constant instanceof Constant.Char) {
			return Constant.V_CHAR(((Constant.Char) constant).value);
		} else if (constant instanceof Constant.Decimal) {
			return Constant.V_DECIMAL(((Constant.Decimal) constant).value);
		} else if (constant instanceof Constant.Tuple) {
			return Constant.V_TUPLE(((Constant.Tuple) constant).values);
		} else if (constant instanceof Closure){
			Closure closure = (Closure)constant;
			return Closure.V_Closure(closure.lambda, closure.params, closure.type);
		} else {
			internalFailure("Not implemented!", "Utility.java", null);
			return null;
		}
	}
	
	/**
	 * Invokes the function 
	 * @param name
	 * @param return_reg
	 * @return
	 */
	public static StackFrame invokeFunction(Block blk, int depth, String name, List<Constant> params, int return_reg){
		StackFrame stackframe = new StackFrame(depth+1, blk, 0, name, return_reg);
		
		//Pass the input parameters.
		int index = 0;
		for(Constant param: params){
			//Constant constant = stackframe.getRegister(operand);
			stackframe.setRegister(index, param);
			index++;
		}
		
		return stackframe;
	}
	
	
	
	/**
	 * Invokes Java print or println method.
	 * @param name the nae of called function
	 * @param paramTypes the list of input parameter types
	 * @param values the list of input parameter values
	 */
	public static void invokeJavaPrintFunction(String name, ArrayList<Type> paramTypes, List<Constant> values){
		java.lang.reflect.Method method = null;
		try {
			Class<?> systemClass = Class.forName("java.lang.System");
			Field outField = systemClass.getDeclaredField("out");
			Class<?> printClass = outField.getType();
			Class<?>[] parameterTypes = new Class[paramTypes.size()];
			// Iterate the parameter types
			for (int i = 0; i < paramTypes.size(); i++) {
				parameterTypes[i] = convertToClass(paramTypes.get(i));
			}
			method = printClass.getMethod(name, parameterTypes);
		
			ArrayList<Object> arguments = new ArrayList<Object>();
			int index = 0;				
			for (Class<?> paramType : method.getParameterTypes()) {
				arguments.add(convertConstantToJavaObject(values.get(index), paramType));
			}
			method.invoke(outField.get(null), arguments.toArray());

		} catch (Exception e) {
			throw new RuntimeException("Error occurs on invoking the Java functions");
		}		
	}
	/**
	 * Invokes the Whiley runtime function
	 * @param classLoader the activated class
	 * @param module_name the package name
	 * @param func_name the function name
	 * @param paramTypes the list of parameter types
	 * @param values the list of parameter values.
	 * @return the return Java object
	 */
	public static Object invokeWhileyRuntimeFucntion(ClassLoader classLoader, String module_name, String func_name,
													 ArrayList<Type> paramTypes, List<Constant> values){
		try {			
			//Load the Class
			//ClassLoader classLoader = this.getClass().getClassLoader();
			Class<?> whileyclass = Class.forName(module_name, true, classLoader);			
			for(Method method: whileyclass.getMethods()){
				//Find the method by checking the method name.
				if(method.getName().startsWith(func_name)){
					//Get the parameter types.
					Object[] params = new Object[paramTypes.size()];
					//Compare the parameter type
					int index = 0;
					for(Class<?> paramType : method.getParameterTypes()){
						//The 'paramType' is Java data type.				    		
						//Thus, we need a conversion from Constant to Java
						Constant operand = values.get(index);
						params[index] = convertConstantToJavaObject(operand, paramType);
						index++;
					}
					
					return method.invoke(null, params);				
				}
			}			
		} catch (Exception e) {				
			//Pop up the current block
			throw new RuntimeException("Error occurs on invoking the Whiley functions");			
		}
		return null;
	}
	

}
