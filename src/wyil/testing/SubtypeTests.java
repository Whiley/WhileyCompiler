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
		checkIsSubtype("any","!void");
	}
	@Test public void test_9() {
		checkIsSubtype("any","!any");
	}
	@Test public void test_10() {
		checkIsSubtype("any","!null");
	}
	@Test public void test_11() {
		checkIsSubtype("any","!int");
	}
	@Test public void test_12() {
		checkIsSubtype("any","[[void]]");
	}
	@Test public void test_13() {
		checkIsSubtype("any","[[any]]");
	}
	@Test public void test_14() {
		checkIsSubtype("any","[[null]]");
	}
	@Test public void test_15() {
		checkIsSubtype("any","[[int]]");
	}
	@Test public void test_16() {
		checkIsSubtype("any","[!void]");
	}
	@Test public void test_17() {
		checkIsSubtype("any","[!any]");
	}
	@Test public void test_18() {
		checkIsSubtype("any","[!null]");
	}
	@Test public void test_19() {
		checkIsSubtype("any","[!int]");
	}
	@Test public void test_20() {
		checkIsSubtype("any","![void]");
	}
	@Test public void test_21() {
		checkIsSubtype("any","![any]");
	}
	@Test public void test_22() {
		checkIsSubtype("any","![null]");
	}
	@Test public void test_23() {
		checkIsSubtype("any","![int]");
	}
	@Test public void test_24() {
		checkIsSubtype("any","!!void");
	}
	@Test public void test_25() {
		checkIsSubtype("any","!!any");
	}
	@Test public void test_26() {
		checkIsSubtype("any","!!null");
	}
	@Test public void test_27() {
		checkIsSubtype("any","!!int");
	}
	@Test public void test_28() {
		checkNotSubtype("null","any");
	}
	@Test public void test_29() {
		checkIsSubtype("null","null");
	}
	@Test public void test_30() {
		checkNotSubtype("null","int");
	}
	@Test public void test_31() {
		checkNotSubtype("null","[void]");
	}
	@Test public void test_32() {
		checkNotSubtype("null","[any]");
	}
	@Test public void test_33() {
		checkNotSubtype("null","[null]");
	}
	@Test public void test_34() {
		checkNotSubtype("null","[int]");
	}
	@Test public void test_35() {
		checkNotSubtype("null","!void");
	}
	@Test public void test_36() {
		checkIsSubtype("null","!any");
	}
	@Test public void test_37() {
		checkNotSubtype("null","!null");
	}
	@Test public void test_38() {
		checkNotSubtype("null","!int");
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
		checkNotSubtype("null","[!void]");
	}
	@Test public void test_44() {
		checkNotSubtype("null","[!any]");
	}
	@Test public void test_45() {
		checkNotSubtype("null","[!null]");
	}
	@Test public void test_46() {
		checkNotSubtype("null","[!int]");
	}
	@Test public void test_47() {
		checkNotSubtype("null","![void]");
	}
	@Test public void test_48() {
		checkNotSubtype("null","![any]");
	}
	@Test public void test_49() {
		checkNotSubtype("null","![null]");
	}
	@Test public void test_50() {
		checkNotSubtype("null","![int]");
	}
	@Test public void test_51() {
		checkIsSubtype("null","!!void");
	}
	@Test public void test_52() {
		checkNotSubtype("null","!!any");
	}
	@Test public void test_53() {
		checkIsSubtype("null","!!null");
	}
	@Test public void test_54() {
		checkNotSubtype("null","!!int");
	}
	@Test public void test_55() {
		checkNotSubtype("int","any");
	}
	@Test public void test_56() {
		checkNotSubtype("int","null");
	}
	@Test public void test_57() {
		checkIsSubtype("int","int");
	}
	@Test public void test_58() {
		checkNotSubtype("int","[void]");
	}
	@Test public void test_59() {
		checkNotSubtype("int","[any]");
	}
	@Test public void test_60() {
		checkNotSubtype("int","[null]");
	}
	@Test public void test_61() {
		checkNotSubtype("int","[int]");
	}
	@Test public void test_62() {
		checkNotSubtype("int","!void");
	}
	@Test public void test_63() {
		checkIsSubtype("int","!any");
	}
	@Test public void test_64() {
		checkNotSubtype("int","!null");
	}
	@Test public void test_65() {
		checkNotSubtype("int","!int");
	}
	@Test public void test_66() {
		checkNotSubtype("int","[[void]]");
	}
	@Test public void test_67() {
		checkNotSubtype("int","[[any]]");
	}
	@Test public void test_68() {
		checkNotSubtype("int","[[null]]");
	}
	@Test public void test_69() {
		checkNotSubtype("int","[[int]]");
	}
	@Test public void test_70() {
		checkNotSubtype("int","[!void]");
	}
	@Test public void test_71() {
		checkNotSubtype("int","[!any]");
	}
	@Test public void test_72() {
		checkNotSubtype("int","[!null]");
	}
	@Test public void test_73() {
		checkNotSubtype("int","[!int]");
	}
	@Test public void test_74() {
		checkNotSubtype("int","![void]");
	}
	@Test public void test_75() {
		checkNotSubtype("int","![any]");
	}
	@Test public void test_76() {
		checkNotSubtype("int","![null]");
	}
	@Test public void test_77() {
		checkNotSubtype("int","![int]");
	}
	@Test public void test_78() {
		checkIsSubtype("int","!!void");
	}
	@Test public void test_79() {
		checkNotSubtype("int","!!any");
	}
	@Test public void test_80() {
		checkNotSubtype("int","!!null");
	}
	@Test public void test_81() {
		checkIsSubtype("int","!!int");
	}
	@Test public void test_82() {
		checkNotSubtype("[void]","any");
	}
	@Test public void test_83() {
		checkNotSubtype("[void]","null");
	}
	@Test public void test_84() {
		checkNotSubtype("[void]","int");
	}
	@Test public void test_85() {
		checkIsSubtype("[void]","[void]");
	}
	@Test public void test_86() {
		checkNotSubtype("[void]","[any]");
	}
	@Test public void test_87() {
		checkNotSubtype("[void]","[null]");
	}
	@Test public void test_88() {
		checkNotSubtype("[void]","[int]");
	}
	@Test public void test_89() {
		checkNotSubtype("[void]","!void");
	}
	@Test public void test_90() {
		checkIsSubtype("[void]","!any");
	}
	@Test public void test_91() {
		checkNotSubtype("[void]","!null");
	}
	@Test public void test_92() {
		checkNotSubtype("[void]","!int");
	}
	@Test public void test_93() {
		checkNotSubtype("[void]","[[void]]");
	}
	@Test public void test_94() {
		checkNotSubtype("[void]","[[any]]");
	}
	@Test public void test_95() {
		checkNotSubtype("[void]","[[null]]");
	}
	@Test public void test_96() {
		checkNotSubtype("[void]","[[int]]");
	}
	@Test public void test_97() {
		checkNotSubtype("[void]","[!void]");
	}
	@Test public void test_98() {
		checkIsSubtype("[void]","[!any]");
	}
	@Test public void test_99() {
		checkNotSubtype("[void]","[!null]");
	}
	@Test public void test_100() {
		checkNotSubtype("[void]","[!int]");
	}
	@Test public void test_101() {
		checkNotSubtype("[void]","![void]");
	}
	@Test public void test_102() {
		checkNotSubtype("[void]","![any]");
	}
	@Test public void test_103() {
		checkNotSubtype("[void]","![null]");
	}
	@Test public void test_104() {
		checkNotSubtype("[void]","![int]");
	}
	@Test public void test_105() {
		checkIsSubtype("[void]","!!void");
	}
	@Test public void test_106() {
		checkNotSubtype("[void]","!!any");
	}
	@Test public void test_107() {
		checkNotSubtype("[void]","!!null");
	}
	@Test public void test_108() {
		checkNotSubtype("[void]","!!int");
	}
	@Test public void test_109() {
		checkNotSubtype("[any]","any");
	}
	@Test public void test_110() {
		checkNotSubtype("[any]","null");
	}
	@Test public void test_111() {
		checkNotSubtype("[any]","int");
	}
	@Test public void test_112() {
		checkIsSubtype("[any]","[void]");
	}
	@Test public void test_113() {
		checkIsSubtype("[any]","[any]");
	}
	@Test public void test_114() {
		checkIsSubtype("[any]","[null]");
	}
	@Test public void test_115() {
		checkIsSubtype("[any]","[int]");
	}
	@Test public void test_116() {
		checkNotSubtype("[any]","!void");
	}
	@Test public void test_117() {
		checkIsSubtype("[any]","!any");
	}
	@Test public void test_118() {
		checkNotSubtype("[any]","!null");
	}
	@Test public void test_119() {
		checkNotSubtype("[any]","!int");
	}
	@Test public void test_120() {
		checkIsSubtype("[any]","[[void]]");
	}
	@Test public void test_121() {
		checkIsSubtype("[any]","[[any]]");
	}
	@Test public void test_122() {
		checkIsSubtype("[any]","[[null]]");
	}
	@Test public void test_123() {
		checkIsSubtype("[any]","[[int]]");
	}
	@Test public void test_124() {
		checkIsSubtype("[any]","[!void]");
	}
	@Test public void test_125() {
		checkIsSubtype("[any]","[!any]");
	}
	@Test public void test_126() {
		checkIsSubtype("[any]","[!null]");
	}
	@Test public void test_127() {
		checkIsSubtype("[any]","[!int]");
	}
	@Test public void test_128() {
		checkNotSubtype("[any]","![void]");
	}
	@Test public void test_129() {
		checkNotSubtype("[any]","![any]");
	}
	@Test public void test_130() {
		checkNotSubtype("[any]","![null]");
	}
	@Test public void test_131() {
		checkNotSubtype("[any]","![int]");
	}
	@Test public void test_132() {
		checkIsSubtype("[any]","!!void");
	}
	@Test public void test_133() {
		checkNotSubtype("[any]","!!any");
	}
	@Test public void test_134() {
		checkNotSubtype("[any]","!!null");
	}
	@Test public void test_135() {
		checkNotSubtype("[any]","!!int");
	}
	@Test public void test_136() {
		checkNotSubtype("[null]","any");
	}
	@Test public void test_137() {
		checkNotSubtype("[null]","null");
	}
	@Test public void test_138() {
		checkNotSubtype("[null]","int");
	}
	@Test public void test_139() {
		checkIsSubtype("[null]","[void]");
	}
	@Test public void test_140() {
		checkNotSubtype("[null]","[any]");
	}
	@Test public void test_141() {
		checkIsSubtype("[null]","[null]");
	}
	@Test public void test_142() {
		checkNotSubtype("[null]","[int]");
	}
	@Test public void test_143() {
		checkNotSubtype("[null]","!void");
	}
	@Test public void test_144() {
		checkIsSubtype("[null]","!any");
	}
	@Test public void test_145() {
		checkNotSubtype("[null]","!null");
	}
	@Test public void test_146() {
		checkNotSubtype("[null]","!int");
	}
	@Test public void test_147() {
		checkNotSubtype("[null]","[[void]]");
	}
	@Test public void test_148() {
		checkNotSubtype("[null]","[[any]]");
	}
	@Test public void test_149() {
		checkNotSubtype("[null]","[[null]]");
	}
	@Test public void test_150() {
		checkNotSubtype("[null]","[[int]]");
	}
	@Test public void test_151() {
		checkNotSubtype("[null]","[!void]");
	}
	@Test public void test_152() {
		checkIsSubtype("[null]","[!any]");
	}
	@Test public void test_153() {
		checkNotSubtype("[null]","[!null]");
	}
	@Test public void test_154() {
		checkNotSubtype("[null]","[!int]");
	}
	@Test public void test_155() {
		checkNotSubtype("[null]","![void]");
	}
	@Test public void test_156() {
		checkNotSubtype("[null]","![any]");
	}
	@Test public void test_157() {
		checkNotSubtype("[null]","![null]");
	}
	@Test public void test_158() {
		checkNotSubtype("[null]","![int]");
	}
	@Test public void test_159() {
		checkIsSubtype("[null]","!!void");
	}
	@Test public void test_160() {
		checkNotSubtype("[null]","!!any");
	}
	@Test public void test_161() {
		checkNotSubtype("[null]","!!null");
	}
	@Test public void test_162() {
		checkNotSubtype("[null]","!!int");
	}
	@Test public void test_163() {
		checkNotSubtype("[int]","any");
	}
	@Test public void test_164() {
		checkNotSubtype("[int]","null");
	}
	@Test public void test_165() {
		checkNotSubtype("[int]","int");
	}
	@Test public void test_166() {
		checkIsSubtype("[int]","[void]");
	}
	@Test public void test_167() {
		checkNotSubtype("[int]","[any]");
	}
	@Test public void test_168() {
		checkNotSubtype("[int]","[null]");
	}
	@Test public void test_169() {
		checkIsSubtype("[int]","[int]");
	}
	@Test public void test_170() {
		checkNotSubtype("[int]","!void");
	}
	@Test public void test_171() {
		checkIsSubtype("[int]","!any");
	}
	@Test public void test_172() {
		checkNotSubtype("[int]","!null");
	}
	@Test public void test_173() {
		checkNotSubtype("[int]","!int");
	}
	@Test public void test_174() {
		checkNotSubtype("[int]","[[void]]");
	}
	@Test public void test_175() {
		checkNotSubtype("[int]","[[any]]");
	}
	@Test public void test_176() {
		checkNotSubtype("[int]","[[null]]");
	}
	@Test public void test_177() {
		checkNotSubtype("[int]","[[int]]");
	}
	@Test public void test_178() {
		checkNotSubtype("[int]","[!void]");
	}
	@Test public void test_179() {
		checkIsSubtype("[int]","[!any]");
	}
	@Test public void test_180() {
		checkNotSubtype("[int]","[!null]");
	}
	@Test public void test_181() {
		checkNotSubtype("[int]","[!int]");
	}
	@Test public void test_182() {
		checkNotSubtype("[int]","![void]");
	}
	@Test public void test_183() {
		checkNotSubtype("[int]","![any]");
	}
	@Test public void test_184() {
		checkNotSubtype("[int]","![null]");
	}
	@Test public void test_185() {
		checkNotSubtype("[int]","![int]");
	}
	@Test public void test_186() {
		checkIsSubtype("[int]","!!void");
	}
	@Test public void test_187() {
		checkNotSubtype("[int]","!!any");
	}
	@Test public void test_188() {
		checkNotSubtype("[int]","!!null");
	}
	@Test public void test_189() {
		checkNotSubtype("[int]","!!int");
	}
	@Test public void test_190() {
		checkIsSubtype("!void","any");
	}
	@Test public void test_191() {
		checkIsSubtype("!void","null");
	}
	@Test public void test_192() {
		checkIsSubtype("!void","int");
	}
	@Test public void test_193() {
		checkIsSubtype("!void","[void]");
	}
	@Test public void test_194() {
		checkIsSubtype("!void","[any]");
	}
	@Test public void test_195() {
		checkIsSubtype("!void","[null]");
	}
	@Test public void test_196() {
		checkIsSubtype("!void","[int]");
	}
	@Test public void test_197() {
		checkIsSubtype("!void","!void");
	}
	@Test public void test_198() {
		checkIsSubtype("!void","!any");
	}
	@Test public void test_199() {
		checkIsSubtype("!void","!null");
	}
	@Test public void test_200() {
		checkIsSubtype("!void","!int");
	}
	@Test public void test_201() {
		checkIsSubtype("!void","[[void]]");
	}
	@Test public void test_202() {
		checkIsSubtype("!void","[[any]]");
	}
	@Test public void test_203() {
		checkIsSubtype("!void","[[null]]");
	}
	@Test public void test_204() {
		checkIsSubtype("!void","[[int]]");
	}
	@Test public void test_205() {
		checkIsSubtype("!void","[!void]");
	}
	@Test public void test_206() {
		checkIsSubtype("!void","[!any]");
	}
	@Test public void test_207() {
		checkIsSubtype("!void","[!null]");
	}
	@Test public void test_208() {
		checkIsSubtype("!void","[!int]");
	}
	@Test public void test_209() {
		checkIsSubtype("!void","![void]");
	}
	@Test public void test_210() {
		checkIsSubtype("!void","![any]");
	}
	@Test public void test_211() {
		checkIsSubtype("!void","![null]");
	}
	@Test public void test_212() {
		checkIsSubtype("!void","![int]");
	}
	@Test public void test_213() {
		checkIsSubtype("!void","!!void");
	}
	@Test public void test_214() {
		checkIsSubtype("!void","!!any");
	}
	@Test public void test_215() {
		checkIsSubtype("!void","!!null");
	}
	@Test public void test_216() {
		checkIsSubtype("!void","!!int");
	}
	@Test public void test_217() {
		checkNotSubtype("!any","any");
	}
	@Test public void test_218() {
		checkNotSubtype("!any","null");
	}
	@Test public void test_219() {
		checkNotSubtype("!any","int");
	}
	@Test public void test_220() {
		checkNotSubtype("!any","[void]");
	}
	@Test public void test_221() {
		checkNotSubtype("!any","[any]");
	}
	@Test public void test_222() {
		checkNotSubtype("!any","[null]");
	}
	@Test public void test_223() {
		checkNotSubtype("!any","[int]");
	}
	@Test public void test_224() {
		checkNotSubtype("!any","!void");
	}
	@Test public void test_225() {
		checkIsSubtype("!any","!any");
	}
	@Test public void test_226() {
		checkNotSubtype("!any","!null");
	}
	@Test public void test_227() {
		checkNotSubtype("!any","!int");
	}
	@Test public void test_228() {
		checkNotSubtype("!any","[[void]]");
	}
	@Test public void test_229() {
		checkNotSubtype("!any","[[any]]");
	}
	@Test public void test_230() {
		checkNotSubtype("!any","[[null]]");
	}
	@Test public void test_231() {
		checkNotSubtype("!any","[[int]]");
	}
	@Test public void test_232() {
		checkNotSubtype("!any","[!void]");
	}
	@Test public void test_233() {
		checkNotSubtype("!any","[!any]");
	}
	@Test public void test_234() {
		checkNotSubtype("!any","[!null]");
	}
	@Test public void test_235() {
		checkNotSubtype("!any","[!int]");
	}
	@Test public void test_236() {
		checkNotSubtype("!any","![void]");
	}
	@Test public void test_237() {
		checkNotSubtype("!any","![any]");
	}
	@Test public void test_238() {
		checkNotSubtype("!any","![null]");
	}
	@Test public void test_239() {
		checkNotSubtype("!any","![int]");
	}
	@Test public void test_240() {
		checkIsSubtype("!any","!!void");
	}
	@Test public void test_241() {
		checkNotSubtype("!any","!!any");
	}
	@Test public void test_242() {
		checkNotSubtype("!any","!!null");
	}
	@Test public void test_243() {
		checkNotSubtype("!any","!!int");
	}
	@Test public void test_244() {
		checkNotSubtype("!null","any");
	}
	@Test public void test_245() {
		checkNotSubtype("!null","null");
	}
	@Test public void test_246() {
		checkIsSubtype("!null","int");
	}
	@Test public void test_247() {
		checkIsSubtype("!null","[void]");
	}
	@Test public void test_248() {
		checkIsSubtype("!null","[any]");
	}
	@Test public void test_249() {
		checkIsSubtype("!null","[null]");
	}
	@Test public void test_250() {
		checkIsSubtype("!null","[int]");
	}
	@Test public void test_251() {
		checkNotSubtype("!null","!void");
	}
	@Test public void test_252() {
		checkIsSubtype("!null","!any");
	}
	@Test public void test_253() {
		checkIsSubtype("!null","!null");
	}
	@Test public void test_254() {
		checkNotSubtype("!null","!int");
	}
	@Test public void test_255() {
		checkIsSubtype("!null","[[void]]");
	}
	@Test public void test_256() {
		checkIsSubtype("!null","[[any]]");
	}
	@Test public void test_257() {
		checkIsSubtype("!null","[[null]]");
	}
	@Test public void test_258() {
		checkIsSubtype("!null","[[int]]");
	}
	@Test public void test_259() {
		checkIsSubtype("!null","[!void]");
	}
	@Test public void test_260() {
		checkIsSubtype("!null","[!any]");
	}
	@Test public void test_261() {
		checkIsSubtype("!null","[!null]");
	}
	@Test public void test_262() {
		checkIsSubtype("!null","[!int]");
	}
	@Test public void test_263() {
		checkNotSubtype("!null","![void]");
	}
	@Test public void test_264() {
		checkNotSubtype("!null","![any]");
	}
	@Test public void test_265() {
		checkNotSubtype("!null","![null]");
	}
	@Test public void test_266() {
		checkNotSubtype("!null","![int]");
	}
	@Test public void test_267() {
		checkIsSubtype("!null","!!void");
	}
	@Test public void test_268() {
		checkNotSubtype("!null","!!any");
	}
	@Test public void test_269() {
		checkNotSubtype("!null","!!null");
	}
	@Test public void test_270() {
		checkIsSubtype("!null","!!int");
	}
	@Test public void test_271() {
		checkNotSubtype("!int","any");
	}
	@Test public void test_272() {
		checkIsSubtype("!int","null");
	}
	@Test public void test_273() {
		checkNotSubtype("!int","int");
	}
	@Test public void test_274() {
		checkIsSubtype("!int","[void]");
	}
	@Test public void test_275() {
		checkIsSubtype("!int","[any]");
	}
	@Test public void test_276() {
		checkIsSubtype("!int","[null]");
	}
	@Test public void test_277() {
		checkIsSubtype("!int","[int]");
	}
	@Test public void test_278() {
		checkNotSubtype("!int","!void");
	}
	@Test public void test_279() {
		checkIsSubtype("!int","!any");
	}
	@Test public void test_280() {
		checkNotSubtype("!int","!null");
	}
	@Test public void test_281() {
		checkIsSubtype("!int","!int");
	}
	@Test public void test_282() {
		checkIsSubtype("!int","[[void]]");
	}
	@Test public void test_283() {
		checkIsSubtype("!int","[[any]]");
	}
	@Test public void test_284() {
		checkIsSubtype("!int","[[null]]");
	}
	@Test public void test_285() {
		checkIsSubtype("!int","[[int]]");
	}
	@Test public void test_286() {
		checkIsSubtype("!int","[!void]");
	}
	@Test public void test_287() {
		checkIsSubtype("!int","[!any]");
	}
	@Test public void test_288() {
		checkIsSubtype("!int","[!null]");
	}
	@Test public void test_289() {
		checkIsSubtype("!int","[!int]");
	}
	@Test public void test_290() {
		checkNotSubtype("!int","![void]");
	}
	@Test public void test_291() {
		checkNotSubtype("!int","![any]");
	}
	@Test public void test_292() {
		checkNotSubtype("!int","![null]");
	}
	@Test public void test_293() {
		checkNotSubtype("!int","![int]");
	}
	@Test public void test_294() {
		checkIsSubtype("!int","!!void");
	}
	@Test public void test_295() {
		checkNotSubtype("!int","!!any");
	}
	@Test public void test_296() {
		checkIsSubtype("!int","!!null");
	}
	@Test public void test_297() {
		checkNotSubtype("!int","!!int");
	}
	@Test public void test_298() {
		checkNotSubtype("[[void]]","any");
	}
	@Test public void test_299() {
		checkNotSubtype("[[void]]","null");
	}
	@Test public void test_300() {
		checkNotSubtype("[[void]]","int");
	}
	@Test public void test_301() {
		checkIsSubtype("[[void]]","[void]");
	}
	@Test public void test_302() {
		checkNotSubtype("[[void]]","[any]");
	}
	@Test public void test_303() {
		checkNotSubtype("[[void]]","[null]");
	}
	@Test public void test_304() {
		checkNotSubtype("[[void]]","[int]");
	}
	@Test public void test_305() {
		checkNotSubtype("[[void]]","!void");
	}
	@Test public void test_306() {
		checkIsSubtype("[[void]]","!any");
	}
	@Test public void test_307() {
		checkNotSubtype("[[void]]","!null");
	}
	@Test public void test_308() {
		checkNotSubtype("[[void]]","!int");
	}
	@Test public void test_309() {
		checkIsSubtype("[[void]]","[[void]]");
	}
	@Test public void test_310() {
		checkNotSubtype("[[void]]","[[any]]");
	}
	@Test public void test_311() {
		checkNotSubtype("[[void]]","[[null]]");
	}
	@Test public void test_312() {
		checkNotSubtype("[[void]]","[[int]]");
	}
	@Test public void test_313() {
		checkNotSubtype("[[void]]","[!void]");
	}
	@Test public void test_314() {
		checkIsSubtype("[[void]]","[!any]");
	}
	@Test public void test_315() {
		checkNotSubtype("[[void]]","[!null]");
	}
	@Test public void test_316() {
		checkNotSubtype("[[void]]","[!int]");
	}
	@Test public void test_317() {
		checkNotSubtype("[[void]]","![void]");
	}
	@Test public void test_318() {
		checkNotSubtype("[[void]]","![any]");
	}
	@Test public void test_319() {
		checkNotSubtype("[[void]]","![null]");
	}
	@Test public void test_320() {
		checkNotSubtype("[[void]]","![int]");
	}
	@Test public void test_321() {
		checkIsSubtype("[[void]]","!!void");
	}
	@Test public void test_322() {
		checkNotSubtype("[[void]]","!!any");
	}
	@Test public void test_323() {
		checkNotSubtype("[[void]]","!!null");
	}
	@Test public void test_324() {
		checkNotSubtype("[[void]]","!!int");
	}
	@Test public void test_325() {
		checkNotSubtype("[[any]]","any");
	}
	@Test public void test_326() {
		checkNotSubtype("[[any]]","null");
	}
	@Test public void test_327() {
		checkNotSubtype("[[any]]","int");
	}
	@Test public void test_328() {
		checkIsSubtype("[[any]]","[void]");
	}
	@Test public void test_329() {
		checkNotSubtype("[[any]]","[any]");
	}
	@Test public void test_330() {
		checkNotSubtype("[[any]]","[null]");
	}
	@Test public void test_331() {
		checkNotSubtype("[[any]]","[int]");
	}
	@Test public void test_332() {
		checkNotSubtype("[[any]]","!void");
	}
	@Test public void test_333() {
		checkIsSubtype("[[any]]","!any");
	}
	@Test public void test_334() {
		checkNotSubtype("[[any]]","!null");
	}
	@Test public void test_335() {
		checkNotSubtype("[[any]]","!int");
	}
	@Test public void test_336() {
		checkIsSubtype("[[any]]","[[void]]");
	}
	@Test public void test_337() {
		checkIsSubtype("[[any]]","[[any]]");
	}
	@Test public void test_338() {
		checkIsSubtype("[[any]]","[[null]]");
	}
	@Test public void test_339() {
		checkIsSubtype("[[any]]","[[int]]");
	}
	@Test public void test_340() {
		checkNotSubtype("[[any]]","[!void]");
	}
	@Test public void test_341() {
		checkIsSubtype("[[any]]","[!any]");
	}
	@Test public void test_342() {
		checkNotSubtype("[[any]]","[!null]");
	}
	@Test public void test_343() {
		checkNotSubtype("[[any]]","[!int]");
	}
	@Test public void test_344() {
		checkNotSubtype("[[any]]","![void]");
	}
	@Test public void test_345() {
		checkNotSubtype("[[any]]","![any]");
	}
	@Test public void test_346() {
		checkNotSubtype("[[any]]","![null]");
	}
	@Test public void test_347() {
		checkNotSubtype("[[any]]","![int]");
	}
	@Test public void test_348() {
		checkIsSubtype("[[any]]","!!void");
	}
	@Test public void test_349() {
		checkNotSubtype("[[any]]","!!any");
	}
	@Test public void test_350() {
		checkNotSubtype("[[any]]","!!null");
	}
	@Test public void test_351() {
		checkNotSubtype("[[any]]","!!int");
	}
	@Test public void test_352() {
		checkNotSubtype("[[null]]","any");
	}
	@Test public void test_353() {
		checkNotSubtype("[[null]]","null");
	}
	@Test public void test_354() {
		checkNotSubtype("[[null]]","int");
	}
	@Test public void test_355() {
		checkIsSubtype("[[null]]","[void]");
	}
	@Test public void test_356() {
		checkNotSubtype("[[null]]","[any]");
	}
	@Test public void test_357() {
		checkNotSubtype("[[null]]","[null]");
	}
	@Test public void test_358() {
		checkNotSubtype("[[null]]","[int]");
	}
	@Test public void test_359() {
		checkNotSubtype("[[null]]","!void");
	}
	@Test public void test_360() {
		checkIsSubtype("[[null]]","!any");
	}
	@Test public void test_361() {
		checkNotSubtype("[[null]]","!null");
	}
	@Test public void test_362() {
		checkNotSubtype("[[null]]","!int");
	}
	@Test public void test_363() {
		checkIsSubtype("[[null]]","[[void]]");
	}
	@Test public void test_364() {
		checkNotSubtype("[[null]]","[[any]]");
	}
	@Test public void test_365() {
		checkIsSubtype("[[null]]","[[null]]");
	}
	@Test public void test_366() {
		checkNotSubtype("[[null]]","[[int]]");
	}
	@Test public void test_367() {
		checkNotSubtype("[[null]]","[!void]");
	}
	@Test public void test_368() {
		checkIsSubtype("[[null]]","[!any]");
	}
	@Test public void test_369() {
		checkNotSubtype("[[null]]","[!null]");
	}
	@Test public void test_370() {
		checkNotSubtype("[[null]]","[!int]");
	}
	@Test public void test_371() {
		checkNotSubtype("[[null]]","![void]");
	}
	@Test public void test_372() {
		checkNotSubtype("[[null]]","![any]");
	}
	@Test public void test_373() {
		checkNotSubtype("[[null]]","![null]");
	}
	@Test public void test_374() {
		checkNotSubtype("[[null]]","![int]");
	}
	@Test public void test_375() {
		checkIsSubtype("[[null]]","!!void");
	}
	@Test public void test_376() {
		checkNotSubtype("[[null]]","!!any");
	}
	@Test public void test_377() {
		checkNotSubtype("[[null]]","!!null");
	}
	@Test public void test_378() {
		checkNotSubtype("[[null]]","!!int");
	}
	@Test public void test_379() {
		checkNotSubtype("[[int]]","any");
	}
	@Test public void test_380() {
		checkNotSubtype("[[int]]","null");
	}
	@Test public void test_381() {
		checkNotSubtype("[[int]]","int");
	}
	@Test public void test_382() {
		checkIsSubtype("[[int]]","[void]");
	}
	@Test public void test_383() {
		checkNotSubtype("[[int]]","[any]");
	}
	@Test public void test_384() {
		checkNotSubtype("[[int]]","[null]");
	}
	@Test public void test_385() {
		checkNotSubtype("[[int]]","[int]");
	}
	@Test public void test_386() {
		checkNotSubtype("[[int]]","!void");
	}
	@Test public void test_387() {
		checkIsSubtype("[[int]]","!any");
	}
	@Test public void test_388() {
		checkNotSubtype("[[int]]","!null");
	}
	@Test public void test_389() {
		checkNotSubtype("[[int]]","!int");
	}
	@Test public void test_390() {
		checkIsSubtype("[[int]]","[[void]]");
	}
	@Test public void test_391() {
		checkNotSubtype("[[int]]","[[any]]");
	}
	@Test public void test_392() {
		checkNotSubtype("[[int]]","[[null]]");
	}
	@Test public void test_393() {
		checkIsSubtype("[[int]]","[[int]]");
	}
	@Test public void test_394() {
		checkNotSubtype("[[int]]","[!void]");
	}
	@Test public void test_395() {
		checkIsSubtype("[[int]]","[!any]");
	}
	@Test public void test_396() {
		checkNotSubtype("[[int]]","[!null]");
	}
	@Test public void test_397() {
		checkNotSubtype("[[int]]","[!int]");
	}
	@Test public void test_398() {
		checkNotSubtype("[[int]]","![void]");
	}
	@Test public void test_399() {
		checkNotSubtype("[[int]]","![any]");
	}
	@Test public void test_400() {
		checkNotSubtype("[[int]]","![null]");
	}
	@Test public void test_401() {
		checkNotSubtype("[[int]]","![int]");
	}
	@Test public void test_402() {
		checkIsSubtype("[[int]]","!!void");
	}
	@Test public void test_403() {
		checkNotSubtype("[[int]]","!!any");
	}
	@Test public void test_404() {
		checkNotSubtype("[[int]]","!!null");
	}
	@Test public void test_405() {
		checkNotSubtype("[[int]]","!!int");
	}
	@Test public void test_406() {
		checkNotSubtype("[!void]","any");
	}
	@Test public void test_407() {
		checkNotSubtype("[!void]","null");
	}
	@Test public void test_408() {
		checkNotSubtype("[!void]","int");
	}
	@Test public void test_409() {
		checkIsSubtype("[!void]","[void]");
	}
	@Test public void test_410() {
		checkIsSubtype("[!void]","[any]");
	}
	@Test public void test_411() {
		checkIsSubtype("[!void]","[null]");
	}
	@Test public void test_412() {
		checkIsSubtype("[!void]","[int]");
	}
	@Test public void test_413() {
		checkNotSubtype("[!void]","!void");
	}
	@Test public void test_414() {
		checkIsSubtype("[!void]","!any");
	}
	@Test public void test_415() {
		checkNotSubtype("[!void]","!null");
	}
	@Test public void test_416() {
		checkNotSubtype("[!void]","!int");
	}
	@Test public void test_417() {
		checkIsSubtype("[!void]","[[void]]");
	}
	@Test public void test_418() {
		checkIsSubtype("[!void]","[[any]]");
	}
	@Test public void test_419() {
		checkIsSubtype("[!void]","[[null]]");
	}
	@Test public void test_420() {
		checkIsSubtype("[!void]","[[int]]");
	}
	@Test public void test_421() {
		checkIsSubtype("[!void]","[!void]");
	}
	@Test public void test_422() {
		checkIsSubtype("[!void]","[!any]");
	}
	@Test public void test_423() {
		checkIsSubtype("[!void]","[!null]");
	}
	@Test public void test_424() {
		checkIsSubtype("[!void]","[!int]");
	}
	@Test public void test_425() {
		checkNotSubtype("[!void]","![void]");
	}
	@Test public void test_426() {
		checkNotSubtype("[!void]","![any]");
	}
	@Test public void test_427() {
		checkNotSubtype("[!void]","![null]");
	}
	@Test public void test_428() {
		checkNotSubtype("[!void]","![int]");
	}
	@Test public void test_429() {
		checkIsSubtype("[!void]","!!void");
	}
	@Test public void test_430() {
		checkNotSubtype("[!void]","!!any");
	}
	@Test public void test_431() {
		checkNotSubtype("[!void]","!!null");
	}
	@Test public void test_432() {
		checkNotSubtype("[!void]","!!int");
	}
	@Test public void test_433() {
		checkNotSubtype("[!any]","any");
	}
	@Test public void test_434() {
		checkNotSubtype("[!any]","null");
	}
	@Test public void test_435() {
		checkNotSubtype("[!any]","int");
	}
	@Test public void test_436() {
		checkIsSubtype("[!any]","[void]");
	}
	@Test public void test_437() {
		checkNotSubtype("[!any]","[any]");
	}
	@Test public void test_438() {
		checkNotSubtype("[!any]","[null]");
	}
	@Test public void test_439() {
		checkNotSubtype("[!any]","[int]");
	}
	@Test public void test_440() {
		checkNotSubtype("[!any]","!void");
	}
	@Test public void test_441() {
		checkIsSubtype("[!any]","!any");
	}
	@Test public void test_442() {
		checkNotSubtype("[!any]","!null");
	}
	@Test public void test_443() {
		checkNotSubtype("[!any]","!int");
	}
	@Test public void test_444() {
		checkNotSubtype("[!any]","[[void]]");
	}
	@Test public void test_445() {
		checkNotSubtype("[!any]","[[any]]");
	}
	@Test public void test_446() {
		checkNotSubtype("[!any]","[[null]]");
	}
	@Test public void test_447() {
		checkNotSubtype("[!any]","[[int]]");
	}
	@Test public void test_448() {
		checkNotSubtype("[!any]","[!void]");
	}
	@Test public void test_449() {
		checkIsSubtype("[!any]","[!any]");
	}
	@Test public void test_450() {
		checkNotSubtype("[!any]","[!null]");
	}
	@Test public void test_451() {
		checkNotSubtype("[!any]","[!int]");
	}
	@Test public void test_452() {
		checkNotSubtype("[!any]","![void]");
	}
	@Test public void test_453() {
		checkNotSubtype("[!any]","![any]");
	}
	@Test public void test_454() {
		checkNotSubtype("[!any]","![null]");
	}
	@Test public void test_455() {
		checkNotSubtype("[!any]","![int]");
	}
	@Test public void test_456() {
		checkIsSubtype("[!any]","!!void");
	}
	@Test public void test_457() {
		checkNotSubtype("[!any]","!!any");
	}
	@Test public void test_458() {
		checkNotSubtype("[!any]","!!null");
	}
	@Test public void test_459() {
		checkNotSubtype("[!any]","!!int");
	}
	@Test public void test_460() {
		checkNotSubtype("[!null]","any");
	}
	@Test public void test_461() {
		checkNotSubtype("[!null]","null");
	}
	@Test public void test_462() {
		checkNotSubtype("[!null]","int");
	}
	@Test public void test_463() {
		checkIsSubtype("[!null]","[void]");
	}
	@Test public void test_464() {
		checkNotSubtype("[!null]","[any]");
	}
	@Test public void test_465() {
		checkNotSubtype("[!null]","[null]");
	}
	@Test public void test_466() {
		checkIsSubtype("[!null]","[int]");
	}
	@Test public void test_467() {
		checkNotSubtype("[!null]","!void");
	}
	@Test public void test_468() {
		checkIsSubtype("[!null]","!any");
	}
	@Test public void test_469() {
		checkNotSubtype("[!null]","!null");
	}
	@Test public void test_470() {
		checkNotSubtype("[!null]","!int");
	}
	@Test public void test_471() {
		checkIsSubtype("[!null]","[[void]]");
	}
	@Test public void test_472() {
		checkIsSubtype("[!null]","[[any]]");
	}
	@Test public void test_473() {
		checkIsSubtype("[!null]","[[null]]");
	}
	@Test public void test_474() {
		checkIsSubtype("[!null]","[[int]]");
	}
	@Test public void test_475() {
		checkNotSubtype("[!null]","[!void]");
	}
	@Test public void test_476() {
		checkIsSubtype("[!null]","[!any]");
	}
	@Test public void test_477() {
		checkIsSubtype("[!null]","[!null]");
	}
	@Test public void test_478() {
		checkNotSubtype("[!null]","[!int]");
	}
	@Test public void test_479() {
		checkNotSubtype("[!null]","![void]");
	}
	@Test public void test_480() {
		checkNotSubtype("[!null]","![any]");
	}
	@Test public void test_481() {
		checkNotSubtype("[!null]","![null]");
	}
	@Test public void test_482() {
		checkNotSubtype("[!null]","![int]");
	}
	@Test public void test_483() {
		checkIsSubtype("[!null]","!!void");
	}
	@Test public void test_484() {
		checkNotSubtype("[!null]","!!any");
	}
	@Test public void test_485() {
		checkNotSubtype("[!null]","!!null");
	}
	@Test public void test_486() {
		checkNotSubtype("[!null]","!!int");
	}
	@Test public void test_487() {
		checkNotSubtype("[!int]","any");
	}
	@Test public void test_488() {
		checkNotSubtype("[!int]","null");
	}
	@Test public void test_489() {
		checkNotSubtype("[!int]","int");
	}
	@Test public void test_490() {
		checkIsSubtype("[!int]","[void]");
	}
	@Test public void test_491() {
		checkNotSubtype("[!int]","[any]");
	}
	@Test public void test_492() {
		checkIsSubtype("[!int]","[null]");
	}
	@Test public void test_493() {
		checkNotSubtype("[!int]","[int]");
	}
	@Test public void test_494() {
		checkNotSubtype("[!int]","!void");
	}
	@Test public void test_495() {
		checkIsSubtype("[!int]","!any");
	}
	@Test public void test_496() {
		checkNotSubtype("[!int]","!null");
	}
	@Test public void test_497() {
		checkNotSubtype("[!int]","!int");
	}
	@Test public void test_498() {
		checkIsSubtype("[!int]","[[void]]");
	}
	@Test public void test_499() {
		checkIsSubtype("[!int]","[[any]]");
	}
	@Test public void test_500() {
		checkIsSubtype("[!int]","[[null]]");
	}
	@Test public void test_501() {
		checkIsSubtype("[!int]","[[int]]");
	}
	@Test public void test_502() {
		checkNotSubtype("[!int]","[!void]");
	}
	@Test public void test_503() {
		checkIsSubtype("[!int]","[!any]");
	}
	@Test public void test_504() {
		checkNotSubtype("[!int]","[!null]");
	}
	@Test public void test_505() {
		checkIsSubtype("[!int]","[!int]");
	}
	@Test public void test_506() {
		checkNotSubtype("[!int]","![void]");
	}
	@Test public void test_507() {
		checkNotSubtype("[!int]","![any]");
	}
	@Test public void test_508() {
		checkNotSubtype("[!int]","![null]");
	}
	@Test public void test_509() {
		checkNotSubtype("[!int]","![int]");
	}
	@Test public void test_510() {
		checkIsSubtype("[!int]","!!void");
	}
	@Test public void test_511() {
		checkNotSubtype("[!int]","!!any");
	}
	@Test public void test_512() {
		checkNotSubtype("[!int]","!!null");
	}
	@Test public void test_513() {
		checkNotSubtype("[!int]","!!int");
	}
	@Test public void test_514() {
		checkNotSubtype("![void]","any");
	}
	@Test public void test_515() {
		checkIsSubtype("![void]","null");
	}
	@Test public void test_516() {
		checkIsSubtype("![void]","int");
	}
	@Test public void test_517() {
		checkNotSubtype("![void]","[void]");
	}
	@Test public void test_518() {
		checkNotSubtype("![void]","[any]");
	}
	@Test public void test_519() {
		checkNotSubtype("![void]","[null]");
	}
	@Test public void test_520() {
		checkNotSubtype("![void]","[int]");
	}
	@Test public void test_521() {
		checkNotSubtype("![void]","!void");
	}
	@Test public void test_522() {
		checkIsSubtype("![void]","!any");
	}
	@Test public void test_523() {
		checkNotSubtype("![void]","!null");
	}
	@Test public void test_524() {
		checkNotSubtype("![void]","!int");
	}
	@Test public void test_525() {
		checkNotSubtype("![void]","[[void]]");
	}
	@Test public void test_526() {
		checkNotSubtype("![void]","[[any]]");
	}
	@Test public void test_527() {
		checkNotSubtype("![void]","[[null]]");
	}
	@Test public void test_528() {
		checkNotSubtype("![void]","[[int]]");
	}
	@Test public void test_529() {
		checkNotSubtype("![void]","[!void]");
	}
	@Test public void test_530() {
		checkNotSubtype("![void]","[!any]");
	}
	@Test public void test_531() {
		checkNotSubtype("![void]","[!null]");
	}
	@Test public void test_532() {
		checkNotSubtype("![void]","[!int]");
	}
	@Test public void test_533() {
		checkIsSubtype("![void]","![void]");
	}
	@Test public void test_534() {
		checkIsSubtype("![void]","![any]");
	}
	@Test public void test_535() {
		checkIsSubtype("![void]","![null]");
	}
	@Test public void test_536() {
		checkIsSubtype("![void]","![int]");
	}
	@Test public void test_537() {
		checkIsSubtype("![void]","!!void");
	}
	@Test public void test_538() {
		checkNotSubtype("![void]","!!any");
	}
	@Test public void test_539() {
		checkIsSubtype("![void]","!!null");
	}
	@Test public void test_540() {
		checkIsSubtype("![void]","!!int");
	}
	@Test public void test_541() {
		checkNotSubtype("![any]","any");
	}
	@Test public void test_542() {
		checkIsSubtype("![any]","null");
	}
	@Test public void test_543() {
		checkIsSubtype("![any]","int");
	}
	@Test public void test_544() {
		checkNotSubtype("![any]","[void]");
	}
	@Test public void test_545() {
		checkNotSubtype("![any]","[any]");
	}
	@Test public void test_546() {
		checkNotSubtype("![any]","[null]");
	}
	@Test public void test_547() {
		checkNotSubtype("![any]","[int]");
	}
	@Test public void test_548() {
		checkNotSubtype("![any]","!void");
	}
	@Test public void test_549() {
		checkIsSubtype("![any]","!any");
	}
	@Test public void test_550() {
		checkNotSubtype("![any]","!null");
	}
	@Test public void test_551() {
		checkNotSubtype("![any]","!int");
	}
	@Test public void test_552() {
		checkNotSubtype("![any]","[[void]]");
	}
	@Test public void test_553() {
		checkNotSubtype("![any]","[[any]]");
	}
	@Test public void test_554() {
		checkNotSubtype("![any]","[[null]]");
	}
	@Test public void test_555() {
		checkNotSubtype("![any]","[[int]]");
	}
	@Test public void test_556() {
		checkNotSubtype("![any]","[!void]");
	}
	@Test public void test_557() {
		checkNotSubtype("![any]","[!any]");
	}
	@Test public void test_558() {
		checkNotSubtype("![any]","[!null]");
	}
	@Test public void test_559() {
		checkNotSubtype("![any]","[!int]");
	}
	@Test public void test_560() {
		checkNotSubtype("![any]","![void]");
	}
	@Test public void test_561() {
		checkIsSubtype("![any]","![any]");
	}
	@Test public void test_562() {
		checkNotSubtype("![any]","![null]");
	}
	@Test public void test_563() {
		checkNotSubtype("![any]","![int]");
	}
	@Test public void test_564() {
		checkIsSubtype("![any]","!!void");
	}
	@Test public void test_565() {
		checkNotSubtype("![any]","!!any");
	}
	@Test public void test_566() {
		checkIsSubtype("![any]","!!null");
	}
	@Test public void test_567() {
		checkIsSubtype("![any]","!!int");
	}
	@Test public void test_568() {
		checkNotSubtype("![null]","any");
	}
	@Test public void test_569() {
		checkIsSubtype("![null]","null");
	}
	@Test public void test_570() {
		checkIsSubtype("![null]","int");
	}
	@Test public void test_571() {
		checkNotSubtype("![null]","[void]");
	}
	@Test public void test_572() {
		checkNotSubtype("![null]","[any]");
	}
	@Test public void test_573() {
		checkNotSubtype("![null]","[null]");
	}
	@Test public void test_574() {
		checkNotSubtype("![null]","[int]");
	}
	@Test public void test_575() {
		checkNotSubtype("![null]","!void");
	}
	@Test public void test_576() {
		checkIsSubtype("![null]","!any");
	}
	@Test public void test_577() {
		checkNotSubtype("![null]","!null");
	}
	@Test public void test_578() {
		checkNotSubtype("![null]","!int");
	}
	@Test public void test_579() {
		checkNotSubtype("![null]","[[void]]");
	}
	@Test public void test_580() {
		checkNotSubtype("![null]","[[any]]");
	}
	@Test public void test_581() {
		checkNotSubtype("![null]","[[null]]");
	}
	@Test public void test_582() {
		checkNotSubtype("![null]","[[int]]");
	}
	@Test public void test_583() {
		checkNotSubtype("![null]","[!void]");
	}
	@Test public void test_584() {
		checkNotSubtype("![null]","[!any]");
	}
	@Test public void test_585() {
		checkNotSubtype("![null]","[!null]");
	}
	@Test public void test_586() {
		checkNotSubtype("![null]","[!int]");
	}
	@Test public void test_587() {
		checkNotSubtype("![null]","![void]");
	}
	@Test public void test_588() {
		checkIsSubtype("![null]","![any]");
	}
	@Test public void test_589() {
		checkIsSubtype("![null]","![null]");
	}
	@Test public void test_590() {
		checkNotSubtype("![null]","![int]");
	}
	@Test public void test_591() {
		checkIsSubtype("![null]","!!void");
	}
	@Test public void test_592() {
		checkNotSubtype("![null]","!!any");
	}
	@Test public void test_593() {
		checkIsSubtype("![null]","!!null");
	}
	@Test public void test_594() {
		checkIsSubtype("![null]","!!int");
	}
	@Test public void test_595() {
		checkNotSubtype("![int]","any");
	}
	@Test public void test_596() {
		checkIsSubtype("![int]","null");
	}
	@Test public void test_597() {
		checkIsSubtype("![int]","int");
	}
	@Test public void test_598() {
		checkNotSubtype("![int]","[void]");
	}
	@Test public void test_599() {
		checkNotSubtype("![int]","[any]");
	}
	@Test public void test_600() {
		checkNotSubtype("![int]","[null]");
	}
	@Test public void test_601() {
		checkNotSubtype("![int]","[int]");
	}
	@Test public void test_602() {
		checkNotSubtype("![int]","!void");
	}
	@Test public void test_603() {
		checkIsSubtype("![int]","!any");
	}
	@Test public void test_604() {
		checkNotSubtype("![int]","!null");
	}
	@Test public void test_605() {
		checkNotSubtype("![int]","!int");
	}
	@Test public void test_606() {
		checkNotSubtype("![int]","[[void]]");
	}
	@Test public void test_607() {
		checkNotSubtype("![int]","[[any]]");
	}
	@Test public void test_608() {
		checkNotSubtype("![int]","[[null]]");
	}
	@Test public void test_609() {
		checkNotSubtype("![int]","[[int]]");
	}
	@Test public void test_610() {
		checkNotSubtype("![int]","[!void]");
	}
	@Test public void test_611() {
		checkNotSubtype("![int]","[!any]");
	}
	@Test public void test_612() {
		checkNotSubtype("![int]","[!null]");
	}
	@Test public void test_613() {
		checkNotSubtype("![int]","[!int]");
	}
	@Test public void test_614() {
		checkNotSubtype("![int]","![void]");
	}
	@Test public void test_615() {
		checkIsSubtype("![int]","![any]");
	}
	@Test public void test_616() {
		checkNotSubtype("![int]","![null]");
	}
	@Test public void test_617() {
		checkIsSubtype("![int]","![int]");
	}
	@Test public void test_618() {
		checkIsSubtype("![int]","!!void");
	}
	@Test public void test_619() {
		checkNotSubtype("![int]","!!any");
	}
	@Test public void test_620() {
		checkIsSubtype("![int]","!!null");
	}
	@Test public void test_621() {
		checkIsSubtype("![int]","!!int");
	}
	@Test public void test_622() {
		checkNotSubtype("!!void","any");
	}
	@Test public void test_623() {
		checkNotSubtype("!!void","null");
	}
	@Test public void test_624() {
		checkNotSubtype("!!void","int");
	}
	@Test public void test_625() {
		checkNotSubtype("!!void","[void]");
	}
	@Test public void test_626() {
		checkNotSubtype("!!void","[any]");
	}
	@Test public void test_627() {
		checkNotSubtype("!!void","[null]");
	}
	@Test public void test_628() {
		checkNotSubtype("!!void","[int]");
	}
	@Test public void test_629() {
		checkNotSubtype("!!void","!void");
	}
	@Test public void test_630() {
		checkIsSubtype("!!void","!any");
	}
	@Test public void test_631() {
		checkNotSubtype("!!void","!null");
	}
	@Test public void test_632() {
		checkNotSubtype("!!void","!int");
	}
	@Test public void test_633() {
		checkNotSubtype("!!void","[[void]]");
	}
	@Test public void test_634() {
		checkNotSubtype("!!void","[[any]]");
	}
	@Test public void test_635() {
		checkNotSubtype("!!void","[[null]]");
	}
	@Test public void test_636() {
		checkNotSubtype("!!void","[[int]]");
	}
	@Test public void test_637() {
		checkNotSubtype("!!void","[!void]");
	}
	@Test public void test_638() {
		checkNotSubtype("!!void","[!any]");
	}
	@Test public void test_639() {
		checkNotSubtype("!!void","[!null]");
	}
	@Test public void test_640() {
		checkNotSubtype("!!void","[!int]");
	}
	@Test public void test_641() {
		checkNotSubtype("!!void","![void]");
	}
	@Test public void test_642() {
		checkNotSubtype("!!void","![any]");
	}
	@Test public void test_643() {
		checkNotSubtype("!!void","![null]");
	}
	@Test public void test_644() {
		checkNotSubtype("!!void","![int]");
	}
	@Test public void test_645() {
		checkIsSubtype("!!void","!!void");
	}
	@Test public void test_646() {
		checkNotSubtype("!!void","!!any");
	}
	@Test public void test_647() {
		checkNotSubtype("!!void","!!null");
	}
	@Test public void test_648() {
		checkNotSubtype("!!void","!!int");
	}
	@Test public void test_649() {
		checkIsSubtype("!!any","any");
	}
	@Test public void test_650() {
		checkIsSubtype("!!any","null");
	}
	@Test public void test_651() {
		checkIsSubtype("!!any","int");
	}
	@Test public void test_652() {
		checkIsSubtype("!!any","[void]");
	}
	@Test public void test_653() {
		checkIsSubtype("!!any","[any]");
	}
	@Test public void test_654() {
		checkIsSubtype("!!any","[null]");
	}
	@Test public void test_655() {
		checkIsSubtype("!!any","[int]");
	}
	@Test public void test_656() {
		checkIsSubtype("!!any","!void");
	}
	@Test public void test_657() {
		checkIsSubtype("!!any","!any");
	}
	@Test public void test_658() {
		checkIsSubtype("!!any","!null");
	}
	@Test public void test_659() {
		checkIsSubtype("!!any","!int");
	}
	@Test public void test_660() {
		checkIsSubtype("!!any","[[void]]");
	}
	@Test public void test_661() {
		checkIsSubtype("!!any","[[any]]");
	}
	@Test public void test_662() {
		checkIsSubtype("!!any","[[null]]");
	}
	@Test public void test_663() {
		checkIsSubtype("!!any","[[int]]");
	}
	@Test public void test_664() {
		checkIsSubtype("!!any","[!void]");
	}
	@Test public void test_665() {
		checkIsSubtype("!!any","[!any]");
	}
	@Test public void test_666() {
		checkIsSubtype("!!any","[!null]");
	}
	@Test public void test_667() {
		checkIsSubtype("!!any","[!int]");
	}
	@Test public void test_668() {
		checkIsSubtype("!!any","![void]");
	}
	@Test public void test_669() {
		checkIsSubtype("!!any","![any]");
	}
	@Test public void test_670() {
		checkIsSubtype("!!any","![null]");
	}
	@Test public void test_671() {
		checkIsSubtype("!!any","![int]");
	}
	@Test public void test_672() {
		checkIsSubtype("!!any","!!void");
	}
	@Test public void test_673() {
		checkIsSubtype("!!any","!!any");
	}
	@Test public void test_674() {
		checkIsSubtype("!!any","!!null");
	}
	@Test public void test_675() {
		checkIsSubtype("!!any","!!int");
	}
	@Test public void test_676() {
		checkNotSubtype("!!null","any");
	}
	@Test public void test_677() {
		checkIsSubtype("!!null","null");
	}
	@Test public void test_678() {
		checkNotSubtype("!!null","int");
	}
	@Test public void test_679() {
		checkNotSubtype("!!null","[void]");
	}
	@Test public void test_680() {
		checkNotSubtype("!!null","[any]");
	}
	@Test public void test_681() {
		checkNotSubtype("!!null","[null]");
	}
	@Test public void test_682() {
		checkNotSubtype("!!null","[int]");
	}
	@Test public void test_683() {
		checkNotSubtype("!!null","!void");
	}
	@Test public void test_684() {
		checkIsSubtype("!!null","!any");
	}
	@Test public void test_685() {
		checkNotSubtype("!!null","!null");
	}
	@Test public void test_686() {
		checkNotSubtype("!!null","!int");
	}
	@Test public void test_687() {
		checkNotSubtype("!!null","[[void]]");
	}
	@Test public void test_688() {
		checkNotSubtype("!!null","[[any]]");
	}
	@Test public void test_689() {
		checkNotSubtype("!!null","[[null]]");
	}
	@Test public void test_690() {
		checkNotSubtype("!!null","[[int]]");
	}
	@Test public void test_691() {
		checkNotSubtype("!!null","[!void]");
	}
	@Test public void test_692() {
		checkNotSubtype("!!null","[!any]");
	}
	@Test public void test_693() {
		checkNotSubtype("!!null","[!null]");
	}
	@Test public void test_694() {
		checkNotSubtype("!!null","[!int]");
	}
	@Test public void test_695() {
		checkNotSubtype("!!null","![void]");
	}
	@Test public void test_696() {
		checkNotSubtype("!!null","![any]");
	}
	@Test public void test_697() {
		checkNotSubtype("!!null","![null]");
	}
	@Test public void test_698() {
		checkNotSubtype("!!null","![int]");
	}
	@Test public void test_699() {
		checkIsSubtype("!!null","!!void");
	}
	@Test public void test_700() {
		checkNotSubtype("!!null","!!any");
	}
	@Test public void test_701() {
		checkIsSubtype("!!null","!!null");
	}
	@Test public void test_702() {
		checkNotSubtype("!!null","!!int");
	}
	@Test public void test_703() {
		checkNotSubtype("!!int","any");
	}
	@Test public void test_704() {
		checkNotSubtype("!!int","null");
	}
	@Test public void test_705() {
		checkIsSubtype("!!int","int");
	}
	@Test public void test_706() {
		checkNotSubtype("!!int","[void]");
	}
	@Test public void test_707() {
		checkNotSubtype("!!int","[any]");
	}
	@Test public void test_708() {
		checkNotSubtype("!!int","[null]");
	}
	@Test public void test_709() {
		checkNotSubtype("!!int","[int]");
	}
	@Test public void test_710() {
		checkNotSubtype("!!int","!void");
	}
	@Test public void test_711() {
		checkIsSubtype("!!int","!any");
	}
	@Test public void test_712() {
		checkNotSubtype("!!int","!null");
	}
	@Test public void test_713() {
		checkNotSubtype("!!int","!int");
	}
	@Test public void test_714() {
		checkNotSubtype("!!int","[[void]]");
	}
	@Test public void test_715() {
		checkNotSubtype("!!int","[[any]]");
	}
	@Test public void test_716() {
		checkNotSubtype("!!int","[[null]]");
	}
	@Test public void test_717() {
		checkNotSubtype("!!int","[[int]]");
	}
	@Test public void test_718() {
		checkNotSubtype("!!int","[!void]");
	}
	@Test public void test_719() {
		checkNotSubtype("!!int","[!any]");
	}
	@Test public void test_720() {
		checkNotSubtype("!!int","[!null]");
	}
	@Test public void test_721() {
		checkNotSubtype("!!int","[!int]");
	}
	@Test public void test_722() {
		checkNotSubtype("!!int","![void]");
	}
	@Test public void test_723() {
		checkNotSubtype("!!int","![any]");
	}
	@Test public void test_724() {
		checkNotSubtype("!!int","![null]");
	}
	@Test public void test_725() {
		checkNotSubtype("!!int","![int]");
	}
	@Test public void test_726() {
		checkIsSubtype("!!int","!!void");
	}
	@Test public void test_727() {
		checkNotSubtype("!!int","!!any");
	}
	@Test public void test_728() {
		checkNotSubtype("!!int","!!null");
	}
	@Test public void test_729() {
		checkIsSubtype("!!int","!!int");
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
