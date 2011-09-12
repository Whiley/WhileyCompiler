// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class RecursiveSubtypeTests {
	@Test public void test_1() { checkIsSubtype("any","any"); }
	@Test public void test_2() { checkIsSubtype("any","null"); }
	@Test public void test_3() { checkIsSubtype("any","{void f1}"); }
	@Test public void test_4() { checkIsSubtype("any","{void f2}"); }
	@Test public void test_5() { checkIsSubtype("any","{any f1}"); }
	@Test public void test_6() { checkIsSubtype("any","{any f2}"); }
	@Test public void test_7() { checkIsSubtype("any","{null f1}"); }
	@Test public void test_8() { checkIsSubtype("any","{null f2}"); }
	@Test public void test_9() { checkIsSubtype("any","!void"); }
	@Test public void test_10() { checkIsSubtype("any","!any"); }
	@Test public void test_11() { checkIsSubtype("any","!null"); }
	@Test public void test_12() { checkIsSubtype("any","{{void f1} f1}"); }
	@Test public void test_13() { checkIsSubtype("any","{{void f2} f1}"); }
	@Test public void test_14() { checkIsSubtype("any","{{void f1} f2}"); }
	@Test public void test_15() { checkIsSubtype("any","{{void f2} f2}"); }
	@Test public void test_16() { checkIsSubtype("any","{{any f1} f1}"); }
	@Test public void test_17() { checkIsSubtype("any","{{any f2} f1}"); }
	@Test public void test_18() { checkIsSubtype("any","{{any f1} f2}"); }
	@Test public void test_19() { checkIsSubtype("any","{{any f2} f2}"); }
	@Test public void test_20() { checkIsSubtype("any","{{null f1} f1}"); }
	@Test public void test_21() { checkIsSubtype("any","{{null f2} f1}"); }
	@Test public void test_22() { checkIsSubtype("any","{{null f1} f2}"); }
	@Test public void test_23() { checkIsSubtype("any","{{null f2} f2}"); }
	@Test public void test_24() { checkIsSubtype("any","{!void f1}"); }
	@Test public void test_25() { checkIsSubtype("any","{!void f2}"); }
	@Test public void test_26() { checkIsSubtype("any","{!any f1}"); }
	@Test public void test_27() { checkIsSubtype("any","{!any f2}"); }
	@Test public void test_28() { checkIsSubtype("any","{!null f1}"); }
	@Test public void test_29() { checkIsSubtype("any","{!null f2}"); }
	@Test public void test_30() { checkIsSubtype("any","void|void"); }
	@Test public void test_31() { checkIsSubtype("any","void|any"); }
	@Test public void test_32() { checkIsSubtype("any","void|null"); }
	@Test public void test_33() { checkIsSubtype("any","any|void"); }
	@Test public void test_34() { checkIsSubtype("any","any|any"); }
	@Test public void test_35() { checkIsSubtype("any","any|null"); }
	@Test public void test_36() { checkIsSubtype("any","null|void"); }
	@Test public void test_37() { checkIsSubtype("any","null|any"); }
	@Test public void test_38() { checkIsSubtype("any","null|null"); }
	@Test public void test_39() { checkIsSubtype("any","{void f1}|void"); }
	@Test public void test_40() { checkIsSubtype("any","{void f2}|void"); }
	@Test public void test_41() { checkIsSubtype("any","{any f1}|any"); }
	@Test public void test_42() { checkIsSubtype("any","{any f2}|any"); }
	@Test public void test_43() { checkIsSubtype("any","{null f1}|null"); }
	@Test public void test_44() { checkIsSubtype("any","{null f2}|null"); }
	@Test public void test_45() { checkIsSubtype("any","!void|void"); }
	@Test public void test_46() { checkIsSubtype("any","!any|any"); }
	@Test public void test_47() { checkIsSubtype("any","!null|null"); }
	@Test public void test_48() { checkIsSubtype("any","void&void"); }
	@Test public void test_49() { checkIsSubtype("any","void&any"); }
	@Test public void test_50() { checkIsSubtype("any","void&null"); }
	@Test public void test_51() { checkIsSubtype("any","any&void"); }
	@Test public void test_52() { checkIsSubtype("any","any&any"); }
	@Test public void test_53() { checkIsSubtype("any","any&null"); }
	@Test public void test_54() { checkIsSubtype("any","null&void"); }
	@Test public void test_55() { checkIsSubtype("any","null&any"); }
	@Test public void test_56() { checkIsSubtype("any","null&null"); }
	@Test public void test_57() { checkIsSubtype("any","{void f1}&void"); }
	@Test public void test_58() { checkIsSubtype("any","{void f2}&void"); }
	@Test public void test_59() { checkIsSubtype("any","{any f1}&any"); }
	@Test public void test_60() { checkIsSubtype("any","{any f2}&any"); }
	@Test public void test_61() { checkIsSubtype("any","{null f1}&null"); }
	@Test public void test_62() { checkIsSubtype("any","{null f2}&null"); }
	@Test public void test_63() { checkIsSubtype("any","!void&void"); }
	@Test public void test_64() { checkIsSubtype("any","!any&any"); }
	@Test public void test_65() { checkIsSubtype("any","!null&null"); }
	@Test public void test_66() { checkIsSubtype("any","!{void f1}"); }
	@Test public void test_67() { checkIsSubtype("any","!{void f2}"); }
	@Test public void test_68() { checkIsSubtype("any","!{any f1}"); }
	@Test public void test_69() { checkIsSubtype("any","!{any f2}"); }
	@Test public void test_70() { checkIsSubtype("any","!{null f1}"); }
	@Test public void test_71() { checkIsSubtype("any","!{null f2}"); }
	@Test public void test_72() { checkIsSubtype("any","!!void"); }
	@Test public void test_73() { checkIsSubtype("any","!!any"); }
	@Test public void test_74() { checkIsSubtype("any","!!null"); }
	@Test public void test_75() { checkNotSubtype("null","any"); }
	@Test public void test_76() { checkIsSubtype("null","null"); }
	@Test public void test_77() { checkIsSubtype("null","{void f1}"); }
	@Test public void test_78() { checkIsSubtype("null","{void f2}"); }
	@Test public void test_79() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_80() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_81() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_82() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_83() { checkNotSubtype("null","!void"); }
	@Test public void test_84() { checkIsSubtype("null","!any"); }
	@Test public void test_85() { checkNotSubtype("null","!null"); }
	@Test public void test_86() { checkIsSubtype("null","{{void f1} f1}"); }
	@Test public void test_87() { checkIsSubtype("null","{{void f2} f1}"); }
	@Test public void test_88() { checkIsSubtype("null","{{void f1} f2}"); }
	@Test public void test_89() { checkIsSubtype("null","{{void f2} f2}"); }
	@Test public void test_90() { checkNotSubtype("null","{{any f1} f1}"); }
	@Test public void test_91() { checkNotSubtype("null","{{any f2} f1}"); }
	@Test public void test_92() { checkNotSubtype("null","{{any f1} f2}"); }
	@Test public void test_93() { checkNotSubtype("null","{{any f2} f2}"); }
	@Test public void test_94() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_95() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_96() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_97() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_98() { checkNotSubtype("null","{!void f1}"); }
	@Test public void test_99() { checkNotSubtype("null","{!void f2}"); }
	@Test public void test_100() { checkIsSubtype("null","{!any f1}"); }
	@Test public void test_101() { checkIsSubtype("null","{!any f2}"); }
	@Test public void test_102() { checkNotSubtype("null","{!null f1}"); }
	@Test public void test_103() { checkNotSubtype("null","{!null f2}"); }
	@Test public void test_104() { checkIsSubtype("null","void|void"); }
	@Test public void test_105() { checkNotSubtype("null","void|any"); }
	@Test public void test_106() { checkIsSubtype("null","void|null"); }
	@Test public void test_107() { checkNotSubtype("null","any|void"); }
	@Test public void test_108() { checkNotSubtype("null","any|any"); }
	@Test public void test_109() { checkNotSubtype("null","any|null"); }
	@Test public void test_110() { checkIsSubtype("null","null|void"); }
	@Test public void test_111() { checkNotSubtype("null","null|any"); }
	@Test public void test_112() { checkIsSubtype("null","null|null"); }
	@Test public void test_113() { checkIsSubtype("null","{void f1}|void"); }
	@Test public void test_114() { checkIsSubtype("null","{void f2}|void"); }
	@Test public void test_115() { checkNotSubtype("null","{any f1}|any"); }
	@Test public void test_116() { checkNotSubtype("null","{any f2}|any"); }
	@Test public void test_117() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_118() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_119() { checkNotSubtype("null","!void|void"); }
	@Test public void test_120() { checkNotSubtype("null","!any|any"); }
	@Test public void test_121() { checkNotSubtype("null","!null|null"); }
	@Test public void test_122() { checkIsSubtype("null","void&void"); }
	@Test public void test_123() { checkIsSubtype("null","void&any"); }
	@Test public void test_124() { checkIsSubtype("null","void&null"); }
	@Test public void test_125() { checkIsSubtype("null","any&void"); }
	@Test public void test_126() { checkNotSubtype("null","any&any"); }
	@Test public void test_127() { checkIsSubtype("null","any&null"); }
	@Test public void test_128() { checkIsSubtype("null","null&void"); }
	@Test public void test_129() { checkIsSubtype("null","null&any"); }
	@Test public void test_130() { checkIsSubtype("null","null&null"); }
	@Test public void test_131() { checkIsSubtype("null","{void f1}&void"); }
	@Test public void test_132() { checkIsSubtype("null","{void f2}&void"); }
	@Test public void test_133() { checkNotSubtype("null","{any f1}&any"); }
	@Test public void test_134() { checkNotSubtype("null","{any f2}&any"); }
	@Test public void test_135() { checkIsSubtype("null","{null f1}&null"); }
	@Test public void test_136() { checkIsSubtype("null","{null f2}&null"); }
	@Test public void test_137() { checkIsSubtype("null","!void&void"); }
	@Test public void test_138() { checkIsSubtype("null","!any&any"); }
	@Test public void test_139() { checkIsSubtype("null","!null&null"); }
	@Test public void test_140() { checkNotSubtype("null","!{void f1}"); }
	@Test public void test_141() { checkNotSubtype("null","!{void f2}"); }
	@Test public void test_142() { checkNotSubtype("null","!{any f1}"); }
	@Test public void test_143() { checkNotSubtype("null","!{any f2}"); }
	@Test public void test_144() { checkNotSubtype("null","!{null f1}"); }
	@Test public void test_145() { checkNotSubtype("null","!{null f2}"); }
	@Test public void test_146() { checkIsSubtype("null","!!void"); }
	@Test public void test_147() { checkNotSubtype("null","!!any"); }
	@Test public void test_148() { checkIsSubtype("null","!!null"); }
	@Test public void test_149() { checkNotSubtype("{void f1}","any"); }
	@Test public void test_150() { checkNotSubtype("{void f1}","null"); }
	@Test public void test_151() { checkIsSubtype("{void f1}","{void f1}"); }
	@Test public void test_152() { checkIsSubtype("{void f1}","{void f2}"); }
	@Test public void test_153() { checkNotSubtype("{void f1}","{any f1}"); }
	@Test public void test_154() { checkNotSubtype("{void f1}","{any f2}"); }
	@Test public void test_155() { checkNotSubtype("{void f1}","{null f1}"); }
	@Test public void test_156() { checkNotSubtype("{void f1}","{null f2}"); }
	@Test public void test_157() { checkNotSubtype("{void f1}","!void"); }
	@Test public void test_158() { checkIsSubtype("{void f1}","!any"); }
	@Test public void test_159() { checkNotSubtype("{void f1}","!null"); }
	@Test public void test_160() { checkIsSubtype("{void f1}","{{void f1} f1}"); }
	@Test public void test_161() { checkIsSubtype("{void f1}","{{void f2} f1}"); }
	@Test public void test_162() { checkIsSubtype("{void f1}","{{void f1} f2}"); }
	@Test public void test_163() { checkIsSubtype("{void f1}","{{void f2} f2}"); }
	@Test public void test_164() { checkNotSubtype("{void f1}","{{any f1} f1}"); }
	@Test public void test_165() { checkNotSubtype("{void f1}","{{any f2} f1}"); }
	@Test public void test_166() { checkNotSubtype("{void f1}","{{any f1} f2}"); }
	@Test public void test_167() { checkNotSubtype("{void f1}","{{any f2} f2}"); }
	@Test public void test_168() { checkNotSubtype("{void f1}","{{null f1} f1}"); }
	@Test public void test_169() { checkNotSubtype("{void f1}","{{null f2} f1}"); }
	@Test public void test_170() { checkNotSubtype("{void f1}","{{null f1} f2}"); }
	@Test public void test_171() { checkNotSubtype("{void f1}","{{null f2} f2}"); }
	@Test public void test_172() { checkNotSubtype("{void f1}","{!void f1}"); }
	@Test public void test_173() { checkNotSubtype("{void f1}","{!void f2}"); }
	@Test public void test_174() { checkIsSubtype("{void f1}","{!any f1}"); }
	@Test public void test_175() { checkIsSubtype("{void f1}","{!any f2}"); }
	@Test public void test_176() { checkNotSubtype("{void f1}","{!null f1}"); }
	@Test public void test_177() { checkNotSubtype("{void f1}","{!null f2}"); }
	@Test public void test_178() { checkIsSubtype("{void f1}","void|void"); }
	@Test public void test_179() { checkNotSubtype("{void f1}","void|any"); }
	@Test public void test_180() { checkNotSubtype("{void f1}","void|null"); }
	@Test public void test_181() { checkNotSubtype("{void f1}","any|void"); }
	@Test public void test_182() { checkNotSubtype("{void f1}","any|any"); }
	@Test public void test_183() { checkNotSubtype("{void f1}","any|null"); }
	@Test public void test_184() { checkNotSubtype("{void f1}","null|void"); }
	@Test public void test_185() { checkNotSubtype("{void f1}","null|any"); }
	@Test public void test_186() { checkNotSubtype("{void f1}","null|null"); }
	@Test public void test_187() { checkIsSubtype("{void f1}","{void f1}|void"); }
	@Test public void test_188() { checkIsSubtype("{void f1}","{void f2}|void"); }
	@Test public void test_189() { checkNotSubtype("{void f1}","{any f1}|any"); }
	@Test public void test_190() { checkNotSubtype("{void f1}","{any f2}|any"); }
	@Test public void test_191() { checkNotSubtype("{void f1}","{null f1}|null"); }
	@Test public void test_192() { checkNotSubtype("{void f1}","{null f2}|null"); }
	@Test public void test_193() { checkNotSubtype("{void f1}","!void|void"); }
	@Test public void test_194() { checkNotSubtype("{void f1}","!any|any"); }
	@Test public void test_195() { checkNotSubtype("{void f1}","!null|null"); }
	@Test public void test_196() { checkIsSubtype("{void f1}","void&void"); }
	@Test public void test_197() { checkIsSubtype("{void f1}","void&any"); }
	@Test public void test_198() { checkIsSubtype("{void f1}","void&null"); }
	@Test public void test_199() { checkIsSubtype("{void f1}","any&void"); }
	@Test public void test_200() { checkNotSubtype("{void f1}","any&any"); }
	@Test public void test_201() { checkNotSubtype("{void f1}","any&null"); }
	@Test public void test_202() { checkIsSubtype("{void f1}","null&void"); }
	@Test public void test_203() { checkNotSubtype("{void f1}","null&any"); }
	@Test public void test_204() { checkNotSubtype("{void f1}","null&null"); }
	@Test public void test_205() { checkIsSubtype("{void f1}","{void f1}&void"); }
	@Test public void test_206() { checkIsSubtype("{void f1}","{void f2}&void"); }
	@Test public void test_207() { checkNotSubtype("{void f1}","{any f1}&any"); }
	@Test public void test_208() { checkNotSubtype("{void f1}","{any f2}&any"); }
	@Test public void test_209() { checkIsSubtype("{void f1}","{null f1}&null"); }
	@Test public void test_210() { checkIsSubtype("{void f1}","{null f2}&null"); }
	@Test public void test_211() { checkIsSubtype("{void f1}","!void&void"); }
	@Test public void test_212() { checkIsSubtype("{void f1}","!any&any"); }
	@Test public void test_213() { checkIsSubtype("{void f1}","!null&null"); }
	@Test public void test_214() { checkNotSubtype("{void f1}","!{void f1}"); }
	@Test public void test_215() { checkNotSubtype("{void f1}","!{void f2}"); }
	@Test public void test_216() { checkNotSubtype("{void f1}","!{any f1}"); }
	@Test public void test_217() { checkNotSubtype("{void f1}","!{any f2}"); }
	@Test public void test_218() { checkNotSubtype("{void f1}","!{null f1}"); }
	@Test public void test_219() { checkNotSubtype("{void f1}","!{null f2}"); }
	@Test public void test_220() { checkIsSubtype("{void f1}","!!void"); }
	@Test public void test_221() { checkNotSubtype("{void f1}","!!any"); }
	@Test public void test_222() { checkNotSubtype("{void f1}","!!null"); }
	@Test public void test_223() { checkNotSubtype("{void f2}","any"); }
	@Test public void test_224() { checkNotSubtype("{void f2}","null"); }
	@Test public void test_225() { checkIsSubtype("{void f2}","{void f1}"); }
	@Test public void test_226() { checkIsSubtype("{void f2}","{void f2}"); }
	@Test public void test_227() { checkNotSubtype("{void f2}","{any f1}"); }
	@Test public void test_228() { checkNotSubtype("{void f2}","{any f2}"); }
	@Test public void test_229() { checkNotSubtype("{void f2}","{null f1}"); }
	@Test public void test_230() { checkNotSubtype("{void f2}","{null f2}"); }
	@Test public void test_231() { checkNotSubtype("{void f2}","!void"); }
	@Test public void test_232() { checkIsSubtype("{void f2}","!any"); }
	@Test public void test_233() { checkNotSubtype("{void f2}","!null"); }
	@Test public void test_234() { checkIsSubtype("{void f2}","{{void f1} f1}"); }
	@Test public void test_235() { checkIsSubtype("{void f2}","{{void f2} f1}"); }
	@Test public void test_236() { checkIsSubtype("{void f2}","{{void f1} f2}"); }
	@Test public void test_237() { checkIsSubtype("{void f2}","{{void f2} f2}"); }
	@Test public void test_238() { checkNotSubtype("{void f2}","{{any f1} f1}"); }
	@Test public void test_239() { checkNotSubtype("{void f2}","{{any f2} f1}"); }
	@Test public void test_240() { checkNotSubtype("{void f2}","{{any f1} f2}"); }
	@Test public void test_241() { checkNotSubtype("{void f2}","{{any f2} f2}"); }
	@Test public void test_242() { checkNotSubtype("{void f2}","{{null f1} f1}"); }
	@Test public void test_243() { checkNotSubtype("{void f2}","{{null f2} f1}"); }
	@Test public void test_244() { checkNotSubtype("{void f2}","{{null f1} f2}"); }
	@Test public void test_245() { checkNotSubtype("{void f2}","{{null f2} f2}"); }
	@Test public void test_246() { checkNotSubtype("{void f2}","{!void f1}"); }
	@Test public void test_247() { checkNotSubtype("{void f2}","{!void f2}"); }
	@Test public void test_248() { checkIsSubtype("{void f2}","{!any f1}"); }
	@Test public void test_249() { checkIsSubtype("{void f2}","{!any f2}"); }
	@Test public void test_250() { checkNotSubtype("{void f2}","{!null f1}"); }
	@Test public void test_251() { checkNotSubtype("{void f2}","{!null f2}"); }
	@Test public void test_252() { checkIsSubtype("{void f2}","void|void"); }
	@Test public void test_253() { checkNotSubtype("{void f2}","void|any"); }
	@Test public void test_254() { checkNotSubtype("{void f2}","void|null"); }
	@Test public void test_255() { checkNotSubtype("{void f2}","any|void"); }
	@Test public void test_256() { checkNotSubtype("{void f2}","any|any"); }
	@Test public void test_257() { checkNotSubtype("{void f2}","any|null"); }
	@Test public void test_258() { checkNotSubtype("{void f2}","null|void"); }
	@Test public void test_259() { checkNotSubtype("{void f2}","null|any"); }
	@Test public void test_260() { checkNotSubtype("{void f2}","null|null"); }
	@Test public void test_261() { checkIsSubtype("{void f2}","{void f1}|void"); }
	@Test public void test_262() { checkIsSubtype("{void f2}","{void f2}|void"); }
	@Test public void test_263() { checkNotSubtype("{void f2}","{any f1}|any"); }
	@Test public void test_264() { checkNotSubtype("{void f2}","{any f2}|any"); }
	@Test public void test_265() { checkNotSubtype("{void f2}","{null f1}|null"); }
	@Test public void test_266() { checkNotSubtype("{void f2}","{null f2}|null"); }
	@Test public void test_267() { checkNotSubtype("{void f2}","!void|void"); }
	@Test public void test_268() { checkNotSubtype("{void f2}","!any|any"); }
	@Test public void test_269() { checkNotSubtype("{void f2}","!null|null"); }
	@Test public void test_270() { checkIsSubtype("{void f2}","void&void"); }
	@Test public void test_271() { checkIsSubtype("{void f2}","void&any"); }
	@Test public void test_272() { checkIsSubtype("{void f2}","void&null"); }
	@Test public void test_273() { checkIsSubtype("{void f2}","any&void"); }
	@Test public void test_274() { checkNotSubtype("{void f2}","any&any"); }
	@Test public void test_275() { checkNotSubtype("{void f2}","any&null"); }
	@Test public void test_276() { checkIsSubtype("{void f2}","null&void"); }
	@Test public void test_277() { checkNotSubtype("{void f2}","null&any"); }
	@Test public void test_278() { checkNotSubtype("{void f2}","null&null"); }
	@Test public void test_279() { checkIsSubtype("{void f2}","{void f1}&void"); }
	@Test public void test_280() { checkIsSubtype("{void f2}","{void f2}&void"); }
	@Test public void test_281() { checkNotSubtype("{void f2}","{any f1}&any"); }
	@Test public void test_282() { checkNotSubtype("{void f2}","{any f2}&any"); }
	@Test public void test_283() { checkIsSubtype("{void f2}","{null f1}&null"); }
	@Test public void test_284() { checkIsSubtype("{void f2}","{null f2}&null"); }
	@Test public void test_285() { checkIsSubtype("{void f2}","!void&void"); }
	@Test public void test_286() { checkIsSubtype("{void f2}","!any&any"); }
	@Test public void test_287() { checkIsSubtype("{void f2}","!null&null"); }
	@Test public void test_288() { checkNotSubtype("{void f2}","!{void f1}"); }
	@Test public void test_289() { checkNotSubtype("{void f2}","!{void f2}"); }
	@Test public void test_290() { checkNotSubtype("{void f2}","!{any f1}"); }
	@Test public void test_291() { checkNotSubtype("{void f2}","!{any f2}"); }
	@Test public void test_292() { checkNotSubtype("{void f2}","!{null f1}"); }
	@Test public void test_293() { checkNotSubtype("{void f2}","!{null f2}"); }
	@Test public void test_294() { checkIsSubtype("{void f2}","!!void"); }
	@Test public void test_295() { checkNotSubtype("{void f2}","!!any"); }
	@Test public void test_296() { checkNotSubtype("{void f2}","!!null"); }
	@Test public void test_297() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_298() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_299() { checkIsSubtype("{any f1}","{void f1}"); }
	@Test public void test_300() { checkIsSubtype("{any f1}","{void f2}"); }
	@Test public void test_301() { checkIsSubtype("{any f1}","{any f1}"); }
	@Test public void test_302() { checkNotSubtype("{any f1}","{any f2}"); }
	@Test public void test_303() { checkIsSubtype("{any f1}","{null f1}"); }
	@Test public void test_304() { checkNotSubtype("{any f1}","{null f2}"); }
	@Test public void test_305() { checkNotSubtype("{any f1}","!void"); }
	@Test public void test_306() { checkIsSubtype("{any f1}","!any"); }
	@Test public void test_307() { checkNotSubtype("{any f1}","!null"); }
	@Test public void test_308() { checkIsSubtype("{any f1}","{{void f1} f1}"); }
	@Test public void test_309() { checkIsSubtype("{any f1}","{{void f2} f1}"); }
	@Test public void test_310() { checkIsSubtype("{any f1}","{{void f1} f2}"); }
	@Test public void test_311() { checkIsSubtype("{any f1}","{{void f2} f2}"); }
	@Test public void test_312() { checkIsSubtype("{any f1}","{{any f1} f1}"); }
	@Test public void test_313() { checkIsSubtype("{any f1}","{{any f2} f1}"); }
	@Test public void test_314() { checkNotSubtype("{any f1}","{{any f1} f2}"); }
	@Test public void test_315() { checkNotSubtype("{any f1}","{{any f2} f2}"); }
	@Test public void test_316() { checkIsSubtype("{any f1}","{{null f1} f1}"); }
	@Test public void test_317() { checkIsSubtype("{any f1}","{{null f2} f1}"); }
	@Test public void test_318() { checkNotSubtype("{any f1}","{{null f1} f2}"); }
	@Test public void test_319() { checkNotSubtype("{any f1}","{{null f2} f2}"); }
	@Test public void test_320() { checkIsSubtype("{any f1}","{!void f1}"); }
	@Test public void test_321() { checkNotSubtype("{any f1}","{!void f2}"); }
	@Test public void test_322() { checkIsSubtype("{any f1}","{!any f1}"); }
	@Test public void test_323() { checkIsSubtype("{any f1}","{!any f2}"); }
	@Test public void test_324() { checkIsSubtype("{any f1}","{!null f1}"); }
	@Test public void test_325() { checkNotSubtype("{any f1}","{!null f2}"); }
	@Test public void test_326() { checkIsSubtype("{any f1}","void|void"); }
	@Test public void test_327() { checkNotSubtype("{any f1}","void|any"); }
	@Test public void test_328() { checkNotSubtype("{any f1}","void|null"); }
	@Test public void test_329() { checkNotSubtype("{any f1}","any|void"); }
	@Test public void test_330() { checkNotSubtype("{any f1}","any|any"); }
	@Test public void test_331() { checkNotSubtype("{any f1}","any|null"); }
	@Test public void test_332() { checkNotSubtype("{any f1}","null|void"); }
	@Test public void test_333() { checkNotSubtype("{any f1}","null|any"); }
	@Test public void test_334() { checkNotSubtype("{any f1}","null|null"); }
	@Test public void test_335() { checkIsSubtype("{any f1}","{void f1}|void"); }
	@Test public void test_336() { checkIsSubtype("{any f1}","{void f2}|void"); }
	@Test public void test_337() { checkNotSubtype("{any f1}","{any f1}|any"); }
	@Test public void test_338() { checkNotSubtype("{any f1}","{any f2}|any"); }
	@Test public void test_339() { checkNotSubtype("{any f1}","{null f1}|null"); }
	@Test public void test_340() { checkNotSubtype("{any f1}","{null f2}|null"); }
	@Test public void test_341() { checkNotSubtype("{any f1}","!void|void"); }
	@Test public void test_342() { checkNotSubtype("{any f1}","!any|any"); }
	@Test public void test_343() { checkNotSubtype("{any f1}","!null|null"); }
	@Test public void test_344() { checkIsSubtype("{any f1}","void&void"); }
	@Test public void test_345() { checkIsSubtype("{any f1}","void&any"); }
	@Test public void test_346() { checkIsSubtype("{any f1}","void&null"); }
	@Test public void test_347() { checkIsSubtype("{any f1}","any&void"); }
	@Test public void test_348() { checkNotSubtype("{any f1}","any&any"); }
	@Test public void test_349() { checkNotSubtype("{any f1}","any&null"); }
	@Test public void test_350() { checkIsSubtype("{any f1}","null&void"); }
	@Test public void test_351() { checkNotSubtype("{any f1}","null&any"); }
	@Test public void test_352() { checkNotSubtype("{any f1}","null&null"); }
	@Test public void test_353() { checkIsSubtype("{any f1}","{void f1}&void"); }
	@Test public void test_354() { checkIsSubtype("{any f1}","{void f2}&void"); }
	@Test public void test_355() { checkIsSubtype("{any f1}","{any f1}&any"); }
	@Test public void test_356() { checkNotSubtype("{any f1}","{any f2}&any"); }
	@Test public void test_357() { checkIsSubtype("{any f1}","{null f1}&null"); }
	@Test public void test_358() { checkIsSubtype("{any f1}","{null f2}&null"); }
	@Test public void test_359() { checkIsSubtype("{any f1}","!void&void"); }
	@Test public void test_360() { checkIsSubtype("{any f1}","!any&any"); }
	@Test public void test_361() { checkIsSubtype("{any f1}","!null&null"); }
	@Test public void test_362() { checkNotSubtype("{any f1}","!{void f1}"); }
	@Test public void test_363() { checkNotSubtype("{any f1}","!{void f2}"); }
	@Test public void test_364() { checkNotSubtype("{any f1}","!{any f1}"); }
	@Test public void test_365() { checkNotSubtype("{any f1}","!{any f2}"); }
	@Test public void test_366() { checkNotSubtype("{any f1}","!{null f1}"); }
	@Test public void test_367() { checkNotSubtype("{any f1}","!{null f2}"); }
	@Test public void test_368() { checkIsSubtype("{any f1}","!!void"); }
	@Test public void test_369() { checkNotSubtype("{any f1}","!!any"); }
	@Test public void test_370() { checkNotSubtype("{any f1}","!!null"); }
	@Test public void test_371() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_372() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_373() { checkIsSubtype("{any f2}","{void f1}"); }
	@Test public void test_374() { checkIsSubtype("{any f2}","{void f2}"); }
	@Test public void test_375() { checkNotSubtype("{any f2}","{any f1}"); }
	@Test public void test_376() { checkIsSubtype("{any f2}","{any f2}"); }
	@Test public void test_377() { checkNotSubtype("{any f2}","{null f1}"); }
	@Test public void test_378() { checkIsSubtype("{any f2}","{null f2}"); }
	@Test public void test_379() { checkNotSubtype("{any f2}","!void"); }
	@Test public void test_380() { checkIsSubtype("{any f2}","!any"); }
	@Test public void test_381() { checkNotSubtype("{any f2}","!null"); }
	@Test public void test_382() { checkIsSubtype("{any f2}","{{void f1} f1}"); }
	@Test public void test_383() { checkIsSubtype("{any f2}","{{void f2} f1}"); }
	@Test public void test_384() { checkIsSubtype("{any f2}","{{void f1} f2}"); }
	@Test public void test_385() { checkIsSubtype("{any f2}","{{void f2} f2}"); }
	@Test public void test_386() { checkNotSubtype("{any f2}","{{any f1} f1}"); }
	@Test public void test_387() { checkNotSubtype("{any f2}","{{any f2} f1}"); }
	@Test public void test_388() { checkIsSubtype("{any f2}","{{any f1} f2}"); }
	@Test public void test_389() { checkIsSubtype("{any f2}","{{any f2} f2}"); }
	@Test public void test_390() { checkNotSubtype("{any f2}","{{null f1} f1}"); }
	@Test public void test_391() { checkNotSubtype("{any f2}","{{null f2} f1}"); }
	@Test public void test_392() { checkIsSubtype("{any f2}","{{null f1} f2}"); }
	@Test public void test_393() { checkIsSubtype("{any f2}","{{null f2} f2}"); }
	@Test public void test_394() { checkNotSubtype("{any f2}","{!void f1}"); }
	@Test public void test_395() { checkIsSubtype("{any f2}","{!void f2}"); }
	@Test public void test_396() { checkIsSubtype("{any f2}","{!any f1}"); }
	@Test public void test_397() { checkIsSubtype("{any f2}","{!any f2}"); }
	@Test public void test_398() { checkNotSubtype("{any f2}","{!null f1}"); }
	@Test public void test_399() { checkIsSubtype("{any f2}","{!null f2}"); }
	@Test public void test_400() { checkIsSubtype("{any f2}","void|void"); }
	@Test public void test_401() { checkNotSubtype("{any f2}","void|any"); }
	@Test public void test_402() { checkNotSubtype("{any f2}","void|null"); }
	@Test public void test_403() { checkNotSubtype("{any f2}","any|void"); }
	@Test public void test_404() { checkNotSubtype("{any f2}","any|any"); }
	@Test public void test_405() { checkNotSubtype("{any f2}","any|null"); }
	@Test public void test_406() { checkNotSubtype("{any f2}","null|void"); }
	@Test public void test_407() { checkNotSubtype("{any f2}","null|any"); }
	@Test public void test_408() { checkNotSubtype("{any f2}","null|null"); }
	@Test public void test_409() { checkIsSubtype("{any f2}","{void f1}|void"); }
	@Test public void test_410() { checkIsSubtype("{any f2}","{void f2}|void"); }
	@Test public void test_411() { checkNotSubtype("{any f2}","{any f1}|any"); }
	@Test public void test_412() { checkNotSubtype("{any f2}","{any f2}|any"); }
	@Test public void test_413() { checkNotSubtype("{any f2}","{null f1}|null"); }
	@Test public void test_414() { checkNotSubtype("{any f2}","{null f2}|null"); }
	@Test public void test_415() { checkNotSubtype("{any f2}","!void|void"); }
	@Test public void test_416() { checkNotSubtype("{any f2}","!any|any"); }
	@Test public void test_417() { checkNotSubtype("{any f2}","!null|null"); }
	@Test public void test_418() { checkIsSubtype("{any f2}","void&void"); }
	@Test public void test_419() { checkIsSubtype("{any f2}","void&any"); }
	@Test public void test_420() { checkIsSubtype("{any f2}","void&null"); }
	@Test public void test_421() { checkIsSubtype("{any f2}","any&void"); }
	@Test public void test_422() { checkNotSubtype("{any f2}","any&any"); }
	@Test public void test_423() { checkNotSubtype("{any f2}","any&null"); }
	@Test public void test_424() { checkIsSubtype("{any f2}","null&void"); }
	@Test public void test_425() { checkNotSubtype("{any f2}","null&any"); }
	@Test public void test_426() { checkNotSubtype("{any f2}","null&null"); }
	@Test public void test_427() { checkIsSubtype("{any f2}","{void f1}&void"); }
	@Test public void test_428() { checkIsSubtype("{any f2}","{void f2}&void"); }
	@Test public void test_429() { checkNotSubtype("{any f2}","{any f1}&any"); }
	@Test public void test_430() { checkIsSubtype("{any f2}","{any f2}&any"); }
	@Test public void test_431() { checkIsSubtype("{any f2}","{null f1}&null"); }
	@Test public void test_432() { checkIsSubtype("{any f2}","{null f2}&null"); }
	@Test public void test_433() { checkIsSubtype("{any f2}","!void&void"); }
	@Test public void test_434() { checkIsSubtype("{any f2}","!any&any"); }
	@Test public void test_435() { checkIsSubtype("{any f2}","!null&null"); }
	@Test public void test_436() { checkNotSubtype("{any f2}","!{void f1}"); }
	@Test public void test_437() { checkNotSubtype("{any f2}","!{void f2}"); }
	@Test public void test_438() { checkNotSubtype("{any f2}","!{any f1}"); }
	@Test public void test_439() { checkNotSubtype("{any f2}","!{any f2}"); }
	@Test public void test_440() { checkNotSubtype("{any f2}","!{null f1}"); }
	@Test public void test_441() { checkNotSubtype("{any f2}","!{null f2}"); }
	@Test public void test_442() { checkIsSubtype("{any f2}","!!void"); }
	@Test public void test_443() { checkNotSubtype("{any f2}","!!any"); }
	@Test public void test_444() { checkNotSubtype("{any f2}","!!null"); }
	@Test public void test_445() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_446() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_447() { checkIsSubtype("{null f1}","{void f1}"); }
	@Test public void test_448() { checkIsSubtype("{null f1}","{void f2}"); }
	@Test public void test_449() { checkNotSubtype("{null f1}","{any f1}"); }
	@Test public void test_450() { checkNotSubtype("{null f1}","{any f2}"); }
	@Test public void test_451() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_452() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_453() { checkNotSubtype("{null f1}","!void"); }
	@Test public void test_454() { checkIsSubtype("{null f1}","!any"); }
	@Test public void test_455() { checkNotSubtype("{null f1}","!null"); }
	@Test public void test_456() { checkIsSubtype("{null f1}","{{void f1} f1}"); }
	@Test public void test_457() { checkIsSubtype("{null f1}","{{void f2} f1}"); }
	@Test public void test_458() { checkIsSubtype("{null f1}","{{void f1} f2}"); }
	@Test public void test_459() { checkIsSubtype("{null f1}","{{void f2} f2}"); }
	@Test public void test_460() { checkNotSubtype("{null f1}","{{any f1} f1}"); }
	@Test public void test_461() { checkNotSubtype("{null f1}","{{any f2} f1}"); }
	@Test public void test_462() { checkNotSubtype("{null f1}","{{any f1} f2}"); }
	@Test public void test_463() { checkNotSubtype("{null f1}","{{any f2} f2}"); }
	@Test public void test_464() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test public void test_465() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test public void test_466() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test public void test_467() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test public void test_468() { checkNotSubtype("{null f1}","{!void f1}"); }
	@Test public void test_469() { checkNotSubtype("{null f1}","{!void f2}"); }
	@Test public void test_470() { checkIsSubtype("{null f1}","{!any f1}"); }
	@Test public void test_471() { checkIsSubtype("{null f1}","{!any f2}"); }
	@Test public void test_472() { checkNotSubtype("{null f1}","{!null f1}"); }
	@Test public void test_473() { checkNotSubtype("{null f1}","{!null f2}"); }
	@Test public void test_474() { checkIsSubtype("{null f1}","void|void"); }
	@Test public void test_475() { checkNotSubtype("{null f1}","void|any"); }
	@Test public void test_476() { checkNotSubtype("{null f1}","void|null"); }
	@Test public void test_477() { checkNotSubtype("{null f1}","any|void"); }
	@Test public void test_478() { checkNotSubtype("{null f1}","any|any"); }
	@Test public void test_479() { checkNotSubtype("{null f1}","any|null"); }
	@Test public void test_480() { checkNotSubtype("{null f1}","null|void"); }
	@Test public void test_481() { checkNotSubtype("{null f1}","null|any"); }
	@Test public void test_482() { checkNotSubtype("{null f1}","null|null"); }
	@Test public void test_483() { checkIsSubtype("{null f1}","{void f1}|void"); }
	@Test public void test_484() { checkIsSubtype("{null f1}","{void f2}|void"); }
	@Test public void test_485() { checkNotSubtype("{null f1}","{any f1}|any"); }
	@Test public void test_486() { checkNotSubtype("{null f1}","{any f2}|any"); }
	@Test public void test_487() { checkNotSubtype("{null f1}","{null f1}|null"); }
	@Test public void test_488() { checkNotSubtype("{null f1}","{null f2}|null"); }
	@Test public void test_489() { checkNotSubtype("{null f1}","!void|void"); }
	@Test public void test_490() { checkNotSubtype("{null f1}","!any|any"); }
	@Test public void test_491() { checkNotSubtype("{null f1}","!null|null"); }
	@Test public void test_492() { checkIsSubtype("{null f1}","void&void"); }
	@Test public void test_493() { checkIsSubtype("{null f1}","void&any"); }
	@Test public void test_494() { checkIsSubtype("{null f1}","void&null"); }
	@Test public void test_495() { checkIsSubtype("{null f1}","any&void"); }
	@Test public void test_496() { checkNotSubtype("{null f1}","any&any"); }
	@Test public void test_497() { checkNotSubtype("{null f1}","any&null"); }
	@Test public void test_498() { checkIsSubtype("{null f1}","null&void"); }
	@Test public void test_499() { checkNotSubtype("{null f1}","null&any"); }
	@Test public void test_500() { checkNotSubtype("{null f1}","null&null"); }
	@Test public void test_501() { checkIsSubtype("{null f1}","{void f1}&void"); }
	@Test public void test_502() { checkIsSubtype("{null f1}","{void f2}&void"); }
	@Test public void test_503() { checkNotSubtype("{null f1}","{any f1}&any"); }
	@Test public void test_504() { checkNotSubtype("{null f1}","{any f2}&any"); }
	@Test public void test_505() { checkIsSubtype("{null f1}","{null f1}&null"); }
	@Test public void test_506() { checkIsSubtype("{null f1}","{null f2}&null"); }
	@Test public void test_507() { checkIsSubtype("{null f1}","!void&void"); }
	@Test public void test_508() { checkIsSubtype("{null f1}","!any&any"); }
	@Test public void test_509() { checkIsSubtype("{null f1}","!null&null"); }
	@Test public void test_510() { checkNotSubtype("{null f1}","!{void f1}"); }
	@Test public void test_511() { checkNotSubtype("{null f1}","!{void f2}"); }
	@Test public void test_512() { checkNotSubtype("{null f1}","!{any f1}"); }
	@Test public void test_513() { checkNotSubtype("{null f1}","!{any f2}"); }
	@Test public void test_514() { checkNotSubtype("{null f1}","!{null f1}"); }
	@Test public void test_515() { checkNotSubtype("{null f1}","!{null f2}"); }
	@Test public void test_516() { checkIsSubtype("{null f1}","!!void"); }
	@Test public void test_517() { checkNotSubtype("{null f1}","!!any"); }
	@Test public void test_518() { checkNotSubtype("{null f1}","!!null"); }
	@Test public void test_519() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_520() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_521() { checkIsSubtype("{null f2}","{void f1}"); }
	@Test public void test_522() { checkIsSubtype("{null f2}","{void f2}"); }
	@Test public void test_523() { checkNotSubtype("{null f2}","{any f1}"); }
	@Test public void test_524() { checkNotSubtype("{null f2}","{any f2}"); }
	@Test public void test_525() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_526() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_527() { checkNotSubtype("{null f2}","!void"); }
	@Test public void test_528() { checkIsSubtype("{null f2}","!any"); }
	@Test public void test_529() { checkNotSubtype("{null f2}","!null"); }
	@Test public void test_530() { checkIsSubtype("{null f2}","{{void f1} f1}"); }
	@Test public void test_531() { checkIsSubtype("{null f2}","{{void f2} f1}"); }
	@Test public void test_532() { checkIsSubtype("{null f2}","{{void f1} f2}"); }
	@Test public void test_533() { checkIsSubtype("{null f2}","{{void f2} f2}"); }
	@Test public void test_534() { checkNotSubtype("{null f2}","{{any f1} f1}"); }
	@Test public void test_535() { checkNotSubtype("{null f2}","{{any f2} f1}"); }
	@Test public void test_536() { checkNotSubtype("{null f2}","{{any f1} f2}"); }
	@Test public void test_537() { checkNotSubtype("{null f2}","{{any f2} f2}"); }
	@Test public void test_538() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test public void test_539() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test public void test_540() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test public void test_541() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test public void test_542() { checkNotSubtype("{null f2}","{!void f1}"); }
	@Test public void test_543() { checkNotSubtype("{null f2}","{!void f2}"); }
	@Test public void test_544() { checkIsSubtype("{null f2}","{!any f1}"); }
	@Test public void test_545() { checkIsSubtype("{null f2}","{!any f2}"); }
	@Test public void test_546() { checkNotSubtype("{null f2}","{!null f1}"); }
	@Test public void test_547() { checkNotSubtype("{null f2}","{!null f2}"); }
	@Test public void test_548() { checkIsSubtype("{null f2}","void|void"); }
	@Test public void test_549() { checkNotSubtype("{null f2}","void|any"); }
	@Test public void test_550() { checkNotSubtype("{null f2}","void|null"); }
	@Test public void test_551() { checkNotSubtype("{null f2}","any|void"); }
	@Test public void test_552() { checkNotSubtype("{null f2}","any|any"); }
	@Test public void test_553() { checkNotSubtype("{null f2}","any|null"); }
	@Test public void test_554() { checkNotSubtype("{null f2}","null|void"); }
	@Test public void test_555() { checkNotSubtype("{null f2}","null|any"); }
	@Test public void test_556() { checkNotSubtype("{null f2}","null|null"); }
	@Test public void test_557() { checkIsSubtype("{null f2}","{void f1}|void"); }
	@Test public void test_558() { checkIsSubtype("{null f2}","{void f2}|void"); }
	@Test public void test_559() { checkNotSubtype("{null f2}","{any f1}|any"); }
	@Test public void test_560() { checkNotSubtype("{null f2}","{any f2}|any"); }
	@Test public void test_561() { checkNotSubtype("{null f2}","{null f1}|null"); }
	@Test public void test_562() { checkNotSubtype("{null f2}","{null f2}|null"); }
	@Test public void test_563() { checkNotSubtype("{null f2}","!void|void"); }
	@Test public void test_564() { checkNotSubtype("{null f2}","!any|any"); }
	@Test public void test_565() { checkNotSubtype("{null f2}","!null|null"); }
	@Test public void test_566() { checkIsSubtype("{null f2}","void&void"); }
	@Test public void test_567() { checkIsSubtype("{null f2}","void&any"); }
	@Test public void test_568() { checkIsSubtype("{null f2}","void&null"); }
	@Test public void test_569() { checkIsSubtype("{null f2}","any&void"); }
	@Test public void test_570() { checkNotSubtype("{null f2}","any&any"); }
	@Test public void test_571() { checkNotSubtype("{null f2}","any&null"); }
	@Test public void test_572() { checkIsSubtype("{null f2}","null&void"); }
	@Test public void test_573() { checkNotSubtype("{null f2}","null&any"); }
	@Test public void test_574() { checkNotSubtype("{null f2}","null&null"); }
	@Test public void test_575() { checkIsSubtype("{null f2}","{void f1}&void"); }
	@Test public void test_576() { checkIsSubtype("{null f2}","{void f2}&void"); }
	@Test public void test_577() { checkNotSubtype("{null f2}","{any f1}&any"); }
	@Test public void test_578() { checkNotSubtype("{null f2}","{any f2}&any"); }
	@Test public void test_579() { checkIsSubtype("{null f2}","{null f1}&null"); }
	@Test public void test_580() { checkIsSubtype("{null f2}","{null f2}&null"); }
	@Test public void test_581() { checkIsSubtype("{null f2}","!void&void"); }
	@Test public void test_582() { checkIsSubtype("{null f2}","!any&any"); }
	@Test public void test_583() { checkIsSubtype("{null f2}","!null&null"); }
	@Test public void test_584() { checkNotSubtype("{null f2}","!{void f1}"); }
	@Test public void test_585() { checkNotSubtype("{null f2}","!{void f2}"); }
	@Test public void test_586() { checkNotSubtype("{null f2}","!{any f1}"); }
	@Test public void test_587() { checkNotSubtype("{null f2}","!{any f2}"); }
	@Test public void test_588() { checkNotSubtype("{null f2}","!{null f1}"); }
	@Test public void test_589() { checkNotSubtype("{null f2}","!{null f2}"); }
	@Test public void test_590() { checkIsSubtype("{null f2}","!!void"); }
	@Test public void test_591() { checkNotSubtype("{null f2}","!!any"); }
	@Test public void test_592() { checkNotSubtype("{null f2}","!!null"); }
	@Test public void test_593() { checkIsSubtype("!void","any"); }
	@Test public void test_594() { checkIsSubtype("!void","null"); }
	@Test public void test_595() { checkIsSubtype("!void","{void f1}"); }
	@Test public void test_596() { checkIsSubtype("!void","{void f2}"); }
	@Test public void test_597() { checkIsSubtype("!void","{any f1}"); }
	@Test public void test_598() { checkIsSubtype("!void","{any f2}"); }
	@Test public void test_599() { checkIsSubtype("!void","{null f1}"); }
	@Test public void test_600() { checkIsSubtype("!void","{null f2}"); }
	@Test public void test_601() { checkIsSubtype("!void","!void"); }
	@Test public void test_602() { checkIsSubtype("!void","!any"); }
	@Test public void test_603() { checkIsSubtype("!void","!null"); }
	@Test public void test_604() { checkIsSubtype("!void","{{void f1} f1}"); }
	@Test public void test_605() { checkIsSubtype("!void","{{void f2} f1}"); }
	@Test public void test_606() { checkIsSubtype("!void","{{void f1} f2}"); }
	@Test public void test_607() { checkIsSubtype("!void","{{void f2} f2}"); }
	@Test public void test_608() { checkIsSubtype("!void","{{any f1} f1}"); }
	@Test public void test_609() { checkIsSubtype("!void","{{any f2} f1}"); }
	@Test public void test_610() { checkIsSubtype("!void","{{any f1} f2}"); }
	@Test public void test_611() { checkIsSubtype("!void","{{any f2} f2}"); }
	@Test public void test_612() { checkIsSubtype("!void","{{null f1} f1}"); }
	@Test public void test_613() { checkIsSubtype("!void","{{null f2} f1}"); }
	@Test public void test_614() { checkIsSubtype("!void","{{null f1} f2}"); }
	@Test public void test_615() { checkIsSubtype("!void","{{null f2} f2}"); }
	@Test public void test_616() { checkIsSubtype("!void","{!void f1}"); }
	@Test public void test_617() { checkIsSubtype("!void","{!void f2}"); }
	@Test public void test_618() { checkIsSubtype("!void","{!any f1}"); }
	@Test public void test_619() { checkIsSubtype("!void","{!any f2}"); }
	@Test public void test_620() { checkIsSubtype("!void","{!null f1}"); }
	@Test public void test_621() { checkIsSubtype("!void","{!null f2}"); }
	@Test public void test_622() { checkIsSubtype("!void","void|void"); }
	@Test public void test_623() { checkIsSubtype("!void","void|any"); }
	@Test public void test_624() { checkIsSubtype("!void","void|null"); }
	@Test public void test_625() { checkIsSubtype("!void","any|void"); }
	@Test public void test_626() { checkIsSubtype("!void","any|any"); }
	@Test public void test_627() { checkIsSubtype("!void","any|null"); }
	@Test public void test_628() { checkIsSubtype("!void","null|void"); }
	@Test public void test_629() { checkIsSubtype("!void","null|any"); }
	@Test public void test_630() { checkIsSubtype("!void","null|null"); }
	@Test public void test_631() { checkIsSubtype("!void","{void f1}|void"); }
	@Test public void test_632() { checkIsSubtype("!void","{void f2}|void"); }
	@Test public void test_633() { checkIsSubtype("!void","{any f1}|any"); }
	@Test public void test_634() { checkIsSubtype("!void","{any f2}|any"); }
	@Test public void test_635() { checkIsSubtype("!void","{null f1}|null"); }
	@Test public void test_636() { checkIsSubtype("!void","{null f2}|null"); }
	@Test public void test_637() { checkIsSubtype("!void","!void|void"); }
	@Test public void test_638() { checkIsSubtype("!void","!any|any"); }
	@Test public void test_639() { checkIsSubtype("!void","!null|null"); }
	@Test public void test_640() { checkIsSubtype("!void","void&void"); }
	@Test public void test_641() { checkIsSubtype("!void","void&any"); }
	@Test public void test_642() { checkIsSubtype("!void","void&null"); }
	@Test public void test_643() { checkIsSubtype("!void","any&void"); }
	@Test public void test_644() { checkIsSubtype("!void","any&any"); }
	@Test public void test_645() { checkIsSubtype("!void","any&null"); }
	@Test public void test_646() { checkIsSubtype("!void","null&void"); }
	@Test public void test_647() { checkIsSubtype("!void","null&any"); }
	@Test public void test_648() { checkIsSubtype("!void","null&null"); }
	@Test public void test_649() { checkIsSubtype("!void","{void f1}&void"); }
	@Test public void test_650() { checkIsSubtype("!void","{void f2}&void"); }
	@Test public void test_651() { checkIsSubtype("!void","{any f1}&any"); }
	@Test public void test_652() { checkIsSubtype("!void","{any f2}&any"); }
	@Test public void test_653() { checkIsSubtype("!void","{null f1}&null"); }
	@Test public void test_654() { checkIsSubtype("!void","{null f2}&null"); }
	@Test public void test_655() { checkIsSubtype("!void","!void&void"); }
	@Test public void test_656() { checkIsSubtype("!void","!any&any"); }
	@Test public void test_657() { checkIsSubtype("!void","!null&null"); }
	@Test public void test_658() { checkIsSubtype("!void","!{void f1}"); }
	@Test public void test_659() { checkIsSubtype("!void","!{void f2}"); }
	@Test public void test_660() { checkIsSubtype("!void","!{any f1}"); }
	@Test public void test_661() { checkIsSubtype("!void","!{any f2}"); }
	@Test public void test_662() { checkIsSubtype("!void","!{null f1}"); }
	@Test public void test_663() { checkIsSubtype("!void","!{null f2}"); }
	@Test public void test_664() { checkIsSubtype("!void","!!void"); }
	@Test public void test_665() { checkIsSubtype("!void","!!any"); }
	@Test public void test_666() { checkIsSubtype("!void","!!null"); }
	@Test public void test_667() { checkNotSubtype("!any","any"); }
	@Test public void test_668() { checkNotSubtype("!any","null"); }
	@Test public void test_669() { checkIsSubtype("!any","{void f1}"); }
	@Test public void test_670() { checkIsSubtype("!any","{void f2}"); }
	@Test public void test_671() { checkNotSubtype("!any","{any f1}"); }
	@Test public void test_672() { checkNotSubtype("!any","{any f2}"); }
	@Test public void test_673() { checkNotSubtype("!any","{null f1}"); }
	@Test public void test_674() { checkNotSubtype("!any","{null f2}"); }
	@Test public void test_675() { checkNotSubtype("!any","!void"); }
	@Test public void test_676() { checkIsSubtype("!any","!any"); }
	@Test public void test_677() { checkNotSubtype("!any","!null"); }
	@Test public void test_678() { checkIsSubtype("!any","{{void f1} f1}"); }
	@Test public void test_679() { checkIsSubtype("!any","{{void f2} f1}"); }
	@Test public void test_680() { checkIsSubtype("!any","{{void f1} f2}"); }
	@Test public void test_681() { checkIsSubtype("!any","{{void f2} f2}"); }
	@Test public void test_682() { checkNotSubtype("!any","{{any f1} f1}"); }
	@Test public void test_683() { checkNotSubtype("!any","{{any f2} f1}"); }
	@Test public void test_684() { checkNotSubtype("!any","{{any f1} f2}"); }
	@Test public void test_685() { checkNotSubtype("!any","{{any f2} f2}"); }
	@Test public void test_686() { checkNotSubtype("!any","{{null f1} f1}"); }
	@Test public void test_687() { checkNotSubtype("!any","{{null f2} f1}"); }
	@Test public void test_688() { checkNotSubtype("!any","{{null f1} f2}"); }
	@Test public void test_689() { checkNotSubtype("!any","{{null f2} f2}"); }
	@Test public void test_690() { checkNotSubtype("!any","{!void f1}"); }
	@Test public void test_691() { checkNotSubtype("!any","{!void f2}"); }
	@Test public void test_692() { checkIsSubtype("!any","{!any f1}"); }
	@Test public void test_693() { checkIsSubtype("!any","{!any f2}"); }
	@Test public void test_694() { checkNotSubtype("!any","{!null f1}"); }
	@Test public void test_695() { checkNotSubtype("!any","{!null f2}"); }
	@Test public void test_696() { checkIsSubtype("!any","void|void"); }
	@Test public void test_697() { checkNotSubtype("!any","void|any"); }
	@Test public void test_698() { checkNotSubtype("!any","void|null"); }
	@Test public void test_699() { checkNotSubtype("!any","any|void"); }
	@Test public void test_700() { checkNotSubtype("!any","any|any"); }
	@Test public void test_701() { checkNotSubtype("!any","any|null"); }
	@Test public void test_702() { checkNotSubtype("!any","null|void"); }
	@Test public void test_703() { checkNotSubtype("!any","null|any"); }
	@Test public void test_704() { checkNotSubtype("!any","null|null"); }
	@Test public void test_705() { checkIsSubtype("!any","{void f1}|void"); }
	@Test public void test_706() { checkIsSubtype("!any","{void f2}|void"); }
	@Test public void test_707() { checkNotSubtype("!any","{any f1}|any"); }
	@Test public void test_708() { checkNotSubtype("!any","{any f2}|any"); }
	@Test public void test_709() { checkNotSubtype("!any","{null f1}|null"); }
	@Test public void test_710() { checkNotSubtype("!any","{null f2}|null"); }
	@Test public void test_711() { checkNotSubtype("!any","!void|void"); }
	@Test public void test_712() { checkNotSubtype("!any","!any|any"); }
	@Test public void test_713() { checkNotSubtype("!any","!null|null"); }
	@Test public void test_714() { checkIsSubtype("!any","void&void"); }
	@Test public void test_715() { checkIsSubtype("!any","void&any"); }
	@Test public void test_716() { checkIsSubtype("!any","void&null"); }
	@Test public void test_717() { checkIsSubtype("!any","any&void"); }
	@Test public void test_718() { checkNotSubtype("!any","any&any"); }
	@Test public void test_719() { checkNotSubtype("!any","any&null"); }
	@Test public void test_720() { checkIsSubtype("!any","null&void"); }
	@Test public void test_721() { checkNotSubtype("!any","null&any"); }
	@Test public void test_722() { checkNotSubtype("!any","null&null"); }
	@Test public void test_723() { checkIsSubtype("!any","{void f1}&void"); }
	@Test public void test_724() { checkIsSubtype("!any","{void f2}&void"); }
	@Test public void test_725() { checkNotSubtype("!any","{any f1}&any"); }
	@Test public void test_726() { checkNotSubtype("!any","{any f2}&any"); }
	@Test public void test_727() { checkIsSubtype("!any","{null f1}&null"); }
	@Test public void test_728() { checkIsSubtype("!any","{null f2}&null"); }
	@Test public void test_729() { checkIsSubtype("!any","!void&void"); }
	@Test public void test_730() { checkIsSubtype("!any","!any&any"); }
	@Test public void test_731() { checkIsSubtype("!any","!null&null"); }
	@Test public void test_732() { checkNotSubtype("!any","!{void f1}"); }
	@Test public void test_733() { checkNotSubtype("!any","!{void f2}"); }
	@Test public void test_734() { checkNotSubtype("!any","!{any f1}"); }
	@Test public void test_735() { checkNotSubtype("!any","!{any f2}"); }
	@Test public void test_736() { checkNotSubtype("!any","!{null f1}"); }
	@Test public void test_737() { checkNotSubtype("!any","!{null f2}"); }
	@Test public void test_738() { checkIsSubtype("!any","!!void"); }
	@Test public void test_739() { checkNotSubtype("!any","!!any"); }
	@Test public void test_740() { checkNotSubtype("!any","!!null"); }
	@Test public void test_741() { checkNotSubtype("!null","any"); }
	@Test public void test_742() { checkNotSubtype("!null","null"); }
	@Test public void test_743() { checkIsSubtype("!null","{void f1}"); }
	@Test public void test_744() { checkIsSubtype("!null","{void f2}"); }
	@Test public void test_745() { checkIsSubtype("!null","{any f1}"); }
	@Test public void test_746() { checkIsSubtype("!null","{any f2}"); }
	@Test public void test_747() { checkIsSubtype("!null","{null f1}"); }
	@Test public void test_748() { checkIsSubtype("!null","{null f2}"); }
	@Test public void test_749() { checkNotSubtype("!null","!void"); }
	@Test public void test_750() { checkIsSubtype("!null","!any"); }
	@Test public void test_751() { checkIsSubtype("!null","!null"); }
	@Test public void test_752() { checkIsSubtype("!null","{{void f1} f1}"); }
	@Test public void test_753() { checkIsSubtype("!null","{{void f2} f1}"); }
	@Test public void test_754() { checkIsSubtype("!null","{{void f1} f2}"); }
	@Test public void test_755() { checkIsSubtype("!null","{{void f2} f2}"); }
	@Test public void test_756() { checkIsSubtype("!null","{{any f1} f1}"); }
	@Test public void test_757() { checkIsSubtype("!null","{{any f2} f1}"); }
	@Test public void test_758() { checkIsSubtype("!null","{{any f1} f2}"); }
	@Test public void test_759() { checkIsSubtype("!null","{{any f2} f2}"); }
	@Test public void test_760() { checkIsSubtype("!null","{{null f1} f1}"); }
	@Test public void test_761() { checkIsSubtype("!null","{{null f2} f1}"); }
	@Test public void test_762() { checkIsSubtype("!null","{{null f1} f2}"); }
	@Test public void test_763() { checkIsSubtype("!null","{{null f2} f2}"); }
	@Test public void test_764() { checkIsSubtype("!null","{!void f1}"); }
	@Test public void test_765() { checkIsSubtype("!null","{!void f2}"); }
	@Test public void test_766() { checkIsSubtype("!null","{!any f1}"); }
	@Test public void test_767() { checkIsSubtype("!null","{!any f2}"); }
	@Test public void test_768() { checkIsSubtype("!null","{!null f1}"); }
	@Test public void test_769() { checkIsSubtype("!null","{!null f2}"); }
	@Test public void test_770() { checkIsSubtype("!null","void|void"); }
	@Test public void test_771() { checkNotSubtype("!null","void|any"); }
	@Test public void test_772() { checkNotSubtype("!null","void|null"); }
	@Test public void test_773() { checkNotSubtype("!null","any|void"); }
	@Test public void test_774() { checkNotSubtype("!null","any|any"); }
	@Test public void test_775() { checkNotSubtype("!null","any|null"); }
	@Test public void test_776() { checkNotSubtype("!null","null|void"); }
	@Test public void test_777() { checkNotSubtype("!null","null|any"); }
	@Test public void test_778() { checkNotSubtype("!null","null|null"); }
	@Test public void test_779() { checkIsSubtype("!null","{void f1}|void"); }
	@Test public void test_780() { checkIsSubtype("!null","{void f2}|void"); }
	@Test public void test_781() { checkNotSubtype("!null","{any f1}|any"); }
	@Test public void test_782() { checkNotSubtype("!null","{any f2}|any"); }
	@Test public void test_783() { checkNotSubtype("!null","{null f1}|null"); }
	@Test public void test_784() { checkNotSubtype("!null","{null f2}|null"); }
	@Test public void test_785() { checkNotSubtype("!null","!void|void"); }
	@Test public void test_786() { checkNotSubtype("!null","!any|any"); }
	@Test public void test_787() { checkNotSubtype("!null","!null|null"); }
	@Test public void test_788() { checkIsSubtype("!null","void&void"); }
	@Test public void test_789() { checkIsSubtype("!null","void&any"); }
	@Test public void test_790() { checkIsSubtype("!null","void&null"); }
	@Test public void test_791() { checkIsSubtype("!null","any&void"); }
	@Test public void test_792() { checkNotSubtype("!null","any&any"); }
	@Test public void test_793() { checkNotSubtype("!null","any&null"); }
	@Test public void test_794() { checkIsSubtype("!null","null&void"); }
	@Test public void test_795() { checkNotSubtype("!null","null&any"); }
	@Test public void test_796() { checkNotSubtype("!null","null&null"); }
	@Test public void test_797() { checkIsSubtype("!null","{void f1}&void"); }
	@Test public void test_798() { checkIsSubtype("!null","{void f2}&void"); }
	@Test public void test_799() { checkIsSubtype("!null","{any f1}&any"); }
	@Test public void test_800() { checkIsSubtype("!null","{any f2}&any"); }
	@Test public void test_801() { checkIsSubtype("!null","{null f1}&null"); }
	@Test public void test_802() { checkIsSubtype("!null","{null f2}&null"); }
	@Test public void test_803() { checkIsSubtype("!null","!void&void"); }
	@Test public void test_804() { checkIsSubtype("!null","!any&any"); }
	@Test public void test_805() { checkIsSubtype("!null","!null&null"); }
	@Test public void test_806() { checkNotSubtype("!null","!{void f1}"); }
	@Test public void test_807() { checkNotSubtype("!null","!{void f2}"); }
	@Test public void test_808() { checkNotSubtype("!null","!{any f1}"); }
	@Test public void test_809() { checkNotSubtype("!null","!{any f2}"); }
	@Test public void test_810() { checkNotSubtype("!null","!{null f1}"); }
	@Test public void test_811() { checkNotSubtype("!null","!{null f2}"); }
	@Test public void test_812() { checkIsSubtype("!null","!!void"); }
	@Test public void test_813() { checkNotSubtype("!null","!!any"); }
	@Test public void test_814() { checkNotSubtype("!null","!!null"); }
	@Test public void test_815() { checkNotSubtype("{{void f1} f1}","any"); }
	@Test public void test_816() { checkNotSubtype("{{void f1} f1}","null"); }
	@Test public void test_817() { checkIsSubtype("{{void f1} f1}","{void f1}"); }
	@Test public void test_818() { checkIsSubtype("{{void f1} f1}","{void f2}"); }
	@Test public void test_819() { checkNotSubtype("{{void f1} f1}","{any f1}"); }
	@Test public void test_820() { checkNotSubtype("{{void f1} f1}","{any f2}"); }
	@Test public void test_821() { checkNotSubtype("{{void f1} f1}","{null f1}"); }
	@Test public void test_822() { checkNotSubtype("{{void f1} f1}","{null f2}"); }
	@Test public void test_823() { checkNotSubtype("{{void f1} f1}","!void"); }
	@Test public void test_824() { checkIsSubtype("{{void f1} f1}","!any"); }
	@Test public void test_825() { checkNotSubtype("{{void f1} f1}","!null"); }
	@Test public void test_826() { checkIsSubtype("{{void f1} f1}","{{void f1} f1}"); }
	@Test public void test_827() { checkIsSubtype("{{void f1} f1}","{{void f2} f1}"); }
	@Test public void test_828() { checkIsSubtype("{{void f1} f1}","{{void f1} f2}"); }
	@Test public void test_829() { checkIsSubtype("{{void f1} f1}","{{void f2} f2}"); }
	@Test public void test_830() { checkNotSubtype("{{void f1} f1}","{{any f1} f1}"); }
	@Test public void test_831() { checkNotSubtype("{{void f1} f1}","{{any f2} f1}"); }
	@Test public void test_832() { checkNotSubtype("{{void f1} f1}","{{any f1} f2}"); }
	@Test public void test_833() { checkNotSubtype("{{void f1} f1}","{{any f2} f2}"); }
	@Test public void test_834() { checkNotSubtype("{{void f1} f1}","{{null f1} f1}"); }
	@Test public void test_835() { checkNotSubtype("{{void f1} f1}","{{null f2} f1}"); }
	@Test public void test_836() { checkNotSubtype("{{void f1} f1}","{{null f1} f2}"); }
	@Test public void test_837() { checkNotSubtype("{{void f1} f1}","{{null f2} f2}"); }
	@Test public void test_838() { checkNotSubtype("{{void f1} f1}","{!void f1}"); }
	@Test public void test_839() { checkNotSubtype("{{void f1} f1}","{!void f2}"); }
	@Test public void test_840() { checkIsSubtype("{{void f1} f1}","{!any f1}"); }
	@Test public void test_841() { checkIsSubtype("{{void f1} f1}","{!any f2}"); }
	@Test public void test_842() { checkNotSubtype("{{void f1} f1}","{!null f1}"); }
	@Test public void test_843() { checkNotSubtype("{{void f1} f1}","{!null f2}"); }
	@Test public void test_844() { checkIsSubtype("{{void f1} f1}","void|void"); }
	@Test public void test_845() { checkNotSubtype("{{void f1} f1}","void|any"); }
	@Test public void test_846() { checkNotSubtype("{{void f1} f1}","void|null"); }
	@Test public void test_847() { checkNotSubtype("{{void f1} f1}","any|void"); }
	@Test public void test_848() { checkNotSubtype("{{void f1} f1}","any|any"); }
	@Test public void test_849() { checkNotSubtype("{{void f1} f1}","any|null"); }
	@Test public void test_850() { checkNotSubtype("{{void f1} f1}","null|void"); }
	@Test public void test_851() { checkNotSubtype("{{void f1} f1}","null|any"); }
	@Test public void test_852() { checkNotSubtype("{{void f1} f1}","null|null"); }
	@Test public void test_853() { checkIsSubtype("{{void f1} f1}","{void f1}|void"); }
	@Test public void test_854() { checkIsSubtype("{{void f1} f1}","{void f2}|void"); }
	@Test public void test_855() { checkNotSubtype("{{void f1} f1}","{any f1}|any"); }
	@Test public void test_856() { checkNotSubtype("{{void f1} f1}","{any f2}|any"); }
	@Test public void test_857() { checkNotSubtype("{{void f1} f1}","{null f1}|null"); }
	@Test public void test_858() { checkNotSubtype("{{void f1} f1}","{null f2}|null"); }
	@Test public void test_859() { checkNotSubtype("{{void f1} f1}","!void|void"); }
	@Test public void test_860() { checkNotSubtype("{{void f1} f1}","!any|any"); }
	@Test public void test_861() { checkNotSubtype("{{void f1} f1}","!null|null"); }
	@Test public void test_862() { checkIsSubtype("{{void f1} f1}","void&void"); }
	@Test public void test_863() { checkIsSubtype("{{void f1} f1}","void&any"); }
	@Test public void test_864() { checkIsSubtype("{{void f1} f1}","void&null"); }
	@Test public void test_865() { checkIsSubtype("{{void f1} f1}","any&void"); }
	@Test public void test_866() { checkNotSubtype("{{void f1} f1}","any&any"); }
	@Test public void test_867() { checkNotSubtype("{{void f1} f1}","any&null"); }
	@Test public void test_868() { checkIsSubtype("{{void f1} f1}","null&void"); }
	@Test public void test_869() { checkNotSubtype("{{void f1} f1}","null&any"); }
	@Test public void test_870() { checkNotSubtype("{{void f1} f1}","null&null"); }
	@Test public void test_871() { checkIsSubtype("{{void f1} f1}","{void f1}&void"); }
	@Test public void test_872() { checkIsSubtype("{{void f1} f1}","{void f2}&void"); }
	@Test public void test_873() { checkNotSubtype("{{void f1} f1}","{any f1}&any"); }
	@Test public void test_874() { checkNotSubtype("{{void f1} f1}","{any f2}&any"); }
	@Test public void test_875() { checkIsSubtype("{{void f1} f1}","{null f1}&null"); }
	@Test public void test_876() { checkIsSubtype("{{void f1} f1}","{null f2}&null"); }
	@Test public void test_877() { checkIsSubtype("{{void f1} f1}","!void&void"); }
	@Test public void test_878() { checkIsSubtype("{{void f1} f1}","!any&any"); }
	@Test public void test_879() { checkIsSubtype("{{void f1} f1}","!null&null"); }
	@Test public void test_880() { checkNotSubtype("{{void f1} f1}","!{void f1}"); }
	@Test public void test_881() { checkNotSubtype("{{void f1} f1}","!{void f2}"); }
	@Test public void test_882() { checkNotSubtype("{{void f1} f1}","!{any f1}"); }
	@Test public void test_883() { checkNotSubtype("{{void f1} f1}","!{any f2}"); }
	@Test public void test_884() { checkNotSubtype("{{void f1} f1}","!{null f1}"); }
	@Test public void test_885() { checkNotSubtype("{{void f1} f1}","!{null f2}"); }
	@Test public void test_886() { checkIsSubtype("{{void f1} f1}","!!void"); }
	@Test public void test_887() { checkNotSubtype("{{void f1} f1}","!!any"); }
	@Test public void test_888() { checkNotSubtype("{{void f1} f1}","!!null"); }
	@Test public void test_889() { checkNotSubtype("{{void f2} f1}","any"); }
	@Test public void test_890() { checkNotSubtype("{{void f2} f1}","null"); }
	@Test public void test_891() { checkIsSubtype("{{void f2} f1}","{void f1}"); }
	@Test public void test_892() { checkIsSubtype("{{void f2} f1}","{void f2}"); }
	@Test public void test_893() { checkNotSubtype("{{void f2} f1}","{any f1}"); }
	@Test public void test_894() { checkNotSubtype("{{void f2} f1}","{any f2}"); }
	@Test public void test_895() { checkNotSubtype("{{void f2} f1}","{null f1}"); }
	@Test public void test_896() { checkNotSubtype("{{void f2} f1}","{null f2}"); }
	@Test public void test_897() { checkNotSubtype("{{void f2} f1}","!void"); }
	@Test public void test_898() { checkIsSubtype("{{void f2} f1}","!any"); }
	@Test public void test_899() { checkNotSubtype("{{void f2} f1}","!null"); }
	@Test public void test_900() { checkIsSubtype("{{void f2} f1}","{{void f1} f1}"); }
	@Test public void test_901() { checkIsSubtype("{{void f2} f1}","{{void f2} f1}"); }
	@Test public void test_902() { checkIsSubtype("{{void f2} f1}","{{void f1} f2}"); }
	@Test public void test_903() { checkIsSubtype("{{void f2} f1}","{{void f2} f2}"); }
	@Test public void test_904() { checkNotSubtype("{{void f2} f1}","{{any f1} f1}"); }
	@Test public void test_905() { checkNotSubtype("{{void f2} f1}","{{any f2} f1}"); }
	@Test public void test_906() { checkNotSubtype("{{void f2} f1}","{{any f1} f2}"); }
	@Test public void test_907() { checkNotSubtype("{{void f2} f1}","{{any f2} f2}"); }
	@Test public void test_908() { checkNotSubtype("{{void f2} f1}","{{null f1} f1}"); }
	@Test public void test_909() { checkNotSubtype("{{void f2} f1}","{{null f2} f1}"); }
	@Test public void test_910() { checkNotSubtype("{{void f2} f1}","{{null f1} f2}"); }
	@Test public void test_911() { checkNotSubtype("{{void f2} f1}","{{null f2} f2}"); }
	@Test public void test_912() { checkNotSubtype("{{void f2} f1}","{!void f1}"); }
	@Test public void test_913() { checkNotSubtype("{{void f2} f1}","{!void f2}"); }
	@Test public void test_914() { checkIsSubtype("{{void f2} f1}","{!any f1}"); }
	@Test public void test_915() { checkIsSubtype("{{void f2} f1}","{!any f2}"); }
	@Test public void test_916() { checkNotSubtype("{{void f2} f1}","{!null f1}"); }
	@Test public void test_917() { checkNotSubtype("{{void f2} f1}","{!null f2}"); }
	@Test public void test_918() { checkIsSubtype("{{void f2} f1}","void|void"); }
	@Test public void test_919() { checkNotSubtype("{{void f2} f1}","void|any"); }
	@Test public void test_920() { checkNotSubtype("{{void f2} f1}","void|null"); }
	@Test public void test_921() { checkNotSubtype("{{void f2} f1}","any|void"); }
	@Test public void test_922() { checkNotSubtype("{{void f2} f1}","any|any"); }
	@Test public void test_923() { checkNotSubtype("{{void f2} f1}","any|null"); }
	@Test public void test_924() { checkNotSubtype("{{void f2} f1}","null|void"); }
	@Test public void test_925() { checkNotSubtype("{{void f2} f1}","null|any"); }
	@Test public void test_926() { checkNotSubtype("{{void f2} f1}","null|null"); }
	@Test public void test_927() { checkIsSubtype("{{void f2} f1}","{void f1}|void"); }
	@Test public void test_928() { checkIsSubtype("{{void f2} f1}","{void f2}|void"); }
	@Test public void test_929() { checkNotSubtype("{{void f2} f1}","{any f1}|any"); }
	@Test public void test_930() { checkNotSubtype("{{void f2} f1}","{any f2}|any"); }
	@Test public void test_931() { checkNotSubtype("{{void f2} f1}","{null f1}|null"); }
	@Test public void test_932() { checkNotSubtype("{{void f2} f1}","{null f2}|null"); }
	@Test public void test_933() { checkNotSubtype("{{void f2} f1}","!void|void"); }
	@Test public void test_934() { checkNotSubtype("{{void f2} f1}","!any|any"); }
	@Test public void test_935() { checkNotSubtype("{{void f2} f1}","!null|null"); }
	@Test public void test_936() { checkIsSubtype("{{void f2} f1}","void&void"); }
	@Test public void test_937() { checkIsSubtype("{{void f2} f1}","void&any"); }
	@Test public void test_938() { checkIsSubtype("{{void f2} f1}","void&null"); }
	@Test public void test_939() { checkIsSubtype("{{void f2} f1}","any&void"); }
	@Test public void test_940() { checkNotSubtype("{{void f2} f1}","any&any"); }
	@Test public void test_941() { checkNotSubtype("{{void f2} f1}","any&null"); }
	@Test public void test_942() { checkIsSubtype("{{void f2} f1}","null&void"); }
	@Test public void test_943() { checkNotSubtype("{{void f2} f1}","null&any"); }
	@Test public void test_944() { checkNotSubtype("{{void f2} f1}","null&null"); }
	@Test public void test_945() { checkIsSubtype("{{void f2} f1}","{void f1}&void"); }
	@Test public void test_946() { checkIsSubtype("{{void f2} f1}","{void f2}&void"); }
	@Test public void test_947() { checkNotSubtype("{{void f2} f1}","{any f1}&any"); }
	@Test public void test_948() { checkNotSubtype("{{void f2} f1}","{any f2}&any"); }
	@Test public void test_949() { checkIsSubtype("{{void f2} f1}","{null f1}&null"); }
	@Test public void test_950() { checkIsSubtype("{{void f2} f1}","{null f2}&null"); }
	@Test public void test_951() { checkIsSubtype("{{void f2} f1}","!void&void"); }
	@Test public void test_952() { checkIsSubtype("{{void f2} f1}","!any&any"); }
	@Test public void test_953() { checkIsSubtype("{{void f2} f1}","!null&null"); }
	@Test public void test_954() { checkNotSubtype("{{void f2} f1}","!{void f1}"); }
	@Test public void test_955() { checkNotSubtype("{{void f2} f1}","!{void f2}"); }
	@Test public void test_956() { checkNotSubtype("{{void f2} f1}","!{any f1}"); }
	@Test public void test_957() { checkNotSubtype("{{void f2} f1}","!{any f2}"); }
	@Test public void test_958() { checkNotSubtype("{{void f2} f1}","!{null f1}"); }
	@Test public void test_959() { checkNotSubtype("{{void f2} f1}","!{null f2}"); }
	@Test public void test_960() { checkIsSubtype("{{void f2} f1}","!!void"); }
	@Test public void test_961() { checkNotSubtype("{{void f2} f1}","!!any"); }
	@Test public void test_962() { checkNotSubtype("{{void f2} f1}","!!null"); }
	@Test public void test_963() { checkNotSubtype("{{void f1} f2}","any"); }
	@Test public void test_964() { checkNotSubtype("{{void f1} f2}","null"); }
	@Test public void test_965() { checkIsSubtype("{{void f1} f2}","{void f1}"); }
	@Test public void test_966() { checkIsSubtype("{{void f1} f2}","{void f2}"); }
	@Test public void test_967() { checkNotSubtype("{{void f1} f2}","{any f1}"); }
	@Test public void test_968() { checkNotSubtype("{{void f1} f2}","{any f2}"); }
	@Test public void test_969() { checkNotSubtype("{{void f1} f2}","{null f1}"); }
	@Test public void test_970() { checkNotSubtype("{{void f1} f2}","{null f2}"); }
	@Test public void test_971() { checkNotSubtype("{{void f1} f2}","!void"); }
	@Test public void test_972() { checkIsSubtype("{{void f1} f2}","!any"); }
	@Test public void test_973() { checkNotSubtype("{{void f1} f2}","!null"); }
	@Test public void test_974() { checkIsSubtype("{{void f1} f2}","{{void f1} f1}"); }
	@Test public void test_975() { checkIsSubtype("{{void f1} f2}","{{void f2} f1}"); }
	@Test public void test_976() { checkIsSubtype("{{void f1} f2}","{{void f1} f2}"); }
	@Test public void test_977() { checkIsSubtype("{{void f1} f2}","{{void f2} f2}"); }
	@Test public void test_978() { checkNotSubtype("{{void f1} f2}","{{any f1} f1}"); }
	@Test public void test_979() { checkNotSubtype("{{void f1} f2}","{{any f2} f1}"); }
	@Test public void test_980() { checkNotSubtype("{{void f1} f2}","{{any f1} f2}"); }
	@Test public void test_981() { checkNotSubtype("{{void f1} f2}","{{any f2} f2}"); }
	@Test public void test_982() { checkNotSubtype("{{void f1} f2}","{{null f1} f1}"); }
	@Test public void test_983() { checkNotSubtype("{{void f1} f2}","{{null f2} f1}"); }
	@Test public void test_984() { checkNotSubtype("{{void f1} f2}","{{null f1} f2}"); }
	@Test public void test_985() { checkNotSubtype("{{void f1} f2}","{{null f2} f2}"); }
	@Test public void test_986() { checkNotSubtype("{{void f1} f2}","{!void f1}"); }
	@Test public void test_987() { checkNotSubtype("{{void f1} f2}","{!void f2}"); }
	@Test public void test_988() { checkIsSubtype("{{void f1} f2}","{!any f1}"); }
	@Test public void test_989() { checkIsSubtype("{{void f1} f2}","{!any f2}"); }
	@Test public void test_990() { checkNotSubtype("{{void f1} f2}","{!null f1}"); }
	@Test public void test_991() { checkNotSubtype("{{void f1} f2}","{!null f2}"); }
	@Test public void test_992() { checkIsSubtype("{{void f1} f2}","void|void"); }
	@Test public void test_993() { checkNotSubtype("{{void f1} f2}","void|any"); }
	@Test public void test_994() { checkNotSubtype("{{void f1} f2}","void|null"); }
	@Test public void test_995() { checkNotSubtype("{{void f1} f2}","any|void"); }
	@Test public void test_996() { checkNotSubtype("{{void f1} f2}","any|any"); }
	@Test public void test_997() { checkNotSubtype("{{void f1} f2}","any|null"); }
	@Test public void test_998() { checkNotSubtype("{{void f1} f2}","null|void"); }
	@Test public void test_999() { checkNotSubtype("{{void f1} f2}","null|any"); }
	@Test public void test_1000() { checkNotSubtype("{{void f1} f2}","null|null"); }
	@Test public void test_1001() { checkIsSubtype("{{void f1} f2}","{void f1}|void"); }
	@Test public void test_1002() { checkIsSubtype("{{void f1} f2}","{void f2}|void"); }
	@Test public void test_1003() { checkNotSubtype("{{void f1} f2}","{any f1}|any"); }
	@Test public void test_1004() { checkNotSubtype("{{void f1} f2}","{any f2}|any"); }
	@Test public void test_1005() { checkNotSubtype("{{void f1} f2}","{null f1}|null"); }
	@Test public void test_1006() { checkNotSubtype("{{void f1} f2}","{null f2}|null"); }
	@Test public void test_1007() { checkNotSubtype("{{void f1} f2}","!void|void"); }
	@Test public void test_1008() { checkNotSubtype("{{void f1} f2}","!any|any"); }
	@Test public void test_1009() { checkNotSubtype("{{void f1} f2}","!null|null"); }
	@Test public void test_1010() { checkIsSubtype("{{void f1} f2}","void&void"); }
	@Test public void test_1011() { checkIsSubtype("{{void f1} f2}","void&any"); }
	@Test public void test_1012() { checkIsSubtype("{{void f1} f2}","void&null"); }
	@Test public void test_1013() { checkIsSubtype("{{void f1} f2}","any&void"); }
	@Test public void test_1014() { checkNotSubtype("{{void f1} f2}","any&any"); }
	@Test public void test_1015() { checkNotSubtype("{{void f1} f2}","any&null"); }
	@Test public void test_1016() { checkIsSubtype("{{void f1} f2}","null&void"); }
	@Test public void test_1017() { checkNotSubtype("{{void f1} f2}","null&any"); }
	@Test public void test_1018() { checkNotSubtype("{{void f1} f2}","null&null"); }
	@Test public void test_1019() { checkIsSubtype("{{void f1} f2}","{void f1}&void"); }
	@Test public void test_1020() { checkIsSubtype("{{void f1} f2}","{void f2}&void"); }
	@Test public void test_1021() { checkNotSubtype("{{void f1} f2}","{any f1}&any"); }
	@Test public void test_1022() { checkNotSubtype("{{void f1} f2}","{any f2}&any"); }
	@Test public void test_1023() { checkIsSubtype("{{void f1} f2}","{null f1}&null"); }
	@Test public void test_1024() { checkIsSubtype("{{void f1} f2}","{null f2}&null"); }
	@Test public void test_1025() { checkIsSubtype("{{void f1} f2}","!void&void"); }
	@Test public void test_1026() { checkIsSubtype("{{void f1} f2}","!any&any"); }
	@Test public void test_1027() { checkIsSubtype("{{void f1} f2}","!null&null"); }
	@Test public void test_1028() { checkNotSubtype("{{void f1} f2}","!{void f1}"); }
	@Test public void test_1029() { checkNotSubtype("{{void f1} f2}","!{void f2}"); }
	@Test public void test_1030() { checkNotSubtype("{{void f1} f2}","!{any f1}"); }
	@Test public void test_1031() { checkNotSubtype("{{void f1} f2}","!{any f2}"); }
	@Test public void test_1032() { checkNotSubtype("{{void f1} f2}","!{null f1}"); }
	@Test public void test_1033() { checkNotSubtype("{{void f1} f2}","!{null f2}"); }
	@Test public void test_1034() { checkIsSubtype("{{void f1} f2}","!!void"); }
	@Test public void test_1035() { checkNotSubtype("{{void f1} f2}","!!any"); }
	@Test public void test_1036() { checkNotSubtype("{{void f1} f2}","!!null"); }
	@Test public void test_1037() { checkNotSubtype("{{void f2} f2}","any"); }
	@Test public void test_1038() { checkNotSubtype("{{void f2} f2}","null"); }
	@Test public void test_1039() { checkIsSubtype("{{void f2} f2}","{void f1}"); }
	@Test public void test_1040() { checkIsSubtype("{{void f2} f2}","{void f2}"); }
	@Test public void test_1041() { checkNotSubtype("{{void f2} f2}","{any f1}"); }
	@Test public void test_1042() { checkNotSubtype("{{void f2} f2}","{any f2}"); }
	@Test public void test_1043() { checkNotSubtype("{{void f2} f2}","{null f1}"); }
	@Test public void test_1044() { checkNotSubtype("{{void f2} f2}","{null f2}"); }
	@Test public void test_1045() { checkNotSubtype("{{void f2} f2}","!void"); }
	@Test public void test_1046() { checkIsSubtype("{{void f2} f2}","!any"); }
	@Test public void test_1047() { checkNotSubtype("{{void f2} f2}","!null"); }
	@Test public void test_1048() { checkIsSubtype("{{void f2} f2}","{{void f1} f1}"); }
	@Test public void test_1049() { checkIsSubtype("{{void f2} f2}","{{void f2} f1}"); }
	@Test public void test_1050() { checkIsSubtype("{{void f2} f2}","{{void f1} f2}"); }
	@Test public void test_1051() { checkIsSubtype("{{void f2} f2}","{{void f2} f2}"); }
	@Test public void test_1052() { checkNotSubtype("{{void f2} f2}","{{any f1} f1}"); }
	@Test public void test_1053() { checkNotSubtype("{{void f2} f2}","{{any f2} f1}"); }
	@Test public void test_1054() { checkNotSubtype("{{void f2} f2}","{{any f1} f2}"); }
	@Test public void test_1055() { checkNotSubtype("{{void f2} f2}","{{any f2} f2}"); }
	@Test public void test_1056() { checkNotSubtype("{{void f2} f2}","{{null f1} f1}"); }
	@Test public void test_1057() { checkNotSubtype("{{void f2} f2}","{{null f2} f1}"); }
	@Test public void test_1058() { checkNotSubtype("{{void f2} f2}","{{null f1} f2}"); }
	@Test public void test_1059() { checkNotSubtype("{{void f2} f2}","{{null f2} f2}"); }
	@Test public void test_1060() { checkNotSubtype("{{void f2} f2}","{!void f1}"); }
	@Test public void test_1061() { checkNotSubtype("{{void f2} f2}","{!void f2}"); }
	@Test public void test_1062() { checkIsSubtype("{{void f2} f2}","{!any f1}"); }
	@Test public void test_1063() { checkIsSubtype("{{void f2} f2}","{!any f2}"); }
	@Test public void test_1064() { checkNotSubtype("{{void f2} f2}","{!null f1}"); }
	@Test public void test_1065() { checkNotSubtype("{{void f2} f2}","{!null f2}"); }
	@Test public void test_1066() { checkIsSubtype("{{void f2} f2}","void|void"); }
	@Test public void test_1067() { checkNotSubtype("{{void f2} f2}","void|any"); }
	@Test public void test_1068() { checkNotSubtype("{{void f2} f2}","void|null"); }
	@Test public void test_1069() { checkNotSubtype("{{void f2} f2}","any|void"); }
	@Test public void test_1070() { checkNotSubtype("{{void f2} f2}","any|any"); }
	@Test public void test_1071() { checkNotSubtype("{{void f2} f2}","any|null"); }
	@Test public void test_1072() { checkNotSubtype("{{void f2} f2}","null|void"); }
	@Test public void test_1073() { checkNotSubtype("{{void f2} f2}","null|any"); }
	@Test public void test_1074() { checkNotSubtype("{{void f2} f2}","null|null"); }
	@Test public void test_1075() { checkIsSubtype("{{void f2} f2}","{void f1}|void"); }
	@Test public void test_1076() { checkIsSubtype("{{void f2} f2}","{void f2}|void"); }
	@Test public void test_1077() { checkNotSubtype("{{void f2} f2}","{any f1}|any"); }
	@Test public void test_1078() { checkNotSubtype("{{void f2} f2}","{any f2}|any"); }
	@Test public void test_1079() { checkNotSubtype("{{void f2} f2}","{null f1}|null"); }
	@Test public void test_1080() { checkNotSubtype("{{void f2} f2}","{null f2}|null"); }
	@Test public void test_1081() { checkNotSubtype("{{void f2} f2}","!void|void"); }
	@Test public void test_1082() { checkNotSubtype("{{void f2} f2}","!any|any"); }
	@Test public void test_1083() { checkNotSubtype("{{void f2} f2}","!null|null"); }
	@Test public void test_1084() { checkIsSubtype("{{void f2} f2}","void&void"); }
	@Test public void test_1085() { checkIsSubtype("{{void f2} f2}","void&any"); }
	@Test public void test_1086() { checkIsSubtype("{{void f2} f2}","void&null"); }
	@Test public void test_1087() { checkIsSubtype("{{void f2} f2}","any&void"); }
	@Test public void test_1088() { checkNotSubtype("{{void f2} f2}","any&any"); }
	@Test public void test_1089() { checkNotSubtype("{{void f2} f2}","any&null"); }
	@Test public void test_1090() { checkIsSubtype("{{void f2} f2}","null&void"); }
	@Test public void test_1091() { checkNotSubtype("{{void f2} f2}","null&any"); }
	@Test public void test_1092() { checkNotSubtype("{{void f2} f2}","null&null"); }
	@Test public void test_1093() { checkIsSubtype("{{void f2} f2}","{void f1}&void"); }
	@Test public void test_1094() { checkIsSubtype("{{void f2} f2}","{void f2}&void"); }
	@Test public void test_1095() { checkNotSubtype("{{void f2} f2}","{any f1}&any"); }
	@Test public void test_1096() { checkNotSubtype("{{void f2} f2}","{any f2}&any"); }
	@Test public void test_1097() { checkIsSubtype("{{void f2} f2}","{null f1}&null"); }
	@Test public void test_1098() { checkIsSubtype("{{void f2} f2}","{null f2}&null"); }
	@Test public void test_1099() { checkIsSubtype("{{void f2} f2}","!void&void"); }
	@Test public void test_1100() { checkIsSubtype("{{void f2} f2}","!any&any"); }
	@Test public void test_1101() { checkIsSubtype("{{void f2} f2}","!null&null"); }
	@Test public void test_1102() { checkNotSubtype("{{void f2} f2}","!{void f1}"); }
	@Test public void test_1103() { checkNotSubtype("{{void f2} f2}","!{void f2}"); }
	@Test public void test_1104() { checkNotSubtype("{{void f2} f2}","!{any f1}"); }
	@Test public void test_1105() { checkNotSubtype("{{void f2} f2}","!{any f2}"); }
	@Test public void test_1106() { checkNotSubtype("{{void f2} f2}","!{null f1}"); }
	@Test public void test_1107() { checkNotSubtype("{{void f2} f2}","!{null f2}"); }
	@Test public void test_1108() { checkIsSubtype("{{void f2} f2}","!!void"); }
	@Test public void test_1109() { checkNotSubtype("{{void f2} f2}","!!any"); }
	@Test public void test_1110() { checkNotSubtype("{{void f2} f2}","!!null"); }
	@Test public void test_1111() { checkNotSubtype("{{any f1} f1}","any"); }
	@Test public void test_1112() { checkNotSubtype("{{any f1} f1}","null"); }
	@Test public void test_1113() { checkIsSubtype("{{any f1} f1}","{void f1}"); }
	@Test public void test_1114() { checkIsSubtype("{{any f1} f1}","{void f2}"); }
	@Test public void test_1115() { checkNotSubtype("{{any f1} f1}","{any f1}"); }
	@Test public void test_1116() { checkNotSubtype("{{any f1} f1}","{any f2}"); }
	@Test public void test_1117() { checkNotSubtype("{{any f1} f1}","{null f1}"); }
	@Test public void test_1118() { checkNotSubtype("{{any f1} f1}","{null f2}"); }
	@Test public void test_1119() { checkNotSubtype("{{any f1} f1}","!void"); }
	@Test public void test_1120() { checkIsSubtype("{{any f1} f1}","!any"); }
	@Test public void test_1121() { checkNotSubtype("{{any f1} f1}","!null"); }
	@Test public void test_1122() { checkIsSubtype("{{any f1} f1}","{{void f1} f1}"); }
	@Test public void test_1123() { checkIsSubtype("{{any f1} f1}","{{void f2} f1}"); }
	@Test public void test_1124() { checkIsSubtype("{{any f1} f1}","{{void f1} f2}"); }
	@Test public void test_1125() { checkIsSubtype("{{any f1} f1}","{{void f2} f2}"); }
	@Test public void test_1126() { checkIsSubtype("{{any f1} f1}","{{any f1} f1}"); }
	@Test public void test_1127() { checkNotSubtype("{{any f1} f1}","{{any f2} f1}"); }
	@Test public void test_1128() { checkNotSubtype("{{any f1} f1}","{{any f1} f2}"); }
	@Test public void test_1129() { checkNotSubtype("{{any f1} f1}","{{any f2} f2}"); }
	@Test public void test_1130() { checkIsSubtype("{{any f1} f1}","{{null f1} f1}"); }
	@Test public void test_1131() { checkNotSubtype("{{any f1} f1}","{{null f2} f1}"); }
	@Test public void test_1132() { checkNotSubtype("{{any f1} f1}","{{null f1} f2}"); }
	@Test public void test_1133() { checkNotSubtype("{{any f1} f1}","{{null f2} f2}"); }
	@Test public void test_1134() { checkNotSubtype("{{any f1} f1}","{!void f1}"); }
	@Test public void test_1135() { checkNotSubtype("{{any f1} f1}","{!void f2}"); }
	@Test public void test_1136() { checkIsSubtype("{{any f1} f1}","{!any f1}"); }
	@Test public void test_1137() { checkIsSubtype("{{any f1} f1}","{!any f2}"); }
	@Test public void test_1138() { checkNotSubtype("{{any f1} f1}","{!null f1}"); }
	@Test public void test_1139() { checkNotSubtype("{{any f1} f1}","{!null f2}"); }
	@Test public void test_1140() { checkIsSubtype("{{any f1} f1}","void|void"); }
	@Test public void test_1141() { checkNotSubtype("{{any f1} f1}","void|any"); }
	@Test public void test_1142() { checkNotSubtype("{{any f1} f1}","void|null"); }
	@Test public void test_1143() { checkNotSubtype("{{any f1} f1}","any|void"); }
	@Test public void test_1144() { checkNotSubtype("{{any f1} f1}","any|any"); }
	@Test public void test_1145() { checkNotSubtype("{{any f1} f1}","any|null"); }
	@Test public void test_1146() { checkNotSubtype("{{any f1} f1}","null|void"); }
	@Test public void test_1147() { checkNotSubtype("{{any f1} f1}","null|any"); }
	@Test public void test_1148() { checkNotSubtype("{{any f1} f1}","null|null"); }
	@Test public void test_1149() { checkIsSubtype("{{any f1} f1}","{void f1}|void"); }
	@Test public void test_1150() { checkIsSubtype("{{any f1} f1}","{void f2}|void"); }
	@Test public void test_1151() { checkNotSubtype("{{any f1} f1}","{any f1}|any"); }
	@Test public void test_1152() { checkNotSubtype("{{any f1} f1}","{any f2}|any"); }
	@Test public void test_1153() { checkNotSubtype("{{any f1} f1}","{null f1}|null"); }
	@Test public void test_1154() { checkNotSubtype("{{any f1} f1}","{null f2}|null"); }
	@Test public void test_1155() { checkNotSubtype("{{any f1} f1}","!void|void"); }
	@Test public void test_1156() { checkNotSubtype("{{any f1} f1}","!any|any"); }
	@Test public void test_1157() { checkNotSubtype("{{any f1} f1}","!null|null"); }
	@Test public void test_1158() { checkIsSubtype("{{any f1} f1}","void&void"); }
	@Test public void test_1159() { checkIsSubtype("{{any f1} f1}","void&any"); }
	@Test public void test_1160() { checkIsSubtype("{{any f1} f1}","void&null"); }
	@Test public void test_1161() { checkIsSubtype("{{any f1} f1}","any&void"); }
	@Test public void test_1162() { checkNotSubtype("{{any f1} f1}","any&any"); }
	@Test public void test_1163() { checkNotSubtype("{{any f1} f1}","any&null"); }
	@Test public void test_1164() { checkIsSubtype("{{any f1} f1}","null&void"); }
	@Test public void test_1165() { checkNotSubtype("{{any f1} f1}","null&any"); }
	@Test public void test_1166() { checkNotSubtype("{{any f1} f1}","null&null"); }
	@Test public void test_1167() { checkIsSubtype("{{any f1} f1}","{void f1}&void"); }
	@Test public void test_1168() { checkIsSubtype("{{any f1} f1}","{void f2}&void"); }
	@Test public void test_1169() { checkNotSubtype("{{any f1} f1}","{any f1}&any"); }
	@Test public void test_1170() { checkNotSubtype("{{any f1} f1}","{any f2}&any"); }
	@Test public void test_1171() { checkIsSubtype("{{any f1} f1}","{null f1}&null"); }
	@Test public void test_1172() { checkIsSubtype("{{any f1} f1}","{null f2}&null"); }
	@Test public void test_1173() { checkIsSubtype("{{any f1} f1}","!void&void"); }
	@Test public void test_1174() { checkIsSubtype("{{any f1} f1}","!any&any"); }
	@Test public void test_1175() { checkIsSubtype("{{any f1} f1}","!null&null"); }
	@Test public void test_1176() { checkNotSubtype("{{any f1} f1}","!{void f1}"); }
	@Test public void test_1177() { checkNotSubtype("{{any f1} f1}","!{void f2}"); }
	@Test public void test_1178() { checkNotSubtype("{{any f1} f1}","!{any f1}"); }
	@Test public void test_1179() { checkNotSubtype("{{any f1} f1}","!{any f2}"); }
	@Test public void test_1180() { checkNotSubtype("{{any f1} f1}","!{null f1}"); }
	@Test public void test_1181() { checkNotSubtype("{{any f1} f1}","!{null f2}"); }
	@Test public void test_1182() { checkIsSubtype("{{any f1} f1}","!!void"); }
	@Test public void test_1183() { checkNotSubtype("{{any f1} f1}","!!any"); }
	@Test public void test_1184() { checkNotSubtype("{{any f1} f1}","!!null"); }
	@Test public void test_1185() { checkNotSubtype("{{any f2} f1}","any"); }
	@Test public void test_1186() { checkNotSubtype("{{any f2} f1}","null"); }
	@Test public void test_1187() { checkIsSubtype("{{any f2} f1}","{void f1}"); }
	@Test public void test_1188() { checkIsSubtype("{{any f2} f1}","{void f2}"); }
	@Test public void test_1189() { checkNotSubtype("{{any f2} f1}","{any f1}"); }
	@Test public void test_1190() { checkNotSubtype("{{any f2} f1}","{any f2}"); }
	@Test public void test_1191() { checkNotSubtype("{{any f2} f1}","{null f1}"); }
	@Test public void test_1192() { checkNotSubtype("{{any f2} f1}","{null f2}"); }
	@Test public void test_1193() { checkNotSubtype("{{any f2} f1}","!void"); }
	@Test public void test_1194() { checkIsSubtype("{{any f2} f1}","!any"); }
	@Test public void test_1195() { checkNotSubtype("{{any f2} f1}","!null"); }
	@Test public void test_1196() { checkIsSubtype("{{any f2} f1}","{{void f1} f1}"); }
	@Test public void test_1197() { checkIsSubtype("{{any f2} f1}","{{void f2} f1}"); }
	@Test public void test_1198() { checkIsSubtype("{{any f2} f1}","{{void f1} f2}"); }
	@Test public void test_1199() { checkIsSubtype("{{any f2} f1}","{{void f2} f2}"); }
	@Test public void test_1200() { checkNotSubtype("{{any f2} f1}","{{any f1} f1}"); }
	@Test public void test_1201() { checkIsSubtype("{{any f2} f1}","{{any f2} f1}"); }
	@Test public void test_1202() { checkNotSubtype("{{any f2} f1}","{{any f1} f2}"); }
	@Test public void test_1203() { checkNotSubtype("{{any f2} f1}","{{any f2} f2}"); }
	@Test public void test_1204() { checkNotSubtype("{{any f2} f1}","{{null f1} f1}"); }
	@Test public void test_1205() { checkIsSubtype("{{any f2} f1}","{{null f2} f1}"); }
	@Test public void test_1206() { checkNotSubtype("{{any f2} f1}","{{null f1} f2}"); }
	@Test public void test_1207() { checkNotSubtype("{{any f2} f1}","{{null f2} f2}"); }
	@Test public void test_1208() { checkNotSubtype("{{any f2} f1}","{!void f1}"); }
	@Test public void test_1209() { checkNotSubtype("{{any f2} f1}","{!void f2}"); }
	@Test public void test_1210() { checkIsSubtype("{{any f2} f1}","{!any f1}"); }
	@Test public void test_1211() { checkIsSubtype("{{any f2} f1}","{!any f2}"); }
	@Test public void test_1212() { checkNotSubtype("{{any f2} f1}","{!null f1}"); }
	@Test public void test_1213() { checkNotSubtype("{{any f2} f1}","{!null f2}"); }
	@Test public void test_1214() { checkIsSubtype("{{any f2} f1}","void|void"); }
	@Test public void test_1215() { checkNotSubtype("{{any f2} f1}","void|any"); }
	@Test public void test_1216() { checkNotSubtype("{{any f2} f1}","void|null"); }
	@Test public void test_1217() { checkNotSubtype("{{any f2} f1}","any|void"); }
	@Test public void test_1218() { checkNotSubtype("{{any f2} f1}","any|any"); }
	@Test public void test_1219() { checkNotSubtype("{{any f2} f1}","any|null"); }
	@Test public void test_1220() { checkNotSubtype("{{any f2} f1}","null|void"); }
	@Test public void test_1221() { checkNotSubtype("{{any f2} f1}","null|any"); }
	@Test public void test_1222() { checkNotSubtype("{{any f2} f1}","null|null"); }
	@Test public void test_1223() { checkIsSubtype("{{any f2} f1}","{void f1}|void"); }
	@Test public void test_1224() { checkIsSubtype("{{any f2} f1}","{void f2}|void"); }
	@Test public void test_1225() { checkNotSubtype("{{any f2} f1}","{any f1}|any"); }
	@Test public void test_1226() { checkNotSubtype("{{any f2} f1}","{any f2}|any"); }
	@Test public void test_1227() { checkNotSubtype("{{any f2} f1}","{null f1}|null"); }
	@Test public void test_1228() { checkNotSubtype("{{any f2} f1}","{null f2}|null"); }
	@Test public void test_1229() { checkNotSubtype("{{any f2} f1}","!void|void"); }
	@Test public void test_1230() { checkNotSubtype("{{any f2} f1}","!any|any"); }
	@Test public void test_1231() { checkNotSubtype("{{any f2} f1}","!null|null"); }
	@Test public void test_1232() { checkIsSubtype("{{any f2} f1}","void&void"); }
	@Test public void test_1233() { checkIsSubtype("{{any f2} f1}","void&any"); }
	@Test public void test_1234() { checkIsSubtype("{{any f2} f1}","void&null"); }
	@Test public void test_1235() { checkIsSubtype("{{any f2} f1}","any&void"); }
	@Test public void test_1236() { checkNotSubtype("{{any f2} f1}","any&any"); }
	@Test public void test_1237() { checkNotSubtype("{{any f2} f1}","any&null"); }
	@Test public void test_1238() { checkIsSubtype("{{any f2} f1}","null&void"); }
	@Test public void test_1239() { checkNotSubtype("{{any f2} f1}","null&any"); }
	@Test public void test_1240() { checkNotSubtype("{{any f2} f1}","null&null"); }
	@Test public void test_1241() { checkIsSubtype("{{any f2} f1}","{void f1}&void"); }
	@Test public void test_1242() { checkIsSubtype("{{any f2} f1}","{void f2}&void"); }
	@Test public void test_1243() { checkNotSubtype("{{any f2} f1}","{any f1}&any"); }
	@Test public void test_1244() { checkNotSubtype("{{any f2} f1}","{any f2}&any"); }
	@Test public void test_1245() { checkIsSubtype("{{any f2} f1}","{null f1}&null"); }
	@Test public void test_1246() { checkIsSubtype("{{any f2} f1}","{null f2}&null"); }
	@Test public void test_1247() { checkIsSubtype("{{any f2} f1}","!void&void"); }
	@Test public void test_1248() { checkIsSubtype("{{any f2} f1}","!any&any"); }
	@Test public void test_1249() { checkIsSubtype("{{any f2} f1}","!null&null"); }
	@Test public void test_1250() { checkNotSubtype("{{any f2} f1}","!{void f1}"); }
	@Test public void test_1251() { checkNotSubtype("{{any f2} f1}","!{void f2}"); }
	@Test public void test_1252() { checkNotSubtype("{{any f2} f1}","!{any f1}"); }
	@Test public void test_1253() { checkNotSubtype("{{any f2} f1}","!{any f2}"); }
	@Test public void test_1254() { checkNotSubtype("{{any f2} f1}","!{null f1}"); }
	@Test public void test_1255() { checkNotSubtype("{{any f2} f1}","!{null f2}"); }
	@Test public void test_1256() { checkIsSubtype("{{any f2} f1}","!!void"); }
	@Test public void test_1257() { checkNotSubtype("{{any f2} f1}","!!any"); }
	@Test public void test_1258() { checkNotSubtype("{{any f2} f1}","!!null"); }
	@Test public void test_1259() { checkNotSubtype("{{any f1} f2}","any"); }
	@Test public void test_1260() { checkNotSubtype("{{any f1} f2}","null"); }
	@Test public void test_1261() { checkIsSubtype("{{any f1} f2}","{void f1}"); }
	@Test public void test_1262() { checkIsSubtype("{{any f1} f2}","{void f2}"); }
	@Test public void test_1263() { checkNotSubtype("{{any f1} f2}","{any f1}"); }
	@Test public void test_1264() { checkNotSubtype("{{any f1} f2}","{any f2}"); }
	@Test public void test_1265() { checkNotSubtype("{{any f1} f2}","{null f1}"); }
	@Test public void test_1266() { checkNotSubtype("{{any f1} f2}","{null f2}"); }
	@Test public void test_1267() { checkNotSubtype("{{any f1} f2}","!void"); }
	@Test public void test_1268() { checkIsSubtype("{{any f1} f2}","!any"); }
	@Test public void test_1269() { checkNotSubtype("{{any f1} f2}","!null"); }
	@Test public void test_1270() { checkIsSubtype("{{any f1} f2}","{{void f1} f1}"); }
	@Test public void test_1271() { checkIsSubtype("{{any f1} f2}","{{void f2} f1}"); }
	@Test public void test_1272() { checkIsSubtype("{{any f1} f2}","{{void f1} f2}"); }
	@Test public void test_1273() { checkIsSubtype("{{any f1} f2}","{{void f2} f2}"); }
	@Test public void test_1274() { checkNotSubtype("{{any f1} f2}","{{any f1} f1}"); }
	@Test public void test_1275() { checkNotSubtype("{{any f1} f2}","{{any f2} f1}"); }
	@Test public void test_1276() { checkIsSubtype("{{any f1} f2}","{{any f1} f2}"); }
	@Test public void test_1277() { checkNotSubtype("{{any f1} f2}","{{any f2} f2}"); }
	@Test public void test_1278() { checkNotSubtype("{{any f1} f2}","{{null f1} f1}"); }
	@Test public void test_1279() { checkNotSubtype("{{any f1} f2}","{{null f2} f1}"); }
	@Test public void test_1280() { checkIsSubtype("{{any f1} f2}","{{null f1} f2}"); }
	@Test public void test_1281() { checkNotSubtype("{{any f1} f2}","{{null f2} f2}"); }
	@Test public void test_1282() { checkNotSubtype("{{any f1} f2}","{!void f1}"); }
	@Test public void test_1283() { checkNotSubtype("{{any f1} f2}","{!void f2}"); }
	@Test public void test_1284() { checkIsSubtype("{{any f1} f2}","{!any f1}"); }
	@Test public void test_1285() { checkIsSubtype("{{any f1} f2}","{!any f2}"); }
	@Test public void test_1286() { checkNotSubtype("{{any f1} f2}","{!null f1}"); }
	@Test public void test_1287() { checkNotSubtype("{{any f1} f2}","{!null f2}"); }
	@Test public void test_1288() { checkIsSubtype("{{any f1} f2}","void|void"); }
	@Test public void test_1289() { checkNotSubtype("{{any f1} f2}","void|any"); }
	@Test public void test_1290() { checkNotSubtype("{{any f1} f2}","void|null"); }
	@Test public void test_1291() { checkNotSubtype("{{any f1} f2}","any|void"); }
	@Test public void test_1292() { checkNotSubtype("{{any f1} f2}","any|any"); }
	@Test public void test_1293() { checkNotSubtype("{{any f1} f2}","any|null"); }
	@Test public void test_1294() { checkNotSubtype("{{any f1} f2}","null|void"); }
	@Test public void test_1295() { checkNotSubtype("{{any f1} f2}","null|any"); }
	@Test public void test_1296() { checkNotSubtype("{{any f1} f2}","null|null"); }
	@Test public void test_1297() { checkIsSubtype("{{any f1} f2}","{void f1}|void"); }
	@Test public void test_1298() { checkIsSubtype("{{any f1} f2}","{void f2}|void"); }
	@Test public void test_1299() { checkNotSubtype("{{any f1} f2}","{any f1}|any"); }
	@Test public void test_1300() { checkNotSubtype("{{any f1} f2}","{any f2}|any"); }
	@Test public void test_1301() { checkNotSubtype("{{any f1} f2}","{null f1}|null"); }
	@Test public void test_1302() { checkNotSubtype("{{any f1} f2}","{null f2}|null"); }
	@Test public void test_1303() { checkNotSubtype("{{any f1} f2}","!void|void"); }
	@Test public void test_1304() { checkNotSubtype("{{any f1} f2}","!any|any"); }
	@Test public void test_1305() { checkNotSubtype("{{any f1} f2}","!null|null"); }
	@Test public void test_1306() { checkIsSubtype("{{any f1} f2}","void&void"); }
	@Test public void test_1307() { checkIsSubtype("{{any f1} f2}","void&any"); }
	@Test public void test_1308() { checkIsSubtype("{{any f1} f2}","void&null"); }
	@Test public void test_1309() { checkIsSubtype("{{any f1} f2}","any&void"); }
	@Test public void test_1310() { checkNotSubtype("{{any f1} f2}","any&any"); }
	@Test public void test_1311() { checkNotSubtype("{{any f1} f2}","any&null"); }
	@Test public void test_1312() { checkIsSubtype("{{any f1} f2}","null&void"); }
	@Test public void test_1313() { checkNotSubtype("{{any f1} f2}","null&any"); }
	@Test public void test_1314() { checkNotSubtype("{{any f1} f2}","null&null"); }
	@Test public void test_1315() { checkIsSubtype("{{any f1} f2}","{void f1}&void"); }
	@Test public void test_1316() { checkIsSubtype("{{any f1} f2}","{void f2}&void"); }
	@Test public void test_1317() { checkNotSubtype("{{any f1} f2}","{any f1}&any"); }
	@Test public void test_1318() { checkNotSubtype("{{any f1} f2}","{any f2}&any"); }
	@Test public void test_1319() { checkIsSubtype("{{any f1} f2}","{null f1}&null"); }
	@Test public void test_1320() { checkIsSubtype("{{any f1} f2}","{null f2}&null"); }
	@Test public void test_1321() { checkIsSubtype("{{any f1} f2}","!void&void"); }
	@Test public void test_1322() { checkIsSubtype("{{any f1} f2}","!any&any"); }
	@Test public void test_1323() { checkIsSubtype("{{any f1} f2}","!null&null"); }
	@Test public void test_1324() { checkNotSubtype("{{any f1} f2}","!{void f1}"); }
	@Test public void test_1325() { checkNotSubtype("{{any f1} f2}","!{void f2}"); }
	@Test public void test_1326() { checkNotSubtype("{{any f1} f2}","!{any f1}"); }
	@Test public void test_1327() { checkNotSubtype("{{any f1} f2}","!{any f2}"); }
	@Test public void test_1328() { checkNotSubtype("{{any f1} f2}","!{null f1}"); }
	@Test public void test_1329() { checkNotSubtype("{{any f1} f2}","!{null f2}"); }
	@Test public void test_1330() { checkIsSubtype("{{any f1} f2}","!!void"); }
	@Test public void test_1331() { checkNotSubtype("{{any f1} f2}","!!any"); }
	@Test public void test_1332() { checkNotSubtype("{{any f1} f2}","!!null"); }
	@Test public void test_1333() { checkNotSubtype("{{any f2} f2}","any"); }
	@Test public void test_1334() { checkNotSubtype("{{any f2} f2}","null"); }
	@Test public void test_1335() { checkIsSubtype("{{any f2} f2}","{void f1}"); }
	@Test public void test_1336() { checkIsSubtype("{{any f2} f2}","{void f2}"); }
	@Test public void test_1337() { checkNotSubtype("{{any f2} f2}","{any f1}"); }
	@Test public void test_1338() { checkNotSubtype("{{any f2} f2}","{any f2}"); }
	@Test public void test_1339() { checkNotSubtype("{{any f2} f2}","{null f1}"); }
	@Test public void test_1340() { checkNotSubtype("{{any f2} f2}","{null f2}"); }
	@Test public void test_1341() { checkNotSubtype("{{any f2} f2}","!void"); }
	@Test public void test_1342() { checkIsSubtype("{{any f2} f2}","!any"); }
	@Test public void test_1343() { checkNotSubtype("{{any f2} f2}","!null"); }
	@Test public void test_1344() { checkIsSubtype("{{any f2} f2}","{{void f1} f1}"); }
	@Test public void test_1345() { checkIsSubtype("{{any f2} f2}","{{void f2} f1}"); }
	@Test public void test_1346() { checkIsSubtype("{{any f2} f2}","{{void f1} f2}"); }
	@Test public void test_1347() { checkIsSubtype("{{any f2} f2}","{{void f2} f2}"); }
	@Test public void test_1348() { checkNotSubtype("{{any f2} f2}","{{any f1} f1}"); }
	@Test public void test_1349() { checkNotSubtype("{{any f2} f2}","{{any f2} f1}"); }
	@Test public void test_1350() { checkNotSubtype("{{any f2} f2}","{{any f1} f2}"); }
	@Test public void test_1351() { checkIsSubtype("{{any f2} f2}","{{any f2} f2}"); }
	@Test public void test_1352() { checkNotSubtype("{{any f2} f2}","{{null f1} f1}"); }
	@Test public void test_1353() { checkNotSubtype("{{any f2} f2}","{{null f2} f1}"); }
	@Test public void test_1354() { checkNotSubtype("{{any f2} f2}","{{null f1} f2}"); }
	@Test public void test_1355() { checkIsSubtype("{{any f2} f2}","{{null f2} f2}"); }
	@Test public void test_1356() { checkNotSubtype("{{any f2} f2}","{!void f1}"); }
	@Test public void test_1357() { checkNotSubtype("{{any f2} f2}","{!void f2}"); }
	@Test public void test_1358() { checkIsSubtype("{{any f2} f2}","{!any f1}"); }
	@Test public void test_1359() { checkIsSubtype("{{any f2} f2}","{!any f2}"); }
	@Test public void test_1360() { checkNotSubtype("{{any f2} f2}","{!null f1}"); }
	@Test public void test_1361() { checkNotSubtype("{{any f2} f2}","{!null f2}"); }
	@Test public void test_1362() { checkIsSubtype("{{any f2} f2}","void|void"); }
	@Test public void test_1363() { checkNotSubtype("{{any f2} f2}","void|any"); }
	@Test public void test_1364() { checkNotSubtype("{{any f2} f2}","void|null"); }
	@Test public void test_1365() { checkNotSubtype("{{any f2} f2}","any|void"); }
	@Test public void test_1366() { checkNotSubtype("{{any f2} f2}","any|any"); }
	@Test public void test_1367() { checkNotSubtype("{{any f2} f2}","any|null"); }
	@Test public void test_1368() { checkNotSubtype("{{any f2} f2}","null|void"); }
	@Test public void test_1369() { checkNotSubtype("{{any f2} f2}","null|any"); }
	@Test public void test_1370() { checkNotSubtype("{{any f2} f2}","null|null"); }
	@Test public void test_1371() { checkIsSubtype("{{any f2} f2}","{void f1}|void"); }
	@Test public void test_1372() { checkIsSubtype("{{any f2} f2}","{void f2}|void"); }
	@Test public void test_1373() { checkNotSubtype("{{any f2} f2}","{any f1}|any"); }
	@Test public void test_1374() { checkNotSubtype("{{any f2} f2}","{any f2}|any"); }
	@Test public void test_1375() { checkNotSubtype("{{any f2} f2}","{null f1}|null"); }
	@Test public void test_1376() { checkNotSubtype("{{any f2} f2}","{null f2}|null"); }
	@Test public void test_1377() { checkNotSubtype("{{any f2} f2}","!void|void"); }
	@Test public void test_1378() { checkNotSubtype("{{any f2} f2}","!any|any"); }
	@Test public void test_1379() { checkNotSubtype("{{any f2} f2}","!null|null"); }
	@Test public void test_1380() { checkIsSubtype("{{any f2} f2}","void&void"); }
	@Test public void test_1381() { checkIsSubtype("{{any f2} f2}","void&any"); }
	@Test public void test_1382() { checkIsSubtype("{{any f2} f2}","void&null"); }
	@Test public void test_1383() { checkIsSubtype("{{any f2} f2}","any&void"); }
	@Test public void test_1384() { checkNotSubtype("{{any f2} f2}","any&any"); }
	@Test public void test_1385() { checkNotSubtype("{{any f2} f2}","any&null"); }
	@Test public void test_1386() { checkIsSubtype("{{any f2} f2}","null&void"); }
	@Test public void test_1387() { checkNotSubtype("{{any f2} f2}","null&any"); }
	@Test public void test_1388() { checkNotSubtype("{{any f2} f2}","null&null"); }
	@Test public void test_1389() { checkIsSubtype("{{any f2} f2}","{void f1}&void"); }
	@Test public void test_1390() { checkIsSubtype("{{any f2} f2}","{void f2}&void"); }
	@Test public void test_1391() { checkNotSubtype("{{any f2} f2}","{any f1}&any"); }
	@Test public void test_1392() { checkNotSubtype("{{any f2} f2}","{any f2}&any"); }
	@Test public void test_1393() { checkIsSubtype("{{any f2} f2}","{null f1}&null"); }
	@Test public void test_1394() { checkIsSubtype("{{any f2} f2}","{null f2}&null"); }
	@Test public void test_1395() { checkIsSubtype("{{any f2} f2}","!void&void"); }
	@Test public void test_1396() { checkIsSubtype("{{any f2} f2}","!any&any"); }
	@Test public void test_1397() { checkIsSubtype("{{any f2} f2}","!null&null"); }
	@Test public void test_1398() { checkNotSubtype("{{any f2} f2}","!{void f1}"); }
	@Test public void test_1399() { checkNotSubtype("{{any f2} f2}","!{void f2}"); }
	@Test public void test_1400() { checkNotSubtype("{{any f2} f2}","!{any f1}"); }
	@Test public void test_1401() { checkNotSubtype("{{any f2} f2}","!{any f2}"); }
	@Test public void test_1402() { checkNotSubtype("{{any f2} f2}","!{null f1}"); }
	@Test public void test_1403() { checkNotSubtype("{{any f2} f2}","!{null f2}"); }
	@Test public void test_1404() { checkIsSubtype("{{any f2} f2}","!!void"); }
	@Test public void test_1405() { checkNotSubtype("{{any f2} f2}","!!any"); }
	@Test public void test_1406() { checkNotSubtype("{{any f2} f2}","!!null"); }
	@Test public void test_1407() { checkNotSubtype("{{null f1} f1}","any"); }
	@Test public void test_1408() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_1409() { checkIsSubtype("{{null f1} f1}","{void f1}"); }
	@Test public void test_1410() { checkIsSubtype("{{null f1} f1}","{void f2}"); }
	@Test public void test_1411() { checkNotSubtype("{{null f1} f1}","{any f1}"); }
	@Test public void test_1412() { checkNotSubtype("{{null f1} f1}","{any f2}"); }
	@Test public void test_1413() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test public void test_1414() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test public void test_1415() { checkNotSubtype("{{null f1} f1}","!void"); }
	@Test public void test_1416() { checkIsSubtype("{{null f1} f1}","!any"); }
	@Test public void test_1417() { checkNotSubtype("{{null f1} f1}","!null"); }
	@Test public void test_1418() { checkIsSubtype("{{null f1} f1}","{{void f1} f1}"); }
	@Test public void test_1419() { checkIsSubtype("{{null f1} f1}","{{void f2} f1}"); }
	@Test public void test_1420() { checkIsSubtype("{{null f1} f1}","{{void f1} f2}"); }
	@Test public void test_1421() { checkIsSubtype("{{null f1} f1}","{{void f2} f2}"); }
	@Test public void test_1422() { checkNotSubtype("{{null f1} f1}","{{any f1} f1}"); }
	@Test public void test_1423() { checkNotSubtype("{{null f1} f1}","{{any f2} f1}"); }
	@Test public void test_1424() { checkNotSubtype("{{null f1} f1}","{{any f1} f2}"); }
	@Test public void test_1425() { checkNotSubtype("{{null f1} f1}","{{any f2} f2}"); }
	@Test public void test_1426() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test public void test_1427() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test public void test_1428() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test public void test_1429() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test public void test_1430() { checkNotSubtype("{{null f1} f1}","{!void f1}"); }
	@Test public void test_1431() { checkNotSubtype("{{null f1} f1}","{!void f2}"); }
	@Test public void test_1432() { checkIsSubtype("{{null f1} f1}","{!any f1}"); }
	@Test public void test_1433() { checkIsSubtype("{{null f1} f1}","{!any f2}"); }
	@Test public void test_1434() { checkNotSubtype("{{null f1} f1}","{!null f1}"); }
	@Test public void test_1435() { checkNotSubtype("{{null f1} f1}","{!null f2}"); }
	@Test public void test_1436() { checkIsSubtype("{{null f1} f1}","void|void"); }
	@Test public void test_1437() { checkNotSubtype("{{null f1} f1}","void|any"); }
	@Test public void test_1438() { checkNotSubtype("{{null f1} f1}","void|null"); }
	@Test public void test_1439() { checkNotSubtype("{{null f1} f1}","any|void"); }
	@Test public void test_1440() { checkNotSubtype("{{null f1} f1}","any|any"); }
	@Test public void test_1441() { checkNotSubtype("{{null f1} f1}","any|null"); }
	@Test public void test_1442() { checkNotSubtype("{{null f1} f1}","null|void"); }
	@Test public void test_1443() { checkNotSubtype("{{null f1} f1}","null|any"); }
	@Test public void test_1444() { checkNotSubtype("{{null f1} f1}","null|null"); }
	@Test public void test_1445() { checkIsSubtype("{{null f1} f1}","{void f1}|void"); }
	@Test public void test_1446() { checkIsSubtype("{{null f1} f1}","{void f2}|void"); }
	@Test public void test_1447() { checkNotSubtype("{{null f1} f1}","{any f1}|any"); }
	@Test public void test_1448() { checkNotSubtype("{{null f1} f1}","{any f2}|any"); }
	@Test public void test_1449() { checkNotSubtype("{{null f1} f1}","{null f1}|null"); }
	@Test public void test_1450() { checkNotSubtype("{{null f1} f1}","{null f2}|null"); }
	@Test public void test_1451() { checkNotSubtype("{{null f1} f1}","!void|void"); }
	@Test public void test_1452() { checkNotSubtype("{{null f1} f1}","!any|any"); }
	@Test public void test_1453() { checkNotSubtype("{{null f1} f1}","!null|null"); }
	@Test public void test_1454() { checkIsSubtype("{{null f1} f1}","void&void"); }
	@Test public void test_1455() { checkIsSubtype("{{null f1} f1}","void&any"); }
	@Test public void test_1456() { checkIsSubtype("{{null f1} f1}","void&null"); }
	@Test public void test_1457() { checkIsSubtype("{{null f1} f1}","any&void"); }
	@Test public void test_1458() { checkNotSubtype("{{null f1} f1}","any&any"); }
	@Test public void test_1459() { checkNotSubtype("{{null f1} f1}","any&null"); }
	@Test public void test_1460() { checkIsSubtype("{{null f1} f1}","null&void"); }
	@Test public void test_1461() { checkNotSubtype("{{null f1} f1}","null&any"); }
	@Test public void test_1462() { checkNotSubtype("{{null f1} f1}","null&null"); }
	@Test public void test_1463() { checkIsSubtype("{{null f1} f1}","{void f1}&void"); }
	@Test public void test_1464() { checkIsSubtype("{{null f1} f1}","{void f2}&void"); }
	@Test public void test_1465() { checkNotSubtype("{{null f1} f1}","{any f1}&any"); }
	@Test public void test_1466() { checkNotSubtype("{{null f1} f1}","{any f2}&any"); }
	@Test public void test_1467() { checkIsSubtype("{{null f1} f1}","{null f1}&null"); }
	@Test public void test_1468() { checkIsSubtype("{{null f1} f1}","{null f2}&null"); }
	@Test public void test_1469() { checkIsSubtype("{{null f1} f1}","!void&void"); }
	@Test public void test_1470() { checkIsSubtype("{{null f1} f1}","!any&any"); }
	@Test public void test_1471() { checkIsSubtype("{{null f1} f1}","!null&null"); }
	@Test public void test_1472() { checkNotSubtype("{{null f1} f1}","!{void f1}"); }
	@Test public void test_1473() { checkNotSubtype("{{null f1} f1}","!{void f2}"); }
	@Test public void test_1474() { checkNotSubtype("{{null f1} f1}","!{any f1}"); }
	@Test public void test_1475() { checkNotSubtype("{{null f1} f1}","!{any f2}"); }
	@Test public void test_1476() { checkNotSubtype("{{null f1} f1}","!{null f1}"); }
	@Test public void test_1477() { checkNotSubtype("{{null f1} f1}","!{null f2}"); }
	@Test public void test_1478() { checkIsSubtype("{{null f1} f1}","!!void"); }
	@Test public void test_1479() { checkNotSubtype("{{null f1} f1}","!!any"); }
	@Test public void test_1480() { checkNotSubtype("{{null f1} f1}","!!null"); }
	@Test public void test_1481() { checkNotSubtype("{{null f2} f1}","any"); }
	@Test public void test_1482() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_1483() { checkIsSubtype("{{null f2} f1}","{void f1}"); }
	@Test public void test_1484() { checkIsSubtype("{{null f2} f1}","{void f2}"); }
	@Test public void test_1485() { checkNotSubtype("{{null f2} f1}","{any f1}"); }
	@Test public void test_1486() { checkNotSubtype("{{null f2} f1}","{any f2}"); }
	@Test public void test_1487() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test public void test_1488() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test public void test_1489() { checkNotSubtype("{{null f2} f1}","!void"); }
	@Test public void test_1490() { checkIsSubtype("{{null f2} f1}","!any"); }
	@Test public void test_1491() { checkNotSubtype("{{null f2} f1}","!null"); }
	@Test public void test_1492() { checkIsSubtype("{{null f2} f1}","{{void f1} f1}"); }
	@Test public void test_1493() { checkIsSubtype("{{null f2} f1}","{{void f2} f1}"); }
	@Test public void test_1494() { checkIsSubtype("{{null f2} f1}","{{void f1} f2}"); }
	@Test public void test_1495() { checkIsSubtype("{{null f2} f1}","{{void f2} f2}"); }
	@Test public void test_1496() { checkNotSubtype("{{null f2} f1}","{{any f1} f1}"); }
	@Test public void test_1497() { checkNotSubtype("{{null f2} f1}","{{any f2} f1}"); }
	@Test public void test_1498() { checkNotSubtype("{{null f2} f1}","{{any f1} f2}"); }
	@Test public void test_1499() { checkNotSubtype("{{null f2} f1}","{{any f2} f2}"); }
	@Test public void test_1500() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test public void test_1501() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test public void test_1502() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test public void test_1503() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test public void test_1504() { checkNotSubtype("{{null f2} f1}","{!void f1}"); }
	@Test public void test_1505() { checkNotSubtype("{{null f2} f1}","{!void f2}"); }
	@Test public void test_1506() { checkIsSubtype("{{null f2} f1}","{!any f1}"); }
	@Test public void test_1507() { checkIsSubtype("{{null f2} f1}","{!any f2}"); }
	@Test public void test_1508() { checkNotSubtype("{{null f2} f1}","{!null f1}"); }
	@Test public void test_1509() { checkNotSubtype("{{null f2} f1}","{!null f2}"); }
	@Test public void test_1510() { checkIsSubtype("{{null f2} f1}","void|void"); }
	@Test public void test_1511() { checkNotSubtype("{{null f2} f1}","void|any"); }
	@Test public void test_1512() { checkNotSubtype("{{null f2} f1}","void|null"); }
	@Test public void test_1513() { checkNotSubtype("{{null f2} f1}","any|void"); }
	@Test public void test_1514() { checkNotSubtype("{{null f2} f1}","any|any"); }
	@Test public void test_1515() { checkNotSubtype("{{null f2} f1}","any|null"); }
	@Test public void test_1516() { checkNotSubtype("{{null f2} f1}","null|void"); }
	@Test public void test_1517() { checkNotSubtype("{{null f2} f1}","null|any"); }
	@Test public void test_1518() { checkNotSubtype("{{null f2} f1}","null|null"); }
	@Test public void test_1519() { checkIsSubtype("{{null f2} f1}","{void f1}|void"); }
	@Test public void test_1520() { checkIsSubtype("{{null f2} f1}","{void f2}|void"); }
	@Test public void test_1521() { checkNotSubtype("{{null f2} f1}","{any f1}|any"); }
	@Test public void test_1522() { checkNotSubtype("{{null f2} f1}","{any f2}|any"); }
	@Test public void test_1523() { checkNotSubtype("{{null f2} f1}","{null f1}|null"); }
	@Test public void test_1524() { checkNotSubtype("{{null f2} f1}","{null f2}|null"); }
	@Test public void test_1525() { checkNotSubtype("{{null f2} f1}","!void|void"); }
	@Test public void test_1526() { checkNotSubtype("{{null f2} f1}","!any|any"); }
	@Test public void test_1527() { checkNotSubtype("{{null f2} f1}","!null|null"); }
	@Test public void test_1528() { checkIsSubtype("{{null f2} f1}","void&void"); }
	@Test public void test_1529() { checkIsSubtype("{{null f2} f1}","void&any"); }
	@Test public void test_1530() { checkIsSubtype("{{null f2} f1}","void&null"); }
	@Test public void test_1531() { checkIsSubtype("{{null f2} f1}","any&void"); }
	@Test public void test_1532() { checkNotSubtype("{{null f2} f1}","any&any"); }
	@Test public void test_1533() { checkNotSubtype("{{null f2} f1}","any&null"); }
	@Test public void test_1534() { checkIsSubtype("{{null f2} f1}","null&void"); }
	@Test public void test_1535() { checkNotSubtype("{{null f2} f1}","null&any"); }
	@Test public void test_1536() { checkNotSubtype("{{null f2} f1}","null&null"); }
	@Test public void test_1537() { checkIsSubtype("{{null f2} f1}","{void f1}&void"); }
	@Test public void test_1538() { checkIsSubtype("{{null f2} f1}","{void f2}&void"); }
	@Test public void test_1539() { checkNotSubtype("{{null f2} f1}","{any f1}&any"); }
	@Test public void test_1540() { checkNotSubtype("{{null f2} f1}","{any f2}&any"); }
	@Test public void test_1541() { checkIsSubtype("{{null f2} f1}","{null f1}&null"); }
	@Test public void test_1542() { checkIsSubtype("{{null f2} f1}","{null f2}&null"); }
	@Test public void test_1543() { checkIsSubtype("{{null f2} f1}","!void&void"); }
	@Test public void test_1544() { checkIsSubtype("{{null f2} f1}","!any&any"); }
	@Test public void test_1545() { checkIsSubtype("{{null f2} f1}","!null&null"); }
	@Test public void test_1546() { checkNotSubtype("{{null f2} f1}","!{void f1}"); }
	@Test public void test_1547() { checkNotSubtype("{{null f2} f1}","!{void f2}"); }
	@Test public void test_1548() { checkNotSubtype("{{null f2} f1}","!{any f1}"); }
	@Test public void test_1549() { checkNotSubtype("{{null f2} f1}","!{any f2}"); }
	@Test public void test_1550() { checkNotSubtype("{{null f2} f1}","!{null f1}"); }
	@Test public void test_1551() { checkNotSubtype("{{null f2} f1}","!{null f2}"); }
	@Test public void test_1552() { checkIsSubtype("{{null f2} f1}","!!void"); }
	@Test public void test_1553() { checkNotSubtype("{{null f2} f1}","!!any"); }
	@Test public void test_1554() { checkNotSubtype("{{null f2} f1}","!!null"); }
	@Test public void test_1555() { checkNotSubtype("{{null f1} f2}","any"); }
	@Test public void test_1556() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_1557() { checkIsSubtype("{{null f1} f2}","{void f1}"); }
	@Test public void test_1558() { checkIsSubtype("{{null f1} f2}","{void f2}"); }
	@Test public void test_1559() { checkNotSubtype("{{null f1} f2}","{any f1}"); }
	@Test public void test_1560() { checkNotSubtype("{{null f1} f2}","{any f2}"); }
	@Test public void test_1561() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test public void test_1562() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test public void test_1563() { checkNotSubtype("{{null f1} f2}","!void"); }
	@Test public void test_1564() { checkIsSubtype("{{null f1} f2}","!any"); }
	@Test public void test_1565() { checkNotSubtype("{{null f1} f2}","!null"); }
	@Test public void test_1566() { checkIsSubtype("{{null f1} f2}","{{void f1} f1}"); }
	@Test public void test_1567() { checkIsSubtype("{{null f1} f2}","{{void f2} f1}"); }
	@Test public void test_1568() { checkIsSubtype("{{null f1} f2}","{{void f1} f2}"); }
	@Test public void test_1569() { checkIsSubtype("{{null f1} f2}","{{void f2} f2}"); }
	@Test public void test_1570() { checkNotSubtype("{{null f1} f2}","{{any f1} f1}"); }
	@Test public void test_1571() { checkNotSubtype("{{null f1} f2}","{{any f2} f1}"); }
	@Test public void test_1572() { checkNotSubtype("{{null f1} f2}","{{any f1} f2}"); }
	@Test public void test_1573() { checkNotSubtype("{{null f1} f2}","{{any f2} f2}"); }
	@Test public void test_1574() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test public void test_1575() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test public void test_1576() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test public void test_1577() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test public void test_1578() { checkNotSubtype("{{null f1} f2}","{!void f1}"); }
	@Test public void test_1579() { checkNotSubtype("{{null f1} f2}","{!void f2}"); }
	@Test public void test_1580() { checkIsSubtype("{{null f1} f2}","{!any f1}"); }
	@Test public void test_1581() { checkIsSubtype("{{null f1} f2}","{!any f2}"); }
	@Test public void test_1582() { checkNotSubtype("{{null f1} f2}","{!null f1}"); }
	@Test public void test_1583() { checkNotSubtype("{{null f1} f2}","{!null f2}"); }
	@Test public void test_1584() { checkIsSubtype("{{null f1} f2}","void|void"); }
	@Test public void test_1585() { checkNotSubtype("{{null f1} f2}","void|any"); }
	@Test public void test_1586() { checkNotSubtype("{{null f1} f2}","void|null"); }
	@Test public void test_1587() { checkNotSubtype("{{null f1} f2}","any|void"); }
	@Test public void test_1588() { checkNotSubtype("{{null f1} f2}","any|any"); }
	@Test public void test_1589() { checkNotSubtype("{{null f1} f2}","any|null"); }
	@Test public void test_1590() { checkNotSubtype("{{null f1} f2}","null|void"); }
	@Test public void test_1591() { checkNotSubtype("{{null f1} f2}","null|any"); }
	@Test public void test_1592() { checkNotSubtype("{{null f1} f2}","null|null"); }
	@Test public void test_1593() { checkIsSubtype("{{null f1} f2}","{void f1}|void"); }
	@Test public void test_1594() { checkIsSubtype("{{null f1} f2}","{void f2}|void"); }
	@Test public void test_1595() { checkNotSubtype("{{null f1} f2}","{any f1}|any"); }
	@Test public void test_1596() { checkNotSubtype("{{null f1} f2}","{any f2}|any"); }
	@Test public void test_1597() { checkNotSubtype("{{null f1} f2}","{null f1}|null"); }
	@Test public void test_1598() { checkNotSubtype("{{null f1} f2}","{null f2}|null"); }
	@Test public void test_1599() { checkNotSubtype("{{null f1} f2}","!void|void"); }
	@Test public void test_1600() { checkNotSubtype("{{null f1} f2}","!any|any"); }
	@Test public void test_1601() { checkNotSubtype("{{null f1} f2}","!null|null"); }
	@Test public void test_1602() { checkIsSubtype("{{null f1} f2}","void&void"); }
	@Test public void test_1603() { checkIsSubtype("{{null f1} f2}","void&any"); }
	@Test public void test_1604() { checkIsSubtype("{{null f1} f2}","void&null"); }
	@Test public void test_1605() { checkIsSubtype("{{null f1} f2}","any&void"); }
	@Test public void test_1606() { checkNotSubtype("{{null f1} f2}","any&any"); }
	@Test public void test_1607() { checkNotSubtype("{{null f1} f2}","any&null"); }
	@Test public void test_1608() { checkIsSubtype("{{null f1} f2}","null&void"); }
	@Test public void test_1609() { checkNotSubtype("{{null f1} f2}","null&any"); }
	@Test public void test_1610() { checkNotSubtype("{{null f1} f2}","null&null"); }
	@Test public void test_1611() { checkIsSubtype("{{null f1} f2}","{void f1}&void"); }
	@Test public void test_1612() { checkIsSubtype("{{null f1} f2}","{void f2}&void"); }
	@Test public void test_1613() { checkNotSubtype("{{null f1} f2}","{any f1}&any"); }
	@Test public void test_1614() { checkNotSubtype("{{null f1} f2}","{any f2}&any"); }
	@Test public void test_1615() { checkIsSubtype("{{null f1} f2}","{null f1}&null"); }
	@Test public void test_1616() { checkIsSubtype("{{null f1} f2}","{null f2}&null"); }
	@Test public void test_1617() { checkIsSubtype("{{null f1} f2}","!void&void"); }
	@Test public void test_1618() { checkIsSubtype("{{null f1} f2}","!any&any"); }
	@Test public void test_1619() { checkIsSubtype("{{null f1} f2}","!null&null"); }
	@Test public void test_1620() { checkNotSubtype("{{null f1} f2}","!{void f1}"); }
	@Test public void test_1621() { checkNotSubtype("{{null f1} f2}","!{void f2}"); }
	@Test public void test_1622() { checkNotSubtype("{{null f1} f2}","!{any f1}"); }
	@Test public void test_1623() { checkNotSubtype("{{null f1} f2}","!{any f2}"); }
	@Test public void test_1624() { checkNotSubtype("{{null f1} f2}","!{null f1}"); }
	@Test public void test_1625() { checkNotSubtype("{{null f1} f2}","!{null f2}"); }
	@Test public void test_1626() { checkIsSubtype("{{null f1} f2}","!!void"); }
	@Test public void test_1627() { checkNotSubtype("{{null f1} f2}","!!any"); }
	@Test public void test_1628() { checkNotSubtype("{{null f1} f2}","!!null"); }
	@Test public void test_1629() { checkNotSubtype("{{null f2} f2}","any"); }
	@Test public void test_1630() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_1631() { checkIsSubtype("{{null f2} f2}","{void f1}"); }
	@Test public void test_1632() { checkIsSubtype("{{null f2} f2}","{void f2}"); }
	@Test public void test_1633() { checkNotSubtype("{{null f2} f2}","{any f1}"); }
	@Test public void test_1634() { checkNotSubtype("{{null f2} f2}","{any f2}"); }
	@Test public void test_1635() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test public void test_1636() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test public void test_1637() { checkNotSubtype("{{null f2} f2}","!void"); }
	@Test public void test_1638() { checkIsSubtype("{{null f2} f2}","!any"); }
	@Test public void test_1639() { checkNotSubtype("{{null f2} f2}","!null"); }
	@Test public void test_1640() { checkIsSubtype("{{null f2} f2}","{{void f1} f1}"); }
	@Test public void test_1641() { checkIsSubtype("{{null f2} f2}","{{void f2} f1}"); }
	@Test public void test_1642() { checkIsSubtype("{{null f2} f2}","{{void f1} f2}"); }
	@Test public void test_1643() { checkIsSubtype("{{null f2} f2}","{{void f2} f2}"); }
	@Test public void test_1644() { checkNotSubtype("{{null f2} f2}","{{any f1} f1}"); }
	@Test public void test_1645() { checkNotSubtype("{{null f2} f2}","{{any f2} f1}"); }
	@Test public void test_1646() { checkNotSubtype("{{null f2} f2}","{{any f1} f2}"); }
	@Test public void test_1647() { checkNotSubtype("{{null f2} f2}","{{any f2} f2}"); }
	@Test public void test_1648() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test public void test_1649() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test public void test_1650() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test public void test_1651() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test public void test_1652() { checkNotSubtype("{{null f2} f2}","{!void f1}"); }
	@Test public void test_1653() { checkNotSubtype("{{null f2} f2}","{!void f2}"); }
	@Test public void test_1654() { checkIsSubtype("{{null f2} f2}","{!any f1}"); }
	@Test public void test_1655() { checkIsSubtype("{{null f2} f2}","{!any f2}"); }
	@Test public void test_1656() { checkNotSubtype("{{null f2} f2}","{!null f1}"); }
	@Test public void test_1657() { checkNotSubtype("{{null f2} f2}","{!null f2}"); }
	@Test public void test_1658() { checkIsSubtype("{{null f2} f2}","void|void"); }
	@Test public void test_1659() { checkNotSubtype("{{null f2} f2}","void|any"); }
	@Test public void test_1660() { checkNotSubtype("{{null f2} f2}","void|null"); }
	@Test public void test_1661() { checkNotSubtype("{{null f2} f2}","any|void"); }
	@Test public void test_1662() { checkNotSubtype("{{null f2} f2}","any|any"); }
	@Test public void test_1663() { checkNotSubtype("{{null f2} f2}","any|null"); }
	@Test public void test_1664() { checkNotSubtype("{{null f2} f2}","null|void"); }
	@Test public void test_1665() { checkNotSubtype("{{null f2} f2}","null|any"); }
	@Test public void test_1666() { checkNotSubtype("{{null f2} f2}","null|null"); }
	@Test public void test_1667() { checkIsSubtype("{{null f2} f2}","{void f1}|void"); }
	@Test public void test_1668() { checkIsSubtype("{{null f2} f2}","{void f2}|void"); }
	@Test public void test_1669() { checkNotSubtype("{{null f2} f2}","{any f1}|any"); }
	@Test public void test_1670() { checkNotSubtype("{{null f2} f2}","{any f2}|any"); }
	@Test public void test_1671() { checkNotSubtype("{{null f2} f2}","{null f1}|null"); }
	@Test public void test_1672() { checkNotSubtype("{{null f2} f2}","{null f2}|null"); }
	@Test public void test_1673() { checkNotSubtype("{{null f2} f2}","!void|void"); }
	@Test public void test_1674() { checkNotSubtype("{{null f2} f2}","!any|any"); }
	@Test public void test_1675() { checkNotSubtype("{{null f2} f2}","!null|null"); }
	@Test public void test_1676() { checkIsSubtype("{{null f2} f2}","void&void"); }
	@Test public void test_1677() { checkIsSubtype("{{null f2} f2}","void&any"); }
	@Test public void test_1678() { checkIsSubtype("{{null f2} f2}","void&null"); }
	@Test public void test_1679() { checkIsSubtype("{{null f2} f2}","any&void"); }
	@Test public void test_1680() { checkNotSubtype("{{null f2} f2}","any&any"); }
	@Test public void test_1681() { checkNotSubtype("{{null f2} f2}","any&null"); }
	@Test public void test_1682() { checkIsSubtype("{{null f2} f2}","null&void"); }
	@Test public void test_1683() { checkNotSubtype("{{null f2} f2}","null&any"); }
	@Test public void test_1684() { checkNotSubtype("{{null f2} f2}","null&null"); }
	@Test public void test_1685() { checkIsSubtype("{{null f2} f2}","{void f1}&void"); }
	@Test public void test_1686() { checkIsSubtype("{{null f2} f2}","{void f2}&void"); }
	@Test public void test_1687() { checkNotSubtype("{{null f2} f2}","{any f1}&any"); }
	@Test public void test_1688() { checkNotSubtype("{{null f2} f2}","{any f2}&any"); }
	@Test public void test_1689() { checkIsSubtype("{{null f2} f2}","{null f1}&null"); }
	@Test public void test_1690() { checkIsSubtype("{{null f2} f2}","{null f2}&null"); }
	@Test public void test_1691() { checkIsSubtype("{{null f2} f2}","!void&void"); }
	@Test public void test_1692() { checkIsSubtype("{{null f2} f2}","!any&any"); }
	@Test public void test_1693() { checkIsSubtype("{{null f2} f2}","!null&null"); }
	@Test public void test_1694() { checkNotSubtype("{{null f2} f2}","!{void f1}"); }
	@Test public void test_1695() { checkNotSubtype("{{null f2} f2}","!{void f2}"); }
	@Test public void test_1696() { checkNotSubtype("{{null f2} f2}","!{any f1}"); }
	@Test public void test_1697() { checkNotSubtype("{{null f2} f2}","!{any f2}"); }
	@Test public void test_1698() { checkNotSubtype("{{null f2} f2}","!{null f1}"); }
	@Test public void test_1699() { checkNotSubtype("{{null f2} f2}","!{null f2}"); }
	@Test public void test_1700() { checkIsSubtype("{{null f2} f2}","!!void"); }
	@Test public void test_1701() { checkNotSubtype("{{null f2} f2}","!!any"); }
	@Test public void test_1702() { checkNotSubtype("{{null f2} f2}","!!null"); }
	@Test public void test_1703() { checkNotSubtype("{!void f1}","any"); }
	@Test public void test_1704() { checkNotSubtype("{!void f1}","null"); }
	@Test public void test_1705() { checkIsSubtype("{!void f1}","{void f1}"); }
	@Test public void test_1706() { checkIsSubtype("{!void f1}","{void f2}"); }
	@Test public void test_1707() { checkIsSubtype("{!void f1}","{any f1}"); }
	@Test public void test_1708() { checkNotSubtype("{!void f1}","{any f2}"); }
	@Test public void test_1709() { checkIsSubtype("{!void f1}","{null f1}"); }
	@Test public void test_1710() { checkNotSubtype("{!void f1}","{null f2}"); }
	@Test public void test_1711() { checkNotSubtype("{!void f1}","!void"); }
	@Test public void test_1712() { checkIsSubtype("{!void f1}","!any"); }
	@Test public void test_1713() { checkNotSubtype("{!void f1}","!null"); }
	@Test public void test_1714() { checkIsSubtype("{!void f1}","{{void f1} f1}"); }
	@Test public void test_1715() { checkIsSubtype("{!void f1}","{{void f2} f1}"); }
	@Test public void test_1716() { checkIsSubtype("{!void f1}","{{void f1} f2}"); }
	@Test public void test_1717() { checkIsSubtype("{!void f1}","{{void f2} f2}"); }
	@Test public void test_1718() { checkIsSubtype("{!void f1}","{{any f1} f1}"); }
	@Test public void test_1719() { checkIsSubtype("{!void f1}","{{any f2} f1}"); }
	@Test public void test_1720() { checkNotSubtype("{!void f1}","{{any f1} f2}"); }
	@Test public void test_1721() { checkNotSubtype("{!void f1}","{{any f2} f2}"); }
	@Test public void test_1722() { checkIsSubtype("{!void f1}","{{null f1} f1}"); }
	@Test public void test_1723() { checkIsSubtype("{!void f1}","{{null f2} f1}"); }
	@Test public void test_1724() { checkNotSubtype("{!void f1}","{{null f1} f2}"); }
	@Test public void test_1725() { checkNotSubtype("{!void f1}","{{null f2} f2}"); }
	@Test public void test_1726() { checkIsSubtype("{!void f1}","{!void f1}"); }
	@Test public void test_1727() { checkNotSubtype("{!void f1}","{!void f2}"); }
	@Test public void test_1728() { checkIsSubtype("{!void f1}","{!any f1}"); }
	@Test public void test_1729() { checkIsSubtype("{!void f1}","{!any f2}"); }
	@Test public void test_1730() { checkIsSubtype("{!void f1}","{!null f1}"); }
	@Test public void test_1731() { checkNotSubtype("{!void f1}","{!null f2}"); }
	@Test public void test_1732() { checkIsSubtype("{!void f1}","void|void"); }
	@Test public void test_1733() { checkNotSubtype("{!void f1}","void|any"); }
	@Test public void test_1734() { checkNotSubtype("{!void f1}","void|null"); }
	@Test public void test_1735() { checkNotSubtype("{!void f1}","any|void"); }
	@Test public void test_1736() { checkNotSubtype("{!void f1}","any|any"); }
	@Test public void test_1737() { checkNotSubtype("{!void f1}","any|null"); }
	@Test public void test_1738() { checkNotSubtype("{!void f1}","null|void"); }
	@Test public void test_1739() { checkNotSubtype("{!void f1}","null|any"); }
	@Test public void test_1740() { checkNotSubtype("{!void f1}","null|null"); }
	@Test public void test_1741() { checkIsSubtype("{!void f1}","{void f1}|void"); }
	@Test public void test_1742() { checkIsSubtype("{!void f1}","{void f2}|void"); }
	@Test public void test_1743() { checkNotSubtype("{!void f1}","{any f1}|any"); }
	@Test public void test_1744() { checkNotSubtype("{!void f1}","{any f2}|any"); }
	@Test public void test_1745() { checkNotSubtype("{!void f1}","{null f1}|null"); }
	@Test public void test_1746() { checkNotSubtype("{!void f1}","{null f2}|null"); }
	@Test public void test_1747() { checkNotSubtype("{!void f1}","!void|void"); }
	@Test public void test_1748() { checkNotSubtype("{!void f1}","!any|any"); }
	@Test public void test_1749() { checkNotSubtype("{!void f1}","!null|null"); }
	@Test public void test_1750() { checkIsSubtype("{!void f1}","void&void"); }
	@Test public void test_1751() { checkIsSubtype("{!void f1}","void&any"); }
	@Test public void test_1752() { checkIsSubtype("{!void f1}","void&null"); }
	@Test public void test_1753() { checkIsSubtype("{!void f1}","any&void"); }
	@Test public void test_1754() { checkNotSubtype("{!void f1}","any&any"); }
	@Test public void test_1755() { checkNotSubtype("{!void f1}","any&null"); }
	@Test public void test_1756() { checkIsSubtype("{!void f1}","null&void"); }
	@Test public void test_1757() { checkNotSubtype("{!void f1}","null&any"); }
	@Test public void test_1758() { checkNotSubtype("{!void f1}","null&null"); }
	@Test public void test_1759() { checkIsSubtype("{!void f1}","{void f1}&void"); }
	@Test public void test_1760() { checkIsSubtype("{!void f1}","{void f2}&void"); }
	@Test public void test_1761() { checkIsSubtype("{!void f1}","{any f1}&any"); }
	@Test public void test_1762() { checkNotSubtype("{!void f1}","{any f2}&any"); }
	@Test public void test_1763() { checkIsSubtype("{!void f1}","{null f1}&null"); }
	@Test public void test_1764() { checkIsSubtype("{!void f1}","{null f2}&null"); }
	@Test public void test_1765() { checkIsSubtype("{!void f1}","!void&void"); }
	@Test public void test_1766() { checkIsSubtype("{!void f1}","!any&any"); }
	@Test public void test_1767() { checkIsSubtype("{!void f1}","!null&null"); }
	@Test public void test_1768() { checkNotSubtype("{!void f1}","!{void f1}"); }
	@Test public void test_1769() { checkNotSubtype("{!void f1}","!{void f2}"); }
	@Test public void test_1770() { checkNotSubtype("{!void f1}","!{any f1}"); }
	@Test public void test_1771() { checkNotSubtype("{!void f1}","!{any f2}"); }
	@Test public void test_1772() { checkNotSubtype("{!void f1}","!{null f1}"); }
	@Test public void test_1773() { checkNotSubtype("{!void f1}","!{null f2}"); }
	@Test public void test_1774() { checkIsSubtype("{!void f1}","!!void"); }
	@Test public void test_1775() { checkNotSubtype("{!void f1}","!!any"); }
	@Test public void test_1776() { checkNotSubtype("{!void f1}","!!null"); }
	@Test public void test_1777() { checkNotSubtype("{!void f2}","any"); }
	@Test public void test_1778() { checkNotSubtype("{!void f2}","null"); }
	@Test public void test_1779() { checkIsSubtype("{!void f2}","{void f1}"); }
	@Test public void test_1780() { checkIsSubtype("{!void f2}","{void f2}"); }
	@Test public void test_1781() { checkNotSubtype("{!void f2}","{any f1}"); }
	@Test public void test_1782() { checkIsSubtype("{!void f2}","{any f2}"); }
	@Test public void test_1783() { checkNotSubtype("{!void f2}","{null f1}"); }
	@Test public void test_1784() { checkIsSubtype("{!void f2}","{null f2}"); }
	@Test public void test_1785() { checkNotSubtype("{!void f2}","!void"); }
	@Test public void test_1786() { checkIsSubtype("{!void f2}","!any"); }
	@Test public void test_1787() { checkNotSubtype("{!void f2}","!null"); }
	@Test public void test_1788() { checkIsSubtype("{!void f2}","{{void f1} f1}"); }
	@Test public void test_1789() { checkIsSubtype("{!void f2}","{{void f2} f1}"); }
	@Test public void test_1790() { checkIsSubtype("{!void f2}","{{void f1} f2}"); }
	@Test public void test_1791() { checkIsSubtype("{!void f2}","{{void f2} f2}"); }
	@Test public void test_1792() { checkNotSubtype("{!void f2}","{{any f1} f1}"); }
	@Test public void test_1793() { checkNotSubtype("{!void f2}","{{any f2} f1}"); }
	@Test public void test_1794() { checkIsSubtype("{!void f2}","{{any f1} f2}"); }
	@Test public void test_1795() { checkIsSubtype("{!void f2}","{{any f2} f2}"); }
	@Test public void test_1796() { checkNotSubtype("{!void f2}","{{null f1} f1}"); }
	@Test public void test_1797() { checkNotSubtype("{!void f2}","{{null f2} f1}"); }
	@Test public void test_1798() { checkIsSubtype("{!void f2}","{{null f1} f2}"); }
	@Test public void test_1799() { checkIsSubtype("{!void f2}","{{null f2} f2}"); }
	@Test public void test_1800() { checkNotSubtype("{!void f2}","{!void f1}"); }
	@Test public void test_1801() { checkIsSubtype("{!void f2}","{!void f2}"); }
	@Test public void test_1802() { checkIsSubtype("{!void f2}","{!any f1}"); }
	@Test public void test_1803() { checkIsSubtype("{!void f2}","{!any f2}"); }
	@Test public void test_1804() { checkNotSubtype("{!void f2}","{!null f1}"); }
	@Test public void test_1805() { checkIsSubtype("{!void f2}","{!null f2}"); }
	@Test public void test_1806() { checkIsSubtype("{!void f2}","void|void"); }
	@Test public void test_1807() { checkNotSubtype("{!void f2}","void|any"); }
	@Test public void test_1808() { checkNotSubtype("{!void f2}","void|null"); }
	@Test public void test_1809() { checkNotSubtype("{!void f2}","any|void"); }
	@Test public void test_1810() { checkNotSubtype("{!void f2}","any|any"); }
	@Test public void test_1811() { checkNotSubtype("{!void f2}","any|null"); }
	@Test public void test_1812() { checkNotSubtype("{!void f2}","null|void"); }
	@Test public void test_1813() { checkNotSubtype("{!void f2}","null|any"); }
	@Test public void test_1814() { checkNotSubtype("{!void f2}","null|null"); }
	@Test public void test_1815() { checkIsSubtype("{!void f2}","{void f1}|void"); }
	@Test public void test_1816() { checkIsSubtype("{!void f2}","{void f2}|void"); }
	@Test public void test_1817() { checkNotSubtype("{!void f2}","{any f1}|any"); }
	@Test public void test_1818() { checkNotSubtype("{!void f2}","{any f2}|any"); }
	@Test public void test_1819() { checkNotSubtype("{!void f2}","{null f1}|null"); }
	@Test public void test_1820() { checkNotSubtype("{!void f2}","{null f2}|null"); }
	@Test public void test_1821() { checkNotSubtype("{!void f2}","!void|void"); }
	@Test public void test_1822() { checkNotSubtype("{!void f2}","!any|any"); }
	@Test public void test_1823() { checkNotSubtype("{!void f2}","!null|null"); }
	@Test public void test_1824() { checkIsSubtype("{!void f2}","void&void"); }
	@Test public void test_1825() { checkIsSubtype("{!void f2}","void&any"); }
	@Test public void test_1826() { checkIsSubtype("{!void f2}","void&null"); }
	@Test public void test_1827() { checkIsSubtype("{!void f2}","any&void"); }
	@Test public void test_1828() { checkNotSubtype("{!void f2}","any&any"); }
	@Test public void test_1829() { checkNotSubtype("{!void f2}","any&null"); }
	@Test public void test_1830() { checkIsSubtype("{!void f2}","null&void"); }
	@Test public void test_1831() { checkNotSubtype("{!void f2}","null&any"); }
	@Test public void test_1832() { checkNotSubtype("{!void f2}","null&null"); }
	@Test public void test_1833() { checkIsSubtype("{!void f2}","{void f1}&void"); }
	@Test public void test_1834() { checkIsSubtype("{!void f2}","{void f2}&void"); }
	@Test public void test_1835() { checkNotSubtype("{!void f2}","{any f1}&any"); }
	@Test public void test_1836() { checkIsSubtype("{!void f2}","{any f2}&any"); }
	@Test public void test_1837() { checkIsSubtype("{!void f2}","{null f1}&null"); }
	@Test public void test_1838() { checkIsSubtype("{!void f2}","{null f2}&null"); }
	@Test public void test_1839() { checkIsSubtype("{!void f2}","!void&void"); }
	@Test public void test_1840() { checkIsSubtype("{!void f2}","!any&any"); }
	@Test public void test_1841() { checkIsSubtype("{!void f2}","!null&null"); }
	@Test public void test_1842() { checkNotSubtype("{!void f2}","!{void f1}"); }
	@Test public void test_1843() { checkNotSubtype("{!void f2}","!{void f2}"); }
	@Test public void test_1844() { checkNotSubtype("{!void f2}","!{any f1}"); }
	@Test public void test_1845() { checkNotSubtype("{!void f2}","!{any f2}"); }
	@Test public void test_1846() { checkNotSubtype("{!void f2}","!{null f1}"); }
	@Test public void test_1847() { checkNotSubtype("{!void f2}","!{null f2}"); }
	@Test public void test_1848() { checkIsSubtype("{!void f2}","!!void"); }
	@Test public void test_1849() { checkNotSubtype("{!void f2}","!!any"); }
	@Test public void test_1850() { checkNotSubtype("{!void f2}","!!null"); }
	@Test public void test_1851() { checkNotSubtype("{!any f1}","any"); }
	@Test public void test_1852() { checkNotSubtype("{!any f1}","null"); }
	@Test public void test_1853() { checkIsSubtype("{!any f1}","{void f1}"); }
	@Test public void test_1854() { checkIsSubtype("{!any f1}","{void f2}"); }
	@Test public void test_1855() { checkNotSubtype("{!any f1}","{any f1}"); }
	@Test public void test_1856() { checkNotSubtype("{!any f1}","{any f2}"); }
	@Test public void test_1857() { checkNotSubtype("{!any f1}","{null f1}"); }
	@Test public void test_1858() { checkNotSubtype("{!any f1}","{null f2}"); }
	@Test public void test_1859() { checkNotSubtype("{!any f1}","!void"); }
	@Test public void test_1860() { checkIsSubtype("{!any f1}","!any"); }
	@Test public void test_1861() { checkNotSubtype("{!any f1}","!null"); }
	@Test public void test_1862() { checkIsSubtype("{!any f1}","{{void f1} f1}"); }
	@Test public void test_1863() { checkIsSubtype("{!any f1}","{{void f2} f1}"); }
	@Test public void test_1864() { checkIsSubtype("{!any f1}","{{void f1} f2}"); }
	@Test public void test_1865() { checkIsSubtype("{!any f1}","{{void f2} f2}"); }
	@Test public void test_1866() { checkNotSubtype("{!any f1}","{{any f1} f1}"); }
	@Test public void test_1867() { checkNotSubtype("{!any f1}","{{any f2} f1}"); }
	@Test public void test_1868() { checkNotSubtype("{!any f1}","{{any f1} f2}"); }
	@Test public void test_1869() { checkNotSubtype("{!any f1}","{{any f2} f2}"); }
	@Test public void test_1870() { checkNotSubtype("{!any f1}","{{null f1} f1}"); }
	@Test public void test_1871() { checkNotSubtype("{!any f1}","{{null f2} f1}"); }
	@Test public void test_1872() { checkNotSubtype("{!any f1}","{{null f1} f2}"); }
	@Test public void test_1873() { checkNotSubtype("{!any f1}","{{null f2} f2}"); }
	@Test public void test_1874() { checkNotSubtype("{!any f1}","{!void f1}"); }
	@Test public void test_1875() { checkNotSubtype("{!any f1}","{!void f2}"); }
	@Test public void test_1876() { checkIsSubtype("{!any f1}","{!any f1}"); }
	@Test public void test_1877() { checkIsSubtype("{!any f1}","{!any f2}"); }
	@Test public void test_1878() { checkNotSubtype("{!any f1}","{!null f1}"); }
	@Test public void test_1879() { checkNotSubtype("{!any f1}","{!null f2}"); }
	@Test public void test_1880() { checkIsSubtype("{!any f1}","void|void"); }
	@Test public void test_1881() { checkNotSubtype("{!any f1}","void|any"); }
	@Test public void test_1882() { checkNotSubtype("{!any f1}","void|null"); }
	@Test public void test_1883() { checkNotSubtype("{!any f1}","any|void"); }
	@Test public void test_1884() { checkNotSubtype("{!any f1}","any|any"); }
	@Test public void test_1885() { checkNotSubtype("{!any f1}","any|null"); }
	@Test public void test_1886() { checkNotSubtype("{!any f1}","null|void"); }
	@Test public void test_1887() { checkNotSubtype("{!any f1}","null|any"); }
	@Test public void test_1888() { checkNotSubtype("{!any f1}","null|null"); }
	@Test public void test_1889() { checkIsSubtype("{!any f1}","{void f1}|void"); }
	@Test public void test_1890() { checkIsSubtype("{!any f1}","{void f2}|void"); }
	@Test public void test_1891() { checkNotSubtype("{!any f1}","{any f1}|any"); }
	@Test public void test_1892() { checkNotSubtype("{!any f1}","{any f2}|any"); }
	@Test public void test_1893() { checkNotSubtype("{!any f1}","{null f1}|null"); }
	@Test public void test_1894() { checkNotSubtype("{!any f1}","{null f2}|null"); }
	@Test public void test_1895() { checkNotSubtype("{!any f1}","!void|void"); }
	@Test public void test_1896() { checkNotSubtype("{!any f1}","!any|any"); }
	@Test public void test_1897() { checkNotSubtype("{!any f1}","!null|null"); }
	@Test public void test_1898() { checkIsSubtype("{!any f1}","void&void"); }
	@Test public void test_1899() { checkIsSubtype("{!any f1}","void&any"); }
	@Test public void test_1900() { checkIsSubtype("{!any f1}","void&null"); }
	@Test public void test_1901() { checkIsSubtype("{!any f1}","any&void"); }
	@Test public void test_1902() { checkNotSubtype("{!any f1}","any&any"); }
	@Test public void test_1903() { checkNotSubtype("{!any f1}","any&null"); }
	@Test public void test_1904() { checkIsSubtype("{!any f1}","null&void"); }
	@Test public void test_1905() { checkNotSubtype("{!any f1}","null&any"); }
	@Test public void test_1906() { checkNotSubtype("{!any f1}","null&null"); }
	@Test public void test_1907() { checkIsSubtype("{!any f1}","{void f1}&void"); }
	@Test public void test_1908() { checkIsSubtype("{!any f1}","{void f2}&void"); }
	@Test public void test_1909() { checkNotSubtype("{!any f1}","{any f1}&any"); }
	@Test public void test_1910() { checkNotSubtype("{!any f1}","{any f2}&any"); }
	@Test public void test_1911() { checkIsSubtype("{!any f1}","{null f1}&null"); }
	@Test public void test_1912() { checkIsSubtype("{!any f1}","{null f2}&null"); }
	@Test public void test_1913() { checkIsSubtype("{!any f1}","!void&void"); }
	@Test public void test_1914() { checkIsSubtype("{!any f1}","!any&any"); }
	@Test public void test_1915() { checkIsSubtype("{!any f1}","!null&null"); }
	@Test public void test_1916() { checkNotSubtype("{!any f1}","!{void f1}"); }
	@Test public void test_1917() { checkNotSubtype("{!any f1}","!{void f2}"); }
	@Test public void test_1918() { checkNotSubtype("{!any f1}","!{any f1}"); }
	@Test public void test_1919() { checkNotSubtype("{!any f1}","!{any f2}"); }
	@Test public void test_1920() { checkNotSubtype("{!any f1}","!{null f1}"); }
	@Test public void test_1921() { checkNotSubtype("{!any f1}","!{null f2}"); }
	@Test public void test_1922() { checkIsSubtype("{!any f1}","!!void"); }
	@Test public void test_1923() { checkNotSubtype("{!any f1}","!!any"); }
	@Test public void test_1924() { checkNotSubtype("{!any f1}","!!null"); }
	@Test public void test_1925() { checkNotSubtype("{!any f2}","any"); }
	@Test public void test_1926() { checkNotSubtype("{!any f2}","null"); }
	@Test public void test_1927() { checkIsSubtype("{!any f2}","{void f1}"); }
	@Test public void test_1928() { checkIsSubtype("{!any f2}","{void f2}"); }
	@Test public void test_1929() { checkNotSubtype("{!any f2}","{any f1}"); }
	@Test public void test_1930() { checkNotSubtype("{!any f2}","{any f2}"); }
	@Test public void test_1931() { checkNotSubtype("{!any f2}","{null f1}"); }
	@Test public void test_1932() { checkNotSubtype("{!any f2}","{null f2}"); }
	@Test public void test_1933() { checkNotSubtype("{!any f2}","!void"); }
	@Test public void test_1934() { checkIsSubtype("{!any f2}","!any"); }
	@Test public void test_1935() { checkNotSubtype("{!any f2}","!null"); }
	@Test public void test_1936() { checkIsSubtype("{!any f2}","{{void f1} f1}"); }
	@Test public void test_1937() { checkIsSubtype("{!any f2}","{{void f2} f1}"); }
	@Test public void test_1938() { checkIsSubtype("{!any f2}","{{void f1} f2}"); }
	@Test public void test_1939() { checkIsSubtype("{!any f2}","{{void f2} f2}"); }
	@Test public void test_1940() { checkNotSubtype("{!any f2}","{{any f1} f1}"); }
	@Test public void test_1941() { checkNotSubtype("{!any f2}","{{any f2} f1}"); }
	@Test public void test_1942() { checkNotSubtype("{!any f2}","{{any f1} f2}"); }
	@Test public void test_1943() { checkNotSubtype("{!any f2}","{{any f2} f2}"); }
	@Test public void test_1944() { checkNotSubtype("{!any f2}","{{null f1} f1}"); }
	@Test public void test_1945() { checkNotSubtype("{!any f2}","{{null f2} f1}"); }
	@Test public void test_1946() { checkNotSubtype("{!any f2}","{{null f1} f2}"); }
	@Test public void test_1947() { checkNotSubtype("{!any f2}","{{null f2} f2}"); }
	@Test public void test_1948() { checkNotSubtype("{!any f2}","{!void f1}"); }
	@Test public void test_1949() { checkNotSubtype("{!any f2}","{!void f2}"); }
	@Test public void test_1950() { checkIsSubtype("{!any f2}","{!any f1}"); }
	@Test public void test_1951() { checkIsSubtype("{!any f2}","{!any f2}"); }
	@Test public void test_1952() { checkNotSubtype("{!any f2}","{!null f1}"); }
	@Test public void test_1953() { checkNotSubtype("{!any f2}","{!null f2}"); }
	@Test public void test_1954() { checkIsSubtype("{!any f2}","void|void"); }
	@Test public void test_1955() { checkNotSubtype("{!any f2}","void|any"); }
	@Test public void test_1956() { checkNotSubtype("{!any f2}","void|null"); }
	@Test public void test_1957() { checkNotSubtype("{!any f2}","any|void"); }
	@Test public void test_1958() { checkNotSubtype("{!any f2}","any|any"); }
	@Test public void test_1959() { checkNotSubtype("{!any f2}","any|null"); }
	@Test public void test_1960() { checkNotSubtype("{!any f2}","null|void"); }
	@Test public void test_1961() { checkNotSubtype("{!any f2}","null|any"); }
	@Test public void test_1962() { checkNotSubtype("{!any f2}","null|null"); }
	@Test public void test_1963() { checkIsSubtype("{!any f2}","{void f1}|void"); }
	@Test public void test_1964() { checkIsSubtype("{!any f2}","{void f2}|void"); }
	@Test public void test_1965() { checkNotSubtype("{!any f2}","{any f1}|any"); }
	@Test public void test_1966() { checkNotSubtype("{!any f2}","{any f2}|any"); }
	@Test public void test_1967() { checkNotSubtype("{!any f2}","{null f1}|null"); }
	@Test public void test_1968() { checkNotSubtype("{!any f2}","{null f2}|null"); }
	@Test public void test_1969() { checkNotSubtype("{!any f2}","!void|void"); }
	@Test public void test_1970() { checkNotSubtype("{!any f2}","!any|any"); }
	@Test public void test_1971() { checkNotSubtype("{!any f2}","!null|null"); }
	@Test public void test_1972() { checkIsSubtype("{!any f2}","void&void"); }
	@Test public void test_1973() { checkIsSubtype("{!any f2}","void&any"); }
	@Test public void test_1974() { checkIsSubtype("{!any f2}","void&null"); }
	@Test public void test_1975() { checkIsSubtype("{!any f2}","any&void"); }
	@Test public void test_1976() { checkNotSubtype("{!any f2}","any&any"); }
	@Test public void test_1977() { checkNotSubtype("{!any f2}","any&null"); }
	@Test public void test_1978() { checkIsSubtype("{!any f2}","null&void"); }
	@Test public void test_1979() { checkNotSubtype("{!any f2}","null&any"); }
	@Test public void test_1980() { checkNotSubtype("{!any f2}","null&null"); }
	@Test public void test_1981() { checkIsSubtype("{!any f2}","{void f1}&void"); }
	@Test public void test_1982() { checkIsSubtype("{!any f2}","{void f2}&void"); }
	@Test public void test_1983() { checkNotSubtype("{!any f2}","{any f1}&any"); }
	@Test public void test_1984() { checkNotSubtype("{!any f2}","{any f2}&any"); }
	@Test public void test_1985() { checkIsSubtype("{!any f2}","{null f1}&null"); }
	@Test public void test_1986() { checkIsSubtype("{!any f2}","{null f2}&null"); }
	@Test public void test_1987() { checkIsSubtype("{!any f2}","!void&void"); }
	@Test public void test_1988() { checkIsSubtype("{!any f2}","!any&any"); }
	@Test public void test_1989() { checkIsSubtype("{!any f2}","!null&null"); }
	@Test public void test_1990() { checkNotSubtype("{!any f2}","!{void f1}"); }
	@Test public void test_1991() { checkNotSubtype("{!any f2}","!{void f2}"); }
	@Test public void test_1992() { checkNotSubtype("{!any f2}","!{any f1}"); }
	@Test public void test_1993() { checkNotSubtype("{!any f2}","!{any f2}"); }
	@Test public void test_1994() { checkNotSubtype("{!any f2}","!{null f1}"); }
	@Test public void test_1995() { checkNotSubtype("{!any f2}","!{null f2}"); }
	@Test public void test_1996() { checkIsSubtype("{!any f2}","!!void"); }
	@Test public void test_1997() { checkNotSubtype("{!any f2}","!!any"); }
	@Test public void test_1998() { checkNotSubtype("{!any f2}","!!null"); }
	@Test public void test_1999() { checkNotSubtype("{!null f1}","any"); }
	@Test public void test_2000() { checkNotSubtype("{!null f1}","null"); }
	@Test public void test_2001() { checkIsSubtype("{!null f1}","{void f1}"); }
	@Test public void test_2002() { checkIsSubtype("{!null f1}","{void f2}"); }
	@Test public void test_2003() { checkNotSubtype("{!null f1}","{any f1}"); }
	@Test public void test_2004() { checkNotSubtype("{!null f1}","{any f2}"); }
	@Test public void test_2005() { checkNotSubtype("{!null f1}","{null f1}"); }
	@Test public void test_2006() { checkNotSubtype("{!null f1}","{null f2}"); }
	@Test public void test_2007() { checkNotSubtype("{!null f1}","!void"); }
	@Test public void test_2008() { checkIsSubtype("{!null f1}","!any"); }
	@Test public void test_2009() { checkNotSubtype("{!null f1}","!null"); }
	@Test public void test_2010() { checkIsSubtype("{!null f1}","{{void f1} f1}"); }
	@Test public void test_2011() { checkIsSubtype("{!null f1}","{{void f2} f1}"); }
	@Test public void test_2012() { checkIsSubtype("{!null f1}","{{void f1} f2}"); }
	@Test public void test_2013() { checkIsSubtype("{!null f1}","{{void f2} f2}"); }
	@Test public void test_2014() { checkIsSubtype("{!null f1}","{{any f1} f1}"); }
	@Test public void test_2015() { checkIsSubtype("{!null f1}","{{any f2} f1}"); }
	@Test public void test_2016() { checkNotSubtype("{!null f1}","{{any f1} f2}"); }
	@Test public void test_2017() { checkNotSubtype("{!null f1}","{{any f2} f2}"); }
	@Test public void test_2018() { checkIsSubtype("{!null f1}","{{null f1} f1}"); }
	@Test public void test_2019() { checkIsSubtype("{!null f1}","{{null f2} f1}"); }
	@Test public void test_2020() { checkNotSubtype("{!null f1}","{{null f1} f2}"); }
	@Test public void test_2021() { checkNotSubtype("{!null f1}","{{null f2} f2}"); }
	@Test public void test_2022() { checkNotSubtype("{!null f1}","{!void f1}"); }
	@Test public void test_2023() { checkNotSubtype("{!null f1}","{!void f2}"); }
	@Test public void test_2024() { checkIsSubtype("{!null f1}","{!any f1}"); }
	@Test public void test_2025() { checkIsSubtype("{!null f1}","{!any f2}"); }
	@Test public void test_2026() { checkIsSubtype("{!null f1}","{!null f1}"); }
	@Test public void test_2027() { checkNotSubtype("{!null f1}","{!null f2}"); }
	@Test public void test_2028() { checkIsSubtype("{!null f1}","void|void"); }
	@Test public void test_2029() { checkNotSubtype("{!null f1}","void|any"); }
	@Test public void test_2030() { checkNotSubtype("{!null f1}","void|null"); }
	@Test public void test_2031() { checkNotSubtype("{!null f1}","any|void"); }
	@Test public void test_2032() { checkNotSubtype("{!null f1}","any|any"); }
	@Test public void test_2033() { checkNotSubtype("{!null f1}","any|null"); }
	@Test public void test_2034() { checkNotSubtype("{!null f1}","null|void"); }
	@Test public void test_2035() { checkNotSubtype("{!null f1}","null|any"); }
	@Test public void test_2036() { checkNotSubtype("{!null f1}","null|null"); }
	@Test public void test_2037() { checkIsSubtype("{!null f1}","{void f1}|void"); }
	@Test public void test_2038() { checkIsSubtype("{!null f1}","{void f2}|void"); }
	@Test public void test_2039() { checkNotSubtype("{!null f1}","{any f1}|any"); }
	@Test public void test_2040() { checkNotSubtype("{!null f1}","{any f2}|any"); }
	@Test public void test_2041() { checkNotSubtype("{!null f1}","{null f1}|null"); }
	@Test public void test_2042() { checkNotSubtype("{!null f1}","{null f2}|null"); }
	@Test public void test_2043() { checkNotSubtype("{!null f1}","!void|void"); }
	@Test public void test_2044() { checkNotSubtype("{!null f1}","!any|any"); }
	@Test public void test_2045() { checkNotSubtype("{!null f1}","!null|null"); }
	@Test public void test_2046() { checkIsSubtype("{!null f1}","void&void"); }
	@Test public void test_2047() { checkIsSubtype("{!null f1}","void&any"); }
	@Test public void test_2048() { checkIsSubtype("{!null f1}","void&null"); }
	@Test public void test_2049() { checkIsSubtype("{!null f1}","any&void"); }
	@Test public void test_2050() { checkNotSubtype("{!null f1}","any&any"); }
	@Test public void test_2051() { checkNotSubtype("{!null f1}","any&null"); }
	@Test public void test_2052() { checkIsSubtype("{!null f1}","null&void"); }
	@Test public void test_2053() { checkNotSubtype("{!null f1}","null&any"); }
	@Test public void test_2054() { checkNotSubtype("{!null f1}","null&null"); }
	@Test public void test_2055() { checkIsSubtype("{!null f1}","{void f1}&void"); }
	@Test public void test_2056() { checkIsSubtype("{!null f1}","{void f2}&void"); }
	@Test public void test_2057() { checkNotSubtype("{!null f1}","{any f1}&any"); }
	@Test public void test_2058() { checkNotSubtype("{!null f1}","{any f2}&any"); }
	@Test public void test_2059() { checkIsSubtype("{!null f1}","{null f1}&null"); }
	@Test public void test_2060() { checkIsSubtype("{!null f1}","{null f2}&null"); }
	@Test public void test_2061() { checkIsSubtype("{!null f1}","!void&void"); }
	@Test public void test_2062() { checkIsSubtype("{!null f1}","!any&any"); }
	@Test public void test_2063() { checkIsSubtype("{!null f1}","!null&null"); }
	@Test public void test_2064() { checkNotSubtype("{!null f1}","!{void f1}"); }
	@Test public void test_2065() { checkNotSubtype("{!null f1}","!{void f2}"); }
	@Test public void test_2066() { checkNotSubtype("{!null f1}","!{any f1}"); }
	@Test public void test_2067() { checkNotSubtype("{!null f1}","!{any f2}"); }
	@Test public void test_2068() { checkNotSubtype("{!null f1}","!{null f1}"); }
	@Test public void test_2069() { checkNotSubtype("{!null f1}","!{null f2}"); }
	@Test public void test_2070() { checkIsSubtype("{!null f1}","!!void"); }
	@Test public void test_2071() { checkNotSubtype("{!null f1}","!!any"); }
	@Test public void test_2072() { checkNotSubtype("{!null f1}","!!null"); }
	@Test public void test_2073() { checkNotSubtype("{!null f2}","any"); }
	@Test public void test_2074() { checkNotSubtype("{!null f2}","null"); }
	@Test public void test_2075() { checkIsSubtype("{!null f2}","{void f1}"); }
	@Test public void test_2076() { checkIsSubtype("{!null f2}","{void f2}"); }
	@Test public void test_2077() { checkNotSubtype("{!null f2}","{any f1}"); }
	@Test public void test_2078() { checkNotSubtype("{!null f2}","{any f2}"); }
	@Test public void test_2079() { checkNotSubtype("{!null f2}","{null f1}"); }
	@Test public void test_2080() { checkNotSubtype("{!null f2}","{null f2}"); }
	@Test public void test_2081() { checkNotSubtype("{!null f2}","!void"); }
	@Test public void test_2082() { checkIsSubtype("{!null f2}","!any"); }
	@Test public void test_2083() { checkNotSubtype("{!null f2}","!null"); }
	@Test public void test_2084() { checkIsSubtype("{!null f2}","{{void f1} f1}"); }
	@Test public void test_2085() { checkIsSubtype("{!null f2}","{{void f2} f1}"); }
	@Test public void test_2086() { checkIsSubtype("{!null f2}","{{void f1} f2}"); }
	@Test public void test_2087() { checkIsSubtype("{!null f2}","{{void f2} f2}"); }
	@Test public void test_2088() { checkNotSubtype("{!null f2}","{{any f1} f1}"); }
	@Test public void test_2089() { checkNotSubtype("{!null f2}","{{any f2} f1}"); }
	@Test public void test_2090() { checkIsSubtype("{!null f2}","{{any f1} f2}"); }
	@Test public void test_2091() { checkIsSubtype("{!null f2}","{{any f2} f2}"); }
	@Test public void test_2092() { checkNotSubtype("{!null f2}","{{null f1} f1}"); }
	@Test public void test_2093() { checkNotSubtype("{!null f2}","{{null f2} f1}"); }
	@Test public void test_2094() { checkIsSubtype("{!null f2}","{{null f1} f2}"); }
	@Test public void test_2095() { checkIsSubtype("{!null f2}","{{null f2} f2}"); }
	@Test public void test_2096() { checkNotSubtype("{!null f2}","{!void f1}"); }
	@Test public void test_2097() { checkNotSubtype("{!null f2}","{!void f2}"); }
	@Test public void test_2098() { checkIsSubtype("{!null f2}","{!any f1}"); }
	@Test public void test_2099() { checkIsSubtype("{!null f2}","{!any f2}"); }
	@Test public void test_2100() { checkNotSubtype("{!null f2}","{!null f1}"); }
	@Test public void test_2101() { checkIsSubtype("{!null f2}","{!null f2}"); }
	@Test public void test_2102() { checkIsSubtype("{!null f2}","void|void"); }
	@Test public void test_2103() { checkNotSubtype("{!null f2}","void|any"); }
	@Test public void test_2104() { checkNotSubtype("{!null f2}","void|null"); }
	@Test public void test_2105() { checkNotSubtype("{!null f2}","any|void"); }
	@Test public void test_2106() { checkNotSubtype("{!null f2}","any|any"); }
	@Test public void test_2107() { checkNotSubtype("{!null f2}","any|null"); }
	@Test public void test_2108() { checkNotSubtype("{!null f2}","null|void"); }
	@Test public void test_2109() { checkNotSubtype("{!null f2}","null|any"); }
	@Test public void test_2110() { checkNotSubtype("{!null f2}","null|null"); }
	@Test public void test_2111() { checkIsSubtype("{!null f2}","{void f1}|void"); }
	@Test public void test_2112() { checkIsSubtype("{!null f2}","{void f2}|void"); }
	@Test public void test_2113() { checkNotSubtype("{!null f2}","{any f1}|any"); }
	@Test public void test_2114() { checkNotSubtype("{!null f2}","{any f2}|any"); }
	@Test public void test_2115() { checkNotSubtype("{!null f2}","{null f1}|null"); }
	@Test public void test_2116() { checkNotSubtype("{!null f2}","{null f2}|null"); }
	@Test public void test_2117() { checkNotSubtype("{!null f2}","!void|void"); }
	@Test public void test_2118() { checkNotSubtype("{!null f2}","!any|any"); }
	@Test public void test_2119() { checkNotSubtype("{!null f2}","!null|null"); }
	@Test public void test_2120() { checkIsSubtype("{!null f2}","void&void"); }
	@Test public void test_2121() { checkIsSubtype("{!null f2}","void&any"); }
	@Test public void test_2122() { checkIsSubtype("{!null f2}","void&null"); }
	@Test public void test_2123() { checkIsSubtype("{!null f2}","any&void"); }
	@Test public void test_2124() { checkNotSubtype("{!null f2}","any&any"); }
	@Test public void test_2125() { checkNotSubtype("{!null f2}","any&null"); }
	@Test public void test_2126() { checkIsSubtype("{!null f2}","null&void"); }
	@Test public void test_2127() { checkNotSubtype("{!null f2}","null&any"); }
	@Test public void test_2128() { checkNotSubtype("{!null f2}","null&null"); }
	@Test public void test_2129() { checkIsSubtype("{!null f2}","{void f1}&void"); }
	@Test public void test_2130() { checkIsSubtype("{!null f2}","{void f2}&void"); }
	@Test public void test_2131() { checkNotSubtype("{!null f2}","{any f1}&any"); }
	@Test public void test_2132() { checkNotSubtype("{!null f2}","{any f2}&any"); }
	@Test public void test_2133() { checkIsSubtype("{!null f2}","{null f1}&null"); }
	@Test public void test_2134() { checkIsSubtype("{!null f2}","{null f2}&null"); }
	@Test public void test_2135() { checkIsSubtype("{!null f2}","!void&void"); }
	@Test public void test_2136() { checkIsSubtype("{!null f2}","!any&any"); }
	@Test public void test_2137() { checkIsSubtype("{!null f2}","!null&null"); }
	@Test public void test_2138() { checkNotSubtype("{!null f2}","!{void f1}"); }
	@Test public void test_2139() { checkNotSubtype("{!null f2}","!{void f2}"); }
	@Test public void test_2140() { checkNotSubtype("{!null f2}","!{any f1}"); }
	@Test public void test_2141() { checkNotSubtype("{!null f2}","!{any f2}"); }
	@Test public void test_2142() { checkNotSubtype("{!null f2}","!{null f1}"); }
	@Test public void test_2143() { checkNotSubtype("{!null f2}","!{null f2}"); }
	@Test public void test_2144() { checkIsSubtype("{!null f2}","!!void"); }
	@Test public void test_2145() { checkNotSubtype("{!null f2}","!!any"); }
	@Test public void test_2146() { checkNotSubtype("{!null f2}","!!null"); }
	@Test public void test_2147() { checkNotSubtype("void|void","any"); }
	@Test public void test_2148() { checkNotSubtype("void|void","null"); }
	@Test public void test_2149() { checkIsSubtype("void|void","{void f1}"); }
	@Test public void test_2150() { checkIsSubtype("void|void","{void f2}"); }
	@Test public void test_2151() { checkNotSubtype("void|void","{any f1}"); }
	@Test public void test_2152() { checkNotSubtype("void|void","{any f2}"); }
	@Test public void test_2153() { checkNotSubtype("void|void","{null f1}"); }
	@Test public void test_2154() { checkNotSubtype("void|void","{null f2}"); }
	@Test public void test_2155() { checkNotSubtype("void|void","!void"); }
	@Test public void test_2156() { checkIsSubtype("void|void","!any"); }
	@Test public void test_2157() { checkNotSubtype("void|void","!null"); }
	@Test public void test_2158() { checkIsSubtype("void|void","{{void f1} f1}"); }
	@Test public void test_2159() { checkIsSubtype("void|void","{{void f2} f1}"); }
	@Test public void test_2160() { checkIsSubtype("void|void","{{void f1} f2}"); }
	@Test public void test_2161() { checkIsSubtype("void|void","{{void f2} f2}"); }
	@Test public void test_2162() { checkNotSubtype("void|void","{{any f1} f1}"); }
	@Test public void test_2163() { checkNotSubtype("void|void","{{any f2} f1}"); }
	@Test public void test_2164() { checkNotSubtype("void|void","{{any f1} f2}"); }
	@Test public void test_2165() { checkNotSubtype("void|void","{{any f2} f2}"); }
	@Test public void test_2166() { checkNotSubtype("void|void","{{null f1} f1}"); }
	@Test public void test_2167() { checkNotSubtype("void|void","{{null f2} f1}"); }
	@Test public void test_2168() { checkNotSubtype("void|void","{{null f1} f2}"); }
	@Test public void test_2169() { checkNotSubtype("void|void","{{null f2} f2}"); }
	@Test public void test_2170() { checkNotSubtype("void|void","{!void f1}"); }
	@Test public void test_2171() { checkNotSubtype("void|void","{!void f2}"); }
	@Test public void test_2172() { checkIsSubtype("void|void","{!any f1}"); }
	@Test public void test_2173() { checkIsSubtype("void|void","{!any f2}"); }
	@Test public void test_2174() { checkNotSubtype("void|void","{!null f1}"); }
	@Test public void test_2175() { checkNotSubtype("void|void","{!null f2}"); }
	@Test public void test_2176() { checkIsSubtype("void|void","void|void"); }
	@Test public void test_2177() { checkNotSubtype("void|void","void|any"); }
	@Test public void test_2178() { checkNotSubtype("void|void","void|null"); }
	@Test public void test_2179() { checkNotSubtype("void|void","any|void"); }
	@Test public void test_2180() { checkNotSubtype("void|void","any|any"); }
	@Test public void test_2181() { checkNotSubtype("void|void","any|null"); }
	@Test public void test_2182() { checkNotSubtype("void|void","null|void"); }
	@Test public void test_2183() { checkNotSubtype("void|void","null|any"); }
	@Test public void test_2184() { checkNotSubtype("void|void","null|null"); }
	@Test public void test_2185() { checkIsSubtype("void|void","{void f1}|void"); }
	@Test public void test_2186() { checkIsSubtype("void|void","{void f2}|void"); }
	@Test public void test_2187() { checkNotSubtype("void|void","{any f1}|any"); }
	@Test public void test_2188() { checkNotSubtype("void|void","{any f2}|any"); }
	@Test public void test_2189() { checkNotSubtype("void|void","{null f1}|null"); }
	@Test public void test_2190() { checkNotSubtype("void|void","{null f2}|null"); }
	@Test public void test_2191() { checkNotSubtype("void|void","!void|void"); }
	@Test public void test_2192() { checkNotSubtype("void|void","!any|any"); }
	@Test public void test_2193() { checkNotSubtype("void|void","!null|null"); }
	@Test public void test_2194() { checkIsSubtype("void|void","void&void"); }
	@Test public void test_2195() { checkIsSubtype("void|void","void&any"); }
	@Test public void test_2196() { checkIsSubtype("void|void","void&null"); }
	@Test public void test_2197() { checkIsSubtype("void|void","any&void"); }
	@Test public void test_2198() { checkNotSubtype("void|void","any&any"); }
	@Test public void test_2199() { checkNotSubtype("void|void","any&null"); }
	@Test public void test_2200() { checkIsSubtype("void|void","null&void"); }
	@Test public void test_2201() { checkNotSubtype("void|void","null&any"); }
	@Test public void test_2202() { checkNotSubtype("void|void","null&null"); }
	@Test public void test_2203() { checkIsSubtype("void|void","{void f1}&void"); }
	@Test public void test_2204() { checkIsSubtype("void|void","{void f2}&void"); }
	@Test public void test_2205() { checkNotSubtype("void|void","{any f1}&any"); }
	@Test public void test_2206() { checkNotSubtype("void|void","{any f2}&any"); }
	@Test public void test_2207() { checkIsSubtype("void|void","{null f1}&null"); }
	@Test public void test_2208() { checkIsSubtype("void|void","{null f2}&null"); }
	@Test public void test_2209() { checkIsSubtype("void|void","!void&void"); }
	@Test public void test_2210() { checkIsSubtype("void|void","!any&any"); }
	@Test public void test_2211() { checkIsSubtype("void|void","!null&null"); }
	@Test public void test_2212() { checkNotSubtype("void|void","!{void f1}"); }
	@Test public void test_2213() { checkNotSubtype("void|void","!{void f2}"); }
	@Test public void test_2214() { checkNotSubtype("void|void","!{any f1}"); }
	@Test public void test_2215() { checkNotSubtype("void|void","!{any f2}"); }
	@Test public void test_2216() { checkNotSubtype("void|void","!{null f1}"); }
	@Test public void test_2217() { checkNotSubtype("void|void","!{null f2}"); }
	@Test public void test_2218() { checkIsSubtype("void|void","!!void"); }
	@Test public void test_2219() { checkNotSubtype("void|void","!!any"); }
	@Test public void test_2220() { checkNotSubtype("void|void","!!null"); }
	@Test public void test_2221() { checkIsSubtype("void|any","any"); }
	@Test public void test_2222() { checkIsSubtype("void|any","null"); }
	@Test public void test_2223() { checkIsSubtype("void|any","{void f1}"); }
	@Test public void test_2224() { checkIsSubtype("void|any","{void f2}"); }
	@Test public void test_2225() { checkIsSubtype("void|any","{any f1}"); }
	@Test public void test_2226() { checkIsSubtype("void|any","{any f2}"); }
	@Test public void test_2227() { checkIsSubtype("void|any","{null f1}"); }
	@Test public void test_2228() { checkIsSubtype("void|any","{null f2}"); }
	@Test public void test_2229() { checkIsSubtype("void|any","!void"); }
	@Test public void test_2230() { checkIsSubtype("void|any","!any"); }
	@Test public void test_2231() { checkIsSubtype("void|any","!null"); }
	@Test public void test_2232() { checkIsSubtype("void|any","{{void f1} f1}"); }
	@Test public void test_2233() { checkIsSubtype("void|any","{{void f2} f1}"); }
	@Test public void test_2234() { checkIsSubtype("void|any","{{void f1} f2}"); }
	@Test public void test_2235() { checkIsSubtype("void|any","{{void f2} f2}"); }
	@Test public void test_2236() { checkIsSubtype("void|any","{{any f1} f1}"); }
	@Test public void test_2237() { checkIsSubtype("void|any","{{any f2} f1}"); }
	@Test public void test_2238() { checkIsSubtype("void|any","{{any f1} f2}"); }
	@Test public void test_2239() { checkIsSubtype("void|any","{{any f2} f2}"); }
	@Test public void test_2240() { checkIsSubtype("void|any","{{null f1} f1}"); }
	@Test public void test_2241() { checkIsSubtype("void|any","{{null f2} f1}"); }
	@Test public void test_2242() { checkIsSubtype("void|any","{{null f1} f2}"); }
	@Test public void test_2243() { checkIsSubtype("void|any","{{null f2} f2}"); }
	@Test public void test_2244() { checkIsSubtype("void|any","{!void f1}"); }
	@Test public void test_2245() { checkIsSubtype("void|any","{!void f2}"); }
	@Test public void test_2246() { checkIsSubtype("void|any","{!any f1}"); }
	@Test public void test_2247() { checkIsSubtype("void|any","{!any f2}"); }
	@Test public void test_2248() { checkIsSubtype("void|any","{!null f1}"); }
	@Test public void test_2249() { checkIsSubtype("void|any","{!null f2}"); }
	@Test public void test_2250() { checkIsSubtype("void|any","void|void"); }
	@Test public void test_2251() { checkIsSubtype("void|any","void|any"); }
	@Test public void test_2252() { checkIsSubtype("void|any","void|null"); }
	@Test public void test_2253() { checkIsSubtype("void|any","any|void"); }
	@Test public void test_2254() { checkIsSubtype("void|any","any|any"); }
	@Test public void test_2255() { checkIsSubtype("void|any","any|null"); }
	@Test public void test_2256() { checkIsSubtype("void|any","null|void"); }
	@Test public void test_2257() { checkIsSubtype("void|any","null|any"); }
	@Test public void test_2258() { checkIsSubtype("void|any","null|null"); }
	@Test public void test_2259() { checkIsSubtype("void|any","{void f1}|void"); }
	@Test public void test_2260() { checkIsSubtype("void|any","{void f2}|void"); }
	@Test public void test_2261() { checkIsSubtype("void|any","{any f1}|any"); }
	@Test public void test_2262() { checkIsSubtype("void|any","{any f2}|any"); }
	@Test public void test_2263() { checkIsSubtype("void|any","{null f1}|null"); }
	@Test public void test_2264() { checkIsSubtype("void|any","{null f2}|null"); }
	@Test public void test_2265() { checkIsSubtype("void|any","!void|void"); }
	@Test public void test_2266() { checkIsSubtype("void|any","!any|any"); }
	@Test public void test_2267() { checkIsSubtype("void|any","!null|null"); }
	@Test public void test_2268() { checkIsSubtype("void|any","void&void"); }
	@Test public void test_2269() { checkIsSubtype("void|any","void&any"); }
	@Test public void test_2270() { checkIsSubtype("void|any","void&null"); }
	@Test public void test_2271() { checkIsSubtype("void|any","any&void"); }
	@Test public void test_2272() { checkIsSubtype("void|any","any&any"); }
	@Test public void test_2273() { checkIsSubtype("void|any","any&null"); }
	@Test public void test_2274() { checkIsSubtype("void|any","null&void"); }
	@Test public void test_2275() { checkIsSubtype("void|any","null&any"); }
	@Test public void test_2276() { checkIsSubtype("void|any","null&null"); }
	@Test public void test_2277() { checkIsSubtype("void|any","{void f1}&void"); }
	@Test public void test_2278() { checkIsSubtype("void|any","{void f2}&void"); }
	@Test public void test_2279() { checkIsSubtype("void|any","{any f1}&any"); }
	@Test public void test_2280() { checkIsSubtype("void|any","{any f2}&any"); }
	@Test public void test_2281() { checkIsSubtype("void|any","{null f1}&null"); }
	@Test public void test_2282() { checkIsSubtype("void|any","{null f2}&null"); }
	@Test public void test_2283() { checkIsSubtype("void|any","!void&void"); }
	@Test public void test_2284() { checkIsSubtype("void|any","!any&any"); }
	@Test public void test_2285() { checkIsSubtype("void|any","!null&null"); }
	@Test public void test_2286() { checkIsSubtype("void|any","!{void f1}"); }
	@Test public void test_2287() { checkIsSubtype("void|any","!{void f2}"); }
	@Test public void test_2288() { checkIsSubtype("void|any","!{any f1}"); }
	@Test public void test_2289() { checkIsSubtype("void|any","!{any f2}"); }
	@Test public void test_2290() { checkIsSubtype("void|any","!{null f1}"); }
	@Test public void test_2291() { checkIsSubtype("void|any","!{null f2}"); }
	@Test public void test_2292() { checkIsSubtype("void|any","!!void"); }
	@Test public void test_2293() { checkIsSubtype("void|any","!!any"); }
	@Test public void test_2294() { checkIsSubtype("void|any","!!null"); }
	@Test public void test_2295() { checkNotSubtype("void|null","any"); }
	@Test public void test_2296() { checkIsSubtype("void|null","null"); }
	@Test public void test_2297() { checkIsSubtype("void|null","{void f1}"); }
	@Test public void test_2298() { checkIsSubtype("void|null","{void f2}"); }
	@Test public void test_2299() { checkNotSubtype("void|null","{any f1}"); }
	@Test public void test_2300() { checkNotSubtype("void|null","{any f2}"); }
	@Test public void test_2301() { checkNotSubtype("void|null","{null f1}"); }
	@Test public void test_2302() { checkNotSubtype("void|null","{null f2}"); }
	@Test public void test_2303() { checkNotSubtype("void|null","!void"); }
	@Test public void test_2304() { checkIsSubtype("void|null","!any"); }
	@Test public void test_2305() { checkNotSubtype("void|null","!null"); }
	@Test public void test_2306() { checkIsSubtype("void|null","{{void f1} f1}"); }
	@Test public void test_2307() { checkIsSubtype("void|null","{{void f2} f1}"); }
	@Test public void test_2308() { checkIsSubtype("void|null","{{void f1} f2}"); }
	@Test public void test_2309() { checkIsSubtype("void|null","{{void f2} f2}"); }
	@Test public void test_2310() { checkNotSubtype("void|null","{{any f1} f1}"); }
	@Test public void test_2311() { checkNotSubtype("void|null","{{any f2} f1}"); }
	@Test public void test_2312() { checkNotSubtype("void|null","{{any f1} f2}"); }
	@Test public void test_2313() { checkNotSubtype("void|null","{{any f2} f2}"); }
	@Test public void test_2314() { checkNotSubtype("void|null","{{null f1} f1}"); }
	@Test public void test_2315() { checkNotSubtype("void|null","{{null f2} f1}"); }
	@Test public void test_2316() { checkNotSubtype("void|null","{{null f1} f2}"); }
	@Test public void test_2317() { checkNotSubtype("void|null","{{null f2} f2}"); }
	@Test public void test_2318() { checkNotSubtype("void|null","{!void f1}"); }
	@Test public void test_2319() { checkNotSubtype("void|null","{!void f2}"); }
	@Test public void test_2320() { checkIsSubtype("void|null","{!any f1}"); }
	@Test public void test_2321() { checkIsSubtype("void|null","{!any f2}"); }
	@Test public void test_2322() { checkNotSubtype("void|null","{!null f1}"); }
	@Test public void test_2323() { checkNotSubtype("void|null","{!null f2}"); }
	@Test public void test_2324() { checkIsSubtype("void|null","void|void"); }
	@Test public void test_2325() { checkNotSubtype("void|null","void|any"); }
	@Test public void test_2326() { checkIsSubtype("void|null","void|null"); }
	@Test public void test_2327() { checkNotSubtype("void|null","any|void"); }
	@Test public void test_2328() { checkNotSubtype("void|null","any|any"); }
	@Test public void test_2329() { checkNotSubtype("void|null","any|null"); }
	@Test public void test_2330() { checkIsSubtype("void|null","null|void"); }
	@Test public void test_2331() { checkNotSubtype("void|null","null|any"); }
	@Test public void test_2332() { checkIsSubtype("void|null","null|null"); }
	@Test public void test_2333() { checkIsSubtype("void|null","{void f1}|void"); }
	@Test public void test_2334() { checkIsSubtype("void|null","{void f2}|void"); }
	@Test public void test_2335() { checkNotSubtype("void|null","{any f1}|any"); }
	@Test public void test_2336() { checkNotSubtype("void|null","{any f2}|any"); }
	@Test public void test_2337() { checkNotSubtype("void|null","{null f1}|null"); }
	@Test public void test_2338() { checkNotSubtype("void|null","{null f2}|null"); }
	@Test public void test_2339() { checkNotSubtype("void|null","!void|void"); }
	@Test public void test_2340() { checkNotSubtype("void|null","!any|any"); }
	@Test public void test_2341() { checkNotSubtype("void|null","!null|null"); }
	@Test public void test_2342() { checkIsSubtype("void|null","void&void"); }
	@Test public void test_2343() { checkIsSubtype("void|null","void&any"); }
	@Test public void test_2344() { checkIsSubtype("void|null","void&null"); }
	@Test public void test_2345() { checkIsSubtype("void|null","any&void"); }
	@Test public void test_2346() { checkNotSubtype("void|null","any&any"); }
	@Test public void test_2347() { checkIsSubtype("void|null","any&null"); }
	@Test public void test_2348() { checkIsSubtype("void|null","null&void"); }
	@Test public void test_2349() { checkIsSubtype("void|null","null&any"); }
	@Test public void test_2350() { checkIsSubtype("void|null","null&null"); }
	@Test public void test_2351() { checkIsSubtype("void|null","{void f1}&void"); }
	@Test public void test_2352() { checkIsSubtype("void|null","{void f2}&void"); }
	@Test public void test_2353() { checkNotSubtype("void|null","{any f1}&any"); }
	@Test public void test_2354() { checkNotSubtype("void|null","{any f2}&any"); }
	@Test public void test_2355() { checkIsSubtype("void|null","{null f1}&null"); }
	@Test public void test_2356() { checkIsSubtype("void|null","{null f2}&null"); }
	@Test public void test_2357() { checkIsSubtype("void|null","!void&void"); }
	@Test public void test_2358() { checkIsSubtype("void|null","!any&any"); }
	@Test public void test_2359() { checkIsSubtype("void|null","!null&null"); }
	@Test public void test_2360() { checkNotSubtype("void|null","!{void f1}"); }
	@Test public void test_2361() { checkNotSubtype("void|null","!{void f2}"); }
	@Test public void test_2362() { checkNotSubtype("void|null","!{any f1}"); }
	@Test public void test_2363() { checkNotSubtype("void|null","!{any f2}"); }
	@Test public void test_2364() { checkNotSubtype("void|null","!{null f1}"); }
	@Test public void test_2365() { checkNotSubtype("void|null","!{null f2}"); }
	@Test public void test_2366() { checkIsSubtype("void|null","!!void"); }
	@Test public void test_2367() { checkNotSubtype("void|null","!!any"); }
	@Test public void test_2368() { checkIsSubtype("void|null","!!null"); }
	@Test public void test_2369() { checkIsSubtype("any|void","any"); }
	@Test public void test_2370() { checkIsSubtype("any|void","null"); }
	@Test public void test_2371() { checkIsSubtype("any|void","{void f1}"); }
	@Test public void test_2372() { checkIsSubtype("any|void","{void f2}"); }
	@Test public void test_2373() { checkIsSubtype("any|void","{any f1}"); }
	@Test public void test_2374() { checkIsSubtype("any|void","{any f2}"); }
	@Test public void test_2375() { checkIsSubtype("any|void","{null f1}"); }
	@Test public void test_2376() { checkIsSubtype("any|void","{null f2}"); }
	@Test public void test_2377() { checkIsSubtype("any|void","!void"); }
	@Test public void test_2378() { checkIsSubtype("any|void","!any"); }
	@Test public void test_2379() { checkIsSubtype("any|void","!null"); }
	@Test public void test_2380() { checkIsSubtype("any|void","{{void f1} f1}"); }
	@Test public void test_2381() { checkIsSubtype("any|void","{{void f2} f1}"); }
	@Test public void test_2382() { checkIsSubtype("any|void","{{void f1} f2}"); }
	@Test public void test_2383() { checkIsSubtype("any|void","{{void f2} f2}"); }
	@Test public void test_2384() { checkIsSubtype("any|void","{{any f1} f1}"); }
	@Test public void test_2385() { checkIsSubtype("any|void","{{any f2} f1}"); }
	@Test public void test_2386() { checkIsSubtype("any|void","{{any f1} f2}"); }
	@Test public void test_2387() { checkIsSubtype("any|void","{{any f2} f2}"); }
	@Test public void test_2388() { checkIsSubtype("any|void","{{null f1} f1}"); }
	@Test public void test_2389() { checkIsSubtype("any|void","{{null f2} f1}"); }
	@Test public void test_2390() { checkIsSubtype("any|void","{{null f1} f2}"); }
	@Test public void test_2391() { checkIsSubtype("any|void","{{null f2} f2}"); }
	@Test public void test_2392() { checkIsSubtype("any|void","{!void f1}"); }
	@Test public void test_2393() { checkIsSubtype("any|void","{!void f2}"); }
	@Test public void test_2394() { checkIsSubtype("any|void","{!any f1}"); }
	@Test public void test_2395() { checkIsSubtype("any|void","{!any f2}"); }
	@Test public void test_2396() { checkIsSubtype("any|void","{!null f1}"); }
	@Test public void test_2397() { checkIsSubtype("any|void","{!null f2}"); }
	@Test public void test_2398() { checkIsSubtype("any|void","void|void"); }
	@Test public void test_2399() { checkIsSubtype("any|void","void|any"); }
	@Test public void test_2400() { checkIsSubtype("any|void","void|null"); }
	@Test public void test_2401() { checkIsSubtype("any|void","any|void"); }
	@Test public void test_2402() { checkIsSubtype("any|void","any|any"); }
	@Test public void test_2403() { checkIsSubtype("any|void","any|null"); }
	@Test public void test_2404() { checkIsSubtype("any|void","null|void"); }
	@Test public void test_2405() { checkIsSubtype("any|void","null|any"); }
	@Test public void test_2406() { checkIsSubtype("any|void","null|null"); }
	@Test public void test_2407() { checkIsSubtype("any|void","{void f1}|void"); }
	@Test public void test_2408() { checkIsSubtype("any|void","{void f2}|void"); }
	@Test public void test_2409() { checkIsSubtype("any|void","{any f1}|any"); }
	@Test public void test_2410() { checkIsSubtype("any|void","{any f2}|any"); }
	@Test public void test_2411() { checkIsSubtype("any|void","{null f1}|null"); }
	@Test public void test_2412() { checkIsSubtype("any|void","{null f2}|null"); }
	@Test public void test_2413() { checkIsSubtype("any|void","!void|void"); }
	@Test public void test_2414() { checkIsSubtype("any|void","!any|any"); }
	@Test public void test_2415() { checkIsSubtype("any|void","!null|null"); }
	@Test public void test_2416() { checkIsSubtype("any|void","void&void"); }
	@Test public void test_2417() { checkIsSubtype("any|void","void&any"); }
	@Test public void test_2418() { checkIsSubtype("any|void","void&null"); }
	@Test public void test_2419() { checkIsSubtype("any|void","any&void"); }
	@Test public void test_2420() { checkIsSubtype("any|void","any&any"); }
	@Test public void test_2421() { checkIsSubtype("any|void","any&null"); }
	@Test public void test_2422() { checkIsSubtype("any|void","null&void"); }
	@Test public void test_2423() { checkIsSubtype("any|void","null&any"); }
	@Test public void test_2424() { checkIsSubtype("any|void","null&null"); }
	@Test public void test_2425() { checkIsSubtype("any|void","{void f1}&void"); }
	@Test public void test_2426() { checkIsSubtype("any|void","{void f2}&void"); }
	@Test public void test_2427() { checkIsSubtype("any|void","{any f1}&any"); }
	@Test public void test_2428() { checkIsSubtype("any|void","{any f2}&any"); }
	@Test public void test_2429() { checkIsSubtype("any|void","{null f1}&null"); }
	@Test public void test_2430() { checkIsSubtype("any|void","{null f2}&null"); }
	@Test public void test_2431() { checkIsSubtype("any|void","!void&void"); }
	@Test public void test_2432() { checkIsSubtype("any|void","!any&any"); }
	@Test public void test_2433() { checkIsSubtype("any|void","!null&null"); }
	@Test public void test_2434() { checkIsSubtype("any|void","!{void f1}"); }
	@Test public void test_2435() { checkIsSubtype("any|void","!{void f2}"); }
	@Test public void test_2436() { checkIsSubtype("any|void","!{any f1}"); }
	@Test public void test_2437() { checkIsSubtype("any|void","!{any f2}"); }
	@Test public void test_2438() { checkIsSubtype("any|void","!{null f1}"); }
	@Test public void test_2439() { checkIsSubtype("any|void","!{null f2}"); }
	@Test public void test_2440() { checkIsSubtype("any|void","!!void"); }
	@Test public void test_2441() { checkIsSubtype("any|void","!!any"); }
	@Test public void test_2442() { checkIsSubtype("any|void","!!null"); }
	@Test public void test_2443() { checkIsSubtype("any|any","any"); }
	@Test public void test_2444() { checkIsSubtype("any|any","null"); }
	@Test public void test_2445() { checkIsSubtype("any|any","{void f1}"); }
	@Test public void test_2446() { checkIsSubtype("any|any","{void f2}"); }
	@Test public void test_2447() { checkIsSubtype("any|any","{any f1}"); }
	@Test public void test_2448() { checkIsSubtype("any|any","{any f2}"); }
	@Test public void test_2449() { checkIsSubtype("any|any","{null f1}"); }
	@Test public void test_2450() { checkIsSubtype("any|any","{null f2}"); }
	@Test public void test_2451() { checkIsSubtype("any|any","!void"); }
	@Test public void test_2452() { checkIsSubtype("any|any","!any"); }
	@Test public void test_2453() { checkIsSubtype("any|any","!null"); }
	@Test public void test_2454() { checkIsSubtype("any|any","{{void f1} f1}"); }
	@Test public void test_2455() { checkIsSubtype("any|any","{{void f2} f1}"); }
	@Test public void test_2456() { checkIsSubtype("any|any","{{void f1} f2}"); }
	@Test public void test_2457() { checkIsSubtype("any|any","{{void f2} f2}"); }
	@Test public void test_2458() { checkIsSubtype("any|any","{{any f1} f1}"); }
	@Test public void test_2459() { checkIsSubtype("any|any","{{any f2} f1}"); }
	@Test public void test_2460() { checkIsSubtype("any|any","{{any f1} f2}"); }
	@Test public void test_2461() { checkIsSubtype("any|any","{{any f2} f2}"); }
	@Test public void test_2462() { checkIsSubtype("any|any","{{null f1} f1}"); }
	@Test public void test_2463() { checkIsSubtype("any|any","{{null f2} f1}"); }
	@Test public void test_2464() { checkIsSubtype("any|any","{{null f1} f2}"); }
	@Test public void test_2465() { checkIsSubtype("any|any","{{null f2} f2}"); }
	@Test public void test_2466() { checkIsSubtype("any|any","{!void f1}"); }
	@Test public void test_2467() { checkIsSubtype("any|any","{!void f2}"); }
	@Test public void test_2468() { checkIsSubtype("any|any","{!any f1}"); }
	@Test public void test_2469() { checkIsSubtype("any|any","{!any f2}"); }
	@Test public void test_2470() { checkIsSubtype("any|any","{!null f1}"); }
	@Test public void test_2471() { checkIsSubtype("any|any","{!null f2}"); }
	@Test public void test_2472() { checkIsSubtype("any|any","void|void"); }
	@Test public void test_2473() { checkIsSubtype("any|any","void|any"); }
	@Test public void test_2474() { checkIsSubtype("any|any","void|null"); }
	@Test public void test_2475() { checkIsSubtype("any|any","any|void"); }
	@Test public void test_2476() { checkIsSubtype("any|any","any|any"); }
	@Test public void test_2477() { checkIsSubtype("any|any","any|null"); }
	@Test public void test_2478() { checkIsSubtype("any|any","null|void"); }
	@Test public void test_2479() { checkIsSubtype("any|any","null|any"); }
	@Test public void test_2480() { checkIsSubtype("any|any","null|null"); }
	@Test public void test_2481() { checkIsSubtype("any|any","{void f1}|void"); }
	@Test public void test_2482() { checkIsSubtype("any|any","{void f2}|void"); }
	@Test public void test_2483() { checkIsSubtype("any|any","{any f1}|any"); }
	@Test public void test_2484() { checkIsSubtype("any|any","{any f2}|any"); }
	@Test public void test_2485() { checkIsSubtype("any|any","{null f1}|null"); }
	@Test public void test_2486() { checkIsSubtype("any|any","{null f2}|null"); }
	@Test public void test_2487() { checkIsSubtype("any|any","!void|void"); }
	@Test public void test_2488() { checkIsSubtype("any|any","!any|any"); }
	@Test public void test_2489() { checkIsSubtype("any|any","!null|null"); }
	@Test public void test_2490() { checkIsSubtype("any|any","void&void"); }
	@Test public void test_2491() { checkIsSubtype("any|any","void&any"); }
	@Test public void test_2492() { checkIsSubtype("any|any","void&null"); }
	@Test public void test_2493() { checkIsSubtype("any|any","any&void"); }
	@Test public void test_2494() { checkIsSubtype("any|any","any&any"); }
	@Test public void test_2495() { checkIsSubtype("any|any","any&null"); }
	@Test public void test_2496() { checkIsSubtype("any|any","null&void"); }
	@Test public void test_2497() { checkIsSubtype("any|any","null&any"); }
	@Test public void test_2498() { checkIsSubtype("any|any","null&null"); }
	@Test public void test_2499() { checkIsSubtype("any|any","{void f1}&void"); }
	@Test public void test_2500() { checkIsSubtype("any|any","{void f2}&void"); }
	@Test public void test_2501() { checkIsSubtype("any|any","{any f1}&any"); }
	@Test public void test_2502() { checkIsSubtype("any|any","{any f2}&any"); }
	@Test public void test_2503() { checkIsSubtype("any|any","{null f1}&null"); }
	@Test public void test_2504() { checkIsSubtype("any|any","{null f2}&null"); }
	@Test public void test_2505() { checkIsSubtype("any|any","!void&void"); }
	@Test public void test_2506() { checkIsSubtype("any|any","!any&any"); }
	@Test public void test_2507() { checkIsSubtype("any|any","!null&null"); }
	@Test public void test_2508() { checkIsSubtype("any|any","!{void f1}"); }
	@Test public void test_2509() { checkIsSubtype("any|any","!{void f2}"); }
	@Test public void test_2510() { checkIsSubtype("any|any","!{any f1}"); }
	@Test public void test_2511() { checkIsSubtype("any|any","!{any f2}"); }
	@Test public void test_2512() { checkIsSubtype("any|any","!{null f1}"); }
	@Test public void test_2513() { checkIsSubtype("any|any","!{null f2}"); }
	@Test public void test_2514() { checkIsSubtype("any|any","!!void"); }
	@Test public void test_2515() { checkIsSubtype("any|any","!!any"); }
	@Test public void test_2516() { checkIsSubtype("any|any","!!null"); }
	@Test public void test_2517() { checkIsSubtype("any|null","any"); }
	@Test public void test_2518() { checkIsSubtype("any|null","null"); }
	@Test public void test_2519() { checkIsSubtype("any|null","{void f1}"); }
	@Test public void test_2520() { checkIsSubtype("any|null","{void f2}"); }
	@Test public void test_2521() { checkIsSubtype("any|null","{any f1}"); }
	@Test public void test_2522() { checkIsSubtype("any|null","{any f2}"); }
	@Test public void test_2523() { checkIsSubtype("any|null","{null f1}"); }
	@Test public void test_2524() { checkIsSubtype("any|null","{null f2}"); }
	@Test public void test_2525() { checkIsSubtype("any|null","!void"); }
	@Test public void test_2526() { checkIsSubtype("any|null","!any"); }
	@Test public void test_2527() { checkIsSubtype("any|null","!null"); }
	@Test public void test_2528() { checkIsSubtype("any|null","{{void f1} f1}"); }
	@Test public void test_2529() { checkIsSubtype("any|null","{{void f2} f1}"); }
	@Test public void test_2530() { checkIsSubtype("any|null","{{void f1} f2}"); }
	@Test public void test_2531() { checkIsSubtype("any|null","{{void f2} f2}"); }
	@Test public void test_2532() { checkIsSubtype("any|null","{{any f1} f1}"); }
	@Test public void test_2533() { checkIsSubtype("any|null","{{any f2} f1}"); }
	@Test public void test_2534() { checkIsSubtype("any|null","{{any f1} f2}"); }
	@Test public void test_2535() { checkIsSubtype("any|null","{{any f2} f2}"); }
	@Test public void test_2536() { checkIsSubtype("any|null","{{null f1} f1}"); }
	@Test public void test_2537() { checkIsSubtype("any|null","{{null f2} f1}"); }
	@Test public void test_2538() { checkIsSubtype("any|null","{{null f1} f2}"); }
	@Test public void test_2539() { checkIsSubtype("any|null","{{null f2} f2}"); }
	@Test public void test_2540() { checkIsSubtype("any|null","{!void f1}"); }
	@Test public void test_2541() { checkIsSubtype("any|null","{!void f2}"); }
	@Test public void test_2542() { checkIsSubtype("any|null","{!any f1}"); }
	@Test public void test_2543() { checkIsSubtype("any|null","{!any f2}"); }
	@Test public void test_2544() { checkIsSubtype("any|null","{!null f1}"); }
	@Test public void test_2545() { checkIsSubtype("any|null","{!null f2}"); }
	@Test public void test_2546() { checkIsSubtype("any|null","void|void"); }
	@Test public void test_2547() { checkIsSubtype("any|null","void|any"); }
	@Test public void test_2548() { checkIsSubtype("any|null","void|null"); }
	@Test public void test_2549() { checkIsSubtype("any|null","any|void"); }
	@Test public void test_2550() { checkIsSubtype("any|null","any|any"); }
	@Test public void test_2551() { checkIsSubtype("any|null","any|null"); }
	@Test public void test_2552() { checkIsSubtype("any|null","null|void"); }
	@Test public void test_2553() { checkIsSubtype("any|null","null|any"); }
	@Test public void test_2554() { checkIsSubtype("any|null","null|null"); }
	@Test public void test_2555() { checkIsSubtype("any|null","{void f1}|void"); }
	@Test public void test_2556() { checkIsSubtype("any|null","{void f2}|void"); }
	@Test public void test_2557() { checkIsSubtype("any|null","{any f1}|any"); }
	@Test public void test_2558() { checkIsSubtype("any|null","{any f2}|any"); }
	@Test public void test_2559() { checkIsSubtype("any|null","{null f1}|null"); }
	@Test public void test_2560() { checkIsSubtype("any|null","{null f2}|null"); }
	@Test public void test_2561() { checkIsSubtype("any|null","!void|void"); }
	@Test public void test_2562() { checkIsSubtype("any|null","!any|any"); }
	@Test public void test_2563() { checkIsSubtype("any|null","!null|null"); }
	@Test public void test_2564() { checkIsSubtype("any|null","void&void"); }
	@Test public void test_2565() { checkIsSubtype("any|null","void&any"); }
	@Test public void test_2566() { checkIsSubtype("any|null","void&null"); }
	@Test public void test_2567() { checkIsSubtype("any|null","any&void"); }
	@Test public void test_2568() { checkIsSubtype("any|null","any&any"); }
	@Test public void test_2569() { checkIsSubtype("any|null","any&null"); }
	@Test public void test_2570() { checkIsSubtype("any|null","null&void"); }
	@Test public void test_2571() { checkIsSubtype("any|null","null&any"); }
	@Test public void test_2572() { checkIsSubtype("any|null","null&null"); }
	@Test public void test_2573() { checkIsSubtype("any|null","{void f1}&void"); }
	@Test public void test_2574() { checkIsSubtype("any|null","{void f2}&void"); }
	@Test public void test_2575() { checkIsSubtype("any|null","{any f1}&any"); }
	@Test public void test_2576() { checkIsSubtype("any|null","{any f2}&any"); }
	@Test public void test_2577() { checkIsSubtype("any|null","{null f1}&null"); }
	@Test public void test_2578() { checkIsSubtype("any|null","{null f2}&null"); }
	@Test public void test_2579() { checkIsSubtype("any|null","!void&void"); }
	@Test public void test_2580() { checkIsSubtype("any|null","!any&any"); }
	@Test public void test_2581() { checkIsSubtype("any|null","!null&null"); }
	@Test public void test_2582() { checkIsSubtype("any|null","!{void f1}"); }
	@Test public void test_2583() { checkIsSubtype("any|null","!{void f2}"); }
	@Test public void test_2584() { checkIsSubtype("any|null","!{any f1}"); }
	@Test public void test_2585() { checkIsSubtype("any|null","!{any f2}"); }
	@Test public void test_2586() { checkIsSubtype("any|null","!{null f1}"); }
	@Test public void test_2587() { checkIsSubtype("any|null","!{null f2}"); }
	@Test public void test_2588() { checkIsSubtype("any|null","!!void"); }
	@Test public void test_2589() { checkIsSubtype("any|null","!!any"); }
	@Test public void test_2590() { checkIsSubtype("any|null","!!null"); }
	@Test public void test_2591() { checkNotSubtype("null|void","any"); }
	@Test public void test_2592() { checkIsSubtype("null|void","null"); }
	@Test public void test_2593() { checkIsSubtype("null|void","{void f1}"); }
	@Test public void test_2594() { checkIsSubtype("null|void","{void f2}"); }
	@Test public void test_2595() { checkNotSubtype("null|void","{any f1}"); }
	@Test public void test_2596() { checkNotSubtype("null|void","{any f2}"); }
	@Test public void test_2597() { checkNotSubtype("null|void","{null f1}"); }
	@Test public void test_2598() { checkNotSubtype("null|void","{null f2}"); }
	@Test public void test_2599() { checkNotSubtype("null|void","!void"); }
	@Test public void test_2600() { checkIsSubtype("null|void","!any"); }
	@Test public void test_2601() { checkNotSubtype("null|void","!null"); }
	@Test public void test_2602() { checkIsSubtype("null|void","{{void f1} f1}"); }
	@Test public void test_2603() { checkIsSubtype("null|void","{{void f2} f1}"); }
	@Test public void test_2604() { checkIsSubtype("null|void","{{void f1} f2}"); }
	@Test public void test_2605() { checkIsSubtype("null|void","{{void f2} f2}"); }
	@Test public void test_2606() { checkNotSubtype("null|void","{{any f1} f1}"); }
	@Test public void test_2607() { checkNotSubtype("null|void","{{any f2} f1}"); }
	@Test public void test_2608() { checkNotSubtype("null|void","{{any f1} f2}"); }
	@Test public void test_2609() { checkNotSubtype("null|void","{{any f2} f2}"); }
	@Test public void test_2610() { checkNotSubtype("null|void","{{null f1} f1}"); }
	@Test public void test_2611() { checkNotSubtype("null|void","{{null f2} f1}"); }
	@Test public void test_2612() { checkNotSubtype("null|void","{{null f1} f2}"); }
	@Test public void test_2613() { checkNotSubtype("null|void","{{null f2} f2}"); }
	@Test public void test_2614() { checkNotSubtype("null|void","{!void f1}"); }
	@Test public void test_2615() { checkNotSubtype("null|void","{!void f2}"); }
	@Test public void test_2616() { checkIsSubtype("null|void","{!any f1}"); }
	@Test public void test_2617() { checkIsSubtype("null|void","{!any f2}"); }
	@Test public void test_2618() { checkNotSubtype("null|void","{!null f1}"); }
	@Test public void test_2619() { checkNotSubtype("null|void","{!null f2}"); }
	@Test public void test_2620() { checkIsSubtype("null|void","void|void"); }
	@Test public void test_2621() { checkNotSubtype("null|void","void|any"); }
	@Test public void test_2622() { checkIsSubtype("null|void","void|null"); }
	@Test public void test_2623() { checkNotSubtype("null|void","any|void"); }
	@Test public void test_2624() { checkNotSubtype("null|void","any|any"); }
	@Test public void test_2625() { checkNotSubtype("null|void","any|null"); }
	@Test public void test_2626() { checkIsSubtype("null|void","null|void"); }
	@Test public void test_2627() { checkNotSubtype("null|void","null|any"); }
	@Test public void test_2628() { checkIsSubtype("null|void","null|null"); }
	@Test public void test_2629() { checkIsSubtype("null|void","{void f1}|void"); }
	@Test public void test_2630() { checkIsSubtype("null|void","{void f2}|void"); }
	@Test public void test_2631() { checkNotSubtype("null|void","{any f1}|any"); }
	@Test public void test_2632() { checkNotSubtype("null|void","{any f2}|any"); }
	@Test public void test_2633() { checkNotSubtype("null|void","{null f1}|null"); }
	@Test public void test_2634() { checkNotSubtype("null|void","{null f2}|null"); }
	@Test public void test_2635() { checkNotSubtype("null|void","!void|void"); }
	@Test public void test_2636() { checkNotSubtype("null|void","!any|any"); }
	@Test public void test_2637() { checkNotSubtype("null|void","!null|null"); }
	@Test public void test_2638() { checkIsSubtype("null|void","void&void"); }
	@Test public void test_2639() { checkIsSubtype("null|void","void&any"); }
	@Test public void test_2640() { checkIsSubtype("null|void","void&null"); }
	@Test public void test_2641() { checkIsSubtype("null|void","any&void"); }
	@Test public void test_2642() { checkNotSubtype("null|void","any&any"); }
	@Test public void test_2643() { checkIsSubtype("null|void","any&null"); }
	@Test public void test_2644() { checkIsSubtype("null|void","null&void"); }
	@Test public void test_2645() { checkIsSubtype("null|void","null&any"); }
	@Test public void test_2646() { checkIsSubtype("null|void","null&null"); }
	@Test public void test_2647() { checkIsSubtype("null|void","{void f1}&void"); }
	@Test public void test_2648() { checkIsSubtype("null|void","{void f2}&void"); }
	@Test public void test_2649() { checkNotSubtype("null|void","{any f1}&any"); }
	@Test public void test_2650() { checkNotSubtype("null|void","{any f2}&any"); }
	@Test public void test_2651() { checkIsSubtype("null|void","{null f1}&null"); }
	@Test public void test_2652() { checkIsSubtype("null|void","{null f2}&null"); }
	@Test public void test_2653() { checkIsSubtype("null|void","!void&void"); }
	@Test public void test_2654() { checkIsSubtype("null|void","!any&any"); }
	@Test public void test_2655() { checkIsSubtype("null|void","!null&null"); }
	@Test public void test_2656() { checkNotSubtype("null|void","!{void f1}"); }
	@Test public void test_2657() { checkNotSubtype("null|void","!{void f2}"); }
	@Test public void test_2658() { checkNotSubtype("null|void","!{any f1}"); }
	@Test public void test_2659() { checkNotSubtype("null|void","!{any f2}"); }
	@Test public void test_2660() { checkNotSubtype("null|void","!{null f1}"); }
	@Test public void test_2661() { checkNotSubtype("null|void","!{null f2}"); }
	@Test public void test_2662() { checkIsSubtype("null|void","!!void"); }
	@Test public void test_2663() { checkNotSubtype("null|void","!!any"); }
	@Test public void test_2664() { checkIsSubtype("null|void","!!null"); }
	@Test public void test_2665() { checkIsSubtype("null|any","any"); }
	@Test public void test_2666() { checkIsSubtype("null|any","null"); }
	@Test public void test_2667() { checkIsSubtype("null|any","{void f1}"); }
	@Test public void test_2668() { checkIsSubtype("null|any","{void f2}"); }
	@Test public void test_2669() { checkIsSubtype("null|any","{any f1}"); }
	@Test public void test_2670() { checkIsSubtype("null|any","{any f2}"); }
	@Test public void test_2671() { checkIsSubtype("null|any","{null f1}"); }
	@Test public void test_2672() { checkIsSubtype("null|any","{null f2}"); }
	@Test public void test_2673() { checkIsSubtype("null|any","!void"); }
	@Test public void test_2674() { checkIsSubtype("null|any","!any"); }
	@Test public void test_2675() { checkIsSubtype("null|any","!null"); }
	@Test public void test_2676() { checkIsSubtype("null|any","{{void f1} f1}"); }
	@Test public void test_2677() { checkIsSubtype("null|any","{{void f2} f1}"); }
	@Test public void test_2678() { checkIsSubtype("null|any","{{void f1} f2}"); }
	@Test public void test_2679() { checkIsSubtype("null|any","{{void f2} f2}"); }
	@Test public void test_2680() { checkIsSubtype("null|any","{{any f1} f1}"); }
	@Test public void test_2681() { checkIsSubtype("null|any","{{any f2} f1}"); }
	@Test public void test_2682() { checkIsSubtype("null|any","{{any f1} f2}"); }
	@Test public void test_2683() { checkIsSubtype("null|any","{{any f2} f2}"); }
	@Test public void test_2684() { checkIsSubtype("null|any","{{null f1} f1}"); }
	@Test public void test_2685() { checkIsSubtype("null|any","{{null f2} f1}"); }
	@Test public void test_2686() { checkIsSubtype("null|any","{{null f1} f2}"); }
	@Test public void test_2687() { checkIsSubtype("null|any","{{null f2} f2}"); }
	@Test public void test_2688() { checkIsSubtype("null|any","{!void f1}"); }
	@Test public void test_2689() { checkIsSubtype("null|any","{!void f2}"); }
	@Test public void test_2690() { checkIsSubtype("null|any","{!any f1}"); }
	@Test public void test_2691() { checkIsSubtype("null|any","{!any f2}"); }
	@Test public void test_2692() { checkIsSubtype("null|any","{!null f1}"); }
	@Test public void test_2693() { checkIsSubtype("null|any","{!null f2}"); }
	@Test public void test_2694() { checkIsSubtype("null|any","void|void"); }
	@Test public void test_2695() { checkIsSubtype("null|any","void|any"); }
	@Test public void test_2696() { checkIsSubtype("null|any","void|null"); }
	@Test public void test_2697() { checkIsSubtype("null|any","any|void"); }
	@Test public void test_2698() { checkIsSubtype("null|any","any|any"); }
	@Test public void test_2699() { checkIsSubtype("null|any","any|null"); }
	@Test public void test_2700() { checkIsSubtype("null|any","null|void"); }
	@Test public void test_2701() { checkIsSubtype("null|any","null|any"); }
	@Test public void test_2702() { checkIsSubtype("null|any","null|null"); }
	@Test public void test_2703() { checkIsSubtype("null|any","{void f1}|void"); }
	@Test public void test_2704() { checkIsSubtype("null|any","{void f2}|void"); }
	@Test public void test_2705() { checkIsSubtype("null|any","{any f1}|any"); }
	@Test public void test_2706() { checkIsSubtype("null|any","{any f2}|any"); }
	@Test public void test_2707() { checkIsSubtype("null|any","{null f1}|null"); }
	@Test public void test_2708() { checkIsSubtype("null|any","{null f2}|null"); }
	@Test public void test_2709() { checkIsSubtype("null|any","!void|void"); }
	@Test public void test_2710() { checkIsSubtype("null|any","!any|any"); }
	@Test public void test_2711() { checkIsSubtype("null|any","!null|null"); }
	@Test public void test_2712() { checkIsSubtype("null|any","void&void"); }
	@Test public void test_2713() { checkIsSubtype("null|any","void&any"); }
	@Test public void test_2714() { checkIsSubtype("null|any","void&null"); }
	@Test public void test_2715() { checkIsSubtype("null|any","any&void"); }
	@Test public void test_2716() { checkIsSubtype("null|any","any&any"); }
	@Test public void test_2717() { checkIsSubtype("null|any","any&null"); }
	@Test public void test_2718() { checkIsSubtype("null|any","null&void"); }
	@Test public void test_2719() { checkIsSubtype("null|any","null&any"); }
	@Test public void test_2720() { checkIsSubtype("null|any","null&null"); }
	@Test public void test_2721() { checkIsSubtype("null|any","{void f1}&void"); }
	@Test public void test_2722() { checkIsSubtype("null|any","{void f2}&void"); }
	@Test public void test_2723() { checkIsSubtype("null|any","{any f1}&any"); }
	@Test public void test_2724() { checkIsSubtype("null|any","{any f2}&any"); }
	@Test public void test_2725() { checkIsSubtype("null|any","{null f1}&null"); }
	@Test public void test_2726() { checkIsSubtype("null|any","{null f2}&null"); }
	@Test public void test_2727() { checkIsSubtype("null|any","!void&void"); }
	@Test public void test_2728() { checkIsSubtype("null|any","!any&any"); }
	@Test public void test_2729() { checkIsSubtype("null|any","!null&null"); }
	@Test public void test_2730() { checkIsSubtype("null|any","!{void f1}"); }
	@Test public void test_2731() { checkIsSubtype("null|any","!{void f2}"); }
	@Test public void test_2732() { checkIsSubtype("null|any","!{any f1}"); }
	@Test public void test_2733() { checkIsSubtype("null|any","!{any f2}"); }
	@Test public void test_2734() { checkIsSubtype("null|any","!{null f1}"); }
	@Test public void test_2735() { checkIsSubtype("null|any","!{null f2}"); }
	@Test public void test_2736() { checkIsSubtype("null|any","!!void"); }
	@Test public void test_2737() { checkIsSubtype("null|any","!!any"); }
	@Test public void test_2738() { checkIsSubtype("null|any","!!null"); }
	@Test public void test_2739() { checkNotSubtype("null|null","any"); }
	@Test public void test_2740() { checkIsSubtype("null|null","null"); }
	@Test public void test_2741() { checkIsSubtype("null|null","{void f1}"); }
	@Test public void test_2742() { checkIsSubtype("null|null","{void f2}"); }
	@Test public void test_2743() { checkNotSubtype("null|null","{any f1}"); }
	@Test public void test_2744() { checkNotSubtype("null|null","{any f2}"); }
	@Test public void test_2745() { checkNotSubtype("null|null","{null f1}"); }
	@Test public void test_2746() { checkNotSubtype("null|null","{null f2}"); }
	@Test public void test_2747() { checkNotSubtype("null|null","!void"); }
	@Test public void test_2748() { checkIsSubtype("null|null","!any"); }
	@Test public void test_2749() { checkNotSubtype("null|null","!null"); }
	@Test public void test_2750() { checkIsSubtype("null|null","{{void f1} f1}"); }
	@Test public void test_2751() { checkIsSubtype("null|null","{{void f2} f1}"); }
	@Test public void test_2752() { checkIsSubtype("null|null","{{void f1} f2}"); }
	@Test public void test_2753() { checkIsSubtype("null|null","{{void f2} f2}"); }
	@Test public void test_2754() { checkNotSubtype("null|null","{{any f1} f1}"); }
	@Test public void test_2755() { checkNotSubtype("null|null","{{any f2} f1}"); }
	@Test public void test_2756() { checkNotSubtype("null|null","{{any f1} f2}"); }
	@Test public void test_2757() { checkNotSubtype("null|null","{{any f2} f2}"); }
	@Test public void test_2758() { checkNotSubtype("null|null","{{null f1} f1}"); }
	@Test public void test_2759() { checkNotSubtype("null|null","{{null f2} f1}"); }
	@Test public void test_2760() { checkNotSubtype("null|null","{{null f1} f2}"); }
	@Test public void test_2761() { checkNotSubtype("null|null","{{null f2} f2}"); }
	@Test public void test_2762() { checkNotSubtype("null|null","{!void f1}"); }
	@Test public void test_2763() { checkNotSubtype("null|null","{!void f2}"); }
	@Test public void test_2764() { checkIsSubtype("null|null","{!any f1}"); }
	@Test public void test_2765() { checkIsSubtype("null|null","{!any f2}"); }
	@Test public void test_2766() { checkNotSubtype("null|null","{!null f1}"); }
	@Test public void test_2767() { checkNotSubtype("null|null","{!null f2}"); }
	@Test public void test_2768() { checkIsSubtype("null|null","void|void"); }
	@Test public void test_2769() { checkNotSubtype("null|null","void|any"); }
	@Test public void test_2770() { checkIsSubtype("null|null","void|null"); }
	@Test public void test_2771() { checkNotSubtype("null|null","any|void"); }
	@Test public void test_2772() { checkNotSubtype("null|null","any|any"); }
	@Test public void test_2773() { checkNotSubtype("null|null","any|null"); }
	@Test public void test_2774() { checkIsSubtype("null|null","null|void"); }
	@Test public void test_2775() { checkNotSubtype("null|null","null|any"); }
	@Test public void test_2776() { checkIsSubtype("null|null","null|null"); }
	@Test public void test_2777() { checkIsSubtype("null|null","{void f1}|void"); }
	@Test public void test_2778() { checkIsSubtype("null|null","{void f2}|void"); }
	@Test public void test_2779() { checkNotSubtype("null|null","{any f1}|any"); }
	@Test public void test_2780() { checkNotSubtype("null|null","{any f2}|any"); }
	@Test public void test_2781() { checkNotSubtype("null|null","{null f1}|null"); }
	@Test public void test_2782() { checkNotSubtype("null|null","{null f2}|null"); }
	@Test public void test_2783() { checkNotSubtype("null|null","!void|void"); }
	@Test public void test_2784() { checkNotSubtype("null|null","!any|any"); }
	@Test public void test_2785() { checkNotSubtype("null|null","!null|null"); }
	@Test public void test_2786() { checkIsSubtype("null|null","void&void"); }
	@Test public void test_2787() { checkIsSubtype("null|null","void&any"); }
	@Test public void test_2788() { checkIsSubtype("null|null","void&null"); }
	@Test public void test_2789() { checkIsSubtype("null|null","any&void"); }
	@Test public void test_2790() { checkNotSubtype("null|null","any&any"); }
	@Test public void test_2791() { checkIsSubtype("null|null","any&null"); }
	@Test public void test_2792() { checkIsSubtype("null|null","null&void"); }
	@Test public void test_2793() { checkIsSubtype("null|null","null&any"); }
	@Test public void test_2794() { checkIsSubtype("null|null","null&null"); }
	@Test public void test_2795() { checkIsSubtype("null|null","{void f1}&void"); }
	@Test public void test_2796() { checkIsSubtype("null|null","{void f2}&void"); }
	@Test public void test_2797() { checkNotSubtype("null|null","{any f1}&any"); }
	@Test public void test_2798() { checkNotSubtype("null|null","{any f2}&any"); }
	@Test public void test_2799() { checkIsSubtype("null|null","{null f1}&null"); }
	@Test public void test_2800() { checkIsSubtype("null|null","{null f2}&null"); }
	@Test public void test_2801() { checkIsSubtype("null|null","!void&void"); }
	@Test public void test_2802() { checkIsSubtype("null|null","!any&any"); }
	@Test public void test_2803() { checkIsSubtype("null|null","!null&null"); }
	@Test public void test_2804() { checkNotSubtype("null|null","!{void f1}"); }
	@Test public void test_2805() { checkNotSubtype("null|null","!{void f2}"); }
	@Test public void test_2806() { checkNotSubtype("null|null","!{any f1}"); }
	@Test public void test_2807() { checkNotSubtype("null|null","!{any f2}"); }
	@Test public void test_2808() { checkNotSubtype("null|null","!{null f1}"); }
	@Test public void test_2809() { checkNotSubtype("null|null","!{null f2}"); }
	@Test public void test_2810() { checkIsSubtype("null|null","!!void"); }
	@Test public void test_2811() { checkNotSubtype("null|null","!!any"); }
	@Test public void test_2812() { checkIsSubtype("null|null","!!null"); }
	@Test public void test_2813() { checkNotSubtype("{void f1}|void","any"); }
	@Test public void test_2814() { checkNotSubtype("{void f1}|void","null"); }
	@Test public void test_2815() { checkIsSubtype("{void f1}|void","{void f1}"); }
	@Test public void test_2816() { checkIsSubtype("{void f1}|void","{void f2}"); }
	@Test public void test_2817() { checkNotSubtype("{void f1}|void","{any f1}"); }
	@Test public void test_2818() { checkNotSubtype("{void f1}|void","{any f2}"); }
	@Test public void test_2819() { checkNotSubtype("{void f1}|void","{null f1}"); }
	@Test public void test_2820() { checkNotSubtype("{void f1}|void","{null f2}"); }
	@Test public void test_2821() { checkNotSubtype("{void f1}|void","!void"); }
	@Test public void test_2822() { checkIsSubtype("{void f1}|void","!any"); }
	@Test public void test_2823() { checkNotSubtype("{void f1}|void","!null"); }
	@Test public void test_2824() { checkIsSubtype("{void f1}|void","{{void f1} f1}"); }
	@Test public void test_2825() { checkIsSubtype("{void f1}|void","{{void f2} f1}"); }
	@Test public void test_2826() { checkIsSubtype("{void f1}|void","{{void f1} f2}"); }
	@Test public void test_2827() { checkIsSubtype("{void f1}|void","{{void f2} f2}"); }
	@Test public void test_2828() { checkNotSubtype("{void f1}|void","{{any f1} f1}"); }
	@Test public void test_2829() { checkNotSubtype("{void f1}|void","{{any f2} f1}"); }
	@Test public void test_2830() { checkNotSubtype("{void f1}|void","{{any f1} f2}"); }
	@Test public void test_2831() { checkNotSubtype("{void f1}|void","{{any f2} f2}"); }
	@Test public void test_2832() { checkNotSubtype("{void f1}|void","{{null f1} f1}"); }
	@Test public void test_2833() { checkNotSubtype("{void f1}|void","{{null f2} f1}"); }
	@Test public void test_2834() { checkNotSubtype("{void f1}|void","{{null f1} f2}"); }
	@Test public void test_2835() { checkNotSubtype("{void f1}|void","{{null f2} f2}"); }
	@Test public void test_2836() { checkNotSubtype("{void f1}|void","{!void f1}"); }
	@Test public void test_2837() { checkNotSubtype("{void f1}|void","{!void f2}"); }
	@Test public void test_2838() { checkIsSubtype("{void f1}|void","{!any f1}"); }
	@Test public void test_2839() { checkIsSubtype("{void f1}|void","{!any f2}"); }
	@Test public void test_2840() { checkNotSubtype("{void f1}|void","{!null f1}"); }
	@Test public void test_2841() { checkNotSubtype("{void f1}|void","{!null f2}"); }
	@Test public void test_2842() { checkIsSubtype("{void f1}|void","void|void"); }
	@Test public void test_2843() { checkNotSubtype("{void f1}|void","void|any"); }
	@Test public void test_2844() { checkNotSubtype("{void f1}|void","void|null"); }
	@Test public void test_2845() { checkNotSubtype("{void f1}|void","any|void"); }
	@Test public void test_2846() { checkNotSubtype("{void f1}|void","any|any"); }
	@Test public void test_2847() { checkNotSubtype("{void f1}|void","any|null"); }
	@Test public void test_2848() { checkNotSubtype("{void f1}|void","null|void"); }
	@Test public void test_2849() { checkNotSubtype("{void f1}|void","null|any"); }
	@Test public void test_2850() { checkNotSubtype("{void f1}|void","null|null"); }
	@Test public void test_2851() { checkIsSubtype("{void f1}|void","{void f1}|void"); }
	@Test public void test_2852() { checkIsSubtype("{void f1}|void","{void f2}|void"); }
	@Test public void test_2853() { checkNotSubtype("{void f1}|void","{any f1}|any"); }
	@Test public void test_2854() { checkNotSubtype("{void f1}|void","{any f2}|any"); }
	@Test public void test_2855() { checkNotSubtype("{void f1}|void","{null f1}|null"); }
	@Test public void test_2856() { checkNotSubtype("{void f1}|void","{null f2}|null"); }
	@Test public void test_2857() { checkNotSubtype("{void f1}|void","!void|void"); }
	@Test public void test_2858() { checkNotSubtype("{void f1}|void","!any|any"); }
	@Test public void test_2859() { checkNotSubtype("{void f1}|void","!null|null"); }
	@Test public void test_2860() { checkIsSubtype("{void f1}|void","void&void"); }
	@Test public void test_2861() { checkIsSubtype("{void f1}|void","void&any"); }
	@Test public void test_2862() { checkIsSubtype("{void f1}|void","void&null"); }
	@Test public void test_2863() { checkIsSubtype("{void f1}|void","any&void"); }
	@Test public void test_2864() { checkNotSubtype("{void f1}|void","any&any"); }
	@Test public void test_2865() { checkNotSubtype("{void f1}|void","any&null"); }
	@Test public void test_2866() { checkIsSubtype("{void f1}|void","null&void"); }
	@Test public void test_2867() { checkNotSubtype("{void f1}|void","null&any"); }
	@Test public void test_2868() { checkNotSubtype("{void f1}|void","null&null"); }
	@Test public void test_2869() { checkIsSubtype("{void f1}|void","{void f1}&void"); }
	@Test public void test_2870() { checkIsSubtype("{void f1}|void","{void f2}&void"); }
	@Test public void test_2871() { checkNotSubtype("{void f1}|void","{any f1}&any"); }
	@Test public void test_2872() { checkNotSubtype("{void f1}|void","{any f2}&any"); }
	@Test public void test_2873() { checkIsSubtype("{void f1}|void","{null f1}&null"); }
	@Test public void test_2874() { checkIsSubtype("{void f1}|void","{null f2}&null"); }
	@Test public void test_2875() { checkIsSubtype("{void f1}|void","!void&void"); }
	@Test public void test_2876() { checkIsSubtype("{void f1}|void","!any&any"); }
	@Test public void test_2877() { checkIsSubtype("{void f1}|void","!null&null"); }
	@Test public void test_2878() { checkNotSubtype("{void f1}|void","!{void f1}"); }
	@Test public void test_2879() { checkNotSubtype("{void f1}|void","!{void f2}"); }
	@Test public void test_2880() { checkNotSubtype("{void f1}|void","!{any f1}"); }
	@Test public void test_2881() { checkNotSubtype("{void f1}|void","!{any f2}"); }
	@Test public void test_2882() { checkNotSubtype("{void f1}|void","!{null f1}"); }
	@Test public void test_2883() { checkNotSubtype("{void f1}|void","!{null f2}"); }
	@Test public void test_2884() { checkIsSubtype("{void f1}|void","!!void"); }
	@Test public void test_2885() { checkNotSubtype("{void f1}|void","!!any"); }
	@Test public void test_2886() { checkNotSubtype("{void f1}|void","!!null"); }
	@Test public void test_2887() { checkNotSubtype("{void f2}|void","any"); }
	@Test public void test_2888() { checkNotSubtype("{void f2}|void","null"); }
	@Test public void test_2889() { checkIsSubtype("{void f2}|void","{void f1}"); }
	@Test public void test_2890() { checkIsSubtype("{void f2}|void","{void f2}"); }
	@Test public void test_2891() { checkNotSubtype("{void f2}|void","{any f1}"); }
	@Test public void test_2892() { checkNotSubtype("{void f2}|void","{any f2}"); }
	@Test public void test_2893() { checkNotSubtype("{void f2}|void","{null f1}"); }
	@Test public void test_2894() { checkNotSubtype("{void f2}|void","{null f2}"); }
	@Test public void test_2895() { checkNotSubtype("{void f2}|void","!void"); }
	@Test public void test_2896() { checkIsSubtype("{void f2}|void","!any"); }
	@Test public void test_2897() { checkNotSubtype("{void f2}|void","!null"); }
	@Test public void test_2898() { checkIsSubtype("{void f2}|void","{{void f1} f1}"); }
	@Test public void test_2899() { checkIsSubtype("{void f2}|void","{{void f2} f1}"); }
	@Test public void test_2900() { checkIsSubtype("{void f2}|void","{{void f1} f2}"); }
	@Test public void test_2901() { checkIsSubtype("{void f2}|void","{{void f2} f2}"); }
	@Test public void test_2902() { checkNotSubtype("{void f2}|void","{{any f1} f1}"); }
	@Test public void test_2903() { checkNotSubtype("{void f2}|void","{{any f2} f1}"); }
	@Test public void test_2904() { checkNotSubtype("{void f2}|void","{{any f1} f2}"); }
	@Test public void test_2905() { checkNotSubtype("{void f2}|void","{{any f2} f2}"); }
	@Test public void test_2906() { checkNotSubtype("{void f2}|void","{{null f1} f1}"); }
	@Test public void test_2907() { checkNotSubtype("{void f2}|void","{{null f2} f1}"); }
	@Test public void test_2908() { checkNotSubtype("{void f2}|void","{{null f1} f2}"); }
	@Test public void test_2909() { checkNotSubtype("{void f2}|void","{{null f2} f2}"); }
	@Test public void test_2910() { checkNotSubtype("{void f2}|void","{!void f1}"); }
	@Test public void test_2911() { checkNotSubtype("{void f2}|void","{!void f2}"); }
	@Test public void test_2912() { checkIsSubtype("{void f2}|void","{!any f1}"); }
	@Test public void test_2913() { checkIsSubtype("{void f2}|void","{!any f2}"); }
	@Test public void test_2914() { checkNotSubtype("{void f2}|void","{!null f1}"); }
	@Test public void test_2915() { checkNotSubtype("{void f2}|void","{!null f2}"); }
	@Test public void test_2916() { checkIsSubtype("{void f2}|void","void|void"); }
	@Test public void test_2917() { checkNotSubtype("{void f2}|void","void|any"); }
	@Test public void test_2918() { checkNotSubtype("{void f2}|void","void|null"); }
	@Test public void test_2919() { checkNotSubtype("{void f2}|void","any|void"); }
	@Test public void test_2920() { checkNotSubtype("{void f2}|void","any|any"); }
	@Test public void test_2921() { checkNotSubtype("{void f2}|void","any|null"); }
	@Test public void test_2922() { checkNotSubtype("{void f2}|void","null|void"); }
	@Test public void test_2923() { checkNotSubtype("{void f2}|void","null|any"); }
	@Test public void test_2924() { checkNotSubtype("{void f2}|void","null|null"); }
	@Test public void test_2925() { checkIsSubtype("{void f2}|void","{void f1}|void"); }
	@Test public void test_2926() { checkIsSubtype("{void f2}|void","{void f2}|void"); }
	@Test public void test_2927() { checkNotSubtype("{void f2}|void","{any f1}|any"); }
	@Test public void test_2928() { checkNotSubtype("{void f2}|void","{any f2}|any"); }
	@Test public void test_2929() { checkNotSubtype("{void f2}|void","{null f1}|null"); }
	@Test public void test_2930() { checkNotSubtype("{void f2}|void","{null f2}|null"); }
	@Test public void test_2931() { checkNotSubtype("{void f2}|void","!void|void"); }
	@Test public void test_2932() { checkNotSubtype("{void f2}|void","!any|any"); }
	@Test public void test_2933() { checkNotSubtype("{void f2}|void","!null|null"); }
	@Test public void test_2934() { checkIsSubtype("{void f2}|void","void&void"); }
	@Test public void test_2935() { checkIsSubtype("{void f2}|void","void&any"); }
	@Test public void test_2936() { checkIsSubtype("{void f2}|void","void&null"); }
	@Test public void test_2937() { checkIsSubtype("{void f2}|void","any&void"); }
	@Test public void test_2938() { checkNotSubtype("{void f2}|void","any&any"); }
	@Test public void test_2939() { checkNotSubtype("{void f2}|void","any&null"); }
	@Test public void test_2940() { checkIsSubtype("{void f2}|void","null&void"); }
	@Test public void test_2941() { checkNotSubtype("{void f2}|void","null&any"); }
	@Test public void test_2942() { checkNotSubtype("{void f2}|void","null&null"); }
	@Test public void test_2943() { checkIsSubtype("{void f2}|void","{void f1}&void"); }
	@Test public void test_2944() { checkIsSubtype("{void f2}|void","{void f2}&void"); }
	@Test public void test_2945() { checkNotSubtype("{void f2}|void","{any f1}&any"); }
	@Test public void test_2946() { checkNotSubtype("{void f2}|void","{any f2}&any"); }
	@Test public void test_2947() { checkIsSubtype("{void f2}|void","{null f1}&null"); }
	@Test public void test_2948() { checkIsSubtype("{void f2}|void","{null f2}&null"); }
	@Test public void test_2949() { checkIsSubtype("{void f2}|void","!void&void"); }
	@Test public void test_2950() { checkIsSubtype("{void f2}|void","!any&any"); }
	@Test public void test_2951() { checkIsSubtype("{void f2}|void","!null&null"); }
	@Test public void test_2952() { checkNotSubtype("{void f2}|void","!{void f1}"); }
	@Test public void test_2953() { checkNotSubtype("{void f2}|void","!{void f2}"); }
	@Test public void test_2954() { checkNotSubtype("{void f2}|void","!{any f1}"); }
	@Test public void test_2955() { checkNotSubtype("{void f2}|void","!{any f2}"); }
	@Test public void test_2956() { checkNotSubtype("{void f2}|void","!{null f1}"); }
	@Test public void test_2957() { checkNotSubtype("{void f2}|void","!{null f2}"); }
	@Test public void test_2958() { checkIsSubtype("{void f2}|void","!!void"); }
	@Test public void test_2959() { checkNotSubtype("{void f2}|void","!!any"); }
	@Test public void test_2960() { checkNotSubtype("{void f2}|void","!!null"); }
	@Test public void test_2961() { checkIsSubtype("{any f1}|any","any"); }
	@Test public void test_2962() { checkIsSubtype("{any f1}|any","null"); }
	@Test public void test_2963() { checkIsSubtype("{any f1}|any","{void f1}"); }
	@Test public void test_2964() { checkIsSubtype("{any f1}|any","{void f2}"); }
	@Test public void test_2965() { checkIsSubtype("{any f1}|any","{any f1}"); }
	@Test public void test_2966() { checkIsSubtype("{any f1}|any","{any f2}"); }
	@Test public void test_2967() { checkIsSubtype("{any f1}|any","{null f1}"); }
	@Test public void test_2968() { checkIsSubtype("{any f1}|any","{null f2}"); }
	@Test public void test_2969() { checkIsSubtype("{any f1}|any","!void"); }
	@Test public void test_2970() { checkIsSubtype("{any f1}|any","!any"); }
	@Test public void test_2971() { checkIsSubtype("{any f1}|any","!null"); }
	@Test public void test_2972() { checkIsSubtype("{any f1}|any","{{void f1} f1}"); }
	@Test public void test_2973() { checkIsSubtype("{any f1}|any","{{void f2} f1}"); }
	@Test public void test_2974() { checkIsSubtype("{any f1}|any","{{void f1} f2}"); }
	@Test public void test_2975() { checkIsSubtype("{any f1}|any","{{void f2} f2}"); }
	@Test public void test_2976() { checkIsSubtype("{any f1}|any","{{any f1} f1}"); }
	@Test public void test_2977() { checkIsSubtype("{any f1}|any","{{any f2} f1}"); }
	@Test public void test_2978() { checkIsSubtype("{any f1}|any","{{any f1} f2}"); }
	@Test public void test_2979() { checkIsSubtype("{any f1}|any","{{any f2} f2}"); }
	@Test public void test_2980() { checkIsSubtype("{any f1}|any","{{null f1} f1}"); }
	@Test public void test_2981() { checkIsSubtype("{any f1}|any","{{null f2} f1}"); }
	@Test public void test_2982() { checkIsSubtype("{any f1}|any","{{null f1} f2}"); }
	@Test public void test_2983() { checkIsSubtype("{any f1}|any","{{null f2} f2}"); }
	@Test public void test_2984() { checkIsSubtype("{any f1}|any","{!void f1}"); }
	@Test public void test_2985() { checkIsSubtype("{any f1}|any","{!void f2}"); }
	@Test public void test_2986() { checkIsSubtype("{any f1}|any","{!any f1}"); }
	@Test public void test_2987() { checkIsSubtype("{any f1}|any","{!any f2}"); }
	@Test public void test_2988() { checkIsSubtype("{any f1}|any","{!null f1}"); }
	@Test public void test_2989() { checkIsSubtype("{any f1}|any","{!null f2}"); }
	@Test public void test_2990() { checkIsSubtype("{any f1}|any","void|void"); }
	@Test public void test_2991() { checkIsSubtype("{any f1}|any","void|any"); }
	@Test public void test_2992() { checkIsSubtype("{any f1}|any","void|null"); }
	@Test public void test_2993() { checkIsSubtype("{any f1}|any","any|void"); }
	@Test public void test_2994() { checkIsSubtype("{any f1}|any","any|any"); }
	@Test public void test_2995() { checkIsSubtype("{any f1}|any","any|null"); }
	@Test public void test_2996() { checkIsSubtype("{any f1}|any","null|void"); }
	@Test public void test_2997() { checkIsSubtype("{any f1}|any","null|any"); }
	@Test public void test_2998() { checkIsSubtype("{any f1}|any","null|null"); }
	@Test public void test_2999() { checkIsSubtype("{any f1}|any","{void f1}|void"); }
	@Test public void test_3000() { checkIsSubtype("{any f1}|any","{void f2}|void"); }
	@Test public void test_3001() { checkIsSubtype("{any f1}|any","{any f1}|any"); }
	@Test public void test_3002() { checkIsSubtype("{any f1}|any","{any f2}|any"); }
	@Test public void test_3003() { checkIsSubtype("{any f1}|any","{null f1}|null"); }
	@Test public void test_3004() { checkIsSubtype("{any f1}|any","{null f2}|null"); }
	@Test public void test_3005() { checkIsSubtype("{any f1}|any","!void|void"); }
	@Test public void test_3006() { checkIsSubtype("{any f1}|any","!any|any"); }
	@Test public void test_3007() { checkIsSubtype("{any f1}|any","!null|null"); }
	@Test public void test_3008() { checkIsSubtype("{any f1}|any","void&void"); }
	@Test public void test_3009() { checkIsSubtype("{any f1}|any","void&any"); }
	@Test public void test_3010() { checkIsSubtype("{any f1}|any","void&null"); }
	@Test public void test_3011() { checkIsSubtype("{any f1}|any","any&void"); }
	@Test public void test_3012() { checkIsSubtype("{any f1}|any","any&any"); }
	@Test public void test_3013() { checkIsSubtype("{any f1}|any","any&null"); }
	@Test public void test_3014() { checkIsSubtype("{any f1}|any","null&void"); }
	@Test public void test_3015() { checkIsSubtype("{any f1}|any","null&any"); }
	@Test public void test_3016() { checkIsSubtype("{any f1}|any","null&null"); }
	@Test public void test_3017() { checkIsSubtype("{any f1}|any","{void f1}&void"); }
	@Test public void test_3018() { checkIsSubtype("{any f1}|any","{void f2}&void"); }
	@Test public void test_3019() { checkIsSubtype("{any f1}|any","{any f1}&any"); }
	@Test public void test_3020() { checkIsSubtype("{any f1}|any","{any f2}&any"); }
	@Test public void test_3021() { checkIsSubtype("{any f1}|any","{null f1}&null"); }
	@Test public void test_3022() { checkIsSubtype("{any f1}|any","{null f2}&null"); }
	@Test public void test_3023() { checkIsSubtype("{any f1}|any","!void&void"); }
	@Test public void test_3024() { checkIsSubtype("{any f1}|any","!any&any"); }
	@Test public void test_3025() { checkIsSubtype("{any f1}|any","!null&null"); }
	@Test public void test_3026() { checkIsSubtype("{any f1}|any","!{void f1}"); }
	@Test public void test_3027() { checkIsSubtype("{any f1}|any","!{void f2}"); }
	@Test public void test_3028() { checkIsSubtype("{any f1}|any","!{any f1}"); }
	@Test public void test_3029() { checkIsSubtype("{any f1}|any","!{any f2}"); }
	@Test public void test_3030() { checkIsSubtype("{any f1}|any","!{null f1}"); }
	@Test public void test_3031() { checkIsSubtype("{any f1}|any","!{null f2}"); }
	@Test public void test_3032() { checkIsSubtype("{any f1}|any","!!void"); }
	@Test public void test_3033() { checkIsSubtype("{any f1}|any","!!any"); }
	@Test public void test_3034() { checkIsSubtype("{any f1}|any","!!null"); }
	@Test public void test_3035() { checkIsSubtype("{any f2}|any","any"); }
	@Test public void test_3036() { checkIsSubtype("{any f2}|any","null"); }
	@Test public void test_3037() { checkIsSubtype("{any f2}|any","{void f1}"); }
	@Test public void test_3038() { checkIsSubtype("{any f2}|any","{void f2}"); }
	@Test public void test_3039() { checkIsSubtype("{any f2}|any","{any f1}"); }
	@Test public void test_3040() { checkIsSubtype("{any f2}|any","{any f2}"); }
	@Test public void test_3041() { checkIsSubtype("{any f2}|any","{null f1}"); }
	@Test public void test_3042() { checkIsSubtype("{any f2}|any","{null f2}"); }
	@Test public void test_3043() { checkIsSubtype("{any f2}|any","!void"); }
	@Test public void test_3044() { checkIsSubtype("{any f2}|any","!any"); }
	@Test public void test_3045() { checkIsSubtype("{any f2}|any","!null"); }
	@Test public void test_3046() { checkIsSubtype("{any f2}|any","{{void f1} f1}"); }
	@Test public void test_3047() { checkIsSubtype("{any f2}|any","{{void f2} f1}"); }
	@Test public void test_3048() { checkIsSubtype("{any f2}|any","{{void f1} f2}"); }
	@Test public void test_3049() { checkIsSubtype("{any f2}|any","{{void f2} f2}"); }
	@Test public void test_3050() { checkIsSubtype("{any f2}|any","{{any f1} f1}"); }
	@Test public void test_3051() { checkIsSubtype("{any f2}|any","{{any f2} f1}"); }
	@Test public void test_3052() { checkIsSubtype("{any f2}|any","{{any f1} f2}"); }
	@Test public void test_3053() { checkIsSubtype("{any f2}|any","{{any f2} f2}"); }
	@Test public void test_3054() { checkIsSubtype("{any f2}|any","{{null f1} f1}"); }
	@Test public void test_3055() { checkIsSubtype("{any f2}|any","{{null f2} f1}"); }
	@Test public void test_3056() { checkIsSubtype("{any f2}|any","{{null f1} f2}"); }
	@Test public void test_3057() { checkIsSubtype("{any f2}|any","{{null f2} f2}"); }
	@Test public void test_3058() { checkIsSubtype("{any f2}|any","{!void f1}"); }
	@Test public void test_3059() { checkIsSubtype("{any f2}|any","{!void f2}"); }
	@Test public void test_3060() { checkIsSubtype("{any f2}|any","{!any f1}"); }
	@Test public void test_3061() { checkIsSubtype("{any f2}|any","{!any f2}"); }
	@Test public void test_3062() { checkIsSubtype("{any f2}|any","{!null f1}"); }
	@Test public void test_3063() { checkIsSubtype("{any f2}|any","{!null f2}"); }
	@Test public void test_3064() { checkIsSubtype("{any f2}|any","void|void"); }
	@Test public void test_3065() { checkIsSubtype("{any f2}|any","void|any"); }
	@Test public void test_3066() { checkIsSubtype("{any f2}|any","void|null"); }
	@Test public void test_3067() { checkIsSubtype("{any f2}|any","any|void"); }
	@Test public void test_3068() { checkIsSubtype("{any f2}|any","any|any"); }
	@Test public void test_3069() { checkIsSubtype("{any f2}|any","any|null"); }
	@Test public void test_3070() { checkIsSubtype("{any f2}|any","null|void"); }
	@Test public void test_3071() { checkIsSubtype("{any f2}|any","null|any"); }
	@Test public void test_3072() { checkIsSubtype("{any f2}|any","null|null"); }
	@Test public void test_3073() { checkIsSubtype("{any f2}|any","{void f1}|void"); }
	@Test public void test_3074() { checkIsSubtype("{any f2}|any","{void f2}|void"); }
	@Test public void test_3075() { checkIsSubtype("{any f2}|any","{any f1}|any"); }
	@Test public void test_3076() { checkIsSubtype("{any f2}|any","{any f2}|any"); }
	@Test public void test_3077() { checkIsSubtype("{any f2}|any","{null f1}|null"); }
	@Test public void test_3078() { checkIsSubtype("{any f2}|any","{null f2}|null"); }
	@Test public void test_3079() { checkIsSubtype("{any f2}|any","!void|void"); }
	@Test public void test_3080() { checkIsSubtype("{any f2}|any","!any|any"); }
	@Test public void test_3081() { checkIsSubtype("{any f2}|any","!null|null"); }
	@Test public void test_3082() { checkIsSubtype("{any f2}|any","void&void"); }
	@Test public void test_3083() { checkIsSubtype("{any f2}|any","void&any"); }
	@Test public void test_3084() { checkIsSubtype("{any f2}|any","void&null"); }
	@Test public void test_3085() { checkIsSubtype("{any f2}|any","any&void"); }
	@Test public void test_3086() { checkIsSubtype("{any f2}|any","any&any"); }
	@Test public void test_3087() { checkIsSubtype("{any f2}|any","any&null"); }
	@Test public void test_3088() { checkIsSubtype("{any f2}|any","null&void"); }
	@Test public void test_3089() { checkIsSubtype("{any f2}|any","null&any"); }
	@Test public void test_3090() { checkIsSubtype("{any f2}|any","null&null"); }
	@Test public void test_3091() { checkIsSubtype("{any f2}|any","{void f1}&void"); }
	@Test public void test_3092() { checkIsSubtype("{any f2}|any","{void f2}&void"); }
	@Test public void test_3093() { checkIsSubtype("{any f2}|any","{any f1}&any"); }
	@Test public void test_3094() { checkIsSubtype("{any f2}|any","{any f2}&any"); }
	@Test public void test_3095() { checkIsSubtype("{any f2}|any","{null f1}&null"); }
	@Test public void test_3096() { checkIsSubtype("{any f2}|any","{null f2}&null"); }
	@Test public void test_3097() { checkIsSubtype("{any f2}|any","!void&void"); }
	@Test public void test_3098() { checkIsSubtype("{any f2}|any","!any&any"); }
	@Test public void test_3099() { checkIsSubtype("{any f2}|any","!null&null"); }
	@Test public void test_3100() { checkIsSubtype("{any f2}|any","!{void f1}"); }
	@Test public void test_3101() { checkIsSubtype("{any f2}|any","!{void f2}"); }
	@Test public void test_3102() { checkIsSubtype("{any f2}|any","!{any f1}"); }
	@Test public void test_3103() { checkIsSubtype("{any f2}|any","!{any f2}"); }
	@Test public void test_3104() { checkIsSubtype("{any f2}|any","!{null f1}"); }
	@Test public void test_3105() { checkIsSubtype("{any f2}|any","!{null f2}"); }
	@Test public void test_3106() { checkIsSubtype("{any f2}|any","!!void"); }
	@Test public void test_3107() { checkIsSubtype("{any f2}|any","!!any"); }
	@Test public void test_3108() { checkIsSubtype("{any f2}|any","!!null"); }
	@Test public void test_3109() { checkNotSubtype("{null f1}|null","any"); }
	@Test public void test_3110() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_3111() { checkIsSubtype("{null f1}|null","{void f1}"); }
	@Test public void test_3112() { checkIsSubtype("{null f1}|null","{void f2}"); }
	@Test public void test_3113() { checkNotSubtype("{null f1}|null","{any f1}"); }
	@Test public void test_3114() { checkNotSubtype("{null f1}|null","{any f2}"); }
	@Test public void test_3115() { checkIsSubtype("{null f1}|null","{null f1}"); }
	@Test public void test_3116() { checkNotSubtype("{null f1}|null","{null f2}"); }
	@Test public void test_3117() { checkNotSubtype("{null f1}|null","!void"); }
	@Test public void test_3118() { checkIsSubtype("{null f1}|null","!any"); }
	@Test public void test_3119() { checkNotSubtype("{null f1}|null","!null"); }
	@Test public void test_3120() { checkIsSubtype("{null f1}|null","{{void f1} f1}"); }
	@Test public void test_3121() { checkIsSubtype("{null f1}|null","{{void f2} f1}"); }
	@Test public void test_3122() { checkIsSubtype("{null f1}|null","{{void f1} f2}"); }
	@Test public void test_3123() { checkIsSubtype("{null f1}|null","{{void f2} f2}"); }
	@Test public void test_3124() { checkNotSubtype("{null f1}|null","{{any f1} f1}"); }
	@Test public void test_3125() { checkNotSubtype("{null f1}|null","{{any f2} f1}"); }
	@Test public void test_3126() { checkNotSubtype("{null f1}|null","{{any f1} f2}"); }
	@Test public void test_3127() { checkNotSubtype("{null f1}|null","{{any f2} f2}"); }
	@Test public void test_3128() { checkNotSubtype("{null f1}|null","{{null f1} f1}"); }
	@Test public void test_3129() { checkNotSubtype("{null f1}|null","{{null f2} f1}"); }
	@Test public void test_3130() { checkNotSubtype("{null f1}|null","{{null f1} f2}"); }
	@Test public void test_3131() { checkNotSubtype("{null f1}|null","{{null f2} f2}"); }
	@Test public void test_3132() { checkNotSubtype("{null f1}|null","{!void f1}"); }
	@Test public void test_3133() { checkNotSubtype("{null f1}|null","{!void f2}"); }
	@Test public void test_3134() { checkIsSubtype("{null f1}|null","{!any f1}"); }
	@Test public void test_3135() { checkIsSubtype("{null f1}|null","{!any f2}"); }
	@Test public void test_3136() { checkNotSubtype("{null f1}|null","{!null f1}"); }
	@Test public void test_3137() { checkNotSubtype("{null f1}|null","{!null f2}"); }
	@Test public void test_3138() { checkIsSubtype("{null f1}|null","void|void"); }
	@Test public void test_3139() { checkNotSubtype("{null f1}|null","void|any"); }
	@Test public void test_3140() { checkIsSubtype("{null f1}|null","void|null"); }
	@Test public void test_3141() { checkNotSubtype("{null f1}|null","any|void"); }
	@Test public void test_3142() { checkNotSubtype("{null f1}|null","any|any"); }
	@Test public void test_3143() { checkNotSubtype("{null f1}|null","any|null"); }
	@Test public void test_3144() { checkIsSubtype("{null f1}|null","null|void"); }
	@Test public void test_3145() { checkNotSubtype("{null f1}|null","null|any"); }
	@Test public void test_3146() { checkIsSubtype("{null f1}|null","null|null"); }
	@Test public void test_3147() { checkIsSubtype("{null f1}|null","{void f1}|void"); }
	@Test public void test_3148() { checkIsSubtype("{null f1}|null","{void f2}|void"); }
	@Test public void test_3149() { checkNotSubtype("{null f1}|null","{any f1}|any"); }
	@Test public void test_3150() { checkNotSubtype("{null f1}|null","{any f2}|any"); }
	@Test public void test_3151() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test public void test_3152() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test public void test_3153() { checkNotSubtype("{null f1}|null","!void|void"); }
	@Test public void test_3154() { checkNotSubtype("{null f1}|null","!any|any"); }
	@Test public void test_3155() { checkNotSubtype("{null f1}|null","!null|null"); }
	@Test public void test_3156() { checkIsSubtype("{null f1}|null","void&void"); }
	@Test public void test_3157() { checkIsSubtype("{null f1}|null","void&any"); }
	@Test public void test_3158() { checkIsSubtype("{null f1}|null","void&null"); }
	@Test public void test_3159() { checkIsSubtype("{null f1}|null","any&void"); }
	@Test public void test_3160() { checkNotSubtype("{null f1}|null","any&any"); }
	@Test public void test_3161() { checkIsSubtype("{null f1}|null","any&null"); }
	@Test public void test_3162() { checkIsSubtype("{null f1}|null","null&void"); }
	@Test public void test_3163() { checkIsSubtype("{null f1}|null","null&any"); }
	@Test public void test_3164() { checkIsSubtype("{null f1}|null","null&null"); }
	@Test public void test_3165() { checkIsSubtype("{null f1}|null","{void f1}&void"); }
	@Test public void test_3166() { checkIsSubtype("{null f1}|null","{void f2}&void"); }
	@Test public void test_3167() { checkNotSubtype("{null f1}|null","{any f1}&any"); }
	@Test public void test_3168() { checkNotSubtype("{null f1}|null","{any f2}&any"); }
	@Test public void test_3169() { checkIsSubtype("{null f1}|null","{null f1}&null"); }
	@Test public void test_3170() { checkIsSubtype("{null f1}|null","{null f2}&null"); }
	@Test public void test_3171() { checkIsSubtype("{null f1}|null","!void&void"); }
	@Test public void test_3172() { checkIsSubtype("{null f1}|null","!any&any"); }
	@Test public void test_3173() { checkIsSubtype("{null f1}|null","!null&null"); }
	@Test public void test_3174() { checkNotSubtype("{null f1}|null","!{void f1}"); }
	@Test public void test_3175() { checkNotSubtype("{null f1}|null","!{void f2}"); }
	@Test public void test_3176() { checkNotSubtype("{null f1}|null","!{any f1}"); }
	@Test public void test_3177() { checkNotSubtype("{null f1}|null","!{any f2}"); }
	@Test public void test_3178() { checkNotSubtype("{null f1}|null","!{null f1}"); }
	@Test public void test_3179() { checkNotSubtype("{null f1}|null","!{null f2}"); }
	@Test public void test_3180() { checkIsSubtype("{null f1}|null","!!void"); }
	@Test public void test_3181() { checkNotSubtype("{null f1}|null","!!any"); }
	@Test public void test_3182() { checkIsSubtype("{null f1}|null","!!null"); }
	@Test public void test_3183() { checkNotSubtype("{null f2}|null","any"); }
	@Test public void test_3184() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_3185() { checkIsSubtype("{null f2}|null","{void f1}"); }
	@Test public void test_3186() { checkIsSubtype("{null f2}|null","{void f2}"); }
	@Test public void test_3187() { checkNotSubtype("{null f2}|null","{any f1}"); }
	@Test public void test_3188() { checkNotSubtype("{null f2}|null","{any f2}"); }
	@Test public void test_3189() { checkNotSubtype("{null f2}|null","{null f1}"); }
	@Test public void test_3190() { checkIsSubtype("{null f2}|null","{null f2}"); }
	@Test public void test_3191() { checkNotSubtype("{null f2}|null","!void"); }
	@Test public void test_3192() { checkIsSubtype("{null f2}|null","!any"); }
	@Test public void test_3193() { checkNotSubtype("{null f2}|null","!null"); }
	@Test public void test_3194() { checkIsSubtype("{null f2}|null","{{void f1} f1}"); }
	@Test public void test_3195() { checkIsSubtype("{null f2}|null","{{void f2} f1}"); }
	@Test public void test_3196() { checkIsSubtype("{null f2}|null","{{void f1} f2}"); }
	@Test public void test_3197() { checkIsSubtype("{null f2}|null","{{void f2} f2}"); }
	@Test public void test_3198() { checkNotSubtype("{null f2}|null","{{any f1} f1}"); }
	@Test public void test_3199() { checkNotSubtype("{null f2}|null","{{any f2} f1}"); }
	@Test public void test_3200() { checkNotSubtype("{null f2}|null","{{any f1} f2}"); }
	@Test public void test_3201() { checkNotSubtype("{null f2}|null","{{any f2} f2}"); }
	@Test public void test_3202() { checkNotSubtype("{null f2}|null","{{null f1} f1}"); }
	@Test public void test_3203() { checkNotSubtype("{null f2}|null","{{null f2} f1}"); }
	@Test public void test_3204() { checkNotSubtype("{null f2}|null","{{null f1} f2}"); }
	@Test public void test_3205() { checkNotSubtype("{null f2}|null","{{null f2} f2}"); }
	@Test public void test_3206() { checkNotSubtype("{null f2}|null","{!void f1}"); }
	@Test public void test_3207() { checkNotSubtype("{null f2}|null","{!void f2}"); }
	@Test public void test_3208() { checkIsSubtype("{null f2}|null","{!any f1}"); }
	@Test public void test_3209() { checkIsSubtype("{null f2}|null","{!any f2}"); }
	@Test public void test_3210() { checkNotSubtype("{null f2}|null","{!null f1}"); }
	@Test public void test_3211() { checkNotSubtype("{null f2}|null","{!null f2}"); }
	@Test public void test_3212() { checkIsSubtype("{null f2}|null","void|void"); }
	@Test public void test_3213() { checkNotSubtype("{null f2}|null","void|any"); }
	@Test public void test_3214() { checkIsSubtype("{null f2}|null","void|null"); }
	@Test public void test_3215() { checkNotSubtype("{null f2}|null","any|void"); }
	@Test public void test_3216() { checkNotSubtype("{null f2}|null","any|any"); }
	@Test public void test_3217() { checkNotSubtype("{null f2}|null","any|null"); }
	@Test public void test_3218() { checkIsSubtype("{null f2}|null","null|void"); }
	@Test public void test_3219() { checkNotSubtype("{null f2}|null","null|any"); }
	@Test public void test_3220() { checkIsSubtype("{null f2}|null","null|null"); }
	@Test public void test_3221() { checkIsSubtype("{null f2}|null","{void f1}|void"); }
	@Test public void test_3222() { checkIsSubtype("{null f2}|null","{void f2}|void"); }
	@Test public void test_3223() { checkNotSubtype("{null f2}|null","{any f1}|any"); }
	@Test public void test_3224() { checkNotSubtype("{null f2}|null","{any f2}|any"); }
	@Test public void test_3225() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test public void test_3226() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test public void test_3227() { checkNotSubtype("{null f2}|null","!void|void"); }
	@Test public void test_3228() { checkNotSubtype("{null f2}|null","!any|any"); }
	@Test public void test_3229() { checkNotSubtype("{null f2}|null","!null|null"); }
	@Test public void test_3230() { checkIsSubtype("{null f2}|null","void&void"); }
	@Test public void test_3231() { checkIsSubtype("{null f2}|null","void&any"); }
	@Test public void test_3232() { checkIsSubtype("{null f2}|null","void&null"); }
	@Test public void test_3233() { checkIsSubtype("{null f2}|null","any&void"); }
	@Test public void test_3234() { checkNotSubtype("{null f2}|null","any&any"); }
	@Test public void test_3235() { checkIsSubtype("{null f2}|null","any&null"); }
	@Test public void test_3236() { checkIsSubtype("{null f2}|null","null&void"); }
	@Test public void test_3237() { checkIsSubtype("{null f2}|null","null&any"); }
	@Test public void test_3238() { checkIsSubtype("{null f2}|null","null&null"); }
	@Test public void test_3239() { checkIsSubtype("{null f2}|null","{void f1}&void"); }
	@Test public void test_3240() { checkIsSubtype("{null f2}|null","{void f2}&void"); }
	@Test public void test_3241() { checkNotSubtype("{null f2}|null","{any f1}&any"); }
	@Test public void test_3242() { checkNotSubtype("{null f2}|null","{any f2}&any"); }
	@Test public void test_3243() { checkIsSubtype("{null f2}|null","{null f1}&null"); }
	@Test public void test_3244() { checkIsSubtype("{null f2}|null","{null f2}&null"); }
	@Test public void test_3245() { checkIsSubtype("{null f2}|null","!void&void"); }
	@Test public void test_3246() { checkIsSubtype("{null f2}|null","!any&any"); }
	@Test public void test_3247() { checkIsSubtype("{null f2}|null","!null&null"); }
	@Test public void test_3248() { checkNotSubtype("{null f2}|null","!{void f1}"); }
	@Test public void test_3249() { checkNotSubtype("{null f2}|null","!{void f2}"); }
	@Test public void test_3250() { checkNotSubtype("{null f2}|null","!{any f1}"); }
	@Test public void test_3251() { checkNotSubtype("{null f2}|null","!{any f2}"); }
	@Test public void test_3252() { checkNotSubtype("{null f2}|null","!{null f1}"); }
	@Test public void test_3253() { checkNotSubtype("{null f2}|null","!{null f2}"); }
	@Test public void test_3254() { checkIsSubtype("{null f2}|null","!!void"); }
	@Test public void test_3255() { checkNotSubtype("{null f2}|null","!!any"); }
	@Test public void test_3256() { checkIsSubtype("{null f2}|null","!!null"); }
	@Test public void test_3257() { checkIsSubtype("!void|void","any"); }
	@Test public void test_3258() { checkIsSubtype("!void|void","null"); }
	@Test public void test_3259() { checkIsSubtype("!void|void","{void f1}"); }
	@Test public void test_3260() { checkIsSubtype("!void|void","{void f2}"); }
	@Test public void test_3261() { checkIsSubtype("!void|void","{any f1}"); }
	@Test public void test_3262() { checkIsSubtype("!void|void","{any f2}"); }
	@Test public void test_3263() { checkIsSubtype("!void|void","{null f1}"); }
	@Test public void test_3264() { checkIsSubtype("!void|void","{null f2}"); }
	@Test public void test_3265() { checkIsSubtype("!void|void","!void"); }
	@Test public void test_3266() { checkIsSubtype("!void|void","!any"); }
	@Test public void test_3267() { checkIsSubtype("!void|void","!null"); }
	@Test public void test_3268() { checkIsSubtype("!void|void","{{void f1} f1}"); }
	@Test public void test_3269() { checkIsSubtype("!void|void","{{void f2} f1}"); }
	@Test public void test_3270() { checkIsSubtype("!void|void","{{void f1} f2}"); }
	@Test public void test_3271() { checkIsSubtype("!void|void","{{void f2} f2}"); }
	@Test public void test_3272() { checkIsSubtype("!void|void","{{any f1} f1}"); }
	@Test public void test_3273() { checkIsSubtype("!void|void","{{any f2} f1}"); }
	@Test public void test_3274() { checkIsSubtype("!void|void","{{any f1} f2}"); }
	@Test public void test_3275() { checkIsSubtype("!void|void","{{any f2} f2}"); }
	@Test public void test_3276() { checkIsSubtype("!void|void","{{null f1} f1}"); }
	@Test public void test_3277() { checkIsSubtype("!void|void","{{null f2} f1}"); }
	@Test public void test_3278() { checkIsSubtype("!void|void","{{null f1} f2}"); }
	@Test public void test_3279() { checkIsSubtype("!void|void","{{null f2} f2}"); }
	@Test public void test_3280() { checkIsSubtype("!void|void","{!void f1}"); }
	@Test public void test_3281() { checkIsSubtype("!void|void","{!void f2}"); }
	@Test public void test_3282() { checkIsSubtype("!void|void","{!any f1}"); }
	@Test public void test_3283() { checkIsSubtype("!void|void","{!any f2}"); }
	@Test public void test_3284() { checkIsSubtype("!void|void","{!null f1}"); }
	@Test public void test_3285() { checkIsSubtype("!void|void","{!null f2}"); }
	@Test public void test_3286() { checkIsSubtype("!void|void","void|void"); }
	@Test public void test_3287() { checkIsSubtype("!void|void","void|any"); }
	@Test public void test_3288() { checkIsSubtype("!void|void","void|null"); }
	@Test public void test_3289() { checkIsSubtype("!void|void","any|void"); }
	@Test public void test_3290() { checkIsSubtype("!void|void","any|any"); }
	@Test public void test_3291() { checkIsSubtype("!void|void","any|null"); }
	@Test public void test_3292() { checkIsSubtype("!void|void","null|void"); }
	@Test public void test_3293() { checkIsSubtype("!void|void","null|any"); }
	@Test public void test_3294() { checkIsSubtype("!void|void","null|null"); }
	@Test public void test_3295() { checkIsSubtype("!void|void","{void f1}|void"); }
	@Test public void test_3296() { checkIsSubtype("!void|void","{void f2}|void"); }
	@Test public void test_3297() { checkIsSubtype("!void|void","{any f1}|any"); }
	@Test public void test_3298() { checkIsSubtype("!void|void","{any f2}|any"); }
	@Test public void test_3299() { checkIsSubtype("!void|void","{null f1}|null"); }
	@Test public void test_3300() { checkIsSubtype("!void|void","{null f2}|null"); }
	@Test public void test_3301() { checkIsSubtype("!void|void","!void|void"); }
	@Test public void test_3302() { checkIsSubtype("!void|void","!any|any"); }
	@Test public void test_3303() { checkIsSubtype("!void|void","!null|null"); }
	@Test public void test_3304() { checkIsSubtype("!void|void","void&void"); }
	@Test public void test_3305() { checkIsSubtype("!void|void","void&any"); }
	@Test public void test_3306() { checkIsSubtype("!void|void","void&null"); }
	@Test public void test_3307() { checkIsSubtype("!void|void","any&void"); }
	@Test public void test_3308() { checkIsSubtype("!void|void","any&any"); }
	@Test public void test_3309() { checkIsSubtype("!void|void","any&null"); }
	@Test public void test_3310() { checkIsSubtype("!void|void","null&void"); }
	@Test public void test_3311() { checkIsSubtype("!void|void","null&any"); }
	@Test public void test_3312() { checkIsSubtype("!void|void","null&null"); }
	@Test public void test_3313() { checkIsSubtype("!void|void","{void f1}&void"); }
	@Test public void test_3314() { checkIsSubtype("!void|void","{void f2}&void"); }
	@Test public void test_3315() { checkIsSubtype("!void|void","{any f1}&any"); }
	@Test public void test_3316() { checkIsSubtype("!void|void","{any f2}&any"); }
	@Test public void test_3317() { checkIsSubtype("!void|void","{null f1}&null"); }
	@Test public void test_3318() { checkIsSubtype("!void|void","{null f2}&null"); }
	@Test public void test_3319() { checkIsSubtype("!void|void","!void&void"); }
	@Test public void test_3320() { checkIsSubtype("!void|void","!any&any"); }
	@Test public void test_3321() { checkIsSubtype("!void|void","!null&null"); }
	@Test public void test_3322() { checkIsSubtype("!void|void","!{void f1}"); }
	@Test public void test_3323() { checkIsSubtype("!void|void","!{void f2}"); }
	@Test public void test_3324() { checkIsSubtype("!void|void","!{any f1}"); }
	@Test public void test_3325() { checkIsSubtype("!void|void","!{any f2}"); }
	@Test public void test_3326() { checkIsSubtype("!void|void","!{null f1}"); }
	@Test public void test_3327() { checkIsSubtype("!void|void","!{null f2}"); }
	@Test public void test_3328() { checkIsSubtype("!void|void","!!void"); }
	@Test public void test_3329() { checkIsSubtype("!void|void","!!any"); }
	@Test public void test_3330() { checkIsSubtype("!void|void","!!null"); }
	@Test public void test_3331() { checkIsSubtype("!any|any","any"); }
	@Test public void test_3332() { checkIsSubtype("!any|any","null"); }
	@Test public void test_3333() { checkIsSubtype("!any|any","{void f1}"); }
	@Test public void test_3334() { checkIsSubtype("!any|any","{void f2}"); }
	@Test public void test_3335() { checkIsSubtype("!any|any","{any f1}"); }
	@Test public void test_3336() { checkIsSubtype("!any|any","{any f2}"); }
	@Test public void test_3337() { checkIsSubtype("!any|any","{null f1}"); }
	@Test public void test_3338() { checkIsSubtype("!any|any","{null f2}"); }
	@Test public void test_3339() { checkIsSubtype("!any|any","!void"); }
	@Test public void test_3340() { checkIsSubtype("!any|any","!any"); }
	@Test public void test_3341() { checkIsSubtype("!any|any","!null"); }
	@Test public void test_3342() { checkIsSubtype("!any|any","{{void f1} f1}"); }
	@Test public void test_3343() { checkIsSubtype("!any|any","{{void f2} f1}"); }
	@Test public void test_3344() { checkIsSubtype("!any|any","{{void f1} f2}"); }
	@Test public void test_3345() { checkIsSubtype("!any|any","{{void f2} f2}"); }
	@Test public void test_3346() { checkIsSubtype("!any|any","{{any f1} f1}"); }
	@Test public void test_3347() { checkIsSubtype("!any|any","{{any f2} f1}"); }
	@Test public void test_3348() { checkIsSubtype("!any|any","{{any f1} f2}"); }
	@Test public void test_3349() { checkIsSubtype("!any|any","{{any f2} f2}"); }
	@Test public void test_3350() { checkIsSubtype("!any|any","{{null f1} f1}"); }
	@Test public void test_3351() { checkIsSubtype("!any|any","{{null f2} f1}"); }
	@Test public void test_3352() { checkIsSubtype("!any|any","{{null f1} f2}"); }
	@Test public void test_3353() { checkIsSubtype("!any|any","{{null f2} f2}"); }
	@Test public void test_3354() { checkIsSubtype("!any|any","{!void f1}"); }
	@Test public void test_3355() { checkIsSubtype("!any|any","{!void f2}"); }
	@Test public void test_3356() { checkIsSubtype("!any|any","{!any f1}"); }
	@Test public void test_3357() { checkIsSubtype("!any|any","{!any f2}"); }
	@Test public void test_3358() { checkIsSubtype("!any|any","{!null f1}"); }
	@Test public void test_3359() { checkIsSubtype("!any|any","{!null f2}"); }
	@Test public void test_3360() { checkIsSubtype("!any|any","void|void"); }
	@Test public void test_3361() { checkIsSubtype("!any|any","void|any"); }
	@Test public void test_3362() { checkIsSubtype("!any|any","void|null"); }
	@Test public void test_3363() { checkIsSubtype("!any|any","any|void"); }
	@Test public void test_3364() { checkIsSubtype("!any|any","any|any"); }
	@Test public void test_3365() { checkIsSubtype("!any|any","any|null"); }
	@Test public void test_3366() { checkIsSubtype("!any|any","null|void"); }
	@Test public void test_3367() { checkIsSubtype("!any|any","null|any"); }
	@Test public void test_3368() { checkIsSubtype("!any|any","null|null"); }
	@Test public void test_3369() { checkIsSubtype("!any|any","{void f1}|void"); }
	@Test public void test_3370() { checkIsSubtype("!any|any","{void f2}|void"); }
	@Test public void test_3371() { checkIsSubtype("!any|any","{any f1}|any"); }
	@Test public void test_3372() { checkIsSubtype("!any|any","{any f2}|any"); }
	@Test public void test_3373() { checkIsSubtype("!any|any","{null f1}|null"); }
	@Test public void test_3374() { checkIsSubtype("!any|any","{null f2}|null"); }
	@Test public void test_3375() { checkIsSubtype("!any|any","!void|void"); }
	@Test public void test_3376() { checkIsSubtype("!any|any","!any|any"); }
	@Test public void test_3377() { checkIsSubtype("!any|any","!null|null"); }
	@Test public void test_3378() { checkIsSubtype("!any|any","void&void"); }
	@Test public void test_3379() { checkIsSubtype("!any|any","void&any"); }
	@Test public void test_3380() { checkIsSubtype("!any|any","void&null"); }
	@Test public void test_3381() { checkIsSubtype("!any|any","any&void"); }
	@Test public void test_3382() { checkIsSubtype("!any|any","any&any"); }
	@Test public void test_3383() { checkIsSubtype("!any|any","any&null"); }
	@Test public void test_3384() { checkIsSubtype("!any|any","null&void"); }
	@Test public void test_3385() { checkIsSubtype("!any|any","null&any"); }
	@Test public void test_3386() { checkIsSubtype("!any|any","null&null"); }
	@Test public void test_3387() { checkIsSubtype("!any|any","{void f1}&void"); }
	@Test public void test_3388() { checkIsSubtype("!any|any","{void f2}&void"); }
	@Test public void test_3389() { checkIsSubtype("!any|any","{any f1}&any"); }
	@Test public void test_3390() { checkIsSubtype("!any|any","{any f2}&any"); }
	@Test public void test_3391() { checkIsSubtype("!any|any","{null f1}&null"); }
	@Test public void test_3392() { checkIsSubtype("!any|any","{null f2}&null"); }
	@Test public void test_3393() { checkIsSubtype("!any|any","!void&void"); }
	@Test public void test_3394() { checkIsSubtype("!any|any","!any&any"); }
	@Test public void test_3395() { checkIsSubtype("!any|any","!null&null"); }
	@Test public void test_3396() { checkIsSubtype("!any|any","!{void f1}"); }
	@Test public void test_3397() { checkIsSubtype("!any|any","!{void f2}"); }
	@Test public void test_3398() { checkIsSubtype("!any|any","!{any f1}"); }
	@Test public void test_3399() { checkIsSubtype("!any|any","!{any f2}"); }
	@Test public void test_3400() { checkIsSubtype("!any|any","!{null f1}"); }
	@Test public void test_3401() { checkIsSubtype("!any|any","!{null f2}"); }
	@Test public void test_3402() { checkIsSubtype("!any|any","!!void"); }
	@Test public void test_3403() { checkIsSubtype("!any|any","!!any"); }
	@Test public void test_3404() { checkIsSubtype("!any|any","!!null"); }
	@Test public void test_3405() { checkIsSubtype("!null|null","any"); }
	@Test public void test_3406() { checkIsSubtype("!null|null","null"); }
	@Test public void test_3407() { checkIsSubtype("!null|null","{void f1}"); }
	@Test public void test_3408() { checkIsSubtype("!null|null","{void f2}"); }
	@Test public void test_3409() { checkIsSubtype("!null|null","{any f1}"); }
	@Test public void test_3410() { checkIsSubtype("!null|null","{any f2}"); }
	@Test public void test_3411() { checkIsSubtype("!null|null","{null f1}"); }
	@Test public void test_3412() { checkIsSubtype("!null|null","{null f2}"); }
	@Test public void test_3413() { checkIsSubtype("!null|null","!void"); }
	@Test public void test_3414() { checkIsSubtype("!null|null","!any"); }
	@Test public void test_3415() { checkIsSubtype("!null|null","!null"); }
	@Test public void test_3416() { checkIsSubtype("!null|null","{{void f1} f1}"); }
	@Test public void test_3417() { checkIsSubtype("!null|null","{{void f2} f1}"); }
	@Test public void test_3418() { checkIsSubtype("!null|null","{{void f1} f2}"); }
	@Test public void test_3419() { checkIsSubtype("!null|null","{{void f2} f2}"); }
	@Test public void test_3420() { checkIsSubtype("!null|null","{{any f1} f1}"); }
	@Test public void test_3421() { checkIsSubtype("!null|null","{{any f2} f1}"); }
	@Test public void test_3422() { checkIsSubtype("!null|null","{{any f1} f2}"); }
	@Test public void test_3423() { checkIsSubtype("!null|null","{{any f2} f2}"); }
	@Test public void test_3424() { checkIsSubtype("!null|null","{{null f1} f1}"); }
	@Test public void test_3425() { checkIsSubtype("!null|null","{{null f2} f1}"); }
	@Test public void test_3426() { checkIsSubtype("!null|null","{{null f1} f2}"); }
	@Test public void test_3427() { checkIsSubtype("!null|null","{{null f2} f2}"); }
	@Test public void test_3428() { checkIsSubtype("!null|null","{!void f1}"); }
	@Test public void test_3429() { checkIsSubtype("!null|null","{!void f2}"); }
	@Test public void test_3430() { checkIsSubtype("!null|null","{!any f1}"); }
	@Test public void test_3431() { checkIsSubtype("!null|null","{!any f2}"); }
	@Test public void test_3432() { checkIsSubtype("!null|null","{!null f1}"); }
	@Test public void test_3433() { checkIsSubtype("!null|null","{!null f2}"); }
	@Test public void test_3434() { checkIsSubtype("!null|null","void|void"); }
	@Test public void test_3435() { checkIsSubtype("!null|null","void|any"); }
	@Test public void test_3436() { checkIsSubtype("!null|null","void|null"); }
	@Test public void test_3437() { checkIsSubtype("!null|null","any|void"); }
	@Test public void test_3438() { checkIsSubtype("!null|null","any|any"); }
	@Test public void test_3439() { checkIsSubtype("!null|null","any|null"); }
	@Test public void test_3440() { checkIsSubtype("!null|null","null|void"); }
	@Test public void test_3441() { checkIsSubtype("!null|null","null|any"); }
	@Test public void test_3442() { checkIsSubtype("!null|null","null|null"); }
	@Test public void test_3443() { checkIsSubtype("!null|null","{void f1}|void"); }
	@Test public void test_3444() { checkIsSubtype("!null|null","{void f2}|void"); }
	@Test public void test_3445() { checkIsSubtype("!null|null","{any f1}|any"); }
	@Test public void test_3446() { checkIsSubtype("!null|null","{any f2}|any"); }
	@Test public void test_3447() { checkIsSubtype("!null|null","{null f1}|null"); }
	@Test public void test_3448() { checkIsSubtype("!null|null","{null f2}|null"); }
	@Test public void test_3449() { checkIsSubtype("!null|null","!void|void"); }
	@Test public void test_3450() { checkIsSubtype("!null|null","!any|any"); }
	@Test public void test_3451() { checkIsSubtype("!null|null","!null|null"); }
	@Test public void test_3452() { checkIsSubtype("!null|null","void&void"); }
	@Test public void test_3453() { checkIsSubtype("!null|null","void&any"); }
	@Test public void test_3454() { checkIsSubtype("!null|null","void&null"); }
	@Test public void test_3455() { checkIsSubtype("!null|null","any&void"); }
	@Test public void test_3456() { checkIsSubtype("!null|null","any&any"); }
	@Test public void test_3457() { checkIsSubtype("!null|null","any&null"); }
	@Test public void test_3458() { checkIsSubtype("!null|null","null&void"); }
	@Test public void test_3459() { checkIsSubtype("!null|null","null&any"); }
	@Test public void test_3460() { checkIsSubtype("!null|null","null&null"); }
	@Test public void test_3461() { checkIsSubtype("!null|null","{void f1}&void"); }
	@Test public void test_3462() { checkIsSubtype("!null|null","{void f2}&void"); }
	@Test public void test_3463() { checkIsSubtype("!null|null","{any f1}&any"); }
	@Test public void test_3464() { checkIsSubtype("!null|null","{any f2}&any"); }
	@Test public void test_3465() { checkIsSubtype("!null|null","{null f1}&null"); }
	@Test public void test_3466() { checkIsSubtype("!null|null","{null f2}&null"); }
	@Test public void test_3467() { checkIsSubtype("!null|null","!void&void"); }
	@Test public void test_3468() { checkIsSubtype("!null|null","!any&any"); }
	@Test public void test_3469() { checkIsSubtype("!null|null","!null&null"); }
	@Test public void test_3470() { checkIsSubtype("!null|null","!{void f1}"); }
	@Test public void test_3471() { checkIsSubtype("!null|null","!{void f2}"); }
	@Test public void test_3472() { checkIsSubtype("!null|null","!{any f1}"); }
	@Test public void test_3473() { checkIsSubtype("!null|null","!{any f2}"); }
	@Test public void test_3474() { checkIsSubtype("!null|null","!{null f1}"); }
	@Test public void test_3475() { checkIsSubtype("!null|null","!{null f2}"); }
	@Test public void test_3476() { checkIsSubtype("!null|null","!!void"); }
	@Test public void test_3477() { checkIsSubtype("!null|null","!!any"); }
	@Test public void test_3478() { checkIsSubtype("!null|null","!!null"); }
	@Test public void test_3479() { checkNotSubtype("void&void","any"); }
	@Test public void test_3480() { checkNotSubtype("void&void","null"); }
	@Test public void test_3481() { checkIsSubtype("void&void","{void f1}"); }
	@Test public void test_3482() { checkIsSubtype("void&void","{void f2}"); }
	@Test public void test_3483() { checkNotSubtype("void&void","{any f1}"); }
	@Test public void test_3484() { checkNotSubtype("void&void","{any f2}"); }
	@Test public void test_3485() { checkNotSubtype("void&void","{null f1}"); }
	@Test public void test_3486() { checkNotSubtype("void&void","{null f2}"); }
	@Test public void test_3487() { checkNotSubtype("void&void","!void"); }
	@Test public void test_3488() { checkIsSubtype("void&void","!any"); }
	@Test public void test_3489() { checkNotSubtype("void&void","!null"); }
	@Test public void test_3490() { checkIsSubtype("void&void","{{void f1} f1}"); }
	@Test public void test_3491() { checkIsSubtype("void&void","{{void f2} f1}"); }
	@Test public void test_3492() { checkIsSubtype("void&void","{{void f1} f2}"); }
	@Test public void test_3493() { checkIsSubtype("void&void","{{void f2} f2}"); }
	@Test public void test_3494() { checkNotSubtype("void&void","{{any f1} f1}"); }
	@Test public void test_3495() { checkNotSubtype("void&void","{{any f2} f1}"); }
	@Test public void test_3496() { checkNotSubtype("void&void","{{any f1} f2}"); }
	@Test public void test_3497() { checkNotSubtype("void&void","{{any f2} f2}"); }
	@Test public void test_3498() { checkNotSubtype("void&void","{{null f1} f1}"); }
	@Test public void test_3499() { checkNotSubtype("void&void","{{null f2} f1}"); }
	@Test public void test_3500() { checkNotSubtype("void&void","{{null f1} f2}"); }
	@Test public void test_3501() { checkNotSubtype("void&void","{{null f2} f2}"); }
	@Test public void test_3502() { checkNotSubtype("void&void","{!void f1}"); }
	@Test public void test_3503() { checkNotSubtype("void&void","{!void f2}"); }
	@Test public void test_3504() { checkIsSubtype("void&void","{!any f1}"); }
	@Test public void test_3505() { checkIsSubtype("void&void","{!any f2}"); }
	@Test public void test_3506() { checkNotSubtype("void&void","{!null f1}"); }
	@Test public void test_3507() { checkNotSubtype("void&void","{!null f2}"); }
	@Test public void test_3508() { checkIsSubtype("void&void","void|void"); }
	@Test public void test_3509() { checkNotSubtype("void&void","void|any"); }
	@Test public void test_3510() { checkNotSubtype("void&void","void|null"); }
	@Test public void test_3511() { checkNotSubtype("void&void","any|void"); }
	@Test public void test_3512() { checkNotSubtype("void&void","any|any"); }
	@Test public void test_3513() { checkNotSubtype("void&void","any|null"); }
	@Test public void test_3514() { checkNotSubtype("void&void","null|void"); }
	@Test public void test_3515() { checkNotSubtype("void&void","null|any"); }
	@Test public void test_3516() { checkNotSubtype("void&void","null|null"); }
	@Test public void test_3517() { checkIsSubtype("void&void","{void f1}|void"); }
	@Test public void test_3518() { checkIsSubtype("void&void","{void f2}|void"); }
	@Test public void test_3519() { checkNotSubtype("void&void","{any f1}|any"); }
	@Test public void test_3520() { checkNotSubtype("void&void","{any f2}|any"); }
	@Test public void test_3521() { checkNotSubtype("void&void","{null f1}|null"); }
	@Test public void test_3522() { checkNotSubtype("void&void","{null f2}|null"); }
	@Test public void test_3523() { checkNotSubtype("void&void","!void|void"); }
	@Test public void test_3524() { checkNotSubtype("void&void","!any|any"); }
	@Test public void test_3525() { checkNotSubtype("void&void","!null|null"); }
	@Test public void test_3526() { checkIsSubtype("void&void","void&void"); }
	@Test public void test_3527() { checkIsSubtype("void&void","void&any"); }
	@Test public void test_3528() { checkIsSubtype("void&void","void&null"); }
	@Test public void test_3529() { checkIsSubtype("void&void","any&void"); }
	@Test public void test_3530() { checkNotSubtype("void&void","any&any"); }
	@Test public void test_3531() { checkNotSubtype("void&void","any&null"); }
	@Test public void test_3532() { checkIsSubtype("void&void","null&void"); }
	@Test public void test_3533() { checkNotSubtype("void&void","null&any"); }
	@Test public void test_3534() { checkNotSubtype("void&void","null&null"); }
	@Test public void test_3535() { checkIsSubtype("void&void","{void f1}&void"); }
	@Test public void test_3536() { checkIsSubtype("void&void","{void f2}&void"); }
	@Test public void test_3537() { checkNotSubtype("void&void","{any f1}&any"); }
	@Test public void test_3538() { checkNotSubtype("void&void","{any f2}&any"); }
	@Test public void test_3539() { checkIsSubtype("void&void","{null f1}&null"); }
	@Test public void test_3540() { checkIsSubtype("void&void","{null f2}&null"); }
	@Test public void test_3541() { checkIsSubtype("void&void","!void&void"); }
	@Test public void test_3542() { checkIsSubtype("void&void","!any&any"); }
	@Test public void test_3543() { checkIsSubtype("void&void","!null&null"); }
	@Test public void test_3544() { checkNotSubtype("void&void","!{void f1}"); }
	@Test public void test_3545() { checkNotSubtype("void&void","!{void f2}"); }
	@Test public void test_3546() { checkNotSubtype("void&void","!{any f1}"); }
	@Test public void test_3547() { checkNotSubtype("void&void","!{any f2}"); }
	@Test public void test_3548() { checkNotSubtype("void&void","!{null f1}"); }
	@Test public void test_3549() { checkNotSubtype("void&void","!{null f2}"); }
	@Test public void test_3550() { checkIsSubtype("void&void","!!void"); }
	@Test public void test_3551() { checkNotSubtype("void&void","!!any"); }
	@Test public void test_3552() { checkNotSubtype("void&void","!!null"); }
	@Test public void test_3553() { checkNotSubtype("void&any","any"); }
	@Test public void test_3554() { checkNotSubtype("void&any","null"); }
	@Test public void test_3555() { checkIsSubtype("void&any","{void f1}"); }
	@Test public void test_3556() { checkIsSubtype("void&any","{void f2}"); }
	@Test public void test_3557() { checkNotSubtype("void&any","{any f1}"); }
	@Test public void test_3558() { checkNotSubtype("void&any","{any f2}"); }
	@Test public void test_3559() { checkNotSubtype("void&any","{null f1}"); }
	@Test public void test_3560() { checkNotSubtype("void&any","{null f2}"); }
	@Test public void test_3561() { checkNotSubtype("void&any","!void"); }
	@Test public void test_3562() { checkIsSubtype("void&any","!any"); }
	@Test public void test_3563() { checkNotSubtype("void&any","!null"); }
	@Test public void test_3564() { checkIsSubtype("void&any","{{void f1} f1}"); }
	@Test public void test_3565() { checkIsSubtype("void&any","{{void f2} f1}"); }
	@Test public void test_3566() { checkIsSubtype("void&any","{{void f1} f2}"); }
	@Test public void test_3567() { checkIsSubtype("void&any","{{void f2} f2}"); }
	@Test public void test_3568() { checkNotSubtype("void&any","{{any f1} f1}"); }
	@Test public void test_3569() { checkNotSubtype("void&any","{{any f2} f1}"); }
	@Test public void test_3570() { checkNotSubtype("void&any","{{any f1} f2}"); }
	@Test public void test_3571() { checkNotSubtype("void&any","{{any f2} f2}"); }
	@Test public void test_3572() { checkNotSubtype("void&any","{{null f1} f1}"); }
	@Test public void test_3573() { checkNotSubtype("void&any","{{null f2} f1}"); }
	@Test public void test_3574() { checkNotSubtype("void&any","{{null f1} f2}"); }
	@Test public void test_3575() { checkNotSubtype("void&any","{{null f2} f2}"); }
	@Test public void test_3576() { checkNotSubtype("void&any","{!void f1}"); }
	@Test public void test_3577() { checkNotSubtype("void&any","{!void f2}"); }
	@Test public void test_3578() { checkIsSubtype("void&any","{!any f1}"); }
	@Test public void test_3579() { checkIsSubtype("void&any","{!any f2}"); }
	@Test public void test_3580() { checkNotSubtype("void&any","{!null f1}"); }
	@Test public void test_3581() { checkNotSubtype("void&any","{!null f2}"); }
	@Test public void test_3582() { checkIsSubtype("void&any","void|void"); }
	@Test public void test_3583() { checkNotSubtype("void&any","void|any"); }
	@Test public void test_3584() { checkNotSubtype("void&any","void|null"); }
	@Test public void test_3585() { checkNotSubtype("void&any","any|void"); }
	@Test public void test_3586() { checkNotSubtype("void&any","any|any"); }
	@Test public void test_3587() { checkNotSubtype("void&any","any|null"); }
	@Test public void test_3588() { checkNotSubtype("void&any","null|void"); }
	@Test public void test_3589() { checkNotSubtype("void&any","null|any"); }
	@Test public void test_3590() { checkNotSubtype("void&any","null|null"); }
	@Test public void test_3591() { checkIsSubtype("void&any","{void f1}|void"); }
	@Test public void test_3592() { checkIsSubtype("void&any","{void f2}|void"); }
	@Test public void test_3593() { checkNotSubtype("void&any","{any f1}|any"); }
	@Test public void test_3594() { checkNotSubtype("void&any","{any f2}|any"); }
	@Test public void test_3595() { checkNotSubtype("void&any","{null f1}|null"); }
	@Test public void test_3596() { checkNotSubtype("void&any","{null f2}|null"); }
	@Test public void test_3597() { checkNotSubtype("void&any","!void|void"); }
	@Test public void test_3598() { checkNotSubtype("void&any","!any|any"); }
	@Test public void test_3599() { checkNotSubtype("void&any","!null|null"); }
	@Test public void test_3600() { checkIsSubtype("void&any","void&void"); }
	@Test public void test_3601() { checkIsSubtype("void&any","void&any"); }
	@Test public void test_3602() { checkIsSubtype("void&any","void&null"); }
	@Test public void test_3603() { checkIsSubtype("void&any","any&void"); }
	@Test public void test_3604() { checkNotSubtype("void&any","any&any"); }
	@Test public void test_3605() { checkNotSubtype("void&any","any&null"); }
	@Test public void test_3606() { checkIsSubtype("void&any","null&void"); }
	@Test public void test_3607() { checkNotSubtype("void&any","null&any"); }
	@Test public void test_3608() { checkNotSubtype("void&any","null&null"); }
	@Test public void test_3609() { checkIsSubtype("void&any","{void f1}&void"); }
	@Test public void test_3610() { checkIsSubtype("void&any","{void f2}&void"); }
	@Test public void test_3611() { checkNotSubtype("void&any","{any f1}&any"); }
	@Test public void test_3612() { checkNotSubtype("void&any","{any f2}&any"); }
	@Test public void test_3613() { checkIsSubtype("void&any","{null f1}&null"); }
	@Test public void test_3614() { checkIsSubtype("void&any","{null f2}&null"); }
	@Test public void test_3615() { checkIsSubtype("void&any","!void&void"); }
	@Test public void test_3616() { checkIsSubtype("void&any","!any&any"); }
	@Test public void test_3617() { checkIsSubtype("void&any","!null&null"); }
	@Test public void test_3618() { checkNotSubtype("void&any","!{void f1}"); }
	@Test public void test_3619() { checkNotSubtype("void&any","!{void f2}"); }
	@Test public void test_3620() { checkNotSubtype("void&any","!{any f1}"); }
	@Test public void test_3621() { checkNotSubtype("void&any","!{any f2}"); }
	@Test public void test_3622() { checkNotSubtype("void&any","!{null f1}"); }
	@Test public void test_3623() { checkNotSubtype("void&any","!{null f2}"); }
	@Test public void test_3624() { checkIsSubtype("void&any","!!void"); }
	@Test public void test_3625() { checkNotSubtype("void&any","!!any"); }
	@Test public void test_3626() { checkNotSubtype("void&any","!!null"); }
	@Test public void test_3627() { checkNotSubtype("void&null","any"); }
	@Test public void test_3628() { checkNotSubtype("void&null","null"); }
	@Test public void test_3629() { checkIsSubtype("void&null","{void f1}"); }
	@Test public void test_3630() { checkIsSubtype("void&null","{void f2}"); }
	@Test public void test_3631() { checkNotSubtype("void&null","{any f1}"); }
	@Test public void test_3632() { checkNotSubtype("void&null","{any f2}"); }
	@Test public void test_3633() { checkNotSubtype("void&null","{null f1}"); }
	@Test public void test_3634() { checkNotSubtype("void&null","{null f2}"); }
	@Test public void test_3635() { checkNotSubtype("void&null","!void"); }
	@Test public void test_3636() { checkIsSubtype("void&null","!any"); }
	@Test public void test_3637() { checkNotSubtype("void&null","!null"); }
	@Test public void test_3638() { checkIsSubtype("void&null","{{void f1} f1}"); }
	@Test public void test_3639() { checkIsSubtype("void&null","{{void f2} f1}"); }
	@Test public void test_3640() { checkIsSubtype("void&null","{{void f1} f2}"); }
	@Test public void test_3641() { checkIsSubtype("void&null","{{void f2} f2}"); }
	@Test public void test_3642() { checkNotSubtype("void&null","{{any f1} f1}"); }
	@Test public void test_3643() { checkNotSubtype("void&null","{{any f2} f1}"); }
	@Test public void test_3644() { checkNotSubtype("void&null","{{any f1} f2}"); }
	@Test public void test_3645() { checkNotSubtype("void&null","{{any f2} f2}"); }
	@Test public void test_3646() { checkNotSubtype("void&null","{{null f1} f1}"); }
	@Test public void test_3647() { checkNotSubtype("void&null","{{null f2} f1}"); }
	@Test public void test_3648() { checkNotSubtype("void&null","{{null f1} f2}"); }
	@Test public void test_3649() { checkNotSubtype("void&null","{{null f2} f2}"); }
	@Test public void test_3650() { checkNotSubtype("void&null","{!void f1}"); }
	@Test public void test_3651() { checkNotSubtype("void&null","{!void f2}"); }
	@Test public void test_3652() { checkIsSubtype("void&null","{!any f1}"); }
	@Test public void test_3653() { checkIsSubtype("void&null","{!any f2}"); }
	@Test public void test_3654() { checkNotSubtype("void&null","{!null f1}"); }
	@Test public void test_3655() { checkNotSubtype("void&null","{!null f2}"); }
	@Test public void test_3656() { checkIsSubtype("void&null","void|void"); }
	@Test public void test_3657() { checkNotSubtype("void&null","void|any"); }
	@Test public void test_3658() { checkNotSubtype("void&null","void|null"); }
	@Test public void test_3659() { checkNotSubtype("void&null","any|void"); }
	@Test public void test_3660() { checkNotSubtype("void&null","any|any"); }
	@Test public void test_3661() { checkNotSubtype("void&null","any|null"); }
	@Test public void test_3662() { checkNotSubtype("void&null","null|void"); }
	@Test public void test_3663() { checkNotSubtype("void&null","null|any"); }
	@Test public void test_3664() { checkNotSubtype("void&null","null|null"); }
	@Test public void test_3665() { checkIsSubtype("void&null","{void f1}|void"); }
	@Test public void test_3666() { checkIsSubtype("void&null","{void f2}|void"); }
	@Test public void test_3667() { checkNotSubtype("void&null","{any f1}|any"); }
	@Test public void test_3668() { checkNotSubtype("void&null","{any f2}|any"); }
	@Test public void test_3669() { checkNotSubtype("void&null","{null f1}|null"); }
	@Test public void test_3670() { checkNotSubtype("void&null","{null f2}|null"); }
	@Test public void test_3671() { checkNotSubtype("void&null","!void|void"); }
	@Test public void test_3672() { checkNotSubtype("void&null","!any|any"); }
	@Test public void test_3673() { checkNotSubtype("void&null","!null|null"); }
	@Test public void test_3674() { checkIsSubtype("void&null","void&void"); }
	@Test public void test_3675() { checkIsSubtype("void&null","void&any"); }
	@Test public void test_3676() { checkIsSubtype("void&null","void&null"); }
	@Test public void test_3677() { checkIsSubtype("void&null","any&void"); }
	@Test public void test_3678() { checkNotSubtype("void&null","any&any"); }
	@Test public void test_3679() { checkNotSubtype("void&null","any&null"); }
	@Test public void test_3680() { checkIsSubtype("void&null","null&void"); }
	@Test public void test_3681() { checkNotSubtype("void&null","null&any"); }
	@Test public void test_3682() { checkNotSubtype("void&null","null&null"); }
	@Test public void test_3683() { checkIsSubtype("void&null","{void f1}&void"); }
	@Test public void test_3684() { checkIsSubtype("void&null","{void f2}&void"); }
	@Test public void test_3685() { checkNotSubtype("void&null","{any f1}&any"); }
	@Test public void test_3686() { checkNotSubtype("void&null","{any f2}&any"); }
	@Test public void test_3687() { checkIsSubtype("void&null","{null f1}&null"); }
	@Test public void test_3688() { checkIsSubtype("void&null","{null f2}&null"); }
	@Test public void test_3689() { checkIsSubtype("void&null","!void&void"); }
	@Test public void test_3690() { checkIsSubtype("void&null","!any&any"); }
	@Test public void test_3691() { checkIsSubtype("void&null","!null&null"); }
	@Test public void test_3692() { checkNotSubtype("void&null","!{void f1}"); }
	@Test public void test_3693() { checkNotSubtype("void&null","!{void f2}"); }
	@Test public void test_3694() { checkNotSubtype("void&null","!{any f1}"); }
	@Test public void test_3695() { checkNotSubtype("void&null","!{any f2}"); }
	@Test public void test_3696() { checkNotSubtype("void&null","!{null f1}"); }
	@Test public void test_3697() { checkNotSubtype("void&null","!{null f2}"); }
	@Test public void test_3698() { checkIsSubtype("void&null","!!void"); }
	@Test public void test_3699() { checkNotSubtype("void&null","!!any"); }
	@Test public void test_3700() { checkNotSubtype("void&null","!!null"); }
	@Test public void test_3701() { checkNotSubtype("any&void","any"); }
	@Test public void test_3702() { checkNotSubtype("any&void","null"); }
	@Test public void test_3703() { checkIsSubtype("any&void","{void f1}"); }
	@Test public void test_3704() { checkIsSubtype("any&void","{void f2}"); }
	@Test public void test_3705() { checkNotSubtype("any&void","{any f1}"); }
	@Test public void test_3706() { checkNotSubtype("any&void","{any f2}"); }
	@Test public void test_3707() { checkNotSubtype("any&void","{null f1}"); }
	@Test public void test_3708() { checkNotSubtype("any&void","{null f2}"); }
	@Test public void test_3709() { checkNotSubtype("any&void","!void"); }
	@Test public void test_3710() { checkIsSubtype("any&void","!any"); }
	@Test public void test_3711() { checkNotSubtype("any&void","!null"); }
	@Test public void test_3712() { checkIsSubtype("any&void","{{void f1} f1}"); }
	@Test public void test_3713() { checkIsSubtype("any&void","{{void f2} f1}"); }
	@Test public void test_3714() { checkIsSubtype("any&void","{{void f1} f2}"); }
	@Test public void test_3715() { checkIsSubtype("any&void","{{void f2} f2}"); }
	@Test public void test_3716() { checkNotSubtype("any&void","{{any f1} f1}"); }
	@Test public void test_3717() { checkNotSubtype("any&void","{{any f2} f1}"); }
	@Test public void test_3718() { checkNotSubtype("any&void","{{any f1} f2}"); }
	@Test public void test_3719() { checkNotSubtype("any&void","{{any f2} f2}"); }
	@Test public void test_3720() { checkNotSubtype("any&void","{{null f1} f1}"); }
	@Test public void test_3721() { checkNotSubtype("any&void","{{null f2} f1}"); }
	@Test public void test_3722() { checkNotSubtype("any&void","{{null f1} f2}"); }
	@Test public void test_3723() { checkNotSubtype("any&void","{{null f2} f2}"); }
	@Test public void test_3724() { checkNotSubtype("any&void","{!void f1}"); }
	@Test public void test_3725() { checkNotSubtype("any&void","{!void f2}"); }
	@Test public void test_3726() { checkIsSubtype("any&void","{!any f1}"); }
	@Test public void test_3727() { checkIsSubtype("any&void","{!any f2}"); }
	@Test public void test_3728() { checkNotSubtype("any&void","{!null f1}"); }
	@Test public void test_3729() { checkNotSubtype("any&void","{!null f2}"); }
	@Test public void test_3730() { checkIsSubtype("any&void","void|void"); }
	@Test public void test_3731() { checkNotSubtype("any&void","void|any"); }
	@Test public void test_3732() { checkNotSubtype("any&void","void|null"); }
	@Test public void test_3733() { checkNotSubtype("any&void","any|void"); }
	@Test public void test_3734() { checkNotSubtype("any&void","any|any"); }
	@Test public void test_3735() { checkNotSubtype("any&void","any|null"); }
	@Test public void test_3736() { checkNotSubtype("any&void","null|void"); }
	@Test public void test_3737() { checkNotSubtype("any&void","null|any"); }
	@Test public void test_3738() { checkNotSubtype("any&void","null|null"); }
	@Test public void test_3739() { checkIsSubtype("any&void","{void f1}|void"); }
	@Test public void test_3740() { checkIsSubtype("any&void","{void f2}|void"); }
	@Test public void test_3741() { checkNotSubtype("any&void","{any f1}|any"); }
	@Test public void test_3742() { checkNotSubtype("any&void","{any f2}|any"); }
	@Test public void test_3743() { checkNotSubtype("any&void","{null f1}|null"); }
	@Test public void test_3744() { checkNotSubtype("any&void","{null f2}|null"); }
	@Test public void test_3745() { checkNotSubtype("any&void","!void|void"); }
	@Test public void test_3746() { checkNotSubtype("any&void","!any|any"); }
	@Test public void test_3747() { checkNotSubtype("any&void","!null|null"); }
	@Test public void test_3748() { checkIsSubtype("any&void","void&void"); }
	@Test public void test_3749() { checkIsSubtype("any&void","void&any"); }
	@Test public void test_3750() { checkIsSubtype("any&void","void&null"); }
	@Test public void test_3751() { checkIsSubtype("any&void","any&void"); }
	@Test public void test_3752() { checkNotSubtype("any&void","any&any"); }
	@Test public void test_3753() { checkNotSubtype("any&void","any&null"); }
	@Test public void test_3754() { checkIsSubtype("any&void","null&void"); }
	@Test public void test_3755() { checkNotSubtype("any&void","null&any"); }
	@Test public void test_3756() { checkNotSubtype("any&void","null&null"); }
	@Test public void test_3757() { checkIsSubtype("any&void","{void f1}&void"); }
	@Test public void test_3758() { checkIsSubtype("any&void","{void f2}&void"); }
	@Test public void test_3759() { checkNotSubtype("any&void","{any f1}&any"); }
	@Test public void test_3760() { checkNotSubtype("any&void","{any f2}&any"); }
	@Test public void test_3761() { checkIsSubtype("any&void","{null f1}&null"); }
	@Test public void test_3762() { checkIsSubtype("any&void","{null f2}&null"); }
	@Test public void test_3763() { checkIsSubtype("any&void","!void&void"); }
	@Test public void test_3764() { checkIsSubtype("any&void","!any&any"); }
	@Test public void test_3765() { checkIsSubtype("any&void","!null&null"); }
	@Test public void test_3766() { checkNotSubtype("any&void","!{void f1}"); }
	@Test public void test_3767() { checkNotSubtype("any&void","!{void f2}"); }
	@Test public void test_3768() { checkNotSubtype("any&void","!{any f1}"); }
	@Test public void test_3769() { checkNotSubtype("any&void","!{any f2}"); }
	@Test public void test_3770() { checkNotSubtype("any&void","!{null f1}"); }
	@Test public void test_3771() { checkNotSubtype("any&void","!{null f2}"); }
	@Test public void test_3772() { checkIsSubtype("any&void","!!void"); }
	@Test public void test_3773() { checkNotSubtype("any&void","!!any"); }
	@Test public void test_3774() { checkNotSubtype("any&void","!!null"); }
	@Test public void test_3775() { checkIsSubtype("any&any","any"); }
	@Test public void test_3776() { checkIsSubtype("any&any","null"); }
	@Test public void test_3777() { checkIsSubtype("any&any","{void f1}"); }
	@Test public void test_3778() { checkIsSubtype("any&any","{void f2}"); }
	@Test public void test_3779() { checkIsSubtype("any&any","{any f1}"); }
	@Test public void test_3780() { checkIsSubtype("any&any","{any f2}"); }
	@Test public void test_3781() { checkIsSubtype("any&any","{null f1}"); }
	@Test public void test_3782() { checkIsSubtype("any&any","{null f2}"); }
	@Test public void test_3783() { checkIsSubtype("any&any","!void"); }
	@Test public void test_3784() { checkIsSubtype("any&any","!any"); }
	@Test public void test_3785() { checkIsSubtype("any&any","!null"); }
	@Test public void test_3786() { checkIsSubtype("any&any","{{void f1} f1}"); }
	@Test public void test_3787() { checkIsSubtype("any&any","{{void f2} f1}"); }
	@Test public void test_3788() { checkIsSubtype("any&any","{{void f1} f2}"); }
	@Test public void test_3789() { checkIsSubtype("any&any","{{void f2} f2}"); }
	@Test public void test_3790() { checkIsSubtype("any&any","{{any f1} f1}"); }
	@Test public void test_3791() { checkIsSubtype("any&any","{{any f2} f1}"); }
	@Test public void test_3792() { checkIsSubtype("any&any","{{any f1} f2}"); }
	@Test public void test_3793() { checkIsSubtype("any&any","{{any f2} f2}"); }
	@Test public void test_3794() { checkIsSubtype("any&any","{{null f1} f1}"); }
	@Test public void test_3795() { checkIsSubtype("any&any","{{null f2} f1}"); }
	@Test public void test_3796() { checkIsSubtype("any&any","{{null f1} f2}"); }
	@Test public void test_3797() { checkIsSubtype("any&any","{{null f2} f2}"); }
	@Test public void test_3798() { checkIsSubtype("any&any","{!void f1}"); }
	@Test public void test_3799() { checkIsSubtype("any&any","{!void f2}"); }
	@Test public void test_3800() { checkIsSubtype("any&any","{!any f1}"); }
	@Test public void test_3801() { checkIsSubtype("any&any","{!any f2}"); }
	@Test public void test_3802() { checkIsSubtype("any&any","{!null f1}"); }
	@Test public void test_3803() { checkIsSubtype("any&any","{!null f2}"); }
	@Test public void test_3804() { checkIsSubtype("any&any","void|void"); }
	@Test public void test_3805() { checkIsSubtype("any&any","void|any"); }
	@Test public void test_3806() { checkIsSubtype("any&any","void|null"); }
	@Test public void test_3807() { checkIsSubtype("any&any","any|void"); }
	@Test public void test_3808() { checkIsSubtype("any&any","any|any"); }
	@Test public void test_3809() { checkIsSubtype("any&any","any|null"); }
	@Test public void test_3810() { checkIsSubtype("any&any","null|void"); }
	@Test public void test_3811() { checkIsSubtype("any&any","null|any"); }
	@Test public void test_3812() { checkIsSubtype("any&any","null|null"); }
	@Test public void test_3813() { checkIsSubtype("any&any","{void f1}|void"); }
	@Test public void test_3814() { checkIsSubtype("any&any","{void f2}|void"); }
	@Test public void test_3815() { checkIsSubtype("any&any","{any f1}|any"); }
	@Test public void test_3816() { checkIsSubtype("any&any","{any f2}|any"); }
	@Test public void test_3817() { checkIsSubtype("any&any","{null f1}|null"); }
	@Test public void test_3818() { checkIsSubtype("any&any","{null f2}|null"); }
	@Test public void test_3819() { checkIsSubtype("any&any","!void|void"); }
	@Test public void test_3820() { checkIsSubtype("any&any","!any|any"); }
	@Test public void test_3821() { checkIsSubtype("any&any","!null|null"); }
	@Test public void test_3822() { checkIsSubtype("any&any","void&void"); }
	@Test public void test_3823() { checkIsSubtype("any&any","void&any"); }
	@Test public void test_3824() { checkIsSubtype("any&any","void&null"); }
	@Test public void test_3825() { checkIsSubtype("any&any","any&void"); }
	@Test public void test_3826() { checkIsSubtype("any&any","any&any"); }
	@Test public void test_3827() { checkIsSubtype("any&any","any&null"); }
	@Test public void test_3828() { checkIsSubtype("any&any","null&void"); }
	@Test public void test_3829() { checkIsSubtype("any&any","null&any"); }
	@Test public void test_3830() { checkIsSubtype("any&any","null&null"); }
	@Test public void test_3831() { checkIsSubtype("any&any","{void f1}&void"); }
	@Test public void test_3832() { checkIsSubtype("any&any","{void f2}&void"); }
	@Test public void test_3833() { checkIsSubtype("any&any","{any f1}&any"); }
	@Test public void test_3834() { checkIsSubtype("any&any","{any f2}&any"); }
	@Test public void test_3835() { checkIsSubtype("any&any","{null f1}&null"); }
	@Test public void test_3836() { checkIsSubtype("any&any","{null f2}&null"); }
	@Test public void test_3837() { checkIsSubtype("any&any","!void&void"); }
	@Test public void test_3838() { checkIsSubtype("any&any","!any&any"); }
	@Test public void test_3839() { checkIsSubtype("any&any","!null&null"); }
	@Test public void test_3840() { checkIsSubtype("any&any","!{void f1}"); }
	@Test public void test_3841() { checkIsSubtype("any&any","!{void f2}"); }
	@Test public void test_3842() { checkIsSubtype("any&any","!{any f1}"); }
	@Test public void test_3843() { checkIsSubtype("any&any","!{any f2}"); }
	@Test public void test_3844() { checkIsSubtype("any&any","!{null f1}"); }
	@Test public void test_3845() { checkIsSubtype("any&any","!{null f2}"); }
	@Test public void test_3846() { checkIsSubtype("any&any","!!void"); }
	@Test public void test_3847() { checkIsSubtype("any&any","!!any"); }
	@Test public void test_3848() { checkIsSubtype("any&any","!!null"); }
	@Test public void test_3849() { checkNotSubtype("any&null","any"); }
	@Test public void test_3850() { checkIsSubtype("any&null","null"); }
	@Test public void test_3851() { checkIsSubtype("any&null","{void f1}"); }
	@Test public void test_3852() { checkIsSubtype("any&null","{void f2}"); }
	@Test public void test_3853() { checkNotSubtype("any&null","{any f1}"); }
	@Test public void test_3854() { checkNotSubtype("any&null","{any f2}"); }
	@Test public void test_3855() { checkNotSubtype("any&null","{null f1}"); }
	@Test public void test_3856() { checkNotSubtype("any&null","{null f2}"); }
	@Test public void test_3857() { checkNotSubtype("any&null","!void"); }
	@Test public void test_3858() { checkIsSubtype("any&null","!any"); }
	@Test public void test_3859() { checkNotSubtype("any&null","!null"); }
	@Test public void test_3860() { checkIsSubtype("any&null","{{void f1} f1}"); }
	@Test public void test_3861() { checkIsSubtype("any&null","{{void f2} f1}"); }
	@Test public void test_3862() { checkIsSubtype("any&null","{{void f1} f2}"); }
	@Test public void test_3863() { checkIsSubtype("any&null","{{void f2} f2}"); }
	@Test public void test_3864() { checkNotSubtype("any&null","{{any f1} f1}"); }
	@Test public void test_3865() { checkNotSubtype("any&null","{{any f2} f1}"); }
	@Test public void test_3866() { checkNotSubtype("any&null","{{any f1} f2}"); }
	@Test public void test_3867() { checkNotSubtype("any&null","{{any f2} f2}"); }
	@Test public void test_3868() { checkNotSubtype("any&null","{{null f1} f1}"); }
	@Test public void test_3869() { checkNotSubtype("any&null","{{null f2} f1}"); }
	@Test public void test_3870() { checkNotSubtype("any&null","{{null f1} f2}"); }
	@Test public void test_3871() { checkNotSubtype("any&null","{{null f2} f2}"); }
	@Test public void test_3872() { checkNotSubtype("any&null","{!void f1}"); }
	@Test public void test_3873() { checkNotSubtype("any&null","{!void f2}"); }
	@Test public void test_3874() { checkIsSubtype("any&null","{!any f1}"); }
	@Test public void test_3875() { checkIsSubtype("any&null","{!any f2}"); }
	@Test public void test_3876() { checkNotSubtype("any&null","{!null f1}"); }
	@Test public void test_3877() { checkNotSubtype("any&null","{!null f2}"); }
	@Test public void test_3878() { checkIsSubtype("any&null","void|void"); }
	@Test public void test_3879() { checkNotSubtype("any&null","void|any"); }
	@Test public void test_3880() { checkIsSubtype("any&null","void|null"); }
	@Test public void test_3881() { checkNotSubtype("any&null","any|void"); }
	@Test public void test_3882() { checkNotSubtype("any&null","any|any"); }
	@Test public void test_3883() { checkNotSubtype("any&null","any|null"); }
	@Test public void test_3884() { checkIsSubtype("any&null","null|void"); }
	@Test public void test_3885() { checkNotSubtype("any&null","null|any"); }
	@Test public void test_3886() { checkIsSubtype("any&null","null|null"); }
	@Test public void test_3887() { checkIsSubtype("any&null","{void f1}|void"); }
	@Test public void test_3888() { checkIsSubtype("any&null","{void f2}|void"); }
	@Test public void test_3889() { checkNotSubtype("any&null","{any f1}|any"); }
	@Test public void test_3890() { checkNotSubtype("any&null","{any f2}|any"); }
	@Test public void test_3891() { checkNotSubtype("any&null","{null f1}|null"); }
	@Test public void test_3892() { checkNotSubtype("any&null","{null f2}|null"); }
	@Test public void test_3893() { checkNotSubtype("any&null","!void|void"); }
	@Test public void test_3894() { checkNotSubtype("any&null","!any|any"); }
	@Test public void test_3895() { checkNotSubtype("any&null","!null|null"); }
	@Test public void test_3896() { checkIsSubtype("any&null","void&void"); }
	@Test public void test_3897() { checkIsSubtype("any&null","void&any"); }
	@Test public void test_3898() { checkIsSubtype("any&null","void&null"); }
	@Test public void test_3899() { checkIsSubtype("any&null","any&void"); }
	@Test public void test_3900() { checkNotSubtype("any&null","any&any"); }
	@Test public void test_3901() { checkIsSubtype("any&null","any&null"); }
	@Test public void test_3902() { checkIsSubtype("any&null","null&void"); }
	@Test public void test_3903() { checkIsSubtype("any&null","null&any"); }
	@Test public void test_3904() { checkIsSubtype("any&null","null&null"); }
	@Test public void test_3905() { checkIsSubtype("any&null","{void f1}&void"); }
	@Test public void test_3906() { checkIsSubtype("any&null","{void f2}&void"); }
	@Test public void test_3907() { checkNotSubtype("any&null","{any f1}&any"); }
	@Test public void test_3908() { checkNotSubtype("any&null","{any f2}&any"); }
	@Test public void test_3909() { checkIsSubtype("any&null","{null f1}&null"); }
	@Test public void test_3910() { checkIsSubtype("any&null","{null f2}&null"); }
	@Test public void test_3911() { checkIsSubtype("any&null","!void&void"); }
	@Test public void test_3912() { checkIsSubtype("any&null","!any&any"); }
	@Test public void test_3913() { checkIsSubtype("any&null","!null&null"); }
	@Test public void test_3914() { checkNotSubtype("any&null","!{void f1}"); }
	@Test public void test_3915() { checkNotSubtype("any&null","!{void f2}"); }
	@Test public void test_3916() { checkNotSubtype("any&null","!{any f1}"); }
	@Test public void test_3917() { checkNotSubtype("any&null","!{any f2}"); }
	@Test public void test_3918() { checkNotSubtype("any&null","!{null f1}"); }
	@Test public void test_3919() { checkNotSubtype("any&null","!{null f2}"); }
	@Test public void test_3920() { checkIsSubtype("any&null","!!void"); }
	@Test public void test_3921() { checkNotSubtype("any&null","!!any"); }
	@Test public void test_3922() { checkIsSubtype("any&null","!!null"); }
	@Test public void test_3923() { checkNotSubtype("null&void","any"); }
	@Test public void test_3924() { checkNotSubtype("null&void","null"); }
	@Test public void test_3925() { checkIsSubtype("null&void","{void f1}"); }
	@Test public void test_3926() { checkIsSubtype("null&void","{void f2}"); }
	@Test public void test_3927() { checkNotSubtype("null&void","{any f1}"); }
	@Test public void test_3928() { checkNotSubtype("null&void","{any f2}"); }
	@Test public void test_3929() { checkNotSubtype("null&void","{null f1}"); }
	@Test public void test_3930() { checkNotSubtype("null&void","{null f2}"); }
	@Test public void test_3931() { checkNotSubtype("null&void","!void"); }
	@Test public void test_3932() { checkIsSubtype("null&void","!any"); }
	@Test public void test_3933() { checkNotSubtype("null&void","!null"); }
	@Test public void test_3934() { checkIsSubtype("null&void","{{void f1} f1}"); }
	@Test public void test_3935() { checkIsSubtype("null&void","{{void f2} f1}"); }
	@Test public void test_3936() { checkIsSubtype("null&void","{{void f1} f2}"); }
	@Test public void test_3937() { checkIsSubtype("null&void","{{void f2} f2}"); }
	@Test public void test_3938() { checkNotSubtype("null&void","{{any f1} f1}"); }
	@Test public void test_3939() { checkNotSubtype("null&void","{{any f2} f1}"); }
	@Test public void test_3940() { checkNotSubtype("null&void","{{any f1} f2}"); }
	@Test public void test_3941() { checkNotSubtype("null&void","{{any f2} f2}"); }
	@Test public void test_3942() { checkNotSubtype("null&void","{{null f1} f1}"); }
	@Test public void test_3943() { checkNotSubtype("null&void","{{null f2} f1}"); }
	@Test public void test_3944() { checkNotSubtype("null&void","{{null f1} f2}"); }
	@Test public void test_3945() { checkNotSubtype("null&void","{{null f2} f2}"); }
	@Test public void test_3946() { checkNotSubtype("null&void","{!void f1}"); }
	@Test public void test_3947() { checkNotSubtype("null&void","{!void f2}"); }
	@Test public void test_3948() { checkIsSubtype("null&void","{!any f1}"); }
	@Test public void test_3949() { checkIsSubtype("null&void","{!any f2}"); }
	@Test public void test_3950() { checkNotSubtype("null&void","{!null f1}"); }
	@Test public void test_3951() { checkNotSubtype("null&void","{!null f2}"); }
	@Test public void test_3952() { checkIsSubtype("null&void","void|void"); }
	@Test public void test_3953() { checkNotSubtype("null&void","void|any"); }
	@Test public void test_3954() { checkNotSubtype("null&void","void|null"); }
	@Test public void test_3955() { checkNotSubtype("null&void","any|void"); }
	@Test public void test_3956() { checkNotSubtype("null&void","any|any"); }
	@Test public void test_3957() { checkNotSubtype("null&void","any|null"); }
	@Test public void test_3958() { checkNotSubtype("null&void","null|void"); }
	@Test public void test_3959() { checkNotSubtype("null&void","null|any"); }
	@Test public void test_3960() { checkNotSubtype("null&void","null|null"); }
	@Test public void test_3961() { checkIsSubtype("null&void","{void f1}|void"); }
	@Test public void test_3962() { checkIsSubtype("null&void","{void f2}|void"); }
	@Test public void test_3963() { checkNotSubtype("null&void","{any f1}|any"); }
	@Test public void test_3964() { checkNotSubtype("null&void","{any f2}|any"); }
	@Test public void test_3965() { checkNotSubtype("null&void","{null f1}|null"); }
	@Test public void test_3966() { checkNotSubtype("null&void","{null f2}|null"); }
	@Test public void test_3967() { checkNotSubtype("null&void","!void|void"); }
	@Test public void test_3968() { checkNotSubtype("null&void","!any|any"); }
	@Test public void test_3969() { checkNotSubtype("null&void","!null|null"); }
	@Test public void test_3970() { checkIsSubtype("null&void","void&void"); }
	@Test public void test_3971() { checkIsSubtype("null&void","void&any"); }
	@Test public void test_3972() { checkIsSubtype("null&void","void&null"); }
	@Test public void test_3973() { checkIsSubtype("null&void","any&void"); }
	@Test public void test_3974() { checkNotSubtype("null&void","any&any"); }
	@Test public void test_3975() { checkNotSubtype("null&void","any&null"); }
	@Test public void test_3976() { checkIsSubtype("null&void","null&void"); }
	@Test public void test_3977() { checkNotSubtype("null&void","null&any"); }
	@Test public void test_3978() { checkNotSubtype("null&void","null&null"); }
	@Test public void test_3979() { checkIsSubtype("null&void","{void f1}&void"); }
	@Test public void test_3980() { checkIsSubtype("null&void","{void f2}&void"); }
	@Test public void test_3981() { checkNotSubtype("null&void","{any f1}&any"); }
	@Test public void test_3982() { checkNotSubtype("null&void","{any f2}&any"); }
	@Test public void test_3983() { checkIsSubtype("null&void","{null f1}&null"); }
	@Test public void test_3984() { checkIsSubtype("null&void","{null f2}&null"); }
	@Test public void test_3985() { checkIsSubtype("null&void","!void&void"); }
	@Test public void test_3986() { checkIsSubtype("null&void","!any&any"); }
	@Test public void test_3987() { checkIsSubtype("null&void","!null&null"); }
	@Test public void test_3988() { checkNotSubtype("null&void","!{void f1}"); }
	@Test public void test_3989() { checkNotSubtype("null&void","!{void f2}"); }
	@Test public void test_3990() { checkNotSubtype("null&void","!{any f1}"); }
	@Test public void test_3991() { checkNotSubtype("null&void","!{any f2}"); }
	@Test public void test_3992() { checkNotSubtype("null&void","!{null f1}"); }
	@Test public void test_3993() { checkNotSubtype("null&void","!{null f2}"); }
	@Test public void test_3994() { checkIsSubtype("null&void","!!void"); }
	@Test public void test_3995() { checkNotSubtype("null&void","!!any"); }
	@Test public void test_3996() { checkNotSubtype("null&void","!!null"); }
	@Test public void test_3997() { checkNotSubtype("null&any","any"); }
	@Test public void test_3998() { checkIsSubtype("null&any","null"); }
	@Test public void test_3999() { checkIsSubtype("null&any","{void f1}"); }
	@Test public void test_4000() { checkIsSubtype("null&any","{void f2}"); }
	@Test public void test_4001() { checkNotSubtype("null&any","{any f1}"); }
	@Test public void test_4002() { checkNotSubtype("null&any","{any f2}"); }
	@Test public void test_4003() { checkNotSubtype("null&any","{null f1}"); }
	@Test public void test_4004() { checkNotSubtype("null&any","{null f2}"); }
	@Test public void test_4005() { checkNotSubtype("null&any","!void"); }
	@Test public void test_4006() { checkIsSubtype("null&any","!any"); }
	@Test public void test_4007() { checkNotSubtype("null&any","!null"); }
	@Test public void test_4008() { checkIsSubtype("null&any","{{void f1} f1}"); }
	@Test public void test_4009() { checkIsSubtype("null&any","{{void f2} f1}"); }
	@Test public void test_4010() { checkIsSubtype("null&any","{{void f1} f2}"); }
	@Test public void test_4011() { checkIsSubtype("null&any","{{void f2} f2}"); }
	@Test public void test_4012() { checkNotSubtype("null&any","{{any f1} f1}"); }
	@Test public void test_4013() { checkNotSubtype("null&any","{{any f2} f1}"); }
	@Test public void test_4014() { checkNotSubtype("null&any","{{any f1} f2}"); }
	@Test public void test_4015() { checkNotSubtype("null&any","{{any f2} f2}"); }
	@Test public void test_4016() { checkNotSubtype("null&any","{{null f1} f1}"); }
	@Test public void test_4017() { checkNotSubtype("null&any","{{null f2} f1}"); }
	@Test public void test_4018() { checkNotSubtype("null&any","{{null f1} f2}"); }
	@Test public void test_4019() { checkNotSubtype("null&any","{{null f2} f2}"); }
	@Test public void test_4020() { checkNotSubtype("null&any","{!void f1}"); }
	@Test public void test_4021() { checkNotSubtype("null&any","{!void f2}"); }
	@Test public void test_4022() { checkIsSubtype("null&any","{!any f1}"); }
	@Test public void test_4023() { checkIsSubtype("null&any","{!any f2}"); }
	@Test public void test_4024() { checkNotSubtype("null&any","{!null f1}"); }
	@Test public void test_4025() { checkNotSubtype("null&any","{!null f2}"); }
	@Test public void test_4026() { checkIsSubtype("null&any","void|void"); }
	@Test public void test_4027() { checkNotSubtype("null&any","void|any"); }
	@Test public void test_4028() { checkIsSubtype("null&any","void|null"); }
	@Test public void test_4029() { checkNotSubtype("null&any","any|void"); }
	@Test public void test_4030() { checkNotSubtype("null&any","any|any"); }
	@Test public void test_4031() { checkNotSubtype("null&any","any|null"); }
	@Test public void test_4032() { checkIsSubtype("null&any","null|void"); }
	@Test public void test_4033() { checkNotSubtype("null&any","null|any"); }
	@Test public void test_4034() { checkIsSubtype("null&any","null|null"); }
	@Test public void test_4035() { checkIsSubtype("null&any","{void f1}|void"); }
	@Test public void test_4036() { checkIsSubtype("null&any","{void f2}|void"); }
	@Test public void test_4037() { checkNotSubtype("null&any","{any f1}|any"); }
	@Test public void test_4038() { checkNotSubtype("null&any","{any f2}|any"); }
	@Test public void test_4039() { checkNotSubtype("null&any","{null f1}|null"); }
	@Test public void test_4040() { checkNotSubtype("null&any","{null f2}|null"); }
	@Test public void test_4041() { checkNotSubtype("null&any","!void|void"); }
	@Test public void test_4042() { checkNotSubtype("null&any","!any|any"); }
	@Test public void test_4043() { checkNotSubtype("null&any","!null|null"); }
	@Test public void test_4044() { checkIsSubtype("null&any","void&void"); }
	@Test public void test_4045() { checkIsSubtype("null&any","void&any"); }
	@Test public void test_4046() { checkIsSubtype("null&any","void&null"); }
	@Test public void test_4047() { checkIsSubtype("null&any","any&void"); }
	@Test public void test_4048() { checkNotSubtype("null&any","any&any"); }
	@Test public void test_4049() { checkIsSubtype("null&any","any&null"); }
	@Test public void test_4050() { checkIsSubtype("null&any","null&void"); }
	@Test public void test_4051() { checkIsSubtype("null&any","null&any"); }
	@Test public void test_4052() { checkIsSubtype("null&any","null&null"); }
	@Test public void test_4053() { checkIsSubtype("null&any","{void f1}&void"); }
	@Test public void test_4054() { checkIsSubtype("null&any","{void f2}&void"); }
	@Test public void test_4055() { checkNotSubtype("null&any","{any f1}&any"); }
	@Test public void test_4056() { checkNotSubtype("null&any","{any f2}&any"); }
	@Test public void test_4057() { checkIsSubtype("null&any","{null f1}&null"); }
	@Test public void test_4058() { checkIsSubtype("null&any","{null f2}&null"); }
	@Test public void test_4059() { checkIsSubtype("null&any","!void&void"); }
	@Test public void test_4060() { checkIsSubtype("null&any","!any&any"); }
	@Test public void test_4061() { checkIsSubtype("null&any","!null&null"); }
	@Test public void test_4062() { checkNotSubtype("null&any","!{void f1}"); }
	@Test public void test_4063() { checkNotSubtype("null&any","!{void f2}"); }
	@Test public void test_4064() { checkNotSubtype("null&any","!{any f1}"); }
	@Test public void test_4065() { checkNotSubtype("null&any","!{any f2}"); }
	@Test public void test_4066() { checkNotSubtype("null&any","!{null f1}"); }
	@Test public void test_4067() { checkNotSubtype("null&any","!{null f2}"); }
	@Test public void test_4068() { checkIsSubtype("null&any","!!void"); }
	@Test public void test_4069() { checkNotSubtype("null&any","!!any"); }
	@Test public void test_4070() { checkIsSubtype("null&any","!!null"); }
	@Test public void test_4071() { checkNotSubtype("null&null","any"); }
	@Test public void test_4072() { checkIsSubtype("null&null","null"); }
	@Test public void test_4073() { checkIsSubtype("null&null","{void f1}"); }
	@Test public void test_4074() { checkIsSubtype("null&null","{void f2}"); }
	@Test public void test_4075() { checkNotSubtype("null&null","{any f1}"); }
	@Test public void test_4076() { checkNotSubtype("null&null","{any f2}"); }
	@Test public void test_4077() { checkNotSubtype("null&null","{null f1}"); }
	@Test public void test_4078() { checkNotSubtype("null&null","{null f2}"); }
	@Test public void test_4079() { checkNotSubtype("null&null","!void"); }
	@Test public void test_4080() { checkIsSubtype("null&null","!any"); }
	@Test public void test_4081() { checkNotSubtype("null&null","!null"); }
	@Test public void test_4082() { checkIsSubtype("null&null","{{void f1} f1}"); }
	@Test public void test_4083() { checkIsSubtype("null&null","{{void f2} f1}"); }
	@Test public void test_4084() { checkIsSubtype("null&null","{{void f1} f2}"); }
	@Test public void test_4085() { checkIsSubtype("null&null","{{void f2} f2}"); }
	@Test public void test_4086() { checkNotSubtype("null&null","{{any f1} f1}"); }
	@Test public void test_4087() { checkNotSubtype("null&null","{{any f2} f1}"); }
	@Test public void test_4088() { checkNotSubtype("null&null","{{any f1} f2}"); }
	@Test public void test_4089() { checkNotSubtype("null&null","{{any f2} f2}"); }
	@Test public void test_4090() { checkNotSubtype("null&null","{{null f1} f1}"); }
	@Test public void test_4091() { checkNotSubtype("null&null","{{null f2} f1}"); }
	@Test public void test_4092() { checkNotSubtype("null&null","{{null f1} f2}"); }
	@Test public void test_4093() { checkNotSubtype("null&null","{{null f2} f2}"); }
	@Test public void test_4094() { checkNotSubtype("null&null","{!void f1}"); }
	@Test public void test_4095() { checkNotSubtype("null&null","{!void f2}"); }
	@Test public void test_4096() { checkIsSubtype("null&null","{!any f1}"); }
	@Test public void test_4097() { checkIsSubtype("null&null","{!any f2}"); }
	@Test public void test_4098() { checkNotSubtype("null&null","{!null f1}"); }
	@Test public void test_4099() { checkNotSubtype("null&null","{!null f2}"); }
	@Test public void test_4100() { checkIsSubtype("null&null","void|void"); }
	@Test public void test_4101() { checkNotSubtype("null&null","void|any"); }
	@Test public void test_4102() { checkIsSubtype("null&null","void|null"); }
	@Test public void test_4103() { checkNotSubtype("null&null","any|void"); }
	@Test public void test_4104() { checkNotSubtype("null&null","any|any"); }
	@Test public void test_4105() { checkNotSubtype("null&null","any|null"); }
	@Test public void test_4106() { checkIsSubtype("null&null","null|void"); }
	@Test public void test_4107() { checkNotSubtype("null&null","null|any"); }
	@Test public void test_4108() { checkIsSubtype("null&null","null|null"); }
	@Test public void test_4109() { checkIsSubtype("null&null","{void f1}|void"); }
	@Test public void test_4110() { checkIsSubtype("null&null","{void f2}|void"); }
	@Test public void test_4111() { checkNotSubtype("null&null","{any f1}|any"); }
	@Test public void test_4112() { checkNotSubtype("null&null","{any f2}|any"); }
	@Test public void test_4113() { checkNotSubtype("null&null","{null f1}|null"); }
	@Test public void test_4114() { checkNotSubtype("null&null","{null f2}|null"); }
	@Test public void test_4115() { checkNotSubtype("null&null","!void|void"); }
	@Test public void test_4116() { checkNotSubtype("null&null","!any|any"); }
	@Test public void test_4117() { checkNotSubtype("null&null","!null|null"); }
	@Test public void test_4118() { checkIsSubtype("null&null","void&void"); }
	@Test public void test_4119() { checkIsSubtype("null&null","void&any"); }
	@Test public void test_4120() { checkIsSubtype("null&null","void&null"); }
	@Test public void test_4121() { checkIsSubtype("null&null","any&void"); }
	@Test public void test_4122() { checkNotSubtype("null&null","any&any"); }
	@Test public void test_4123() { checkIsSubtype("null&null","any&null"); }
	@Test public void test_4124() { checkIsSubtype("null&null","null&void"); }
	@Test public void test_4125() { checkIsSubtype("null&null","null&any"); }
	@Test public void test_4126() { checkIsSubtype("null&null","null&null"); }
	@Test public void test_4127() { checkIsSubtype("null&null","{void f1}&void"); }
	@Test public void test_4128() { checkIsSubtype("null&null","{void f2}&void"); }
	@Test public void test_4129() { checkNotSubtype("null&null","{any f1}&any"); }
	@Test public void test_4130() { checkNotSubtype("null&null","{any f2}&any"); }
	@Test public void test_4131() { checkIsSubtype("null&null","{null f1}&null"); }
	@Test public void test_4132() { checkIsSubtype("null&null","{null f2}&null"); }
	@Test public void test_4133() { checkIsSubtype("null&null","!void&void"); }
	@Test public void test_4134() { checkIsSubtype("null&null","!any&any"); }
	@Test public void test_4135() { checkIsSubtype("null&null","!null&null"); }
	@Test public void test_4136() { checkNotSubtype("null&null","!{void f1}"); }
	@Test public void test_4137() { checkNotSubtype("null&null","!{void f2}"); }
	@Test public void test_4138() { checkNotSubtype("null&null","!{any f1}"); }
	@Test public void test_4139() { checkNotSubtype("null&null","!{any f2}"); }
	@Test public void test_4140() { checkNotSubtype("null&null","!{null f1}"); }
	@Test public void test_4141() { checkNotSubtype("null&null","!{null f2}"); }
	@Test public void test_4142() { checkIsSubtype("null&null","!!void"); }
	@Test public void test_4143() { checkNotSubtype("null&null","!!any"); }
	@Test public void test_4144() { checkIsSubtype("null&null","!!null"); }
	@Test public void test_4145() { checkNotSubtype("{void f1}&void","any"); }
	@Test public void test_4146() { checkNotSubtype("{void f1}&void","null"); }
	@Test public void test_4147() { checkIsSubtype("{void f1}&void","{void f1}"); }
	@Test public void test_4148() { checkIsSubtype("{void f1}&void","{void f2}"); }
	@Test public void test_4149() { checkNotSubtype("{void f1}&void","{any f1}"); }
	@Test public void test_4150() { checkNotSubtype("{void f1}&void","{any f2}"); }
	@Test public void test_4151() { checkNotSubtype("{void f1}&void","{null f1}"); }
	@Test public void test_4152() { checkNotSubtype("{void f1}&void","{null f2}"); }
	@Test public void test_4153() { checkNotSubtype("{void f1}&void","!void"); }
	@Test public void test_4154() { checkIsSubtype("{void f1}&void","!any"); }
	@Test public void test_4155() { checkNotSubtype("{void f1}&void","!null"); }
	@Test public void test_4156() { checkIsSubtype("{void f1}&void","{{void f1} f1}"); }
	@Test public void test_4157() { checkIsSubtype("{void f1}&void","{{void f2} f1}"); }
	@Test public void test_4158() { checkIsSubtype("{void f1}&void","{{void f1} f2}"); }
	@Test public void test_4159() { checkIsSubtype("{void f1}&void","{{void f2} f2}"); }
	@Test public void test_4160() { checkNotSubtype("{void f1}&void","{{any f1} f1}"); }
	@Test public void test_4161() { checkNotSubtype("{void f1}&void","{{any f2} f1}"); }
	@Test public void test_4162() { checkNotSubtype("{void f1}&void","{{any f1} f2}"); }
	@Test public void test_4163() { checkNotSubtype("{void f1}&void","{{any f2} f2}"); }
	@Test public void test_4164() { checkNotSubtype("{void f1}&void","{{null f1} f1}"); }
	@Test public void test_4165() { checkNotSubtype("{void f1}&void","{{null f2} f1}"); }
	@Test public void test_4166() { checkNotSubtype("{void f1}&void","{{null f1} f2}"); }
	@Test public void test_4167() { checkNotSubtype("{void f1}&void","{{null f2} f2}"); }
	@Test public void test_4168() { checkNotSubtype("{void f1}&void","{!void f1}"); }
	@Test public void test_4169() { checkNotSubtype("{void f1}&void","{!void f2}"); }
	@Test public void test_4170() { checkIsSubtype("{void f1}&void","{!any f1}"); }
	@Test public void test_4171() { checkIsSubtype("{void f1}&void","{!any f2}"); }
	@Test public void test_4172() { checkNotSubtype("{void f1}&void","{!null f1}"); }
	@Test public void test_4173() { checkNotSubtype("{void f1}&void","{!null f2}"); }
	@Test public void test_4174() { checkIsSubtype("{void f1}&void","void|void"); }
	@Test public void test_4175() { checkNotSubtype("{void f1}&void","void|any"); }
	@Test public void test_4176() { checkNotSubtype("{void f1}&void","void|null"); }
	@Test public void test_4177() { checkNotSubtype("{void f1}&void","any|void"); }
	@Test public void test_4178() { checkNotSubtype("{void f1}&void","any|any"); }
	@Test public void test_4179() { checkNotSubtype("{void f1}&void","any|null"); }
	@Test public void test_4180() { checkNotSubtype("{void f1}&void","null|void"); }
	@Test public void test_4181() { checkNotSubtype("{void f1}&void","null|any"); }
	@Test public void test_4182() { checkNotSubtype("{void f1}&void","null|null"); }
	@Test public void test_4183() { checkIsSubtype("{void f1}&void","{void f1}|void"); }
	@Test public void test_4184() { checkIsSubtype("{void f1}&void","{void f2}|void"); }
	@Test public void test_4185() { checkNotSubtype("{void f1}&void","{any f1}|any"); }
	@Test public void test_4186() { checkNotSubtype("{void f1}&void","{any f2}|any"); }
	@Test public void test_4187() { checkNotSubtype("{void f1}&void","{null f1}|null"); }
	@Test public void test_4188() { checkNotSubtype("{void f1}&void","{null f2}|null"); }
	@Test public void test_4189() { checkNotSubtype("{void f1}&void","!void|void"); }
	@Test public void test_4190() { checkNotSubtype("{void f1}&void","!any|any"); }
	@Test public void test_4191() { checkNotSubtype("{void f1}&void","!null|null"); }
	@Test public void test_4192() { checkIsSubtype("{void f1}&void","void&void"); }
	@Test public void test_4193() { checkIsSubtype("{void f1}&void","void&any"); }
	@Test public void test_4194() { checkIsSubtype("{void f1}&void","void&null"); }
	@Test public void test_4195() { checkIsSubtype("{void f1}&void","any&void"); }
	@Test public void test_4196() { checkNotSubtype("{void f1}&void","any&any"); }
	@Test public void test_4197() { checkNotSubtype("{void f1}&void","any&null"); }
	@Test public void test_4198() { checkIsSubtype("{void f1}&void","null&void"); }
	@Test public void test_4199() { checkNotSubtype("{void f1}&void","null&any"); }
	@Test public void test_4200() { checkNotSubtype("{void f1}&void","null&null"); }
	@Test public void test_4201() { checkIsSubtype("{void f1}&void","{void f1}&void"); }
	@Test public void test_4202() { checkIsSubtype("{void f1}&void","{void f2}&void"); }
	@Test public void test_4203() { checkNotSubtype("{void f1}&void","{any f1}&any"); }
	@Test public void test_4204() { checkNotSubtype("{void f1}&void","{any f2}&any"); }
	@Test public void test_4205() { checkIsSubtype("{void f1}&void","{null f1}&null"); }
	@Test public void test_4206() { checkIsSubtype("{void f1}&void","{null f2}&null"); }
	@Test public void test_4207() { checkIsSubtype("{void f1}&void","!void&void"); }
	@Test public void test_4208() { checkIsSubtype("{void f1}&void","!any&any"); }
	@Test public void test_4209() { checkIsSubtype("{void f1}&void","!null&null"); }
	@Test public void test_4210() { checkNotSubtype("{void f1}&void","!{void f1}"); }
	@Test public void test_4211() { checkNotSubtype("{void f1}&void","!{void f2}"); }
	@Test public void test_4212() { checkNotSubtype("{void f1}&void","!{any f1}"); }
	@Test public void test_4213() { checkNotSubtype("{void f1}&void","!{any f2}"); }
	@Test public void test_4214() { checkNotSubtype("{void f1}&void","!{null f1}"); }
	@Test public void test_4215() { checkNotSubtype("{void f1}&void","!{null f2}"); }
	@Test public void test_4216() { checkIsSubtype("{void f1}&void","!!void"); }
	@Test public void test_4217() { checkNotSubtype("{void f1}&void","!!any"); }
	@Test public void test_4218() { checkNotSubtype("{void f1}&void","!!null"); }
	@Test public void test_4219() { checkNotSubtype("{void f2}&void","any"); }
	@Test public void test_4220() { checkNotSubtype("{void f2}&void","null"); }
	@Test public void test_4221() { checkIsSubtype("{void f2}&void","{void f1}"); }
	@Test public void test_4222() { checkIsSubtype("{void f2}&void","{void f2}"); }
	@Test public void test_4223() { checkNotSubtype("{void f2}&void","{any f1}"); }
	@Test public void test_4224() { checkNotSubtype("{void f2}&void","{any f2}"); }
	@Test public void test_4225() { checkNotSubtype("{void f2}&void","{null f1}"); }
	@Test public void test_4226() { checkNotSubtype("{void f2}&void","{null f2}"); }
	@Test public void test_4227() { checkNotSubtype("{void f2}&void","!void"); }
	@Test public void test_4228() { checkIsSubtype("{void f2}&void","!any"); }
	@Test public void test_4229() { checkNotSubtype("{void f2}&void","!null"); }
	@Test public void test_4230() { checkIsSubtype("{void f2}&void","{{void f1} f1}"); }
	@Test public void test_4231() { checkIsSubtype("{void f2}&void","{{void f2} f1}"); }
	@Test public void test_4232() { checkIsSubtype("{void f2}&void","{{void f1} f2}"); }
	@Test public void test_4233() { checkIsSubtype("{void f2}&void","{{void f2} f2}"); }
	@Test public void test_4234() { checkNotSubtype("{void f2}&void","{{any f1} f1}"); }
	@Test public void test_4235() { checkNotSubtype("{void f2}&void","{{any f2} f1}"); }
	@Test public void test_4236() { checkNotSubtype("{void f2}&void","{{any f1} f2}"); }
	@Test public void test_4237() { checkNotSubtype("{void f2}&void","{{any f2} f2}"); }
	@Test public void test_4238() { checkNotSubtype("{void f2}&void","{{null f1} f1}"); }
	@Test public void test_4239() { checkNotSubtype("{void f2}&void","{{null f2} f1}"); }
	@Test public void test_4240() { checkNotSubtype("{void f2}&void","{{null f1} f2}"); }
	@Test public void test_4241() { checkNotSubtype("{void f2}&void","{{null f2} f2}"); }
	@Test public void test_4242() { checkNotSubtype("{void f2}&void","{!void f1}"); }
	@Test public void test_4243() { checkNotSubtype("{void f2}&void","{!void f2}"); }
	@Test public void test_4244() { checkIsSubtype("{void f2}&void","{!any f1}"); }
	@Test public void test_4245() { checkIsSubtype("{void f2}&void","{!any f2}"); }
	@Test public void test_4246() { checkNotSubtype("{void f2}&void","{!null f1}"); }
	@Test public void test_4247() { checkNotSubtype("{void f2}&void","{!null f2}"); }
	@Test public void test_4248() { checkIsSubtype("{void f2}&void","void|void"); }
	@Test public void test_4249() { checkNotSubtype("{void f2}&void","void|any"); }
	@Test public void test_4250() { checkNotSubtype("{void f2}&void","void|null"); }
	@Test public void test_4251() { checkNotSubtype("{void f2}&void","any|void"); }
	@Test public void test_4252() { checkNotSubtype("{void f2}&void","any|any"); }
	@Test public void test_4253() { checkNotSubtype("{void f2}&void","any|null"); }
	@Test public void test_4254() { checkNotSubtype("{void f2}&void","null|void"); }
	@Test public void test_4255() { checkNotSubtype("{void f2}&void","null|any"); }
	@Test public void test_4256() { checkNotSubtype("{void f2}&void","null|null"); }
	@Test public void test_4257() { checkIsSubtype("{void f2}&void","{void f1}|void"); }
	@Test public void test_4258() { checkIsSubtype("{void f2}&void","{void f2}|void"); }
	@Test public void test_4259() { checkNotSubtype("{void f2}&void","{any f1}|any"); }
	@Test public void test_4260() { checkNotSubtype("{void f2}&void","{any f2}|any"); }
	@Test public void test_4261() { checkNotSubtype("{void f2}&void","{null f1}|null"); }
	@Test public void test_4262() { checkNotSubtype("{void f2}&void","{null f2}|null"); }
	@Test public void test_4263() { checkNotSubtype("{void f2}&void","!void|void"); }
	@Test public void test_4264() { checkNotSubtype("{void f2}&void","!any|any"); }
	@Test public void test_4265() { checkNotSubtype("{void f2}&void","!null|null"); }
	@Test public void test_4266() { checkIsSubtype("{void f2}&void","void&void"); }
	@Test public void test_4267() { checkIsSubtype("{void f2}&void","void&any"); }
	@Test public void test_4268() { checkIsSubtype("{void f2}&void","void&null"); }
	@Test public void test_4269() { checkIsSubtype("{void f2}&void","any&void"); }
	@Test public void test_4270() { checkNotSubtype("{void f2}&void","any&any"); }
	@Test public void test_4271() { checkNotSubtype("{void f2}&void","any&null"); }
	@Test public void test_4272() { checkIsSubtype("{void f2}&void","null&void"); }
	@Test public void test_4273() { checkNotSubtype("{void f2}&void","null&any"); }
	@Test public void test_4274() { checkNotSubtype("{void f2}&void","null&null"); }
	@Test public void test_4275() { checkIsSubtype("{void f2}&void","{void f1}&void"); }
	@Test public void test_4276() { checkIsSubtype("{void f2}&void","{void f2}&void"); }
	@Test public void test_4277() { checkNotSubtype("{void f2}&void","{any f1}&any"); }
	@Test public void test_4278() { checkNotSubtype("{void f2}&void","{any f2}&any"); }
	@Test public void test_4279() { checkIsSubtype("{void f2}&void","{null f1}&null"); }
	@Test public void test_4280() { checkIsSubtype("{void f2}&void","{null f2}&null"); }
	@Test public void test_4281() { checkIsSubtype("{void f2}&void","!void&void"); }
	@Test public void test_4282() { checkIsSubtype("{void f2}&void","!any&any"); }
	@Test public void test_4283() { checkIsSubtype("{void f2}&void","!null&null"); }
	@Test public void test_4284() { checkNotSubtype("{void f2}&void","!{void f1}"); }
	@Test public void test_4285() { checkNotSubtype("{void f2}&void","!{void f2}"); }
	@Test public void test_4286() { checkNotSubtype("{void f2}&void","!{any f1}"); }
	@Test public void test_4287() { checkNotSubtype("{void f2}&void","!{any f2}"); }
	@Test public void test_4288() { checkNotSubtype("{void f2}&void","!{null f1}"); }
	@Test public void test_4289() { checkNotSubtype("{void f2}&void","!{null f2}"); }
	@Test public void test_4290() { checkIsSubtype("{void f2}&void","!!void"); }
	@Test public void test_4291() { checkNotSubtype("{void f2}&void","!!any"); }
	@Test public void test_4292() { checkNotSubtype("{void f2}&void","!!null"); }
	@Test public void test_4293() { checkNotSubtype("{any f1}&any","any"); }
	@Test public void test_4294() { checkNotSubtype("{any f1}&any","null"); }
	@Test public void test_4295() { checkIsSubtype("{any f1}&any","{void f1}"); }
	@Test public void test_4296() { checkIsSubtype("{any f1}&any","{void f2}"); }
	@Test public void test_4297() { checkIsSubtype("{any f1}&any","{any f1}"); }
	@Test public void test_4298() { checkNotSubtype("{any f1}&any","{any f2}"); }
	@Test public void test_4299() { checkIsSubtype("{any f1}&any","{null f1}"); }
	@Test public void test_4300() { checkNotSubtype("{any f1}&any","{null f2}"); }
	@Test public void test_4301() { checkNotSubtype("{any f1}&any","!void"); }
	@Test public void test_4302() { checkIsSubtype("{any f1}&any","!any"); }
	@Test public void test_4303() { checkNotSubtype("{any f1}&any","!null"); }
	@Test public void test_4304() { checkIsSubtype("{any f1}&any","{{void f1} f1}"); }
	@Test public void test_4305() { checkIsSubtype("{any f1}&any","{{void f2} f1}"); }
	@Test public void test_4306() { checkIsSubtype("{any f1}&any","{{void f1} f2}"); }
	@Test public void test_4307() { checkIsSubtype("{any f1}&any","{{void f2} f2}"); }
	@Test public void test_4308() { checkIsSubtype("{any f1}&any","{{any f1} f1}"); }
	@Test public void test_4309() { checkIsSubtype("{any f1}&any","{{any f2} f1}"); }
	@Test public void test_4310() { checkNotSubtype("{any f1}&any","{{any f1} f2}"); }
	@Test public void test_4311() { checkNotSubtype("{any f1}&any","{{any f2} f2}"); }
	@Test public void test_4312() { checkIsSubtype("{any f1}&any","{{null f1} f1}"); }
	@Test public void test_4313() { checkIsSubtype("{any f1}&any","{{null f2} f1}"); }
	@Test public void test_4314() { checkNotSubtype("{any f1}&any","{{null f1} f2}"); }
	@Test public void test_4315() { checkNotSubtype("{any f1}&any","{{null f2} f2}"); }
	@Test public void test_4316() { checkIsSubtype("{any f1}&any","{!void f1}"); }
	@Test public void test_4317() { checkNotSubtype("{any f1}&any","{!void f2}"); }
	@Test public void test_4318() { checkIsSubtype("{any f1}&any","{!any f1}"); }
	@Test public void test_4319() { checkIsSubtype("{any f1}&any","{!any f2}"); }
	@Test public void test_4320() { checkIsSubtype("{any f1}&any","{!null f1}"); }
	@Test public void test_4321() { checkNotSubtype("{any f1}&any","{!null f2}"); }
	@Test public void test_4322() { checkIsSubtype("{any f1}&any","void|void"); }
	@Test public void test_4323() { checkNotSubtype("{any f1}&any","void|any"); }
	@Test public void test_4324() { checkNotSubtype("{any f1}&any","void|null"); }
	@Test public void test_4325() { checkNotSubtype("{any f1}&any","any|void"); }
	@Test public void test_4326() { checkNotSubtype("{any f1}&any","any|any"); }
	@Test public void test_4327() { checkNotSubtype("{any f1}&any","any|null"); }
	@Test public void test_4328() { checkNotSubtype("{any f1}&any","null|void"); }
	@Test public void test_4329() { checkNotSubtype("{any f1}&any","null|any"); }
	@Test public void test_4330() { checkNotSubtype("{any f1}&any","null|null"); }
	@Test public void test_4331() { checkIsSubtype("{any f1}&any","{void f1}|void"); }
	@Test public void test_4332() { checkIsSubtype("{any f1}&any","{void f2}|void"); }
	@Test public void test_4333() { checkNotSubtype("{any f1}&any","{any f1}|any"); }
	@Test public void test_4334() { checkNotSubtype("{any f1}&any","{any f2}|any"); }
	@Test public void test_4335() { checkNotSubtype("{any f1}&any","{null f1}|null"); }
	@Test public void test_4336() { checkNotSubtype("{any f1}&any","{null f2}|null"); }
	@Test public void test_4337() { checkNotSubtype("{any f1}&any","!void|void"); }
	@Test public void test_4338() { checkNotSubtype("{any f1}&any","!any|any"); }
	@Test public void test_4339() { checkNotSubtype("{any f1}&any","!null|null"); }
	@Test public void test_4340() { checkIsSubtype("{any f1}&any","void&void"); }
	@Test public void test_4341() { checkIsSubtype("{any f1}&any","void&any"); }
	@Test public void test_4342() { checkIsSubtype("{any f1}&any","void&null"); }
	@Test public void test_4343() { checkIsSubtype("{any f1}&any","any&void"); }
	@Test public void test_4344() { checkNotSubtype("{any f1}&any","any&any"); }
	@Test public void test_4345() { checkNotSubtype("{any f1}&any","any&null"); }
	@Test public void test_4346() { checkIsSubtype("{any f1}&any","null&void"); }
	@Test public void test_4347() { checkNotSubtype("{any f1}&any","null&any"); }
	@Test public void test_4348() { checkNotSubtype("{any f1}&any","null&null"); }
	@Test public void test_4349() { checkIsSubtype("{any f1}&any","{void f1}&void"); }
	@Test public void test_4350() { checkIsSubtype("{any f1}&any","{void f2}&void"); }
	@Test public void test_4351() { checkIsSubtype("{any f1}&any","{any f1}&any"); }
	@Test public void test_4352() { checkNotSubtype("{any f1}&any","{any f2}&any"); }
	@Test public void test_4353() { checkIsSubtype("{any f1}&any","{null f1}&null"); }
	@Test public void test_4354() { checkIsSubtype("{any f1}&any","{null f2}&null"); }
	@Test public void test_4355() { checkIsSubtype("{any f1}&any","!void&void"); }
	@Test public void test_4356() { checkIsSubtype("{any f1}&any","!any&any"); }
	@Test public void test_4357() { checkIsSubtype("{any f1}&any","!null&null"); }
	@Test public void test_4358() { checkNotSubtype("{any f1}&any","!{void f1}"); }
	@Test public void test_4359() { checkNotSubtype("{any f1}&any","!{void f2}"); }
	@Test public void test_4360() { checkNotSubtype("{any f1}&any","!{any f1}"); }
	@Test public void test_4361() { checkNotSubtype("{any f1}&any","!{any f2}"); }
	@Test public void test_4362() { checkNotSubtype("{any f1}&any","!{null f1}"); }
	@Test public void test_4363() { checkNotSubtype("{any f1}&any","!{null f2}"); }
	@Test public void test_4364() { checkIsSubtype("{any f1}&any","!!void"); }
	@Test public void test_4365() { checkNotSubtype("{any f1}&any","!!any"); }
	@Test public void test_4366() { checkNotSubtype("{any f1}&any","!!null"); }
	@Test public void test_4367() { checkNotSubtype("{any f2}&any","any"); }
	@Test public void test_4368() { checkNotSubtype("{any f2}&any","null"); }
	@Test public void test_4369() { checkIsSubtype("{any f2}&any","{void f1}"); }
	@Test public void test_4370() { checkIsSubtype("{any f2}&any","{void f2}"); }
	@Test public void test_4371() { checkNotSubtype("{any f2}&any","{any f1}"); }
	@Test public void test_4372() { checkIsSubtype("{any f2}&any","{any f2}"); }
	@Test public void test_4373() { checkNotSubtype("{any f2}&any","{null f1}"); }
	@Test public void test_4374() { checkIsSubtype("{any f2}&any","{null f2}"); }
	@Test public void test_4375() { checkNotSubtype("{any f2}&any","!void"); }
	@Test public void test_4376() { checkIsSubtype("{any f2}&any","!any"); }
	@Test public void test_4377() { checkNotSubtype("{any f2}&any","!null"); }
	@Test public void test_4378() { checkIsSubtype("{any f2}&any","{{void f1} f1}"); }
	@Test public void test_4379() { checkIsSubtype("{any f2}&any","{{void f2} f1}"); }
	@Test public void test_4380() { checkIsSubtype("{any f2}&any","{{void f1} f2}"); }
	@Test public void test_4381() { checkIsSubtype("{any f2}&any","{{void f2} f2}"); }
	@Test public void test_4382() { checkNotSubtype("{any f2}&any","{{any f1} f1}"); }
	@Test public void test_4383() { checkNotSubtype("{any f2}&any","{{any f2} f1}"); }
	@Test public void test_4384() { checkIsSubtype("{any f2}&any","{{any f1} f2}"); }
	@Test public void test_4385() { checkIsSubtype("{any f2}&any","{{any f2} f2}"); }
	@Test public void test_4386() { checkNotSubtype("{any f2}&any","{{null f1} f1}"); }
	@Test public void test_4387() { checkNotSubtype("{any f2}&any","{{null f2} f1}"); }
	@Test public void test_4388() { checkIsSubtype("{any f2}&any","{{null f1} f2}"); }
	@Test public void test_4389() { checkIsSubtype("{any f2}&any","{{null f2} f2}"); }
	@Test public void test_4390() { checkNotSubtype("{any f2}&any","{!void f1}"); }
	@Test public void test_4391() { checkIsSubtype("{any f2}&any","{!void f2}"); }
	@Test public void test_4392() { checkIsSubtype("{any f2}&any","{!any f1}"); }
	@Test public void test_4393() { checkIsSubtype("{any f2}&any","{!any f2}"); }
	@Test public void test_4394() { checkNotSubtype("{any f2}&any","{!null f1}"); }
	@Test public void test_4395() { checkIsSubtype("{any f2}&any","{!null f2}"); }
	@Test public void test_4396() { checkIsSubtype("{any f2}&any","void|void"); }
	@Test public void test_4397() { checkNotSubtype("{any f2}&any","void|any"); }
	@Test public void test_4398() { checkNotSubtype("{any f2}&any","void|null"); }
	@Test public void test_4399() { checkNotSubtype("{any f2}&any","any|void"); }
	@Test public void test_4400() { checkNotSubtype("{any f2}&any","any|any"); }
	@Test public void test_4401() { checkNotSubtype("{any f2}&any","any|null"); }
	@Test public void test_4402() { checkNotSubtype("{any f2}&any","null|void"); }
	@Test public void test_4403() { checkNotSubtype("{any f2}&any","null|any"); }
	@Test public void test_4404() { checkNotSubtype("{any f2}&any","null|null"); }
	@Test public void test_4405() { checkIsSubtype("{any f2}&any","{void f1}|void"); }
	@Test public void test_4406() { checkIsSubtype("{any f2}&any","{void f2}|void"); }
	@Test public void test_4407() { checkNotSubtype("{any f2}&any","{any f1}|any"); }
	@Test public void test_4408() { checkNotSubtype("{any f2}&any","{any f2}|any"); }
	@Test public void test_4409() { checkNotSubtype("{any f2}&any","{null f1}|null"); }
	@Test public void test_4410() { checkNotSubtype("{any f2}&any","{null f2}|null"); }
	@Test public void test_4411() { checkNotSubtype("{any f2}&any","!void|void"); }
	@Test public void test_4412() { checkNotSubtype("{any f2}&any","!any|any"); }
	@Test public void test_4413() { checkNotSubtype("{any f2}&any","!null|null"); }
	@Test public void test_4414() { checkIsSubtype("{any f2}&any","void&void"); }
	@Test public void test_4415() { checkIsSubtype("{any f2}&any","void&any"); }
	@Test public void test_4416() { checkIsSubtype("{any f2}&any","void&null"); }
	@Test public void test_4417() { checkIsSubtype("{any f2}&any","any&void"); }
	@Test public void test_4418() { checkNotSubtype("{any f2}&any","any&any"); }
	@Test public void test_4419() { checkNotSubtype("{any f2}&any","any&null"); }
	@Test public void test_4420() { checkIsSubtype("{any f2}&any","null&void"); }
	@Test public void test_4421() { checkNotSubtype("{any f2}&any","null&any"); }
	@Test public void test_4422() { checkNotSubtype("{any f2}&any","null&null"); }
	@Test public void test_4423() { checkIsSubtype("{any f2}&any","{void f1}&void"); }
	@Test public void test_4424() { checkIsSubtype("{any f2}&any","{void f2}&void"); }
	@Test public void test_4425() { checkNotSubtype("{any f2}&any","{any f1}&any"); }
	@Test public void test_4426() { checkIsSubtype("{any f2}&any","{any f2}&any"); }
	@Test public void test_4427() { checkIsSubtype("{any f2}&any","{null f1}&null"); }
	@Test public void test_4428() { checkIsSubtype("{any f2}&any","{null f2}&null"); }
	@Test public void test_4429() { checkIsSubtype("{any f2}&any","!void&void"); }
	@Test public void test_4430() { checkIsSubtype("{any f2}&any","!any&any"); }
	@Test public void test_4431() { checkIsSubtype("{any f2}&any","!null&null"); }
	@Test public void test_4432() { checkNotSubtype("{any f2}&any","!{void f1}"); }
	@Test public void test_4433() { checkNotSubtype("{any f2}&any","!{void f2}"); }
	@Test public void test_4434() { checkNotSubtype("{any f2}&any","!{any f1}"); }
	@Test public void test_4435() { checkNotSubtype("{any f2}&any","!{any f2}"); }
	@Test public void test_4436() { checkNotSubtype("{any f2}&any","!{null f1}"); }
	@Test public void test_4437() { checkNotSubtype("{any f2}&any","!{null f2}"); }
	@Test public void test_4438() { checkIsSubtype("{any f2}&any","!!void"); }
	@Test public void test_4439() { checkNotSubtype("{any f2}&any","!!any"); }
	@Test public void test_4440() { checkNotSubtype("{any f2}&any","!!null"); }
	@Test public void test_4441() { checkNotSubtype("{null f1}&null","any"); }
	@Test public void test_4442() { checkNotSubtype("{null f1}&null","null"); }
	@Test public void test_4443() { checkIsSubtype("{null f1}&null","{void f1}"); }
	@Test public void test_4444() { checkIsSubtype("{null f1}&null","{void f2}"); }
	@Test public void test_4445() { checkNotSubtype("{null f1}&null","{any f1}"); }
	@Test public void test_4446() { checkNotSubtype("{null f1}&null","{any f2}"); }
	@Test public void test_4447() { checkNotSubtype("{null f1}&null","{null f1}"); }
	@Test public void test_4448() { checkNotSubtype("{null f1}&null","{null f2}"); }
	@Test public void test_4449() { checkNotSubtype("{null f1}&null","!void"); }
	@Test public void test_4450() { checkIsSubtype("{null f1}&null","!any"); }
	@Test public void test_4451() { checkNotSubtype("{null f1}&null","!null"); }
	@Test public void test_4452() { checkIsSubtype("{null f1}&null","{{void f1} f1}"); }
	@Test public void test_4453() { checkIsSubtype("{null f1}&null","{{void f2} f1}"); }
	@Test public void test_4454() { checkIsSubtype("{null f1}&null","{{void f1} f2}"); }
	@Test public void test_4455() { checkIsSubtype("{null f1}&null","{{void f2} f2}"); }
	@Test public void test_4456() { checkNotSubtype("{null f1}&null","{{any f1} f1}"); }
	@Test public void test_4457() { checkNotSubtype("{null f1}&null","{{any f2} f1}"); }
	@Test public void test_4458() { checkNotSubtype("{null f1}&null","{{any f1} f2}"); }
	@Test public void test_4459() { checkNotSubtype("{null f1}&null","{{any f2} f2}"); }
	@Test public void test_4460() { checkNotSubtype("{null f1}&null","{{null f1} f1}"); }
	@Test public void test_4461() { checkNotSubtype("{null f1}&null","{{null f2} f1}"); }
	@Test public void test_4462() { checkNotSubtype("{null f1}&null","{{null f1} f2}"); }
	@Test public void test_4463() { checkNotSubtype("{null f1}&null","{{null f2} f2}"); }
	@Test public void test_4464() { checkNotSubtype("{null f1}&null","{!void f1}"); }
	@Test public void test_4465() { checkNotSubtype("{null f1}&null","{!void f2}"); }
	@Test public void test_4466() { checkIsSubtype("{null f1}&null","{!any f1}"); }
	@Test public void test_4467() { checkIsSubtype("{null f1}&null","{!any f2}"); }
	@Test public void test_4468() { checkNotSubtype("{null f1}&null","{!null f1}"); }
	@Test public void test_4469() { checkNotSubtype("{null f1}&null","{!null f2}"); }
	@Test public void test_4470() { checkIsSubtype("{null f1}&null","void|void"); }
	@Test public void test_4471() { checkNotSubtype("{null f1}&null","void|any"); }
	@Test public void test_4472() { checkNotSubtype("{null f1}&null","void|null"); }
	@Test public void test_4473() { checkNotSubtype("{null f1}&null","any|void"); }
	@Test public void test_4474() { checkNotSubtype("{null f1}&null","any|any"); }
	@Test public void test_4475() { checkNotSubtype("{null f1}&null","any|null"); }
	@Test public void test_4476() { checkNotSubtype("{null f1}&null","null|void"); }
	@Test public void test_4477() { checkNotSubtype("{null f1}&null","null|any"); }
	@Test public void test_4478() { checkNotSubtype("{null f1}&null","null|null"); }
	@Test public void test_4479() { checkIsSubtype("{null f1}&null","{void f1}|void"); }
	@Test public void test_4480() { checkIsSubtype("{null f1}&null","{void f2}|void"); }
	@Test public void test_4481() { checkNotSubtype("{null f1}&null","{any f1}|any"); }
	@Test public void test_4482() { checkNotSubtype("{null f1}&null","{any f2}|any"); }
	@Test public void test_4483() { checkNotSubtype("{null f1}&null","{null f1}|null"); }
	@Test public void test_4484() { checkNotSubtype("{null f1}&null","{null f2}|null"); }
	@Test public void test_4485() { checkNotSubtype("{null f1}&null","!void|void"); }
	@Test public void test_4486() { checkNotSubtype("{null f1}&null","!any|any"); }
	@Test public void test_4487() { checkNotSubtype("{null f1}&null","!null|null"); }
	@Test public void test_4488() { checkIsSubtype("{null f1}&null","void&void"); }
	@Test public void test_4489() { checkIsSubtype("{null f1}&null","void&any"); }
	@Test public void test_4490() { checkIsSubtype("{null f1}&null","void&null"); }
	@Test public void test_4491() { checkIsSubtype("{null f1}&null","any&void"); }
	@Test public void test_4492() { checkNotSubtype("{null f1}&null","any&any"); }
	@Test public void test_4493() { checkNotSubtype("{null f1}&null","any&null"); }
	@Test public void test_4494() { checkIsSubtype("{null f1}&null","null&void"); }
	@Test public void test_4495() { checkNotSubtype("{null f1}&null","null&any"); }
	@Test public void test_4496() { checkNotSubtype("{null f1}&null","null&null"); }
	@Test public void test_4497() { checkIsSubtype("{null f1}&null","{void f1}&void"); }
	@Test public void test_4498() { checkIsSubtype("{null f1}&null","{void f2}&void"); }
	@Test public void test_4499() { checkNotSubtype("{null f1}&null","{any f1}&any"); }
	@Test public void test_4500() { checkNotSubtype("{null f1}&null","{any f2}&any"); }
	@Test public void test_4501() { checkIsSubtype("{null f1}&null","{null f1}&null"); }
	@Test public void test_4502() { checkIsSubtype("{null f1}&null","{null f2}&null"); }
	@Test public void test_4503() { checkIsSubtype("{null f1}&null","!void&void"); }
	@Test public void test_4504() { checkIsSubtype("{null f1}&null","!any&any"); }
	@Test public void test_4505() { checkIsSubtype("{null f1}&null","!null&null"); }
	@Test public void test_4506() { checkNotSubtype("{null f1}&null","!{void f1}"); }
	@Test public void test_4507() { checkNotSubtype("{null f1}&null","!{void f2}"); }
	@Test public void test_4508() { checkNotSubtype("{null f1}&null","!{any f1}"); }
	@Test public void test_4509() { checkNotSubtype("{null f1}&null","!{any f2}"); }
	@Test public void test_4510() { checkNotSubtype("{null f1}&null","!{null f1}"); }
	@Test public void test_4511() { checkNotSubtype("{null f1}&null","!{null f2}"); }
	@Test public void test_4512() { checkIsSubtype("{null f1}&null","!!void"); }
	@Test public void test_4513() { checkNotSubtype("{null f1}&null","!!any"); }
	@Test public void test_4514() { checkNotSubtype("{null f1}&null","!!null"); }
	@Test public void test_4515() { checkNotSubtype("{null f2}&null","any"); }
	@Test public void test_4516() { checkNotSubtype("{null f2}&null","null"); }
	@Test public void test_4517() { checkIsSubtype("{null f2}&null","{void f1}"); }
	@Test public void test_4518() { checkIsSubtype("{null f2}&null","{void f2}"); }
	@Test public void test_4519() { checkNotSubtype("{null f2}&null","{any f1}"); }
	@Test public void test_4520() { checkNotSubtype("{null f2}&null","{any f2}"); }
	@Test public void test_4521() { checkNotSubtype("{null f2}&null","{null f1}"); }
	@Test public void test_4522() { checkNotSubtype("{null f2}&null","{null f2}"); }
	@Test public void test_4523() { checkNotSubtype("{null f2}&null","!void"); }
	@Test public void test_4524() { checkIsSubtype("{null f2}&null","!any"); }
	@Test public void test_4525() { checkNotSubtype("{null f2}&null","!null"); }
	@Test public void test_4526() { checkIsSubtype("{null f2}&null","{{void f1} f1}"); }
	@Test public void test_4527() { checkIsSubtype("{null f2}&null","{{void f2} f1}"); }
	@Test public void test_4528() { checkIsSubtype("{null f2}&null","{{void f1} f2}"); }
	@Test public void test_4529() { checkIsSubtype("{null f2}&null","{{void f2} f2}"); }
	@Test public void test_4530() { checkNotSubtype("{null f2}&null","{{any f1} f1}"); }
	@Test public void test_4531() { checkNotSubtype("{null f2}&null","{{any f2} f1}"); }
	@Test public void test_4532() { checkNotSubtype("{null f2}&null","{{any f1} f2}"); }
	@Test public void test_4533() { checkNotSubtype("{null f2}&null","{{any f2} f2}"); }
	@Test public void test_4534() { checkNotSubtype("{null f2}&null","{{null f1} f1}"); }
	@Test public void test_4535() { checkNotSubtype("{null f2}&null","{{null f2} f1}"); }
	@Test public void test_4536() { checkNotSubtype("{null f2}&null","{{null f1} f2}"); }
	@Test public void test_4537() { checkNotSubtype("{null f2}&null","{{null f2} f2}"); }
	@Test public void test_4538() { checkNotSubtype("{null f2}&null","{!void f1}"); }
	@Test public void test_4539() { checkNotSubtype("{null f2}&null","{!void f2}"); }
	@Test public void test_4540() { checkIsSubtype("{null f2}&null","{!any f1}"); }
	@Test public void test_4541() { checkIsSubtype("{null f2}&null","{!any f2}"); }
	@Test public void test_4542() { checkNotSubtype("{null f2}&null","{!null f1}"); }
	@Test public void test_4543() { checkNotSubtype("{null f2}&null","{!null f2}"); }
	@Test public void test_4544() { checkIsSubtype("{null f2}&null","void|void"); }
	@Test public void test_4545() { checkNotSubtype("{null f2}&null","void|any"); }
	@Test public void test_4546() { checkNotSubtype("{null f2}&null","void|null"); }
	@Test public void test_4547() { checkNotSubtype("{null f2}&null","any|void"); }
	@Test public void test_4548() { checkNotSubtype("{null f2}&null","any|any"); }
	@Test public void test_4549() { checkNotSubtype("{null f2}&null","any|null"); }
	@Test public void test_4550() { checkNotSubtype("{null f2}&null","null|void"); }
	@Test public void test_4551() { checkNotSubtype("{null f2}&null","null|any"); }
	@Test public void test_4552() { checkNotSubtype("{null f2}&null","null|null"); }
	@Test public void test_4553() { checkIsSubtype("{null f2}&null","{void f1}|void"); }
	@Test public void test_4554() { checkIsSubtype("{null f2}&null","{void f2}|void"); }
	@Test public void test_4555() { checkNotSubtype("{null f2}&null","{any f1}|any"); }
	@Test public void test_4556() { checkNotSubtype("{null f2}&null","{any f2}|any"); }
	@Test public void test_4557() { checkNotSubtype("{null f2}&null","{null f1}|null"); }
	@Test public void test_4558() { checkNotSubtype("{null f2}&null","{null f2}|null"); }
	@Test public void test_4559() { checkNotSubtype("{null f2}&null","!void|void"); }
	@Test public void test_4560() { checkNotSubtype("{null f2}&null","!any|any"); }
	@Test public void test_4561() { checkNotSubtype("{null f2}&null","!null|null"); }
	@Test public void test_4562() { checkIsSubtype("{null f2}&null","void&void"); }
	@Test public void test_4563() { checkIsSubtype("{null f2}&null","void&any"); }
	@Test public void test_4564() { checkIsSubtype("{null f2}&null","void&null"); }
	@Test public void test_4565() { checkIsSubtype("{null f2}&null","any&void"); }
	@Test public void test_4566() { checkNotSubtype("{null f2}&null","any&any"); }
	@Test public void test_4567() { checkNotSubtype("{null f2}&null","any&null"); }
	@Test public void test_4568() { checkIsSubtype("{null f2}&null","null&void"); }
	@Test public void test_4569() { checkNotSubtype("{null f2}&null","null&any"); }
	@Test public void test_4570() { checkNotSubtype("{null f2}&null","null&null"); }
	@Test public void test_4571() { checkIsSubtype("{null f2}&null","{void f1}&void"); }
	@Test public void test_4572() { checkIsSubtype("{null f2}&null","{void f2}&void"); }
	@Test public void test_4573() { checkNotSubtype("{null f2}&null","{any f1}&any"); }
	@Test public void test_4574() { checkNotSubtype("{null f2}&null","{any f2}&any"); }
	@Test public void test_4575() { checkIsSubtype("{null f2}&null","{null f1}&null"); }
	@Test public void test_4576() { checkIsSubtype("{null f2}&null","{null f2}&null"); }
	@Test public void test_4577() { checkIsSubtype("{null f2}&null","!void&void"); }
	@Test public void test_4578() { checkIsSubtype("{null f2}&null","!any&any"); }
	@Test public void test_4579() { checkIsSubtype("{null f2}&null","!null&null"); }
	@Test public void test_4580() { checkNotSubtype("{null f2}&null","!{void f1}"); }
	@Test public void test_4581() { checkNotSubtype("{null f2}&null","!{void f2}"); }
	@Test public void test_4582() { checkNotSubtype("{null f2}&null","!{any f1}"); }
	@Test public void test_4583() { checkNotSubtype("{null f2}&null","!{any f2}"); }
	@Test public void test_4584() { checkNotSubtype("{null f2}&null","!{null f1}"); }
	@Test public void test_4585() { checkNotSubtype("{null f2}&null","!{null f2}"); }
	@Test public void test_4586() { checkIsSubtype("{null f2}&null","!!void"); }
	@Test public void test_4587() { checkNotSubtype("{null f2}&null","!!any"); }
	@Test public void test_4588() { checkNotSubtype("{null f2}&null","!!null"); }
	@Test public void test_4589() { checkNotSubtype("!void&void","any"); }
	@Test public void test_4590() { checkNotSubtype("!void&void","null"); }
	@Test public void test_4591() { checkIsSubtype("!void&void","{void f1}"); }
	@Test public void test_4592() { checkIsSubtype("!void&void","{void f2}"); }
	@Test public void test_4593() { checkNotSubtype("!void&void","{any f1}"); }
	@Test public void test_4594() { checkNotSubtype("!void&void","{any f2}"); }
	@Test public void test_4595() { checkNotSubtype("!void&void","{null f1}"); }
	@Test public void test_4596() { checkNotSubtype("!void&void","{null f2}"); }
	@Test public void test_4597() { checkNotSubtype("!void&void","!void"); }
	@Test public void test_4598() { checkIsSubtype("!void&void","!any"); }
	@Test public void test_4599() { checkNotSubtype("!void&void","!null"); }
	@Test public void test_4600() { checkIsSubtype("!void&void","{{void f1} f1}"); }
	@Test public void test_4601() { checkIsSubtype("!void&void","{{void f2} f1}"); }
	@Test public void test_4602() { checkIsSubtype("!void&void","{{void f1} f2}"); }
	@Test public void test_4603() { checkIsSubtype("!void&void","{{void f2} f2}"); }
	@Test public void test_4604() { checkNotSubtype("!void&void","{{any f1} f1}"); }
	@Test public void test_4605() { checkNotSubtype("!void&void","{{any f2} f1}"); }
	@Test public void test_4606() { checkNotSubtype("!void&void","{{any f1} f2}"); }
	@Test public void test_4607() { checkNotSubtype("!void&void","{{any f2} f2}"); }
	@Test public void test_4608() { checkNotSubtype("!void&void","{{null f1} f1}"); }
	@Test public void test_4609() { checkNotSubtype("!void&void","{{null f2} f1}"); }
	@Test public void test_4610() { checkNotSubtype("!void&void","{{null f1} f2}"); }
	@Test public void test_4611() { checkNotSubtype("!void&void","{{null f2} f2}"); }
	@Test public void test_4612() { checkNotSubtype("!void&void","{!void f1}"); }
	@Test public void test_4613() { checkNotSubtype("!void&void","{!void f2}"); }
	@Test public void test_4614() { checkIsSubtype("!void&void","{!any f1}"); }
	@Test public void test_4615() { checkIsSubtype("!void&void","{!any f2}"); }
	@Test public void test_4616() { checkNotSubtype("!void&void","{!null f1}"); }
	@Test public void test_4617() { checkNotSubtype("!void&void","{!null f2}"); }
	@Test public void test_4618() { checkIsSubtype("!void&void","void|void"); }
	@Test public void test_4619() { checkNotSubtype("!void&void","void|any"); }
	@Test public void test_4620() { checkNotSubtype("!void&void","void|null"); }
	@Test public void test_4621() { checkNotSubtype("!void&void","any|void"); }
	@Test public void test_4622() { checkNotSubtype("!void&void","any|any"); }
	@Test public void test_4623() { checkNotSubtype("!void&void","any|null"); }
	@Test public void test_4624() { checkNotSubtype("!void&void","null|void"); }
	@Test public void test_4625() { checkNotSubtype("!void&void","null|any"); }
	@Test public void test_4626() { checkNotSubtype("!void&void","null|null"); }
	@Test public void test_4627() { checkIsSubtype("!void&void","{void f1}|void"); }
	@Test public void test_4628() { checkIsSubtype("!void&void","{void f2}|void"); }
	@Test public void test_4629() { checkNotSubtype("!void&void","{any f1}|any"); }
	@Test public void test_4630() { checkNotSubtype("!void&void","{any f2}|any"); }
	@Test public void test_4631() { checkNotSubtype("!void&void","{null f1}|null"); }
	@Test public void test_4632() { checkNotSubtype("!void&void","{null f2}|null"); }
	@Test public void test_4633() { checkNotSubtype("!void&void","!void|void"); }
	@Test public void test_4634() { checkNotSubtype("!void&void","!any|any"); }
	@Test public void test_4635() { checkNotSubtype("!void&void","!null|null"); }
	@Test public void test_4636() { checkIsSubtype("!void&void","void&void"); }
	@Test public void test_4637() { checkIsSubtype("!void&void","void&any"); }
	@Test public void test_4638() { checkIsSubtype("!void&void","void&null"); }
	@Test public void test_4639() { checkIsSubtype("!void&void","any&void"); }
	@Test public void test_4640() { checkNotSubtype("!void&void","any&any"); }
	@Test public void test_4641() { checkNotSubtype("!void&void","any&null"); }
	@Test public void test_4642() { checkIsSubtype("!void&void","null&void"); }
	@Test public void test_4643() { checkNotSubtype("!void&void","null&any"); }
	@Test public void test_4644() { checkNotSubtype("!void&void","null&null"); }
	@Test public void test_4645() { checkIsSubtype("!void&void","{void f1}&void"); }
	@Test public void test_4646() { checkIsSubtype("!void&void","{void f2}&void"); }
	@Test public void test_4647() { checkNotSubtype("!void&void","{any f1}&any"); }
	@Test public void test_4648() { checkNotSubtype("!void&void","{any f2}&any"); }
	@Test public void test_4649() { checkIsSubtype("!void&void","{null f1}&null"); }
	@Test public void test_4650() { checkIsSubtype("!void&void","{null f2}&null"); }
	@Test public void test_4651() { checkIsSubtype("!void&void","!void&void"); }
	@Test public void test_4652() { checkIsSubtype("!void&void","!any&any"); }
	@Test public void test_4653() { checkIsSubtype("!void&void","!null&null"); }
	@Test public void test_4654() { checkNotSubtype("!void&void","!{void f1}"); }
	@Test public void test_4655() { checkNotSubtype("!void&void","!{void f2}"); }
	@Test public void test_4656() { checkNotSubtype("!void&void","!{any f1}"); }
	@Test public void test_4657() { checkNotSubtype("!void&void","!{any f2}"); }
	@Test public void test_4658() { checkNotSubtype("!void&void","!{null f1}"); }
	@Test public void test_4659() { checkNotSubtype("!void&void","!{null f2}"); }
	@Test public void test_4660() { checkIsSubtype("!void&void","!!void"); }
	@Test public void test_4661() { checkNotSubtype("!void&void","!!any"); }
	@Test public void test_4662() { checkNotSubtype("!void&void","!!null"); }
	@Test public void test_4663() { checkNotSubtype("!any&any","any"); }
	@Test public void test_4664() { checkNotSubtype("!any&any","null"); }
	@Test public void test_4665() { checkIsSubtype("!any&any","{void f1}"); }
	@Test public void test_4666() { checkIsSubtype("!any&any","{void f2}"); }
	@Test public void test_4667() { checkNotSubtype("!any&any","{any f1}"); }
	@Test public void test_4668() { checkNotSubtype("!any&any","{any f2}"); }
	@Test public void test_4669() { checkNotSubtype("!any&any","{null f1}"); }
	@Test public void test_4670() { checkNotSubtype("!any&any","{null f2}"); }
	@Test public void test_4671() { checkNotSubtype("!any&any","!void"); }
	@Test public void test_4672() { checkIsSubtype("!any&any","!any"); }
	@Test public void test_4673() { checkNotSubtype("!any&any","!null"); }
	@Test public void test_4674() { checkIsSubtype("!any&any","{{void f1} f1}"); }
	@Test public void test_4675() { checkIsSubtype("!any&any","{{void f2} f1}"); }
	@Test public void test_4676() { checkIsSubtype("!any&any","{{void f1} f2}"); }
	@Test public void test_4677() { checkIsSubtype("!any&any","{{void f2} f2}"); }
	@Test public void test_4678() { checkNotSubtype("!any&any","{{any f1} f1}"); }
	@Test public void test_4679() { checkNotSubtype("!any&any","{{any f2} f1}"); }
	@Test public void test_4680() { checkNotSubtype("!any&any","{{any f1} f2}"); }
	@Test public void test_4681() { checkNotSubtype("!any&any","{{any f2} f2}"); }
	@Test public void test_4682() { checkNotSubtype("!any&any","{{null f1} f1}"); }
	@Test public void test_4683() { checkNotSubtype("!any&any","{{null f2} f1}"); }
	@Test public void test_4684() { checkNotSubtype("!any&any","{{null f1} f2}"); }
	@Test public void test_4685() { checkNotSubtype("!any&any","{{null f2} f2}"); }
	@Test public void test_4686() { checkNotSubtype("!any&any","{!void f1}"); }
	@Test public void test_4687() { checkNotSubtype("!any&any","{!void f2}"); }
	@Test public void test_4688() { checkIsSubtype("!any&any","{!any f1}"); }
	@Test public void test_4689() { checkIsSubtype("!any&any","{!any f2}"); }
	@Test public void test_4690() { checkNotSubtype("!any&any","{!null f1}"); }
	@Test public void test_4691() { checkNotSubtype("!any&any","{!null f2}"); }
	@Test public void test_4692() { checkIsSubtype("!any&any","void|void"); }
	@Test public void test_4693() { checkNotSubtype("!any&any","void|any"); }
	@Test public void test_4694() { checkNotSubtype("!any&any","void|null"); }
	@Test public void test_4695() { checkNotSubtype("!any&any","any|void"); }
	@Test public void test_4696() { checkNotSubtype("!any&any","any|any"); }
	@Test public void test_4697() { checkNotSubtype("!any&any","any|null"); }
	@Test public void test_4698() { checkNotSubtype("!any&any","null|void"); }
	@Test public void test_4699() { checkNotSubtype("!any&any","null|any"); }
	@Test public void test_4700() { checkNotSubtype("!any&any","null|null"); }
	@Test public void test_4701() { checkIsSubtype("!any&any","{void f1}|void"); }
	@Test public void test_4702() { checkIsSubtype("!any&any","{void f2}|void"); }
	@Test public void test_4703() { checkNotSubtype("!any&any","{any f1}|any"); }
	@Test public void test_4704() { checkNotSubtype("!any&any","{any f2}|any"); }
	@Test public void test_4705() { checkNotSubtype("!any&any","{null f1}|null"); }
	@Test public void test_4706() { checkNotSubtype("!any&any","{null f2}|null"); }
	@Test public void test_4707() { checkNotSubtype("!any&any","!void|void"); }
	@Test public void test_4708() { checkNotSubtype("!any&any","!any|any"); }
	@Test public void test_4709() { checkNotSubtype("!any&any","!null|null"); }
	@Test public void test_4710() { checkIsSubtype("!any&any","void&void"); }
	@Test public void test_4711() { checkIsSubtype("!any&any","void&any"); }
	@Test public void test_4712() { checkIsSubtype("!any&any","void&null"); }
	@Test public void test_4713() { checkIsSubtype("!any&any","any&void"); }
	@Test public void test_4714() { checkNotSubtype("!any&any","any&any"); }
	@Test public void test_4715() { checkNotSubtype("!any&any","any&null"); }
	@Test public void test_4716() { checkIsSubtype("!any&any","null&void"); }
	@Test public void test_4717() { checkNotSubtype("!any&any","null&any"); }
	@Test public void test_4718() { checkNotSubtype("!any&any","null&null"); }
	@Test public void test_4719() { checkIsSubtype("!any&any","{void f1}&void"); }
	@Test public void test_4720() { checkIsSubtype("!any&any","{void f2}&void"); }
	@Test public void test_4721() { checkNotSubtype("!any&any","{any f1}&any"); }
	@Test public void test_4722() { checkNotSubtype("!any&any","{any f2}&any"); }
	@Test public void test_4723() { checkIsSubtype("!any&any","{null f1}&null"); }
	@Test public void test_4724() { checkIsSubtype("!any&any","{null f2}&null"); }
	@Test public void test_4725() { checkIsSubtype("!any&any","!void&void"); }
	@Test public void test_4726() { checkIsSubtype("!any&any","!any&any"); }
	@Test public void test_4727() { checkIsSubtype("!any&any","!null&null"); }
	@Test public void test_4728() { checkNotSubtype("!any&any","!{void f1}"); }
	@Test public void test_4729() { checkNotSubtype("!any&any","!{void f2}"); }
	@Test public void test_4730() { checkNotSubtype("!any&any","!{any f1}"); }
	@Test public void test_4731() { checkNotSubtype("!any&any","!{any f2}"); }
	@Test public void test_4732() { checkNotSubtype("!any&any","!{null f1}"); }
	@Test public void test_4733() { checkNotSubtype("!any&any","!{null f2}"); }
	@Test public void test_4734() { checkIsSubtype("!any&any","!!void"); }
	@Test public void test_4735() { checkNotSubtype("!any&any","!!any"); }
	@Test public void test_4736() { checkNotSubtype("!any&any","!!null"); }
	@Test public void test_4737() { checkNotSubtype("!null&null","any"); }
	@Test public void test_4738() { checkNotSubtype("!null&null","null"); }
	@Test public void test_4739() { checkIsSubtype("!null&null","{void f1}"); }
	@Test public void test_4740() { checkIsSubtype("!null&null","{void f2}"); }
	@Test public void test_4741() { checkNotSubtype("!null&null","{any f1}"); }
	@Test public void test_4742() { checkNotSubtype("!null&null","{any f2}"); }
	@Test public void test_4743() { checkNotSubtype("!null&null","{null f1}"); }
	@Test public void test_4744() { checkNotSubtype("!null&null","{null f2}"); }
	@Test public void test_4745() { checkNotSubtype("!null&null","!void"); }
	@Test public void test_4746() { checkIsSubtype("!null&null","!any"); }
	@Test public void test_4747() { checkNotSubtype("!null&null","!null"); }
	@Test public void test_4748() { checkIsSubtype("!null&null","{{void f1} f1}"); }
	@Test public void test_4749() { checkIsSubtype("!null&null","{{void f2} f1}"); }
	@Test public void test_4750() { checkIsSubtype("!null&null","{{void f1} f2}"); }
	@Test public void test_4751() { checkIsSubtype("!null&null","{{void f2} f2}"); }
	@Test public void test_4752() { checkNotSubtype("!null&null","{{any f1} f1}"); }
	@Test public void test_4753() { checkNotSubtype("!null&null","{{any f2} f1}"); }
	@Test public void test_4754() { checkNotSubtype("!null&null","{{any f1} f2}"); }
	@Test public void test_4755() { checkNotSubtype("!null&null","{{any f2} f2}"); }
	@Test public void test_4756() { checkNotSubtype("!null&null","{{null f1} f1}"); }
	@Test public void test_4757() { checkNotSubtype("!null&null","{{null f2} f1}"); }
	@Test public void test_4758() { checkNotSubtype("!null&null","{{null f1} f2}"); }
	@Test public void test_4759() { checkNotSubtype("!null&null","{{null f2} f2}"); }
	@Test public void test_4760() { checkNotSubtype("!null&null","{!void f1}"); }
	@Test public void test_4761() { checkNotSubtype("!null&null","{!void f2}"); }
	@Test public void test_4762() { checkIsSubtype("!null&null","{!any f1}"); }
	@Test public void test_4763() { checkIsSubtype("!null&null","{!any f2}"); }
	@Test public void test_4764() { checkNotSubtype("!null&null","{!null f1}"); }
	@Test public void test_4765() { checkNotSubtype("!null&null","{!null f2}"); }
	@Test public void test_4766() { checkIsSubtype("!null&null","void|void"); }
	@Test public void test_4767() { checkNotSubtype("!null&null","void|any"); }
	@Test public void test_4768() { checkNotSubtype("!null&null","void|null"); }
	@Test public void test_4769() { checkNotSubtype("!null&null","any|void"); }
	@Test public void test_4770() { checkNotSubtype("!null&null","any|any"); }
	@Test public void test_4771() { checkNotSubtype("!null&null","any|null"); }
	@Test public void test_4772() { checkNotSubtype("!null&null","null|void"); }
	@Test public void test_4773() { checkNotSubtype("!null&null","null|any"); }
	@Test public void test_4774() { checkNotSubtype("!null&null","null|null"); }
	@Test public void test_4775() { checkIsSubtype("!null&null","{void f1}|void"); }
	@Test public void test_4776() { checkIsSubtype("!null&null","{void f2}|void"); }
	@Test public void test_4777() { checkNotSubtype("!null&null","{any f1}|any"); }
	@Test public void test_4778() { checkNotSubtype("!null&null","{any f2}|any"); }
	@Test public void test_4779() { checkNotSubtype("!null&null","{null f1}|null"); }
	@Test public void test_4780() { checkNotSubtype("!null&null","{null f2}|null"); }
	@Test public void test_4781() { checkNotSubtype("!null&null","!void|void"); }
	@Test public void test_4782() { checkNotSubtype("!null&null","!any|any"); }
	@Test public void test_4783() { checkNotSubtype("!null&null","!null|null"); }
	@Test public void test_4784() { checkIsSubtype("!null&null","void&void"); }
	@Test public void test_4785() { checkIsSubtype("!null&null","void&any"); }
	@Test public void test_4786() { checkIsSubtype("!null&null","void&null"); }
	@Test public void test_4787() { checkIsSubtype("!null&null","any&void"); }
	@Test public void test_4788() { checkNotSubtype("!null&null","any&any"); }
	@Test public void test_4789() { checkNotSubtype("!null&null","any&null"); }
	@Test public void test_4790() { checkIsSubtype("!null&null","null&void"); }
	@Test public void test_4791() { checkNotSubtype("!null&null","null&any"); }
	@Test public void test_4792() { checkNotSubtype("!null&null","null&null"); }
	@Test public void test_4793() { checkIsSubtype("!null&null","{void f1}&void"); }
	@Test public void test_4794() { checkIsSubtype("!null&null","{void f2}&void"); }
	@Test public void test_4795() { checkNotSubtype("!null&null","{any f1}&any"); }
	@Test public void test_4796() { checkNotSubtype("!null&null","{any f2}&any"); }
	@Test public void test_4797() { checkIsSubtype("!null&null","{null f1}&null"); }
	@Test public void test_4798() { checkIsSubtype("!null&null","{null f2}&null"); }
	@Test public void test_4799() { checkIsSubtype("!null&null","!void&void"); }
	@Test public void test_4800() { checkIsSubtype("!null&null","!any&any"); }
	@Test public void test_4801() { checkIsSubtype("!null&null","!null&null"); }
	@Test public void test_4802() { checkNotSubtype("!null&null","!{void f1}"); }
	@Test public void test_4803() { checkNotSubtype("!null&null","!{void f2}"); }
	@Test public void test_4804() { checkNotSubtype("!null&null","!{any f1}"); }
	@Test public void test_4805() { checkNotSubtype("!null&null","!{any f2}"); }
	@Test public void test_4806() { checkNotSubtype("!null&null","!{null f1}"); }
	@Test public void test_4807() { checkNotSubtype("!null&null","!{null f2}"); }
	@Test public void test_4808() { checkIsSubtype("!null&null","!!void"); }
	@Test public void test_4809() { checkNotSubtype("!null&null","!!any"); }
	@Test public void test_4810() { checkNotSubtype("!null&null","!!null"); }
	@Test public void test_4811() { checkIsSubtype("!{void f1}","any"); }
	@Test public void test_4812() { checkIsSubtype("!{void f1}","null"); }
	@Test public void test_4813() { checkIsSubtype("!{void f1}","{void f1}"); }
	@Test public void test_4814() { checkIsSubtype("!{void f1}","{void f2}"); }
	@Test public void test_4815() { checkIsSubtype("!{void f1}","{any f1}"); }
	@Test public void test_4816() { checkIsSubtype("!{void f1}","{any f2}"); }
	@Test public void test_4817() { checkIsSubtype("!{void f1}","{null f1}"); }
	@Test public void test_4818() { checkIsSubtype("!{void f1}","{null f2}"); }
	@Test public void test_4819() { checkIsSubtype("!{void f1}","!void"); }
	@Test public void test_4820() { checkIsSubtype("!{void f1}","!any"); }
	@Test public void test_4821() { checkIsSubtype("!{void f1}","!null"); }
	@Test public void test_4822() { checkIsSubtype("!{void f1}","{{void f1} f1}"); }
	@Test public void test_4823() { checkIsSubtype("!{void f1}","{{void f2} f1}"); }
	@Test public void test_4824() { checkIsSubtype("!{void f1}","{{void f1} f2}"); }
	@Test public void test_4825() { checkIsSubtype("!{void f1}","{{void f2} f2}"); }
	@Test public void test_4826() { checkIsSubtype("!{void f1}","{{any f1} f1}"); }
	@Test public void test_4827() { checkIsSubtype("!{void f1}","{{any f2} f1}"); }
	@Test public void test_4828() { checkIsSubtype("!{void f1}","{{any f1} f2}"); }
	@Test public void test_4829() { checkIsSubtype("!{void f1}","{{any f2} f2}"); }
	@Test public void test_4830() { checkIsSubtype("!{void f1}","{{null f1} f1}"); }
	@Test public void test_4831() { checkIsSubtype("!{void f1}","{{null f2} f1}"); }
	@Test public void test_4832() { checkIsSubtype("!{void f1}","{{null f1} f2}"); }
	@Test public void test_4833() { checkIsSubtype("!{void f1}","{{null f2} f2}"); }
	@Test public void test_4834() { checkIsSubtype("!{void f1}","{!void f1}"); }
	@Test public void test_4835() { checkIsSubtype("!{void f1}","{!void f2}"); }
	@Test public void test_4836() { checkIsSubtype("!{void f1}","{!any f1}"); }
	@Test public void test_4837() { checkIsSubtype("!{void f1}","{!any f2}"); }
	@Test public void test_4838() { checkIsSubtype("!{void f1}","{!null f1}"); }
	@Test public void test_4839() { checkIsSubtype("!{void f1}","{!null f2}"); }
	@Test public void test_4840() { checkIsSubtype("!{void f1}","void|void"); }
	@Test public void test_4841() { checkIsSubtype("!{void f1}","void|any"); }
	@Test public void test_4842() { checkIsSubtype("!{void f1}","void|null"); }
	@Test public void test_4843() { checkIsSubtype("!{void f1}","any|void"); }
	@Test public void test_4844() { checkIsSubtype("!{void f1}","any|any"); }
	@Test public void test_4845() { checkIsSubtype("!{void f1}","any|null"); }
	@Test public void test_4846() { checkIsSubtype("!{void f1}","null|void"); }
	@Test public void test_4847() { checkIsSubtype("!{void f1}","null|any"); }
	@Test public void test_4848() { checkIsSubtype("!{void f1}","null|null"); }
	@Test public void test_4849() { checkIsSubtype("!{void f1}","{void f1}|void"); }
	@Test public void test_4850() { checkIsSubtype("!{void f1}","{void f2}|void"); }
	@Test public void test_4851() { checkIsSubtype("!{void f1}","{any f1}|any"); }
	@Test public void test_4852() { checkIsSubtype("!{void f1}","{any f2}|any"); }
	@Test public void test_4853() { checkIsSubtype("!{void f1}","{null f1}|null"); }
	@Test public void test_4854() { checkIsSubtype("!{void f1}","{null f2}|null"); }
	@Test public void test_4855() { checkIsSubtype("!{void f1}","!void|void"); }
	@Test public void test_4856() { checkIsSubtype("!{void f1}","!any|any"); }
	@Test public void test_4857() { checkIsSubtype("!{void f1}","!null|null"); }
	@Test public void test_4858() { checkIsSubtype("!{void f1}","void&void"); }
	@Test public void test_4859() { checkIsSubtype("!{void f1}","void&any"); }
	@Test public void test_4860() { checkIsSubtype("!{void f1}","void&null"); }
	@Test public void test_4861() { checkIsSubtype("!{void f1}","any&void"); }
	@Test public void test_4862() { checkIsSubtype("!{void f1}","any&any"); }
	@Test public void test_4863() { checkIsSubtype("!{void f1}","any&null"); }
	@Test public void test_4864() { checkIsSubtype("!{void f1}","null&void"); }
	@Test public void test_4865() { checkIsSubtype("!{void f1}","null&any"); }
	@Test public void test_4866() { checkIsSubtype("!{void f1}","null&null"); }
	@Test public void test_4867() { checkIsSubtype("!{void f1}","{void f1}&void"); }
	@Test public void test_4868() { checkIsSubtype("!{void f1}","{void f2}&void"); }
	@Test public void test_4869() { checkIsSubtype("!{void f1}","{any f1}&any"); }
	@Test public void test_4870() { checkIsSubtype("!{void f1}","{any f2}&any"); }
	@Test public void test_4871() { checkIsSubtype("!{void f1}","{null f1}&null"); }
	@Test public void test_4872() { checkIsSubtype("!{void f1}","{null f2}&null"); }
	@Test public void test_4873() { checkIsSubtype("!{void f1}","!void&void"); }
	@Test public void test_4874() { checkIsSubtype("!{void f1}","!any&any"); }
	@Test public void test_4875() { checkIsSubtype("!{void f1}","!null&null"); }
	@Test public void test_4876() { checkIsSubtype("!{void f1}","!{void f1}"); }
	@Test public void test_4877() { checkIsSubtype("!{void f1}","!{void f2}"); }
	@Test public void test_4878() { checkIsSubtype("!{void f1}","!{any f1}"); }
	@Test public void test_4879() { checkIsSubtype("!{void f1}","!{any f2}"); }
	@Test public void test_4880() { checkIsSubtype("!{void f1}","!{null f1}"); }
	@Test public void test_4881() { checkIsSubtype("!{void f1}","!{null f2}"); }
	@Test public void test_4882() { checkIsSubtype("!{void f1}","!!void"); }
	@Test public void test_4883() { checkIsSubtype("!{void f1}","!!any"); }
	@Test public void test_4884() { checkIsSubtype("!{void f1}","!!null"); }
	@Test public void test_4885() { checkIsSubtype("!{void f2}","any"); }
	@Test public void test_4886() { checkIsSubtype("!{void f2}","null"); }
	@Test public void test_4887() { checkIsSubtype("!{void f2}","{void f1}"); }
	@Test public void test_4888() { checkIsSubtype("!{void f2}","{void f2}"); }
	@Test public void test_4889() { checkIsSubtype("!{void f2}","{any f1}"); }
	@Test public void test_4890() { checkIsSubtype("!{void f2}","{any f2}"); }
	@Test public void test_4891() { checkIsSubtype("!{void f2}","{null f1}"); }
	@Test public void test_4892() { checkIsSubtype("!{void f2}","{null f2}"); }
	@Test public void test_4893() { checkIsSubtype("!{void f2}","!void"); }
	@Test public void test_4894() { checkIsSubtype("!{void f2}","!any"); }
	@Test public void test_4895() { checkIsSubtype("!{void f2}","!null"); }
	@Test public void test_4896() { checkIsSubtype("!{void f2}","{{void f1} f1}"); }
	@Test public void test_4897() { checkIsSubtype("!{void f2}","{{void f2} f1}"); }
	@Test public void test_4898() { checkIsSubtype("!{void f2}","{{void f1} f2}"); }
	@Test public void test_4899() { checkIsSubtype("!{void f2}","{{void f2} f2}"); }
	@Test public void test_4900() { checkIsSubtype("!{void f2}","{{any f1} f1}"); }
	@Test public void test_4901() { checkIsSubtype("!{void f2}","{{any f2} f1}"); }
	@Test public void test_4902() { checkIsSubtype("!{void f2}","{{any f1} f2}"); }
	@Test public void test_4903() { checkIsSubtype("!{void f2}","{{any f2} f2}"); }
	@Test public void test_4904() { checkIsSubtype("!{void f2}","{{null f1} f1}"); }
	@Test public void test_4905() { checkIsSubtype("!{void f2}","{{null f2} f1}"); }
	@Test public void test_4906() { checkIsSubtype("!{void f2}","{{null f1} f2}"); }
	@Test public void test_4907() { checkIsSubtype("!{void f2}","{{null f2} f2}"); }
	@Test public void test_4908() { checkIsSubtype("!{void f2}","{!void f1}"); }
	@Test public void test_4909() { checkIsSubtype("!{void f2}","{!void f2}"); }
	@Test public void test_4910() { checkIsSubtype("!{void f2}","{!any f1}"); }
	@Test public void test_4911() { checkIsSubtype("!{void f2}","{!any f2}"); }
	@Test public void test_4912() { checkIsSubtype("!{void f2}","{!null f1}"); }
	@Test public void test_4913() { checkIsSubtype("!{void f2}","{!null f2}"); }
	@Test public void test_4914() { checkIsSubtype("!{void f2}","void|void"); }
	@Test public void test_4915() { checkIsSubtype("!{void f2}","void|any"); }
	@Test public void test_4916() { checkIsSubtype("!{void f2}","void|null"); }
	@Test public void test_4917() { checkIsSubtype("!{void f2}","any|void"); }
	@Test public void test_4918() { checkIsSubtype("!{void f2}","any|any"); }
	@Test public void test_4919() { checkIsSubtype("!{void f2}","any|null"); }
	@Test public void test_4920() { checkIsSubtype("!{void f2}","null|void"); }
	@Test public void test_4921() { checkIsSubtype("!{void f2}","null|any"); }
	@Test public void test_4922() { checkIsSubtype("!{void f2}","null|null"); }
	@Test public void test_4923() { checkIsSubtype("!{void f2}","{void f1}|void"); }
	@Test public void test_4924() { checkIsSubtype("!{void f2}","{void f2}|void"); }
	@Test public void test_4925() { checkIsSubtype("!{void f2}","{any f1}|any"); }
	@Test public void test_4926() { checkIsSubtype("!{void f2}","{any f2}|any"); }
	@Test public void test_4927() { checkIsSubtype("!{void f2}","{null f1}|null"); }
	@Test public void test_4928() { checkIsSubtype("!{void f2}","{null f2}|null"); }
	@Test public void test_4929() { checkIsSubtype("!{void f2}","!void|void"); }
	@Test public void test_4930() { checkIsSubtype("!{void f2}","!any|any"); }
	@Test public void test_4931() { checkIsSubtype("!{void f2}","!null|null"); }
	@Test public void test_4932() { checkIsSubtype("!{void f2}","void&void"); }
	@Test public void test_4933() { checkIsSubtype("!{void f2}","void&any"); }
	@Test public void test_4934() { checkIsSubtype("!{void f2}","void&null"); }
	@Test public void test_4935() { checkIsSubtype("!{void f2}","any&void"); }
	@Test public void test_4936() { checkIsSubtype("!{void f2}","any&any"); }
	@Test public void test_4937() { checkIsSubtype("!{void f2}","any&null"); }
	@Test public void test_4938() { checkIsSubtype("!{void f2}","null&void"); }
	@Test public void test_4939() { checkIsSubtype("!{void f2}","null&any"); }
	@Test public void test_4940() { checkIsSubtype("!{void f2}","null&null"); }
	@Test public void test_4941() { checkIsSubtype("!{void f2}","{void f1}&void"); }
	@Test public void test_4942() { checkIsSubtype("!{void f2}","{void f2}&void"); }
	@Test public void test_4943() { checkIsSubtype("!{void f2}","{any f1}&any"); }
	@Test public void test_4944() { checkIsSubtype("!{void f2}","{any f2}&any"); }
	@Test public void test_4945() { checkIsSubtype("!{void f2}","{null f1}&null"); }
	@Test public void test_4946() { checkIsSubtype("!{void f2}","{null f2}&null"); }
	@Test public void test_4947() { checkIsSubtype("!{void f2}","!void&void"); }
	@Test public void test_4948() { checkIsSubtype("!{void f2}","!any&any"); }
	@Test public void test_4949() { checkIsSubtype("!{void f2}","!null&null"); }
	@Test public void test_4950() { checkIsSubtype("!{void f2}","!{void f1}"); }
	@Test public void test_4951() { checkIsSubtype("!{void f2}","!{void f2}"); }
	@Test public void test_4952() { checkIsSubtype("!{void f2}","!{any f1}"); }
	@Test public void test_4953() { checkIsSubtype("!{void f2}","!{any f2}"); }
	@Test public void test_4954() { checkIsSubtype("!{void f2}","!{null f1}"); }
	@Test public void test_4955() { checkIsSubtype("!{void f2}","!{null f2}"); }
	@Test public void test_4956() { checkIsSubtype("!{void f2}","!!void"); }
	@Test public void test_4957() { checkIsSubtype("!{void f2}","!!any"); }
	@Test public void test_4958() { checkIsSubtype("!{void f2}","!!null"); }
	@Test public void test_4959() { checkNotSubtype("!{any f1}","any"); }
	@Test public void test_4960() { checkIsSubtype("!{any f1}","null"); }
	@Test public void test_4961() { checkIsSubtype("!{any f1}","{void f1}"); }
	@Test public void test_4962() { checkIsSubtype("!{any f1}","{void f2}"); }
	@Test public void test_4963() { checkNotSubtype("!{any f1}","{any f1}"); }
	@Test public void test_4964() { checkIsSubtype("!{any f1}","{any f2}"); }
	@Test public void test_4965() { checkNotSubtype("!{any f1}","{null f1}"); }
	@Test public void test_4966() { checkIsSubtype("!{any f1}","{null f2}"); }
	@Test public void test_4967() { checkNotSubtype("!{any f1}","!void"); }
	@Test public void test_4968() { checkIsSubtype("!{any f1}","!any"); }
	@Test public void test_4969() { checkNotSubtype("!{any f1}","!null"); }
	@Test public void test_4970() { checkIsSubtype("!{any f1}","{{void f1} f1}"); }
	@Test public void test_4971() { checkIsSubtype("!{any f1}","{{void f2} f1}"); }
	@Test public void test_4972() { checkIsSubtype("!{any f1}","{{void f1} f2}"); }
	@Test public void test_4973() { checkIsSubtype("!{any f1}","{{void f2} f2}"); }
	@Test public void test_4974() { checkNotSubtype("!{any f1}","{{any f1} f1}"); }
	@Test public void test_4975() { checkNotSubtype("!{any f1}","{{any f2} f1}"); }
	@Test public void test_4976() { checkIsSubtype("!{any f1}","{{any f1} f2}"); }
	@Test public void test_4977() { checkIsSubtype("!{any f1}","{{any f2} f2}"); }
	@Test public void test_4978() { checkNotSubtype("!{any f1}","{{null f1} f1}"); }
	@Test public void test_4979() { checkNotSubtype("!{any f1}","{{null f2} f1}"); }
	@Test public void test_4980() { checkIsSubtype("!{any f1}","{{null f1} f2}"); }
	@Test public void test_4981() { checkIsSubtype("!{any f1}","{{null f2} f2}"); }
	@Test public void test_4982() { checkNotSubtype("!{any f1}","{!void f1}"); }
	@Test public void test_4983() { checkIsSubtype("!{any f1}","{!void f2}"); }
	@Test public void test_4984() { checkIsSubtype("!{any f1}","{!any f1}"); }
	@Test public void test_4985() { checkIsSubtype("!{any f1}","{!any f2}"); }
	@Test public void test_4986() { checkNotSubtype("!{any f1}","{!null f1}"); }
	@Test public void test_4987() { checkIsSubtype("!{any f1}","{!null f2}"); }
	@Test public void test_4988() { checkIsSubtype("!{any f1}","void|void"); }
	@Test public void test_4989() { checkNotSubtype("!{any f1}","void|any"); }
	@Test public void test_4990() { checkIsSubtype("!{any f1}","void|null"); }
	@Test public void test_4991() { checkNotSubtype("!{any f1}","any|void"); }
	@Test public void test_4992() { checkNotSubtype("!{any f1}","any|any"); }
	@Test public void test_4993() { checkNotSubtype("!{any f1}","any|null"); }
	@Test public void test_4994() { checkIsSubtype("!{any f1}","null|void"); }
	@Test public void test_4995() { checkNotSubtype("!{any f1}","null|any"); }
	@Test public void test_4996() { checkIsSubtype("!{any f1}","null|null"); }
	@Test public void test_4997() { checkIsSubtype("!{any f1}","{void f1}|void"); }
	@Test public void test_4998() { checkIsSubtype("!{any f1}","{void f2}|void"); }
	@Test public void test_4999() { checkNotSubtype("!{any f1}","{any f1}|any"); }
	@Test public void test_5000() { checkNotSubtype("!{any f1}","{any f2}|any"); }
	@Test public void test_5001() { checkNotSubtype("!{any f1}","{null f1}|null"); }
	@Test public void test_5002() { checkIsSubtype("!{any f1}","{null f2}|null"); }
	@Test public void test_5003() { checkNotSubtype("!{any f1}","!void|void"); }
	@Test public void test_5004() { checkNotSubtype("!{any f1}","!any|any"); }
	@Test public void test_5005() { checkNotSubtype("!{any f1}","!null|null"); }
	@Test public void test_5006() { checkIsSubtype("!{any f1}","void&void"); }
	@Test public void test_5007() { checkIsSubtype("!{any f1}","void&any"); }
	@Test public void test_5008() { checkIsSubtype("!{any f1}","void&null"); }
	@Test public void test_5009() { checkIsSubtype("!{any f1}","any&void"); }
	@Test public void test_5010() { checkNotSubtype("!{any f1}","any&any"); }
	@Test public void test_5011() { checkIsSubtype("!{any f1}","any&null"); }
	@Test public void test_5012() { checkIsSubtype("!{any f1}","null&void"); }
	@Test public void test_5013() { checkIsSubtype("!{any f1}","null&any"); }
	@Test public void test_5014() { checkIsSubtype("!{any f1}","null&null"); }
	@Test public void test_5015() { checkIsSubtype("!{any f1}","{void f1}&void"); }
	@Test public void test_5016() { checkIsSubtype("!{any f1}","{void f2}&void"); }
	@Test public void test_5017() { checkNotSubtype("!{any f1}","{any f1}&any"); }
	@Test public void test_5018() { checkIsSubtype("!{any f1}","{any f2}&any"); }
	@Test public void test_5019() { checkIsSubtype("!{any f1}","{null f1}&null"); }
	@Test public void test_5020() { checkIsSubtype("!{any f1}","{null f2}&null"); }
	@Test public void test_5021() { checkIsSubtype("!{any f1}","!void&void"); }
	@Test public void test_5022() { checkIsSubtype("!{any f1}","!any&any"); }
	@Test public void test_5023() { checkIsSubtype("!{any f1}","!null&null"); }
	@Test public void test_5024() { checkNotSubtype("!{any f1}","!{void f1}"); }
	@Test public void test_5025() { checkNotSubtype("!{any f1}","!{void f2}"); }
	@Test public void test_5026() { checkIsSubtype("!{any f1}","!{any f1}"); }
	@Test public void test_5027() { checkNotSubtype("!{any f1}","!{any f2}"); }
	@Test public void test_5028() { checkNotSubtype("!{any f1}","!{null f1}"); }
	@Test public void test_5029() { checkNotSubtype("!{any f1}","!{null f2}"); }
	@Test public void test_5030() { checkIsSubtype("!{any f1}","!!void"); }
	@Test public void test_5031() { checkNotSubtype("!{any f1}","!!any"); }
	@Test public void test_5032() { checkIsSubtype("!{any f1}","!!null"); }
	@Test public void test_5033() { checkNotSubtype("!{any f2}","any"); }
	@Test public void test_5034() { checkIsSubtype("!{any f2}","null"); }
	@Test public void test_5035() { checkIsSubtype("!{any f2}","{void f1}"); }
	@Test public void test_5036() { checkIsSubtype("!{any f2}","{void f2}"); }
	@Test public void test_5037() { checkIsSubtype("!{any f2}","{any f1}"); }
	@Test public void test_5038() { checkNotSubtype("!{any f2}","{any f2}"); }
	@Test public void test_5039() { checkIsSubtype("!{any f2}","{null f1}"); }
	@Test public void test_5040() { checkNotSubtype("!{any f2}","{null f2}"); }
	@Test public void test_5041() { checkNotSubtype("!{any f2}","!void"); }
	@Test public void test_5042() { checkIsSubtype("!{any f2}","!any"); }
	@Test public void test_5043() { checkNotSubtype("!{any f2}","!null"); }
	@Test public void test_5044() { checkIsSubtype("!{any f2}","{{void f1} f1}"); }
	@Test public void test_5045() { checkIsSubtype("!{any f2}","{{void f2} f1}"); }
	@Test public void test_5046() { checkIsSubtype("!{any f2}","{{void f1} f2}"); }
	@Test public void test_5047() { checkIsSubtype("!{any f2}","{{void f2} f2}"); }
	@Test public void test_5048() { checkIsSubtype("!{any f2}","{{any f1} f1}"); }
	@Test public void test_5049() { checkIsSubtype("!{any f2}","{{any f2} f1}"); }
	@Test public void test_5050() { checkNotSubtype("!{any f2}","{{any f1} f2}"); }
	@Test public void test_5051() { checkNotSubtype("!{any f2}","{{any f2} f2}"); }
	@Test public void test_5052() { checkIsSubtype("!{any f2}","{{null f1} f1}"); }
	@Test public void test_5053() { checkIsSubtype("!{any f2}","{{null f2} f1}"); }
	@Test public void test_5054() { checkNotSubtype("!{any f2}","{{null f1} f2}"); }
	@Test public void test_5055() { checkNotSubtype("!{any f2}","{{null f2} f2}"); }
	@Test public void test_5056() { checkIsSubtype("!{any f2}","{!void f1}"); }
	@Test public void test_5057() { checkNotSubtype("!{any f2}","{!void f2}"); }
	@Test public void test_5058() { checkIsSubtype("!{any f2}","{!any f1}"); }
	@Test public void test_5059() { checkIsSubtype("!{any f2}","{!any f2}"); }
	@Test public void test_5060() { checkIsSubtype("!{any f2}","{!null f1}"); }
	@Test public void test_5061() { checkNotSubtype("!{any f2}","{!null f2}"); }
	@Test public void test_5062() { checkIsSubtype("!{any f2}","void|void"); }
	@Test public void test_5063() { checkNotSubtype("!{any f2}","void|any"); }
	@Test public void test_5064() { checkIsSubtype("!{any f2}","void|null"); }
	@Test public void test_5065() { checkNotSubtype("!{any f2}","any|void"); }
	@Test public void test_5066() { checkNotSubtype("!{any f2}","any|any"); }
	@Test public void test_5067() { checkNotSubtype("!{any f2}","any|null"); }
	@Test public void test_5068() { checkIsSubtype("!{any f2}","null|void"); }
	@Test public void test_5069() { checkNotSubtype("!{any f2}","null|any"); }
	@Test public void test_5070() { checkIsSubtype("!{any f2}","null|null"); }
	@Test public void test_5071() { checkIsSubtype("!{any f2}","{void f1}|void"); }
	@Test public void test_5072() { checkIsSubtype("!{any f2}","{void f2}|void"); }
	@Test public void test_5073() { checkNotSubtype("!{any f2}","{any f1}|any"); }
	@Test public void test_5074() { checkNotSubtype("!{any f2}","{any f2}|any"); }
	@Test public void test_5075() { checkIsSubtype("!{any f2}","{null f1}|null"); }
	@Test public void test_5076() { checkNotSubtype("!{any f2}","{null f2}|null"); }
	@Test public void test_5077() { checkNotSubtype("!{any f2}","!void|void"); }
	@Test public void test_5078() { checkNotSubtype("!{any f2}","!any|any"); }
	@Test public void test_5079() { checkNotSubtype("!{any f2}","!null|null"); }
	@Test public void test_5080() { checkIsSubtype("!{any f2}","void&void"); }
	@Test public void test_5081() { checkIsSubtype("!{any f2}","void&any"); }
	@Test public void test_5082() { checkIsSubtype("!{any f2}","void&null"); }
	@Test public void test_5083() { checkIsSubtype("!{any f2}","any&void"); }
	@Test public void test_5084() { checkNotSubtype("!{any f2}","any&any"); }
	@Test public void test_5085() { checkIsSubtype("!{any f2}","any&null"); }
	@Test public void test_5086() { checkIsSubtype("!{any f2}","null&void"); }
	@Test public void test_5087() { checkIsSubtype("!{any f2}","null&any"); }
	@Test public void test_5088() { checkIsSubtype("!{any f2}","null&null"); }
	@Test public void test_5089() { checkIsSubtype("!{any f2}","{void f1}&void"); }
	@Test public void test_5090() { checkIsSubtype("!{any f2}","{void f2}&void"); }
	@Test public void test_5091() { checkIsSubtype("!{any f2}","{any f1}&any"); }
	@Test public void test_5092() { checkNotSubtype("!{any f2}","{any f2}&any"); }
	@Test public void test_5093() { checkIsSubtype("!{any f2}","{null f1}&null"); }
	@Test public void test_5094() { checkIsSubtype("!{any f2}","{null f2}&null"); }
	@Test public void test_5095() { checkIsSubtype("!{any f2}","!void&void"); }
	@Test public void test_5096() { checkIsSubtype("!{any f2}","!any&any"); }
	@Test public void test_5097() { checkIsSubtype("!{any f2}","!null&null"); }
	@Test public void test_5098() { checkNotSubtype("!{any f2}","!{void f1}"); }
	@Test public void test_5099() { checkNotSubtype("!{any f2}","!{void f2}"); }
	@Test public void test_5100() { checkNotSubtype("!{any f2}","!{any f1}"); }
	@Test public void test_5101() { checkIsSubtype("!{any f2}","!{any f2}"); }
	@Test public void test_5102() { checkNotSubtype("!{any f2}","!{null f1}"); }
	@Test public void test_5103() { checkNotSubtype("!{any f2}","!{null f2}"); }
	@Test public void test_5104() { checkIsSubtype("!{any f2}","!!void"); }
	@Test public void test_5105() { checkNotSubtype("!{any f2}","!!any"); }
	@Test public void test_5106() { checkIsSubtype("!{any f2}","!!null"); }
	@Test public void test_5107() { checkNotSubtype("!{null f1}","any"); }
	@Test public void test_5108() { checkIsSubtype("!{null f1}","null"); }
	@Test public void test_5109() { checkIsSubtype("!{null f1}","{void f1}"); }
	@Test public void test_5110() { checkIsSubtype("!{null f1}","{void f2}"); }
	@Test public void test_5111() { checkNotSubtype("!{null f1}","{any f1}"); }
	@Test public void test_5112() { checkIsSubtype("!{null f1}","{any f2}"); }
	@Test public void test_5113() { checkNotSubtype("!{null f1}","{null f1}"); }
	@Test public void test_5114() { checkIsSubtype("!{null f1}","{null f2}"); }
	@Test public void test_5115() { checkNotSubtype("!{null f1}","!void"); }
	@Test public void test_5116() { checkIsSubtype("!{null f1}","!any"); }
	@Test public void test_5117() { checkNotSubtype("!{null f1}","!null"); }
	@Test public void test_5118() { checkIsSubtype("!{null f1}","{{void f1} f1}"); }
	@Test public void test_5119() { checkIsSubtype("!{null f1}","{{void f2} f1}"); }
	@Test public void test_5120() { checkIsSubtype("!{null f1}","{{void f1} f2}"); }
	@Test public void test_5121() { checkIsSubtype("!{null f1}","{{void f2} f2}"); }
	@Test public void test_5122() { checkIsSubtype("!{null f1}","{{any f1} f1}"); }
	@Test public void test_5123() { checkIsSubtype("!{null f1}","{{any f2} f1}"); }
	@Test public void test_5124() { checkIsSubtype("!{null f1}","{{any f1} f2}"); }
	@Test public void test_5125() { checkIsSubtype("!{null f1}","{{any f2} f2}"); }
	@Test public void test_5126() { checkIsSubtype("!{null f1}","{{null f1} f1}"); }
	@Test public void test_5127() { checkIsSubtype("!{null f1}","{{null f2} f1}"); }
	@Test public void test_5128() { checkIsSubtype("!{null f1}","{{null f1} f2}"); }
	@Test public void test_5129() { checkIsSubtype("!{null f1}","{{null f2} f2}"); }
	@Test public void test_5130() { checkNotSubtype("!{null f1}","{!void f1}"); }
	@Test public void test_5131() { checkIsSubtype("!{null f1}","{!void f2}"); }
	@Test public void test_5132() { checkIsSubtype("!{null f1}","{!any f1}"); }
	@Test public void test_5133() { checkIsSubtype("!{null f1}","{!any f2}"); }
	@Test public void test_5134() { checkIsSubtype("!{null f1}","{!null f1}"); }
	@Test public void test_5135() { checkIsSubtype("!{null f1}","{!null f2}"); }
	@Test public void test_5136() { checkIsSubtype("!{null f1}","void|void"); }
	@Test public void test_5137() { checkNotSubtype("!{null f1}","void|any"); }
	@Test public void test_5138() { checkIsSubtype("!{null f1}","void|null"); }
	@Test public void test_5139() { checkNotSubtype("!{null f1}","any|void"); }
	@Test public void test_5140() { checkNotSubtype("!{null f1}","any|any"); }
	@Test public void test_5141() { checkNotSubtype("!{null f1}","any|null"); }
	@Test public void test_5142() { checkIsSubtype("!{null f1}","null|void"); }
	@Test public void test_5143() { checkNotSubtype("!{null f1}","null|any"); }
	@Test public void test_5144() { checkIsSubtype("!{null f1}","null|null"); }
	@Test public void test_5145() { checkIsSubtype("!{null f1}","{void f1}|void"); }
	@Test public void test_5146() { checkIsSubtype("!{null f1}","{void f2}|void"); }
	@Test public void test_5147() { checkNotSubtype("!{null f1}","{any f1}|any"); }
	@Test public void test_5148() { checkNotSubtype("!{null f1}","{any f2}|any"); }
	@Test public void test_5149() { checkNotSubtype("!{null f1}","{null f1}|null"); }
	@Test public void test_5150() { checkIsSubtype("!{null f1}","{null f2}|null"); }
	@Test public void test_5151() { checkNotSubtype("!{null f1}","!void|void"); }
	@Test public void test_5152() { checkNotSubtype("!{null f1}","!any|any"); }
	@Test public void test_5153() { checkNotSubtype("!{null f1}","!null|null"); }
	@Test public void test_5154() { checkIsSubtype("!{null f1}","void&void"); }
	@Test public void test_5155() { checkIsSubtype("!{null f1}","void&any"); }
	@Test public void test_5156() { checkIsSubtype("!{null f1}","void&null"); }
	@Test public void test_5157() { checkIsSubtype("!{null f1}","any&void"); }
	@Test public void test_5158() { checkNotSubtype("!{null f1}","any&any"); }
	@Test public void test_5159() { checkIsSubtype("!{null f1}","any&null"); }
	@Test public void test_5160() { checkIsSubtype("!{null f1}","null&void"); }
	@Test public void test_5161() { checkIsSubtype("!{null f1}","null&any"); }
	@Test public void test_5162() { checkIsSubtype("!{null f1}","null&null"); }
	@Test public void test_5163() { checkIsSubtype("!{null f1}","{void f1}&void"); }
	@Test public void test_5164() { checkIsSubtype("!{null f1}","{void f2}&void"); }
	@Test public void test_5165() { checkNotSubtype("!{null f1}","{any f1}&any"); }
	@Test public void test_5166() { checkIsSubtype("!{null f1}","{any f2}&any"); }
	@Test public void test_5167() { checkIsSubtype("!{null f1}","{null f1}&null"); }
	@Test public void test_5168() { checkIsSubtype("!{null f1}","{null f2}&null"); }
	@Test public void test_5169() { checkIsSubtype("!{null f1}","!void&void"); }
	@Test public void test_5170() { checkIsSubtype("!{null f1}","!any&any"); }
	@Test public void test_5171() { checkIsSubtype("!{null f1}","!null&null"); }
	@Test public void test_5172() { checkNotSubtype("!{null f1}","!{void f1}"); }
	@Test public void test_5173() { checkNotSubtype("!{null f1}","!{void f2}"); }
	@Test public void test_5174() { checkIsSubtype("!{null f1}","!{any f1}"); }
	@Test public void test_5175() { checkNotSubtype("!{null f1}","!{any f2}"); }
	@Test public void test_5176() { checkIsSubtype("!{null f1}","!{null f1}"); }
	@Test public void test_5177() { checkNotSubtype("!{null f1}","!{null f2}"); }
	@Test public void test_5178() { checkIsSubtype("!{null f1}","!!void"); }
	@Test public void test_5179() { checkNotSubtype("!{null f1}","!!any"); }
	@Test public void test_5180() { checkIsSubtype("!{null f1}","!!null"); }
	@Test public void test_5181() { checkNotSubtype("!{null f2}","any"); }
	@Test public void test_5182() { checkIsSubtype("!{null f2}","null"); }
	@Test public void test_5183() { checkIsSubtype("!{null f2}","{void f1}"); }
	@Test public void test_5184() { checkIsSubtype("!{null f2}","{void f2}"); }
	@Test public void test_5185() { checkIsSubtype("!{null f2}","{any f1}"); }
	@Test public void test_5186() { checkNotSubtype("!{null f2}","{any f2}"); }
	@Test public void test_5187() { checkIsSubtype("!{null f2}","{null f1}"); }
	@Test public void test_5188() { checkNotSubtype("!{null f2}","{null f2}"); }
	@Test public void test_5189() { checkNotSubtype("!{null f2}","!void"); }
	@Test public void test_5190() { checkIsSubtype("!{null f2}","!any"); }
	@Test public void test_5191() { checkNotSubtype("!{null f2}","!null"); }
	@Test public void test_5192() { checkIsSubtype("!{null f2}","{{void f1} f1}"); }
	@Test public void test_5193() { checkIsSubtype("!{null f2}","{{void f2} f1}"); }
	@Test public void test_5194() { checkIsSubtype("!{null f2}","{{void f1} f2}"); }
	@Test public void test_5195() { checkIsSubtype("!{null f2}","{{void f2} f2}"); }
	@Test public void test_5196() { checkIsSubtype("!{null f2}","{{any f1} f1}"); }
	@Test public void test_5197() { checkIsSubtype("!{null f2}","{{any f2} f1}"); }
	@Test public void test_5198() { checkIsSubtype("!{null f2}","{{any f1} f2}"); }
	@Test public void test_5199() { checkIsSubtype("!{null f2}","{{any f2} f2}"); }
	@Test public void test_5200() { checkIsSubtype("!{null f2}","{{null f1} f1}"); }
	@Test public void test_5201() { checkIsSubtype("!{null f2}","{{null f2} f1}"); }
	@Test public void test_5202() { checkIsSubtype("!{null f2}","{{null f1} f2}"); }
	@Test public void test_5203() { checkIsSubtype("!{null f2}","{{null f2} f2}"); }
	@Test public void test_5204() { checkIsSubtype("!{null f2}","{!void f1}"); }
	@Test public void test_5205() { checkNotSubtype("!{null f2}","{!void f2}"); }
	@Test public void test_5206() { checkIsSubtype("!{null f2}","{!any f1}"); }
	@Test public void test_5207() { checkIsSubtype("!{null f2}","{!any f2}"); }
	@Test public void test_5208() { checkIsSubtype("!{null f2}","{!null f1}"); }
	@Test public void test_5209() { checkIsSubtype("!{null f2}","{!null f2}"); }
	@Test public void test_5210() { checkIsSubtype("!{null f2}","void|void"); }
	@Test public void test_5211() { checkNotSubtype("!{null f2}","void|any"); }
	@Test public void test_5212() { checkIsSubtype("!{null f2}","void|null"); }
	@Test public void test_5213() { checkNotSubtype("!{null f2}","any|void"); }
	@Test public void test_5214() { checkNotSubtype("!{null f2}","any|any"); }
	@Test public void test_5215() { checkNotSubtype("!{null f2}","any|null"); }
	@Test public void test_5216() { checkIsSubtype("!{null f2}","null|void"); }
	@Test public void test_5217() { checkNotSubtype("!{null f2}","null|any"); }
	@Test public void test_5218() { checkIsSubtype("!{null f2}","null|null"); }
	@Test public void test_5219() { checkIsSubtype("!{null f2}","{void f1}|void"); }
	@Test public void test_5220() { checkIsSubtype("!{null f2}","{void f2}|void"); }
	@Test public void test_5221() { checkNotSubtype("!{null f2}","{any f1}|any"); }
	@Test public void test_5222() { checkNotSubtype("!{null f2}","{any f2}|any"); }
	@Test public void test_5223() { checkIsSubtype("!{null f2}","{null f1}|null"); }
	@Test public void test_5224() { checkNotSubtype("!{null f2}","{null f2}|null"); }
	@Test public void test_5225() { checkNotSubtype("!{null f2}","!void|void"); }
	@Test public void test_5226() { checkNotSubtype("!{null f2}","!any|any"); }
	@Test public void test_5227() { checkNotSubtype("!{null f2}","!null|null"); }
	@Test public void test_5228() { checkIsSubtype("!{null f2}","void&void"); }
	@Test public void test_5229() { checkIsSubtype("!{null f2}","void&any"); }
	@Test public void test_5230() { checkIsSubtype("!{null f2}","void&null"); }
	@Test public void test_5231() { checkIsSubtype("!{null f2}","any&void"); }
	@Test public void test_5232() { checkNotSubtype("!{null f2}","any&any"); }
	@Test public void test_5233() { checkIsSubtype("!{null f2}","any&null"); }
	@Test public void test_5234() { checkIsSubtype("!{null f2}","null&void"); }
	@Test public void test_5235() { checkIsSubtype("!{null f2}","null&any"); }
	@Test public void test_5236() { checkIsSubtype("!{null f2}","null&null"); }
	@Test public void test_5237() { checkIsSubtype("!{null f2}","{void f1}&void"); }
	@Test public void test_5238() { checkIsSubtype("!{null f2}","{void f2}&void"); }
	@Test public void test_5239() { checkIsSubtype("!{null f2}","{any f1}&any"); }
	@Test public void test_5240() { checkNotSubtype("!{null f2}","{any f2}&any"); }
	@Test public void test_5241() { checkIsSubtype("!{null f2}","{null f1}&null"); }
	@Test public void test_5242() { checkIsSubtype("!{null f2}","{null f2}&null"); }
	@Test public void test_5243() { checkIsSubtype("!{null f2}","!void&void"); }
	@Test public void test_5244() { checkIsSubtype("!{null f2}","!any&any"); }
	@Test public void test_5245() { checkIsSubtype("!{null f2}","!null&null"); }
	@Test public void test_5246() { checkNotSubtype("!{null f2}","!{void f1}"); }
	@Test public void test_5247() { checkNotSubtype("!{null f2}","!{void f2}"); }
	@Test public void test_5248() { checkNotSubtype("!{null f2}","!{any f1}"); }
	@Test public void test_5249() { checkIsSubtype("!{null f2}","!{any f2}"); }
	@Test public void test_5250() { checkNotSubtype("!{null f2}","!{null f1}"); }
	@Test public void test_5251() { checkIsSubtype("!{null f2}","!{null f2}"); }
	@Test public void test_5252() { checkIsSubtype("!{null f2}","!!void"); }
	@Test public void test_5253() { checkNotSubtype("!{null f2}","!!any"); }
	@Test public void test_5254() { checkIsSubtype("!{null f2}","!!null"); }
	@Test public void test_5255() { checkNotSubtype("!!void","any"); }
	@Test public void test_5256() { checkNotSubtype("!!void","null"); }
	@Test public void test_5257() { checkIsSubtype("!!void","{void f1}"); }
	@Test public void test_5258() { checkIsSubtype("!!void","{void f2}"); }
	@Test public void test_5259() { checkNotSubtype("!!void","{any f1}"); }
	@Test public void test_5260() { checkNotSubtype("!!void","{any f2}"); }
	@Test public void test_5261() { checkNotSubtype("!!void","{null f1}"); }
	@Test public void test_5262() { checkNotSubtype("!!void","{null f2}"); }
	@Test public void test_5263() { checkNotSubtype("!!void","!void"); }
	@Test public void test_5264() { checkIsSubtype("!!void","!any"); }
	@Test public void test_5265() { checkNotSubtype("!!void","!null"); }
	@Test public void test_5266() { checkIsSubtype("!!void","{{void f1} f1}"); }
	@Test public void test_5267() { checkIsSubtype("!!void","{{void f2} f1}"); }
	@Test public void test_5268() { checkIsSubtype("!!void","{{void f1} f2}"); }
	@Test public void test_5269() { checkIsSubtype("!!void","{{void f2} f2}"); }
	@Test public void test_5270() { checkNotSubtype("!!void","{{any f1} f1}"); }
	@Test public void test_5271() { checkNotSubtype("!!void","{{any f2} f1}"); }
	@Test public void test_5272() { checkNotSubtype("!!void","{{any f1} f2}"); }
	@Test public void test_5273() { checkNotSubtype("!!void","{{any f2} f2}"); }
	@Test public void test_5274() { checkNotSubtype("!!void","{{null f1} f1}"); }
	@Test public void test_5275() { checkNotSubtype("!!void","{{null f2} f1}"); }
	@Test public void test_5276() { checkNotSubtype("!!void","{{null f1} f2}"); }
	@Test public void test_5277() { checkNotSubtype("!!void","{{null f2} f2}"); }
	@Test public void test_5278() { checkNotSubtype("!!void","{!void f1}"); }
	@Test public void test_5279() { checkNotSubtype("!!void","{!void f2}"); }
	@Test public void test_5280() { checkIsSubtype("!!void","{!any f1}"); }
	@Test public void test_5281() { checkIsSubtype("!!void","{!any f2}"); }
	@Test public void test_5282() { checkNotSubtype("!!void","{!null f1}"); }
	@Test public void test_5283() { checkNotSubtype("!!void","{!null f2}"); }
	@Test public void test_5284() { checkIsSubtype("!!void","void|void"); }
	@Test public void test_5285() { checkNotSubtype("!!void","void|any"); }
	@Test public void test_5286() { checkNotSubtype("!!void","void|null"); }
	@Test public void test_5287() { checkNotSubtype("!!void","any|void"); }
	@Test public void test_5288() { checkNotSubtype("!!void","any|any"); }
	@Test public void test_5289() { checkNotSubtype("!!void","any|null"); }
	@Test public void test_5290() { checkNotSubtype("!!void","null|void"); }
	@Test public void test_5291() { checkNotSubtype("!!void","null|any"); }
	@Test public void test_5292() { checkNotSubtype("!!void","null|null"); }
	@Test public void test_5293() { checkIsSubtype("!!void","{void f1}|void"); }
	@Test public void test_5294() { checkIsSubtype("!!void","{void f2}|void"); }
	@Test public void test_5295() { checkNotSubtype("!!void","{any f1}|any"); }
	@Test public void test_5296() { checkNotSubtype("!!void","{any f2}|any"); }
	@Test public void test_5297() { checkNotSubtype("!!void","{null f1}|null"); }
	@Test public void test_5298() { checkNotSubtype("!!void","{null f2}|null"); }
	@Test public void test_5299() { checkNotSubtype("!!void","!void|void"); }
	@Test public void test_5300() { checkNotSubtype("!!void","!any|any"); }
	@Test public void test_5301() { checkNotSubtype("!!void","!null|null"); }
	@Test public void test_5302() { checkIsSubtype("!!void","void&void"); }
	@Test public void test_5303() { checkIsSubtype("!!void","void&any"); }
	@Test public void test_5304() { checkIsSubtype("!!void","void&null"); }
	@Test public void test_5305() { checkIsSubtype("!!void","any&void"); }
	@Test public void test_5306() { checkNotSubtype("!!void","any&any"); }
	@Test public void test_5307() { checkNotSubtype("!!void","any&null"); }
	@Test public void test_5308() { checkIsSubtype("!!void","null&void"); }
	@Test public void test_5309() { checkNotSubtype("!!void","null&any"); }
	@Test public void test_5310() { checkNotSubtype("!!void","null&null"); }
	@Test public void test_5311() { checkIsSubtype("!!void","{void f1}&void"); }
	@Test public void test_5312() { checkIsSubtype("!!void","{void f2}&void"); }
	@Test public void test_5313() { checkNotSubtype("!!void","{any f1}&any"); }
	@Test public void test_5314() { checkNotSubtype("!!void","{any f2}&any"); }
	@Test public void test_5315() { checkIsSubtype("!!void","{null f1}&null"); }
	@Test public void test_5316() { checkIsSubtype("!!void","{null f2}&null"); }
	@Test public void test_5317() { checkIsSubtype("!!void","!void&void"); }
	@Test public void test_5318() { checkIsSubtype("!!void","!any&any"); }
	@Test public void test_5319() { checkIsSubtype("!!void","!null&null"); }
	@Test public void test_5320() { checkNotSubtype("!!void","!{void f1}"); }
	@Test public void test_5321() { checkNotSubtype("!!void","!{void f2}"); }
	@Test public void test_5322() { checkNotSubtype("!!void","!{any f1}"); }
	@Test public void test_5323() { checkNotSubtype("!!void","!{any f2}"); }
	@Test public void test_5324() { checkNotSubtype("!!void","!{null f1}"); }
	@Test public void test_5325() { checkNotSubtype("!!void","!{null f2}"); }
	@Test public void test_5326() { checkIsSubtype("!!void","!!void"); }
	@Test public void test_5327() { checkNotSubtype("!!void","!!any"); }
	@Test public void test_5328() { checkNotSubtype("!!void","!!null"); }
	@Test public void test_5329() { checkIsSubtype("!!any","any"); }
	@Test public void test_5330() { checkIsSubtype("!!any","null"); }
	@Test public void test_5331() { checkIsSubtype("!!any","{void f1}"); }
	@Test public void test_5332() { checkIsSubtype("!!any","{void f2}"); }
	@Test public void test_5333() { checkIsSubtype("!!any","{any f1}"); }
	@Test public void test_5334() { checkIsSubtype("!!any","{any f2}"); }
	@Test public void test_5335() { checkIsSubtype("!!any","{null f1}"); }
	@Test public void test_5336() { checkIsSubtype("!!any","{null f2}"); }
	@Test public void test_5337() { checkIsSubtype("!!any","!void"); }
	@Test public void test_5338() { checkIsSubtype("!!any","!any"); }
	@Test public void test_5339() { checkIsSubtype("!!any","!null"); }
	@Test public void test_5340() { checkIsSubtype("!!any","{{void f1} f1}"); }
	@Test public void test_5341() { checkIsSubtype("!!any","{{void f2} f1}"); }
	@Test public void test_5342() { checkIsSubtype("!!any","{{void f1} f2}"); }
	@Test public void test_5343() { checkIsSubtype("!!any","{{void f2} f2}"); }
	@Test public void test_5344() { checkIsSubtype("!!any","{{any f1} f1}"); }
	@Test public void test_5345() { checkIsSubtype("!!any","{{any f2} f1}"); }
	@Test public void test_5346() { checkIsSubtype("!!any","{{any f1} f2}"); }
	@Test public void test_5347() { checkIsSubtype("!!any","{{any f2} f2}"); }
	@Test public void test_5348() { checkIsSubtype("!!any","{{null f1} f1}"); }
	@Test public void test_5349() { checkIsSubtype("!!any","{{null f2} f1}"); }
	@Test public void test_5350() { checkIsSubtype("!!any","{{null f1} f2}"); }
	@Test public void test_5351() { checkIsSubtype("!!any","{{null f2} f2}"); }
	@Test public void test_5352() { checkIsSubtype("!!any","{!void f1}"); }
	@Test public void test_5353() { checkIsSubtype("!!any","{!void f2}"); }
	@Test public void test_5354() { checkIsSubtype("!!any","{!any f1}"); }
	@Test public void test_5355() { checkIsSubtype("!!any","{!any f2}"); }
	@Test public void test_5356() { checkIsSubtype("!!any","{!null f1}"); }
	@Test public void test_5357() { checkIsSubtype("!!any","{!null f2}"); }
	@Test public void test_5358() { checkIsSubtype("!!any","void|void"); }
	@Test public void test_5359() { checkIsSubtype("!!any","void|any"); }
	@Test public void test_5360() { checkIsSubtype("!!any","void|null"); }
	@Test public void test_5361() { checkIsSubtype("!!any","any|void"); }
	@Test public void test_5362() { checkIsSubtype("!!any","any|any"); }
	@Test public void test_5363() { checkIsSubtype("!!any","any|null"); }
	@Test public void test_5364() { checkIsSubtype("!!any","null|void"); }
	@Test public void test_5365() { checkIsSubtype("!!any","null|any"); }
	@Test public void test_5366() { checkIsSubtype("!!any","null|null"); }
	@Test public void test_5367() { checkIsSubtype("!!any","{void f1}|void"); }
	@Test public void test_5368() { checkIsSubtype("!!any","{void f2}|void"); }
	@Test public void test_5369() { checkIsSubtype("!!any","{any f1}|any"); }
	@Test public void test_5370() { checkIsSubtype("!!any","{any f2}|any"); }
	@Test public void test_5371() { checkIsSubtype("!!any","{null f1}|null"); }
	@Test public void test_5372() { checkIsSubtype("!!any","{null f2}|null"); }
	@Test public void test_5373() { checkIsSubtype("!!any","!void|void"); }
	@Test public void test_5374() { checkIsSubtype("!!any","!any|any"); }
	@Test public void test_5375() { checkIsSubtype("!!any","!null|null"); }
	@Test public void test_5376() { checkIsSubtype("!!any","void&void"); }
	@Test public void test_5377() { checkIsSubtype("!!any","void&any"); }
	@Test public void test_5378() { checkIsSubtype("!!any","void&null"); }
	@Test public void test_5379() { checkIsSubtype("!!any","any&void"); }
	@Test public void test_5380() { checkIsSubtype("!!any","any&any"); }
	@Test public void test_5381() { checkIsSubtype("!!any","any&null"); }
	@Test public void test_5382() { checkIsSubtype("!!any","null&void"); }
	@Test public void test_5383() { checkIsSubtype("!!any","null&any"); }
	@Test public void test_5384() { checkIsSubtype("!!any","null&null"); }
	@Test public void test_5385() { checkIsSubtype("!!any","{void f1}&void"); }
	@Test public void test_5386() { checkIsSubtype("!!any","{void f2}&void"); }
	@Test public void test_5387() { checkIsSubtype("!!any","{any f1}&any"); }
	@Test public void test_5388() { checkIsSubtype("!!any","{any f2}&any"); }
	@Test public void test_5389() { checkIsSubtype("!!any","{null f1}&null"); }
	@Test public void test_5390() { checkIsSubtype("!!any","{null f2}&null"); }
	@Test public void test_5391() { checkIsSubtype("!!any","!void&void"); }
	@Test public void test_5392() { checkIsSubtype("!!any","!any&any"); }
	@Test public void test_5393() { checkIsSubtype("!!any","!null&null"); }
	@Test public void test_5394() { checkIsSubtype("!!any","!{void f1}"); }
	@Test public void test_5395() { checkIsSubtype("!!any","!{void f2}"); }
	@Test public void test_5396() { checkIsSubtype("!!any","!{any f1}"); }
	@Test public void test_5397() { checkIsSubtype("!!any","!{any f2}"); }
	@Test public void test_5398() { checkIsSubtype("!!any","!{null f1}"); }
	@Test public void test_5399() { checkIsSubtype("!!any","!{null f2}"); }
	@Test public void test_5400() { checkIsSubtype("!!any","!!void"); }
	@Test public void test_5401() { checkIsSubtype("!!any","!!any"); }
	@Test public void test_5402() { checkIsSubtype("!!any","!!null"); }
	@Test public void test_5403() { checkNotSubtype("!!null","any"); }
	@Test public void test_5404() { checkIsSubtype("!!null","null"); }
	@Test public void test_5405() { checkIsSubtype("!!null","{void f1}"); }
	@Test public void test_5406() { checkIsSubtype("!!null","{void f2}"); }
	@Test public void test_5407() { checkNotSubtype("!!null","{any f1}"); }
	@Test public void test_5408() { checkNotSubtype("!!null","{any f2}"); }
	@Test public void test_5409() { checkNotSubtype("!!null","{null f1}"); }
	@Test public void test_5410() { checkNotSubtype("!!null","{null f2}"); }
	@Test public void test_5411() { checkNotSubtype("!!null","!void"); }
	@Test public void test_5412() { checkIsSubtype("!!null","!any"); }
	@Test public void test_5413() { checkNotSubtype("!!null","!null"); }
	@Test public void test_5414() { checkIsSubtype("!!null","{{void f1} f1}"); }
	@Test public void test_5415() { checkIsSubtype("!!null","{{void f2} f1}"); }
	@Test public void test_5416() { checkIsSubtype("!!null","{{void f1} f2}"); }
	@Test public void test_5417() { checkIsSubtype("!!null","{{void f2} f2}"); }
	@Test public void test_5418() { checkNotSubtype("!!null","{{any f1} f1}"); }
	@Test public void test_5419() { checkNotSubtype("!!null","{{any f2} f1}"); }
	@Test public void test_5420() { checkNotSubtype("!!null","{{any f1} f2}"); }
	@Test public void test_5421() { checkNotSubtype("!!null","{{any f2} f2}"); }
	@Test public void test_5422() { checkNotSubtype("!!null","{{null f1} f1}"); }
	@Test public void test_5423() { checkNotSubtype("!!null","{{null f2} f1}"); }
	@Test public void test_5424() { checkNotSubtype("!!null","{{null f1} f2}"); }
	@Test public void test_5425() { checkNotSubtype("!!null","{{null f2} f2}"); }
	@Test public void test_5426() { checkNotSubtype("!!null","{!void f1}"); }
	@Test public void test_5427() { checkNotSubtype("!!null","{!void f2}"); }
	@Test public void test_5428() { checkIsSubtype("!!null","{!any f1}"); }
	@Test public void test_5429() { checkIsSubtype("!!null","{!any f2}"); }
	@Test public void test_5430() { checkNotSubtype("!!null","{!null f1}"); }
	@Test public void test_5431() { checkNotSubtype("!!null","{!null f2}"); }
	@Test public void test_5432() { checkIsSubtype("!!null","void|void"); }
	@Test public void test_5433() { checkNotSubtype("!!null","void|any"); }
	@Test public void test_5434() { checkIsSubtype("!!null","void|null"); }
	@Test public void test_5435() { checkNotSubtype("!!null","any|void"); }
	@Test public void test_5436() { checkNotSubtype("!!null","any|any"); }
	@Test public void test_5437() { checkNotSubtype("!!null","any|null"); }
	@Test public void test_5438() { checkIsSubtype("!!null","null|void"); }
	@Test public void test_5439() { checkNotSubtype("!!null","null|any"); }
	@Test public void test_5440() { checkIsSubtype("!!null","null|null"); }
	@Test public void test_5441() { checkIsSubtype("!!null","{void f1}|void"); }
	@Test public void test_5442() { checkIsSubtype("!!null","{void f2}|void"); }
	@Test public void test_5443() { checkNotSubtype("!!null","{any f1}|any"); }
	@Test public void test_5444() { checkNotSubtype("!!null","{any f2}|any"); }
	@Test public void test_5445() { checkNotSubtype("!!null","{null f1}|null"); }
	@Test public void test_5446() { checkNotSubtype("!!null","{null f2}|null"); }
	@Test public void test_5447() { checkNotSubtype("!!null","!void|void"); }
	@Test public void test_5448() { checkNotSubtype("!!null","!any|any"); }
	@Test public void test_5449() { checkNotSubtype("!!null","!null|null"); }
	@Test public void test_5450() { checkIsSubtype("!!null","void&void"); }
	@Test public void test_5451() { checkIsSubtype("!!null","void&any"); }
	@Test public void test_5452() { checkIsSubtype("!!null","void&null"); }
	@Test public void test_5453() { checkIsSubtype("!!null","any&void"); }
	@Test public void test_5454() { checkNotSubtype("!!null","any&any"); }
	@Test public void test_5455() { checkIsSubtype("!!null","any&null"); }
	@Test public void test_5456() { checkIsSubtype("!!null","null&void"); }
	@Test public void test_5457() { checkIsSubtype("!!null","null&any"); }
	@Test public void test_5458() { checkIsSubtype("!!null","null&null"); }
	@Test public void test_5459() { checkIsSubtype("!!null","{void f1}&void"); }
	@Test public void test_5460() { checkIsSubtype("!!null","{void f2}&void"); }
	@Test public void test_5461() { checkNotSubtype("!!null","{any f1}&any"); }
	@Test public void test_5462() { checkNotSubtype("!!null","{any f2}&any"); }
	@Test public void test_5463() { checkIsSubtype("!!null","{null f1}&null"); }
	@Test public void test_5464() { checkIsSubtype("!!null","{null f2}&null"); }
	@Test public void test_5465() { checkIsSubtype("!!null","!void&void"); }
	@Test public void test_5466() { checkIsSubtype("!!null","!any&any"); }
	@Test public void test_5467() { checkIsSubtype("!!null","!null&null"); }
	@Test public void test_5468() { checkNotSubtype("!!null","!{void f1}"); }
	@Test public void test_5469() { checkNotSubtype("!!null","!{void f2}"); }
	@Test public void test_5470() { checkNotSubtype("!!null","!{any f1}"); }
	@Test public void test_5471() { checkNotSubtype("!!null","!{any f2}"); }
	@Test public void test_5472() { checkNotSubtype("!!null","!{null f1}"); }
	@Test public void test_5473() { checkNotSubtype("!!null","!{null f2}"); }
	@Test public void test_5474() { checkIsSubtype("!!null","!!void"); }
	@Test public void test_5475() { checkNotSubtype("!!null","!!any"); }
	@Test public void test_5476() { checkIsSubtype("!!null","!!null"); }

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
