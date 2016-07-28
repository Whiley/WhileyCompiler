package wyjc.util;

import static jasm.lang.JvmTypes.JAVA_LANG_OBJECT;
import static jasm.lang.Modifier.*;

import static wyjc.Wyil2JavaBuilder.JAVA_LANG_OBJECT_ARRAY;
import static wyjc.Wyil2JavaBuilder.WHILEYLAMBDA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jasm.attributes.Code.Handler;
import jasm.lang.Bytecode;
import jasm.lang.ClassFile;
import jasm.lang.JvmType;
import jasm.lang.JvmTypes;
import jasm.lang.Modifier;
import wyjc.Wyil2JavaBuilder;

/**
 * This is a template for generating a "lambda class", which is used to
 * represent lambda's in Whiley. The basic form of the generated class is:
 * 
 * <pre>
 * final class NN extends WyLambda {
 *    private final T1 r1;
 *    ...
 *    private final Tn rn;
 *    
 *    public NN(T1 r1, ... Tn rn) {
 *       super();
 *       this.r1 = r1;
 *       ...
 *       this.rn = rn;
 *    }
 *    
 *    public Object call(Object... params) {
 *      return C.m(params[0], ... params[m], r1, ..., rn);
 *    }
 * }
 * </pre>
 * 
 * Here, the fields <code>r1</code> to <code>rn</code> correspond to the
 * "environment". Those are variables from the enclosing scope which are passed
 * through to the lambda body. In this case, the body always consists of a
 * static method call to a given method.
 * 
 * @author David J. Pearce
 *
 */
public class LambdaTemplate {

	/**
	 * JVM Class version number to use (e.g. 49)
	 */
	private final int version;
	
	/**
	 * The class that this template will generate
	 */
	private final JvmType.Clazz thisClass;	
	
	/**
	 * The enclosing class of the target method. That is the static method which
	 * will be invoked by this lambda.
	 */
	private final JvmType.Clazz targetClass;
	
	/**
	 * The name of the target method.  That is the static method which
	 * will be invoked by this lambda.
	 */ 
	private final String targetMethod;
	
	/**
	 * The type of the target function. This excludes the "environment" which
	 * will extend the set of concrete parameter types.
	 */
	private final JvmType.Function type;
	
	/**
	 * The set of environment variables which will be stored as fields
	 */
	private final JvmType[] environment;
	
	/**
	 * Create a given LambdaTemplate with which a special lambda class can be
	 * constructed.
	 * 
	 * @param version
	 *            JVM Class version number to use (e.g. 49)
	 * @param thisClass
	 *            The class that this template will generate
	 * @param targetClass
	 *            The enclosing class of the target method. That is the static
	 *            method which will be invoked by this lambda.
	 * @param targetMethod
	 *            The name of the target method. That is the static method which
	 *            will be invoked by this lambda.
	 * @param type
	 *            The type of the target function. This excludes the
	 *            "environment" which will extend the set of concrete parameter
	 *            types.
	 * @param environment
	 *            The set of environment variables which will be stored as
	 *            fields
	 */
	public LambdaTemplate(int version, JvmType.Clazz thisClass, JvmType.Clazz targetClass, String targetMethod,
			JvmType.Function type, JvmType... environment) {
		this.version = version;
		this.thisClass = thisClass;
		this.targetClass = targetClass;
		this.targetMethod = targetMethod;
		this.type = type;
		this.environment = environment;
	}
	
