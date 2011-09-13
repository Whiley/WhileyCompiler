// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class RecursiveSubtypeTests {
	@Test public void test_1() { checkIsSubtype("null","null"); }
	@Test public void test_2() { checkNotSubtype("null","int"); }
	@Test public void test_3() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_4() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_5() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_6() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_7() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_8() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_9() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_10() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_11() { checkNotSubtype("null","{{int f1} f1}"); }
	@Test public void test_12() { checkNotSubtype("null","{{int f2} f1}"); }
	@Test public void test_13() { checkNotSubtype("null","{{int f1} f2}"); }
	@Test public void test_14() { checkNotSubtype("null","{{int f2} f2}"); }
	@Test public void test_15() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test public void test_16() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test public void test_17() { checkNotSubtype("null","X<{X|int f1}>"); }
	@Test public void test_18() { checkNotSubtype("null","X<{X|int f2}>"); }
	@Test public void test_19() { checkIsSubtype("null","null|null"); }
	@Test public void test_20() { checkNotSubtype("null","null|int"); }
	@Test public void test_21() { checkNotSubtype("null","null|{null f1}"); }
	@Test public void test_22() { checkNotSubtype("null","null|{null f2}"); }
	@Test public void test_23() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test public void test_24() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test public void test_25() { checkIsSubtype("null","X<null|(X|null)>"); }
	@Test public void test_26() { checkNotSubtype("null","int|null"); }
	@Test public void test_27() { checkNotSubtype("null","int|int"); }
	@Test public void test_28() { checkNotSubtype("null","int|{int f1}"); }
	@Test public void test_29() { checkNotSubtype("null","int|{int f2}"); }
	@Test public void test_30() { checkNotSubtype("null","X<int|{X f1}>"); }
	@Test public void test_31() { checkNotSubtype("null","X<int|{X f2}>"); }
	@Test public void test_32() { checkNotSubtype("null","X<int|(X|int)>"); }
	@Test public void test_33() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_34() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_35() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test public void test_36() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test public void test_37() { checkNotSubtype("null","{int f1}|int"); }
	@Test public void test_38() { checkNotSubtype("null","{int f2}|int"); }
	@Test public void test_39() { checkNotSubtype("null","X<{X f1}|int>"); }
	@Test public void test_40() { checkNotSubtype("null","X<{X f2}|int>"); }
	@Test public void test_41() { checkIsSubtype("null","X<(X|null)|null>"); }
	@Test public void test_42() { checkNotSubtype("null","X<(X|int)|int>"); }
	@Test public void test_43() { checkNotSubtype("int","null"); }
	@Test public void test_44() { checkIsSubtype("int","int"); }
	@Test public void test_45() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_46() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_47() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_48() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_49() { checkNotSubtype("int","{{null f1} f1}"); }
	@Test public void test_50() { checkNotSubtype("int","{{null f2} f1}"); }
	@Test public void test_51() { checkNotSubtype("int","{{null f1} f2}"); }
	@Test public void test_52() { checkNotSubtype("int","{{null f2} f2}"); }
	@Test public void test_53() { checkNotSubtype("int","{{int f1} f1}"); }
	@Test public void test_54() { checkNotSubtype("int","{{int f2} f1}"); }
	@Test public void test_55() { checkNotSubtype("int","{{int f1} f2}"); }
	@Test public void test_56() { checkNotSubtype("int","{{int f2} f2}"); }
	@Test public void test_57() { checkNotSubtype("int","X<{X|null f1}>"); }
	@Test public void test_58() { checkNotSubtype("int","X<{X|null f2}>"); }
	@Test public void test_59() { checkNotSubtype("int","X<{X|int f1}>"); }
	@Test public void test_60() { checkNotSubtype("int","X<{X|int f2}>"); }
	@Test public void test_61() { checkNotSubtype("int","null|null"); }
	@Test public void test_62() { checkNotSubtype("int","null|int"); }
	@Test public void test_63() { checkNotSubtype("int","null|{null f1}"); }
	@Test public void test_64() { checkNotSubtype("int","null|{null f2}"); }
	@Test public void test_65() { checkNotSubtype("int","X<null|{X f1}>"); }
	@Test public void test_66() { checkNotSubtype("int","X<null|{X f2}>"); }
	@Test public void test_67() { checkNotSubtype("int","X<null|(X|null)>"); }
	@Test public void test_68() { checkNotSubtype("int","int|null"); }
	@Test public void test_69() { checkIsSubtype("int","int|int"); }
	@Test public void test_70() { checkNotSubtype("int","int|{int f1}"); }
	@Test public void test_71() { checkNotSubtype("int","int|{int f2}"); }
	@Test public void test_72() { checkNotSubtype("int","X<int|{X f1}>"); }
	@Test public void test_73() { checkNotSubtype("int","X<int|{X f2}>"); }
	@Test public void test_74() { checkIsSubtype("int","X<int|(X|int)>"); }
	@Test public void test_75() { checkNotSubtype("int","{null f1}|null"); }
	@Test public void test_76() { checkNotSubtype("int","{null f2}|null"); }
	@Test public void test_77() { checkNotSubtype("int","X<{X f1}|null>"); }
	@Test public void test_78() { checkNotSubtype("int","X<{X f2}|null>"); }
	@Test public void test_79() { checkNotSubtype("int","{int f1}|int"); }
	@Test public void test_80() { checkNotSubtype("int","{int f2}|int"); }
	@Test public void test_81() { checkNotSubtype("int","X<{X f1}|int>"); }
	@Test public void test_82() { checkNotSubtype("int","X<{X f2}|int>"); }
	@Test public void test_83() { checkNotSubtype("int","X<(X|null)|null>"); }
	@Test public void test_84() { checkIsSubtype("int","X<(X|int)|int>"); }
	@Test public void test_85() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_86() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_87() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_88() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_89() { checkNotSubtype("{null f1}","{int f1}"); }
	@Test public void test_90() { checkNotSubtype("{null f1}","{int f2}"); }
	@Test public void test_91() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test public void test_92() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test public void test_93() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test public void test_94() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test public void test_95() { checkNotSubtype("{null f1}","{{int f1} f1}"); }
	@Test public void test_96() { checkNotSubtype("{null f1}","{{int f2} f1}"); }
	@Test public void test_97() { checkNotSubtype("{null f1}","{{int f1} f2}"); }
	@Test public void test_98() { checkNotSubtype("{null f1}","{{int f2} f2}"); }
	@Test public void test_99() { checkNotSubtype("{null f1}","X<{X|null f1}>"); }
	@Test public void test_100() { checkNotSubtype("{null f1}","X<{X|null f2}>"); }
	@Test public void test_101() { checkNotSubtype("{null f1}","X<{X|int f1}>"); }
	@Test public void test_102() { checkNotSubtype("{null f1}","X<{X|int f2}>"); }
	@Test public void test_103() { checkNotSubtype("{null f1}","null|null"); }
	@Test public void test_104() { checkNotSubtype("{null f1}","null|int"); }
	@Test public void test_105() { checkNotSubtype("{null f1}","null|{null f1}"); }
	@Test public void test_106() { checkNotSubtype("{null f1}","null|{null f2}"); }
	@Test public void test_107() { checkNotSubtype("{null f1}","X<null|{X f1}>"); }
	@Test public void test_108() { checkNotSubtype("{null f1}","X<null|{X f2}>"); }
	@Test public void test_109() { checkNotSubtype("{null f1}","X<null|(X|null)>"); }
	@Test public void test_110() { checkNotSubtype("{null f1}","int|null"); }
	@Test public void test_111() { checkNotSubtype("{null f1}","int|int"); }
	@Test public void test_112() { checkNotSubtype("{null f1}","int|{int f1}"); }
	@Test public void test_113() { checkNotSubtype("{null f1}","int|{int f2}"); }
	@Test public void test_114() { checkNotSubtype("{null f1}","X<int|{X f1}>"); }
	@Test public void test_115() { checkNotSubtype("{null f1}","X<int|{X f2}>"); }
	@Test public void test_116() { checkNotSubtype("{null f1}","X<int|(X|int)>"); }
	@Test public void test_117() { checkNotSubtype("{null f1}","{null f1}|null"); }
	@Test public void test_118() { checkNotSubtype("{null f1}","{null f2}|null"); }
	@Test public void test_119() { checkNotSubtype("{null f1}","X<{X f1}|null>"); }
	@Test public void test_120() { checkNotSubtype("{null f1}","X<{X f2}|null>"); }
	@Test public void test_121() { checkNotSubtype("{null f1}","{int f1}|int"); }
	@Test public void test_122() { checkNotSubtype("{null f1}","{int f2}|int"); }
	@Test public void test_123() { checkNotSubtype("{null f1}","X<{X f1}|int>"); }
	@Test public void test_124() { checkNotSubtype("{null f1}","X<{X f2}|int>"); }
	@Test public void test_125() { checkNotSubtype("{null f1}","X<(X|null)|null>"); }
	@Test public void test_126() { checkNotSubtype("{null f1}","X<(X|int)|int>"); }
	@Test public void test_127() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_128() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_129() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_130() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_131() { checkNotSubtype("{null f2}","{int f1}"); }
	@Test public void test_132() { checkNotSubtype("{null f2}","{int f2}"); }
	@Test public void test_133() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test public void test_134() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test public void test_135() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test public void test_136() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test public void test_137() { checkNotSubtype("{null f2}","{{int f1} f1}"); }
	@Test public void test_138() { checkNotSubtype("{null f2}","{{int f2} f1}"); }
	@Test public void test_139() { checkNotSubtype("{null f2}","{{int f1} f2}"); }
	@Test public void test_140() { checkNotSubtype("{null f2}","{{int f2} f2}"); }
	@Test public void test_141() { checkNotSubtype("{null f2}","X<{X|null f1}>"); }
	@Test public void test_142() { checkNotSubtype("{null f2}","X<{X|null f2}>"); }
	@Test public void test_143() { checkNotSubtype("{null f2}","X<{X|int f1}>"); }
	@Test public void test_144() { checkNotSubtype("{null f2}","X<{X|int f2}>"); }
	@Test public void test_145() { checkNotSubtype("{null f2}","null|null"); }
	@Test public void test_146() { checkNotSubtype("{null f2}","null|int"); }
	@Test public void test_147() { checkNotSubtype("{null f2}","null|{null f1}"); }
	@Test public void test_148() { checkNotSubtype("{null f2}","null|{null f2}"); }
	@Test public void test_149() { checkNotSubtype("{null f2}","X<null|{X f1}>"); }
	@Test public void test_150() { checkNotSubtype("{null f2}","X<null|{X f2}>"); }
	@Test public void test_151() { checkNotSubtype("{null f2}","X<null|(X|null)>"); }
	@Test public void test_152() { checkNotSubtype("{null f2}","int|null"); }
	@Test public void test_153() { checkNotSubtype("{null f2}","int|int"); }
	@Test public void test_154() { checkNotSubtype("{null f2}","int|{int f1}"); }
	@Test public void test_155() { checkNotSubtype("{null f2}","int|{int f2}"); }
	@Test public void test_156() { checkNotSubtype("{null f2}","X<int|{X f1}>"); }
	@Test public void test_157() { checkNotSubtype("{null f2}","X<int|{X f2}>"); }
	@Test public void test_158() { checkNotSubtype("{null f2}","X<int|(X|int)>"); }
	@Test public void test_159() { checkNotSubtype("{null f2}","{null f1}|null"); }
	@Test public void test_160() { checkNotSubtype("{null f2}","{null f2}|null"); }
	@Test public void test_161() { checkNotSubtype("{null f2}","X<{X f1}|null>"); }
	@Test public void test_162() { checkNotSubtype("{null f2}","X<{X f2}|null>"); }
	@Test public void test_163() { checkNotSubtype("{null f2}","{int f1}|int"); }
	@Test public void test_164() { checkNotSubtype("{null f2}","{int f2}|int"); }
	@Test public void test_165() { checkNotSubtype("{null f2}","X<{X f1}|int>"); }
	@Test public void test_166() { checkNotSubtype("{null f2}","X<{X f2}|int>"); }
	@Test public void test_167() { checkNotSubtype("{null f2}","X<(X|null)|null>"); }
	@Test public void test_168() { checkNotSubtype("{null f2}","X<(X|int)|int>"); }
	@Test public void test_169() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_170() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_171() { checkNotSubtype("{int f1}","{null f1}"); }
	@Test public void test_172() { checkNotSubtype("{int f1}","{null f2}"); }
	@Test public void test_173() { checkIsSubtype("{int f1}","{int f1}"); }
	@Test public void test_174() { checkNotSubtype("{int f1}","{int f2}"); }
	@Test public void test_175() { checkNotSubtype("{int f1}","{{null f1} f1}"); }
	@Test public void test_176() { checkNotSubtype("{int f1}","{{null f2} f1}"); }
	@Test public void test_177() { checkNotSubtype("{int f1}","{{null f1} f2}"); }
	@Test public void test_178() { checkNotSubtype("{int f1}","{{null f2} f2}"); }
	@Test public void test_179() { checkNotSubtype("{int f1}","{{int f1} f1}"); }
	@Test public void test_180() { checkNotSubtype("{int f1}","{{int f2} f1}"); }
	@Test public void test_181() { checkNotSubtype("{int f1}","{{int f1} f2}"); }
	@Test public void test_182() { checkNotSubtype("{int f1}","{{int f2} f2}"); }
	@Test public void test_183() { checkNotSubtype("{int f1}","X<{X|null f1}>"); }
	@Test public void test_184() { checkNotSubtype("{int f1}","X<{X|null f2}>"); }
	@Test public void test_185() { checkNotSubtype("{int f1}","X<{X|int f1}>"); }
	@Test public void test_186() { checkNotSubtype("{int f1}","X<{X|int f2}>"); }
	@Test public void test_187() { checkNotSubtype("{int f1}","null|null"); }
	@Test public void test_188() { checkNotSubtype("{int f1}","null|int"); }
	@Test public void test_189() { checkNotSubtype("{int f1}","null|{null f1}"); }
	@Test public void test_190() { checkNotSubtype("{int f1}","null|{null f2}"); }
	@Test public void test_191() { checkNotSubtype("{int f1}","X<null|{X f1}>"); }
	@Test public void test_192() { checkNotSubtype("{int f1}","X<null|{X f2}>"); }
	@Test public void test_193() { checkNotSubtype("{int f1}","X<null|(X|null)>"); }
	@Test public void test_194() { checkNotSubtype("{int f1}","int|null"); }
	@Test public void test_195() { checkNotSubtype("{int f1}","int|int"); }
	@Test public void test_196() { checkNotSubtype("{int f1}","int|{int f1}"); }
	@Test public void test_197() { checkNotSubtype("{int f1}","int|{int f2}"); }
	@Test public void test_198() { checkNotSubtype("{int f1}","X<int|{X f1}>"); }
	@Test public void test_199() { checkNotSubtype("{int f1}","X<int|{X f2}>"); }
	@Test public void test_200() { checkNotSubtype("{int f1}","X<int|(X|int)>"); }
	@Test public void test_201() { checkNotSubtype("{int f1}","{null f1}|null"); }
	@Test public void test_202() { checkNotSubtype("{int f1}","{null f2}|null"); }
	@Test public void test_203() { checkNotSubtype("{int f1}","X<{X f1}|null>"); }
	@Test public void test_204() { checkNotSubtype("{int f1}","X<{X f2}|null>"); }
	@Test public void test_205() { checkNotSubtype("{int f1}","{int f1}|int"); }
	@Test public void test_206() { checkNotSubtype("{int f1}","{int f2}|int"); }
	@Test public void test_207() { checkNotSubtype("{int f1}","X<{X f1}|int>"); }
	@Test public void test_208() { checkNotSubtype("{int f1}","X<{X f2}|int>"); }
	@Test public void test_209() { checkNotSubtype("{int f1}","X<(X|null)|null>"); }
	@Test public void test_210() { checkNotSubtype("{int f1}","X<(X|int)|int>"); }
	@Test public void test_211() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_212() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_213() { checkNotSubtype("{int f2}","{null f1}"); }
	@Test public void test_214() { checkNotSubtype("{int f2}","{null f2}"); }
	@Test public void test_215() { checkNotSubtype("{int f2}","{int f1}"); }
	@Test public void test_216() { checkIsSubtype("{int f2}","{int f2}"); }
	@Test public void test_217() { checkNotSubtype("{int f2}","{{null f1} f1}"); }
	@Test public void test_218() { checkNotSubtype("{int f2}","{{null f2} f1}"); }
	@Test public void test_219() { checkNotSubtype("{int f2}","{{null f1} f2}"); }
	@Test public void test_220() { checkNotSubtype("{int f2}","{{null f2} f2}"); }
	@Test public void test_221() { checkNotSubtype("{int f2}","{{int f1} f1}"); }
	@Test public void test_222() { checkNotSubtype("{int f2}","{{int f2} f1}"); }
	@Test public void test_223() { checkNotSubtype("{int f2}","{{int f1} f2}"); }
	@Test public void test_224() { checkNotSubtype("{int f2}","{{int f2} f2}"); }
	@Test public void test_225() { checkNotSubtype("{int f2}","X<{X|null f1}>"); }
	@Test public void test_226() { checkNotSubtype("{int f2}","X<{X|null f2}>"); }
	@Test public void test_227() { checkNotSubtype("{int f2}","X<{X|int f1}>"); }
	@Test public void test_228() { checkNotSubtype("{int f2}","X<{X|int f2}>"); }
	@Test public void test_229() { checkNotSubtype("{int f2}","null|null"); }
	@Test public void test_230() { checkNotSubtype("{int f2}","null|int"); }
	@Test public void test_231() { checkNotSubtype("{int f2}","null|{null f1}"); }
	@Test public void test_232() { checkNotSubtype("{int f2}","null|{null f2}"); }
	@Test public void test_233() { checkNotSubtype("{int f2}","X<null|{X f1}>"); }
	@Test public void test_234() { checkNotSubtype("{int f2}","X<null|{X f2}>"); }
	@Test public void test_235() { checkNotSubtype("{int f2}","X<null|(X|null)>"); }
	@Test public void test_236() { checkNotSubtype("{int f2}","int|null"); }
	@Test public void test_237() { checkNotSubtype("{int f2}","int|int"); }
	@Test public void test_238() { checkNotSubtype("{int f2}","int|{int f1}"); }
	@Test public void test_239() { checkNotSubtype("{int f2}","int|{int f2}"); }
	@Test public void test_240() { checkNotSubtype("{int f2}","X<int|{X f1}>"); }
	@Test public void test_241() { checkNotSubtype("{int f2}","X<int|{X f2}>"); }
	@Test public void test_242() { checkNotSubtype("{int f2}","X<int|(X|int)>"); }
	@Test public void test_243() { checkNotSubtype("{int f2}","{null f1}|null"); }
	@Test public void test_244() { checkNotSubtype("{int f2}","{null f2}|null"); }
	@Test public void test_245() { checkNotSubtype("{int f2}","X<{X f1}|null>"); }
	@Test public void test_246() { checkNotSubtype("{int f2}","X<{X f2}|null>"); }
	@Test public void test_247() { checkNotSubtype("{int f2}","{int f1}|int"); }
	@Test public void test_248() { checkNotSubtype("{int f2}","{int f2}|int"); }
	@Test public void test_249() { checkNotSubtype("{int f2}","X<{X f1}|int>"); }
	@Test public void test_250() { checkNotSubtype("{int f2}","X<{X f2}|int>"); }
	@Test public void test_251() { checkNotSubtype("{int f2}","X<(X|null)|null>"); }
	@Test public void test_252() { checkNotSubtype("{int f2}","X<(X|int)|int>"); }
	@Test public void test_253() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_254() { checkNotSubtype("{{null f1} f1}","int"); }
	@Test public void test_255() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test public void test_256() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test public void test_257() { checkNotSubtype("{{null f1} f1}","{int f1}"); }
	@Test public void test_258() { checkNotSubtype("{{null f1} f1}","{int f2}"); }
	@Test public void test_259() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test public void test_260() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test public void test_261() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test public void test_262() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test public void test_263() { checkNotSubtype("{{null f1} f1}","{{int f1} f1}"); }
	@Test public void test_264() { checkNotSubtype("{{null f1} f1}","{{int f2} f1}"); }
	@Test public void test_265() { checkNotSubtype("{{null f1} f1}","{{int f1} f2}"); }
	@Test public void test_266() { checkNotSubtype("{{null f1} f1}","{{int f2} f2}"); }
	@Test public void test_267() { checkNotSubtype("{{null f1} f1}","X<{X|null f1}>"); }
	@Test public void test_268() { checkNotSubtype("{{null f1} f1}","X<{X|null f2}>"); }
	@Test public void test_269() { checkNotSubtype("{{null f1} f1}","X<{X|int f1}>"); }
	@Test public void test_270() { checkNotSubtype("{{null f1} f1}","X<{X|int f2}>"); }
	@Test public void test_271() { checkNotSubtype("{{null f1} f1}","null|null"); }
	@Test public void test_272() { checkNotSubtype("{{null f1} f1}","null|int"); }
	@Test public void test_273() { checkNotSubtype("{{null f1} f1}","null|{null f1}"); }
	@Test public void test_274() { checkNotSubtype("{{null f1} f1}","null|{null f2}"); }
	@Test public void test_275() { checkNotSubtype("{{null f1} f1}","X<null|{X f1}>"); }
	@Test public void test_276() { checkNotSubtype("{{null f1} f1}","X<null|{X f2}>"); }
	@Test public void test_277() { checkNotSubtype("{{null f1} f1}","X<null|(X|null)>"); }
	@Test public void test_278() { checkNotSubtype("{{null f1} f1}","int|null"); }
	@Test public void test_279() { checkNotSubtype("{{null f1} f1}","int|int"); }
	@Test public void test_280() { checkNotSubtype("{{null f1} f1}","int|{int f1}"); }
	@Test public void test_281() { checkNotSubtype("{{null f1} f1}","int|{int f2}"); }
	@Test public void test_282() { checkNotSubtype("{{null f1} f1}","X<int|{X f1}>"); }
	@Test public void test_283() { checkNotSubtype("{{null f1} f1}","X<int|{X f2}>"); }
	@Test public void test_284() { checkNotSubtype("{{null f1} f1}","X<int|(X|int)>"); }
	@Test public void test_285() { checkNotSubtype("{{null f1} f1}","{null f1}|null"); }
	@Test public void test_286() { checkNotSubtype("{{null f1} f1}","{null f2}|null"); }
	@Test public void test_287() { checkNotSubtype("{{null f1} f1}","X<{X f1}|null>"); }
	@Test public void test_288() { checkNotSubtype("{{null f1} f1}","X<{X f2}|null>"); }
	@Test public void test_289() { checkNotSubtype("{{null f1} f1}","{int f1}|int"); }
	@Test public void test_290() { checkNotSubtype("{{null f1} f1}","{int f2}|int"); }
	@Test public void test_291() { checkNotSubtype("{{null f1} f1}","X<{X f1}|int>"); }
	@Test public void test_292() { checkNotSubtype("{{null f1} f1}","X<{X f2}|int>"); }
	@Test public void test_293() { checkNotSubtype("{{null f1} f1}","X<(X|null)|null>"); }
	@Test public void test_294() { checkNotSubtype("{{null f1} f1}","X<(X|int)|int>"); }
	@Test public void test_295() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_296() { checkNotSubtype("{{null f2} f1}","int"); }
	@Test public void test_297() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test public void test_298() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test public void test_299() { checkNotSubtype("{{null f2} f1}","{int f1}"); }
	@Test public void test_300() { checkNotSubtype("{{null f2} f1}","{int f2}"); }
	@Test public void test_301() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test public void test_302() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test public void test_303() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test public void test_304() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test public void test_305() { checkNotSubtype("{{null f2} f1}","{{int f1} f1}"); }
	@Test public void test_306() { checkNotSubtype("{{null f2} f1}","{{int f2} f1}"); }
	@Test public void test_307() { checkNotSubtype("{{null f2} f1}","{{int f1} f2}"); }
	@Test public void test_308() { checkNotSubtype("{{null f2} f1}","{{int f2} f2}"); }
	@Test public void test_309() { checkNotSubtype("{{null f2} f1}","X<{X|null f1}>"); }
	@Test public void test_310() { checkNotSubtype("{{null f2} f1}","X<{X|null f2}>"); }
	@Test public void test_311() { checkNotSubtype("{{null f2} f1}","X<{X|int f1}>"); }
	@Test public void test_312() { checkNotSubtype("{{null f2} f1}","X<{X|int f2}>"); }
	@Test public void test_313() { checkNotSubtype("{{null f2} f1}","null|null"); }
	@Test public void test_314() { checkNotSubtype("{{null f2} f1}","null|int"); }
	@Test public void test_315() { checkNotSubtype("{{null f2} f1}","null|{null f1}"); }
	@Test public void test_316() { checkNotSubtype("{{null f2} f1}","null|{null f2}"); }
	@Test public void test_317() { checkNotSubtype("{{null f2} f1}","X<null|{X f1}>"); }
	@Test public void test_318() { checkNotSubtype("{{null f2} f1}","X<null|{X f2}>"); }
	@Test public void test_319() { checkNotSubtype("{{null f2} f1}","X<null|(X|null)>"); }
	@Test public void test_320() { checkNotSubtype("{{null f2} f1}","int|null"); }
	@Test public void test_321() { checkNotSubtype("{{null f2} f1}","int|int"); }
	@Test public void test_322() { checkNotSubtype("{{null f2} f1}","int|{int f1}"); }
	@Test public void test_323() { checkNotSubtype("{{null f2} f1}","int|{int f2}"); }
	@Test public void test_324() { checkNotSubtype("{{null f2} f1}","X<int|{X f1}>"); }
	@Test public void test_325() { checkNotSubtype("{{null f2} f1}","X<int|{X f2}>"); }
	@Test public void test_326() { checkNotSubtype("{{null f2} f1}","X<int|(X|int)>"); }
	@Test public void test_327() { checkNotSubtype("{{null f2} f1}","{null f1}|null"); }
	@Test public void test_328() { checkNotSubtype("{{null f2} f1}","{null f2}|null"); }
	@Test public void test_329() { checkNotSubtype("{{null f2} f1}","X<{X f1}|null>"); }
	@Test public void test_330() { checkNotSubtype("{{null f2} f1}","X<{X f2}|null>"); }
	@Test public void test_331() { checkNotSubtype("{{null f2} f1}","{int f1}|int"); }
	@Test public void test_332() { checkNotSubtype("{{null f2} f1}","{int f2}|int"); }
	@Test public void test_333() { checkNotSubtype("{{null f2} f1}","X<{X f1}|int>"); }
	@Test public void test_334() { checkNotSubtype("{{null f2} f1}","X<{X f2}|int>"); }
	@Test public void test_335() { checkNotSubtype("{{null f2} f1}","X<(X|null)|null>"); }
	@Test public void test_336() { checkNotSubtype("{{null f2} f1}","X<(X|int)|int>"); }
	@Test public void test_337() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_338() { checkNotSubtype("{{null f1} f2}","int"); }
	@Test public void test_339() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test public void test_340() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test public void test_341() { checkNotSubtype("{{null f1} f2}","{int f1}"); }
	@Test public void test_342() { checkNotSubtype("{{null f1} f2}","{int f2}"); }
	@Test public void test_343() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test public void test_344() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test public void test_345() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test public void test_346() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test public void test_347() { checkNotSubtype("{{null f1} f2}","{{int f1} f1}"); }
	@Test public void test_348() { checkNotSubtype("{{null f1} f2}","{{int f2} f1}"); }
	@Test public void test_349() { checkNotSubtype("{{null f1} f2}","{{int f1} f2}"); }
	@Test public void test_350() { checkNotSubtype("{{null f1} f2}","{{int f2} f2}"); }
	@Test public void test_351() { checkNotSubtype("{{null f1} f2}","X<{X|null f1}>"); }
	@Test public void test_352() { checkNotSubtype("{{null f1} f2}","X<{X|null f2}>"); }
	@Test public void test_353() { checkNotSubtype("{{null f1} f2}","X<{X|int f1}>"); }
	@Test public void test_354() { checkNotSubtype("{{null f1} f2}","X<{X|int f2}>"); }
	@Test public void test_355() { checkNotSubtype("{{null f1} f2}","null|null"); }
	@Test public void test_356() { checkNotSubtype("{{null f1} f2}","null|int"); }
	@Test public void test_357() { checkNotSubtype("{{null f1} f2}","null|{null f1}"); }
	@Test public void test_358() { checkNotSubtype("{{null f1} f2}","null|{null f2}"); }
	@Test public void test_359() { checkNotSubtype("{{null f1} f2}","X<null|{X f1}>"); }
	@Test public void test_360() { checkNotSubtype("{{null f1} f2}","X<null|{X f2}>"); }
	@Test public void test_361() { checkNotSubtype("{{null f1} f2}","X<null|(X|null)>"); }
	@Test public void test_362() { checkNotSubtype("{{null f1} f2}","int|null"); }
	@Test public void test_363() { checkNotSubtype("{{null f1} f2}","int|int"); }
	@Test public void test_364() { checkNotSubtype("{{null f1} f2}","int|{int f1}"); }
	@Test public void test_365() { checkNotSubtype("{{null f1} f2}","int|{int f2}"); }
	@Test public void test_366() { checkNotSubtype("{{null f1} f2}","X<int|{X f1}>"); }
	@Test public void test_367() { checkNotSubtype("{{null f1} f2}","X<int|{X f2}>"); }
	@Test public void test_368() { checkNotSubtype("{{null f1} f2}","X<int|(X|int)>"); }
	@Test public void test_369() { checkNotSubtype("{{null f1} f2}","{null f1}|null"); }
	@Test public void test_370() { checkNotSubtype("{{null f1} f2}","{null f2}|null"); }
	@Test public void test_371() { checkNotSubtype("{{null f1} f2}","X<{X f1}|null>"); }
	@Test public void test_372() { checkNotSubtype("{{null f1} f2}","X<{X f2}|null>"); }
	@Test public void test_373() { checkNotSubtype("{{null f1} f2}","{int f1}|int"); }
	@Test public void test_374() { checkNotSubtype("{{null f1} f2}","{int f2}|int"); }
	@Test public void test_375() { checkNotSubtype("{{null f1} f2}","X<{X f1}|int>"); }
	@Test public void test_376() { checkNotSubtype("{{null f1} f2}","X<{X f2}|int>"); }
	@Test public void test_377() { checkNotSubtype("{{null f1} f2}","X<(X|null)|null>"); }
	@Test public void test_378() { checkNotSubtype("{{null f1} f2}","X<(X|int)|int>"); }
	@Test public void test_379() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_380() { checkNotSubtype("{{null f2} f2}","int"); }
	@Test public void test_381() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test public void test_382() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test public void test_383() { checkNotSubtype("{{null f2} f2}","{int f1}"); }
	@Test public void test_384() { checkNotSubtype("{{null f2} f2}","{int f2}"); }
	@Test public void test_385() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test public void test_386() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test public void test_387() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test public void test_388() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test public void test_389() { checkNotSubtype("{{null f2} f2}","{{int f1} f1}"); }
	@Test public void test_390() { checkNotSubtype("{{null f2} f2}","{{int f2} f1}"); }
	@Test public void test_391() { checkNotSubtype("{{null f2} f2}","{{int f1} f2}"); }
	@Test public void test_392() { checkNotSubtype("{{null f2} f2}","{{int f2} f2}"); }
	@Test public void test_393() { checkNotSubtype("{{null f2} f2}","X<{X|null f1}>"); }
	@Test public void test_394() { checkNotSubtype("{{null f2} f2}","X<{X|null f2}>"); }
	@Test public void test_395() { checkNotSubtype("{{null f2} f2}","X<{X|int f1}>"); }
	@Test public void test_396() { checkNotSubtype("{{null f2} f2}","X<{X|int f2}>"); }
	@Test public void test_397() { checkNotSubtype("{{null f2} f2}","null|null"); }
	@Test public void test_398() { checkNotSubtype("{{null f2} f2}","null|int"); }
	@Test public void test_399() { checkNotSubtype("{{null f2} f2}","null|{null f1}"); }
	@Test public void test_400() { checkNotSubtype("{{null f2} f2}","null|{null f2}"); }
	@Test public void test_401() { checkNotSubtype("{{null f2} f2}","X<null|{X f1}>"); }
	@Test public void test_402() { checkNotSubtype("{{null f2} f2}","X<null|{X f2}>"); }
	@Test public void test_403() { checkNotSubtype("{{null f2} f2}","X<null|(X|null)>"); }
	@Test public void test_404() { checkNotSubtype("{{null f2} f2}","int|null"); }
	@Test public void test_405() { checkNotSubtype("{{null f2} f2}","int|int"); }
	@Test public void test_406() { checkNotSubtype("{{null f2} f2}","int|{int f1}"); }
	@Test public void test_407() { checkNotSubtype("{{null f2} f2}","int|{int f2}"); }
	@Test public void test_408() { checkNotSubtype("{{null f2} f2}","X<int|{X f1}>"); }
	@Test public void test_409() { checkNotSubtype("{{null f2} f2}","X<int|{X f2}>"); }
	@Test public void test_410() { checkNotSubtype("{{null f2} f2}","X<int|(X|int)>"); }
	@Test public void test_411() { checkNotSubtype("{{null f2} f2}","{null f1}|null"); }
	@Test public void test_412() { checkNotSubtype("{{null f2} f2}","{null f2}|null"); }
	@Test public void test_413() { checkNotSubtype("{{null f2} f2}","X<{X f1}|null>"); }
	@Test public void test_414() { checkNotSubtype("{{null f2} f2}","X<{X f2}|null>"); }
	@Test public void test_415() { checkNotSubtype("{{null f2} f2}","{int f1}|int"); }
	@Test public void test_416() { checkNotSubtype("{{null f2} f2}","{int f2}|int"); }
	@Test public void test_417() { checkNotSubtype("{{null f2} f2}","X<{X f1}|int>"); }
	@Test public void test_418() { checkNotSubtype("{{null f2} f2}","X<{X f2}|int>"); }
	@Test public void test_419() { checkNotSubtype("{{null f2} f2}","X<(X|null)|null>"); }
	@Test public void test_420() { checkNotSubtype("{{null f2} f2}","X<(X|int)|int>"); }
	@Test public void test_421() { checkNotSubtype("{{int f1} f1}","null"); }
	@Test public void test_422() { checkNotSubtype("{{int f1} f1}","int"); }
	@Test public void test_423() { checkNotSubtype("{{int f1} f1}","{null f1}"); }
	@Test public void test_424() { checkNotSubtype("{{int f1} f1}","{null f2}"); }
	@Test public void test_425() { checkNotSubtype("{{int f1} f1}","{int f1}"); }
	@Test public void test_426() { checkNotSubtype("{{int f1} f1}","{int f2}"); }
	@Test public void test_427() { checkNotSubtype("{{int f1} f1}","{{null f1} f1}"); }
	@Test public void test_428() { checkNotSubtype("{{int f1} f1}","{{null f2} f1}"); }
	@Test public void test_429() { checkNotSubtype("{{int f1} f1}","{{null f1} f2}"); }
	@Test public void test_430() { checkNotSubtype("{{int f1} f1}","{{null f2} f2}"); }
	@Test public void test_431() { checkIsSubtype("{{int f1} f1}","{{int f1} f1}"); }
	@Test public void test_432() { checkNotSubtype("{{int f1} f1}","{{int f2} f1}"); }
	@Test public void test_433() { checkNotSubtype("{{int f1} f1}","{{int f1} f2}"); }
	@Test public void test_434() { checkNotSubtype("{{int f1} f1}","{{int f2} f2}"); }
	@Test public void test_435() { checkNotSubtype("{{int f1} f1}","X<{X|null f1}>"); }
	@Test public void test_436() { checkNotSubtype("{{int f1} f1}","X<{X|null f2}>"); }
	@Test public void test_437() { checkNotSubtype("{{int f1} f1}","X<{X|int f1}>"); }
	@Test public void test_438() { checkNotSubtype("{{int f1} f1}","X<{X|int f2}>"); }
	@Test public void test_439() { checkNotSubtype("{{int f1} f1}","null|null"); }
	@Test public void test_440() { checkNotSubtype("{{int f1} f1}","null|int"); }
	@Test public void test_441() { checkNotSubtype("{{int f1} f1}","null|{null f1}"); }
	@Test public void test_442() { checkNotSubtype("{{int f1} f1}","null|{null f2}"); }
	@Test public void test_443() { checkNotSubtype("{{int f1} f1}","X<null|{X f1}>"); }
	@Test public void test_444() { checkNotSubtype("{{int f1} f1}","X<null|{X f2}>"); }
	@Test public void test_445() { checkNotSubtype("{{int f1} f1}","X<null|(X|null)>"); }
	@Test public void test_446() { checkNotSubtype("{{int f1} f1}","int|null"); }
	@Test public void test_447() { checkNotSubtype("{{int f1} f1}","int|int"); }
	@Test public void test_448() { checkNotSubtype("{{int f1} f1}","int|{int f1}"); }
	@Test public void test_449() { checkNotSubtype("{{int f1} f1}","int|{int f2}"); }
	@Test public void test_450() { checkNotSubtype("{{int f1} f1}","X<int|{X f1}>"); }
	@Test public void test_451() { checkNotSubtype("{{int f1} f1}","X<int|{X f2}>"); }
	@Test public void test_452() { checkNotSubtype("{{int f1} f1}","X<int|(X|int)>"); }
	@Test public void test_453() { checkNotSubtype("{{int f1} f1}","{null f1}|null"); }
	@Test public void test_454() { checkNotSubtype("{{int f1} f1}","{null f2}|null"); }
	@Test public void test_455() { checkNotSubtype("{{int f1} f1}","X<{X f1}|null>"); }
	@Test public void test_456() { checkNotSubtype("{{int f1} f1}","X<{X f2}|null>"); }
	@Test public void test_457() { checkNotSubtype("{{int f1} f1}","{int f1}|int"); }
	@Test public void test_458() { checkNotSubtype("{{int f1} f1}","{int f2}|int"); }
	@Test public void test_459() { checkNotSubtype("{{int f1} f1}","X<{X f1}|int>"); }
	@Test public void test_460() { checkNotSubtype("{{int f1} f1}","X<{X f2}|int>"); }
	@Test public void test_461() { checkNotSubtype("{{int f1} f1}","X<(X|null)|null>"); }
	@Test public void test_462() { checkNotSubtype("{{int f1} f1}","X<(X|int)|int>"); }
	@Test public void test_463() { checkNotSubtype("{{int f2} f1}","null"); }
	@Test public void test_464() { checkNotSubtype("{{int f2} f1}","int"); }
	@Test public void test_465() { checkNotSubtype("{{int f2} f1}","{null f1}"); }
	@Test public void test_466() { checkNotSubtype("{{int f2} f1}","{null f2}"); }
	@Test public void test_467() { checkNotSubtype("{{int f2} f1}","{int f1}"); }
	@Test public void test_468() { checkNotSubtype("{{int f2} f1}","{int f2}"); }
	@Test public void test_469() { checkNotSubtype("{{int f2} f1}","{{null f1} f1}"); }
	@Test public void test_470() { checkNotSubtype("{{int f2} f1}","{{null f2} f1}"); }
	@Test public void test_471() { checkNotSubtype("{{int f2} f1}","{{null f1} f2}"); }
	@Test public void test_472() { checkNotSubtype("{{int f2} f1}","{{null f2} f2}"); }
	@Test public void test_473() { checkNotSubtype("{{int f2} f1}","{{int f1} f1}"); }
	@Test public void test_474() { checkIsSubtype("{{int f2} f1}","{{int f2} f1}"); }
	@Test public void test_475() { checkNotSubtype("{{int f2} f1}","{{int f1} f2}"); }
	@Test public void test_476() { checkNotSubtype("{{int f2} f1}","{{int f2} f2}"); }
	@Test public void test_477() { checkNotSubtype("{{int f2} f1}","X<{X|null f1}>"); }
	@Test public void test_478() { checkNotSubtype("{{int f2} f1}","X<{X|null f2}>"); }
	@Test public void test_479() { checkNotSubtype("{{int f2} f1}","X<{X|int f1}>"); }
	@Test public void test_480() { checkNotSubtype("{{int f2} f1}","X<{X|int f2}>"); }
	@Test public void test_481() { checkNotSubtype("{{int f2} f1}","null|null"); }
	@Test public void test_482() { checkNotSubtype("{{int f2} f1}","null|int"); }
	@Test public void test_483() { checkNotSubtype("{{int f2} f1}","null|{null f1}"); }
	@Test public void test_484() { checkNotSubtype("{{int f2} f1}","null|{null f2}"); }
	@Test public void test_485() { checkNotSubtype("{{int f2} f1}","X<null|{X f1}>"); }
	@Test public void test_486() { checkNotSubtype("{{int f2} f1}","X<null|{X f2}>"); }
	@Test public void test_487() { checkNotSubtype("{{int f2} f1}","X<null|(X|null)>"); }
	@Test public void test_488() { checkNotSubtype("{{int f2} f1}","int|null"); }
	@Test public void test_489() { checkNotSubtype("{{int f2} f1}","int|int"); }
	@Test public void test_490() { checkNotSubtype("{{int f2} f1}","int|{int f1}"); }
	@Test public void test_491() { checkNotSubtype("{{int f2} f1}","int|{int f2}"); }
	@Test public void test_492() { checkNotSubtype("{{int f2} f1}","X<int|{X f1}>"); }
	@Test public void test_493() { checkNotSubtype("{{int f2} f1}","X<int|{X f2}>"); }
	@Test public void test_494() { checkNotSubtype("{{int f2} f1}","X<int|(X|int)>"); }
	@Test public void test_495() { checkNotSubtype("{{int f2} f1}","{null f1}|null"); }
	@Test public void test_496() { checkNotSubtype("{{int f2} f1}","{null f2}|null"); }
	@Test public void test_497() { checkNotSubtype("{{int f2} f1}","X<{X f1}|null>"); }
	@Test public void test_498() { checkNotSubtype("{{int f2} f1}","X<{X f2}|null>"); }
	@Test public void test_499() { checkNotSubtype("{{int f2} f1}","{int f1}|int"); }
	@Test public void test_500() { checkNotSubtype("{{int f2} f1}","{int f2}|int"); }
	@Test public void test_501() { checkNotSubtype("{{int f2} f1}","X<{X f1}|int>"); }
	@Test public void test_502() { checkNotSubtype("{{int f2} f1}","X<{X f2}|int>"); }
	@Test public void test_503() { checkNotSubtype("{{int f2} f1}","X<(X|null)|null>"); }
	@Test public void test_504() { checkNotSubtype("{{int f2} f1}","X<(X|int)|int>"); }
	@Test public void test_505() { checkNotSubtype("{{int f1} f2}","null"); }
	@Test public void test_506() { checkNotSubtype("{{int f1} f2}","int"); }
	@Test public void test_507() { checkNotSubtype("{{int f1} f2}","{null f1}"); }
	@Test public void test_508() { checkNotSubtype("{{int f1} f2}","{null f2}"); }
	@Test public void test_509() { checkNotSubtype("{{int f1} f2}","{int f1}"); }
	@Test public void test_510() { checkNotSubtype("{{int f1} f2}","{int f2}"); }
	@Test public void test_511() { checkNotSubtype("{{int f1} f2}","{{null f1} f1}"); }
	@Test public void test_512() { checkNotSubtype("{{int f1} f2}","{{null f2} f1}"); }
	@Test public void test_513() { checkNotSubtype("{{int f1} f2}","{{null f1} f2}"); }
	@Test public void test_514() { checkNotSubtype("{{int f1} f2}","{{null f2} f2}"); }
	@Test public void test_515() { checkNotSubtype("{{int f1} f2}","{{int f1} f1}"); }
	@Test public void test_516() { checkNotSubtype("{{int f1} f2}","{{int f2} f1}"); }
	@Test public void test_517() { checkIsSubtype("{{int f1} f2}","{{int f1} f2}"); }
	@Test public void test_518() { checkNotSubtype("{{int f1} f2}","{{int f2} f2}"); }
	@Test public void test_519() { checkNotSubtype("{{int f1} f2}","X<{X|null f1}>"); }
	@Test public void test_520() { checkNotSubtype("{{int f1} f2}","X<{X|null f2}>"); }
	@Test public void test_521() { checkNotSubtype("{{int f1} f2}","X<{X|int f1}>"); }
	@Test public void test_522() { checkNotSubtype("{{int f1} f2}","X<{X|int f2}>"); }
	@Test public void test_523() { checkNotSubtype("{{int f1} f2}","null|null"); }
	@Test public void test_524() { checkNotSubtype("{{int f1} f2}","null|int"); }
	@Test public void test_525() { checkNotSubtype("{{int f1} f2}","null|{null f1}"); }
	@Test public void test_526() { checkNotSubtype("{{int f1} f2}","null|{null f2}"); }
	@Test public void test_527() { checkNotSubtype("{{int f1} f2}","X<null|{X f1}>"); }
	@Test public void test_528() { checkNotSubtype("{{int f1} f2}","X<null|{X f2}>"); }
	@Test public void test_529() { checkNotSubtype("{{int f1} f2}","X<null|(X|null)>"); }
	@Test public void test_530() { checkNotSubtype("{{int f1} f2}","int|null"); }
	@Test public void test_531() { checkNotSubtype("{{int f1} f2}","int|int"); }
	@Test public void test_532() { checkNotSubtype("{{int f1} f2}","int|{int f1}"); }
	@Test public void test_533() { checkNotSubtype("{{int f1} f2}","int|{int f2}"); }
	@Test public void test_534() { checkNotSubtype("{{int f1} f2}","X<int|{X f1}>"); }
	@Test public void test_535() { checkNotSubtype("{{int f1} f2}","X<int|{X f2}>"); }
	@Test public void test_536() { checkNotSubtype("{{int f1} f2}","X<int|(X|int)>"); }
	@Test public void test_537() { checkNotSubtype("{{int f1} f2}","{null f1}|null"); }
	@Test public void test_538() { checkNotSubtype("{{int f1} f2}","{null f2}|null"); }
	@Test public void test_539() { checkNotSubtype("{{int f1} f2}","X<{X f1}|null>"); }
	@Test public void test_540() { checkNotSubtype("{{int f1} f2}","X<{X f2}|null>"); }
	@Test public void test_541() { checkNotSubtype("{{int f1} f2}","{int f1}|int"); }
	@Test public void test_542() { checkNotSubtype("{{int f1} f2}","{int f2}|int"); }
	@Test public void test_543() { checkNotSubtype("{{int f1} f2}","X<{X f1}|int>"); }
	@Test public void test_544() { checkNotSubtype("{{int f1} f2}","X<{X f2}|int>"); }
	@Test public void test_545() { checkNotSubtype("{{int f1} f2}","X<(X|null)|null>"); }
	@Test public void test_546() { checkNotSubtype("{{int f1} f2}","X<(X|int)|int>"); }
	@Test public void test_547() { checkNotSubtype("{{int f2} f2}","null"); }
	@Test public void test_548() { checkNotSubtype("{{int f2} f2}","int"); }
	@Test public void test_549() { checkNotSubtype("{{int f2} f2}","{null f1}"); }
	@Test public void test_550() { checkNotSubtype("{{int f2} f2}","{null f2}"); }
	@Test public void test_551() { checkNotSubtype("{{int f2} f2}","{int f1}"); }
	@Test public void test_552() { checkNotSubtype("{{int f2} f2}","{int f2}"); }
	@Test public void test_553() { checkNotSubtype("{{int f2} f2}","{{null f1} f1}"); }
	@Test public void test_554() { checkNotSubtype("{{int f2} f2}","{{null f2} f1}"); }
	@Test public void test_555() { checkNotSubtype("{{int f2} f2}","{{null f1} f2}"); }
	@Test public void test_556() { checkNotSubtype("{{int f2} f2}","{{null f2} f2}"); }
	@Test public void test_557() { checkNotSubtype("{{int f2} f2}","{{int f1} f1}"); }
	@Test public void test_558() { checkNotSubtype("{{int f2} f2}","{{int f2} f1}"); }
	@Test public void test_559() { checkNotSubtype("{{int f2} f2}","{{int f1} f2}"); }
	@Test public void test_560() { checkIsSubtype("{{int f2} f2}","{{int f2} f2}"); }
	@Test public void test_561() { checkNotSubtype("{{int f2} f2}","X<{X|null f1}>"); }
	@Test public void test_562() { checkNotSubtype("{{int f2} f2}","X<{X|null f2}>"); }
	@Test public void test_563() { checkNotSubtype("{{int f2} f2}","X<{X|int f1}>"); }
	@Test public void test_564() { checkNotSubtype("{{int f2} f2}","X<{X|int f2}>"); }
	@Test public void test_565() { checkNotSubtype("{{int f2} f2}","null|null"); }
	@Test public void test_566() { checkNotSubtype("{{int f2} f2}","null|int"); }
	@Test public void test_567() { checkNotSubtype("{{int f2} f2}","null|{null f1}"); }
	@Test public void test_568() { checkNotSubtype("{{int f2} f2}","null|{null f2}"); }
	@Test public void test_569() { checkNotSubtype("{{int f2} f2}","X<null|{X f1}>"); }
	@Test public void test_570() { checkNotSubtype("{{int f2} f2}","X<null|{X f2}>"); }
	@Test public void test_571() { checkNotSubtype("{{int f2} f2}","X<null|(X|null)>"); }
	@Test public void test_572() { checkNotSubtype("{{int f2} f2}","int|null"); }
	@Test public void test_573() { checkNotSubtype("{{int f2} f2}","int|int"); }
	@Test public void test_574() { checkNotSubtype("{{int f2} f2}","int|{int f1}"); }
	@Test public void test_575() { checkNotSubtype("{{int f2} f2}","int|{int f2}"); }
	@Test public void test_576() { checkNotSubtype("{{int f2} f2}","X<int|{X f1}>"); }
	@Test public void test_577() { checkNotSubtype("{{int f2} f2}","X<int|{X f2}>"); }
	@Test public void test_578() { checkNotSubtype("{{int f2} f2}","X<int|(X|int)>"); }
	@Test public void test_579() { checkNotSubtype("{{int f2} f2}","{null f1}|null"); }
	@Test public void test_580() { checkNotSubtype("{{int f2} f2}","{null f2}|null"); }
	@Test public void test_581() { checkNotSubtype("{{int f2} f2}","X<{X f1}|null>"); }
	@Test public void test_582() { checkNotSubtype("{{int f2} f2}","X<{X f2}|null>"); }
	@Test public void test_583() { checkNotSubtype("{{int f2} f2}","{int f1}|int"); }
	@Test public void test_584() { checkNotSubtype("{{int f2} f2}","{int f2}|int"); }
	@Test public void test_585() { checkNotSubtype("{{int f2} f2}","X<{X f1}|int>"); }
	@Test public void test_586() { checkNotSubtype("{{int f2} f2}","X<{X f2}|int>"); }
	@Test public void test_587() { checkNotSubtype("{{int f2} f2}","X<(X|null)|null>"); }
	@Test public void test_588() { checkNotSubtype("{{int f2} f2}","X<(X|int)|int>"); }
	@Test public void test_589() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test public void test_590() { checkNotSubtype("X<{X|null f1}>","int"); }
	@Test public void test_591() { checkIsSubtype("X<{X|null f1}>","{null f1}"); }
	@Test public void test_592() { checkNotSubtype("X<{X|null f1}>","{null f2}"); }
	@Test public void test_593() { checkNotSubtype("X<{X|null f1}>","{int f1}"); }
	@Test public void test_594() { checkNotSubtype("X<{X|null f1}>","{int f2}"); }
	@Test public void test_595() { checkIsSubtype("X<{X|null f1}>","{{null f1} f1}"); }
	@Test public void test_596() { checkNotSubtype("X<{X|null f1}>","{{null f2} f1}"); }
	@Test public void test_597() { checkNotSubtype("X<{X|null f1}>","{{null f1} f2}"); }
	@Test public void test_598() { checkNotSubtype("X<{X|null f1}>","{{null f2} f2}"); }
	@Test public void test_599() { checkNotSubtype("X<{X|null f1}>","{{int f1} f1}"); }
	@Test public void test_600() { checkNotSubtype("X<{X|null f1}>","{{int f2} f1}"); }
	@Test public void test_601() { checkNotSubtype("X<{X|null f1}>","{{int f1} f2}"); }
	@Test public void test_602() { checkNotSubtype("X<{X|null f1}>","{{int f2} f2}"); }
	@Test public void test_603() { checkIsSubtype("X<{X|null f1}>","X<{X|null f1}>"); }
	@Test public void test_604() { checkNotSubtype("X<{X|null f1}>","X<{X|null f2}>"); }
	@Test public void test_605() { checkNotSubtype("X<{X|null f1}>","X<{X|int f1}>"); }
	@Test public void test_606() { checkNotSubtype("X<{X|null f1}>","X<{X|int f2}>"); }
	@Test public void test_607() { checkNotSubtype("X<{X|null f1}>","null|null"); }
	@Test public void test_608() { checkNotSubtype("X<{X|null f1}>","null|int"); }
	@Test public void test_609() { checkNotSubtype("X<{X|null f1}>","null|{null f1}"); }
	@Test public void test_610() { checkNotSubtype("X<{X|null f1}>","null|{null f2}"); }
	@Test public void test_611() { checkNotSubtype("X<{X|null f1}>","X<null|{X f1}>"); }
	@Test public void test_612() { checkNotSubtype("X<{X|null f1}>","X<null|{X f2}>"); }
	@Test public void test_613() { checkNotSubtype("X<{X|null f1}>","X<null|(X|null)>"); }
	@Test public void test_614() { checkNotSubtype("X<{X|null f1}>","int|null"); }
	@Test public void test_615() { checkNotSubtype("X<{X|null f1}>","int|int"); }
	@Test public void test_616() { checkNotSubtype("X<{X|null f1}>","int|{int f1}"); }
	@Test public void test_617() { checkNotSubtype("X<{X|null f1}>","int|{int f2}"); }
	@Test public void test_618() { checkNotSubtype("X<{X|null f1}>","X<int|{X f1}>"); }
	@Test public void test_619() { checkNotSubtype("X<{X|null f1}>","X<int|{X f2}>"); }
	@Test public void test_620() { checkNotSubtype("X<{X|null f1}>","X<int|(X|int)>"); }
	@Test public void test_621() { checkNotSubtype("X<{X|null f1}>","{null f1}|null"); }
	@Test public void test_622() { checkNotSubtype("X<{X|null f1}>","{null f2}|null"); }
	@Test public void test_623() { checkNotSubtype("X<{X|null f1}>","X<{X f1}|null>"); }
	@Test public void test_624() { checkNotSubtype("X<{X|null f1}>","X<{X f2}|null>"); }
	@Test public void test_625() { checkNotSubtype("X<{X|null f1}>","{int f1}|int"); }
	@Test public void test_626() { checkNotSubtype("X<{X|null f1}>","{int f2}|int"); }
	@Test public void test_627() { checkNotSubtype("X<{X|null f1}>","X<{X f1}|int>"); }
	@Test public void test_628() { checkNotSubtype("X<{X|null f1}>","X<{X f2}|int>"); }
	@Test public void test_629() { checkNotSubtype("X<{X|null f1}>","X<(X|null)|null>"); }
	@Test public void test_630() { checkNotSubtype("X<{X|null f1}>","X<(X|int)|int>"); }
	@Test public void test_631() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test public void test_632() { checkNotSubtype("X<{X|null f2}>","int"); }
	@Test public void test_633() { checkNotSubtype("X<{X|null f2}>","{null f1}"); }
	@Test public void test_634() { checkIsSubtype("X<{X|null f2}>","{null f2}"); }
	@Test public void test_635() { checkNotSubtype("X<{X|null f2}>","{int f1}"); }
	@Test public void test_636() { checkNotSubtype("X<{X|null f2}>","{int f2}"); }
	@Test public void test_637() { checkNotSubtype("X<{X|null f2}>","{{null f1} f1}"); }
	@Test public void test_638() { checkNotSubtype("X<{X|null f2}>","{{null f2} f1}"); }
	@Test public void test_639() { checkNotSubtype("X<{X|null f2}>","{{null f1} f2}"); }
	@Test public void test_640() { checkIsSubtype("X<{X|null f2}>","{{null f2} f2}"); }
	@Test public void test_641() { checkNotSubtype("X<{X|null f2}>","{{int f1} f1}"); }
	@Test public void test_642() { checkNotSubtype("X<{X|null f2}>","{{int f2} f1}"); }
	@Test public void test_643() { checkNotSubtype("X<{X|null f2}>","{{int f1} f2}"); }
	@Test public void test_644() { checkNotSubtype("X<{X|null f2}>","{{int f2} f2}"); }
	@Test public void test_645() { checkNotSubtype("X<{X|null f2}>","X<{X|null f1}>"); }
	@Test public void test_646() { checkIsSubtype("X<{X|null f2}>","X<{X|null f2}>"); }
	@Test public void test_647() { checkNotSubtype("X<{X|null f2}>","X<{X|int f1}>"); }
	@Test public void test_648() { checkNotSubtype("X<{X|null f2}>","X<{X|int f2}>"); }
	@Test public void test_649() { checkNotSubtype("X<{X|null f2}>","null|null"); }
	@Test public void test_650() { checkNotSubtype("X<{X|null f2}>","null|int"); }
	@Test public void test_651() { checkNotSubtype("X<{X|null f2}>","null|{null f1}"); }
	@Test public void test_652() { checkNotSubtype("X<{X|null f2}>","null|{null f2}"); }
	@Test public void test_653() { checkNotSubtype("X<{X|null f2}>","X<null|{X f1}>"); }
	@Test public void test_654() { checkNotSubtype("X<{X|null f2}>","X<null|{X f2}>"); }
	@Test public void test_655() { checkNotSubtype("X<{X|null f2}>","X<null|(X|null)>"); }
	@Test public void test_656() { checkNotSubtype("X<{X|null f2}>","int|null"); }
	@Test public void test_657() { checkNotSubtype("X<{X|null f2}>","int|int"); }
	@Test public void test_658() { checkNotSubtype("X<{X|null f2}>","int|{int f1}"); }
	@Test public void test_659() { checkNotSubtype("X<{X|null f2}>","int|{int f2}"); }
	@Test public void test_660() { checkNotSubtype("X<{X|null f2}>","X<int|{X f1}>"); }
	@Test public void test_661() { checkNotSubtype("X<{X|null f2}>","X<int|{X f2}>"); }
	@Test public void test_662() { checkNotSubtype("X<{X|null f2}>","X<int|(X|int)>"); }
	@Test public void test_663() { checkNotSubtype("X<{X|null f2}>","{null f1}|null"); }
	@Test public void test_664() { checkNotSubtype("X<{X|null f2}>","{null f2}|null"); }
	@Test public void test_665() { checkNotSubtype("X<{X|null f2}>","X<{X f1}|null>"); }
	@Test public void test_666() { checkNotSubtype("X<{X|null f2}>","X<{X f2}|null>"); }
	@Test public void test_667() { checkNotSubtype("X<{X|null f2}>","{int f1}|int"); }
	@Test public void test_668() { checkNotSubtype("X<{X|null f2}>","{int f2}|int"); }
	@Test public void test_669() { checkNotSubtype("X<{X|null f2}>","X<{X f1}|int>"); }
	@Test public void test_670() { checkNotSubtype("X<{X|null f2}>","X<{X f2}|int>"); }
	@Test public void test_671() { checkNotSubtype("X<{X|null f2}>","X<(X|null)|null>"); }
	@Test public void test_672() { checkNotSubtype("X<{X|null f2}>","X<(X|int)|int>"); }
	@Test public void test_673() { checkNotSubtype("X<{X|int f1}>","null"); }
	@Test public void test_674() { checkNotSubtype("X<{X|int f1}>","int"); }
	@Test public void test_675() { checkNotSubtype("X<{X|int f1}>","{null f1}"); }
	@Test public void test_676() { checkNotSubtype("X<{X|int f1}>","{null f2}"); }
	@Test public void test_677() { checkIsSubtype("X<{X|int f1}>","{int f1}"); }
	@Test public void test_678() { checkNotSubtype("X<{X|int f1}>","{int f2}"); }
	@Test public void test_679() { checkNotSubtype("X<{X|int f1}>","{{null f1} f1}"); }
	@Test public void test_680() { checkNotSubtype("X<{X|int f1}>","{{null f2} f1}"); }
	@Test public void test_681() { checkNotSubtype("X<{X|int f1}>","{{null f1} f2}"); }
	@Test public void test_682() { checkNotSubtype("X<{X|int f1}>","{{null f2} f2}"); }
	@Test public void test_683() { checkIsSubtype("X<{X|int f1}>","{{int f1} f1}"); }
	@Test public void test_684() { checkNotSubtype("X<{X|int f1}>","{{int f2} f1}"); }
	@Test public void test_685() { checkNotSubtype("X<{X|int f1}>","{{int f1} f2}"); }
	@Test public void test_686() { checkNotSubtype("X<{X|int f1}>","{{int f2} f2}"); }
	@Test public void test_687() { checkNotSubtype("X<{X|int f1}>","X<{X|null f1}>"); }
	@Test public void test_688() { checkNotSubtype("X<{X|int f1}>","X<{X|null f2}>"); }
	@Test public void test_689() { checkIsSubtype("X<{X|int f1}>","X<{X|int f1}>"); }
	@Test public void test_690() { checkNotSubtype("X<{X|int f1}>","X<{X|int f2}>"); }
	@Test public void test_691() { checkNotSubtype("X<{X|int f1}>","null|null"); }
	@Test public void test_692() { checkNotSubtype("X<{X|int f1}>","null|int"); }
	@Test public void test_693() { checkNotSubtype("X<{X|int f1}>","null|{null f1}"); }
	@Test public void test_694() { checkNotSubtype("X<{X|int f1}>","null|{null f2}"); }
	@Test public void test_695() { checkNotSubtype("X<{X|int f1}>","X<null|{X f1}>"); }
	@Test public void test_696() { checkNotSubtype("X<{X|int f1}>","X<null|{X f2}>"); }
	@Test public void test_697() { checkNotSubtype("X<{X|int f1}>","X<null|(X|null)>"); }
	@Test public void test_698() { checkNotSubtype("X<{X|int f1}>","int|null"); }
	@Test public void test_699() { checkNotSubtype("X<{X|int f1}>","int|int"); }
	@Test public void test_700() { checkNotSubtype("X<{X|int f1}>","int|{int f1}"); }
	@Test public void test_701() { checkNotSubtype("X<{X|int f1}>","int|{int f2}"); }
	@Test public void test_702() { checkNotSubtype("X<{X|int f1}>","X<int|{X f1}>"); }
	@Test public void test_703() { checkNotSubtype("X<{X|int f1}>","X<int|{X f2}>"); }
	@Test public void test_704() { checkNotSubtype("X<{X|int f1}>","X<int|(X|int)>"); }
	@Test public void test_705() { checkNotSubtype("X<{X|int f1}>","{null f1}|null"); }
	@Test public void test_706() { checkNotSubtype("X<{X|int f1}>","{null f2}|null"); }
	@Test public void test_707() { checkNotSubtype("X<{X|int f1}>","X<{X f1}|null>"); }
	@Test public void test_708() { checkNotSubtype("X<{X|int f1}>","X<{X f2}|null>"); }
	@Test public void test_709() { checkNotSubtype("X<{X|int f1}>","{int f1}|int"); }
	@Test public void test_710() { checkNotSubtype("X<{X|int f1}>","{int f2}|int"); }
	@Test public void test_711() { checkNotSubtype("X<{X|int f1}>","X<{X f1}|int>"); }
	@Test public void test_712() { checkNotSubtype("X<{X|int f1}>","X<{X f2}|int>"); }
	@Test public void test_713() { checkNotSubtype("X<{X|int f1}>","X<(X|null)|null>"); }
	@Test public void test_714() { checkNotSubtype("X<{X|int f1}>","X<(X|int)|int>"); }
	@Test public void test_715() { checkNotSubtype("X<{X|int f2}>","null"); }
	@Test public void test_716() { checkNotSubtype("X<{X|int f2}>","int"); }
	@Test public void test_717() { checkNotSubtype("X<{X|int f2}>","{null f1}"); }
	@Test public void test_718() { checkNotSubtype("X<{X|int f2}>","{null f2}"); }
	@Test public void test_719() { checkNotSubtype("X<{X|int f2}>","{int f1}"); }
	@Test public void test_720() { checkIsSubtype("X<{X|int f2}>","{int f2}"); }
	@Test public void test_721() { checkNotSubtype("X<{X|int f2}>","{{null f1} f1}"); }
	@Test public void test_722() { checkNotSubtype("X<{X|int f2}>","{{null f2} f1}"); }
	@Test public void test_723() { checkNotSubtype("X<{X|int f2}>","{{null f1} f2}"); }
	@Test public void test_724() { checkNotSubtype("X<{X|int f2}>","{{null f2} f2}"); }
	@Test public void test_725() { checkNotSubtype("X<{X|int f2}>","{{int f1} f1}"); }
	@Test public void test_726() { checkNotSubtype("X<{X|int f2}>","{{int f2} f1}"); }
	@Test public void test_727() { checkNotSubtype("X<{X|int f2}>","{{int f1} f2}"); }
	@Test public void test_728() { checkIsSubtype("X<{X|int f2}>","{{int f2} f2}"); }
	@Test public void test_729() { checkNotSubtype("X<{X|int f2}>","X<{X|null f1}>"); }
	@Test public void test_730() { checkNotSubtype("X<{X|int f2}>","X<{X|null f2}>"); }
	@Test public void test_731() { checkNotSubtype("X<{X|int f2}>","X<{X|int f1}>"); }
	@Test public void test_732() { checkIsSubtype("X<{X|int f2}>","X<{X|int f2}>"); }
	@Test public void test_733() { checkNotSubtype("X<{X|int f2}>","null|null"); }
	@Test public void test_734() { checkNotSubtype("X<{X|int f2}>","null|int"); }
	@Test public void test_735() { checkNotSubtype("X<{X|int f2}>","null|{null f1}"); }
	@Test public void test_736() { checkNotSubtype("X<{X|int f2}>","null|{null f2}"); }
	@Test public void test_737() { checkNotSubtype("X<{X|int f2}>","X<null|{X f1}>"); }
	@Test public void test_738() { checkNotSubtype("X<{X|int f2}>","X<null|{X f2}>"); }
	@Test public void test_739() { checkNotSubtype("X<{X|int f2}>","X<null|(X|null)>"); }
	@Test public void test_740() { checkNotSubtype("X<{X|int f2}>","int|null"); }
	@Test public void test_741() { checkNotSubtype("X<{X|int f2}>","int|int"); }
	@Test public void test_742() { checkNotSubtype("X<{X|int f2}>","int|{int f1}"); }
	@Test public void test_743() { checkNotSubtype("X<{X|int f2}>","int|{int f2}"); }
	@Test public void test_744() { checkNotSubtype("X<{X|int f2}>","X<int|{X f1}>"); }
	@Test public void test_745() { checkNotSubtype("X<{X|int f2}>","X<int|{X f2}>"); }
	@Test public void test_746() { checkNotSubtype("X<{X|int f2}>","X<int|(X|int)>"); }
	@Test public void test_747() { checkNotSubtype("X<{X|int f2}>","{null f1}|null"); }
	@Test public void test_748() { checkNotSubtype("X<{X|int f2}>","{null f2}|null"); }
	@Test public void test_749() { checkNotSubtype("X<{X|int f2}>","X<{X f1}|null>"); }
	@Test public void test_750() { checkNotSubtype("X<{X|int f2}>","X<{X f2}|null>"); }
	@Test public void test_751() { checkNotSubtype("X<{X|int f2}>","{int f1}|int"); }
	@Test public void test_752() { checkNotSubtype("X<{X|int f2}>","{int f2}|int"); }
	@Test public void test_753() { checkNotSubtype("X<{X|int f2}>","X<{X f1}|int>"); }
	@Test public void test_754() { checkNotSubtype("X<{X|int f2}>","X<{X f2}|int>"); }
	@Test public void test_755() { checkNotSubtype("X<{X|int f2}>","X<(X|null)|null>"); }
	@Test public void test_756() { checkNotSubtype("X<{X|int f2}>","X<(X|int)|int>"); }
	@Test public void test_757() { checkIsSubtype("null|null","null"); }
	@Test public void test_758() { checkNotSubtype("null|null","int"); }
	@Test public void test_759() { checkNotSubtype("null|null","{null f1}"); }
	@Test public void test_760() { checkNotSubtype("null|null","{null f2}"); }
	@Test public void test_761() { checkNotSubtype("null|null","{int f1}"); }
	@Test public void test_762() { checkNotSubtype("null|null","{int f2}"); }
	@Test public void test_763() { checkNotSubtype("null|null","{{null f1} f1}"); }
	@Test public void test_764() { checkNotSubtype("null|null","{{null f2} f1}"); }
	@Test public void test_765() { checkNotSubtype("null|null","{{null f1} f2}"); }
	@Test public void test_766() { checkNotSubtype("null|null","{{null f2} f2}"); }
	@Test public void test_767() { checkNotSubtype("null|null","{{int f1} f1}"); }
	@Test public void test_768() { checkNotSubtype("null|null","{{int f2} f1}"); }
	@Test public void test_769() { checkNotSubtype("null|null","{{int f1} f2}"); }
	@Test public void test_770() { checkNotSubtype("null|null","{{int f2} f2}"); }
	@Test public void test_771() { checkNotSubtype("null|null","X<{X|null f1}>"); }
	@Test public void test_772() { checkNotSubtype("null|null","X<{X|null f2}>"); }
	@Test public void test_773() { checkNotSubtype("null|null","X<{X|int f1}>"); }
	@Test public void test_774() { checkNotSubtype("null|null","X<{X|int f2}>"); }
	@Test public void test_775() { checkIsSubtype("null|null","null|null"); }
	@Test public void test_776() { checkNotSubtype("null|null","null|int"); }
	@Test public void test_777() { checkNotSubtype("null|null","null|{null f1}"); }
	@Test public void test_778() { checkNotSubtype("null|null","null|{null f2}"); }
	@Test public void test_779() { checkNotSubtype("null|null","X<null|{X f1}>"); }
	@Test public void test_780() { checkNotSubtype("null|null","X<null|{X f2}>"); }
	@Test public void test_781() { checkIsSubtype("null|null","X<null|(X|null)>"); }
	@Test public void test_782() { checkNotSubtype("null|null","int|null"); }
	@Test public void test_783() { checkNotSubtype("null|null","int|int"); }
	@Test public void test_784() { checkNotSubtype("null|null","int|{int f1}"); }
	@Test public void test_785() { checkNotSubtype("null|null","int|{int f2}"); }
	@Test public void test_786() { checkNotSubtype("null|null","X<int|{X f1}>"); }
	@Test public void test_787() { checkNotSubtype("null|null","X<int|{X f2}>"); }
	@Test public void test_788() { checkNotSubtype("null|null","X<int|(X|int)>"); }
	@Test public void test_789() { checkNotSubtype("null|null","{null f1}|null"); }
	@Test public void test_790() { checkNotSubtype("null|null","{null f2}|null"); }
	@Test public void test_791() { checkNotSubtype("null|null","X<{X f1}|null>"); }
	@Test public void test_792() { checkNotSubtype("null|null","X<{X f2}|null>"); }
	@Test public void test_793() { checkNotSubtype("null|null","{int f1}|int"); }
	@Test public void test_794() { checkNotSubtype("null|null","{int f2}|int"); }
	@Test public void test_795() { checkNotSubtype("null|null","X<{X f1}|int>"); }
	@Test public void test_796() { checkNotSubtype("null|null","X<{X f2}|int>"); }
	@Test public void test_797() { checkIsSubtype("null|null","X<(X|null)|null>"); }
	@Test public void test_798() { checkNotSubtype("null|null","X<(X|int)|int>"); }
	@Test public void test_799() { checkIsSubtype("null|int","null"); }
	@Test public void test_800() { checkIsSubtype("null|int","int"); }
	@Test public void test_801() { checkNotSubtype("null|int","{null f1}"); }
	@Test public void test_802() { checkNotSubtype("null|int","{null f2}"); }
	@Test public void test_803() { checkNotSubtype("null|int","{int f1}"); }
	@Test public void test_804() { checkNotSubtype("null|int","{int f2}"); }
	@Test public void test_805() { checkNotSubtype("null|int","{{null f1} f1}"); }
	@Test public void test_806() { checkNotSubtype("null|int","{{null f2} f1}"); }
	@Test public void test_807() { checkNotSubtype("null|int","{{null f1} f2}"); }
	@Test public void test_808() { checkNotSubtype("null|int","{{null f2} f2}"); }
	@Test public void test_809() { checkNotSubtype("null|int","{{int f1} f1}"); }
	@Test public void test_810() { checkNotSubtype("null|int","{{int f2} f1}"); }
	@Test public void test_811() { checkNotSubtype("null|int","{{int f1} f2}"); }
	@Test public void test_812() { checkNotSubtype("null|int","{{int f2} f2}"); }
	@Test public void test_813() { checkNotSubtype("null|int","X<{X|null f1}>"); }
	@Test public void test_814() { checkNotSubtype("null|int","X<{X|null f2}>"); }
	@Test public void test_815() { checkNotSubtype("null|int","X<{X|int f1}>"); }
	@Test public void test_816() { checkNotSubtype("null|int","X<{X|int f2}>"); }
	@Test public void test_817() { checkIsSubtype("null|int","null|null"); }
	@Test public void test_818() { checkIsSubtype("null|int","null|int"); }
	@Test public void test_819() { checkNotSubtype("null|int","null|{null f1}"); }
	@Test public void test_820() { checkNotSubtype("null|int","null|{null f2}"); }
	@Test public void test_821() { checkNotSubtype("null|int","X<null|{X f1}>"); }
	@Test public void test_822() { checkNotSubtype("null|int","X<null|{X f2}>"); }
	@Test public void test_823() { checkIsSubtype("null|int","X<null|(X|null)>"); }
	@Test public void test_824() { checkIsSubtype("null|int","int|null"); }
	@Test public void test_825() { checkIsSubtype("null|int","int|int"); }
	@Test public void test_826() { checkNotSubtype("null|int","int|{int f1}"); }
	@Test public void test_827() { checkNotSubtype("null|int","int|{int f2}"); }
	@Test public void test_828() { checkNotSubtype("null|int","X<int|{X f1}>"); }
	@Test public void test_829() { checkNotSubtype("null|int","X<int|{X f2}>"); }
	@Test public void test_830() { checkIsSubtype("null|int","X<int|(X|int)>"); }
	@Test public void test_831() { checkNotSubtype("null|int","{null f1}|null"); }
	@Test public void test_832() { checkNotSubtype("null|int","{null f2}|null"); }
	@Test public void test_833() { checkNotSubtype("null|int","X<{X f1}|null>"); }
	@Test public void test_834() { checkNotSubtype("null|int","X<{X f2}|null>"); }
	@Test public void test_835() { checkNotSubtype("null|int","{int f1}|int"); }
	@Test public void test_836() { checkNotSubtype("null|int","{int f2}|int"); }
	@Test public void test_837() { checkNotSubtype("null|int","X<{X f1}|int>"); }
	@Test public void test_838() { checkNotSubtype("null|int","X<{X f2}|int>"); }
	@Test public void test_839() { checkIsSubtype("null|int","X<(X|null)|null>"); }
	@Test public void test_840() { checkIsSubtype("null|int","X<(X|int)|int>"); }
	@Test public void test_841() { checkIsSubtype("null|{null f1}","null"); }
	@Test public void test_842() { checkNotSubtype("null|{null f1}","int"); }
	@Test public void test_843() { checkIsSubtype("null|{null f1}","{null f1}"); }
	@Test public void test_844() { checkNotSubtype("null|{null f1}","{null f2}"); }
	@Test public void test_845() { checkNotSubtype("null|{null f1}","{int f1}"); }
	@Test public void test_846() { checkNotSubtype("null|{null f1}","{int f2}"); }
	@Test public void test_847() { checkNotSubtype("null|{null f1}","{{null f1} f1}"); }
	@Test public void test_848() { checkNotSubtype("null|{null f1}","{{null f2} f1}"); }
	@Test public void test_849() { checkNotSubtype("null|{null f1}","{{null f1} f2}"); }
	@Test public void test_850() { checkNotSubtype("null|{null f1}","{{null f2} f2}"); }
	@Test public void test_851() { checkNotSubtype("null|{null f1}","{{int f1} f1}"); }
	@Test public void test_852() { checkNotSubtype("null|{null f1}","{{int f2} f1}"); }
	@Test public void test_853() { checkNotSubtype("null|{null f1}","{{int f1} f2}"); }
	@Test public void test_854() { checkNotSubtype("null|{null f1}","{{int f2} f2}"); }
	@Test public void test_855() { checkNotSubtype("null|{null f1}","X<{X|null f1}>"); }
	@Test public void test_856() { checkNotSubtype("null|{null f1}","X<{X|null f2}>"); }
	@Test public void test_857() { checkNotSubtype("null|{null f1}","X<{X|int f1}>"); }
	@Test public void test_858() { checkNotSubtype("null|{null f1}","X<{X|int f2}>"); }
	@Test public void test_859() { checkIsSubtype("null|{null f1}","null|null"); }
	@Test public void test_860() { checkNotSubtype("null|{null f1}","null|int"); }
	@Test public void test_861() { checkIsSubtype("null|{null f1}","null|{null f1}"); }
	@Test public void test_862() { checkNotSubtype("null|{null f1}","null|{null f2}"); }
	@Test public void test_863() { checkNotSubtype("null|{null f1}","X<null|{X f1}>"); }
	@Test public void test_864() { checkNotSubtype("null|{null f1}","X<null|{X f2}>"); }
	@Test public void test_865() { checkIsSubtype("null|{null f1}","X<null|(X|null)>"); }
	@Test public void test_866() { checkNotSubtype("null|{null f1}","int|null"); }
	@Test public void test_867() { checkNotSubtype("null|{null f1}","int|int"); }
	@Test public void test_868() { checkNotSubtype("null|{null f1}","int|{int f1}"); }
	@Test public void test_869() { checkNotSubtype("null|{null f1}","int|{int f2}"); }
	@Test public void test_870() { checkNotSubtype("null|{null f1}","X<int|{X f1}>"); }
	@Test public void test_871() { checkNotSubtype("null|{null f1}","X<int|{X f2}>"); }
	@Test public void test_872() { checkNotSubtype("null|{null f1}","X<int|(X|int)>"); }
	@Test public void test_873() { checkIsSubtype("null|{null f1}","{null f1}|null"); }
	@Test public void test_874() { checkNotSubtype("null|{null f1}","{null f2}|null"); }
	@Test public void test_875() { checkNotSubtype("null|{null f1}","X<{X f1}|null>"); }
	@Test public void test_876() { checkNotSubtype("null|{null f1}","X<{X f2}|null>"); }
	@Test public void test_877() { checkNotSubtype("null|{null f1}","{int f1}|int"); }
	@Test public void test_878() { checkNotSubtype("null|{null f1}","{int f2}|int"); }
	@Test public void test_879() { checkNotSubtype("null|{null f1}","X<{X f1}|int>"); }
	@Test public void test_880() { checkNotSubtype("null|{null f1}","X<{X f2}|int>"); }
	@Test public void test_881() { checkIsSubtype("null|{null f1}","X<(X|null)|null>"); }
	@Test public void test_882() { checkNotSubtype("null|{null f1}","X<(X|int)|int>"); }
	@Test public void test_883() { checkIsSubtype("null|{null f2}","null"); }
	@Test public void test_884() { checkNotSubtype("null|{null f2}","int"); }
	@Test public void test_885() { checkNotSubtype("null|{null f2}","{null f1}"); }
	@Test public void test_886() { checkIsSubtype("null|{null f2}","{null f2}"); }
	@Test public void test_887() { checkNotSubtype("null|{null f2}","{int f1}"); }
	@Test public void test_888() { checkNotSubtype("null|{null f2}","{int f2}"); }
	@Test public void test_889() { checkNotSubtype("null|{null f2}","{{null f1} f1}"); }
	@Test public void test_890() { checkNotSubtype("null|{null f2}","{{null f2} f1}"); }
	@Test public void test_891() { checkNotSubtype("null|{null f2}","{{null f1} f2}"); }
	@Test public void test_892() { checkNotSubtype("null|{null f2}","{{null f2} f2}"); }
	@Test public void test_893() { checkNotSubtype("null|{null f2}","{{int f1} f1}"); }
	@Test public void test_894() { checkNotSubtype("null|{null f2}","{{int f2} f1}"); }
	@Test public void test_895() { checkNotSubtype("null|{null f2}","{{int f1} f2}"); }
	@Test public void test_896() { checkNotSubtype("null|{null f2}","{{int f2} f2}"); }
	@Test public void test_897() { checkNotSubtype("null|{null f2}","X<{X|null f1}>"); }
	@Test public void test_898() { checkNotSubtype("null|{null f2}","X<{X|null f2}>"); }
	@Test public void test_899() { checkNotSubtype("null|{null f2}","X<{X|int f1}>"); }
	@Test public void test_900() { checkNotSubtype("null|{null f2}","X<{X|int f2}>"); }
	@Test public void test_901() { checkIsSubtype("null|{null f2}","null|null"); }
	@Test public void test_902() { checkNotSubtype("null|{null f2}","null|int"); }
	@Test public void test_903() { checkNotSubtype("null|{null f2}","null|{null f1}"); }
	@Test public void test_904() { checkIsSubtype("null|{null f2}","null|{null f2}"); }
	@Test public void test_905() { checkNotSubtype("null|{null f2}","X<null|{X f1}>"); }
	@Test public void test_906() { checkNotSubtype("null|{null f2}","X<null|{X f2}>"); }
	@Test public void test_907() { checkIsSubtype("null|{null f2}","X<null|(X|null)>"); }
	@Test public void test_908() { checkNotSubtype("null|{null f2}","int|null"); }
	@Test public void test_909() { checkNotSubtype("null|{null f2}","int|int"); }
	@Test public void test_910() { checkNotSubtype("null|{null f2}","int|{int f1}"); }
	@Test public void test_911() { checkNotSubtype("null|{null f2}","int|{int f2}"); }
	@Test public void test_912() { checkNotSubtype("null|{null f2}","X<int|{X f1}>"); }
	@Test public void test_913() { checkNotSubtype("null|{null f2}","X<int|{X f2}>"); }
	@Test public void test_914() { checkNotSubtype("null|{null f2}","X<int|(X|int)>"); }
	@Test public void test_915() { checkNotSubtype("null|{null f2}","{null f1}|null"); }
	@Test public void test_916() { checkIsSubtype("null|{null f2}","{null f2}|null"); }
	@Test public void test_917() { checkNotSubtype("null|{null f2}","X<{X f1}|null>"); }
	@Test public void test_918() { checkNotSubtype("null|{null f2}","X<{X f2}|null>"); }
	@Test public void test_919() { checkNotSubtype("null|{null f2}","{int f1}|int"); }
	@Test public void test_920() { checkNotSubtype("null|{null f2}","{int f2}|int"); }
	@Test public void test_921() { checkNotSubtype("null|{null f2}","X<{X f1}|int>"); }
	@Test public void test_922() { checkNotSubtype("null|{null f2}","X<{X f2}|int>"); }
	@Test public void test_923() { checkIsSubtype("null|{null f2}","X<(X|null)|null>"); }
	@Test public void test_924() { checkNotSubtype("null|{null f2}","X<(X|int)|int>"); }
	@Test public void test_925() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test public void test_926() { checkNotSubtype("X<null|{X f1}>","int"); }
	@Test public void test_927() { checkIsSubtype("X<null|{X f1}>","{null f1}"); }
	@Test public void test_928() { checkNotSubtype("X<null|{X f1}>","{null f2}"); }
	@Test public void test_929() { checkNotSubtype("X<null|{X f1}>","{int f1}"); }
	@Test public void test_930() { checkNotSubtype("X<null|{X f1}>","{int f2}"); }
	@Test public void test_931() { checkIsSubtype("X<null|{X f1}>","{{null f1} f1}"); }
	@Test public void test_932() { checkNotSubtype("X<null|{X f1}>","{{null f2} f1}"); }
	@Test public void test_933() { checkNotSubtype("X<null|{X f1}>","{{null f1} f2}"); }
	@Test public void test_934() { checkNotSubtype("X<null|{X f1}>","{{null f2} f2}"); }
	@Test public void test_935() { checkNotSubtype("X<null|{X f1}>","{{int f1} f1}"); }
	@Test public void test_936() { checkNotSubtype("X<null|{X f1}>","{{int f2} f1}"); }
	@Test public void test_937() { checkNotSubtype("X<null|{X f1}>","{{int f1} f2}"); }
	@Test public void test_938() { checkNotSubtype("X<null|{X f1}>","{{int f2} f2}"); }
	@Test public void test_939() { checkIsSubtype("X<null|{X f1}>","X<{X|null f1}>"); }
	@Test public void test_940() { checkNotSubtype("X<null|{X f1}>","X<{X|null f2}>"); }
	@Test public void test_941() { checkNotSubtype("X<null|{X f1}>","X<{X|int f1}>"); }
	@Test public void test_942() { checkNotSubtype("X<null|{X f1}>","X<{X|int f2}>"); }
	@Test public void test_943() { checkIsSubtype("X<null|{X f1}>","null|null"); }
	@Test public void test_944() { checkNotSubtype("X<null|{X f1}>","null|int"); }
	@Test public void test_945() { checkIsSubtype("X<null|{X f1}>","null|{null f1}"); }
	@Test public void test_946() { checkNotSubtype("X<null|{X f1}>","null|{null f2}"); }
	@Test public void test_947() { checkIsSubtype("X<null|{X f1}>","X<null|{X f1}>"); }
	@Test public void test_948() { checkNotSubtype("X<null|{X f1}>","X<null|{X f2}>"); }
	@Test public void test_949() { checkIsSubtype("X<null|{X f1}>","X<null|(X|null)>"); }
	@Test public void test_950() { checkNotSubtype("X<null|{X f1}>","int|null"); }
	@Test public void test_951() { checkNotSubtype("X<null|{X f1}>","int|int"); }
	@Test public void test_952() { checkNotSubtype("X<null|{X f1}>","int|{int f1}"); }
	@Test public void test_953() { checkNotSubtype("X<null|{X f1}>","int|{int f2}"); }
	@Test public void test_954() { checkNotSubtype("X<null|{X f1}>","X<int|{X f1}>"); }
	@Test public void test_955() { checkNotSubtype("X<null|{X f1}>","X<int|{X f2}>"); }
	@Test public void test_956() { checkNotSubtype("X<null|{X f1}>","X<int|(X|int)>"); }
	@Test public void test_957() { checkIsSubtype("X<null|{X f1}>","{null f1}|null"); }
	@Test public void test_958() { checkNotSubtype("X<null|{X f1}>","{null f2}|null"); }
	@Test public void test_959() { checkIsSubtype("X<null|{X f1}>","X<{X f1}|null>"); }
	@Test public void test_960() { checkNotSubtype("X<null|{X f1}>","X<{X f2}|null>"); }
	@Test public void test_961() { checkNotSubtype("X<null|{X f1}>","{int f1}|int"); }
	@Test public void test_962() { checkNotSubtype("X<null|{X f1}>","{int f2}|int"); }
	@Test public void test_963() { checkNotSubtype("X<null|{X f1}>","X<{X f1}|int>"); }
	@Test public void test_964() { checkNotSubtype("X<null|{X f1}>","X<{X f2}|int>"); }
	@Test public void test_965() { checkIsSubtype("X<null|{X f1}>","X<(X|null)|null>"); }
	@Test public void test_966() { checkNotSubtype("X<null|{X f1}>","X<(X|int)|int>"); }
	@Test public void test_967() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test public void test_968() { checkNotSubtype("X<null|{X f2}>","int"); }
	@Test public void test_969() { checkNotSubtype("X<null|{X f2}>","{null f1}"); }
	@Test public void test_970() { checkIsSubtype("X<null|{X f2}>","{null f2}"); }
	@Test public void test_971() { checkNotSubtype("X<null|{X f2}>","{int f1}"); }
	@Test public void test_972() { checkNotSubtype("X<null|{X f2}>","{int f2}"); }
	@Test public void test_973() { checkNotSubtype("X<null|{X f2}>","{{null f1} f1}"); }
	@Test public void test_974() { checkNotSubtype("X<null|{X f2}>","{{null f2} f1}"); }
	@Test public void test_975() { checkNotSubtype("X<null|{X f2}>","{{null f1} f2}"); }
	@Test public void test_976() { checkIsSubtype("X<null|{X f2}>","{{null f2} f2}"); }
	@Test public void test_977() { checkNotSubtype("X<null|{X f2}>","{{int f1} f1}"); }
	@Test public void test_978() { checkNotSubtype("X<null|{X f2}>","{{int f2} f1}"); }
	@Test public void test_979() { checkNotSubtype("X<null|{X f2}>","{{int f1} f2}"); }
	@Test public void test_980() { checkNotSubtype("X<null|{X f2}>","{{int f2} f2}"); }
	@Test public void test_981() { checkNotSubtype("X<null|{X f2}>","X<{X|null f1}>"); }
	@Test public void test_982() { checkIsSubtype("X<null|{X f2}>","X<{X|null f2}>"); }
	@Test public void test_983() { checkNotSubtype("X<null|{X f2}>","X<{X|int f1}>"); }
	@Test public void test_984() { checkNotSubtype("X<null|{X f2}>","X<{X|int f2}>"); }
	@Test public void test_985() { checkIsSubtype("X<null|{X f2}>","null|null"); }
	@Test public void test_986() { checkNotSubtype("X<null|{X f2}>","null|int"); }
	@Test public void test_987() { checkNotSubtype("X<null|{X f2}>","null|{null f1}"); }
	@Test public void test_988() { checkIsSubtype("X<null|{X f2}>","null|{null f2}"); }
	@Test public void test_989() { checkNotSubtype("X<null|{X f2}>","X<null|{X f1}>"); }
	@Test public void test_990() { checkIsSubtype("X<null|{X f2}>","X<null|{X f2}>"); }
	@Test public void test_991() { checkIsSubtype("X<null|{X f2}>","X<null|(X|null)>"); }
	@Test public void test_992() { checkNotSubtype("X<null|{X f2}>","int|null"); }
	@Test public void test_993() { checkNotSubtype("X<null|{X f2}>","int|int"); }
	@Test public void test_994() { checkNotSubtype("X<null|{X f2}>","int|{int f1}"); }
	@Test public void test_995() { checkNotSubtype("X<null|{X f2}>","int|{int f2}"); }
	@Test public void test_996() { checkNotSubtype("X<null|{X f2}>","X<int|{X f1}>"); }
	@Test public void test_997() { checkNotSubtype("X<null|{X f2}>","X<int|{X f2}>"); }
	@Test public void test_998() { checkNotSubtype("X<null|{X f2}>","X<int|(X|int)>"); }
	@Test public void test_999() { checkNotSubtype("X<null|{X f2}>","{null f1}|null"); }
	@Test public void test_1000() { checkIsSubtype("X<null|{X f2}>","{null f2}|null"); }
	@Test public void test_1001() { checkNotSubtype("X<null|{X f2}>","X<{X f1}|null>"); }
	@Test public void test_1002() { checkIsSubtype("X<null|{X f2}>","X<{X f2}|null>"); }
	@Test public void test_1003() { checkNotSubtype("X<null|{X f2}>","{int f1}|int"); }
	@Test public void test_1004() { checkNotSubtype("X<null|{X f2}>","{int f2}|int"); }
	@Test public void test_1005() { checkNotSubtype("X<null|{X f2}>","X<{X f1}|int>"); }
	@Test public void test_1006() { checkNotSubtype("X<null|{X f2}>","X<{X f2}|int>"); }
	@Test public void test_1007() { checkIsSubtype("X<null|{X f2}>","X<(X|null)|null>"); }
	@Test public void test_1008() { checkNotSubtype("X<null|{X f2}>","X<(X|int)|int>"); }
	@Test public void test_1009() { checkIsSubtype("X<null|(X|null)>","null"); }
	@Test public void test_1010() { checkNotSubtype("X<null|(X|null)>","int"); }
	@Test public void test_1011() { checkNotSubtype("X<null|(X|null)>","{null f1}"); }
	@Test public void test_1012() { checkNotSubtype("X<null|(X|null)>","{null f2}"); }
	@Test public void test_1013() { checkNotSubtype("X<null|(X|null)>","{int f1}"); }
	@Test public void test_1014() { checkNotSubtype("X<null|(X|null)>","{int f2}"); }
	@Test public void test_1015() { checkNotSubtype("X<null|(X|null)>","{{null f1} f1}"); }
	@Test public void test_1016() { checkNotSubtype("X<null|(X|null)>","{{null f2} f1}"); }
	@Test public void test_1017() { checkNotSubtype("X<null|(X|null)>","{{null f1} f2}"); }
	@Test public void test_1018() { checkNotSubtype("X<null|(X|null)>","{{null f2} f2}"); }
	@Test public void test_1019() { checkNotSubtype("X<null|(X|null)>","{{int f1} f1}"); }
	@Test public void test_1020() { checkNotSubtype("X<null|(X|null)>","{{int f2} f1}"); }
	@Test public void test_1021() { checkNotSubtype("X<null|(X|null)>","{{int f1} f2}"); }
	@Test public void test_1022() { checkNotSubtype("X<null|(X|null)>","{{int f2} f2}"); }
	@Test public void test_1023() { checkNotSubtype("X<null|(X|null)>","X<{X|null f1}>"); }
	@Test public void test_1024() { checkNotSubtype("X<null|(X|null)>","X<{X|null f2}>"); }
	@Test public void test_1025() { checkNotSubtype("X<null|(X|null)>","X<{X|int f1}>"); }
	@Test public void test_1026() { checkNotSubtype("X<null|(X|null)>","X<{X|int f2}>"); }
	@Test public void test_1027() { checkIsSubtype("X<null|(X|null)>","null|null"); }
	@Test public void test_1028() { checkNotSubtype("X<null|(X|null)>","null|int"); }
	@Test public void test_1029() { checkNotSubtype("X<null|(X|null)>","null|{null f1}"); }
	@Test public void test_1030() { checkNotSubtype("X<null|(X|null)>","null|{null f2}"); }
	@Test public void test_1031() { checkNotSubtype("X<null|(X|null)>","X<null|{X f1}>"); }
	@Test public void test_1032() { checkNotSubtype("X<null|(X|null)>","X<null|{X f2}>"); }
	@Test public void test_1033() { checkIsSubtype("X<null|(X|null)>","X<null|(X|null)>"); }
	@Test public void test_1034() { checkNotSubtype("X<null|(X|null)>","int|null"); }
	@Test public void test_1035() { checkNotSubtype("X<null|(X|null)>","int|int"); }
	@Test public void test_1036() { checkNotSubtype("X<null|(X|null)>","int|{int f1}"); }
	@Test public void test_1037() { checkNotSubtype("X<null|(X|null)>","int|{int f2}"); }
	@Test public void test_1038() { checkNotSubtype("X<null|(X|null)>","X<int|{X f1}>"); }
	@Test public void test_1039() { checkNotSubtype("X<null|(X|null)>","X<int|{X f2}>"); }
	@Test public void test_1040() { checkNotSubtype("X<null|(X|null)>","X<int|(X|int)>"); }
	@Test public void test_1041() { checkNotSubtype("X<null|(X|null)>","{null f1}|null"); }
	@Test public void test_1042() { checkNotSubtype("X<null|(X|null)>","{null f2}|null"); }
	@Test public void test_1043() { checkNotSubtype("X<null|(X|null)>","X<{X f1}|null>"); }
	@Test public void test_1044() { checkNotSubtype("X<null|(X|null)>","X<{X f2}|null>"); }
	@Test public void test_1045() { checkNotSubtype("X<null|(X|null)>","{int f1}|int"); }
	@Test public void test_1046() { checkNotSubtype("X<null|(X|null)>","{int f2}|int"); }
	@Test public void test_1047() { checkNotSubtype("X<null|(X|null)>","X<{X f1}|int>"); }
	@Test public void test_1048() { checkNotSubtype("X<null|(X|null)>","X<{X f2}|int>"); }
	@Test public void test_1049() { checkIsSubtype("X<null|(X|null)>","X<(X|null)|null>"); }
	@Test public void test_1050() { checkNotSubtype("X<null|(X|null)>","X<(X|int)|int>"); }
	@Test public void test_1051() { checkIsSubtype("int|null","null"); }
	@Test public void test_1052() { checkIsSubtype("int|null","int"); }
	@Test public void test_1053() { checkNotSubtype("int|null","{null f1}"); }
	@Test public void test_1054() { checkNotSubtype("int|null","{null f2}"); }
	@Test public void test_1055() { checkNotSubtype("int|null","{int f1}"); }
	@Test public void test_1056() { checkNotSubtype("int|null","{int f2}"); }
	@Test public void test_1057() { checkNotSubtype("int|null","{{null f1} f1}"); }
	@Test public void test_1058() { checkNotSubtype("int|null","{{null f2} f1}"); }
	@Test public void test_1059() { checkNotSubtype("int|null","{{null f1} f2}"); }
	@Test public void test_1060() { checkNotSubtype("int|null","{{null f2} f2}"); }
	@Test public void test_1061() { checkNotSubtype("int|null","{{int f1} f1}"); }
	@Test public void test_1062() { checkNotSubtype("int|null","{{int f2} f1}"); }
	@Test public void test_1063() { checkNotSubtype("int|null","{{int f1} f2}"); }
	@Test public void test_1064() { checkNotSubtype("int|null","{{int f2} f2}"); }
	@Test public void test_1065() { checkNotSubtype("int|null","X<{X|null f1}>"); }
	@Test public void test_1066() { checkNotSubtype("int|null","X<{X|null f2}>"); }
	@Test public void test_1067() { checkNotSubtype("int|null","X<{X|int f1}>"); }
	@Test public void test_1068() { checkNotSubtype("int|null","X<{X|int f2}>"); }
	@Test public void test_1069() { checkIsSubtype("int|null","null|null"); }
	@Test public void test_1070() { checkIsSubtype("int|null","null|int"); }
	@Test public void test_1071() { checkNotSubtype("int|null","null|{null f1}"); }
	@Test public void test_1072() { checkNotSubtype("int|null","null|{null f2}"); }
	@Test public void test_1073() { checkNotSubtype("int|null","X<null|{X f1}>"); }
	@Test public void test_1074() { checkNotSubtype("int|null","X<null|{X f2}>"); }
	@Test public void test_1075() { checkIsSubtype("int|null","X<null|(X|null)>"); }
	@Test public void test_1076() { checkIsSubtype("int|null","int|null"); }
	@Test public void test_1077() { checkIsSubtype("int|null","int|int"); }
	@Test public void test_1078() { checkNotSubtype("int|null","int|{int f1}"); }
	@Test public void test_1079() { checkNotSubtype("int|null","int|{int f2}"); }
	@Test public void test_1080() { checkNotSubtype("int|null","X<int|{X f1}>"); }
	@Test public void test_1081() { checkNotSubtype("int|null","X<int|{X f2}>"); }
	@Test public void test_1082() { checkIsSubtype("int|null","X<int|(X|int)>"); }
	@Test public void test_1083() { checkNotSubtype("int|null","{null f1}|null"); }
	@Test public void test_1084() { checkNotSubtype("int|null","{null f2}|null"); }
	@Test public void test_1085() { checkNotSubtype("int|null","X<{X f1}|null>"); }
	@Test public void test_1086() { checkNotSubtype("int|null","X<{X f2}|null>"); }
	@Test public void test_1087() { checkNotSubtype("int|null","{int f1}|int"); }
	@Test public void test_1088() { checkNotSubtype("int|null","{int f2}|int"); }
	@Test public void test_1089() { checkNotSubtype("int|null","X<{X f1}|int>"); }
	@Test public void test_1090() { checkNotSubtype("int|null","X<{X f2}|int>"); }
	@Test public void test_1091() { checkIsSubtype("int|null","X<(X|null)|null>"); }
	@Test public void test_1092() { checkIsSubtype("int|null","X<(X|int)|int>"); }
	@Test public void test_1093() { checkNotSubtype("int|int","null"); }
	@Test public void test_1094() { checkIsSubtype("int|int","int"); }
	@Test public void test_1095() { checkNotSubtype("int|int","{null f1}"); }
	@Test public void test_1096() { checkNotSubtype("int|int","{null f2}"); }
	@Test public void test_1097() { checkNotSubtype("int|int","{int f1}"); }
	@Test public void test_1098() { checkNotSubtype("int|int","{int f2}"); }
	@Test public void test_1099() { checkNotSubtype("int|int","{{null f1} f1}"); }
	@Test public void test_1100() { checkNotSubtype("int|int","{{null f2} f1}"); }
	@Test public void test_1101() { checkNotSubtype("int|int","{{null f1} f2}"); }
	@Test public void test_1102() { checkNotSubtype("int|int","{{null f2} f2}"); }
	@Test public void test_1103() { checkNotSubtype("int|int","{{int f1} f1}"); }
	@Test public void test_1104() { checkNotSubtype("int|int","{{int f2} f1}"); }
	@Test public void test_1105() { checkNotSubtype("int|int","{{int f1} f2}"); }
	@Test public void test_1106() { checkNotSubtype("int|int","{{int f2} f2}"); }
	@Test public void test_1107() { checkNotSubtype("int|int","X<{X|null f1}>"); }
	@Test public void test_1108() { checkNotSubtype("int|int","X<{X|null f2}>"); }
	@Test public void test_1109() { checkNotSubtype("int|int","X<{X|int f1}>"); }
	@Test public void test_1110() { checkNotSubtype("int|int","X<{X|int f2}>"); }
	@Test public void test_1111() { checkNotSubtype("int|int","null|null"); }
	@Test public void test_1112() { checkNotSubtype("int|int","null|int"); }
	@Test public void test_1113() { checkNotSubtype("int|int","null|{null f1}"); }
	@Test public void test_1114() { checkNotSubtype("int|int","null|{null f2}"); }
	@Test public void test_1115() { checkNotSubtype("int|int","X<null|{X f1}>"); }
	@Test public void test_1116() { checkNotSubtype("int|int","X<null|{X f2}>"); }
	@Test public void test_1117() { checkNotSubtype("int|int","X<null|(X|null)>"); }
	@Test public void test_1118() { checkNotSubtype("int|int","int|null"); }
	@Test public void test_1119() { checkIsSubtype("int|int","int|int"); }
	@Test public void test_1120() { checkNotSubtype("int|int","int|{int f1}"); }
	@Test public void test_1121() { checkNotSubtype("int|int","int|{int f2}"); }
	@Test public void test_1122() { checkNotSubtype("int|int","X<int|{X f1}>"); }
	@Test public void test_1123() { checkNotSubtype("int|int","X<int|{X f2}>"); }
	@Test public void test_1124() { checkIsSubtype("int|int","X<int|(X|int)>"); }
	@Test public void test_1125() { checkNotSubtype("int|int","{null f1}|null"); }
	@Test public void test_1126() { checkNotSubtype("int|int","{null f2}|null"); }
	@Test public void test_1127() { checkNotSubtype("int|int","X<{X f1}|null>"); }
	@Test public void test_1128() { checkNotSubtype("int|int","X<{X f2}|null>"); }
	@Test public void test_1129() { checkNotSubtype("int|int","{int f1}|int"); }
	@Test public void test_1130() { checkNotSubtype("int|int","{int f2}|int"); }
	@Test public void test_1131() { checkNotSubtype("int|int","X<{X f1}|int>"); }
	@Test public void test_1132() { checkNotSubtype("int|int","X<{X f2}|int>"); }
	@Test public void test_1133() { checkNotSubtype("int|int","X<(X|null)|null>"); }
	@Test public void test_1134() { checkIsSubtype("int|int","X<(X|int)|int>"); }
	@Test public void test_1135() { checkNotSubtype("int|{int f1}","null"); }
	@Test public void test_1136() { checkIsSubtype("int|{int f1}","int"); }
	@Test public void test_1137() { checkNotSubtype("int|{int f1}","{null f1}"); }
	@Test public void test_1138() { checkNotSubtype("int|{int f1}","{null f2}"); }
	@Test public void test_1139() { checkIsSubtype("int|{int f1}","{int f1}"); }
	@Test public void test_1140() { checkNotSubtype("int|{int f1}","{int f2}"); }
	@Test public void test_1141() { checkNotSubtype("int|{int f1}","{{null f1} f1}"); }
	@Test public void test_1142() { checkNotSubtype("int|{int f1}","{{null f2} f1}"); }
	@Test public void test_1143() { checkNotSubtype("int|{int f1}","{{null f1} f2}"); }
	@Test public void test_1144() { checkNotSubtype("int|{int f1}","{{null f2} f2}"); }
	@Test public void test_1145() { checkNotSubtype("int|{int f1}","{{int f1} f1}"); }
	@Test public void test_1146() { checkNotSubtype("int|{int f1}","{{int f2} f1}"); }
	@Test public void test_1147() { checkNotSubtype("int|{int f1}","{{int f1} f2}"); }
	@Test public void test_1148() { checkNotSubtype("int|{int f1}","{{int f2} f2}"); }
	@Test public void test_1149() { checkNotSubtype("int|{int f1}","X<{X|null f1}>"); }
	@Test public void test_1150() { checkNotSubtype("int|{int f1}","X<{X|null f2}>"); }
	@Test public void test_1151() { checkNotSubtype("int|{int f1}","X<{X|int f1}>"); }
	@Test public void test_1152() { checkNotSubtype("int|{int f1}","X<{X|int f2}>"); }
	@Test public void test_1153() { checkNotSubtype("int|{int f1}","null|null"); }
	@Test public void test_1154() { checkNotSubtype("int|{int f1}","null|int"); }
	@Test public void test_1155() { checkNotSubtype("int|{int f1}","null|{null f1}"); }
	@Test public void test_1156() { checkNotSubtype("int|{int f1}","null|{null f2}"); }
	@Test public void test_1157() { checkNotSubtype("int|{int f1}","X<null|{X f1}>"); }
	@Test public void test_1158() { checkNotSubtype("int|{int f1}","X<null|{X f2}>"); }
	@Test public void test_1159() { checkNotSubtype("int|{int f1}","X<null|(X|null)>"); }
	@Test public void test_1160() { checkNotSubtype("int|{int f1}","int|null"); }
	@Test public void test_1161() { checkIsSubtype("int|{int f1}","int|int"); }
	@Test public void test_1162() { checkIsSubtype("int|{int f1}","int|{int f1}"); }
	@Test public void test_1163() { checkNotSubtype("int|{int f1}","int|{int f2}"); }
	@Test public void test_1164() { checkNotSubtype("int|{int f1}","X<int|{X f1}>"); }
	@Test public void test_1165() { checkNotSubtype("int|{int f1}","X<int|{X f2}>"); }
	@Test public void test_1166() { checkIsSubtype("int|{int f1}","X<int|(X|int)>"); }
	@Test public void test_1167() { checkNotSubtype("int|{int f1}","{null f1}|null"); }
	@Test public void test_1168() { checkNotSubtype("int|{int f1}","{null f2}|null"); }
	@Test public void test_1169() { checkNotSubtype("int|{int f1}","X<{X f1}|null>"); }
	@Test public void test_1170() { checkNotSubtype("int|{int f1}","X<{X f2}|null>"); }
	@Test public void test_1171() { checkIsSubtype("int|{int f1}","{int f1}|int"); }
	@Test public void test_1172() { checkNotSubtype("int|{int f1}","{int f2}|int"); }
	@Test public void test_1173() { checkNotSubtype("int|{int f1}","X<{X f1}|int>"); }
	@Test public void test_1174() { checkNotSubtype("int|{int f1}","X<{X f2}|int>"); }
	@Test public void test_1175() { checkNotSubtype("int|{int f1}","X<(X|null)|null>"); }
	@Test public void test_1176() { checkIsSubtype("int|{int f1}","X<(X|int)|int>"); }
	@Test public void test_1177() { checkNotSubtype("int|{int f2}","null"); }
	@Test public void test_1178() { checkIsSubtype("int|{int f2}","int"); }
	@Test public void test_1179() { checkNotSubtype("int|{int f2}","{null f1}"); }
	@Test public void test_1180() { checkNotSubtype("int|{int f2}","{null f2}"); }
	@Test public void test_1181() { checkNotSubtype("int|{int f2}","{int f1}"); }
	@Test public void test_1182() { checkIsSubtype("int|{int f2}","{int f2}"); }
	@Test public void test_1183() { checkNotSubtype("int|{int f2}","{{null f1} f1}"); }
	@Test public void test_1184() { checkNotSubtype("int|{int f2}","{{null f2} f1}"); }
	@Test public void test_1185() { checkNotSubtype("int|{int f2}","{{null f1} f2}"); }
	@Test public void test_1186() { checkNotSubtype("int|{int f2}","{{null f2} f2}"); }
	@Test public void test_1187() { checkNotSubtype("int|{int f2}","{{int f1} f1}"); }
	@Test public void test_1188() { checkNotSubtype("int|{int f2}","{{int f2} f1}"); }
	@Test public void test_1189() { checkNotSubtype("int|{int f2}","{{int f1} f2}"); }
	@Test public void test_1190() { checkNotSubtype("int|{int f2}","{{int f2} f2}"); }
	@Test public void test_1191() { checkNotSubtype("int|{int f2}","X<{X|null f1}>"); }
	@Test public void test_1192() { checkNotSubtype("int|{int f2}","X<{X|null f2}>"); }
	@Test public void test_1193() { checkNotSubtype("int|{int f2}","X<{X|int f1}>"); }
	@Test public void test_1194() { checkNotSubtype("int|{int f2}","X<{X|int f2}>"); }
	@Test public void test_1195() { checkNotSubtype("int|{int f2}","null|null"); }
	@Test public void test_1196() { checkNotSubtype("int|{int f2}","null|int"); }
	@Test public void test_1197() { checkNotSubtype("int|{int f2}","null|{null f1}"); }
	@Test public void test_1198() { checkNotSubtype("int|{int f2}","null|{null f2}"); }
	@Test public void test_1199() { checkNotSubtype("int|{int f2}","X<null|{X f1}>"); }
	@Test public void test_1200() { checkNotSubtype("int|{int f2}","X<null|{X f2}>"); }
	@Test public void test_1201() { checkNotSubtype("int|{int f2}","X<null|(X|null)>"); }
	@Test public void test_1202() { checkNotSubtype("int|{int f2}","int|null"); }
	@Test public void test_1203() { checkIsSubtype("int|{int f2}","int|int"); }
	@Test public void test_1204() { checkNotSubtype("int|{int f2}","int|{int f1}"); }
	@Test public void test_1205() { checkIsSubtype("int|{int f2}","int|{int f2}"); }
	@Test public void test_1206() { checkNotSubtype("int|{int f2}","X<int|{X f1}>"); }
	@Test public void test_1207() { checkNotSubtype("int|{int f2}","X<int|{X f2}>"); }
	@Test public void test_1208() { checkIsSubtype("int|{int f2}","X<int|(X|int)>"); }
	@Test public void test_1209() { checkNotSubtype("int|{int f2}","{null f1}|null"); }
	@Test public void test_1210() { checkNotSubtype("int|{int f2}","{null f2}|null"); }
	@Test public void test_1211() { checkNotSubtype("int|{int f2}","X<{X f1}|null>"); }
	@Test public void test_1212() { checkNotSubtype("int|{int f2}","X<{X f2}|null>"); }
	@Test public void test_1213() { checkNotSubtype("int|{int f2}","{int f1}|int"); }
	@Test public void test_1214() { checkIsSubtype("int|{int f2}","{int f2}|int"); }
	@Test public void test_1215() { checkNotSubtype("int|{int f2}","X<{X f1}|int>"); }
	@Test public void test_1216() { checkNotSubtype("int|{int f2}","X<{X f2}|int>"); }
	@Test public void test_1217() { checkNotSubtype("int|{int f2}","X<(X|null)|null>"); }
	@Test public void test_1218() { checkIsSubtype("int|{int f2}","X<(X|int)|int>"); }
	@Test public void test_1219() { checkNotSubtype("X<int|{X f1}>","null"); }
	@Test public void test_1220() { checkIsSubtype("X<int|{X f1}>","int"); }
	@Test public void test_1221() { checkNotSubtype("X<int|{X f1}>","{null f1}"); }
	@Test public void test_1222() { checkNotSubtype("X<int|{X f1}>","{null f2}"); }
	@Test public void test_1223() { checkIsSubtype("X<int|{X f1}>","{int f1}"); }
	@Test public void test_1224() { checkNotSubtype("X<int|{X f1}>","{int f2}"); }
	@Test public void test_1225() { checkNotSubtype("X<int|{X f1}>","{{null f1} f1}"); }
	@Test public void test_1226() { checkNotSubtype("X<int|{X f1}>","{{null f2} f1}"); }
	@Test public void test_1227() { checkNotSubtype("X<int|{X f1}>","{{null f1} f2}"); }
	@Test public void test_1228() { checkNotSubtype("X<int|{X f1}>","{{null f2} f2}"); }
	@Test public void test_1229() { checkIsSubtype("X<int|{X f1}>","{{int f1} f1}"); }
	@Test public void test_1230() { checkNotSubtype("X<int|{X f1}>","{{int f2} f1}"); }
	@Test public void test_1231() { checkNotSubtype("X<int|{X f1}>","{{int f1} f2}"); }
	@Test public void test_1232() { checkNotSubtype("X<int|{X f1}>","{{int f2} f2}"); }
	@Test public void test_1233() { checkNotSubtype("X<int|{X f1}>","X<{X|null f1}>"); }
	@Test public void test_1234() { checkNotSubtype("X<int|{X f1}>","X<{X|null f2}>"); }
	@Test public void test_1235() { checkIsSubtype("X<int|{X f1}>","X<{X|int f1}>"); }
	@Test public void test_1236() { checkNotSubtype("X<int|{X f1}>","X<{X|int f2}>"); }
	@Test public void test_1237() { checkNotSubtype("X<int|{X f1}>","null|null"); }
	@Test public void test_1238() { checkNotSubtype("X<int|{X f1}>","null|int"); }
	@Test public void test_1239() { checkNotSubtype("X<int|{X f1}>","null|{null f1}"); }
	@Test public void test_1240() { checkNotSubtype("X<int|{X f1}>","null|{null f2}"); }
	@Test public void test_1241() { checkNotSubtype("X<int|{X f1}>","X<null|{X f1}>"); }
	@Test public void test_1242() { checkNotSubtype("X<int|{X f1}>","X<null|{X f2}>"); }
	@Test public void test_1243() { checkNotSubtype("X<int|{X f1}>","X<null|(X|null)>"); }
	@Test public void test_1244() { checkNotSubtype("X<int|{X f1}>","int|null"); }
	@Test public void test_1245() { checkIsSubtype("X<int|{X f1}>","int|int"); }
	@Test public void test_1246() { checkIsSubtype("X<int|{X f1}>","int|{int f1}"); }
	@Test public void test_1247() { checkNotSubtype("X<int|{X f1}>","int|{int f2}"); }
	@Test public void test_1248() { checkIsSubtype("X<int|{X f1}>","X<int|{X f1}>"); }
	@Test public void test_1249() { checkNotSubtype("X<int|{X f1}>","X<int|{X f2}>"); }
	@Test public void test_1250() { checkIsSubtype("X<int|{X f1}>","X<int|(X|int)>"); }
	@Test public void test_1251() { checkNotSubtype("X<int|{X f1}>","{null f1}|null"); }
	@Test public void test_1252() { checkNotSubtype("X<int|{X f1}>","{null f2}|null"); }
	@Test public void test_1253() { checkNotSubtype("X<int|{X f1}>","X<{X f1}|null>"); }
	@Test public void test_1254() { checkNotSubtype("X<int|{X f1}>","X<{X f2}|null>"); }
	@Test public void test_1255() { checkIsSubtype("X<int|{X f1}>","{int f1}|int"); }
	@Test public void test_1256() { checkNotSubtype("X<int|{X f1}>","{int f2}|int"); }
	@Test public void test_1257() { checkIsSubtype("X<int|{X f1}>","X<{X f1}|int>"); }
	@Test public void test_1258() { checkNotSubtype("X<int|{X f1}>","X<{X f2}|int>"); }
	@Test public void test_1259() { checkNotSubtype("X<int|{X f1}>","X<(X|null)|null>"); }
	@Test public void test_1260() { checkIsSubtype("X<int|{X f1}>","X<(X|int)|int>"); }
	@Test public void test_1261() { checkNotSubtype("X<int|{X f2}>","null"); }
	@Test public void test_1262() { checkIsSubtype("X<int|{X f2}>","int"); }
	@Test public void test_1263() { checkNotSubtype("X<int|{X f2}>","{null f1}"); }
	@Test public void test_1264() { checkNotSubtype("X<int|{X f2}>","{null f2}"); }
	@Test public void test_1265() { checkNotSubtype("X<int|{X f2}>","{int f1}"); }
	@Test public void test_1266() { checkIsSubtype("X<int|{X f2}>","{int f2}"); }
	@Test public void test_1267() { checkNotSubtype("X<int|{X f2}>","{{null f1} f1}"); }
	@Test public void test_1268() { checkNotSubtype("X<int|{X f2}>","{{null f2} f1}"); }
	@Test public void test_1269() { checkNotSubtype("X<int|{X f2}>","{{null f1} f2}"); }
	@Test public void test_1270() { checkNotSubtype("X<int|{X f2}>","{{null f2} f2}"); }
	@Test public void test_1271() { checkNotSubtype("X<int|{X f2}>","{{int f1} f1}"); }
	@Test public void test_1272() { checkNotSubtype("X<int|{X f2}>","{{int f2} f1}"); }
	@Test public void test_1273() { checkNotSubtype("X<int|{X f2}>","{{int f1} f2}"); }
	@Test public void test_1274() { checkIsSubtype("X<int|{X f2}>","{{int f2} f2}"); }
	@Test public void test_1275() { checkNotSubtype("X<int|{X f2}>","X<{X|null f1}>"); }
	@Test public void test_1276() { checkNotSubtype("X<int|{X f2}>","X<{X|null f2}>"); }
	@Test public void test_1277() { checkNotSubtype("X<int|{X f2}>","X<{X|int f1}>"); }
	@Test public void test_1278() { checkIsSubtype("X<int|{X f2}>","X<{X|int f2}>"); }
	@Test public void test_1279() { checkNotSubtype("X<int|{X f2}>","null|null"); }
	@Test public void test_1280() { checkNotSubtype("X<int|{X f2}>","null|int"); }
	@Test public void test_1281() { checkNotSubtype("X<int|{X f2}>","null|{null f1}"); }
	@Test public void test_1282() { checkNotSubtype("X<int|{X f2}>","null|{null f2}"); }
	@Test public void test_1283() { checkNotSubtype("X<int|{X f2}>","X<null|{X f1}>"); }
	@Test public void test_1284() { checkNotSubtype("X<int|{X f2}>","X<null|{X f2}>"); }
	@Test public void test_1285() { checkNotSubtype("X<int|{X f2}>","X<null|(X|null)>"); }
	@Test public void test_1286() { checkNotSubtype("X<int|{X f2}>","int|null"); }
	@Test public void test_1287() { checkIsSubtype("X<int|{X f2}>","int|int"); }
	@Test public void test_1288() { checkNotSubtype("X<int|{X f2}>","int|{int f1}"); }
	@Test public void test_1289() { checkIsSubtype("X<int|{X f2}>","int|{int f2}"); }
	@Test public void test_1290() { checkNotSubtype("X<int|{X f2}>","X<int|{X f1}>"); }
	@Test public void test_1291() { checkIsSubtype("X<int|{X f2}>","X<int|{X f2}>"); }
	@Test public void test_1292() { checkIsSubtype("X<int|{X f2}>","X<int|(X|int)>"); }
	@Test public void test_1293() { checkNotSubtype("X<int|{X f2}>","{null f1}|null"); }
	@Test public void test_1294() { checkNotSubtype("X<int|{X f2}>","{null f2}|null"); }
	@Test public void test_1295() { checkNotSubtype("X<int|{X f2}>","X<{X f1}|null>"); }
	@Test public void test_1296() { checkNotSubtype("X<int|{X f2}>","X<{X f2}|null>"); }
	@Test public void test_1297() { checkNotSubtype("X<int|{X f2}>","{int f1}|int"); }
	@Test public void test_1298() { checkIsSubtype("X<int|{X f2}>","{int f2}|int"); }
	@Test public void test_1299() { checkNotSubtype("X<int|{X f2}>","X<{X f1}|int>"); }
	@Test public void test_1300() { checkIsSubtype("X<int|{X f2}>","X<{X f2}|int>"); }
	@Test public void test_1301() { checkNotSubtype("X<int|{X f2}>","X<(X|null)|null>"); }
	@Test public void test_1302() { checkIsSubtype("X<int|{X f2}>","X<(X|int)|int>"); }
	@Test public void test_1303() { checkNotSubtype("X<int|(X|int)>","null"); }
	@Test public void test_1304() { checkIsSubtype("X<int|(X|int)>","int"); }
	@Test public void test_1305() { checkNotSubtype("X<int|(X|int)>","{null f1}"); }
	@Test public void test_1306() { checkNotSubtype("X<int|(X|int)>","{null f2}"); }
	@Test public void test_1307() { checkNotSubtype("X<int|(X|int)>","{int f1}"); }
	@Test public void test_1308() { checkNotSubtype("X<int|(X|int)>","{int f2}"); }
	@Test public void test_1309() { checkNotSubtype("X<int|(X|int)>","{{null f1} f1}"); }
	@Test public void test_1310() { checkNotSubtype("X<int|(X|int)>","{{null f2} f1}"); }
	@Test public void test_1311() { checkNotSubtype("X<int|(X|int)>","{{null f1} f2}"); }
	@Test public void test_1312() { checkNotSubtype("X<int|(X|int)>","{{null f2} f2}"); }
	@Test public void test_1313() { checkNotSubtype("X<int|(X|int)>","{{int f1} f1}"); }
	@Test public void test_1314() { checkNotSubtype("X<int|(X|int)>","{{int f2} f1}"); }
	@Test public void test_1315() { checkNotSubtype("X<int|(X|int)>","{{int f1} f2}"); }
	@Test public void test_1316() { checkNotSubtype("X<int|(X|int)>","{{int f2} f2}"); }
	@Test public void test_1317() { checkNotSubtype("X<int|(X|int)>","X<{X|null f1}>"); }
	@Test public void test_1318() { checkNotSubtype("X<int|(X|int)>","X<{X|null f2}>"); }
	@Test public void test_1319() { checkNotSubtype("X<int|(X|int)>","X<{X|int f1}>"); }
	@Test public void test_1320() { checkNotSubtype("X<int|(X|int)>","X<{X|int f2}>"); }
	@Test public void test_1321() { checkNotSubtype("X<int|(X|int)>","null|null"); }
	@Test public void test_1322() { checkNotSubtype("X<int|(X|int)>","null|int"); }
	@Test public void test_1323() { checkNotSubtype("X<int|(X|int)>","null|{null f1}"); }
	@Test public void test_1324() { checkNotSubtype("X<int|(X|int)>","null|{null f2}"); }
	@Test public void test_1325() { checkNotSubtype("X<int|(X|int)>","X<null|{X f1}>"); }
	@Test public void test_1326() { checkNotSubtype("X<int|(X|int)>","X<null|{X f2}>"); }
	@Test public void test_1327() { checkNotSubtype("X<int|(X|int)>","X<null|(X|null)>"); }
	@Test public void test_1328() { checkNotSubtype("X<int|(X|int)>","int|null"); }
	@Test public void test_1329() { checkIsSubtype("X<int|(X|int)>","int|int"); }
	@Test public void test_1330() { checkNotSubtype("X<int|(X|int)>","int|{int f1}"); }
	@Test public void test_1331() { checkNotSubtype("X<int|(X|int)>","int|{int f2}"); }
	@Test public void test_1332() { checkNotSubtype("X<int|(X|int)>","X<int|{X f1}>"); }
	@Test public void test_1333() { checkNotSubtype("X<int|(X|int)>","X<int|{X f2}>"); }
	@Test public void test_1334() { checkIsSubtype("X<int|(X|int)>","X<int|(X|int)>"); }
	@Test public void test_1335() { checkNotSubtype("X<int|(X|int)>","{null f1}|null"); }
	@Test public void test_1336() { checkNotSubtype("X<int|(X|int)>","{null f2}|null"); }
	@Test public void test_1337() { checkNotSubtype("X<int|(X|int)>","X<{X f1}|null>"); }
	@Test public void test_1338() { checkNotSubtype("X<int|(X|int)>","X<{X f2}|null>"); }
	@Test public void test_1339() { checkNotSubtype("X<int|(X|int)>","{int f1}|int"); }
	@Test public void test_1340() { checkNotSubtype("X<int|(X|int)>","{int f2}|int"); }
	@Test public void test_1341() { checkNotSubtype("X<int|(X|int)>","X<{X f1}|int>"); }
	@Test public void test_1342() { checkNotSubtype("X<int|(X|int)>","X<{X f2}|int>"); }
	@Test public void test_1343() { checkNotSubtype("X<int|(X|int)>","X<(X|null)|null>"); }
	@Test public void test_1344() { checkIsSubtype("X<int|(X|int)>","X<(X|int)|int>"); }
	@Test public void test_1345() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_1346() { checkNotSubtype("{null f1}|null","int"); }
	@Test public void test_1347() { checkIsSubtype("{null f1}|null","{null f1}"); }
	@Test public void test_1348() { checkNotSubtype("{null f1}|null","{null f2}"); }
	@Test public void test_1349() { checkNotSubtype("{null f1}|null","{int f1}"); }
	@Test public void test_1350() { checkNotSubtype("{null f1}|null","{int f2}"); }
	@Test public void test_1351() { checkNotSubtype("{null f1}|null","{{null f1} f1}"); }
	@Test public void test_1352() { checkNotSubtype("{null f1}|null","{{null f2} f1}"); }
	@Test public void test_1353() { checkNotSubtype("{null f1}|null","{{null f1} f2}"); }
	@Test public void test_1354() { checkNotSubtype("{null f1}|null","{{null f2} f2}"); }
	@Test public void test_1355() { checkNotSubtype("{null f1}|null","{{int f1} f1}"); }
	@Test public void test_1356() { checkNotSubtype("{null f1}|null","{{int f2} f1}"); }
	@Test public void test_1357() { checkNotSubtype("{null f1}|null","{{int f1} f2}"); }
	@Test public void test_1358() { checkNotSubtype("{null f1}|null","{{int f2} f2}"); }
	@Test public void test_1359() { checkNotSubtype("{null f1}|null","X<{X|null f1}>"); }
	@Test public void test_1360() { checkNotSubtype("{null f1}|null","X<{X|null f2}>"); }
	@Test public void test_1361() { checkNotSubtype("{null f1}|null","X<{X|int f1}>"); }
	@Test public void test_1362() { checkNotSubtype("{null f1}|null","X<{X|int f2}>"); }
	@Test public void test_1363() { checkIsSubtype("{null f1}|null","null|null"); }
	@Test public void test_1364() { checkNotSubtype("{null f1}|null","null|int"); }
	@Test public void test_1365() { checkIsSubtype("{null f1}|null","null|{null f1}"); }
	@Test public void test_1366() { checkNotSubtype("{null f1}|null","null|{null f2}"); }
	@Test public void test_1367() { checkNotSubtype("{null f1}|null","X<null|{X f1}>"); }
	@Test public void test_1368() { checkNotSubtype("{null f1}|null","X<null|{X f2}>"); }
	@Test public void test_1369() { checkIsSubtype("{null f1}|null","X<null|(X|null)>"); }
	@Test public void test_1370() { checkNotSubtype("{null f1}|null","int|null"); }
	@Test public void test_1371() { checkNotSubtype("{null f1}|null","int|int"); }
	@Test public void test_1372() { checkNotSubtype("{null f1}|null","int|{int f1}"); }
	@Test public void test_1373() { checkNotSubtype("{null f1}|null","int|{int f2}"); }
	@Test public void test_1374() { checkNotSubtype("{null f1}|null","X<int|{X f1}>"); }
	@Test public void test_1375() { checkNotSubtype("{null f1}|null","X<int|{X f2}>"); }
	@Test public void test_1376() { checkNotSubtype("{null f1}|null","X<int|(X|int)>"); }
	@Test public void test_1377() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test public void test_1378() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test public void test_1379() { checkNotSubtype("{null f1}|null","X<{X f1}|null>"); }
	@Test public void test_1380() { checkNotSubtype("{null f1}|null","X<{X f2}|null>"); }
	@Test public void test_1381() { checkNotSubtype("{null f1}|null","{int f1}|int"); }
	@Test public void test_1382() { checkNotSubtype("{null f1}|null","{int f2}|int"); }
	@Test public void test_1383() { checkNotSubtype("{null f1}|null","X<{X f1}|int>"); }
	@Test public void test_1384() { checkNotSubtype("{null f1}|null","X<{X f2}|int>"); }
	@Test public void test_1385() { checkIsSubtype("{null f1}|null","X<(X|null)|null>"); }
	@Test public void test_1386() { checkNotSubtype("{null f1}|null","X<(X|int)|int>"); }
	@Test public void test_1387() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_1388() { checkNotSubtype("{null f2}|null","int"); }
	@Test public void test_1389() { checkNotSubtype("{null f2}|null","{null f1}"); }
	@Test public void test_1390() { checkIsSubtype("{null f2}|null","{null f2}"); }
	@Test public void test_1391() { checkNotSubtype("{null f2}|null","{int f1}"); }
	@Test public void test_1392() { checkNotSubtype("{null f2}|null","{int f2}"); }
	@Test public void test_1393() { checkNotSubtype("{null f2}|null","{{null f1} f1}"); }
	@Test public void test_1394() { checkNotSubtype("{null f2}|null","{{null f2} f1}"); }
	@Test public void test_1395() { checkNotSubtype("{null f2}|null","{{null f1} f2}"); }
	@Test public void test_1396() { checkNotSubtype("{null f2}|null","{{null f2} f2}"); }
	@Test public void test_1397() { checkNotSubtype("{null f2}|null","{{int f1} f1}"); }
	@Test public void test_1398() { checkNotSubtype("{null f2}|null","{{int f2} f1}"); }
	@Test public void test_1399() { checkNotSubtype("{null f2}|null","{{int f1} f2}"); }
	@Test public void test_1400() { checkNotSubtype("{null f2}|null","{{int f2} f2}"); }
	@Test public void test_1401() { checkNotSubtype("{null f2}|null","X<{X|null f1}>"); }
	@Test public void test_1402() { checkNotSubtype("{null f2}|null","X<{X|null f2}>"); }
	@Test public void test_1403() { checkNotSubtype("{null f2}|null","X<{X|int f1}>"); }
	@Test public void test_1404() { checkNotSubtype("{null f2}|null","X<{X|int f2}>"); }
	@Test public void test_1405() { checkIsSubtype("{null f2}|null","null|null"); }
	@Test public void test_1406() { checkNotSubtype("{null f2}|null","null|int"); }
	@Test public void test_1407() { checkNotSubtype("{null f2}|null","null|{null f1}"); }
	@Test public void test_1408() { checkIsSubtype("{null f2}|null","null|{null f2}"); }
	@Test public void test_1409() { checkNotSubtype("{null f2}|null","X<null|{X f1}>"); }
	@Test public void test_1410() { checkNotSubtype("{null f2}|null","X<null|{X f2}>"); }
	@Test public void test_1411() { checkIsSubtype("{null f2}|null","X<null|(X|null)>"); }
	@Test public void test_1412() { checkNotSubtype("{null f2}|null","int|null"); }
	@Test public void test_1413() { checkNotSubtype("{null f2}|null","int|int"); }
	@Test public void test_1414() { checkNotSubtype("{null f2}|null","int|{int f1}"); }
	@Test public void test_1415() { checkNotSubtype("{null f2}|null","int|{int f2}"); }
	@Test public void test_1416() { checkNotSubtype("{null f2}|null","X<int|{X f1}>"); }
	@Test public void test_1417() { checkNotSubtype("{null f2}|null","X<int|{X f2}>"); }
	@Test public void test_1418() { checkNotSubtype("{null f2}|null","X<int|(X|int)>"); }
	@Test public void test_1419() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test public void test_1420() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test public void test_1421() { checkNotSubtype("{null f2}|null","X<{X f1}|null>"); }
	@Test public void test_1422() { checkNotSubtype("{null f2}|null","X<{X f2}|null>"); }
	@Test public void test_1423() { checkNotSubtype("{null f2}|null","{int f1}|int"); }
	@Test public void test_1424() { checkNotSubtype("{null f2}|null","{int f2}|int"); }
	@Test public void test_1425() { checkNotSubtype("{null f2}|null","X<{X f1}|int>"); }
	@Test public void test_1426() { checkNotSubtype("{null f2}|null","X<{X f2}|int>"); }
	@Test public void test_1427() { checkIsSubtype("{null f2}|null","X<(X|null)|null>"); }
	@Test public void test_1428() { checkNotSubtype("{null f2}|null","X<(X|int)|int>"); }
	@Test public void test_1429() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test public void test_1430() { checkNotSubtype("X<{X f1}|null>","int"); }
	@Test public void test_1431() { checkIsSubtype("X<{X f1}|null>","{null f1}"); }
	@Test public void test_1432() { checkNotSubtype("X<{X f1}|null>","{null f2}"); }
	@Test public void test_1433() { checkNotSubtype("X<{X f1}|null>","{int f1}"); }
	@Test public void test_1434() { checkNotSubtype("X<{X f1}|null>","{int f2}"); }
	@Test public void test_1435() { checkIsSubtype("X<{X f1}|null>","{{null f1} f1}"); }
	@Test public void test_1436() { checkNotSubtype("X<{X f1}|null>","{{null f2} f1}"); }
	@Test public void test_1437() { checkNotSubtype("X<{X f1}|null>","{{null f1} f2}"); }
	@Test public void test_1438() { checkNotSubtype("X<{X f1}|null>","{{null f2} f2}"); }
	@Test public void test_1439() { checkNotSubtype("X<{X f1}|null>","{{int f1} f1}"); }
	@Test public void test_1440() { checkNotSubtype("X<{X f1}|null>","{{int f2} f1}"); }
	@Test public void test_1441() { checkNotSubtype("X<{X f1}|null>","{{int f1} f2}"); }
	@Test public void test_1442() { checkNotSubtype("X<{X f1}|null>","{{int f2} f2}"); }
	@Test public void test_1443() { checkIsSubtype("X<{X f1}|null>","X<{X|null f1}>"); }
	@Test public void test_1444() { checkNotSubtype("X<{X f1}|null>","X<{X|null f2}>"); }
	@Test public void test_1445() { checkNotSubtype("X<{X f1}|null>","X<{X|int f1}>"); }
	@Test public void test_1446() { checkNotSubtype("X<{X f1}|null>","X<{X|int f2}>"); }
	@Test public void test_1447() { checkIsSubtype("X<{X f1}|null>","null|null"); }
	@Test public void test_1448() { checkNotSubtype("X<{X f1}|null>","null|int"); }
	@Test public void test_1449() { checkIsSubtype("X<{X f1}|null>","null|{null f1}"); }
	@Test public void test_1450() { checkNotSubtype("X<{X f1}|null>","null|{null f2}"); }
	@Test public void test_1451() { checkIsSubtype("X<{X f1}|null>","X<null|{X f1}>"); }
	@Test public void test_1452() { checkNotSubtype("X<{X f1}|null>","X<null|{X f2}>"); }
	@Test public void test_1453() { checkIsSubtype("X<{X f1}|null>","X<null|(X|null)>"); }
	@Test public void test_1454() { checkNotSubtype("X<{X f1}|null>","int|null"); }
	@Test public void test_1455() { checkNotSubtype("X<{X f1}|null>","int|int"); }
	@Test public void test_1456() { checkNotSubtype("X<{X f1}|null>","int|{int f1}"); }
	@Test public void test_1457() { checkNotSubtype("X<{X f1}|null>","int|{int f2}"); }
	@Test public void test_1458() { checkNotSubtype("X<{X f1}|null>","X<int|{X f1}>"); }
	@Test public void test_1459() { checkNotSubtype("X<{X f1}|null>","X<int|{X f2}>"); }
	@Test public void test_1460() { checkNotSubtype("X<{X f1}|null>","X<int|(X|int)>"); }
	@Test public void test_1461() { checkIsSubtype("X<{X f1}|null>","{null f1}|null"); }
	@Test public void test_1462() { checkNotSubtype("X<{X f1}|null>","{null f2}|null"); }
	@Test public void test_1463() { checkIsSubtype("X<{X f1}|null>","X<{X f1}|null>"); }
	@Test public void test_1464() { checkNotSubtype("X<{X f1}|null>","X<{X f2}|null>"); }
	@Test public void test_1465() { checkNotSubtype("X<{X f1}|null>","{int f1}|int"); }
	@Test public void test_1466() { checkNotSubtype("X<{X f1}|null>","{int f2}|int"); }
	@Test public void test_1467() { checkNotSubtype("X<{X f1}|null>","X<{X f1}|int>"); }
	@Test public void test_1468() { checkNotSubtype("X<{X f1}|null>","X<{X f2}|int>"); }
	@Test public void test_1469() { checkIsSubtype("X<{X f1}|null>","X<(X|null)|null>"); }
	@Test public void test_1470() { checkNotSubtype("X<{X f1}|null>","X<(X|int)|int>"); }
	@Test public void test_1471() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test public void test_1472() { checkNotSubtype("X<{X f2}|null>","int"); }
	@Test public void test_1473() { checkNotSubtype("X<{X f2}|null>","{null f1}"); }
	@Test public void test_1474() { checkIsSubtype("X<{X f2}|null>","{null f2}"); }
	@Test public void test_1475() { checkNotSubtype("X<{X f2}|null>","{int f1}"); }
	@Test public void test_1476() { checkNotSubtype("X<{X f2}|null>","{int f2}"); }
	@Test public void test_1477() { checkNotSubtype("X<{X f2}|null>","{{null f1} f1}"); }
	@Test public void test_1478() { checkNotSubtype("X<{X f2}|null>","{{null f2} f1}"); }
	@Test public void test_1479() { checkNotSubtype("X<{X f2}|null>","{{null f1} f2}"); }
	@Test public void test_1480() { checkIsSubtype("X<{X f2}|null>","{{null f2} f2}"); }
	@Test public void test_1481() { checkNotSubtype("X<{X f2}|null>","{{int f1} f1}"); }
	@Test public void test_1482() { checkNotSubtype("X<{X f2}|null>","{{int f2} f1}"); }
	@Test public void test_1483() { checkNotSubtype("X<{X f2}|null>","{{int f1} f2}"); }
	@Test public void test_1484() { checkNotSubtype("X<{X f2}|null>","{{int f2} f2}"); }
	@Test public void test_1485() { checkNotSubtype("X<{X f2}|null>","X<{X|null f1}>"); }
	@Test public void test_1486() { checkIsSubtype("X<{X f2}|null>","X<{X|null f2}>"); }
	@Test public void test_1487() { checkNotSubtype("X<{X f2}|null>","X<{X|int f1}>"); }
	@Test public void test_1488() { checkNotSubtype("X<{X f2}|null>","X<{X|int f2}>"); }
	@Test public void test_1489() { checkIsSubtype("X<{X f2}|null>","null|null"); }
	@Test public void test_1490() { checkNotSubtype("X<{X f2}|null>","null|int"); }
	@Test public void test_1491() { checkNotSubtype("X<{X f2}|null>","null|{null f1}"); }
	@Test public void test_1492() { checkIsSubtype("X<{X f2}|null>","null|{null f2}"); }
	@Test public void test_1493() { checkNotSubtype("X<{X f2}|null>","X<null|{X f1}>"); }
	@Test public void test_1494() { checkIsSubtype("X<{X f2}|null>","X<null|{X f2}>"); }
	@Test public void test_1495() { checkIsSubtype("X<{X f2}|null>","X<null|(X|null)>"); }
	@Test public void test_1496() { checkNotSubtype("X<{X f2}|null>","int|null"); }
	@Test public void test_1497() { checkNotSubtype("X<{X f2}|null>","int|int"); }
	@Test public void test_1498() { checkNotSubtype("X<{X f2}|null>","int|{int f1}"); }
	@Test public void test_1499() { checkNotSubtype("X<{X f2}|null>","int|{int f2}"); }
	@Test public void test_1500() { checkNotSubtype("X<{X f2}|null>","X<int|{X f1}>"); }
	@Test public void test_1501() { checkNotSubtype("X<{X f2}|null>","X<int|{X f2}>"); }
	@Test public void test_1502() { checkNotSubtype("X<{X f2}|null>","X<int|(X|int)>"); }
	@Test public void test_1503() { checkNotSubtype("X<{X f2}|null>","{null f1}|null"); }
	@Test public void test_1504() { checkIsSubtype("X<{X f2}|null>","{null f2}|null"); }
	@Test public void test_1505() { checkNotSubtype("X<{X f2}|null>","X<{X f1}|null>"); }
	@Test public void test_1506() { checkIsSubtype("X<{X f2}|null>","X<{X f2}|null>"); }
	@Test public void test_1507() { checkNotSubtype("X<{X f2}|null>","{int f1}|int"); }
	@Test public void test_1508() { checkNotSubtype("X<{X f2}|null>","{int f2}|int"); }
	@Test public void test_1509() { checkNotSubtype("X<{X f2}|null>","X<{X f1}|int>"); }
	@Test public void test_1510() { checkNotSubtype("X<{X f2}|null>","X<{X f2}|int>"); }
	@Test public void test_1511() { checkIsSubtype("X<{X f2}|null>","X<(X|null)|null>"); }
	@Test public void test_1512() { checkNotSubtype("X<{X f2}|null>","X<(X|int)|int>"); }
	@Test public void test_1513() { checkNotSubtype("{int f1}|int","null"); }
	@Test public void test_1514() { checkIsSubtype("{int f1}|int","int"); }
	@Test public void test_1515() { checkNotSubtype("{int f1}|int","{null f1}"); }
	@Test public void test_1516() { checkNotSubtype("{int f1}|int","{null f2}"); }
	@Test public void test_1517() { checkIsSubtype("{int f1}|int","{int f1}"); }
	@Test public void test_1518() { checkNotSubtype("{int f1}|int","{int f2}"); }
	@Test public void test_1519() { checkNotSubtype("{int f1}|int","{{null f1} f1}"); }
	@Test public void test_1520() { checkNotSubtype("{int f1}|int","{{null f2} f1}"); }
	@Test public void test_1521() { checkNotSubtype("{int f1}|int","{{null f1} f2}"); }
	@Test public void test_1522() { checkNotSubtype("{int f1}|int","{{null f2} f2}"); }
	@Test public void test_1523() { checkNotSubtype("{int f1}|int","{{int f1} f1}"); }
	@Test public void test_1524() { checkNotSubtype("{int f1}|int","{{int f2} f1}"); }
	@Test public void test_1525() { checkNotSubtype("{int f1}|int","{{int f1} f2}"); }
	@Test public void test_1526() { checkNotSubtype("{int f1}|int","{{int f2} f2}"); }
	@Test public void test_1527() { checkNotSubtype("{int f1}|int","X<{X|null f1}>"); }
	@Test public void test_1528() { checkNotSubtype("{int f1}|int","X<{X|null f2}>"); }
	@Test public void test_1529() { checkNotSubtype("{int f1}|int","X<{X|int f1}>"); }
	@Test public void test_1530() { checkNotSubtype("{int f1}|int","X<{X|int f2}>"); }
	@Test public void test_1531() { checkNotSubtype("{int f1}|int","null|null"); }
	@Test public void test_1532() { checkNotSubtype("{int f1}|int","null|int"); }
	@Test public void test_1533() { checkNotSubtype("{int f1}|int","null|{null f1}"); }
	@Test public void test_1534() { checkNotSubtype("{int f1}|int","null|{null f2}"); }
	@Test public void test_1535() { checkNotSubtype("{int f1}|int","X<null|{X f1}>"); }
	@Test public void test_1536() { checkNotSubtype("{int f1}|int","X<null|{X f2}>"); }
	@Test public void test_1537() { checkNotSubtype("{int f1}|int","X<null|(X|null)>"); }
	@Test public void test_1538() { checkNotSubtype("{int f1}|int","int|null"); }
	@Test public void test_1539() { checkIsSubtype("{int f1}|int","int|int"); }
	@Test public void test_1540() { checkIsSubtype("{int f1}|int","int|{int f1}"); }
	@Test public void test_1541() { checkNotSubtype("{int f1}|int","int|{int f2}"); }
	@Test public void test_1542() { checkNotSubtype("{int f1}|int","X<int|{X f1}>"); }
	@Test public void test_1543() { checkNotSubtype("{int f1}|int","X<int|{X f2}>"); }
	@Test public void test_1544() { checkIsSubtype("{int f1}|int","X<int|(X|int)>"); }
	@Test public void test_1545() { checkNotSubtype("{int f1}|int","{null f1}|null"); }
	@Test public void test_1546() { checkNotSubtype("{int f1}|int","{null f2}|null"); }
	@Test public void test_1547() { checkNotSubtype("{int f1}|int","X<{X f1}|null>"); }
	@Test public void test_1548() { checkNotSubtype("{int f1}|int","X<{X f2}|null>"); }
	@Test public void test_1549() { checkIsSubtype("{int f1}|int","{int f1}|int"); }
	@Test public void test_1550() { checkNotSubtype("{int f1}|int","{int f2}|int"); }
	@Test public void test_1551() { checkNotSubtype("{int f1}|int","X<{X f1}|int>"); }
	@Test public void test_1552() { checkNotSubtype("{int f1}|int","X<{X f2}|int>"); }
	@Test public void test_1553() { checkNotSubtype("{int f1}|int","X<(X|null)|null>"); }
	@Test public void test_1554() { checkIsSubtype("{int f1}|int","X<(X|int)|int>"); }
	@Test public void test_1555() { checkNotSubtype("{int f2}|int","null"); }
	@Test public void test_1556() { checkIsSubtype("{int f2}|int","int"); }
	@Test public void test_1557() { checkNotSubtype("{int f2}|int","{null f1}"); }
	@Test public void test_1558() { checkNotSubtype("{int f2}|int","{null f2}"); }
	@Test public void test_1559() { checkNotSubtype("{int f2}|int","{int f1}"); }
	@Test public void test_1560() { checkIsSubtype("{int f2}|int","{int f2}"); }
	@Test public void test_1561() { checkNotSubtype("{int f2}|int","{{null f1} f1}"); }
	@Test public void test_1562() { checkNotSubtype("{int f2}|int","{{null f2} f1}"); }
	@Test public void test_1563() { checkNotSubtype("{int f2}|int","{{null f1} f2}"); }
	@Test public void test_1564() { checkNotSubtype("{int f2}|int","{{null f2} f2}"); }
	@Test public void test_1565() { checkNotSubtype("{int f2}|int","{{int f1} f1}"); }
	@Test public void test_1566() { checkNotSubtype("{int f2}|int","{{int f2} f1}"); }
	@Test public void test_1567() { checkNotSubtype("{int f2}|int","{{int f1} f2}"); }
	@Test public void test_1568() { checkNotSubtype("{int f2}|int","{{int f2} f2}"); }
	@Test public void test_1569() { checkNotSubtype("{int f2}|int","X<{X|null f1}>"); }
	@Test public void test_1570() { checkNotSubtype("{int f2}|int","X<{X|null f2}>"); }
	@Test public void test_1571() { checkNotSubtype("{int f2}|int","X<{X|int f1}>"); }
	@Test public void test_1572() { checkNotSubtype("{int f2}|int","X<{X|int f2}>"); }
	@Test public void test_1573() { checkNotSubtype("{int f2}|int","null|null"); }
	@Test public void test_1574() { checkNotSubtype("{int f2}|int","null|int"); }
	@Test public void test_1575() { checkNotSubtype("{int f2}|int","null|{null f1}"); }
	@Test public void test_1576() { checkNotSubtype("{int f2}|int","null|{null f2}"); }
	@Test public void test_1577() { checkNotSubtype("{int f2}|int","X<null|{X f1}>"); }
	@Test public void test_1578() { checkNotSubtype("{int f2}|int","X<null|{X f2}>"); }
	@Test public void test_1579() { checkNotSubtype("{int f2}|int","X<null|(X|null)>"); }
	@Test public void test_1580() { checkNotSubtype("{int f2}|int","int|null"); }
	@Test public void test_1581() { checkIsSubtype("{int f2}|int","int|int"); }
	@Test public void test_1582() { checkNotSubtype("{int f2}|int","int|{int f1}"); }
	@Test public void test_1583() { checkIsSubtype("{int f2}|int","int|{int f2}"); }
	@Test public void test_1584() { checkNotSubtype("{int f2}|int","X<int|{X f1}>"); }
	@Test public void test_1585() { checkNotSubtype("{int f2}|int","X<int|{X f2}>"); }
	@Test public void test_1586() { checkIsSubtype("{int f2}|int","X<int|(X|int)>"); }
	@Test public void test_1587() { checkNotSubtype("{int f2}|int","{null f1}|null"); }
	@Test public void test_1588() { checkNotSubtype("{int f2}|int","{null f2}|null"); }
	@Test public void test_1589() { checkNotSubtype("{int f2}|int","X<{X f1}|null>"); }
	@Test public void test_1590() { checkNotSubtype("{int f2}|int","X<{X f2}|null>"); }
	@Test public void test_1591() { checkNotSubtype("{int f2}|int","{int f1}|int"); }
	@Test public void test_1592() { checkIsSubtype("{int f2}|int","{int f2}|int"); }
	@Test public void test_1593() { checkNotSubtype("{int f2}|int","X<{X f1}|int>"); }
	@Test public void test_1594() { checkNotSubtype("{int f2}|int","X<{X f2}|int>"); }
	@Test public void test_1595() { checkNotSubtype("{int f2}|int","X<(X|null)|null>"); }
	@Test public void test_1596() { checkIsSubtype("{int f2}|int","X<(X|int)|int>"); }
	@Test public void test_1597() { checkNotSubtype("X<{X f1}|int>","null"); }
	@Test public void test_1598() { checkIsSubtype("X<{X f1}|int>","int"); }
	@Test public void test_1599() { checkNotSubtype("X<{X f1}|int>","{null f1}"); }
	@Test public void test_1600() { checkNotSubtype("X<{X f1}|int>","{null f2}"); }
	@Test public void test_1601() { checkIsSubtype("X<{X f1}|int>","{int f1}"); }
	@Test public void test_1602() { checkNotSubtype("X<{X f1}|int>","{int f2}"); }
	@Test public void test_1603() { checkNotSubtype("X<{X f1}|int>","{{null f1} f1}"); }
	@Test public void test_1604() { checkNotSubtype("X<{X f1}|int>","{{null f2} f1}"); }
	@Test public void test_1605() { checkNotSubtype("X<{X f1}|int>","{{null f1} f2}"); }
	@Test public void test_1606() { checkNotSubtype("X<{X f1}|int>","{{null f2} f2}"); }
	@Test public void test_1607() { checkIsSubtype("X<{X f1}|int>","{{int f1} f1}"); }
	@Test public void test_1608() { checkNotSubtype("X<{X f1}|int>","{{int f2} f1}"); }
	@Test public void test_1609() { checkNotSubtype("X<{X f1}|int>","{{int f1} f2}"); }
	@Test public void test_1610() { checkNotSubtype("X<{X f1}|int>","{{int f2} f2}"); }
	@Test public void test_1611() { checkNotSubtype("X<{X f1}|int>","X<{X|null f1}>"); }
	@Test public void test_1612() { checkNotSubtype("X<{X f1}|int>","X<{X|null f2}>"); }
	@Test public void test_1613() { checkIsSubtype("X<{X f1}|int>","X<{X|int f1}>"); }
	@Test public void test_1614() { checkNotSubtype("X<{X f1}|int>","X<{X|int f2}>"); }
	@Test public void test_1615() { checkNotSubtype("X<{X f1}|int>","null|null"); }
	@Test public void test_1616() { checkNotSubtype("X<{X f1}|int>","null|int"); }
	@Test public void test_1617() { checkNotSubtype("X<{X f1}|int>","null|{null f1}"); }
	@Test public void test_1618() { checkNotSubtype("X<{X f1}|int>","null|{null f2}"); }
	@Test public void test_1619() { checkNotSubtype("X<{X f1}|int>","X<null|{X f1}>"); }
	@Test public void test_1620() { checkNotSubtype("X<{X f1}|int>","X<null|{X f2}>"); }
	@Test public void test_1621() { checkNotSubtype("X<{X f1}|int>","X<null|(X|null)>"); }
	@Test public void test_1622() { checkNotSubtype("X<{X f1}|int>","int|null"); }
	@Test public void test_1623() { checkIsSubtype("X<{X f1}|int>","int|int"); }
	@Test public void test_1624() { checkIsSubtype("X<{X f1}|int>","int|{int f1}"); }
	@Test public void test_1625() { checkNotSubtype("X<{X f1}|int>","int|{int f2}"); }
	@Test public void test_1626() { checkIsSubtype("X<{X f1}|int>","X<int|{X f1}>"); }
	@Test public void test_1627() { checkNotSubtype("X<{X f1}|int>","X<int|{X f2}>"); }
	@Test public void test_1628() { checkIsSubtype("X<{X f1}|int>","X<int|(X|int)>"); }
	@Test public void test_1629() { checkNotSubtype("X<{X f1}|int>","{null f1}|null"); }
	@Test public void test_1630() { checkNotSubtype("X<{X f1}|int>","{null f2}|null"); }
	@Test public void test_1631() { checkNotSubtype("X<{X f1}|int>","X<{X f1}|null>"); }
	@Test public void test_1632() { checkNotSubtype("X<{X f1}|int>","X<{X f2}|null>"); }
	@Test public void test_1633() { checkIsSubtype("X<{X f1}|int>","{int f1}|int"); }
	@Test public void test_1634() { checkNotSubtype("X<{X f1}|int>","{int f2}|int"); }
	@Test public void test_1635() { checkIsSubtype("X<{X f1}|int>","X<{X f1}|int>"); }
	@Test public void test_1636() { checkNotSubtype("X<{X f1}|int>","X<{X f2}|int>"); }
	@Test public void test_1637() { checkNotSubtype("X<{X f1}|int>","X<(X|null)|null>"); }
	@Test public void test_1638() { checkIsSubtype("X<{X f1}|int>","X<(X|int)|int>"); }
	@Test public void test_1639() { checkNotSubtype("X<{X f2}|int>","null"); }
	@Test public void test_1640() { checkIsSubtype("X<{X f2}|int>","int"); }
	@Test public void test_1641() { checkNotSubtype("X<{X f2}|int>","{null f1}"); }
	@Test public void test_1642() { checkNotSubtype("X<{X f2}|int>","{null f2}"); }
	@Test public void test_1643() { checkNotSubtype("X<{X f2}|int>","{int f1}"); }
	@Test public void test_1644() { checkIsSubtype("X<{X f2}|int>","{int f2}"); }
	@Test public void test_1645() { checkNotSubtype("X<{X f2}|int>","{{null f1} f1}"); }
	@Test public void test_1646() { checkNotSubtype("X<{X f2}|int>","{{null f2} f1}"); }
	@Test public void test_1647() { checkNotSubtype("X<{X f2}|int>","{{null f1} f2}"); }
	@Test public void test_1648() { checkNotSubtype("X<{X f2}|int>","{{null f2} f2}"); }
	@Test public void test_1649() { checkNotSubtype("X<{X f2}|int>","{{int f1} f1}"); }
	@Test public void test_1650() { checkNotSubtype("X<{X f2}|int>","{{int f2} f1}"); }
	@Test public void test_1651() { checkNotSubtype("X<{X f2}|int>","{{int f1} f2}"); }
	@Test public void test_1652() { checkIsSubtype("X<{X f2}|int>","{{int f2} f2}"); }
	@Test public void test_1653() { checkNotSubtype("X<{X f2}|int>","X<{X|null f1}>"); }
	@Test public void test_1654() { checkNotSubtype("X<{X f2}|int>","X<{X|null f2}>"); }
	@Test public void test_1655() { checkNotSubtype("X<{X f2}|int>","X<{X|int f1}>"); }
	@Test public void test_1656() { checkIsSubtype("X<{X f2}|int>","X<{X|int f2}>"); }
	@Test public void test_1657() { checkNotSubtype("X<{X f2}|int>","null|null"); }
	@Test public void test_1658() { checkNotSubtype("X<{X f2}|int>","null|int"); }
	@Test public void test_1659() { checkNotSubtype("X<{X f2}|int>","null|{null f1}"); }
	@Test public void test_1660() { checkNotSubtype("X<{X f2}|int>","null|{null f2}"); }
	@Test public void test_1661() { checkNotSubtype("X<{X f2}|int>","X<null|{X f1}>"); }
	@Test public void test_1662() { checkNotSubtype("X<{X f2}|int>","X<null|{X f2}>"); }
	@Test public void test_1663() { checkNotSubtype("X<{X f2}|int>","X<null|(X|null)>"); }
	@Test public void test_1664() { checkNotSubtype("X<{X f2}|int>","int|null"); }
	@Test public void test_1665() { checkIsSubtype("X<{X f2}|int>","int|int"); }
	@Test public void test_1666() { checkNotSubtype("X<{X f2}|int>","int|{int f1}"); }
	@Test public void test_1667() { checkIsSubtype("X<{X f2}|int>","int|{int f2}"); }
	@Test public void test_1668() { checkNotSubtype("X<{X f2}|int>","X<int|{X f1}>"); }
	@Test public void test_1669() { checkIsSubtype("X<{X f2}|int>","X<int|{X f2}>"); }
	@Test public void test_1670() { checkIsSubtype("X<{X f2}|int>","X<int|(X|int)>"); }
	@Test public void test_1671() { checkNotSubtype("X<{X f2}|int>","{null f1}|null"); }
	@Test public void test_1672() { checkNotSubtype("X<{X f2}|int>","{null f2}|null"); }
	@Test public void test_1673() { checkNotSubtype("X<{X f2}|int>","X<{X f1}|null>"); }
	@Test public void test_1674() { checkNotSubtype("X<{X f2}|int>","X<{X f2}|null>"); }
	@Test public void test_1675() { checkNotSubtype("X<{X f2}|int>","{int f1}|int"); }
	@Test public void test_1676() { checkIsSubtype("X<{X f2}|int>","{int f2}|int"); }
	@Test public void test_1677() { checkNotSubtype("X<{X f2}|int>","X<{X f1}|int>"); }
	@Test public void test_1678() { checkIsSubtype("X<{X f2}|int>","X<{X f2}|int>"); }
	@Test public void test_1679() { checkNotSubtype("X<{X f2}|int>","X<(X|null)|null>"); }
	@Test public void test_1680() { checkIsSubtype("X<{X f2}|int>","X<(X|int)|int>"); }
	@Test public void test_1681() { checkIsSubtype("X<(X|null)|null>","null"); }
	@Test public void test_1682() { checkNotSubtype("X<(X|null)|null>","int"); }
	@Test public void test_1683() { checkNotSubtype("X<(X|null)|null>","{null f1}"); }
	@Test public void test_1684() { checkNotSubtype("X<(X|null)|null>","{null f2}"); }
	@Test public void test_1685() { checkNotSubtype("X<(X|null)|null>","{int f1}"); }
	@Test public void test_1686() { checkNotSubtype("X<(X|null)|null>","{int f2}"); }
	@Test public void test_1687() { checkNotSubtype("X<(X|null)|null>","{{null f1} f1}"); }
	@Test public void test_1688() { checkNotSubtype("X<(X|null)|null>","{{null f2} f1}"); }
	@Test public void test_1689() { checkNotSubtype("X<(X|null)|null>","{{null f1} f2}"); }
	@Test public void test_1690() { checkNotSubtype("X<(X|null)|null>","{{null f2} f2}"); }
	@Test public void test_1691() { checkNotSubtype("X<(X|null)|null>","{{int f1} f1}"); }
	@Test public void test_1692() { checkNotSubtype("X<(X|null)|null>","{{int f2} f1}"); }
	@Test public void test_1693() { checkNotSubtype("X<(X|null)|null>","{{int f1} f2}"); }
	@Test public void test_1694() { checkNotSubtype("X<(X|null)|null>","{{int f2} f2}"); }
	@Test public void test_1695() { checkNotSubtype("X<(X|null)|null>","X<{X|null f1}>"); }
	@Test public void test_1696() { checkNotSubtype("X<(X|null)|null>","X<{X|null f2}>"); }
	@Test public void test_1697() { checkNotSubtype("X<(X|null)|null>","X<{X|int f1}>"); }
	@Test public void test_1698() { checkNotSubtype("X<(X|null)|null>","X<{X|int f2}>"); }
	@Test public void test_1699() { checkIsSubtype("X<(X|null)|null>","null|null"); }
	@Test public void test_1700() { checkNotSubtype("X<(X|null)|null>","null|int"); }
	@Test public void test_1701() { checkNotSubtype("X<(X|null)|null>","null|{null f1}"); }
	@Test public void test_1702() { checkNotSubtype("X<(X|null)|null>","null|{null f2}"); }
	@Test public void test_1703() { checkNotSubtype("X<(X|null)|null>","X<null|{X f1}>"); }
	@Test public void test_1704() { checkNotSubtype("X<(X|null)|null>","X<null|{X f2}>"); }
	@Test public void test_1705() { checkIsSubtype("X<(X|null)|null>","X<null|(X|null)>"); }
	@Test public void test_1706() { checkNotSubtype("X<(X|null)|null>","int|null"); }
	@Test public void test_1707() { checkNotSubtype("X<(X|null)|null>","int|int"); }
	@Test public void test_1708() { checkNotSubtype("X<(X|null)|null>","int|{int f1}"); }
	@Test public void test_1709() { checkNotSubtype("X<(X|null)|null>","int|{int f2}"); }
	@Test public void test_1710() { checkNotSubtype("X<(X|null)|null>","X<int|{X f1}>"); }
	@Test public void test_1711() { checkNotSubtype("X<(X|null)|null>","X<int|{X f2}>"); }
	@Test public void test_1712() { checkNotSubtype("X<(X|null)|null>","X<int|(X|int)>"); }
	@Test public void test_1713() { checkNotSubtype("X<(X|null)|null>","{null f1}|null"); }
	@Test public void test_1714() { checkNotSubtype("X<(X|null)|null>","{null f2}|null"); }
	@Test public void test_1715() { checkNotSubtype("X<(X|null)|null>","X<{X f1}|null>"); }
	@Test public void test_1716() { checkNotSubtype("X<(X|null)|null>","X<{X f2}|null>"); }
	@Test public void test_1717() { checkNotSubtype("X<(X|null)|null>","{int f1}|int"); }
	@Test public void test_1718() { checkNotSubtype("X<(X|null)|null>","{int f2}|int"); }
	@Test public void test_1719() { checkNotSubtype("X<(X|null)|null>","X<{X f1}|int>"); }
	@Test public void test_1720() { checkNotSubtype("X<(X|null)|null>","X<{X f2}|int>"); }
	@Test public void test_1721() { checkIsSubtype("X<(X|null)|null>","X<(X|null)|null>"); }
	@Test public void test_1722() { checkNotSubtype("X<(X|null)|null>","X<(X|int)|int>"); }
	@Test public void test_1723() { checkNotSubtype("X<(X|int)|int>","null"); }
	@Test public void test_1724() { checkIsSubtype("X<(X|int)|int>","int"); }
	@Test public void test_1725() { checkNotSubtype("X<(X|int)|int>","{null f1}"); }
	@Test public void test_1726() { checkNotSubtype("X<(X|int)|int>","{null f2}"); }
	@Test public void test_1727() { checkNotSubtype("X<(X|int)|int>","{int f1}"); }
	@Test public void test_1728() { checkNotSubtype("X<(X|int)|int>","{int f2}"); }
	@Test public void test_1729() { checkNotSubtype("X<(X|int)|int>","{{null f1} f1}"); }
	@Test public void test_1730() { checkNotSubtype("X<(X|int)|int>","{{null f2} f1}"); }
	@Test public void test_1731() { checkNotSubtype("X<(X|int)|int>","{{null f1} f2}"); }
	@Test public void test_1732() { checkNotSubtype("X<(X|int)|int>","{{null f2} f2}"); }
	@Test public void test_1733() { checkNotSubtype("X<(X|int)|int>","{{int f1} f1}"); }
	@Test public void test_1734() { checkNotSubtype("X<(X|int)|int>","{{int f2} f1}"); }
	@Test public void test_1735() { checkNotSubtype("X<(X|int)|int>","{{int f1} f2}"); }
	@Test public void test_1736() { checkNotSubtype("X<(X|int)|int>","{{int f2} f2}"); }
	@Test public void test_1737() { checkNotSubtype("X<(X|int)|int>","X<{X|null f1}>"); }
	@Test public void test_1738() { checkNotSubtype("X<(X|int)|int>","X<{X|null f2}>"); }
	@Test public void test_1739() { checkNotSubtype("X<(X|int)|int>","X<{X|int f1}>"); }
	@Test public void test_1740() { checkNotSubtype("X<(X|int)|int>","X<{X|int f2}>"); }
	@Test public void test_1741() { checkNotSubtype("X<(X|int)|int>","null|null"); }
	@Test public void test_1742() { checkNotSubtype("X<(X|int)|int>","null|int"); }
	@Test public void test_1743() { checkNotSubtype("X<(X|int)|int>","null|{null f1}"); }
	@Test public void test_1744() { checkNotSubtype("X<(X|int)|int>","null|{null f2}"); }
	@Test public void test_1745() { checkNotSubtype("X<(X|int)|int>","X<null|{X f1}>"); }
	@Test public void test_1746() { checkNotSubtype("X<(X|int)|int>","X<null|{X f2}>"); }
	@Test public void test_1747() { checkNotSubtype("X<(X|int)|int>","X<null|(X|null)>"); }
	@Test public void test_1748() { checkNotSubtype("X<(X|int)|int>","int|null"); }
	@Test public void test_1749() { checkIsSubtype("X<(X|int)|int>","int|int"); }
	@Test public void test_1750() { checkNotSubtype("X<(X|int)|int>","int|{int f1}"); }
	@Test public void test_1751() { checkNotSubtype("X<(X|int)|int>","int|{int f2}"); }
	@Test public void test_1752() { checkNotSubtype("X<(X|int)|int>","X<int|{X f1}>"); }
	@Test public void test_1753() { checkNotSubtype("X<(X|int)|int>","X<int|{X f2}>"); }
	@Test public void test_1754() { checkIsSubtype("X<(X|int)|int>","X<int|(X|int)>"); }
	@Test public void test_1755() { checkNotSubtype("X<(X|int)|int>","{null f1}|null"); }
	@Test public void test_1756() { checkNotSubtype("X<(X|int)|int>","{null f2}|null"); }
	@Test public void test_1757() { checkNotSubtype("X<(X|int)|int>","X<{X f1}|null>"); }
	@Test public void test_1758() { checkNotSubtype("X<(X|int)|int>","X<{X f2}|null>"); }
	@Test public void test_1759() { checkNotSubtype("X<(X|int)|int>","{int f1}|int"); }
	@Test public void test_1760() { checkNotSubtype("X<(X|int)|int>","{int f2}|int"); }
	@Test public void test_1761() { checkNotSubtype("X<(X|int)|int>","X<{X f1}|int>"); }
	@Test public void test_1762() { checkNotSubtype("X<(X|int)|int>","X<{X f2}|int>"); }
	@Test public void test_1763() { checkNotSubtype("X<(X|int)|int>","X<(X|null)|null>"); }
	@Test public void test_1764() { checkIsSubtype("X<(X|int)|int>","X<(X|int)|int>"); }

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
