// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class RecursiveSubtypeTests {
	@Test public void test_1() { checkIsSubtype("null","null"); }
	@Test public void test_2() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_3() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_4() { checkIsSubtype("null","X<X|null>"); }
	@Test public void test_5() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_6() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_7() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_8() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_9() { checkNotSubtype("null","{X<X|null> f1}"); }
	@Test public void test_10() { checkNotSubtype("null","{X<X|null> f2}"); }
	@Test public void test_11() { checkIsSubtype("null","null|null"); }
	@Test public void test_12() { checkIsSubtype("null","null|X<{X f1}>"); }
	@Test public void test_13() { checkIsSubtype("null","null|X<{X f2}>"); }
	@Test public void test_14() { checkNotSubtype("null","null|{null f1}"); }
	@Test public void test_15() { checkNotSubtype("null","null|{null f2}"); }
	@Test public void test_16() { checkIsSubtype("null","null|(X<null|X>)"); }
	@Test public void test_17() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_18() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_19() { checkIsSubtype("null","X<{X f1}>|null"); }
	@Test public void test_20() { checkIsSubtype("null","X<{X f2}>|null"); }
	@Test public void test_21() { checkNotSubtype("null","X<X|{null f1}>"); }
	@Test public void test_22() { checkNotSubtype("null","X<X|{null f2}>"); }
	@Test public void test_23() { checkIsSubtype("null","(X<X|null>)|null"); }
	@Test public void test_24() { checkIsSubtype("null","X<X|(Y<Y|null>)>"); }
	@Test public void test_25() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_26() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_27() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_28() { checkNotSubtype("{null f1}","X<X|null>"); }
	@Test public void test_29() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test public void test_30() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test public void test_31() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test public void test_32() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test public void test_33() { checkIsSubtype("{null f1}","{X<X|null> f1}"); }
	@Test public void test_34() { checkNotSubtype("{null f1}","{X<X|null> f2}"); }
	@Test public void test_35() { checkNotSubtype("{null f1}","null|null"); }
	@Test public void test_36() { checkNotSubtype("{null f1}","null|X<{X f1}>"); }
	@Test public void test_37() { checkNotSubtype("{null f1}","null|X<{X f2}>"); }
	@Test public void test_38() { checkNotSubtype("{null f1}","null|{null f1}"); }
	@Test public void test_39() { checkNotSubtype("{null f1}","null|{null f2}"); }
	@Test public void test_40() { checkNotSubtype("{null f1}","null|(X<null|X>)"); }
	@Test public void test_41() { checkNotSubtype("{null f1}","{null f1}|null"); }
	@Test public void test_42() { checkNotSubtype("{null f1}","{null f2}|null"); }
	@Test public void test_43() { checkNotSubtype("{null f1}","X<{X f1}>|null"); }
	@Test public void test_44() { checkNotSubtype("{null f1}","X<{X f2}>|null"); }
	@Test public void test_45() { checkIsSubtype("{null f1}","X<X|{null f1}>"); }
	@Test public void test_46() { checkNotSubtype("{null f1}","X<X|{null f2}>"); }
	@Test public void test_47() { checkNotSubtype("{null f1}","(X<X|null>)|null"); }
	@Test public void test_48() { checkNotSubtype("{null f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_49() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_50() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_51() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_52() { checkNotSubtype("{null f2}","X<X|null>"); }
	@Test public void test_53() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test public void test_54() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test public void test_55() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test public void test_56() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test public void test_57() { checkNotSubtype("{null f2}","{X<X|null> f1}"); }
	@Test public void test_58() { checkIsSubtype("{null f2}","{X<X|null> f2}"); }
	@Test public void test_59() { checkNotSubtype("{null f2}","null|null"); }
	@Test public void test_60() { checkNotSubtype("{null f2}","null|X<{X f1}>"); }
	@Test public void test_61() { checkNotSubtype("{null f2}","null|X<{X f2}>"); }
	@Test public void test_62() { checkNotSubtype("{null f2}","null|{null f1}"); }
	@Test public void test_63() { checkNotSubtype("{null f2}","null|{null f2}"); }
	@Test public void test_64() { checkNotSubtype("{null f2}","null|(X<null|X>)"); }
	@Test public void test_65() { checkNotSubtype("{null f2}","{null f1}|null"); }
	@Test public void test_66() { checkNotSubtype("{null f2}","{null f2}|null"); }
	@Test public void test_67() { checkNotSubtype("{null f2}","X<{X f1}>|null"); }
	@Test public void test_68() { checkNotSubtype("{null f2}","X<{X f2}>|null"); }
	@Test public void test_69() { checkNotSubtype("{null f2}","X<X|{null f1}>"); }
	@Test public void test_70() { checkIsSubtype("{null f2}","X<X|{null f2}>"); }
	@Test public void test_71() { checkNotSubtype("{null f2}","(X<X|null>)|null"); }
	@Test public void test_72() { checkNotSubtype("{null f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_73() { checkIsSubtype("X<X|null>","null"); }
	@Test public void test_74() { checkNotSubtype("X<X|null>","{null f1}"); }
	@Test public void test_75() { checkNotSubtype("X<X|null>","{null f2}"); }
	@Test public void test_76() { checkIsSubtype("X<X|null>","X<X|null>"); }
	@Test public void test_77() { checkNotSubtype("X<X|null>","{{null f1} f1}"); }
	@Test public void test_78() { checkNotSubtype("X<X|null>","{{null f2} f1}"); }
	@Test public void test_79() { checkNotSubtype("X<X|null>","{{null f1} f2}"); }
	@Test public void test_80() { checkNotSubtype("X<X|null>","{{null f2} f2}"); }
	@Test public void test_81() { checkNotSubtype("X<X|null>","{X<X|null> f1}"); }
	@Test public void test_82() { checkNotSubtype("X<X|null>","{X<X|null> f2}"); }
	@Test public void test_83() { checkIsSubtype("X<X|null>","null|null"); }
	@Test public void test_84() { checkIsSubtype("X<X|null>","null|X<{X f1}>"); }
	@Test public void test_85() { checkIsSubtype("X<X|null>","null|X<{X f2}>"); }
	@Test public void test_86() { checkNotSubtype("X<X|null>","null|{null f1}"); }
	@Test public void test_87() { checkNotSubtype("X<X|null>","null|{null f2}"); }
	@Test public void test_88() { checkIsSubtype("X<X|null>","null|(X<null|X>)"); }
	@Test public void test_89() { checkNotSubtype("X<X|null>","{null f1}|null"); }
	@Test public void test_90() { checkNotSubtype("X<X|null>","{null f2}|null"); }
	@Test public void test_91() { checkIsSubtype("X<X|null>","X<{X f1}>|null"); }
	@Test public void test_92() { checkIsSubtype("X<X|null>","X<{X f2}>|null"); }
	@Test public void test_93() { checkNotSubtype("X<X|null>","X<X|{null f1}>"); }
	@Test public void test_94() { checkNotSubtype("X<X|null>","X<X|{null f2}>"); }
	@Test public void test_95() { checkIsSubtype("X<X|null>","(X<X|null>)|null"); }
	@Test public void test_96() { checkIsSubtype("X<X|null>","X<X|(Y<Y|null>)>"); }
	@Test public void test_97() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_98() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test public void test_99() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test public void test_100() { checkNotSubtype("{{null f1} f1}","X<X|null>"); }
	@Test public void test_101() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test public void test_102() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test public void test_103() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test public void test_104() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test public void test_105() { checkNotSubtype("{{null f1} f1}","{X<X|null> f1}"); }
	@Test public void test_106() { checkNotSubtype("{{null f1} f1}","{X<X|null> f2}"); }
	@Test public void test_107() { checkNotSubtype("{{null f1} f1}","null|null"); }
	@Test public void test_108() { checkNotSubtype("{{null f1} f1}","null|X<{X f1}>"); }
	@Test public void test_109() { checkNotSubtype("{{null f1} f1}","null|X<{X f2}>"); }
	@Test public void test_110() { checkNotSubtype("{{null f1} f1}","null|{null f1}"); }
	@Test public void test_111() { checkNotSubtype("{{null f1} f1}","null|{null f2}"); }
	@Test public void test_112() { checkNotSubtype("{{null f1} f1}","null|(X<null|X>)"); }
	@Test public void test_113() { checkNotSubtype("{{null f1} f1}","{null f1}|null"); }
	@Test public void test_114() { checkNotSubtype("{{null f1} f1}","{null f2}|null"); }
	@Test public void test_115() { checkNotSubtype("{{null f1} f1}","X<{X f1}>|null"); }
	@Test public void test_116() { checkNotSubtype("{{null f1} f1}","X<{X f2}>|null"); }
	@Test public void test_117() { checkNotSubtype("{{null f1} f1}","X<X|{null f1}>"); }
	@Test public void test_118() { checkNotSubtype("{{null f1} f1}","X<X|{null f2}>"); }
	@Test public void test_119() { checkNotSubtype("{{null f1} f1}","(X<X|null>)|null"); }
	@Test public void test_120() { checkNotSubtype("{{null f1} f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_121() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_122() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test public void test_123() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test public void test_124() { checkNotSubtype("{{null f2} f1}","X<X|null>"); }
	@Test public void test_125() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test public void test_126() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test public void test_127() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test public void test_128() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test public void test_129() { checkNotSubtype("{{null f2} f1}","{X<X|null> f1}"); }
	@Test public void test_130() { checkNotSubtype("{{null f2} f1}","{X<X|null> f2}"); }
	@Test public void test_131() { checkNotSubtype("{{null f2} f1}","null|null"); }
	@Test public void test_132() { checkNotSubtype("{{null f2} f1}","null|X<{X f1}>"); }
	@Test public void test_133() { checkNotSubtype("{{null f2} f1}","null|X<{X f2}>"); }
	@Test public void test_134() { checkNotSubtype("{{null f2} f1}","null|{null f1}"); }
	@Test public void test_135() { checkNotSubtype("{{null f2} f1}","null|{null f2}"); }
	@Test public void test_136() { checkNotSubtype("{{null f2} f1}","null|(X<null|X>)"); }
	@Test public void test_137() { checkNotSubtype("{{null f2} f1}","{null f1}|null"); }
	@Test public void test_138() { checkNotSubtype("{{null f2} f1}","{null f2}|null"); }
	@Test public void test_139() { checkNotSubtype("{{null f2} f1}","X<{X f1}>|null"); }
	@Test public void test_140() { checkNotSubtype("{{null f2} f1}","X<{X f2}>|null"); }
	@Test public void test_141() { checkNotSubtype("{{null f2} f1}","X<X|{null f1}>"); }
	@Test public void test_142() { checkNotSubtype("{{null f2} f1}","X<X|{null f2}>"); }
	@Test public void test_143() { checkNotSubtype("{{null f2} f1}","(X<X|null>)|null"); }
	@Test public void test_144() { checkNotSubtype("{{null f2} f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_145() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_146() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test public void test_147() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test public void test_148() { checkNotSubtype("{{null f1} f2}","X<X|null>"); }
	@Test public void test_149() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test public void test_150() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test public void test_151() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test public void test_152() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test public void test_153() { checkNotSubtype("{{null f1} f2}","{X<X|null> f1}"); }
	@Test public void test_154() { checkNotSubtype("{{null f1} f2}","{X<X|null> f2}"); }
	@Test public void test_155() { checkNotSubtype("{{null f1} f2}","null|null"); }
	@Test public void test_156() { checkNotSubtype("{{null f1} f2}","null|X<{X f1}>"); }
	@Test public void test_157() { checkNotSubtype("{{null f1} f2}","null|X<{X f2}>"); }
	@Test public void test_158() { checkNotSubtype("{{null f1} f2}","null|{null f1}"); }
	@Test public void test_159() { checkNotSubtype("{{null f1} f2}","null|{null f2}"); }
	@Test public void test_160() { checkNotSubtype("{{null f1} f2}","null|(X<null|X>)"); }
	@Test public void test_161() { checkNotSubtype("{{null f1} f2}","{null f1}|null"); }
	@Test public void test_162() { checkNotSubtype("{{null f1} f2}","{null f2}|null"); }
	@Test public void test_163() { checkNotSubtype("{{null f1} f2}","X<{X f1}>|null"); }
	@Test public void test_164() { checkNotSubtype("{{null f1} f2}","X<{X f2}>|null"); }
	@Test public void test_165() { checkNotSubtype("{{null f1} f2}","X<X|{null f1}>"); }
	@Test public void test_166() { checkNotSubtype("{{null f1} f2}","X<X|{null f2}>"); }
	@Test public void test_167() { checkNotSubtype("{{null f1} f2}","(X<X|null>)|null"); }
	@Test public void test_168() { checkNotSubtype("{{null f1} f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_169() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_170() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test public void test_171() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test public void test_172() { checkNotSubtype("{{null f2} f2}","X<X|null>"); }
	@Test public void test_173() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test public void test_174() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test public void test_175() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test public void test_176() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test public void test_177() { checkNotSubtype("{{null f2} f2}","{X<X|null> f1}"); }
	@Test public void test_178() { checkNotSubtype("{{null f2} f2}","{X<X|null> f2}"); }
	@Test public void test_179() { checkNotSubtype("{{null f2} f2}","null|null"); }
	@Test public void test_180() { checkNotSubtype("{{null f2} f2}","null|X<{X f1}>"); }
	@Test public void test_181() { checkNotSubtype("{{null f2} f2}","null|X<{X f2}>"); }
	@Test public void test_182() { checkNotSubtype("{{null f2} f2}","null|{null f1}"); }
	@Test public void test_183() { checkNotSubtype("{{null f2} f2}","null|{null f2}"); }
	@Test public void test_184() { checkNotSubtype("{{null f2} f2}","null|(X<null|X>)"); }
	@Test public void test_185() { checkNotSubtype("{{null f2} f2}","{null f1}|null"); }
	@Test public void test_186() { checkNotSubtype("{{null f2} f2}","{null f2}|null"); }
	@Test public void test_187() { checkNotSubtype("{{null f2} f2}","X<{X f1}>|null"); }
	@Test public void test_188() { checkNotSubtype("{{null f2} f2}","X<{X f2}>|null"); }
	@Test public void test_189() { checkNotSubtype("{{null f2} f2}","X<X|{null f1}>"); }
	@Test public void test_190() { checkNotSubtype("{{null f2} f2}","X<X|{null f2}>"); }
	@Test public void test_191() { checkNotSubtype("{{null f2} f2}","(X<X|null>)|null"); }
	@Test public void test_192() { checkNotSubtype("{{null f2} f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_193() { checkNotSubtype("{X<X|null> f1}","null"); }
	@Test public void test_194() { checkIsSubtype("{X<X|null> f1}","{null f1}"); }
	@Test public void test_195() { checkNotSubtype("{X<X|null> f1}","{null f2}"); }
	@Test public void test_196() { checkNotSubtype("{X<X|null> f1}","X<X|null>"); }
	@Test public void test_197() { checkNotSubtype("{X<X|null> f1}","{{null f1} f1}"); }
	@Test public void test_198() { checkNotSubtype("{X<X|null> f1}","{{null f2} f1}"); }
	@Test public void test_199() { checkNotSubtype("{X<X|null> f1}","{{null f1} f2}"); }
	@Test public void test_200() { checkNotSubtype("{X<X|null> f1}","{{null f2} f2}"); }
	@Test public void test_201() { checkIsSubtype("{X<X|null> f1}","{X<X|null> f1}"); }
	@Test public void test_202() { checkNotSubtype("{X<X|null> f1}","{X<X|null> f2}"); }
	@Test public void test_203() { checkNotSubtype("{X<X|null> f1}","null|null"); }
	@Test public void test_204() { checkNotSubtype("{X<X|null> f1}","null|X<{X f1}>"); }
	@Test public void test_205() { checkNotSubtype("{X<X|null> f1}","null|X<{X f2}>"); }
	@Test public void test_206() { checkNotSubtype("{X<X|null> f1}","null|{null f1}"); }
	@Test public void test_207() { checkNotSubtype("{X<X|null> f1}","null|{null f2}"); }
	@Test public void test_208() { checkNotSubtype("{X<X|null> f1}","null|(X<null|X>)"); }
	@Test public void test_209() { checkNotSubtype("{X<X|null> f1}","{null f1}|null"); }
	@Test public void test_210() { checkNotSubtype("{X<X|null> f1}","{null f2}|null"); }
	@Test public void test_211() { checkNotSubtype("{X<X|null> f1}","X<{X f1}>|null"); }
	@Test public void test_212() { checkNotSubtype("{X<X|null> f1}","X<{X f2}>|null"); }
	@Test public void test_213() { checkIsSubtype("{X<X|null> f1}","X<X|{null f1}>"); }
	@Test public void test_214() { checkNotSubtype("{X<X|null> f1}","X<X|{null f2}>"); }
	@Test public void test_215() { checkNotSubtype("{X<X|null> f1}","(X<X|null>)|null"); }
	@Test public void test_216() { checkNotSubtype("{X<X|null> f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_217() { checkNotSubtype("{X<X|null> f2}","null"); }
	@Test public void test_218() { checkNotSubtype("{X<X|null> f2}","{null f1}"); }
	@Test public void test_219() { checkIsSubtype("{X<X|null> f2}","{null f2}"); }
	@Test public void test_220() { checkNotSubtype("{X<X|null> f2}","X<X|null>"); }
	@Test public void test_221() { checkNotSubtype("{X<X|null> f2}","{{null f1} f1}"); }
	@Test public void test_222() { checkNotSubtype("{X<X|null> f2}","{{null f2} f1}"); }
	@Test public void test_223() { checkNotSubtype("{X<X|null> f2}","{{null f1} f2}"); }
	@Test public void test_224() { checkNotSubtype("{X<X|null> f2}","{{null f2} f2}"); }
	@Test public void test_225() { checkNotSubtype("{X<X|null> f2}","{X<X|null> f1}"); }
	@Test public void test_226() { checkIsSubtype("{X<X|null> f2}","{X<X|null> f2}"); }
	@Test public void test_227() { checkNotSubtype("{X<X|null> f2}","null|null"); }
	@Test public void test_228() { checkNotSubtype("{X<X|null> f2}","null|X<{X f1}>"); }
	@Test public void test_229() { checkNotSubtype("{X<X|null> f2}","null|X<{X f2}>"); }
	@Test public void test_230() { checkNotSubtype("{X<X|null> f2}","null|{null f1}"); }
	@Test public void test_231() { checkNotSubtype("{X<X|null> f2}","null|{null f2}"); }
	@Test public void test_232() { checkNotSubtype("{X<X|null> f2}","null|(X<null|X>)"); }
	@Test public void test_233() { checkNotSubtype("{X<X|null> f2}","{null f1}|null"); }
	@Test public void test_234() { checkNotSubtype("{X<X|null> f2}","{null f2}|null"); }
	@Test public void test_235() { checkNotSubtype("{X<X|null> f2}","X<{X f1}>|null"); }
	@Test public void test_236() { checkNotSubtype("{X<X|null> f2}","X<{X f2}>|null"); }
	@Test public void test_237() { checkNotSubtype("{X<X|null> f2}","X<X|{null f1}>"); }
	@Test public void test_238() { checkIsSubtype("{X<X|null> f2}","X<X|{null f2}>"); }
	@Test public void test_239() { checkNotSubtype("{X<X|null> f2}","(X<X|null>)|null"); }
	@Test public void test_240() { checkNotSubtype("{X<X|null> f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_241() { checkIsSubtype("null|null","null"); }
	@Test public void test_242() { checkNotSubtype("null|null","{null f1}"); }
	@Test public void test_243() { checkNotSubtype("null|null","{null f2}"); }
	@Test public void test_244() { checkIsSubtype("null|null","X<X|null>"); }
	@Test public void test_245() { checkNotSubtype("null|null","{{null f1} f1}"); }
	@Test public void test_246() { checkNotSubtype("null|null","{{null f2} f1}"); }
	@Test public void test_247() { checkNotSubtype("null|null","{{null f1} f2}"); }
	@Test public void test_248() { checkNotSubtype("null|null","{{null f2} f2}"); }
	@Test public void test_249() { checkNotSubtype("null|null","{X<X|null> f1}"); }
	@Test public void test_250() { checkNotSubtype("null|null","{X<X|null> f2}"); }
	@Test public void test_251() { checkIsSubtype("null|null","null|null"); }
	@Test public void test_252() { checkIsSubtype("null|null","null|X<{X f1}>"); }
	@Test public void test_253() { checkIsSubtype("null|null","null|X<{X f2}>"); }
	@Test public void test_254() { checkNotSubtype("null|null","null|{null f1}"); }
	@Test public void test_255() { checkNotSubtype("null|null","null|{null f2}"); }
	@Test public void test_256() { checkIsSubtype("null|null","null|(X<null|X>)"); }
	@Test public void test_257() { checkNotSubtype("null|null","{null f1}|null"); }
	@Test public void test_258() { checkNotSubtype("null|null","{null f2}|null"); }
	@Test public void test_259() { checkIsSubtype("null|null","X<{X f1}>|null"); }
	@Test public void test_260() { checkIsSubtype("null|null","X<{X f2}>|null"); }
	@Test public void test_261() { checkNotSubtype("null|null","X<X|{null f1}>"); }
	@Test public void test_262() { checkNotSubtype("null|null","X<X|{null f2}>"); }
	@Test public void test_263() { checkIsSubtype("null|null","(X<X|null>)|null"); }
	@Test public void test_264() { checkIsSubtype("null|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_265() { checkIsSubtype("null|X<{X f1}>","null"); }
	@Test public void test_266() { checkNotSubtype("null|X<{X f1}>","{null f1}"); }
	@Test public void test_267() { checkNotSubtype("null|X<{X f1}>","{null f2}"); }
	@Test public void test_268() { checkIsSubtype("null|X<{X f1}>","X<X|null>"); }
	@Test public void test_269() { checkNotSubtype("null|X<{X f1}>","{{null f1} f1}"); }
	@Test public void test_270() { checkNotSubtype("null|X<{X f1}>","{{null f2} f1}"); }
	@Test public void test_271() { checkNotSubtype("null|X<{X f1}>","{{null f1} f2}"); }
	@Test public void test_272() { checkNotSubtype("null|X<{X f1}>","{{null f2} f2}"); }
	@Test public void test_273() { checkNotSubtype("null|X<{X f1}>","{X<X|null> f1}"); }
	@Test public void test_274() { checkNotSubtype("null|X<{X f1}>","{X<X|null> f2}"); }
	@Test public void test_275() { checkIsSubtype("null|X<{X f1}>","null|null"); }
	@Test public void test_276() { checkIsSubtype("null|X<{X f1}>","null|X<{X f1}>"); }
	@Test public void test_277() { checkIsSubtype("null|X<{X f1}>","null|X<{X f2}>"); }
	@Test public void test_278() { checkNotSubtype("null|X<{X f1}>","null|{null f1}"); }
	@Test public void test_279() { checkNotSubtype("null|X<{X f1}>","null|{null f2}"); }
	@Test public void test_280() { checkIsSubtype("null|X<{X f1}>","null|(X<null|X>)"); }
	@Test public void test_281() { checkNotSubtype("null|X<{X f1}>","{null f1}|null"); }
	@Test public void test_282() { checkNotSubtype("null|X<{X f1}>","{null f2}|null"); }
	@Test public void test_283() { checkIsSubtype("null|X<{X f1}>","X<{X f1}>|null"); }
	@Test public void test_284() { checkIsSubtype("null|X<{X f1}>","X<{X f2}>|null"); }
	@Test public void test_285() { checkNotSubtype("null|X<{X f1}>","X<X|{null f1}>"); }
	@Test public void test_286() { checkNotSubtype("null|X<{X f1}>","X<X|{null f2}>"); }
	@Test public void test_287() { checkIsSubtype("null|X<{X f1}>","(X<X|null>)|null"); }
	@Test public void test_288() { checkIsSubtype("null|X<{X f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_289() { checkIsSubtype("null|X<{X f2}>","null"); }
	@Test public void test_290() { checkNotSubtype("null|X<{X f2}>","{null f1}"); }
	@Test public void test_291() { checkNotSubtype("null|X<{X f2}>","{null f2}"); }
	@Test public void test_292() { checkIsSubtype("null|X<{X f2}>","X<X|null>"); }
	@Test public void test_293() { checkNotSubtype("null|X<{X f2}>","{{null f1} f1}"); }
	@Test public void test_294() { checkNotSubtype("null|X<{X f2}>","{{null f2} f1}"); }
	@Test public void test_295() { checkNotSubtype("null|X<{X f2}>","{{null f1} f2}"); }
	@Test public void test_296() { checkNotSubtype("null|X<{X f2}>","{{null f2} f2}"); }
	@Test public void test_297() { checkNotSubtype("null|X<{X f2}>","{X<X|null> f1}"); }
	@Test public void test_298() { checkNotSubtype("null|X<{X f2}>","{X<X|null> f2}"); }
	@Test public void test_299() { checkIsSubtype("null|X<{X f2}>","null|null"); }
	@Test public void test_300() { checkIsSubtype("null|X<{X f2}>","null|X<{X f1}>"); }
	@Test public void test_301() { checkIsSubtype("null|X<{X f2}>","null|X<{X f2}>"); }
	@Test public void test_302() { checkNotSubtype("null|X<{X f2}>","null|{null f1}"); }
	@Test public void test_303() { checkNotSubtype("null|X<{X f2}>","null|{null f2}"); }
	@Test public void test_304() { checkIsSubtype("null|X<{X f2}>","null|(X<null|X>)"); }
	@Test public void test_305() { checkNotSubtype("null|X<{X f2}>","{null f1}|null"); }
	@Test public void test_306() { checkNotSubtype("null|X<{X f2}>","{null f2}|null"); }
	@Test public void test_307() { checkIsSubtype("null|X<{X f2}>","X<{X f1}>|null"); }
	@Test public void test_308() { checkIsSubtype("null|X<{X f2}>","X<{X f2}>|null"); }
	@Test public void test_309() { checkNotSubtype("null|X<{X f2}>","X<X|{null f1}>"); }
	@Test public void test_310() { checkNotSubtype("null|X<{X f2}>","X<X|{null f2}>"); }
	@Test public void test_311() { checkIsSubtype("null|X<{X f2}>","(X<X|null>)|null"); }
	@Test public void test_312() { checkIsSubtype("null|X<{X f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_313() { checkIsSubtype("null|{null f1}","null"); }
	@Test public void test_314() { checkIsSubtype("null|{null f1}","{null f1}"); }
	@Test public void test_315() { checkNotSubtype("null|{null f1}","{null f2}"); }
	@Test public void test_316() { checkIsSubtype("null|{null f1}","X<X|null>"); }
	@Test public void test_317() { checkNotSubtype("null|{null f1}","{{null f1} f1}"); }
	@Test public void test_318() { checkNotSubtype("null|{null f1}","{{null f2} f1}"); }
	@Test public void test_319() { checkNotSubtype("null|{null f1}","{{null f1} f2}"); }
	@Test public void test_320() { checkNotSubtype("null|{null f1}","{{null f2} f2}"); }
	@Test public void test_321() { checkIsSubtype("null|{null f1}","{X<X|null> f1}"); }
	@Test public void test_322() { checkNotSubtype("null|{null f1}","{X<X|null> f2}"); }
	@Test public void test_323() { checkIsSubtype("null|{null f1}","null|null"); }
	@Test public void test_324() { checkIsSubtype("null|{null f1}","null|X<{X f1}>"); }
	@Test public void test_325() { checkIsSubtype("null|{null f1}","null|X<{X f2}>"); }
	@Test public void test_326() { checkIsSubtype("null|{null f1}","null|{null f1}"); }
	@Test public void test_327() { checkNotSubtype("null|{null f1}","null|{null f2}"); }
	@Test public void test_328() { checkIsSubtype("null|{null f1}","null|(X<null|X>)"); }
	@Test public void test_329() { checkIsSubtype("null|{null f1}","{null f1}|null"); }
	@Test public void test_330() { checkNotSubtype("null|{null f1}","{null f2}|null"); }
	@Test public void test_331() { checkIsSubtype("null|{null f1}","X<{X f1}>|null"); }
	@Test public void test_332() { checkIsSubtype("null|{null f1}","X<{X f2}>|null"); }
	@Test public void test_333() { checkIsSubtype("null|{null f1}","X<X|{null f1}>"); }
	@Test public void test_334() { checkNotSubtype("null|{null f1}","X<X|{null f2}>"); }
	@Test public void test_335() { checkIsSubtype("null|{null f1}","(X<X|null>)|null"); }
	@Test public void test_336() { checkIsSubtype("null|{null f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_337() { checkIsSubtype("null|{null f2}","null"); }
	@Test public void test_338() { checkNotSubtype("null|{null f2}","{null f1}"); }
	@Test public void test_339() { checkIsSubtype("null|{null f2}","{null f2}"); }
	@Test public void test_340() { checkIsSubtype("null|{null f2}","X<X|null>"); }
	@Test public void test_341() { checkNotSubtype("null|{null f2}","{{null f1} f1}"); }
	@Test public void test_342() { checkNotSubtype("null|{null f2}","{{null f2} f1}"); }
	@Test public void test_343() { checkNotSubtype("null|{null f2}","{{null f1} f2}"); }
	@Test public void test_344() { checkNotSubtype("null|{null f2}","{{null f2} f2}"); }
	@Test public void test_345() { checkNotSubtype("null|{null f2}","{X<X|null> f1}"); }
	@Test public void test_346() { checkIsSubtype("null|{null f2}","{X<X|null> f2}"); }
	@Test public void test_347() { checkIsSubtype("null|{null f2}","null|null"); }
	@Test public void test_348() { checkIsSubtype("null|{null f2}","null|X<{X f1}>"); }
	@Test public void test_349() { checkIsSubtype("null|{null f2}","null|X<{X f2}>"); }
	@Test public void test_350() { checkNotSubtype("null|{null f2}","null|{null f1}"); }
	@Test public void test_351() { checkIsSubtype("null|{null f2}","null|{null f2}"); }
	@Test public void test_352() { checkIsSubtype("null|{null f2}","null|(X<null|X>)"); }
	@Test public void test_353() { checkNotSubtype("null|{null f2}","{null f1}|null"); }
	@Test public void test_354() { checkIsSubtype("null|{null f2}","{null f2}|null"); }
	@Test public void test_355() { checkIsSubtype("null|{null f2}","X<{X f1}>|null"); }
	@Test public void test_356() { checkIsSubtype("null|{null f2}","X<{X f2}>|null"); }
	@Test public void test_357() { checkNotSubtype("null|{null f2}","X<X|{null f1}>"); }
	@Test public void test_358() { checkIsSubtype("null|{null f2}","X<X|{null f2}>"); }
	@Test public void test_359() { checkIsSubtype("null|{null f2}","(X<X|null>)|null"); }
	@Test public void test_360() { checkIsSubtype("null|{null f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_361() { checkIsSubtype("null|(X<null|X>)","null"); }
	@Test public void test_362() { checkNotSubtype("null|(X<null|X>)","{null f1}"); }
	@Test public void test_363() { checkNotSubtype("null|(X<null|X>)","{null f2}"); }
	@Test public void test_364() { checkIsSubtype("null|(X<null|X>)","X<X|null>"); }
	@Test public void test_365() { checkNotSubtype("null|(X<null|X>)","{{null f1} f1}"); }
	@Test public void test_366() { checkNotSubtype("null|(X<null|X>)","{{null f2} f1}"); }
	@Test public void test_367() { checkNotSubtype("null|(X<null|X>)","{{null f1} f2}"); }
	@Test public void test_368() { checkNotSubtype("null|(X<null|X>)","{{null f2} f2}"); }
	@Test public void test_369() { checkNotSubtype("null|(X<null|X>)","{X<X|null> f1}"); }
	@Test public void test_370() { checkNotSubtype("null|(X<null|X>)","{X<X|null> f2}"); }
	@Test public void test_371() { checkIsSubtype("null|(X<null|X>)","null|null"); }
	@Test public void test_372() { checkIsSubtype("null|(X<null|X>)","null|X<{X f1}>"); }
	@Test public void test_373() { checkIsSubtype("null|(X<null|X>)","null|X<{X f2}>"); }
	@Test public void test_374() { checkNotSubtype("null|(X<null|X>)","null|{null f1}"); }
	@Test public void test_375() { checkNotSubtype("null|(X<null|X>)","null|{null f2}"); }
	@Test public void test_376() { checkIsSubtype("null|(X<null|X>)","null|(X<null|X>)"); }
	@Test public void test_377() { checkNotSubtype("null|(X<null|X>)","{null f1}|null"); }
	@Test public void test_378() { checkNotSubtype("null|(X<null|X>)","{null f2}|null"); }
	@Test public void test_379() { checkIsSubtype("null|(X<null|X>)","X<{X f1}>|null"); }
	@Test public void test_380() { checkIsSubtype("null|(X<null|X>)","X<{X f2}>|null"); }
	@Test public void test_381() { checkNotSubtype("null|(X<null|X>)","X<X|{null f1}>"); }
	@Test public void test_382() { checkNotSubtype("null|(X<null|X>)","X<X|{null f2}>"); }
	@Test public void test_383() { checkIsSubtype("null|(X<null|X>)","(X<X|null>)|null"); }
	@Test public void test_384() { checkIsSubtype("null|(X<null|X>)","X<X|(Y<Y|null>)>"); }
	@Test public void test_385() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_386() { checkIsSubtype("{null f1}|null","{null f1}"); }
	@Test public void test_387() { checkNotSubtype("{null f1}|null","{null f2}"); }
	@Test public void test_388() { checkIsSubtype("{null f1}|null","X<X|null>"); }
	@Test public void test_389() { checkNotSubtype("{null f1}|null","{{null f1} f1}"); }
	@Test public void test_390() { checkNotSubtype("{null f1}|null","{{null f2} f1}"); }
	@Test public void test_391() { checkNotSubtype("{null f1}|null","{{null f1} f2}"); }
	@Test public void test_392() { checkNotSubtype("{null f1}|null","{{null f2} f2}"); }
	@Test public void test_393() { checkIsSubtype("{null f1}|null","{X<X|null> f1}"); }
	@Test public void test_394() { checkNotSubtype("{null f1}|null","{X<X|null> f2}"); }
	@Test public void test_395() { checkIsSubtype("{null f1}|null","null|null"); }
	@Test public void test_396() { checkIsSubtype("{null f1}|null","null|X<{X f1}>"); }
	@Test public void test_397() { checkIsSubtype("{null f1}|null","null|X<{X f2}>"); }
	@Test public void test_398() { checkIsSubtype("{null f1}|null","null|{null f1}"); }
	@Test public void test_399() { checkNotSubtype("{null f1}|null","null|{null f2}"); }
	@Test public void test_400() { checkIsSubtype("{null f1}|null","null|(X<null|X>)"); }
	@Test public void test_401() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test public void test_402() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test public void test_403() { checkIsSubtype("{null f1}|null","X<{X f1}>|null"); }
	@Test public void test_404() { checkIsSubtype("{null f1}|null","X<{X f2}>|null"); }
	@Test public void test_405() { checkIsSubtype("{null f1}|null","X<X|{null f1}>"); }
	@Test public void test_406() { checkNotSubtype("{null f1}|null","X<X|{null f2}>"); }
	@Test public void test_407() { checkIsSubtype("{null f1}|null","(X<X|null>)|null"); }
	@Test public void test_408() { checkIsSubtype("{null f1}|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_409() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_410() { checkNotSubtype("{null f2}|null","{null f1}"); }
	@Test public void test_411() { checkIsSubtype("{null f2}|null","{null f2}"); }
	@Test public void test_412() { checkIsSubtype("{null f2}|null","X<X|null>"); }
	@Test public void test_413() { checkNotSubtype("{null f2}|null","{{null f1} f1}"); }
	@Test public void test_414() { checkNotSubtype("{null f2}|null","{{null f2} f1}"); }
	@Test public void test_415() { checkNotSubtype("{null f2}|null","{{null f1} f2}"); }
	@Test public void test_416() { checkNotSubtype("{null f2}|null","{{null f2} f2}"); }
	@Test public void test_417() { checkNotSubtype("{null f2}|null","{X<X|null> f1}"); }
	@Test public void test_418() { checkIsSubtype("{null f2}|null","{X<X|null> f2}"); }
	@Test public void test_419() { checkIsSubtype("{null f2}|null","null|null"); }
	@Test public void test_420() { checkIsSubtype("{null f2}|null","null|X<{X f1}>"); }
	@Test public void test_421() { checkIsSubtype("{null f2}|null","null|X<{X f2}>"); }
	@Test public void test_422() { checkNotSubtype("{null f2}|null","null|{null f1}"); }
	@Test public void test_423() { checkIsSubtype("{null f2}|null","null|{null f2}"); }
	@Test public void test_424() { checkIsSubtype("{null f2}|null","null|(X<null|X>)"); }
	@Test public void test_425() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test public void test_426() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test public void test_427() { checkIsSubtype("{null f2}|null","X<{X f1}>|null"); }
	@Test public void test_428() { checkIsSubtype("{null f2}|null","X<{X f2}>|null"); }
	@Test public void test_429() { checkNotSubtype("{null f2}|null","X<X|{null f1}>"); }
	@Test public void test_430() { checkIsSubtype("{null f2}|null","X<X|{null f2}>"); }
	@Test public void test_431() { checkIsSubtype("{null f2}|null","(X<X|null>)|null"); }
	@Test public void test_432() { checkIsSubtype("{null f2}|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_433() { checkIsSubtype("X<{X f1}>|null","null"); }
	@Test public void test_434() { checkNotSubtype("X<{X f1}>|null","{null f1}"); }
	@Test public void test_435() { checkNotSubtype("X<{X f1}>|null","{null f2}"); }
	@Test public void test_436() { checkIsSubtype("X<{X f1}>|null","X<X|null>"); }
	@Test public void test_437() { checkNotSubtype("X<{X f1}>|null","{{null f1} f1}"); }
	@Test public void test_438() { checkNotSubtype("X<{X f1}>|null","{{null f2} f1}"); }
	@Test public void test_439() { checkNotSubtype("X<{X f1}>|null","{{null f1} f2}"); }
	@Test public void test_440() { checkNotSubtype("X<{X f1}>|null","{{null f2} f2}"); }
	@Test public void test_441() { checkNotSubtype("X<{X f1}>|null","{X<X|null> f1}"); }
	@Test public void test_442() { checkNotSubtype("X<{X f1}>|null","{X<X|null> f2}"); }
	@Test public void test_443() { checkIsSubtype("X<{X f1}>|null","null|null"); }
	@Test public void test_444() { checkIsSubtype("X<{X f1}>|null","null|X<{X f1}>"); }
	@Test public void test_445() { checkIsSubtype("X<{X f1}>|null","null|X<{X f2}>"); }
	@Test public void test_446() { checkNotSubtype("X<{X f1}>|null","null|{null f1}"); }
	@Test public void test_447() { checkNotSubtype("X<{X f1}>|null","null|{null f2}"); }
	@Test public void test_448() { checkIsSubtype("X<{X f1}>|null","null|(X<null|X>)"); }
	@Test public void test_449() { checkNotSubtype("X<{X f1}>|null","{null f1}|null"); }
	@Test public void test_450() { checkNotSubtype("X<{X f1}>|null","{null f2}|null"); }
	@Test public void test_451() { checkIsSubtype("X<{X f1}>|null","X<{X f1}>|null"); }
	@Test public void test_452() { checkIsSubtype("X<{X f1}>|null","X<{X f2}>|null"); }
	@Test public void test_453() { checkNotSubtype("X<{X f1}>|null","X<X|{null f1}>"); }
	@Test public void test_454() { checkNotSubtype("X<{X f1}>|null","X<X|{null f2}>"); }
	@Test public void test_455() { checkIsSubtype("X<{X f1}>|null","(X<X|null>)|null"); }
	@Test public void test_456() { checkIsSubtype("X<{X f1}>|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_457() { checkIsSubtype("X<{X f2}>|null","null"); }
	@Test public void test_458() { checkNotSubtype("X<{X f2}>|null","{null f1}"); }
	@Test public void test_459() { checkNotSubtype("X<{X f2}>|null","{null f2}"); }
	@Test public void test_460() { checkIsSubtype("X<{X f2}>|null","X<X|null>"); }
	@Test public void test_461() { checkNotSubtype("X<{X f2}>|null","{{null f1} f1}"); }
	@Test public void test_462() { checkNotSubtype("X<{X f2}>|null","{{null f2} f1}"); }
	@Test public void test_463() { checkNotSubtype("X<{X f2}>|null","{{null f1} f2}"); }
	@Test public void test_464() { checkNotSubtype("X<{X f2}>|null","{{null f2} f2}"); }
	@Test public void test_465() { checkNotSubtype("X<{X f2}>|null","{X<X|null> f1}"); }
	@Test public void test_466() { checkNotSubtype("X<{X f2}>|null","{X<X|null> f2}"); }
	@Test public void test_467() { checkIsSubtype("X<{X f2}>|null","null|null"); }
	@Test public void test_468() { checkIsSubtype("X<{X f2}>|null","null|X<{X f1}>"); }
	@Test public void test_469() { checkIsSubtype("X<{X f2}>|null","null|X<{X f2}>"); }
	@Test public void test_470() { checkNotSubtype("X<{X f2}>|null","null|{null f1}"); }
	@Test public void test_471() { checkNotSubtype("X<{X f2}>|null","null|{null f2}"); }
	@Test public void test_472() { checkIsSubtype("X<{X f2}>|null","null|(X<null|X>)"); }
	@Test public void test_473() { checkNotSubtype("X<{X f2}>|null","{null f1}|null"); }
	@Test public void test_474() { checkNotSubtype("X<{X f2}>|null","{null f2}|null"); }
	@Test public void test_475() { checkIsSubtype("X<{X f2}>|null","X<{X f1}>|null"); }
	@Test public void test_476() { checkIsSubtype("X<{X f2}>|null","X<{X f2}>|null"); }
	@Test public void test_477() { checkNotSubtype("X<{X f2}>|null","X<X|{null f1}>"); }
	@Test public void test_478() { checkNotSubtype("X<{X f2}>|null","X<X|{null f2}>"); }
	@Test public void test_479() { checkIsSubtype("X<{X f2}>|null","(X<X|null>)|null"); }
	@Test public void test_480() { checkIsSubtype("X<{X f2}>|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_481() { checkNotSubtype("X<X|{null f1}>","null"); }
	@Test public void test_482() { checkIsSubtype("X<X|{null f1}>","{null f1}"); }
	@Test public void test_483() { checkNotSubtype("X<X|{null f1}>","{null f2}"); }
	@Test public void test_484() { checkNotSubtype("X<X|{null f1}>","X<X|null>"); }
	@Test public void test_485() { checkNotSubtype("X<X|{null f1}>","{{null f1} f1}"); }
	@Test public void test_486() { checkNotSubtype("X<X|{null f1}>","{{null f2} f1}"); }
	@Test public void test_487() { checkNotSubtype("X<X|{null f1}>","{{null f1} f2}"); }
	@Test public void test_488() { checkNotSubtype("X<X|{null f1}>","{{null f2} f2}"); }
	@Test public void test_489() { checkIsSubtype("X<X|{null f1}>","{X<X|null> f1}"); }
	@Test public void test_490() { checkNotSubtype("X<X|{null f1}>","{X<X|null> f2}"); }
	@Test public void test_491() { checkNotSubtype("X<X|{null f1}>","null|null"); }
	@Test public void test_492() { checkNotSubtype("X<X|{null f1}>","null|X<{X f1}>"); }
	@Test public void test_493() { checkNotSubtype("X<X|{null f1}>","null|X<{X f2}>"); }
	@Test public void test_494() { checkNotSubtype("X<X|{null f1}>","null|{null f1}"); }
	@Test public void test_495() { checkNotSubtype("X<X|{null f1}>","null|{null f2}"); }
	@Test public void test_496() { checkNotSubtype("X<X|{null f1}>","null|(X<null|X>)"); }
	@Test public void test_497() { checkNotSubtype("X<X|{null f1}>","{null f1}|null"); }
	@Test public void test_498() { checkNotSubtype("X<X|{null f1}>","{null f2}|null"); }
	@Test public void test_499() { checkNotSubtype("X<X|{null f1}>","X<{X f1}>|null"); }
	@Test public void test_500() { checkNotSubtype("X<X|{null f1}>","X<{X f2}>|null"); }
	@Test public void test_501() { checkIsSubtype("X<X|{null f1}>","X<X|{null f1}>"); }
	@Test public void test_502() { checkNotSubtype("X<X|{null f1}>","X<X|{null f2}>"); }
	@Test public void test_503() { checkNotSubtype("X<X|{null f1}>","(X<X|null>)|null"); }
	@Test public void test_504() { checkNotSubtype("X<X|{null f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_505() { checkNotSubtype("X<X|{null f2}>","null"); }
	@Test public void test_506() { checkNotSubtype("X<X|{null f2}>","{null f1}"); }
	@Test public void test_507() { checkIsSubtype("X<X|{null f2}>","{null f2}"); }
	@Test public void test_508() { checkNotSubtype("X<X|{null f2}>","X<X|null>"); }
	@Test public void test_509() { checkNotSubtype("X<X|{null f2}>","{{null f1} f1}"); }
	@Test public void test_510() { checkNotSubtype("X<X|{null f2}>","{{null f2} f1}"); }
	@Test public void test_511() { checkNotSubtype("X<X|{null f2}>","{{null f1} f2}"); }
	@Test public void test_512() { checkNotSubtype("X<X|{null f2}>","{{null f2} f2}"); }
	@Test public void test_513() { checkNotSubtype("X<X|{null f2}>","{X<X|null> f1}"); }
	@Test public void test_514() { checkIsSubtype("X<X|{null f2}>","{X<X|null> f2}"); }
	@Test public void test_515() { checkNotSubtype("X<X|{null f2}>","null|null"); }
	@Test public void test_516() { checkNotSubtype("X<X|{null f2}>","null|X<{X f1}>"); }
	@Test public void test_517() { checkNotSubtype("X<X|{null f2}>","null|X<{X f2}>"); }
	@Test public void test_518() { checkNotSubtype("X<X|{null f2}>","null|{null f1}"); }
	@Test public void test_519() { checkNotSubtype("X<X|{null f2}>","null|{null f2}"); }
	@Test public void test_520() { checkNotSubtype("X<X|{null f2}>","null|(X<null|X>)"); }
	@Test public void test_521() { checkNotSubtype("X<X|{null f2}>","{null f1}|null"); }
	@Test public void test_522() { checkNotSubtype("X<X|{null f2}>","{null f2}|null"); }
	@Test public void test_523() { checkNotSubtype("X<X|{null f2}>","X<{X f1}>|null"); }
	@Test public void test_524() { checkNotSubtype("X<X|{null f2}>","X<{X f2}>|null"); }
	@Test public void test_525() { checkNotSubtype("X<X|{null f2}>","X<X|{null f1}>"); }
	@Test public void test_526() { checkIsSubtype("X<X|{null f2}>","X<X|{null f2}>"); }
	@Test public void test_527() { checkNotSubtype("X<X|{null f2}>","(X<X|null>)|null"); }
	@Test public void test_528() { checkNotSubtype("X<X|{null f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_529() { checkIsSubtype("(X<X|null>)|null","null"); }
	@Test public void test_530() { checkNotSubtype("(X<X|null>)|null","{null f1}"); }
	@Test public void test_531() { checkNotSubtype("(X<X|null>)|null","{null f2}"); }
	@Test public void test_532() { checkIsSubtype("(X<X|null>)|null","X<X|null>"); }
	@Test public void test_533() { checkNotSubtype("(X<X|null>)|null","{{null f1} f1}"); }
	@Test public void test_534() { checkNotSubtype("(X<X|null>)|null","{{null f2} f1}"); }
	@Test public void test_535() { checkNotSubtype("(X<X|null>)|null","{{null f1} f2}"); }
	@Test public void test_536() { checkNotSubtype("(X<X|null>)|null","{{null f2} f2}"); }
	@Test public void test_537() { checkNotSubtype("(X<X|null>)|null","{X<X|null> f1}"); }
	@Test public void test_538() { checkNotSubtype("(X<X|null>)|null","{X<X|null> f2}"); }
	@Test public void test_539() { checkIsSubtype("(X<X|null>)|null","null|null"); }
	@Test public void test_540() { checkIsSubtype("(X<X|null>)|null","null|X<{X f1}>"); }
	@Test public void test_541() { checkIsSubtype("(X<X|null>)|null","null|X<{X f2}>"); }
	@Test public void test_542() { checkNotSubtype("(X<X|null>)|null","null|{null f1}"); }
	@Test public void test_543() { checkNotSubtype("(X<X|null>)|null","null|{null f2}"); }
	@Test public void test_544() { checkIsSubtype("(X<X|null>)|null","null|(X<null|X>)"); }
	@Test public void test_545() { checkNotSubtype("(X<X|null>)|null","{null f1}|null"); }
	@Test public void test_546() { checkNotSubtype("(X<X|null>)|null","{null f2}|null"); }
	@Test public void test_547() { checkIsSubtype("(X<X|null>)|null","X<{X f1}>|null"); }
	@Test public void test_548() { checkIsSubtype("(X<X|null>)|null","X<{X f2}>|null"); }
	@Test public void test_549() { checkNotSubtype("(X<X|null>)|null","X<X|{null f1}>"); }
	@Test public void test_550() { checkNotSubtype("(X<X|null>)|null","X<X|{null f2}>"); }
	@Test public void test_551() { checkIsSubtype("(X<X|null>)|null","(X<X|null>)|null"); }
	@Test public void test_552() { checkIsSubtype("(X<X|null>)|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_553() { checkIsSubtype("X<X|(Y<Y|null>)>","null"); }
	@Test public void test_554() { checkNotSubtype("X<X|(Y<Y|null>)>","{null f1}"); }
	@Test public void test_555() { checkNotSubtype("X<X|(Y<Y|null>)>","{null f2}"); }
	@Test public void test_556() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|null>"); }
	@Test public void test_557() { checkNotSubtype("X<X|(Y<Y|null>)>","{{null f1} f1}"); }
	@Test public void test_558() { checkNotSubtype("X<X|(Y<Y|null>)>","{{null f2} f1}"); }
	@Test public void test_559() { checkNotSubtype("X<X|(Y<Y|null>)>","{{null f1} f2}"); }
	@Test public void test_560() { checkNotSubtype("X<X|(Y<Y|null>)>","{{null f2} f2}"); }
	@Test public void test_561() { checkNotSubtype("X<X|(Y<Y|null>)>","{X<X|null> f1}"); }
	@Test public void test_562() { checkNotSubtype("X<X|(Y<Y|null>)>","{X<X|null> f2}"); }
	@Test public void test_563() { checkIsSubtype("X<X|(Y<Y|null>)>","null|null"); }
	@Test public void test_564() { checkIsSubtype("X<X|(Y<Y|null>)>","null|X<{X f1}>"); }
	@Test public void test_565() { checkIsSubtype("X<X|(Y<Y|null>)>","null|X<{X f2}>"); }
	@Test public void test_566() { checkNotSubtype("X<X|(Y<Y|null>)>","null|{null f1}"); }
	@Test public void test_567() { checkNotSubtype("X<X|(Y<Y|null>)>","null|{null f2}"); }
	@Test public void test_568() { checkIsSubtype("X<X|(Y<Y|null>)>","null|(X<null|X>)"); }
	@Test public void test_569() { checkNotSubtype("X<X|(Y<Y|null>)>","{null f1}|null"); }
	@Test public void test_570() { checkNotSubtype("X<X|(Y<Y|null>)>","{null f2}|null"); }
	@Test public void test_571() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f1}>|null"); }
	@Test public void test_572() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f2}>|null"); }
	@Test public void test_573() { checkNotSubtype("X<X|(Y<Y|null>)>","X<X|{null f1}>"); }
	@Test public void test_574() { checkNotSubtype("X<X|(Y<Y|null>)>","X<X|{null f2}>"); }
	@Test public void test_575() { checkIsSubtype("X<X|(Y<Y|null>)>","(X<X|null>)|null"); }
	@Test public void test_576() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|(Y<Y|null>)>"); }

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