	/**
	 * Generate the lambda class.
	 * 
	 * @return
	 */
	public ClassFile generateClass() {
		// Construct empty class
		List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_FINAL);
		ClassFile cf = new ClassFile(version, thisClass, WHILEYLAMBDA, new ArrayList<JvmType.Clazz>(),
				modifiers);
		// Add Fields
		for(int i=0;i!=environment.length;++i) {
			String name = "r" + i;
			cf.fields().add(generateField(name,environment[i]));
		}		
		// Add Constructor
		cf.methods().add(generateConstructor());
		// Add implementation of WyLambda.call(Object[]) ===
		cf.methods().add(generateCallMethod());
		// Done
		return cf;
	}
	

	/**
	 * Build a constructor for a lambda expression. This constructor accepts
	 * zero or more parameters which constitute the "environment". That is,
	 * variables from the enclosing scope of the lambda which will be passed
	 * through and stored in the lambda class itself.
	 * 
	 * @return
	 */
	private ClassFile.Method generateConstructor() {
		List<Modifier> modifiers = modifiers(ACC_PUBLIC);
		JvmType.Function superConstructorType = new JvmType.Function(JvmTypes.T_VOID);
		JvmType.Function constructorType = new JvmType.Function(JvmTypes.T_VOID, environment);
		// Create constructor method
		ClassFile.Method constructor = new ClassFile.Method("<init>", constructorType, modifiers);
		// Create body of constructor which called super-class constructor, and
		// assigns each environment parameter to a corresponding field.
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		// Call superclass constructor
		bytecodes.add(new Bytecode.Load(0, thisClass));
		bytecodes.add(new Bytecode.Invoke(Wyil2JavaBuilder.WHILEYLAMBDA, "<init>", superConstructorType,
				Bytecode.InvokeMode.SPECIAL));
		// Assign each parameter to a corresponding named field.
		for (int i = 0; i != environment.length; ++i) {
			JvmType fieldType = environment[i];
			bytecodes.add(new Bytecode.Load(0, thisClass));
			bytecodes.add(new Bytecode.Load(i + 1, fieldType));
			bytecodes.add(new Bytecode.PutField(thisClass, "r" + i, fieldType, Bytecode.FieldMode.NONSTATIC));
		}
		bytecodes.add(new Bytecode.Return(null));
		// Add code attribute to constructor
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes, new ArrayList<Handler>(), constructor);
		constructor.attributes().add(code);
		//
		return constructor;
	}
	

	/**
	 * Build a method implementing the body of this lambda. In this case, the
	 * body consists of invoking a given method in the target class. The
	 * parameters for the invocation are formed from two sources. First, the
	 * parameters which are passed in to the call itself as an object array;
	 * Second, the parameters originally passed to the constructor and now
	 * stored as fields.
	 * 
	 * @param lambdaClassType
	 * @return
	 */
	private ClassFile.Method generateCallMethod() {
		List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_FINAL);		
		// Create constructor method
		JvmType.Function callType = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT_ARRAY);
		ClassFile.Method method = new ClassFile.Method("call", callType, modifiers);
		// Create body of call method
		List<Bytecode> bytecodes = new ArrayList<Bytecode>();
		// Load parameters onto stack from the parameters array
		decodeLambdaParameterArray(1,type.parameterTypes(),bytecodes);
		// Load environment onto stack
		loadEnvironment(bytecodes);
		// Construct the actual target type
		ArrayList<JvmType> actualParameterTypes = new ArrayList<JvmType>(type.parameterTypes());
		actualParameterTypes.addAll(Arrays.asList(environment));
		JvmType.Function actualType = new JvmType.Function(type.returnType(), actualParameterTypes);
		// Now, invoke the target method
		bytecodes.add(new Bytecode.Invoke(targetClass, targetMethod, actualType, Bytecode.InvokeMode.STATIC));
		// Deal with return values (if applicable)
		if (type.returnType() instanceof JvmType.Void) {
			// Called function doesn't return anything, but we have to as the
			// type of WyLambda.call dictates this. Therefore, push on dummy
			// null value.
			bytecodes.add(new Bytecode.LoadConst(null));
		} 
		bytecodes.add(new Bytecode.Return(JAVA_LANG_OBJECT));		
		// Add code attribute to call method
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes, new ArrayList<Handler>(), method);
		method.attributes().add(code);
		// Done
		return method;
	}
	
	/**
	 * Generate a field in which the given environment variable can be stored.
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	private ClassFile.Field generateField(String name, JvmType type) {
		List<Modifier> modifiers = modifiers(ACC_PRIVATE, ACC_FINAL);
		return new ClassFile.Field(name, type, modifiers);
	}
	
	/**
	 * Given an array of objects, load each element onto the stack in order of
	 * occurrence and convert them into the appropriate form.
	 * 
	 * @param source
	 *            Source register containing the array
	 * @param types
	 *            the target type of each element
	 * @param bytecodes
	 */
	private void decodeLambdaParameterArray(int source, List<JvmType> types, List<Bytecode> bytecodes) {
		for (int i = 0; i != types.size(); ++i) {
			bytecodes.add(new Bytecode.Load(source, JAVA_LANG_OBJECT_ARRAY));
			bytecodes.add(new Bytecode.LoadConst(i));
			bytecodes.add(new Bytecode.ArrayLoad(JAVA_LANG_OBJECT_ARRAY));
			bytecodes.add(new Bytecode.CheckCast(types.get(i)));
		}
	}
	
	/**
	 * Load each environment variable onto the stack
	 * 
	 * @param bytecodes
	 */
	private void loadEnvironment(List<Bytecode> bytecodes) {
		for (int i = 0; i != environment.length; ++i) {
			String name = "r" + i;
			bytecodes.add(new Bytecode.Load(0,thisClass));
			bytecodes.add(new Bytecode.GetField(thisClass, name, environment[i], Bytecode.FieldMode.NONSTATIC));
		}
	}
	
	/**
	 * Construct a list of modifiers from an array of (potentially null)
	 * modifiers.
	 * 
	 * @param mods
	 * @return
	 */
	private static List<Modifier> modifiers(Modifier... mods) {
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		for (Modifier m : mods) {
			if (m != null) {
				modifiers.add(m);
			}
		}
		return modifiers;
	}
}
