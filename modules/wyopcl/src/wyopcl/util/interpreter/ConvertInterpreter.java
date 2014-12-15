package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyopcl.util.DecimalFraction;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Convert</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Convert
 *
 */
public class ConvertInterpreter extends Interpreter {
	private static ConvertInterpreter instance;
	private ConvertInterpreter() {	}

	/* Implement the Singleton pattern to ensure this class has one instance. */
	public static ConvertInterpreter getInstance() {
		if (instance == null) {
			instance = new ConvertInterpreter();
		}
		return instance;
	}

	private Constant.List toConstantList(Constant constant, Type fromType, Type.List toType) {
		// No needs of casting for UnionOfList type.
		if (fromType instanceof Type.UnionOfLists) {
			return (Constant.List) constant;
		}

		if (fromType instanceof Type.List) {
			Type fromElemType = ((Type.List) fromType).element();
			Type toElemType = ((Type.List) toType).element();

			if (toElemType.equals(fromElemType)) {
				return (Constant.List) constant;// No casting
			}

			if (fromElemType instanceof Type.Void && toElemType instanceof Type.List) {
				ArrayList<Constant> values = new ArrayList<Constant>();
				Constant.List inner_list = Constant.V_LIST(new ArrayList<Constant>());
				values.add(inner_list);				
				Constant.List v_LIST = Constant.V_LIST(values);
				return v_LIST;// Return an empty and nested list.
			}

			if (fromElemType instanceof Type.Void) {
				// Return an empty list.
				return Constant.V_LIST(new ArrayList<Constant>());
			}			

			//Convert each element into the given type. 
			List<Constant> values = new ArrayList<Constant>();
			Iterator<Constant> iterator = ((Constant.List) constant).values.iterator();
			while (iterator.hasNext()) {
				Constant value = iterator.next();
				values.add(castConstanttoConstant(value, fromElemType, toElemType));
			}
			return Constant.V_LIST(values);
		} 

		if (fromType instanceof Type.Strung) {
			// Cast Constant.Strung to Constant.List
			List<Constant> values = new ArrayList<Constant>();
			char[] charArray = ((Constant.Strung) constant).value.toCharArray();
			for (char c : charArray) {
				// Cast char to int.
				values.add(Constant.V_INTEGER(BigInteger.valueOf((int) c)));
			}
			return Constant.V_LIST(values);
		}

		internalFailure("Not implemented!", "ConvertInterpreter.java", null);
		return null;		

	}


	/**
	 * Converts a constant to a Constant.Record
	 * @param constant 
	 * @param fromType
	 * @param toType the assigned type of Constant.Record
	 * @return 
	 */
	private Constant.Record toConstantRecord(Constant constant, Type fromType, Type.Record toType) {
		Constant.Record record = (Constant.Record) constant;
		Type fromKeyType, toKeyType = null;
		HashMap<String, Type> fromFieldTypes = null;
		if (fromType instanceof Type.Record) {
			fromFieldTypes = ((Type.Record) fromType).fields();
		} else if (fromType instanceof Type.UnionOfRecords) {
			fromFieldTypes = ((Type.UnionOfRecords) fromType).fields();
		}
		Map<String, Constant> map = new HashMap<String, Constant>();
		HashMap<String, Type> toElemTypeValues = toType.fields();	
		for (Entry<String, Constant> entry:record.values.entrySet() ) {
			Constant value = entry.getValue();
			String key = entry.getKey();
			fromKeyType = fromFieldTypes.get(key);
			toKeyType = toElemTypeValues.get(key);
			map.put(key, castConstanttoConstant(value, fromKeyType, toKeyType));
		}

		return Constant.V_RECORD(map);

	}

	private Constant.Char toConstantChar(Constant constant, Type fromType, Type.Char toType) {
		if (fromType instanceof Type.Int) {
			return Constant.V_CHAR((char) (((Constant.Integer) constant).value.intValue()));
		} else {
			internalFailure("Not implemented!", "ConvertInterpreter.java", null);
			return null;
		}

	}

