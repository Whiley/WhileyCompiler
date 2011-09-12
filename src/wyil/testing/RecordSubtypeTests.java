// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class RecordSubtypeTests {
	@Test public void test_1() { checkIsSubtype("any","any"); }
	@Test public void test_2() { checkIsSubtype("any","null"); }
	@Test public void test_3() { checkIsSubtype("any","int"); }
	@Test public void test_4() { checkIsSubtype("any","{void f1}"); }
	@Test public void test_5() { checkIsSubtype("any","{void f2}"); }
	@Test public void test_6() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_7() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_8() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_9() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_10() { checkIsSubtype("any","{int f1}"); }
	@Test public void test_11() { checkIsSubtype("any","{int f2}"); }
	@Test public void test_12() { checkIsSubtype("any","{void f1,void f2}"); }
	@Test public void test_13() { checkIsSubtype("any","{void f2,void f3}"); }
	@Test public void test_14() { checkIsSubtype("any","{void f1,any f2}"); }
	@Test public void test_15() { checkIsSubtype("any","{void f2,any f3}"); }
	@Test public void test_16() { checkIsSubtype("any","{void f1,null f2}"); }
	@Test public void test_17() { checkIsSubtype("any","{void f2,null f3}"); }
	@Test public void test_18() { checkIsSubtype("any","{void f1,int f2}"); }
	@Test public void test_19() { checkIsSubtype("any","{void f2,int f3}"); }
	@Test public void test_20() { checkIsSubtype("any","{any f1,void f2}"); }
	@Test public void test_21() { checkIsSubtype("any","{any f2,void f3}"); }
	@Test public void test_22() { checkIsSubtype("any","{any f1,any f2}"); }
	@Test public void test_23() { checkIsSubtype("any","{any f2,any f3}"); }
	@Test public void test_24() { checkIsSubtype("any","{any f1,null f2}"); }
	@Test public void test_25() { checkIsSubtype("any","{any f2,null f3}"); }
	@Test public void test_26() { checkIsSubtype("any","{any f1,int f2}"); }
	@Test public void test_27() { checkIsSubtype("any","{any f2,int f3}"); }
	@Test public void test_28() { checkIsSubtype("any","{null f1,void f2}"); }
	@Test public void test_29() { checkIsSubtype("any","{null f2,void f3}"); }
	@Test public void test_30() { checkIsSubtype("any","{null f1,any f2}"); }
	@Test public void test_31() { checkIsSubtype("any","{null f2,any f3}"); }
	@Test public void test_32() { checkIsSubtype("any","{null f1,null f2}"); }
	@Test public void test_33() { checkIsSubtype("any","{null f2,null f3}"); }
	@Test public void test_34() { checkIsSubtype("any","{null f1,int f2}"); }
	@Test public void test_35() { checkIsSubtype("any","{null f2,int f3}"); }
	@Test public void test_36() { checkIsSubtype("any","{int f1,void f2}"); }
	@Test public void test_37() { checkIsSubtype("any","{int f2,void f3}"); }
	@Test public void test_38() { checkIsSubtype("any","{int f1,any f2}"); }
	@Test public void test_39() { checkIsSubtype("any","{int f2,any f3}"); }
	@Test public void test_40() { checkIsSubtype("any","{int f1,null f2}"); }
	@Test public void test_41() { checkIsSubtype("any","{int f2,null f3}"); }
	@Test public void test_42() { checkIsSubtype("any","{int f1,int f2}"); }
	@Test public void test_43() { checkIsSubtype("any","{int f2,int f3}"); }
	@Test public void test_44() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_45() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_46() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_47() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_48() { checkIsSubtype("any","{{void f1} f1,void f2}"); }
	@Test public void test_49() { checkIsSubtype("any","{{void f2} f1,void f2}"); }
	@Test public void test_50() { checkIsSubtype("any","{{void f1} f2,void f3}"); }
	@Test public void test_51() { checkIsSubtype("any","{{void f2} f2,void f3}"); }
	@Test public void test_52() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_53() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_54() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_55() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_56() { checkIsSubtype("any","{{any f1} f1,any f2}"); }
	@Test public void test_57() { checkIsSubtype("any","{{any f2} f1,any f2}"); }
	@Test public void test_58() { checkIsSubtype("any","{{any f1} f2,any f3}"); }
	@Test public void test_59() { checkIsSubtype("any","{{any f2} f2,any f3}"); }
	@Test public void test_60() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_61() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_62() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_63() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_64() { checkIsSubtype("any","{{null f1} f1,null f2}"); }
	@Test public void test_65() { checkIsSubtype("any","{{null f2} f1,null f2}"); }
	@Test public void test_66() { checkIsSubtype("any","{{null f1} f2,null f3}"); }
	@Test public void test_67() { checkIsSubtype("any","{{null f2} f2,null f3}"); }
	@Test public void test_68() { checkIsSubtype("any","{{int f1} f1}"); }
	@Test public void test_69() { checkIsSubtype("any","{{int f2} f1}"); }
	@Test public void test_70() { checkIsSubtype("any","{{int f1} f2}"); }
	@Test public void test_71() { checkIsSubtype("any","{{int f2} f2}"); }
	@Test public void test_72() { checkIsSubtype("any","{{int f1} f1,int f2}"); }
	@Test public void test_73() { checkIsSubtype("any","{{int f2} f1,int f2}"); }
	@Test public void test_74() { checkIsSubtype("any","{{int f1} f2,int f3}"); }
	@Test public void test_75() { checkIsSubtype("any","{{int f2} f2,int f3}"); }
	@Test public void test_76() { checkNotSubtype("null","any"); }
	@Test public void test_77() { checkIsSubtype("null","null"); }
	@Test public void test_78() { checkNotSubtype("null","int"); }
	@Test public void test_79() { checkIsSubtype("null","{void f1}"); }
	@Test public void test_80() { checkIsSubtype("null","{void f2}"); }
	@Test public void test_81() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_82() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_83() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_84() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_85() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_86() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_87() { checkIsSubtype("null","{void f1,void f2}"); }
	@Test public void test_88() { checkIsSubtype("null","{void f2,void f3}"); }
	@Test public void test_89() { checkIsSubtype("null","{void f1,any f2}"); }
	@Test public void test_90() { checkIsSubtype("null","{void f2,any f3}"); }
	@Test public void test_91() { checkIsSubtype("null","{void f1,null f2}"); }
	@Test public void test_92() { checkIsSubtype("null","{void f2,null f3}"); }
	@Test public void test_93() { checkIsSubtype("null","{void f1,int f2}"); }
	@Test public void test_94() { checkIsSubtype("null","{void f2,int f3}"); }
	@Test public void test_95() { checkIsSubtype("null","{any f1,void f2}"); }
	@Test public void test_96() { checkIsSubtype("null","{any f2,void f3}"); }
	@Test public void test_97() { checkNotSubtype("null","{any f1,any f2}"); }
	@Test public void test_98() { checkNotSubtype("null","{any f2,any f3}"); }
	@Test public void test_99() { checkNotSubtype("null","{any f1,null f2}"); }
	@Test public void test_100() { checkNotSubtype("null","{any f2,null f3}"); }
	@Test public void test_101() { checkNotSubtype("null","{any f1,int f2}"); }
	@Test public void test_102() { checkNotSubtype("null","{any f2,int f3}"); }
	@Test public void test_103() { checkIsSubtype("null","{null f1,void f2}"); }
	@Test public void test_104() { checkIsSubtype("null","{null f2,void f3}"); }
	@Test public void test_105() { checkNotSubtype("null","{null f1,any f2}"); }
	@Test public void test_106() { checkNotSubtype("null","{null f2,any f3}"); }
	@Test public void test_107() { checkNotSubtype("null","{null f1,null f2}"); }
	@Test public void test_108() { checkNotSubtype("null","{null f2,null f3}"); }
	@Test public void test_109() { checkNotSubtype("null","{null f1,int f2}"); }
	@Test public void test_110() { checkNotSubtype("null","{null f2,int f3}"); }
	@Test public void test_111() { checkIsSubtype("null","{int f1,void f2}"); }
	@Test public void test_112() { checkIsSubtype("null","{int f2,void f3}"); }
	@Test public void test_113() { checkNotSubtype("null","{int f1,any f2}"); }
	@Test public void test_114() { checkNotSubtype("null","{int f2,any f3}"); }
	@Test public void test_115() { checkNotSubtype("null","{int f1,null f2}"); }
	@Test public void test_116() { checkNotSubtype("null","{int f2,null f3}"); }
	@Test public void test_117() { checkNotSubtype("null","{int f1,int f2}"); }
	@Test public void test_118() { checkNotSubtype("null","{int f2,int f3}"); }
	@Test public void test_119() { checkIsSubtype("null","{{void f1} f1}"); }
	@Test public void test_120() { checkIsSubtype("null","{{void f2} f1}"); }
	@Test public void test_121() { checkIsSubtype("null","{{void f1} f2}"); }
	@Test public void test_122() { checkIsSubtype("null","{{void f2} f2}"); }
	@Test public void test_123() { checkIsSubtype("null","{{void f1} f1,void f2}"); }
	@Test public void test_124() { checkIsSubtype("null","{{void f2} f1,void f2}"); }
	@Test public void test_125() { checkIsSubtype("null","{{void f1} f2,void f3}"); }
	@Test public void test_126() { checkIsSubtype("null","{{void f2} f2,void f3}"); }
	@Test public void test_127() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_128() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_129() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_130() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_131() { checkNotSubtype("null","{{any f1} f1,any f2}"); }
	@Test public void test_132() { checkNotSubtype("null","{{any f2} f1,any f2}"); }
	@Test public void test_133() { checkNotSubtype("null","{{any f1} f2,any f3}"); }
	@Test public void test_134() { checkNotSubtype("null","{{any f2} f2,any f3}"); }
	@Test public void test_135() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_136() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_137() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_138() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_139() { checkNotSubtype("null","{{null f1} f1,null f2}"); }
	@Test public void test_140() { checkNotSubtype("null","{{null f2} f1,null f2}"); }
	@Test public void test_141() { checkNotSubtype("null","{{null f1} f2,null f3}"); }
	@Test public void test_142() { checkNotSubtype("null","{{null f2} f2,null f3}"); }
	@Test public void test_143() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_144() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_145() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_146() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_147() { checkNotSubtype("null","{{int f1} f1,int f2}"); }
	@Test public void test_148() { checkNotSubtype("null","{{int f2} f1,int f2}"); }
	@Test public void test_149() { checkNotSubtype("null","{{int f1} f2,int f3}"); }
	@Test public void test_150() { checkNotSubtype("null","{{int f2} f2,int f3}"); }
	@Test public void test_151() { checkNotSubtype("int","any"); }
	@Test public void test_152() { checkNotSubtype("int","null"); }
	@Test public void test_153() { checkIsSubtype("int","int"); }
	@Test public void test_154() { checkIsSubtype("int","{void f1}"); }
	@Test public void test_155() { checkIsSubtype("int","{void f2}"); }
	@Test public void test_156() { checkNotSubtype("int","{any f1}"); }
	@Test public void test_157() { checkNotSubtype("int","{any f2}"); }
	@Test public void test_158() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_159() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_160() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_161() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_162() { checkIsSubtype("int","{void f1,void f2}"); }
	@Test public void test_163() { checkIsSubtype("int","{void f2,void f3}"); }
	@Test public void test_164() { checkIsSubtype("int","{void f1,any f2}"); }
	@Test public void test_165() { checkIsSubtype("int","{void f2,any f3}"); }
	@Test public void test_166() { checkIsSubtype("int","{void f1,null f2}"); }
	@Test public void test_167() { checkIsSubtype("int","{void f2,null f3}"); }
	@Test public void test_168() { checkIsSubtype("int","{void f1,int f2}"); }
	@Test public void test_169() { checkIsSubtype("int","{void f2,int f3}"); }
	@Test public void test_170() { checkIsSubtype("int","{any f1,void f2}"); }
	@Test public void test_171() { checkIsSubtype("int","{any f2,void f3}"); }
	@Test public void test_172() { checkNotSubtype("int","{any f1,any f2}"); }
	@Test public void test_173() { checkNotSubtype("int","{any f2,any f3}"); }
	@Test public void test_174() { checkNotSubtype("int","{any f1,null f2}"); }
	@Test public void test_175() { checkNotSubtype("int","{any f2,null f3}"); }
	@Test public void test_176() { checkNotSubtype("int","{any f1,int f2}"); }
	@Test public void test_177() { checkNotSubtype("int","{any f2,int f3}"); }
	@Test public void test_178() { checkIsSubtype("int","{null f1,void f2}"); }
	@Test public void test_179() { checkIsSubtype("int","{null f2,void f3}"); }
	@Test public void test_180() { checkNotSubtype("int","{null f1,any f2}"); }
	@Test public void test_181() { checkNotSubtype("int","{null f2,any f3}"); }
	@Test public void test_182() { checkNotSubtype("int","{null f1,null f2}"); }
	@Test public void test_183() { checkNotSubtype("int","{null f2,null f3}"); }
	@Test public void test_184() { checkNotSubtype("int","{null f1,int f2}"); }
	@Test public void test_185() { checkNotSubtype("int","{null f2,int f3}"); }
	@Test public void test_186() { checkIsSubtype("int","{int f1,void f2}"); }
	@Test public void test_187() { checkIsSubtype("int","{int f2,void f3}"); }
	@Test public void test_188() { checkNotSubtype("int","{int f1,any f2}"); }
	@Test public void test_189() { checkNotSubtype("int","{int f2,any f3}"); }
	@Test public void test_190() { checkNotSubtype("int","{int f1,null f2}"); }
	@Test public void test_191() { checkNotSubtype("int","{int f2,null f3}"); }
	@Test public void test_192() { checkNotSubtype("int","{int f1,int f2}"); }
	@Test public void test_193() { checkNotSubtype("int","{int f2,int f3}"); }
	@Test public void test_194() { checkIsSubtype("int","{{void f1} f1}"); }
	@Test public void test_195() { checkIsSubtype("int","{{void f2} f1}"); }
	@Test public void test_196() { checkIsSubtype("int","{{void f1} f2}"); }
	@Test public void test_197() { checkIsSubtype("int","{{void f2} f2}"); }
	@Test public void test_198() { checkIsSubtype("int","{{void f1} f1,void f2}"); }
	@Test public void test_199() { checkIsSubtype("int","{{void f2} f1,void f2}"); }
	@Test public void test_200() { checkIsSubtype("int","{{void f1} f2,void f3}"); }
	@Test public void test_201() { checkIsSubtype("int","{{void f2} f2,void f3}"); }
	@Test public void test_202() { checkNotSubtype("int","{{any f1} f1}"); }
	@Test public void test_203() { checkNotSubtype("int","{{any f2} f1}"); }
	@Test public void test_204() { checkNotSubtype("int","{{any f1} f2}"); }
	@Test public void test_205() { checkNotSubtype("int","{{any f2} f2}"); }
	@Test public void test_206() { checkNotSubtype("int","{{any f1} f1,any f2}"); }
	@Test public void test_207() { checkNotSubtype("int","{{any f2} f1,any f2}"); }
	@Test public void test_208() { checkNotSubtype("int","{{any f1} f2,any f3}"); }
	@Test public void test_209() { checkNotSubtype("int","{{any f2} f2,any f3}"); }
	@Test public void test_210() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_211() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_212() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_213() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_214() { checkNotSubtype("int","{{null f1} f1,null f2}"); }
	@Test public void test_215() { checkNotSubtype("int","{{null f2} f1,null f2}"); }
	@Test public void test_216() { checkNotSubtype("int","{{null f1} f2,null f3}"); }
	@Test public void test_217() { checkNotSubtype("int","{{null f2} f2,null f3}"); }
	@Test public void test_218() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_219() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_220() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_221() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_222() { checkNotSubtype("int","{{int f1} f1,int f2}"); }
	@Test public void test_223() { checkNotSubtype("int","{{int f2} f1,int f2}"); }
	@Test public void test_224() { checkNotSubtype("int","{{int f1} f2,int f3}"); }
	@Test public void test_225() { checkNotSubtype("int","{{int f2} f2,int f3}"); }
	@Test public void test_226() { checkNotSubtype("{void f1}","any"); }
	@Test public void test_227() { checkNotSubtype("{void f1}","null"); }
	@Test public void test_228() { checkNotSubtype("{void f1}","int"); }
	@Test public void test_229() { checkIsSubtype("{void f1}","{void f1}"); }
	@Test public void test_230() { checkIsSubtype("{void f1}","{void f2}"); }
	@Test public void test_231() { checkNotSubtype("{void f1}","{any f1}"); }
	@Test public void test_232() { checkNotSubtype("{void f1}","{any f2}"); }
	@Test public void test_233() { checkNotSubtype("{void f1}","{null f1}"); }
	@Test public void test_234() { checkNotSubtype("{void f1}","{null f2}"); }
	@Test public void test_235() { checkNotSubtype("{void f1}","{int f1}"); }
	@Test public void test_236() { checkNotSubtype("{void f1}","{int f2}"); }
	@Test public void test_237() { checkIsSubtype("{void f1}","{void f1,void f2}"); }
	@Test public void test_238() { checkIsSubtype("{void f1}","{void f2,void f3}"); }
	@Test public void test_239() { checkIsSubtype("{void f1}","{void f1,any f2}"); }
	@Test public void test_240() { checkIsSubtype("{void f1}","{void f2,any f3}"); }
	@Test public void test_241() { checkIsSubtype("{void f1}","{void f1,null f2}"); }
	@Test public void test_242() { checkIsSubtype("{void f1}","{void f2,null f3}"); }
	@Test public void test_243() { checkIsSubtype("{void f1}","{void f1,int f2}"); }
	@Test public void test_244() { checkIsSubtype("{void f1}","{void f2,int f3}"); }
	@Test public void test_245() { checkIsSubtype("{void f1}","{any f1,void f2}"); }
	@Test public void test_246() { checkIsSubtype("{void f1}","{any f2,void f3}"); }
	@Test public void test_247() { checkNotSubtype("{void f1}","{any f1,any f2}"); }
	@Test public void test_248() { checkNotSubtype("{void f1}","{any f2,any f3}"); }
	@Test public void test_249() { checkNotSubtype("{void f1}","{any f1,null f2}"); }
	@Test public void test_250() { checkNotSubtype("{void f1}","{any f2,null f3}"); }
	@Test public void test_251() { checkNotSubtype("{void f1}","{any f1,int f2}"); }
	@Test public void test_252() { checkNotSubtype("{void f1}","{any f2,int f3}"); }
	@Test public void test_253() { checkIsSubtype("{void f1}","{null f1,void f2}"); }
	@Test public void test_254() { checkIsSubtype("{void f1}","{null f2,void f3}"); }
	@Test public void test_255() { checkNotSubtype("{void f1}","{null f1,any f2}"); }
	@Test public void test_256() { checkNotSubtype("{void f1}","{null f2,any f3}"); }
	@Test public void test_257() { checkNotSubtype("{void f1}","{null f1,null f2}"); }
	@Test public void test_258() { checkNotSubtype("{void f1}","{null f2,null f3}"); }
	@Test public void test_259() { checkNotSubtype("{void f1}","{null f1,int f2}"); }
	@Test public void test_260() { checkNotSubtype("{void f1}","{null f2,int f3}"); }
	@Test public void test_261() { checkIsSubtype("{void f1}","{int f1,void f2}"); }
	@Test public void test_262() { checkIsSubtype("{void f1}","{int f2,void f3}"); }
	@Test public void test_263() { checkNotSubtype("{void f1}","{int f1,any f2}"); }
	@Test public void test_264() { checkNotSubtype("{void f1}","{int f2,any f3}"); }
	@Test public void test_265() { checkNotSubtype("{void f1}","{int f1,null f2}"); }
	@Test public void test_266() { checkNotSubtype("{void f1}","{int f2,null f3}"); }
	@Test public void test_267() { checkNotSubtype("{void f1}","{int f1,int f2}"); }
	@Test public void test_268() { checkNotSubtype("{void f1}","{int f2,int f3}"); }
	@Test public void test_269() { checkIsSubtype("{void f1}","{{void f1} f1}"); }
	@Test public void test_270() { checkIsSubtype("{void f1}","{{void f2} f1}"); }
	@Test public void test_271() { checkIsSubtype("{void f1}","{{void f1} f2}"); }
	@Test public void test_272() { checkIsSubtype("{void f1}","{{void f2} f2}"); }
	@Test public void test_273() { checkIsSubtype("{void f1}","{{void f1} f1,void f2}"); }
	@Test public void test_274() { checkIsSubtype("{void f1}","{{void f2} f1,void f2}"); }
	@Test public void test_275() { checkIsSubtype("{void f1}","{{void f1} f2,void f3}"); }
	@Test public void test_276() { checkIsSubtype("{void f1}","{{void f2} f2,void f3}"); }
	@Test public void test_277() { checkNotSubtype("{void f1}","{{any f1} f1}"); }
	@Test public void test_278() { checkNotSubtype("{void f1}","{{any f2} f1}"); }
	@Test public void test_279() { checkNotSubtype("{void f1}","{{any f1} f2}"); }
	@Test public void test_280() { checkNotSubtype("{void f1}","{{any f2} f2}"); }
	@Test public void test_281() { checkNotSubtype("{void f1}","{{any f1} f1,any f2}"); }
	@Test public void test_282() { checkNotSubtype("{void f1}","{{any f2} f1,any f2}"); }
	@Test public void test_283() { checkNotSubtype("{void f1}","{{any f1} f2,any f3}"); }
	@Test public void test_284() { checkNotSubtype("{void f1}","{{any f2} f2,any f3}"); }
	@Test public void test_285() { checkNotSubtype("{void f1}","{{null f1} f1}"); }
	@Test public void test_286() { checkNotSubtype("{void f1}","{{null f2} f1}"); }
	@Test public void test_287() { checkNotSubtype("{void f1}","{{null f1} f2}"); }
	@Test public void test_288() { checkNotSubtype("{void f1}","{{null f2} f2}"); }
	@Test public void test_289() { checkNotSubtype("{void f1}","{{null f1} f1,null f2}"); }
	@Test public void test_290() { checkNotSubtype("{void f1}","{{null f2} f1,null f2}"); }
	@Test public void test_291() { checkNotSubtype("{void f1}","{{null f1} f2,null f3}"); }
	@Test public void test_292() { checkNotSubtype("{void f1}","{{null f2} f2,null f3}"); }
	@Test public void test_293() { checkNotSubtype("{void f1}","{{int f1} f1}"); }
	@Test public void test_294() { checkNotSubtype("{void f1}","{{int f2} f1}"); }
	@Test public void test_295() { checkNotSubtype("{void f1}","{{int f1} f2}"); }
	@Test public void test_296() { checkNotSubtype("{void f1}","{{int f2} f2}"); }
	@Test public void test_297() { checkNotSubtype("{void f1}","{{int f1} f1,int f2}"); }
	@Test public void test_298() { checkNotSubtype("{void f1}","{{int f2} f1,int f2}"); }
	@Test public void test_299() { checkNotSubtype("{void f1}","{{int f1} f2,int f3}"); }
	@Test public void test_300() { checkNotSubtype("{void f1}","{{int f2} f2,int f3}"); }
	@Test public void test_301() { checkNotSubtype("{void f2}","any"); }
	@Test public void test_302() { checkNotSubtype("{void f2}","null"); }
	@Test public void test_303() { checkNotSubtype("{void f2}","int"); }
	@Test public void test_304() { checkIsSubtype("{void f2}","{void f1}"); }
	@Test public void test_305() { checkIsSubtype("{void f2}","{void f2}"); }
	@Test public void test_306() { checkNotSubtype("{void f2}","{any f1}"); }
	@Test public void test_307() { checkNotSubtype("{void f2}","{any f2}"); }
	@Test public void test_308() { checkNotSubtype("{void f2}","{null f1}"); }
	@Test public void test_309() { checkNotSubtype("{void f2}","{null f2}"); }
	@Test public void test_310() { checkNotSubtype("{void f2}","{int f1}"); }
	@Test public void test_311() { checkNotSubtype("{void f2}","{int f2}"); }
	@Test public void test_312() { checkIsSubtype("{void f2}","{void f1,void f2}"); }
	@Test public void test_313() { checkIsSubtype("{void f2}","{void f2,void f3}"); }
	@Test public void test_314() { checkIsSubtype("{void f2}","{void f1,any f2}"); }
	@Test public void test_315() { checkIsSubtype("{void f2}","{void f2,any f3}"); }
	@Test public void test_316() { checkIsSubtype("{void f2}","{void f1,null f2}"); }
	@Test public void test_317() { checkIsSubtype("{void f2}","{void f2,null f3}"); }
	@Test public void test_318() { checkIsSubtype("{void f2}","{void f1,int f2}"); }
	@Test public void test_319() { checkIsSubtype("{void f2}","{void f2,int f3}"); }
	@Test public void test_320() { checkIsSubtype("{void f2}","{any f1,void f2}"); }
	@Test public void test_321() { checkIsSubtype("{void f2}","{any f2,void f3}"); }
	@Test public void test_322() { checkNotSubtype("{void f2}","{any f1,any f2}"); }
	@Test public void test_323() { checkNotSubtype("{void f2}","{any f2,any f3}"); }
	@Test public void test_324() { checkNotSubtype("{void f2}","{any f1,null f2}"); }
	@Test public void test_325() { checkNotSubtype("{void f2}","{any f2,null f3}"); }
	@Test public void test_326() { checkNotSubtype("{void f2}","{any f1,int f2}"); }
	@Test public void test_327() { checkNotSubtype("{void f2}","{any f2,int f3}"); }
	@Test public void test_328() { checkIsSubtype("{void f2}","{null f1,void f2}"); }
	@Test public void test_329() { checkIsSubtype("{void f2}","{null f2,void f3}"); }
	@Test public void test_330() { checkNotSubtype("{void f2}","{null f1,any f2}"); }
	@Test public void test_331() { checkNotSubtype("{void f2}","{null f2,any f3}"); }
	@Test public void test_332() { checkNotSubtype("{void f2}","{null f1,null f2}"); }
	@Test public void test_333() { checkNotSubtype("{void f2}","{null f2,null f3}"); }
	@Test public void test_334() { checkNotSubtype("{void f2}","{null f1,int f2}"); }
	@Test public void test_335() { checkNotSubtype("{void f2}","{null f2,int f3}"); }
	@Test public void test_336() { checkIsSubtype("{void f2}","{int f1,void f2}"); }
	@Test public void test_337() { checkIsSubtype("{void f2}","{int f2,void f3}"); }
	@Test public void test_338() { checkNotSubtype("{void f2}","{int f1,any f2}"); }
	@Test public void test_339() { checkNotSubtype("{void f2}","{int f2,any f3}"); }
	@Test public void test_340() { checkNotSubtype("{void f2}","{int f1,null f2}"); }
	@Test public void test_341() { checkNotSubtype("{void f2}","{int f2,null f3}"); }
	@Test public void test_342() { checkNotSubtype("{void f2}","{int f1,int f2}"); }
	@Test public void test_343() { checkNotSubtype("{void f2}","{int f2,int f3}"); }
	@Test public void test_344() { checkIsSubtype("{void f2}","{{void f1} f1}"); }
	@Test public void test_345() { checkIsSubtype("{void f2}","{{void f2} f1}"); }
	@Test public void test_346() { checkIsSubtype("{void f2}","{{void f1} f2}"); }
	@Test public void test_347() { checkIsSubtype("{void f2}","{{void f2} f2}"); }
	@Test public void test_348() { checkIsSubtype("{void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_349() { checkIsSubtype("{void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_350() { checkIsSubtype("{void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_351() { checkIsSubtype("{void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_352() { checkNotSubtype("{void f2}","{{any f1} f1}"); }
	@Test public void test_353() { checkNotSubtype("{void f2}","{{any f2} f1}"); }
	@Test public void test_354() { checkNotSubtype("{void f2}","{{any f1} f2}"); }
	@Test public void test_355() { checkNotSubtype("{void f2}","{{any f2} f2}"); }
	@Test public void test_356() { checkNotSubtype("{void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_357() { checkNotSubtype("{void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_358() { checkNotSubtype("{void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_359() { checkNotSubtype("{void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_360() { checkNotSubtype("{void f2}","{{null f1} f1}"); }
	@Test public void test_361() { checkNotSubtype("{void f2}","{{null f2} f1}"); }
	@Test public void test_362() { checkNotSubtype("{void f2}","{{null f1} f2}"); }
	@Test public void test_363() { checkNotSubtype("{void f2}","{{null f2} f2}"); }
	@Test public void test_364() { checkNotSubtype("{void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_365() { checkNotSubtype("{void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_366() { checkNotSubtype("{void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_367() { checkNotSubtype("{void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_368() { checkNotSubtype("{void f2}","{{int f1} f1}"); }
	@Test public void test_369() { checkNotSubtype("{void f2}","{{int f2} f1}"); }
	@Test public void test_370() { checkNotSubtype("{void f2}","{{int f1} f2}"); }
	@Test public void test_371() { checkNotSubtype("{void f2}","{{int f2} f2}"); }
	@Test public void test_372() { checkNotSubtype("{void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_373() { checkNotSubtype("{void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_374() { checkNotSubtype("{void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_375() { checkNotSubtype("{void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_376() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_377() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_378() { checkNotSubtype("{any f1}","int"); }
	@Test public void test_379() { checkIsSubtype("{any f1}","{void f1}"); }
	@Test public void test_380() { checkIsSubtype("{any f1}","{void f2}"); }
	@Test public void test_381() { checkIsSubtype("{any f1}","{any f1}"); }
	@Test public void test_382() { checkNotSubtype("{any f1}","{any f2}"); }
	@Test public void test_383() { checkIsSubtype("{any f1}","{null f1}"); }
	@Test public void test_384() { checkNotSubtype("{any f1}","{null f2}"); }
	@Test public void test_385() { checkIsSubtype("{any f1}","{int f1}"); }
	@Test public void test_386() { checkNotSubtype("{any f1}","{int f2}"); }
	@Test public void test_387() { checkIsSubtype("{any f1}","{void f1,void f2}"); }
	@Test public void test_388() { checkIsSubtype("{any f1}","{void f2,void f3}"); }
	@Test public void test_389() { checkIsSubtype("{any f1}","{void f1,any f2}"); }
	@Test public void test_390() { checkIsSubtype("{any f1}","{void f2,any f3}"); }
	@Test public void test_391() { checkIsSubtype("{any f1}","{void f1,null f2}"); }
	@Test public void test_392() { checkIsSubtype("{any f1}","{void f2,null f3}"); }
	@Test public void test_393() { checkIsSubtype("{any f1}","{void f1,int f2}"); }
	@Test public void test_394() { checkIsSubtype("{any f1}","{void f2,int f3}"); }
	@Test public void test_395() { checkIsSubtype("{any f1}","{any f1,void f2}"); }
	@Test public void test_396() { checkIsSubtype("{any f1}","{any f2,void f3}"); }
	@Test public void test_397() { checkNotSubtype("{any f1}","{any f1,any f2}"); }
	@Test public void test_398() { checkNotSubtype("{any f1}","{any f2,any f3}"); }
	@Test public void test_399() { checkNotSubtype("{any f1}","{any f1,null f2}"); }
	@Test public void test_400() { checkNotSubtype("{any f1}","{any f2,null f3}"); }
	@Test public void test_401() { checkNotSubtype("{any f1}","{any f1,int f2}"); }
	@Test public void test_402() { checkNotSubtype("{any f1}","{any f2,int f3}"); }
	@Test public void test_403() { checkIsSubtype("{any f1}","{null f1,void f2}"); }
	@Test public void test_404() { checkIsSubtype("{any f1}","{null f2,void f3}"); }
	@Test public void test_405() { checkNotSubtype("{any f1}","{null f1,any f2}"); }
	@Test public void test_406() { checkNotSubtype("{any f1}","{null f2,any f3}"); }
	@Test public void test_407() { checkNotSubtype("{any f1}","{null f1,null f2}"); }
	@Test public void test_408() { checkNotSubtype("{any f1}","{null f2,null f3}"); }
	@Test public void test_409() { checkNotSubtype("{any f1}","{null f1,int f2}"); }
	@Test public void test_410() { checkNotSubtype("{any f1}","{null f2,int f3}"); }
	@Test public void test_411() { checkIsSubtype("{any f1}","{int f1,void f2}"); }
	@Test public void test_412() { checkIsSubtype("{any f1}","{int f2,void f3}"); }
	@Test public void test_413() { checkNotSubtype("{any f1}","{int f1,any f2}"); }
	@Test public void test_414() { checkNotSubtype("{any f1}","{int f2,any f3}"); }
	@Test public void test_415() { checkNotSubtype("{any f1}","{int f1,null f2}"); }
	@Test public void test_416() { checkNotSubtype("{any f1}","{int f2,null f3}"); }
	@Test public void test_417() { checkNotSubtype("{any f1}","{int f1,int f2}"); }
	@Test public void test_418() { checkNotSubtype("{any f1}","{int f2,int f3}"); }
	@Test public void test_419() { checkIsSubtype("{any f1}","{{void f1} f1}"); }
	@Test public void test_420() { checkIsSubtype("{any f1}","{{void f2} f1}"); }
	@Test public void test_421() { checkIsSubtype("{any f1}","{{void f1} f2}"); }
	@Test public void test_422() { checkIsSubtype("{any f1}","{{void f2} f2}"); }
	@Test public void test_423() { checkIsSubtype("{any f1}","{{void f1} f1,void f2}"); }
	@Test public void test_424() { checkIsSubtype("{any f1}","{{void f2} f1,void f2}"); }
	@Test public void test_425() { checkIsSubtype("{any f1}","{{void f1} f2,void f3}"); }
	@Test public void test_426() { checkIsSubtype("{any f1}","{{void f2} f2,void f3}"); }
	@Test public void test_427() { checkIsSubtype("{any f1}","{{any f1} f1}"); }
	@Test public void test_428() { checkIsSubtype("{any f1}","{{any f2} f1}"); }
	@Test public void test_429() { checkNotSubtype("{any f1}","{{any f1} f2}"); }
	@Test public void test_430() { checkNotSubtype("{any f1}","{{any f2} f2}"); }
	@Test public void test_431() { checkNotSubtype("{any f1}","{{any f1} f1,any f2}"); }
	@Test public void test_432() { checkNotSubtype("{any f1}","{{any f2} f1,any f2}"); }
	@Test public void test_433() { checkNotSubtype("{any f1}","{{any f1} f2,any f3}"); }
	@Test public void test_434() { checkNotSubtype("{any f1}","{{any f2} f2,any f3}"); }
	@Test public void test_435() { checkIsSubtype("{any f1}","{{null f1} f1}"); }
	@Test public void test_436() { checkIsSubtype("{any f1}","{{null f2} f1}"); }
	@Test public void test_437() { checkNotSubtype("{any f1}","{{null f1} f2}"); }
	@Test public void test_438() { checkNotSubtype("{any f1}","{{null f2} f2}"); }
	@Test public void test_439() { checkNotSubtype("{any f1}","{{null f1} f1,null f2}"); }
	@Test public void test_440() { checkNotSubtype("{any f1}","{{null f2} f1,null f2}"); }
	@Test public void test_441() { checkNotSubtype("{any f1}","{{null f1} f2,null f3}"); }
	@Test public void test_442() { checkNotSubtype("{any f1}","{{null f2} f2,null f3}"); }
	@Test public void test_443() { checkIsSubtype("{any f1}","{{int f1} f1}"); }
	@Test public void test_444() { checkIsSubtype("{any f1}","{{int f2} f1}"); }
	@Test public void test_445() { checkNotSubtype("{any f1}","{{int f1} f2}"); }
	@Test public void test_446() { checkNotSubtype("{any f1}","{{int f2} f2}"); }
	@Test public void test_447() { checkNotSubtype("{any f1}","{{int f1} f1,int f2}"); }
	@Test public void test_448() { checkNotSubtype("{any f1}","{{int f2} f1,int f2}"); }
	@Test public void test_449() { checkNotSubtype("{any f1}","{{int f1} f2,int f3}"); }
	@Test public void test_450() { checkNotSubtype("{any f1}","{{int f2} f2,int f3}"); }
	@Test public void test_451() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_452() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_453() { checkNotSubtype("{any f2}","int"); }
	@Test public void test_454() { checkIsSubtype("{any f2}","{void f1}"); }
	@Test public void test_455() { checkIsSubtype("{any f2}","{void f2}"); }
	@Test public void test_456() { checkNotSubtype("{any f2}","{any f1}"); }
	@Test public void test_457() { checkIsSubtype("{any f2}","{any f2}"); }
	@Test public void test_458() { checkNotSubtype("{any f2}","{null f1}"); }
	@Test public void test_459() { checkIsSubtype("{any f2}","{null f2}"); }
	@Test public void test_460() { checkNotSubtype("{any f2}","{int f1}"); }
	@Test public void test_461() { checkIsSubtype("{any f2}","{int f2}"); }
	@Test public void test_462() { checkIsSubtype("{any f2}","{void f1,void f2}"); }
	@Test public void test_463() { checkIsSubtype("{any f2}","{void f2,void f3}"); }
	@Test public void test_464() { checkIsSubtype("{any f2}","{void f1,any f2}"); }
	@Test public void test_465() { checkIsSubtype("{any f2}","{void f2,any f3}"); }
	@Test public void test_466() { checkIsSubtype("{any f2}","{void f1,null f2}"); }
	@Test public void test_467() { checkIsSubtype("{any f2}","{void f2,null f3}"); }
	@Test public void test_468() { checkIsSubtype("{any f2}","{void f1,int f2}"); }
	@Test public void test_469() { checkIsSubtype("{any f2}","{void f2,int f3}"); }
	@Test public void test_470() { checkIsSubtype("{any f2}","{any f1,void f2}"); }
	@Test public void test_471() { checkIsSubtype("{any f2}","{any f2,void f3}"); }
	@Test public void test_472() { checkNotSubtype("{any f2}","{any f1,any f2}"); }
	@Test public void test_473() { checkNotSubtype("{any f2}","{any f2,any f3}"); }
	@Test public void test_474() { checkNotSubtype("{any f2}","{any f1,null f2}"); }
	@Test public void test_475() { checkNotSubtype("{any f2}","{any f2,null f3}"); }
	@Test public void test_476() { checkNotSubtype("{any f2}","{any f1,int f2}"); }
	@Test public void test_477() { checkNotSubtype("{any f2}","{any f2,int f3}"); }
	@Test public void test_478() { checkIsSubtype("{any f2}","{null f1,void f2}"); }
	@Test public void test_479() { checkIsSubtype("{any f2}","{null f2,void f3}"); }
	@Test public void test_480() { checkNotSubtype("{any f2}","{null f1,any f2}"); }
	@Test public void test_481() { checkNotSubtype("{any f2}","{null f2,any f3}"); }
	@Test public void test_482() { checkNotSubtype("{any f2}","{null f1,null f2}"); }
	@Test public void test_483() { checkNotSubtype("{any f2}","{null f2,null f3}"); }
	@Test public void test_484() { checkNotSubtype("{any f2}","{null f1,int f2}"); }
	@Test public void test_485() { checkNotSubtype("{any f2}","{null f2,int f3}"); }
	@Test public void test_486() { checkIsSubtype("{any f2}","{int f1,void f2}"); }
	@Test public void test_487() { checkIsSubtype("{any f2}","{int f2,void f3}"); }
	@Test public void test_488() { checkNotSubtype("{any f2}","{int f1,any f2}"); }
	@Test public void test_489() { checkNotSubtype("{any f2}","{int f2,any f3}"); }
	@Test public void test_490() { checkNotSubtype("{any f2}","{int f1,null f2}"); }
	@Test public void test_491() { checkNotSubtype("{any f2}","{int f2,null f3}"); }
	@Test public void test_492() { checkNotSubtype("{any f2}","{int f1,int f2}"); }
	@Test public void test_493() { checkNotSubtype("{any f2}","{int f2,int f3}"); }
	@Test public void test_494() { checkIsSubtype("{any f2}","{{void f1} f1}"); }
	@Test public void test_495() { checkIsSubtype("{any f2}","{{void f2} f1}"); }
	@Test public void test_496() { checkIsSubtype("{any f2}","{{void f1} f2}"); }
	@Test public void test_497() { checkIsSubtype("{any f2}","{{void f2} f2}"); }
	@Test public void test_498() { checkIsSubtype("{any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_499() { checkIsSubtype("{any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_500() { checkIsSubtype("{any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_501() { checkIsSubtype("{any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_502() { checkNotSubtype("{any f2}","{{any f1} f1}"); }
	@Test public void test_503() { checkNotSubtype("{any f2}","{{any f2} f1}"); }
	@Test public void test_504() { checkIsSubtype("{any f2}","{{any f1} f2}"); }
	@Test public void test_505() { checkIsSubtype("{any f2}","{{any f2} f2}"); }
	@Test public void test_506() { checkNotSubtype("{any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_507() { checkNotSubtype("{any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_508() { checkNotSubtype("{any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_509() { checkNotSubtype("{any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_510() { checkNotSubtype("{any f2}","{{null f1} f1}"); }
	@Test public void test_511() { checkNotSubtype("{any f2}","{{null f2} f1}"); }
	@Test public void test_512() { checkIsSubtype("{any f2}","{{null f1} f2}"); }
	@Test public void test_513() { checkIsSubtype("{any f2}","{{null f2} f2}"); }
	@Test public void test_514() { checkNotSubtype("{any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_515() { checkNotSubtype("{any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_516() { checkNotSubtype("{any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_517() { checkNotSubtype("{any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_518() { checkNotSubtype("{any f2}","{{int f1} f1}"); }
	@Test public void test_519() { checkNotSubtype("{any f2}","{{int f2} f1}"); }
	@Test public void test_520() { checkIsSubtype("{any f2}","{{int f1} f2}"); }
	@Test public void test_521() { checkIsSubtype("{any f2}","{{int f2} f2}"); }
	@Test public void test_522() { checkNotSubtype("{any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_523() { checkNotSubtype("{any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_524() { checkNotSubtype("{any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_525() { checkNotSubtype("{any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_526() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_527() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_528() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_529() { checkIsSubtype("{null f1}","{void f1}"); }
	@Test public void test_530() { checkIsSubtype("{null f1}","{void f2}"); }
	@Test public void test_531() { checkNotSubtype("{null f1}","{any f1}"); }
	@Test public void test_532() { checkNotSubtype("{null f1}","{any f2}"); }
	@Test public void test_533() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_534() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_535() { checkNotSubtype("{null f1}","{int f1}"); }
	@Test public void test_536() { checkNotSubtype("{null f1}","{int f2}"); }
	@Test public void test_537() { checkIsSubtype("{null f1}","{void f1,void f2}"); }
	@Test public void test_538() { checkIsSubtype("{null f1}","{void f2,void f3}"); }
	@Test public void test_539() { checkIsSubtype("{null f1}","{void f1,any f2}"); }
	@Test public void test_540() { checkIsSubtype("{null f1}","{void f2,any f3}"); }
	@Test public void test_541() { checkIsSubtype("{null f1}","{void f1,null f2}"); }
	@Test public void test_542() { checkIsSubtype("{null f1}","{void f2,null f3}"); }
	@Test public void test_543() { checkIsSubtype("{null f1}","{void f1,int f2}"); }
	@Test public void test_544() { checkIsSubtype("{null f1}","{void f2,int f3}"); }
	@Test public void test_545() { checkIsSubtype("{null f1}","{any f1,void f2}"); }
	@Test public void test_546() { checkIsSubtype("{null f1}","{any f2,void f3}"); }
	@Test public void test_547() { checkNotSubtype("{null f1}","{any f1,any f2}"); }
	@Test public void test_548() { checkNotSubtype("{null f1}","{any f2,any f3}"); }
	@Test public void test_549() { checkNotSubtype("{null f1}","{any f1,null f2}"); }
	@Test public void test_550() { checkNotSubtype("{null f1}","{any f2,null f3}"); }
	@Test public void test_551() { checkNotSubtype("{null f1}","{any f1,int f2}"); }
	@Test public void test_552() { checkNotSubtype("{null f1}","{any f2,int f3}"); }
	@Test public void test_553() { checkIsSubtype("{null f1}","{null f1,void f2}"); }
	@Test public void test_554() { checkIsSubtype("{null f1}","{null f2,void f3}"); }
	@Test public void test_555() { checkNotSubtype("{null f1}","{null f1,any f2}"); }
	@Test public void test_556() { checkNotSubtype("{null f1}","{null f2,any f3}"); }
	@Test public void test_557() { checkNotSubtype("{null f1}","{null f1,null f2}"); }
	@Test public void test_558() { checkNotSubtype("{null f1}","{null f2,null f3}"); }
	@Test public void test_559() { checkNotSubtype("{null f1}","{null f1,int f2}"); }
	@Test public void test_560() { checkNotSubtype("{null f1}","{null f2,int f3}"); }
	@Test public void test_561() { checkIsSubtype("{null f1}","{int f1,void f2}"); }
	@Test public void test_562() { checkIsSubtype("{null f1}","{int f2,void f3}"); }
	@Test public void test_563() { checkNotSubtype("{null f1}","{int f1,any f2}"); }
	@Test public void test_564() { checkNotSubtype("{null f1}","{int f2,any f3}"); }
	@Test public void test_565() { checkNotSubtype("{null f1}","{int f1,null f2}"); }
	@Test public void test_566() { checkNotSubtype("{null f1}","{int f2,null f3}"); }
	@Test public void test_567() { checkNotSubtype("{null f1}","{int f1,int f2}"); }
	@Test public void test_568() { checkNotSubtype("{null f1}","{int f2,int f3}"); }
	@Test public void test_569() { checkIsSubtype("{null f1}","{{void f1} f1}"); }
	@Test public void test_570() { checkIsSubtype("{null f1}","{{void f2} f1}"); }
	@Test public void test_571() { checkIsSubtype("{null f1}","{{void f1} f2}"); }
	@Test public void test_572() { checkIsSubtype("{null f1}","{{void f2} f2}"); }
	@Test public void test_573() { checkIsSubtype("{null f1}","{{void f1} f1,void f2}"); }
	@Test public void test_574() { checkIsSubtype("{null f1}","{{void f2} f1,void f2}"); }
	@Test public void test_575() { checkIsSubtype("{null f1}","{{void f1} f2,void f3}"); }
	@Test public void test_576() { checkIsSubtype("{null f1}","{{void f2} f2,void f3}"); }
	@Test public void test_577() { checkNotSubtype("{null f1}","{{any f1} f1}"); }
	@Test public void test_578() { checkNotSubtype("{null f1}","{{any f2} f1}"); }
	@Test public void test_579() { checkNotSubtype("{null f1}","{{any f1} f2}"); }
	@Test public void test_580() { checkNotSubtype("{null f1}","{{any f2} f2}"); }
	@Test public void test_581() { checkNotSubtype("{null f1}","{{any f1} f1,any f2}"); }
	@Test public void test_582() { checkNotSubtype("{null f1}","{{any f2} f1,any f2}"); }
	@Test public void test_583() { checkNotSubtype("{null f1}","{{any f1} f2,any f3}"); }
	@Test public void test_584() { checkNotSubtype("{null f1}","{{any f2} f2,any f3}"); }
	@Test public void test_585() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test public void test_586() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test public void test_587() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test public void test_588() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test public void test_589() { checkNotSubtype("{null f1}","{{null f1} f1,null f2}"); }
	@Test public void test_590() { checkNotSubtype("{null f1}","{{null f2} f1,null f2}"); }
	@Test public void test_591() { checkNotSubtype("{null f1}","{{null f1} f2,null f3}"); }
	@Test public void test_592() { checkNotSubtype("{null f1}","{{null f2} f2,null f3}"); }
	@Test public void test_593() { checkNotSubtype("{null f1}","{{int f1} f1}"); }
	@Test public void test_594() { checkNotSubtype("{null f1}","{{int f2} f1}"); }
	@Test public void test_595() { checkNotSubtype("{null f1}","{{int f1} f2}"); }
	@Test public void test_596() { checkNotSubtype("{null f1}","{{int f2} f2}"); }
	@Test public void test_597() { checkNotSubtype("{null f1}","{{int f1} f1,int f2}"); }
	@Test public void test_598() { checkNotSubtype("{null f1}","{{int f2} f1,int f2}"); }
	@Test public void test_599() { checkNotSubtype("{null f1}","{{int f1} f2,int f3}"); }
	@Test public void test_600() { checkNotSubtype("{null f1}","{{int f2} f2,int f3}"); }
	@Test public void test_601() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_602() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_603() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_604() { checkIsSubtype("{null f2}","{void f1}"); }
	@Test public void test_605() { checkIsSubtype("{null f2}","{void f2}"); }
	@Test public void test_606() { checkNotSubtype("{null f2}","{any f1}"); }
	@Test public void test_607() { checkNotSubtype("{null f2}","{any f2}"); }
	@Test public void test_608() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_609() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_610() { checkNotSubtype("{null f2}","{int f1}"); }
	@Test public void test_611() { checkNotSubtype("{null f2}","{int f2}"); }
	@Test public void test_612() { checkIsSubtype("{null f2}","{void f1,void f2}"); }
	@Test public void test_613() { checkIsSubtype("{null f2}","{void f2,void f3}"); }
	@Test public void test_614() { checkIsSubtype("{null f2}","{void f1,any f2}"); }
	@Test public void test_615() { checkIsSubtype("{null f2}","{void f2,any f3}"); }
	@Test public void test_616() { checkIsSubtype("{null f2}","{void f1,null f2}"); }
	@Test public void test_617() { checkIsSubtype("{null f2}","{void f2,null f3}"); }
	@Test public void test_618() { checkIsSubtype("{null f2}","{void f1,int f2}"); }
	@Test public void test_619() { checkIsSubtype("{null f2}","{void f2,int f3}"); }
	@Test public void test_620() { checkIsSubtype("{null f2}","{any f1,void f2}"); }
	@Test public void test_621() { checkIsSubtype("{null f2}","{any f2,void f3}"); }
	@Test public void test_622() { checkNotSubtype("{null f2}","{any f1,any f2}"); }
	@Test public void test_623() { checkNotSubtype("{null f2}","{any f2,any f3}"); }
	@Test public void test_624() { checkNotSubtype("{null f2}","{any f1,null f2}"); }
	@Test public void test_625() { checkNotSubtype("{null f2}","{any f2,null f3}"); }
	@Test public void test_626() { checkNotSubtype("{null f2}","{any f1,int f2}"); }
	@Test public void test_627() { checkNotSubtype("{null f2}","{any f2,int f3}"); }
	@Test public void test_628() { checkIsSubtype("{null f2}","{null f1,void f2}"); }
	@Test public void test_629() { checkIsSubtype("{null f2}","{null f2,void f3}"); }
	@Test public void test_630() { checkNotSubtype("{null f2}","{null f1,any f2}"); }
	@Test public void test_631() { checkNotSubtype("{null f2}","{null f2,any f3}"); }
	@Test public void test_632() { checkNotSubtype("{null f2}","{null f1,null f2}"); }
	@Test public void test_633() { checkNotSubtype("{null f2}","{null f2,null f3}"); }
	@Test public void test_634() { checkNotSubtype("{null f2}","{null f1,int f2}"); }
	@Test public void test_635() { checkNotSubtype("{null f2}","{null f2,int f3}"); }
	@Test public void test_636() { checkIsSubtype("{null f2}","{int f1,void f2}"); }
	@Test public void test_637() { checkIsSubtype("{null f2}","{int f2,void f3}"); }
	@Test public void test_638() { checkNotSubtype("{null f2}","{int f1,any f2}"); }
	@Test public void test_639() { checkNotSubtype("{null f2}","{int f2,any f3}"); }
	@Test public void test_640() { checkNotSubtype("{null f2}","{int f1,null f2}"); }
	@Test public void test_641() { checkNotSubtype("{null f2}","{int f2,null f3}"); }
	@Test public void test_642() { checkNotSubtype("{null f2}","{int f1,int f2}"); }
	@Test public void test_643() { checkNotSubtype("{null f2}","{int f2,int f3}"); }
	@Test public void test_644() { checkIsSubtype("{null f2}","{{void f1} f1}"); }
	@Test public void test_645() { checkIsSubtype("{null f2}","{{void f2} f1}"); }
	@Test public void test_646() { checkIsSubtype("{null f2}","{{void f1} f2}"); }
	@Test public void test_647() { checkIsSubtype("{null f2}","{{void f2} f2}"); }
	@Test public void test_648() { checkIsSubtype("{null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_649() { checkIsSubtype("{null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_650() { checkIsSubtype("{null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_651() { checkIsSubtype("{null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_652() { checkNotSubtype("{null f2}","{{any f1} f1}"); }
	@Test public void test_653() { checkNotSubtype("{null f2}","{{any f2} f1}"); }
	@Test public void test_654() { checkNotSubtype("{null f2}","{{any f1} f2}"); }
	@Test public void test_655() { checkNotSubtype("{null f2}","{{any f2} f2}"); }
	@Test public void test_656() { checkNotSubtype("{null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_657() { checkNotSubtype("{null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_658() { checkNotSubtype("{null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_659() { checkNotSubtype("{null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_660() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test public void test_661() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test public void test_662() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test public void test_663() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test public void test_664() { checkNotSubtype("{null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_665() { checkNotSubtype("{null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_666() { checkNotSubtype("{null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_667() { checkNotSubtype("{null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_668() { checkNotSubtype("{null f2}","{{int f1} f1}"); }
	@Test public void test_669() { checkNotSubtype("{null f2}","{{int f2} f1}"); }
	@Test public void test_670() { checkNotSubtype("{null f2}","{{int f1} f2}"); }
	@Test public void test_671() { checkNotSubtype("{null f2}","{{int f2} f2}"); }
	@Test public void test_672() { checkNotSubtype("{null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_673() { checkNotSubtype("{null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_674() { checkNotSubtype("{null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_675() { checkNotSubtype("{null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_676() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_677() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_678() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_679() { checkIsSubtype("{int f1}","{void f1}"); }
	@Test public void test_680() { checkIsSubtype("{int f1}","{void f2}"); }
	@Test public void test_681() { checkNotSubtype("{int f1}","{any f1}"); }
	@Test public void test_682() { checkNotSubtype("{int f1}","{any f2}"); }
	@Test public void test_683() { checkNotSubtype("{int f1}","{null f1}"); }
	@Test public void test_684() { checkNotSubtype("{int f1}","{null f2}"); }
	@Test public void test_685() { checkIsSubtype("{int f1}","{int f1}"); }
	@Test public void test_686() { checkNotSubtype("{int f1}","{int f2}"); }
	@Test public void test_687() { checkIsSubtype("{int f1}","{void f1,void f2}"); }
	@Test public void test_688() { checkIsSubtype("{int f1}","{void f2,void f3}"); }
	@Test public void test_689() { checkIsSubtype("{int f1}","{void f1,any f2}"); }
	@Test public void test_690() { checkIsSubtype("{int f1}","{void f2,any f3}"); }
	@Test public void test_691() { checkIsSubtype("{int f1}","{void f1,null f2}"); }
	@Test public void test_692() { checkIsSubtype("{int f1}","{void f2,null f3}"); }
	@Test public void test_693() { checkIsSubtype("{int f1}","{void f1,int f2}"); }
	@Test public void test_694() { checkIsSubtype("{int f1}","{void f2,int f3}"); }
	@Test public void test_695() { checkIsSubtype("{int f1}","{any f1,void f2}"); }
	@Test public void test_696() { checkIsSubtype("{int f1}","{any f2,void f3}"); }
	@Test public void test_697() { checkNotSubtype("{int f1}","{any f1,any f2}"); }
	@Test public void test_698() { checkNotSubtype("{int f1}","{any f2,any f3}"); }
	@Test public void test_699() { checkNotSubtype("{int f1}","{any f1,null f2}"); }
	@Test public void test_700() { checkNotSubtype("{int f1}","{any f2,null f3}"); }
	@Test public void test_701() { checkNotSubtype("{int f1}","{any f1,int f2}"); }
	@Test public void test_702() { checkNotSubtype("{int f1}","{any f2,int f3}"); }
	@Test public void test_703() { checkIsSubtype("{int f1}","{null f1,void f2}"); }
	@Test public void test_704() { checkIsSubtype("{int f1}","{null f2,void f3}"); }
	@Test public void test_705() { checkNotSubtype("{int f1}","{null f1,any f2}"); }
	@Test public void test_706() { checkNotSubtype("{int f1}","{null f2,any f3}"); }
	@Test public void test_707() { checkNotSubtype("{int f1}","{null f1,null f2}"); }
	@Test public void test_708() { checkNotSubtype("{int f1}","{null f2,null f3}"); }
	@Test public void test_709() { checkNotSubtype("{int f1}","{null f1,int f2}"); }
	@Test public void test_710() { checkNotSubtype("{int f1}","{null f2,int f3}"); }
	@Test public void test_711() { checkIsSubtype("{int f1}","{int f1,void f2}"); }
	@Test public void test_712() { checkIsSubtype("{int f1}","{int f2,void f3}"); }
	@Test public void test_713() { checkNotSubtype("{int f1}","{int f1,any f2}"); }
	@Test public void test_714() { checkNotSubtype("{int f1}","{int f2,any f3}"); }
	@Test public void test_715() { checkNotSubtype("{int f1}","{int f1,null f2}"); }
	@Test public void test_716() { checkNotSubtype("{int f1}","{int f2,null f3}"); }
	@Test public void test_717() { checkNotSubtype("{int f1}","{int f1,int f2}"); }
	@Test public void test_718() { checkNotSubtype("{int f1}","{int f2,int f3}"); }
	@Test public void test_719() { checkIsSubtype("{int f1}","{{void f1} f1}"); }
	@Test public void test_720() { checkIsSubtype("{int f1}","{{void f2} f1}"); }
	@Test public void test_721() { checkIsSubtype("{int f1}","{{void f1} f2}"); }
	@Test public void test_722() { checkIsSubtype("{int f1}","{{void f2} f2}"); }
	@Test public void test_723() { checkIsSubtype("{int f1}","{{void f1} f1,void f2}"); }
	@Test public void test_724() { checkIsSubtype("{int f1}","{{void f2} f1,void f2}"); }
	@Test public void test_725() { checkIsSubtype("{int f1}","{{void f1} f2,void f3}"); }
	@Test public void test_726() { checkIsSubtype("{int f1}","{{void f2} f2,void f3}"); }
	@Test public void test_727() { checkNotSubtype("{int f1}","{{any f1} f1}"); }
	@Test public void test_728() { checkNotSubtype("{int f1}","{{any f2} f1}"); }
	@Test public void test_729() { checkNotSubtype("{int f1}","{{any f1} f2}"); }
	@Test public void test_730() { checkNotSubtype("{int f1}","{{any f2} f2}"); }
	@Test public void test_731() { checkNotSubtype("{int f1}","{{any f1} f1,any f2}"); }
	@Test public void test_732() { checkNotSubtype("{int f1}","{{any f2} f1,any f2}"); }
	@Test public void test_733() { checkNotSubtype("{int f1}","{{any f1} f2,any f3}"); }
	@Test public void test_734() { checkNotSubtype("{int f1}","{{any f2} f2,any f3}"); }
	@Test public void test_735() { checkNotSubtype("{int f1}","{{null f1} f1}"); }
	@Test public void test_736() { checkNotSubtype("{int f1}","{{null f2} f1}"); }
	@Test public void test_737() { checkNotSubtype("{int f1}","{{null f1} f2}"); }
	@Test public void test_738() { checkNotSubtype("{int f1}","{{null f2} f2}"); }
	@Test public void test_739() { checkNotSubtype("{int f1}","{{null f1} f1,null f2}"); }
	@Test public void test_740() { checkNotSubtype("{int f1}","{{null f2} f1,null f2}"); }
	@Test public void test_741() { checkNotSubtype("{int f1}","{{null f1} f2,null f3}"); }
	@Test public void test_742() { checkNotSubtype("{int f1}","{{null f2} f2,null f3}"); }
	@Test public void test_743() { checkNotSubtype("{int f1}","{{int f1} f1}"); }
	@Test public void test_744() { checkNotSubtype("{int f1}","{{int f2} f1}"); }
	@Test public void test_745() { checkNotSubtype("{int f1}","{{int f1} f2}"); }
	@Test public void test_746() { checkNotSubtype("{int f1}","{{int f2} f2}"); }
	@Test public void test_747() { checkNotSubtype("{int f1}","{{int f1} f1,int f2}"); }
	@Test public void test_748() { checkNotSubtype("{int f1}","{{int f2} f1,int f2}"); }
	@Test public void test_749() { checkNotSubtype("{int f1}","{{int f1} f2,int f3}"); }
	@Test public void test_750() { checkNotSubtype("{int f1}","{{int f2} f2,int f3}"); }
	@Test public void test_751() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_752() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_753() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_754() { checkIsSubtype("{int f2}","{void f1}"); }
	@Test public void test_755() { checkIsSubtype("{int f2}","{void f2}"); }
	@Test public void test_756() { checkNotSubtype("{int f2}","{any f1}"); }
	@Test public void test_757() { checkNotSubtype("{int f2}","{any f2}"); }
	@Test public void test_758() { checkNotSubtype("{int f2}","{null f1}"); }
	@Test public void test_759() { checkNotSubtype("{int f2}","{null f2}"); }
	@Test public void test_760() { checkNotSubtype("{int f2}","{int f1}"); }
	@Test public void test_761() { checkIsSubtype("{int f2}","{int f2}"); }
	@Test public void test_762() { checkIsSubtype("{int f2}","{void f1,void f2}"); }
	@Test public void test_763() { checkIsSubtype("{int f2}","{void f2,void f3}"); }
	@Test public void test_764() { checkIsSubtype("{int f2}","{void f1,any f2}"); }
	@Test public void test_765() { checkIsSubtype("{int f2}","{void f2,any f3}"); }
	@Test public void test_766() { checkIsSubtype("{int f2}","{void f1,null f2}"); }
	@Test public void test_767() { checkIsSubtype("{int f2}","{void f2,null f3}"); }
	@Test public void test_768() { checkIsSubtype("{int f2}","{void f1,int f2}"); }
	@Test public void test_769() { checkIsSubtype("{int f2}","{void f2,int f3}"); }
	@Test public void test_770() { checkIsSubtype("{int f2}","{any f1,void f2}"); }
	@Test public void test_771() { checkIsSubtype("{int f2}","{any f2,void f3}"); }
	@Test public void test_772() { checkNotSubtype("{int f2}","{any f1,any f2}"); }
	@Test public void test_773() { checkNotSubtype("{int f2}","{any f2,any f3}"); }
	@Test public void test_774() { checkNotSubtype("{int f2}","{any f1,null f2}"); }
	@Test public void test_775() { checkNotSubtype("{int f2}","{any f2,null f3}"); }
	@Test public void test_776() { checkNotSubtype("{int f2}","{any f1,int f2}"); }
	@Test public void test_777() { checkNotSubtype("{int f2}","{any f2,int f3}"); }
	@Test public void test_778() { checkIsSubtype("{int f2}","{null f1,void f2}"); }
	@Test public void test_779() { checkIsSubtype("{int f2}","{null f2,void f3}"); }
	@Test public void test_780() { checkNotSubtype("{int f2}","{null f1,any f2}"); }
	@Test public void test_781() { checkNotSubtype("{int f2}","{null f2,any f3}"); }
	@Test public void test_782() { checkNotSubtype("{int f2}","{null f1,null f2}"); }
	@Test public void test_783() { checkNotSubtype("{int f2}","{null f2,null f3}"); }
	@Test public void test_784() { checkNotSubtype("{int f2}","{null f1,int f2}"); }
	@Test public void test_785() { checkNotSubtype("{int f2}","{null f2,int f3}"); }
	@Test public void test_786() { checkIsSubtype("{int f2}","{int f1,void f2}"); }
	@Test public void test_787() { checkIsSubtype("{int f2}","{int f2,void f3}"); }
	@Test public void test_788() { checkNotSubtype("{int f2}","{int f1,any f2}"); }
	@Test public void test_789() { checkNotSubtype("{int f2}","{int f2,any f3}"); }
	@Test public void test_790() { checkNotSubtype("{int f2}","{int f1,null f2}"); }
	@Test public void test_791() { checkNotSubtype("{int f2}","{int f2,null f3}"); }
	@Test public void test_792() { checkNotSubtype("{int f2}","{int f1,int f2}"); }
	@Test public void test_793() { checkNotSubtype("{int f2}","{int f2,int f3}"); }
	@Test public void test_794() { checkIsSubtype("{int f2}","{{void f1} f1}"); }
	@Test public void test_795() { checkIsSubtype("{int f2}","{{void f2} f1}"); }
	@Test public void test_796() { checkIsSubtype("{int f2}","{{void f1} f2}"); }
	@Test public void test_797() { checkIsSubtype("{int f2}","{{void f2} f2}"); }
	@Test public void test_798() { checkIsSubtype("{int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_799() { checkIsSubtype("{int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_800() { checkIsSubtype("{int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_801() { checkIsSubtype("{int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_802() { checkNotSubtype("{int f2}","{{any f1} f1}"); }
	@Test public void test_803() { checkNotSubtype("{int f2}","{{any f2} f1}"); }
	@Test public void test_804() { checkNotSubtype("{int f2}","{{any f1} f2}"); }
	@Test public void test_805() { checkNotSubtype("{int f2}","{{any f2} f2}"); }
	@Test public void test_806() { checkNotSubtype("{int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_807() { checkNotSubtype("{int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_808() { checkNotSubtype("{int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_809() { checkNotSubtype("{int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_810() { checkNotSubtype("{int f2}","{{null f1} f1}"); }
	@Test public void test_811() { checkNotSubtype("{int f2}","{{null f2} f1}"); }
	@Test public void test_812() { checkNotSubtype("{int f2}","{{null f1} f2}"); }
	@Test public void test_813() { checkNotSubtype("{int f2}","{{null f2} f2}"); }
	@Test public void test_814() { checkNotSubtype("{int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_815() { checkNotSubtype("{int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_816() { checkNotSubtype("{int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_817() { checkNotSubtype("{int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_818() { checkNotSubtype("{int f2}","{{int f1} f1}"); }
	@Test public void test_819() { checkNotSubtype("{int f2}","{{int f2} f1}"); }
	@Test public void test_820() { checkNotSubtype("{int f2}","{{int f1} f2}"); }
	@Test public void test_821() { checkNotSubtype("{int f2}","{{int f2} f2}"); }
	@Test public void test_822() { checkNotSubtype("{int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_823() { checkNotSubtype("{int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_824() { checkNotSubtype("{int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_825() { checkNotSubtype("{int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_826() { checkNotSubtype("{void f1,void f2}","any"); }
	@Test public void test_827() { checkNotSubtype("{void f1,void f2}","null"); }
	@Test public void test_828() { checkNotSubtype("{void f1,void f2}","int"); }
	@Test public void test_829() { checkIsSubtype("{void f1,void f2}","{void f1}"); }
	@Test public void test_830() { checkIsSubtype("{void f1,void f2}","{void f2}"); }
	@Test public void test_831() { checkNotSubtype("{void f1,void f2}","{any f1}"); }
	@Test public void test_832() { checkNotSubtype("{void f1,void f2}","{any f2}"); }
	@Test public void test_833() { checkNotSubtype("{void f1,void f2}","{null f1}"); }
	@Test public void test_834() { checkNotSubtype("{void f1,void f2}","{null f2}"); }
	@Test public void test_835() { checkNotSubtype("{void f1,void f2}","{int f1}"); }
	@Test public void test_836() { checkNotSubtype("{void f1,void f2}","{int f2}"); }
	@Test public void test_837() { checkIsSubtype("{void f1,void f2}","{void f1,void f2}"); }
	@Test public void test_838() { checkIsSubtype("{void f1,void f2}","{void f2,void f3}"); }
	@Test public void test_839() { checkIsSubtype("{void f1,void f2}","{void f1,any f2}"); }
	@Test public void test_840() { checkIsSubtype("{void f1,void f2}","{void f2,any f3}"); }
	@Test public void test_841() { checkIsSubtype("{void f1,void f2}","{void f1,null f2}"); }
	@Test public void test_842() { checkIsSubtype("{void f1,void f2}","{void f2,null f3}"); }
	@Test public void test_843() { checkIsSubtype("{void f1,void f2}","{void f1,int f2}"); }
	@Test public void test_844() { checkIsSubtype("{void f1,void f2}","{void f2,int f3}"); }
	@Test public void test_845() { checkIsSubtype("{void f1,void f2}","{any f1,void f2}"); }
	@Test public void test_846() { checkIsSubtype("{void f1,void f2}","{any f2,void f3}"); }
	@Test public void test_847() { checkNotSubtype("{void f1,void f2}","{any f1,any f2}"); }
	@Test public void test_848() { checkNotSubtype("{void f1,void f2}","{any f2,any f3}"); }
	@Test public void test_849() { checkNotSubtype("{void f1,void f2}","{any f1,null f2}"); }
	@Test public void test_850() { checkNotSubtype("{void f1,void f2}","{any f2,null f3}"); }
	@Test public void test_851() { checkNotSubtype("{void f1,void f2}","{any f1,int f2}"); }
	@Test public void test_852() { checkNotSubtype("{void f1,void f2}","{any f2,int f3}"); }
	@Test public void test_853() { checkIsSubtype("{void f1,void f2}","{null f1,void f2}"); }
	@Test public void test_854() { checkIsSubtype("{void f1,void f2}","{null f2,void f3}"); }
	@Test public void test_855() { checkNotSubtype("{void f1,void f2}","{null f1,any f2}"); }
	@Test public void test_856() { checkNotSubtype("{void f1,void f2}","{null f2,any f3}"); }
	@Test public void test_857() { checkNotSubtype("{void f1,void f2}","{null f1,null f2}"); }
	@Test public void test_858() { checkNotSubtype("{void f1,void f2}","{null f2,null f3}"); }
	@Test public void test_859() { checkNotSubtype("{void f1,void f2}","{null f1,int f2}"); }
	@Test public void test_860() { checkNotSubtype("{void f1,void f2}","{null f2,int f3}"); }
	@Test public void test_861() { checkIsSubtype("{void f1,void f2}","{int f1,void f2}"); }
	@Test public void test_862() { checkIsSubtype("{void f1,void f2}","{int f2,void f3}"); }
	@Test public void test_863() { checkNotSubtype("{void f1,void f2}","{int f1,any f2}"); }
	@Test public void test_864() { checkNotSubtype("{void f1,void f2}","{int f2,any f3}"); }
	@Test public void test_865() { checkNotSubtype("{void f1,void f2}","{int f1,null f2}"); }
	@Test public void test_866() { checkNotSubtype("{void f1,void f2}","{int f2,null f3}"); }
	@Test public void test_867() { checkNotSubtype("{void f1,void f2}","{int f1,int f2}"); }
	@Test public void test_868() { checkNotSubtype("{void f1,void f2}","{int f2,int f3}"); }
	@Test public void test_869() { checkIsSubtype("{void f1,void f2}","{{void f1} f1}"); }
	@Test public void test_870() { checkIsSubtype("{void f1,void f2}","{{void f2} f1}"); }
	@Test public void test_871() { checkIsSubtype("{void f1,void f2}","{{void f1} f2}"); }
	@Test public void test_872() { checkIsSubtype("{void f1,void f2}","{{void f2} f2}"); }
	@Test public void test_873() { checkIsSubtype("{void f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_874() { checkIsSubtype("{void f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_875() { checkIsSubtype("{void f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_876() { checkIsSubtype("{void f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_877() { checkNotSubtype("{void f1,void f2}","{{any f1} f1}"); }
	@Test public void test_878() { checkNotSubtype("{void f1,void f2}","{{any f2} f1}"); }
	@Test public void test_879() { checkNotSubtype("{void f1,void f2}","{{any f1} f2}"); }
	@Test public void test_880() { checkNotSubtype("{void f1,void f2}","{{any f2} f2}"); }
	@Test public void test_881() { checkNotSubtype("{void f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_882() { checkNotSubtype("{void f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_883() { checkNotSubtype("{void f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_884() { checkNotSubtype("{void f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_885() { checkNotSubtype("{void f1,void f2}","{{null f1} f1}"); }
	@Test public void test_886() { checkNotSubtype("{void f1,void f2}","{{null f2} f1}"); }
	@Test public void test_887() { checkNotSubtype("{void f1,void f2}","{{null f1} f2}"); }
	@Test public void test_888() { checkNotSubtype("{void f1,void f2}","{{null f2} f2}"); }
	@Test public void test_889() { checkNotSubtype("{void f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_890() { checkNotSubtype("{void f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_891() { checkNotSubtype("{void f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_892() { checkNotSubtype("{void f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_893() { checkNotSubtype("{void f1,void f2}","{{int f1} f1}"); }
	@Test public void test_894() { checkNotSubtype("{void f1,void f2}","{{int f2} f1}"); }
	@Test public void test_895() { checkNotSubtype("{void f1,void f2}","{{int f1} f2}"); }
	@Test public void test_896() { checkNotSubtype("{void f1,void f2}","{{int f2} f2}"); }
	@Test public void test_897() { checkNotSubtype("{void f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_898() { checkNotSubtype("{void f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_899() { checkNotSubtype("{void f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_900() { checkNotSubtype("{void f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_901() { checkNotSubtype("{void f2,void f3}","any"); }
	@Test public void test_902() { checkNotSubtype("{void f2,void f3}","null"); }
	@Test public void test_903() { checkNotSubtype("{void f2,void f3}","int"); }
	@Test public void test_904() { checkIsSubtype("{void f2,void f3}","{void f1}"); }
	@Test public void test_905() { checkIsSubtype("{void f2,void f3}","{void f2}"); }
	@Test public void test_906() { checkNotSubtype("{void f2,void f3}","{any f1}"); }
	@Test public void test_907() { checkNotSubtype("{void f2,void f3}","{any f2}"); }
	@Test public void test_908() { checkNotSubtype("{void f2,void f3}","{null f1}"); }
	@Test public void test_909() { checkNotSubtype("{void f2,void f3}","{null f2}"); }
	@Test public void test_910() { checkNotSubtype("{void f2,void f3}","{int f1}"); }
	@Test public void test_911() { checkNotSubtype("{void f2,void f3}","{int f2}"); }
	@Test public void test_912() { checkIsSubtype("{void f2,void f3}","{void f1,void f2}"); }
	@Test public void test_913() { checkIsSubtype("{void f2,void f3}","{void f2,void f3}"); }
	@Test public void test_914() { checkIsSubtype("{void f2,void f3}","{void f1,any f2}"); }
	@Test public void test_915() { checkIsSubtype("{void f2,void f3}","{void f2,any f3}"); }
	@Test public void test_916() { checkIsSubtype("{void f2,void f3}","{void f1,null f2}"); }
	@Test public void test_917() { checkIsSubtype("{void f2,void f3}","{void f2,null f3}"); }
	@Test public void test_918() { checkIsSubtype("{void f2,void f3}","{void f1,int f2}"); }
	@Test public void test_919() { checkIsSubtype("{void f2,void f3}","{void f2,int f3}"); }
	@Test public void test_920() { checkIsSubtype("{void f2,void f3}","{any f1,void f2}"); }
	@Test public void test_921() { checkIsSubtype("{void f2,void f3}","{any f2,void f3}"); }
	@Test public void test_922() { checkNotSubtype("{void f2,void f3}","{any f1,any f2}"); }
	@Test public void test_923() { checkNotSubtype("{void f2,void f3}","{any f2,any f3}"); }
	@Test public void test_924() { checkNotSubtype("{void f2,void f3}","{any f1,null f2}"); }
	@Test public void test_925() { checkNotSubtype("{void f2,void f3}","{any f2,null f3}"); }
	@Test public void test_926() { checkNotSubtype("{void f2,void f3}","{any f1,int f2}"); }
	@Test public void test_927() { checkNotSubtype("{void f2,void f3}","{any f2,int f3}"); }
	@Test public void test_928() { checkIsSubtype("{void f2,void f3}","{null f1,void f2}"); }
	@Test public void test_929() { checkIsSubtype("{void f2,void f3}","{null f2,void f3}"); }
	@Test public void test_930() { checkNotSubtype("{void f2,void f3}","{null f1,any f2}"); }
	@Test public void test_931() { checkNotSubtype("{void f2,void f3}","{null f2,any f3}"); }
	@Test public void test_932() { checkNotSubtype("{void f2,void f3}","{null f1,null f2}"); }
	@Test public void test_933() { checkNotSubtype("{void f2,void f3}","{null f2,null f3}"); }
	@Test public void test_934() { checkNotSubtype("{void f2,void f3}","{null f1,int f2}"); }
	@Test public void test_935() { checkNotSubtype("{void f2,void f3}","{null f2,int f3}"); }
	@Test public void test_936() { checkIsSubtype("{void f2,void f3}","{int f1,void f2}"); }
	@Test public void test_937() { checkIsSubtype("{void f2,void f3}","{int f2,void f3}"); }
	@Test public void test_938() { checkNotSubtype("{void f2,void f3}","{int f1,any f2}"); }
	@Test public void test_939() { checkNotSubtype("{void f2,void f3}","{int f2,any f3}"); }
	@Test public void test_940() { checkNotSubtype("{void f2,void f3}","{int f1,null f2}"); }
	@Test public void test_941() { checkNotSubtype("{void f2,void f3}","{int f2,null f3}"); }
	@Test public void test_942() { checkNotSubtype("{void f2,void f3}","{int f1,int f2}"); }
	@Test public void test_943() { checkNotSubtype("{void f2,void f3}","{int f2,int f3}"); }
	@Test public void test_944() { checkIsSubtype("{void f2,void f3}","{{void f1} f1}"); }
	@Test public void test_945() { checkIsSubtype("{void f2,void f3}","{{void f2} f1}"); }
	@Test public void test_946() { checkIsSubtype("{void f2,void f3}","{{void f1} f2}"); }
	@Test public void test_947() { checkIsSubtype("{void f2,void f3}","{{void f2} f2}"); }
	@Test public void test_948() { checkIsSubtype("{void f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_949() { checkIsSubtype("{void f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_950() { checkIsSubtype("{void f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_951() { checkIsSubtype("{void f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_952() { checkNotSubtype("{void f2,void f3}","{{any f1} f1}"); }
	@Test public void test_953() { checkNotSubtype("{void f2,void f3}","{{any f2} f1}"); }
	@Test public void test_954() { checkNotSubtype("{void f2,void f3}","{{any f1} f2}"); }
	@Test public void test_955() { checkNotSubtype("{void f2,void f3}","{{any f2} f2}"); }
	@Test public void test_956() { checkNotSubtype("{void f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_957() { checkNotSubtype("{void f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_958() { checkNotSubtype("{void f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_959() { checkNotSubtype("{void f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_960() { checkNotSubtype("{void f2,void f3}","{{null f1} f1}"); }
	@Test public void test_961() { checkNotSubtype("{void f2,void f3}","{{null f2} f1}"); }
	@Test public void test_962() { checkNotSubtype("{void f2,void f3}","{{null f1} f2}"); }
	@Test public void test_963() { checkNotSubtype("{void f2,void f3}","{{null f2} f2}"); }
	@Test public void test_964() { checkNotSubtype("{void f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_965() { checkNotSubtype("{void f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_966() { checkNotSubtype("{void f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_967() { checkNotSubtype("{void f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_968() { checkNotSubtype("{void f2,void f3}","{{int f1} f1}"); }
	@Test public void test_969() { checkNotSubtype("{void f2,void f3}","{{int f2} f1}"); }
	@Test public void test_970() { checkNotSubtype("{void f2,void f3}","{{int f1} f2}"); }
	@Test public void test_971() { checkNotSubtype("{void f2,void f3}","{{int f2} f2}"); }
	@Test public void test_972() { checkNotSubtype("{void f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_973() { checkNotSubtype("{void f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_974() { checkNotSubtype("{void f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_975() { checkNotSubtype("{void f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_976() { checkNotSubtype("{void f1,any f2}","any"); }
	@Test public void test_977() { checkNotSubtype("{void f1,any f2}","null"); }
	@Test public void test_978() { checkNotSubtype("{void f1,any f2}","int"); }
	@Test public void test_979() { checkIsSubtype("{void f1,any f2}","{void f1}"); }
	@Test public void test_980() { checkIsSubtype("{void f1,any f2}","{void f2}"); }
	@Test public void test_981() { checkNotSubtype("{void f1,any f2}","{any f1}"); }
	@Test public void test_982() { checkNotSubtype("{void f1,any f2}","{any f2}"); }
	@Test public void test_983() { checkNotSubtype("{void f1,any f2}","{null f1}"); }
	@Test public void test_984() { checkNotSubtype("{void f1,any f2}","{null f2}"); }
	@Test public void test_985() { checkNotSubtype("{void f1,any f2}","{int f1}"); }
	@Test public void test_986() { checkNotSubtype("{void f1,any f2}","{int f2}"); }
	@Test public void test_987() { checkIsSubtype("{void f1,any f2}","{void f1,void f2}"); }
	@Test public void test_988() { checkIsSubtype("{void f1,any f2}","{void f2,void f3}"); }
	@Test public void test_989() { checkIsSubtype("{void f1,any f2}","{void f1,any f2}"); }
	@Test public void test_990() { checkIsSubtype("{void f1,any f2}","{void f2,any f3}"); }
	@Test public void test_991() { checkIsSubtype("{void f1,any f2}","{void f1,null f2}"); }
	@Test public void test_992() { checkIsSubtype("{void f1,any f2}","{void f2,null f3}"); }
	@Test public void test_993() { checkIsSubtype("{void f1,any f2}","{void f1,int f2}"); }
	@Test public void test_994() { checkIsSubtype("{void f1,any f2}","{void f2,int f3}"); }
	@Test public void test_995() { checkIsSubtype("{void f1,any f2}","{any f1,void f2}"); }
	@Test public void test_996() { checkIsSubtype("{void f1,any f2}","{any f2,void f3}"); }
	@Test public void test_997() { checkNotSubtype("{void f1,any f2}","{any f1,any f2}"); }
	@Test public void test_998() { checkNotSubtype("{void f1,any f2}","{any f2,any f3}"); }
	@Test public void test_999() { checkNotSubtype("{void f1,any f2}","{any f1,null f2}"); }
	@Test public void test_1000() { checkNotSubtype("{void f1,any f2}","{any f2,null f3}"); }
	@Test public void test_1001() { checkNotSubtype("{void f1,any f2}","{any f1,int f2}"); }
	@Test public void test_1002() { checkNotSubtype("{void f1,any f2}","{any f2,int f3}"); }
	@Test public void test_1003() { checkIsSubtype("{void f1,any f2}","{null f1,void f2}"); }
	@Test public void test_1004() { checkIsSubtype("{void f1,any f2}","{null f2,void f3}"); }
	@Test public void test_1005() { checkNotSubtype("{void f1,any f2}","{null f1,any f2}"); }
	@Test public void test_1006() { checkNotSubtype("{void f1,any f2}","{null f2,any f3}"); }
	@Test public void test_1007() { checkNotSubtype("{void f1,any f2}","{null f1,null f2}"); }
	@Test public void test_1008() { checkNotSubtype("{void f1,any f2}","{null f2,null f3}"); }
	@Test public void test_1009() { checkNotSubtype("{void f1,any f2}","{null f1,int f2}"); }
	@Test public void test_1010() { checkNotSubtype("{void f1,any f2}","{null f2,int f3}"); }
	@Test public void test_1011() { checkIsSubtype("{void f1,any f2}","{int f1,void f2}"); }
	@Test public void test_1012() { checkIsSubtype("{void f1,any f2}","{int f2,void f3}"); }
	@Test public void test_1013() { checkNotSubtype("{void f1,any f2}","{int f1,any f2}"); }
	@Test public void test_1014() { checkNotSubtype("{void f1,any f2}","{int f2,any f3}"); }
	@Test public void test_1015() { checkNotSubtype("{void f1,any f2}","{int f1,null f2}"); }
	@Test public void test_1016() { checkNotSubtype("{void f1,any f2}","{int f2,null f3}"); }
	@Test public void test_1017() { checkNotSubtype("{void f1,any f2}","{int f1,int f2}"); }
	@Test public void test_1018() { checkNotSubtype("{void f1,any f2}","{int f2,int f3}"); }
	@Test public void test_1019() { checkIsSubtype("{void f1,any f2}","{{void f1} f1}"); }
	@Test public void test_1020() { checkIsSubtype("{void f1,any f2}","{{void f2} f1}"); }
	@Test public void test_1021() { checkIsSubtype("{void f1,any f2}","{{void f1} f2}"); }
	@Test public void test_1022() { checkIsSubtype("{void f1,any f2}","{{void f2} f2}"); }
	@Test public void test_1023() { checkIsSubtype("{void f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1024() { checkIsSubtype("{void f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1025() { checkIsSubtype("{void f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1026() { checkIsSubtype("{void f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1027() { checkNotSubtype("{void f1,any f2}","{{any f1} f1}"); }
	@Test public void test_1028() { checkNotSubtype("{void f1,any f2}","{{any f2} f1}"); }
	@Test public void test_1029() { checkNotSubtype("{void f1,any f2}","{{any f1} f2}"); }
	@Test public void test_1030() { checkNotSubtype("{void f1,any f2}","{{any f2} f2}"); }
	@Test public void test_1031() { checkNotSubtype("{void f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1032() { checkNotSubtype("{void f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1033() { checkNotSubtype("{void f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1034() { checkNotSubtype("{void f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1035() { checkNotSubtype("{void f1,any f2}","{{null f1} f1}"); }
	@Test public void test_1036() { checkNotSubtype("{void f1,any f2}","{{null f2} f1}"); }
	@Test public void test_1037() { checkNotSubtype("{void f1,any f2}","{{null f1} f2}"); }
	@Test public void test_1038() { checkNotSubtype("{void f1,any f2}","{{null f2} f2}"); }
	@Test public void test_1039() { checkNotSubtype("{void f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1040() { checkNotSubtype("{void f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1041() { checkNotSubtype("{void f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1042() { checkNotSubtype("{void f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1043() { checkNotSubtype("{void f1,any f2}","{{int f1} f1}"); }
	@Test public void test_1044() { checkNotSubtype("{void f1,any f2}","{{int f2} f1}"); }
	@Test public void test_1045() { checkNotSubtype("{void f1,any f2}","{{int f1} f2}"); }
	@Test public void test_1046() { checkNotSubtype("{void f1,any f2}","{{int f2} f2}"); }
	@Test public void test_1047() { checkNotSubtype("{void f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1048() { checkNotSubtype("{void f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1049() { checkNotSubtype("{void f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1050() { checkNotSubtype("{void f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1051() { checkNotSubtype("{void f2,any f3}","any"); }
	@Test public void test_1052() { checkNotSubtype("{void f2,any f3}","null"); }
	@Test public void test_1053() { checkNotSubtype("{void f2,any f3}","int"); }
	@Test public void test_1054() { checkIsSubtype("{void f2,any f3}","{void f1}"); }
	@Test public void test_1055() { checkIsSubtype("{void f2,any f3}","{void f2}"); }
	@Test public void test_1056() { checkNotSubtype("{void f2,any f3}","{any f1}"); }
	@Test public void test_1057() { checkNotSubtype("{void f2,any f3}","{any f2}"); }
	@Test public void test_1058() { checkNotSubtype("{void f2,any f3}","{null f1}"); }
	@Test public void test_1059() { checkNotSubtype("{void f2,any f3}","{null f2}"); }
	@Test public void test_1060() { checkNotSubtype("{void f2,any f3}","{int f1}"); }
	@Test public void test_1061() { checkNotSubtype("{void f2,any f3}","{int f2}"); }
	@Test public void test_1062() { checkIsSubtype("{void f2,any f3}","{void f1,void f2}"); }
	@Test public void test_1063() { checkIsSubtype("{void f2,any f3}","{void f2,void f3}"); }
	@Test public void test_1064() { checkIsSubtype("{void f2,any f3}","{void f1,any f2}"); }
	@Test public void test_1065() { checkIsSubtype("{void f2,any f3}","{void f2,any f3}"); }
	@Test public void test_1066() { checkIsSubtype("{void f2,any f3}","{void f1,null f2}"); }
	@Test public void test_1067() { checkIsSubtype("{void f2,any f3}","{void f2,null f3}"); }
	@Test public void test_1068() { checkIsSubtype("{void f2,any f3}","{void f1,int f2}"); }
	@Test public void test_1069() { checkIsSubtype("{void f2,any f3}","{void f2,int f3}"); }
	@Test public void test_1070() { checkIsSubtype("{void f2,any f3}","{any f1,void f2}"); }
	@Test public void test_1071() { checkIsSubtype("{void f2,any f3}","{any f2,void f3}"); }
	@Test public void test_1072() { checkNotSubtype("{void f2,any f3}","{any f1,any f2}"); }
	@Test public void test_1073() { checkNotSubtype("{void f2,any f3}","{any f2,any f3}"); }
	@Test public void test_1074() { checkNotSubtype("{void f2,any f3}","{any f1,null f2}"); }
	@Test public void test_1075() { checkNotSubtype("{void f2,any f3}","{any f2,null f3}"); }
	@Test public void test_1076() { checkNotSubtype("{void f2,any f3}","{any f1,int f2}"); }
	@Test public void test_1077() { checkNotSubtype("{void f2,any f3}","{any f2,int f3}"); }
	@Test public void test_1078() { checkIsSubtype("{void f2,any f3}","{null f1,void f2}"); }
	@Test public void test_1079() { checkIsSubtype("{void f2,any f3}","{null f2,void f3}"); }
	@Test public void test_1080() { checkNotSubtype("{void f2,any f3}","{null f1,any f2}"); }
	@Test public void test_1081() { checkNotSubtype("{void f2,any f3}","{null f2,any f3}"); }
	@Test public void test_1082() { checkNotSubtype("{void f2,any f3}","{null f1,null f2}"); }
	@Test public void test_1083() { checkNotSubtype("{void f2,any f3}","{null f2,null f3}"); }
	@Test public void test_1084() { checkNotSubtype("{void f2,any f3}","{null f1,int f2}"); }
	@Test public void test_1085() { checkNotSubtype("{void f2,any f3}","{null f2,int f3}"); }
	@Test public void test_1086() { checkIsSubtype("{void f2,any f3}","{int f1,void f2}"); }
	@Test public void test_1087() { checkIsSubtype("{void f2,any f3}","{int f2,void f3}"); }
	@Test public void test_1088() { checkNotSubtype("{void f2,any f3}","{int f1,any f2}"); }
	@Test public void test_1089() { checkNotSubtype("{void f2,any f3}","{int f2,any f3}"); }
	@Test public void test_1090() { checkNotSubtype("{void f2,any f3}","{int f1,null f2}"); }
	@Test public void test_1091() { checkNotSubtype("{void f2,any f3}","{int f2,null f3}"); }
	@Test public void test_1092() { checkNotSubtype("{void f2,any f3}","{int f1,int f2}"); }
	@Test public void test_1093() { checkNotSubtype("{void f2,any f3}","{int f2,int f3}"); }
	@Test public void test_1094() { checkIsSubtype("{void f2,any f3}","{{void f1} f1}"); }
	@Test public void test_1095() { checkIsSubtype("{void f2,any f3}","{{void f2} f1}"); }
	@Test public void test_1096() { checkIsSubtype("{void f2,any f3}","{{void f1} f2}"); }
	@Test public void test_1097() { checkIsSubtype("{void f2,any f3}","{{void f2} f2}"); }
	@Test public void test_1098() { checkIsSubtype("{void f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_1099() { checkIsSubtype("{void f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_1100() { checkIsSubtype("{void f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_1101() { checkIsSubtype("{void f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_1102() { checkNotSubtype("{void f2,any f3}","{{any f1} f1}"); }
	@Test public void test_1103() { checkNotSubtype("{void f2,any f3}","{{any f2} f1}"); }
	@Test public void test_1104() { checkNotSubtype("{void f2,any f3}","{{any f1} f2}"); }
	@Test public void test_1105() { checkNotSubtype("{void f2,any f3}","{{any f2} f2}"); }
	@Test public void test_1106() { checkNotSubtype("{void f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_1107() { checkNotSubtype("{void f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_1108() { checkNotSubtype("{void f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_1109() { checkNotSubtype("{void f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_1110() { checkNotSubtype("{void f2,any f3}","{{null f1} f1}"); }
	@Test public void test_1111() { checkNotSubtype("{void f2,any f3}","{{null f2} f1}"); }
	@Test public void test_1112() { checkNotSubtype("{void f2,any f3}","{{null f1} f2}"); }
	@Test public void test_1113() { checkNotSubtype("{void f2,any f3}","{{null f2} f2}"); }
	@Test public void test_1114() { checkNotSubtype("{void f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_1115() { checkNotSubtype("{void f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_1116() { checkNotSubtype("{void f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_1117() { checkNotSubtype("{void f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_1118() { checkNotSubtype("{void f2,any f3}","{{int f1} f1}"); }
	@Test public void test_1119() { checkNotSubtype("{void f2,any f3}","{{int f2} f1}"); }
	@Test public void test_1120() { checkNotSubtype("{void f2,any f3}","{{int f1} f2}"); }
	@Test public void test_1121() { checkNotSubtype("{void f2,any f3}","{{int f2} f2}"); }
	@Test public void test_1122() { checkNotSubtype("{void f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_1123() { checkNotSubtype("{void f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_1124() { checkNotSubtype("{void f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_1125() { checkNotSubtype("{void f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_1126() { checkNotSubtype("{void f1,null f2}","any"); }
	@Test public void test_1127() { checkNotSubtype("{void f1,null f2}","null"); }
	@Test public void test_1128() { checkNotSubtype("{void f1,null f2}","int"); }
	@Test public void test_1129() { checkIsSubtype("{void f1,null f2}","{void f1}"); }
	@Test public void test_1130() { checkIsSubtype("{void f1,null f2}","{void f2}"); }
	@Test public void test_1131() { checkNotSubtype("{void f1,null f2}","{any f1}"); }
	@Test public void test_1132() { checkNotSubtype("{void f1,null f2}","{any f2}"); }
	@Test public void test_1133() { checkNotSubtype("{void f1,null f2}","{null f1}"); }
	@Test public void test_1134() { checkNotSubtype("{void f1,null f2}","{null f2}"); }
	@Test public void test_1135() { checkNotSubtype("{void f1,null f2}","{int f1}"); }
	@Test public void test_1136() { checkNotSubtype("{void f1,null f2}","{int f2}"); }
	@Test public void test_1137() { checkIsSubtype("{void f1,null f2}","{void f1,void f2}"); }
	@Test public void test_1138() { checkIsSubtype("{void f1,null f2}","{void f2,void f3}"); }
	@Test public void test_1139() { checkIsSubtype("{void f1,null f2}","{void f1,any f2}"); }
	@Test public void test_1140() { checkIsSubtype("{void f1,null f2}","{void f2,any f3}"); }
	@Test public void test_1141() { checkIsSubtype("{void f1,null f2}","{void f1,null f2}"); }
	@Test public void test_1142() { checkIsSubtype("{void f1,null f2}","{void f2,null f3}"); }
	@Test public void test_1143() { checkIsSubtype("{void f1,null f2}","{void f1,int f2}"); }
	@Test public void test_1144() { checkIsSubtype("{void f1,null f2}","{void f2,int f3}"); }
	@Test public void test_1145() { checkIsSubtype("{void f1,null f2}","{any f1,void f2}"); }
	@Test public void test_1146() { checkIsSubtype("{void f1,null f2}","{any f2,void f3}"); }
	@Test public void test_1147() { checkNotSubtype("{void f1,null f2}","{any f1,any f2}"); }
	@Test public void test_1148() { checkNotSubtype("{void f1,null f2}","{any f2,any f3}"); }
	@Test public void test_1149() { checkNotSubtype("{void f1,null f2}","{any f1,null f2}"); }
	@Test public void test_1150() { checkNotSubtype("{void f1,null f2}","{any f2,null f3}"); }
	@Test public void test_1151() { checkNotSubtype("{void f1,null f2}","{any f1,int f2}"); }
	@Test public void test_1152() { checkNotSubtype("{void f1,null f2}","{any f2,int f3}"); }
	@Test public void test_1153() { checkIsSubtype("{void f1,null f2}","{null f1,void f2}"); }
	@Test public void test_1154() { checkIsSubtype("{void f1,null f2}","{null f2,void f3}"); }
	@Test public void test_1155() { checkNotSubtype("{void f1,null f2}","{null f1,any f2}"); }
	@Test public void test_1156() { checkNotSubtype("{void f1,null f2}","{null f2,any f3}"); }
	@Test public void test_1157() { checkNotSubtype("{void f1,null f2}","{null f1,null f2}"); }
	@Test public void test_1158() { checkNotSubtype("{void f1,null f2}","{null f2,null f3}"); }
	@Test public void test_1159() { checkNotSubtype("{void f1,null f2}","{null f1,int f2}"); }
	@Test public void test_1160() { checkNotSubtype("{void f1,null f2}","{null f2,int f3}"); }
	@Test public void test_1161() { checkIsSubtype("{void f1,null f2}","{int f1,void f2}"); }
	@Test public void test_1162() { checkIsSubtype("{void f1,null f2}","{int f2,void f3}"); }
	@Test public void test_1163() { checkNotSubtype("{void f1,null f2}","{int f1,any f2}"); }
	@Test public void test_1164() { checkNotSubtype("{void f1,null f2}","{int f2,any f3}"); }
	@Test public void test_1165() { checkNotSubtype("{void f1,null f2}","{int f1,null f2}"); }
	@Test public void test_1166() { checkNotSubtype("{void f1,null f2}","{int f2,null f3}"); }
	@Test public void test_1167() { checkNotSubtype("{void f1,null f2}","{int f1,int f2}"); }
	@Test public void test_1168() { checkNotSubtype("{void f1,null f2}","{int f2,int f3}"); }
	@Test public void test_1169() { checkIsSubtype("{void f1,null f2}","{{void f1} f1}"); }
	@Test public void test_1170() { checkIsSubtype("{void f1,null f2}","{{void f2} f1}"); }
	@Test public void test_1171() { checkIsSubtype("{void f1,null f2}","{{void f1} f2}"); }
	@Test public void test_1172() { checkIsSubtype("{void f1,null f2}","{{void f2} f2}"); }
	@Test public void test_1173() { checkIsSubtype("{void f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1174() { checkIsSubtype("{void f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1175() { checkIsSubtype("{void f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1176() { checkIsSubtype("{void f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1177() { checkNotSubtype("{void f1,null f2}","{{any f1} f1}"); }
	@Test public void test_1178() { checkNotSubtype("{void f1,null f2}","{{any f2} f1}"); }
	@Test public void test_1179() { checkNotSubtype("{void f1,null f2}","{{any f1} f2}"); }
	@Test public void test_1180() { checkNotSubtype("{void f1,null f2}","{{any f2} f2}"); }
	@Test public void test_1181() { checkNotSubtype("{void f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1182() { checkNotSubtype("{void f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1183() { checkNotSubtype("{void f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1184() { checkNotSubtype("{void f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1185() { checkNotSubtype("{void f1,null f2}","{{null f1} f1}"); }
	@Test public void test_1186() { checkNotSubtype("{void f1,null f2}","{{null f2} f1}"); }
	@Test public void test_1187() { checkNotSubtype("{void f1,null f2}","{{null f1} f2}"); }
	@Test public void test_1188() { checkNotSubtype("{void f1,null f2}","{{null f2} f2}"); }
	@Test public void test_1189() { checkNotSubtype("{void f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1190() { checkNotSubtype("{void f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1191() { checkNotSubtype("{void f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1192() { checkNotSubtype("{void f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1193() { checkNotSubtype("{void f1,null f2}","{{int f1} f1}"); }
	@Test public void test_1194() { checkNotSubtype("{void f1,null f2}","{{int f2} f1}"); }
	@Test public void test_1195() { checkNotSubtype("{void f1,null f2}","{{int f1} f2}"); }
	@Test public void test_1196() { checkNotSubtype("{void f1,null f2}","{{int f2} f2}"); }
	@Test public void test_1197() { checkNotSubtype("{void f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1198() { checkNotSubtype("{void f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1199() { checkNotSubtype("{void f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1200() { checkNotSubtype("{void f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1201() { checkNotSubtype("{void f2,null f3}","any"); }
	@Test public void test_1202() { checkNotSubtype("{void f2,null f3}","null"); }
	@Test public void test_1203() { checkNotSubtype("{void f2,null f3}","int"); }
	@Test public void test_1204() { checkIsSubtype("{void f2,null f3}","{void f1}"); }
	@Test public void test_1205() { checkIsSubtype("{void f2,null f3}","{void f2}"); }
	@Test public void test_1206() { checkNotSubtype("{void f2,null f3}","{any f1}"); }
	@Test public void test_1207() { checkNotSubtype("{void f2,null f3}","{any f2}"); }
	@Test public void test_1208() { checkNotSubtype("{void f2,null f3}","{null f1}"); }
	@Test public void test_1209() { checkNotSubtype("{void f2,null f3}","{null f2}"); }
	@Test public void test_1210() { checkNotSubtype("{void f2,null f3}","{int f1}"); }
	@Test public void test_1211() { checkNotSubtype("{void f2,null f3}","{int f2}"); }
	@Test public void test_1212() { checkIsSubtype("{void f2,null f3}","{void f1,void f2}"); }
	@Test public void test_1213() { checkIsSubtype("{void f2,null f3}","{void f2,void f3}"); }
	@Test public void test_1214() { checkIsSubtype("{void f2,null f3}","{void f1,any f2}"); }
	@Test public void test_1215() { checkIsSubtype("{void f2,null f3}","{void f2,any f3}"); }
	@Test public void test_1216() { checkIsSubtype("{void f2,null f3}","{void f1,null f2}"); }
	@Test public void test_1217() { checkIsSubtype("{void f2,null f3}","{void f2,null f3}"); }
	@Test public void test_1218() { checkIsSubtype("{void f2,null f3}","{void f1,int f2}"); }
	@Test public void test_1219() { checkIsSubtype("{void f2,null f3}","{void f2,int f3}"); }
	@Test public void test_1220() { checkIsSubtype("{void f2,null f3}","{any f1,void f2}"); }
	@Test public void test_1221() { checkIsSubtype("{void f2,null f3}","{any f2,void f3}"); }
	@Test public void test_1222() { checkNotSubtype("{void f2,null f3}","{any f1,any f2}"); }
	@Test public void test_1223() { checkNotSubtype("{void f2,null f3}","{any f2,any f3}"); }
	@Test public void test_1224() { checkNotSubtype("{void f2,null f3}","{any f1,null f2}"); }
	@Test public void test_1225() { checkNotSubtype("{void f2,null f3}","{any f2,null f3}"); }
	@Test public void test_1226() { checkNotSubtype("{void f2,null f3}","{any f1,int f2}"); }
	@Test public void test_1227() { checkNotSubtype("{void f2,null f3}","{any f2,int f3}"); }
	@Test public void test_1228() { checkIsSubtype("{void f2,null f3}","{null f1,void f2}"); }
	@Test public void test_1229() { checkIsSubtype("{void f2,null f3}","{null f2,void f3}"); }
	@Test public void test_1230() { checkNotSubtype("{void f2,null f3}","{null f1,any f2}"); }
	@Test public void test_1231() { checkNotSubtype("{void f2,null f3}","{null f2,any f3}"); }
	@Test public void test_1232() { checkNotSubtype("{void f2,null f3}","{null f1,null f2}"); }
	@Test public void test_1233() { checkNotSubtype("{void f2,null f3}","{null f2,null f3}"); }
	@Test public void test_1234() { checkNotSubtype("{void f2,null f3}","{null f1,int f2}"); }
	@Test public void test_1235() { checkNotSubtype("{void f2,null f3}","{null f2,int f3}"); }
	@Test public void test_1236() { checkIsSubtype("{void f2,null f3}","{int f1,void f2}"); }
	@Test public void test_1237() { checkIsSubtype("{void f2,null f3}","{int f2,void f3}"); }
	@Test public void test_1238() { checkNotSubtype("{void f2,null f3}","{int f1,any f2}"); }
	@Test public void test_1239() { checkNotSubtype("{void f2,null f3}","{int f2,any f3}"); }
	@Test public void test_1240() { checkNotSubtype("{void f2,null f3}","{int f1,null f2}"); }
	@Test public void test_1241() { checkNotSubtype("{void f2,null f3}","{int f2,null f3}"); }
	@Test public void test_1242() { checkNotSubtype("{void f2,null f3}","{int f1,int f2}"); }
	@Test public void test_1243() { checkNotSubtype("{void f2,null f3}","{int f2,int f3}"); }
	@Test public void test_1244() { checkIsSubtype("{void f2,null f3}","{{void f1} f1}"); }
	@Test public void test_1245() { checkIsSubtype("{void f2,null f3}","{{void f2} f1}"); }
	@Test public void test_1246() { checkIsSubtype("{void f2,null f3}","{{void f1} f2}"); }
	@Test public void test_1247() { checkIsSubtype("{void f2,null f3}","{{void f2} f2}"); }
	@Test public void test_1248() { checkIsSubtype("{void f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_1249() { checkIsSubtype("{void f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_1250() { checkIsSubtype("{void f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_1251() { checkIsSubtype("{void f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_1252() { checkNotSubtype("{void f2,null f3}","{{any f1} f1}"); }
	@Test public void test_1253() { checkNotSubtype("{void f2,null f3}","{{any f2} f1}"); }
	@Test public void test_1254() { checkNotSubtype("{void f2,null f3}","{{any f1} f2}"); }
	@Test public void test_1255() { checkNotSubtype("{void f2,null f3}","{{any f2} f2}"); }
	@Test public void test_1256() { checkNotSubtype("{void f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_1257() { checkNotSubtype("{void f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_1258() { checkNotSubtype("{void f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_1259() { checkNotSubtype("{void f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_1260() { checkNotSubtype("{void f2,null f3}","{{null f1} f1}"); }
	@Test public void test_1261() { checkNotSubtype("{void f2,null f3}","{{null f2} f1}"); }
	@Test public void test_1262() { checkNotSubtype("{void f2,null f3}","{{null f1} f2}"); }
	@Test public void test_1263() { checkNotSubtype("{void f2,null f3}","{{null f2} f2}"); }
	@Test public void test_1264() { checkNotSubtype("{void f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_1265() { checkNotSubtype("{void f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_1266() { checkNotSubtype("{void f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_1267() { checkNotSubtype("{void f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_1268() { checkNotSubtype("{void f2,null f3}","{{int f1} f1}"); }
	@Test public void test_1269() { checkNotSubtype("{void f2,null f3}","{{int f2} f1}"); }
	@Test public void test_1270() { checkNotSubtype("{void f2,null f3}","{{int f1} f2}"); }
	@Test public void test_1271() { checkNotSubtype("{void f2,null f3}","{{int f2} f2}"); }
	@Test public void test_1272() { checkNotSubtype("{void f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_1273() { checkNotSubtype("{void f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_1274() { checkNotSubtype("{void f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_1275() { checkNotSubtype("{void f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_1276() { checkNotSubtype("{void f1,int f2}","any"); }
	@Test public void test_1277() { checkNotSubtype("{void f1,int f2}","null"); }
	@Test public void test_1278() { checkNotSubtype("{void f1,int f2}","int"); }
	@Test public void test_1279() { checkIsSubtype("{void f1,int f2}","{void f1}"); }
	@Test public void test_1280() { checkIsSubtype("{void f1,int f2}","{void f2}"); }
	@Test public void test_1281() { checkNotSubtype("{void f1,int f2}","{any f1}"); }
	@Test public void test_1282() { checkNotSubtype("{void f1,int f2}","{any f2}"); }
	@Test public void test_1283() { checkNotSubtype("{void f1,int f2}","{null f1}"); }
	@Test public void test_1284() { checkNotSubtype("{void f1,int f2}","{null f2}"); }
	@Test public void test_1285() { checkNotSubtype("{void f1,int f2}","{int f1}"); }
	@Test public void test_1286() { checkNotSubtype("{void f1,int f2}","{int f2}"); }
	@Test public void test_1287() { checkIsSubtype("{void f1,int f2}","{void f1,void f2}"); }
	@Test public void test_1288() { checkIsSubtype("{void f1,int f2}","{void f2,void f3}"); }
	@Test public void test_1289() { checkIsSubtype("{void f1,int f2}","{void f1,any f2}"); }
	@Test public void test_1290() { checkIsSubtype("{void f1,int f2}","{void f2,any f3}"); }
	@Test public void test_1291() { checkIsSubtype("{void f1,int f2}","{void f1,null f2}"); }
	@Test public void test_1292() { checkIsSubtype("{void f1,int f2}","{void f2,null f3}"); }
	@Test public void test_1293() { checkIsSubtype("{void f1,int f2}","{void f1,int f2}"); }
	@Test public void test_1294() { checkIsSubtype("{void f1,int f2}","{void f2,int f3}"); }
	@Test public void test_1295() { checkIsSubtype("{void f1,int f2}","{any f1,void f2}"); }
	@Test public void test_1296() { checkIsSubtype("{void f1,int f2}","{any f2,void f3}"); }
	@Test public void test_1297() { checkNotSubtype("{void f1,int f2}","{any f1,any f2}"); }
	@Test public void test_1298() { checkNotSubtype("{void f1,int f2}","{any f2,any f3}"); }
	@Test public void test_1299() { checkNotSubtype("{void f1,int f2}","{any f1,null f2}"); }
	@Test public void test_1300() { checkNotSubtype("{void f1,int f2}","{any f2,null f3}"); }
	@Test public void test_1301() { checkNotSubtype("{void f1,int f2}","{any f1,int f2}"); }
	@Test public void test_1302() { checkNotSubtype("{void f1,int f2}","{any f2,int f3}"); }
	@Test public void test_1303() { checkIsSubtype("{void f1,int f2}","{null f1,void f2}"); }
	@Test public void test_1304() { checkIsSubtype("{void f1,int f2}","{null f2,void f3}"); }
	@Test public void test_1305() { checkNotSubtype("{void f1,int f2}","{null f1,any f2}"); }
	@Test public void test_1306() { checkNotSubtype("{void f1,int f2}","{null f2,any f3}"); }
	@Test public void test_1307() { checkNotSubtype("{void f1,int f2}","{null f1,null f2}"); }
	@Test public void test_1308() { checkNotSubtype("{void f1,int f2}","{null f2,null f3}"); }
	@Test public void test_1309() { checkNotSubtype("{void f1,int f2}","{null f1,int f2}"); }
	@Test public void test_1310() { checkNotSubtype("{void f1,int f2}","{null f2,int f3}"); }
	@Test public void test_1311() { checkIsSubtype("{void f1,int f2}","{int f1,void f2}"); }
	@Test public void test_1312() { checkIsSubtype("{void f1,int f2}","{int f2,void f3}"); }
	@Test public void test_1313() { checkNotSubtype("{void f1,int f2}","{int f1,any f2}"); }
	@Test public void test_1314() { checkNotSubtype("{void f1,int f2}","{int f2,any f3}"); }
	@Test public void test_1315() { checkNotSubtype("{void f1,int f2}","{int f1,null f2}"); }
	@Test public void test_1316() { checkNotSubtype("{void f1,int f2}","{int f2,null f3}"); }
	@Test public void test_1317() { checkNotSubtype("{void f1,int f2}","{int f1,int f2}"); }
	@Test public void test_1318() { checkNotSubtype("{void f1,int f2}","{int f2,int f3}"); }
	@Test public void test_1319() { checkIsSubtype("{void f1,int f2}","{{void f1} f1}"); }
	@Test public void test_1320() { checkIsSubtype("{void f1,int f2}","{{void f2} f1}"); }
	@Test public void test_1321() { checkIsSubtype("{void f1,int f2}","{{void f1} f2}"); }
	@Test public void test_1322() { checkIsSubtype("{void f1,int f2}","{{void f2} f2}"); }
	@Test public void test_1323() { checkIsSubtype("{void f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1324() { checkIsSubtype("{void f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1325() { checkIsSubtype("{void f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1326() { checkIsSubtype("{void f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1327() { checkNotSubtype("{void f1,int f2}","{{any f1} f1}"); }
	@Test public void test_1328() { checkNotSubtype("{void f1,int f2}","{{any f2} f1}"); }
	@Test public void test_1329() { checkNotSubtype("{void f1,int f2}","{{any f1} f2}"); }
	@Test public void test_1330() { checkNotSubtype("{void f1,int f2}","{{any f2} f2}"); }
	@Test public void test_1331() { checkNotSubtype("{void f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1332() { checkNotSubtype("{void f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1333() { checkNotSubtype("{void f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1334() { checkNotSubtype("{void f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1335() { checkNotSubtype("{void f1,int f2}","{{null f1} f1}"); }
	@Test public void test_1336() { checkNotSubtype("{void f1,int f2}","{{null f2} f1}"); }
	@Test public void test_1337() { checkNotSubtype("{void f1,int f2}","{{null f1} f2}"); }
	@Test public void test_1338() { checkNotSubtype("{void f1,int f2}","{{null f2} f2}"); }
	@Test public void test_1339() { checkNotSubtype("{void f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1340() { checkNotSubtype("{void f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1341() { checkNotSubtype("{void f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1342() { checkNotSubtype("{void f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1343() { checkNotSubtype("{void f1,int f2}","{{int f1} f1}"); }
	@Test public void test_1344() { checkNotSubtype("{void f1,int f2}","{{int f2} f1}"); }
	@Test public void test_1345() { checkNotSubtype("{void f1,int f2}","{{int f1} f2}"); }
	@Test public void test_1346() { checkNotSubtype("{void f1,int f2}","{{int f2} f2}"); }
	@Test public void test_1347() { checkNotSubtype("{void f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1348() { checkNotSubtype("{void f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1349() { checkNotSubtype("{void f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1350() { checkNotSubtype("{void f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1351() { checkNotSubtype("{void f2,int f3}","any"); }
	@Test public void test_1352() { checkNotSubtype("{void f2,int f3}","null"); }
	@Test public void test_1353() { checkNotSubtype("{void f2,int f3}","int"); }
	@Test public void test_1354() { checkIsSubtype("{void f2,int f3}","{void f1}"); }
	@Test public void test_1355() { checkIsSubtype("{void f2,int f3}","{void f2}"); }
	@Test public void test_1356() { checkNotSubtype("{void f2,int f3}","{any f1}"); }
	@Test public void test_1357() { checkNotSubtype("{void f2,int f3}","{any f2}"); }
	@Test public void test_1358() { checkNotSubtype("{void f2,int f3}","{null f1}"); }
	@Test public void test_1359() { checkNotSubtype("{void f2,int f3}","{null f2}"); }
	@Test public void test_1360() { checkNotSubtype("{void f2,int f3}","{int f1}"); }
	@Test public void test_1361() { checkNotSubtype("{void f2,int f3}","{int f2}"); }
	@Test public void test_1362() { checkIsSubtype("{void f2,int f3}","{void f1,void f2}"); }
	@Test public void test_1363() { checkIsSubtype("{void f2,int f3}","{void f2,void f3}"); }
	@Test public void test_1364() { checkIsSubtype("{void f2,int f3}","{void f1,any f2}"); }
	@Test public void test_1365() { checkIsSubtype("{void f2,int f3}","{void f2,any f3}"); }
	@Test public void test_1366() { checkIsSubtype("{void f2,int f3}","{void f1,null f2}"); }
	@Test public void test_1367() { checkIsSubtype("{void f2,int f3}","{void f2,null f3}"); }
	@Test public void test_1368() { checkIsSubtype("{void f2,int f3}","{void f1,int f2}"); }
	@Test public void test_1369() { checkIsSubtype("{void f2,int f3}","{void f2,int f3}"); }
	@Test public void test_1370() { checkIsSubtype("{void f2,int f3}","{any f1,void f2}"); }
	@Test public void test_1371() { checkIsSubtype("{void f2,int f3}","{any f2,void f3}"); }
	@Test public void test_1372() { checkNotSubtype("{void f2,int f3}","{any f1,any f2}"); }
	@Test public void test_1373() { checkNotSubtype("{void f2,int f3}","{any f2,any f3}"); }
	@Test public void test_1374() { checkNotSubtype("{void f2,int f3}","{any f1,null f2}"); }
	@Test public void test_1375() { checkNotSubtype("{void f2,int f3}","{any f2,null f3}"); }
	@Test public void test_1376() { checkNotSubtype("{void f2,int f3}","{any f1,int f2}"); }
	@Test public void test_1377() { checkNotSubtype("{void f2,int f3}","{any f2,int f3}"); }
	@Test public void test_1378() { checkIsSubtype("{void f2,int f3}","{null f1,void f2}"); }
	@Test public void test_1379() { checkIsSubtype("{void f2,int f3}","{null f2,void f3}"); }
	@Test public void test_1380() { checkNotSubtype("{void f2,int f3}","{null f1,any f2}"); }
	@Test public void test_1381() { checkNotSubtype("{void f2,int f3}","{null f2,any f3}"); }
	@Test public void test_1382() { checkNotSubtype("{void f2,int f3}","{null f1,null f2}"); }
	@Test public void test_1383() { checkNotSubtype("{void f2,int f3}","{null f2,null f3}"); }
	@Test public void test_1384() { checkNotSubtype("{void f2,int f3}","{null f1,int f2}"); }
	@Test public void test_1385() { checkNotSubtype("{void f2,int f3}","{null f2,int f3}"); }
	@Test public void test_1386() { checkIsSubtype("{void f2,int f3}","{int f1,void f2}"); }
	@Test public void test_1387() { checkIsSubtype("{void f2,int f3}","{int f2,void f3}"); }
	@Test public void test_1388() { checkNotSubtype("{void f2,int f3}","{int f1,any f2}"); }
	@Test public void test_1389() { checkNotSubtype("{void f2,int f3}","{int f2,any f3}"); }
	@Test public void test_1390() { checkNotSubtype("{void f2,int f3}","{int f1,null f2}"); }
	@Test public void test_1391() { checkNotSubtype("{void f2,int f3}","{int f2,null f3}"); }
	@Test public void test_1392() { checkNotSubtype("{void f2,int f3}","{int f1,int f2}"); }
	@Test public void test_1393() { checkNotSubtype("{void f2,int f3}","{int f2,int f3}"); }
	@Test public void test_1394() { checkIsSubtype("{void f2,int f3}","{{void f1} f1}"); }
	@Test public void test_1395() { checkIsSubtype("{void f2,int f3}","{{void f2} f1}"); }
	@Test public void test_1396() { checkIsSubtype("{void f2,int f3}","{{void f1} f2}"); }
	@Test public void test_1397() { checkIsSubtype("{void f2,int f3}","{{void f2} f2}"); }
	@Test public void test_1398() { checkIsSubtype("{void f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_1399() { checkIsSubtype("{void f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_1400() { checkIsSubtype("{void f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_1401() { checkIsSubtype("{void f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_1402() { checkNotSubtype("{void f2,int f3}","{{any f1} f1}"); }
	@Test public void test_1403() { checkNotSubtype("{void f2,int f3}","{{any f2} f1}"); }
	@Test public void test_1404() { checkNotSubtype("{void f2,int f3}","{{any f1} f2}"); }
	@Test public void test_1405() { checkNotSubtype("{void f2,int f3}","{{any f2} f2}"); }
	@Test public void test_1406() { checkNotSubtype("{void f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_1407() { checkNotSubtype("{void f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_1408() { checkNotSubtype("{void f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_1409() { checkNotSubtype("{void f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_1410() { checkNotSubtype("{void f2,int f3}","{{null f1} f1}"); }
	@Test public void test_1411() { checkNotSubtype("{void f2,int f3}","{{null f2} f1}"); }
	@Test public void test_1412() { checkNotSubtype("{void f2,int f3}","{{null f1} f2}"); }
	@Test public void test_1413() { checkNotSubtype("{void f2,int f3}","{{null f2} f2}"); }
	@Test public void test_1414() { checkNotSubtype("{void f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_1415() { checkNotSubtype("{void f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_1416() { checkNotSubtype("{void f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_1417() { checkNotSubtype("{void f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_1418() { checkNotSubtype("{void f2,int f3}","{{int f1} f1}"); }
	@Test public void test_1419() { checkNotSubtype("{void f2,int f3}","{{int f2} f1}"); }
	@Test public void test_1420() { checkNotSubtype("{void f2,int f3}","{{int f1} f2}"); }
	@Test public void test_1421() { checkNotSubtype("{void f2,int f3}","{{int f2} f2}"); }
	@Test public void test_1422() { checkNotSubtype("{void f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_1423() { checkNotSubtype("{void f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_1424() { checkNotSubtype("{void f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_1425() { checkNotSubtype("{void f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_1426() { checkNotSubtype("{any f1,void f2}","any"); }
	@Test public void test_1427() { checkNotSubtype("{any f1,void f2}","null"); }
	@Test public void test_1428() { checkNotSubtype("{any f1,void f2}","int"); }
	@Test public void test_1429() { checkIsSubtype("{any f1,void f2}","{void f1}"); }
	@Test public void test_1430() { checkIsSubtype("{any f1,void f2}","{void f2}"); }
	@Test public void test_1431() { checkNotSubtype("{any f1,void f2}","{any f1}"); }
	@Test public void test_1432() { checkNotSubtype("{any f1,void f2}","{any f2}"); }
	@Test public void test_1433() { checkNotSubtype("{any f1,void f2}","{null f1}"); }
	@Test public void test_1434() { checkNotSubtype("{any f1,void f2}","{null f2}"); }
	@Test public void test_1435() { checkNotSubtype("{any f1,void f2}","{int f1}"); }
	@Test public void test_1436() { checkNotSubtype("{any f1,void f2}","{int f2}"); }
	@Test public void test_1437() { checkIsSubtype("{any f1,void f2}","{void f1,void f2}"); }
	@Test public void test_1438() { checkIsSubtype("{any f1,void f2}","{void f2,void f3}"); }
	@Test public void test_1439() { checkIsSubtype("{any f1,void f2}","{void f1,any f2}"); }
	@Test public void test_1440() { checkIsSubtype("{any f1,void f2}","{void f2,any f3}"); }
	@Test public void test_1441() { checkIsSubtype("{any f1,void f2}","{void f1,null f2}"); }
	@Test public void test_1442() { checkIsSubtype("{any f1,void f2}","{void f2,null f3}"); }
	@Test public void test_1443() { checkIsSubtype("{any f1,void f2}","{void f1,int f2}"); }
	@Test public void test_1444() { checkIsSubtype("{any f1,void f2}","{void f2,int f3}"); }
	@Test public void test_1445() { checkIsSubtype("{any f1,void f2}","{any f1,void f2}"); }
	@Test public void test_1446() { checkIsSubtype("{any f1,void f2}","{any f2,void f3}"); }
	@Test public void test_1447() { checkNotSubtype("{any f1,void f2}","{any f1,any f2}"); }
	@Test public void test_1448() { checkNotSubtype("{any f1,void f2}","{any f2,any f3}"); }
	@Test public void test_1449() { checkNotSubtype("{any f1,void f2}","{any f1,null f2}"); }
	@Test public void test_1450() { checkNotSubtype("{any f1,void f2}","{any f2,null f3}"); }
	@Test public void test_1451() { checkNotSubtype("{any f1,void f2}","{any f1,int f2}"); }
	@Test public void test_1452() { checkNotSubtype("{any f1,void f2}","{any f2,int f3}"); }
	@Test public void test_1453() { checkIsSubtype("{any f1,void f2}","{null f1,void f2}"); }
	@Test public void test_1454() { checkIsSubtype("{any f1,void f2}","{null f2,void f3}"); }
	@Test public void test_1455() { checkNotSubtype("{any f1,void f2}","{null f1,any f2}"); }
	@Test public void test_1456() { checkNotSubtype("{any f1,void f2}","{null f2,any f3}"); }
	@Test public void test_1457() { checkNotSubtype("{any f1,void f2}","{null f1,null f2}"); }
	@Test public void test_1458() { checkNotSubtype("{any f1,void f2}","{null f2,null f3}"); }
	@Test public void test_1459() { checkNotSubtype("{any f1,void f2}","{null f1,int f2}"); }
	@Test public void test_1460() { checkNotSubtype("{any f1,void f2}","{null f2,int f3}"); }
	@Test public void test_1461() { checkIsSubtype("{any f1,void f2}","{int f1,void f2}"); }
	@Test public void test_1462() { checkIsSubtype("{any f1,void f2}","{int f2,void f3}"); }
	@Test public void test_1463() { checkNotSubtype("{any f1,void f2}","{int f1,any f2}"); }
	@Test public void test_1464() { checkNotSubtype("{any f1,void f2}","{int f2,any f3}"); }
	@Test public void test_1465() { checkNotSubtype("{any f1,void f2}","{int f1,null f2}"); }
	@Test public void test_1466() { checkNotSubtype("{any f1,void f2}","{int f2,null f3}"); }
	@Test public void test_1467() { checkNotSubtype("{any f1,void f2}","{int f1,int f2}"); }
	@Test public void test_1468() { checkNotSubtype("{any f1,void f2}","{int f2,int f3}"); }
	@Test public void test_1469() { checkIsSubtype("{any f1,void f2}","{{void f1} f1}"); }
	@Test public void test_1470() { checkIsSubtype("{any f1,void f2}","{{void f2} f1}"); }
	@Test public void test_1471() { checkIsSubtype("{any f1,void f2}","{{void f1} f2}"); }
	@Test public void test_1472() { checkIsSubtype("{any f1,void f2}","{{void f2} f2}"); }
	@Test public void test_1473() { checkIsSubtype("{any f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1474() { checkIsSubtype("{any f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1475() { checkIsSubtype("{any f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1476() { checkIsSubtype("{any f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1477() { checkNotSubtype("{any f1,void f2}","{{any f1} f1}"); }
	@Test public void test_1478() { checkNotSubtype("{any f1,void f2}","{{any f2} f1}"); }
	@Test public void test_1479() { checkNotSubtype("{any f1,void f2}","{{any f1} f2}"); }
	@Test public void test_1480() { checkNotSubtype("{any f1,void f2}","{{any f2} f2}"); }
	@Test public void test_1481() { checkNotSubtype("{any f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1482() { checkNotSubtype("{any f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1483() { checkNotSubtype("{any f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1484() { checkNotSubtype("{any f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1485() { checkNotSubtype("{any f1,void f2}","{{null f1} f1}"); }
	@Test public void test_1486() { checkNotSubtype("{any f1,void f2}","{{null f2} f1}"); }
	@Test public void test_1487() { checkNotSubtype("{any f1,void f2}","{{null f1} f2}"); }
	@Test public void test_1488() { checkNotSubtype("{any f1,void f2}","{{null f2} f2}"); }
	@Test public void test_1489() { checkNotSubtype("{any f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1490() { checkNotSubtype("{any f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1491() { checkNotSubtype("{any f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1492() { checkNotSubtype("{any f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1493() { checkNotSubtype("{any f1,void f2}","{{int f1} f1}"); }
	@Test public void test_1494() { checkNotSubtype("{any f1,void f2}","{{int f2} f1}"); }
	@Test public void test_1495() { checkNotSubtype("{any f1,void f2}","{{int f1} f2}"); }
	@Test public void test_1496() { checkNotSubtype("{any f1,void f2}","{{int f2} f2}"); }
	@Test public void test_1497() { checkNotSubtype("{any f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1498() { checkNotSubtype("{any f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1499() { checkNotSubtype("{any f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1500() { checkNotSubtype("{any f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1501() { checkNotSubtype("{any f2,void f3}","any"); }
	@Test public void test_1502() { checkNotSubtype("{any f2,void f3}","null"); }
	@Test public void test_1503() { checkNotSubtype("{any f2,void f3}","int"); }
	@Test public void test_1504() { checkIsSubtype("{any f2,void f3}","{void f1}"); }
	@Test public void test_1505() { checkIsSubtype("{any f2,void f3}","{void f2}"); }
	@Test public void test_1506() { checkNotSubtype("{any f2,void f3}","{any f1}"); }
	@Test public void test_1507() { checkNotSubtype("{any f2,void f3}","{any f2}"); }
	@Test public void test_1508() { checkNotSubtype("{any f2,void f3}","{null f1}"); }
	@Test public void test_1509() { checkNotSubtype("{any f2,void f3}","{null f2}"); }
	@Test public void test_1510() { checkNotSubtype("{any f2,void f3}","{int f1}"); }
	@Test public void test_1511() { checkNotSubtype("{any f2,void f3}","{int f2}"); }
	@Test public void test_1512() { checkIsSubtype("{any f2,void f3}","{void f1,void f2}"); }
	@Test public void test_1513() { checkIsSubtype("{any f2,void f3}","{void f2,void f3}"); }
	@Test public void test_1514() { checkIsSubtype("{any f2,void f3}","{void f1,any f2}"); }
	@Test public void test_1515() { checkIsSubtype("{any f2,void f3}","{void f2,any f3}"); }
	@Test public void test_1516() { checkIsSubtype("{any f2,void f3}","{void f1,null f2}"); }
	@Test public void test_1517() { checkIsSubtype("{any f2,void f3}","{void f2,null f3}"); }
	@Test public void test_1518() { checkIsSubtype("{any f2,void f3}","{void f1,int f2}"); }
	@Test public void test_1519() { checkIsSubtype("{any f2,void f3}","{void f2,int f3}"); }
	@Test public void test_1520() { checkIsSubtype("{any f2,void f3}","{any f1,void f2}"); }
	@Test public void test_1521() { checkIsSubtype("{any f2,void f3}","{any f2,void f3}"); }
	@Test public void test_1522() { checkNotSubtype("{any f2,void f3}","{any f1,any f2}"); }
	@Test public void test_1523() { checkNotSubtype("{any f2,void f3}","{any f2,any f3}"); }
	@Test public void test_1524() { checkNotSubtype("{any f2,void f3}","{any f1,null f2}"); }
	@Test public void test_1525() { checkNotSubtype("{any f2,void f3}","{any f2,null f3}"); }
	@Test public void test_1526() { checkNotSubtype("{any f2,void f3}","{any f1,int f2}"); }
	@Test public void test_1527() { checkNotSubtype("{any f2,void f3}","{any f2,int f3}"); }
	@Test public void test_1528() { checkIsSubtype("{any f2,void f3}","{null f1,void f2}"); }
	@Test public void test_1529() { checkIsSubtype("{any f2,void f3}","{null f2,void f3}"); }
	@Test public void test_1530() { checkNotSubtype("{any f2,void f3}","{null f1,any f2}"); }
	@Test public void test_1531() { checkNotSubtype("{any f2,void f3}","{null f2,any f3}"); }
	@Test public void test_1532() { checkNotSubtype("{any f2,void f3}","{null f1,null f2}"); }
	@Test public void test_1533() { checkNotSubtype("{any f2,void f3}","{null f2,null f3}"); }
	@Test public void test_1534() { checkNotSubtype("{any f2,void f3}","{null f1,int f2}"); }
	@Test public void test_1535() { checkNotSubtype("{any f2,void f3}","{null f2,int f3}"); }
	@Test public void test_1536() { checkIsSubtype("{any f2,void f3}","{int f1,void f2}"); }
	@Test public void test_1537() { checkIsSubtype("{any f2,void f3}","{int f2,void f3}"); }
	@Test public void test_1538() { checkNotSubtype("{any f2,void f3}","{int f1,any f2}"); }
	@Test public void test_1539() { checkNotSubtype("{any f2,void f3}","{int f2,any f3}"); }
	@Test public void test_1540() { checkNotSubtype("{any f2,void f3}","{int f1,null f2}"); }
	@Test public void test_1541() { checkNotSubtype("{any f2,void f3}","{int f2,null f3}"); }
	@Test public void test_1542() { checkNotSubtype("{any f2,void f3}","{int f1,int f2}"); }
	@Test public void test_1543() { checkNotSubtype("{any f2,void f3}","{int f2,int f3}"); }
	@Test public void test_1544() { checkIsSubtype("{any f2,void f3}","{{void f1} f1}"); }
	@Test public void test_1545() { checkIsSubtype("{any f2,void f3}","{{void f2} f1}"); }
	@Test public void test_1546() { checkIsSubtype("{any f2,void f3}","{{void f1} f2}"); }
	@Test public void test_1547() { checkIsSubtype("{any f2,void f3}","{{void f2} f2}"); }
	@Test public void test_1548() { checkIsSubtype("{any f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_1549() { checkIsSubtype("{any f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_1550() { checkIsSubtype("{any f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_1551() { checkIsSubtype("{any f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_1552() { checkNotSubtype("{any f2,void f3}","{{any f1} f1}"); }
	@Test public void test_1553() { checkNotSubtype("{any f2,void f3}","{{any f2} f1}"); }
	@Test public void test_1554() { checkNotSubtype("{any f2,void f3}","{{any f1} f2}"); }
	@Test public void test_1555() { checkNotSubtype("{any f2,void f3}","{{any f2} f2}"); }
	@Test public void test_1556() { checkNotSubtype("{any f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_1557() { checkNotSubtype("{any f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_1558() { checkNotSubtype("{any f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_1559() { checkNotSubtype("{any f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_1560() { checkNotSubtype("{any f2,void f3}","{{null f1} f1}"); }
	@Test public void test_1561() { checkNotSubtype("{any f2,void f3}","{{null f2} f1}"); }
	@Test public void test_1562() { checkNotSubtype("{any f2,void f3}","{{null f1} f2}"); }
	@Test public void test_1563() { checkNotSubtype("{any f2,void f3}","{{null f2} f2}"); }
	@Test public void test_1564() { checkNotSubtype("{any f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_1565() { checkNotSubtype("{any f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_1566() { checkNotSubtype("{any f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_1567() { checkNotSubtype("{any f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_1568() { checkNotSubtype("{any f2,void f3}","{{int f1} f1}"); }
	@Test public void test_1569() { checkNotSubtype("{any f2,void f3}","{{int f2} f1}"); }
	@Test public void test_1570() { checkNotSubtype("{any f2,void f3}","{{int f1} f2}"); }
	@Test public void test_1571() { checkNotSubtype("{any f2,void f3}","{{int f2} f2}"); }
	@Test public void test_1572() { checkNotSubtype("{any f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_1573() { checkNotSubtype("{any f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_1574() { checkNotSubtype("{any f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_1575() { checkNotSubtype("{any f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_1576() { checkNotSubtype("{any f1,any f2}","any"); }
	@Test public void test_1577() { checkNotSubtype("{any f1,any f2}","null"); }
	@Test public void test_1578() { checkNotSubtype("{any f1,any f2}","int"); }
	@Test public void test_1579() { checkIsSubtype("{any f1,any f2}","{void f1}"); }
	@Test public void test_1580() { checkIsSubtype("{any f1,any f2}","{void f2}"); }
	@Test public void test_1581() { checkNotSubtype("{any f1,any f2}","{any f1}"); }
	@Test public void test_1582() { checkNotSubtype("{any f1,any f2}","{any f2}"); }
	@Test public void test_1583() { checkNotSubtype("{any f1,any f2}","{null f1}"); }
	@Test public void test_1584() { checkNotSubtype("{any f1,any f2}","{null f2}"); }
	@Test public void test_1585() { checkNotSubtype("{any f1,any f2}","{int f1}"); }
	@Test public void test_1586() { checkNotSubtype("{any f1,any f2}","{int f2}"); }
	@Test public void test_1587() { checkIsSubtype("{any f1,any f2}","{void f1,void f2}"); }
	@Test public void test_1588() { checkIsSubtype("{any f1,any f2}","{void f2,void f3}"); }
	@Test public void test_1589() { checkIsSubtype("{any f1,any f2}","{void f1,any f2}"); }
	@Test public void test_1590() { checkIsSubtype("{any f1,any f2}","{void f2,any f3}"); }
	@Test public void test_1591() { checkIsSubtype("{any f1,any f2}","{void f1,null f2}"); }
	@Test public void test_1592() { checkIsSubtype("{any f1,any f2}","{void f2,null f3}"); }
	@Test public void test_1593() { checkIsSubtype("{any f1,any f2}","{void f1,int f2}"); }
	@Test public void test_1594() { checkIsSubtype("{any f1,any f2}","{void f2,int f3}"); }
	@Test public void test_1595() { checkIsSubtype("{any f1,any f2}","{any f1,void f2}"); }
	@Test public void test_1596() { checkIsSubtype("{any f1,any f2}","{any f2,void f3}"); }
	@Test public void test_1597() { checkIsSubtype("{any f1,any f2}","{any f1,any f2}"); }
	@Test public void test_1598() { checkNotSubtype("{any f1,any f2}","{any f2,any f3}"); }
	@Test public void test_1599() { checkIsSubtype("{any f1,any f2}","{any f1,null f2}"); }
	@Test public void test_1600() { checkNotSubtype("{any f1,any f2}","{any f2,null f3}"); }
	@Test public void test_1601() { checkIsSubtype("{any f1,any f2}","{any f1,int f2}"); }
	@Test public void test_1602() { checkNotSubtype("{any f1,any f2}","{any f2,int f3}"); }
	@Test public void test_1603() { checkIsSubtype("{any f1,any f2}","{null f1,void f2}"); }
	@Test public void test_1604() { checkIsSubtype("{any f1,any f2}","{null f2,void f3}"); }
	@Test public void test_1605() { checkIsSubtype("{any f1,any f2}","{null f1,any f2}"); }
	@Test public void test_1606() { checkNotSubtype("{any f1,any f2}","{null f2,any f3}"); }
	@Test public void test_1607() { checkIsSubtype("{any f1,any f2}","{null f1,null f2}"); }
	@Test public void test_1608() { checkNotSubtype("{any f1,any f2}","{null f2,null f3}"); }
	@Test public void test_1609() { checkIsSubtype("{any f1,any f2}","{null f1,int f2}"); }
	@Test public void test_1610() { checkNotSubtype("{any f1,any f2}","{null f2,int f3}"); }
	@Test public void test_1611() { checkIsSubtype("{any f1,any f2}","{int f1,void f2}"); }
	@Test public void test_1612() { checkIsSubtype("{any f1,any f2}","{int f2,void f3}"); }
	@Test public void test_1613() { checkIsSubtype("{any f1,any f2}","{int f1,any f2}"); }
	@Test public void test_1614() { checkNotSubtype("{any f1,any f2}","{int f2,any f3}"); }
	@Test public void test_1615() { checkIsSubtype("{any f1,any f2}","{int f1,null f2}"); }
	@Test public void test_1616() { checkNotSubtype("{any f1,any f2}","{int f2,null f3}"); }
	@Test public void test_1617() { checkIsSubtype("{any f1,any f2}","{int f1,int f2}"); }
	@Test public void test_1618() { checkNotSubtype("{any f1,any f2}","{int f2,int f3}"); }
	@Test public void test_1619() { checkIsSubtype("{any f1,any f2}","{{void f1} f1}"); }
	@Test public void test_1620() { checkIsSubtype("{any f1,any f2}","{{void f2} f1}"); }
	@Test public void test_1621() { checkIsSubtype("{any f1,any f2}","{{void f1} f2}"); }
	@Test public void test_1622() { checkIsSubtype("{any f1,any f2}","{{void f2} f2}"); }
	@Test public void test_1623() { checkIsSubtype("{any f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1624() { checkIsSubtype("{any f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1625() { checkIsSubtype("{any f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1626() { checkIsSubtype("{any f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1627() { checkNotSubtype("{any f1,any f2}","{{any f1} f1}"); }
	@Test public void test_1628() { checkNotSubtype("{any f1,any f2}","{{any f2} f1}"); }
	@Test public void test_1629() { checkNotSubtype("{any f1,any f2}","{{any f1} f2}"); }
	@Test public void test_1630() { checkNotSubtype("{any f1,any f2}","{{any f2} f2}"); }
	@Test public void test_1631() { checkIsSubtype("{any f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1632() { checkIsSubtype("{any f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1633() { checkNotSubtype("{any f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1634() { checkNotSubtype("{any f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1635() { checkNotSubtype("{any f1,any f2}","{{null f1} f1}"); }
	@Test public void test_1636() { checkNotSubtype("{any f1,any f2}","{{null f2} f1}"); }
	@Test public void test_1637() { checkNotSubtype("{any f1,any f2}","{{null f1} f2}"); }
	@Test public void test_1638() { checkNotSubtype("{any f1,any f2}","{{null f2} f2}"); }
	@Test public void test_1639() { checkIsSubtype("{any f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1640() { checkIsSubtype("{any f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1641() { checkNotSubtype("{any f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1642() { checkNotSubtype("{any f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1643() { checkNotSubtype("{any f1,any f2}","{{int f1} f1}"); }
	@Test public void test_1644() { checkNotSubtype("{any f1,any f2}","{{int f2} f1}"); }
	@Test public void test_1645() { checkNotSubtype("{any f1,any f2}","{{int f1} f2}"); }
	@Test public void test_1646() { checkNotSubtype("{any f1,any f2}","{{int f2} f2}"); }
	@Test public void test_1647() { checkIsSubtype("{any f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1648() { checkIsSubtype("{any f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1649() { checkNotSubtype("{any f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1650() { checkNotSubtype("{any f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1651() { checkNotSubtype("{any f2,any f3}","any"); }
	@Test public void test_1652() { checkNotSubtype("{any f2,any f3}","null"); }
	@Test public void test_1653() { checkNotSubtype("{any f2,any f3}","int"); }
	@Test public void test_1654() { checkIsSubtype("{any f2,any f3}","{void f1}"); }
	@Test public void test_1655() { checkIsSubtype("{any f2,any f3}","{void f2}"); }
	@Test public void test_1656() { checkNotSubtype("{any f2,any f3}","{any f1}"); }
	@Test public void test_1657() { checkNotSubtype("{any f2,any f3}","{any f2}"); }
	@Test public void test_1658() { checkNotSubtype("{any f2,any f3}","{null f1}"); }
	@Test public void test_1659() { checkNotSubtype("{any f2,any f3}","{null f2}"); }
	@Test public void test_1660() { checkNotSubtype("{any f2,any f3}","{int f1}"); }
	@Test public void test_1661() { checkNotSubtype("{any f2,any f3}","{int f2}"); }
	@Test public void test_1662() { checkIsSubtype("{any f2,any f3}","{void f1,void f2}"); }
	@Test public void test_1663() { checkIsSubtype("{any f2,any f3}","{void f2,void f3}"); }
	@Test public void test_1664() { checkIsSubtype("{any f2,any f3}","{void f1,any f2}"); }
	@Test public void test_1665() { checkIsSubtype("{any f2,any f3}","{void f2,any f3}"); }
	@Test public void test_1666() { checkIsSubtype("{any f2,any f3}","{void f1,null f2}"); }
	@Test public void test_1667() { checkIsSubtype("{any f2,any f3}","{void f2,null f3}"); }
	@Test public void test_1668() { checkIsSubtype("{any f2,any f3}","{void f1,int f2}"); }
	@Test public void test_1669() { checkIsSubtype("{any f2,any f3}","{void f2,int f3}"); }
	@Test public void test_1670() { checkIsSubtype("{any f2,any f3}","{any f1,void f2}"); }
	@Test public void test_1671() { checkIsSubtype("{any f2,any f3}","{any f2,void f3}"); }
	@Test public void test_1672() { checkNotSubtype("{any f2,any f3}","{any f1,any f2}"); }
	@Test public void test_1673() { checkIsSubtype("{any f2,any f3}","{any f2,any f3}"); }
	@Test public void test_1674() { checkNotSubtype("{any f2,any f3}","{any f1,null f2}"); }
	@Test public void test_1675() { checkIsSubtype("{any f2,any f3}","{any f2,null f3}"); }
	@Test public void test_1676() { checkNotSubtype("{any f2,any f3}","{any f1,int f2}"); }
	@Test public void test_1677() { checkIsSubtype("{any f2,any f3}","{any f2,int f3}"); }
	@Test public void test_1678() { checkIsSubtype("{any f2,any f3}","{null f1,void f2}"); }
	@Test public void test_1679() { checkIsSubtype("{any f2,any f3}","{null f2,void f3}"); }
	@Test public void test_1680() { checkNotSubtype("{any f2,any f3}","{null f1,any f2}"); }
	@Test public void test_1681() { checkIsSubtype("{any f2,any f3}","{null f2,any f3}"); }
	@Test public void test_1682() { checkNotSubtype("{any f2,any f3}","{null f1,null f2}"); }
	@Test public void test_1683() { checkIsSubtype("{any f2,any f3}","{null f2,null f3}"); }
	@Test public void test_1684() { checkNotSubtype("{any f2,any f3}","{null f1,int f2}"); }
	@Test public void test_1685() { checkIsSubtype("{any f2,any f3}","{null f2,int f3}"); }
	@Test public void test_1686() { checkIsSubtype("{any f2,any f3}","{int f1,void f2}"); }
	@Test public void test_1687() { checkIsSubtype("{any f2,any f3}","{int f2,void f3}"); }
	@Test public void test_1688() { checkNotSubtype("{any f2,any f3}","{int f1,any f2}"); }
	@Test public void test_1689() { checkIsSubtype("{any f2,any f3}","{int f2,any f3}"); }
	@Test public void test_1690() { checkNotSubtype("{any f2,any f3}","{int f1,null f2}"); }
	@Test public void test_1691() { checkIsSubtype("{any f2,any f3}","{int f2,null f3}"); }
	@Test public void test_1692() { checkNotSubtype("{any f2,any f3}","{int f1,int f2}"); }
	@Test public void test_1693() { checkIsSubtype("{any f2,any f3}","{int f2,int f3}"); }
	@Test public void test_1694() { checkIsSubtype("{any f2,any f3}","{{void f1} f1}"); }
	@Test public void test_1695() { checkIsSubtype("{any f2,any f3}","{{void f2} f1}"); }
	@Test public void test_1696() { checkIsSubtype("{any f2,any f3}","{{void f1} f2}"); }
	@Test public void test_1697() { checkIsSubtype("{any f2,any f3}","{{void f2} f2}"); }
	@Test public void test_1698() { checkIsSubtype("{any f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_1699() { checkIsSubtype("{any f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_1700() { checkIsSubtype("{any f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_1701() { checkIsSubtype("{any f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_1702() { checkNotSubtype("{any f2,any f3}","{{any f1} f1}"); }
	@Test public void test_1703() { checkNotSubtype("{any f2,any f3}","{{any f2} f1}"); }
	@Test public void test_1704() { checkNotSubtype("{any f2,any f3}","{{any f1} f2}"); }
	@Test public void test_1705() { checkNotSubtype("{any f2,any f3}","{{any f2} f2}"); }
	@Test public void test_1706() { checkNotSubtype("{any f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_1707() { checkNotSubtype("{any f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_1708() { checkIsSubtype("{any f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_1709() { checkIsSubtype("{any f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_1710() { checkNotSubtype("{any f2,any f3}","{{null f1} f1}"); }
	@Test public void test_1711() { checkNotSubtype("{any f2,any f3}","{{null f2} f1}"); }
	@Test public void test_1712() { checkNotSubtype("{any f2,any f3}","{{null f1} f2}"); }
	@Test public void test_1713() { checkNotSubtype("{any f2,any f3}","{{null f2} f2}"); }
	@Test public void test_1714() { checkNotSubtype("{any f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_1715() { checkNotSubtype("{any f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_1716() { checkIsSubtype("{any f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_1717() { checkIsSubtype("{any f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_1718() { checkNotSubtype("{any f2,any f3}","{{int f1} f1}"); }
	@Test public void test_1719() { checkNotSubtype("{any f2,any f3}","{{int f2} f1}"); }
	@Test public void test_1720() { checkNotSubtype("{any f2,any f3}","{{int f1} f2}"); }
	@Test public void test_1721() { checkNotSubtype("{any f2,any f3}","{{int f2} f2}"); }
	@Test public void test_1722() { checkNotSubtype("{any f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_1723() { checkNotSubtype("{any f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_1724() { checkIsSubtype("{any f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_1725() { checkIsSubtype("{any f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_1726() { checkNotSubtype("{any f1,null f2}","any"); }
	@Test public void test_1727() { checkNotSubtype("{any f1,null f2}","null"); }
	@Test public void test_1728() { checkNotSubtype("{any f1,null f2}","int"); }
	@Test public void test_1729() { checkIsSubtype("{any f1,null f2}","{void f1}"); }
	@Test public void test_1730() { checkIsSubtype("{any f1,null f2}","{void f2}"); }
	@Test public void test_1731() { checkNotSubtype("{any f1,null f2}","{any f1}"); }
	@Test public void test_1732() { checkNotSubtype("{any f1,null f2}","{any f2}"); }
	@Test public void test_1733() { checkNotSubtype("{any f1,null f2}","{null f1}"); }
	@Test public void test_1734() { checkNotSubtype("{any f1,null f2}","{null f2}"); }
	@Test public void test_1735() { checkNotSubtype("{any f1,null f2}","{int f1}"); }
	@Test public void test_1736() { checkNotSubtype("{any f1,null f2}","{int f2}"); }
	@Test public void test_1737() { checkIsSubtype("{any f1,null f2}","{void f1,void f2}"); }
	@Test public void test_1738() { checkIsSubtype("{any f1,null f2}","{void f2,void f3}"); }
	@Test public void test_1739() { checkIsSubtype("{any f1,null f2}","{void f1,any f2}"); }
	@Test public void test_1740() { checkIsSubtype("{any f1,null f2}","{void f2,any f3}"); }
	@Test public void test_1741() { checkIsSubtype("{any f1,null f2}","{void f1,null f2}"); }
	@Test public void test_1742() { checkIsSubtype("{any f1,null f2}","{void f2,null f3}"); }
	@Test public void test_1743() { checkIsSubtype("{any f1,null f2}","{void f1,int f2}"); }
	@Test public void test_1744() { checkIsSubtype("{any f1,null f2}","{void f2,int f3}"); }
	@Test public void test_1745() { checkIsSubtype("{any f1,null f2}","{any f1,void f2}"); }
	@Test public void test_1746() { checkIsSubtype("{any f1,null f2}","{any f2,void f3}"); }
	@Test public void test_1747() { checkNotSubtype("{any f1,null f2}","{any f1,any f2}"); }
	@Test public void test_1748() { checkNotSubtype("{any f1,null f2}","{any f2,any f3}"); }
	@Test public void test_1749() { checkIsSubtype("{any f1,null f2}","{any f1,null f2}"); }
	@Test public void test_1750() { checkNotSubtype("{any f1,null f2}","{any f2,null f3}"); }
	@Test public void test_1751() { checkNotSubtype("{any f1,null f2}","{any f1,int f2}"); }
	@Test public void test_1752() { checkNotSubtype("{any f1,null f2}","{any f2,int f3}"); }
	@Test public void test_1753() { checkIsSubtype("{any f1,null f2}","{null f1,void f2}"); }
	@Test public void test_1754() { checkIsSubtype("{any f1,null f2}","{null f2,void f3}"); }
	@Test public void test_1755() { checkNotSubtype("{any f1,null f2}","{null f1,any f2}"); }
	@Test public void test_1756() { checkNotSubtype("{any f1,null f2}","{null f2,any f3}"); }
	@Test public void test_1757() { checkIsSubtype("{any f1,null f2}","{null f1,null f2}"); }
	@Test public void test_1758() { checkNotSubtype("{any f1,null f2}","{null f2,null f3}"); }
	@Test public void test_1759() { checkNotSubtype("{any f1,null f2}","{null f1,int f2}"); }
	@Test public void test_1760() { checkNotSubtype("{any f1,null f2}","{null f2,int f3}"); }
	@Test public void test_1761() { checkIsSubtype("{any f1,null f2}","{int f1,void f2}"); }
	@Test public void test_1762() { checkIsSubtype("{any f1,null f2}","{int f2,void f3}"); }
	@Test public void test_1763() { checkNotSubtype("{any f1,null f2}","{int f1,any f2}"); }
	@Test public void test_1764() { checkNotSubtype("{any f1,null f2}","{int f2,any f3}"); }
	@Test public void test_1765() { checkIsSubtype("{any f1,null f2}","{int f1,null f2}"); }
	@Test public void test_1766() { checkNotSubtype("{any f1,null f2}","{int f2,null f3}"); }
	@Test public void test_1767() { checkNotSubtype("{any f1,null f2}","{int f1,int f2}"); }
	@Test public void test_1768() { checkNotSubtype("{any f1,null f2}","{int f2,int f3}"); }
	@Test public void test_1769() { checkIsSubtype("{any f1,null f2}","{{void f1} f1}"); }
	@Test public void test_1770() { checkIsSubtype("{any f1,null f2}","{{void f2} f1}"); }
	@Test public void test_1771() { checkIsSubtype("{any f1,null f2}","{{void f1} f2}"); }
	@Test public void test_1772() { checkIsSubtype("{any f1,null f2}","{{void f2} f2}"); }
	@Test public void test_1773() { checkIsSubtype("{any f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1774() { checkIsSubtype("{any f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1775() { checkIsSubtype("{any f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1776() { checkIsSubtype("{any f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1777() { checkNotSubtype("{any f1,null f2}","{{any f1} f1}"); }
	@Test public void test_1778() { checkNotSubtype("{any f1,null f2}","{{any f2} f1}"); }
	@Test public void test_1779() { checkNotSubtype("{any f1,null f2}","{{any f1} f2}"); }
	@Test public void test_1780() { checkNotSubtype("{any f1,null f2}","{{any f2} f2}"); }
	@Test public void test_1781() { checkNotSubtype("{any f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1782() { checkNotSubtype("{any f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1783() { checkNotSubtype("{any f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1784() { checkNotSubtype("{any f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1785() { checkNotSubtype("{any f1,null f2}","{{null f1} f1}"); }
	@Test public void test_1786() { checkNotSubtype("{any f1,null f2}","{{null f2} f1}"); }
	@Test public void test_1787() { checkNotSubtype("{any f1,null f2}","{{null f1} f2}"); }
	@Test public void test_1788() { checkNotSubtype("{any f1,null f2}","{{null f2} f2}"); }
	@Test public void test_1789() { checkIsSubtype("{any f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1790() { checkIsSubtype("{any f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1791() { checkNotSubtype("{any f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1792() { checkNotSubtype("{any f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1793() { checkNotSubtype("{any f1,null f2}","{{int f1} f1}"); }
	@Test public void test_1794() { checkNotSubtype("{any f1,null f2}","{{int f2} f1}"); }
	@Test public void test_1795() { checkNotSubtype("{any f1,null f2}","{{int f1} f2}"); }
	@Test public void test_1796() { checkNotSubtype("{any f1,null f2}","{{int f2} f2}"); }
	@Test public void test_1797() { checkNotSubtype("{any f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1798() { checkNotSubtype("{any f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1799() { checkNotSubtype("{any f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1800() { checkNotSubtype("{any f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1801() { checkNotSubtype("{any f2,null f3}","any"); }
	@Test public void test_1802() { checkNotSubtype("{any f2,null f3}","null"); }
	@Test public void test_1803() { checkNotSubtype("{any f2,null f3}","int"); }
	@Test public void test_1804() { checkIsSubtype("{any f2,null f3}","{void f1}"); }
	@Test public void test_1805() { checkIsSubtype("{any f2,null f3}","{void f2}"); }
	@Test public void test_1806() { checkNotSubtype("{any f2,null f3}","{any f1}"); }
	@Test public void test_1807() { checkNotSubtype("{any f2,null f3}","{any f2}"); }
	@Test public void test_1808() { checkNotSubtype("{any f2,null f3}","{null f1}"); }
	@Test public void test_1809() { checkNotSubtype("{any f2,null f3}","{null f2}"); }
	@Test public void test_1810() { checkNotSubtype("{any f2,null f3}","{int f1}"); }
	@Test public void test_1811() { checkNotSubtype("{any f2,null f3}","{int f2}"); }
	@Test public void test_1812() { checkIsSubtype("{any f2,null f3}","{void f1,void f2}"); }
	@Test public void test_1813() { checkIsSubtype("{any f2,null f3}","{void f2,void f3}"); }
	@Test public void test_1814() { checkIsSubtype("{any f2,null f3}","{void f1,any f2}"); }
	@Test public void test_1815() { checkIsSubtype("{any f2,null f3}","{void f2,any f3}"); }
	@Test public void test_1816() { checkIsSubtype("{any f2,null f3}","{void f1,null f2}"); }
	@Test public void test_1817() { checkIsSubtype("{any f2,null f3}","{void f2,null f3}"); }
	@Test public void test_1818() { checkIsSubtype("{any f2,null f3}","{void f1,int f2}"); }
	@Test public void test_1819() { checkIsSubtype("{any f2,null f3}","{void f2,int f3}"); }
	@Test public void test_1820() { checkIsSubtype("{any f2,null f3}","{any f1,void f2}"); }
	@Test public void test_1821() { checkIsSubtype("{any f2,null f3}","{any f2,void f3}"); }
	@Test public void test_1822() { checkNotSubtype("{any f2,null f3}","{any f1,any f2}"); }
	@Test public void test_1823() { checkNotSubtype("{any f2,null f3}","{any f2,any f3}"); }
	@Test public void test_1824() { checkNotSubtype("{any f2,null f3}","{any f1,null f2}"); }
	@Test public void test_1825() { checkIsSubtype("{any f2,null f3}","{any f2,null f3}"); }
	@Test public void test_1826() { checkNotSubtype("{any f2,null f3}","{any f1,int f2}"); }
	@Test public void test_1827() { checkNotSubtype("{any f2,null f3}","{any f2,int f3}"); }
	@Test public void test_1828() { checkIsSubtype("{any f2,null f3}","{null f1,void f2}"); }
	@Test public void test_1829() { checkIsSubtype("{any f2,null f3}","{null f2,void f3}"); }
	@Test public void test_1830() { checkNotSubtype("{any f2,null f3}","{null f1,any f2}"); }
	@Test public void test_1831() { checkNotSubtype("{any f2,null f3}","{null f2,any f3}"); }
	@Test public void test_1832() { checkNotSubtype("{any f2,null f3}","{null f1,null f2}"); }
	@Test public void test_1833() { checkIsSubtype("{any f2,null f3}","{null f2,null f3}"); }
	@Test public void test_1834() { checkNotSubtype("{any f2,null f3}","{null f1,int f2}"); }
	@Test public void test_1835() { checkNotSubtype("{any f2,null f3}","{null f2,int f3}"); }
	@Test public void test_1836() { checkIsSubtype("{any f2,null f3}","{int f1,void f2}"); }
	@Test public void test_1837() { checkIsSubtype("{any f2,null f3}","{int f2,void f3}"); }
	@Test public void test_1838() { checkNotSubtype("{any f2,null f3}","{int f1,any f2}"); }
	@Test public void test_1839() { checkNotSubtype("{any f2,null f3}","{int f2,any f3}"); }
	@Test public void test_1840() { checkNotSubtype("{any f2,null f3}","{int f1,null f2}"); }
	@Test public void test_1841() { checkIsSubtype("{any f2,null f3}","{int f2,null f3}"); }
	@Test public void test_1842() { checkNotSubtype("{any f2,null f3}","{int f1,int f2}"); }
	@Test public void test_1843() { checkNotSubtype("{any f2,null f3}","{int f2,int f3}"); }
	@Test public void test_1844() { checkIsSubtype("{any f2,null f3}","{{void f1} f1}"); }
	@Test public void test_1845() { checkIsSubtype("{any f2,null f3}","{{void f2} f1}"); }
	@Test public void test_1846() { checkIsSubtype("{any f2,null f3}","{{void f1} f2}"); }
	@Test public void test_1847() { checkIsSubtype("{any f2,null f3}","{{void f2} f2}"); }
	@Test public void test_1848() { checkIsSubtype("{any f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_1849() { checkIsSubtype("{any f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_1850() { checkIsSubtype("{any f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_1851() { checkIsSubtype("{any f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_1852() { checkNotSubtype("{any f2,null f3}","{{any f1} f1}"); }
	@Test public void test_1853() { checkNotSubtype("{any f2,null f3}","{{any f2} f1}"); }
	@Test public void test_1854() { checkNotSubtype("{any f2,null f3}","{{any f1} f2}"); }
	@Test public void test_1855() { checkNotSubtype("{any f2,null f3}","{{any f2} f2}"); }
	@Test public void test_1856() { checkNotSubtype("{any f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_1857() { checkNotSubtype("{any f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_1858() { checkNotSubtype("{any f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_1859() { checkNotSubtype("{any f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_1860() { checkNotSubtype("{any f2,null f3}","{{null f1} f1}"); }
	@Test public void test_1861() { checkNotSubtype("{any f2,null f3}","{{null f2} f1}"); }
	@Test public void test_1862() { checkNotSubtype("{any f2,null f3}","{{null f1} f2}"); }
	@Test public void test_1863() { checkNotSubtype("{any f2,null f3}","{{null f2} f2}"); }
	@Test public void test_1864() { checkNotSubtype("{any f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_1865() { checkNotSubtype("{any f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_1866() { checkIsSubtype("{any f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_1867() { checkIsSubtype("{any f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_1868() { checkNotSubtype("{any f2,null f3}","{{int f1} f1}"); }
	@Test public void test_1869() { checkNotSubtype("{any f2,null f3}","{{int f2} f1}"); }
	@Test public void test_1870() { checkNotSubtype("{any f2,null f3}","{{int f1} f2}"); }
	@Test public void test_1871() { checkNotSubtype("{any f2,null f3}","{{int f2} f2}"); }
	@Test public void test_1872() { checkNotSubtype("{any f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_1873() { checkNotSubtype("{any f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_1874() { checkNotSubtype("{any f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_1875() { checkNotSubtype("{any f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_1876() { checkNotSubtype("{any f1,int f2}","any"); }
	@Test public void test_1877() { checkNotSubtype("{any f1,int f2}","null"); }
	@Test public void test_1878() { checkNotSubtype("{any f1,int f2}","int"); }
	@Test public void test_1879() { checkIsSubtype("{any f1,int f2}","{void f1}"); }
	@Test public void test_1880() { checkIsSubtype("{any f1,int f2}","{void f2}"); }
	@Test public void test_1881() { checkNotSubtype("{any f1,int f2}","{any f1}"); }
	@Test public void test_1882() { checkNotSubtype("{any f1,int f2}","{any f2}"); }
	@Test public void test_1883() { checkNotSubtype("{any f1,int f2}","{null f1}"); }
	@Test public void test_1884() { checkNotSubtype("{any f1,int f2}","{null f2}"); }
	@Test public void test_1885() { checkNotSubtype("{any f1,int f2}","{int f1}"); }
	@Test public void test_1886() { checkNotSubtype("{any f1,int f2}","{int f2}"); }
	@Test public void test_1887() { checkIsSubtype("{any f1,int f2}","{void f1,void f2}"); }
	@Test public void test_1888() { checkIsSubtype("{any f1,int f2}","{void f2,void f3}"); }
	@Test public void test_1889() { checkIsSubtype("{any f1,int f2}","{void f1,any f2}"); }
	@Test public void test_1890() { checkIsSubtype("{any f1,int f2}","{void f2,any f3}"); }
	@Test public void test_1891() { checkIsSubtype("{any f1,int f2}","{void f1,null f2}"); }
	@Test public void test_1892() { checkIsSubtype("{any f1,int f2}","{void f2,null f3}"); }
	@Test public void test_1893() { checkIsSubtype("{any f1,int f2}","{void f1,int f2}"); }
	@Test public void test_1894() { checkIsSubtype("{any f1,int f2}","{void f2,int f3}"); }
	@Test public void test_1895() { checkIsSubtype("{any f1,int f2}","{any f1,void f2}"); }
	@Test public void test_1896() { checkIsSubtype("{any f1,int f2}","{any f2,void f3}"); }
	@Test public void test_1897() { checkNotSubtype("{any f1,int f2}","{any f1,any f2}"); }
	@Test public void test_1898() { checkNotSubtype("{any f1,int f2}","{any f2,any f3}"); }
	@Test public void test_1899() { checkNotSubtype("{any f1,int f2}","{any f1,null f2}"); }
	@Test public void test_1900() { checkNotSubtype("{any f1,int f2}","{any f2,null f3}"); }
	@Test public void test_1901() { checkIsSubtype("{any f1,int f2}","{any f1,int f2}"); }
	@Test public void test_1902() { checkNotSubtype("{any f1,int f2}","{any f2,int f3}"); }
	@Test public void test_1903() { checkIsSubtype("{any f1,int f2}","{null f1,void f2}"); }
	@Test public void test_1904() { checkIsSubtype("{any f1,int f2}","{null f2,void f3}"); }
	@Test public void test_1905() { checkNotSubtype("{any f1,int f2}","{null f1,any f2}"); }
	@Test public void test_1906() { checkNotSubtype("{any f1,int f2}","{null f2,any f3}"); }
	@Test public void test_1907() { checkNotSubtype("{any f1,int f2}","{null f1,null f2}"); }
	@Test public void test_1908() { checkNotSubtype("{any f1,int f2}","{null f2,null f3}"); }
	@Test public void test_1909() { checkIsSubtype("{any f1,int f2}","{null f1,int f2}"); }
	@Test public void test_1910() { checkNotSubtype("{any f1,int f2}","{null f2,int f3}"); }
	@Test public void test_1911() { checkIsSubtype("{any f1,int f2}","{int f1,void f2}"); }
	@Test public void test_1912() { checkIsSubtype("{any f1,int f2}","{int f2,void f3}"); }
	@Test public void test_1913() { checkNotSubtype("{any f1,int f2}","{int f1,any f2}"); }
	@Test public void test_1914() { checkNotSubtype("{any f1,int f2}","{int f2,any f3}"); }
	@Test public void test_1915() { checkNotSubtype("{any f1,int f2}","{int f1,null f2}"); }
	@Test public void test_1916() { checkNotSubtype("{any f1,int f2}","{int f2,null f3}"); }
	@Test public void test_1917() { checkIsSubtype("{any f1,int f2}","{int f1,int f2}"); }
	@Test public void test_1918() { checkNotSubtype("{any f1,int f2}","{int f2,int f3}"); }
	@Test public void test_1919() { checkIsSubtype("{any f1,int f2}","{{void f1} f1}"); }
	@Test public void test_1920() { checkIsSubtype("{any f1,int f2}","{{void f2} f1}"); }
	@Test public void test_1921() { checkIsSubtype("{any f1,int f2}","{{void f1} f2}"); }
	@Test public void test_1922() { checkIsSubtype("{any f1,int f2}","{{void f2} f2}"); }
	@Test public void test_1923() { checkIsSubtype("{any f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_1924() { checkIsSubtype("{any f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_1925() { checkIsSubtype("{any f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_1926() { checkIsSubtype("{any f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_1927() { checkNotSubtype("{any f1,int f2}","{{any f1} f1}"); }
	@Test public void test_1928() { checkNotSubtype("{any f1,int f2}","{{any f2} f1}"); }
	@Test public void test_1929() { checkNotSubtype("{any f1,int f2}","{{any f1} f2}"); }
	@Test public void test_1930() { checkNotSubtype("{any f1,int f2}","{{any f2} f2}"); }
	@Test public void test_1931() { checkNotSubtype("{any f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_1932() { checkNotSubtype("{any f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_1933() { checkNotSubtype("{any f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_1934() { checkNotSubtype("{any f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_1935() { checkNotSubtype("{any f1,int f2}","{{null f1} f1}"); }
	@Test public void test_1936() { checkNotSubtype("{any f1,int f2}","{{null f2} f1}"); }
	@Test public void test_1937() { checkNotSubtype("{any f1,int f2}","{{null f1} f2}"); }
	@Test public void test_1938() { checkNotSubtype("{any f1,int f2}","{{null f2} f2}"); }
	@Test public void test_1939() { checkNotSubtype("{any f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_1940() { checkNotSubtype("{any f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_1941() { checkNotSubtype("{any f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_1942() { checkNotSubtype("{any f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_1943() { checkNotSubtype("{any f1,int f2}","{{int f1} f1}"); }
	@Test public void test_1944() { checkNotSubtype("{any f1,int f2}","{{int f2} f1}"); }
	@Test public void test_1945() { checkNotSubtype("{any f1,int f2}","{{int f1} f2}"); }
	@Test public void test_1946() { checkNotSubtype("{any f1,int f2}","{{int f2} f2}"); }
	@Test public void test_1947() { checkIsSubtype("{any f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_1948() { checkIsSubtype("{any f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_1949() { checkNotSubtype("{any f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_1950() { checkNotSubtype("{any f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_1951() { checkNotSubtype("{any f2,int f3}","any"); }
	@Test public void test_1952() { checkNotSubtype("{any f2,int f3}","null"); }
	@Test public void test_1953() { checkNotSubtype("{any f2,int f3}","int"); }
	@Test public void test_1954() { checkIsSubtype("{any f2,int f3}","{void f1}"); }
	@Test public void test_1955() { checkIsSubtype("{any f2,int f3}","{void f2}"); }
	@Test public void test_1956() { checkNotSubtype("{any f2,int f3}","{any f1}"); }
	@Test public void test_1957() { checkNotSubtype("{any f2,int f3}","{any f2}"); }
	@Test public void test_1958() { checkNotSubtype("{any f2,int f3}","{null f1}"); }
	@Test public void test_1959() { checkNotSubtype("{any f2,int f3}","{null f2}"); }
	@Test public void test_1960() { checkNotSubtype("{any f2,int f3}","{int f1}"); }
	@Test public void test_1961() { checkNotSubtype("{any f2,int f3}","{int f2}"); }
	@Test public void test_1962() { checkIsSubtype("{any f2,int f3}","{void f1,void f2}"); }
	@Test public void test_1963() { checkIsSubtype("{any f2,int f3}","{void f2,void f3}"); }
	@Test public void test_1964() { checkIsSubtype("{any f2,int f3}","{void f1,any f2}"); }
	@Test public void test_1965() { checkIsSubtype("{any f2,int f3}","{void f2,any f3}"); }
	@Test public void test_1966() { checkIsSubtype("{any f2,int f3}","{void f1,null f2}"); }
	@Test public void test_1967() { checkIsSubtype("{any f2,int f3}","{void f2,null f3}"); }
	@Test public void test_1968() { checkIsSubtype("{any f2,int f3}","{void f1,int f2}"); }
	@Test public void test_1969() { checkIsSubtype("{any f2,int f3}","{void f2,int f3}"); }
	@Test public void test_1970() { checkIsSubtype("{any f2,int f3}","{any f1,void f2}"); }
	@Test public void test_1971() { checkIsSubtype("{any f2,int f3}","{any f2,void f3}"); }
	@Test public void test_1972() { checkNotSubtype("{any f2,int f3}","{any f1,any f2}"); }
	@Test public void test_1973() { checkNotSubtype("{any f2,int f3}","{any f2,any f3}"); }
	@Test public void test_1974() { checkNotSubtype("{any f2,int f3}","{any f1,null f2}"); }
	@Test public void test_1975() { checkNotSubtype("{any f2,int f3}","{any f2,null f3}"); }
	@Test public void test_1976() { checkNotSubtype("{any f2,int f3}","{any f1,int f2}"); }
	@Test public void test_1977() { checkIsSubtype("{any f2,int f3}","{any f2,int f3}"); }
	@Test public void test_1978() { checkIsSubtype("{any f2,int f3}","{null f1,void f2}"); }
	@Test public void test_1979() { checkIsSubtype("{any f2,int f3}","{null f2,void f3}"); }
	@Test public void test_1980() { checkNotSubtype("{any f2,int f3}","{null f1,any f2}"); }
	@Test public void test_1981() { checkNotSubtype("{any f2,int f3}","{null f2,any f3}"); }
	@Test public void test_1982() { checkNotSubtype("{any f2,int f3}","{null f1,null f2}"); }
	@Test public void test_1983() { checkNotSubtype("{any f2,int f3}","{null f2,null f3}"); }
	@Test public void test_1984() { checkNotSubtype("{any f2,int f3}","{null f1,int f2}"); }
	@Test public void test_1985() { checkIsSubtype("{any f2,int f3}","{null f2,int f3}"); }
	@Test public void test_1986() { checkIsSubtype("{any f2,int f3}","{int f1,void f2}"); }
	@Test public void test_1987() { checkIsSubtype("{any f2,int f3}","{int f2,void f3}"); }
	@Test public void test_1988() { checkNotSubtype("{any f2,int f3}","{int f1,any f2}"); }
	@Test public void test_1989() { checkNotSubtype("{any f2,int f3}","{int f2,any f3}"); }
	@Test public void test_1990() { checkNotSubtype("{any f2,int f3}","{int f1,null f2}"); }
	@Test public void test_1991() { checkNotSubtype("{any f2,int f3}","{int f2,null f3}"); }
	@Test public void test_1992() { checkNotSubtype("{any f2,int f3}","{int f1,int f2}"); }
	@Test public void test_1993() { checkIsSubtype("{any f2,int f3}","{int f2,int f3}"); }
	@Test public void test_1994() { checkIsSubtype("{any f2,int f3}","{{void f1} f1}"); }
	@Test public void test_1995() { checkIsSubtype("{any f2,int f3}","{{void f2} f1}"); }
	@Test public void test_1996() { checkIsSubtype("{any f2,int f3}","{{void f1} f2}"); }
	@Test public void test_1997() { checkIsSubtype("{any f2,int f3}","{{void f2} f2}"); }
	@Test public void test_1998() { checkIsSubtype("{any f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_1999() { checkIsSubtype("{any f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_2000() { checkIsSubtype("{any f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_2001() { checkIsSubtype("{any f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_2002() { checkNotSubtype("{any f2,int f3}","{{any f1} f1}"); }
	@Test public void test_2003() { checkNotSubtype("{any f2,int f3}","{{any f2} f1}"); }
	@Test public void test_2004() { checkNotSubtype("{any f2,int f3}","{{any f1} f2}"); }
	@Test public void test_2005() { checkNotSubtype("{any f2,int f3}","{{any f2} f2}"); }
	@Test public void test_2006() { checkNotSubtype("{any f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_2007() { checkNotSubtype("{any f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_2008() { checkNotSubtype("{any f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_2009() { checkNotSubtype("{any f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_2010() { checkNotSubtype("{any f2,int f3}","{{null f1} f1}"); }
	@Test public void test_2011() { checkNotSubtype("{any f2,int f3}","{{null f2} f1}"); }
	@Test public void test_2012() { checkNotSubtype("{any f2,int f3}","{{null f1} f2}"); }
	@Test public void test_2013() { checkNotSubtype("{any f2,int f3}","{{null f2} f2}"); }
	@Test public void test_2014() { checkNotSubtype("{any f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_2015() { checkNotSubtype("{any f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_2016() { checkNotSubtype("{any f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_2017() { checkNotSubtype("{any f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_2018() { checkNotSubtype("{any f2,int f3}","{{int f1} f1}"); }
	@Test public void test_2019() { checkNotSubtype("{any f2,int f3}","{{int f2} f1}"); }
	@Test public void test_2020() { checkNotSubtype("{any f2,int f3}","{{int f1} f2}"); }
	@Test public void test_2021() { checkNotSubtype("{any f2,int f3}","{{int f2} f2}"); }
	@Test public void test_2022() { checkNotSubtype("{any f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_2023() { checkNotSubtype("{any f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_2024() { checkIsSubtype("{any f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_2025() { checkIsSubtype("{any f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_2026() { checkNotSubtype("{null f1,void f2}","any"); }
	@Test public void test_2027() { checkNotSubtype("{null f1,void f2}","null"); }
	@Test public void test_2028() { checkNotSubtype("{null f1,void f2}","int"); }
	@Test public void test_2029() { checkIsSubtype("{null f1,void f2}","{void f1}"); }
	@Test public void test_2030() { checkIsSubtype("{null f1,void f2}","{void f2}"); }
	@Test public void test_2031() { checkNotSubtype("{null f1,void f2}","{any f1}"); }
	@Test public void test_2032() { checkNotSubtype("{null f1,void f2}","{any f2}"); }
	@Test public void test_2033() { checkNotSubtype("{null f1,void f2}","{null f1}"); }
	@Test public void test_2034() { checkNotSubtype("{null f1,void f2}","{null f2}"); }
	@Test public void test_2035() { checkNotSubtype("{null f1,void f2}","{int f1}"); }
	@Test public void test_2036() { checkNotSubtype("{null f1,void f2}","{int f2}"); }
	@Test public void test_2037() { checkIsSubtype("{null f1,void f2}","{void f1,void f2}"); }
	@Test public void test_2038() { checkIsSubtype("{null f1,void f2}","{void f2,void f3}"); }
	@Test public void test_2039() { checkIsSubtype("{null f1,void f2}","{void f1,any f2}"); }
	@Test public void test_2040() { checkIsSubtype("{null f1,void f2}","{void f2,any f3}"); }
	@Test public void test_2041() { checkIsSubtype("{null f1,void f2}","{void f1,null f2}"); }
	@Test public void test_2042() { checkIsSubtype("{null f1,void f2}","{void f2,null f3}"); }
	@Test public void test_2043() { checkIsSubtype("{null f1,void f2}","{void f1,int f2}"); }
	@Test public void test_2044() { checkIsSubtype("{null f1,void f2}","{void f2,int f3}"); }
	@Test public void test_2045() { checkIsSubtype("{null f1,void f2}","{any f1,void f2}"); }
	@Test public void test_2046() { checkIsSubtype("{null f1,void f2}","{any f2,void f3}"); }
	@Test public void test_2047() { checkNotSubtype("{null f1,void f2}","{any f1,any f2}"); }
	@Test public void test_2048() { checkNotSubtype("{null f1,void f2}","{any f2,any f3}"); }
	@Test public void test_2049() { checkNotSubtype("{null f1,void f2}","{any f1,null f2}"); }
	@Test public void test_2050() { checkNotSubtype("{null f1,void f2}","{any f2,null f3}"); }
	@Test public void test_2051() { checkNotSubtype("{null f1,void f2}","{any f1,int f2}"); }
	@Test public void test_2052() { checkNotSubtype("{null f1,void f2}","{any f2,int f3}"); }
	@Test public void test_2053() { checkIsSubtype("{null f1,void f2}","{null f1,void f2}"); }
	@Test public void test_2054() { checkIsSubtype("{null f1,void f2}","{null f2,void f3}"); }
	@Test public void test_2055() { checkNotSubtype("{null f1,void f2}","{null f1,any f2}"); }
	@Test public void test_2056() { checkNotSubtype("{null f1,void f2}","{null f2,any f3}"); }
	@Test public void test_2057() { checkNotSubtype("{null f1,void f2}","{null f1,null f2}"); }
	@Test public void test_2058() { checkNotSubtype("{null f1,void f2}","{null f2,null f3}"); }
	@Test public void test_2059() { checkNotSubtype("{null f1,void f2}","{null f1,int f2}"); }
	@Test public void test_2060() { checkNotSubtype("{null f1,void f2}","{null f2,int f3}"); }
	@Test public void test_2061() { checkIsSubtype("{null f1,void f2}","{int f1,void f2}"); }
	@Test public void test_2062() { checkIsSubtype("{null f1,void f2}","{int f2,void f3}"); }
	@Test public void test_2063() { checkNotSubtype("{null f1,void f2}","{int f1,any f2}"); }
	@Test public void test_2064() { checkNotSubtype("{null f1,void f2}","{int f2,any f3}"); }
	@Test public void test_2065() { checkNotSubtype("{null f1,void f2}","{int f1,null f2}"); }
	@Test public void test_2066() { checkNotSubtype("{null f1,void f2}","{int f2,null f3}"); }
	@Test public void test_2067() { checkNotSubtype("{null f1,void f2}","{int f1,int f2}"); }
	@Test public void test_2068() { checkNotSubtype("{null f1,void f2}","{int f2,int f3}"); }
	@Test public void test_2069() { checkIsSubtype("{null f1,void f2}","{{void f1} f1}"); }
	@Test public void test_2070() { checkIsSubtype("{null f1,void f2}","{{void f2} f1}"); }
	@Test public void test_2071() { checkIsSubtype("{null f1,void f2}","{{void f1} f2}"); }
	@Test public void test_2072() { checkIsSubtype("{null f1,void f2}","{{void f2} f2}"); }
	@Test public void test_2073() { checkIsSubtype("{null f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_2074() { checkIsSubtype("{null f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_2075() { checkIsSubtype("{null f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_2076() { checkIsSubtype("{null f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_2077() { checkNotSubtype("{null f1,void f2}","{{any f1} f1}"); }
	@Test public void test_2078() { checkNotSubtype("{null f1,void f2}","{{any f2} f1}"); }
	@Test public void test_2079() { checkNotSubtype("{null f1,void f2}","{{any f1} f2}"); }
	@Test public void test_2080() { checkNotSubtype("{null f1,void f2}","{{any f2} f2}"); }
	@Test public void test_2081() { checkNotSubtype("{null f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_2082() { checkNotSubtype("{null f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_2083() { checkNotSubtype("{null f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_2084() { checkNotSubtype("{null f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_2085() { checkNotSubtype("{null f1,void f2}","{{null f1} f1}"); }
	@Test public void test_2086() { checkNotSubtype("{null f1,void f2}","{{null f2} f1}"); }
	@Test public void test_2087() { checkNotSubtype("{null f1,void f2}","{{null f1} f2}"); }
	@Test public void test_2088() { checkNotSubtype("{null f1,void f2}","{{null f2} f2}"); }
	@Test public void test_2089() { checkNotSubtype("{null f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_2090() { checkNotSubtype("{null f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_2091() { checkNotSubtype("{null f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_2092() { checkNotSubtype("{null f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_2093() { checkNotSubtype("{null f1,void f2}","{{int f1} f1}"); }
	@Test public void test_2094() { checkNotSubtype("{null f1,void f2}","{{int f2} f1}"); }
	@Test public void test_2095() { checkNotSubtype("{null f1,void f2}","{{int f1} f2}"); }
	@Test public void test_2096() { checkNotSubtype("{null f1,void f2}","{{int f2} f2}"); }
	@Test public void test_2097() { checkNotSubtype("{null f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_2098() { checkNotSubtype("{null f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_2099() { checkNotSubtype("{null f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_2100() { checkNotSubtype("{null f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_2101() { checkNotSubtype("{null f2,void f3}","any"); }
	@Test public void test_2102() { checkNotSubtype("{null f2,void f3}","null"); }
	@Test public void test_2103() { checkNotSubtype("{null f2,void f3}","int"); }
	@Test public void test_2104() { checkIsSubtype("{null f2,void f3}","{void f1}"); }
	@Test public void test_2105() { checkIsSubtype("{null f2,void f3}","{void f2}"); }
	@Test public void test_2106() { checkNotSubtype("{null f2,void f3}","{any f1}"); }
	@Test public void test_2107() { checkNotSubtype("{null f2,void f3}","{any f2}"); }
	@Test public void test_2108() { checkNotSubtype("{null f2,void f3}","{null f1}"); }
	@Test public void test_2109() { checkNotSubtype("{null f2,void f3}","{null f2}"); }
	@Test public void test_2110() { checkNotSubtype("{null f2,void f3}","{int f1}"); }
	@Test public void test_2111() { checkNotSubtype("{null f2,void f3}","{int f2}"); }
	@Test public void test_2112() { checkIsSubtype("{null f2,void f3}","{void f1,void f2}"); }
	@Test public void test_2113() { checkIsSubtype("{null f2,void f3}","{void f2,void f3}"); }
	@Test public void test_2114() { checkIsSubtype("{null f2,void f3}","{void f1,any f2}"); }
	@Test public void test_2115() { checkIsSubtype("{null f2,void f3}","{void f2,any f3}"); }
	@Test public void test_2116() { checkIsSubtype("{null f2,void f3}","{void f1,null f2}"); }
	@Test public void test_2117() { checkIsSubtype("{null f2,void f3}","{void f2,null f3}"); }
	@Test public void test_2118() { checkIsSubtype("{null f2,void f3}","{void f1,int f2}"); }
	@Test public void test_2119() { checkIsSubtype("{null f2,void f3}","{void f2,int f3}"); }
	@Test public void test_2120() { checkIsSubtype("{null f2,void f3}","{any f1,void f2}"); }
	@Test public void test_2121() { checkIsSubtype("{null f2,void f3}","{any f2,void f3}"); }
	@Test public void test_2122() { checkNotSubtype("{null f2,void f3}","{any f1,any f2}"); }
	@Test public void test_2123() { checkNotSubtype("{null f2,void f3}","{any f2,any f3}"); }
	@Test public void test_2124() { checkNotSubtype("{null f2,void f3}","{any f1,null f2}"); }
	@Test public void test_2125() { checkNotSubtype("{null f2,void f3}","{any f2,null f3}"); }
	@Test public void test_2126() { checkNotSubtype("{null f2,void f3}","{any f1,int f2}"); }
	@Test public void test_2127() { checkNotSubtype("{null f2,void f3}","{any f2,int f3}"); }
	@Test public void test_2128() { checkIsSubtype("{null f2,void f3}","{null f1,void f2}"); }
	@Test public void test_2129() { checkIsSubtype("{null f2,void f3}","{null f2,void f3}"); }
	@Test public void test_2130() { checkNotSubtype("{null f2,void f3}","{null f1,any f2}"); }
	@Test public void test_2131() { checkNotSubtype("{null f2,void f3}","{null f2,any f3}"); }
	@Test public void test_2132() { checkNotSubtype("{null f2,void f3}","{null f1,null f2}"); }
	@Test public void test_2133() { checkNotSubtype("{null f2,void f3}","{null f2,null f3}"); }
	@Test public void test_2134() { checkNotSubtype("{null f2,void f3}","{null f1,int f2}"); }
	@Test public void test_2135() { checkNotSubtype("{null f2,void f3}","{null f2,int f3}"); }
	@Test public void test_2136() { checkIsSubtype("{null f2,void f3}","{int f1,void f2}"); }
	@Test public void test_2137() { checkIsSubtype("{null f2,void f3}","{int f2,void f3}"); }
	@Test public void test_2138() { checkNotSubtype("{null f2,void f3}","{int f1,any f2}"); }
	@Test public void test_2139() { checkNotSubtype("{null f2,void f3}","{int f2,any f3}"); }
	@Test public void test_2140() { checkNotSubtype("{null f2,void f3}","{int f1,null f2}"); }
	@Test public void test_2141() { checkNotSubtype("{null f2,void f3}","{int f2,null f3}"); }
	@Test public void test_2142() { checkNotSubtype("{null f2,void f3}","{int f1,int f2}"); }
	@Test public void test_2143() { checkNotSubtype("{null f2,void f3}","{int f2,int f3}"); }
	@Test public void test_2144() { checkIsSubtype("{null f2,void f3}","{{void f1} f1}"); }
	@Test public void test_2145() { checkIsSubtype("{null f2,void f3}","{{void f2} f1}"); }
	@Test public void test_2146() { checkIsSubtype("{null f2,void f3}","{{void f1} f2}"); }
	@Test public void test_2147() { checkIsSubtype("{null f2,void f3}","{{void f2} f2}"); }
	@Test public void test_2148() { checkIsSubtype("{null f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_2149() { checkIsSubtype("{null f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_2150() { checkIsSubtype("{null f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_2151() { checkIsSubtype("{null f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_2152() { checkNotSubtype("{null f2,void f3}","{{any f1} f1}"); }
	@Test public void test_2153() { checkNotSubtype("{null f2,void f3}","{{any f2} f1}"); }
	@Test public void test_2154() { checkNotSubtype("{null f2,void f3}","{{any f1} f2}"); }
	@Test public void test_2155() { checkNotSubtype("{null f2,void f3}","{{any f2} f2}"); }
	@Test public void test_2156() { checkNotSubtype("{null f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_2157() { checkNotSubtype("{null f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_2158() { checkNotSubtype("{null f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_2159() { checkNotSubtype("{null f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_2160() { checkNotSubtype("{null f2,void f3}","{{null f1} f1}"); }
	@Test public void test_2161() { checkNotSubtype("{null f2,void f3}","{{null f2} f1}"); }
	@Test public void test_2162() { checkNotSubtype("{null f2,void f3}","{{null f1} f2}"); }
	@Test public void test_2163() { checkNotSubtype("{null f2,void f3}","{{null f2} f2}"); }
	@Test public void test_2164() { checkNotSubtype("{null f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_2165() { checkNotSubtype("{null f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_2166() { checkNotSubtype("{null f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_2167() { checkNotSubtype("{null f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_2168() { checkNotSubtype("{null f2,void f3}","{{int f1} f1}"); }
	@Test public void test_2169() { checkNotSubtype("{null f2,void f3}","{{int f2} f1}"); }
	@Test public void test_2170() { checkNotSubtype("{null f2,void f3}","{{int f1} f2}"); }
	@Test public void test_2171() { checkNotSubtype("{null f2,void f3}","{{int f2} f2}"); }
	@Test public void test_2172() { checkNotSubtype("{null f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_2173() { checkNotSubtype("{null f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_2174() { checkNotSubtype("{null f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_2175() { checkNotSubtype("{null f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_2176() { checkNotSubtype("{null f1,any f2}","any"); }
	@Test public void test_2177() { checkNotSubtype("{null f1,any f2}","null"); }
	@Test public void test_2178() { checkNotSubtype("{null f1,any f2}","int"); }
	@Test public void test_2179() { checkIsSubtype("{null f1,any f2}","{void f1}"); }
	@Test public void test_2180() { checkIsSubtype("{null f1,any f2}","{void f2}"); }
	@Test public void test_2181() { checkNotSubtype("{null f1,any f2}","{any f1}"); }
	@Test public void test_2182() { checkNotSubtype("{null f1,any f2}","{any f2}"); }
	@Test public void test_2183() { checkNotSubtype("{null f1,any f2}","{null f1}"); }
	@Test public void test_2184() { checkNotSubtype("{null f1,any f2}","{null f2}"); }
	@Test public void test_2185() { checkNotSubtype("{null f1,any f2}","{int f1}"); }
	@Test public void test_2186() { checkNotSubtype("{null f1,any f2}","{int f2}"); }
	@Test public void test_2187() { checkIsSubtype("{null f1,any f2}","{void f1,void f2}"); }
	@Test public void test_2188() { checkIsSubtype("{null f1,any f2}","{void f2,void f3}"); }
	@Test public void test_2189() { checkIsSubtype("{null f1,any f2}","{void f1,any f2}"); }
	@Test public void test_2190() { checkIsSubtype("{null f1,any f2}","{void f2,any f3}"); }
	@Test public void test_2191() { checkIsSubtype("{null f1,any f2}","{void f1,null f2}"); }
	@Test public void test_2192() { checkIsSubtype("{null f1,any f2}","{void f2,null f3}"); }
	@Test public void test_2193() { checkIsSubtype("{null f1,any f2}","{void f1,int f2}"); }
	@Test public void test_2194() { checkIsSubtype("{null f1,any f2}","{void f2,int f3}"); }
	@Test public void test_2195() { checkIsSubtype("{null f1,any f2}","{any f1,void f2}"); }
	@Test public void test_2196() { checkIsSubtype("{null f1,any f2}","{any f2,void f3}"); }
	@Test public void test_2197() { checkNotSubtype("{null f1,any f2}","{any f1,any f2}"); }
	@Test public void test_2198() { checkNotSubtype("{null f1,any f2}","{any f2,any f3}"); }
	@Test public void test_2199() { checkNotSubtype("{null f1,any f2}","{any f1,null f2}"); }
	@Test public void test_2200() { checkNotSubtype("{null f1,any f2}","{any f2,null f3}"); }
	@Test public void test_2201() { checkNotSubtype("{null f1,any f2}","{any f1,int f2}"); }
	@Test public void test_2202() { checkNotSubtype("{null f1,any f2}","{any f2,int f3}"); }
	@Test public void test_2203() { checkIsSubtype("{null f1,any f2}","{null f1,void f2}"); }
	@Test public void test_2204() { checkIsSubtype("{null f1,any f2}","{null f2,void f3}"); }
	@Test public void test_2205() { checkIsSubtype("{null f1,any f2}","{null f1,any f2}"); }
	@Test public void test_2206() { checkNotSubtype("{null f1,any f2}","{null f2,any f3}"); }
	@Test public void test_2207() { checkIsSubtype("{null f1,any f2}","{null f1,null f2}"); }
	@Test public void test_2208() { checkNotSubtype("{null f1,any f2}","{null f2,null f3}"); }
	@Test public void test_2209() { checkIsSubtype("{null f1,any f2}","{null f1,int f2}"); }
	@Test public void test_2210() { checkNotSubtype("{null f1,any f2}","{null f2,int f3}"); }
	@Test public void test_2211() { checkIsSubtype("{null f1,any f2}","{int f1,void f2}"); }
	@Test public void test_2212() { checkIsSubtype("{null f1,any f2}","{int f2,void f3}"); }
	@Test public void test_2213() { checkNotSubtype("{null f1,any f2}","{int f1,any f2}"); }
	@Test public void test_2214() { checkNotSubtype("{null f1,any f2}","{int f2,any f3}"); }
	@Test public void test_2215() { checkNotSubtype("{null f1,any f2}","{int f1,null f2}"); }
	@Test public void test_2216() { checkNotSubtype("{null f1,any f2}","{int f2,null f3}"); }
	@Test public void test_2217() { checkNotSubtype("{null f1,any f2}","{int f1,int f2}"); }
	@Test public void test_2218() { checkNotSubtype("{null f1,any f2}","{int f2,int f3}"); }
	@Test public void test_2219() { checkIsSubtype("{null f1,any f2}","{{void f1} f1}"); }
	@Test public void test_2220() { checkIsSubtype("{null f1,any f2}","{{void f2} f1}"); }
	@Test public void test_2221() { checkIsSubtype("{null f1,any f2}","{{void f1} f2}"); }
	@Test public void test_2222() { checkIsSubtype("{null f1,any f2}","{{void f2} f2}"); }
	@Test public void test_2223() { checkIsSubtype("{null f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_2224() { checkIsSubtype("{null f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_2225() { checkIsSubtype("{null f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_2226() { checkIsSubtype("{null f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_2227() { checkNotSubtype("{null f1,any f2}","{{any f1} f1}"); }
	@Test public void test_2228() { checkNotSubtype("{null f1,any f2}","{{any f2} f1}"); }
	@Test public void test_2229() { checkNotSubtype("{null f1,any f2}","{{any f1} f2}"); }
	@Test public void test_2230() { checkNotSubtype("{null f1,any f2}","{{any f2} f2}"); }
	@Test public void test_2231() { checkNotSubtype("{null f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_2232() { checkNotSubtype("{null f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_2233() { checkNotSubtype("{null f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_2234() { checkNotSubtype("{null f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_2235() { checkNotSubtype("{null f1,any f2}","{{null f1} f1}"); }
	@Test public void test_2236() { checkNotSubtype("{null f1,any f2}","{{null f2} f1}"); }
	@Test public void test_2237() { checkNotSubtype("{null f1,any f2}","{{null f1} f2}"); }
	@Test public void test_2238() { checkNotSubtype("{null f1,any f2}","{{null f2} f2}"); }
	@Test public void test_2239() { checkNotSubtype("{null f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_2240() { checkNotSubtype("{null f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_2241() { checkNotSubtype("{null f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_2242() { checkNotSubtype("{null f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_2243() { checkNotSubtype("{null f1,any f2}","{{int f1} f1}"); }
	@Test public void test_2244() { checkNotSubtype("{null f1,any f2}","{{int f2} f1}"); }
	@Test public void test_2245() { checkNotSubtype("{null f1,any f2}","{{int f1} f2}"); }
	@Test public void test_2246() { checkNotSubtype("{null f1,any f2}","{{int f2} f2}"); }
	@Test public void test_2247() { checkNotSubtype("{null f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_2248() { checkNotSubtype("{null f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_2249() { checkNotSubtype("{null f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_2250() { checkNotSubtype("{null f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_2251() { checkNotSubtype("{null f2,any f3}","any"); }
	@Test public void test_2252() { checkNotSubtype("{null f2,any f3}","null"); }
	@Test public void test_2253() { checkNotSubtype("{null f2,any f3}","int"); }
	@Test public void test_2254() { checkIsSubtype("{null f2,any f3}","{void f1}"); }
	@Test public void test_2255() { checkIsSubtype("{null f2,any f3}","{void f2}"); }
	@Test public void test_2256() { checkNotSubtype("{null f2,any f3}","{any f1}"); }
	@Test public void test_2257() { checkNotSubtype("{null f2,any f3}","{any f2}"); }
	@Test public void test_2258() { checkNotSubtype("{null f2,any f3}","{null f1}"); }
	@Test public void test_2259() { checkNotSubtype("{null f2,any f3}","{null f2}"); }
	@Test public void test_2260() { checkNotSubtype("{null f2,any f3}","{int f1}"); }
	@Test public void test_2261() { checkNotSubtype("{null f2,any f3}","{int f2}"); }
	@Test public void test_2262() { checkIsSubtype("{null f2,any f3}","{void f1,void f2}"); }
	@Test public void test_2263() { checkIsSubtype("{null f2,any f3}","{void f2,void f3}"); }
	@Test public void test_2264() { checkIsSubtype("{null f2,any f3}","{void f1,any f2}"); }
	@Test public void test_2265() { checkIsSubtype("{null f2,any f3}","{void f2,any f3}"); }
	@Test public void test_2266() { checkIsSubtype("{null f2,any f3}","{void f1,null f2}"); }
	@Test public void test_2267() { checkIsSubtype("{null f2,any f3}","{void f2,null f3}"); }
	@Test public void test_2268() { checkIsSubtype("{null f2,any f3}","{void f1,int f2}"); }
	@Test public void test_2269() { checkIsSubtype("{null f2,any f3}","{void f2,int f3}"); }
	@Test public void test_2270() { checkIsSubtype("{null f2,any f3}","{any f1,void f2}"); }
	@Test public void test_2271() { checkIsSubtype("{null f2,any f3}","{any f2,void f3}"); }
	@Test public void test_2272() { checkNotSubtype("{null f2,any f3}","{any f1,any f2}"); }
	@Test public void test_2273() { checkNotSubtype("{null f2,any f3}","{any f2,any f3}"); }
	@Test public void test_2274() { checkNotSubtype("{null f2,any f3}","{any f1,null f2}"); }
	@Test public void test_2275() { checkNotSubtype("{null f2,any f3}","{any f2,null f3}"); }
	@Test public void test_2276() { checkNotSubtype("{null f2,any f3}","{any f1,int f2}"); }
	@Test public void test_2277() { checkNotSubtype("{null f2,any f3}","{any f2,int f3}"); }
	@Test public void test_2278() { checkIsSubtype("{null f2,any f3}","{null f1,void f2}"); }
	@Test public void test_2279() { checkIsSubtype("{null f2,any f3}","{null f2,void f3}"); }
	@Test public void test_2280() { checkNotSubtype("{null f2,any f3}","{null f1,any f2}"); }
	@Test public void test_2281() { checkIsSubtype("{null f2,any f3}","{null f2,any f3}"); }
	@Test public void test_2282() { checkNotSubtype("{null f2,any f3}","{null f1,null f2}"); }
	@Test public void test_2283() { checkIsSubtype("{null f2,any f3}","{null f2,null f3}"); }
	@Test public void test_2284() { checkNotSubtype("{null f2,any f3}","{null f1,int f2}"); }
	@Test public void test_2285() { checkIsSubtype("{null f2,any f3}","{null f2,int f3}"); }
	@Test public void test_2286() { checkIsSubtype("{null f2,any f3}","{int f1,void f2}"); }
	@Test public void test_2287() { checkIsSubtype("{null f2,any f3}","{int f2,void f3}"); }
	@Test public void test_2288() { checkNotSubtype("{null f2,any f3}","{int f1,any f2}"); }
	@Test public void test_2289() { checkNotSubtype("{null f2,any f3}","{int f2,any f3}"); }
	@Test public void test_2290() { checkNotSubtype("{null f2,any f3}","{int f1,null f2}"); }
	@Test public void test_2291() { checkNotSubtype("{null f2,any f3}","{int f2,null f3}"); }
	@Test public void test_2292() { checkNotSubtype("{null f2,any f3}","{int f1,int f2}"); }
	@Test public void test_2293() { checkNotSubtype("{null f2,any f3}","{int f2,int f3}"); }
	@Test public void test_2294() { checkIsSubtype("{null f2,any f3}","{{void f1} f1}"); }
	@Test public void test_2295() { checkIsSubtype("{null f2,any f3}","{{void f2} f1}"); }
	@Test public void test_2296() { checkIsSubtype("{null f2,any f3}","{{void f1} f2}"); }
	@Test public void test_2297() { checkIsSubtype("{null f2,any f3}","{{void f2} f2}"); }
	@Test public void test_2298() { checkIsSubtype("{null f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_2299() { checkIsSubtype("{null f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_2300() { checkIsSubtype("{null f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_2301() { checkIsSubtype("{null f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_2302() { checkNotSubtype("{null f2,any f3}","{{any f1} f1}"); }
	@Test public void test_2303() { checkNotSubtype("{null f2,any f3}","{{any f2} f1}"); }
	@Test public void test_2304() { checkNotSubtype("{null f2,any f3}","{{any f1} f2}"); }
	@Test public void test_2305() { checkNotSubtype("{null f2,any f3}","{{any f2} f2}"); }
	@Test public void test_2306() { checkNotSubtype("{null f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_2307() { checkNotSubtype("{null f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_2308() { checkNotSubtype("{null f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_2309() { checkNotSubtype("{null f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_2310() { checkNotSubtype("{null f2,any f3}","{{null f1} f1}"); }
	@Test public void test_2311() { checkNotSubtype("{null f2,any f3}","{{null f2} f1}"); }
	@Test public void test_2312() { checkNotSubtype("{null f2,any f3}","{{null f1} f2}"); }
	@Test public void test_2313() { checkNotSubtype("{null f2,any f3}","{{null f2} f2}"); }
	@Test public void test_2314() { checkNotSubtype("{null f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_2315() { checkNotSubtype("{null f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_2316() { checkNotSubtype("{null f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_2317() { checkNotSubtype("{null f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_2318() { checkNotSubtype("{null f2,any f3}","{{int f1} f1}"); }
	@Test public void test_2319() { checkNotSubtype("{null f2,any f3}","{{int f2} f1}"); }
	@Test public void test_2320() { checkNotSubtype("{null f2,any f3}","{{int f1} f2}"); }
	@Test public void test_2321() { checkNotSubtype("{null f2,any f3}","{{int f2} f2}"); }
	@Test public void test_2322() { checkNotSubtype("{null f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_2323() { checkNotSubtype("{null f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_2324() { checkNotSubtype("{null f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_2325() { checkNotSubtype("{null f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_2326() { checkNotSubtype("{null f1,null f2}","any"); }
	@Test public void test_2327() { checkNotSubtype("{null f1,null f2}","null"); }
	@Test public void test_2328() { checkNotSubtype("{null f1,null f2}","int"); }
	@Test public void test_2329() { checkIsSubtype("{null f1,null f2}","{void f1}"); }
	@Test public void test_2330() { checkIsSubtype("{null f1,null f2}","{void f2}"); }
	@Test public void test_2331() { checkNotSubtype("{null f1,null f2}","{any f1}"); }
	@Test public void test_2332() { checkNotSubtype("{null f1,null f2}","{any f2}"); }
	@Test public void test_2333() { checkNotSubtype("{null f1,null f2}","{null f1}"); }
	@Test public void test_2334() { checkNotSubtype("{null f1,null f2}","{null f2}"); }
	@Test public void test_2335() { checkNotSubtype("{null f1,null f2}","{int f1}"); }
	@Test public void test_2336() { checkNotSubtype("{null f1,null f2}","{int f2}"); }
	@Test public void test_2337() { checkIsSubtype("{null f1,null f2}","{void f1,void f2}"); }
	@Test public void test_2338() { checkIsSubtype("{null f1,null f2}","{void f2,void f3}"); }
	@Test public void test_2339() { checkIsSubtype("{null f1,null f2}","{void f1,any f2}"); }
	@Test public void test_2340() { checkIsSubtype("{null f1,null f2}","{void f2,any f3}"); }
	@Test public void test_2341() { checkIsSubtype("{null f1,null f2}","{void f1,null f2}"); }
	@Test public void test_2342() { checkIsSubtype("{null f1,null f2}","{void f2,null f3}"); }
	@Test public void test_2343() { checkIsSubtype("{null f1,null f2}","{void f1,int f2}"); }
	@Test public void test_2344() { checkIsSubtype("{null f1,null f2}","{void f2,int f3}"); }
	@Test public void test_2345() { checkIsSubtype("{null f1,null f2}","{any f1,void f2}"); }
	@Test public void test_2346() { checkIsSubtype("{null f1,null f2}","{any f2,void f3}"); }
	@Test public void test_2347() { checkNotSubtype("{null f1,null f2}","{any f1,any f2}"); }
	@Test public void test_2348() { checkNotSubtype("{null f1,null f2}","{any f2,any f3}"); }
	@Test public void test_2349() { checkNotSubtype("{null f1,null f2}","{any f1,null f2}"); }
	@Test public void test_2350() { checkNotSubtype("{null f1,null f2}","{any f2,null f3}"); }
	@Test public void test_2351() { checkNotSubtype("{null f1,null f2}","{any f1,int f2}"); }
	@Test public void test_2352() { checkNotSubtype("{null f1,null f2}","{any f2,int f3}"); }
	@Test public void test_2353() { checkIsSubtype("{null f1,null f2}","{null f1,void f2}"); }
	@Test public void test_2354() { checkIsSubtype("{null f1,null f2}","{null f2,void f3}"); }
	@Test public void test_2355() { checkNotSubtype("{null f1,null f2}","{null f1,any f2}"); }
	@Test public void test_2356() { checkNotSubtype("{null f1,null f2}","{null f2,any f3}"); }
	@Test public void test_2357() { checkIsSubtype("{null f1,null f2}","{null f1,null f2}"); }
	@Test public void test_2358() { checkNotSubtype("{null f1,null f2}","{null f2,null f3}"); }
	@Test public void test_2359() { checkNotSubtype("{null f1,null f2}","{null f1,int f2}"); }
	@Test public void test_2360() { checkNotSubtype("{null f1,null f2}","{null f2,int f3}"); }
	@Test public void test_2361() { checkIsSubtype("{null f1,null f2}","{int f1,void f2}"); }
	@Test public void test_2362() { checkIsSubtype("{null f1,null f2}","{int f2,void f3}"); }
	@Test public void test_2363() { checkNotSubtype("{null f1,null f2}","{int f1,any f2}"); }
	@Test public void test_2364() { checkNotSubtype("{null f1,null f2}","{int f2,any f3}"); }
	@Test public void test_2365() { checkNotSubtype("{null f1,null f2}","{int f1,null f2}"); }
	@Test public void test_2366() { checkNotSubtype("{null f1,null f2}","{int f2,null f3}"); }
	@Test public void test_2367() { checkNotSubtype("{null f1,null f2}","{int f1,int f2}"); }
	@Test public void test_2368() { checkNotSubtype("{null f1,null f2}","{int f2,int f3}"); }
	@Test public void test_2369() { checkIsSubtype("{null f1,null f2}","{{void f1} f1}"); }
	@Test public void test_2370() { checkIsSubtype("{null f1,null f2}","{{void f2} f1}"); }
	@Test public void test_2371() { checkIsSubtype("{null f1,null f2}","{{void f1} f2}"); }
	@Test public void test_2372() { checkIsSubtype("{null f1,null f2}","{{void f2} f2}"); }
	@Test public void test_2373() { checkIsSubtype("{null f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_2374() { checkIsSubtype("{null f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_2375() { checkIsSubtype("{null f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_2376() { checkIsSubtype("{null f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_2377() { checkNotSubtype("{null f1,null f2}","{{any f1} f1}"); }
	@Test public void test_2378() { checkNotSubtype("{null f1,null f2}","{{any f2} f1}"); }
	@Test public void test_2379() { checkNotSubtype("{null f1,null f2}","{{any f1} f2}"); }
	@Test public void test_2380() { checkNotSubtype("{null f1,null f2}","{{any f2} f2}"); }
	@Test public void test_2381() { checkNotSubtype("{null f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_2382() { checkNotSubtype("{null f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_2383() { checkNotSubtype("{null f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_2384() { checkNotSubtype("{null f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_2385() { checkNotSubtype("{null f1,null f2}","{{null f1} f1}"); }
	@Test public void test_2386() { checkNotSubtype("{null f1,null f2}","{{null f2} f1}"); }
	@Test public void test_2387() { checkNotSubtype("{null f1,null f2}","{{null f1} f2}"); }
	@Test public void test_2388() { checkNotSubtype("{null f1,null f2}","{{null f2} f2}"); }
	@Test public void test_2389() { checkNotSubtype("{null f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_2390() { checkNotSubtype("{null f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_2391() { checkNotSubtype("{null f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_2392() { checkNotSubtype("{null f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_2393() { checkNotSubtype("{null f1,null f2}","{{int f1} f1}"); }
	@Test public void test_2394() { checkNotSubtype("{null f1,null f2}","{{int f2} f1}"); }
	@Test public void test_2395() { checkNotSubtype("{null f1,null f2}","{{int f1} f2}"); }
	@Test public void test_2396() { checkNotSubtype("{null f1,null f2}","{{int f2} f2}"); }
	@Test public void test_2397() { checkNotSubtype("{null f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_2398() { checkNotSubtype("{null f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_2399() { checkNotSubtype("{null f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_2400() { checkNotSubtype("{null f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_2401() { checkNotSubtype("{null f2,null f3}","any"); }
	@Test public void test_2402() { checkNotSubtype("{null f2,null f3}","null"); }
	@Test public void test_2403() { checkNotSubtype("{null f2,null f3}","int"); }
	@Test public void test_2404() { checkIsSubtype("{null f2,null f3}","{void f1}"); }
	@Test public void test_2405() { checkIsSubtype("{null f2,null f3}","{void f2}"); }
	@Test public void test_2406() { checkNotSubtype("{null f2,null f3}","{any f1}"); }
	@Test public void test_2407() { checkNotSubtype("{null f2,null f3}","{any f2}"); }
	@Test public void test_2408() { checkNotSubtype("{null f2,null f3}","{null f1}"); }
	@Test public void test_2409() { checkNotSubtype("{null f2,null f3}","{null f2}"); }
	@Test public void test_2410() { checkNotSubtype("{null f2,null f3}","{int f1}"); }
	@Test public void test_2411() { checkNotSubtype("{null f2,null f3}","{int f2}"); }
	@Test public void test_2412() { checkIsSubtype("{null f2,null f3}","{void f1,void f2}"); }
	@Test public void test_2413() { checkIsSubtype("{null f2,null f3}","{void f2,void f3}"); }
	@Test public void test_2414() { checkIsSubtype("{null f2,null f3}","{void f1,any f2}"); }
	@Test public void test_2415() { checkIsSubtype("{null f2,null f3}","{void f2,any f3}"); }
	@Test public void test_2416() { checkIsSubtype("{null f2,null f3}","{void f1,null f2}"); }
	@Test public void test_2417() { checkIsSubtype("{null f2,null f3}","{void f2,null f3}"); }
	@Test public void test_2418() { checkIsSubtype("{null f2,null f3}","{void f1,int f2}"); }
	@Test public void test_2419() { checkIsSubtype("{null f2,null f3}","{void f2,int f3}"); }
	@Test public void test_2420() { checkIsSubtype("{null f2,null f3}","{any f1,void f2}"); }
	@Test public void test_2421() { checkIsSubtype("{null f2,null f3}","{any f2,void f3}"); }
	@Test public void test_2422() { checkNotSubtype("{null f2,null f3}","{any f1,any f2}"); }
	@Test public void test_2423() { checkNotSubtype("{null f2,null f3}","{any f2,any f3}"); }
	@Test public void test_2424() { checkNotSubtype("{null f2,null f3}","{any f1,null f2}"); }
	@Test public void test_2425() { checkNotSubtype("{null f2,null f3}","{any f2,null f3}"); }
	@Test public void test_2426() { checkNotSubtype("{null f2,null f3}","{any f1,int f2}"); }
	@Test public void test_2427() { checkNotSubtype("{null f2,null f3}","{any f2,int f3}"); }
	@Test public void test_2428() { checkIsSubtype("{null f2,null f3}","{null f1,void f2}"); }
	@Test public void test_2429() { checkIsSubtype("{null f2,null f3}","{null f2,void f3}"); }
	@Test public void test_2430() { checkNotSubtype("{null f2,null f3}","{null f1,any f2}"); }
	@Test public void test_2431() { checkNotSubtype("{null f2,null f3}","{null f2,any f3}"); }
	@Test public void test_2432() { checkNotSubtype("{null f2,null f3}","{null f1,null f2}"); }
	@Test public void test_2433() { checkIsSubtype("{null f2,null f3}","{null f2,null f3}"); }
	@Test public void test_2434() { checkNotSubtype("{null f2,null f3}","{null f1,int f2}"); }
	@Test public void test_2435() { checkNotSubtype("{null f2,null f3}","{null f2,int f3}"); }
	@Test public void test_2436() { checkIsSubtype("{null f2,null f3}","{int f1,void f2}"); }
	@Test public void test_2437() { checkIsSubtype("{null f2,null f3}","{int f2,void f3}"); }
	@Test public void test_2438() { checkNotSubtype("{null f2,null f3}","{int f1,any f2}"); }
	@Test public void test_2439() { checkNotSubtype("{null f2,null f3}","{int f2,any f3}"); }
	@Test public void test_2440() { checkNotSubtype("{null f2,null f3}","{int f1,null f2}"); }
	@Test public void test_2441() { checkNotSubtype("{null f2,null f3}","{int f2,null f3}"); }
	@Test public void test_2442() { checkNotSubtype("{null f2,null f3}","{int f1,int f2}"); }
	@Test public void test_2443() { checkNotSubtype("{null f2,null f3}","{int f2,int f3}"); }
	@Test public void test_2444() { checkIsSubtype("{null f2,null f3}","{{void f1} f1}"); }
	@Test public void test_2445() { checkIsSubtype("{null f2,null f3}","{{void f2} f1}"); }
	@Test public void test_2446() { checkIsSubtype("{null f2,null f3}","{{void f1} f2}"); }
	@Test public void test_2447() { checkIsSubtype("{null f2,null f3}","{{void f2} f2}"); }
	@Test public void test_2448() { checkIsSubtype("{null f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_2449() { checkIsSubtype("{null f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_2450() { checkIsSubtype("{null f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_2451() { checkIsSubtype("{null f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_2452() { checkNotSubtype("{null f2,null f3}","{{any f1} f1}"); }
	@Test public void test_2453() { checkNotSubtype("{null f2,null f3}","{{any f2} f1}"); }
	@Test public void test_2454() { checkNotSubtype("{null f2,null f3}","{{any f1} f2}"); }
	@Test public void test_2455() { checkNotSubtype("{null f2,null f3}","{{any f2} f2}"); }
	@Test public void test_2456() { checkNotSubtype("{null f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_2457() { checkNotSubtype("{null f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_2458() { checkNotSubtype("{null f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_2459() { checkNotSubtype("{null f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_2460() { checkNotSubtype("{null f2,null f3}","{{null f1} f1}"); }
	@Test public void test_2461() { checkNotSubtype("{null f2,null f3}","{{null f2} f1}"); }
	@Test public void test_2462() { checkNotSubtype("{null f2,null f3}","{{null f1} f2}"); }
	@Test public void test_2463() { checkNotSubtype("{null f2,null f3}","{{null f2} f2}"); }
	@Test public void test_2464() { checkNotSubtype("{null f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_2465() { checkNotSubtype("{null f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_2466() { checkNotSubtype("{null f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_2467() { checkNotSubtype("{null f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_2468() { checkNotSubtype("{null f2,null f3}","{{int f1} f1}"); }
	@Test public void test_2469() { checkNotSubtype("{null f2,null f3}","{{int f2} f1}"); }
	@Test public void test_2470() { checkNotSubtype("{null f2,null f3}","{{int f1} f2}"); }
	@Test public void test_2471() { checkNotSubtype("{null f2,null f3}","{{int f2} f2}"); }
	@Test public void test_2472() { checkNotSubtype("{null f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_2473() { checkNotSubtype("{null f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_2474() { checkNotSubtype("{null f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_2475() { checkNotSubtype("{null f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_2476() { checkNotSubtype("{null f1,int f2}","any"); }
	@Test public void test_2477() { checkNotSubtype("{null f1,int f2}","null"); }
	@Test public void test_2478() { checkNotSubtype("{null f1,int f2}","int"); }
	@Test public void test_2479() { checkIsSubtype("{null f1,int f2}","{void f1}"); }
	@Test public void test_2480() { checkIsSubtype("{null f1,int f2}","{void f2}"); }
	@Test public void test_2481() { checkNotSubtype("{null f1,int f2}","{any f1}"); }
	@Test public void test_2482() { checkNotSubtype("{null f1,int f2}","{any f2}"); }
	@Test public void test_2483() { checkNotSubtype("{null f1,int f2}","{null f1}"); }
	@Test public void test_2484() { checkNotSubtype("{null f1,int f2}","{null f2}"); }
	@Test public void test_2485() { checkNotSubtype("{null f1,int f2}","{int f1}"); }
	@Test public void test_2486() { checkNotSubtype("{null f1,int f2}","{int f2}"); }
	@Test public void test_2487() { checkIsSubtype("{null f1,int f2}","{void f1,void f2}"); }
	@Test public void test_2488() { checkIsSubtype("{null f1,int f2}","{void f2,void f3}"); }
	@Test public void test_2489() { checkIsSubtype("{null f1,int f2}","{void f1,any f2}"); }
	@Test public void test_2490() { checkIsSubtype("{null f1,int f2}","{void f2,any f3}"); }
	@Test public void test_2491() { checkIsSubtype("{null f1,int f2}","{void f1,null f2}"); }
	@Test public void test_2492() { checkIsSubtype("{null f1,int f2}","{void f2,null f3}"); }
	@Test public void test_2493() { checkIsSubtype("{null f1,int f2}","{void f1,int f2}"); }
	@Test public void test_2494() { checkIsSubtype("{null f1,int f2}","{void f2,int f3}"); }
	@Test public void test_2495() { checkIsSubtype("{null f1,int f2}","{any f1,void f2}"); }
	@Test public void test_2496() { checkIsSubtype("{null f1,int f2}","{any f2,void f3}"); }
	@Test public void test_2497() { checkNotSubtype("{null f1,int f2}","{any f1,any f2}"); }
	@Test public void test_2498() { checkNotSubtype("{null f1,int f2}","{any f2,any f3}"); }
	@Test public void test_2499() { checkNotSubtype("{null f1,int f2}","{any f1,null f2}"); }
	@Test public void test_2500() { checkNotSubtype("{null f1,int f2}","{any f2,null f3}"); }
	@Test public void test_2501() { checkNotSubtype("{null f1,int f2}","{any f1,int f2}"); }
	@Test public void test_2502() { checkNotSubtype("{null f1,int f2}","{any f2,int f3}"); }
	@Test public void test_2503() { checkIsSubtype("{null f1,int f2}","{null f1,void f2}"); }
	@Test public void test_2504() { checkIsSubtype("{null f1,int f2}","{null f2,void f3}"); }
	@Test public void test_2505() { checkNotSubtype("{null f1,int f2}","{null f1,any f2}"); }
	@Test public void test_2506() { checkNotSubtype("{null f1,int f2}","{null f2,any f3}"); }
	@Test public void test_2507() { checkNotSubtype("{null f1,int f2}","{null f1,null f2}"); }
	@Test public void test_2508() { checkNotSubtype("{null f1,int f2}","{null f2,null f3}"); }
	@Test public void test_2509() { checkIsSubtype("{null f1,int f2}","{null f1,int f2}"); }
	@Test public void test_2510() { checkNotSubtype("{null f1,int f2}","{null f2,int f3}"); }
	@Test public void test_2511() { checkIsSubtype("{null f1,int f2}","{int f1,void f2}"); }
	@Test public void test_2512() { checkIsSubtype("{null f1,int f2}","{int f2,void f3}"); }
	@Test public void test_2513() { checkNotSubtype("{null f1,int f2}","{int f1,any f2}"); }
	@Test public void test_2514() { checkNotSubtype("{null f1,int f2}","{int f2,any f3}"); }
	@Test public void test_2515() { checkNotSubtype("{null f1,int f2}","{int f1,null f2}"); }
	@Test public void test_2516() { checkNotSubtype("{null f1,int f2}","{int f2,null f3}"); }
	@Test public void test_2517() { checkNotSubtype("{null f1,int f2}","{int f1,int f2}"); }
	@Test public void test_2518() { checkNotSubtype("{null f1,int f2}","{int f2,int f3}"); }
	@Test public void test_2519() { checkIsSubtype("{null f1,int f2}","{{void f1} f1}"); }
	@Test public void test_2520() { checkIsSubtype("{null f1,int f2}","{{void f2} f1}"); }
	@Test public void test_2521() { checkIsSubtype("{null f1,int f2}","{{void f1} f2}"); }
	@Test public void test_2522() { checkIsSubtype("{null f1,int f2}","{{void f2} f2}"); }
	@Test public void test_2523() { checkIsSubtype("{null f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_2524() { checkIsSubtype("{null f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_2525() { checkIsSubtype("{null f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_2526() { checkIsSubtype("{null f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_2527() { checkNotSubtype("{null f1,int f2}","{{any f1} f1}"); }
	@Test public void test_2528() { checkNotSubtype("{null f1,int f2}","{{any f2} f1}"); }
	@Test public void test_2529() { checkNotSubtype("{null f1,int f2}","{{any f1} f2}"); }
	@Test public void test_2530() { checkNotSubtype("{null f1,int f2}","{{any f2} f2}"); }
	@Test public void test_2531() { checkNotSubtype("{null f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_2532() { checkNotSubtype("{null f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_2533() { checkNotSubtype("{null f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_2534() { checkNotSubtype("{null f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_2535() { checkNotSubtype("{null f1,int f2}","{{null f1} f1}"); }
	@Test public void test_2536() { checkNotSubtype("{null f1,int f2}","{{null f2} f1}"); }
	@Test public void test_2537() { checkNotSubtype("{null f1,int f2}","{{null f1} f2}"); }
	@Test public void test_2538() { checkNotSubtype("{null f1,int f2}","{{null f2} f2}"); }
	@Test public void test_2539() { checkNotSubtype("{null f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_2540() { checkNotSubtype("{null f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_2541() { checkNotSubtype("{null f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_2542() { checkNotSubtype("{null f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_2543() { checkNotSubtype("{null f1,int f2}","{{int f1} f1}"); }
	@Test public void test_2544() { checkNotSubtype("{null f1,int f2}","{{int f2} f1}"); }
	@Test public void test_2545() { checkNotSubtype("{null f1,int f2}","{{int f1} f2}"); }
	@Test public void test_2546() { checkNotSubtype("{null f1,int f2}","{{int f2} f2}"); }
	@Test public void test_2547() { checkNotSubtype("{null f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_2548() { checkNotSubtype("{null f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_2549() { checkNotSubtype("{null f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_2550() { checkNotSubtype("{null f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_2551() { checkNotSubtype("{null f2,int f3}","any"); }
	@Test public void test_2552() { checkNotSubtype("{null f2,int f3}","null"); }
	@Test public void test_2553() { checkNotSubtype("{null f2,int f3}","int"); }
	@Test public void test_2554() { checkIsSubtype("{null f2,int f3}","{void f1}"); }
	@Test public void test_2555() { checkIsSubtype("{null f2,int f3}","{void f2}"); }
	@Test public void test_2556() { checkNotSubtype("{null f2,int f3}","{any f1}"); }
	@Test public void test_2557() { checkNotSubtype("{null f2,int f3}","{any f2}"); }
	@Test public void test_2558() { checkNotSubtype("{null f2,int f3}","{null f1}"); }
	@Test public void test_2559() { checkNotSubtype("{null f2,int f3}","{null f2}"); }
	@Test public void test_2560() { checkNotSubtype("{null f2,int f3}","{int f1}"); }
	@Test public void test_2561() { checkNotSubtype("{null f2,int f3}","{int f2}"); }
	@Test public void test_2562() { checkIsSubtype("{null f2,int f3}","{void f1,void f2}"); }
	@Test public void test_2563() { checkIsSubtype("{null f2,int f3}","{void f2,void f3}"); }
	@Test public void test_2564() { checkIsSubtype("{null f2,int f3}","{void f1,any f2}"); }
	@Test public void test_2565() { checkIsSubtype("{null f2,int f3}","{void f2,any f3}"); }
	@Test public void test_2566() { checkIsSubtype("{null f2,int f3}","{void f1,null f2}"); }
	@Test public void test_2567() { checkIsSubtype("{null f2,int f3}","{void f2,null f3}"); }
	@Test public void test_2568() { checkIsSubtype("{null f2,int f3}","{void f1,int f2}"); }
	@Test public void test_2569() { checkIsSubtype("{null f2,int f3}","{void f2,int f3}"); }
	@Test public void test_2570() { checkIsSubtype("{null f2,int f3}","{any f1,void f2}"); }
	@Test public void test_2571() { checkIsSubtype("{null f2,int f3}","{any f2,void f3}"); }
	@Test public void test_2572() { checkNotSubtype("{null f2,int f3}","{any f1,any f2}"); }
	@Test public void test_2573() { checkNotSubtype("{null f2,int f3}","{any f2,any f3}"); }
	@Test public void test_2574() { checkNotSubtype("{null f2,int f3}","{any f1,null f2}"); }
	@Test public void test_2575() { checkNotSubtype("{null f2,int f3}","{any f2,null f3}"); }
	@Test public void test_2576() { checkNotSubtype("{null f2,int f3}","{any f1,int f2}"); }
	@Test public void test_2577() { checkNotSubtype("{null f2,int f3}","{any f2,int f3}"); }
	@Test public void test_2578() { checkIsSubtype("{null f2,int f3}","{null f1,void f2}"); }
	@Test public void test_2579() { checkIsSubtype("{null f2,int f3}","{null f2,void f3}"); }
	@Test public void test_2580() { checkNotSubtype("{null f2,int f3}","{null f1,any f2}"); }
	@Test public void test_2581() { checkNotSubtype("{null f2,int f3}","{null f2,any f3}"); }
	@Test public void test_2582() { checkNotSubtype("{null f2,int f3}","{null f1,null f2}"); }
	@Test public void test_2583() { checkNotSubtype("{null f2,int f3}","{null f2,null f3}"); }
	@Test public void test_2584() { checkNotSubtype("{null f2,int f3}","{null f1,int f2}"); }
	@Test public void test_2585() { checkIsSubtype("{null f2,int f3}","{null f2,int f3}"); }
	@Test public void test_2586() { checkIsSubtype("{null f2,int f3}","{int f1,void f2}"); }
	@Test public void test_2587() { checkIsSubtype("{null f2,int f3}","{int f2,void f3}"); }
	@Test public void test_2588() { checkNotSubtype("{null f2,int f3}","{int f1,any f2}"); }
	@Test public void test_2589() { checkNotSubtype("{null f2,int f3}","{int f2,any f3}"); }
	@Test public void test_2590() { checkNotSubtype("{null f2,int f3}","{int f1,null f2}"); }
	@Test public void test_2591() { checkNotSubtype("{null f2,int f3}","{int f2,null f3}"); }
	@Test public void test_2592() { checkNotSubtype("{null f2,int f3}","{int f1,int f2}"); }
	@Test public void test_2593() { checkNotSubtype("{null f2,int f3}","{int f2,int f3}"); }
	@Test public void test_2594() { checkIsSubtype("{null f2,int f3}","{{void f1} f1}"); }
	@Test public void test_2595() { checkIsSubtype("{null f2,int f3}","{{void f2} f1}"); }
	@Test public void test_2596() { checkIsSubtype("{null f2,int f3}","{{void f1} f2}"); }
	@Test public void test_2597() { checkIsSubtype("{null f2,int f3}","{{void f2} f2}"); }
	@Test public void test_2598() { checkIsSubtype("{null f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_2599() { checkIsSubtype("{null f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_2600() { checkIsSubtype("{null f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_2601() { checkIsSubtype("{null f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_2602() { checkNotSubtype("{null f2,int f3}","{{any f1} f1}"); }
	@Test public void test_2603() { checkNotSubtype("{null f2,int f3}","{{any f2} f1}"); }
	@Test public void test_2604() { checkNotSubtype("{null f2,int f3}","{{any f1} f2}"); }
	@Test public void test_2605() { checkNotSubtype("{null f2,int f3}","{{any f2} f2}"); }
	@Test public void test_2606() { checkNotSubtype("{null f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_2607() { checkNotSubtype("{null f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_2608() { checkNotSubtype("{null f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_2609() { checkNotSubtype("{null f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_2610() { checkNotSubtype("{null f2,int f3}","{{null f1} f1}"); }
	@Test public void test_2611() { checkNotSubtype("{null f2,int f3}","{{null f2} f1}"); }
	@Test public void test_2612() { checkNotSubtype("{null f2,int f3}","{{null f1} f2}"); }
	@Test public void test_2613() { checkNotSubtype("{null f2,int f3}","{{null f2} f2}"); }
	@Test public void test_2614() { checkNotSubtype("{null f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_2615() { checkNotSubtype("{null f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_2616() { checkNotSubtype("{null f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_2617() { checkNotSubtype("{null f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_2618() { checkNotSubtype("{null f2,int f3}","{{int f1} f1}"); }
	@Test public void test_2619() { checkNotSubtype("{null f2,int f3}","{{int f2} f1}"); }
	@Test public void test_2620() { checkNotSubtype("{null f2,int f3}","{{int f1} f2}"); }
	@Test public void test_2621() { checkNotSubtype("{null f2,int f3}","{{int f2} f2}"); }
	@Test public void test_2622() { checkNotSubtype("{null f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_2623() { checkNotSubtype("{null f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_2624() { checkNotSubtype("{null f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_2625() { checkNotSubtype("{null f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_2626() { checkNotSubtype("{int f1,void f2}","any"); }
	@Test public void test_2627() { checkNotSubtype("{int f1,void f2}","null"); }
	@Test public void test_2628() { checkNotSubtype("{int f1,void f2}","int"); }
	@Test public void test_2629() { checkIsSubtype("{int f1,void f2}","{void f1}"); }
	@Test public void test_2630() { checkIsSubtype("{int f1,void f2}","{void f2}"); }
	@Test public void test_2631() { checkNotSubtype("{int f1,void f2}","{any f1}"); }
	@Test public void test_2632() { checkNotSubtype("{int f1,void f2}","{any f2}"); }
	@Test public void test_2633() { checkNotSubtype("{int f1,void f2}","{null f1}"); }
	@Test public void test_2634() { checkNotSubtype("{int f1,void f2}","{null f2}"); }
	@Test public void test_2635() { checkNotSubtype("{int f1,void f2}","{int f1}"); }
	@Test public void test_2636() { checkNotSubtype("{int f1,void f2}","{int f2}"); }
	@Test public void test_2637() { checkIsSubtype("{int f1,void f2}","{void f1,void f2}"); }
	@Test public void test_2638() { checkIsSubtype("{int f1,void f2}","{void f2,void f3}"); }
	@Test public void test_2639() { checkIsSubtype("{int f1,void f2}","{void f1,any f2}"); }
	@Test public void test_2640() { checkIsSubtype("{int f1,void f2}","{void f2,any f3}"); }
	@Test public void test_2641() { checkIsSubtype("{int f1,void f2}","{void f1,null f2}"); }
	@Test public void test_2642() { checkIsSubtype("{int f1,void f2}","{void f2,null f3}"); }
	@Test public void test_2643() { checkIsSubtype("{int f1,void f2}","{void f1,int f2}"); }
	@Test public void test_2644() { checkIsSubtype("{int f1,void f2}","{void f2,int f3}"); }
	@Test public void test_2645() { checkIsSubtype("{int f1,void f2}","{any f1,void f2}"); }
	@Test public void test_2646() { checkIsSubtype("{int f1,void f2}","{any f2,void f3}"); }
	@Test public void test_2647() { checkNotSubtype("{int f1,void f2}","{any f1,any f2}"); }
	@Test public void test_2648() { checkNotSubtype("{int f1,void f2}","{any f2,any f3}"); }
	@Test public void test_2649() { checkNotSubtype("{int f1,void f2}","{any f1,null f2}"); }
	@Test public void test_2650() { checkNotSubtype("{int f1,void f2}","{any f2,null f3}"); }
	@Test public void test_2651() { checkNotSubtype("{int f1,void f2}","{any f1,int f2}"); }
	@Test public void test_2652() { checkNotSubtype("{int f1,void f2}","{any f2,int f3}"); }
	@Test public void test_2653() { checkIsSubtype("{int f1,void f2}","{null f1,void f2}"); }
	@Test public void test_2654() { checkIsSubtype("{int f1,void f2}","{null f2,void f3}"); }
	@Test public void test_2655() { checkNotSubtype("{int f1,void f2}","{null f1,any f2}"); }
	@Test public void test_2656() { checkNotSubtype("{int f1,void f2}","{null f2,any f3}"); }
	@Test public void test_2657() { checkNotSubtype("{int f1,void f2}","{null f1,null f2}"); }
	@Test public void test_2658() { checkNotSubtype("{int f1,void f2}","{null f2,null f3}"); }
	@Test public void test_2659() { checkNotSubtype("{int f1,void f2}","{null f1,int f2}"); }
	@Test public void test_2660() { checkNotSubtype("{int f1,void f2}","{null f2,int f3}"); }
	@Test public void test_2661() { checkIsSubtype("{int f1,void f2}","{int f1,void f2}"); }
	@Test public void test_2662() { checkIsSubtype("{int f1,void f2}","{int f2,void f3}"); }
	@Test public void test_2663() { checkNotSubtype("{int f1,void f2}","{int f1,any f2}"); }
	@Test public void test_2664() { checkNotSubtype("{int f1,void f2}","{int f2,any f3}"); }
	@Test public void test_2665() { checkNotSubtype("{int f1,void f2}","{int f1,null f2}"); }
	@Test public void test_2666() { checkNotSubtype("{int f1,void f2}","{int f2,null f3}"); }
	@Test public void test_2667() { checkNotSubtype("{int f1,void f2}","{int f1,int f2}"); }
	@Test public void test_2668() { checkNotSubtype("{int f1,void f2}","{int f2,int f3}"); }
	@Test public void test_2669() { checkIsSubtype("{int f1,void f2}","{{void f1} f1}"); }
	@Test public void test_2670() { checkIsSubtype("{int f1,void f2}","{{void f2} f1}"); }
	@Test public void test_2671() { checkIsSubtype("{int f1,void f2}","{{void f1} f2}"); }
	@Test public void test_2672() { checkIsSubtype("{int f1,void f2}","{{void f2} f2}"); }
	@Test public void test_2673() { checkIsSubtype("{int f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_2674() { checkIsSubtype("{int f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_2675() { checkIsSubtype("{int f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_2676() { checkIsSubtype("{int f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_2677() { checkNotSubtype("{int f1,void f2}","{{any f1} f1}"); }
	@Test public void test_2678() { checkNotSubtype("{int f1,void f2}","{{any f2} f1}"); }
	@Test public void test_2679() { checkNotSubtype("{int f1,void f2}","{{any f1} f2}"); }
	@Test public void test_2680() { checkNotSubtype("{int f1,void f2}","{{any f2} f2}"); }
	@Test public void test_2681() { checkNotSubtype("{int f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_2682() { checkNotSubtype("{int f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_2683() { checkNotSubtype("{int f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_2684() { checkNotSubtype("{int f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_2685() { checkNotSubtype("{int f1,void f2}","{{null f1} f1}"); }
	@Test public void test_2686() { checkNotSubtype("{int f1,void f2}","{{null f2} f1}"); }
	@Test public void test_2687() { checkNotSubtype("{int f1,void f2}","{{null f1} f2}"); }
	@Test public void test_2688() { checkNotSubtype("{int f1,void f2}","{{null f2} f2}"); }
	@Test public void test_2689() { checkNotSubtype("{int f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_2690() { checkNotSubtype("{int f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_2691() { checkNotSubtype("{int f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_2692() { checkNotSubtype("{int f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_2693() { checkNotSubtype("{int f1,void f2}","{{int f1} f1}"); }
	@Test public void test_2694() { checkNotSubtype("{int f1,void f2}","{{int f2} f1}"); }
	@Test public void test_2695() { checkNotSubtype("{int f1,void f2}","{{int f1} f2}"); }
	@Test public void test_2696() { checkNotSubtype("{int f1,void f2}","{{int f2} f2}"); }
	@Test public void test_2697() { checkNotSubtype("{int f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_2698() { checkNotSubtype("{int f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_2699() { checkNotSubtype("{int f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_2700() { checkNotSubtype("{int f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_2701() { checkNotSubtype("{int f2,void f3}","any"); }
	@Test public void test_2702() { checkNotSubtype("{int f2,void f3}","null"); }
	@Test public void test_2703() { checkNotSubtype("{int f2,void f3}","int"); }
	@Test public void test_2704() { checkIsSubtype("{int f2,void f3}","{void f1}"); }
	@Test public void test_2705() { checkIsSubtype("{int f2,void f3}","{void f2}"); }
	@Test public void test_2706() { checkNotSubtype("{int f2,void f3}","{any f1}"); }
	@Test public void test_2707() { checkNotSubtype("{int f2,void f3}","{any f2}"); }
	@Test public void test_2708() { checkNotSubtype("{int f2,void f3}","{null f1}"); }
	@Test public void test_2709() { checkNotSubtype("{int f2,void f3}","{null f2}"); }
	@Test public void test_2710() { checkNotSubtype("{int f2,void f3}","{int f1}"); }
	@Test public void test_2711() { checkNotSubtype("{int f2,void f3}","{int f2}"); }
	@Test public void test_2712() { checkIsSubtype("{int f2,void f3}","{void f1,void f2}"); }
	@Test public void test_2713() { checkIsSubtype("{int f2,void f3}","{void f2,void f3}"); }
	@Test public void test_2714() { checkIsSubtype("{int f2,void f3}","{void f1,any f2}"); }
	@Test public void test_2715() { checkIsSubtype("{int f2,void f3}","{void f2,any f3}"); }
	@Test public void test_2716() { checkIsSubtype("{int f2,void f3}","{void f1,null f2}"); }
	@Test public void test_2717() { checkIsSubtype("{int f2,void f3}","{void f2,null f3}"); }
	@Test public void test_2718() { checkIsSubtype("{int f2,void f3}","{void f1,int f2}"); }
	@Test public void test_2719() { checkIsSubtype("{int f2,void f3}","{void f2,int f3}"); }
	@Test public void test_2720() { checkIsSubtype("{int f2,void f3}","{any f1,void f2}"); }
	@Test public void test_2721() { checkIsSubtype("{int f2,void f3}","{any f2,void f3}"); }
	@Test public void test_2722() { checkNotSubtype("{int f2,void f3}","{any f1,any f2}"); }
	@Test public void test_2723() { checkNotSubtype("{int f2,void f3}","{any f2,any f3}"); }
	@Test public void test_2724() { checkNotSubtype("{int f2,void f3}","{any f1,null f2}"); }
	@Test public void test_2725() { checkNotSubtype("{int f2,void f3}","{any f2,null f3}"); }
	@Test public void test_2726() { checkNotSubtype("{int f2,void f3}","{any f1,int f2}"); }
	@Test public void test_2727() { checkNotSubtype("{int f2,void f3}","{any f2,int f3}"); }
	@Test public void test_2728() { checkIsSubtype("{int f2,void f3}","{null f1,void f2}"); }
	@Test public void test_2729() { checkIsSubtype("{int f2,void f3}","{null f2,void f3}"); }
	@Test public void test_2730() { checkNotSubtype("{int f2,void f3}","{null f1,any f2}"); }
	@Test public void test_2731() { checkNotSubtype("{int f2,void f3}","{null f2,any f3}"); }
	@Test public void test_2732() { checkNotSubtype("{int f2,void f3}","{null f1,null f2}"); }
	@Test public void test_2733() { checkNotSubtype("{int f2,void f3}","{null f2,null f3}"); }
	@Test public void test_2734() { checkNotSubtype("{int f2,void f3}","{null f1,int f2}"); }
	@Test public void test_2735() { checkNotSubtype("{int f2,void f3}","{null f2,int f3}"); }
	@Test public void test_2736() { checkIsSubtype("{int f2,void f3}","{int f1,void f2}"); }
	@Test public void test_2737() { checkIsSubtype("{int f2,void f3}","{int f2,void f3}"); }
	@Test public void test_2738() { checkNotSubtype("{int f2,void f3}","{int f1,any f2}"); }
	@Test public void test_2739() { checkNotSubtype("{int f2,void f3}","{int f2,any f3}"); }
	@Test public void test_2740() { checkNotSubtype("{int f2,void f3}","{int f1,null f2}"); }
	@Test public void test_2741() { checkNotSubtype("{int f2,void f3}","{int f2,null f3}"); }
	@Test public void test_2742() { checkNotSubtype("{int f2,void f3}","{int f1,int f2}"); }
	@Test public void test_2743() { checkNotSubtype("{int f2,void f3}","{int f2,int f3}"); }
	@Test public void test_2744() { checkIsSubtype("{int f2,void f3}","{{void f1} f1}"); }
	@Test public void test_2745() { checkIsSubtype("{int f2,void f3}","{{void f2} f1}"); }
	@Test public void test_2746() { checkIsSubtype("{int f2,void f3}","{{void f1} f2}"); }
	@Test public void test_2747() { checkIsSubtype("{int f2,void f3}","{{void f2} f2}"); }
	@Test public void test_2748() { checkIsSubtype("{int f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_2749() { checkIsSubtype("{int f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_2750() { checkIsSubtype("{int f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_2751() { checkIsSubtype("{int f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_2752() { checkNotSubtype("{int f2,void f3}","{{any f1} f1}"); }
	@Test public void test_2753() { checkNotSubtype("{int f2,void f3}","{{any f2} f1}"); }
	@Test public void test_2754() { checkNotSubtype("{int f2,void f3}","{{any f1} f2}"); }
	@Test public void test_2755() { checkNotSubtype("{int f2,void f3}","{{any f2} f2}"); }
	@Test public void test_2756() { checkNotSubtype("{int f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_2757() { checkNotSubtype("{int f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_2758() { checkNotSubtype("{int f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_2759() { checkNotSubtype("{int f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_2760() { checkNotSubtype("{int f2,void f3}","{{null f1} f1}"); }
	@Test public void test_2761() { checkNotSubtype("{int f2,void f3}","{{null f2} f1}"); }
	@Test public void test_2762() { checkNotSubtype("{int f2,void f3}","{{null f1} f2}"); }
	@Test public void test_2763() { checkNotSubtype("{int f2,void f3}","{{null f2} f2}"); }
	@Test public void test_2764() { checkNotSubtype("{int f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_2765() { checkNotSubtype("{int f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_2766() { checkNotSubtype("{int f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_2767() { checkNotSubtype("{int f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_2768() { checkNotSubtype("{int f2,void f3}","{{int f1} f1}"); }
	@Test public void test_2769() { checkNotSubtype("{int f2,void f3}","{{int f2} f1}"); }
	@Test public void test_2770() { checkNotSubtype("{int f2,void f3}","{{int f1} f2}"); }
	@Test public void test_2771() { checkNotSubtype("{int f2,void f3}","{{int f2} f2}"); }
	@Test public void test_2772() { checkNotSubtype("{int f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_2773() { checkNotSubtype("{int f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_2774() { checkNotSubtype("{int f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_2775() { checkNotSubtype("{int f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_2776() { checkNotSubtype("{int f1,any f2}","any"); }
	@Test public void test_2777() { checkNotSubtype("{int f1,any f2}","null"); }
	@Test public void test_2778() { checkNotSubtype("{int f1,any f2}","int"); }
	@Test public void test_2779() { checkIsSubtype("{int f1,any f2}","{void f1}"); }
	@Test public void test_2780() { checkIsSubtype("{int f1,any f2}","{void f2}"); }
	@Test public void test_2781() { checkNotSubtype("{int f1,any f2}","{any f1}"); }
	@Test public void test_2782() { checkNotSubtype("{int f1,any f2}","{any f2}"); }
	@Test public void test_2783() { checkNotSubtype("{int f1,any f2}","{null f1}"); }
	@Test public void test_2784() { checkNotSubtype("{int f1,any f2}","{null f2}"); }
	@Test public void test_2785() { checkNotSubtype("{int f1,any f2}","{int f1}"); }
	@Test public void test_2786() { checkNotSubtype("{int f1,any f2}","{int f2}"); }
	@Test public void test_2787() { checkIsSubtype("{int f1,any f2}","{void f1,void f2}"); }
	@Test public void test_2788() { checkIsSubtype("{int f1,any f2}","{void f2,void f3}"); }
	@Test public void test_2789() { checkIsSubtype("{int f1,any f2}","{void f1,any f2}"); }
	@Test public void test_2790() { checkIsSubtype("{int f1,any f2}","{void f2,any f3}"); }
	@Test public void test_2791() { checkIsSubtype("{int f1,any f2}","{void f1,null f2}"); }
	@Test public void test_2792() { checkIsSubtype("{int f1,any f2}","{void f2,null f3}"); }
	@Test public void test_2793() { checkIsSubtype("{int f1,any f2}","{void f1,int f2}"); }
	@Test public void test_2794() { checkIsSubtype("{int f1,any f2}","{void f2,int f3}"); }
	@Test public void test_2795() { checkIsSubtype("{int f1,any f2}","{any f1,void f2}"); }
	@Test public void test_2796() { checkIsSubtype("{int f1,any f2}","{any f2,void f3}"); }
	@Test public void test_2797() { checkNotSubtype("{int f1,any f2}","{any f1,any f2}"); }
	@Test public void test_2798() { checkNotSubtype("{int f1,any f2}","{any f2,any f3}"); }
	@Test public void test_2799() { checkNotSubtype("{int f1,any f2}","{any f1,null f2}"); }
	@Test public void test_2800() { checkNotSubtype("{int f1,any f2}","{any f2,null f3}"); }
	@Test public void test_2801() { checkNotSubtype("{int f1,any f2}","{any f1,int f2}"); }
	@Test public void test_2802() { checkNotSubtype("{int f1,any f2}","{any f2,int f3}"); }
	@Test public void test_2803() { checkIsSubtype("{int f1,any f2}","{null f1,void f2}"); }
	@Test public void test_2804() { checkIsSubtype("{int f1,any f2}","{null f2,void f3}"); }
	@Test public void test_2805() { checkNotSubtype("{int f1,any f2}","{null f1,any f2}"); }
	@Test public void test_2806() { checkNotSubtype("{int f1,any f2}","{null f2,any f3}"); }
	@Test public void test_2807() { checkNotSubtype("{int f1,any f2}","{null f1,null f2}"); }
	@Test public void test_2808() { checkNotSubtype("{int f1,any f2}","{null f2,null f3}"); }
	@Test public void test_2809() { checkNotSubtype("{int f1,any f2}","{null f1,int f2}"); }
	@Test public void test_2810() { checkNotSubtype("{int f1,any f2}","{null f2,int f3}"); }
	@Test public void test_2811() { checkIsSubtype("{int f1,any f2}","{int f1,void f2}"); }
	@Test public void test_2812() { checkIsSubtype("{int f1,any f2}","{int f2,void f3}"); }
	@Test public void test_2813() { checkIsSubtype("{int f1,any f2}","{int f1,any f2}"); }
	@Test public void test_2814() { checkNotSubtype("{int f1,any f2}","{int f2,any f3}"); }
	@Test public void test_2815() { checkIsSubtype("{int f1,any f2}","{int f1,null f2}"); }
	@Test public void test_2816() { checkNotSubtype("{int f1,any f2}","{int f2,null f3}"); }
	@Test public void test_2817() { checkIsSubtype("{int f1,any f2}","{int f1,int f2}"); }
	@Test public void test_2818() { checkNotSubtype("{int f1,any f2}","{int f2,int f3}"); }
	@Test public void test_2819() { checkIsSubtype("{int f1,any f2}","{{void f1} f1}"); }
	@Test public void test_2820() { checkIsSubtype("{int f1,any f2}","{{void f2} f1}"); }
	@Test public void test_2821() { checkIsSubtype("{int f1,any f2}","{{void f1} f2}"); }
	@Test public void test_2822() { checkIsSubtype("{int f1,any f2}","{{void f2} f2}"); }
	@Test public void test_2823() { checkIsSubtype("{int f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_2824() { checkIsSubtype("{int f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_2825() { checkIsSubtype("{int f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_2826() { checkIsSubtype("{int f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_2827() { checkNotSubtype("{int f1,any f2}","{{any f1} f1}"); }
	@Test public void test_2828() { checkNotSubtype("{int f1,any f2}","{{any f2} f1}"); }
	@Test public void test_2829() { checkNotSubtype("{int f1,any f2}","{{any f1} f2}"); }
	@Test public void test_2830() { checkNotSubtype("{int f1,any f2}","{{any f2} f2}"); }
	@Test public void test_2831() { checkNotSubtype("{int f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_2832() { checkNotSubtype("{int f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_2833() { checkNotSubtype("{int f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_2834() { checkNotSubtype("{int f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_2835() { checkNotSubtype("{int f1,any f2}","{{null f1} f1}"); }
	@Test public void test_2836() { checkNotSubtype("{int f1,any f2}","{{null f2} f1}"); }
	@Test public void test_2837() { checkNotSubtype("{int f1,any f2}","{{null f1} f2}"); }
	@Test public void test_2838() { checkNotSubtype("{int f1,any f2}","{{null f2} f2}"); }
	@Test public void test_2839() { checkNotSubtype("{int f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_2840() { checkNotSubtype("{int f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_2841() { checkNotSubtype("{int f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_2842() { checkNotSubtype("{int f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_2843() { checkNotSubtype("{int f1,any f2}","{{int f1} f1}"); }
	@Test public void test_2844() { checkNotSubtype("{int f1,any f2}","{{int f2} f1}"); }
	@Test public void test_2845() { checkNotSubtype("{int f1,any f2}","{{int f1} f2}"); }
	@Test public void test_2846() { checkNotSubtype("{int f1,any f2}","{{int f2} f2}"); }
	@Test public void test_2847() { checkNotSubtype("{int f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_2848() { checkNotSubtype("{int f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_2849() { checkNotSubtype("{int f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_2850() { checkNotSubtype("{int f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_2851() { checkNotSubtype("{int f2,any f3}","any"); }
	@Test public void test_2852() { checkNotSubtype("{int f2,any f3}","null"); }
	@Test public void test_2853() { checkNotSubtype("{int f2,any f3}","int"); }
	@Test public void test_2854() { checkIsSubtype("{int f2,any f3}","{void f1}"); }
	@Test public void test_2855() { checkIsSubtype("{int f2,any f3}","{void f2}"); }
	@Test public void test_2856() { checkNotSubtype("{int f2,any f3}","{any f1}"); }
	@Test public void test_2857() { checkNotSubtype("{int f2,any f3}","{any f2}"); }
	@Test public void test_2858() { checkNotSubtype("{int f2,any f3}","{null f1}"); }
	@Test public void test_2859() { checkNotSubtype("{int f2,any f3}","{null f2}"); }
	@Test public void test_2860() { checkNotSubtype("{int f2,any f3}","{int f1}"); }
	@Test public void test_2861() { checkNotSubtype("{int f2,any f3}","{int f2}"); }
	@Test public void test_2862() { checkIsSubtype("{int f2,any f3}","{void f1,void f2}"); }
	@Test public void test_2863() { checkIsSubtype("{int f2,any f3}","{void f2,void f3}"); }
	@Test public void test_2864() { checkIsSubtype("{int f2,any f3}","{void f1,any f2}"); }
	@Test public void test_2865() { checkIsSubtype("{int f2,any f3}","{void f2,any f3}"); }
	@Test public void test_2866() { checkIsSubtype("{int f2,any f3}","{void f1,null f2}"); }
	@Test public void test_2867() { checkIsSubtype("{int f2,any f3}","{void f2,null f3}"); }
	@Test public void test_2868() { checkIsSubtype("{int f2,any f3}","{void f1,int f2}"); }
	@Test public void test_2869() { checkIsSubtype("{int f2,any f3}","{void f2,int f3}"); }
	@Test public void test_2870() { checkIsSubtype("{int f2,any f3}","{any f1,void f2}"); }
	@Test public void test_2871() { checkIsSubtype("{int f2,any f3}","{any f2,void f3}"); }
	@Test public void test_2872() { checkNotSubtype("{int f2,any f3}","{any f1,any f2}"); }
	@Test public void test_2873() { checkNotSubtype("{int f2,any f3}","{any f2,any f3}"); }
	@Test public void test_2874() { checkNotSubtype("{int f2,any f3}","{any f1,null f2}"); }
	@Test public void test_2875() { checkNotSubtype("{int f2,any f3}","{any f2,null f3}"); }
	@Test public void test_2876() { checkNotSubtype("{int f2,any f3}","{any f1,int f2}"); }
	@Test public void test_2877() { checkNotSubtype("{int f2,any f3}","{any f2,int f3}"); }
	@Test public void test_2878() { checkIsSubtype("{int f2,any f3}","{null f1,void f2}"); }
	@Test public void test_2879() { checkIsSubtype("{int f2,any f3}","{null f2,void f3}"); }
	@Test public void test_2880() { checkNotSubtype("{int f2,any f3}","{null f1,any f2}"); }
	@Test public void test_2881() { checkNotSubtype("{int f2,any f3}","{null f2,any f3}"); }
	@Test public void test_2882() { checkNotSubtype("{int f2,any f3}","{null f1,null f2}"); }
	@Test public void test_2883() { checkNotSubtype("{int f2,any f3}","{null f2,null f3}"); }
	@Test public void test_2884() { checkNotSubtype("{int f2,any f3}","{null f1,int f2}"); }
	@Test public void test_2885() { checkNotSubtype("{int f2,any f3}","{null f2,int f3}"); }
	@Test public void test_2886() { checkIsSubtype("{int f2,any f3}","{int f1,void f2}"); }
	@Test public void test_2887() { checkIsSubtype("{int f2,any f3}","{int f2,void f3}"); }
	@Test public void test_2888() { checkNotSubtype("{int f2,any f3}","{int f1,any f2}"); }
	@Test public void test_2889() { checkIsSubtype("{int f2,any f3}","{int f2,any f3}"); }
	@Test public void test_2890() { checkNotSubtype("{int f2,any f3}","{int f1,null f2}"); }
	@Test public void test_2891() { checkIsSubtype("{int f2,any f3}","{int f2,null f3}"); }
	@Test public void test_2892() { checkNotSubtype("{int f2,any f3}","{int f1,int f2}"); }
	@Test public void test_2893() { checkIsSubtype("{int f2,any f3}","{int f2,int f3}"); }
	@Test public void test_2894() { checkIsSubtype("{int f2,any f3}","{{void f1} f1}"); }
	@Test public void test_2895() { checkIsSubtype("{int f2,any f3}","{{void f2} f1}"); }
	@Test public void test_2896() { checkIsSubtype("{int f2,any f3}","{{void f1} f2}"); }
	@Test public void test_2897() { checkIsSubtype("{int f2,any f3}","{{void f2} f2}"); }
	@Test public void test_2898() { checkIsSubtype("{int f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_2899() { checkIsSubtype("{int f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_2900() { checkIsSubtype("{int f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_2901() { checkIsSubtype("{int f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_2902() { checkNotSubtype("{int f2,any f3}","{{any f1} f1}"); }
	@Test public void test_2903() { checkNotSubtype("{int f2,any f3}","{{any f2} f1}"); }
	@Test public void test_2904() { checkNotSubtype("{int f2,any f3}","{{any f1} f2}"); }
	@Test public void test_2905() { checkNotSubtype("{int f2,any f3}","{{any f2} f2}"); }
	@Test public void test_2906() { checkNotSubtype("{int f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_2907() { checkNotSubtype("{int f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_2908() { checkNotSubtype("{int f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_2909() { checkNotSubtype("{int f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_2910() { checkNotSubtype("{int f2,any f3}","{{null f1} f1}"); }
	@Test public void test_2911() { checkNotSubtype("{int f2,any f3}","{{null f2} f1}"); }
	@Test public void test_2912() { checkNotSubtype("{int f2,any f3}","{{null f1} f2}"); }
	@Test public void test_2913() { checkNotSubtype("{int f2,any f3}","{{null f2} f2}"); }
	@Test public void test_2914() { checkNotSubtype("{int f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_2915() { checkNotSubtype("{int f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_2916() { checkNotSubtype("{int f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_2917() { checkNotSubtype("{int f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_2918() { checkNotSubtype("{int f2,any f3}","{{int f1} f1}"); }
	@Test public void test_2919() { checkNotSubtype("{int f2,any f3}","{{int f2} f1}"); }
	@Test public void test_2920() { checkNotSubtype("{int f2,any f3}","{{int f1} f2}"); }
	@Test public void test_2921() { checkNotSubtype("{int f2,any f3}","{{int f2} f2}"); }
	@Test public void test_2922() { checkNotSubtype("{int f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_2923() { checkNotSubtype("{int f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_2924() { checkNotSubtype("{int f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_2925() { checkNotSubtype("{int f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_2926() { checkNotSubtype("{int f1,null f2}","any"); }
	@Test public void test_2927() { checkNotSubtype("{int f1,null f2}","null"); }
	@Test public void test_2928() { checkNotSubtype("{int f1,null f2}","int"); }
	@Test public void test_2929() { checkIsSubtype("{int f1,null f2}","{void f1}"); }
	@Test public void test_2930() { checkIsSubtype("{int f1,null f2}","{void f2}"); }
	@Test public void test_2931() { checkNotSubtype("{int f1,null f2}","{any f1}"); }
	@Test public void test_2932() { checkNotSubtype("{int f1,null f2}","{any f2}"); }
	@Test public void test_2933() { checkNotSubtype("{int f1,null f2}","{null f1}"); }
	@Test public void test_2934() { checkNotSubtype("{int f1,null f2}","{null f2}"); }
	@Test public void test_2935() { checkNotSubtype("{int f1,null f2}","{int f1}"); }
	@Test public void test_2936() { checkNotSubtype("{int f1,null f2}","{int f2}"); }
	@Test public void test_2937() { checkIsSubtype("{int f1,null f2}","{void f1,void f2}"); }
	@Test public void test_2938() { checkIsSubtype("{int f1,null f2}","{void f2,void f3}"); }
	@Test public void test_2939() { checkIsSubtype("{int f1,null f2}","{void f1,any f2}"); }
	@Test public void test_2940() { checkIsSubtype("{int f1,null f2}","{void f2,any f3}"); }
	@Test public void test_2941() { checkIsSubtype("{int f1,null f2}","{void f1,null f2}"); }
	@Test public void test_2942() { checkIsSubtype("{int f1,null f2}","{void f2,null f3}"); }
	@Test public void test_2943() { checkIsSubtype("{int f1,null f2}","{void f1,int f2}"); }
	@Test public void test_2944() { checkIsSubtype("{int f1,null f2}","{void f2,int f3}"); }
	@Test public void test_2945() { checkIsSubtype("{int f1,null f2}","{any f1,void f2}"); }
	@Test public void test_2946() { checkIsSubtype("{int f1,null f2}","{any f2,void f3}"); }
	@Test public void test_2947() { checkNotSubtype("{int f1,null f2}","{any f1,any f2}"); }
	@Test public void test_2948() { checkNotSubtype("{int f1,null f2}","{any f2,any f3}"); }
	@Test public void test_2949() { checkNotSubtype("{int f1,null f2}","{any f1,null f2}"); }
	@Test public void test_2950() { checkNotSubtype("{int f1,null f2}","{any f2,null f3}"); }
	@Test public void test_2951() { checkNotSubtype("{int f1,null f2}","{any f1,int f2}"); }
	@Test public void test_2952() { checkNotSubtype("{int f1,null f2}","{any f2,int f3}"); }
	@Test public void test_2953() { checkIsSubtype("{int f1,null f2}","{null f1,void f2}"); }
	@Test public void test_2954() { checkIsSubtype("{int f1,null f2}","{null f2,void f3}"); }
	@Test public void test_2955() { checkNotSubtype("{int f1,null f2}","{null f1,any f2}"); }
	@Test public void test_2956() { checkNotSubtype("{int f1,null f2}","{null f2,any f3}"); }
	@Test public void test_2957() { checkNotSubtype("{int f1,null f2}","{null f1,null f2}"); }
	@Test public void test_2958() { checkNotSubtype("{int f1,null f2}","{null f2,null f3}"); }
	@Test public void test_2959() { checkNotSubtype("{int f1,null f2}","{null f1,int f2}"); }
	@Test public void test_2960() { checkNotSubtype("{int f1,null f2}","{null f2,int f3}"); }
	@Test public void test_2961() { checkIsSubtype("{int f1,null f2}","{int f1,void f2}"); }
	@Test public void test_2962() { checkIsSubtype("{int f1,null f2}","{int f2,void f3}"); }
	@Test public void test_2963() { checkNotSubtype("{int f1,null f2}","{int f1,any f2}"); }
	@Test public void test_2964() { checkNotSubtype("{int f1,null f2}","{int f2,any f3}"); }
	@Test public void test_2965() { checkIsSubtype("{int f1,null f2}","{int f1,null f2}"); }
	@Test public void test_2966() { checkNotSubtype("{int f1,null f2}","{int f2,null f3}"); }
	@Test public void test_2967() { checkNotSubtype("{int f1,null f2}","{int f1,int f2}"); }
	@Test public void test_2968() { checkNotSubtype("{int f1,null f2}","{int f2,int f3}"); }
	@Test public void test_2969() { checkIsSubtype("{int f1,null f2}","{{void f1} f1}"); }
	@Test public void test_2970() { checkIsSubtype("{int f1,null f2}","{{void f2} f1}"); }
	@Test public void test_2971() { checkIsSubtype("{int f1,null f2}","{{void f1} f2}"); }
	@Test public void test_2972() { checkIsSubtype("{int f1,null f2}","{{void f2} f2}"); }
	@Test public void test_2973() { checkIsSubtype("{int f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_2974() { checkIsSubtype("{int f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_2975() { checkIsSubtype("{int f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_2976() { checkIsSubtype("{int f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_2977() { checkNotSubtype("{int f1,null f2}","{{any f1} f1}"); }
	@Test public void test_2978() { checkNotSubtype("{int f1,null f2}","{{any f2} f1}"); }
	@Test public void test_2979() { checkNotSubtype("{int f1,null f2}","{{any f1} f2}"); }
	@Test public void test_2980() { checkNotSubtype("{int f1,null f2}","{{any f2} f2}"); }
	@Test public void test_2981() { checkNotSubtype("{int f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_2982() { checkNotSubtype("{int f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_2983() { checkNotSubtype("{int f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_2984() { checkNotSubtype("{int f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_2985() { checkNotSubtype("{int f1,null f2}","{{null f1} f1}"); }
	@Test public void test_2986() { checkNotSubtype("{int f1,null f2}","{{null f2} f1}"); }
	@Test public void test_2987() { checkNotSubtype("{int f1,null f2}","{{null f1} f2}"); }
	@Test public void test_2988() { checkNotSubtype("{int f1,null f2}","{{null f2} f2}"); }
	@Test public void test_2989() { checkNotSubtype("{int f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_2990() { checkNotSubtype("{int f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_2991() { checkNotSubtype("{int f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_2992() { checkNotSubtype("{int f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_2993() { checkNotSubtype("{int f1,null f2}","{{int f1} f1}"); }
	@Test public void test_2994() { checkNotSubtype("{int f1,null f2}","{{int f2} f1}"); }
	@Test public void test_2995() { checkNotSubtype("{int f1,null f2}","{{int f1} f2}"); }
	@Test public void test_2996() { checkNotSubtype("{int f1,null f2}","{{int f2} f2}"); }
	@Test public void test_2997() { checkNotSubtype("{int f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_2998() { checkNotSubtype("{int f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_2999() { checkNotSubtype("{int f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3000() { checkNotSubtype("{int f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3001() { checkNotSubtype("{int f2,null f3}","any"); }
	@Test public void test_3002() { checkNotSubtype("{int f2,null f3}","null"); }
	@Test public void test_3003() { checkNotSubtype("{int f2,null f3}","int"); }
	@Test public void test_3004() { checkIsSubtype("{int f2,null f3}","{void f1}"); }
	@Test public void test_3005() { checkIsSubtype("{int f2,null f3}","{void f2}"); }
	@Test public void test_3006() { checkNotSubtype("{int f2,null f3}","{any f1}"); }
	@Test public void test_3007() { checkNotSubtype("{int f2,null f3}","{any f2}"); }
	@Test public void test_3008() { checkNotSubtype("{int f2,null f3}","{null f1}"); }
	@Test public void test_3009() { checkNotSubtype("{int f2,null f3}","{null f2}"); }
	@Test public void test_3010() { checkNotSubtype("{int f2,null f3}","{int f1}"); }
	@Test public void test_3011() { checkNotSubtype("{int f2,null f3}","{int f2}"); }
	@Test public void test_3012() { checkIsSubtype("{int f2,null f3}","{void f1,void f2}"); }
	@Test public void test_3013() { checkIsSubtype("{int f2,null f3}","{void f2,void f3}"); }
	@Test public void test_3014() { checkIsSubtype("{int f2,null f3}","{void f1,any f2}"); }
	@Test public void test_3015() { checkIsSubtype("{int f2,null f3}","{void f2,any f3}"); }
	@Test public void test_3016() { checkIsSubtype("{int f2,null f3}","{void f1,null f2}"); }
	@Test public void test_3017() { checkIsSubtype("{int f2,null f3}","{void f2,null f3}"); }
	@Test public void test_3018() { checkIsSubtype("{int f2,null f3}","{void f1,int f2}"); }
	@Test public void test_3019() { checkIsSubtype("{int f2,null f3}","{void f2,int f3}"); }
	@Test public void test_3020() { checkIsSubtype("{int f2,null f3}","{any f1,void f2}"); }
	@Test public void test_3021() { checkIsSubtype("{int f2,null f3}","{any f2,void f3}"); }
	@Test public void test_3022() { checkNotSubtype("{int f2,null f3}","{any f1,any f2}"); }
	@Test public void test_3023() { checkNotSubtype("{int f2,null f3}","{any f2,any f3}"); }
	@Test public void test_3024() { checkNotSubtype("{int f2,null f3}","{any f1,null f2}"); }
	@Test public void test_3025() { checkNotSubtype("{int f2,null f3}","{any f2,null f3}"); }
	@Test public void test_3026() { checkNotSubtype("{int f2,null f3}","{any f1,int f2}"); }
	@Test public void test_3027() { checkNotSubtype("{int f2,null f3}","{any f2,int f3}"); }
	@Test public void test_3028() { checkIsSubtype("{int f2,null f3}","{null f1,void f2}"); }
	@Test public void test_3029() { checkIsSubtype("{int f2,null f3}","{null f2,void f3}"); }
	@Test public void test_3030() { checkNotSubtype("{int f2,null f3}","{null f1,any f2}"); }
	@Test public void test_3031() { checkNotSubtype("{int f2,null f3}","{null f2,any f3}"); }
	@Test public void test_3032() { checkNotSubtype("{int f2,null f3}","{null f1,null f2}"); }
	@Test public void test_3033() { checkNotSubtype("{int f2,null f3}","{null f2,null f3}"); }
	@Test public void test_3034() { checkNotSubtype("{int f2,null f3}","{null f1,int f2}"); }
	@Test public void test_3035() { checkNotSubtype("{int f2,null f3}","{null f2,int f3}"); }
	@Test public void test_3036() { checkIsSubtype("{int f2,null f3}","{int f1,void f2}"); }
	@Test public void test_3037() { checkIsSubtype("{int f2,null f3}","{int f2,void f3}"); }
	@Test public void test_3038() { checkNotSubtype("{int f2,null f3}","{int f1,any f2}"); }
	@Test public void test_3039() { checkNotSubtype("{int f2,null f3}","{int f2,any f3}"); }
	@Test public void test_3040() { checkNotSubtype("{int f2,null f3}","{int f1,null f2}"); }
	@Test public void test_3041() { checkIsSubtype("{int f2,null f3}","{int f2,null f3}"); }
	@Test public void test_3042() { checkNotSubtype("{int f2,null f3}","{int f1,int f2}"); }
	@Test public void test_3043() { checkNotSubtype("{int f2,null f3}","{int f2,int f3}"); }
	@Test public void test_3044() { checkIsSubtype("{int f2,null f3}","{{void f1} f1}"); }
	@Test public void test_3045() { checkIsSubtype("{int f2,null f3}","{{void f2} f1}"); }
	@Test public void test_3046() { checkIsSubtype("{int f2,null f3}","{{void f1} f2}"); }
	@Test public void test_3047() { checkIsSubtype("{int f2,null f3}","{{void f2} f2}"); }
	@Test public void test_3048() { checkIsSubtype("{int f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_3049() { checkIsSubtype("{int f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_3050() { checkIsSubtype("{int f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_3051() { checkIsSubtype("{int f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_3052() { checkNotSubtype("{int f2,null f3}","{{any f1} f1}"); }
	@Test public void test_3053() { checkNotSubtype("{int f2,null f3}","{{any f2} f1}"); }
	@Test public void test_3054() { checkNotSubtype("{int f2,null f3}","{{any f1} f2}"); }
	@Test public void test_3055() { checkNotSubtype("{int f2,null f3}","{{any f2} f2}"); }
	@Test public void test_3056() { checkNotSubtype("{int f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_3057() { checkNotSubtype("{int f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_3058() { checkNotSubtype("{int f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_3059() { checkNotSubtype("{int f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_3060() { checkNotSubtype("{int f2,null f3}","{{null f1} f1}"); }
	@Test public void test_3061() { checkNotSubtype("{int f2,null f3}","{{null f2} f1}"); }
	@Test public void test_3062() { checkNotSubtype("{int f2,null f3}","{{null f1} f2}"); }
	@Test public void test_3063() { checkNotSubtype("{int f2,null f3}","{{null f2} f2}"); }
	@Test public void test_3064() { checkNotSubtype("{int f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3065() { checkNotSubtype("{int f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3066() { checkNotSubtype("{int f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3067() { checkNotSubtype("{int f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3068() { checkNotSubtype("{int f2,null f3}","{{int f1} f1}"); }
	@Test public void test_3069() { checkNotSubtype("{int f2,null f3}","{{int f2} f1}"); }
	@Test public void test_3070() { checkNotSubtype("{int f2,null f3}","{{int f1} f2}"); }
	@Test public void test_3071() { checkNotSubtype("{int f2,null f3}","{{int f2} f2}"); }
	@Test public void test_3072() { checkNotSubtype("{int f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3073() { checkNotSubtype("{int f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3074() { checkNotSubtype("{int f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3075() { checkNotSubtype("{int f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3076() { checkNotSubtype("{int f1,int f2}","any"); }
	@Test public void test_3077() { checkNotSubtype("{int f1,int f2}","null"); }
	@Test public void test_3078() { checkNotSubtype("{int f1,int f2}","int"); }
	@Test public void test_3079() { checkIsSubtype("{int f1,int f2}","{void f1}"); }
	@Test public void test_3080() { checkIsSubtype("{int f1,int f2}","{void f2}"); }
	@Test public void test_3081() { checkNotSubtype("{int f1,int f2}","{any f1}"); }
	@Test public void test_3082() { checkNotSubtype("{int f1,int f2}","{any f2}"); }
	@Test public void test_3083() { checkNotSubtype("{int f1,int f2}","{null f1}"); }
	@Test public void test_3084() { checkNotSubtype("{int f1,int f2}","{null f2}"); }
	@Test public void test_3085() { checkNotSubtype("{int f1,int f2}","{int f1}"); }
	@Test public void test_3086() { checkNotSubtype("{int f1,int f2}","{int f2}"); }
	@Test public void test_3087() { checkIsSubtype("{int f1,int f2}","{void f1,void f2}"); }
	@Test public void test_3088() { checkIsSubtype("{int f1,int f2}","{void f2,void f3}"); }
	@Test public void test_3089() { checkIsSubtype("{int f1,int f2}","{void f1,any f2}"); }
	@Test public void test_3090() { checkIsSubtype("{int f1,int f2}","{void f2,any f3}"); }
	@Test public void test_3091() { checkIsSubtype("{int f1,int f2}","{void f1,null f2}"); }
	@Test public void test_3092() { checkIsSubtype("{int f1,int f2}","{void f2,null f3}"); }
	@Test public void test_3093() { checkIsSubtype("{int f1,int f2}","{void f1,int f2}"); }
	@Test public void test_3094() { checkIsSubtype("{int f1,int f2}","{void f2,int f3}"); }
	@Test public void test_3095() { checkIsSubtype("{int f1,int f2}","{any f1,void f2}"); }
	@Test public void test_3096() { checkIsSubtype("{int f1,int f2}","{any f2,void f3}"); }
	@Test public void test_3097() { checkNotSubtype("{int f1,int f2}","{any f1,any f2}"); }
	@Test public void test_3098() { checkNotSubtype("{int f1,int f2}","{any f2,any f3}"); }
	@Test public void test_3099() { checkNotSubtype("{int f1,int f2}","{any f1,null f2}"); }
	@Test public void test_3100() { checkNotSubtype("{int f1,int f2}","{any f2,null f3}"); }
	@Test public void test_3101() { checkNotSubtype("{int f1,int f2}","{any f1,int f2}"); }
	@Test public void test_3102() { checkNotSubtype("{int f1,int f2}","{any f2,int f3}"); }
	@Test public void test_3103() { checkIsSubtype("{int f1,int f2}","{null f1,void f2}"); }
	@Test public void test_3104() { checkIsSubtype("{int f1,int f2}","{null f2,void f3}"); }
	@Test public void test_3105() { checkNotSubtype("{int f1,int f2}","{null f1,any f2}"); }
	@Test public void test_3106() { checkNotSubtype("{int f1,int f2}","{null f2,any f3}"); }
	@Test public void test_3107() { checkNotSubtype("{int f1,int f2}","{null f1,null f2}"); }
	@Test public void test_3108() { checkNotSubtype("{int f1,int f2}","{null f2,null f3}"); }
	@Test public void test_3109() { checkNotSubtype("{int f1,int f2}","{null f1,int f2}"); }
	@Test public void test_3110() { checkNotSubtype("{int f1,int f2}","{null f2,int f3}"); }
	@Test public void test_3111() { checkIsSubtype("{int f1,int f2}","{int f1,void f2}"); }
	@Test public void test_3112() { checkIsSubtype("{int f1,int f2}","{int f2,void f3}"); }
	@Test public void test_3113() { checkNotSubtype("{int f1,int f2}","{int f1,any f2}"); }
	@Test public void test_3114() { checkNotSubtype("{int f1,int f2}","{int f2,any f3}"); }
	@Test public void test_3115() { checkNotSubtype("{int f1,int f2}","{int f1,null f2}"); }
	@Test public void test_3116() { checkNotSubtype("{int f1,int f2}","{int f2,null f3}"); }
	@Test public void test_3117() { checkIsSubtype("{int f1,int f2}","{int f1,int f2}"); }
	@Test public void test_3118() { checkNotSubtype("{int f1,int f2}","{int f2,int f3}"); }
	@Test public void test_3119() { checkIsSubtype("{int f1,int f2}","{{void f1} f1}"); }
	@Test public void test_3120() { checkIsSubtype("{int f1,int f2}","{{void f2} f1}"); }
	@Test public void test_3121() { checkIsSubtype("{int f1,int f2}","{{void f1} f2}"); }
	@Test public void test_3122() { checkIsSubtype("{int f1,int f2}","{{void f2} f2}"); }
	@Test public void test_3123() { checkIsSubtype("{int f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_3124() { checkIsSubtype("{int f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_3125() { checkIsSubtype("{int f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_3126() { checkIsSubtype("{int f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_3127() { checkNotSubtype("{int f1,int f2}","{{any f1} f1}"); }
	@Test public void test_3128() { checkNotSubtype("{int f1,int f2}","{{any f2} f1}"); }
	@Test public void test_3129() { checkNotSubtype("{int f1,int f2}","{{any f1} f2}"); }
	@Test public void test_3130() { checkNotSubtype("{int f1,int f2}","{{any f2} f2}"); }
	@Test public void test_3131() { checkNotSubtype("{int f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_3132() { checkNotSubtype("{int f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_3133() { checkNotSubtype("{int f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_3134() { checkNotSubtype("{int f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_3135() { checkNotSubtype("{int f1,int f2}","{{null f1} f1}"); }
	@Test public void test_3136() { checkNotSubtype("{int f1,int f2}","{{null f2} f1}"); }
	@Test public void test_3137() { checkNotSubtype("{int f1,int f2}","{{null f1} f2}"); }
	@Test public void test_3138() { checkNotSubtype("{int f1,int f2}","{{null f2} f2}"); }
	@Test public void test_3139() { checkNotSubtype("{int f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3140() { checkNotSubtype("{int f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3141() { checkNotSubtype("{int f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3142() { checkNotSubtype("{int f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3143() { checkNotSubtype("{int f1,int f2}","{{int f1} f1}"); }
	@Test public void test_3144() { checkNotSubtype("{int f1,int f2}","{{int f2} f1}"); }
	@Test public void test_3145() { checkNotSubtype("{int f1,int f2}","{{int f1} f2}"); }
	@Test public void test_3146() { checkNotSubtype("{int f1,int f2}","{{int f2} f2}"); }
	@Test public void test_3147() { checkNotSubtype("{int f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3148() { checkNotSubtype("{int f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3149() { checkNotSubtype("{int f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3150() { checkNotSubtype("{int f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3151() { checkNotSubtype("{int f2,int f3}","any"); }
	@Test public void test_3152() { checkNotSubtype("{int f2,int f3}","null"); }
	@Test public void test_3153() { checkNotSubtype("{int f2,int f3}","int"); }
	@Test public void test_3154() { checkIsSubtype("{int f2,int f3}","{void f1}"); }
	@Test public void test_3155() { checkIsSubtype("{int f2,int f3}","{void f2}"); }
	@Test public void test_3156() { checkNotSubtype("{int f2,int f3}","{any f1}"); }
	@Test public void test_3157() { checkNotSubtype("{int f2,int f3}","{any f2}"); }
	@Test public void test_3158() { checkNotSubtype("{int f2,int f3}","{null f1}"); }
	@Test public void test_3159() { checkNotSubtype("{int f2,int f3}","{null f2}"); }
	@Test public void test_3160() { checkNotSubtype("{int f2,int f3}","{int f1}"); }
	@Test public void test_3161() { checkNotSubtype("{int f2,int f3}","{int f2}"); }
	@Test public void test_3162() { checkIsSubtype("{int f2,int f3}","{void f1,void f2}"); }
	@Test public void test_3163() { checkIsSubtype("{int f2,int f3}","{void f2,void f3}"); }
	@Test public void test_3164() { checkIsSubtype("{int f2,int f3}","{void f1,any f2}"); }
	@Test public void test_3165() { checkIsSubtype("{int f2,int f3}","{void f2,any f3}"); }
	@Test public void test_3166() { checkIsSubtype("{int f2,int f3}","{void f1,null f2}"); }
	@Test public void test_3167() { checkIsSubtype("{int f2,int f3}","{void f2,null f3}"); }
	@Test public void test_3168() { checkIsSubtype("{int f2,int f3}","{void f1,int f2}"); }
	@Test public void test_3169() { checkIsSubtype("{int f2,int f3}","{void f2,int f3}"); }
	@Test public void test_3170() { checkIsSubtype("{int f2,int f3}","{any f1,void f2}"); }
	@Test public void test_3171() { checkIsSubtype("{int f2,int f3}","{any f2,void f3}"); }
	@Test public void test_3172() { checkNotSubtype("{int f2,int f3}","{any f1,any f2}"); }
	@Test public void test_3173() { checkNotSubtype("{int f2,int f3}","{any f2,any f3}"); }
	@Test public void test_3174() { checkNotSubtype("{int f2,int f3}","{any f1,null f2}"); }
	@Test public void test_3175() { checkNotSubtype("{int f2,int f3}","{any f2,null f3}"); }
	@Test public void test_3176() { checkNotSubtype("{int f2,int f3}","{any f1,int f2}"); }
	@Test public void test_3177() { checkNotSubtype("{int f2,int f3}","{any f2,int f3}"); }
	@Test public void test_3178() { checkIsSubtype("{int f2,int f3}","{null f1,void f2}"); }
	@Test public void test_3179() { checkIsSubtype("{int f2,int f3}","{null f2,void f3}"); }
	@Test public void test_3180() { checkNotSubtype("{int f2,int f3}","{null f1,any f2}"); }
	@Test public void test_3181() { checkNotSubtype("{int f2,int f3}","{null f2,any f3}"); }
	@Test public void test_3182() { checkNotSubtype("{int f2,int f3}","{null f1,null f2}"); }
	@Test public void test_3183() { checkNotSubtype("{int f2,int f3}","{null f2,null f3}"); }
	@Test public void test_3184() { checkNotSubtype("{int f2,int f3}","{null f1,int f2}"); }
	@Test public void test_3185() { checkNotSubtype("{int f2,int f3}","{null f2,int f3}"); }
	@Test public void test_3186() { checkIsSubtype("{int f2,int f3}","{int f1,void f2}"); }
	@Test public void test_3187() { checkIsSubtype("{int f2,int f3}","{int f2,void f3}"); }
	@Test public void test_3188() { checkNotSubtype("{int f2,int f3}","{int f1,any f2}"); }
	@Test public void test_3189() { checkNotSubtype("{int f2,int f3}","{int f2,any f3}"); }
	@Test public void test_3190() { checkNotSubtype("{int f2,int f3}","{int f1,null f2}"); }
	@Test public void test_3191() { checkNotSubtype("{int f2,int f3}","{int f2,null f3}"); }
	@Test public void test_3192() { checkNotSubtype("{int f2,int f3}","{int f1,int f2}"); }
	@Test public void test_3193() { checkIsSubtype("{int f2,int f3}","{int f2,int f3}"); }
	@Test public void test_3194() { checkIsSubtype("{int f2,int f3}","{{void f1} f1}"); }
	@Test public void test_3195() { checkIsSubtype("{int f2,int f3}","{{void f2} f1}"); }
	@Test public void test_3196() { checkIsSubtype("{int f2,int f3}","{{void f1} f2}"); }
	@Test public void test_3197() { checkIsSubtype("{int f2,int f3}","{{void f2} f2}"); }
	@Test public void test_3198() { checkIsSubtype("{int f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_3199() { checkIsSubtype("{int f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_3200() { checkIsSubtype("{int f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_3201() { checkIsSubtype("{int f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_3202() { checkNotSubtype("{int f2,int f3}","{{any f1} f1}"); }
	@Test public void test_3203() { checkNotSubtype("{int f2,int f3}","{{any f2} f1}"); }
	@Test public void test_3204() { checkNotSubtype("{int f2,int f3}","{{any f1} f2}"); }
	@Test public void test_3205() { checkNotSubtype("{int f2,int f3}","{{any f2} f2}"); }
	@Test public void test_3206() { checkNotSubtype("{int f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_3207() { checkNotSubtype("{int f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_3208() { checkNotSubtype("{int f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_3209() { checkNotSubtype("{int f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_3210() { checkNotSubtype("{int f2,int f3}","{{null f1} f1}"); }
	@Test public void test_3211() { checkNotSubtype("{int f2,int f3}","{{null f2} f1}"); }
	@Test public void test_3212() { checkNotSubtype("{int f2,int f3}","{{null f1} f2}"); }
	@Test public void test_3213() { checkNotSubtype("{int f2,int f3}","{{null f2} f2}"); }
	@Test public void test_3214() { checkNotSubtype("{int f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3215() { checkNotSubtype("{int f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3216() { checkNotSubtype("{int f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3217() { checkNotSubtype("{int f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3218() { checkNotSubtype("{int f2,int f3}","{{int f1} f1}"); }
	@Test public void test_3219() { checkNotSubtype("{int f2,int f3}","{{int f2} f1}"); }
	@Test public void test_3220() { checkNotSubtype("{int f2,int f3}","{{int f1} f2}"); }
	@Test public void test_3221() { checkNotSubtype("{int f2,int f3}","{{int f2} f2}"); }
	@Test public void test_3222() { checkNotSubtype("{int f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3223() { checkNotSubtype("{int f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3224() { checkNotSubtype("{int f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3225() { checkNotSubtype("{int f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3226() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_3227() { checkNotSubtype("{{void f1} f1}","null"); }
	@Test public void test_3228() { checkNotSubtype("{{void f1} f1}","int"); }
	@Test public void test_3229() { checkIsSubtype("{{void f1} f1}","{void f1}"); }
	@Test public void test_3230() { checkIsSubtype("{{void f1} f1}","{void f2}"); }
	@Test public void test_3231() { checkNotSubtype("{{void f1} f1}","{any f1}"); }
	@Test public void test_3232() { checkNotSubtype("{{void f1} f1}","{any f2}"); }
	@Test public void test_3233() { checkNotSubtype("{{void f1} f1}","{null f1}"); }
	@Test public void test_3234() { checkNotSubtype("{{void f1} f1}","{null f2}"); }
	@Test public void test_3235() { checkNotSubtype("{{void f1} f1}","{int f1}"); }
	@Test public void test_3236() { checkNotSubtype("{{void f1} f1}","{int f2}"); }
	@Test public void test_3237() { checkIsSubtype("{{void f1} f1}","{void f1,void f2}"); }
	@Test public void test_3238() { checkIsSubtype("{{void f1} f1}","{void f2,void f3}"); }
	@Test public void test_3239() { checkIsSubtype("{{void f1} f1}","{void f1,any f2}"); }
	@Test public void test_3240() { checkIsSubtype("{{void f1} f1}","{void f2,any f3}"); }
	@Test public void test_3241() { checkIsSubtype("{{void f1} f1}","{void f1,null f2}"); }
	@Test public void test_3242() { checkIsSubtype("{{void f1} f1}","{void f2,null f3}"); }
	@Test public void test_3243() { checkIsSubtype("{{void f1} f1}","{void f1,int f2}"); }
	@Test public void test_3244() { checkIsSubtype("{{void f1} f1}","{void f2,int f3}"); }
	@Test public void test_3245() { checkIsSubtype("{{void f1} f1}","{any f1,void f2}"); }
	@Test public void test_3246() { checkIsSubtype("{{void f1} f1}","{any f2,void f3}"); }
	@Test public void test_3247() { checkNotSubtype("{{void f1} f1}","{any f1,any f2}"); }
	@Test public void test_3248() { checkNotSubtype("{{void f1} f1}","{any f2,any f3}"); }
	@Test public void test_3249() { checkNotSubtype("{{void f1} f1}","{any f1,null f2}"); }
	@Test public void test_3250() { checkNotSubtype("{{void f1} f1}","{any f2,null f3}"); }
	@Test public void test_3251() { checkNotSubtype("{{void f1} f1}","{any f1,int f2}"); }
	@Test public void test_3252() { checkNotSubtype("{{void f1} f1}","{any f2,int f3}"); }
	@Test public void test_3253() { checkIsSubtype("{{void f1} f1}","{null f1,void f2}"); }
	@Test public void test_3254() { checkIsSubtype("{{void f1} f1}","{null f2,void f3}"); }
	@Test public void test_3255() { checkNotSubtype("{{void f1} f1}","{null f1,any f2}"); }
	@Test public void test_3256() { checkNotSubtype("{{void f1} f1}","{null f2,any f3}"); }
	@Test public void test_3257() { checkNotSubtype("{{void f1} f1}","{null f1,null f2}"); }
	@Test public void test_3258() { checkNotSubtype("{{void f1} f1}","{null f2,null f3}"); }
	@Test public void test_3259() { checkNotSubtype("{{void f1} f1}","{null f1,int f2}"); }
	@Test public void test_3260() { checkNotSubtype("{{void f1} f1}","{null f2,int f3}"); }
	@Test public void test_3261() { checkIsSubtype("{{void f1} f1}","{int f1,void f2}"); }
	@Test public void test_3262() { checkIsSubtype("{{void f1} f1}","{int f2,void f3}"); }
	@Test public void test_3263() { checkNotSubtype("{{void f1} f1}","{int f1,any f2}"); }
	@Test public void test_3264() { checkNotSubtype("{{void f1} f1}","{int f2,any f3}"); }
	@Test public void test_3265() { checkNotSubtype("{{void f1} f1}","{int f1,null f2}"); }
	@Test public void test_3266() { checkNotSubtype("{{void f1} f1}","{int f2,null f3}"); }
	@Test public void test_3267() { checkNotSubtype("{{void f1} f1}","{int f1,int f2}"); }
	@Test public void test_3268() { checkNotSubtype("{{void f1} f1}","{int f2,int f3}"); }
	@Test public void test_3269() { checkIsSubtype("{{void f1} f1}","{{void f1} f1}"); }
	@Test public void test_3270() { checkIsSubtype("{{void f1} f1}","{{void f2} f1}"); }
	@Test public void test_3271() { checkIsSubtype("{{void f1} f1}","{{void f1} f2}"); }
	@Test public void test_3272() { checkIsSubtype("{{void f1} f1}","{{void f2} f2}"); }
	@Test public void test_3273() { checkIsSubtype("{{void f1} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_3274() { checkIsSubtype("{{void f1} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_3275() { checkIsSubtype("{{void f1} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_3276() { checkIsSubtype("{{void f1} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_3277() { checkNotSubtype("{{void f1} f1}","{{any f1} f1}"); }
	@Test public void test_3278() { checkNotSubtype("{{void f1} f1}","{{any f2} f1}"); }
	@Test public void test_3279() { checkNotSubtype("{{void f1} f1}","{{any f1} f2}"); }
	@Test public void test_3280() { checkNotSubtype("{{void f1} f1}","{{any f2} f2}"); }
	@Test public void test_3281() { checkNotSubtype("{{void f1} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_3282() { checkNotSubtype("{{void f1} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_3283() { checkNotSubtype("{{void f1} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_3284() { checkNotSubtype("{{void f1} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_3285() { checkNotSubtype("{{void f1} f1}","{{null f1} f1}"); }
	@Test public void test_3286() { checkNotSubtype("{{void f1} f1}","{{null f2} f1}"); }
	@Test public void test_3287() { checkNotSubtype("{{void f1} f1}","{{null f1} f2}"); }
	@Test public void test_3288() { checkNotSubtype("{{void f1} f1}","{{null f2} f2}"); }
	@Test public void test_3289() { checkNotSubtype("{{void f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_3290() { checkNotSubtype("{{void f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_3291() { checkNotSubtype("{{void f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_3292() { checkNotSubtype("{{void f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_3293() { checkNotSubtype("{{void f1} f1}","{{int f1} f1}"); }
	@Test public void test_3294() { checkNotSubtype("{{void f1} f1}","{{int f2} f1}"); }
	@Test public void test_3295() { checkNotSubtype("{{void f1} f1}","{{int f1} f2}"); }
	@Test public void test_3296() { checkNotSubtype("{{void f1} f1}","{{int f2} f2}"); }
	@Test public void test_3297() { checkNotSubtype("{{void f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_3298() { checkNotSubtype("{{void f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_3299() { checkNotSubtype("{{void f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_3300() { checkNotSubtype("{{void f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_3301() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_3302() { checkNotSubtype("{{void f2} f1}","null"); }
	@Test public void test_3303() { checkNotSubtype("{{void f2} f1}","int"); }
	@Test public void test_3304() { checkIsSubtype("{{void f2} f1}","{void f1}"); }
	@Test public void test_3305() { checkIsSubtype("{{void f2} f1}","{void f2}"); }
	@Test public void test_3306() { checkNotSubtype("{{void f2} f1}","{any f1}"); }
	@Test public void test_3307() { checkNotSubtype("{{void f2} f1}","{any f2}"); }
	@Test public void test_3308() { checkNotSubtype("{{void f2} f1}","{null f1}"); }
	@Test public void test_3309() { checkNotSubtype("{{void f2} f1}","{null f2}"); }
	@Test public void test_3310() { checkNotSubtype("{{void f2} f1}","{int f1}"); }
	@Test public void test_3311() { checkNotSubtype("{{void f2} f1}","{int f2}"); }
	@Test public void test_3312() { checkIsSubtype("{{void f2} f1}","{void f1,void f2}"); }
	@Test public void test_3313() { checkIsSubtype("{{void f2} f1}","{void f2,void f3}"); }
	@Test public void test_3314() { checkIsSubtype("{{void f2} f1}","{void f1,any f2}"); }
	@Test public void test_3315() { checkIsSubtype("{{void f2} f1}","{void f2,any f3}"); }
	@Test public void test_3316() { checkIsSubtype("{{void f2} f1}","{void f1,null f2}"); }
	@Test public void test_3317() { checkIsSubtype("{{void f2} f1}","{void f2,null f3}"); }
	@Test public void test_3318() { checkIsSubtype("{{void f2} f1}","{void f1,int f2}"); }
	@Test public void test_3319() { checkIsSubtype("{{void f2} f1}","{void f2,int f3}"); }
	@Test public void test_3320() { checkIsSubtype("{{void f2} f1}","{any f1,void f2}"); }
	@Test public void test_3321() { checkIsSubtype("{{void f2} f1}","{any f2,void f3}"); }
	@Test public void test_3322() { checkNotSubtype("{{void f2} f1}","{any f1,any f2}"); }
	@Test public void test_3323() { checkNotSubtype("{{void f2} f1}","{any f2,any f3}"); }
	@Test public void test_3324() { checkNotSubtype("{{void f2} f1}","{any f1,null f2}"); }
	@Test public void test_3325() { checkNotSubtype("{{void f2} f1}","{any f2,null f3}"); }
	@Test public void test_3326() { checkNotSubtype("{{void f2} f1}","{any f1,int f2}"); }
	@Test public void test_3327() { checkNotSubtype("{{void f2} f1}","{any f2,int f3}"); }
	@Test public void test_3328() { checkIsSubtype("{{void f2} f1}","{null f1,void f2}"); }
	@Test public void test_3329() { checkIsSubtype("{{void f2} f1}","{null f2,void f3}"); }
	@Test public void test_3330() { checkNotSubtype("{{void f2} f1}","{null f1,any f2}"); }
	@Test public void test_3331() { checkNotSubtype("{{void f2} f1}","{null f2,any f3}"); }
	@Test public void test_3332() { checkNotSubtype("{{void f2} f1}","{null f1,null f2}"); }
	@Test public void test_3333() { checkNotSubtype("{{void f2} f1}","{null f2,null f3}"); }
	@Test public void test_3334() { checkNotSubtype("{{void f2} f1}","{null f1,int f2}"); }
	@Test public void test_3335() { checkNotSubtype("{{void f2} f1}","{null f2,int f3}"); }
	@Test public void test_3336() { checkIsSubtype("{{void f2} f1}","{int f1,void f2}"); }
	@Test public void test_3337() { checkIsSubtype("{{void f2} f1}","{int f2,void f3}"); }
	@Test public void test_3338() { checkNotSubtype("{{void f2} f1}","{int f1,any f2}"); }
	@Test public void test_3339() { checkNotSubtype("{{void f2} f1}","{int f2,any f3}"); }
	@Test public void test_3340() { checkNotSubtype("{{void f2} f1}","{int f1,null f2}"); }
	@Test public void test_3341() { checkNotSubtype("{{void f2} f1}","{int f2,null f3}"); }
	@Test public void test_3342() { checkNotSubtype("{{void f2} f1}","{int f1,int f2}"); }
	@Test public void test_3343() { checkNotSubtype("{{void f2} f1}","{int f2,int f3}"); }
	@Test public void test_3344() { checkIsSubtype("{{void f2} f1}","{{void f1} f1}"); }
	@Test public void test_3345() { checkIsSubtype("{{void f2} f1}","{{void f2} f1}"); }
	@Test public void test_3346() { checkIsSubtype("{{void f2} f1}","{{void f1} f2}"); }
	@Test public void test_3347() { checkIsSubtype("{{void f2} f1}","{{void f2} f2}"); }
	@Test public void test_3348() { checkIsSubtype("{{void f2} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_3349() { checkIsSubtype("{{void f2} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_3350() { checkIsSubtype("{{void f2} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_3351() { checkIsSubtype("{{void f2} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_3352() { checkNotSubtype("{{void f2} f1}","{{any f1} f1}"); }
	@Test public void test_3353() { checkNotSubtype("{{void f2} f1}","{{any f2} f1}"); }
	@Test public void test_3354() { checkNotSubtype("{{void f2} f1}","{{any f1} f2}"); }
	@Test public void test_3355() { checkNotSubtype("{{void f2} f1}","{{any f2} f2}"); }
	@Test public void test_3356() { checkNotSubtype("{{void f2} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_3357() { checkNotSubtype("{{void f2} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_3358() { checkNotSubtype("{{void f2} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_3359() { checkNotSubtype("{{void f2} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_3360() { checkNotSubtype("{{void f2} f1}","{{null f1} f1}"); }
	@Test public void test_3361() { checkNotSubtype("{{void f2} f1}","{{null f2} f1}"); }
	@Test public void test_3362() { checkNotSubtype("{{void f2} f1}","{{null f1} f2}"); }
	@Test public void test_3363() { checkNotSubtype("{{void f2} f1}","{{null f2} f2}"); }
	@Test public void test_3364() { checkNotSubtype("{{void f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_3365() { checkNotSubtype("{{void f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_3366() { checkNotSubtype("{{void f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_3367() { checkNotSubtype("{{void f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_3368() { checkNotSubtype("{{void f2} f1}","{{int f1} f1}"); }
	@Test public void test_3369() { checkNotSubtype("{{void f2} f1}","{{int f2} f1}"); }
	@Test public void test_3370() { checkNotSubtype("{{void f2} f1}","{{int f1} f2}"); }
	@Test public void test_3371() { checkNotSubtype("{{void f2} f1}","{{int f2} f2}"); }
	@Test public void test_3372() { checkNotSubtype("{{void f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_3373() { checkNotSubtype("{{void f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_3374() { checkNotSubtype("{{void f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_3375() { checkNotSubtype("{{void f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_3376() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_3377() { checkNotSubtype("{{void f1} f2}","null"); }
	@Test public void test_3378() { checkNotSubtype("{{void f1} f2}","int"); }
	@Test public void test_3379() { checkIsSubtype("{{void f1} f2}","{void f1}"); }
	@Test public void test_3380() { checkIsSubtype("{{void f1} f2}","{void f2}"); }
	@Test public void test_3381() { checkNotSubtype("{{void f1} f2}","{any f1}"); }
	@Test public void test_3382() { checkNotSubtype("{{void f1} f2}","{any f2}"); }
	@Test public void test_3383() { checkNotSubtype("{{void f1} f2}","{null f1}"); }
	@Test public void test_3384() { checkNotSubtype("{{void f1} f2}","{null f2}"); }
	@Test public void test_3385() { checkNotSubtype("{{void f1} f2}","{int f1}"); }
	@Test public void test_3386() { checkNotSubtype("{{void f1} f2}","{int f2}"); }
	@Test public void test_3387() { checkIsSubtype("{{void f1} f2}","{void f1,void f2}"); }
	@Test public void test_3388() { checkIsSubtype("{{void f1} f2}","{void f2,void f3}"); }
	@Test public void test_3389() { checkIsSubtype("{{void f1} f2}","{void f1,any f2}"); }
	@Test public void test_3390() { checkIsSubtype("{{void f1} f2}","{void f2,any f3}"); }
	@Test public void test_3391() { checkIsSubtype("{{void f1} f2}","{void f1,null f2}"); }
	@Test public void test_3392() { checkIsSubtype("{{void f1} f2}","{void f2,null f3}"); }
	@Test public void test_3393() { checkIsSubtype("{{void f1} f2}","{void f1,int f2}"); }
	@Test public void test_3394() { checkIsSubtype("{{void f1} f2}","{void f2,int f3}"); }
	@Test public void test_3395() { checkIsSubtype("{{void f1} f2}","{any f1,void f2}"); }
	@Test public void test_3396() { checkIsSubtype("{{void f1} f2}","{any f2,void f3}"); }
	@Test public void test_3397() { checkNotSubtype("{{void f1} f2}","{any f1,any f2}"); }
	@Test public void test_3398() { checkNotSubtype("{{void f1} f2}","{any f2,any f3}"); }
	@Test public void test_3399() { checkNotSubtype("{{void f1} f2}","{any f1,null f2}"); }
	@Test public void test_3400() { checkNotSubtype("{{void f1} f2}","{any f2,null f3}"); }
	@Test public void test_3401() { checkNotSubtype("{{void f1} f2}","{any f1,int f2}"); }
	@Test public void test_3402() { checkNotSubtype("{{void f1} f2}","{any f2,int f3}"); }
	@Test public void test_3403() { checkIsSubtype("{{void f1} f2}","{null f1,void f2}"); }
	@Test public void test_3404() { checkIsSubtype("{{void f1} f2}","{null f2,void f3}"); }
	@Test public void test_3405() { checkNotSubtype("{{void f1} f2}","{null f1,any f2}"); }
	@Test public void test_3406() { checkNotSubtype("{{void f1} f2}","{null f2,any f3}"); }
	@Test public void test_3407() { checkNotSubtype("{{void f1} f2}","{null f1,null f2}"); }
	@Test public void test_3408() { checkNotSubtype("{{void f1} f2}","{null f2,null f3}"); }
	@Test public void test_3409() { checkNotSubtype("{{void f1} f2}","{null f1,int f2}"); }
	@Test public void test_3410() { checkNotSubtype("{{void f1} f2}","{null f2,int f3}"); }
	@Test public void test_3411() { checkIsSubtype("{{void f1} f2}","{int f1,void f2}"); }
	@Test public void test_3412() { checkIsSubtype("{{void f1} f2}","{int f2,void f3}"); }
	@Test public void test_3413() { checkNotSubtype("{{void f1} f2}","{int f1,any f2}"); }
	@Test public void test_3414() { checkNotSubtype("{{void f1} f2}","{int f2,any f3}"); }
	@Test public void test_3415() { checkNotSubtype("{{void f1} f2}","{int f1,null f2}"); }
	@Test public void test_3416() { checkNotSubtype("{{void f1} f2}","{int f2,null f3}"); }
	@Test public void test_3417() { checkNotSubtype("{{void f1} f2}","{int f1,int f2}"); }
	@Test public void test_3418() { checkNotSubtype("{{void f1} f2}","{int f2,int f3}"); }
	@Test public void test_3419() { checkIsSubtype("{{void f1} f2}","{{void f1} f1}"); }
	@Test public void test_3420() { checkIsSubtype("{{void f1} f2}","{{void f2} f1}"); }
	@Test public void test_3421() { checkIsSubtype("{{void f1} f2}","{{void f1} f2}"); }
	@Test public void test_3422() { checkIsSubtype("{{void f1} f2}","{{void f2} f2}"); }
	@Test public void test_3423() { checkIsSubtype("{{void f1} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_3424() { checkIsSubtype("{{void f1} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_3425() { checkIsSubtype("{{void f1} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_3426() { checkIsSubtype("{{void f1} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_3427() { checkNotSubtype("{{void f1} f2}","{{any f1} f1}"); }
	@Test public void test_3428() { checkNotSubtype("{{void f1} f2}","{{any f2} f1}"); }
	@Test public void test_3429() { checkNotSubtype("{{void f1} f2}","{{any f1} f2}"); }
	@Test public void test_3430() { checkNotSubtype("{{void f1} f2}","{{any f2} f2}"); }
	@Test public void test_3431() { checkNotSubtype("{{void f1} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_3432() { checkNotSubtype("{{void f1} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_3433() { checkNotSubtype("{{void f1} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_3434() { checkNotSubtype("{{void f1} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_3435() { checkNotSubtype("{{void f1} f2}","{{null f1} f1}"); }
	@Test public void test_3436() { checkNotSubtype("{{void f1} f2}","{{null f2} f1}"); }
	@Test public void test_3437() { checkNotSubtype("{{void f1} f2}","{{null f1} f2}"); }
	@Test public void test_3438() { checkNotSubtype("{{void f1} f2}","{{null f2} f2}"); }
	@Test public void test_3439() { checkNotSubtype("{{void f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3440() { checkNotSubtype("{{void f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3441() { checkNotSubtype("{{void f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3442() { checkNotSubtype("{{void f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3443() { checkNotSubtype("{{void f1} f2}","{{int f1} f1}"); }
	@Test public void test_3444() { checkNotSubtype("{{void f1} f2}","{{int f2} f1}"); }
	@Test public void test_3445() { checkNotSubtype("{{void f1} f2}","{{int f1} f2}"); }
	@Test public void test_3446() { checkNotSubtype("{{void f1} f2}","{{int f2} f2}"); }
	@Test public void test_3447() { checkNotSubtype("{{void f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3448() { checkNotSubtype("{{void f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3449() { checkNotSubtype("{{void f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3450() { checkNotSubtype("{{void f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3451() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_3452() { checkNotSubtype("{{void f2} f2}","null"); }
	@Test public void test_3453() { checkNotSubtype("{{void f2} f2}","int"); }
	@Test public void test_3454() { checkIsSubtype("{{void f2} f2}","{void f1}"); }
	@Test public void test_3455() { checkIsSubtype("{{void f2} f2}","{void f2}"); }
	@Test public void test_3456() { checkNotSubtype("{{void f2} f2}","{any f1}"); }
	@Test public void test_3457() { checkNotSubtype("{{void f2} f2}","{any f2}"); }
	@Test public void test_3458() { checkNotSubtype("{{void f2} f2}","{null f1}"); }
	@Test public void test_3459() { checkNotSubtype("{{void f2} f2}","{null f2}"); }
	@Test public void test_3460() { checkNotSubtype("{{void f2} f2}","{int f1}"); }
	@Test public void test_3461() { checkNotSubtype("{{void f2} f2}","{int f2}"); }
	@Test public void test_3462() { checkIsSubtype("{{void f2} f2}","{void f1,void f2}"); }
	@Test public void test_3463() { checkIsSubtype("{{void f2} f2}","{void f2,void f3}"); }
	@Test public void test_3464() { checkIsSubtype("{{void f2} f2}","{void f1,any f2}"); }
	@Test public void test_3465() { checkIsSubtype("{{void f2} f2}","{void f2,any f3}"); }
	@Test public void test_3466() { checkIsSubtype("{{void f2} f2}","{void f1,null f2}"); }
	@Test public void test_3467() { checkIsSubtype("{{void f2} f2}","{void f2,null f3}"); }
	@Test public void test_3468() { checkIsSubtype("{{void f2} f2}","{void f1,int f2}"); }
	@Test public void test_3469() { checkIsSubtype("{{void f2} f2}","{void f2,int f3}"); }
	@Test public void test_3470() { checkIsSubtype("{{void f2} f2}","{any f1,void f2}"); }
	@Test public void test_3471() { checkIsSubtype("{{void f2} f2}","{any f2,void f3}"); }
	@Test public void test_3472() { checkNotSubtype("{{void f2} f2}","{any f1,any f2}"); }
	@Test public void test_3473() { checkNotSubtype("{{void f2} f2}","{any f2,any f3}"); }
	@Test public void test_3474() { checkNotSubtype("{{void f2} f2}","{any f1,null f2}"); }
	@Test public void test_3475() { checkNotSubtype("{{void f2} f2}","{any f2,null f3}"); }
	@Test public void test_3476() { checkNotSubtype("{{void f2} f2}","{any f1,int f2}"); }
	@Test public void test_3477() { checkNotSubtype("{{void f2} f2}","{any f2,int f3}"); }
	@Test public void test_3478() { checkIsSubtype("{{void f2} f2}","{null f1,void f2}"); }
	@Test public void test_3479() { checkIsSubtype("{{void f2} f2}","{null f2,void f3}"); }
	@Test public void test_3480() { checkNotSubtype("{{void f2} f2}","{null f1,any f2}"); }
	@Test public void test_3481() { checkNotSubtype("{{void f2} f2}","{null f2,any f3}"); }
	@Test public void test_3482() { checkNotSubtype("{{void f2} f2}","{null f1,null f2}"); }
	@Test public void test_3483() { checkNotSubtype("{{void f2} f2}","{null f2,null f3}"); }
	@Test public void test_3484() { checkNotSubtype("{{void f2} f2}","{null f1,int f2}"); }
	@Test public void test_3485() { checkNotSubtype("{{void f2} f2}","{null f2,int f3}"); }
	@Test public void test_3486() { checkIsSubtype("{{void f2} f2}","{int f1,void f2}"); }
	@Test public void test_3487() { checkIsSubtype("{{void f2} f2}","{int f2,void f3}"); }
	@Test public void test_3488() { checkNotSubtype("{{void f2} f2}","{int f1,any f2}"); }
	@Test public void test_3489() { checkNotSubtype("{{void f2} f2}","{int f2,any f3}"); }
	@Test public void test_3490() { checkNotSubtype("{{void f2} f2}","{int f1,null f2}"); }
	@Test public void test_3491() { checkNotSubtype("{{void f2} f2}","{int f2,null f3}"); }
	@Test public void test_3492() { checkNotSubtype("{{void f2} f2}","{int f1,int f2}"); }
	@Test public void test_3493() { checkNotSubtype("{{void f2} f2}","{int f2,int f3}"); }
	@Test public void test_3494() { checkIsSubtype("{{void f2} f2}","{{void f1} f1}"); }
	@Test public void test_3495() { checkIsSubtype("{{void f2} f2}","{{void f2} f1}"); }
	@Test public void test_3496() { checkIsSubtype("{{void f2} f2}","{{void f1} f2}"); }
	@Test public void test_3497() { checkIsSubtype("{{void f2} f2}","{{void f2} f2}"); }
	@Test public void test_3498() { checkIsSubtype("{{void f2} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_3499() { checkIsSubtype("{{void f2} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_3500() { checkIsSubtype("{{void f2} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_3501() { checkIsSubtype("{{void f2} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_3502() { checkNotSubtype("{{void f2} f2}","{{any f1} f1}"); }
	@Test public void test_3503() { checkNotSubtype("{{void f2} f2}","{{any f2} f1}"); }
	@Test public void test_3504() { checkNotSubtype("{{void f2} f2}","{{any f1} f2}"); }
	@Test public void test_3505() { checkNotSubtype("{{void f2} f2}","{{any f2} f2}"); }
	@Test public void test_3506() { checkNotSubtype("{{void f2} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_3507() { checkNotSubtype("{{void f2} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_3508() { checkNotSubtype("{{void f2} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_3509() { checkNotSubtype("{{void f2} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_3510() { checkNotSubtype("{{void f2} f2}","{{null f1} f1}"); }
	@Test public void test_3511() { checkNotSubtype("{{void f2} f2}","{{null f2} f1}"); }
	@Test public void test_3512() { checkNotSubtype("{{void f2} f2}","{{null f1} f2}"); }
	@Test public void test_3513() { checkNotSubtype("{{void f2} f2}","{{null f2} f2}"); }
	@Test public void test_3514() { checkNotSubtype("{{void f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3515() { checkNotSubtype("{{void f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3516() { checkNotSubtype("{{void f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3517() { checkNotSubtype("{{void f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3518() { checkNotSubtype("{{void f2} f2}","{{int f1} f1}"); }
	@Test public void test_3519() { checkNotSubtype("{{void f2} f2}","{{int f2} f1}"); }
	@Test public void test_3520() { checkNotSubtype("{{void f2} f2}","{{int f1} f2}"); }
	@Test public void test_3521() { checkNotSubtype("{{void f2} f2}","{{int f2} f2}"); }
	@Test public void test_3522() { checkNotSubtype("{{void f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3523() { checkNotSubtype("{{void f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3524() { checkNotSubtype("{{void f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3525() { checkNotSubtype("{{void f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3526() { checkNotSubtype("{{void f1} f1,void f2}","any"); }
	@Test public void test_3527() { checkNotSubtype("{{void f1} f1,void f2}","null"); }
	@Test public void test_3528() { checkNotSubtype("{{void f1} f1,void f2}","int"); }
	@Test public void test_3529() { checkIsSubtype("{{void f1} f1,void f2}","{void f1}"); }
	@Test public void test_3530() { checkIsSubtype("{{void f1} f1,void f2}","{void f2}"); }
	@Test public void test_3531() { checkNotSubtype("{{void f1} f1,void f2}","{any f1}"); }
	@Test public void test_3532() { checkNotSubtype("{{void f1} f1,void f2}","{any f2}"); }
	@Test public void test_3533() { checkNotSubtype("{{void f1} f1,void f2}","{null f1}"); }
	@Test public void test_3534() { checkNotSubtype("{{void f1} f1,void f2}","{null f2}"); }
	@Test public void test_3535() { checkNotSubtype("{{void f1} f1,void f2}","{int f1}"); }
	@Test public void test_3536() { checkNotSubtype("{{void f1} f1,void f2}","{int f2}"); }
	@Test public void test_3537() { checkIsSubtype("{{void f1} f1,void f2}","{void f1,void f2}"); }
	@Test public void test_3538() { checkIsSubtype("{{void f1} f1,void f2}","{void f2,void f3}"); }
	@Test public void test_3539() { checkIsSubtype("{{void f1} f1,void f2}","{void f1,any f2}"); }
	@Test public void test_3540() { checkIsSubtype("{{void f1} f1,void f2}","{void f2,any f3}"); }
	@Test public void test_3541() { checkIsSubtype("{{void f1} f1,void f2}","{void f1,null f2}"); }
	@Test public void test_3542() { checkIsSubtype("{{void f1} f1,void f2}","{void f2,null f3}"); }
	@Test public void test_3543() { checkIsSubtype("{{void f1} f1,void f2}","{void f1,int f2}"); }
	@Test public void test_3544() { checkIsSubtype("{{void f1} f1,void f2}","{void f2,int f3}"); }
	@Test public void test_3545() { checkIsSubtype("{{void f1} f1,void f2}","{any f1,void f2}"); }
	@Test public void test_3546() { checkIsSubtype("{{void f1} f1,void f2}","{any f2,void f3}"); }
	@Test public void test_3547() { checkNotSubtype("{{void f1} f1,void f2}","{any f1,any f2}"); }
	@Test public void test_3548() { checkNotSubtype("{{void f1} f1,void f2}","{any f2,any f3}"); }
	@Test public void test_3549() { checkNotSubtype("{{void f1} f1,void f2}","{any f1,null f2}"); }
	@Test public void test_3550() { checkNotSubtype("{{void f1} f1,void f2}","{any f2,null f3}"); }
	@Test public void test_3551() { checkNotSubtype("{{void f1} f1,void f2}","{any f1,int f2}"); }
	@Test public void test_3552() { checkNotSubtype("{{void f1} f1,void f2}","{any f2,int f3}"); }
	@Test public void test_3553() { checkIsSubtype("{{void f1} f1,void f2}","{null f1,void f2}"); }
	@Test public void test_3554() { checkIsSubtype("{{void f1} f1,void f2}","{null f2,void f3}"); }
	@Test public void test_3555() { checkNotSubtype("{{void f1} f1,void f2}","{null f1,any f2}"); }
	@Test public void test_3556() { checkNotSubtype("{{void f1} f1,void f2}","{null f2,any f3}"); }
	@Test public void test_3557() { checkNotSubtype("{{void f1} f1,void f2}","{null f1,null f2}"); }
	@Test public void test_3558() { checkNotSubtype("{{void f1} f1,void f2}","{null f2,null f3}"); }
	@Test public void test_3559() { checkNotSubtype("{{void f1} f1,void f2}","{null f1,int f2}"); }
	@Test public void test_3560() { checkNotSubtype("{{void f1} f1,void f2}","{null f2,int f3}"); }
	@Test public void test_3561() { checkIsSubtype("{{void f1} f1,void f2}","{int f1,void f2}"); }
	@Test public void test_3562() { checkIsSubtype("{{void f1} f1,void f2}","{int f2,void f3}"); }
	@Test public void test_3563() { checkNotSubtype("{{void f1} f1,void f2}","{int f1,any f2}"); }
	@Test public void test_3564() { checkNotSubtype("{{void f1} f1,void f2}","{int f2,any f3}"); }
	@Test public void test_3565() { checkNotSubtype("{{void f1} f1,void f2}","{int f1,null f2}"); }
	@Test public void test_3566() { checkNotSubtype("{{void f1} f1,void f2}","{int f2,null f3}"); }
	@Test public void test_3567() { checkNotSubtype("{{void f1} f1,void f2}","{int f1,int f2}"); }
	@Test public void test_3568() { checkNotSubtype("{{void f1} f1,void f2}","{int f2,int f3}"); }
	@Test public void test_3569() { checkIsSubtype("{{void f1} f1,void f2}","{{void f1} f1}"); }
	@Test public void test_3570() { checkIsSubtype("{{void f1} f1,void f2}","{{void f2} f1}"); }
	@Test public void test_3571() { checkIsSubtype("{{void f1} f1,void f2}","{{void f1} f2}"); }
	@Test public void test_3572() { checkIsSubtype("{{void f1} f1,void f2}","{{void f2} f2}"); }
	@Test public void test_3573() { checkIsSubtype("{{void f1} f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_3574() { checkIsSubtype("{{void f1} f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_3575() { checkIsSubtype("{{void f1} f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_3576() { checkIsSubtype("{{void f1} f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_3577() { checkNotSubtype("{{void f1} f1,void f2}","{{any f1} f1}"); }
	@Test public void test_3578() { checkNotSubtype("{{void f1} f1,void f2}","{{any f2} f1}"); }
	@Test public void test_3579() { checkNotSubtype("{{void f1} f1,void f2}","{{any f1} f2}"); }
	@Test public void test_3580() { checkNotSubtype("{{void f1} f1,void f2}","{{any f2} f2}"); }
	@Test public void test_3581() { checkNotSubtype("{{void f1} f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_3582() { checkNotSubtype("{{void f1} f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_3583() { checkNotSubtype("{{void f1} f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_3584() { checkNotSubtype("{{void f1} f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_3585() { checkNotSubtype("{{void f1} f1,void f2}","{{null f1} f1}"); }
	@Test public void test_3586() { checkNotSubtype("{{void f1} f1,void f2}","{{null f2} f1}"); }
	@Test public void test_3587() { checkNotSubtype("{{void f1} f1,void f2}","{{null f1} f2}"); }
	@Test public void test_3588() { checkNotSubtype("{{void f1} f1,void f2}","{{null f2} f2}"); }
	@Test public void test_3589() { checkNotSubtype("{{void f1} f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3590() { checkNotSubtype("{{void f1} f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3591() { checkNotSubtype("{{void f1} f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3592() { checkNotSubtype("{{void f1} f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3593() { checkNotSubtype("{{void f1} f1,void f2}","{{int f1} f1}"); }
	@Test public void test_3594() { checkNotSubtype("{{void f1} f1,void f2}","{{int f2} f1}"); }
	@Test public void test_3595() { checkNotSubtype("{{void f1} f1,void f2}","{{int f1} f2}"); }
	@Test public void test_3596() { checkNotSubtype("{{void f1} f1,void f2}","{{int f2} f2}"); }
	@Test public void test_3597() { checkNotSubtype("{{void f1} f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3598() { checkNotSubtype("{{void f1} f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3599() { checkNotSubtype("{{void f1} f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3600() { checkNotSubtype("{{void f1} f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3601() { checkNotSubtype("{{void f2} f1,void f2}","any"); }
	@Test public void test_3602() { checkNotSubtype("{{void f2} f1,void f2}","null"); }
	@Test public void test_3603() { checkNotSubtype("{{void f2} f1,void f2}","int"); }
	@Test public void test_3604() { checkIsSubtype("{{void f2} f1,void f2}","{void f1}"); }
	@Test public void test_3605() { checkIsSubtype("{{void f2} f1,void f2}","{void f2}"); }
	@Test public void test_3606() { checkNotSubtype("{{void f2} f1,void f2}","{any f1}"); }
	@Test public void test_3607() { checkNotSubtype("{{void f2} f1,void f2}","{any f2}"); }
	@Test public void test_3608() { checkNotSubtype("{{void f2} f1,void f2}","{null f1}"); }
	@Test public void test_3609() { checkNotSubtype("{{void f2} f1,void f2}","{null f2}"); }
	@Test public void test_3610() { checkNotSubtype("{{void f2} f1,void f2}","{int f1}"); }
	@Test public void test_3611() { checkNotSubtype("{{void f2} f1,void f2}","{int f2}"); }
	@Test public void test_3612() { checkIsSubtype("{{void f2} f1,void f2}","{void f1,void f2}"); }
	@Test public void test_3613() { checkIsSubtype("{{void f2} f1,void f2}","{void f2,void f3}"); }
	@Test public void test_3614() { checkIsSubtype("{{void f2} f1,void f2}","{void f1,any f2}"); }
	@Test public void test_3615() { checkIsSubtype("{{void f2} f1,void f2}","{void f2,any f3}"); }
	@Test public void test_3616() { checkIsSubtype("{{void f2} f1,void f2}","{void f1,null f2}"); }
	@Test public void test_3617() { checkIsSubtype("{{void f2} f1,void f2}","{void f2,null f3}"); }
	@Test public void test_3618() { checkIsSubtype("{{void f2} f1,void f2}","{void f1,int f2}"); }
	@Test public void test_3619() { checkIsSubtype("{{void f2} f1,void f2}","{void f2,int f3}"); }
	@Test public void test_3620() { checkIsSubtype("{{void f2} f1,void f2}","{any f1,void f2}"); }
	@Test public void test_3621() { checkIsSubtype("{{void f2} f1,void f2}","{any f2,void f3}"); }
	@Test public void test_3622() { checkNotSubtype("{{void f2} f1,void f2}","{any f1,any f2}"); }
	@Test public void test_3623() { checkNotSubtype("{{void f2} f1,void f2}","{any f2,any f3}"); }
	@Test public void test_3624() { checkNotSubtype("{{void f2} f1,void f2}","{any f1,null f2}"); }
	@Test public void test_3625() { checkNotSubtype("{{void f2} f1,void f2}","{any f2,null f3}"); }
	@Test public void test_3626() { checkNotSubtype("{{void f2} f1,void f2}","{any f1,int f2}"); }
	@Test public void test_3627() { checkNotSubtype("{{void f2} f1,void f2}","{any f2,int f3}"); }
	@Test public void test_3628() { checkIsSubtype("{{void f2} f1,void f2}","{null f1,void f2}"); }
	@Test public void test_3629() { checkIsSubtype("{{void f2} f1,void f2}","{null f2,void f3}"); }
	@Test public void test_3630() { checkNotSubtype("{{void f2} f1,void f2}","{null f1,any f2}"); }
	@Test public void test_3631() { checkNotSubtype("{{void f2} f1,void f2}","{null f2,any f3}"); }
	@Test public void test_3632() { checkNotSubtype("{{void f2} f1,void f2}","{null f1,null f2}"); }
	@Test public void test_3633() { checkNotSubtype("{{void f2} f1,void f2}","{null f2,null f3}"); }
	@Test public void test_3634() { checkNotSubtype("{{void f2} f1,void f2}","{null f1,int f2}"); }
	@Test public void test_3635() { checkNotSubtype("{{void f2} f1,void f2}","{null f2,int f3}"); }
	@Test public void test_3636() { checkIsSubtype("{{void f2} f1,void f2}","{int f1,void f2}"); }
	@Test public void test_3637() { checkIsSubtype("{{void f2} f1,void f2}","{int f2,void f3}"); }
	@Test public void test_3638() { checkNotSubtype("{{void f2} f1,void f2}","{int f1,any f2}"); }
	@Test public void test_3639() { checkNotSubtype("{{void f2} f1,void f2}","{int f2,any f3}"); }
	@Test public void test_3640() { checkNotSubtype("{{void f2} f1,void f2}","{int f1,null f2}"); }
	@Test public void test_3641() { checkNotSubtype("{{void f2} f1,void f2}","{int f2,null f3}"); }
	@Test public void test_3642() { checkNotSubtype("{{void f2} f1,void f2}","{int f1,int f2}"); }
	@Test public void test_3643() { checkNotSubtype("{{void f2} f1,void f2}","{int f2,int f3}"); }
	@Test public void test_3644() { checkIsSubtype("{{void f2} f1,void f2}","{{void f1} f1}"); }
	@Test public void test_3645() { checkIsSubtype("{{void f2} f1,void f2}","{{void f2} f1}"); }
	@Test public void test_3646() { checkIsSubtype("{{void f2} f1,void f2}","{{void f1} f2}"); }
	@Test public void test_3647() { checkIsSubtype("{{void f2} f1,void f2}","{{void f2} f2}"); }
	@Test public void test_3648() { checkIsSubtype("{{void f2} f1,void f2}","{{void f1} f1,void f2}"); }
	@Test public void test_3649() { checkIsSubtype("{{void f2} f1,void f2}","{{void f2} f1,void f2}"); }
	@Test public void test_3650() { checkIsSubtype("{{void f2} f1,void f2}","{{void f1} f2,void f3}"); }
	@Test public void test_3651() { checkIsSubtype("{{void f2} f1,void f2}","{{void f2} f2,void f3}"); }
	@Test public void test_3652() { checkNotSubtype("{{void f2} f1,void f2}","{{any f1} f1}"); }
	@Test public void test_3653() { checkNotSubtype("{{void f2} f1,void f2}","{{any f2} f1}"); }
	@Test public void test_3654() { checkNotSubtype("{{void f2} f1,void f2}","{{any f1} f2}"); }
	@Test public void test_3655() { checkNotSubtype("{{void f2} f1,void f2}","{{any f2} f2}"); }
	@Test public void test_3656() { checkNotSubtype("{{void f2} f1,void f2}","{{any f1} f1,any f2}"); }
	@Test public void test_3657() { checkNotSubtype("{{void f2} f1,void f2}","{{any f2} f1,any f2}"); }
	@Test public void test_3658() { checkNotSubtype("{{void f2} f1,void f2}","{{any f1} f2,any f3}"); }
	@Test public void test_3659() { checkNotSubtype("{{void f2} f1,void f2}","{{any f2} f2,any f3}"); }
	@Test public void test_3660() { checkNotSubtype("{{void f2} f1,void f2}","{{null f1} f1}"); }
	@Test public void test_3661() { checkNotSubtype("{{void f2} f1,void f2}","{{null f2} f1}"); }
	@Test public void test_3662() { checkNotSubtype("{{void f2} f1,void f2}","{{null f1} f2}"); }
	@Test public void test_3663() { checkNotSubtype("{{void f2} f1,void f2}","{{null f2} f2}"); }
	@Test public void test_3664() { checkNotSubtype("{{void f2} f1,void f2}","{{null f1} f1,null f2}"); }
	@Test public void test_3665() { checkNotSubtype("{{void f2} f1,void f2}","{{null f2} f1,null f2}"); }
	@Test public void test_3666() { checkNotSubtype("{{void f2} f1,void f2}","{{null f1} f2,null f3}"); }
	@Test public void test_3667() { checkNotSubtype("{{void f2} f1,void f2}","{{null f2} f2,null f3}"); }
	@Test public void test_3668() { checkNotSubtype("{{void f2} f1,void f2}","{{int f1} f1}"); }
	@Test public void test_3669() { checkNotSubtype("{{void f2} f1,void f2}","{{int f2} f1}"); }
	@Test public void test_3670() { checkNotSubtype("{{void f2} f1,void f2}","{{int f1} f2}"); }
	@Test public void test_3671() { checkNotSubtype("{{void f2} f1,void f2}","{{int f2} f2}"); }
	@Test public void test_3672() { checkNotSubtype("{{void f2} f1,void f2}","{{int f1} f1,int f2}"); }
	@Test public void test_3673() { checkNotSubtype("{{void f2} f1,void f2}","{{int f2} f1,int f2}"); }
	@Test public void test_3674() { checkNotSubtype("{{void f2} f1,void f2}","{{int f1} f2,int f3}"); }
	@Test public void test_3675() { checkNotSubtype("{{void f2} f1,void f2}","{{int f2} f2,int f3}"); }
	@Test public void test_3676() { checkNotSubtype("{{void f1} f2,void f3}","any"); }
	@Test public void test_3677() { checkNotSubtype("{{void f1} f2,void f3}","null"); }
	@Test public void test_3678() { checkNotSubtype("{{void f1} f2,void f3}","int"); }
	@Test public void test_3679() { checkIsSubtype("{{void f1} f2,void f3}","{void f1}"); }
	@Test public void test_3680() { checkIsSubtype("{{void f1} f2,void f3}","{void f2}"); }
	@Test public void test_3681() { checkNotSubtype("{{void f1} f2,void f3}","{any f1}"); }
	@Test public void test_3682() { checkNotSubtype("{{void f1} f2,void f3}","{any f2}"); }
	@Test public void test_3683() { checkNotSubtype("{{void f1} f2,void f3}","{null f1}"); }
	@Test public void test_3684() { checkNotSubtype("{{void f1} f2,void f3}","{null f2}"); }
	@Test public void test_3685() { checkNotSubtype("{{void f1} f2,void f3}","{int f1}"); }
	@Test public void test_3686() { checkNotSubtype("{{void f1} f2,void f3}","{int f2}"); }
	@Test public void test_3687() { checkIsSubtype("{{void f1} f2,void f3}","{void f1,void f2}"); }
	@Test public void test_3688() { checkIsSubtype("{{void f1} f2,void f3}","{void f2,void f3}"); }
	@Test public void test_3689() { checkIsSubtype("{{void f1} f2,void f3}","{void f1,any f2}"); }
	@Test public void test_3690() { checkIsSubtype("{{void f1} f2,void f3}","{void f2,any f3}"); }
	@Test public void test_3691() { checkIsSubtype("{{void f1} f2,void f3}","{void f1,null f2}"); }
	@Test public void test_3692() { checkIsSubtype("{{void f1} f2,void f3}","{void f2,null f3}"); }
	@Test public void test_3693() { checkIsSubtype("{{void f1} f2,void f3}","{void f1,int f2}"); }
	@Test public void test_3694() { checkIsSubtype("{{void f1} f2,void f3}","{void f2,int f3}"); }
	@Test public void test_3695() { checkIsSubtype("{{void f1} f2,void f3}","{any f1,void f2}"); }
	@Test public void test_3696() { checkIsSubtype("{{void f1} f2,void f3}","{any f2,void f3}"); }
	@Test public void test_3697() { checkNotSubtype("{{void f1} f2,void f3}","{any f1,any f2}"); }
	@Test public void test_3698() { checkNotSubtype("{{void f1} f2,void f3}","{any f2,any f3}"); }
	@Test public void test_3699() { checkNotSubtype("{{void f1} f2,void f3}","{any f1,null f2}"); }
	@Test public void test_3700() { checkNotSubtype("{{void f1} f2,void f3}","{any f2,null f3}"); }
	@Test public void test_3701() { checkNotSubtype("{{void f1} f2,void f3}","{any f1,int f2}"); }
	@Test public void test_3702() { checkNotSubtype("{{void f1} f2,void f3}","{any f2,int f3}"); }
	@Test public void test_3703() { checkIsSubtype("{{void f1} f2,void f3}","{null f1,void f2}"); }
	@Test public void test_3704() { checkIsSubtype("{{void f1} f2,void f3}","{null f2,void f3}"); }
	@Test public void test_3705() { checkNotSubtype("{{void f1} f2,void f3}","{null f1,any f2}"); }
	@Test public void test_3706() { checkNotSubtype("{{void f1} f2,void f3}","{null f2,any f3}"); }
	@Test public void test_3707() { checkNotSubtype("{{void f1} f2,void f3}","{null f1,null f2}"); }
	@Test public void test_3708() { checkNotSubtype("{{void f1} f2,void f3}","{null f2,null f3}"); }
	@Test public void test_3709() { checkNotSubtype("{{void f1} f2,void f3}","{null f1,int f2}"); }
	@Test public void test_3710() { checkNotSubtype("{{void f1} f2,void f3}","{null f2,int f3}"); }
	@Test public void test_3711() { checkIsSubtype("{{void f1} f2,void f3}","{int f1,void f2}"); }
	@Test public void test_3712() { checkIsSubtype("{{void f1} f2,void f3}","{int f2,void f3}"); }
	@Test public void test_3713() { checkNotSubtype("{{void f1} f2,void f3}","{int f1,any f2}"); }
	@Test public void test_3714() { checkNotSubtype("{{void f1} f2,void f3}","{int f2,any f3}"); }
	@Test public void test_3715() { checkNotSubtype("{{void f1} f2,void f3}","{int f1,null f2}"); }
	@Test public void test_3716() { checkNotSubtype("{{void f1} f2,void f3}","{int f2,null f3}"); }
	@Test public void test_3717() { checkNotSubtype("{{void f1} f2,void f3}","{int f1,int f2}"); }
	@Test public void test_3718() { checkNotSubtype("{{void f1} f2,void f3}","{int f2,int f3}"); }
	@Test public void test_3719() { checkIsSubtype("{{void f1} f2,void f3}","{{void f1} f1}"); }
	@Test public void test_3720() { checkIsSubtype("{{void f1} f2,void f3}","{{void f2} f1}"); }
	@Test public void test_3721() { checkIsSubtype("{{void f1} f2,void f3}","{{void f1} f2}"); }
	@Test public void test_3722() { checkIsSubtype("{{void f1} f2,void f3}","{{void f2} f2}"); }
	@Test public void test_3723() { checkIsSubtype("{{void f1} f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_3724() { checkIsSubtype("{{void f1} f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_3725() { checkIsSubtype("{{void f1} f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_3726() { checkIsSubtype("{{void f1} f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_3727() { checkNotSubtype("{{void f1} f2,void f3}","{{any f1} f1}"); }
	@Test public void test_3728() { checkNotSubtype("{{void f1} f2,void f3}","{{any f2} f1}"); }
	@Test public void test_3729() { checkNotSubtype("{{void f1} f2,void f3}","{{any f1} f2}"); }
	@Test public void test_3730() { checkNotSubtype("{{void f1} f2,void f3}","{{any f2} f2}"); }
	@Test public void test_3731() { checkNotSubtype("{{void f1} f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_3732() { checkNotSubtype("{{void f1} f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_3733() { checkNotSubtype("{{void f1} f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_3734() { checkNotSubtype("{{void f1} f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_3735() { checkNotSubtype("{{void f1} f2,void f3}","{{null f1} f1}"); }
	@Test public void test_3736() { checkNotSubtype("{{void f1} f2,void f3}","{{null f2} f1}"); }
	@Test public void test_3737() { checkNotSubtype("{{void f1} f2,void f3}","{{null f1} f2}"); }
	@Test public void test_3738() { checkNotSubtype("{{void f1} f2,void f3}","{{null f2} f2}"); }
	@Test public void test_3739() { checkNotSubtype("{{void f1} f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3740() { checkNotSubtype("{{void f1} f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3741() { checkNotSubtype("{{void f1} f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3742() { checkNotSubtype("{{void f1} f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3743() { checkNotSubtype("{{void f1} f2,void f3}","{{int f1} f1}"); }
	@Test public void test_3744() { checkNotSubtype("{{void f1} f2,void f3}","{{int f2} f1}"); }
	@Test public void test_3745() { checkNotSubtype("{{void f1} f2,void f3}","{{int f1} f2}"); }
	@Test public void test_3746() { checkNotSubtype("{{void f1} f2,void f3}","{{int f2} f2}"); }
	@Test public void test_3747() { checkNotSubtype("{{void f1} f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3748() { checkNotSubtype("{{void f1} f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3749() { checkNotSubtype("{{void f1} f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3750() { checkNotSubtype("{{void f1} f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3751() { checkNotSubtype("{{void f2} f2,void f3}","any"); }
	@Test public void test_3752() { checkNotSubtype("{{void f2} f2,void f3}","null"); }
	@Test public void test_3753() { checkNotSubtype("{{void f2} f2,void f3}","int"); }
	@Test public void test_3754() { checkIsSubtype("{{void f2} f2,void f3}","{void f1}"); }
	@Test public void test_3755() { checkIsSubtype("{{void f2} f2,void f3}","{void f2}"); }
	@Test public void test_3756() { checkNotSubtype("{{void f2} f2,void f3}","{any f1}"); }
	@Test public void test_3757() { checkNotSubtype("{{void f2} f2,void f3}","{any f2}"); }
	@Test public void test_3758() { checkNotSubtype("{{void f2} f2,void f3}","{null f1}"); }
	@Test public void test_3759() { checkNotSubtype("{{void f2} f2,void f3}","{null f2}"); }
	@Test public void test_3760() { checkNotSubtype("{{void f2} f2,void f3}","{int f1}"); }
	@Test public void test_3761() { checkNotSubtype("{{void f2} f2,void f3}","{int f2}"); }
	@Test public void test_3762() { checkIsSubtype("{{void f2} f2,void f3}","{void f1,void f2}"); }
	@Test public void test_3763() { checkIsSubtype("{{void f2} f2,void f3}","{void f2,void f3}"); }
	@Test public void test_3764() { checkIsSubtype("{{void f2} f2,void f3}","{void f1,any f2}"); }
	@Test public void test_3765() { checkIsSubtype("{{void f2} f2,void f3}","{void f2,any f3}"); }
	@Test public void test_3766() { checkIsSubtype("{{void f2} f2,void f3}","{void f1,null f2}"); }
	@Test public void test_3767() { checkIsSubtype("{{void f2} f2,void f3}","{void f2,null f3}"); }
	@Test public void test_3768() { checkIsSubtype("{{void f2} f2,void f3}","{void f1,int f2}"); }
	@Test public void test_3769() { checkIsSubtype("{{void f2} f2,void f3}","{void f2,int f3}"); }
	@Test public void test_3770() { checkIsSubtype("{{void f2} f2,void f3}","{any f1,void f2}"); }
	@Test public void test_3771() { checkIsSubtype("{{void f2} f2,void f3}","{any f2,void f3}"); }
	@Test public void test_3772() { checkNotSubtype("{{void f2} f2,void f3}","{any f1,any f2}"); }
	@Test public void test_3773() { checkNotSubtype("{{void f2} f2,void f3}","{any f2,any f3}"); }
	@Test public void test_3774() { checkNotSubtype("{{void f2} f2,void f3}","{any f1,null f2}"); }
	@Test public void test_3775() { checkNotSubtype("{{void f2} f2,void f3}","{any f2,null f3}"); }
	@Test public void test_3776() { checkNotSubtype("{{void f2} f2,void f3}","{any f1,int f2}"); }
	@Test public void test_3777() { checkNotSubtype("{{void f2} f2,void f3}","{any f2,int f3}"); }
	@Test public void test_3778() { checkIsSubtype("{{void f2} f2,void f3}","{null f1,void f2}"); }
	@Test public void test_3779() { checkIsSubtype("{{void f2} f2,void f3}","{null f2,void f3}"); }
	@Test public void test_3780() { checkNotSubtype("{{void f2} f2,void f3}","{null f1,any f2}"); }
	@Test public void test_3781() { checkNotSubtype("{{void f2} f2,void f3}","{null f2,any f3}"); }
	@Test public void test_3782() { checkNotSubtype("{{void f2} f2,void f3}","{null f1,null f2}"); }
	@Test public void test_3783() { checkNotSubtype("{{void f2} f2,void f3}","{null f2,null f3}"); }
	@Test public void test_3784() { checkNotSubtype("{{void f2} f2,void f3}","{null f1,int f2}"); }
	@Test public void test_3785() { checkNotSubtype("{{void f2} f2,void f3}","{null f2,int f3}"); }
	@Test public void test_3786() { checkIsSubtype("{{void f2} f2,void f3}","{int f1,void f2}"); }
	@Test public void test_3787() { checkIsSubtype("{{void f2} f2,void f3}","{int f2,void f3}"); }
	@Test public void test_3788() { checkNotSubtype("{{void f2} f2,void f3}","{int f1,any f2}"); }
	@Test public void test_3789() { checkNotSubtype("{{void f2} f2,void f3}","{int f2,any f3}"); }
	@Test public void test_3790() { checkNotSubtype("{{void f2} f2,void f3}","{int f1,null f2}"); }
	@Test public void test_3791() { checkNotSubtype("{{void f2} f2,void f3}","{int f2,null f3}"); }
	@Test public void test_3792() { checkNotSubtype("{{void f2} f2,void f3}","{int f1,int f2}"); }
	@Test public void test_3793() { checkNotSubtype("{{void f2} f2,void f3}","{int f2,int f3}"); }
	@Test public void test_3794() { checkIsSubtype("{{void f2} f2,void f3}","{{void f1} f1}"); }
	@Test public void test_3795() { checkIsSubtype("{{void f2} f2,void f3}","{{void f2} f1}"); }
	@Test public void test_3796() { checkIsSubtype("{{void f2} f2,void f3}","{{void f1} f2}"); }
	@Test public void test_3797() { checkIsSubtype("{{void f2} f2,void f3}","{{void f2} f2}"); }
	@Test public void test_3798() { checkIsSubtype("{{void f2} f2,void f3}","{{void f1} f1,void f2}"); }
	@Test public void test_3799() { checkIsSubtype("{{void f2} f2,void f3}","{{void f2} f1,void f2}"); }
	@Test public void test_3800() { checkIsSubtype("{{void f2} f2,void f3}","{{void f1} f2,void f3}"); }
	@Test public void test_3801() { checkIsSubtype("{{void f2} f2,void f3}","{{void f2} f2,void f3}"); }
	@Test public void test_3802() { checkNotSubtype("{{void f2} f2,void f3}","{{any f1} f1}"); }
	@Test public void test_3803() { checkNotSubtype("{{void f2} f2,void f3}","{{any f2} f1}"); }
	@Test public void test_3804() { checkNotSubtype("{{void f2} f2,void f3}","{{any f1} f2}"); }
	@Test public void test_3805() { checkNotSubtype("{{void f2} f2,void f3}","{{any f2} f2}"); }
	@Test public void test_3806() { checkNotSubtype("{{void f2} f2,void f3}","{{any f1} f1,any f2}"); }
	@Test public void test_3807() { checkNotSubtype("{{void f2} f2,void f3}","{{any f2} f1,any f2}"); }
	@Test public void test_3808() { checkNotSubtype("{{void f2} f2,void f3}","{{any f1} f2,any f3}"); }
	@Test public void test_3809() { checkNotSubtype("{{void f2} f2,void f3}","{{any f2} f2,any f3}"); }
	@Test public void test_3810() { checkNotSubtype("{{void f2} f2,void f3}","{{null f1} f1}"); }
	@Test public void test_3811() { checkNotSubtype("{{void f2} f2,void f3}","{{null f2} f1}"); }
	@Test public void test_3812() { checkNotSubtype("{{void f2} f2,void f3}","{{null f1} f2}"); }
	@Test public void test_3813() { checkNotSubtype("{{void f2} f2,void f3}","{{null f2} f2}"); }
	@Test public void test_3814() { checkNotSubtype("{{void f2} f2,void f3}","{{null f1} f1,null f2}"); }
	@Test public void test_3815() { checkNotSubtype("{{void f2} f2,void f3}","{{null f2} f1,null f2}"); }
	@Test public void test_3816() { checkNotSubtype("{{void f2} f2,void f3}","{{null f1} f2,null f3}"); }
	@Test public void test_3817() { checkNotSubtype("{{void f2} f2,void f3}","{{null f2} f2,null f3}"); }
	@Test public void test_3818() { checkNotSubtype("{{void f2} f2,void f3}","{{int f1} f1}"); }
	@Test public void test_3819() { checkNotSubtype("{{void f2} f2,void f3}","{{int f2} f1}"); }
	@Test public void test_3820() { checkNotSubtype("{{void f2} f2,void f3}","{{int f1} f2}"); }
	@Test public void test_3821() { checkNotSubtype("{{void f2} f2,void f3}","{{int f2} f2}"); }
	@Test public void test_3822() { checkNotSubtype("{{void f2} f2,void f3}","{{int f1} f1,int f2}"); }
	@Test public void test_3823() { checkNotSubtype("{{void f2} f2,void f3}","{{int f2} f1,int f2}"); }
	@Test public void test_3824() { checkNotSubtype("{{void f2} f2,void f3}","{{int f1} f2,int f3}"); }
	@Test public void test_3825() { checkNotSubtype("{{void f2} f2,void f3}","{{int f2} f2,int f3}"); }
	@Test public void test_3826() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_3827() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_3828() { checkNotSubtype("{{any f1} f1}","int"); }
	@Test public void test_3829() { checkIsSubtype("{{any f1} f1}","{void f1}"); }
	@Test public void test_3830() { checkIsSubtype("{{any f1} f1}","{void f2}"); }
	@Test public void test_3831() { checkNotSubtype("{{any f1} f1}","{any f1}"); }
	@Test public void test_3832() { checkNotSubtype("{{any f1} f1}","{any f2}"); }
	@Test public void test_3833() { checkNotSubtype("{{any f1} f1}","{null f1}"); }
	@Test public void test_3834() { checkNotSubtype("{{any f1} f1}","{null f2}"); }
	@Test public void test_3835() { checkNotSubtype("{{any f1} f1}","{int f1}"); }
	@Test public void test_3836() { checkNotSubtype("{{any f1} f1}","{int f2}"); }
	@Test public void test_3837() { checkIsSubtype("{{any f1} f1}","{void f1,void f2}"); }
	@Test public void test_3838() { checkIsSubtype("{{any f1} f1}","{void f2,void f3}"); }
	@Test public void test_3839() { checkIsSubtype("{{any f1} f1}","{void f1,any f2}"); }
	@Test public void test_3840() { checkIsSubtype("{{any f1} f1}","{void f2,any f3}"); }
	@Test public void test_3841() { checkIsSubtype("{{any f1} f1}","{void f1,null f2}"); }
	@Test public void test_3842() { checkIsSubtype("{{any f1} f1}","{void f2,null f3}"); }
	@Test public void test_3843() { checkIsSubtype("{{any f1} f1}","{void f1,int f2}"); }
	@Test public void test_3844() { checkIsSubtype("{{any f1} f1}","{void f2,int f3}"); }
	@Test public void test_3845() { checkIsSubtype("{{any f1} f1}","{any f1,void f2}"); }
	@Test public void test_3846() { checkIsSubtype("{{any f1} f1}","{any f2,void f3}"); }
	@Test public void test_3847() { checkNotSubtype("{{any f1} f1}","{any f1,any f2}"); }
	@Test public void test_3848() { checkNotSubtype("{{any f1} f1}","{any f2,any f3}"); }
	@Test public void test_3849() { checkNotSubtype("{{any f1} f1}","{any f1,null f2}"); }
	@Test public void test_3850() { checkNotSubtype("{{any f1} f1}","{any f2,null f3}"); }
	@Test public void test_3851() { checkNotSubtype("{{any f1} f1}","{any f1,int f2}"); }
	@Test public void test_3852() { checkNotSubtype("{{any f1} f1}","{any f2,int f3}"); }
	@Test public void test_3853() { checkIsSubtype("{{any f1} f1}","{null f1,void f2}"); }
	@Test public void test_3854() { checkIsSubtype("{{any f1} f1}","{null f2,void f3}"); }
	@Test public void test_3855() { checkNotSubtype("{{any f1} f1}","{null f1,any f2}"); }
	@Test public void test_3856() { checkNotSubtype("{{any f1} f1}","{null f2,any f3}"); }
	@Test public void test_3857() { checkNotSubtype("{{any f1} f1}","{null f1,null f2}"); }
	@Test public void test_3858() { checkNotSubtype("{{any f1} f1}","{null f2,null f3}"); }
	@Test public void test_3859() { checkNotSubtype("{{any f1} f1}","{null f1,int f2}"); }
	@Test public void test_3860() { checkNotSubtype("{{any f1} f1}","{null f2,int f3}"); }
	@Test public void test_3861() { checkIsSubtype("{{any f1} f1}","{int f1,void f2}"); }
	@Test public void test_3862() { checkIsSubtype("{{any f1} f1}","{int f2,void f3}"); }
	@Test public void test_3863() { checkNotSubtype("{{any f1} f1}","{int f1,any f2}"); }
	@Test public void test_3864() { checkNotSubtype("{{any f1} f1}","{int f2,any f3}"); }
	@Test public void test_3865() { checkNotSubtype("{{any f1} f1}","{int f1,null f2}"); }
	@Test public void test_3866() { checkNotSubtype("{{any f1} f1}","{int f2,null f3}"); }
	@Test public void test_3867() { checkNotSubtype("{{any f1} f1}","{int f1,int f2}"); }
	@Test public void test_3868() { checkNotSubtype("{{any f1} f1}","{int f2,int f3}"); }
	@Test public void test_3869() { checkIsSubtype("{{any f1} f1}","{{void f1} f1}"); }
	@Test public void test_3870() { checkIsSubtype("{{any f1} f1}","{{void f2} f1}"); }
	@Test public void test_3871() { checkIsSubtype("{{any f1} f1}","{{void f1} f2}"); }
	@Test public void test_3872() { checkIsSubtype("{{any f1} f1}","{{void f2} f2}"); }
	@Test public void test_3873() { checkIsSubtype("{{any f1} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_3874() { checkIsSubtype("{{any f1} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_3875() { checkIsSubtype("{{any f1} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_3876() { checkIsSubtype("{{any f1} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_3877() { checkIsSubtype("{{any f1} f1}","{{any f1} f1}"); }
	@Test public void test_3878() { checkNotSubtype("{{any f1} f1}","{{any f2} f1}"); }
	@Test public void test_3879() { checkNotSubtype("{{any f1} f1}","{{any f1} f2}"); }
	@Test public void test_3880() { checkNotSubtype("{{any f1} f1}","{{any f2} f2}"); }
	@Test public void test_3881() { checkNotSubtype("{{any f1} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_3882() { checkNotSubtype("{{any f1} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_3883() { checkNotSubtype("{{any f1} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_3884() { checkNotSubtype("{{any f1} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_3885() { checkIsSubtype("{{any f1} f1}","{{null f1} f1}"); }
	@Test public void test_3886() { checkNotSubtype("{{any f1} f1}","{{null f2} f1}"); }
	@Test public void test_3887() { checkNotSubtype("{{any f1} f1}","{{null f1} f2}"); }
	@Test public void test_3888() { checkNotSubtype("{{any f1} f1}","{{null f2} f2}"); }
	@Test public void test_3889() { checkNotSubtype("{{any f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_3890() { checkNotSubtype("{{any f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_3891() { checkNotSubtype("{{any f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_3892() { checkNotSubtype("{{any f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_3893() { checkIsSubtype("{{any f1} f1}","{{int f1} f1}"); }
	@Test public void test_3894() { checkNotSubtype("{{any f1} f1}","{{int f2} f1}"); }
	@Test public void test_3895() { checkNotSubtype("{{any f1} f1}","{{int f1} f2}"); }
	@Test public void test_3896() { checkNotSubtype("{{any f1} f1}","{{int f2} f2}"); }
	@Test public void test_3897() { checkNotSubtype("{{any f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_3898() { checkNotSubtype("{{any f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_3899() { checkNotSubtype("{{any f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_3900() { checkNotSubtype("{{any f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_3901() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_3902() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_3903() { checkNotSubtype("{{any f2} f1}","int"); }
	@Test public void test_3904() { checkIsSubtype("{{any f2} f1}","{void f1}"); }
	@Test public void test_3905() { checkIsSubtype("{{any f2} f1}","{void f2}"); }
	@Test public void test_3906() { checkNotSubtype("{{any f2} f1}","{any f1}"); }
	@Test public void test_3907() { checkNotSubtype("{{any f2} f1}","{any f2}"); }
	@Test public void test_3908() { checkNotSubtype("{{any f2} f1}","{null f1}"); }
	@Test public void test_3909() { checkNotSubtype("{{any f2} f1}","{null f2}"); }
	@Test public void test_3910() { checkNotSubtype("{{any f2} f1}","{int f1}"); }
	@Test public void test_3911() { checkNotSubtype("{{any f2} f1}","{int f2}"); }
	@Test public void test_3912() { checkIsSubtype("{{any f2} f1}","{void f1,void f2}"); }
	@Test public void test_3913() { checkIsSubtype("{{any f2} f1}","{void f2,void f3}"); }
	@Test public void test_3914() { checkIsSubtype("{{any f2} f1}","{void f1,any f2}"); }
	@Test public void test_3915() { checkIsSubtype("{{any f2} f1}","{void f2,any f3}"); }
	@Test public void test_3916() { checkIsSubtype("{{any f2} f1}","{void f1,null f2}"); }
	@Test public void test_3917() { checkIsSubtype("{{any f2} f1}","{void f2,null f3}"); }
	@Test public void test_3918() { checkIsSubtype("{{any f2} f1}","{void f1,int f2}"); }
	@Test public void test_3919() { checkIsSubtype("{{any f2} f1}","{void f2,int f3}"); }
	@Test public void test_3920() { checkIsSubtype("{{any f2} f1}","{any f1,void f2}"); }
	@Test public void test_3921() { checkIsSubtype("{{any f2} f1}","{any f2,void f3}"); }
	@Test public void test_3922() { checkNotSubtype("{{any f2} f1}","{any f1,any f2}"); }
	@Test public void test_3923() { checkNotSubtype("{{any f2} f1}","{any f2,any f3}"); }
	@Test public void test_3924() { checkNotSubtype("{{any f2} f1}","{any f1,null f2}"); }
	@Test public void test_3925() { checkNotSubtype("{{any f2} f1}","{any f2,null f3}"); }
	@Test public void test_3926() { checkNotSubtype("{{any f2} f1}","{any f1,int f2}"); }
	@Test public void test_3927() { checkNotSubtype("{{any f2} f1}","{any f2,int f3}"); }
	@Test public void test_3928() { checkIsSubtype("{{any f2} f1}","{null f1,void f2}"); }
	@Test public void test_3929() { checkIsSubtype("{{any f2} f1}","{null f2,void f3}"); }
	@Test public void test_3930() { checkNotSubtype("{{any f2} f1}","{null f1,any f2}"); }
	@Test public void test_3931() { checkNotSubtype("{{any f2} f1}","{null f2,any f3}"); }
	@Test public void test_3932() { checkNotSubtype("{{any f2} f1}","{null f1,null f2}"); }
	@Test public void test_3933() { checkNotSubtype("{{any f2} f1}","{null f2,null f3}"); }
	@Test public void test_3934() { checkNotSubtype("{{any f2} f1}","{null f1,int f2}"); }
	@Test public void test_3935() { checkNotSubtype("{{any f2} f1}","{null f2,int f3}"); }
	@Test public void test_3936() { checkIsSubtype("{{any f2} f1}","{int f1,void f2}"); }
	@Test public void test_3937() { checkIsSubtype("{{any f2} f1}","{int f2,void f3}"); }
	@Test public void test_3938() { checkNotSubtype("{{any f2} f1}","{int f1,any f2}"); }
	@Test public void test_3939() { checkNotSubtype("{{any f2} f1}","{int f2,any f3}"); }
	@Test public void test_3940() { checkNotSubtype("{{any f2} f1}","{int f1,null f2}"); }
	@Test public void test_3941() { checkNotSubtype("{{any f2} f1}","{int f2,null f3}"); }
	@Test public void test_3942() { checkNotSubtype("{{any f2} f1}","{int f1,int f2}"); }
	@Test public void test_3943() { checkNotSubtype("{{any f2} f1}","{int f2,int f3}"); }
	@Test public void test_3944() { checkIsSubtype("{{any f2} f1}","{{void f1} f1}"); }
	@Test public void test_3945() { checkIsSubtype("{{any f2} f1}","{{void f2} f1}"); }
	@Test public void test_3946() { checkIsSubtype("{{any f2} f1}","{{void f1} f2}"); }
	@Test public void test_3947() { checkIsSubtype("{{any f2} f1}","{{void f2} f2}"); }
	@Test public void test_3948() { checkIsSubtype("{{any f2} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_3949() { checkIsSubtype("{{any f2} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_3950() { checkIsSubtype("{{any f2} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_3951() { checkIsSubtype("{{any f2} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_3952() { checkNotSubtype("{{any f2} f1}","{{any f1} f1}"); }
	@Test public void test_3953() { checkIsSubtype("{{any f2} f1}","{{any f2} f1}"); }
	@Test public void test_3954() { checkNotSubtype("{{any f2} f1}","{{any f1} f2}"); }
	@Test public void test_3955() { checkNotSubtype("{{any f2} f1}","{{any f2} f2}"); }
	@Test public void test_3956() { checkNotSubtype("{{any f2} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_3957() { checkNotSubtype("{{any f2} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_3958() { checkNotSubtype("{{any f2} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_3959() { checkNotSubtype("{{any f2} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_3960() { checkNotSubtype("{{any f2} f1}","{{null f1} f1}"); }
	@Test public void test_3961() { checkIsSubtype("{{any f2} f1}","{{null f2} f1}"); }
	@Test public void test_3962() { checkNotSubtype("{{any f2} f1}","{{null f1} f2}"); }
	@Test public void test_3963() { checkNotSubtype("{{any f2} f1}","{{null f2} f2}"); }
	@Test public void test_3964() { checkNotSubtype("{{any f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_3965() { checkNotSubtype("{{any f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_3966() { checkNotSubtype("{{any f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_3967() { checkNotSubtype("{{any f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_3968() { checkNotSubtype("{{any f2} f1}","{{int f1} f1}"); }
	@Test public void test_3969() { checkIsSubtype("{{any f2} f1}","{{int f2} f1}"); }
	@Test public void test_3970() { checkNotSubtype("{{any f2} f1}","{{int f1} f2}"); }
	@Test public void test_3971() { checkNotSubtype("{{any f2} f1}","{{int f2} f2}"); }
	@Test public void test_3972() { checkNotSubtype("{{any f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_3973() { checkNotSubtype("{{any f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_3974() { checkNotSubtype("{{any f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_3975() { checkNotSubtype("{{any f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_3976() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_3977() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_3978() { checkNotSubtype("{{any f1} f2}","int"); }
	@Test public void test_3979() { checkIsSubtype("{{any f1} f2}","{void f1}"); }
	@Test public void test_3980() { checkIsSubtype("{{any f1} f2}","{void f2}"); }
	@Test public void test_3981() { checkNotSubtype("{{any f1} f2}","{any f1}"); }
	@Test public void test_3982() { checkNotSubtype("{{any f1} f2}","{any f2}"); }
	@Test public void test_3983() { checkNotSubtype("{{any f1} f2}","{null f1}"); }
	@Test public void test_3984() { checkNotSubtype("{{any f1} f2}","{null f2}"); }
	@Test public void test_3985() { checkNotSubtype("{{any f1} f2}","{int f1}"); }
	@Test public void test_3986() { checkNotSubtype("{{any f1} f2}","{int f2}"); }
	@Test public void test_3987() { checkIsSubtype("{{any f1} f2}","{void f1,void f2}"); }
	@Test public void test_3988() { checkIsSubtype("{{any f1} f2}","{void f2,void f3}"); }
	@Test public void test_3989() { checkIsSubtype("{{any f1} f2}","{void f1,any f2}"); }
	@Test public void test_3990() { checkIsSubtype("{{any f1} f2}","{void f2,any f3}"); }
	@Test public void test_3991() { checkIsSubtype("{{any f1} f2}","{void f1,null f2}"); }
	@Test public void test_3992() { checkIsSubtype("{{any f1} f2}","{void f2,null f3}"); }
	@Test public void test_3993() { checkIsSubtype("{{any f1} f2}","{void f1,int f2}"); }
	@Test public void test_3994() { checkIsSubtype("{{any f1} f2}","{void f2,int f3}"); }
	@Test public void test_3995() { checkIsSubtype("{{any f1} f2}","{any f1,void f2}"); }
	@Test public void test_3996() { checkIsSubtype("{{any f1} f2}","{any f2,void f3}"); }
	@Test public void test_3997() { checkNotSubtype("{{any f1} f2}","{any f1,any f2}"); }
	@Test public void test_3998() { checkNotSubtype("{{any f1} f2}","{any f2,any f3}"); }
	@Test public void test_3999() { checkNotSubtype("{{any f1} f2}","{any f1,null f2}"); }
	@Test public void test_4000() { checkNotSubtype("{{any f1} f2}","{any f2,null f3}"); }
	@Test public void test_4001() { checkNotSubtype("{{any f1} f2}","{any f1,int f2}"); }
	@Test public void test_4002() { checkNotSubtype("{{any f1} f2}","{any f2,int f3}"); }
	@Test public void test_4003() { checkIsSubtype("{{any f1} f2}","{null f1,void f2}"); }
	@Test public void test_4004() { checkIsSubtype("{{any f1} f2}","{null f2,void f3}"); }
	@Test public void test_4005() { checkNotSubtype("{{any f1} f2}","{null f1,any f2}"); }
	@Test public void test_4006() { checkNotSubtype("{{any f1} f2}","{null f2,any f3}"); }
	@Test public void test_4007() { checkNotSubtype("{{any f1} f2}","{null f1,null f2}"); }
	@Test public void test_4008() { checkNotSubtype("{{any f1} f2}","{null f2,null f3}"); }
	@Test public void test_4009() { checkNotSubtype("{{any f1} f2}","{null f1,int f2}"); }
	@Test public void test_4010() { checkNotSubtype("{{any f1} f2}","{null f2,int f3}"); }
	@Test public void test_4011() { checkIsSubtype("{{any f1} f2}","{int f1,void f2}"); }
	@Test public void test_4012() { checkIsSubtype("{{any f1} f2}","{int f2,void f3}"); }
	@Test public void test_4013() { checkNotSubtype("{{any f1} f2}","{int f1,any f2}"); }
	@Test public void test_4014() { checkNotSubtype("{{any f1} f2}","{int f2,any f3}"); }
	@Test public void test_4015() { checkNotSubtype("{{any f1} f2}","{int f1,null f2}"); }
	@Test public void test_4016() { checkNotSubtype("{{any f1} f2}","{int f2,null f3}"); }
	@Test public void test_4017() { checkNotSubtype("{{any f1} f2}","{int f1,int f2}"); }
	@Test public void test_4018() { checkNotSubtype("{{any f1} f2}","{int f2,int f3}"); }
	@Test public void test_4019() { checkIsSubtype("{{any f1} f2}","{{void f1} f1}"); }
	@Test public void test_4020() { checkIsSubtype("{{any f1} f2}","{{void f2} f1}"); }
	@Test public void test_4021() { checkIsSubtype("{{any f1} f2}","{{void f1} f2}"); }
	@Test public void test_4022() { checkIsSubtype("{{any f1} f2}","{{void f2} f2}"); }
	@Test public void test_4023() { checkIsSubtype("{{any f1} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4024() { checkIsSubtype("{{any f1} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4025() { checkIsSubtype("{{any f1} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4026() { checkIsSubtype("{{any f1} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4027() { checkNotSubtype("{{any f1} f2}","{{any f1} f1}"); }
	@Test public void test_4028() { checkNotSubtype("{{any f1} f2}","{{any f2} f1}"); }
	@Test public void test_4029() { checkIsSubtype("{{any f1} f2}","{{any f1} f2}"); }
	@Test public void test_4030() { checkNotSubtype("{{any f1} f2}","{{any f2} f2}"); }
	@Test public void test_4031() { checkNotSubtype("{{any f1} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4032() { checkNotSubtype("{{any f1} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4033() { checkNotSubtype("{{any f1} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4034() { checkNotSubtype("{{any f1} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4035() { checkNotSubtype("{{any f1} f2}","{{null f1} f1}"); }
	@Test public void test_4036() { checkNotSubtype("{{any f1} f2}","{{null f2} f1}"); }
	@Test public void test_4037() { checkIsSubtype("{{any f1} f2}","{{null f1} f2}"); }
	@Test public void test_4038() { checkNotSubtype("{{any f1} f2}","{{null f2} f2}"); }
	@Test public void test_4039() { checkNotSubtype("{{any f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4040() { checkNotSubtype("{{any f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4041() { checkNotSubtype("{{any f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4042() { checkNotSubtype("{{any f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4043() { checkNotSubtype("{{any f1} f2}","{{int f1} f1}"); }
	@Test public void test_4044() { checkNotSubtype("{{any f1} f2}","{{int f2} f1}"); }
	@Test public void test_4045() { checkIsSubtype("{{any f1} f2}","{{int f1} f2}"); }
	@Test public void test_4046() { checkNotSubtype("{{any f1} f2}","{{int f2} f2}"); }
	@Test public void test_4047() { checkNotSubtype("{{any f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4048() { checkNotSubtype("{{any f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4049() { checkNotSubtype("{{any f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4050() { checkNotSubtype("{{any f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4051() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_4052() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_4053() { checkNotSubtype("{{any f2} f2}","int"); }
	@Test public void test_4054() { checkIsSubtype("{{any f2} f2}","{void f1}"); }
	@Test public void test_4055() { checkIsSubtype("{{any f2} f2}","{void f2}"); }
	@Test public void test_4056() { checkNotSubtype("{{any f2} f2}","{any f1}"); }
	@Test public void test_4057() { checkNotSubtype("{{any f2} f2}","{any f2}"); }
	@Test public void test_4058() { checkNotSubtype("{{any f2} f2}","{null f1}"); }
	@Test public void test_4059() { checkNotSubtype("{{any f2} f2}","{null f2}"); }
	@Test public void test_4060() { checkNotSubtype("{{any f2} f2}","{int f1}"); }
	@Test public void test_4061() { checkNotSubtype("{{any f2} f2}","{int f2}"); }
	@Test public void test_4062() { checkIsSubtype("{{any f2} f2}","{void f1,void f2}"); }
	@Test public void test_4063() { checkIsSubtype("{{any f2} f2}","{void f2,void f3}"); }
	@Test public void test_4064() { checkIsSubtype("{{any f2} f2}","{void f1,any f2}"); }
	@Test public void test_4065() { checkIsSubtype("{{any f2} f2}","{void f2,any f3}"); }
	@Test public void test_4066() { checkIsSubtype("{{any f2} f2}","{void f1,null f2}"); }
	@Test public void test_4067() { checkIsSubtype("{{any f2} f2}","{void f2,null f3}"); }
	@Test public void test_4068() { checkIsSubtype("{{any f2} f2}","{void f1,int f2}"); }
	@Test public void test_4069() { checkIsSubtype("{{any f2} f2}","{void f2,int f3}"); }
	@Test public void test_4070() { checkIsSubtype("{{any f2} f2}","{any f1,void f2}"); }
	@Test public void test_4071() { checkIsSubtype("{{any f2} f2}","{any f2,void f3}"); }
	@Test public void test_4072() { checkNotSubtype("{{any f2} f2}","{any f1,any f2}"); }
	@Test public void test_4073() { checkNotSubtype("{{any f2} f2}","{any f2,any f3}"); }
	@Test public void test_4074() { checkNotSubtype("{{any f2} f2}","{any f1,null f2}"); }
	@Test public void test_4075() { checkNotSubtype("{{any f2} f2}","{any f2,null f3}"); }
	@Test public void test_4076() { checkNotSubtype("{{any f2} f2}","{any f1,int f2}"); }
	@Test public void test_4077() { checkNotSubtype("{{any f2} f2}","{any f2,int f3}"); }
	@Test public void test_4078() { checkIsSubtype("{{any f2} f2}","{null f1,void f2}"); }
	@Test public void test_4079() { checkIsSubtype("{{any f2} f2}","{null f2,void f3}"); }
	@Test public void test_4080() { checkNotSubtype("{{any f2} f2}","{null f1,any f2}"); }
	@Test public void test_4081() { checkNotSubtype("{{any f2} f2}","{null f2,any f3}"); }
	@Test public void test_4082() { checkNotSubtype("{{any f2} f2}","{null f1,null f2}"); }
	@Test public void test_4083() { checkNotSubtype("{{any f2} f2}","{null f2,null f3}"); }
	@Test public void test_4084() { checkNotSubtype("{{any f2} f2}","{null f1,int f2}"); }
	@Test public void test_4085() { checkNotSubtype("{{any f2} f2}","{null f2,int f3}"); }
	@Test public void test_4086() { checkIsSubtype("{{any f2} f2}","{int f1,void f2}"); }
	@Test public void test_4087() { checkIsSubtype("{{any f2} f2}","{int f2,void f3}"); }
	@Test public void test_4088() { checkNotSubtype("{{any f2} f2}","{int f1,any f2}"); }
	@Test public void test_4089() { checkNotSubtype("{{any f2} f2}","{int f2,any f3}"); }
	@Test public void test_4090() { checkNotSubtype("{{any f2} f2}","{int f1,null f2}"); }
	@Test public void test_4091() { checkNotSubtype("{{any f2} f2}","{int f2,null f3}"); }
	@Test public void test_4092() { checkNotSubtype("{{any f2} f2}","{int f1,int f2}"); }
	@Test public void test_4093() { checkNotSubtype("{{any f2} f2}","{int f2,int f3}"); }
	@Test public void test_4094() { checkIsSubtype("{{any f2} f2}","{{void f1} f1}"); }
	@Test public void test_4095() { checkIsSubtype("{{any f2} f2}","{{void f2} f1}"); }
	@Test public void test_4096() { checkIsSubtype("{{any f2} f2}","{{void f1} f2}"); }
	@Test public void test_4097() { checkIsSubtype("{{any f2} f2}","{{void f2} f2}"); }
	@Test public void test_4098() { checkIsSubtype("{{any f2} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4099() { checkIsSubtype("{{any f2} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4100() { checkIsSubtype("{{any f2} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4101() { checkIsSubtype("{{any f2} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4102() { checkNotSubtype("{{any f2} f2}","{{any f1} f1}"); }
	@Test public void test_4103() { checkNotSubtype("{{any f2} f2}","{{any f2} f1}"); }
	@Test public void test_4104() { checkNotSubtype("{{any f2} f2}","{{any f1} f2}"); }
	@Test public void test_4105() { checkIsSubtype("{{any f2} f2}","{{any f2} f2}"); }
	@Test public void test_4106() { checkNotSubtype("{{any f2} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4107() { checkNotSubtype("{{any f2} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4108() { checkNotSubtype("{{any f2} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4109() { checkNotSubtype("{{any f2} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4110() { checkNotSubtype("{{any f2} f2}","{{null f1} f1}"); }
	@Test public void test_4111() { checkNotSubtype("{{any f2} f2}","{{null f2} f1}"); }
	@Test public void test_4112() { checkNotSubtype("{{any f2} f2}","{{null f1} f2}"); }
	@Test public void test_4113() { checkIsSubtype("{{any f2} f2}","{{null f2} f2}"); }
	@Test public void test_4114() { checkNotSubtype("{{any f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4115() { checkNotSubtype("{{any f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4116() { checkNotSubtype("{{any f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4117() { checkNotSubtype("{{any f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4118() { checkNotSubtype("{{any f2} f2}","{{int f1} f1}"); }
	@Test public void test_4119() { checkNotSubtype("{{any f2} f2}","{{int f2} f1}"); }
	@Test public void test_4120() { checkNotSubtype("{{any f2} f2}","{{int f1} f2}"); }
	@Test public void test_4121() { checkIsSubtype("{{any f2} f2}","{{int f2} f2}"); }
	@Test public void test_4122() { checkNotSubtype("{{any f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4123() { checkNotSubtype("{{any f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4124() { checkNotSubtype("{{any f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4125() { checkNotSubtype("{{any f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4126() { checkNotSubtype("{{any f1} f1,any f2}","any"); }
	@Test public void test_4127() { checkNotSubtype("{{any f1} f1,any f2}","null"); }
	@Test public void test_4128() { checkNotSubtype("{{any f1} f1,any f2}","int"); }
	@Test public void test_4129() { checkIsSubtype("{{any f1} f1,any f2}","{void f1}"); }
	@Test public void test_4130() { checkIsSubtype("{{any f1} f1,any f2}","{void f2}"); }
	@Test public void test_4131() { checkNotSubtype("{{any f1} f1,any f2}","{any f1}"); }
	@Test public void test_4132() { checkNotSubtype("{{any f1} f1,any f2}","{any f2}"); }
	@Test public void test_4133() { checkNotSubtype("{{any f1} f1,any f2}","{null f1}"); }
	@Test public void test_4134() { checkNotSubtype("{{any f1} f1,any f2}","{null f2}"); }
	@Test public void test_4135() { checkNotSubtype("{{any f1} f1,any f2}","{int f1}"); }
	@Test public void test_4136() { checkNotSubtype("{{any f1} f1,any f2}","{int f2}"); }
	@Test public void test_4137() { checkIsSubtype("{{any f1} f1,any f2}","{void f1,void f2}"); }
	@Test public void test_4138() { checkIsSubtype("{{any f1} f1,any f2}","{void f2,void f3}"); }
	@Test public void test_4139() { checkIsSubtype("{{any f1} f1,any f2}","{void f1,any f2}"); }
	@Test public void test_4140() { checkIsSubtype("{{any f1} f1,any f2}","{void f2,any f3}"); }
	@Test public void test_4141() { checkIsSubtype("{{any f1} f1,any f2}","{void f1,null f2}"); }
	@Test public void test_4142() { checkIsSubtype("{{any f1} f1,any f2}","{void f2,null f3}"); }
	@Test public void test_4143() { checkIsSubtype("{{any f1} f1,any f2}","{void f1,int f2}"); }
	@Test public void test_4144() { checkIsSubtype("{{any f1} f1,any f2}","{void f2,int f3}"); }
	@Test public void test_4145() { checkIsSubtype("{{any f1} f1,any f2}","{any f1,void f2}"); }
	@Test public void test_4146() { checkIsSubtype("{{any f1} f1,any f2}","{any f2,void f3}"); }
	@Test public void test_4147() { checkNotSubtype("{{any f1} f1,any f2}","{any f1,any f2}"); }
	@Test public void test_4148() { checkNotSubtype("{{any f1} f1,any f2}","{any f2,any f3}"); }
	@Test public void test_4149() { checkNotSubtype("{{any f1} f1,any f2}","{any f1,null f2}"); }
	@Test public void test_4150() { checkNotSubtype("{{any f1} f1,any f2}","{any f2,null f3}"); }
	@Test public void test_4151() { checkNotSubtype("{{any f1} f1,any f2}","{any f1,int f2}"); }
	@Test public void test_4152() { checkNotSubtype("{{any f1} f1,any f2}","{any f2,int f3}"); }
	@Test public void test_4153() { checkIsSubtype("{{any f1} f1,any f2}","{null f1,void f2}"); }
	@Test public void test_4154() { checkIsSubtype("{{any f1} f1,any f2}","{null f2,void f3}"); }
	@Test public void test_4155() { checkNotSubtype("{{any f1} f1,any f2}","{null f1,any f2}"); }
	@Test public void test_4156() { checkNotSubtype("{{any f1} f1,any f2}","{null f2,any f3}"); }
	@Test public void test_4157() { checkNotSubtype("{{any f1} f1,any f2}","{null f1,null f2}"); }
	@Test public void test_4158() { checkNotSubtype("{{any f1} f1,any f2}","{null f2,null f3}"); }
	@Test public void test_4159() { checkNotSubtype("{{any f1} f1,any f2}","{null f1,int f2}"); }
	@Test public void test_4160() { checkNotSubtype("{{any f1} f1,any f2}","{null f2,int f3}"); }
	@Test public void test_4161() { checkIsSubtype("{{any f1} f1,any f2}","{int f1,void f2}"); }
	@Test public void test_4162() { checkIsSubtype("{{any f1} f1,any f2}","{int f2,void f3}"); }
	@Test public void test_4163() { checkNotSubtype("{{any f1} f1,any f2}","{int f1,any f2}"); }
	@Test public void test_4164() { checkNotSubtype("{{any f1} f1,any f2}","{int f2,any f3}"); }
	@Test public void test_4165() { checkNotSubtype("{{any f1} f1,any f2}","{int f1,null f2}"); }
	@Test public void test_4166() { checkNotSubtype("{{any f1} f1,any f2}","{int f2,null f3}"); }
	@Test public void test_4167() { checkNotSubtype("{{any f1} f1,any f2}","{int f1,int f2}"); }
	@Test public void test_4168() { checkNotSubtype("{{any f1} f1,any f2}","{int f2,int f3}"); }
	@Test public void test_4169() { checkIsSubtype("{{any f1} f1,any f2}","{{void f1} f1}"); }
	@Test public void test_4170() { checkIsSubtype("{{any f1} f1,any f2}","{{void f2} f1}"); }
	@Test public void test_4171() { checkIsSubtype("{{any f1} f1,any f2}","{{void f1} f2}"); }
	@Test public void test_4172() { checkIsSubtype("{{any f1} f1,any f2}","{{void f2} f2}"); }
	@Test public void test_4173() { checkIsSubtype("{{any f1} f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4174() { checkIsSubtype("{{any f1} f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4175() { checkIsSubtype("{{any f1} f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4176() { checkIsSubtype("{{any f1} f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4177() { checkNotSubtype("{{any f1} f1,any f2}","{{any f1} f1}"); }
	@Test public void test_4178() { checkNotSubtype("{{any f1} f1,any f2}","{{any f2} f1}"); }
	@Test public void test_4179() { checkNotSubtype("{{any f1} f1,any f2}","{{any f1} f2}"); }
	@Test public void test_4180() { checkNotSubtype("{{any f1} f1,any f2}","{{any f2} f2}"); }
	@Test public void test_4181() { checkIsSubtype("{{any f1} f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4182() { checkNotSubtype("{{any f1} f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4183() { checkNotSubtype("{{any f1} f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4184() { checkNotSubtype("{{any f1} f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4185() { checkNotSubtype("{{any f1} f1,any f2}","{{null f1} f1}"); }
	@Test public void test_4186() { checkNotSubtype("{{any f1} f1,any f2}","{{null f2} f1}"); }
	@Test public void test_4187() { checkNotSubtype("{{any f1} f1,any f2}","{{null f1} f2}"); }
	@Test public void test_4188() { checkNotSubtype("{{any f1} f1,any f2}","{{null f2} f2}"); }
	@Test public void test_4189() { checkIsSubtype("{{any f1} f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4190() { checkNotSubtype("{{any f1} f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4191() { checkNotSubtype("{{any f1} f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4192() { checkNotSubtype("{{any f1} f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4193() { checkNotSubtype("{{any f1} f1,any f2}","{{int f1} f1}"); }
	@Test public void test_4194() { checkNotSubtype("{{any f1} f1,any f2}","{{int f2} f1}"); }
	@Test public void test_4195() { checkNotSubtype("{{any f1} f1,any f2}","{{int f1} f2}"); }
	@Test public void test_4196() { checkNotSubtype("{{any f1} f1,any f2}","{{int f2} f2}"); }
	@Test public void test_4197() { checkIsSubtype("{{any f1} f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4198() { checkNotSubtype("{{any f1} f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4199() { checkNotSubtype("{{any f1} f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4200() { checkNotSubtype("{{any f1} f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4201() { checkNotSubtype("{{any f2} f1,any f2}","any"); }
	@Test public void test_4202() { checkNotSubtype("{{any f2} f1,any f2}","null"); }
	@Test public void test_4203() { checkNotSubtype("{{any f2} f1,any f2}","int"); }
	@Test public void test_4204() { checkIsSubtype("{{any f2} f1,any f2}","{void f1}"); }
	@Test public void test_4205() { checkIsSubtype("{{any f2} f1,any f2}","{void f2}"); }
	@Test public void test_4206() { checkNotSubtype("{{any f2} f1,any f2}","{any f1}"); }
	@Test public void test_4207() { checkNotSubtype("{{any f2} f1,any f2}","{any f2}"); }
	@Test public void test_4208() { checkNotSubtype("{{any f2} f1,any f2}","{null f1}"); }
	@Test public void test_4209() { checkNotSubtype("{{any f2} f1,any f2}","{null f2}"); }
	@Test public void test_4210() { checkNotSubtype("{{any f2} f1,any f2}","{int f1}"); }
	@Test public void test_4211() { checkNotSubtype("{{any f2} f1,any f2}","{int f2}"); }
	@Test public void test_4212() { checkIsSubtype("{{any f2} f1,any f2}","{void f1,void f2}"); }
	@Test public void test_4213() { checkIsSubtype("{{any f2} f1,any f2}","{void f2,void f3}"); }
	@Test public void test_4214() { checkIsSubtype("{{any f2} f1,any f2}","{void f1,any f2}"); }
	@Test public void test_4215() { checkIsSubtype("{{any f2} f1,any f2}","{void f2,any f3}"); }
	@Test public void test_4216() { checkIsSubtype("{{any f2} f1,any f2}","{void f1,null f2}"); }
	@Test public void test_4217() { checkIsSubtype("{{any f2} f1,any f2}","{void f2,null f3}"); }
	@Test public void test_4218() { checkIsSubtype("{{any f2} f1,any f2}","{void f1,int f2}"); }
	@Test public void test_4219() { checkIsSubtype("{{any f2} f1,any f2}","{void f2,int f3}"); }
	@Test public void test_4220() { checkIsSubtype("{{any f2} f1,any f2}","{any f1,void f2}"); }
	@Test public void test_4221() { checkIsSubtype("{{any f2} f1,any f2}","{any f2,void f3}"); }
	@Test public void test_4222() { checkNotSubtype("{{any f2} f1,any f2}","{any f1,any f2}"); }
	@Test public void test_4223() { checkNotSubtype("{{any f2} f1,any f2}","{any f2,any f3}"); }
	@Test public void test_4224() { checkNotSubtype("{{any f2} f1,any f2}","{any f1,null f2}"); }
	@Test public void test_4225() { checkNotSubtype("{{any f2} f1,any f2}","{any f2,null f3}"); }
	@Test public void test_4226() { checkNotSubtype("{{any f2} f1,any f2}","{any f1,int f2}"); }
	@Test public void test_4227() { checkNotSubtype("{{any f2} f1,any f2}","{any f2,int f3}"); }
	@Test public void test_4228() { checkIsSubtype("{{any f2} f1,any f2}","{null f1,void f2}"); }
	@Test public void test_4229() { checkIsSubtype("{{any f2} f1,any f2}","{null f2,void f3}"); }
	@Test public void test_4230() { checkNotSubtype("{{any f2} f1,any f2}","{null f1,any f2}"); }
	@Test public void test_4231() { checkNotSubtype("{{any f2} f1,any f2}","{null f2,any f3}"); }
	@Test public void test_4232() { checkNotSubtype("{{any f2} f1,any f2}","{null f1,null f2}"); }
	@Test public void test_4233() { checkNotSubtype("{{any f2} f1,any f2}","{null f2,null f3}"); }
	@Test public void test_4234() { checkNotSubtype("{{any f2} f1,any f2}","{null f1,int f2}"); }
	@Test public void test_4235() { checkNotSubtype("{{any f2} f1,any f2}","{null f2,int f3}"); }
	@Test public void test_4236() { checkIsSubtype("{{any f2} f1,any f2}","{int f1,void f2}"); }
	@Test public void test_4237() { checkIsSubtype("{{any f2} f1,any f2}","{int f2,void f3}"); }
	@Test public void test_4238() { checkNotSubtype("{{any f2} f1,any f2}","{int f1,any f2}"); }
	@Test public void test_4239() { checkNotSubtype("{{any f2} f1,any f2}","{int f2,any f3}"); }
	@Test public void test_4240() { checkNotSubtype("{{any f2} f1,any f2}","{int f1,null f2}"); }
	@Test public void test_4241() { checkNotSubtype("{{any f2} f1,any f2}","{int f2,null f3}"); }
	@Test public void test_4242() { checkNotSubtype("{{any f2} f1,any f2}","{int f1,int f2}"); }
	@Test public void test_4243() { checkNotSubtype("{{any f2} f1,any f2}","{int f2,int f3}"); }
	@Test public void test_4244() { checkIsSubtype("{{any f2} f1,any f2}","{{void f1} f1}"); }
	@Test public void test_4245() { checkIsSubtype("{{any f2} f1,any f2}","{{void f2} f1}"); }
	@Test public void test_4246() { checkIsSubtype("{{any f2} f1,any f2}","{{void f1} f2}"); }
	@Test public void test_4247() { checkIsSubtype("{{any f2} f1,any f2}","{{void f2} f2}"); }
	@Test public void test_4248() { checkIsSubtype("{{any f2} f1,any f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4249() { checkIsSubtype("{{any f2} f1,any f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4250() { checkIsSubtype("{{any f2} f1,any f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4251() { checkIsSubtype("{{any f2} f1,any f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4252() { checkNotSubtype("{{any f2} f1,any f2}","{{any f1} f1}"); }
	@Test public void test_4253() { checkNotSubtype("{{any f2} f1,any f2}","{{any f2} f1}"); }
	@Test public void test_4254() { checkNotSubtype("{{any f2} f1,any f2}","{{any f1} f2}"); }
	@Test public void test_4255() { checkNotSubtype("{{any f2} f1,any f2}","{{any f2} f2}"); }
	@Test public void test_4256() { checkNotSubtype("{{any f2} f1,any f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4257() { checkIsSubtype("{{any f2} f1,any f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4258() { checkNotSubtype("{{any f2} f1,any f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4259() { checkNotSubtype("{{any f2} f1,any f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4260() { checkNotSubtype("{{any f2} f1,any f2}","{{null f1} f1}"); }
	@Test public void test_4261() { checkNotSubtype("{{any f2} f1,any f2}","{{null f2} f1}"); }
	@Test public void test_4262() { checkNotSubtype("{{any f2} f1,any f2}","{{null f1} f2}"); }
	@Test public void test_4263() { checkNotSubtype("{{any f2} f1,any f2}","{{null f2} f2}"); }
	@Test public void test_4264() { checkNotSubtype("{{any f2} f1,any f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4265() { checkIsSubtype("{{any f2} f1,any f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4266() { checkNotSubtype("{{any f2} f1,any f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4267() { checkNotSubtype("{{any f2} f1,any f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4268() { checkNotSubtype("{{any f2} f1,any f2}","{{int f1} f1}"); }
	@Test public void test_4269() { checkNotSubtype("{{any f2} f1,any f2}","{{int f2} f1}"); }
	@Test public void test_4270() { checkNotSubtype("{{any f2} f1,any f2}","{{int f1} f2}"); }
	@Test public void test_4271() { checkNotSubtype("{{any f2} f1,any f2}","{{int f2} f2}"); }
	@Test public void test_4272() { checkNotSubtype("{{any f2} f1,any f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4273() { checkIsSubtype("{{any f2} f1,any f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4274() { checkNotSubtype("{{any f2} f1,any f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4275() { checkNotSubtype("{{any f2} f1,any f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4276() { checkNotSubtype("{{any f1} f2,any f3}","any"); }
	@Test public void test_4277() { checkNotSubtype("{{any f1} f2,any f3}","null"); }
	@Test public void test_4278() { checkNotSubtype("{{any f1} f2,any f3}","int"); }
	@Test public void test_4279() { checkIsSubtype("{{any f1} f2,any f3}","{void f1}"); }
	@Test public void test_4280() { checkIsSubtype("{{any f1} f2,any f3}","{void f2}"); }
	@Test public void test_4281() { checkNotSubtype("{{any f1} f2,any f3}","{any f1}"); }
	@Test public void test_4282() { checkNotSubtype("{{any f1} f2,any f3}","{any f2}"); }
	@Test public void test_4283() { checkNotSubtype("{{any f1} f2,any f3}","{null f1}"); }
	@Test public void test_4284() { checkNotSubtype("{{any f1} f2,any f3}","{null f2}"); }
	@Test public void test_4285() { checkNotSubtype("{{any f1} f2,any f3}","{int f1}"); }
	@Test public void test_4286() { checkNotSubtype("{{any f1} f2,any f3}","{int f2}"); }
	@Test public void test_4287() { checkIsSubtype("{{any f1} f2,any f3}","{void f1,void f2}"); }
	@Test public void test_4288() { checkIsSubtype("{{any f1} f2,any f3}","{void f2,void f3}"); }
	@Test public void test_4289() { checkIsSubtype("{{any f1} f2,any f3}","{void f1,any f2}"); }
	@Test public void test_4290() { checkIsSubtype("{{any f1} f2,any f3}","{void f2,any f3}"); }
	@Test public void test_4291() { checkIsSubtype("{{any f1} f2,any f3}","{void f1,null f2}"); }
	@Test public void test_4292() { checkIsSubtype("{{any f1} f2,any f3}","{void f2,null f3}"); }
	@Test public void test_4293() { checkIsSubtype("{{any f1} f2,any f3}","{void f1,int f2}"); }
	@Test public void test_4294() { checkIsSubtype("{{any f1} f2,any f3}","{void f2,int f3}"); }
	@Test public void test_4295() { checkIsSubtype("{{any f1} f2,any f3}","{any f1,void f2}"); }
	@Test public void test_4296() { checkIsSubtype("{{any f1} f2,any f3}","{any f2,void f3}"); }
	@Test public void test_4297() { checkNotSubtype("{{any f1} f2,any f3}","{any f1,any f2}"); }
	@Test public void test_4298() { checkNotSubtype("{{any f1} f2,any f3}","{any f2,any f3}"); }
	@Test public void test_4299() { checkNotSubtype("{{any f1} f2,any f3}","{any f1,null f2}"); }
	@Test public void test_4300() { checkNotSubtype("{{any f1} f2,any f3}","{any f2,null f3}"); }
	@Test public void test_4301() { checkNotSubtype("{{any f1} f2,any f3}","{any f1,int f2}"); }
	@Test public void test_4302() { checkNotSubtype("{{any f1} f2,any f3}","{any f2,int f3}"); }
	@Test public void test_4303() { checkIsSubtype("{{any f1} f2,any f3}","{null f1,void f2}"); }
	@Test public void test_4304() { checkIsSubtype("{{any f1} f2,any f3}","{null f2,void f3}"); }
	@Test public void test_4305() { checkNotSubtype("{{any f1} f2,any f3}","{null f1,any f2}"); }
	@Test public void test_4306() { checkNotSubtype("{{any f1} f2,any f3}","{null f2,any f3}"); }
	@Test public void test_4307() { checkNotSubtype("{{any f1} f2,any f3}","{null f1,null f2}"); }
	@Test public void test_4308() { checkNotSubtype("{{any f1} f2,any f3}","{null f2,null f3}"); }
	@Test public void test_4309() { checkNotSubtype("{{any f1} f2,any f3}","{null f1,int f2}"); }
	@Test public void test_4310() { checkNotSubtype("{{any f1} f2,any f3}","{null f2,int f3}"); }
	@Test public void test_4311() { checkIsSubtype("{{any f1} f2,any f3}","{int f1,void f2}"); }
	@Test public void test_4312() { checkIsSubtype("{{any f1} f2,any f3}","{int f2,void f3}"); }
	@Test public void test_4313() { checkNotSubtype("{{any f1} f2,any f3}","{int f1,any f2}"); }
	@Test public void test_4314() { checkNotSubtype("{{any f1} f2,any f3}","{int f2,any f3}"); }
	@Test public void test_4315() { checkNotSubtype("{{any f1} f2,any f3}","{int f1,null f2}"); }
	@Test public void test_4316() { checkNotSubtype("{{any f1} f2,any f3}","{int f2,null f3}"); }
	@Test public void test_4317() { checkNotSubtype("{{any f1} f2,any f3}","{int f1,int f2}"); }
	@Test public void test_4318() { checkNotSubtype("{{any f1} f2,any f3}","{int f2,int f3}"); }
	@Test public void test_4319() { checkIsSubtype("{{any f1} f2,any f3}","{{void f1} f1}"); }
	@Test public void test_4320() { checkIsSubtype("{{any f1} f2,any f3}","{{void f2} f1}"); }
	@Test public void test_4321() { checkIsSubtype("{{any f1} f2,any f3}","{{void f1} f2}"); }
	@Test public void test_4322() { checkIsSubtype("{{any f1} f2,any f3}","{{void f2} f2}"); }
	@Test public void test_4323() { checkIsSubtype("{{any f1} f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_4324() { checkIsSubtype("{{any f1} f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_4325() { checkIsSubtype("{{any f1} f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_4326() { checkIsSubtype("{{any f1} f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_4327() { checkNotSubtype("{{any f1} f2,any f3}","{{any f1} f1}"); }
	@Test public void test_4328() { checkNotSubtype("{{any f1} f2,any f3}","{{any f2} f1}"); }
	@Test public void test_4329() { checkNotSubtype("{{any f1} f2,any f3}","{{any f1} f2}"); }
	@Test public void test_4330() { checkNotSubtype("{{any f1} f2,any f3}","{{any f2} f2}"); }
	@Test public void test_4331() { checkNotSubtype("{{any f1} f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_4332() { checkNotSubtype("{{any f1} f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_4333() { checkIsSubtype("{{any f1} f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_4334() { checkNotSubtype("{{any f1} f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_4335() { checkNotSubtype("{{any f1} f2,any f3}","{{null f1} f1}"); }
	@Test public void test_4336() { checkNotSubtype("{{any f1} f2,any f3}","{{null f2} f1}"); }
	@Test public void test_4337() { checkNotSubtype("{{any f1} f2,any f3}","{{null f1} f2}"); }
	@Test public void test_4338() { checkNotSubtype("{{any f1} f2,any f3}","{{null f2} f2}"); }
	@Test public void test_4339() { checkNotSubtype("{{any f1} f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_4340() { checkNotSubtype("{{any f1} f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_4341() { checkIsSubtype("{{any f1} f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_4342() { checkNotSubtype("{{any f1} f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_4343() { checkNotSubtype("{{any f1} f2,any f3}","{{int f1} f1}"); }
	@Test public void test_4344() { checkNotSubtype("{{any f1} f2,any f3}","{{int f2} f1}"); }
	@Test public void test_4345() { checkNotSubtype("{{any f1} f2,any f3}","{{int f1} f2}"); }
	@Test public void test_4346() { checkNotSubtype("{{any f1} f2,any f3}","{{int f2} f2}"); }
	@Test public void test_4347() { checkNotSubtype("{{any f1} f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_4348() { checkNotSubtype("{{any f1} f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_4349() { checkIsSubtype("{{any f1} f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_4350() { checkNotSubtype("{{any f1} f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_4351() { checkNotSubtype("{{any f2} f2,any f3}","any"); }
	@Test public void test_4352() { checkNotSubtype("{{any f2} f2,any f3}","null"); }
	@Test public void test_4353() { checkNotSubtype("{{any f2} f2,any f3}","int"); }
	@Test public void test_4354() { checkIsSubtype("{{any f2} f2,any f3}","{void f1}"); }
	@Test public void test_4355() { checkIsSubtype("{{any f2} f2,any f3}","{void f2}"); }
	@Test public void test_4356() { checkNotSubtype("{{any f2} f2,any f3}","{any f1}"); }
	@Test public void test_4357() { checkNotSubtype("{{any f2} f2,any f3}","{any f2}"); }
	@Test public void test_4358() { checkNotSubtype("{{any f2} f2,any f3}","{null f1}"); }
	@Test public void test_4359() { checkNotSubtype("{{any f2} f2,any f3}","{null f2}"); }
	@Test public void test_4360() { checkNotSubtype("{{any f2} f2,any f3}","{int f1}"); }
	@Test public void test_4361() { checkNotSubtype("{{any f2} f2,any f3}","{int f2}"); }
	@Test public void test_4362() { checkIsSubtype("{{any f2} f2,any f3}","{void f1,void f2}"); }
	@Test public void test_4363() { checkIsSubtype("{{any f2} f2,any f3}","{void f2,void f3}"); }
	@Test public void test_4364() { checkIsSubtype("{{any f2} f2,any f3}","{void f1,any f2}"); }
	@Test public void test_4365() { checkIsSubtype("{{any f2} f2,any f3}","{void f2,any f3}"); }
	@Test public void test_4366() { checkIsSubtype("{{any f2} f2,any f3}","{void f1,null f2}"); }
	@Test public void test_4367() { checkIsSubtype("{{any f2} f2,any f3}","{void f2,null f3}"); }
	@Test public void test_4368() { checkIsSubtype("{{any f2} f2,any f3}","{void f1,int f2}"); }
	@Test public void test_4369() { checkIsSubtype("{{any f2} f2,any f3}","{void f2,int f3}"); }
	@Test public void test_4370() { checkIsSubtype("{{any f2} f2,any f3}","{any f1,void f2}"); }
	@Test public void test_4371() { checkIsSubtype("{{any f2} f2,any f3}","{any f2,void f3}"); }
	@Test public void test_4372() { checkNotSubtype("{{any f2} f2,any f3}","{any f1,any f2}"); }
	@Test public void test_4373() { checkNotSubtype("{{any f2} f2,any f3}","{any f2,any f3}"); }
	@Test public void test_4374() { checkNotSubtype("{{any f2} f2,any f3}","{any f1,null f2}"); }
	@Test public void test_4375() { checkNotSubtype("{{any f2} f2,any f3}","{any f2,null f3}"); }
	@Test public void test_4376() { checkNotSubtype("{{any f2} f2,any f3}","{any f1,int f2}"); }
	@Test public void test_4377() { checkNotSubtype("{{any f2} f2,any f3}","{any f2,int f3}"); }
	@Test public void test_4378() { checkIsSubtype("{{any f2} f2,any f3}","{null f1,void f2}"); }
	@Test public void test_4379() { checkIsSubtype("{{any f2} f2,any f3}","{null f2,void f3}"); }
	@Test public void test_4380() { checkNotSubtype("{{any f2} f2,any f3}","{null f1,any f2}"); }
	@Test public void test_4381() { checkNotSubtype("{{any f2} f2,any f3}","{null f2,any f3}"); }
	@Test public void test_4382() { checkNotSubtype("{{any f2} f2,any f3}","{null f1,null f2}"); }
	@Test public void test_4383() { checkNotSubtype("{{any f2} f2,any f3}","{null f2,null f3}"); }
	@Test public void test_4384() { checkNotSubtype("{{any f2} f2,any f3}","{null f1,int f2}"); }
	@Test public void test_4385() { checkNotSubtype("{{any f2} f2,any f3}","{null f2,int f3}"); }
	@Test public void test_4386() { checkIsSubtype("{{any f2} f2,any f3}","{int f1,void f2}"); }
	@Test public void test_4387() { checkIsSubtype("{{any f2} f2,any f3}","{int f2,void f3}"); }
	@Test public void test_4388() { checkNotSubtype("{{any f2} f2,any f3}","{int f1,any f2}"); }
	@Test public void test_4389() { checkNotSubtype("{{any f2} f2,any f3}","{int f2,any f3}"); }
	@Test public void test_4390() { checkNotSubtype("{{any f2} f2,any f3}","{int f1,null f2}"); }
	@Test public void test_4391() { checkNotSubtype("{{any f2} f2,any f3}","{int f2,null f3}"); }
	@Test public void test_4392() { checkNotSubtype("{{any f2} f2,any f3}","{int f1,int f2}"); }
	@Test public void test_4393() { checkNotSubtype("{{any f2} f2,any f3}","{int f2,int f3}"); }
	@Test public void test_4394() { checkIsSubtype("{{any f2} f2,any f3}","{{void f1} f1}"); }
	@Test public void test_4395() { checkIsSubtype("{{any f2} f2,any f3}","{{void f2} f1}"); }
	@Test public void test_4396() { checkIsSubtype("{{any f2} f2,any f3}","{{void f1} f2}"); }
	@Test public void test_4397() { checkIsSubtype("{{any f2} f2,any f3}","{{void f2} f2}"); }
	@Test public void test_4398() { checkIsSubtype("{{any f2} f2,any f3}","{{void f1} f1,void f2}"); }
	@Test public void test_4399() { checkIsSubtype("{{any f2} f2,any f3}","{{void f2} f1,void f2}"); }
	@Test public void test_4400() { checkIsSubtype("{{any f2} f2,any f3}","{{void f1} f2,void f3}"); }
	@Test public void test_4401() { checkIsSubtype("{{any f2} f2,any f3}","{{void f2} f2,void f3}"); }
	@Test public void test_4402() { checkNotSubtype("{{any f2} f2,any f3}","{{any f1} f1}"); }
	@Test public void test_4403() { checkNotSubtype("{{any f2} f2,any f3}","{{any f2} f1}"); }
	@Test public void test_4404() { checkNotSubtype("{{any f2} f2,any f3}","{{any f1} f2}"); }
	@Test public void test_4405() { checkNotSubtype("{{any f2} f2,any f3}","{{any f2} f2}"); }
	@Test public void test_4406() { checkNotSubtype("{{any f2} f2,any f3}","{{any f1} f1,any f2}"); }
	@Test public void test_4407() { checkNotSubtype("{{any f2} f2,any f3}","{{any f2} f1,any f2}"); }
	@Test public void test_4408() { checkNotSubtype("{{any f2} f2,any f3}","{{any f1} f2,any f3}"); }
	@Test public void test_4409() { checkIsSubtype("{{any f2} f2,any f3}","{{any f2} f2,any f3}"); }
	@Test public void test_4410() { checkNotSubtype("{{any f2} f2,any f3}","{{null f1} f1}"); }
	@Test public void test_4411() { checkNotSubtype("{{any f2} f2,any f3}","{{null f2} f1}"); }
	@Test public void test_4412() { checkNotSubtype("{{any f2} f2,any f3}","{{null f1} f2}"); }
	@Test public void test_4413() { checkNotSubtype("{{any f2} f2,any f3}","{{null f2} f2}"); }
	@Test public void test_4414() { checkNotSubtype("{{any f2} f2,any f3}","{{null f1} f1,null f2}"); }
	@Test public void test_4415() { checkNotSubtype("{{any f2} f2,any f3}","{{null f2} f1,null f2}"); }
	@Test public void test_4416() { checkNotSubtype("{{any f2} f2,any f3}","{{null f1} f2,null f3}"); }
	@Test public void test_4417() { checkIsSubtype("{{any f2} f2,any f3}","{{null f2} f2,null f3}"); }
	@Test public void test_4418() { checkNotSubtype("{{any f2} f2,any f3}","{{int f1} f1}"); }
	@Test public void test_4419() { checkNotSubtype("{{any f2} f2,any f3}","{{int f2} f1}"); }
	@Test public void test_4420() { checkNotSubtype("{{any f2} f2,any f3}","{{int f1} f2}"); }
	@Test public void test_4421() { checkNotSubtype("{{any f2} f2,any f3}","{{int f2} f2}"); }
	@Test public void test_4422() { checkNotSubtype("{{any f2} f2,any f3}","{{int f1} f1,int f2}"); }
	@Test public void test_4423() { checkNotSubtype("{{any f2} f2,any f3}","{{int f2} f1,int f2}"); }
	@Test public void test_4424() { checkNotSubtype("{{any f2} f2,any f3}","{{int f1} f2,int f3}"); }
	@Test public void test_4425() { checkIsSubtype("{{any f2} f2,any f3}","{{int f2} f2,int f3}"); }
	@Test public void test_4426() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_4427() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_4428() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_4429() { checkIsSubtype("{{null f1} f1}","{void f1}"); }
	@Test public void test_4430() { checkIsSubtype("{{null f1} f1}","{void f2}"); }
	@Test public void test_4431() { checkNotSubtype("{{null f1} f1}","{any f1}"); }
	@Test public void test_4432() { checkNotSubtype("{{null f1} f1}","{any f2}"); }
	@Test public void test_4433() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test public void test_4434() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test public void test_4435() { checkNotSubtype("{{null f1} f1}","{int f1}"); }
	@Test public void test_4436() { checkNotSubtype("{{null f1} f1}","{int f2}"); }
	@Test public void test_4437() { checkIsSubtype("{{null f1} f1}","{void f1,void f2}"); }
	@Test public void test_4438() { checkIsSubtype("{{null f1} f1}","{void f2,void f3}"); }
	@Test public void test_4439() { checkIsSubtype("{{null f1} f1}","{void f1,any f2}"); }
	@Test public void test_4440() { checkIsSubtype("{{null f1} f1}","{void f2,any f3}"); }
	@Test public void test_4441() { checkIsSubtype("{{null f1} f1}","{void f1,null f2}"); }
	@Test public void test_4442() { checkIsSubtype("{{null f1} f1}","{void f2,null f3}"); }
	@Test public void test_4443() { checkIsSubtype("{{null f1} f1}","{void f1,int f2}"); }
	@Test public void test_4444() { checkIsSubtype("{{null f1} f1}","{void f2,int f3}"); }
	@Test public void test_4445() { checkIsSubtype("{{null f1} f1}","{any f1,void f2}"); }
	@Test public void test_4446() { checkIsSubtype("{{null f1} f1}","{any f2,void f3}"); }
	@Test public void test_4447() { checkNotSubtype("{{null f1} f1}","{any f1,any f2}"); }
	@Test public void test_4448() { checkNotSubtype("{{null f1} f1}","{any f2,any f3}"); }
	@Test public void test_4449() { checkNotSubtype("{{null f1} f1}","{any f1,null f2}"); }
	@Test public void test_4450() { checkNotSubtype("{{null f1} f1}","{any f2,null f3}"); }
	@Test public void test_4451() { checkNotSubtype("{{null f1} f1}","{any f1,int f2}"); }
	@Test public void test_4452() { checkNotSubtype("{{null f1} f1}","{any f2,int f3}"); }
	@Test public void test_4453() { checkIsSubtype("{{null f1} f1}","{null f1,void f2}"); }
	@Test public void test_4454() { checkIsSubtype("{{null f1} f1}","{null f2,void f3}"); }
	@Test public void test_4455() { checkNotSubtype("{{null f1} f1}","{null f1,any f2}"); }
	@Test public void test_4456() { checkNotSubtype("{{null f1} f1}","{null f2,any f3}"); }
	@Test public void test_4457() { checkNotSubtype("{{null f1} f1}","{null f1,null f2}"); }
	@Test public void test_4458() { checkNotSubtype("{{null f1} f1}","{null f2,null f3}"); }
	@Test public void test_4459() { checkNotSubtype("{{null f1} f1}","{null f1,int f2}"); }
	@Test public void test_4460() { checkNotSubtype("{{null f1} f1}","{null f2,int f3}"); }
	@Test public void test_4461() { checkIsSubtype("{{null f1} f1}","{int f1,void f2}"); }
	@Test public void test_4462() { checkIsSubtype("{{null f1} f1}","{int f2,void f3}"); }
	@Test public void test_4463() { checkNotSubtype("{{null f1} f1}","{int f1,any f2}"); }
	@Test public void test_4464() { checkNotSubtype("{{null f1} f1}","{int f2,any f3}"); }
	@Test public void test_4465() { checkNotSubtype("{{null f1} f1}","{int f1,null f2}"); }
	@Test public void test_4466() { checkNotSubtype("{{null f1} f1}","{int f2,null f3}"); }
	@Test public void test_4467() { checkNotSubtype("{{null f1} f1}","{int f1,int f2}"); }
	@Test public void test_4468() { checkNotSubtype("{{null f1} f1}","{int f2,int f3}"); }
	@Test public void test_4469() { checkIsSubtype("{{null f1} f1}","{{void f1} f1}"); }
	@Test public void test_4470() { checkIsSubtype("{{null f1} f1}","{{void f2} f1}"); }
	@Test public void test_4471() { checkIsSubtype("{{null f1} f1}","{{void f1} f2}"); }
	@Test public void test_4472() { checkIsSubtype("{{null f1} f1}","{{void f2} f2}"); }
	@Test public void test_4473() { checkIsSubtype("{{null f1} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_4474() { checkIsSubtype("{{null f1} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_4475() { checkIsSubtype("{{null f1} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_4476() { checkIsSubtype("{{null f1} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_4477() { checkNotSubtype("{{null f1} f1}","{{any f1} f1}"); }
	@Test public void test_4478() { checkNotSubtype("{{null f1} f1}","{{any f2} f1}"); }
	@Test public void test_4479() { checkNotSubtype("{{null f1} f1}","{{any f1} f2}"); }
	@Test public void test_4480() { checkNotSubtype("{{null f1} f1}","{{any f2} f2}"); }
	@Test public void test_4481() { checkNotSubtype("{{null f1} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_4482() { checkNotSubtype("{{null f1} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_4483() { checkNotSubtype("{{null f1} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_4484() { checkNotSubtype("{{null f1} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_4485() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test public void test_4486() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test public void test_4487() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test public void test_4488() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test public void test_4489() { checkNotSubtype("{{null f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_4490() { checkNotSubtype("{{null f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_4491() { checkNotSubtype("{{null f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_4492() { checkNotSubtype("{{null f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_4493() { checkNotSubtype("{{null f1} f1}","{{int f1} f1}"); }
	@Test public void test_4494() { checkNotSubtype("{{null f1} f1}","{{int f2} f1}"); }
	@Test public void test_4495() { checkNotSubtype("{{null f1} f1}","{{int f1} f2}"); }
	@Test public void test_4496() { checkNotSubtype("{{null f1} f1}","{{int f2} f2}"); }
	@Test public void test_4497() { checkNotSubtype("{{null f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_4498() { checkNotSubtype("{{null f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_4499() { checkNotSubtype("{{null f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_4500() { checkNotSubtype("{{null f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_4501() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_4502() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_4503() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_4504() { checkIsSubtype("{{null f2} f1}","{void f1}"); }
	@Test public void test_4505() { checkIsSubtype("{{null f2} f1}","{void f2}"); }
	@Test public void test_4506() { checkNotSubtype("{{null f2} f1}","{any f1}"); }
	@Test public void test_4507() { checkNotSubtype("{{null f2} f1}","{any f2}"); }
	@Test public void test_4508() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test public void test_4509() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test public void test_4510() { checkNotSubtype("{{null f2} f1}","{int f1}"); }
	@Test public void test_4511() { checkNotSubtype("{{null f2} f1}","{int f2}"); }
	@Test public void test_4512() { checkIsSubtype("{{null f2} f1}","{void f1,void f2}"); }
	@Test public void test_4513() { checkIsSubtype("{{null f2} f1}","{void f2,void f3}"); }
	@Test public void test_4514() { checkIsSubtype("{{null f2} f1}","{void f1,any f2}"); }
	@Test public void test_4515() { checkIsSubtype("{{null f2} f1}","{void f2,any f3}"); }
	@Test public void test_4516() { checkIsSubtype("{{null f2} f1}","{void f1,null f2}"); }
	@Test public void test_4517() { checkIsSubtype("{{null f2} f1}","{void f2,null f3}"); }
	@Test public void test_4518() { checkIsSubtype("{{null f2} f1}","{void f1,int f2}"); }
	@Test public void test_4519() { checkIsSubtype("{{null f2} f1}","{void f2,int f3}"); }
	@Test public void test_4520() { checkIsSubtype("{{null f2} f1}","{any f1,void f2}"); }
	@Test public void test_4521() { checkIsSubtype("{{null f2} f1}","{any f2,void f3}"); }
	@Test public void test_4522() { checkNotSubtype("{{null f2} f1}","{any f1,any f2}"); }
	@Test public void test_4523() { checkNotSubtype("{{null f2} f1}","{any f2,any f3}"); }
	@Test public void test_4524() { checkNotSubtype("{{null f2} f1}","{any f1,null f2}"); }
	@Test public void test_4525() { checkNotSubtype("{{null f2} f1}","{any f2,null f3}"); }
	@Test public void test_4526() { checkNotSubtype("{{null f2} f1}","{any f1,int f2}"); }
	@Test public void test_4527() { checkNotSubtype("{{null f2} f1}","{any f2,int f3}"); }
	@Test public void test_4528() { checkIsSubtype("{{null f2} f1}","{null f1,void f2}"); }
	@Test public void test_4529() { checkIsSubtype("{{null f2} f1}","{null f2,void f3}"); }
	@Test public void test_4530() { checkNotSubtype("{{null f2} f1}","{null f1,any f2}"); }
	@Test public void test_4531() { checkNotSubtype("{{null f2} f1}","{null f2,any f3}"); }
	@Test public void test_4532() { checkNotSubtype("{{null f2} f1}","{null f1,null f2}"); }
	@Test public void test_4533() { checkNotSubtype("{{null f2} f1}","{null f2,null f3}"); }
	@Test public void test_4534() { checkNotSubtype("{{null f2} f1}","{null f1,int f2}"); }
	@Test public void test_4535() { checkNotSubtype("{{null f2} f1}","{null f2,int f3}"); }
	@Test public void test_4536() { checkIsSubtype("{{null f2} f1}","{int f1,void f2}"); }
	@Test public void test_4537() { checkIsSubtype("{{null f2} f1}","{int f2,void f3}"); }
	@Test public void test_4538() { checkNotSubtype("{{null f2} f1}","{int f1,any f2}"); }
	@Test public void test_4539() { checkNotSubtype("{{null f2} f1}","{int f2,any f3}"); }
	@Test public void test_4540() { checkNotSubtype("{{null f2} f1}","{int f1,null f2}"); }
	@Test public void test_4541() { checkNotSubtype("{{null f2} f1}","{int f2,null f3}"); }
	@Test public void test_4542() { checkNotSubtype("{{null f2} f1}","{int f1,int f2}"); }
	@Test public void test_4543() { checkNotSubtype("{{null f2} f1}","{int f2,int f3}"); }
	@Test public void test_4544() { checkIsSubtype("{{null f2} f1}","{{void f1} f1}"); }
	@Test public void test_4545() { checkIsSubtype("{{null f2} f1}","{{void f2} f1}"); }
	@Test public void test_4546() { checkIsSubtype("{{null f2} f1}","{{void f1} f2}"); }
	@Test public void test_4547() { checkIsSubtype("{{null f2} f1}","{{void f2} f2}"); }
	@Test public void test_4548() { checkIsSubtype("{{null f2} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_4549() { checkIsSubtype("{{null f2} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_4550() { checkIsSubtype("{{null f2} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_4551() { checkIsSubtype("{{null f2} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_4552() { checkNotSubtype("{{null f2} f1}","{{any f1} f1}"); }
	@Test public void test_4553() { checkNotSubtype("{{null f2} f1}","{{any f2} f1}"); }
	@Test public void test_4554() { checkNotSubtype("{{null f2} f1}","{{any f1} f2}"); }
	@Test public void test_4555() { checkNotSubtype("{{null f2} f1}","{{any f2} f2}"); }
	@Test public void test_4556() { checkNotSubtype("{{null f2} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_4557() { checkNotSubtype("{{null f2} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_4558() { checkNotSubtype("{{null f2} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_4559() { checkNotSubtype("{{null f2} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_4560() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test public void test_4561() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test public void test_4562() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test public void test_4563() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test public void test_4564() { checkNotSubtype("{{null f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_4565() { checkNotSubtype("{{null f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_4566() { checkNotSubtype("{{null f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_4567() { checkNotSubtype("{{null f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_4568() { checkNotSubtype("{{null f2} f1}","{{int f1} f1}"); }
	@Test public void test_4569() { checkNotSubtype("{{null f2} f1}","{{int f2} f1}"); }
	@Test public void test_4570() { checkNotSubtype("{{null f2} f1}","{{int f1} f2}"); }
	@Test public void test_4571() { checkNotSubtype("{{null f2} f1}","{{int f2} f2}"); }
	@Test public void test_4572() { checkNotSubtype("{{null f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_4573() { checkNotSubtype("{{null f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_4574() { checkNotSubtype("{{null f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_4575() { checkNotSubtype("{{null f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_4576() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_4577() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_4578() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_4579() { checkIsSubtype("{{null f1} f2}","{void f1}"); }
	@Test public void test_4580() { checkIsSubtype("{{null f1} f2}","{void f2}"); }
	@Test public void test_4581() { checkNotSubtype("{{null f1} f2}","{any f1}"); }
	@Test public void test_4582() { checkNotSubtype("{{null f1} f2}","{any f2}"); }
	@Test public void test_4583() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test public void test_4584() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test public void test_4585() { checkNotSubtype("{{null f1} f2}","{int f1}"); }
	@Test public void test_4586() { checkNotSubtype("{{null f1} f2}","{int f2}"); }
	@Test public void test_4587() { checkIsSubtype("{{null f1} f2}","{void f1,void f2}"); }
	@Test public void test_4588() { checkIsSubtype("{{null f1} f2}","{void f2,void f3}"); }
	@Test public void test_4589() { checkIsSubtype("{{null f1} f2}","{void f1,any f2}"); }
	@Test public void test_4590() { checkIsSubtype("{{null f1} f2}","{void f2,any f3}"); }
	@Test public void test_4591() { checkIsSubtype("{{null f1} f2}","{void f1,null f2}"); }
	@Test public void test_4592() { checkIsSubtype("{{null f1} f2}","{void f2,null f3}"); }
	@Test public void test_4593() { checkIsSubtype("{{null f1} f2}","{void f1,int f2}"); }
	@Test public void test_4594() { checkIsSubtype("{{null f1} f2}","{void f2,int f3}"); }
	@Test public void test_4595() { checkIsSubtype("{{null f1} f2}","{any f1,void f2}"); }
	@Test public void test_4596() { checkIsSubtype("{{null f1} f2}","{any f2,void f3}"); }
	@Test public void test_4597() { checkNotSubtype("{{null f1} f2}","{any f1,any f2}"); }
	@Test public void test_4598() { checkNotSubtype("{{null f1} f2}","{any f2,any f3}"); }
	@Test public void test_4599() { checkNotSubtype("{{null f1} f2}","{any f1,null f2}"); }
	@Test public void test_4600() { checkNotSubtype("{{null f1} f2}","{any f2,null f3}"); }
	@Test public void test_4601() { checkNotSubtype("{{null f1} f2}","{any f1,int f2}"); }
	@Test public void test_4602() { checkNotSubtype("{{null f1} f2}","{any f2,int f3}"); }
	@Test public void test_4603() { checkIsSubtype("{{null f1} f2}","{null f1,void f2}"); }
	@Test public void test_4604() { checkIsSubtype("{{null f1} f2}","{null f2,void f3}"); }
	@Test public void test_4605() { checkNotSubtype("{{null f1} f2}","{null f1,any f2}"); }
	@Test public void test_4606() { checkNotSubtype("{{null f1} f2}","{null f2,any f3}"); }
	@Test public void test_4607() { checkNotSubtype("{{null f1} f2}","{null f1,null f2}"); }
	@Test public void test_4608() { checkNotSubtype("{{null f1} f2}","{null f2,null f3}"); }
	@Test public void test_4609() { checkNotSubtype("{{null f1} f2}","{null f1,int f2}"); }
	@Test public void test_4610() { checkNotSubtype("{{null f1} f2}","{null f2,int f3}"); }
	@Test public void test_4611() { checkIsSubtype("{{null f1} f2}","{int f1,void f2}"); }
	@Test public void test_4612() { checkIsSubtype("{{null f1} f2}","{int f2,void f3}"); }
	@Test public void test_4613() { checkNotSubtype("{{null f1} f2}","{int f1,any f2}"); }
	@Test public void test_4614() { checkNotSubtype("{{null f1} f2}","{int f2,any f3}"); }
	@Test public void test_4615() { checkNotSubtype("{{null f1} f2}","{int f1,null f2}"); }
	@Test public void test_4616() { checkNotSubtype("{{null f1} f2}","{int f2,null f3}"); }
	@Test public void test_4617() { checkNotSubtype("{{null f1} f2}","{int f1,int f2}"); }
	@Test public void test_4618() { checkNotSubtype("{{null f1} f2}","{int f2,int f3}"); }
	@Test public void test_4619() { checkIsSubtype("{{null f1} f2}","{{void f1} f1}"); }
	@Test public void test_4620() { checkIsSubtype("{{null f1} f2}","{{void f2} f1}"); }
	@Test public void test_4621() { checkIsSubtype("{{null f1} f2}","{{void f1} f2}"); }
	@Test public void test_4622() { checkIsSubtype("{{null f1} f2}","{{void f2} f2}"); }
	@Test public void test_4623() { checkIsSubtype("{{null f1} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4624() { checkIsSubtype("{{null f1} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4625() { checkIsSubtype("{{null f1} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4626() { checkIsSubtype("{{null f1} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4627() { checkNotSubtype("{{null f1} f2}","{{any f1} f1}"); }
	@Test public void test_4628() { checkNotSubtype("{{null f1} f2}","{{any f2} f1}"); }
	@Test public void test_4629() { checkNotSubtype("{{null f1} f2}","{{any f1} f2}"); }
	@Test public void test_4630() { checkNotSubtype("{{null f1} f2}","{{any f2} f2}"); }
	@Test public void test_4631() { checkNotSubtype("{{null f1} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4632() { checkNotSubtype("{{null f1} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4633() { checkNotSubtype("{{null f1} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4634() { checkNotSubtype("{{null f1} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4635() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test public void test_4636() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test public void test_4637() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test public void test_4638() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test public void test_4639() { checkNotSubtype("{{null f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4640() { checkNotSubtype("{{null f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4641() { checkNotSubtype("{{null f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4642() { checkNotSubtype("{{null f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4643() { checkNotSubtype("{{null f1} f2}","{{int f1} f1}"); }
	@Test public void test_4644() { checkNotSubtype("{{null f1} f2}","{{int f2} f1}"); }
	@Test public void test_4645() { checkNotSubtype("{{null f1} f2}","{{int f1} f2}"); }
	@Test public void test_4646() { checkNotSubtype("{{null f1} f2}","{{int f2} f2}"); }
	@Test public void test_4647() { checkNotSubtype("{{null f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4648() { checkNotSubtype("{{null f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4649() { checkNotSubtype("{{null f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4650() { checkNotSubtype("{{null f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4651() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_4652() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_4653() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_4654() { checkIsSubtype("{{null f2} f2}","{void f1}"); }
	@Test public void test_4655() { checkIsSubtype("{{null f2} f2}","{void f2}"); }
	@Test public void test_4656() { checkNotSubtype("{{null f2} f2}","{any f1}"); }
	@Test public void test_4657() { checkNotSubtype("{{null f2} f2}","{any f2}"); }
	@Test public void test_4658() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test public void test_4659() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test public void test_4660() { checkNotSubtype("{{null f2} f2}","{int f1}"); }
	@Test public void test_4661() { checkNotSubtype("{{null f2} f2}","{int f2}"); }
	@Test public void test_4662() { checkIsSubtype("{{null f2} f2}","{void f1,void f2}"); }
	@Test public void test_4663() { checkIsSubtype("{{null f2} f2}","{void f2,void f3}"); }
	@Test public void test_4664() { checkIsSubtype("{{null f2} f2}","{void f1,any f2}"); }
	@Test public void test_4665() { checkIsSubtype("{{null f2} f2}","{void f2,any f3}"); }
	@Test public void test_4666() { checkIsSubtype("{{null f2} f2}","{void f1,null f2}"); }
	@Test public void test_4667() { checkIsSubtype("{{null f2} f2}","{void f2,null f3}"); }
	@Test public void test_4668() { checkIsSubtype("{{null f2} f2}","{void f1,int f2}"); }
	@Test public void test_4669() { checkIsSubtype("{{null f2} f2}","{void f2,int f3}"); }
	@Test public void test_4670() { checkIsSubtype("{{null f2} f2}","{any f1,void f2}"); }
	@Test public void test_4671() { checkIsSubtype("{{null f2} f2}","{any f2,void f3}"); }
	@Test public void test_4672() { checkNotSubtype("{{null f2} f2}","{any f1,any f2}"); }
	@Test public void test_4673() { checkNotSubtype("{{null f2} f2}","{any f2,any f3}"); }
	@Test public void test_4674() { checkNotSubtype("{{null f2} f2}","{any f1,null f2}"); }
	@Test public void test_4675() { checkNotSubtype("{{null f2} f2}","{any f2,null f3}"); }
	@Test public void test_4676() { checkNotSubtype("{{null f2} f2}","{any f1,int f2}"); }
	@Test public void test_4677() { checkNotSubtype("{{null f2} f2}","{any f2,int f3}"); }
	@Test public void test_4678() { checkIsSubtype("{{null f2} f2}","{null f1,void f2}"); }
	@Test public void test_4679() { checkIsSubtype("{{null f2} f2}","{null f2,void f3}"); }
	@Test public void test_4680() { checkNotSubtype("{{null f2} f2}","{null f1,any f2}"); }
	@Test public void test_4681() { checkNotSubtype("{{null f2} f2}","{null f2,any f3}"); }
	@Test public void test_4682() { checkNotSubtype("{{null f2} f2}","{null f1,null f2}"); }
	@Test public void test_4683() { checkNotSubtype("{{null f2} f2}","{null f2,null f3}"); }
	@Test public void test_4684() { checkNotSubtype("{{null f2} f2}","{null f1,int f2}"); }
	@Test public void test_4685() { checkNotSubtype("{{null f2} f2}","{null f2,int f3}"); }
	@Test public void test_4686() { checkIsSubtype("{{null f2} f2}","{int f1,void f2}"); }
	@Test public void test_4687() { checkIsSubtype("{{null f2} f2}","{int f2,void f3}"); }
	@Test public void test_4688() { checkNotSubtype("{{null f2} f2}","{int f1,any f2}"); }
	@Test public void test_4689() { checkNotSubtype("{{null f2} f2}","{int f2,any f3}"); }
	@Test public void test_4690() { checkNotSubtype("{{null f2} f2}","{int f1,null f2}"); }
	@Test public void test_4691() { checkNotSubtype("{{null f2} f2}","{int f2,null f3}"); }
	@Test public void test_4692() { checkNotSubtype("{{null f2} f2}","{int f1,int f2}"); }
	@Test public void test_4693() { checkNotSubtype("{{null f2} f2}","{int f2,int f3}"); }
	@Test public void test_4694() { checkIsSubtype("{{null f2} f2}","{{void f1} f1}"); }
	@Test public void test_4695() { checkIsSubtype("{{null f2} f2}","{{void f2} f1}"); }
	@Test public void test_4696() { checkIsSubtype("{{null f2} f2}","{{void f1} f2}"); }
	@Test public void test_4697() { checkIsSubtype("{{null f2} f2}","{{void f2} f2}"); }
	@Test public void test_4698() { checkIsSubtype("{{null f2} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4699() { checkIsSubtype("{{null f2} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4700() { checkIsSubtype("{{null f2} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4701() { checkIsSubtype("{{null f2} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4702() { checkNotSubtype("{{null f2} f2}","{{any f1} f1}"); }
	@Test public void test_4703() { checkNotSubtype("{{null f2} f2}","{{any f2} f1}"); }
	@Test public void test_4704() { checkNotSubtype("{{null f2} f2}","{{any f1} f2}"); }
	@Test public void test_4705() { checkNotSubtype("{{null f2} f2}","{{any f2} f2}"); }
	@Test public void test_4706() { checkNotSubtype("{{null f2} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4707() { checkNotSubtype("{{null f2} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4708() { checkNotSubtype("{{null f2} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4709() { checkNotSubtype("{{null f2} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4710() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test public void test_4711() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test public void test_4712() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test public void test_4713() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test public void test_4714() { checkNotSubtype("{{null f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4715() { checkNotSubtype("{{null f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4716() { checkNotSubtype("{{null f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4717() { checkNotSubtype("{{null f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4718() { checkNotSubtype("{{null f2} f2}","{{int f1} f1}"); }
	@Test public void test_4719() { checkNotSubtype("{{null f2} f2}","{{int f2} f1}"); }
	@Test public void test_4720() { checkNotSubtype("{{null f2} f2}","{{int f1} f2}"); }
	@Test public void test_4721() { checkNotSubtype("{{null f2} f2}","{{int f2} f2}"); }
	@Test public void test_4722() { checkNotSubtype("{{null f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4723() { checkNotSubtype("{{null f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4724() { checkNotSubtype("{{null f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4725() { checkNotSubtype("{{null f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4726() { checkNotSubtype("{{null f1} f1,null f2}","any"); }
	@Test public void test_4727() { checkNotSubtype("{{null f1} f1,null f2}","null"); }
	@Test public void test_4728() { checkNotSubtype("{{null f1} f1,null f2}","int"); }
	@Test public void test_4729() { checkIsSubtype("{{null f1} f1,null f2}","{void f1}"); }
	@Test public void test_4730() { checkIsSubtype("{{null f1} f1,null f2}","{void f2}"); }
	@Test public void test_4731() { checkNotSubtype("{{null f1} f1,null f2}","{any f1}"); }
	@Test public void test_4732() { checkNotSubtype("{{null f1} f1,null f2}","{any f2}"); }
	@Test public void test_4733() { checkNotSubtype("{{null f1} f1,null f2}","{null f1}"); }
	@Test public void test_4734() { checkNotSubtype("{{null f1} f1,null f2}","{null f2}"); }
	@Test public void test_4735() { checkNotSubtype("{{null f1} f1,null f2}","{int f1}"); }
	@Test public void test_4736() { checkNotSubtype("{{null f1} f1,null f2}","{int f2}"); }
	@Test public void test_4737() { checkIsSubtype("{{null f1} f1,null f2}","{void f1,void f2}"); }
	@Test public void test_4738() { checkIsSubtype("{{null f1} f1,null f2}","{void f2,void f3}"); }
	@Test public void test_4739() { checkIsSubtype("{{null f1} f1,null f2}","{void f1,any f2}"); }
	@Test public void test_4740() { checkIsSubtype("{{null f1} f1,null f2}","{void f2,any f3}"); }
	@Test public void test_4741() { checkIsSubtype("{{null f1} f1,null f2}","{void f1,null f2}"); }
	@Test public void test_4742() { checkIsSubtype("{{null f1} f1,null f2}","{void f2,null f3}"); }
	@Test public void test_4743() { checkIsSubtype("{{null f1} f1,null f2}","{void f1,int f2}"); }
	@Test public void test_4744() { checkIsSubtype("{{null f1} f1,null f2}","{void f2,int f3}"); }
	@Test public void test_4745() { checkIsSubtype("{{null f1} f1,null f2}","{any f1,void f2}"); }
	@Test public void test_4746() { checkIsSubtype("{{null f1} f1,null f2}","{any f2,void f3}"); }
	@Test public void test_4747() { checkNotSubtype("{{null f1} f1,null f2}","{any f1,any f2}"); }
	@Test public void test_4748() { checkNotSubtype("{{null f1} f1,null f2}","{any f2,any f3}"); }
	@Test public void test_4749() { checkNotSubtype("{{null f1} f1,null f2}","{any f1,null f2}"); }
	@Test public void test_4750() { checkNotSubtype("{{null f1} f1,null f2}","{any f2,null f3}"); }
	@Test public void test_4751() { checkNotSubtype("{{null f1} f1,null f2}","{any f1,int f2}"); }
	@Test public void test_4752() { checkNotSubtype("{{null f1} f1,null f2}","{any f2,int f3}"); }
	@Test public void test_4753() { checkIsSubtype("{{null f1} f1,null f2}","{null f1,void f2}"); }
	@Test public void test_4754() { checkIsSubtype("{{null f1} f1,null f2}","{null f2,void f3}"); }
	@Test public void test_4755() { checkNotSubtype("{{null f1} f1,null f2}","{null f1,any f2}"); }
	@Test public void test_4756() { checkNotSubtype("{{null f1} f1,null f2}","{null f2,any f3}"); }
	@Test public void test_4757() { checkNotSubtype("{{null f1} f1,null f2}","{null f1,null f2}"); }
	@Test public void test_4758() { checkNotSubtype("{{null f1} f1,null f2}","{null f2,null f3}"); }
	@Test public void test_4759() { checkNotSubtype("{{null f1} f1,null f2}","{null f1,int f2}"); }
	@Test public void test_4760() { checkNotSubtype("{{null f1} f1,null f2}","{null f2,int f3}"); }
	@Test public void test_4761() { checkIsSubtype("{{null f1} f1,null f2}","{int f1,void f2}"); }
	@Test public void test_4762() { checkIsSubtype("{{null f1} f1,null f2}","{int f2,void f3}"); }
	@Test public void test_4763() { checkNotSubtype("{{null f1} f1,null f2}","{int f1,any f2}"); }
	@Test public void test_4764() { checkNotSubtype("{{null f1} f1,null f2}","{int f2,any f3}"); }
	@Test public void test_4765() { checkNotSubtype("{{null f1} f1,null f2}","{int f1,null f2}"); }
	@Test public void test_4766() { checkNotSubtype("{{null f1} f1,null f2}","{int f2,null f3}"); }
	@Test public void test_4767() { checkNotSubtype("{{null f1} f1,null f2}","{int f1,int f2}"); }
	@Test public void test_4768() { checkNotSubtype("{{null f1} f1,null f2}","{int f2,int f3}"); }
	@Test public void test_4769() { checkIsSubtype("{{null f1} f1,null f2}","{{void f1} f1}"); }
	@Test public void test_4770() { checkIsSubtype("{{null f1} f1,null f2}","{{void f2} f1}"); }
	@Test public void test_4771() { checkIsSubtype("{{null f1} f1,null f2}","{{void f1} f2}"); }
	@Test public void test_4772() { checkIsSubtype("{{null f1} f1,null f2}","{{void f2} f2}"); }
	@Test public void test_4773() { checkIsSubtype("{{null f1} f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4774() { checkIsSubtype("{{null f1} f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4775() { checkIsSubtype("{{null f1} f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4776() { checkIsSubtype("{{null f1} f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4777() { checkNotSubtype("{{null f1} f1,null f2}","{{any f1} f1}"); }
	@Test public void test_4778() { checkNotSubtype("{{null f1} f1,null f2}","{{any f2} f1}"); }
	@Test public void test_4779() { checkNotSubtype("{{null f1} f1,null f2}","{{any f1} f2}"); }
	@Test public void test_4780() { checkNotSubtype("{{null f1} f1,null f2}","{{any f2} f2}"); }
	@Test public void test_4781() { checkNotSubtype("{{null f1} f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4782() { checkNotSubtype("{{null f1} f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4783() { checkNotSubtype("{{null f1} f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4784() { checkNotSubtype("{{null f1} f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4785() { checkNotSubtype("{{null f1} f1,null f2}","{{null f1} f1}"); }
	@Test public void test_4786() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f1}"); }
	@Test public void test_4787() { checkNotSubtype("{{null f1} f1,null f2}","{{null f1} f2}"); }
	@Test public void test_4788() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f2}"); }
	@Test public void test_4789() { checkIsSubtype("{{null f1} f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4790() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4791() { checkNotSubtype("{{null f1} f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4792() { checkNotSubtype("{{null f1} f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4793() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f1}"); }
	@Test public void test_4794() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f1}"); }
	@Test public void test_4795() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f2}"); }
	@Test public void test_4796() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f2}"); }
	@Test public void test_4797() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4798() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4799() { checkNotSubtype("{{null f1} f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4800() { checkNotSubtype("{{null f1} f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4801() { checkNotSubtype("{{null f2} f1,null f2}","any"); }
	@Test public void test_4802() { checkNotSubtype("{{null f2} f1,null f2}","null"); }
	@Test public void test_4803() { checkNotSubtype("{{null f2} f1,null f2}","int"); }
	@Test public void test_4804() { checkIsSubtype("{{null f2} f1,null f2}","{void f1}"); }
	@Test public void test_4805() { checkIsSubtype("{{null f2} f1,null f2}","{void f2}"); }
	@Test public void test_4806() { checkNotSubtype("{{null f2} f1,null f2}","{any f1}"); }
	@Test public void test_4807() { checkNotSubtype("{{null f2} f1,null f2}","{any f2}"); }
	@Test public void test_4808() { checkNotSubtype("{{null f2} f1,null f2}","{null f1}"); }
	@Test public void test_4809() { checkNotSubtype("{{null f2} f1,null f2}","{null f2}"); }
	@Test public void test_4810() { checkNotSubtype("{{null f2} f1,null f2}","{int f1}"); }
	@Test public void test_4811() { checkNotSubtype("{{null f2} f1,null f2}","{int f2}"); }
	@Test public void test_4812() { checkIsSubtype("{{null f2} f1,null f2}","{void f1,void f2}"); }
	@Test public void test_4813() { checkIsSubtype("{{null f2} f1,null f2}","{void f2,void f3}"); }
	@Test public void test_4814() { checkIsSubtype("{{null f2} f1,null f2}","{void f1,any f2}"); }
	@Test public void test_4815() { checkIsSubtype("{{null f2} f1,null f2}","{void f2,any f3}"); }
	@Test public void test_4816() { checkIsSubtype("{{null f2} f1,null f2}","{void f1,null f2}"); }
	@Test public void test_4817() { checkIsSubtype("{{null f2} f1,null f2}","{void f2,null f3}"); }
	@Test public void test_4818() { checkIsSubtype("{{null f2} f1,null f2}","{void f1,int f2}"); }
	@Test public void test_4819() { checkIsSubtype("{{null f2} f1,null f2}","{void f2,int f3}"); }
	@Test public void test_4820() { checkIsSubtype("{{null f2} f1,null f2}","{any f1,void f2}"); }
	@Test public void test_4821() { checkIsSubtype("{{null f2} f1,null f2}","{any f2,void f3}"); }
	@Test public void test_4822() { checkNotSubtype("{{null f2} f1,null f2}","{any f1,any f2}"); }
	@Test public void test_4823() { checkNotSubtype("{{null f2} f1,null f2}","{any f2,any f3}"); }
	@Test public void test_4824() { checkNotSubtype("{{null f2} f1,null f2}","{any f1,null f2}"); }
	@Test public void test_4825() { checkNotSubtype("{{null f2} f1,null f2}","{any f2,null f3}"); }
	@Test public void test_4826() { checkNotSubtype("{{null f2} f1,null f2}","{any f1,int f2}"); }
	@Test public void test_4827() { checkNotSubtype("{{null f2} f1,null f2}","{any f2,int f3}"); }
	@Test public void test_4828() { checkIsSubtype("{{null f2} f1,null f2}","{null f1,void f2}"); }
	@Test public void test_4829() { checkIsSubtype("{{null f2} f1,null f2}","{null f2,void f3}"); }
	@Test public void test_4830() { checkNotSubtype("{{null f2} f1,null f2}","{null f1,any f2}"); }
	@Test public void test_4831() { checkNotSubtype("{{null f2} f1,null f2}","{null f2,any f3}"); }
	@Test public void test_4832() { checkNotSubtype("{{null f2} f1,null f2}","{null f1,null f2}"); }
	@Test public void test_4833() { checkNotSubtype("{{null f2} f1,null f2}","{null f2,null f3}"); }
	@Test public void test_4834() { checkNotSubtype("{{null f2} f1,null f2}","{null f1,int f2}"); }
	@Test public void test_4835() { checkNotSubtype("{{null f2} f1,null f2}","{null f2,int f3}"); }
	@Test public void test_4836() { checkIsSubtype("{{null f2} f1,null f2}","{int f1,void f2}"); }
	@Test public void test_4837() { checkIsSubtype("{{null f2} f1,null f2}","{int f2,void f3}"); }
	@Test public void test_4838() { checkNotSubtype("{{null f2} f1,null f2}","{int f1,any f2}"); }
	@Test public void test_4839() { checkNotSubtype("{{null f2} f1,null f2}","{int f2,any f3}"); }
	@Test public void test_4840() { checkNotSubtype("{{null f2} f1,null f2}","{int f1,null f2}"); }
	@Test public void test_4841() { checkNotSubtype("{{null f2} f1,null f2}","{int f2,null f3}"); }
	@Test public void test_4842() { checkNotSubtype("{{null f2} f1,null f2}","{int f1,int f2}"); }
	@Test public void test_4843() { checkNotSubtype("{{null f2} f1,null f2}","{int f2,int f3}"); }
	@Test public void test_4844() { checkIsSubtype("{{null f2} f1,null f2}","{{void f1} f1}"); }
	@Test public void test_4845() { checkIsSubtype("{{null f2} f1,null f2}","{{void f2} f1}"); }
	@Test public void test_4846() { checkIsSubtype("{{null f2} f1,null f2}","{{void f1} f2}"); }
	@Test public void test_4847() { checkIsSubtype("{{null f2} f1,null f2}","{{void f2} f2}"); }
	@Test public void test_4848() { checkIsSubtype("{{null f2} f1,null f2}","{{void f1} f1,void f2}"); }
	@Test public void test_4849() { checkIsSubtype("{{null f2} f1,null f2}","{{void f2} f1,void f2}"); }
	@Test public void test_4850() { checkIsSubtype("{{null f2} f1,null f2}","{{void f1} f2,void f3}"); }
	@Test public void test_4851() { checkIsSubtype("{{null f2} f1,null f2}","{{void f2} f2,void f3}"); }
	@Test public void test_4852() { checkNotSubtype("{{null f2} f1,null f2}","{{any f1} f1}"); }
	@Test public void test_4853() { checkNotSubtype("{{null f2} f1,null f2}","{{any f2} f1}"); }
	@Test public void test_4854() { checkNotSubtype("{{null f2} f1,null f2}","{{any f1} f2}"); }
	@Test public void test_4855() { checkNotSubtype("{{null f2} f1,null f2}","{{any f2} f2}"); }
	@Test public void test_4856() { checkNotSubtype("{{null f2} f1,null f2}","{{any f1} f1,any f2}"); }
	@Test public void test_4857() { checkNotSubtype("{{null f2} f1,null f2}","{{any f2} f1,any f2}"); }
	@Test public void test_4858() { checkNotSubtype("{{null f2} f1,null f2}","{{any f1} f2,any f3}"); }
	@Test public void test_4859() { checkNotSubtype("{{null f2} f1,null f2}","{{any f2} f2,any f3}"); }
	@Test public void test_4860() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f1}"); }
	@Test public void test_4861() { checkNotSubtype("{{null f2} f1,null f2}","{{null f2} f1}"); }
	@Test public void test_4862() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f2}"); }
	@Test public void test_4863() { checkNotSubtype("{{null f2} f1,null f2}","{{null f2} f2}"); }
	@Test public void test_4864() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f1,null f2}"); }
	@Test public void test_4865() { checkIsSubtype("{{null f2} f1,null f2}","{{null f2} f1,null f2}"); }
	@Test public void test_4866() { checkNotSubtype("{{null f2} f1,null f2}","{{null f1} f2,null f3}"); }
	@Test public void test_4867() { checkNotSubtype("{{null f2} f1,null f2}","{{null f2} f2,null f3}"); }
	@Test public void test_4868() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f1}"); }
	@Test public void test_4869() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f1}"); }
	@Test public void test_4870() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f2}"); }
	@Test public void test_4871() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f2}"); }
	@Test public void test_4872() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f1,int f2}"); }
	@Test public void test_4873() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f1,int f2}"); }
	@Test public void test_4874() { checkNotSubtype("{{null f2} f1,null f2}","{{int f1} f2,int f3}"); }
	@Test public void test_4875() { checkNotSubtype("{{null f2} f1,null f2}","{{int f2} f2,int f3}"); }
	@Test public void test_4876() { checkNotSubtype("{{null f1} f2,null f3}","any"); }
	@Test public void test_4877() { checkNotSubtype("{{null f1} f2,null f3}","null"); }
	@Test public void test_4878() { checkNotSubtype("{{null f1} f2,null f3}","int"); }
	@Test public void test_4879() { checkIsSubtype("{{null f1} f2,null f3}","{void f1}"); }
	@Test public void test_4880() { checkIsSubtype("{{null f1} f2,null f3}","{void f2}"); }
	@Test public void test_4881() { checkNotSubtype("{{null f1} f2,null f3}","{any f1}"); }
	@Test public void test_4882() { checkNotSubtype("{{null f1} f2,null f3}","{any f2}"); }
	@Test public void test_4883() { checkNotSubtype("{{null f1} f2,null f3}","{null f1}"); }
	@Test public void test_4884() { checkNotSubtype("{{null f1} f2,null f3}","{null f2}"); }
	@Test public void test_4885() { checkNotSubtype("{{null f1} f2,null f3}","{int f1}"); }
	@Test public void test_4886() { checkNotSubtype("{{null f1} f2,null f3}","{int f2}"); }
	@Test public void test_4887() { checkIsSubtype("{{null f1} f2,null f3}","{void f1,void f2}"); }
	@Test public void test_4888() { checkIsSubtype("{{null f1} f2,null f3}","{void f2,void f3}"); }
	@Test public void test_4889() { checkIsSubtype("{{null f1} f2,null f3}","{void f1,any f2}"); }
	@Test public void test_4890() { checkIsSubtype("{{null f1} f2,null f3}","{void f2,any f3}"); }
	@Test public void test_4891() { checkIsSubtype("{{null f1} f2,null f3}","{void f1,null f2}"); }
	@Test public void test_4892() { checkIsSubtype("{{null f1} f2,null f3}","{void f2,null f3}"); }
	@Test public void test_4893() { checkIsSubtype("{{null f1} f2,null f3}","{void f1,int f2}"); }
	@Test public void test_4894() { checkIsSubtype("{{null f1} f2,null f3}","{void f2,int f3}"); }
	@Test public void test_4895() { checkIsSubtype("{{null f1} f2,null f3}","{any f1,void f2}"); }
	@Test public void test_4896() { checkIsSubtype("{{null f1} f2,null f3}","{any f2,void f3}"); }
	@Test public void test_4897() { checkNotSubtype("{{null f1} f2,null f3}","{any f1,any f2}"); }
	@Test public void test_4898() { checkNotSubtype("{{null f1} f2,null f3}","{any f2,any f3}"); }
	@Test public void test_4899() { checkNotSubtype("{{null f1} f2,null f3}","{any f1,null f2}"); }
	@Test public void test_4900() { checkNotSubtype("{{null f1} f2,null f3}","{any f2,null f3}"); }
	@Test public void test_4901() { checkNotSubtype("{{null f1} f2,null f3}","{any f1,int f2}"); }
	@Test public void test_4902() { checkNotSubtype("{{null f1} f2,null f3}","{any f2,int f3}"); }
	@Test public void test_4903() { checkIsSubtype("{{null f1} f2,null f3}","{null f1,void f2}"); }
	@Test public void test_4904() { checkIsSubtype("{{null f1} f2,null f3}","{null f2,void f3}"); }
	@Test public void test_4905() { checkNotSubtype("{{null f1} f2,null f3}","{null f1,any f2}"); }
	@Test public void test_4906() { checkNotSubtype("{{null f1} f2,null f3}","{null f2,any f3}"); }
	@Test public void test_4907() { checkNotSubtype("{{null f1} f2,null f3}","{null f1,null f2}"); }
	@Test public void test_4908() { checkNotSubtype("{{null f1} f2,null f3}","{null f2,null f3}"); }
	@Test public void test_4909() { checkNotSubtype("{{null f1} f2,null f3}","{null f1,int f2}"); }
	@Test public void test_4910() { checkNotSubtype("{{null f1} f2,null f3}","{null f2,int f3}"); }
	@Test public void test_4911() { checkIsSubtype("{{null f1} f2,null f3}","{int f1,void f2}"); }
	@Test public void test_4912() { checkIsSubtype("{{null f1} f2,null f3}","{int f2,void f3}"); }
	@Test public void test_4913() { checkNotSubtype("{{null f1} f2,null f3}","{int f1,any f2}"); }
	@Test public void test_4914() { checkNotSubtype("{{null f1} f2,null f3}","{int f2,any f3}"); }
	@Test public void test_4915() { checkNotSubtype("{{null f1} f2,null f3}","{int f1,null f2}"); }
	@Test public void test_4916() { checkNotSubtype("{{null f1} f2,null f3}","{int f2,null f3}"); }
	@Test public void test_4917() { checkNotSubtype("{{null f1} f2,null f3}","{int f1,int f2}"); }
	@Test public void test_4918() { checkNotSubtype("{{null f1} f2,null f3}","{int f2,int f3}"); }
	@Test public void test_4919() { checkIsSubtype("{{null f1} f2,null f3}","{{void f1} f1}"); }
	@Test public void test_4920() { checkIsSubtype("{{null f1} f2,null f3}","{{void f2} f1}"); }
	@Test public void test_4921() { checkIsSubtype("{{null f1} f2,null f3}","{{void f1} f2}"); }
	@Test public void test_4922() { checkIsSubtype("{{null f1} f2,null f3}","{{void f2} f2}"); }
	@Test public void test_4923() { checkIsSubtype("{{null f1} f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_4924() { checkIsSubtype("{{null f1} f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_4925() { checkIsSubtype("{{null f1} f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_4926() { checkIsSubtype("{{null f1} f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_4927() { checkNotSubtype("{{null f1} f2,null f3}","{{any f1} f1}"); }
	@Test public void test_4928() { checkNotSubtype("{{null f1} f2,null f3}","{{any f2} f1}"); }
	@Test public void test_4929() { checkNotSubtype("{{null f1} f2,null f3}","{{any f1} f2}"); }
	@Test public void test_4930() { checkNotSubtype("{{null f1} f2,null f3}","{{any f2} f2}"); }
	@Test public void test_4931() { checkNotSubtype("{{null f1} f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_4932() { checkNotSubtype("{{null f1} f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_4933() { checkNotSubtype("{{null f1} f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_4934() { checkNotSubtype("{{null f1} f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_4935() { checkNotSubtype("{{null f1} f2,null f3}","{{null f1} f1}"); }
	@Test public void test_4936() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f1}"); }
	@Test public void test_4937() { checkNotSubtype("{{null f1} f2,null f3}","{{null f1} f2}"); }
	@Test public void test_4938() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f2}"); }
	@Test public void test_4939() { checkNotSubtype("{{null f1} f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_4940() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_4941() { checkIsSubtype("{{null f1} f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_4942() { checkNotSubtype("{{null f1} f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_4943() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f1}"); }
	@Test public void test_4944() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f1}"); }
	@Test public void test_4945() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f2}"); }
	@Test public void test_4946() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f2}"); }
	@Test public void test_4947() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_4948() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_4949() { checkNotSubtype("{{null f1} f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_4950() { checkNotSubtype("{{null f1} f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_4951() { checkNotSubtype("{{null f2} f2,null f3}","any"); }
	@Test public void test_4952() { checkNotSubtype("{{null f2} f2,null f3}","null"); }
	@Test public void test_4953() { checkNotSubtype("{{null f2} f2,null f3}","int"); }
	@Test public void test_4954() { checkIsSubtype("{{null f2} f2,null f3}","{void f1}"); }
	@Test public void test_4955() { checkIsSubtype("{{null f2} f2,null f3}","{void f2}"); }
	@Test public void test_4956() { checkNotSubtype("{{null f2} f2,null f3}","{any f1}"); }
	@Test public void test_4957() { checkNotSubtype("{{null f2} f2,null f3}","{any f2}"); }
	@Test public void test_4958() { checkNotSubtype("{{null f2} f2,null f3}","{null f1}"); }
	@Test public void test_4959() { checkNotSubtype("{{null f2} f2,null f3}","{null f2}"); }
	@Test public void test_4960() { checkNotSubtype("{{null f2} f2,null f3}","{int f1}"); }
	@Test public void test_4961() { checkNotSubtype("{{null f2} f2,null f3}","{int f2}"); }
	@Test public void test_4962() { checkIsSubtype("{{null f2} f2,null f3}","{void f1,void f2}"); }
	@Test public void test_4963() { checkIsSubtype("{{null f2} f2,null f3}","{void f2,void f3}"); }
	@Test public void test_4964() { checkIsSubtype("{{null f2} f2,null f3}","{void f1,any f2}"); }
	@Test public void test_4965() { checkIsSubtype("{{null f2} f2,null f3}","{void f2,any f3}"); }
	@Test public void test_4966() { checkIsSubtype("{{null f2} f2,null f3}","{void f1,null f2}"); }
	@Test public void test_4967() { checkIsSubtype("{{null f2} f2,null f3}","{void f2,null f3}"); }
	@Test public void test_4968() { checkIsSubtype("{{null f2} f2,null f3}","{void f1,int f2}"); }
	@Test public void test_4969() { checkIsSubtype("{{null f2} f2,null f3}","{void f2,int f3}"); }
	@Test public void test_4970() { checkIsSubtype("{{null f2} f2,null f3}","{any f1,void f2}"); }
	@Test public void test_4971() { checkIsSubtype("{{null f2} f2,null f3}","{any f2,void f3}"); }
	@Test public void test_4972() { checkNotSubtype("{{null f2} f2,null f3}","{any f1,any f2}"); }
	@Test public void test_4973() { checkNotSubtype("{{null f2} f2,null f3}","{any f2,any f3}"); }
	@Test public void test_4974() { checkNotSubtype("{{null f2} f2,null f3}","{any f1,null f2}"); }
	@Test public void test_4975() { checkNotSubtype("{{null f2} f2,null f3}","{any f2,null f3}"); }
	@Test public void test_4976() { checkNotSubtype("{{null f2} f2,null f3}","{any f1,int f2}"); }
	@Test public void test_4977() { checkNotSubtype("{{null f2} f2,null f3}","{any f2,int f3}"); }
	@Test public void test_4978() { checkIsSubtype("{{null f2} f2,null f3}","{null f1,void f2}"); }
	@Test public void test_4979() { checkIsSubtype("{{null f2} f2,null f3}","{null f2,void f3}"); }
	@Test public void test_4980() { checkNotSubtype("{{null f2} f2,null f3}","{null f1,any f2}"); }
	@Test public void test_4981() { checkNotSubtype("{{null f2} f2,null f3}","{null f2,any f3}"); }
	@Test public void test_4982() { checkNotSubtype("{{null f2} f2,null f3}","{null f1,null f2}"); }
	@Test public void test_4983() { checkNotSubtype("{{null f2} f2,null f3}","{null f2,null f3}"); }
	@Test public void test_4984() { checkNotSubtype("{{null f2} f2,null f3}","{null f1,int f2}"); }
	@Test public void test_4985() { checkNotSubtype("{{null f2} f2,null f3}","{null f2,int f3}"); }
	@Test public void test_4986() { checkIsSubtype("{{null f2} f2,null f3}","{int f1,void f2}"); }
	@Test public void test_4987() { checkIsSubtype("{{null f2} f2,null f3}","{int f2,void f3}"); }
	@Test public void test_4988() { checkNotSubtype("{{null f2} f2,null f3}","{int f1,any f2}"); }
	@Test public void test_4989() { checkNotSubtype("{{null f2} f2,null f3}","{int f2,any f3}"); }
	@Test public void test_4990() { checkNotSubtype("{{null f2} f2,null f3}","{int f1,null f2}"); }
	@Test public void test_4991() { checkNotSubtype("{{null f2} f2,null f3}","{int f2,null f3}"); }
	@Test public void test_4992() { checkNotSubtype("{{null f2} f2,null f3}","{int f1,int f2}"); }
	@Test public void test_4993() { checkNotSubtype("{{null f2} f2,null f3}","{int f2,int f3}"); }
	@Test public void test_4994() { checkIsSubtype("{{null f2} f2,null f3}","{{void f1} f1}"); }
	@Test public void test_4995() { checkIsSubtype("{{null f2} f2,null f3}","{{void f2} f1}"); }
	@Test public void test_4996() { checkIsSubtype("{{null f2} f2,null f3}","{{void f1} f2}"); }
	@Test public void test_4997() { checkIsSubtype("{{null f2} f2,null f3}","{{void f2} f2}"); }
	@Test public void test_4998() { checkIsSubtype("{{null f2} f2,null f3}","{{void f1} f1,void f2}"); }
	@Test public void test_4999() { checkIsSubtype("{{null f2} f2,null f3}","{{void f2} f1,void f2}"); }
	@Test public void test_5000() { checkIsSubtype("{{null f2} f2,null f3}","{{void f1} f2,void f3}"); }
	@Test public void test_5001() { checkIsSubtype("{{null f2} f2,null f3}","{{void f2} f2,void f3}"); }
	@Test public void test_5002() { checkNotSubtype("{{null f2} f2,null f3}","{{any f1} f1}"); }
	@Test public void test_5003() { checkNotSubtype("{{null f2} f2,null f3}","{{any f2} f1}"); }
	@Test public void test_5004() { checkNotSubtype("{{null f2} f2,null f3}","{{any f1} f2}"); }
	@Test public void test_5005() { checkNotSubtype("{{null f2} f2,null f3}","{{any f2} f2}"); }
	@Test public void test_5006() { checkNotSubtype("{{null f2} f2,null f3}","{{any f1} f1,any f2}"); }
	@Test public void test_5007() { checkNotSubtype("{{null f2} f2,null f3}","{{any f2} f1,any f2}"); }
	@Test public void test_5008() { checkNotSubtype("{{null f2} f2,null f3}","{{any f1} f2,any f3}"); }
	@Test public void test_5009() { checkNotSubtype("{{null f2} f2,null f3}","{{any f2} f2,any f3}"); }
	@Test public void test_5010() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f1}"); }
	@Test public void test_5011() { checkNotSubtype("{{null f2} f2,null f3}","{{null f2} f1}"); }
	@Test public void test_5012() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f2}"); }
	@Test public void test_5013() { checkNotSubtype("{{null f2} f2,null f3}","{{null f2} f2}"); }
	@Test public void test_5014() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f1,null f2}"); }
	@Test public void test_5015() { checkNotSubtype("{{null f2} f2,null f3}","{{null f2} f1,null f2}"); }
	@Test public void test_5016() { checkNotSubtype("{{null f2} f2,null f3}","{{null f1} f2,null f3}"); }
	@Test public void test_5017() { checkIsSubtype("{{null f2} f2,null f3}","{{null f2} f2,null f3}"); }
	@Test public void test_5018() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f1}"); }
	@Test public void test_5019() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f1}"); }
	@Test public void test_5020() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f2}"); }
	@Test public void test_5021() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f2}"); }
	@Test public void test_5022() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f1,int f2}"); }
	@Test public void test_5023() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f1,int f2}"); }
	@Test public void test_5024() { checkNotSubtype("{{null f2} f2,null f3}","{{int f1} f2,int f3}"); }
	@Test public void test_5025() { checkNotSubtype("{{null f2} f2,null f3}","{{int f2} f2,int f3}"); }
	@Test public void test_5026() { checkNotSubtype("{{int f1} f1}","any"); }
	@Test public void test_5027() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_5028() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_5029() { checkIsSubtype("{{int f1} f1}","{void f1}"); }
	@Test public void test_5030() { checkIsSubtype("{{int f1} f1}","{void f2}"); }
	@Test public void test_5031() { checkNotSubtype("{{int f1} f1}","{any f1}"); }
	@Test public void test_5032() { checkNotSubtype("{{int f1} f1}","{any f2}"); }
	@Test public void test_5033() { checkNotSubtype("{{int f1} f1}","{null f1}"); }
	@Test public void test_5034() { checkNotSubtype("{{int f1} f1}","{null f2}"); }
	@Test public void test_5035() { checkNotSubtype("{{int f1} f1}","{int f1}"); }
	@Test public void test_5036() { checkNotSubtype("{{int f1} f1}","{int f2}"); }
	@Test public void test_5037() { checkIsSubtype("{{int f1} f1}","{void f1,void f2}"); }
	@Test public void test_5038() { checkIsSubtype("{{int f1} f1}","{void f2,void f3}"); }
	@Test public void test_5039() { checkIsSubtype("{{int f1} f1}","{void f1,any f2}"); }
	@Test public void test_5040() { checkIsSubtype("{{int f1} f1}","{void f2,any f3}"); }
	@Test public void test_5041() { checkIsSubtype("{{int f1} f1}","{void f1,null f2}"); }
	@Test public void test_5042() { checkIsSubtype("{{int f1} f1}","{void f2,null f3}"); }
	@Test public void test_5043() { checkIsSubtype("{{int f1} f1}","{void f1,int f2}"); }
	@Test public void test_5044() { checkIsSubtype("{{int f1} f1}","{void f2,int f3}"); }
	@Test public void test_5045() { checkIsSubtype("{{int f1} f1}","{any f1,void f2}"); }
	@Test public void test_5046() { checkIsSubtype("{{int f1} f1}","{any f2,void f3}"); }
	@Test public void test_5047() { checkNotSubtype("{{int f1} f1}","{any f1,any f2}"); }
	@Test public void test_5048() { checkNotSubtype("{{int f1} f1}","{any f2,any f3}"); }
	@Test public void test_5049() { checkNotSubtype("{{int f1} f1}","{any f1,null f2}"); }
	@Test public void test_5050() { checkNotSubtype("{{int f1} f1}","{any f2,null f3}"); }
	@Test public void test_5051() { checkNotSubtype("{{int f1} f1}","{any f1,int f2}"); }
	@Test public void test_5052() { checkNotSubtype("{{int f1} f1}","{any f2,int f3}"); }
	@Test public void test_5053() { checkIsSubtype("{{int f1} f1}","{null f1,void f2}"); }
	@Test public void test_5054() { checkIsSubtype("{{int f1} f1}","{null f2,void f3}"); }
	@Test public void test_5055() { checkNotSubtype("{{int f1} f1}","{null f1,any f2}"); }
	@Test public void test_5056() { checkNotSubtype("{{int f1} f1}","{null f2,any f3}"); }
	@Test public void test_5057() { checkNotSubtype("{{int f1} f1}","{null f1,null f2}"); }
	@Test public void test_5058() { checkNotSubtype("{{int f1} f1}","{null f2,null f3}"); }
	@Test public void test_5059() { checkNotSubtype("{{int f1} f1}","{null f1,int f2}"); }
	@Test public void test_5060() { checkNotSubtype("{{int f1} f1}","{null f2,int f3}"); }
	@Test public void test_5061() { checkIsSubtype("{{int f1} f1}","{int f1,void f2}"); }
	@Test public void test_5062() { checkIsSubtype("{{int f1} f1}","{int f2,void f3}"); }
	@Test public void test_5063() { checkNotSubtype("{{int f1} f1}","{int f1,any f2}"); }
	@Test public void test_5064() { checkNotSubtype("{{int f1} f1}","{int f2,any f3}"); }
	@Test public void test_5065() { checkNotSubtype("{{int f1} f1}","{int f1,null f2}"); }
	@Test public void test_5066() { checkNotSubtype("{{int f1} f1}","{int f2,null f3}"); }
	@Test public void test_5067() { checkNotSubtype("{{int f1} f1}","{int f1,int f2}"); }
	@Test public void test_5068() { checkNotSubtype("{{int f1} f1}","{int f2,int f3}"); }
	@Test public void test_5069() { checkIsSubtype("{{int f1} f1}","{{void f1} f1}"); }
	@Test public void test_5070() { checkIsSubtype("{{int f1} f1}","{{void f2} f1}"); }
	@Test public void test_5071() { checkIsSubtype("{{int f1} f1}","{{void f1} f2}"); }
	@Test public void test_5072() { checkIsSubtype("{{int f1} f1}","{{void f2} f2}"); }
	@Test public void test_5073() { checkIsSubtype("{{int f1} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_5074() { checkIsSubtype("{{int f1} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_5075() { checkIsSubtype("{{int f1} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_5076() { checkIsSubtype("{{int f1} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_5077() { checkNotSubtype("{{int f1} f1}","{{any f1} f1}"); }
	@Test public void test_5078() { checkNotSubtype("{{int f1} f1}","{{any f2} f1}"); }
	@Test public void test_5079() { checkNotSubtype("{{int f1} f1}","{{any f1} f2}"); }
	@Test public void test_5080() { checkNotSubtype("{{int f1} f1}","{{any f2} f2}"); }
	@Test public void test_5081() { checkNotSubtype("{{int f1} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_5082() { checkNotSubtype("{{int f1} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_5083() { checkNotSubtype("{{int f1} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_5084() { checkNotSubtype("{{int f1} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_5085() { checkNotSubtype("{{int f1} f1}","{{null f1} f1}"); }
	@Test public void test_5086() { checkNotSubtype("{{int f1} f1}","{{null f2} f1}"); }
	@Test public void test_5087() { checkNotSubtype("{{int f1} f1}","{{null f1} f2}"); }
	@Test public void test_5088() { checkNotSubtype("{{int f1} f1}","{{null f2} f2}"); }
	@Test public void test_5089() { checkNotSubtype("{{int f1} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_5090() { checkNotSubtype("{{int f1} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_5091() { checkNotSubtype("{{int f1} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_5092() { checkNotSubtype("{{int f1} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_5093() { checkIsSubtype("{{int f1} f1}","{{int f1} f1}"); }
	@Test public void test_5094() { checkNotSubtype("{{int f1} f1}","{{int f2} f1}"); }
	@Test public void test_5095() { checkNotSubtype("{{int f1} f1}","{{int f1} f2}"); }
	@Test public void test_5096() { checkNotSubtype("{{int f1} f1}","{{int f2} f2}"); }
	@Test public void test_5097() { checkNotSubtype("{{int f1} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_5098() { checkNotSubtype("{{int f1} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_5099() { checkNotSubtype("{{int f1} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_5100() { checkNotSubtype("{{int f1} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_5101() { checkNotSubtype("{{int f2} f1}","any"); }
	@Test public void test_5102() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_5103() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_5104() { checkIsSubtype("{{int f2} f1}","{void f1}"); }
	@Test public void test_5105() { checkIsSubtype("{{int f2} f1}","{void f2}"); }
	@Test public void test_5106() { checkNotSubtype("{{int f2} f1}","{any f1}"); }
	@Test public void test_5107() { checkNotSubtype("{{int f2} f1}","{any f2}"); }
	@Test public void test_5108() { checkNotSubtype("{{int f2} f1}","{null f1}"); }
	@Test public void test_5109() { checkNotSubtype("{{int f2} f1}","{null f2}"); }
	@Test public void test_5110() { checkNotSubtype("{{int f2} f1}","{int f1}"); }
	@Test public void test_5111() { checkNotSubtype("{{int f2} f1}","{int f2}"); }
	@Test public void test_5112() { checkIsSubtype("{{int f2} f1}","{void f1,void f2}"); }
	@Test public void test_5113() { checkIsSubtype("{{int f2} f1}","{void f2,void f3}"); }
	@Test public void test_5114() { checkIsSubtype("{{int f2} f1}","{void f1,any f2}"); }
	@Test public void test_5115() { checkIsSubtype("{{int f2} f1}","{void f2,any f3}"); }
	@Test public void test_5116() { checkIsSubtype("{{int f2} f1}","{void f1,null f2}"); }
	@Test public void test_5117() { checkIsSubtype("{{int f2} f1}","{void f2,null f3}"); }
	@Test public void test_5118() { checkIsSubtype("{{int f2} f1}","{void f1,int f2}"); }
	@Test public void test_5119() { checkIsSubtype("{{int f2} f1}","{void f2,int f3}"); }
	@Test public void test_5120() { checkIsSubtype("{{int f2} f1}","{any f1,void f2}"); }
	@Test public void test_5121() { checkIsSubtype("{{int f2} f1}","{any f2,void f3}"); }
	@Test public void test_5122() { checkNotSubtype("{{int f2} f1}","{any f1,any f2}"); }
	@Test public void test_5123() { checkNotSubtype("{{int f2} f1}","{any f2,any f3}"); }
	@Test public void test_5124() { checkNotSubtype("{{int f2} f1}","{any f1,null f2}"); }
	@Test public void test_5125() { checkNotSubtype("{{int f2} f1}","{any f2,null f3}"); }
	@Test public void test_5126() { checkNotSubtype("{{int f2} f1}","{any f1,int f2}"); }
	@Test public void test_5127() { checkNotSubtype("{{int f2} f1}","{any f2,int f3}"); }
	@Test public void test_5128() { checkIsSubtype("{{int f2} f1}","{null f1,void f2}"); }
	@Test public void test_5129() { checkIsSubtype("{{int f2} f1}","{null f2,void f3}"); }
	@Test public void test_5130() { checkNotSubtype("{{int f2} f1}","{null f1,any f2}"); }
	@Test public void test_5131() { checkNotSubtype("{{int f2} f1}","{null f2,any f3}"); }
	@Test public void test_5132() { checkNotSubtype("{{int f2} f1}","{null f1,null f2}"); }
	@Test public void test_5133() { checkNotSubtype("{{int f2} f1}","{null f2,null f3}"); }
	@Test public void test_5134() { checkNotSubtype("{{int f2} f1}","{null f1,int f2}"); }
	@Test public void test_5135() { checkNotSubtype("{{int f2} f1}","{null f2,int f3}"); }
	@Test public void test_5136() { checkIsSubtype("{{int f2} f1}","{int f1,void f2}"); }
	@Test public void test_5137() { checkIsSubtype("{{int f2} f1}","{int f2,void f3}"); }
	@Test public void test_5138() { checkNotSubtype("{{int f2} f1}","{int f1,any f2}"); }
	@Test public void test_5139() { checkNotSubtype("{{int f2} f1}","{int f2,any f3}"); }
	@Test public void test_5140() { checkNotSubtype("{{int f2} f1}","{int f1,null f2}"); }
	@Test public void test_5141() { checkNotSubtype("{{int f2} f1}","{int f2,null f3}"); }
	@Test public void test_5142() { checkNotSubtype("{{int f2} f1}","{int f1,int f2}"); }
	@Test public void test_5143() { checkNotSubtype("{{int f2} f1}","{int f2,int f3}"); }
	@Test public void test_5144() { checkIsSubtype("{{int f2} f1}","{{void f1} f1}"); }
	@Test public void test_5145() { checkIsSubtype("{{int f2} f1}","{{void f2} f1}"); }
	@Test public void test_5146() { checkIsSubtype("{{int f2} f1}","{{void f1} f2}"); }
	@Test public void test_5147() { checkIsSubtype("{{int f2} f1}","{{void f2} f2}"); }
	@Test public void test_5148() { checkIsSubtype("{{int f2} f1}","{{void f1} f1,void f2}"); }
	@Test public void test_5149() { checkIsSubtype("{{int f2} f1}","{{void f2} f1,void f2}"); }
	@Test public void test_5150() { checkIsSubtype("{{int f2} f1}","{{void f1} f2,void f3}"); }
	@Test public void test_5151() { checkIsSubtype("{{int f2} f1}","{{void f2} f2,void f3}"); }
	@Test public void test_5152() { checkNotSubtype("{{int f2} f1}","{{any f1} f1}"); }
	@Test public void test_5153() { checkNotSubtype("{{int f2} f1}","{{any f2} f1}"); }
	@Test public void test_5154() { checkNotSubtype("{{int f2} f1}","{{any f1} f2}"); }
	@Test public void test_5155() { checkNotSubtype("{{int f2} f1}","{{any f2} f2}"); }
	@Test public void test_5156() { checkNotSubtype("{{int f2} f1}","{{any f1} f1,any f2}"); }
	@Test public void test_5157() { checkNotSubtype("{{int f2} f1}","{{any f2} f1,any f2}"); }
	@Test public void test_5158() { checkNotSubtype("{{int f2} f1}","{{any f1} f2,any f3}"); }
	@Test public void test_5159() { checkNotSubtype("{{int f2} f1}","{{any f2} f2,any f3}"); }
	@Test public void test_5160() { checkNotSubtype("{{int f2} f1}","{{null f1} f1}"); }
	@Test public void test_5161() { checkNotSubtype("{{int f2} f1}","{{null f2} f1}"); }
	@Test public void test_5162() { checkNotSubtype("{{int f2} f1}","{{null f1} f2}"); }
	@Test public void test_5163() { checkNotSubtype("{{int f2} f1}","{{null f2} f2}"); }
	@Test public void test_5164() { checkNotSubtype("{{int f2} f1}","{{null f1} f1,null f2}"); }
	@Test public void test_5165() { checkNotSubtype("{{int f2} f1}","{{null f2} f1,null f2}"); }
	@Test public void test_5166() { checkNotSubtype("{{int f2} f1}","{{null f1} f2,null f3}"); }
	@Test public void test_5167() { checkNotSubtype("{{int f2} f1}","{{null f2} f2,null f3}"); }
	@Test public void test_5168() { checkNotSubtype("{{int f2} f1}","{{int f1} f1}"); }
	@Test public void test_5169() { checkIsSubtype("{{int f2} f1}","{{int f2} f1}"); }
	@Test public void test_5170() { checkNotSubtype("{{int f2} f1}","{{int f1} f2}"); }
	@Test public void test_5171() { checkNotSubtype("{{int f2} f1}","{{int f2} f2}"); }
	@Test public void test_5172() { checkNotSubtype("{{int f2} f1}","{{int f1} f1,int f2}"); }
	@Test public void test_5173() { checkNotSubtype("{{int f2} f1}","{{int f2} f1,int f2}"); }
	@Test public void test_5174() { checkNotSubtype("{{int f2} f1}","{{int f1} f2,int f3}"); }
	@Test public void test_5175() { checkNotSubtype("{{int f2} f1}","{{int f2} f2,int f3}"); }
	@Test public void test_5176() { checkNotSubtype("{{int f1} f2}","any"); }
	@Test public void test_5177() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_5178() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_5179() { checkIsSubtype("{{int f1} f2}","{void f1}"); }
	@Test public void test_5180() { checkIsSubtype("{{int f1} f2}","{void f2}"); }
	@Test public void test_5181() { checkNotSubtype("{{int f1} f2}","{any f1}"); }
	@Test public void test_5182() { checkNotSubtype("{{int f1} f2}","{any f2}"); }
	@Test public void test_5183() { checkNotSubtype("{{int f1} f2}","{null f1}"); }
	@Test public void test_5184() { checkNotSubtype("{{int f1} f2}","{null f2}"); }
	@Test public void test_5185() { checkNotSubtype("{{int f1} f2}","{int f1}"); }
	@Test public void test_5186() { checkNotSubtype("{{int f1} f2}","{int f2}"); }
	@Test public void test_5187() { checkIsSubtype("{{int f1} f2}","{void f1,void f2}"); }
	@Test public void test_5188() { checkIsSubtype("{{int f1} f2}","{void f2,void f3}"); }
	@Test public void test_5189() { checkIsSubtype("{{int f1} f2}","{void f1,any f2}"); }
	@Test public void test_5190() { checkIsSubtype("{{int f1} f2}","{void f2,any f3}"); }
	@Test public void test_5191() { checkIsSubtype("{{int f1} f2}","{void f1,null f2}"); }
	@Test public void test_5192() { checkIsSubtype("{{int f1} f2}","{void f2,null f3}"); }
	@Test public void test_5193() { checkIsSubtype("{{int f1} f2}","{void f1,int f2}"); }
	@Test public void test_5194() { checkIsSubtype("{{int f1} f2}","{void f2,int f3}"); }
	@Test public void test_5195() { checkIsSubtype("{{int f1} f2}","{any f1,void f2}"); }
	@Test public void test_5196() { checkIsSubtype("{{int f1} f2}","{any f2,void f3}"); }
	@Test public void test_5197() { checkNotSubtype("{{int f1} f2}","{any f1,any f2}"); }
	@Test public void test_5198() { checkNotSubtype("{{int f1} f2}","{any f2,any f3}"); }
	@Test public void test_5199() { checkNotSubtype("{{int f1} f2}","{any f1,null f2}"); }
	@Test public void test_5200() { checkNotSubtype("{{int f1} f2}","{any f2,null f3}"); }
	@Test public void test_5201() { checkNotSubtype("{{int f1} f2}","{any f1,int f2}"); }
	@Test public void test_5202() { checkNotSubtype("{{int f1} f2}","{any f2,int f3}"); }
	@Test public void test_5203() { checkIsSubtype("{{int f1} f2}","{null f1,void f2}"); }
	@Test public void test_5204() { checkIsSubtype("{{int f1} f2}","{null f2,void f3}"); }
	@Test public void test_5205() { checkNotSubtype("{{int f1} f2}","{null f1,any f2}"); }
	@Test public void test_5206() { checkNotSubtype("{{int f1} f2}","{null f2,any f3}"); }
	@Test public void test_5207() { checkNotSubtype("{{int f1} f2}","{null f1,null f2}"); }
	@Test public void test_5208() { checkNotSubtype("{{int f1} f2}","{null f2,null f3}"); }
	@Test public void test_5209() { checkNotSubtype("{{int f1} f2}","{null f1,int f2}"); }
	@Test public void test_5210() { checkNotSubtype("{{int f1} f2}","{null f2,int f3}"); }
	@Test public void test_5211() { checkIsSubtype("{{int f1} f2}","{int f1,void f2}"); }
	@Test public void test_5212() { checkIsSubtype("{{int f1} f2}","{int f2,void f3}"); }
	@Test public void test_5213() { checkNotSubtype("{{int f1} f2}","{int f1,any f2}"); }
	@Test public void test_5214() { checkNotSubtype("{{int f1} f2}","{int f2,any f3}"); }
	@Test public void test_5215() { checkNotSubtype("{{int f1} f2}","{int f1,null f2}"); }
	@Test public void test_5216() { checkNotSubtype("{{int f1} f2}","{int f2,null f3}"); }
	@Test public void test_5217() { checkNotSubtype("{{int f1} f2}","{int f1,int f2}"); }
	@Test public void test_5218() { checkNotSubtype("{{int f1} f2}","{int f2,int f3}"); }
	@Test public void test_5219() { checkIsSubtype("{{int f1} f2}","{{void f1} f1}"); }
	@Test public void test_5220() { checkIsSubtype("{{int f1} f2}","{{void f2} f1}"); }
	@Test public void test_5221() { checkIsSubtype("{{int f1} f2}","{{void f1} f2}"); }
	@Test public void test_5222() { checkIsSubtype("{{int f1} f2}","{{void f2} f2}"); }
	@Test public void test_5223() { checkIsSubtype("{{int f1} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_5224() { checkIsSubtype("{{int f1} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_5225() { checkIsSubtype("{{int f1} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_5226() { checkIsSubtype("{{int f1} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_5227() { checkNotSubtype("{{int f1} f2}","{{any f1} f1}"); }
	@Test public void test_5228() { checkNotSubtype("{{int f1} f2}","{{any f2} f1}"); }
	@Test public void test_5229() { checkNotSubtype("{{int f1} f2}","{{any f1} f2}"); }
	@Test public void test_5230() { checkNotSubtype("{{int f1} f2}","{{any f2} f2}"); }
	@Test public void test_5231() { checkNotSubtype("{{int f1} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_5232() { checkNotSubtype("{{int f1} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_5233() { checkNotSubtype("{{int f1} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_5234() { checkNotSubtype("{{int f1} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_5235() { checkNotSubtype("{{int f1} f2}","{{null f1} f1}"); }
	@Test public void test_5236() { checkNotSubtype("{{int f1} f2}","{{null f2} f1}"); }
	@Test public void test_5237() { checkNotSubtype("{{int f1} f2}","{{null f1} f2}"); }
	@Test public void test_5238() { checkNotSubtype("{{int f1} f2}","{{null f2} f2}"); }
	@Test public void test_5239() { checkNotSubtype("{{int f1} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_5240() { checkNotSubtype("{{int f1} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_5241() { checkNotSubtype("{{int f1} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_5242() { checkNotSubtype("{{int f1} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_5243() { checkNotSubtype("{{int f1} f2}","{{int f1} f1}"); }
	@Test public void test_5244() { checkNotSubtype("{{int f1} f2}","{{int f2} f1}"); }
	@Test public void test_5245() { checkIsSubtype("{{int f1} f2}","{{int f1} f2}"); }
	@Test public void test_5246() { checkNotSubtype("{{int f1} f2}","{{int f2} f2}"); }
	@Test public void test_5247() { checkNotSubtype("{{int f1} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_5248() { checkNotSubtype("{{int f1} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_5249() { checkNotSubtype("{{int f1} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_5250() { checkNotSubtype("{{int f1} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_5251() { checkNotSubtype("{{int f2} f2}","any"); }
	@Test public void test_5252() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_5253() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_5254() { checkIsSubtype("{{int f2} f2}","{void f1}"); }
	@Test public void test_5255() { checkIsSubtype("{{int f2} f2}","{void f2}"); }
	@Test public void test_5256() { checkNotSubtype("{{int f2} f2}","{any f1}"); }
	@Test public void test_5257() { checkNotSubtype("{{int f2} f2}","{any f2}"); }
	@Test public void test_5258() { checkNotSubtype("{{int f2} f2}","{null f1}"); }
	@Test public void test_5259() { checkNotSubtype("{{int f2} f2}","{null f2}"); }
	@Test public void test_5260() { checkNotSubtype("{{int f2} f2}","{int f1}"); }
	@Test public void test_5261() { checkNotSubtype("{{int f2} f2}","{int f2}"); }
	@Test public void test_5262() { checkIsSubtype("{{int f2} f2}","{void f1,void f2}"); }
	@Test public void test_5263() { checkIsSubtype("{{int f2} f2}","{void f2,void f3}"); }
	@Test public void test_5264() { checkIsSubtype("{{int f2} f2}","{void f1,any f2}"); }
	@Test public void test_5265() { checkIsSubtype("{{int f2} f2}","{void f2,any f3}"); }
	@Test public void test_5266() { checkIsSubtype("{{int f2} f2}","{void f1,null f2}"); }
	@Test public void test_5267() { checkIsSubtype("{{int f2} f2}","{void f2,null f3}"); }
	@Test public void test_5268() { checkIsSubtype("{{int f2} f2}","{void f1,int f2}"); }
	@Test public void test_5269() { checkIsSubtype("{{int f2} f2}","{void f2,int f3}"); }
	@Test public void test_5270() { checkIsSubtype("{{int f2} f2}","{any f1,void f2}"); }
	@Test public void test_5271() { checkIsSubtype("{{int f2} f2}","{any f2,void f3}"); }
	@Test public void test_5272() { checkNotSubtype("{{int f2} f2}","{any f1,any f2}"); }
	@Test public void test_5273() { checkNotSubtype("{{int f2} f2}","{any f2,any f3}"); }
	@Test public void test_5274() { checkNotSubtype("{{int f2} f2}","{any f1,null f2}"); }
	@Test public void test_5275() { checkNotSubtype("{{int f2} f2}","{any f2,null f3}"); }
	@Test public void test_5276() { checkNotSubtype("{{int f2} f2}","{any f1,int f2}"); }
	@Test public void test_5277() { checkNotSubtype("{{int f2} f2}","{any f2,int f3}"); }
	@Test public void test_5278() { checkIsSubtype("{{int f2} f2}","{null f1,void f2}"); }
	@Test public void test_5279() { checkIsSubtype("{{int f2} f2}","{null f2,void f3}"); }
	@Test public void test_5280() { checkNotSubtype("{{int f2} f2}","{null f1,any f2}"); }
	@Test public void test_5281() { checkNotSubtype("{{int f2} f2}","{null f2,any f3}"); }
	@Test public void test_5282() { checkNotSubtype("{{int f2} f2}","{null f1,null f2}"); }
	@Test public void test_5283() { checkNotSubtype("{{int f2} f2}","{null f2,null f3}"); }
	@Test public void test_5284() { checkNotSubtype("{{int f2} f2}","{null f1,int f2}"); }
	@Test public void test_5285() { checkNotSubtype("{{int f2} f2}","{null f2,int f3}"); }
	@Test public void test_5286() { checkIsSubtype("{{int f2} f2}","{int f1,void f2}"); }
	@Test public void test_5287() { checkIsSubtype("{{int f2} f2}","{int f2,void f3}"); }
	@Test public void test_5288() { checkNotSubtype("{{int f2} f2}","{int f1,any f2}"); }
	@Test public void test_5289() { checkNotSubtype("{{int f2} f2}","{int f2,any f3}"); }
	@Test public void test_5290() { checkNotSubtype("{{int f2} f2}","{int f1,null f2}"); }
	@Test public void test_5291() { checkNotSubtype("{{int f2} f2}","{int f2,null f3}"); }
	@Test public void test_5292() { checkNotSubtype("{{int f2} f2}","{int f1,int f2}"); }
	@Test public void test_5293() { checkNotSubtype("{{int f2} f2}","{int f2,int f3}"); }
	@Test public void test_5294() { checkIsSubtype("{{int f2} f2}","{{void f1} f1}"); }
	@Test public void test_5295() { checkIsSubtype("{{int f2} f2}","{{void f2} f1}"); }
	@Test public void test_5296() { checkIsSubtype("{{int f2} f2}","{{void f1} f2}"); }
	@Test public void test_5297() { checkIsSubtype("{{int f2} f2}","{{void f2} f2}"); }
	@Test public void test_5298() { checkIsSubtype("{{int f2} f2}","{{void f1} f1,void f2}"); }
	@Test public void test_5299() { checkIsSubtype("{{int f2} f2}","{{void f2} f1,void f2}"); }
	@Test public void test_5300() { checkIsSubtype("{{int f2} f2}","{{void f1} f2,void f3}"); }
	@Test public void test_5301() { checkIsSubtype("{{int f2} f2}","{{void f2} f2,void f3}"); }
	@Test public void test_5302() { checkNotSubtype("{{int f2} f2}","{{any f1} f1}"); }
	@Test public void test_5303() { checkNotSubtype("{{int f2} f2}","{{any f2} f1}"); }
	@Test public void test_5304() { checkNotSubtype("{{int f2} f2}","{{any f1} f2}"); }
	@Test public void test_5305() { checkNotSubtype("{{int f2} f2}","{{any f2} f2}"); }
	@Test public void test_5306() { checkNotSubtype("{{int f2} f2}","{{any f1} f1,any f2}"); }
	@Test public void test_5307() { checkNotSubtype("{{int f2} f2}","{{any f2} f1,any f2}"); }
	@Test public void test_5308() { checkNotSubtype("{{int f2} f2}","{{any f1} f2,any f3}"); }
	@Test public void test_5309() { checkNotSubtype("{{int f2} f2}","{{any f2} f2,any f3}"); }
	@Test public void test_5310() { checkNotSubtype("{{int f2} f2}","{{null f1} f1}"); }
	@Test public void test_5311() { checkNotSubtype("{{int f2} f2}","{{null f2} f1}"); }
	@Test public void test_5312() { checkNotSubtype("{{int f2} f2}","{{null f1} f2}"); }
	@Test public void test_5313() { checkNotSubtype("{{int f2} f2}","{{null f2} f2}"); }
	@Test public void test_5314() { checkNotSubtype("{{int f2} f2}","{{null f1} f1,null f2}"); }
	@Test public void test_5315() { checkNotSubtype("{{int f2} f2}","{{null f2} f1,null f2}"); }
	@Test public void test_5316() { checkNotSubtype("{{int f2} f2}","{{null f1} f2,null f3}"); }
	@Test public void test_5317() { checkNotSubtype("{{int f2} f2}","{{null f2} f2,null f3}"); }
	@Test public void test_5318() { checkNotSubtype("{{int f2} f2}","{{int f1} f1}"); }
	@Test public void test_5319() { checkNotSubtype("{{int f2} f2}","{{int f2} f1}"); }
	@Test public void test_5320() { checkNotSubtype("{{int f2} f2}","{{int f1} f2}"); }
	@Test public void test_5321() { checkIsSubtype("{{int f2} f2}","{{int f2} f2}"); }
	@Test public void test_5322() { checkNotSubtype("{{int f2} f2}","{{int f1} f1,int f2}"); }
	@Test public void test_5323() { checkNotSubtype("{{int f2} f2}","{{int f2} f1,int f2}"); }
	@Test public void test_5324() { checkNotSubtype("{{int f2} f2}","{{int f1} f2,int f3}"); }
	@Test public void test_5325() { checkNotSubtype("{{int f2} f2}","{{int f2} f2,int f3}"); }
	@Test public void test_5326() { checkNotSubtype("{{int f1} f1,int f2}","any"); }
	@Test public void test_5327() { checkNotSubtype("{{int f1} f1,int f2}","null"); }
	@Test public void test_5328() { checkNotSubtype("{{int f1} f1,int f2}","int"); }
	@Test public void test_5329() { checkIsSubtype("{{int f1} f1,int f2}","{void f1}"); }
	@Test public void test_5330() { checkIsSubtype("{{int f1} f1,int f2}","{void f2}"); }
	@Test public void test_5331() { checkNotSubtype("{{int f1} f1,int f2}","{any f1}"); }
	@Test public void test_5332() { checkNotSubtype("{{int f1} f1,int f2}","{any f2}"); }
	@Test public void test_5333() { checkNotSubtype("{{int f1} f1,int f2}","{null f1}"); }
	@Test public void test_5334() { checkNotSubtype("{{int f1} f1,int f2}","{null f2}"); }
	@Test public void test_5335() { checkNotSubtype("{{int f1} f1,int f2}","{int f1}"); }
	@Test public void test_5336() { checkNotSubtype("{{int f1} f1,int f2}","{int f2}"); }
	@Test public void test_5337() { checkIsSubtype("{{int f1} f1,int f2}","{void f1,void f2}"); }
	@Test public void test_5338() { checkIsSubtype("{{int f1} f1,int f2}","{void f2,void f3}"); }
	@Test public void test_5339() { checkIsSubtype("{{int f1} f1,int f2}","{void f1,any f2}"); }
	@Test public void test_5340() { checkIsSubtype("{{int f1} f1,int f2}","{void f2,any f3}"); }
	@Test public void test_5341() { checkIsSubtype("{{int f1} f1,int f2}","{void f1,null f2}"); }
	@Test public void test_5342() { checkIsSubtype("{{int f1} f1,int f2}","{void f2,null f3}"); }
	@Test public void test_5343() { checkIsSubtype("{{int f1} f1,int f2}","{void f1,int f2}"); }
	@Test public void test_5344() { checkIsSubtype("{{int f1} f1,int f2}","{void f2,int f3}"); }
	@Test public void test_5345() { checkIsSubtype("{{int f1} f1,int f2}","{any f1,void f2}"); }
	@Test public void test_5346() { checkIsSubtype("{{int f1} f1,int f2}","{any f2,void f3}"); }
	@Test public void test_5347() { checkNotSubtype("{{int f1} f1,int f2}","{any f1,any f2}"); }
	@Test public void test_5348() { checkNotSubtype("{{int f1} f1,int f2}","{any f2,any f3}"); }
	@Test public void test_5349() { checkNotSubtype("{{int f1} f1,int f2}","{any f1,null f2}"); }
	@Test public void test_5350() { checkNotSubtype("{{int f1} f1,int f2}","{any f2,null f3}"); }
	@Test public void test_5351() { checkNotSubtype("{{int f1} f1,int f2}","{any f1,int f2}"); }
	@Test public void test_5352() { checkNotSubtype("{{int f1} f1,int f2}","{any f2,int f3}"); }
	@Test public void test_5353() { checkIsSubtype("{{int f1} f1,int f2}","{null f1,void f2}"); }
	@Test public void test_5354() { checkIsSubtype("{{int f1} f1,int f2}","{null f2,void f3}"); }
	@Test public void test_5355() { checkNotSubtype("{{int f1} f1,int f2}","{null f1,any f2}"); }
	@Test public void test_5356() { checkNotSubtype("{{int f1} f1,int f2}","{null f2,any f3}"); }
	@Test public void test_5357() { checkNotSubtype("{{int f1} f1,int f2}","{null f1,null f2}"); }
	@Test public void test_5358() { checkNotSubtype("{{int f1} f1,int f2}","{null f2,null f3}"); }
	@Test public void test_5359() { checkNotSubtype("{{int f1} f1,int f2}","{null f1,int f2}"); }
	@Test public void test_5360() { checkNotSubtype("{{int f1} f1,int f2}","{null f2,int f3}"); }
	@Test public void test_5361() { checkIsSubtype("{{int f1} f1,int f2}","{int f1,void f2}"); }
	@Test public void test_5362() { checkIsSubtype("{{int f1} f1,int f2}","{int f2,void f3}"); }
	@Test public void test_5363() { checkNotSubtype("{{int f1} f1,int f2}","{int f1,any f2}"); }
	@Test public void test_5364() { checkNotSubtype("{{int f1} f1,int f2}","{int f2,any f3}"); }
	@Test public void test_5365() { checkNotSubtype("{{int f1} f1,int f2}","{int f1,null f2}"); }
	@Test public void test_5366() { checkNotSubtype("{{int f1} f1,int f2}","{int f2,null f3}"); }
	@Test public void test_5367() { checkNotSubtype("{{int f1} f1,int f2}","{int f1,int f2}"); }
	@Test public void test_5368() { checkNotSubtype("{{int f1} f1,int f2}","{int f2,int f3}"); }
	@Test public void test_5369() { checkIsSubtype("{{int f1} f1,int f2}","{{void f1} f1}"); }
	@Test public void test_5370() { checkIsSubtype("{{int f1} f1,int f2}","{{void f2} f1}"); }
	@Test public void test_5371() { checkIsSubtype("{{int f1} f1,int f2}","{{void f1} f2}"); }
	@Test public void test_5372() { checkIsSubtype("{{int f1} f1,int f2}","{{void f2} f2}"); }
	@Test public void test_5373() { checkIsSubtype("{{int f1} f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_5374() { checkIsSubtype("{{int f1} f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_5375() { checkIsSubtype("{{int f1} f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_5376() { checkIsSubtype("{{int f1} f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_5377() { checkNotSubtype("{{int f1} f1,int f2}","{{any f1} f1}"); }
	@Test public void test_5378() { checkNotSubtype("{{int f1} f1,int f2}","{{any f2} f1}"); }
	@Test public void test_5379() { checkNotSubtype("{{int f1} f1,int f2}","{{any f1} f2}"); }
	@Test public void test_5380() { checkNotSubtype("{{int f1} f1,int f2}","{{any f2} f2}"); }
	@Test public void test_5381() { checkNotSubtype("{{int f1} f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_5382() { checkNotSubtype("{{int f1} f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_5383() { checkNotSubtype("{{int f1} f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_5384() { checkNotSubtype("{{int f1} f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_5385() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f1}"); }
	@Test public void test_5386() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f1}"); }
	@Test public void test_5387() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f2}"); }
	@Test public void test_5388() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f2}"); }
	@Test public void test_5389() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_5390() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_5391() { checkNotSubtype("{{int f1} f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_5392() { checkNotSubtype("{{int f1} f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_5393() { checkNotSubtype("{{int f1} f1,int f2}","{{int f1} f1}"); }
	@Test public void test_5394() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f1}"); }
	@Test public void test_5395() { checkNotSubtype("{{int f1} f1,int f2}","{{int f1} f2}"); }
	@Test public void test_5396() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f2}"); }
	@Test public void test_5397() { checkIsSubtype("{{int f1} f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_5398() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_5399() { checkNotSubtype("{{int f1} f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_5400() { checkNotSubtype("{{int f1} f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_5401() { checkNotSubtype("{{int f2} f1,int f2}","any"); }
	@Test public void test_5402() { checkNotSubtype("{{int f2} f1,int f2}","null"); }
	@Test public void test_5403() { checkNotSubtype("{{int f2} f1,int f2}","int"); }
	@Test public void test_5404() { checkIsSubtype("{{int f2} f1,int f2}","{void f1}"); }
	@Test public void test_5405() { checkIsSubtype("{{int f2} f1,int f2}","{void f2}"); }
	@Test public void test_5406() { checkNotSubtype("{{int f2} f1,int f2}","{any f1}"); }
	@Test public void test_5407() { checkNotSubtype("{{int f2} f1,int f2}","{any f2}"); }
	@Test public void test_5408() { checkNotSubtype("{{int f2} f1,int f2}","{null f1}"); }
	@Test public void test_5409() { checkNotSubtype("{{int f2} f1,int f2}","{null f2}"); }
	@Test public void test_5410() { checkNotSubtype("{{int f2} f1,int f2}","{int f1}"); }
	@Test public void test_5411() { checkNotSubtype("{{int f2} f1,int f2}","{int f2}"); }
	@Test public void test_5412() { checkIsSubtype("{{int f2} f1,int f2}","{void f1,void f2}"); }
	@Test public void test_5413() { checkIsSubtype("{{int f2} f1,int f2}","{void f2,void f3}"); }
	@Test public void test_5414() { checkIsSubtype("{{int f2} f1,int f2}","{void f1,any f2}"); }
	@Test public void test_5415() { checkIsSubtype("{{int f2} f1,int f2}","{void f2,any f3}"); }
	@Test public void test_5416() { checkIsSubtype("{{int f2} f1,int f2}","{void f1,null f2}"); }
	@Test public void test_5417() { checkIsSubtype("{{int f2} f1,int f2}","{void f2,null f3}"); }
	@Test public void test_5418() { checkIsSubtype("{{int f2} f1,int f2}","{void f1,int f2}"); }
	@Test public void test_5419() { checkIsSubtype("{{int f2} f1,int f2}","{void f2,int f3}"); }
	@Test public void test_5420() { checkIsSubtype("{{int f2} f1,int f2}","{any f1,void f2}"); }
	@Test public void test_5421() { checkIsSubtype("{{int f2} f1,int f2}","{any f2,void f3}"); }
	@Test public void test_5422() { checkNotSubtype("{{int f2} f1,int f2}","{any f1,any f2}"); }
	@Test public void test_5423() { checkNotSubtype("{{int f2} f1,int f2}","{any f2,any f3}"); }
	@Test public void test_5424() { checkNotSubtype("{{int f2} f1,int f2}","{any f1,null f2}"); }
	@Test public void test_5425() { checkNotSubtype("{{int f2} f1,int f2}","{any f2,null f3}"); }
	@Test public void test_5426() { checkNotSubtype("{{int f2} f1,int f2}","{any f1,int f2}"); }
	@Test public void test_5427() { checkNotSubtype("{{int f2} f1,int f2}","{any f2,int f3}"); }
	@Test public void test_5428() { checkIsSubtype("{{int f2} f1,int f2}","{null f1,void f2}"); }
	@Test public void test_5429() { checkIsSubtype("{{int f2} f1,int f2}","{null f2,void f3}"); }
	@Test public void test_5430() { checkNotSubtype("{{int f2} f1,int f2}","{null f1,any f2}"); }
	@Test public void test_5431() { checkNotSubtype("{{int f2} f1,int f2}","{null f2,any f3}"); }
	@Test public void test_5432() { checkNotSubtype("{{int f2} f1,int f2}","{null f1,null f2}"); }
	@Test public void test_5433() { checkNotSubtype("{{int f2} f1,int f2}","{null f2,null f3}"); }
	@Test public void test_5434() { checkNotSubtype("{{int f2} f1,int f2}","{null f1,int f2}"); }
	@Test public void test_5435() { checkNotSubtype("{{int f2} f1,int f2}","{null f2,int f3}"); }
	@Test public void test_5436() { checkIsSubtype("{{int f2} f1,int f2}","{int f1,void f2}"); }
	@Test public void test_5437() { checkIsSubtype("{{int f2} f1,int f2}","{int f2,void f3}"); }
	@Test public void test_5438() { checkNotSubtype("{{int f2} f1,int f2}","{int f1,any f2}"); }
	@Test public void test_5439() { checkNotSubtype("{{int f2} f1,int f2}","{int f2,any f3}"); }
	@Test public void test_5440() { checkNotSubtype("{{int f2} f1,int f2}","{int f1,null f2}"); }
	@Test public void test_5441() { checkNotSubtype("{{int f2} f1,int f2}","{int f2,null f3}"); }
	@Test public void test_5442() { checkNotSubtype("{{int f2} f1,int f2}","{int f1,int f2}"); }
	@Test public void test_5443() { checkNotSubtype("{{int f2} f1,int f2}","{int f2,int f3}"); }
	@Test public void test_5444() { checkIsSubtype("{{int f2} f1,int f2}","{{void f1} f1}"); }
	@Test public void test_5445() { checkIsSubtype("{{int f2} f1,int f2}","{{void f2} f1}"); }
	@Test public void test_5446() { checkIsSubtype("{{int f2} f1,int f2}","{{void f1} f2}"); }
	@Test public void test_5447() { checkIsSubtype("{{int f2} f1,int f2}","{{void f2} f2}"); }
	@Test public void test_5448() { checkIsSubtype("{{int f2} f1,int f2}","{{void f1} f1,void f2}"); }
	@Test public void test_5449() { checkIsSubtype("{{int f2} f1,int f2}","{{void f2} f1,void f2}"); }
	@Test public void test_5450() { checkIsSubtype("{{int f2} f1,int f2}","{{void f1} f2,void f3}"); }
	@Test public void test_5451() { checkIsSubtype("{{int f2} f1,int f2}","{{void f2} f2,void f3}"); }
	@Test public void test_5452() { checkNotSubtype("{{int f2} f1,int f2}","{{any f1} f1}"); }
	@Test public void test_5453() { checkNotSubtype("{{int f2} f1,int f2}","{{any f2} f1}"); }
	@Test public void test_5454() { checkNotSubtype("{{int f2} f1,int f2}","{{any f1} f2}"); }
	@Test public void test_5455() { checkNotSubtype("{{int f2} f1,int f2}","{{any f2} f2}"); }
	@Test public void test_5456() { checkNotSubtype("{{int f2} f1,int f2}","{{any f1} f1,any f2}"); }
	@Test public void test_5457() { checkNotSubtype("{{int f2} f1,int f2}","{{any f2} f1,any f2}"); }
	@Test public void test_5458() { checkNotSubtype("{{int f2} f1,int f2}","{{any f1} f2,any f3}"); }
	@Test public void test_5459() { checkNotSubtype("{{int f2} f1,int f2}","{{any f2} f2,any f3}"); }
	@Test public void test_5460() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f1}"); }
	@Test public void test_5461() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f1}"); }
	@Test public void test_5462() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f2}"); }
	@Test public void test_5463() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f2}"); }
	@Test public void test_5464() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f1,null f2}"); }
	@Test public void test_5465() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f1,null f2}"); }
	@Test public void test_5466() { checkNotSubtype("{{int f2} f1,int f2}","{{null f1} f2,null f3}"); }
	@Test public void test_5467() { checkNotSubtype("{{int f2} f1,int f2}","{{null f2} f2,null f3}"); }
	@Test public void test_5468() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f1}"); }
	@Test public void test_5469() { checkNotSubtype("{{int f2} f1,int f2}","{{int f2} f1}"); }
	@Test public void test_5470() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f2}"); }
	@Test public void test_5471() { checkNotSubtype("{{int f2} f1,int f2}","{{int f2} f2}"); }
	@Test public void test_5472() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f1,int f2}"); }
	@Test public void test_5473() { checkIsSubtype("{{int f2} f1,int f2}","{{int f2} f1,int f2}"); }
	@Test public void test_5474() { checkNotSubtype("{{int f2} f1,int f2}","{{int f1} f2,int f3}"); }
	@Test public void test_5475() { checkNotSubtype("{{int f2} f1,int f2}","{{int f2} f2,int f3}"); }
	@Test public void test_5476() { checkNotSubtype("{{int f1} f2,int f3}","any"); }
	@Test public void test_5477() { checkNotSubtype("{{int f1} f2,int f3}","null"); }
	@Test public void test_5478() { checkNotSubtype("{{int f1} f2,int f3}","int"); }
	@Test public void test_5479() { checkIsSubtype("{{int f1} f2,int f3}","{void f1}"); }
	@Test public void test_5480() { checkIsSubtype("{{int f1} f2,int f3}","{void f2}"); }
	@Test public void test_5481() { checkNotSubtype("{{int f1} f2,int f3}","{any f1}"); }
	@Test public void test_5482() { checkNotSubtype("{{int f1} f2,int f3}","{any f2}"); }
	@Test public void test_5483() { checkNotSubtype("{{int f1} f2,int f3}","{null f1}"); }
	@Test public void test_5484() { checkNotSubtype("{{int f1} f2,int f3}","{null f2}"); }
	@Test public void test_5485() { checkNotSubtype("{{int f1} f2,int f3}","{int f1}"); }
	@Test public void test_5486() { checkNotSubtype("{{int f1} f2,int f3}","{int f2}"); }
	@Test public void test_5487() { checkIsSubtype("{{int f1} f2,int f3}","{void f1,void f2}"); }
	@Test public void test_5488() { checkIsSubtype("{{int f1} f2,int f3}","{void f2,void f3}"); }
	@Test public void test_5489() { checkIsSubtype("{{int f1} f2,int f3}","{void f1,any f2}"); }
	@Test public void test_5490() { checkIsSubtype("{{int f1} f2,int f3}","{void f2,any f3}"); }
	@Test public void test_5491() { checkIsSubtype("{{int f1} f2,int f3}","{void f1,null f2}"); }
	@Test public void test_5492() { checkIsSubtype("{{int f1} f2,int f3}","{void f2,null f3}"); }
	@Test public void test_5493() { checkIsSubtype("{{int f1} f2,int f3}","{void f1,int f2}"); }
	@Test public void test_5494() { checkIsSubtype("{{int f1} f2,int f3}","{void f2,int f3}"); }
	@Test public void test_5495() { checkIsSubtype("{{int f1} f2,int f3}","{any f1,void f2}"); }
	@Test public void test_5496() { checkIsSubtype("{{int f1} f2,int f3}","{any f2,void f3}"); }
	@Test public void test_5497() { checkNotSubtype("{{int f1} f2,int f3}","{any f1,any f2}"); }
	@Test public void test_5498() { checkNotSubtype("{{int f1} f2,int f3}","{any f2,any f3}"); }
	@Test public void test_5499() { checkNotSubtype("{{int f1} f2,int f3}","{any f1,null f2}"); }
	@Test public void test_5500() { checkNotSubtype("{{int f1} f2,int f3}","{any f2,null f3}"); }
	@Test public void test_5501() { checkNotSubtype("{{int f1} f2,int f3}","{any f1,int f2}"); }
	@Test public void test_5502() { checkNotSubtype("{{int f1} f2,int f3}","{any f2,int f3}"); }
	@Test public void test_5503() { checkIsSubtype("{{int f1} f2,int f3}","{null f1,void f2}"); }
	@Test public void test_5504() { checkIsSubtype("{{int f1} f2,int f3}","{null f2,void f3}"); }
	@Test public void test_5505() { checkNotSubtype("{{int f1} f2,int f3}","{null f1,any f2}"); }
	@Test public void test_5506() { checkNotSubtype("{{int f1} f2,int f3}","{null f2,any f3}"); }
	@Test public void test_5507() { checkNotSubtype("{{int f1} f2,int f3}","{null f1,null f2}"); }
	@Test public void test_5508() { checkNotSubtype("{{int f1} f2,int f3}","{null f2,null f3}"); }
	@Test public void test_5509() { checkNotSubtype("{{int f1} f2,int f3}","{null f1,int f2}"); }
	@Test public void test_5510() { checkNotSubtype("{{int f1} f2,int f3}","{null f2,int f3}"); }
	@Test public void test_5511() { checkIsSubtype("{{int f1} f2,int f3}","{int f1,void f2}"); }
	@Test public void test_5512() { checkIsSubtype("{{int f1} f2,int f3}","{int f2,void f3}"); }
	@Test public void test_5513() { checkNotSubtype("{{int f1} f2,int f3}","{int f1,any f2}"); }
	@Test public void test_5514() { checkNotSubtype("{{int f1} f2,int f3}","{int f2,any f3}"); }
	@Test public void test_5515() { checkNotSubtype("{{int f1} f2,int f3}","{int f1,null f2}"); }
	@Test public void test_5516() { checkNotSubtype("{{int f1} f2,int f3}","{int f2,null f3}"); }
	@Test public void test_5517() { checkNotSubtype("{{int f1} f2,int f3}","{int f1,int f2}"); }
	@Test public void test_5518() { checkNotSubtype("{{int f1} f2,int f3}","{int f2,int f3}"); }
	@Test public void test_5519() { checkIsSubtype("{{int f1} f2,int f3}","{{void f1} f1}"); }
	@Test public void test_5520() { checkIsSubtype("{{int f1} f2,int f3}","{{void f2} f1}"); }
	@Test public void test_5521() { checkIsSubtype("{{int f1} f2,int f3}","{{void f1} f2}"); }
	@Test public void test_5522() { checkIsSubtype("{{int f1} f2,int f3}","{{void f2} f2}"); }
	@Test public void test_5523() { checkIsSubtype("{{int f1} f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_5524() { checkIsSubtype("{{int f1} f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_5525() { checkIsSubtype("{{int f1} f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_5526() { checkIsSubtype("{{int f1} f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_5527() { checkNotSubtype("{{int f1} f2,int f3}","{{any f1} f1}"); }
	@Test public void test_5528() { checkNotSubtype("{{int f1} f2,int f3}","{{any f2} f1}"); }
	@Test public void test_5529() { checkNotSubtype("{{int f1} f2,int f3}","{{any f1} f2}"); }
	@Test public void test_5530() { checkNotSubtype("{{int f1} f2,int f3}","{{any f2} f2}"); }
	@Test public void test_5531() { checkNotSubtype("{{int f1} f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_5532() { checkNotSubtype("{{int f1} f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_5533() { checkNotSubtype("{{int f1} f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_5534() { checkNotSubtype("{{int f1} f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_5535() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f1}"); }
	@Test public void test_5536() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f1}"); }
	@Test public void test_5537() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f2}"); }
	@Test public void test_5538() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f2}"); }
	@Test public void test_5539() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_5540() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_5541() { checkNotSubtype("{{int f1} f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_5542() { checkNotSubtype("{{int f1} f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_5543() { checkNotSubtype("{{int f1} f2,int f3}","{{int f1} f1}"); }
	@Test public void test_5544() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f1}"); }
	@Test public void test_5545() { checkNotSubtype("{{int f1} f2,int f3}","{{int f1} f2}"); }
	@Test public void test_5546() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f2}"); }
	@Test public void test_5547() { checkNotSubtype("{{int f1} f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_5548() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_5549() { checkIsSubtype("{{int f1} f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_5550() { checkNotSubtype("{{int f1} f2,int f3}","{{int f2} f2,int f3}"); }
	@Test public void test_5551() { checkNotSubtype("{{int f2} f2,int f3}","any"); }
	@Test public void test_5552() { checkNotSubtype("{{int f2} f2,int f3}","null"); }
	@Test public void test_5553() { checkNotSubtype("{{int f2} f2,int f3}","int"); }
	@Test public void test_5554() { checkIsSubtype("{{int f2} f2,int f3}","{void f1}"); }
	@Test public void test_5555() { checkIsSubtype("{{int f2} f2,int f3}","{void f2}"); }
	@Test public void test_5556() { checkNotSubtype("{{int f2} f2,int f3}","{any f1}"); }
	@Test public void test_5557() { checkNotSubtype("{{int f2} f2,int f3}","{any f2}"); }
	@Test public void test_5558() { checkNotSubtype("{{int f2} f2,int f3}","{null f1}"); }
	@Test public void test_5559() { checkNotSubtype("{{int f2} f2,int f3}","{null f2}"); }
	@Test public void test_5560() { checkNotSubtype("{{int f2} f2,int f3}","{int f1}"); }
	@Test public void test_5561() { checkNotSubtype("{{int f2} f2,int f3}","{int f2}"); }
	@Test public void test_5562() { checkIsSubtype("{{int f2} f2,int f3}","{void f1,void f2}"); }
	@Test public void test_5563() { checkIsSubtype("{{int f2} f2,int f3}","{void f2,void f3}"); }
	@Test public void test_5564() { checkIsSubtype("{{int f2} f2,int f3}","{void f1,any f2}"); }
	@Test public void test_5565() { checkIsSubtype("{{int f2} f2,int f3}","{void f2,any f3}"); }
	@Test public void test_5566() { checkIsSubtype("{{int f2} f2,int f3}","{void f1,null f2}"); }
	@Test public void test_5567() { checkIsSubtype("{{int f2} f2,int f3}","{void f2,null f3}"); }
	@Test public void test_5568() { checkIsSubtype("{{int f2} f2,int f3}","{void f1,int f2}"); }
	@Test public void test_5569() { checkIsSubtype("{{int f2} f2,int f3}","{void f2,int f3}"); }
	@Test public void test_5570() { checkIsSubtype("{{int f2} f2,int f3}","{any f1,void f2}"); }
	@Test public void test_5571() { checkIsSubtype("{{int f2} f2,int f3}","{any f2,void f3}"); }
	@Test public void test_5572() { checkNotSubtype("{{int f2} f2,int f3}","{any f1,any f2}"); }
	@Test public void test_5573() { checkNotSubtype("{{int f2} f2,int f3}","{any f2,any f3}"); }
	@Test public void test_5574() { checkNotSubtype("{{int f2} f2,int f3}","{any f1,null f2}"); }
	@Test public void test_5575() { checkNotSubtype("{{int f2} f2,int f3}","{any f2,null f3}"); }
	@Test public void test_5576() { checkNotSubtype("{{int f2} f2,int f3}","{any f1,int f2}"); }
	@Test public void test_5577() { checkNotSubtype("{{int f2} f2,int f3}","{any f2,int f3}"); }
	@Test public void test_5578() { checkIsSubtype("{{int f2} f2,int f3}","{null f1,void f2}"); }
	@Test public void test_5579() { checkIsSubtype("{{int f2} f2,int f3}","{null f2,void f3}"); }
	@Test public void test_5580() { checkNotSubtype("{{int f2} f2,int f3}","{null f1,any f2}"); }
	@Test public void test_5581() { checkNotSubtype("{{int f2} f2,int f3}","{null f2,any f3}"); }
	@Test public void test_5582() { checkNotSubtype("{{int f2} f2,int f3}","{null f1,null f2}"); }
	@Test public void test_5583() { checkNotSubtype("{{int f2} f2,int f3}","{null f2,null f3}"); }
	@Test public void test_5584() { checkNotSubtype("{{int f2} f2,int f3}","{null f1,int f2}"); }
	@Test public void test_5585() { checkNotSubtype("{{int f2} f2,int f3}","{null f2,int f3}"); }
	@Test public void test_5586() { checkIsSubtype("{{int f2} f2,int f3}","{int f1,void f2}"); }
	@Test public void test_5587() { checkIsSubtype("{{int f2} f2,int f3}","{int f2,void f3}"); }
	@Test public void test_5588() { checkNotSubtype("{{int f2} f2,int f3}","{int f1,any f2}"); }
	@Test public void test_5589() { checkNotSubtype("{{int f2} f2,int f3}","{int f2,any f3}"); }
	@Test public void test_5590() { checkNotSubtype("{{int f2} f2,int f3}","{int f1,null f2}"); }
	@Test public void test_5591() { checkNotSubtype("{{int f2} f2,int f3}","{int f2,null f3}"); }
	@Test public void test_5592() { checkNotSubtype("{{int f2} f2,int f3}","{int f1,int f2}"); }
	@Test public void test_5593() { checkNotSubtype("{{int f2} f2,int f3}","{int f2,int f3}"); }
	@Test public void test_5594() { checkIsSubtype("{{int f2} f2,int f3}","{{void f1} f1}"); }
	@Test public void test_5595() { checkIsSubtype("{{int f2} f2,int f3}","{{void f2} f1}"); }
	@Test public void test_5596() { checkIsSubtype("{{int f2} f2,int f3}","{{void f1} f2}"); }
	@Test public void test_5597() { checkIsSubtype("{{int f2} f2,int f3}","{{void f2} f2}"); }
	@Test public void test_5598() { checkIsSubtype("{{int f2} f2,int f3}","{{void f1} f1,void f2}"); }
	@Test public void test_5599() { checkIsSubtype("{{int f2} f2,int f3}","{{void f2} f1,void f2}"); }
	@Test public void test_5600() { checkIsSubtype("{{int f2} f2,int f3}","{{void f1} f2,void f3}"); }
	@Test public void test_5601() { checkIsSubtype("{{int f2} f2,int f3}","{{void f2} f2,void f3}"); }
	@Test public void test_5602() { checkNotSubtype("{{int f2} f2,int f3}","{{any f1} f1}"); }
	@Test public void test_5603() { checkNotSubtype("{{int f2} f2,int f3}","{{any f2} f1}"); }
	@Test public void test_5604() { checkNotSubtype("{{int f2} f2,int f3}","{{any f1} f2}"); }
	@Test public void test_5605() { checkNotSubtype("{{int f2} f2,int f3}","{{any f2} f2}"); }
	@Test public void test_5606() { checkNotSubtype("{{int f2} f2,int f3}","{{any f1} f1,any f2}"); }
	@Test public void test_5607() { checkNotSubtype("{{int f2} f2,int f3}","{{any f2} f1,any f2}"); }
	@Test public void test_5608() { checkNotSubtype("{{int f2} f2,int f3}","{{any f1} f2,any f3}"); }
	@Test public void test_5609() { checkNotSubtype("{{int f2} f2,int f3}","{{any f2} f2,any f3}"); }
	@Test public void test_5610() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f1}"); }
	@Test public void test_5611() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f1}"); }
	@Test public void test_5612() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f2}"); }
	@Test public void test_5613() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f2}"); }
	@Test public void test_5614() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f1,null f2}"); }
	@Test public void test_5615() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f1,null f2}"); }
	@Test public void test_5616() { checkNotSubtype("{{int f2} f2,int f3}","{{null f1} f2,null f3}"); }
	@Test public void test_5617() { checkNotSubtype("{{int f2} f2,int f3}","{{null f2} f2,null f3}"); }
	@Test public void test_5618() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f1}"); }
	@Test public void test_5619() { checkNotSubtype("{{int f2} f2,int f3}","{{int f2} f1}"); }
	@Test public void test_5620() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f2}"); }
	@Test public void test_5621() { checkNotSubtype("{{int f2} f2,int f3}","{{int f2} f2}"); }
	@Test public void test_5622() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f1,int f2}"); }
	@Test public void test_5623() { checkNotSubtype("{{int f2} f2,int f3}","{{int f2} f1,int f2}"); }
	@Test public void test_5624() { checkNotSubtype("{{int f2} f2,int f3}","{{int f1} f2,int f3}"); }
	@Test public void test_5625() { checkIsSubtype("{{int f2} f2,int f3}","{{int f2} f2,int f3}"); }

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
