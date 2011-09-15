// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class RecursiveSubtypeTests {
	@Test public void test_1() { checkIsSubtype("any","any"); }
	@Test public void test_2() { checkIsSubtype("any","null"); }
	@Test public void test_3() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_4() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_5() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_6() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_7() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_8() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_9() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_10() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_11() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_12() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_13() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_14() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_15() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_16() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_17() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_18() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_19() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_20() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_21() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_22() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_23() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_24() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_25() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_26() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_27() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_28() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_29() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_30() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_31() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_32() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_33() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_34() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_35() { checkIsSubtype("any","any"); }
	@Test public void test_36() { checkIsSubtype("any","any"); }
	@Test public void test_37() { checkIsSubtype("any","any"); }
	@Test public void test_38() { checkIsSubtype("any","any"); }
	@Test public void test_39() { checkIsSubtype("any","any"); }
	@Test public void test_40() { checkIsSubtype("any","any"); }
	@Test public void test_41() { checkIsSubtype("any","any"); }
	@Test public void test_42() { checkIsSubtype("any","any"); }
	@Test public void test_43() { checkIsSubtype("any","any"); }
	@Test public void test_44() { checkIsSubtype("any","null"); }
	@Test public void test_45() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_46() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_47() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_48() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_49() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_50() { checkIsSubtype("any","null"); }
	@Test public void test_51() { checkIsSubtype("any","any"); }
	@Test public void test_52() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_53() { checkIsSubtype("any","any"); }
	@Test public void test_54() { checkIsSubtype("any","any"); }
	@Test public void test_55() { checkIsSubtype("any","any"); }
	@Test public void test_56() { checkIsSubtype("any","any"); }
	@Test public void test_57() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_58() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_59() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_60() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_61() { checkIsSubtype("any","any"); }
	@Test public void test_62() { checkIsSubtype("any","null"); }
	@Test public void test_63() { checkNotSubtype("null","any"); }
	@Test public void test_64() { checkIsSubtype("null","null"); }
	@Test public void test_65() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_66() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_67() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_68() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_69() { checkNotSubtype("null","(any,any)"); }
	@Test public void test_70() { checkNotSubtype("null","(any,null)"); }
	@Test public void test_71() { checkIsSubtype("null","(any,{any f1})"); }
	@Test public void test_72() { checkIsSubtype("null","(any,{any f2})"); }
	@Test public void test_73() { checkNotSubtype("null","X<(any,X|any)>"); }
	@Test public void test_74() { checkNotSubtype("null","(null,any)"); }
	@Test public void test_75() { checkNotSubtype("null","(null,null)"); }
	@Test public void test_76() { checkIsSubtype("null","(null,{null f1})"); }
	@Test public void test_77() { checkIsSubtype("null","(null,{null f2})"); }
	@Test public void test_78() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test public void test_79() { checkNotSubtype("null","({any f1},any)"); }
	@Test public void test_80() { checkNotSubtype("null","({any f2},any)"); }
	@Test public void test_81() { checkNotSubtype("null","({null f1},null)"); }
	@Test public void test_82() { checkNotSubtype("null","({null f2},null)"); }
	@Test public void test_83() { checkNotSubtype("null","X<(X|any,any)>"); }
	@Test public void test_84() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test public void test_85() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_86() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_87() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_88() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_89() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_90() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_91() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_92() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_93() { checkNotSubtype("null","X<{X|any f1}>"); }
	@Test public void test_94() { checkNotSubtype("null","X<{X|any f2}>"); }
	@Test public void test_95() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test public void test_96() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test public void test_97() { checkNotSubtype("null","any"); }
	@Test public void test_98() { checkNotSubtype("null","any"); }
	@Test public void test_99() { checkNotSubtype("null","any"); }
	@Test public void test_100() { checkNotSubtype("null","any"); }
	@Test public void test_101() { checkNotSubtype("null","any"); }
	@Test public void test_102() { checkNotSubtype("null","any"); }
	@Test public void test_103() { checkNotSubtype("null","any"); }
	@Test public void test_104() { checkNotSubtype("null","any"); }
	@Test public void test_105() { checkNotSubtype("null","any"); }
	@Test public void test_106() { checkIsSubtype("null","null"); }
	@Test public void test_107() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test public void test_108() { checkNotSubtype("null","null|{null f1}"); }
	@Test public void test_109() { checkNotSubtype("null","null|{null f2}"); }
	@Test public void test_110() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test public void test_111() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test public void test_112() { checkIsSubtype("null","null"); }
	@Test public void test_113() { checkNotSubtype("null","any"); }
	@Test public void test_114() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test public void test_115() { checkNotSubtype("null","any"); }
	@Test public void test_116() { checkNotSubtype("null","any"); }
	@Test public void test_117() { checkNotSubtype("null","any"); }
	@Test public void test_118() { checkNotSubtype("null","any"); }
	@Test public void test_119() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_120() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_121() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test public void test_122() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test public void test_123() { checkNotSubtype("null","any"); }
	@Test public void test_124() { checkIsSubtype("null","null"); }
	@Test public void test_125() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_126() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_127() { checkIsSubtype("{any f1}","{any f1}"); }
	@Test public void test_128() { checkNotSubtype("{any f1}","{any f2}"); }
	@Test public void test_129() { checkIsSubtype("{any f1}","{null f1}"); }
	@Test public void test_130() { checkNotSubtype("{any f1}","{null f2}"); }
	@Test public void test_131() { checkNotSubtype("{any f1}","(any,any)"); }
	@Test public void test_132() { checkNotSubtype("{any f1}","(any,null)"); }
	@Test public void test_133() { checkIsSubtype("{any f1}","(any,{any f1})"); }
	@Test public void test_134() { checkIsSubtype("{any f1}","(any,{any f2})"); }
	@Test public void test_135() { checkNotSubtype("{any f1}","X<(any,X|any)>"); }
	@Test public void test_136() { checkNotSubtype("{any f1}","(null,any)"); }
	@Test public void test_137() { checkNotSubtype("{any f1}","(null,null)"); }
	@Test public void test_138() { checkIsSubtype("{any f1}","(null,{null f1})"); }
	@Test public void test_139() { checkIsSubtype("{any f1}","(null,{null f2})"); }
	@Test public void test_140() { checkNotSubtype("{any f1}","X<(null,X|null)>"); }
	@Test public void test_141() { checkNotSubtype("{any f1}","({any f1},any)"); }
	@Test public void test_142() { checkNotSubtype("{any f1}","({any f2},any)"); }
	@Test public void test_143() { checkNotSubtype("{any f1}","({null f1},null)"); }
	@Test public void test_144() { checkNotSubtype("{any f1}","({null f2},null)"); }
	@Test public void test_145() { checkNotSubtype("{any f1}","X<(X|any,any)>"); }
	@Test public void test_146() { checkNotSubtype("{any f1}","X<(X|null,null)>"); }
	@Test public void test_147() { checkIsSubtype("{any f1}","{{any f1} f1}"); }
	@Test public void test_148() { checkIsSubtype("{any f1}","{{any f2} f1}"); }
	@Test public void test_149() { checkNotSubtype("{any f1}","{{any f1} f2}"); }
	@Test public void test_150() { checkNotSubtype("{any f1}","{{any f2} f2}"); }
	@Test public void test_151() { checkIsSubtype("{any f1}","{{null f1} f1}"); }
	@Test public void test_152() { checkIsSubtype("{any f1}","{{null f2} f1}"); }
	@Test public void test_153() { checkNotSubtype("{any f1}","{{null f1} f2}"); }
	@Test public void test_154() { checkNotSubtype("{any f1}","{{null f2} f2}"); }
	@Test public void test_155() { checkIsSubtype("{any f1}","X<{X|any f1}>"); }
	@Test public void test_156() { checkNotSubtype("{any f1}","X<{X|any f2}>"); }
	@Test public void test_157() { checkIsSubtype("{any f1}","X<{X|null f1}>"); }
	@Test public void test_158() { checkNotSubtype("{any f1}","X<{X|null f2}>"); }
	@Test public void test_159() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_160() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_161() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_162() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_163() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_164() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_165() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_166() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_167() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_168() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_169() { checkNotSubtype("{any f1}","X<null|(X,null)>"); }
	@Test public void test_170() { checkNotSubtype("{any f1}","null|{null f1}"); }
	@Test public void test_171() { checkNotSubtype("{any f1}","null|{null f2}"); }
	@Test public void test_172() { checkNotSubtype("{any f1}","X<null|{X f1}>"); }
	@Test public void test_173() { checkNotSubtype("{any f1}","X<null|{X f2}>"); }
	@Test public void test_174() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_175() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_176() { checkNotSubtype("{any f1}","X<(X,null)|null>"); }
	@Test public void test_177() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_178() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_179() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_180() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_181() { checkNotSubtype("{any f1}","{null f1}|null"); }
	@Test public void test_182() { checkNotSubtype("{any f1}","{null f2}|null"); }
	@Test public void test_183() { checkNotSubtype("{any f1}","X<{X f1}|null>"); }
	@Test public void test_184() { checkNotSubtype("{any f1}","X<{X f2}|null>"); }
	@Test public void test_185() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_186() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_187() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_188() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_189() { checkNotSubtype("{any f2}","{any f1}"); }
	@Test public void test_190() { checkIsSubtype("{any f2}","{any f2}"); }
	@Test public void test_191() { checkNotSubtype("{any f2}","{null f1}"); }
	@Test public void test_192() { checkIsSubtype("{any f2}","{null f2}"); }
	@Test public void test_193() { checkNotSubtype("{any f2}","(any,any)"); }
	@Test public void test_194() { checkNotSubtype("{any f2}","(any,null)"); }
	@Test public void test_195() { checkIsSubtype("{any f2}","(any,{any f1})"); }
	@Test public void test_196() { checkIsSubtype("{any f2}","(any,{any f2})"); }
	@Test public void test_197() { checkNotSubtype("{any f2}","X<(any,X|any)>"); }
	@Test public void test_198() { checkNotSubtype("{any f2}","(null,any)"); }
	@Test public void test_199() { checkNotSubtype("{any f2}","(null,null)"); }
	@Test public void test_200() { checkIsSubtype("{any f2}","(null,{null f1})"); }
	@Test public void test_201() { checkIsSubtype("{any f2}","(null,{null f2})"); }
	@Test public void test_202() { checkNotSubtype("{any f2}","X<(null,X|null)>"); }
	@Test public void test_203() { checkNotSubtype("{any f2}","({any f1},any)"); }
	@Test public void test_204() { checkNotSubtype("{any f2}","({any f2},any)"); }
	@Test public void test_205() { checkNotSubtype("{any f2}","({null f1},null)"); }
	@Test public void test_206() { checkNotSubtype("{any f2}","({null f2},null)"); }
	@Test public void test_207() { checkNotSubtype("{any f2}","X<(X|any,any)>"); }
	@Test public void test_208() { checkNotSubtype("{any f2}","X<(X|null,null)>"); }
	@Test public void test_209() { checkNotSubtype("{any f2}","{{any f1} f1}"); }
	@Test public void test_210() { checkNotSubtype("{any f2}","{{any f2} f1}"); }
	@Test public void test_211() { checkIsSubtype("{any f2}","{{any f1} f2}"); }
	@Test public void test_212() { checkIsSubtype("{any f2}","{{any f2} f2}"); }
	@Test public void test_213() { checkNotSubtype("{any f2}","{{null f1} f1}"); }
	@Test public void test_214() { checkNotSubtype("{any f2}","{{null f2} f1}"); }
	@Test public void test_215() { checkIsSubtype("{any f2}","{{null f1} f2}"); }
	@Test public void test_216() { checkIsSubtype("{any f2}","{{null f2} f2}"); }
	@Test public void test_217() { checkNotSubtype("{any f2}","X<{X|any f1}>"); }
	@Test public void test_218() { checkIsSubtype("{any f2}","X<{X|any f2}>"); }
	@Test public void test_219() { checkNotSubtype("{any f2}","X<{X|null f1}>"); }
	@Test public void test_220() { checkIsSubtype("{any f2}","X<{X|null f2}>"); }
	@Test public void test_221() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_222() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_223() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_224() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_225() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_226() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_227() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_228() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_229() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_230() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_231() { checkNotSubtype("{any f2}","X<null|(X,null)>"); }
	@Test public void test_232() { checkNotSubtype("{any f2}","null|{null f1}"); }
	@Test public void test_233() { checkNotSubtype("{any f2}","null|{null f2}"); }
	@Test public void test_234() { checkNotSubtype("{any f2}","X<null|{X f1}>"); }
	@Test public void test_235() { checkNotSubtype("{any f2}","X<null|{X f2}>"); }
	@Test public void test_236() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_237() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_238() { checkNotSubtype("{any f2}","X<(X,null)|null>"); }
	@Test public void test_239() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_240() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_241() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_242() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_243() { checkNotSubtype("{any f2}","{null f1}|null"); }
	@Test public void test_244() { checkNotSubtype("{any f2}","{null f2}|null"); }
	@Test public void test_245() { checkNotSubtype("{any f2}","X<{X f1}|null>"); }
	@Test public void test_246() { checkNotSubtype("{any f2}","X<{X f2}|null>"); }
	@Test public void test_247() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_248() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_249() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_250() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_251() { checkNotSubtype("{null f1}","{any f1}"); }
	@Test public void test_252() { checkNotSubtype("{null f1}","{any f2}"); }
	@Test public void test_253() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_254() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_255() { checkNotSubtype("{null f1}","(any,any)"); }
	@Test public void test_256() { checkNotSubtype("{null f1}","(any,null)"); }
	@Test public void test_257() { checkIsSubtype("{null f1}","(any,{any f1})"); }
	@Test public void test_258() { checkIsSubtype("{null f1}","(any,{any f2})"); }
	@Test public void test_259() { checkNotSubtype("{null f1}","X<(any,X|any)>"); }
	@Test public void test_260() { checkNotSubtype("{null f1}","(null,any)"); }
	@Test public void test_261() { checkNotSubtype("{null f1}","(null,null)"); }
	@Test public void test_262() { checkIsSubtype("{null f1}","(null,{null f1})"); }
	@Test public void test_263() { checkIsSubtype("{null f1}","(null,{null f2})"); }
	@Test public void test_264() { checkNotSubtype("{null f1}","X<(null,X|null)>"); }
	@Test public void test_265() { checkNotSubtype("{null f1}","({any f1},any)"); }
	@Test public void test_266() { checkNotSubtype("{null f1}","({any f2},any)"); }
	@Test public void test_267() { checkNotSubtype("{null f1}","({null f1},null)"); }
	@Test public void test_268() { checkNotSubtype("{null f1}","({null f2},null)"); }
	@Test public void test_269() { checkNotSubtype("{null f1}","X<(X|any,any)>"); }
	@Test public void test_270() { checkNotSubtype("{null f1}","X<(X|null,null)>"); }
	@Test public void test_271() { checkNotSubtype("{null f1}","{{any f1} f1}"); }
	@Test public void test_272() { checkNotSubtype("{null f1}","{{any f2} f1}"); }
	@Test public void test_273() { checkNotSubtype("{null f1}","{{any f1} f2}"); }
	@Test public void test_274() { checkNotSubtype("{null f1}","{{any f2} f2}"); }
	@Test public void test_275() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test public void test_276() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test public void test_277() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test public void test_278() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test public void test_279() { checkNotSubtype("{null f1}","X<{X|any f1}>"); }
	@Test public void test_280() { checkNotSubtype("{null f1}","X<{X|any f2}>"); }
	@Test public void test_281() { checkNotSubtype("{null f1}","X<{X|null f1}>"); }
	@Test public void test_282() { checkNotSubtype("{null f1}","X<{X|null f2}>"); }
	@Test public void test_283() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_284() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_285() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_286() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_287() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_288() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_289() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_290() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_291() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_292() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_293() { checkNotSubtype("{null f1}","X<null|(X,null)>"); }
	@Test public void test_294() { checkNotSubtype("{null f1}","null|{null f1}"); }
	@Test public void test_295() { checkNotSubtype("{null f1}","null|{null f2}"); }
	@Test public void test_296() { checkNotSubtype("{null f1}","X<null|{X f1}>"); }
	@Test public void test_297() { checkNotSubtype("{null f1}","X<null|{X f2}>"); }
	@Test public void test_298() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_299() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_300() { checkNotSubtype("{null f1}","X<(X,null)|null>"); }
	@Test public void test_301() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_302() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_303() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_304() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_305() { checkNotSubtype("{null f1}","{null f1}|null"); }
	@Test public void test_306() { checkNotSubtype("{null f1}","{null f2}|null"); }
	@Test public void test_307() { checkNotSubtype("{null f1}","X<{X f1}|null>"); }
	@Test public void test_308() { checkNotSubtype("{null f1}","X<{X f2}|null>"); }
	@Test public void test_309() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_310() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_311() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_312() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_313() { checkNotSubtype("{null f2}","{any f1}"); }
	@Test public void test_314() { checkNotSubtype("{null f2}","{any f2}"); }
	@Test public void test_315() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_316() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_317() { checkNotSubtype("{null f2}","(any,any)"); }
	@Test public void test_318() { checkNotSubtype("{null f2}","(any,null)"); }
	@Test public void test_319() { checkIsSubtype("{null f2}","(any,{any f1})"); }
	@Test public void test_320() { checkIsSubtype("{null f2}","(any,{any f2})"); }
	@Test public void test_321() { checkNotSubtype("{null f2}","X<(any,X|any)>"); }
	@Test public void test_322() { checkNotSubtype("{null f2}","(null,any)"); }
	@Test public void test_323() { checkNotSubtype("{null f2}","(null,null)"); }
	@Test public void test_324() { checkIsSubtype("{null f2}","(null,{null f1})"); }
	@Test public void test_325() { checkIsSubtype("{null f2}","(null,{null f2})"); }
	@Test public void test_326() { checkNotSubtype("{null f2}","X<(null,X|null)>"); }
	@Test public void test_327() { checkNotSubtype("{null f2}","({any f1},any)"); }
	@Test public void test_328() { checkNotSubtype("{null f2}","({any f2},any)"); }
	@Test public void test_329() { checkNotSubtype("{null f2}","({null f1},null)"); }
	@Test public void test_330() { checkNotSubtype("{null f2}","({null f2},null)"); }
	@Test public void test_331() { checkNotSubtype("{null f2}","X<(X|any,any)>"); }
	@Test public void test_332() { checkNotSubtype("{null f2}","X<(X|null,null)>"); }
	@Test public void test_333() { checkNotSubtype("{null f2}","{{any f1} f1}"); }
	@Test public void test_334() { checkNotSubtype("{null f2}","{{any f2} f1}"); }
	@Test public void test_335() { checkNotSubtype("{null f2}","{{any f1} f2}"); }
	@Test public void test_336() { checkNotSubtype("{null f2}","{{any f2} f2}"); }
	@Test public void test_337() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test public void test_338() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test public void test_339() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test public void test_340() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test public void test_341() { checkNotSubtype("{null f2}","X<{X|any f1}>"); }
	@Test public void test_342() { checkNotSubtype("{null f2}","X<{X|any f2}>"); }
	@Test public void test_343() { checkNotSubtype("{null f2}","X<{X|null f1}>"); }
	@Test public void test_344() { checkNotSubtype("{null f2}","X<{X|null f2}>"); }
	@Test public void test_345() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_346() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_347() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_348() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_349() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_350() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_351() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_352() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_353() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_354() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_355() { checkNotSubtype("{null f2}","X<null|(X,null)>"); }
	@Test public void test_356() { checkNotSubtype("{null f2}","null|{null f1}"); }
	@Test public void test_357() { checkNotSubtype("{null f2}","null|{null f2}"); }
	@Test public void test_358() { checkNotSubtype("{null f2}","X<null|{X f1}>"); }
	@Test public void test_359() { checkNotSubtype("{null f2}","X<null|{X f2}>"); }
	@Test public void test_360() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_361() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_362() { checkNotSubtype("{null f2}","X<(X,null)|null>"); }
	@Test public void test_363() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_364() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_365() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_366() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_367() { checkNotSubtype("{null f2}","{null f1}|null"); }
	@Test public void test_368() { checkNotSubtype("{null f2}","{null f2}|null"); }
	@Test public void test_369() { checkNotSubtype("{null f2}","X<{X f1}|null>"); }
	@Test public void test_370() { checkNotSubtype("{null f2}","X<{X f2}|null>"); }
	@Test public void test_371() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_372() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_373() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_374() { checkNotSubtype("(any,any)","null"); }
	@Test public void test_375() { checkNotSubtype("(any,any)","{any f1}"); }
	@Test public void test_376() { checkNotSubtype("(any,any)","{any f2}"); }
	@Test public void test_377() { checkNotSubtype("(any,any)","{null f1}"); }
	@Test public void test_378() { checkNotSubtype("(any,any)","{null f2}"); }
	@Test public void test_379() { checkIsSubtype("(any,any)","(any,any)"); }
	@Test public void test_380() { checkIsSubtype("(any,any)","(any,null)"); }
	@Test public void test_381() { checkIsSubtype("(any,any)","(any,{any f1})"); }
	@Test public void test_382() { checkIsSubtype("(any,any)","(any,{any f2})"); }
	@Test public void test_383() { checkIsSubtype("(any,any)","X<(any,X|any)>"); }
	@Test public void test_384() { checkIsSubtype("(any,any)","(null,any)"); }
	@Test public void test_385() { checkIsSubtype("(any,any)","(null,null)"); }
	@Test public void test_386() { checkIsSubtype("(any,any)","(null,{null f1})"); }
	@Test public void test_387() { checkIsSubtype("(any,any)","(null,{null f2})"); }
	@Test public void test_388() { checkIsSubtype("(any,any)","X<(null,X|null)>"); }
	@Test public void test_389() { checkIsSubtype("(any,any)","({any f1},any)"); }
	@Test public void test_390() { checkIsSubtype("(any,any)","({any f2},any)"); }
	@Test public void test_391() { checkIsSubtype("(any,any)","({null f1},null)"); }
	@Test public void test_392() { checkIsSubtype("(any,any)","({null f2},null)"); }
	@Test public void test_393() { checkIsSubtype("(any,any)","X<(X|any,any)>"); }
	@Test public void test_394() { checkIsSubtype("(any,any)","X<(X|null,null)>"); }
	@Test public void test_395() { checkNotSubtype("(any,any)","{{any f1} f1}"); }
	@Test public void test_396() { checkNotSubtype("(any,any)","{{any f2} f1}"); }
	@Test public void test_397() { checkNotSubtype("(any,any)","{{any f1} f2}"); }
	@Test public void test_398() { checkNotSubtype("(any,any)","{{any f2} f2}"); }
	@Test public void test_399() { checkNotSubtype("(any,any)","{{null f1} f1}"); }
	@Test public void test_400() { checkNotSubtype("(any,any)","{{null f2} f1}"); }
	@Test public void test_401() { checkNotSubtype("(any,any)","{{null f1} f2}"); }
	@Test public void test_402() { checkNotSubtype("(any,any)","{{null f2} f2}"); }
	@Test public void test_403() { checkNotSubtype("(any,any)","X<{X|any f1}>"); }
	@Test public void test_404() { checkNotSubtype("(any,any)","X<{X|any f2}>"); }
	@Test public void test_405() { checkNotSubtype("(any,any)","X<{X|null f1}>"); }
	@Test public void test_406() { checkNotSubtype("(any,any)","X<{X|null f2}>"); }
	@Test public void test_407() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_408() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_409() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_410() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_411() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_412() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_413() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_414() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_415() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_416() { checkNotSubtype("(any,any)","null"); }
	@Test public void test_417() { checkNotSubtype("(any,any)","X<null|(X,null)>"); }
	@Test public void test_418() { checkNotSubtype("(any,any)","null|{null f1}"); }
	@Test public void test_419() { checkNotSubtype("(any,any)","null|{null f2}"); }
	@Test public void test_420() { checkNotSubtype("(any,any)","X<null|{X f1}>"); }
	@Test public void test_421() { checkNotSubtype("(any,any)","X<null|{X f2}>"); }
	@Test public void test_422() { checkNotSubtype("(any,any)","null"); }
	@Test public void test_423() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_424() { checkNotSubtype("(any,any)","X<(X,null)|null>"); }
	@Test public void test_425() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_426() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_427() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_428() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_429() { checkNotSubtype("(any,any)","{null f1}|null"); }
	@Test public void test_430() { checkNotSubtype("(any,any)","{null f2}|null"); }
	@Test public void test_431() { checkNotSubtype("(any,any)","X<{X f1}|null>"); }
	@Test public void test_432() { checkNotSubtype("(any,any)","X<{X f2}|null>"); }
	@Test public void test_433() { checkNotSubtype("(any,any)","any"); }
	@Test public void test_434() { checkNotSubtype("(any,any)","null"); }
	@Test public void test_435() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_436() { checkNotSubtype("(any,null)","null"); }
	@Test public void test_437() { checkNotSubtype("(any,null)","{any f1}"); }
	@Test public void test_438() { checkNotSubtype("(any,null)","{any f2}"); }
	@Test public void test_439() { checkNotSubtype("(any,null)","{null f1}"); }
	@Test public void test_440() { checkNotSubtype("(any,null)","{null f2}"); }
	@Test public void test_441() { checkNotSubtype("(any,null)","(any,any)"); }
	@Test public void test_442() { checkIsSubtype("(any,null)","(any,null)"); }
	@Test public void test_443() { checkIsSubtype("(any,null)","(any,{any f1})"); }
	@Test public void test_444() { checkIsSubtype("(any,null)","(any,{any f2})"); }
	@Test public void test_445() { checkNotSubtype("(any,null)","X<(any,X|any)>"); }
	@Test public void test_446() { checkNotSubtype("(any,null)","(null,any)"); }
	@Test public void test_447() { checkIsSubtype("(any,null)","(null,null)"); }
	@Test public void test_448() { checkIsSubtype("(any,null)","(null,{null f1})"); }
	@Test public void test_449() { checkIsSubtype("(any,null)","(null,{null f2})"); }
	@Test public void test_450() { checkIsSubtype("(any,null)","X<(null,X|null)>"); }
	@Test public void test_451() { checkNotSubtype("(any,null)","({any f1},any)"); }
	@Test public void test_452() { checkNotSubtype("(any,null)","({any f2},any)"); }
	@Test public void test_453() { checkIsSubtype("(any,null)","({null f1},null)"); }
	@Test public void test_454() { checkIsSubtype("(any,null)","({null f2},null)"); }
	@Test public void test_455() { checkNotSubtype("(any,null)","X<(X|any,any)>"); }
	@Test public void test_456() { checkIsSubtype("(any,null)","X<(X|null,null)>"); }
	@Test public void test_457() { checkNotSubtype("(any,null)","{{any f1} f1}"); }
	@Test public void test_458() { checkNotSubtype("(any,null)","{{any f2} f1}"); }
	@Test public void test_459() { checkNotSubtype("(any,null)","{{any f1} f2}"); }
	@Test public void test_460() { checkNotSubtype("(any,null)","{{any f2} f2}"); }
	@Test public void test_461() { checkNotSubtype("(any,null)","{{null f1} f1}"); }
	@Test public void test_462() { checkNotSubtype("(any,null)","{{null f2} f1}"); }
	@Test public void test_463() { checkNotSubtype("(any,null)","{{null f1} f2}"); }
	@Test public void test_464() { checkNotSubtype("(any,null)","{{null f2} f2}"); }
	@Test public void test_465() { checkNotSubtype("(any,null)","X<{X|any f1}>"); }
	@Test public void test_466() { checkNotSubtype("(any,null)","X<{X|any f2}>"); }
	@Test public void test_467() { checkNotSubtype("(any,null)","X<{X|null f1}>"); }
	@Test public void test_468() { checkNotSubtype("(any,null)","X<{X|null f2}>"); }
	@Test public void test_469() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_470() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_471() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_472() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_473() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_474() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_475() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_476() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_477() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_478() { checkNotSubtype("(any,null)","null"); }
	@Test public void test_479() { checkNotSubtype("(any,null)","X<null|(X,null)>"); }
	@Test public void test_480() { checkNotSubtype("(any,null)","null|{null f1}"); }
	@Test public void test_481() { checkNotSubtype("(any,null)","null|{null f2}"); }
	@Test public void test_482() { checkNotSubtype("(any,null)","X<null|{X f1}>"); }
	@Test public void test_483() { checkNotSubtype("(any,null)","X<null|{X f2}>"); }
	@Test public void test_484() { checkNotSubtype("(any,null)","null"); }
	@Test public void test_485() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_486() { checkNotSubtype("(any,null)","X<(X,null)|null>"); }
	@Test public void test_487() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_488() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_489() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_490() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_491() { checkNotSubtype("(any,null)","{null f1}|null"); }
	@Test public void test_492() { checkNotSubtype("(any,null)","{null f2}|null"); }
	@Test public void test_493() { checkNotSubtype("(any,null)","X<{X f1}|null>"); }
	@Test public void test_494() { checkNotSubtype("(any,null)","X<{X f2}|null>"); }
	@Test public void test_495() { checkNotSubtype("(any,null)","any"); }
	@Test public void test_496() { checkNotSubtype("(any,null)","null"); }
	@Test public void test_497() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_498() { checkNotSubtype("(any,{any f1})","null"); }
	@Test public void test_499() { checkNotSubtype("(any,{any f1})","{any f1}"); }
	@Test public void test_500() { checkNotSubtype("(any,{any f1})","{any f2}"); }
	@Test public void test_501() { checkNotSubtype("(any,{any f1})","{null f1}"); }
	@Test public void test_502() { checkNotSubtype("(any,{any f1})","{null f2}"); }
	@Test public void test_503() { checkNotSubtype("(any,{any f1})","(any,any)"); }
	@Test public void test_504() { checkNotSubtype("(any,{any f1})","(any,null)"); }
	@Test public void test_505() { checkIsSubtype("(any,{any f1})","(any,{any f1})"); }
	@Test public void test_506() { checkIsSubtype("(any,{any f1})","(any,{any f2})"); }
	@Test public void test_507() { checkNotSubtype("(any,{any f1})","X<(any,X|any)>"); }
	@Test public void test_508() { checkNotSubtype("(any,{any f1})","(null,any)"); }
	@Test public void test_509() { checkNotSubtype("(any,{any f1})","(null,null)"); }
	@Test public void test_510() { checkIsSubtype("(any,{any f1})","(null,{null f1})"); }
	@Test public void test_511() { checkIsSubtype("(any,{any f1})","(null,{null f2})"); }
	@Test public void test_512() { checkNotSubtype("(any,{any f1})","X<(null,X|null)>"); }
	@Test public void test_513() { checkNotSubtype("(any,{any f1})","({any f1},any)"); }
	@Test public void test_514() { checkNotSubtype("(any,{any f1})","({any f2},any)"); }
	@Test public void test_515() { checkNotSubtype("(any,{any f1})","({null f1},null)"); }
	@Test public void test_516() { checkNotSubtype("(any,{any f1})","({null f2},null)"); }
	@Test public void test_517() { checkNotSubtype("(any,{any f1})","X<(X|any,any)>"); }
	@Test public void test_518() { checkNotSubtype("(any,{any f1})","X<(X|null,null)>"); }
	@Test public void test_519() { checkNotSubtype("(any,{any f1})","{{any f1} f1}"); }
	@Test public void test_520() { checkNotSubtype("(any,{any f1})","{{any f2} f1}"); }
	@Test public void test_521() { checkNotSubtype("(any,{any f1})","{{any f1} f2}"); }
	@Test public void test_522() { checkNotSubtype("(any,{any f1})","{{any f2} f2}"); }
	@Test public void test_523() { checkNotSubtype("(any,{any f1})","{{null f1} f1}"); }
	@Test public void test_524() { checkNotSubtype("(any,{any f1})","{{null f2} f1}"); }
	@Test public void test_525() { checkNotSubtype("(any,{any f1})","{{null f1} f2}"); }
	@Test public void test_526() { checkNotSubtype("(any,{any f1})","{{null f2} f2}"); }
	@Test public void test_527() { checkNotSubtype("(any,{any f1})","X<{X|any f1}>"); }
	@Test public void test_528() { checkNotSubtype("(any,{any f1})","X<{X|any f2}>"); }
	@Test public void test_529() { checkNotSubtype("(any,{any f1})","X<{X|null f1}>"); }
	@Test public void test_530() { checkNotSubtype("(any,{any f1})","X<{X|null f2}>"); }
	@Test public void test_531() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_532() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_533() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_534() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_535() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_536() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_537() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_538() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_539() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_540() { checkNotSubtype("(any,{any f1})","null"); }
	@Test public void test_541() { checkNotSubtype("(any,{any f1})","X<null|(X,null)>"); }
	@Test public void test_542() { checkNotSubtype("(any,{any f1})","null|{null f1}"); }
	@Test public void test_543() { checkNotSubtype("(any,{any f1})","null|{null f2}"); }
	@Test public void test_544() { checkNotSubtype("(any,{any f1})","X<null|{X f1}>"); }
	@Test public void test_545() { checkNotSubtype("(any,{any f1})","X<null|{X f2}>"); }
	@Test public void test_546() { checkNotSubtype("(any,{any f1})","null"); }
	@Test public void test_547() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_548() { checkNotSubtype("(any,{any f1})","X<(X,null)|null>"); }
	@Test public void test_549() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_550() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_551() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_552() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_553() { checkNotSubtype("(any,{any f1})","{null f1}|null"); }
	@Test public void test_554() { checkNotSubtype("(any,{any f1})","{null f2}|null"); }
	@Test public void test_555() { checkNotSubtype("(any,{any f1})","X<{X f1}|null>"); }
	@Test public void test_556() { checkNotSubtype("(any,{any f1})","X<{X f2}|null>"); }
	@Test public void test_557() { checkNotSubtype("(any,{any f1})","any"); }
	@Test public void test_558() { checkNotSubtype("(any,{any f1})","null"); }
	@Test public void test_559() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_560() { checkNotSubtype("(any,{any f2})","null"); }
	@Test public void test_561() { checkNotSubtype("(any,{any f2})","{any f1}"); }
	@Test public void test_562() { checkNotSubtype("(any,{any f2})","{any f2}"); }
	@Test public void test_563() { checkNotSubtype("(any,{any f2})","{null f1}"); }
	@Test public void test_564() { checkNotSubtype("(any,{any f2})","{null f2}"); }
	@Test public void test_565() { checkNotSubtype("(any,{any f2})","(any,any)"); }
	@Test public void test_566() { checkNotSubtype("(any,{any f2})","(any,null)"); }
	@Test public void test_567() { checkIsSubtype("(any,{any f2})","(any,{any f1})"); }
	@Test public void test_568() { checkIsSubtype("(any,{any f2})","(any,{any f2})"); }
	@Test public void test_569() { checkNotSubtype("(any,{any f2})","X<(any,X|any)>"); }
	@Test public void test_570() { checkNotSubtype("(any,{any f2})","(null,any)"); }
	@Test public void test_571() { checkNotSubtype("(any,{any f2})","(null,null)"); }
	@Test public void test_572() { checkIsSubtype("(any,{any f2})","(null,{null f1})"); }
	@Test public void test_573() { checkIsSubtype("(any,{any f2})","(null,{null f2})"); }
	@Test public void test_574() { checkNotSubtype("(any,{any f2})","X<(null,X|null)>"); }
	@Test public void test_575() { checkNotSubtype("(any,{any f2})","({any f1},any)"); }
	@Test public void test_576() { checkNotSubtype("(any,{any f2})","({any f2},any)"); }
	@Test public void test_577() { checkNotSubtype("(any,{any f2})","({null f1},null)"); }
	@Test public void test_578() { checkNotSubtype("(any,{any f2})","({null f2},null)"); }
	@Test public void test_579() { checkNotSubtype("(any,{any f2})","X<(X|any,any)>"); }
	@Test public void test_580() { checkNotSubtype("(any,{any f2})","X<(X|null,null)>"); }
	@Test public void test_581() { checkNotSubtype("(any,{any f2})","{{any f1} f1}"); }
	@Test public void test_582() { checkNotSubtype("(any,{any f2})","{{any f2} f1}"); }
	@Test public void test_583() { checkNotSubtype("(any,{any f2})","{{any f1} f2}"); }
	@Test public void test_584() { checkNotSubtype("(any,{any f2})","{{any f2} f2}"); }
	@Test public void test_585() { checkNotSubtype("(any,{any f2})","{{null f1} f1}"); }
	@Test public void test_586() { checkNotSubtype("(any,{any f2})","{{null f2} f1}"); }
	@Test public void test_587() { checkNotSubtype("(any,{any f2})","{{null f1} f2}"); }
	@Test public void test_588() { checkNotSubtype("(any,{any f2})","{{null f2} f2}"); }
	@Test public void test_589() { checkNotSubtype("(any,{any f2})","X<{X|any f1}>"); }
	@Test public void test_590() { checkNotSubtype("(any,{any f2})","X<{X|any f2}>"); }
	@Test public void test_591() { checkNotSubtype("(any,{any f2})","X<{X|null f1}>"); }
	@Test public void test_592() { checkNotSubtype("(any,{any f2})","X<{X|null f2}>"); }
	@Test public void test_593() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_594() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_595() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_596() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_597() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_598() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_599() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_600() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_601() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_602() { checkNotSubtype("(any,{any f2})","null"); }
	@Test public void test_603() { checkNotSubtype("(any,{any f2})","X<null|(X,null)>"); }
	@Test public void test_604() { checkNotSubtype("(any,{any f2})","null|{null f1}"); }
	@Test public void test_605() { checkNotSubtype("(any,{any f2})","null|{null f2}"); }
	@Test public void test_606() { checkNotSubtype("(any,{any f2})","X<null|{X f1}>"); }
	@Test public void test_607() { checkNotSubtype("(any,{any f2})","X<null|{X f2}>"); }
	@Test public void test_608() { checkNotSubtype("(any,{any f2})","null"); }
	@Test public void test_609() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_610() { checkNotSubtype("(any,{any f2})","X<(X,null)|null>"); }
	@Test public void test_611() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_612() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_613() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_614() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_615() { checkNotSubtype("(any,{any f2})","{null f1}|null"); }
	@Test public void test_616() { checkNotSubtype("(any,{any f2})","{null f2}|null"); }
	@Test public void test_617() { checkNotSubtype("(any,{any f2})","X<{X f1}|null>"); }
	@Test public void test_618() { checkNotSubtype("(any,{any f2})","X<{X f2}|null>"); }
	@Test public void test_619() { checkNotSubtype("(any,{any f2})","any"); }
	@Test public void test_620() { checkNotSubtype("(any,{any f2})","null"); }
	@Test public void test_621() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_622() { checkNotSubtype("X<(any,X|any)>","null"); }
	@Test public void test_623() { checkNotSubtype("X<(any,X|any)>","{any f1}"); }
	@Test public void test_624() { checkNotSubtype("X<(any,X|any)>","{any f2}"); }
	@Test public void test_625() { checkNotSubtype("X<(any,X|any)>","{null f1}"); }
	@Test public void test_626() { checkNotSubtype("X<(any,X|any)>","{null f2}"); }
	@Test public void test_627() { checkIsSubtype("X<(any,X|any)>","(any,any)"); }
	@Test public void test_628() { checkIsSubtype("X<(any,X|any)>","(any,null)"); }
	@Test public void test_629() { checkIsSubtype("X<(any,X|any)>","(any,{any f1})"); }
	@Test public void test_630() { checkIsSubtype("X<(any,X|any)>","(any,{any f2})"); }
	@Test public void test_631() { checkIsSubtype("X<(any,X|any)>","X<(any,X|any)>"); }
	@Test public void test_632() { checkIsSubtype("X<(any,X|any)>","(null,any)"); }
	@Test public void test_633() { checkIsSubtype("X<(any,X|any)>","(null,null)"); }
	@Test public void test_634() { checkIsSubtype("X<(any,X|any)>","(null,{null f1})"); }
	@Test public void test_635() { checkIsSubtype("X<(any,X|any)>","(null,{null f2})"); }
	@Test public void test_636() { checkIsSubtype("X<(any,X|any)>","X<(null,X|null)>"); }
	@Test public void test_637() { checkIsSubtype("X<(any,X|any)>","({any f1},any)"); }
	@Test public void test_638() { checkIsSubtype("X<(any,X|any)>","({any f2},any)"); }
	@Test public void test_639() { checkIsSubtype("X<(any,X|any)>","({null f1},null)"); }
	@Test public void test_640() { checkIsSubtype("X<(any,X|any)>","({null f2},null)"); }
	@Test public void test_641() { checkIsSubtype("X<(any,X|any)>","X<(X|any,any)>"); }
	@Test public void test_642() { checkIsSubtype("X<(any,X|any)>","X<(X|null,null)>"); }
	@Test public void test_643() { checkNotSubtype("X<(any,X|any)>","{{any f1} f1}"); }
	@Test public void test_644() { checkNotSubtype("X<(any,X|any)>","{{any f2} f1}"); }
	@Test public void test_645() { checkNotSubtype("X<(any,X|any)>","{{any f1} f2}"); }
	@Test public void test_646() { checkNotSubtype("X<(any,X|any)>","{{any f2} f2}"); }
	@Test public void test_647() { checkNotSubtype("X<(any,X|any)>","{{null f1} f1}"); }
	@Test public void test_648() { checkNotSubtype("X<(any,X|any)>","{{null f2} f1}"); }
	@Test public void test_649() { checkNotSubtype("X<(any,X|any)>","{{null f1} f2}"); }
	@Test public void test_650() { checkNotSubtype("X<(any,X|any)>","{{null f2} f2}"); }
	@Test public void test_651() { checkNotSubtype("X<(any,X|any)>","X<{X|any f1}>"); }
	@Test public void test_652() { checkNotSubtype("X<(any,X|any)>","X<{X|any f2}>"); }
	@Test public void test_653() { checkNotSubtype("X<(any,X|any)>","X<{X|null f1}>"); }
	@Test public void test_654() { checkNotSubtype("X<(any,X|any)>","X<{X|null f2}>"); }
	@Test public void test_655() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_656() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_657() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_658() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_659() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_660() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_661() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_662() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_663() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_664() { checkNotSubtype("X<(any,X|any)>","null"); }
	@Test public void test_665() { checkNotSubtype("X<(any,X|any)>","X<null|(X,null)>"); }
	@Test public void test_666() { checkNotSubtype("X<(any,X|any)>","null|{null f1}"); }
	@Test public void test_667() { checkNotSubtype("X<(any,X|any)>","null|{null f2}"); }
	@Test public void test_668() { checkNotSubtype("X<(any,X|any)>","X<null|{X f1}>"); }
	@Test public void test_669() { checkNotSubtype("X<(any,X|any)>","X<null|{X f2}>"); }
	@Test public void test_670() { checkNotSubtype("X<(any,X|any)>","null"); }
	@Test public void test_671() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_672() { checkNotSubtype("X<(any,X|any)>","X<(X,null)|null>"); }
	@Test public void test_673() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_674() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_675() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_676() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_677() { checkNotSubtype("X<(any,X|any)>","{null f1}|null"); }
	@Test public void test_678() { checkNotSubtype("X<(any,X|any)>","{null f2}|null"); }
	@Test public void test_679() { checkNotSubtype("X<(any,X|any)>","X<{X f1}|null>"); }
	@Test public void test_680() { checkNotSubtype("X<(any,X|any)>","X<{X f2}|null>"); }
	@Test public void test_681() { checkNotSubtype("X<(any,X|any)>","any"); }
	@Test public void test_682() { checkNotSubtype("X<(any,X|any)>","null"); }
	@Test public void test_683() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_684() { checkNotSubtype("(null,any)","null"); }
	@Test public void test_685() { checkNotSubtype("(null,any)","{any f1}"); }
	@Test public void test_686() { checkNotSubtype("(null,any)","{any f2}"); }
	@Test public void test_687() { checkNotSubtype("(null,any)","{null f1}"); }
	@Test public void test_688() { checkNotSubtype("(null,any)","{null f2}"); }
	@Test public void test_689() { checkNotSubtype("(null,any)","(any,any)"); }
	@Test public void test_690() { checkNotSubtype("(null,any)","(any,null)"); }
	@Test public void test_691() { checkIsSubtype("(null,any)","(any,{any f1})"); }
	@Test public void test_692() { checkIsSubtype("(null,any)","(any,{any f2})"); }
	@Test public void test_693() { checkNotSubtype("(null,any)","X<(any,X|any)>"); }
	@Test public void test_694() { checkIsSubtype("(null,any)","(null,any)"); }
	@Test public void test_695() { checkIsSubtype("(null,any)","(null,null)"); }
	@Test public void test_696() { checkIsSubtype("(null,any)","(null,{null f1})"); }
	@Test public void test_697() { checkIsSubtype("(null,any)","(null,{null f2})"); }
	@Test public void test_698() { checkIsSubtype("(null,any)","X<(null,X|null)>"); }
	@Test public void test_699() { checkNotSubtype("(null,any)","({any f1},any)"); }
	@Test public void test_700() { checkNotSubtype("(null,any)","({any f2},any)"); }
	@Test public void test_701() { checkNotSubtype("(null,any)","({null f1},null)"); }
	@Test public void test_702() { checkNotSubtype("(null,any)","({null f2},null)"); }
	@Test public void test_703() { checkNotSubtype("(null,any)","X<(X|any,any)>"); }
	@Test public void test_704() { checkIsSubtype("(null,any)","X<(X|null,null)>"); }
	@Test public void test_705() { checkNotSubtype("(null,any)","{{any f1} f1}"); }
	@Test public void test_706() { checkNotSubtype("(null,any)","{{any f2} f1}"); }
	@Test public void test_707() { checkNotSubtype("(null,any)","{{any f1} f2}"); }
	@Test public void test_708() { checkNotSubtype("(null,any)","{{any f2} f2}"); }
	@Test public void test_709() { checkNotSubtype("(null,any)","{{null f1} f1}"); }
	@Test public void test_710() { checkNotSubtype("(null,any)","{{null f2} f1}"); }
	@Test public void test_711() { checkNotSubtype("(null,any)","{{null f1} f2}"); }
	@Test public void test_712() { checkNotSubtype("(null,any)","{{null f2} f2}"); }
	@Test public void test_713() { checkNotSubtype("(null,any)","X<{X|any f1}>"); }
	@Test public void test_714() { checkNotSubtype("(null,any)","X<{X|any f2}>"); }
	@Test public void test_715() { checkNotSubtype("(null,any)","X<{X|null f1}>"); }
	@Test public void test_716() { checkNotSubtype("(null,any)","X<{X|null f2}>"); }
	@Test public void test_717() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_718() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_719() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_720() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_721() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_722() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_723() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_724() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_725() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_726() { checkNotSubtype("(null,any)","null"); }
	@Test public void test_727() { checkNotSubtype("(null,any)","X<null|(X,null)>"); }
	@Test public void test_728() { checkNotSubtype("(null,any)","null|{null f1}"); }
	@Test public void test_729() { checkNotSubtype("(null,any)","null|{null f2}"); }
	@Test public void test_730() { checkNotSubtype("(null,any)","X<null|{X f1}>"); }
	@Test public void test_731() { checkNotSubtype("(null,any)","X<null|{X f2}>"); }
	@Test public void test_732() { checkNotSubtype("(null,any)","null"); }
	@Test public void test_733() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_734() { checkNotSubtype("(null,any)","X<(X,null)|null>"); }
	@Test public void test_735() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_736() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_737() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_738() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_739() { checkNotSubtype("(null,any)","{null f1}|null"); }
	@Test public void test_740() { checkNotSubtype("(null,any)","{null f2}|null"); }
	@Test public void test_741() { checkNotSubtype("(null,any)","X<{X f1}|null>"); }
	@Test public void test_742() { checkNotSubtype("(null,any)","X<{X f2}|null>"); }
	@Test public void test_743() { checkNotSubtype("(null,any)","any"); }
	@Test public void test_744() { checkNotSubtype("(null,any)","null"); }
	@Test public void test_745() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_746() { checkNotSubtype("(null,null)","null"); }
	@Test public void test_747() { checkNotSubtype("(null,null)","{any f1}"); }
	@Test public void test_748() { checkNotSubtype("(null,null)","{any f2}"); }
	@Test public void test_749() { checkNotSubtype("(null,null)","{null f1}"); }
	@Test public void test_750() { checkNotSubtype("(null,null)","{null f2}"); }
	@Test public void test_751() { checkNotSubtype("(null,null)","(any,any)"); }
	@Test public void test_752() { checkNotSubtype("(null,null)","(any,null)"); }
	@Test public void test_753() { checkIsSubtype("(null,null)","(any,{any f1})"); }
	@Test public void test_754() { checkIsSubtype("(null,null)","(any,{any f2})"); }
	@Test public void test_755() { checkNotSubtype("(null,null)","X<(any,X|any)>"); }
	@Test public void test_756() { checkNotSubtype("(null,null)","(null,any)"); }
	@Test public void test_757() { checkIsSubtype("(null,null)","(null,null)"); }
	@Test public void test_758() { checkIsSubtype("(null,null)","(null,{null f1})"); }
	@Test public void test_759() { checkIsSubtype("(null,null)","(null,{null f2})"); }
	@Test public void test_760() { checkIsSubtype("(null,null)","X<(null,X|null)>"); }
	@Test public void test_761() { checkNotSubtype("(null,null)","({any f1},any)"); }
	@Test public void test_762() { checkNotSubtype("(null,null)","({any f2},any)"); }
	@Test public void test_763() { checkNotSubtype("(null,null)","({null f1},null)"); }
	@Test public void test_764() { checkNotSubtype("(null,null)","({null f2},null)"); }
	@Test public void test_765() { checkNotSubtype("(null,null)","X<(X|any,any)>"); }
	@Test public void test_766() { checkIsSubtype("(null,null)","X<(X|null,null)>"); }
	@Test public void test_767() { checkNotSubtype("(null,null)","{{any f1} f1}"); }
	@Test public void test_768() { checkNotSubtype("(null,null)","{{any f2} f1}"); }
	@Test public void test_769() { checkNotSubtype("(null,null)","{{any f1} f2}"); }
	@Test public void test_770() { checkNotSubtype("(null,null)","{{any f2} f2}"); }
	@Test public void test_771() { checkNotSubtype("(null,null)","{{null f1} f1}"); }
	@Test public void test_772() { checkNotSubtype("(null,null)","{{null f2} f1}"); }
	@Test public void test_773() { checkNotSubtype("(null,null)","{{null f1} f2}"); }
	@Test public void test_774() { checkNotSubtype("(null,null)","{{null f2} f2}"); }
	@Test public void test_775() { checkNotSubtype("(null,null)","X<{X|any f1}>"); }
	@Test public void test_776() { checkNotSubtype("(null,null)","X<{X|any f2}>"); }
	@Test public void test_777() { checkNotSubtype("(null,null)","X<{X|null f1}>"); }
	@Test public void test_778() { checkNotSubtype("(null,null)","X<{X|null f2}>"); }
	@Test public void test_779() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_780() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_781() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_782() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_783() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_784() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_785() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_786() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_787() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_788() { checkNotSubtype("(null,null)","null"); }
	@Test public void test_789() { checkNotSubtype("(null,null)","X<null|(X,null)>"); }
	@Test public void test_790() { checkNotSubtype("(null,null)","null|{null f1}"); }
	@Test public void test_791() { checkNotSubtype("(null,null)","null|{null f2}"); }
	@Test public void test_792() { checkNotSubtype("(null,null)","X<null|{X f1}>"); }
	@Test public void test_793() { checkNotSubtype("(null,null)","X<null|{X f2}>"); }
	@Test public void test_794() { checkNotSubtype("(null,null)","null"); }
	@Test public void test_795() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_796() { checkNotSubtype("(null,null)","X<(X,null)|null>"); }
	@Test public void test_797() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_798() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_799() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_800() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_801() { checkNotSubtype("(null,null)","{null f1}|null"); }
	@Test public void test_802() { checkNotSubtype("(null,null)","{null f2}|null"); }
	@Test public void test_803() { checkNotSubtype("(null,null)","X<{X f1}|null>"); }
	@Test public void test_804() { checkNotSubtype("(null,null)","X<{X f2}|null>"); }
	@Test public void test_805() { checkNotSubtype("(null,null)","any"); }
	@Test public void test_806() { checkNotSubtype("(null,null)","null"); }
	@Test public void test_807() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_808() { checkNotSubtype("(null,{null f1})","null"); }
	@Test public void test_809() { checkNotSubtype("(null,{null f1})","{any f1}"); }
	@Test public void test_810() { checkNotSubtype("(null,{null f1})","{any f2}"); }
	@Test public void test_811() { checkNotSubtype("(null,{null f1})","{null f1}"); }
	@Test public void test_812() { checkNotSubtype("(null,{null f1})","{null f2}"); }
	@Test public void test_813() { checkNotSubtype("(null,{null f1})","(any,any)"); }
	@Test public void test_814() { checkNotSubtype("(null,{null f1})","(any,null)"); }
	@Test public void test_815() { checkIsSubtype("(null,{null f1})","(any,{any f1})"); }
	@Test public void test_816() { checkIsSubtype("(null,{null f1})","(any,{any f2})"); }
	@Test public void test_817() { checkNotSubtype("(null,{null f1})","X<(any,X|any)>"); }
	@Test public void test_818() { checkNotSubtype("(null,{null f1})","(null,any)"); }
	@Test public void test_819() { checkNotSubtype("(null,{null f1})","(null,null)"); }
	@Test public void test_820() { checkIsSubtype("(null,{null f1})","(null,{null f1})"); }
	@Test public void test_821() { checkIsSubtype("(null,{null f1})","(null,{null f2})"); }
	@Test public void test_822() { checkNotSubtype("(null,{null f1})","X<(null,X|null)>"); }
	@Test public void test_823() { checkNotSubtype("(null,{null f1})","({any f1},any)"); }
	@Test public void test_824() { checkNotSubtype("(null,{null f1})","({any f2},any)"); }
	@Test public void test_825() { checkNotSubtype("(null,{null f1})","({null f1},null)"); }
	@Test public void test_826() { checkNotSubtype("(null,{null f1})","({null f2},null)"); }
	@Test public void test_827() { checkNotSubtype("(null,{null f1})","X<(X|any,any)>"); }
	@Test public void test_828() { checkNotSubtype("(null,{null f1})","X<(X|null,null)>"); }
	@Test public void test_829() { checkNotSubtype("(null,{null f1})","{{any f1} f1}"); }
	@Test public void test_830() { checkNotSubtype("(null,{null f1})","{{any f2} f1}"); }
	@Test public void test_831() { checkNotSubtype("(null,{null f1})","{{any f1} f2}"); }
	@Test public void test_832() { checkNotSubtype("(null,{null f1})","{{any f2} f2}"); }
	@Test public void test_833() { checkNotSubtype("(null,{null f1})","{{null f1} f1}"); }
	@Test public void test_834() { checkNotSubtype("(null,{null f1})","{{null f2} f1}"); }
	@Test public void test_835() { checkNotSubtype("(null,{null f1})","{{null f1} f2}"); }
	@Test public void test_836() { checkNotSubtype("(null,{null f1})","{{null f2} f2}"); }
	@Test public void test_837() { checkNotSubtype("(null,{null f1})","X<{X|any f1}>"); }
	@Test public void test_838() { checkNotSubtype("(null,{null f1})","X<{X|any f2}>"); }
	@Test public void test_839() { checkNotSubtype("(null,{null f1})","X<{X|null f1}>"); }
	@Test public void test_840() { checkNotSubtype("(null,{null f1})","X<{X|null f2}>"); }
	@Test public void test_841() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_842() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_843() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_844() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_845() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_846() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_847() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_848() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_849() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_850() { checkNotSubtype("(null,{null f1})","null"); }
	@Test public void test_851() { checkNotSubtype("(null,{null f1})","X<null|(X,null)>"); }
	@Test public void test_852() { checkNotSubtype("(null,{null f1})","null|{null f1}"); }
	@Test public void test_853() { checkNotSubtype("(null,{null f1})","null|{null f2}"); }
	@Test public void test_854() { checkNotSubtype("(null,{null f1})","X<null|{X f1}>"); }
	@Test public void test_855() { checkNotSubtype("(null,{null f1})","X<null|{X f2}>"); }
	@Test public void test_856() { checkNotSubtype("(null,{null f1})","null"); }
	@Test public void test_857() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_858() { checkNotSubtype("(null,{null f1})","X<(X,null)|null>"); }
	@Test public void test_859() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_860() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_861() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_862() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_863() { checkNotSubtype("(null,{null f1})","{null f1}|null"); }
	@Test public void test_864() { checkNotSubtype("(null,{null f1})","{null f2}|null"); }
	@Test public void test_865() { checkNotSubtype("(null,{null f1})","X<{X f1}|null>"); }
	@Test public void test_866() { checkNotSubtype("(null,{null f1})","X<{X f2}|null>"); }
	@Test public void test_867() { checkNotSubtype("(null,{null f1})","any"); }
	@Test public void test_868() { checkNotSubtype("(null,{null f1})","null"); }
	@Test public void test_869() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_870() { checkNotSubtype("(null,{null f2})","null"); }
	@Test public void test_871() { checkNotSubtype("(null,{null f2})","{any f1}"); }
	@Test public void test_872() { checkNotSubtype("(null,{null f2})","{any f2}"); }
	@Test public void test_873() { checkNotSubtype("(null,{null f2})","{null f1}"); }
	@Test public void test_874() { checkNotSubtype("(null,{null f2})","{null f2}"); }
	@Test public void test_875() { checkNotSubtype("(null,{null f2})","(any,any)"); }
	@Test public void test_876() { checkNotSubtype("(null,{null f2})","(any,null)"); }
	@Test public void test_877() { checkIsSubtype("(null,{null f2})","(any,{any f1})"); }
	@Test public void test_878() { checkIsSubtype("(null,{null f2})","(any,{any f2})"); }
	@Test public void test_879() { checkNotSubtype("(null,{null f2})","X<(any,X|any)>"); }
	@Test public void test_880() { checkNotSubtype("(null,{null f2})","(null,any)"); }
	@Test public void test_881() { checkNotSubtype("(null,{null f2})","(null,null)"); }
	@Test public void test_882() { checkIsSubtype("(null,{null f2})","(null,{null f1})"); }
	@Test public void test_883() { checkIsSubtype("(null,{null f2})","(null,{null f2})"); }
	@Test public void test_884() { checkNotSubtype("(null,{null f2})","X<(null,X|null)>"); }
	@Test public void test_885() { checkNotSubtype("(null,{null f2})","({any f1},any)"); }
	@Test public void test_886() { checkNotSubtype("(null,{null f2})","({any f2},any)"); }
	@Test public void test_887() { checkNotSubtype("(null,{null f2})","({null f1},null)"); }
	@Test public void test_888() { checkNotSubtype("(null,{null f2})","({null f2},null)"); }
	@Test public void test_889() { checkNotSubtype("(null,{null f2})","X<(X|any,any)>"); }
	@Test public void test_890() { checkNotSubtype("(null,{null f2})","X<(X|null,null)>"); }
	@Test public void test_891() { checkNotSubtype("(null,{null f2})","{{any f1} f1}"); }
	@Test public void test_892() { checkNotSubtype("(null,{null f2})","{{any f2} f1}"); }
	@Test public void test_893() { checkNotSubtype("(null,{null f2})","{{any f1} f2}"); }
	@Test public void test_894() { checkNotSubtype("(null,{null f2})","{{any f2} f2}"); }
	@Test public void test_895() { checkNotSubtype("(null,{null f2})","{{null f1} f1}"); }
	@Test public void test_896() { checkNotSubtype("(null,{null f2})","{{null f2} f1}"); }
	@Test public void test_897() { checkNotSubtype("(null,{null f2})","{{null f1} f2}"); }
	@Test public void test_898() { checkNotSubtype("(null,{null f2})","{{null f2} f2}"); }
	@Test public void test_899() { checkNotSubtype("(null,{null f2})","X<{X|any f1}>"); }
	@Test public void test_900() { checkNotSubtype("(null,{null f2})","X<{X|any f2}>"); }
	@Test public void test_901() { checkNotSubtype("(null,{null f2})","X<{X|null f1}>"); }
	@Test public void test_902() { checkNotSubtype("(null,{null f2})","X<{X|null f2}>"); }
	@Test public void test_903() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_904() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_905() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_906() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_907() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_908() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_909() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_910() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_911() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_912() { checkNotSubtype("(null,{null f2})","null"); }
	@Test public void test_913() { checkNotSubtype("(null,{null f2})","X<null|(X,null)>"); }
	@Test public void test_914() { checkNotSubtype("(null,{null f2})","null|{null f1}"); }
	@Test public void test_915() { checkNotSubtype("(null,{null f2})","null|{null f2}"); }
	@Test public void test_916() { checkNotSubtype("(null,{null f2})","X<null|{X f1}>"); }
	@Test public void test_917() { checkNotSubtype("(null,{null f2})","X<null|{X f2}>"); }
	@Test public void test_918() { checkNotSubtype("(null,{null f2})","null"); }
	@Test public void test_919() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_920() { checkNotSubtype("(null,{null f2})","X<(X,null)|null>"); }
	@Test public void test_921() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_922() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_923() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_924() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_925() { checkNotSubtype("(null,{null f2})","{null f1}|null"); }
	@Test public void test_926() { checkNotSubtype("(null,{null f2})","{null f2}|null"); }
	@Test public void test_927() { checkNotSubtype("(null,{null f2})","X<{X f1}|null>"); }
	@Test public void test_928() { checkNotSubtype("(null,{null f2})","X<{X f2}|null>"); }
	@Test public void test_929() { checkNotSubtype("(null,{null f2})","any"); }
	@Test public void test_930() { checkNotSubtype("(null,{null f2})","null"); }
	@Test public void test_931() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_932() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test public void test_933() { checkNotSubtype("X<(null,X|null)>","{any f1}"); }
	@Test public void test_934() { checkNotSubtype("X<(null,X|null)>","{any f2}"); }
	@Test public void test_935() { checkNotSubtype("X<(null,X|null)>","{null f1}"); }
	@Test public void test_936() { checkNotSubtype("X<(null,X|null)>","{null f2}"); }
	@Test public void test_937() { checkNotSubtype("X<(null,X|null)>","(any,any)"); }
	@Test public void test_938() { checkNotSubtype("X<(null,X|null)>","(any,null)"); }
	@Test public void test_939() { checkIsSubtype("X<(null,X|null)>","(any,{any f1})"); }
	@Test public void test_940() { checkIsSubtype("X<(null,X|null)>","(any,{any f2})"); }
	@Test public void test_941() { checkNotSubtype("X<(null,X|null)>","X<(any,X|any)>"); }
	@Test public void test_942() { checkNotSubtype("X<(null,X|null)>","(null,any)"); }
	@Test public void test_943() { checkIsSubtype("X<(null,X|null)>","(null,null)"); }
	@Test public void test_944() { checkIsSubtype("X<(null,X|null)>","(null,{null f1})"); }
	@Test public void test_945() { checkIsSubtype("X<(null,X|null)>","(null,{null f2})"); }
	@Test public void test_946() { checkIsSubtype("X<(null,X|null)>","X<(null,X|null)>"); }
	@Test public void test_947() { checkNotSubtype("X<(null,X|null)>","({any f1},any)"); }
	@Test public void test_948() { checkNotSubtype("X<(null,X|null)>","({any f2},any)"); }
	@Test public void test_949() { checkNotSubtype("X<(null,X|null)>","({null f1},null)"); }
	@Test public void test_950() { checkNotSubtype("X<(null,X|null)>","({null f2},null)"); }
	@Test public void test_951() { checkNotSubtype("X<(null,X|null)>","X<(X|any,any)>"); }
	@Test public void test_952() { checkIsSubtype("X<(null,X|null)>","X<(X|null,null)>"); }
	@Test public void test_953() { checkNotSubtype("X<(null,X|null)>","{{any f1} f1}"); }
	@Test public void test_954() { checkNotSubtype("X<(null,X|null)>","{{any f2} f1}"); }
	@Test public void test_955() { checkNotSubtype("X<(null,X|null)>","{{any f1} f2}"); }
	@Test public void test_956() { checkNotSubtype("X<(null,X|null)>","{{any f2} f2}"); }
	@Test public void test_957() { checkNotSubtype("X<(null,X|null)>","{{null f1} f1}"); }
	@Test public void test_958() { checkNotSubtype("X<(null,X|null)>","{{null f2} f1}"); }
	@Test public void test_959() { checkNotSubtype("X<(null,X|null)>","{{null f1} f2}"); }
	@Test public void test_960() { checkNotSubtype("X<(null,X|null)>","{{null f2} f2}"); }
	@Test public void test_961() { checkNotSubtype("X<(null,X|null)>","X<{X|any f1}>"); }
	@Test public void test_962() { checkNotSubtype("X<(null,X|null)>","X<{X|any f2}>"); }
	@Test public void test_963() { checkNotSubtype("X<(null,X|null)>","X<{X|null f1}>"); }
	@Test public void test_964() { checkNotSubtype("X<(null,X|null)>","X<{X|null f2}>"); }
	@Test public void test_965() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_966() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_967() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_968() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_969() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_970() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_971() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_972() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_973() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_974() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test public void test_975() { checkNotSubtype("X<(null,X|null)>","X<null|(X,null)>"); }
	@Test public void test_976() { checkNotSubtype("X<(null,X|null)>","null|{null f1}"); }
	@Test public void test_977() { checkNotSubtype("X<(null,X|null)>","null|{null f2}"); }
	@Test public void test_978() { checkNotSubtype("X<(null,X|null)>","X<null|{X f1}>"); }
	@Test public void test_979() { checkNotSubtype("X<(null,X|null)>","X<null|{X f2}>"); }
	@Test public void test_980() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test public void test_981() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_982() { checkNotSubtype("X<(null,X|null)>","X<(X,null)|null>"); }
	@Test public void test_983() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_984() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_985() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_986() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_987() { checkNotSubtype("X<(null,X|null)>","{null f1}|null"); }
	@Test public void test_988() { checkNotSubtype("X<(null,X|null)>","{null f2}|null"); }
	@Test public void test_989() { checkNotSubtype("X<(null,X|null)>","X<{X f1}|null>"); }
	@Test public void test_990() { checkNotSubtype("X<(null,X|null)>","X<{X f2}|null>"); }
	@Test public void test_991() { checkNotSubtype("X<(null,X|null)>","any"); }
	@Test public void test_992() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test public void test_993() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_994() { checkNotSubtype("({any f1},any)","null"); }
	@Test public void test_995() { checkNotSubtype("({any f1},any)","{any f1}"); }
	@Test public void test_996() { checkNotSubtype("({any f1},any)","{any f2}"); }
	@Test public void test_997() { checkNotSubtype("({any f1},any)","{null f1}"); }
	@Test public void test_998() { checkNotSubtype("({any f1},any)","{null f2}"); }
	@Test public void test_999() { checkNotSubtype("({any f1},any)","(any,any)"); }
	@Test public void test_1000() { checkNotSubtype("({any f1},any)","(any,null)"); }
	@Test public void test_1001() { checkIsSubtype("({any f1},any)","(any,{any f1})"); }
	@Test public void test_1002() { checkIsSubtype("({any f1},any)","(any,{any f2})"); }
	@Test public void test_1003() { checkNotSubtype("({any f1},any)","X<(any,X|any)>"); }
	@Test public void test_1004() { checkNotSubtype("({any f1},any)","(null,any)"); }
	@Test public void test_1005() { checkNotSubtype("({any f1},any)","(null,null)"); }
	@Test public void test_1006() { checkIsSubtype("({any f1},any)","(null,{null f1})"); }
	@Test public void test_1007() { checkIsSubtype("({any f1},any)","(null,{null f2})"); }
	@Test public void test_1008() { checkNotSubtype("({any f1},any)","X<(null,X|null)>"); }
	@Test public void test_1009() { checkIsSubtype("({any f1},any)","({any f1},any)"); }
	@Test public void test_1010() { checkNotSubtype("({any f1},any)","({any f2},any)"); }
	@Test public void test_1011() { checkIsSubtype("({any f1},any)","({null f1},null)"); }
	@Test public void test_1012() { checkNotSubtype("({any f1},any)","({null f2},null)"); }
	@Test public void test_1013() { checkNotSubtype("({any f1},any)","X<(X|any,any)>"); }
	@Test public void test_1014() { checkNotSubtype("({any f1},any)","X<(X|null,null)>"); }
	@Test public void test_1015() { checkNotSubtype("({any f1},any)","{{any f1} f1}"); }
	@Test public void test_1016() { checkNotSubtype("({any f1},any)","{{any f2} f1}"); }
	@Test public void test_1017() { checkNotSubtype("({any f1},any)","{{any f1} f2}"); }
	@Test public void test_1018() { checkNotSubtype("({any f1},any)","{{any f2} f2}"); }
	@Test public void test_1019() { checkNotSubtype("({any f1},any)","{{null f1} f1}"); }
	@Test public void test_1020() { checkNotSubtype("({any f1},any)","{{null f2} f1}"); }
	@Test public void test_1021() { checkNotSubtype("({any f1},any)","{{null f1} f2}"); }
	@Test public void test_1022() { checkNotSubtype("({any f1},any)","{{null f2} f2}"); }
	@Test public void test_1023() { checkNotSubtype("({any f1},any)","X<{X|any f1}>"); }
	@Test public void test_1024() { checkNotSubtype("({any f1},any)","X<{X|any f2}>"); }
	@Test public void test_1025() { checkNotSubtype("({any f1},any)","X<{X|null f1}>"); }
	@Test public void test_1026() { checkNotSubtype("({any f1},any)","X<{X|null f2}>"); }
	@Test public void test_1027() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1028() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1029() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1030() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1031() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1032() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1033() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1034() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1035() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1036() { checkNotSubtype("({any f1},any)","null"); }
	@Test public void test_1037() { checkNotSubtype("({any f1},any)","X<null|(X,null)>"); }
	@Test public void test_1038() { checkNotSubtype("({any f1},any)","null|{null f1}"); }
	@Test public void test_1039() { checkNotSubtype("({any f1},any)","null|{null f2}"); }
	@Test public void test_1040() { checkNotSubtype("({any f1},any)","X<null|{X f1}>"); }
	@Test public void test_1041() { checkNotSubtype("({any f1},any)","X<null|{X f2}>"); }
	@Test public void test_1042() { checkNotSubtype("({any f1},any)","null"); }
	@Test public void test_1043() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1044() { checkNotSubtype("({any f1},any)","X<(X,null)|null>"); }
	@Test public void test_1045() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1046() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1047() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1048() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1049() { checkNotSubtype("({any f1},any)","{null f1}|null"); }
	@Test public void test_1050() { checkNotSubtype("({any f1},any)","{null f2}|null"); }
	@Test public void test_1051() { checkNotSubtype("({any f1},any)","X<{X f1}|null>"); }
	@Test public void test_1052() { checkNotSubtype("({any f1},any)","X<{X f2}|null>"); }
	@Test public void test_1053() { checkNotSubtype("({any f1},any)","any"); }
	@Test public void test_1054() { checkNotSubtype("({any f1},any)","null"); }
	@Test public void test_1055() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1056() { checkNotSubtype("({any f2},any)","null"); }
	@Test public void test_1057() { checkNotSubtype("({any f2},any)","{any f1}"); }
	@Test public void test_1058() { checkNotSubtype("({any f2},any)","{any f2}"); }
	@Test public void test_1059() { checkNotSubtype("({any f2},any)","{null f1}"); }
	@Test public void test_1060() { checkNotSubtype("({any f2},any)","{null f2}"); }
	@Test public void test_1061() { checkNotSubtype("({any f2},any)","(any,any)"); }
	@Test public void test_1062() { checkNotSubtype("({any f2},any)","(any,null)"); }
	@Test public void test_1063() { checkIsSubtype("({any f2},any)","(any,{any f1})"); }
	@Test public void test_1064() { checkIsSubtype("({any f2},any)","(any,{any f2})"); }
	@Test public void test_1065() { checkNotSubtype("({any f2},any)","X<(any,X|any)>"); }
	@Test public void test_1066() { checkNotSubtype("({any f2},any)","(null,any)"); }
	@Test public void test_1067() { checkNotSubtype("({any f2},any)","(null,null)"); }
	@Test public void test_1068() { checkIsSubtype("({any f2},any)","(null,{null f1})"); }
	@Test public void test_1069() { checkIsSubtype("({any f2},any)","(null,{null f2})"); }
	@Test public void test_1070() { checkNotSubtype("({any f2},any)","X<(null,X|null)>"); }
	@Test public void test_1071() { checkNotSubtype("({any f2},any)","({any f1},any)"); }
	@Test public void test_1072() { checkIsSubtype("({any f2},any)","({any f2},any)"); }
	@Test public void test_1073() { checkNotSubtype("({any f2},any)","({null f1},null)"); }
	@Test public void test_1074() { checkIsSubtype("({any f2},any)","({null f2},null)"); }
	@Test public void test_1075() { checkNotSubtype("({any f2},any)","X<(X|any,any)>"); }
	@Test public void test_1076() { checkNotSubtype("({any f2},any)","X<(X|null,null)>"); }
	@Test public void test_1077() { checkNotSubtype("({any f2},any)","{{any f1} f1}"); }
	@Test public void test_1078() { checkNotSubtype("({any f2},any)","{{any f2} f1}"); }
	@Test public void test_1079() { checkNotSubtype("({any f2},any)","{{any f1} f2}"); }
	@Test public void test_1080() { checkNotSubtype("({any f2},any)","{{any f2} f2}"); }
	@Test public void test_1081() { checkNotSubtype("({any f2},any)","{{null f1} f1}"); }
	@Test public void test_1082() { checkNotSubtype("({any f2},any)","{{null f2} f1}"); }
	@Test public void test_1083() { checkNotSubtype("({any f2},any)","{{null f1} f2}"); }
	@Test public void test_1084() { checkNotSubtype("({any f2},any)","{{null f2} f2}"); }
	@Test public void test_1085() { checkNotSubtype("({any f2},any)","X<{X|any f1}>"); }
	@Test public void test_1086() { checkNotSubtype("({any f2},any)","X<{X|any f2}>"); }
	@Test public void test_1087() { checkNotSubtype("({any f2},any)","X<{X|null f1}>"); }
	@Test public void test_1088() { checkNotSubtype("({any f2},any)","X<{X|null f2}>"); }
	@Test public void test_1089() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1090() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1091() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1092() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1093() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1094() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1095() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1096() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1097() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1098() { checkNotSubtype("({any f2},any)","null"); }
	@Test public void test_1099() { checkNotSubtype("({any f2},any)","X<null|(X,null)>"); }
	@Test public void test_1100() { checkNotSubtype("({any f2},any)","null|{null f1}"); }
	@Test public void test_1101() { checkNotSubtype("({any f2},any)","null|{null f2}"); }
	@Test public void test_1102() { checkNotSubtype("({any f2},any)","X<null|{X f1}>"); }
	@Test public void test_1103() { checkNotSubtype("({any f2},any)","X<null|{X f2}>"); }
	@Test public void test_1104() { checkNotSubtype("({any f2},any)","null"); }
	@Test public void test_1105() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1106() { checkNotSubtype("({any f2},any)","X<(X,null)|null>"); }
	@Test public void test_1107() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1108() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1109() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1110() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1111() { checkNotSubtype("({any f2},any)","{null f1}|null"); }
	@Test public void test_1112() { checkNotSubtype("({any f2},any)","{null f2}|null"); }
	@Test public void test_1113() { checkNotSubtype("({any f2},any)","X<{X f1}|null>"); }
	@Test public void test_1114() { checkNotSubtype("({any f2},any)","X<{X f2}|null>"); }
	@Test public void test_1115() { checkNotSubtype("({any f2},any)","any"); }
	@Test public void test_1116() { checkNotSubtype("({any f2},any)","null"); }
	@Test public void test_1117() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1118() { checkNotSubtype("({null f1},null)","null"); }
	@Test public void test_1119() { checkNotSubtype("({null f1},null)","{any f1}"); }
	@Test public void test_1120() { checkNotSubtype("({null f1},null)","{any f2}"); }
	@Test public void test_1121() { checkNotSubtype("({null f1},null)","{null f1}"); }
	@Test public void test_1122() { checkNotSubtype("({null f1},null)","{null f2}"); }
	@Test public void test_1123() { checkNotSubtype("({null f1},null)","(any,any)"); }
	@Test public void test_1124() { checkNotSubtype("({null f1},null)","(any,null)"); }
	@Test public void test_1125() { checkIsSubtype("({null f1},null)","(any,{any f1})"); }
	@Test public void test_1126() { checkIsSubtype("({null f1},null)","(any,{any f2})"); }
	@Test public void test_1127() { checkNotSubtype("({null f1},null)","X<(any,X|any)>"); }
	@Test public void test_1128() { checkNotSubtype("({null f1},null)","(null,any)"); }
	@Test public void test_1129() { checkNotSubtype("({null f1},null)","(null,null)"); }
	@Test public void test_1130() { checkIsSubtype("({null f1},null)","(null,{null f1})"); }
	@Test public void test_1131() { checkIsSubtype("({null f1},null)","(null,{null f2})"); }
	@Test public void test_1132() { checkNotSubtype("({null f1},null)","X<(null,X|null)>"); }
	@Test public void test_1133() { checkNotSubtype("({null f1},null)","({any f1},any)"); }
	@Test public void test_1134() { checkNotSubtype("({null f1},null)","({any f2},any)"); }
	@Test public void test_1135() { checkIsSubtype("({null f1},null)","({null f1},null)"); }
	@Test public void test_1136() { checkNotSubtype("({null f1},null)","({null f2},null)"); }
	@Test public void test_1137() { checkNotSubtype("({null f1},null)","X<(X|any,any)>"); }
	@Test public void test_1138() { checkNotSubtype("({null f1},null)","X<(X|null,null)>"); }
	@Test public void test_1139() { checkNotSubtype("({null f1},null)","{{any f1} f1}"); }
	@Test public void test_1140() { checkNotSubtype("({null f1},null)","{{any f2} f1}"); }
	@Test public void test_1141() { checkNotSubtype("({null f1},null)","{{any f1} f2}"); }
	@Test public void test_1142() { checkNotSubtype("({null f1},null)","{{any f2} f2}"); }
	@Test public void test_1143() { checkNotSubtype("({null f1},null)","{{null f1} f1}"); }
	@Test public void test_1144() { checkNotSubtype("({null f1},null)","{{null f2} f1}"); }
	@Test public void test_1145() { checkNotSubtype("({null f1},null)","{{null f1} f2}"); }
	@Test public void test_1146() { checkNotSubtype("({null f1},null)","{{null f2} f2}"); }
	@Test public void test_1147() { checkNotSubtype("({null f1},null)","X<{X|any f1}>"); }
	@Test public void test_1148() { checkNotSubtype("({null f1},null)","X<{X|any f2}>"); }
	@Test public void test_1149() { checkNotSubtype("({null f1},null)","X<{X|null f1}>"); }
	@Test public void test_1150() { checkNotSubtype("({null f1},null)","X<{X|null f2}>"); }
	@Test public void test_1151() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1152() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1153() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1154() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1155() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1156() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1157() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1158() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1159() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1160() { checkNotSubtype("({null f1},null)","null"); }
	@Test public void test_1161() { checkNotSubtype("({null f1},null)","X<null|(X,null)>"); }
	@Test public void test_1162() { checkNotSubtype("({null f1},null)","null|{null f1}"); }
	@Test public void test_1163() { checkNotSubtype("({null f1},null)","null|{null f2}"); }
	@Test public void test_1164() { checkNotSubtype("({null f1},null)","X<null|{X f1}>"); }
	@Test public void test_1165() { checkNotSubtype("({null f1},null)","X<null|{X f2}>"); }
	@Test public void test_1166() { checkNotSubtype("({null f1},null)","null"); }
	@Test public void test_1167() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1168() { checkNotSubtype("({null f1},null)","X<(X,null)|null>"); }
	@Test public void test_1169() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1170() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1171() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1172() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1173() { checkNotSubtype("({null f1},null)","{null f1}|null"); }
	@Test public void test_1174() { checkNotSubtype("({null f1},null)","{null f2}|null"); }
	@Test public void test_1175() { checkNotSubtype("({null f1},null)","X<{X f1}|null>"); }
	@Test public void test_1176() { checkNotSubtype("({null f1},null)","X<{X f2}|null>"); }
	@Test public void test_1177() { checkNotSubtype("({null f1},null)","any"); }
	@Test public void test_1178() { checkNotSubtype("({null f1},null)","null"); }
	@Test public void test_1179() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1180() { checkNotSubtype("({null f2},null)","null"); }
	@Test public void test_1181() { checkNotSubtype("({null f2},null)","{any f1}"); }
	@Test public void test_1182() { checkNotSubtype("({null f2},null)","{any f2}"); }
	@Test public void test_1183() { checkNotSubtype("({null f2},null)","{null f1}"); }
	@Test public void test_1184() { checkNotSubtype("({null f2},null)","{null f2}"); }
	@Test public void test_1185() { checkNotSubtype("({null f2},null)","(any,any)"); }
	@Test public void test_1186() { checkNotSubtype("({null f2},null)","(any,null)"); }
	@Test public void test_1187() { checkIsSubtype("({null f2},null)","(any,{any f1})"); }
	@Test public void test_1188() { checkIsSubtype("({null f2},null)","(any,{any f2})"); }
	@Test public void test_1189() { checkNotSubtype("({null f2},null)","X<(any,X|any)>"); }
	@Test public void test_1190() { checkNotSubtype("({null f2},null)","(null,any)"); }
	@Test public void test_1191() { checkNotSubtype("({null f2},null)","(null,null)"); }
	@Test public void test_1192() { checkIsSubtype("({null f2},null)","(null,{null f1})"); }
	@Test public void test_1193() { checkIsSubtype("({null f2},null)","(null,{null f2})"); }
	@Test public void test_1194() { checkNotSubtype("({null f2},null)","X<(null,X|null)>"); }
	@Test public void test_1195() { checkNotSubtype("({null f2},null)","({any f1},any)"); }
	@Test public void test_1196() { checkNotSubtype("({null f2},null)","({any f2},any)"); }
	@Test public void test_1197() { checkNotSubtype("({null f2},null)","({null f1},null)"); }
	@Test public void test_1198() { checkIsSubtype("({null f2},null)","({null f2},null)"); }
	@Test public void test_1199() { checkNotSubtype("({null f2},null)","X<(X|any,any)>"); }
	@Test public void test_1200() { checkNotSubtype("({null f2},null)","X<(X|null,null)>"); }
	@Test public void test_1201() { checkNotSubtype("({null f2},null)","{{any f1} f1}"); }
	@Test public void test_1202() { checkNotSubtype("({null f2},null)","{{any f2} f1}"); }
	@Test public void test_1203() { checkNotSubtype("({null f2},null)","{{any f1} f2}"); }
	@Test public void test_1204() { checkNotSubtype("({null f2},null)","{{any f2} f2}"); }
	@Test public void test_1205() { checkNotSubtype("({null f2},null)","{{null f1} f1}"); }
	@Test public void test_1206() { checkNotSubtype("({null f2},null)","{{null f2} f1}"); }
	@Test public void test_1207() { checkNotSubtype("({null f2},null)","{{null f1} f2}"); }
	@Test public void test_1208() { checkNotSubtype("({null f2},null)","{{null f2} f2}"); }
	@Test public void test_1209() { checkNotSubtype("({null f2},null)","X<{X|any f1}>"); }
	@Test public void test_1210() { checkNotSubtype("({null f2},null)","X<{X|any f2}>"); }
	@Test public void test_1211() { checkNotSubtype("({null f2},null)","X<{X|null f1}>"); }
	@Test public void test_1212() { checkNotSubtype("({null f2},null)","X<{X|null f2}>"); }
	@Test public void test_1213() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1214() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1215() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1216() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1217() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1218() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1219() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1220() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1221() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1222() { checkNotSubtype("({null f2},null)","null"); }
	@Test public void test_1223() { checkNotSubtype("({null f2},null)","X<null|(X,null)>"); }
	@Test public void test_1224() { checkNotSubtype("({null f2},null)","null|{null f1}"); }
	@Test public void test_1225() { checkNotSubtype("({null f2},null)","null|{null f2}"); }
	@Test public void test_1226() { checkNotSubtype("({null f2},null)","X<null|{X f1}>"); }
	@Test public void test_1227() { checkNotSubtype("({null f2},null)","X<null|{X f2}>"); }
	@Test public void test_1228() { checkNotSubtype("({null f2},null)","null"); }
	@Test public void test_1229() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1230() { checkNotSubtype("({null f2},null)","X<(X,null)|null>"); }
	@Test public void test_1231() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1232() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1233() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1234() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1235() { checkNotSubtype("({null f2},null)","{null f1}|null"); }
	@Test public void test_1236() { checkNotSubtype("({null f2},null)","{null f2}|null"); }
	@Test public void test_1237() { checkNotSubtype("({null f2},null)","X<{X f1}|null>"); }
	@Test public void test_1238() { checkNotSubtype("({null f2},null)","X<{X f2}|null>"); }
	@Test public void test_1239() { checkNotSubtype("({null f2},null)","any"); }
	@Test public void test_1240() { checkNotSubtype("({null f2},null)","null"); }
	@Test public void test_1241() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1242() { checkNotSubtype("X<(X|any,any)>","null"); }
	@Test public void test_1243() { checkNotSubtype("X<(X|any,any)>","{any f1}"); }
	@Test public void test_1244() { checkNotSubtype("X<(X|any,any)>","{any f2}"); }
	@Test public void test_1245() { checkNotSubtype("X<(X|any,any)>","{null f1}"); }
	@Test public void test_1246() { checkNotSubtype("X<(X|any,any)>","{null f2}"); }
	@Test public void test_1247() { checkIsSubtype("X<(X|any,any)>","(any,any)"); }
	@Test public void test_1248() { checkIsSubtype("X<(X|any,any)>","(any,null)"); }
	@Test public void test_1249() { checkIsSubtype("X<(X|any,any)>","(any,{any f1})"); }
	@Test public void test_1250() { checkIsSubtype("X<(X|any,any)>","(any,{any f2})"); }
	@Test public void test_1251() { checkIsSubtype("X<(X|any,any)>","X<(any,X|any)>"); }
	@Test public void test_1252() { checkIsSubtype("X<(X|any,any)>","(null,any)"); }
	@Test public void test_1253() { checkIsSubtype("X<(X|any,any)>","(null,null)"); }
	@Test public void test_1254() { checkIsSubtype("X<(X|any,any)>","(null,{null f1})"); }
	@Test public void test_1255() { checkIsSubtype("X<(X|any,any)>","(null,{null f2})"); }
	@Test public void test_1256() { checkIsSubtype("X<(X|any,any)>","X<(null,X|null)>"); }
	@Test public void test_1257() { checkIsSubtype("X<(X|any,any)>","({any f1},any)"); }
	@Test public void test_1258() { checkIsSubtype("X<(X|any,any)>","({any f2},any)"); }
	@Test public void test_1259() { checkIsSubtype("X<(X|any,any)>","({null f1},null)"); }
	@Test public void test_1260() { checkIsSubtype("X<(X|any,any)>","({null f2},null)"); }
	@Test public void test_1261() { checkIsSubtype("X<(X|any,any)>","X<(X|any,any)>"); }
	@Test public void test_1262() { checkIsSubtype("X<(X|any,any)>","X<(X|null,null)>"); }
	@Test public void test_1263() { checkNotSubtype("X<(X|any,any)>","{{any f1} f1}"); }
	@Test public void test_1264() { checkNotSubtype("X<(X|any,any)>","{{any f2} f1}"); }
	@Test public void test_1265() { checkNotSubtype("X<(X|any,any)>","{{any f1} f2}"); }
	@Test public void test_1266() { checkNotSubtype("X<(X|any,any)>","{{any f2} f2}"); }
	@Test public void test_1267() { checkNotSubtype("X<(X|any,any)>","{{null f1} f1}"); }
	@Test public void test_1268() { checkNotSubtype("X<(X|any,any)>","{{null f2} f1}"); }
	@Test public void test_1269() { checkNotSubtype("X<(X|any,any)>","{{null f1} f2}"); }
	@Test public void test_1270() { checkNotSubtype("X<(X|any,any)>","{{null f2} f2}"); }
	@Test public void test_1271() { checkNotSubtype("X<(X|any,any)>","X<{X|any f1}>"); }
	@Test public void test_1272() { checkNotSubtype("X<(X|any,any)>","X<{X|any f2}>"); }
	@Test public void test_1273() { checkNotSubtype("X<(X|any,any)>","X<{X|null f1}>"); }
	@Test public void test_1274() { checkNotSubtype("X<(X|any,any)>","X<{X|null f2}>"); }
	@Test public void test_1275() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1276() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1277() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1278() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1279() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1280() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1281() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1282() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1283() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1284() { checkNotSubtype("X<(X|any,any)>","null"); }
	@Test public void test_1285() { checkNotSubtype("X<(X|any,any)>","X<null|(X,null)>"); }
	@Test public void test_1286() { checkNotSubtype("X<(X|any,any)>","null|{null f1}"); }
	@Test public void test_1287() { checkNotSubtype("X<(X|any,any)>","null|{null f2}"); }
	@Test public void test_1288() { checkNotSubtype("X<(X|any,any)>","X<null|{X f1}>"); }
	@Test public void test_1289() { checkNotSubtype("X<(X|any,any)>","X<null|{X f2}>"); }
	@Test public void test_1290() { checkNotSubtype("X<(X|any,any)>","null"); }
	@Test public void test_1291() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1292() { checkNotSubtype("X<(X|any,any)>","X<(X,null)|null>"); }
	@Test public void test_1293() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1294() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1295() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1296() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1297() { checkNotSubtype("X<(X|any,any)>","{null f1}|null"); }
	@Test public void test_1298() { checkNotSubtype("X<(X|any,any)>","{null f2}|null"); }
	@Test public void test_1299() { checkNotSubtype("X<(X|any,any)>","X<{X f1}|null>"); }
	@Test public void test_1300() { checkNotSubtype("X<(X|any,any)>","X<{X f2}|null>"); }
	@Test public void test_1301() { checkNotSubtype("X<(X|any,any)>","any"); }
	@Test public void test_1302() { checkNotSubtype("X<(X|any,any)>","null"); }
	@Test public void test_1303() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1304() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test public void test_1305() { checkNotSubtype("X<(X|null,null)>","{any f1}"); }
	@Test public void test_1306() { checkNotSubtype("X<(X|null,null)>","{any f2}"); }
	@Test public void test_1307() { checkNotSubtype("X<(X|null,null)>","{null f1}"); }
	@Test public void test_1308() { checkNotSubtype("X<(X|null,null)>","{null f2}"); }
	@Test public void test_1309() { checkNotSubtype("X<(X|null,null)>","(any,any)"); }
	@Test public void test_1310() { checkNotSubtype("X<(X|null,null)>","(any,null)"); }
	@Test public void test_1311() { checkIsSubtype("X<(X|null,null)>","(any,{any f1})"); }
	@Test public void test_1312() { checkIsSubtype("X<(X|null,null)>","(any,{any f2})"); }
	@Test public void test_1313() { checkNotSubtype("X<(X|null,null)>","X<(any,X|any)>"); }
	@Test public void test_1314() { checkNotSubtype("X<(X|null,null)>","(null,any)"); }
	@Test public void test_1315() { checkIsSubtype("X<(X|null,null)>","(null,null)"); }
	@Test public void test_1316() { checkIsSubtype("X<(X|null,null)>","(null,{null f1})"); }
	@Test public void test_1317() { checkIsSubtype("X<(X|null,null)>","(null,{null f2})"); }
	@Test public void test_1318() { checkIsSubtype("X<(X|null,null)>","X<(null,X|null)>"); }
	@Test public void test_1319() { checkNotSubtype("X<(X|null,null)>","({any f1},any)"); }
	@Test public void test_1320() { checkNotSubtype("X<(X|null,null)>","({any f2},any)"); }
	@Test public void test_1321() { checkNotSubtype("X<(X|null,null)>","({null f1},null)"); }
	@Test public void test_1322() { checkNotSubtype("X<(X|null,null)>","({null f2},null)"); }
	@Test public void test_1323() { checkNotSubtype("X<(X|null,null)>","X<(X|any,any)>"); }
	@Test public void test_1324() { checkIsSubtype("X<(X|null,null)>","X<(X|null,null)>"); }
	@Test public void test_1325() { checkNotSubtype("X<(X|null,null)>","{{any f1} f1}"); }
	@Test public void test_1326() { checkNotSubtype("X<(X|null,null)>","{{any f2} f1}"); }
	@Test public void test_1327() { checkNotSubtype("X<(X|null,null)>","{{any f1} f2}"); }
	@Test public void test_1328() { checkNotSubtype("X<(X|null,null)>","{{any f2} f2}"); }
	@Test public void test_1329() { checkNotSubtype("X<(X|null,null)>","{{null f1} f1}"); }
	@Test public void test_1330() { checkNotSubtype("X<(X|null,null)>","{{null f2} f1}"); }
	@Test public void test_1331() { checkNotSubtype("X<(X|null,null)>","{{null f1} f2}"); }
	@Test public void test_1332() { checkNotSubtype("X<(X|null,null)>","{{null f2} f2}"); }
	@Test public void test_1333() { checkNotSubtype("X<(X|null,null)>","X<{X|any f1}>"); }
	@Test public void test_1334() { checkNotSubtype("X<(X|null,null)>","X<{X|any f2}>"); }
	@Test public void test_1335() { checkNotSubtype("X<(X|null,null)>","X<{X|null f1}>"); }
	@Test public void test_1336() { checkNotSubtype("X<(X|null,null)>","X<{X|null f2}>"); }
	@Test public void test_1337() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1338() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1339() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1340() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1341() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1342() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1343() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1344() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1345() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1346() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test public void test_1347() { checkNotSubtype("X<(X|null,null)>","X<null|(X,null)>"); }
	@Test public void test_1348() { checkNotSubtype("X<(X|null,null)>","null|{null f1}"); }
	@Test public void test_1349() { checkNotSubtype("X<(X|null,null)>","null|{null f2}"); }
	@Test public void test_1350() { checkNotSubtype("X<(X|null,null)>","X<null|{X f1}>"); }
	@Test public void test_1351() { checkNotSubtype("X<(X|null,null)>","X<null|{X f2}>"); }
	@Test public void test_1352() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test public void test_1353() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1354() { checkNotSubtype("X<(X|null,null)>","X<(X,null)|null>"); }
	@Test public void test_1355() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1356() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1357() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1358() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1359() { checkNotSubtype("X<(X|null,null)>","{null f1}|null"); }
	@Test public void test_1360() { checkNotSubtype("X<(X|null,null)>","{null f2}|null"); }
	@Test public void test_1361() { checkNotSubtype("X<(X|null,null)>","X<{X f1}|null>"); }
	@Test public void test_1362() { checkNotSubtype("X<(X|null,null)>","X<{X f2}|null>"); }
	@Test public void test_1363() { checkNotSubtype("X<(X|null,null)>","any"); }
	@Test public void test_1364() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test public void test_1365() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1366() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_1367() { checkNotSubtype("{{any f1} f1}","{any f1}"); }
	@Test public void test_1368() { checkNotSubtype("{{any f1} f1}","{any f2}"); }
	@Test public void test_1369() { checkNotSubtype("{{any f1} f1}","{null f1}"); }
	@Test public void test_1370() { checkNotSubtype("{{any f1} f1}","{null f2}"); }
	@Test public void test_1371() { checkNotSubtype("{{any f1} f1}","(any,any)"); }
	@Test public void test_1372() { checkNotSubtype("{{any f1} f1}","(any,null)"); }
	@Test public void test_1373() { checkIsSubtype("{{any f1} f1}","(any,{any f1})"); }
	@Test public void test_1374() { checkIsSubtype("{{any f1} f1}","(any,{any f2})"); }
	@Test public void test_1375() { checkNotSubtype("{{any f1} f1}","X<(any,X|any)>"); }
	@Test public void test_1376() { checkNotSubtype("{{any f1} f1}","(null,any)"); }
	@Test public void test_1377() { checkNotSubtype("{{any f1} f1}","(null,null)"); }
	@Test public void test_1378() { checkIsSubtype("{{any f1} f1}","(null,{null f1})"); }
	@Test public void test_1379() { checkIsSubtype("{{any f1} f1}","(null,{null f2})"); }
	@Test public void test_1380() { checkNotSubtype("{{any f1} f1}","X<(null,X|null)>"); }
	@Test public void test_1381() { checkNotSubtype("{{any f1} f1}","({any f1},any)"); }
	@Test public void test_1382() { checkNotSubtype("{{any f1} f1}","({any f2},any)"); }
	@Test public void test_1383() { checkNotSubtype("{{any f1} f1}","({null f1},null)"); }
	@Test public void test_1384() { checkNotSubtype("{{any f1} f1}","({null f2},null)"); }
	@Test public void test_1385() { checkNotSubtype("{{any f1} f1}","X<(X|any,any)>"); }
	@Test public void test_1386() { checkNotSubtype("{{any f1} f1}","X<(X|null,null)>"); }
	@Test public void test_1387() { checkIsSubtype("{{any f1} f1}","{{any f1} f1}"); }
	@Test public void test_1388() { checkNotSubtype("{{any f1} f1}","{{any f2} f1}"); }
	@Test public void test_1389() { checkNotSubtype("{{any f1} f1}","{{any f1} f2}"); }
	@Test public void test_1390() { checkNotSubtype("{{any f1} f1}","{{any f2} f2}"); }
	@Test public void test_1391() { checkIsSubtype("{{any f1} f1}","{{null f1} f1}"); }
	@Test public void test_1392() { checkNotSubtype("{{any f1} f1}","{{null f2} f1}"); }
	@Test public void test_1393() { checkNotSubtype("{{any f1} f1}","{{null f1} f2}"); }
	@Test public void test_1394() { checkNotSubtype("{{any f1} f1}","{{null f2} f2}"); }
	@Test public void test_1395() { checkNotSubtype("{{any f1} f1}","X<{X|any f1}>"); }
	@Test public void test_1396() { checkNotSubtype("{{any f1} f1}","X<{X|any f2}>"); }
	@Test public void test_1397() { checkNotSubtype("{{any f1} f1}","X<{X|null f1}>"); }
	@Test public void test_1398() { checkNotSubtype("{{any f1} f1}","X<{X|null f2}>"); }
	@Test public void test_1399() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1400() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1401() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1402() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1403() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1404() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1405() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1406() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1407() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1408() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_1409() { checkNotSubtype("{{any f1} f1}","X<null|(X,null)>"); }
	@Test public void test_1410() { checkNotSubtype("{{any f1} f1}","null|{null f1}"); }
	@Test public void test_1411() { checkNotSubtype("{{any f1} f1}","null|{null f2}"); }
	@Test public void test_1412() { checkNotSubtype("{{any f1} f1}","X<null|{X f1}>"); }
	@Test public void test_1413() { checkNotSubtype("{{any f1} f1}","X<null|{X f2}>"); }
	@Test public void test_1414() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_1415() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1416() { checkNotSubtype("{{any f1} f1}","X<(X,null)|null>"); }
	@Test public void test_1417() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1418() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1419() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1420() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1421() { checkNotSubtype("{{any f1} f1}","{null f1}|null"); }
	@Test public void test_1422() { checkNotSubtype("{{any f1} f1}","{null f2}|null"); }
	@Test public void test_1423() { checkNotSubtype("{{any f1} f1}","X<{X f1}|null>"); }
	@Test public void test_1424() { checkNotSubtype("{{any f1} f1}","X<{X f2}|null>"); }
	@Test public void test_1425() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1426() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_1427() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1428() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_1429() { checkNotSubtype("{{any f2} f1}","{any f1}"); }
	@Test public void test_1430() { checkNotSubtype("{{any f2} f1}","{any f2}"); }
	@Test public void test_1431() { checkNotSubtype("{{any f2} f1}","{null f1}"); }
	@Test public void test_1432() { checkNotSubtype("{{any f2} f1}","{null f2}"); }
	@Test public void test_1433() { checkNotSubtype("{{any f2} f1}","(any,any)"); }
	@Test public void test_1434() { checkNotSubtype("{{any f2} f1}","(any,null)"); }
	@Test public void test_1435() { checkIsSubtype("{{any f2} f1}","(any,{any f1})"); }
	@Test public void test_1436() { checkIsSubtype("{{any f2} f1}","(any,{any f2})"); }
	@Test public void test_1437() { checkNotSubtype("{{any f2} f1}","X<(any,X|any)>"); }
	@Test public void test_1438() { checkNotSubtype("{{any f2} f1}","(null,any)"); }
	@Test public void test_1439() { checkNotSubtype("{{any f2} f1}","(null,null)"); }
	@Test public void test_1440() { checkIsSubtype("{{any f2} f1}","(null,{null f1})"); }
	@Test public void test_1441() { checkIsSubtype("{{any f2} f1}","(null,{null f2})"); }
	@Test public void test_1442() { checkNotSubtype("{{any f2} f1}","X<(null,X|null)>"); }
	@Test public void test_1443() { checkNotSubtype("{{any f2} f1}","({any f1},any)"); }
	@Test public void test_1444() { checkNotSubtype("{{any f2} f1}","({any f2},any)"); }
	@Test public void test_1445() { checkNotSubtype("{{any f2} f1}","({null f1},null)"); }
	@Test public void test_1446() { checkNotSubtype("{{any f2} f1}","({null f2},null)"); }
	@Test public void test_1447() { checkNotSubtype("{{any f2} f1}","X<(X|any,any)>"); }
	@Test public void test_1448() { checkNotSubtype("{{any f2} f1}","X<(X|null,null)>"); }
	@Test public void test_1449() { checkNotSubtype("{{any f2} f1}","{{any f1} f1}"); }
	@Test public void test_1450() { checkIsSubtype("{{any f2} f1}","{{any f2} f1}"); }
	@Test public void test_1451() { checkNotSubtype("{{any f2} f1}","{{any f1} f2}"); }
	@Test public void test_1452() { checkNotSubtype("{{any f2} f1}","{{any f2} f2}"); }
	@Test public void test_1453() { checkNotSubtype("{{any f2} f1}","{{null f1} f1}"); }
	@Test public void test_1454() { checkIsSubtype("{{any f2} f1}","{{null f2} f1}"); }
	@Test public void test_1455() { checkNotSubtype("{{any f2} f1}","{{null f1} f2}"); }
	@Test public void test_1456() { checkNotSubtype("{{any f2} f1}","{{null f2} f2}"); }
	@Test public void test_1457() { checkNotSubtype("{{any f2} f1}","X<{X|any f1}>"); }
	@Test public void test_1458() { checkNotSubtype("{{any f2} f1}","X<{X|any f2}>"); }
	@Test public void test_1459() { checkNotSubtype("{{any f2} f1}","X<{X|null f1}>"); }
	@Test public void test_1460() { checkNotSubtype("{{any f2} f1}","X<{X|null f2}>"); }
	@Test public void test_1461() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1462() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1463() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1464() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1465() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1466() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1467() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1468() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1469() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1470() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_1471() { checkNotSubtype("{{any f2} f1}","X<null|(X,null)>"); }
	@Test public void test_1472() { checkNotSubtype("{{any f2} f1}","null|{null f1}"); }
	@Test public void test_1473() { checkNotSubtype("{{any f2} f1}","null|{null f2}"); }
	@Test public void test_1474() { checkNotSubtype("{{any f2} f1}","X<null|{X f1}>"); }
	@Test public void test_1475() { checkNotSubtype("{{any f2} f1}","X<null|{X f2}>"); }
	@Test public void test_1476() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_1477() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1478() { checkNotSubtype("{{any f2} f1}","X<(X,null)|null>"); }
	@Test public void test_1479() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1480() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1481() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1482() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1483() { checkNotSubtype("{{any f2} f1}","{null f1}|null"); }
	@Test public void test_1484() { checkNotSubtype("{{any f2} f1}","{null f2}|null"); }
	@Test public void test_1485() { checkNotSubtype("{{any f2} f1}","X<{X f1}|null>"); }
	@Test public void test_1486() { checkNotSubtype("{{any f2} f1}","X<{X f2}|null>"); }
	@Test public void test_1487() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1488() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_1489() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1490() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_1491() { checkNotSubtype("{{any f1} f2}","{any f1}"); }
	@Test public void test_1492() { checkNotSubtype("{{any f1} f2}","{any f2}"); }
	@Test public void test_1493() { checkNotSubtype("{{any f1} f2}","{null f1}"); }
	@Test public void test_1494() { checkNotSubtype("{{any f1} f2}","{null f2}"); }
	@Test public void test_1495() { checkNotSubtype("{{any f1} f2}","(any,any)"); }
	@Test public void test_1496() { checkNotSubtype("{{any f1} f2}","(any,null)"); }
	@Test public void test_1497() { checkIsSubtype("{{any f1} f2}","(any,{any f1})"); }
	@Test public void test_1498() { checkIsSubtype("{{any f1} f2}","(any,{any f2})"); }
	@Test public void test_1499() { checkNotSubtype("{{any f1} f2}","X<(any,X|any)>"); }
	@Test public void test_1500() { checkNotSubtype("{{any f1} f2}","(null,any)"); }
	@Test public void test_1501() { checkNotSubtype("{{any f1} f2}","(null,null)"); }
	@Test public void test_1502() { checkIsSubtype("{{any f1} f2}","(null,{null f1})"); }
	@Test public void test_1503() { checkIsSubtype("{{any f1} f2}","(null,{null f2})"); }
	@Test public void test_1504() { checkNotSubtype("{{any f1} f2}","X<(null,X|null)>"); }
	@Test public void test_1505() { checkNotSubtype("{{any f1} f2}","({any f1},any)"); }
	@Test public void test_1506() { checkNotSubtype("{{any f1} f2}","({any f2},any)"); }
	@Test public void test_1507() { checkNotSubtype("{{any f1} f2}","({null f1},null)"); }
	@Test public void test_1508() { checkNotSubtype("{{any f1} f2}","({null f2},null)"); }
	@Test public void test_1509() { checkNotSubtype("{{any f1} f2}","X<(X|any,any)>"); }
	@Test public void test_1510() { checkNotSubtype("{{any f1} f2}","X<(X|null,null)>"); }
	@Test public void test_1511() { checkNotSubtype("{{any f1} f2}","{{any f1} f1}"); }
	@Test public void test_1512() { checkNotSubtype("{{any f1} f2}","{{any f2} f1}"); }
	@Test public void test_1513() { checkIsSubtype("{{any f1} f2}","{{any f1} f2}"); }
	@Test public void test_1514() { checkNotSubtype("{{any f1} f2}","{{any f2} f2}"); }
	@Test public void test_1515() { checkNotSubtype("{{any f1} f2}","{{null f1} f1}"); }
	@Test public void test_1516() { checkNotSubtype("{{any f1} f2}","{{null f2} f1}"); }
	@Test public void test_1517() { checkIsSubtype("{{any f1} f2}","{{null f1} f2}"); }
	@Test public void test_1518() { checkNotSubtype("{{any f1} f2}","{{null f2} f2}"); }
	@Test public void test_1519() { checkNotSubtype("{{any f1} f2}","X<{X|any f1}>"); }
	@Test public void test_1520() { checkNotSubtype("{{any f1} f2}","X<{X|any f2}>"); }
	@Test public void test_1521() { checkNotSubtype("{{any f1} f2}","X<{X|null f1}>"); }
	@Test public void test_1522() { checkNotSubtype("{{any f1} f2}","X<{X|null f2}>"); }
	@Test public void test_1523() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1524() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1525() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1526() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1527() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1528() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1529() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1530() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1531() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1532() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_1533() { checkNotSubtype("{{any f1} f2}","X<null|(X,null)>"); }
	@Test public void test_1534() { checkNotSubtype("{{any f1} f2}","null|{null f1}"); }
	@Test public void test_1535() { checkNotSubtype("{{any f1} f2}","null|{null f2}"); }
	@Test public void test_1536() { checkNotSubtype("{{any f1} f2}","X<null|{X f1}>"); }
	@Test public void test_1537() { checkNotSubtype("{{any f1} f2}","X<null|{X f2}>"); }
	@Test public void test_1538() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_1539() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1540() { checkNotSubtype("{{any f1} f2}","X<(X,null)|null>"); }
	@Test public void test_1541() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1542() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1543() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1544() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1545() { checkNotSubtype("{{any f1} f2}","{null f1}|null"); }
	@Test public void test_1546() { checkNotSubtype("{{any f1} f2}","{null f2}|null"); }
	@Test public void test_1547() { checkNotSubtype("{{any f1} f2}","X<{X f1}|null>"); }
	@Test public void test_1548() { checkNotSubtype("{{any f1} f2}","X<{X f2}|null>"); }
	@Test public void test_1549() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1550() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_1551() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1552() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_1553() { checkNotSubtype("{{any f2} f2}","{any f1}"); }
	@Test public void test_1554() { checkNotSubtype("{{any f2} f2}","{any f2}"); }
	@Test public void test_1555() { checkNotSubtype("{{any f2} f2}","{null f1}"); }
	@Test public void test_1556() { checkNotSubtype("{{any f2} f2}","{null f2}"); }
	@Test public void test_1557() { checkNotSubtype("{{any f2} f2}","(any,any)"); }
	@Test public void test_1558() { checkNotSubtype("{{any f2} f2}","(any,null)"); }
	@Test public void test_1559() { checkIsSubtype("{{any f2} f2}","(any,{any f1})"); }
	@Test public void test_1560() { checkIsSubtype("{{any f2} f2}","(any,{any f2})"); }
	@Test public void test_1561() { checkNotSubtype("{{any f2} f2}","X<(any,X|any)>"); }
	@Test public void test_1562() { checkNotSubtype("{{any f2} f2}","(null,any)"); }
	@Test public void test_1563() { checkNotSubtype("{{any f2} f2}","(null,null)"); }
	@Test public void test_1564() { checkIsSubtype("{{any f2} f2}","(null,{null f1})"); }
	@Test public void test_1565() { checkIsSubtype("{{any f2} f2}","(null,{null f2})"); }
	@Test public void test_1566() { checkNotSubtype("{{any f2} f2}","X<(null,X|null)>"); }
	@Test public void test_1567() { checkNotSubtype("{{any f2} f2}","({any f1},any)"); }
	@Test public void test_1568() { checkNotSubtype("{{any f2} f2}","({any f2},any)"); }
	@Test public void test_1569() { checkNotSubtype("{{any f2} f2}","({null f1},null)"); }
	@Test public void test_1570() { checkNotSubtype("{{any f2} f2}","({null f2},null)"); }
	@Test public void test_1571() { checkNotSubtype("{{any f2} f2}","X<(X|any,any)>"); }
	@Test public void test_1572() { checkNotSubtype("{{any f2} f2}","X<(X|null,null)>"); }
	@Test public void test_1573() { checkNotSubtype("{{any f2} f2}","{{any f1} f1}"); }
	@Test public void test_1574() { checkNotSubtype("{{any f2} f2}","{{any f2} f1}"); }
	@Test public void test_1575() { checkNotSubtype("{{any f2} f2}","{{any f1} f2}"); }
	@Test public void test_1576() { checkIsSubtype("{{any f2} f2}","{{any f2} f2}"); }
	@Test public void test_1577() { checkNotSubtype("{{any f2} f2}","{{null f1} f1}"); }
	@Test public void test_1578() { checkNotSubtype("{{any f2} f2}","{{null f2} f1}"); }
	@Test public void test_1579() { checkNotSubtype("{{any f2} f2}","{{null f1} f2}"); }
	@Test public void test_1580() { checkIsSubtype("{{any f2} f2}","{{null f2} f2}"); }
	@Test public void test_1581() { checkNotSubtype("{{any f2} f2}","X<{X|any f1}>"); }
	@Test public void test_1582() { checkNotSubtype("{{any f2} f2}","X<{X|any f2}>"); }
	@Test public void test_1583() { checkNotSubtype("{{any f2} f2}","X<{X|null f1}>"); }
	@Test public void test_1584() { checkNotSubtype("{{any f2} f2}","X<{X|null f2}>"); }
	@Test public void test_1585() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1586() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1587() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1588() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1589() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1590() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1591() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1592() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1593() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1594() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_1595() { checkNotSubtype("{{any f2} f2}","X<null|(X,null)>"); }
	@Test public void test_1596() { checkNotSubtype("{{any f2} f2}","null|{null f1}"); }
	@Test public void test_1597() { checkNotSubtype("{{any f2} f2}","null|{null f2}"); }
	@Test public void test_1598() { checkNotSubtype("{{any f2} f2}","X<null|{X f1}>"); }
	@Test public void test_1599() { checkNotSubtype("{{any f2} f2}","X<null|{X f2}>"); }
	@Test public void test_1600() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_1601() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1602() { checkNotSubtype("{{any f2} f2}","X<(X,null)|null>"); }
	@Test public void test_1603() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1604() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1605() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1606() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1607() { checkNotSubtype("{{any f2} f2}","{null f1}|null"); }
	@Test public void test_1608() { checkNotSubtype("{{any f2} f2}","{null f2}|null"); }
	@Test public void test_1609() { checkNotSubtype("{{any f2} f2}","X<{X f1}|null>"); }
	@Test public void test_1610() { checkNotSubtype("{{any f2} f2}","X<{X f2}|null>"); }
	@Test public void test_1611() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1612() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_1613() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1614() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_1615() { checkNotSubtype("{{null f1} f1}","{any f1}"); }
	@Test public void test_1616() { checkNotSubtype("{{null f1} f1}","{any f2}"); }
	@Test public void test_1617() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test public void test_1618() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test public void test_1619() { checkNotSubtype("{{null f1} f1}","(any,any)"); }
	@Test public void test_1620() { checkNotSubtype("{{null f1} f1}","(any,null)"); }
	@Test public void test_1621() { checkIsSubtype("{{null f1} f1}","(any,{any f1})"); }
	@Test public void test_1622() { checkIsSubtype("{{null f1} f1}","(any,{any f2})"); }
	@Test public void test_1623() { checkNotSubtype("{{null f1} f1}","X<(any,X|any)>"); }
	@Test public void test_1624() { checkNotSubtype("{{null f1} f1}","(null,any)"); }
	@Test public void test_1625() { checkNotSubtype("{{null f1} f1}","(null,null)"); }
	@Test public void test_1626() { checkIsSubtype("{{null f1} f1}","(null,{null f1})"); }
	@Test public void test_1627() { checkIsSubtype("{{null f1} f1}","(null,{null f2})"); }
	@Test public void test_1628() { checkNotSubtype("{{null f1} f1}","X<(null,X|null)>"); }
	@Test public void test_1629() { checkNotSubtype("{{null f1} f1}","({any f1},any)"); }
	@Test public void test_1630() { checkNotSubtype("{{null f1} f1}","({any f2},any)"); }
	@Test public void test_1631() { checkNotSubtype("{{null f1} f1}","({null f1},null)"); }
	@Test public void test_1632() { checkNotSubtype("{{null f1} f1}","({null f2},null)"); }
	@Test public void test_1633() { checkNotSubtype("{{null f1} f1}","X<(X|any,any)>"); }
	@Test public void test_1634() { checkNotSubtype("{{null f1} f1}","X<(X|null,null)>"); }
	@Test public void test_1635() { checkNotSubtype("{{null f1} f1}","{{any f1} f1}"); }
	@Test public void test_1636() { checkNotSubtype("{{null f1} f1}","{{any f2} f1}"); }
	@Test public void test_1637() { checkNotSubtype("{{null f1} f1}","{{any f1} f2}"); }
	@Test public void test_1638() { checkNotSubtype("{{null f1} f1}","{{any f2} f2}"); }
	@Test public void test_1639() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test public void test_1640() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test public void test_1641() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test public void test_1642() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test public void test_1643() { checkNotSubtype("{{null f1} f1}","X<{X|any f1}>"); }
	@Test public void test_1644() { checkNotSubtype("{{null f1} f1}","X<{X|any f2}>"); }
	@Test public void test_1645() { checkNotSubtype("{{null f1} f1}","X<{X|null f1}>"); }
	@Test public void test_1646() { checkNotSubtype("{{null f1} f1}","X<{X|null f2}>"); }
	@Test public void test_1647() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1648() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1649() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1650() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1651() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1652() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1653() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1654() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1655() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1656() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_1657() { checkNotSubtype("{{null f1} f1}","X<null|(X,null)>"); }
	@Test public void test_1658() { checkNotSubtype("{{null f1} f1}","null|{null f1}"); }
	@Test public void test_1659() { checkNotSubtype("{{null f1} f1}","null|{null f2}"); }
	@Test public void test_1660() { checkNotSubtype("{{null f1} f1}","X<null|{X f1}>"); }
	@Test public void test_1661() { checkNotSubtype("{{null f1} f1}","X<null|{X f2}>"); }
	@Test public void test_1662() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_1663() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1664() { checkNotSubtype("{{null f1} f1}","X<(X,null)|null>"); }
	@Test public void test_1665() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1666() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1667() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1668() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1669() { checkNotSubtype("{{null f1} f1}","{null f1}|null"); }
	@Test public void test_1670() { checkNotSubtype("{{null f1} f1}","{null f2}|null"); }
	@Test public void test_1671() { checkNotSubtype("{{null f1} f1}","X<{X f1}|null>"); }
	@Test public void test_1672() { checkNotSubtype("{{null f1} f1}","X<{X f2}|null>"); }
	@Test public void test_1673() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1674() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_1675() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1676() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_1677() { checkNotSubtype("{{null f2} f1}","{any f1}"); }
	@Test public void test_1678() { checkNotSubtype("{{null f2} f1}","{any f2}"); }
	@Test public void test_1679() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test public void test_1680() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test public void test_1681() { checkNotSubtype("{{null f2} f1}","(any,any)"); }
	@Test public void test_1682() { checkNotSubtype("{{null f2} f1}","(any,null)"); }
	@Test public void test_1683() { checkIsSubtype("{{null f2} f1}","(any,{any f1})"); }
	@Test public void test_1684() { checkIsSubtype("{{null f2} f1}","(any,{any f2})"); }
	@Test public void test_1685() { checkNotSubtype("{{null f2} f1}","X<(any,X|any)>"); }
	@Test public void test_1686() { checkNotSubtype("{{null f2} f1}","(null,any)"); }
	@Test public void test_1687() { checkNotSubtype("{{null f2} f1}","(null,null)"); }
	@Test public void test_1688() { checkIsSubtype("{{null f2} f1}","(null,{null f1})"); }
	@Test public void test_1689() { checkIsSubtype("{{null f2} f1}","(null,{null f2})"); }
	@Test public void test_1690() { checkNotSubtype("{{null f2} f1}","X<(null,X|null)>"); }
	@Test public void test_1691() { checkNotSubtype("{{null f2} f1}","({any f1},any)"); }
	@Test public void test_1692() { checkNotSubtype("{{null f2} f1}","({any f2},any)"); }
	@Test public void test_1693() { checkNotSubtype("{{null f2} f1}","({null f1},null)"); }
	@Test public void test_1694() { checkNotSubtype("{{null f2} f1}","({null f2},null)"); }
	@Test public void test_1695() { checkNotSubtype("{{null f2} f1}","X<(X|any,any)>"); }
	@Test public void test_1696() { checkNotSubtype("{{null f2} f1}","X<(X|null,null)>"); }
	@Test public void test_1697() { checkNotSubtype("{{null f2} f1}","{{any f1} f1}"); }
	@Test public void test_1698() { checkNotSubtype("{{null f2} f1}","{{any f2} f1}"); }
	@Test public void test_1699() { checkNotSubtype("{{null f2} f1}","{{any f1} f2}"); }
	@Test public void test_1700() { checkNotSubtype("{{null f2} f1}","{{any f2} f2}"); }
	@Test public void test_1701() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test public void test_1702() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test public void test_1703() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test public void test_1704() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test public void test_1705() { checkNotSubtype("{{null f2} f1}","X<{X|any f1}>"); }
	@Test public void test_1706() { checkNotSubtype("{{null f2} f1}","X<{X|any f2}>"); }
	@Test public void test_1707() { checkNotSubtype("{{null f2} f1}","X<{X|null f1}>"); }
	@Test public void test_1708() { checkNotSubtype("{{null f2} f1}","X<{X|null f2}>"); }
	@Test public void test_1709() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1710() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1711() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1712() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1713() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1714() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1715() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1716() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1717() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1718() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_1719() { checkNotSubtype("{{null f2} f1}","X<null|(X,null)>"); }
	@Test public void test_1720() { checkNotSubtype("{{null f2} f1}","null|{null f1}"); }
	@Test public void test_1721() { checkNotSubtype("{{null f2} f1}","null|{null f2}"); }
	@Test public void test_1722() { checkNotSubtype("{{null f2} f1}","X<null|{X f1}>"); }
	@Test public void test_1723() { checkNotSubtype("{{null f2} f1}","X<null|{X f2}>"); }
	@Test public void test_1724() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_1725() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1726() { checkNotSubtype("{{null f2} f1}","X<(X,null)|null>"); }
	@Test public void test_1727() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1728() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1729() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1730() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1731() { checkNotSubtype("{{null f2} f1}","{null f1}|null"); }
	@Test public void test_1732() { checkNotSubtype("{{null f2} f1}","{null f2}|null"); }
	@Test public void test_1733() { checkNotSubtype("{{null f2} f1}","X<{X f1}|null>"); }
	@Test public void test_1734() { checkNotSubtype("{{null f2} f1}","X<{X f2}|null>"); }
	@Test public void test_1735() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1736() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_1737() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1738() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_1739() { checkNotSubtype("{{null f1} f2}","{any f1}"); }
	@Test public void test_1740() { checkNotSubtype("{{null f1} f2}","{any f2}"); }
	@Test public void test_1741() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test public void test_1742() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test public void test_1743() { checkNotSubtype("{{null f1} f2}","(any,any)"); }
	@Test public void test_1744() { checkNotSubtype("{{null f1} f2}","(any,null)"); }
	@Test public void test_1745() { checkIsSubtype("{{null f1} f2}","(any,{any f1})"); }
	@Test public void test_1746() { checkIsSubtype("{{null f1} f2}","(any,{any f2})"); }
	@Test public void test_1747() { checkNotSubtype("{{null f1} f2}","X<(any,X|any)>"); }
	@Test public void test_1748() { checkNotSubtype("{{null f1} f2}","(null,any)"); }
	@Test public void test_1749() { checkNotSubtype("{{null f1} f2}","(null,null)"); }
	@Test public void test_1750() { checkIsSubtype("{{null f1} f2}","(null,{null f1})"); }
	@Test public void test_1751() { checkIsSubtype("{{null f1} f2}","(null,{null f2})"); }
	@Test public void test_1752() { checkNotSubtype("{{null f1} f2}","X<(null,X|null)>"); }
	@Test public void test_1753() { checkNotSubtype("{{null f1} f2}","({any f1},any)"); }
	@Test public void test_1754() { checkNotSubtype("{{null f1} f2}","({any f2},any)"); }
	@Test public void test_1755() { checkNotSubtype("{{null f1} f2}","({null f1},null)"); }
	@Test public void test_1756() { checkNotSubtype("{{null f1} f2}","({null f2},null)"); }
	@Test public void test_1757() { checkNotSubtype("{{null f1} f2}","X<(X|any,any)>"); }
	@Test public void test_1758() { checkNotSubtype("{{null f1} f2}","X<(X|null,null)>"); }
	@Test public void test_1759() { checkNotSubtype("{{null f1} f2}","{{any f1} f1}"); }
	@Test public void test_1760() { checkNotSubtype("{{null f1} f2}","{{any f2} f1}"); }
	@Test public void test_1761() { checkNotSubtype("{{null f1} f2}","{{any f1} f2}"); }
	@Test public void test_1762() { checkNotSubtype("{{null f1} f2}","{{any f2} f2}"); }
	@Test public void test_1763() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test public void test_1764() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test public void test_1765() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test public void test_1766() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test public void test_1767() { checkNotSubtype("{{null f1} f2}","X<{X|any f1}>"); }
	@Test public void test_1768() { checkNotSubtype("{{null f1} f2}","X<{X|any f2}>"); }
	@Test public void test_1769() { checkNotSubtype("{{null f1} f2}","X<{X|null f1}>"); }
	@Test public void test_1770() { checkNotSubtype("{{null f1} f2}","X<{X|null f2}>"); }
	@Test public void test_1771() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1772() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1773() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1774() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1775() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1776() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1777() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1778() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1779() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1780() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_1781() { checkNotSubtype("{{null f1} f2}","X<null|(X,null)>"); }
	@Test public void test_1782() { checkNotSubtype("{{null f1} f2}","null|{null f1}"); }
	@Test public void test_1783() { checkNotSubtype("{{null f1} f2}","null|{null f2}"); }
	@Test public void test_1784() { checkNotSubtype("{{null f1} f2}","X<null|{X f1}>"); }
	@Test public void test_1785() { checkNotSubtype("{{null f1} f2}","X<null|{X f2}>"); }
	@Test public void test_1786() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_1787() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1788() { checkNotSubtype("{{null f1} f2}","X<(X,null)|null>"); }
	@Test public void test_1789() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1790() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1791() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1792() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1793() { checkNotSubtype("{{null f1} f2}","{null f1}|null"); }
	@Test public void test_1794() { checkNotSubtype("{{null f1} f2}","{null f2}|null"); }
	@Test public void test_1795() { checkNotSubtype("{{null f1} f2}","X<{X f1}|null>"); }
	@Test public void test_1796() { checkNotSubtype("{{null f1} f2}","X<{X f2}|null>"); }
	@Test public void test_1797() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1798() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_1799() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1800() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_1801() { checkNotSubtype("{{null f2} f2}","{any f1}"); }
	@Test public void test_1802() { checkNotSubtype("{{null f2} f2}","{any f2}"); }
	@Test public void test_1803() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test public void test_1804() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test public void test_1805() { checkNotSubtype("{{null f2} f2}","(any,any)"); }
	@Test public void test_1806() { checkNotSubtype("{{null f2} f2}","(any,null)"); }
	@Test public void test_1807() { checkIsSubtype("{{null f2} f2}","(any,{any f1})"); }
	@Test public void test_1808() { checkIsSubtype("{{null f2} f2}","(any,{any f2})"); }
	@Test public void test_1809() { checkNotSubtype("{{null f2} f2}","X<(any,X|any)>"); }
	@Test public void test_1810() { checkNotSubtype("{{null f2} f2}","(null,any)"); }
	@Test public void test_1811() { checkNotSubtype("{{null f2} f2}","(null,null)"); }
	@Test public void test_1812() { checkIsSubtype("{{null f2} f2}","(null,{null f1})"); }
	@Test public void test_1813() { checkIsSubtype("{{null f2} f2}","(null,{null f2})"); }
	@Test public void test_1814() { checkNotSubtype("{{null f2} f2}","X<(null,X|null)>"); }
	@Test public void test_1815() { checkNotSubtype("{{null f2} f2}","({any f1},any)"); }
	@Test public void test_1816() { checkNotSubtype("{{null f2} f2}","({any f2},any)"); }
	@Test public void test_1817() { checkNotSubtype("{{null f2} f2}","({null f1},null)"); }
	@Test public void test_1818() { checkNotSubtype("{{null f2} f2}","({null f2},null)"); }
	@Test public void test_1819() { checkNotSubtype("{{null f2} f2}","X<(X|any,any)>"); }
	@Test public void test_1820() { checkNotSubtype("{{null f2} f2}","X<(X|null,null)>"); }
	@Test public void test_1821() { checkNotSubtype("{{null f2} f2}","{{any f1} f1}"); }
	@Test public void test_1822() { checkNotSubtype("{{null f2} f2}","{{any f2} f1}"); }
	@Test public void test_1823() { checkNotSubtype("{{null f2} f2}","{{any f1} f2}"); }
	@Test public void test_1824() { checkNotSubtype("{{null f2} f2}","{{any f2} f2}"); }
	@Test public void test_1825() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test public void test_1826() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test public void test_1827() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test public void test_1828() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test public void test_1829() { checkNotSubtype("{{null f2} f2}","X<{X|any f1}>"); }
	@Test public void test_1830() { checkNotSubtype("{{null f2} f2}","X<{X|any f2}>"); }
	@Test public void test_1831() { checkNotSubtype("{{null f2} f2}","X<{X|null f1}>"); }
	@Test public void test_1832() { checkNotSubtype("{{null f2} f2}","X<{X|null f2}>"); }
	@Test public void test_1833() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1834() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1835() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1836() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1837() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1838() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1839() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1840() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1841() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1842() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_1843() { checkNotSubtype("{{null f2} f2}","X<null|(X,null)>"); }
	@Test public void test_1844() { checkNotSubtype("{{null f2} f2}","null|{null f1}"); }
	@Test public void test_1845() { checkNotSubtype("{{null f2} f2}","null|{null f2}"); }
	@Test public void test_1846() { checkNotSubtype("{{null f2} f2}","X<null|{X f1}>"); }
	@Test public void test_1847() { checkNotSubtype("{{null f2} f2}","X<null|{X f2}>"); }
	@Test public void test_1848() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_1849() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1850() { checkNotSubtype("{{null f2} f2}","X<(X,null)|null>"); }
	@Test public void test_1851() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1852() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1853() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1854() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1855() { checkNotSubtype("{{null f2} f2}","{null f1}|null"); }
	@Test public void test_1856() { checkNotSubtype("{{null f2} f2}","{null f2}|null"); }
	@Test public void test_1857() { checkNotSubtype("{{null f2} f2}","X<{X f1}|null>"); }
	@Test public void test_1858() { checkNotSubtype("{{null f2} f2}","X<{X f2}|null>"); }
	@Test public void test_1859() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1860() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_1861() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1862() { checkNotSubtype("X<{X|any f1}>","null"); }
	@Test public void test_1863() { checkIsSubtype("X<{X|any f1}>","{any f1}"); }
	@Test public void test_1864() { checkNotSubtype("X<{X|any f1}>","{any f2}"); }
	@Test public void test_1865() { checkIsSubtype("X<{X|any f1}>","{null f1}"); }
	@Test public void test_1866() { checkNotSubtype("X<{X|any f1}>","{null f2}"); }
	@Test public void test_1867() { checkNotSubtype("X<{X|any f1}>","(any,any)"); }
	@Test public void test_1868() { checkNotSubtype("X<{X|any f1}>","(any,null)"); }
	@Test public void test_1869() { checkIsSubtype("X<{X|any f1}>","(any,{any f1})"); }
	@Test public void test_1870() { checkIsSubtype("X<{X|any f1}>","(any,{any f2})"); }
	@Test public void test_1871() { checkNotSubtype("X<{X|any f1}>","X<(any,X|any)>"); }
	@Test public void test_1872() { checkNotSubtype("X<{X|any f1}>","(null,any)"); }
	@Test public void test_1873() { checkNotSubtype("X<{X|any f1}>","(null,null)"); }
	@Test public void test_1874() { checkIsSubtype("X<{X|any f1}>","(null,{null f1})"); }
	@Test public void test_1875() { checkIsSubtype("X<{X|any f1}>","(null,{null f2})"); }
	@Test public void test_1876() { checkNotSubtype("X<{X|any f1}>","X<(null,X|null)>"); }
	@Test public void test_1877() { checkNotSubtype("X<{X|any f1}>","({any f1},any)"); }
	@Test public void test_1878() { checkNotSubtype("X<{X|any f1}>","({any f2},any)"); }
	@Test public void test_1879() { checkNotSubtype("X<{X|any f1}>","({null f1},null)"); }
	@Test public void test_1880() { checkNotSubtype("X<{X|any f1}>","({null f2},null)"); }
	@Test public void test_1881() { checkNotSubtype("X<{X|any f1}>","X<(X|any,any)>"); }
	@Test public void test_1882() { checkNotSubtype("X<{X|any f1}>","X<(X|null,null)>"); }
	@Test public void test_1883() { checkIsSubtype("X<{X|any f1}>","{{any f1} f1}"); }
	@Test public void test_1884() { checkIsSubtype("X<{X|any f1}>","{{any f2} f1}"); }
	@Test public void test_1885() { checkNotSubtype("X<{X|any f1}>","{{any f1} f2}"); }
	@Test public void test_1886() { checkNotSubtype("X<{X|any f1}>","{{any f2} f2}"); }
	@Test public void test_1887() { checkIsSubtype("X<{X|any f1}>","{{null f1} f1}"); }
	@Test public void test_1888() { checkIsSubtype("X<{X|any f1}>","{{null f2} f1}"); }
	@Test public void test_1889() { checkNotSubtype("X<{X|any f1}>","{{null f1} f2}"); }
	@Test public void test_1890() { checkNotSubtype("X<{X|any f1}>","{{null f2} f2}"); }
	@Test public void test_1891() { checkIsSubtype("X<{X|any f1}>","X<{X|any f1}>"); }
	@Test public void test_1892() { checkNotSubtype("X<{X|any f1}>","X<{X|any f2}>"); }
	@Test public void test_1893() { checkIsSubtype("X<{X|any f1}>","X<{X|null f1}>"); }
	@Test public void test_1894() { checkNotSubtype("X<{X|any f1}>","X<{X|null f2}>"); }
	@Test public void test_1895() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1896() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1897() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1898() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1899() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1900() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1901() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1902() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1903() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1904() { checkNotSubtype("X<{X|any f1}>","null"); }
	@Test public void test_1905() { checkNotSubtype("X<{X|any f1}>","X<null|(X,null)>"); }
	@Test public void test_1906() { checkNotSubtype("X<{X|any f1}>","null|{null f1}"); }
	@Test public void test_1907() { checkNotSubtype("X<{X|any f1}>","null|{null f2}"); }
	@Test public void test_1908() { checkNotSubtype("X<{X|any f1}>","X<null|{X f1}>"); }
	@Test public void test_1909() { checkNotSubtype("X<{X|any f1}>","X<null|{X f2}>"); }
	@Test public void test_1910() { checkNotSubtype("X<{X|any f1}>","null"); }
	@Test public void test_1911() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1912() { checkNotSubtype("X<{X|any f1}>","X<(X,null)|null>"); }
	@Test public void test_1913() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1914() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1915() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1916() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1917() { checkNotSubtype("X<{X|any f1}>","{null f1}|null"); }
	@Test public void test_1918() { checkNotSubtype("X<{X|any f1}>","{null f2}|null"); }
	@Test public void test_1919() { checkNotSubtype("X<{X|any f1}>","X<{X f1}|null>"); }
	@Test public void test_1920() { checkNotSubtype("X<{X|any f1}>","X<{X f2}|null>"); }
	@Test public void test_1921() { checkNotSubtype("X<{X|any f1}>","any"); }
	@Test public void test_1922() { checkNotSubtype("X<{X|any f1}>","null"); }
	@Test public void test_1923() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1924() { checkNotSubtype("X<{X|any f2}>","null"); }
	@Test public void test_1925() { checkNotSubtype("X<{X|any f2}>","{any f1}"); }
	@Test public void test_1926() { checkIsSubtype("X<{X|any f2}>","{any f2}"); }
	@Test public void test_1927() { checkNotSubtype("X<{X|any f2}>","{null f1}"); }
	@Test public void test_1928() { checkIsSubtype("X<{X|any f2}>","{null f2}"); }
	@Test public void test_1929() { checkNotSubtype("X<{X|any f2}>","(any,any)"); }
	@Test public void test_1930() { checkNotSubtype("X<{X|any f2}>","(any,null)"); }
	@Test public void test_1931() { checkIsSubtype("X<{X|any f2}>","(any,{any f1})"); }
	@Test public void test_1932() { checkIsSubtype("X<{X|any f2}>","(any,{any f2})"); }
	@Test public void test_1933() { checkNotSubtype("X<{X|any f2}>","X<(any,X|any)>"); }
	@Test public void test_1934() { checkNotSubtype("X<{X|any f2}>","(null,any)"); }
	@Test public void test_1935() { checkNotSubtype("X<{X|any f2}>","(null,null)"); }
	@Test public void test_1936() { checkIsSubtype("X<{X|any f2}>","(null,{null f1})"); }
	@Test public void test_1937() { checkIsSubtype("X<{X|any f2}>","(null,{null f2})"); }
	@Test public void test_1938() { checkNotSubtype("X<{X|any f2}>","X<(null,X|null)>"); }
	@Test public void test_1939() { checkNotSubtype("X<{X|any f2}>","({any f1},any)"); }
	@Test public void test_1940() { checkNotSubtype("X<{X|any f2}>","({any f2},any)"); }
	@Test public void test_1941() { checkNotSubtype("X<{X|any f2}>","({null f1},null)"); }
	@Test public void test_1942() { checkNotSubtype("X<{X|any f2}>","({null f2},null)"); }
	@Test public void test_1943() { checkNotSubtype("X<{X|any f2}>","X<(X|any,any)>"); }
	@Test public void test_1944() { checkNotSubtype("X<{X|any f2}>","X<(X|null,null)>"); }
	@Test public void test_1945() { checkNotSubtype("X<{X|any f2}>","{{any f1} f1}"); }
	@Test public void test_1946() { checkNotSubtype("X<{X|any f2}>","{{any f2} f1}"); }
	@Test public void test_1947() { checkIsSubtype("X<{X|any f2}>","{{any f1} f2}"); }
	@Test public void test_1948() { checkIsSubtype("X<{X|any f2}>","{{any f2} f2}"); }
	@Test public void test_1949() { checkNotSubtype("X<{X|any f2}>","{{null f1} f1}"); }
	@Test public void test_1950() { checkNotSubtype("X<{X|any f2}>","{{null f2} f1}"); }
	@Test public void test_1951() { checkIsSubtype("X<{X|any f2}>","{{null f1} f2}"); }
	@Test public void test_1952() { checkIsSubtype("X<{X|any f2}>","{{null f2} f2}"); }
	@Test public void test_1953() { checkNotSubtype("X<{X|any f2}>","X<{X|any f1}>"); }
	@Test public void test_1954() { checkIsSubtype("X<{X|any f2}>","X<{X|any f2}>"); }
	@Test public void test_1955() { checkNotSubtype("X<{X|any f2}>","X<{X|null f1}>"); }
	@Test public void test_1956() { checkIsSubtype("X<{X|any f2}>","X<{X|null f2}>"); }
	@Test public void test_1957() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1958() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1959() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1960() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1961() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1962() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1963() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1964() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1965() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1966() { checkNotSubtype("X<{X|any f2}>","null"); }
	@Test public void test_1967() { checkNotSubtype("X<{X|any f2}>","X<null|(X,null)>"); }
	@Test public void test_1968() { checkNotSubtype("X<{X|any f2}>","null|{null f1}"); }
	@Test public void test_1969() { checkNotSubtype("X<{X|any f2}>","null|{null f2}"); }
	@Test public void test_1970() { checkNotSubtype("X<{X|any f2}>","X<null|{X f1}>"); }
	@Test public void test_1971() { checkNotSubtype("X<{X|any f2}>","X<null|{X f2}>"); }
	@Test public void test_1972() { checkNotSubtype("X<{X|any f2}>","null"); }
	@Test public void test_1973() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1974() { checkNotSubtype("X<{X|any f2}>","X<(X,null)|null>"); }
	@Test public void test_1975() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1976() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1977() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1978() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1979() { checkNotSubtype("X<{X|any f2}>","{null f1}|null"); }
	@Test public void test_1980() { checkNotSubtype("X<{X|any f2}>","{null f2}|null"); }
	@Test public void test_1981() { checkNotSubtype("X<{X|any f2}>","X<{X f1}|null>"); }
	@Test public void test_1982() { checkNotSubtype("X<{X|any f2}>","X<{X f2}|null>"); }
	@Test public void test_1983() { checkNotSubtype("X<{X|any f2}>","any"); }
	@Test public void test_1984() { checkNotSubtype("X<{X|any f2}>","null"); }
	@Test public void test_1985() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_1986() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test public void test_1987() { checkNotSubtype("X<{X|null f1}>","{any f1}"); }
	@Test public void test_1988() { checkNotSubtype("X<{X|null f1}>","{any f2}"); }
	@Test public void test_1989() { checkIsSubtype("X<{X|null f1}>","{null f1}"); }
	@Test public void test_1990() { checkNotSubtype("X<{X|null f1}>","{null f2}"); }
	@Test public void test_1991() { checkNotSubtype("X<{X|null f1}>","(any,any)"); }
	@Test public void test_1992() { checkNotSubtype("X<{X|null f1}>","(any,null)"); }
	@Test public void test_1993() { checkIsSubtype("X<{X|null f1}>","(any,{any f1})"); }
	@Test public void test_1994() { checkIsSubtype("X<{X|null f1}>","(any,{any f2})"); }
	@Test public void test_1995() { checkNotSubtype("X<{X|null f1}>","X<(any,X|any)>"); }
	@Test public void test_1996() { checkNotSubtype("X<{X|null f1}>","(null,any)"); }
	@Test public void test_1997() { checkNotSubtype("X<{X|null f1}>","(null,null)"); }
	@Test public void test_1998() { checkIsSubtype("X<{X|null f1}>","(null,{null f1})"); }
	@Test public void test_1999() { checkIsSubtype("X<{X|null f1}>","(null,{null f2})"); }
	@Test public void test_2000() { checkNotSubtype("X<{X|null f1}>","X<(null,X|null)>"); }
	@Test public void test_2001() { checkNotSubtype("X<{X|null f1}>","({any f1},any)"); }
	@Test public void test_2002() { checkNotSubtype("X<{X|null f1}>","({any f2},any)"); }
	@Test public void test_2003() { checkNotSubtype("X<{X|null f1}>","({null f1},null)"); }
	@Test public void test_2004() { checkNotSubtype("X<{X|null f1}>","({null f2},null)"); }
	@Test public void test_2005() { checkNotSubtype("X<{X|null f1}>","X<(X|any,any)>"); }
	@Test public void test_2006() { checkNotSubtype("X<{X|null f1}>","X<(X|null,null)>"); }
	@Test public void test_2007() { checkNotSubtype("X<{X|null f1}>","{{any f1} f1}"); }
	@Test public void test_2008() { checkNotSubtype("X<{X|null f1}>","{{any f2} f1}"); }
	@Test public void test_2009() { checkNotSubtype("X<{X|null f1}>","{{any f1} f2}"); }
	@Test public void test_2010() { checkNotSubtype("X<{X|null f1}>","{{any f2} f2}"); }
	@Test public void test_2011() { checkIsSubtype("X<{X|null f1}>","{{null f1} f1}"); }
	@Test public void test_2012() { checkNotSubtype("X<{X|null f1}>","{{null f2} f1}"); }
	@Test public void test_2013() { checkNotSubtype("X<{X|null f1}>","{{null f1} f2}"); }
	@Test public void test_2014() { checkNotSubtype("X<{X|null f1}>","{{null f2} f2}"); }
	@Test public void test_2015() { checkNotSubtype("X<{X|null f1}>","X<{X|any f1}>"); }
	@Test public void test_2016() { checkNotSubtype("X<{X|null f1}>","X<{X|any f2}>"); }
	@Test public void test_2017() { checkIsSubtype("X<{X|null f1}>","X<{X|null f1}>"); }
	@Test public void test_2018() { checkNotSubtype("X<{X|null f1}>","X<{X|null f2}>"); }
	@Test public void test_2019() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2020() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2021() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2022() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2023() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2024() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2025() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2026() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2027() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2028() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test public void test_2029() { checkNotSubtype("X<{X|null f1}>","X<null|(X,null)>"); }
	@Test public void test_2030() { checkNotSubtype("X<{X|null f1}>","null|{null f1}"); }
	@Test public void test_2031() { checkNotSubtype("X<{X|null f1}>","null|{null f2}"); }
	@Test public void test_2032() { checkNotSubtype("X<{X|null f1}>","X<null|{X f1}>"); }
	@Test public void test_2033() { checkNotSubtype("X<{X|null f1}>","X<null|{X f2}>"); }
	@Test public void test_2034() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test public void test_2035() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2036() { checkNotSubtype("X<{X|null f1}>","X<(X,null)|null>"); }
	@Test public void test_2037() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2038() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2039() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2040() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2041() { checkNotSubtype("X<{X|null f1}>","{null f1}|null"); }
	@Test public void test_2042() { checkNotSubtype("X<{X|null f1}>","{null f2}|null"); }
	@Test public void test_2043() { checkNotSubtype("X<{X|null f1}>","X<{X f1}|null>"); }
	@Test public void test_2044() { checkNotSubtype("X<{X|null f1}>","X<{X f2}|null>"); }
	@Test public void test_2045() { checkNotSubtype("X<{X|null f1}>","any"); }
	@Test public void test_2046() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test public void test_2047() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2048() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test public void test_2049() { checkNotSubtype("X<{X|null f2}>","{any f1}"); }
	@Test public void test_2050() { checkNotSubtype("X<{X|null f2}>","{any f2}"); }
	@Test public void test_2051() { checkNotSubtype("X<{X|null f2}>","{null f1}"); }
	@Test public void test_2052() { checkIsSubtype("X<{X|null f2}>","{null f2}"); }
	@Test public void test_2053() { checkNotSubtype("X<{X|null f2}>","(any,any)"); }
	@Test public void test_2054() { checkNotSubtype("X<{X|null f2}>","(any,null)"); }
	@Test public void test_2055() { checkIsSubtype("X<{X|null f2}>","(any,{any f1})"); }
	@Test public void test_2056() { checkIsSubtype("X<{X|null f2}>","(any,{any f2})"); }
	@Test public void test_2057() { checkNotSubtype("X<{X|null f2}>","X<(any,X|any)>"); }
	@Test public void test_2058() { checkNotSubtype("X<{X|null f2}>","(null,any)"); }
	@Test public void test_2059() { checkNotSubtype("X<{X|null f2}>","(null,null)"); }
	@Test public void test_2060() { checkIsSubtype("X<{X|null f2}>","(null,{null f1})"); }
	@Test public void test_2061() { checkIsSubtype("X<{X|null f2}>","(null,{null f2})"); }
	@Test public void test_2062() { checkNotSubtype("X<{X|null f2}>","X<(null,X|null)>"); }
	@Test public void test_2063() { checkNotSubtype("X<{X|null f2}>","({any f1},any)"); }
	@Test public void test_2064() { checkNotSubtype("X<{X|null f2}>","({any f2},any)"); }
	@Test public void test_2065() { checkNotSubtype("X<{X|null f2}>","({null f1},null)"); }
	@Test public void test_2066() { checkNotSubtype("X<{X|null f2}>","({null f2},null)"); }
	@Test public void test_2067() { checkNotSubtype("X<{X|null f2}>","X<(X|any,any)>"); }
	@Test public void test_2068() { checkNotSubtype("X<{X|null f2}>","X<(X|null,null)>"); }
	@Test public void test_2069() { checkNotSubtype("X<{X|null f2}>","{{any f1} f1}"); }
	@Test public void test_2070() { checkNotSubtype("X<{X|null f2}>","{{any f2} f1}"); }
	@Test public void test_2071() { checkNotSubtype("X<{X|null f2}>","{{any f1} f2}"); }
	@Test public void test_2072() { checkNotSubtype("X<{X|null f2}>","{{any f2} f2}"); }
	@Test public void test_2073() { checkNotSubtype("X<{X|null f2}>","{{null f1} f1}"); }
	@Test public void test_2074() { checkNotSubtype("X<{X|null f2}>","{{null f2} f1}"); }
	@Test public void test_2075() { checkNotSubtype("X<{X|null f2}>","{{null f1} f2}"); }
	@Test public void test_2076() { checkIsSubtype("X<{X|null f2}>","{{null f2} f2}"); }
	@Test public void test_2077() { checkNotSubtype("X<{X|null f2}>","X<{X|any f1}>"); }
	@Test public void test_2078() { checkNotSubtype("X<{X|null f2}>","X<{X|any f2}>"); }
	@Test public void test_2079() { checkNotSubtype("X<{X|null f2}>","X<{X|null f1}>"); }
	@Test public void test_2080() { checkIsSubtype("X<{X|null f2}>","X<{X|null f2}>"); }
	@Test public void test_2081() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2082() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2083() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2084() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2085() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2086() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2087() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2088() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2089() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2090() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test public void test_2091() { checkNotSubtype("X<{X|null f2}>","X<null|(X,null)>"); }
	@Test public void test_2092() { checkNotSubtype("X<{X|null f2}>","null|{null f1}"); }
	@Test public void test_2093() { checkNotSubtype("X<{X|null f2}>","null|{null f2}"); }
	@Test public void test_2094() { checkNotSubtype("X<{X|null f2}>","X<null|{X f1}>"); }
	@Test public void test_2095() { checkNotSubtype("X<{X|null f2}>","X<null|{X f2}>"); }
	@Test public void test_2096() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test public void test_2097() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2098() { checkNotSubtype("X<{X|null f2}>","X<(X,null)|null>"); }
	@Test public void test_2099() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2100() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2101() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2102() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2103() { checkNotSubtype("X<{X|null f2}>","{null f1}|null"); }
	@Test public void test_2104() { checkNotSubtype("X<{X|null f2}>","{null f2}|null"); }
	@Test public void test_2105() { checkNotSubtype("X<{X|null f2}>","X<{X f1}|null>"); }
	@Test public void test_2106() { checkNotSubtype("X<{X|null f2}>","X<{X f2}|null>"); }
	@Test public void test_2107() { checkNotSubtype("X<{X|null f2}>","any"); }
	@Test public void test_2108() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test public void test_2109() { checkIsSubtype("any","any"); }
	@Test public void test_2110() { checkIsSubtype("any","null"); }
	@Test public void test_2111() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2112() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2113() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2114() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2115() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2116() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2117() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_2118() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_2119() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_2120() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2121() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2122() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_2123() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_2124() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_2125() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2126() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2127() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2128() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2129() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_2130() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_2131() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2132() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2133() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2134() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2135() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2136() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2137() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2138() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2139() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_2140() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_2141() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_2142() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_2143() { checkIsSubtype("any","any"); }
	@Test public void test_2144() { checkIsSubtype("any","any"); }
	@Test public void test_2145() { checkIsSubtype("any","any"); }
	@Test public void test_2146() { checkIsSubtype("any","any"); }
	@Test public void test_2147() { checkIsSubtype("any","any"); }
	@Test public void test_2148() { checkIsSubtype("any","any"); }
	@Test public void test_2149() { checkIsSubtype("any","any"); }
	@Test public void test_2150() { checkIsSubtype("any","any"); }
	@Test public void test_2151() { checkIsSubtype("any","any"); }
	@Test public void test_2152() { checkIsSubtype("any","null"); }
	@Test public void test_2153() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_2154() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_2155() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_2156() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_2157() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_2158() { checkIsSubtype("any","null"); }
	@Test public void test_2159() { checkIsSubtype("any","any"); }
	@Test public void test_2160() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_2161() { checkIsSubtype("any","any"); }
	@Test public void test_2162() { checkIsSubtype("any","any"); }
	@Test public void test_2163() { checkIsSubtype("any","any"); }
	@Test public void test_2164() { checkIsSubtype("any","any"); }
	@Test public void test_2165() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2166() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2167() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_2168() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_2169() { checkIsSubtype("any","any"); }
	@Test public void test_2170() { checkIsSubtype("any","null"); }
	@Test public void test_2171() { checkIsSubtype("any","any"); }
	@Test public void test_2172() { checkIsSubtype("any","null"); }
	@Test public void test_2173() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2174() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2175() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2176() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2177() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2178() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2179() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_2180() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_2181() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_2182() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2183() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2184() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_2185() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_2186() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_2187() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2188() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2189() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2190() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2191() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_2192() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_2193() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2194() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2195() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2196() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2197() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2198() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2199() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2200() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2201() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_2202() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_2203() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_2204() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_2205() { checkIsSubtype("any","any"); }
	@Test public void test_2206() { checkIsSubtype("any","any"); }
	@Test public void test_2207() { checkIsSubtype("any","any"); }
	@Test public void test_2208() { checkIsSubtype("any","any"); }
	@Test public void test_2209() { checkIsSubtype("any","any"); }
	@Test public void test_2210() { checkIsSubtype("any","any"); }
	@Test public void test_2211() { checkIsSubtype("any","any"); }
	@Test public void test_2212() { checkIsSubtype("any","any"); }
	@Test public void test_2213() { checkIsSubtype("any","any"); }
	@Test public void test_2214() { checkIsSubtype("any","null"); }
	@Test public void test_2215() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_2216() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_2217() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_2218() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_2219() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_2220() { checkIsSubtype("any","null"); }
	@Test public void test_2221() { checkIsSubtype("any","any"); }
	@Test public void test_2222() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_2223() { checkIsSubtype("any","any"); }
	@Test public void test_2224() { checkIsSubtype("any","any"); }
	@Test public void test_2225() { checkIsSubtype("any","any"); }
	@Test public void test_2226() { checkIsSubtype("any","any"); }
	@Test public void test_2227() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2228() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2229() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_2230() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_2231() { checkIsSubtype("any","any"); }
	@Test public void test_2232() { checkIsSubtype("any","null"); }
	@Test public void test_2233() { checkIsSubtype("any","any"); }
	@Test public void test_2234() { checkIsSubtype("any","null"); }
	@Test public void test_2235() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2236() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2237() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2238() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2239() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2240() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2241() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_2242() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_2243() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_2244() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2245() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2246() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_2247() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_2248() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_2249() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2250() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2251() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2252() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2253() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_2254() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_2255() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2256() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2257() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2258() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2259() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2260() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2261() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2262() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2263() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_2264() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_2265() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_2266() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_2267() { checkIsSubtype("any","any"); }
	@Test public void test_2268() { checkIsSubtype("any","any"); }
	@Test public void test_2269() { checkIsSubtype("any","any"); }
	@Test public void test_2270() { checkIsSubtype("any","any"); }
	@Test public void test_2271() { checkIsSubtype("any","any"); }
	@Test public void test_2272() { checkIsSubtype("any","any"); }
	@Test public void test_2273() { checkIsSubtype("any","any"); }
	@Test public void test_2274() { checkIsSubtype("any","any"); }
	@Test public void test_2275() { checkIsSubtype("any","any"); }
	@Test public void test_2276() { checkIsSubtype("any","null"); }
	@Test public void test_2277() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_2278() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_2279() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_2280() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_2281() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_2282() { checkIsSubtype("any","null"); }
	@Test public void test_2283() { checkIsSubtype("any","any"); }
	@Test public void test_2284() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_2285() { checkIsSubtype("any","any"); }
	@Test public void test_2286() { checkIsSubtype("any","any"); }
	@Test public void test_2287() { checkIsSubtype("any","any"); }
	@Test public void test_2288() { checkIsSubtype("any","any"); }
	@Test public void test_2289() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2290() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2291() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_2292() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_2293() { checkIsSubtype("any","any"); }
	@Test public void test_2294() { checkIsSubtype("any","null"); }
	@Test public void test_2295() { checkIsSubtype("any","any"); }
	@Test public void test_2296() { checkIsSubtype("any","null"); }
	@Test public void test_2297() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2298() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2299() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2300() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2301() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2302() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2303() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_2304() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_2305() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_2306() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2307() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2308() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_2309() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_2310() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_2311() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2312() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2313() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2314() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2315() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_2316() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_2317() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2318() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2319() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2320() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2321() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2322() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2323() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2324() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2325() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_2326() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_2327() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_2328() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_2329() { checkIsSubtype("any","any"); }
	@Test public void test_2330() { checkIsSubtype("any","any"); }
	@Test public void test_2331() { checkIsSubtype("any","any"); }
	@Test public void test_2332() { checkIsSubtype("any","any"); }
	@Test public void test_2333() { checkIsSubtype("any","any"); }
	@Test public void test_2334() { checkIsSubtype("any","any"); }
	@Test public void test_2335() { checkIsSubtype("any","any"); }
	@Test public void test_2336() { checkIsSubtype("any","any"); }
	@Test public void test_2337() { checkIsSubtype("any","any"); }
	@Test public void test_2338() { checkIsSubtype("any","null"); }
	@Test public void test_2339() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_2340() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_2341() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_2342() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_2343() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_2344() { checkIsSubtype("any","null"); }
	@Test public void test_2345() { checkIsSubtype("any","any"); }
	@Test public void test_2346() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_2347() { checkIsSubtype("any","any"); }
	@Test public void test_2348() { checkIsSubtype("any","any"); }
	@Test public void test_2349() { checkIsSubtype("any","any"); }
	@Test public void test_2350() { checkIsSubtype("any","any"); }
	@Test public void test_2351() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2352() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2353() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_2354() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_2355() { checkIsSubtype("any","any"); }
	@Test public void test_2356() { checkIsSubtype("any","null"); }
	@Test public void test_2357() { checkIsSubtype("any","any"); }
	@Test public void test_2358() { checkIsSubtype("any","null"); }
	@Test public void test_2359() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2360() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2361() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2362() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2363() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2364() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2365() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_2366() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_2367() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_2368() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2369() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2370() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_2371() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_2372() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_2373() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2374() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2375() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2376() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2377() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_2378() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_2379() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2380() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2381() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2382() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2383() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2384() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2385() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2386() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2387() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_2388() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_2389() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_2390() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_2391() { checkIsSubtype("any","any"); }
	@Test public void test_2392() { checkIsSubtype("any","any"); }
	@Test public void test_2393() { checkIsSubtype("any","any"); }
	@Test public void test_2394() { checkIsSubtype("any","any"); }
	@Test public void test_2395() { checkIsSubtype("any","any"); }
	@Test public void test_2396() { checkIsSubtype("any","any"); }
	@Test public void test_2397() { checkIsSubtype("any","any"); }
	@Test public void test_2398() { checkIsSubtype("any","any"); }
	@Test public void test_2399() { checkIsSubtype("any","any"); }
	@Test public void test_2400() { checkIsSubtype("any","null"); }
	@Test public void test_2401() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_2402() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_2403() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_2404() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_2405() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_2406() { checkIsSubtype("any","null"); }
	@Test public void test_2407() { checkIsSubtype("any","any"); }
	@Test public void test_2408() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_2409() { checkIsSubtype("any","any"); }
	@Test public void test_2410() { checkIsSubtype("any","any"); }
	@Test public void test_2411() { checkIsSubtype("any","any"); }
	@Test public void test_2412() { checkIsSubtype("any","any"); }
	@Test public void test_2413() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2414() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2415() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_2416() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_2417() { checkIsSubtype("any","any"); }
	@Test public void test_2418() { checkIsSubtype("any","null"); }
	@Test public void test_2419() { checkIsSubtype("any","any"); }
	@Test public void test_2420() { checkIsSubtype("any","null"); }
	@Test public void test_2421() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2422() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2423() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2424() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2425() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2426() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2427() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_2428() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_2429() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_2430() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2431() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2432() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_2433() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_2434() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_2435() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2436() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2437() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2438() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2439() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_2440() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_2441() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2442() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2443() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2444() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2445() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2446() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2447() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2448() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2449() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_2450() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_2451() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_2452() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_2453() { checkIsSubtype("any","any"); }
	@Test public void test_2454() { checkIsSubtype("any","any"); }
	@Test public void test_2455() { checkIsSubtype("any","any"); }
	@Test public void test_2456() { checkIsSubtype("any","any"); }
	@Test public void test_2457() { checkIsSubtype("any","any"); }
	@Test public void test_2458() { checkIsSubtype("any","any"); }
	@Test public void test_2459() { checkIsSubtype("any","any"); }
	@Test public void test_2460() { checkIsSubtype("any","any"); }
	@Test public void test_2461() { checkIsSubtype("any","any"); }
	@Test public void test_2462() { checkIsSubtype("any","null"); }
	@Test public void test_2463() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_2464() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_2465() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_2466() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_2467() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_2468() { checkIsSubtype("any","null"); }
	@Test public void test_2469() { checkIsSubtype("any","any"); }
	@Test public void test_2470() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_2471() { checkIsSubtype("any","any"); }
	@Test public void test_2472() { checkIsSubtype("any","any"); }
	@Test public void test_2473() { checkIsSubtype("any","any"); }
	@Test public void test_2474() { checkIsSubtype("any","any"); }
	@Test public void test_2475() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2476() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2477() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_2478() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_2479() { checkIsSubtype("any","any"); }
	@Test public void test_2480() { checkIsSubtype("any","null"); }
	@Test public void test_2481() { checkIsSubtype("any","any"); }
	@Test public void test_2482() { checkIsSubtype("any","null"); }
	@Test public void test_2483() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2484() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2485() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2486() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2487() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2488() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2489() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_2490() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_2491() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_2492() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2493() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2494() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_2495() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_2496() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_2497() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2498() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2499() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2500() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2501() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_2502() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_2503() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2504() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2505() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2506() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2507() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2508() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2509() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2510() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2511() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_2512() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_2513() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_2514() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_2515() { checkIsSubtype("any","any"); }
	@Test public void test_2516() { checkIsSubtype("any","any"); }
	@Test public void test_2517() { checkIsSubtype("any","any"); }
	@Test public void test_2518() { checkIsSubtype("any","any"); }
	@Test public void test_2519() { checkIsSubtype("any","any"); }
	@Test public void test_2520() { checkIsSubtype("any","any"); }
	@Test public void test_2521() { checkIsSubtype("any","any"); }
	@Test public void test_2522() { checkIsSubtype("any","any"); }
	@Test public void test_2523() { checkIsSubtype("any","any"); }
	@Test public void test_2524() { checkIsSubtype("any","null"); }
	@Test public void test_2525() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_2526() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_2527() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_2528() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_2529() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_2530() { checkIsSubtype("any","null"); }
	@Test public void test_2531() { checkIsSubtype("any","any"); }
	@Test public void test_2532() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_2533() { checkIsSubtype("any","any"); }
	@Test public void test_2534() { checkIsSubtype("any","any"); }
	@Test public void test_2535() { checkIsSubtype("any","any"); }
	@Test public void test_2536() { checkIsSubtype("any","any"); }
	@Test public void test_2537() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2538() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2539() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_2540() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_2541() { checkIsSubtype("any","any"); }
	@Test public void test_2542() { checkIsSubtype("any","null"); }
	@Test public void test_2543() { checkIsSubtype("any","any"); }
	@Test public void test_2544() { checkIsSubtype("any","null"); }
	@Test public void test_2545() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2546() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2547() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2548() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2549() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2550() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2551() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_2552() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_2553() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_2554() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2555() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2556() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_2557() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_2558() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_2559() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2560() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2561() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2562() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2563() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_2564() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_2565() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2566() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2567() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2568() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2569() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2570() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2571() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2572() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2573() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_2574() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_2575() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_2576() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_2577() { checkIsSubtype("any","any"); }
	@Test public void test_2578() { checkIsSubtype("any","any"); }
	@Test public void test_2579() { checkIsSubtype("any","any"); }
	@Test public void test_2580() { checkIsSubtype("any","any"); }
	@Test public void test_2581() { checkIsSubtype("any","any"); }
	@Test public void test_2582() { checkIsSubtype("any","any"); }
	@Test public void test_2583() { checkIsSubtype("any","any"); }
	@Test public void test_2584() { checkIsSubtype("any","any"); }
	@Test public void test_2585() { checkIsSubtype("any","any"); }
	@Test public void test_2586() { checkIsSubtype("any","null"); }
	@Test public void test_2587() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_2588() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_2589() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_2590() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_2591() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_2592() { checkIsSubtype("any","null"); }
	@Test public void test_2593() { checkIsSubtype("any","any"); }
	@Test public void test_2594() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_2595() { checkIsSubtype("any","any"); }
	@Test public void test_2596() { checkIsSubtype("any","any"); }
	@Test public void test_2597() { checkIsSubtype("any","any"); }
	@Test public void test_2598() { checkIsSubtype("any","any"); }
	@Test public void test_2599() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2600() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2601() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_2602() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_2603() { checkIsSubtype("any","any"); }
	@Test public void test_2604() { checkIsSubtype("any","null"); }
	@Test public void test_2605() { checkIsSubtype("any","any"); }
	@Test public void test_2606() { checkIsSubtype("any","null"); }
	@Test public void test_2607() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_2608() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_2609() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_2610() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_2611() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_2612() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_2613() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_2614() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_2615() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_2616() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_2617() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_2618() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_2619() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_2620() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_2621() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_2622() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_2623() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_2624() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_2625() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_2626() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_2627() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_2628() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_2629() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_2630() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_2631() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_2632() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_2633() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_2634() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_2635() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_2636() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_2637() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_2638() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_2639() { checkIsSubtype("any","any"); }
	@Test public void test_2640() { checkIsSubtype("any","any"); }
	@Test public void test_2641() { checkIsSubtype("any","any"); }
	@Test public void test_2642() { checkIsSubtype("any","any"); }
	@Test public void test_2643() { checkIsSubtype("any","any"); }
	@Test public void test_2644() { checkIsSubtype("any","any"); }
	@Test public void test_2645() { checkIsSubtype("any","any"); }
	@Test public void test_2646() { checkIsSubtype("any","any"); }
	@Test public void test_2647() { checkIsSubtype("any","any"); }
	@Test public void test_2648() { checkIsSubtype("any","null"); }
	@Test public void test_2649() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_2650() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_2651() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_2652() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_2653() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_2654() { checkIsSubtype("any","null"); }
	@Test public void test_2655() { checkIsSubtype("any","any"); }
	@Test public void test_2656() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_2657() { checkIsSubtype("any","any"); }
	@Test public void test_2658() { checkIsSubtype("any","any"); }
	@Test public void test_2659() { checkIsSubtype("any","any"); }
	@Test public void test_2660() { checkIsSubtype("any","any"); }
	@Test public void test_2661() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_2662() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_2663() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_2664() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_2665() { checkIsSubtype("any","any"); }
	@Test public void test_2666() { checkIsSubtype("any","null"); }
	@Test public void test_2667() { checkNotSubtype("null","any"); }
	@Test public void test_2668() { checkIsSubtype("null","null"); }
	@Test public void test_2669() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_2670() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_2671() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_2672() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_2673() { checkNotSubtype("null","(any,any)"); }
	@Test public void test_2674() { checkNotSubtype("null","(any,null)"); }
	@Test public void test_2675() { checkIsSubtype("null","(any,{any f1})"); }
	@Test public void test_2676() { checkIsSubtype("null","(any,{any f2})"); }
	@Test public void test_2677() { checkNotSubtype("null","X<(any,X|any)>"); }
	@Test public void test_2678() { checkNotSubtype("null","(null,any)"); }
	@Test public void test_2679() { checkNotSubtype("null","(null,null)"); }
	@Test public void test_2680() { checkIsSubtype("null","(null,{null f1})"); }
	@Test public void test_2681() { checkIsSubtype("null","(null,{null f2})"); }
	@Test public void test_2682() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test public void test_2683() { checkNotSubtype("null","({any f1},any)"); }
	@Test public void test_2684() { checkNotSubtype("null","({any f2},any)"); }
	@Test public void test_2685() { checkNotSubtype("null","({null f1},null)"); }
	@Test public void test_2686() { checkNotSubtype("null","({null f2},null)"); }
	@Test public void test_2687() { checkNotSubtype("null","X<(X|any,any)>"); }
	@Test public void test_2688() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test public void test_2689() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_2690() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_2691() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_2692() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_2693() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_2694() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_2695() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_2696() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_2697() { checkNotSubtype("null","X<{X|any f1}>"); }
	@Test public void test_2698() { checkNotSubtype("null","X<{X|any f2}>"); }
	@Test public void test_2699() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test public void test_2700() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test public void test_2701() { checkNotSubtype("null","any"); }
	@Test public void test_2702() { checkNotSubtype("null","any"); }
	@Test public void test_2703() { checkNotSubtype("null","any"); }
	@Test public void test_2704() { checkNotSubtype("null","any"); }
	@Test public void test_2705() { checkNotSubtype("null","any"); }
	@Test public void test_2706() { checkNotSubtype("null","any"); }
	@Test public void test_2707() { checkNotSubtype("null","any"); }
	@Test public void test_2708() { checkNotSubtype("null","any"); }
	@Test public void test_2709() { checkNotSubtype("null","any"); }
	@Test public void test_2710() { checkIsSubtype("null","null"); }
	@Test public void test_2711() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test public void test_2712() { checkNotSubtype("null","null|{null f1}"); }
	@Test public void test_2713() { checkNotSubtype("null","null|{null f2}"); }
	@Test public void test_2714() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test public void test_2715() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test public void test_2716() { checkIsSubtype("null","null"); }
	@Test public void test_2717() { checkNotSubtype("null","any"); }
	@Test public void test_2718() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test public void test_2719() { checkNotSubtype("null","any"); }
	@Test public void test_2720() { checkNotSubtype("null","any"); }
	@Test public void test_2721() { checkNotSubtype("null","any"); }
	@Test public void test_2722() { checkNotSubtype("null","any"); }
	@Test public void test_2723() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_2724() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_2725() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test public void test_2726() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test public void test_2727() { checkNotSubtype("null","any"); }
	@Test public void test_2728() { checkIsSubtype("null","null"); }
	@Test public void test_2729() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2730() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test public void test_2731() { checkNotSubtype("X<null|(X,null)>","{any f1}"); }
	@Test public void test_2732() { checkNotSubtype("X<null|(X,null)>","{any f2}"); }
	@Test public void test_2733() { checkNotSubtype("X<null|(X,null)>","{null f1}"); }
	@Test public void test_2734() { checkNotSubtype("X<null|(X,null)>","{null f2}"); }
	@Test public void test_2735() { checkNotSubtype("X<null|(X,null)>","(any,any)"); }
	@Test public void test_2736() { checkNotSubtype("X<null|(X,null)>","(any,null)"); }
	@Test public void test_2737() { checkIsSubtype("X<null|(X,null)>","(any,{any f1})"); }
	@Test public void test_2738() { checkIsSubtype("X<null|(X,null)>","(any,{any f2})"); }
	@Test public void test_2739() { checkNotSubtype("X<null|(X,null)>","X<(any,X|any)>"); }
	@Test public void test_2740() { checkNotSubtype("X<null|(X,null)>","(null,any)"); }
	@Test public void test_2741() { checkIsSubtype("X<null|(X,null)>","(null,null)"); }
	@Test public void test_2742() { checkIsSubtype("X<null|(X,null)>","(null,{null f1})"); }
	@Test public void test_2743() { checkIsSubtype("X<null|(X,null)>","(null,{null f2})"); }
	@Test public void test_2744() { checkIsSubtype("X<null|(X,null)>","X<(null,X|null)>"); }
	@Test public void test_2745() { checkNotSubtype("X<null|(X,null)>","({any f1},any)"); }
	@Test public void test_2746() { checkNotSubtype("X<null|(X,null)>","({any f2},any)"); }
	@Test public void test_2747() { checkNotSubtype("X<null|(X,null)>","({null f1},null)"); }
	@Test public void test_2748() { checkNotSubtype("X<null|(X,null)>","({null f2},null)"); }
	@Test public void test_2749() { checkNotSubtype("X<null|(X,null)>","X<(X|any,any)>"); }
	@Test public void test_2750() { checkIsSubtype("X<null|(X,null)>","X<(X|null,null)>"); }
	@Test public void test_2751() { checkNotSubtype("X<null|(X,null)>","{{any f1} f1}"); }
	@Test public void test_2752() { checkNotSubtype("X<null|(X,null)>","{{any f2} f1}"); }
	@Test public void test_2753() { checkNotSubtype("X<null|(X,null)>","{{any f1} f2}"); }
	@Test public void test_2754() { checkNotSubtype("X<null|(X,null)>","{{any f2} f2}"); }
	@Test public void test_2755() { checkNotSubtype("X<null|(X,null)>","{{null f1} f1}"); }
	@Test public void test_2756() { checkNotSubtype("X<null|(X,null)>","{{null f2} f1}"); }
	@Test public void test_2757() { checkNotSubtype("X<null|(X,null)>","{{null f1} f2}"); }
	@Test public void test_2758() { checkNotSubtype("X<null|(X,null)>","{{null f2} f2}"); }
	@Test public void test_2759() { checkNotSubtype("X<null|(X,null)>","X<{X|any f1}>"); }
	@Test public void test_2760() { checkNotSubtype("X<null|(X,null)>","X<{X|any f2}>"); }
	@Test public void test_2761() { checkNotSubtype("X<null|(X,null)>","X<{X|null f1}>"); }
	@Test public void test_2762() { checkNotSubtype("X<null|(X,null)>","X<{X|null f2}>"); }
	@Test public void test_2763() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2764() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2765() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2766() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2767() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2768() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2769() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2770() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2771() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2772() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test public void test_2773() { checkIsSubtype("X<null|(X,null)>","X<null|(X,null)>"); }
	@Test public void test_2774() { checkNotSubtype("X<null|(X,null)>","null|{null f1}"); }
	@Test public void test_2775() { checkNotSubtype("X<null|(X,null)>","null|{null f2}"); }
	@Test public void test_2776() { checkNotSubtype("X<null|(X,null)>","X<null|{X f1}>"); }
	@Test public void test_2777() { checkNotSubtype("X<null|(X,null)>","X<null|{X f2}>"); }
	@Test public void test_2778() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test public void test_2779() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2780() { checkIsSubtype("X<null|(X,null)>","X<(X,null)|null>"); }
	@Test public void test_2781() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2782() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2783() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2784() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2785() { checkNotSubtype("X<null|(X,null)>","{null f1}|null"); }
	@Test public void test_2786() { checkNotSubtype("X<null|(X,null)>","{null f2}|null"); }
	@Test public void test_2787() { checkNotSubtype("X<null|(X,null)>","X<{X f1}|null>"); }
	@Test public void test_2788() { checkNotSubtype("X<null|(X,null)>","X<{X f2}|null>"); }
	@Test public void test_2789() { checkNotSubtype("X<null|(X,null)>","any"); }
	@Test public void test_2790() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test public void test_2791() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2792() { checkIsSubtype("null|{null f1}","null"); }
	@Test public void test_2793() { checkNotSubtype("null|{null f1}","{any f1}"); }
	@Test public void test_2794() { checkNotSubtype("null|{null f1}","{any f2}"); }
	@Test public void test_2795() { checkIsSubtype("null|{null f1}","{null f1}"); }
	@Test public void test_2796() { checkNotSubtype("null|{null f1}","{null f2}"); }
	@Test public void test_2797() { checkNotSubtype("null|{null f1}","(any,any)"); }
	@Test public void test_2798() { checkNotSubtype("null|{null f1}","(any,null)"); }
	@Test public void test_2799() { checkIsSubtype("null|{null f1}","(any,{any f1})"); }
	@Test public void test_2800() { checkIsSubtype("null|{null f1}","(any,{any f2})"); }
	@Test public void test_2801() { checkNotSubtype("null|{null f1}","X<(any,X|any)>"); }
	@Test public void test_2802() { checkNotSubtype("null|{null f1}","(null,any)"); }
	@Test public void test_2803() { checkNotSubtype("null|{null f1}","(null,null)"); }
	@Test public void test_2804() { checkIsSubtype("null|{null f1}","(null,{null f1})"); }
	@Test public void test_2805() { checkIsSubtype("null|{null f1}","(null,{null f2})"); }
	@Test public void test_2806() { checkNotSubtype("null|{null f1}","X<(null,X|null)>"); }
	@Test public void test_2807() { checkNotSubtype("null|{null f1}","({any f1},any)"); }
	@Test public void test_2808() { checkNotSubtype("null|{null f1}","({any f2},any)"); }
	@Test public void test_2809() { checkNotSubtype("null|{null f1}","({null f1},null)"); }
	@Test public void test_2810() { checkNotSubtype("null|{null f1}","({null f2},null)"); }
	@Test public void test_2811() { checkNotSubtype("null|{null f1}","X<(X|any,any)>"); }
	@Test public void test_2812() { checkNotSubtype("null|{null f1}","X<(X|null,null)>"); }
	@Test public void test_2813() { checkNotSubtype("null|{null f1}","{{any f1} f1}"); }
	@Test public void test_2814() { checkNotSubtype("null|{null f1}","{{any f2} f1}"); }
	@Test public void test_2815() { checkNotSubtype("null|{null f1}","{{any f1} f2}"); }
	@Test public void test_2816() { checkNotSubtype("null|{null f1}","{{any f2} f2}"); }
	@Test public void test_2817() { checkNotSubtype("null|{null f1}","{{null f1} f1}"); }
	@Test public void test_2818() { checkNotSubtype("null|{null f1}","{{null f2} f1}"); }
	@Test public void test_2819() { checkNotSubtype("null|{null f1}","{{null f1} f2}"); }
	@Test public void test_2820() { checkNotSubtype("null|{null f1}","{{null f2} f2}"); }
	@Test public void test_2821() { checkNotSubtype("null|{null f1}","X<{X|any f1}>"); }
	@Test public void test_2822() { checkNotSubtype("null|{null f1}","X<{X|any f2}>"); }
	@Test public void test_2823() { checkNotSubtype("null|{null f1}","X<{X|null f1}>"); }
	@Test public void test_2824() { checkNotSubtype("null|{null f1}","X<{X|null f2}>"); }
	@Test public void test_2825() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2826() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2827() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2828() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2829() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2830() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2831() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2832() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2833() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2834() { checkIsSubtype("null|{null f1}","null"); }
	@Test public void test_2835() { checkNotSubtype("null|{null f1}","X<null|(X,null)>"); }
	@Test public void test_2836() { checkIsSubtype("null|{null f1}","null|{null f1}"); }
	@Test public void test_2837() { checkNotSubtype("null|{null f1}","null|{null f2}"); }
	@Test public void test_2838() { checkNotSubtype("null|{null f1}","X<null|{X f1}>"); }
	@Test public void test_2839() { checkNotSubtype("null|{null f1}","X<null|{X f2}>"); }
	@Test public void test_2840() { checkIsSubtype("null|{null f1}","null"); }
	@Test public void test_2841() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2842() { checkNotSubtype("null|{null f1}","X<(X,null)|null>"); }
	@Test public void test_2843() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2844() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2845() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2846() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2847() { checkIsSubtype("null|{null f1}","{null f1}|null"); }
	@Test public void test_2848() { checkNotSubtype("null|{null f1}","{null f2}|null"); }
	@Test public void test_2849() { checkNotSubtype("null|{null f1}","X<{X f1}|null>"); }
	@Test public void test_2850() { checkNotSubtype("null|{null f1}","X<{X f2}|null>"); }
	@Test public void test_2851() { checkNotSubtype("null|{null f1}","any"); }
	@Test public void test_2852() { checkIsSubtype("null|{null f1}","null"); }
	@Test public void test_2853() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2854() { checkIsSubtype("null|{null f2}","null"); }
	@Test public void test_2855() { checkNotSubtype("null|{null f2}","{any f1}"); }
	@Test public void test_2856() { checkNotSubtype("null|{null f2}","{any f2}"); }
	@Test public void test_2857() { checkNotSubtype("null|{null f2}","{null f1}"); }
	@Test public void test_2858() { checkIsSubtype("null|{null f2}","{null f2}"); }
	@Test public void test_2859() { checkNotSubtype("null|{null f2}","(any,any)"); }
	@Test public void test_2860() { checkNotSubtype("null|{null f2}","(any,null)"); }
	@Test public void test_2861() { checkIsSubtype("null|{null f2}","(any,{any f1})"); }
	@Test public void test_2862() { checkIsSubtype("null|{null f2}","(any,{any f2})"); }
	@Test public void test_2863() { checkNotSubtype("null|{null f2}","X<(any,X|any)>"); }
	@Test public void test_2864() { checkNotSubtype("null|{null f2}","(null,any)"); }
	@Test public void test_2865() { checkNotSubtype("null|{null f2}","(null,null)"); }
	@Test public void test_2866() { checkIsSubtype("null|{null f2}","(null,{null f1})"); }
	@Test public void test_2867() { checkIsSubtype("null|{null f2}","(null,{null f2})"); }
	@Test public void test_2868() { checkNotSubtype("null|{null f2}","X<(null,X|null)>"); }
	@Test public void test_2869() { checkNotSubtype("null|{null f2}","({any f1},any)"); }
	@Test public void test_2870() { checkNotSubtype("null|{null f2}","({any f2},any)"); }
	@Test public void test_2871() { checkNotSubtype("null|{null f2}","({null f1},null)"); }
	@Test public void test_2872() { checkNotSubtype("null|{null f2}","({null f2},null)"); }
	@Test public void test_2873() { checkNotSubtype("null|{null f2}","X<(X|any,any)>"); }
	@Test public void test_2874() { checkNotSubtype("null|{null f2}","X<(X|null,null)>"); }
	@Test public void test_2875() { checkNotSubtype("null|{null f2}","{{any f1} f1}"); }
	@Test public void test_2876() { checkNotSubtype("null|{null f2}","{{any f2} f1}"); }
	@Test public void test_2877() { checkNotSubtype("null|{null f2}","{{any f1} f2}"); }
	@Test public void test_2878() { checkNotSubtype("null|{null f2}","{{any f2} f2}"); }
	@Test public void test_2879() { checkNotSubtype("null|{null f2}","{{null f1} f1}"); }
	@Test public void test_2880() { checkNotSubtype("null|{null f2}","{{null f2} f1}"); }
	@Test public void test_2881() { checkNotSubtype("null|{null f2}","{{null f1} f2}"); }
	@Test public void test_2882() { checkNotSubtype("null|{null f2}","{{null f2} f2}"); }
	@Test public void test_2883() { checkNotSubtype("null|{null f2}","X<{X|any f1}>"); }
	@Test public void test_2884() { checkNotSubtype("null|{null f2}","X<{X|any f2}>"); }
	@Test public void test_2885() { checkNotSubtype("null|{null f2}","X<{X|null f1}>"); }
	@Test public void test_2886() { checkNotSubtype("null|{null f2}","X<{X|null f2}>"); }
	@Test public void test_2887() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2888() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2889() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2890() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2891() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2892() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2893() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2894() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2895() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2896() { checkIsSubtype("null|{null f2}","null"); }
	@Test public void test_2897() { checkNotSubtype("null|{null f2}","X<null|(X,null)>"); }
	@Test public void test_2898() { checkNotSubtype("null|{null f2}","null|{null f1}"); }
	@Test public void test_2899() { checkIsSubtype("null|{null f2}","null|{null f2}"); }
	@Test public void test_2900() { checkNotSubtype("null|{null f2}","X<null|{X f1}>"); }
	@Test public void test_2901() { checkNotSubtype("null|{null f2}","X<null|{X f2}>"); }
	@Test public void test_2902() { checkIsSubtype("null|{null f2}","null"); }
	@Test public void test_2903() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2904() { checkNotSubtype("null|{null f2}","X<(X,null)|null>"); }
	@Test public void test_2905() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2906() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2907() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2908() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2909() { checkNotSubtype("null|{null f2}","{null f1}|null"); }
	@Test public void test_2910() { checkIsSubtype("null|{null f2}","{null f2}|null"); }
	@Test public void test_2911() { checkNotSubtype("null|{null f2}","X<{X f1}|null>"); }
	@Test public void test_2912() { checkNotSubtype("null|{null f2}","X<{X f2}|null>"); }
	@Test public void test_2913() { checkNotSubtype("null|{null f2}","any"); }
	@Test public void test_2914() { checkIsSubtype("null|{null f2}","null"); }
	@Test public void test_2915() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2916() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test public void test_2917() { checkNotSubtype("X<null|{X f1}>","{any f1}"); }
	@Test public void test_2918() { checkNotSubtype("X<null|{X f1}>","{any f2}"); }
	@Test public void test_2919() { checkIsSubtype("X<null|{X f1}>","{null f1}"); }
	@Test public void test_2920() { checkNotSubtype("X<null|{X f1}>","{null f2}"); }
	@Test public void test_2921() { checkNotSubtype("X<null|{X f1}>","(any,any)"); }
	@Test public void test_2922() { checkNotSubtype("X<null|{X f1}>","(any,null)"); }
	@Test public void test_2923() { checkIsSubtype("X<null|{X f1}>","(any,{any f1})"); }
	@Test public void test_2924() { checkIsSubtype("X<null|{X f1}>","(any,{any f2})"); }
	@Test public void test_2925() { checkNotSubtype("X<null|{X f1}>","X<(any,X|any)>"); }
	@Test public void test_2926() { checkNotSubtype("X<null|{X f1}>","(null,any)"); }
	@Test public void test_2927() { checkNotSubtype("X<null|{X f1}>","(null,null)"); }
	@Test public void test_2928() { checkIsSubtype("X<null|{X f1}>","(null,{null f1})"); }
	@Test public void test_2929() { checkIsSubtype("X<null|{X f1}>","(null,{null f2})"); }
	@Test public void test_2930() { checkNotSubtype("X<null|{X f1}>","X<(null,X|null)>"); }
	@Test public void test_2931() { checkNotSubtype("X<null|{X f1}>","({any f1},any)"); }
	@Test public void test_2932() { checkNotSubtype("X<null|{X f1}>","({any f2},any)"); }
	@Test public void test_2933() { checkNotSubtype("X<null|{X f1}>","({null f1},null)"); }
	@Test public void test_2934() { checkNotSubtype("X<null|{X f1}>","({null f2},null)"); }
	@Test public void test_2935() { checkNotSubtype("X<null|{X f1}>","X<(X|any,any)>"); }
	@Test public void test_2936() { checkNotSubtype("X<null|{X f1}>","X<(X|null,null)>"); }
	@Test public void test_2937() { checkNotSubtype("X<null|{X f1}>","{{any f1} f1}"); }
	@Test public void test_2938() { checkNotSubtype("X<null|{X f1}>","{{any f2} f1}"); }
	@Test public void test_2939() { checkNotSubtype("X<null|{X f1}>","{{any f1} f2}"); }
	@Test public void test_2940() { checkNotSubtype("X<null|{X f1}>","{{any f2} f2}"); }
	@Test public void test_2941() { checkIsSubtype("X<null|{X f1}>","{{null f1} f1}"); }
	@Test public void test_2942() { checkNotSubtype("X<null|{X f1}>","{{null f2} f1}"); }
	@Test public void test_2943() { checkNotSubtype("X<null|{X f1}>","{{null f1} f2}"); }
	@Test public void test_2944() { checkNotSubtype("X<null|{X f1}>","{{null f2} f2}"); }
	@Test public void test_2945() { checkNotSubtype("X<null|{X f1}>","X<{X|any f1}>"); }
	@Test public void test_2946() { checkNotSubtype("X<null|{X f1}>","X<{X|any f2}>"); }
	@Test public void test_2947() { checkIsSubtype("X<null|{X f1}>","X<{X|null f1}>"); }
	@Test public void test_2948() { checkNotSubtype("X<null|{X f1}>","X<{X|null f2}>"); }
	@Test public void test_2949() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2950() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2951() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2952() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2953() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2954() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2955() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2956() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2957() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2958() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test public void test_2959() { checkNotSubtype("X<null|{X f1}>","X<null|(X,null)>"); }
	@Test public void test_2960() { checkIsSubtype("X<null|{X f1}>","null|{null f1}"); }
	@Test public void test_2961() { checkNotSubtype("X<null|{X f1}>","null|{null f2}"); }
	@Test public void test_2962() { checkIsSubtype("X<null|{X f1}>","X<null|{X f1}>"); }
	@Test public void test_2963() { checkNotSubtype("X<null|{X f1}>","X<null|{X f2}>"); }
	@Test public void test_2964() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test public void test_2965() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2966() { checkNotSubtype("X<null|{X f1}>","X<(X,null)|null>"); }
	@Test public void test_2967() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2968() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2969() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2970() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2971() { checkIsSubtype("X<null|{X f1}>","{null f1}|null"); }
	@Test public void test_2972() { checkNotSubtype("X<null|{X f1}>","{null f2}|null"); }
	@Test public void test_2973() { checkIsSubtype("X<null|{X f1}>","X<{X f1}|null>"); }
	@Test public void test_2974() { checkNotSubtype("X<null|{X f1}>","X<{X f2}|null>"); }
	@Test public void test_2975() { checkNotSubtype("X<null|{X f1}>","any"); }
	@Test public void test_2976() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test public void test_2977() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_2978() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test public void test_2979() { checkNotSubtype("X<null|{X f2}>","{any f1}"); }
	@Test public void test_2980() { checkNotSubtype("X<null|{X f2}>","{any f2}"); }
	@Test public void test_2981() { checkNotSubtype("X<null|{X f2}>","{null f1}"); }
	@Test public void test_2982() { checkIsSubtype("X<null|{X f2}>","{null f2}"); }
	@Test public void test_2983() { checkNotSubtype("X<null|{X f2}>","(any,any)"); }
	@Test public void test_2984() { checkNotSubtype("X<null|{X f2}>","(any,null)"); }
	@Test public void test_2985() { checkIsSubtype("X<null|{X f2}>","(any,{any f1})"); }
	@Test public void test_2986() { checkIsSubtype("X<null|{X f2}>","(any,{any f2})"); }
	@Test public void test_2987() { checkNotSubtype("X<null|{X f2}>","X<(any,X|any)>"); }
	@Test public void test_2988() { checkNotSubtype("X<null|{X f2}>","(null,any)"); }
	@Test public void test_2989() { checkNotSubtype("X<null|{X f2}>","(null,null)"); }
	@Test public void test_2990() { checkIsSubtype("X<null|{X f2}>","(null,{null f1})"); }
	@Test public void test_2991() { checkIsSubtype("X<null|{X f2}>","(null,{null f2})"); }
	@Test public void test_2992() { checkNotSubtype("X<null|{X f2}>","X<(null,X|null)>"); }
	@Test public void test_2993() { checkNotSubtype("X<null|{X f2}>","({any f1},any)"); }
	@Test public void test_2994() { checkNotSubtype("X<null|{X f2}>","({any f2},any)"); }
	@Test public void test_2995() { checkNotSubtype("X<null|{X f2}>","({null f1},null)"); }
	@Test public void test_2996() { checkNotSubtype("X<null|{X f2}>","({null f2},null)"); }
	@Test public void test_2997() { checkNotSubtype("X<null|{X f2}>","X<(X|any,any)>"); }
	@Test public void test_2998() { checkNotSubtype("X<null|{X f2}>","X<(X|null,null)>"); }
	@Test public void test_2999() { checkNotSubtype("X<null|{X f2}>","{{any f1} f1}"); }
	@Test public void test_3000() { checkNotSubtype("X<null|{X f2}>","{{any f2} f1}"); }
	@Test public void test_3001() { checkNotSubtype("X<null|{X f2}>","{{any f1} f2}"); }
	@Test public void test_3002() { checkNotSubtype("X<null|{X f2}>","{{any f2} f2}"); }
	@Test public void test_3003() { checkNotSubtype("X<null|{X f2}>","{{null f1} f1}"); }
	@Test public void test_3004() { checkNotSubtype("X<null|{X f2}>","{{null f2} f1}"); }
	@Test public void test_3005() { checkNotSubtype("X<null|{X f2}>","{{null f1} f2}"); }
	@Test public void test_3006() { checkIsSubtype("X<null|{X f2}>","{{null f2} f2}"); }
	@Test public void test_3007() { checkNotSubtype("X<null|{X f2}>","X<{X|any f1}>"); }
	@Test public void test_3008() { checkNotSubtype("X<null|{X f2}>","X<{X|any f2}>"); }
	@Test public void test_3009() { checkNotSubtype("X<null|{X f2}>","X<{X|null f1}>"); }
	@Test public void test_3010() { checkIsSubtype("X<null|{X f2}>","X<{X|null f2}>"); }
	@Test public void test_3011() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3012() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3013() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3014() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3015() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3016() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3017() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3018() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3019() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3020() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test public void test_3021() { checkNotSubtype("X<null|{X f2}>","X<null|(X,null)>"); }
	@Test public void test_3022() { checkNotSubtype("X<null|{X f2}>","null|{null f1}"); }
	@Test public void test_3023() { checkIsSubtype("X<null|{X f2}>","null|{null f2}"); }
	@Test public void test_3024() { checkNotSubtype("X<null|{X f2}>","X<null|{X f1}>"); }
	@Test public void test_3025() { checkIsSubtype("X<null|{X f2}>","X<null|{X f2}>"); }
	@Test public void test_3026() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test public void test_3027() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3028() { checkNotSubtype("X<null|{X f2}>","X<(X,null)|null>"); }
	@Test public void test_3029() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3030() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3031() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3032() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3033() { checkNotSubtype("X<null|{X f2}>","{null f1}|null"); }
	@Test public void test_3034() { checkIsSubtype("X<null|{X f2}>","{null f2}|null"); }
	@Test public void test_3035() { checkNotSubtype("X<null|{X f2}>","X<{X f1}|null>"); }
	@Test public void test_3036() { checkIsSubtype("X<null|{X f2}>","X<{X f2}|null>"); }
	@Test public void test_3037() { checkNotSubtype("X<null|{X f2}>","any"); }
	@Test public void test_3038() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test public void test_3039() { checkNotSubtype("null","any"); }
	@Test public void test_3040() { checkIsSubtype("null","null"); }
	@Test public void test_3041() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_3042() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_3043() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_3044() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_3045() { checkNotSubtype("null","(any,any)"); }
	@Test public void test_3046() { checkNotSubtype("null","(any,null)"); }
	@Test public void test_3047() { checkIsSubtype("null","(any,{any f1})"); }
	@Test public void test_3048() { checkIsSubtype("null","(any,{any f2})"); }
	@Test public void test_3049() { checkNotSubtype("null","X<(any,X|any)>"); }
	@Test public void test_3050() { checkNotSubtype("null","(null,any)"); }
	@Test public void test_3051() { checkNotSubtype("null","(null,null)"); }
	@Test public void test_3052() { checkIsSubtype("null","(null,{null f1})"); }
	@Test public void test_3053() { checkIsSubtype("null","(null,{null f2})"); }
	@Test public void test_3054() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test public void test_3055() { checkNotSubtype("null","({any f1},any)"); }
	@Test public void test_3056() { checkNotSubtype("null","({any f2},any)"); }
	@Test public void test_3057() { checkNotSubtype("null","({null f1},null)"); }
	@Test public void test_3058() { checkNotSubtype("null","({null f2},null)"); }
	@Test public void test_3059() { checkNotSubtype("null","X<(X|any,any)>"); }
	@Test public void test_3060() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test public void test_3061() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_3062() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_3063() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_3064() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_3065() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_3066() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_3067() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_3068() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_3069() { checkNotSubtype("null","X<{X|any f1}>"); }
	@Test public void test_3070() { checkNotSubtype("null","X<{X|any f2}>"); }
	@Test public void test_3071() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test public void test_3072() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test public void test_3073() { checkNotSubtype("null","any"); }
	@Test public void test_3074() { checkNotSubtype("null","any"); }
	@Test public void test_3075() { checkNotSubtype("null","any"); }
	@Test public void test_3076() { checkNotSubtype("null","any"); }
	@Test public void test_3077() { checkNotSubtype("null","any"); }
	@Test public void test_3078() { checkNotSubtype("null","any"); }
	@Test public void test_3079() { checkNotSubtype("null","any"); }
	@Test public void test_3080() { checkNotSubtype("null","any"); }
	@Test public void test_3081() { checkNotSubtype("null","any"); }
	@Test public void test_3082() { checkIsSubtype("null","null"); }
	@Test public void test_3083() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test public void test_3084() { checkNotSubtype("null","null|{null f1}"); }
	@Test public void test_3085() { checkNotSubtype("null","null|{null f2}"); }
	@Test public void test_3086() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test public void test_3087() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test public void test_3088() { checkIsSubtype("null","null"); }
	@Test public void test_3089() { checkNotSubtype("null","any"); }
	@Test public void test_3090() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test public void test_3091() { checkNotSubtype("null","any"); }
	@Test public void test_3092() { checkNotSubtype("null","any"); }
	@Test public void test_3093() { checkNotSubtype("null","any"); }
	@Test public void test_3094() { checkNotSubtype("null","any"); }
	@Test public void test_3095() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_3096() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_3097() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test public void test_3098() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test public void test_3099() { checkNotSubtype("null","any"); }
	@Test public void test_3100() { checkIsSubtype("null","null"); }
	@Test public void test_3101() { checkIsSubtype("any","any"); }
	@Test public void test_3102() { checkIsSubtype("any","null"); }
	@Test public void test_3103() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_3104() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_3105() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_3106() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_3107() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_3108() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_3109() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_3110() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_3111() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_3112() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_3113() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_3114() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_3115() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_3116() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_3117() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_3118() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_3119() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_3120() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_3121() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_3122() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_3123() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_3124() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_3125() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_3126() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_3127() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_3128() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_3129() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_3130() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_3131() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_3132() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_3133() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_3134() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_3135() { checkIsSubtype("any","any"); }
	@Test public void test_3136() { checkIsSubtype("any","any"); }
	@Test public void test_3137() { checkIsSubtype("any","any"); }
	@Test public void test_3138() { checkIsSubtype("any","any"); }
	@Test public void test_3139() { checkIsSubtype("any","any"); }
	@Test public void test_3140() { checkIsSubtype("any","any"); }
	@Test public void test_3141() { checkIsSubtype("any","any"); }
	@Test public void test_3142() { checkIsSubtype("any","any"); }
	@Test public void test_3143() { checkIsSubtype("any","any"); }
	@Test public void test_3144() { checkIsSubtype("any","null"); }
	@Test public void test_3145() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_3146() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_3147() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_3148() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_3149() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_3150() { checkIsSubtype("any","null"); }
	@Test public void test_3151() { checkIsSubtype("any","any"); }
	@Test public void test_3152() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_3153() { checkIsSubtype("any","any"); }
	@Test public void test_3154() { checkIsSubtype("any","any"); }
	@Test public void test_3155() { checkIsSubtype("any","any"); }
	@Test public void test_3156() { checkIsSubtype("any","any"); }
	@Test public void test_3157() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_3158() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_3159() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_3160() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_3161() { checkIsSubtype("any","any"); }
	@Test public void test_3162() { checkIsSubtype("any","null"); }
	@Test public void test_3163() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3164() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test public void test_3165() { checkNotSubtype("X<(X,null)|null>","{any f1}"); }
	@Test public void test_3166() { checkNotSubtype("X<(X,null)|null>","{any f2}"); }
	@Test public void test_3167() { checkNotSubtype("X<(X,null)|null>","{null f1}"); }
	@Test public void test_3168() { checkNotSubtype("X<(X,null)|null>","{null f2}"); }
	@Test public void test_3169() { checkNotSubtype("X<(X,null)|null>","(any,any)"); }
	@Test public void test_3170() { checkNotSubtype("X<(X,null)|null>","(any,null)"); }
	@Test public void test_3171() { checkIsSubtype("X<(X,null)|null>","(any,{any f1})"); }
	@Test public void test_3172() { checkIsSubtype("X<(X,null)|null>","(any,{any f2})"); }
	@Test public void test_3173() { checkNotSubtype("X<(X,null)|null>","X<(any,X|any)>"); }
	@Test public void test_3174() { checkNotSubtype("X<(X,null)|null>","(null,any)"); }
	@Test public void test_3175() { checkIsSubtype("X<(X,null)|null>","(null,null)"); }
	@Test public void test_3176() { checkIsSubtype("X<(X,null)|null>","(null,{null f1})"); }
	@Test public void test_3177() { checkIsSubtype("X<(X,null)|null>","(null,{null f2})"); }
	@Test public void test_3178() { checkIsSubtype("X<(X,null)|null>","X<(null,X|null)>"); }
	@Test public void test_3179() { checkNotSubtype("X<(X,null)|null>","({any f1},any)"); }
	@Test public void test_3180() { checkNotSubtype("X<(X,null)|null>","({any f2},any)"); }
	@Test public void test_3181() { checkNotSubtype("X<(X,null)|null>","({null f1},null)"); }
	@Test public void test_3182() { checkNotSubtype("X<(X,null)|null>","({null f2},null)"); }
	@Test public void test_3183() { checkNotSubtype("X<(X,null)|null>","X<(X|any,any)>"); }
	@Test public void test_3184() { checkIsSubtype("X<(X,null)|null>","X<(X|null,null)>"); }
	@Test public void test_3185() { checkNotSubtype("X<(X,null)|null>","{{any f1} f1}"); }
	@Test public void test_3186() { checkNotSubtype("X<(X,null)|null>","{{any f2} f1}"); }
	@Test public void test_3187() { checkNotSubtype("X<(X,null)|null>","{{any f1} f2}"); }
	@Test public void test_3188() { checkNotSubtype("X<(X,null)|null>","{{any f2} f2}"); }
	@Test public void test_3189() { checkNotSubtype("X<(X,null)|null>","{{null f1} f1}"); }
	@Test public void test_3190() { checkNotSubtype("X<(X,null)|null>","{{null f2} f1}"); }
	@Test public void test_3191() { checkNotSubtype("X<(X,null)|null>","{{null f1} f2}"); }
	@Test public void test_3192() { checkNotSubtype("X<(X,null)|null>","{{null f2} f2}"); }
	@Test public void test_3193() { checkNotSubtype("X<(X,null)|null>","X<{X|any f1}>"); }
	@Test public void test_3194() { checkNotSubtype("X<(X,null)|null>","X<{X|any f2}>"); }
	@Test public void test_3195() { checkNotSubtype("X<(X,null)|null>","X<{X|null f1}>"); }
	@Test public void test_3196() { checkNotSubtype("X<(X,null)|null>","X<{X|null f2}>"); }
	@Test public void test_3197() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3198() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3199() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3200() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3201() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3202() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3203() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3204() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3205() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3206() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test public void test_3207() { checkIsSubtype("X<(X,null)|null>","X<null|(X,null)>"); }
	@Test public void test_3208() { checkNotSubtype("X<(X,null)|null>","null|{null f1}"); }
	@Test public void test_3209() { checkNotSubtype("X<(X,null)|null>","null|{null f2}"); }
	@Test public void test_3210() { checkNotSubtype("X<(X,null)|null>","X<null|{X f1}>"); }
	@Test public void test_3211() { checkNotSubtype("X<(X,null)|null>","X<null|{X f2}>"); }
	@Test public void test_3212() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test public void test_3213() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3214() { checkIsSubtype("X<(X,null)|null>","X<(X,null)|null>"); }
	@Test public void test_3215() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3216() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3217() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3218() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3219() { checkNotSubtype("X<(X,null)|null>","{null f1}|null"); }
	@Test public void test_3220() { checkNotSubtype("X<(X,null)|null>","{null f2}|null"); }
	@Test public void test_3221() { checkNotSubtype("X<(X,null)|null>","X<{X f1}|null>"); }
	@Test public void test_3222() { checkNotSubtype("X<(X,null)|null>","X<{X f2}|null>"); }
	@Test public void test_3223() { checkNotSubtype("X<(X,null)|null>","any"); }
	@Test public void test_3224() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test public void test_3225() { checkIsSubtype("any","any"); }
	@Test public void test_3226() { checkIsSubtype("any","null"); }
	@Test public void test_3227() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_3228() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_3229() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_3230() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_3231() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_3232() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_3233() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_3234() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_3235() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_3236() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_3237() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_3238() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_3239() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_3240() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_3241() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_3242() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_3243() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_3244() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_3245() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_3246() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_3247() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_3248() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_3249() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_3250() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_3251() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_3252() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_3253() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_3254() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_3255() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_3256() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_3257() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_3258() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_3259() { checkIsSubtype("any","any"); }
	@Test public void test_3260() { checkIsSubtype("any","any"); }
	@Test public void test_3261() { checkIsSubtype("any","any"); }
	@Test public void test_3262() { checkIsSubtype("any","any"); }
	@Test public void test_3263() { checkIsSubtype("any","any"); }
	@Test public void test_3264() { checkIsSubtype("any","any"); }
	@Test public void test_3265() { checkIsSubtype("any","any"); }
	@Test public void test_3266() { checkIsSubtype("any","any"); }
	@Test public void test_3267() { checkIsSubtype("any","any"); }
	@Test public void test_3268() { checkIsSubtype("any","null"); }
	@Test public void test_3269() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_3270() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_3271() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_3272() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_3273() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_3274() { checkIsSubtype("any","null"); }
	@Test public void test_3275() { checkIsSubtype("any","any"); }
	@Test public void test_3276() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_3277() { checkIsSubtype("any","any"); }
	@Test public void test_3278() { checkIsSubtype("any","any"); }
	@Test public void test_3279() { checkIsSubtype("any","any"); }
	@Test public void test_3280() { checkIsSubtype("any","any"); }
	@Test public void test_3281() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_3282() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_3283() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_3284() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_3285() { checkIsSubtype("any","any"); }
	@Test public void test_3286() { checkIsSubtype("any","null"); }
	@Test public void test_3287() { checkIsSubtype("any","any"); }
	@Test public void test_3288() { checkIsSubtype("any","null"); }
	@Test public void test_3289() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_3290() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_3291() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_3292() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_3293() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_3294() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_3295() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_3296() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_3297() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_3298() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_3299() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_3300() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_3301() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_3302() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_3303() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_3304() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_3305() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_3306() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_3307() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_3308() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_3309() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_3310() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_3311() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_3312() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_3313() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_3314() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_3315() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_3316() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_3317() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_3318() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_3319() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_3320() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_3321() { checkIsSubtype("any","any"); }
	@Test public void test_3322() { checkIsSubtype("any","any"); }
	@Test public void test_3323() { checkIsSubtype("any","any"); }
	@Test public void test_3324() { checkIsSubtype("any","any"); }
	@Test public void test_3325() { checkIsSubtype("any","any"); }
	@Test public void test_3326() { checkIsSubtype("any","any"); }
	@Test public void test_3327() { checkIsSubtype("any","any"); }
	@Test public void test_3328() { checkIsSubtype("any","any"); }
	@Test public void test_3329() { checkIsSubtype("any","any"); }
	@Test public void test_3330() { checkIsSubtype("any","null"); }
	@Test public void test_3331() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_3332() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_3333() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_3334() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_3335() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_3336() { checkIsSubtype("any","null"); }
	@Test public void test_3337() { checkIsSubtype("any","any"); }
	@Test public void test_3338() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_3339() { checkIsSubtype("any","any"); }
	@Test public void test_3340() { checkIsSubtype("any","any"); }
	@Test public void test_3341() { checkIsSubtype("any","any"); }
	@Test public void test_3342() { checkIsSubtype("any","any"); }
	@Test public void test_3343() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_3344() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_3345() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_3346() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_3347() { checkIsSubtype("any","any"); }
	@Test public void test_3348() { checkIsSubtype("any","null"); }
	@Test public void test_3349() { checkIsSubtype("any","any"); }
	@Test public void test_3350() { checkIsSubtype("any","null"); }
	@Test public void test_3351() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_3352() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_3353() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_3354() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_3355() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_3356() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_3357() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_3358() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_3359() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_3360() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_3361() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_3362() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_3363() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_3364() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_3365() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_3366() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_3367() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_3368() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_3369() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_3370() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_3371() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_3372() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_3373() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_3374() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_3375() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_3376() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_3377() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_3378() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_3379() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_3380() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_3381() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_3382() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_3383() { checkIsSubtype("any","any"); }
	@Test public void test_3384() { checkIsSubtype("any","any"); }
	@Test public void test_3385() { checkIsSubtype("any","any"); }
	@Test public void test_3386() { checkIsSubtype("any","any"); }
	@Test public void test_3387() { checkIsSubtype("any","any"); }
	@Test public void test_3388() { checkIsSubtype("any","any"); }
	@Test public void test_3389() { checkIsSubtype("any","any"); }
	@Test public void test_3390() { checkIsSubtype("any","any"); }
	@Test public void test_3391() { checkIsSubtype("any","any"); }
	@Test public void test_3392() { checkIsSubtype("any","null"); }
	@Test public void test_3393() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_3394() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_3395() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_3396() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_3397() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_3398() { checkIsSubtype("any","null"); }
	@Test public void test_3399() { checkIsSubtype("any","any"); }
	@Test public void test_3400() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_3401() { checkIsSubtype("any","any"); }
	@Test public void test_3402() { checkIsSubtype("any","any"); }
	@Test public void test_3403() { checkIsSubtype("any","any"); }
	@Test public void test_3404() { checkIsSubtype("any","any"); }
	@Test public void test_3405() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_3406() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_3407() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_3408() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_3409() { checkIsSubtype("any","any"); }
	@Test public void test_3410() { checkIsSubtype("any","null"); }
	@Test public void test_3411() { checkIsSubtype("any","any"); }
	@Test public void test_3412() { checkIsSubtype("any","null"); }
	@Test public void test_3413() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_3414() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_3415() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_3416() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_3417() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_3418() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_3419() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_3420() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_3421() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_3422() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_3423() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_3424() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_3425() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_3426() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_3427() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_3428() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_3429() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_3430() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_3431() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_3432() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_3433() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_3434() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_3435() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_3436() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_3437() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_3438() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_3439() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_3440() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_3441() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_3442() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_3443() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_3444() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_3445() { checkIsSubtype("any","any"); }
	@Test public void test_3446() { checkIsSubtype("any","any"); }
	@Test public void test_3447() { checkIsSubtype("any","any"); }
	@Test public void test_3448() { checkIsSubtype("any","any"); }
	@Test public void test_3449() { checkIsSubtype("any","any"); }
	@Test public void test_3450() { checkIsSubtype("any","any"); }
	@Test public void test_3451() { checkIsSubtype("any","any"); }
	@Test public void test_3452() { checkIsSubtype("any","any"); }
	@Test public void test_3453() { checkIsSubtype("any","any"); }
	@Test public void test_3454() { checkIsSubtype("any","null"); }
	@Test public void test_3455() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_3456() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_3457() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_3458() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_3459() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_3460() { checkIsSubtype("any","null"); }
	@Test public void test_3461() { checkIsSubtype("any","any"); }
	@Test public void test_3462() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_3463() { checkIsSubtype("any","any"); }
	@Test public void test_3464() { checkIsSubtype("any","any"); }
	@Test public void test_3465() { checkIsSubtype("any","any"); }
	@Test public void test_3466() { checkIsSubtype("any","any"); }
	@Test public void test_3467() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_3468() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_3469() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_3470() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_3471() { checkIsSubtype("any","any"); }
	@Test public void test_3472() { checkIsSubtype("any","null"); }
	@Test public void test_3473() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3474() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_3475() { checkNotSubtype("{null f1}|null","{any f1}"); }
	@Test public void test_3476() { checkNotSubtype("{null f1}|null","{any f2}"); }
	@Test public void test_3477() { checkIsSubtype("{null f1}|null","{null f1}"); }
	@Test public void test_3478() { checkNotSubtype("{null f1}|null","{null f2}"); }
	@Test public void test_3479() { checkNotSubtype("{null f1}|null","(any,any)"); }
	@Test public void test_3480() { checkNotSubtype("{null f1}|null","(any,null)"); }
	@Test public void test_3481() { checkIsSubtype("{null f1}|null","(any,{any f1})"); }
	@Test public void test_3482() { checkIsSubtype("{null f1}|null","(any,{any f2})"); }
	@Test public void test_3483() { checkNotSubtype("{null f1}|null","X<(any,X|any)>"); }
	@Test public void test_3484() { checkNotSubtype("{null f1}|null","(null,any)"); }
	@Test public void test_3485() { checkNotSubtype("{null f1}|null","(null,null)"); }
	@Test public void test_3486() { checkIsSubtype("{null f1}|null","(null,{null f1})"); }
	@Test public void test_3487() { checkIsSubtype("{null f1}|null","(null,{null f2})"); }
	@Test public void test_3488() { checkNotSubtype("{null f1}|null","X<(null,X|null)>"); }
	@Test public void test_3489() { checkNotSubtype("{null f1}|null","({any f1},any)"); }
	@Test public void test_3490() { checkNotSubtype("{null f1}|null","({any f2},any)"); }
	@Test public void test_3491() { checkNotSubtype("{null f1}|null","({null f1},null)"); }
	@Test public void test_3492() { checkNotSubtype("{null f1}|null","({null f2},null)"); }
	@Test public void test_3493() { checkNotSubtype("{null f1}|null","X<(X|any,any)>"); }
	@Test public void test_3494() { checkNotSubtype("{null f1}|null","X<(X|null,null)>"); }
	@Test public void test_3495() { checkNotSubtype("{null f1}|null","{{any f1} f1}"); }
	@Test public void test_3496() { checkNotSubtype("{null f1}|null","{{any f2} f1}"); }
	@Test public void test_3497() { checkNotSubtype("{null f1}|null","{{any f1} f2}"); }
	@Test public void test_3498() { checkNotSubtype("{null f1}|null","{{any f2} f2}"); }
	@Test public void test_3499() { checkNotSubtype("{null f1}|null","{{null f1} f1}"); }
	@Test public void test_3500() { checkNotSubtype("{null f1}|null","{{null f2} f1}"); }
	@Test public void test_3501() { checkNotSubtype("{null f1}|null","{{null f1} f2}"); }
	@Test public void test_3502() { checkNotSubtype("{null f1}|null","{{null f2} f2}"); }
	@Test public void test_3503() { checkNotSubtype("{null f1}|null","X<{X|any f1}>"); }
	@Test public void test_3504() { checkNotSubtype("{null f1}|null","X<{X|any f2}>"); }
	@Test public void test_3505() { checkNotSubtype("{null f1}|null","X<{X|null f1}>"); }
	@Test public void test_3506() { checkNotSubtype("{null f1}|null","X<{X|null f2}>"); }
	@Test public void test_3507() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3508() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3509() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3510() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3511() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3512() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3513() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3514() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3515() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3516() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_3517() { checkNotSubtype("{null f1}|null","X<null|(X,null)>"); }
	@Test public void test_3518() { checkIsSubtype("{null f1}|null","null|{null f1}"); }
	@Test public void test_3519() { checkNotSubtype("{null f1}|null","null|{null f2}"); }
	@Test public void test_3520() { checkNotSubtype("{null f1}|null","X<null|{X f1}>"); }
	@Test public void test_3521() { checkNotSubtype("{null f1}|null","X<null|{X f2}>"); }
	@Test public void test_3522() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_3523() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3524() { checkNotSubtype("{null f1}|null","X<(X,null)|null>"); }
	@Test public void test_3525() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3526() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3527() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3528() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3529() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test public void test_3530() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test public void test_3531() { checkNotSubtype("{null f1}|null","X<{X f1}|null>"); }
	@Test public void test_3532() { checkNotSubtype("{null f1}|null","X<{X f2}|null>"); }
	@Test public void test_3533() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3534() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_3535() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3536() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_3537() { checkNotSubtype("{null f2}|null","{any f1}"); }
	@Test public void test_3538() { checkNotSubtype("{null f2}|null","{any f2}"); }
	@Test public void test_3539() { checkNotSubtype("{null f2}|null","{null f1}"); }
	@Test public void test_3540() { checkIsSubtype("{null f2}|null","{null f2}"); }
	@Test public void test_3541() { checkNotSubtype("{null f2}|null","(any,any)"); }
	@Test public void test_3542() { checkNotSubtype("{null f2}|null","(any,null)"); }
	@Test public void test_3543() { checkIsSubtype("{null f2}|null","(any,{any f1})"); }
	@Test public void test_3544() { checkIsSubtype("{null f2}|null","(any,{any f2})"); }
	@Test public void test_3545() { checkNotSubtype("{null f2}|null","X<(any,X|any)>"); }
	@Test public void test_3546() { checkNotSubtype("{null f2}|null","(null,any)"); }
	@Test public void test_3547() { checkNotSubtype("{null f2}|null","(null,null)"); }
	@Test public void test_3548() { checkIsSubtype("{null f2}|null","(null,{null f1})"); }
	@Test public void test_3549() { checkIsSubtype("{null f2}|null","(null,{null f2})"); }
	@Test public void test_3550() { checkNotSubtype("{null f2}|null","X<(null,X|null)>"); }
	@Test public void test_3551() { checkNotSubtype("{null f2}|null","({any f1},any)"); }
	@Test public void test_3552() { checkNotSubtype("{null f2}|null","({any f2},any)"); }
	@Test public void test_3553() { checkNotSubtype("{null f2}|null","({null f1},null)"); }
	@Test public void test_3554() { checkNotSubtype("{null f2}|null","({null f2},null)"); }
	@Test public void test_3555() { checkNotSubtype("{null f2}|null","X<(X|any,any)>"); }
	@Test public void test_3556() { checkNotSubtype("{null f2}|null","X<(X|null,null)>"); }
	@Test public void test_3557() { checkNotSubtype("{null f2}|null","{{any f1} f1}"); }
	@Test public void test_3558() { checkNotSubtype("{null f2}|null","{{any f2} f1}"); }
	@Test public void test_3559() { checkNotSubtype("{null f2}|null","{{any f1} f2}"); }
	@Test public void test_3560() { checkNotSubtype("{null f2}|null","{{any f2} f2}"); }
	@Test public void test_3561() { checkNotSubtype("{null f2}|null","{{null f1} f1}"); }
	@Test public void test_3562() { checkNotSubtype("{null f2}|null","{{null f2} f1}"); }
	@Test public void test_3563() { checkNotSubtype("{null f2}|null","{{null f1} f2}"); }
	@Test public void test_3564() { checkNotSubtype("{null f2}|null","{{null f2} f2}"); }
	@Test public void test_3565() { checkNotSubtype("{null f2}|null","X<{X|any f1}>"); }
	@Test public void test_3566() { checkNotSubtype("{null f2}|null","X<{X|any f2}>"); }
	@Test public void test_3567() { checkNotSubtype("{null f2}|null","X<{X|null f1}>"); }
	@Test public void test_3568() { checkNotSubtype("{null f2}|null","X<{X|null f2}>"); }
	@Test public void test_3569() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3570() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3571() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3572() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3573() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3574() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3575() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3576() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3577() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3578() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_3579() { checkNotSubtype("{null f2}|null","X<null|(X,null)>"); }
	@Test public void test_3580() { checkNotSubtype("{null f2}|null","null|{null f1}"); }
	@Test public void test_3581() { checkIsSubtype("{null f2}|null","null|{null f2}"); }
	@Test public void test_3582() { checkNotSubtype("{null f2}|null","X<null|{X f1}>"); }
	@Test public void test_3583() { checkNotSubtype("{null f2}|null","X<null|{X f2}>"); }
	@Test public void test_3584() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_3585() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3586() { checkNotSubtype("{null f2}|null","X<(X,null)|null>"); }
	@Test public void test_3587() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3588() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3589() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3590() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3591() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test public void test_3592() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test public void test_3593() { checkNotSubtype("{null f2}|null","X<{X f1}|null>"); }
	@Test public void test_3594() { checkNotSubtype("{null f2}|null","X<{X f2}|null>"); }
	@Test public void test_3595() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3596() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_3597() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3598() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test public void test_3599() { checkNotSubtype("X<{X f1}|null>","{any f1}"); }
	@Test public void test_3600() { checkNotSubtype("X<{X f1}|null>","{any f2}"); }
	@Test public void test_3601() { checkIsSubtype("X<{X f1}|null>","{null f1}"); }
	@Test public void test_3602() { checkNotSubtype("X<{X f1}|null>","{null f2}"); }
	@Test public void test_3603() { checkNotSubtype("X<{X f1}|null>","(any,any)"); }
	@Test public void test_3604() { checkNotSubtype("X<{X f1}|null>","(any,null)"); }
	@Test public void test_3605() { checkIsSubtype("X<{X f1}|null>","(any,{any f1})"); }
	@Test public void test_3606() { checkIsSubtype("X<{X f1}|null>","(any,{any f2})"); }
	@Test public void test_3607() { checkNotSubtype("X<{X f1}|null>","X<(any,X|any)>"); }
	@Test public void test_3608() { checkNotSubtype("X<{X f1}|null>","(null,any)"); }
	@Test public void test_3609() { checkNotSubtype("X<{X f1}|null>","(null,null)"); }
	@Test public void test_3610() { checkIsSubtype("X<{X f1}|null>","(null,{null f1})"); }
	@Test public void test_3611() { checkIsSubtype("X<{X f1}|null>","(null,{null f2})"); }
	@Test public void test_3612() { checkNotSubtype("X<{X f1}|null>","X<(null,X|null)>"); }
	@Test public void test_3613() { checkNotSubtype("X<{X f1}|null>","({any f1},any)"); }
	@Test public void test_3614() { checkNotSubtype("X<{X f1}|null>","({any f2},any)"); }
	@Test public void test_3615() { checkNotSubtype("X<{X f1}|null>","({null f1},null)"); }
	@Test public void test_3616() { checkNotSubtype("X<{X f1}|null>","({null f2},null)"); }
	@Test public void test_3617() { checkNotSubtype("X<{X f1}|null>","X<(X|any,any)>"); }
	@Test public void test_3618() { checkNotSubtype("X<{X f1}|null>","X<(X|null,null)>"); }
	@Test public void test_3619() { checkNotSubtype("X<{X f1}|null>","{{any f1} f1}"); }
	@Test public void test_3620() { checkNotSubtype("X<{X f1}|null>","{{any f2} f1}"); }
	@Test public void test_3621() { checkNotSubtype("X<{X f1}|null>","{{any f1} f2}"); }
	@Test public void test_3622() { checkNotSubtype("X<{X f1}|null>","{{any f2} f2}"); }
	@Test public void test_3623() { checkIsSubtype("X<{X f1}|null>","{{null f1} f1}"); }
	@Test public void test_3624() { checkNotSubtype("X<{X f1}|null>","{{null f2} f1}"); }
	@Test public void test_3625() { checkNotSubtype("X<{X f1}|null>","{{null f1} f2}"); }
	@Test public void test_3626() { checkNotSubtype("X<{X f1}|null>","{{null f2} f2}"); }
	@Test public void test_3627() { checkNotSubtype("X<{X f1}|null>","X<{X|any f1}>"); }
	@Test public void test_3628() { checkNotSubtype("X<{X f1}|null>","X<{X|any f2}>"); }
	@Test public void test_3629() { checkIsSubtype("X<{X f1}|null>","X<{X|null f1}>"); }
	@Test public void test_3630() { checkNotSubtype("X<{X f1}|null>","X<{X|null f2}>"); }
	@Test public void test_3631() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3632() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3633() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3634() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3635() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3636() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3637() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3638() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3639() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3640() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test public void test_3641() { checkNotSubtype("X<{X f1}|null>","X<null|(X,null)>"); }
	@Test public void test_3642() { checkIsSubtype("X<{X f1}|null>","null|{null f1}"); }
	@Test public void test_3643() { checkNotSubtype("X<{X f1}|null>","null|{null f2}"); }
	@Test public void test_3644() { checkIsSubtype("X<{X f1}|null>","X<null|{X f1}>"); }
	@Test public void test_3645() { checkNotSubtype("X<{X f1}|null>","X<null|{X f2}>"); }
	@Test public void test_3646() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test public void test_3647() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3648() { checkNotSubtype("X<{X f1}|null>","X<(X,null)|null>"); }
	@Test public void test_3649() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3650() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3651() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3652() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3653() { checkIsSubtype("X<{X f1}|null>","{null f1}|null"); }
	@Test public void test_3654() { checkNotSubtype("X<{X f1}|null>","{null f2}|null"); }
	@Test public void test_3655() { checkIsSubtype("X<{X f1}|null>","X<{X f1}|null>"); }
	@Test public void test_3656() { checkNotSubtype("X<{X f1}|null>","X<{X f2}|null>"); }
	@Test public void test_3657() { checkNotSubtype("X<{X f1}|null>","any"); }
	@Test public void test_3658() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test public void test_3659() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3660() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test public void test_3661() { checkNotSubtype("X<{X f2}|null>","{any f1}"); }
	@Test public void test_3662() { checkNotSubtype("X<{X f2}|null>","{any f2}"); }
	@Test public void test_3663() { checkNotSubtype("X<{X f2}|null>","{null f1}"); }
	@Test public void test_3664() { checkIsSubtype("X<{X f2}|null>","{null f2}"); }
	@Test public void test_3665() { checkNotSubtype("X<{X f2}|null>","(any,any)"); }
	@Test public void test_3666() { checkNotSubtype("X<{X f2}|null>","(any,null)"); }
	@Test public void test_3667() { checkIsSubtype("X<{X f2}|null>","(any,{any f1})"); }
	@Test public void test_3668() { checkIsSubtype("X<{X f2}|null>","(any,{any f2})"); }
	@Test public void test_3669() { checkNotSubtype("X<{X f2}|null>","X<(any,X|any)>"); }
	@Test public void test_3670() { checkNotSubtype("X<{X f2}|null>","(null,any)"); }
	@Test public void test_3671() { checkNotSubtype("X<{X f2}|null>","(null,null)"); }
	@Test public void test_3672() { checkIsSubtype("X<{X f2}|null>","(null,{null f1})"); }
	@Test public void test_3673() { checkIsSubtype("X<{X f2}|null>","(null,{null f2})"); }
	@Test public void test_3674() { checkNotSubtype("X<{X f2}|null>","X<(null,X|null)>"); }
	@Test public void test_3675() { checkNotSubtype("X<{X f2}|null>","({any f1},any)"); }
	@Test public void test_3676() { checkNotSubtype("X<{X f2}|null>","({any f2},any)"); }
	@Test public void test_3677() { checkNotSubtype("X<{X f2}|null>","({null f1},null)"); }
	@Test public void test_3678() { checkNotSubtype("X<{X f2}|null>","({null f2},null)"); }
	@Test public void test_3679() { checkNotSubtype("X<{X f2}|null>","X<(X|any,any)>"); }
	@Test public void test_3680() { checkNotSubtype("X<{X f2}|null>","X<(X|null,null)>"); }
	@Test public void test_3681() { checkNotSubtype("X<{X f2}|null>","{{any f1} f1}"); }
	@Test public void test_3682() { checkNotSubtype("X<{X f2}|null>","{{any f2} f1}"); }
	@Test public void test_3683() { checkNotSubtype("X<{X f2}|null>","{{any f1} f2}"); }
	@Test public void test_3684() { checkNotSubtype("X<{X f2}|null>","{{any f2} f2}"); }
	@Test public void test_3685() { checkNotSubtype("X<{X f2}|null>","{{null f1} f1}"); }
	@Test public void test_3686() { checkNotSubtype("X<{X f2}|null>","{{null f2} f1}"); }
	@Test public void test_3687() { checkNotSubtype("X<{X f2}|null>","{{null f1} f2}"); }
	@Test public void test_3688() { checkIsSubtype("X<{X f2}|null>","{{null f2} f2}"); }
	@Test public void test_3689() { checkNotSubtype("X<{X f2}|null>","X<{X|any f1}>"); }
	@Test public void test_3690() { checkNotSubtype("X<{X f2}|null>","X<{X|any f2}>"); }
	@Test public void test_3691() { checkNotSubtype("X<{X f2}|null>","X<{X|null f1}>"); }
	@Test public void test_3692() { checkIsSubtype("X<{X f2}|null>","X<{X|null f2}>"); }
	@Test public void test_3693() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3694() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3695() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3696() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3697() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3698() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3699() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3700() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3701() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3702() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test public void test_3703() { checkNotSubtype("X<{X f2}|null>","X<null|(X,null)>"); }
	@Test public void test_3704() { checkNotSubtype("X<{X f2}|null>","null|{null f1}"); }
	@Test public void test_3705() { checkIsSubtype("X<{X f2}|null>","null|{null f2}"); }
	@Test public void test_3706() { checkNotSubtype("X<{X f2}|null>","X<null|{X f1}>"); }
	@Test public void test_3707() { checkIsSubtype("X<{X f2}|null>","X<null|{X f2}>"); }
	@Test public void test_3708() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test public void test_3709() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3710() { checkNotSubtype("X<{X f2}|null>","X<(X,null)|null>"); }
	@Test public void test_3711() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3712() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3713() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3714() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3715() { checkNotSubtype("X<{X f2}|null>","{null f1}|null"); }
	@Test public void test_3716() { checkIsSubtype("X<{X f2}|null>","{null f2}|null"); }
	@Test public void test_3717() { checkNotSubtype("X<{X f2}|null>","X<{X f1}|null>"); }
	@Test public void test_3718() { checkIsSubtype("X<{X f2}|null>","X<{X f2}|null>"); }
	@Test public void test_3719() { checkNotSubtype("X<{X f2}|null>","any"); }
	@Test public void test_3720() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test public void test_3721() { checkIsSubtype("any","any"); }
	@Test public void test_3722() { checkIsSubtype("any","null"); }
	@Test public void test_3723() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_3724() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_3725() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_3726() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_3727() { checkIsSubtype("any","(any,any)"); }
	@Test public void test_3728() { checkIsSubtype("any","(any,null)"); }
	@Test public void test_3729() { checkIsSubtype("any","(any,{any f1})"); }
	@Test public void test_3730() { checkIsSubtype("any","(any,{any f2})"); }
	@Test public void test_3731() { checkIsSubtype("any","X<(any,X|any)>"); }
	@Test public void test_3732() { checkIsSubtype("any","(null,any)"); }
	@Test public void test_3733() { checkIsSubtype("any","(null,null)"); }
	@Test public void test_3734() { checkIsSubtype("any","(null,{null f1})"); }
	@Test public void test_3735() { checkIsSubtype("any","(null,{null f2})"); }
	@Test public void test_3736() { checkIsSubtype("any","X<(null,X|null)>"); }
	@Test public void test_3737() { checkIsSubtype("any","({any f1},any)"); }
	@Test public void test_3738() { checkIsSubtype("any","({any f2},any)"); }
	@Test public void test_3739() { checkIsSubtype("any","({null f1},null)"); }
	@Test public void test_3740() { checkIsSubtype("any","({null f2},null)"); }
	@Test public void test_3741() { checkIsSubtype("any","X<(X|any,any)>"); }
	@Test public void test_3742() { checkIsSubtype("any","X<(X|null,null)>"); }
	@Test public void test_3743() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_3744() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_3745() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_3746() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_3747() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_3748() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_3749() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_3750() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_3751() { checkIsSubtype("any","X<{X|any f1}>"); }
	@Test public void test_3752() { checkIsSubtype("any","X<{X|any f2}>"); }
	@Test public void test_3753() { checkIsSubtype("any","X<{X|null f1}>"); }
	@Test public void test_3754() { checkIsSubtype("any","X<{X|null f2}>"); }
	@Test public void test_3755() { checkIsSubtype("any","any"); }
	@Test public void test_3756() { checkIsSubtype("any","any"); }
	@Test public void test_3757() { checkIsSubtype("any","any"); }
	@Test public void test_3758() { checkIsSubtype("any","any"); }
	@Test public void test_3759() { checkIsSubtype("any","any"); }
	@Test public void test_3760() { checkIsSubtype("any","any"); }
	@Test public void test_3761() { checkIsSubtype("any","any"); }
	@Test public void test_3762() { checkIsSubtype("any","any"); }
	@Test public void test_3763() { checkIsSubtype("any","any"); }
	@Test public void test_3764() { checkIsSubtype("any","null"); }
	@Test public void test_3765() { checkIsSubtype("any","X<null|(X,null)>"); }
	@Test public void test_3766() { checkIsSubtype("any","null|{null f1}"); }
	@Test public void test_3767() { checkIsSubtype("any","null|{null f2}"); }
	@Test public void test_3768() { checkIsSubtype("any","X<null|{X f1}>"); }
	@Test public void test_3769() { checkIsSubtype("any","X<null|{X f2}>"); }
	@Test public void test_3770() { checkIsSubtype("any","null"); }
	@Test public void test_3771() { checkIsSubtype("any","any"); }
	@Test public void test_3772() { checkIsSubtype("any","X<(X,null)|null>"); }
	@Test public void test_3773() { checkIsSubtype("any","any"); }
	@Test public void test_3774() { checkIsSubtype("any","any"); }
	@Test public void test_3775() { checkIsSubtype("any","any"); }
	@Test public void test_3776() { checkIsSubtype("any","any"); }
	@Test public void test_3777() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_3778() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_3779() { checkIsSubtype("any","X<{X f1}|null>"); }
	@Test public void test_3780() { checkIsSubtype("any","X<{X f2}|null>"); }
	@Test public void test_3781() { checkIsSubtype("any","any"); }
	@Test public void test_3782() { checkIsSubtype("any","null"); }
	@Test public void test_3783() { checkNotSubtype("null","any"); }
	@Test public void test_3784() { checkIsSubtype("null","null"); }
	@Test public void test_3785() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_3786() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_3787() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_3788() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_3789() { checkNotSubtype("null","(any,any)"); }
	@Test public void test_3790() { checkNotSubtype("null","(any,null)"); }
	@Test public void test_3791() { checkIsSubtype("null","(any,{any f1})"); }
	@Test public void test_3792() { checkIsSubtype("null","(any,{any f2})"); }
	@Test public void test_3793() { checkNotSubtype("null","X<(any,X|any)>"); }
	@Test public void test_3794() { checkNotSubtype("null","(null,any)"); }
	@Test public void test_3795() { checkNotSubtype("null","(null,null)"); }
	@Test public void test_3796() { checkIsSubtype("null","(null,{null f1})"); }
	@Test public void test_3797() { checkIsSubtype("null","(null,{null f2})"); }
	@Test public void test_3798() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test public void test_3799() { checkNotSubtype("null","({any f1},any)"); }
	@Test public void test_3800() { checkNotSubtype("null","({any f2},any)"); }
	@Test public void test_3801() { checkNotSubtype("null","({null f1},null)"); }
	@Test public void test_3802() { checkNotSubtype("null","({null f2},null)"); }
	@Test public void test_3803() { checkNotSubtype("null","X<(X|any,any)>"); }
	@Test public void test_3804() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test public void test_3805() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_3806() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_3807() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_3808() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_3809() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_3810() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_3811() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_3812() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_3813() { checkNotSubtype("null","X<{X|any f1}>"); }
	@Test public void test_3814() { checkNotSubtype("null","X<{X|any f2}>"); }
	@Test public void test_3815() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test public void test_3816() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test public void test_3817() { checkNotSubtype("null","any"); }
	@Test public void test_3818() { checkNotSubtype("null","any"); }
	@Test public void test_3819() { checkNotSubtype("null","any"); }
	@Test public void test_3820() { checkNotSubtype("null","any"); }
	@Test public void test_3821() { checkNotSubtype("null","any"); }
	@Test public void test_3822() { checkNotSubtype("null","any"); }
	@Test public void test_3823() { checkNotSubtype("null","any"); }
	@Test public void test_3824() { checkNotSubtype("null","any"); }
	@Test public void test_3825() { checkNotSubtype("null","any"); }
	@Test public void test_3826() { checkIsSubtype("null","null"); }
	@Test public void test_3827() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test public void test_3828() { checkNotSubtype("null","null|{null f1}"); }
	@Test public void test_3829() { checkNotSubtype("null","null|{null f2}"); }
	@Test public void test_3830() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test public void test_3831() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test public void test_3832() { checkIsSubtype("null","null"); }
	@Test public void test_3833() { checkNotSubtype("null","any"); }
	@Test public void test_3834() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test public void test_3835() { checkNotSubtype("null","any"); }
	@Test public void test_3836() { checkNotSubtype("null","any"); }
	@Test public void test_3837() { checkNotSubtype("null","any"); }
	@Test public void test_3838() { checkNotSubtype("null","any"); }
	@Test public void test_3839() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_3840() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_3841() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test public void test_3842() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test public void test_3843() { checkNotSubtype("null","any"); }
	@Test public void test_3844() { checkIsSubtype("null","null"); }

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