	/**
	 * Convert a Constant object to a Constant.Map
	 * 
	 * @param constant
	 * @param fromType
	 * @param toType
	 * @return
	 */
	private Constant.Map toConstantMap(Constant constant, Type fromType, Type.Map toType) {
		if (fromType == toType) {
			// Directly cast it into a Map.
			return (Constant.Map) constant;
		}
		Type fromKeyType = null, fromValueType = null;
		if (fromType instanceof Type.Map) {
			fromKeyType = ((Type.Map) fromType).key();
			fromValueType = ((Type.Map) fromType).value();
		} else if (fromType instanceof Type.UnionOfMaps) {
			fromKeyType = ((Type.UnionOfMaps) fromType).key();
			fromValueType = ((Type.UnionOfMaps) fromType).value();
		}
		Type toKeyType = ((Type.Map) toType).key();
		Type toValueType = ((Type.Map) toType).value();

		LinkedHashMap<Constant, Constant> convertedMap = new LinkedHashMap<Constant, Constant>();
		if (constant instanceof Constant.Map) {
			Constant.Map map = (Constant.Map) constant;
			if (fromKeyType == null && fromValueType == null && constant instanceof Constant.Map) {
				return map;// requires no type casting.
			}
			// Do the type casting
			Iterator<Entry<Constant, Constant>> iterator = map.values.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Constant, Constant> entry = iterator.next();
				// Convert the tuple type
				Constant key = castConstanttoConstant(entry.getKey(), fromKeyType, toKeyType);
				Constant value = castConstanttoConstant(entry.getValue(), fromValueType, toValueType);
				convertedMap.put(key, value);
			}

		} else if (constant instanceof Constant.List) {
			//Cast a Constant.List to a Constant.Map, whose key is the index starting from 0.
			Constant.List list = (Constant.List) constant;
			Iterator<Constant> iterator = list.values.iterator();
			BigInteger index = BigInteger.ZERO;
			while(iterator.hasNext()){
				Constant elem = iterator.next();
				Constant key = Constant.V_INTEGER(index);
				Constant value = elem;
				convertedMap.put(key, value);
				index = index.add(BigInteger.ONE);
			}
		} else {
			internalFailure("Not implemented!", "ConvertInterpreter.java", null);
			return null;
		}

