package wyil.testing;

import java.io.*;
import java.util.ArrayList;

import wyil.lang.Type;
import org.junit.*;
import static org.junit.Assert.*;


public class TypeTests {	
	public @Test void test_1() {
		checkIsSubtype("any","null");
	}
	public @Test void test_2() {
		checkIsSubtype("any","bool");
	}
	public @Test void test_3() {
		checkIsSubtype("any","byte");
	}
	public @Test void test_4() {
		checkIsSubtype("any","char");
	}
	public @Test void test_5() {
		checkIsSubtype("any","int");
	}
	public @Test void test_6() {
		checkIsSubtype("any","real");
	}
	public @Test void test_7() {
		checkIsSubtype("any","string");
	}
	public @Test void test_8() {
		checkIsSubtype("any","{void}");
	}
	public @Test void test_9() {
		checkIsSubtype("any","{any}");
	}
	public @Test void test_10() {
		checkIsSubtype("any","{null}");
	}
	public @Test void test_11() {
		checkIsSubtype("any","{bool}");
	}
	public @Test void test_12() {
		checkIsSubtype("any","{byte}");
	}
	public @Test void test_13() {
		checkIsSubtype("any","{char}");
	}
	public @Test void test_14() {
		checkIsSubtype("any","{int}");
	}
	public @Test void test_15() {
		checkIsSubtype("any","{real}");
	}
	public @Test void test_16() {
		checkIsSubtype("any","{string}");
	}
	public @Test void test_17() {
		checkIsSubtype("any","[void]");
	}
	public @Test void test_18() {
		checkIsSubtype("any","[any]");
	}
	public @Test void test_19() {
		checkIsSubtype("any","[null]");
	}
	public @Test void test_20() {
		checkIsSubtype("any","[bool]");
	}
	public @Test void test_21() {
		checkIsSubtype("any","[byte]");
	}
	public @Test void test_22() {
		checkIsSubtype("any","[char]");
	}
	public @Test void test_23() {
		checkIsSubtype("any","[int]");
	}
	public @Test void test_24() {
		checkIsSubtype("any","[real]");
	}
	public @Test void test_25() {
		checkIsSubtype("any","[string]");
	}
	public @Test void test_26() {
		checkIsSubtype("null","bool");
	}
	public @Test void test_27() {
		checkIsSubtype("null","byte");
	}
	public @Test void test_28() {
		checkIsSubtype("null","char");
	}
	public @Test void test_29() {
		checkIsSubtype("null","int");
	}
	public @Test void test_30() {
		checkIsSubtype("null","real");
	}
	public @Test void test_31() {
		checkIsSubtype("null","string");
	}
	public @Test void test_32() {
		checkIsSubtype("null","{void}");
	}
	public @Test void test_33() {
		checkIsSubtype("null","{any}");
	}
	public @Test void test_34() {
		checkIsSubtype("null","{null}");
	}
	public @Test void test_35() {
		checkIsSubtype("null","{bool}");
	}
	public @Test void test_36() {
		checkIsSubtype("null","{byte}");
	}
	public @Test void test_37() {
		checkIsSubtype("null","{char}");
	}
	public @Test void test_38() {
		checkIsSubtype("null","{int}");
	}
	public @Test void test_39() {
		checkIsSubtype("null","{real}");
	}
	public @Test void test_40() {
		checkIsSubtype("null","{string}");
	}
	public @Test void test_41() {
		checkIsSubtype("null","[void]");
	}
	public @Test void test_42() {
		checkIsSubtype("null","[any]");
	}
	public @Test void test_43() {
		checkIsSubtype("null","[null]");
	}
	public @Test void test_44() {
		checkIsSubtype("null","[bool]");
	}
	public @Test void test_45() {
		checkIsSubtype("null","[byte]");
	}
	public @Test void test_46() {
		checkIsSubtype("null","[char]");
	}
	public @Test void test_47() {
		checkIsSubtype("null","[int]");
	}
	public @Test void test_48() {
		checkIsSubtype("null","[real]");
	}
	public @Test void test_49() {
		checkIsSubtype("null","[string]");
	}
	public @Test void test_50() {
		checkIsSubtype("bool","byte");
	}
	public @Test void test_51() {
		checkIsSubtype("bool","char");
	}
	public @Test void test_52() {
		checkIsSubtype("bool","int");
	}
	public @Test void test_53() {
		checkIsSubtype("bool","real");
	}
	public @Test void test_54() {
		checkIsSubtype("bool","string");
	}
	public @Test void test_55() {
		checkIsSubtype("bool","{void}");
	}
	public @Test void test_56() {
		checkIsSubtype("bool","{any}");
	}
	public @Test void test_57() {
		checkIsSubtype("bool","{null}");
	}
	public @Test void test_58() {
		checkIsSubtype("bool","{bool}");
	}
	public @Test void test_59() {
		checkIsSubtype("bool","{byte}");
	}
	public @Test void test_60() {
		checkIsSubtype("bool","{char}");
	}
	public @Test void test_61() {
		checkIsSubtype("bool","{int}");
	}
	public @Test void test_62() {
		checkIsSubtype("bool","{real}");
	}
	public @Test void test_63() {
		checkIsSubtype("bool","{string}");
	}
	public @Test void test_64() {
		checkIsSubtype("bool","[void]");
	}
	public @Test void test_65() {
		checkIsSubtype("bool","[any]");
	}
	public @Test void test_66() {
		checkIsSubtype("bool","[null]");
	}
	public @Test void test_67() {
		checkIsSubtype("bool","[bool]");
	}
	public @Test void test_68() {
		checkIsSubtype("bool","[byte]");
	}
	public @Test void test_69() {
		checkIsSubtype("bool","[char]");
	}
	public @Test void test_70() {
		checkIsSubtype("bool","[int]");
	}
	public @Test void test_71() {
		checkIsSubtype("bool","[real]");
	}
	public @Test void test_72() {
		checkIsSubtype("bool","[string]");
	}
	public @Test void test_73() {
		checkIsSubtype("byte","char");
	}
	public @Test void test_74() {
		checkIsSubtype("byte","int");
	}
	public @Test void test_75() {
		checkIsSubtype("byte","real");
	}
	public @Test void test_76() {
		checkIsSubtype("byte","string");
	}
	public @Test void test_77() {
		checkIsSubtype("byte","{void}");
	}
	public @Test void test_78() {
		checkIsSubtype("byte","{any}");
	}
	public @Test void test_79() {
		checkIsSubtype("byte","{null}");
	}
	public @Test void test_80() {
		checkIsSubtype("byte","{bool}");
	}
	public @Test void test_81() {
		checkIsSubtype("byte","{byte}");
	}
	public @Test void test_82() {
		checkIsSubtype("byte","{char}");
	}
	public @Test void test_83() {
		checkIsSubtype("byte","{int}");
	}
	public @Test void test_84() {
		checkIsSubtype("byte","{real}");
	}
	public @Test void test_85() {
		checkIsSubtype("byte","{string}");
	}
	public @Test void test_86() {
		checkIsSubtype("byte","[void]");
	}
	public @Test void test_87() {
		checkIsSubtype("byte","[any]");
	}
	public @Test void test_88() {
		checkIsSubtype("byte","[null]");
	}
	public @Test void test_89() {
		checkIsSubtype("byte","[bool]");
	}
	public @Test void test_90() {
		checkIsSubtype("byte","[byte]");
	}
	public @Test void test_91() {
		checkIsSubtype("byte","[char]");
	}
	public @Test void test_92() {
		checkIsSubtype("byte","[int]");
	}
	public @Test void test_93() {
		checkIsSubtype("byte","[real]");
	}
	public @Test void test_94() {
		checkIsSubtype("byte","[string]");
	}
	public @Test void test_95() {
		checkIsSubtype("char","int");
	}
	public @Test void test_96() {
		checkIsSubtype("char","real");
	}
	public @Test void test_97() {
		checkIsSubtype("char","string");
	}
	public @Test void test_98() {
		checkIsSubtype("char","{void}");
	}
	public @Test void test_99() {
		checkIsSubtype("char","{any}");
	}
	public @Test void test_100() {
		checkIsSubtype("char","{null}");
	}
	public @Test void test_101() {
		checkIsSubtype("char","{bool}");
	}
	public @Test void test_102() {
		checkIsSubtype("char","{byte}");
	}
	public @Test void test_103() {
		checkIsSubtype("char","{char}");
	}
	public @Test void test_104() {
		checkIsSubtype("char","{int}");
	}
	public @Test void test_105() {
		checkIsSubtype("char","{real}");
	}
	public @Test void test_106() {
		checkIsSubtype("char","{string}");
	}
	public @Test void test_107() {
		checkIsSubtype("char","[void]");
	}
	public @Test void test_108() {
		checkIsSubtype("char","[any]");
	}
	public @Test void test_109() {
		checkIsSubtype("char","[null]");
	}
	public @Test void test_110() {
		checkIsSubtype("char","[bool]");
	}
	public @Test void test_111() {
		checkIsSubtype("char","[byte]");
	}
	public @Test void test_112() {
		checkIsSubtype("char","[char]");
	}
	public @Test void test_113() {
		checkIsSubtype("char","[int]");
	}
	public @Test void test_114() {
		checkIsSubtype("char","[real]");
	}
	public @Test void test_115() {
		checkIsSubtype("char","[string]");
	}
	public @Test void test_116() {
		checkIsSubtype("int","real");
	}
	public @Test void test_117() {
		checkIsSubtype("int","string");
	}
	public @Test void test_118() {
		checkIsSubtype("int","{void}");
	}
	public @Test void test_119() {
		checkIsSubtype("int","{any}");
	}
	public @Test void test_120() {
		checkIsSubtype("int","{null}");
	}
	public @Test void test_121() {
		checkIsSubtype("int","{bool}");
	}
	public @Test void test_122() {
		checkIsSubtype("int","{byte}");
	}
	public @Test void test_123() {
		checkIsSubtype("int","{char}");
	}
	public @Test void test_124() {
		checkIsSubtype("int","{int}");
	}
	public @Test void test_125() {
		checkIsSubtype("int","{real}");
	}
	public @Test void test_126() {
		checkIsSubtype("int","{string}");
	}
	public @Test void test_127() {
		checkIsSubtype("int","[void]");
	}
	public @Test void test_128() {
		checkIsSubtype("int","[any]");
	}
	public @Test void test_129() {
		checkIsSubtype("int","[null]");
	}
	public @Test void test_130() {
		checkIsSubtype("int","[bool]");
	}
	public @Test void test_131() {
		checkIsSubtype("int","[byte]");
	}
	public @Test void test_132() {
		checkIsSubtype("int","[char]");
	}
	public @Test void test_133() {
		checkIsSubtype("int","[int]");
	}
	public @Test void test_134() {
		checkIsSubtype("int","[real]");
	}
	public @Test void test_135() {
		checkIsSubtype("int","[string]");
	}
	public @Test void test_136() {
		checkIsSubtype("real","string");
	}
	public @Test void test_137() {
		checkIsSubtype("real","{void}");
	}
	public @Test void test_138() {
		checkIsSubtype("real","{any}");
	}
	public @Test void test_139() {
		checkIsSubtype("real","{null}");
	}
	public @Test void test_140() {
		checkIsSubtype("real","{bool}");
	}
	public @Test void test_141() {
		checkIsSubtype("real","{byte}");
	}
	public @Test void test_142() {
		checkIsSubtype("real","{char}");
	}
	public @Test void test_143() {
		checkIsSubtype("real","{int}");
	}
	public @Test void test_144() {
		checkIsSubtype("real","{real}");
	}
	public @Test void test_145() {
		checkIsSubtype("real","{string}");
	}
	public @Test void test_146() {
		checkIsSubtype("real","[void]");
	}
	public @Test void test_147() {
		checkIsSubtype("real","[any]");
	}
	public @Test void test_148() {
		checkIsSubtype("real","[null]");
	}
	public @Test void test_149() {
		checkIsSubtype("real","[bool]");
	}
	public @Test void test_150() {
		checkIsSubtype("real","[byte]");
	}
	public @Test void test_151() {
		checkIsSubtype("real","[char]");
	}
	public @Test void test_152() {
		checkIsSubtype("real","[int]");
	}
	public @Test void test_153() {
		checkIsSubtype("real","[real]");
	}
	public @Test void test_154() {
		checkIsSubtype("real","[string]");
	}
	public @Test void test_155() {
		checkIsSubtype("string","{void}");
	}
	public @Test void test_156() {
		checkIsSubtype("string","{any}");
	}
	public @Test void test_157() {
		checkIsSubtype("string","{null}");
	}
	public @Test void test_158() {
		checkIsSubtype("string","{bool}");
	}
	public @Test void test_159() {
		checkIsSubtype("string","{byte}");
	}
	public @Test void test_160() {
		checkIsSubtype("string","{char}");
	}
	public @Test void test_161() {
		checkIsSubtype("string","{int}");
	}
	public @Test void test_162() {
		checkIsSubtype("string","{real}");
	}
	public @Test void test_163() {
		checkIsSubtype("string","{string}");
	}
	public @Test void test_164() {
		checkIsSubtype("string","[void]");
	}
	public @Test void test_165() {
		checkIsSubtype("string","[any]");
	}
	public @Test void test_166() {
		checkIsSubtype("string","[null]");
	}
	public @Test void test_167() {
		checkIsSubtype("string","[bool]");
	}
	public @Test void test_168() {
		checkIsSubtype("string","[byte]");
	}
	public @Test void test_169() {
		checkIsSubtype("string","[char]");
	}
	public @Test void test_170() {
		checkIsSubtype("string","[int]");
	}
	public @Test void test_171() {
		checkIsSubtype("string","[real]");
	}
	public @Test void test_172() {
		checkIsSubtype("string","[string]");
	}
	public @Test void test_173() {
		checkIsSubtype("{void}","{any}");
	}
	public @Test void test_174() {
		checkIsSubtype("{void}","{null}");
	}
	public @Test void test_175() {
		checkIsSubtype("{void}","{bool}");
	}
	public @Test void test_176() {
		checkIsSubtype("{void}","{byte}");
	}
	public @Test void test_177() {
		checkIsSubtype("{void}","{char}");
	}
	public @Test void test_178() {
		checkIsSubtype("{void}","{int}");
	}
	public @Test void test_179() {
		checkIsSubtype("{void}","{real}");
	}
	public @Test void test_180() {
		checkIsSubtype("{void}","{string}");
	}
	public @Test void test_181() {
		checkIsSubtype("{void}","[void]");
	}
	public @Test void test_182() {
		checkIsSubtype("{void}","[any]");
	}
	public @Test void test_183() {
		checkIsSubtype("{void}","[null]");
	}
	public @Test void test_184() {
		checkIsSubtype("{void}","[bool]");
	}
	public @Test void test_185() {
		checkIsSubtype("{void}","[byte]");
	}
	public @Test void test_186() {
		checkIsSubtype("{void}","[char]");
	}
	public @Test void test_187() {
		checkIsSubtype("{void}","[int]");
	}
	public @Test void test_188() {
		checkIsSubtype("{void}","[real]");
	}
	public @Test void test_189() {
		checkIsSubtype("{void}","[string]");
	}
	public @Test void test_190() {
		checkIsSubtype("{any}","{null}");
	}
	public @Test void test_191() {
		checkIsSubtype("{any}","{bool}");
	}
	public @Test void test_192() {
		checkIsSubtype("{any}","{byte}");
	}
	public @Test void test_193() {
		checkIsSubtype("{any}","{char}");
	}
	public @Test void test_194() {
		checkIsSubtype("{any}","{int}");
	}
	public @Test void test_195() {
		checkIsSubtype("{any}","{real}");
	}
	public @Test void test_196() {
		checkIsSubtype("{any}","{string}");
	}
	public @Test void test_197() {
		checkIsSubtype("{any}","[void]");
	}
	public @Test void test_198() {
		checkIsSubtype("{any}","[any]");
	}
	public @Test void test_199() {
		checkIsSubtype("{any}","[null]");
	}
	public @Test void test_200() {
		checkIsSubtype("{any}","[bool]");
	}
	public @Test void test_201() {
		checkIsSubtype("{any}","[byte]");
	}
	public @Test void test_202() {
		checkIsSubtype("{any}","[char]");
	}
	public @Test void test_203() {
		checkIsSubtype("{any}","[int]");
	}
	public @Test void test_204() {
		checkIsSubtype("{any}","[real]");
	}
	public @Test void test_205() {
		checkIsSubtype("{any}","[string]");
	}
	public @Test void test_206() {
		checkIsSubtype("{null}","{bool}");
	}
	public @Test void test_207() {
		checkIsSubtype("{null}","{byte}");
	}
	public @Test void test_208() {
		checkIsSubtype("{null}","{char}");
	}
	public @Test void test_209() {
		checkIsSubtype("{null}","{int}");
	}
	public @Test void test_210() {
		checkIsSubtype("{null}","{real}");
	}
	public @Test void test_211() {
		checkIsSubtype("{null}","{string}");
	}
	public @Test void test_212() {
		checkIsSubtype("{null}","[void]");
	}
	public @Test void test_213() {
		checkIsSubtype("{null}","[any]");
	}
	public @Test void test_214() {
		checkIsSubtype("{null}","[null]");
	}
	public @Test void test_215() {
		checkIsSubtype("{null}","[bool]");
	}
	public @Test void test_216() {
		checkIsSubtype("{null}","[byte]");
	}
	public @Test void test_217() {
		checkIsSubtype("{null}","[char]");
	}
	public @Test void test_218() {
		checkIsSubtype("{null}","[int]");
	}
	public @Test void test_219() {
		checkIsSubtype("{null}","[real]");
	}
	public @Test void test_220() {
		checkIsSubtype("{null}","[string]");
	}
	public @Test void test_221() {
		checkIsSubtype("{bool}","{byte}");
	}
	public @Test void test_222() {
		checkIsSubtype("{bool}","{char}");
	}
	public @Test void test_223() {
		checkIsSubtype("{bool}","{int}");
	}
	public @Test void test_224() {
		checkIsSubtype("{bool}","{real}");
	}
	public @Test void test_225() {
		checkIsSubtype("{bool}","{string}");
	}
	public @Test void test_226() {
		checkIsSubtype("{bool}","[void]");
	}
	public @Test void test_227() {
		checkIsSubtype("{bool}","[any]");
	}
	public @Test void test_228() {
		checkIsSubtype("{bool}","[null]");
	}
	public @Test void test_229() {
		checkIsSubtype("{bool}","[bool]");
	}
	public @Test void test_230() {
		checkIsSubtype("{bool}","[byte]");
	}
	public @Test void test_231() {
		checkIsSubtype("{bool}","[char]");
	}
	public @Test void test_232() {
		checkIsSubtype("{bool}","[int]");
	}
	public @Test void test_233() {
		checkIsSubtype("{bool}","[real]");
	}
	public @Test void test_234() {
		checkIsSubtype("{bool}","[string]");
	}
	public @Test void test_235() {
		checkIsSubtype("{byte}","{char}");
	}
	public @Test void test_236() {
		checkIsSubtype("{byte}","{int}");
	}
	public @Test void test_237() {
		checkIsSubtype("{byte}","{real}");
	}
	public @Test void test_238() {
		checkIsSubtype("{byte}","{string}");
	}
	public @Test void test_239() {
		checkIsSubtype("{byte}","[void]");
	}
	public @Test void test_240() {
		checkIsSubtype("{byte}","[any]");
	}
	public @Test void test_241() {
		checkIsSubtype("{byte}","[null]");
	}
	public @Test void test_242() {
		checkIsSubtype("{byte}","[bool]");
	}
	public @Test void test_243() {
		checkIsSubtype("{byte}","[byte]");
	}
	public @Test void test_244() {
		checkIsSubtype("{byte}","[char]");
	}
	public @Test void test_245() {
		checkIsSubtype("{byte}","[int]");
	}
	public @Test void test_246() {
		checkIsSubtype("{byte}","[real]");
	}
	public @Test void test_247() {
		checkIsSubtype("{byte}","[string]");
	}
	public @Test void test_248() {
		checkIsSubtype("{char}","{int}");
	}
	public @Test void test_249() {
		checkIsSubtype("{char}","{real}");
	}
	public @Test void test_250() {
		checkIsSubtype("{char}","{string}");
	}
	public @Test void test_251() {
		checkIsSubtype("{char}","[void]");
	}
	public @Test void test_252() {
		checkIsSubtype("{char}","[any]");
	}
	public @Test void test_253() {
		checkIsSubtype("{char}","[null]");
	}
	public @Test void test_254() {
		checkIsSubtype("{char}","[bool]");
	}
	public @Test void test_255() {
		checkIsSubtype("{char}","[byte]");
	}
	public @Test void test_256() {
		checkIsSubtype("{char}","[char]");
	}
	public @Test void test_257() {
		checkIsSubtype("{char}","[int]");
	}
	public @Test void test_258() {
		checkIsSubtype("{char}","[real]");
	}
	public @Test void test_259() {
		checkIsSubtype("{char}","[string]");
	}
	public @Test void test_260() {
		checkIsSubtype("{int}","{real}");
	}
	public @Test void test_261() {
		checkIsSubtype("{int}","{string}");
	}
	public @Test void test_262() {
		checkIsSubtype("{int}","[void]");
	}
	public @Test void test_263() {
		checkIsSubtype("{int}","[any]");
	}
	public @Test void test_264() {
		checkIsSubtype("{int}","[null]");
	}
	public @Test void test_265() {
		checkIsSubtype("{int}","[bool]");
	}
	public @Test void test_266() {
		checkIsSubtype("{int}","[byte]");
	}
	public @Test void test_267() {
		checkIsSubtype("{int}","[char]");
	}
	public @Test void test_268() {
		checkIsSubtype("{int}","[int]");
	}
	public @Test void test_269() {
		checkIsSubtype("{int}","[real]");
	}
	public @Test void test_270() {
		checkIsSubtype("{int}","[string]");
	}
	public @Test void test_271() {
		checkIsSubtype("{real}","{string}");
	}
	public @Test void test_272() {
		checkIsSubtype("{real}","[void]");
	}
	public @Test void test_273() {
		checkIsSubtype("{real}","[any]");
	}
	public @Test void test_274() {
		checkIsSubtype("{real}","[null]");
	}
	public @Test void test_275() {
		checkIsSubtype("{real}","[bool]");
	}
	public @Test void test_276() {
		checkIsSubtype("{real}","[byte]");
	}
	public @Test void test_277() {
		checkIsSubtype("{real}","[char]");
	}
	public @Test void test_278() {
		checkIsSubtype("{real}","[int]");
	}
	public @Test void test_279() {
		checkIsSubtype("{real}","[real]");
	}
	public @Test void test_280() {
		checkIsSubtype("{real}","[string]");
	}
	public @Test void test_281() {
		checkIsSubtype("{string}","[void]");
	}
	public @Test void test_282() {
		checkIsSubtype("{string}","[any]");
	}
	public @Test void test_283() {
		checkIsSubtype("{string}","[null]");
	}
	public @Test void test_284() {
		checkIsSubtype("{string}","[bool]");
	}
	public @Test void test_285() {
		checkIsSubtype("{string}","[byte]");
	}
	public @Test void test_286() {
		checkIsSubtype("{string}","[char]");
	}
	public @Test void test_287() {
		checkIsSubtype("{string}","[int]");
	}
	public @Test void test_288() {
		checkIsSubtype("{string}","[real]");
	}
	public @Test void test_289() {
		checkIsSubtype("{string}","[string]");
	}
	public @Test void test_290() {
		checkIsSubtype("[void]","[any]");
	}
	public @Test void test_291() {
		checkIsSubtype("[void]","[null]");
	}
	public @Test void test_292() {
		checkIsSubtype("[void]","[bool]");
	}
	public @Test void test_293() {
		checkIsSubtype("[void]","[byte]");
	}
	public @Test void test_294() {
		checkIsSubtype("[void]","[char]");
	}
	public @Test void test_295() {
		checkIsSubtype("[void]","[int]");
	}
	public @Test void test_296() {
		checkIsSubtype("[void]","[real]");
	}
	public @Test void test_297() {
		checkIsSubtype("[void]","[string]");
	}
	public @Test void test_298() {
		checkIsSubtype("[any]","[null]");
	}
	public @Test void test_299() {
		checkIsSubtype("[any]","[bool]");
	}
	public @Test void test_300() {
		checkIsSubtype("[any]","[byte]");
	}
	public @Test void test_301() {
		checkIsSubtype("[any]","[char]");
	}
	public @Test void test_302() {
		checkIsSubtype("[any]","[int]");
	}
	public @Test void test_303() {
		checkIsSubtype("[any]","[real]");
	}
	public @Test void test_304() {
		checkIsSubtype("[any]","[string]");
	}
	public @Test void test_305() {
		checkIsSubtype("[null]","[bool]");
	}
	public @Test void test_306() {
		checkIsSubtype("[null]","[byte]");
	}
	public @Test void test_307() {
		checkIsSubtype("[null]","[char]");
	}
	public @Test void test_308() {
		checkIsSubtype("[null]","[int]");
	}
	public @Test void test_309() {
		checkIsSubtype("[null]","[real]");
	}
	public @Test void test_310() {
		checkIsSubtype("[null]","[string]");
	}
	public @Test void test_311() {
		checkIsSubtype("[bool]","[byte]");
	}
	public @Test void test_312() {
		checkIsSubtype("[bool]","[char]");
	}
	public @Test void test_313() {
		checkIsSubtype("[bool]","[int]");
	}
	public @Test void test_314() {
		checkIsSubtype("[bool]","[real]");
	}
	public @Test void test_315() {
		checkIsSubtype("[bool]","[string]");
	}
	public @Test void test_316() {
		checkIsSubtype("[byte]","[char]");
	}
	public @Test void test_317() {
		checkIsSubtype("[byte]","[int]");
	}
	public @Test void test_318() {
		checkIsSubtype("[byte]","[real]");
	}
	public @Test void test_319() {
		checkIsSubtype("[byte]","[string]");
	}
	public @Test void test_320() {
		checkIsSubtype("[char]","[int]");
	}
	public @Test void test_321() {
		checkIsSubtype("[char]","[real]");
	}
	public @Test void test_322() {
		checkIsSubtype("[char]","[string]");
	}
	public @Test void test_323() {
		checkIsSubtype("[int]","[real]");
	}
	public @Test void test_324() {
		checkIsSubtype("[int]","[string]");
	}
	public @Test void test_325() {
		checkIsSubtype("[real]","[string]");
	}

	
	public void checkIsSubtype(String from, String to) {
		Type ft = Type.fromString(from);
		Type tt = Type.fromString(to);
		assertTrue(Type.isSubtype(ft,tt));
	}
	
	public static void main(String[] args) {
		try {			
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String line;
			ArrayList<String> types = new ArrayList<String>();
			while((line = reader.readLine()) != null) {
				types.add(line);
			}
			int count = 1;
			for(int i=0;i!=types.size();++i) {
				String t1 = types.get(i);
				for(int j=i+1;j<types.size();++j) {
					String t2 = types.get(j);
					System.out.println("\tpublic @Test void test_" + count++ + "() {");
					System.out.println("\t\tcheckIsSubtype(\"" + t1 + "\",\"" + t2 + "\");");
					System.out.println("\t}");
				}
			}
		} catch(IOException e) {
			
		}
	}
}
