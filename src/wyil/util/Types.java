package wyil.util;

import java.io.IOException;
import java.util.*;

import wyil.lang.ModuleID;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyjvm.io.*;

public class Types {

	public static final int EXISTENTIAL_TYPE = 1;
	public static final int ANY_TYPE = 2;
	public static final int VOID_TYPE = 3;
	public static final int NULL_TYPE = 4;
	public static final int BOOL_TYPE = 5;
	public static final int BYTE_TYPE = 6;
	public static final int CHAR_TYPE = 7;
	public static final int INT_TYPE = 8;
	public static final int REAL_TYPE = 9;
	public static final int STRING_TYPE = 10;
	public static final int LIST_TYPE = 11;
	public static final int SET_TYPE = 12;
	public static final int DICTIONARY_TYPE = 13;
	public static final int TUPLE_TYPE = 14;
	public static final int RECORD_TYPE = 15;
	public static final int UNION_TYPE = 16;
	public static final int INTERSECTION_TYPE = 17;
	public static final int PROCESS_TYPE = 18;	
	public static final int FUN_TYPE = 19;
	public static final int METH_TYPE = 20;
	public static final int HEADLESS_METH_TYPE = 21;
	public static final int CONSTRAINT_MASK = 32;			
}