		return Constant.V_MAP(convertedMap);

	}

	private Constant.Set toConstantSet(Constant constant, Type fromType, Type.Set toType) {
		Type fromElemType = null;
		HashSet<Constant> values = new HashSet<Constant>();
		Iterator<Constant> iterator = null;

		if (fromType instanceof Type.Set) {
			fromElemType = ((Type.Set) fromType).element();
		} else if (fromType instanceof Type.List) {
			fromElemType = ((Type.List) fromType).element();
		} else if (fromType instanceof Type.UnionOfCollections) {
			fromElemType = ((Type.UnionOfCollections) fromType).element();
		} else {
			internalFailure("Not implemented!", "ConvertInterpreter.java", null);
		}

		Type toElemType = ((Type.Set) toType).element();
		if (constant instanceof Constant.Set) {
			iterator = ((Constant.Set) constant).values.iterator();
		} else if (constant instanceof Constant.List) {
			iterator = ((Constant.List) constant).values.iterator();
		} else if (constant instanceof Constant.Strung) {
			Constant.Strung strung = (Constant.Strung) constant;
			char[] charArray = strung.value.toCharArray();
			for (char ch : charArray) {
				values.add(castConstanttoConstant(Constant.V_CHAR(ch), fromElemType, toElemType));
			}
			return Constant.V_SET(values);
		} else {
			internalFailure("Not implemented!", "ConvertInterpreter.java", null);
		}

		while (iterator.hasNext()) {
			Constant next = iterator.next();
			// Cast the elements in the from set.
			values.add(castConstanttoConstant(next, fromElemType, toElemType));
		}

		return Constant.V_SET(values);
	}
	/**
	 * Casts a Constant to Constant.Integer
	 * @param constant
	 * @param toType Type.Int
	 * @return
	 */
	private Constant.Integer toConstantInt(Constant constant, Type.Int toType) {

		try{
			if (constant instanceof Constant.Integer) {
				return (Constant.Integer) constant;
			}
			if (constant instanceof Constant.Decimal) {
				Constant.Decimal decimal = (Constant.Decimal) constant;
				// Cast a decimal to an integer
				return Constant.V_INTEGER(decimal.value.toBigInteger());
			} 
			if (constant instanceof Constant.List) {
				Constant.List list = (Constant.List) constant;
				return (Constant.Integer) list.values.get(0);
			} 
			if (constant instanceof Constant.Char) {
				// Cast Char to int
				return Constant.V_INTEGER(BigInteger.valueOf((int) (((Constant.Char) constant).value)));
			} 
			if (constant instanceof Constant.Record){				
				//Get the field types.
				Constant.Record record = (Constant.Record)constant;
				for(Entry<String, Constant> entry:record.values.entrySet()){
					Constant value = entry.getValue();
					Constant result = toConstantInt(value, toType);
					if(result != null){
						return (Constant.Integer) result;
					}
				}	

			}
			if(constant instanceof Constant.Bool){				
				Constant.Bool b = (Constant.Bool)constant;
				//If b == true, then return 1.
				if(b.value){
					return Constant.V_INTEGER(BigInteger.ONE);
				}else{
					return Constant.V_INTEGER(BigInteger.ZERO);
				}

			}


			internalFailure("Not implemented!", "ConvertInterpreter.java", null);

		}catch(ClassCastException ex){
			return null;
		}catch(Exception ex){
			internalFailure("Not implemented!", "ConvertInterpreter.java", null);
		}

		return null;
	}

	private Constant.Decimal toConstantDecimal(Constant constant, Type fromType, Type.Real toType) {
		if (fromType instanceof Type.Int) {
			return Constant.V_DECIMAL(new BigDecimal(((Constant.Integer) constant).value.toString()));
		} else if (fromType instanceof Type.Real) {
			return Constant.V_DECIMAL(((Constant.Decimal) constant).value);
		} else if (fromType instanceof Type.Union) {
			if (constant instanceof Constant.Integer) {
				return Constant.V_DECIMAL(BigDecimal.valueOf(((Constant.Integer) constant).value.longValue()));
			} else if (constant instanceof Constant.Char) {
				char value = ((Constant.Char) constant).value;
				return Constant.V_DECIMAL(BigDecimal.valueOf((int) value));
			} else {
				return (Constant.Decimal) constant;
			}
		} 
		internalFailure("Not implemented!", "ConvertInterpreter.java", null);
		return null;

	}

	private Constant toConstantNegation(Constant constant, Type fromType, Type.Negation toType) {
		if (fromType instanceof Type.Int) {
			Constant.Integer integer = (Constant.Integer) constant;
			return Constant.V_INTEGER(new BigInteger(integer.value.toString()));
		} else if (fromType instanceof Type.Real) {
			internalFailure("Not implemented!", "ConvertInterpreter.java", null);
			return null;
		} else if (fromType instanceof Type.Strung) {
			Constant.Strung strung = (Constant.Strung) constant;
			return strung;
		} 
		internalFailure("Not implemented!", "ConvertInterpreter.java", null);
		return null;

	}

	private Constant.Tuple toConstantTuple(Constant constant, Type fromType, Type.Tuple toType) {
		Constant.Tuple tuple = (Constant.Tuple) constant;
		ArrayList<Constant> values = new ArrayList<Constant>();
		int index = 0;
		for (Constant value : tuple.values) {
			if (fromType instanceof Type.Tuple) {
				values.add(castConstanttoConstant(value, ((Type.Tuple) fromType).element(index),
						((Type.Tuple) toType).element(index)));
			} else if (fromType instanceof Type.UnionOfTuples) {
				values.add(castConstanttoConstant(value, ((Type.UnionOfTuples) fromType).element(index),
						((Type.Tuple) toType).element(index)));
			} else {
				internalFailure("Not implemented!", "ConvertInterpreter.java", null);
				return null;
			}

		}
		return Constant.V_TUPLE(values);

	}

	/**
	 * Casts a Constant from one Constant type to another Constant type  
	 * @param constant the Constant
	 * @param fromType the original type
	 * @param toType the casted type
	 * @return Constant
	 */
	private Constant toConstantAny(Constant constant, Type fromType, Type.Any toType) {
		if (constant instanceof DecimalFraction) {
			// Cast to a string
			return Constant.V_STRING(((DecimalFraction) constant).toString());
		} 
		if (constant instanceof Constant.Decimal) {			
			Constant.Decimal decimal = (Constant.Decimal) constant;
			// If the negative constant contains any decimal, then convert Constant.Decimal to DecimalFraction.
			if (decimal.value.signum()<0 && decimal.value.scale()>0) {
				return DecimalFraction.V_DecimalFraction(decimal);
			}
			//Get the precision of the decimal part. 
			//If precision >1, try to remove the zeros after decimal points
			if(decimal.value.remainder(BigDecimal.ONE).precision()>1){
				BigDecimal frac = decimal.value.stripTrailingZeros();
				return Constant.V_DECIMAL(frac);
			}
			return decimal;
		}

		if(fromType instanceof Type.Union){
			//For union type only.
			if(constant instanceof Constant.Char){
				//Cast the char to Constant.Strung, so that it can output the string without single quote (').
				return Constant.V_STRING(((Constant.Char)constant).value+"");
			}
		}		

		if (constant instanceof Constant) {
			return constant;
		}
		internalFailure("Not implemented!", "ConvertInterpreter.java", null);
		return null;
	}

	private Constant toConstantStrung(Constant constant, Type fromType, Type.Strung toType) {
		if (constant instanceof Constant.Strung) {
			// Cast to a string
			return Constant.V_STRING(constant.toString());
		} else {
			// No needs to convert the type of the operand.
			return constant;
		}
	}
	/**
	 * Converts the constant to the constant of the given Union Type.
	 * @param constant
	 * @param fromType
	 * @param toType the given Union type
	 * @return
	 */
	private Constant toConstantUnion(Constant constant, Type fromType, Type.Union toType) {
		for (Type type: toType.bounds()) {
			//Check the original type is one of Union type.
			if (fromType.equals(type)) {
				return constant;
			}
			// Check if there is any need for converting an integer to a real
			if (constant instanceof Constant.Integer && fromType instanceof Type.Int && type instanceof Type.Real) {
				return toConstantDecimal(constant, fromType, (Type.Real) type);
			}
		}
		return constant;
	}

	private Constant toConstantUnionMaps(Constant constant, Type fromType, Type.UnionOfMaps toType) {
		// The bounds of the union type has been sorted by key, so the order of
		// type has been changed.
		if (constant instanceof Constant.Map) {
			Constant.Map map = (Constant.Map) constant;

			Type fromKeyType, fromValueType;
			fromKeyType = ((Type.Map) fromType).key();
			fromValueType = ((Type.Map) fromType).value();
			Type toKeyType = null, toValueType = null;
			Iterator<Type> iterType = toType.bounds().iterator();
			// Find the matched types by comparing the value type.
			while (iterType.hasNext()) {
				Type boundType = iterType.next();
				if (boundType instanceof Type.Map) {
					toKeyType = ((Type.Map) boundType).key();
					toValueType = ((Type.Map) boundType).value();
					// Compare each bound type with the given value type.
					if (fromValueType.equals(toValueType)) {
						break;
					}
				} else {
					internalFailure("Not implemented!", "ConvertInterpreter.java", null);
					return null;
				}

			}

			if (toKeyType != null && toValueType != null) {
				// Create a new Hashmap
				LinkedHashMap<Constant, Constant> newValues = new LinkedHashMap<Constant, Constant>();

				// Cast the type for each entry in the map.
				Iterator<Entry<Constant, Constant>> iterator = map.values.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<Constant, Constant> entry = iterator.next();
					// Cast the entry
					Constant key = castConstanttoConstant(entry.getKey(), fromKeyType, toKeyType);
					Constant value = castConstanttoConstant(entry.getValue(), fromValueType, toValueType);
					newValues.put(key, value);
				}
				return Constant.V_MAP(newValues);
			}
		}

		return constant;
	}

	private Constant toConstantUnionLists(Constant constant, Type fromType, Type.UnionOfLists toType) {
		if (constant instanceof Constant.List) {
			Constant.List list = (Constant.List) constant;

			Type fromElemType = ((Type.List) fromType).key();
			Type toElemType = null;
			Iterator<Type> iterType = toType.bounds().iterator();
			// Find the matched types by comparing the value type.
			while (iterType.hasNext()) {
				Type boundType = iterType.next();
				if (boundType instanceof Type.List) {
					toElemType = ((Type.List) boundType).key();
					// Compare each bound type with the given value type.
					if (fromElemType.equals(toElemType)) {
						break;
					}
				}
			}

			if (toElemType != null) {
				// Create a new Hashmap
				ArrayList<Constant> newValues = new ArrayList<Constant>();

				// Cast the type for each entry in the map.
				Iterator<Constant> iterator = list.values.iterator();
				while (iterator.hasNext()) {
					Constant element = iterator.next();
					// Cast the entry
					Constant value = castConstanttoConstant(element, fromElemType, toElemType);
					newValues.add(value);
				}
				return Constant.V_LIST(newValues);
			}
		}

		return constant;
	}

	private Constant toConstantUnionTuples(Constant constant, Type fromType, Type.UnionOfTuples toType) {
		if (constant instanceof Constant.Tuple) {
			Constant.Tuple tuple = (Constant.Tuple) constant;
			Iterator<Type> iterator = toType.bounds().iterator();
			while (iterator.hasNext()) {
				Type type = iterator.next();
				if (fromType.equals(type)) {
					return tuple;// No needs of conversion.
				}

			}

			// internalFailure("Not implemented!", "ConvertInterpreter.java",
			// null);
			return tuple;

		}

		return constant;
	}

	private Constant toConstantUnionRecords(Constant constant, Type fromType, Type.UnionOfRecords toType) {
		if (constant instanceof Constant.Record) {
			Constant.Record record = (Constant.Record) constant;
			Iterator<Type> iterator = toType.bounds().iterator();
			while (iterator.hasNext()) {
				Type type = iterator.next();
				if (fromType.equals(type)) {
					return record;// No needs of conversion.
				}

			}


			return record;

		}

		return constant;
	}

	private Constant toConstantReference(Constant constant, Type fromType, Type.Reference toType) {
		//Converting a reference to another reference does not require type-casting.
		return constant;
	}

	private Constant toConstantFunction(Constant constant, Type fromType, Type.Function toType) {
		//Converting a reference to another reference does not require type-casting.
		return constant;
	}



	/***
	 * Cast a Constant object to the Constant object of a specific type.
	 * 
	 * @param constant
	 * @param fromType
	 * @param toType
	 * @return
	 */
	public Constant castConstanttoConstant(Constant constant, wyil.lang.Type fromType, wyil.lang.Type toType) {
		if (toType == null || (fromType != null && fromType.equals(toType))) {
			return constant;// Not casting.
		}

		if (toType instanceof Type.Any) {
			return toConstantAny(constant, fromType, (Type.Any) toType);
		} else if (toType instanceof Type.Char) {
			// Cast ascii to Char
			return toConstantChar(constant, fromType, (Type.Char) toType);
		} else if (toType instanceof Type.Int) {
			return toConstantInt(constant, (Type.Int) toType);
		} else if (toType instanceof Type.List) {
			return toConstantList(constant, fromType, (Type.List) toType);
		} else if (toType instanceof Type.Record) {
			return toConstantRecord(constant, fromType, (Type.Record) toType);
		} else if (toType instanceof Type.Real) {
			return toConstantDecimal(constant, fromType, (Type.Real) toType);
		} else if (toType instanceof Type.Map) {
			return toConstantMap(constant, fromType, (Type.Map) toType);
		} else if (toType instanceof Type.Set) {
			return toConstantSet(constant, fromType, (Type.Set) toType);
		} else if (toType instanceof Type.Tuple) {
			return toConstantTuple(constant, fromType, (Type.Tuple) toType);
		} else if (toType instanceof Type.Negation) {
			return toConstantNegation(constant, fromType, (Type.Negation) toType);
		} else if (toType instanceof Type.UnionOfMaps) {
			return toConstantUnionMaps(constant, fromType, (Type.UnionOfMaps) toType);
		} else if (toType instanceof Type.UnionOfLists) {
			return toConstantUnionLists(constant, fromType, (Type.UnionOfLists) toType);
		} else if (toType instanceof Type.UnionOfTuples) {
			return toConstantUnionTuples(constant, fromType, (Type.UnionOfTuples) toType);
		} else if (toType instanceof Type.UnionOfRecords) {
			return toConstantUnionRecords(constant, fromType, (Type.UnionOfRecords) toType);
		} else if (toType instanceof Type.Union) {
			return toConstantUnion(constant, fromType, (Type.Union) toType);
		} else if (toType instanceof Type.Strung) {
			return toConstantStrung(constant, fromType, (Type.Strung) toType);
		} else if (toType instanceof Type.Reference){
			return toConstantReference(constant, fromType, (Type.Reference)toType);
		} else if (toType instanceof Type.Function){
			return toConstantFunction(constant, fromType, (Type.Function)toType);
		} else {
			internalFailure("Not implemented!", "ConvertInterpreter.java", null);
			return null;
		}
	}

	/**
	 * Converts the input constant to another constant of the given type. 
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.Convert code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		// Read a value from the operand register.
		Constant constant = stackframe.getRegister(code.operand(0));
		// Convert it to the given type.
		Constant result = castConstanttoConstant(constant, code.type(), code.result);
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%" + code.target() + "(" + result + ")");
		stackframe.setLine(++linenumber);
	}

}
