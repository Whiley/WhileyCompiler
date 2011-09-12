// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class SubtypeTests {
	@Test public void test_1() { checkIsSubtype("any","any"); }
	@Test public void test_2() { checkIsSubtype("any","null"); }
	@Test public void test_3() { checkIsSubtype("any","int"); }
	@Test public void test_4() { checkIsSubtype("any","[void]"); }
	@Test public void test_5() { checkIsSubtype("any","[any]"); }
	@Test public void test_6() { checkIsSubtype("any","[null]"); }
	@Test public void test_7() { checkIsSubtype("any","[int]"); }
	@Test public void test_8() { checkIsSubtype("any","{void f1}"); }
	@Test public void test_9() { checkIsSubtype("any","{void f2}"); }
	@Test public void test_10() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_11() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_12() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_13() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_14() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_15() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_16() { checkIsSubtype("any","[[void]]"); }
	@Test public void test_17() { checkIsSubtype("any","[[any]]"); }
	@Test public void test_18() { checkIsSubtype("any","[[null]]"); }
	@Test public void test_19() { checkIsSubtype("any","[[int]]"); }
	@Test public void test_20() { checkIsSubtype("any","[{void f1}]"); }
	@Test public void test_21() { checkIsSubtype("any","[{void f2}]"); }
	@Test public void test_22() { checkIsSubtype("any","[{any f1}]"); }
	@Test public void test_23() { checkIsSubtype("any","[{any f2}]"); }
	@Test public void test_24() { checkIsSubtype("any","[{null f1}]"); }
	@Test public void test_25() { checkIsSubtype("any","[{null f2}]"); }
	@Test public void test_26() { checkIsSubtype("any","[{int f1}]"); }
	@Test public void test_27() { checkIsSubtype("any","[{int f2}]"); }
	@Test public void test_28() { checkIsSubtype("any","{void f1,void f2}"); }
	@Test public void test_29() { checkIsSubtype("any","{void f2,void f3}"); }
	@Test public void test_30() { checkIsSubtype("any","{void f1,any f2}"); }
	@Test public void test_31() { checkIsSubtype("any","{void f2,any f3}"); }
	@Test public void test_32() { checkIsSubtype("any","{void f1,null f2}"); }
	@Test public void test_33() { checkIsSubtype("any","{void f2,null f3}"); }
	@Test public void test_34() { checkIsSubtype("any","{void f1,int f2}"); }
	@Test public void test_35() { checkIsSubtype("any","{void f2,int f3}"); }
	@Test public void test_36() { checkIsSubtype("any","{any f1,void f2}"); }
	@Test public void test_37() { checkIsSubtype("any","{any f2,void f3}"); }
	@Test public void test_38() { checkIsSubtype("any","{any f1,any f2}"); }
	@Test public void test_39() { checkIsSubtype("any","{any f2,any f3}"); }
	@Test public void test_40() { checkIsSubtype("any","{any f1,null f2}"); }
	@Test public void test_41() { checkIsSubtype("any","{any f2,null f3}"); }
	@Test public void test_42() { checkIsSubtype("any","{any f1,int f2}"); }
	@Test public void test_43() { checkIsSubtype("any","{any f2,int f3}"); }
	@Test public void test_44() { checkIsSubtype("any","{null f1,void f2}"); }
	@Test public void test_45() { checkIsSubtype("any","{null f2,void f3}"); }
	@Test public void test_46() { checkIsSubtype("any","{null f1,any f2}"); }
	@Test public void test_47() { checkIsSubtype("any","{null f2,any f3}"); }
	@Test public void test_48() { checkIsSubtype("any","{null f1,null f2}"); }
	@Test public void test_49() { checkIsSubtype("any","{null f2,null f3}"); }
	@Test public void test_50() { checkIsSubtype("any","{null f1,int f2}"); }
	@Test public void test_51() { checkIsSubtype("any","{null f2,int f3}"); }
	@Test public void test_52() { checkIsSubtype("any","{int f1,void f2}"); }
	@Test public void test_53() { checkIsSubtype("any","{int f2,void f3}"); }
	@Test public void test_54() { checkIsSubtype("any","{int f1,any f2}"); }
	@Test public void test_55() { checkIsSubtype("any","{int f2,any f3}"); }
	@Test public void test_56() { checkIsSubtype("any","{int f1,null f2}"); }
	@Test public void test_57() { checkIsSubtype("any","{int f2,null f3}"); }
	@Test public void test_58() { checkIsSubtype("any","{int f1,int f2}"); }
	@Test public void test_59() { checkIsSubtype("any","{int f2,int f3}"); }
	@Test public void test_60() { checkIsSubtype("any","{[void] f1}"); }
	@Test public void test_61() { checkIsSubtype("any","{[void] f2}"); }
	@Test public void test_62() { checkIsSubtype("any","{[void] f1,void f2}"); }
	@Test public void test_63() { checkIsSubtype("any","{[void] f2,void f3}"); }
	@Test public void test_64() { checkIsSubtype("any","{[any] f1}"); }
	@Test public void test_65() { checkIsSubtype("any","{[any] f2}"); }
	@Test public void test_66() { checkIsSubtype("any","{[any] f1,any f2}"); }
	@Test public void test_67() { checkIsSubtype("any","{[any] f2,any f3}"); }
	@Test public void test_68() { checkIsSubtype("any","{[null] f1}"); }
	@Test public void test_69() { checkIsSubtype("any","{[null] f2}"); }
	@Test public void test_70() { checkIsSubtype("any","{[null] f1,null f2}"); }
	@Test public void test_71() { checkIsSubtype("any","{[null] f2,null f3}"); }
	@Test public void test_72() { checkIsSubtype("any","{[int] f1}"); }
	@Test public void test_73() { checkIsSubtype("any","{[int] f2}"); }
	@Test public void test_74() { checkIsSubtype("any","{[int] f1,int f2}"); }
	@Test public void test_75() { checkIsSubtype("any","{[int] f2,int f3}"); }
	@Test public void test_76() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_77() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_78() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_79() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_80() { checkIsSubtype("any","{{void f1} f1,void f2}"); }
	@Test public void test_81() { checkIsSubtype("any","{{void f2} f1,void f2}"); }
	@Test public void test_82() { checkIsSubtype("any","{{void f1} f2,void f3}"); }
	@Test public void test_83() { checkIsSubtype("any","{{void f2} f2,void f3}"); }
	@Test public void test_84() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_85() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_86() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_87() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_88() { checkIsSubtype("any","{{any f1} f1,any f2}"); }
	@Test public void test_89() { checkIsSubtype("any","{{any f2} f1,any f2}"); }
	@Test public void test_90() { checkIsSubtype("any","{{any f1} f2,any f3}"); }
	@Test public void test_91() { checkIsSubtype("any","{{any f2} f2,any f3}"); }
	@Test public void test_92() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_93() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_94() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_95() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_96() { checkIsSubtype("any","{{null f1} f1,null f2}"); }
	@Test public void test_97() { checkIsSubtype("any","{{null f2} f1,null f2}"); }
	@Test public void test_98() { checkIsSubtype("any","{{null f1} f2,null f3}"); }
	@Test public void test_99() { checkIsSubtype("any","{{null f2} f2,null f3}"); }
	@Test public void test_100() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_101() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_102() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_103() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_104() { checkIsSubtype("any","{{int f1} f1,int f2}"); }
	@Test public void test_105() { checkIsSubtype("any","{{int f2} f1,int f2}"); }
	@Test public void test_106() { checkIsSubtype("any","{{int f1} f2,int f3}"); }
	@Test public void test_107() { checkIsSubtype("any","{{int f2} f2,int f3}"); }
	@Test public void test_108() { checkNotSubtype("null","any"); }
	@Test public void test_109() { checkIsSubtype("null","null"); }
	@Test public void test_110() { checkNotSubtype("null","int"); }
	@Test public void test_111() { checkNotSubtype("null","[void]"); }
	@Test public void test_112() { checkNotSubtype("null","[any]"); }
	@Test public void test_113() { checkNotSubtype("null","[null]"); }
	@Test public void test_114() { checkNotSubtype("null","[int]"); }
	@Test public void test_115() { checkIsSubtype("null","{void f1}"); }
	@Test public void test_116() { checkIsSubtype("null","{void f2}"); }
	@Test public void test_117() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_118() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_119() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_120() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_121() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_122() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_123() { checkNotSubtype("null","[[void]]"); }
	@Test public void test_124() { checkNotSubtype("null","[[any]]"); }
	@Test public void test_125() { checkNotSubtype("null","[[null]]"); }
	@Test public void test_126() { checkNotSubtype("null","[[int]]"); }
	@Test public void test_127() { checkNotSubtype("null","[{void f1}]"); }
	@Test public void test_128() { checkNotSubtype("null","[{void f2}]"); }
	@Test public void test_129() { checkNotSubtype("null","[{any f1}]"); }
	@Test public void test_130() { checkNotSubtype("null","[{any f2}]"); }
	@Test public void test_131() { checkNotSubtype("null","[{null f1}]"); }
	@Test public void test_132() { checkNotSubtype("null","[{null f2}]"); }
	@Test public void test_133() { checkNotSubtype("null","[{int f1}]"); }
	@Test public void test_134() { checkNotSubtype("null","[{int f2}]"); }
	@Test public void test_135() { checkIsSubtype("null","{void f1,void f2}"); }
	@Test public void test_136() { checkIsSubtype("null","{void f2,void f3}"); }
	@Test public void test_137() { checkIsSubtype("null","{void f1,any f2}"); }
	@Test public void test_138() { checkIsSubtype("null","{void f2,any f3}"); }
	@Test public void test_139() { checkIsSubtype("null","{void f1,null f2}"); }
	@Test public void test_140() { checkIsSubtype("null","{void f2,null f3}"); }
	@Test public void test_141() { checkIsSubtype("null","{void f1,int f2}"); }
	@Test public void test_142() { checkIsSubtype("null","{void f2,int f3}"); }
	@Test public void test_143() { checkIsSubtype("null","{any f1,void f2}"); }
	@Test public void test_144() { checkIsSubtype("null","{any f2,void f3}"); }
	@Test public void test_145() { checkNotSubtype("null","{any f1,any f2}"); }
	@Test public void test_146() { checkNotSubtype("null","{any f2,any f3}"); }
	@Test public void test_147() { checkNotSubtype("null","{any f1,null f2}"); }
	@Test public void test_148() { checkNotSubtype("null","{any f2,null f3}"); }
	@Test public void test_149() { checkNotSubtype("null","{any f1,int f2}"); }
	@Test public void test_150() { checkNotSubtype("null","{any f2,int f3}"); }
	@Test public void test_151() { checkIsSubtype("null","{null f1,void f2}"); }
	@Test public void test_152() { checkIsSubtype("null","{null f2,void f3}"); }
	@Test public void test_153() { checkNotSubtype("null","{null f1,any f2}"); }
	@Test public void test_154() { checkNotSubtype("null","{null f2,any f3}"); }
	@Test public void test_155() { checkNotSubtype("null","{null f1,null f2}"); }
	@Test public void test_156() { checkNotSubtype("null","{null f2,null f3}"); }
	@Test public void test_157() { checkNotSubtype("null","{null f1,int f2}"); }
	@Test public void test_158() { checkNotSubtype("null","{null f2,int f3}"); }
	@Test public void test_159() { checkIsSubtype("null","{int f1,void f2}"); }
	@Test public void test_160() { checkIsSubtype("null","{int f2,void f3}"); }
	@Test public void test_161() { checkNotSubtype("null","{int f1,any f2}"); }
	@Test public void test_162() { checkNotSubtype("null","{int f2,any f3}"); }
	@Test public void test_163() { checkNotSubtype("null","{int f1,null f2}"); }
	@Test public void test_164() { checkNotSubtype("null","{int f2,null f3}"); }
	@Test public void test_165() { checkNotSubtype("null","{int f1,int f2}"); }
	@Test public void test_166() { checkNotSubtype("null","{int f2,int f3}"); }
	@Test public void test_167() { checkNotSubtype("null","{[void] f1}"); }
	@Test public void test_168() { checkNotSubtype("null","{[void] f2}"); }
	@Test public void test_169() { checkIsSubtype("null","{[void] f1,void f2}"); }
	@Test public void test_170() { checkIsSubtype("null","{[void] f2,void f3}"); }
	@Test public void test_171() { checkNotSubtype("null","{[any] f1}"); }
	@Test public void test_172() { checkNotSubtype("null","{[any] f2}"); }
	@Test public void test_173() { checkNotSubtype("null","{[any] f1,any f2}"); }
	@Test public void test_174() { checkNotSubtype("null","{[any] f2,any f3}"); }
	@Test public void test_175() { checkNotSubtype("null","{[null] f1}"); }
	@Test public void test_176() { checkNotSubtype("null","{[null] f2}"); }
	@Test public void test_177() { checkNotSubtype("null","{[null] f1,null f2}"); }
	@Test public void test_178() { checkNotSubtype("null","{[null] f2,null f3}"); }
	@Test public void test_179() { checkNotSubtype("null","{[int] f1}"); }
	@Test public void test_180() { checkNotSubtype("null","{[int] f2}"); }
	@Test public void test_181() { checkNotSubtype("null","{[int] f1,int f2}"); }
	@Test public void test_182() { checkNotSubtype("null","{[int] f2,int f3}"); }
	@Test public void test_183() { checkIsSubtype("null","{{void f1} f1}"); }
	@Test public void test_184() { checkIsSubtype("null","{{void f2} f1}"); }
	@Test public void test_185() { checkIsSubtype("null","{{void f1} f2}"); }
	@Test public void test_186() { checkIsSubtype("null","{{void f2} f2}"); }
	@Test public void test_187() { checkIsSubtype("null","{{void f1} f1,void f2}"); }
	@Test public void test_188() { checkIsSubtype("null","{{void f2} f1,void f2}"); }
	@Test public void test_189() { checkIsSubtype("null","{{void f1} f2,void f3}"); }
	@Test public void test_190() { checkIsSubtype("null","{{void f2} f2,void f3}"); }
	@Test public void test_191() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_192() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_193() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_194() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_195() { checkNotSubtype("null","{{any f1} f1,any f2}"); }
	@Test public void test_196() { checkNotSubtype("null","{{any f2} f1,any f2}"); }
	@Test public void test_197() { checkNotSubtype("null","{{any f1} f2,any f3}"); }
	@Test public void test_198() { checkNotSubtype("null","{{any f2} f2,any f3}"); }
	@Test public void test_199() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_200() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_201() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_202() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_203() { checkNotSubtype("null","{{null f1} f1,null f2}"); }
	@Test public void test_204() { checkNotSubtype("null","{{null f2} f1,null f2}"); }
	@Test public void test_205() { checkNotSubtype("null","{{null f1} f2,null f3}"); }
	@Test public void test_206() { checkNotSubtype("null","{{null f2} f2,null f3}"); }
	@Test public void test_207() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_208() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_209() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_210() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_211() { checkNotSubtype("null","{{int f1} f1,int f2}"); }
	@Test public void test_212() { checkNotSubtype("null","{{int f2} f1,int f2}"); }
	@Test public void test_213() { checkNotSubtype("null","{{int f1} f2,int f3}"); }
	@Test public void test_214() { checkNotSubtype("null","{{int f2} f2,int f3}"); }
	@Test public void test_215() { checkNotSubtype("int","any"); }
	@Test public void test_216() { checkNotSubtype("int","null"); }
	@Test public void test_217() { checkIsSubtype("int","int"); }
	@Test public void test_218() { checkNotSubtype("int","[void]"); }
	@Test public void test_219() { checkNotSubtype("int","[any]"); }
	@Test public void test_220() { checkNotSubtype("int","[null]"); }
	@Test public void test_221() { checkNotSubtype("int","[int]"); }
	@Test public void test_222() { checkIsSubtype("int","{void f1}"); }
	@Test public void test_223() { checkIsSubtype("int","{void f2}"); }
	@Test public void test_224() { checkNotSubtype("int","{any f1}"); }
	@Test public void test_225() { checkNotSubtype("int","{any f2}"); }
	@Test public void test_226() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_227() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_228() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_229() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_230() { checkNotSubtype("int","[[void]]"); }
	@Test public void test_231() { checkNotSubtype("int","[[any]]"); }
	@Test public void test_232() { checkNotSubtype("int","[[null]]"); }
	@Test public void test_233() { checkNotSubtype("int","[[int]]"); }
	@Test public void test_234() { checkNotSubtype("int","[{void f1}]"); }
	@Test public void test_235() { checkNotSubtype("int","[{void f2}]"); }
	@Test public void test_236() { checkNotSubtype("int","[{any f1}]"); }
	@Test public void test_237() { checkNotSubtype("int","[{any f2}]"); }
	@Test public void test_238() { checkNotSubtype("int","[{null f1}]"); }
	@Test public void test_239() { checkNotSubtype("int","[{null f2}]"); }
	@Test public void test_240() { checkNotSubtype("int","[{int f1}]"); }
	@Test public void test_241() { checkNotSubtype("int","[{int f2}]"); }
	@Test public void test_242() { checkIsSubtype("int","{void f1,void f2}"); }
	@Test public void test_243() { checkIsSubtype("int","{void f2,void f3}"); }
	@Test public void test_244() { checkIsSubtype("int","{void f1,any f2}"); }
	@Test public void test_245() { checkIsSubtype("int","{void f2,any f3}"); }
	@Test public void test_246() { checkIsSubtype("int","{void f1,null f2}"); }
	@Test public void test_247() { checkIsSubtype("int","{void f2,null f3}"); }
	@Test public void test_248() { checkIsSubtype("int","{void f1,int f2}"); }
	@Test public void test_249() { checkIsSubtype("int","{void f2,int f3}"); }
	@Test public void test_250() { checkIsSubtype("int","{any f1,void f2}"); }
	@Test public void test_251() { checkIsSubtype("int","{any f2,void f3}"); }
	@Test public void test_252() { checkNotSubtype("int","{any f1,any f2}"); }
	@Test public void test_253() { checkNotSubtype("int","{any f2,any f3}"); }
	@Test public void test_254() { checkNotSubtype("int","{any f1,null f2}"); }
	@Test public void test_255() { checkNotSubtype("int","{any f2,null f3}"); }
	@Test public void test_256() { checkNotSubtype("int","{any f1,int f2}"); }
	@Test public void test_257() { checkNotSubtype("int","{any f2,int f3}"); }
	@Test public void test_258() { checkIsSubtype("int","{null f1,void f2}"); }
	@Test public void test_259() { checkIsSubtype("int","{null f2,void f3}"); }
	@Test public void test_260() { checkNotSubtype("int","{null f1,any f2}"); }
	@Test public void test_261() { checkNotSubtype("int","{null f2,any f3}"); }
	@Test public void test_262() { checkNotSubtype("int","{null f1,null f2}"); }
	@Test public void test_263() { checkNotSubtype("int","{null f2,null f3}"); }
	@Test public void test_264() { checkNotSubtype("int","{null f1,int f2}"); }
	@Test public void test_265() { checkNotSubtype("int","{null f2,int f3}"); }
	@Test public void test_266() { checkIsSubtype("int","{int f1,void f2}"); }
	@Test public void test_267() { checkIsSubtype("int","{int f2,void f3}"); }
	@Test public void test_268() { checkNotSubtype("int","{int f1,any f2}"); }
	@Test public void test_269() { checkNotSubtype("int","{int f2,any f3}"); }
	@Test public void test_270() { checkNotSubtype("int","{int f1,null f2}"); }
	@Test public void test_271() { checkNotSubtype("int","{int f2,null f3}"); }
	@Test public void test_272() { checkNotSubtype("int","{int f1,int f2}"); }
	@Test public void test_273() { checkNotSubtype("int","{int f2,int f3}"); }
	@Test public void test_274() { checkNotSubtype("int","{[void] f1}"); }
	@Test public void test_275() { checkNotSubtype("int","{[void] f2}"); }
	@Test public void test_276() { checkIsSubtype("int","{[void] f1,void f2}"); }
	@Test public void test_277() { checkIsSubtype("int","{[void] f2,void f3}"); }
	@Test public void test_278() { checkNotSubtype("int","{[any] f1}"); }
	@Test public void test_279() { checkNotSubtype("int","{[any] f2}"); }
	@Test public void test_280() { checkNotSubtype("int","{[any] f1,any f2}"); }
	@Test public void test_281() { checkNotSubtype("int","{[any] f2,any f3}"); }
	@Test public void test_282() { checkNotSubtype("int","{[null] f1}"); }
	@Test public void test_283() { checkNotSubtype("int","{[null] f2}"); }
	@Test public void test_284() { checkNotSubtype("int","{[null] f1,null f2}"); }
	@Test public void test_285() { checkNotSubtype("int","{[null] f2,null f3}"); }
	@Test public void test_286() { checkNotSubtype("int","{[int] f1}"); }
	@Test public void test_287() { checkNotSubtype("int","{[int] f2}"); }
	@Test public void test_288() { checkNotSubtype("int","{[int] f1,int f2}"); }
	@Test public void test_289() { checkNotSubtype("int","{[int] f2,int f3}"); }
	@Test public void test_290() { checkIsSubtype("int","{{void f1} f1}"); }
	@Test public void test_291() { checkIsSubtype("int","{{void f2} f1}"); }
	@Test public void test_292() { checkIsSubtype("int","{{void f1} f2}"); }
	@Test public void test_293() { checkIsSubtype("int","{{void f2} f2}"); }
	@Test public void test_294() { checkIsSubtype("int","{{void f1} f1,void f2}"); }
	@Test public void test_295() { checkIsSubtype("int","{{void f2} f1,void f2}"); }
	@Test public void test_296() { checkIsSubtype("int","{{void f1} f2,void f3}"); }
	@Test public void test_297() { checkIsSubtype("int","{{void f2} f2,void f3}"); }
	@Test public void test_298() { checkNotSubtype("int","{{any f1} f1}"); }
	@Test public void test_299() { checkNotSubtype("int","{{any f2} f1}"); }
	@Test public void test_300() { checkNotSubtype("int","{{any f1} f2}"); }
	@Test public void test_301() { checkNotSubtype("int","{{any f2} f2}"); }
	@Test public void test_302() { checkNotSubtype("int","{{any f1} f1,any f2}"); }
	@Test public void test_303() { checkNotSubtype("int","{{any f2} f1,any f2}"); }
	@Test public void test_304() { checkNotSubtype("int","{{any f1} f2,any f3}"); }
	@Test public void test_305() { checkNotSubtype("int","{{any f2} f2,any f3}"); }
	@Test public void test_306() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_307() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_308() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_309() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_310() { checkNotSubtype("int","{{null f1} f1,null f2}"); }
	@Test public void test_311() { checkNotSubtype("int","{{null f2} f1,null f2}"); }
	@Test public void test_312() { checkNotSubtype("int","{{null f1} f2,null f3}"); }
	@Test public void test_313() { checkNotSubtype("int","{{null f2} f2,null f3}"); }
	@Test public void test_314() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_315() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_316() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_317() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_318() { checkNotSubtype("int","{{int f1} f1,int f2}"); }
	@Test public void test_319() { checkNotSubtype("int","{{int f2} f1,int f2}"); }
	@Test public void test_320() { checkNotSubtype("int","{{int f1} f2,int f3}"); }
	@Test public void test_321() { checkNotSubtype("int","{{int f2} f2,int f3}"); }
	@Test public void test_322() { checkNotSubtype("[void]","any"); }
	@Test public void test_323() { checkNotSubtype("[void]","null"); }
	@Test public void test_324() { checkNotSubtype("[void]","int"); }
	@Test public void test_325() { checkIsSubtype("[void]","[void]"); }
	@Test public void test_326() { checkNotSubtype("[void]","[any]"); }
	@Test public void test_327() { checkNotSubtype("[void]","[null]"); }
	@Test public void test_328() { checkNotSubtype("[void]","[int]"); }
	@Test public void test_329() { checkIsSubtype("[void]","{void f1}"); }
	@Test public void test_330() { checkIsSubtype("[void]","{void f2}"); }
	@Test public void test_331() { checkNotSubtype("[void]","{any f1}"); }
	@Test public void test_332() { checkNotSubtype("[void]","{any f2}"); }
	@Test public void test_333() { checkNotSubtype("[void]","{null f1}"); }
	@Test public void test_334() { checkNotSubtype("[void]","{null f2}"); }
	@Test public void test_335() { checkNotSubtype("[void]","{int f1}"); }
	@Test public void test_336() { checkNotSubtype("[void]","{int f2}"); }
	@Test public void test_337() { checkNotSubtype("[void]","[[void]]"); }
	@Test public void test_338() { checkNotSubtype("[void]","[[any]]"); }
	@Test public void test_339() { checkNotSubtype("[void]","[[null]]"); }
	@Test public void test_340() { checkNotSubtype("[void]","[[int]]"); }
	@Test public void test_341() { checkIsSubtype("[void]","[{void f1}]"); }
	@Test public void test_342() { checkIsSubtype("[void]","[{void f2}]"); }
	@Test public void test_343() { checkNotSubtype("[void]","[{any f1}]"); }
	@Test public void test_344() { checkNotSubtype("[void]","[{any f2}]"); }
	@Test public void test_345() { checkNotSubtype("[void]","[{null f1}]"); }
	@Test public void test_346() { checkNotSubtype("[void]","[{null f2}]"); }
	@Test public void test_347() { checkNotSubtype("[void]","[{int f1}]"); }
	@Test public void test_348() { checkNotSubtype("[void]","[{int f2}]"); }
	@Test public void test_349() { checkIsSubtype("[void]","{void f1,void f2}"); }
	@Test public void test_350() { checkIsSubtype("[void]","{void f2,void f3}"); }
	@Test public void test_351() { checkIsSubtype("[void]","{void f1,any f2}"); }
	@Test public void test_352() { checkIsSubtype("[void]","{void f2,any f3}"); }
	@Test public void test_353() { checkIsSubtype("[void]","{void f1,null f2}"); }
	@Test public void test_354() { checkIsSubtype("[void]","{void f2,null f3}"); }
	@Test public void test_355() { checkIsSubtype("[void]","{void f1,int f2}"); }
	@Test public void test_356() { checkIsSubtype("[void]","{void f2,int f3}"); }
	@Test public void test_357() { checkIsSubtype("[void]","{any f1,void f2}"); }
	@Test public void test_358() { checkIsSubtype("[void]","{any f2,void f3}"); }
	@Test public void test_359() { checkNotSubtype("[void]","{any f1,any f2}"); }
	@Test public void test_360() { checkNotSubtype("[void]","{any f2,any f3}"); }
	@Test public void test_361() { checkNotSubtype("[void]","{any f1,null f2}"); }
	@Test public void test_362() { checkNotSubtype("[void]","{any f2,null f3}"); }
	@Test public void test_363() { checkNotSubtype("[void]","{any f1,int f2}"); }
	@Test public void test_364() { checkNotSubtype("[void]","{any f2,int f3}"); }
	@Test public void test_365() { checkIsSubtype("[void]","{null f1,void f2}"); }
	@Test public void test_366() { checkIsSubtype("[void]","{null f2,void f3}"); }
	@Test public void test_367() { checkNotSubtype("[void]","{null f1,any f2}"); }
	@Test public void test_368() { checkNotSubtype("[void]","{null f2,any f3}"); }
	@Test public void test_369() { checkNotSubtype("[void]","{null f1,null f2}"); }
	@Test public void test_370() { checkNotSubtype("[void]","{null f2,null f3}"); }
	@Test public void test_371() { checkNotSubtype("[void]","{null f1,int f2}"); }
	@Test public void test_372() { checkNotSubtype("[void]","{null f2,int f3}"); }
	@Test public void test_373() { checkIsSubtype("[void]","{int f1,void f2}"); }
	@Test public void test_374() { checkIsSubtype("[void]","{int f2,void f3}"); }
	@Test public void test_375() { checkNotSubtype("[void]","{int f1,any f2}"); }
	@Test public void test_376() { checkNotSubtype("[void]","{int f2,any f3}"); }
	@Test public void test_377() { checkNotSubtype("[void]","{int f1,null f2}"); }
	@Test public void test_378() { checkNotSubtype("[void]","{int f2,null f3}"); }
	@Test public void test_379() { checkNotSubtype("[void]","{int f1,int f2}"); }
	@Test public void test_380() { checkNotSubtype("[void]","{int f2,int f3}"); }
	@Test public void test_381() { checkNotSubtype("[void]","{[void] f1}"); }
	@Test public void test_382() { checkNotSubtype("[void]","{[void] f2}"); }
	@Test public void test_383() { checkIsSubtype("[void]","{[void] f1,void f2}"); }
	@Test public void test_384() { checkIsSubtype("[void]","{[void] f2,void f3}"); }
	@Test public void test_385() { checkNotSubtype("[void]","{[any] f1}"); }
	@Test public void test_386() { checkNotSubtype("[void]","{[any] f2}"); }
	@Test public void test_387() { checkNotSubtype("[void]","{[any] f1,any f2}"); }
	@Test public void test_388() { checkNotSubtype("[void]","{[any] f2,any f3}"); }
	@Test public void test_389() { checkNotSubtype("[void]","{[null] f1}"); }
	@Test public void test_390() { checkNotSubtype("[void]","{[null] f2}"); }
	@Test public void test_391() { checkNotSubtype("[void]","{[null] f1,null f2}"); }
	@Test public void test_392() { checkNotSubtype("[void]","{[null] f2,null f3}"); }
	@Test public void test_393() { checkNotSubtype("[void]","{[int] f1}"); }
	@Test public void test_394() { checkNotSubtype("[void]","{[int] f2}"); }
	@Test public void test_395() { checkNotSubtype("[void]","{[int] f1,int f2}"); }
	@Test public void test_396() { checkNotSubtype("[void]","{[int] f2,int f3}"); }
	@Test public void test_397() { checkIsSubtype("[void]","{{void f1} f1}"); }
	@Test public void test_398() { checkIsSubtype("[void]","{{void f2} f1}"); }
	@Test public void test_399() { checkIsSubtype("[void]","{{void f1} f2}"); }
	@Test public void test_400() { checkIsSubtype("[void]","{{void f2} f2}"); }
	@Test public void test_401() { checkIsSubtype("[void]","{{void f1} f1,void f2}"); }
	@Test public void test_402() { checkIsSubtype("[void]","{{void f2} f1,void f2}"); }
	@Test public void test_403() { checkIsSubtype("[void]","{{void f1} f2,void f3}"); }
	@Test public void test_404() { checkIsSubtype("[void]","{{void f2} f2,void f3}"); }
	@Test public void test_405() { checkNotSubtype("[void]","{{any f1} f1}"); }
	@Test public void test_406() { checkNotSubtype("[void]","{{any f2} f1}"); }
	@Test public void test_407() { checkNotSubtype("[void]","{{any f1} f2}"); }
	@Test public void test_408() { checkNotSubtype("[void]","{{any f2} f2}"); }
	@Test public void test_409() { checkNotSubtype("[void]","{{any f1} f1,any f2}"); }
	@Test public void test_410() { checkNotSubtype("[void]","{{any f2} f1,any f2}"); }
	@Test public void test_411() { checkNotSubtype("[void]","{{any f1} f2,any f3}"); }
	@Test public void test_412() { checkNotSubtype("[void]","{{any f2} f2,any f3}"); }
	@Test public void test_413() { checkNotSubtype("[void]","{{null f1} f1}"); }
	@Test public void test_414() { checkNotSubtype("[void]","{{null f2} f1}"); }
	@Test public void test_415() { checkNotSubtype("[void]","{{null f1} f2}"); }
	@Test public void test_416() { checkNotSubtype("[void]","{{null f2} f2}"); }
	@Test public void test_417() { checkNotSubtype("[void]","{{null f1} f1,null f2}"); }
	@Test public void test_418() { checkNotSubtype("[void]","{{null f2} f1,null f2}"); }
	@Test public void test_419() { checkNotSubtype("[void]","{{null f1} f2,null f3}"); }
	@Test public void test_420() { checkNotSubtype("[void]","{{null f2} f2,null f3}"); }
	@Test public void test_421() { checkNotSubtype("[void]","{{int f1} f1}"); }
	@Test public void test_422() { checkNotSubtype("[void]","{{int f2} f1}"); }
	@Test public void test_423() { checkNotSubtype("[void]","{{int f1} f2}"); }
	@Test public void test_424() { checkNotSubtype("[void]","{{int f2} f2}"); }
	@Test public void test_425() { checkNotSubtype("[void]","{{int f1} f1,int f2}"); }
	@Test public void test_426() { checkNotSubtype("[void]","{{int f2} f1,int f2}"); }
	@Test public void test_427() { checkNotSubtype("[void]","{{int f1} f2,int f3}"); }
	@Test public void test_428() { checkNotSubtype("[void]","{{int f2} f2,int f3}"); }
	@Test public void test_429() { checkNotSubtype("[any]","any"); }
	@Test public void test_430() { checkNotSubtype("[any]","null"); }
	@Test public void test_431() { checkNotSubtype("[any]","int"); }
	@Test public void test_432() { checkIsSubtype("[any]","[void]"); }
	@Test public void test_433() { checkIsSubtype("[any]","[any]"); }
	@Test public void test_434() { checkIsSubtype("[any]","[null]"); }
	@Test public void test_435() { checkIsSubtype("[any]","[int]"); }
	@Test public void test_436() { checkIsSubtype("[any]","{void f1}"); }
	@Test public void test_437() { checkIsSubtype("[any]","{void f2}"); }
	@Test public void test_438() { checkNotSubtype("[any]","{any f1}"); }
	@Test public void test_439() { checkNotSubtype("[any]","{any f2}"); }
	@Test public void test_440() { checkNotSubtype("[any]","{null f1}"); }
	@Test public void test_441() { checkNotSubtype("[any]","{null f2}"); }
	@Test public void test_442() { checkNotSubtype("[any]","{int f1}"); }
	@Test public void test_443() { checkNotSubtype("[any]","{int f2}"); }
	@Test public void test_444() { checkIsSubtype("[any]","[[void]]"); }
	@Test public void test_445() { checkIsSubtype("[any]","[[any]]"); }
	@Test public void test_446() { checkIsSubtype("[any]","[[null]]"); }
	@Test public void test_447() { checkIsSubtype("[any]","[[int]]"); }
	@Test public void test_448() { checkIsSubtype("[any]","[{void f1}]"); }
	@Test public void test_449() { checkIsSubtype("[any]","[{void f2}]"); }
	@Test public void test_450() { checkIsSubtype("[any]","[{any f1}]"); }
	@Test public void test_451() { checkIsSubtype("[any]","[{any f2}]"); }
	@Test public void test_452() { checkIsSubtype("[any]","[{null f1}]"); }
	@Test public void test_453() { checkIsSubtype("[any]","[{null f2}]"); }
	@Test public void test_454() { checkIsSubtype("[any]","[{int f1}]"); }
	@Test public void test_455() { checkIsSubtype("[any]","[{int f2}]"); }
	@Test public void test_456() { checkIsSubtype("[any]","{void f1,void f2}"); }
	@Test public void test_457() { checkIsSubtype("[any]","{void f2,void f3}"); }
	@Test public void test_458() { checkIsSubtype("[any]","{void f1,any f2}"); }
	@Test public void test_459() { checkIsSubtype("[any]","{void f2,any f3}"); }
	@Test public void test_460() { checkIsSubtype("[any]","{void f1,null f2}"); }
	@Test public void test_461() { checkIsSubtype("[any]","{void f2,null f3}"); }
	@Test public void test_462() { checkIsSubtype("[any]","{void f1,int f2}"); }
	@Test public void test_463() { checkIsSubtype("[any]","{void f2,int f3}"); }
	@Test public void test_464() { checkIsSubtype("[any]","{any f1,void f2}"); }
	@Test public void test_465() { checkIsSubtype("[any]","{any f2,void f3}"); }
	@Test public void test_466() { checkNotSubtype("[any]","{any f1,any f2}"); }
	@Test public void test_467() { checkNotSubtype("[any]","{any f2,any f3}"); }
	@Test public void test_468() { checkNotSubtype("[any]","{any f1,null f2}"); }
	@Test public void test_469() { checkNotSubtype("[any]","{any f2,null f3}"); }
	@Test public void test_470() { checkNotSubtype("[any]","{any f1,int f2}"); }
	@Test public void test_471() { checkNotSubtype("[any]","{any f2,int f3}"); }
	@Test public void test_472() { checkIsSubtype("[any]","{null f1,void f2}"); }
	@Test public void test_473() { checkIsSubtype("[any]","{null f2,void f3}"); }
	@Test public void test_474() { checkNotSubtype("[any]","{null f1,any f2}"); }
	@Test public void test_475() { checkNotSubtype("[any]","{null f2,any f3}"); }
	@Test public void test_476() { checkNotSubtype("[any]","{null f1,null f2}"); }
	@Test public void test_477() { checkNotSubtype("[any]","{null f2,null f3}"); }
	@Test public void test_478() { checkNotSubtype("[any]","{null f1,int f2}"); }
	@Test public void test_479() { checkNotSubtype("[any]","{null f2,int f3}"); }
	@Test public void test_480() { checkIsSubtype("[any]","{int f1,void f2}"); }
	@Test public void test_481() { checkIsSubtype("[any]","{int f2,void f3}"); }
	@Test public void test_482() { checkNotSubtype("[any]","{int f1,any f2}"); }
	@Test public void test_483() { checkNotSubtype("[any]","{int f2,any f3}"); }
	@Test public void test_484() { checkNotSubtype("[any]","{int f1,null f2}"); }
	@Test public void test_485() { checkNotSubtype("[any]","{int f2,null f3}"); }
	@Test public void test_486() { checkNotSubtype("[any]","{int f1,int f2}"); }
	@Test public void test_487() { checkNotSubtype("[any]","{int f2,int f3}"); }
	@Test public void test_488() { checkNotSubtype("[any]","{[void] f1}"); }
	@Test public void test_489() { checkNotSubtype("[any]","{[void] f2}"); }
	@Test public void test_490() { checkIsSubtype("[any]","{[void] f1,void f2}"); }
	@Test public void test_491() { checkIsSubtype("[any]","{[void] f2,void f3}"); }
	@Test public void test_492() { checkNotSubtype("[any]","{[any] f1}"); }
	@Test public void test_493() { checkNotSubtype("[any]","{[any] f2}"); }
	@Test public void test_494() { checkNotSubtype("[any]","{[any] f1,any f2}"); }
	@Test public void test_495() { checkNotSubtype("[any]","{[any] f2,any f3}"); }
	@Test public void test_496() { checkNotSubtype("[any]","{[null] f1}"); }
	@Test public void test_497() { checkNotSubtype("[any]","{[null] f2}"); }
	@Test public void test_498() { checkNotSubtype("[any]","{[null] f1,null f2}"); }
	@Test public void test_499() { checkNotSubtype("[any]","{[null] f2,null f3}"); }
	@Test public void test_500() { checkNotSubtype("[any]","{[int] f1}"); }
	@Test public void test_501() { checkNotSubtype("[any]","{[int] f2}"); }
	@Test public void test_502() { checkNotSubtype("[any]","{[int] f1,int f2}"); }
	@Test public void test_503() { checkNotSubtype("[any]","{[int] f2,int f3}"); }
	@Test public void test_504() { checkIsSubtype("[any]","{{void f1} f1}"); }
	@Test public void test_505() { checkIsSubtype("[any]","{{void f2} f1}"); }
	@Test public void test_506() { checkIsSubtype("[any]","{{void f1} f2}"); }
	@Test public void test_507() { checkIsSubtype("[any]","{{void f2} f2}"); }
	@Test public void test_508() { checkIsSubtype("[any]","{{void f1} f1,void f2}"); }
	@Test public void test_509() { checkIsSubtype("[any]","{{void f2} f1,void f2}"); }
	@Test public void test_510() { checkIsSubtype("[any]","{{void f1} f2,void f3}"); }
	@Test public void test_511() { checkIsSubtype("[any]","{{void f2} f2,void f3}"); }
	@Test public void test_512() { checkNotSubtype("[any]","{{any f1} f1}"); }
	@Test public void test_513() { checkNotSubtype("[any]","{{any f2} f1}"); }
	@Test public void test_514() { checkNotSubtype("[any]","{{any f1} f2}"); }
	@Test public void test_515() { checkNotSubtype("[any]","{{any f2} f2}"); }
	@Test public void test_516() { checkNotSubtype("[any]","{{any f1} f1,any f2}"); }
	@Test public void test_517() { checkNotSubtype("[any]","{{any f2} f1,any f2}"); }
	@Test public void test_518() { checkNotSubtype("[any]","{{any f1} f2,any f3}"); }
	@Test public void test_519() { checkNotSubtype("[any]","{{any f2} f2,any f3}"); }
	@Test public void test_520() { checkNotSubtype("[any]","{{null f1} f1}"); }
	@Test public void test_521() { checkNotSubtype("[any]","{{null f2} f1}"); }
	@Test public void test_522() { checkNotSubtype("[any]","{{null f1} f2}"); }
	@Test public void test_523() { checkNotSubtype("[any]","{{null f2} f2}"); }
	@Test public void test_524() { checkNotSubtype("[any]","{{null f1} f1,null f2}"); }
	@Test public void test_525() { checkNotSubtype("[any]","{{null f2} f1,null f2}"); }
	@Test public void test_526() { checkNotSubtype("[any]","{{null f1} f2,null f3}"); }
	@Test public void test_527() { checkNotSubtype("[any]","{{null f2} f2,null f3}"); }
	@Test public void test_528() { checkNotSubtype("[any]","{{int f1} f1}"); }
	@Test public void test_529() { checkNotSubtype("[any]","{{int f2} f1}"); }
	@Test public void test_530() { checkNotSubtype("[any]","{{int f1} f2}"); }
	@Test public void test_531() { checkNotSubtype("[any]","{{int f2} f2}"); }
	@Test public void test_532() { checkNotSubtype("[any]","{{int f1} f1,int f2}"); }
	@Test public void test_533() { checkNotSubtype("[any]","{{int f2} f1,int f2}"); }
	@Test public void test_534() { checkNotSubtype("[any]","{{int f1} f2,int f3}"); }
	@Test public void test_535() { checkNotSubtype("[any]","{{int f2} f2,int f3}"); }
	@Test public void test_536() { checkNotSubtype("[null]","any"); }
	@Test public void test_537() { checkNotSubtype("[null]","null"); }
	@Test public void test_538() { checkNotSubtype("[null]","int"); }
	@Test public void test_539() { checkIsSubtype("[null]","[void]"); }
	@Test public void test_540() { checkNotSubtype("[null]","[any]"); }
	@Test public void test_541() { checkIsSubtype("[null]","[null]"); }
	@Test public void test_542() { checkNotSubtype("[null]","[int]"); }
	@Test public void test_543() { checkIsSubtype("[null]","{void f1}"); }
	@Test public void test_544() { checkIsSubtype("[null]","{void f2}"); }
	@Test public void test_545() { checkNotSubtype("[null]","{any f1}"); }
	@Test public void test_546() { checkNotSubtype("[null]","{any f2}"); }
	@Test public void test_547() { checkNotSubtype("[null]","{null f1}"); }
	@Test public void test_548() { checkNotSubtype("[null]","{null f2}"); }
	@Test public void test_549() { checkNotSubtype("[null]","{int f1}"); }
	@Test public void test_550() { checkNotSubtype("[null]","{int f2}"); }
	@Test public void test_551() { checkNotSubtype("[null]","[[void]]"); }
	@Test public void test_552() { checkNotSubtype("[null]","[[any]]"); }
	@Test public void test_553() { checkNotSubtype("[null]","[[null]]"); }
	@Test public void test_554() { checkNotSubtype("[null]","[[int]]"); }
	@Test public void test_555() { checkIsSubtype("[null]","[{void f1}]"); }
	@Test public void test_556() { checkIsSubtype("[null]","[{void f2}]"); }
	@Test public void test_557() { checkNotSubtype("[null]","[{any f1}]"); }
	@Test public void test_558() { checkNotSubtype("[null]","[{any f2}]"); }
	@Test public void test_559() { checkNotSubtype("[null]","[{null f1}]"); }
	@Test public void test_560() { checkNotSubtype("[null]","[{null f2}]"); }
	@Test public void test_561() { checkNotSubtype("[null]","[{int f1}]"); }
	@Test public void test_562() { checkNotSubtype("[null]","[{int f2}]"); }
	@Test public void test_563() { checkIsSubtype("[null]","{void f1,void f2}"); }
	@Test public void test_564() { checkIsSubtype("[null]","{void f2,void f3}"); }
	@Test public void test_565() { checkIsSubtype("[null]","{void f1,any f2}"); }
	@Test public void test_566() { checkIsSubtype("[null]","{void f2,any f3}"); }
	@Test public void test_567() { checkIsSubtype("[null]","{void f1,null f2}"); }
	@Test public void test_568() { checkIsSubtype("[null]","{void f2,null f3}"); }
	@Test public void test_569() { checkIsSubtype("[null]","{void f1,int f2}"); }
	@Test public void test_570() { checkIsSubtype("[null]","{void f2,int f3}"); }
	@Test public void test_571() { checkIsSubtype("[null]","{any f1,void f2}"); }
	@Test public void test_572() { checkIsSubtype("[null]","{any f2,void f3}"); }
	@Test public void test_573() { checkNotSubtype("[null]","{any f1,any f2}"); }
	@Test public void test_574() { checkNotSubtype("[null]","{any f2,any f3}"); }
	@Test public void test_575() { checkNotSubtype("[null]","{any f1,null f2}"); }
	@Test public void test_576() { checkNotSubtype("[null]","{any f2,null f3}"); }
	@Test public void test_577() { checkNotSubtype("[null]","{any f1,int f2}"); }
	@Test public void test_578() { checkNotSubtype("[null]","{any f2,int f3}"); }
	@Test public void test_579() { checkIsSubtype("[null]","{null f1,void f2}"); }
	@Test public void test_580() { checkIsSubtype("[null]","{null f2,void f3}"); }
	@Test public void test_581() { checkNotSubtype("[null]","{null f1,any f2}"); }
	@Test public void test_582() { checkNotSubtype("[null]","{null f2,any f3}"); }
	@Test public void test_583() { checkNotSubtype("[null]","{null f1,null f2}"); }
	@Test public void test_584() { checkNotSubtype("[null]","{null f2,null f3}"); }
	@Test public void test_585() { checkNotSubtype("[null]","{null f1,int f2}"); }
	@Test public void test_586() { checkNotSubtype("[null]","{null f2,int f3}"); }
	@Test public void test_587() { checkIsSubtype("[null]","{int f1,void f2}"); }
	@Test public void test_588() { checkIsSubtype("[null]","{int f2,void f3}"); }
	@Test public void test_589() { checkNotSubtype("[null]","{int f1,any f2}"); }
	@Test public void test_590() { checkNotSubtype("[null]","{int f2,any f3}"); }
	@Test public void test_591() { checkNotSubtype("[null]","{int f1,null f2}"); }
	@Test public void test_592() { checkNotSubtype("[null]","{int f2,null f3}"); }
	@Test public void test_593() { checkNotSubtype("[null]","{int f1,int f2}"); }
	@Test public void test_594() { checkNotSubtype("[null]","{int f2,int f3}"); }
	@Test public void test_595() { checkNotSubtype("[null]","{[void] f1}"); }
	@Test public void test_596() { checkNotSubtype("[null]","{[void] f2}"); }
	@Test public void test_597() { checkIsSubtype("[null]","{[void] f1,void f2}"); }
	@Test public void test_598() { checkIsSubtype("[null]","{[void] f2,void f3}"); }
	@Test public void test_599() { checkNotSubtype("[null]","{[any] f1}"); }
	@Test public void test_600() { checkNotSubtype("[null]","{[any] f2}"); }
	@Test public void test_601() { checkNotSubtype("[null]","{[any] f1,any f2}"); }
	@Test public void test_602() { checkNotSubtype("[null]","{[any] f2,any f3}"); }
	@Test public void test_603() { checkNotSubtype("[null]","{[null] f1}"); }
	@Test public void test_604() { checkNotSubtype("[null]","{[null] f2}"); }
	@Test public void test_605() { checkNotSubtype("[null]","{[null] f1,null f2}"); }
	@Test public void test_606() { checkNotSubtype("[null]","{[null] f2,null f3}"); }
	@Test public void test_607() { checkNotSubtype("[null]","{[int] f1}"); }
	@Test public void test_608() { checkNotSubtype("[null]","{[int] f2}"); }
	@Test public void test_609() { checkNotSubtype("[null]","{[int] f1,int f2}"); }
	@Test public void test_610() { checkNotSubtype("[null]","{[int] f2,int f3}"); }
	@Test public void test_611() { checkIsSubtype("[null]","{{void f1} f1}"); }
	@Test public void test_612() { checkIsSubtype("[null]","{{void f2} f1}"); }
	@Test public void test_613() { checkIsSubtype("[null]","{{void f1} f2}"); }
	@Test public void test_614() { checkIsSubtype("[null]","{{void f2} f2}"); }
	@Test public void test_615() { checkIsSubtype("[null]","{{void f1} f1,void f2}"); }
	@Test public void test_616() { checkIsSubtype("[null]","{{void f2} f1,void f2}"); }
	@Test public void test_617() { checkIsSubtype("[null]","{{void f1} f2,void f3}"); }
	@Test public void test_618() { checkIsSubtype("[null]","{{void f2} f2,void f3}"); }
	@Test public void test_619() { checkNotSubtype("[null]","{{any f1} f1}"); }
	@Test public void test_620() { checkNotSubtype("[null]","{{any f2} f1}"); }
	@Test public void test_621() { checkNotSubtype("[null]","{{any f1} f2}"); }
	@Test public void test_622() { checkNotSubtype("[null]","{{any f2} f2}"); }
	@Test public void test_623() { checkNotSubtype("[null]","{{any f1} f1,any f2}"); }
	@Test public void test_624() { checkNotSubtype("[null]","{{any f2} f1,any f2}"); }
	@Test public void test_625() { checkNotSubtype("[null]","{{any f1} f2,any f3}"); }
	@Test public void test_626() { checkNotSubtype("[null]","{{any f2} f2,any f3}"); }
	@Test public void test_627() { checkNotSubtype("[null]","{{null f1} f1}"); }
	@Test public void test_628() { checkNotSubtype("[null]","{{null f2} f1}"); }
	@Test public void test_629() { checkNotSubtype("[null]","{{null f1} f2}"); }
	@Test public void test_630() { checkNotSubtype("[null]","{{null f2} f2}"); }
	@Test public void test_631() { checkNotSubtype("[null]","{{null f1} f1,null f2}"); }
	@Test public void test_632() { checkNotSubtype("[null]","{{null f2} f1,null f2}"); }
	@Test public void test_633() { checkNotSubtype("[null]","{{null f1} f2,null f3}"); }
	@Test public void test_634() { checkNotSubtype("[null]","{{null f2} f2,null f3}"); }
	@Test public void test_635() { checkNotSubtype("[null]","{{int f1} f1}"); }
	@Test public void test_636() { checkNotSubtype("[null]","{{int f2} f1}"); }
	@Test public void test_637() { checkNotSubtype("[null]","{{int f1} f2}"); }
	@Test public void test_638() { checkNotSubtype("[null]","{{int f2} f2}"); }
	@Test public void test_639() { checkNotSubtype("[null]","{{int f1} f1,int f2}"); }
	@Test public void test_640() { checkNotSubtype("[null]","{{int f2} f1,int f2}"); }
	@Test public void test_641() { checkNotSubtype("[null]","{{int f1} f2,int f3}"); }
	@Test public void test_642() { checkNotSubtype("[null]","{{int f2} f2,int f3}"); }
	@Test public void test_643() { checkNotSubtype("[int]","any"); }
	@Test public void test_644() { checkNotSubtype("[int]","null"); }
	@Test public void test_645() { checkNotSubtype("[int]","int"); }
	@Test public void test_646() { checkIsSubtype("[int]","[void]"); }
	@Test public void test_647() { checkNotSubtype("[int]","[any]"); }
	@Test public void test_648() { checkNotSubtype("[int]","[null]"); }
	@Test public void test_649() { checkIsSubtype("[int]","[int]"); }
	@Test public void test_650() { checkIsSubtype("[int]","{void f1}"); }
	@Test public void test_651() { checkIsSubtype("[int]","{void f2}"); }
	@Test public void test_652() { checkNotSubtype("[int]","{any f1}"); }
	@Test public void test_653() { checkNotSubtype("[int]","{any f2}"); }
	@Test public void test_654() { checkNotSubtype("[int]","{null f1}"); }
	@Test public void test_655() { checkNotSubtype("[int]","{null f2}"); }
	@Test public void test_656() { checkNotSubtype("[int]","{int f1}"); }
	@Test public void test_657() { checkNotSubtype("[int]","{int f2}"); }
	@Test public void test_658() { checkNotSubtype("[int]","[[void]]"); }
	@Test public void test_659() { checkNotSubtype("[int]","[[any]]"); }
	@Test public void test_660() { checkNotSubtype("[int]","[[null]]"); }
	@Test public void test_661() { checkNotSubtype("[int]","[[int]]"); }
	@Test public void test_662() { checkIsSubtype("[int]","[{void f1}]"); }
	@Test public void test_663() { checkIsSubtype("[int]","[{void f2}]"); }
	@Test public void test_664() { checkNotSubtype("[int]","[{any f1}]"); }
	@Test public void test_665() { checkNotSubtype("[int]","[{any f2}]"); }
	@Test public void test_666() { checkNotSubtype("[int]","[{null f1}]"); }
	@Test public void test_667() { checkNotSubtype("[int]","[{null f2}]"); }
	@Test public void test_668() { checkNotSubtype("[int]","[{int f1}]"); }
	@Test public void test_669() { checkNotSubtype("[int]","[{int f2}]"); }
	@Test public void test_670() { checkIsSubtype("[int]","{void f1,void f2}"); }
	@Test public void test_671() { checkIsSubtype("[int]","{void f2,void f3}"); }
	@Test public void test_672() { checkIsSubtype("[int]","{void f1,any f2}"); }
	@Test public void test_673() { checkIsSubtype("[int]","{void f2,any f3}"); }
	@Test public void test_674() { checkIsSubtype("[int]","{void f1,null f2}"); }
	@Test public void test_675() { checkIsSubtype("[int]","{void f2,null f3}"); }
	@Test public void test_676() { checkIsSubtype("[int]","{void f1,int f2}"); }
	@Test public void test_677() { checkIsSubtype("[int]","{void f2,int f3}"); }
	@Test public void test_678() { checkIsSubtype("[int]","{any f1,void f2}"); }
	@Test public void test_679() { checkIsSubtype("[int]","{any f2,void f3}"); }
	@Test public void test_680() { checkNotSubtype("[int]","{any f1,any f2}"); }
	@Test public void test_681() { checkNotSubtype("[int]","{any f2,any f3}"); }
	@Test public void test_682() { checkNotSubtype("[int]","{any f1,null f2}"); }
	@Test public void test_683() { checkNotSubtype("[int]","{any f2,null f3}"); }
	@Test public void test_684() { checkNotSubtype("[int]","{any f1,int f2}"); }
	@Test public void test_685() { checkNotSubtype("[int]","{any f2,int f3}"); }
	@Test public void test_686() { checkIsSubtype("[int]","{null f1,void f2}"); }
	@Test public void test_687() { checkIsSubtype("[int]","{null f2,void f3}"); }
	@Test public void test_688() { checkNotSubtype("[int]","{null f1,any f2}"); }
	@Test public void test_689() { checkNotSubtype("[int]","{null f2,any f3}"); }
	@Test public void test_690() { checkNotSubtype("[int]","{null f1,null f2}"); }
	@Test public void test_691() { checkNotSubtype("[int]","{null f2,null f3}"); }
	@Test public void test_692() { checkNotSubtype("[int]","{null f1,int f2}"); }
	@Test public void test_693() { checkNotSubtype("[int]","{null f2,int f3}"); }
	@Test public void test_694() { checkIsSubtype("[int]","{int f1,void f2}"); }
	@Test public void test_695() { checkIsSubtype("[int]","{int f2,void f3}"); }
	@Test public void test_696() { checkNotSubtype("[int]","{int f1,any f2}"); }
	@Test public void test_697() { checkNotSubtype("[int]","{int f2,any f3}"); }
	@Test public void test_698() { checkNotSubtype("[int]","{int f1,null f2}"); }
	@Test public void test_699() { checkNotSubtype("[int]","{int f2,null f3}"); }
	@Test public void test_700() { checkNotSubtype("[int]","{int f1,int f2}"); }
	@Test public void test_701() { checkNotSubtype("[int]","{int f2,int f3}"); }
	@Test public void test_702() { checkNotSubtype("[int]","{[void] f1}"); }
	@Test public void test_703() { checkNotSubtype("[int]","{[void] f2}"); }
	@Test public void test_704() { checkIsSubtype("[int]","{[void] f1,void f2}"); }
	@Test public void test_705() { checkIsSubtype("[int]","{[void] f2,void f3}"); }
	@Test public void test_706() { checkNotSubtype("[int]","{[any] f1}"); }
	@Test public void test_707() { checkNotSubtype("[int]","{[any] f2}"); }
	@Test public void test_708() { checkNotSubtype("[int]","{[any] f1,any f2}"); }
	@Test public void test_709() { checkNotSubtype("[int]","{[any] f2,any f3}"); }
	@Test public void test_710() { checkNotSubtype("[int]","{[null] f1}"); }
	@Test public void test_711() { checkNotSubtype("[int]","{[null] f2}"); }
	@Test public void test_712() { checkNotSubtype("[int]","{[null] f1,null f2}"); }
	@Test public void test_713() { checkNotSubtype("[int]","{[null] f2,null f3}"); }
	@Test public void test_714() { checkNotSubtype("[int]","{[int] f1}"); }
	@Test public void test_715() { checkNotSubtype("[int]","{[int] f2}"); }
	@Test public void test_716() { checkNotSubtype("[int]","{[int] f1,int f2}"); }
	@Test public void test_717() { checkNotSubtype("[int]","{[int] f2,int f3}"); }
	@Test public void test_718() { checkIsSubtype("[int]","{{void f1} f1}"); }
	@Test public void test_719() { checkIsSubtype("[int]","{{void f2} f1}"); }
	@Test public void test_720() { checkIsSubtype("[int]","{{void f1} f2}"); }
	@Test public void test_721() { checkIsSubtype("[int]","{{void f2} f2}"); }
	@Test public void test_722() { checkIsSubtype("[int]","{{void f1} f1,void f2}"); }
	@Test public void test_723() { checkIsSubtype("[int]","{{void f2} f1,void f2}"); }
	@Test public void test_724() { checkIsSubtype("[int]","{{void f1} f2,void f3}"); }
	@Test public void test_725() { checkIsSubtype("[int]","{{void f2} f2,void f3}"); }
	@Test public void test_726() { checkNotSubtype("[int]","{{any f1} f1}"); }
	@Test public void test_727() { checkNotSubtype("[int]","{{any f2} f1}"); }
	@Test public void test_728() { checkNotSubtype("[int]","{{any f1} f2}"); }
	@Test public void test_729() { checkNotSubtype("[int]","{{any f2} f2}"); }
	@Test public void test_730() { checkNotSubtype("[int]","{{any f1} f1,any f2}"); }
	@Test public void test_731() { checkNotSubtype("[int]","{{any f2} f1,any f2}"); }
	@Test public void test_732() { checkNotSubtype("[int]","{{any f1} f2,any f3}"); }
	@Test public void test_733() { checkNotSubtype("[int]","{{any f2} f2,any f3}"); }
	@Test public void test_734() { checkNotSubtype("[int]","{{null f1} f1}"); }
	@Test public void test_735() { checkNotSubtype("[int]","{{null f2} f1}"); }
	@Test public void test_736() { checkNotSubtype("[int]","{{null f1} f2}"); }
	@Test public void test_737() { checkNotSubtype("[int]","{{null f2} f2}"); }
	@Test public void test_738() { checkNotSubtype("[int]","{{null f1} f1,null f2}"); }
	@Test public void test_739() { checkNotSubtype("[int]","{{null f2} f1,null f2}"); }
	@Test public void test_740() { checkNotSubtype("[int]","{{null f1} f2,null f3}"); }
	@Test public void test_741() { checkNotSubtype("[int]","{{null f2} f2,null f3}"); }
	@Test public void test_742() { checkNotSubtype("[int]","{{int f1} f1}"); }
	@Test public void test_743() { checkNotSubtype("[int]","{{int f2} f1}"); }
	@Test public void test_744() { checkNotSubtype("[int]","{{int f1} f2}"); }
	@Test public void test_745() { checkNotSubtype("[int]","{{int f2} f2}"); }
	@Test public void test_746() { checkNotSubtype("[int]","{{int f1} f1,int f2}"); }
	@Test public void test_747() { checkNotSubtype("[int]","{{int f2} f1,int f2}"); }
	@Test public void test_748() { checkNotSubtype("[int]","{{int f1} f2,int f3}"); }
	@Test public void test_749() { checkNotSubtype("[int]","{{int f2} f2,int f3}"); }
	@Test public void test_750() { checkNotSubtype("{void f1}","any"); }
	@Test public void test_751() { checkNotSubtype("{void f1}","null"); }
	@Test public void test_752() { checkNotSubtype("{void f1}","int"); }
	@Test public void test_753() { checkNotSubtype("{void f1}","[void]"); }
	@Test public void test_754() { checkNotSubtype("{void f1}","[any]"); }
	@Test public void test_755() { checkNotSubtype("{void f1}","[null]"); }
	@Test public void test_756() { checkNotSubtype("{void f1}","[int]"); }
	@Test public void test_757() { checkIsSubtype("{void f1}","{void f1}"); }
	@Test public void test_758() { checkIsSubtype("{void f1}","{void f2}"); }
	@Test public void test_759() { checkNotSubtype("{void f1}","{any f1}"); }
	@Test public void test_760() { checkNotSubtype("{void f1}","{any f2}"); }
	@Test public void test_761() { checkNotSubtype("{void f1}","{null f1}"); }
	@Test public void test_762() { checkNotSubtype("{void f1}","{null f2}"); }
	@Test public void test_763() { checkNotSubtype("{void f1}","{int f1}"); }
	@Test public void test_764() { checkNotSubtype("{void f1}","{int f2}"); }
	@Test public void test_765() { checkNotSubtype("{void f1}","[[void]]"); }
	@Test public void test_766() { checkNotSubtype("{void f1}","[[any]]"); }
	@Test public void test_767() { checkNotSubtype("{void f1}","[[null]]"); }
	@Test public void test_768() { checkNotSubtype("{void f1}","[[int]]"); }
	@Test public void test_769() { checkNotSubtype("{void f1}","[{void f1}]"); }
	@Test public void test_770() { checkNotSubtype("{void f1}","[{void f2}]"); }
	@Test public void test_771() { checkNotSubtype("{void f1}","[{any f1}]"); }
	@Test public void test_772() { checkNotSubtype("{void f1}","[{any f2}]"); }
	@Test public void test_773() { checkNotSubtype("{void f1}","[{null f1}]"); }
	@Test public void test_774() { checkNotSubtype("{void f1}","[{null f2}]"); }
	@Test public void test_775() { checkNotSubtype("{void f1}","[{int f1}]"); }
	@Test public void test_776() { checkNotSubtype("{void f1}","[{int f2}]"); }
	@Test public void test_777() { checkIsSubtype("{void f1}","{void f1,void f2}"); }
	@Test public void test_778() { checkIsSubtype("{void f1}","{void f2,void f3}"); }
	@Test public void test_779() { checkIsSubtype("{void f1}","{void f1,any f2}"); }
	@Test public void test_780() { checkIsSubtype("{void f1}","{void f2,any f3}"); }
	@Test public void test_781() { checkIsSubtype("{void f1}","{void f1,null f2}"); }
	@Test public void test_782() { checkIsSubtype("{void f1}","{void f2,null f3}"); }
	@Test public void test_783() { checkIsSubtype("{void f1}","{void f1,int f2}"); }
	@Test public void test_784() { checkIsSubtype("{void f1}","{void f2,int f3}"); }
	@Test public void test_785() { checkIsSubtype("{void f1}","{any f1,void f2}"); }
	@Test public void test_786() { checkIsSubtype("{void f1}","{any f2,void f3}"); }
	@Test public void test_787() { checkNotSubtype("{void f1}","{any f1,any f2}"); }
	@Test public void test_788() { checkNotSubtype("{void f1}","{any f2,any f3}"); }
	@Test public void test_789() { checkNotSubtype("{void f1}","{any f1,null f2}"); }
	@Test public void test_790() { checkNotSubtype("{void f1}","{any f2,null f3}"); }
	@Test public void test_791() { checkNotSubtype("{void f1}","{any f1,int f2}"); }
	@Test public void test_792() { checkNotSubtype("{void f1}","{any f2,int f3}"); }
	@Test public void test_793() { checkIsSubtype("{void f1}","{null f1,void f2}"); }
	@Test public void test_794() { checkIsSubtype("{void f1}","{null f2,void f3}"); }
	@Test public void test_795() { checkNotSubtype("{void f1}","{null f1,any f2}"); }
	@Test public void test_796() { checkNotSubtype("{void f1}","{null f2,any f3}"); }
	@Test public void test_797() { checkNotSubtype("{void f1}","{null f1,null f2}"); }
	@Test public void test_798() { checkNotSubtype("{void f1}","{null f2,null f3}"); }
	@Test public void test_799() { checkNotSubtype("{void f1}","{null f1,int f2}"); }
	@Test public void test_800() { checkNotSubtype("{void f1}","{null f2,int f3}"); }
	@Test public void test_801() { checkIsSubtype("{void f1}","{int f1,void f2}"); }
	@Test public void test_802() { checkIsSubtype("{void f1}","{int f2,void f3}"); }
	@Test public void test_803() { checkNotSubtype("{void f1}","{int f1,any f2}"); }
	@Test public void test_804() { checkNotSubtype("{void f1}","{int f2,any f3}"); }
	@Test public void test_805() { checkNotSubtype("{void f1}","{int f1,null f2}"); }
	@Test public void test_806() { checkNotSubtype("{void f1}","{int f2,null f3}"); }
	@Test public void test_807() { checkNotSubtype("{void f1}","{int f1,int f2}"); }
	@Test public void test_808() { checkNotSubtype("{void f1}","{int f2,int f3}"); }
	@Test public void test_809() { checkNotSubtype("{void f1}","{[void] f1}"); }
	@Test public void test_810() { checkNotSubtype("{void f1}","{[void] f2}"); }
	@Test public void test_811() { checkIsSubtype("{void f1}","{[void] f1,void f2}"); }
	@Test public void test_812() { checkIsSubtype("{void f1}","{[void] f2,void f3}"); }
	@Test public void test_813() { checkNotSubtype("{void f1}","{[any] f1}"); }
	@Test public void test_814() { checkNotSubtype("{void f1}","{[any] f2}"); }
	@Test public void test_815() { checkNotSubtype("{void f1}","{[any] f1,any f2}"); }
	@Test public void test_816() { checkNotSubtype("{void f1}","{[any] f2,any f3}"); }
	@Test public void test_817() { checkNotSubtype("{void f1}","{[null] f1}"); }
	@Test public void test_818() { checkNotSubtype("{void f1}","{[null] f2}"); }
	@Test public void test_819() { checkNotSubtype("{void f1}","{[null] f1,null f2}"); }
	@Test public void test_820() { checkNotSubtype("{void f1}","{[null] f2,null f3}"); }
	@Test public void test_821() { checkNotSubtype("{void f1}","{[int] f1}"); }
	@Test public void test_822() { checkNotSubtype("{void f1}","{[int] f2}"); }
	@Test public void test_823() { checkNotSubtype("{void f1}","{[int] f1,int f2}"); }
	@Test public void test_824() { checkNotSubtype("{void f1}","{[int] f2,int f3}"); }
	@Test public void test_825() { checkIsSubtype("{void f1}","{{void f1} f1}"); }
	@Test public void test_826() { checkIsSubtype("{void f1}","{{void f2} f1}"); }
	@Test public void test_827() { checkIsSubtype("{void f1}","{{void f1} f2}"); }
	@Test public void test_828() { checkIsSubtype("{void f1}","{{void f2} f2}"); }
	@Test public void test_829() { checkIsSubtype("{void f1}","{{void f1} f1,void f2}"); }
	@Test public void test_830() { checkIsSubtype("{void f1}","{{void f2} f1,void f2}"); }
	@Test public void test_831() { checkIsSubtype("{void f1}","{{void f1} f2,void f3}"); }
	@Test public void test_832() { checkIsSubtype("{void f1}","{{void f2} f2,void f3}"); }
	@Test public void test_833() { checkNotSubtype("{void f1}","{{any f1} f1}"); }
	@Test public void test_834() { checkNotSubtype("{void f1}","{{any f2} f1}"); }
	@Test public void test_835() { checkNotSubtype("{void f1}","{{any f1} f2}"); }
	@Test public void test_836() { checkNotSubtype("{void f1}","{{any f2} f2}"); }
	@Test public void test_837() { checkNotSubtype("{void f1}","{{any f1} f1,any f2}"); }
	@Test public void test_838() { checkNotSubtype("{void f1}","{{any f2} f1,any f2}"); }
	@Test public void test_839() { checkNotSubtype("{void f1}","{{any f1} f2,any f3}"); }
	@Test public void test_840() { checkNotSubtype("{void f1}","{{any f2} f2,any f3}"); }
	@Test public void test_841() { checkNotSubtype("{void f1}","{{null f1} f1}"); }
	@Test public void test_842() { checkNotSubtype("{void f1}","{{null f2} f1}"); }
	@Test public void test_843() { checkNotSubtype("{void f1}","{{null f1} f2}"); }
	@Test public void test_844() { checkNotSubtype("{void f1}","{{null f2} f2}"); }
	@Test public void test_845() { checkNotSubtype("{void f1}","{{null f1} f1,null f2}"); }
	@Test public void test_846() { checkNotSubtype("{void f1}","{{null f2} f1,null f2}"); }
	@Test public void test_847() { checkNotSubtype("{void f1}","{{null f1} f2,null f3}"); }
	@Test public void test_848() { checkNotSubtype("{void f1}","{{null f2} f2,null f3}"); }
	@Test public void test_849() { checkNotSubtype("{void f1}","{{int f1} f1}"); }
	@Test public void test_850() { checkNotSubtype("{void f1}","{{int f2} f1}"); }
	@Test public void test_851() { checkNotSubtype("{void f1}","{{int f1} f2}"); }
	@Test public void test_852() { checkNotSubtype("{void f1}","{{int f2} f2}"); }
	@Test public void test_853() { checkNotSubtype("{void f1}","{{int f1} f1,int f2}"); }
	@Test public void test_854() { checkNotSubtype("{void f1}","{{int f2} f1,int f2}"); }
	@Test public void test_855() { checkNotSubtype("{void f1}","{{int f1} f2,int f3}"); }
	@Test public void test_856() { checkNotSubtype("{void f1}","{{int f2} f2,int f3}"); }
	@Test public void test_857() { checkNotSubtype("{void f2}","any"); }
	@Test public void test_858() { checkNotSubtype("{void f2}","null"); }
	@Test public void test_859() { checkNotSubtype("{void f2}","int"); }
	@Test public void test_860() { checkNotSubtype("{void f2}","[void]"); }
	@Test public void test_861() { checkNotSubtype("{void f2}","[any]"); }
	@Test public void test_862() { checkNotSubtype("{void f2}","[null]"); }
	@Test public void test_863() { checkNotSubtype("{void f2}","[int]"); }
	@Test public void test_864() { checkIsSubtype("{void f2}","{void f1}"); }
	@Test public void test_865() { checkIsSubtype("{void f2}","{void f2}"); }
	@Test public void test_866() { checkNotSubtype("{void f2}","{any f1}"); }
	@Test public void test_867() { checkNotSubtype("{void f2}","{any f2}"); }
	@Test public void test_868() { checkNotSubtype("{void f2}","{null f1}"); }
	@Test public void test_869() { checkNotSubtype("{void f2}","{null f2}"); }
	@Test public void test_870() { checkNotSubtype("{void f2}","{int f1}"); }
	@Test public void test_871() { checkNotSubtype("{void f2}","{int f2}"); }
	@Test public void test_872() { checkNotSubtype("{void f2}","[[void]]"); }
	@Test public void test_873() { checkNotSubtype("{void f2}","[[any]]"); }
	@Test public void test_874() { checkNotSubtype("{void f2}","[[null]]"); }
	@Test public void test_875() { checkNotSubtype("{void f2}","[[int]]"); }
	@Test public void test_876() { checkNotSubtype("{void f2}","[{void f1}]"); }
	@Test public void test_877() { checkNotSubtype("{void f2}","[{void f2}]"); }
	@Test public void test_878() { checkNotSubtype("{void f2}","[{any f1}]"); }
	@Test public void test_879() { checkNotSubtype("{void f2}","[{any f2}]"); }
	@Test public void test_880() { checkNotSubtype("{void f2}","[{null f1}]"); }
	@Test public void test_881() { checkNotSubtype("{void f2}","[{null f2}]"); }
	@Test public void test_882() { checkNotSubtype("{void f2}","[{int f1}]"); }
	@Test public void test_883() { checkNotSubtype("{void f2}","[{int f2}]"); }
	@Test public void test_884() { checkIsSubtype("{void f2}","{void f1,void f2}"); }
	@Test public void test_885() { checkIsSubtype("{void f2}","{void f2,void f3}"); }
	@Test public void test_886() { checkIsSubtype("{void f2}","{void f1,any f2}"); }
	@Test public void test_887() { checkIsSubtype("{void f2}","{void f2,any f3}"); }
	@Test public void test_888() { checkIsSubtype("{void f2}","{void f1,null f2}"); }
	@Test public void test_889() { checkIsSubtype("{void f2}","{void f2,null f3}"); }
	@Test public void test_890() { checkIsSubtype("{void f2}","{void f1,int f2}"); }
	@Test public void test_891() { checkIsSubtype("{void f2}","{void f2,int f3}"); }
	@Test public void test_892() { checkIsSubtype("{void f2}","{any f1,void f2}"); }
	@Test public void test_893() { checkIsSubtype("{void f2}","{any f2,void f3}"); }
	@Test public void test_894() { checkNotSubtype("{void f2}","{any f1,any f2}"); }
	@Test public void test_895() { checkNotSubtype("{void f2}","{any f2,any f3}"); }
	@Test public void test_896() { checkNotSubtype("{void f2}","{any f1,null f2}"); }
	@Test public void test_897() { checkNotSubtype("{void f2}","{any f2,null f3}"); }
	@Test public void test_898() { checkNotSubtype("{void f2}","{any f1,int f2}"); }
	@Test public void test_899() { checkNotSubtype("{void f2}","{any f2,int f3}"); }
	@Test public void test_900() { checkIsSubtype("{void f2}","{null f1,void f2}"); }
	@Test public void test_901() { checkIsSubtype("{void f2}","{null f2,void f3}"); }
	@Test public void test_902() { checkNotSubtype("{void f2}","{null f1,any f2}"); }
	@Test public void test_903() { checkNotSubtype("{void f2}","{null f2,any f3}"); }
	@Test public void test_904() { checkNotSubtype("{void f2}","{null f1,null f2}"); }
	@Test public void test_905() { checkNotSubtype("{void f2}","{null f2,null f3}"); }
	@Test public void test_906() { checkNotSubtype("{void f2}","{null f1,int f2}"); }
	@Test public void test_907() { checkNotSubtype("{void f2}","{null f2,int f3}"); }
	@Test public void test_908() { checkIsSubtype("{void f2}","{int f1,void f2}"); }
	@Test public void test_909() { checkIsSubtype("{void f2}","{int f2,void f3}"); }
	@Test public void test_910() { checkNotSubtype("{void f2}","{int f1,any f2}"); }
	@Test public void test_911() { checkNotSubtype("{void f2}","{int f2,any f3}"); }
	@Test public void test_912() { checkNotSubtype("{void f2}","{int f1,null f2}"); }
	@Test public void test_913() { checkNotSubtype("{void f2}","{int f2,null f3}"); }
	@Test public void test_914() { checkNotSubtype("{void f2}","{int f1,int f2}"); }
	@Test public void test_915() { checkNotSubtype("{void f2}","{int f2,int f3}"); }
	@Test public void test_916() { checkNotSubtype("{void f2}","{[void] f1}"); }
	@Test public void test_917() { checkNotSubtype("{void f2}","{[void] f2}"); }
	@Test public void test_918() { checkIsSubtype("{void f2}","{[void] f1,void f2}"); }
	@Test public void test_919() { checkIsSubtype("{void f2}","{[void] f2,void f3}"); }
	@Test public void test_920() { checkNotSubtype("{void f2}","{[any] f1}"); }
	@Test public void test_921() { checkNotSubtype("{void f2}","{[any] f2}"); }
	@Test public void test_922() { checkNotSubtype("{void f2}","{[any] f1,any f2}"); }
	@Test public void test_923() { checkNotSubtype("{void f2}","{[any] f2,any f3}"); }
	@Test public void test_924() { checkNotSubtype("{void f2}","{[null] f1}"); }
	@Test public void test_925() { checkNotSubtype("{void f2}","{[null] f2}"); }
	@Test public void test_926() { checkNotSubtype("{void f2}","{[null] f1,null f2}"); }
	@Test public void test_927() { checkNotSubtype("{void f2}","{[null] f2,null f3}"); }
	@Test public void test_928() { checkNotSubtype("{void f2}","{[int] f1}"); }
	@Test public void test_929() { checkNotSubtype("{void f2}","{[int] f2}"); }
	@Test public void test_930() { checkNotSubtype("{void f2}","{[int] f1,int f2}"); }
	@Test public void test_931() { checkNotSubtype("{void f2}","{[int] f2,int f3}"); }
	@Test public void test_932() { checkIsSubtype("{void f2}","{{void f1} f1}"); }
	@Test public void test_933() { checkIsSubtype("{void f2}","{{void f2} f1}"); }
	@Test public void test_934() { checkIsSubtype("{void f2}","{{void f1} f2}"); }
	@Test public void test_935() { checkIsSubtype("{void f2}","{{void f2} f2}"); }
	@Test public void test_936() { checkIsSubtype("{void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_937() { checkIsSubtype("{void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_938() { checkIsSubtype("{void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_939() { checkIsSubtype("{void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_940() { checkNotSubtype("{void f2}","{{any f1} f1}"); }
	@Test public void test_941() { checkNotSubtype("{void f2}","{{any f2} f1}"); }
	@Test public void test_942() { checkNotSubtype("{void f2}","{{any f1} f2}"); }
	@Test public void test_943() { checkNotSubtype("{void f2}","{{any f2} f2}"); }
	@Test public void test_944() { checkNotSubtype("{void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_945() { checkNotSubtype("{void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_946() { checkNotSubtype("{void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_947() { checkNotSubtype("{void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_948() { checkNotSubtype("{void f2}","{{null f1} f1}"); }
	@Test public void test_949() { checkNotSubtype("{void f2}","{{null f2} f1}"); }
	@Test public void test_950() { checkNotSubtype("{void f2}","{{null f1} f2}"); }
	@Test public void test_951() { checkNotSubtype("{void f2}","{{null f2} f2}"); }
	@Test public void test_952() { checkNotSubtype("{void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_953() { checkNotSubtype("{void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_954() { checkNotSubtype("{void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_955() { checkNotSubtype("{void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_956() { checkNotSubtype("{void f2}","{{int f1} f1}"); }
	@Test public void test_957() { checkNotSubtype("{void f2}","{{int f2} f1}"); }
	@Test public void test_958() { checkNotSubtype("{void f2}","{{int f1} f2}"); }
	@Test public void test_959() { checkNotSubtype("{void f2}","{{int f2} f2}"); }
	@Test public void test_960() { checkNotSubtype("{void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_961() { checkNotSubtype("{void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_962() { checkNotSubtype("{void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_963() { checkNotSubtype("{void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_964() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_965() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_966() { checkNotSubtype("{any f1}","int"); }
	@Test public void test_967() { checkNotSubtype("{any f1}","[void]"); }
	@Test public void test_968() { checkNotSubtype("{any f1}","[any]"); }
	@Test public void test_969() { checkNotSubtype("{any f1}","[null]"); }
	@Test public void test_970() { checkNotSubtype("{any f1}","[int]"); }
	@Test public void test_971() { checkIsSubtype("{any f1}","{void f1}"); }
	@Test public void test_972() { checkIsSubtype("{any f1}","{void f2}"); }
	@Test public void test_973() { checkIsSubtype("{any f1}","{any f1}"); }
	@Test public void test_974() { checkNotSubtype("{any f1}","{any f2}"); }
	@Test public void test_975() { checkIsSubtype("{any f1}","{null f1}"); }
	@Test public void test_976() { checkNotSubtype("{any f1}","{null f2}"); }
	@Test public void test_977() { checkIsSubtype("{any f1}","{int f1}"); }
	@Test public void test_978() { checkNotSubtype("{any f1}","{int f2}"); }
	@Test public void test_979() { checkNotSubtype("{any f1}","[[void]]"); }
	@Test public void test_980() { checkNotSubtype("{any f1}","[[any]]"); }
	@Test public void test_981() { checkNotSubtype("{any f1}","[[null]]"); }
	@Test public void test_982() { checkNotSubtype("{any f1}","[[int]]"); }
	@Test public void test_983() { checkNotSubtype("{any f1}","[{void f1}]"); }
	@Test public void test_984() { checkNotSubtype("{any f1}","[{void f2}]"); }
	@Test public void test_985() { checkNotSubtype("{any f1}","[{any f1}]"); }
	@Test public void test_986() { checkNotSubtype("{any f1}","[{any f2}]"); }
	@Test public void test_987() { checkNotSubtype("{any f1}","[{null f1}]"); }
	@Test public void test_988() { checkNotSubtype("{any f1}","[{null f2}]"); }
	@Test public void test_989() { checkNotSubtype("{any f1}","[{int f1}]"); }
	@Test public void test_990() { checkNotSubtype("{any f1}","[{int f2}]"); }
	@Test public void test_991() { checkIsSubtype("{any f1}","{void f1,void f2}"); }
	@Test public void test_992() { checkIsSubtype("{any f1}","{void f2,void f3}"); }
	@Test public void test_993() { checkIsSubtype("{any f1}","{void f1,any f2}"); }
	@Test public void test_994() { checkIsSubtype("{any f1}","{void f2,any f3}"); }
	@Test public void test_995() { checkIsSubtype("{any f1}","{void f1,null f2}"); }
	@Test public void test_996() { checkIsSubtype("{any f1}","{void f2,null f3}"); }
	@Test public void test_997() { checkIsSubtype("{any f1}","{void f1,int f2}"); }
	@Test public void test_998() { checkIsSubtype("{any f1}","{void f2,int f3}"); }
	@Test public void test_999() { checkIsSubtype("{any f1}","{any f1,void f2}"); }
	@Test public void test_1000() { checkIsSubtype("{any f1}","{any f2,void f3}"); }
	@Test public void test_1001() { checkNotSubtype("{any f1}","{any f1,any f2}"); }
	@Test public void test_1002() { checkNotSubtype("{any f1}","{any f2,any f3}"); }
	@Test public void test_1003() { checkNotSubtype("{any f1}","{any f1,null f2}"); }
	@Test public void test_1004() { checkNotSubtype("{any f1}","{any f2,null f3}"); }
	@Test public void test_1005() { checkNotSubtype("{any f1}","{any f1,int f2}"); }
	@Test public void test_1006() { checkNotSubtype("{any f1}","{any f2,int f3}"); }
	@Test public void test_1007() { checkIsSubtype("{any f1}","{null f1,void f2}"); }
	@Test public void test_1008() { checkIsSubtype("{any f1}","{null f2,void f3}"); }
	@Test public void test_1009() { checkNotSubtype("{any f1}","{null f1,any f2}"); }
	@Test public void test_1010() { checkNotSubtype("{any f1}","{null f2,any f3}"); }
	@Test public void test_1011() { checkNotSubtype("{any f1}","{null f1,null f2}"); }
	@Test public void test_1012() { checkNotSubtype("{any f1}","{null f2,null f3}"); }
	@Test public void test_1013() { checkNotSubtype("{any f1}","{null f1,int f2}"); }
	@Test public void test_1014() { checkNotSubtype("{any f1}","{null f2,int f3}"); }
	@Test public void test_1015() { checkIsSubtype("{any f1}","{int f1,void f2}"); }
	@Test public void test_1016() { checkIsSubtype("{any f1}","{int f2,void f3}"); }
	@Test public void test_1017() { checkNotSubtype("{any f1}","{int f1,any f2}"); }
	@Test public void test_1018() { checkNotSubtype("{any f1}","{int f2,any f3}"); }
	@Test public void test_1019() { checkNotSubtype("{any f1}","{int f1,null f2}"); }
	@Test public void test_1020() { checkNotSubtype("{any f1}","{int f2,null f3}"); }
	@Test public void test_1021() { checkNotSubtype("{any f1}","{int f1,int f2}"); }
	@Test public void test_1022() { checkNotSubtype("{any f1}","{int f2,int f3}"); }
	@Test public void test_1023() { checkIsSubtype("{any f1}","{[void] f1}"); }
	@Test public void test_1024() { checkNotSubtype("{any f1}","{[void] f2}"); }
	@Test public void test_1025() { checkIsSubtype("{any f1}","{[void] f1,void f2}"); }
	@Test public void test_1026() { checkIsSubtype("{any f1}","{[void] f2,void f3}"); }
	@Test public void test_1027() { checkIsSubtype("{any f1}","{[any] f1}"); }
	@Test public void test_1028() { checkNotSubtype("{any f1}","{[any] f2}"); }
	@Test public void test_1029() { checkNotSubtype("{any f1}","{[any] f1,any f2}"); }
	@Test public void test_1030() { checkNotSubtype("{any f1}","{[any] f2,any f3}"); }
	@Test public void test_1031() { checkIsSubtype("{any f1}","{[null] f1}"); }
	@Test public void test_1032() { checkNotSubtype("{any f1}","{[null] f2}"); }
	@Test public void test_1033() { checkNotSubtype("{any f1}","{[null] f1,null f2}"); }
	@Test public void test_1034() { checkNotSubtype("{any f1}","{[null] f2,null f3}"); }
	@Test public void test_1035() { checkIsSubtype("{any f1}","{[int] f1}"); }
	@Test public void test_1036() { checkNotSubtype("{any f1}","{[int] f2}"); }
	@Test public void test_1037() { checkNotSubtype("{any f1}","{[int] f1,int f2}"); }
	@Test public void test_1038() { checkNotSubtype("{any f1}","{[int] f2,int f3}"); }
	@Test public void test_1039() { checkIsSubtype("{any f1}","{{void f1} f1}"); }
	@Test public void test_1040() { checkIsSubtype("{any f1}","{{void f2} f1}"); }
	@Test public void test_1041() { checkIsSubtype("{any f1}","{{void f1} f2}"); }
	@Test public void test_1042() { checkIsSubtype("{any f1}","{{void f2} f2}"); }
	@Test public void test_1043() { checkIsSubtype("{any f1}","{{void f1} f1,void f2}"); }
	@Test public void test_1044() { checkIsSubtype("{any f1}","{{void f2} f1,void f2}"); }
	@Test public void test_1045() { checkIsSubtype("{any f1}","{{void f1} f2,void f3}"); }
	@Test public void test_1046() { checkIsSubtype("{any f1}","{{void f2} f2,void f3}"); }
	@Test public void test_1047() { checkIsSubtype("{any f1}","{{any f1} f1}"); }
	@Test public void test_1048() { checkIsSubtype("{any f1}","{{any f2} f1}"); }
	@Test public void test_1049() { checkNotSubtype("{any f1}","{{any f1} f2}"); }
	@Test public void test_1050() { checkNotSubtype("{any f1}","{{any f2} f2}"); }
	@Test public void test_1051() { checkNotSubtype("{any f1}","{{any f1} f1,any f2}"); }
	@Test public void test_1052() { checkNotSubtype("{any f1}","{{any f2} f1,any f2}"); }
	@Test public void test_1053() { checkNotSubtype("{any f1}","{{any f1} f2,any f3}"); }
	@Test public void test_1054() { checkNotSubtype("{any f1}","{{any f2} f2,any f3}"); }
	@Test public void test_1055() { checkIsSubtype("{any f1}","{{null f1} f1}"); }
	@Test public void test_1056() { checkIsSubtype("{any f1}","{{null f2} f1}"); }
	@Test public void test_1057() { checkNotSubtype("{any f1}","{{null f1} f2}"); }
	@Test public void test_1058() { checkNotSubtype("{any f1}","{{null f2} f2}"); }
	@Test public void test_1059() { checkNotSubtype("{any f1}","{{null f1} f1,null f2}"); }
	@Test public void test_1060() { checkNotSubtype("{any f1}","{{null f2} f1,null f2}"); }
	@Test public void test_1061() { checkNotSubtype("{any f1}","{{null f1} f2,null f3}"); }
	@Test public void test_1062() { checkNotSubtype("{any f1}","{{null f2} f2,null f3}"); }
	@Test public void test_1063() { checkIsSubtype("{any f1}","{{int f1} f1}"); }
	@Test public void test_1064() { checkIsSubtype("{any f1}","{{int f2} f1}"); }
	@Test public void test_1065() { checkNotSubtype("{any f1}","{{int f1} f2}"); }
	@Test public void test_1066() { checkNotSubtype("{any f1}","{{int f2} f2}"); }
	@Test public void test_1067() { checkNotSubtype("{any f1}","{{int f1} f1,int f2}"); }
	@Test public void test_1068() { checkNotSubtype("{any f1}","{{int f2} f1,int f2}"); }
	@Test public void test_1069() { checkNotSubtype("{any f1}","{{int f1} f2,int f3}"); }
	@Test public void test_1070() { checkNotSubtype("{any f1}","{{int f2} f2,int f3}"); }
	@Test public void test_1071() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_1072() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_1073() { checkNotSubtype("{any f2}","int"); }
	@Test public void test_1074() { checkNotSubtype("{any f2}","[void]"); }
	@Test public void test_1075() { checkNotSubtype("{any f2}","[any]"); }
	@Test public void test_1076() { checkNotSubtype("{any f2}","[null]"); }
	@Test public void test_1077() { checkNotSubtype("{any f2}","[int]"); }
	@Test public void test_1078() { checkIsSubtype("{any f2}","{void f1}"); }
	@Test public void test_1079() { checkIsSubtype("{any f2}","{void f2}"); }
	@Test public void test_1080() { checkNotSubtype("{any f2}","{any f1}"); }
	@Test public void test_1081() { checkIsSubtype("{any f2}","{any f2}"); }
	@Test public void test_1082() { checkNotSubtype("{any f2}","{null f1}"); }
	@Test public void test_1083() { checkIsSubtype("{any f2}","{null f2}"); }
	@Test public void test_1084() { checkNotSubtype("{any f2}","{int f1}"); }
	@Test public void test_1085() { checkIsSubtype("{any f2}","{int f2}"); }
	@Test public void test_1086() { checkNotSubtype("{any f2}","[[void]]"); }
	@Test public void test_1087() { checkNotSubtype("{any f2}","[[any]]"); }
	@Test public void test_1088() { checkNotSubtype("{any f2}","[[null]]"); }
	@Test public void test_1089() { checkNotSubtype("{any f2}","[[int]]"); }
	@Test public void test_1090() { checkNotSubtype("{any f2}","[{void f1}]"); }
	@Test public void test_1091() { checkNotSubtype("{any f2}","[{void f2}]"); }
	@Test public void test_1092() { checkNotSubtype("{any f2}","[{any f1}]"); }
	@Test public void test_1093() { checkNotSubtype("{any f2}","[{any f2}]"); }
	@Test public void test_1094() { checkNotSubtype("{any f2}","[{null f1}]"); }
	@Test public void test_1095() { checkNotSubtype("{any f2}","[{null f2}]"); }
	@Test public void test_1096() { checkNotSubtype("{any f2}","[{int f1}]"); }
	@Test public void test_1097() { checkNotSubtype("{any f2}","[{int f2}]"); }
	@Test public void test_1098() { checkIsSubtype("{any f2}","{void f1,void f2}"); }
	@Test public void test_1099() { checkIsSubtype("{any f2}","{void f2,void f3}"); }
	@Test public void test_1100() { checkIsSubtype("{any f2}","{void f1,any f2}"); }
	@Test public void test_1101() { checkIsSubtype("{any f2}","{void f2,any f3}"); }
	@Test public void test_1102() { checkIsSubtype("{any f2}","{void f1,null f2}"); }
	@Test public void test_1103() { checkIsSubtype("{any f2}","{void f2,null f3}"); }
	@Test public void test_1104() { checkIsSubtype("{any f2}","{void f1,int f2}"); }
	@Test public void test_1105() { checkIsSubtype("{any f2}","{void f2,int f3}"); }
	@Test public void test_1106() { checkIsSubtype("{any f2}","{any f1,void f2}"); }
	@Test public void test_1107() { checkIsSubtype("{any f2}","{any f2,void f3}"); }
	@Test public void test_1108() { checkNotSubtype("{any f2}","{any f1,any f2}"); }
	@Test public void test_1109() { checkNotSubtype("{any f2}","{any f2,any f3}"); }
	@Test public void test_1110() { checkNotSubtype("{any f2}","{any f1,null f2}"); }
	@Test public void test_1111() { checkNotSubtype("{any f2}","{any f2,null f3}"); }
	@Test public void test_1112() { checkNotSubtype("{any f2}","{any f1,int f2}"); }
	@Test public void test_1113() { checkNotSubtype("{any f2}","{any f2,int f3}"); }
	@Test public void test_1114() { checkIsSubtype("{any f2}","{null f1,void f2}"); }
	@Test public void test_1115() { checkIsSubtype("{any f2}","{null f2,void f3}"); }
	@Test public void test_1116() { checkNotSubtype("{any f2}","{null f1,any f2}"); }
	@Test public void test_1117() { checkNotSubtype("{any f2}","{null f2,any f3}"); }
	@Test public void test_1118() { checkNotSubtype("{any f2}","{null f1,null f2}"); }
	@Test public void test_1119() { checkNotSubtype("{any f2}","{null f2,null f3}"); }
	@Test public void test_1120() { checkNotSubtype("{any f2}","{null f1,int f2}"); }
	@Test public void test_1121() { checkNotSubtype("{any f2}","{null f2,int f3}"); }
	@Test public void test_1122() { checkIsSubtype("{any f2}","{int f1,void f2}"); }
	@Test public void test_1123() { checkIsSubtype("{any f2}","{int f2,void f3}"); }
	@Test public void test_1124() { checkNotSubtype("{any f2}","{int f1,any f2}"); }
	@Test public void test_1125() { checkNotSubtype("{any f2}","{int f2,any f3}"); }
	@Test public void test_1126() { checkNotSubtype("{any f2}","{int f1,null f2}"); }
	@Test public void test_1127() { checkNotSubtype("{any f2}","{int f2,null f3}"); }
	@Test public void test_1128() { checkNotSubtype("{any f2}","{int f1,int f2}"); }
	@Test public void test_1129() { checkNotSubtype("{any f2}","{int f2,int f3}"); }
	@Test public void test_1130() { checkNotSubtype("{any f2}","{[void] f1}"); }
	@Test public void test_1131() { checkIsSubtype("{any f2}","{[void] f2}"); }
	@Test public void test_1132() { checkIsSubtype("{any f2}","{[void] f1,void f2}"); }
	@Test public void test_1133() { checkIsSubtype("{any f2}","{[void] f2,void f3}"); }
	@Test public void test_1134() { checkNotSubtype("{any f2}","{[any] f1}"); }
	@Test public void test_1135() { checkIsSubtype("{any f2}","{[any] f2}"); }
	@Test public void test_1136() { checkNotSubtype("{any f2}","{[any] f1,any f2}"); }
	@Test public void test_1137() { checkNotSubtype("{any f2}","{[any] f2,any f3}"); }
	@Test public void test_1138() { checkNotSubtype("{any f2}","{[null] f1}"); }
	@Test public void test_1139() { checkIsSubtype("{any f2}","{[null] f2}"); }
	@Test public void test_1140() { checkNotSubtype("{any f2}","{[null] f1,null f2}"); }
	@Test public void test_1141() { checkNotSubtype("{any f2}","{[null] f2,null f3}"); }
	@Test public void test_1142() { checkNotSubtype("{any f2}","{[int] f1}"); }
	@Test public void test_1143() { checkIsSubtype("{any f2}","{[int] f2}"); }
	@Test public void test_1144() { checkNotSubtype("{any f2}","{[int] f1,int f2}"); }
	@Test public void test_1145() { checkNotSubtype("{any f2}","{[int] f2,int f3}"); }
	@Test public void test_1146() { checkIsSubtype("{any f2}","{{void f1} f1}"); }
	@Test public void test_1147() { checkIsSubtype("{any f2}","{{void f2} f1}"); }
	@Test public void test_1148() { checkIsSubtype("{any f2}","{{void f1} f2}"); }
	@Test public void test_1149() { checkIsSubtype("{any f2}","{{void f2} f2}"); }
	@Test public void test_1150() { checkIsSubtype("{any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1151() { checkIsSubtype("{any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1152() { checkIsSubtype("{any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1153() { checkIsSubtype("{any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1154() { checkNotSubtype("{any f2}","{{any f1} f1}"); }
	@Test public void test_1155() { checkNotSubtype("{any f2}","{{any f2} f1}"); }
	@Test public void test_1156() { checkIsSubtype("{any f2}","{{any f1} f2}"); }
	@Test public void test_1157() { checkIsSubtype("{any f2}","{{any f2} f2}"); }
	@Test public void test_1158() { checkNotSubtype("{any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1159() { checkNotSubtype("{any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1160() { checkNotSubtype("{any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1161() { checkNotSubtype("{any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1162() { checkNotSubtype("{any f2}","{{null f1} f1}"); }
	@Test public void test_1163() { checkNotSubtype("{any f2}","{{null f2} f1}"); }
	@Test public void test_1164() { checkIsSubtype("{any f2}","{{null f1} f2}"); }
	@Test public void test_1165() { checkIsSubtype("{any f2}","{{null f2} f2}"); }
	@Test public void test_1166() { checkNotSubtype("{any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1167() { checkNotSubtype("{any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1168() { checkNotSubtype("{any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1169() { checkNotSubtype("{any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1170() { checkNotSubtype("{any f2}","{{int f1} f1}"); }
	@Test public void test_1171() { checkNotSubtype("{any f2}","{{int f2} f1}"); }
	@Test public void test_1172() { checkIsSubtype("{any f2}","{{int f1} f2}"); }
	@Test public void test_1173() { checkIsSubtype("{any f2}","{{int f2} f2}"); }
	@Test public void test_1174() { checkNotSubtype("{any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1175() { checkNotSubtype("{any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1176() { checkNotSubtype("{any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1177() { checkNotSubtype("{any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1178() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_1179() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_1180() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_1181() { checkNotSubtype("{null f1}","[void]"); }
	@Test public void test_1182() { checkNotSubtype("{null f1}","[any]"); }
	@Test public void test_1183() { checkNotSubtype("{null f1}","[null]"); }
	@Test public void test_1184() { checkNotSubtype("{null f1}","[int]"); }
	@Test public void test_1185() { checkIsSubtype("{null f1}","{void f1}"); }
	@Test public void test_1186() { checkIsSubtype("{null f1}","{void f2}"); }
	@Test public void test_1187() { checkNotSubtype("{null f1}","{any f1}"); }
	@Test public void test_1188() { checkNotSubtype("{null f1}","{any f2}"); }
	@Test public void test_1189() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_1190() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_1191() { checkNotSubtype("{null f1}","{int f1}"); }
	@Test public void test_1192() { checkNotSubtype("{null f1}","{int f2}"); }
	@Test public void test_1193() { checkNotSubtype("{null f1}","[[void]]"); }
	@Test public void test_1194() { checkNotSubtype("{null f1}","[[any]]"); }
	@Test public void test_1195() { checkNotSubtype("{null f1}","[[null]]"); }
	@Test public void test_1196() { checkNotSubtype("{null f1}","[[int]]"); }
	@Test public void test_1197() { checkNotSubtype("{null f1}","[{void f1}]"); }
	@Test public void test_1198() { checkNotSubtype("{null f1}","[{void f2}]"); }
	@Test public void test_1199() { checkNotSubtype("{null f1}","[{any f1}]"); }
	@Test public void test_1200() { checkNotSubtype("{null f1}","[{any f2}]"); }
	@Test public void test_1201() { checkNotSubtype("{null f1}","[{null f1}]"); }
	@Test public void test_1202() { checkNotSubtype("{null f1}","[{null f2}]"); }
	@Test public void test_1203() { checkNotSubtype("{null f1}","[{int f1}]"); }
	@Test public void test_1204() { checkNotSubtype("{null f1}","[{int f2}]"); }
	@Test public void test_1205() { checkIsSubtype("{null f1}","{void f1,void f2}"); }
	@Test public void test_1206() { checkIsSubtype("{null f1}","{void f2,void f3}"); }
	@Test public void test_1207() { checkIsSubtype("{null f1}","{void f1,any f2}"); }
	@Test public void test_1208() { checkIsSubtype("{null f1}","{void f2,any f3}"); }
	@Test public void test_1209() { checkIsSubtype("{null f1}","{void f1,null f2}"); }
	@Test public void test_1210() { checkIsSubtype("{null f1}","{void f2,null f3}"); }
	@Test public void test_1211() { checkIsSubtype("{null f1}","{void f1,int f2}"); }
	@Test public void test_1212() { checkIsSubtype("{null f1}","{void f2,int f3}"); }
	@Test public void test_1213() { checkIsSubtype("{null f1}","{any f1,void f2}"); }
	@Test public void test_1214() { checkIsSubtype("{null f1}","{any f2,void f3}"); }
	@Test public void test_1215() { checkNotSubtype("{null f1}","{any f1,any f2}"); }
	@Test public void test_1216() { checkNotSubtype("{null f1}","{any f2,any f3}"); }
	@Test public void test_1217() { checkNotSubtype("{null f1}","{any f1,null f2}"); }
	@Test public void test_1218() { checkNotSubtype("{null f1}","{any f2,null f3}"); }
	@Test public void test_1219() { checkNotSubtype("{null f1}","{any f1,int f2}"); }
	@Test public void test_1220() { checkNotSubtype("{null f1}","{any f2,int f3}"); }
	@Test public void test_1221() { checkIsSubtype("{null f1}","{null f1,void f2}"); }
	@Test public void test_1222() { checkIsSubtype("{null f1}","{null f2,void f3}"); }
	@Test public void test_1223() { checkNotSubtype("{null f1}","{null f1,any f2}"); }
	@Test public void test_1224() { checkNotSubtype("{null f1}","{null f2,any f3}"); }
	@Test public void test_1225() { checkNotSubtype("{null f1}","{null f1,null f2}"); }
	@Test public void test_1226() { checkNotSubtype("{null f1}","{null f2,null f3}"); }
	@Test public void test_1227() { checkNotSubtype("{null f1}","{null f1,int f2}"); }
	@Test public void test_1228() { checkNotSubtype("{null f1}","{null f2,int f3}"); }
	@Test public void test_1229() { checkIsSubtype("{null f1}","{int f1,void f2}"); }
	@Test public void test_1230() { checkIsSubtype("{null f1}","{int f2,void f3}"); }
	@Test public void test_1231() { checkNotSubtype("{null f1}","{int f1,any f2}"); }
	@Test public void test_1232() { checkNotSubtype("{null f1}","{int f2,any f3}"); }
	@Test public void test_1233() { checkNotSubtype("{null f1}","{int f1,null f2}"); }
	@Test public void test_1234() { checkNotSubtype("{null f1}","{int f2,null f3}"); }
	@Test public void test_1235() { checkNotSubtype("{null f1}","{int f1,int f2}"); }
	@Test public void test_1236() { checkNotSubtype("{null f1}","{int f2,int f3}"); }
	@Test public void test_1237() { checkNotSubtype("{null f1}","{[void] f1}"); }
	@Test public void test_1238() { checkNotSubtype("{null f1}","{[void] f2}"); }
	@Test public void test_1239() { checkIsSubtype("{null f1}","{[void] f1,void f2}"); }
	@Test public void test_1240() { checkIsSubtype("{null f1}","{[void] f2,void f3}"); }
	@Test public void test_1241() { checkNotSubtype("{null f1}","{[any] f1}"); }
	@Test public void test_1242() { checkNotSubtype("{null f1}","{[any] f2}"); }
	@Test public void test_1243() { checkNotSubtype("{null f1}","{[any] f1,any f2}"); }
	@Test public void test_1244() { checkNotSubtype("{null f1}","{[any] f2,any f3}"); }
	@Test public void test_1245() { checkNotSubtype("{null f1}","{[null] f1}"); }
	@Test public void test_1246() { checkNotSubtype("{null f1}","{[null] f2}"); }
	@Test public void test_1247() { checkNotSubtype("{null f1}","{[null] f1,null f2}"); }
	@Test public void test_1248() { checkNotSubtype("{null f1}","{[null] f2,null f3}"); }
	@Test public void test_1249() { checkNotSubtype("{null f1}","{[int] f1}"); }
	@Test public void test_1250() { checkNotSubtype("{null f1}","{[int] f2}"); }
	@Test public void test_1251() { checkNotSubtype("{null f1}","{[int] f1,int f2}"); }
	@Test public void test_1252() { checkNotSubtype("{null f1}","{[int] f2,int f3}"); }
	@Test public void test_1253() { checkIsSubtype("{null f1}","{{void f1} f1}"); }
	@Test public void test_1254() { checkIsSubtype("{null f1}","{{void f2} f1}"); }
	@Test public void test_1255() { checkIsSubtype("{null f1}","{{void f1} f2}"); }
	@Test public void test_1256() { checkIsSubtype("{null f1}","{{void f2} f2}"); }
	@Test public void test_1257() { checkIsSubtype("{null f1}","{{void f1} f1,void f2}"); }
	@Test public void test_1258() { checkIsSubtype("{null f1}","{{void f2} f1,void f2}"); }
	@Test public void test_1259() { checkIsSubtype("{null f1}","{{void f1} f2,void f3}"); }
	@Test public void test_1260() { checkIsSubtype("{null f1}","{{void f2} f2,void f3}"); }
	@Test public void test_1261() { checkNotSubtype("{null f1}","{{any f1} f1}"); }
	@Test public void test_1262() { checkNotSubtype("{null f1}","{{any f2} f1}"); }
	@Test public void test_1263() { checkNotSubtype("{null f1}","{{any f1} f2}"); }
	@Test public void test_1264() { checkNotSubtype("{null f1}","{{any f2} f2}"); }
	@Test public void test_1265() { checkNotSubtype("{null f1}","{{any f1} f1,any f2}"); }
	@Test public void test_1266() { checkNotSubtype("{null f1}","{{any f2} f1,any f2}"); }
	@Test public void test_1267() { checkNotSubtype("{null f1}","{{any f1} f2,any f3}"); }
	@Test public void test_1268() { checkNotSubtype("{null f1}","{{any f2} f2,any f3}"); }
	@Test public void test_1269() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test public void test_1270() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test public void test_1271() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test public void test_1272() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test public void test_1273() { checkNotSubtype("{null f1}","{{null f1} f1,null f2}"); }
	@Test public void test_1274() { checkNotSubtype("{null f1}","{{null f2} f1,null f2}"); }
	@Test public void test_1275() { checkNotSubtype("{null f1}","{{null f1} f2,null f3}"); }
	@Test public void test_1276() { checkNotSubtype("{null f1}","{{null f2} f2,null f3}"); }
	@Test public void test_1277() { checkNotSubtype("{null f1}","{{int f1} f1}"); }
	@Test public void test_1278() { checkNotSubtype("{null f1}","{{int f2} f1}"); }
	@Test public void test_1279() { checkNotSubtype("{null f1}","{{int f1} f2}"); }
	@Test public void test_1280() { checkNotSubtype("{null f1}","{{int f2} f2}"); }
	@Test public void test_1281() { checkNotSubtype("{null f1}","{{int f1} f1,int f2}"); }
	@Test public void test_1282() { checkNotSubtype("{null f1}","{{int f2} f1,int f2}"); }
	@Test public void test_1283() { checkNotSubtype("{null f1}","{{int f1} f2,int f3}"); }
	@Test public void test_1284() { checkNotSubtype("{null f1}","{{int f2} f2,int f3}"); }
	@Test public void test_1285() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_1286() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_1287() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_1288() { checkNotSubtype("{null f2}","[void]"); }
	@Test public void test_1289() { checkNotSubtype("{null f2}","[any]"); }
	@Test public void test_1290() { checkNotSubtype("{null f2}","[null]"); }
	@Test public void test_1291() { checkNotSubtype("{null f2}","[int]"); }
	@Test public void test_1292() { checkIsSubtype("{null f2}","{void f1}"); }
	@Test public void test_1293() { checkIsSubtype("{null f2}","{void f2}"); }
	@Test public void test_1294() { checkNotSubtype("{null f2}","{any f1}"); }
	@Test public void test_1295() { checkNotSubtype("{null f2}","{any f2}"); }
	@Test public void test_1296() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_1297() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_1298() { checkNotSubtype("{null f2}","{int f1}"); }
	@Test public void test_1299() { checkNotSubtype("{null f2}","{int f2}"); }
	@Test public void test_1300() { checkNotSubtype("{null f2}","[[void]]"); }
	@Test public void test_1301() { checkNotSubtype("{null f2}","[[any]]"); }
	@Test public void test_1302() { checkNotSubtype("{null f2}","[[null]]"); }
	@Test public void test_1303() { checkNotSubtype("{null f2}","[[int]]"); }
	@Test public void test_1304() { checkNotSubtype("{null f2}","[{void f1}]"); }
	@Test public void test_1305() { checkNotSubtype("{null f2}","[{void f2}]"); }
	@Test public void test_1306() { checkNotSubtype("{null f2}","[{any f1}]"); }
	@Test public void test_1307() { checkNotSubtype("{null f2}","[{any f2}]"); }
	@Test public void test_1308() { checkNotSubtype("{null f2}","[{null f1}]"); }
	@Test public void test_1309() { checkNotSubtype("{null f2}","[{null f2}]"); }
	@Test public void test_1310() { checkNotSubtype("{null f2}","[{int f1}]"); }
	@Test public void test_1311() { checkNotSubtype("{null f2}","[{int f2}]"); }
	@Test public void test_1312() { checkIsSubtype("{null f2}","{void f1,void f2}"); }
	@Test public void test_1313() { checkIsSubtype("{null f2}","{void f2,void f3}"); }
	@Test public void test_1314() { checkIsSubtype("{null f2}","{void f1,any f2}"); }
	@Test public void test_1315() { checkIsSubtype("{null f2}","{void f2,any f3}"); }
	@Test public void test_1316() { checkIsSubtype("{null f2}","{void f1,null f2}"); }
	@Test public void test_1317() { checkIsSubtype("{null f2}","{void f2,null f3}"); }
	@Test public void test_1318() { checkIsSubtype("{null f2}","{void f1,int f2}"); }
	@Test public void test_1319() { checkIsSubtype("{null f2}","{void f2,int f3}"); }
	@Test public void test_1320() { checkIsSubtype("{null f2}","{any f1,void f2}"); }
	@Test public void test_1321() { checkIsSubtype("{null f2}","{any f2,void f3}"); }
	@Test public void test_1322() { checkNotSubtype("{null f2}","{any f1,any f2}"); }
	@Test public void test_1323() { checkNotSubtype("{null f2}","{any f2,any f3}"); }
	@Test public void test_1324() { checkNotSubtype("{null f2}","{any f1,null f2}"); }
	@Test public void test_1325() { checkNotSubtype("{null f2}","{any f2,null f3}"); }
	@Test public void test_1326() { checkNotSubtype("{null f2}","{any f1,int f2}"); }
	@Test public void test_1327() { checkNotSubtype("{null f2}","{any f2,int f3}"); }
	@Test public void test_1328() { checkIsSubtype("{null f2}","{null f1,void f2}"); }
	@Test public void test_1329() { checkIsSubtype("{null f2}","{null f2,void f3}"); }
	@Test public void test_1330() { checkNotSubtype("{null f2}","{null f1,any f2}"); }
	@Test public void test_1331() { checkNotSubtype("{null f2}","{null f2,any f3}"); }
	@Test public void test_1332() { checkNotSubtype("{null f2}","{null f1,null f2}"); }
	@Test public void test_1333() { checkNotSubtype("{null f2}","{null f2,null f3}"); }
	@Test public void test_1334() { checkNotSubtype("{null f2}","{null f1,int f2}"); }
	@Test public void test_1335() { checkNotSubtype("{null f2}","{null f2,int f3}"); }
	@Test public void test_1336() { checkIsSubtype("{null f2}","{int f1,void f2}"); }
	@Test public void test_1337() { checkIsSubtype("{null f2}","{int f2,void f3}"); }
	@Test public void test_1338() { checkNotSubtype("{null f2}","{int f1,any f2}"); }
	@Test public void test_1339() { checkNotSubtype("{null f2}","{int f2,any f3}"); }
	@Test public void test_1340() { checkNotSubtype("{null f2}","{int f1,null f2}"); }
	@Test public void test_1341() { checkNotSubtype("{null f2}","{int f2,null f3}"); }
	@Test public void test_1342() { checkNotSubtype("{null f2}","{int f1,int f2}"); }
	@Test public void test_1343() { checkNotSubtype("{null f2}","{int f2,int f3}"); }
	@Test public void test_1344() { checkNotSubtype("{null f2}","{[void] f1}"); }
	@Test public void test_1345() { checkNotSubtype("{null f2}","{[void] f2}"); }
	@Test public void test_1346() { checkIsSubtype("{null f2}","{[void] f1,void f2}"); }
	@Test public void test_1347() { checkIsSubtype("{null f2}","{[void] f2,void f3}"); }
	@Test public void test_1348() { checkNotSubtype("{null f2}","{[any] f1}"); }
	@Test public void test_1349() { checkNotSubtype("{null f2}","{[any] f2}"); }
	@Test public void test_1350() { checkNotSubtype("{null f2}","{[any] f1,any f2}"); }
	@Test public void test_1351() { checkNotSubtype("{null f2}","{[any] f2,any f3}"); }
	@Test public void test_1352() { checkNotSubtype("{null f2}","{[null] f1}"); }
	@Test public void test_1353() { checkNotSubtype("{null f2}","{[null] f2}"); }
	@Test public void test_1354() { checkNotSubtype("{null f2}","{[null] f1,null f2}"); }
	@Test public void test_1355() { checkNotSubtype("{null f2}","{[null] f2,null f3}"); }
	@Test public void test_1356() { checkNotSubtype("{null f2}","{[int] f1}"); }
	@Test public void test_1357() { checkNotSubtype("{null f2}","{[int] f2}"); }
	@Test public void test_1358() { checkNotSubtype("{null f2}","{[int] f1,int f2}"); }
	@Test public void test_1359() { checkNotSubtype("{null f2}","{[int] f2,int f3}"); }
	@Test public void test_1360() { checkIsSubtype("{null f2}","{{void f1} f1}"); }
	@Test public void test_1361() { checkIsSubtype("{null f2}","{{void f2} f1}"); }
	@Test public void test_1362() { checkIsSubtype("{null f2}","{{void f1} f2}"); }
	@Test public void test_1363() { checkIsSubtype("{null f2}","{{void f2} f2}"); }
	@Test public void test_1364() { checkIsSubtype("{null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1365() { checkIsSubtype("{null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1366() { checkIsSubtype("{null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1367() { checkIsSubtype("{null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1368() { checkNotSubtype("{null f2}","{{any f1} f1}"); }
	@Test public void test_1369() { checkNotSubtype("{null f2}","{{any f2} f1}"); }
	@Test public void test_1370() { checkNotSubtype("{null f2}","{{any f1} f2}"); }
	@Test public void test_1371() { checkNotSubtype("{null f2}","{{any f2} f2}"); }
	@Test public void test_1372() { checkNotSubtype("{null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1373() { checkNotSubtype("{null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1374() { checkNotSubtype("{null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1375() { checkNotSubtype("{null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1376() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test public void test_1377() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test public void test_1378() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test public void test_1379() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test public void test_1380() { checkNotSubtype("{null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1381() { checkNotSubtype("{null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1382() { checkNotSubtype("{null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1383() { checkNotSubtype("{null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1384() { checkNotSubtype("{null f2}","{{int f1} f1}"); }
	@Test public void test_1385() { checkNotSubtype("{null f2}","{{int f2} f1}"); }
	@Test public void test_1386() { checkNotSubtype("{null f2}","{{int f1} f2}"); }
	@Test public void test_1387() { checkNotSubtype("{null f2}","{{int f2} f2}"); }
	@Test public void test_1388() { checkNotSubtype("{null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1389() { checkNotSubtype("{null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1390() { checkNotSubtype("{null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1391() { checkNotSubtype("{null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1392() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_1393() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_1394() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_1395() { checkNotSubtype("{int f1}","[void]"); }
	@Test public void test_1396() { checkNotSubtype("{int f1}","[any]"); }
	@Test public void test_1397() { checkNotSubtype("{int f1}","[null]"); }
	@Test public void test_1398() { checkNotSubtype("{int f1}","[int]"); }
	@Test public void test_1399() { checkIsSubtype("{int f1}","{void f1}"); }
	@Test public void test_1400() { checkIsSubtype("{int f1}","{void f2}"); }
	@Test public void test_1401() { checkNotSubtype("{int f1}","{any f1}"); }
	@Test public void test_1402() { checkNotSubtype("{int f1}","{any f2}"); }
	@Test public void test_1403() { checkNotSubtype("{int f1}","{null f1}"); }
	@Test public void test_1404() { checkNotSubtype("{int f1}","{null f2}"); }
	@Test public void test_1405() { checkIsSubtype("{int f1}","{int f1}"); }
	@Test public void test_1406() { checkNotSubtype("{int f1}","{int f2}"); }
	@Test public void test_1407() { checkNotSubtype("{int f1}","[[void]]"); }
	@Test public void test_1408() { checkNotSubtype("{int f1}","[[any]]"); }
	@Test public void test_1409() { checkNotSubtype("{int f1}","[[null]]"); }
	@Test public void test_1410() { checkNotSubtype("{int f1}","[[int]]"); }
	@Test public void test_1411() { checkNotSubtype("{int f1}","[{void f1}]"); }
	@Test public void test_1412() { checkNotSubtype("{int f1}","[{void f2}]"); }
	@Test public void test_1413() { checkNotSubtype("{int f1}","[{any f1}]"); }
	@Test public void test_1414() { checkNotSubtype("{int f1}","[{any f2}]"); }
	@Test public void test_1415() { checkNotSubtype("{int f1}","[{null f1}]"); }
	@Test public void test_1416() { checkNotSubtype("{int f1}","[{null f2}]"); }
	@Test public void test_1417() { checkNotSubtype("{int f1}","[{int f1}]"); }
	@Test public void test_1418() { checkNotSubtype("{int f1}","[{int f2}]"); }
	@Test public void test_1419() { checkIsSubtype("{int f1}","{void f1,void f2}"); }
	@Test public void test_1420() { checkIsSubtype("{int f1}","{void f2,void f3}"); }
	@Test public void test_1421() { checkIsSubtype("{int f1}","{void f1,any f2}"); }
	@Test public void test_1422() { checkIsSubtype("{int f1}","{void f2,any f3}"); }
	@Test public void test_1423() { checkIsSubtype("{int f1}","{void f1,null f2}"); }
	@Test public void test_1424() { checkIsSubtype("{int f1}","{void f2,null f3}"); }
	@Test public void test_1425() { checkIsSubtype("{int f1}","{void f1,int f2}"); }
	@Test public void test_1426() { checkIsSubtype("{int f1}","{void f2,int f3}"); }
	@Test public void test_1427() { checkIsSubtype("{int f1}","{any f1,void f2}"); }
	@Test public void test_1428() { checkIsSubtype("{int f1}","{any f2,void f3}"); }
	@Test public void test_1429() { checkNotSubtype("{int f1}","{any f1,any f2}"); }
	@Test public void test_1430() { checkNotSubtype("{int f1}","{any f2,any f3}"); }
	@Test public void test_1431() { checkNotSubtype("{int f1}","{any f1,null f2}"); }
	@Test public void test_1432() { checkNotSubtype("{int f1}","{any f2,null f3}"); }
	@Test public void test_1433() { checkNotSubtype("{int f1}","{any f1,int f2}"); }
	@Test public void test_1434() { checkNotSubtype("{int f1}","{any f2,int f3}"); }
	@Test public void test_1435() { checkIsSubtype("{int f1}","{null f1,void f2}"); }
	@Test public void test_1436() { checkIsSubtype("{int f1}","{null f2,void f3}"); }
	@Test public void test_1437() { checkNotSubtype("{int f1}","{null f1,any f2}"); }
	@Test public void test_1438() { checkNotSubtype("{int f1}","{null f2,any f3}"); }
	@Test public void test_1439() { checkNotSubtype("{int f1}","{null f1,null f2}"); }
	@Test public void test_1440() { checkNotSubtype("{int f1}","{null f2,null f3}"); }
	@Test public void test_1441() { checkNotSubtype("{int f1}","{null f1,int f2}"); }
	@Test public void test_1442() { checkNotSubtype("{int f1}","{null f2,int f3}"); }
	@Test public void test_1443() { checkIsSubtype("{int f1}","{int f1,void f2}"); }
	@Test public void test_1444() { checkIsSubtype("{int f1}","{int f2,void f3}"); }
	@Test public void test_1445() { checkNotSubtype("{int f1}","{int f1,any f2}"); }
	@Test public void test_1446() { checkNotSubtype("{int f1}","{int f2,any f3}"); }
	@Test public void test_1447() { checkNotSubtype("{int f1}","{int f1,null f2}"); }
	@Test public void test_1448() { checkNotSubtype("{int f1}","{int f2,null f3}"); }
	@Test public void test_1449() { checkNotSubtype("{int f1}","{int f1,int f2}"); }
	@Test public void test_1450() { checkNotSubtype("{int f1}","{int f2,int f3}"); }
	@Test public void test_1451() { checkNotSubtype("{int f1}","{[void] f1}"); }
	@Test public void test_1452() { checkNotSubtype("{int f1}","{[void] f2}"); }
	@Test public void test_1453() { checkIsSubtype("{int f1}","{[void] f1,void f2}"); }
	@Test public void test_1454() { checkIsSubtype("{int f1}","{[void] f2,void f3}"); }
	@Test public void test_1455() { checkNotSubtype("{int f1}","{[any] f1}"); }
	@Test public void test_1456() { checkNotSubtype("{int f1}","{[any] f2}"); }
	@Test public void test_1457() { checkNotSubtype("{int f1}","{[any] f1,any f2}"); }
	@Test public void test_1458() { checkNotSubtype("{int f1}","{[any] f2,any f3}"); }
	@Test public void test_1459() { checkNotSubtype("{int f1}","{[null] f1}"); }
	@Test public void test_1460() { checkNotSubtype("{int f1}","{[null] f2}"); }
	@Test public void test_1461() { checkNotSubtype("{int f1}","{[null] f1,null f2}"); }
	@Test public void test_1462() { checkNotSubtype("{int f1}","{[null] f2,null f3}"); }
	@Test public void test_1463() { checkNotSubtype("{int f1}","{[int] f1}"); }
	@Test public void test_1464() { checkNotSubtype("{int f1}","{[int] f2}"); }
	@Test public void test_1465() { checkNotSubtype("{int f1}","{[int] f1,int f2}"); }
	@Test public void test_1466() { checkNotSubtype("{int f1}","{[int] f2,int f3}"); }
	@Test public void test_1467() { checkIsSubtype("{int f1}","{{void f1} f1}"); }
	@Test public void test_1468() { checkIsSubtype("{int f1}","{{void f2} f1}"); }
	@Test public void test_1469() { checkIsSubtype("{int f1}","{{void f1} f2}"); }
	@Test public void test_1470() { checkIsSubtype("{int f1}","{{void f2} f2}"); }
	@Test public void test_1471() { checkIsSubtype("{int f1}","{{void f1} f1,void f2}"); }
	@Test public void test_1472() { checkIsSubtype("{int f1}","{{void f2} f1,void f2}"); }
	@Test public void test_1473() { checkIsSubtype("{int f1}","{{void f1} f2,void f3}"); }
	@Test public void test_1474() { checkIsSubtype("{int f1}","{{void f2} f2,void f3}"); }
	@Test public void test_1475() { checkNotSubtype("{int f1}","{{any f1} f1}"); }
	@Test public void test_1476() { checkNotSubtype("{int f1}","{{any f2} f1}"); }
	@Test public void test_1477() { checkNotSubtype("{int f1}","{{any f1} f2}"); }
	@Test public void test_1478() { checkNotSubtype("{int f1}","{{any f2} f2}"); }
	@Test public void test_1479() { checkNotSubtype("{int f1}","{{any f1} f1,any f2}"); }
	@Test public void test_1480() { checkNotSubtype("{int f1}","{{any f2} f1,any f2}"); }
	@Test public void test_1481() { checkNotSubtype("{int f1}","{{any f1} f2,any f3}"); }
	@Test public void test_1482() { checkNotSubtype("{int f1}","{{any f2} f2,any f3}"); }
	@Test public void test_1483() { checkNotSubtype("{int f1}","{{null f1} f1}"); }
	@Test public void test_1484() { checkNotSubtype("{int f1}","{{null f2} f1}"); }
	@Test public void test_1485() { checkNotSubtype("{int f1}","{{null f1} f2}"); }
	@Test public void test_1486() { checkNotSubtype("{int f1}","{{null f2} f2}"); }
	@Test public void test_1487() { checkNotSubtype("{int f1}","{{null f1} f1,null f2}"); }
	@Test public void test_1488() { checkNotSubtype("{int f1}","{{null f2} f1,null f2}"); }
	@Test public void test_1489() { checkNotSubtype("{int f1}","{{null f1} f2,null f3}"); }
	@Test public void test_1490() { checkNotSubtype("{int f1}","{{null f2} f2,null f3}"); }
	@Test public void test_1491() { checkNotSubtype("{int f1}","{{int f1} f1}"); }
	@Test public void test_1492() { checkNotSubtype("{int f1}","{{int f2} f1}"); }
	@Test public void test_1493() { checkNotSubtype("{int f1}","{{int f1} f2}"); }
	@Test public void test_1494() { checkNotSubtype("{int f1}","{{int f2} f2}"); }
	@Test public void test_1495() { checkNotSubtype("{int f1}","{{int f1} f1,int f2}"); }
	@Test public void test_1496() { checkNotSubtype("{int f1}","{{int f2} f1,int f2}"); }
	@Test public void test_1497() { checkNotSubtype("{int f1}","{{int f1} f2,int f3}"); }
	@Test public void test_1498() { checkNotSubtype("{int f1}","{{int f2} f2,int f3}"); }
	@Test public void test_1499() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_1500() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_1501() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_1502() { checkNotSubtype("{int f2}","[void]"); }
	@Test public void test_1503() { checkNotSubtype("{int f2}","[any]"); }
	@Test public void test_1504() { checkNotSubtype("{int f2}","[null]"); }
	@Test public void test_1505() { checkNotSubtype("{int f2}","[int]"); }
	@Test public void test_1506() { checkIsSubtype("{int f2}","{void f1}"); }
	@Test public void test_1507() { checkIsSubtype("{int f2}","{void f2}"); }
	@Test public void test_1508() { checkNotSubtype("{int f2}","{any f1}"); }
	@Test public void test_1509() { checkNotSubtype("{int f2}","{any f2}"); }
	@Test public void test_1510() { checkNotSubtype("{int f2}","{null f1}"); }
	@Test public void test_1511() { checkNotSubtype("{int f2}","{null f2}"); }
	@Test public void test_1512() { checkNotSubtype("{int f2}","{int f1}"); }
	@Test public void test_1513() { checkIsSubtype("{int f2}","{int f2}"); }
	@Test public void test_1514() { checkNotSubtype("{int f2}","[[void]]"); }
	@Test public void test_1515() { checkNotSubtype("{int f2}","[[any]]"); }
	@Test public void test_1516() { checkNotSubtype("{int f2}","[[null]]"); }
	@Test public void test_1517() { checkNotSubtype("{int f2}","[[int]]"); }
	@Test public void test_1518() { checkNotSubtype("{int f2}","[{void f1}]"); }
	@Test public void test_1519() { checkNotSubtype("{int f2}","[{void f2}]"); }
	@Test public void test_1520() { checkNotSubtype("{int f2}","[{any f1}]"); }
	@Test public void test_1521() { checkNotSubtype("{int f2}","[{any f2}]"); }
	@Test public void test_1522() { checkNotSubtype("{int f2}","[{null f1}]"); }
	@Test public void test_1523() { checkNotSubtype("{int f2}","[{null f2}]"); }
	@Test public void test_1524() { checkNotSubtype("{int f2}","[{int f1}]"); }
	@Test public void test_1525() { checkNotSubtype("{int f2}","[{int f2}]"); }
	@Test public void test_1526() { checkIsSubtype("{int f2}","{void f1,void f2}"); }
	@Test public void test_1527() { checkIsSubtype("{int f2}","{void f2,void f3}"); }
	@Test public void test_1528() { checkIsSubtype("{int f2}","{void f1,any f2}"); }
	@Test public void test_1529() { checkIsSubtype("{int f2}","{void f2,any f3}"); }
	@Test public void test_1530() { checkIsSubtype("{int f2}","{void f1,null f2}"); }
	@Test public void test_1531() { checkIsSubtype("{int f2}","{void f2,null f3}"); }
	@Test public void test_1532() { checkIsSubtype("{int f2}","{void f1,int f2}"); }
	@Test public void test_1533() { checkIsSubtype("{int f2}","{void f2,int f3}"); }
	@Test public void test_1534() { checkIsSubtype("{int f2}","{any f1,void f2}"); }
	@Test public void test_1535() { checkIsSubtype("{int f2}","{any f2,void f3}"); }
	@Test public void test_1536() { checkNotSubtype("{int f2}","{any f1,any f2}"); }
	@Test public void test_1537() { checkNotSubtype("{int f2}","{any f2,any f3}"); }
	@Test public void test_1538() { checkNotSubtype("{int f2}","{any f1,null f2}"); }
	@Test public void test_1539() { checkNotSubtype("{int f2}","{any f2,null f3}"); }
	@Test public void test_1540() { checkNotSubtype("{int f2}","{any f1,int f2}"); }
	@Test public void test_1541() { checkNotSubtype("{int f2}","{any f2,int f3}"); }
	@Test public void test_1542() { checkIsSubtype("{int f2}","{null f1,void f2}"); }
	@Test public void test_1543() { checkIsSubtype("{int f2}","{null f2,void f3}"); }
	@Test public void test_1544() { checkNotSubtype("{int f2}","{null f1,any f2}"); }
	@Test public void test_1545() { checkNotSubtype("{int f2}","{null f2,any f3}"); }
	@Test public void test_1546() { checkNotSubtype("{int f2}","{null f1,null f2}"); }
	@Test public void test_1547() { checkNotSubtype("{int f2}","{null f2,null f3}"); }
	@Test public void test_1548() { checkNotSubtype("{int f2}","{null f1,int f2}"); }
	@Test public void test_1549() { checkNotSubtype("{int f2}","{null f2,int f3}"); }
	@Test public void test_1550() { checkIsSubtype("{int f2}","{int f1,void f2}"); }
	@Test public void test_1551() { checkIsSubtype("{int f2}","{int f2,void f3}"); }
	@Test public void test_1552() { checkNotSubtype("{int f2}","{int f1,any f2}"); }
	@Test public void test_1553() { checkNotSubtype("{int f2}","{int f2,any f3}"); }
	@Test public void test_1554() { checkNotSubtype("{int f2}","{int f1,null f2}"); }
	@Test public void test_1555() { checkNotSubtype("{int f2}","{int f2,null f3}"); }
	@Test public void test_1556() { checkNotSubtype("{int f2}","{int f1,int f2}"); }
	@Test public void test_1557() { checkNotSubtype("{int f2}","{int f2,int f3}"); }
	@Test public void test_1558() { checkNotSubtype("{int f2}","{[void] f1}"); }
	@Test public void test_1559() { checkNotSubtype("{int f2}","{[void] f2}"); }
	@Test public void test_1560() { checkIsSubtype("{int f2}","{[void] f1,void f2}"); }
	@Test public void test_1561() { checkIsSubtype("{int f2}","{[void] f2,void f3}"); }
	@Test public void test_1562() { checkNotSubtype("{int f2}","{[any] f1}"); }
	@Test public void test_1563() { checkNotSubtype("{int f2}","{[any] f2}"); }
	@Test public void test_1564() { checkNotSubtype("{int f2}","{[any] f1,any f2}"); }
	@Test public void test_1565() { checkNotSubtype("{int f2}","{[any] f2,any f3}"); }
	@Test public void test_1566() { checkNotSubtype("{int f2}","{[null] f1}"); }
	@Test public void test_1567() { checkNotSubtype("{int f2}","{[null] f2}"); }
	@Test public void test_1568() { checkNotSubtype("{int f2}","{[null] f1,null f2}"); }
	@Test public void test_1569() { checkNotSubtype("{int f2}","{[null] f2,null f3}"); }
	@Test public void test_1570() { checkNotSubtype("{int f2}","{[int] f1}"); }
	@Test public void test_1571() { checkNotSubtype("{int f2}","{[int] f2}"); }
	@Test public void test_1572() { checkNotSubtype("{int f2}","{[int] f1,int f2}"); }
	@Test public void test_1573() { checkNotSubtype("{int f2}","{[int] f2,int f3}"); }
	@Test public void test_1574() { checkIsSubtype("{int f2}","{{void f1} f1}"); }
	@Test public void test_1575() { checkIsSubtype("{int f2}","{{void f2} f1}"); }
	@Test public void test_1576() { checkIsSubtype("{int f2}","{{void f1} f2}"); }
	@Test public void test_1577() { checkIsSubtype("{int f2}","{{void f2} f2}"); }
	@Test public void test_1578() { checkIsSubtype("{int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1579() { checkIsSubtype("{int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1580() { checkIsSubtype("{int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1581() { checkIsSubtype("{int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1582() { checkNotSubtype("{int f2}","{{any f1} f1}"); }
	@Test public void test_1583() { checkNotSubtype("{int f2}","{{any f2} f1}"); }
	@Test public void test_1584() { checkNotSubtype("{int f2}","{{any f1} f2}"); }
	@Test public void test_1585() { checkNotSubtype("{int f2}","{{any f2} f2}"); }
	@Test public void test_1586() { checkNotSubtype("{int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1587() { checkNotSubtype("{int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1588() { checkNotSubtype("{int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1589() { checkNotSubtype("{int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1590() { checkNotSubtype("{int f2}","{{null f1} f1}"); }
	@Test public void test_1591() { checkNotSubtype("{int f2}","{{null f2} f1}"); }
	@Test public void test_1592() { checkNotSubtype("{int f2}","{{null f1} f2}"); }
	@Test public void test_1593() { checkNotSubtype("{int f2}","{{null f2} f2}"); }
	@Test public void test_1594() { checkNotSubtype("{int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1595() { checkNotSubtype("{int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1596() { checkNotSubtype("{int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1597() { checkNotSubtype("{int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1598() { checkNotSubtype("{int f2}","{{int f1} f1}"); }
	@Test public void test_1599() { checkNotSubtype("{int f2}","{{int f2} f1}"); }
	@Test public void test_1600() { checkNotSubtype("{int f2}","{{int f1} f2}"); }
	@Test public void test_1601() { checkNotSubtype("{int f2}","{{int f2} f2}"); }
	@Test public void test_1602() { checkNotSubtype("{int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1603() { checkNotSubtype("{int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1604() { checkNotSubtype("{int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1605() { checkNotSubtype("{int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1606() { checkNotSubtype("[[void]]","any"); }
	@Test public void test_1607() { checkNotSubtype("[[void]]","null"); }
	@Test public void test_1608() { checkNotSubtype("[[void]]","int"); }
	@Test public void test_1609() { checkIsSubtype("[[void]]","[void]"); }
	@Test public void test_1610() { checkNotSubtype("[[void]]","[any]"); }
	@Test public void test_1611() { checkNotSubtype("[[void]]","[null]"); }
	@Test public void test_1612() { checkNotSubtype("[[void]]","[int]"); }
	@Test public void test_1613() { checkIsSubtype("[[void]]","{void f1}"); }
	@Test public void test_1614() { checkIsSubtype("[[void]]","{void f2}"); }
	@Test public void test_1615() { checkNotSubtype("[[void]]","{any f1}"); }
	@Test public void test_1616() { checkNotSubtype("[[void]]","{any f2}"); }
	@Test public void test_1617() { checkNotSubtype("[[void]]","{null f1}"); }
	@Test public void test_1618() { checkNotSubtype("[[void]]","{null f2}"); }
	@Test public void test_1619() { checkNotSubtype("[[void]]","{int f1}"); }
	@Test public void test_1620() { checkNotSubtype("[[void]]","{int f2}"); }
	@Test public void test_1621() { checkIsSubtype("[[void]]","[[void]]"); }
	@Test public void test_1622() { checkNotSubtype("[[void]]","[[any]]"); }
	@Test public void test_1623() { checkNotSubtype("[[void]]","[[null]]"); }
	@Test public void test_1624() { checkNotSubtype("[[void]]","[[int]]"); }
	@Test public void test_1625() { checkIsSubtype("[[void]]","[{void f1}]"); }
	@Test public void test_1626() { checkIsSubtype("[[void]]","[{void f2}]"); }
	@Test public void test_1627() { checkNotSubtype("[[void]]","[{any f1}]"); }
	@Test public void test_1628() { checkNotSubtype("[[void]]","[{any f2}]"); }
	@Test public void test_1629() { checkNotSubtype("[[void]]","[{null f1}]"); }
	@Test public void test_1630() { checkNotSubtype("[[void]]","[{null f2}]"); }
	@Test public void test_1631() { checkNotSubtype("[[void]]","[{int f1}]"); }
	@Test public void test_1632() { checkNotSubtype("[[void]]","[{int f2}]"); }
	@Test public void test_1633() { checkIsSubtype("[[void]]","{void f1,void f2}"); }
	@Test public void test_1634() { checkIsSubtype("[[void]]","{void f2,void f3}"); }
	@Test public void test_1635() { checkIsSubtype("[[void]]","{void f1,any f2}"); }
	@Test public void test_1636() { checkIsSubtype("[[void]]","{void f2,any f3}"); }
	@Test public void test_1637() { checkIsSubtype("[[void]]","{void f1,null f2}"); }
	@Test public void test_1638() { checkIsSubtype("[[void]]","{void f2,null f3}"); }
	@Test public void test_1639() { checkIsSubtype("[[void]]","{void f1,int f2}"); }
	@Test public void test_1640() { checkIsSubtype("[[void]]","{void f2,int f3}"); }
	@Test public void test_1641() { checkIsSubtype("[[void]]","{any f1,void f2}"); }
	@Test public void test_1642() { checkIsSubtype("[[void]]","{any f2,void f3}"); }
	@Test public void test_1643() { checkNotSubtype("[[void]]","{any f1,any f2}"); }
	@Test public void test_1644() { checkNotSubtype("[[void]]","{any f2,any f3}"); }
	@Test public void test_1645() { checkNotSubtype("[[void]]","{any f1,null f2}"); }
	@Test public void test_1646() { checkNotSubtype("[[void]]","{any f2,null f3}"); }
	@Test public void test_1647() { checkNotSubtype("[[void]]","{any f1,int f2}"); }
	@Test public void test_1648() { checkNotSubtype("[[void]]","{any f2,int f3}"); }
	@Test public void test_1649() { checkIsSubtype("[[void]]","{null f1,void f2}"); }
	@Test public void test_1650() { checkIsSubtype("[[void]]","{null f2,void f3}"); }
	@Test public void test_1651() { checkNotSubtype("[[void]]","{null f1,any f2}"); }
	@Test public void test_1652() { checkNotSubtype("[[void]]","{null f2,any f3}"); }
	@Test public void test_1653() { checkNotSubtype("[[void]]","{null f1,null f2}"); }
	@Test public void test_1654() { checkNotSubtype("[[void]]","{null f2,null f3}"); }
	@Test public void test_1655() { checkNotSubtype("[[void]]","{null f1,int f2}"); }
	@Test public void test_1656() { checkNotSubtype("[[void]]","{null f2,int f3}"); }
	@Test public void test_1657() { checkIsSubtype("[[void]]","{int f1,void f2}"); }
	@Test public void test_1658() { checkIsSubtype("[[void]]","{int f2,void f3}"); }
	@Test public void test_1659() { checkNotSubtype("[[void]]","{int f1,any f2}"); }
	@Test public void test_1660() { checkNotSubtype("[[void]]","{int f2,any f3}"); }
	@Test public void test_1661() { checkNotSubtype("[[void]]","{int f1,null f2}"); }
	@Test public void test_1662() { checkNotSubtype("[[void]]","{int f2,null f3}"); }
	@Test public void test_1663() { checkNotSubtype("[[void]]","{int f1,int f2}"); }
	@Test public void test_1664() { checkNotSubtype("[[void]]","{int f2,int f3}"); }
	@Test public void test_1665() { checkNotSubtype("[[void]]","{[void] f1}"); }
	@Test public void test_1666() { checkNotSubtype("[[void]]","{[void] f2}"); }
	@Test public void test_1667() { checkIsSubtype("[[void]]","{[void] f1,void f2}"); }
	@Test public void test_1668() { checkIsSubtype("[[void]]","{[void] f2,void f3}"); }
	@Test public void test_1669() { checkNotSubtype("[[void]]","{[any] f1}"); }
	@Test public void test_1670() { checkNotSubtype("[[void]]","{[any] f2}"); }
	@Test public void test_1671() { checkNotSubtype("[[void]]","{[any] f1,any f2}"); }
	@Test public void test_1672() { checkNotSubtype("[[void]]","{[any] f2,any f3}"); }
	@Test public void test_1673() { checkNotSubtype("[[void]]","{[null] f1}"); }
	@Test public void test_1674() { checkNotSubtype("[[void]]","{[null] f2}"); }
	@Test public void test_1675() { checkNotSubtype("[[void]]","{[null] f1,null f2}"); }
	@Test public void test_1676() { checkNotSubtype("[[void]]","{[null] f2,null f3}"); }
	@Test public void test_1677() { checkNotSubtype("[[void]]","{[int] f1}"); }
	@Test public void test_1678() { checkNotSubtype("[[void]]","{[int] f2}"); }
	@Test public void test_1679() { checkNotSubtype("[[void]]","{[int] f1,int f2}"); }
	@Test public void test_1680() { checkNotSubtype("[[void]]","{[int] f2,int f3}"); }
	@Test public void test_1681() { checkIsSubtype("[[void]]","{{void f1} f1}"); }
	@Test public void test_1682() { checkIsSubtype("[[void]]","{{void f2} f1}"); }
	@Test public void test_1683() { checkIsSubtype("[[void]]","{{void f1} f2}"); }
	@Test public void test_1684() { checkIsSubtype("[[void]]","{{void f2} f2}"); }
	@Test public void test_1685() { checkIsSubtype("[[void]]","{{void f1} f1,void f2}"); }
	@Test public void test_1686() { checkIsSubtype("[[void]]","{{void f2} f1,void f2}"); }
	@Test public void test_1687() { checkIsSubtype("[[void]]","{{void f1} f2,void f3}"); }
	@Test public void test_1688() { checkIsSubtype("[[void]]","{{void f2} f2,void f3}"); }
	@Test public void test_1689() { checkNotSubtype("[[void]]","{{any f1} f1}"); }
	@Test public void test_1690() { checkNotSubtype("[[void]]","{{any f2} f1}"); }
	@Test public void test_1691() { checkNotSubtype("[[void]]","{{any f1} f2}"); }
	@Test public void test_1692() { checkNotSubtype("[[void]]","{{any f2} f2}"); }
	@Test public void test_1693() { checkNotSubtype("[[void]]","{{any f1} f1,any f2}"); }
	@Test public void test_1694() { checkNotSubtype("[[void]]","{{any f2} f1,any f2}"); }
	@Test public void test_1695() { checkNotSubtype("[[void]]","{{any f1} f2,any f3}"); }
	@Test public void test_1696() { checkNotSubtype("[[void]]","{{any f2} f2,any f3}"); }
	@Test public void test_1697() { checkNotSubtype("[[void]]","{{null f1} f1}"); }
	@Test public void test_1698() { checkNotSubtype("[[void]]","{{null f2} f1}"); }
	@Test public void test_1699() { checkNotSubtype("[[void]]","{{null f1} f2}"); }
	@Test public void test_1700() { checkNotSubtype("[[void]]","{{null f2} f2}"); }
	@Test public void test_1701() { checkNotSubtype("[[void]]","{{null f1} f1,null f2}"); }
	@Test public void test_1702() { checkNotSubtype("[[void]]","{{null f2} f1,null f2}"); }
	@Test public void test_1703() { checkNotSubtype("[[void]]","{{null f1} f2,null f3}"); }
	@Test public void test_1704() { checkNotSubtype("[[void]]","{{null f2} f2,null f3}"); }
	@Test public void test_1705() { checkNotSubtype("[[void]]","{{int f1} f1}"); }
	@Test public void test_1706() { checkNotSubtype("[[void]]","{{int f2} f1}"); }
	@Test public void test_1707() { checkNotSubtype("[[void]]","{{int f1} f2}"); }
	@Test public void test_1708() { checkNotSubtype("[[void]]","{{int f2} f2}"); }
	@Test public void test_1709() { checkNotSubtype("[[void]]","{{int f1} f1,int f2}"); }
	@Test public void test_1710() { checkNotSubtype("[[void]]","{{int f2} f1,int f2}"); }
	@Test public void test_1711() { checkNotSubtype("[[void]]","{{int f1} f2,int f3}"); }
	@Test public void test_1712() { checkNotSubtype("[[void]]","{{int f2} f2,int f3}"); }
	@Test public void test_1713() { checkNotSubtype("[[any]]","any"); }
	@Test public void test_1714() { checkNotSubtype("[[any]]","null"); }
	@Test public void test_1715() { checkNotSubtype("[[any]]","int"); }
	@Test public void test_1716() { checkIsSubtype("[[any]]","[void]"); }
	@Test public void test_1717() { checkNotSubtype("[[any]]","[any]"); }
	@Test public void test_1718() { checkNotSubtype("[[any]]","[null]"); }
	@Test public void test_1719() { checkNotSubtype("[[any]]","[int]"); }
	@Test public void test_1720() { checkIsSubtype("[[any]]","{void f1}"); }
	@Test public void test_1721() { checkIsSubtype("[[any]]","{void f2}"); }
	@Test public void test_1722() { checkNotSubtype("[[any]]","{any f1}"); }
	@Test public void test_1723() { checkNotSubtype("[[any]]","{any f2}"); }
	@Test public void test_1724() { checkNotSubtype("[[any]]","{null f1}"); }
	@Test public void test_1725() { checkNotSubtype("[[any]]","{null f2}"); }
	@Test public void test_1726() { checkNotSubtype("[[any]]","{int f1}"); }
	@Test public void test_1727() { checkNotSubtype("[[any]]","{int f2}"); }
	@Test public void test_1728() { checkIsSubtype("[[any]]","[[void]]"); }
	@Test public void test_1729() { checkIsSubtype("[[any]]","[[any]]"); }
	@Test public void test_1730() { checkIsSubtype("[[any]]","[[null]]"); }
	@Test public void test_1731() { checkIsSubtype("[[any]]","[[int]]"); }
	@Test public void test_1732() { checkIsSubtype("[[any]]","[{void f1}]"); }
	@Test public void test_1733() { checkIsSubtype("[[any]]","[{void f2}]"); }
	@Test public void test_1734() { checkNotSubtype("[[any]]","[{any f1}]"); }
	@Test public void test_1735() { checkNotSubtype("[[any]]","[{any f2}]"); }
	@Test public void test_1736() { checkNotSubtype("[[any]]","[{null f1}]"); }
	@Test public void test_1737() { checkNotSubtype("[[any]]","[{null f2}]"); }
	@Test public void test_1738() { checkNotSubtype("[[any]]","[{int f1}]"); }
	@Test public void test_1739() { checkNotSubtype("[[any]]","[{int f2}]"); }
	@Test public void test_1740() { checkIsSubtype("[[any]]","{void f1,void f2}"); }
	@Test public void test_1741() { checkIsSubtype("[[any]]","{void f2,void f3}"); }
	@Test public void test_1742() { checkIsSubtype("[[any]]","{void f1,any f2}"); }
	@Test public void test_1743() { checkIsSubtype("[[any]]","{void f2,any f3}"); }
	@Test public void test_1744() { checkIsSubtype("[[any]]","{void f1,null f2}"); }
	@Test public void test_1745() { checkIsSubtype("[[any]]","{void f2,null f3}"); }
	@Test public void test_1746() { checkIsSubtype("[[any]]","{void f1,int f2}"); }
	@Test public void test_1747() { checkIsSubtype("[[any]]","{void f2,int f3}"); }
	@Test public void test_1748() { checkIsSubtype("[[any]]","{any f1,void f2}"); }
	@Test public void test_1749() { checkIsSubtype("[[any]]","{any f2,void f3}"); }
	@Test public void test_1750() { checkNotSubtype("[[any]]","{any f1,any f2}"); }
	@Test public void test_1751() { checkNotSubtype("[[any]]","{any f2,any f3}"); }
	@Test public void test_1752() { checkNotSubtype("[[any]]","{any f1,null f2}"); }
	@Test public void test_1753() { checkNotSubtype("[[any]]","{any f2,null f3}"); }
	@Test public void test_1754() { checkNotSubtype("[[any]]","{any f1,int f2}"); }
	@Test public void test_1755() { checkNotSubtype("[[any]]","{any f2,int f3}"); }
	@Test public void test_1756() { checkIsSubtype("[[any]]","{null f1,void f2}"); }
	@Test public void test_1757() { checkIsSubtype("[[any]]","{null f2,void f3}"); }
	@Test public void test_1758() { checkNotSubtype("[[any]]","{null f1,any f2}"); }
	@Test public void test_1759() { checkNotSubtype("[[any]]","{null f2,any f3}"); }
	@Test public void test_1760() { checkNotSubtype("[[any]]","{null f1,null f2}"); }
	@Test public void test_1761() { checkNotSubtype("[[any]]","{null f2,null f3}"); }
	@Test public void test_1762() { checkNotSubtype("[[any]]","{null f1,int f2}"); }
	@Test public void test_1763() { checkNotSubtype("[[any]]","{null f2,int f3}"); }
	@Test public void test_1764() { checkIsSubtype("[[any]]","{int f1,void f2}"); }
	@Test public void test_1765() { checkIsSubtype("[[any]]","{int f2,void f3}"); }
	@Test public void test_1766() { checkNotSubtype("[[any]]","{int f1,any f2}"); }
	@Test public void test_1767() { checkNotSubtype("[[any]]","{int f2,any f3}"); }
	@Test public void test_1768() { checkNotSubtype("[[any]]","{int f1,null f2}"); }
	@Test public void test_1769() { checkNotSubtype("[[any]]","{int f2,null f3}"); }
	@Test public void test_1770() { checkNotSubtype("[[any]]","{int f1,int f2}"); }
	@Test public void test_1771() { checkNotSubtype("[[any]]","{int f2,int f3}"); }
	@Test public void test_1772() { checkNotSubtype("[[any]]","{[void] f1}"); }
	@Test public void test_1773() { checkNotSubtype("[[any]]","{[void] f2}"); }
	@Test public void test_1774() { checkIsSubtype("[[any]]","{[void] f1,void f2}"); }
	@Test public void test_1775() { checkIsSubtype("[[any]]","{[void] f2,void f3}"); }
	@Test public void test_1776() { checkNotSubtype("[[any]]","{[any] f1}"); }
	@Test public void test_1777() { checkNotSubtype("[[any]]","{[any] f2}"); }
	@Test public void test_1778() { checkNotSubtype("[[any]]","{[any] f1,any f2}"); }
	@Test public void test_1779() { checkNotSubtype("[[any]]","{[any] f2,any f3}"); }
	@Test public void test_1780() { checkNotSubtype("[[any]]","{[null] f1}"); }
	@Test public void test_1781() { checkNotSubtype("[[any]]","{[null] f2}"); }
	@Test public void test_1782() { checkNotSubtype("[[any]]","{[null] f1,null f2}"); }
	@Test public void test_1783() { checkNotSubtype("[[any]]","{[null] f2,null f3}"); }
	@Test public void test_1784() { checkNotSubtype("[[any]]","{[int] f1}"); }
	@Test public void test_1785() { checkNotSubtype("[[any]]","{[int] f2}"); }
	@Test public void test_1786() { checkNotSubtype("[[any]]","{[int] f1,int f2}"); }
	@Test public void test_1787() { checkNotSubtype("[[any]]","{[int] f2,int f3}"); }
	@Test public void test_1788() { checkIsSubtype("[[any]]","{{void f1} f1}"); }
	@Test public void test_1789() { checkIsSubtype("[[any]]","{{void f2} f1}"); }
	@Test public void test_1790() { checkIsSubtype("[[any]]","{{void f1} f2}"); }
	@Test public void test_1791() { checkIsSubtype("[[any]]","{{void f2} f2}"); }
	@Test public void test_1792() { checkIsSubtype("[[any]]","{{void f1} f1,void f2}"); }
	@Test public void test_1793() { checkIsSubtype("[[any]]","{{void f2} f1,void f2}"); }
	@Test public void test_1794() { checkIsSubtype("[[any]]","{{void f1} f2,void f3}"); }
	@Test public void test_1795() { checkIsSubtype("[[any]]","{{void f2} f2,void f3}"); }
	@Test public void test_1796() { checkNotSubtype("[[any]]","{{any f1} f1}"); }
	@Test public void test_1797() { checkNotSubtype("[[any]]","{{any f2} f1}"); }
	@Test public void test_1798() { checkNotSubtype("[[any]]","{{any f1} f2}"); }
	@Test public void test_1799() { checkNotSubtype("[[any]]","{{any f2} f2}"); }
	@Test public void test_1800() { checkNotSubtype("[[any]]","{{any f1} f1,any f2}"); }
	@Test public void test_1801() { checkNotSubtype("[[any]]","{{any f2} f1,any f2}"); }
	@Test public void test_1802() { checkNotSubtype("[[any]]","{{any f1} f2,any f3}"); }
	@Test public void test_1803() { checkNotSubtype("[[any]]","{{any f2} f2,any f3}"); }
	@Test public void test_1804() { checkNotSubtype("[[any]]","{{null f1} f1}"); }
	@Test public void test_1805() { checkNotSubtype("[[any]]","{{null f2} f1}"); }
	@Test public void test_1806() { checkNotSubtype("[[any]]","{{null f1} f2}"); }
	@Test public void test_1807() { checkNotSubtype("[[any]]","{{null f2} f2}"); }
	@Test public void test_1808() { checkNotSubtype("[[any]]","{{null f1} f1,null f2}"); }
	@Test public void test_1809() { checkNotSubtype("[[any]]","{{null f2} f1,null f2}"); }
	@Test public void test_1810() { checkNotSubtype("[[any]]","{{null f1} f2,null f3}"); }
	@Test public void test_1811() { checkNotSubtype("[[any]]","{{null f2} f2,null f3}"); }
	@Test public void test_1812() { checkNotSubtype("[[any]]","{{int f1} f1}"); }
	@Test public void test_1813() { checkNotSubtype("[[any]]","{{int f2} f1}"); }
	@Test public void test_1814() { checkNotSubtype("[[any]]","{{int f1} f2}"); }
	@Test public void test_1815() { checkNotSubtype("[[any]]","{{int f2} f2}"); }
	@Test public void test_1816() { checkNotSubtype("[[any]]","{{int f1} f1,int f2}"); }
	@Test public void test_1817() { checkNotSubtype("[[any]]","{{int f2} f1,int f2}"); }
	@Test public void test_1818() { checkNotSubtype("[[any]]","{{int f1} f2,int f3}"); }
	@Test public void test_1819() { checkNotSubtype("[[any]]","{{int f2} f2,int f3}"); }
	@Test public void test_1820() { checkNotSubtype("[[null]]","any"); }
	@Test public void test_1821() { checkNotSubtype("[[null]]","null"); }
	@Test public void test_1822() { checkNotSubtype("[[null]]","int"); }
	@Test public void test_1823() { checkIsSubtype("[[null]]","[void]"); }
	@Test public void test_1824() { checkNotSubtype("[[null]]","[any]"); }
	@Test public void test_1825() { checkNotSubtype("[[null]]","[null]"); }
	@Test public void test_1826() { checkNotSubtype("[[null]]","[int]"); }
	@Test public void test_1827() { checkIsSubtype("[[null]]","{void f1}"); }
	@Test public void test_1828() { checkIsSubtype("[[null]]","{void f2}"); }
	@Test public void test_1829() { checkNotSubtype("[[null]]","{any f1}"); }
	@Test public void test_1830() { checkNotSubtype("[[null]]","{any f2}"); }
	@Test public void test_1831() { checkNotSubtype("[[null]]","{null f1}"); }
	@Test public void test_1832() { checkNotSubtype("[[null]]","{null f2}"); }
	@Test public void test_1833() { checkNotSubtype("[[null]]","{int f1}"); }
	@Test public void test_1834() { checkNotSubtype("[[null]]","{int f2}"); }
	@Test public void test_1835() { checkIsSubtype("[[null]]","[[void]]"); }
	@Test public void test_1836() { checkNotSubtype("[[null]]","[[any]]"); }
	@Test public void test_1837() { checkIsSubtype("[[null]]","[[null]]"); }
	@Test public void test_1838() { checkNotSubtype("[[null]]","[[int]]"); }
	@Test public void test_1839() { checkIsSubtype("[[null]]","[{void f1}]"); }
	@Test public void test_1840() { checkIsSubtype("[[null]]","[{void f2}]"); }
	@Test public void test_1841() { checkNotSubtype("[[null]]","[{any f1}]"); }
	@Test public void test_1842() { checkNotSubtype("[[null]]","[{any f2}]"); }
	@Test public void test_1843() { checkNotSubtype("[[null]]","[{null f1}]"); }
	@Test public void test_1844() { checkNotSubtype("[[null]]","[{null f2}]"); }
	@Test public void test_1845() { checkNotSubtype("[[null]]","[{int f1}]"); }
	@Test public void test_1846() { checkNotSubtype("[[null]]","[{int f2}]"); }
	@Test public void test_1847() { checkIsSubtype("[[null]]","{void f1,void f2}"); }
	@Test public void test_1848() { checkIsSubtype("[[null]]","{void f2,void f3}"); }
	@Test public void test_1849() { checkIsSubtype("[[null]]","{void f1,any f2}"); }
	@Test public void test_1850() { checkIsSubtype("[[null]]","{void f2,any f3}"); }
	@Test public void test_1851() { checkIsSubtype("[[null]]","{void f1,null f2}"); }
	@Test public void test_1852() { checkIsSubtype("[[null]]","{void f2,null f3}"); }
	@Test public void test_1853() { checkIsSubtype("[[null]]","{void f1,int f2}"); }
	@Test public void test_1854() { checkIsSubtype("[[null]]","{void f2,int f3}"); }
	@Test public void test_1855() { checkIsSubtype("[[null]]","{any f1,void f2}"); }
	@Test public void test_1856() { checkIsSubtype("[[null]]","{any f2,void f3}"); }
	@Test public void test_1857() { checkNotSubtype("[[null]]","{any f1,any f2}"); }
	@Test public void test_1858() { checkNotSubtype("[[null]]","{any f2,any f3}"); }
	@Test public void test_1859() { checkNotSubtype("[[null]]","{any f1,null f2}"); }
	@Test public void test_1860() { checkNotSubtype("[[null]]","{any f2,null f3}"); }
	@Test public void test_1861() { checkNotSubtype("[[null]]","{any f1,int f2}"); }
	@Test public void test_1862() { checkNotSubtype("[[null]]","{any f2,int f3}"); }
	@Test public void test_1863() { checkIsSubtype("[[null]]","{null f1,void f2}"); }
	@Test public void test_1864() { checkIsSubtype("[[null]]","{null f2,void f3}"); }
	@Test public void test_1865() { checkNotSubtype("[[null]]","{null f1,any f2}"); }
	@Test public void test_1866() { checkNotSubtype("[[null]]","{null f2,any f3}"); }
	@Test public void test_1867() { checkNotSubtype("[[null]]","{null f1,null f2}"); }
	@Test public void test_1868() { checkNotSubtype("[[null]]","{null f2,null f3}"); }
	@Test public void test_1869() { checkNotSubtype("[[null]]","{null f1,int f2}"); }
	@Test public void test_1870() { checkNotSubtype("[[null]]","{null f2,int f3}"); }
	@Test public void test_1871() { checkIsSubtype("[[null]]","{int f1,void f2}"); }
	@Test public void test_1872() { checkIsSubtype("[[null]]","{int f2,void f3}"); }
	@Test public void test_1873() { checkNotSubtype("[[null]]","{int f1,any f2}"); }
	@Test public void test_1874() { checkNotSubtype("[[null]]","{int f2,any f3}"); }
	@Test public void test_1875() { checkNotSubtype("[[null]]","{int f1,null f2}"); }
	@Test public void test_1876() { checkNotSubtype("[[null]]","{int f2,null f3}"); }
	@Test public void test_1877() { checkNotSubtype("[[null]]","{int f1,int f2}"); }
	@Test public void test_1878() { checkNotSubtype("[[null]]","{int f2,int f3}"); }
	@Test public void test_1879() { checkNotSubtype("[[null]]","{[void] f1}"); }
	@Test public void test_1880() { checkNotSubtype("[[null]]","{[void] f2}"); }
	@Test public void test_1881() { checkIsSubtype("[[null]]","{[void] f1,void f2}"); }
	@Test public void test_1882() { checkIsSubtype("[[null]]","{[void] f2,void f3}"); }
	@Test public void test_1883() { checkNotSubtype("[[null]]","{[any] f1}"); }
	@Test public void test_1884() { checkNotSubtype("[[null]]","{[any] f2}"); }
	@Test public void test_1885() { checkNotSubtype("[[null]]","{[any] f1,any f2}"); }
	@Test public void test_1886() { checkNotSubtype("[[null]]","{[any] f2,any f3}"); }
	@Test public void test_1887() { checkNotSubtype("[[null]]","{[null] f1}"); }
	@Test public void test_1888() { checkNotSubtype("[[null]]","{[null] f2}"); }
	@Test public void test_1889() { checkNotSubtype("[[null]]","{[null] f1,null f2}"); }
	@Test public void test_1890() { checkNotSubtype("[[null]]","{[null] f2,null f3}"); }
	@Test public void test_1891() { checkNotSubtype("[[null]]","{[int] f1}"); }
	@Test public void test_1892() { checkNotSubtype("[[null]]","{[int] f2}"); }
	@Test public void test_1893() { checkNotSubtype("[[null]]","{[int] f1,int f2}"); }
	@Test public void test_1894() { checkNotSubtype("[[null]]","{[int] f2,int f3}"); }
	@Test public void test_1895() { checkIsSubtype("[[null]]","{{void f1} f1}"); }
	@Test public void test_1896() { checkIsSubtype("[[null]]","{{void f2} f1}"); }
	@Test public void test_1897() { checkIsSubtype("[[null]]","{{void f1} f2}"); }
	@Test public void test_1898() { checkIsSubtype("[[null]]","{{void f2} f2}"); }
	@Test public void test_1899() { checkIsSubtype("[[null]]","{{void f1} f1,void f2}"); }
	@Test public void test_1900() { checkIsSubtype("[[null]]","{{void f2} f1,void f2}"); }
	@Test public void test_1901() { checkIsSubtype("[[null]]","{{void f1} f2,void f3}"); }
	@Test public void test_1902() { checkIsSubtype("[[null]]","{{void f2} f2,void f3}"); }
	@Test public void test_1903() { checkNotSubtype("[[null]]","{{any f1} f1}"); }
	@Test public void test_1904() { checkNotSubtype("[[null]]","{{any f2} f1}"); }
	@Test public void test_1905() { checkNotSubtype("[[null]]","{{any f1} f2}"); }
	@Test public void test_1906() { checkNotSubtype("[[null]]","{{any f2} f2}"); }
	@Test public void test_1907() { checkNotSubtype("[[null]]","{{any f1} f1,any f2}"); }
	@Test public void test_1908() { checkNotSubtype("[[null]]","{{any f2} f1,any f2}"); }
	@Test public void test_1909() { checkNotSubtype("[[null]]","{{any f1} f2,any f3}"); }
	@Test public void test_1910() { checkNotSubtype("[[null]]","{{any f2} f2,any f3}"); }
	@Test public void test_1911() { checkNotSubtype("[[null]]","{{null f1} f1}"); }
	@Test public void test_1912() { checkNotSubtype("[[null]]","{{null f2} f1}"); }
	@Test public void test_1913() { checkNotSubtype("[[null]]","{{null f1} f2}"); }
	@Test public void test_1914() { checkNotSubtype("[[null]]","{{null f2} f2}"); }
	@Test public void test_1915() { checkNotSubtype("[[null]]","{{null f1} f1,null f2}"); }
	@Test public void test_1916() { checkNotSubtype("[[null]]","{{null f2} f1,null f2}"); }
	@Test public void test_1917() { checkNotSubtype("[[null]]","{{null f1} f2,null f3}"); }
	@Test public void test_1918() { checkNotSubtype("[[null]]","{{null f2} f2,null f3}"); }
	@Test public void test_1919() { checkNotSubtype("[[null]]","{{int f1} f1}"); }
	@Test public void test_1920() { checkNotSubtype("[[null]]","{{int f2} f1}"); }
	@Test public void test_1921() { checkNotSubtype("[[null]]","{{int f1} f2}"); }
	@Test public void test_1922() { checkNotSubtype("[[null]]","{{int f2} f2}"); }
	@Test public void test_1923() { checkNotSubtype("[[null]]","{{int f1} f1,int f2}"); }
	@Test public void test_1924() { checkNotSubtype("[[null]]","{{int f2} f1,int f2}"); }
	@Test public void test_1925() { checkNotSubtype("[[null]]","{{int f1} f2,int f3}"); }
	@Test public void test_1926() { checkNotSubtype("[[null]]","{{int f2} f2,int f3}"); }
	@Test public void test_1927() { checkNotSubtype("[[int]]","any"); }
	@Test public void test_1928() { checkNotSubtype("[[int]]","null"); }
	@Test public void test_1929() { checkNotSubtype("[[int]]","int"); }
	@Test public void test_1930() { checkIsSubtype("[[int]]","[void]"); }
	@Test public void test_1931() { checkNotSubtype("[[int]]","[any]"); }
	@Test public void test_1932() { checkNotSubtype("[[int]]","[null]"); }
	@Test public void test_1933() { checkNotSubtype("[[int]]","[int]"); }
	@Test public void test_1934() { checkIsSubtype("[[int]]","{void f1}"); }
	@Test public void test_1935() { checkIsSubtype("[[int]]","{void f2}"); }
	@Test public void test_1936() { checkNotSubtype("[[int]]","{any f1}"); }
	@Test public void test_1937() { checkNotSubtype("[[int]]","{any f2}"); }
	@Test public void test_1938() { checkNotSubtype("[[int]]","{null f1}"); }
	@Test public void test_1939() { checkNotSubtype("[[int]]","{null f2}"); }
	@Test public void test_1940() { checkNotSubtype("[[int]]","{int f1}"); }
	@Test public void test_1941() { checkNotSubtype("[[int]]","{int f2}"); }
	@Test public void test_1942() { checkIsSubtype("[[int]]","[[void]]"); }
	@Test public void test_1943() { checkNotSubtype("[[int]]","[[any]]"); }
	@Test public void test_1944() { checkNotSubtype("[[int]]","[[null]]"); }
	@Test public void test_1945() { checkIsSubtype("[[int]]","[[int]]"); }
	@Test public void test_1946() { checkIsSubtype("[[int]]","[{void f1}]"); }
	@Test public void test_1947() { checkIsSubtype("[[int]]","[{void f2}]"); }
	@Test public void test_1948() { checkNotSubtype("[[int]]","[{any f1}]"); }
	@Test public void test_1949() { checkNotSubtype("[[int]]","[{any f2}]"); }
	@Test public void test_1950() { checkNotSubtype("[[int]]","[{null f1}]"); }
	@Test public void test_1951() { checkNotSubtype("[[int]]","[{null f2}]"); }
	@Test public void test_1952() { checkNotSubtype("[[int]]","[{int f1}]"); }
	@Test public void test_1953() { checkNotSubtype("[[int]]","[{int f2}]"); }
	@Test public void test_1954() { checkIsSubtype("[[int]]","{void f1,void f2}"); }
	@Test public void test_1955() { checkIsSubtype("[[int]]","{void f2,void f3}"); }
	@Test public void test_1956() { checkIsSubtype("[[int]]","{void f1,any f2}"); }
	@Test public void test_1957() { checkIsSubtype("[[int]]","{void f2,any f3}"); }
	@Test public void test_1958() { checkIsSubtype("[[int]]","{void f1,null f2}"); }
	@Test public void test_1959() { checkIsSubtype("[[int]]","{void f2,null f3}"); }
	@Test public void test_1960() { checkIsSubtype("[[int]]","{void f1,int f2}"); }
	@Test public void test_1961() { checkIsSubtype("[[int]]","{void f2,int f3}"); }
	@Test public void test_1962() { checkIsSubtype("[[int]]","{any f1,void f2}"); }
	@Test public void test_1963() { checkIsSubtype("[[int]]","{any f2,void f3}"); }
	@Test public void test_1964() { checkNotSubtype("[[int]]","{any f1,any f2}"); }
	@Test public void test_1965() { checkNotSubtype("[[int]]","{any f2,any f3}"); }
	@Test public void test_1966() { checkNotSubtype("[[int]]","{any f1,null f2}"); }
	@Test public void test_1967() { checkNotSubtype("[[int]]","{any f2,null f3}"); }
	@Test public void test_1968() { checkNotSubtype("[[int]]","{any f1,int f2}"); }
	@Test public void test_1969() { checkNotSubtype("[[int]]","{any f2,int f3}"); }
	@Test public void test_1970() { checkIsSubtype("[[int]]","{null f1,void f2}"); }
	@Test public void test_1971() { checkIsSubtype("[[int]]","{null f2,void f3}"); }
	@Test public void test_1972() { checkNotSubtype("[[int]]","{null f1,any f2}"); }
	@Test public void test_1973() { checkNotSubtype("[[int]]","{null f2,any f3}"); }
	@Test public void test_1974() { checkNotSubtype("[[int]]","{null f1,null f2}"); }
	@Test public void test_1975() { checkNotSubtype("[[int]]","{null f2,null f3}"); }
	@Test public void test_1976() { checkNotSubtype("[[int]]","{null f1,int f2}"); }
	@Test public void test_1977() { checkNotSubtype("[[int]]","{null f2,int f3}"); }
	@Test public void test_1978() { checkIsSubtype("[[int]]","{int f1,void f2}"); }
	@Test public void test_1979() { checkIsSubtype("[[int]]","{int f2,void f3}"); }
	@Test public void test_1980() { checkNotSubtype("[[int]]","{int f1,any f2}"); }
	@Test public void test_1981() { checkNotSubtype("[[int]]","{int f2,any f3}"); }
	@Test public void test_1982() { checkNotSubtype("[[int]]","{int f1,null f2}"); }
	@Test public void test_1983() { checkNotSubtype("[[int]]","{int f2,null f3}"); }
	@Test public void test_1984() { checkNotSubtype("[[int]]","{int f1,int f2}"); }
	@Test public void test_1985() { checkNotSubtype("[[int]]","{int f2,int f3}"); }
	@Test public void test_1986() { checkNotSubtype("[[int]]","{[void] f1}"); }
	@Test public void test_1987() { checkNotSubtype("[[int]]","{[void] f2}"); }
	@Test public void test_1988() { checkIsSubtype("[[int]]","{[void] f1,void f2}"); }
	@Test public void test_1989() { checkIsSubtype("[[int]]","{[void] f2,void f3}"); }
	@Test public void test_1990() { checkNotSubtype("[[int]]","{[any] f1}"); }
	@Test public void test_1991() { checkNotSubtype("[[int]]","{[any] f2}"); }
	@Test public void test_1992() { checkNotSubtype("[[int]]","{[any] f1,any f2}"); }
	@Test public void test_1993() { checkNotSubtype("[[int]]","{[any] f2,any f3}"); }
	@Test public void test_1994() { checkNotSubtype("[[int]]","{[null] f1}"); }
	@Test public void test_1995() { checkNotSubtype("[[int]]","{[null] f2}"); }
	@Test public void test_1996() { checkNotSubtype("[[int]]","{[null] f1,null f2}"); }
	@Test public void test_1997() { checkNotSubtype("[[int]]","{[null] f2,null f3}"); }
	@Test public void test_1998() { checkNotSubtype("[[int]]","{[int] f1}"); }
	@Test public void test_1999() { checkNotSubtype("[[int]]","{[int] f2}"); }
	@Test public void test_2000() { checkNotSubtype("[[int]]","{[int] f1,int f2}"); }
	@Test public void test_2001() { checkNotSubtype("[[int]]","{[int] f2,int f3}"); }
	@Test public void test_2002() { checkIsSubtype("[[int]]","{{void f1} f1}"); }
	@Test public void test_2003() { checkIsSubtype("[[int]]","{{void f2} f1}"); }
	@Test public void test_2004() { checkIsSubtype("[[int]]","{{void f1} f2}"); }
	@Test public void test_2005() { checkIsSubtype("[[int]]","{{void f2} f2}"); }
	@Test public void test_2006() { checkIsSubtype("[[int]]","{{void f1} f1,void f2}"); }
	@Test public void test_2007() { checkIsSubtype("[[int]]","{{void f2} f1,void f2}"); }
	@Test public void test_2008() { checkIsSubtype("[[int]]","{{void f1} f2,void f3}"); }
	@Test public void test_2009() { checkIsSubtype("[[int]]","{{void f2} f2,void f3}"); }
	@Test public void test_2010() { checkNotSubtype("[[int]]","{{any f1} f1}"); }
	@Test public void test_2011() { checkNotSubtype("[[int]]","{{any f2} f1}"); }
	@Test public void test_2012() { checkNotSubtype("[[int]]","{{any f1} f2}"); }
	@Test public void test_2013() { checkNotSubtype("[[int]]","{{any f2} f2}"); }
	@Test public void test_2014() { checkNotSubtype("[[int]]","{{any f1} f1,any f2}"); }
	@Test public void test_2015() { checkNotSubtype("[[int]]","{{any f2} f1,any f2}"); }
	@Test public void test_2016() { checkNotSubtype("[[int]]","{{any f1} f2,any f3}"); }
	@Test public void test_2017() { checkNotSubtype("[[int]]","{{any f2} f2,any f3}"); }
	@Test public void test_2018() { checkNotSubtype("[[int]]","{{null f1} f1}"); }
	@Test public void test_2019() { checkNotSubtype("[[int]]","{{null f2} f1}"); }
	@Test public void test_2020() { checkNotSubtype("[[int]]","{{null f1} f2}"); }
	@Test public void test_2021() { checkNotSubtype("[[int]]","{{null f2} f2}"); }
	@Test public void test_2022() { checkNotSubtype("[[int]]","{{null f1} f1,null f2}"); }
	@Test public void test_2023() { checkNotSubtype("[[int]]","{{null f2} f1,null f2}"); }
	@Test public void test_2024() { checkNotSubtype("[[int]]","{{null f1} f2,null f3}"); }
	@Test public void test_2025() { checkNotSubtype("[[int]]","{{null f2} f2,null f3}"); }
	@Test public void test_2026() { checkNotSubtype("[[int]]","{{int f1} f1}"); }
	@Test public void test_2027() { checkNotSubtype("[[int]]","{{int f2} f1}"); }
	@Test public void test_2028() { checkNotSubtype("[[int]]","{{int f1} f2}"); }
	@Test public void test_2029() { checkNotSubtype("[[int]]","{{int f2} f2}"); }
	@Test public void test_2030() { checkNotSubtype("[[int]]","{{int f1} f1,int f2}"); }
	@Test public void test_2031() { checkNotSubtype("[[int]]","{{int f2} f1,int f2}"); }
	@Test public void test_2032() { checkNotSubtype("[[int]]","{{int f1} f2,int f3}"); }
	@Test public void test_2033() { checkNotSubtype("[[int]]","{{int f2} f2,int f3}"); }
	@Test public void test_2034() { checkNotSubtype("[{void f1}]","any"); }
	@Test public void test_2035() { checkNotSubtype("[{void f1}]","null"); }
	@Test public void test_2036() { checkNotSubtype("[{void f1}]","int"); }
	@Test public void test_2037() { checkIsSubtype("[{void f1}]","[void]"); }
	@Test public void test_2038() { checkNotSubtype("[{void f1}]","[any]"); }
	@Test public void test_2039() { checkNotSubtype("[{void f1}]","[null]"); }
	@Test public void test_2040() { checkNotSubtype("[{void f1}]","[int]"); }
	@Test public void test_2041() { checkIsSubtype("[{void f1}]","{void f1}"); }
	@Test public void test_2042() { checkIsSubtype("[{void f1}]","{void f2}"); }
	@Test public void test_2043() { checkNotSubtype("[{void f1}]","{any f1}"); }
	@Test public void test_2044() { checkNotSubtype("[{void f1}]","{any f2}"); }
	@Test public void test_2045() { checkNotSubtype("[{void f1}]","{null f1}"); }
	@Test public void test_2046() { checkNotSubtype("[{void f1}]","{null f2}"); }
	@Test public void test_2047() { checkNotSubtype("[{void f1}]","{int f1}"); }
	@Test public void test_2048() { checkNotSubtype("[{void f1}]","{int f2}"); }
	@Test public void test_2049() { checkNotSubtype("[{void f1}]","[[void]]"); }
	@Test public void test_2050() { checkNotSubtype("[{void f1}]","[[any]]"); }
	@Test public void test_2051() { checkNotSubtype("[{void f1}]","[[null]]"); }
	@Test public void test_2052() { checkNotSubtype("[{void f1}]","[[int]]"); }
	@Test public void test_2053() { checkIsSubtype("[{void f1}]","[{void f1}]"); }
	@Test public void test_2054() { checkIsSubtype("[{void f1}]","[{void f2}]"); }
	@Test public void test_2055() { checkNotSubtype("[{void f1}]","[{any f1}]"); }
	@Test public void test_2056() { checkNotSubtype("[{void f1}]","[{any f2}]"); }
	@Test public void test_2057() { checkNotSubtype("[{void f1}]","[{null f1}]"); }
	@Test public void test_2058() { checkNotSubtype("[{void f1}]","[{null f2}]"); }
	@Test public void test_2059() { checkNotSubtype("[{void f1}]","[{int f1}]"); }
	@Test public void test_2060() { checkNotSubtype("[{void f1}]","[{int f2}]"); }
	@Test public void test_2061() { checkIsSubtype("[{void f1}]","{void f1,void f2}"); }
	@Test public void test_2062() { checkIsSubtype("[{void f1}]","{void f2,void f3}"); }
	@Test public void test_2063() { checkIsSubtype("[{void f1}]","{void f1,any f2}"); }
	@Test public void test_2064() { checkIsSubtype("[{void f1}]","{void f2,any f3}"); }
	@Test public void test_2065() { checkIsSubtype("[{void f1}]","{void f1,null f2}"); }
	@Test public void test_2066() { checkIsSubtype("[{void f1}]","{void f2,null f3}"); }
	@Test public void test_2067() { checkIsSubtype("[{void f1}]","{void f1,int f2}"); }
	@Test public void test_2068() { checkIsSubtype("[{void f1}]","{void f2,int f3}"); }
	@Test public void test_2069() { checkIsSubtype("[{void f1}]","{any f1,void f2}"); }
	@Test public void test_2070() { checkIsSubtype("[{void f1}]","{any f2,void f3}"); }
	@Test public void test_2071() { checkNotSubtype("[{void f1}]","{any f1,any f2}"); }
	@Test public void test_2072() { checkNotSubtype("[{void f1}]","{any f2,any f3}"); }
	@Test public void test_2073() { checkNotSubtype("[{void f1}]","{any f1,null f2}"); }
	@Test public void test_2074() { checkNotSubtype("[{void f1}]","{any f2,null f3}"); }
	@Test public void test_2075() { checkNotSubtype("[{void f1}]","{any f1,int f2}"); }
	@Test public void test_2076() { checkNotSubtype("[{void f1}]","{any f2,int f3}"); }
	@Test public void test_2077() { checkIsSubtype("[{void f1}]","{null f1,void f2}"); }
	@Test public void test_2078() { checkIsSubtype("[{void f1}]","{null f2,void f3}"); }
	@Test public void test_2079() { checkNotSubtype("[{void f1}]","{null f1,any f2}"); }
	@Test public void test_2080() { checkNotSubtype("[{void f1}]","{null f2,any f3}"); }
	@Test public void test_2081() { checkNotSubtype("[{void f1}]","{null f1,null f2}"); }
	@Test public void test_2082() { checkNotSubtype("[{void f1}]","{null f2,null f3}"); }
	@Test public void test_2083() { checkNotSubtype("[{void f1}]","{null f1,int f2}"); }
	@Test public void test_2084() { checkNotSubtype("[{void f1}]","{null f2,int f3}"); }
	@Test public void test_2085() { checkIsSubtype("[{void f1}]","{int f1,void f2}"); }
	@Test public void test_2086() { checkIsSubtype("[{void f1}]","{int f2,void f3}"); }
	@Test public void test_2087() { checkNotSubtype("[{void f1}]","{int f1,any f2}"); }
	@Test public void test_2088() { checkNotSubtype("[{void f1}]","{int f2,any f3}"); }
	@Test public void test_2089() { checkNotSubtype("[{void f1}]","{int f1,null f2}"); }
	@Test public void test_2090() { checkNotSubtype("[{void f1}]","{int f2,null f3}"); }
	@Test public void test_2091() { checkNotSubtype("[{void f1}]","{int f1,int f2}"); }
	@Test public void test_2092() { checkNotSubtype("[{void f1}]","{int f2,int f3}"); }
	@Test public void test_2093() { checkNotSubtype("[{void f1}]","{[void] f1}"); }
	@Test public void test_2094() { checkNotSubtype("[{void f1}]","{[void] f2}"); }
	@Test public void test_2095() { checkIsSubtype("[{void f1}]","{[void] f1,void f2}"); }
	@Test public void test_2096() { checkIsSubtype("[{void f1}]","{[void] f2,void f3}"); }
	@Test public void test_2097() { checkNotSubtype("[{void f1}]","{[any] f1}"); }
	@Test public void test_2098() { checkNotSubtype("[{void f1}]","{[any] f2}"); }
	@Test public void test_2099() { checkNotSubtype("[{void f1}]","{[any] f1,any f2}"); }
	@Test public void test_2100() { checkNotSubtype("[{void f1}]","{[any] f2,any f3}"); }
	@Test public void test_2101() { checkNotSubtype("[{void f1}]","{[null] f1}"); }
	@Test public void test_2102() { checkNotSubtype("[{void f1}]","{[null] f2}"); }
	@Test public void test_2103() { checkNotSubtype("[{void f1}]","{[null] f1,null f2}"); }
	@Test public void test_2104() { checkNotSubtype("[{void f1}]","{[null] f2,null f3}"); }
	@Test public void test_2105() { checkNotSubtype("[{void f1}]","{[int] f1}"); }
	@Test public void test_2106() { checkNotSubtype("[{void f1}]","{[int] f2}"); }
	@Test public void test_2107() { checkNotSubtype("[{void f1}]","{[int] f1,int f2}"); }
	@Test public void test_2108() { checkNotSubtype("[{void f1}]","{[int] f2,int f3}"); }
	@Test public void test_2109() { checkIsSubtype("[{void f1}]","{{void f1} f1}"); }
	@Test public void test_2110() { checkIsSubtype("[{void f1}]","{{void f2} f1}"); }
	@Test public void test_2111() { checkIsSubtype("[{void f1}]","{{void f1} f2}"); }
	@Test public void test_2112() { checkIsSubtype("[{void f1}]","{{void f2} f2}"); }
	@Test public void test_2113() { checkIsSubtype("[{void f1}]","{{void f1} f1,void f2}"); }
	@Test public void test_2114() { checkIsSubtype("[{void f1}]","{{void f2} f1,void f2}"); }
	@Test public void test_2115() { checkIsSubtype("[{void f1}]","{{void f1} f2,void f3}"); }
	@Test public void test_2116() { checkIsSubtype("[{void f1}]","{{void f2} f2,void f3}"); }
	@Test public void test_2117() { checkNotSubtype("[{void f1}]","{{any f1} f1}"); }
	@Test public void test_2118() { checkNotSubtype("[{void f1}]","{{any f2} f1}"); }
	@Test public void test_2119() { checkNotSubtype("[{void f1}]","{{any f1} f2}"); }
	@Test public void test_2120() { checkNotSubtype("[{void f1}]","{{any f2} f2}"); }
	@Test public void test_2121() { checkNotSubtype("[{void f1}]","{{any f1} f1,any f2}"); }
	@Test public void test_2122() { checkNotSubtype("[{void f1}]","{{any f2} f1,any f2}"); }
	@Test public void test_2123() { checkNotSubtype("[{void f1}]","{{any f1} f2,any f3}"); }
	@Test public void test_2124() { checkNotSubtype("[{void f1}]","{{any f2} f2,any f3}"); }
	@Test public void test_2125() { checkNotSubtype("[{void f1}]","{{null f1} f1}"); }
	@Test public void test_2126() { checkNotSubtype("[{void f1}]","{{null f2} f1}"); }
	@Test public void test_2127() { checkNotSubtype("[{void f1}]","{{null f1} f2}"); }
	@Test public void test_2128() { checkNotSubtype("[{void f1}]","{{null f2} f2}"); }
	@Test public void test_2129() { checkNotSubtype("[{void f1}]","{{null f1} f1,null f2}"); }
	@Test public void test_2130() { checkNotSubtype("[{void f1}]","{{null f2} f1,null f2}"); }
	@Test public void test_2131() { checkNotSubtype("[{void f1}]","{{null f1} f2,null f3}"); }
	@Test public void test_2132() { checkNotSubtype("[{void f1}]","{{null f2} f2,null f3}"); }
	@Test public void test_2133() { checkNotSubtype("[{void f1}]","{{int f1} f1}"); }
	@Test public void test_2134() { checkNotSubtype("[{void f1}]","{{int f2} f1}"); }
	@Test public void test_2135() { checkNotSubtype("[{void f1}]","{{int f1} f2}"); }
	@Test public void test_2136() { checkNotSubtype("[{void f1}]","{{int f2} f2}"); }
	@Test public void test_2137() { checkNotSubtype("[{void f1}]","{{int f1} f1,int f2}"); }
	@Test public void test_2138() { checkNotSubtype("[{void f1}]","{{int f2} f1,int f2}"); }
	@Test public void test_2139() { checkNotSubtype("[{void f1}]","{{int f1} f2,int f3}"); }
	@Test public void test_2140() { checkNotSubtype("[{void f1}]","{{int f2} f2,int f3}"); }
	@Test public void test_2141() { checkNotSubtype("[{void f2}]","any"); }
	@Test public void test_2142() { checkNotSubtype("[{void f2}]","null"); }
	@Test public void test_2143() { checkNotSubtype("[{void f2}]","int"); }
	@Test public void test_2144() { checkIsSubtype("[{void f2}]","[void]"); }
	@Test public void test_2145() { checkNotSubtype("[{void f2}]","[any]"); }
	@Test public void test_2146() { checkNotSubtype("[{void f2}]","[null]"); }
	@Test public void test_2147() { checkNotSubtype("[{void f2}]","[int]"); }
	@Test public void test_2148() { checkIsSubtype("[{void f2}]","{void f1}"); }
	@Test public void test_2149() { checkIsSubtype("[{void f2}]","{void f2}"); }
	@Test public void test_2150() { checkNotSubtype("[{void f2}]","{any f1}"); }
	@Test public void test_2151() { checkNotSubtype("[{void f2}]","{any f2}"); }
	@Test public void test_2152() { checkNotSubtype("[{void f2}]","{null f1}"); }
	@Test public void test_2153() { checkNotSubtype("[{void f2}]","{null f2}"); }
	@Test public void test_2154() { checkNotSubtype("[{void f2}]","{int f1}"); }
	@Test public void test_2155() { checkNotSubtype("[{void f2}]","{int f2}"); }
	@Test public void test_2156() { checkNotSubtype("[{void f2}]","[[void]]"); }
	@Test public void test_2157() { checkNotSubtype("[{void f2}]","[[any]]"); }
	@Test public void test_2158() { checkNotSubtype("[{void f2}]","[[null]]"); }
	@Test public void test_2159() { checkNotSubtype("[{void f2}]","[[int]]"); }
	@Test public void test_2160() { checkIsSubtype("[{void f2}]","[{void f1}]"); }
	@Test public void test_2161() { checkIsSubtype("[{void f2}]","[{void f2}]"); }
	@Test public void test_2162() { checkNotSubtype("[{void f2}]","[{any f1}]"); }
	@Test public void test_2163() { checkNotSubtype("[{void f2}]","[{any f2}]"); }
	@Test public void test_2164() { checkNotSubtype("[{void f2}]","[{null f1}]"); }
	@Test public void test_2165() { checkNotSubtype("[{void f2}]","[{null f2}]"); }
	@Test public void test_2166() { checkNotSubtype("[{void f2}]","[{int f1}]"); }
	@Test public void test_2167() { checkNotSubtype("[{void f2}]","[{int f2}]"); }
	@Test public void test_2168() { checkIsSubtype("[{void f2}]","{void f1,void f2}"); }
	@Test public void test_2169() { checkIsSubtype("[{void f2}]","{void f2,void f3}"); }
	@Test public void test_2170() { checkIsSubtype("[{void f2}]","{void f1,any f2}"); }
	@Test public void test_2171() { checkIsSubtype("[{void f2}]","{void f2,any f3}"); }
	@Test public void test_2172() { checkIsSubtype("[{void f2}]","{void f1,null f2}"); }
	@Test public void test_2173() { checkIsSubtype("[{void f2}]","{void f2,null f3}"); }
	@Test public void test_2174() { checkIsSubtype("[{void f2}]","{void f1,int f2}"); }
	@Test public void test_2175() { checkIsSubtype("[{void f2}]","{void f2,int f3}"); }
	@Test public void test_2176() { checkIsSubtype("[{void f2}]","{any f1,void f2}"); }
	@Test public void test_2177() { checkIsSubtype("[{void f2}]","{any f2,void f3}"); }
	@Test public void test_2178() { checkNotSubtype("[{void f2}]","{any f1,any f2}"); }
	@Test public void test_2179() { checkNotSubtype("[{void f2}]","{any f2,any f3}"); }
	@Test public void test_2180() { checkNotSubtype("[{void f2}]","{any f1,null f2}"); }
	@Test public void test_2181() { checkNotSubtype("[{void f2}]","{any f2,null f3}"); }
	@Test public void test_2182() { checkNotSubtype("[{void f2}]","{any f1,int f2}"); }
	@Test public void test_2183() { checkNotSubtype("[{void f2}]","{any f2,int f3}"); }
	@Test public void test_2184() { checkIsSubtype("[{void f2}]","{null f1,void f2}"); }
	@Test public void test_2185() { checkIsSubtype("[{void f2}]","{null f2,void f3}"); }
	@Test public void test_2186() { checkNotSubtype("[{void f2}]","{null f1,any f2}"); }
	@Test public void test_2187() { checkNotSubtype("[{void f2}]","{null f2,any f3}"); }
	@Test public void test_2188() { checkNotSubtype("[{void f2}]","{null f1,null f2}"); }
	@Test public void test_2189() { checkNotSubtype("[{void f2}]","{null f2,null f3}"); }
	@Test public void test_2190() { checkNotSubtype("[{void f2}]","{null f1,int f2}"); }
	@Test public void test_2191() { checkNotSubtype("[{void f2}]","{null f2,int f3}"); }
	@Test public void test_2192() { checkIsSubtype("[{void f2}]","{int f1,void f2}"); }
	@Test public void test_2193() { checkIsSubtype("[{void f2}]","{int f2,void f3}"); }
	@Test public void test_2194() { checkNotSubtype("[{void f2}]","{int f1,any f2}"); }
	@Test public void test_2195() { checkNotSubtype("[{void f2}]","{int f2,any f3}"); }
	@Test public void test_2196() { checkNotSubtype("[{void f2}]","{int f1,null f2}"); }
	@Test public void test_2197() { checkNotSubtype("[{void f2}]","{int f2,null f3}"); }
	@Test public void test_2198() { checkNotSubtype("[{void f2}]","{int f1,int f2}"); }
	@Test public void test_2199() { checkNotSubtype("[{void f2}]","{int f2,int f3}"); }
	@Test public void test_2200() { checkNotSubtype("[{void f2}]","{[void] f1}"); }
	@Test public void test_2201() { checkNotSubtype("[{void f2}]","{[void] f2}"); }
	@Test public void test_2202() { checkIsSubtype("[{void f2}]","{[void] f1,void f2}"); }
	@Test public void test_2203() { checkIsSubtype("[{void f2}]","{[void] f2,void f3}"); }
	@Test public void test_2204() { checkNotSubtype("[{void f2}]","{[any] f1}"); }
	@Test public void test_2205() { checkNotSubtype("[{void f2}]","{[any] f2}"); }
	@Test public void test_2206() { checkNotSubtype("[{void f2}]","{[any] f1,any f2}"); }
	@Test public void test_2207() { checkNotSubtype("[{void f2}]","{[any] f2,any f3}"); }
	@Test public void test_2208() { checkNotSubtype("[{void f2}]","{[null] f1}"); }
	@Test public void test_2209() { checkNotSubtype("[{void f2}]","{[null] f2}"); }
	@Test public void test_2210() { checkNotSubtype("[{void f2}]","{[null] f1,null f2}"); }
	@Test public void test_2211() { checkNotSubtype("[{void f2}]","{[null] f2,null f3}"); }
	@Test public void test_2212() { checkNotSubtype("[{void f2}]","{[int] f1}"); }
	@Test public void test_2213() { checkNotSubtype("[{void f2}]","{[int] f2}"); }
	@Test public void test_2214() { checkNotSubtype("[{void f2}]","{[int] f1,int f2}"); }
	@Test public void test_2215() { checkNotSubtype("[{void f2}]","{[int] f2,int f3}"); }
	@Test public void test_2216() { checkIsSubtype("[{void f2}]","{{void f1} f1}"); }
	@Test public void test_2217() { checkIsSubtype("[{void f2}]","{{void f2} f1}"); }
	@Test public void test_2218() { checkIsSubtype("[{void f2}]","{{void f1} f2}"); }
	@Test public void test_2219() { checkIsSubtype("[{void f2}]","{{void f2} f2}"); }
	@Test public void test_2220() { checkIsSubtype("[{void f2}]","{{void f1} f1,void f2}"); }
	@Test public void test_2221() { checkIsSubtype("[{void f2}]","{{void f2} f1,void f2}"); }
	@Test public void test_2222() { checkIsSubtype("[{void f2}]","{{void f1} f2,void f3}"); }
	@Test public void test_2223() { checkIsSubtype("[{void f2}]","{{void f2} f2,void f3}"); }
	@Test public void test_2224() { checkNotSubtype("[{void f2}]","{{any f1} f1}"); }
	@Test public void test_2225() { checkNotSubtype("[{void f2}]","{{any f2} f1}"); }
	@Test public void test_2226() { checkNotSubtype("[{void f2}]","{{any f1} f2}"); }
	@Test public void test_2227() { checkNotSubtype("[{void f2}]","{{any f2} f2}"); }
	@Test public void test_2228() { checkNotSubtype("[{void f2}]","{{any f1} f1,any f2}"); }
	@Test public void test_2229() { checkNotSubtype("[{void f2}]","{{any f2} f1,any f2}"); }
	@Test public void test_2230() { checkNotSubtype("[{void f2}]","{{any f1} f2,any f3}"); }
	@Test public void test_2231() { checkNotSubtype("[{void f2}]","{{any f2} f2,any f3}"); }
	@Test public void test_2232() { checkNotSubtype("[{void f2}]","{{null f1} f1}"); }
	@Test public void test_2233() { checkNotSubtype("[{void f2}]","{{null f2} f1}"); }
	@Test public void test_2234() { checkNotSubtype("[{void f2}]","{{null f1} f2}"); }
	@Test public void test_2235() { checkNotSubtype("[{void f2}]","{{null f2} f2}"); }
	@Test public void test_2236() { checkNotSubtype("[{void f2}]","{{null f1} f1,null f2}"); }
	@Test public void test_2237() { checkNotSubtype("[{void f2}]","{{null f2} f1,null f2}"); }
	@Test public void test_2238() { checkNotSubtype("[{void f2}]","{{null f1} f2,null f3}"); }
	@Test public void test_2239() { checkNotSubtype("[{void f2}]","{{null f2} f2,null f3}"); }
	@Test public void test_2240() { checkNotSubtype("[{void f2}]","{{int f1} f1}"); }
	@Test public void test_2241() { checkNotSubtype("[{void f2}]","{{int f2} f1}"); }
	@Test public void test_2242() { checkNotSubtype("[{void f2}]","{{int f1} f2}"); }
	@Test public void test_2243() { checkNotSubtype("[{void f2}]","{{int f2} f2}"); }
	@Test public void test_2244() { checkNotSubtype("[{void f2}]","{{int f1} f1,int f2}"); }
	@Test public void test_2245() { checkNotSubtype("[{void f2}]","{{int f2} f1,int f2}"); }
	@Test public void test_2246() { checkNotSubtype("[{void f2}]","{{int f1} f2,int f3}"); }
	@Test public void test_2247() { checkNotSubtype("[{void f2}]","{{int f2} f2,int f3}"); }
	@Test public void test_2248() { checkNotSubtype("[{any f1}]","any"); }
	@Test public void test_2249() { checkNotSubtype("[{any f1}]","null"); }
	@Test public void test_2250() { checkNotSubtype("[{any f1}]","int"); }
	@Test public void test_2251() { checkIsSubtype("[{any f1}]","[void]"); }
	@Test public void test_2252() { checkNotSubtype("[{any f1}]","[any]"); }
	@Test public void test_2253() { checkNotSubtype("[{any f1}]","[null]"); }
	@Test public void test_2254() { checkNotSubtype("[{any f1}]","[int]"); }
	@Test public void test_2255() { checkIsSubtype("[{any f1}]","{void f1}"); }
	@Test public void test_2256() { checkIsSubtype("[{any f1}]","{void f2}"); }
	@Test public void test_2257() { checkNotSubtype("[{any f1}]","{any f1}"); }
	@Test public void test_2258() { checkNotSubtype("[{any f1}]","{any f2}"); }
	@Test public void test_2259() { checkNotSubtype("[{any f1}]","{null f1}"); }
	@Test public void test_2260() { checkNotSubtype("[{any f1}]","{null f2}"); }
	@Test public void test_2261() { checkNotSubtype("[{any f1}]","{int f1}"); }
	@Test public void test_2262() { checkNotSubtype("[{any f1}]","{int f2}"); }
	@Test public void test_2263() { checkNotSubtype("[{any f1}]","[[void]]"); }
	@Test public void test_2264() { checkNotSubtype("[{any f1}]","[[any]]"); }
	@Test public void test_2265() { checkNotSubtype("[{any f1}]","[[null]]"); }
	@Test public void test_2266() { checkNotSubtype("[{any f1}]","[[int]]"); }
	@Test public void test_2267() { checkIsSubtype("[{any f1}]","[{void f1}]"); }
	@Test public void test_2268() { checkIsSubtype("[{any f1}]","[{void f2}]"); }
	@Test public void test_2269() { checkIsSubtype("[{any f1}]","[{any f1}]"); }
	@Test public void test_2270() { checkNotSubtype("[{any f1}]","[{any f2}]"); }
	@Test public void test_2271() { checkIsSubtype("[{any f1}]","[{null f1}]"); }
	@Test public void test_2272() { checkNotSubtype("[{any f1}]","[{null f2}]"); }
	@Test public void test_2273() { checkIsSubtype("[{any f1}]","[{int f1}]"); }
	@Test public void test_2274() { checkNotSubtype("[{any f1}]","[{int f2}]"); }
	@Test public void test_2275() { checkIsSubtype("[{any f1}]","{void f1,void f2}"); }
	@Test public void test_2276() { checkIsSubtype("[{any f1}]","{void f2,void f3}"); }
	@Test public void test_2277() { checkIsSubtype("[{any f1}]","{void f1,any f2}"); }
	@Test public void test_2278() { checkIsSubtype("[{any f1}]","{void f2,any f3}"); }
	@Test public void test_2279() { checkIsSubtype("[{any f1}]","{void f1,null f2}"); }
	@Test public void test_2280() { checkIsSubtype("[{any f1}]","{void f2,null f3}"); }
	@Test public void test_2281() { checkIsSubtype("[{any f1}]","{void f1,int f2}"); }
	@Test public void test_2282() { checkIsSubtype("[{any f1}]","{void f2,int f3}"); }
	@Test public void test_2283() { checkIsSubtype("[{any f1}]","{any f1,void f2}"); }
	@Test public void test_2284() { checkIsSubtype("[{any f1}]","{any f2,void f3}"); }
	@Test public void test_2285() { checkNotSubtype("[{any f1}]","{any f1,any f2}"); }
	@Test public void test_2286() { checkNotSubtype("[{any f1}]","{any f2,any f3}"); }
	@Test public void test_2287() { checkNotSubtype("[{any f1}]","{any f1,null f2}"); }
	@Test public void test_2288() { checkNotSubtype("[{any f1}]","{any f2,null f3}"); }
	@Test public void test_2289() { checkNotSubtype("[{any f1}]","{any f1,int f2}"); }
	@Test public void test_2290() { checkNotSubtype("[{any f1}]","{any f2,int f3}"); }
	@Test public void test_2291() { checkIsSubtype("[{any f1}]","{null f1,void f2}"); }
	@Test public void test_2292() { checkIsSubtype("[{any f1}]","{null f2,void f3}"); }
	@Test public void test_2293() { checkNotSubtype("[{any f1}]","{null f1,any f2}"); }
	@Test public void test_2294() { checkNotSubtype("[{any f1}]","{null f2,any f3}"); }
	@Test public void test_2295() { checkNotSubtype("[{any f1}]","{null f1,null f2}"); }
	@Test public void test_2296() { checkNotSubtype("[{any f1}]","{null f2,null f3}"); }
	@Test public void test_2297() { checkNotSubtype("[{any f1}]","{null f1,int f2}"); }
	@Test public void test_2298() { checkNotSubtype("[{any f1}]","{null f2,int f3}"); }
	@Test public void test_2299() { checkIsSubtype("[{any f1}]","{int f1,void f2}"); }
	@Test public void test_2300() { checkIsSubtype("[{any f1}]","{int f2,void f3}"); }
	@Test public void test_2301() { checkNotSubtype("[{any f1}]","{int f1,any f2}"); }
	@Test public void test_2302() { checkNotSubtype("[{any f1}]","{int f2,any f3}"); }
	@Test public void test_2303() { checkNotSubtype("[{any f1}]","{int f1,null f2}"); }
	@Test public void test_2304() { checkNotSubtype("[{any f1}]","{int f2,null f3}"); }
	@Test public void test_2305() { checkNotSubtype("[{any f1}]","{int f1,int f2}"); }
	@Test public void test_2306() { checkNotSubtype("[{any f1}]","{int f2,int f3}"); }
	@Test public void test_2307() { checkNotSubtype("[{any f1}]","{[void] f1}"); }
	@Test public void test_2308() { checkNotSubtype("[{any f1}]","{[void] f2}"); }
	@Test public void test_2309() { checkIsSubtype("[{any f1}]","{[void] f1,void f2}"); }
	@Test public void test_2310() { checkIsSubtype("[{any f1}]","{[void] f2,void f3}"); }
	@Test public void test_2311() { checkNotSubtype("[{any f1}]","{[any] f1}"); }
	@Test public void test_2312() { checkNotSubtype("[{any f1}]","{[any] f2}"); }
	@Test public void test_2313() { checkNotSubtype("[{any f1}]","{[any] f1,any f2}"); }
	@Test public void test_2314() { checkNotSubtype("[{any f1}]","{[any] f2,any f3}"); }
	@Test public void test_2315() { checkNotSubtype("[{any f1}]","{[null] f1}"); }
	@Test public void test_2316() { checkNotSubtype("[{any f1}]","{[null] f2}"); }
	@Test public void test_2317() { checkNotSubtype("[{any f1}]","{[null] f1,null f2}"); }
	@Test public void test_2318() { checkNotSubtype("[{any f1}]","{[null] f2,null f3}"); }
	@Test public void test_2319() { checkNotSubtype("[{any f1}]","{[int] f1}"); }
	@Test public void test_2320() { checkNotSubtype("[{any f1}]","{[int] f2}"); }
	@Test public void test_2321() { checkNotSubtype("[{any f1}]","{[int] f1,int f2}"); }
	@Test public void test_2322() { checkNotSubtype("[{any f1}]","{[int] f2,int f3}"); }
	@Test public void test_2323() { checkIsSubtype("[{any f1}]","{{void f1} f1}"); }
	@Test public void test_2324() { checkIsSubtype("[{any f1}]","{{void f2} f1}"); }
	@Test public void test_2325() { checkIsSubtype("[{any f1}]","{{void f1} f2}"); }
	@Test public void test_2326() { checkIsSubtype("[{any f1}]","{{void f2} f2}"); }
	@Test public void test_2327() { checkIsSubtype("[{any f1}]","{{void f1} f1,void f2}"); }
	@Test public void test_2328() { checkIsSubtype("[{any f1}]","{{void f2} f1,void f2}"); }
	@Test public void test_2329() { checkIsSubtype("[{any f1}]","{{void f1} f2,void f3}"); }
	@Test public void test_2330() { checkIsSubtype("[{any f1}]","{{void f2} f2,void f3}"); }
	@Test public void test_2331() { checkNotSubtype("[{any f1}]","{{any f1} f1}"); }
	@Test public void test_2332() { checkNotSubtype("[{any f1}]","{{any f2} f1}"); }
	@Test public void test_2333() { checkNotSubtype("[{any f1}]","{{any f1} f2}"); }
	@Test public void test_2334() { checkNotSubtype("[{any f1}]","{{any f2} f2}"); }
	@Test public void test_2335() { checkNotSubtype("[{any f1}]","{{any f1} f1,any f2}"); }
	@Test public void test_2336() { checkNotSubtype("[{any f1}]","{{any f2} f1,any f2}"); }
	@Test public void test_2337() { checkNotSubtype("[{any f1}]","{{any f1} f2,any f3}"); }
	@Test public void test_2338() { checkNotSubtype("[{any f1}]","{{any f2} f2,any f3}"); }
	@Test public void test_2339() { checkNotSubtype("[{any f1}]","{{null f1} f1}"); }
	@Test public void test_2340() { checkNotSubtype("[{any f1}]","{{null f2} f1}"); }
	@Test public void test_2341() { checkNotSubtype("[{any f1}]","{{null f1} f2}"); }
	@Test public void test_2342() { checkNotSubtype("[{any f1}]","{{null f2} f2}"); }
	@Test public void test_2343() { checkNotSubtype("[{any f1}]","{{null f1} f1,null f2}"); }
	@Test public void test_2344() { checkNotSubtype("[{any f1}]","{{null f2} f1,null f2}"); }
	@Test public void test_2345() { checkNotSubtype("[{any f1}]","{{null f1} f2,null f3}"); }
	@Test public void test_2346() { checkNotSubtype("[{any f1}]","{{null f2} f2,null f3}"); }
	@Test public void test_2347() { checkNotSubtype("[{any f1}]","{{int f1} f1}"); }
	@Test public void test_2348() { checkNotSubtype("[{any f1}]","{{int f2} f1}"); }
	@Test public void test_2349() { checkNotSubtype("[{any f1}]","{{int f1} f2}"); }
	@Test public void test_2350() { checkNotSubtype("[{any f1}]","{{int f2} f2}"); }
	@Test public void test_2351() { checkNotSubtype("[{any f1}]","{{int f1} f1,int f2}"); }
	@Test public void test_2352() { checkNotSubtype("[{any f1}]","{{int f2} f1,int f2}"); }
	@Test public void test_2353() { checkNotSubtype("[{any f1}]","{{int f1} f2,int f3}"); }
	@Test public void test_2354() { checkNotSubtype("[{any f1}]","{{int f2} f2,int f3}"); }
	@Test public void test_2355() { checkNotSubtype("[{any f2}]","any"); }
	@Test public void test_2356() { checkNotSubtype("[{any f2}]","null"); }
	@Test public void test_2357() { checkNotSubtype("[{any f2}]","int"); }
	@Test public void test_2358() { checkIsSubtype("[{any f2}]","[void]"); }
	@Test public void test_2359() { checkNotSubtype("[{any f2}]","[any]"); }
	@Test public void test_2360() { checkNotSubtype("[{any f2}]","[null]"); }
	@Test public void test_2361() { checkNotSubtype("[{any f2}]","[int]"); }
	@Test public void test_2362() { checkIsSubtype("[{any f2}]","{void f1}"); }
	@Test public void test_2363() { checkIsSubtype("[{any f2}]","{void f2}"); }
	@Test public void test_2364() { checkNotSubtype("[{any f2}]","{any f1}"); }
	@Test public void test_2365() { checkNotSubtype("[{any f2}]","{any f2}"); }
	@Test public void test_2366() { checkNotSubtype("[{any f2}]","{null f1}"); }
	@Test public void test_2367() { checkNotSubtype("[{any f2}]","{null f2}"); }
	@Test public void test_2368() { checkNotSubtype("[{any f2}]","{int f1}"); }
	@Test public void test_2369() { checkNotSubtype("[{any f2}]","{int f2}"); }
	@Test public void test_2370() { checkNotSubtype("[{any f2}]","[[void]]"); }
	@Test public void test_2371() { checkNotSubtype("[{any f2}]","[[any]]"); }
	@Test public void test_2372() { checkNotSubtype("[{any f2}]","[[null]]"); }
	@Test public void test_2373() { checkNotSubtype("[{any f2}]","[[int]]"); }
	@Test public void test_2374() { checkIsSubtype("[{any f2}]","[{void f1}]"); }
	@Test public void test_2375() { checkIsSubtype("[{any f2}]","[{void f2}]"); }
	@Test public void test_2376() { checkNotSubtype("[{any f2}]","[{any f1}]"); }
	@Test public void test_2377() { checkIsSubtype("[{any f2}]","[{any f2}]"); }
	@Test public void test_2378() { checkNotSubtype("[{any f2}]","[{null f1}]"); }
	@Test public void test_2379() { checkIsSubtype("[{any f2}]","[{null f2}]"); }
	@Test public void test_2380() { checkNotSubtype("[{any f2}]","[{int f1}]"); }
	@Test public void test_2381() { checkIsSubtype("[{any f2}]","[{int f2}]"); }
	@Test public void test_2382() { checkIsSubtype("[{any f2}]","{void f1,void f2}"); }
	@Test public void test_2383() { checkIsSubtype("[{any f2}]","{void f2,void f3}"); }
	@Test public void test_2384() { checkIsSubtype("[{any f2}]","{void f1,any f2}"); }
	@Test public void test_2385() { checkIsSubtype("[{any f2}]","{void f2,any f3}"); }
	@Test public void test_2386() { checkIsSubtype("[{any f2}]","{void f1,null f2}"); }
	@Test public void test_2387() { checkIsSubtype("[{any f2}]","{void f2,null f3}"); }
	@Test public void test_2388() { checkIsSubtype("[{any f2}]","{void f1,int f2}"); }
	@Test public void test_2389() { checkIsSubtype("[{any f2}]","{void f2,int f3}"); }
	@Test public void test_2390() { checkIsSubtype("[{any f2}]","{any f1,void f2}"); }
	@Test public void test_2391() { checkIsSubtype("[{any f2}]","{any f2,void f3}"); }
	@Test public void test_2392() { checkNotSubtype("[{any f2}]","{any f1,any f2}"); }
	@Test public void test_2393() { checkNotSubtype("[{any f2}]","{any f2,any f3}"); }
	@Test public void test_2394() { checkNotSubtype("[{any f2}]","{any f1,null f2}"); }
	@Test public void test_2395() { checkNotSubtype("[{any f2}]","{any f2,null f3}"); }
	@Test public void test_2396() { checkNotSubtype("[{any f2}]","{any f1,int f2}"); }
	@Test public void test_2397() { checkNotSubtype("[{any f2}]","{any f2,int f3}"); }
	@Test public void test_2398() { checkIsSubtype("[{any f2}]","{null f1,void f2}"); }
	@Test public void test_2399() { checkIsSubtype("[{any f2}]","{null f2,void f3}"); }
	@Test public void test_2400() { checkNotSubtype("[{any f2}]","{null f1,any f2}"); }
	@Test public void test_2401() { checkNotSubtype("[{any f2}]","{null f2,any f3}"); }
	@Test public void test_2402() { checkNotSubtype("[{any f2}]","{null f1,null f2}"); }
	@Test public void test_2403() { checkNotSubtype("[{any f2}]","{null f2,null f3}"); }
	@Test public void test_2404() { checkNotSubtype("[{any f2}]","{null f1,int f2}"); }
	@Test public void test_2405() { checkNotSubtype("[{any f2}]","{null f2,int f3}"); }
	@Test public void test_2406() { checkIsSubtype("[{any f2}]","{int f1,void f2}"); }
	@Test public void test_2407() { checkIsSubtype("[{any f2}]","{int f2,void f3}"); }
	@Test public void test_2408() { checkNotSubtype("[{any f2}]","{int f1,any f2}"); }
	@Test public void test_2409() { checkNotSubtype("[{any f2}]","{int f2,any f3}"); }
	@Test public void test_2410() { checkNotSubtype("[{any f2}]","{int f1,null f2}"); }
	@Test public void test_2411() { checkNotSubtype("[{any f2}]","{int f2,null f3}"); }
	@Test public void test_2412() { checkNotSubtype("[{any f2}]","{int f1,int f2}"); }
	@Test public void test_2413() { checkNotSubtype("[{any f2}]","{int f2,int f3}"); }
	@Test public void test_2414() { checkNotSubtype("[{any f2}]","{[void] f1}"); }
	@Test public void test_2415() { checkNotSubtype("[{any f2}]","{[void] f2}"); }
	@Test public void test_2416() { checkIsSubtype("[{any f2}]","{[void] f1,void f2}"); }
	@Test public void test_2417() { checkIsSubtype("[{any f2}]","{[void] f2,void f3}"); }
	@Test public void test_2418() { checkNotSubtype("[{any f2}]","{[any] f1}"); }
	@Test public void test_2419() { checkNotSubtype("[{any f2}]","{[any] f2}"); }
	@Test public void test_2420() { checkNotSubtype("[{any f2}]","{[any] f1,any f2}"); }
	@Test public void test_2421() { checkNotSubtype("[{any f2}]","{[any] f2,any f3}"); }
	@Test public void test_2422() { checkNotSubtype("[{any f2}]","{[null] f1}"); }
	@Test public void test_2423() { checkNotSubtype("[{any f2}]","{[null] f2}"); }
	@Test public void test_2424() { checkNotSubtype("[{any f2}]","{[null] f1,null f2}"); }
	@Test public void test_2425() { checkNotSubtype("[{any f2}]","{[null] f2,null f3}"); }
	@Test public void test_2426() { checkNotSubtype("[{any f2}]","{[int] f1}"); }
	@Test public void test_2427() { checkNotSubtype("[{any f2}]","{[int] f2}"); }
	@Test public void test_2428() { checkNotSubtype("[{any f2}]","{[int] f1,int f2}"); }
	@Test public void test_2429() { checkNotSubtype("[{any f2}]","{[int] f2,int f3}"); }
	@Test public void test_2430() { checkIsSubtype("[{any f2}]","{{void f1} f1}"); }
	@Test public void test_2431() { checkIsSubtype("[{any f2}]","{{void f2} f1}"); }
	@Test public void test_2432() { checkIsSubtype("[{any f2}]","{{void f1} f2}"); }
	@Test public void test_2433() { checkIsSubtype("[{any f2}]","{{void f2} f2}"); }
	@Test public void test_2434() { checkIsSubtype("[{any f2}]","{{void f1} f1,void f2}"); }
	@Test public void test_2435() { checkIsSubtype("[{any f2}]","{{void f2} f1,void f2}"); }
	@Test public void test_2436() { checkIsSubtype("[{any f2}]","{{void f1} f2,void f3}"); }
	@Test public void test_2437() { checkIsSubtype("[{any f2}]","{{void f2} f2,void f3}"); }
	@Test public void test_2438() { checkNotSubtype("[{any f2}]","{{any f1} f1}"); }
	@Test public void test_2439() { checkNotSubtype("[{any f2}]","{{any f2} f1}"); }
	@Test public void test_2440() { checkNotSubtype("[{any f2}]","{{any f1} f2}"); }
	@Test public void test_2441() { checkNotSubtype("[{any f2}]","{{any f2} f2}"); }
	@Test public void test_2442() { checkNotSubtype("[{any f2}]","{{any f1} f1,any f2}"); }
	@Test public void test_2443() { checkNotSubtype("[{any f2}]","{{any f2} f1,any f2}"); }
	@Test public void test_2444() { checkNotSubtype("[{any f2}]","{{any f1} f2,any f3}"); }
	@Test public void test_2445() { checkNotSubtype("[{any f2}]","{{any f2} f2,any f3}"); }
	@Test public void test_2446() { checkNotSubtype("[{any f2}]","{{null f1} f1}"); }
	@Test public void test_2447() { checkNotSubtype("[{any f2}]","{{null f2} f1}"); }
	@Test public void test_2448() { checkNotSubtype("[{any f2}]","{{null f1} f2}"); }
	@Test public void test_2449() { checkNotSubtype("[{any f2}]","{{null f2} f2}"); }
	@Test public void test_2450() { checkNotSubtype("[{any f2}]","{{null f1} f1,null f2}"); }
	@Test public void test_2451() { checkNotSubtype("[{any f2}]","{{null f2} f1,null f2}"); }
	@Test public void test_2452() { checkNotSubtype("[{any f2}]","{{null f1} f2,null f3}"); }
	@Test public void test_2453() { checkNotSubtype("[{any f2}]","{{null f2} f2,null f3}"); }
	@Test public void test_2454() { checkNotSubtype("[{any f2}]","{{int f1} f1}"); }
	@Test public void test_2455() { checkNotSubtype("[{any f2}]","{{int f2} f1}"); }
	@Test public void test_2456() { checkNotSubtype("[{any f2}]","{{int f1} f2}"); }
	@Test public void test_2457() { checkNotSubtype("[{any f2}]","{{int f2} f2}"); }
	@Test public void test_2458() { checkNotSubtype("[{any f2}]","{{int f1} f1,int f2}"); }
	@Test public void test_2459() { checkNotSubtype("[{any f2}]","{{int f2} f1,int f2}"); }
	@Test public void test_2460() { checkNotSubtype("[{any f2}]","{{int f1} f2,int f3}"); }
	@Test public void test_2461() { checkNotSubtype("[{any f2}]","{{int f2} f2,int f3}"); }
	@Test public void test_2462() { checkNotSubtype("[{null f1}]","any"); }
	@Test public void test_2463() { checkNotSubtype("[{null f1}]","null"); }
	@Test public void test_2464() { checkNotSubtype("[{null f1}]","int"); }
	@Test public void test_2465() { checkIsSubtype("[{null f1}]","[void]"); }
	@Test public void test_2466() { checkNotSubtype("[{null f1}]","[any]"); }
	@Test public void test_2467() { checkNotSubtype("[{null f1}]","[null]"); }
	@Test public void test_2468() { checkNotSubtype("[{null f1}]","[int]"); }
	@Test public void test_2469() { checkIsSubtype("[{null f1}]","{void f1}"); }
	@Test public void test_2470() { checkIsSubtype("[{null f1}]","{void f2}"); }
	@Test public void test_2471() { checkNotSubtype("[{null f1}]","{any f1}"); }
	@Test public void test_2472() { checkNotSubtype("[{null f1}]","{any f2}"); }
	@Test public void test_2473() { checkNotSubtype("[{null f1}]","{null f1}"); }
	@Test public void test_2474() { checkNotSubtype("[{null f1}]","{null f2}"); }
	@Test public void test_2475() { checkNotSubtype("[{null f1}]","{int f1}"); }
	@Test public void test_2476() { checkNotSubtype("[{null f1}]","{int f2}"); }
	@Test public void test_2477() { checkNotSubtype("[{null f1}]","[[void]]"); }
	@Test public void test_2478() { checkNotSubtype("[{null f1}]","[[any]]"); }
	@Test public void test_2479() { checkNotSubtype("[{null f1}]","[[null]]"); }
	@Test public void test_2480() { checkNotSubtype("[{null f1}]","[[int]]"); }
	@Test public void test_2481() { checkIsSubtype("[{null f1}]","[{void f1}]"); }
	@Test public void test_2482() { checkIsSubtype("[{null f1}]","[{void f2}]"); }
	@Test public void test_2483() { checkNotSubtype("[{null f1}]","[{any f1}]"); }
	@Test public void test_2484() { checkNotSubtype("[{null f1}]","[{any f2}]"); }
	@Test public void test_2485() { checkIsSubtype("[{null f1}]","[{null f1}]"); }
	@Test public void test_2486() { checkNotSubtype("[{null f1}]","[{null f2}]"); }
	@Test public void test_2487() { checkNotSubtype("[{null f1}]","[{int f1}]"); }
	@Test public void test_2488() { checkNotSubtype("[{null f1}]","[{int f2}]"); }
	@Test public void test_2489() { checkIsSubtype("[{null f1}]","{void f1,void f2}"); }
	@Test public void test_2490() { checkIsSubtype("[{null f1}]","{void f2,void f3}"); }
	@Test public void test_2491() { checkIsSubtype("[{null f1}]","{void f1,any f2}"); }
	@Test public void test_2492() { checkIsSubtype("[{null f1}]","{void f2,any f3}"); }
	@Test public void test_2493() { checkIsSubtype("[{null f1}]","{void f1,null f2}"); }
	@Test public void test_2494() { checkIsSubtype("[{null f1}]","{void f2,null f3}"); }
	@Test public void test_2495() { checkIsSubtype("[{null f1}]","{void f1,int f2}"); }
	@Test public void test_2496() { checkIsSubtype("[{null f1}]","{void f2,int f3}"); }
	@Test public void test_2497() { checkIsSubtype("[{null f1}]","{any f1,void f2}"); }
	@Test public void test_2498() { checkIsSubtype("[{null f1}]","{any f2,void f3}"); }
	@Test public void test_2499() { checkNotSubtype("[{null f1}]","{any f1,any f2}"); }
	@Test public void test_2500() { checkNotSubtype("[{null f1}]","{any f2,any f3}"); }
	@Test public void test_2501() { checkNotSubtype("[{null f1}]","{any f1,null f2}"); }
	@Test public void test_2502() { checkNotSubtype("[{null f1}]","{any f2,null f3}"); }
	@Test public void test_2503() { checkNotSubtype("[{null f1}]","{any f1,int f2}"); }
	@Test public void test_2504() { checkNotSubtype("[{null f1}]","{any f2,int f3}"); }
	@Test public void test_2505() { checkIsSubtype("[{null f1}]","{null f1,void f2}"); }
	@Test public void test_2506() { checkIsSubtype("[{null f1}]","{null f2,void f3}"); }
	@Test public void test_2507() { checkNotSubtype("[{null f1}]","{null f1,any f2}"); }
	@Test public void test_2508() { checkNotSubtype("[{null f1}]","{null f2,any f3}"); }
	@Test public void test_2509() { checkNotSubtype("[{null f1}]","{null f1,null f2}"); }
	@Test public void test_2510() { checkNotSubtype("[{null f1}]","{null f2,null f3}"); }
	@Test public void test_2511() { checkNotSubtype("[{null f1}]","{null f1,int f2}"); }
	@Test public void test_2512() { checkNotSubtype("[{null f1}]","{null f2,int f3}"); }
	@Test public void test_2513() { checkIsSubtype("[{null f1}]","{int f1,void f2}"); }
	@Test public void test_2514() { checkIsSubtype("[{null f1}]","{int f2,void f3}"); }
	@Test public void test_2515() { checkNotSubtype("[{null f1}]","{int f1,any f2}"); }
	@Test public void test_2516() { checkNotSubtype("[{null f1}]","{int f2,any f3}"); }
	@Test public void test_2517() { checkNotSubtype("[{null f1}]","{int f1,null f2}"); }
	@Test public void test_2518() { checkNotSubtype("[{null f1}]","{int f2,null f3}"); }
	@Test public void test_2519() { checkNotSubtype("[{null f1}]","{int f1,int f2}"); }
	@Test public void test_2520() { checkNotSubtype("[{null f1}]","{int f2,int f3}"); }
	@Test public void test_2521() { checkNotSubtype("[{null f1}]","{[void] f1}"); }
	@Test public void test_2522() { checkNotSubtype("[{null f1}]","{[void] f2}"); }
	@Test public void test_2523() { checkIsSubtype("[{null f1}]","{[void] f1,void f2}"); }
	@Test public void test_2524() { checkIsSubtype("[{null f1}]","{[void] f2,void f3}"); }
	@Test public void test_2525() { checkNotSubtype("[{null f1}]","{[any] f1}"); }
	@Test public void test_2526() { checkNotSubtype("[{null f1}]","{[any] f2}"); }
	@Test public void test_2527() { checkNotSubtype("[{null f1}]","{[any] f1,any f2}"); }
	@Test public void test_2528() { checkNotSubtype("[{null f1}]","{[any] f2,any f3}"); }
	@Test public void test_2529() { checkNotSubtype("[{null f1}]","{[null] f1}"); }
	@Test public void test_2530() { checkNotSubtype("[{null f1}]","{[null] f2}"); }
	@Test public void test_2531() { checkNotSubtype("[{null f1}]","{[null] f1,null f2}"); }
	@Test public void test_2532() { checkNotSubtype("[{null f1}]","{[null] f2,null f3}"); }
	@Test public void test_2533() { checkNotSubtype("[{null f1}]","{[int] f1}"); }
	@Test public void test_2534() { checkNotSubtype("[{null f1}]","{[int] f2}"); }
	@Test public void test_2535() { checkNotSubtype("[{null f1}]","{[int] f1,int f2}"); }
	@Test public void test_2536() { checkNotSubtype("[{null f1}]","{[int] f2,int f3}"); }
	@Test public void test_2537() { checkIsSubtype("[{null f1}]","{{void f1} f1}"); }
	@Test public void test_2538() { checkIsSubtype("[{null f1}]","{{void f2} f1}"); }
	@Test public void test_2539() { checkIsSubtype("[{null f1}]","{{void f1} f2}"); }
	@Test public void test_2540() { checkIsSubtype("[{null f1}]","{{void f2} f2}"); }
	@Test public void test_2541() { checkIsSubtype("[{null f1}]","{{void f1} f1,void f2}"); }
	@Test public void test_2542() { checkIsSubtype("[{null f1}]","{{void f2} f1,void f2}"); }
	@Test public void test_2543() { checkIsSubtype("[{null f1}]","{{void f1} f2,void f3}"); }
	@Test public void test_2544() { checkIsSubtype("[{null f1}]","{{void f2} f2,void f3}"); }
	@Test public void test_2545() { checkNotSubtype("[{null f1}]","{{any f1} f1}"); }
	@Test public void test_2546() { checkNotSubtype("[{null f1}]","{{any f2} f1}"); }
	@Test public void test_2547() { checkNotSubtype("[{null f1}]","{{any f1} f2}"); }
	@Test public void test_2548() { checkNotSubtype("[{null f1}]","{{any f2} f2}"); }
	@Test public void test_2549() { checkNotSubtype("[{null f1}]","{{any f1} f1,any f2}"); }
	@Test public void test_2550() { checkNotSubtype("[{null f1}]","{{any f2} f1,any f2}"); }
	@Test public void test_2551() { checkNotSubtype("[{null f1}]","{{any f1} f2,any f3}"); }
	@Test public void test_2552() { checkNotSubtype("[{null f1}]","{{any f2} f2,any f3}"); }
	@Test public void test_2553() { checkNotSubtype("[{null f1}]","{{null f1} f1}"); }
	@Test public void test_2554() { checkNotSubtype("[{null f1}]","{{null f2} f1}"); }
	@Test public void test_2555() { checkNotSubtype("[{null f1}]","{{null f1} f2}"); }
	@Test public void test_2556() { checkNotSubtype("[{null f1}]","{{null f2} f2}"); }
	@Test public void test_2557() { checkNotSubtype("[{null f1}]","{{null f1} f1,null f2}"); }
	@Test public void test_2558() { checkNotSubtype("[{null f1}]","{{null f2} f1,null f2}"); }
	@Test public void test_2559() { checkNotSubtype("[{null f1}]","{{null f1} f2,null f3}"); }
	@Test public void test_2560() { checkNotSubtype("[{null f1}]","{{null f2} f2,null f3}"); }
	@Test public void test_2561() { checkNotSubtype("[{null f1}]","{{int f1} f1}"); }
	@Test public void test_2562() { checkNotSubtype("[{null f1}]","{{int f2} f1}"); }
	@Test public void test_2563() { checkNotSubtype("[{null f1}]","{{int f1} f2}"); }
	@Test public void test_2564() { checkNotSubtype("[{null f1}]","{{int f2} f2}"); }
	@Test public void test_2565() { checkNotSubtype("[{null f1}]","{{int f1} f1,int f2}"); }
	@Test public void test_2566() { checkNotSubtype("[{null f1}]","{{int f2} f1,int f2}"); }
	@Test public void test_2567() { checkNotSubtype("[{null f1}]","{{int f1} f2,int f3}"); }
	@Test public void test_2568() { checkNotSubtype("[{null f1}]","{{int f2} f2,int f3}"); }
	@Test public void test_2569() { checkNotSubtype("[{null f2}]","any"); }
	@Test public void test_2570() { checkNotSubtype("[{null f2}]","null"); }
	@Test public void test_2571() { checkNotSubtype("[{null f2}]","int"); }
	@Test public void test_2572() { checkIsSubtype("[{null f2}]","[void]"); }
	@Test public void test_2573() { checkNotSubtype("[{null f2}]","[any]"); }
	@Test public void test_2574() { checkNotSubtype("[{null f2}]","[null]"); }
	@Test public void test_2575() { checkNotSubtype("[{null f2}]","[int]"); }
	@Test public void test_2576() { checkIsSubtype("[{null f2}]","{void f1}"); }
	@Test public void test_2577() { checkIsSubtype("[{null f2}]","{void f2}"); }
	@Test public void test_2578() { checkNotSubtype("[{null f2}]","{any f1}"); }
	@Test public void test_2579() { checkNotSubtype("[{null f2}]","{any f2}"); }
	@Test public void test_2580() { checkNotSubtype("[{null f2}]","{null f1}"); }
	@Test public void test_2581() { checkNotSubtype("[{null f2}]","{null f2}"); }
	@Test public void test_2582() { checkNotSubtype("[{null f2}]","{int f1}"); }
	@Test public void test_2583() { checkNotSubtype("[{null f2}]","{int f2}"); }
	@Test public void test_2584() { checkNotSubtype("[{null f2}]","[[void]]"); }
	@Test public void test_2585() { checkNotSubtype("[{null f2}]","[[any]]"); }
	@Test public void test_2586() { checkNotSubtype("[{null f2}]","[[null]]"); }
	@Test public void test_2587() { checkNotSubtype("[{null f2}]","[[int]]"); }
	@Test public void test_2588() { checkIsSubtype("[{null f2}]","[{void f1}]"); }
	@Test public void test_2589() { checkIsSubtype("[{null f2}]","[{void f2}]"); }
	@Test public void test_2590() { checkNotSubtype("[{null f2}]","[{any f1}]"); }
	@Test public void test_2591() { checkNotSubtype("[{null f2}]","[{any f2}]"); }
	@Test public void test_2592() { checkNotSubtype("[{null f2}]","[{null f1}]"); }
	@Test public void test_2593() { checkIsSubtype("[{null f2}]","[{null f2}]"); }
	@Test public void test_2594() { checkNotSubtype("[{null f2}]","[{int f1}]"); }
	@Test public void test_2595() { checkNotSubtype("[{null f2}]","[{int f2}]"); }
	@Test public void test_2596() { checkIsSubtype("[{null f2}]","{void f1,void f2}"); }
	@Test public void test_2597() { checkIsSubtype("[{null f2}]","{void f2,void f3}"); }
	@Test public void test_2598() { checkIsSubtype("[{null f2}]","{void f1,any f2}"); }
	@Test public void test_2599() { checkIsSubtype("[{null f2}]","{void f2,any f3}"); }
	@Test public void test_2600() { checkIsSubtype("[{null f2}]","{void f1,null f2}"); }
	@Test public void test_2601() { checkIsSubtype("[{null f2}]","{void f2,null f3}"); }
	@Test public void test_2602() { checkIsSubtype("[{null f2}]","{void f1,int f2}"); }
	@Test public void test_2603() { checkIsSubtype("[{null f2}]","{void f2,int f3}"); }
	@Test public void test_2604() { checkIsSubtype("[{null f2}]","{any f1,void f2}"); }
	@Test public void test_2605() { checkIsSubtype("[{null f2}]","{any f2,void f3}"); }
	@Test public void test_2606() { checkNotSubtype("[{null f2}]","{any f1,any f2}"); }
	@Test public void test_2607() { checkNotSubtype("[{null f2}]","{any f2,any f3}"); }
	@Test public void test_2608() { checkNotSubtype("[{null f2}]","{any f1,null f2}"); }
	@Test public void test_2609() { checkNotSubtype("[{null f2}]","{any f2,null f3}"); }
	@Test public void test_2610() { checkNotSubtype("[{null f2}]","{any f1,int f2}"); }
	@Test public void test_2611() { checkNotSubtype("[{null f2}]","{any f2,int f3}"); }
	@Test public void test_2612() { checkIsSubtype("[{null f2}]","{null f1,void f2}"); }
	@Test public void test_2613() { checkIsSubtype("[{null f2}]","{null f2,void f3}"); }
	@Test public void test_2614() { checkNotSubtype("[{null f2}]","{null f1,any f2}"); }
	@Test public void test_2615() { checkNotSubtype("[{null f2}]","{null f2,any f3}"); }
	@Test public void test_2616() { checkNotSubtype("[{null f2}]","{null f1,null f2}"); }
	@Test public void test_2617() { checkNotSubtype("[{null f2}]","{null f2,null f3}"); }
	@Test public void test_2618() { checkNotSubtype("[{null f2}]","{null f1,int f2}"); }
	@Test public void test_2619() { checkNotSubtype("[{null f2}]","{null f2,int f3}"); }
	@Test public void test_2620() { checkIsSubtype("[{null f2}]","{int f1,void f2}"); }
	@Test public void test_2621() { checkIsSubtype("[{null f2}]","{int f2,void f3}"); }
	@Test public void test_2622() { checkNotSubtype("[{null f2}]","{int f1,any f2}"); }
	@Test public void test_2623() { checkNotSubtype("[{null f2}]","{int f2,any f3}"); }
	@Test public void test_2624() { checkNotSubtype("[{null f2}]","{int f1,null f2}"); }
	@Test public void test_2625() { checkNotSubtype("[{null f2}]","{int f2,null f3}"); }
	@Test public void test_2626() { checkNotSubtype("[{null f2}]","{int f1,int f2}"); }
	@Test public void test_2627() { checkNotSubtype("[{null f2}]","{int f2,int f3}"); }
	@Test public void test_2628() { checkNotSubtype("[{null f2}]","{[void] f1}"); }
	@Test public void test_2629() { checkNotSubtype("[{null f2}]","{[void] f2}"); }
	@Test public void test_2630() { checkIsSubtype("[{null f2}]","{[void] f1,void f2}"); }
	@Test public void test_2631() { checkIsSubtype("[{null f2}]","{[void] f2,void f3}"); }
	@Test public void test_2632() { checkNotSubtype("[{null f2}]","{[any] f1}"); }
	@Test public void test_2633() { checkNotSubtype("[{null f2}]","{[any] f2}"); }
	@Test public void test_2634() { checkNotSubtype("[{null f2}]","{[any] f1,any f2}"); }
	@Test public void test_2635() { checkNotSubtype("[{null f2}]","{[any] f2,any f3}"); }
	@Test public void test_2636() { checkNotSubtype("[{null f2}]","{[null] f1}"); }
	@Test public void test_2637() { checkNotSubtype("[{null f2}]","{[null] f2}"); }
	@Test public void test_2638() { checkNotSubtype("[{null f2}]","{[null] f1,null f2}"); }
	@Test public void test_2639() { checkNotSubtype("[{null f2}]","{[null] f2,null f3}"); }
	@Test public void test_2640() { checkNotSubtype("[{null f2}]","{[int] f1}"); }
	@Test public void test_2641() { checkNotSubtype("[{null f2}]","{[int] f2}"); }
	@Test public void test_2642() { checkNotSubtype("[{null f2}]","{[int] f1,int f2}"); }
	@Test public void test_2643() { checkNotSubtype("[{null f2}]","{[int] f2,int f3}"); }
	@Test public void test_2644() { checkIsSubtype("[{null f2}]","{{void f1} f1}"); }
	@Test public void test_2645() { checkIsSubtype("[{null f2}]","{{void f2} f1}"); }
	@Test public void test_2646() { checkIsSubtype("[{null f2}]","{{void f1} f2}"); }
	@Test public void test_2647() { checkIsSubtype("[{null f2}]","{{void f2} f2}"); }
	@Test public void test_2648() { checkIsSubtype("[{null f2}]","{{void f1} f1,void f2}"); }
	@Test public void test_2649() { checkIsSubtype("[{null f2}]","{{void f2} f1,void f2}"); }
	@Test public void test_2650() { checkIsSubtype("[{null f2}]","{{void f1} f2,void f3}"); }
	@Test public void test_2651() { checkIsSubtype("[{null f2}]","{{void f2} f2,void f3}"); }
	@Test public void test_2652() { checkNotSubtype("[{null f2}]","{{any f1} f1}"); }
	@Test public void test_2653() { checkNotSubtype("[{null f2}]","{{any f2} f1}"); }
	@Test public void test_2654() { checkNotSubtype("[{null f2}]","{{any f1} f2}"); }
	@Test public void test_2655() { checkNotSubtype("[{null f2}]","{{any f2} f2}"); }
	@Test public void test_2656() { checkNotSubtype("[{null f2}]","{{any f1} f1,any f2}"); }
	@Test public void test_2657() { checkNotSubtype("[{null f2}]","{{any f2} f1,any f2}"); }
	@Test public void test_2658() { checkNotSubtype("[{null f2}]","{{any f1} f2,any f3}"); }
	@Test public void test_2659() { checkNotSubtype("[{null f2}]","{{any f2} f2,any f3}"); }
	@Test public void test_2660() { checkNotSubtype("[{null f2}]","{{null f1} f1}"); }
	@Test public void test_2661() { checkNotSubtype("[{null f2}]","{{null f2} f1}"); }
	@Test public void test_2662() { checkNotSubtype("[{null f2}]","{{null f1} f2}"); }
	@Test public void test_2663() { checkNotSubtype("[{null f2}]","{{null f2} f2}"); }
	@Test public void test_2664() { checkNotSubtype("[{null f2}]","{{null f1} f1,null f2}"); }
	@Test public void test_2665() { checkNotSubtype("[{null f2}]","{{null f2} f1,null f2}"); }
	@Test public void test_2666() { checkNotSubtype("[{null f2}]","{{null f1} f2,null f3}"); }
	@Test public void test_2667() { checkNotSubtype("[{null f2}]","{{null f2} f2,null f3}"); }
	@Test public void test_2668() { checkNotSubtype("[{null f2}]","{{int f1} f1}"); }
	@Test public void test_2669() { checkNotSubtype("[{null f2}]","{{int f2} f1}"); }
	@Test public void test_2670() { checkNotSubtype("[{null f2}]","{{int f1} f2}"); }
	@Test public void test_2671() { checkNotSubtype("[{null f2}]","{{int f2} f2}"); }
	@Test public void test_2672() { checkNotSubtype("[{null f2}]","{{int f1} f1,int f2}"); }
	@Test public void test_2673() { checkNotSubtype("[{null f2}]","{{int f2} f1,int f2}"); }
	@Test public void test_2674() { checkNotSubtype("[{null f2}]","{{int f1} f2,int f3}"); }
	@Test public void test_2675() { checkNotSubtype("[{null f2}]","{{int f2} f2,int f3}"); }
	@Test public void test_2676() { checkNotSubtype("[{int f1}]","any"); }
	@Test public void test_2677() { checkNotSubtype("[{int f1}]","null"); }
	@Test public void test_2678() { checkNotSubtype("[{int f1}]","int"); }
	@Test public void test_2679() { checkIsSubtype("[{int f1}]","[void]"); }
	@Test public void test_2680() { checkNotSubtype("[{int f1}]","[any]"); }
	@Test public void test_2681() { checkNotSubtype("[{int f1}]","[null]"); }
	@Test public void test_2682() { checkNotSubtype("[{int f1}]","[int]"); }
	@Test public void test_2683() { checkIsSubtype("[{int f1}]","{void f1}"); }
	@Test public void test_2684() { checkIsSubtype("[{int f1}]","{void f2}"); }
	@Test public void test_2685() { checkNotSubtype("[{int f1}]","{any f1}"); }
	@Test public void test_2686() { checkNotSubtype("[{int f1}]","{any f2}"); }
	@Test public void test_2687() { checkNotSubtype("[{int f1}]","{null f1}"); }
	@Test public void test_2688() { checkNotSubtype("[{int f1}]","{null f2}"); }
	@Test public void test_2689() { checkNotSubtype("[{int f1}]","{int f1}"); }
	@Test public void test_2690() { checkNotSubtype("[{int f1}]","{int f2}"); }
	@Test public void test_2691() { checkNotSubtype("[{int f1}]","[[void]]"); }
	@Test public void test_2692() { checkNotSubtype("[{int f1}]","[[any]]"); }
	@Test public void test_2693() { checkNotSubtype("[{int f1}]","[[null]]"); }
	@Test public void test_2694() { checkNotSubtype("[{int f1}]","[[int]]"); }
	@Test public void test_2695() { checkIsSubtype("[{int f1}]","[{void f1}]"); }
	@Test public void test_2696() { checkIsSubtype("[{int f1}]","[{void f2}]"); }
	@Test public void test_2697() { checkNotSubtype("[{int f1}]","[{any f1}]"); }
	@Test public void test_2698() { checkNotSubtype("[{int f1}]","[{any f2}]"); }
	@Test public void test_2699() { checkNotSubtype("[{int f1}]","[{null f1}]"); }
	@Test public void test_2700() { checkNotSubtype("[{int f1}]","[{null f2}]"); }
	@Test public void test_2701() { checkIsSubtype("[{int f1}]","[{int f1}]"); }
	@Test public void test_2702() { checkNotSubtype("[{int f1}]","[{int f2}]"); }
	@Test public void test_2703() { checkIsSubtype("[{int f1}]","{void f1,void f2}"); }
	@Test public void test_2704() { checkIsSubtype("[{int f1}]","{void f2,void f3}"); }
	@Test public void test_2705() { checkIsSubtype("[{int f1}]","{void f1,any f2}"); }
	@Test public void test_2706() { checkIsSubtype("[{int f1}]","{void f2,any f3}"); }
	@Test public void test_2707() { checkIsSubtype("[{int f1}]","{void f1,null f2}"); }
	@Test public void test_2708() { checkIsSubtype("[{int f1}]","{void f2,null f3}"); }
	@Test public void test_2709() { checkIsSubtype("[{int f1}]","{void f1,int f2}"); }
	@Test public void test_2710() { checkIsSubtype("[{int f1}]","{void f2,int f3}"); }
	@Test public void test_2711() { checkIsSubtype("[{int f1}]","{any f1,void f2}"); }
	@Test public void test_2712() { checkIsSubtype("[{int f1}]","{any f2,void f3}"); }
	@Test public void test_2713() { checkNotSubtype("[{int f1}]","{any f1,any f2}"); }
	@Test public void test_2714() { checkNotSubtype("[{int f1}]","{any f2,any f3}"); }
	@Test public void test_2715() { checkNotSubtype("[{int f1}]","{any f1,null f2}"); }
	@Test public void test_2716() { checkNotSubtype("[{int f1}]","{any f2,null f3}"); }
	@Test public void test_2717() { checkNotSubtype("[{int f1}]","{any f1,int f2}"); }
	@Test public void test_2718() { checkNotSubtype("[{int f1}]","{any f2,int f3}"); }
	@Test public void test_2719() { checkIsSubtype("[{int f1}]","{null f1,void f2}"); }
	@Test public void test_2720() { checkIsSubtype("[{int f1}]","{null f2,void f3}"); }
	@Test public void test_2721() { checkNotSubtype("[{int f1}]","{null f1,any f2}"); }
	@Test public void test_2722() { checkNotSubtype("[{int f1}]","{null f2,any f3}"); }
	@Test public void test_2723() { checkNotSubtype("[{int f1}]","{null f1,null f2}"); }
	@Test public void test_2724() { checkNotSubtype("[{int f1}]","{null f2,null f3}"); }
	@Test public void test_2725() { checkNotSubtype("[{int f1}]","{null f1,int f2}"); }
	@Test public void test_2726() { checkNotSubtype("[{int f1}]","{null f2,int f3}"); }
	@Test public void test_2727() { checkIsSubtype("[{int f1}]","{int f1,void f2}"); }
	@Test public void test_2728() { checkIsSubtype("[{int f1}]","{int f2,void f3}"); }
	@Test public void test_2729() { checkNotSubtype("[{int f1}]","{int f1,any f2}"); }
	@Test public void test_2730() { checkNotSubtype("[{int f1}]","{int f2,any f3}"); }
	@Test public void test_2731() { checkNotSubtype("[{int f1}]","{int f1,null f2}"); }
	@Test public void test_2732() { checkNotSubtype("[{int f1}]","{int f2,null f3}"); }
	@Test public void test_2733() { checkNotSubtype("[{int f1}]","{int f1,int f2}"); }
	@Test public void test_2734() { checkNotSubtype("[{int f1}]","{int f2,int f3}"); }
	@Test public void test_2735() { checkNotSubtype("[{int f1}]","{[void] f1}"); }
	@Test public void test_2736() { checkNotSubtype("[{int f1}]","{[void] f2}"); }
	@Test public void test_2737() { checkIsSubtype("[{int f1}]","{[void] f1,void f2}"); }
	@Test public void test_2738() { checkIsSubtype("[{int f1}]","{[void] f2,void f3}"); }
	@Test public void test_2739() { checkNotSubtype("[{int f1}]","{[any] f1}"); }
	@Test public void test_2740() { checkNotSubtype("[{int f1}]","{[any] f2}"); }
	@Test public void test_2741() { checkNotSubtype("[{int f1}]","{[any] f1,any f2}"); }
	@Test public void test_2742() { checkNotSubtype("[{int f1}]","{[any] f2,any f3}"); }
	@Test public void test_2743() { checkNotSubtype("[{int f1}]","{[null] f1}"); }
	@Test public void test_2744() { checkNotSubtype("[{int f1}]","{[null] f2}"); }
	@Test public void test_2745() { checkNotSubtype("[{int f1}]","{[null] f1,null f2}"); }
	@Test public void test_2746() { checkNotSubtype("[{int f1}]","{[null] f2,null f3}"); }
	@Test public void test_2747() { checkNotSubtype("[{int f1}]","{[int] f1}"); }
	@Test public void test_2748() { checkNotSubtype("[{int f1}]","{[int] f2}"); }
	@Test public void test_2749() { checkNotSubtype("[{int f1}]","{[int] f1,int f2}"); }
	@Test public void test_2750() { checkNotSubtype("[{int f1}]","{[int] f2,int f3}"); }
	@Test public void test_2751() { checkIsSubtype("[{int f1}]","{{void f1} f1}"); }
	@Test public void test_2752() { checkIsSubtype("[{int f1}]","{{void f2} f1}"); }
	@Test public void test_2753() { checkIsSubtype("[{int f1}]","{{void f1} f2}"); }
	@Test public void test_2754() { checkIsSubtype("[{int f1}]","{{void f2} f2}"); }
	@Test public void test_2755() { checkIsSubtype("[{int f1}]","{{void f1} f1,void f2}"); }
	@Test public void test_2756() { checkIsSubtype("[{int f1}]","{{void f2} f1,void f2}"); }
	@Test public void test_2757() { checkIsSubtype("[{int f1}]","{{void f1} f2,void f3}"); }
	@Test public void test_2758() { checkIsSubtype("[{int f1}]","{{void f2} f2,void f3}"); }
	@Test public void test_2759() { checkNotSubtype("[{int f1}]","{{any f1} f1}"); }
	@Test public void test_2760() { checkNotSubtype("[{int f1}]","{{any f2} f1}"); }
	@Test public void test_2761() { checkNotSubtype("[{int f1}]","{{any f1} f2}"); }
	@Test public void test_2762() { checkNotSubtype("[{int f1}]","{{any f2} f2}"); }
	@Test public void test_2763() { checkNotSubtype("[{int f1}]","{{any f1} f1,any f2}"); }
	@Test public void test_2764() { checkNotSubtype("[{int f1}]","{{any f2} f1,any f2}"); }
	@Test public void test_2765() { checkNotSubtype("[{int f1}]","{{any f1} f2,any f3}"); }
	@Test public void test_2766() { checkNotSubtype("[{int f1}]","{{any f2} f2,any f3}"); }
	@Test public void test_2767() { checkNotSubtype("[{int f1}]","{{null f1} f1}"); }
	@Test public void test_2768() { checkNotSubtype("[{int f1}]","{{null f2} f1}"); }
	@Test public void test_2769() { checkNotSubtype("[{int f1}]","{{null f1} f2}"); }
	@Test public void test_2770() { checkNotSubtype("[{int f1}]","{{null f2} f2}"); }
	@Test public void test_2771() { checkNotSubtype("[{int f1}]","{{null f1} f1,null f2}"); }
	@Test public void test_2772() { checkNotSubtype("[{int f1}]","{{null f2} f1,null f2}"); }
	@Test public void test_2773() { checkNotSubtype("[{int f1}]","{{null f1} f2,null f3}"); }
	@Test public void test_2774() { checkNotSubtype("[{int f1}]","{{null f2} f2,null f3}"); }
	@Test public void test_2775() { checkNotSubtype("[{int f1}]","{{int f1} f1}"); }
	@Test public void test_2776() { checkNotSubtype("[{int f1}]","{{int f2} f1}"); }
	@Test public void test_2777() { checkNotSubtype("[{int f1}]","{{int f1} f2}"); }
	@Test public void test_2778() { checkNotSubtype("[{int f1}]","{{int f2} f2}"); }
	@Test public void test_2779() { checkNotSubtype("[{int f1}]","{{int f1} f1,int f2}"); }
	@Test public void test_2780() { checkNotSubtype("[{int f1}]","{{int f2} f1,int f2}"); }
	@Test public void test_2781() { checkNotSubtype("[{int f1}]","{{int f1} f2,int f3}"); }
	@Test public void test_2782() { checkNotSubtype("[{int f1}]","{{int f2} f2,int f3}"); }
	@Test public void test_2783() { checkNotSubtype("[{int f2}]","any"); }
	@Test public void test_2784() { checkNotSubtype("[{int f2}]","null"); }
	@Test public void test_2785() { checkNotSubtype("[{int f2}]","int"); }
	@Test public void test_2786() { checkIsSubtype("[{int f2}]","[void]"); }
	@Test public void test_2787() { checkNotSubtype("[{int f2}]","[any]"); }
	@Test public void test_2788() { checkNotSubtype("[{int f2}]","[null]"); }
	@Test public void test_2789() { checkNotSubtype("[{int f2}]","[int]"); }
	@Test public void test_2790() { checkIsSubtype("[{int f2}]","{void f1}"); }
	@Test public void test_2791() { checkIsSubtype("[{int f2}]","{void f2}"); }
	@Test public void test_2792() { checkNotSubtype("[{int f2}]","{any f1}"); }
	@Test public void test_2793() { checkNotSubtype("[{int f2}]","{any f2}"); }
	@Test public void test_2794() { checkNotSubtype("[{int f2}]","{null f1}"); }
	@Test public void test_2795() { checkNotSubtype("[{int f2}]","{null f2}"); }
	@Test public void test_2796() { checkNotSubtype("[{int f2}]","{int f1}"); }
	@Test public void test_2797() { checkNotSubtype("[{int f2}]","{int f2}"); }
	@Test public void test_2798() { checkNotSubtype("[{int f2}]","[[void]]"); }
	@Test public void test_2799() { checkNotSubtype("[{int f2}]","[[any]]"); }
	@Test public void test_2800() { checkNotSubtype("[{int f2}]","[[null]]"); }
	@Test public void test_2801() { checkNotSubtype("[{int f2}]","[[int]]"); }
	@Test public void test_2802() { checkIsSubtype("[{int f2}]","[{void f1}]"); }
	@Test public void test_2803() { checkIsSubtype("[{int f2}]","[{void f2}]"); }
	@Test public void test_2804() { checkNotSubtype("[{int f2}]","[{any f1}]"); }
	@Test public void test_2805() { checkNotSubtype("[{int f2}]","[{any f2}]"); }
	@Test public void test_2806() { checkNotSubtype("[{int f2}]","[{null f1}]"); }
	@Test public void test_2807() { checkNotSubtype("[{int f2}]","[{null f2}]"); }
	@Test public void test_2808() { checkNotSubtype("[{int f2}]","[{int f1}]"); }
	@Test public void test_2809() { checkIsSubtype("[{int f2}]","[{int f2}]"); }
	@Test public void test_2810() { checkIsSubtype("[{int f2}]","{void f1,void f2}"); }
	@Test public void test_2811() { checkIsSubtype("[{int f2}]","{void f2,void f3}"); }
	@Test public void test_2812() { checkIsSubtype("[{int f2}]","{void f1,any f2}"); }
	@Test public void test_2813() { checkIsSubtype("[{int f2}]","{void f2,any f3}"); }
	@Test public void test_2814() { checkIsSubtype("[{int f2}]","{void f1,null f2}"); }
	@Test public void test_2815() { checkIsSubtype("[{int f2}]","{void f2,null f3}"); }
	@Test public void test_2816() { checkIsSubtype("[{int f2}]","{void f1,int f2}"); }
	@Test public void test_2817() { checkIsSubtype("[{int f2}]","{void f2,int f3}"); }
	@Test public void test_2818() { checkIsSubtype("[{int f2}]","{any f1,void f2}"); }
	@Test public void test_2819() { checkIsSubtype("[{int f2}]","{any f2,void f3}"); }
	@Test public void test_2820() { checkNotSubtype("[{int f2}]","{any f1,any f2}"); }
	@Test public void test_2821() { checkNotSubtype("[{int f2}]","{any f2,any f3}"); }
	@Test public void test_2822() { checkNotSubtype("[{int f2}]","{any f1,null f2}"); }
	@Test public void test_2823() { checkNotSubtype("[{int f2}]","{any f2,null f3}"); }
	@Test public void test_2824() { checkNotSubtype("[{int f2}]","{any f1,int f2}"); }
	@Test public void test_2825() { checkNotSubtype("[{int f2}]","{any f2,int f3}"); }
	@Test public void test_2826() { checkIsSubtype("[{int f2}]","{null f1,void f2}"); }
	@Test public void test_2827() { checkIsSubtype("[{int f2}]","{null f2,void f3}"); }
	@Test public void test_2828() { checkNotSubtype("[{int f2}]","{null f1,any f2}"); }
	@Test public void test_2829() { checkNotSubtype("[{int f2}]","{null f2,any f3}"); }
	@Test public void test_2830() { checkNotSubtype("[{int f2}]","{null f1,null f2}"); }
	@Test public void test_2831() { checkNotSubtype("[{int f2}]","{null f2,null f3}"); }
	@Test public void test_2832() { checkNotSubtype("[{int f2}]","{null f1,int f2}"); }
	@Test public void test_2833() { checkNotSubtype("[{int f2}]","{null f2,int f3}"); }
	@Test public void test_2834() { checkIsSubtype("[{int f2}]","{int f1,void f2}"); }
	@Test public void test_2835() { checkIsSubtype("[{int f2}]","{int f2,void f3}"); }
	@Test public void test_2836() { checkNotSubtype("[{int f2}]","{int f1,any f2}"); }
	@Test public void test_2837() { checkNotSubtype("[{int f2}]","{int f2,any f3}"); }
	@Test public void test_2838() { checkNotSubtype("[{int f2}]","{int f1,null f2}"); }
	@Test public void test_2839() { checkNotSubtype("[{int f2}]","{int f2,null f3}"); }
	@Test public void test_2840() { checkNotSubtype("[{int f2}]","{int f1,int f2}"); }
	@Test public void test_2841() { checkNotSubtype("[{int f2}]","{int f2,int f3}"); }
	@Test public void test_2842() { checkNotSubtype("[{int f2}]","{[void] f1}"); }
	@Test public void test_2843() { checkNotSubtype("[{int f2}]","{[void] f2}"); }
	@Test public void test_2844() { checkIsSubtype("[{int f2}]","{[void] f1,void f2}"); }
	@Test public void test_2845() { checkIsSubtype("[{int f2}]","{[void] f2,void f3}"); }
	@Test public void test_2846() { checkNotSubtype("[{int f2}]","{[any] f1}"); }
	@Test public void test_2847() { checkNotSubtype("[{int f2}]","{[any] f2}"); }
	@Test public void test_2848() { checkNotSubtype("[{int f2}]","{[any] f1,any f2}"); }
	@Test public void test_2849() { checkNotSubtype("[{int f2}]","{[any] f2,any f3}"); }
	@Test public void test_2850() { checkNotSubtype("[{int f2}]","{[null] f1}"); }
	@Test public void test_2851() { checkNotSubtype("[{int f2}]","{[null] f2}"); }
	@Test public void test_2852() { checkNotSubtype("[{int f2}]","{[null] f1,null f2}"); }
	@Test public void test_2853() { checkNotSubtype("[{int f2}]","{[null] f2,null f3}"); }
	@Test public void test_2854() { checkNotSubtype("[{int f2}]","{[int] f1}"); }
	@Test public void test_2855() { checkNotSubtype("[{int f2}]","{[int] f2}"); }
	@Test public void test_2856() { checkNotSubtype("[{int f2}]","{[int] f1,int f2}"); }
	@Test public void test_2857() { checkNotSubtype("[{int f2}]","{[int] f2,int f3}"); }
	@Test public void test_2858() { checkIsSubtype("[{int f2}]","{{void f1} f1}"); }
	@Test public void test_2859() { checkIsSubtype("[{int f2}]","{{void f2} f1}"); }
	@Test public void test_2860() { checkIsSubtype("[{int f2}]","{{void f1} f2}"); }
	@Test public void test_2861() { checkIsSubtype("[{int f2}]","{{void f2} f2}"); }
	@Test public void test_2862() { checkIsSubtype("[{int f2}]","{{void f1} f1,void f2}"); }
	@Test public void test_2863() { checkIsSubtype("[{int f2}]","{{void f2} f1,void f2}"); }
	@Test public void test_2864() { checkIsSubtype("[{int f2}]","{{void f1} f2,void f3}"); }
	@Test public void test_2865() { checkIsSubtype("[{int f2}]","{{void f2} f2,void f3}"); }
	@Test public void test_2866() { checkNotSubtype("[{int f2}]","{{any f1} f1}"); }
	@Test public void test_2867() { checkNotSubtype("[{int f2}]","{{any f2} f1}"); }
	@Test public void test_2868() { checkNotSubtype("[{int f2}]","{{any f1} f2}"); }
	@Test public void test_2869() { checkNotSubtype("[{int f2}]","{{any f2} f2}"); }
	@Test public void test_2870() { checkNotSubtype("[{int f2}]","{{any f1} f1,any f2}"); }
	@Test public void test_2871() { checkNotSubtype("[{int f2}]","{{any f2} f1,any f2}"); }
	@Test public void test_2872() { checkNotSubtype("[{int f2}]","{{any f1} f2,any f3}"); }
	@Test public void test_2873() { checkNotSubtype("[{int f2}]","{{any f2} f2,any f3}"); }
	@Test public void test_2874() { checkNotSubtype("[{int f2}]","{{null f1} f1}"); }
	@Test public void test_2875() { checkNotSubtype("[{int f2}]","{{null f2} f1}"); }
	@Test public void test_2876() { checkNotSubtype("[{int f2}]","{{null f1} f2}"); }
	@Test public void test_2877() { checkNotSubtype("[{int f2}]","{{null f2} f2}"); }
	@Test public void test_2878() { checkNotSubtype("[{int f2}]","{{null f1} f1,null f2}"); }
	@Test public void test_2879() { checkNotSubtype("[{int f2}]","{{null f2} f1,null f2}"); }
	@Test public void test_2880() { checkNotSubtype("[{int f2}]","{{null f1} f2,null f3}"); }
	@Test public void test_2881() { checkNotSubtype("[{int f2}]","{{null f2} f2,null f3}"); }
	@Test public void test_2882() { checkNotSubtype("[{int f2}]","{{int f1} f1}"); }
	@Test public void test_2883() { checkNotSubtype("[{int f2}]","{{int f2} f1}"); }
	@Test public void test_2884() { checkNotSubtype("[{int f2}]","{{int f1} f2}"); }
	@Test public void test_2885() { checkNotSubtype("[{int f2}]","{{int f2} f2}"); }
	@Test public void test_2886() { checkNotSubtype("[{int f2}]","{{int f1} f1,int f2}"); }
	@Test public void test_2887() { checkNotSubtype("[{int f2}]","{{int f2} f1,int f2}"); }
	@Test public void test_2888() { checkNotSubtype("[{int f2}]","{{int f1} f2,int f3}"); }
	@Test public void test_2889() { checkNotSubtype("[{int f2}]","{{int f2} f2,int f3}"); }
	@Test public void test_2890() { checkNotSubtype("{void f1,void f2}","any"); }
	@Test public void test_2891() { checkNotSubtype("{void f1,void f2}","null"); }
	@Test public void test_2892() { checkNotSubtype("{void f1,void f2}","int"); }
	@Test public void test_2893() { checkNotSubtype("{void f1,void f2}","[void]"); }
	@Test public void test_2894() { checkNotSubtype("{void f1,void f2}","[any]"); }
	@Test public void test_2895() { checkNotSubtype("{void f1,void f2}","[null]"); }
	@Test public void test_2896() { checkNotSubtype("{void f1,void f2}","[int]"); }
	@Test public void test_2897() { checkIsSubtype("{void f1,void f2}","{void f1}"); }
	@Test public void test_2898() { checkIsSubtype("{void f1,void f2}","{void f2}"); }
	@Test public void test_2899() { checkNotSubtype("{void f1,void f2}","{any f1}"); }
	@Test public void test_2900() { checkNotSubtype("{void f1,void f2}","{any f2}"); }
	@Test public void test_2901() { checkNotSubtype("{void f1,void f2}","{null f1}"); }
	@Test public void test_2902() { checkNotSubtype("{void f1,void f2}","{null f2}"); }
	@Test public void test_2903() { checkNotSubtype("{void f1,void f2}","{int f1}"); }
	@Test public void test_2904() { checkNotSubtype("{void f1,void f2}","{int f2}"); }
	@Test public void test_2905() { checkNotSubtype("{void f1,void f2}","[[void]]"); }
	@Test public void test_2906() { checkNotSubtype("{void f1,void f2}","[[any]]"); }
	@Test public void test_2907() { checkNotSubtype("{void f1,void f2}","[[null]]"); }
	@Test public void test_2908() { checkNotSubtype("{void f1,void f2}","[[int]]"); }
	@Test public void test_2909() { checkNotSubtype("{void f1,void f2}","[{void f1}]"); }
	@Test public void test_2910() { checkNotSubtype("{void f1,void f2}","[{void f2}]"); }
	@Test public void test_2911() { checkNotSubtype("{void f1,void f2}","[{any f1}]"); }
	@Test public void test_2912() { checkNotSubtype("{void f1,void f2}","[{any f2}]"); }
	@Test public void test_2913() { checkNotSubtype("{void f1,void f2}","[{null f1}]"); }
	@Test public void test_2914() { checkNotSubtype("{void f1,void f2}","[{null f2}]"); }
	@Test public void test_2915() { checkNotSubtype("{void f1,void f2}","[{int f1}]"); }
	@Test public void test_2916() { checkNotSubtype("{void f1,void f2}","[{int f2}]"); }
	@Test public void test_2917() { checkIsSubtype("{void f1,void f2}","{void f1,void f2}"); }
	@Test public void test_2918() { checkIsSubtype("{void f1,void f2}","{void f2,void f3}"); }
	@Test public void test_2919() { checkIsSubtype("{void f1,void f2}","{void f1,any f2}"); }
	@Test public void test_2920() { checkIsSubtype("{void f1,void f2}","{void f2,any f3}"); }
	@Test public void test_2921() { checkIsSubtype("{void f1,void f2}","{void f1,null f2}"); }
	@Test public void test_2922() { checkIsSubtype("{void f1,void f2}","{void f2,null f3}"); }
	@Test public void test_2923() { checkIsSubtype("{void f1,void f2}","{void f1,int f2}"); }
	@Test public void test_2924() { checkIsSubtype("{void f1,void f2}","{void f2,int f3}"); }
	@Test public void test_2925() { checkIsSubtype("{void f1,void f2}","{any f1,void f2}"); }
	@Test public void test_2926() { checkIsSubtype("{void f1,void f2}","{any f2,void f3}"); }
	@Test public void test_2927() { checkNotSubtype("{void f1,void f2}","{any f1,any f2}"); }
	@Test public void test_2928() { checkNotSubtype("{void f1,void f2}","{any f2,any f3}"); }
	@Test public void test_2929() { checkNotSubtype("{void f1,void f2}","{any f1,null f2}"); }
	@Test public void test_2930() { checkNotSubtype("{void f1,void f2}","{any f2,null f3}"); }
	@Test public void test_2931() { checkNotSubtype("{void f1,void f2}","{any f1,int f2}"); }
	@Test public void test_2932() { checkNotSubtype("{void f1,void f2}","{any f2,int f3}"); }
	@Test public void test_2933() { checkIsSubtype("{void f1,void f2}","{null f1,void f2}"); }
	@Test public void test_2934() { checkIsSubtype("{void f1,void f2}","{null f2,void f3}"); }
	@Test public void test_2935() { checkNotSubtype("{void f1,void f2}","{null f1,any f2}"); }
	@Test public void test_2936() { checkNotSubtype("{void f1,void f2}","{null f2,any f3}"); }
	@Test public void test_2937() { checkNotSubtype("{void f1,void f2}","{null f1,null f2}"); }
	@Test public void test_2938() { checkNotSubtype("{void f1,void f2}","{null f2,null f3}"); }
	@Test public void test_2939() { checkNotSubtype("{void f1,void f2}","{null f1,int f2}"); }
	@Test public void test_2940() { checkNotSubtype("{void f1,void f2}","{null f2,int f3}"); }
	@Test public void test_2941() { checkIsSubtype("{void f1,void f2}","{int f1,void f2}"); }
	@Test public void test_2942() { checkIsSubtype("{void f1,void f2}","{int f2,void f3}"); }
	@Test public void test_2943() { checkNotSubtype("{void f1,void f2}","{int f1,any f2}"); }
	@Test public void test_2944() { checkNotSubtype("{void f1,void f2}","{int f2,any f3}"); }
	@Test public void test_2945() { checkNotSubtype("{void f1,void f2}","{int f1,null f2}"); }
	@Test public void test_2946() { checkNotSubtype("{void f1,void f2}","{int f2,null f3}"); }
	@Test public void test_2947() { checkNotSubtype("{void f1,void f2}","{int f1,int f2}"); }
	@Test public void test_2948() { checkNotSubtype("{void f1,void f2}","{int f2,int f3}"); }
	@Test public void test_2949() { checkNotSubtype("{void f1,void f2}","{[void] f1}"); }
	@Test public void test_2950() { checkNotSubtype("{void f1,void f2}","{[void] f2}"); }
	@Test public void test_2951() { checkIsSubtype("{void f1,void f2}","{[void] f1,void f2}"); }
	@Test public void test_2952() { checkIsSubtype("{void f1,void f2}","{[void] f2,void f3}"); }
	@Test public void test_2953() { checkNotSubtype("{void f1,void f2}","{[any] f1}"); }
	@Test public void test_2954() { checkNotSubtype("{void f1,void f2}","{[any] f2}"); }
	@Test public void test_2955() { checkNotSubtype("{void f1,void f2}","{[any] f1,any f2}"); }
	@Test public void test_2956() { checkNotSubtype("{void f1,void f2}","{[any] f2,any f3}"); }
	@Test public void test_2957() { checkNotSubtype("{void f1,void f2}","{[null] f1}"); }
	@Test public void test_2958() { checkNotSubtype("{void f1,void f2}","{[null] f2}"); }
	@Test public void test_2959() { checkNotSubtype("{void f1,void f2}","{[null] f1,null f2}"); }
	@Test public void test_2960() { checkNotSubtype("{void f1,void f2}","{[null] f2,null f3}"); }
	@Test public void test_2961() { checkNotSubtype("{void f1,void f2}","{[int] f1}"); }
	@Test public void test_2962() { checkNotSubtype("{void f1,void f2}","{[int] f2}"); }
	@Test public void test_2963() { checkNotSubtype("{void f1,void f2}","{[int] f1,int f2}"); }
	@Test public void test_2964() { checkNotSubtype("{void f1,void f2}","{[int] f2,int f3}"); }
	@Test public void test_2965() { checkIsSubtype("{void f1,void f2}","{{void f1} f1}"); }
	@Test public void test_2966() { checkIsSubtype("{void f1,void f2}","{{void f2} f1}"); }
	@Test public void test_2967() { checkIsSubtype("{void f1,void f2}","{{void f1} f2}"); }
	@Test public void test_2968() { checkIsSubtype("{void f1,void f2}","{{void f2} f2}"); }
	@Test public void test_2969() { checkIsSubtype("{void f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_2970() { checkIsSubtype("{void f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_2971() { checkIsSubtype("{void f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_2972() { checkIsSubtype("{void f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_2973() { checkNotSubtype("{void f1,void f2}","{{any f1} f1}"); }
	@Test public void test_2974() { checkNotSubtype("{void f1,void f2}","{{any f2} f1}"); }
	@Test public void test_2975() { checkNotSubtype("{void f1,void f2}","{{any f1} f2}"); }
	@Test public void test_2976() { checkNotSubtype("{void f1,void f2}","{{any f2} f2}"); }
	@Test public void test_2977() { checkNotSubtype("{void f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_2978() { checkNotSubtype("{void f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_2979() { checkNotSubtype("{void f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_2980() { checkNotSubtype("{void f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_2981() { checkNotSubtype("{void f1,void f2}","{{null f1} f1}"); }
	@Test public void test_2982() { checkNotSubtype("{void f1,void f2}","{{null f2} f1}"); }
	@Test public void test_2983() { checkNotSubtype("{void f1,void f2}","{{null f1} f2}"); }
	@Test public void test_2984() { checkNotSubtype("{void f1,void f2}","{{null f2} f2}"); }
	@Test public void test_2985() { checkNotSubtype("{void f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_2986() { checkNotSubtype("{void f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_2987() { checkNotSubtype("{void f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_2988() { checkNotSubtype("{void f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_2989() { checkNotSubtype("{void f1,void f2}","{{int f1} f1}"); }
	@Test public void test_2990() { checkNotSubtype("{void f1,void f2}","{{int f2} f1}"); }
	@Test public void test_2991() { checkNotSubtype("{void f1,void f2}","{{int f1} f2}"); }
	@Test public void test_2992() { checkNotSubtype("{void f1,void f2}","{{int f2} f2}"); }
	@Test public void test_2993() { checkNotSubtype("{void f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_2994() { checkNotSubtype("{void f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_2995() { checkNotSubtype("{void f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_2996() { checkNotSubtype("{void f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_2997() { checkNotSubtype("{void f2,void f3}","any"); }
	@Test public void test_2998() { checkNotSubtype("{void f2,void f3}","null"); }
	@Test public void test_2999() { checkNotSubtype("{void f2,void f3}","int"); }
	@Test public void test_3000() { checkNotSubtype("{void f2,void f3}","[void]"); }
	@Test public void test_3001() { checkNotSubtype("{void f2,void f3}","[any]"); }
	@Test public void test_3002() { checkNotSubtype("{void f2,void f3}","[null]"); }
	@Test public void test_3003() { checkNotSubtype("{void f2,void f3}","[int]"); }
	@Test public void test_3004() { checkIsSubtype("{void f2,void f3}","{void f1}"); }
	@Test public void test_3005() { checkIsSubtype("{void f2,void f3}","{void f2}"); }
	@Test public void test_3006() { checkNotSubtype("{void f2,void f3}","{any f1}"); }
	@Test public void test_3007() { checkNotSubtype("{void f2,void f3}","{any f2}"); }
	@Test public void test_3008() { checkNotSubtype("{void f2,void f3}","{null f1}"); }
	@Test public void test_3009() { checkNotSubtype("{void f2,void f3}","{null f2}"); }
	@Test public void test_3010() { checkNotSubtype("{void f2,void f3}","{int f1}"); }
	@Test public void test_3011() { checkNotSubtype("{void f2,void f3}","{int f2}"); }
	@Test public void test_3012() { checkNotSubtype("{void f2,void f3}","[[void]]"); }
	@Test public void test_3013() { checkNotSubtype("{void f2,void f3}","[[any]]"); }
	@Test public void test_3014() { checkNotSubtype("{void f2,void f3}","[[null]]"); }
	@Test public void test_3015() { checkNotSubtype("{void f2,void f3}","[[int]]"); }
	@Test public void test_3016() { checkNotSubtype("{void f2,void f3}","[{void f1}]"); }
	@Test public void test_3017() { checkNotSubtype("{void f2,void f3}","[{void f2}]"); }
	@Test public void test_3018() { checkNotSubtype("{void f2,void f3}","[{any f1}]"); }
	@Test public void test_3019() { checkNotSubtype("{void f2,void f3}","[{any f2}]"); }
	@Test public void test_3020() { checkNotSubtype("{void f2,void f3}","[{null f1}]"); }
	@Test public void test_3021() { checkNotSubtype("{void f2,void f3}","[{null f2}]"); }
	@Test public void test_3022() { checkNotSubtype("{void f2,void f3}","[{int f1}]"); }
	@Test public void test_3023() { checkNotSubtype("{void f2,void f3}","[{int f2}]"); }
	@Test public void test_3024() { checkIsSubtype("{void f2,void f3}","{void f1,void f2}"); }
	@Test public void test_3025() { checkIsSubtype("{void f2,void f3}","{void f2,void f3}"); }
	@Test public void test_3026() { checkIsSubtype("{void f2,void f3}","{void f1,any f2}"); }
	@Test public void test_3027() { checkIsSubtype("{void f2,void f3}","{void f2,any f3}"); }
	@Test public void test_3028() { checkIsSubtype("{void f2,void f3}","{void f1,null f2}"); }
	@Test public void test_3029() { checkIsSubtype("{void f2,void f3}","{void f2,null f3}"); }
	@Test public void test_3030() { checkIsSubtype("{void f2,void f3}","{void f1,int f2}"); }
	@Test public void test_3031() { checkIsSubtype("{void f2,void f3}","{void f2,int f3}"); }
	@Test public void test_3032() { checkIsSubtype("{void f2,void f3}","{any f1,void f2}"); }
	@Test public void test_3033() { checkIsSubtype("{void f2,void f3}","{any f2,void f3}"); }
	@Test public void test_3034() { checkNotSubtype("{void f2,void f3}","{any f1,any f2}"); }
	@Test public void test_3035() { checkNotSubtype("{void f2,void f3}","{any f2,any f3}"); }
	@Test public void test_3036() { checkNotSubtype("{void f2,void f3}","{any f1,null f2}"); }
	@Test public void test_3037() { checkNotSubtype("{void f2,void f3}","{any f2,null f3}"); }
	@Test public void test_3038() { checkNotSubtype("{void f2,void f3}","{any f1,int f2}"); }
	@Test public void test_3039() { checkNotSubtype("{void f2,void f3}","{any f2,int f3}"); }
	@Test public void test_3040() { checkIsSubtype("{void f2,void f3}","{null f1,void f2}"); }
	@Test public void test_3041() { checkIsSubtype("{void f2,void f3}","{null f2,void f3}"); }
	@Test public void test_3042() { checkNotSubtype("{void f2,void f3}","{null f1,any f2}"); }
	@Test public void test_3043() { checkNotSubtype("{void f2,void f3}","{null f2,any f3}"); }
	@Test public void test_3044() { checkNotSubtype("{void f2,void f3}","{null f1,null f2}"); }
	@Test public void test_3045() { checkNotSubtype("{void f2,void f3}","{null f2,null f3}"); }
	@Test public void test_3046() { checkNotSubtype("{void f2,void f3}","{null f1,int f2}"); }
	@Test public void test_3047() { checkNotSubtype("{void f2,void f3}","{null f2,int f3}"); }
	@Test public void test_3048() { checkIsSubtype("{void f2,void f3}","{int f1,void f2}"); }
	@Test public void test_3049() { checkIsSubtype("{void f2,void f3}","{int f2,void f3}"); }
	@Test public void test_3050() { checkNotSubtype("{void f2,void f3}","{int f1,any f2}"); }
	@Test public void test_3051() { checkNotSubtype("{void f2,void f3}","{int f2,any f3}"); }
	@Test public void test_3052() { checkNotSubtype("{void f2,void f3}","{int f1,null f2}"); }
	@Test public void test_3053() { checkNotSubtype("{void f2,void f3}","{int f2,null f3}"); }
	@Test public void test_3054() { checkNotSubtype("{void f2,void f3}","{int f1,int f2}"); }
	@Test public void test_3055() { checkNotSubtype("{void f2,void f3}","{int f2,int f3}"); }
	@Test public void test_3056() { checkNotSubtype("{void f2,void f3}","{[void] f1}"); }
	@Test public void test_3057() { checkNotSubtype("{void f2,void f3}","{[void] f2}"); }
	@Test public void test_3058() { checkIsSubtype("{void f2,void f3}","{[void] f1,void f2}"); }
	@Test public void test_3059() { checkIsSubtype("{void f2,void f3}","{[void] f2,void f3}"); }
	@Test public void test_3060() { checkNotSubtype("{void f2,void f3}","{[any] f1}"); }
	@Test public void test_3061() { checkNotSubtype("{void f2,void f3}","{[any] f2}"); }
	@Test public void test_3062() { checkNotSubtype("{void f2,void f3}","{[any] f1,any f2}"); }
	@Test public void test_3063() { checkNotSubtype("{void f2,void f3}","{[any] f2,any f3}"); }
	@Test public void test_3064() { checkNotSubtype("{void f2,void f3}","{[null] f1}"); }
	@Test public void test_3065() { checkNotSubtype("{void f2,void f3}","{[null] f2}"); }
	@Test public void test_3066() { checkNotSubtype("{void f2,void f3}","{[null] f1,null f2}"); }
	@Test public void test_3067() { checkNotSubtype("{void f2,void f3}","{[null] f2,null f3}"); }
	@Test public void test_3068() { checkNotSubtype("{void f2,void f3}","{[int] f1}"); }
	@Test public void test_3069() { checkNotSubtype("{void f2,void f3}","{[int] f2}"); }
	@Test public void test_3070() { checkNotSubtype("{void f2,void f3}","{[int] f1,int f2}"); }
	@Test public void test_3071() { checkNotSubtype("{void f2,void f3}","{[int] f2,int f3}"); }
	@Test public void test_3072() { checkIsSubtype("{void f2,void f3}","{{void f1} f1}"); }
	@Test public void test_3073() { checkIsSubtype("{void f2,void f3}","{{void f2} f1}"); }
	@Test public void test_3074() { checkIsSubtype("{void f2,void f3}","{{void f1} f2}"); }
	@Test public void test_3075() { checkIsSubtype("{void f2,void f3}","{{void f2} f2}"); }
	@Test public void test_3076() { checkIsSubtype("{void f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_3077() { checkIsSubtype("{void f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_3078() { checkIsSubtype("{void f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_3079() { checkIsSubtype("{void f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_3080() { checkNotSubtype("{void f2,void f3}","{{any f1} f1}"); }
	@Test public void test_3081() { checkNotSubtype("{void f2,void f3}","{{any f2} f1}"); }
	@Test public void test_3082() { checkNotSubtype("{void f2,void f3}","{{any f1} f2}"); }
	@Test public void test_3083() { checkNotSubtype("{void f2,void f3}","{{any f2} f2}"); }
	@Test public void test_3084() { checkNotSubtype("{void f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_3085() { checkNotSubtype("{void f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_3086() { checkNotSubtype("{void f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_3087() { checkNotSubtype("{void f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_3088() { checkNotSubtype("{void f2,void f3}","{{null f1} f1}"); }
	@Test public void test_3089() { checkNotSubtype("{void f2,void f3}","{{null f2} f1}"); }
	@Test public void test_3090() { checkNotSubtype("{void f2,void f3}","{{null f1} f2}"); }
	@Test public void test_3091() { checkNotSubtype("{void f2,void f3}","{{null f2} f2}"); }
	@Test public void test_3092() { checkNotSubtype("{void f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3093() { checkNotSubtype("{void f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3094() { checkNotSubtype("{void f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3095() { checkNotSubtype("{void f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3096() { checkNotSubtype("{void f2,void f3}","{{int f1} f1}"); }
	@Test public void test_3097() { checkNotSubtype("{void f2,void f3}","{{int f2} f1}"); }
	@Test public void test_3098() { checkNotSubtype("{void f2,void f3}","{{int f1} f2}"); }
	@Test public void test_3099() { checkNotSubtype("{void f2,void f3}","{{int f2} f2}"); }
	@Test public void test_3100() { checkNotSubtype("{void f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3101() { checkNotSubtype("{void f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3102() { checkNotSubtype("{void f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3103() { checkNotSubtype("{void f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3104() { checkNotSubtype("{void f1,any f2}","any"); }
	@Test public void test_3105() { checkNotSubtype("{void f1,any f2}","null"); }
	@Test public void test_3106() { checkNotSubtype("{void f1,any f2}","int"); }
	@Test public void test_3107() { checkNotSubtype("{void f1,any f2}","[void]"); }
	@Test public void test_3108() { checkNotSubtype("{void f1,any f2}","[any]"); }
	@Test public void test_3109() { checkNotSubtype("{void f1,any f2}","[null]"); }
	@Test public void test_3110() { checkNotSubtype("{void f1,any f2}","[int]"); }
	@Test public void test_3111() { checkIsSubtype("{void f1,any f2}","{void f1}"); }
	@Test public void test_3112() { checkIsSubtype("{void f1,any f2}","{void f2}"); }
	@Test public void test_3113() { checkNotSubtype("{void f1,any f2}","{any f1}"); }
	@Test public void test_3114() { checkNotSubtype("{void f1,any f2}","{any f2}"); }
	@Test public void test_3115() { checkNotSubtype("{void f1,any f2}","{null f1}"); }
	@Test public void test_3116() { checkNotSubtype("{void f1,any f2}","{null f2}"); }
	@Test public void test_3117() { checkNotSubtype("{void f1,any f2}","{int f1}"); }
	@Test public void test_3118() { checkNotSubtype("{void f1,any f2}","{int f2}"); }
	@Test public void test_3119() { checkNotSubtype("{void f1,any f2}","[[void]]"); }
	@Test public void test_3120() { checkNotSubtype("{void f1,any f2}","[[any]]"); }
	@Test public void test_3121() { checkNotSubtype("{void f1,any f2}","[[null]]"); }
	@Test public void test_3122() { checkNotSubtype("{void f1,any f2}","[[int]]"); }
	@Test public void test_3123() { checkNotSubtype("{void f1,any f2}","[{void f1}]"); }
	@Test public void test_3124() { checkNotSubtype("{void f1,any f2}","[{void f2}]"); }
	@Test public void test_3125() { checkNotSubtype("{void f1,any f2}","[{any f1}]"); }
	@Test public void test_3126() { checkNotSubtype("{void f1,any f2}","[{any f2}]"); }
	@Test public void test_3127() { checkNotSubtype("{void f1,any f2}","[{null f1}]"); }
	@Test public void test_3128() { checkNotSubtype("{void f1,any f2}","[{null f2}]"); }
	@Test public void test_3129() { checkNotSubtype("{void f1,any f2}","[{int f1}]"); }
	@Test public void test_3130() { checkNotSubtype("{void f1,any f2}","[{int f2}]"); }
	@Test public void test_3131() { checkIsSubtype("{void f1,any f2}","{void f1,void f2}"); }
	@Test public void test_3132() { checkIsSubtype("{void f1,any f2}","{void f2,void f3}"); }
	@Test public void test_3133() { checkIsSubtype("{void f1,any f2}","{void f1,any f2}"); }
	@Test public void test_3134() { checkIsSubtype("{void f1,any f2}","{void f2,any f3}"); }
	@Test public void test_3135() { checkIsSubtype("{void f1,any f2}","{void f1,null f2}"); }
	@Test public void test_3136() { checkIsSubtype("{void f1,any f2}","{void f2,null f3}"); }
	@Test public void test_3137() { checkIsSubtype("{void f1,any f2}","{void f1,int f2}"); }
	@Test public void test_3138() { checkIsSubtype("{void f1,any f2}","{void f2,int f3}"); }
	@Test public void test_3139() { checkIsSubtype("{void f1,any f2}","{any f1,void f2}"); }
	@Test public void test_3140() { checkIsSubtype("{void f1,any f2}","{any f2,void f3}"); }
	@Test public void test_3141() { checkNotSubtype("{void f1,any f2}","{any f1,any f2}"); }
	@Test public void test_3142() { checkNotSubtype("{void f1,any f2}","{any f2,any f3}"); }
	@Test public void test_3143() { checkNotSubtype("{void f1,any f2}","{any f1,null f2}"); }
	@Test public void test_3144() { checkNotSubtype("{void f1,any f2}","{any f2,null f3}"); }
	@Test public void test_3145() { checkNotSubtype("{void f1,any f2}","{any f1,int f2}"); }
	@Test public void test_3146() { checkNotSubtype("{void f1,any f2}","{any f2,int f3}"); }
	@Test public void test_3147() { checkIsSubtype("{void f1,any f2}","{null f1,void f2}"); }
	@Test public void test_3148() { checkIsSubtype("{void f1,any f2}","{null f2,void f3}"); }
	@Test public void test_3149() { checkNotSubtype("{void f1,any f2}","{null f1,any f2}"); }
	@Test public void test_3150() { checkNotSubtype("{void f1,any f2}","{null f2,any f3}"); }
	@Test public void test_3151() { checkNotSubtype("{void f1,any f2}","{null f1,null f2}"); }
	@Test public void test_3152() { checkNotSubtype("{void f1,any f2}","{null f2,null f3}"); }
	@Test public void test_3153() { checkNotSubtype("{void f1,any f2}","{null f1,int f2}"); }
	@Test public void test_3154() { checkNotSubtype("{void f1,any f2}","{null f2,int f3}"); }
	@Test public void test_3155() { checkIsSubtype("{void f1,any f2}","{int f1,void f2}"); }
	@Test public void test_3156() { checkIsSubtype("{void f1,any f2}","{int f2,void f3}"); }
	@Test public void test_3157() { checkNotSubtype("{void f1,any f2}","{int f1,any f2}"); }
	@Test public void test_3158() { checkNotSubtype("{void f1,any f2}","{int f2,any f3}"); }
	@Test public void test_3159() { checkNotSubtype("{void f1,any f2}","{int f1,null f2}"); }
	@Test public void test_3160() { checkNotSubtype("{void f1,any f2}","{int f2,null f3}"); }
	@Test public void test_3161() { checkNotSubtype("{void f1,any f2}","{int f1,int f2}"); }
	@Test public void test_3162() { checkNotSubtype("{void f1,any f2}","{int f2,int f3}"); }
	@Test public void test_3163() { checkNotSubtype("{void f1,any f2}","{[void] f1}"); }
	@Test public void test_3164() { checkNotSubtype("{void f1,any f2}","{[void] f2}"); }
	@Test public void test_3165() { checkIsSubtype("{void f1,any f2}","{[void] f1,void f2}"); }
	@Test public void test_3166() { checkIsSubtype("{void f1,any f2}","{[void] f2,void f3}"); }
	@Test public void test_3167() { checkNotSubtype("{void f1,any f2}","{[any] f1}"); }
	@Test public void test_3168() { checkNotSubtype("{void f1,any f2}","{[any] f2}"); }
	@Test public void test_3169() { checkNotSubtype("{void f1,any f2}","{[any] f1,any f2}"); }
	@Test public void test_3170() { checkNotSubtype("{void f1,any f2}","{[any] f2,any f3}"); }
	@Test public void test_3171() { checkNotSubtype("{void f1,any f2}","{[null] f1}"); }
	@Test public void test_3172() { checkNotSubtype("{void f1,any f2}","{[null] f2}"); }
	@Test public void test_3173() { checkNotSubtype("{void f1,any f2}","{[null] f1,null f2}"); }
	@Test public void test_3174() { checkNotSubtype("{void f1,any f2}","{[null] f2,null f3}"); }
	@Test public void test_3175() { checkNotSubtype("{void f1,any f2}","{[int] f1}"); }
	@Test public void test_3176() { checkNotSubtype("{void f1,any f2}","{[int] f2}"); }
	@Test public void test_3177() { checkNotSubtype("{void f1,any f2}","{[int] f1,int f2}"); }
	@Test public void test_3178() { checkNotSubtype("{void f1,any f2}","{[int] f2,int f3}"); }
	@Test public void test_3179() { checkIsSubtype("{void f1,any f2}","{{void f1} f1}"); }
	@Test public void test_3180() { checkIsSubtype("{void f1,any f2}","{{void f2} f1}"); }
	@Test public void test_3181() { checkIsSubtype("{void f1,any f2}","{{void f1} f2}"); }
	@Test public void test_3182() { checkIsSubtype("{void f1,any f2}","{{void f2} f2}"); }
	@Test public void test_3183() { checkIsSubtype("{void f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_3184() { checkIsSubtype("{void f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_3185() { checkIsSubtype("{void f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_3186() { checkIsSubtype("{void f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_3187() { checkNotSubtype("{void f1,any f2}","{{any f1} f1}"); }
	@Test public void test_3188() { checkNotSubtype("{void f1,any f2}","{{any f2} f1}"); }
	@Test public void test_3189() { checkNotSubtype("{void f1,any f2}","{{any f1} f2}"); }
	@Test public void test_3190() { checkNotSubtype("{void f1,any f2}","{{any f2} f2}"); }
	@Test public void test_3191() { checkNotSubtype("{void f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_3192() { checkNotSubtype("{void f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_3193() { checkNotSubtype("{void f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_3194() { checkNotSubtype("{void f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_3195() { checkNotSubtype("{void f1,any f2}","{{null f1} f1}"); }
	@Test public void test_3196() { checkNotSubtype("{void f1,any f2}","{{null f2} f1}"); }
	@Test public void test_3197() { checkNotSubtype("{void f1,any f2}","{{null f1} f2}"); }
	@Test public void test_3198() { checkNotSubtype("{void f1,any f2}","{{null f2} f2}"); }
	@Test public void test_3199() { checkNotSubtype("{void f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3200() { checkNotSubtype("{void f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3201() { checkNotSubtype("{void f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3202() { checkNotSubtype("{void f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3203() { checkNotSubtype("{void f1,any f2}","{{int f1} f1}"); }
	@Test public void test_3204() { checkNotSubtype("{void f1,any f2}","{{int f2} f1}"); }
	@Test public void test_3205() { checkNotSubtype("{void f1,any f2}","{{int f1} f2}"); }
	@Test public void test_3206() { checkNotSubtype("{void f1,any f2}","{{int f2} f2}"); }
	@Test public void test_3207() { checkNotSubtype("{void f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3208() { checkNotSubtype("{void f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3209() { checkNotSubtype("{void f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3210() { checkNotSubtype("{void f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3211() { checkNotSubtype("{void f2,any f3}","any"); }
	@Test public void test_3212() { checkNotSubtype("{void f2,any f3}","null"); }
	@Test public void test_3213() { checkNotSubtype("{void f2,any f3}","int"); }
	@Test public void test_3214() { checkNotSubtype("{void f2,any f3}","[void]"); }
	@Test public void test_3215() { checkNotSubtype("{void f2,any f3}","[any]"); }
	@Test public void test_3216() { checkNotSubtype("{void f2,any f3}","[null]"); }
	@Test public void test_3217() { checkNotSubtype("{void f2,any f3}","[int]"); }
	@Test public void test_3218() { checkIsSubtype("{void f2,any f3}","{void f1}"); }
	@Test public void test_3219() { checkIsSubtype("{void f2,any f3}","{void f2}"); }
	@Test public void test_3220() { checkNotSubtype("{void f2,any f3}","{any f1}"); }
	@Test public void test_3221() { checkNotSubtype("{void f2,any f3}","{any f2}"); }
	@Test public void test_3222() { checkNotSubtype("{void f2,any f3}","{null f1}"); }
	@Test public void test_3223() { checkNotSubtype("{void f2,any f3}","{null f2}"); }
	@Test public void test_3224() { checkNotSubtype("{void f2,any f3}","{int f1}"); }
	@Test public void test_3225() { checkNotSubtype("{void f2,any f3}","{int f2}"); }
	@Test public void test_3226() { checkNotSubtype("{void f2,any f3}","[[void]]"); }
	@Test public void test_3227() { checkNotSubtype("{void f2,any f3}","[[any]]"); }
	@Test public void test_3228() { checkNotSubtype("{void f2,any f3}","[[null]]"); }
	@Test public void test_3229() { checkNotSubtype("{void f2,any f3}","[[int]]"); }
	@Test public void test_3230() { checkNotSubtype("{void f2,any f3}","[{void f1}]"); }
	@Test public void test_3231() { checkNotSubtype("{void f2,any f3}","[{void f2}]"); }
	@Test public void test_3232() { checkNotSubtype("{void f2,any f3}","[{any f1}]"); }
	@Test public void test_3233() { checkNotSubtype("{void f2,any f3}","[{any f2}]"); }
	@Test public void test_3234() { checkNotSubtype("{void f2,any f3}","[{null f1}]"); }
	@Test public void test_3235() { checkNotSubtype("{void f2,any f3}","[{null f2}]"); }
	@Test public void test_3236() { checkNotSubtype("{void f2,any f3}","[{int f1}]"); }
	@Test public void test_3237() { checkNotSubtype("{void f2,any f3}","[{int f2}]"); }
	@Test public void test_3238() { checkIsSubtype("{void f2,any f3}","{void f1,void f2}"); }
	@Test public void test_3239() { checkIsSubtype("{void f2,any f3}","{void f2,void f3}"); }
	@Test public void test_3240() { checkIsSubtype("{void f2,any f3}","{void f1,any f2}"); }
	@Test public void test_3241() { checkIsSubtype("{void f2,any f3}","{void f2,any f3}"); }
	@Test public void test_3242() { checkIsSubtype("{void f2,any f3}","{void f1,null f2}"); }
	@Test public void test_3243() { checkIsSubtype("{void f2,any f3}","{void f2,null f3}"); }
	@Test public void test_3244() { checkIsSubtype("{void f2,any f3}","{void f1,int f2}"); }
	@Test public void test_3245() { checkIsSubtype("{void f2,any f3}","{void f2,int f3}"); }
	@Test public void test_3246() { checkIsSubtype("{void f2,any f3}","{any f1,void f2}"); }
	@Test public void test_3247() { checkIsSubtype("{void f2,any f3}","{any f2,void f3}"); }
	@Test public void test_3248() { checkNotSubtype("{void f2,any f3}","{any f1,any f2}"); }
	@Test public void test_3249() { checkNotSubtype("{void f2,any f3}","{any f2,any f3}"); }
	@Test public void test_3250() { checkNotSubtype("{void f2,any f3}","{any f1,null f2}"); }
	@Test public void test_3251() { checkNotSubtype("{void f2,any f3}","{any f2,null f3}"); }
	@Test public void test_3252() { checkNotSubtype("{void f2,any f3}","{any f1,int f2}"); }
	@Test public void test_3253() { checkNotSubtype("{void f2,any f3}","{any f2,int f3}"); }
	@Test public void test_3254() { checkIsSubtype("{void f2,any f3}","{null f1,void f2}"); }
	@Test public void test_3255() { checkIsSubtype("{void f2,any f3}","{null f2,void f3}"); }
	@Test public void test_3256() { checkNotSubtype("{void f2,any f3}","{null f1,any f2}"); }
	@Test public void test_3257() { checkNotSubtype("{void f2,any f3}","{null f2,any f3}"); }
	@Test public void test_3258() { checkNotSubtype("{void f2,any f3}","{null f1,null f2}"); }
	@Test public void test_3259() { checkNotSubtype("{void f2,any f3}","{null f2,null f3}"); }
	@Test public void test_3260() { checkNotSubtype("{void f2,any f3}","{null f1,int f2}"); }
	@Test public void test_3261() { checkNotSubtype("{void f2,any f3}","{null f2,int f3}"); }
	@Test public void test_3262() { checkIsSubtype("{void f2,any f3}","{int f1,void f2}"); }
	@Test public void test_3263() { checkIsSubtype("{void f2,any f3}","{int f2,void f3}"); }
	@Test public void test_3264() { checkNotSubtype("{void f2,any f3}","{int f1,any f2}"); }
	@Test public void test_3265() { checkNotSubtype("{void f2,any f3}","{int f2,any f3}"); }
	@Test public void test_3266() { checkNotSubtype("{void f2,any f3}","{int f1,null f2}"); }
	@Test public void test_3267() { checkNotSubtype("{void f2,any f3}","{int f2,null f3}"); }
	@Test public void test_3268() { checkNotSubtype("{void f2,any f3}","{int f1,int f2}"); }
	@Test public void test_3269() { checkNotSubtype("{void f2,any f3}","{int f2,int f3}"); }
	@Test public void test_3270() { checkNotSubtype("{void f2,any f3}","{[void] f1}"); }
	@Test public void test_3271() { checkNotSubtype("{void f2,any f3}","{[void] f2}"); }
	@Test public void test_3272() { checkIsSubtype("{void f2,any f3}","{[void] f1,void f2}"); }
	@Test public void test_3273() { checkIsSubtype("{void f2,any f3}","{[void] f2,void f3}"); }
	@Test public void test_3274() { checkNotSubtype("{void f2,any f3}","{[any] f1}"); }
	@Test public void test_3275() { checkNotSubtype("{void f2,any f3}","{[any] f2}"); }
	@Test public void test_3276() { checkNotSubtype("{void f2,any f3}","{[any] f1,any f2}"); }
	@Test public void test_3277() { checkNotSubtype("{void f2,any f3}","{[any] f2,any f3}"); }
	@Test public void test_3278() { checkNotSubtype("{void f2,any f3}","{[null] f1}"); }
	@Test public void test_3279() { checkNotSubtype("{void f2,any f3}","{[null] f2}"); }
	@Test public void test_3280() { checkNotSubtype("{void f2,any f3}","{[null] f1,null f2}"); }
	@Test public void test_3281() { checkNotSubtype("{void f2,any f3}","{[null] f2,null f3}"); }
	@Test public void test_3282() { checkNotSubtype("{void f2,any f3}","{[int] f1}"); }
	@Test public void test_3283() { checkNotSubtype("{void f2,any f3}","{[int] f2}"); }
	@Test public void test_3284() { checkNotSubtype("{void f2,any f3}","{[int] f1,int f2}"); }
	@Test public void test_3285() { checkNotSubtype("{void f2,any f3}","{[int] f2,int f3}"); }
	@Test public void test_3286() { checkIsSubtype("{void f2,any f3}","{{void f1} f1}"); }
	@Test public void test_3287() { checkIsSubtype("{void f2,any f3}","{{void f2} f1}"); }
	@Test public void test_3288() { checkIsSubtype("{void f2,any f3}","{{void f1} f2}"); }
	@Test public void test_3289() { checkIsSubtype("{void f2,any f3}","{{void f2} f2}"); }
	@Test public void test_3290() { checkIsSubtype("{void f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_3291() { checkIsSubtype("{void f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_3292() { checkIsSubtype("{void f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_3293() { checkIsSubtype("{void f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_3294() { checkNotSubtype("{void f2,any f3}","{{any f1} f1}"); }
	@Test public void test_3295() { checkNotSubtype("{void f2,any f3}","{{any f2} f1}"); }
	@Test public void test_3296() { checkNotSubtype("{void f2,any f3}","{{any f1} f2}"); }
	@Test public void test_3297() { checkNotSubtype("{void f2,any f3}","{{any f2} f2}"); }
	@Test public void test_3298() { checkNotSubtype("{void f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_3299() { checkNotSubtype("{void f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_3300() { checkNotSubtype("{void f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_3301() { checkNotSubtype("{void f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_3302() { checkNotSubtype("{void f2,any f3}","{{null f1} f1}"); }
	@Test public void test_3303() { checkNotSubtype("{void f2,any f3}","{{null f2} f1}"); }
	@Test public void test_3304() { checkNotSubtype("{void f2,any f3}","{{null f1} f2}"); }
	@Test public void test_3305() { checkNotSubtype("{void f2,any f3}","{{null f2} f2}"); }
	@Test public void test_3306() { checkNotSubtype("{void f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3307() { checkNotSubtype("{void f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3308() { checkNotSubtype("{void f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3309() { checkNotSubtype("{void f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3310() { checkNotSubtype("{void f2,any f3}","{{int f1} f1}"); }
	@Test public void test_3311() { checkNotSubtype("{void f2,any f3}","{{int f2} f1}"); }
	@Test public void test_3312() { checkNotSubtype("{void f2,any f3}","{{int f1} f2}"); }
	@Test public void test_3313() { checkNotSubtype("{void f2,any f3}","{{int f2} f2}"); }
	@Test public void test_3314() { checkNotSubtype("{void f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3315() { checkNotSubtype("{void f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3316() { checkNotSubtype("{void f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3317() { checkNotSubtype("{void f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3318() { checkNotSubtype("{void f1,null f2}","any"); }
	@Test public void test_3319() { checkNotSubtype("{void f1,null f2}","null"); }
	@Test public void test_3320() { checkNotSubtype("{void f1,null f2}","int"); }
	@Test public void test_3321() { checkNotSubtype("{void f1,null f2}","[void]"); }
	@Test public void test_3322() { checkNotSubtype("{void f1,null f2}","[any]"); }
	@Test public void test_3323() { checkNotSubtype("{void f1,null f2}","[null]"); }
	@Test public void test_3324() { checkNotSubtype("{void f1,null f2}","[int]"); }
	@Test public void test_3325() { checkIsSubtype("{void f1,null f2}","{void f1}"); }
	@Test public void test_3326() { checkIsSubtype("{void f1,null f2}","{void f2}"); }
	@Test public void test_3327() { checkNotSubtype("{void f1,null f2}","{any f1}"); }
	@Test public void test_3328() { checkNotSubtype("{void f1,null f2}","{any f2}"); }
	@Test public void test_3329() { checkNotSubtype("{void f1,null f2}","{null f1}"); }
	@Test public void test_3330() { checkNotSubtype("{void f1,null f2}","{null f2}"); }
	@Test public void test_3331() { checkNotSubtype("{void f1,null f2}","{int f1}"); }
	@Test public void test_3332() { checkNotSubtype("{void f1,null f2}","{int f2}"); }
	@Test public void test_3333() { checkNotSubtype("{void f1,null f2}","[[void]]"); }
	@Test public void test_3334() { checkNotSubtype("{void f1,null f2}","[[any]]"); }
	@Test public void test_3335() { checkNotSubtype("{void f1,null f2}","[[null]]"); }
	@Test public void test_3336() { checkNotSubtype("{void f1,null f2}","[[int]]"); }
	@Test public void test_3337() { checkNotSubtype("{void f1,null f2}","[{void f1}]"); }
	@Test public void test_3338() { checkNotSubtype("{void f1,null f2}","[{void f2}]"); }
	@Test public void test_3339() { checkNotSubtype("{void f1,null f2}","[{any f1}]"); }
	@Test public void test_3340() { checkNotSubtype("{void f1,null f2}","[{any f2}]"); }
	@Test public void test_3341() { checkNotSubtype("{void f1,null f2}","[{null f1}]"); }
	@Test public void test_3342() { checkNotSubtype("{void f1,null f2}","[{null f2}]"); }
	@Test public void test_3343() { checkNotSubtype("{void f1,null f2}","[{int f1}]"); }
	@Test public void test_3344() { checkNotSubtype("{void f1,null f2}","[{int f2}]"); }
	@Test public void test_3345() { checkIsSubtype("{void f1,null f2}","{void f1,void f2}"); }
	@Test public void test_3346() { checkIsSubtype("{void f1,null f2}","{void f2,void f3}"); }
	@Test public void test_3347() { checkIsSubtype("{void f1,null f2}","{void f1,any f2}"); }
	@Test public void test_3348() { checkIsSubtype("{void f1,null f2}","{void f2,any f3}"); }
	@Test public void test_3349() { checkIsSubtype("{void f1,null f2}","{void f1,null f2}"); }
	@Test public void test_3350() { checkIsSubtype("{void f1,null f2}","{void f2,null f3}"); }
	@Test public void test_3351() { checkIsSubtype("{void f1,null f2}","{void f1,int f2}"); }
	@Test public void test_3352() { checkIsSubtype("{void f1,null f2}","{void f2,int f3}"); }
	@Test public void test_3353() { checkIsSubtype("{void f1,null f2}","{any f1,void f2}"); }
	@Test public void test_3354() { checkIsSubtype("{void f1,null f2}","{any f2,void f3}"); }
	@Test public void test_3355() { checkNotSubtype("{void f1,null f2}","{any f1,any f2}"); }
	@Test public void test_3356() { checkNotSubtype("{void f1,null f2}","{any f2,any f3}"); }
	@Test public void test_3357() { checkNotSubtype("{void f1,null f2}","{any f1,null f2}"); }
	@Test public void test_3358() { checkNotSubtype("{void f1,null f2}","{any f2,null f3}"); }
	@Test public void test_3359() { checkNotSubtype("{void f1,null f2}","{any f1,int f2}"); }
	@Test public void test_3360() { checkNotSubtype("{void f1,null f2}","{any f2,int f3}"); }
	@Test public void test_3361() { checkIsSubtype("{void f1,null f2}","{null f1,void f2}"); }
	@Test public void test_3362() { checkIsSubtype("{void f1,null f2}","{null f2,void f3}"); }
	@Test public void test_3363() { checkNotSubtype("{void f1,null f2}","{null f1,any f2}"); }
	@Test public void test_3364() { checkNotSubtype("{void f1,null f2}","{null f2,any f3}"); }
	@Test public void test_3365() { checkNotSubtype("{void f1,null f2}","{null f1,null f2}"); }
	@Test public void test_3366() { checkNotSubtype("{void f1,null f2}","{null f2,null f3}"); }
	@Test public void test_3367() { checkNotSubtype("{void f1,null f2}","{null f1,int f2}"); }
	@Test public void test_3368() { checkNotSubtype("{void f1,null f2}","{null f2,int f3}"); }
	@Test public void test_3369() { checkIsSubtype("{void f1,null f2}","{int f1,void f2}"); }
	@Test public void test_3370() { checkIsSubtype("{void f1,null f2}","{int f2,void f3}"); }
	@Test public void test_3371() { checkNotSubtype("{void f1,null f2}","{int f1,any f2}"); }
	@Test public void test_3372() { checkNotSubtype("{void f1,null f2}","{int f2,any f3}"); }
	@Test public void test_3373() { checkNotSubtype("{void f1,null f2}","{int f1,null f2}"); }
	@Test public void test_3374() { checkNotSubtype("{void f1,null f2}","{int f2,null f3}"); }
	@Test public void test_3375() { checkNotSubtype("{void f1,null f2}","{int f1,int f2}"); }
	@Test public void test_3376() { checkNotSubtype("{void f1,null f2}","{int f2,int f3}"); }
	@Test public void test_3377() { checkNotSubtype("{void f1,null f2}","{[void] f1}"); }
	@Test public void test_3378() { checkNotSubtype("{void f1,null f2}","{[void] f2}"); }
	@Test public void test_3379() { checkIsSubtype("{void f1,null f2}","{[void] f1,void f2}"); }
	@Test public void test_3380() { checkIsSubtype("{void f1,null f2}","{[void] f2,void f3}"); }
	@Test public void test_3381() { checkNotSubtype("{void f1,null f2}","{[any] f1}"); }
	@Test public void test_3382() { checkNotSubtype("{void f1,null f2}","{[any] f2}"); }
	@Test public void test_3383() { checkNotSubtype("{void f1,null f2}","{[any] f1,any f2}"); }
	@Test public void test_3384() { checkNotSubtype("{void f1,null f2}","{[any] f2,any f3}"); }
	@Test public void test_3385() { checkNotSubtype("{void f1,null f2}","{[null] f1}"); }
	@Test public void test_3386() { checkNotSubtype("{void f1,null f2}","{[null] f2}"); }
	@Test public void test_3387() { checkNotSubtype("{void f1,null f2}","{[null] f1,null f2}"); }
	@Test public void test_3388() { checkNotSubtype("{void f1,null f2}","{[null] f2,null f3}"); }
	@Test public void test_3389() { checkNotSubtype("{void f1,null f2}","{[int] f1}"); }
	@Test public void test_3390() { checkNotSubtype("{void f1,null f2}","{[int] f2}"); }
	@Test public void test_3391() { checkNotSubtype("{void f1,null f2}","{[int] f1,int f2}"); }
	@Test public void test_3392() { checkNotSubtype("{void f1,null f2}","{[int] f2,int f3}"); }
	@Test public void test_3393() { checkIsSubtype("{void f1,null f2}","{{void f1} f1}"); }
	@Test public void test_3394() { checkIsSubtype("{void f1,null f2}","{{void f2} f1}"); }
	@Test public void test_3395() { checkIsSubtype("{void f1,null f2}","{{void f1} f2}"); }
	@Test public void test_3396() { checkIsSubtype("{void f1,null f2}","{{void f2} f2}"); }
	@Test public void test_3397() { checkIsSubtype("{void f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_3398() { checkIsSubtype("{void f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_3399() { checkIsSubtype("{void f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_3400() { checkIsSubtype("{void f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_3401() { checkNotSubtype("{void f1,null f2}","{{any f1} f1}"); }
	@Test public void test_3402() { checkNotSubtype("{void f1,null f2}","{{any f2} f1}"); }
	@Test public void test_3403() { checkNotSubtype("{void f1,null f2}","{{any f1} f2}"); }
	@Test public void test_3404() { checkNotSubtype("{void f1,null f2}","{{any f2} f2}"); }
	@Test public void test_3405() { checkNotSubtype("{void f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_3406() { checkNotSubtype("{void f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_3407() { checkNotSubtype("{void f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_3408() { checkNotSubtype("{void f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_3409() { checkNotSubtype("{void f1,null f2}","{{null f1} f1}"); }
	@Test public void test_3410() { checkNotSubtype("{void f1,null f2}","{{null f2} f1}"); }
	@Test public void test_3411() { checkNotSubtype("{void f1,null f2}","{{null f1} f2}"); }
	@Test public void test_3412() { checkNotSubtype("{void f1,null f2}","{{null f2} f2}"); }
	@Test public void test_3413() { checkNotSubtype("{void f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3414() { checkNotSubtype("{void f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3415() { checkNotSubtype("{void f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3416() { checkNotSubtype("{void f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3417() { checkNotSubtype("{void f1,null f2}","{{int f1} f1}"); }
	@Test public void test_3418() { checkNotSubtype("{void f1,null f2}","{{int f2} f1}"); }
	@Test public void test_3419() { checkNotSubtype("{void f1,null f2}","{{int f1} f2}"); }
	@Test public void test_3420() { checkNotSubtype("{void f1,null f2}","{{int f2} f2}"); }
	@Test public void test_3421() { checkNotSubtype("{void f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3422() { checkNotSubtype("{void f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3423() { checkNotSubtype("{void f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3424() { checkNotSubtype("{void f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3425() { checkNotSubtype("{void f2,null f3}","any"); }
	@Test public void test_3426() { checkNotSubtype("{void f2,null f3}","null"); }
	@Test public void test_3427() { checkNotSubtype("{void f2,null f3}","int"); }
	@Test public void test_3428() { checkNotSubtype("{void f2,null f3}","[void]"); }
	@Test public void test_3429() { checkNotSubtype("{void f2,null f3}","[any]"); }
	@Test public void test_3430() { checkNotSubtype("{void f2,null f3}","[null]"); }
	@Test public void test_3431() { checkNotSubtype("{void f2,null f3}","[int]"); }
	@Test public void test_3432() { checkIsSubtype("{void f2,null f3}","{void f1}"); }
	@Test public void test_3433() { checkIsSubtype("{void f2,null f3}","{void f2}"); }
	@Test public void test_3434() { checkNotSubtype("{void f2,null f3}","{any f1}"); }
	@Test public void test_3435() { checkNotSubtype("{void f2,null f3}","{any f2}"); }
	@Test public void test_3436() { checkNotSubtype("{void f2,null f3}","{null f1}"); }
	@Test public void test_3437() { checkNotSubtype("{void f2,null f3}","{null f2}"); }
	@Test public void test_3438() { checkNotSubtype("{void f2,null f3}","{int f1}"); }
	@Test public void test_3439() { checkNotSubtype("{void f2,null f3}","{int f2}"); }
	@Test public void test_3440() { checkNotSubtype("{void f2,null f3}","[[void]]"); }
	@Test public void test_3441() { checkNotSubtype("{void f2,null f3}","[[any]]"); }
	@Test public void test_3442() { checkNotSubtype("{void f2,null f3}","[[null]]"); }
	@Test public void test_3443() { checkNotSubtype("{void f2,null f3}","[[int]]"); }
	@Test public void test_3444() { checkNotSubtype("{void f2,null f3}","[{void f1}]"); }
	@Test public void test_3445() { checkNotSubtype("{void f2,null f3}","[{void f2}]"); }
	@Test public void test_3446() { checkNotSubtype("{void f2,null f3}","[{any f1}]"); }
	@Test public void test_3447() { checkNotSubtype("{void f2,null f3}","[{any f2}]"); }
	@Test public void test_3448() { checkNotSubtype("{void f2,null f3}","[{null f1}]"); }
	@Test public void test_3449() { checkNotSubtype("{void f2,null f3}","[{null f2}]"); }
	@Test public void test_3450() { checkNotSubtype("{void f2,null f3}","[{int f1}]"); }
	@Test public void test_3451() { checkNotSubtype("{void f2,null f3}","[{int f2}]"); }
	@Test public void test_3452() { checkIsSubtype("{void f2,null f3}","{void f1,void f2}"); }
	@Test public void test_3453() { checkIsSubtype("{void f2,null f3}","{void f2,void f3}"); }
	@Test public void test_3454() { checkIsSubtype("{void f2,null f3}","{void f1,any f2}"); }
	@Test public void test_3455() { checkIsSubtype("{void f2,null f3}","{void f2,any f3}"); }
	@Test public void test_3456() { checkIsSubtype("{void f2,null f3}","{void f1,null f2}"); }
	@Test public void test_3457() { checkIsSubtype("{void f2,null f3}","{void f2,null f3}"); }
	@Test public void test_3458() { checkIsSubtype("{void f2,null f3}","{void f1,int f2}"); }
	@Test public void test_3459() { checkIsSubtype("{void f2,null f3}","{void f2,int f3}"); }
	@Test public void test_3460() { checkIsSubtype("{void f2,null f3}","{any f1,void f2}"); }
	@Test public void test_3461() { checkIsSubtype("{void f2,null f3}","{any f2,void f3}"); }
	@Test public void test_3462() { checkNotSubtype("{void f2,null f3}","{any f1,any f2}"); }
	@Test public void test_3463() { checkNotSubtype("{void f2,null f3}","{any f2,any f3}"); }
	@Test public void test_3464() { checkNotSubtype("{void f2,null f3}","{any f1,null f2}"); }
	@Test public void test_3465() { checkNotSubtype("{void f2,null f3}","{any f2,null f3}"); }
	@Test public void test_3466() { checkNotSubtype("{void f2,null f3}","{any f1,int f2}"); }
	@Test public void test_3467() { checkNotSubtype("{void f2,null f3}","{any f2,int f3}"); }
	@Test public void test_3468() { checkIsSubtype("{void f2,null f3}","{null f1,void f2}"); }
	@Test public void test_3469() { checkIsSubtype("{void f2,null f3}","{null f2,void f3}"); }
	@Test public void test_3470() { checkNotSubtype("{void f2,null f3}","{null f1,any f2}"); }
	@Test public void test_3471() { checkNotSubtype("{void f2,null f3}","{null f2,any f3}"); }
	@Test public void test_3472() { checkNotSubtype("{void f2,null f3}","{null f1,null f2}"); }
	@Test public void test_3473() { checkNotSubtype("{void f2,null f3}","{null f2,null f3}"); }
	@Test public void test_3474() { checkNotSubtype("{void f2,null f3}","{null f1,int f2}"); }
	@Test public void test_3475() { checkNotSubtype("{void f2,null f3}","{null f2,int f3}"); }
	@Test public void test_3476() { checkIsSubtype("{void f2,null f3}","{int f1,void f2}"); }
	@Test public void test_3477() { checkIsSubtype("{void f2,null f3}","{int f2,void f3}"); }
	@Test public void test_3478() { checkNotSubtype("{void f2,null f3}","{int f1,any f2}"); }
	@Test public void test_3479() { checkNotSubtype("{void f2,null f3}","{int f2,any f3}"); }
	@Test public void test_3480() { checkNotSubtype("{void f2,null f3}","{int f1,null f2}"); }
	@Test public void test_3481() { checkNotSubtype("{void f2,null f3}","{int f2,null f3}"); }
	@Test public void test_3482() { checkNotSubtype("{void f2,null f3}","{int f1,int f2}"); }
	@Test public void test_3483() { checkNotSubtype("{void f2,null f3}","{int f2,int f3}"); }
	@Test public void test_3484() { checkNotSubtype("{void f2,null f3}","{[void] f1}"); }
	@Test public void test_3485() { checkNotSubtype("{void f2,null f3}","{[void] f2}"); }
	@Test public void test_3486() { checkIsSubtype("{void f2,null f3}","{[void] f1,void f2}"); }
	@Test public void test_3487() { checkIsSubtype("{void f2,null f3}","{[void] f2,void f3}"); }
	@Test public void test_3488() { checkNotSubtype("{void f2,null f3}","{[any] f1}"); }
	@Test public void test_3489() { checkNotSubtype("{void f2,null f3}","{[any] f2}"); }
	@Test public void test_3490() { checkNotSubtype("{void f2,null f3}","{[any] f1,any f2}"); }
	@Test public void test_3491() { checkNotSubtype("{void f2,null f3}","{[any] f2,any f3}"); }
	@Test public void test_3492() { checkNotSubtype("{void f2,null f3}","{[null] f1}"); }
	@Test public void test_3493() { checkNotSubtype("{void f2,null f3}","{[null] f2}"); }
	@Test public void test_3494() { checkNotSubtype("{void f2,null f3}","{[null] f1,null f2}"); }
	@Test public void test_3495() { checkNotSubtype("{void f2,null f3}","{[null] f2,null f3}"); }
	@Test public void test_3496() { checkNotSubtype("{void f2,null f3}","{[int] f1}"); }
	@Test public void test_3497() { checkNotSubtype("{void f2,null f3}","{[int] f2}"); }
	@Test public void test_3498() { checkNotSubtype("{void f2,null f3}","{[int] f1,int f2}"); }
	@Test public void test_3499() { checkNotSubtype("{void f2,null f3}","{[int] f2,int f3}"); }
	@Test public void test_3500() { checkIsSubtype("{void f2,null f3}","{{void f1} f1}"); }
	@Test public void test_3501() { checkIsSubtype("{void f2,null f3}","{{void f2} f1}"); }
	@Test public void test_3502() { checkIsSubtype("{void f2,null f3}","{{void f1} f2}"); }
	@Test public void test_3503() { checkIsSubtype("{void f2,null f3}","{{void f2} f2}"); }
	@Test public void test_3504() { checkIsSubtype("{void f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_3505() { checkIsSubtype("{void f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_3506() { checkIsSubtype("{void f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_3507() { checkIsSubtype("{void f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_3508() { checkNotSubtype("{void f2,null f3}","{{any f1} f1}"); }
	@Test public void test_3509() { checkNotSubtype("{void f2,null f3}","{{any f2} f1}"); }
	@Test public void test_3510() { checkNotSubtype("{void f2,null f3}","{{any f1} f2}"); }
	@Test public void test_3511() { checkNotSubtype("{void f2,null f3}","{{any f2} f2}"); }
	@Test public void test_3512() { checkNotSubtype("{void f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_3513() { checkNotSubtype("{void f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_3514() { checkNotSubtype("{void f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_3515() { checkNotSubtype("{void f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_3516() { checkNotSubtype("{void f2,null f3}","{{null f1} f1}"); }
	@Test public void test_3517() { checkNotSubtype("{void f2,null f3}","{{null f2} f1}"); }
	@Test public void test_3518() { checkNotSubtype("{void f2,null f3}","{{null f1} f2}"); }
	@Test public void test_3519() { checkNotSubtype("{void f2,null f3}","{{null f2} f2}"); }
	@Test public void test_3520() { checkNotSubtype("{void f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3521() { checkNotSubtype("{void f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3522() { checkNotSubtype("{void f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3523() { checkNotSubtype("{void f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3524() { checkNotSubtype("{void f2,null f3}","{{int f1} f1}"); }
	@Test public void test_3525() { checkNotSubtype("{void f2,null f3}","{{int f2} f1}"); }
	@Test public void test_3526() { checkNotSubtype("{void f2,null f3}","{{int f1} f2}"); }
	@Test public void test_3527() { checkNotSubtype("{void f2,null f3}","{{int f2} f2}"); }
	@Test public void test_3528() { checkNotSubtype("{void f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3529() { checkNotSubtype("{void f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3530() { checkNotSubtype("{void f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3531() { checkNotSubtype("{void f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3532() { checkNotSubtype("{void f1,int f2}","any"); }
	@Test public void test_3533() { checkNotSubtype("{void f1,int f2}","null"); }
	@Test public void test_3534() { checkNotSubtype("{void f1,int f2}","int"); }
	@Test public void test_3535() { checkNotSubtype("{void f1,int f2}","[void]"); }
	@Test public void test_3536() { checkNotSubtype("{void f1,int f2}","[any]"); }
	@Test public void test_3537() { checkNotSubtype("{void f1,int f2}","[null]"); }
	@Test public void test_3538() { checkNotSubtype("{void f1,int f2}","[int]"); }
	@Test public void test_3539() { checkIsSubtype("{void f1,int f2}","{void f1}"); }
	@Test public void test_3540() { checkIsSubtype("{void f1,int f2}","{void f2}"); }
	@Test public void test_3541() { checkNotSubtype("{void f1,int f2}","{any f1}"); }
	@Test public void test_3542() { checkNotSubtype("{void f1,int f2}","{any f2}"); }
	@Test public void test_3543() { checkNotSubtype("{void f1,int f2}","{null f1}"); }
	@Test public void test_3544() { checkNotSubtype("{void f1,int f2}","{null f2}"); }
	@Test public void test_3545() { checkNotSubtype("{void f1,int f2}","{int f1}"); }
	@Test public void test_3546() { checkNotSubtype("{void f1,int f2}","{int f2}"); }
	@Test public void test_3547() { checkNotSubtype("{void f1,int f2}","[[void]]"); }
	@Test public void test_3548() { checkNotSubtype("{void f1,int f2}","[[any]]"); }
	@Test public void test_3549() { checkNotSubtype("{void f1,int f2}","[[null]]"); }
	@Test public void test_3550() { checkNotSubtype("{void f1,int f2}","[[int]]"); }
	@Test public void test_3551() { checkNotSubtype("{void f1,int f2}","[{void f1}]"); }
	@Test public void test_3552() { checkNotSubtype("{void f1,int f2}","[{void f2}]"); }
	@Test public void test_3553() { checkNotSubtype("{void f1,int f2}","[{any f1}]"); }
	@Test public void test_3554() { checkNotSubtype("{void f1,int f2}","[{any f2}]"); }
	@Test public void test_3555() { checkNotSubtype("{void f1,int f2}","[{null f1}]"); }
	@Test public void test_3556() { checkNotSubtype("{void f1,int f2}","[{null f2}]"); }
	@Test public void test_3557() { checkNotSubtype("{void f1,int f2}","[{int f1}]"); }
	@Test public void test_3558() { checkNotSubtype("{void f1,int f2}","[{int f2}]"); }
	@Test public void test_3559() { checkIsSubtype("{void f1,int f2}","{void f1,void f2}"); }
	@Test public void test_3560() { checkIsSubtype("{void f1,int f2}","{void f2,void f3}"); }
	@Test public void test_3561() { checkIsSubtype("{void f1,int f2}","{void f1,any f2}"); }
	@Test public void test_3562() { checkIsSubtype("{void f1,int f2}","{void f2,any f3}"); }
	@Test public void test_3563() { checkIsSubtype("{void f1,int f2}","{void f1,null f2}"); }
	@Test public void test_3564() { checkIsSubtype("{void f1,int f2}","{void f2,null f3}"); }
	@Test public void test_3565() { checkIsSubtype("{void f1,int f2}","{void f1,int f2}"); }
	@Test public void test_3566() { checkIsSubtype("{void f1,int f2}","{void f2,int f3}"); }
	@Test public void test_3567() { checkIsSubtype("{void f1,int f2}","{any f1,void f2}"); }
	@Test public void test_3568() { checkIsSubtype("{void f1,int f2}","{any f2,void f3}"); }
	@Test public void test_3569() { checkNotSubtype("{void f1,int f2}","{any f1,any f2}"); }
	@Test public void test_3570() { checkNotSubtype("{void f1,int f2}","{any f2,any f3}"); }
	@Test public void test_3571() { checkNotSubtype("{void f1,int f2}","{any f1,null f2}"); }
	@Test public void test_3572() { checkNotSubtype("{void f1,int f2}","{any f2,null f3}"); }
	@Test public void test_3573() { checkNotSubtype("{void f1,int f2}","{any f1,int f2}"); }
	@Test public void test_3574() { checkNotSubtype("{void f1,int f2}","{any f2,int f3}"); }
	@Test public void test_3575() { checkIsSubtype("{void f1,int f2}","{null f1,void f2}"); }
	@Test public void test_3576() { checkIsSubtype("{void f1,int f2}","{null f2,void f3}"); }
	@Test public void test_3577() { checkNotSubtype("{void f1,int f2}","{null f1,any f2}"); }
	@Test public void test_3578() { checkNotSubtype("{void f1,int f2}","{null f2,any f3}"); }
	@Test public void test_3579() { checkNotSubtype("{void f1,int f2}","{null f1,null f2}"); }
	@Test public void test_3580() { checkNotSubtype("{void f1,int f2}","{null f2,null f3}"); }
	@Test public void test_3581() { checkNotSubtype("{void f1,int f2}","{null f1,int f2}"); }
	@Test public void test_3582() { checkNotSubtype("{void f1,int f2}","{null f2,int f3}"); }
	@Test public void test_3583() { checkIsSubtype("{void f1,int f2}","{int f1,void f2}"); }
	@Test public void test_3584() { checkIsSubtype("{void f1,int f2}","{int f2,void f3}"); }
	@Test public void test_3585() { checkNotSubtype("{void f1,int f2}","{int f1,any f2}"); }
	@Test public void test_3586() { checkNotSubtype("{void f1,int f2}","{int f2,any f3}"); }
	@Test public void test_3587() { checkNotSubtype("{void f1,int f2}","{int f1,null f2}"); }
	@Test public void test_3588() { checkNotSubtype("{void f1,int f2}","{int f2,null f3}"); }
	@Test public void test_3589() { checkNotSubtype("{void f1,int f2}","{int f1,int f2}"); }
	@Test public void test_3590() { checkNotSubtype("{void f1,int f2}","{int f2,int f3}"); }
	@Test public void test_3591() { checkNotSubtype("{void f1,int f2}","{[void] f1}"); }
	@Test public void test_3592() { checkNotSubtype("{void f1,int f2}","{[void] f2}"); }
	@Test public void test_3593() { checkIsSubtype("{void f1,int f2}","{[void] f1,void f2}"); }
	@Test public void test_3594() { checkIsSubtype("{void f1,int f2}","{[void] f2,void f3}"); }
	@Test public void test_3595() { checkNotSubtype("{void f1,int f2}","{[any] f1}"); }
	@Test public void test_3596() { checkNotSubtype("{void f1,int f2}","{[any] f2}"); }
	@Test public void test_3597() { checkNotSubtype("{void f1,int f2}","{[any] f1,any f2}"); }
	@Test public void test_3598() { checkNotSubtype("{void f1,int f2}","{[any] f2,any f3}"); }
	@Test public void test_3599() { checkNotSubtype("{void f1,int f2}","{[null] f1}"); }
	@Test public void test_3600() { checkNotSubtype("{void f1,int f2}","{[null] f2}"); }
	@Test public void test_3601() { checkNotSubtype("{void f1,int f2}","{[null] f1,null f2}"); }
	@Test public void test_3602() { checkNotSubtype("{void f1,int f2}","{[null] f2,null f3}"); }
	@Test public void test_3603() { checkNotSubtype("{void f1,int f2}","{[int] f1}"); }
	@Test public void test_3604() { checkNotSubtype("{void f1,int f2}","{[int] f2}"); }
	@Test public void test_3605() { checkNotSubtype("{void f1,int f2}","{[int] f1,int f2}"); }
	@Test public void test_3606() { checkNotSubtype("{void f1,int f2}","{[int] f2,int f3}"); }
	@Test public void test_3607() { checkIsSubtype("{void f1,int f2}","{{void f1} f1}"); }
	@Test public void test_3608() { checkIsSubtype("{void f1,int f2}","{{void f2} f1}"); }
	@Test public void test_3609() { checkIsSubtype("{void f1,int f2}","{{void f1} f2}"); }
	@Test public void test_3610() { checkIsSubtype("{void f1,int f2}","{{void f2} f2}"); }
	@Test public void test_3611() { checkIsSubtype("{void f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_3612() { checkIsSubtype("{void f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_3613() { checkIsSubtype("{void f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_3614() { checkIsSubtype("{void f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_3615() { checkNotSubtype("{void f1,int f2}","{{any f1} f1}"); }
	@Test public void test_3616() { checkNotSubtype("{void f1,int f2}","{{any f2} f1}"); }
	@Test public void test_3617() { checkNotSubtype("{void f1,int f2}","{{any f1} f2}"); }
	@Test public void test_3618() { checkNotSubtype("{void f1,int f2}","{{any f2} f2}"); }
	@Test public void test_3619() { checkNotSubtype("{void f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_3620() { checkNotSubtype("{void f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_3621() { checkNotSubtype("{void f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_3622() { checkNotSubtype("{void f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_3623() { checkNotSubtype("{void f1,int f2}","{{null f1} f1}"); }
	@Test public void test_3624() { checkNotSubtype("{void f1,int f2}","{{null f2} f1}"); }
	@Test public void test_3625() { checkNotSubtype("{void f1,int f2}","{{null f1} f2}"); }
	@Test public void test_3626() { checkNotSubtype("{void f1,int f2}","{{null f2} f2}"); }
	@Test public void test_3627() { checkNotSubtype("{void f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3628() { checkNotSubtype("{void f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3629() { checkNotSubtype("{void f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3630() { checkNotSubtype("{void f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3631() { checkNotSubtype("{void f1,int f2}","{{int f1} f1}"); }
	@Test public void test_3632() { checkNotSubtype("{void f1,int f2}","{{int f2} f1}"); }
	@Test public void test_3633() { checkNotSubtype("{void f1,int f2}","{{int f1} f2}"); }
	@Test public void test_3634() { checkNotSubtype("{void f1,int f2}","{{int f2} f2}"); }
	@Test public void test_3635() { checkNotSubtype("{void f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3636() { checkNotSubtype("{void f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3637() { checkNotSubtype("{void f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3638() { checkNotSubtype("{void f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3639() { checkNotSubtype("{void f2,int f3}","any"); }
	@Test public void test_3640() { checkNotSubtype("{void f2,int f3}","null"); }
	@Test public void test_3641() { checkNotSubtype("{void f2,int f3}","int"); }
	@Test public void test_3642() { checkNotSubtype("{void f2,int f3}","[void]"); }
	@Test public void test_3643() { checkNotSubtype("{void f2,int f3}","[any]"); }
	@Test public void test_3644() { checkNotSubtype("{void f2,int f3}","[null]"); }
	@Test public void test_3645() { checkNotSubtype("{void f2,int f3}","[int]"); }
	@Test public void test_3646() { checkIsSubtype("{void f2,int f3}","{void f1}"); }
	@Test public void test_3647() { checkIsSubtype("{void f2,int f3}","{void f2}"); }
	@Test public void test_3648() { checkNotSubtype("{void f2,int f3}","{any f1}"); }
	@Test public void test_3649() { checkNotSubtype("{void f2,int f3}","{any f2}"); }
	@Test public void test_3650() { checkNotSubtype("{void f2,int f3}","{null f1}"); }
	@Test public void test_3651() { checkNotSubtype("{void f2,int f3}","{null f2}"); }
	@Test public void test_3652() { checkNotSubtype("{void f2,int f3}","{int f1}"); }
	@Test public void test_3653() { checkNotSubtype("{void f2,int f3}","{int f2}"); }
	@Test public void test_3654() { checkNotSubtype("{void f2,int f3}","[[void]]"); }
	@Test public void test_3655() { checkNotSubtype("{void f2,int f3}","[[any]]"); }
	@Test public void test_3656() { checkNotSubtype("{void f2,int f3}","[[null]]"); }
	@Test public void test_3657() { checkNotSubtype("{void f2,int f3}","[[int]]"); }
	@Test public void test_3658() { checkNotSubtype("{void f2,int f3}","[{void f1}]"); }
	@Test public void test_3659() { checkNotSubtype("{void f2,int f3}","[{void f2}]"); }
	@Test public void test_3660() { checkNotSubtype("{void f2,int f3}","[{any f1}]"); }
	@Test public void test_3661() { checkNotSubtype("{void f2,int f3}","[{any f2}]"); }
	@Test public void test_3662() { checkNotSubtype("{void f2,int f3}","[{null f1}]"); }
	@Test public void test_3663() { checkNotSubtype("{void f2,int f3}","[{null f2}]"); }
	@Test public void test_3664() { checkNotSubtype("{void f2,int f3}","[{int f1}]"); }
	@Test public void test_3665() { checkNotSubtype("{void f2,int f3}","[{int f2}]"); }
	@Test public void test_3666() { checkIsSubtype("{void f2,int f3}","{void f1,void f2}"); }
	@Test public void test_3667() { checkIsSubtype("{void f2,int f3}","{void f2,void f3}"); }
	@Test public void test_3668() { checkIsSubtype("{void f2,int f3}","{void f1,any f2}"); }
	@Test public void test_3669() { checkIsSubtype("{void f2,int f3}","{void f2,any f3}"); }
	@Test public void test_3670() { checkIsSubtype("{void f2,int f3}","{void f1,null f2}"); }
	@Test public void test_3671() { checkIsSubtype("{void f2,int f3}","{void f2,null f3}"); }
	@Test public void test_3672() { checkIsSubtype("{void f2,int f3}","{void f1,int f2}"); }
	@Test public void test_3673() { checkIsSubtype("{void f2,int f3}","{void f2,int f3}"); }
	@Test public void test_3674() { checkIsSubtype("{void f2,int f3}","{any f1,void f2}"); }
	@Test public void test_3675() { checkIsSubtype("{void f2,int f3}","{any f2,void f3}"); }
	@Test public void test_3676() { checkNotSubtype("{void f2,int f3}","{any f1,any f2}"); }
	@Test public void test_3677() { checkNotSubtype("{void f2,int f3}","{any f2,any f3}"); }
	@Test public void test_3678() { checkNotSubtype("{void f2,int f3}","{any f1,null f2}"); }
	@Test public void test_3679() { checkNotSubtype("{void f2,int f3}","{any f2,null f3}"); }
	@Test public void test_3680() { checkNotSubtype("{void f2,int f3}","{any f1,int f2}"); }
	@Test public void test_3681() { checkNotSubtype("{void f2,int f3}","{any f2,int f3}"); }
	@Test public void test_3682() { checkIsSubtype("{void f2,int f3}","{null f1,void f2}"); }
	@Test public void test_3683() { checkIsSubtype("{void f2,int f3}","{null f2,void f3}"); }
	@Test public void test_3684() { checkNotSubtype("{void f2,int f3}","{null f1,any f2}"); }
	@Test public void test_3685() { checkNotSubtype("{void f2,int f3}","{null f2,any f3}"); }
	@Test public void test_3686() { checkNotSubtype("{void f2,int f3}","{null f1,null f2}"); }
	@Test public void test_3687() { checkNotSubtype("{void f2,int f3}","{null f2,null f3}"); }
	@Test public void test_3688() { checkNotSubtype("{void f2,int f3}","{null f1,int f2}"); }
	@Test public void test_3689() { checkNotSubtype("{void f2,int f3}","{null f2,int f3}"); }
	@Test public void test_3690() { checkIsSubtype("{void f2,int f3}","{int f1,void f2}"); }
	@Test public void test_3691() { checkIsSubtype("{void f2,int f3}","{int f2,void f3}"); }
	@Test public void test_3692() { checkNotSubtype("{void f2,int f3}","{int f1,any f2}"); }
	@Test public void test_3693() { checkNotSubtype("{void f2,int f3}","{int f2,any f3}"); }
	@Test public void test_3694() { checkNotSubtype("{void f2,int f3}","{int f1,null f2}"); }
	@Test public void test_3695() { checkNotSubtype("{void f2,int f3}","{int f2,null f3}"); }
	@Test public void test_3696() { checkNotSubtype("{void f2,int f3}","{int f1,int f2}"); }
	@Test public void test_3697() { checkNotSubtype("{void f2,int f3}","{int f2,int f3}"); }
	@Test public void test_3698() { checkNotSubtype("{void f2,int f3}","{[void] f1}"); }
	@Test public void test_3699() { checkNotSubtype("{void f2,int f3}","{[void] f2}"); }
	@Test public void test_3700() { checkIsSubtype("{void f2,int f3}","{[void] f1,void f2}"); }
	@Test public void test_3701() { checkIsSubtype("{void f2,int f3}","{[void] f2,void f3}"); }
	@Test public void test_3702() { checkNotSubtype("{void f2,int f3}","{[any] f1}"); }
	@Test public void test_3703() { checkNotSubtype("{void f2,int f3}","{[any] f2}"); }
	@Test public void test_3704() { checkNotSubtype("{void f2,int f3}","{[any] f1,any f2}"); }
	@Test public void test_3705() { checkNotSubtype("{void f2,int f3}","{[any] f2,any f3}"); }
	@Test public void test_3706() { checkNotSubtype("{void f2,int f3}","{[null] f1}"); }
	@Test public void test_3707() { checkNotSubtype("{void f2,int f3}","{[null] f2}"); }
	@Test public void test_3708() { checkNotSubtype("{void f2,int f3}","{[null] f1,null f2}"); }
	@Test public void test_3709() { checkNotSubtype("{void f2,int f3}","{[null] f2,null f3}"); }
	@Test public void test_3710() { checkNotSubtype("{void f2,int f3}","{[int] f1}"); }
	@Test public void test_3711() { checkNotSubtype("{void f2,int f3}","{[int] f2}"); }
	@Test public void test_3712() { checkNotSubtype("{void f2,int f3}","{[int] f1,int f2}"); }
	@Test public void test_3713() { checkNotSubtype("{void f2,int f3}","{[int] f2,int f3}"); }
	@Test public void test_3714() { checkIsSubtype("{void f2,int f3}","{{void f1} f1}"); }
	@Test public void test_3715() { checkIsSubtype("{void f2,int f3}","{{void f2} f1}"); }
	@Test public void test_3716() { checkIsSubtype("{void f2,int f3}","{{void f1} f2}"); }
	@Test public void test_3717() { checkIsSubtype("{void f2,int f3}","{{void f2} f2}"); }
	@Test public void test_3718() { checkIsSubtype("{void f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_3719() { checkIsSubtype("{void f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_3720() { checkIsSubtype("{void f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_3721() { checkIsSubtype("{void f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_3722() { checkNotSubtype("{void f2,int f3}","{{any f1} f1}"); }
	@Test public void test_3723() { checkNotSubtype("{void f2,int f3}","{{any f2} f1}"); }
	@Test public void test_3724() { checkNotSubtype("{void f2,int f3}","{{any f1} f2}"); }
	@Test public void test_3725() { checkNotSubtype("{void f2,int f3}","{{any f2} f2}"); }
	@Test public void test_3726() { checkNotSubtype("{void f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_3727() { checkNotSubtype("{void f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_3728() { checkNotSubtype("{void f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_3729() { checkNotSubtype("{void f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_3730() { checkNotSubtype("{void f2,int f3}","{{null f1} f1}"); }
	@Test public void test_3731() { checkNotSubtype("{void f2,int f3}","{{null f2} f1}"); }
	@Test public void test_3732() { checkNotSubtype("{void f2,int f3}","{{null f1} f2}"); }
	@Test public void test_3733() { checkNotSubtype("{void f2,int f3}","{{null f2} f2}"); }
	@Test public void test_3734() { checkNotSubtype("{void f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3735() { checkNotSubtype("{void f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3736() { checkNotSubtype("{void f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3737() { checkNotSubtype("{void f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3738() { checkNotSubtype("{void f2,int f3}","{{int f1} f1}"); }
	@Test public void test_3739() { checkNotSubtype("{void f2,int f3}","{{int f2} f1}"); }
	@Test public void test_3740() { checkNotSubtype("{void f2,int f3}","{{int f1} f2}"); }
	@Test public void test_3741() { checkNotSubtype("{void f2,int f3}","{{int f2} f2}"); }
	@Test public void test_3742() { checkNotSubtype("{void f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3743() { checkNotSubtype("{void f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3744() { checkNotSubtype("{void f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3745() { checkNotSubtype("{void f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3746() { checkNotSubtype("{any f1,void f2}","any"); }
	@Test public void test_3747() { checkNotSubtype("{any f1,void f2}","null"); }
	@Test public void test_3748() { checkNotSubtype("{any f1,void f2}","int"); }
	@Test public void test_3749() { checkNotSubtype("{any f1,void f2}","[void]"); }
	@Test public void test_3750() { checkNotSubtype("{any f1,void f2}","[any]"); }
	@Test public void test_3751() { checkNotSubtype("{any f1,void f2}","[null]"); }
	@Test public void test_3752() { checkNotSubtype("{any f1,void f2}","[int]"); }
	@Test public void test_3753() { checkIsSubtype("{any f1,void f2}","{void f1}"); }
	@Test public void test_3754() { checkIsSubtype("{any f1,void f2}","{void f2}"); }
	@Test public void test_3755() { checkNotSubtype("{any f1,void f2}","{any f1}"); }
	@Test public void test_3756() { checkNotSubtype("{any f1,void f2}","{any f2}"); }
	@Test public void test_3757() { checkNotSubtype("{any f1,void f2}","{null f1}"); }
	@Test public void test_3758() { checkNotSubtype("{any f1,void f2}","{null f2}"); }
	@Test public void test_3759() { checkNotSubtype("{any f1,void f2}","{int f1}"); }
	@Test public void test_3760() { checkNotSubtype("{any f1,void f2}","{int f2}"); }
	@Test public void test_3761() { checkNotSubtype("{any f1,void f2}","[[void]]"); }
	@Test public void test_3762() { checkNotSubtype("{any f1,void f2}","[[any]]"); }
	@Test public void test_3763() { checkNotSubtype("{any f1,void f2}","[[null]]"); }
	@Test public void test_3764() { checkNotSubtype("{any f1,void f2}","[[int]]"); }
	@Test public void test_3765() { checkNotSubtype("{any f1,void f2}","[{void f1}]"); }
	@Test public void test_3766() { checkNotSubtype("{any f1,void f2}","[{void f2}]"); }
	@Test public void test_3767() { checkNotSubtype("{any f1,void f2}","[{any f1}]"); }
	@Test public void test_3768() { checkNotSubtype("{any f1,void f2}","[{any f2}]"); }
	@Test public void test_3769() { checkNotSubtype("{any f1,void f2}","[{null f1}]"); }
	@Test public void test_3770() { checkNotSubtype("{any f1,void f2}","[{null f2}]"); }
	@Test public void test_3771() { checkNotSubtype("{any f1,void f2}","[{int f1}]"); }
	@Test public void test_3772() { checkNotSubtype("{any f1,void f2}","[{int f2}]"); }
	@Test public void test_3773() { checkIsSubtype("{any f1,void f2}","{void f1,void f2}"); }
	@Test public void test_3774() { checkIsSubtype("{any f1,void f2}","{void f2,void f3}"); }
	@Test public void test_3775() { checkIsSubtype("{any f1,void f2}","{void f1,any f2}"); }
	@Test public void test_3776() { checkIsSubtype("{any f1,void f2}","{void f2,any f3}"); }
	@Test public void test_3777() { checkIsSubtype("{any f1,void f2}","{void f1,null f2}"); }
	@Test public void test_3778() { checkIsSubtype("{any f1,void f2}","{void f2,null f3}"); }
	@Test public void test_3779() { checkIsSubtype("{any f1,void f2}","{void f1,int f2}"); }
	@Test public void test_3780() { checkIsSubtype("{any f1,void f2}","{void f2,int f3}"); }
	@Test public void test_3781() { checkIsSubtype("{any f1,void f2}","{any f1,void f2}"); }
	@Test public void test_3782() { checkIsSubtype("{any f1,void f2}","{any f2,void f3}"); }
	@Test public void test_3783() { checkNotSubtype("{any f1,void f2}","{any f1,any f2}"); }
	@Test public void test_3784() { checkNotSubtype("{any f1,void f2}","{any f2,any f3}"); }
	@Test public void test_3785() { checkNotSubtype("{any f1,void f2}","{any f1,null f2}"); }
	@Test public void test_3786() { checkNotSubtype("{any f1,void f2}","{any f2,null f3}"); }
	@Test public void test_3787() { checkNotSubtype("{any f1,void f2}","{any f1,int f2}"); }
	@Test public void test_3788() { checkNotSubtype("{any f1,void f2}","{any f2,int f3}"); }
	@Test public void test_3789() { checkIsSubtype("{any f1,void f2}","{null f1,void f2}"); }
	@Test public void test_3790() { checkIsSubtype("{any f1,void f2}","{null f2,void f3}"); }
	@Test public void test_3791() { checkNotSubtype("{any f1,void f2}","{null f1,any f2}"); }
	@Test public void test_3792() { checkNotSubtype("{any f1,void f2}","{null f2,any f3}"); }
	@Test public void test_3793() { checkNotSubtype("{any f1,void f2}","{null f1,null f2}"); }
	@Test public void test_3794() { checkNotSubtype("{any f1,void f2}","{null f2,null f3}"); }
	@Test public void test_3795() { checkNotSubtype("{any f1,void f2}","{null f1,int f2}"); }
	@Test public void test_3796() { checkNotSubtype("{any f1,void f2}","{null f2,int f3}"); }
	@Test public void test_3797() { checkIsSubtype("{any f1,void f2}","{int f1,void f2}"); }
	@Test public void test_3798() { checkIsSubtype("{any f1,void f2}","{int f2,void f3}"); }
	@Test public void test_3799() { checkNotSubtype("{any f1,void f2}","{int f1,any f2}"); }
	@Test public void test_3800() { checkNotSubtype("{any f1,void f2}","{int f2,any f3}"); }
	@Test public void test_3801() { checkNotSubtype("{any f1,void f2}","{int f1,null f2}"); }
	@Test public void test_3802() { checkNotSubtype("{any f1,void f2}","{int f2,null f3}"); }
	@Test public void test_3803() { checkNotSubtype("{any f1,void f2}","{int f1,int f2}"); }
	@Test public void test_3804() { checkNotSubtype("{any f1,void f2}","{int f2,int f3}"); }
	@Test public void test_3805() { checkNotSubtype("{any f1,void f2}","{[void] f1}"); }
	@Test public void test_3806() { checkNotSubtype("{any f1,void f2}","{[void] f2}"); }
	@Test public void test_3807() { checkIsSubtype("{any f1,void f2}","{[void] f1,void f2}"); }
	@Test public void test_3808() { checkIsSubtype("{any f1,void f2}","{[void] f2,void f3}"); }
	@Test public void test_3809() { checkNotSubtype("{any f1,void f2}","{[any] f1}"); }
	@Test public void test_3810() { checkNotSubtype("{any f1,void f2}","{[any] f2}"); }
	@Test public void test_3811() { checkNotSubtype("{any f1,void f2}","{[any] f1,any f2}"); }
	@Test public void test_3812() { checkNotSubtype("{any f1,void f2}","{[any] f2,any f3}"); }
	@Test public void test_3813() { checkNotSubtype("{any f1,void f2}","{[null] f1}"); }
	@Test public void test_3814() { checkNotSubtype("{any f1,void f2}","{[null] f2}"); }
	@Test public void test_3815() { checkNotSubtype("{any f1,void f2}","{[null] f1,null f2}"); }
	@Test public void test_3816() { checkNotSubtype("{any f1,void f2}","{[null] f2,null f3}"); }
	@Test public void test_3817() { checkNotSubtype("{any f1,void f2}","{[int] f1}"); }
	@Test public void test_3818() { checkNotSubtype("{any f1,void f2}","{[int] f2}"); }
	@Test public void test_3819() { checkNotSubtype("{any f1,void f2}","{[int] f1,int f2}"); }
	@Test public void test_3820() { checkNotSubtype("{any f1,void f2}","{[int] f2,int f3}"); }
	@Test public void test_3821() { checkIsSubtype("{any f1,void f2}","{{void f1} f1}"); }
	@Test public void test_3822() { checkIsSubtype("{any f1,void f2}","{{void f2} f1}"); }
	@Test public void test_3823() { checkIsSubtype("{any f1,void f2}","{{void f1} f2}"); }
	@Test public void test_3824() { checkIsSubtype("{any f1,void f2}","{{void f2} f2}"); }
	@Test public void test_3825() { checkIsSubtype("{any f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_3826() { checkIsSubtype("{any f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_3827() { checkIsSubtype("{any f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_3828() { checkIsSubtype("{any f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_3829() { checkNotSubtype("{any f1,void f2}","{{any f1} f1}"); }
	@Test public void test_3830() { checkNotSubtype("{any f1,void f2}","{{any f2} f1}"); }
	@Test public void test_3831() { checkNotSubtype("{any f1,void f2}","{{any f1} f2}"); }
	@Test public void test_3832() { checkNotSubtype("{any f1,void f2}","{{any f2} f2}"); }
	@Test public void test_3833() { checkNotSubtype("{any f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_3834() { checkNotSubtype("{any f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_3835() { checkNotSubtype("{any f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_3836() { checkNotSubtype("{any f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_3837() { checkNotSubtype("{any f1,void f2}","{{null f1} f1}"); }
	@Test public void test_3838() { checkNotSubtype("{any f1,void f2}","{{null f2} f1}"); }
	@Test public void test_3839() { checkNotSubtype("{any f1,void f2}","{{null f1} f2}"); }
	@Test public void test_3840() { checkNotSubtype("{any f1,void f2}","{{null f2} f2}"); }
	@Test public void test_3841() { checkNotSubtype("{any f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3842() { checkNotSubtype("{any f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3843() { checkNotSubtype("{any f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3844() { checkNotSubtype("{any f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3845() { checkNotSubtype("{any f1,void f2}","{{int f1} f1}"); }
	@Test public void test_3846() { checkNotSubtype("{any f1,void f2}","{{int f2} f1}"); }
	@Test public void test_3847() { checkNotSubtype("{any f1,void f2}","{{int f1} f2}"); }
	@Test public void test_3848() { checkNotSubtype("{any f1,void f2}","{{int f2} f2}"); }
	@Test public void test_3849() { checkNotSubtype("{any f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3850() { checkNotSubtype("{any f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3851() { checkNotSubtype("{any f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3852() { checkNotSubtype("{any f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3853() { checkNotSubtype("{any f2,void f3}","any"); }
	@Test public void test_3854() { checkNotSubtype("{any f2,void f3}","null"); }
	@Test public void test_3855() { checkNotSubtype("{any f2,void f3}","int"); }
	@Test public void test_3856() { checkNotSubtype("{any f2,void f3}","[void]"); }
	@Test public void test_3857() { checkNotSubtype("{any f2,void f3}","[any]"); }
	@Test public void test_3858() { checkNotSubtype("{any f2,void f3}","[null]"); }
	@Test public void test_3859() { checkNotSubtype("{any f2,void f3}","[int]"); }
	@Test public void test_3860() { checkIsSubtype("{any f2,void f3}","{void f1}"); }
	@Test public void test_3861() { checkIsSubtype("{any f2,void f3}","{void f2}"); }
	@Test public void test_3862() { checkNotSubtype("{any f2,void f3}","{any f1}"); }
	@Test public void test_3863() { checkNotSubtype("{any f2,void f3}","{any f2}"); }
	@Test public void test_3864() { checkNotSubtype("{any f2,void f3}","{null f1}"); }
	@Test public void test_3865() { checkNotSubtype("{any f2,void f3}","{null f2}"); }
	@Test public void test_3866() { checkNotSubtype("{any f2,void f3}","{int f1}"); }
	@Test public void test_3867() { checkNotSubtype("{any f2,void f3}","{int f2}"); }
	@Test public void test_3868() { checkNotSubtype("{any f2,void f3}","[[void]]"); }
	@Test public void test_3869() { checkNotSubtype("{any f2,void f3}","[[any]]"); }
	@Test public void test_3870() { checkNotSubtype("{any f2,void f3}","[[null]]"); }
	@Test public void test_3871() { checkNotSubtype("{any f2,void f3}","[[int]]"); }
	@Test public void test_3872() { checkNotSubtype("{any f2,void f3}","[{void f1}]"); }
	@Test public void test_3873() { checkNotSubtype("{any f2,void f3}","[{void f2}]"); }
	@Test public void test_3874() { checkNotSubtype("{any f2,void f3}","[{any f1}]"); }
	@Test public void test_3875() { checkNotSubtype("{any f2,void f3}","[{any f2}]"); }
	@Test public void test_3876() { checkNotSubtype("{any f2,void f3}","[{null f1}]"); }
	@Test public void test_3877() { checkNotSubtype("{any f2,void f3}","[{null f2}]"); }
	@Test public void test_3878() { checkNotSubtype("{any f2,void f3}","[{int f1}]"); }
	@Test public void test_3879() { checkNotSubtype("{any f2,void f3}","[{int f2}]"); }
	@Test public void test_3880() { checkIsSubtype("{any f2,void f3}","{void f1,void f2}"); }
	@Test public void test_3881() { checkIsSubtype("{any f2,void f3}","{void f2,void f3}"); }
	@Test public void test_3882() { checkIsSubtype("{any f2,void f3}","{void f1,any f2}"); }
	@Test public void test_3883() { checkIsSubtype("{any f2,void f3}","{void f2,any f3}"); }
	@Test public void test_3884() { checkIsSubtype("{any f2,void f3}","{void f1,null f2}"); }
	@Test public void test_3885() { checkIsSubtype("{any f2,void f3}","{void f2,null f3}"); }
	@Test public void test_3886() { checkIsSubtype("{any f2,void f3}","{void f1,int f2}"); }
	@Test public void test_3887() { checkIsSubtype("{any f2,void f3}","{void f2,int f3}"); }
	@Test public void test_3888() { checkIsSubtype("{any f2,void f3}","{any f1,void f2}"); }
	@Test public void test_3889() { checkIsSubtype("{any f2,void f3}","{any f2,void f3}"); }
	@Test public void test_3890() { checkNotSubtype("{any f2,void f3}","{any f1,any f2}"); }
	@Test public void test_3891() { checkNotSubtype("{any f2,void f3}","{any f2,any f3}"); }
	@Test public void test_3892() { checkNotSubtype("{any f2,void f3}","{any f1,null f2}"); }
	@Test public void test_3893() { checkNotSubtype("{any f2,void f3}","{any f2,null f3}"); }
	@Test public void test_3894() { checkNotSubtype("{any f2,void f3}","{any f1,int f2}"); }
	@Test public void test_3895() { checkNotSubtype("{any f2,void f3}","{any f2,int f3}"); }
	@Test public void test_3896() { checkIsSubtype("{any f2,void f3}","{null f1,void f2}"); }
	@Test public void test_3897() { checkIsSubtype("{any f2,void f3}","{null f2,void f3}"); }
	@Test public void test_3898() { checkNotSubtype("{any f2,void f3}","{null f1,any f2}"); }
	@Test public void test_3899() { checkNotSubtype("{any f2,void f3}","{null f2,any f3}"); }
	@Test public void test_3900() { checkNotSubtype("{any f2,void f3}","{null f1,null f2}"); }
	@Test public void test_3901() { checkNotSubtype("{any f2,void f3}","{null f2,null f3}"); }
	@Test public void test_3902() { checkNotSubtype("{any f2,void f3}","{null f1,int f2}"); }
	@Test public void test_3903() { checkNotSubtype("{any f2,void f3}","{null f2,int f3}"); }
	@Test public void test_3904() { checkIsSubtype("{any f2,void f3}","{int f1,void f2}"); }
	@Test public void test_3905() { checkIsSubtype("{any f2,void f3}","{int f2,void f3}"); }
	@Test public void test_3906() { checkNotSubtype("{any f2,void f3}","{int f1,any f2}"); }
	@Test public void test_3907() { checkNotSubtype("{any f2,void f3}","{int f2,any f3}"); }
	@Test public void test_3908() { checkNotSubtype("{any f2,void f3}","{int f1,null f2}"); }
	@Test public void test_3909() { checkNotSubtype("{any f2,void f3}","{int f2,null f3}"); }
	@Test public void test_3910() { checkNotSubtype("{any f2,void f3}","{int f1,int f2}"); }
	@Test public void test_3911() { checkNotSubtype("{any f2,void f3}","{int f2,int f3}"); }
	@Test public void test_3912() { checkNotSubtype("{any f2,void f3}","{[void] f1}"); }
	@Test public void test_3913() { checkNotSubtype("{any f2,void f3}","{[void] f2}"); }
	@Test public void test_3914() { checkIsSubtype("{any f2,void f3}","{[void] f1,void f2}"); }
	@Test public void test_3915() { checkIsSubtype("{any f2,void f3}","{[void] f2,void f3}"); }
	@Test public void test_3916() { checkNotSubtype("{any f2,void f3}","{[any] f1}"); }
	@Test public void test_3917() { checkNotSubtype("{any f2,void f3}","{[any] f2}"); }
	@Test public void test_3918() { checkNotSubtype("{any f2,void f3}","{[any] f1,any f2}"); }
	@Test public void test_3919() { checkNotSubtype("{any f2,void f3}","{[any] f2,any f3}"); }
	@Test public void test_3920() { checkNotSubtype("{any f2,void f3}","{[null] f1}"); }
	@Test public void test_3921() { checkNotSubtype("{any f2,void f3}","{[null] f2}"); }
	@Test public void test_3922() { checkNotSubtype("{any f2,void f3}","{[null] f1,null f2}"); }
	@Test public void test_3923() { checkNotSubtype("{any f2,void f3}","{[null] f2,null f3}"); }
	@Test public void test_3924() { checkNotSubtype("{any f2,void f3}","{[int] f1}"); }
	@Test public void test_3925() { checkNotSubtype("{any f2,void f3}","{[int] f2}"); }
	@Test public void test_3926() { checkNotSubtype("{any f2,void f3}","{[int] f1,int f2}"); }
	@Test public void test_3927() { checkNotSubtype("{any f2,void f3}","{[int] f2,int f3}"); }
	@Test public void test_3928() { checkIsSubtype("{any f2,void f3}","{{void f1} f1}"); }
	@Test public void test_3929() { checkIsSubtype("{any f2,void f3}","{{void f2} f1}"); }
	@Test public void test_3930() { checkIsSubtype("{any f2,void f3}","{{void f1} f2}"); }
	@Test public void test_3931() { checkIsSubtype("{any f2,void f3}","{{void f2} f2}"); }
	@Test public void test_3932() { checkIsSubtype("{any f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_3933() { checkIsSubtype("{any f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_3934() { checkIsSubtype("{any f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_3935() { checkIsSubtype("{any f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_3936() { checkNotSubtype("{any f2,void f3}","{{any f1} f1}"); }
	@Test public void test_3937() { checkNotSubtype("{any f2,void f3}","{{any f2} f1}"); }
	@Test public void test_3938() { checkNotSubtype("{any f2,void f3}","{{any f1} f2}"); }
	@Test public void test_3939() { checkNotSubtype("{any f2,void f3}","{{any f2} f2}"); }
	@Test public void test_3940() { checkNotSubtype("{any f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_3941() { checkNotSubtype("{any f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_3942() { checkNotSubtype("{any f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_3943() { checkNotSubtype("{any f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_3944() { checkNotSubtype("{any f2,void f3}","{{null f1} f1}"); }
	@Test public void test_3945() { checkNotSubtype("{any f2,void f3}","{{null f2} f1}"); }
	@Test public void test_3946() { checkNotSubtype("{any f2,void f3}","{{null f1} f2}"); }
	@Test public void test_3947() { checkNotSubtype("{any f2,void f3}","{{null f2} f2}"); }
	@Test public void test_3948() { checkNotSubtype("{any f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3949() { checkNotSubtype("{any f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3950() { checkNotSubtype("{any f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3951() { checkNotSubtype("{any f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3952() { checkNotSubtype("{any f2,void f3}","{{int f1} f1}"); }
	@Test public void test_3953() { checkNotSubtype("{any f2,void f3}","{{int f2} f1}"); }
	@Test public void test_3954() { checkNotSubtype("{any f2,void f3}","{{int f1} f2}"); }
	@Test public void test_3955() { checkNotSubtype("{any f2,void f3}","{{int f2} f2}"); }
	@Test public void test_3956() { checkNotSubtype("{any f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3957() { checkNotSubtype("{any f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3958() { checkNotSubtype("{any f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3959() { checkNotSubtype("{any f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3960() { checkNotSubtype("{any f1,any f2}","any"); }
	@Test public void test_3961() { checkNotSubtype("{any f1,any f2}","null"); }
	@Test public void test_3962() { checkNotSubtype("{any f1,any f2}","int"); }
	@Test public void test_3963() { checkNotSubtype("{any f1,any f2}","[void]"); }
	@Test public void test_3964() { checkNotSubtype("{any f1,any f2}","[any]"); }
	@Test public void test_3965() { checkNotSubtype("{any f1,any f2}","[null]"); }
	@Test public void test_3966() { checkNotSubtype("{any f1,any f2}","[int]"); }
	@Test public void test_3967() { checkIsSubtype("{any f1,any f2}","{void f1}"); }
	@Test public void test_3968() { checkIsSubtype("{any f1,any f2}","{void f2}"); }
	@Test public void test_3969() { checkNotSubtype("{any f1,any f2}","{any f1}"); }
	@Test public void test_3970() { checkNotSubtype("{any f1,any f2}","{any f2}"); }
	@Test public void test_3971() { checkNotSubtype("{any f1,any f2}","{null f1}"); }
	@Test public void test_3972() { checkNotSubtype("{any f1,any f2}","{null f2}"); }
	@Test public void test_3973() { checkNotSubtype("{any f1,any f2}","{int f1}"); }
	@Test public void test_3974() { checkNotSubtype("{any f1,any f2}","{int f2}"); }
	@Test public void test_3975() { checkNotSubtype("{any f1,any f2}","[[void]]"); }
	@Test public void test_3976() { checkNotSubtype("{any f1,any f2}","[[any]]"); }
	@Test public void test_3977() { checkNotSubtype("{any f1,any f2}","[[null]]"); }
	@Test public void test_3978() { checkNotSubtype("{any f1,any f2}","[[int]]"); }
	@Test public void test_3979() { checkNotSubtype("{any f1,any f2}","[{void f1}]"); }
	@Test public void test_3980() { checkNotSubtype("{any f1,any f2}","[{void f2}]"); }
	@Test public void test_3981() { checkNotSubtype("{any f1,any f2}","[{any f1}]"); }
	@Test public void test_3982() { checkNotSubtype("{any f1,any f2}","[{any f2}]"); }
	@Test public void test_3983() { checkNotSubtype("{any f1,any f2}","[{null f1}]"); }
	@Test public void test_3984() { checkNotSubtype("{any f1,any f2}","[{null f2}]"); }
	@Test public void test_3985() { checkNotSubtype("{any f1,any f2}","[{int f1}]"); }
	@Test public void test_3986() { checkNotSubtype("{any f1,any f2}","[{int f2}]"); }
	@Test public void test_3987() { checkIsSubtype("{any f1,any f2}","{void f1,void f2}"); }
	@Test public void test_3988() { checkIsSubtype("{any f1,any f2}","{void f2,void f3}"); }
	@Test public void test_3989() { checkIsSubtype("{any f1,any f2}","{void f1,any f2}"); }
	@Test public void test_3990() { checkIsSubtype("{any f1,any f2}","{void f2,any f3}"); }
	@Test public void test_3991() { checkIsSubtype("{any f1,any f2}","{void f1,null f2}"); }
	@Test public void test_3992() { checkIsSubtype("{any f1,any f2}","{void f2,null f3}"); }
	@Test public void test_3993() { checkIsSubtype("{any f1,any f2}","{void f1,int f2}"); }
	@Test public void test_3994() { checkIsSubtype("{any f1,any f2}","{void f2,int f3}"); }
	@Test public void test_3995() { checkIsSubtype("{any f1,any f2}","{any f1,void f2}"); }
	@Test public void test_3996() { checkIsSubtype("{any f1,any f2}","{any f2,void f3}"); }
	@Test public void test_3997() { checkIsSubtype("{any f1,any f2}","{any f1,any f2}"); }
	@Test public void test_3998() { checkNotSubtype("{any f1,any f2}","{any f2,any f3}"); }
	@Test public void test_3999() { checkIsSubtype("{any f1,any f2}","{any f1,null f2}"); }
	@Test public void test_4000() { checkNotSubtype("{any f1,any f2}","{any f2,null f3}"); }
	@Test public void test_4001() { checkIsSubtype("{any f1,any f2}","{any f1,int f2}"); }
	@Test public void test_4002() { checkNotSubtype("{any f1,any f2}","{any f2,int f3}"); }
	@Test public void test_4003() { checkIsSubtype("{any f1,any f2}","{null f1,void f2}"); }
	@Test public void test_4004() { checkIsSubtype("{any f1,any f2}","{null f2,void f3}"); }
	@Test public void test_4005() { checkIsSubtype("{any f1,any f2}","{null f1,any f2}"); }
	@Test public void test_4006() { checkNotSubtype("{any f1,any f2}","{null f2,any f3}"); }
	@Test public void test_4007() { checkIsSubtype("{any f1,any f2}","{null f1,null f2}"); }
	@Test public void test_4008() { checkNotSubtype("{any f1,any f2}","{null f2,null f3}"); }
	@Test public void test_4009() { checkIsSubtype("{any f1,any f2}","{null f1,int f2}"); }
	@Test public void test_4010() { checkNotSubtype("{any f1,any f2}","{null f2,int f3}"); }
	@Test public void test_4011() { checkIsSubtype("{any f1,any f2}","{int f1,void f2}"); }
	@Test public void test_4012() { checkIsSubtype("{any f1,any f2}","{int f2,void f3}"); }
	@Test public void test_4013() { checkIsSubtype("{any f1,any f2}","{int f1,any f2}"); }
	@Test public void test_4014() { checkNotSubtype("{any f1,any f2}","{int f2,any f3}"); }
	@Test public void test_4015() { checkIsSubtype("{any f1,any f2}","{int f1,null f2}"); }
	@Test public void test_4016() { checkNotSubtype("{any f1,any f2}","{int f2,null f3}"); }
	@Test public void test_4017() { checkIsSubtype("{any f1,any f2}","{int f1,int f2}"); }
	@Test public void test_4018() { checkNotSubtype("{any f1,any f2}","{int f2,int f3}"); }
	@Test public void test_4019() { checkNotSubtype("{any f1,any f2}","{[void] f1}"); }
	@Test public void test_4020() { checkNotSubtype("{any f1,any f2}","{[void] f2}"); }
	@Test public void test_4021() { checkIsSubtype("{any f1,any f2}","{[void] f1,void f2}"); }
	@Test public void test_4022() { checkIsSubtype("{any f1,any f2}","{[void] f2,void f3}"); }
	@Test public void test_4023() { checkNotSubtype("{any f1,any f2}","{[any] f1}"); }
	@Test public void test_4024() { checkNotSubtype("{any f1,any f2}","{[any] f2}"); }
	@Test public void test_4025() { checkIsSubtype("{any f1,any f2}","{[any] f1,any f2}"); }
	@Test public void test_4026() { checkNotSubtype("{any f1,any f2}","{[any] f2,any f3}"); }
	@Test public void test_4027() { checkNotSubtype("{any f1,any f2}","{[null] f1}"); }
	@Test public void test_4028() { checkNotSubtype("{any f1,any f2}","{[null] f2}"); }
	@Test public void test_4029() { checkIsSubtype("{any f1,any f2}","{[null] f1,null f2}"); }
	@Test public void test_4030() { checkNotSubtype("{any f1,any f2}","{[null] f2,null f3}"); }
	@Test public void test_4031() { checkNotSubtype("{any f1,any f2}","{[int] f1}"); }
	@Test public void test_4032() { checkNotSubtype("{any f1,any f2}","{[int] f2}"); }
	@Test public void test_4033() { checkIsSubtype("{any f1,any f2}","{[int] f1,int f2}"); }
	@Test public void test_4034() { checkNotSubtype("{any f1,any f2}","{[int] f2,int f3}"); }
	@Test public void test_4035() { checkIsSubtype("{any f1,any f2}","{{void f1} f1}"); }
	@Test public void test_4036() { checkIsSubtype("{any f1,any f2}","{{void f2} f1}"); }
	@Test public void test_4037() { checkIsSubtype("{any f1,any f2}","{{void f1} f2}"); }
	@Test public void test_4038() { checkIsSubtype("{any f1,any f2}","{{void f2} f2}"); }
	@Test public void test_4039() { checkIsSubtype("{any f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4040() { checkIsSubtype("{any f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4041() { checkIsSubtype("{any f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4042() { checkIsSubtype("{any f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4043() { checkNotSubtype("{any f1,any f2}","{{any f1} f1}"); }
	@Test public void test_4044() { checkNotSubtype("{any f1,any f2}","{{any f2} f1}"); }
	@Test public void test_4045() { checkNotSubtype("{any f1,any f2}","{{any f1} f2}"); }
	@Test public void test_4046() { checkNotSubtype("{any f1,any f2}","{{any f2} f2}"); }
	@Test public void test_4047() { checkIsSubtype("{any f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4048() { checkIsSubtype("{any f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4049() { checkNotSubtype("{any f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4050() { checkNotSubtype("{any f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4051() { checkNotSubtype("{any f1,any f2}","{{null f1} f1}"); }
	@Test public void test_4052() { checkNotSubtype("{any f1,any f2}","{{null f2} f1}"); }
	@Test public void test_4053() { checkNotSubtype("{any f1,any f2}","{{null f1} f2}"); }
	@Test public void test_4054() { checkNotSubtype("{any f1,any f2}","{{null f2} f2}"); }
	@Test public void test_4055() { checkIsSubtype("{any f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4056() { checkIsSubtype("{any f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4057() { checkNotSubtype("{any f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4058() { checkNotSubtype("{any f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4059() { checkNotSubtype("{any f1,any f2}","{{int f1} f1}"); }
	@Test public void test_4060() { checkNotSubtype("{any f1,any f2}","{{int f2} f1}"); }
	@Test public void test_4061() { checkNotSubtype("{any f1,any f2}","{{int f1} f2}"); }
	@Test public void test_4062() { checkNotSubtype("{any f1,any f2}","{{int f2} f2}"); }
	@Test public void test_4063() { checkIsSubtype("{any f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4064() { checkIsSubtype("{any f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4065() { checkNotSubtype("{any f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4066() { checkNotSubtype("{any f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4067() { checkNotSubtype("{any f2,any f3}","any"); }
	@Test public void test_4068() { checkNotSubtype("{any f2,any f3}","null"); }
	@Test public void test_4069() { checkNotSubtype("{any f2,any f3}","int"); }
	@Test public void test_4070() { checkNotSubtype("{any f2,any f3}","[void]"); }
	@Test public void test_4071() { checkNotSubtype("{any f2,any f3}","[any]"); }
	@Test public void test_4072() { checkNotSubtype("{any f2,any f3}","[null]"); }
	@Test public void test_4073() { checkNotSubtype("{any f2,any f3}","[int]"); }
	@Test public void test_4074() { checkIsSubtype("{any f2,any f3}","{void f1}"); }
	@Test public void test_4075() { checkIsSubtype("{any f2,any f3}","{void f2}"); }
	@Test public void test_4076() { checkNotSubtype("{any f2,any f3}","{any f1}"); }
	@Test public void test_4077() { checkNotSubtype("{any f2,any f3}","{any f2}"); }
	@Test public void test_4078() { checkNotSubtype("{any f2,any f3}","{null f1}"); }
	@Test public void test_4079() { checkNotSubtype("{any f2,any f3}","{null f2}"); }
	@Test public void test_4080() { checkNotSubtype("{any f2,any f3}","{int f1}"); }
	@Test public void test_4081() { checkNotSubtype("{any f2,any f3}","{int f2}"); }
	@Test public void test_4082() { checkNotSubtype("{any f2,any f3}","[[void]]"); }
	@Test public void test_4083() { checkNotSubtype("{any f2,any f3}","[[any]]"); }
	@Test public void test_4084() { checkNotSubtype("{any f2,any f3}","[[null]]"); }
	@Test public void test_4085() { checkNotSubtype("{any f2,any f3}","[[int]]"); }
	@Test public void test_4086() { checkNotSubtype("{any f2,any f3}","[{void f1}]"); }
	@Test public void test_4087() { checkNotSubtype("{any f2,any f3}","[{void f2}]"); }
	@Test public void test_4088() { checkNotSubtype("{any f2,any f3}","[{any f1}]"); }
	@Test public void test_4089() { checkNotSubtype("{any f2,any f3}","[{any f2}]"); }
	@Test public void test_4090() { checkNotSubtype("{any f2,any f3}","[{null f1}]"); }
	@Test public void test_4091() { checkNotSubtype("{any f2,any f3}","[{null f2}]"); }
	@Test public void test_4092() { checkNotSubtype("{any f2,any f3}","[{int f1}]"); }
	@Test public void test_4093() { checkNotSubtype("{any f2,any f3}","[{int f2}]"); }
	@Test public void test_4094() { checkIsSubtype("{any f2,any f3}","{void f1,void f2}"); }
	@Test public void test_4095() { checkIsSubtype("{any f2,any f3}","{void f2,void f3}"); }
	@Test public void test_4096() { checkIsSubtype("{any f2,any f3}","{void f1,any f2}"); }
	@Test public void test_4097() { checkIsSubtype("{any f2,any f3}","{void f2,any f3}"); }
	@Test public void test_4098() { checkIsSubtype("{any f2,any f3}","{void f1,null f2}"); }
	@Test public void test_4099() { checkIsSubtype("{any f2,any f3}","{void f2,null f3}"); }
	@Test public void test_4100() { checkIsSubtype("{any f2,any f3}","{void f1,int f2}"); }
	@Test public void test_4101() { checkIsSubtype("{any f2,any f3}","{void f2,int f3}"); }
	@Test public void test_4102() { checkIsSubtype("{any f2,any f3}","{any f1,void f2}"); }
	@Test public void test_4103() { checkIsSubtype("{any f2,any f3}","{any f2,void f3}"); }
	@Test public void test_4104() { checkNotSubtype("{any f2,any f3}","{any f1,any f2}"); }
	@Test public void test_4105() { checkIsSubtype("{any f2,any f3}","{any f2,any f3}"); }
	@Test public void test_4106() { checkNotSubtype("{any f2,any f3}","{any f1,null f2}"); }
	@Test public void test_4107() { checkIsSubtype("{any f2,any f3}","{any f2,null f3}"); }
	@Test public void test_4108() { checkNotSubtype("{any f2,any f3}","{any f1,int f2}"); }
	@Test public void test_4109() { checkIsSubtype("{any f2,any f3}","{any f2,int f3}"); }
	@Test public void test_4110() { checkIsSubtype("{any f2,any f3}","{null f1,void f2}"); }
	@Test public void test_4111() { checkIsSubtype("{any f2,any f3}","{null f2,void f3}"); }
	@Test public void test_4112() { checkNotSubtype("{any f2,any f3}","{null f1,any f2}"); }
	@Test public void test_4113() { checkIsSubtype("{any f2,any f3}","{null f2,any f3}"); }
	@Test public void test_4114() { checkNotSubtype("{any f2,any f3}","{null f1,null f2}"); }
	@Test public void test_4115() { checkIsSubtype("{any f2,any f3}","{null f2,null f3}"); }
	@Test public void test_4116() { checkNotSubtype("{any f2,any f3}","{null f1,int f2}"); }
	@Test public void test_4117() { checkIsSubtype("{any f2,any f3}","{null f2,int f3}"); }
	@Test public void test_4118() { checkIsSubtype("{any f2,any f3}","{int f1,void f2}"); }
	@Test public void test_4119() { checkIsSubtype("{any f2,any f3}","{int f2,void f3}"); }
	@Test public void test_4120() { checkNotSubtype("{any f2,any f3}","{int f1,any f2}"); }
	@Test public void test_4121() { checkIsSubtype("{any f2,any f3}","{int f2,any f3}"); }
	@Test public void test_4122() { checkNotSubtype("{any f2,any f3}","{int f1,null f2}"); }
	@Test public void test_4123() { checkIsSubtype("{any f2,any f3}","{int f2,null f3}"); }
	@Test public void test_4124() { checkNotSubtype("{any f2,any f3}","{int f1,int f2}"); }
	@Test public void test_4125() { checkIsSubtype("{any f2,any f3}","{int f2,int f3}"); }
	@Test public void test_4126() { checkNotSubtype("{any f2,any f3}","{[void] f1}"); }
	@Test public void test_4127() { checkNotSubtype("{any f2,any f3}","{[void] f2}"); }
	@Test public void test_4128() { checkIsSubtype("{any f2,any f3}","{[void] f1,void f2}"); }
	@Test public void test_4129() { checkIsSubtype("{any f2,any f3}","{[void] f2,void f3}"); }
	@Test public void test_4130() { checkNotSubtype("{any f2,any f3}","{[any] f1}"); }
	@Test public void test_4131() { checkNotSubtype("{any f2,any f3}","{[any] f2}"); }
	@Test public void test_4132() { checkNotSubtype("{any f2,any f3}","{[any] f1,any f2}"); }
	@Test public void test_4133() { checkIsSubtype("{any f2,any f3}","{[any] f2,any f3}"); }
	@Test public void test_4134() { checkNotSubtype("{any f2,any f3}","{[null] f1}"); }
	@Test public void test_4135() { checkNotSubtype("{any f2,any f3}","{[null] f2}"); }
	@Test public void test_4136() { checkNotSubtype("{any f2,any f3}","{[null] f1,null f2}"); }
	@Test public void test_4137() { checkIsSubtype("{any f2,any f3}","{[null] f2,null f3}"); }
	@Test public void test_4138() { checkNotSubtype("{any f2,any f3}","{[int] f1}"); }
	@Test public void test_4139() { checkNotSubtype("{any f2,any f3}","{[int] f2}"); }
	@Test public void test_4140() { checkNotSubtype("{any f2,any f3}","{[int] f1,int f2}"); }
	@Test public void test_4141() { checkIsSubtype("{any f2,any f3}","{[int] f2,int f3}"); }
	@Test public void test_4142() { checkIsSubtype("{any f2,any f3}","{{void f1} f1}"); }
	@Test public void test_4143() { checkIsSubtype("{any f2,any f3}","{{void f2} f1}"); }
	@Test public void test_4144() { checkIsSubtype("{any f2,any f3}","{{void f1} f2}"); }
	@Test public void test_4145() { checkIsSubtype("{any f2,any f3}","{{void f2} f2}"); }
	@Test public void test_4146() { checkIsSubtype("{any f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_4147() { checkIsSubtype("{any f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_4148() { checkIsSubtype("{any f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_4149() { checkIsSubtype("{any f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_4150() { checkNotSubtype("{any f2,any f3}","{{any f1} f1}"); }
	@Test public void test_4151() { checkNotSubtype("{any f2,any f3}","{{any f2} f1}"); }
	@Test public void test_4152() { checkNotSubtype("{any f2,any f3}","{{any f1} f2}"); }
	@Test public void test_4153() { checkNotSubtype("{any f2,any f3}","{{any f2} f2}"); }
	@Test public void test_4154() { checkNotSubtype("{any f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_4155() { checkNotSubtype("{any f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_4156() { checkIsSubtype("{any f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_4157() { checkIsSubtype("{any f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_4158() { checkNotSubtype("{any f2,any f3}","{{null f1} f1}"); }
	@Test public void test_4159() { checkNotSubtype("{any f2,any f3}","{{null f2} f1}"); }
	@Test public void test_4160() { checkNotSubtype("{any f2,any f3}","{{null f1} f2}"); }
	@Test public void test_4161() { checkNotSubtype("{any f2,any f3}","{{null f2} f2}"); }
	@Test public void test_4162() { checkNotSubtype("{any f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_4163() { checkNotSubtype("{any f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_4164() { checkIsSubtype("{any f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_4165() { checkIsSubtype("{any f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_4166() { checkNotSubtype("{any f2,any f3}","{{int f1} f1}"); }
	@Test public void test_4167() { checkNotSubtype("{any f2,any f3}","{{int f2} f1}"); }
	@Test public void test_4168() { checkNotSubtype("{any f2,any f3}","{{int f1} f2}"); }
	@Test public void test_4169() { checkNotSubtype("{any f2,any f3}","{{int f2} f2}"); }
	@Test public void test_4170() { checkNotSubtype("{any f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_4171() { checkNotSubtype("{any f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_4172() { checkIsSubtype("{any f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_4173() { checkIsSubtype("{any f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_4174() { checkNotSubtype("{any f1,null f2}","any"); }
	@Test public void test_4175() { checkNotSubtype("{any f1,null f2}","null"); }
	@Test public void test_4176() { checkNotSubtype("{any f1,null f2}","int"); }
	@Test public void test_4177() { checkNotSubtype("{any f1,null f2}","[void]"); }
	@Test public void test_4178() { checkNotSubtype("{any f1,null f2}","[any]"); }
	@Test public void test_4179() { checkNotSubtype("{any f1,null f2}","[null]"); }
	@Test public void test_4180() { checkNotSubtype("{any f1,null f2}","[int]"); }
	@Test public void test_4181() { checkIsSubtype("{any f1,null f2}","{void f1}"); }
	@Test public void test_4182() { checkIsSubtype("{any f1,null f2}","{void f2}"); }
	@Test public void test_4183() { checkNotSubtype("{any f1,null f2}","{any f1}"); }
	@Test public void test_4184() { checkNotSubtype("{any f1,null f2}","{any f2}"); }
	@Test public void test_4185() { checkNotSubtype("{any f1,null f2}","{null f1}"); }
	@Test public void test_4186() { checkNotSubtype("{any f1,null f2}","{null f2}"); }
	@Test public void test_4187() { checkNotSubtype("{any f1,null f2}","{int f1}"); }
	@Test public void test_4188() { checkNotSubtype("{any f1,null f2}","{int f2}"); }
	@Test public void test_4189() { checkNotSubtype("{any f1,null f2}","[[void]]"); }
	@Test public void test_4190() { checkNotSubtype("{any f1,null f2}","[[any]]"); }
	@Test public void test_4191() { checkNotSubtype("{any f1,null f2}","[[null]]"); }
	@Test public void test_4192() { checkNotSubtype("{any f1,null f2}","[[int]]"); }
	@Test public void test_4193() { checkNotSubtype("{any f1,null f2}","[{void f1}]"); }
	@Test public void test_4194() { checkNotSubtype("{any f1,null f2}","[{void f2}]"); }
	@Test public void test_4195() { checkNotSubtype("{any f1,null f2}","[{any f1}]"); }
	@Test public void test_4196() { checkNotSubtype("{any f1,null f2}","[{any f2}]"); }
	@Test public void test_4197() { checkNotSubtype("{any f1,null f2}","[{null f1}]"); }
	@Test public void test_4198() { checkNotSubtype("{any f1,null f2}","[{null f2}]"); }
	@Test public void test_4199() { checkNotSubtype("{any f1,null f2}","[{int f1}]"); }
	@Test public void test_4200() { checkNotSubtype("{any f1,null f2}","[{int f2}]"); }
	@Test public void test_4201() { checkIsSubtype("{any f1,null f2}","{void f1,void f2}"); }
	@Test public void test_4202() { checkIsSubtype("{any f1,null f2}","{void f2,void f3}"); }
	@Test public void test_4203() { checkIsSubtype("{any f1,null f2}","{void f1,any f2}"); }
	@Test public void test_4204() { checkIsSubtype("{any f1,null f2}","{void f2,any f3}"); }
	@Test public void test_4205() { checkIsSubtype("{any f1,null f2}","{void f1,null f2}"); }
	@Test public void test_4206() { checkIsSubtype("{any f1,null f2}","{void f2,null f3}"); }
	@Test public void test_4207() { checkIsSubtype("{any f1,null f2}","{void f1,int f2}"); }
	@Test public void test_4208() { checkIsSubtype("{any f1,null f2}","{void f2,int f3}"); }
	@Test public void test_4209() { checkIsSubtype("{any f1,null f2}","{any f1,void f2}"); }
	@Test public void test_4210() { checkIsSubtype("{any f1,null f2}","{any f2,void f3}"); }
	@Test public void test_4211() { checkNotSubtype("{any f1,null f2}","{any f1,any f2}"); }
	@Test public void test_4212() { checkNotSubtype("{any f1,null f2}","{any f2,any f3}"); }
	@Test public void test_4213() { checkIsSubtype("{any f1,null f2}","{any f1,null f2}"); }
	@Test public void test_4214() { checkNotSubtype("{any f1,null f2}","{any f2,null f3}"); }
	@Test public void test_4215() { checkNotSubtype("{any f1,null f2}","{any f1,int f2}"); }
	@Test public void test_4216() { checkNotSubtype("{any f1,null f2}","{any f2,int f3}"); }
	@Test public void test_4217() { checkIsSubtype("{any f1,null f2}","{null f1,void f2}"); }
	@Test public void test_4218() { checkIsSubtype("{any f1,null f2}","{null f2,void f3}"); }
	@Test public void test_4219() { checkNotSubtype("{any f1,null f2}","{null f1,any f2}"); }
	@Test public void test_4220() { checkNotSubtype("{any f1,null f2}","{null f2,any f3}"); }
	@Test public void test_4221() { checkIsSubtype("{any f1,null f2}","{null f1,null f2}"); }
	@Test public void test_4222() { checkNotSubtype("{any f1,null f2}","{null f2,null f3}"); }
	@Test public void test_4223() { checkNotSubtype("{any f1,null f2}","{null f1,int f2}"); }
	@Test public void test_4224() { checkNotSubtype("{any f1,null f2}","{null f2,int f3}"); }
	@Test public void test_4225() { checkIsSubtype("{any f1,null f2}","{int f1,void f2}"); }
	@Test public void test_4226() { checkIsSubtype("{any f1,null f2}","{int f2,void f3}"); }
	@Test public void test_4227() { checkNotSubtype("{any f1,null f2}","{int f1,any f2}"); }
	@Test public void test_4228() { checkNotSubtype("{any f1,null f2}","{int f2,any f3}"); }
	@Test public void test_4229() { checkIsSubtype("{any f1,null f2}","{int f1,null f2}"); }
	@Test public void test_4230() { checkNotSubtype("{any f1,null f2}","{int f2,null f3}"); }
	@Test public void test_4231() { checkNotSubtype("{any f1,null f2}","{int f1,int f2}"); }
	@Test public void test_4232() { checkNotSubtype("{any f1,null f2}","{int f2,int f3}"); }
	@Test public void test_4233() { checkNotSubtype("{any f1,null f2}","{[void] f1}"); }
	@Test public void test_4234() { checkNotSubtype("{any f1,null f2}","{[void] f2}"); }
	@Test public void test_4235() { checkIsSubtype("{any f1,null f2}","{[void] f1,void f2}"); }
	@Test public void test_4236() { checkIsSubtype("{any f1,null f2}","{[void] f2,void f3}"); }
	@Test public void test_4237() { checkNotSubtype("{any f1,null f2}","{[any] f1}"); }
	@Test public void test_4238() { checkNotSubtype("{any f1,null f2}","{[any] f2}"); }
	@Test public void test_4239() { checkNotSubtype("{any f1,null f2}","{[any] f1,any f2}"); }
	@Test public void test_4240() { checkNotSubtype("{any f1,null f2}","{[any] f2,any f3}"); }
	@Test public void test_4241() { checkNotSubtype("{any f1,null f2}","{[null] f1}"); }
	@Test public void test_4242() { checkNotSubtype("{any f1,null f2}","{[null] f2}"); }
	@Test public void test_4243() { checkIsSubtype("{any f1,null f2}","{[null] f1,null f2}"); }
	@Test public void test_4244() { checkNotSubtype("{any f1,null f2}","{[null] f2,null f3}"); }
	@Test public void test_4245() { checkNotSubtype("{any f1,null f2}","{[int] f1}"); }
	@Test public void test_4246() { checkNotSubtype("{any f1,null f2}","{[int] f2}"); }
	@Test public void test_4247() { checkNotSubtype("{any f1,null f2}","{[int] f1,int f2}"); }
	@Test public void test_4248() { checkNotSubtype("{any f1,null f2}","{[int] f2,int f3}"); }
	@Test public void test_4249() { checkIsSubtype("{any f1,null f2}","{{void f1} f1}"); }
	@Test public void test_4250() { checkIsSubtype("{any f1,null f2}","{{void f2} f1}"); }
	@Test public void test_4251() { checkIsSubtype("{any f1,null f2}","{{void f1} f2}"); }
	@Test public void test_4252() { checkIsSubtype("{any f1,null f2}","{{void f2} f2}"); }
	@Test public void test_4253() { checkIsSubtype("{any f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4254() { checkIsSubtype("{any f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4255() { checkIsSubtype("{any f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4256() { checkIsSubtype("{any f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4257() { checkNotSubtype("{any f1,null f2}","{{any f1} f1}"); }
	@Test public void test_4258() { checkNotSubtype("{any f1,null f2}","{{any f2} f1}"); }
	@Test public void test_4259() { checkNotSubtype("{any f1,null f2}","{{any f1} f2}"); }
	@Test public void test_4260() { checkNotSubtype("{any f1,null f2}","{{any f2} f2}"); }
	@Test public void test_4261() { checkNotSubtype("{any f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4262() { checkNotSubtype("{any f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4263() { checkNotSubtype("{any f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4264() { checkNotSubtype("{any f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4265() { checkNotSubtype("{any f1,null f2}","{{null f1} f1}"); }
	@Test public void test_4266() { checkNotSubtype("{any f1,null f2}","{{null f2} f1}"); }
	@Test public void test_4267() { checkNotSubtype("{any f1,null f2}","{{null f1} f2}"); }
	@Test public void test_4268() { checkNotSubtype("{any f1,null f2}","{{null f2} f2}"); }
	@Test public void test_4269() { checkIsSubtype("{any f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4270() { checkIsSubtype("{any f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4271() { checkNotSubtype("{any f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4272() { checkNotSubtype("{any f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4273() { checkNotSubtype("{any f1,null f2}","{{int f1} f1}"); }
	@Test public void test_4274() { checkNotSubtype("{any f1,null f2}","{{int f2} f1}"); }
	@Test public void test_4275() { checkNotSubtype("{any f1,null f2}","{{int f1} f2}"); }
	@Test public void test_4276() { checkNotSubtype("{any f1,null f2}","{{int f2} f2}"); }
	@Test public void test_4277() { checkNotSubtype("{any f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4278() { checkNotSubtype("{any f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4279() { checkNotSubtype("{any f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4280() { checkNotSubtype("{any f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4281() { checkNotSubtype("{any f2,null f3}","any"); }
	@Test public void test_4282() { checkNotSubtype("{any f2,null f3}","null"); }
	@Test public void test_4283() { checkNotSubtype("{any f2,null f3}","int"); }
	@Test public void test_4284() { checkNotSubtype("{any f2,null f3}","[void]"); }
	@Test public void test_4285() { checkNotSubtype("{any f2,null f3}","[any]"); }
	@Test public void test_4286() { checkNotSubtype("{any f2,null f3}","[null]"); }
	@Test public void test_4287() { checkNotSubtype("{any f2,null f3}","[int]"); }
	@Test public void test_4288() { checkIsSubtype("{any f2,null f3}","{void f1}"); }
	@Test public void test_4289() { checkIsSubtype("{any f2,null f3}","{void f2}"); }
	@Test public void test_4290() { checkNotSubtype("{any f2,null f3}","{any f1}"); }
	@Test public void test_4291() { checkNotSubtype("{any f2,null f3}","{any f2}"); }
	@Test public void test_4292() { checkNotSubtype("{any f2,null f3}","{null f1}"); }
	@Test public void test_4293() { checkNotSubtype("{any f2,null f3}","{null f2}"); }
	@Test public void test_4294() { checkNotSubtype("{any f2,null f3}","{int f1}"); }
	@Test public void test_4295() { checkNotSubtype("{any f2,null f3}","{int f2}"); }
	@Test public void test_4296() { checkNotSubtype("{any f2,null f3}","[[void]]"); }
	@Test public void test_4297() { checkNotSubtype("{any f2,null f3}","[[any]]"); }
	@Test public void test_4298() { checkNotSubtype("{any f2,null f3}","[[null]]"); }
	@Test public void test_4299() { checkNotSubtype("{any f2,null f3}","[[int]]"); }
	@Test public void test_4300() { checkNotSubtype("{any f2,null f3}","[{void f1}]"); }
	@Test public void test_4301() { checkNotSubtype("{any f2,null f3}","[{void f2}]"); }
	@Test public void test_4302() { checkNotSubtype("{any f2,null f3}","[{any f1}]"); }
	@Test public void test_4303() { checkNotSubtype("{any f2,null f3}","[{any f2}]"); }
	@Test public void test_4304() { checkNotSubtype("{any f2,null f3}","[{null f1}]"); }
	@Test public void test_4305() { checkNotSubtype("{any f2,null f3}","[{null f2}]"); }
	@Test public void test_4306() { checkNotSubtype("{any f2,null f3}","[{int f1}]"); }
	@Test public void test_4307() { checkNotSubtype("{any f2,null f3}","[{int f2}]"); }
	@Test public void test_4308() { checkIsSubtype("{any f2,null f3}","{void f1,void f2}"); }
	@Test public void test_4309() { checkIsSubtype("{any f2,null f3}","{void f2,void f3}"); }
	@Test public void test_4310() { checkIsSubtype("{any f2,null f3}","{void f1,any f2}"); }
	@Test public void test_4311() { checkIsSubtype("{any f2,null f3}","{void f2,any f3}"); }
	@Test public void test_4312() { checkIsSubtype("{any f2,null f3}","{void f1,null f2}"); }
	@Test public void test_4313() { checkIsSubtype("{any f2,null f3}","{void f2,null f3}"); }
	@Test public void test_4314() { checkIsSubtype("{any f2,null f3}","{void f1,int f2}"); }
	@Test public void test_4315() { checkIsSubtype("{any f2,null f3}","{void f2,int f3}"); }
	@Test public void test_4316() { checkIsSubtype("{any f2,null f3}","{any f1,void f2}"); }
	@Test public void test_4317() { checkIsSubtype("{any f2,null f3}","{any f2,void f3}"); }
	@Test public void test_4318() { checkNotSubtype("{any f2,null f3}","{any f1,any f2}"); }
	@Test public void test_4319() { checkNotSubtype("{any f2,null f3}","{any f2,any f3}"); }
	@Test public void test_4320() { checkNotSubtype("{any f2,null f3}","{any f1,null f2}"); }
	@Test public void test_4321() { checkIsSubtype("{any f2,null f3}","{any f2,null f3}"); }
	@Test public void test_4322() { checkNotSubtype("{any f2,null f3}","{any f1,int f2}"); }
	@Test public void test_4323() { checkNotSubtype("{any f2,null f3}","{any f2,int f3}"); }
	@Test public void test_4324() { checkIsSubtype("{any f2,null f3}","{null f1,void f2}"); }
	@Test public void test_4325() { checkIsSubtype("{any f2,null f3}","{null f2,void f3}"); }
	@Test public void test_4326() { checkNotSubtype("{any f2,null f3}","{null f1,any f2}"); }
	@Test public void test_4327() { checkNotSubtype("{any f2,null f3}","{null f2,any f3}"); }
	@Test public void test_4328() { checkNotSubtype("{any f2,null f3}","{null f1,null f2}"); }
	@Test public void test_4329() { checkIsSubtype("{any f2,null f3}","{null f2,null f3}"); }
	@Test public void test_4330() { checkNotSubtype("{any f2,null f3}","{null f1,int f2}"); }
	@Test public void test_4331() { checkNotSubtype("{any f2,null f3}","{null f2,int f3}"); }
	@Test public void test_4332() { checkIsSubtype("{any f2,null f3}","{int f1,void f2}"); }
	@Test public void test_4333() { checkIsSubtype("{any f2,null f3}","{int f2,void f3}"); }
	@Test public void test_4334() { checkNotSubtype("{any f2,null f3}","{int f1,any f2}"); }
	@Test public void test_4335() { checkNotSubtype("{any f2,null f3}","{int f2,any f3}"); }
	@Test public void test_4336() { checkNotSubtype("{any f2,null f3}","{int f1,null f2}"); }
	@Test public void test_4337() { checkIsSubtype("{any f2,null f3}","{int f2,null f3}"); }
	@Test public void test_4338() { checkNotSubtype("{any f2,null f3}","{int f1,int f2}"); }
	@Test public void test_4339() { checkNotSubtype("{any f2,null f3}","{int f2,int f3}"); }
	@Test public void test_4340() { checkNotSubtype("{any f2,null f3}","{[void] f1}"); }
	@Test public void test_4341() { checkNotSubtype("{any f2,null f3}","{[void] f2}"); }
	@Test public void test_4342() { checkIsSubtype("{any f2,null f3}","{[void] f1,void f2}"); }
	@Test public void test_4343() { checkIsSubtype("{any f2,null f3}","{[void] f2,void f3}"); }
	@Test public void test_4344() { checkNotSubtype("{any f2,null f3}","{[any] f1}"); }
	@Test public void test_4345() { checkNotSubtype("{any f2,null f3}","{[any] f2}"); }
	@Test public void test_4346() { checkNotSubtype("{any f2,null f3}","{[any] f1,any f2}"); }
	@Test public void test_4347() { checkNotSubtype("{any f2,null f3}","{[any] f2,any f3}"); }
	@Test public void test_4348() { checkNotSubtype("{any f2,null f3}","{[null] f1}"); }
	@Test public void test_4349() { checkNotSubtype("{any f2,null f3}","{[null] f2}"); }
	@Test public void test_4350() { checkNotSubtype("{any f2,null f3}","{[null] f1,null f2}"); }
	@Test public void test_4351() { checkIsSubtype("{any f2,null f3}","{[null] f2,null f3}"); }
	@Test public void test_4352() { checkNotSubtype("{any f2,null f3}","{[int] f1}"); }
	@Test public void test_4353() { checkNotSubtype("{any f2,null f3}","{[int] f2}"); }
	@Test public void test_4354() { checkNotSubtype("{any f2,null f3}","{[int] f1,int f2}"); }
	@Test public void test_4355() { checkNotSubtype("{any f2,null f3}","{[int] f2,int f3}"); }
	@Test public void test_4356() { checkIsSubtype("{any f2,null f3}","{{void f1} f1}"); }
	@Test public void test_4357() { checkIsSubtype("{any f2,null f3}","{{void f2} f1}"); }
	@Test public void test_4358() { checkIsSubtype("{any f2,null f3}","{{void f1} f2}"); }
	@Test public void test_4359() { checkIsSubtype("{any f2,null f3}","{{void f2} f2}"); }
	@Test public void test_4360() { checkIsSubtype("{any f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_4361() { checkIsSubtype("{any f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_4362() { checkIsSubtype("{any f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_4363() { checkIsSubtype("{any f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_4364() { checkNotSubtype("{any f2,null f3}","{{any f1} f1}"); }
	@Test public void test_4365() { checkNotSubtype("{any f2,null f3}","{{any f2} f1}"); }
	@Test public void test_4366() { checkNotSubtype("{any f2,null f3}","{{any f1} f2}"); }
	@Test public void test_4367() { checkNotSubtype("{any f2,null f3}","{{any f2} f2}"); }
	@Test public void test_4368() { checkNotSubtype("{any f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_4369() { checkNotSubtype("{any f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_4370() { checkNotSubtype("{any f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_4371() { checkNotSubtype("{any f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_4372() { checkNotSubtype("{any f2,null f3}","{{null f1} f1}"); }
	@Test public void test_4373() { checkNotSubtype("{any f2,null f3}","{{null f2} f1}"); }
	@Test public void test_4374() { checkNotSubtype("{any f2,null f3}","{{null f1} f2}"); }
	@Test public void test_4375() { checkNotSubtype("{any f2,null f3}","{{null f2} f2}"); }
	@Test public void test_4376() { checkNotSubtype("{any f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_4377() { checkNotSubtype("{any f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_4378() { checkIsSubtype("{any f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_4379() { checkIsSubtype("{any f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_4380() { checkNotSubtype("{any f2,null f3}","{{int f1} f1}"); }
	@Test public void test_4381() { checkNotSubtype("{any f2,null f3}","{{int f2} f1}"); }
	@Test public void test_4382() { checkNotSubtype("{any f2,null f3}","{{int f1} f2}"); }
	@Test public void test_4383() { checkNotSubtype("{any f2,null f3}","{{int f2} f2}"); }
	@Test public void test_4384() { checkNotSubtype("{any f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_4385() { checkNotSubtype("{any f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_4386() { checkNotSubtype("{any f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_4387() { checkNotSubtype("{any f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_4388() { checkNotSubtype("{any f1,int f2}","any"); }
	@Test public void test_4389() { checkNotSubtype("{any f1,int f2}","null"); }
	@Test public void test_4390() { checkNotSubtype("{any f1,int f2}","int"); }
	@Test public void test_4391() { checkNotSubtype("{any f1,int f2}","[void]"); }
	@Test public void test_4392() { checkNotSubtype("{any f1,int f2}","[any]"); }
	@Test public void test_4393() { checkNotSubtype("{any f1,int f2}","[null]"); }
	@Test public void test_4394() { checkNotSubtype("{any f1,int f2}","[int]"); }
	@Test public void test_4395() { checkIsSubtype("{any f1,int f2}","{void f1}"); }
	@Test public void test_4396() { checkIsSubtype("{any f1,int f2}","{void f2}"); }
	@Test public void test_4397() { checkNotSubtype("{any f1,int f2}","{any f1}"); }
	@Test public void test_4398() { checkNotSubtype("{any f1,int f2}","{any f2}"); }
	@Test public void test_4399() { checkNotSubtype("{any f1,int f2}","{null f1}"); }
	@Test public void test_4400() { checkNotSubtype("{any f1,int f2}","{null f2}"); }
	@Test public void test_4401() { checkNotSubtype("{any f1,int f2}","{int f1}"); }
	@Test public void test_4402() { checkNotSubtype("{any f1,int f2}","{int f2}"); }
	@Test public void test_4403() { checkNotSubtype("{any f1,int f2}","[[void]]"); }
	@Test public void test_4404() { checkNotSubtype("{any f1,int f2}","[[any]]"); }
	@Test public void test_4405() { checkNotSubtype("{any f1,int f2}","[[null]]"); }
	@Test public void test_4406() { checkNotSubtype("{any f1,int f2}","[[int]]"); }
	@Test public void test_4407() { checkNotSubtype("{any f1,int f2}","[{void f1}]"); }
	@Test public void test_4408() { checkNotSubtype("{any f1,int f2}","[{void f2}]"); }
	@Test public void test_4409() { checkNotSubtype("{any f1,int f2}","[{any f1}]"); }
	@Test public void test_4410() { checkNotSubtype("{any f1,int f2}","[{any f2}]"); }
	@Test public void test_4411() { checkNotSubtype("{any f1,int f2}","[{null f1}]"); }
	@Test public void test_4412() { checkNotSubtype("{any f1,int f2}","[{null f2}]"); }
	@Test public void test_4413() { checkNotSubtype("{any f1,int f2}","[{int f1}]"); }
	@Test public void test_4414() { checkNotSubtype("{any f1,int f2}","[{int f2}]"); }
	@Test public void test_4415() { checkIsSubtype("{any f1,int f2}","{void f1,void f2}"); }
	@Test public void test_4416() { checkIsSubtype("{any f1,int f2}","{void f2,void f3}"); }
	@Test public void test_4417() { checkIsSubtype("{any f1,int f2}","{void f1,any f2}"); }
	@Test public void test_4418() { checkIsSubtype("{any f1,int f2}","{void f2,any f3}"); }
	@Test public void test_4419() { checkIsSubtype("{any f1,int f2}","{void f1,null f2}"); }
	@Test public void test_4420() { checkIsSubtype("{any f1,int f2}","{void f2,null f3}"); }
	@Test public void test_4421() { checkIsSubtype("{any f1,int f2}","{void f1,int f2}"); }
	@Test public void test_4422() { checkIsSubtype("{any f1,int f2}","{void f2,int f3}"); }
	@Test public void test_4423() { checkIsSubtype("{any f1,int f2}","{any f1,void f2}"); }
	@Test public void test_4424() { checkIsSubtype("{any f1,int f2}","{any f2,void f3}"); }
	@Test public void test_4425() { checkNotSubtype("{any f1,int f2}","{any f1,any f2}"); }
	@Test public void test_4426() { checkNotSubtype("{any f1,int f2}","{any f2,any f3}"); }
	@Test public void test_4427() { checkNotSubtype("{any f1,int f2}","{any f1,null f2}"); }
	@Test public void test_4428() { checkNotSubtype("{any f1,int f2}","{any f2,null f3}"); }
	@Test public void test_4429() { checkIsSubtype("{any f1,int f2}","{any f1,int f2}"); }
	@Test public void test_4430() { checkNotSubtype("{any f1,int f2}","{any f2,int f3}"); }
	@Test public void test_4431() { checkIsSubtype("{any f1,int f2}","{null f1,void f2}"); }
	@Test public void test_4432() { checkIsSubtype("{any f1,int f2}","{null f2,void f3}"); }
	@Test public void test_4433() { checkNotSubtype("{any f1,int f2}","{null f1,any f2}"); }
	@Test public void test_4434() { checkNotSubtype("{any f1,int f2}","{null f2,any f3}"); }
	@Test public void test_4435() { checkNotSubtype("{any f1,int f2}","{null f1,null f2}"); }
	@Test public void test_4436() { checkNotSubtype("{any f1,int f2}","{null f2,null f3}"); }
	@Test public void test_4437() { checkIsSubtype("{any f1,int f2}","{null f1,int f2}"); }
	@Test public void test_4438() { checkNotSubtype("{any f1,int f2}","{null f2,int f3}"); }
	@Test public void test_4439() { checkIsSubtype("{any f1,int f2}","{int f1,void f2}"); }
	@Test public void test_4440() { checkIsSubtype("{any f1,int f2}","{int f2,void f3}"); }
	@Test public void test_4441() { checkNotSubtype("{any f1,int f2}","{int f1,any f2}"); }
	@Test public void test_4442() { checkNotSubtype("{any f1,int f2}","{int f2,any f3}"); }
	@Test public void test_4443() { checkNotSubtype("{any f1,int f2}","{int f1,null f2}"); }
	@Test public void test_4444() { checkNotSubtype("{any f1,int f2}","{int f2,null f3}"); }
	@Test public void test_4445() { checkIsSubtype("{any f1,int f2}","{int f1,int f2}"); }
	@Test public void test_4446() { checkNotSubtype("{any f1,int f2}","{int f2,int f3}"); }
	@Test public void test_4447() { checkNotSubtype("{any f1,int f2}","{[void] f1}"); }
	@Test public void test_4448() { checkNotSubtype("{any f1,int f2}","{[void] f2}"); }
	@Test public void test_4449() { checkIsSubtype("{any f1,int f2}","{[void] f1,void f2}"); }
	@Test public void test_4450() { checkIsSubtype("{any f1,int f2}","{[void] f2,void f3}"); }
	@Test public void test_4451() { checkNotSubtype("{any f1,int f2}","{[any] f1}"); }
	@Test public void test_4452() { checkNotSubtype("{any f1,int f2}","{[any] f2}"); }
	@Test public void test_4453() { checkNotSubtype("{any f1,int f2}","{[any] f1,any f2}"); }
	@Test public void test_4454() { checkNotSubtype("{any f1,int f2}","{[any] f2,any f3}"); }
	@Test public void test_4455() { checkNotSubtype("{any f1,int f2}","{[null] f1}"); }
	@Test public void test_4456() { checkNotSubtype("{any f1,int f2}","{[null] f2}"); }
	@Test public void test_4457() { checkNotSubtype("{any f1,int f2}","{[null] f1,null f2}"); }
	@Test public void test_4458() { checkNotSubtype("{any f1,int f2}","{[null] f2,null f3}"); }
	@Test public void test_4459() { checkNotSubtype("{any f1,int f2}","{[int] f1}"); }
	@Test public void test_4460() { checkNotSubtype("{any f1,int f2}","{[int] f2}"); }
	@Test public void test_4461() { checkIsSubtype("{any f1,int f2}","{[int] f1,int f2}"); }
	@Test public void test_4462() { checkNotSubtype("{any f1,int f2}","{[int] f2,int f3}"); }
	@Test public void test_4463() { checkIsSubtype("{any f1,int f2}","{{void f1} f1}"); }
	@Test public void test_4464() { checkIsSubtype("{any f1,int f2}","{{void f2} f1}"); }
	@Test public void test_4465() { checkIsSubtype("{any f1,int f2}","{{void f1} f2}"); }
	@Test public void test_4466() { checkIsSubtype("{any f1,int f2}","{{void f2} f2}"); }
	@Test public void test_4467() { checkIsSubtype("{any f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4468() { checkIsSubtype("{any f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4469() { checkIsSubtype("{any f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4470() { checkIsSubtype("{any f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4471() { checkNotSubtype("{any f1,int f2}","{{any f1} f1}"); }
	@Test public void test_4472() { checkNotSubtype("{any f1,int f2}","{{any f2} f1}"); }
	@Test public void test_4473() { checkNotSubtype("{any f1,int f2}","{{any f1} f2}"); }
	@Test public void test_4474() { checkNotSubtype("{any f1,int f2}","{{any f2} f2}"); }
	@Test public void test_4475() { checkNotSubtype("{any f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4476() { checkNotSubtype("{any f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4477() { checkNotSubtype("{any f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4478() { checkNotSubtype("{any f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4479() { checkNotSubtype("{any f1,int f2}","{{null f1} f1}"); }
	@Test public void test_4480() { checkNotSubtype("{any f1,int f2}","{{null f2} f1}"); }
	@Test public void test_4481() { checkNotSubtype("{any f1,int f2}","{{null f1} f2}"); }
	@Test public void test_4482() { checkNotSubtype("{any f1,int f2}","{{null f2} f2}"); }
	@Test public void test_4483() { checkNotSubtype("{any f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4484() { checkNotSubtype("{any f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4485() { checkNotSubtype("{any f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4486() { checkNotSubtype("{any f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4487() { checkNotSubtype("{any f1,int f2}","{{int f1} f1}"); }
	@Test public void test_4488() { checkNotSubtype("{any f1,int f2}","{{int f2} f1}"); }
	@Test public void test_4489() { checkNotSubtype("{any f1,int f2}","{{int f1} f2}"); }
	@Test public void test_4490() { checkNotSubtype("{any f1,int f2}","{{int f2} f2}"); }
	@Test public void test_4491() { checkIsSubtype("{any f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4492() { checkIsSubtype("{any f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4493() { checkNotSubtype("{any f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4494() { checkNotSubtype("{any f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4495() { checkNotSubtype("{any f2,int f3}","any"); }
	@Test public void test_4496() { checkNotSubtype("{any f2,int f3}","null"); }
	@Test public void test_4497() { checkNotSubtype("{any f2,int f3}","int"); }
	@Test public void test_4498() { checkNotSubtype("{any f2,int f3}","[void]"); }
	@Test public void test_4499() { checkNotSubtype("{any f2,int f3}","[any]"); }
	@Test public void test_4500() { checkNotSubtype("{any f2,int f3}","[null]"); }
	@Test public void test_4501() { checkNotSubtype("{any f2,int f3}","[int]"); }
	@Test public void test_4502() { checkIsSubtype("{any f2,int f3}","{void f1}"); }
	@Test public void test_4503() { checkIsSubtype("{any f2,int f3}","{void f2}"); }
	@Test public void test_4504() { checkNotSubtype("{any f2,int f3}","{any f1}"); }
	@Test public void test_4505() { checkNotSubtype("{any f2,int f3}","{any f2}"); }
	@Test public void test_4506() { checkNotSubtype("{any f2,int f3}","{null f1}"); }
	@Test public void test_4507() { checkNotSubtype("{any f2,int f3}","{null f2}"); }
	@Test public void test_4508() { checkNotSubtype("{any f2,int f3}","{int f1}"); }
	@Test public void test_4509() { checkNotSubtype("{any f2,int f3}","{int f2}"); }
	@Test public void test_4510() { checkNotSubtype("{any f2,int f3}","[[void]]"); }
	@Test public void test_4511() { checkNotSubtype("{any f2,int f3}","[[any]]"); }
	@Test public void test_4512() { checkNotSubtype("{any f2,int f3}","[[null]]"); }
	@Test public void test_4513() { checkNotSubtype("{any f2,int f3}","[[int]]"); }
	@Test public void test_4514() { checkNotSubtype("{any f2,int f3}","[{void f1}]"); }
	@Test public void test_4515() { checkNotSubtype("{any f2,int f3}","[{void f2}]"); }
	@Test public void test_4516() { checkNotSubtype("{any f2,int f3}","[{any f1}]"); }
	@Test public void test_4517() { checkNotSubtype("{any f2,int f3}","[{any f2}]"); }
	@Test public void test_4518() { checkNotSubtype("{any f2,int f3}","[{null f1}]"); }
	@Test public void test_4519() { checkNotSubtype("{any f2,int f3}","[{null f2}]"); }
	@Test public void test_4520() { checkNotSubtype("{any f2,int f3}","[{int f1}]"); }
	@Test public void test_4521() { checkNotSubtype("{any f2,int f3}","[{int f2}]"); }
	@Test public void test_4522() { checkIsSubtype("{any f2,int f3}","{void f1,void f2}"); }
	@Test public void test_4523() { checkIsSubtype("{any f2,int f3}","{void f2,void f3}"); }
	@Test public void test_4524() { checkIsSubtype("{any f2,int f3}","{void f1,any f2}"); }
	@Test public void test_4525() { checkIsSubtype("{any f2,int f3}","{void f2,any f3}"); }
	@Test public void test_4526() { checkIsSubtype("{any f2,int f3}","{void f1,null f2}"); }
	@Test public void test_4527() { checkIsSubtype("{any f2,int f3}","{void f2,null f3}"); }
	@Test public void test_4528() { checkIsSubtype("{any f2,int f3}","{void f1,int f2}"); }
	@Test public void test_4529() { checkIsSubtype("{any f2,int f3}","{void f2,int f3}"); }
	@Test public void test_4530() { checkIsSubtype("{any f2,int f3}","{any f1,void f2}"); }
	@Test public void test_4531() { checkIsSubtype("{any f2,int f3}","{any f2,void f3}"); }
	@Test public void test_4532() { checkNotSubtype("{any f2,int f3}","{any f1,any f2}"); }
	@Test public void test_4533() { checkNotSubtype("{any f2,int f3}","{any f2,any f3}"); }
	@Test public void test_4534() { checkNotSubtype("{any f2,int f3}","{any f1,null f2}"); }
	@Test public void test_4535() { checkNotSubtype("{any f2,int f3}","{any f2,null f3}"); }
	@Test public void test_4536() { checkNotSubtype("{any f2,int f3}","{any f1,int f2}"); }
	@Test public void test_4537() { checkIsSubtype("{any f2,int f3}","{any f2,int f3}"); }
	@Test public void test_4538() { checkIsSubtype("{any f2,int f3}","{null f1,void f2}"); }
	@Test public void test_4539() { checkIsSubtype("{any f2,int f3}","{null f2,void f3}"); }
	@Test public void test_4540() { checkNotSubtype("{any f2,int f3}","{null f1,any f2}"); }
	@Test public void test_4541() { checkNotSubtype("{any f2,int f3}","{null f2,any f3}"); }
	@Test public void test_4542() { checkNotSubtype("{any f2,int f3}","{null f1,null f2}"); }
	@Test public void test_4543() { checkNotSubtype("{any f2,int f3}","{null f2,null f3}"); }
	@Test public void test_4544() { checkNotSubtype("{any f2,int f3}","{null f1,int f2}"); }
	@Test public void test_4545() { checkIsSubtype("{any f2,int f3}","{null f2,int f3}"); }
	@Test public void test_4546() { checkIsSubtype("{any f2,int f3}","{int f1,void f2}"); }
	@Test public void test_4547() { checkIsSubtype("{any f2,int f3}","{int f2,void f3}"); }
	@Test public void test_4548() { checkNotSubtype("{any f2,int f3}","{int f1,any f2}"); }
	@Test public void test_4549() { checkNotSubtype("{any f2,int f3}","{int f2,any f3}"); }
	@Test public void test_4550() { checkNotSubtype("{any f2,int f3}","{int f1,null f2}"); }
	@Test public void test_4551() { checkNotSubtype("{any f2,int f3}","{int f2,null f3}"); }
	@Test public void test_4552() { checkNotSubtype("{any f2,int f3}","{int f1,int f2}"); }
	@Test public void test_4553() { checkIsSubtype("{any f2,int f3}","{int f2,int f3}"); }
	@Test public void test_4554() { checkNotSubtype("{any f2,int f3}","{[void] f1}"); }
	@Test public void test_4555() { checkNotSubtype("{any f2,int f3}","{[void] f2}"); }
	@Test public void test_4556() { checkIsSubtype("{any f2,int f3}","{[void] f1,void f2}"); }
	@Test public void test_4557() { checkIsSubtype("{any f2,int f3}","{[void] f2,void f3}"); }
	@Test public void test_4558() { checkNotSubtype("{any f2,int f3}","{[any] f1}"); }
	@Test public void test_4559() { checkNotSubtype("{any f2,int f3}","{[any] f2}"); }
	@Test public void test_4560() { checkNotSubtype("{any f2,int f3}","{[any] f1,any f2}"); }
	@Test public void test_4561() { checkNotSubtype("{any f2,int f3}","{[any] f2,any f3}"); }
	@Test public void test_4562() { checkNotSubtype("{any f2,int f3}","{[null] f1}"); }
	@Test public void test_4563() { checkNotSubtype("{any f2,int f3}","{[null] f2}"); }
	@Test public void test_4564() { checkNotSubtype("{any f2,int f3}","{[null] f1,null f2}"); }
	@Test public void test_4565() { checkNotSubtype("{any f2,int f3}","{[null] f2,null f3}"); }
	@Test public void test_4566() { checkNotSubtype("{any f2,int f3}","{[int] f1}"); }
	@Test public void test_4567() { checkNotSubtype("{any f2,int f3}","{[int] f2}"); }
	@Test public void test_4568() { checkNotSubtype("{any f2,int f3}","{[int] f1,int f2}"); }
	@Test public void test_4569() { checkIsSubtype("{any f2,int f3}","{[int] f2,int f3}"); }
	@Test public void test_4570() { checkIsSubtype("{any f2,int f3}","{{void f1} f1}"); }
	@Test public void test_4571() { checkIsSubtype("{any f2,int f3}","{{void f2} f1}"); }
	@Test public void test_4572() { checkIsSubtype("{any f2,int f3}","{{void f1} f2}"); }
	@Test public void test_4573() { checkIsSubtype("{any f2,int f3}","{{void f2} f2}"); }
	@Test public void test_4574() { checkIsSubtype("{any f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_4575() { checkIsSubtype("{any f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_4576() { checkIsSubtype("{any f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_4577() { checkIsSubtype("{any f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_4578() { checkNotSubtype("{any f2,int f3}","{{any f1} f1}"); }
	@Test public void test_4579() { checkNotSubtype("{any f2,int f3}","{{any f2} f1}"); }
	@Test public void test_4580() { checkNotSubtype("{any f2,int f3}","{{any f1} f2}"); }
	@Test public void test_4581() { checkNotSubtype("{any f2,int f3}","{{any f2} f2}"); }
	@Test public void test_4582() { checkNotSubtype("{any f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_4583() { checkNotSubtype("{any f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_4584() { checkNotSubtype("{any f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_4585() { checkNotSubtype("{any f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_4586() { checkNotSubtype("{any f2,int f3}","{{null f1} f1}"); }
	@Test public void test_4587() { checkNotSubtype("{any f2,int f3}","{{null f2} f1}"); }
	@Test public void test_4588() { checkNotSubtype("{any f2,int f3}","{{null f1} f2}"); }
	@Test public void test_4589() { checkNotSubtype("{any f2,int f3}","{{null f2} f2}"); }
	@Test public void test_4590() { checkNotSubtype("{any f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_4591() { checkNotSubtype("{any f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_4592() { checkNotSubtype("{any f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_4593() { checkNotSubtype("{any f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_4594() { checkNotSubtype("{any f2,int f3}","{{int f1} f1}"); }
	@Test public void test_4595() { checkNotSubtype("{any f2,int f3}","{{int f2} f1}"); }
	@Test public void test_4596() { checkNotSubtype("{any f2,int f3}","{{int f1} f2}"); }
	@Test public void test_4597() { checkNotSubtype("{any f2,int f3}","{{int f2} f2}"); }
	@Test public void test_4598() { checkNotSubtype("{any f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_4599() { checkNotSubtype("{any f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_4600() { checkIsSubtype("{any f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_4601() { checkIsSubtype("{any f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_4602() { checkNotSubtype("{null f1,void f2}","any"); }
	@Test public void test_4603() { checkNotSubtype("{null f1,void f2}","null"); }
	@Test public void test_4604() { checkNotSubtype("{null f1,void f2}","int"); }
	@Test public void test_4605() { checkNotSubtype("{null f1,void f2}","[void]"); }
	@Test public void test_4606() { checkNotSubtype("{null f1,void f2}","[any]"); }
	@Test public void test_4607() { checkNotSubtype("{null f1,void f2}","[null]"); }
	@Test public void test_4608() { checkNotSubtype("{null f1,void f2}","[int]"); }
	@Test public void test_4609() { checkIsSubtype("{null f1,void f2}","{void f1}"); }
	@Test public void test_4610() { checkIsSubtype("{null f1,void f2}","{void f2}"); }
	@Test public void test_4611() { checkNotSubtype("{null f1,void f2}","{any f1}"); }
	@Test public void test_4612() { checkNotSubtype("{null f1,void f2}","{any f2}"); }
	@Test public void test_4613() { checkNotSubtype("{null f1,void f2}","{null f1}"); }
	@Test public void test_4614() { checkNotSubtype("{null f1,void f2}","{null f2}"); }
	@Test public void test_4615() { checkNotSubtype("{null f1,void f2}","{int f1}"); }
	@Test public void test_4616() { checkNotSubtype("{null f1,void f2}","{int f2}"); }
	@Test public void test_4617() { checkNotSubtype("{null f1,void f2}","[[void]]"); }
	@Test public void test_4618() { checkNotSubtype("{null f1,void f2}","[[any]]"); }
	@Test public void test_4619() { checkNotSubtype("{null f1,void f2}","[[null]]"); }
	@Test public void test_4620() { checkNotSubtype("{null f1,void f2}","[[int]]"); }
	@Test public void test_4621() { checkNotSubtype("{null f1,void f2}","[{void f1}]"); }
	@Test public void test_4622() { checkNotSubtype("{null f1,void f2}","[{void f2}]"); }
	@Test public void test_4623() { checkNotSubtype("{null f1,void f2}","[{any f1}]"); }
	@Test public void test_4624() { checkNotSubtype("{null f1,void f2}","[{any f2}]"); }
	@Test public void test_4625() { checkNotSubtype("{null f1,void f2}","[{null f1}]"); }
	@Test public void test_4626() { checkNotSubtype("{null f1,void f2}","[{null f2}]"); }
	@Test public void test_4627() { checkNotSubtype("{null f1,void f2}","[{int f1}]"); }
	@Test public void test_4628() { checkNotSubtype("{null f1,void f2}","[{int f2}]"); }
	@Test public void test_4629() { checkIsSubtype("{null f1,void f2}","{void f1,void f2}"); }
	@Test public void test_4630() { checkIsSubtype("{null f1,void f2}","{void f2,void f3}"); }
	@Test public void test_4631() { checkIsSubtype("{null f1,void f2}","{void f1,any f2}"); }
	@Test public void test_4632() { checkIsSubtype("{null f1,void f2}","{void f2,any f3}"); }
	@Test public void test_4633() { checkIsSubtype("{null f1,void f2}","{void f1,null f2}"); }
	@Test public void test_4634() { checkIsSubtype("{null f1,void f2}","{void f2,null f3}"); }
	@Test public void test_4635() { checkIsSubtype("{null f1,void f2}","{void f1,int f2}"); }
	@Test public void test_4636() { checkIsSubtype("{null f1,void f2}","{void f2,int f3}"); }
	@Test public void test_4637() { checkIsSubtype("{null f1,void f2}","{any f1,void f2}"); }
	@Test public void test_4638() { checkIsSubtype("{null f1,void f2}","{any f2,void f3}"); }
	@Test public void test_4639() { checkNotSubtype("{null f1,void f2}","{any f1,any f2}"); }
	@Test public void test_4640() { checkNotSubtype("{null f1,void f2}","{any f2,any f3}"); }
	@Test public void test_4641() { checkNotSubtype("{null f1,void f2}","{any f1,null f2}"); }
	@Test public void test_4642() { checkNotSubtype("{null f1,void f2}","{any f2,null f3}"); }
	@Test public void test_4643() { checkNotSubtype("{null f1,void f2}","{any f1,int f2}"); }
	@Test public void test_4644() { checkNotSubtype("{null f1,void f2}","{any f2,int f3}"); }
	@Test public void test_4645() { checkIsSubtype("{null f1,void f2}","{null f1,void f2}"); }
	@Test public void test_4646() { checkIsSubtype("{null f1,void f2}","{null f2,void f3}"); }
	@Test public void test_4647() { checkNotSubtype("{null f1,void f2}","{null f1,any f2}"); }
	@Test public void test_4648() { checkNotSubtype("{null f1,void f2}","{null f2,any f3}"); }
	@Test public void test_4649() { checkNotSubtype("{null f1,void f2}","{null f1,null f2}"); }
	@Test public void test_4650() { checkNotSubtype("{null f1,void f2}","{null f2,null f3}"); }
	@Test public void test_4651() { checkNotSubtype("{null f1,void f2}","{null f1,int f2}"); }
	@Test public void test_4652() { checkNotSubtype("{null f1,void f2}","{null f2,int f3}"); }
	@Test public void test_4653() { checkIsSubtype("{null f1,void f2}","{int f1,void f2}"); }
	@Test public void test_4654() { checkIsSubtype("{null f1,void f2}","{int f2,void f3}"); }
	@Test public void test_4655() { checkNotSubtype("{null f1,void f2}","{int f1,any f2}"); }
	@Test public void test_4656() { checkNotSubtype("{null f1,void f2}","{int f2,any f3}"); }
	@Test public void test_4657() { checkNotSubtype("{null f1,void f2}","{int f1,null f2}"); }
	@Test public void test_4658() { checkNotSubtype("{null f1,void f2}","{int f2,null f3}"); }
	@Test public void test_4659() { checkNotSubtype("{null f1,void f2}","{int f1,int f2}"); }
	@Test public void test_4660() { checkNotSubtype("{null f1,void f2}","{int f2,int f3}"); }
	@Test public void test_4661() { checkNotSubtype("{null f1,void f2}","{[void] f1}"); }
	@Test public void test_4662() { checkNotSubtype("{null f1,void f2}","{[void] f2}"); }
	@Test public void test_4663() { checkIsSubtype("{null f1,void f2}","{[void] f1,void f2}"); }
	@Test public void test_4664() { checkIsSubtype("{null f1,void f2}","{[void] f2,void f3}"); }
	@Test public void test_4665() { checkNotSubtype("{null f1,void f2}","{[any] f1}"); }
	@Test public void test_4666() { checkNotSubtype("{null f1,void f2}","{[any] f2}"); }
	@Test public void test_4667() { checkNotSubtype("{null f1,void f2}","{[any] f1,any f2}"); }
	@Test public void test_4668() { checkNotSubtype("{null f1,void f2}","{[any] f2,any f3}"); }
	@Test public void test_4669() { checkNotSubtype("{null f1,void f2}","{[null] f1}"); }
	@Test public void test_4670() { checkNotSubtype("{null f1,void f2}","{[null] f2}"); }
	@Test public void test_4671() { checkNotSubtype("{null f1,void f2}","{[null] f1,null f2}"); }
	@Test public void test_4672() { checkNotSubtype("{null f1,void f2}","{[null] f2,null f3}"); }
	@Test public void test_4673() { checkNotSubtype("{null f1,void f2}","{[int] f1}"); }
	@Test public void test_4674() { checkNotSubtype("{null f1,void f2}","{[int] f2}"); }
	@Test public void test_4675() { checkNotSubtype("{null f1,void f2}","{[int] f1,int f2}"); }
	@Test public void test_4676() { checkNotSubtype("{null f1,void f2}","{[int] f2,int f3}"); }
	@Test public void test_4677() { checkIsSubtype("{null f1,void f2}","{{void f1} f1}"); }
	@Test public void test_4678() { checkIsSubtype("{null f1,void f2}","{{void f2} f1}"); }
	@Test public void test_4679() { checkIsSubtype("{null f1,void f2}","{{void f1} f2}"); }
	@Test public void test_4680() { checkIsSubtype("{null f1,void f2}","{{void f2} f2}"); }
	@Test public void test_4681() { checkIsSubtype("{null f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4682() { checkIsSubtype("{null f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4683() { checkIsSubtype("{null f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4684() { checkIsSubtype("{null f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4685() { checkNotSubtype("{null f1,void f2}","{{any f1} f1}"); }
	@Test public void test_4686() { checkNotSubtype("{null f1,void f2}","{{any f2} f1}"); }
	@Test public void test_4687() { checkNotSubtype("{null f1,void f2}","{{any f1} f2}"); }
	@Test public void test_4688() { checkNotSubtype("{null f1,void f2}","{{any f2} f2}"); }
	@Test public void test_4689() { checkNotSubtype("{null f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4690() { checkNotSubtype("{null f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4691() { checkNotSubtype("{null f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4692() { checkNotSubtype("{null f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4693() { checkNotSubtype("{null f1,void f2}","{{null f1} f1}"); }
	@Test public void test_4694() { checkNotSubtype("{null f1,void f2}","{{null f2} f1}"); }
	@Test public void test_4695() { checkNotSubtype("{null f1,void f2}","{{null f1} f2}"); }
	@Test public void test_4696() { checkNotSubtype("{null f1,void f2}","{{null f2} f2}"); }
	@Test public void test_4697() { checkNotSubtype("{null f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4698() { checkNotSubtype("{null f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4699() { checkNotSubtype("{null f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4700() { checkNotSubtype("{null f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4701() { checkNotSubtype("{null f1,void f2}","{{int f1} f1}"); }
	@Test public void test_4702() { checkNotSubtype("{null f1,void f2}","{{int f2} f1}"); }
	@Test public void test_4703() { checkNotSubtype("{null f1,void f2}","{{int f1} f2}"); }
	@Test public void test_4704() { checkNotSubtype("{null f1,void f2}","{{int f2} f2}"); }
	@Test public void test_4705() { checkNotSubtype("{null f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4706() { checkNotSubtype("{null f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4707() { checkNotSubtype("{null f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4708() { checkNotSubtype("{null f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4709() { checkNotSubtype("{null f2,void f3}","any"); }
	@Test public void test_4710() { checkNotSubtype("{null f2,void f3}","null"); }
	@Test public void test_4711() { checkNotSubtype("{null f2,void f3}","int"); }
	@Test public void test_4712() { checkNotSubtype("{null f2,void f3}","[void]"); }
	@Test public void test_4713() { checkNotSubtype("{null f2,void f3}","[any]"); }
	@Test public void test_4714() { checkNotSubtype("{null f2,void f3}","[null]"); }
	@Test public void test_4715() { checkNotSubtype("{null f2,void f3}","[int]"); }
	@Test public void test_4716() { checkIsSubtype("{null f2,void f3}","{void f1}"); }
	@Test public void test_4717() { checkIsSubtype("{null f2,void f3}","{void f2}"); }
	@Test public void test_4718() { checkNotSubtype("{null f2,void f3}","{any f1}"); }
	@Test public void test_4719() { checkNotSubtype("{null f2,void f3}","{any f2}"); }
	@Test public void test_4720() { checkNotSubtype("{null f2,void f3}","{null f1}"); }
	@Test public void test_4721() { checkNotSubtype("{null f2,void f3}","{null f2}"); }
	@Test public void test_4722() { checkNotSubtype("{null f2,void f3}","{int f1}"); }
	@Test public void test_4723() { checkNotSubtype("{null f2,void f3}","{int f2}"); }
	@Test public void test_4724() { checkNotSubtype("{null f2,void f3}","[[void]]"); }
	@Test public void test_4725() { checkNotSubtype("{null f2,void f3}","[[any]]"); }
	@Test public void test_4726() { checkNotSubtype("{null f2,void f3}","[[null]]"); }
	@Test public void test_4727() { checkNotSubtype("{null f2,void f3}","[[int]]"); }
	@Test public void test_4728() { checkNotSubtype("{null f2,void f3}","[{void f1}]"); }
	@Test public void test_4729() { checkNotSubtype("{null f2,void f3}","[{void f2}]"); }
	@Test public void test_4730() { checkNotSubtype("{null f2,void f3}","[{any f1}]"); }
	@Test public void test_4731() { checkNotSubtype("{null f2,void f3}","[{any f2}]"); }
	@Test public void test_4732() { checkNotSubtype("{null f2,void f3}","[{null f1}]"); }
	@Test public void test_4733() { checkNotSubtype("{null f2,void f3}","[{null f2}]"); }
	@Test public void test_4734() { checkNotSubtype("{null f2,void f3}","[{int f1}]"); }
	@Test public void test_4735() { checkNotSubtype("{null f2,void f3}","[{int f2}]"); }
	@Test public void test_4736() { checkIsSubtype("{null f2,void f3}","{void f1,void f2}"); }
	@Test public void test_4737() { checkIsSubtype("{null f2,void f3}","{void f2,void f3}"); }
	@Test public void test_4738() { checkIsSubtype("{null f2,void f3}","{void f1,any f2}"); }
	@Test public void test_4739() { checkIsSubtype("{null f2,void f3}","{void f2,any f3}"); }
	@Test public void test_4740() { checkIsSubtype("{null f2,void f3}","{void f1,null f2}"); }
	@Test public void test_4741() { checkIsSubtype("{null f2,void f3}","{void f2,null f3}"); }
	@Test public void test_4742() { checkIsSubtype("{null f2,void f3}","{void f1,int f2}"); }
	@Test public void test_4743() { checkIsSubtype("{null f2,void f3}","{void f2,int f3}"); }
	@Test public void test_4744() { checkIsSubtype("{null f2,void f3}","{any f1,void f2}"); }
	@Test public void test_4745() { checkIsSubtype("{null f2,void f3}","{any f2,void f3}"); }
	@Test public void test_4746() { checkNotSubtype("{null f2,void f3}","{any f1,any f2}"); }
	@Test public void test_4747() { checkNotSubtype("{null f2,void f3}","{any f2,any f3}"); }
	@Test public void test_4748() { checkNotSubtype("{null f2,void f3}","{any f1,null f2}"); }
	@Test public void test_4749() { checkNotSubtype("{null f2,void f3}","{any f2,null f3}"); }
	@Test public void test_4750() { checkNotSubtype("{null f2,void f3}","{any f1,int f2}"); }
	@Test public void test_4751() { checkNotSubtype("{null f2,void f3}","{any f2,int f3}"); }
	@Test public void test_4752() { checkIsSubtype("{null f2,void f3}","{null f1,void f2}"); }
	@Test public void test_4753() { checkIsSubtype("{null f2,void f3}","{null f2,void f3}"); }
	@Test public void test_4754() { checkNotSubtype("{null f2,void f3}","{null f1,any f2}"); }
	@Test public void test_4755() { checkNotSubtype("{null f2,void f3}","{null f2,any f3}"); }
	@Test public void test_4756() { checkNotSubtype("{null f2,void f3}","{null f1,null f2}"); }
	@Test public void test_4757() { checkNotSubtype("{null f2,void f3}","{null f2,null f3}"); }
	@Test public void test_4758() { checkNotSubtype("{null f2,void f3}","{null f1,int f2}"); }
	@Test public void test_4759() { checkNotSubtype("{null f2,void f3}","{null f2,int f3}"); }
	@Test public void test_4760() { checkIsSubtype("{null f2,void f3}","{int f1,void f2}"); }
	@Test public void test_4761() { checkIsSubtype("{null f2,void f3}","{int f2,void f3}"); }
	@Test public void test_4762() { checkNotSubtype("{null f2,void f3}","{int f1,any f2}"); }
	@Test public void test_4763() { checkNotSubtype("{null f2,void f3}","{int f2,any f3}"); }
	@Test public void test_4764() { checkNotSubtype("{null f2,void f3}","{int f1,null f2}"); }
	@Test public void test_4765() { checkNotSubtype("{null f2,void f3}","{int f2,null f3}"); }
	@Test public void test_4766() { checkNotSubtype("{null f2,void f3}","{int f1,int f2}"); }
	@Test public void test_4767() { checkNotSubtype("{null f2,void f3}","{int f2,int f3}"); }
	@Test public void test_4768() { checkNotSubtype("{null f2,void f3}","{[void] f1}"); }
	@Test public void test_4769() { checkNotSubtype("{null f2,void f3}","{[void] f2}"); }
	@Test public void test_4770() { checkIsSubtype("{null f2,void f3}","{[void] f1,void f2}"); }
	@Test public void test_4771() { checkIsSubtype("{null f2,void f3}","{[void] f2,void f3}"); }
	@Test public void test_4772() { checkNotSubtype("{null f2,void f3}","{[any] f1}"); }
	@Test public void test_4773() { checkNotSubtype("{null f2,void f3}","{[any] f2}"); }
	@Test public void test_4774() { checkNotSubtype("{null f2,void f3}","{[any] f1,any f2}"); }
	@Test public void test_4775() { checkNotSubtype("{null f2,void f3}","{[any] f2,any f3}"); }
	@Test public void test_4776() { checkNotSubtype("{null f2,void f3}","{[null] f1}"); }
	@Test public void test_4777() { checkNotSubtype("{null f2,void f3}","{[null] f2}"); }
	@Test public void test_4778() { checkNotSubtype("{null f2,void f3}","{[null] f1,null f2}"); }
	@Test public void test_4779() { checkNotSubtype("{null f2,void f3}","{[null] f2,null f3}"); }
	@Test public void test_4780() { checkNotSubtype("{null f2,void f3}","{[int] f1}"); }
	@Test public void test_4781() { checkNotSubtype("{null f2,void f3}","{[int] f2}"); }
	@Test public void test_4782() { checkNotSubtype("{null f2,void f3}","{[int] f1,int f2}"); }
	@Test public void test_4783() { checkNotSubtype("{null f2,void f3}","{[int] f2,int f3}"); }
	@Test public void test_4784() { checkIsSubtype("{null f2,void f3}","{{void f1} f1}"); }
	@Test public void test_4785() { checkIsSubtype("{null f2,void f3}","{{void f2} f1}"); }
	@Test public void test_4786() { checkIsSubtype("{null f2,void f3}","{{void f1} f2}"); }
	@Test public void test_4787() { checkIsSubtype("{null f2,void f3}","{{void f2} f2}"); }
	@Test public void test_4788() { checkIsSubtype("{null f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_4789() { checkIsSubtype("{null f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_4790() { checkIsSubtype("{null f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_4791() { checkIsSubtype("{null f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_4792() { checkNotSubtype("{null f2,void f3}","{{any f1} f1}"); }
	@Test public void test_4793() { checkNotSubtype("{null f2,void f3}","{{any f2} f1}"); }
	@Test public void test_4794() { checkNotSubtype("{null f2,void f3}","{{any f1} f2}"); }
	@Test public void test_4795() { checkNotSubtype("{null f2,void f3}","{{any f2} f2}"); }
	@Test public void test_4796() { checkNotSubtype("{null f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_4797() { checkNotSubtype("{null f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_4798() { checkNotSubtype("{null f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_4799() { checkNotSubtype("{null f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_4800() { checkNotSubtype("{null f2,void f3}","{{null f1} f1}"); }
	@Test public void test_4801() { checkNotSubtype("{null f2,void f3}","{{null f2} f1}"); }
	@Test public void test_4802() { checkNotSubtype("{null f2,void f3}","{{null f1} f2}"); }
	@Test public void test_4803() { checkNotSubtype("{null f2,void f3}","{{null f2} f2}"); }
	@Test public void test_4804() { checkNotSubtype("{null f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_4805() { checkNotSubtype("{null f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_4806() { checkNotSubtype("{null f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_4807() { checkNotSubtype("{null f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_4808() { checkNotSubtype("{null f2,void f3}","{{int f1} f1}"); }
	@Test public void test_4809() { checkNotSubtype("{null f2,void f3}","{{int f2} f1}"); }
	@Test public void test_4810() { checkNotSubtype("{null f2,void f3}","{{int f1} f2}"); }
	@Test public void test_4811() { checkNotSubtype("{null f2,void f3}","{{int f2} f2}"); }
	@Test public void test_4812() { checkNotSubtype("{null f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_4813() { checkNotSubtype("{null f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_4814() { checkNotSubtype("{null f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_4815() { checkNotSubtype("{null f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_4816() { checkNotSubtype("{null f1,any f2}","any"); }
	@Test public void test_4817() { checkNotSubtype("{null f1,any f2}","null"); }
	@Test public void test_4818() { checkNotSubtype("{null f1,any f2}","int"); }
	@Test public void test_4819() { checkNotSubtype("{null f1,any f2}","[void]"); }
	@Test public void test_4820() { checkNotSubtype("{null f1,any f2}","[any]"); }
	@Test public void test_4821() { checkNotSubtype("{null f1,any f2}","[null]"); }
	@Test public void test_4822() { checkNotSubtype("{null f1,any f2}","[int]"); }
	@Test public void test_4823() { checkIsSubtype("{null f1,any f2}","{void f1}"); }
	@Test public void test_4824() { checkIsSubtype("{null f1,any f2}","{void f2}"); }
	@Test public void test_4825() { checkNotSubtype("{null f1,any f2}","{any f1}"); }
	@Test public void test_4826() { checkNotSubtype("{null f1,any f2}","{any f2}"); }
	@Test public void test_4827() { checkNotSubtype("{null f1,any f2}","{null f1}"); }
	@Test public void test_4828() { checkNotSubtype("{null f1,any f2}","{null f2}"); }
	@Test public void test_4829() { checkNotSubtype("{null f1,any f2}","{int f1}"); }
	@Test public void test_4830() { checkNotSubtype("{null f1,any f2}","{int f2}"); }
	@Test public void test_4831() { checkNotSubtype("{null f1,any f2}","[[void]]"); }
	@Test public void test_4832() { checkNotSubtype("{null f1,any f2}","[[any]]"); }
	@Test public void test_4833() { checkNotSubtype("{null f1,any f2}","[[null]]"); }
	@Test public void test_4834() { checkNotSubtype("{null f1,any f2}","[[int]]"); }
	@Test public void test_4835() { checkNotSubtype("{null f1,any f2}","[{void f1}]"); }
	@Test public void test_4836() { checkNotSubtype("{null f1,any f2}","[{void f2}]"); }
	@Test public void test_4837() { checkNotSubtype("{null f1,any f2}","[{any f1}]"); }
	@Test public void test_4838() { checkNotSubtype("{null f1,any f2}","[{any f2}]"); }
	@Test public void test_4839() { checkNotSubtype("{null f1,any f2}","[{null f1}]"); }
	@Test public void test_4840() { checkNotSubtype("{null f1,any f2}","[{null f2}]"); }
	@Test public void test_4841() { checkNotSubtype("{null f1,any f2}","[{int f1}]"); }
	@Test public void test_4842() { checkNotSubtype("{null f1,any f2}","[{int f2}]"); }
	@Test public void test_4843() { checkIsSubtype("{null f1,any f2}","{void f1,void f2}"); }
	@Test public void test_4844() { checkIsSubtype("{null f1,any f2}","{void f2,void f3}"); }
	@Test public void test_4845() { checkIsSubtype("{null f1,any f2}","{void f1,any f2}"); }
	@Test public void test_4846() { checkIsSubtype("{null f1,any f2}","{void f2,any f3}"); }
	@Test public void test_4847() { checkIsSubtype("{null f1,any f2}","{void f1,null f2}"); }
	@Test public void test_4848() { checkIsSubtype("{null f1,any f2}","{void f2,null f3}"); }
	@Test public void test_4849() { checkIsSubtype("{null f1,any f2}","{void f1,int f2}"); }
	@Test public void test_4850() { checkIsSubtype("{null f1,any f2}","{void f2,int f3}"); }
	@Test public void test_4851() { checkIsSubtype("{null f1,any f2}","{any f1,void f2}"); }
	@Test public void test_4852() { checkIsSubtype("{null f1,any f2}","{any f2,void f3}"); }
	@Test public void test_4853() { checkNotSubtype("{null f1,any f2}","{any f1,any f2}"); }
	@Test public void test_4854() { checkNotSubtype("{null f1,any f2}","{any f2,any f3}"); }
	@Test public void test_4855() { checkNotSubtype("{null f1,any f2}","{any f1,null f2}"); }
	@Test public void test_4856() { checkNotSubtype("{null f1,any f2}","{any f2,null f3}"); }
	@Test public void test_4857() { checkNotSubtype("{null f1,any f2}","{any f1,int f2}"); }
	@Test public void test_4858() { checkNotSubtype("{null f1,any f2}","{any f2,int f3}"); }
	@Test public void test_4859() { checkIsSubtype("{null f1,any f2}","{null f1,void f2}"); }
	@Test public void test_4860() { checkIsSubtype("{null f1,any f2}","{null f2,void f3}"); }
	@Test public void test_4861() { checkIsSubtype("{null f1,any f2}","{null f1,any f2}"); }
	@Test public void test_4862() { checkNotSubtype("{null f1,any f2}","{null f2,any f3}"); }
	@Test public void test_4863() { checkIsSubtype("{null f1,any f2}","{null f1,null f2}"); }
	@Test public void test_4864() { checkNotSubtype("{null f1,any f2}","{null f2,null f3}"); }
	@Test public void test_4865() { checkIsSubtype("{null f1,any f2}","{null f1,int f2}"); }
	@Test public void test_4866() { checkNotSubtype("{null f1,any f2}","{null f2,int f3}"); }
	@Test public void test_4867() { checkIsSubtype("{null f1,any f2}","{int f1,void f2}"); }
	@Test public void test_4868() { checkIsSubtype("{null f1,any f2}","{int f2,void f3}"); }
	@Test public void test_4869() { checkNotSubtype("{null f1,any f2}","{int f1,any f2}"); }
	@Test public void test_4870() { checkNotSubtype("{null f1,any f2}","{int f2,any f3}"); }
	@Test public void test_4871() { checkNotSubtype("{null f1,any f2}","{int f1,null f2}"); }
	@Test public void test_4872() { checkNotSubtype("{null f1,any f2}","{int f2,null f3}"); }
	@Test public void test_4873() { checkNotSubtype("{null f1,any f2}","{int f1,int f2}"); }
	@Test public void test_4874() { checkNotSubtype("{null f1,any f2}","{int f2,int f3}"); }
	@Test public void test_4875() { checkNotSubtype("{null f1,any f2}","{[void] f1}"); }
	@Test public void test_4876() { checkNotSubtype("{null f1,any f2}","{[void] f2}"); }
	@Test public void test_4877() { checkIsSubtype("{null f1,any f2}","{[void] f1,void f2}"); }
	@Test public void test_4878() { checkIsSubtype("{null f1,any f2}","{[void] f2,void f3}"); }
	@Test public void test_4879() { checkNotSubtype("{null f1,any f2}","{[any] f1}"); }
	@Test public void test_4880() { checkNotSubtype("{null f1,any f2}","{[any] f2}"); }
	@Test public void test_4881() { checkNotSubtype("{null f1,any f2}","{[any] f1,any f2}"); }
	@Test public void test_4882() { checkNotSubtype("{null f1,any f2}","{[any] f2,any f3}"); }
	@Test public void test_4883() { checkNotSubtype("{null f1,any f2}","{[null] f1}"); }
	@Test public void test_4884() { checkNotSubtype("{null f1,any f2}","{[null] f2}"); }
	@Test public void test_4885() { checkNotSubtype("{null f1,any f2}","{[null] f1,null f2}"); }
	@Test public void test_4886() { checkNotSubtype("{null f1,any f2}","{[null] f2,null f3}"); }
	@Test public void test_4887() { checkNotSubtype("{null f1,any f2}","{[int] f1}"); }
	@Test public void test_4888() { checkNotSubtype("{null f1,any f2}","{[int] f2}"); }
	@Test public void test_4889() { checkNotSubtype("{null f1,any f2}","{[int] f1,int f2}"); }
	@Test public void test_4890() { checkNotSubtype("{null f1,any f2}","{[int] f2,int f3}"); }
	@Test public void test_4891() { checkIsSubtype("{null f1,any f2}","{{void f1} f1}"); }
	@Test public void test_4892() { checkIsSubtype("{null f1,any f2}","{{void f2} f1}"); }
	@Test public void test_4893() { checkIsSubtype("{null f1,any f2}","{{void f1} f2}"); }
	@Test public void test_4894() { checkIsSubtype("{null f1,any f2}","{{void f2} f2}"); }
	@Test public void test_4895() { checkIsSubtype("{null f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4896() { checkIsSubtype("{null f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4897() { checkIsSubtype("{null f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4898() { checkIsSubtype("{null f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4899() { checkNotSubtype("{null f1,any f2}","{{any f1} f1}"); }
	@Test public void test_4900() { checkNotSubtype("{null f1,any f2}","{{any f2} f1}"); }
	@Test public void test_4901() { checkNotSubtype("{null f1,any f2}","{{any f1} f2}"); }
	@Test public void test_4902() { checkNotSubtype("{null f1,any f2}","{{any f2} f2}"); }
	@Test public void test_4903() { checkNotSubtype("{null f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4904() { checkNotSubtype("{null f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4905() { checkNotSubtype("{null f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4906() { checkNotSubtype("{null f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4907() { checkNotSubtype("{null f1,any f2}","{{null f1} f1}"); }
	@Test public void test_4908() { checkNotSubtype("{null f1,any f2}","{{null f2} f1}"); }
	@Test public void test_4909() { checkNotSubtype("{null f1,any f2}","{{null f1} f2}"); }
	@Test public void test_4910() { checkNotSubtype("{null f1,any f2}","{{null f2} f2}"); }
	@Test public void test_4911() { checkNotSubtype("{null f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4912() { checkNotSubtype("{null f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4913() { checkNotSubtype("{null f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4914() { checkNotSubtype("{null f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4915() { checkNotSubtype("{null f1,any f2}","{{int f1} f1}"); }
	@Test public void test_4916() { checkNotSubtype("{null f1,any f2}","{{int f2} f1}"); }
	@Test public void test_4917() { checkNotSubtype("{null f1,any f2}","{{int f1} f2}"); }
	@Test public void test_4918() { checkNotSubtype("{null f1,any f2}","{{int f2} f2}"); }
	@Test public void test_4919() { checkNotSubtype("{null f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4920() { checkNotSubtype("{null f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4921() { checkNotSubtype("{null f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4922() { checkNotSubtype("{null f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4923() { checkNotSubtype("{null f2,any f3}","any"); }
	@Test public void test_4924() { checkNotSubtype("{null f2,any f3}","null"); }
	@Test public void test_4925() { checkNotSubtype("{null f2,any f3}","int"); }
	@Test public void test_4926() { checkNotSubtype("{null f2,any f3}","[void]"); }
	@Test public void test_4927() { checkNotSubtype("{null f2,any f3}","[any]"); }
	@Test public void test_4928() { checkNotSubtype("{null f2,any f3}","[null]"); }
	@Test public void test_4929() { checkNotSubtype("{null f2,any f3}","[int]"); }
	@Test public void test_4930() { checkIsSubtype("{null f2,any f3}","{void f1}"); }
	@Test public void test_4931() { checkIsSubtype("{null f2,any f3}","{void f2}"); }
	@Test public void test_4932() { checkNotSubtype("{null f2,any f3}","{any f1}"); }
	@Test public void test_4933() { checkNotSubtype("{null f2,any f3}","{any f2}"); }
	@Test public void test_4934() { checkNotSubtype("{null f2,any f3}","{null f1}"); }
	@Test public void test_4935() { checkNotSubtype("{null f2,any f3}","{null f2}"); }
	@Test public void test_4936() { checkNotSubtype("{null f2,any f3}","{int f1}"); }
	@Test public void test_4937() { checkNotSubtype("{null f2,any f3}","{int f2}"); }
	@Test public void test_4938() { checkNotSubtype("{null f2,any f3}","[[void]]"); }
	@Test public void test_4939() { checkNotSubtype("{null f2,any f3}","[[any]]"); }
	@Test public void test_4940() { checkNotSubtype("{null f2,any f3}","[[null]]"); }
	@Test public void test_4941() { checkNotSubtype("{null f2,any f3}","[[int]]"); }
	@Test public void test_4942() { checkNotSubtype("{null f2,any f3}","[{void f1}]"); }
	@Test public void test_4943() { checkNotSubtype("{null f2,any f3}","[{void f2}]"); }
	@Test public void test_4944() { checkNotSubtype("{null f2,any f3}","[{any f1}]"); }
	@Test public void test_4945() { checkNotSubtype("{null f2,any f3}","[{any f2}]"); }
	@Test public void test_4946() { checkNotSubtype("{null f2,any f3}","[{null f1}]"); }
	@Test public void test_4947() { checkNotSubtype("{null f2,any f3}","[{null f2}]"); }
	@Test public void test_4948() { checkNotSubtype("{null f2,any f3}","[{int f1}]"); }
	@Test public void test_4949() { checkNotSubtype("{null f2,any f3}","[{int f2}]"); }
	@Test public void test_4950() { checkIsSubtype("{null f2,any f3}","{void f1,void f2}"); }
	@Test public void test_4951() { checkIsSubtype("{null f2,any f3}","{void f2,void f3}"); }
	@Test public void test_4952() { checkIsSubtype("{null f2,any f3}","{void f1,any f2}"); }
	@Test public void test_4953() { checkIsSubtype("{null f2,any f3}","{void f2,any f3}"); }
	@Test public void test_4954() { checkIsSubtype("{null f2,any f3}","{void f1,null f2}"); }
	@Test public void test_4955() { checkIsSubtype("{null f2,any f3}","{void f2,null f3}"); }
	@Test public void test_4956() { checkIsSubtype("{null f2,any f3}","{void f1,int f2}"); }
	@Test public void test_4957() { checkIsSubtype("{null f2,any f3}","{void f2,int f3}"); }
	@Test public void test_4958() { checkIsSubtype("{null f2,any f3}","{any f1,void f2}"); }
	@Test public void test_4959() { checkIsSubtype("{null f2,any f3}","{any f2,void f3}"); }
	@Test public void test_4960() { checkNotSubtype("{null f2,any f3}","{any f1,any f2}"); }
	@Test public void test_4961() { checkNotSubtype("{null f2,any f3}","{any f2,any f3}"); }
	@Test public void test_4962() { checkNotSubtype("{null f2,any f3}","{any f1,null f2}"); }
	@Test public void test_4963() { checkNotSubtype("{null f2,any f3}","{any f2,null f3}"); }
	@Test public void test_4964() { checkNotSubtype("{null f2,any f3}","{any f1,int f2}"); }
	@Test public void test_4965() { checkNotSubtype("{null f2,any f3}","{any f2,int f3}"); }
	@Test public void test_4966() { checkIsSubtype("{null f2,any f3}","{null f1,void f2}"); }
	@Test public void test_4967() { checkIsSubtype("{null f2,any f3}","{null f2,void f3}"); }
	@Test public void test_4968() { checkNotSubtype("{null f2,any f3}","{null f1,any f2}"); }
	@Test public void test_4969() { checkIsSubtype("{null f2,any f3}","{null f2,any f3}"); }
	@Test public void test_4970() { checkNotSubtype("{null f2,any f3}","{null f1,null f2}"); }
	@Test public void test_4971() { checkIsSubtype("{null f2,any f3}","{null f2,null f3}"); }
	@Test public void test_4972() { checkNotSubtype("{null f2,any f3}","{null f1,int f2}"); }
	@Test public void test_4973() { checkIsSubtype("{null f2,any f3}","{null f2,int f3}"); }
	@Test public void test_4974() { checkIsSubtype("{null f2,any f3}","{int f1,void f2}"); }
	@Test public void test_4975() { checkIsSubtype("{null f2,any f3}","{int f2,void f3}"); }
	@Test public void test_4976() { checkNotSubtype("{null f2,any f3}","{int f1,any f2}"); }
	@Test public void test_4977() { checkNotSubtype("{null f2,any f3}","{int f2,any f3}"); }
	@Test public void test_4978() { checkNotSubtype("{null f2,any f3}","{int f1,null f2}"); }
	@Test public void test_4979() { checkNotSubtype("{null f2,any f3}","{int f2,null f3}"); }
	@Test public void test_4980() { checkNotSubtype("{null f2,any f3}","{int f1,int f2}"); }
	@Test public void test_4981() { checkNotSubtype("{null f2,any f3}","{int f2,int f3}"); }
	@Test public void test_4982() { checkNotSubtype("{null f2,any f3}","{[void] f1}"); }
	@Test public void test_4983() { checkNotSubtype("{null f2,any f3}","{[void] f2}"); }
	@Test public void test_4984() { checkIsSubtype("{null f2,any f3}","{[void] f1,void f2}"); }
	@Test public void test_4985() { checkIsSubtype("{null f2,any f3}","{[void] f2,void f3}"); }
	@Test public void test_4986() { checkNotSubtype("{null f2,any f3}","{[any] f1}"); }
	@Test public void test_4987() { checkNotSubtype("{null f2,any f3}","{[any] f2}"); }
	@Test public void test_4988() { checkNotSubtype("{null f2,any f3}","{[any] f1,any f2}"); }
	@Test public void test_4989() { checkNotSubtype("{null f2,any f3}","{[any] f2,any f3}"); }
	@Test public void test_4990() { checkNotSubtype("{null f2,any f3}","{[null] f1}"); }
	@Test public void test_4991() { checkNotSubtype("{null f2,any f3}","{[null] f2}"); }
	@Test public void test_4992() { checkNotSubtype("{null f2,any f3}","{[null] f1,null f2}"); }
	@Test public void test_4993() { checkNotSubtype("{null f2,any f3}","{[null] f2,null f3}"); }
	@Test public void test_4994() { checkNotSubtype("{null f2,any f3}","{[int] f1}"); }
	@Test public void test_4995() { checkNotSubtype("{null f2,any f3}","{[int] f2}"); }
	@Test public void test_4996() { checkNotSubtype("{null f2,any f3}","{[int] f1,int f2}"); }
	@Test public void test_4997() { checkNotSubtype("{null f2,any f3}","{[int] f2,int f3}"); }
	@Test public void test_4998() { checkIsSubtype("{null f2,any f3}","{{void f1} f1}"); }
	@Test public void test_4999() { checkIsSubtype("{null f2,any f3}","{{void f2} f1}"); }
	@Test public void test_5000() { checkIsSubtype("{null f2,any f3}","{{void f1} f2}"); }
	@Test public void test_5001() { checkIsSubtype("{null f2,any f3}","{{void f2} f2}"); }
	@Test public void test_5002() { checkIsSubtype("{null f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_5003() { checkIsSubtype("{null f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_5004() { checkIsSubtype("{null f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_5005() { checkIsSubtype("{null f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_5006() { checkNotSubtype("{null f2,any f3}","{{any f1} f1}"); }
	@Test public void test_5007() { checkNotSubtype("{null f2,any f3}","{{any f2} f1}"); }
	@Test public void test_5008() { checkNotSubtype("{null f2,any f3}","{{any f1} f2}"); }
	@Test public void test_5009() { checkNotSubtype("{null f2,any f3}","{{any f2} f2}"); }
	@Test public void test_5010() { checkNotSubtype("{null f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_5011() { checkNotSubtype("{null f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_5012() { checkNotSubtype("{null f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_5013() { checkNotSubtype("{null f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_5014() { checkNotSubtype("{null f2,any f3}","{{null f1} f1}"); }
	@Test public void test_5015() { checkNotSubtype("{null f2,any f3}","{{null f2} f1}"); }
	@Test public void test_5016() { checkNotSubtype("{null f2,any f3}","{{null f1} f2}"); }
	@Test public void test_5017() { checkNotSubtype("{null f2,any f3}","{{null f2} f2}"); }
	@Test public void test_5018() { checkNotSubtype("{null f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_5019() { checkNotSubtype("{null f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_5020() { checkNotSubtype("{null f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_5021() { checkNotSubtype("{null f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_5022() { checkNotSubtype("{null f2,any f3}","{{int f1} f1}"); }
	@Test public void test_5023() { checkNotSubtype("{null f2,any f3}","{{int f2} f1}"); }
	@Test public void test_5024() { checkNotSubtype("{null f2,any f3}","{{int f1} f2}"); }
	@Test public void test_5025() { checkNotSubtype("{null f2,any f3}","{{int f2} f2}"); }
	@Test public void test_5026() { checkNotSubtype("{null f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_5027() { checkNotSubtype("{null f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_5028() { checkNotSubtype("{null f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_5029() { checkNotSubtype("{null f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_5030() { checkNotSubtype("{null f1,null f2}","any"); }
	@Test public void test_5031() { checkNotSubtype("{null f1,null f2}","null"); }
	@Test public void test_5032() { checkNotSubtype("{null f1,null f2}","int"); }
	@Test public void test_5033() { checkNotSubtype("{null f1,null f2}","[void]"); }
	@Test public void test_5034() { checkNotSubtype("{null f1,null f2}","[any]"); }
	@Test public void test_5035() { checkNotSubtype("{null f1,null f2}","[null]"); }
	@Test public void test_5036() { checkNotSubtype("{null f1,null f2}","[int]"); }
	@Test public void test_5037() { checkIsSubtype("{null f1,null f2}","{void f1}"); }
	@Test public void test_5038() { checkIsSubtype("{null f1,null f2}","{void f2}"); }
	@Test public void test_5039() { checkNotSubtype("{null f1,null f2}","{any f1}"); }
	@Test public void test_5040() { checkNotSubtype("{null f1,null f2}","{any f2}"); }
	@Test public void test_5041() { checkNotSubtype("{null f1,null f2}","{null f1}"); }
	@Test public void test_5042() { checkNotSubtype("{null f1,null f2}","{null f2}"); }
	@Test public void test_5043() { checkNotSubtype("{null f1,null f2}","{int f1}"); }
	@Test public void test_5044() { checkNotSubtype("{null f1,null f2}","{int f2}"); }
	@Test public void test_5045() { checkNotSubtype("{null f1,null f2}","[[void]]"); }
	@Test public void test_5046() { checkNotSubtype("{null f1,null f2}","[[any]]"); }
	@Test public void test_5047() { checkNotSubtype("{null f1,null f2}","[[null]]"); }
	@Test public void test_5048() { checkNotSubtype("{null f1,null f2}","[[int]]"); }
	@Test public void test_5049() { checkNotSubtype("{null f1,null f2}","[{void f1}]"); }
	@Test public void test_5050() { checkNotSubtype("{null f1,null f2}","[{void f2}]"); }
	@Test public void test_5051() { checkNotSubtype("{null f1,null f2}","[{any f1}]"); }
	@Test public void test_5052() { checkNotSubtype("{null f1,null f2}","[{any f2}]"); }
	@Test public void test_5053() { checkNotSubtype("{null f1,null f2}","[{null f1}]"); }
	@Test public void test_5054() { checkNotSubtype("{null f1,null f2}","[{null f2}]"); }
	@Test public void test_5055() { checkNotSubtype("{null f1,null f2}","[{int f1}]"); }
	@Test public void test_5056() { checkNotSubtype("{null f1,null f2}","[{int f2}]"); }
	@Test public void test_5057() { checkIsSubtype("{null f1,null f2}","{void f1,void f2}"); }
	@Test public void test_5058() { checkIsSubtype("{null f1,null f2}","{void f2,void f3}"); }
	@Test public void test_5059() { checkIsSubtype("{null f1,null f2}","{void f1,any f2}"); }
	@Test public void test_5060() { checkIsSubtype("{null f1,null f2}","{void f2,any f3}"); }
	@Test public void test_5061() { checkIsSubtype("{null f1,null f2}","{void f1,null f2}"); }
	@Test public void test_5062() { checkIsSubtype("{null f1,null f2}","{void f2,null f3}"); }
	@Test public void test_5063() { checkIsSubtype("{null f1,null f2}","{void f1,int f2}"); }
	@Test public void test_5064() { checkIsSubtype("{null f1,null f2}","{void f2,int f3}"); }
	@Test public void test_5065() { checkIsSubtype("{null f1,null f2}","{any f1,void f2}"); }
	@Test public void test_5066() { checkIsSubtype("{null f1,null f2}","{any f2,void f3}"); }
	@Test public void test_5067() { checkNotSubtype("{null f1,null f2}","{any f1,any f2}"); }
	@Test public void test_5068() { checkNotSubtype("{null f1,null f2}","{any f2,any f3}"); }
	@Test public void test_5069() { checkNotSubtype("{null f1,null f2}","{any f1,null f2}"); }
	@Test public void test_5070() { checkNotSubtype("{null f1,null f2}","{any f2,null f3}"); }
	@Test public void test_5071() { checkNotSubtype("{null f1,null f2}","{any f1,int f2}"); }
	@Test public void test_5072() { checkNotSubtype("{null f1,null f2}","{any f2,int f3}"); }
	@Test public void test_5073() { checkIsSubtype("{null f1,null f2}","{null f1,void f2}"); }
	@Test public void test_5074() { checkIsSubtype("{null f1,null f2}","{null f2,void f3}"); }
	@Test public void test_5075() { checkNotSubtype("{null f1,null f2}","{null f1,any f2}"); }
	@Test public void test_5076() { checkNotSubtype("{null f1,null f2}","{null f2,any f3}"); }
	@Test public void test_5077() { checkIsSubtype("{null f1,null f2}","{null f1,null f2}"); }
	@Test public void test_5078() { checkNotSubtype("{null f1,null f2}","{null f2,null f3}"); }
	@Test public void test_5079() { checkNotSubtype("{null f1,null f2}","{null f1,int f2}"); }
	@Test public void test_5080() { checkNotSubtype("{null f1,null f2}","{null f2,int f3}"); }
	@Test public void test_5081() { checkIsSubtype("{null f1,null f2}","{int f1,void f2}"); }
	@Test public void test_5082() { checkIsSubtype("{null f1,null f2}","{int f2,void f3}"); }
	@Test public void test_5083() { checkNotSubtype("{null f1,null f2}","{int f1,any f2}"); }
	@Test public void test_5084() { checkNotSubtype("{null f1,null f2}","{int f2,any f3}"); }
	@Test public void test_5085() { checkNotSubtype("{null f1,null f2}","{int f1,null f2}"); }
	@Test public void test_5086() { checkNotSubtype("{null f1,null f2}","{int f2,null f3}"); }
	@Test public void test_5087() { checkNotSubtype("{null f1,null f2}","{int f1,int f2}"); }
	@Test public void test_5088() { checkNotSubtype("{null f1,null f2}","{int f2,int f3}"); }
	@Test public void test_5089() { checkNotSubtype("{null f1,null f2}","{[void] f1}"); }
	@Test public void test_5090() { checkNotSubtype("{null f1,null f2}","{[void] f2}"); }
	@Test public void test_5091() { checkIsSubtype("{null f1,null f2}","{[void] f1,void f2}"); }
	@Test public void test_5092() { checkIsSubtype("{null f1,null f2}","{[void] f2,void f3}"); }
	@Test public void test_5093() { checkNotSubtype("{null f1,null f2}","{[any] f1}"); }
	@Test public void test_5094() { checkNotSubtype("{null f1,null f2}","{[any] f2}"); }
	@Test public void test_5095() { checkNotSubtype("{null f1,null f2}","{[any] f1,any f2}"); }
	@Test public void test_5096() { checkNotSubtype("{null f1,null f2}","{[any] f2,any f3}"); }
	@Test public void test_5097() { checkNotSubtype("{null f1,null f2}","{[null] f1}"); }
	@Test public void test_5098() { checkNotSubtype("{null f1,null f2}","{[null] f2}"); }
	@Test public void test_5099() { checkNotSubtype("{null f1,null f2}","{[null] f1,null f2}"); }
	@Test public void test_5100() { checkNotSubtype("{null f1,null f2}","{[null] f2,null f3}"); }
	@Test public void test_5101() { checkNotSubtype("{null f1,null f2}","{[int] f1}"); }
	@Test public void test_5102() { checkNotSubtype("{null f1,null f2}","{[int] f2}"); }
	@Test public void test_5103() { checkNotSubtype("{null f1,null f2}","{[int] f1,int f2}"); }
	@Test public void test_5104() { checkNotSubtype("{null f1,null f2}","{[int] f2,int f3}"); }
	@Test public void test_5105() { checkIsSubtype("{null f1,null f2}","{{void f1} f1}"); }
	@Test public void test_5106() { checkIsSubtype("{null f1,null f2}","{{void f2} f1}"); }
	@Test public void test_5107() { checkIsSubtype("{null f1,null f2}","{{void f1} f2}"); }
	@Test public void test_5108() { checkIsSubtype("{null f1,null f2}","{{void f2} f2}"); }
	@Test public void test_5109() { checkIsSubtype("{null f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_5110() { checkIsSubtype("{null f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_5111() { checkIsSubtype("{null f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_5112() { checkIsSubtype("{null f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_5113() { checkNotSubtype("{null f1,null f2}","{{any f1} f1}"); }
	@Test public void test_5114() { checkNotSubtype("{null f1,null f2}","{{any f2} f1}"); }
	@Test public void test_5115() { checkNotSubtype("{null f1,null f2}","{{any f1} f2}"); }
	@Test public void test_5116() { checkNotSubtype("{null f1,null f2}","{{any f2} f2}"); }
	@Test public void test_5117() { checkNotSubtype("{null f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_5118() { checkNotSubtype("{null f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_5119() { checkNotSubtype("{null f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_5120() { checkNotSubtype("{null f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_5121() { checkNotSubtype("{null f1,null f2}","{{null f1} f1}"); }
	@Test public void test_5122() { checkNotSubtype("{null f1,null f2}","{{null f2} f1}"); }
	@Test public void test_5123() { checkNotSubtype("{null f1,null f2}","{{null f1} f2}"); }
	@Test public void test_5124() { checkNotSubtype("{null f1,null f2}","{{null f2} f2}"); }
	@Test public void test_5125() { checkNotSubtype("{null f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_5126() { checkNotSubtype("{null f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_5127() { checkNotSubtype("{null f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_5128() { checkNotSubtype("{null f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_5129() { checkNotSubtype("{null f1,null f2}","{{int f1} f1}"); }
	@Test public void test_5130() { checkNotSubtype("{null f1,null f2}","{{int f2} f1}"); }
	@Test public void test_5131() { checkNotSubtype("{null f1,null f2}","{{int f1} f2}"); }
	@Test public void test_5132() { checkNotSubtype("{null f1,null f2}","{{int f2} f2}"); }
	@Test public void test_5133() { checkNotSubtype("{null f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_5134() { checkNotSubtype("{null f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_5135() { checkNotSubtype("{null f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_5136() { checkNotSubtype("{null f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_5137() { checkNotSubtype("{null f2,null f3}","any"); }
	@Test public void test_5138() { checkNotSubtype("{null f2,null f3}","null"); }
	@Test public void test_5139() { checkNotSubtype("{null f2,null f3}","int"); }
	@Test public void test_5140() { checkNotSubtype("{null f2,null f3}","[void]"); }
	@Test public void test_5141() { checkNotSubtype("{null f2,null f3}","[any]"); }
	@Test public void test_5142() { checkNotSubtype("{null f2,null f3}","[null]"); }
	@Test public void test_5143() { checkNotSubtype("{null f2,null f3}","[int]"); }
	@Test public void test_5144() { checkIsSubtype("{null f2,null f3}","{void f1}"); }
	@Test public void test_5145() { checkIsSubtype("{null f2,null f3}","{void f2}"); }
	@Test public void test_5146() { checkNotSubtype("{null f2,null f3}","{any f1}"); }
	@Test public void test_5147() { checkNotSubtype("{null f2,null f3}","{any f2}"); }
	@Test public void test_5148() { checkNotSubtype("{null f2,null f3}","{null f1}"); }
	@Test public void test_5149() { checkNotSubtype("{null f2,null f3}","{null f2}"); }
	@Test public void test_5150() { checkNotSubtype("{null f2,null f3}","{int f1}"); }
	@Test public void test_5151() { checkNotSubtype("{null f2,null f3}","{int f2}"); }
	@Test public void test_5152() { checkNotSubtype("{null f2,null f3}","[[void]]"); }
	@Test public void test_5153() { checkNotSubtype("{null f2,null f3}","[[any]]"); }
	@Test public void test_5154() { checkNotSubtype("{null f2,null f3}","[[null]]"); }
	@Test public void test_5155() { checkNotSubtype("{null f2,null f3}","[[int]]"); }
	@Test public void test_5156() { checkNotSubtype("{null f2,null f3}","[{void f1}]"); }
	@Test public void test_5157() { checkNotSubtype("{null f2,null f3}","[{void f2}]"); }
	@Test public void test_5158() { checkNotSubtype("{null f2,null f3}","[{any f1}]"); }
	@Test public void test_5159() { checkNotSubtype("{null f2,null f3}","[{any f2}]"); }
	@Test public void test_5160() { checkNotSubtype("{null f2,null f3}","[{null f1}]"); }
	@Test public void test_5161() { checkNotSubtype("{null f2,null f3}","[{null f2}]"); }
	@Test public void test_5162() { checkNotSubtype("{null f2,null f3}","[{int f1}]"); }
	@Test public void test_5163() { checkNotSubtype("{null f2,null f3}","[{int f2}]"); }
	@Test public void test_5164() { checkIsSubtype("{null f2,null f3}","{void f1,void f2}"); }
	@Test public void test_5165() { checkIsSubtype("{null f2,null f3}","{void f2,void f3}"); }
	@Test public void test_5166() { checkIsSubtype("{null f2,null f3}","{void f1,any f2}"); }
	@Test public void test_5167() { checkIsSubtype("{null f2,null f3}","{void f2,any f3}"); }
	@Test public void test_5168() { checkIsSubtype("{null f2,null f3}","{void f1,null f2}"); }
	@Test public void test_5169() { checkIsSubtype("{null f2,null f3}","{void f2,null f3}"); }
	@Test public void test_5170() { checkIsSubtype("{null f2,null f3}","{void f1,int f2}"); }
	@Test public void test_5171() { checkIsSubtype("{null f2,null f3}","{void f2,int f3}"); }
	@Test public void test_5172() { checkIsSubtype("{null f2,null f3}","{any f1,void f2}"); }
	@Test public void test_5173() { checkIsSubtype("{null f2,null f3}","{any f2,void f3}"); }
	@Test public void test_5174() { checkNotSubtype("{null f2,null f3}","{any f1,any f2}"); }
	@Test public void test_5175() { checkNotSubtype("{null f2,null f3}","{any f2,any f3}"); }
	@Test public void test_5176() { checkNotSubtype("{null f2,null f3}","{any f1,null f2}"); }
	@Test public void test_5177() { checkNotSubtype("{null f2,null f3}","{any f2,null f3}"); }
	@Test public void test_5178() { checkNotSubtype("{null f2,null f3}","{any f1,int f2}"); }
	@Test public void test_5179() { checkNotSubtype("{null f2,null f3}","{any f2,int f3}"); }
	@Test public void test_5180() { checkIsSubtype("{null f2,null f3}","{null f1,void f2}"); }
	@Test public void test_5181() { checkIsSubtype("{null f2,null f3}","{null f2,void f3}"); }
	@Test public void test_5182() { checkNotSubtype("{null f2,null f3}","{null f1,any f2}"); }
	@Test public void test_5183() { checkNotSubtype("{null f2,null f3}","{null f2,any f3}"); }
	@Test public void test_5184() { checkNotSubtype("{null f2,null f3}","{null f1,null f2}"); }
	@Test public void test_5185() { checkIsSubtype("{null f2,null f3}","{null f2,null f3}"); }
	@Test public void test_5186() { checkNotSubtype("{null f2,null f3}","{null f1,int f2}"); }
	@Test public void test_5187() { checkNotSubtype("{null f2,null f3}","{null f2,int f3}"); }
	@Test public void test_5188() { checkIsSubtype("{null f2,null f3}","{int f1,void f2}"); }
	@Test public void test_5189() { checkIsSubtype("{null f2,null f3}","{int f2,void f3}"); }
	@Test public void test_5190() { checkNotSubtype("{null f2,null f3}","{int f1,any f2}"); }
	@Test public void test_5191() { checkNotSubtype("{null f2,null f3}","{int f2,any f3}"); }
	@Test public void test_5192() { checkNotSubtype("{null f2,null f3}","{int f1,null f2}"); }
	@Test public void test_5193() { checkNotSubtype("{null f2,null f3}","{int f2,null f3}"); }
	@Test public void test_5194() { checkNotSubtype("{null f2,null f3}","{int f1,int f2}"); }
	@Test public void test_5195() { checkNotSubtype("{null f2,null f3}","{int f2,int f3}"); }
	@Test public void test_5196() { checkNotSubtype("{null f2,null f3}","{[void] f1}"); }
	@Test public void test_5197() { checkNotSubtype("{null f2,null f3}","{[void] f2}"); }
	@Test public void test_5198() { checkIsSubtype("{null f2,null f3}","{[void] f1,void f2}"); }
	@Test public void test_5199() { checkIsSubtype("{null f2,null f3}","{[void] f2,void f3}"); }
	@Test public void test_5200() { checkNotSubtype("{null f2,null f3}","{[any] f1}"); }
	@Test public void test_5201() { checkNotSubtype("{null f2,null f3}","{[any] f2}"); }
	@Test public void test_5202() { checkNotSubtype("{null f2,null f3}","{[any] f1,any f2}"); }
	@Test public void test_5203() { checkNotSubtype("{null f2,null f3}","{[any] f2,any f3}"); }
	@Test public void test_5204() { checkNotSubtype("{null f2,null f3}","{[null] f1}"); }
	@Test public void test_5205() { checkNotSubtype("{null f2,null f3}","{[null] f2}"); }
	@Test public void test_5206() { checkNotSubtype("{null f2,null f3}","{[null] f1,null f2}"); }
	@Test public void test_5207() { checkNotSubtype("{null f2,null f3}","{[null] f2,null f3}"); }
	@Test public void test_5208() { checkNotSubtype("{null f2,null f3}","{[int] f1}"); }
	@Test public void test_5209() { checkNotSubtype("{null f2,null f3}","{[int] f2}"); }
	@Test public void test_5210() { checkNotSubtype("{null f2,null f3}","{[int] f1,int f2}"); }
	@Test public void test_5211() { checkNotSubtype("{null f2,null f3}","{[int] f2,int f3}"); }
	@Test public void test_5212() { checkIsSubtype("{null f2,null f3}","{{void f1} f1}"); }
	@Test public void test_5213() { checkIsSubtype("{null f2,null f3}","{{void f2} f1}"); }
	@Test public void test_5214() { checkIsSubtype("{null f2,null f3}","{{void f1} f2}"); }
	@Test public void test_5215() { checkIsSubtype("{null f2,null f3}","{{void f2} f2}"); }
	@Test public void test_5216() { checkIsSubtype("{null f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_5217() { checkIsSubtype("{null f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_5218() { checkIsSubtype("{null f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_5219() { checkIsSubtype("{null f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_5220() { checkNotSubtype("{null f2,null f3}","{{any f1} f1}"); }
	@Test public void test_5221() { checkNotSubtype("{null f2,null f3}","{{any f2} f1}"); }
	@Test public void test_5222() { checkNotSubtype("{null f2,null f3}","{{any f1} f2}"); }
	@Test public void test_5223() { checkNotSubtype("{null f2,null f3}","{{any f2} f2}"); }
	@Test public void test_5224() { checkNotSubtype("{null f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_5225() { checkNotSubtype("{null f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_5226() { checkNotSubtype("{null f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_5227() { checkNotSubtype("{null f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_5228() { checkNotSubtype("{null f2,null f3}","{{null f1} f1}"); }
	@Test public void test_5229() { checkNotSubtype("{null f2,null f3}","{{null f2} f1}"); }
	@Test public void test_5230() { checkNotSubtype("{null f2,null f3}","{{null f1} f2}"); }
	@Test public void test_5231() { checkNotSubtype("{null f2,null f3}","{{null f2} f2}"); }
	@Test public void test_5232() { checkNotSubtype("{null f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_5233() { checkNotSubtype("{null f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_5234() { checkNotSubtype("{null f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_5235() { checkNotSubtype("{null f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_5236() { checkNotSubtype("{null f2,null f3}","{{int f1} f1}"); }
	@Test public void test_5237() { checkNotSubtype("{null f2,null f3}","{{int f2} f1}"); }
	@Test public void test_5238() { checkNotSubtype("{null f2,null f3}","{{int f1} f2}"); }
	@Test public void test_5239() { checkNotSubtype("{null f2,null f3}","{{int f2} f2}"); }
	@Test public void test_5240() { checkNotSubtype("{null f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_5241() { checkNotSubtype("{null f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_5242() { checkNotSubtype("{null f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_5243() { checkNotSubtype("{null f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_5244() { checkNotSubtype("{null f1,int f2}","any"); }
	@Test public void test_5245() { checkNotSubtype("{null f1,int f2}","null"); }
	@Test public void test_5246() { checkNotSubtype("{null f1,int f2}","int"); }
	@Test public void test_5247() { checkNotSubtype("{null f1,int f2}","[void]"); }
	@Test public void test_5248() { checkNotSubtype("{null f1,int f2}","[any]"); }
	@Test public void test_5249() { checkNotSubtype("{null f1,int f2}","[null]"); }
	@Test public void test_5250() { checkNotSubtype("{null f1,int f2}","[int]"); }
	@Test public void test_5251() { checkIsSubtype("{null f1,int f2}","{void f1}"); }
	@Test public void test_5252() { checkIsSubtype("{null f1,int f2}","{void f2}"); }
	@Test public void test_5253() { checkNotSubtype("{null f1,int f2}","{any f1}"); }
	@Test public void test_5254() { checkNotSubtype("{null f1,int f2}","{any f2}"); }
	@Test public void test_5255() { checkNotSubtype("{null f1,int f2}","{null f1}"); }
	@Test public void test_5256() { checkNotSubtype("{null f1,int f2}","{null f2}"); }
	@Test public void test_5257() { checkNotSubtype("{null f1,int f2}","{int f1}"); }
	@Test public void test_5258() { checkNotSubtype("{null f1,int f2}","{int f2}"); }
	@Test public void test_5259() { checkNotSubtype("{null f1,int f2}","[[void]]"); }
	@Test public void test_5260() { checkNotSubtype("{null f1,int f2}","[[any]]"); }
	@Test public void test_5261() { checkNotSubtype("{null f1,int f2}","[[null]]"); }
	@Test public void test_5262() { checkNotSubtype("{null f1,int f2}","[[int]]"); }
	@Test public void test_5263() { checkNotSubtype("{null f1,int f2}","[{void f1}]"); }
	@Test public void test_5264() { checkNotSubtype("{null f1,int f2}","[{void f2}]"); }
	@Test public void test_5265() { checkNotSubtype("{null f1,int f2}","[{any f1}]"); }
	@Test public void test_5266() { checkNotSubtype("{null f1,int f2}","[{any f2}]"); }
	@Test public void test_5267() { checkNotSubtype("{null f1,int f2}","[{null f1}]"); }
	@Test public void test_5268() { checkNotSubtype("{null f1,int f2}","[{null f2}]"); }
	@Test public void test_5269() { checkNotSubtype("{null f1,int f2}","[{int f1}]"); }
	@Test public void test_5270() { checkNotSubtype("{null f1,int f2}","[{int f2}]"); }
	@Test public void test_5271() { checkIsSubtype("{null f1,int f2}","{void f1,void f2}"); }
	@Test public void test_5272() { checkIsSubtype("{null f1,int f2}","{void f2,void f3}"); }
	@Test public void test_5273() { checkIsSubtype("{null f1,int f2}","{void f1,any f2}"); }
	@Test public void test_5274() { checkIsSubtype("{null f1,int f2}","{void f2,any f3}"); }
	@Test public void test_5275() { checkIsSubtype("{null f1,int f2}","{void f1,null f2}"); }
	@Test public void test_5276() { checkIsSubtype("{null f1,int f2}","{void f2,null f3}"); }
	@Test public void test_5277() { checkIsSubtype("{null f1,int f2}","{void f1,int f2}"); }
	@Test public void test_5278() { checkIsSubtype("{null f1,int f2}","{void f2,int f3}"); }
	@Test public void test_5279() { checkIsSubtype("{null f1,int f2}","{any f1,void f2}"); }
	@Test public void test_5280() { checkIsSubtype("{null f1,int f2}","{any f2,void f3}"); }
	@Test public void test_5281() { checkNotSubtype("{null f1,int f2}","{any f1,any f2}"); }
	@Test public void test_5282() { checkNotSubtype("{null f1,int f2}","{any f2,any f3}"); }
	@Test public void test_5283() { checkNotSubtype("{null f1,int f2}","{any f1,null f2}"); }
	@Test public void test_5284() { checkNotSubtype("{null f1,int f2}","{any f2,null f3}"); }
	@Test public void test_5285() { checkNotSubtype("{null f1,int f2}","{any f1,int f2}"); }
	@Test public void test_5286() { checkNotSubtype("{null f1,int f2}","{any f2,int f3}"); }
	@Test public void test_5287() { checkIsSubtype("{null f1,int f2}","{null f1,void f2}"); }
	@Test public void test_5288() { checkIsSubtype("{null f1,int f2}","{null f2,void f3}"); }
	@Test public void test_5289() { checkNotSubtype("{null f1,int f2}","{null f1,any f2}"); }
	@Test public void test_5290() { checkNotSubtype("{null f1,int f2}","{null f2,any f3}"); }
	@Test public void test_5291() { checkNotSubtype("{null f1,int f2}","{null f1,null f2}"); }
	@Test public void test_5292() { checkNotSubtype("{null f1,int f2}","{null f2,null f3}"); }
	@Test public void test_5293() { checkIsSubtype("{null f1,int f2}","{null f1,int f2}"); }
	@Test public void test_5294() { checkNotSubtype("{null f1,int f2}","{null f2,int f3}"); }
	@Test public void test_5295() { checkIsSubtype("{null f1,int f2}","{int f1,void f2}"); }
	@Test public void test_5296() { checkIsSubtype("{null f1,int f2}","{int f2,void f3}"); }
	@Test public void test_5297() { checkNotSubtype("{null f1,int f2}","{int f1,any f2}"); }
	@Test public void test_5298() { checkNotSubtype("{null f1,int f2}","{int f2,any f3}"); }
	@Test public void test_5299() { checkNotSubtype("{null f1,int f2}","{int f1,null f2}"); }
	@Test public void test_5300() { checkNotSubtype("{null f1,int f2}","{int f2,null f3}"); }
	@Test public void test_5301() { checkNotSubtype("{null f1,int f2}","{int f1,int f2}"); }
	@Test public void test_5302() { checkNotSubtype("{null f1,int f2}","{int f2,int f3}"); }
	@Test public void test_5303() { checkNotSubtype("{null f1,int f2}","{[void] f1}"); }
	@Test public void test_5304() { checkNotSubtype("{null f1,int f2}","{[void] f2}"); }
	@Test public void test_5305() { checkIsSubtype("{null f1,int f2}","{[void] f1,void f2}"); }
	@Test public void test_5306() { checkIsSubtype("{null f1,int f2}","{[void] f2,void f3}"); }
	@Test public void test_5307() { checkNotSubtype("{null f1,int f2}","{[any] f1}"); }
	@Test public void test_5308() { checkNotSubtype("{null f1,int f2}","{[any] f2}"); }
	@Test public void test_5309() { checkNotSubtype("{null f1,int f2}","{[any] f1,any f2}"); }
	@Test public void test_5310() { checkNotSubtype("{null f1,int f2}","{[any] f2,any f3}"); }
	@Test public void test_5311() { checkNotSubtype("{null f1,int f2}","{[null] f1}"); }
	@Test public void test_5312() { checkNotSubtype("{null f1,int f2}","{[null] f2}"); }
	@Test public void test_5313() { checkNotSubtype("{null f1,int f2}","{[null] f1,null f2}"); }
	@Test public void test_5314() { checkNotSubtype("{null f1,int f2}","{[null] f2,null f3}"); }
	@Test public void test_5315() { checkNotSubtype("{null f1,int f2}","{[int] f1}"); }
	@Test public void test_5316() { checkNotSubtype("{null f1,int f2}","{[int] f2}"); }
	@Test public void test_5317() { checkNotSubtype("{null f1,int f2}","{[int] f1,int f2}"); }
	@Test public void test_5318() { checkNotSubtype("{null f1,int f2}","{[int] f2,int f3}"); }
	@Test public void test_5319() { checkIsSubtype("{null f1,int f2}","{{void f1} f1}"); }
	@Test public void test_5320() { checkIsSubtype("{null f1,int f2}","{{void f2} f1}"); }
	@Test public void test_5321() { checkIsSubtype("{null f1,int f2}","{{void f1} f2}"); }
	@Test public void test_5322() { checkIsSubtype("{null f1,int f2}","{{void f2} f2}"); }
	@Test public void test_5323() { checkIsSubtype("{null f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_5324() { checkIsSubtype("{null f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_5325() { checkIsSubtype("{null f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_5326() { checkIsSubtype("{null f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_5327() { checkNotSubtype("{null f1,int f2}","{{any f1} f1}"); }
	@Test public void test_5328() { checkNotSubtype("{null f1,int f2}","{{any f2} f1}"); }
	@Test public void test_5329() { checkNotSubtype("{null f1,int f2}","{{any f1} f2}"); }
	@Test public void test_5330() { checkNotSubtype("{null f1,int f2}","{{any f2} f2}"); }
	@Test public void test_5331() { checkNotSubtype("{null f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_5332() { checkNotSubtype("{null f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_5333() { checkNotSubtype("{null f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_5334() { checkNotSubtype("{null f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_5335() { checkNotSubtype("{null f1,int f2}","{{null f1} f1}"); }
	@Test public void test_5336() { checkNotSubtype("{null f1,int f2}","{{null f2} f1}"); }
	@Test public void test_5337() { checkNotSubtype("{null f1,int f2}","{{null f1} f2}"); }
	@Test public void test_5338() { checkNotSubtype("{null f1,int f2}","{{null f2} f2}"); }
	@Test public void test_5339() { checkNotSubtype("{null f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_5340() { checkNotSubtype("{null f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_5341() { checkNotSubtype("{null f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_5342() { checkNotSubtype("{null f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_5343() { checkNotSubtype("{null f1,int f2}","{{int f1} f1}"); }
	@Test public void test_5344() { checkNotSubtype("{null f1,int f2}","{{int f2} f1}"); }
	@Test public void test_5345() { checkNotSubtype("{null f1,int f2}","{{int f1} f2}"); }
	@Test public void test_5346() { checkNotSubtype("{null f1,int f2}","{{int f2} f2}"); }
	@Test public void test_5347() { checkNotSubtype("{null f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_5348() { checkNotSubtype("{null f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_5349() { checkNotSubtype("{null f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_5350() { checkNotSubtype("{null f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_5351() { checkNotSubtype("{null f2,int f3}","any"); }
	@Test public void test_5352() { checkNotSubtype("{null f2,int f3}","null"); }
	@Test public void test_5353() { checkNotSubtype("{null f2,int f3}","int"); }
	@Test public void test_5354() { checkNotSubtype("{null f2,int f3}","[void]"); }
	@Test public void test_5355() { checkNotSubtype("{null f2,int f3}","[any]"); }
	@Test public void test_5356() { checkNotSubtype("{null f2,int f3}","[null]"); }
	@Test public void test_5357() { checkNotSubtype("{null f2,int f3}","[int]"); }
	@Test public void test_5358() { checkIsSubtype("{null f2,int f3}","{void f1}"); }
	@Test public void test_5359() { checkIsSubtype("{null f2,int f3}","{void f2}"); }
	@Test public void test_5360() { checkNotSubtype("{null f2,int f3}","{any f1}"); }
	@Test public void test_5361() { checkNotSubtype("{null f2,int f3}","{any f2}"); }
	@Test public void test_5362() { checkNotSubtype("{null f2,int f3}","{null f1}"); }
	@Test public void test_5363() { checkNotSubtype("{null f2,int f3}","{null f2}"); }
	@Test public void test_5364() { checkNotSubtype("{null f2,int f3}","{int f1}"); }
	@Test public void test_5365() { checkNotSubtype("{null f2,int f3}","{int f2}"); }
	@Test public void test_5366() { checkNotSubtype("{null f2,int f3}","[[void]]"); }
	@Test public void test_5367() { checkNotSubtype("{null f2,int f3}","[[any]]"); }
	@Test public void test_5368() { checkNotSubtype("{null f2,int f3}","[[null]]"); }
	@Test public void test_5369() { checkNotSubtype("{null f2,int f3}","[[int]]"); }
	@Test public void test_5370() { checkNotSubtype("{null f2,int f3}","[{void f1}]"); }
	@Test public void test_5371() { checkNotSubtype("{null f2,int f3}","[{void f2}]"); }
	@Test public void test_5372() { checkNotSubtype("{null f2,int f3}","[{any f1}]"); }
	@Test public void test_5373() { checkNotSubtype("{null f2,int f3}","[{any f2}]"); }
	@Test public void test_5374() { checkNotSubtype("{null f2,int f3}","[{null f1}]"); }
	@Test public void test_5375() { checkNotSubtype("{null f2,int f3}","[{null f2}]"); }
	@Test public void test_5376() { checkNotSubtype("{null f2,int f3}","[{int f1}]"); }
	@Test public void test_5377() { checkNotSubtype("{null f2,int f3}","[{int f2}]"); }
	@Test public void test_5378() { checkIsSubtype("{null f2,int f3}","{void f1,void f2}"); }
	@Test public void test_5379() { checkIsSubtype("{null f2,int f3}","{void f2,void f3}"); }
	@Test public void test_5380() { checkIsSubtype("{null f2,int f3}","{void f1,any f2}"); }
	@Test public void test_5381() { checkIsSubtype("{null f2,int f3}","{void f2,any f3}"); }
	@Test public void test_5382() { checkIsSubtype("{null f2,int f3}","{void f1,null f2}"); }
	@Test public void test_5383() { checkIsSubtype("{null f2,int f3}","{void f2,null f3}"); }
	@Test public void test_5384() { checkIsSubtype("{null f2,int f3}","{void f1,int f2}"); }
	@Test public void test_5385() { checkIsSubtype("{null f2,int f3}","{void f2,int f3}"); }
	@Test public void test_5386() { checkIsSubtype("{null f2,int f3}","{any f1,void f2}"); }
	@Test public void test_5387() { checkIsSubtype("{null f2,int f3}","{any f2,void f3}"); }
	@Test public void test_5388() { checkNotSubtype("{null f2,int f3}","{any f1,any f2}"); }
	@Test public void test_5389() { checkNotSubtype("{null f2,int f3}","{any f2,any f3}"); }
	@Test public void test_5390() { checkNotSubtype("{null f2,int f3}","{any f1,null f2}"); }
	@Test public void test_5391() { checkNotSubtype("{null f2,int f3}","{any f2,null f3}"); }
	@Test public void test_5392() { checkNotSubtype("{null f2,int f3}","{any f1,int f2}"); }
	@Test public void test_5393() { checkNotSubtype("{null f2,int f3}","{any f2,int f3}"); }
	@Test public void test_5394() { checkIsSubtype("{null f2,int f3}","{null f1,void f2}"); }
	@Test public void test_5395() { checkIsSubtype("{null f2,int f3}","{null f2,void f3}"); }
	@Test public void test_5396() { checkNotSubtype("{null f2,int f3}","{null f1,any f2}"); }
	@Test public void test_5397() { checkNotSubtype("{null f2,int f3}","{null f2,any f3}"); }
	@Test public void test_5398() { checkNotSubtype("{null f2,int f3}","{null f1,null f2}"); }
	@Test public void test_5399() { checkNotSubtype("{null f2,int f3}","{null f2,null f3}"); }
	@Test public void test_5400() { checkNotSubtype("{null f2,int f3}","{null f1,int f2}"); }
	@Test public void test_5401() { checkIsSubtype("{null f2,int f3}","{null f2,int f3}"); }
	@Test public void test_5402() { checkIsSubtype("{null f2,int f3}","{int f1,void f2}"); }
	@Test public void test_5403() { checkIsSubtype("{null f2,int f3}","{int f2,void f3}"); }
	@Test public void test_5404() { checkNotSubtype("{null f2,int f3}","{int f1,any f2}"); }
	@Test public void test_5405() { checkNotSubtype("{null f2,int f3}","{int f2,any f3}"); }
	@Test public void test_5406() { checkNotSubtype("{null f2,int f3}","{int f1,null f2}"); }
	@Test public void test_5407() { checkNotSubtype("{null f2,int f3}","{int f2,null f3}"); }
	@Test public void test_5408() { checkNotSubtype("{null f2,int f3}","{int f1,int f2}"); }
	@Test public void test_5409() { checkNotSubtype("{null f2,int f3}","{int f2,int f3}"); }
	@Test public void test_5410() { checkNotSubtype("{null f2,int f3}","{[void] f1}"); }
	@Test public void test_5411() { checkNotSubtype("{null f2,int f3}","{[void] f2}"); }
	@Test public void test_5412() { checkIsSubtype("{null f2,int f3}","{[void] f1,void f2}"); }
	@Test public void test_5413() { checkIsSubtype("{null f2,int f3}","{[void] f2,void f3}"); }
	@Test public void test_5414() { checkNotSubtype("{null f2,int f3}","{[any] f1}"); }
	@Test public void test_5415() { checkNotSubtype("{null f2,int f3}","{[any] f2}"); }
	@Test public void test_5416() { checkNotSubtype("{null f2,int f3}","{[any] f1,any f2}"); }
	@Test public void test_5417() { checkNotSubtype("{null f2,int f3}","{[any] f2,any f3}"); }
	@Test public void test_5418() { checkNotSubtype("{null f2,int f3}","{[null] f1}"); }
	@Test public void test_5419() { checkNotSubtype("{null f2,int f3}","{[null] f2}"); }
	@Test public void test_5420() { checkNotSubtype("{null f2,int f3}","{[null] f1,null f2}"); }
	@Test public void test_5421() { checkNotSubtype("{null f2,int f3}","{[null] f2,null f3}"); }
	@Test public void test_5422() { checkNotSubtype("{null f2,int f3}","{[int] f1}"); }
	@Test public void test_5423() { checkNotSubtype("{null f2,int f3}","{[int] f2}"); }
	@Test public void test_5424() { checkNotSubtype("{null f2,int f3}","{[int] f1,int f2}"); }
	@Test public void test_5425() { checkNotSubtype("{null f2,int f3}","{[int] f2,int f3}"); }
	@Test public void test_5426() { checkIsSubtype("{null f2,int f3}","{{void f1} f1}"); }
	@Test public void test_5427() { checkIsSubtype("{null f2,int f3}","{{void f2} f1}"); }
	@Test public void test_5428() { checkIsSubtype("{null f2,int f3}","{{void f1} f2}"); }
	@Test public void test_5429() { checkIsSubtype("{null f2,int f3}","{{void f2} f2}"); }
	@Test public void test_5430() { checkIsSubtype("{null f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_5431() { checkIsSubtype("{null f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_5432() { checkIsSubtype("{null f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_5433() { checkIsSubtype("{null f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_5434() { checkNotSubtype("{null f2,int f3}","{{any f1} f1}"); }
	@Test public void test_5435() { checkNotSubtype("{null f2,int f3}","{{any f2} f1}"); }
	@Test public void test_5436() { checkNotSubtype("{null f2,int f3}","{{any f1} f2}"); }
	@Test public void test_5437() { checkNotSubtype("{null f2,int f3}","{{any f2} f2}"); }
	@Test public void test_5438() { checkNotSubtype("{null f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_5439() { checkNotSubtype("{null f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_5440() { checkNotSubtype("{null f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_5441() { checkNotSubtype("{null f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_5442() { checkNotSubtype("{null f2,int f3}","{{null f1} f1}"); }
	@Test public void test_5443() { checkNotSubtype("{null f2,int f3}","{{null f2} f1}"); }
	@Test public void test_5444() { checkNotSubtype("{null f2,int f3}","{{null f1} f2}"); }
	@Test public void test_5445() { checkNotSubtype("{null f2,int f3}","{{null f2} f2}"); }
	@Test public void test_5446() { checkNotSubtype("{null f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_5447() { checkNotSubtype("{null f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_5448() { checkNotSubtype("{null f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_5449() { checkNotSubtype("{null f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_5450() { checkNotSubtype("{null f2,int f3}","{{int f1} f1}"); }
	@Test public void test_5451() { checkNotSubtype("{null f2,int f3}","{{int f2} f1}"); }
	@Test public void test_5452() { checkNotSubtype("{null f2,int f3}","{{int f1} f2}"); }
	@Test public void test_5453() { checkNotSubtype("{null f2,int f3}","{{int f2} f2}"); }
	@Test public void test_5454() { checkNotSubtype("{null f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_5455() { checkNotSubtype("{null f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_5456() { checkNotSubtype("{null f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_5457() { checkNotSubtype("{null f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_5458() { checkNotSubtype("{int f1,void f2}","any"); }
	@Test public void test_5459() { checkNotSubtype("{int f1,void f2}","null"); }
	@Test public void test_5460() { checkNotSubtype("{int f1,void f2}","int"); }
	@Test public void test_5461() { checkNotSubtype("{int f1,void f2}","[void]"); }
	@Test public void test_5462() { checkNotSubtype("{int f1,void f2}","[any]"); }
	@Test public void test_5463() { checkNotSubtype("{int f1,void f2}","[null]"); }
	@Test public void test_5464() { checkNotSubtype("{int f1,void f2}","[int]"); }
	@Test public void test_5465() { checkIsSubtype("{int f1,void f2}","{void f1}"); }
	@Test public void test_5466() { checkIsSubtype("{int f1,void f2}","{void f2}"); }
	@Test public void test_5467() { checkNotSubtype("{int f1,void f2}","{any f1}"); }
	@Test public void test_5468() { checkNotSubtype("{int f1,void f2}","{any f2}"); }
	@Test public void test_5469() { checkNotSubtype("{int f1,void f2}","{null f1}"); }
	@Test public void test_5470() { checkNotSubtype("{int f1,void f2}","{null f2}"); }
	@Test public void test_5471() { checkNotSubtype("{int f1,void f2}","{int f1}"); }
	@Test public void test_5472() { checkNotSubtype("{int f1,void f2}","{int f2}"); }
	@Test public void test_5473() { checkNotSubtype("{int f1,void f2}","[[void]]"); }
	@Test public void test_5474() { checkNotSubtype("{int f1,void f2}","[[any]]"); }
	@Test public void test_5475() { checkNotSubtype("{int f1,void f2}","[[null]]"); }
	@Test public void test_5476() { checkNotSubtype("{int f1,void f2}","[[int]]"); }
	@Test public void test_5477() { checkNotSubtype("{int f1,void f2}","[{void f1}]"); }
	@Test public void test_5478() { checkNotSubtype("{int f1,void f2}","[{void f2}]"); }
	@Test public void test_5479() { checkNotSubtype("{int f1,void f2}","[{any f1}]"); }
	@Test public void test_5480() { checkNotSubtype("{int f1,void f2}","[{any f2}]"); }
	@Test public void test_5481() { checkNotSubtype("{int f1,void f2}","[{null f1}]"); }
	@Test public void test_5482() { checkNotSubtype("{int f1,void f2}","[{null f2}]"); }
	@Test public void test_5483() { checkNotSubtype("{int f1,void f2}","[{int f1}]"); }
	@Test public void test_5484() { checkNotSubtype("{int f1,void f2}","[{int f2}]"); }
	@Test public void test_5485() { checkIsSubtype("{int f1,void f2}","{void f1,void f2}"); }
	@Test public void test_5486() { checkIsSubtype("{int f1,void f2}","{void f2,void f3}"); }
	@Test public void test_5487() { checkIsSubtype("{int f1,void f2}","{void f1,any f2}"); }
	@Test public void test_5488() { checkIsSubtype("{int f1,void f2}","{void f2,any f3}"); }
	@Test public void test_5489() { checkIsSubtype("{int f1,void f2}","{void f1,null f2}"); }
	@Test public void test_5490() { checkIsSubtype("{int f1,void f2}","{void f2,null f3}"); }
	@Test public void test_5491() { checkIsSubtype("{int f1,void f2}","{void f1,int f2}"); }
	@Test public void test_5492() { checkIsSubtype("{int f1,void f2}","{void f2,int f3}"); }
	@Test public void test_5493() { checkIsSubtype("{int f1,void f2}","{any f1,void f2}"); }
	@Test public void test_5494() { checkIsSubtype("{int f1,void f2}","{any f2,void f3}"); }
	@Test public void test_5495() { checkNotSubtype("{int f1,void f2}","{any f1,any f2}"); }
	@Test public void test_5496() { checkNotSubtype("{int f1,void f2}","{any f2,any f3}"); }
	@Test public void test_5497() { checkNotSubtype("{int f1,void f2}","{any f1,null f2}"); }
	@Test public void test_5498() { checkNotSubtype("{int f1,void f2}","{any f2,null f3}"); }
	@Test public void test_5499() { checkNotSubtype("{int f1,void f2}","{any f1,int f2}"); }
	@Test public void test_5500() { checkNotSubtype("{int f1,void f2}","{any f2,int f3}"); }
	@Test public void test_5501() { checkIsSubtype("{int f1,void f2}","{null f1,void f2}"); }
	@Test public void test_5502() { checkIsSubtype("{int f1,void f2}","{null f2,void f3}"); }
	@Test public void test_5503() { checkNotSubtype("{int f1,void f2}","{null f1,any f2}"); }
	@Test public void test_5504() { checkNotSubtype("{int f1,void f2}","{null f2,any f3}"); }
	@Test public void test_5505() { checkNotSubtype("{int f1,void f2}","{null f1,null f2}"); }
	@Test public void test_5506() { checkNotSubtype("{int f1,void f2}","{null f2,null f3}"); }
	@Test public void test_5507() { checkNotSubtype("{int f1,void f2}","{null f1,int f2}"); }
	@Test public void test_5508() { checkNotSubtype("{int f1,void f2}","{null f2,int f3}"); }
	@Test public void test_5509() { checkIsSubtype("{int f1,void f2}","{int f1,void f2}"); }
	@Test public void test_5510() { checkIsSubtype("{int f1,void f2}","{int f2,void f3}"); }
	@Test public void test_5511() { checkNotSubtype("{int f1,void f2}","{int f1,any f2}"); }
	@Test public void test_5512() { checkNotSubtype("{int f1,void f2}","{int f2,any f3}"); }
	@Test public void test_5513() { checkNotSubtype("{int f1,void f2}","{int f1,null f2}"); }
	@Test public void test_5514() { checkNotSubtype("{int f1,void f2}","{int f2,null f3}"); }
	@Test public void test_5515() { checkNotSubtype("{int f1,void f2}","{int f1,int f2}"); }
	@Test public void test_5516() { checkNotSubtype("{int f1,void f2}","{int f2,int f3}"); }
	@Test public void test_5517() { checkNotSubtype("{int f1,void f2}","{[void] f1}"); }
	@Test public void test_5518() { checkNotSubtype("{int f1,void f2}","{[void] f2}"); }
	@Test public void test_5519() { checkIsSubtype("{int f1,void f2}","{[void] f1,void f2}"); }
	@Test public void test_5520() { checkIsSubtype("{int f1,void f2}","{[void] f2,void f3}"); }
	@Test public void test_5521() { checkNotSubtype("{int f1,void f2}","{[any] f1}"); }
	@Test public void test_5522() { checkNotSubtype("{int f1,void f2}","{[any] f2}"); }
	@Test public void test_5523() { checkNotSubtype("{int f1,void f2}","{[any] f1,any f2}"); }
	@Test public void test_5524() { checkNotSubtype("{int f1,void f2}","{[any] f2,any f3}"); }
	@Test public void test_5525() { checkNotSubtype("{int f1,void f2}","{[null] f1}"); }
	@Test public void test_5526() { checkNotSubtype("{int f1,void f2}","{[null] f2}"); }
	@Test public void test_5527() { checkNotSubtype("{int f1,void f2}","{[null] f1,null f2}"); }
	@Test public void test_5528() { checkNotSubtype("{int f1,void f2}","{[null] f2,null f3}"); }
	@Test public void test_5529() { checkNotSubtype("{int f1,void f2}","{[int] f1}"); }
	@Test public void test_5530() { checkNotSubtype("{int f1,void f2}","{[int] f2}"); }
	@Test public void test_5531() { checkNotSubtype("{int f1,void f2}","{[int] f1,int f2}"); }
	@Test public void test_5532() { checkNotSubtype("{int f1,void f2}","{[int] f2,int f3}"); }
	@Test public void test_5533() { checkIsSubtype("{int f1,void f2}","{{void f1} f1}"); }
	@Test public void test_5534() { checkIsSubtype("{int f1,void f2}","{{void f2} f1}"); }
	@Test public void test_5535() { checkIsSubtype("{int f1,void f2}","{{void f1} f2}"); }
	@Test public void test_5536() { checkIsSubtype("{int f1,void f2}","{{void f2} f2}"); }
	@Test public void test_5537() { checkIsSubtype("{int f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_5538() { checkIsSubtype("{int f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_5539() { checkIsSubtype("{int f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_5540() { checkIsSubtype("{int f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_5541() { checkNotSubtype("{int f1,void f2}","{{any f1} f1}"); }
	@Test public void test_5542() { checkNotSubtype("{int f1,void f2}","{{any f2} f1}"); }
	@Test public void test_5543() { checkNotSubtype("{int f1,void f2}","{{any f1} f2}"); }
	@Test public void test_5544() { checkNotSubtype("{int f1,void f2}","{{any f2} f2}"); }
	@Test public void test_5545() { checkNotSubtype("{int f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_5546() { checkNotSubtype("{int f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_5547() { checkNotSubtype("{int f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_5548() { checkNotSubtype("{int f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_5549() { checkNotSubtype("{int f1,void f2}","{{null f1} f1}"); }
	@Test public void test_5550() { checkNotSubtype("{int f1,void f2}","{{null f2} f1}"); }
	@Test public void test_5551() { checkNotSubtype("{int f1,void f2}","{{null f1} f2}"); }
	@Test public void test_5552() { checkNotSubtype("{int f1,void f2}","{{null f2} f2}"); }
	@Test public void test_5553() { checkNotSubtype("{int f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_5554() { checkNotSubtype("{int f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_5555() { checkNotSubtype("{int f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_5556() { checkNotSubtype("{int f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_5557() { checkNotSubtype("{int f1,void f2}","{{int f1} f1}"); }
	@Test public void test_5558() { checkNotSubtype("{int f1,void f2}","{{int f2} f1}"); }
	@Test public void test_5559() { checkNotSubtype("{int f1,void f2}","{{int f1} f2}"); }
	@Test public void test_5560() { checkNotSubtype("{int f1,void f2}","{{int f2} f2}"); }
	@Test public void test_5561() { checkNotSubtype("{int f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_5562() { checkNotSubtype("{int f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_5563() { checkNotSubtype("{int f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_5564() { checkNotSubtype("{int f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_5565() { checkNotSubtype("{int f2,void f3}","any"); }
	@Test public void test_5566() { checkNotSubtype("{int f2,void f3}","null"); }
	@Test public void test_5567() { checkNotSubtype("{int f2,void f3}","int"); }
	@Test public void test_5568() { checkNotSubtype("{int f2,void f3}","[void]"); }
	@Test public void test_5569() { checkNotSubtype("{int f2,void f3}","[any]"); }
	@Test public void test_5570() { checkNotSubtype("{int f2,void f3}","[null]"); }
	@Test public void test_5571() { checkNotSubtype("{int f2,void f3}","[int]"); }
	@Test public void test_5572() { checkIsSubtype("{int f2,void f3}","{void f1}"); }
	@Test public void test_5573() { checkIsSubtype("{int f2,void f3}","{void f2}"); }
	@Test public void test_5574() { checkNotSubtype("{int f2,void f3}","{any f1}"); }
	@Test public void test_5575() { checkNotSubtype("{int f2,void f3}","{any f2}"); }
	@Test public void test_5576() { checkNotSubtype("{int f2,void f3}","{null f1}"); }
	@Test public void test_5577() { checkNotSubtype("{int f2,void f3}","{null f2}"); }
	@Test public void test_5578() { checkNotSubtype("{int f2,void f3}","{int f1}"); }
	@Test public void test_5579() { checkNotSubtype("{int f2,void f3}","{int f2}"); }
	@Test public void test_5580() { checkNotSubtype("{int f2,void f3}","[[void]]"); }
	@Test public void test_5581() { checkNotSubtype("{int f2,void f3}","[[any]]"); }
	@Test public void test_5582() { checkNotSubtype("{int f2,void f3}","[[null]]"); }
	@Test public void test_5583() { checkNotSubtype("{int f2,void f3}","[[int]]"); }
	@Test public void test_5584() { checkNotSubtype("{int f2,void f3}","[{void f1}]"); }
	@Test public void test_5585() { checkNotSubtype("{int f2,void f3}","[{void f2}]"); }
	@Test public void test_5586() { checkNotSubtype("{int f2,void f3}","[{any f1}]"); }
	@Test public void test_5587() { checkNotSubtype("{int f2,void f3}","[{any f2}]"); }
	@Test public void test_5588() { checkNotSubtype("{int f2,void f3}","[{null f1}]"); }
	@Test public void test_5589() { checkNotSubtype("{int f2,void f3}","[{null f2}]"); }
	@Test public void test_5590() { checkNotSubtype("{int f2,void f3}","[{int f1}]"); }
	@Test public void test_5591() { checkNotSubtype("{int f2,void f3}","[{int f2}]"); }
	@Test public void test_5592() { checkIsSubtype("{int f2,void f3}","{void f1,void f2}"); }
	@Test public void test_5593() { checkIsSubtype("{int f2,void f3}","{void f2,void f3}"); }
	@Test public void test_5594() { checkIsSubtype("{int f2,void f3}","{void f1,any f2}"); }
	@Test public void test_5595() { checkIsSubtype("{int f2,void f3}","{void f2,any f3}"); }
	@Test public void test_5596() { checkIsSubtype("{int f2,void f3}","{void f1,null f2}"); }
	@Test public void test_5597() { checkIsSubtype("{int f2,void f3}","{void f2,null f3}"); }
	@Test public void test_5598() { checkIsSubtype("{int f2,void f3}","{void f1,int f2}"); }
	@Test public void test_5599() { checkIsSubtype("{int f2,void f3}","{void f2,int f3}"); }
	@Test public void test_5600() { checkIsSubtype("{int f2,void f3}","{any f1,void f2}"); }
	@Test public void test_5601() { checkIsSubtype("{int f2,void f3}","{any f2,void f3}"); }
	@Test public void test_5602() { checkNotSubtype("{int f2,void f3}","{any f1,any f2}"); }
	@Test public void test_5603() { checkNotSubtype("{int f2,void f3}","{any f2,any f3}"); }
	@Test public void test_5604() { checkNotSubtype("{int f2,void f3}","{any f1,null f2}"); }
	@Test public void test_5605() { checkNotSubtype("{int f2,void f3}","{any f2,null f3}"); }
	@Test public void test_5606() { checkNotSubtype("{int f2,void f3}","{any f1,int f2}"); }
	@Test public void test_5607() { checkNotSubtype("{int f2,void f3}","{any f2,int f3}"); }
	@Test public void test_5608() { checkIsSubtype("{int f2,void f3}","{null f1,void f2}"); }
	@Test public void test_5609() { checkIsSubtype("{int f2,void f3}","{null f2,void f3}"); }
	@Test public void test_5610() { checkNotSubtype("{int f2,void f3}","{null f1,any f2}"); }
	@Test public void test_5611() { checkNotSubtype("{int f2,void f3}","{null f2,any f3}"); }
	@Test public void test_5612() { checkNotSubtype("{int f2,void f3}","{null f1,null f2}"); }
	@Test public void test_5613() { checkNotSubtype("{int f2,void f3}","{null f2,null f3}"); }
	@Test public void test_5614() { checkNotSubtype("{int f2,void f3}","{null f1,int f2}"); }
	@Test public void test_5615() { checkNotSubtype("{int f2,void f3}","{null f2,int f3}"); }
	@Test public void test_5616() { checkIsSubtype("{int f2,void f3}","{int f1,void f2}"); }
	@Test public void test_5617() { checkIsSubtype("{int f2,void f3}","{int f2,void f3}"); }
	@Test public void test_5618() { checkNotSubtype("{int f2,void f3}","{int f1,any f2}"); }
	@Test public void test_5619() { checkNotSubtype("{int f2,void f3}","{int f2,any f3}"); }
	@Test public void test_5620() { checkNotSubtype("{int f2,void f3}","{int f1,null f2}"); }
	@Test public void test_5621() { checkNotSubtype("{int f2,void f3}","{int f2,null f3}"); }
	@Test public void test_5622() { checkNotSubtype("{int f2,void f3}","{int f1,int f2}"); }
	@Test public void test_5623() { checkNotSubtype("{int f2,void f3}","{int f2,int f3}"); }
	@Test public void test_5624() { checkNotSubtype("{int f2,void f3}","{[void] f1}"); }
	@Test public void test_5625() { checkNotSubtype("{int f2,void f3}","{[void] f2}"); }
	@Test public void test_5626() { checkIsSubtype("{int f2,void f3}","{[void] f1,void f2}"); }
	@Test public void test_5627() { checkIsSubtype("{int f2,void f3}","{[void] f2,void f3}"); }
	@Test public void test_5628() { checkNotSubtype("{int f2,void f3}","{[any] f1}"); }
	@Test public void test_5629() { checkNotSubtype("{int f2,void f3}","{[any] f2}"); }
	@Test public void test_5630() { checkNotSubtype("{int f2,void f3}","{[any] f1,any f2}"); }
	@Test public void test_5631() { checkNotSubtype("{int f2,void f3}","{[any] f2,any f3}"); }
	@Test public void test_5632() { checkNotSubtype("{int f2,void f3}","{[null] f1}"); }
	@Test public void test_5633() { checkNotSubtype("{int f2,void f3}","{[null] f2}"); }
	@Test public void test_5634() { checkNotSubtype("{int f2,void f3}","{[null] f1,null f2}"); }
	@Test public void test_5635() { checkNotSubtype("{int f2,void f3}","{[null] f2,null f3}"); }
	@Test public void test_5636() { checkNotSubtype("{int f2,void f3}","{[int] f1}"); }
	@Test public void test_5637() { checkNotSubtype("{int f2,void f3}","{[int] f2}"); }
	@Test public void test_5638() { checkNotSubtype("{int f2,void f3}","{[int] f1,int f2}"); }
	@Test public void test_5639() { checkNotSubtype("{int f2,void f3}","{[int] f2,int f3}"); }
	@Test public void test_5640() { checkIsSubtype("{int f2,void f3}","{{void f1} f1}"); }
	@Test public void test_5641() { checkIsSubtype("{int f2,void f3}","{{void f2} f1}"); }
	@Test public void test_5642() { checkIsSubtype("{int f2,void f3}","{{void f1} f2}"); }
	@Test public void test_5643() { checkIsSubtype("{int f2,void f3}","{{void f2} f2}"); }
	@Test public void test_5644() { checkIsSubtype("{int f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_5645() { checkIsSubtype("{int f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_5646() { checkIsSubtype("{int f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_5647() { checkIsSubtype("{int f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_5648() { checkNotSubtype("{int f2,void f3}","{{any f1} f1}"); }
	@Test public void test_5649() { checkNotSubtype("{int f2,void f3}","{{any f2} f1}"); }
	@Test public void test_5650() { checkNotSubtype("{int f2,void f3}","{{any f1} f2}"); }
	@Test public void test_5651() { checkNotSubtype("{int f2,void f3}","{{any f2} f2}"); }
	@Test public void test_5652() { checkNotSubtype("{int f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_5653() { checkNotSubtype("{int f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_5654() { checkNotSubtype("{int f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_5655() { checkNotSubtype("{int f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_5656() { checkNotSubtype("{int f2,void f3}","{{null f1} f1}"); }
	@Test public void test_5657() { checkNotSubtype("{int f2,void f3}","{{null f2} f1}"); }
	@Test public void test_5658() { checkNotSubtype("{int f2,void f3}","{{null f1} f2}"); }
	@Test public void test_5659() { checkNotSubtype("{int f2,void f3}","{{null f2} f2}"); }
	@Test public void test_5660() { checkNotSubtype("{int f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_5661() { checkNotSubtype("{int f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_5662() { checkNotSubtype("{int f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_5663() { checkNotSubtype("{int f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_5664() { checkNotSubtype("{int f2,void f3}","{{int f1} f1}"); }
	@Test public void test_5665() { checkNotSubtype("{int f2,void f3}","{{int f2} f1}"); }
	@Test public void test_5666() { checkNotSubtype("{int f2,void f3}","{{int f1} f2}"); }
	@Test public void test_5667() { checkNotSubtype("{int f2,void f3}","{{int f2} f2}"); }
	@Test public void test_5668() { checkNotSubtype("{int f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_5669() { checkNotSubtype("{int f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_5670() { checkNotSubtype("{int f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_5671() { checkNotSubtype("{int f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_5672() { checkNotSubtype("{int f1,any f2}","any"); }
	@Test public void test_5673() { checkNotSubtype("{int f1,any f2}","null"); }
	@Test public void test_5674() { checkNotSubtype("{int f1,any f2}","int"); }
	@Test public void test_5675() { checkNotSubtype("{int f1,any f2}","[void]"); }
	@Test public void test_5676() { checkNotSubtype("{int f1,any f2}","[any]"); }
	@Test public void test_5677() { checkNotSubtype("{int f1,any f2}","[null]"); }
	@Test public void test_5678() { checkNotSubtype("{int f1,any f2}","[int]"); }
	@Test public void test_5679() { checkIsSubtype("{int f1,any f2}","{void f1}"); }
	@Test public void test_5680() { checkIsSubtype("{int f1,any f2}","{void f2}"); }
	@Test public void test_5681() { checkNotSubtype("{int f1,any f2}","{any f1}"); }
	@Test public void test_5682() { checkNotSubtype("{int f1,any f2}","{any f2}"); }
	@Test public void test_5683() { checkNotSubtype("{int f1,any f2}","{null f1}"); }
	@Test public void test_5684() { checkNotSubtype("{int f1,any f2}","{null f2}"); }
	@Test public void test_5685() { checkNotSubtype("{int f1,any f2}","{int f1}"); }
	@Test public void test_5686() { checkNotSubtype("{int f1,any f2}","{int f2}"); }
	@Test public void test_5687() { checkNotSubtype("{int f1,any f2}","[[void]]"); }
	@Test public void test_5688() { checkNotSubtype("{int f1,any f2}","[[any]]"); }
	@Test public void test_5689() { checkNotSubtype("{int f1,any f2}","[[null]]"); }
	@Test public void test_5690() { checkNotSubtype("{int f1,any f2}","[[int]]"); }
	@Test public void test_5691() { checkNotSubtype("{int f1,any f2}","[{void f1}]"); }
	@Test public void test_5692() { checkNotSubtype("{int f1,any f2}","[{void f2}]"); }
	@Test public void test_5693() { checkNotSubtype("{int f1,any f2}","[{any f1}]"); }
	@Test public void test_5694() { checkNotSubtype("{int f1,any f2}","[{any f2}]"); }
	@Test public void test_5695() { checkNotSubtype("{int f1,any f2}","[{null f1}]"); }
	@Test public void test_5696() { checkNotSubtype("{int f1,any f2}","[{null f2}]"); }
	@Test public void test_5697() { checkNotSubtype("{int f1,any f2}","[{int f1}]"); }
	@Test public void test_5698() { checkNotSubtype("{int f1,any f2}","[{int f2}]"); }
	@Test public void test_5699() { checkIsSubtype("{int f1,any f2}","{void f1,void f2}"); }
	@Test public void test_5700() { checkIsSubtype("{int f1,any f2}","{void f2,void f3}"); }
	@Test public void test_5701() { checkIsSubtype("{int f1,any f2}","{void f1,any f2}"); }
	@Test public void test_5702() { checkIsSubtype("{int f1,any f2}","{void f2,any f3}"); }
	@Test public void test_5703() { checkIsSubtype("{int f1,any f2}","{void f1,null f2}"); }
	@Test public void test_5704() { checkIsSubtype("{int f1,any f2}","{void f2,null f3}"); }
	@Test public void test_5705() { checkIsSubtype("{int f1,any f2}","{void f1,int f2}"); }
	@Test public void test_5706() { checkIsSubtype("{int f1,any f2}","{void f2,int f3}"); }
	@Test public void test_5707() { checkIsSubtype("{int f1,any f2}","{any f1,void f2}"); }
	@Test public void test_5708() { checkIsSubtype("{int f1,any f2}","{any f2,void f3}"); }
	@Test public void test_5709() { checkNotSubtype("{int f1,any f2}","{any f1,any f2}"); }
	@Test public void test_5710() { checkNotSubtype("{int f1,any f2}","{any f2,any f3}"); }
	@Test public void test_5711() { checkNotSubtype("{int f1,any f2}","{any f1,null f2}"); }
	@Test public void test_5712() { checkNotSubtype("{int f1,any f2}","{any f2,null f3}"); }
	@Test public void test_5713() { checkNotSubtype("{int f1,any f2}","{any f1,int f2}"); }
	@Test public void test_5714() { checkNotSubtype("{int f1,any f2}","{any f2,int f3}"); }
	@Test public void test_5715() { checkIsSubtype("{int f1,any f2}","{null f1,void f2}"); }
	@Test public void test_5716() { checkIsSubtype("{int f1,any f2}","{null f2,void f3}"); }
	@Test public void test_5717() { checkNotSubtype("{int f1,any f2}","{null f1,any f2}"); }
	@Test public void test_5718() { checkNotSubtype("{int f1,any f2}","{null f2,any f3}"); }
	@Test public void test_5719() { checkNotSubtype("{int f1,any f2}","{null f1,null f2}"); }
	@Test public void test_5720() { checkNotSubtype("{int f1,any f2}","{null f2,null f3}"); }
	@Test public void test_5721() { checkNotSubtype("{int f1,any f2}","{null f1,int f2}"); }
	@Test public void test_5722() { checkNotSubtype("{int f1,any f2}","{null f2,int f3}"); }
	@Test public void test_5723() { checkIsSubtype("{int f1,any f2}","{int f1,void f2}"); }
	@Test public void test_5724() { checkIsSubtype("{int f1,any f2}","{int f2,void f3}"); }
	@Test public void test_5725() { checkIsSubtype("{int f1,any f2}","{int f1,any f2}"); }
	@Test public void test_5726() { checkNotSubtype("{int f1,any f2}","{int f2,any f3}"); }
	@Test public void test_5727() { checkIsSubtype("{int f1,any f2}","{int f1,null f2}"); }
	@Test public void test_5728() { checkNotSubtype("{int f1,any f2}","{int f2,null f3}"); }
	@Test public void test_5729() { checkIsSubtype("{int f1,any f2}","{int f1,int f2}"); }
	@Test public void test_5730() { checkNotSubtype("{int f1,any f2}","{int f2,int f3}"); }
	@Test public void test_5731() { checkNotSubtype("{int f1,any f2}","{[void] f1}"); }
	@Test public void test_5732() { checkNotSubtype("{int f1,any f2}","{[void] f2}"); }
	@Test public void test_5733() { checkIsSubtype("{int f1,any f2}","{[void] f1,void f2}"); }
	@Test public void test_5734() { checkIsSubtype("{int f1,any f2}","{[void] f2,void f3}"); }
	@Test public void test_5735() { checkNotSubtype("{int f1,any f2}","{[any] f1}"); }
	@Test public void test_5736() { checkNotSubtype("{int f1,any f2}","{[any] f2}"); }
	@Test public void test_5737() { checkNotSubtype("{int f1,any f2}","{[any] f1,any f2}"); }
	@Test public void test_5738() { checkNotSubtype("{int f1,any f2}","{[any] f2,any f3}"); }
	@Test public void test_5739() { checkNotSubtype("{int f1,any f2}","{[null] f1}"); }
	@Test public void test_5740() { checkNotSubtype("{int f1,any f2}","{[null] f2}"); }
	@Test public void test_5741() { checkNotSubtype("{int f1,any f2}","{[null] f1,null f2}"); }
	@Test public void test_5742() { checkNotSubtype("{int f1,any f2}","{[null] f2,null f3}"); }
	@Test public void test_5743() { checkNotSubtype("{int f1,any f2}","{[int] f1}"); }
	@Test public void test_5744() { checkNotSubtype("{int f1,any f2}","{[int] f2}"); }
	@Test public void test_5745() { checkNotSubtype("{int f1,any f2}","{[int] f1,int f2}"); }
	@Test public void test_5746() { checkNotSubtype("{int f1,any f2}","{[int] f2,int f3}"); }
	@Test public void test_5747() { checkIsSubtype("{int f1,any f2}","{{void f1} f1}"); }
	@Test public void test_5748() { checkIsSubtype("{int f1,any f2}","{{void f2} f1}"); }
	@Test public void test_5749() { checkIsSubtype("{int f1,any f2}","{{void f1} f2}"); }
	@Test public void test_5750() { checkIsSubtype("{int f1,any f2}","{{void f2} f2}"); }
	@Test public void test_5751() { checkIsSubtype("{int f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_5752() { checkIsSubtype("{int f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_5753() { checkIsSubtype("{int f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_5754() { checkIsSubtype("{int f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_5755() { checkNotSubtype("{int f1,any f2}","{{any f1} f1}"); }
	@Test public void test_5756() { checkNotSubtype("{int f1,any f2}","{{any f2} f1}"); }
	@Test public void test_5757() { checkNotSubtype("{int f1,any f2}","{{any f1} f2}"); }
	@Test public void test_5758() { checkNotSubtype("{int f1,any f2}","{{any f2} f2}"); }
	@Test public void test_5759() { checkNotSubtype("{int f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_5760() { checkNotSubtype("{int f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_5761() { checkNotSubtype("{int f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_5762() { checkNotSubtype("{int f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_5763() { checkNotSubtype("{int f1,any f2}","{{null f1} f1}"); }
	@Test public void test_5764() { checkNotSubtype("{int f1,any f2}","{{null f2} f1}"); }
	@Test public void test_5765() { checkNotSubtype("{int f1,any f2}","{{null f1} f2}"); }
	@Test public void test_5766() { checkNotSubtype("{int f1,any f2}","{{null f2} f2}"); }
	@Test public void test_5767() { checkNotSubtype("{int f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_5768() { checkNotSubtype("{int f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_5769() { checkNotSubtype("{int f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_5770() { checkNotSubtype("{int f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_5771() { checkNotSubtype("{int f1,any f2}","{{int f1} f1}"); }
	@Test public void test_5772() { checkNotSubtype("{int f1,any f2}","{{int f2} f1}"); }
	@Test public void test_5773() { checkNotSubtype("{int f1,any f2}","{{int f1} f2}"); }
	@Test public void test_5774() { checkNotSubtype("{int f1,any f2}","{{int f2} f2}"); }
	@Test public void test_5775() { checkNotSubtype("{int f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_5776() { checkNotSubtype("{int f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_5777() { checkNotSubtype("{int f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_5778() { checkNotSubtype("{int f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_5779() { checkNotSubtype("{int f2,any f3}","any"); }
	@Test public void test_5780() { checkNotSubtype("{int f2,any f3}","null"); }
	@Test public void test_5781() { checkNotSubtype("{int f2,any f3}","int"); }
	@Test public void test_5782() { checkNotSubtype("{int f2,any f3}","[void]"); }
	@Test public void test_5783() { checkNotSubtype("{int f2,any f3}","[any]"); }
	@Test public void test_5784() { checkNotSubtype("{int f2,any f3}","[null]"); }
	@Test public void test_5785() { checkNotSubtype("{int f2,any f3}","[int]"); }
	@Test public void test_5786() { checkIsSubtype("{int f2,any f3}","{void f1}"); }
	@Test public void test_5787() { checkIsSubtype("{int f2,any f3}","{void f2}"); }
	@Test public void test_5788() { checkNotSubtype("{int f2,any f3}","{any f1}"); }
	@Test public void test_5789() { checkNotSubtype("{int f2,any f3}","{any f2}"); }
	@Test public void test_5790() { checkNotSubtype("{int f2,any f3}","{null f1}"); }
	@Test public void test_5791() { checkNotSubtype("{int f2,any f3}","{null f2}"); }
	@Test public void test_5792() { checkNotSubtype("{int f2,any f3}","{int f1}"); }
	@Test public void test_5793() { checkNotSubtype("{int f2,any f3}","{int f2}"); }
	@Test public void test_5794() { checkNotSubtype("{int f2,any f3}","[[void]]"); }
	@Test public void test_5795() { checkNotSubtype("{int f2,any f3}","[[any]]"); }
	@Test public void test_5796() { checkNotSubtype("{int f2,any f3}","[[null]]"); }
	@Test public void test_5797() { checkNotSubtype("{int f2,any f3}","[[int]]"); }
	@Test public void test_5798() { checkNotSubtype("{int f2,any f3}","[{void f1}]"); }
	@Test public void test_5799() { checkNotSubtype("{int f2,any f3}","[{void f2}]"); }
	@Test public void test_5800() { checkNotSubtype("{int f2,any f3}","[{any f1}]"); }
	@Test public void test_5801() { checkNotSubtype("{int f2,any f3}","[{any f2}]"); }
	@Test public void test_5802() { checkNotSubtype("{int f2,any f3}","[{null f1}]"); }
	@Test public void test_5803() { checkNotSubtype("{int f2,any f3}","[{null f2}]"); }
	@Test public void test_5804() { checkNotSubtype("{int f2,any f3}","[{int f1}]"); }
	@Test public void test_5805() { checkNotSubtype("{int f2,any f3}","[{int f2}]"); }
	@Test public void test_5806() { checkIsSubtype("{int f2,any f3}","{void f1,void f2}"); }
	@Test public void test_5807() { checkIsSubtype("{int f2,any f3}","{void f2,void f3}"); }
	@Test public void test_5808() { checkIsSubtype("{int f2,any f3}","{void f1,any f2}"); }
	@Test public void test_5809() { checkIsSubtype("{int f2,any f3}","{void f2,any f3}"); }
	@Test public void test_5810() { checkIsSubtype("{int f2,any f3}","{void f1,null f2}"); }
	@Test public void test_5811() { checkIsSubtype("{int f2,any f3}","{void f2,null f3}"); }
	@Test public void test_5812() { checkIsSubtype("{int f2,any f3}","{void f1,int f2}"); }
	@Test public void test_5813() { checkIsSubtype("{int f2,any f3}","{void f2,int f3}"); }
	@Test public void test_5814() { checkIsSubtype("{int f2,any f3}","{any f1,void f2}"); }
	@Test public void test_5815() { checkIsSubtype("{int f2,any f3}","{any f2,void f3}"); }
	@Test public void test_5816() { checkNotSubtype("{int f2,any f3}","{any f1,any f2}"); }
	@Test public void test_5817() { checkNotSubtype("{int f2,any f3}","{any f2,any f3}"); }
	@Test public void test_5818() { checkNotSubtype("{int f2,any f3}","{any f1,null f2}"); }
	@Test public void test_5819() { checkNotSubtype("{int f2,any f3}","{any f2,null f3}"); }
	@Test public void test_5820() { checkNotSubtype("{int f2,any f3}","{any f1,int f2}"); }
	@Test public void test_5821() { checkNotSubtype("{int f2,any f3}","{any f2,int f3}"); }
	@Test public void test_5822() { checkIsSubtype("{int f2,any f3}","{null f1,void f2}"); }
	@Test public void test_5823() { checkIsSubtype("{int f2,any f3}","{null f2,void f3}"); }
	@Test public void test_5824() { checkNotSubtype("{int f2,any f3}","{null f1,any f2}"); }
	@Test public void test_5825() { checkNotSubtype("{int f2,any f3}","{null f2,any f3}"); }
	@Test public void test_5826() { checkNotSubtype("{int f2,any f3}","{null f1,null f2}"); }
	@Test public void test_5827() { checkNotSubtype("{int f2,any f3}","{null f2,null f3}"); }
	@Test public void test_5828() { checkNotSubtype("{int f2,any f3}","{null f1,int f2}"); }
	@Test public void test_5829() { checkNotSubtype("{int f2,any f3}","{null f2,int f3}"); }
	@Test public void test_5830() { checkIsSubtype("{int f2,any f3}","{int f1,void f2}"); }
	@Test public void test_5831() { checkIsSubtype("{int f2,any f3}","{int f2,void f3}"); }
	@Test public void test_5832() { checkNotSubtype("{int f2,any f3}","{int f1,any f2}"); }
	@Test public void test_5833() { checkIsSubtype("{int f2,any f3}","{int f2,any f3}"); }
	@Test public void test_5834() { checkNotSubtype("{int f2,any f3}","{int f1,null f2}"); }
	@Test public void test_5835() { checkIsSubtype("{int f2,any f3}","{int f2,null f3}"); }
	@Test public void test_5836() { checkNotSubtype("{int f2,any f3}","{int f1,int f2}"); }
	@Test public void test_5837() { checkIsSubtype("{int f2,any f3}","{int f2,int f3}"); }
	@Test public void test_5838() { checkNotSubtype("{int f2,any f3}","{[void] f1}"); }
	@Test public void test_5839() { checkNotSubtype("{int f2,any f3}","{[void] f2}"); }
	@Test public void test_5840() { checkIsSubtype("{int f2,any f3}","{[void] f1,void f2}"); }
	@Test public void test_5841() { checkIsSubtype("{int f2,any f3}","{[void] f2,void f3}"); }
	@Test public void test_5842() { checkNotSubtype("{int f2,any f3}","{[any] f1}"); }
	@Test public void test_5843() { checkNotSubtype("{int f2,any f3}","{[any] f2}"); }
	@Test public void test_5844() { checkNotSubtype("{int f2,any f3}","{[any] f1,any f2}"); }
	@Test public void test_5845() { checkNotSubtype("{int f2,any f3}","{[any] f2,any f3}"); }
	@Test public void test_5846() { checkNotSubtype("{int f2,any f3}","{[null] f1}"); }
	@Test public void test_5847() { checkNotSubtype("{int f2,any f3}","{[null] f2}"); }
	@Test public void test_5848() { checkNotSubtype("{int f2,any f3}","{[null] f1,null f2}"); }
	@Test public void test_5849() { checkNotSubtype("{int f2,any f3}","{[null] f2,null f3}"); }
	@Test public void test_5850() { checkNotSubtype("{int f2,any f3}","{[int] f1}"); }
	@Test public void test_5851() { checkNotSubtype("{int f2,any f3}","{[int] f2}"); }
	@Test public void test_5852() { checkNotSubtype("{int f2,any f3}","{[int] f1,int f2}"); }
	@Test public void test_5853() { checkNotSubtype("{int f2,any f3}","{[int] f2,int f3}"); }
	@Test public void test_5854() { checkIsSubtype("{int f2,any f3}","{{void f1} f1}"); }
	@Test public void test_5855() { checkIsSubtype("{int f2,any f3}","{{void f2} f1}"); }
	@Test public void test_5856() { checkIsSubtype("{int f2,any f3}","{{void f1} f2}"); }
	@Test public void test_5857() { checkIsSubtype("{int f2,any f3}","{{void f2} f2}"); }
	@Test public void test_5858() { checkIsSubtype("{int f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_5859() { checkIsSubtype("{int f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_5860() { checkIsSubtype("{int f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_5861() { checkIsSubtype("{int f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_5862() { checkNotSubtype("{int f2,any f3}","{{any f1} f1}"); }
	@Test public void test_5863() { checkNotSubtype("{int f2,any f3}","{{any f2} f1}"); }
	@Test public void test_5864() { checkNotSubtype("{int f2,any f3}","{{any f1} f2}"); }
	@Test public void test_5865() { checkNotSubtype("{int f2,any f3}","{{any f2} f2}"); }
	@Test public void test_5866() { checkNotSubtype("{int f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_5867() { checkNotSubtype("{int f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_5868() { checkNotSubtype("{int f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_5869() { checkNotSubtype("{int f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_5870() { checkNotSubtype("{int f2,any f3}","{{null f1} f1}"); }
	@Test public void test_5871() { checkNotSubtype("{int f2,any f3}","{{null f2} f1}"); }
	@Test public void test_5872() { checkNotSubtype("{int f2,any f3}","{{null f1} f2}"); }
	@Test public void test_5873() { checkNotSubtype("{int f2,any f3}","{{null f2} f2}"); }
	@Test public void test_5874() { checkNotSubtype("{int f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_5875() { checkNotSubtype("{int f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_5876() { checkNotSubtype("{int f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_5877() { checkNotSubtype("{int f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_5878() { checkNotSubtype("{int f2,any f3}","{{int f1} f1}"); }
	@Test public void test_5879() { checkNotSubtype("{int f2,any f3}","{{int f2} f1}"); }
	@Test public void test_5880() { checkNotSubtype("{int f2,any f3}","{{int f1} f2}"); }
	@Test public void test_5881() { checkNotSubtype("{int f2,any f3}","{{int f2} f2}"); }
	@Test public void test_5882() { checkNotSubtype("{int f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_5883() { checkNotSubtype("{int f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_5884() { checkNotSubtype("{int f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_5885() { checkNotSubtype("{int f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_5886() { checkNotSubtype("{int f1,null f2}","any"); }
	@Test public void test_5887() { checkNotSubtype("{int f1,null f2}","null"); }
	@Test public void test_5888() { checkNotSubtype("{int f1,null f2}","int"); }
	@Test public void test_5889() { checkNotSubtype("{int f1,null f2}","[void]"); }
	@Test public void test_5890() { checkNotSubtype("{int f1,null f2}","[any]"); }
	@Test public void test_5891() { checkNotSubtype("{int f1,null f2}","[null]"); }
	@Test public void test_5892() { checkNotSubtype("{int f1,null f2}","[int]"); }
	@Test public void test_5893() { checkIsSubtype("{int f1,null f2}","{void f1}"); }
	@Test public void test_5894() { checkIsSubtype("{int f1,null f2}","{void f2}"); }
	@Test public void test_5895() { checkNotSubtype("{int f1,null f2}","{any f1}"); }
	@Test public void test_5896() { checkNotSubtype("{int f1,null f2}","{any f2}"); }
	@Test public void test_5897() { checkNotSubtype("{int f1,null f2}","{null f1}"); }
	@Test public void test_5898() { checkNotSubtype("{int f1,null f2}","{null f2}"); }
	@Test public void test_5899() { checkNotSubtype("{int f1,null f2}","{int f1}"); }
	@Test public void test_5900() { checkNotSubtype("{int f1,null f2}","{int f2}"); }
	@Test public void test_5901() { checkNotSubtype("{int f1,null f2}","[[void]]"); }
	@Test public void test_5902() { checkNotSubtype("{int f1,null f2}","[[any]]"); }
	@Test public void test_5903() { checkNotSubtype("{int f1,null f2}","[[null]]"); }
	@Test public void test_5904() { checkNotSubtype("{int f1,null f2}","[[int]]"); }
	@Test public void test_5905() { checkNotSubtype("{int f1,null f2}","[{void f1}]"); }
	@Test public void test_5906() { checkNotSubtype("{int f1,null f2}","[{void f2}]"); }
	@Test public void test_5907() { checkNotSubtype("{int f1,null f2}","[{any f1}]"); }
	@Test public void test_5908() { checkNotSubtype("{int f1,null f2}","[{any f2}]"); }
	@Test public void test_5909() { checkNotSubtype("{int f1,null f2}","[{null f1}]"); }
	@Test public void test_5910() { checkNotSubtype("{int f1,null f2}","[{null f2}]"); }
	@Test public void test_5911() { checkNotSubtype("{int f1,null f2}","[{int f1}]"); }
	@Test public void test_5912() { checkNotSubtype("{int f1,null f2}","[{int f2}]"); }
	@Test public void test_5913() { checkIsSubtype("{int f1,null f2}","{void f1,void f2}"); }
	@Test public void test_5914() { checkIsSubtype("{int f1,null f2}","{void f2,void f3}"); }
	@Test public void test_5915() { checkIsSubtype("{int f1,null f2}","{void f1,any f2}"); }
	@Test public void test_5916() { checkIsSubtype("{int f1,null f2}","{void f2,any f3}"); }
	@Test public void test_5917() { checkIsSubtype("{int f1,null f2}","{void f1,null f2}"); }
	@Test public void test_5918() { checkIsSubtype("{int f1,null f2}","{void f2,null f3}"); }
	@Test public void test_5919() { checkIsSubtype("{int f1,null f2}","{void f1,int f2}"); }
	@Test public void test_5920() { checkIsSubtype("{int f1,null f2}","{void f2,int f3}"); }
	@Test public void test_5921() { checkIsSubtype("{int f1,null f2}","{any f1,void f2}"); }
	@Test public void test_5922() { checkIsSubtype("{int f1,null f2}","{any f2,void f3}"); }
	@Test public void test_5923() { checkNotSubtype("{int f1,null f2}","{any f1,any f2}"); }
	@Test public void test_5924() { checkNotSubtype("{int f1,null f2}","{any f2,any f3}"); }
	@Test public void test_5925() { checkNotSubtype("{int f1,null f2}","{any f1,null f2}"); }
	@Test public void test_5926() { checkNotSubtype("{int f1,null f2}","{any f2,null f3}"); }
	@Test public void test_5927() { checkNotSubtype("{int f1,null f2}","{any f1,int f2}"); }
	@Test public void test_5928() { checkNotSubtype("{int f1,null f2}","{any f2,int f3}"); }
	@Test public void test_5929() { checkIsSubtype("{int f1,null f2}","{null f1,void f2}"); }
	@Test public void test_5930() { checkIsSubtype("{int f1,null f2}","{null f2,void f3}"); }
	@Test public void test_5931() { checkNotSubtype("{int f1,null f2}","{null f1,any f2}"); }
	@Test public void test_5932() { checkNotSubtype("{int f1,null f2}","{null f2,any f3}"); }
	@Test public void test_5933() { checkNotSubtype("{int f1,null f2}","{null f1,null f2}"); }
	@Test public void test_5934() { checkNotSubtype("{int f1,null f2}","{null f2,null f3}"); }
	@Test public void test_5935() { checkNotSubtype("{int f1,null f2}","{null f1,int f2}"); }
	@Test public void test_5936() { checkNotSubtype("{int f1,null f2}","{null f2,int f3}"); }
	@Test public void test_5937() { checkIsSubtype("{int f1,null f2}","{int f1,void f2}"); }
	@Test public void test_5938() { checkIsSubtype("{int f1,null f2}","{int f2,void f3}"); }
	@Test public void test_5939() { checkNotSubtype("{int f1,null f2}","{int f1,any f2}"); }
	@Test public void test_5940() { checkNotSubtype("{int f1,null f2}","{int f2,any f3}"); }
	@Test public void test_5941() { checkIsSubtype("{int f1,null f2}","{int f1,null f2}"); }
	@Test public void test_5942() { checkNotSubtype("{int f1,null f2}","{int f2,null f3}"); }
	@Test public void test_5943() { checkNotSubtype("{int f1,null f2}","{int f1,int f2}"); }
	@Test public void test_5944() { checkNotSubtype("{int f1,null f2}","{int f2,int f3}"); }
	@Test public void test_5945() { checkNotSubtype("{int f1,null f2}","{[void] f1}"); }
	@Test public void test_5946() { checkNotSubtype("{int f1,null f2}","{[void] f2}"); }
	@Test public void test_5947() { checkIsSubtype("{int f1,null f2}","{[void] f1,void f2}"); }
	@Test public void test_5948() { checkIsSubtype("{int f1,null f2}","{[void] f2,void f3}"); }
	@Test public void test_5949() { checkNotSubtype("{int f1,null f2}","{[any] f1}"); }
	@Test public void test_5950() { checkNotSubtype("{int f1,null f2}","{[any] f2}"); }
	@Test public void test_5951() { checkNotSubtype("{int f1,null f2}","{[any] f1,any f2}"); }
	@Test public void test_5952() { checkNotSubtype("{int f1,null f2}","{[any] f2,any f3}"); }
	@Test public void test_5953() { checkNotSubtype("{int f1,null f2}","{[null] f1}"); }
	@Test public void test_5954() { checkNotSubtype("{int f1,null f2}","{[null] f2}"); }
	@Test public void test_5955() { checkNotSubtype("{int f1,null f2}","{[null] f1,null f2}"); }
	@Test public void test_5956() { checkNotSubtype("{int f1,null f2}","{[null] f2,null f3}"); }
	@Test public void test_5957() { checkNotSubtype("{int f1,null f2}","{[int] f1}"); }
	@Test public void test_5958() { checkNotSubtype("{int f1,null f2}","{[int] f2}"); }
	@Test public void test_5959() { checkNotSubtype("{int f1,null f2}","{[int] f1,int f2}"); }
	@Test public void test_5960() { checkNotSubtype("{int f1,null f2}","{[int] f2,int f3}"); }
	@Test public void test_5961() { checkIsSubtype("{int f1,null f2}","{{void f1} f1}"); }
	@Test public void test_5962() { checkIsSubtype("{int f1,null f2}","{{void f2} f1}"); }
	@Test public void test_5963() { checkIsSubtype("{int f1,null f2}","{{void f1} f2}"); }
	@Test public void test_5964() { checkIsSubtype("{int f1,null f2}","{{void f2} f2}"); }
	@Test public void test_5965() { checkIsSubtype("{int f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_5966() { checkIsSubtype("{int f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_5967() { checkIsSubtype("{int f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_5968() { checkIsSubtype("{int f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_5969() { checkNotSubtype("{int f1,null f2}","{{any f1} f1}"); }
	@Test public void test_5970() { checkNotSubtype("{int f1,null f2}","{{any f2} f1}"); }
	@Test public void test_5971() { checkNotSubtype("{int f1,null f2}","{{any f1} f2}"); }
	@Test public void test_5972() { checkNotSubtype("{int f1,null f2}","{{any f2} f2}"); }
	@Test public void test_5973() { checkNotSubtype("{int f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_5974() { checkNotSubtype("{int f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_5975() { checkNotSubtype("{int f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_5976() { checkNotSubtype("{int f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_5977() { checkNotSubtype("{int f1,null f2}","{{null f1} f1}"); }
	@Test public void test_5978() { checkNotSubtype("{int f1,null f2}","{{null f2} f1}"); }
	@Test public void test_5979() { checkNotSubtype("{int f1,null f2}","{{null f1} f2}"); }
	@Test public void test_5980() { checkNotSubtype("{int f1,null f2}","{{null f2} f2}"); }
	@Test public void test_5981() { checkNotSubtype("{int f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_5982() { checkNotSubtype("{int f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_5983() { checkNotSubtype("{int f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_5984() { checkNotSubtype("{int f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_5985() { checkNotSubtype("{int f1,null f2}","{{int f1} f1}"); }
	@Test public void test_5986() { checkNotSubtype("{int f1,null f2}","{{int f2} f1}"); }
	@Test public void test_5987() { checkNotSubtype("{int f1,null f2}","{{int f1} f2}"); }
	@Test public void test_5988() { checkNotSubtype("{int f1,null f2}","{{int f2} f2}"); }
	@Test public void test_5989() { checkNotSubtype("{int f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_5990() { checkNotSubtype("{int f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_5991() { checkNotSubtype("{int f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_5992() { checkNotSubtype("{int f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_5993() { checkNotSubtype("{int f2,null f3}","any"); }
	@Test public void test_5994() { checkNotSubtype("{int f2,null f3}","null"); }
	@Test public void test_5995() { checkNotSubtype("{int f2,null f3}","int"); }
	@Test public void test_5996() { checkNotSubtype("{int f2,null f3}","[void]"); }
	@Test public void test_5997() { checkNotSubtype("{int f2,null f3}","[any]"); }
	@Test public void test_5998() { checkNotSubtype("{int f2,null f3}","[null]"); }
	@Test public void test_5999() { checkNotSubtype("{int f2,null f3}","[int]"); }
	@Test public void test_6000() { checkIsSubtype("{int f2,null f3}","{void f1}"); }
	@Test public void test_6001() { checkIsSubtype("{int f2,null f3}","{void f2}"); }
	@Test public void test_6002() { checkNotSubtype("{int f2,null f3}","{any f1}"); }
	@Test public void test_6003() { checkNotSubtype("{int f2,null f3}","{any f2}"); }
	@Test public void test_6004() { checkNotSubtype("{int f2,null f3}","{null f1}"); }
	@Test public void test_6005() { checkNotSubtype("{int f2,null f3}","{null f2}"); }
	@Test public void test_6006() { checkNotSubtype("{int f2,null f3}","{int f1}"); }
	@Test public void test_6007() { checkNotSubtype("{int f2,null f3}","{int f2}"); }
	@Test public void test_6008() { checkNotSubtype("{int f2,null f3}","[[void]]"); }
	@Test public void test_6009() { checkNotSubtype("{int f2,null f3}","[[any]]"); }
	@Test public void test_6010() { checkNotSubtype("{int f2,null f3}","[[null]]"); }
	@Test public void test_6011() { checkNotSubtype("{int f2,null f3}","[[int]]"); }
	@Test public void test_6012() { checkNotSubtype("{int f2,null f3}","[{void f1}]"); }
	@Test public void test_6013() { checkNotSubtype("{int f2,null f3}","[{void f2}]"); }
	@Test public void test_6014() { checkNotSubtype("{int f2,null f3}","[{any f1}]"); }
	@Test public void test_6015() { checkNotSubtype("{int f2,null f3}","[{any f2}]"); }
	@Test public void test_6016() { checkNotSubtype("{int f2,null f3}","[{null f1}]"); }
	@Test public void test_6017() { checkNotSubtype("{int f2,null f3}","[{null f2}]"); }
	@Test public void test_6018() { checkNotSubtype("{int f2,null f3}","[{int f1}]"); }
	@Test public void test_6019() { checkNotSubtype("{int f2,null f3}","[{int f2}]"); }
	@Test public void test_6020() { checkIsSubtype("{int f2,null f3}","{void f1,void f2}"); }
	@Test public void test_6021() { checkIsSubtype("{int f2,null f3}","{void f2,void f3}"); }
	@Test public void test_6022() { checkIsSubtype("{int f2,null f3}","{void f1,any f2}"); }
	@Test public void test_6023() { checkIsSubtype("{int f2,null f3}","{void f2,any f3}"); }
	@Test public void test_6024() { checkIsSubtype("{int f2,null f3}","{void f1,null f2}"); }
	@Test public void test_6025() { checkIsSubtype("{int f2,null f3}","{void f2,null f3}"); }
	@Test public void test_6026() { checkIsSubtype("{int f2,null f3}","{void f1,int f2}"); }
	@Test public void test_6027() { checkIsSubtype("{int f2,null f3}","{void f2,int f3}"); }
	@Test public void test_6028() { checkIsSubtype("{int f2,null f3}","{any f1,void f2}"); }
	@Test public void test_6029() { checkIsSubtype("{int f2,null f3}","{any f2,void f3}"); }
	@Test public void test_6030() { checkNotSubtype("{int f2,null f3}","{any f1,any f2}"); }
	@Test public void test_6031() { checkNotSubtype("{int f2,null f3}","{any f2,any f3}"); }
	@Test public void test_6032() { checkNotSubtype("{int f2,null f3}","{any f1,null f2}"); }
	@Test public void test_6033() { checkNotSubtype("{int f2,null f3}","{any f2,null f3}"); }
	@Test public void test_6034() { checkNotSubtype("{int f2,null f3}","{any f1,int f2}"); }
	@Test public void test_6035() { checkNotSubtype("{int f2,null f3}","{any f2,int f3}"); }
	@Test public void test_6036() { checkIsSubtype("{int f2,null f3}","{null f1,void f2}"); }
	@Test public void test_6037() { checkIsSubtype("{int f2,null f3}","{null f2,void f3}"); }
	@Test public void test_6038() { checkNotSubtype("{int f2,null f3}","{null f1,any f2}"); }
	@Test public void test_6039() { checkNotSubtype("{int f2,null f3}","{null f2,any f3}"); }
	@Test public void test_6040() { checkNotSubtype("{int f2,null f3}","{null f1,null f2}"); }
	@Test public void test_6041() { checkNotSubtype("{int f2,null f3}","{null f2,null f3}"); }
	@Test public void test_6042() { checkNotSubtype("{int f2,null f3}","{null f1,int f2}"); }
	@Test public void test_6043() { checkNotSubtype("{int f2,null f3}","{null f2,int f3}"); }
	@Test public void test_6044() { checkIsSubtype("{int f2,null f3}","{int f1,void f2}"); }
	@Test public void test_6045() { checkIsSubtype("{int f2,null f3}","{int f2,void f3}"); }
	@Test public void test_6046() { checkNotSubtype("{int f2,null f3}","{int f1,any f2}"); }
	@Test public void test_6047() { checkNotSubtype("{int f2,null f3}","{int f2,any f3}"); }
	@Test public void test_6048() { checkNotSubtype("{int f2,null f3}","{int f1,null f2}"); }
	@Test public void test_6049() { checkIsSubtype("{int f2,null f3}","{int f2,null f3}"); }
	@Test public void test_6050() { checkNotSubtype("{int f2,null f3}","{int f1,int f2}"); }
	@Test public void test_6051() { checkNotSubtype("{int f2,null f3}","{int f2,int f3}"); }
	@Test public void test_6052() { checkNotSubtype("{int f2,null f3}","{[void] f1}"); }
	@Test public void test_6053() { checkNotSubtype("{int f2,null f3}","{[void] f2}"); }
	@Test public void test_6054() { checkIsSubtype("{int f2,null f3}","{[void] f1,void f2}"); }
	@Test public void test_6055() { checkIsSubtype("{int f2,null f3}","{[void] f2,void f3}"); }
	@Test public void test_6056() { checkNotSubtype("{int f2,null f3}","{[any] f1}"); }
	@Test public void test_6057() { checkNotSubtype("{int f2,null f3}","{[any] f2}"); }
	@Test public void test_6058() { checkNotSubtype("{int f2,null f3}","{[any] f1,any f2}"); }
	@Test public void test_6059() { checkNotSubtype("{int f2,null f3}","{[any] f2,any f3}"); }
	@Test public void test_6060() { checkNotSubtype("{int f2,null f3}","{[null] f1}"); }
	@Test public void test_6061() { checkNotSubtype("{int f2,null f3}","{[null] f2}"); }
	@Test public void test_6062() { checkNotSubtype("{int f2,null f3}","{[null] f1,null f2}"); }
	@Test public void test_6063() { checkNotSubtype("{int f2,null f3}","{[null] f2,null f3}"); }
	@Test public void test_6064() { checkNotSubtype("{int f2,null f3}","{[int] f1}"); }
	@Test public void test_6065() { checkNotSubtype("{int f2,null f3}","{[int] f2}"); }
	@Test public void test_6066() { checkNotSubtype("{int f2,null f3}","{[int] f1,int f2}"); }
	@Test public void test_6067() { checkNotSubtype("{int f2,null f3}","{[int] f2,int f3}"); }
	@Test public void test_6068() { checkIsSubtype("{int f2,null f3}","{{void f1} f1}"); }
	@Test public void test_6069() { checkIsSubtype("{int f2,null f3}","{{void f2} f1}"); }
	@Test public void test_6070() { checkIsSubtype("{int f2,null f3}","{{void f1} f2}"); }
	@Test public void test_6071() { checkIsSubtype("{int f2,null f3}","{{void f2} f2}"); }
	@Test public void test_6072() { checkIsSubtype("{int f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_6073() { checkIsSubtype("{int f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_6074() { checkIsSubtype("{int f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_6075() { checkIsSubtype("{int f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_6076() { checkNotSubtype("{int f2,null f3}","{{any f1} f1}"); }
	@Test public void test_6077() { checkNotSubtype("{int f2,null f3}","{{any f2} f1}"); }
	@Test public void test_6078() { checkNotSubtype("{int f2,null f3}","{{any f1} f2}"); }
	@Test public void test_6079() { checkNotSubtype("{int f2,null f3}","{{any f2} f2}"); }
	@Test public void test_6080() { checkNotSubtype("{int f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_6081() { checkNotSubtype("{int f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_6082() { checkNotSubtype("{int f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_6083() { checkNotSubtype("{int f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_6084() { checkNotSubtype("{int f2,null f3}","{{null f1} f1}"); }
	@Test public void test_6085() { checkNotSubtype("{int f2,null f3}","{{null f2} f1}"); }
	@Test public void test_6086() { checkNotSubtype("{int f2,null f3}","{{null f1} f2}"); }
	@Test public void test_6087() { checkNotSubtype("{int f2,null f3}","{{null f2} f2}"); }
	@Test public void test_6088() { checkNotSubtype("{int f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_6089() { checkNotSubtype("{int f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_6090() { checkNotSubtype("{int f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_6091() { checkNotSubtype("{int f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_6092() { checkNotSubtype("{int f2,null f3}","{{int f1} f1}"); }
	@Test public void test_6093() { checkNotSubtype("{int f2,null f3}","{{int f2} f1}"); }
	@Test public void test_6094() { checkNotSubtype("{int f2,null f3}","{{int f1} f2}"); }
	@Test public void test_6095() { checkNotSubtype("{int f2,null f3}","{{int f2} f2}"); }
	@Test public void test_6096() { checkNotSubtype("{int f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_6097() { checkNotSubtype("{int f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_6098() { checkNotSubtype("{int f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_6099() { checkNotSubtype("{int f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_6100() { checkNotSubtype("{int f1,int f2}","any"); }
	@Test public void test_6101() { checkNotSubtype("{int f1,int f2}","null"); }
	@Test public void test_6102() { checkNotSubtype("{int f1,int f2}","int"); }
	@Test public void test_6103() { checkNotSubtype("{int f1,int f2}","[void]"); }
	@Test public void test_6104() { checkNotSubtype("{int f1,int f2}","[any]"); }
	@Test public void test_6105() { checkNotSubtype("{int f1,int f2}","[null]"); }
	@Test public void test_6106() { checkNotSubtype("{int f1,int f2}","[int]"); }
	@Test public void test_6107() { checkIsSubtype("{int f1,int f2}","{void f1}"); }
	@Test public void test_6108() { checkIsSubtype("{int f1,int f2}","{void f2}"); }
	@Test public void test_6109() { checkNotSubtype("{int f1,int f2}","{any f1}"); }
	@Test public void test_6110() { checkNotSubtype("{int f1,int f2}","{any f2}"); }
	@Test public void test_6111() { checkNotSubtype("{int f1,int f2}","{null f1}"); }
	@Test public void test_6112() { checkNotSubtype("{int f1,int f2}","{null f2}"); }
	@Test public void test_6113() { checkNotSubtype("{int f1,int f2}","{int f1}"); }
	@Test public void test_6114() { checkNotSubtype("{int f1,int f2}","{int f2}"); }
	@Test public void test_6115() { checkNotSubtype("{int f1,int f2}","[[void]]"); }
	@Test public void test_6116() { checkNotSubtype("{int f1,int f2}","[[any]]"); }
	@Test public void test_6117() { checkNotSubtype("{int f1,int f2}","[[null]]"); }
	@Test public void test_6118() { checkNotSubtype("{int f1,int f2}","[[int]]"); }
	@Test public void test_6119() { checkNotSubtype("{int f1,int f2}","[{void f1}]"); }
	@Test public void test_6120() { checkNotSubtype("{int f1,int f2}","[{void f2}]"); }
	@Test public void test_6121() { checkNotSubtype("{int f1,int f2}","[{any f1}]"); }
	@Test public void test_6122() { checkNotSubtype("{int f1,int f2}","[{any f2}]"); }
	@Test public void test_6123() { checkNotSubtype("{int f1,int f2}","[{null f1}]"); }
	@Test public void test_6124() { checkNotSubtype("{int f1,int f2}","[{null f2}]"); }
	@Test public void test_6125() { checkNotSubtype("{int f1,int f2}","[{int f1}]"); }
	@Test public void test_6126() { checkNotSubtype("{int f1,int f2}","[{int f2}]"); }
	@Test public void test_6127() { checkIsSubtype("{int f1,int f2}","{void f1,void f2}"); }
	@Test public void test_6128() { checkIsSubtype("{int f1,int f2}","{void f2,void f3}"); }
	@Test public void test_6129() { checkIsSubtype("{int f1,int f2}","{void f1,any f2}"); }
	@Test public void test_6130() { checkIsSubtype("{int f1,int f2}","{void f2,any f3}"); }
	@Test public void test_6131() { checkIsSubtype("{int f1,int f2}","{void f1,null f2}"); }
	@Test public void test_6132() { checkIsSubtype("{int f1,int f2}","{void f2,null f3}"); }
	@Test public void test_6133() { checkIsSubtype("{int f1,int f2}","{void f1,int f2}"); }
	@Test public void test_6134() { checkIsSubtype("{int f1,int f2}","{void f2,int f3}"); }
	@Test public void test_6135() { checkIsSubtype("{int f1,int f2}","{any f1,void f2}"); }
	@Test public void test_6136() { checkIsSubtype("{int f1,int f2}","{any f2,void f3}"); }
	@Test public void test_6137() { checkNotSubtype("{int f1,int f2}","{any f1,any f2}"); }
	@Test public void test_6138() { checkNotSubtype("{int f1,int f2}","{any f2,any f3}"); }
	@Test public void test_6139() { checkNotSubtype("{int f1,int f2}","{any f1,null f2}"); }
	@Test public void test_6140() { checkNotSubtype("{int f1,int f2}","{any f2,null f3}"); }
	@Test public void test_6141() { checkNotSubtype("{int f1,int f2}","{any f1,int f2}"); }
	@Test public void test_6142() { checkNotSubtype("{int f1,int f2}","{any f2,int f3}"); }
	@Test public void test_6143() { checkIsSubtype("{int f1,int f2}","{null f1,void f2}"); }
	@Test public void test_6144() { checkIsSubtype("{int f1,int f2}","{null f2,void f3}"); }
	@Test public void test_6145() { checkNotSubtype("{int f1,int f2}","{null f1,any f2}"); }
	@Test public void test_6146() { checkNotSubtype("{int f1,int f2}","{null f2,any f3}"); }
	@Test public void test_6147() { checkNotSubtype("{int f1,int f2}","{null f1,null f2}"); }
	@Test public void test_6148() { checkNotSubtype("{int f1,int f2}","{null f2,null f3}"); }
	@Test public void test_6149() { checkNotSubtype("{int f1,int f2}","{null f1,int f2}"); }
	@Test public void test_6150() { checkNotSubtype("{int f1,int f2}","{null f2,int f3}"); }
	@Test public void test_6151() { checkIsSubtype("{int f1,int f2}","{int f1,void f2}"); }
	@Test public void test_6152() { checkIsSubtype("{int f1,int f2}","{int f2,void f3}"); }
	@Test public void test_6153() { checkNotSubtype("{int f1,int f2}","{int f1,any f2}"); }
	@Test public void test_6154() { checkNotSubtype("{int f1,int f2}","{int f2,any f3}"); }
	@Test public void test_6155() { checkNotSubtype("{int f1,int f2}","{int f1,null f2}"); }
	@Test public void test_6156() { checkNotSubtype("{int f1,int f2}","{int f2,null f3}"); }
	@Test public void test_6157() { checkIsSubtype("{int f1,int f2}","{int f1,int f2}"); }
	@Test public void test_6158() { checkNotSubtype("{int f1,int f2}","{int f2,int f3}"); }
	@Test public void test_6159() { checkNotSubtype("{int f1,int f2}","{[void] f1}"); }
	@Test public void test_6160() { checkNotSubtype("{int f1,int f2}","{[void] f2}"); }
	@Test public void test_6161() { checkIsSubtype("{int f1,int f2}","{[void] f1,void f2}"); }
	@Test public void test_6162() { checkIsSubtype("{int f1,int f2}","{[void] f2,void f3}"); }
	@Test public void test_6163() { checkNotSubtype("{int f1,int f2}","{[any] f1}"); }
	@Test public void test_6164() { checkNotSubtype("{int f1,int f2}","{[any] f2}"); }
	@Test public void test_6165() { checkNotSubtype("{int f1,int f2}","{[any] f1,any f2}"); }
	@Test public void test_6166() { checkNotSubtype("{int f1,int f2}","{[any] f2,any f3}"); }
	@Test public void test_6167() { checkNotSubtype("{int f1,int f2}","{[null] f1}"); }
	@Test public void test_6168() { checkNotSubtype("{int f1,int f2}","{[null] f2}"); }
	@Test public void test_6169() { checkNotSubtype("{int f1,int f2}","{[null] f1,null f2}"); }
	@Test public void test_6170() { checkNotSubtype("{int f1,int f2}","{[null] f2,null f3}"); }
	@Test public void test_6171() { checkNotSubtype("{int f1,int f2}","{[int] f1}"); }
	@Test public void test_6172() { checkNotSubtype("{int f1,int f2}","{[int] f2}"); }
	@Test public void test_6173() { checkNotSubtype("{int f1,int f2}","{[int] f1,int f2}"); }
	@Test public void test_6174() { checkNotSubtype("{int f1,int f2}","{[int] f2,int f3}"); }
	@Test public void test_6175() { checkIsSubtype("{int f1,int f2}","{{void f1} f1}"); }
	@Test public void test_6176() { checkIsSubtype("{int f1,int f2}","{{void f2} f1}"); }
	@Test public void test_6177() { checkIsSubtype("{int f1,int f2}","{{void f1} f2}"); }
	@Test public void test_6178() { checkIsSubtype("{int f1,int f2}","{{void f2} f2}"); }
	@Test public void test_6179() { checkIsSubtype("{int f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_6180() { checkIsSubtype("{int f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_6181() { checkIsSubtype("{int f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_6182() { checkIsSubtype("{int f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_6183() { checkNotSubtype("{int f1,int f2}","{{any f1} f1}"); }
	@Test public void test_6184() { checkNotSubtype("{int f1,int f2}","{{any f2} f1}"); }
	@Test public void test_6185() { checkNotSubtype("{int f1,int f2}","{{any f1} f2}"); }
	@Test public void test_6186() { checkNotSubtype("{int f1,int f2}","{{any f2} f2}"); }
	@Test public void test_6187() { checkNotSubtype("{int f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_6188() { checkNotSubtype("{int f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_6189() { checkNotSubtype("{int f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_6190() { checkNotSubtype("{int f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_6191() { checkNotSubtype("{int f1,int f2}","{{null f1} f1}"); }
	@Test public void test_6192() { checkNotSubtype("{int f1,int f2}","{{null f2} f1}"); }
	@Test public void test_6193() { checkNotSubtype("{int f1,int f2}","{{null f1} f2}"); }
	@Test public void test_6194() { checkNotSubtype("{int f1,int f2}","{{null f2} f2}"); }
	@Test public void test_6195() { checkNotSubtype("{int f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_6196() { checkNotSubtype("{int f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_6197() { checkNotSubtype("{int f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_6198() { checkNotSubtype("{int f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_6199() { checkNotSubtype("{int f1,int f2}","{{int f1} f1}"); }
	@Test public void test_6200() { checkNotSubtype("{int f1,int f2}","{{int f2} f1}"); }
	@Test public void test_6201() { checkNotSubtype("{int f1,int f2}","{{int f1} f2}"); }
	@Test public void test_6202() { checkNotSubtype("{int f1,int f2}","{{int f2} f2}"); }
	@Test public void test_6203() { checkNotSubtype("{int f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_6204() { checkNotSubtype("{int f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_6205() { checkNotSubtype("{int f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_6206() { checkNotSubtype("{int f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_6207() { checkNotSubtype("{int f2,int f3}","any"); }
	@Test public void test_6208() { checkNotSubtype("{int f2,int f3}","null"); }
	@Test public void test_6209() { checkNotSubtype("{int f2,int f3}","int"); }
	@Test public void test_6210() { checkNotSubtype("{int f2,int f3}","[void]"); }
	@Test public void test_6211() { checkNotSubtype("{int f2,int f3}","[any]"); }
	@Test public void test_6212() { checkNotSubtype("{int f2,int f3}","[null]"); }
	@Test public void test_6213() { checkNotSubtype("{int f2,int f3}","[int]"); }
	@Test public void test_6214() { checkIsSubtype("{int f2,int f3}","{void f1}"); }
	@Test public void test_6215() { checkIsSubtype("{int f2,int f3}","{void f2}"); }
	@Test public void test_6216() { checkNotSubtype("{int f2,int f3}","{any f1}"); }
	@Test public void test_6217() { checkNotSubtype("{int f2,int f3}","{any f2}"); }
	@Test public void test_6218() { checkNotSubtype("{int f2,int f3}","{null f1}"); }
	@Test public void test_6219() { checkNotSubtype("{int f2,int f3}","{null f2}"); }
	@Test public void test_6220() { checkNotSubtype("{int f2,int f3}","{int f1}"); }
	@Test public void test_6221() { checkNotSubtype("{int f2,int f3}","{int f2}"); }
	@Test public void test_6222() { checkNotSubtype("{int f2,int f3}","[[void]]"); }
	@Test public void test_6223() { checkNotSubtype("{int f2,int f3}","[[any]]"); }
	@Test public void test_6224() { checkNotSubtype("{int f2,int f3}","[[null]]"); }
	@Test public void test_6225() { checkNotSubtype("{int f2,int f3}","[[int]]"); }
	@Test public void test_6226() { checkNotSubtype("{int f2,int f3}","[{void f1}]"); }
	@Test public void test_6227() { checkNotSubtype("{int f2,int f3}","[{void f2}]"); }
	@Test public void test_6228() { checkNotSubtype("{int f2,int f3}","[{any f1}]"); }
	@Test public void test_6229() { checkNotSubtype("{int f2,int f3}","[{any f2}]"); }
	@Test public void test_6230() { checkNotSubtype("{int f2,int f3}","[{null f1}]"); }
	@Test public void test_6231() { checkNotSubtype("{int f2,int f3}","[{null f2}]"); }
	@Test public void test_6232() { checkNotSubtype("{int f2,int f3}","[{int f1}]"); }
	@Test public void test_6233() { checkNotSubtype("{int f2,int f3}","[{int f2}]"); }
	@Test public void test_6234() { checkIsSubtype("{int f2,int f3}","{void f1,void f2}"); }
	@Test public void test_6235() { checkIsSubtype("{int f2,int f3}","{void f2,void f3}"); }
	@Test public void test_6236() { checkIsSubtype("{int f2,int f3}","{void f1,any f2}"); }
	@Test public void test_6237() { checkIsSubtype("{int f2,int f3}","{void f2,any f3}"); }
	@Test public void test_6238() { checkIsSubtype("{int f2,int f3}","{void f1,null f2}"); }
	@Test public void test_6239() { checkIsSubtype("{int f2,int f3}","{void f2,null f3}"); }
	@Test public void test_6240() { checkIsSubtype("{int f2,int f3}","{void f1,int f2}"); }
	@Test public void test_6241() { checkIsSubtype("{int f2,int f3}","{void f2,int f3}"); }
	@Test public void test_6242() { checkIsSubtype("{int f2,int f3}","{any f1,void f2}"); }
	@Test public void test_6243() { checkIsSubtype("{int f2,int f3}","{any f2,void f3}"); }
	@Test public void test_6244() { checkNotSubtype("{int f2,int f3}","{any f1,any f2}"); }
	@Test public void test_6245() { checkNotSubtype("{int f2,int f3}","{any f2,any f3}"); }
	@Test public void test_6246() { checkNotSubtype("{int f2,int f3}","{any f1,null f2}"); }
	@Test public void test_6247() { checkNotSubtype("{int f2,int f3}","{any f2,null f3}"); }
	@Test public void test_6248() { checkNotSubtype("{int f2,int f3}","{any f1,int f2}"); }
	@Test public void test_6249() { checkNotSubtype("{int f2,int f3}","{any f2,int f3}"); }
	@Test public void test_6250() { checkIsSubtype("{int f2,int f3}","{null f1,void f2}"); }
	@Test public void test_6251() { checkIsSubtype("{int f2,int f3}","{null f2,void f3}"); }
	@Test public void test_6252() { checkNotSubtype("{int f2,int f3}","{null f1,any f2}"); }
	@Test public void test_6253() { checkNotSubtype("{int f2,int f3}","{null f2,any f3}"); }
	@Test public void test_6254() { checkNotSubtype("{int f2,int f3}","{null f1,null f2}"); }
	@Test public void test_6255() { checkNotSubtype("{int f2,int f3}","{null f2,null f3}"); }
	@Test public void test_6256() { checkNotSubtype("{int f2,int f3}","{null f1,int f2}"); }
	@Test public void test_6257() { checkNotSubtype("{int f2,int f3}","{null f2,int f3}"); }
	@Test public void test_6258() { checkIsSubtype("{int f2,int f3}","{int f1,void f2}"); }
	@Test public void test_6259() { checkIsSubtype("{int f2,int f3}","{int f2,void f3}"); }
	@Test public void test_6260() { checkNotSubtype("{int f2,int f3}","{int f1,any f2}"); }
	@Test public void test_6261() { checkNotSubtype("{int f2,int f3}","{int f2,any f3}"); }
	@Test public void test_6262() { checkNotSubtype("{int f2,int f3}","{int f1,null f2}"); }
	@Test public void test_6263() { checkNotSubtype("{int f2,int f3}","{int f2,null f3}"); }
	@Test public void test_6264() { checkNotSubtype("{int f2,int f3}","{int f1,int f2}"); }
	@Test public void test_6265() { checkIsSubtype("{int f2,int f3}","{int f2,int f3}"); }
	@Test public void test_6266() { checkNotSubtype("{int f2,int f3}","{[void] f1}"); }
	@Test public void test_6267() { checkNotSubtype("{int f2,int f3}","{[void] f2}"); }
	@Test public void test_6268() { checkIsSubtype("{int f2,int f3}","{[void] f1,void f2}"); }
	@Test public void test_6269() { checkIsSubtype("{int f2,int f3}","{[void] f2,void f3}"); }
	@Test public void test_6270() { checkNotSubtype("{int f2,int f3}","{[any] f1}"); }
	@Test public void test_6271() { checkNotSubtype("{int f2,int f3}","{[any] f2}"); }
	@Test public void test_6272() { checkNotSubtype("{int f2,int f3}","{[any] f1,any f2}"); }
	@Test public void test_6273() { checkNotSubtype("{int f2,int f3}","{[any] f2,any f3}"); }
	@Test public void test_6274() { checkNotSubtype("{int f2,int f3}","{[null] f1}"); }
	@Test public void test_6275() { checkNotSubtype("{int f2,int f3}","{[null] f2}"); }
	@Test public void test_6276() { checkNotSubtype("{int f2,int f3}","{[null] f1,null f2}"); }
	@Test public void test_6277() { checkNotSubtype("{int f2,int f3}","{[null] f2,null f3}"); }
	@Test public void test_6278() { checkNotSubtype("{int f2,int f3}","{[int] f1}"); }
	@Test public void test_6279() { checkNotSubtype("{int f2,int f3}","{[int] f2}"); }
	@Test public void test_6280() { checkNotSubtype("{int f2,int f3}","{[int] f1,int f2}"); }
	@Test public void test_6281() { checkNotSubtype("{int f2,int f3}","{[int] f2,int f3}"); }
	@Test public void test_6282() { checkIsSubtype("{int f2,int f3}","{{void f1} f1}"); }
	@Test public void test_6283() { checkIsSubtype("{int f2,int f3}","{{void f2} f1}"); }
	@Test public void test_6284() { checkIsSubtype("{int f2,int f3}","{{void f1} f2}"); }
	@Test public void test_6285() { checkIsSubtype("{int f2,int f3}","{{void f2} f2}"); }
	@Test public void test_6286() { checkIsSubtype("{int f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_6287() { checkIsSubtype("{int f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_6288() { checkIsSubtype("{int f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_6289() { checkIsSubtype("{int f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_6290() { checkNotSubtype("{int f2,int f3}","{{any f1} f1}"); }
	@Test public void test_6291() { checkNotSubtype("{int f2,int f3}","{{any f2} f1}"); }
	@Test public void test_6292() { checkNotSubtype("{int f2,int f3}","{{any f1} f2}"); }
	@Test public void test_6293() { checkNotSubtype("{int f2,int f3}","{{any f2} f2}"); }
	@Test public void test_6294() { checkNotSubtype("{int f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_6295() { checkNotSubtype("{int f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_6296() { checkNotSubtype("{int f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_6297() { checkNotSubtype("{int f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_6298() { checkNotSubtype("{int f2,int f3}","{{null f1} f1}"); }
	@Test public void test_6299() { checkNotSubtype("{int f2,int f3}","{{null f2} f1}"); }
	@Test public void test_6300() { checkNotSubtype("{int f2,int f3}","{{null f1} f2}"); }
	@Test public void test_6301() { checkNotSubtype("{int f2,int f3}","{{null f2} f2}"); }
	@Test public void test_6302() { checkNotSubtype("{int f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_6303() { checkNotSubtype("{int f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_6304() { checkNotSubtype("{int f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_6305() { checkNotSubtype("{int f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_6306() { checkNotSubtype("{int f2,int f3}","{{int f1} f1}"); }
	@Test public void test_6307() { checkNotSubtype("{int f2,int f3}","{{int f2} f1}"); }
	@Test public void test_6308() { checkNotSubtype("{int f2,int f3}","{{int f1} f2}"); }
	@Test public void test_6309() { checkNotSubtype("{int f2,int f3}","{{int f2} f2}"); }
	@Test public void test_6310() { checkNotSubtype("{int f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_6311() { checkNotSubtype("{int f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_6312() { checkNotSubtype("{int f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_6313() { checkNotSubtype("{int f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_6314() { checkNotSubtype("{[void] f1}","any"); }
	@Test public void test_6315() { checkNotSubtype("{[void] f1}","null"); }
	@Test public void test_6316() { checkNotSubtype("{[void] f1}","int"); }
	@Test public void test_6317() { checkNotSubtype("{[void] f1}","[void]"); }
	@Test public void test_6318() { checkNotSubtype("{[void] f1}","[any]"); }
	@Test public void test_6319() { checkNotSubtype("{[void] f1}","[null]"); }
	@Test public void test_6320() { checkNotSubtype("{[void] f1}","[int]"); }
	@Test public void test_6321() { checkIsSubtype("{[void] f1}","{void f1}"); }
	@Test public void test_6322() { checkIsSubtype("{[void] f1}","{void f2}"); }
	@Test public void test_6323() { checkNotSubtype("{[void] f1}","{any f1}"); }
	@Test public void test_6324() { checkNotSubtype("{[void] f1}","{any f2}"); }
	@Test public void test_6325() { checkNotSubtype("{[void] f1}","{null f1}"); }
	@Test public void test_6326() { checkNotSubtype("{[void] f1}","{null f2}"); }
	@Test public void test_6327() { checkNotSubtype("{[void] f1}","{int f1}"); }
	@Test public void test_6328() { checkNotSubtype("{[void] f1}","{int f2}"); }
	@Test public void test_6329() { checkNotSubtype("{[void] f1}","[[void]]"); }
	@Test public void test_6330() { checkNotSubtype("{[void] f1}","[[any]]"); }
	@Test public void test_6331() { checkNotSubtype("{[void] f1}","[[null]]"); }
	@Test public void test_6332() { checkNotSubtype("{[void] f1}","[[int]]"); }
	@Test public void test_6333() { checkNotSubtype("{[void] f1}","[{void f1}]"); }
	@Test public void test_6334() { checkNotSubtype("{[void] f1}","[{void f2}]"); }
	@Test public void test_6335() { checkNotSubtype("{[void] f1}","[{any f1}]"); }
	@Test public void test_6336() { checkNotSubtype("{[void] f1}","[{any f2}]"); }
	@Test public void test_6337() { checkNotSubtype("{[void] f1}","[{null f1}]"); }
	@Test public void test_6338() { checkNotSubtype("{[void] f1}","[{null f2}]"); }
	@Test public void test_6339() { checkNotSubtype("{[void] f1}","[{int f1}]"); }
	@Test public void test_6340() { checkNotSubtype("{[void] f1}","[{int f2}]"); }
	@Test public void test_6341() { checkIsSubtype("{[void] f1}","{void f1,void f2}"); }
	@Test public void test_6342() { checkIsSubtype("{[void] f1}","{void f2,void f3}"); }
	@Test public void test_6343() { checkIsSubtype("{[void] f1}","{void f1,any f2}"); }
	@Test public void test_6344() { checkIsSubtype("{[void] f1}","{void f2,any f3}"); }
	@Test public void test_6345() { checkIsSubtype("{[void] f1}","{void f1,null f2}"); }
	@Test public void test_6346() { checkIsSubtype("{[void] f1}","{void f2,null f3}"); }
	@Test public void test_6347() { checkIsSubtype("{[void] f1}","{void f1,int f2}"); }
	@Test public void test_6348() { checkIsSubtype("{[void] f1}","{void f2,int f3}"); }
	@Test public void test_6349() { checkIsSubtype("{[void] f1}","{any f1,void f2}"); }
	@Test public void test_6350() { checkIsSubtype("{[void] f1}","{any f2,void f3}"); }
	@Test public void test_6351() { checkNotSubtype("{[void] f1}","{any f1,any f2}"); }
	@Test public void test_6352() { checkNotSubtype("{[void] f1}","{any f2,any f3}"); }
	@Test public void test_6353() { checkNotSubtype("{[void] f1}","{any f1,null f2}"); }
	@Test public void test_6354() { checkNotSubtype("{[void] f1}","{any f2,null f3}"); }
	@Test public void test_6355() { checkNotSubtype("{[void] f1}","{any f1,int f2}"); }
	@Test public void test_6356() { checkNotSubtype("{[void] f1}","{any f2,int f3}"); }
	@Test public void test_6357() { checkIsSubtype("{[void] f1}","{null f1,void f2}"); }
	@Test public void test_6358() { checkIsSubtype("{[void] f1}","{null f2,void f3}"); }
	@Test public void test_6359() { checkNotSubtype("{[void] f1}","{null f1,any f2}"); }
	@Test public void test_6360() { checkNotSubtype("{[void] f1}","{null f2,any f3}"); }
	@Test public void test_6361() { checkNotSubtype("{[void] f1}","{null f1,null f2}"); }
	@Test public void test_6362() { checkNotSubtype("{[void] f1}","{null f2,null f3}"); }
	@Test public void test_6363() { checkNotSubtype("{[void] f1}","{null f1,int f2}"); }
	@Test public void test_6364() { checkNotSubtype("{[void] f1}","{null f2,int f3}"); }
	@Test public void test_6365() { checkIsSubtype("{[void] f1}","{int f1,void f2}"); }
	@Test public void test_6366() { checkIsSubtype("{[void] f1}","{int f2,void f3}"); }
	@Test public void test_6367() { checkNotSubtype("{[void] f1}","{int f1,any f2}"); }
	@Test public void test_6368() { checkNotSubtype("{[void] f1}","{int f2,any f3}"); }
	@Test public void test_6369() { checkNotSubtype("{[void] f1}","{int f1,null f2}"); }
	@Test public void test_6370() { checkNotSubtype("{[void] f1}","{int f2,null f3}"); }
	@Test public void test_6371() { checkNotSubtype("{[void] f1}","{int f1,int f2}"); }
	@Test public void test_6372() { checkNotSubtype("{[void] f1}","{int f2,int f3}"); }
	@Test public void test_6373() { checkIsSubtype("{[void] f1}","{[void] f1}"); }
	@Test public void test_6374() { checkNotSubtype("{[void] f1}","{[void] f2}"); }
	@Test public void test_6375() { checkIsSubtype("{[void] f1}","{[void] f1,void f2}"); }
	@Test public void test_6376() { checkIsSubtype("{[void] f1}","{[void] f2,void f3}"); }
	@Test public void test_6377() { checkNotSubtype("{[void] f1}","{[any] f1}"); }
	@Test public void test_6378() { checkNotSubtype("{[void] f1}","{[any] f2}"); }
	@Test public void test_6379() { checkNotSubtype("{[void] f1}","{[any] f1,any f2}"); }
	@Test public void test_6380() { checkNotSubtype("{[void] f1}","{[any] f2,any f3}"); }
	@Test public void test_6381() { checkNotSubtype("{[void] f1}","{[null] f1}"); }
	@Test public void test_6382() { checkNotSubtype("{[void] f1}","{[null] f2}"); }
	@Test public void test_6383() { checkNotSubtype("{[void] f1}","{[null] f1,null f2}"); }
	@Test public void test_6384() { checkNotSubtype("{[void] f1}","{[null] f2,null f3}"); }
	@Test public void test_6385() { checkNotSubtype("{[void] f1}","{[int] f1}"); }
	@Test public void test_6386() { checkNotSubtype("{[void] f1}","{[int] f2}"); }
	@Test public void test_6387() { checkNotSubtype("{[void] f1}","{[int] f1,int f2}"); }
	@Test public void test_6388() { checkNotSubtype("{[void] f1}","{[int] f2,int f3}"); }
	@Test public void test_6389() { checkIsSubtype("{[void] f1}","{{void f1} f1}"); }
	@Test public void test_6390() { checkIsSubtype("{[void] f1}","{{void f2} f1}"); }
	@Test public void test_6391() { checkIsSubtype("{[void] f1}","{{void f1} f2}"); }
	@Test public void test_6392() { checkIsSubtype("{[void] f1}","{{void f2} f2}"); }
	@Test public void test_6393() { checkIsSubtype("{[void] f1}","{{void f1} f1,void f2}"); }
	@Test public void test_6394() { checkIsSubtype("{[void] f1}","{{void f2} f1,void f2}"); }
	@Test public void test_6395() { checkIsSubtype("{[void] f1}","{{void f1} f2,void f3}"); }
	@Test public void test_6396() { checkIsSubtype("{[void] f1}","{{void f2} f2,void f3}"); }
	@Test public void test_6397() { checkNotSubtype("{[void] f1}","{{any f1} f1}"); }
	@Test public void test_6398() { checkNotSubtype("{[void] f1}","{{any f2} f1}"); }
	@Test public void test_6399() { checkNotSubtype("{[void] f1}","{{any f1} f2}"); }
	@Test public void test_6400() { checkNotSubtype("{[void] f1}","{{any f2} f2}"); }
	@Test public void test_6401() { checkNotSubtype("{[void] f1}","{{any f1} f1,any f2}"); }
	@Test public void test_6402() { checkNotSubtype("{[void] f1}","{{any f2} f1,any f2}"); }
	@Test public void test_6403() { checkNotSubtype("{[void] f1}","{{any f1} f2,any f3}"); }
	@Test public void test_6404() { checkNotSubtype("{[void] f1}","{{any f2} f2,any f3}"); }
	@Test public void test_6405() { checkNotSubtype("{[void] f1}","{{null f1} f1}"); }
	@Test public void test_6406() { checkNotSubtype("{[void] f1}","{{null f2} f1}"); }
	@Test public void test_6407() { checkNotSubtype("{[void] f1}","{{null f1} f2}"); }
	@Test public void test_6408() { checkNotSubtype("{[void] f1}","{{null f2} f2}"); }
	@Test public void test_6409() { checkNotSubtype("{[void] f1}","{{null f1} f1,null f2}"); }
	@Test public void test_6410() { checkNotSubtype("{[void] f1}","{{null f2} f1,null f2}"); }
	@Test public void test_6411() { checkNotSubtype("{[void] f1}","{{null f1} f2,null f3}"); }
	@Test public void test_6412() { checkNotSubtype("{[void] f1}","{{null f2} f2,null f3}"); }
	@Test public void test_6413() { checkNotSubtype("{[void] f1}","{{int f1} f1}"); }
	@Test public void test_6414() { checkNotSubtype("{[void] f1}","{{int f2} f1}"); }
	@Test public void test_6415() { checkNotSubtype("{[void] f1}","{{int f1} f2}"); }
	@Test public void test_6416() { checkNotSubtype("{[void] f1}","{{int f2} f2}"); }
	@Test public void test_6417() { checkNotSubtype("{[void] f1}","{{int f1} f1,int f2}"); }
	@Test public void test_6418() { checkNotSubtype("{[void] f1}","{{int f2} f1,int f2}"); }
	@Test public void test_6419() { checkNotSubtype("{[void] f1}","{{int f1} f2,int f3}"); }
	@Test public void test_6420() { checkNotSubtype("{[void] f1}","{{int f2} f2,int f3}"); }
	@Test public void test_6421() { checkNotSubtype("{[void] f2}","any"); }
	@Test public void test_6422() { checkNotSubtype("{[void] f2}","null"); }
	@Test public void test_6423() { checkNotSubtype("{[void] f2}","int"); }
	@Test public void test_6424() { checkNotSubtype("{[void] f2}","[void]"); }
	@Test public void test_6425() { checkNotSubtype("{[void] f2}","[any]"); }
	@Test public void test_6426() { checkNotSubtype("{[void] f2}","[null]"); }
	@Test public void test_6427() { checkNotSubtype("{[void] f2}","[int]"); }
	@Test public void test_6428() { checkIsSubtype("{[void] f2}","{void f1}"); }
	@Test public void test_6429() { checkIsSubtype("{[void] f2}","{void f2}"); }
	@Test public void test_6430() { checkNotSubtype("{[void] f2}","{any f1}"); }
	@Test public void test_6431() { checkNotSubtype("{[void] f2}","{any f2}"); }
	@Test public void test_6432() { checkNotSubtype("{[void] f2}","{null f1}"); }
	@Test public void test_6433() { checkNotSubtype("{[void] f2}","{null f2}"); }
	@Test public void test_6434() { checkNotSubtype("{[void] f2}","{int f1}"); }
	@Test public void test_6435() { checkNotSubtype("{[void] f2}","{int f2}"); }
	@Test public void test_6436() { checkNotSubtype("{[void] f2}","[[void]]"); }
	@Test public void test_6437() { checkNotSubtype("{[void] f2}","[[any]]"); }
	@Test public void test_6438() { checkNotSubtype("{[void] f2}","[[null]]"); }
	@Test public void test_6439() { checkNotSubtype("{[void] f2}","[[int]]"); }
	@Test public void test_6440() { checkNotSubtype("{[void] f2}","[{void f1}]"); }
	@Test public void test_6441() { checkNotSubtype("{[void] f2}","[{void f2}]"); }
	@Test public void test_6442() { checkNotSubtype("{[void] f2}","[{any f1}]"); }
	@Test public void test_6443() { checkNotSubtype("{[void] f2}","[{any f2}]"); }
	@Test public void test_6444() { checkNotSubtype("{[void] f2}","[{null f1}]"); }
	@Test public void test_6445() { checkNotSubtype("{[void] f2}","[{null f2}]"); }
	@Test public void test_6446() { checkNotSubtype("{[void] f2}","[{int f1}]"); }
	@Test public void test_6447() { checkNotSubtype("{[void] f2}","[{int f2}]"); }
	@Test public void test_6448() { checkIsSubtype("{[void] f2}","{void f1,void f2}"); }
	@Test public void test_6449() { checkIsSubtype("{[void] f2}","{void f2,void f3}"); }
	@Test public void test_6450() { checkIsSubtype("{[void] f2}","{void f1,any f2}"); }
	@Test public void test_6451() { checkIsSubtype("{[void] f2}","{void f2,any f3}"); }
	@Test public void test_6452() { checkIsSubtype("{[void] f2}","{void f1,null f2}"); }
	@Test public void test_6453() { checkIsSubtype("{[void] f2}","{void f2,null f3}"); }
	@Test public void test_6454() { checkIsSubtype("{[void] f2}","{void f1,int f2}"); }
	@Test public void test_6455() { checkIsSubtype("{[void] f2}","{void f2,int f3}"); }
	@Test public void test_6456() { checkIsSubtype("{[void] f2}","{any f1,void f2}"); }
	@Test public void test_6457() { checkIsSubtype("{[void] f2}","{any f2,void f3}"); }
	@Test public void test_6458() { checkNotSubtype("{[void] f2}","{any f1,any f2}"); }
	@Test public void test_6459() { checkNotSubtype("{[void] f2}","{any f2,any f3}"); }
	@Test public void test_6460() { checkNotSubtype("{[void] f2}","{any f1,null f2}"); }
	@Test public void test_6461() { checkNotSubtype("{[void] f2}","{any f2,null f3}"); }
	@Test public void test_6462() { checkNotSubtype("{[void] f2}","{any f1,int f2}"); }
	@Test public void test_6463() { checkNotSubtype("{[void] f2}","{any f2,int f3}"); }
	@Test public void test_6464() { checkIsSubtype("{[void] f2}","{null f1,void f2}"); }
	@Test public void test_6465() { checkIsSubtype("{[void] f2}","{null f2,void f3}"); }
	@Test public void test_6466() { checkNotSubtype("{[void] f2}","{null f1,any f2}"); }
	@Test public void test_6467() { checkNotSubtype("{[void] f2}","{null f2,any f3}"); }
	@Test public void test_6468() { checkNotSubtype("{[void] f2}","{null f1,null f2}"); }
	@Test public void test_6469() { checkNotSubtype("{[void] f2}","{null f2,null f3}"); }
	@Test public void test_6470() { checkNotSubtype("{[void] f2}","{null f1,int f2}"); }
	@Test public void test_6471() { checkNotSubtype("{[void] f2}","{null f2,int f3}"); }
	@Test public void test_6472() { checkIsSubtype("{[void] f2}","{int f1,void f2}"); }
	@Test public void test_6473() { checkIsSubtype("{[void] f2}","{int f2,void f3}"); }
	@Test public void test_6474() { checkNotSubtype("{[void] f2}","{int f1,any f2}"); }
	@Test public void test_6475() { checkNotSubtype("{[void] f2}","{int f2,any f3}"); }
	@Test public void test_6476() { checkNotSubtype("{[void] f2}","{int f1,null f2}"); }
	@Test public void test_6477() { checkNotSubtype("{[void] f2}","{int f2,null f3}"); }
	@Test public void test_6478() { checkNotSubtype("{[void] f2}","{int f1,int f2}"); }
	@Test public void test_6479() { checkNotSubtype("{[void] f2}","{int f2,int f3}"); }
	@Test public void test_6480() { checkNotSubtype("{[void] f2}","{[void] f1}"); }
	@Test public void test_6481() { checkIsSubtype("{[void] f2}","{[void] f2}"); }
	@Test public void test_6482() { checkIsSubtype("{[void] f2}","{[void] f1,void f2}"); }
	@Test public void test_6483() { checkIsSubtype("{[void] f2}","{[void] f2,void f3}"); }
	@Test public void test_6484() { checkNotSubtype("{[void] f2}","{[any] f1}"); }
	@Test public void test_6485() { checkNotSubtype("{[void] f2}","{[any] f2}"); }
	@Test public void test_6486() { checkNotSubtype("{[void] f2}","{[any] f1,any f2}"); }
	@Test public void test_6487() { checkNotSubtype("{[void] f2}","{[any] f2,any f3}"); }
	@Test public void test_6488() { checkNotSubtype("{[void] f2}","{[null] f1}"); }
	@Test public void test_6489() { checkNotSubtype("{[void] f2}","{[null] f2}"); }
	@Test public void test_6490() { checkNotSubtype("{[void] f2}","{[null] f1,null f2}"); }
	@Test public void test_6491() { checkNotSubtype("{[void] f2}","{[null] f2,null f3}"); }
	@Test public void test_6492() { checkNotSubtype("{[void] f2}","{[int] f1}"); }
	@Test public void test_6493() { checkNotSubtype("{[void] f2}","{[int] f2}"); }
	@Test public void test_6494() { checkNotSubtype("{[void] f2}","{[int] f1,int f2}"); }
	@Test public void test_6495() { checkNotSubtype("{[void] f2}","{[int] f2,int f3}"); }
	@Test public void test_6496() { checkIsSubtype("{[void] f2}","{{void f1} f1}"); }
	@Test public void test_6497() { checkIsSubtype("{[void] f2}","{{void f2} f1}"); }
	@Test public void test_6498() { checkIsSubtype("{[void] f2}","{{void f1} f2}"); }
	@Test public void test_6499() { checkIsSubtype("{[void] f2}","{{void f2} f2}"); }
	@Test public void test_6500() { checkIsSubtype("{[void] f2}","{{void f1} f1,void f2}"); }
	@Test public void test_6501() { checkIsSubtype("{[void] f2}","{{void f2} f1,void f2}"); }
	@Test public void test_6502() { checkIsSubtype("{[void] f2}","{{void f1} f2,void f3}"); }
	@Test public void test_6503() { checkIsSubtype("{[void] f2}","{{void f2} f2,void f3}"); }
	@Test public void test_6504() { checkNotSubtype("{[void] f2}","{{any f1} f1}"); }
	@Test public void test_6505() { checkNotSubtype("{[void] f2}","{{any f2} f1}"); }
	@Test public void test_6506() { checkNotSubtype("{[void] f2}","{{any f1} f2}"); }
	@Test public void test_6507() { checkNotSubtype("{[void] f2}","{{any f2} f2}"); }
	@Test public void test_6508() { checkNotSubtype("{[void] f2}","{{any f1} f1,any f2}"); }
	@Test public void test_6509() { checkNotSubtype("{[void] f2}","{{any f2} f1,any f2}"); }
	@Test public void test_6510() { checkNotSubtype("{[void] f2}","{{any f1} f2,any f3}"); }
	@Test public void test_6511() { checkNotSubtype("{[void] f2}","{{any f2} f2,any f3}"); }
	@Test public void test_6512() { checkNotSubtype("{[void] f2}","{{null f1} f1}"); }
	@Test public void test_6513() { checkNotSubtype("{[void] f2}","{{null f2} f1}"); }
	@Test public void test_6514() { checkNotSubtype("{[void] f2}","{{null f1} f2}"); }
	@Test public void test_6515() { checkNotSubtype("{[void] f2}","{{null f2} f2}"); }
	@Test public void test_6516() { checkNotSubtype("{[void] f2}","{{null f1} f1,null f2}"); }
	@Test public void test_6517() { checkNotSubtype("{[void] f2}","{{null f2} f1,null f2}"); }
	@Test public void test_6518() { checkNotSubtype("{[void] f2}","{{null f1} f2,null f3}"); }
	@Test public void test_6519() { checkNotSubtype("{[void] f2}","{{null f2} f2,null f3}"); }
	@Test public void test_6520() { checkNotSubtype("{[void] f2}","{{int f1} f1}"); }
	@Test public void test_6521() { checkNotSubtype("{[void] f2}","{{int f2} f1}"); }
	@Test public void test_6522() { checkNotSubtype("{[void] f2}","{{int f1} f2}"); }
	@Test public void test_6523() { checkNotSubtype("{[void] f2}","{{int f2} f2}"); }
	@Test public void test_6524() { checkNotSubtype("{[void] f2}","{{int f1} f1,int f2}"); }
	@Test public void test_6525() { checkNotSubtype("{[void] f2}","{{int f2} f1,int f2}"); }
	@Test public void test_6526() { checkNotSubtype("{[void] f2}","{{int f1} f2,int f3}"); }
	@Test public void test_6527() { checkNotSubtype("{[void] f2}","{{int f2} f2,int f3}"); }
	@Test public void test_6528() { checkNotSubtype("{[void] f1,void f2}","any"); }
	@Test public void test_6529() { checkNotSubtype("{[void] f1,void f2}","null"); }
	@Test public void test_6530() { checkNotSubtype("{[void] f1,void f2}","int"); }
	@Test public void test_6531() { checkNotSubtype("{[void] f1,void f2}","[void]"); }
	@Test public void test_6532() { checkNotSubtype("{[void] f1,void f2}","[any]"); }
	@Test public void test_6533() { checkNotSubtype("{[void] f1,void f2}","[null]"); }
	@Test public void test_6534() { checkNotSubtype("{[void] f1,void f2}","[int]"); }
	@Test public void test_6535() { checkIsSubtype("{[void] f1,void f2}","{void f1}"); }
	@Test public void test_6536() { checkIsSubtype("{[void] f1,void f2}","{void f2}"); }
	@Test public void test_6537() { checkNotSubtype("{[void] f1,void f2}","{any f1}"); }
	@Test public void test_6538() { checkNotSubtype("{[void] f1,void f2}","{any f2}"); }
	@Test public void test_6539() { checkNotSubtype("{[void] f1,void f2}","{null f1}"); }
	@Test public void test_6540() { checkNotSubtype("{[void] f1,void f2}","{null f2}"); }
	@Test public void test_6541() { checkNotSubtype("{[void] f1,void f2}","{int f1}"); }
	@Test public void test_6542() { checkNotSubtype("{[void] f1,void f2}","{int f2}"); }
	@Test public void test_6543() { checkNotSubtype("{[void] f1,void f2}","[[void]]"); }
	@Test public void test_6544() { checkNotSubtype("{[void] f1,void f2}","[[any]]"); }
	@Test public void test_6545() { checkNotSubtype("{[void] f1,void f2}","[[null]]"); }
	@Test public void test_6546() { checkNotSubtype("{[void] f1,void f2}","[[int]]"); }
	@Test public void test_6547() { checkNotSubtype("{[void] f1,void f2}","[{void f1}]"); }
	@Test public void test_6548() { checkNotSubtype("{[void] f1,void f2}","[{void f2}]"); }
	@Test public void test_6549() { checkNotSubtype("{[void] f1,void f2}","[{any f1}]"); }
	@Test public void test_6550() { checkNotSubtype("{[void] f1,void f2}","[{any f2}]"); }
	@Test public void test_6551() { checkNotSubtype("{[void] f1,void f2}","[{null f1}]"); }
	@Test public void test_6552() { checkNotSubtype("{[void] f1,void f2}","[{null f2}]"); }
	@Test public void test_6553() { checkNotSubtype("{[void] f1,void f2}","[{int f1}]"); }
	@Test public void test_6554() { checkNotSubtype("{[void] f1,void f2}","[{int f2}]"); }
	@Test public void test_6555() { checkIsSubtype("{[void] f1,void f2}","{void f1,void f2}"); }
	@Test public void test_6556() { checkIsSubtype("{[void] f1,void f2}","{void f2,void f3}"); }
	@Test public void test_6557() { checkIsSubtype("{[void] f1,void f2}","{void f1,any f2}"); }
	@Test public void test_6558() { checkIsSubtype("{[void] f1,void f2}","{void f2,any f3}"); }
	@Test public void test_6559() { checkIsSubtype("{[void] f1,void f2}","{void f1,null f2}"); }
	@Test public void test_6560() { checkIsSubtype("{[void] f1,void f2}","{void f2,null f3}"); }
	@Test public void test_6561() { checkIsSubtype("{[void] f1,void f2}","{void f1,int f2}"); }
	@Test public void test_6562() { checkIsSubtype("{[void] f1,void f2}","{void f2,int f3}"); }
	@Test public void test_6563() { checkIsSubtype("{[void] f1,void f2}","{any f1,void f2}"); }
	@Test public void test_6564() { checkIsSubtype("{[void] f1,void f2}","{any f2,void f3}"); }
	@Test public void test_6565() { checkNotSubtype("{[void] f1,void f2}","{any f1,any f2}"); }
	@Test public void test_6566() { checkNotSubtype("{[void] f1,void f2}","{any f2,any f3}"); }
	@Test public void test_6567() { checkNotSubtype("{[void] f1,void f2}","{any f1,null f2}"); }
	@Test public void test_6568() { checkNotSubtype("{[void] f1,void f2}","{any f2,null f3}"); }
	@Test public void test_6569() { checkNotSubtype("{[void] f1,void f2}","{any f1,int f2}"); }
	@Test public void test_6570() { checkNotSubtype("{[void] f1,void f2}","{any f2,int f3}"); }
	@Test public void test_6571() { checkIsSubtype("{[void] f1,void f2}","{null f1,void f2}"); }
	@Test public void test_6572() { checkIsSubtype("{[void] f1,void f2}","{null f2,void f3}"); }
	@Test public void test_6573() { checkNotSubtype("{[void] f1,void f2}","{null f1,any f2}"); }
	@Test public void test_6574() { checkNotSubtype("{[void] f1,void f2}","{null f2,any f3}"); }
	@Test public void test_6575() { checkNotSubtype("{[void] f1,void f2}","{null f1,null f2}"); }
	@Test public void test_6576() { checkNotSubtype("{[void] f1,void f2}","{null f2,null f3}"); }
	@Test public void test_6577() { checkNotSubtype("{[void] f1,void f2}","{null f1,int f2}"); }
	@Test public void test_6578() { checkNotSubtype("{[void] f1,void f2}","{null f2,int f3}"); }
	@Test public void test_6579() { checkIsSubtype("{[void] f1,void f2}","{int f1,void f2}"); }
	@Test public void test_6580() { checkIsSubtype("{[void] f1,void f2}","{int f2,void f3}"); }
	@Test public void test_6581() { checkNotSubtype("{[void] f1,void f2}","{int f1,any f2}"); }
	@Test public void test_6582() { checkNotSubtype("{[void] f1,void f2}","{int f2,any f3}"); }
	@Test public void test_6583() { checkNotSubtype("{[void] f1,void f2}","{int f1,null f2}"); }
	@Test public void test_6584() { checkNotSubtype("{[void] f1,void f2}","{int f2,null f3}"); }
	@Test public void test_6585() { checkNotSubtype("{[void] f1,void f2}","{int f1,int f2}"); }
	@Test public void test_6586() { checkNotSubtype("{[void] f1,void f2}","{int f2,int f3}"); }
	@Test public void test_6587() { checkNotSubtype("{[void] f1,void f2}","{[void] f1}"); }
	@Test public void test_6588() { checkNotSubtype("{[void] f1,void f2}","{[void] f2}"); }
	@Test public void test_6589() { checkIsSubtype("{[void] f1,void f2}","{[void] f1,void f2}"); }
	@Test public void test_6590() { checkIsSubtype("{[void] f1,void f2}","{[void] f2,void f3}"); }
	@Test public void test_6591() { checkNotSubtype("{[void] f1,void f2}","{[any] f1}"); }
	@Test public void test_6592() { checkNotSubtype("{[void] f1,void f2}","{[any] f2}"); }
	@Test public void test_6593() { checkNotSubtype("{[void] f1,void f2}","{[any] f1,any f2}"); }
	@Test public void test_6594() { checkNotSubtype("{[void] f1,void f2}","{[any] f2,any f3}"); }
	@Test public void test_6595() { checkNotSubtype("{[void] f1,void f2}","{[null] f1}"); }
	@Test public void test_6596() { checkNotSubtype("{[void] f1,void f2}","{[null] f2}"); }
	@Test public void test_6597() { checkNotSubtype("{[void] f1,void f2}","{[null] f1,null f2}"); }
	@Test public void test_6598() { checkNotSubtype("{[void] f1,void f2}","{[null] f2,null f3}"); }
	@Test public void test_6599() { checkNotSubtype("{[void] f1,void f2}","{[int] f1}"); }
	@Test public void test_6600() { checkNotSubtype("{[void] f1,void f2}","{[int] f2}"); }
	@Test public void test_6601() { checkNotSubtype("{[void] f1,void f2}","{[int] f1,int f2}"); }
	@Test public void test_6602() { checkNotSubtype("{[void] f1,void f2}","{[int] f2,int f3}"); }
	@Test public void test_6603() { checkIsSubtype("{[void] f1,void f2}","{{void f1} f1}"); }
	@Test public void test_6604() { checkIsSubtype("{[void] f1,void f2}","{{void f2} f1}"); }
	@Test public void test_6605() { checkIsSubtype("{[void] f1,void f2}","{{void f1} f2}"); }
	@Test public void test_6606() { checkIsSubtype("{[void] f1,void f2}","{{void f2} f2}"); }
	@Test public void test_6607() { checkIsSubtype("{[void] f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_6608() { checkIsSubtype("{[void] f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_6609() { checkIsSubtype("{[void] f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_6610() { checkIsSubtype("{[void] f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_6611() { checkNotSubtype("{[void] f1,void f2}","{{any f1} f1}"); }
	@Test public void test_6612() { checkNotSubtype("{[void] f1,void f2}","{{any f2} f1}"); }
	@Test public void test_6613() { checkNotSubtype("{[void] f1,void f2}","{{any f1} f2}"); }
	@Test public void test_6614() { checkNotSubtype("{[void] f1,void f2}","{{any f2} f2}"); }
	@Test public void test_6615() { checkNotSubtype("{[void] f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_6616() { checkNotSubtype("{[void] f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_6617() { checkNotSubtype("{[void] f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_6618() { checkNotSubtype("{[void] f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_6619() { checkNotSubtype("{[void] f1,void f2}","{{null f1} f1}"); }
	@Test public void test_6620() { checkNotSubtype("{[void] f1,void f2}","{{null f2} f1}"); }
	@Test public void test_6621() { checkNotSubtype("{[void] f1,void f2}","{{null f1} f2}"); }
	@Test public void test_6622() { checkNotSubtype("{[void] f1,void f2}","{{null f2} f2}"); }
	@Test public void test_6623() { checkNotSubtype("{[void] f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_6624() { checkNotSubtype("{[void] f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_6625() { checkNotSubtype("{[void] f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_6626() { checkNotSubtype("{[void] f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_6627() { checkNotSubtype("{[void] f1,void f2}","{{int f1} f1}"); }
	@Test public void test_6628() { checkNotSubtype("{[void] f1,void f2}","{{int f2} f1}"); }
	@Test public void test_6629() { checkNotSubtype("{[void] f1,void f2}","{{int f1} f2}"); }
	@Test public void test_6630() { checkNotSubtype("{[void] f1,void f2}","{{int f2} f2}"); }
	@Test public void test_6631() { checkNotSubtype("{[void] f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_6632() { checkNotSubtype("{[void] f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_6633() { checkNotSubtype("{[void] f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_6634() { checkNotSubtype("{[void] f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_6635() { checkNotSubtype("{[void] f2,void f3}","any"); }
	@Test public void test_6636() { checkNotSubtype("{[void] f2,void f3}","null"); }
	@Test public void test_6637() { checkNotSubtype("{[void] f2,void f3}","int"); }
	@Test public void test_6638() { checkNotSubtype("{[void] f2,void f3}","[void]"); }
	@Test public void test_6639() { checkNotSubtype("{[void] f2,void f3}","[any]"); }
	@Test public void test_6640() { checkNotSubtype("{[void] f2,void f3}","[null]"); }
	@Test public void test_6641() { checkNotSubtype("{[void] f2,void f3}","[int]"); }
	@Test public void test_6642() { checkIsSubtype("{[void] f2,void f3}","{void f1}"); }
	@Test public void test_6643() { checkIsSubtype("{[void] f2,void f3}","{void f2}"); }
	@Test public void test_6644() { checkNotSubtype("{[void] f2,void f3}","{any f1}"); }
	@Test public void test_6645() { checkNotSubtype("{[void] f2,void f3}","{any f2}"); }
	@Test public void test_6646() { checkNotSubtype("{[void] f2,void f3}","{null f1}"); }
	@Test public void test_6647() { checkNotSubtype("{[void] f2,void f3}","{null f2}"); }
	@Test public void test_6648() { checkNotSubtype("{[void] f2,void f3}","{int f1}"); }
	@Test public void test_6649() { checkNotSubtype("{[void] f2,void f3}","{int f2}"); }
	@Test public void test_6650() { checkNotSubtype("{[void] f2,void f3}","[[void]]"); }
	@Test public void test_6651() { checkNotSubtype("{[void] f2,void f3}","[[any]]"); }
	@Test public void test_6652() { checkNotSubtype("{[void] f2,void f3}","[[null]]"); }
	@Test public void test_6653() { checkNotSubtype("{[void] f2,void f3}","[[int]]"); }
	@Test public void test_6654() { checkNotSubtype("{[void] f2,void f3}","[{void f1}]"); }
	@Test public void test_6655() { checkNotSubtype("{[void] f2,void f3}","[{void f2}]"); }
	@Test public void test_6656() { checkNotSubtype("{[void] f2,void f3}","[{any f1}]"); }
	@Test public void test_6657() { checkNotSubtype("{[void] f2,void f3}","[{any f2}]"); }
	@Test public void test_6658() { checkNotSubtype("{[void] f2,void f3}","[{null f1}]"); }
	@Test public void test_6659() { checkNotSubtype("{[void] f2,void f3}","[{null f2}]"); }
	@Test public void test_6660() { checkNotSubtype("{[void] f2,void f3}","[{int f1}]"); }
	@Test public void test_6661() { checkNotSubtype("{[void] f2,void f3}","[{int f2}]"); }
	@Test public void test_6662() { checkIsSubtype("{[void] f2,void f3}","{void f1,void f2}"); }
	@Test public void test_6663() { checkIsSubtype("{[void] f2,void f3}","{void f2,void f3}"); }
	@Test public void test_6664() { checkIsSubtype("{[void] f2,void f3}","{void f1,any f2}"); }
	@Test public void test_6665() { checkIsSubtype("{[void] f2,void f3}","{void f2,any f3}"); }
	@Test public void test_6666() { checkIsSubtype("{[void] f2,void f3}","{void f1,null f2}"); }
	@Test public void test_6667() { checkIsSubtype("{[void] f2,void f3}","{void f2,null f3}"); }
	@Test public void test_6668() { checkIsSubtype("{[void] f2,void f3}","{void f1,int f2}"); }
	@Test public void test_6669() { checkIsSubtype("{[void] f2,void f3}","{void f2,int f3}"); }
	@Test public void test_6670() { checkIsSubtype("{[void] f2,void f3}","{any f1,void f2}"); }
	@Test public void test_6671() { checkIsSubtype("{[void] f2,void f3}","{any f2,void f3}"); }
	@Test public void test_6672() { checkNotSubtype("{[void] f2,void f3}","{any f1,any f2}"); }
	@Test public void test_6673() { checkNotSubtype("{[void] f2,void f3}","{any f2,any f3}"); }
	@Test public void test_6674() { checkNotSubtype("{[void] f2,void f3}","{any f1,null f2}"); }
	@Test public void test_6675() { checkNotSubtype("{[void] f2,void f3}","{any f2,null f3}"); }
	@Test public void test_6676() { checkNotSubtype("{[void] f2,void f3}","{any f1,int f2}"); }
	@Test public void test_6677() { checkNotSubtype("{[void] f2,void f3}","{any f2,int f3}"); }
	@Test public void test_6678() { checkIsSubtype("{[void] f2,void f3}","{null f1,void f2}"); }
	@Test public void test_6679() { checkIsSubtype("{[void] f2,void f3}","{null f2,void f3}"); }
	@Test public void test_6680() { checkNotSubtype("{[void] f2,void f3}","{null f1,any f2}"); }
	@Test public void test_6681() { checkNotSubtype("{[void] f2,void f3}","{null f2,any f3}"); }
	@Test public void test_6682() { checkNotSubtype("{[void] f2,void f3}","{null f1,null f2}"); }
	@Test public void test_6683() { checkNotSubtype("{[void] f2,void f3}","{null f2,null f3}"); }
	@Test public void test_6684() { checkNotSubtype("{[void] f2,void f3}","{null f1,int f2}"); }
	@Test public void test_6685() { checkNotSubtype("{[void] f2,void f3}","{null f2,int f3}"); }
	@Test public void test_6686() { checkIsSubtype("{[void] f2,void f3}","{int f1,void f2}"); }
	@Test public void test_6687() { checkIsSubtype("{[void] f2,void f3}","{int f2,void f3}"); }
	@Test public void test_6688() { checkNotSubtype("{[void] f2,void f3}","{int f1,any f2}"); }
	@Test public void test_6689() { checkNotSubtype("{[void] f2,void f3}","{int f2,any f3}"); }
	@Test public void test_6690() { checkNotSubtype("{[void] f2,void f3}","{int f1,null f2}"); }
	@Test public void test_6691() { checkNotSubtype("{[void] f2,void f3}","{int f2,null f3}"); }
	@Test public void test_6692() { checkNotSubtype("{[void] f2,void f3}","{int f1,int f2}"); }
	@Test public void test_6693() { checkNotSubtype("{[void] f2,void f3}","{int f2,int f3}"); }
	@Test public void test_6694() { checkNotSubtype("{[void] f2,void f3}","{[void] f1}"); }
	@Test public void test_6695() { checkNotSubtype("{[void] f2,void f3}","{[void] f2}"); }
	@Test public void test_6696() { checkIsSubtype("{[void] f2,void f3}","{[void] f1,void f2}"); }
	@Test public void test_6697() { checkIsSubtype("{[void] f2,void f3}","{[void] f2,void f3}"); }
	@Test public void test_6698() { checkNotSubtype("{[void] f2,void f3}","{[any] f1}"); }
	@Test public void test_6699() { checkNotSubtype("{[void] f2,void f3}","{[any] f2}"); }
	@Test public void test_6700() { checkNotSubtype("{[void] f2,void f3}","{[any] f1,any f2}"); }
	@Test public void test_6701() { checkNotSubtype("{[void] f2,void f3}","{[any] f2,any f3}"); }
	@Test public void test_6702() { checkNotSubtype("{[void] f2,void f3}","{[null] f1}"); }
	@Test public void test_6703() { checkNotSubtype("{[void] f2,void f3}","{[null] f2}"); }
	@Test public void test_6704() { checkNotSubtype("{[void] f2,void f3}","{[null] f1,null f2}"); }
	@Test public void test_6705() { checkNotSubtype("{[void] f2,void f3}","{[null] f2,null f3}"); }
	@Test public void test_6706() { checkNotSubtype("{[void] f2,void f3}","{[int] f1}"); }
	@Test public void test_6707() { checkNotSubtype("{[void] f2,void f3}","{[int] f2}"); }
	@Test public void test_6708() { checkNotSubtype("{[void] f2,void f3}","{[int] f1,int f2}"); }
	@Test public void test_6709() { checkNotSubtype("{[void] f2,void f3}","{[int] f2,int f3}"); }
	@Test public void test_6710() { checkIsSubtype("{[void] f2,void f3}","{{void f1} f1}"); }
	@Test public void test_6711() { checkIsSubtype("{[void] f2,void f3}","{{void f2} f1}"); }
	@Test public void test_6712() { checkIsSubtype("{[void] f2,void f3}","{{void f1} f2}"); }
	@Test public void test_6713() { checkIsSubtype("{[void] f2,void f3}","{{void f2} f2}"); }
	@Test public void test_6714() { checkIsSubtype("{[void] f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_6715() { checkIsSubtype("{[void] f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_6716() { checkIsSubtype("{[void] f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_6717() { checkIsSubtype("{[void] f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_6718() { checkNotSubtype("{[void] f2,void f3}","{{any f1} f1}"); }
	@Test public void test_6719() { checkNotSubtype("{[void] f2,void f3}","{{any f2} f1}"); }
	@Test public void test_6720() { checkNotSubtype("{[void] f2,void f3}","{{any f1} f2}"); }
	@Test public void test_6721() { checkNotSubtype("{[void] f2,void f3}","{{any f2} f2}"); }
	@Test public void test_6722() { checkNotSubtype("{[void] f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_6723() { checkNotSubtype("{[void] f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_6724() { checkNotSubtype("{[void] f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_6725() { checkNotSubtype("{[void] f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_6726() { checkNotSubtype("{[void] f2,void f3}","{{null f1} f1}"); }
	@Test public void test_6727() { checkNotSubtype("{[void] f2,void f3}","{{null f2} f1}"); }
	@Test public void test_6728() { checkNotSubtype("{[void] f2,void f3}","{{null f1} f2}"); }
	@Test public void test_6729() { checkNotSubtype("{[void] f2,void f3}","{{null f2} f2}"); }
	@Test public void test_6730() { checkNotSubtype("{[void] f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_6731() { checkNotSubtype("{[void] f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_6732() { checkNotSubtype("{[void] f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_6733() { checkNotSubtype("{[void] f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_6734() { checkNotSubtype("{[void] f2,void f3}","{{int f1} f1}"); }
	@Test public void test_6735() { checkNotSubtype("{[void] f2,void f3}","{{int f2} f1}"); }
	@Test public void test_6736() { checkNotSubtype("{[void] f2,void f3}","{{int f1} f2}"); }
	@Test public void test_6737() { checkNotSubtype("{[void] f2,void f3}","{{int f2} f2}"); }
	@Test public void test_6738() { checkNotSubtype("{[void] f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_6739() { checkNotSubtype("{[void] f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_6740() { checkNotSubtype("{[void] f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_6741() { checkNotSubtype("{[void] f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_6742() { checkNotSubtype("{[any] f1}","any"); }
	@Test public void test_6743() { checkNotSubtype("{[any] f1}","null"); }
	@Test public void test_6744() { checkNotSubtype("{[any] f1}","int"); }
	@Test public void test_6745() { checkNotSubtype("{[any] f1}","[void]"); }
	@Test public void test_6746() { checkNotSubtype("{[any] f1}","[any]"); }
	@Test public void test_6747() { checkNotSubtype("{[any] f1}","[null]"); }
	@Test public void test_6748() { checkNotSubtype("{[any] f1}","[int]"); }
	@Test public void test_6749() { checkIsSubtype("{[any] f1}","{void f1}"); }
	@Test public void test_6750() { checkIsSubtype("{[any] f1}","{void f2}"); }
	@Test public void test_6751() { checkNotSubtype("{[any] f1}","{any f1}"); }
	@Test public void test_6752() { checkNotSubtype("{[any] f1}","{any f2}"); }
	@Test public void test_6753() { checkNotSubtype("{[any] f1}","{null f1}"); }
	@Test public void test_6754() { checkNotSubtype("{[any] f1}","{null f2}"); }
	@Test public void test_6755() { checkNotSubtype("{[any] f1}","{int f1}"); }
	@Test public void test_6756() { checkNotSubtype("{[any] f1}","{int f2}"); }
	@Test public void test_6757() { checkNotSubtype("{[any] f1}","[[void]]"); }
	@Test public void test_6758() { checkNotSubtype("{[any] f1}","[[any]]"); }
	@Test public void test_6759() { checkNotSubtype("{[any] f1}","[[null]]"); }
	@Test public void test_6760() { checkNotSubtype("{[any] f1}","[[int]]"); }
	@Test public void test_6761() { checkNotSubtype("{[any] f1}","[{void f1}]"); }
	@Test public void test_6762() { checkNotSubtype("{[any] f1}","[{void f2}]"); }
	@Test public void test_6763() { checkNotSubtype("{[any] f1}","[{any f1}]"); }
	@Test public void test_6764() { checkNotSubtype("{[any] f1}","[{any f2}]"); }
	@Test public void test_6765() { checkNotSubtype("{[any] f1}","[{null f1}]"); }
	@Test public void test_6766() { checkNotSubtype("{[any] f1}","[{null f2}]"); }
	@Test public void test_6767() { checkNotSubtype("{[any] f1}","[{int f1}]"); }
	@Test public void test_6768() { checkNotSubtype("{[any] f1}","[{int f2}]"); }
	@Test public void test_6769() { checkIsSubtype("{[any] f1}","{void f1,void f2}"); }
	@Test public void test_6770() { checkIsSubtype("{[any] f1}","{void f2,void f3}"); }
	@Test public void test_6771() { checkIsSubtype("{[any] f1}","{void f1,any f2}"); }
	@Test public void test_6772() { checkIsSubtype("{[any] f1}","{void f2,any f3}"); }
	@Test public void test_6773() { checkIsSubtype("{[any] f1}","{void f1,null f2}"); }
	@Test public void test_6774() { checkIsSubtype("{[any] f1}","{void f2,null f3}"); }
	@Test public void test_6775() { checkIsSubtype("{[any] f1}","{void f1,int f2}"); }
	@Test public void test_6776() { checkIsSubtype("{[any] f1}","{void f2,int f3}"); }
	@Test public void test_6777() { checkIsSubtype("{[any] f1}","{any f1,void f2}"); }
	@Test public void test_6778() { checkIsSubtype("{[any] f1}","{any f2,void f3}"); }
	@Test public void test_6779() { checkNotSubtype("{[any] f1}","{any f1,any f2}"); }
	@Test public void test_6780() { checkNotSubtype("{[any] f1}","{any f2,any f3}"); }
	@Test public void test_6781() { checkNotSubtype("{[any] f1}","{any f1,null f2}"); }
	@Test public void test_6782() { checkNotSubtype("{[any] f1}","{any f2,null f3}"); }
	@Test public void test_6783() { checkNotSubtype("{[any] f1}","{any f1,int f2}"); }
	@Test public void test_6784() { checkNotSubtype("{[any] f1}","{any f2,int f3}"); }
	@Test public void test_6785() { checkIsSubtype("{[any] f1}","{null f1,void f2}"); }
	@Test public void test_6786() { checkIsSubtype("{[any] f1}","{null f2,void f3}"); }
	@Test public void test_6787() { checkNotSubtype("{[any] f1}","{null f1,any f2}"); }
	@Test public void test_6788() { checkNotSubtype("{[any] f1}","{null f2,any f3}"); }
	@Test public void test_6789() { checkNotSubtype("{[any] f1}","{null f1,null f2}"); }
	@Test public void test_6790() { checkNotSubtype("{[any] f1}","{null f2,null f3}"); }
	@Test public void test_6791() { checkNotSubtype("{[any] f1}","{null f1,int f2}"); }
	@Test public void test_6792() { checkNotSubtype("{[any] f1}","{null f2,int f3}"); }
	@Test public void test_6793() { checkIsSubtype("{[any] f1}","{int f1,void f2}"); }
	@Test public void test_6794() { checkIsSubtype("{[any] f1}","{int f2,void f3}"); }
	@Test public void test_6795() { checkNotSubtype("{[any] f1}","{int f1,any f2}"); }
	@Test public void test_6796() { checkNotSubtype("{[any] f1}","{int f2,any f3}"); }
	@Test public void test_6797() { checkNotSubtype("{[any] f1}","{int f1,null f2}"); }
	@Test public void test_6798() { checkNotSubtype("{[any] f1}","{int f2,null f3}"); }
	@Test public void test_6799() { checkNotSubtype("{[any] f1}","{int f1,int f2}"); }
	@Test public void test_6800() { checkNotSubtype("{[any] f1}","{int f2,int f3}"); }
	@Test public void test_6801() { checkIsSubtype("{[any] f1}","{[void] f1}"); }
	@Test public void test_6802() { checkNotSubtype("{[any] f1}","{[void] f2}"); }
	@Test public void test_6803() { checkIsSubtype("{[any] f1}","{[void] f1,void f2}"); }
	@Test public void test_6804() { checkIsSubtype("{[any] f1}","{[void] f2,void f3}"); }
	@Test public void test_6805() { checkIsSubtype("{[any] f1}","{[any] f1}"); }
	@Test public void test_6806() { checkNotSubtype("{[any] f1}","{[any] f2}"); }
	@Test public void test_6807() { checkNotSubtype("{[any] f1}","{[any] f1,any f2}"); }
	@Test public void test_6808() { checkNotSubtype("{[any] f1}","{[any] f2,any f3}"); }
	@Test public void test_6809() { checkIsSubtype("{[any] f1}","{[null] f1}"); }
	@Test public void test_6810() { checkNotSubtype("{[any] f1}","{[null] f2}"); }
	@Test public void test_6811() { checkNotSubtype("{[any] f1}","{[null] f1,null f2}"); }
	@Test public void test_6812() { checkNotSubtype("{[any] f1}","{[null] f2,null f3}"); }
	@Test public void test_6813() { checkIsSubtype("{[any] f1}","{[int] f1}"); }
	@Test public void test_6814() { checkNotSubtype("{[any] f1}","{[int] f2}"); }
	@Test public void test_6815() { checkNotSubtype("{[any] f1}","{[int] f1,int f2}"); }
	@Test public void test_6816() { checkNotSubtype("{[any] f1}","{[int] f2,int f3}"); }
	@Test public void test_6817() { checkIsSubtype("{[any] f1}","{{void f1} f1}"); }
	@Test public void test_6818() { checkIsSubtype("{[any] f1}","{{void f2} f1}"); }
	@Test public void test_6819() { checkIsSubtype("{[any] f1}","{{void f1} f2}"); }
	@Test public void test_6820() { checkIsSubtype("{[any] f1}","{{void f2} f2}"); }
	@Test public void test_6821() { checkIsSubtype("{[any] f1}","{{void f1} f1,void f2}"); }
	@Test public void test_6822() { checkIsSubtype("{[any] f1}","{{void f2} f1,void f2}"); }
	@Test public void test_6823() { checkIsSubtype("{[any] f1}","{{void f1} f2,void f3}"); }
	@Test public void test_6824() { checkIsSubtype("{[any] f1}","{{void f2} f2,void f3}"); }
	@Test public void test_6825() { checkNotSubtype("{[any] f1}","{{any f1} f1}"); }
	@Test public void test_6826() { checkNotSubtype("{[any] f1}","{{any f2} f1}"); }
	@Test public void test_6827() { checkNotSubtype("{[any] f1}","{{any f1} f2}"); }
	@Test public void test_6828() { checkNotSubtype("{[any] f1}","{{any f2} f2}"); }
	@Test public void test_6829() { checkNotSubtype("{[any] f1}","{{any f1} f1,any f2}"); }
	@Test public void test_6830() { checkNotSubtype("{[any] f1}","{{any f2} f1,any f2}"); }
	@Test public void test_6831() { checkNotSubtype("{[any] f1}","{{any f1} f2,any f3}"); }
	@Test public void test_6832() { checkNotSubtype("{[any] f1}","{{any f2} f2,any f3}"); }
	@Test public void test_6833() { checkNotSubtype("{[any] f1}","{{null f1} f1}"); }
	@Test public void test_6834() { checkNotSubtype("{[any] f1}","{{null f2} f1}"); }
	@Test public void test_6835() { checkNotSubtype("{[any] f1}","{{null f1} f2}"); }
	@Test public void test_6836() { checkNotSubtype("{[any] f1}","{{null f2} f2}"); }
	@Test public void test_6837() { checkNotSubtype("{[any] f1}","{{null f1} f1,null f2}"); }
	@Test public void test_6838() { checkNotSubtype("{[any] f1}","{{null f2} f1,null f2}"); }
	@Test public void test_6839() { checkNotSubtype("{[any] f1}","{{null f1} f2,null f3}"); }
	@Test public void test_6840() { checkNotSubtype("{[any] f1}","{{null f2} f2,null f3}"); }
	@Test public void test_6841() { checkNotSubtype("{[any] f1}","{{int f1} f1}"); }
	@Test public void test_6842() { checkNotSubtype("{[any] f1}","{{int f2} f1}"); }
	@Test public void test_6843() { checkNotSubtype("{[any] f1}","{{int f1} f2}"); }
	@Test public void test_6844() { checkNotSubtype("{[any] f1}","{{int f2} f2}"); }
	@Test public void test_6845() { checkNotSubtype("{[any] f1}","{{int f1} f1,int f2}"); }
	@Test public void test_6846() { checkNotSubtype("{[any] f1}","{{int f2} f1,int f2}"); }
	@Test public void test_6847() { checkNotSubtype("{[any] f1}","{{int f1} f2,int f3}"); }
	@Test public void test_6848() { checkNotSubtype("{[any] f1}","{{int f2} f2,int f3}"); }
	@Test public void test_6849() { checkNotSubtype("{[any] f2}","any"); }
	@Test public void test_6850() { checkNotSubtype("{[any] f2}","null"); }
	@Test public void test_6851() { checkNotSubtype("{[any] f2}","int"); }
	@Test public void test_6852() { checkNotSubtype("{[any] f2}","[void]"); }
	@Test public void test_6853() { checkNotSubtype("{[any] f2}","[any]"); }
	@Test public void test_6854() { checkNotSubtype("{[any] f2}","[null]"); }
	@Test public void test_6855() { checkNotSubtype("{[any] f2}","[int]"); }
	@Test public void test_6856() { checkIsSubtype("{[any] f2}","{void f1}"); }
	@Test public void test_6857() { checkIsSubtype("{[any] f2}","{void f2}"); }
	@Test public void test_6858() { checkNotSubtype("{[any] f2}","{any f1}"); }
	@Test public void test_6859() { checkNotSubtype("{[any] f2}","{any f2}"); }
	@Test public void test_6860() { checkNotSubtype("{[any] f2}","{null f1}"); }
	@Test public void test_6861() { checkNotSubtype("{[any] f2}","{null f2}"); }
	@Test public void test_6862() { checkNotSubtype("{[any] f2}","{int f1}"); }
	@Test public void test_6863() { checkNotSubtype("{[any] f2}","{int f2}"); }
	@Test public void test_6864() { checkNotSubtype("{[any] f2}","[[void]]"); }
	@Test public void test_6865() { checkNotSubtype("{[any] f2}","[[any]]"); }
	@Test public void test_6866() { checkNotSubtype("{[any] f2}","[[null]]"); }
	@Test public void test_6867() { checkNotSubtype("{[any] f2}","[[int]]"); }
	@Test public void test_6868() { checkNotSubtype("{[any] f2}","[{void f1}]"); }
	@Test public void test_6869() { checkNotSubtype("{[any] f2}","[{void f2}]"); }
	@Test public void test_6870() { checkNotSubtype("{[any] f2}","[{any f1}]"); }
	@Test public void test_6871() { checkNotSubtype("{[any] f2}","[{any f2}]"); }
	@Test public void test_6872() { checkNotSubtype("{[any] f2}","[{null f1}]"); }
	@Test public void test_6873() { checkNotSubtype("{[any] f2}","[{null f2}]"); }
	@Test public void test_6874() { checkNotSubtype("{[any] f2}","[{int f1}]"); }
	@Test public void test_6875() { checkNotSubtype("{[any] f2}","[{int f2}]"); }
	@Test public void test_6876() { checkIsSubtype("{[any] f2}","{void f1,void f2}"); }
	@Test public void test_6877() { checkIsSubtype("{[any] f2}","{void f2,void f3}"); }
	@Test public void test_6878() { checkIsSubtype("{[any] f2}","{void f1,any f2}"); }
	@Test public void test_6879() { checkIsSubtype("{[any] f2}","{void f2,any f3}"); }
	@Test public void test_6880() { checkIsSubtype("{[any] f2}","{void f1,null f2}"); }
	@Test public void test_6881() { checkIsSubtype("{[any] f2}","{void f2,null f3}"); }
	@Test public void test_6882() { checkIsSubtype("{[any] f2}","{void f1,int f2}"); }
	@Test public void test_6883() { checkIsSubtype("{[any] f2}","{void f2,int f3}"); }
	@Test public void test_6884() { checkIsSubtype("{[any] f2}","{any f1,void f2}"); }
	@Test public void test_6885() { checkIsSubtype("{[any] f2}","{any f2,void f3}"); }
	@Test public void test_6886() { checkNotSubtype("{[any] f2}","{any f1,any f2}"); }
	@Test public void test_6887() { checkNotSubtype("{[any] f2}","{any f2,any f3}"); }
	@Test public void test_6888() { checkNotSubtype("{[any] f2}","{any f1,null f2}"); }
	@Test public void test_6889() { checkNotSubtype("{[any] f2}","{any f2,null f3}"); }
	@Test public void test_6890() { checkNotSubtype("{[any] f2}","{any f1,int f2}"); }
	@Test public void test_6891() { checkNotSubtype("{[any] f2}","{any f2,int f3}"); }
	@Test public void test_6892() { checkIsSubtype("{[any] f2}","{null f1,void f2}"); }
	@Test public void test_6893() { checkIsSubtype("{[any] f2}","{null f2,void f3}"); }
	@Test public void test_6894() { checkNotSubtype("{[any] f2}","{null f1,any f2}"); }
	@Test public void test_6895() { checkNotSubtype("{[any] f2}","{null f2,any f3}"); }
	@Test public void test_6896() { checkNotSubtype("{[any] f2}","{null f1,null f2}"); }
	@Test public void test_6897() { checkNotSubtype("{[any] f2}","{null f2,null f3}"); }
	@Test public void test_6898() { checkNotSubtype("{[any] f2}","{null f1,int f2}"); }
	@Test public void test_6899() { checkNotSubtype("{[any] f2}","{null f2,int f3}"); }
	@Test public void test_6900() { checkIsSubtype("{[any] f2}","{int f1,void f2}"); }
	@Test public void test_6901() { checkIsSubtype("{[any] f2}","{int f2,void f3}"); }
	@Test public void test_6902() { checkNotSubtype("{[any] f2}","{int f1,any f2}"); }
	@Test public void test_6903() { checkNotSubtype("{[any] f2}","{int f2,any f3}"); }
	@Test public void test_6904() { checkNotSubtype("{[any] f2}","{int f1,null f2}"); }
	@Test public void test_6905() { checkNotSubtype("{[any] f2}","{int f2,null f3}"); }
	@Test public void test_6906() { checkNotSubtype("{[any] f2}","{int f1,int f2}"); }
	@Test public void test_6907() { checkNotSubtype("{[any] f2}","{int f2,int f3}"); }
	@Test public void test_6908() { checkNotSubtype("{[any] f2}","{[void] f1}"); }
	@Test public void test_6909() { checkIsSubtype("{[any] f2}","{[void] f2}"); }
	@Test public void test_6910() { checkIsSubtype("{[any] f2}","{[void] f1,void f2}"); }
	@Test public void test_6911() { checkIsSubtype("{[any] f2}","{[void] f2,void f3}"); }
	@Test public void test_6912() { checkNotSubtype("{[any] f2}","{[any] f1}"); }
	@Test public void test_6913() { checkIsSubtype("{[any] f2}","{[any] f2}"); }
	@Test public void test_6914() { checkNotSubtype("{[any] f2}","{[any] f1,any f2}"); }
	@Test public void test_6915() { checkNotSubtype("{[any] f2}","{[any] f2,any f3}"); }
	@Test public void test_6916() { checkNotSubtype("{[any] f2}","{[null] f1}"); }
	@Test public void test_6917() { checkIsSubtype("{[any] f2}","{[null] f2}"); }
	@Test public void test_6918() { checkNotSubtype("{[any] f2}","{[null] f1,null f2}"); }
	@Test public void test_6919() { checkNotSubtype("{[any] f2}","{[null] f2,null f3}"); }
	@Test public void test_6920() { checkNotSubtype("{[any] f2}","{[int] f1}"); }
	@Test public void test_6921() { checkIsSubtype("{[any] f2}","{[int] f2}"); }
	@Test public void test_6922() { checkNotSubtype("{[any] f2}","{[int] f1,int f2}"); }
	@Test public void test_6923() { checkNotSubtype("{[any] f2}","{[int] f2,int f3}"); }
	@Test public void test_6924() { checkIsSubtype("{[any] f2}","{{void f1} f1}"); }
	@Test public void test_6925() { checkIsSubtype("{[any] f2}","{{void f2} f1}"); }
	@Test public void test_6926() { checkIsSubtype("{[any] f2}","{{void f1} f2}"); }
	@Test public void test_6927() { checkIsSubtype("{[any] f2}","{{void f2} f2}"); }
	@Test public void test_6928() { checkIsSubtype("{[any] f2}","{{void f1} f1,void f2}"); }
	@Test public void test_6929() { checkIsSubtype("{[any] f2}","{{void f2} f1,void f2}"); }
	@Test public void test_6930() { checkIsSubtype("{[any] f2}","{{void f1} f2,void f3}"); }
	@Test public void test_6931() { checkIsSubtype("{[any] f2}","{{void f2} f2,void f3}"); }
	@Test public void test_6932() { checkNotSubtype("{[any] f2}","{{any f1} f1}"); }
	@Test public void test_6933() { checkNotSubtype("{[any] f2}","{{any f2} f1}"); }
	@Test public void test_6934() { checkNotSubtype("{[any] f2}","{{any f1} f2}"); }
	@Test public void test_6935() { checkNotSubtype("{[any] f2}","{{any f2} f2}"); }
	@Test public void test_6936() { checkNotSubtype("{[any] f2}","{{any f1} f1,any f2}"); }
	@Test public void test_6937() { checkNotSubtype("{[any] f2}","{{any f2} f1,any f2}"); }
	@Test public void test_6938() { checkNotSubtype("{[any] f2}","{{any f1} f2,any f3}"); }
	@Test public void test_6939() { checkNotSubtype("{[any] f2}","{{any f2} f2,any f3}"); }
	@Test public void test_6940() { checkNotSubtype("{[any] f2}","{{null f1} f1}"); }
	@Test public void test_6941() { checkNotSubtype("{[any] f2}","{{null f2} f1}"); }
	@Test public void test_6942() { checkNotSubtype("{[any] f2}","{{null f1} f2}"); }
	@Test public void test_6943() { checkNotSubtype("{[any] f2}","{{null f2} f2}"); }
	@Test public void test_6944() { checkNotSubtype("{[any] f2}","{{null f1} f1,null f2}"); }
	@Test public void test_6945() { checkNotSubtype("{[any] f2}","{{null f2} f1,null f2}"); }
	@Test public void test_6946() { checkNotSubtype("{[any] f2}","{{null f1} f2,null f3}"); }
	@Test public void test_6947() { checkNotSubtype("{[any] f2}","{{null f2} f2,null f3}"); }
	@Test public void test_6948() { checkNotSubtype("{[any] f2}","{{int f1} f1}"); }
	@Test public void test_6949() { checkNotSubtype("{[any] f2}","{{int f2} f1}"); }
	@Test public void test_6950() { checkNotSubtype("{[any] f2}","{{int f1} f2}"); }
	@Test public void test_6951() { checkNotSubtype("{[any] f2}","{{int f2} f2}"); }
	@Test public void test_6952() { checkNotSubtype("{[any] f2}","{{int f1} f1,int f2}"); }
	@Test public void test_6953() { checkNotSubtype("{[any] f2}","{{int f2} f1,int f2}"); }
	@Test public void test_6954() { checkNotSubtype("{[any] f2}","{{int f1} f2,int f3}"); }
	@Test public void test_6955() { checkNotSubtype("{[any] f2}","{{int f2} f2,int f3}"); }
	@Test public void test_6956() { checkNotSubtype("{[any] f1,any f2}","any"); }
	@Test public void test_6957() { checkNotSubtype("{[any] f1,any f2}","null"); }
	@Test public void test_6958() { checkNotSubtype("{[any] f1,any f2}","int"); }
	@Test public void test_6959() { checkNotSubtype("{[any] f1,any f2}","[void]"); }
	@Test public void test_6960() { checkNotSubtype("{[any] f1,any f2}","[any]"); }
	@Test public void test_6961() { checkNotSubtype("{[any] f1,any f2}","[null]"); }
	@Test public void test_6962() { checkNotSubtype("{[any] f1,any f2}","[int]"); }
	@Test public void test_6963() { checkIsSubtype("{[any] f1,any f2}","{void f1}"); }
	@Test public void test_6964() { checkIsSubtype("{[any] f1,any f2}","{void f2}"); }
	@Test public void test_6965() { checkNotSubtype("{[any] f1,any f2}","{any f1}"); }
	@Test public void test_6966() { checkNotSubtype("{[any] f1,any f2}","{any f2}"); }
	@Test public void test_6967() { checkNotSubtype("{[any] f1,any f2}","{null f1}"); }
	@Test public void test_6968() { checkNotSubtype("{[any] f1,any f2}","{null f2}"); }
	@Test public void test_6969() { checkNotSubtype("{[any] f1,any f2}","{int f1}"); }
	@Test public void test_6970() { checkNotSubtype("{[any] f1,any f2}","{int f2}"); }
	@Test public void test_6971() { checkNotSubtype("{[any] f1,any f2}","[[void]]"); }
	@Test public void test_6972() { checkNotSubtype("{[any] f1,any f2}","[[any]]"); }
	@Test public void test_6973() { checkNotSubtype("{[any] f1,any f2}","[[null]]"); }
	@Test public void test_6974() { checkNotSubtype("{[any] f1,any f2}","[[int]]"); }
	@Test public void test_6975() { checkNotSubtype("{[any] f1,any f2}","[{void f1}]"); }
	@Test public void test_6976() { checkNotSubtype("{[any] f1,any f2}","[{void f2}]"); }
	@Test public void test_6977() { checkNotSubtype("{[any] f1,any f2}","[{any f1}]"); }
	@Test public void test_6978() { checkNotSubtype("{[any] f1,any f2}","[{any f2}]"); }
	@Test public void test_6979() { checkNotSubtype("{[any] f1,any f2}","[{null f1}]"); }
	@Test public void test_6980() { checkNotSubtype("{[any] f1,any f2}","[{null f2}]"); }
	@Test public void test_6981() { checkNotSubtype("{[any] f1,any f2}","[{int f1}]"); }
	@Test public void test_6982() { checkNotSubtype("{[any] f1,any f2}","[{int f2}]"); }
	@Test public void test_6983() { checkIsSubtype("{[any] f1,any f2}","{void f1,void f2}"); }
	@Test public void test_6984() { checkIsSubtype("{[any] f1,any f2}","{void f2,void f3}"); }
	@Test public void test_6985() { checkIsSubtype("{[any] f1,any f2}","{void f1,any f2}"); }
	@Test public void test_6986() { checkIsSubtype("{[any] f1,any f2}","{void f2,any f3}"); }
	@Test public void test_6987() { checkIsSubtype("{[any] f1,any f2}","{void f1,null f2}"); }
	@Test public void test_6988() { checkIsSubtype("{[any] f1,any f2}","{void f2,null f3}"); }
	@Test public void test_6989() { checkIsSubtype("{[any] f1,any f2}","{void f1,int f2}"); }
	@Test public void test_6990() { checkIsSubtype("{[any] f1,any f2}","{void f2,int f3}"); }
	@Test public void test_6991() { checkIsSubtype("{[any] f1,any f2}","{any f1,void f2}"); }
	@Test public void test_6992() { checkIsSubtype("{[any] f1,any f2}","{any f2,void f3}"); }
	@Test public void test_6993() { checkNotSubtype("{[any] f1,any f2}","{any f1,any f2}"); }
	@Test public void test_6994() { checkNotSubtype("{[any] f1,any f2}","{any f2,any f3}"); }
	@Test public void test_6995() { checkNotSubtype("{[any] f1,any f2}","{any f1,null f2}"); }
	@Test public void test_6996() { checkNotSubtype("{[any] f1,any f2}","{any f2,null f3}"); }
	@Test public void test_6997() { checkNotSubtype("{[any] f1,any f2}","{any f1,int f2}"); }
	@Test public void test_6998() { checkNotSubtype("{[any] f1,any f2}","{any f2,int f3}"); }
	@Test public void test_6999() { checkIsSubtype("{[any] f1,any f2}","{null f1,void f2}"); }
	@Test public void test_7000() { checkIsSubtype("{[any] f1,any f2}","{null f2,void f3}"); }
	@Test public void test_7001() { checkNotSubtype("{[any] f1,any f2}","{null f1,any f2}"); }
	@Test public void test_7002() { checkNotSubtype("{[any] f1,any f2}","{null f2,any f3}"); }
	@Test public void test_7003() { checkNotSubtype("{[any] f1,any f2}","{null f1,null f2}"); }
	@Test public void test_7004() { checkNotSubtype("{[any] f1,any f2}","{null f2,null f3}"); }
	@Test public void test_7005() { checkNotSubtype("{[any] f1,any f2}","{null f1,int f2}"); }
	@Test public void test_7006() { checkNotSubtype("{[any] f1,any f2}","{null f2,int f3}"); }
	@Test public void test_7007() { checkIsSubtype("{[any] f1,any f2}","{int f1,void f2}"); }
	@Test public void test_7008() { checkIsSubtype("{[any] f1,any f2}","{int f2,void f3}"); }
	@Test public void test_7009() { checkNotSubtype("{[any] f1,any f2}","{int f1,any f2}"); }
	@Test public void test_7010() { checkNotSubtype("{[any] f1,any f2}","{int f2,any f3}"); }
	@Test public void test_7011() { checkNotSubtype("{[any] f1,any f2}","{int f1,null f2}"); }
	@Test public void test_7012() { checkNotSubtype("{[any] f1,any f2}","{int f2,null f3}"); }
	@Test public void test_7013() { checkNotSubtype("{[any] f1,any f2}","{int f1,int f2}"); }
	@Test public void test_7014() { checkNotSubtype("{[any] f1,any f2}","{int f2,int f3}"); }
	@Test public void test_7015() { checkNotSubtype("{[any] f1,any f2}","{[void] f1}"); }
	@Test public void test_7016() { checkNotSubtype("{[any] f1,any f2}","{[void] f2}"); }
	@Test public void test_7017() { checkIsSubtype("{[any] f1,any f2}","{[void] f1,void f2}"); }
	@Test public void test_7018() { checkIsSubtype("{[any] f1,any f2}","{[void] f2,void f3}"); }
	@Test public void test_7019() { checkNotSubtype("{[any] f1,any f2}","{[any] f1}"); }
	@Test public void test_7020() { checkNotSubtype("{[any] f1,any f2}","{[any] f2}"); }
	@Test public void test_7021() { checkIsSubtype("{[any] f1,any f2}","{[any] f1,any f2}"); }
	@Test public void test_7022() { checkNotSubtype("{[any] f1,any f2}","{[any] f2,any f3}"); }
	@Test public void test_7023() { checkNotSubtype("{[any] f1,any f2}","{[null] f1}"); }
	@Test public void test_7024() { checkNotSubtype("{[any] f1,any f2}","{[null] f2}"); }
	@Test public void test_7025() { checkIsSubtype("{[any] f1,any f2}","{[null] f1,null f2}"); }
	@Test public void test_7026() { checkNotSubtype("{[any] f1,any f2}","{[null] f2,null f3}"); }
	@Test public void test_7027() { checkNotSubtype("{[any] f1,any f2}","{[int] f1}"); }
	@Test public void test_7028() { checkNotSubtype("{[any] f1,any f2}","{[int] f2}"); }
	@Test public void test_7029() { checkIsSubtype("{[any] f1,any f2}","{[int] f1,int f2}"); }
	@Test public void test_7030() { checkNotSubtype("{[any] f1,any f2}","{[int] f2,int f3}"); }
	@Test public void test_7031() { checkIsSubtype("{[any] f1,any f2}","{{void f1} f1}"); }
	@Test public void test_7032() { checkIsSubtype("{[any] f1,any f2}","{{void f2} f1}"); }
	@Test public void test_7033() { checkIsSubtype("{[any] f1,any f2}","{{void f1} f2}"); }
	@Test public void test_7034() { checkIsSubtype("{[any] f1,any f2}","{{void f2} f2}"); }
	@Test public void test_7035() { checkIsSubtype("{[any] f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_7036() { checkIsSubtype("{[any] f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_7037() { checkIsSubtype("{[any] f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_7038() { checkIsSubtype("{[any] f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_7039() { checkNotSubtype("{[any] f1,any f2}","{{any f1} f1}"); }
	@Test public void test_7040() { checkNotSubtype("{[any] f1,any f2}","{{any f2} f1}"); }
	@Test public void test_7041() { checkNotSubtype("{[any] f1,any f2}","{{any f1} f2}"); }
	@Test public void test_7042() { checkNotSubtype("{[any] f1,any f2}","{{any f2} f2}"); }
	@Test public void test_7043() { checkNotSubtype("{[any] f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_7044() { checkNotSubtype("{[any] f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_7045() { checkNotSubtype("{[any] f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_7046() { checkNotSubtype("{[any] f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_7047() { checkNotSubtype("{[any] f1,any f2}","{{null f1} f1}"); }
	@Test public void test_7048() { checkNotSubtype("{[any] f1,any f2}","{{null f2} f1}"); }
	@Test public void test_7049() { checkNotSubtype("{[any] f1,any f2}","{{null f1} f2}"); }
	@Test public void test_7050() { checkNotSubtype("{[any] f1,any f2}","{{null f2} f2}"); }
	@Test public void test_7051() { checkNotSubtype("{[any] f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_7052() { checkNotSubtype("{[any] f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_7053() { checkNotSubtype("{[any] f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_7054() { checkNotSubtype("{[any] f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_7055() { checkNotSubtype("{[any] f1,any f2}","{{int f1} f1}"); }
	@Test public void test_7056() { checkNotSubtype("{[any] f1,any f2}","{{int f2} f1}"); }
	@Test public void test_7057() { checkNotSubtype("{[any] f1,any f2}","{{int f1} f2}"); }
	@Test public void test_7058() { checkNotSubtype("{[any] f1,any f2}","{{int f2} f2}"); }
	@Test public void test_7059() { checkNotSubtype("{[any] f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_7060() { checkNotSubtype("{[any] f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_7061() { checkNotSubtype("{[any] f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_7062() { checkNotSubtype("{[any] f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_7063() { checkNotSubtype("{[any] f2,any f3}","any"); }
	@Test public void test_7064() { checkNotSubtype("{[any] f2,any f3}","null"); }
	@Test public void test_7065() { checkNotSubtype("{[any] f2,any f3}","int"); }
	@Test public void test_7066() { checkNotSubtype("{[any] f2,any f3}","[void]"); }
	@Test public void test_7067() { checkNotSubtype("{[any] f2,any f3}","[any]"); }
	@Test public void test_7068() { checkNotSubtype("{[any] f2,any f3}","[null]"); }
	@Test public void test_7069() { checkNotSubtype("{[any] f2,any f3}","[int]"); }
	@Test public void test_7070() { checkIsSubtype("{[any] f2,any f3}","{void f1}"); }
	@Test public void test_7071() { checkIsSubtype("{[any] f2,any f3}","{void f2}"); }
	@Test public void test_7072() { checkNotSubtype("{[any] f2,any f3}","{any f1}"); }
	@Test public void test_7073() { checkNotSubtype("{[any] f2,any f3}","{any f2}"); }
	@Test public void test_7074() { checkNotSubtype("{[any] f2,any f3}","{null f1}"); }
	@Test public void test_7075() { checkNotSubtype("{[any] f2,any f3}","{null f2}"); }
	@Test public void test_7076() { checkNotSubtype("{[any] f2,any f3}","{int f1}"); }
	@Test public void test_7077() { checkNotSubtype("{[any] f2,any f3}","{int f2}"); }
	@Test public void test_7078() { checkNotSubtype("{[any] f2,any f3}","[[void]]"); }
	@Test public void test_7079() { checkNotSubtype("{[any] f2,any f3}","[[any]]"); }
	@Test public void test_7080() { checkNotSubtype("{[any] f2,any f3}","[[null]]"); }
	@Test public void test_7081() { checkNotSubtype("{[any] f2,any f3}","[[int]]"); }
	@Test public void test_7082() { checkNotSubtype("{[any] f2,any f3}","[{void f1}]"); }
	@Test public void test_7083() { checkNotSubtype("{[any] f2,any f3}","[{void f2}]"); }
	@Test public void test_7084() { checkNotSubtype("{[any] f2,any f3}","[{any f1}]"); }
	@Test public void test_7085() { checkNotSubtype("{[any] f2,any f3}","[{any f2}]"); }
	@Test public void test_7086() { checkNotSubtype("{[any] f2,any f3}","[{null f1}]"); }
	@Test public void test_7087() { checkNotSubtype("{[any] f2,any f3}","[{null f2}]"); }
	@Test public void test_7088() { checkNotSubtype("{[any] f2,any f3}","[{int f1}]"); }
	@Test public void test_7089() { checkNotSubtype("{[any] f2,any f3}","[{int f2}]"); }
	@Test public void test_7090() { checkIsSubtype("{[any] f2,any f3}","{void f1,void f2}"); }
	@Test public void test_7091() { checkIsSubtype("{[any] f2,any f3}","{void f2,void f3}"); }
	@Test public void test_7092() { checkIsSubtype("{[any] f2,any f3}","{void f1,any f2}"); }
	@Test public void test_7093() { checkIsSubtype("{[any] f2,any f3}","{void f2,any f3}"); }
	@Test public void test_7094() { checkIsSubtype("{[any] f2,any f3}","{void f1,null f2}"); }
	@Test public void test_7095() { checkIsSubtype("{[any] f2,any f3}","{void f2,null f3}"); }
	@Test public void test_7096() { checkIsSubtype("{[any] f2,any f3}","{void f1,int f2}"); }
	@Test public void test_7097() { checkIsSubtype("{[any] f2,any f3}","{void f2,int f3}"); }
	@Test public void test_7098() { checkIsSubtype("{[any] f2,any f3}","{any f1,void f2}"); }
	@Test public void test_7099() { checkIsSubtype("{[any] f2,any f3}","{any f2,void f3}"); }
	@Test public void test_7100() { checkNotSubtype("{[any] f2,any f3}","{any f1,any f2}"); }
	@Test public void test_7101() { checkNotSubtype("{[any] f2,any f3}","{any f2,any f3}"); }
	@Test public void test_7102() { checkNotSubtype("{[any] f2,any f3}","{any f1,null f2}"); }
	@Test public void test_7103() { checkNotSubtype("{[any] f2,any f3}","{any f2,null f3}"); }
	@Test public void test_7104() { checkNotSubtype("{[any] f2,any f3}","{any f1,int f2}"); }
	@Test public void test_7105() { checkNotSubtype("{[any] f2,any f3}","{any f2,int f3}"); }
	@Test public void test_7106() { checkIsSubtype("{[any] f2,any f3}","{null f1,void f2}"); }
	@Test public void test_7107() { checkIsSubtype("{[any] f2,any f3}","{null f2,void f3}"); }
	@Test public void test_7108() { checkNotSubtype("{[any] f2,any f3}","{null f1,any f2}"); }
	@Test public void test_7109() { checkNotSubtype("{[any] f2,any f3}","{null f2,any f3}"); }
	@Test public void test_7110() { checkNotSubtype("{[any] f2,any f3}","{null f1,null f2}"); }
	@Test public void test_7111() { checkNotSubtype("{[any] f2,any f3}","{null f2,null f3}"); }
	@Test public void test_7112() { checkNotSubtype("{[any] f2,any f3}","{null f1,int f2}"); }
	@Test public void test_7113() { checkNotSubtype("{[any] f2,any f3}","{null f2,int f3}"); }
	@Test public void test_7114() { checkIsSubtype("{[any] f2,any f3}","{int f1,void f2}"); }
	@Test public void test_7115() { checkIsSubtype("{[any] f2,any f3}","{int f2,void f3}"); }
	@Test public void test_7116() { checkNotSubtype("{[any] f2,any f3}","{int f1,any f2}"); }
	@Test public void test_7117() { checkNotSubtype("{[any] f2,any f3}","{int f2,any f3}"); }
	@Test public void test_7118() { checkNotSubtype("{[any] f2,any f3}","{int f1,null f2}"); }
	@Test public void test_7119() { checkNotSubtype("{[any] f2,any f3}","{int f2,null f3}"); }
	@Test public void test_7120() { checkNotSubtype("{[any] f2,any f3}","{int f1,int f2}"); }
	@Test public void test_7121() { checkNotSubtype("{[any] f2,any f3}","{int f2,int f3}"); }
	@Test public void test_7122() { checkNotSubtype("{[any] f2,any f3}","{[void] f1}"); }
	@Test public void test_7123() { checkNotSubtype("{[any] f2,any f3}","{[void] f2}"); }
	@Test public void test_7124() { checkIsSubtype("{[any] f2,any f3}","{[void] f1,void f2}"); }
	@Test public void test_7125() { checkIsSubtype("{[any] f2,any f3}","{[void] f2,void f3}"); }
	@Test public void test_7126() { checkNotSubtype("{[any] f2,any f3}","{[any] f1}"); }
	@Test public void test_7127() { checkNotSubtype("{[any] f2,any f3}","{[any] f2}"); }
	@Test public void test_7128() { checkNotSubtype("{[any] f2,any f3}","{[any] f1,any f2}"); }
	@Test public void test_7129() { checkIsSubtype("{[any] f2,any f3}","{[any] f2,any f3}"); }
	@Test public void test_7130() { checkNotSubtype("{[any] f2,any f3}","{[null] f1}"); }
	@Test public void test_7131() { checkNotSubtype("{[any] f2,any f3}","{[null] f2}"); }
	@Test public void test_7132() { checkNotSubtype("{[any] f2,any f3}","{[null] f1,null f2}"); }
	@Test public void test_7133() { checkIsSubtype("{[any] f2,any f3}","{[null] f2,null f3}"); }
	@Test public void test_7134() { checkNotSubtype("{[any] f2,any f3}","{[int] f1}"); }
	@Test public void test_7135() { checkNotSubtype("{[any] f2,any f3}","{[int] f2}"); }
	@Test public void test_7136() { checkNotSubtype("{[any] f2,any f3}","{[int] f1,int f2}"); }
	@Test public void test_7137() { checkIsSubtype("{[any] f2,any f3}","{[int] f2,int f3}"); }
	@Test public void test_7138() { checkIsSubtype("{[any] f2,any f3}","{{void f1} f1}"); }
	@Test public void test_7139() { checkIsSubtype("{[any] f2,any f3}","{{void f2} f1}"); }
	@Test public void test_7140() { checkIsSubtype("{[any] f2,any f3}","{{void f1} f2}"); }
	@Test public void test_7141() { checkIsSubtype("{[any] f2,any f3}","{{void f2} f2}"); }
	@Test public void test_7142() { checkIsSubtype("{[any] f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_7143() { checkIsSubtype("{[any] f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_7144() { checkIsSubtype("{[any] f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_7145() { checkIsSubtype("{[any] f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_7146() { checkNotSubtype("{[any] f2,any f3}","{{any f1} f1}"); }
	@Test public void test_7147() { checkNotSubtype("{[any] f2,any f3}","{{any f2} f1}"); }
	@Test public void test_7148() { checkNotSubtype("{[any] f2,any f3}","{{any f1} f2}"); }
	@Test public void test_7149() { checkNotSubtype("{[any] f2,any f3}","{{any f2} f2}"); }
	@Test public void test_7150() { checkNotSubtype("{[any] f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_7151() { checkNotSubtype("{[any] f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_7152() { checkNotSubtype("{[any] f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_7153() { checkNotSubtype("{[any] f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_7154() { checkNotSubtype("{[any] f2,any f3}","{{null f1} f1}"); }
	@Test public void test_7155() { checkNotSubtype("{[any] f2,any f3}","{{null f2} f1}"); }
	@Test public void test_7156() { checkNotSubtype("{[any] f2,any f3}","{{null f1} f2}"); }
	@Test public void test_7157() { checkNotSubtype("{[any] f2,any f3}","{{null f2} f2}"); }
	@Test public void test_7158() { checkNotSubtype("{[any] f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_7159() { checkNotSubtype("{[any] f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_7160() { checkNotSubtype("{[any] f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_7161() { checkNotSubtype("{[any] f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_7162() { checkNotSubtype("{[any] f2,any f3}","{{int f1} f1}"); }
	@Test public void test_7163() { checkNotSubtype("{[any] f2,any f3}","{{int f2} f1}"); }
	@Test public void test_7164() { checkNotSubtype("{[any] f2,any f3}","{{int f1} f2}"); }
	@Test public void test_7165() { checkNotSubtype("{[any] f2,any f3}","{{int f2} f2}"); }
	@Test public void test_7166() { checkNotSubtype("{[any] f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_7167() { checkNotSubtype("{[any] f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_7168() { checkNotSubtype("{[any] f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_7169() { checkNotSubtype("{[any] f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_7170() { checkNotSubtype("{[null] f1}","any"); }
	@Test public void test_7171() { checkNotSubtype("{[null] f1}","null"); }
	@Test public void test_7172() { checkNotSubtype("{[null] f1}","int"); }
	@Test public void test_7173() { checkNotSubtype("{[null] f1}","[void]"); }
	@Test public void test_7174() { checkNotSubtype("{[null] f1}","[any]"); }
	@Test public void test_7175() { checkNotSubtype("{[null] f1}","[null]"); }
	@Test public void test_7176() { checkNotSubtype("{[null] f1}","[int]"); }
	@Test public void test_7177() { checkIsSubtype("{[null] f1}","{void f1}"); }
	@Test public void test_7178() { checkIsSubtype("{[null] f1}","{void f2}"); }
	@Test public void test_7179() { checkNotSubtype("{[null] f1}","{any f1}"); }
	@Test public void test_7180() { checkNotSubtype("{[null] f1}","{any f2}"); }
	@Test public void test_7181() { checkNotSubtype("{[null] f1}","{null f1}"); }
	@Test public void test_7182() { checkNotSubtype("{[null] f1}","{null f2}"); }
	@Test public void test_7183() { checkNotSubtype("{[null] f1}","{int f1}"); }
	@Test public void test_7184() { checkNotSubtype("{[null] f1}","{int f2}"); }
	@Test public void test_7185() { checkNotSubtype("{[null] f1}","[[void]]"); }
	@Test public void test_7186() { checkNotSubtype("{[null] f1}","[[any]]"); }
	@Test public void test_7187() { checkNotSubtype("{[null] f1}","[[null]]"); }
	@Test public void test_7188() { checkNotSubtype("{[null] f1}","[[int]]"); }
	@Test public void test_7189() { checkNotSubtype("{[null] f1}","[{void f1}]"); }
	@Test public void test_7190() { checkNotSubtype("{[null] f1}","[{void f2}]"); }
	@Test public void test_7191() { checkNotSubtype("{[null] f1}","[{any f1}]"); }
	@Test public void test_7192() { checkNotSubtype("{[null] f1}","[{any f2}]"); }
	@Test public void test_7193() { checkNotSubtype("{[null] f1}","[{null f1}]"); }
	@Test public void test_7194() { checkNotSubtype("{[null] f1}","[{null f2}]"); }
	@Test public void test_7195() { checkNotSubtype("{[null] f1}","[{int f1}]"); }
	@Test public void test_7196() { checkNotSubtype("{[null] f1}","[{int f2}]"); }
	@Test public void test_7197() { checkIsSubtype("{[null] f1}","{void f1,void f2}"); }
	@Test public void test_7198() { checkIsSubtype("{[null] f1}","{void f2,void f3}"); }
	@Test public void test_7199() { checkIsSubtype("{[null] f1}","{void f1,any f2}"); }
	@Test public void test_7200() { checkIsSubtype("{[null] f1}","{void f2,any f3}"); }
	@Test public void test_7201() { checkIsSubtype("{[null] f1}","{void f1,null f2}"); }
	@Test public void test_7202() { checkIsSubtype("{[null] f1}","{void f2,null f3}"); }
	@Test public void test_7203() { checkIsSubtype("{[null] f1}","{void f1,int f2}"); }
	@Test public void test_7204() { checkIsSubtype("{[null] f1}","{void f2,int f3}"); }
	@Test public void test_7205() { checkIsSubtype("{[null] f1}","{any f1,void f2}"); }
	@Test public void test_7206() { checkIsSubtype("{[null] f1}","{any f2,void f3}"); }
	@Test public void test_7207() { checkNotSubtype("{[null] f1}","{any f1,any f2}"); }
	@Test public void test_7208() { checkNotSubtype("{[null] f1}","{any f2,any f3}"); }
	@Test public void test_7209() { checkNotSubtype("{[null] f1}","{any f1,null f2}"); }
	@Test public void test_7210() { checkNotSubtype("{[null] f1}","{any f2,null f3}"); }
	@Test public void test_7211() { checkNotSubtype("{[null] f1}","{any f1,int f2}"); }
	@Test public void test_7212() { checkNotSubtype("{[null] f1}","{any f2,int f3}"); }
	@Test public void test_7213() { checkIsSubtype("{[null] f1}","{null f1,void f2}"); }
	@Test public void test_7214() { checkIsSubtype("{[null] f1}","{null f2,void f3}"); }
	@Test public void test_7215() { checkNotSubtype("{[null] f1}","{null f1,any f2}"); }
	@Test public void test_7216() { checkNotSubtype("{[null] f1}","{null f2,any f3}"); }
	@Test public void test_7217() { checkNotSubtype("{[null] f1}","{null f1,null f2}"); }
	@Test public void test_7218() { checkNotSubtype("{[null] f1}","{null f2,null f3}"); }
	@Test public void test_7219() { checkNotSubtype("{[null] f1}","{null f1,int f2}"); }
	@Test public void test_7220() { checkNotSubtype("{[null] f1}","{null f2,int f3}"); }
	@Test public void test_7221() { checkIsSubtype("{[null] f1}","{int f1,void f2}"); }
	@Test public void test_7222() { checkIsSubtype("{[null] f1}","{int f2,void f3}"); }
	@Test public void test_7223() { checkNotSubtype("{[null] f1}","{int f1,any f2}"); }
	@Test public void test_7224() { checkNotSubtype("{[null] f1}","{int f2,any f3}"); }
	@Test public void test_7225() { checkNotSubtype("{[null] f1}","{int f1,null f2}"); }
	@Test public void test_7226() { checkNotSubtype("{[null] f1}","{int f2,null f3}"); }
	@Test public void test_7227() { checkNotSubtype("{[null] f1}","{int f1,int f2}"); }
	@Test public void test_7228() { checkNotSubtype("{[null] f1}","{int f2,int f3}"); }
	@Test public void test_7229() { checkIsSubtype("{[null] f1}","{[void] f1}"); }
	@Test public void test_7230() { checkNotSubtype("{[null] f1}","{[void] f2}"); }
	@Test public void test_7231() { checkIsSubtype("{[null] f1}","{[void] f1,void f2}"); }
	@Test public void test_7232() { checkIsSubtype("{[null] f1}","{[void] f2,void f3}"); }
	@Test public void test_7233() { checkNotSubtype("{[null] f1}","{[any] f1}"); }
	@Test public void test_7234() { checkNotSubtype("{[null] f1}","{[any] f2}"); }
	@Test public void test_7235() { checkNotSubtype("{[null] f1}","{[any] f1,any f2}"); }
	@Test public void test_7236() { checkNotSubtype("{[null] f1}","{[any] f2,any f3}"); }
	@Test public void test_7237() { checkIsSubtype("{[null] f1}","{[null] f1}"); }
	@Test public void test_7238() { checkNotSubtype("{[null] f1}","{[null] f2}"); }
	@Test public void test_7239() { checkNotSubtype("{[null] f1}","{[null] f1,null f2}"); }
	@Test public void test_7240() { checkNotSubtype("{[null] f1}","{[null] f2,null f3}"); }
	@Test public void test_7241() { checkNotSubtype("{[null] f1}","{[int] f1}"); }
	@Test public void test_7242() { checkNotSubtype("{[null] f1}","{[int] f2}"); }
	@Test public void test_7243() { checkNotSubtype("{[null] f1}","{[int] f1,int f2}"); }
	@Test public void test_7244() { checkNotSubtype("{[null] f1}","{[int] f2,int f3}"); }
	@Test public void test_7245() { checkIsSubtype("{[null] f1}","{{void f1} f1}"); }
	@Test public void test_7246() { checkIsSubtype("{[null] f1}","{{void f2} f1}"); }
	@Test public void test_7247() { checkIsSubtype("{[null] f1}","{{void f1} f2}"); }
	@Test public void test_7248() { checkIsSubtype("{[null] f1}","{{void f2} f2}"); }
	@Test public void test_7249() { checkIsSubtype("{[null] f1}","{{void f1} f1,void f2}"); }
	@Test public void test_7250() { checkIsSubtype("{[null] f1}","{{void f2} f1,void f2}"); }
	@Test public void test_7251() { checkIsSubtype("{[null] f1}","{{void f1} f2,void f3}"); }
	@Test public void test_7252() { checkIsSubtype("{[null] f1}","{{void f2} f2,void f3}"); }
	@Test public void test_7253() { checkNotSubtype("{[null] f1}","{{any f1} f1}"); }
	@Test public void test_7254() { checkNotSubtype("{[null] f1}","{{any f2} f1}"); }
	@Test public void test_7255() { checkNotSubtype("{[null] f1}","{{any f1} f2}"); }
	@Test public void test_7256() { checkNotSubtype("{[null] f1}","{{any f2} f2}"); }
	@Test public void test_7257() { checkNotSubtype("{[null] f1}","{{any f1} f1,any f2}"); }
	@Test public void test_7258() { checkNotSubtype("{[null] f1}","{{any f2} f1,any f2}"); }
	@Test public void test_7259() { checkNotSubtype("{[null] f1}","{{any f1} f2,any f3}"); }
	@Test public void test_7260() { checkNotSubtype("{[null] f1}","{{any f2} f2,any f3}"); }
	@Test public void test_7261() { checkNotSubtype("{[null] f1}","{{null f1} f1}"); }
	@Test public void test_7262() { checkNotSubtype("{[null] f1}","{{null f2} f1}"); }
	@Test public void test_7263() { checkNotSubtype("{[null] f1}","{{null f1} f2}"); }
	@Test public void test_7264() { checkNotSubtype("{[null] f1}","{{null f2} f2}"); }
	@Test public void test_7265() { checkNotSubtype("{[null] f1}","{{null f1} f1,null f2}"); }
	@Test public void test_7266() { checkNotSubtype("{[null] f1}","{{null f2} f1,null f2}"); }
	@Test public void test_7267() { checkNotSubtype("{[null] f1}","{{null f1} f2,null f3}"); }
	@Test public void test_7268() { checkNotSubtype("{[null] f1}","{{null f2} f2,null f3}"); }
	@Test public void test_7269() { checkNotSubtype("{[null] f1}","{{int f1} f1}"); }
	@Test public void test_7270() { checkNotSubtype("{[null] f1}","{{int f2} f1}"); }
	@Test public void test_7271() { checkNotSubtype("{[null] f1}","{{int f1} f2}"); }
	@Test public void test_7272() { checkNotSubtype("{[null] f1}","{{int f2} f2}"); }
	@Test public void test_7273() { checkNotSubtype("{[null] f1}","{{int f1} f1,int f2}"); }
	@Test public void test_7274() { checkNotSubtype("{[null] f1}","{{int f2} f1,int f2}"); }
	@Test public void test_7275() { checkNotSubtype("{[null] f1}","{{int f1} f2,int f3}"); }
	@Test public void test_7276() { checkNotSubtype("{[null] f1}","{{int f2} f2,int f3}"); }
	@Test public void test_7277() { checkNotSubtype("{[null] f2}","any"); }
	@Test public void test_7278() { checkNotSubtype("{[null] f2}","null"); }
	@Test public void test_7279() { checkNotSubtype("{[null] f2}","int"); }
	@Test public void test_7280() { checkNotSubtype("{[null] f2}","[void]"); }
	@Test public void test_7281() { checkNotSubtype("{[null] f2}","[any]"); }
	@Test public void test_7282() { checkNotSubtype("{[null] f2}","[null]"); }
	@Test public void test_7283() { checkNotSubtype("{[null] f2}","[int]"); }
	@Test public void test_7284() { checkIsSubtype("{[null] f2}","{void f1}"); }
	@Test public void test_7285() { checkIsSubtype("{[null] f2}","{void f2}"); }
	@Test public void test_7286() { checkNotSubtype("{[null] f2}","{any f1}"); }
	@Test public void test_7287() { checkNotSubtype("{[null] f2}","{any f2}"); }
	@Test public void test_7288() { checkNotSubtype("{[null] f2}","{null f1}"); }
	@Test public void test_7289() { checkNotSubtype("{[null] f2}","{null f2}"); }
	@Test public void test_7290() { checkNotSubtype("{[null] f2}","{int f1}"); }
	@Test public void test_7291() { checkNotSubtype("{[null] f2}","{int f2}"); }
	@Test public void test_7292() { checkNotSubtype("{[null] f2}","[[void]]"); }
	@Test public void test_7293() { checkNotSubtype("{[null] f2}","[[any]]"); }
	@Test public void test_7294() { checkNotSubtype("{[null] f2}","[[null]]"); }
	@Test public void test_7295() { checkNotSubtype("{[null] f2}","[[int]]"); }
	@Test public void test_7296() { checkNotSubtype("{[null] f2}","[{void f1}]"); }
	@Test public void test_7297() { checkNotSubtype("{[null] f2}","[{void f2}]"); }
	@Test public void test_7298() { checkNotSubtype("{[null] f2}","[{any f1}]"); }
	@Test public void test_7299() { checkNotSubtype("{[null] f2}","[{any f2}]"); }
	@Test public void test_7300() { checkNotSubtype("{[null] f2}","[{null f1}]"); }
	@Test public void test_7301() { checkNotSubtype("{[null] f2}","[{null f2}]"); }
	@Test public void test_7302() { checkNotSubtype("{[null] f2}","[{int f1}]"); }
	@Test public void test_7303() { checkNotSubtype("{[null] f2}","[{int f2}]"); }
	@Test public void test_7304() { checkIsSubtype("{[null] f2}","{void f1,void f2}"); }
	@Test public void test_7305() { checkIsSubtype("{[null] f2}","{void f2,void f3}"); }
	@Test public void test_7306() { checkIsSubtype("{[null] f2}","{void f1,any f2}"); }
	@Test public void test_7307() { checkIsSubtype("{[null] f2}","{void f2,any f3}"); }
	@Test public void test_7308() { checkIsSubtype("{[null] f2}","{void f1,null f2}"); }
	@Test public void test_7309() { checkIsSubtype("{[null] f2}","{void f2,null f3}"); }
	@Test public void test_7310() { checkIsSubtype("{[null] f2}","{void f1,int f2}"); }
	@Test public void test_7311() { checkIsSubtype("{[null] f2}","{void f2,int f3}"); }
	@Test public void test_7312() { checkIsSubtype("{[null] f2}","{any f1,void f2}"); }
	@Test public void test_7313() { checkIsSubtype("{[null] f2}","{any f2,void f3}"); }
	@Test public void test_7314() { checkNotSubtype("{[null] f2}","{any f1,any f2}"); }
	@Test public void test_7315() { checkNotSubtype("{[null] f2}","{any f2,any f3}"); }
	@Test public void test_7316() { checkNotSubtype("{[null] f2}","{any f1,null f2}"); }
	@Test public void test_7317() { checkNotSubtype("{[null] f2}","{any f2,null f3}"); }
	@Test public void test_7318() { checkNotSubtype("{[null] f2}","{any f1,int f2}"); }
	@Test public void test_7319() { checkNotSubtype("{[null] f2}","{any f2,int f3}"); }
	@Test public void test_7320() { checkIsSubtype("{[null] f2}","{null f1,void f2}"); }
	@Test public void test_7321() { checkIsSubtype("{[null] f2}","{null f2,void f3}"); }
	@Test public void test_7322() { checkNotSubtype("{[null] f2}","{null f1,any f2}"); }
	@Test public void test_7323() { checkNotSubtype("{[null] f2}","{null f2,any f3}"); }
	@Test public void test_7324() { checkNotSubtype("{[null] f2}","{null f1,null f2}"); }
	@Test public void test_7325() { checkNotSubtype("{[null] f2}","{null f2,null f3}"); }
	@Test public void test_7326() { checkNotSubtype("{[null] f2}","{null f1,int f2}"); }
	@Test public void test_7327() { checkNotSubtype("{[null] f2}","{null f2,int f3}"); }
	@Test public void test_7328() { checkIsSubtype("{[null] f2}","{int f1,void f2}"); }
	@Test public void test_7329() { checkIsSubtype("{[null] f2}","{int f2,void f3}"); }
	@Test public void test_7330() { checkNotSubtype("{[null] f2}","{int f1,any f2}"); }
	@Test public void test_7331() { checkNotSubtype("{[null] f2}","{int f2,any f3}"); }
	@Test public void test_7332() { checkNotSubtype("{[null] f2}","{int f1,null f2}"); }
	@Test public void test_7333() { checkNotSubtype("{[null] f2}","{int f2,null f3}"); }
	@Test public void test_7334() { checkNotSubtype("{[null] f2}","{int f1,int f2}"); }
	@Test public void test_7335() { checkNotSubtype("{[null] f2}","{int f2,int f3}"); }
	@Test public void test_7336() { checkNotSubtype("{[null] f2}","{[void] f1}"); }
	@Test public void test_7337() { checkIsSubtype("{[null] f2}","{[void] f2}"); }
	@Test public void test_7338() { checkIsSubtype("{[null] f2}","{[void] f1,void f2}"); }
	@Test public void test_7339() { checkIsSubtype("{[null] f2}","{[void] f2,void f3}"); }
	@Test public void test_7340() { checkNotSubtype("{[null] f2}","{[any] f1}"); }
	@Test public void test_7341() { checkNotSubtype("{[null] f2}","{[any] f2}"); }
	@Test public void test_7342() { checkNotSubtype("{[null] f2}","{[any] f1,any f2}"); }
	@Test public void test_7343() { checkNotSubtype("{[null] f2}","{[any] f2,any f3}"); }
	@Test public void test_7344() { checkNotSubtype("{[null] f2}","{[null] f1}"); }
	@Test public void test_7345() { checkIsSubtype("{[null] f2}","{[null] f2}"); }
	@Test public void test_7346() { checkNotSubtype("{[null] f2}","{[null] f1,null f2}"); }
	@Test public void test_7347() { checkNotSubtype("{[null] f2}","{[null] f2,null f3}"); }
	@Test public void test_7348() { checkNotSubtype("{[null] f2}","{[int] f1}"); }
	@Test public void test_7349() { checkNotSubtype("{[null] f2}","{[int] f2}"); }
	@Test public void test_7350() { checkNotSubtype("{[null] f2}","{[int] f1,int f2}"); }
	@Test public void test_7351() { checkNotSubtype("{[null] f2}","{[int] f2,int f3}"); }
	@Test public void test_7352() { checkIsSubtype("{[null] f2}","{{void f1} f1}"); }
	@Test public void test_7353() { checkIsSubtype("{[null] f2}","{{void f2} f1}"); }
	@Test public void test_7354() { checkIsSubtype("{[null] f2}","{{void f1} f2}"); }
	@Test public void test_7355() { checkIsSubtype("{[null] f2}","{{void f2} f2}"); }
	@Test public void test_7356() { checkIsSubtype("{[null] f2}","{{void f1} f1,void f2}"); }
	@Test public void test_7357() { checkIsSubtype("{[null] f2}","{{void f2} f1,void f2}"); }
	@Test public void test_7358() { checkIsSubtype("{[null] f2}","{{void f1} f2,void f3}"); }
	@Test public void test_7359() { checkIsSubtype("{[null] f2}","{{void f2} f2,void f3}"); }
	@Test public void test_7360() { checkNotSubtype("{[null] f2}","{{any f1} f1}"); }
	@Test public void test_7361() { checkNotSubtype("{[null] f2}","{{any f2} f1}"); }
	@Test public void test_7362() { checkNotSubtype("{[null] f2}","{{any f1} f2}"); }
	@Test public void test_7363() { checkNotSubtype("{[null] f2}","{{any f2} f2}"); }
	@Test public void test_7364() { checkNotSubtype("{[null] f2}","{{any f1} f1,any f2}"); }
	@Test public void test_7365() { checkNotSubtype("{[null] f2}","{{any f2} f1,any f2}"); }
	@Test public void test_7366() { checkNotSubtype("{[null] f2}","{{any f1} f2,any f3}"); }
	@Test public void test_7367() { checkNotSubtype("{[null] f2}","{{any f2} f2,any f3}"); }
	@Test public void test_7368() { checkNotSubtype("{[null] f2}","{{null f1} f1}"); }
	@Test public void test_7369() { checkNotSubtype("{[null] f2}","{{null f2} f1}"); }
	@Test public void test_7370() { checkNotSubtype("{[null] f2}","{{null f1} f2}"); }
	@Test public void test_7371() { checkNotSubtype("{[null] f2}","{{null f2} f2}"); }
	@Test public void test_7372() { checkNotSubtype("{[null] f2}","{{null f1} f1,null f2}"); }
	@Test public void test_7373() { checkNotSubtype("{[null] f2}","{{null f2} f1,null f2}"); }
	@Test public void test_7374() { checkNotSubtype("{[null] f2}","{{null f1} f2,null f3}"); }
	@Test public void test_7375() { checkNotSubtype("{[null] f2}","{{null f2} f2,null f3}"); }
	@Test public void test_7376() { checkNotSubtype("{[null] f2}","{{int f1} f1}"); }
	@Test public void test_7377() { checkNotSubtype("{[null] f2}","{{int f2} f1}"); }
	@Test public void test_7378() { checkNotSubtype("{[null] f2}","{{int f1} f2}"); }
	@Test public void test_7379() { checkNotSubtype("{[null] f2}","{{int f2} f2}"); }
	@Test public void test_7380() { checkNotSubtype("{[null] f2}","{{int f1} f1,int f2}"); }
	@Test public void test_7381() { checkNotSubtype("{[null] f2}","{{int f2} f1,int f2}"); }
	@Test public void test_7382() { checkNotSubtype("{[null] f2}","{{int f1} f2,int f3}"); }
	@Test public void test_7383() { checkNotSubtype("{[null] f2}","{{int f2} f2,int f3}"); }
	@Test public void test_7384() { checkNotSubtype("{[null] f1,null f2}","any"); }
	@Test public void test_7385() { checkNotSubtype("{[null] f1,null f2}","null"); }
	@Test public void test_7386() { checkNotSubtype("{[null] f1,null f2}","int"); }
	@Test public void test_7387() { checkNotSubtype("{[null] f1,null f2}","[void]"); }
	@Test public void test_7388() { checkNotSubtype("{[null] f1,null f2}","[any]"); }
	@Test public void test_7389() { checkNotSubtype("{[null] f1,null f2}","[null]"); }
	@Test public void test_7390() { checkNotSubtype("{[null] f1,null f2}","[int]"); }
	@Test public void test_7391() { checkIsSubtype("{[null] f1,null f2}","{void f1}"); }
	@Test public void test_7392() { checkIsSubtype("{[null] f1,null f2}","{void f2}"); }
	@Test public void test_7393() { checkNotSubtype("{[null] f1,null f2}","{any f1}"); }
	@Test public void test_7394() { checkNotSubtype("{[null] f1,null f2}","{any f2}"); }
	@Test public void test_7395() { checkNotSubtype("{[null] f1,null f2}","{null f1}"); }
	@Test public void test_7396() { checkNotSubtype("{[null] f1,null f2}","{null f2}"); }
	@Test public void test_7397() { checkNotSubtype("{[null] f1,null f2}","{int f1}"); }
	@Test public void test_7398() { checkNotSubtype("{[null] f1,null f2}","{int f2}"); }
	@Test public void test_7399() { checkNotSubtype("{[null] f1,null f2}","[[void]]"); }
	@Test public void test_7400() { checkNotSubtype("{[null] f1,null f2}","[[any]]"); }
	@Test public void test_7401() { checkNotSubtype("{[null] f1,null f2}","[[null]]"); }
	@Test public void test_7402() { checkNotSubtype("{[null] f1,null f2}","[[int]]"); }
	@Test public void test_7403() { checkNotSubtype("{[null] f1,null f2}","[{void f1}]"); }
	@Test public void test_7404() { checkNotSubtype("{[null] f1,null f2}","[{void f2}]"); }
	@Test public void test_7405() { checkNotSubtype("{[null] f1,null f2}","[{any f1}]"); }
	@Test public void test_7406() { checkNotSubtype("{[null] f1,null f2}","[{any f2}]"); }
	@Test public void test_7407() { checkNotSubtype("{[null] f1,null f2}","[{null f1}]"); }
	@Test public void test_7408() { checkNotSubtype("{[null] f1,null f2}","[{null f2}]"); }
	@Test public void test_7409() { checkNotSubtype("{[null] f1,null f2}","[{int f1}]"); }
	@Test public void test_7410() { checkNotSubtype("{[null] f1,null f2}","[{int f2}]"); }
	@Test public void test_7411() { checkIsSubtype("{[null] f1,null f2}","{void f1,void f2}"); }
	@Test public void test_7412() { checkIsSubtype("{[null] f1,null f2}","{void f2,void f3}"); }
	@Test public void test_7413() { checkIsSubtype("{[null] f1,null f2}","{void f1,any f2}"); }
	@Test public void test_7414() { checkIsSubtype("{[null] f1,null f2}","{void f2,any f3}"); }
	@Test public void test_7415() { checkIsSubtype("{[null] f1,null f2}","{void f1,null f2}"); }
	@Test public void test_7416() { checkIsSubtype("{[null] f1,null f2}","{void f2,null f3}"); }
	@Test public void test_7417() { checkIsSubtype("{[null] f1,null f2}","{void f1,int f2}"); }
	@Test public void test_7418() { checkIsSubtype("{[null] f1,null f2}","{void f2,int f3}"); }
	@Test public void test_7419() { checkIsSubtype("{[null] f1,null f2}","{any f1,void f2}"); }
	@Test public void test_7420() { checkIsSubtype("{[null] f1,null f2}","{any f2,void f3}"); }
	@Test public void test_7421() { checkNotSubtype("{[null] f1,null f2}","{any f1,any f2}"); }
	@Test public void test_7422() { checkNotSubtype("{[null] f1,null f2}","{any f2,any f3}"); }
	@Test public void test_7423() { checkNotSubtype("{[null] f1,null f2}","{any f1,null f2}"); }
	@Test public void test_7424() { checkNotSubtype("{[null] f1,null f2}","{any f2,null f3}"); }
	@Test public void test_7425() { checkNotSubtype("{[null] f1,null f2}","{any f1,int f2}"); }
	@Test public void test_7426() { checkNotSubtype("{[null] f1,null f2}","{any f2,int f3}"); }
	@Test public void test_7427() { checkIsSubtype("{[null] f1,null f2}","{null f1,void f2}"); }
	@Test public void test_7428() { checkIsSubtype("{[null] f1,null f2}","{null f2,void f3}"); }
	@Test public void test_7429() { checkNotSubtype("{[null] f1,null f2}","{null f1,any f2}"); }
	@Test public void test_7430() { checkNotSubtype("{[null] f1,null f2}","{null f2,any f3}"); }
	@Test public void test_7431() { checkNotSubtype("{[null] f1,null f2}","{null f1,null f2}"); }
	@Test public void test_7432() { checkNotSubtype("{[null] f1,null f2}","{null f2,null f3}"); }
	@Test public void test_7433() { checkNotSubtype("{[null] f1,null f2}","{null f1,int f2}"); }
	@Test public void test_7434() { checkNotSubtype("{[null] f1,null f2}","{null f2,int f3}"); }
	@Test public void test_7435() { checkIsSubtype("{[null] f1,null f2}","{int f1,void f2}"); }
	@Test public void test_7436() { checkIsSubtype("{[null] f1,null f2}","{int f2,void f3}"); }
	@Test public void test_7437() { checkNotSubtype("{[null] f1,null f2}","{int f1,any f2}"); }
	@Test public void test_7438() { checkNotSubtype("{[null] f1,null f2}","{int f2,any f3}"); }
	@Test public void test_7439() { checkNotSubtype("{[null] f1,null f2}","{int f1,null f2}"); }
	@Test public void test_7440() { checkNotSubtype("{[null] f1,null f2}","{int f2,null f3}"); }
	@Test public void test_7441() { checkNotSubtype("{[null] f1,null f2}","{int f1,int f2}"); }
	@Test public void test_7442() { checkNotSubtype("{[null] f1,null f2}","{int f2,int f3}"); }
	@Test public void test_7443() { checkNotSubtype("{[null] f1,null f2}","{[void] f1}"); }
	@Test public void test_7444() { checkNotSubtype("{[null] f1,null f2}","{[void] f2}"); }
	@Test public void test_7445() { checkIsSubtype("{[null] f1,null f2}","{[void] f1,void f2}"); }
	@Test public void test_7446() { checkIsSubtype("{[null] f1,null f2}","{[void] f2,void f3}"); }
	@Test public void test_7447() { checkNotSubtype("{[null] f1,null f2}","{[any] f1}"); }
	@Test public void test_7448() { checkNotSubtype("{[null] f1,null f2}","{[any] f2}"); }
	@Test public void test_7449() { checkNotSubtype("{[null] f1,null f2}","{[any] f1,any f2}"); }
	@Test public void test_7450() { checkNotSubtype("{[null] f1,null f2}","{[any] f2,any f3}"); }
	@Test public void test_7451() { checkNotSubtype("{[null] f1,null f2}","{[null] f1}"); }
	@Test public void test_7452() { checkNotSubtype("{[null] f1,null f2}","{[null] f2}"); }
	@Test public void test_7453() { checkIsSubtype("{[null] f1,null f2}","{[null] f1,null f2}"); }
	@Test public void test_7454() { checkNotSubtype("{[null] f1,null f2}","{[null] f2,null f3}"); }
	@Test public void test_7455() { checkNotSubtype("{[null] f1,null f2}","{[int] f1}"); }
	@Test public void test_7456() { checkNotSubtype("{[null] f1,null f2}","{[int] f2}"); }
	@Test public void test_7457() { checkNotSubtype("{[null] f1,null f2}","{[int] f1,int f2}"); }
	@Test public void test_7458() { checkNotSubtype("{[null] f1,null f2}","{[int] f2,int f3}"); }
	@Test public void test_7459() { checkIsSubtype("{[null] f1,null f2}","{{void f1} f1}"); }
	@Test public void test_7460() { checkIsSubtype("{[null] f1,null f2}","{{void f2} f1}"); }
	@Test public void test_7461() { checkIsSubtype("{[null] f1,null f2}","{{void f1} f2}"); }
	@Test public void test_7462() { checkIsSubtype("{[null] f1,null f2}","{{void f2} f2}"); }
	@Test public void test_7463() { checkIsSubtype("{[null] f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_7464() { checkIsSubtype("{[null] f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_7465() { checkIsSubtype("{[null] f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_7466() { checkIsSubtype("{[null] f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_7467() { checkNotSubtype("{[null] f1,null f2}","{{any f1} f1}"); }
	@Test public void test_7468() { checkNotSubtype("{[null] f1,null f2}","{{any f2} f1}"); }
	@Test public void test_7469() { checkNotSubtype("{[null] f1,null f2}","{{any f1} f2}"); }
	@Test public void test_7470() { checkNotSubtype("{[null] f1,null f2}","{{any f2} f2}"); }
	@Test public void test_7471() { checkNotSubtype("{[null] f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_7472() { checkNotSubtype("{[null] f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_7473() { checkNotSubtype("{[null] f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_7474() { checkNotSubtype("{[null] f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_7475() { checkNotSubtype("{[null] f1,null f2}","{{null f1} f1}"); }
	@Test public void test_7476() { checkNotSubtype("{[null] f1,null f2}","{{null f2} f1}"); }
	@Test public void test_7477() { checkNotSubtype("{[null] f1,null f2}","{{null f1} f2}"); }
	@Test public void test_7478() { checkNotSubtype("{[null] f1,null f2}","{{null f2} f2}"); }
	@Test public void test_7479() { checkNotSubtype("{[null] f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_7480() { checkNotSubtype("{[null] f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_7481() { checkNotSubtype("{[null] f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_7482() { checkNotSubtype("{[null] f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_7483() { checkNotSubtype("{[null] f1,null f2}","{{int f1} f1}"); }
	@Test public void test_7484() { checkNotSubtype("{[null] f1,null f2}","{{int f2} f1}"); }
	@Test public void test_7485() { checkNotSubtype("{[null] f1,null f2}","{{int f1} f2}"); }
	@Test public void test_7486() { checkNotSubtype("{[null] f1,null f2}","{{int f2} f2}"); }
	@Test public void test_7487() { checkNotSubtype("{[null] f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_7488() { checkNotSubtype("{[null] f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_7489() { checkNotSubtype("{[null] f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_7490() { checkNotSubtype("{[null] f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_7491() { checkNotSubtype("{[null] f2,null f3}","any"); }
	@Test public void test_7492() { checkNotSubtype("{[null] f2,null f3}","null"); }
	@Test public void test_7493() { checkNotSubtype("{[null] f2,null f3}","int"); }
	@Test public void test_7494() { checkNotSubtype("{[null] f2,null f3}","[void]"); }
	@Test public void test_7495() { checkNotSubtype("{[null] f2,null f3}","[any]"); }
	@Test public void test_7496() { checkNotSubtype("{[null] f2,null f3}","[null]"); }
	@Test public void test_7497() { checkNotSubtype("{[null] f2,null f3}","[int]"); }
	@Test public void test_7498() { checkIsSubtype("{[null] f2,null f3}","{void f1}"); }
	@Test public void test_7499() { checkIsSubtype("{[null] f2,null f3}","{void f2}"); }
	@Test public void test_7500() { checkNotSubtype("{[null] f2,null f3}","{any f1}"); }
	@Test public void test_7501() { checkNotSubtype("{[null] f2,null f3}","{any f2}"); }
	@Test public void test_7502() { checkNotSubtype("{[null] f2,null f3}","{null f1}"); }
	@Test public void test_7503() { checkNotSubtype("{[null] f2,null f3}","{null f2}"); }
	@Test public void test_7504() { checkNotSubtype("{[null] f2,null f3}","{int f1}"); }
	@Test public void test_7505() { checkNotSubtype("{[null] f2,null f3}","{int f2}"); }
	@Test public void test_7506() { checkNotSubtype("{[null] f2,null f3}","[[void]]"); }
	@Test public void test_7507() { checkNotSubtype("{[null] f2,null f3}","[[any]]"); }
	@Test public void test_7508() { checkNotSubtype("{[null] f2,null f3}","[[null]]"); }
	@Test public void test_7509() { checkNotSubtype("{[null] f2,null f3}","[[int]]"); }
	@Test public void test_7510() { checkNotSubtype("{[null] f2,null f3}","[{void f1}]"); }
	@Test public void test_7511() { checkNotSubtype("{[null] f2,null f3}","[{void f2}]"); }
	@Test public void test_7512() { checkNotSubtype("{[null] f2,null f3}","[{any f1}]"); }
	@Test public void test_7513() { checkNotSubtype("{[null] f2,null f3}","[{any f2}]"); }
	@Test public void test_7514() { checkNotSubtype("{[null] f2,null f3}","[{null f1}]"); }
	@Test public void test_7515() { checkNotSubtype("{[null] f2,null f3}","[{null f2}]"); }
	@Test public void test_7516() { checkNotSubtype("{[null] f2,null f3}","[{int f1}]"); }
	@Test public void test_7517() { checkNotSubtype("{[null] f2,null f3}","[{int f2}]"); }
	@Test public void test_7518() { checkIsSubtype("{[null] f2,null f3}","{void f1,void f2}"); }
	@Test public void test_7519() { checkIsSubtype("{[null] f2,null f3}","{void f2,void f3}"); }
	@Test public void test_7520() { checkIsSubtype("{[null] f2,null f3}","{void f1,any f2}"); }
	@Test public void test_7521() { checkIsSubtype("{[null] f2,null f3}","{void f2,any f3}"); }
	@Test public void test_7522() { checkIsSubtype("{[null] f2,null f3}","{void f1,null f2}"); }
	@Test public void test_7523() { checkIsSubtype("{[null] f2,null f3}","{void f2,null f3}"); }
	@Test public void test_7524() { checkIsSubtype("{[null] f2,null f3}","{void f1,int f2}"); }
	@Test public void test_7525() { checkIsSubtype("{[null] f2,null f3}","{void f2,int f3}"); }
	@Test public void test_7526() { checkIsSubtype("{[null] f2,null f3}","{any f1,void f2}"); }
	@Test public void test_7527() { checkIsSubtype("{[null] f2,null f3}","{any f2,void f3}"); }
	@Test public void test_7528() { checkNotSubtype("{[null] f2,null f3}","{any f1,any f2}"); }
	@Test public void test_7529() { checkNotSubtype("{[null] f2,null f3}","{any f2,any f3}"); }
	@Test public void test_7530() { checkNotSubtype("{[null] f2,null f3}","{any f1,null f2}"); }
	@Test public void test_7531() { checkNotSubtype("{[null] f2,null f3}","{any f2,null f3}"); }
	@Test public void test_7532() { checkNotSubtype("{[null] f2,null f3}","{any f1,int f2}"); }
	@Test public void test_7533() { checkNotSubtype("{[null] f2,null f3}","{any f2,int f3}"); }
	@Test public void test_7534() { checkIsSubtype("{[null] f2,null f3}","{null f1,void f2}"); }
	@Test public void test_7535() { checkIsSubtype("{[null] f2,null f3}","{null f2,void f3}"); }
	@Test public void test_7536() { checkNotSubtype("{[null] f2,null f3}","{null f1,any f2}"); }
	@Test public void test_7537() { checkNotSubtype("{[null] f2,null f3}","{null f2,any f3}"); }
	@Test public void test_7538() { checkNotSubtype("{[null] f2,null f3}","{null f1,null f2}"); }
	@Test public void test_7539() { checkNotSubtype("{[null] f2,null f3}","{null f2,null f3}"); }
	@Test public void test_7540() { checkNotSubtype("{[null] f2,null f3}","{null f1,int f2}"); }
	@Test public void test_7541() { checkNotSubtype("{[null] f2,null f3}","{null f2,int f3}"); }
	@Test public void test_7542() { checkIsSubtype("{[null] f2,null f3}","{int f1,void f2}"); }
	@Test public void test_7543() { checkIsSubtype("{[null] f2,null f3}","{int f2,void f3}"); }
	@Test public void test_7544() { checkNotSubtype("{[null] f2,null f3}","{int f1,any f2}"); }
	@Test public void test_7545() { checkNotSubtype("{[null] f2,null f3}","{int f2,any f3}"); }
	@Test public void test_7546() { checkNotSubtype("{[null] f2,null f3}","{int f1,null f2}"); }
	@Test public void test_7547() { checkNotSubtype("{[null] f2,null f3}","{int f2,null f3}"); }
	@Test public void test_7548() { checkNotSubtype("{[null] f2,null f3}","{int f1,int f2}"); }
	@Test public void test_7549() { checkNotSubtype("{[null] f2,null f3}","{int f2,int f3}"); }
	@Test public void test_7550() { checkNotSubtype("{[null] f2,null f3}","{[void] f1}"); }
	@Test public void test_7551() { checkNotSubtype("{[null] f2,null f3}","{[void] f2}"); }
	@Test public void test_7552() { checkIsSubtype("{[null] f2,null f3}","{[void] f1,void f2}"); }
	@Test public void test_7553() { checkIsSubtype("{[null] f2,null f3}","{[void] f2,void f3}"); }
	@Test public void test_7554() { checkNotSubtype("{[null] f2,null f3}","{[any] f1}"); }
	@Test public void test_7555() { checkNotSubtype("{[null] f2,null f3}","{[any] f2}"); }
	@Test public void test_7556() { checkNotSubtype("{[null] f2,null f3}","{[any] f1,any f2}"); }
	@Test public void test_7557() { checkNotSubtype("{[null] f2,null f3}","{[any] f2,any f3}"); }
	@Test public void test_7558() { checkNotSubtype("{[null] f2,null f3}","{[null] f1}"); }
	@Test public void test_7559() { checkNotSubtype("{[null] f2,null f3}","{[null] f2}"); }
	@Test public void test_7560() { checkNotSubtype("{[null] f2,null f3}","{[null] f1,null f2}"); }
	@Test public void test_7561() { checkIsSubtype("{[null] f2,null f3}","{[null] f2,null f3}"); }
	@Test public void test_7562() { checkNotSubtype("{[null] f2,null f3}","{[int] f1}"); }
	@Test public void test_7563() { checkNotSubtype("{[null] f2,null f3}","{[int] f2}"); }
	@Test public void test_7564() { checkNotSubtype("{[null] f2,null f3}","{[int] f1,int f2}"); }
	@Test public void test_7565() { checkNotSubtype("{[null] f2,null f3}","{[int] f2,int f3}"); }
	@Test public void test_7566() { checkIsSubtype("{[null] f2,null f3}","{{void f1} f1}"); }
	@Test public void test_7567() { checkIsSubtype("{[null] f2,null f3}","{{void f2} f1}"); }
	@Test public void test_7568() { checkIsSubtype("{[null] f2,null f3}","{{void f1} f2}"); }
	@Test public void test_7569() { checkIsSubtype("{[null] f2,null f3}","{{void f2} f2}"); }
	@Test public void test_7570() { checkIsSubtype("{[null] f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_7571() { checkIsSubtype("{[null] f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_7572() { checkIsSubtype("{[null] f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_7573() { checkIsSubtype("{[null] f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_7574() { checkNotSubtype("{[null] f2,null f3}","{{any f1} f1}"); }
	@Test public void test_7575() { checkNotSubtype("{[null] f2,null f3}","{{any f2} f1}"); }
	@Test public void test_7576() { checkNotSubtype("{[null] f2,null f3}","{{any f1} f2}"); }
	@Test public void test_7577() { checkNotSubtype("{[null] f2,null f3}","{{any f2} f2}"); }
	@Test public void test_7578() { checkNotSubtype("{[null] f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_7579() { checkNotSubtype("{[null] f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_7580() { checkNotSubtype("{[null] f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_7581() { checkNotSubtype("{[null] f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_7582() { checkNotSubtype("{[null] f2,null f3}","{{null f1} f1}"); }
	@Test public void test_7583() { checkNotSubtype("{[null] f2,null f3}","{{null f2} f1}"); }
	@Test public void test_7584() { checkNotSubtype("{[null] f2,null f3}","{{null f1} f2}"); }
	@Test public void test_7585() { checkNotSubtype("{[null] f2,null f3}","{{null f2} f2}"); }
	@Test public void test_7586() { checkNotSubtype("{[null] f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_7587() { checkNotSubtype("{[null] f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_7588() { checkNotSubtype("{[null] f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_7589() { checkNotSubtype("{[null] f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_7590() { checkNotSubtype("{[null] f2,null f3}","{{int f1} f1}"); }
	@Test public void test_7591() { checkNotSubtype("{[null] f2,null f3}","{{int f2} f1}"); }
	@Test public void test_7592() { checkNotSubtype("{[null] f2,null f3}","{{int f1} f2}"); }
	@Test public void test_7593() { checkNotSubtype("{[null] f2,null f3}","{{int f2} f2}"); }
	@Test public void test_7594() { checkNotSubtype("{[null] f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_7595() { checkNotSubtype("{[null] f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_7596() { checkNotSubtype("{[null] f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_7597() { checkNotSubtype("{[null] f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_7598() { checkNotSubtype("{[int] f1}","any"); }
	@Test public void test_7599() { checkNotSubtype("{[int] f1}","null"); }
	@Test public void test_7600() { checkNotSubtype("{[int] f1}","int"); }
	@Test public void test_7601() { checkNotSubtype("{[int] f1}","[void]"); }
	@Test public void test_7602() { checkNotSubtype("{[int] f1}","[any]"); }
	@Test public void test_7603() { checkNotSubtype("{[int] f1}","[null]"); }
	@Test public void test_7604() { checkNotSubtype("{[int] f1}","[int]"); }
	@Test public void test_7605() { checkIsSubtype("{[int] f1}","{void f1}"); }
	@Test public void test_7606() { checkIsSubtype("{[int] f1}","{void f2}"); }
	@Test public void test_7607() { checkNotSubtype("{[int] f1}","{any f1}"); }
	@Test public void test_7608() { checkNotSubtype("{[int] f1}","{any f2}"); }
	@Test public void test_7609() { checkNotSubtype("{[int] f1}","{null f1}"); }
	@Test public void test_7610() { checkNotSubtype("{[int] f1}","{null f2}"); }
	@Test public void test_7611() { checkNotSubtype("{[int] f1}","{int f1}"); }
	@Test public void test_7612() { checkNotSubtype("{[int] f1}","{int f2}"); }
	@Test public void test_7613() { checkNotSubtype("{[int] f1}","[[void]]"); }
	@Test public void test_7614() { checkNotSubtype("{[int] f1}","[[any]]"); }
	@Test public void test_7615() { checkNotSubtype("{[int] f1}","[[null]]"); }
	@Test public void test_7616() { checkNotSubtype("{[int] f1}","[[int]]"); }
	@Test public void test_7617() { checkNotSubtype("{[int] f1}","[{void f1}]"); }
	@Test public void test_7618() { checkNotSubtype("{[int] f1}","[{void f2}]"); }
	@Test public void test_7619() { checkNotSubtype("{[int] f1}","[{any f1}]"); }
	@Test public void test_7620() { checkNotSubtype("{[int] f1}","[{any f2}]"); }
	@Test public void test_7621() { checkNotSubtype("{[int] f1}","[{null f1}]"); }
	@Test public void test_7622() { checkNotSubtype("{[int] f1}","[{null f2}]"); }
	@Test public void test_7623() { checkNotSubtype("{[int] f1}","[{int f1}]"); }
	@Test public void test_7624() { checkNotSubtype("{[int] f1}","[{int f2}]"); }
	@Test public void test_7625() { checkIsSubtype("{[int] f1}","{void f1,void f2}"); }
	@Test public void test_7626() { checkIsSubtype("{[int] f1}","{void f2,void f3}"); }
	@Test public void test_7627() { checkIsSubtype("{[int] f1}","{void f1,any f2}"); }
	@Test public void test_7628() { checkIsSubtype("{[int] f1}","{void f2,any f3}"); }
	@Test public void test_7629() { checkIsSubtype("{[int] f1}","{void f1,null f2}"); }
	@Test public void test_7630() { checkIsSubtype("{[int] f1}","{void f2,null f3}"); }
	@Test public void test_7631() { checkIsSubtype("{[int] f1}","{void f1,int f2}"); }
	@Test public void test_7632() { checkIsSubtype("{[int] f1}","{void f2,int f3}"); }
	@Test public void test_7633() { checkIsSubtype("{[int] f1}","{any f1,void f2}"); }
	@Test public void test_7634() { checkIsSubtype("{[int] f1}","{any f2,void f3}"); }
	@Test public void test_7635() { checkNotSubtype("{[int] f1}","{any f1,any f2}"); }
	@Test public void test_7636() { checkNotSubtype("{[int] f1}","{any f2,any f3}"); }
	@Test public void test_7637() { checkNotSubtype("{[int] f1}","{any f1,null f2}"); }
	@Test public void test_7638() { checkNotSubtype("{[int] f1}","{any f2,null f3}"); }
	@Test public void test_7639() { checkNotSubtype("{[int] f1}","{any f1,int f2}"); }
	@Test public void test_7640() { checkNotSubtype("{[int] f1}","{any f2,int f3}"); }
	@Test public void test_7641() { checkIsSubtype("{[int] f1}","{null f1,void f2}"); }
	@Test public void test_7642() { checkIsSubtype("{[int] f1}","{null f2,void f3}"); }
	@Test public void test_7643() { checkNotSubtype("{[int] f1}","{null f1,any f2}"); }
	@Test public void test_7644() { checkNotSubtype("{[int] f1}","{null f2,any f3}"); }
	@Test public void test_7645() { checkNotSubtype("{[int] f1}","{null f1,null f2}"); }
	@Test public void test_7646() { checkNotSubtype("{[int] f1}","{null f2,null f3}"); }
	@Test public void test_7647() { checkNotSubtype("{[int] f1}","{null f1,int f2}"); }
	@Test public void test_7648() { checkNotSubtype("{[int] f1}","{null f2,int f3}"); }
	@Test public void test_7649() { checkIsSubtype("{[int] f1}","{int f1,void f2}"); }
	@Test public void test_7650() { checkIsSubtype("{[int] f1}","{int f2,void f3}"); }
	@Test public void test_7651() { checkNotSubtype("{[int] f1}","{int f1,any f2}"); }
	@Test public void test_7652() { checkNotSubtype("{[int] f1}","{int f2,any f3}"); }
	@Test public void test_7653() { checkNotSubtype("{[int] f1}","{int f1,null f2}"); }
	@Test public void test_7654() { checkNotSubtype("{[int] f1}","{int f2,null f3}"); }
	@Test public void test_7655() { checkNotSubtype("{[int] f1}","{int f1,int f2}"); }
	@Test public void test_7656() { checkNotSubtype("{[int] f1}","{int f2,int f3}"); }
	@Test public void test_7657() { checkIsSubtype("{[int] f1}","{[void] f1}"); }
	@Test public void test_7658() { checkNotSubtype("{[int] f1}","{[void] f2}"); }
	@Test public void test_7659() { checkIsSubtype("{[int] f1}","{[void] f1,void f2}"); }
	@Test public void test_7660() { checkIsSubtype("{[int] f1}","{[void] f2,void f3}"); }
	@Test public void test_7661() { checkNotSubtype("{[int] f1}","{[any] f1}"); }
	@Test public void test_7662() { checkNotSubtype("{[int] f1}","{[any] f2}"); }
	@Test public void test_7663() { checkNotSubtype("{[int] f1}","{[any] f1,any f2}"); }
	@Test public void test_7664() { checkNotSubtype("{[int] f1}","{[any] f2,any f3}"); }
	@Test public void test_7665() { checkNotSubtype("{[int] f1}","{[null] f1}"); }
	@Test public void test_7666() { checkNotSubtype("{[int] f1}","{[null] f2}"); }
	@Test public void test_7667() { checkNotSubtype("{[int] f1}","{[null] f1,null f2}"); }
	@Test public void test_7668() { checkNotSubtype("{[int] f1}","{[null] f2,null f3}"); }
	@Test public void test_7669() { checkIsSubtype("{[int] f1}","{[int] f1}"); }
	@Test public void test_7670() { checkNotSubtype("{[int] f1}","{[int] f2}"); }
	@Test public void test_7671() { checkNotSubtype("{[int] f1}","{[int] f1,int f2}"); }
	@Test public void test_7672() { checkNotSubtype("{[int] f1}","{[int] f2,int f3}"); }
	@Test public void test_7673() { checkIsSubtype("{[int] f1}","{{void f1} f1}"); }
	@Test public void test_7674() { checkIsSubtype("{[int] f1}","{{void f2} f1}"); }
	@Test public void test_7675() { checkIsSubtype("{[int] f1}","{{void f1} f2}"); }
	@Test public void test_7676() { checkIsSubtype("{[int] f1}","{{void f2} f2}"); }
	@Test public void test_7677() { checkIsSubtype("{[int] f1}","{{void f1} f1,void f2}"); }
	@Test public void test_7678() { checkIsSubtype("{[int] f1}","{{void f2} f1,void f2}"); }
	@Test public void test_7679() { checkIsSubtype("{[int] f1}","{{void f1} f2,void f3}"); }
	@Test public void test_7680() { checkIsSubtype("{[int] f1}","{{void f2} f2,void f3}"); }
	@Test public void test_7681() { checkNotSubtype("{[int] f1}","{{any f1} f1}"); }
	@Test public void test_7682() { checkNotSubtype("{[int] f1}","{{any f2} f1}"); }
	@Test public void test_7683() { checkNotSubtype("{[int] f1}","{{any f1} f2}"); }
	@Test public void test_7684() { checkNotSubtype("{[int] f1}","{{any f2} f2}"); }
	@Test public void test_7685() { checkNotSubtype("{[int] f1}","{{any f1} f1,any f2}"); }
	@Test public void test_7686() { checkNotSubtype("{[int] f1}","{{any f2} f1,any f2}"); }
	@Test public void test_7687() { checkNotSubtype("{[int] f1}","{{any f1} f2,any f3}"); }
	@Test public void test_7688() { checkNotSubtype("{[int] f1}","{{any f2} f2,any f3}"); }
	@Test public void test_7689() { checkNotSubtype("{[int] f1}","{{null f1} f1}"); }
	@Test public void test_7690() { checkNotSubtype("{[int] f1}","{{null f2} f1}"); }
	@Test public void test_7691() { checkNotSubtype("{[int] f1}","{{null f1} f2}"); }
	@Test public void test_7692() { checkNotSubtype("{[int] f1}","{{null f2} f2}"); }
	@Test public void test_7693() { checkNotSubtype("{[int] f1}","{{null f1} f1,null f2}"); }
	@Test public void test_7694() { checkNotSubtype("{[int] f1}","{{null f2} f1,null f2}"); }
	@Test public void test_7695() { checkNotSubtype("{[int] f1}","{{null f1} f2,null f3}"); }
	@Test public void test_7696() { checkNotSubtype("{[int] f1}","{{null f2} f2,null f3}"); }
	@Test public void test_7697() { checkNotSubtype("{[int] f1}","{{int f1} f1}"); }
	@Test public void test_7698() { checkNotSubtype("{[int] f1}","{{int f2} f1}"); }
	@Test public void test_7699() { checkNotSubtype("{[int] f1}","{{int f1} f2}"); }
	@Test public void test_7700() { checkNotSubtype("{[int] f1}","{{int f2} f2}"); }
	@Test public void test_7701() { checkNotSubtype("{[int] f1}","{{int f1} f1,int f2}"); }
	@Test public void test_7702() { checkNotSubtype("{[int] f1}","{{int f2} f1,int f2}"); }
	@Test public void test_7703() { checkNotSubtype("{[int] f1}","{{int f1} f2,int f3}"); }
	@Test public void test_7704() { checkNotSubtype("{[int] f1}","{{int f2} f2,int f3}"); }
	@Test public void test_7705() { checkNotSubtype("{[int] f2}","any"); }
	@Test public void test_7706() { checkNotSubtype("{[int] f2}","null"); }
	@Test public void test_7707() { checkNotSubtype("{[int] f2}","int"); }
	@Test public void test_7708() { checkNotSubtype("{[int] f2}","[void]"); }
	@Test public void test_7709() { checkNotSubtype("{[int] f2}","[any]"); }
	@Test public void test_7710() { checkNotSubtype("{[int] f2}","[null]"); }
	@Test public void test_7711() { checkNotSubtype("{[int] f2}","[int]"); }
	@Test public void test_7712() { checkIsSubtype("{[int] f2}","{void f1}"); }
	@Test public void test_7713() { checkIsSubtype("{[int] f2}","{void f2}"); }
	@Test public void test_7714() { checkNotSubtype("{[int] f2}","{any f1}"); }
	@Test public void test_7715() { checkNotSubtype("{[int] f2}","{any f2}"); }
	@Test public void test_7716() { checkNotSubtype("{[int] f2}","{null f1}"); }
	@Test public void test_7717() { checkNotSubtype("{[int] f2}","{null f2}"); }
	@Test public void test_7718() { checkNotSubtype("{[int] f2}","{int f1}"); }
	@Test public void test_7719() { checkNotSubtype("{[int] f2}","{int f2}"); }
	@Test public void test_7720() { checkNotSubtype("{[int] f2}","[[void]]"); }
	@Test public void test_7721() { checkNotSubtype("{[int] f2}","[[any]]"); }
	@Test public void test_7722() { checkNotSubtype("{[int] f2}","[[null]]"); }
	@Test public void test_7723() { checkNotSubtype("{[int] f2}","[[int]]"); }
	@Test public void test_7724() { checkNotSubtype("{[int] f2}","[{void f1}]"); }
	@Test public void test_7725() { checkNotSubtype("{[int] f2}","[{void f2}]"); }
	@Test public void test_7726() { checkNotSubtype("{[int] f2}","[{any f1}]"); }
	@Test public void test_7727() { checkNotSubtype("{[int] f2}","[{any f2}]"); }
	@Test public void test_7728() { checkNotSubtype("{[int] f2}","[{null f1}]"); }
	@Test public void test_7729() { checkNotSubtype("{[int] f2}","[{null f2}]"); }
	@Test public void test_7730() { checkNotSubtype("{[int] f2}","[{int f1}]"); }
	@Test public void test_7731() { checkNotSubtype("{[int] f2}","[{int f2}]"); }
	@Test public void test_7732() { checkIsSubtype("{[int] f2}","{void f1,void f2}"); }
	@Test public void test_7733() { checkIsSubtype("{[int] f2}","{void f2,void f3}"); }
	@Test public void test_7734() { checkIsSubtype("{[int] f2}","{void f1,any f2}"); }
	@Test public void test_7735() { checkIsSubtype("{[int] f2}","{void f2,any f3}"); }
	@Test public void test_7736() { checkIsSubtype("{[int] f2}","{void f1,null f2}"); }
	@Test public void test_7737() { checkIsSubtype("{[int] f2}","{void f2,null f3}"); }
	@Test public void test_7738() { checkIsSubtype("{[int] f2}","{void f1,int f2}"); }
	@Test public void test_7739() { checkIsSubtype("{[int] f2}","{void f2,int f3}"); }
	@Test public void test_7740() { checkIsSubtype("{[int] f2}","{any f1,void f2}"); }
	@Test public void test_7741() { checkIsSubtype("{[int] f2}","{any f2,void f3}"); }
	@Test public void test_7742() { checkNotSubtype("{[int] f2}","{any f1,any f2}"); }
	@Test public void test_7743() { checkNotSubtype("{[int] f2}","{any f2,any f3}"); }
	@Test public void test_7744() { checkNotSubtype("{[int] f2}","{any f1,null f2}"); }
	@Test public void test_7745() { checkNotSubtype("{[int] f2}","{any f2,null f3}"); }
	@Test public void test_7746() { checkNotSubtype("{[int] f2}","{any f1,int f2}"); }
	@Test public void test_7747() { checkNotSubtype("{[int] f2}","{any f2,int f3}"); }
	@Test public void test_7748() { checkIsSubtype("{[int] f2}","{null f1,void f2}"); }
	@Test public void test_7749() { checkIsSubtype("{[int] f2}","{null f2,void f3}"); }
	@Test public void test_7750() { checkNotSubtype("{[int] f2}","{null f1,any f2}"); }
	@Test public void test_7751() { checkNotSubtype("{[int] f2}","{null f2,any f3}"); }
	@Test public void test_7752() { checkNotSubtype("{[int] f2}","{null f1,null f2}"); }
	@Test public void test_7753() { checkNotSubtype("{[int] f2}","{null f2,null f3}"); }
	@Test public void test_7754() { checkNotSubtype("{[int] f2}","{null f1,int f2}"); }
	@Test public void test_7755() { checkNotSubtype("{[int] f2}","{null f2,int f3}"); }
	@Test public void test_7756() { checkIsSubtype("{[int] f2}","{int f1,void f2}"); }
	@Test public void test_7757() { checkIsSubtype("{[int] f2}","{int f2,void f3}"); }
	@Test public void test_7758() { checkNotSubtype("{[int] f2}","{int f1,any f2}"); }
	@Test public void test_7759() { checkNotSubtype("{[int] f2}","{int f2,any f3}"); }
	@Test public void test_7760() { checkNotSubtype("{[int] f2}","{int f1,null f2}"); }
	@Test public void test_7761() { checkNotSubtype("{[int] f2}","{int f2,null f3}"); }
	@Test public void test_7762() { checkNotSubtype("{[int] f2}","{int f1,int f2}"); }
	@Test public void test_7763() { checkNotSubtype("{[int] f2}","{int f2,int f3}"); }
	@Test public void test_7764() { checkNotSubtype("{[int] f2}","{[void] f1}"); }
	@Test public void test_7765() { checkIsSubtype("{[int] f2}","{[void] f2}"); }
	@Test public void test_7766() { checkIsSubtype("{[int] f2}","{[void] f1,void f2}"); }
	@Test public void test_7767() { checkIsSubtype("{[int] f2}","{[void] f2,void f3}"); }
	@Test public void test_7768() { checkNotSubtype("{[int] f2}","{[any] f1}"); }
	@Test public void test_7769() { checkNotSubtype("{[int] f2}","{[any] f2}"); }
	@Test public void test_7770() { checkNotSubtype("{[int] f2}","{[any] f1,any f2}"); }
	@Test public void test_7771() { checkNotSubtype("{[int] f2}","{[any] f2,any f3}"); }
	@Test public void test_7772() { checkNotSubtype("{[int] f2}","{[null] f1}"); }
	@Test public void test_7773() { checkNotSubtype("{[int] f2}","{[null] f2}"); }
	@Test public void test_7774() { checkNotSubtype("{[int] f2}","{[null] f1,null f2}"); }
	@Test public void test_7775() { checkNotSubtype("{[int] f2}","{[null] f2,null f3}"); }
	@Test public void test_7776() { checkNotSubtype("{[int] f2}","{[int] f1}"); }
	@Test public void test_7777() { checkIsSubtype("{[int] f2}","{[int] f2}"); }
	@Test public void test_7778() { checkNotSubtype("{[int] f2}","{[int] f1,int f2}"); }
	@Test public void test_7779() { checkNotSubtype("{[int] f2}","{[int] f2,int f3}"); }
	@Test public void test_7780() { checkIsSubtype("{[int] f2}","{{void f1} f1}"); }
	@Test public void test_7781() { checkIsSubtype("{[int] f2}","{{void f2} f1}"); }
	@Test public void test_7782() { checkIsSubtype("{[int] f2}","{{void f1} f2}"); }
	@Test public void test_7783() { checkIsSubtype("{[int] f2}","{{void f2} f2}"); }
	@Test public void test_7784() { checkIsSubtype("{[int] f2}","{{void f1} f1,void f2}"); }
	@Test public void test_7785() { checkIsSubtype("{[int] f2}","{{void f2} f1,void f2}"); }
	@Test public void test_7786() { checkIsSubtype("{[int] f2}","{{void f1} f2,void f3}"); }
	@Test public void test_7787() { checkIsSubtype("{[int] f2}","{{void f2} f2,void f3}"); }
	@Test public void test_7788() { checkNotSubtype("{[int] f2}","{{any f1} f1}"); }
	@Test public void test_7789() { checkNotSubtype("{[int] f2}","{{any f2} f1}"); }
	@Test public void test_7790() { checkNotSubtype("{[int] f2}","{{any f1} f2}"); }
	@Test public void test_7791() { checkNotSubtype("{[int] f2}","{{any f2} f2}"); }
	@Test public void test_7792() { checkNotSubtype("{[int] f2}","{{any f1} f1,any f2}"); }
	@Test public void test_7793() { checkNotSubtype("{[int] f2}","{{any f2} f1,any f2}"); }
	@Test public void test_7794() { checkNotSubtype("{[int] f2}","{{any f1} f2,any f3}"); }
	@Test public void test_7795() { checkNotSubtype("{[int] f2}","{{any f2} f2,any f3}"); }
	@Test public void test_7796() { checkNotSubtype("{[int] f2}","{{null f1} f1}"); }
	@Test public void test_7797() { checkNotSubtype("{[int] f2}","{{null f2} f1}"); }
	@Test public void test_7798() { checkNotSubtype("{[int] f2}","{{null f1} f2}"); }
	@Test public void test_7799() { checkNotSubtype("{[int] f2}","{{null f2} f2}"); }
	@Test public void test_7800() { checkNotSubtype("{[int] f2}","{{null f1} f1,null f2}"); }
	@Test public void test_7801() { checkNotSubtype("{[int] f2}","{{null f2} f1,null f2}"); }
	@Test public void test_7802() { checkNotSubtype("{[int] f2}","{{null f1} f2,null f3}"); }
	@Test public void test_7803() { checkNotSubtype("{[int] f2}","{{null f2} f2,null f3}"); }
	@Test public void test_7804() { checkNotSubtype("{[int] f2}","{{int f1} f1}"); }
	@Test public void test_7805() { checkNotSubtype("{[int] f2}","{{int f2} f1}"); }
	@Test public void test_7806() { checkNotSubtype("{[int] f2}","{{int f1} f2}"); }
	@Test public void test_7807() { checkNotSubtype("{[int] f2}","{{int f2} f2}"); }
	@Test public void test_7808() { checkNotSubtype("{[int] f2}","{{int f1} f1,int f2}"); }
	@Test public void test_7809() { checkNotSubtype("{[int] f2}","{{int f2} f1,int f2}"); }
	@Test public void test_7810() { checkNotSubtype("{[int] f2}","{{int f1} f2,int f3}"); }
	@Test public void test_7811() { checkNotSubtype("{[int] f2}","{{int f2} f2,int f3}"); }
	@Test public void test_7812() { checkNotSubtype("{[int] f1,int f2}","any"); }
	@Test public void test_7813() { checkNotSubtype("{[int] f1,int f2}","null"); }
	@Test public void test_7814() { checkNotSubtype("{[int] f1,int f2}","int"); }
	@Test public void test_7815() { checkNotSubtype("{[int] f1,int f2}","[void]"); }
	@Test public void test_7816() { checkNotSubtype("{[int] f1,int f2}","[any]"); }
	@Test public void test_7817() { checkNotSubtype("{[int] f1,int f2}","[null]"); }
	@Test public void test_7818() { checkNotSubtype("{[int] f1,int f2}","[int]"); }
	@Test public void test_7819() { checkIsSubtype("{[int] f1,int f2}","{void f1}"); }
	@Test public void test_7820() { checkIsSubtype("{[int] f1,int f2}","{void f2}"); }
	@Test public void test_7821() { checkNotSubtype("{[int] f1,int f2}","{any f1}"); }
	@Test public void test_7822() { checkNotSubtype("{[int] f1,int f2}","{any f2}"); }
	@Test public void test_7823() { checkNotSubtype("{[int] f1,int f2}","{null f1}"); }
	@Test public void test_7824() { checkNotSubtype("{[int] f1,int f2}","{null f2}"); }
	@Test public void test_7825() { checkNotSubtype("{[int] f1,int f2}","{int f1}"); }
	@Test public void test_7826() { checkNotSubtype("{[int] f1,int f2}","{int f2}"); }
	@Test public void test_7827() { checkNotSubtype("{[int] f1,int f2}","[[void]]"); }
	@Test public void test_7828() { checkNotSubtype("{[int] f1,int f2}","[[any]]"); }
	@Test public void test_7829() { checkNotSubtype("{[int] f1,int f2}","[[null]]"); }
	@Test public void test_7830() { checkNotSubtype("{[int] f1,int f2}","[[int]]"); }
	@Test public void test_7831() { checkNotSubtype("{[int] f1,int f2}","[{void f1}]"); }
	@Test public void test_7832() { checkNotSubtype("{[int] f1,int f2}","[{void f2}]"); }
	@Test public void test_7833() { checkNotSubtype("{[int] f1,int f2}","[{any f1}]"); }
	@Test public void test_7834() { checkNotSubtype("{[int] f1,int f2}","[{any f2}]"); }
	@Test public void test_7835() { checkNotSubtype("{[int] f1,int f2}","[{null f1}]"); }
	@Test public void test_7836() { checkNotSubtype("{[int] f1,int f2}","[{null f2}]"); }
	@Test public void test_7837() { checkNotSubtype("{[int] f1,int f2}","[{int f1}]"); }
	@Test public void test_7838() { checkNotSubtype("{[int] f1,int f2}","[{int f2}]"); }
	@Test public void test_7839() { checkIsSubtype("{[int] f1,int f2}","{void f1,void f2}"); }
	@Test public void test_7840() { checkIsSubtype("{[int] f1,int f2}","{void f2,void f3}"); }
	@Test public void test_7841() { checkIsSubtype("{[int] f1,int f2}","{void f1,any f2}"); }
	@Test public void test_7842() { checkIsSubtype("{[int] f1,int f2}","{void f2,any f3}"); }
	@Test public void test_7843() { checkIsSubtype("{[int] f1,int f2}","{void f1,null f2}"); }
	@Test public void test_7844() { checkIsSubtype("{[int] f1,int f2}","{void f2,null f3}"); }
	@Test public void test_7845() { checkIsSubtype("{[int] f1,int f2}","{void f1,int f2}"); }
	@Test public void test_7846() { checkIsSubtype("{[int] f1,int f2}","{void f2,int f3}"); }
	@Test public void test_7847() { checkIsSubtype("{[int] f1,int f2}","{any f1,void f2}"); }
	@Test public void test_7848() { checkIsSubtype("{[int] f1,int f2}","{any f2,void f3}"); }
	@Test public void test_7849() { checkNotSubtype("{[int] f1,int f2}","{any f1,any f2}"); }
	@Test public void test_7850() { checkNotSubtype("{[int] f1,int f2}","{any f2,any f3}"); }
	@Test public void test_7851() { checkNotSubtype("{[int] f1,int f2}","{any f1,null f2}"); }
	@Test public void test_7852() { checkNotSubtype("{[int] f1,int f2}","{any f2,null f3}"); }
	@Test public void test_7853() { checkNotSubtype("{[int] f1,int f2}","{any f1,int f2}"); }
	@Test public void test_7854() { checkNotSubtype("{[int] f1,int f2}","{any f2,int f3}"); }
	@Test public void test_7855() { checkIsSubtype("{[int] f1,int f2}","{null f1,void f2}"); }
	@Test public void test_7856() { checkIsSubtype("{[int] f1,int f2}","{null f2,void f3}"); }
	@Test public void test_7857() { checkNotSubtype("{[int] f1,int f2}","{null f1,any f2}"); }
	@Test public void test_7858() { checkNotSubtype("{[int] f1,int f2}","{null f2,any f3}"); }
	@Test public void test_7859() { checkNotSubtype("{[int] f1,int f2}","{null f1,null f2}"); }
	@Test public void test_7860() { checkNotSubtype("{[int] f1,int f2}","{null f2,null f3}"); }
	@Test public void test_7861() { checkNotSubtype("{[int] f1,int f2}","{null f1,int f2}"); }
	@Test public void test_7862() { checkNotSubtype("{[int] f1,int f2}","{null f2,int f3}"); }
	@Test public void test_7863() { checkIsSubtype("{[int] f1,int f2}","{int f1,void f2}"); }
	@Test public void test_7864() { checkIsSubtype("{[int] f1,int f2}","{int f2,void f3}"); }
	@Test public void test_7865() { checkNotSubtype("{[int] f1,int f2}","{int f1,any f2}"); }
	@Test public void test_7866() { checkNotSubtype("{[int] f1,int f2}","{int f2,any f3}"); }
	@Test public void test_7867() { checkNotSubtype("{[int] f1,int f2}","{int f1,null f2}"); }
	@Test public void test_7868() { checkNotSubtype("{[int] f1,int f2}","{int f2,null f3}"); }
	@Test public void test_7869() { checkNotSubtype("{[int] f1,int f2}","{int f1,int f2}"); }
	@Test public void test_7870() { checkNotSubtype("{[int] f1,int f2}","{int f2,int f3}"); }
	@Test public void test_7871() { checkNotSubtype("{[int] f1,int f2}","{[void] f1}"); }
	@Test public void test_7872() { checkNotSubtype("{[int] f1,int f2}","{[void] f2}"); }
	@Test public void test_7873() { checkIsSubtype("{[int] f1,int f2}","{[void] f1,void f2}"); }
	@Test public void test_7874() { checkIsSubtype("{[int] f1,int f2}","{[void] f2,void f3}"); }
	@Test public void test_7875() { checkNotSubtype("{[int] f1,int f2}","{[any] f1}"); }
	@Test public void test_7876() { checkNotSubtype("{[int] f1,int f2}","{[any] f2}"); }
	@Test public void test_7877() { checkNotSubtype("{[int] f1,int f2}","{[any] f1,any f2}"); }
	@Test public void test_7878() { checkNotSubtype("{[int] f1,int f2}","{[any] f2,any f3}"); }
	@Test public void test_7879() { checkNotSubtype("{[int] f1,int f2}","{[null] f1}"); }
	@Test public void test_7880() { checkNotSubtype("{[int] f1,int f2}","{[null] f2}"); }
	@Test public void test_7881() { checkNotSubtype("{[int] f1,int f2}","{[null] f1,null f2}"); }
	@Test public void test_7882() { checkNotSubtype("{[int] f1,int f2}","{[null] f2,null f3}"); }
	@Test public void test_7883() { checkNotSubtype("{[int] f1,int f2}","{[int] f1}"); }
	@Test public void test_7884() { checkNotSubtype("{[int] f1,int f2}","{[int] f2}"); }
	@Test public void test_7885() { checkIsSubtype("{[int] f1,int f2}","{[int] f1,int f2}"); }
	@Test public void test_7886() { checkNotSubtype("{[int] f1,int f2}","{[int] f2,int f3}"); }
	@Test public void test_7887() { checkIsSubtype("{[int] f1,int f2}","{{void f1} f1}"); }
	@Test public void test_7888() { checkIsSubtype("{[int] f1,int f2}","{{void f2} f1}"); }
	@Test public void test_7889() { checkIsSubtype("{[int] f1,int f2}","{{void f1} f2}"); }
	@Test public void test_7890() { checkIsSubtype("{[int] f1,int f2}","{{void f2} f2}"); }
	@Test public void test_7891() { checkIsSubtype("{[int] f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_7892() { checkIsSubtype("{[int] f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_7893() { checkIsSubtype("{[int] f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_7894() { checkIsSubtype("{[int] f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_7895() { checkNotSubtype("{[int] f1,int f2}","{{any f1} f1}"); }
	@Test public void test_7896() { checkNotSubtype("{[int] f1,int f2}","{{any f2} f1}"); }
	@Test public void test_7897() { checkNotSubtype("{[int] f1,int f2}","{{any f1} f2}"); }
	@Test public void test_7898() { checkNotSubtype("{[int] f1,int f2}","{{any f2} f2}"); }
	@Test public void test_7899() { checkNotSubtype("{[int] f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_7900() { checkNotSubtype("{[int] f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_7901() { checkNotSubtype("{[int] f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_7902() { checkNotSubtype("{[int] f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_7903() { checkNotSubtype("{[int] f1,int f2}","{{null f1} f1}"); }
	@Test public void test_7904() { checkNotSubtype("{[int] f1,int f2}","{{null f2} f1}"); }
	@Test public void test_7905() { checkNotSubtype("{[int] f1,int f2}","{{null f1} f2}"); }
	@Test public void test_7906() { checkNotSubtype("{[int] f1,int f2}","{{null f2} f2}"); }
	@Test public void test_7907() { checkNotSubtype("{[int] f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_7908() { checkNotSubtype("{[int] f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_7909() { checkNotSubtype("{[int] f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_7910() { checkNotSubtype("{[int] f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_7911() { checkNotSubtype("{[int] f1,int f2}","{{int f1} f1}"); }
	@Test public void test_7912() { checkNotSubtype("{[int] f1,int f2}","{{int f2} f1}"); }
	@Test public void test_7913() { checkNotSubtype("{[int] f1,int f2}","{{int f1} f2}"); }
	@Test public void test_7914() { checkNotSubtype("{[int] f1,int f2}","{{int f2} f2}"); }
	@Test public void test_7915() { checkNotSubtype("{[int] f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_7916() { checkNotSubtype("{[int] f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_7917() { checkNotSubtype("{[int] f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_7918() { checkNotSubtype("{[int] f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_7919() { checkNotSubtype("{[int] f2,int f3}","any"); }
	@Test public void test_7920() { checkNotSubtype("{[int] f2,int f3}","null"); }
	@Test public void test_7921() { checkNotSubtype("{[int] f2,int f3}","int"); }
	@Test public void test_7922() { checkNotSubtype("{[int] f2,int f3}","[void]"); }
	@Test public void test_7923() { checkNotSubtype("{[int] f2,int f3}","[any]"); }
	@Test public void test_7924() { checkNotSubtype("{[int] f2,int f3}","[null]"); }
	@Test public void test_7925() { checkNotSubtype("{[int] f2,int f3}","[int]"); }
	@Test public void test_7926() { checkIsSubtype("{[int] f2,int f3}","{void f1}"); }
	@Test public void test_7927() { checkIsSubtype("{[int] f2,int f3}","{void f2}"); }
	@Test public void test_7928() { checkNotSubtype("{[int] f2,int f3}","{any f1}"); }
	@Test public void test_7929() { checkNotSubtype("{[int] f2,int f3}","{any f2}"); }
	@Test public void test_7930() { checkNotSubtype("{[int] f2,int f3}","{null f1}"); }
	@Test public void test_7931() { checkNotSubtype("{[int] f2,int f3}","{null f2}"); }
	@Test public void test_7932() { checkNotSubtype("{[int] f2,int f3}","{int f1}"); }
	@Test public void test_7933() { checkNotSubtype("{[int] f2,int f3}","{int f2}"); }
	@Test public void test_7934() { checkNotSubtype("{[int] f2,int f3}","[[void]]"); }
	@Test public void test_7935() { checkNotSubtype("{[int] f2,int f3}","[[any]]"); }
	@Test public void test_7936() { checkNotSubtype("{[int] f2,int f3}","[[null]]"); }
	@Test public void test_7937() { checkNotSubtype("{[int] f2,int f3}","[[int]]"); }
	@Test public void test_7938() { checkNotSubtype("{[int] f2,int f3}","[{void f1}]"); }
	@Test public void test_7939() { checkNotSubtype("{[int] f2,int f3}","[{void f2}]"); }
	@Test public void test_7940() { checkNotSubtype("{[int] f2,int f3}","[{any f1}]"); }
	@Test public void test_7941() { checkNotSubtype("{[int] f2,int f3}","[{any f2}]"); }
	@Test public void test_7942() { checkNotSubtype("{[int] f2,int f3}","[{null f1}]"); }
	@Test public void test_7943() { checkNotSubtype("{[int] f2,int f3}","[{null f2}]"); }
	@Test public void test_7944() { checkNotSubtype("{[int] f2,int f3}","[{int f1}]"); }
	@Test public void test_7945() { checkNotSubtype("{[int] f2,int f3}","[{int f2}]"); }
	@Test public void test_7946() { checkIsSubtype("{[int] f2,int f3}","{void f1,void f2}"); }
	@Test public void test_7947() { checkIsSubtype("{[int] f2,int f3}","{void f2,void f3}"); }
	@Test public void test_7948() { checkIsSubtype("{[int] f2,int f3}","{void f1,any f2}"); }
	@Test public void test_7949() { checkIsSubtype("{[int] f2,int f3}","{void f2,any f3}"); }
	@Test public void test_7950() { checkIsSubtype("{[int] f2,int f3}","{void f1,null f2}"); }
	@Test public void test_7951() { checkIsSubtype("{[int] f2,int f3}","{void f2,null f3}"); }
	@Test public void test_7952() { checkIsSubtype("{[int] f2,int f3}","{void f1,int f2}"); }
	@Test public void test_7953() { checkIsSubtype("{[int] f2,int f3}","{void f2,int f3}"); }
	@Test public void test_7954() { checkIsSubtype("{[int] f2,int f3}","{any f1,void f2}"); }
	@Test public void test_7955() { checkIsSubtype("{[int] f2,int f3}","{any f2,void f3}"); }
	@Test public void test_7956() { checkNotSubtype("{[int] f2,int f3}","{any f1,any f2}"); }
	@Test public void test_7957() { checkNotSubtype("{[int] f2,int f3}","{any f2,any f3}"); }
	@Test public void test_7958() { checkNotSubtype("{[int] f2,int f3}","{any f1,null f2}"); }
	@Test public void test_7959() { checkNotSubtype("{[int] f2,int f3}","{any f2,null f3}"); }
	@Test public void test_7960() { checkNotSubtype("{[int] f2,int f3}","{any f1,int f2}"); }
	@Test public void test_7961() { checkNotSubtype("{[int] f2,int f3}","{any f2,int f3}"); }
	@Test public void test_7962() { checkIsSubtype("{[int] f2,int f3}","{null f1,void f2}"); }
	@Test public void test_7963() { checkIsSubtype("{[int] f2,int f3}","{null f2,void f3}"); }
	@Test public void test_7964() { checkNotSubtype("{[int] f2,int f3}","{null f1,any f2}"); }
	@Test public void test_7965() { checkNotSubtype("{[int] f2,int f3}","{null f2,any f3}"); }
	@Test public void test_7966() { checkNotSubtype("{[int] f2,int f3}","{null f1,null f2}"); }
	@Test public void test_7967() { checkNotSubtype("{[int] f2,int f3}","{null f2,null f3}"); }
	@Test public void test_7968() { checkNotSubtype("{[int] f2,int f3}","{null f1,int f2}"); }
	@Test public void test_7969() { checkNotSubtype("{[int] f2,int f3}","{null f2,int f3}"); }
	@Test public void test_7970() { checkIsSubtype("{[int] f2,int f3}","{int f1,void f2}"); }
	@Test public void test_7971() { checkIsSubtype("{[int] f2,int f3}","{int f2,void f3}"); }
	@Test public void test_7972() { checkNotSubtype("{[int] f2,int f3}","{int f1,any f2}"); }
	@Test public void test_7973() { checkNotSubtype("{[int] f2,int f3}","{int f2,any f3}"); }
	@Test public void test_7974() { checkNotSubtype("{[int] f2,int f3}","{int f1,null f2}"); }
	@Test public void test_7975() { checkNotSubtype("{[int] f2,int f3}","{int f2,null f3}"); }
	@Test public void test_7976() { checkNotSubtype("{[int] f2,int f3}","{int f1,int f2}"); }
	@Test public void test_7977() { checkNotSubtype("{[int] f2,int f3}","{int f2,int f3}"); }
	@Test public void test_7978() { checkNotSubtype("{[int] f2,int f3}","{[void] f1}"); }
	@Test public void test_7979() { checkNotSubtype("{[int] f2,int f3}","{[void] f2}"); }
	@Test public void test_7980() { checkIsSubtype("{[int] f2,int f3}","{[void] f1,void f2}"); }
	@Test public void test_7981() { checkIsSubtype("{[int] f2,int f3}","{[void] f2,void f3}"); }
	@Test public void test_7982() { checkNotSubtype("{[int] f2,int f3}","{[any] f1}"); }
	@Test public void test_7983() { checkNotSubtype("{[int] f2,int f3}","{[any] f2}"); }
	@Test public void test_7984() { checkNotSubtype("{[int] f2,int f3}","{[any] f1,any f2}"); }
	@Test public void test_7985() { checkNotSubtype("{[int] f2,int f3}","{[any] f2,any f3}"); }
	@Test public void test_7986() { checkNotSubtype("{[int] f2,int f3}","{[null] f1}"); }
	@Test public void test_7987() { checkNotSubtype("{[int] f2,int f3}","{[null] f2}"); }
	@Test public void test_7988() { checkNotSubtype("{[int] f2,int f3}","{[null] f1,null f2}"); }
	@Test public void test_7989() { checkNotSubtype("{[int] f2,int f3}","{[null] f2,null f3}"); }
	@Test public void test_7990() { checkNotSubtype("{[int] f2,int f3}","{[int] f1}"); }
	@Test public void test_7991() { checkNotSubtype("{[int] f2,int f3}","{[int] f2}"); }
	@Test public void test_7992() { checkNotSubtype("{[int] f2,int f3}","{[int] f1,int f2}"); }
	@Test public void test_7993() { checkIsSubtype("{[int] f2,int f3}","{[int] f2,int f3}"); }
	@Test public void test_7994() { checkIsSubtype("{[int] f2,int f3}","{{void f1} f1}"); }
	@Test public void test_7995() { checkIsSubtype("{[int] f2,int f3}","{{void f2} f1}"); }
	@Test public void test_7996() { checkIsSubtype("{[int] f2,int f3}","{{void f1} f2}"); }
	@Test public void test_7997() { checkIsSubtype("{[int] f2,int f3}","{{void f2} f2}"); }
	@Test public void test_7998() { checkIsSubtype("{[int] f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_7999() { checkIsSubtype("{[int] f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_8000() { checkIsSubtype("{[int] f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_8001() { checkIsSubtype("{[int] f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_8002() { checkNotSubtype("{[int] f2,int f3}","{{any f1} f1}"); }
	@Test public void test_8003() { checkNotSubtype("{[int] f2,int f3}","{{any f2} f1}"); }
	@Test public void test_8004() { checkNotSubtype("{[int] f2,int f3}","{{any f1} f2}"); }
	@Test public void test_8005() { checkNotSubtype("{[int] f2,int f3}","{{any f2} f2}"); }
	@Test public void test_8006() { checkNotSubtype("{[int] f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_8007() { checkNotSubtype("{[int] f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_8008() { checkNotSubtype("{[int] f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_8009() { checkNotSubtype("{[int] f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_8010() { checkNotSubtype("{[int] f2,int f3}","{{null f1} f1}"); }
	@Test public void test_8011() { checkNotSubtype("{[int] f2,int f3}","{{null f2} f1}"); }
	@Test public void test_8012() { checkNotSubtype("{[int] f2,int f3}","{{null f1} f2}"); }
	@Test public void test_8013() { checkNotSubtype("{[int] f2,int f3}","{{null f2} f2}"); }
	@Test public void test_8014() { checkNotSubtype("{[int] f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_8015() { checkNotSubtype("{[int] f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_8016() { checkNotSubtype("{[int] f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_8017() { checkNotSubtype("{[int] f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_8018() { checkNotSubtype("{[int] f2,int f3}","{{int f1} f1}"); }
	@Test public void test_8019() { checkNotSubtype("{[int] f2,int f3}","{{int f2} f1}"); }
	@Test public void test_8020() { checkNotSubtype("{[int] f2,int f3}","{{int f1} f2}"); }
	@Test public void test_8021() { checkNotSubtype("{[int] f2,int f3}","{{int f2} f2}"); }
	@Test public void test_8022() { checkNotSubtype("{[int] f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_8023() { checkNotSubtype("{[int] f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_8024() { checkNotSubtype("{[int] f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_8025() { checkNotSubtype("{[int] f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_8026() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_8027() { checkNotSubtype("{{void f1} f1}","null"); }
	@Test public void test_8028() { checkNotSubtype("{{void f1} f1}","int"); }
	@Test public void test_8029() { checkNotSubtype("{{void f1} f1}","[void]"); }
	@Test public void test_8030() { checkNotSubtype("{{void f1} f1}","[any]"); }
	@Test public void test_8031() { checkNotSubtype("{{void f1} f1}","[null]"); }
	@Test public void test_8032() { checkNotSubtype("{{void f1} f1}","[int]"); }
	@Test public void test_8033() { checkIsSubtype("{{void f1} f1}","{void f1}"); }
	@Test public void test_8034() { checkIsSubtype("{{void f1} f1}","{void f2}"); }
	@Test public void test_8035() { checkNotSubtype("{{void f1} f1}","{any f1}"); }
	@Test public void test_8036() { checkNotSubtype("{{void f1} f1}","{any f2}"); }
	@Test public void test_8037() { checkNotSubtype("{{void f1} f1}","{null f1}"); }
	@Test public void test_8038() { checkNotSubtype("{{void f1} f1}","{null f2}"); }
	@Test public void test_8039() { checkNotSubtype("{{void f1} f1}","{int f1}"); }
	@Test public void test_8040() { checkNotSubtype("{{void f1} f1}","{int f2}"); }
	@Test public void test_8041() { checkNotSubtype("{{void f1} f1}","[[void]]"); }
	@Test public void test_8042() { checkNotSubtype("{{void f1} f1}","[[any]]"); }
	@Test public void test_8043() { checkNotSubtype("{{void f1} f1}","[[null]]"); }
	@Test public void test_8044() { checkNotSubtype("{{void f1} f1}","[[int]]"); }
	@Test public void test_8045() { checkNotSubtype("{{void f1} f1}","[{void f1}]"); }
	@Test public void test_8046() { checkNotSubtype("{{void f1} f1}","[{void f2}]"); }
	@Test public void test_8047() { checkNotSubtype("{{void f1} f1}","[{any f1}]"); }
	@Test public void test_8048() { checkNotSubtype("{{void f1} f1}","[{any f2}]"); }
	@Test public void test_8049() { checkNotSubtype("{{void f1} f1}","[{null f1}]"); }
	@Test public void test_8050() { checkNotSubtype("{{void f1} f1}","[{null f2}]"); }
	@Test public void test_8051() { checkNotSubtype("{{void f1} f1}","[{int f1}]"); }
	@Test public void test_8052() { checkNotSubtype("{{void f1} f1}","[{int f2}]"); }
	@Test public void test_8053() { checkIsSubtype("{{void f1} f1}","{void f1,void f2}"); }
	@Test public void test_8054() { checkIsSubtype("{{void f1} f1}","{void f2,void f3}"); }
	@Test public void test_8055() { checkIsSubtype("{{void f1} f1}","{void f1,any f2}"); }
	@Test public void test_8056() { checkIsSubtype("{{void f1} f1}","{void f2,any f3}"); }
	@Test public void test_8057() { checkIsSubtype("{{void f1} f1}","{void f1,null f2}"); }
	@Test public void test_8058() { checkIsSubtype("{{void f1} f1}","{void f2,null f3}"); }
	@Test public void test_8059() { checkIsSubtype("{{void f1} f1}","{void f1,int f2}"); }
	@Test public void test_8060() { checkIsSubtype("{{void f1} f1}","{void f2,int f3}"); }
	@Test public void test_8061() { checkIsSubtype("{{void f1} f1}","{any f1,void f2}"); }
	@Test public void test_8062() { checkIsSubtype("{{void f1} f1}","{any f2,void f3}"); }
	@Test public void test_8063() { checkNotSubtype("{{void f1} f1}","{any f1,any f2}"); }
	@Test public void test_8064() { checkNotSubtype("{{void f1} f1}","{any f2,any f3}"); }
	@Test public void test_8065() { checkNotSubtype("{{void f1} f1}","{any f1,null f2}"); }
	@Test public void test_8066() { checkNotSubtype("{{void f1} f1}","{any f2,null f3}"); }
	@Test public void test_8067() { checkNotSubtype("{{void f1} f1}","{any f1,int f2}"); }
	@Test public void test_8068() { checkNotSubtype("{{void f1} f1}","{any f2,int f3}"); }
	@Test public void test_8069() { checkIsSubtype("{{void f1} f1}","{null f1,void f2}"); }
	@Test public void test_8070() { checkIsSubtype("{{void f1} f1}","{null f2,void f3}"); }
	@Test public void test_8071() { checkNotSubtype("{{void f1} f1}","{null f1,any f2}"); }
	@Test public void test_8072() { checkNotSubtype("{{void f1} f1}","{null f2,any f3}"); }
	@Test public void test_8073() { checkNotSubtype("{{void f1} f1}","{null f1,null f2}"); }
	@Test public void test_8074() { checkNotSubtype("{{void f1} f1}","{null f2,null f3}"); }
	@Test public void test_8075() { checkNotSubtype("{{void f1} f1}","{null f1,int f2}"); }
	@Test public void test_8076() { checkNotSubtype("{{void f1} f1}","{null f2,int f3}"); }
	@Test public void test_8077() { checkIsSubtype("{{void f1} f1}","{int f1,void f2}"); }
	@Test public void test_8078() { checkIsSubtype("{{void f1} f1}","{int f2,void f3}"); }
	@Test public void test_8079() { checkNotSubtype("{{void f1} f1}","{int f1,any f2}"); }
	@Test public void test_8080() { checkNotSubtype("{{void f1} f1}","{int f2,any f3}"); }
	@Test public void test_8081() { checkNotSubtype("{{void f1} f1}","{int f1,null f2}"); }
	@Test public void test_8082() { checkNotSubtype("{{void f1} f1}","{int f2,null f3}"); }
	@Test public void test_8083() { checkNotSubtype("{{void f1} f1}","{int f1,int f2}"); }
	@Test public void test_8084() { checkNotSubtype("{{void f1} f1}","{int f2,int f3}"); }
	@Test public void test_8085() { checkNotSubtype("{{void f1} f1}","{[void] f1}"); }
	@Test public void test_8086() { checkNotSubtype("{{void f1} f1}","{[void] f2}"); }
	@Test public void test_8087() { checkIsSubtype("{{void f1} f1}","{[void] f1,void f2}"); }
	@Test public void test_8088() { checkIsSubtype("{{void f1} f1}","{[void] f2,void f3}"); }
	@Test public void test_8089() { checkNotSubtype("{{void f1} f1}","{[any] f1}"); }
	@Test public void test_8090() { checkNotSubtype("{{void f1} f1}","{[any] f2}"); }
	@Test public void test_8091() { checkNotSubtype("{{void f1} f1}","{[any] f1,any f2}"); }
	@Test public void test_8092() { checkNotSubtype("{{void f1} f1}","{[any] f2,any f3}"); }
	@Test public void test_8093() { checkNotSubtype("{{void f1} f1}","{[null] f1}"); }
	@Test public void test_8094() { checkNotSubtype("{{void f1} f1}","{[null] f2}"); }
	@Test public void test_8095() { checkNotSubtype("{{void f1} f1}","{[null] f1,null f2}"); }
	@Test public void test_8096() { checkNotSubtype("{{void f1} f1}","{[null] f2,null f3}"); }
	@Test public void test_8097() { checkNotSubtype("{{void f1} f1}","{[int] f1}"); }
	@Test public void test_8098() { checkNotSubtype("{{void f1} f1}","{[int] f2}"); }
	@Test public void test_8099() { checkNotSubtype("{{void f1} f1}","{[int] f1,int f2}"); }
	@Test public void test_8100() { checkNotSubtype("{{void f1} f1}","{[int] f2,int f3}"); }
	@Test public void test_8101() { checkIsSubtype("{{void f1} f1}","{{void f1} f1}"); }
	@Test public void test_8102() { checkIsSubtype("{{void f1} f1}","{{void f2} f1}"); }
	@Test public void test_8103() { checkIsSubtype("{{void f1} f1}","{{void f1} f2}"); }
	@Test public void test_8104() { checkIsSubtype("{{void f1} f1}","{{void f2} f2}"); }
	@Test public void test_8105() { checkIsSubtype("{{void f1} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_8106() { checkIsSubtype("{{void f1} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_8107() { checkIsSubtype("{{void f1} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_8108() { checkIsSubtype("{{void f1} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_8109() { checkNotSubtype("{{void f1} f1}","{{any f1} f1}"); }
	@Test public void test_8110() { checkNotSubtype("{{void f1} f1}","{{any f2} f1}"); }
	@Test public void test_8111() { checkNotSubtype("{{void f1} f1}","{{any f1} f2}"); }
	@Test public void test_8112() { checkNotSubtype("{{void f1} f1}","{{any f2} f2}"); }
	@Test public void test_8113() { checkNotSubtype("{{void f1} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_8114() { checkNotSubtype("{{void f1} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_8115() { checkNotSubtype("{{void f1} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_8116() { checkNotSubtype("{{void f1} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_8117() { checkNotSubtype("{{void f1} f1}","{{null f1} f1}"); }
	@Test public void test_8118() { checkNotSubtype("{{void f1} f1}","{{null f2} f1}"); }
	@Test public void test_8119() { checkNotSubtype("{{void f1} f1}","{{null f1} f2}"); }
	@Test public void test_8120() { checkNotSubtype("{{void f1} f1}","{{null f2} f2}"); }
	@Test public void test_8121() { checkNotSubtype("{{void f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_8122() { checkNotSubtype("{{void f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_8123() { checkNotSubtype("{{void f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_8124() { checkNotSubtype("{{void f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_8125() { checkNotSubtype("{{void f1} f1}","{{int f1} f1}"); }
	@Test public void test_8126() { checkNotSubtype("{{void f1} f1}","{{int f2} f1}"); }
	@Test public void test_8127() { checkNotSubtype("{{void f1} f1}","{{int f1} f2}"); }
	@Test public void test_8128() { checkNotSubtype("{{void f1} f1}","{{int f2} f2}"); }
	@Test public void test_8129() { checkNotSubtype("{{void f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_8130() { checkNotSubtype("{{void f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_8131() { checkNotSubtype("{{void f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_8132() { checkNotSubtype("{{void f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_8133() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_8134() { checkNotSubtype("{{void f2} f1}","null"); }
	@Test public void test_8135() { checkNotSubtype("{{void f2} f1}","int"); }
	@Test public void test_8136() { checkNotSubtype("{{void f2} f1}","[void]"); }
	@Test public void test_8137() { checkNotSubtype("{{void f2} f1}","[any]"); }
	@Test public void test_8138() { checkNotSubtype("{{void f2} f1}","[null]"); }
	@Test public void test_8139() { checkNotSubtype("{{void f2} f1}","[int]"); }
	@Test public void test_8140() { checkIsSubtype("{{void f2} f1}","{void f1}"); }
	@Test public void test_8141() { checkIsSubtype("{{void f2} f1}","{void f2}"); }
	@Test public void test_8142() { checkNotSubtype("{{void f2} f1}","{any f1}"); }
	@Test public void test_8143() { checkNotSubtype("{{void f2} f1}","{any f2}"); }
	@Test public void test_8144() { checkNotSubtype("{{void f2} f1}","{null f1}"); }
	@Test public void test_8145() { checkNotSubtype("{{void f2} f1}","{null f2}"); }
	@Test public void test_8146() { checkNotSubtype("{{void f2} f1}","{int f1}"); }
	@Test public void test_8147() { checkNotSubtype("{{void f2} f1}","{int f2}"); }
	@Test public void test_8148() { checkNotSubtype("{{void f2} f1}","[[void]]"); }
	@Test public void test_8149() { checkNotSubtype("{{void f2} f1}","[[any]]"); }
	@Test public void test_8150() { checkNotSubtype("{{void f2} f1}","[[null]]"); }
	@Test public void test_8151() { checkNotSubtype("{{void f2} f1}","[[int]]"); }
	@Test public void test_8152() { checkNotSubtype("{{void f2} f1}","[{void f1}]"); }
	@Test public void test_8153() { checkNotSubtype("{{void f2} f1}","[{void f2}]"); }
	@Test public void test_8154() { checkNotSubtype("{{void f2} f1}","[{any f1}]"); }
	@Test public void test_8155() { checkNotSubtype("{{void f2} f1}","[{any f2}]"); }
	@Test public void test_8156() { checkNotSubtype("{{void f2} f1}","[{null f1}]"); }
	@Test public void test_8157() { checkNotSubtype("{{void f2} f1}","[{null f2}]"); }
	@Test public void test_8158() { checkNotSubtype("{{void f2} f1}","[{int f1}]"); }
	@Test public void test_8159() { checkNotSubtype("{{void f2} f1}","[{int f2}]"); }
	@Test public void test_8160() { checkIsSubtype("{{void f2} f1}","{void f1,void f2}"); }
	@Test public void test_8161() { checkIsSubtype("{{void f2} f1}","{void f2,void f3}"); }
	@Test public void test_8162() { checkIsSubtype("{{void f2} f1}","{void f1,any f2}"); }
	@Test public void test_8163() { checkIsSubtype("{{void f2} f1}","{void f2,any f3}"); }
	@Test public void test_8164() { checkIsSubtype("{{void f2} f1}","{void f1,null f2}"); }
	@Test public void test_8165() { checkIsSubtype("{{void f2} f1}","{void f2,null f3}"); }
	@Test public void test_8166() { checkIsSubtype("{{void f2} f1}","{void f1,int f2}"); }
	@Test public void test_8167() { checkIsSubtype("{{void f2} f1}","{void f2,int f3}"); }
	@Test public void test_8168() { checkIsSubtype("{{void f2} f1}","{any f1,void f2}"); }
	@Test public void test_8169() { checkIsSubtype("{{void f2} f1}","{any f2,void f3}"); }
	@Test public void test_8170() { checkNotSubtype("{{void f2} f1}","{any f1,any f2}"); }
	@Test public void test_8171() { checkNotSubtype("{{void f2} f1}","{any f2,any f3}"); }
	@Test public void test_8172() { checkNotSubtype("{{void f2} f1}","{any f1,null f2}"); }
	@Test public void test_8173() { checkNotSubtype("{{void f2} f1}","{any f2,null f3}"); }
	@Test public void test_8174() { checkNotSubtype("{{void f2} f1}","{any f1,int f2}"); }
	@Test public void test_8175() { checkNotSubtype("{{void f2} f1}","{any f2,int f3}"); }
	@Test public void test_8176() { checkIsSubtype("{{void f2} f1}","{null f1,void f2}"); }
	@Test public void test_8177() { checkIsSubtype("{{void f2} f1}","{null f2,void f3}"); }
	@Test public void test_8178() { checkNotSubtype("{{void f2} f1}","{null f1,any f2}"); }
	@Test public void test_8179() { checkNotSubtype("{{void f2} f1}","{null f2,any f3}"); }
	@Test public void test_8180() { checkNotSubtype("{{void f2} f1}","{null f1,null f2}"); }
	@Test public void test_8181() { checkNotSubtype("{{void f2} f1}","{null f2,null f3}"); }
	@Test public void test_8182() { checkNotSubtype("{{void f2} f1}","{null f1,int f2}"); }
	@Test public void test_8183() { checkNotSubtype("{{void f2} f1}","{null f2,int f3}"); }
	@Test public void test_8184() { checkIsSubtype("{{void f2} f1}","{int f1,void f2}"); }
	@Test public void test_8185() { checkIsSubtype("{{void f2} f1}","{int f2,void f3}"); }
	@Test public void test_8186() { checkNotSubtype("{{void f2} f1}","{int f1,any f2}"); }
	@Test public void test_8187() { checkNotSubtype("{{void f2} f1}","{int f2,any f3}"); }
	@Test public void test_8188() { checkNotSubtype("{{void f2} f1}","{int f1,null f2}"); }
	@Test public void test_8189() { checkNotSubtype("{{void f2} f1}","{int f2,null f3}"); }
	@Test public void test_8190() { checkNotSubtype("{{void f2} f1}","{int f1,int f2}"); }
	@Test public void test_8191() { checkNotSubtype("{{void f2} f1}","{int f2,int f3}"); }
	@Test public void test_8192() { checkNotSubtype("{{void f2} f1}","{[void] f1}"); }
	@Test public void test_8193() { checkNotSubtype("{{void f2} f1}","{[void] f2}"); }
	@Test public void test_8194() { checkIsSubtype("{{void f2} f1}","{[void] f1,void f2}"); }
	@Test public void test_8195() { checkIsSubtype("{{void f2} f1}","{[void] f2,void f3}"); }
	@Test public void test_8196() { checkNotSubtype("{{void f2} f1}","{[any] f1}"); }
	@Test public void test_8197() { checkNotSubtype("{{void f2} f1}","{[any] f2}"); }
	@Test public void test_8198() { checkNotSubtype("{{void f2} f1}","{[any] f1,any f2}"); }
	@Test public void test_8199() { checkNotSubtype("{{void f2} f1}","{[any] f2,any f3}"); }
	@Test public void test_8200() { checkNotSubtype("{{void f2} f1}","{[null] f1}"); }
	@Test public void test_8201() { checkNotSubtype("{{void f2} f1}","{[null] f2}"); }
	@Test public void test_8202() { checkNotSubtype("{{void f2} f1}","{[null] f1,null f2}"); }
	@Test public void test_8203() { checkNotSubtype("{{void f2} f1}","{[null] f2,null f3}"); }
	@Test public void test_8204() { checkNotSubtype("{{void f2} f1}","{[int] f1}"); }
	@Test public void test_8205() { checkNotSubtype("{{void f2} f1}","{[int] f2}"); }
	@Test public void test_8206() { checkNotSubtype("{{void f2} f1}","{[int] f1,int f2}"); }
	@Test public void test_8207() { checkNotSubtype("{{void f2} f1}","{[int] f2,int f3}"); }
	@Test public void test_8208() { checkIsSubtype("{{void f2} f1}","{{void f1} f1}"); }
	@Test public void test_8209() { checkIsSubtype("{{void f2} f1}","{{void f2} f1}"); }
	@Test public void test_8210() { checkIsSubtype("{{void f2} f1}","{{void f1} f2}"); }
	@Test public void test_8211() { checkIsSubtype("{{void f2} f1}","{{void f2} f2}"); }
	@Test public void test_8212() { checkIsSubtype("{{void f2} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_8213() { checkIsSubtype("{{void f2} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_8214() { checkIsSubtype("{{void f2} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_8215() { checkIsSubtype("{{void f2} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_8216() { checkNotSubtype("{{void f2} f1}","{{any f1} f1}"); }
	@Test public void test_8217() { checkNotSubtype("{{void f2} f1}","{{any f2} f1}"); }
	@Test public void test_8218() { checkNotSubtype("{{void f2} f1}","{{any f1} f2}"); }
	@Test public void test_8219() { checkNotSubtype("{{void f2} f1}","{{any f2} f2}"); }
	@Test public void test_8220() { checkNotSubtype("{{void f2} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_8221() { checkNotSubtype("{{void f2} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_8222() { checkNotSubtype("{{void f2} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_8223() { checkNotSubtype("{{void f2} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_8224() { checkNotSubtype("{{void f2} f1}","{{null f1} f1}"); }
	@Test public void test_8225() { checkNotSubtype("{{void f2} f1}","{{null f2} f1}"); }
	@Test public void test_8226() { checkNotSubtype("{{void f2} f1}","{{null f1} f2}"); }
	@Test public void test_8227() { checkNotSubtype("{{void f2} f1}","{{null f2} f2}"); }
	@Test public void test_8228() { checkNotSubtype("{{void f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_8229() { checkNotSubtype("{{void f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_8230() { checkNotSubtype("{{void f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_8231() { checkNotSubtype("{{void f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_8232() { checkNotSubtype("{{void f2} f1}","{{int f1} f1}"); }
	@Test public void test_8233() { checkNotSubtype("{{void f2} f1}","{{int f2} f1}"); }
	@Test public void test_8234() { checkNotSubtype("{{void f2} f1}","{{int f1} f2}"); }
	@Test public void test_8235() { checkNotSubtype("{{void f2} f1}","{{int f2} f2}"); }
	@Test public void test_8236() { checkNotSubtype("{{void f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_8237() { checkNotSubtype("{{void f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_8238() { checkNotSubtype("{{void f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_8239() { checkNotSubtype("{{void f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_8240() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_8241() { checkNotSubtype("{{void f1} f2}","null"); }
	@Test public void test_8242() { checkNotSubtype("{{void f1} f2}","int"); }
	@Test public void test_8243() { checkNotSubtype("{{void f1} f2}","[void]"); }
	@Test public void test_8244() { checkNotSubtype("{{void f1} f2}","[any]"); }
	@Test public void test_8245() { checkNotSubtype("{{void f1} f2}","[null]"); }
	@Test public void test_8246() { checkNotSubtype("{{void f1} f2}","[int]"); }
	@Test public void test_8247() { checkIsSubtype("{{void f1} f2}","{void f1}"); }
	@Test public void test_8248() { checkIsSubtype("{{void f1} f2}","{void f2}"); }
	@Test public void test_8249() { checkNotSubtype("{{void f1} f2}","{any f1}"); }
	@Test public void test_8250() { checkNotSubtype("{{void f1} f2}","{any f2}"); }
	@Test public void test_8251() { checkNotSubtype("{{void f1} f2}","{null f1}"); }
	@Test public void test_8252() { checkNotSubtype("{{void f1} f2}","{null f2}"); }
	@Test public void test_8253() { checkNotSubtype("{{void f1} f2}","{int f1}"); }
	@Test public void test_8254() { checkNotSubtype("{{void f1} f2}","{int f2}"); }
	@Test public void test_8255() { checkNotSubtype("{{void f1} f2}","[[void]]"); }
	@Test public void test_8256() { checkNotSubtype("{{void f1} f2}","[[any]]"); }
	@Test public void test_8257() { checkNotSubtype("{{void f1} f2}","[[null]]"); }
	@Test public void test_8258() { checkNotSubtype("{{void f1} f2}","[[int]]"); }
	@Test public void test_8259() { checkNotSubtype("{{void f1} f2}","[{void f1}]"); }
	@Test public void test_8260() { checkNotSubtype("{{void f1} f2}","[{void f2}]"); }
	@Test public void test_8261() { checkNotSubtype("{{void f1} f2}","[{any f1}]"); }
	@Test public void test_8262() { checkNotSubtype("{{void f1} f2}","[{any f2}]"); }
	@Test public void test_8263() { checkNotSubtype("{{void f1} f2}","[{null f1}]"); }
	@Test public void test_8264() { checkNotSubtype("{{void f1} f2}","[{null f2}]"); }
	@Test public void test_8265() { checkNotSubtype("{{void f1} f2}","[{int f1}]"); }
	@Test public void test_8266() { checkNotSubtype("{{void f1} f2}","[{int f2}]"); }
	@Test public void test_8267() { checkIsSubtype("{{void f1} f2}","{void f1,void f2}"); }
	@Test public void test_8268() { checkIsSubtype("{{void f1} f2}","{void f2,void f3}"); }
	@Test public void test_8269() { checkIsSubtype("{{void f1} f2}","{void f1,any f2}"); }
	@Test public void test_8270() { checkIsSubtype("{{void f1} f2}","{void f2,any f3}"); }
	@Test public void test_8271() { checkIsSubtype("{{void f1} f2}","{void f1,null f2}"); }
	@Test public void test_8272() { checkIsSubtype("{{void f1} f2}","{void f2,null f3}"); }
	@Test public void test_8273() { checkIsSubtype("{{void f1} f2}","{void f1,int f2}"); }
	@Test public void test_8274() { checkIsSubtype("{{void f1} f2}","{void f2,int f3}"); }
	@Test public void test_8275() { checkIsSubtype("{{void f1} f2}","{any f1,void f2}"); }
	@Test public void test_8276() { checkIsSubtype("{{void f1} f2}","{any f2,void f3}"); }
	@Test public void test_8277() { checkNotSubtype("{{void f1} f2}","{any f1,any f2}"); }
	@Test public void test_8278() { checkNotSubtype("{{void f1} f2}","{any f2,any f3}"); }
	@Test public void test_8279() { checkNotSubtype("{{void f1} f2}","{any f1,null f2}"); }
	@Test public void test_8280() { checkNotSubtype("{{void f1} f2}","{any f2,null f3}"); }
	@Test public void test_8281() { checkNotSubtype("{{void f1} f2}","{any f1,int f2}"); }
	@Test public void test_8282() { checkNotSubtype("{{void f1} f2}","{any f2,int f3}"); }
	@Test public void test_8283() { checkIsSubtype("{{void f1} f2}","{null f1,void f2}"); }
	@Test public void test_8284() { checkIsSubtype("{{void f1} f2}","{null f2,void f3}"); }
	@Test public void test_8285() { checkNotSubtype("{{void f1} f2}","{null f1,any f2}"); }
	@Test public void test_8286() { checkNotSubtype("{{void f1} f2}","{null f2,any f3}"); }
	@Test public void test_8287() { checkNotSubtype("{{void f1} f2}","{null f1,null f2}"); }
	@Test public void test_8288() { checkNotSubtype("{{void f1} f2}","{null f2,null f3}"); }
	@Test public void test_8289() { checkNotSubtype("{{void f1} f2}","{null f1,int f2}"); }
	@Test public void test_8290() { checkNotSubtype("{{void f1} f2}","{null f2,int f3}"); }
	@Test public void test_8291() { checkIsSubtype("{{void f1} f2}","{int f1,void f2}"); }
	@Test public void test_8292() { checkIsSubtype("{{void f1} f2}","{int f2,void f3}"); }
	@Test public void test_8293() { checkNotSubtype("{{void f1} f2}","{int f1,any f2}"); }
	@Test public void test_8294() { checkNotSubtype("{{void f1} f2}","{int f2,any f3}"); }
	@Test public void test_8295() { checkNotSubtype("{{void f1} f2}","{int f1,null f2}"); }
	@Test public void test_8296() { checkNotSubtype("{{void f1} f2}","{int f2,null f3}"); }
	@Test public void test_8297() { checkNotSubtype("{{void f1} f2}","{int f1,int f2}"); }
	@Test public void test_8298() { checkNotSubtype("{{void f1} f2}","{int f2,int f3}"); }
	@Test public void test_8299() { checkNotSubtype("{{void f1} f2}","{[void] f1}"); }
	@Test public void test_8300() { checkNotSubtype("{{void f1} f2}","{[void] f2}"); }
	@Test public void test_8301() { checkIsSubtype("{{void f1} f2}","{[void] f1,void f2}"); }
	@Test public void test_8302() { checkIsSubtype("{{void f1} f2}","{[void] f2,void f3}"); }
	@Test public void test_8303() { checkNotSubtype("{{void f1} f2}","{[any] f1}"); }
	@Test public void test_8304() { checkNotSubtype("{{void f1} f2}","{[any] f2}"); }
	@Test public void test_8305() { checkNotSubtype("{{void f1} f2}","{[any] f1,any f2}"); }
	@Test public void test_8306() { checkNotSubtype("{{void f1} f2}","{[any] f2,any f3}"); }
	@Test public void test_8307() { checkNotSubtype("{{void f1} f2}","{[null] f1}"); }
	@Test public void test_8308() { checkNotSubtype("{{void f1} f2}","{[null] f2}"); }
	@Test public void test_8309() { checkNotSubtype("{{void f1} f2}","{[null] f1,null f2}"); }
	@Test public void test_8310() { checkNotSubtype("{{void f1} f2}","{[null] f2,null f3}"); }
	@Test public void test_8311() { checkNotSubtype("{{void f1} f2}","{[int] f1}"); }
	@Test public void test_8312() { checkNotSubtype("{{void f1} f2}","{[int] f2}"); }
	@Test public void test_8313() { checkNotSubtype("{{void f1} f2}","{[int] f1,int f2}"); }
	@Test public void test_8314() { checkNotSubtype("{{void f1} f2}","{[int] f2,int f3}"); }
	@Test public void test_8315() { checkIsSubtype("{{void f1} f2}","{{void f1} f1}"); }
	@Test public void test_8316() { checkIsSubtype("{{void f1} f2}","{{void f2} f1}"); }
	@Test public void test_8317() { checkIsSubtype("{{void f1} f2}","{{void f1} f2}"); }
	@Test public void test_8318() { checkIsSubtype("{{void f1} f2}","{{void f2} f2}"); }
	@Test public void test_8319() { checkIsSubtype("{{void f1} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_8320() { checkIsSubtype("{{void f1} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_8321() { checkIsSubtype("{{void f1} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_8322() { checkIsSubtype("{{void f1} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_8323() { checkNotSubtype("{{void f1} f2}","{{any f1} f1}"); }
	@Test public void test_8324() { checkNotSubtype("{{void f1} f2}","{{any f2} f1}"); }
	@Test public void test_8325() { checkNotSubtype("{{void f1} f2}","{{any f1} f2}"); }
	@Test public void test_8326() { checkNotSubtype("{{void f1} f2}","{{any f2} f2}"); }
	@Test public void test_8327() { checkNotSubtype("{{void f1} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_8328() { checkNotSubtype("{{void f1} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_8329() { checkNotSubtype("{{void f1} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_8330() { checkNotSubtype("{{void f1} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_8331() { checkNotSubtype("{{void f1} f2}","{{null f1} f1}"); }
	@Test public void test_8332() { checkNotSubtype("{{void f1} f2}","{{null f2} f1}"); }
	@Test public void test_8333() { checkNotSubtype("{{void f1} f2}","{{null f1} f2}"); }
	@Test public void test_8334() { checkNotSubtype("{{void f1} f2}","{{null f2} f2}"); }
	@Test public void test_8335() { checkNotSubtype("{{void f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_8336() { checkNotSubtype("{{void f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_8337() { checkNotSubtype("{{void f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_8338() { checkNotSubtype("{{void f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_8339() { checkNotSubtype("{{void f1} f2}","{{int f1} f1}"); }
	@Test public void test_8340() { checkNotSubtype("{{void f1} f2}","{{int f2} f1}"); }
	@Test public void test_8341() { checkNotSubtype("{{void f1} f2}","{{int f1} f2}"); }
	@Test public void test_8342() { checkNotSubtype("{{void f1} f2}","{{int f2} f2}"); }
	@Test public void test_8343() { checkNotSubtype("{{void f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_8344() { checkNotSubtype("{{void f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_8345() { checkNotSubtype("{{void f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_8346() { checkNotSubtype("{{void f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_8347() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_8348() { checkNotSubtype("{{void f2} f2}","null"); }
	@Test public void test_8349() { checkNotSubtype("{{void f2} f2}","int"); }
	@Test public void test_8350() { checkNotSubtype("{{void f2} f2}","[void]"); }
	@Test public void test_8351() { checkNotSubtype("{{void f2} f2}","[any]"); }
	@Test public void test_8352() { checkNotSubtype("{{void f2} f2}","[null]"); }
	@Test public void test_8353() { checkNotSubtype("{{void f2} f2}","[int]"); }
	@Test public void test_8354() { checkIsSubtype("{{void f2} f2}","{void f1}"); }
	@Test public void test_8355() { checkIsSubtype("{{void f2} f2}","{void f2}"); }
	@Test public void test_8356() { checkNotSubtype("{{void f2} f2}","{any f1}"); }
	@Test public void test_8357() { checkNotSubtype("{{void f2} f2}","{any f2}"); }
	@Test public void test_8358() { checkNotSubtype("{{void f2} f2}","{null f1}"); }
	@Test public void test_8359() { checkNotSubtype("{{void f2} f2}","{null f2}"); }
	@Test public void test_8360() { checkNotSubtype("{{void f2} f2}","{int f1}"); }
	@Test public void test_8361() { checkNotSubtype("{{void f2} f2}","{int f2}"); }
	@Test public void test_8362() { checkNotSubtype("{{void f2} f2}","[[void]]"); }
	@Test public void test_8363() { checkNotSubtype("{{void f2} f2}","[[any]]"); }
	@Test public void test_8364() { checkNotSubtype("{{void f2} f2}","[[null]]"); }
	@Test public void test_8365() { checkNotSubtype("{{void f2} f2}","[[int]]"); }
	@Test public void test_8366() { checkNotSubtype("{{void f2} f2}","[{void f1}]"); }
	@Test public void test_8367() { checkNotSubtype("{{void f2} f2}","[{void f2}]"); }
	@Test public void test_8368() { checkNotSubtype("{{void f2} f2}","[{any f1}]"); }
	@Test public void test_8369() { checkNotSubtype("{{void f2} f2}","[{any f2}]"); }
	@Test public void test_8370() { checkNotSubtype("{{void f2} f2}","[{null f1}]"); }
	@Test public void test_8371() { checkNotSubtype("{{void f2} f2}","[{null f2}]"); }
	@Test public void test_8372() { checkNotSubtype("{{void f2} f2}","[{int f1}]"); }
	@Test public void test_8373() { checkNotSubtype("{{void f2} f2}","[{int f2}]"); }
	@Test public void test_8374() { checkIsSubtype("{{void f2} f2}","{void f1,void f2}"); }
	@Test public void test_8375() { checkIsSubtype("{{void f2} f2}","{void f2,void f3}"); }
	@Test public void test_8376() { checkIsSubtype("{{void f2} f2}","{void f1,any f2}"); }
	@Test public void test_8377() { checkIsSubtype("{{void f2} f2}","{void f2,any f3}"); }
	@Test public void test_8378() { checkIsSubtype("{{void f2} f2}","{void f1,null f2}"); }
	@Test public void test_8379() { checkIsSubtype("{{void f2} f2}","{void f2,null f3}"); }
	@Test public void test_8380() { checkIsSubtype("{{void f2} f2}","{void f1,int f2}"); }
	@Test public void test_8381() { checkIsSubtype("{{void f2} f2}","{void f2,int f3}"); }
	@Test public void test_8382() { checkIsSubtype("{{void f2} f2}","{any f1,void f2}"); }
	@Test public void test_8383() { checkIsSubtype("{{void f2} f2}","{any f2,void f3}"); }
	@Test public void test_8384() { checkNotSubtype("{{void f2} f2}","{any f1,any f2}"); }
	@Test public void test_8385() { checkNotSubtype("{{void f2} f2}","{any f2,any f3}"); }
	@Test public void test_8386() { checkNotSubtype("{{void f2} f2}","{any f1,null f2}"); }
	@Test public void test_8387() { checkNotSubtype("{{void f2} f2}","{any f2,null f3}"); }
	@Test public void test_8388() { checkNotSubtype("{{void f2} f2}","{any f1,int f2}"); }
	@Test public void test_8389() { checkNotSubtype("{{void f2} f2}","{any f2,int f3}"); }
	@Test public void test_8390() { checkIsSubtype("{{void f2} f2}","{null f1,void f2}"); }
	@Test public void test_8391() { checkIsSubtype("{{void f2} f2}","{null f2,void f3}"); }
	@Test public void test_8392() { checkNotSubtype("{{void f2} f2}","{null f1,any f2}"); }
	@Test public void test_8393() { checkNotSubtype("{{void f2} f2}","{null f2,any f3}"); }
	@Test public void test_8394() { checkNotSubtype("{{void f2} f2}","{null f1,null f2}"); }
	@Test public void test_8395() { checkNotSubtype("{{void f2} f2}","{null f2,null f3}"); }
	@Test public void test_8396() { checkNotSubtype("{{void f2} f2}","{null f1,int f2}"); }
	@Test public void test_8397() { checkNotSubtype("{{void f2} f2}","{null f2,int f3}"); }
	@Test public void test_8398() { checkIsSubtype("{{void f2} f2}","{int f1,void f2}"); }
	@Test public void test_8399() { checkIsSubtype("{{void f2} f2}","{int f2,void f3}"); }
	@Test public void test_8400() { checkNotSubtype("{{void f2} f2}","{int f1,any f2}"); }
	@Test public void test_8401() { checkNotSubtype("{{void f2} f2}","{int f2,any f3}"); }
	@Test public void test_8402() { checkNotSubtype("{{void f2} f2}","{int f1,null f2}"); }
	@Test public void test_8403() { checkNotSubtype("{{void f2} f2}","{int f2,null f3}"); }
	@Test public void test_8404() { checkNotSubtype("{{void f2} f2}","{int f1,int f2}"); }
	@Test public void test_8405() { checkNotSubtype("{{void f2} f2}","{int f2,int f3}"); }
	@Test public void test_8406() { checkNotSubtype("{{void f2} f2}","{[void] f1}"); }
	@Test public void test_8407() { checkNotSubtype("{{void f2} f2}","{[void] f2}"); }
	@Test public void test_8408() { checkIsSubtype("{{void f2} f2}","{[void] f1,void f2}"); }
	@Test public void test_8409() { checkIsSubtype("{{void f2} f2}","{[void] f2,void f3}"); }
	@Test public void test_8410() { checkNotSubtype("{{void f2} f2}","{[any] f1}"); }
	@Test public void test_8411() { checkNotSubtype("{{void f2} f2}","{[any] f2}"); }
	@Test public void test_8412() { checkNotSubtype("{{void f2} f2}","{[any] f1,any f2}"); }
	@Test public void test_8413() { checkNotSubtype("{{void f2} f2}","{[any] f2,any f3}"); }
	@Test public void test_8414() { checkNotSubtype("{{void f2} f2}","{[null] f1}"); }
	@Test public void test_8415() { checkNotSubtype("{{void f2} f2}","{[null] f2}"); }
	@Test public void test_8416() { checkNotSubtype("{{void f2} f2}","{[null] f1,null f2}"); }
	@Test public void test_8417() { checkNotSubtype("{{void f2} f2}","{[null] f2,null f3}"); }
	@Test public void test_8418() { checkNotSubtype("{{void f2} f2}","{[int] f1}"); }
	@Test public void test_8419() { checkNotSubtype("{{void f2} f2}","{[int] f2}"); }
	@Test public void test_8420() { checkNotSubtype("{{void f2} f2}","{[int] f1,int f2}"); }
	@Test public void test_8421() { checkNotSubtype("{{void f2} f2}","{[int] f2,int f3}"); }
	@Test public void test_8422() { checkIsSubtype("{{void f2} f2}","{{void f1} f1}"); }
	@Test public void test_8423() { checkIsSubtype("{{void f2} f2}","{{void f2} f1}"); }
	@Test public void test_8424() { checkIsSubtype("{{void f2} f2}","{{void f1} f2}"); }
	@Test public void test_8425() { checkIsSubtype("{{void f2} f2}","{{void f2} f2}"); }
	@Test public void test_8426() { checkIsSubtype("{{void f2} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_8427() { checkIsSubtype("{{void f2} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_8428() { checkIsSubtype("{{void f2} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_8429() { checkIsSubtype("{{void f2} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_8430() { checkNotSubtype("{{void f2} f2}","{{any f1} f1}"); }
	@Test public void test_8431() { checkNotSubtype("{{void f2} f2}","{{any f2} f1}"); }
	@Test public void test_8432() { checkNotSubtype("{{void f2} f2}","{{any f1} f2}"); }
	@Test public void test_8433() { checkNotSubtype("{{void f2} f2}","{{any f2} f2}"); }
	@Test public void test_8434() { checkNotSubtype("{{void f2} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_8435() { checkNotSubtype("{{void f2} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_8436() { checkNotSubtype("{{void f2} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_8437() { checkNotSubtype("{{void f2} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_8438() { checkNotSubtype("{{void f2} f2}","{{null f1} f1}"); }
	@Test public void test_8439() { checkNotSubtype("{{void f2} f2}","{{null f2} f1}"); }
	@Test public void test_8440() { checkNotSubtype("{{void f2} f2}","{{null f1} f2}"); }
	@Test public void test_8441() { checkNotSubtype("{{void f2} f2}","{{null f2} f2}"); }
	@Test public void test_8442() { checkNotSubtype("{{void f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_8443() { checkNotSubtype("{{void f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_8444() { checkNotSubtype("{{void f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_8445() { checkNotSubtype("{{void f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_8446() { checkNotSubtype("{{void f2} f2}","{{int f1} f1}"); }
	@Test public void test_8447() { checkNotSubtype("{{void f2} f2}","{{int f2} f1}"); }
	@Test public void test_8448() { checkNotSubtype("{{void f2} f2}","{{int f1} f2}"); }
	@Test public void test_8449() { checkNotSubtype("{{void f2} f2}","{{int f2} f2}"); }
	@Test public void test_8450() { checkNotSubtype("{{void f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_8451() { checkNotSubtype("{{void f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_8452() { checkNotSubtype("{{void f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_8453() { checkNotSubtype("{{void f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_8454() { checkNotSubtype("{{void f1} f1,void f2}","any"); }
	@Test public void test_8455() { checkNotSubtype("{{void f1} f1,void f2}","null"); }
	@Test public void test_8456() { checkNotSubtype("{{void f1} f1,void f2}","int"); }
	@Test public void test_8457() { checkNotSubtype("{{void f1} f1,void f2}","[void]"); }
	@Test public void test_8458() { checkNotSubtype("{{void f1} f1,void f2}","[any]"); }
	@Test public void test_8459() { checkNotSubtype("{{void f1} f1,void f2}","[null]"); }
	@Test public void test_8460() { checkNotSubtype("{{void f1} f1,void f2}","[int]"); }
	@Test public void test_8461() { checkIsSubtype("{{void f1} f1,void f2}","{void f1}"); }
	@Test public void test_8462() { checkIsSubtype("{{void f1} f1,void f2}","{void f2}"); }
	@Test public void test_8463() { checkNotSubtype("{{void f1} f1,void f2}","{any f1}"); }
	@Test public void test_8464() { checkNotSubtype("{{void f1} f1,void f2}","{any f2}"); }
	@Test public void test_8465() { checkNotSubtype("{{void f1} f1,void f2}","{null f1}"); }
	@Test public void test_8466() { checkNotSubtype("{{void f1} f1,void f2}","{null f2}"); }
	@Test public void test_8467() { checkNotSubtype("{{void f1} f1,void f2}","{int f1}"); }
	@Test public void test_8468() { checkNotSubtype("{{void f1} f1,void f2}","{int f2}"); }
	@Test public void test_8469() { checkNotSubtype("{{void f1} f1,void f2}","[[void]]"); }
	@Test public void test_8470() { checkNotSubtype("{{void f1} f1,void f2}","[[any]]"); }
	@Test public void test_8471() { checkNotSubtype("{{void f1} f1,void f2}","[[null]]"); }
	@Test public void test_8472() { checkNotSubtype("{{void f1} f1,void f2}","[[int]]"); }
	@Test public void test_8473() { checkNotSubtype("{{void f1} f1,void f2}","[{void f1}]"); }
	@Test public void test_8474() { checkNotSubtype("{{void f1} f1,void f2}","[{void f2}]"); }
	@Test public void test_8475() { checkNotSubtype("{{void f1} f1,void f2}","[{any f1}]"); }
	@Test public void test_8476() { checkNotSubtype("{{void f1} f1,void f2}","[{any f2}]"); }
	@Test public void test_8477() { checkNotSubtype("{{void f1} f1,void f2}","[{null f1}]"); }
	@Test public void test_8478() { checkNotSubtype("{{void f1} f1,void f2}","[{null f2}]"); }
	@Test public void test_8479() { checkNotSubtype("{{void f1} f1,void f2}","[{int f1}]"); }
	@Test public void test_8480() { checkNotSubtype("{{void f1} f1,void f2}","[{int f2}]"); }
	@Test public void test_8481() { checkIsSubtype("{{void f1} f1,void f2}","{void f1,void f2}"); }
	@Test public void test_8482() { checkIsSubtype("{{void f1} f1,void f2}","{void f2,void f3}"); }
	@Test public void test_8483() { checkIsSubtype("{{void f1} f1,void f2}","{void f1,any f2}"); }
	@Test public void test_8484() { checkIsSubtype("{{void f1} f1,void f2}","{void f2,any f3}"); }
	@Test public void test_8485() { checkIsSubtype("{{void f1} f1,void f2}","{void f1,null f2}"); }
	@Test public void test_8486() { checkIsSubtype("{{void f1} f1,void f2}","{void f2,null f3}"); }
	@Test public void test_8487() { checkIsSubtype("{{void f1} f1,void f2}","{void f1,int f2}"); }
	@Test public void test_8488() { checkIsSubtype("{{void f1} f1,void f2}","{void f2,int f3}"); }
	@Test public void test_8489() { checkIsSubtype("{{void f1} f1,void f2}","{any f1,void f2}"); }
	@Test public void test_8490() { checkIsSubtype("{{void f1} f1,void f2}","{any f2,void f3}"); }
	@Test public void test_8491() { checkNotSubtype("{{void f1} f1,void f2}","{any f1,any f2}"); }
	@Test public void test_8492() { checkNotSubtype("{{void f1} f1,void f2}","{any f2,any f3}"); }
	@Test public void test_8493() { checkNotSubtype("{{void f1} f1,void f2}","{any f1,null f2}"); }
	@Test public void test_8494() { checkNotSubtype("{{void f1} f1,void f2}","{any f2,null f3}"); }
	@Test public void test_8495() { checkNotSubtype("{{void f1} f1,void f2}","{any f1,int f2}"); }
	@Test public void test_8496() { checkNotSubtype("{{void f1} f1,void f2}","{any f2,int f3}"); }
	@Test public void test_8497() { checkIsSubtype("{{void f1} f1,void f2}","{null f1,void f2}"); }
	@Test public void test_8498() { checkIsSubtype("{{void f1} f1,void f2}","{null f2,void f3}"); }
	@Test public void test_8499() { checkNotSubtype("{{void f1} f1,void f2}","{null f1,any f2}"); }
	@Test public void test_8500() { checkNotSubtype("{{void f1} f1,void f2}","{null f2,any f3}"); }
	@Test public void test_8501() { checkNotSubtype("{{void f1} f1,void f2}","{null f1,null f2}"); }
	@Test public void test_8502() { checkNotSubtype("{{void f1} f1,void f2}","{null f2,null f3}"); }
	@Test public void test_8503() { checkNotSubtype("{{void f1} f1,void f2}","{null f1,int f2}"); }
	@Test public void test_8504() { checkNotSubtype("{{void f1} f1,void f2}","{null f2,int f3}"); }
	@Test public void test_8505() { checkIsSubtype("{{void f1} f1,void f2}","{int f1,void f2}"); }
	@Test public void test_8506() { checkIsSubtype("{{void f1} f1,void f2}","{int f2,void f3}"); }
	@Test public void test_8507() { checkNotSubtype("{{void f1} f1,void f2}","{int f1,any f2}"); }
	@Test public void test_8508() { checkNotSubtype("{{void f1} f1,void f2}","{int f2,any f3}"); }
	@Test public void test_8509() { checkNotSubtype("{{void f1} f1,void f2}","{int f1,null f2}"); }
	@Test public void test_8510() { checkNotSubtype("{{void f1} f1,void f2}","{int f2,null f3}"); }
	@Test public void test_8511() { checkNotSubtype("{{void f1} f1,void f2}","{int f1,int f2}"); }
	@Test public void test_8512() { checkNotSubtype("{{void f1} f1,void f2}","{int f2,int f3}"); }
	@Test public void test_8513() { checkNotSubtype("{{void f1} f1,void f2}","{[void] f1}"); }
	@Test public void test_8514() { checkNotSubtype("{{void f1} f1,void f2}","{[void] f2}"); }
	@Test public void test_8515() { checkIsSubtype("{{void f1} f1,void f2}","{[void] f1,void f2}"); }
	@Test public void test_8516() { checkIsSubtype("{{void f1} f1,void f2}","{[void] f2,void f3}"); }
	@Test public void test_8517() { checkNotSubtype("{{void f1} f1,void f2}","{[any] f1}"); }
	@Test public void test_8518() { checkNotSubtype("{{void f1} f1,void f2}","{[any] f2}"); }
	@Test public void test_8519() { checkNotSubtype("{{void f1} f1,void f2}","{[any] f1,any f2}"); }
	@Test public void test_8520() { checkNotSubtype("{{void f1} f1,void f2}","{[any] f2,any f3}"); }
	@Test public void test_8521() { checkNotSubtype("{{void f1} f1,void f2}","{[null] f1}"); }
	@Test public void test_8522() { checkNotSubtype("{{void f1} f1,void f2}","{[null] f2}"); }
	@Test public void test_8523() { checkNotSubtype("{{void f1} f1,void f2}","{[null] f1,null f2}"); }
	@Test public void test_8524() { checkNotSubtype("{{void f1} f1,void f2}","{[null] f2,null f3}"); }
	@Test public void test_8525() { checkNotSubtype("{{void f1} f1,void f2}","{[int] f1}"); }
	@Test public void test_8526() { checkNotSubtype("{{void f1} f1,void f2}","{[int] f2}"); }
	@Test public void test_8527() { checkNotSubtype("{{void f1} f1,void f2}","{[int] f1,int f2}"); }
	@Test public void test_8528() { checkNotSubtype("{{void f1} f1,void f2}","{[int] f2,int f3}"); }
	@Test public void test_8529() { checkIsSubtype("{{void f1} f1,void f2}","{{void f1} f1}"); }
	@Test public void test_8530() { checkIsSubtype("{{void f1} f1,void f2}","{{void f2} f1}"); }
	@Test public void test_8531() { checkIsSubtype("{{void f1} f1,void f2}","{{void f1} f2}"); }
	@Test public void test_8532() { checkIsSubtype("{{void f1} f1,void f2}","{{void f2} f2}"); }
	@Test public void test_8533() { checkIsSubtype("{{void f1} f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_8534() { checkIsSubtype("{{void f1} f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_8535() { checkIsSubtype("{{void f1} f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_8536() { checkIsSubtype("{{void f1} f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_8537() { checkNotSubtype("{{void f1} f1,void f2}","{{any f1} f1}"); }
	@Test public void test_8538() { checkNotSubtype("{{void f1} f1,void f2}","{{any f2} f1}"); }
	@Test public void test_8539() { checkNotSubtype("{{void f1} f1,void f2}","{{any f1} f2}"); }
	@Test public void test_8540() { checkNotSubtype("{{void f1} f1,void f2}","{{any f2} f2}"); }
	@Test public void test_8541() { checkNotSubtype("{{void f1} f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_8542() { checkNotSubtype("{{void f1} f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_8543() { checkNotSubtype("{{void f1} f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_8544() { checkNotSubtype("{{void f1} f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_8545() { checkNotSubtype("{{void f1} f1,void f2}","{{null f1} f1}"); }
	@Test public void test_8546() { checkNotSubtype("{{void f1} f1,void f2}","{{null f2} f1}"); }
	@Test public void test_8547() { checkNotSubtype("{{void f1} f1,void f2}","{{null f1} f2}"); }
	@Test public void test_8548() { checkNotSubtype("{{void f1} f1,void f2}","{{null f2} f2}"); }
	@Test public void test_8549() { checkNotSubtype("{{void f1} f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_8550() { checkNotSubtype("{{void f1} f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_8551() { checkNotSubtype("{{void f1} f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_8552() { checkNotSubtype("{{void f1} f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_8553() { checkNotSubtype("{{void f1} f1,void f2}","{{int f1} f1}"); }
	@Test public void test_8554() { checkNotSubtype("{{void f1} f1,void f2}","{{int f2} f1}"); }
	@Test public void test_8555() { checkNotSubtype("{{void f1} f1,void f2}","{{int f1} f2}"); }
	@Test public void test_8556() { checkNotSubtype("{{void f1} f1,void f2}","{{int f2} f2}"); }
	@Test public void test_8557() { checkNotSubtype("{{void f1} f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_8558() { checkNotSubtype("{{void f1} f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_8559() { checkNotSubtype("{{void f1} f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_8560() { checkNotSubtype("{{void f1} f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_8561() { checkNotSubtype("{{void f2} f1,void f2}","any"); }
	@Test public void test_8562() { checkNotSubtype("{{void f2} f1,void f2}","null"); }
	@Test public void test_8563() { checkNotSubtype("{{void f2} f1,void f2}","int"); }
	@Test public void test_8564() { checkNotSubtype("{{void f2} f1,void f2}","[void]"); }
	@Test public void test_8565() { checkNotSubtype("{{void f2} f1,void f2}","[any]"); }
	@Test public void test_8566() { checkNotSubtype("{{void f2} f1,void f2}","[null]"); }
	@Test public void test_8567() { checkNotSubtype("{{void f2} f1,void f2}","[int]"); }
	@Test public void test_8568() { checkIsSubtype("{{void f2} f1,void f2}","{void f1}"); }
	@Test public void test_8569() { checkIsSubtype("{{void f2} f1,void f2}","{void f2}"); }
	@Test public void test_8570() { checkNotSubtype("{{void f2} f1,void f2}","{any f1}"); }
	@Test public void test_8571() { checkNotSubtype("{{void f2} f1,void f2}","{any f2}"); }
	@Test public void test_8572() { checkNotSubtype("{{void f2} f1,void f2}","{null f1}"); }
	@Test public void test_8573() { checkNotSubtype("{{void f2} f1,void f2}","{null f2}"); }
	@Test public void test_8574() { checkNotSubtype("{{void f2} f1,void f2}","{int f1}"); }
	@Test public void test_8575() { checkNotSubtype("{{void f2} f1,void f2}","{int f2}"); }
	@Test public void test_8576() { checkNotSubtype("{{void f2} f1,void f2}","[[void]]"); }
	@Test public void test_8577() { checkNotSubtype("{{void f2} f1,void f2}","[[any]]"); }
	@Test public void test_8578() { checkNotSubtype("{{void f2} f1,void f2}","[[null]]"); }
	@Test public void test_8579() { checkNotSubtype("{{void f2} f1,void f2}","[[int]]"); }
	@Test public void test_8580() { checkNotSubtype("{{void f2} f1,void f2}","[{void f1}]"); }
	@Test public void test_8581() { checkNotSubtype("{{void f2} f1,void f2}","[{void f2}]"); }
	@Test public void test_8582() { checkNotSubtype("{{void f2} f1,void f2}","[{any f1}]"); }
	@Test public void test_8583() { checkNotSubtype("{{void f2} f1,void f2}","[{any f2}]"); }
	@Test public void test_8584() { checkNotSubtype("{{void f2} f1,void f2}","[{null f1}]"); }
	@Test public void test_8585() { checkNotSubtype("{{void f2} f1,void f2}","[{null f2}]"); }
	@Test public void test_8586() { checkNotSubtype("{{void f2} f1,void f2}","[{int f1}]"); }
	@Test public void test_8587() { checkNotSubtype("{{void f2} f1,void f2}","[{int f2}]"); }
	@Test public void test_8588() { checkIsSubtype("{{void f2} f1,void f2}","{void f1,void f2}"); }
	@Test public void test_8589() { checkIsSubtype("{{void f2} f1,void f2}","{void f2,void f3}"); }
	@Test public void test_8590() { checkIsSubtype("{{void f2} f1,void f2}","{void f1,any f2}"); }
	@Test public void test_8591() { checkIsSubtype("{{void f2} f1,void f2}","{void f2,any f3}"); }
	@Test public void test_8592() { checkIsSubtype("{{void f2} f1,void f2}","{void f1,null f2}"); }
	@Test public void test_8593() { checkIsSubtype("{{void f2} f1,void f2}","{void f2,null f3}"); }
	@Test public void test_8594() { checkIsSubtype("{{void f2} f1,void f2}","{void f1,int f2}"); }
	@Test public void test_8595() { checkIsSubtype("{{void f2} f1,void f2}","{void f2,int f3}"); }
	@Test public void test_8596() { checkIsSubtype("{{void f2} f1,void f2}","{any f1,void f2}"); }
	@Test public void test_8597() { checkIsSubtype("{{void f2} f1,void f2}","{any f2,void f3}"); }
	@Test public void test_8598() { checkNotSubtype("{{void f2} f1,void f2}","{any f1,any f2}"); }
	@Test public void test_8599() { checkNotSubtype("{{void f2} f1,void f2}","{any f2,any f3}"); }
	@Test public void test_8600() { checkNotSubtype("{{void f2} f1,void f2}","{any f1,null f2}"); }
	@Test public void test_8601() { checkNotSubtype("{{void f2} f1,void f2}","{any f2,null f3}"); }
	@Test public void test_8602() { checkNotSubtype("{{void f2} f1,void f2}","{any f1,int f2}"); }
	@Test public void test_8603() { checkNotSubtype("{{void f2} f1,void f2}","{any f2,int f3}"); }
	@Test public void test_8604() { checkIsSubtype("{{void f2} f1,void f2}","{null f1,void f2}"); }
	@Test public void test_8605() { checkIsSubtype("{{void f2} f1,void f2}","{null f2,void f3}"); }
	@Test public void test_8606() { checkNotSubtype("{{void f2} f1,void f2}","{null f1,any f2}"); }
	@Test public void test_8607() { checkNotSubtype("{{void f2} f1,void f2}","{null f2,any f3}"); }
	@Test public void test_8608() { checkNotSubtype("{{void f2} f1,void f2}","{null f1,null f2}"); }
	@Test public void test_8609() { checkNotSubtype("{{void f2} f1,void f2}","{null f2,null f3}"); }
	@Test public void test_8610() { checkNotSubtype("{{void f2} f1,void f2}","{null f1,int f2}"); }
	@Test public void test_8611() { checkNotSubtype("{{void f2} f1,void f2}","{null f2,int f3}"); }
	@Test public void test_8612() { checkIsSubtype("{{void f2} f1,void f2}","{int f1,void f2}"); }
	@Test public void test_8613() { checkIsSubtype("{{void f2} f1,void f2}","{int f2,void f3}"); }
	@Test public void test_8614() { checkNotSubtype("{{void f2} f1,void f2}","{int f1,any f2}"); }
	@Test public void test_8615() { checkNotSubtype("{{void f2} f1,void f2}","{int f2,any f3}"); }
	@Test public void test_8616() { checkNotSubtype("{{void f2} f1,void f2}","{int f1,null f2}"); }
	@Test public void test_8617() { checkNotSubtype("{{void f2} f1,void f2}","{int f2,null f3}"); }
	@Test public void test_8618() { checkNotSubtype("{{void f2} f1,void f2}","{int f1,int f2}"); }
	@Test public void test_8619() { checkNotSubtype("{{void f2} f1,void f2}","{int f2,int f3}"); }
	@Test public void test_8620() { checkNotSubtype("{{void f2} f1,void f2}","{[void] f1}"); }
	@Test public void test_8621() { checkNotSubtype("{{void f2} f1,void f2}","{[void] f2}"); }
	@Test public void test_8622() { checkIsSubtype("{{void f2} f1,void f2}","{[void] f1,void f2}"); }
	@Test public void test_8623() { checkIsSubtype("{{void f2} f1,void f2}","{[void] f2,void f3}"); }
	@Test public void test_8624() { checkNotSubtype("{{void f2} f1,void f2}","{[any] f1}"); }
	@Test public void test_8625() { checkNotSubtype("{{void f2} f1,void f2}","{[any] f2}"); }
	@Test public void test_8626() { checkNotSubtype("{{void f2} f1,void f2}","{[any] f1,any f2}"); }
	@Test public void test_8627() { checkNotSubtype("{{void f2} f1,void f2}","{[any] f2,any f3}"); }
	@Test public void test_8628() { checkNotSubtype("{{void f2} f1,void f2}","{[null] f1}"); }
	@Test public void test_8629() { checkNotSubtype("{{void f2} f1,void f2}","{[null] f2}"); }
	@Test public void test_8630() { checkNotSubtype("{{void f2} f1,void f2}","{[null] f1,null f2}"); }
	@Test public void test_8631() { checkNotSubtype("{{void f2} f1,void f2}","{[null] f2,null f3}"); }
	@Test public void test_8632() { checkNotSubtype("{{void f2} f1,void f2}","{[int] f1}"); }
	@Test public void test_8633() { checkNotSubtype("{{void f2} f1,void f2}","{[int] f2}"); }
	@Test public void test_8634() { checkNotSubtype("{{void f2} f1,void f2}","{[int] f1,int f2}"); }
	@Test public void test_8635() { checkNotSubtype("{{void f2} f1,void f2}","{[int] f2,int f3}"); }
	@Test public void test_8636() { checkIsSubtype("{{void f2} f1,void f2}","{{void f1} f1}"); }
	@Test public void test_8637() { checkIsSubtype("{{void f2} f1,void f2}","{{void f2} f1}"); }
	@Test public void test_8638() { checkIsSubtype("{{void f2} f1,void f2}","{{void f1} f2}"); }
	@Test public void test_8639() { checkIsSubtype("{{void f2} f1,void f2}","{{void f2} f2}"); }
	@Test public void test_8640() { checkIsSubtype("{{void f2} f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_8641() { checkIsSubtype("{{void f2} f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_8642() { checkIsSubtype("{{void f2} f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_8643() { checkIsSubtype("{{void f2} f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_8644() { checkNotSubtype("{{void f2} f1,void f2}","{{any f1} f1}"); }
	@Test public void test_8645() { checkNotSubtype("{{void f2} f1,void f2}","{{any f2} f1}"); }
	@Test public void test_8646() { checkNotSubtype("{{void f2} f1,void f2}","{{any f1} f2}"); }
	@Test public void test_8647() { checkNotSubtype("{{void f2} f1,void f2}","{{any f2} f2}"); }
	@Test public void test_8648() { checkNotSubtype("{{void f2} f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_8649() { checkNotSubtype("{{void f2} f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_8650() { checkNotSubtype("{{void f2} f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_8651() { checkNotSubtype("{{void f2} f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_8652() { checkNotSubtype("{{void f2} f1,void f2}","{{null f1} f1}"); }
	@Test public void test_8653() { checkNotSubtype("{{void f2} f1,void f2}","{{null f2} f1}"); }
	@Test public void test_8654() { checkNotSubtype("{{void f2} f1,void f2}","{{null f1} f2}"); }
	@Test public void test_8655() { checkNotSubtype("{{void f2} f1,void f2}","{{null f2} f2}"); }
	@Test public void test_8656() { checkNotSubtype("{{void f2} f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_8657() { checkNotSubtype("{{void f2} f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_8658() { checkNotSubtype("{{void f2} f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_8659() { checkNotSubtype("{{void f2} f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_8660() { checkNotSubtype("{{void f2} f1,void f2}","{{int f1} f1}"); }
	@Test public void test_8661() { checkNotSubtype("{{void f2} f1,void f2}","{{int f2} f1}"); }
	@Test public void test_8662() { checkNotSubtype("{{void f2} f1,void f2}","{{int f1} f2}"); }
	@Test public void test_8663() { checkNotSubtype("{{void f2} f1,void f2}","{{int f2} f2}"); }
	@Test public void test_8664() { checkNotSubtype("{{void f2} f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_8665() { checkNotSubtype("{{void f2} f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_8666() { checkNotSubtype("{{void f2} f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_8667() { checkNotSubtype("{{void f2} f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_8668() { checkNotSubtype("{{void f1} f2,void f3}","any"); }
	@Test public void test_8669() { checkNotSubtype("{{void f1} f2,void f3}","null"); }
	@Test public void test_8670() { checkNotSubtype("{{void f1} f2,void f3}","int"); }
	@Test public void test_8671() { checkNotSubtype("{{void f1} f2,void f3}","[void]"); }
	@Test public void test_8672() { checkNotSubtype("{{void f1} f2,void f3}","[any]"); }
	@Test public void test_8673() { checkNotSubtype("{{void f1} f2,void f3}","[null]"); }
	@Test public void test_8674() { checkNotSubtype("{{void f1} f2,void f3}","[int]"); }
	@Test public void test_8675() { checkIsSubtype("{{void f1} f2,void f3}","{void f1}"); }
	@Test public void test_8676() { checkIsSubtype("{{void f1} f2,void f3}","{void f2}"); }
	@Test public void test_8677() { checkNotSubtype("{{void f1} f2,void f3}","{any f1}"); }
	@Test public void test_8678() { checkNotSubtype("{{void f1} f2,void f3}","{any f2}"); }
	@Test public void test_8679() { checkNotSubtype("{{void f1} f2,void f3}","{null f1}"); }
	@Test public void test_8680() { checkNotSubtype("{{void f1} f2,void f3}","{null f2}"); }
	@Test public void test_8681() { checkNotSubtype("{{void f1} f2,void f3}","{int f1}"); }
	@Test public void test_8682() { checkNotSubtype("{{void f1} f2,void f3}","{int f2}"); }
	@Test public void test_8683() { checkNotSubtype("{{void f1} f2,void f3}","[[void]]"); }
	@Test public void test_8684() { checkNotSubtype("{{void f1} f2,void f3}","[[any]]"); }
	@Test public void test_8685() { checkNotSubtype("{{void f1} f2,void f3}","[[null]]"); }
	@Test public void test_8686() { checkNotSubtype("{{void f1} f2,void f3}","[[int]]"); }
	@Test public void test_8687() { checkNotSubtype("{{void f1} f2,void f3}","[{void f1}]"); }
	@Test public void test_8688() { checkNotSubtype("{{void f1} f2,void f3}","[{void f2}]"); }
	@Test public void test_8689() { checkNotSubtype("{{void f1} f2,void f3}","[{any f1}]"); }
	@Test public void test_8690() { checkNotSubtype("{{void f1} f2,void f3}","[{any f2}]"); }
	@Test public void test_8691() { checkNotSubtype("{{void f1} f2,void f3}","[{null f1}]"); }
	@Test public void test_8692() { checkNotSubtype("{{void f1} f2,void f3}","[{null f2}]"); }
	@Test public void test_8693() { checkNotSubtype("{{void f1} f2,void f3}","[{int f1}]"); }
	@Test public void test_8694() { checkNotSubtype("{{void f1} f2,void f3}","[{int f2}]"); }
	@Test public void test_8695() { checkIsSubtype("{{void f1} f2,void f3}","{void f1,void f2}"); }
	@Test public void test_8696() { checkIsSubtype("{{void f1} f2,void f3}","{void f2,void f3}"); }
	@Test public void test_8697() { checkIsSubtype("{{void f1} f2,void f3}","{void f1,any f2}"); }
	@Test public void test_8698() { checkIsSubtype("{{void f1} f2,void f3}","{void f2,any f3}"); }
	@Test public void test_8699() { checkIsSubtype("{{void f1} f2,void f3}","{void f1,null f2}"); }
	@Test public void test_8700() { checkIsSubtype("{{void f1} f2,void f3}","{void f2,null f3}"); }
	@Test public void test_8701() { checkIsSubtype("{{void f1} f2,void f3}","{void f1,int f2}"); }
	@Test public void test_8702() { checkIsSubtype("{{void f1} f2,void f3}","{void f2,int f3}"); }
	@Test public void test_8703() { checkIsSubtype("{{void f1} f2,void f3}","{any f1,void f2}"); }
	@Test public void test_8704() { checkIsSubtype("{{void f1} f2,void f3}","{any f2,void f3}"); }
	@Test public void test_8705() { checkNotSubtype("{{void f1} f2,void f3}","{any f1,any f2}"); }
	@Test public void test_8706() { checkNotSubtype("{{void f1} f2,void f3}","{any f2,any f3}"); }
	@Test public void test_8707() { checkNotSubtype("{{void f1} f2,void f3}","{any f1,null f2}"); }
	@Test public void test_8708() { checkNotSubtype("{{void f1} f2,void f3}","{any f2,null f3}"); }
	@Test public void test_8709() { checkNotSubtype("{{void f1} f2,void f3}","{any f1,int f2}"); }
	@Test public void test_8710() { checkNotSubtype("{{void f1} f2,void f3}","{any f2,int f3}"); }
	@Test public void test_8711() { checkIsSubtype("{{void f1} f2,void f3}","{null f1,void f2}"); }
	@Test public void test_8712() { checkIsSubtype("{{void f1} f2,void f3}","{null f2,void f3}"); }
	@Test public void test_8713() { checkNotSubtype("{{void f1} f2,void f3}","{null f1,any f2}"); }
	@Test public void test_8714() { checkNotSubtype("{{void f1} f2,void f3}","{null f2,any f3}"); }
	@Test public void test_8715() { checkNotSubtype("{{void f1} f2,void f3}","{null f1,null f2}"); }
	@Test public void test_8716() { checkNotSubtype("{{void f1} f2,void f3}","{null f2,null f3}"); }
	@Test public void test_8717() { checkNotSubtype("{{void f1} f2,void f3}","{null f1,int f2}"); }
	@Test public void test_8718() { checkNotSubtype("{{void f1} f2,void f3}","{null f2,int f3}"); }
	@Test public void test_8719() { checkIsSubtype("{{void f1} f2,void f3}","{int f1,void f2}"); }
	@Test public void test_8720() { checkIsSubtype("{{void f1} f2,void f3}","{int f2,void f3}"); }
	@Test public void test_8721() { checkNotSubtype("{{void f1} f2,void f3}","{int f1,any f2}"); }
	@Test public void test_8722() { checkNotSubtype("{{void f1} f2,void f3}","{int f2,any f3}"); }
	@Test public void test_8723() { checkNotSubtype("{{void f1} f2,void f3}","{int f1,null f2}"); }
	@Test public void test_8724() { checkNotSubtype("{{void f1} f2,void f3}","{int f2,null f3}"); }
	@Test public void test_8725() { checkNotSubtype("{{void f1} f2,void f3}","{int f1,int f2}"); }
	@Test public void test_8726() { checkNotSubtype("{{void f1} f2,void f3}","{int f2,int f3}"); }
	@Test public void test_8727() { checkNotSubtype("{{void f1} f2,void f3}","{[void] f1}"); }
	@Test public void test_8728() { checkNotSubtype("{{void f1} f2,void f3}","{[void] f2}"); }
	@Test public void test_8729() { checkIsSubtype("{{void f1} f2,void f3}","{[void] f1,void f2}"); }
	@Test public void test_8730() { checkIsSubtype("{{void f1} f2,void f3}","{[void] f2,void f3}"); }
	@Test public void test_8731() { checkNotSubtype("{{void f1} f2,void f3}","{[any] f1}"); }
	@Test public void test_8732() { checkNotSubtype("{{void f1} f2,void f3}","{[any] f2}"); }
	@Test public void test_8733() { checkNotSubtype("{{void f1} f2,void f3}","{[any] f1,any f2}"); }
	@Test public void test_8734() { checkNotSubtype("{{void f1} f2,void f3}","{[any] f2,any f3}"); }
	@Test public void test_8735() { checkNotSubtype("{{void f1} f2,void f3}","{[null] f1}"); }
	@Test public void test_8736() { checkNotSubtype("{{void f1} f2,void f3}","{[null] f2}"); }
	@Test public void test_8737() { checkNotSubtype("{{void f1} f2,void f3}","{[null] f1,null f2}"); }
	@Test public void test_8738() { checkNotSubtype("{{void f1} f2,void f3}","{[null] f2,null f3}"); }
	@Test public void test_8739() { checkNotSubtype("{{void f1} f2,void f3}","{[int] f1}"); }
	@Test public void test_8740() { checkNotSubtype("{{void f1} f2,void f3}","{[int] f2}"); }
	@Test public void test_8741() { checkNotSubtype("{{void f1} f2,void f3}","{[int] f1,int f2}"); }
	@Test public void test_8742() { checkNotSubtype("{{void f1} f2,void f3}","{[int] f2,int f3}"); }
	@Test public void test_8743() { checkIsSubtype("{{void f1} f2,void f3}","{{void f1} f1}"); }
	@Test public void test_8744() { checkIsSubtype("{{void f1} f2,void f3}","{{void f2} f1}"); }
	@Test public void test_8745() { checkIsSubtype("{{void f1} f2,void f3}","{{void f1} f2}"); }
	@Test public void test_8746() { checkIsSubtype("{{void f1} f2,void f3}","{{void f2} f2}"); }
	@Test public void test_8747() { checkIsSubtype("{{void f1} f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_8748() { checkIsSubtype("{{void f1} f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_8749() { checkIsSubtype("{{void f1} f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_8750() { checkIsSubtype("{{void f1} f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_8751() { checkNotSubtype("{{void f1} f2,void f3}","{{any f1} f1}"); }
	@Test public void test_8752() { checkNotSubtype("{{void f1} f2,void f3}","{{any f2} f1}"); }
	@Test public void test_8753() { checkNotSubtype("{{void f1} f2,void f3}","{{any f1} f2}"); }
	@Test public void test_8754() { checkNotSubtype("{{void f1} f2,void f3}","{{any f2} f2}"); }
	@Test public void test_8755() { checkNotSubtype("{{void f1} f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_8756() { checkNotSubtype("{{void f1} f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_8757() { checkNotSubtype("{{void f1} f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_8758() { checkNotSubtype("{{void f1} f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_8759() { checkNotSubtype("{{void f1} f2,void f3}","{{null f1} f1}"); }
	@Test public void test_8760() { checkNotSubtype("{{void f1} f2,void f3}","{{null f2} f1}"); }
	@Test public void test_8761() { checkNotSubtype("{{void f1} f2,void f3}","{{null f1} f2}"); }
	@Test public void test_8762() { checkNotSubtype("{{void f1} f2,void f3}","{{null f2} f2}"); }
	@Test public void test_8763() { checkNotSubtype("{{void f1} f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_8764() { checkNotSubtype("{{void f1} f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_8765() { checkNotSubtype("{{void f1} f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_8766() { checkNotSubtype("{{void f1} f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_8767() { checkNotSubtype("{{void f1} f2,void f3}","{{int f1} f1}"); }
	@Test public void test_8768() { checkNotSubtype("{{void f1} f2,void f3}","{{int f2} f1}"); }
	@Test public void test_8769() { checkNotSubtype("{{void f1} f2,void f3}","{{int f1} f2}"); }
	@Test public void test_8770() { checkNotSubtype("{{void f1} f2,void f3}","{{int f2} f2}"); }
	@Test public void test_8771() { checkNotSubtype("{{void f1} f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_8772() { checkNotSubtype("{{void f1} f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_8773() { checkNotSubtype("{{void f1} f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_8774() { checkNotSubtype("{{void f1} f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_8775() { checkNotSubtype("{{void f2} f2,void f3}","any"); }
	@Test public void test_8776() { checkNotSubtype("{{void f2} f2,void f3}","null"); }
	@Test public void test_8777() { checkNotSubtype("{{void f2} f2,void f3}","int"); }
	@Test public void test_8778() { checkNotSubtype("{{void f2} f2,void f3}","[void]"); }
	@Test public void test_8779() { checkNotSubtype("{{void f2} f2,void f3}","[any]"); }
	@Test public void test_8780() { checkNotSubtype("{{void f2} f2,void f3}","[null]"); }
	@Test public void test_8781() { checkNotSubtype("{{void f2} f2,void f3}","[int]"); }
	@Test public void test_8782() { checkIsSubtype("{{void f2} f2,void f3}","{void f1}"); }
	@Test public void test_8783() { checkIsSubtype("{{void f2} f2,void f3}","{void f2}"); }
	@Test public void test_8784() { checkNotSubtype("{{void f2} f2,void f3}","{any f1}"); }
	@Test public void test_8785() { checkNotSubtype("{{void f2} f2,void f3}","{any f2}"); }
	@Test public void test_8786() { checkNotSubtype("{{void f2} f2,void f3}","{null f1}"); }
	@Test public void test_8787() { checkNotSubtype("{{void f2} f2,void f3}","{null f2}"); }
	@Test public void test_8788() { checkNotSubtype("{{void f2} f2,void f3}","{int f1}"); }
	@Test public void test_8789() { checkNotSubtype("{{void f2} f2,void f3}","{int f2}"); }
	@Test public void test_8790() { checkNotSubtype("{{void f2} f2,void f3}","[[void]]"); }
	@Test public void test_8791() { checkNotSubtype("{{void f2} f2,void f3}","[[any]]"); }
	@Test public void test_8792() { checkNotSubtype("{{void f2} f2,void f3}","[[null]]"); }
	@Test public void test_8793() { checkNotSubtype("{{void f2} f2,void f3}","[[int]]"); }
	@Test public void test_8794() { checkNotSubtype("{{void f2} f2,void f3}","[{void f1}]"); }
	@Test public void test_8795() { checkNotSubtype("{{void f2} f2,void f3}","[{void f2}]"); }
	@Test public void test_8796() { checkNotSubtype("{{void f2} f2,void f3}","[{any f1}]"); }
	@Test public void test_8797() { checkNotSubtype("{{void f2} f2,void f3}","[{any f2}]"); }
	@Test public void test_8798() { checkNotSubtype("{{void f2} f2,void f3}","[{null f1}]"); }
	@Test public void test_8799() { checkNotSubtype("{{void f2} f2,void f3}","[{null f2}]"); }
	@Test public void test_8800() { checkNotSubtype("{{void f2} f2,void f3}","[{int f1}]"); }
	@Test public void test_8801() { checkNotSubtype("{{void f2} f2,void f3}","[{int f2}]"); }
	@Test public void test_8802() { checkIsSubtype("{{void f2} f2,void f3}","{void f1,void f2}"); }
	@Test public void test_8803() { checkIsSubtype("{{void f2} f2,void f3}","{void f2,void f3}"); }
	@Test public void test_8804() { checkIsSubtype("{{void f2} f2,void f3}","{void f1,any f2}"); }
	@Test public void test_8805() { checkIsSubtype("{{void f2} f2,void f3}","{void f2,any f3}"); }
	@Test public void test_8806() { checkIsSubtype("{{void f2} f2,void f3}","{void f1,null f2}"); }
	@Test public void test_8807() { checkIsSubtype("{{void f2} f2,void f3}","{void f2,null f3}"); }
	@Test public void test_8808() { checkIsSubtype("{{void f2} f2,void f3}","{void f1,int f2}"); }
	@Test public void test_8809() { checkIsSubtype("{{void f2} f2,void f3}","{void f2,int f3}"); }
	@Test public void test_8810() { checkIsSubtype("{{void f2} f2,void f3}","{any f1,void f2}"); }
	@Test public void test_8811() { checkIsSubtype("{{void f2} f2,void f3}","{any f2,void f3}"); }
	@Test public void test_8812() { checkNotSubtype("{{void f2} f2,void f3}","{any f1,any f2}"); }
	@Test public void test_8813() { checkNotSubtype("{{void f2} f2,void f3}","{any f2,any f3}"); }
	@Test public void test_8814() { checkNotSubtype("{{void f2} f2,void f3}","{any f1,null f2}"); }
	@Test public void test_8815() { checkNotSubtype("{{void f2} f2,void f3}","{any f2,null f3}"); }
	@Test public void test_8816() { checkNotSubtype("{{void f2} f2,void f3}","{any f1,int f2}"); }
	@Test public void test_8817() { checkNotSubtype("{{void f2} f2,void f3}","{any f2,int f3}"); }
	@Test public void test_8818() { checkIsSubtype("{{void f2} f2,void f3}","{null f1,void f2}"); }
	@Test public void test_8819() { checkIsSubtype("{{void f2} f2,void f3}","{null f2,void f3}"); }
	@Test public void test_8820() { checkNotSubtype("{{void f2} f2,void f3}","{null f1,any f2}"); }
	@Test public void test_8821() { checkNotSubtype("{{void f2} f2,void f3}","{null f2,any f3}"); }
	@Test public void test_8822() { checkNotSubtype("{{void f2} f2,void f3}","{null f1,null f2}"); }
	@Test public void test_8823() { checkNotSubtype("{{void f2} f2,void f3}","{null f2,null f3}"); }
	@Test public void test_8824() { checkNotSubtype("{{void f2} f2,void f3}","{null f1,int f2}"); }
	@Test public void test_8825() { checkNotSubtype("{{void f2} f2,void f3}","{null f2,int f3}"); }
	@Test public void test_8826() { checkIsSubtype("{{void f2} f2,void f3}","{int f1,void f2}"); }
	@Test public void test_8827() { checkIsSubtype("{{void f2} f2,void f3}","{int f2,void f3}"); }
	@Test public void test_8828() { checkNotSubtype("{{void f2} f2,void f3}","{int f1,any f2}"); }
	@Test public void test_8829() { checkNotSubtype("{{void f2} f2,void f3}","{int f2,any f3}"); }
	@Test public void test_8830() { checkNotSubtype("{{void f2} f2,void f3}","{int f1,null f2}"); }
	@Test public void test_8831() { checkNotSubtype("{{void f2} f2,void f3}","{int f2,null f3}"); }
	@Test public void test_8832() { checkNotSubtype("{{void f2} f2,void f3}","{int f1,int f2}"); }
	@Test public void test_8833() { checkNotSubtype("{{void f2} f2,void f3}","{int f2,int f3}"); }
	@Test public void test_8834() { checkNotSubtype("{{void f2} f2,void f3}","{[void] f1}"); }
	@Test public void test_8835() { checkNotSubtype("{{void f2} f2,void f3}","{[void] f2}"); }
	@Test public void test_8836() { checkIsSubtype("{{void f2} f2,void f3}","{[void] f1,void f2}"); }
	@Test public void test_8837() { checkIsSubtype("{{void f2} f2,void f3}","{[void] f2,void f3}"); }
	@Test public void test_8838() { checkNotSubtype("{{void f2} f2,void f3}","{[any] f1}"); }
	@Test public void test_8839() { checkNotSubtype("{{void f2} f2,void f3}","{[any] f2}"); }
	@Test public void test_8840() { checkNotSubtype("{{void f2} f2,void f3}","{[any] f1,any f2}"); }
	@Test public void test_8841() { checkNotSubtype("{{void f2} f2,void f3}","{[any] f2,any f3}"); }
	@Test public void test_8842() { checkNotSubtype("{{void f2} f2,void f3}","{[null] f1}"); }
	@Test public void test_8843() { checkNotSubtype("{{void f2} f2,void f3}","{[null] f2}"); }
	@Test public void test_8844() { checkNotSubtype("{{void f2} f2,void f3}","{[null] f1,null f2}"); }
	@Test public void test_8845() { checkNotSubtype("{{void f2} f2,void f3}","{[null] f2,null f3}"); }
	@Test public void test_8846() { checkNotSubtype("{{void f2} f2,void f3}","{[int] f1}"); }
	@Test public void test_8847() { checkNotSubtype("{{void f2} f2,void f3}","{[int] f2}"); }
	@Test public void test_8848() { checkNotSubtype("{{void f2} f2,void f3}","{[int] f1,int f2}"); }
	@Test public void test_8849() { checkNotSubtype("{{void f2} f2,void f3}","{[int] f2,int f3}"); }
	@Test public void test_8850() { checkIsSubtype("{{void f2} f2,void f3}","{{void f1} f1}"); }
	@Test public void test_8851() { checkIsSubtype("{{void f2} f2,void f3}","{{void f2} f1}"); }
	@Test public void test_8852() { checkIsSubtype("{{void f2} f2,void f3}","{{void f1} f2}"); }
	@Test public void test_8853() { checkIsSubtype("{{void f2} f2,void f3}","{{void f2} f2}"); }
	@Test public void test_8854() { checkIsSubtype("{{void f2} f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_8855() { checkIsSubtype("{{void f2} f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_8856() { checkIsSubtype("{{void f2} f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_8857() { checkIsSubtype("{{void f2} f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_8858() { checkNotSubtype("{{void f2} f2,void f3}","{{any f1} f1}"); }
	@Test public void test_8859() { checkNotSubtype("{{void f2} f2,void f3}","{{any f2} f1}"); }
	@Test public void test_8860() { checkNotSubtype("{{void f2} f2,void f3}","{{any f1} f2}"); }
	@Test public void test_8861() { checkNotSubtype("{{void f2} f2,void f3}","{{any f2} f2}"); }
	@Test public void test_8862() { checkNotSubtype("{{void f2} f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_8863() { checkNotSubtype("{{void f2} f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_8864() { checkNotSubtype("{{void f2} f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_8865() { checkNotSubtype("{{void f2} f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_8866() { checkNotSubtype("{{void f2} f2,void f3}","{{null f1} f1}"); }
	@Test public void test_8867() { checkNotSubtype("{{void f2} f2,void f3}","{{null f2} f1}"); }
	@Test public void test_8868() { checkNotSubtype("{{void f2} f2,void f3}","{{null f1} f2}"); }
	@Test public void test_8869() { checkNotSubtype("{{void f2} f2,void f3}","{{null f2} f2}"); }
	@Test public void test_8870() { checkNotSubtype("{{void f2} f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_8871() { checkNotSubtype("{{void f2} f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_8872() { checkNotSubtype("{{void f2} f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_8873() { checkNotSubtype("{{void f2} f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_8874() { checkNotSubtype("{{void f2} f2,void f3}","{{int f1} f1}"); }
	@Test public void test_8875() { checkNotSubtype("{{void f2} f2,void f3}","{{int f2} f1}"); }
	@Test public void test_8876() { checkNotSubtype("{{void f2} f2,void f3}","{{int f1} f2}"); }
	@Test public void test_8877() { checkNotSubtype("{{void f2} f2,void f3}","{{int f2} f2}"); }
	@Test public void test_8878() { checkNotSubtype("{{void f2} f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_8879() { checkNotSubtype("{{void f2} f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_8880() { checkNotSubtype("{{void f2} f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_8881() { checkNotSubtype("{{void f2} f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_8882() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_8883() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_8884() { checkNotSubtype("{{any f1} f1}","int"); }
	@Test public void test_8885() { checkNotSubtype("{{any f1} f1}","[void]"); }
	@Test public void test_8886() { checkNotSubtype("{{any f1} f1}","[any]"); }
	@Test public void test_8887() { checkNotSubtype("{{any f1} f1}","[null]"); }
	@Test public void test_8888() { checkNotSubtype("{{any f1} f1}","[int]"); }
	@Test public void test_8889() { checkIsSubtype("{{any f1} f1}","{void f1}"); }
	@Test public void test_8890() { checkIsSubtype("{{any f1} f1}","{void f2}"); }
	@Test public void test_8891() { checkNotSubtype("{{any f1} f1}","{any f1}"); }
	@Test public void test_8892() { checkNotSubtype("{{any f1} f1}","{any f2}"); }
	@Test public void test_8893() { checkNotSubtype("{{any f1} f1}","{null f1}"); }
	@Test public void test_8894() { checkNotSubtype("{{any f1} f1}","{null f2}"); }
	@Test public void test_8895() { checkNotSubtype("{{any f1} f1}","{int f1}"); }
	@Test public void test_8896() { checkNotSubtype("{{any f1} f1}","{int f2}"); }
	@Test public void test_8897() { checkNotSubtype("{{any f1} f1}","[[void]]"); }
	@Test public void test_8898() { checkNotSubtype("{{any f1} f1}","[[any]]"); }
	@Test public void test_8899() { checkNotSubtype("{{any f1} f1}","[[null]]"); }
	@Test public void test_8900() { checkNotSubtype("{{any f1} f1}","[[int]]"); }
	@Test public void test_8901() { checkNotSubtype("{{any f1} f1}","[{void f1}]"); }
	@Test public void test_8902() { checkNotSubtype("{{any f1} f1}","[{void f2}]"); }
	@Test public void test_8903() { checkNotSubtype("{{any f1} f1}","[{any f1}]"); }
	@Test public void test_8904() { checkNotSubtype("{{any f1} f1}","[{any f2}]"); }
	@Test public void test_8905() { checkNotSubtype("{{any f1} f1}","[{null f1}]"); }
	@Test public void test_8906() { checkNotSubtype("{{any f1} f1}","[{null f2}]"); }
	@Test public void test_8907() { checkNotSubtype("{{any f1} f1}","[{int f1}]"); }
	@Test public void test_8908() { checkNotSubtype("{{any f1} f1}","[{int f2}]"); }
	@Test public void test_8909() { checkIsSubtype("{{any f1} f1}","{void f1,void f2}"); }
	@Test public void test_8910() { checkIsSubtype("{{any f1} f1}","{void f2,void f3}"); }
	@Test public void test_8911() { checkIsSubtype("{{any f1} f1}","{void f1,any f2}"); }
	@Test public void test_8912() { checkIsSubtype("{{any f1} f1}","{void f2,any f3}"); }
	@Test public void test_8913() { checkIsSubtype("{{any f1} f1}","{void f1,null f2}"); }
	@Test public void test_8914() { checkIsSubtype("{{any f1} f1}","{void f2,null f3}"); }
	@Test public void test_8915() { checkIsSubtype("{{any f1} f1}","{void f1,int f2}"); }
	@Test public void test_8916() { checkIsSubtype("{{any f1} f1}","{void f2,int f3}"); }
	@Test public void test_8917() { checkIsSubtype("{{any f1} f1}","{any f1,void f2}"); }
	@Test public void test_8918() { checkIsSubtype("{{any f1} f1}","{any f2,void f3}"); }
	@Test public void test_8919() { checkNotSubtype("{{any f1} f1}","{any f1,any f2}"); }
	@Test public void test_8920() { checkNotSubtype("{{any f1} f1}","{any f2,any f3}"); }
	@Test public void test_8921() { checkNotSubtype("{{any f1} f1}","{any f1,null f2}"); }
	@Test public void test_8922() { checkNotSubtype("{{any f1} f1}","{any f2,null f3}"); }
	@Test public void test_8923() { checkNotSubtype("{{any f1} f1}","{any f1,int f2}"); }
	@Test public void test_8924() { checkNotSubtype("{{any f1} f1}","{any f2,int f3}"); }
	@Test public void test_8925() { checkIsSubtype("{{any f1} f1}","{null f1,void f2}"); }
	@Test public void test_8926() { checkIsSubtype("{{any f1} f1}","{null f2,void f3}"); }
	@Test public void test_8927() { checkNotSubtype("{{any f1} f1}","{null f1,any f2}"); }
	@Test public void test_8928() { checkNotSubtype("{{any f1} f1}","{null f2,any f3}"); }
	@Test public void test_8929() { checkNotSubtype("{{any f1} f1}","{null f1,null f2}"); }
	@Test public void test_8930() { checkNotSubtype("{{any f1} f1}","{null f2,null f3}"); }
	@Test public void test_8931() { checkNotSubtype("{{any f1} f1}","{null f1,int f2}"); }
	@Test public void test_8932() { checkNotSubtype("{{any f1} f1}","{null f2,int f3}"); }
	@Test public void test_8933() { checkIsSubtype("{{any f1} f1}","{int f1,void f2}"); }
	@Test public void test_8934() { checkIsSubtype("{{any f1} f1}","{int f2,void f3}"); }
	@Test public void test_8935() { checkNotSubtype("{{any f1} f1}","{int f1,any f2}"); }
	@Test public void test_8936() { checkNotSubtype("{{any f1} f1}","{int f2,any f3}"); }
	@Test public void test_8937() { checkNotSubtype("{{any f1} f1}","{int f1,null f2}"); }
	@Test public void test_8938() { checkNotSubtype("{{any f1} f1}","{int f2,null f3}"); }
	@Test public void test_8939() { checkNotSubtype("{{any f1} f1}","{int f1,int f2}"); }
	@Test public void test_8940() { checkNotSubtype("{{any f1} f1}","{int f2,int f3}"); }
	@Test public void test_8941() { checkNotSubtype("{{any f1} f1}","{[void] f1}"); }
	@Test public void test_8942() { checkNotSubtype("{{any f1} f1}","{[void] f2}"); }
	@Test public void test_8943() { checkIsSubtype("{{any f1} f1}","{[void] f1,void f2}"); }
	@Test public void test_8944() { checkIsSubtype("{{any f1} f1}","{[void] f2,void f3}"); }
	@Test public void test_8945() { checkNotSubtype("{{any f1} f1}","{[any] f1}"); }
	@Test public void test_8946() { checkNotSubtype("{{any f1} f1}","{[any] f2}"); }
	@Test public void test_8947() { checkNotSubtype("{{any f1} f1}","{[any] f1,any f2}"); }
	@Test public void test_8948() { checkNotSubtype("{{any f1} f1}","{[any] f2,any f3}"); }
	@Test public void test_8949() { checkNotSubtype("{{any f1} f1}","{[null] f1}"); }
	@Test public void test_8950() { checkNotSubtype("{{any f1} f1}","{[null] f2}"); }
	@Test public void test_8951() { checkNotSubtype("{{any f1} f1}","{[null] f1,null f2}"); }
	@Test public void test_8952() { checkNotSubtype("{{any f1} f1}","{[null] f2,null f3}"); }
	@Test public void test_8953() { checkNotSubtype("{{any f1} f1}","{[int] f1}"); }
	@Test public void test_8954() { checkNotSubtype("{{any f1} f1}","{[int] f2}"); }
	@Test public void test_8955() { checkNotSubtype("{{any f1} f1}","{[int] f1,int f2}"); }
	@Test public void test_8956() { checkNotSubtype("{{any f1} f1}","{[int] f2,int f3}"); }
	@Test public void test_8957() { checkIsSubtype("{{any f1} f1}","{{void f1} f1}"); }
	@Test public void test_8958() { checkIsSubtype("{{any f1} f1}","{{void f2} f1}"); }
	@Test public void test_8959() { checkIsSubtype("{{any f1} f1}","{{void f1} f2}"); }
	@Test public void test_8960() { checkIsSubtype("{{any f1} f1}","{{void f2} f2}"); }
	@Test public void test_8961() { checkIsSubtype("{{any f1} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_8962() { checkIsSubtype("{{any f1} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_8963() { checkIsSubtype("{{any f1} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_8964() { checkIsSubtype("{{any f1} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_8965() { checkIsSubtype("{{any f1} f1}","{{any f1} f1}"); }
	@Test public void test_8966() { checkNotSubtype("{{any f1} f1}","{{any f2} f1}"); }
	@Test public void test_8967() { checkNotSubtype("{{any f1} f1}","{{any f1} f2}"); }
	@Test public void test_8968() { checkNotSubtype("{{any f1} f1}","{{any f2} f2}"); }
	@Test public void test_8969() { checkNotSubtype("{{any f1} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_8970() { checkNotSubtype("{{any f1} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_8971() { checkNotSubtype("{{any f1} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_8972() { checkNotSubtype("{{any f1} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_8973() { checkIsSubtype("{{any f1} f1}","{{null f1} f1}"); }
	@Test public void test_8974() { checkNotSubtype("{{any f1} f1}","{{null f2} f1}"); }
	@Test public void test_8975() { checkNotSubtype("{{any f1} f1}","{{null f1} f2}"); }
	@Test public void test_8976() { checkNotSubtype("{{any f1} f1}","{{null f2} f2}"); }
	@Test public void test_8977() { checkNotSubtype("{{any f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_8978() { checkNotSubtype("{{any f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_8979() { checkNotSubtype("{{any f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_8980() { checkNotSubtype("{{any f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_8981() { checkIsSubtype("{{any f1} f1}","{{int f1} f1}"); }
	@Test public void test_8982() { checkNotSubtype("{{any f1} f1}","{{int f2} f1}"); }
	@Test public void test_8983() { checkNotSubtype("{{any f1} f1}","{{int f1} f2}"); }
	@Test public void test_8984() { checkNotSubtype("{{any f1} f1}","{{int f2} f2}"); }
	@Test public void test_8985() { checkNotSubtype("{{any f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_8986() { checkNotSubtype("{{any f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_8987() { checkNotSubtype("{{any f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_8988() { checkNotSubtype("{{any f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_8989() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_8990() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_8991() { checkNotSubtype("{{any f2} f1}","int"); }
	@Test public void test_8992() { checkNotSubtype("{{any f2} f1}","[void]"); }
	@Test public void test_8993() { checkNotSubtype("{{any f2} f1}","[any]"); }
	@Test public void test_8994() { checkNotSubtype("{{any f2} f1}","[null]"); }
	@Test public void test_8995() { checkNotSubtype("{{any f2} f1}","[int]"); }
	@Test public void test_8996() { checkIsSubtype("{{any f2} f1}","{void f1}"); }
	@Test public void test_8997() { checkIsSubtype("{{any f2} f1}","{void f2}"); }
	@Test public void test_8998() { checkNotSubtype("{{any f2} f1}","{any f1}"); }
	@Test public void test_8999() { checkNotSubtype("{{any f2} f1}","{any f2}"); }
	@Test public void test_9000() { checkNotSubtype("{{any f2} f1}","{null f1}"); }
	@Test public void test_9001() { checkNotSubtype("{{any f2} f1}","{null f2}"); }
	@Test public void test_9002() { checkNotSubtype("{{any f2} f1}","{int f1}"); }
	@Test public void test_9003() { checkNotSubtype("{{any f2} f1}","{int f2}"); }
	@Test public void test_9004() { checkNotSubtype("{{any f2} f1}","[[void]]"); }
	@Test public void test_9005() { checkNotSubtype("{{any f2} f1}","[[any]]"); }
	@Test public void test_9006() { checkNotSubtype("{{any f2} f1}","[[null]]"); }
	@Test public void test_9007() { checkNotSubtype("{{any f2} f1}","[[int]]"); }
	@Test public void test_9008() { checkNotSubtype("{{any f2} f1}","[{void f1}]"); }
	@Test public void test_9009() { checkNotSubtype("{{any f2} f1}","[{void f2}]"); }
	@Test public void test_9010() { checkNotSubtype("{{any f2} f1}","[{any f1}]"); }
	@Test public void test_9011() { checkNotSubtype("{{any f2} f1}","[{any f2}]"); }
	@Test public void test_9012() { checkNotSubtype("{{any f2} f1}","[{null f1}]"); }
	@Test public void test_9013() { checkNotSubtype("{{any f2} f1}","[{null f2}]"); }
	@Test public void test_9014() { checkNotSubtype("{{any f2} f1}","[{int f1}]"); }
	@Test public void test_9015() { checkNotSubtype("{{any f2} f1}","[{int f2}]"); }
	@Test public void test_9016() { checkIsSubtype("{{any f2} f1}","{void f1,void f2}"); }
	@Test public void test_9017() { checkIsSubtype("{{any f2} f1}","{void f2,void f3}"); }
	@Test public void test_9018() { checkIsSubtype("{{any f2} f1}","{void f1,any f2}"); }
	@Test public void test_9019() { checkIsSubtype("{{any f2} f1}","{void f2,any f3}"); }
	@Test public void test_9020() { checkIsSubtype("{{any f2} f1}","{void f1,null f2}"); }
	@Test public void test_9021() { checkIsSubtype("{{any f2} f1}","{void f2,null f3}"); }
	@Test public void test_9022() { checkIsSubtype("{{any f2} f1}","{void f1,int f2}"); }
	@Test public void test_9023() { checkIsSubtype("{{any f2} f1}","{void f2,int f3}"); }
	@Test public void test_9024() { checkIsSubtype("{{any f2} f1}","{any f1,void f2}"); }
	@Test public void test_9025() { checkIsSubtype("{{any f2} f1}","{any f2,void f3}"); }
	@Test public void test_9026() { checkNotSubtype("{{any f2} f1}","{any f1,any f2}"); }
	@Test public void test_9027() { checkNotSubtype("{{any f2} f1}","{any f2,any f3}"); }
	@Test public void test_9028() { checkNotSubtype("{{any f2} f1}","{any f1,null f2}"); }
	@Test public void test_9029() { checkNotSubtype("{{any f2} f1}","{any f2,null f3}"); }
	@Test public void test_9030() { checkNotSubtype("{{any f2} f1}","{any f1,int f2}"); }
	@Test public void test_9031() { checkNotSubtype("{{any f2} f1}","{any f2,int f3}"); }
	@Test public void test_9032() { checkIsSubtype("{{any f2} f1}","{null f1,void f2}"); }
	@Test public void test_9033() { checkIsSubtype("{{any f2} f1}","{null f2,void f3}"); }
	@Test public void test_9034() { checkNotSubtype("{{any f2} f1}","{null f1,any f2}"); }
	@Test public void test_9035() { checkNotSubtype("{{any f2} f1}","{null f2,any f3}"); }
	@Test public void test_9036() { checkNotSubtype("{{any f2} f1}","{null f1,null f2}"); }
	@Test public void test_9037() { checkNotSubtype("{{any f2} f1}","{null f2,null f3}"); }
	@Test public void test_9038() { checkNotSubtype("{{any f2} f1}","{null f1,int f2}"); }
	@Test public void test_9039() { checkNotSubtype("{{any f2} f1}","{null f2,int f3}"); }
	@Test public void test_9040() { checkIsSubtype("{{any f2} f1}","{int f1,void f2}"); }
	@Test public void test_9041() { checkIsSubtype("{{any f2} f1}","{int f2,void f3}"); }
	@Test public void test_9042() { checkNotSubtype("{{any f2} f1}","{int f1,any f2}"); }
	@Test public void test_9043() { checkNotSubtype("{{any f2} f1}","{int f2,any f3}"); }
	@Test public void test_9044() { checkNotSubtype("{{any f2} f1}","{int f1,null f2}"); }
	@Test public void test_9045() { checkNotSubtype("{{any f2} f1}","{int f2,null f3}"); }
	@Test public void test_9046() { checkNotSubtype("{{any f2} f1}","{int f1,int f2}"); }
	@Test public void test_9047() { checkNotSubtype("{{any f2} f1}","{int f2,int f3}"); }
	@Test public void test_9048() { checkNotSubtype("{{any f2} f1}","{[void] f1}"); }
	@Test public void test_9049() { checkNotSubtype("{{any f2} f1}","{[void] f2}"); }
	@Test public void test_9050() { checkIsSubtype("{{any f2} f1}","{[void] f1,void f2}"); }
	@Test public void test_9051() { checkIsSubtype("{{any f2} f1}","{[void] f2,void f3}"); }
	@Test public void test_9052() { checkNotSubtype("{{any f2} f1}","{[any] f1}"); }
	@Test public void test_9053() { checkNotSubtype("{{any f2} f1}","{[any] f2}"); }
	@Test public void test_9054() { checkNotSubtype("{{any f2} f1}","{[any] f1,any f2}"); }
	@Test public void test_9055() { checkNotSubtype("{{any f2} f1}","{[any] f2,any f3}"); }
	@Test public void test_9056() { checkNotSubtype("{{any f2} f1}","{[null] f1}"); }
	@Test public void test_9057() { checkNotSubtype("{{any f2} f1}","{[null] f2}"); }
	@Test public void test_9058() { checkNotSubtype("{{any f2} f1}","{[null] f1,null f2}"); }
	@Test public void test_9059() { checkNotSubtype("{{any f2} f1}","{[null] f2,null f3}"); }
	@Test public void test_9060() { checkNotSubtype("{{any f2} f1}","{[int] f1}"); }
	@Test public void test_9061() { checkNotSubtype("{{any f2} f1}","{[int] f2}"); }
	@Test public void test_9062() { checkNotSubtype("{{any f2} f1}","{[int] f1,int f2}"); }
	@Test public void test_9063() { checkNotSubtype("{{any f2} f1}","{[int] f2,int f3}"); }
	@Test public void test_9064() { checkIsSubtype("{{any f2} f1}","{{void f1} f1}"); }
	@Test public void test_9065() { checkIsSubtype("{{any f2} f1}","{{void f2} f1}"); }
	@Test public void test_9066() { checkIsSubtype("{{any f2} f1}","{{void f1} f2}"); }
	@Test public void test_9067() { checkIsSubtype("{{any f2} f1}","{{void f2} f2}"); }
	@Test public void test_9068() { checkIsSubtype("{{any f2} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_9069() { checkIsSubtype("{{any f2} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_9070() { checkIsSubtype("{{any f2} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_9071() { checkIsSubtype("{{any f2} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_9072() { checkNotSubtype("{{any f2} f1}","{{any f1} f1}"); }
	@Test public void test_9073() { checkIsSubtype("{{any f2} f1}","{{any f2} f1}"); }
	@Test public void test_9074() { checkNotSubtype("{{any f2} f1}","{{any f1} f2}"); }
	@Test public void test_9075() { checkNotSubtype("{{any f2} f1}","{{any f2} f2}"); }
	@Test public void test_9076() { checkNotSubtype("{{any f2} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_9077() { checkNotSubtype("{{any f2} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_9078() { checkNotSubtype("{{any f2} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_9079() { checkNotSubtype("{{any f2} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_9080() { checkNotSubtype("{{any f2} f1}","{{null f1} f1}"); }
	@Test public void test_9081() { checkIsSubtype("{{any f2} f1}","{{null f2} f1}"); }
	@Test public void test_9082() { checkNotSubtype("{{any f2} f1}","{{null f1} f2}"); }
	@Test public void test_9083() { checkNotSubtype("{{any f2} f1}","{{null f2} f2}"); }
	@Test public void test_9084() { checkNotSubtype("{{any f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_9085() { checkNotSubtype("{{any f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_9086() { checkNotSubtype("{{any f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_9087() { checkNotSubtype("{{any f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_9088() { checkNotSubtype("{{any f2} f1}","{{int f1} f1}"); }
	@Test public void test_9089() { checkIsSubtype("{{any f2} f1}","{{int f2} f1}"); }
	@Test public void test_9090() { checkNotSubtype("{{any f2} f1}","{{int f1} f2}"); }
	@Test public void test_9091() { checkNotSubtype("{{any f2} f1}","{{int f2} f2}"); }
	@Test public void test_9092() { checkNotSubtype("{{any f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_9093() { checkNotSubtype("{{any f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_9094() { checkNotSubtype("{{any f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_9095() { checkNotSubtype("{{any f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_9096() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_9097() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_9098() { checkNotSubtype("{{any f1} f2}","int"); }
	@Test public void test_9099() { checkNotSubtype("{{any f1} f2}","[void]"); }
	@Test public void test_9100() { checkNotSubtype("{{any f1} f2}","[any]"); }
	@Test public void test_9101() { checkNotSubtype("{{any f1} f2}","[null]"); }
	@Test public void test_9102() { checkNotSubtype("{{any f1} f2}","[int]"); }
	@Test public void test_9103() { checkIsSubtype("{{any f1} f2}","{void f1}"); }
	@Test public void test_9104() { checkIsSubtype("{{any f1} f2}","{void f2}"); }
	@Test public void test_9105() { checkNotSubtype("{{any f1} f2}","{any f1}"); }
	@Test public void test_9106() { checkNotSubtype("{{any f1} f2}","{any f2}"); }
	@Test public void test_9107() { checkNotSubtype("{{any f1} f2}","{null f1}"); }
	@Test public void test_9108() { checkNotSubtype("{{any f1} f2}","{null f2}"); }
	@Test public void test_9109() { checkNotSubtype("{{any f1} f2}","{int f1}"); }
	@Test public void test_9110() { checkNotSubtype("{{any f1} f2}","{int f2}"); }
	@Test public void test_9111() { checkNotSubtype("{{any f1} f2}","[[void]]"); }
	@Test public void test_9112() { checkNotSubtype("{{any f1} f2}","[[any]]"); }
	@Test public void test_9113() { checkNotSubtype("{{any f1} f2}","[[null]]"); }
	@Test public void test_9114() { checkNotSubtype("{{any f1} f2}","[[int]]"); }
	@Test public void test_9115() { checkNotSubtype("{{any f1} f2}","[{void f1}]"); }
	@Test public void test_9116() { checkNotSubtype("{{any f1} f2}","[{void f2}]"); }
	@Test public void test_9117() { checkNotSubtype("{{any f1} f2}","[{any f1}]"); }
	@Test public void test_9118() { checkNotSubtype("{{any f1} f2}","[{any f2}]"); }
	@Test public void test_9119() { checkNotSubtype("{{any f1} f2}","[{null f1}]"); }
	@Test public void test_9120() { checkNotSubtype("{{any f1} f2}","[{null f2}]"); }
	@Test public void test_9121() { checkNotSubtype("{{any f1} f2}","[{int f1}]"); }
	@Test public void test_9122() { checkNotSubtype("{{any f1} f2}","[{int f2}]"); }
	@Test public void test_9123() { checkIsSubtype("{{any f1} f2}","{void f1,void f2}"); }
	@Test public void test_9124() { checkIsSubtype("{{any f1} f2}","{void f2,void f3}"); }
	@Test public void test_9125() { checkIsSubtype("{{any f1} f2}","{void f1,any f2}"); }
	@Test public void test_9126() { checkIsSubtype("{{any f1} f2}","{void f2,any f3}"); }
	@Test public void test_9127() { checkIsSubtype("{{any f1} f2}","{void f1,null f2}"); }
	@Test public void test_9128() { checkIsSubtype("{{any f1} f2}","{void f2,null f3}"); }
	@Test public void test_9129() { checkIsSubtype("{{any f1} f2}","{void f1,int f2}"); }
	@Test public void test_9130() { checkIsSubtype("{{any f1} f2}","{void f2,int f3}"); }
	@Test public void test_9131() { checkIsSubtype("{{any f1} f2}","{any f1,void f2}"); }
	@Test public void test_9132() { checkIsSubtype("{{any f1} f2}","{any f2,void f3}"); }
	@Test public void test_9133() { checkNotSubtype("{{any f1} f2}","{any f1,any f2}"); }
	@Test public void test_9134() { checkNotSubtype("{{any f1} f2}","{any f2,any f3}"); }
	@Test public void test_9135() { checkNotSubtype("{{any f1} f2}","{any f1,null f2}"); }
	@Test public void test_9136() { checkNotSubtype("{{any f1} f2}","{any f2,null f3}"); }
	@Test public void test_9137() { checkNotSubtype("{{any f1} f2}","{any f1,int f2}"); }
	@Test public void test_9138() { checkNotSubtype("{{any f1} f2}","{any f2,int f3}"); }
	@Test public void test_9139() { checkIsSubtype("{{any f1} f2}","{null f1,void f2}"); }
	@Test public void test_9140() { checkIsSubtype("{{any f1} f2}","{null f2,void f3}"); }
	@Test public void test_9141() { checkNotSubtype("{{any f1} f2}","{null f1,any f2}"); }
	@Test public void test_9142() { checkNotSubtype("{{any f1} f2}","{null f2,any f3}"); }
	@Test public void test_9143() { checkNotSubtype("{{any f1} f2}","{null f1,null f2}"); }
	@Test public void test_9144() { checkNotSubtype("{{any f1} f2}","{null f2,null f3}"); }
	@Test public void test_9145() { checkNotSubtype("{{any f1} f2}","{null f1,int f2}"); }
	@Test public void test_9146() { checkNotSubtype("{{any f1} f2}","{null f2,int f3}"); }
	@Test public void test_9147() { checkIsSubtype("{{any f1} f2}","{int f1,void f2}"); }
	@Test public void test_9148() { checkIsSubtype("{{any f1} f2}","{int f2,void f3}"); }
	@Test public void test_9149() { checkNotSubtype("{{any f1} f2}","{int f1,any f2}"); }
	@Test public void test_9150() { checkNotSubtype("{{any f1} f2}","{int f2,any f3}"); }
	@Test public void test_9151() { checkNotSubtype("{{any f1} f2}","{int f1,null f2}"); }
	@Test public void test_9152() { checkNotSubtype("{{any f1} f2}","{int f2,null f3}"); }
	@Test public void test_9153() { checkNotSubtype("{{any f1} f2}","{int f1,int f2}"); }
	@Test public void test_9154() { checkNotSubtype("{{any f1} f2}","{int f2,int f3}"); }
	@Test public void test_9155() { checkNotSubtype("{{any f1} f2}","{[void] f1}"); }
	@Test public void test_9156() { checkNotSubtype("{{any f1} f2}","{[void] f2}"); }
	@Test public void test_9157() { checkIsSubtype("{{any f1} f2}","{[void] f1,void f2}"); }
	@Test public void test_9158() { checkIsSubtype("{{any f1} f2}","{[void] f2,void f3}"); }
	@Test public void test_9159() { checkNotSubtype("{{any f1} f2}","{[any] f1}"); }
	@Test public void test_9160() { checkNotSubtype("{{any f1} f2}","{[any] f2}"); }
	@Test public void test_9161() { checkNotSubtype("{{any f1} f2}","{[any] f1,any f2}"); }
	@Test public void test_9162() { checkNotSubtype("{{any f1} f2}","{[any] f2,any f3}"); }
	@Test public void test_9163() { checkNotSubtype("{{any f1} f2}","{[null] f1}"); }
	@Test public void test_9164() { checkNotSubtype("{{any f1} f2}","{[null] f2}"); }
	@Test public void test_9165() { checkNotSubtype("{{any f1} f2}","{[null] f1,null f2}"); }
	@Test public void test_9166() { checkNotSubtype("{{any f1} f2}","{[null] f2,null f3}"); }
	@Test public void test_9167() { checkNotSubtype("{{any f1} f2}","{[int] f1}"); }
	@Test public void test_9168() { checkNotSubtype("{{any f1} f2}","{[int] f2}"); }
	@Test public void test_9169() { checkNotSubtype("{{any f1} f2}","{[int] f1,int f2}"); }
	@Test public void test_9170() { checkNotSubtype("{{any f1} f2}","{[int] f2,int f3}"); }
	@Test public void test_9171() { checkIsSubtype("{{any f1} f2}","{{void f1} f1}"); }
	@Test public void test_9172() { checkIsSubtype("{{any f1} f2}","{{void f2} f1}"); }
	@Test public void test_9173() { checkIsSubtype("{{any f1} f2}","{{void f1} f2}"); }
	@Test public void test_9174() { checkIsSubtype("{{any f1} f2}","{{void f2} f2}"); }
	@Test public void test_9175() { checkIsSubtype("{{any f1} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_9176() { checkIsSubtype("{{any f1} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_9177() { checkIsSubtype("{{any f1} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_9178() { checkIsSubtype("{{any f1} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_9179() { checkNotSubtype("{{any f1} f2}","{{any f1} f1}"); }
	@Test public void test_9180() { checkNotSubtype("{{any f1} f2}","{{any f2} f1}"); }
	@Test public void test_9181() { checkIsSubtype("{{any f1} f2}","{{any f1} f2}"); }
	@Test public void test_9182() { checkNotSubtype("{{any f1} f2}","{{any f2} f2}"); }
	@Test public void test_9183() { checkNotSubtype("{{any f1} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_9184() { checkNotSubtype("{{any f1} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_9185() { checkNotSubtype("{{any f1} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_9186() { checkNotSubtype("{{any f1} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_9187() { checkNotSubtype("{{any f1} f2}","{{null f1} f1}"); }
	@Test public void test_9188() { checkNotSubtype("{{any f1} f2}","{{null f2} f1}"); }
	@Test public void test_9189() { checkIsSubtype("{{any f1} f2}","{{null f1} f2}"); }
	@Test public void test_9190() { checkNotSubtype("{{any f1} f2}","{{null f2} f2}"); }
	@Test public void test_9191() { checkNotSubtype("{{any f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_9192() { checkNotSubtype("{{any f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_9193() { checkNotSubtype("{{any f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_9194() { checkNotSubtype("{{any f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_9195() { checkNotSubtype("{{any f1} f2}","{{int f1} f1}"); }
	@Test public void test_9196() { checkNotSubtype("{{any f1} f2}","{{int f2} f1}"); }
	@Test public void test_9197() { checkIsSubtype("{{any f1} f2}","{{int f1} f2}"); }
	@Test public void test_9198() { checkNotSubtype("{{any f1} f2}","{{int f2} f2}"); }
	@Test public void test_9199() { checkNotSubtype("{{any f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_9200() { checkNotSubtype("{{any f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_9201() { checkNotSubtype("{{any f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_9202() { checkNotSubtype("{{any f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_9203() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_9204() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_9205() { checkNotSubtype("{{any f2} f2}","int"); }
	@Test public void test_9206() { checkNotSubtype("{{any f2} f2}","[void]"); }
	@Test public void test_9207() { checkNotSubtype("{{any f2} f2}","[any]"); }
	@Test public void test_9208() { checkNotSubtype("{{any f2} f2}","[null]"); }
	@Test public void test_9209() { checkNotSubtype("{{any f2} f2}","[int]"); }
	@Test public void test_9210() { checkIsSubtype("{{any f2} f2}","{void f1}"); }
	@Test public void test_9211() { checkIsSubtype("{{any f2} f2}","{void f2}"); }
	@Test public void test_9212() { checkNotSubtype("{{any f2} f2}","{any f1}"); }
	@Test public void test_9213() { checkNotSubtype("{{any f2} f2}","{any f2}"); }
	@Test public void test_9214() { checkNotSubtype("{{any f2} f2}","{null f1}"); }
	@Test public void test_9215() { checkNotSubtype("{{any f2} f2}","{null f2}"); }
	@Test public void test_9216() { checkNotSubtype("{{any f2} f2}","{int f1}"); }
	@Test public void test_9217() { checkNotSubtype("{{any f2} f2}","{int f2}"); }
	@Test public void test_9218() { checkNotSubtype("{{any f2} f2}","[[void]]"); }
	@Test public void test_9219() { checkNotSubtype("{{any f2} f2}","[[any]]"); }
	@Test public void test_9220() { checkNotSubtype("{{any f2} f2}","[[null]]"); }
	@Test public void test_9221() { checkNotSubtype("{{any f2} f2}","[[int]]"); }
	@Test public void test_9222() { checkNotSubtype("{{any f2} f2}","[{void f1}]"); }
	@Test public void test_9223() { checkNotSubtype("{{any f2} f2}","[{void f2}]"); }
	@Test public void test_9224() { checkNotSubtype("{{any f2} f2}","[{any f1}]"); }
	@Test public void test_9225() { checkNotSubtype("{{any f2} f2}","[{any f2}]"); }
	@Test public void test_9226() { checkNotSubtype("{{any f2} f2}","[{null f1}]"); }
	@Test public void test_9227() { checkNotSubtype("{{any f2} f2}","[{null f2}]"); }
	@Test public void test_9228() { checkNotSubtype("{{any f2} f2}","[{int f1}]"); }
	@Test public void test_9229() { checkNotSubtype("{{any f2} f2}","[{int f2}]"); }
	@Test public void test_9230() { checkIsSubtype("{{any f2} f2}","{void f1,void f2}"); }
	@Test public void test_9231() { checkIsSubtype("{{any f2} f2}","{void f2,void f3}"); }
	@Test public void test_9232() { checkIsSubtype("{{any f2} f2}","{void f1,any f2}"); }
	@Test public void test_9233() { checkIsSubtype("{{any f2} f2}","{void f2,any f3}"); }
	@Test public void test_9234() { checkIsSubtype("{{any f2} f2}","{void f1,null f2}"); }
	@Test public void test_9235() { checkIsSubtype("{{any f2} f2}","{void f2,null f3}"); }
	@Test public void test_9236() { checkIsSubtype("{{any f2} f2}","{void f1,int f2}"); }
	@Test public void test_9237() { checkIsSubtype("{{any f2} f2}","{void f2,int f3}"); }
	@Test public void test_9238() { checkIsSubtype("{{any f2} f2}","{any f1,void f2}"); }
	@Test public void test_9239() { checkIsSubtype("{{any f2} f2}","{any f2,void f3}"); }
	@Test public void test_9240() { checkNotSubtype("{{any f2} f2}","{any f1,any f2}"); }
	@Test public void test_9241() { checkNotSubtype("{{any f2} f2}","{any f2,any f3}"); }
	@Test public void test_9242() { checkNotSubtype("{{any f2} f2}","{any f1,null f2}"); }
	@Test public void test_9243() { checkNotSubtype("{{any f2} f2}","{any f2,null f3}"); }
	@Test public void test_9244() { checkNotSubtype("{{any f2} f2}","{any f1,int f2}"); }
	@Test public void test_9245() { checkNotSubtype("{{any f2} f2}","{any f2,int f3}"); }
	@Test public void test_9246() { checkIsSubtype("{{any f2} f2}","{null f1,void f2}"); }
	@Test public void test_9247() { checkIsSubtype("{{any f2} f2}","{null f2,void f3}"); }
	@Test public void test_9248() { checkNotSubtype("{{any f2} f2}","{null f1,any f2}"); }
	@Test public void test_9249() { checkNotSubtype("{{any f2} f2}","{null f2,any f3}"); }
	@Test public void test_9250() { checkNotSubtype("{{any f2} f2}","{null f1,null f2}"); }
	@Test public void test_9251() { checkNotSubtype("{{any f2} f2}","{null f2,null f3}"); }
	@Test public void test_9252() { checkNotSubtype("{{any f2} f2}","{null f1,int f2}"); }
	@Test public void test_9253() { checkNotSubtype("{{any f2} f2}","{null f2,int f3}"); }
	@Test public void test_9254() { checkIsSubtype("{{any f2} f2}","{int f1,void f2}"); }
	@Test public void test_9255() { checkIsSubtype("{{any f2} f2}","{int f2,void f3}"); }
	@Test public void test_9256() { checkNotSubtype("{{any f2} f2}","{int f1,any f2}"); }
	@Test public void test_9257() { checkNotSubtype("{{any f2} f2}","{int f2,any f3}"); }
	@Test public void test_9258() { checkNotSubtype("{{any f2} f2}","{int f1,null f2}"); }
	@Test public void test_9259() { checkNotSubtype("{{any f2} f2}","{int f2,null f3}"); }
	@Test public void test_9260() { checkNotSubtype("{{any f2} f2}","{int f1,int f2}"); }
	@Test public void test_9261() { checkNotSubtype("{{any f2} f2}","{int f2,int f3}"); }
	@Test public void test_9262() { checkNotSubtype("{{any f2} f2}","{[void] f1}"); }
	@Test public void test_9263() { checkNotSubtype("{{any f2} f2}","{[void] f2}"); }
	@Test public void test_9264() { checkIsSubtype("{{any f2} f2}","{[void] f1,void f2}"); }
	@Test public void test_9265() { checkIsSubtype("{{any f2} f2}","{[void] f2,void f3}"); }
	@Test public void test_9266() { checkNotSubtype("{{any f2} f2}","{[any] f1}"); }
	@Test public void test_9267() { checkNotSubtype("{{any f2} f2}","{[any] f2}"); }
	@Test public void test_9268() { checkNotSubtype("{{any f2} f2}","{[any] f1,any f2}"); }
	@Test public void test_9269() { checkNotSubtype("{{any f2} f2}","{[any] f2,any f3}"); }
	@Test public void test_9270() { checkNotSubtype("{{any f2} f2}","{[null] f1}"); }
	@Test public void test_9271() { checkNotSubtype("{{any f2} f2}","{[null] f2}"); }
	@Test public void test_9272() { checkNotSubtype("{{any f2} f2}","{[null] f1,null f2}"); }
	@Test public void test_9273() { checkNotSubtype("{{any f2} f2}","{[null] f2,null f3}"); }
	@Test public void test_9274() { checkNotSubtype("{{any f2} f2}","{[int] f1}"); }
	@Test public void test_9275() { checkNotSubtype("{{any f2} f2}","{[int] f2}"); }
	@Test public void test_9276() { checkNotSubtype("{{any f2} f2}","{[int] f1,int f2}"); }
	@Test public void test_9277() { checkNotSubtype("{{any f2} f2}","{[int] f2,int f3}"); }
	@Test public void test_9278() { checkIsSubtype("{{any f2} f2}","{{void f1} f1}"); }
	@Test public void test_9279() { checkIsSubtype("{{any f2} f2}","{{void f2} f1}"); }
	@Test public void test_9280() { checkIsSubtype("{{any f2} f2}","{{void f1} f2}"); }
	@Test public void test_9281() { checkIsSubtype("{{any f2} f2}","{{void f2} f2}"); }
	@Test public void test_9282() { checkIsSubtype("{{any f2} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_9283() { checkIsSubtype("{{any f2} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_9284() { checkIsSubtype("{{any f2} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_9285() { checkIsSubtype("{{any f2} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_9286() { checkNotSubtype("{{any f2} f2}","{{any f1} f1}"); }
	@Test public void test_9287() { checkNotSubtype("{{any f2} f2}","{{any f2} f1}"); }
	@Test public void test_9288() { checkNotSubtype("{{any f2} f2}","{{any f1} f2}"); }
	@Test public void test_9289() { checkIsSubtype("{{any f2} f2}","{{any f2} f2}"); }
	@Test public void test_9290() { checkNotSubtype("{{any f2} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_9291() { checkNotSubtype("{{any f2} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_9292() { checkNotSubtype("{{any f2} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_9293() { checkNotSubtype("{{any f2} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_9294() { checkNotSubtype("{{any f2} f2}","{{null f1} f1}"); }
	@Test public void test_9295() { checkNotSubtype("{{any f2} f2}","{{null f2} f1}"); }
	@Test public void test_9296() { checkNotSubtype("{{any f2} f2}","{{null f1} f2}"); }
	@Test public void test_9297() { checkIsSubtype("{{any f2} f2}","{{null f2} f2}"); }
	@Test public void test_9298() { checkNotSubtype("{{any f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_9299() { checkNotSubtype("{{any f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_9300() { checkNotSubtype("{{any f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_9301() { checkNotSubtype("{{any f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_9302() { checkNotSubtype("{{any f2} f2}","{{int f1} f1}"); }
	@Test public void test_9303() { checkNotSubtype("{{any f2} f2}","{{int f2} f1}"); }
	@Test public void test_9304() { checkNotSubtype("{{any f2} f2}","{{int f1} f2}"); }
	@Test public void test_9305() { checkIsSubtype("{{any f2} f2}","{{int f2} f2}"); }
	@Test public void test_9306() { checkNotSubtype("{{any f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_9307() { checkNotSubtype("{{any f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_9308() { checkNotSubtype("{{any f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_9309() { checkNotSubtype("{{any f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_9310() { checkNotSubtype("{{any f1} f1,any f2}","any"); }
	@Test public void test_9311() { checkNotSubtype("{{any f1} f1,any f2}","null"); }
	@Test public void test_9312() { checkNotSubtype("{{any f1} f1,any f2}","int"); }
	@Test public void test_9313() { checkNotSubtype("{{any f1} f1,any f2}","[void]"); }
	@Test public void test_9314() { checkNotSubtype("{{any f1} f1,any f2}","[any]"); }
	@Test public void test_9315() { checkNotSubtype("{{any f1} f1,any f2}","[null]"); }
	@Test public void test_9316() { checkNotSubtype("{{any f1} f1,any f2}","[int]"); }
	@Test public void test_9317() { checkIsSubtype("{{any f1} f1,any f2}","{void f1}"); }
	@Test public void test_9318() { checkIsSubtype("{{any f1} f1,any f2}","{void f2}"); }
	@Test public void test_9319() { checkNotSubtype("{{any f1} f1,any f2}","{any f1}"); }
	@Test public void test_9320() { checkNotSubtype("{{any f1} f1,any f2}","{any f2}"); }
	@Test public void test_9321() { checkNotSubtype("{{any f1} f1,any f2}","{null f1}"); }
	@Test public void test_9322() { checkNotSubtype("{{any f1} f1,any f2}","{null f2}"); }
	@Test public void test_9323() { checkNotSubtype("{{any f1} f1,any f2}","{int f1}"); }
	@Test public void test_9324() { checkNotSubtype("{{any f1} f1,any f2}","{int f2}"); }
	@Test public void test_9325() { checkNotSubtype("{{any f1} f1,any f2}","[[void]]"); }
	@Test public void test_9326() { checkNotSubtype("{{any f1} f1,any f2}","[[any]]"); }
	@Test public void test_9327() { checkNotSubtype("{{any f1} f1,any f2}","[[null]]"); }
	@Test public void test_9328() { checkNotSubtype("{{any f1} f1,any f2}","[[int]]"); }
	@Test public void test_9329() { checkNotSubtype("{{any f1} f1,any f2}","[{void f1}]"); }
	@Test public void test_9330() { checkNotSubtype("{{any f1} f1,any f2}","[{void f2}]"); }
	@Test public void test_9331() { checkNotSubtype("{{any f1} f1,any f2}","[{any f1}]"); }
	@Test public void test_9332() { checkNotSubtype("{{any f1} f1,any f2}","[{any f2}]"); }
	@Test public void test_9333() { checkNotSubtype("{{any f1} f1,any f2}","[{null f1}]"); }
	@Test public void test_9334() { checkNotSubtype("{{any f1} f1,any f2}","[{null f2}]"); }
	@Test public void test_9335() { checkNotSubtype("{{any f1} f1,any f2}","[{int f1}]"); }
	@Test public void test_9336() { checkNotSubtype("{{any f1} f1,any f2}","[{int f2}]"); }
	@Test public void test_9337() { checkIsSubtype("{{any f1} f1,any f2}","{void f1,void f2}"); }
	@Test public void test_9338() { checkIsSubtype("{{any f1} f1,any f2}","{void f2,void f3}"); }
	@Test public void test_9339() { checkIsSubtype("{{any f1} f1,any f2}","{void f1,any f2}"); }
	@Test public void test_9340() { checkIsSubtype("{{any f1} f1,any f2}","{void f2,any f3}"); }
	@Test public void test_9341() { checkIsSubtype("{{any f1} f1,any f2}","{void f1,null f2}"); }
	@Test public void test_9342() { checkIsSubtype("{{any f1} f1,any f2}","{void f2,null f3}"); }
	@Test public void test_9343() { checkIsSubtype("{{any f1} f1,any f2}","{void f1,int f2}"); }
	@Test public void test_9344() { checkIsSubtype("{{any f1} f1,any f2}","{void f2,int f3}"); }
	@Test public void test_9345() { checkIsSubtype("{{any f1} f1,any f2}","{any f1,void f2}"); }
	@Test public void test_9346() { checkIsSubtype("{{any f1} f1,any f2}","{any f2,void f3}"); }
	@Test public void test_9347() { checkNotSubtype("{{any f1} f1,any f2}","{any f1,any f2}"); }
	@Test public void test_9348() { checkNotSubtype("{{any f1} f1,any f2}","{any f2,any f3}"); }
	@Test public void test_9349() { checkNotSubtype("{{any f1} f1,any f2}","{any f1,null f2}"); }
	@Test public void test_9350() { checkNotSubtype("{{any f1} f1,any f2}","{any f2,null f3}"); }
	@Test public void test_9351() { checkNotSubtype("{{any f1} f1,any f2}","{any f1,int f2}"); }
	@Test public void test_9352() { checkNotSubtype("{{any f1} f1,any f2}","{any f2,int f3}"); }
	@Test public void test_9353() { checkIsSubtype("{{any f1} f1,any f2}","{null f1,void f2}"); }
	@Test public void test_9354() { checkIsSubtype("{{any f1} f1,any f2}","{null f2,void f3}"); }
	@Test public void test_9355() { checkNotSubtype("{{any f1} f1,any f2}","{null f1,any f2}"); }
	@Test public void test_9356() { checkNotSubtype("{{any f1} f1,any f2}","{null f2,any f3}"); }
	@Test public void test_9357() { checkNotSubtype("{{any f1} f1,any f2}","{null f1,null f2}"); }
	@Test public void test_9358() { checkNotSubtype("{{any f1} f1,any f2}","{null f2,null f3}"); }
	@Test public void test_9359() { checkNotSubtype("{{any f1} f1,any f2}","{null f1,int f2}"); }
	@Test public void test_9360() { checkNotSubtype("{{any f1} f1,any f2}","{null f2,int f3}"); }
	@Test public void test_9361() { checkIsSubtype("{{any f1} f1,any f2}","{int f1,void f2}"); }
	@Test public void test_9362() { checkIsSubtype("{{any f1} f1,any f2}","{int f2,void f3}"); }
	@Test public void test_9363() { checkNotSubtype("{{any f1} f1,any f2}","{int f1,any f2}"); }
	@Test public void test_9364() { checkNotSubtype("{{any f1} f1,any f2}","{int f2,any f3}"); }
	@Test public void test_9365() { checkNotSubtype("{{any f1} f1,any f2}","{int f1,null f2}"); }
	@Test public void test_9366() { checkNotSubtype("{{any f1} f1,any f2}","{int f2,null f3}"); }
	@Test public void test_9367() { checkNotSubtype("{{any f1} f1,any f2}","{int f1,int f2}"); }
	@Test public void test_9368() { checkNotSubtype("{{any f1} f1,any f2}","{int f2,int f3}"); }
	@Test public void test_9369() { checkNotSubtype("{{any f1} f1,any f2}","{[void] f1}"); }
	@Test public void test_9370() { checkNotSubtype("{{any f1} f1,any f2}","{[void] f2}"); }
	@Test public void test_9371() { checkIsSubtype("{{any f1} f1,any f2}","{[void] f1,void f2}"); }
	@Test public void test_9372() { checkIsSubtype("{{any f1} f1,any f2}","{[void] f2,void f3}"); }
	@Test public void test_9373() { checkNotSubtype("{{any f1} f1,any f2}","{[any] f1}"); }
	@Test public void test_9374() { checkNotSubtype("{{any f1} f1,any f2}","{[any] f2}"); }
	@Test public void test_9375() { checkNotSubtype("{{any f1} f1,any f2}","{[any] f1,any f2}"); }
	@Test public void test_9376() { checkNotSubtype("{{any f1} f1,any f2}","{[any] f2,any f3}"); }
	@Test public void test_9377() { checkNotSubtype("{{any f1} f1,any f2}","{[null] f1}"); }
	@Test public void test_9378() { checkNotSubtype("{{any f1} f1,any f2}","{[null] f2}"); }
	@Test public void test_9379() { checkNotSubtype("{{any f1} f1,any f2}","{[null] f1,null f2}"); }
	@Test public void test_9380() { checkNotSubtype("{{any f1} f1,any f2}","{[null] f2,null f3}"); }
	@Test public void test_9381() { checkNotSubtype("{{any f1} f1,any f2}","{[int] f1}"); }
	@Test public void test_9382() { checkNotSubtype("{{any f1} f1,any f2}","{[int] f2}"); }
	@Test public void test_9383() { checkNotSubtype("{{any f1} f1,any f2}","{[int] f1,int f2}"); }
	@Test public void test_9384() { checkNotSubtype("{{any f1} f1,any f2}","{[int] f2,int f3}"); }
	@Test public void test_9385() { checkIsSubtype("{{any f1} f1,any f2}","{{void f1} f1}"); }
	@Test public void test_9386() { checkIsSubtype("{{any f1} f1,any f2}","{{void f2} f1}"); }
	@Test public void test_9387() { checkIsSubtype("{{any f1} f1,any f2}","{{void f1} f2}"); }
	@Test public void test_9388() { checkIsSubtype("{{any f1} f1,any f2}","{{void f2} f2}"); }
	@Test public void test_9389() { checkIsSubtype("{{any f1} f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_9390() { checkIsSubtype("{{any f1} f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_9391() { checkIsSubtype("{{any f1} f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_9392() { checkIsSubtype("{{any f1} f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_9393() { checkNotSubtype("{{any f1} f1,any f2}","{{any f1} f1}"); }
	@Test public void test_9394() { checkNotSubtype("{{any f1} f1,any f2}","{{any f2} f1}"); }
	@Test public void test_9395() { checkNotSubtype("{{any f1} f1,any f2}","{{any f1} f2}"); }
	@Test public void test_9396() { checkNotSubtype("{{any f1} f1,any f2}","{{any f2} f2}"); }
	@Test public void test_9397() { checkIsSubtype("{{any f1} f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_9398() { checkNotSubtype("{{any f1} f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_9399() { checkNotSubtype("{{any f1} f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_9400() { checkNotSubtype("{{any f1} f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_9401() { checkNotSubtype("{{any f1} f1,any f2}","{{null f1} f1}"); }
	@Test public void test_9402() { checkNotSubtype("{{any f1} f1,any f2}","{{null f2} f1}"); }
	@Test public void test_9403() { checkNotSubtype("{{any f1} f1,any f2}","{{null f1} f2}"); }
	@Test public void test_9404() { checkNotSubtype("{{any f1} f1,any f2}","{{null f2} f2}"); }
	@Test public void test_9405() { checkIsSubtype("{{any f1} f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_9406() { checkNotSubtype("{{any f1} f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_9407() { checkNotSubtype("{{any f1} f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_9408() { checkNotSubtype("{{any f1} f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_9409() { checkNotSubtype("{{any f1} f1,any f2}","{{int f1} f1}"); }
	@Test public void test_9410() { checkNotSubtype("{{any f1} f1,any f2}","{{int f2} f1}"); }
	@Test public void test_9411() { checkNotSubtype("{{any f1} f1,any f2}","{{int f1} f2}"); }
	@Test public void test_9412() { checkNotSubtype("{{any f1} f1,any f2}","{{int f2} f2}"); }
	@Test public void test_9413() { checkIsSubtype("{{any f1} f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_9414() { checkNotSubtype("{{any f1} f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_9415() { checkNotSubtype("{{any f1} f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_9416() { checkNotSubtype("{{any f1} f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_9417() { checkNotSubtype("{{any f2} f1,any f2}","any"); }
	@Test public void test_9418() { checkNotSubtype("{{any f2} f1,any f2}","null"); }
	@Test public void test_9419() { checkNotSubtype("{{any f2} f1,any f2}","int"); }
	@Test public void test_9420() { checkNotSubtype("{{any f2} f1,any f2}","[void]"); }
	@Test public void test_9421() { checkNotSubtype("{{any f2} f1,any f2}","[any]"); }
	@Test public void test_9422() { checkNotSubtype("{{any f2} f1,any f2}","[null]"); }
	@Test public void test_9423() { checkNotSubtype("{{any f2} f1,any f2}","[int]"); }
	@Test public void test_9424() { checkIsSubtype("{{any f2} f1,any f2}","{void f1}"); }
	@Test public void test_9425() { checkIsSubtype("{{any f2} f1,any f2}","{void f2}"); }
	@Test public void test_9426() { checkNotSubtype("{{any f2} f1,any f2}","{any f1}"); }
	@Test public void test_9427() { checkNotSubtype("{{any f2} f1,any f2}","{any f2}"); }
	@Test public void test_9428() { checkNotSubtype("{{any f2} f1,any f2}","{null f1}"); }
	@Test public void test_9429() { checkNotSubtype("{{any f2} f1,any f2}","{null f2}"); }
	@Test public void test_9430() { checkNotSubtype("{{any f2} f1,any f2}","{int f1}"); }
	@Test public void test_9431() { checkNotSubtype("{{any f2} f1,any f2}","{int f2}"); }
	@Test public void test_9432() { checkNotSubtype("{{any f2} f1,any f2}","[[void]]"); }
	@Test public void test_9433() { checkNotSubtype("{{any f2} f1,any f2}","[[any]]"); }
	@Test public void test_9434() { checkNotSubtype("{{any f2} f1,any f2}","[[null]]"); }
	@Test public void test_9435() { checkNotSubtype("{{any f2} f1,any f2}","[[int]]"); }
	@Test public void test_9436() { checkNotSubtype("{{any f2} f1,any f2}","[{void f1}]"); }
	@Test public void test_9437() { checkNotSubtype("{{any f2} f1,any f2}","[{void f2}]"); }
	@Test public void test_9438() { checkNotSubtype("{{any f2} f1,any f2}","[{any f1}]"); }
	@Test public void test_9439() { checkNotSubtype("{{any f2} f1,any f2}","[{any f2}]"); }
	@Test public void test_9440() { checkNotSubtype("{{any f2} f1,any f2}","[{null f1}]"); }
	@Test public void test_9441() { checkNotSubtype("{{any f2} f1,any f2}","[{null f2}]"); }
	@Test public void test_9442() { checkNotSubtype("{{any f2} f1,any f2}","[{int f1}]"); }
	@Test public void test_9443() { checkNotSubtype("{{any f2} f1,any f2}","[{int f2}]"); }
	@Test public void test_9444() { checkIsSubtype("{{any f2} f1,any f2}","{void f1,void f2}"); }
	@Test public void test_9445() { checkIsSubtype("{{any f2} f1,any f2}","{void f2,void f3}"); }
	@Test public void test_9446() { checkIsSubtype("{{any f2} f1,any f2}","{void f1,any f2}"); }
	@Test public void test_9447() { checkIsSubtype("{{any f2} f1,any f2}","{void f2,any f3}"); }
	@Test public void test_9448() { checkIsSubtype("{{any f2} f1,any f2}","{void f1,null f2}"); }
	@Test public void test_9449() { checkIsSubtype("{{any f2} f1,any f2}","{void f2,null f3}"); }
	@Test public void test_9450() { checkIsSubtype("{{any f2} f1,any f2}","{void f1,int f2}"); }
	@Test public void test_9451() { checkIsSubtype("{{any f2} f1,any f2}","{void f2,int f3}"); }
	@Test public void test_9452() { checkIsSubtype("{{any f2} f1,any f2}","{any f1,void f2}"); }
	@Test public void test_9453() { checkIsSubtype("{{any f2} f1,any f2}","{any f2,void f3}"); }
	@Test public void test_9454() { checkNotSubtype("{{any f2} f1,any f2}","{any f1,any f2}"); }
	@Test public void test_9455() { checkNotSubtype("{{any f2} f1,any f2}","{any f2,any f3}"); }
	@Test public void test_9456() { checkNotSubtype("{{any f2} f1,any f2}","{any f1,null f2}"); }
	@Test public void test_9457() { checkNotSubtype("{{any f2} f1,any f2}","{any f2,null f3}"); }
	@Test public void test_9458() { checkNotSubtype("{{any f2} f1,any f2}","{any f1,int f2}"); }
	@Test public void test_9459() { checkNotSubtype("{{any f2} f1,any f2}","{any f2,int f3}"); }
	@Test public void test_9460() { checkIsSubtype("{{any f2} f1,any f2}","{null f1,void f2}"); }
	@Test public void test_9461() { checkIsSubtype("{{any f2} f1,any f2}","{null f2,void f3}"); }
	@Test public void test_9462() { checkNotSubtype("{{any f2} f1,any f2}","{null f1,any f2}"); }
	@Test public void test_9463() { checkNotSubtype("{{any f2} f1,any f2}","{null f2,any f3}"); }
	@Test public void test_9464() { checkNotSubtype("{{any f2} f1,any f2}","{null f1,null f2}"); }
	@Test public void test_9465() { checkNotSubtype("{{any f2} f1,any f2}","{null f2,null f3}"); }
	@Test public void test_9466() { checkNotSubtype("{{any f2} f1,any f2}","{null f1,int f2}"); }
	@Test public void test_9467() { checkNotSubtype("{{any f2} f1,any f2}","{null f2,int f3}"); }
	@Test public void test_9468() { checkIsSubtype("{{any f2} f1,any f2}","{int f1,void f2}"); }
	@Test public void test_9469() { checkIsSubtype("{{any f2} f1,any f2}","{int f2,void f3}"); }
	@Test public void test_9470() { checkNotSubtype("{{any f2} f1,any f2}","{int f1,any f2}"); }
	@Test public void test_9471() { checkNotSubtype("{{any f2} f1,any f2}","{int f2,any f3}"); }
	@Test public void test_9472() { checkNotSubtype("{{any f2} f1,any f2}","{int f1,null f2}"); }
	@Test public void test_9473() { checkNotSubtype("{{any f2} f1,any f2}","{int f2,null f3}"); }
	@Test public void test_9474() { checkNotSubtype("{{any f2} f1,any f2}","{int f1,int f2}"); }
	@Test public void test_9475() { checkNotSubtype("{{any f2} f1,any f2}","{int f2,int f3}"); }
	@Test public void test_9476() { checkNotSubtype("{{any f2} f1,any f2}","{[void] f1}"); }
	@Test public void test_9477() { checkNotSubtype("{{any f2} f1,any f2}","{[void] f2}"); }
	@Test public void test_9478() { checkIsSubtype("{{any f2} f1,any f2}","{[void] f1,void f2}"); }
	@Test public void test_9479() { checkIsSubtype("{{any f2} f1,any f2}","{[void] f2,void f3}"); }
	@Test public void test_9480() { checkNotSubtype("{{any f2} f1,any f2}","{[any] f1}"); }
	@Test public void test_9481() { checkNotSubtype("{{any f2} f1,any f2}","{[any] f2}"); }
	@Test public void test_9482() { checkNotSubtype("{{any f2} f1,any f2}","{[any] f1,any f2}"); }
	@Test public void test_9483() { checkNotSubtype("{{any f2} f1,any f2}","{[any] f2,any f3}"); }
	@Test public void test_9484() { checkNotSubtype("{{any f2} f1,any f2}","{[null] f1}"); }
	@Test public void test_9485() { checkNotSubtype("{{any f2} f1,any f2}","{[null] f2}"); }
	@Test public void test_9486() { checkNotSubtype("{{any f2} f1,any f2}","{[null] f1,null f2}"); }
	@Test public void test_9487() { checkNotSubtype("{{any f2} f1,any f2}","{[null] f2,null f3}"); }
	@Test public void test_9488() { checkNotSubtype("{{any f2} f1,any f2}","{[int] f1}"); }
	@Test public void test_9489() { checkNotSubtype("{{any f2} f1,any f2}","{[int] f2}"); }
	@Test public void test_9490() { checkNotSubtype("{{any f2} f1,any f2}","{[int] f1,int f2}"); }
	@Test public void test_9491() { checkNotSubtype("{{any f2} f1,any f2}","{[int] f2,int f3}"); }
	@Test public void test_9492() { checkIsSubtype("{{any f2} f1,any f2}","{{void f1} f1}"); }
	@Test public void test_9493() { checkIsSubtype("{{any f2} f1,any f2}","{{void f2} f1}"); }
	@Test public void test_9494() { checkIsSubtype("{{any f2} f1,any f2}","{{void f1} f2}"); }
	@Test public void test_9495() { checkIsSubtype("{{any f2} f1,any f2}","{{void f2} f2}"); }
	@Test public void test_9496() { checkIsSubtype("{{any f2} f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_9497() { checkIsSubtype("{{any f2} f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_9498() { checkIsSubtype("{{any f2} f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_9499() { checkIsSubtype("{{any f2} f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_9500() { checkNotSubtype("{{any f2} f1,any f2}","{{any f1} f1}"); }
	@Test public void test_9501() { checkNotSubtype("{{any f2} f1,any f2}","{{any f2} f1}"); }
	@Test public void test_9502() { checkNotSubtype("{{any f2} f1,any f2}","{{any f1} f2}"); }
	@Test public void test_9503() { checkNotSubtype("{{any f2} f1,any f2}","{{any f2} f2}"); }
	@Test public void test_9504() { checkNotSubtype("{{any f2} f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_9505() { checkIsSubtype("{{any f2} f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_9506() { checkNotSubtype("{{any f2} f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_9507() { checkNotSubtype("{{any f2} f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_9508() { checkNotSubtype("{{any f2} f1,any f2}","{{null f1} f1}"); }
	@Test public void test_9509() { checkNotSubtype("{{any f2} f1,any f2}","{{null f2} f1}"); }
	@Test public void test_9510() { checkNotSubtype("{{any f2} f1,any f2}","{{null f1} f2}"); }
	@Test public void test_9511() { checkNotSubtype("{{any f2} f1,any f2}","{{null f2} f2}"); }
	@Test public void test_9512() { checkNotSubtype("{{any f2} f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_9513() { checkIsSubtype("{{any f2} f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_9514() { checkNotSubtype("{{any f2} f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_9515() { checkNotSubtype("{{any f2} f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_9516() { checkNotSubtype("{{any f2} f1,any f2}","{{int f1} f1}"); }
	@Test public void test_9517() { checkNotSubtype("{{any f2} f1,any f2}","{{int f2} f1}"); }
	@Test public void test_9518() { checkNotSubtype("{{any f2} f1,any f2}","{{int f1} f2}"); }
	@Test public void test_9519() { checkNotSubtype("{{any f2} f1,any f2}","{{int f2} f2}"); }
	@Test public void test_9520() { checkNotSubtype("{{any f2} f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_9521() { checkIsSubtype("{{any f2} f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_9522() { checkNotSubtype("{{any f2} f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_9523() { checkNotSubtype("{{any f2} f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_9524() { checkNotSubtype("{{any f1} f2,any f3}","any"); }
	@Test public void test_9525() { checkNotSubtype("{{any f1} f2,any f3}","null"); }
	@Test public void test_9526() { checkNotSubtype("{{any f1} f2,any f3}","int"); }
	@Test public void test_9527() { checkNotSubtype("{{any f1} f2,any f3}","[void]"); }
	@Test public void test_9528() { checkNotSubtype("{{any f1} f2,any f3}","[any]"); }
	@Test public void test_9529() { checkNotSubtype("{{any f1} f2,any f3}","[null]"); }
	@Test public void test_9530() { checkNotSubtype("{{any f1} f2,any f3}","[int]"); }
	@Test public void test_9531() { checkIsSubtype("{{any f1} f2,any f3}","{void f1}"); }
	@Test public void test_9532() { checkIsSubtype("{{any f1} f2,any f3}","{void f2}"); }
	@Test public void test_9533() { checkNotSubtype("{{any f1} f2,any f3}","{any f1}"); }
	@Test public void test_9534() { checkNotSubtype("{{any f1} f2,any f3}","{any f2}"); }
	@Test public void test_9535() { checkNotSubtype("{{any f1} f2,any f3}","{null f1}"); }
	@Test public void test_9536() { checkNotSubtype("{{any f1} f2,any f3}","{null f2}"); }
	@Test public void test_9537() { checkNotSubtype("{{any f1} f2,any f3}","{int f1}"); }
	@Test public void test_9538() { checkNotSubtype("{{any f1} f2,any f3}","{int f2}"); }
	@Test public void test_9539() { checkNotSubtype("{{any f1} f2,any f3}","[[void]]"); }
	@Test public void test_9540() { checkNotSubtype("{{any f1} f2,any f3}","[[any]]"); }
	@Test public void test_9541() { checkNotSubtype("{{any f1} f2,any f3}","[[null]]"); }
	@Test public void test_9542() { checkNotSubtype("{{any f1} f2,any f3}","[[int]]"); }
	@Test public void test_9543() { checkNotSubtype("{{any f1} f2,any f3}","[{void f1}]"); }
	@Test public void test_9544() { checkNotSubtype("{{any f1} f2,any f3}","[{void f2}]"); }
	@Test public void test_9545() { checkNotSubtype("{{any f1} f2,any f3}","[{any f1}]"); }
	@Test public void test_9546() { checkNotSubtype("{{any f1} f2,any f3}","[{any f2}]"); }
	@Test public void test_9547() { checkNotSubtype("{{any f1} f2,any f3}","[{null f1}]"); }
	@Test public void test_9548() { checkNotSubtype("{{any f1} f2,any f3}","[{null f2}]"); }
	@Test public void test_9549() { checkNotSubtype("{{any f1} f2,any f3}","[{int f1}]"); }
	@Test public void test_9550() { checkNotSubtype("{{any f1} f2,any f3}","[{int f2}]"); }
	@Test public void test_9551() { checkIsSubtype("{{any f1} f2,any f3}","{void f1,void f2}"); }
	@Test public void test_9552() { checkIsSubtype("{{any f1} f2,any f3}","{void f2,void f3}"); }
	@Test public void test_9553() { checkIsSubtype("{{any f1} f2,any f3}","{void f1,any f2}"); }
	@Test public void test_9554() { checkIsSubtype("{{any f1} f2,any f3}","{void f2,any f3}"); }
	@Test public void test_9555() { checkIsSubtype("{{any f1} f2,any f3}","{void f1,null f2}"); }
	@Test public void test_9556() { checkIsSubtype("{{any f1} f2,any f3}","{void f2,null f3}"); }
	@Test public void test_9557() { checkIsSubtype("{{any f1} f2,any f3}","{void f1,int f2}"); }
	@Test public void test_9558() { checkIsSubtype("{{any f1} f2,any f3}","{void f2,int f3}"); }
	@Test public void test_9559() { checkIsSubtype("{{any f1} f2,any f3}","{any f1,void f2}"); }
	@Test public void test_9560() { checkIsSubtype("{{any f1} f2,any f3}","{any f2,void f3}"); }
	@Test public void test_9561() { checkNotSubtype("{{any f1} f2,any f3}","{any f1,any f2}"); }
	@Test public void test_9562() { checkNotSubtype("{{any f1} f2,any f3}","{any f2,any f3}"); }
	@Test public void test_9563() { checkNotSubtype("{{any f1} f2,any f3}","{any f1,null f2}"); }
	@Test public void test_9564() { checkNotSubtype("{{any f1} f2,any f3}","{any f2,null f3}"); }
	@Test public void test_9565() { checkNotSubtype("{{any f1} f2,any f3}","{any f1,int f2}"); }
	@Test public void test_9566() { checkNotSubtype("{{any f1} f2,any f3}","{any f2,int f3}"); }
	@Test public void test_9567() { checkIsSubtype("{{any f1} f2,any f3}","{null f1,void f2}"); }
	@Test public void test_9568() { checkIsSubtype("{{any f1} f2,any f3}","{null f2,void f3}"); }
	@Test public void test_9569() { checkNotSubtype("{{any f1} f2,any f3}","{null f1,any f2}"); }
	@Test public void test_9570() { checkNotSubtype("{{any f1} f2,any f3}","{null f2,any f3}"); }
	@Test public void test_9571() { checkNotSubtype("{{any f1} f2,any f3}","{null f1,null f2}"); }
	@Test public void test_9572() { checkNotSubtype("{{any f1} f2,any f3}","{null f2,null f3}"); }
	@Test public void test_9573() { checkNotSubtype("{{any f1} f2,any f3}","{null f1,int f2}"); }
	@Test public void test_9574() { checkNotSubtype("{{any f1} f2,any f3}","{null f2,int f3}"); }
	@Test public void test_9575() { checkIsSubtype("{{any f1} f2,any f3}","{int f1,void f2}"); }
	@Test public void test_9576() { checkIsSubtype("{{any f1} f2,any f3}","{int f2,void f3}"); }
	@Test public void test_9577() { checkNotSubtype("{{any f1} f2,any f3}","{int f1,any f2}"); }
	@Test public void test_9578() { checkNotSubtype("{{any f1} f2,any f3}","{int f2,any f3}"); }
	@Test public void test_9579() { checkNotSubtype("{{any f1} f2,any f3}","{int f1,null f2}"); }
	@Test public void test_9580() { checkNotSubtype("{{any f1} f2,any f3}","{int f2,null f3}"); }
	@Test public void test_9581() { checkNotSubtype("{{any f1} f2,any f3}","{int f1,int f2}"); }
	@Test public void test_9582() { checkNotSubtype("{{any f1} f2,any f3}","{int f2,int f3}"); }
	@Test public void test_9583() { checkNotSubtype("{{any f1} f2,any f3}","{[void] f1}"); }
	@Test public void test_9584() { checkNotSubtype("{{any f1} f2,any f3}","{[void] f2}"); }
	@Test public void test_9585() { checkIsSubtype("{{any f1} f2,any f3}","{[void] f1,void f2}"); }
	@Test public void test_9586() { checkIsSubtype("{{any f1} f2,any f3}","{[void] f2,void f3}"); }
	@Test public void test_9587() { checkNotSubtype("{{any f1} f2,any f3}","{[any] f1}"); }
	@Test public void test_9588() { checkNotSubtype("{{any f1} f2,any f3}","{[any] f2}"); }
	@Test public void test_9589() { checkNotSubtype("{{any f1} f2,any f3}","{[any] f1,any f2}"); }
	@Test public void test_9590() { checkNotSubtype("{{any f1} f2,any f3}","{[any] f2,any f3}"); }
	@Test public void test_9591() { checkNotSubtype("{{any f1} f2,any f3}","{[null] f1}"); }
	@Test public void test_9592() { checkNotSubtype("{{any f1} f2,any f3}","{[null] f2}"); }
	@Test public void test_9593() { checkNotSubtype("{{any f1} f2,any f3}","{[null] f1,null f2}"); }
	@Test public void test_9594() { checkNotSubtype("{{any f1} f2,any f3}","{[null] f2,null f3}"); }
	@Test public void test_9595() { checkNotSubtype("{{any f1} f2,any f3}","{[int] f1}"); }
	@Test public void test_9596() { checkNotSubtype("{{any f1} f2,any f3}","{[int] f2}"); }
	@Test public void test_9597() { checkNotSubtype("{{any f1} f2,any f3}","{[int] f1,int f2}"); }
	@Test public void test_9598() { checkNotSubtype("{{any f1} f2,any f3}","{[int] f2,int f3}"); }
	@Test public void test_9599() { checkIsSubtype("{{any f1} f2,any f3}","{{void f1} f1}"); }
	@Test public void test_9600() { checkIsSubtype("{{any f1} f2,any f3}","{{void f2} f1}"); }
	@Test public void test_9601() { checkIsSubtype("{{any f1} f2,any f3}","{{void f1} f2}"); }
	@Test public void test_9602() { checkIsSubtype("{{any f1} f2,any f3}","{{void f2} f2}"); }
	@Test public void test_9603() { checkIsSubtype("{{any f1} f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_9604() { checkIsSubtype("{{any f1} f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_9605() { checkIsSubtype("{{any f1} f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_9606() { checkIsSubtype("{{any f1} f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_9607() { checkNotSubtype("{{any f1} f2,any f3}","{{any f1} f1}"); }
	@Test public void test_9608() { checkNotSubtype("{{any f1} f2,any f3}","{{any f2} f1}"); }
	@Test public void test_9609() { checkNotSubtype("{{any f1} f2,any f3}","{{any f1} f2}"); }
	@Test public void test_9610() { checkNotSubtype("{{any f1} f2,any f3}","{{any f2} f2}"); }
	@Test public void test_9611() { checkNotSubtype("{{any f1} f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_9612() { checkNotSubtype("{{any f1} f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_9613() { checkIsSubtype("{{any f1} f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_9614() { checkNotSubtype("{{any f1} f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_9615() { checkNotSubtype("{{any f1} f2,any f3}","{{null f1} f1}"); }
	@Test public void test_9616() { checkNotSubtype("{{any f1} f2,any f3}","{{null f2} f1}"); }
	@Test public void test_9617() { checkNotSubtype("{{any f1} f2,any f3}","{{null f1} f2}"); }
	@Test public void test_9618() { checkNotSubtype("{{any f1} f2,any f3}","{{null f2} f2}"); }
	@Test public void test_9619() { checkNotSubtype("{{any f1} f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_9620() { checkNotSubtype("{{any f1} f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_9621() { checkIsSubtype("{{any f1} f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_9622() { checkNotSubtype("{{any f1} f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_9623() { checkNotSubtype("{{any f1} f2,any f3}","{{int f1} f1}"); }
	@Test public void test_9624() { checkNotSubtype("{{any f1} f2,any f3}","{{int f2} f1}"); }
	@Test public void test_9625() { checkNotSubtype("{{any f1} f2,any f3}","{{int f1} f2}"); }
	@Test public void test_9626() { checkNotSubtype("{{any f1} f2,any f3}","{{int f2} f2}"); }
	@Test public void test_9627() { checkNotSubtype("{{any f1} f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_9628() { checkNotSubtype("{{any f1} f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_9629() { checkIsSubtype("{{any f1} f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_9630() { checkNotSubtype("{{any f1} f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_9631() { checkNotSubtype("{{any f2} f2,any f3}","any"); }
	@Test public void test_9632() { checkNotSubtype("{{any f2} f2,any f3}","null"); }
	@Test public void test_9633() { checkNotSubtype("{{any f2} f2,any f3}","int"); }
	@Test public void test_9634() { checkNotSubtype("{{any f2} f2,any f3}","[void]"); }
	@Test public void test_9635() { checkNotSubtype("{{any f2} f2,any f3}","[any]"); }
	@Test public void test_9636() { checkNotSubtype("{{any f2} f2,any f3}","[null]"); }
	@Test public void test_9637() { checkNotSubtype("{{any f2} f2,any f3}","[int]"); }
	@Test public void test_9638() { checkIsSubtype("{{any f2} f2,any f3}","{void f1}"); }
	@Test public void test_9639() { checkIsSubtype("{{any f2} f2,any f3}","{void f2}"); }
	@Test public void test_9640() { checkNotSubtype("{{any f2} f2,any f3}","{any f1}"); }
	@Test public void test_9641() { checkNotSubtype("{{any f2} f2,any f3}","{any f2}"); }
	@Test public void test_9642() { checkNotSubtype("{{any f2} f2,any f3}","{null f1}"); }
	@Test public void test_9643() { checkNotSubtype("{{any f2} f2,any f3}","{null f2}"); }
	@Test public void test_9644() { checkNotSubtype("{{any f2} f2,any f3}","{int f1}"); }
	@Test public void test_9645() { checkNotSubtype("{{any f2} f2,any f3}","{int f2}"); }
	@Test public void test_9646() { checkNotSubtype("{{any f2} f2,any f3}","[[void]]"); }
	@Test public void test_9647() { checkNotSubtype("{{any f2} f2,any f3}","[[any]]"); }
	@Test public void test_9648() { checkNotSubtype("{{any f2} f2,any f3}","[[null]]"); }
	@Test public void test_9649() { checkNotSubtype("{{any f2} f2,any f3}","[[int]]"); }
	@Test public void test_9650() { checkNotSubtype("{{any f2} f2,any f3}","[{void f1}]"); }
	@Test public void test_9651() { checkNotSubtype("{{any f2} f2,any f3}","[{void f2}]"); }
	@Test public void test_9652() { checkNotSubtype("{{any f2} f2,any f3}","[{any f1}]"); }
	@Test public void test_9653() { checkNotSubtype("{{any f2} f2,any f3}","[{any f2}]"); }
	@Test public void test_9654() { checkNotSubtype("{{any f2} f2,any f3}","[{null f1}]"); }
	@Test public void test_9655() { checkNotSubtype("{{any f2} f2,any f3}","[{null f2}]"); }
	@Test public void test_9656() { checkNotSubtype("{{any f2} f2,any f3}","[{int f1}]"); }
	@Test public void test_9657() { checkNotSubtype("{{any f2} f2,any f3}","[{int f2}]"); }
	@Test public void test_9658() { checkIsSubtype("{{any f2} f2,any f3}","{void f1,void f2}"); }
	@Test public void test_9659() { checkIsSubtype("{{any f2} f2,any f3}","{void f2,void f3}"); }
	@Test public void test_9660() { checkIsSubtype("{{any f2} f2,any f3}","{void f1,any f2}"); }
	@Test public void test_9661() { checkIsSubtype("{{any f2} f2,any f3}","{void f2,any f3}"); }
	@Test public void test_9662() { checkIsSubtype("{{any f2} f2,any f3}","{void f1,null f2}"); }
	@Test public void test_9663() { checkIsSubtype("{{any f2} f2,any f3}","{void f2,null f3}"); }
	@Test public void test_9664() { checkIsSubtype("{{any f2} f2,any f3}","{void f1,int f2}"); }
	@Test public void test_9665() { checkIsSubtype("{{any f2} f2,any f3}","{void f2,int f3}"); }
	@Test public void test_9666() { checkIsSubtype("{{any f2} f2,any f3}","{any f1,void f2}"); }
	@Test public void test_9667() { checkIsSubtype("{{any f2} f2,any f3}","{any f2,void f3}"); }
	@Test public void test_9668() { checkNotSubtype("{{any f2} f2,any f3}","{any f1,any f2}"); }
	@Test public void test_9669() { checkNotSubtype("{{any f2} f2,any f3}","{any f2,any f3}"); }
	@Test public void test_9670() { checkNotSubtype("{{any f2} f2,any f3}","{any f1,null f2}"); }
	@Test public void test_9671() { checkNotSubtype("{{any f2} f2,any f3}","{any f2,null f3}"); }
	@Test public void test_9672() { checkNotSubtype("{{any f2} f2,any f3}","{any f1,int f2}"); }
	@Test public void test_9673() { checkNotSubtype("{{any f2} f2,any f3}","{any f2,int f3}"); }
	@Test public void test_9674() { checkIsSubtype("{{any f2} f2,any f3}","{null f1,void f2}"); }
	@Test public void test_9675() { checkIsSubtype("{{any f2} f2,any f3}","{null f2,void f3}"); }
	@Test public void test_9676() { checkNotSubtype("{{any f2} f2,any f3}","{null f1,any f2}"); }
	@Test public void test_9677() { checkNotSubtype("{{any f2} f2,any f3}","{null f2,any f3}"); }
	@Test public void test_9678() { checkNotSubtype("{{any f2} f2,any f3}","{null f1,null f2}"); }
	@Test public void test_9679() { checkNotSubtype("{{any f2} f2,any f3}","{null f2,null f3}"); }
	@Test public void test_9680() { checkNotSubtype("{{any f2} f2,any f3}","{null f1,int f2}"); }
	@Test public void test_9681() { checkNotSubtype("{{any f2} f2,any f3}","{null f2,int f3}"); }
	@Test public void test_9682() { checkIsSubtype("{{any f2} f2,any f3}","{int f1,void f2}"); }
	@Test public void test_9683() { checkIsSubtype("{{any f2} f2,any f3}","{int f2,void f3}"); }
	@Test public void test_9684() { checkNotSubtype("{{any f2} f2,any f3}","{int f1,any f2}"); }
	@Test public void test_9685() { checkNotSubtype("{{any f2} f2,any f3}","{int f2,any f3}"); }
	@Test public void test_9686() { checkNotSubtype("{{any f2} f2,any f3}","{int f1,null f2}"); }
	@Test public void test_9687() { checkNotSubtype("{{any f2} f2,any f3}","{int f2,null f3}"); }
	@Test public void test_9688() { checkNotSubtype("{{any f2} f2,any f3}","{int f1,int f2}"); }
	@Test public void test_9689() { checkNotSubtype("{{any f2} f2,any f3}","{int f2,int f3}"); }
	@Test public void test_9690() { checkNotSubtype("{{any f2} f2,any f3}","{[void] f1}"); }
	@Test public void test_9691() { checkNotSubtype("{{any f2} f2,any f3}","{[void] f2}"); }
	@Test public void test_9692() { checkIsSubtype("{{any f2} f2,any f3}","{[void] f1,void f2}"); }
	@Test public void test_9693() { checkIsSubtype("{{any f2} f2,any f3}","{[void] f2,void f3}"); }
	@Test public void test_9694() { checkNotSubtype("{{any f2} f2,any f3}","{[any] f1}"); }
	@Test public void test_9695() { checkNotSubtype("{{any f2} f2,any f3}","{[any] f2}"); }
	@Test public void test_9696() { checkNotSubtype("{{any f2} f2,any f3}","{[any] f1,any f2}"); }
	@Test public void test_9697() { checkNotSubtype("{{any f2} f2,any f3}","{[any] f2,any f3}"); }
	@Test public void test_9698() { checkNotSubtype("{{any f2} f2,any f3}","{[null] f1}"); }
	@Test public void test_9699() { checkNotSubtype("{{any f2} f2,any f3}","{[null] f2}"); }
	@Test public void test_9700() { checkNotSubtype("{{any f2} f2,any f3}","{[null] f1,null f2}"); }
	@Test public void test_9701() { checkNotSubtype("{{any f2} f2,any f3}","{[null] f2,null f3}"); }
	@Test public void test_9702() { checkNotSubtype("{{any f2} f2,any f3}","{[int] f1}"); }
	@Test public void test_9703() { checkNotSubtype("{{any f2} f2,any f3}","{[int] f2}"); }
	@Test public void test_9704() { checkNotSubtype("{{any f2} f2,any f3}","{[int] f1,int f2}"); }
	@Test public void test_9705() { checkNotSubtype("{{any f2} f2,any f3}","{[int] f2,int f3}"); }
	@Test public void test_9706() { checkIsSubtype("{{any f2} f2,any f3}","{{void f1} f1}"); }
	@Test public void test_9707() { checkIsSubtype("{{any f2} f2,any f3}","{{void f2} f1}"); }
	@Test public void test_9708() { checkIsSubtype("{{any f2} f2,any f3}","{{void f1} f2}"); }
	@Test public void test_9709() { checkIsSubtype("{{any f2} f2,any f3}","{{void f2} f2}"); }
	@Test public void test_9710() { checkIsSubtype("{{any f2} f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_9711() { checkIsSubtype("{{any f2} f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_9712() { checkIsSubtype("{{any f2} f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_9713() { checkIsSubtype("{{any f2} f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_9714() { checkNotSubtype("{{any f2} f2,any f3}","{{any f1} f1}"); }
	@Test public void test_9715() { checkNotSubtype("{{any f2} f2,any f3}","{{any f2} f1}"); }
	@Test public void test_9716() { checkNotSubtype("{{any f2} f2,any f3}","{{any f1} f2}"); }
	@Test public void test_9717() { checkNotSubtype("{{any f2} f2,any f3}","{{any f2} f2}"); }
	@Test public void test_9718() { checkNotSubtype("{{any f2} f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_9719() { checkNotSubtype("{{any f2} f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_9720() { checkNotSubtype("{{any f2} f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_9721() { checkIsSubtype("{{any f2} f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_9722() { checkNotSubtype("{{any f2} f2,any f3}","{{null f1} f1}"); }
	@Test public void test_9723() { checkNotSubtype("{{any f2} f2,any f3}","{{null f2} f1}"); }
	@Test public void test_9724() { checkNotSubtype("{{any f2} f2,any f3}","{{null f1} f2}"); }
	@Test public void test_9725() { checkNotSubtype("{{any f2} f2,any f3}","{{null f2} f2}"); }
	@Test public void test_9726() { checkNotSubtype("{{any f2} f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_9727() { checkNotSubtype("{{any f2} f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_9728() { checkNotSubtype("{{any f2} f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_9729() { checkIsSubtype("{{any f2} f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_9730() { checkNotSubtype("{{any f2} f2,any f3}","{{int f1} f1}"); }
	@Test public void test_9731() { checkNotSubtype("{{any f2} f2,any f3}","{{int f2} f1}"); }
	@Test public void test_9732() { checkNotSubtype("{{any f2} f2,any f3}","{{int f1} f2}"); }
	@Test public void test_9733() { checkNotSubtype("{{any f2} f2,any f3}","{{int f2} f2}"); }
	@Test public void test_9734() { checkNotSubtype("{{any f2} f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_9735() { checkNotSubtype("{{any f2} f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_9736() { checkNotSubtype("{{any f2} f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_9737() { checkIsSubtype("{{any f2} f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_9738() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_9739() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_9740() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_9741() { checkNotSubtype("{{null f1} f1}","[void]"); }
	@Test public void test_9742() { checkNotSubtype("{{null f1} f1}","[any]"); }
	@Test public void test_9743() { checkNotSubtype("{{null f1} f1}","[null]"); }
	@Test public void test_9744() { checkNotSubtype("{{null f1} f1}","[int]"); }
	@Test public void test_9745() { checkIsSubtype("{{null f1} f1}","{void f1}"); }
	@Test public void test_9746() { checkIsSubtype("{{null f1} f1}","{void f2}"); }
	@Test public void test_9747() { checkNotSubtype("{{null f1} f1}","{any f1}"); }
	@Test public void test_9748() { checkNotSubtype("{{null f1} f1}","{any f2}"); }
	@Test public void test_9749() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test public void test_9750() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test public void test_9751() { checkNotSubtype("{{null f1} f1}","{int f1}"); }
	@Test public void test_9752() { checkNotSubtype("{{null f1} f1}","{int f2}"); }
	@Test public void test_9753() { checkNotSubtype("{{null f1} f1}","[[void]]"); }
	@Test public void test_9754() { checkNotSubtype("{{null f1} f1}","[[any]]"); }
	@Test public void test_9755() { checkNotSubtype("{{null f1} f1}","[[null]]"); }
	@Test public void test_9756() { checkNotSubtype("{{null f1} f1}","[[int]]"); }
	@Test public void test_9757() { checkNotSubtype("{{null f1} f1}","[{void f1}]"); }
	@Test public void test_9758() { checkNotSubtype("{{null f1} f1}","[{void f2}]"); }
	@Test public void test_9759() { checkNotSubtype("{{null f1} f1}","[{any f1}]"); }
	@Test public void test_9760() { checkNotSubtype("{{null f1} f1}","[{any f2}]"); }
	@Test public void test_9761() { checkNotSubtype("{{null f1} f1}","[{null f1}]"); }
	@Test public void test_9762() { checkNotSubtype("{{null f1} f1}","[{null f2}]"); }
	@Test public void test_9763() { checkNotSubtype("{{null f1} f1}","[{int f1}]"); }
	@Test public void test_9764() { checkNotSubtype("{{null f1} f1}","[{int f2}]"); }
	@Test public void test_9765() { checkIsSubtype("{{null f1} f1}","{void f1,void f2}"); }
	@Test public void test_9766() { checkIsSubtype("{{null f1} f1}","{void f2,void f3}"); }
	@Test public void test_9767() { checkIsSubtype("{{null f1} f1}","{void f1,any f2}"); }
	@Test public void test_9768() { checkIsSubtype("{{null f1} f1}","{void f2,any f3}"); }
	@Test public void test_9769() { checkIsSubtype("{{null f1} f1}","{void f1,null f2}"); }
	@Test public void test_9770() { checkIsSubtype("{{null f1} f1}","{void f2,null f3}"); }
	@Test public void test_9771() { checkIsSubtype("{{null f1} f1}","{void f1,int f2}"); }
	@Test public void test_9772() { checkIsSubtype("{{null f1} f1}","{void f2,int f3}"); }
	@Test public void test_9773() { checkIsSubtype("{{null f1} f1}","{any f1,void f2}"); }
	@Test public void test_9774() { checkIsSubtype("{{null f1} f1}","{any f2,void f3}"); }
	@Test public void test_9775() { checkNotSubtype("{{null f1} f1}","{any f1,any f2}"); }
	@Test public void test_9776() { checkNotSubtype("{{null f1} f1}","{any f2,any f3}"); }
	@Test public void test_9777() { checkNotSubtype("{{null f1} f1}","{any f1,null f2}"); }
	@Test public void test_9778() { checkNotSubtype("{{null f1} f1}","{any f2,null f3}"); }
	@Test public void test_9779() { checkNotSubtype("{{null f1} f1}","{any f1,int f2}"); }
	@Test public void test_9780() { checkNotSubtype("{{null f1} f1}","{any f2,int f3}"); }
	@Test public void test_9781() { checkIsSubtype("{{null f1} f1}","{null f1,void f2}"); }
	@Test public void test_9782() { checkIsSubtype("{{null f1} f1}","{null f2,void f3}"); }
	@Test public void test_9783() { checkNotSubtype("{{null f1} f1}","{null f1,any f2}"); }
	@Test public void test_9784() { checkNotSubtype("{{null f1} f1}","{null f2,any f3}"); }
	@Test public void test_9785() { checkNotSubtype("{{null f1} f1}","{null f1,null f2}"); }
	@Test public void test_9786() { checkNotSubtype("{{null f1} f1}","{null f2,null f3}"); }
	@Test public void test_9787() { checkNotSubtype("{{null f1} f1}","{null f1,int f2}"); }
	@Test public void test_9788() { checkNotSubtype("{{null f1} f1}","{null f2,int f3}"); }
	@Test public void test_9789() { checkIsSubtype("{{null f1} f1}","{int f1,void f2}"); }
	@Test public void test_9790() { checkIsSubtype("{{null f1} f1}","{int f2,void f3}"); }
	@Test public void test_9791() { checkNotSubtype("{{null f1} f1}","{int f1,any f2}"); }
	@Test public void test_9792() { checkNotSubtype("{{null f1} f1}","{int f2,any f3}"); }
	@Test public void test_9793() { checkNotSubtype("{{null f1} f1}","{int f1,null f2}"); }
	@Test public void test_9794() { checkNotSubtype("{{null f1} f1}","{int f2,null f3}"); }
	@Test public void test_9795() { checkNotSubtype("{{null f1} f1}","{int f1,int f2}"); }
	@Test public void test_9796() { checkNotSubtype("{{null f1} f1}","{int f2,int f3}"); }
	@Test public void test_9797() { checkNotSubtype("{{null f1} f1}","{[void] f1}"); }
	@Test public void test_9798() { checkNotSubtype("{{null f1} f1}","{[void] f2}"); }
	@Test public void test_9799() { checkIsSubtype("{{null f1} f1}","{[void] f1,void f2}"); }
	@Test public void test_9800() { checkIsSubtype("{{null f1} f1}","{[void] f2,void f3}"); }
	@Test public void test_9801() { checkNotSubtype("{{null f1} f1}","{[any] f1}"); }
	@Test public void test_9802() { checkNotSubtype("{{null f1} f1}","{[any] f2}"); }
	@Test public void test_9803() { checkNotSubtype("{{null f1} f1}","{[any] f1,any f2}"); }
	@Test public void test_9804() { checkNotSubtype("{{null f1} f1}","{[any] f2,any f3}"); }
	@Test public void test_9805() { checkNotSubtype("{{null f1} f1}","{[null] f1}"); }
	@Test public void test_9806() { checkNotSubtype("{{null f1} f1}","{[null] f2}"); }
	@Test public void test_9807() { checkNotSubtype("{{null f1} f1}","{[null] f1,null f2}"); }
	@Test public void test_9808() { checkNotSubtype("{{null f1} f1}","{[null] f2,null f3}"); }
	@Test public void test_9809() { checkNotSubtype("{{null f1} f1}","{[int] f1}"); }
	@Test public void test_9810() { checkNotSubtype("{{null f1} f1}","{[int] f2}"); }
	@Test public void test_9811() { checkNotSubtype("{{null f1} f1}","{[int] f1,int f2}"); }
	@Test public void test_9812() { checkNotSubtype("{{null f1} f1}","{[int] f2,int f3}"); }
	@Test public void test_9813() { checkIsSubtype("{{null f1} f1}","{{void f1} f1}"); }
	@Test public void test_9814() { checkIsSubtype("{{null f1} f1}","{{void f2} f1}"); }
	@Test public void test_9815() { checkIsSubtype("{{null f1} f1}","{{void f1} f2}"); }
	@Test public void test_9816() { checkIsSubtype("{{null f1} f1}","{{void f2} f2}"); }
	@Test public void test_9817() { checkIsSubtype("{{null f1} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_9818() { checkIsSubtype("{{null f1} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_9819() { checkIsSubtype("{{null f1} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_9820() { checkIsSubtype("{{null f1} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_9821() { checkNotSubtype("{{null f1} f1}","{{any f1} f1}"); }
	@Test public void test_9822() { checkNotSubtype("{{null f1} f1}","{{any f2} f1}"); }
	@Test public void test_9823() { checkNotSubtype("{{null f1} f1}","{{any f1} f2}"); }
	@Test public void test_9824() { checkNotSubtype("{{null f1} f1}","{{any f2} f2}"); }
	@Test public void test_9825() { checkNotSubtype("{{null f1} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_9826() { checkNotSubtype("{{null f1} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_9827() { checkNotSubtype("{{null f1} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_9828() { checkNotSubtype("{{null f1} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_9829() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test public void test_9830() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test public void test_9831() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test public void test_9832() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test public void test_9833() { checkNotSubtype("{{null f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_9834() { checkNotSubtype("{{null f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_9835() { checkNotSubtype("{{null f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_9836() { checkNotSubtype("{{null f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_9837() { checkNotSubtype("{{null f1} f1}","{{int f1} f1}"); }
	@Test public void test_9838() { checkNotSubtype("{{null f1} f1}","{{int f2} f1}"); }
	@Test public void test_9839() { checkNotSubtype("{{null f1} f1}","{{int f1} f2}"); }
	@Test public void test_9840() { checkNotSubtype("{{null f1} f1}","{{int f2} f2}"); }
	@Test public void test_9841() { checkNotSubtype("{{null f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_9842() { checkNotSubtype("{{null f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_9843() { checkNotSubtype("{{null f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_9844() { checkNotSubtype("{{null f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_9845() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_9846() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_9847() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_9848() { checkNotSubtype("{{null f2} f1}","[void]"); }
	@Test public void test_9849() { checkNotSubtype("{{null f2} f1}","[any]"); }
	@Test public void test_9850() { checkNotSubtype("{{null f2} f1}","[null]"); }
	@Test public void test_9851() { checkNotSubtype("{{null f2} f1}","[int]"); }
	@Test public void test_9852() { checkIsSubtype("{{null f2} f1}","{void f1}"); }
	@Test public void test_9853() { checkIsSubtype("{{null f2} f1}","{void f2}"); }
	@Test public void test_9854() { checkNotSubtype("{{null f2} f1}","{any f1}"); }
	@Test public void test_9855() { checkNotSubtype("{{null f2} f1}","{any f2}"); }
	@Test public void test_9856() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test public void test_9857() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test public void test_9858() { checkNotSubtype("{{null f2} f1}","{int f1}"); }
	@Test public void test_9859() { checkNotSubtype("{{null f2} f1}","{int f2}"); }
	@Test public void test_9860() { checkNotSubtype("{{null f2} f1}","[[void]]"); }
	@Test public void test_9861() { checkNotSubtype("{{null f2} f1}","[[any]]"); }
	@Test public void test_9862() { checkNotSubtype("{{null f2} f1}","[[null]]"); }
	@Test public void test_9863() { checkNotSubtype("{{null f2} f1}","[[int]]"); }
	@Test public void test_9864() { checkNotSubtype("{{null f2} f1}","[{void f1}]"); }
	@Test public void test_9865() { checkNotSubtype("{{null f2} f1}","[{void f2}]"); }
	@Test public void test_9866() { checkNotSubtype("{{null f2} f1}","[{any f1}]"); }
	@Test public void test_9867() { checkNotSubtype("{{null f2} f1}","[{any f2}]"); }
	@Test public void test_9868() { checkNotSubtype("{{null f2} f1}","[{null f1}]"); }
	@Test public void test_9869() { checkNotSubtype("{{null f2} f1}","[{null f2}]"); }
	@Test public void test_9870() { checkNotSubtype("{{null f2} f1}","[{int f1}]"); }
	@Test public void test_9871() { checkNotSubtype("{{null f2} f1}","[{int f2}]"); }
	@Test public void test_9872() { checkIsSubtype("{{null f2} f1}","{void f1,void f2}"); }
	@Test public void test_9873() { checkIsSubtype("{{null f2} f1}","{void f2,void f3}"); }
	@Test public void test_9874() { checkIsSubtype("{{null f2} f1}","{void f1,any f2}"); }
	@Test public void test_9875() { checkIsSubtype("{{null f2} f1}","{void f2,any f3}"); }
	@Test public void test_9876() { checkIsSubtype("{{null f2} f1}","{void f1,null f2}"); }
	@Test public void test_9877() { checkIsSubtype("{{null f2} f1}","{void f2,null f3}"); }
	@Test public void test_9878() { checkIsSubtype("{{null f2} f1}","{void f1,int f2}"); }
	@Test public void test_9879() { checkIsSubtype("{{null f2} f1}","{void f2,int f3}"); }
	@Test public void test_9880() { checkIsSubtype("{{null f2} f1}","{any f1,void f2}"); }
	@Test public void test_9881() { checkIsSubtype("{{null f2} f1}","{any f2,void f3}"); }
	@Test public void test_9882() { checkNotSubtype("{{null f2} f1}","{any f1,any f2}"); }
	@Test public void test_9883() { checkNotSubtype("{{null f2} f1}","{any f2,any f3}"); }
	@Test public void test_9884() { checkNotSubtype("{{null f2} f1}","{any f1,null f2}"); }
	@Test public void test_9885() { checkNotSubtype("{{null f2} f1}","{any f2,null f3}"); }
	@Test public void test_9886() { checkNotSubtype("{{null f2} f1}","{any f1,int f2}"); }
	@Test public void test_9887() { checkNotSubtype("{{null f2} f1}","{any f2,int f3}"); }
	@Test public void test_9888() { checkIsSubtype("{{null f2} f1}","{null f1,void f2}"); }
	@Test public void test_9889() { checkIsSubtype("{{null f2} f1}","{null f2,void f3}"); }
	@Test public void test_9890() { checkNotSubtype("{{null f2} f1}","{null f1,any f2}"); }
	@Test public void test_9891() { checkNotSubtype("{{null f2} f1}","{null f2,any f3}"); }
	@Test public void test_9892() { checkNotSubtype("{{null f2} f1}","{null f1,null f2}"); }
	@Test public void test_9893() { checkNotSubtype("{{null f2} f1}","{null f2,null f3}"); }
	@Test public void test_9894() { checkNotSubtype("{{null f2} f1}","{null f1,int f2}"); }
	@Test public void test_9895() { checkNotSubtype("{{null f2} f1}","{null f2,int f3}"); }
	@Test public void test_9896() { checkIsSubtype("{{null f2} f1}","{int f1,void f2}"); }
	@Test public void test_9897() { checkIsSubtype("{{null f2} f1}","{int f2,void f3}"); }
	@Test public void test_9898() { checkNotSubtype("{{null f2} f1}","{int f1,any f2}"); }
	@Test public void test_9899() { checkNotSubtype("{{null f2} f1}","{int f2,any f3}"); }
	@Test public void test_9900() { checkNotSubtype("{{null f2} f1}","{int f1,null f2}"); }
	@Test public void test_9901() { checkNotSubtype("{{null f2} f1}","{int f2,null f3}"); }
	@Test public void test_9902() { checkNotSubtype("{{null f2} f1}","{int f1,int f2}"); }
	@Test public void test_9903() { checkNotSubtype("{{null f2} f1}","{int f2,int f3}"); }
	@Test public void test_9904() { checkNotSubtype("{{null f2} f1}","{[void] f1}"); }
	@Test public void test_9905() { checkNotSubtype("{{null f2} f1}","{[void] f2}"); }
	@Test public void test_9906() { checkIsSubtype("{{null f2} f1}","{[void] f1,void f2}"); }
	@Test public void test_9907() { checkIsSubtype("{{null f2} f1}","{[void] f2,void f3}"); }
	@Test public void test_9908() { checkNotSubtype("{{null f2} f1}","{[any] f1}"); }
	@Test public void test_9909() { checkNotSubtype("{{null f2} f1}","{[any] f2}"); }
	@Test public void test_9910() { checkNotSubtype("{{null f2} f1}","{[any] f1,any f2}"); }
	@Test public void test_9911() { checkNotSubtype("{{null f2} f1}","{[any] f2,any f3}"); }
	@Test public void test_9912() { checkNotSubtype("{{null f2} f1}","{[null] f1}"); }
	@Test public void test_9913() { checkNotSubtype("{{null f2} f1}","{[null] f2}"); }
	@Test public void test_9914() { checkNotSubtype("{{null f2} f1}","{[null] f1,null f2}"); }
	@Test public void test_9915() { checkNotSubtype("{{null f2} f1}","{[null] f2,null f3}"); }
	@Test public void test_9916() { checkNotSubtype("{{null f2} f1}","{[int] f1}"); }
	@Test public void test_9917() { checkNotSubtype("{{null f2} f1}","{[int] f2}"); }
	@Test public void test_9918() { checkNotSubtype("{{null f2} f1}","{[int] f1,int f2}"); }
	@Test public void test_9919() { checkNotSubtype("{{null f2} f1}","{[int] f2,int f3}"); }
	@Test public void test_9920() { checkIsSubtype("{{null f2} f1}","{{void f1} f1}"); }
	@Test public void test_9921() { checkIsSubtype("{{null f2} f1}","{{void f2} f1}"); }
	@Test public void test_9922() { checkIsSubtype("{{null f2} f1}","{{void f1} f2}"); }
	@Test public void test_9923() { checkIsSubtype("{{null f2} f1}","{{void f2} f2}"); }
	@Test public void test_9924() { checkIsSubtype("{{null f2} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_9925() { checkIsSubtype("{{null f2} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_9926() { checkIsSubtype("{{null f2} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_9927() { checkIsSubtype("{{null f2} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_9928() { checkNotSubtype("{{null f2} f1}","{{any f1} f1}"); }
	@Test public void test_9929() { checkNotSubtype("{{null f2} f1}","{{any f2} f1}"); }
	@Test public void test_9930() { checkNotSubtype("{{null f2} f1}","{{any f1} f2}"); }
	@Test public void test_9931() { checkNotSubtype("{{null f2} f1}","{{any f2} f2}"); }
	@Test public void test_9932() { checkNotSubtype("{{null f2} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_9933() { checkNotSubtype("{{null f2} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_9934() { checkNotSubtype("{{null f2} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_9935() { checkNotSubtype("{{null f2} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_9936() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test public void test_9937() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test public void test_9938() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test public void test_9939() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test public void test_9940() { checkNotSubtype("{{null f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_9941() { checkNotSubtype("{{null f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_9942() { checkNotSubtype("{{null f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_9943() { checkNotSubtype("{{null f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_9944() { checkNotSubtype("{{null f2} f1}","{{int f1} f1}"); }
	@Test public void test_9945() { checkNotSubtype("{{null f2} f1}","{{int f2} f1}"); }
	@Test public void test_9946() { checkNotSubtype("{{null f2} f1}","{{int f1} f2}"); }
	@Test public void test_9947() { checkNotSubtype("{{null f2} f1}","{{int f2} f2}"); }
	@Test public void test_9948() { checkNotSubtype("{{null f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_9949() { checkNotSubtype("{{null f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_9950() { checkNotSubtype("{{null f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_9951() { checkNotSubtype("{{null f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_9952() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_9953() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_9954() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_9955() { checkNotSubtype("{{null f1} f2}","[void]"); }
	@Test public void test_9956() { checkNotSubtype("{{null f1} f2}","[any]"); }
	@Test public void test_9957() { checkNotSubtype("{{null f1} f2}","[null]"); }
	@Test public void test_9958() { checkNotSubtype("{{null f1} f2}","[int]"); }
	@Test public void test_9959() { checkIsSubtype("{{null f1} f2}","{void f1}"); }
	@Test public void test_9960() { checkIsSubtype("{{null f1} f2}","{void f2}"); }
	@Test public void test_9961() { checkNotSubtype("{{null f1} f2}","{any f1}"); }
	@Test public void test_9962() { checkNotSubtype("{{null f1} f2}","{any f2}"); }
	@Test public void test_9963() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test public void test_9964() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test public void test_9965() { checkNotSubtype("{{null f1} f2}","{int f1}"); }
	@Test public void test_9966() { checkNotSubtype("{{null f1} f2}","{int f2}"); }
	@Test public void test_9967() { checkNotSubtype("{{null f1} f2}","[[void]]"); }
	@Test public void test_9968() { checkNotSubtype("{{null f1} f2}","[[any]]"); }
	@Test public void test_9969() { checkNotSubtype("{{null f1} f2}","[[null]]"); }
	@Test public void test_9970() { checkNotSubtype("{{null f1} f2}","[[int]]"); }
	@Test public void test_9971() { checkNotSubtype("{{null f1} f2}","[{void f1}]"); }
	@Test public void test_9972() { checkNotSubtype("{{null f1} f2}","[{void f2}]"); }
	@Test public void test_9973() { checkNotSubtype("{{null f1} f2}","[{any f1}]"); }
	@Test public void test_9974() { checkNotSubtype("{{null f1} f2}","[{any f2}]"); }
	@Test public void test_9975() { checkNotSubtype("{{null f1} f2}","[{null f1}]"); }
	@Test public void test_9976() { checkNotSubtype("{{null f1} f2}","[{null f2}]"); }
	@Test public void test_9977() { checkNotSubtype("{{null f1} f2}","[{int f1}]"); }
	@Test public void test_9978() { checkNotSubtype("{{null f1} f2}","[{int f2}]"); }
	@Test public void test_9979() { checkIsSubtype("{{null f1} f2}","{void f1,void f2}"); }
	@Test public void test_9980() { checkIsSubtype("{{null f1} f2}","{void f2,void f3}"); }
	@Test public void test_9981() { checkIsSubtype("{{null f1} f2}","{void f1,any f2}"); }
	@Test public void test_9982() { checkIsSubtype("{{null f1} f2}","{void f2,any f3}"); }
	@Test public void test_9983() { checkIsSubtype("{{null f1} f2}","{void f1,null f2}"); }
	@Test public void test_9984() { checkIsSubtype("{{null f1} f2}","{void f2,null f3}"); }
	@Test public void test_9985() { checkIsSubtype("{{null f1} f2}","{void f1,int f2}"); }
	@Test public void test_9986() { checkIsSubtype("{{null f1} f2}","{void f2,int f3}"); }
	@Test public void test_9987() { checkIsSubtype("{{null f1} f2}","{any f1,void f2}"); }
	@Test public void test_9988() { checkIsSubtype("{{null f1} f2}","{any f2,void f3}"); }
	@Test public void test_9989() { checkNotSubtype("{{null f1} f2}","{any f1,any f2}"); }
	@Test public void test_9990() { checkNotSubtype("{{null f1} f2}","{any f2,any f3}"); }
	@Test public void test_9991() { checkNotSubtype("{{null f1} f2}","{any f1,null f2}"); }
	@Test public void test_9992() { checkNotSubtype("{{null f1} f2}","{any f2,null f3}"); }
	@Test public void test_9993() { checkNotSubtype("{{null f1} f2}","{any f1,int f2}"); }
	@Test public void test_9994() { checkNotSubtype("{{null f1} f2}","{any f2,int f3}"); }
	@Test public void test_9995() { checkIsSubtype("{{null f1} f2}","{null f1,void f2}"); }
	@Test public void test_9996() { checkIsSubtype("{{null f1} f2}","{null f2,void f3}"); }
	@Test public void test_9997() { checkNotSubtype("{{null f1} f2}","{null f1,any f2}"); }
	@Test public void test_9998() { checkNotSubtype("{{null f1} f2}","{null f2,any f3}"); }
	@Test public void test_9999() { checkNotSubtype("{{null f1} f2}","{null f1,null f2}"); }
	@Test public void test_10000() { checkNotSubtype("{{null f1} f2}","{null f2,null f3}"); }
	@Test public void test_10001() { checkNotSubtype("{{null f1} f2}","{null f1,int f2}"); }
	@Test public void test_10002() { checkNotSubtype("{{null f1} f2}","{null f2,int f3}"); }
	@Test public void test_10003() { checkIsSubtype("{{null f1} f2}","{int f1,void f2}"); }
	@Test public void test_10004() { checkIsSubtype("{{null f1} f2}","{int f2,void f3}"); }
	@Test public void test_10005() { checkNotSubtype("{{null f1} f2}","{int f1,any f2}"); }
	@Test public void test_10006() { checkNotSubtype("{{null f1} f2}","{int f2,any f3}"); }
	@Test public void test_10007() { checkNotSubtype("{{null f1} f2}","{int f1,null f2}"); }
	@Test public void test_10008() { checkNotSubtype("{{null f1} f2}","{int f2,null f3}"); }
	@Test public void test_10009() { checkNotSubtype("{{null f1} f2}","{int f1,int f2}"); }
	@Test public void test_10010() { checkNotSubtype("{{null f1} f2}","{int f2,int f3}"); }
	@Test public void test_10011() { checkNotSubtype("{{null f1} f2}","{[void] f1}"); }
	@Test public void test_10012() { checkNotSubtype("{{null f1} f2}","{[void] f2}"); }
	@Test public void test_10013() { checkIsSubtype("{{null f1} f2}","{[void] f1,void f2}"); }
	@Test public void test_10014() { checkIsSubtype("{{null f1} f2}","{[void] f2,void f3}"); }
	@Test public void test_10015() { checkNotSubtype("{{null f1} f2}","{[any] f1}"); }
	@Test public void test_10016() { checkNotSubtype("{{null f1} f2}","{[any] f2}"); }
	@Test public void test_10017() { checkNotSubtype("{{null f1} f2}","{[any] f1,any f2}"); }
	@Test public void test_10018() { checkNotSubtype("{{null f1} f2}","{[any] f2,any f3}"); }
	@Test public void test_10019() { checkNotSubtype("{{null f1} f2}","{[null] f1}"); }
	@Test public void test_10020() { checkNotSubtype("{{null f1} f2}","{[null] f2}"); }
	@Test public void test_10021() { checkNotSubtype("{{null f1} f2}","{[null] f1,null f2}"); }
	@Test public void test_10022() { checkNotSubtype("{{null f1} f2}","{[null] f2,null f3}"); }
	@Test public void test_10023() { checkNotSubtype("{{null f1} f2}","{[int] f1}"); }
	@Test public void test_10024() { checkNotSubtype("{{null f1} f2}","{[int] f2}"); }
	@Test public void test_10025() { checkNotSubtype("{{null f1} f2}","{[int] f1,int f2}"); }
	@Test public void test_10026() { checkNotSubtype("{{null f1} f2}","{[int] f2,int f3}"); }
	@Test public void test_10027() { checkIsSubtype("{{null f1} f2}","{{void f1} f1}"); }
	@Test public void test_10028() { checkIsSubtype("{{null f1} f2}","{{void f2} f1}"); }
	@Test public void test_10029() { checkIsSubtype("{{null f1} f2}","{{void f1} f2}"); }
	@Test public void test_10030() { checkIsSubtype("{{null f1} f2}","{{void f2} f2}"); }
	@Test public void test_10031() { checkIsSubtype("{{null f1} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_10032() { checkIsSubtype("{{null f1} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_10033() { checkIsSubtype("{{null f1} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_10034() { checkIsSubtype("{{null f1} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_10035() { checkNotSubtype("{{null f1} f2}","{{any f1} f1}"); }
	@Test public void test_10036() { checkNotSubtype("{{null f1} f2}","{{any f2} f1}"); }
	@Test public void test_10037() { checkNotSubtype("{{null f1} f2}","{{any f1} f2}"); }
	@Test public void test_10038() { checkNotSubtype("{{null f1} f2}","{{any f2} f2}"); }
	@Test public void test_10039() { checkNotSubtype("{{null f1} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_10040() { checkNotSubtype("{{null f1} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_10041() { checkNotSubtype("{{null f1} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_10042() { checkNotSubtype("{{null f1} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_10043() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test public void test_10044() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test public void test_10045() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test public void test_10046() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test public void test_10047() { checkNotSubtype("{{null f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_10048() { checkNotSubtype("{{null f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_10049() { checkNotSubtype("{{null f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_10050() { checkNotSubtype("{{null f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_10051() { checkNotSubtype("{{null f1} f2}","{{int f1} f1}"); }
	@Test public void test_10052() { checkNotSubtype("{{null f1} f2}","{{int f2} f1}"); }
	@Test public void test_10053() { checkNotSubtype("{{null f1} f2}","{{int f1} f2}"); }
	@Test public void test_10054() { checkNotSubtype("{{null f1} f2}","{{int f2} f2}"); }
	@Test public void test_10055() { checkNotSubtype("{{null f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_10056() { checkNotSubtype("{{null f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_10057() { checkNotSubtype("{{null f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_10058() { checkNotSubtype("{{null f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_10059() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_10060() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_10061() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_10062() { checkNotSubtype("{{null f2} f2}","[void]"); }
	@Test public void test_10063() { checkNotSubtype("{{null f2} f2}","[any]"); }
	@Test public void test_10064() { checkNotSubtype("{{null f2} f2}","[null]"); }
	@Test public void test_10065() { checkNotSubtype("{{null f2} f2}","[int]"); }
	@Test public void test_10066() { checkIsSubtype("{{null f2} f2}","{void f1}"); }
	@Test public void test_10067() { checkIsSubtype("{{null f2} f2}","{void f2}"); }
	@Test public void test_10068() { checkNotSubtype("{{null f2} f2}","{any f1}"); }
	@Test public void test_10069() { checkNotSubtype("{{null f2} f2}","{any f2}"); }
	@Test public void test_10070() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test public void test_10071() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test public void test_10072() { checkNotSubtype("{{null f2} f2}","{int f1}"); }
	@Test public void test_10073() { checkNotSubtype("{{null f2} f2}","{int f2}"); }
	@Test public void test_10074() { checkNotSubtype("{{null f2} f2}","[[void]]"); }
	@Test public void test_10075() { checkNotSubtype("{{null f2} f2}","[[any]]"); }
	@Test public void test_10076() { checkNotSubtype("{{null f2} f2}","[[null]]"); }
	@Test public void test_10077() { checkNotSubtype("{{null f2} f2}","[[int]]"); }
	@Test public void test_10078() { checkNotSubtype("{{null f2} f2}","[{void f1}]"); }
	@Test public void test_10079() { checkNotSubtype("{{null f2} f2}","[{void f2}]"); }
	@Test public void test_10080() { checkNotSubtype("{{null f2} f2}","[{any f1}]"); }
	@Test public void test_10081() { checkNotSubtype("{{null f2} f2}","[{any f2}]"); }
	@Test public void test_10082() { checkNotSubtype("{{null f2} f2}","[{null f1}]"); }
	@Test public void test_10083() { checkNotSubtype("{{null f2} f2}","[{null f2}]"); }
	@Test public void test_10084() { checkNotSubtype("{{null f2} f2}","[{int f1}]"); }
	@Test public void test_10085() { checkNotSubtype("{{null f2} f2}","[{int f2}]"); }
	@Test public void test_10086() { checkIsSubtype("{{null f2} f2}","{void f1,void f2}"); }
	@Test public void test_10087() { checkIsSubtype("{{null f2} f2}","{void f2,void f3}"); }
	@Test public void test_10088() { checkIsSubtype("{{null f2} f2}","{void f1,any f2}"); }
	@Test public void test_10089() { checkIsSubtype("{{null f2} f2}","{void f2,any f3}"); }
	@Test public void test_10090() { checkIsSubtype("{{null f2} f2}","{void f1,null f2}"); }
	@Test public void test_10091() { checkIsSubtype("{{null f2} f2}","{void f2,null f3}"); }
	@Test public void test_10092() { checkIsSubtype("{{null f2} f2}","{void f1,int f2}"); }
	@Test public void test_10093() { checkIsSubtype("{{null f2} f2}","{void f2,int f3}"); }
	@Test public void test_10094() { checkIsSubtype("{{null f2} f2}","{any f1,void f2}"); }
	@Test public void test_10095() { checkIsSubtype("{{null f2} f2}","{any f2,void f3}"); }
	@Test public void test_10096() { checkNotSubtype("{{null f2} f2}","{any f1,any f2}"); }
	@Test public void test_10097() { checkNotSubtype("{{null f2} f2}","{any f2,any f3}"); }
	@Test public void test_10098() { checkNotSubtype("{{null f2} f2}","{any f1,null f2}"); }
	@Test public void test_10099() { checkNotSubtype("{{null f2} f2}","{any f2,null f3}"); }
	@Test public void test_10100() { checkNotSubtype("{{null f2} f2}","{any f1,int f2}"); }
	@Test public void test_10101() { checkNotSubtype("{{null f2} f2}","{any f2,int f3}"); }
	@Test public void test_10102() { checkIsSubtype("{{null f2} f2}","{null f1,void f2}"); }
	@Test public void test_10103() { checkIsSubtype("{{null f2} f2}","{null f2,void f3}"); }
	@Test public void test_10104() { checkNotSubtype("{{null f2} f2}","{null f1,any f2}"); }
	@Test public void test_10105() { checkNotSubtype("{{null f2} f2}","{null f2,any f3}"); }
	@Test public void test_10106() { checkNotSubtype("{{null f2} f2}","{null f1,null f2}"); }
	@Test public void test_10107() { checkNotSubtype("{{null f2} f2}","{null f2,null f3}"); }
	@Test public void test_10108() { checkNotSubtype("{{null f2} f2}","{null f1,int f2}"); }
	@Test public void test_10109() { checkNotSubtype("{{null f2} f2}","{null f2,int f3}"); }
	@Test public void test_10110() { checkIsSubtype("{{null f2} f2}","{int f1,void f2}"); }
	@Test public void test_10111() { checkIsSubtype("{{null f2} f2}","{int f2,void f3}"); }
	@Test public void test_10112() { checkNotSubtype("{{null f2} f2}","{int f1,any f2}"); }
	@Test public void test_10113() { checkNotSubtype("{{null f2} f2}","{int f2,any f3}"); }
	@Test public void test_10114() { checkNotSubtype("{{null f2} f2}","{int f1,null f2}"); }
	@Test public void test_10115() { checkNotSubtype("{{null f2} f2}","{int f2,null f3}"); }
	@Test public void test_10116() { checkNotSubtype("{{null f2} f2}","{int f1,int f2}"); }
	@Test public void test_10117() { checkNotSubtype("{{null f2} f2}","{int f2,int f3}"); }
	@Test public void test_10118() { checkNotSubtype("{{null f2} f2}","{[void] f1}"); }
	@Test public void test_10119() { checkNotSubtype("{{null f2} f2}","{[void] f2}"); }
	@Test public void test_10120() { checkIsSubtype("{{null f2} f2}","{[void] f1,void f2}"); }
	@Test public void test_10121() { checkIsSubtype("{{null f2} f2}","{[void] f2,void f3}"); }
	@Test public void test_10122() { checkNotSubtype("{{null f2} f2}","{[any] f1}"); }
	@Test public void test_10123() { checkNotSubtype("{{null f2} f2}","{[any] f2}"); }
	@Test public void test_10124() { checkNotSubtype("{{null f2} f2}","{[any] f1,any f2}"); }
	@Test public void test_10125() { checkNotSubtype("{{null f2} f2}","{[any] f2,any f3}"); }
	@Test public void test_10126() { checkNotSubtype("{{null f2} f2}","{[null] f1}"); }
	@Test public void test_10127() { checkNotSubtype("{{null f2} f2}","{[null] f2}"); }
	@Test public void test_10128() { checkNotSubtype("{{null f2} f2}","{[null] f1,null f2}"); }
	@Test public void test_10129() { checkNotSubtype("{{null f2} f2}","{[null] f2,null f3}"); }
	@Test public void test_10130() { checkNotSubtype("{{null f2} f2}","{[int] f1}"); }
	@Test public void test_10131() { checkNotSubtype("{{null f2} f2}","{[int] f2}"); }
	@Test public void test_10132() { checkNotSubtype("{{null f2} f2}","{[int] f1,int f2}"); }
	@Test public void test_10133() { checkNotSubtype("{{null f2} f2}","{[int] f2,int f3}"); }
	@Test public void test_10134() { checkIsSubtype("{{null f2} f2}","{{void f1} f1}"); }
	@Test public void test_10135() { checkIsSubtype("{{null f2} f2}","{{void f2} f1}"); }
	@Test public void test_10136() { checkIsSubtype("{{null f2} f2}","{{void f1} f2}"); }
	@Test public void test_10137() { checkIsSubtype("{{null f2} f2}","{{void f2} f2}"); }
	@Test public void test_10138() { checkIsSubtype("{{null f2} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_10139() { checkIsSubtype("{{null f2} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_10140() { checkIsSubtype("{{null f2} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_10141() { checkIsSubtype("{{null f2} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_10142() { checkNotSubtype("{{null f2} f2}","{{any f1} f1}"); }
	@Test public void test_10143() { checkNotSubtype("{{null f2} f2}","{{any f2} f1}"); }
	@Test public void test_10144() { checkNotSubtype("{{null f2} f2}","{{any f1} f2}"); }
	@Test public void test_10145() { checkNotSubtype("{{null f2} f2}","{{any f2} f2}"); }
	@Test public void test_10146() { checkNotSubtype("{{null f2} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_10147() { checkNotSubtype("{{null f2} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_10148() { checkNotSubtype("{{null f2} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_10149() { checkNotSubtype("{{null f2} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_10150() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test public void test_10151() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test public void test_10152() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test public void test_10153() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test public void test_10154() { checkNotSubtype("{{null f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_10155() { checkNotSubtype("{{null f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_10156() { checkNotSubtype("{{null f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_10157() { checkNotSubtype("{{null f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_10158() { checkNotSubtype("{{null f2} f2}","{{int f1} f1}"); }
	@Test public void test_10159() { checkNotSubtype("{{null f2} f2}","{{int f2} f1}"); }
	@Test public void test_10160() { checkNotSubtype("{{null f2} f2}","{{int f1} f2}"); }
	@Test public void test_10161() { checkNotSubtype("{{null f2} f2}","{{int f2} f2}"); }
	@Test public void test_10162() { checkNotSubtype("{{null f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_10163() { checkNotSubtype("{{null f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_10164() { checkNotSubtype("{{null f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_10165() { checkNotSubtype("{{null f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_10166() { checkNotSubtype("{{null f1} f1,null f2}","any"); }
	@Test public void test_10167() { checkNotSubtype("{{null f1} f1,null f2}","null"); }
	@Test public void test_10168() { checkNotSubtype("{{null f1} f1,null f2}","int"); }
	@Test public void test_10169() { checkNotSubtype("{{null f1} f1,null f2}","[void]"); }
	@Test public void test_10170() { checkNotSubtype("{{null f1} f1,null f2}","[any]"); }
	@Test public void test_10171() { checkNotSubtype("{{null f1} f1,null f2}","[null]"); }
	@Test public void test_10172() { checkNotSubtype("{{null f1} f1,null f2}","[int]"); }
	@Test public void test_10173() { checkIsSubtype("{{null f1} f1,null f2}","{void f1}"); }
	@Test public void test_10174() { checkIsSubtype("{{null f1} f1,null f2}","{void f2}"); }
	@Test public void test_10175() { checkNotSubtype("{{null f1} f1,null f2}","{any f1}"); }
	@Test public void test_10176() { checkNotSubtype("{{null f1} f1,null f2}","{any f2}"); }
	@Test public void test_10177() { checkNotSubtype("{{null f1} f1,null f2}","{null f1}"); }
	@Test public void test_10178() { checkNotSubtype("{{null f1} f1,null f2}","{null f2}"); }
	@Test public void test_10179() { checkNotSubtype("{{null f1} f1,null f2}","{int f1}"); }
	@Test public void test_10180() { checkNotSubtype("{{null f1} f1,null f2}","{int f2}"); }
	@Test public void test_10181() { checkNotSubtype("{{null f1} f1,null f2}","[[void]]"); }
	@Test public void test_10182() { checkNotSubtype("{{null f1} f1,null f2}","[[any]]"); }
	@Test public void test_10183() { checkNotSubtype("{{null f1} f1,null f2}","[[null]]"); }
	@Test public void test_10184() { checkNotSubtype("{{null f1} f1,null f2}","[[int]]"); }
	@Test public void test_10185() { checkNotSubtype("{{null f1} f1,null f2}","[{void f1}]"); }
	@Test public void test_10186() { checkNotSubtype("{{null f1} f1,null f2}","[{void f2}]"); }
	@Test public void test_10187() { checkNotSubtype("{{null f1} f1,null f2}","[{any f1}]"); }
	@Test public void test_10188() { checkNotSubtype("{{null f1} f1,null f2}","[{any f2}]"); }
	@Test public void test_10189() { checkNotSubtype("{{null f1} f1,null f2}","[{null f1}]"); }
	@Test public void test_10190() { checkNotSubtype("{{null f1} f1,null f2}","[{null f2}]"); }
	@Test public void test_10191() { checkNotSubtype("{{null f1} f1,null f2}","[{int f1}]"); }
	@Test public void test_10192() { checkNotSubtype("{{null f1} f1,null f2}","[{int f2}]"); }
	@Test public void test_10193() { checkIsSubtype("{{null f1} f1,null f2}","{void f1,void f2}"); }
	@Test public void test_10194() { checkIsSubtype("{{null f1} f1,null f2}","{void f2,void f3}"); }
	@Test public void test_10195() { checkIsSubtype("{{null f1} f1,null f2}","{void f1,any f2}"); }
	@Test public void test_10196() { checkIsSubtype("{{null f1} f1,null f2}","{void f2,any f3}"); }
	@Test public void test_10197() { checkIsSubtype("{{null f1} f1,null f2}","{void f1,null f2}"); }
	@Test public void test_10198() { checkIsSubtype("{{null f1} f1,null f2}","{void f2,null f3}"); }
	@Test public void test_10199() { checkIsSubtype("{{null f1} f1,null f2}","{void f1,int f2}"); }
	@Test public void test_10200() { checkIsSubtype("{{null f1} f1,null f2}","{void f2,int f3}"); }
	@Test public void test_10201() { checkIsSubtype("{{null f1} f1,null f2}","{any f1,void f2}"); }
	@Test public void test_10202() { checkIsSubtype("{{null f1} f1,null f2}","{any f2,void f3}"); }
	@Test public void test_10203() { checkNotSubtype("{{null f1} f1,null f2}","{any f1,any f2}"); }
	@Test public void test_10204() { checkNotSubtype("{{null f1} f1,null f2}","{any f2,any f3}"); }
	@Test public void test_10205() { checkNotSubtype("{{null f1} f1,null f2}","{any f1,null f2}"); }
	@Test public void test_10206() { checkNotSubtype("{{null f1} f1,null f2}","{any f2,null f3}"); }
	@Test public void test_10207() { checkNotSubtype("{{null f1} f1,null f2}","{any f1,int f2}"); }
	@Test public void test_10208() { checkNotSubtype("{{null f1} f1,null f2}","{any f2,int f3}"); }
	@Test public void test_10209() { checkIsSubtype("{{null f1} f1,null f2}","{null f1,void f2}"); }
	@Test public void test_10210() { checkIsSubtype("{{null f1} f1,null f2}","{null f2,void f3}"); }
	@Test public void test_10211() { checkNotSubtype("{{null f1} f1,null f2}","{null f1,any f2}"); }
	@Test public void test_10212() { checkNotSubtype("{{null f1} f1,null f2}","{null f2,any f3}"); }
	@Test public void test_10213() { checkNotSubtype("{{null f1} f1,null f2}","{null f1,null f2}"); }
	@Test public void test_10214() { checkNotSubtype("{{null f1} f1,null f2}","{null f2,null f3}"); }
	@Test public void test_10215() { checkNotSubtype("{{null f1} f1,null f2}","{null f1,int f2}"); }
	@Test public void test_10216() { checkNotSubtype("{{null f1} f1,null f2}","{null f2,int f3}"); }
	@Test public void test_10217() { checkIsSubtype("{{null f1} f1,null f2}","{int f1,void f2}"); }
	@Test public void test_10218() { checkIsSubtype("{{null f1} f1,null f2}","{int f2,void f3}"); }
	@Test public void test_10219() { checkNotSubtype("{{null f1} f1,null f2}","{int f1,any f2}"); }
	@Test public void test_10220() { checkNotSubtype("{{null f1} f1,null f2}","{int f2,any f3}"); }
	@Test public void test_10221() { checkNotSubtype("{{null f1} f1,null f2}","{int f1,null f2}"); }
	@Test public void test_10222() { checkNotSubtype("{{null f1} f1,null f2}","{int f2,null f3}"); }
	@Test public void test_10223() { checkNotSubtype("{{null f1} f1,null f2}","{int f1,int f2}"); }
	@Test public void test_10224() { checkNotSubtype("{{null f1} f1,null f2}","{int f2,int f3}"); }
	@Test public void test_10225() { checkNotSubtype("{{null f1} f1,null f2}","{[void] f1}"); }
	@Test public void test_10226() { checkNotSubtype("{{null f1} f1,null f2}","{[void] f2}"); }
	@Test public void test_10227() { checkIsSubtype("{{null f1} f1,null f2}","{[void] f1,void f2}"); }
	@Test public void test_10228() { checkIsSubtype("{{null f1} f1,null f2}","{[void] f2,void f3}"); }
	@Test public void test_10229() { checkNotSubtype("{{null f1} f1,null f2}","{[any] f1}"); }
	@Test public void test_10230() { checkNotSubtype("{{null f1} f1,null f2}","{[any] f2}"); }
	@Test public void test_10231() { checkNotSubtype("{{null f1} f1,null f2}","{[any] f1,any f2}"); }
	@Test public void test_10232() { checkNotSubtype("{{null f1} f1,null f2}","{[any] f2,any f3}"); }
	@Test public void test_10233() { checkNotSubtype("{{null f1} f1,null f2}","{[null] f1}"); }
	@Test public void test_10234() { checkNotSubtype("{{null f1} f1,null f2}","{[null] f2}"); }
	@Test public void test_10235() { checkNotSubtype("{{null f1} f1,null f2}","{[null] f1,null f2}"); }
	@Test public void test_10236() { checkNotSubtype("{{null f1} f1,null f2}","{[null] f2,null f3}"); }
	@Test public void test_10237() { checkNotSubtype("{{null f1} f1,null f2}","{[int] f1}"); }
	@Test public void test_10238() { checkNotSubtype("{{null f1} f1,null f2}","{[int] f2}"); }
	@Test public void test_10239() { checkNotSubtype("{{null f1} f1,null f2}","{[int] f1,int f2}"); }
	@Test public void test_10240() { checkNotSubtype("{{null f1} f1,null f2}","{[int] f2,int f3}"); }
	@Test public void test_10241() { checkIsSubtype("{{null f1} f1,null f2}","{{void f1} f1}"); }
	@Test public void test_10242() { checkIsSubtype("{{null f1} f1,null f2}","{{void f2} f1}"); }
	@Test public void test_10243() { checkIsSubtype("{{null f1} f1,null f2}","{{void f1} f2}"); }
	@Test public void test_10244() { checkIsSubtype("{{null f1} f1,null f2}","{{void f2} f2}"); }
	@Test public void test_10245() { checkIsSubtype("{{null f1} f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_10246() { checkIsSubtype("{{null f1} f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_10247() { checkIsSubtype("{{null f1} f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_10248() { checkIsSubtype("{{null f1} f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_10249() { checkNotSubtype("{{null f1} f1,null f2}","{{any f1} f1}"); }
	@Test public void test_10250() { checkNotSubtype("{{null f1} f1,null f2}","{{any f2} f1}"); }
	@Test public void test_10251() { checkNotSubtype("{{null f1} f1,null f2}","{{any f1} f2}"); }
	@Test public void test_10252() { checkNotSubtype("{{null f1} f1,null f2}","{{any f2} f2}"); }
	@Test public void test_10253() { checkNotSubtype("{{null f1} f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_10254() { checkNotSubtype("{{null f1} f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_10255() { checkNotSubtype("{{null f1} f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_10256() { checkNotSubtype("{{null f1} f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_10257() { checkNotSubtype("{{null f1} f1,null f2}","{{null f1} f1}"); }
	@Test public void test_10258() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f1}"); }
	@Test public void test_10259() { checkNotSubtype("{{null f1} f1,null f2}","{{null f1} f2}"); }
	@Test public void test_10260() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f2}"); }
	@Test public void test_10261() { checkIsSubtype("{{null f1} f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_10262() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_10263() { checkNotSubtype("{{null f1} f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_10264() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_10265() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f1}"); }
	@Test public void test_10266() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f1}"); }
	@Test public void test_10267() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f2}"); }
	@Test public void test_10268() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f2}"); }
	@Test public void test_10269() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_10270() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_10271() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_10272() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_10273() { checkNotSubtype("{{null f2} f1,null f2}","any"); }
	@Test public void test_10274() { checkNotSubtype("{{null f2} f1,null f2}","null"); }
	@Test public void test_10275() { checkNotSubtype("{{null f2} f1,null f2}","int"); }
	@Test public void test_10276() { checkNotSubtype("{{null f2} f1,null f2}","[void]"); }
	@Test public void test_10277() { checkNotSubtype("{{null f2} f1,null f2}","[any]"); }
	@Test public void test_10278() { checkNotSubtype("{{null f2} f1,null f2}","[null]"); }
	@Test public void test_10279() { checkNotSubtype("{{null f2} f1,null f2}","[int]"); }
	@Test public void test_10280() { checkIsSubtype("{{null f2} f1,null f2}","{void f1}"); }
	@Test public void test_10281() { checkIsSubtype("{{null f2} f1,null f2}","{void f2}"); }
	@Test public void test_10282() { checkNotSubtype("{{null f2} f1,null f2}","{any f1}"); }
	@Test public void test_10283() { checkNotSubtype("{{null f2} f1,null f2}","{any f2}"); }
	@Test public void test_10284() { checkNotSubtype("{{null f2} f1,null f2}","{null f1}"); }
	@Test public void test_10285() { checkNotSubtype("{{null f2} f1,null f2}","{null f2}"); }
	@Test public void test_10286() { checkNotSubtype("{{null f2} f1,null f2}","{int f1}"); }
	@Test public void test_10287() { checkNotSubtype("{{null f2} f1,null f2}","{int f2}"); }
	@Test public void test_10288() { checkNotSubtype("{{null f2} f1,null f2}","[[void]]"); }
	@Test public void test_10289() { checkNotSubtype("{{null f2} f1,null f2}","[[any]]"); }
	@Test public void test_10290() { checkNotSubtype("{{null f2} f1,null f2}","[[null]]"); }
	@Test public void test_10291() { checkNotSubtype("{{null f2} f1,null f2}","[[int]]"); }
	@Test public void test_10292() { checkNotSubtype("{{null f2} f1,null f2}","[{void f1}]"); }
	@Test public void test_10293() { checkNotSubtype("{{null f2} f1,null f2}","[{void f2}]"); }
	@Test public void test_10294() { checkNotSubtype("{{null f2} f1,null f2}","[{any f1}]"); }
	@Test public void test_10295() { checkNotSubtype("{{null f2} f1,null f2}","[{any f2}]"); }
	@Test public void test_10296() { checkNotSubtype("{{null f2} f1,null f2}","[{null f1}]"); }
	@Test public void test_10297() { checkNotSubtype("{{null f2} f1,null f2}","[{null f2}]"); }
	@Test public void test_10298() { checkNotSubtype("{{null f2} f1,null f2}","[{int f1}]"); }
	@Test public void test_10299() { checkNotSubtype("{{null f2} f1,null f2}","[{int f2}]"); }
	@Test public void test_10300() { checkIsSubtype("{{null f2} f1,null f2}","{void f1,void f2}"); }
	@Test public void test_10301() { checkIsSubtype("{{null f2} f1,null f2}","{void f2,void f3}"); }
	@Test public void test_10302() { checkIsSubtype("{{null f2} f1,null f2}","{void f1,any f2}"); }
	@Test public void test_10303() { checkIsSubtype("{{null f2} f1,null f2}","{void f2,any f3}"); }
	@Test public void test_10304() { checkIsSubtype("{{null f2} f1,null f2}","{void f1,null f2}"); }
	@Test public void test_10305() { checkIsSubtype("{{null f2} f1,null f2}","{void f2,null f3}"); }
	@Test public void test_10306() { checkIsSubtype("{{null f2} f1,null f2}","{void f1,int f2}"); }
	@Test public void test_10307() { checkIsSubtype("{{null f2} f1,null f2}","{void f2,int f3}"); }
	@Test public void test_10308() { checkIsSubtype("{{null f2} f1,null f2}","{any f1,void f2}"); }
	@Test public void test_10309() { checkIsSubtype("{{null f2} f1,null f2}","{any f2,void f3}"); }
	@Test public void test_10310() { checkNotSubtype("{{null f2} f1,null f2}","{any f1,any f2}"); }
	@Test public void test_10311() { checkNotSubtype("{{null f2} f1,null f2}","{any f2,any f3}"); }
	@Test public void test_10312() { checkNotSubtype("{{null f2} f1,null f2}","{any f1,null f2}"); }
	@Test public void test_10313() { checkNotSubtype("{{null f2} f1,null f2}","{any f2,null f3}"); }
	@Test public void test_10314() { checkNotSubtype("{{null f2} f1,null f2}","{any f1,int f2}"); }
	@Test public void test_10315() { checkNotSubtype("{{null f2} f1,null f2}","{any f2,int f3}"); }
	@Test public void test_10316() { checkIsSubtype("{{null f2} f1,null f2}","{null f1,void f2}"); }
	@Test public void test_10317() { checkIsSubtype("{{null f2} f1,null f2}","{null f2,void f3}"); }
	@Test public void test_10318() { checkNotSubtype("{{null f2} f1,null f2}","{null f1,any f2}"); }
	@Test public void test_10319() { checkNotSubtype("{{null f2} f1,null f2}","{null f2,any f3}"); }
	@Test public void test_10320() { checkNotSubtype("{{null f2} f1,null f2}","{null f1,null f2}"); }
	@Test public void test_10321() { checkNotSubtype("{{null f2} f1,null f2}","{null f2,null f3}"); }
	@Test public void test_10322() { checkNotSubtype("{{null f2} f1,null f2}","{null f1,int f2}"); }
	@Test public void test_10323() { checkNotSubtype("{{null f2} f1,null f2}","{null f2,int f3}"); }
	@Test public void test_10324() { checkIsSubtype("{{null f2} f1,null f2}","{int f1,void f2}"); }
	@Test public void test_10325() { checkIsSubtype("{{null f2} f1,null f2}","{int f2,void f3}"); }
	@Test public void test_10326() { checkNotSubtype("{{null f2} f1,null f2}","{int f1,any f2}"); }
	@Test public void test_10327() { checkNotSubtype("{{null f2} f1,null f2}","{int f2,any f3}"); }
	@Test public void test_10328() { checkNotSubtype("{{null f2} f1,null f2}","{int f1,null f2}"); }
	@Test public void test_10329() { checkNotSubtype("{{null f2} f1,null f2}","{int f2,null f3}"); }
	@Test public void test_10330() { checkNotSubtype("{{null f2} f1,null f2}","{int f1,int f2}"); }
	@Test public void test_10331() { checkNotSubtype("{{null f2} f1,null f2}","{int f2,int f3}"); }
	@Test public void test_10332() { checkNotSubtype("{{null f2} f1,null f2}","{[void] f1}"); }
	@Test public void test_10333() { checkNotSubtype("{{null f2} f1,null f2}","{[void] f2}"); }
	@Test public void test_10334() { checkIsSubtype("{{null f2} f1,null f2}","{[void] f1,void f2}"); }
	@Test public void test_10335() { checkIsSubtype("{{null f2} f1,null f2}","{[void] f2,void f3}"); }
	@Test public void test_10336() { checkNotSubtype("{{null f2} f1,null f2}","{[any] f1}"); }
	@Test public void test_10337() { checkNotSubtype("{{null f2} f1,null f2}","{[any] f2}"); }
	@Test public void test_10338() { checkNotSubtype("{{null f2} f1,null f2}","{[any] f1,any f2}"); }
	@Test public void test_10339() { checkNotSubtype("{{null f2} f1,null f2}","{[any] f2,any f3}"); }
	@Test public void test_10340() { checkNotSubtype("{{null f2} f1,null f2}","{[null] f1}"); }
	@Test public void test_10341() { checkNotSubtype("{{null f2} f1,null f2}","{[null] f2}"); }
	@Test public void test_10342() { checkNotSubtype("{{null f2} f1,null f2}","{[null] f1,null f2}"); }
	@Test public void test_10343() { checkNotSubtype("{{null f2} f1,null f2}","{[null] f2,null f3}"); }
	@Test public void test_10344() { checkNotSubtype("{{null f2} f1,null f2}","{[int] f1}"); }
	@Test public void test_10345() { checkNotSubtype("{{null f2} f1,null f2}","{[int] f2}"); }
	@Test public void test_10346() { checkNotSubtype("{{null f2} f1,null f2}","{[int] f1,int f2}"); }
	@Test public void test_10347() { checkNotSubtype("{{null f2} f1,null f2}","{[int] f2,int f3}"); }
	@Test public void test_10348() { checkIsSubtype("{{null f2} f1,null f2}","{{void f1} f1}"); }
	@Test public void test_10349() { checkIsSubtype("{{null f2} f1,null f2}","{{void f2} f1}"); }
	@Test public void test_10350() { checkIsSubtype("{{null f2} f1,null f2}","{{void f1} f2}"); }
	@Test public void test_10351() { checkIsSubtype("{{null f2} f1,null f2}","{{void f2} f2}"); }
	@Test public void test_10352() { checkIsSubtype("{{null f2} f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_10353() { checkIsSubtype("{{null f2} f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_10354() { checkIsSubtype("{{null f2} f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_10355() { checkIsSubtype("{{null f2} f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_10356() { checkNotSubtype("{{null f2} f1,null f2}","{{any f1} f1}"); }
	@Test public void test_10357() { checkNotSubtype("{{null f2} f1,null f2}","{{any f2} f1}"); }
	@Test public void test_10358() { checkNotSubtype("{{null f2} f1,null f2}","{{any f1} f2}"); }
	@Test public void test_10359() { checkNotSubtype("{{null f2} f1,null f2}","{{any f2} f2}"); }
	@Test public void test_10360() { checkNotSubtype("{{null f2} f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_10361() { checkNotSubtype("{{null f2} f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_10362() { checkNotSubtype("{{null f2} f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_10363() { checkNotSubtype("{{null f2} f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_10364() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f1}"); }
	@Test public void test_10365() { checkNotSubtype("{{null f2} f1,null f2}","{{null f2} f1}"); }
	@Test public void test_10366() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f2}"); }
	@Test public void test_10367() { checkNotSubtype("{{null f2} f1,null f2}","{{null f2} f2}"); }
	@Test public void test_10368() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_10369() { checkIsSubtype("{{null f2} f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_10370() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_10371() { checkNotSubtype("{{null f2} f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_10372() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f1}"); }
	@Test public void test_10373() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f1}"); }
	@Test public void test_10374() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f2}"); }
	@Test public void test_10375() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f2}"); }
	@Test public void test_10376() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_10377() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_10378() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_10379() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_10380() { checkNotSubtype("{{null f1} f2,null f3}","any"); }
	@Test public void test_10381() { checkNotSubtype("{{null f1} f2,null f3}","null"); }
	@Test public void test_10382() { checkNotSubtype("{{null f1} f2,null f3}","int"); }
	@Test public void test_10383() { checkNotSubtype("{{null f1} f2,null f3}","[void]"); }
	@Test public void test_10384() { checkNotSubtype("{{null f1} f2,null f3}","[any]"); }
	@Test public void test_10385() { checkNotSubtype("{{null f1} f2,null f3}","[null]"); }
	@Test public void test_10386() { checkNotSubtype("{{null f1} f2,null f3}","[int]"); }
	@Test public void test_10387() { checkIsSubtype("{{null f1} f2,null f3}","{void f1}"); }
	@Test public void test_10388() { checkIsSubtype("{{null f1} f2,null f3}","{void f2}"); }
	@Test public void test_10389() { checkNotSubtype("{{null f1} f2,null f3}","{any f1}"); }
	@Test public void test_10390() { checkNotSubtype("{{null f1} f2,null f3}","{any f2}"); }
	@Test public void test_10391() { checkNotSubtype("{{null f1} f2,null f3}","{null f1}"); }
	@Test public void test_10392() { checkNotSubtype("{{null f1} f2,null f3}","{null f2}"); }
	@Test public void test_10393() { checkNotSubtype("{{null f1} f2,null f3}","{int f1}"); }
	@Test public void test_10394() { checkNotSubtype("{{null f1} f2,null f3}","{int f2}"); }
	@Test public void test_10395() { checkNotSubtype("{{null f1} f2,null f3}","[[void]]"); }
	@Test public void test_10396() { checkNotSubtype("{{null f1} f2,null f3}","[[any]]"); }
	@Test public void test_10397() { checkNotSubtype("{{null f1} f2,null f3}","[[null]]"); }
	@Test public void test_10398() { checkNotSubtype("{{null f1} f2,null f3}","[[int]]"); }
	@Test public void test_10399() { checkNotSubtype("{{null f1} f2,null f3}","[{void f1}]"); }
	@Test public void test_10400() { checkNotSubtype("{{null f1} f2,null f3}","[{void f2}]"); }
	@Test public void test_10401() { checkNotSubtype("{{null f1} f2,null f3}","[{any f1}]"); }
	@Test public void test_10402() { checkNotSubtype("{{null f1} f2,null f3}","[{any f2}]"); }
	@Test public void test_10403() { checkNotSubtype("{{null f1} f2,null f3}","[{null f1}]"); }
	@Test public void test_10404() { checkNotSubtype("{{null f1} f2,null f3}","[{null f2}]"); }
	@Test public void test_10405() { checkNotSubtype("{{null f1} f2,null f3}","[{int f1}]"); }
	@Test public void test_10406() { checkNotSubtype("{{null f1} f2,null f3}","[{int f2}]"); }
	@Test public void test_10407() { checkIsSubtype("{{null f1} f2,null f3}","{void f1,void f2}"); }
	@Test public void test_10408() { checkIsSubtype("{{null f1} f2,null f3}","{void f2,void f3}"); }
	@Test public void test_10409() { checkIsSubtype("{{null f1} f2,null f3}","{void f1,any f2}"); }
	@Test public void test_10410() { checkIsSubtype("{{null f1} f2,null f3}","{void f2,any f3}"); }
	@Test public void test_10411() { checkIsSubtype("{{null f1} f2,null f3}","{void f1,null f2}"); }
	@Test public void test_10412() { checkIsSubtype("{{null f1} f2,null f3}","{void f2,null f3}"); }
	@Test public void test_10413() { checkIsSubtype("{{null f1} f2,null f3}","{void f1,int f2}"); }
	@Test public void test_10414() { checkIsSubtype("{{null f1} f2,null f3}","{void f2,int f3}"); }
	@Test public void test_10415() { checkIsSubtype("{{null f1} f2,null f3}","{any f1,void f2}"); }
	@Test public void test_10416() { checkIsSubtype("{{null f1} f2,null f3}","{any f2,void f3}"); }
	@Test public void test_10417() { checkNotSubtype("{{null f1} f2,null f3}","{any f1,any f2}"); }
	@Test public void test_10418() { checkNotSubtype("{{null f1} f2,null f3}","{any f2,any f3}"); }
	@Test public void test_10419() { checkNotSubtype("{{null f1} f2,null f3}","{any f1,null f2}"); }
	@Test public void test_10420() { checkNotSubtype("{{null f1} f2,null f3}","{any f2,null f3}"); }
	@Test public void test_10421() { checkNotSubtype("{{null f1} f2,null f3}","{any f1,int f2}"); }
	@Test public void test_10422() { checkNotSubtype("{{null f1} f2,null f3}","{any f2,int f3}"); }
	@Test public void test_10423() { checkIsSubtype("{{null f1} f2,null f3}","{null f1,void f2}"); }
	@Test public void test_10424() { checkIsSubtype("{{null f1} f2,null f3}","{null f2,void f3}"); }
	@Test public void test_10425() { checkNotSubtype("{{null f1} f2,null f3}","{null f1,any f2}"); }
	@Test public void test_10426() { checkNotSubtype("{{null f1} f2,null f3}","{null f2,any f3}"); }
	@Test public void test_10427() { checkNotSubtype("{{null f1} f2,null f3}","{null f1,null f2}"); }
	@Test public void test_10428() { checkNotSubtype("{{null f1} f2,null f3}","{null f2,null f3}"); }
	@Test public void test_10429() { checkNotSubtype("{{null f1} f2,null f3}","{null f1,int f2}"); }
	@Test public void test_10430() { checkNotSubtype("{{null f1} f2,null f3}","{null f2,int f3}"); }
	@Test public void test_10431() { checkIsSubtype("{{null f1} f2,null f3}","{int f1,void f2}"); }
	@Test public void test_10432() { checkIsSubtype("{{null f1} f2,null f3}","{int f2,void f3}"); }
	@Test public void test_10433() { checkNotSubtype("{{null f1} f2,null f3}","{int f1,any f2}"); }
	@Test public void test_10434() { checkNotSubtype("{{null f1} f2,null f3}","{int f2,any f3}"); }
	@Test public void test_10435() { checkNotSubtype("{{null f1} f2,null f3}","{int f1,null f2}"); }
	@Test public void test_10436() { checkNotSubtype("{{null f1} f2,null f3}","{int f2,null f3}"); }
	@Test public void test_10437() { checkNotSubtype("{{null f1} f2,null f3}","{int f1,int f2}"); }
	@Test public void test_10438() { checkNotSubtype("{{null f1} f2,null f3}","{int f2,int f3}"); }
	@Test public void test_10439() { checkNotSubtype("{{null f1} f2,null f3}","{[void] f1}"); }
	@Test public void test_10440() { checkNotSubtype("{{null f1} f2,null f3}","{[void] f2}"); }
	@Test public void test_10441() { checkIsSubtype("{{null f1} f2,null f3}","{[void] f1,void f2}"); }
	@Test public void test_10442() { checkIsSubtype("{{null f1} f2,null f3}","{[void] f2,void f3}"); }
	@Test public void test_10443() { checkNotSubtype("{{null f1} f2,null f3}","{[any] f1}"); }
	@Test public void test_10444() { checkNotSubtype("{{null f1} f2,null f3}","{[any] f2}"); }
	@Test public void test_10445() { checkNotSubtype("{{null f1} f2,null f3}","{[any] f1,any f2}"); }
	@Test public void test_10446() { checkNotSubtype("{{null f1} f2,null f3}","{[any] f2,any f3}"); }
	@Test public void test_10447() { checkNotSubtype("{{null f1} f2,null f3}","{[null] f1}"); }
	@Test public void test_10448() { checkNotSubtype("{{null f1} f2,null f3}","{[null] f2}"); }
	@Test public void test_10449() { checkNotSubtype("{{null f1} f2,null f3}","{[null] f1,null f2}"); }
	@Test public void test_10450() { checkNotSubtype("{{null f1} f2,null f3}","{[null] f2,null f3}"); }
	@Test public void test_10451() { checkNotSubtype("{{null f1} f2,null f3}","{[int] f1}"); }
	@Test public void test_10452() { checkNotSubtype("{{null f1} f2,null f3}","{[int] f2}"); }
	@Test public void test_10453() { checkNotSubtype("{{null f1} f2,null f3}","{[int] f1,int f2}"); }
	@Test public void test_10454() { checkNotSubtype("{{null f1} f2,null f3}","{[int] f2,int f3}"); }
	@Test public void test_10455() { checkIsSubtype("{{null f1} f2,null f3}","{{void f1} f1}"); }
	@Test public void test_10456() { checkIsSubtype("{{null f1} f2,null f3}","{{void f2} f1}"); }
	@Test public void test_10457() { checkIsSubtype("{{null f1} f2,null f3}","{{void f1} f2}"); }
	@Test public void test_10458() { checkIsSubtype("{{null f1} f2,null f3}","{{void f2} f2}"); }
	@Test public void test_10459() { checkIsSubtype("{{null f1} f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_10460() { checkIsSubtype("{{null f1} f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_10461() { checkIsSubtype("{{null f1} f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_10462() { checkIsSubtype("{{null f1} f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_10463() { checkNotSubtype("{{null f1} f2,null f3}","{{any f1} f1}"); }
	@Test public void test_10464() { checkNotSubtype("{{null f1} f2,null f3}","{{any f2} f1}"); }
	@Test public void test_10465() { checkNotSubtype("{{null f1} f2,null f3}","{{any f1} f2}"); }
	@Test public void test_10466() { checkNotSubtype("{{null f1} f2,null f3}","{{any f2} f2}"); }
	@Test public void test_10467() { checkNotSubtype("{{null f1} f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_10468() { checkNotSubtype("{{null f1} f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_10469() { checkNotSubtype("{{null f1} f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_10470() { checkNotSubtype("{{null f1} f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_10471() { checkNotSubtype("{{null f1} f2,null f3}","{{null f1} f1}"); }
	@Test public void test_10472() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f1}"); }
	@Test public void test_10473() { checkNotSubtype("{{null f1} f2,null f3}","{{null f1} f2}"); }
	@Test public void test_10474() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f2}"); }
	@Test public void test_10475() { checkNotSubtype("{{null f1} f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_10476() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_10477() { checkIsSubtype("{{null f1} f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_10478() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_10479() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f1}"); }
	@Test public void test_10480() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f1}"); }
	@Test public void test_10481() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f2}"); }
	@Test public void test_10482() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f2}"); }
	@Test public void test_10483() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_10484() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_10485() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_10486() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_10487() { checkNotSubtype("{{null f2} f2,null f3}","any"); }
	@Test public void test_10488() { checkNotSubtype("{{null f2} f2,null f3}","null"); }
	@Test public void test_10489() { checkNotSubtype("{{null f2} f2,null f3}","int"); }
	@Test public void test_10490() { checkNotSubtype("{{null f2} f2,null f3}","[void]"); }
	@Test public void test_10491() { checkNotSubtype("{{null f2} f2,null f3}","[any]"); }
	@Test public void test_10492() { checkNotSubtype("{{null f2} f2,null f3}","[null]"); }
	@Test public void test_10493() { checkNotSubtype("{{null f2} f2,null f3}","[int]"); }
	@Test public void test_10494() { checkIsSubtype("{{null f2} f2,null f3}","{void f1}"); }
	@Test public void test_10495() { checkIsSubtype("{{null f2} f2,null f3}","{void f2}"); }
	@Test public void test_10496() { checkNotSubtype("{{null f2} f2,null f3}","{any f1}"); }
	@Test public void test_10497() { checkNotSubtype("{{null f2} f2,null f3}","{any f2}"); }
	@Test public void test_10498() { checkNotSubtype("{{null f2} f2,null f3}","{null f1}"); }
	@Test public void test_10499() { checkNotSubtype("{{null f2} f2,null f3}","{null f2}"); }
	@Test public void test_10500() { checkNotSubtype("{{null f2} f2,null f3}","{int f1}"); }
	@Test public void test_10501() { checkNotSubtype("{{null f2} f2,null f3}","{int f2}"); }
	@Test public void test_10502() { checkNotSubtype("{{null f2} f2,null f3}","[[void]]"); }
	@Test public void test_10503() { checkNotSubtype("{{null f2} f2,null f3}","[[any]]"); }
	@Test public void test_10504() { checkNotSubtype("{{null f2} f2,null f3}","[[null]]"); }
	@Test public void test_10505() { checkNotSubtype("{{null f2} f2,null f3}","[[int]]"); }
	@Test public void test_10506() { checkNotSubtype("{{null f2} f2,null f3}","[{void f1}]"); }
	@Test public void test_10507() { checkNotSubtype("{{null f2} f2,null f3}","[{void f2}]"); }
	@Test public void test_10508() { checkNotSubtype("{{null f2} f2,null f3}","[{any f1}]"); }
	@Test public void test_10509() { checkNotSubtype("{{null f2} f2,null f3}","[{any f2}]"); }
	@Test public void test_10510() { checkNotSubtype("{{null f2} f2,null f3}","[{null f1}]"); }
	@Test public void test_10511() { checkNotSubtype("{{null f2} f2,null f3}","[{null f2}]"); }
	@Test public void test_10512() { checkNotSubtype("{{null f2} f2,null f3}","[{int f1}]"); }
	@Test public void test_10513() { checkNotSubtype("{{null f2} f2,null f3}","[{int f2}]"); }
	@Test public void test_10514() { checkIsSubtype("{{null f2} f2,null f3}","{void f1,void f2}"); }
	@Test public void test_10515() { checkIsSubtype("{{null f2} f2,null f3}","{void f2,void f3}"); }
	@Test public void test_10516() { checkIsSubtype("{{null f2} f2,null f3}","{void f1,any f2}"); }
	@Test public void test_10517() { checkIsSubtype("{{null f2} f2,null f3}","{void f2,any f3}"); }
	@Test public void test_10518() { checkIsSubtype("{{null f2} f2,null f3}","{void f1,null f2}"); }
	@Test public void test_10519() { checkIsSubtype("{{null f2} f2,null f3}","{void f2,null f3}"); }
	@Test public void test_10520() { checkIsSubtype("{{null f2} f2,null f3}","{void f1,int f2}"); }
	@Test public void test_10521() { checkIsSubtype("{{null f2} f2,null f3}","{void f2,int f3}"); }
	@Test public void test_10522() { checkIsSubtype("{{null f2} f2,null f3}","{any f1,void f2}"); }
	@Test public void test_10523() { checkIsSubtype("{{null f2} f2,null f3}","{any f2,void f3}"); }
	@Test public void test_10524() { checkNotSubtype("{{null f2} f2,null f3}","{any f1,any f2}"); }
	@Test public void test_10525() { checkNotSubtype("{{null f2} f2,null f3}","{any f2,any f3}"); }
	@Test public void test_10526() { checkNotSubtype("{{null f2} f2,null f3}","{any f1,null f2}"); }
	@Test public void test_10527() { checkNotSubtype("{{null f2} f2,null f3}","{any f2,null f3}"); }
	@Test public void test_10528() { checkNotSubtype("{{null f2} f2,null f3}","{any f1,int f2}"); }
	@Test public void test_10529() { checkNotSubtype("{{null f2} f2,null f3}","{any f2,int f3}"); }
	@Test public void test_10530() { checkIsSubtype("{{null f2} f2,null f3}","{null f1,void f2}"); }
	@Test public void test_10531() { checkIsSubtype("{{null f2} f2,null f3}","{null f2,void f3}"); }
	@Test public void test_10532() { checkNotSubtype("{{null f2} f2,null f3}","{null f1,any f2}"); }
	@Test public void test_10533() { checkNotSubtype("{{null f2} f2,null f3}","{null f2,any f3}"); }
	@Test public void test_10534() { checkNotSubtype("{{null f2} f2,null f3}","{null f1,null f2}"); }
	@Test public void test_10535() { checkNotSubtype("{{null f2} f2,null f3}","{null f2,null f3}"); }
	@Test public void test_10536() { checkNotSubtype("{{null f2} f2,null f3}","{null f1,int f2}"); }
	@Test public void test_10537() { checkNotSubtype("{{null f2} f2,null f3}","{null f2,int f3}"); }
	@Test public void test_10538() { checkIsSubtype("{{null f2} f2,null f3}","{int f1,void f2}"); }
	@Test public void test_10539() { checkIsSubtype("{{null f2} f2,null f3}","{int f2,void f3}"); }
	@Test public void test_10540() { checkNotSubtype("{{null f2} f2,null f3}","{int f1,any f2}"); }
	@Test public void test_10541() { checkNotSubtype("{{null f2} f2,null f3}","{int f2,any f3}"); }
	@Test public void test_10542() { checkNotSubtype("{{null f2} f2,null f3}","{int f1,null f2}"); }
	@Test public void test_10543() { checkNotSubtype("{{null f2} f2,null f3}","{int f2,null f3}"); }
	@Test public void test_10544() { checkNotSubtype("{{null f2} f2,null f3}","{int f1,int f2}"); }
	@Test public void test_10545() { checkNotSubtype("{{null f2} f2,null f3}","{int f2,int f3}"); }
	@Test public void test_10546() { checkNotSubtype("{{null f2} f2,null f3}","{[void] f1}"); }
	@Test public void test_10547() { checkNotSubtype("{{null f2} f2,null f3}","{[void] f2}"); }
	@Test public void test_10548() { checkIsSubtype("{{null f2} f2,null f3}","{[void] f1,void f2}"); }
	@Test public void test_10549() { checkIsSubtype("{{null f2} f2,null f3}","{[void] f2,void f3}"); }
	@Test public void test_10550() { checkNotSubtype("{{null f2} f2,null f3}","{[any] f1}"); }
	@Test public void test_10551() { checkNotSubtype("{{null f2} f2,null f3}","{[any] f2}"); }
	@Test public void test_10552() { checkNotSubtype("{{null f2} f2,null f3}","{[any] f1,any f2}"); }
	@Test public void test_10553() { checkNotSubtype("{{null f2} f2,null f3}","{[any] f2,any f3}"); }
	@Test public void test_10554() { checkNotSubtype("{{null f2} f2,null f3}","{[null] f1}"); }
	@Test public void test_10555() { checkNotSubtype("{{null f2} f2,null f3}","{[null] f2}"); }
	@Test public void test_10556() { checkNotSubtype("{{null f2} f2,null f3}","{[null] f1,null f2}"); }
	@Test public void test_10557() { checkNotSubtype("{{null f2} f2,null f3}","{[null] f2,null f3}"); }
	@Test public void test_10558() { checkNotSubtype("{{null f2} f2,null f3}","{[int] f1}"); }
	@Test public void test_10559() { checkNotSubtype("{{null f2} f2,null f3}","{[int] f2}"); }
	@Test public void test_10560() { checkNotSubtype("{{null f2} f2,null f3}","{[int] f1,int f2}"); }
	@Test public void test_10561() { checkNotSubtype("{{null f2} f2,null f3}","{[int] f2,int f3}"); }
	@Test public void test_10562() { checkIsSubtype("{{null f2} f2,null f3}","{{void f1} f1}"); }
	@Test public void test_10563() { checkIsSubtype("{{null f2} f2,null f3}","{{void f2} f1}"); }
	@Test public void test_10564() { checkIsSubtype("{{null f2} f2,null f3}","{{void f1} f2}"); }
	@Test public void test_10565() { checkIsSubtype("{{null f2} f2,null f3}","{{void f2} f2}"); }
	@Test public void test_10566() { checkIsSubtype("{{null f2} f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_10567() { checkIsSubtype("{{null f2} f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_10568() { checkIsSubtype("{{null f2} f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_10569() { checkIsSubtype("{{null f2} f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_10570() { checkNotSubtype("{{null f2} f2,null f3}","{{any f1} f1}"); }
	@Test public void test_10571() { checkNotSubtype("{{null f2} f2,null f3}","{{any f2} f1}"); }
	@Test public void test_10572() { checkNotSubtype("{{null f2} f2,null f3}","{{any f1} f2}"); }
	@Test public void test_10573() { checkNotSubtype("{{null f2} f2,null f3}","{{any f2} f2}"); }
	@Test public void test_10574() { checkNotSubtype("{{null f2} f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_10575() { checkNotSubtype("{{null f2} f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_10576() { checkNotSubtype("{{null f2} f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_10577() { checkNotSubtype("{{null f2} f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_10578() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f1}"); }
	@Test public void test_10579() { checkNotSubtype("{{null f2} f2,null f3}","{{null f2} f1}"); }
	@Test public void test_10580() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f2}"); }
	@Test public void test_10581() { checkNotSubtype("{{null f2} f2,null f3}","{{null f2} f2}"); }
	@Test public void test_10582() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_10583() { checkNotSubtype("{{null f2} f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_10584() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_10585() { checkIsSubtype("{{null f2} f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_10586() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f1}"); }
	@Test public void test_10587() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f1}"); }
	@Test public void test_10588() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f2}"); }
	@Test public void test_10589() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f2}"); }
	@Test public void test_10590() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_10591() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_10592() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_10593() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_10594() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_10595() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_10596() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_10597() { checkNotSubtype("{{int f1} f1}","[void]"); }
	@Test public void test_10598() { checkNotSubtype("{{int f1} f1}","[any]"); }
	@Test public void test_10599() { checkNotSubtype("{{int f1} f1}","[null]"); }
	@Test public void test_10600() { checkNotSubtype("{{int f1} f1}","[int]"); }
	@Test public void test_10601() { checkIsSubtype("{{int f1} f1}","{void f1}"); }
	@Test public void test_10602() { checkIsSubtype("{{int f1} f1}","{void f2}"); }
	@Test public void test_10603() { checkNotSubtype("{{int f1} f1}","{any f1}"); }
	@Test public void test_10604() { checkNotSubtype("{{int f1} f1}","{any f2}"); }
	@Test public void test_10605() { checkNotSubtype("{{int f1} f1}","{null f1}"); }
	@Test public void test_10606() { checkNotSubtype("{{int f1} f1}","{null f2}"); }
	@Test public void test_10607() { checkNotSubtype("{{int f1} f1}","{int f1}"); }
	@Test public void test_10608() { checkNotSubtype("{{int f1} f1}","{int f2}"); }
	@Test public void test_10609() { checkNotSubtype("{{int f1} f1}","[[void]]"); }
	@Test public void test_10610() { checkNotSubtype("{{int f1} f1}","[[any]]"); }
	@Test public void test_10611() { checkNotSubtype("{{int f1} f1}","[[null]]"); }
	@Test public void test_10612() { checkNotSubtype("{{int f1} f1}","[[int]]"); }
	@Test public void test_10613() { checkNotSubtype("{{int f1} f1}","[{void f1}]"); }
	@Test public void test_10614() { checkNotSubtype("{{int f1} f1}","[{void f2}]"); }
	@Test public void test_10615() { checkNotSubtype("{{int f1} f1}","[{any f1}]"); }
	@Test public void test_10616() { checkNotSubtype("{{int f1} f1}","[{any f2}]"); }
	@Test public void test_10617() { checkNotSubtype("{{int f1} f1}","[{null f1}]"); }
	@Test public void test_10618() { checkNotSubtype("{{int f1} f1}","[{null f2}]"); }
	@Test public void test_10619() { checkNotSubtype("{{int f1} f1}","[{int f1}]"); }
	@Test public void test_10620() { checkNotSubtype("{{int f1} f1}","[{int f2}]"); }
	@Test public void test_10621() { checkIsSubtype("{{int f1} f1}","{void f1,void f2}"); }
	@Test public void test_10622() { checkIsSubtype("{{int f1} f1}","{void f2,void f3}"); }
	@Test public void test_10623() { checkIsSubtype("{{int f1} f1}","{void f1,any f2}"); }
	@Test public void test_10624() { checkIsSubtype("{{int f1} f1}","{void f2,any f3}"); }
	@Test public void test_10625() { checkIsSubtype("{{int f1} f1}","{void f1,null f2}"); }
	@Test public void test_10626() { checkIsSubtype("{{int f1} f1}","{void f2,null f3}"); }
	@Test public void test_10627() { checkIsSubtype("{{int f1} f1}","{void f1,int f2}"); }
	@Test public void test_10628() { checkIsSubtype("{{int f1} f1}","{void f2,int f3}"); }
	@Test public void test_10629() { checkIsSubtype("{{int f1} f1}","{any f1,void f2}"); }
	@Test public void test_10630() { checkIsSubtype("{{int f1} f1}","{any f2,void f3}"); }
	@Test public void test_10631() { checkNotSubtype("{{int f1} f1}","{any f1,any f2}"); }
	@Test public void test_10632() { checkNotSubtype("{{int f1} f1}","{any f2,any f3}"); }
	@Test public void test_10633() { checkNotSubtype("{{int f1} f1}","{any f1,null f2}"); }
	@Test public void test_10634() { checkNotSubtype("{{int f1} f1}","{any f2,null f3}"); }
	@Test public void test_10635() { checkNotSubtype("{{int f1} f1}","{any f1,int f2}"); }
	@Test public void test_10636() { checkNotSubtype("{{int f1} f1}","{any f2,int f3}"); }
	@Test public void test_10637() { checkIsSubtype("{{int f1} f1}","{null f1,void f2}"); }
	@Test public void test_10638() { checkIsSubtype("{{int f1} f1}","{null f2,void f3}"); }
	@Test public void test_10639() { checkNotSubtype("{{int f1} f1}","{null f1,any f2}"); }
	@Test public void test_10640() { checkNotSubtype("{{int f1} f1}","{null f2,any f3}"); }
	@Test public void test_10641() { checkNotSubtype("{{int f1} f1}","{null f1,null f2}"); }
	@Test public void test_10642() { checkNotSubtype("{{int f1} f1}","{null f2,null f3}"); }
	@Test public void test_10643() { checkNotSubtype("{{int f1} f1}","{null f1,int f2}"); }
	@Test public void test_10644() { checkNotSubtype("{{int f1} f1}","{null f2,int f3}"); }
	@Test public void test_10645() { checkIsSubtype("{{int f1} f1}","{int f1,void f2}"); }
	@Test public void test_10646() { checkIsSubtype("{{int f1} f1}","{int f2,void f3}"); }
	@Test public void test_10647() { checkNotSubtype("{{int f1} f1}","{int f1,any f2}"); }
	@Test public void test_10648() { checkNotSubtype("{{int f1} f1}","{int f2,any f3}"); }
	@Test public void test_10649() { checkNotSubtype("{{int f1} f1}","{int f1,null f2}"); }
	@Test public void test_10650() { checkNotSubtype("{{int f1} f1}","{int f2,null f3}"); }
	@Test public void test_10651() { checkNotSubtype("{{int f1} f1}","{int f1,int f2}"); }
	@Test public void test_10652() { checkNotSubtype("{{int f1} f1}","{int f2,int f3}"); }
	@Test public void test_10653() { checkNotSubtype("{{int f1} f1}","{[void] f1}"); }
	@Test public void test_10654() { checkNotSubtype("{{int f1} f1}","{[void] f2}"); }
	@Test public void test_10655() { checkIsSubtype("{{int f1} f1}","{[void] f1,void f2}"); }
	@Test public void test_10656() { checkIsSubtype("{{int f1} f1}","{[void] f2,void f3}"); }
	@Test public void test_10657() { checkNotSubtype("{{int f1} f1}","{[any] f1}"); }
	@Test public void test_10658() { checkNotSubtype("{{int f1} f1}","{[any] f2}"); }
	@Test public void test_10659() { checkNotSubtype("{{int f1} f1}","{[any] f1,any f2}"); }
	@Test public void test_10660() { checkNotSubtype("{{int f1} f1}","{[any] f2,any f3}"); }
	@Test public void test_10661() { checkNotSubtype("{{int f1} f1}","{[null] f1}"); }
	@Test public void test_10662() { checkNotSubtype("{{int f1} f1}","{[null] f2}"); }
	@Test public void test_10663() { checkNotSubtype("{{int f1} f1}","{[null] f1,null f2}"); }
	@Test public void test_10664() { checkNotSubtype("{{int f1} f1}","{[null] f2,null f3}"); }
	@Test public void test_10665() { checkNotSubtype("{{int f1} f1}","{[int] f1}"); }
	@Test public void test_10666() { checkNotSubtype("{{int f1} f1}","{[int] f2}"); }
	@Test public void test_10667() { checkNotSubtype("{{int f1} f1}","{[int] f1,int f2}"); }
	@Test public void test_10668() { checkNotSubtype("{{int f1} f1}","{[int] f2,int f3}"); }
	@Test public void test_10669() { checkIsSubtype("{{int f1} f1}","{{void f1} f1}"); }
	@Test public void test_10670() { checkIsSubtype("{{int f1} f1}","{{void f2} f1}"); }
	@Test public void test_10671() { checkIsSubtype("{{int f1} f1}","{{void f1} f2}"); }
	@Test public void test_10672() { checkIsSubtype("{{int f1} f1}","{{void f2} f2}"); }
	@Test public void test_10673() { checkIsSubtype("{{int f1} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_10674() { checkIsSubtype("{{int f1} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_10675() { checkIsSubtype("{{int f1} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_10676() { checkIsSubtype("{{int f1} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_10677() { checkNotSubtype("{{int f1} f1}","{{any f1} f1}"); }
	@Test public void test_10678() { checkNotSubtype("{{int f1} f1}","{{any f2} f1}"); }
	@Test public void test_10679() { checkNotSubtype("{{int f1} f1}","{{any f1} f2}"); }
	@Test public void test_10680() { checkNotSubtype("{{int f1} f1}","{{any f2} f2}"); }
	@Test public void test_10681() { checkNotSubtype("{{int f1} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_10682() { checkNotSubtype("{{int f1} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_10683() { checkNotSubtype("{{int f1} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_10684() { checkNotSubtype("{{int f1} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_10685() { checkNotSubtype("{{int f1} f1}","{{null f1} f1}"); }
	@Test public void test_10686() { checkNotSubtype("{{int f1} f1}","{{null f2} f1}"); }
	@Test public void test_10687() { checkNotSubtype("{{int f1} f1}","{{null f1} f2}"); }
	@Test public void test_10688() { checkNotSubtype("{{int f1} f1}","{{null f2} f2}"); }
	@Test public void test_10689() { checkNotSubtype("{{int f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_10690() { checkNotSubtype("{{int f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_10691() { checkNotSubtype("{{int f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_10692() { checkNotSubtype("{{int f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_10693() { checkIsSubtype("{{int f1} f1}","{{int f1} f1}"); }
	@Test public void test_10694() { checkNotSubtype("{{int f1} f1}","{{int f2} f1}"); }
	@Test public void test_10695() { checkNotSubtype("{{int f1} f1}","{{int f1} f2}"); }
	@Test public void test_10696() { checkNotSubtype("{{int f1} f1}","{{int f2} f2}"); }
	@Test public void test_10697() { checkNotSubtype("{{int f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_10698() { checkNotSubtype("{{int f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_10699() { checkNotSubtype("{{int f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_10700() { checkNotSubtype("{{int f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_10701() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_10702() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_10703() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_10704() { checkNotSubtype("{{int f2} f1}","[void]"); }
	@Test public void test_10705() { checkNotSubtype("{{int f2} f1}","[any]"); }
	@Test public void test_10706() { checkNotSubtype("{{int f2} f1}","[null]"); }
	@Test public void test_10707() { checkNotSubtype("{{int f2} f1}","[int]"); }
	@Test public void test_10708() { checkIsSubtype("{{int f2} f1}","{void f1}"); }
	@Test public void test_10709() { checkIsSubtype("{{int f2} f1}","{void f2}"); }
	@Test public void test_10710() { checkNotSubtype("{{int f2} f1}","{any f1}"); }
	@Test public void test_10711() { checkNotSubtype("{{int f2} f1}","{any f2}"); }
	@Test public void test_10712() { checkNotSubtype("{{int f2} f1}","{null f1}"); }
	@Test public void test_10713() { checkNotSubtype("{{int f2} f1}","{null f2}"); }
	@Test public void test_10714() { checkNotSubtype("{{int f2} f1}","{int f1}"); }
	@Test public void test_10715() { checkNotSubtype("{{int f2} f1}","{int f2}"); }
	@Test public void test_10716() { checkNotSubtype("{{int f2} f1}","[[void]]"); }
	@Test public void test_10717() { checkNotSubtype("{{int f2} f1}","[[any]]"); }
	@Test public void test_10718() { checkNotSubtype("{{int f2} f1}","[[null]]"); }
	@Test public void test_10719() { checkNotSubtype("{{int f2} f1}","[[int]]"); }
	@Test public void test_10720() { checkNotSubtype("{{int f2} f1}","[{void f1}]"); }
	@Test public void test_10721() { checkNotSubtype("{{int f2} f1}","[{void f2}]"); }
	@Test public void test_10722() { checkNotSubtype("{{int f2} f1}","[{any f1}]"); }
	@Test public void test_10723() { checkNotSubtype("{{int f2} f1}","[{any f2}]"); }
	@Test public void test_10724() { checkNotSubtype("{{int f2} f1}","[{null f1}]"); }
	@Test public void test_10725() { checkNotSubtype("{{int f2} f1}","[{null f2}]"); }
	@Test public void test_10726() { checkNotSubtype("{{int f2} f1}","[{int f1}]"); }
	@Test public void test_10727() { checkNotSubtype("{{int f2} f1}","[{int f2}]"); }
	@Test public void test_10728() { checkIsSubtype("{{int f2} f1}","{void f1,void f2}"); }
	@Test public void test_10729() { checkIsSubtype("{{int f2} f1}","{void f2,void f3}"); }
	@Test public void test_10730() { checkIsSubtype("{{int f2} f1}","{void f1,any f2}"); }
	@Test public void test_10731() { checkIsSubtype("{{int f2} f1}","{void f2,any f3}"); }
	@Test public void test_10732() { checkIsSubtype("{{int f2} f1}","{void f1,null f2}"); }
	@Test public void test_10733() { checkIsSubtype("{{int f2} f1}","{void f2,null f3}"); }
	@Test public void test_10734() { checkIsSubtype("{{int f2} f1}","{void f1,int f2}"); }
	@Test public void test_10735() { checkIsSubtype("{{int f2} f1}","{void f2,int f3}"); }
	@Test public void test_10736() { checkIsSubtype("{{int f2} f1}","{any f1,void f2}"); }
	@Test public void test_10737() { checkIsSubtype("{{int f2} f1}","{any f2,void f3}"); }
	@Test public void test_10738() { checkNotSubtype("{{int f2} f1}","{any f1,any f2}"); }
	@Test public void test_10739() { checkNotSubtype("{{int f2} f1}","{any f2,any f3}"); }
	@Test public void test_10740() { checkNotSubtype("{{int f2} f1}","{any f1,null f2}"); }
	@Test public void test_10741() { checkNotSubtype("{{int f2} f1}","{any f2,null f3}"); }
	@Test public void test_10742() { checkNotSubtype("{{int f2} f1}","{any f1,int f2}"); }
	@Test public void test_10743() { checkNotSubtype("{{int f2} f1}","{any f2,int f3}"); }
	@Test public void test_10744() { checkIsSubtype("{{int f2} f1}","{null f1,void f2}"); }
	@Test public void test_10745() { checkIsSubtype("{{int f2} f1}","{null f2,void f3}"); }
	@Test public void test_10746() { checkNotSubtype("{{int f2} f1}","{null f1,any f2}"); }
	@Test public void test_10747() { checkNotSubtype("{{int f2} f1}","{null f2,any f3}"); }
	@Test public void test_10748() { checkNotSubtype("{{int f2} f1}","{null f1,null f2}"); }
	@Test public void test_10749() { checkNotSubtype("{{int f2} f1}","{null f2,null f3}"); }
	@Test public void test_10750() { checkNotSubtype("{{int f2} f1}","{null f1,int f2}"); }
	@Test public void test_10751() { checkNotSubtype("{{int f2} f1}","{null f2,int f3}"); }
	@Test public void test_10752() { checkIsSubtype("{{int f2} f1}","{int f1,void f2}"); }
	@Test public void test_10753() { checkIsSubtype("{{int f2} f1}","{int f2,void f3}"); }
	@Test public void test_10754() { checkNotSubtype("{{int f2} f1}","{int f1,any f2}"); }
	@Test public void test_10755() { checkNotSubtype("{{int f2} f1}","{int f2,any f3}"); }
	@Test public void test_10756() { checkNotSubtype("{{int f2} f1}","{int f1,null f2}"); }
	@Test public void test_10757() { checkNotSubtype("{{int f2} f1}","{int f2,null f3}"); }
	@Test public void test_10758() { checkNotSubtype("{{int f2} f1}","{int f1,int f2}"); }
	@Test public void test_10759() { checkNotSubtype("{{int f2} f1}","{int f2,int f3}"); }
	@Test public void test_10760() { checkNotSubtype("{{int f2} f1}","{[void] f1}"); }
	@Test public void test_10761() { checkNotSubtype("{{int f2} f1}","{[void] f2}"); }
	@Test public void test_10762() { checkIsSubtype("{{int f2} f1}","{[void] f1,void f2}"); }
	@Test public void test_10763() { checkIsSubtype("{{int f2} f1}","{[void] f2,void f3}"); }
	@Test public void test_10764() { checkNotSubtype("{{int f2} f1}","{[any] f1}"); }
	@Test public void test_10765() { checkNotSubtype("{{int f2} f1}","{[any] f2}"); }
	@Test public void test_10766() { checkNotSubtype("{{int f2} f1}","{[any] f1,any f2}"); }
	@Test public void test_10767() { checkNotSubtype("{{int f2} f1}","{[any] f2,any f3}"); }
	@Test public void test_10768() { checkNotSubtype("{{int f2} f1}","{[null] f1}"); }
	@Test public void test_10769() { checkNotSubtype("{{int f2} f1}","{[null] f2}"); }
	@Test public void test_10770() { checkNotSubtype("{{int f2} f1}","{[null] f1,null f2}"); }
	@Test public void test_10771() { checkNotSubtype("{{int f2} f1}","{[null] f2,null f3}"); }
	@Test public void test_10772() { checkNotSubtype("{{int f2} f1}","{[int] f1}"); }
	@Test public void test_10773() { checkNotSubtype("{{int f2} f1}","{[int] f2}"); }
	@Test public void test_10774() { checkNotSubtype("{{int f2} f1}","{[int] f1,int f2}"); }
	@Test public void test_10775() { checkNotSubtype("{{int f2} f1}","{[int] f2,int f3}"); }
	@Test public void test_10776() { checkIsSubtype("{{int f2} f1}","{{void f1} f1}"); }
	@Test public void test_10777() { checkIsSubtype("{{int f2} f1}","{{void f2} f1}"); }
	@Test public void test_10778() { checkIsSubtype("{{int f2} f1}","{{void f1} f2}"); }
	@Test public void test_10779() { checkIsSubtype("{{int f2} f1}","{{void f2} f2}"); }
	@Test public void test_10780() { checkIsSubtype("{{int f2} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_10781() { checkIsSubtype("{{int f2} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_10782() { checkIsSubtype("{{int f2} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_10783() { checkIsSubtype("{{int f2} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_10784() { checkNotSubtype("{{int f2} f1}","{{any f1} f1}"); }
	@Test public void test_10785() { checkNotSubtype("{{int f2} f1}","{{any f2} f1}"); }
	@Test public void test_10786() { checkNotSubtype("{{int f2} f1}","{{any f1} f2}"); }
	@Test public void test_10787() { checkNotSubtype("{{int f2} f1}","{{any f2} f2}"); }
	@Test public void test_10788() { checkNotSubtype("{{int f2} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_10789() { checkNotSubtype("{{int f2} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_10790() { checkNotSubtype("{{int f2} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_10791() { checkNotSubtype("{{int f2} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_10792() { checkNotSubtype("{{int f2} f1}","{{null f1} f1}"); }
	@Test public void test_10793() { checkNotSubtype("{{int f2} f1}","{{null f2} f1}"); }
	@Test public void test_10794() { checkNotSubtype("{{int f2} f1}","{{null f1} f2}"); }
	@Test public void test_10795() { checkNotSubtype("{{int f2} f1}","{{null f2} f2}"); }
	@Test public void test_10796() { checkNotSubtype("{{int f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_10797() { checkNotSubtype("{{int f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_10798() { checkNotSubtype("{{int f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_10799() { checkNotSubtype("{{int f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_10800() { checkNotSubtype("{{int f2} f1}","{{int f1} f1}"); }
	@Test public void test_10801() { checkIsSubtype("{{int f2} f1}","{{int f2} f1}"); }
	@Test public void test_10802() { checkNotSubtype("{{int f2} f1}","{{int f1} f2}"); }
	@Test public void test_10803() { checkNotSubtype("{{int f2} f1}","{{int f2} f2}"); }
	@Test public void test_10804() { checkNotSubtype("{{int f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_10805() { checkNotSubtype("{{int f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_10806() { checkNotSubtype("{{int f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_10807() { checkNotSubtype("{{int f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_10808() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_10809() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_10810() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_10811() { checkNotSubtype("{{int f1} f2}","[void]"); }
	@Test public void test_10812() { checkNotSubtype("{{int f1} f2}","[any]"); }
	@Test public void test_10813() { checkNotSubtype("{{int f1} f2}","[null]"); }
	@Test public void test_10814() { checkNotSubtype("{{int f1} f2}","[int]"); }
	@Test public void test_10815() { checkIsSubtype("{{int f1} f2}","{void f1}"); }
	@Test public void test_10816() { checkIsSubtype("{{int f1} f2}","{void f2}"); }
	@Test public void test_10817() { checkNotSubtype("{{int f1} f2}","{any f1}"); }
	@Test public void test_10818() { checkNotSubtype("{{int f1} f2}","{any f2}"); }
	@Test public void test_10819() { checkNotSubtype("{{int f1} f2}","{null f1}"); }
	@Test public void test_10820() { checkNotSubtype("{{int f1} f2}","{null f2}"); }
	@Test public void test_10821() { checkNotSubtype("{{int f1} f2}","{int f1}"); }
	@Test public void test_10822() { checkNotSubtype("{{int f1} f2}","{int f2}"); }
	@Test public void test_10823() { checkNotSubtype("{{int f1} f2}","[[void]]"); }
	@Test public void test_10824() { checkNotSubtype("{{int f1} f2}","[[any]]"); }
	@Test public void test_10825() { checkNotSubtype("{{int f1} f2}","[[null]]"); }
	@Test public void test_10826() { checkNotSubtype("{{int f1} f2}","[[int]]"); }
	@Test public void test_10827() { checkNotSubtype("{{int f1} f2}","[{void f1}]"); }
	@Test public void test_10828() { checkNotSubtype("{{int f1} f2}","[{void f2}]"); }
	@Test public void test_10829() { checkNotSubtype("{{int f1} f2}","[{any f1}]"); }
	@Test public void test_10830() { checkNotSubtype("{{int f1} f2}","[{any f2}]"); }
	@Test public void test_10831() { checkNotSubtype("{{int f1} f2}","[{null f1}]"); }
	@Test public void test_10832() { checkNotSubtype("{{int f1} f2}","[{null f2}]"); }
	@Test public void test_10833() { checkNotSubtype("{{int f1} f2}","[{int f1}]"); }
	@Test public void test_10834() { checkNotSubtype("{{int f1} f2}","[{int f2}]"); }
	@Test public void test_10835() { checkIsSubtype("{{int f1} f2}","{void f1,void f2}"); }
	@Test public void test_10836() { checkIsSubtype("{{int f1} f2}","{void f2,void f3}"); }
	@Test public void test_10837() { checkIsSubtype("{{int f1} f2}","{void f1,any f2}"); }
	@Test public void test_10838() { checkIsSubtype("{{int f1} f2}","{void f2,any f3}"); }
	@Test public void test_10839() { checkIsSubtype("{{int f1} f2}","{void f1,null f2}"); }
	@Test public void test_10840() { checkIsSubtype("{{int f1} f2}","{void f2,null f3}"); }
	@Test public void test_10841() { checkIsSubtype("{{int f1} f2}","{void f1,int f2}"); }
	@Test public void test_10842() { checkIsSubtype("{{int f1} f2}","{void f2,int f3}"); }
	@Test public void test_10843() { checkIsSubtype("{{int f1} f2}","{any f1,void f2}"); }
	@Test public void test_10844() { checkIsSubtype("{{int f1} f2}","{any f2,void f3}"); }
	@Test public void test_10845() { checkNotSubtype("{{int f1} f2}","{any f1,any f2}"); }
	@Test public void test_10846() { checkNotSubtype("{{int f1} f2}","{any f2,any f3}"); }
	@Test public void test_10847() { checkNotSubtype("{{int f1} f2}","{any f1,null f2}"); }
	@Test public void test_10848() { checkNotSubtype("{{int f1} f2}","{any f2,null f3}"); }
	@Test public void test_10849() { checkNotSubtype("{{int f1} f2}","{any f1,int f2}"); }
	@Test public void test_10850() { checkNotSubtype("{{int f1} f2}","{any f2,int f3}"); }
	@Test public void test_10851() { checkIsSubtype("{{int f1} f2}","{null f1,void f2}"); }
	@Test public void test_10852() { checkIsSubtype("{{int f1} f2}","{null f2,void f3}"); }
	@Test public void test_10853() { checkNotSubtype("{{int f1} f2}","{null f1,any f2}"); }
	@Test public void test_10854() { checkNotSubtype("{{int f1} f2}","{null f2,any f3}"); }
	@Test public void test_10855() { checkNotSubtype("{{int f1} f2}","{null f1,null f2}"); }
	@Test public void test_10856() { checkNotSubtype("{{int f1} f2}","{null f2,null f3}"); }
	@Test public void test_10857() { checkNotSubtype("{{int f1} f2}","{null f1,int f2}"); }
	@Test public void test_10858() { checkNotSubtype("{{int f1} f2}","{null f2,int f3}"); }
	@Test public void test_10859() { checkIsSubtype("{{int f1} f2}","{int f1,void f2}"); }
	@Test public void test_10860() { checkIsSubtype("{{int f1} f2}","{int f2,void f3}"); }
	@Test public void test_10861() { checkNotSubtype("{{int f1} f2}","{int f1,any f2}"); }
	@Test public void test_10862() { checkNotSubtype("{{int f1} f2}","{int f2,any f3}"); }
	@Test public void test_10863() { checkNotSubtype("{{int f1} f2}","{int f1,null f2}"); }
	@Test public void test_10864() { checkNotSubtype("{{int f1} f2}","{int f2,null f3}"); }
	@Test public void test_10865() { checkNotSubtype("{{int f1} f2}","{int f1,int f2}"); }
	@Test public void test_10866() { checkNotSubtype("{{int f1} f2}","{int f2,int f3}"); }
	@Test public void test_10867() { checkNotSubtype("{{int f1} f2}","{[void] f1}"); }
	@Test public void test_10868() { checkNotSubtype("{{int f1} f2}","{[void] f2}"); }
	@Test public void test_10869() { checkIsSubtype("{{int f1} f2}","{[void] f1,void f2}"); }
	@Test public void test_10870() { checkIsSubtype("{{int f1} f2}","{[void] f2,void f3}"); }
	@Test public void test_10871() { checkNotSubtype("{{int f1} f2}","{[any] f1}"); }
	@Test public void test_10872() { checkNotSubtype("{{int f1} f2}","{[any] f2}"); }
	@Test public void test_10873() { checkNotSubtype("{{int f1} f2}","{[any] f1,any f2}"); }
	@Test public void test_10874() { checkNotSubtype("{{int f1} f2}","{[any] f2,any f3}"); }
	@Test public void test_10875() { checkNotSubtype("{{int f1} f2}","{[null] f1}"); }
	@Test public void test_10876() { checkNotSubtype("{{int f1} f2}","{[null] f2}"); }
	@Test public void test_10877() { checkNotSubtype("{{int f1} f2}","{[null] f1,null f2}"); }
	@Test public void test_10878() { checkNotSubtype("{{int f1} f2}","{[null] f2,null f3}"); }
	@Test public void test_10879() { checkNotSubtype("{{int f1} f2}","{[int] f1}"); }
	@Test public void test_10880() { checkNotSubtype("{{int f1} f2}","{[int] f2}"); }
	@Test public void test_10881() { checkNotSubtype("{{int f1} f2}","{[int] f1,int f2}"); }
	@Test public void test_10882() { checkNotSubtype("{{int f1} f2}","{[int] f2,int f3}"); }
	@Test public void test_10883() { checkIsSubtype("{{int f1} f2}","{{void f1} f1}"); }
	@Test public void test_10884() { checkIsSubtype("{{int f1} f2}","{{void f2} f1}"); }
	@Test public void test_10885() { checkIsSubtype("{{int f1} f2}","{{void f1} f2}"); }
	@Test public void test_10886() { checkIsSubtype("{{int f1} f2}","{{void f2} f2}"); }
	@Test public void test_10887() { checkIsSubtype("{{int f1} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_10888() { checkIsSubtype("{{int f1} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_10889() { checkIsSubtype("{{int f1} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_10890() { checkIsSubtype("{{int f1} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_10891() { checkNotSubtype("{{int f1} f2}","{{any f1} f1}"); }
	@Test public void test_10892() { checkNotSubtype("{{int f1} f2}","{{any f2} f1}"); }
	@Test public void test_10893() { checkNotSubtype("{{int f1} f2}","{{any f1} f2}"); }
	@Test public void test_10894() { checkNotSubtype("{{int f1} f2}","{{any f2} f2}"); }
	@Test public void test_10895() { checkNotSubtype("{{int f1} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_10896() { checkNotSubtype("{{int f1} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_10897() { checkNotSubtype("{{int f1} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_10898() { checkNotSubtype("{{int f1} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_10899() { checkNotSubtype("{{int f1} f2}","{{null f1} f1}"); }
	@Test public void test_10900() { checkNotSubtype("{{int f1} f2}","{{null f2} f1}"); }
	@Test public void test_10901() { checkNotSubtype("{{int f1} f2}","{{null f1} f2}"); }
	@Test public void test_10902() { checkNotSubtype("{{int f1} f2}","{{null f2} f2}"); }
	@Test public void test_10903() { checkNotSubtype("{{int f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_10904() { checkNotSubtype("{{int f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_10905() { checkNotSubtype("{{int f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_10906() { checkNotSubtype("{{int f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_10907() { checkNotSubtype("{{int f1} f2}","{{int f1} f1}"); }
	@Test public void test_10908() { checkNotSubtype("{{int f1} f2}","{{int f2} f1}"); }
	@Test public void test_10909() { checkIsSubtype("{{int f1} f2}","{{int f1} f2}"); }
	@Test public void test_10910() { checkNotSubtype("{{int f1} f2}","{{int f2} f2}"); }
	@Test public void test_10911() { checkNotSubtype("{{int f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_10912() { checkNotSubtype("{{int f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_10913() { checkNotSubtype("{{int f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_10914() { checkNotSubtype("{{int f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_10915() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_10916() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_10917() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_10918() { checkNotSubtype("{{int f2} f2}","[void]"); }
	@Test public void test_10919() { checkNotSubtype("{{int f2} f2}","[any]"); }
	@Test public void test_10920() { checkNotSubtype("{{int f2} f2}","[null]"); }
	@Test public void test_10921() { checkNotSubtype("{{int f2} f2}","[int]"); }
	@Test public void test_10922() { checkIsSubtype("{{int f2} f2}","{void f1}"); }
	@Test public void test_10923() { checkIsSubtype("{{int f2} f2}","{void f2}"); }
	@Test public void test_10924() { checkNotSubtype("{{int f2} f2}","{any f1}"); }
	@Test public void test_10925() { checkNotSubtype("{{int f2} f2}","{any f2}"); }
	@Test public void test_10926() { checkNotSubtype("{{int f2} f2}","{null f1}"); }
	@Test public void test_10927() { checkNotSubtype("{{int f2} f2}","{null f2}"); }
	@Test public void test_10928() { checkNotSubtype("{{int f2} f2}","{int f1}"); }
	@Test public void test_10929() { checkNotSubtype("{{int f2} f2}","{int f2}"); }
	@Test public void test_10930() { checkNotSubtype("{{int f2} f2}","[[void]]"); }
	@Test public void test_10931() { checkNotSubtype("{{int f2} f2}","[[any]]"); }
	@Test public void test_10932() { checkNotSubtype("{{int f2} f2}","[[null]]"); }
	@Test public void test_10933() { checkNotSubtype("{{int f2} f2}","[[int]]"); }
	@Test public void test_10934() { checkNotSubtype("{{int f2} f2}","[{void f1}]"); }
	@Test public void test_10935() { checkNotSubtype("{{int f2} f2}","[{void f2}]"); }
	@Test public void test_10936() { checkNotSubtype("{{int f2} f2}","[{any f1}]"); }
	@Test public void test_10937() { checkNotSubtype("{{int f2} f2}","[{any f2}]"); }
	@Test public void test_10938() { checkNotSubtype("{{int f2} f2}","[{null f1}]"); }
	@Test public void test_10939() { checkNotSubtype("{{int f2} f2}","[{null f2}]"); }
	@Test public void test_10940() { checkNotSubtype("{{int f2} f2}","[{int f1}]"); }
	@Test public void test_10941() { checkNotSubtype("{{int f2} f2}","[{int f2}]"); }
	@Test public void test_10942() { checkIsSubtype("{{int f2} f2}","{void f1,void f2}"); }
	@Test public void test_10943() { checkIsSubtype("{{int f2} f2}","{void f2,void f3}"); }
	@Test public void test_10944() { checkIsSubtype("{{int f2} f2}","{void f1,any f2}"); }
	@Test public void test_10945() { checkIsSubtype("{{int f2} f2}","{void f2,any f3}"); }
	@Test public void test_10946() { checkIsSubtype("{{int f2} f2}","{void f1,null f2}"); }
	@Test public void test_10947() { checkIsSubtype("{{int f2} f2}","{void f2,null f3}"); }
	@Test public void test_10948() { checkIsSubtype("{{int f2} f2}","{void f1,int f2}"); }
	@Test public void test_10949() { checkIsSubtype("{{int f2} f2}","{void f2,int f3}"); }
	@Test public void test_10950() { checkIsSubtype("{{int f2} f2}","{any f1,void f2}"); }
	@Test public void test_10951() { checkIsSubtype("{{int f2} f2}","{any f2,void f3}"); }
	@Test public void test_10952() { checkNotSubtype("{{int f2} f2}","{any f1,any f2}"); }
	@Test public void test_10953() { checkNotSubtype("{{int f2} f2}","{any f2,any f3}"); }
	@Test public void test_10954() { checkNotSubtype("{{int f2} f2}","{any f1,null f2}"); }
	@Test public void test_10955() { checkNotSubtype("{{int f2} f2}","{any f2,null f3}"); }
	@Test public void test_10956() { checkNotSubtype("{{int f2} f2}","{any f1,int f2}"); }
	@Test public void test_10957() { checkNotSubtype("{{int f2} f2}","{any f2,int f3}"); }
	@Test public void test_10958() { checkIsSubtype("{{int f2} f2}","{null f1,void f2}"); }
	@Test public void test_10959() { checkIsSubtype("{{int f2} f2}","{null f2,void f3}"); }
	@Test public void test_10960() { checkNotSubtype("{{int f2} f2}","{null f1,any f2}"); }
	@Test public void test_10961() { checkNotSubtype("{{int f2} f2}","{null f2,any f3}"); }
	@Test public void test_10962() { checkNotSubtype("{{int f2} f2}","{null f1,null f2}"); }
	@Test public void test_10963() { checkNotSubtype("{{int f2} f2}","{null f2,null f3}"); }
	@Test public void test_10964() { checkNotSubtype("{{int f2} f2}","{null f1,int f2}"); }
	@Test public void test_10965() { checkNotSubtype("{{int f2} f2}","{null f2,int f3}"); }
	@Test public void test_10966() { checkIsSubtype("{{int f2} f2}","{int f1,void f2}"); }
	@Test public void test_10967() { checkIsSubtype("{{int f2} f2}","{int f2,void f3}"); }
	@Test public void test_10968() { checkNotSubtype("{{int f2} f2}","{int f1,any f2}"); }
	@Test public void test_10969() { checkNotSubtype("{{int f2} f2}","{int f2,any f3}"); }
	@Test public void test_10970() { checkNotSubtype("{{int f2} f2}","{int f1,null f2}"); }
	@Test public void test_10971() { checkNotSubtype("{{int f2} f2}","{int f2,null f3}"); }
	@Test public void test_10972() { checkNotSubtype("{{int f2} f2}","{int f1,int f2}"); }
	@Test public void test_10973() { checkNotSubtype("{{int f2} f2}","{int f2,int f3}"); }
	@Test public void test_10974() { checkNotSubtype("{{int f2} f2}","{[void] f1}"); }
	@Test public void test_10975() { checkNotSubtype("{{int f2} f2}","{[void] f2}"); }
	@Test public void test_10976() { checkIsSubtype("{{int f2} f2}","{[void] f1,void f2}"); }
	@Test public void test_10977() { checkIsSubtype("{{int f2} f2}","{[void] f2,void f3}"); }
	@Test public void test_10978() { checkNotSubtype("{{int f2} f2}","{[any] f1}"); }
	@Test public void test_10979() { checkNotSubtype("{{int f2} f2}","{[any] f2}"); }
	@Test public void test_10980() { checkNotSubtype("{{int f2} f2}","{[any] f1,any f2}"); }
	@Test public void test_10981() { checkNotSubtype("{{int f2} f2}","{[any] f2,any f3}"); }
	@Test public void test_10982() { checkNotSubtype("{{int f2} f2}","{[null] f1}"); }
	@Test public void test_10983() { checkNotSubtype("{{int f2} f2}","{[null] f2}"); }
	@Test public void test_10984() { checkNotSubtype("{{int f2} f2}","{[null] f1,null f2}"); }
	@Test public void test_10985() { checkNotSubtype("{{int f2} f2}","{[null] f2,null f3}"); }
	@Test public void test_10986() { checkNotSubtype("{{int f2} f2}","{[int] f1}"); }
	@Test public void test_10987() { checkNotSubtype("{{int f2} f2}","{[int] f2}"); }
	@Test public void test_10988() { checkNotSubtype("{{int f2} f2}","{[int] f1,int f2}"); }
	@Test public void test_10989() { checkNotSubtype("{{int f2} f2}","{[int] f2,int f3}"); }
	@Test public void test_10990() { checkIsSubtype("{{int f2} f2}","{{void f1} f1}"); }
	@Test public void test_10991() { checkIsSubtype("{{int f2} f2}","{{void f2} f1}"); }
	@Test public void test_10992() { checkIsSubtype("{{int f2} f2}","{{void f1} f2}"); }
	@Test public void test_10993() { checkIsSubtype("{{int f2} f2}","{{void f2} f2}"); }
	@Test public void test_10994() { checkIsSubtype("{{int f2} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_10995() { checkIsSubtype("{{int f2} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_10996() { checkIsSubtype("{{int f2} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_10997() { checkIsSubtype("{{int f2} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_10998() { checkNotSubtype("{{int f2} f2}","{{any f1} f1}"); }
	@Test public void test_10999() { checkNotSubtype("{{int f2} f2}","{{any f2} f1}"); }
	@Test public void test_11000() { checkNotSubtype("{{int f2} f2}","{{any f1} f2}"); }
	@Test public void test_11001() { checkNotSubtype("{{int f2} f2}","{{any f2} f2}"); }
	@Test public void test_11002() { checkNotSubtype("{{int f2} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_11003() { checkNotSubtype("{{int f2} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_11004() { checkNotSubtype("{{int f2} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_11005() { checkNotSubtype("{{int f2} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_11006() { checkNotSubtype("{{int f2} f2}","{{null f1} f1}"); }
	@Test public void test_11007() { checkNotSubtype("{{int f2} f2}","{{null f2} f1}"); }
	@Test public void test_11008() { checkNotSubtype("{{int f2} f2}","{{null f1} f2}"); }
	@Test public void test_11009() { checkNotSubtype("{{int f2} f2}","{{null f2} f2}"); }
	@Test public void test_11010() { checkNotSubtype("{{int f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_11011() { checkNotSubtype("{{int f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_11012() { checkNotSubtype("{{int f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_11013() { checkNotSubtype("{{int f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_11014() { checkNotSubtype("{{int f2} f2}","{{int f1} f1}"); }
	@Test public void test_11015() { checkNotSubtype("{{int f2} f2}","{{int f2} f1}"); }
	@Test public void test_11016() { checkNotSubtype("{{int f2} f2}","{{int f1} f2}"); }
	@Test public void test_11017() { checkIsSubtype("{{int f2} f2}","{{int f2} f2}"); }
	@Test public void test_11018() { checkNotSubtype("{{int f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_11019() { checkNotSubtype("{{int f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_11020() { checkNotSubtype("{{int f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_11021() { checkNotSubtype("{{int f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_11022() { checkNotSubtype("{{int f1} f1,int f2}","any"); }
	@Test public void test_11023() { checkNotSubtype("{{int f1} f1,int f2}","null"); }
	@Test public void test_11024() { checkNotSubtype("{{int f1} f1,int f2}","int"); }
	@Test public void test_11025() { checkNotSubtype("{{int f1} f1,int f2}","[void]"); }
	@Test public void test_11026() { checkNotSubtype("{{int f1} f1,int f2}","[any]"); }
	@Test public void test_11027() { checkNotSubtype("{{int f1} f1,int f2}","[null]"); }
	@Test public void test_11028() { checkNotSubtype("{{int f1} f1,int f2}","[int]"); }
	@Test public void test_11029() { checkIsSubtype("{{int f1} f1,int f2}","{void f1}"); }
	@Test public void test_11030() { checkIsSubtype("{{int f1} f1,int f2}","{void f2}"); }
	@Test public void test_11031() { checkNotSubtype("{{int f1} f1,int f2}","{any f1}"); }
	@Test public void test_11032() { checkNotSubtype("{{int f1} f1,int f2}","{any f2}"); }
	@Test public void test_11033() { checkNotSubtype("{{int f1} f1,int f2}","{null f1}"); }
	@Test public void test_11034() { checkNotSubtype("{{int f1} f1,int f2}","{null f2}"); }
	@Test public void test_11035() { checkNotSubtype("{{int f1} f1,int f2}","{int f1}"); }
	@Test public void test_11036() { checkNotSubtype("{{int f1} f1,int f2}","{int f2}"); }
	@Test public void test_11037() { checkNotSubtype("{{int f1} f1,int f2}","[[void]]"); }
	@Test public void test_11038() { checkNotSubtype("{{int f1} f1,int f2}","[[any]]"); }
	@Test public void test_11039() { checkNotSubtype("{{int f1} f1,int f2}","[[null]]"); }
	@Test public void test_11040() { checkNotSubtype("{{int f1} f1,int f2}","[[int]]"); }
	@Test public void test_11041() { checkNotSubtype("{{int f1} f1,int f2}","[{void f1}]"); }
	@Test public void test_11042() { checkNotSubtype("{{int f1} f1,int f2}","[{void f2}]"); }
	@Test public void test_11043() { checkNotSubtype("{{int f1} f1,int f2}","[{any f1}]"); }
	@Test public void test_11044() { checkNotSubtype("{{int f1} f1,int f2}","[{any f2}]"); }
	@Test public void test_11045() { checkNotSubtype("{{int f1} f1,int f2}","[{null f1}]"); }
	@Test public void test_11046() { checkNotSubtype("{{int f1} f1,int f2}","[{null f2}]"); }
	@Test public void test_11047() { checkNotSubtype("{{int f1} f1,int f2}","[{int f1}]"); }
	@Test public void test_11048() { checkNotSubtype("{{int f1} f1,int f2}","[{int f2}]"); }
	@Test public void test_11049() { checkIsSubtype("{{int f1} f1,int f2}","{void f1,void f2}"); }
	@Test public void test_11050() { checkIsSubtype("{{int f1} f1,int f2}","{void f2,void f3}"); }
	@Test public void test_11051() { checkIsSubtype("{{int f1} f1,int f2}","{void f1,any f2}"); }
	@Test public void test_11052() { checkIsSubtype("{{int f1} f1,int f2}","{void f2,any f3}"); }
	@Test public void test_11053() { checkIsSubtype("{{int f1} f1,int f2}","{void f1,null f2}"); }
	@Test public void test_11054() { checkIsSubtype("{{int f1} f1,int f2}","{void f2,null f3}"); }
	@Test public void test_11055() { checkIsSubtype("{{int f1} f1,int f2}","{void f1,int f2}"); }
	@Test public void test_11056() { checkIsSubtype("{{int f1} f1,int f2}","{void f2,int f3}"); }
	@Test public void test_11057() { checkIsSubtype("{{int f1} f1,int f2}","{any f1,void f2}"); }
	@Test public void test_11058() { checkIsSubtype("{{int f1} f1,int f2}","{any f2,void f3}"); }
	@Test public void test_11059() { checkNotSubtype("{{int f1} f1,int f2}","{any f1,any f2}"); }
	@Test public void test_11060() { checkNotSubtype("{{int f1} f1,int f2}","{any f2,any f3}"); }
	@Test public void test_11061() { checkNotSubtype("{{int f1} f1,int f2}","{any f1,null f2}"); }
	@Test public void test_11062() { checkNotSubtype("{{int f1} f1,int f2}","{any f2,null f3}"); }
	@Test public void test_11063() { checkNotSubtype("{{int f1} f1,int f2}","{any f1,int f2}"); }
	@Test public void test_11064() { checkNotSubtype("{{int f1} f1,int f2}","{any f2,int f3}"); }
	@Test public void test_11065() { checkIsSubtype("{{int f1} f1,int f2}","{null f1,void f2}"); }
	@Test public void test_11066() { checkIsSubtype("{{int f1} f1,int f2}","{null f2,void f3}"); }
	@Test public void test_11067() { checkNotSubtype("{{int f1} f1,int f2}","{null f1,any f2}"); }
	@Test public void test_11068() { checkNotSubtype("{{int f1} f1,int f2}","{null f2,any f3}"); }
	@Test public void test_11069() { checkNotSubtype("{{int f1} f1,int f2}","{null f1,null f2}"); }
	@Test public void test_11070() { checkNotSubtype("{{int f1} f1,int f2}","{null f2,null f3}"); }
	@Test public void test_11071() { checkNotSubtype("{{int f1} f1,int f2}","{null f1,int f2}"); }
	@Test public void test_11072() { checkNotSubtype("{{int f1} f1,int f2}","{null f2,int f3}"); }
	@Test public void test_11073() { checkIsSubtype("{{int f1} f1,int f2}","{int f1,void f2}"); }
	@Test public void test_11074() { checkIsSubtype("{{int f1} f1,int f2}","{int f2,void f3}"); }
	@Test public void test_11075() { checkNotSubtype("{{int f1} f1,int f2}","{int f1,any f2}"); }
	@Test public void test_11076() { checkNotSubtype("{{int f1} f1,int f2}","{int f2,any f3}"); }
	@Test public void test_11077() { checkNotSubtype("{{int f1} f1,int f2}","{int f1,null f2}"); }
	@Test public void test_11078() { checkNotSubtype("{{int f1} f1,int f2}","{int f2,null f3}"); }
	@Test public void test_11079() { checkNotSubtype("{{int f1} f1,int f2}","{int f1,int f2}"); }
	@Test public void test_11080() { checkNotSubtype("{{int f1} f1,int f2}","{int f2,int f3}"); }
	@Test public void test_11081() { checkNotSubtype("{{int f1} f1,int f2}","{[void] f1}"); }
	@Test public void test_11082() { checkNotSubtype("{{int f1} f1,int f2}","{[void] f2}"); }
	@Test public void test_11083() { checkIsSubtype("{{int f1} f1,int f2}","{[void] f1,void f2}"); }
	@Test public void test_11084() { checkIsSubtype("{{int f1} f1,int f2}","{[void] f2,void f3}"); }
	@Test public void test_11085() { checkNotSubtype("{{int f1} f1,int f2}","{[any] f1}"); }
	@Test public void test_11086() { checkNotSubtype("{{int f1} f1,int f2}","{[any] f2}"); }
	@Test public void test_11087() { checkNotSubtype("{{int f1} f1,int f2}","{[any] f1,any f2}"); }
	@Test public void test_11088() { checkNotSubtype("{{int f1} f1,int f2}","{[any] f2,any f3}"); }
	@Test public void test_11089() { checkNotSubtype("{{int f1} f1,int f2}","{[null] f1}"); }
	@Test public void test_11090() { checkNotSubtype("{{int f1} f1,int f2}","{[null] f2}"); }
	@Test public void test_11091() { checkNotSubtype("{{int f1} f1,int f2}","{[null] f1,null f2}"); }
	@Test public void test_11092() { checkNotSubtype("{{int f1} f1,int f2}","{[null] f2,null f3}"); }
	@Test public void test_11093() { checkNotSubtype("{{int f1} f1,int f2}","{[int] f1}"); }
	@Test public void test_11094() { checkNotSubtype("{{int f1} f1,int f2}","{[int] f2}"); }
	@Test public void test_11095() { checkNotSubtype("{{int f1} f1,int f2}","{[int] f1,int f2}"); }
	@Test public void test_11096() { checkNotSubtype("{{int f1} f1,int f2}","{[int] f2,int f3}"); }
	@Test public void test_11097() { checkIsSubtype("{{int f1} f1,int f2}","{{void f1} f1}"); }
	@Test public void test_11098() { checkIsSubtype("{{int f1} f1,int f2}","{{void f2} f1}"); }
	@Test public void test_11099() { checkIsSubtype("{{int f1} f1,int f2}","{{void f1} f2}"); }
	@Test public void test_11100() { checkIsSubtype("{{int f1} f1,int f2}","{{void f2} f2}"); }
	@Test public void test_11101() { checkIsSubtype("{{int f1} f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_11102() { checkIsSubtype("{{int f1} f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_11103() { checkIsSubtype("{{int f1} f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_11104() { checkIsSubtype("{{int f1} f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_11105() { checkNotSubtype("{{int f1} f1,int f2}","{{any f1} f1}"); }
	@Test public void test_11106() { checkNotSubtype("{{int f1} f1,int f2}","{{any f2} f1}"); }
	@Test public void test_11107() { checkNotSubtype("{{int f1} f1,int f2}","{{any f1} f2}"); }
	@Test public void test_11108() { checkNotSubtype("{{int f1} f1,int f2}","{{any f2} f2}"); }
	@Test public void test_11109() { checkNotSubtype("{{int f1} f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_11110() { checkNotSubtype("{{int f1} f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_11111() { checkNotSubtype("{{int f1} f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_11112() { checkNotSubtype("{{int f1} f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_11113() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f1}"); }
	@Test public void test_11114() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f1}"); }
	@Test public void test_11115() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f2}"); }
	@Test public void test_11116() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f2}"); }
	@Test public void test_11117() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_11118() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_11119() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_11120() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_11121() { checkNotSubtype("{{int f1} f1,int f2}","{{int f1} f1}"); }
	@Test public void test_11122() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f1}"); }
	@Test public void test_11123() { checkNotSubtype("{{int f1} f1,int f2}","{{int f1} f2}"); }
	@Test public void test_11124() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f2}"); }
	@Test public void test_11125() { checkIsSubtype("{{int f1} f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_11126() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_11127() { checkNotSubtype("{{int f1} f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_11128() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_11129() { checkNotSubtype("{{int f2} f1,int f2}","any"); }
	@Test public void test_11130() { checkNotSubtype("{{int f2} f1,int f2}","null"); }
	@Test public void test_11131() { checkNotSubtype("{{int f2} f1,int f2}","int"); }
	@Test public void test_11132() { checkNotSubtype("{{int f2} f1,int f2}","[void]"); }
	@Test public void test_11133() { checkNotSubtype("{{int f2} f1,int f2}","[any]"); }
	@Test public void test_11134() { checkNotSubtype("{{int f2} f1,int f2}","[null]"); }
	@Test public void test_11135() { checkNotSubtype("{{int f2} f1,int f2}","[int]"); }
	@Test public void test_11136() { checkIsSubtype("{{int f2} f1,int f2}","{void f1}"); }
	@Test public void test_11137() { checkIsSubtype("{{int f2} f1,int f2}","{void f2}"); }
	@Test public void test_11138() { checkNotSubtype("{{int f2} f1,int f2}","{any f1}"); }
	@Test public void test_11139() { checkNotSubtype("{{int f2} f1,int f2}","{any f2}"); }
	@Test public void test_11140() { checkNotSubtype("{{int f2} f1,int f2}","{null f1}"); }
	@Test public void test_11141() { checkNotSubtype("{{int f2} f1,int f2}","{null f2}"); }
	@Test public void test_11142() { checkNotSubtype("{{int f2} f1,int f2}","{int f1}"); }
	@Test public void test_11143() { checkNotSubtype("{{int f2} f1,int f2}","{int f2}"); }
	@Test public void test_11144() { checkNotSubtype("{{int f2} f1,int f2}","[[void]]"); }
	@Test public void test_11145() { checkNotSubtype("{{int f2} f1,int f2}","[[any]]"); }
	@Test public void test_11146() { checkNotSubtype("{{int f2} f1,int f2}","[[null]]"); }
	@Test public void test_11147() { checkNotSubtype("{{int f2} f1,int f2}","[[int]]"); }
	@Test public void test_11148() { checkNotSubtype("{{int f2} f1,int f2}","[{void f1}]"); }
	@Test public void test_11149() { checkNotSubtype("{{int f2} f1,int f2}","[{void f2}]"); }
	@Test public void test_11150() { checkNotSubtype("{{int f2} f1,int f2}","[{any f1}]"); }
	@Test public void test_11151() { checkNotSubtype("{{int f2} f1,int f2}","[{any f2}]"); }
	@Test public void test_11152() { checkNotSubtype("{{int f2} f1,int f2}","[{null f1}]"); }
	@Test public void test_11153() { checkNotSubtype("{{int f2} f1,int f2}","[{null f2}]"); }
	@Test public void test_11154() { checkNotSubtype("{{int f2} f1,int f2}","[{int f1}]"); }
	@Test public void test_11155() { checkNotSubtype("{{int f2} f1,int f2}","[{int f2}]"); }
	@Test public void test_11156() { checkIsSubtype("{{int f2} f1,int f2}","{void f1,void f2}"); }
	@Test public void test_11157() { checkIsSubtype("{{int f2} f1,int f2}","{void f2,void f3}"); }
	@Test public void test_11158() { checkIsSubtype("{{int f2} f1,int f2}","{void f1,any f2}"); }
	@Test public void test_11159() { checkIsSubtype("{{int f2} f1,int f2}","{void f2,any f3}"); }
	@Test public void test_11160() { checkIsSubtype("{{int f2} f1,int f2}","{void f1,null f2}"); }
	@Test public void test_11161() { checkIsSubtype("{{int f2} f1,int f2}","{void f2,null f3}"); }
	@Test public void test_11162() { checkIsSubtype("{{int f2} f1,int f2}","{void f1,int f2}"); }
	@Test public void test_11163() { checkIsSubtype("{{int f2} f1,int f2}","{void f2,int f3}"); }
	@Test public void test_11164() { checkIsSubtype("{{int f2} f1,int f2}","{any f1,void f2}"); }
	@Test public void test_11165() { checkIsSubtype("{{int f2} f1,int f2}","{any f2,void f3}"); }
	@Test public void test_11166() { checkNotSubtype("{{int f2} f1,int f2}","{any f1,any f2}"); }
	@Test public void test_11167() { checkNotSubtype("{{int f2} f1,int f2}","{any f2,any f3}"); }
	@Test public void test_11168() { checkNotSubtype("{{int f2} f1,int f2}","{any f1,null f2}"); }
	@Test public void test_11169() { checkNotSubtype("{{int f2} f1,int f2}","{any f2,null f3}"); }
	@Test public void test_11170() { checkNotSubtype("{{int f2} f1,int f2}","{any f1,int f2}"); }
	@Test public void test_11171() { checkNotSubtype("{{int f2} f1,int f2}","{any f2,int f3}"); }
	@Test public void test_11172() { checkIsSubtype("{{int f2} f1,int f2}","{null f1,void f2}"); }
	@Test public void test_11173() { checkIsSubtype("{{int f2} f1,int f2}","{null f2,void f3}"); }
	@Test public void test_11174() { checkNotSubtype("{{int f2} f1,int f2}","{null f1,any f2}"); }
	@Test public void test_11175() { checkNotSubtype("{{int f2} f1,int f2}","{null f2,any f3}"); }
	@Test public void test_11176() { checkNotSubtype("{{int f2} f1,int f2}","{null f1,null f2}"); }
	@Test public void test_11177() { checkNotSubtype("{{int f2} f1,int f2}","{null f2,null f3}"); }
	@Test public void test_11178() { checkNotSubtype("{{int f2} f1,int f2}","{null f1,int f2}"); }
	@Test public void test_11179() { checkNotSubtype("{{int f2} f1,int f2}","{null f2,int f3}"); }
	@Test public void test_11180() { checkIsSubtype("{{int f2} f1,int f2}","{int f1,void f2}"); }
	@Test public void test_11181() { checkIsSubtype("{{int f2} f1,int f2}","{int f2,void f3}"); }
	@Test public void test_11182() { checkNotSubtype("{{int f2} f1,int f2}","{int f1,any f2}"); }
	@Test public void test_11183() { checkNotSubtype("{{int f2} f1,int f2}","{int f2,any f3}"); }
	@Test public void test_11184() { checkNotSubtype("{{int f2} f1,int f2}","{int f1,null f2}"); }
	@Test public void test_11185() { checkNotSubtype("{{int f2} f1,int f2}","{int f2,null f3}"); }
	@Test public void test_11186() { checkNotSubtype("{{int f2} f1,int f2}","{int f1,int f2}"); }
	@Test public void test_11187() { checkNotSubtype("{{int f2} f1,int f2}","{int f2,int f3}"); }
	@Test public void test_11188() { checkNotSubtype("{{int f2} f1,int f2}","{[void] f1}"); }
	@Test public void test_11189() { checkNotSubtype("{{int f2} f1,int f2}","{[void] f2}"); }
	@Test public void test_11190() { checkIsSubtype("{{int f2} f1,int f2}","{[void] f1,void f2}"); }
	@Test public void test_11191() { checkIsSubtype("{{int f2} f1,int f2}","{[void] f2,void f3}"); }
	@Test public void test_11192() { checkNotSubtype("{{int f2} f1,int f2}","{[any] f1}"); }
	@Test public void test_11193() { checkNotSubtype("{{int f2} f1,int f2}","{[any] f2}"); }
	@Test public void test_11194() { checkNotSubtype("{{int f2} f1,int f2}","{[any] f1,any f2}"); }
	@Test public void test_11195() { checkNotSubtype("{{int f2} f1,int f2}","{[any] f2,any f3}"); }
	@Test public void test_11196() { checkNotSubtype("{{int f2} f1,int f2}","{[null] f1}"); }
	@Test public void test_11197() { checkNotSubtype("{{int f2} f1,int f2}","{[null] f2}"); }
	@Test public void test_11198() { checkNotSubtype("{{int f2} f1,int f2}","{[null] f1,null f2}"); }
	@Test public void test_11199() { checkNotSubtype("{{int f2} f1,int f2}","{[null] f2,null f3}"); }
	@Test public void test_11200() { checkNotSubtype("{{int f2} f1,int f2}","{[int] f1}"); }
	@Test public void test_11201() { checkNotSubtype("{{int f2} f1,int f2}","{[int] f2}"); }
	@Test public void test_11202() { checkNotSubtype("{{int f2} f1,int f2}","{[int] f1,int f2}"); }
	@Test public void test_11203() { checkNotSubtype("{{int f2} f1,int f2}","{[int] f2,int f3}"); }
	@Test public void test_11204() { checkIsSubtype("{{int f2} f1,int f2}","{{void f1} f1}"); }
	@Test public void test_11205() { checkIsSubtype("{{int f2} f1,int f2}","{{void f2} f1}"); }
	@Test public void test_11206() { checkIsSubtype("{{int f2} f1,int f2}","{{void f1} f2}"); }
	@Test public void test_11207() { checkIsSubtype("{{int f2} f1,int f2}","{{void f2} f2}"); }
	@Test public void test_11208() { checkIsSubtype("{{int f2} f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_11209() { checkIsSubtype("{{int f2} f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_11210() { checkIsSubtype("{{int f2} f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_11211() { checkIsSubtype("{{int f2} f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_11212() { checkNotSubtype("{{int f2} f1,int f2}","{{any f1} f1}"); }
	@Test public void test_11213() { checkNotSubtype("{{int f2} f1,int f2}","{{any f2} f1}"); }
	@Test public void test_11214() { checkNotSubtype("{{int f2} f1,int f2}","{{any f1} f2}"); }
	@Test public void test_11215() { checkNotSubtype("{{int f2} f1,int f2}","{{any f2} f2}"); }
	@Test public void test_11216() { checkNotSubtype("{{int f2} f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_11217() { checkNotSubtype("{{int f2} f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_11218() { checkNotSubtype("{{int f2} f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_11219() { checkNotSubtype("{{int f2} f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_11220() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f1}"); }
	@Test public void test_11221() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f1}"); }
	@Test public void test_11222() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f2}"); }
	@Test public void test_11223() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f2}"); }
	@Test public void test_11224() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_11225() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_11226() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_11227() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_11228() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f1}"); }
	@Test public void test_11229() { checkNotSubtype("{{int f2} f1,int f2}","{{int f2} f1}"); }
	@Test public void test_11230() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f2}"); }
	@Test public void test_11231() { checkNotSubtype("{{int f2} f1,int f2}","{{int f2} f2}"); }
	@Test public void test_11232() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_11233() { checkIsSubtype("{{int f2} f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_11234() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_11235() { checkNotSubtype("{{int f2} f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_11236() { checkNotSubtype("{{int f1} f2,int f3}","any"); }
	@Test public void test_11237() { checkNotSubtype("{{int f1} f2,int f3}","null"); }
	@Test public void test_11238() { checkNotSubtype("{{int f1} f2,int f3}","int"); }
	@Test public void test_11239() { checkNotSubtype("{{int f1} f2,int f3}","[void]"); }
	@Test public void test_11240() { checkNotSubtype("{{int f1} f2,int f3}","[any]"); }
	@Test public void test_11241() { checkNotSubtype("{{int f1} f2,int f3}","[null]"); }
	@Test public void test_11242() { checkNotSubtype("{{int f1} f2,int f3}","[int]"); }
	@Test public void test_11243() { checkIsSubtype("{{int f1} f2,int f3}","{void f1}"); }
	@Test public void test_11244() { checkIsSubtype("{{int f1} f2,int f3}","{void f2}"); }
	@Test public void test_11245() { checkNotSubtype("{{int f1} f2,int f3}","{any f1}"); }
	@Test public void test_11246() { checkNotSubtype("{{int f1} f2,int f3}","{any f2}"); }
	@Test public void test_11247() { checkNotSubtype("{{int f1} f2,int f3}","{null f1}"); }
	@Test public void test_11248() { checkNotSubtype("{{int f1} f2,int f3}","{null f2}"); }
	@Test public void test_11249() { checkNotSubtype("{{int f1} f2,int f3}","{int f1}"); }
	@Test public void test_11250() { checkNotSubtype("{{int f1} f2,int f3}","{int f2}"); }
	@Test public void test_11251() { checkNotSubtype("{{int f1} f2,int f3}","[[void]]"); }
	@Test public void test_11252() { checkNotSubtype("{{int f1} f2,int f3}","[[any]]"); }
	@Test public void test_11253() { checkNotSubtype("{{int f1} f2,int f3}","[[null]]"); }
	@Test public void test_11254() { checkNotSubtype("{{int f1} f2,int f3}","[[int]]"); }
	@Test public void test_11255() { checkNotSubtype("{{int f1} f2,int f3}","[{void f1}]"); }
	@Test public void test_11256() { checkNotSubtype("{{int f1} f2,int f3}","[{void f2}]"); }
	@Test public void test_11257() { checkNotSubtype("{{int f1} f2,int f3}","[{any f1}]"); }
	@Test public void test_11258() { checkNotSubtype("{{int f1} f2,int f3}","[{any f2}]"); }
	@Test public void test_11259() { checkNotSubtype("{{int f1} f2,int f3}","[{null f1}]"); }
	@Test public void test_11260() { checkNotSubtype("{{int f1} f2,int f3}","[{null f2}]"); }
	@Test public void test_11261() { checkNotSubtype("{{int f1} f2,int f3}","[{int f1}]"); }
	@Test public void test_11262() { checkNotSubtype("{{int f1} f2,int f3}","[{int f2}]"); }
	@Test public void test_11263() { checkIsSubtype("{{int f1} f2,int f3}","{void f1,void f2}"); }
	@Test public void test_11264() { checkIsSubtype("{{int f1} f2,int f3}","{void f2,void f3}"); }
	@Test public void test_11265() { checkIsSubtype("{{int f1} f2,int f3}","{void f1,any f2}"); }
	@Test public void test_11266() { checkIsSubtype("{{int f1} f2,int f3}","{void f2,any f3}"); }
	@Test public void test_11267() { checkIsSubtype("{{int f1} f2,int f3}","{void f1,null f2}"); }
	@Test public void test_11268() { checkIsSubtype("{{int f1} f2,int f3}","{void f2,null f3}"); }
	@Test public void test_11269() { checkIsSubtype("{{int f1} f2,int f3}","{void f1,int f2}"); }
	@Test public void test_11270() { checkIsSubtype("{{int f1} f2,int f3}","{void f2,int f3}"); }
	@Test public void test_11271() { checkIsSubtype("{{int f1} f2,int f3}","{any f1,void f2}"); }
	@Test public void test_11272() { checkIsSubtype("{{int f1} f2,int f3}","{any f2,void f3}"); }
	@Test public void test_11273() { checkNotSubtype("{{int f1} f2,int f3}","{any f1,any f2}"); }
	@Test public void test_11274() { checkNotSubtype("{{int f1} f2,int f3}","{any f2,any f3}"); }
	@Test public void test_11275() { checkNotSubtype("{{int f1} f2,int f3}","{any f1,null f2}"); }
	@Test public void test_11276() { checkNotSubtype("{{int f1} f2,int f3}","{any f2,null f3}"); }
	@Test public void test_11277() { checkNotSubtype("{{int f1} f2,int f3}","{any f1,int f2}"); }
	@Test public void test_11278() { checkNotSubtype("{{int f1} f2,int f3}","{any f2,int f3}"); }
	@Test public void test_11279() { checkIsSubtype("{{int f1} f2,int f3}","{null f1,void f2}"); }
	@Test public void test_11280() { checkIsSubtype("{{int f1} f2,int f3}","{null f2,void f3}"); }
	@Test public void test_11281() { checkNotSubtype("{{int f1} f2,int f3}","{null f1,any f2}"); }
	@Test public void test_11282() { checkNotSubtype("{{int f1} f2,int f3}","{null f2,any f3}"); }
	@Test public void test_11283() { checkNotSubtype("{{int f1} f2,int f3}","{null f1,null f2}"); }
	@Test public void test_11284() { checkNotSubtype("{{int f1} f2,int f3}","{null f2,null f3}"); }
	@Test public void test_11285() { checkNotSubtype("{{int f1} f2,int f3}","{null f1,int f2}"); }
	@Test public void test_11286() { checkNotSubtype("{{int f1} f2,int f3}","{null f2,int f3}"); }
	@Test public void test_11287() { checkIsSubtype("{{int f1} f2,int f3}","{int f1,void f2}"); }
	@Test public void test_11288() { checkIsSubtype("{{int f1} f2,int f3}","{int f2,void f3}"); }
	@Test public void test_11289() { checkNotSubtype("{{int f1} f2,int f3}","{int f1,any f2}"); }
	@Test public void test_11290() { checkNotSubtype("{{int f1} f2,int f3}","{int f2,any f3}"); }
	@Test public void test_11291() { checkNotSubtype("{{int f1} f2,int f3}","{int f1,null f2}"); }
	@Test public void test_11292() { checkNotSubtype("{{int f1} f2,int f3}","{int f2,null f3}"); }
	@Test public void test_11293() { checkNotSubtype("{{int f1} f2,int f3}","{int f1,int f2}"); }
	@Test public void test_11294() { checkNotSubtype("{{int f1} f2,int f3}","{int f2,int f3}"); }
	@Test public void test_11295() { checkNotSubtype("{{int f1} f2,int f3}","{[void] f1}"); }
	@Test public void test_11296() { checkNotSubtype("{{int f1} f2,int f3}","{[void] f2}"); }
	@Test public void test_11297() { checkIsSubtype("{{int f1} f2,int f3}","{[void] f1,void f2}"); }
	@Test public void test_11298() { checkIsSubtype("{{int f1} f2,int f3}","{[void] f2,void f3}"); }
	@Test public void test_11299() { checkNotSubtype("{{int f1} f2,int f3}","{[any] f1}"); }
	@Test public void test_11300() { checkNotSubtype("{{int f1} f2,int f3}","{[any] f2}"); }
	@Test public void test_11301() { checkNotSubtype("{{int f1} f2,int f3}","{[any] f1,any f2}"); }
	@Test public void test_11302() { checkNotSubtype("{{int f1} f2,int f3}","{[any] f2,any f3}"); }
	@Test public void test_11303() { checkNotSubtype("{{int f1} f2,int f3}","{[null] f1}"); }
	@Test public void test_11304() { checkNotSubtype("{{int f1} f2,int f3}","{[null] f2}"); }
	@Test public void test_11305() { checkNotSubtype("{{int f1} f2,int f3}","{[null] f1,null f2}"); }
	@Test public void test_11306() { checkNotSubtype("{{int f1} f2,int f3}","{[null] f2,null f3}"); }
	@Test public void test_11307() { checkNotSubtype("{{int f1} f2,int f3}","{[int] f1}"); }
	@Test public void test_11308() { checkNotSubtype("{{int f1} f2,int f3}","{[int] f2}"); }
	@Test public void test_11309() { checkNotSubtype("{{int f1} f2,int f3}","{[int] f1,int f2}"); }
	@Test public void test_11310() { checkNotSubtype("{{int f1} f2,int f3}","{[int] f2,int f3}"); }
	@Test public void test_11311() { checkIsSubtype("{{int f1} f2,int f3}","{{void f1} f1}"); }
	@Test public void test_11312() { checkIsSubtype("{{int f1} f2,int f3}","{{void f2} f1}"); }
	@Test public void test_11313() { checkIsSubtype("{{int f1} f2,int f3}","{{void f1} f2}"); }
	@Test public void test_11314() { checkIsSubtype("{{int f1} f2,int f3}","{{void f2} f2}"); }
	@Test public void test_11315() { checkIsSubtype("{{int f1} f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_11316() { checkIsSubtype("{{int f1} f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_11317() { checkIsSubtype("{{int f1} f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_11318() { checkIsSubtype("{{int f1} f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_11319() { checkNotSubtype("{{int f1} f2,int f3}","{{any f1} f1}"); }
	@Test public void test_11320() { checkNotSubtype("{{int f1} f2,int f3}","{{any f2} f1}"); }
	@Test public void test_11321() { checkNotSubtype("{{int f1} f2,int f3}","{{any f1} f2}"); }
	@Test public void test_11322() { checkNotSubtype("{{int f1} f2,int f3}","{{any f2} f2}"); }
	@Test public void test_11323() { checkNotSubtype("{{int f1} f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_11324() { checkNotSubtype("{{int f1} f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_11325() { checkNotSubtype("{{int f1} f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_11326() { checkNotSubtype("{{int f1} f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_11327() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f1}"); }
	@Test public void test_11328() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f1}"); }
	@Test public void test_11329() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f2}"); }
	@Test public void test_11330() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f2}"); }
	@Test public void test_11331() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_11332() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_11333() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_11334() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_11335() { checkNotSubtype("{{int f1} f2,int f3}","{{int f1} f1}"); }
	@Test public void test_11336() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f1}"); }
	@Test public void test_11337() { checkNotSubtype("{{int f1} f2,int f3}","{{int f1} f2}"); }
	@Test public void test_11338() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f2}"); }
	@Test public void test_11339() { checkNotSubtype("{{int f1} f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_11340() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_11341() { checkIsSubtype("{{int f1} f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_11342() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_11343() { checkNotSubtype("{{int f2} f2,int f3}","any"); }
	@Test public void test_11344() { checkNotSubtype("{{int f2} f2,int f3}","null"); }
	@Test public void test_11345() { checkNotSubtype("{{int f2} f2,int f3}","int"); }
	@Test public void test_11346() { checkNotSubtype("{{int f2} f2,int f3}","[void]"); }
	@Test public void test_11347() { checkNotSubtype("{{int f2} f2,int f3}","[any]"); }
	@Test public void test_11348() { checkNotSubtype("{{int f2} f2,int f3}","[null]"); }
	@Test public void test_11349() { checkNotSubtype("{{int f2} f2,int f3}","[int]"); }
	@Test public void test_11350() { checkIsSubtype("{{int f2} f2,int f3}","{void f1}"); }
	@Test public void test_11351() { checkIsSubtype("{{int f2} f2,int f3}","{void f2}"); }
	@Test public void test_11352() { checkNotSubtype("{{int f2} f2,int f3}","{any f1}"); }
	@Test public void test_11353() { checkNotSubtype("{{int f2} f2,int f3}","{any f2}"); }
	@Test public void test_11354() { checkNotSubtype("{{int f2} f2,int f3}","{null f1}"); }
	@Test public void test_11355() { checkNotSubtype("{{int f2} f2,int f3}","{null f2}"); }
	@Test public void test_11356() { checkNotSubtype("{{int f2} f2,int f3}","{int f1}"); }
	@Test public void test_11357() { checkNotSubtype("{{int f2} f2,int f3}","{int f2}"); }
	@Test public void test_11358() { checkNotSubtype("{{int f2} f2,int f3}","[[void]]"); }
	@Test public void test_11359() { checkNotSubtype("{{int f2} f2,int f3}","[[any]]"); }
	@Test public void test_11360() { checkNotSubtype("{{int f2} f2,int f3}","[[null]]"); }
	@Test public void test_11361() { checkNotSubtype("{{int f2} f2,int f3}","[[int]]"); }
	@Test public void test_11362() { checkNotSubtype("{{int f2} f2,int f3}","[{void f1}]"); }
	@Test public void test_11363() { checkNotSubtype("{{int f2} f2,int f3}","[{void f2}]"); }
	@Test public void test_11364() { checkNotSubtype("{{int f2} f2,int f3}","[{any f1}]"); }
	@Test public void test_11365() { checkNotSubtype("{{int f2} f2,int f3}","[{any f2}]"); }
	@Test public void test_11366() { checkNotSubtype("{{int f2} f2,int f3}","[{null f1}]"); }
	@Test public void test_11367() { checkNotSubtype("{{int f2} f2,int f3}","[{null f2}]"); }
	@Test public void test_11368() { checkNotSubtype("{{int f2} f2,int f3}","[{int f1}]"); }
	@Test public void test_11369() { checkNotSubtype("{{int f2} f2,int f3}","[{int f2}]"); }
	@Test public void test_11370() { checkIsSubtype("{{int f2} f2,int f3}","{void f1,void f2}"); }
	@Test public void test_11371() { checkIsSubtype("{{int f2} f2,int f3}","{void f2,void f3}"); }
	@Test public void test_11372() { checkIsSubtype("{{int f2} f2,int f3}","{void f1,any f2}"); }
	@Test public void test_11373() { checkIsSubtype("{{int f2} f2,int f3}","{void f2,any f3}"); }
	@Test public void test_11374() { checkIsSubtype("{{int f2} f2,int f3}","{void f1,null f2}"); }
	@Test public void test_11375() { checkIsSubtype("{{int f2} f2,int f3}","{void f2,null f3}"); }
	@Test public void test_11376() { checkIsSubtype("{{int f2} f2,int f3}","{void f1,int f2}"); }
	@Test public void test_11377() { checkIsSubtype("{{int f2} f2,int f3}","{void f2,int f3}"); }
	@Test public void test_11378() { checkIsSubtype("{{int f2} f2,int f3}","{any f1,void f2}"); }
	@Test public void test_11379() { checkIsSubtype("{{int f2} f2,int f3}","{any f2,void f3}"); }
	@Test public void test_11380() { checkNotSubtype("{{int f2} f2,int f3}","{any f1,any f2}"); }
	@Test public void test_11381() { checkNotSubtype("{{int f2} f2,int f3}","{any f2,any f3}"); }
	@Test public void test_11382() { checkNotSubtype("{{int f2} f2,int f3}","{any f1,null f2}"); }
	@Test public void test_11383() { checkNotSubtype("{{int f2} f2,int f3}","{any f2,null f3}"); }
	@Test public void test_11384() { checkNotSubtype("{{int f2} f2,int f3}","{any f1,int f2}"); }
	@Test public void test_11385() { checkNotSubtype("{{int f2} f2,int f3}","{any f2,int f3}"); }
	@Test public void test_11386() { checkIsSubtype("{{int f2} f2,int f3}","{null f1,void f2}"); }
	@Test public void test_11387() { checkIsSubtype("{{int f2} f2,int f3}","{null f2,void f3}"); }
	@Test public void test_11388() { checkNotSubtype("{{int f2} f2,int f3}","{null f1,any f2}"); }
	@Test public void test_11389() { checkNotSubtype("{{int f2} f2,int f3}","{null f2,any f3}"); }
	@Test public void test_11390() { checkNotSubtype("{{int f2} f2,int f3}","{null f1,null f2}"); }
	@Test public void test_11391() { checkNotSubtype("{{int f2} f2,int f3}","{null f2,null f3}"); }
	@Test public void test_11392() { checkNotSubtype("{{int f2} f2,int f3}","{null f1,int f2}"); }
	@Test public void test_11393() { checkNotSubtype("{{int f2} f2,int f3}","{null f2,int f3}"); }
	@Test public void test_11394() { checkIsSubtype("{{int f2} f2,int f3}","{int f1,void f2}"); }
	@Test public void test_11395() { checkIsSubtype("{{int f2} f2,int f3}","{int f2,void f3}"); }
	@Test public void test_11396() { checkNotSubtype("{{int f2} f2,int f3}","{int f1,any f2}"); }
	@Test public void test_11397() { checkNotSubtype("{{int f2} f2,int f3}","{int f2,any f3}"); }
	@Test public void test_11398() { checkNotSubtype("{{int f2} f2,int f3}","{int f1,null f2}"); }
	@Test public void test_11399() { checkNotSubtype("{{int f2} f2,int f3}","{int f2,null f3}"); }
	@Test public void test_11400() { checkNotSubtype("{{int f2} f2,int f3}","{int f1,int f2}"); }
	@Test public void test_11401() { checkNotSubtype("{{int f2} f2,int f3}","{int f2,int f3}"); }
	@Test public void test_11402() { checkNotSubtype("{{int f2} f2,int f3}","{[void] f1}"); }
	@Test public void test_11403() { checkNotSubtype("{{int f2} f2,int f3}","{[void] f2}"); }
	@Test public void test_11404() { checkIsSubtype("{{int f2} f2,int f3}","{[void] f1,void f2}"); }
	@Test public void test_11405() { checkIsSubtype("{{int f2} f2,int f3}","{[void] f2,void f3}"); }
	@Test public void test_11406() { checkNotSubtype("{{int f2} f2,int f3}","{[any] f1}"); }
	@Test public void test_11407() { checkNotSubtype("{{int f2} f2,int f3}","{[any] f2}"); }
	@Test public void test_11408() { checkNotSubtype("{{int f2} f2,int f3}","{[any] f1,any f2}"); }
	@Test public void test_11409() { checkNotSubtype("{{int f2} f2,int f3}","{[any] f2,any f3}"); }
	@Test public void test_11410() { checkNotSubtype("{{int f2} f2,int f3}","{[null] f1}"); }
	@Test public void test_11411() { checkNotSubtype("{{int f2} f2,int f3}","{[null] f2}"); }
	@Test public void test_11412() { checkNotSubtype("{{int f2} f2,int f3}","{[null] f1,null f2}"); }
	@Test public void test_11413() { checkNotSubtype("{{int f2} f2,int f3}","{[null] f2,null f3}"); }
	@Test public void test_11414() { checkNotSubtype("{{int f2} f2,int f3}","{[int] f1}"); }
	@Test public void test_11415() { checkNotSubtype("{{int f2} f2,int f3}","{[int] f2}"); }
	@Test public void test_11416() { checkNotSubtype("{{int f2} f2,int f3}","{[int] f1,int f2}"); }
	@Test public void test_11417() { checkNotSubtype("{{int f2} f2,int f3}","{[int] f2,int f3}"); }
	@Test public void test_11418() { checkIsSubtype("{{int f2} f2,int f3}","{{void f1} f1}"); }
	@Test public void test_11419() { checkIsSubtype("{{int f2} f2,int f3}","{{void f2} f1}"); }
	@Test public void test_11420() { checkIsSubtype("{{int f2} f2,int f3}","{{void f1} f2}"); }
	@Test public void test_11421() { checkIsSubtype("{{int f2} f2,int f3}","{{void f2} f2}"); }
	@Test public void test_11422() { checkIsSubtype("{{int f2} f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_11423() { checkIsSubtype("{{int f2} f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_11424() { checkIsSubtype("{{int f2} f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_11425() { checkIsSubtype("{{int f2} f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_11426() { checkNotSubtype("{{int f2} f2,int f3}","{{any f1} f1}"); }
	@Test public void test_11427() { checkNotSubtype("{{int f2} f2,int f3}","{{any f2} f1}"); }
	@Test public void test_11428() { checkNotSubtype("{{int f2} f2,int f3}","{{any f1} f2}"); }
	@Test public void test_11429() { checkNotSubtype("{{int f2} f2,int f3}","{{any f2} f2}"); }
	@Test public void test_11430() { checkNotSubtype("{{int f2} f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_11431() { checkNotSubtype("{{int f2} f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_11432() { checkNotSubtype("{{int f2} f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_11433() { checkNotSubtype("{{int f2} f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_11434() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f1}"); }
	@Test public void test_11435() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f1}"); }
	@Test public void test_11436() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f2}"); }
	@Test public void test_11437() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f2}"); }
	@Test public void test_11438() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_11439() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_11440() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_11441() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_11442() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f1}"); }
	@Test public void test_11443() { checkNotSubtype("{{int f2} f2,int f3}","{{int f2} f1}"); }
	@Test public void test_11444() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f2}"); }
	@Test public void test_11445() { checkNotSubtype("{{int f2} f2,int f3}","{{int f2} f2}"); }
	@Test public void test_11446() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_11447() { checkNotSubtype("{{int f2} f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_11448() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_11449() { checkIsSubtype("{{int f2} f2,int f3}","{{int f2} f2,int f3}"); }

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
