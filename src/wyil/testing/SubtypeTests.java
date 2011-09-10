// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class SubtypeTests {
	@Test public void test_1() {
		checkIsSubtype("any","any");
	}
	@Test public void test_2() {
		checkIsSubtype("any","null");
	}
	@Test public void test_3() {
		checkIsSubtype("any","int");
	}
	@Test public void test_4() {
		checkIsSubtype("any","[void]");
	}
	@Test public void test_5() {
		checkIsSubtype("any","[any]");
	}
	@Test public void test_6() {
		checkIsSubtype("any","[null]");
	}
	@Test public void test_7() {
		checkIsSubtype("any","[int]");
	}
	@Test public void test_8() {
		checkIsSubtype("any","[[void]]");
	}
	@Test public void test_9() {
		checkIsSubtype("any","[[any]]");
	}
	@Test public void test_10() {
		checkIsSubtype("any","[[null]]");
	}
	@Test public void test_11() {
		checkIsSubtype("any","[[int]]");
	}
	@Test public void test_12() {
		checkIsSubtype("any","void&void");
	}
	@Test public void test_13() {
		checkIsSubtype("any","void&any");
	}
	@Test public void test_14() {
		checkIsSubtype("any","void&null");
	}
	@Test public void test_15() {
		checkIsSubtype("any","void&int");
	}
	@Test public void test_16() {
		checkIsSubtype("any","any&void");
	}
	@Test public void test_17() {
		checkIsSubtype("any","any&any");
	}
	@Test public void test_18() {
		checkIsSubtype("any","any&null");
	}
	@Test public void test_19() {
		checkIsSubtype("any","any&int");
	}
	@Test public void test_20() {
		checkIsSubtype("any","null&void");
	}
	@Test public void test_21() {
		checkIsSubtype("any","null&any");
	}
	@Test public void test_22() {
		checkIsSubtype("any","null&null");
	}
	@Test public void test_23() {
		checkIsSubtype("any","null&int");
	}
	@Test public void test_24() {
		checkIsSubtype("any","int&void");
	}
	@Test public void test_25() {
		checkIsSubtype("any","int&any");
	}
	@Test public void test_26() {
		checkIsSubtype("any","int&null");
	}
	@Test public void test_27() {
		checkIsSubtype("any","int&int");
	}
	@Test public void test_28() {
		checkIsSubtype("any","[void]&void");
	}
	@Test public void test_29() {
		checkIsSubtype("any","[any]&any");
	}
	@Test public void test_30() {
		checkIsSubtype("any","[null]&null");
	}
	@Test public void test_31() {
		checkIsSubtype("any","[int]&int");
	}
	@Test public void test_32() {
		checkNotSubtype("null","any");
	}
	@Test public void test_33() {
		checkIsSubtype("null","null");
	}
	@Test public void test_34() {
		checkNotSubtype("null","int");
	}
	@Test public void test_35() {
		checkNotSubtype("null","[void]");
	}
	@Test public void test_36() {
		checkNotSubtype("null","[any]");
	}
	@Test public void test_37() {
		checkNotSubtype("null","[null]");
	}
	@Test public void test_38() {
		checkNotSubtype("null","[int]");
	}
	@Test public void test_39() {
		checkNotSubtype("null","[[void]]");
	}
	@Test public void test_40() {
		checkNotSubtype("null","[[any]]");
	}
	@Test public void test_41() {
		checkNotSubtype("null","[[null]]");
	}
	@Test public void test_42() {
		checkNotSubtype("null","[[int]]");
	}
	@Test public void test_43() {
		checkIsSubtype("null","void&void");
	}
	@Test public void test_44() {
		checkIsSubtype("null","void&any");
	}
	@Test public void test_45() {
		checkIsSubtype("null","void&null");
	}
	@Test public void test_46() {
		checkIsSubtype("null","void&int");
	}
	@Test public void test_47() {
		checkIsSubtype("null","any&void");
	}
	@Test public void test_48() {
		checkNotSubtype("null","any&any");
	}
	@Test public void test_49() {
		checkIsSubtype("null","any&null");
	}
	@Test public void test_50() {
		checkNotSubtype("null","any&int");
	}
	@Test public void test_51() {
		checkIsSubtype("null","null&void");
	}
	@Test public void test_52() {
		checkIsSubtype("null","null&any");
	}
	@Test public void test_53() {
		checkIsSubtype("null","null&null");
	}
	@Test public void test_54() {
		checkIsSubtype("null","null&int");
	}
	@Test public void test_55() {
		checkIsSubtype("null","int&void");
	}
	@Test public void test_56() {
		checkNotSubtype("null","int&any");
	}
	@Test public void test_57() {
		checkIsSubtype("null","int&null");
	}
	@Test public void test_58() {
		checkNotSubtype("null","int&int");
	}
	@Test public void test_59() {
		checkIsSubtype("null","[void]&void");
	}
	@Test public void test_60() {
		checkNotSubtype("null","[any]&any");
	}
	@Test public void test_61() {
		checkIsSubtype("null","[null]&null");
	}
	@Test public void test_62() {
		checkIsSubtype("null","[int]&int");
	}
	@Test public void test_63() {
		checkNotSubtype("int","any");
	}
	@Test public void test_64() {
		checkNotSubtype("int","null");
	}
	@Test public void test_65() {
		checkIsSubtype("int","int");
	}
	@Test public void test_66() {
		checkNotSubtype("int","[void]");
	}
	@Test public void test_67() {
		checkNotSubtype("int","[any]");
	}
	@Test public void test_68() {
		checkNotSubtype("int","[null]");
	}
	@Test public void test_69() {
		checkNotSubtype("int","[int]");
	}
	@Test public void test_70() {
		checkNotSubtype("int","[[void]]");
	}
	@Test public void test_71() {
		checkNotSubtype("int","[[any]]");
	}
	@Test public void test_72() {
		checkNotSubtype("int","[[null]]");
	}
	@Test public void test_73() {
		checkNotSubtype("int","[[int]]");
	}
	@Test public void test_74() {
		checkIsSubtype("int","void&void");
	}
	@Test public void test_75() {
		checkIsSubtype("int","void&any");
	}
	@Test public void test_76() {
		checkIsSubtype("int","void&null");
	}
	@Test public void test_77() {
		checkIsSubtype("int","void&int");
	}
	@Test public void test_78() {
		checkIsSubtype("int","any&void");
	}
	@Test public void test_79() {
		checkNotSubtype("int","any&any");
	}
	@Test public void test_80() {
		checkNotSubtype("int","any&null");
	}
	@Test public void test_81() {
		checkIsSubtype("int","any&int");
	}
	@Test public void test_82() {
		checkIsSubtype("int","null&void");
	}
	@Test public void test_83() {
		checkNotSubtype("int","null&any");
	}
	@Test public void test_84() {
		checkNotSubtype("int","null&null");
	}
	@Test public void test_85() {
		checkIsSubtype("int","null&int");
	}
	@Test public void test_86() {
		checkIsSubtype("int","int&void");
	}
	@Test public void test_87() {
		checkIsSubtype("int","int&any");
	}
	@Test public void test_88() {
		checkIsSubtype("int","int&null");
	}
	@Test public void test_89() {
		checkIsSubtype("int","int&int");
	}
	@Test public void test_90() {
		checkIsSubtype("int","[void]&void");
	}
	@Test public void test_91() {
		checkNotSubtype("int","[any]&any");
	}
	@Test public void test_92() {
		checkIsSubtype("int","[null]&null");
	}
	@Test public void test_93() {
		checkIsSubtype("int","[int]&int");
	}
	@Test public void test_94() {
		checkNotSubtype("[void]","any");
	}
	@Test public void test_95() {
		checkNotSubtype("[void]","null");
	}
	@Test public void test_96() {
		checkNotSubtype("[void]","int");
	}
	@Test public void test_97() {
		checkIsSubtype("[void]","[void]");
	}
	@Test public void test_98() {
		checkNotSubtype("[void]","[any]");
	}
	@Test public void test_99() {
		checkNotSubtype("[void]","[null]");
	}
	@Test public void test_100() {
		checkNotSubtype("[void]","[int]");
	}
	@Test public void test_101() {
		checkNotSubtype("[void]","[[void]]");
	}
	@Test public void test_102() {
		checkNotSubtype("[void]","[[any]]");
	}
	@Test public void test_103() {
		checkNotSubtype("[void]","[[null]]");
	}
	@Test public void test_104() {
		checkNotSubtype("[void]","[[int]]");
	}
	@Test public void test_105() {
		checkIsSubtype("[void]","void&void");
	}
	@Test public void test_106() {
		checkIsSubtype("[void]","void&any");
	}
	@Test public void test_107() {
		checkIsSubtype("[void]","void&null");
	}
	@Test public void test_108() {
		checkIsSubtype("[void]","void&int");
	}
	@Test public void test_109() {
		checkIsSubtype("[void]","any&void");
	}
	@Test public void test_110() {
		checkNotSubtype("[void]","any&any");
	}
	@Test public void test_111() {
		checkNotSubtype("[void]","any&null");
	}
	@Test public void test_112() {
		checkNotSubtype("[void]","any&int");
	}
	@Test public void test_113() {
		checkIsSubtype("[void]","null&void");
	}
	@Test public void test_114() {
		checkNotSubtype("[void]","null&any");
	}
	@Test public void test_115() {
		checkNotSubtype("[void]","null&null");
	}
	@Test public void test_116() {
		checkIsSubtype("[void]","null&int");
	}
	@Test public void test_117() {
		checkIsSubtype("[void]","int&void");
	}
	@Test public void test_118() {
		checkNotSubtype("[void]","int&any");
	}
	@Test public void test_119() {
		checkIsSubtype("[void]","int&null");
	}
	@Test public void test_120() {
		checkNotSubtype("[void]","int&int");
	}
	@Test public void test_121() {
		checkIsSubtype("[void]","[void]&void");
	}
	@Test public void test_122() {
		checkNotSubtype("[void]","[any]&any");
	}
	@Test public void test_123() {
		checkIsSubtype("[void]","[null]&null");
	}
	@Test public void test_124() {
		checkIsSubtype("[void]","[int]&int");
	}
	@Test public void test_125() {
		checkNotSubtype("[any]","any");
	}
	@Test public void test_126() {
		checkNotSubtype("[any]","null");
	}
	@Test public void test_127() {
		checkNotSubtype("[any]","int");
	}
	@Test public void test_128() {
		checkIsSubtype("[any]","[void]");
	}
	@Test public void test_129() {
		checkIsSubtype("[any]","[any]");
	}
	@Test public void test_130() {
		checkIsSubtype("[any]","[null]");
	}
	@Test public void test_131() {
		checkIsSubtype("[any]","[int]");
	}
	@Test public void test_132() {
		checkIsSubtype("[any]","[[void]]");
	}
	@Test public void test_133() {
		checkIsSubtype("[any]","[[any]]");
	}
	@Test public void test_134() {
		checkIsSubtype("[any]","[[null]]");
	}
	@Test public void test_135() {
		checkIsSubtype("[any]","[[int]]");
	}
	@Test public void test_136() {
		checkIsSubtype("[any]","void&void");
	}
	@Test public void test_137() {
		checkIsSubtype("[any]","void&any");
	}
	@Test public void test_138() {
		checkIsSubtype("[any]","void&null");
	}
	@Test public void test_139() {
		checkIsSubtype("[any]","void&int");
	}
	@Test public void test_140() {
		checkIsSubtype("[any]","any&void");
	}
	@Test public void test_141() {
		checkNotSubtype("[any]","any&any");
	}
	@Test public void test_142() {
		checkNotSubtype("[any]","any&null");
	}
	@Test public void test_143() {
		checkNotSubtype("[any]","any&int");
	}
	@Test public void test_144() {
		checkIsSubtype("[any]","null&void");
	}
	@Test public void test_145() {
		checkNotSubtype("[any]","null&any");
	}
	@Test public void test_146() {
		checkNotSubtype("[any]","null&null");
	}
	@Test public void test_147() {
		checkIsSubtype("[any]","null&int");
	}
	@Test public void test_148() {
		checkIsSubtype("[any]","int&void");
	}
	@Test public void test_149() {
		checkNotSubtype("[any]","int&any");
	}
	@Test public void test_150() {
		checkIsSubtype("[any]","int&null");
	}
	@Test public void test_151() {
		checkNotSubtype("[any]","int&int");
	}
	@Test public void test_152() {
		checkIsSubtype("[any]","[void]&void");
	}
	@Test public void test_153() {
		checkIsSubtype("[any]","[any]&any");
	}
	@Test public void test_154() {
		checkIsSubtype("[any]","[null]&null");
	}
	@Test public void test_155() {
		checkIsSubtype("[any]","[int]&int");
	}
	@Test public void test_156() {
		checkNotSubtype("[null]","any");
	}
	@Test public void test_157() {
		checkNotSubtype("[null]","null");
	}
	@Test public void test_158() {
		checkNotSubtype("[null]","int");
	}
	@Test public void test_159() {
		checkIsSubtype("[null]","[void]");
	}
	@Test public void test_160() {
		checkNotSubtype("[null]","[any]");
	}
	@Test public void test_161() {
		checkIsSubtype("[null]","[null]");
	}
	@Test public void test_162() {
		checkNotSubtype("[null]","[int]");
	}
	@Test public void test_163() {
		checkNotSubtype("[null]","[[void]]");
	}
	@Test public void test_164() {
		checkNotSubtype("[null]","[[any]]");
	}
	@Test public void test_165() {
		checkNotSubtype("[null]","[[null]]");
	}
	@Test public void test_166() {
		checkNotSubtype("[null]","[[int]]");
	}
	@Test public void test_167() {
		checkIsSubtype("[null]","void&void");
	}
	@Test public void test_168() {
		checkIsSubtype("[null]","void&any");
	}
	@Test public void test_169() {
		checkIsSubtype("[null]","void&null");
	}
	@Test public void test_170() {
		checkIsSubtype("[null]","void&int");
	}
	@Test public void test_171() {
		checkIsSubtype("[null]","any&void");
	}
	@Test public void test_172() {
		checkNotSubtype("[null]","any&any");
	}
	@Test public void test_173() {
		checkNotSubtype("[null]","any&null");
	}
	@Test public void test_174() {
		checkNotSubtype("[null]","any&int");
	}
	@Test public void test_175() {
		checkIsSubtype("[null]","null&void");
	}
	@Test public void test_176() {
		checkNotSubtype("[null]","null&any");
	}
	@Test public void test_177() {
		checkNotSubtype("[null]","null&null");
	}
	@Test public void test_178() {
		checkIsSubtype("[null]","null&int");
	}
	@Test public void test_179() {
		checkIsSubtype("[null]","int&void");
	}
	@Test public void test_180() {
		checkNotSubtype("[null]","int&any");
	}
	@Test public void test_181() {
		checkIsSubtype("[null]","int&null");
	}
	@Test public void test_182() {
		checkNotSubtype("[null]","int&int");
	}
	@Test public void test_183() {
		checkIsSubtype("[null]","[void]&void");
	}
	@Test public void test_184() {
		checkNotSubtype("[null]","[any]&any");
	}
	@Test public void test_185() {
		checkIsSubtype("[null]","[null]&null");
	}
	@Test public void test_186() {
		checkIsSubtype("[null]","[int]&int");
	}
	@Test public void test_187() {
		checkNotSubtype("[int]","any");
	}
	@Test public void test_188() {
		checkNotSubtype("[int]","null");
	}
	@Test public void test_189() {
		checkNotSubtype("[int]","int");
	}
	@Test public void test_190() {
		checkIsSubtype("[int]","[void]");
	}
	@Test public void test_191() {
		checkNotSubtype("[int]","[any]");
	}
	@Test public void test_192() {
		checkNotSubtype("[int]","[null]");
	}
	@Test public void test_193() {
		checkIsSubtype("[int]","[int]");
	}
	@Test public void test_194() {
		checkNotSubtype("[int]","[[void]]");
	}
	@Test public void test_195() {
		checkNotSubtype("[int]","[[any]]");
	}
	@Test public void test_196() {
		checkNotSubtype("[int]","[[null]]");
	}
	@Test public void test_197() {
		checkNotSubtype("[int]","[[int]]");
	}
	@Test public void test_198() {
		checkIsSubtype("[int]","void&void");
	}
	@Test public void test_199() {
		checkIsSubtype("[int]","void&any");
	}
	@Test public void test_200() {
		checkIsSubtype("[int]","void&null");
	}
	@Test public void test_201() {
		checkIsSubtype("[int]","void&int");
	}
	@Test public void test_202() {
		checkIsSubtype("[int]","any&void");
	}
	@Test public void test_203() {
		checkNotSubtype("[int]","any&any");
	}
	@Test public void test_204() {
		checkNotSubtype("[int]","any&null");
	}
	@Test public void test_205() {
		checkNotSubtype("[int]","any&int");
	}
	@Test public void test_206() {
		checkIsSubtype("[int]","null&void");
	}
	@Test public void test_207() {
		checkNotSubtype("[int]","null&any");
	}
	@Test public void test_208() {
		checkNotSubtype("[int]","null&null");
	}
	@Test public void test_209() {
		checkIsSubtype("[int]","null&int");
	}
	@Test public void test_210() {
		checkIsSubtype("[int]","int&void");
	}
	@Test public void test_211() {
		checkNotSubtype("[int]","int&any");
	}
	@Test public void test_212() {
		checkIsSubtype("[int]","int&null");
	}
	@Test public void test_213() {
		checkNotSubtype("[int]","int&int");
	}
	@Test public void test_214() {
		checkIsSubtype("[int]","[void]&void");
	}
	@Test public void test_215() {
		checkNotSubtype("[int]","[any]&any");
	}
	@Test public void test_216() {
		checkIsSubtype("[int]","[null]&null");
	}
	@Test public void test_217() {
		checkIsSubtype("[int]","[int]&int");
	}
	@Test public void test_218() {
		checkNotSubtype("[[void]]","any");
	}
	@Test public void test_219() {
		checkNotSubtype("[[void]]","null");
	}
	@Test public void test_220() {
		checkNotSubtype("[[void]]","int");
	}
	@Test public void test_221() {
		checkIsSubtype("[[void]]","[void]");
	}
	@Test public void test_222() {
		checkNotSubtype("[[void]]","[any]");
	}
	@Test public void test_223() {
		checkNotSubtype("[[void]]","[null]");
	}
	@Test public void test_224() {
		checkNotSubtype("[[void]]","[int]");
	}
	@Test public void test_225() {
		checkIsSubtype("[[void]]","[[void]]");
	}
	@Test public void test_226() {
		checkNotSubtype("[[void]]","[[any]]");
	}
	@Test public void test_227() {
		checkNotSubtype("[[void]]","[[null]]");
	}
	@Test public void test_228() {
		checkNotSubtype("[[void]]","[[int]]");
	}
	@Test public void test_229() {
		checkIsSubtype("[[void]]","void&void");
	}
	@Test public void test_230() {
		checkIsSubtype("[[void]]","void&any");
	}
	@Test public void test_231() {
		checkIsSubtype("[[void]]","void&null");
	}
	@Test public void test_232() {
		checkIsSubtype("[[void]]","void&int");
	}
	@Test public void test_233() {
		checkIsSubtype("[[void]]","any&void");
	}
	@Test public void test_234() {
		checkNotSubtype("[[void]]","any&any");
	}
	@Test public void test_235() {
		checkNotSubtype("[[void]]","any&null");
	}
	@Test public void test_236() {
		checkNotSubtype("[[void]]","any&int");
	}
	@Test public void test_237() {
		checkIsSubtype("[[void]]","null&void");
	}
	@Test public void test_238() {
		checkNotSubtype("[[void]]","null&any");
	}
	@Test public void test_239() {
		checkNotSubtype("[[void]]","null&null");
	}
	@Test public void test_240() {
		checkIsSubtype("[[void]]","null&int");
	}
	@Test public void test_241() {
		checkIsSubtype("[[void]]","int&void");
	}
	@Test public void test_242() {
		checkNotSubtype("[[void]]","int&any");
	}
	@Test public void test_243() {
		checkIsSubtype("[[void]]","int&null");
	}
	@Test public void test_244() {
		checkNotSubtype("[[void]]","int&int");
	}
	@Test public void test_245() {
		checkIsSubtype("[[void]]","[void]&void");
	}
	@Test public void test_246() {
		checkNotSubtype("[[void]]","[any]&any");
	}
	@Test public void test_247() {
		checkIsSubtype("[[void]]","[null]&null");
	}
	@Test public void test_248() {
		checkIsSubtype("[[void]]","[int]&int");
	}
	@Test public void test_249() {
		checkNotSubtype("[[any]]","any");
	}
	@Test public void test_250() {
		checkNotSubtype("[[any]]","null");
	}
	@Test public void test_251() {
		checkNotSubtype("[[any]]","int");
	}
	@Test public void test_252() {
		checkIsSubtype("[[any]]","[void]");
	}
	@Test public void test_253() {
		checkNotSubtype("[[any]]","[any]");
	}
	@Test public void test_254() {
		checkNotSubtype("[[any]]","[null]");
	}
	@Test public void test_255() {
		checkNotSubtype("[[any]]","[int]");
	}
	@Test public void test_256() {
		checkIsSubtype("[[any]]","[[void]]");
	}
	@Test public void test_257() {
		checkIsSubtype("[[any]]","[[any]]");
	}
	@Test public void test_258() {
		checkIsSubtype("[[any]]","[[null]]");
	}
	@Test public void test_259() {
		checkIsSubtype("[[any]]","[[int]]");
	}
	@Test public void test_260() {
		checkIsSubtype("[[any]]","void&void");
	}
	@Test public void test_261() {
		checkIsSubtype("[[any]]","void&any");
	}
	@Test public void test_262() {
		checkIsSubtype("[[any]]","void&null");
	}
	@Test public void test_263() {
		checkIsSubtype("[[any]]","void&int");
	}
	@Test public void test_264() {
		checkIsSubtype("[[any]]","any&void");
	}
	@Test public void test_265() {
		checkNotSubtype("[[any]]","any&any");
	}
	@Test public void test_266() {
		checkNotSubtype("[[any]]","any&null");
	}
	@Test public void test_267() {
		checkNotSubtype("[[any]]","any&int");
	}
	@Test public void test_268() {
		checkIsSubtype("[[any]]","null&void");
	}
	@Test public void test_269() {
		checkNotSubtype("[[any]]","null&any");
	}
	@Test public void test_270() {
		checkNotSubtype("[[any]]","null&null");
	}
	@Test public void test_271() {
		checkIsSubtype("[[any]]","null&int");
	}
	@Test public void test_272() {
		checkIsSubtype("[[any]]","int&void");
	}
	@Test public void test_273() {
		checkNotSubtype("[[any]]","int&any");
	}
	@Test public void test_274() {
		checkIsSubtype("[[any]]","int&null");
	}
	@Test public void test_275() {
		checkNotSubtype("[[any]]","int&int");
	}
	@Test public void test_276() {
		checkIsSubtype("[[any]]","[void]&void");
	}
	@Test public void test_277() {
		checkNotSubtype("[[any]]","[any]&any");
	}
	@Test public void test_278() {
		checkIsSubtype("[[any]]","[null]&null");
	}
	@Test public void test_279() {
		checkIsSubtype("[[any]]","[int]&int");
	}
	@Test public void test_280() {
		checkNotSubtype("[[null]]","any");
	}
	@Test public void test_281() {
		checkNotSubtype("[[null]]","null");
	}
	@Test public void test_282() {
		checkNotSubtype("[[null]]","int");
	}
	@Test public void test_283() {
		checkIsSubtype("[[null]]","[void]");
	}
	@Test public void test_284() {
		checkNotSubtype("[[null]]","[any]");
	}
	@Test public void test_285() {
		checkNotSubtype("[[null]]","[null]");
	}
	@Test public void test_286() {
		checkNotSubtype("[[null]]","[int]");
	}
	@Test public void test_287() {
		checkIsSubtype("[[null]]","[[void]]");
	}
	@Test public void test_288() {
		checkNotSubtype("[[null]]","[[any]]");
	}
	@Test public void test_289() {
		checkIsSubtype("[[null]]","[[null]]");
	}
	@Test public void test_290() {
		checkNotSubtype("[[null]]","[[int]]");
	}
	@Test public void test_291() {
		checkIsSubtype("[[null]]","void&void");
	}
	@Test public void test_292() {
		checkIsSubtype("[[null]]","void&any");
	}
	@Test public void test_293() {
		checkIsSubtype("[[null]]","void&null");
	}
	@Test public void test_294() {
		checkIsSubtype("[[null]]","void&int");
	}
	@Test public void test_295() {
		checkIsSubtype("[[null]]","any&void");
	}
	@Test public void test_296() {
		checkNotSubtype("[[null]]","any&any");
	}
	@Test public void test_297() {
		checkNotSubtype("[[null]]","any&null");
	}
	@Test public void test_298() {
		checkNotSubtype("[[null]]","any&int");
	}
	@Test public void test_299() {
		checkIsSubtype("[[null]]","null&void");
	}
	@Test public void test_300() {
		checkNotSubtype("[[null]]","null&any");
	}
	@Test public void test_301() {
		checkNotSubtype("[[null]]","null&null");
	}
	@Test public void test_302() {
		checkIsSubtype("[[null]]","null&int");
	}
	@Test public void test_303() {
		checkIsSubtype("[[null]]","int&void");
	}
	@Test public void test_304() {
		checkNotSubtype("[[null]]","int&any");
	}
	@Test public void test_305() {
		checkIsSubtype("[[null]]","int&null");
	}
	@Test public void test_306() {
		checkNotSubtype("[[null]]","int&int");
	}
	@Test public void test_307() {
		checkIsSubtype("[[null]]","[void]&void");
	}
	@Test public void test_308() {
		checkNotSubtype("[[null]]","[any]&any");
	}
	@Test public void test_309() {
		checkIsSubtype("[[null]]","[null]&null");
	}
	@Test public void test_310() {
		checkIsSubtype("[[null]]","[int]&int");
	}
	@Test public void test_311() {
		checkNotSubtype("[[int]]","any");
	}
	@Test public void test_312() {
		checkNotSubtype("[[int]]","null");
	}
	@Test public void test_313() {
		checkNotSubtype("[[int]]","int");
	}
	@Test public void test_314() {
		checkIsSubtype("[[int]]","[void]");
	}
	@Test public void test_315() {
		checkNotSubtype("[[int]]","[any]");
	}
	@Test public void test_316() {
		checkNotSubtype("[[int]]","[null]");
	}
	@Test public void test_317() {
		checkNotSubtype("[[int]]","[int]");
	}
	@Test public void test_318() {
		checkIsSubtype("[[int]]","[[void]]");
	}
	@Test public void test_319() {
		checkNotSubtype("[[int]]","[[any]]");
	}
	@Test public void test_320() {
		checkNotSubtype("[[int]]","[[null]]");
	}
	@Test public void test_321() {
		checkIsSubtype("[[int]]","[[int]]");
	}
	@Test public void test_322() {
		checkIsSubtype("[[int]]","void&void");
	}
	@Test public void test_323() {
		checkIsSubtype("[[int]]","void&any");
	}
	@Test public void test_324() {
		checkIsSubtype("[[int]]","void&null");
	}
	@Test public void test_325() {
		checkIsSubtype("[[int]]","void&int");
	}
	@Test public void test_326() {
		checkIsSubtype("[[int]]","any&void");
	}
	@Test public void test_327() {
		checkNotSubtype("[[int]]","any&any");
	}
	@Test public void test_328() {
		checkNotSubtype("[[int]]","any&null");
	}
	@Test public void test_329() {
		checkNotSubtype("[[int]]","any&int");
	}
	@Test public void test_330() {
		checkIsSubtype("[[int]]","null&void");
	}
	@Test public void test_331() {
		checkNotSubtype("[[int]]","null&any");
	}
	@Test public void test_332() {
		checkNotSubtype("[[int]]","null&null");
	}
	@Test public void test_333() {
		checkIsSubtype("[[int]]","null&int");
	}
	@Test public void test_334() {
		checkIsSubtype("[[int]]","int&void");
	}
	@Test public void test_335() {
		checkNotSubtype("[[int]]","int&any");
	}
	@Test public void test_336() {
		checkIsSubtype("[[int]]","int&null");
	}
	@Test public void test_337() {
		checkNotSubtype("[[int]]","int&int");
	}
	@Test public void test_338() {
		checkIsSubtype("[[int]]","[void]&void");
	}
	@Test public void test_339() {
		checkNotSubtype("[[int]]","[any]&any");
	}
	@Test public void test_340() {
		checkIsSubtype("[[int]]","[null]&null");
	}
	@Test public void test_341() {
		checkIsSubtype("[[int]]","[int]&int");
	}
	@Test public void test_342() {
		checkNotSubtype("void&void","any");
	}
	@Test public void test_343() {
		checkNotSubtype("void&void","null");
	}
	@Test public void test_344() {
		checkNotSubtype("void&void","int");
	}
	@Test public void test_345() {
		checkNotSubtype("void&void","[void]");
	}
	@Test public void test_346() {
		checkNotSubtype("void&void","[any]");
	}
	@Test public void test_347() {
		checkNotSubtype("void&void","[null]");
	}
	@Test public void test_348() {
		checkNotSubtype("void&void","[int]");
	}
	@Test public void test_349() {
		checkNotSubtype("void&void","[[void]]");
	}
	@Test public void test_350() {
		checkNotSubtype("void&void","[[any]]");
	}
	@Test public void test_351() {
		checkNotSubtype("void&void","[[null]]");
	}
	@Test public void test_352() {
		checkNotSubtype("void&void","[[int]]");
	}
	@Test public void test_353() {
		checkIsSubtype("void&void","void&void");
	}
	@Test public void test_354() {
		checkIsSubtype("void&void","void&any");
	}
	@Test public void test_355() {
		checkIsSubtype("void&void","void&null");
	}
	@Test public void test_356() {
		checkIsSubtype("void&void","void&int");
	}
	@Test public void test_357() {
		checkIsSubtype("void&void","any&void");
	}
	@Test public void test_358() {
		checkNotSubtype("void&void","any&any");
	}
	@Test public void test_359() {
		checkNotSubtype("void&void","any&null");
	}
	@Test public void test_360() {
		checkNotSubtype("void&void","any&int");
	}
	@Test public void test_361() {
		checkIsSubtype("void&void","null&void");
	}
	@Test public void test_362() {
		checkNotSubtype("void&void","null&any");
	}
	@Test public void test_363() {
		checkNotSubtype("void&void","null&null");
	}
	@Test public void test_364() {
		checkIsSubtype("void&void","null&int");
	}
	@Test public void test_365() {
		checkIsSubtype("void&void","int&void");
	}
	@Test public void test_366() {
		checkNotSubtype("void&void","int&any");
	}
	@Test public void test_367() {
		checkIsSubtype("void&void","int&null");
	}
	@Test public void test_368() {
		checkNotSubtype("void&void","int&int");
	}
	@Test public void test_369() {
		checkIsSubtype("void&void","[void]&void");
	}
	@Test public void test_370() {
		checkNotSubtype("void&void","[any]&any");
	}
	@Test public void test_371() {
		checkIsSubtype("void&void","[null]&null");
	}
	@Test public void test_372() {
		checkIsSubtype("void&void","[int]&int");
	}
	@Test public void test_373() {
		checkNotSubtype("void&any","any");
	}
	@Test public void test_374() {
		checkNotSubtype("void&any","null");
	}
	@Test public void test_375() {
		checkNotSubtype("void&any","int");
	}
	@Test public void test_376() {
		checkNotSubtype("void&any","[void]");
	}
	@Test public void test_377() {
		checkNotSubtype("void&any","[any]");
	}
	@Test public void test_378() {
		checkNotSubtype("void&any","[null]");
	}
	@Test public void test_379() {
		checkNotSubtype("void&any","[int]");
	}
	@Test public void test_380() {
		checkNotSubtype("void&any","[[void]]");
	}
	@Test public void test_381() {
		checkNotSubtype("void&any","[[any]]");
	}
	@Test public void test_382() {
		checkNotSubtype("void&any","[[null]]");
	}
	@Test public void test_383() {
		checkNotSubtype("void&any","[[int]]");
	}
	@Test public void test_384() {
		checkIsSubtype("void&any","void&void");
	}
	@Test public void test_385() {
		checkIsSubtype("void&any","void&any");
	}
	@Test public void test_386() {
		checkIsSubtype("void&any","void&null");
	}
	@Test public void test_387() {
		checkIsSubtype("void&any","void&int");
	}
	@Test public void test_388() {
		checkIsSubtype("void&any","any&void");
	}
	@Test public void test_389() {
		checkNotSubtype("void&any","any&any");
	}
	@Test public void test_390() {
		checkNotSubtype("void&any","any&null");
	}
	@Test public void test_391() {
		checkNotSubtype("void&any","any&int");
	}
	@Test public void test_392() {
		checkIsSubtype("void&any","null&void");
	}
	@Test public void test_393() {
		checkNotSubtype("void&any","null&any");
	}
	@Test public void test_394() {
		checkNotSubtype("void&any","null&null");
	}
	@Test public void test_395() {
		checkIsSubtype("void&any","null&int");
	}
	@Test public void test_396() {
		checkIsSubtype("void&any","int&void");
	}
	@Test public void test_397() {
		checkNotSubtype("void&any","int&any");
	}
	@Test public void test_398() {
		checkIsSubtype("void&any","int&null");
	}
	@Test public void test_399() {
		checkNotSubtype("void&any","int&int");
	}
	@Test public void test_400() {
		checkIsSubtype("void&any","[void]&void");
	}
	@Test public void test_401() {
		checkNotSubtype("void&any","[any]&any");
	}
	@Test public void test_402() {
		checkIsSubtype("void&any","[null]&null");
	}
	@Test public void test_403() {
		checkIsSubtype("void&any","[int]&int");
	}
	@Test public void test_404() {
		checkNotSubtype("void&null","any");
	}
	@Test public void test_405() {
		checkNotSubtype("void&null","null");
	}
	@Test public void test_406() {
		checkNotSubtype("void&null","int");
	}
	@Test public void test_407() {
		checkNotSubtype("void&null","[void]");
	}
	@Test public void test_408() {
		checkNotSubtype("void&null","[any]");
	}
	@Test public void test_409() {
		checkNotSubtype("void&null","[null]");
	}
	@Test public void test_410() {
		checkNotSubtype("void&null","[int]");
	}
	@Test public void test_411() {
		checkNotSubtype("void&null","[[void]]");
	}
	@Test public void test_412() {
		checkNotSubtype("void&null","[[any]]");
	}
	@Test public void test_413() {
		checkNotSubtype("void&null","[[null]]");
	}
	@Test public void test_414() {
		checkNotSubtype("void&null","[[int]]");
	}
	@Test public void test_415() {
		checkIsSubtype("void&null","void&void");
	}
	@Test public void test_416() {
		checkIsSubtype("void&null","void&any");
	}
	@Test public void test_417() {
		checkIsSubtype("void&null","void&null");
	}
	@Test public void test_418() {
		checkIsSubtype("void&null","void&int");
	}
	@Test public void test_419() {
		checkIsSubtype("void&null","any&void");
	}
	@Test public void test_420() {
		checkNotSubtype("void&null","any&any");
	}
	@Test public void test_421() {
		checkNotSubtype("void&null","any&null");
	}
	@Test public void test_422() {
		checkNotSubtype("void&null","any&int");
	}
	@Test public void test_423() {
		checkIsSubtype("void&null","null&void");
	}
	@Test public void test_424() {
		checkNotSubtype("void&null","null&any");
	}
	@Test public void test_425() {
		checkNotSubtype("void&null","null&null");
	}
	@Test public void test_426() {
		checkIsSubtype("void&null","null&int");
	}
	@Test public void test_427() {
		checkIsSubtype("void&null","int&void");
	}
	@Test public void test_428() {
		checkNotSubtype("void&null","int&any");
	}
	@Test public void test_429() {
		checkIsSubtype("void&null","int&null");
	}
	@Test public void test_430() {
		checkNotSubtype("void&null","int&int");
	}
	@Test public void test_431() {
		checkIsSubtype("void&null","[void]&void");
	}
	@Test public void test_432() {
		checkNotSubtype("void&null","[any]&any");
	}
	@Test public void test_433() {
		checkIsSubtype("void&null","[null]&null");
	}
	@Test public void test_434() {
		checkIsSubtype("void&null","[int]&int");
	}
	@Test public void test_435() {
		checkNotSubtype("void&int","any");
	}
	@Test public void test_436() {
		checkNotSubtype("void&int","null");
	}
	@Test public void test_437() {
		checkNotSubtype("void&int","int");
	}
	@Test public void test_438() {
		checkNotSubtype("void&int","[void]");
	}
	@Test public void test_439() {
		checkNotSubtype("void&int","[any]");
	}
	@Test public void test_440() {
		checkNotSubtype("void&int","[null]");
	}
	@Test public void test_441() {
		checkNotSubtype("void&int","[int]");
	}
	@Test public void test_442() {
		checkNotSubtype("void&int","[[void]]");
	}
	@Test public void test_443() {
		checkNotSubtype("void&int","[[any]]");
	}
	@Test public void test_444() {
		checkNotSubtype("void&int","[[null]]");
	}
	@Test public void test_445() {
		checkNotSubtype("void&int","[[int]]");
	}
	@Test public void test_446() {
		checkIsSubtype("void&int","void&void");
	}
	@Test public void test_447() {
		checkIsSubtype("void&int","void&any");
	}
	@Test public void test_448() {
		checkIsSubtype("void&int","void&null");
	}
	@Test public void test_449() {
		checkIsSubtype("void&int","void&int");
	}
	@Test public void test_450() {
		checkIsSubtype("void&int","any&void");
	}
	@Test public void test_451() {
		checkNotSubtype("void&int","any&any");
	}
	@Test public void test_452() {
		checkNotSubtype("void&int","any&null");
	}
	@Test public void test_453() {
		checkNotSubtype("void&int","any&int");
	}
	@Test public void test_454() {
		checkIsSubtype("void&int","null&void");
	}
	@Test public void test_455() {
		checkNotSubtype("void&int","null&any");
	}
	@Test public void test_456() {
		checkNotSubtype("void&int","null&null");
	}
	@Test public void test_457() {
		checkIsSubtype("void&int","null&int");
	}
	@Test public void test_458() {
		checkIsSubtype("void&int","int&void");
	}
	@Test public void test_459() {
		checkNotSubtype("void&int","int&any");
	}
	@Test public void test_460() {
		checkIsSubtype("void&int","int&null");
	}
	@Test public void test_461() {
		checkNotSubtype("void&int","int&int");
	}
	@Test public void test_462() {
		checkIsSubtype("void&int","[void]&void");
	}
	@Test public void test_463() {
		checkNotSubtype("void&int","[any]&any");
	}
	@Test public void test_464() {
		checkIsSubtype("void&int","[null]&null");
	}
	@Test public void test_465() {
		checkIsSubtype("void&int","[int]&int");
	}
	@Test public void test_466() {
		checkNotSubtype("any&void","any");
	}
	@Test public void test_467() {
		checkNotSubtype("any&void","null");
	}
	@Test public void test_468() {
		checkNotSubtype("any&void","int");
	}
	@Test public void test_469() {
		checkNotSubtype("any&void","[void]");
	}
	@Test public void test_470() {
		checkNotSubtype("any&void","[any]");
	}
	@Test public void test_471() {
		checkNotSubtype("any&void","[null]");
	}
	@Test public void test_472() {
		checkNotSubtype("any&void","[int]");
	}
	@Test public void test_473() {
		checkNotSubtype("any&void","[[void]]");
	}
	@Test public void test_474() {
		checkNotSubtype("any&void","[[any]]");
	}
	@Test public void test_475() {
		checkNotSubtype("any&void","[[null]]");
	}
	@Test public void test_476() {
		checkNotSubtype("any&void","[[int]]");
	}
	@Test public void test_477() {
		checkIsSubtype("any&void","void&void");
	}
	@Test public void test_478() {
		checkIsSubtype("any&void","void&any");
	}
	@Test public void test_479() {
		checkIsSubtype("any&void","void&null");
	}
	@Test public void test_480() {
		checkIsSubtype("any&void","void&int");
	}
	@Test public void test_481() {
		checkIsSubtype("any&void","any&void");
	}
	@Test public void test_482() {
		checkNotSubtype("any&void","any&any");
	}
	@Test public void test_483() {
		checkNotSubtype("any&void","any&null");
	}
	@Test public void test_484() {
		checkNotSubtype("any&void","any&int");
	}
	@Test public void test_485() {
		checkIsSubtype("any&void","null&void");
	}
	@Test public void test_486() {
		checkNotSubtype("any&void","null&any");
	}
	@Test public void test_487() {
		checkNotSubtype("any&void","null&null");
	}
	@Test public void test_488() {
		checkIsSubtype("any&void","null&int");
	}
	@Test public void test_489() {
		checkIsSubtype("any&void","int&void");
	}
	@Test public void test_490() {
		checkNotSubtype("any&void","int&any");
	}
	@Test public void test_491() {
		checkIsSubtype("any&void","int&null");
	}
	@Test public void test_492() {
		checkNotSubtype("any&void","int&int");
	}
	@Test public void test_493() {
		checkIsSubtype("any&void","[void]&void");
	}
	@Test public void test_494() {
		checkNotSubtype("any&void","[any]&any");
	}
	@Test public void test_495() {
		checkIsSubtype("any&void","[null]&null");
	}
	@Test public void test_496() {
		checkIsSubtype("any&void","[int]&int");
	}
	@Test public void test_497() {
		checkIsSubtype("any&any","any");
	}
	@Test public void test_498() {
		checkIsSubtype("any&any","null");
	}
	@Test public void test_499() {
		checkIsSubtype("any&any","int");
	}
	@Test public void test_500() {
		checkIsSubtype("any&any","[void]");
	}
	@Test public void test_501() {
		checkIsSubtype("any&any","[any]");
	}
	@Test public void test_502() {
		checkIsSubtype("any&any","[null]");
	}
	@Test public void test_503() {
		checkIsSubtype("any&any","[int]");
	}
	@Test public void test_504() {
		checkIsSubtype("any&any","[[void]]");
	}
	@Test public void test_505() {
		checkIsSubtype("any&any","[[any]]");
	}
	@Test public void test_506() {
		checkIsSubtype("any&any","[[null]]");
	}
	@Test public void test_507() {
		checkIsSubtype("any&any","[[int]]");
	}
	@Test public void test_508() {
		checkIsSubtype("any&any","void&void");
	}
	@Test public void test_509() {
		checkIsSubtype("any&any","void&any");
	}
	@Test public void test_510() {
		checkIsSubtype("any&any","void&null");
	}
	@Test public void test_511() {
		checkIsSubtype("any&any","void&int");
	}
	@Test public void test_512() {
		checkIsSubtype("any&any","any&void");
	}
	@Test public void test_513() {
		checkIsSubtype("any&any","any&any");
	}
	@Test public void test_514() {
		checkIsSubtype("any&any","any&null");
	}
	@Test public void test_515() {
		checkIsSubtype("any&any","any&int");
	}
	@Test public void test_516() {
		checkIsSubtype("any&any","null&void");
	}
	@Test public void test_517() {
		checkIsSubtype("any&any","null&any");
	}
	@Test public void test_518() {
		checkIsSubtype("any&any","null&null");
	}
	@Test public void test_519() {
		checkIsSubtype("any&any","null&int");
	}
	@Test public void test_520() {
		checkIsSubtype("any&any","int&void");
	}
	@Test public void test_521() {
		checkIsSubtype("any&any","int&any");
	}
	@Test public void test_522() {
		checkIsSubtype("any&any","int&null");
	}
	@Test public void test_523() {
		checkIsSubtype("any&any","int&int");
	}
	@Test public void test_524() {
		checkIsSubtype("any&any","[void]&void");
	}
	@Test public void test_525() {
		checkIsSubtype("any&any","[any]&any");
	}
	@Test public void test_526() {
		checkIsSubtype("any&any","[null]&null");
	}
	@Test public void test_527() {
		checkIsSubtype("any&any","[int]&int");
	}
	@Test public void test_528() {
		checkNotSubtype("any&null","any");
	}
	@Test public void test_529() {
		checkIsSubtype("any&null","null");
	}
	@Test public void test_530() {
		checkNotSubtype("any&null","int");
	}
	@Test public void test_531() {
		checkNotSubtype("any&null","[void]");
	}
	@Test public void test_532() {
		checkNotSubtype("any&null","[any]");
	}
	@Test public void test_533() {
		checkNotSubtype("any&null","[null]");
	}
	@Test public void test_534() {
		checkNotSubtype("any&null","[int]");
	}
	@Test public void test_535() {
		checkNotSubtype("any&null","[[void]]");
	}
	@Test public void test_536() {
		checkNotSubtype("any&null","[[any]]");
	}
	@Test public void test_537() {
		checkNotSubtype("any&null","[[null]]");
	}
	@Test public void test_538() {
		checkNotSubtype("any&null","[[int]]");
	}
	@Test public void test_539() {
		checkIsSubtype("any&null","void&void");
	}
	@Test public void test_540() {
		checkIsSubtype("any&null","void&any");
	}
	@Test public void test_541() {
		checkIsSubtype("any&null","void&null");
	}
	@Test public void test_542() {
		checkIsSubtype("any&null","void&int");
	}
	@Test public void test_543() {
		checkIsSubtype("any&null","any&void");
	}
	@Test public void test_544() {
		checkNotSubtype("any&null","any&any");
	}
	@Test public void test_545() {
		checkIsSubtype("any&null","any&null");
	}
	@Test public void test_546() {
		checkNotSubtype("any&null","any&int");
	}
	@Test public void test_547() {
		checkIsSubtype("any&null","null&void");
	}
	@Test public void test_548() {
		checkIsSubtype("any&null","null&any");
	}
	@Test public void test_549() {
		checkIsSubtype("any&null","null&null");
	}
	@Test public void test_550() {
		checkIsSubtype("any&null","null&int");
	}
	@Test public void test_551() {
		checkIsSubtype("any&null","int&void");
	}
	@Test public void test_552() {
		checkNotSubtype("any&null","int&any");
	}
	@Test public void test_553() {
		checkIsSubtype("any&null","int&null");
	}
	@Test public void test_554() {
		checkNotSubtype("any&null","int&int");
	}
	@Test public void test_555() {
		checkIsSubtype("any&null","[void]&void");
	}
	@Test public void test_556() {
		checkNotSubtype("any&null","[any]&any");
	}
	@Test public void test_557() {
		checkIsSubtype("any&null","[null]&null");
	}
	@Test public void test_558() {
		checkIsSubtype("any&null","[int]&int");
	}
	@Test public void test_559() {
		checkNotSubtype("any&int","any");
	}
	@Test public void test_560() {
		checkNotSubtype("any&int","null");
	}
	@Test public void test_561() {
		checkIsSubtype("any&int","int");
	}
	@Test public void test_562() {
		checkNotSubtype("any&int","[void]");
	}
	@Test public void test_563() {
		checkNotSubtype("any&int","[any]");
	}
	@Test public void test_564() {
		checkNotSubtype("any&int","[null]");
	}
	@Test public void test_565() {
		checkNotSubtype("any&int","[int]");
	}
	@Test public void test_566() {
		checkNotSubtype("any&int","[[void]]");
	}
	@Test public void test_567() {
		checkNotSubtype("any&int","[[any]]");
	}
	@Test public void test_568() {
		checkNotSubtype("any&int","[[null]]");
	}
	@Test public void test_569() {
		checkNotSubtype("any&int","[[int]]");
	}
	@Test public void test_570() {
		checkIsSubtype("any&int","void&void");
	}
	@Test public void test_571() {
		checkIsSubtype("any&int","void&any");
	}
	@Test public void test_572() {
		checkIsSubtype("any&int","void&null");
	}
	@Test public void test_573() {
		checkIsSubtype("any&int","void&int");
	}
	@Test public void test_574() {
		checkIsSubtype("any&int","any&void");
	}
	@Test public void test_575() {
		checkNotSubtype("any&int","any&any");
	}
	@Test public void test_576() {
		checkNotSubtype("any&int","any&null");
	}
	@Test public void test_577() {
		checkIsSubtype("any&int","any&int");
	}
	@Test public void test_578() {
		checkIsSubtype("any&int","null&void");
	}
	@Test public void test_579() {
		checkNotSubtype("any&int","null&any");
	}
	@Test public void test_580() {
		checkNotSubtype("any&int","null&null");
	}
	@Test public void test_581() {
		checkIsSubtype("any&int","null&int");
	}
	@Test public void test_582() {
		checkIsSubtype("any&int","int&void");
	}
	@Test public void test_583() {
		checkIsSubtype("any&int","int&any");
	}
	@Test public void test_584() {
		checkIsSubtype("any&int","int&null");
	}
	@Test public void test_585() {
		checkIsSubtype("any&int","int&int");
	}
	@Test public void test_586() {
		checkIsSubtype("any&int","[void]&void");
	}
	@Test public void test_587() {
		checkNotSubtype("any&int","[any]&any");
	}
	@Test public void test_588() {
		checkIsSubtype("any&int","[null]&null");
	}
	@Test public void test_589() {
		checkIsSubtype("any&int","[int]&int");
	}
	@Test public void test_590() {
		checkNotSubtype("null&void","any");
	}
	@Test public void test_591() {
		checkNotSubtype("null&void","null");
	}
	@Test public void test_592() {
		checkNotSubtype("null&void","int");
	}
	@Test public void test_593() {
		checkNotSubtype("null&void","[void]");
	}
	@Test public void test_594() {
		checkNotSubtype("null&void","[any]");
	}
	@Test public void test_595() {
		checkNotSubtype("null&void","[null]");
	}
	@Test public void test_596() {
		checkNotSubtype("null&void","[int]");
	}
	@Test public void test_597() {
		checkNotSubtype("null&void","[[void]]");
	}
	@Test public void test_598() {
		checkNotSubtype("null&void","[[any]]");
	}
	@Test public void test_599() {
		checkNotSubtype("null&void","[[null]]");
	}
	@Test public void test_600() {
		checkNotSubtype("null&void","[[int]]");
	}
	@Test public void test_601() {
		checkIsSubtype("null&void","void&void");
	}
	@Test public void test_602() {
		checkIsSubtype("null&void","void&any");
	}
	@Test public void test_603() {
		checkIsSubtype("null&void","void&null");
	}
	@Test public void test_604() {
		checkIsSubtype("null&void","void&int");
	}
	@Test public void test_605() {
		checkIsSubtype("null&void","any&void");
	}
	@Test public void test_606() {
		checkNotSubtype("null&void","any&any");
	}
	@Test public void test_607() {
		checkNotSubtype("null&void","any&null");
	}
	@Test public void test_608() {
		checkNotSubtype("null&void","any&int");
	}
	@Test public void test_609() {
		checkIsSubtype("null&void","null&void");
	}
	@Test public void test_610() {
		checkNotSubtype("null&void","null&any");
	}
	@Test public void test_611() {
		checkNotSubtype("null&void","null&null");
	}
	@Test public void test_612() {
		checkIsSubtype("null&void","null&int");
	}
	@Test public void test_613() {
		checkIsSubtype("null&void","int&void");
	}
	@Test public void test_614() {
		checkNotSubtype("null&void","int&any");
	}
	@Test public void test_615() {
		checkIsSubtype("null&void","int&null");
	}
	@Test public void test_616() {
		checkNotSubtype("null&void","int&int");
	}
	@Test public void test_617() {
		checkIsSubtype("null&void","[void]&void");
	}
	@Test public void test_618() {
		checkNotSubtype("null&void","[any]&any");
	}
	@Test public void test_619() {
		checkIsSubtype("null&void","[null]&null");
	}
	@Test public void test_620() {
		checkIsSubtype("null&void","[int]&int");
	}
	@Test public void test_621() {
		checkNotSubtype("null&any","any");
	}
	@Test public void test_622() {
		checkIsSubtype("null&any","null");
	}
	@Test public void test_623() {
		checkNotSubtype("null&any","int");
	}
	@Test public void test_624() {
		checkNotSubtype("null&any","[void]");
	}
	@Test public void test_625() {
		checkNotSubtype("null&any","[any]");
	}
	@Test public void test_626() {
		checkNotSubtype("null&any","[null]");
	}
	@Test public void test_627() {
		checkNotSubtype("null&any","[int]");
	}
	@Test public void test_628() {
		checkNotSubtype("null&any","[[void]]");
	}
	@Test public void test_629() {
		checkNotSubtype("null&any","[[any]]");
	}
	@Test public void test_630() {
		checkNotSubtype("null&any","[[null]]");
	}
	@Test public void test_631() {
		checkNotSubtype("null&any","[[int]]");
	}
	@Test public void test_632() {
		checkIsSubtype("null&any","void&void");
	}
	@Test public void test_633() {
		checkIsSubtype("null&any","void&any");
	}
	@Test public void test_634() {
		checkIsSubtype("null&any","void&null");
	}
	@Test public void test_635() {
		checkIsSubtype("null&any","void&int");
	}
	@Test public void test_636() {
		checkIsSubtype("null&any","any&void");
	}
	@Test public void test_637() {
		checkNotSubtype("null&any","any&any");
	}
	@Test public void test_638() {
		checkIsSubtype("null&any","any&null");
	}
	@Test public void test_639() {
		checkNotSubtype("null&any","any&int");
	}
	@Test public void test_640() {
		checkIsSubtype("null&any","null&void");
	}
	@Test public void test_641() {
		checkIsSubtype("null&any","null&any");
	}
	@Test public void test_642() {
		checkIsSubtype("null&any","null&null");
	}
	@Test public void test_643() {
		checkIsSubtype("null&any","null&int");
	}
	@Test public void test_644() {
		checkIsSubtype("null&any","int&void");
	}
	@Test public void test_645() {
		checkNotSubtype("null&any","int&any");
	}
	@Test public void test_646() {
		checkIsSubtype("null&any","int&null");
	}
	@Test public void test_647() {
		checkNotSubtype("null&any","int&int");
	}
	@Test public void test_648() {
		checkIsSubtype("null&any","[void]&void");
	}
	@Test public void test_649() {
		checkNotSubtype("null&any","[any]&any");
	}
	@Test public void test_650() {
		checkIsSubtype("null&any","[null]&null");
	}
	@Test public void test_651() {
		checkIsSubtype("null&any","[int]&int");
	}
	@Test public void test_652() {
		checkNotSubtype("null&null","any");
	}
	@Test public void test_653() {
		checkIsSubtype("null&null","null");
	}
	@Test public void test_654() {
		checkNotSubtype("null&null","int");
	}
	@Test public void test_655() {
		checkNotSubtype("null&null","[void]");
	}
	@Test public void test_656() {
		checkNotSubtype("null&null","[any]");
	}
	@Test public void test_657() {
		checkNotSubtype("null&null","[null]");
	}
	@Test public void test_658() {
		checkNotSubtype("null&null","[int]");
	}
	@Test public void test_659() {
		checkNotSubtype("null&null","[[void]]");
	}
	@Test public void test_660() {
		checkNotSubtype("null&null","[[any]]");
	}
	@Test public void test_661() {
		checkNotSubtype("null&null","[[null]]");
	}
	@Test public void test_662() {
		checkNotSubtype("null&null","[[int]]");
	}
	@Test public void test_663() {
		checkIsSubtype("null&null","void&void");
	}
	@Test public void test_664() {
		checkIsSubtype("null&null","void&any");
	}
	@Test public void test_665() {
		checkIsSubtype("null&null","void&null");
	}
	@Test public void test_666() {
		checkIsSubtype("null&null","void&int");
	}
	@Test public void test_667() {
		checkIsSubtype("null&null","any&void");
	}
	@Test public void test_668() {
		checkNotSubtype("null&null","any&any");
	}
	@Test public void test_669() {
		checkIsSubtype("null&null","any&null");
	}
	@Test public void test_670() {
		checkNotSubtype("null&null","any&int");
	}
	@Test public void test_671() {
		checkIsSubtype("null&null","null&void");
	}
	@Test public void test_672() {
		checkIsSubtype("null&null","null&any");
	}
	@Test public void test_673() {
		checkIsSubtype("null&null","null&null");
	}
	@Test public void test_674() {
		checkIsSubtype("null&null","null&int");
	}
	@Test public void test_675() {
		checkIsSubtype("null&null","int&void");
	}
	@Test public void test_676() {
		checkNotSubtype("null&null","int&any");
	}
	@Test public void test_677() {
		checkIsSubtype("null&null","int&null");
	}
	@Test public void test_678() {
		checkNotSubtype("null&null","int&int");
	}
	@Test public void test_679() {
		checkIsSubtype("null&null","[void]&void");
	}
	@Test public void test_680() {
		checkNotSubtype("null&null","[any]&any");
	}
	@Test public void test_681() {
		checkIsSubtype("null&null","[null]&null");
	}
	@Test public void test_682() {
		checkIsSubtype("null&null","[int]&int");
	}
	@Test public void test_683() {
		checkNotSubtype("null&int","any");
	}
	@Test public void test_684() {
		checkNotSubtype("null&int","null");
	}
	@Test public void test_685() {
		checkNotSubtype("null&int","int");
	}
	@Test public void test_686() {
		checkNotSubtype("null&int","[void]");
	}
	@Test public void test_687() {
		checkNotSubtype("null&int","[any]");
	}
	@Test public void test_688() {
		checkNotSubtype("null&int","[null]");
	}
	@Test public void test_689() {
		checkNotSubtype("null&int","[int]");
	}
	@Test public void test_690() {
		checkNotSubtype("null&int","[[void]]");
	}
	@Test public void test_691() {
		checkNotSubtype("null&int","[[any]]");
	}
	@Test public void test_692() {
		checkNotSubtype("null&int","[[null]]");
	}
	@Test public void test_693() {
		checkNotSubtype("null&int","[[int]]");
	}
	@Test public void test_694() {
		checkIsSubtype("null&int","void&void");
	}
	@Test public void test_695() {
		checkIsSubtype("null&int","void&any");
	}
	@Test public void test_696() {
		checkIsSubtype("null&int","void&null");
	}
	@Test public void test_697() {
		checkIsSubtype("null&int","void&int");
	}
	@Test public void test_698() {
		checkIsSubtype("null&int","any&void");
	}
	@Test public void test_699() {
		checkNotSubtype("null&int","any&any");
	}
	@Test public void test_700() {
		checkNotSubtype("null&int","any&null");
	}
	@Test public void test_701() {
		checkNotSubtype("null&int","any&int");
	}
	@Test public void test_702() {
		checkIsSubtype("null&int","null&void");
	}
	@Test public void test_703() {
		checkNotSubtype("null&int","null&any");
	}
	@Test public void test_704() {
		checkNotSubtype("null&int","null&null");
	}
	@Test public void test_705() {
		checkIsSubtype("null&int","null&int");
	}
	@Test public void test_706() {
		checkIsSubtype("null&int","int&void");
	}
	@Test public void test_707() {
		checkNotSubtype("null&int","int&any");
	}
	@Test public void test_708() {
		checkIsSubtype("null&int","int&null");
	}
	@Test public void test_709() {
		checkNotSubtype("null&int","int&int");
	}
	@Test public void test_710() {
		checkIsSubtype("null&int","[void]&void");
	}
	@Test public void test_711() {
		checkNotSubtype("null&int","[any]&any");
	}
	@Test public void test_712() {
		checkIsSubtype("null&int","[null]&null");
	}
	@Test public void test_713() {
		checkIsSubtype("null&int","[int]&int");
	}
	@Test public void test_714() {
		checkNotSubtype("int&void","any");
	}
	@Test public void test_715() {
		checkNotSubtype("int&void","null");
	}
	@Test public void test_716() {
		checkNotSubtype("int&void","int");
	}
	@Test public void test_717() {
		checkNotSubtype("int&void","[void]");
	}
	@Test public void test_718() {
		checkNotSubtype("int&void","[any]");
	}
	@Test public void test_719() {
		checkNotSubtype("int&void","[null]");
	}
	@Test public void test_720() {
		checkNotSubtype("int&void","[int]");
	}
	@Test public void test_721() {
		checkNotSubtype("int&void","[[void]]");
	}
	@Test public void test_722() {
		checkNotSubtype("int&void","[[any]]");
	}
	@Test public void test_723() {
		checkNotSubtype("int&void","[[null]]");
	}
	@Test public void test_724() {
		checkNotSubtype("int&void","[[int]]");
	}
	@Test public void test_725() {
		checkIsSubtype("int&void","void&void");
	}
	@Test public void test_726() {
		checkIsSubtype("int&void","void&any");
	}
	@Test public void test_727() {
		checkIsSubtype("int&void","void&null");
	}
	@Test public void test_728() {
		checkIsSubtype("int&void","void&int");
	}
	@Test public void test_729() {
		checkIsSubtype("int&void","any&void");
	}
	@Test public void test_730() {
		checkNotSubtype("int&void","any&any");
	}
	@Test public void test_731() {
		checkNotSubtype("int&void","any&null");
	}
	@Test public void test_732() {
		checkNotSubtype("int&void","any&int");
	}
	@Test public void test_733() {
		checkIsSubtype("int&void","null&void");
	}
	@Test public void test_734() {
		checkNotSubtype("int&void","null&any");
	}
	@Test public void test_735() {
		checkNotSubtype("int&void","null&null");
	}
	@Test public void test_736() {
		checkIsSubtype("int&void","null&int");
	}
	@Test public void test_737() {
		checkIsSubtype("int&void","int&void");
	}
	@Test public void test_738() {
		checkNotSubtype("int&void","int&any");
	}
	@Test public void test_739() {
		checkIsSubtype("int&void","int&null");
	}
	@Test public void test_740() {
		checkNotSubtype("int&void","int&int");
	}
	@Test public void test_741() {
		checkIsSubtype("int&void","[void]&void");
	}
	@Test public void test_742() {
		checkNotSubtype("int&void","[any]&any");
	}
	@Test public void test_743() {
		checkIsSubtype("int&void","[null]&null");
	}
	@Test public void test_744() {
		checkIsSubtype("int&void","[int]&int");
	}
	@Test public void test_745() {
		checkNotSubtype("int&any","any");
	}
	@Test public void test_746() {
		checkNotSubtype("int&any","null");
	}
	@Test public void test_747() {
		checkIsSubtype("int&any","int");
	}
	@Test public void test_748() {
		checkNotSubtype("int&any","[void]");
	}
	@Test public void test_749() {
		checkNotSubtype("int&any","[any]");
	}
	@Test public void test_750() {
		checkNotSubtype("int&any","[null]");
	}
	@Test public void test_751() {
		checkNotSubtype("int&any","[int]");
	}
	@Test public void test_752() {
		checkNotSubtype("int&any","[[void]]");
	}
	@Test public void test_753() {
		checkNotSubtype("int&any","[[any]]");
	}
	@Test public void test_754() {
		checkNotSubtype("int&any","[[null]]");
	}
	@Test public void test_755() {
		checkNotSubtype("int&any","[[int]]");
	}
	@Test public void test_756() {
		checkIsSubtype("int&any","void&void");
	}
	@Test public void test_757() {
		checkIsSubtype("int&any","void&any");
	}
	@Test public void test_758() {
		checkIsSubtype("int&any","void&null");
	}
	@Test public void test_759() {
		checkIsSubtype("int&any","void&int");
	}
	@Test public void test_760() {
		checkIsSubtype("int&any","any&void");
	}
	@Test public void test_761() {
		checkNotSubtype("int&any","any&any");
	}
	@Test public void test_762() {
		checkNotSubtype("int&any","any&null");
	}
	@Test public void test_763() {
		checkIsSubtype("int&any","any&int");
	}
	@Test public void test_764() {
		checkIsSubtype("int&any","null&void");
	}
	@Test public void test_765() {
		checkNotSubtype("int&any","null&any");
	}
	@Test public void test_766() {
		checkNotSubtype("int&any","null&null");
	}
	@Test public void test_767() {
		checkIsSubtype("int&any","null&int");
	}
	@Test public void test_768() {
		checkIsSubtype("int&any","int&void");
	}
	@Test public void test_769() {
		checkIsSubtype("int&any","int&any");
	}
	@Test public void test_770() {
		checkIsSubtype("int&any","int&null");
	}
	@Test public void test_771() {
		checkIsSubtype("int&any","int&int");
	}
	@Test public void test_772() {
		checkIsSubtype("int&any","[void]&void");
	}
	@Test public void test_773() {
		checkNotSubtype("int&any","[any]&any");
	}
	@Test public void test_774() {
		checkIsSubtype("int&any","[null]&null");
	}
	@Test public void test_775() {
		checkIsSubtype("int&any","[int]&int");
	}
	@Test public void test_776() {
		checkNotSubtype("int&null","any");
	}
	@Test public void test_777() {
		checkNotSubtype("int&null","null");
	}
	@Test public void test_778() {
		checkNotSubtype("int&null","int");
	}
	@Test public void test_779() {
		checkNotSubtype("int&null","[void]");
	}
	@Test public void test_780() {
		checkNotSubtype("int&null","[any]");
	}
	@Test public void test_781() {
		checkNotSubtype("int&null","[null]");
	}
	@Test public void test_782() {
		checkNotSubtype("int&null","[int]");
	}
	@Test public void test_783() {
		checkNotSubtype("int&null","[[void]]");
	}
	@Test public void test_784() {
		checkNotSubtype("int&null","[[any]]");
	}
	@Test public void test_785() {
		checkNotSubtype("int&null","[[null]]");
	}
	@Test public void test_786() {
		checkNotSubtype("int&null","[[int]]");
	}
	@Test public void test_787() {
		checkIsSubtype("int&null","void&void");
	}
	@Test public void test_788() {
		checkIsSubtype("int&null","void&any");
	}
	@Test public void test_789() {
		checkIsSubtype("int&null","void&null");
	}
	@Test public void test_790() {
		checkIsSubtype("int&null","void&int");
	}
	@Test public void test_791() {
		checkIsSubtype("int&null","any&void");
	}
	@Test public void test_792() {
		checkNotSubtype("int&null","any&any");
	}
	@Test public void test_793() {
		checkNotSubtype("int&null","any&null");
	}
	@Test public void test_794() {
		checkNotSubtype("int&null","any&int");
	}
	@Test public void test_795() {
		checkIsSubtype("int&null","null&void");
	}
	@Test public void test_796() {
		checkNotSubtype("int&null","null&any");
	}
	@Test public void test_797() {
		checkNotSubtype("int&null","null&null");
	}
	@Test public void test_798() {
		checkIsSubtype("int&null","null&int");
	}
	@Test public void test_799() {
		checkIsSubtype("int&null","int&void");
	}
	@Test public void test_800() {
		checkNotSubtype("int&null","int&any");
	}
	@Test public void test_801() {
		checkIsSubtype("int&null","int&null");
	}
	@Test public void test_802() {
		checkNotSubtype("int&null","int&int");
	}
	@Test public void test_803() {
		checkIsSubtype("int&null","[void]&void");
	}
	@Test public void test_804() {
		checkNotSubtype("int&null","[any]&any");
	}
	@Test public void test_805() {
		checkIsSubtype("int&null","[null]&null");
	}
	@Test public void test_806() {
		checkIsSubtype("int&null","[int]&int");
	}
	@Test public void test_807() {
		checkNotSubtype("int&int","any");
	}
	@Test public void test_808() {
		checkNotSubtype("int&int","null");
	}
	@Test public void test_809() {
		checkIsSubtype("int&int","int");
	}
	@Test public void test_810() {
		checkNotSubtype("int&int","[void]");
	}
	@Test public void test_811() {
		checkNotSubtype("int&int","[any]");
	}
	@Test public void test_812() {
		checkNotSubtype("int&int","[null]");
	}
	@Test public void test_813() {
		checkNotSubtype("int&int","[int]");
	}
	@Test public void test_814() {
		checkNotSubtype("int&int","[[void]]");
	}
	@Test public void test_815() {
		checkNotSubtype("int&int","[[any]]");
	}
	@Test public void test_816() {
		checkNotSubtype("int&int","[[null]]");
	}
	@Test public void test_817() {
		checkNotSubtype("int&int","[[int]]");
	}
	@Test public void test_818() {
		checkIsSubtype("int&int","void&void");
	}
	@Test public void test_819() {
		checkIsSubtype("int&int","void&any");
	}
	@Test public void test_820() {
		checkIsSubtype("int&int","void&null");
	}
	@Test public void test_821() {
		checkIsSubtype("int&int","void&int");
	}
	@Test public void test_822() {
		checkIsSubtype("int&int","any&void");
	}
	@Test public void test_823() {
		checkNotSubtype("int&int","any&any");
	}
	@Test public void test_824() {
		checkNotSubtype("int&int","any&null");
	}
	@Test public void test_825() {
		checkIsSubtype("int&int","any&int");
	}
	@Test public void test_826() {
		checkIsSubtype("int&int","null&void");
	}
	@Test public void test_827() {
		checkNotSubtype("int&int","null&any");
	}
	@Test public void test_828() {
		checkNotSubtype("int&int","null&null");
	}
	@Test public void test_829() {
		checkIsSubtype("int&int","null&int");
	}
	@Test public void test_830() {
		checkIsSubtype("int&int","int&void");
	}
	@Test public void test_831() {
		checkIsSubtype("int&int","int&any");
	}
	@Test public void test_832() {
		checkIsSubtype("int&int","int&null");
	}
	@Test public void test_833() {
		checkIsSubtype("int&int","int&int");
	}
	@Test public void test_834() {
		checkIsSubtype("int&int","[void]&void");
	}
	@Test public void test_835() {
		checkNotSubtype("int&int","[any]&any");
	}
	@Test public void test_836() {
		checkIsSubtype("int&int","[null]&null");
	}
	@Test public void test_837() {
		checkIsSubtype("int&int","[int]&int");
	}
	@Test public void test_838() {
		checkNotSubtype("[void]&void","any");
	}
	@Test public void test_839() {
		checkNotSubtype("[void]&void","null");
	}
	@Test public void test_840() {
		checkNotSubtype("[void]&void","int");
	}
	@Test public void test_841() {
		checkNotSubtype("[void]&void","[void]");
	}
	@Test public void test_842() {
		checkNotSubtype("[void]&void","[any]");
	}
	@Test public void test_843() {
		checkNotSubtype("[void]&void","[null]");
	}
	@Test public void test_844() {
		checkNotSubtype("[void]&void","[int]");
	}
	@Test public void test_845() {
		checkNotSubtype("[void]&void","[[void]]");
	}
	@Test public void test_846() {
		checkNotSubtype("[void]&void","[[any]]");
	}
	@Test public void test_847() {
		checkNotSubtype("[void]&void","[[null]]");
	}
	@Test public void test_848() {
		checkNotSubtype("[void]&void","[[int]]");
	}
	@Test public void test_849() {
		checkIsSubtype("[void]&void","void&void");
	}
	@Test public void test_850() {
		checkIsSubtype("[void]&void","void&any");
	}
	@Test public void test_851() {
		checkIsSubtype("[void]&void","void&null");
	}
	@Test public void test_852() {
		checkIsSubtype("[void]&void","void&int");
	}
	@Test public void test_853() {
		checkIsSubtype("[void]&void","any&void");
	}
	@Test public void test_854() {
		checkNotSubtype("[void]&void","any&any");
	}
	@Test public void test_855() {
		checkNotSubtype("[void]&void","any&null");
	}
	@Test public void test_856() {
		checkNotSubtype("[void]&void","any&int");
	}
	@Test public void test_857() {
		checkIsSubtype("[void]&void","null&void");
	}
	@Test public void test_858() {
		checkNotSubtype("[void]&void","null&any");
	}
	@Test public void test_859() {
		checkNotSubtype("[void]&void","null&null");
	}
	@Test public void test_860() {
		checkIsSubtype("[void]&void","null&int");
	}
	@Test public void test_861() {
		checkIsSubtype("[void]&void","int&void");
	}
	@Test public void test_862() {
		checkNotSubtype("[void]&void","int&any");
	}
	@Test public void test_863() {
		checkIsSubtype("[void]&void","int&null");
	}
	@Test public void test_864() {
		checkNotSubtype("[void]&void","int&int");
	}
	@Test public void test_865() {
		checkIsSubtype("[void]&void","[void]&void");
	}
	@Test public void test_866() {
		checkNotSubtype("[void]&void","[any]&any");
	}
	@Test public void test_867() {
		checkIsSubtype("[void]&void","[null]&null");
	}
	@Test public void test_868() {
		checkIsSubtype("[void]&void","[int]&int");
	}
	@Test public void test_869() {
		checkNotSubtype("[any]&any","any");
	}
	@Test public void test_870() {
		checkNotSubtype("[any]&any","null");
	}
	@Test public void test_871() {
		checkNotSubtype("[any]&any","int");
	}
	@Test public void test_872() {
		checkIsSubtype("[any]&any","[void]");
	}
	@Test public void test_873() {
		checkIsSubtype("[any]&any","[any]");
	}
	@Test public void test_874() {
		checkIsSubtype("[any]&any","[null]");
	}
	@Test public void test_875() {
		checkIsSubtype("[any]&any","[int]");
	}
	@Test public void test_876() {
		checkIsSubtype("[any]&any","[[void]]");
	}
	@Test public void test_877() {
		checkIsSubtype("[any]&any","[[any]]");
	}
	@Test public void test_878() {
		checkIsSubtype("[any]&any","[[null]]");
	}
	@Test public void test_879() {
		checkIsSubtype("[any]&any","[[int]]");
	}
	@Test public void test_880() {
		checkIsSubtype("[any]&any","void&void");
	}
	@Test public void test_881() {
		checkIsSubtype("[any]&any","void&any");
	}
	@Test public void test_882() {
		checkIsSubtype("[any]&any","void&null");
	}
	@Test public void test_883() {
		checkIsSubtype("[any]&any","void&int");
	}
	@Test public void test_884() {
		checkIsSubtype("[any]&any","any&void");
	}
	@Test public void test_885() {
		checkNotSubtype("[any]&any","any&any");
	}
	@Test public void test_886() {
		checkNotSubtype("[any]&any","any&null");
	}
	@Test public void test_887() {
		checkNotSubtype("[any]&any","any&int");
	}
	@Test public void test_888() {
		checkIsSubtype("[any]&any","null&void");
	}
	@Test public void test_889() {
		checkNotSubtype("[any]&any","null&any");
	}
	@Test public void test_890() {
		checkNotSubtype("[any]&any","null&null");
	}
	@Test public void test_891() {
		checkIsSubtype("[any]&any","null&int");
	}
	@Test public void test_892() {
		checkIsSubtype("[any]&any","int&void");
	}
	@Test public void test_893() {
		checkNotSubtype("[any]&any","int&any");
	}
	@Test public void test_894() {
		checkIsSubtype("[any]&any","int&null");
	}
	@Test public void test_895() {
		checkNotSubtype("[any]&any","int&int");
	}
	@Test public void test_896() {
		checkIsSubtype("[any]&any","[void]&void");
	}
	@Test public void test_897() {
		checkIsSubtype("[any]&any","[any]&any");
	}
	@Test public void test_898() {
		checkIsSubtype("[any]&any","[null]&null");
	}
	@Test public void test_899() {
		checkIsSubtype("[any]&any","[int]&int");
	}
	@Test public void test_900() {
		checkNotSubtype("[null]&null","any");
	}
	@Test public void test_901() {
		checkNotSubtype("[null]&null","null");
	}
	@Test public void test_902() {
		checkNotSubtype("[null]&null","int");
	}
	@Test public void test_903() {
		checkNotSubtype("[null]&null","[void]");
	}
	@Test public void test_904() {
		checkNotSubtype("[null]&null","[any]");
	}
	@Test public void test_905() {
		checkNotSubtype("[null]&null","[null]");
	}
	@Test public void test_906() {
		checkNotSubtype("[null]&null","[int]");
	}
	@Test public void test_907() {
		checkNotSubtype("[null]&null","[[void]]");
	}
	@Test public void test_908() {
		checkNotSubtype("[null]&null","[[any]]");
	}
	@Test public void test_909() {
		checkNotSubtype("[null]&null","[[null]]");
	}
	@Test public void test_910() {
		checkNotSubtype("[null]&null","[[int]]");
	}
	@Test public void test_911() {
		checkIsSubtype("[null]&null","void&void");
	}
	@Test public void test_912() {
		checkIsSubtype("[null]&null","void&any");
	}
	@Test public void test_913() {
		checkIsSubtype("[null]&null","void&null");
	}
	@Test public void test_914() {
		checkIsSubtype("[null]&null","void&int");
	}
	@Test public void test_915() {
		checkIsSubtype("[null]&null","any&void");
	}
	@Test public void test_916() {
		checkNotSubtype("[null]&null","any&any");
	}
	@Test public void test_917() {
		checkNotSubtype("[null]&null","any&null");
	}
	@Test public void test_918() {
		checkNotSubtype("[null]&null","any&int");
	}
	@Test public void test_919() {
		checkIsSubtype("[null]&null","null&void");
	}
	@Test public void test_920() {
		checkNotSubtype("[null]&null","null&any");
	}
	@Test public void test_921() {
		checkNotSubtype("[null]&null","null&null");
	}
	@Test public void test_922() {
		checkIsSubtype("[null]&null","null&int");
	}
	@Test public void test_923() {
		checkIsSubtype("[null]&null","int&void");
	}
	@Test public void test_924() {
		checkNotSubtype("[null]&null","int&any");
	}
	@Test public void test_925() {
		checkIsSubtype("[null]&null","int&null");
	}
	@Test public void test_926() {
		checkNotSubtype("[null]&null","int&int");
	}
	@Test public void test_927() {
		checkIsSubtype("[null]&null","[void]&void");
	}
	@Test public void test_928() {
		checkNotSubtype("[null]&null","[any]&any");
	}
	@Test public void test_929() {
		checkIsSubtype("[null]&null","[null]&null");
	}
	@Test public void test_930() {
		checkIsSubtype("[null]&null","[int]&int");
	}
	@Test public void test_931() {
		checkNotSubtype("[int]&int","any");
	}
	@Test public void test_932() {
		checkNotSubtype("[int]&int","null");
	}
	@Test public void test_933() {
		checkNotSubtype("[int]&int","int");
	}
	@Test public void test_934() {
		checkNotSubtype("[int]&int","[void]");
	}
	@Test public void test_935() {
		checkNotSubtype("[int]&int","[any]");
	}
	@Test public void test_936() {
		checkNotSubtype("[int]&int","[null]");
	}
	@Test public void test_937() {
		checkNotSubtype("[int]&int","[int]");
	}
	@Test public void test_938() {
		checkNotSubtype("[int]&int","[[void]]");
	}
	@Test public void test_939() {
		checkNotSubtype("[int]&int","[[any]]");
	}
	@Test public void test_940() {
		checkNotSubtype("[int]&int","[[null]]");
	}
	@Test public void test_941() {
		checkNotSubtype("[int]&int","[[int]]");
	}
	@Test public void test_942() {
		checkIsSubtype("[int]&int","void&void");
	}
	@Test public void test_943() {
		checkIsSubtype("[int]&int","void&any");
	}
	@Test public void test_944() {
		checkIsSubtype("[int]&int","void&null");
	}
	@Test public void test_945() {
		checkIsSubtype("[int]&int","void&int");
	}
	@Test public void test_946() {
		checkIsSubtype("[int]&int","any&void");
	}
	@Test public void test_947() {
		checkNotSubtype("[int]&int","any&any");
	}
	@Test public void test_948() {
		checkNotSubtype("[int]&int","any&null");
	}
	@Test public void test_949() {
		checkNotSubtype("[int]&int","any&int");
	}
	@Test public void test_950() {
		checkIsSubtype("[int]&int","null&void");
	}
	@Test public void test_951() {
		checkNotSubtype("[int]&int","null&any");
	}
	@Test public void test_952() {
		checkNotSubtype("[int]&int","null&null");
	}
	@Test public void test_953() {
		checkIsSubtype("[int]&int","null&int");
	}
	@Test public void test_954() {
		checkIsSubtype("[int]&int","int&void");
	}
	@Test public void test_955() {
		checkNotSubtype("[int]&int","int&any");
	}
	@Test public void test_956() {
		checkIsSubtype("[int]&int","int&null");
	}
	@Test public void test_957() {
		checkNotSubtype("[int]&int","int&int");
	}
	@Test public void test_958() {
		checkIsSubtype("[int]&int","[void]&void");
	}
	@Test public void test_959() {
		checkNotSubtype("[int]&int","[any]&any");
	}
	@Test public void test_960() {
		checkIsSubtype("[int]&int","[null]&null");
	}
	@Test public void test_961() {
		checkIsSubtype("[int]&int","[int]&int");
	}

	private void checkIsSubtype(String from, String to) {
		Type ft = Type.fromString(from);
		Type tt = Type.fromString(to);
		assertTrue(Type.isSubtype(ft,tt));
	}
	private void checkNotSubtype(String from, String to) {
		Type ft = Type.fromString(from);
		Type tt = Type.fromString(to);
		assertFalse(Type.isSubtype(ft,tt));
	}
}
