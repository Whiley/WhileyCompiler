// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class RecursiveSubtypeTests {
	@Test public void test_1() { checkIsSubtype("null","null"); }
	@Test public void test_2() { checkIsSubtype("null","X<{X f1}>"); }
	@Test public void test_3() { checkIsSubtype("null","X<{X f2}>"); }
	@Test public void test_4() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_5() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_6() { checkIsSubtype("null","{X<{X f1}> f1}"); }
	@Test public void test_7() { checkIsSubtype("null","{X<{X f2}> f1}"); }
	@Test public void test_8() { checkIsSubtype("null","{X<{X f1}> f2}"); }
	@Test public void test_9() { checkIsSubtype("null","{X<{X f2}> f2}"); }
	@Test public void test_10() { checkIsSubtype("null","X<X|null>"); }
	@Test public void test_11() { checkIsSubtype("null","X<X|Y<{Y f1}>>"); }
	@Test public void test_12() { checkIsSubtype("null","X<X|Y<{Y f2}>>"); }
	@Test public void test_13() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test public void test_14() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test public void test_15() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test public void test_16() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test public void test_17() { checkIsSubtype("null","{{X<{X f1}> f1} f1}"); }
	@Test public void test_18() { checkIsSubtype("null","{{X<{X f2}> f1} f1}"); }
	@Test public void test_19() { checkIsSubtype("null","{{X<{X f1}> f2} f1}"); }
	@Test public void test_20() { checkIsSubtype("null","{{X<{X f2}> f2} f1}"); }
	@Test public void test_21() { checkIsSubtype("null","{{X<{X f1}> f1} f2}"); }
	@Test public void test_22() { checkIsSubtype("null","{{X<{X f2}> f1} f2}"); }
	@Test public void test_23() { checkIsSubtype("null","{{X<{X f1}> f2} f2}"); }
	@Test public void test_24() { checkIsSubtype("null","{{X<{X f2}> f2} f2}"); }
	@Test public void test_25() { checkIsSubtype("null","X<{{{X f1} f1} f1}>"); }
	@Test public void test_26() { checkIsSubtype("null","X<{{{X f2} f1} f1}>"); }
	@Test public void test_27() { checkIsSubtype("null","X<{{{X f1} f2} f1}>"); }
	@Test public void test_28() { checkIsSubtype("null","X<{{{X f2} f2} f1}>"); }
	@Test public void test_29() { checkIsSubtype("null","X<{{{X f1} f1} f2}>"); }
	@Test public void test_30() { checkIsSubtype("null","X<{{{X f2} f1} f2}>"); }
	@Test public void test_31() { checkIsSubtype("null","X<{{{X f1} f2} f2}>"); }
	@Test public void test_32() { checkIsSubtype("null","X<{{{X f2} f2} f2}>"); }
	@Test public void test_33() { checkIsSubtype("null","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_34() { checkIsSubtype("null","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_35() { checkIsSubtype("null","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_36() { checkIsSubtype("null","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_37() { checkNotSubtype("null","{X<X|null> f1}"); }
	@Test public void test_38() { checkNotSubtype("null","{X<X|null> f2}"); }
	@Test public void test_39() { checkIsSubtype("null","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_40() { checkIsSubtype("null","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_41() { checkIsSubtype("null","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_42() { checkIsSubtype("null","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_43() { checkIsSubtype("null","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_44() { checkIsSubtype("null","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_45() { checkIsSubtype("null","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_46() { checkIsSubtype("null","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_47() { checkIsSubtype("null","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_48() { checkIsSubtype("null","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_49() { checkIsSubtype("null","null|null"); }
	@Test public void test_50() { checkIsSubtype("null","null|X<{X f1}>"); }
	@Test public void test_51() { checkIsSubtype("null","null|X<{X f2}>"); }
	@Test public void test_52() { checkNotSubtype("null","null|{null f1}"); }
	@Test public void test_53() { checkNotSubtype("null","null|{null f2}"); }
	@Test public void test_54() { checkIsSubtype("null","null|(X<null|X>)"); }
	@Test public void test_55() { checkNotSubtype("null","{null f1}|null"); }
	@Test public void test_56() { checkNotSubtype("null","{null f2}|null"); }
	@Test public void test_57() { checkIsSubtype("null","X<{X f1}>|null"); }
	@Test public void test_58() { checkIsSubtype("null","X<{X f2}>|null"); }
	@Test public void test_59() { checkNotSubtype("null","X<X|{null f1}>"); }
	@Test public void test_60() { checkNotSubtype("null","X<X|{null f2}>"); }
	@Test public void test_61() { checkIsSubtype("null","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_62() { checkIsSubtype("null","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_63() { checkIsSubtype("null","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_64() { checkIsSubtype("null","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_65() { checkIsSubtype("null","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_66() { checkIsSubtype("null","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_67() { checkIsSubtype("null","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_68() { checkIsSubtype("null","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_69() { checkIsSubtype("null","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_70() { checkIsSubtype("null","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_71() { checkIsSubtype("null","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_72() { checkIsSubtype("null","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_73() { checkIsSubtype("null","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_74() { checkIsSubtype("null","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_75() { checkIsSubtype("null","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_76() { checkIsSubtype("null","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_77() { checkIsSubtype("null","X<X|{{X f1} f1}>"); }
	@Test public void test_78() { checkIsSubtype("null","X<X|{{X f2} f1}>"); }
	@Test public void test_79() { checkIsSubtype("null","X<X|{{X f1} f2}>"); }
	@Test public void test_80() { checkIsSubtype("null","X<X|{{X f2} f2}>"); }
	@Test public void test_81() { checkIsSubtype("null","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_82() { checkIsSubtype("null","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_83() { checkIsSubtype("null","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_84() { checkIsSubtype("null","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_85() { checkIsSubtype("null","(X<X|null>)|null"); }
	@Test public void test_86() { checkIsSubtype("null","X<X|(Y<Y|null>)>"); }
	@Test public void test_87() { checkIsSubtype("null","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_88() { checkIsSubtype("null","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_89() { checkIsSubtype("null","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_90() { checkIsSubtype("null","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_91() { checkIsSubtype("null","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_92() { checkIsSubtype("null","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_93() { checkIsSubtype("null","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_94() { checkNotSubtype("X<{X f1}>","null"); }
	@Test public void test_95() { checkIsSubtype("X<{X f1}>","X<{X f1}>"); }
	@Test public void test_96() { checkIsSubtype("X<{X f1}>","X<{X f2}>"); }
	@Test public void test_97() { checkNotSubtype("X<{X f1}>","{null f1}"); }
	@Test public void test_98() { checkNotSubtype("X<{X f1}>","{null f2}"); }
	@Test public void test_99() { checkIsSubtype("X<{X f1}>","{X<{X f1}> f1}"); }
	@Test public void test_100() { checkIsSubtype("X<{X f1}>","{X<{X f2}> f1}"); }
	@Test public void test_101() { checkIsSubtype("X<{X f1}>","{X<{X f1}> f2}"); }
	@Test public void test_102() { checkIsSubtype("X<{X f1}>","{X<{X f2}> f2}"); }
	@Test public void test_103() { checkNotSubtype("X<{X f1}>","X<X|null>"); }
	@Test public void test_104() { checkIsSubtype("X<{X f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_105() { checkIsSubtype("X<{X f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_106() { checkNotSubtype("X<{X f1}>","{{null f1} f1}"); }
	@Test public void test_107() { checkNotSubtype("X<{X f1}>","{{null f2} f1}"); }
	@Test public void test_108() { checkNotSubtype("X<{X f1}>","{{null f1} f2}"); }
	@Test public void test_109() { checkNotSubtype("X<{X f1}>","{{null f2} f2}"); }
	@Test public void test_110() { checkIsSubtype("X<{X f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_111() { checkIsSubtype("X<{X f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_112() { checkIsSubtype("X<{X f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_113() { checkIsSubtype("X<{X f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_114() { checkIsSubtype("X<{X f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_115() { checkIsSubtype("X<{X f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_116() { checkIsSubtype("X<{X f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_117() { checkIsSubtype("X<{X f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_118() { checkIsSubtype("X<{X f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_119() { checkIsSubtype("X<{X f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_120() { checkIsSubtype("X<{X f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_121() { checkIsSubtype("X<{X f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_122() { checkIsSubtype("X<{X f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_123() { checkIsSubtype("X<{X f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_124() { checkIsSubtype("X<{X f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_125() { checkIsSubtype("X<{X f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_126() { checkIsSubtype("X<{X f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_127() { checkIsSubtype("X<{X f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_128() { checkIsSubtype("X<{X f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_129() { checkIsSubtype("X<{X f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_130() { checkNotSubtype("X<{X f1}>","{X<X|null> f1}"); }
	@Test public void test_131() { checkNotSubtype("X<{X f1}>","{X<X|null> f2}"); }
	@Test public void test_132() { checkIsSubtype("X<{X f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_133() { checkIsSubtype("X<{X f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_134() { checkIsSubtype("X<{X f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_135() { checkIsSubtype("X<{X f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_136() { checkIsSubtype("X<{X f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_137() { checkIsSubtype("X<{X f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_138() { checkIsSubtype("X<{X f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_139() { checkIsSubtype("X<{X f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_140() { checkIsSubtype("X<{X f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_141() { checkIsSubtype("X<{X f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_142() { checkNotSubtype("X<{X f1}>","null|null"); }
	@Test public void test_143() { checkNotSubtype("X<{X f1}>","null|X<{X f1}>"); }
	@Test public void test_144() { checkNotSubtype("X<{X f1}>","null|X<{X f2}>"); }
	@Test public void test_145() { checkNotSubtype("X<{X f1}>","null|{null f1}"); }
	@Test public void test_146() { checkNotSubtype("X<{X f1}>","null|{null f2}"); }
	@Test public void test_147() { checkNotSubtype("X<{X f1}>","null|(X<null|X>)"); }
	@Test public void test_148() { checkNotSubtype("X<{X f1}>","{null f1}|null"); }
	@Test public void test_149() { checkNotSubtype("X<{X f1}>","{null f2}|null"); }
	@Test public void test_150() { checkNotSubtype("X<{X f1}>","X<{X f1}>|null"); }
	@Test public void test_151() { checkNotSubtype("X<{X f1}>","X<{X f2}>|null"); }
	@Test public void test_152() { checkNotSubtype("X<{X f1}>","X<X|{null f1}>"); }
	@Test public void test_153() { checkNotSubtype("X<{X f1}>","X<X|{null f2}>"); }
	@Test public void test_154() { checkIsSubtype("X<{X f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_155() { checkIsSubtype("X<{X f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_156() { checkIsSubtype("X<{X f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_157() { checkIsSubtype("X<{X f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_158() { checkIsSubtype("X<{X f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_159() { checkIsSubtype("X<{X f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_160() { checkIsSubtype("X<{X f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_161() { checkIsSubtype("X<{X f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_162() { checkIsSubtype("X<{X f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_163() { checkIsSubtype("X<{X f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_164() { checkIsSubtype("X<{X f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_165() { checkIsSubtype("X<{X f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_166() { checkIsSubtype("X<{X f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_167() { checkIsSubtype("X<{X f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_168() { checkIsSubtype("X<{X f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_169() { checkIsSubtype("X<{X f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_170() { checkIsSubtype("X<{X f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_171() { checkIsSubtype("X<{X f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_172() { checkIsSubtype("X<{X f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_173() { checkIsSubtype("X<{X f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_174() { checkIsSubtype("X<{X f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_175() { checkIsSubtype("X<{X f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_176() { checkIsSubtype("X<{X f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_177() { checkIsSubtype("X<{X f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_178() { checkNotSubtype("X<{X f1}>","(X<X|null>)|null"); }
	@Test public void test_179() { checkNotSubtype("X<{X f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_180() { checkIsSubtype("X<{X f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_181() { checkIsSubtype("X<{X f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_182() { checkIsSubtype("X<{X f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_183() { checkIsSubtype("X<{X f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_184() { checkIsSubtype("X<{X f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_185() { checkIsSubtype("X<{X f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_186() { checkIsSubtype("X<{X f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_187() { checkNotSubtype("X<{X f2}>","null"); }
	@Test public void test_188() { checkIsSubtype("X<{X f2}>","X<{X f1}>"); }
	@Test public void test_189() { checkIsSubtype("X<{X f2}>","X<{X f2}>"); }
	@Test public void test_190() { checkNotSubtype("X<{X f2}>","{null f1}"); }
	@Test public void test_191() { checkNotSubtype("X<{X f2}>","{null f2}"); }
	@Test public void test_192() { checkIsSubtype("X<{X f2}>","{X<{X f1}> f1}"); }
	@Test public void test_193() { checkIsSubtype("X<{X f2}>","{X<{X f2}> f1}"); }
	@Test public void test_194() { checkIsSubtype("X<{X f2}>","{X<{X f1}> f2}"); }
	@Test public void test_195() { checkIsSubtype("X<{X f2}>","{X<{X f2}> f2}"); }
	@Test public void test_196() { checkNotSubtype("X<{X f2}>","X<X|null>"); }
	@Test public void test_197() { checkIsSubtype("X<{X f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_198() { checkIsSubtype("X<{X f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_199() { checkNotSubtype("X<{X f2}>","{{null f1} f1}"); }
	@Test public void test_200() { checkNotSubtype("X<{X f2}>","{{null f2} f1}"); }
	@Test public void test_201() { checkNotSubtype("X<{X f2}>","{{null f1} f2}"); }
	@Test public void test_202() { checkNotSubtype("X<{X f2}>","{{null f2} f2}"); }
	@Test public void test_203() { checkIsSubtype("X<{X f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_204() { checkIsSubtype("X<{X f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_205() { checkIsSubtype("X<{X f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_206() { checkIsSubtype("X<{X f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_207() { checkIsSubtype("X<{X f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_208() { checkIsSubtype("X<{X f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_209() { checkIsSubtype("X<{X f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_210() { checkIsSubtype("X<{X f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_211() { checkIsSubtype("X<{X f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_212() { checkIsSubtype("X<{X f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_213() { checkIsSubtype("X<{X f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_214() { checkIsSubtype("X<{X f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_215() { checkIsSubtype("X<{X f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_216() { checkIsSubtype("X<{X f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_217() { checkIsSubtype("X<{X f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_218() { checkIsSubtype("X<{X f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_219() { checkIsSubtype("X<{X f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_220() { checkIsSubtype("X<{X f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_221() { checkIsSubtype("X<{X f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_222() { checkIsSubtype("X<{X f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_223() { checkNotSubtype("X<{X f2}>","{X<X|null> f1}"); }
	@Test public void test_224() { checkNotSubtype("X<{X f2}>","{X<X|null> f2}"); }
	@Test public void test_225() { checkIsSubtype("X<{X f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_226() { checkIsSubtype("X<{X f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_227() { checkIsSubtype("X<{X f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_228() { checkIsSubtype("X<{X f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_229() { checkIsSubtype("X<{X f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_230() { checkIsSubtype("X<{X f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_231() { checkIsSubtype("X<{X f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_232() { checkIsSubtype("X<{X f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_233() { checkIsSubtype("X<{X f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_234() { checkIsSubtype("X<{X f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_235() { checkNotSubtype("X<{X f2}>","null|null"); }
	@Test public void test_236() { checkNotSubtype("X<{X f2}>","null|X<{X f1}>"); }
	@Test public void test_237() { checkNotSubtype("X<{X f2}>","null|X<{X f2}>"); }
	@Test public void test_238() { checkNotSubtype("X<{X f2}>","null|{null f1}"); }
	@Test public void test_239() { checkNotSubtype("X<{X f2}>","null|{null f2}"); }
	@Test public void test_240() { checkNotSubtype("X<{X f2}>","null|(X<null|X>)"); }
	@Test public void test_241() { checkNotSubtype("X<{X f2}>","{null f1}|null"); }
	@Test public void test_242() { checkNotSubtype("X<{X f2}>","{null f2}|null"); }
	@Test public void test_243() { checkNotSubtype("X<{X f2}>","X<{X f1}>|null"); }
	@Test public void test_244() { checkNotSubtype("X<{X f2}>","X<{X f2}>|null"); }
	@Test public void test_245() { checkNotSubtype("X<{X f2}>","X<X|{null f1}>"); }
	@Test public void test_246() { checkNotSubtype("X<{X f2}>","X<X|{null f2}>"); }
	@Test public void test_247() { checkIsSubtype("X<{X f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_248() { checkIsSubtype("X<{X f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_249() { checkIsSubtype("X<{X f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_250() { checkIsSubtype("X<{X f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_251() { checkIsSubtype("X<{X f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_252() { checkIsSubtype("X<{X f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_253() { checkIsSubtype("X<{X f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_254() { checkIsSubtype("X<{X f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_255() { checkIsSubtype("X<{X f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_256() { checkIsSubtype("X<{X f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_257() { checkIsSubtype("X<{X f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_258() { checkIsSubtype("X<{X f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_259() { checkIsSubtype("X<{X f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_260() { checkIsSubtype("X<{X f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_261() { checkIsSubtype("X<{X f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_262() { checkIsSubtype("X<{X f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_263() { checkIsSubtype("X<{X f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_264() { checkIsSubtype("X<{X f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_265() { checkIsSubtype("X<{X f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_266() { checkIsSubtype("X<{X f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_267() { checkIsSubtype("X<{X f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_268() { checkIsSubtype("X<{X f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_269() { checkIsSubtype("X<{X f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_270() { checkIsSubtype("X<{X f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_271() { checkNotSubtype("X<{X f2}>","(X<X|null>)|null"); }
	@Test public void test_272() { checkNotSubtype("X<{X f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_273() { checkIsSubtype("X<{X f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_274() { checkIsSubtype("X<{X f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_275() { checkIsSubtype("X<{X f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_276() { checkIsSubtype("X<{X f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_277() { checkIsSubtype("X<{X f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_278() { checkIsSubtype("X<{X f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_279() { checkIsSubtype("X<{X f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_280() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_281() { checkIsSubtype("{null f1}","X<{X f1}>"); }
	@Test public void test_282() { checkIsSubtype("{null f1}","X<{X f2}>"); }
	@Test public void test_283() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_284() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_285() { checkIsSubtype("{null f1}","{X<{X f1}> f1}"); }
	@Test public void test_286() { checkIsSubtype("{null f1}","{X<{X f2}> f1}"); }
	@Test public void test_287() { checkIsSubtype("{null f1}","{X<{X f1}> f2}"); }
	@Test public void test_288() { checkIsSubtype("{null f1}","{X<{X f2}> f2}"); }
	@Test public void test_289() { checkNotSubtype("{null f1}","X<X|null>"); }
	@Test public void test_290() { checkIsSubtype("{null f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_291() { checkIsSubtype("{null f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_292() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test public void test_293() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test public void test_294() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test public void test_295() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test public void test_296() { checkIsSubtype("{null f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_297() { checkIsSubtype("{null f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_298() { checkIsSubtype("{null f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_299() { checkIsSubtype("{null f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_300() { checkIsSubtype("{null f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_301() { checkIsSubtype("{null f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_302() { checkIsSubtype("{null f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_303() { checkIsSubtype("{null f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_304() { checkIsSubtype("{null f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_305() { checkIsSubtype("{null f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_306() { checkIsSubtype("{null f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_307() { checkIsSubtype("{null f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_308() { checkIsSubtype("{null f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_309() { checkIsSubtype("{null f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_310() { checkIsSubtype("{null f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_311() { checkIsSubtype("{null f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_312() { checkIsSubtype("{null f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_313() { checkIsSubtype("{null f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_314() { checkIsSubtype("{null f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_315() { checkIsSubtype("{null f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_316() { checkIsSubtype("{null f1}","{X<X|null> f1}"); }
	@Test public void test_317() { checkNotSubtype("{null f1}","{X<X|null> f2}"); }
	@Test public void test_318() { checkIsSubtype("{null f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_319() { checkIsSubtype("{null f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_320() { checkIsSubtype("{null f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_321() { checkIsSubtype("{null f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_322() { checkIsSubtype("{null f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_323() { checkIsSubtype("{null f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_324() { checkIsSubtype("{null f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_325() { checkIsSubtype("{null f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_326() { checkIsSubtype("{null f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_327() { checkIsSubtype("{null f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_328() { checkNotSubtype("{null f1}","null|null"); }
	@Test public void test_329() { checkNotSubtype("{null f1}","null|X<{X f1}>"); }
	@Test public void test_330() { checkNotSubtype("{null f1}","null|X<{X f2}>"); }
	@Test public void test_331() { checkNotSubtype("{null f1}","null|{null f1}"); }
	@Test public void test_332() { checkNotSubtype("{null f1}","null|{null f2}"); }
	@Test public void test_333() { checkNotSubtype("{null f1}","null|(X<null|X>)"); }
	@Test public void test_334() { checkNotSubtype("{null f1}","{null f1}|null"); }
	@Test public void test_335() { checkNotSubtype("{null f1}","{null f2}|null"); }
	@Test public void test_336() { checkNotSubtype("{null f1}","X<{X f1}>|null"); }
	@Test public void test_337() { checkNotSubtype("{null f1}","X<{X f2}>|null"); }
	@Test public void test_338() { checkIsSubtype("{null f1}","X<X|{null f1}>"); }
	@Test public void test_339() { checkNotSubtype("{null f1}","X<X|{null f2}>"); }
	@Test public void test_340() { checkIsSubtype("{null f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_341() { checkIsSubtype("{null f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_342() { checkIsSubtype("{null f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_343() { checkIsSubtype("{null f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_344() { checkIsSubtype("{null f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_345() { checkIsSubtype("{null f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_346() { checkIsSubtype("{null f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_347() { checkIsSubtype("{null f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_348() { checkIsSubtype("{null f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_349() { checkIsSubtype("{null f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_350() { checkIsSubtype("{null f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_351() { checkIsSubtype("{null f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_352() { checkIsSubtype("{null f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_353() { checkIsSubtype("{null f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_354() { checkIsSubtype("{null f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_355() { checkIsSubtype("{null f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_356() { checkIsSubtype("{null f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_357() { checkIsSubtype("{null f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_358() { checkIsSubtype("{null f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_359() { checkIsSubtype("{null f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_360() { checkIsSubtype("{null f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_361() { checkIsSubtype("{null f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_362() { checkIsSubtype("{null f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_363() { checkIsSubtype("{null f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_364() { checkNotSubtype("{null f1}","(X<X|null>)|null"); }
	@Test public void test_365() { checkNotSubtype("{null f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_366() { checkIsSubtype("{null f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_367() { checkIsSubtype("{null f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_368() { checkIsSubtype("{null f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_369() { checkIsSubtype("{null f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_370() { checkIsSubtype("{null f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_371() { checkIsSubtype("{null f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_372() { checkIsSubtype("{null f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_373() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_374() { checkIsSubtype("{null f2}","X<{X f1}>"); }
	@Test public void test_375() { checkIsSubtype("{null f2}","X<{X f2}>"); }
	@Test public void test_376() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_377() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_378() { checkIsSubtype("{null f2}","{X<{X f1}> f1}"); }
	@Test public void test_379() { checkIsSubtype("{null f2}","{X<{X f2}> f1}"); }
	@Test public void test_380() { checkIsSubtype("{null f2}","{X<{X f1}> f2}"); }
	@Test public void test_381() { checkIsSubtype("{null f2}","{X<{X f2}> f2}"); }
	@Test public void test_382() { checkNotSubtype("{null f2}","X<X|null>"); }
	@Test public void test_383() { checkIsSubtype("{null f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_384() { checkIsSubtype("{null f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_385() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test public void test_386() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test public void test_387() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test public void test_388() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test public void test_389() { checkIsSubtype("{null f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_390() { checkIsSubtype("{null f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_391() { checkIsSubtype("{null f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_392() { checkIsSubtype("{null f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_393() { checkIsSubtype("{null f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_394() { checkIsSubtype("{null f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_395() { checkIsSubtype("{null f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_396() { checkIsSubtype("{null f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_397() { checkIsSubtype("{null f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_398() { checkIsSubtype("{null f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_399() { checkIsSubtype("{null f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_400() { checkIsSubtype("{null f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_401() { checkIsSubtype("{null f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_402() { checkIsSubtype("{null f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_403() { checkIsSubtype("{null f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_404() { checkIsSubtype("{null f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_405() { checkIsSubtype("{null f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_406() { checkIsSubtype("{null f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_407() { checkIsSubtype("{null f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_408() { checkIsSubtype("{null f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_409() { checkNotSubtype("{null f2}","{X<X|null> f1}"); }
	@Test public void test_410() { checkIsSubtype("{null f2}","{X<X|null> f2}"); }
	@Test public void test_411() { checkIsSubtype("{null f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_412() { checkIsSubtype("{null f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_413() { checkIsSubtype("{null f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_414() { checkIsSubtype("{null f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_415() { checkIsSubtype("{null f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_416() { checkIsSubtype("{null f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_417() { checkIsSubtype("{null f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_418() { checkIsSubtype("{null f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_419() { checkIsSubtype("{null f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_420() { checkIsSubtype("{null f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_421() { checkNotSubtype("{null f2}","null|null"); }
	@Test public void test_422() { checkNotSubtype("{null f2}","null|X<{X f1}>"); }
	@Test public void test_423() { checkNotSubtype("{null f2}","null|X<{X f2}>"); }
	@Test public void test_424() { checkNotSubtype("{null f2}","null|{null f1}"); }
	@Test public void test_425() { checkNotSubtype("{null f2}","null|{null f2}"); }
	@Test public void test_426() { checkNotSubtype("{null f2}","null|(X<null|X>)"); }
	@Test public void test_427() { checkNotSubtype("{null f2}","{null f1}|null"); }
	@Test public void test_428() { checkNotSubtype("{null f2}","{null f2}|null"); }
	@Test public void test_429() { checkNotSubtype("{null f2}","X<{X f1}>|null"); }
	@Test public void test_430() { checkNotSubtype("{null f2}","X<{X f2}>|null"); }
	@Test public void test_431() { checkNotSubtype("{null f2}","X<X|{null f1}>"); }
	@Test public void test_432() { checkIsSubtype("{null f2}","X<X|{null f2}>"); }
	@Test public void test_433() { checkIsSubtype("{null f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_434() { checkIsSubtype("{null f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_435() { checkIsSubtype("{null f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_436() { checkIsSubtype("{null f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_437() { checkIsSubtype("{null f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_438() { checkIsSubtype("{null f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_439() { checkIsSubtype("{null f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_440() { checkIsSubtype("{null f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_441() { checkIsSubtype("{null f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_442() { checkIsSubtype("{null f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_443() { checkIsSubtype("{null f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_444() { checkIsSubtype("{null f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_445() { checkIsSubtype("{null f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_446() { checkIsSubtype("{null f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_447() { checkIsSubtype("{null f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_448() { checkIsSubtype("{null f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_449() { checkIsSubtype("{null f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_450() { checkIsSubtype("{null f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_451() { checkIsSubtype("{null f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_452() { checkIsSubtype("{null f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_453() { checkIsSubtype("{null f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_454() { checkIsSubtype("{null f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_455() { checkIsSubtype("{null f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_456() { checkIsSubtype("{null f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_457() { checkNotSubtype("{null f2}","(X<X|null>)|null"); }
	@Test public void test_458() { checkNotSubtype("{null f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_459() { checkIsSubtype("{null f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_460() { checkIsSubtype("{null f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_461() { checkIsSubtype("{null f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_462() { checkIsSubtype("{null f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_463() { checkIsSubtype("{null f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_464() { checkIsSubtype("{null f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_465() { checkIsSubtype("{null f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_466() { checkNotSubtype("{X<{X f1}> f1}","null"); }
	@Test public void test_467() { checkIsSubtype("{X<{X f1}> f1}","X<{X f1}>"); }
	@Test public void test_468() { checkIsSubtype("{X<{X f1}> f1}","X<{X f2}>"); }
	@Test public void test_469() { checkNotSubtype("{X<{X f1}> f1}","{null f1}"); }
	@Test public void test_470() { checkNotSubtype("{X<{X f1}> f1}","{null f2}"); }
	@Test public void test_471() { checkIsSubtype("{X<{X f1}> f1}","{X<{X f1}> f1}"); }
	@Test public void test_472() { checkIsSubtype("{X<{X f1}> f1}","{X<{X f2}> f1}"); }
	@Test public void test_473() { checkIsSubtype("{X<{X f1}> f1}","{X<{X f1}> f2}"); }
	@Test public void test_474() { checkIsSubtype("{X<{X f1}> f1}","{X<{X f2}> f2}"); }
	@Test public void test_475() { checkNotSubtype("{X<{X f1}> f1}","X<X|null>"); }
	@Test public void test_476() { checkIsSubtype("{X<{X f1}> f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_477() { checkIsSubtype("{X<{X f1}> f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_478() { checkNotSubtype("{X<{X f1}> f1}","{{null f1} f1}"); }
	@Test public void test_479() { checkNotSubtype("{X<{X f1}> f1}","{{null f2} f1}"); }
	@Test public void test_480() { checkNotSubtype("{X<{X f1}> f1}","{{null f1} f2}"); }
	@Test public void test_481() { checkNotSubtype("{X<{X f1}> f1}","{{null f2} f2}"); }
	@Test public void test_482() { checkIsSubtype("{X<{X f1}> f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_483() { checkIsSubtype("{X<{X f1}> f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_484() { checkIsSubtype("{X<{X f1}> f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_485() { checkIsSubtype("{X<{X f1}> f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_486() { checkIsSubtype("{X<{X f1}> f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_487() { checkIsSubtype("{X<{X f1}> f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_488() { checkIsSubtype("{X<{X f1}> f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_489() { checkIsSubtype("{X<{X f1}> f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_490() { checkIsSubtype("{X<{X f1}> f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_491() { checkIsSubtype("{X<{X f1}> f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_492() { checkIsSubtype("{X<{X f1}> f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_493() { checkIsSubtype("{X<{X f1}> f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_494() { checkIsSubtype("{X<{X f1}> f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_495() { checkIsSubtype("{X<{X f1}> f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_496() { checkIsSubtype("{X<{X f1}> f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_497() { checkIsSubtype("{X<{X f1}> f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_498() { checkIsSubtype("{X<{X f1}> f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_499() { checkIsSubtype("{X<{X f1}> f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_500() { checkIsSubtype("{X<{X f1}> f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_501() { checkIsSubtype("{X<{X f1}> f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_502() { checkNotSubtype("{X<{X f1}> f1}","{X<X|null> f1}"); }
	@Test public void test_503() { checkNotSubtype("{X<{X f1}> f1}","{X<X|null> f2}"); }
	@Test public void test_504() { checkIsSubtype("{X<{X f1}> f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_505() { checkIsSubtype("{X<{X f1}> f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_506() { checkIsSubtype("{X<{X f1}> f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_507() { checkIsSubtype("{X<{X f1}> f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_508() { checkIsSubtype("{X<{X f1}> f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_509() { checkIsSubtype("{X<{X f1}> f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_510() { checkIsSubtype("{X<{X f1}> f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_511() { checkIsSubtype("{X<{X f1}> f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_512() { checkIsSubtype("{X<{X f1}> f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_513() { checkIsSubtype("{X<{X f1}> f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_514() { checkNotSubtype("{X<{X f1}> f1}","null|null"); }
	@Test public void test_515() { checkNotSubtype("{X<{X f1}> f1}","null|X<{X f1}>"); }
	@Test public void test_516() { checkNotSubtype("{X<{X f1}> f1}","null|X<{X f2}>"); }
	@Test public void test_517() { checkNotSubtype("{X<{X f1}> f1}","null|{null f1}"); }
	@Test public void test_518() { checkNotSubtype("{X<{X f1}> f1}","null|{null f2}"); }
	@Test public void test_519() { checkNotSubtype("{X<{X f1}> f1}","null|(X<null|X>)"); }
	@Test public void test_520() { checkNotSubtype("{X<{X f1}> f1}","{null f1}|null"); }
	@Test public void test_521() { checkNotSubtype("{X<{X f1}> f1}","{null f2}|null"); }
	@Test public void test_522() { checkNotSubtype("{X<{X f1}> f1}","X<{X f1}>|null"); }
	@Test public void test_523() { checkNotSubtype("{X<{X f1}> f1}","X<{X f2}>|null"); }
	@Test public void test_524() { checkNotSubtype("{X<{X f1}> f1}","X<X|{null f1}>"); }
	@Test public void test_525() { checkNotSubtype("{X<{X f1}> f1}","X<X|{null f2}>"); }
	@Test public void test_526() { checkIsSubtype("{X<{X f1}> f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_527() { checkIsSubtype("{X<{X f1}> f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_528() { checkIsSubtype("{X<{X f1}> f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_529() { checkIsSubtype("{X<{X f1}> f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_530() { checkIsSubtype("{X<{X f1}> f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_531() { checkIsSubtype("{X<{X f1}> f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_532() { checkIsSubtype("{X<{X f1}> f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_533() { checkIsSubtype("{X<{X f1}> f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_534() { checkIsSubtype("{X<{X f1}> f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_535() { checkIsSubtype("{X<{X f1}> f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_536() { checkIsSubtype("{X<{X f1}> f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_537() { checkIsSubtype("{X<{X f1}> f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_538() { checkIsSubtype("{X<{X f1}> f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_539() { checkIsSubtype("{X<{X f1}> f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_540() { checkIsSubtype("{X<{X f1}> f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_541() { checkIsSubtype("{X<{X f1}> f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_542() { checkIsSubtype("{X<{X f1}> f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_543() { checkIsSubtype("{X<{X f1}> f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_544() { checkIsSubtype("{X<{X f1}> f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_545() { checkIsSubtype("{X<{X f1}> f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_546() { checkIsSubtype("{X<{X f1}> f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_547() { checkIsSubtype("{X<{X f1}> f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_548() { checkIsSubtype("{X<{X f1}> f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_549() { checkIsSubtype("{X<{X f1}> f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_550() { checkNotSubtype("{X<{X f1}> f1}","(X<X|null>)|null"); }
	@Test public void test_551() { checkNotSubtype("{X<{X f1}> f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_552() { checkIsSubtype("{X<{X f1}> f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_553() { checkIsSubtype("{X<{X f1}> f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_554() { checkIsSubtype("{X<{X f1}> f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_555() { checkIsSubtype("{X<{X f1}> f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_556() { checkIsSubtype("{X<{X f1}> f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_557() { checkIsSubtype("{X<{X f1}> f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_558() { checkIsSubtype("{X<{X f1}> f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_559() { checkNotSubtype("{X<{X f2}> f1}","null"); }
	@Test public void test_560() { checkIsSubtype("{X<{X f2}> f1}","X<{X f1}>"); }
	@Test public void test_561() { checkIsSubtype("{X<{X f2}> f1}","X<{X f2}>"); }
	@Test public void test_562() { checkNotSubtype("{X<{X f2}> f1}","{null f1}"); }
	@Test public void test_563() { checkNotSubtype("{X<{X f2}> f1}","{null f2}"); }
	@Test public void test_564() { checkIsSubtype("{X<{X f2}> f1}","{X<{X f1}> f1}"); }
	@Test public void test_565() { checkIsSubtype("{X<{X f2}> f1}","{X<{X f2}> f1}"); }
	@Test public void test_566() { checkIsSubtype("{X<{X f2}> f1}","{X<{X f1}> f2}"); }
	@Test public void test_567() { checkIsSubtype("{X<{X f2}> f1}","{X<{X f2}> f2}"); }
	@Test public void test_568() { checkNotSubtype("{X<{X f2}> f1}","X<X|null>"); }
	@Test public void test_569() { checkIsSubtype("{X<{X f2}> f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_570() { checkIsSubtype("{X<{X f2}> f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_571() { checkNotSubtype("{X<{X f2}> f1}","{{null f1} f1}"); }
	@Test public void test_572() { checkNotSubtype("{X<{X f2}> f1}","{{null f2} f1}"); }
	@Test public void test_573() { checkNotSubtype("{X<{X f2}> f1}","{{null f1} f2}"); }
	@Test public void test_574() { checkNotSubtype("{X<{X f2}> f1}","{{null f2} f2}"); }
	@Test public void test_575() { checkIsSubtype("{X<{X f2}> f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_576() { checkIsSubtype("{X<{X f2}> f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_577() { checkIsSubtype("{X<{X f2}> f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_578() { checkIsSubtype("{X<{X f2}> f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_579() { checkIsSubtype("{X<{X f2}> f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_580() { checkIsSubtype("{X<{X f2}> f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_581() { checkIsSubtype("{X<{X f2}> f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_582() { checkIsSubtype("{X<{X f2}> f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_583() { checkIsSubtype("{X<{X f2}> f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_584() { checkIsSubtype("{X<{X f2}> f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_585() { checkIsSubtype("{X<{X f2}> f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_586() { checkIsSubtype("{X<{X f2}> f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_587() { checkIsSubtype("{X<{X f2}> f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_588() { checkIsSubtype("{X<{X f2}> f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_589() { checkIsSubtype("{X<{X f2}> f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_590() { checkIsSubtype("{X<{X f2}> f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_591() { checkIsSubtype("{X<{X f2}> f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_592() { checkIsSubtype("{X<{X f2}> f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_593() { checkIsSubtype("{X<{X f2}> f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_594() { checkIsSubtype("{X<{X f2}> f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_595() { checkNotSubtype("{X<{X f2}> f1}","{X<X|null> f1}"); }
	@Test public void test_596() { checkNotSubtype("{X<{X f2}> f1}","{X<X|null> f2}"); }
	@Test public void test_597() { checkIsSubtype("{X<{X f2}> f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_598() { checkIsSubtype("{X<{X f2}> f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_599() { checkIsSubtype("{X<{X f2}> f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_600() { checkIsSubtype("{X<{X f2}> f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_601() { checkIsSubtype("{X<{X f2}> f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_602() { checkIsSubtype("{X<{X f2}> f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_603() { checkIsSubtype("{X<{X f2}> f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_604() { checkIsSubtype("{X<{X f2}> f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_605() { checkIsSubtype("{X<{X f2}> f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_606() { checkIsSubtype("{X<{X f2}> f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_607() { checkNotSubtype("{X<{X f2}> f1}","null|null"); }
	@Test public void test_608() { checkNotSubtype("{X<{X f2}> f1}","null|X<{X f1}>"); }
	@Test public void test_609() { checkNotSubtype("{X<{X f2}> f1}","null|X<{X f2}>"); }
	@Test public void test_610() { checkNotSubtype("{X<{X f2}> f1}","null|{null f1}"); }
	@Test public void test_611() { checkNotSubtype("{X<{X f2}> f1}","null|{null f2}"); }
	@Test public void test_612() { checkNotSubtype("{X<{X f2}> f1}","null|(X<null|X>)"); }
	@Test public void test_613() { checkNotSubtype("{X<{X f2}> f1}","{null f1}|null"); }
	@Test public void test_614() { checkNotSubtype("{X<{X f2}> f1}","{null f2}|null"); }
	@Test public void test_615() { checkNotSubtype("{X<{X f2}> f1}","X<{X f1}>|null"); }
	@Test public void test_616() { checkNotSubtype("{X<{X f2}> f1}","X<{X f2}>|null"); }
	@Test public void test_617() { checkNotSubtype("{X<{X f2}> f1}","X<X|{null f1}>"); }
	@Test public void test_618() { checkNotSubtype("{X<{X f2}> f1}","X<X|{null f2}>"); }
	@Test public void test_619() { checkIsSubtype("{X<{X f2}> f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_620() { checkIsSubtype("{X<{X f2}> f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_621() { checkIsSubtype("{X<{X f2}> f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_622() { checkIsSubtype("{X<{X f2}> f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_623() { checkIsSubtype("{X<{X f2}> f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_624() { checkIsSubtype("{X<{X f2}> f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_625() { checkIsSubtype("{X<{X f2}> f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_626() { checkIsSubtype("{X<{X f2}> f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_627() { checkIsSubtype("{X<{X f2}> f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_628() { checkIsSubtype("{X<{X f2}> f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_629() { checkIsSubtype("{X<{X f2}> f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_630() { checkIsSubtype("{X<{X f2}> f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_631() { checkIsSubtype("{X<{X f2}> f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_632() { checkIsSubtype("{X<{X f2}> f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_633() { checkIsSubtype("{X<{X f2}> f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_634() { checkIsSubtype("{X<{X f2}> f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_635() { checkIsSubtype("{X<{X f2}> f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_636() { checkIsSubtype("{X<{X f2}> f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_637() { checkIsSubtype("{X<{X f2}> f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_638() { checkIsSubtype("{X<{X f2}> f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_639() { checkIsSubtype("{X<{X f2}> f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_640() { checkIsSubtype("{X<{X f2}> f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_641() { checkIsSubtype("{X<{X f2}> f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_642() { checkIsSubtype("{X<{X f2}> f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_643() { checkNotSubtype("{X<{X f2}> f1}","(X<X|null>)|null"); }
	@Test public void test_644() { checkNotSubtype("{X<{X f2}> f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_645() { checkIsSubtype("{X<{X f2}> f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_646() { checkIsSubtype("{X<{X f2}> f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_647() { checkIsSubtype("{X<{X f2}> f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_648() { checkIsSubtype("{X<{X f2}> f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_649() { checkIsSubtype("{X<{X f2}> f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_650() { checkIsSubtype("{X<{X f2}> f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_651() { checkIsSubtype("{X<{X f2}> f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_652() { checkNotSubtype("{X<{X f1}> f2}","null"); }
	@Test public void test_653() { checkIsSubtype("{X<{X f1}> f2}","X<{X f1}>"); }
	@Test public void test_654() { checkIsSubtype("{X<{X f1}> f2}","X<{X f2}>"); }
	@Test public void test_655() { checkNotSubtype("{X<{X f1}> f2}","{null f1}"); }
	@Test public void test_656() { checkNotSubtype("{X<{X f1}> f2}","{null f2}"); }
	@Test public void test_657() { checkIsSubtype("{X<{X f1}> f2}","{X<{X f1}> f1}"); }
	@Test public void test_658() { checkIsSubtype("{X<{X f1}> f2}","{X<{X f2}> f1}"); }
	@Test public void test_659() { checkIsSubtype("{X<{X f1}> f2}","{X<{X f1}> f2}"); }
	@Test public void test_660() { checkIsSubtype("{X<{X f1}> f2}","{X<{X f2}> f2}"); }
	@Test public void test_661() { checkNotSubtype("{X<{X f1}> f2}","X<X|null>"); }
	@Test public void test_662() { checkIsSubtype("{X<{X f1}> f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_663() { checkIsSubtype("{X<{X f1}> f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_664() { checkNotSubtype("{X<{X f1}> f2}","{{null f1} f1}"); }
	@Test public void test_665() { checkNotSubtype("{X<{X f1}> f2}","{{null f2} f1}"); }
	@Test public void test_666() { checkNotSubtype("{X<{X f1}> f2}","{{null f1} f2}"); }
	@Test public void test_667() { checkNotSubtype("{X<{X f1}> f2}","{{null f2} f2}"); }
	@Test public void test_668() { checkIsSubtype("{X<{X f1}> f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_669() { checkIsSubtype("{X<{X f1}> f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_670() { checkIsSubtype("{X<{X f1}> f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_671() { checkIsSubtype("{X<{X f1}> f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_672() { checkIsSubtype("{X<{X f1}> f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_673() { checkIsSubtype("{X<{X f1}> f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_674() { checkIsSubtype("{X<{X f1}> f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_675() { checkIsSubtype("{X<{X f1}> f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_676() { checkIsSubtype("{X<{X f1}> f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_677() { checkIsSubtype("{X<{X f1}> f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_678() { checkIsSubtype("{X<{X f1}> f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_679() { checkIsSubtype("{X<{X f1}> f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_680() { checkIsSubtype("{X<{X f1}> f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_681() { checkIsSubtype("{X<{X f1}> f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_682() { checkIsSubtype("{X<{X f1}> f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_683() { checkIsSubtype("{X<{X f1}> f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_684() { checkIsSubtype("{X<{X f1}> f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_685() { checkIsSubtype("{X<{X f1}> f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_686() { checkIsSubtype("{X<{X f1}> f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_687() { checkIsSubtype("{X<{X f1}> f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_688() { checkNotSubtype("{X<{X f1}> f2}","{X<X|null> f1}"); }
	@Test public void test_689() { checkNotSubtype("{X<{X f1}> f2}","{X<X|null> f2}"); }
	@Test public void test_690() { checkIsSubtype("{X<{X f1}> f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_691() { checkIsSubtype("{X<{X f1}> f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_692() { checkIsSubtype("{X<{X f1}> f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_693() { checkIsSubtype("{X<{X f1}> f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_694() { checkIsSubtype("{X<{X f1}> f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_695() { checkIsSubtype("{X<{X f1}> f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_696() { checkIsSubtype("{X<{X f1}> f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_697() { checkIsSubtype("{X<{X f1}> f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_698() { checkIsSubtype("{X<{X f1}> f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_699() { checkIsSubtype("{X<{X f1}> f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_700() { checkNotSubtype("{X<{X f1}> f2}","null|null"); }
	@Test public void test_701() { checkNotSubtype("{X<{X f1}> f2}","null|X<{X f1}>"); }
	@Test public void test_702() { checkNotSubtype("{X<{X f1}> f2}","null|X<{X f2}>"); }
	@Test public void test_703() { checkNotSubtype("{X<{X f1}> f2}","null|{null f1}"); }
	@Test public void test_704() { checkNotSubtype("{X<{X f1}> f2}","null|{null f2}"); }
	@Test public void test_705() { checkNotSubtype("{X<{X f1}> f2}","null|(X<null|X>)"); }
	@Test public void test_706() { checkNotSubtype("{X<{X f1}> f2}","{null f1}|null"); }
	@Test public void test_707() { checkNotSubtype("{X<{X f1}> f2}","{null f2}|null"); }
	@Test public void test_708() { checkNotSubtype("{X<{X f1}> f2}","X<{X f1}>|null"); }
	@Test public void test_709() { checkNotSubtype("{X<{X f1}> f2}","X<{X f2}>|null"); }
	@Test public void test_710() { checkNotSubtype("{X<{X f1}> f2}","X<X|{null f1}>"); }
	@Test public void test_711() { checkNotSubtype("{X<{X f1}> f2}","X<X|{null f2}>"); }
	@Test public void test_712() { checkIsSubtype("{X<{X f1}> f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_713() { checkIsSubtype("{X<{X f1}> f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_714() { checkIsSubtype("{X<{X f1}> f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_715() { checkIsSubtype("{X<{X f1}> f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_716() { checkIsSubtype("{X<{X f1}> f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_717() { checkIsSubtype("{X<{X f1}> f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_718() { checkIsSubtype("{X<{X f1}> f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_719() { checkIsSubtype("{X<{X f1}> f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_720() { checkIsSubtype("{X<{X f1}> f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_721() { checkIsSubtype("{X<{X f1}> f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_722() { checkIsSubtype("{X<{X f1}> f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_723() { checkIsSubtype("{X<{X f1}> f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_724() { checkIsSubtype("{X<{X f1}> f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_725() { checkIsSubtype("{X<{X f1}> f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_726() { checkIsSubtype("{X<{X f1}> f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_727() { checkIsSubtype("{X<{X f1}> f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_728() { checkIsSubtype("{X<{X f1}> f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_729() { checkIsSubtype("{X<{X f1}> f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_730() { checkIsSubtype("{X<{X f1}> f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_731() { checkIsSubtype("{X<{X f1}> f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_732() { checkIsSubtype("{X<{X f1}> f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_733() { checkIsSubtype("{X<{X f1}> f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_734() { checkIsSubtype("{X<{X f1}> f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_735() { checkIsSubtype("{X<{X f1}> f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_736() { checkNotSubtype("{X<{X f1}> f2}","(X<X|null>)|null"); }
	@Test public void test_737() { checkNotSubtype("{X<{X f1}> f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_738() { checkIsSubtype("{X<{X f1}> f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_739() { checkIsSubtype("{X<{X f1}> f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_740() { checkIsSubtype("{X<{X f1}> f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_741() { checkIsSubtype("{X<{X f1}> f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_742() { checkIsSubtype("{X<{X f1}> f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_743() { checkIsSubtype("{X<{X f1}> f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_744() { checkIsSubtype("{X<{X f1}> f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_745() { checkNotSubtype("{X<{X f2}> f2}","null"); }
	@Test public void test_746() { checkIsSubtype("{X<{X f2}> f2}","X<{X f1}>"); }
	@Test public void test_747() { checkIsSubtype("{X<{X f2}> f2}","X<{X f2}>"); }
	@Test public void test_748() { checkNotSubtype("{X<{X f2}> f2}","{null f1}"); }
	@Test public void test_749() { checkNotSubtype("{X<{X f2}> f2}","{null f2}"); }
	@Test public void test_750() { checkIsSubtype("{X<{X f2}> f2}","{X<{X f1}> f1}"); }
	@Test public void test_751() { checkIsSubtype("{X<{X f2}> f2}","{X<{X f2}> f1}"); }
	@Test public void test_752() { checkIsSubtype("{X<{X f2}> f2}","{X<{X f1}> f2}"); }
	@Test public void test_753() { checkIsSubtype("{X<{X f2}> f2}","{X<{X f2}> f2}"); }
	@Test public void test_754() { checkNotSubtype("{X<{X f2}> f2}","X<X|null>"); }
	@Test public void test_755() { checkIsSubtype("{X<{X f2}> f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_756() { checkIsSubtype("{X<{X f2}> f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_757() { checkNotSubtype("{X<{X f2}> f2}","{{null f1} f1}"); }
	@Test public void test_758() { checkNotSubtype("{X<{X f2}> f2}","{{null f2} f1}"); }
	@Test public void test_759() { checkNotSubtype("{X<{X f2}> f2}","{{null f1} f2}"); }
	@Test public void test_760() { checkNotSubtype("{X<{X f2}> f2}","{{null f2} f2}"); }
	@Test public void test_761() { checkIsSubtype("{X<{X f2}> f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_762() { checkIsSubtype("{X<{X f2}> f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_763() { checkIsSubtype("{X<{X f2}> f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_764() { checkIsSubtype("{X<{X f2}> f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_765() { checkIsSubtype("{X<{X f2}> f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_766() { checkIsSubtype("{X<{X f2}> f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_767() { checkIsSubtype("{X<{X f2}> f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_768() { checkIsSubtype("{X<{X f2}> f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_769() { checkIsSubtype("{X<{X f2}> f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_770() { checkIsSubtype("{X<{X f2}> f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_771() { checkIsSubtype("{X<{X f2}> f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_772() { checkIsSubtype("{X<{X f2}> f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_773() { checkIsSubtype("{X<{X f2}> f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_774() { checkIsSubtype("{X<{X f2}> f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_775() { checkIsSubtype("{X<{X f2}> f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_776() { checkIsSubtype("{X<{X f2}> f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_777() { checkIsSubtype("{X<{X f2}> f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_778() { checkIsSubtype("{X<{X f2}> f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_779() { checkIsSubtype("{X<{X f2}> f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_780() { checkIsSubtype("{X<{X f2}> f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_781() { checkNotSubtype("{X<{X f2}> f2}","{X<X|null> f1}"); }
	@Test public void test_782() { checkNotSubtype("{X<{X f2}> f2}","{X<X|null> f2}"); }
	@Test public void test_783() { checkIsSubtype("{X<{X f2}> f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_784() { checkIsSubtype("{X<{X f2}> f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_785() { checkIsSubtype("{X<{X f2}> f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_786() { checkIsSubtype("{X<{X f2}> f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_787() { checkIsSubtype("{X<{X f2}> f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_788() { checkIsSubtype("{X<{X f2}> f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_789() { checkIsSubtype("{X<{X f2}> f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_790() { checkIsSubtype("{X<{X f2}> f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_791() { checkIsSubtype("{X<{X f2}> f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_792() { checkIsSubtype("{X<{X f2}> f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_793() { checkNotSubtype("{X<{X f2}> f2}","null|null"); }
	@Test public void test_794() { checkNotSubtype("{X<{X f2}> f2}","null|X<{X f1}>"); }
	@Test public void test_795() { checkNotSubtype("{X<{X f2}> f2}","null|X<{X f2}>"); }
	@Test public void test_796() { checkNotSubtype("{X<{X f2}> f2}","null|{null f1}"); }
	@Test public void test_797() { checkNotSubtype("{X<{X f2}> f2}","null|{null f2}"); }
	@Test public void test_798() { checkNotSubtype("{X<{X f2}> f2}","null|(X<null|X>)"); }
	@Test public void test_799() { checkNotSubtype("{X<{X f2}> f2}","{null f1}|null"); }
	@Test public void test_800() { checkNotSubtype("{X<{X f2}> f2}","{null f2}|null"); }
	@Test public void test_801() { checkNotSubtype("{X<{X f2}> f2}","X<{X f1}>|null"); }
	@Test public void test_802() { checkNotSubtype("{X<{X f2}> f2}","X<{X f2}>|null"); }
	@Test public void test_803() { checkNotSubtype("{X<{X f2}> f2}","X<X|{null f1}>"); }
	@Test public void test_804() { checkNotSubtype("{X<{X f2}> f2}","X<X|{null f2}>"); }
	@Test public void test_805() { checkIsSubtype("{X<{X f2}> f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_806() { checkIsSubtype("{X<{X f2}> f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_807() { checkIsSubtype("{X<{X f2}> f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_808() { checkIsSubtype("{X<{X f2}> f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_809() { checkIsSubtype("{X<{X f2}> f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_810() { checkIsSubtype("{X<{X f2}> f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_811() { checkIsSubtype("{X<{X f2}> f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_812() { checkIsSubtype("{X<{X f2}> f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_813() { checkIsSubtype("{X<{X f2}> f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_814() { checkIsSubtype("{X<{X f2}> f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_815() { checkIsSubtype("{X<{X f2}> f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_816() { checkIsSubtype("{X<{X f2}> f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_817() { checkIsSubtype("{X<{X f2}> f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_818() { checkIsSubtype("{X<{X f2}> f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_819() { checkIsSubtype("{X<{X f2}> f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_820() { checkIsSubtype("{X<{X f2}> f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_821() { checkIsSubtype("{X<{X f2}> f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_822() { checkIsSubtype("{X<{X f2}> f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_823() { checkIsSubtype("{X<{X f2}> f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_824() { checkIsSubtype("{X<{X f2}> f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_825() { checkIsSubtype("{X<{X f2}> f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_826() { checkIsSubtype("{X<{X f2}> f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_827() { checkIsSubtype("{X<{X f2}> f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_828() { checkIsSubtype("{X<{X f2}> f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_829() { checkNotSubtype("{X<{X f2}> f2}","(X<X|null>)|null"); }
	@Test public void test_830() { checkNotSubtype("{X<{X f2}> f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_831() { checkIsSubtype("{X<{X f2}> f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_832() { checkIsSubtype("{X<{X f2}> f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_833() { checkIsSubtype("{X<{X f2}> f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_834() { checkIsSubtype("{X<{X f2}> f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_835() { checkIsSubtype("{X<{X f2}> f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_836() { checkIsSubtype("{X<{X f2}> f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_837() { checkIsSubtype("{X<{X f2}> f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_838() { checkIsSubtype("X<X|null>","null"); }
	@Test public void test_839() { checkIsSubtype("X<X|null>","X<{X f1}>"); }
	@Test public void test_840() { checkIsSubtype("X<X|null>","X<{X f2}>"); }
	@Test public void test_841() { checkNotSubtype("X<X|null>","{null f1}"); }
	@Test public void test_842() { checkNotSubtype("X<X|null>","{null f2}"); }
	@Test public void test_843() { checkIsSubtype("X<X|null>","{X<{X f1}> f1}"); }
	@Test public void test_844() { checkIsSubtype("X<X|null>","{X<{X f2}> f1}"); }
	@Test public void test_845() { checkIsSubtype("X<X|null>","{X<{X f1}> f2}"); }
	@Test public void test_846() { checkIsSubtype("X<X|null>","{X<{X f2}> f2}"); }
	@Test public void test_847() { checkIsSubtype("X<X|null>","X<X|null>"); }
	@Test public void test_848() { checkIsSubtype("X<X|null>","X<X|Y<{Y f1}>>"); }
	@Test public void test_849() { checkIsSubtype("X<X|null>","X<X|Y<{Y f2}>>"); }
	@Test public void test_850() { checkNotSubtype("X<X|null>","{{null f1} f1}"); }
	@Test public void test_851() { checkNotSubtype("X<X|null>","{{null f2} f1}"); }
	@Test public void test_852() { checkNotSubtype("X<X|null>","{{null f1} f2}"); }
	@Test public void test_853() { checkNotSubtype("X<X|null>","{{null f2} f2}"); }
	@Test public void test_854() { checkIsSubtype("X<X|null>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_855() { checkIsSubtype("X<X|null>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_856() { checkIsSubtype("X<X|null>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_857() { checkIsSubtype("X<X|null>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_858() { checkIsSubtype("X<X|null>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_859() { checkIsSubtype("X<X|null>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_860() { checkIsSubtype("X<X|null>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_861() { checkIsSubtype("X<X|null>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_862() { checkIsSubtype("X<X|null>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_863() { checkIsSubtype("X<X|null>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_864() { checkIsSubtype("X<X|null>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_865() { checkIsSubtype("X<X|null>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_866() { checkIsSubtype("X<X|null>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_867() { checkIsSubtype("X<X|null>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_868() { checkIsSubtype("X<X|null>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_869() { checkIsSubtype("X<X|null>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_870() { checkIsSubtype("X<X|null>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_871() { checkIsSubtype("X<X|null>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_872() { checkIsSubtype("X<X|null>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_873() { checkIsSubtype("X<X|null>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_874() { checkNotSubtype("X<X|null>","{X<X|null> f1}"); }
	@Test public void test_875() { checkNotSubtype("X<X|null>","{X<X|null> f2}"); }
	@Test public void test_876() { checkIsSubtype("X<X|null>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_877() { checkIsSubtype("X<X|null>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_878() { checkIsSubtype("X<X|null>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_879() { checkIsSubtype("X<X|null>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_880() { checkIsSubtype("X<X|null>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_881() { checkIsSubtype("X<X|null>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_882() { checkIsSubtype("X<X|null>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_883() { checkIsSubtype("X<X|null>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_884() { checkIsSubtype("X<X|null>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_885() { checkIsSubtype("X<X|null>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_886() { checkIsSubtype("X<X|null>","null|null"); }
	@Test public void test_887() { checkIsSubtype("X<X|null>","null|X<{X f1}>"); }
	@Test public void test_888() { checkIsSubtype("X<X|null>","null|X<{X f2}>"); }
	@Test public void test_889() { checkNotSubtype("X<X|null>","null|{null f1}"); }
	@Test public void test_890() { checkNotSubtype("X<X|null>","null|{null f2}"); }
	@Test public void test_891() { checkIsSubtype("X<X|null>","null|(X<null|X>)"); }
	@Test public void test_892() { checkNotSubtype("X<X|null>","{null f1}|null"); }
	@Test public void test_893() { checkNotSubtype("X<X|null>","{null f2}|null"); }
	@Test public void test_894() { checkIsSubtype("X<X|null>","X<{X f1}>|null"); }
	@Test public void test_895() { checkIsSubtype("X<X|null>","X<{X f2}>|null"); }
	@Test public void test_896() { checkNotSubtype("X<X|null>","X<X|{null f1}>"); }
	@Test public void test_897() { checkNotSubtype("X<X|null>","X<X|{null f2}>"); }
	@Test public void test_898() { checkIsSubtype("X<X|null>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_899() { checkIsSubtype("X<X|null>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_900() { checkIsSubtype("X<X|null>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_901() { checkIsSubtype("X<X|null>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_902() { checkIsSubtype("X<X|null>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_903() { checkIsSubtype("X<X|null>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_904() { checkIsSubtype("X<X|null>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_905() { checkIsSubtype("X<X|null>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_906() { checkIsSubtype("X<X|null>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_907() { checkIsSubtype("X<X|null>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_908() { checkIsSubtype("X<X|null>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_909() { checkIsSubtype("X<X|null>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_910() { checkIsSubtype("X<X|null>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_911() { checkIsSubtype("X<X|null>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_912() { checkIsSubtype("X<X|null>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_913() { checkIsSubtype("X<X|null>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_914() { checkIsSubtype("X<X|null>","X<X|{{X f1} f1}>"); }
	@Test public void test_915() { checkIsSubtype("X<X|null>","X<X|{{X f2} f1}>"); }
	@Test public void test_916() { checkIsSubtype("X<X|null>","X<X|{{X f1} f2}>"); }
	@Test public void test_917() { checkIsSubtype("X<X|null>","X<X|{{X f2} f2}>"); }
	@Test public void test_918() { checkIsSubtype("X<X|null>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_919() { checkIsSubtype("X<X|null>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_920() { checkIsSubtype("X<X|null>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_921() { checkIsSubtype("X<X|null>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_922() { checkIsSubtype("X<X|null>","(X<X|null>)|null"); }
	@Test public void test_923() { checkIsSubtype("X<X|null>","X<X|(Y<Y|null>)>"); }
	@Test public void test_924() { checkIsSubtype("X<X|null>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_925() { checkIsSubtype("X<X|null>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_926() { checkIsSubtype("X<X|null>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_927() { checkIsSubtype("X<X|null>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_928() { checkIsSubtype("X<X|null>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_929() { checkIsSubtype("X<X|null>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_930() { checkIsSubtype("X<X|null>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_931() { checkNotSubtype("X<X|Y<{Y f1}>>","null"); }
	@Test public void test_932() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f1}>"); }
	@Test public void test_933() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f2}>"); }
	@Test public void test_934() { checkNotSubtype("X<X|Y<{Y f1}>>","{null f1}"); }
	@Test public void test_935() { checkNotSubtype("X<X|Y<{Y f1}>>","{null f2}"); }
	@Test public void test_936() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<{X f1}> f1}"); }
	@Test public void test_937() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<{X f2}> f1}"); }
	@Test public void test_938() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<{X f1}> f2}"); }
	@Test public void test_939() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<{X f2}> f2}"); }
	@Test public void test_940() { checkNotSubtype("X<X|Y<{Y f1}>>","X<X|null>"); }
	@Test public void test_941() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|Y<{Y f1}>>"); }
	@Test public void test_942() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|Y<{Y f2}>>"); }
	@Test public void test_943() { checkNotSubtype("X<X|Y<{Y f1}>>","{{null f1} f1}"); }
	@Test public void test_944() { checkNotSubtype("X<X|Y<{Y f1}>>","{{null f2} f1}"); }
	@Test public void test_945() { checkNotSubtype("X<X|Y<{Y f1}>>","{{null f1} f2}"); }
	@Test public void test_946() { checkNotSubtype("X<X|Y<{Y f1}>>","{{null f2} f2}"); }
	@Test public void test_947() { checkIsSubtype("X<X|Y<{Y f1}>>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_948() { checkIsSubtype("X<X|Y<{Y f1}>>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_949() { checkIsSubtype("X<X|Y<{Y f1}>>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_950() { checkIsSubtype("X<X|Y<{Y f1}>>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_951() { checkIsSubtype("X<X|Y<{Y f1}>>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_952() { checkIsSubtype("X<X|Y<{Y f1}>>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_953() { checkIsSubtype("X<X|Y<{Y f1}>>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_954() { checkIsSubtype("X<X|Y<{Y f1}>>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_955() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_956() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_957() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_958() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_959() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_960() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_961() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_962() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_963() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_964() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_965() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_966() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_967() { checkNotSubtype("X<X|Y<{Y f1}>>","{X<X|null> f1}"); }
	@Test public void test_968() { checkNotSubtype("X<X|Y<{Y f1}>>","{X<X|null> f2}"); }
	@Test public void test_969() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_970() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_971() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_972() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_973() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_974() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_975() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_976() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_977() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_978() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_979() { checkNotSubtype("X<X|Y<{Y f1}>>","null|null"); }
	@Test public void test_980() { checkNotSubtype("X<X|Y<{Y f1}>>","null|X<{X f1}>"); }
	@Test public void test_981() { checkNotSubtype("X<X|Y<{Y f1}>>","null|X<{X f2}>"); }
	@Test public void test_982() { checkNotSubtype("X<X|Y<{Y f1}>>","null|{null f1}"); }
	@Test public void test_983() { checkNotSubtype("X<X|Y<{Y f1}>>","null|{null f2}"); }
	@Test public void test_984() { checkNotSubtype("X<X|Y<{Y f1}>>","null|(X<null|X>)"); }
	@Test public void test_985() { checkNotSubtype("X<X|Y<{Y f1}>>","{null f1}|null"); }
	@Test public void test_986() { checkNotSubtype("X<X|Y<{Y f1}>>","{null f2}|null"); }
	@Test public void test_987() { checkNotSubtype("X<X|Y<{Y f1}>>","X<{X f1}>|null"); }
	@Test public void test_988() { checkNotSubtype("X<X|Y<{Y f1}>>","X<{X f2}>|null"); }
	@Test public void test_989() { checkNotSubtype("X<X|Y<{Y f1}>>","X<X|{null f1}>"); }
	@Test public void test_990() { checkNotSubtype("X<X|Y<{Y f1}>>","X<X|{null f2}>"); }
	@Test public void test_991() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_992() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_993() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_994() { checkIsSubtype("X<X|Y<{Y f1}>>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_995() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_996() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_997() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_998() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_999() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1000() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1001() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1002() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1003() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1004() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1005() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1006() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1007() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{{X f1} f1}>"); }
	@Test public void test_1008() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{{X f2} f1}>"); }
	@Test public void test_1009() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{{X f1} f2}>"); }
	@Test public void test_1010() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{{X f2} f2}>"); }
	@Test public void test_1011() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1012() { checkIsSubtype("X<X|Y<{Y f1}>>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1013() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1014() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1015() { checkNotSubtype("X<X|Y<{Y f1}>>","(X<X|null>)|null"); }
	@Test public void test_1016() { checkNotSubtype("X<X|Y<{Y f1}>>","X<X|(Y<Y|null>)>"); }
	@Test public void test_1017() { checkIsSubtype("X<X|Y<{Y f1}>>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1018() { checkIsSubtype("X<X|Y<{Y f1}>>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1019() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1020() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1021() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1022() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1023() { checkIsSubtype("X<X|Y<{Y f1}>>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1024() { checkNotSubtype("X<X|Y<{Y f2}>>","null"); }
	@Test public void test_1025() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f1}>"); }
	@Test public void test_1026() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f2}>"); }
	@Test public void test_1027() { checkNotSubtype("X<X|Y<{Y f2}>>","{null f1}"); }
	@Test public void test_1028() { checkNotSubtype("X<X|Y<{Y f2}>>","{null f2}"); }
	@Test public void test_1029() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<{X f1}> f1}"); }
	@Test public void test_1030() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<{X f2}> f1}"); }
	@Test public void test_1031() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<{X f1}> f2}"); }
	@Test public void test_1032() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<{X f2}> f2}"); }
	@Test public void test_1033() { checkNotSubtype("X<X|Y<{Y f2}>>","X<X|null>"); }
	@Test public void test_1034() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|Y<{Y f1}>>"); }
	@Test public void test_1035() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|Y<{Y f2}>>"); }
	@Test public void test_1036() { checkNotSubtype("X<X|Y<{Y f2}>>","{{null f1} f1}"); }
	@Test public void test_1037() { checkNotSubtype("X<X|Y<{Y f2}>>","{{null f2} f1}"); }
	@Test public void test_1038() { checkNotSubtype("X<X|Y<{Y f2}>>","{{null f1} f2}"); }
	@Test public void test_1039() { checkNotSubtype("X<X|Y<{Y f2}>>","{{null f2} f2}"); }
	@Test public void test_1040() { checkIsSubtype("X<X|Y<{Y f2}>>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1041() { checkIsSubtype("X<X|Y<{Y f2}>>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1042() { checkIsSubtype("X<X|Y<{Y f2}>>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1043() { checkIsSubtype("X<X|Y<{Y f2}>>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1044() { checkIsSubtype("X<X|Y<{Y f2}>>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1045() { checkIsSubtype("X<X|Y<{Y f2}>>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1046() { checkIsSubtype("X<X|Y<{Y f2}>>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1047() { checkIsSubtype("X<X|Y<{Y f2}>>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1048() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1049() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1050() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1051() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1052() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1053() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1054() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1055() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1056() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1057() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1058() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1059() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1060() { checkNotSubtype("X<X|Y<{Y f2}>>","{X<X|null> f1}"); }
	@Test public void test_1061() { checkNotSubtype("X<X|Y<{Y f2}>>","{X<X|null> f2}"); }
	@Test public void test_1062() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1063() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1064() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1065() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1066() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1067() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1068() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1069() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1070() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1071() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1072() { checkNotSubtype("X<X|Y<{Y f2}>>","null|null"); }
	@Test public void test_1073() { checkNotSubtype("X<X|Y<{Y f2}>>","null|X<{X f1}>"); }
	@Test public void test_1074() { checkNotSubtype("X<X|Y<{Y f2}>>","null|X<{X f2}>"); }
	@Test public void test_1075() { checkNotSubtype("X<X|Y<{Y f2}>>","null|{null f1}"); }
	@Test public void test_1076() { checkNotSubtype("X<X|Y<{Y f2}>>","null|{null f2}"); }
	@Test public void test_1077() { checkNotSubtype("X<X|Y<{Y f2}>>","null|(X<null|X>)"); }
	@Test public void test_1078() { checkNotSubtype("X<X|Y<{Y f2}>>","{null f1}|null"); }
	@Test public void test_1079() { checkNotSubtype("X<X|Y<{Y f2}>>","{null f2}|null"); }
	@Test public void test_1080() { checkNotSubtype("X<X|Y<{Y f2}>>","X<{X f1}>|null"); }
	@Test public void test_1081() { checkNotSubtype("X<X|Y<{Y f2}>>","X<{X f2}>|null"); }
	@Test public void test_1082() { checkNotSubtype("X<X|Y<{Y f2}>>","X<X|{null f1}>"); }
	@Test public void test_1083() { checkNotSubtype("X<X|Y<{Y f2}>>","X<X|{null f2}>"); }
	@Test public void test_1084() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1085() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1086() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1087() { checkIsSubtype("X<X|Y<{Y f2}>>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1088() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1089() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1090() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1091() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1092() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1093() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1094() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1095() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1096() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1097() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1098() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1099() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1100() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{{X f1} f1}>"); }
	@Test public void test_1101() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{{X f2} f1}>"); }
	@Test public void test_1102() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{{X f1} f2}>"); }
	@Test public void test_1103() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{{X f2} f2}>"); }
	@Test public void test_1104() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1105() { checkIsSubtype("X<X|Y<{Y f2}>>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1106() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1107() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1108() { checkNotSubtype("X<X|Y<{Y f2}>>","(X<X|null>)|null"); }
	@Test public void test_1109() { checkNotSubtype("X<X|Y<{Y f2}>>","X<X|(Y<Y|null>)>"); }
	@Test public void test_1110() { checkIsSubtype("X<X|Y<{Y f2}>>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1111() { checkIsSubtype("X<X|Y<{Y f2}>>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1112() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1113() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1114() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1115() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1116() { checkIsSubtype("X<X|Y<{Y f2}>>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1117() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test public void test_1118() { checkIsSubtype("{{null f1} f1}","X<{X f1}>"); }
	@Test public void test_1119() { checkIsSubtype("{{null f1} f1}","X<{X f2}>"); }
	@Test public void test_1120() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test public void test_1121() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test public void test_1122() { checkIsSubtype("{{null f1} f1}","{X<{X f1}> f1}"); }
	@Test public void test_1123() { checkIsSubtype("{{null f1} f1}","{X<{X f2}> f1}"); }
	@Test public void test_1124() { checkIsSubtype("{{null f1} f1}","{X<{X f1}> f2}"); }
	@Test public void test_1125() { checkIsSubtype("{{null f1} f1}","{X<{X f2}> f2}"); }
	@Test public void test_1126() { checkNotSubtype("{{null f1} f1}","X<X|null>"); }
	@Test public void test_1127() { checkIsSubtype("{{null f1} f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1128() { checkIsSubtype("{{null f1} f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1129() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test public void test_1130() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test public void test_1131() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test public void test_1132() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test public void test_1133() { checkIsSubtype("{{null f1} f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1134() { checkIsSubtype("{{null f1} f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1135() { checkIsSubtype("{{null f1} f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1136() { checkIsSubtype("{{null f1} f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1137() { checkIsSubtype("{{null f1} f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1138() { checkIsSubtype("{{null f1} f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1139() { checkIsSubtype("{{null f1} f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1140() { checkIsSubtype("{{null f1} f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1141() { checkIsSubtype("{{null f1} f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1142() { checkIsSubtype("{{null f1} f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1143() { checkIsSubtype("{{null f1} f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1144() { checkIsSubtype("{{null f1} f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1145() { checkIsSubtype("{{null f1} f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1146() { checkIsSubtype("{{null f1} f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1147() { checkIsSubtype("{{null f1} f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1148() { checkIsSubtype("{{null f1} f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1149() { checkIsSubtype("{{null f1} f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1150() { checkIsSubtype("{{null f1} f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1151() { checkIsSubtype("{{null f1} f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1152() { checkIsSubtype("{{null f1} f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1153() { checkNotSubtype("{{null f1} f1}","{X<X|null> f1}"); }
	@Test public void test_1154() { checkNotSubtype("{{null f1} f1}","{X<X|null> f2}"); }
	@Test public void test_1155() { checkIsSubtype("{{null f1} f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1156() { checkIsSubtype("{{null f1} f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1157() { checkIsSubtype("{{null f1} f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1158() { checkIsSubtype("{{null f1} f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1159() { checkIsSubtype("{{null f1} f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1160() { checkIsSubtype("{{null f1} f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1161() { checkIsSubtype("{{null f1} f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1162() { checkIsSubtype("{{null f1} f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1163() { checkIsSubtype("{{null f1} f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1164() { checkIsSubtype("{{null f1} f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1165() { checkNotSubtype("{{null f1} f1}","null|null"); }
	@Test public void test_1166() { checkNotSubtype("{{null f1} f1}","null|X<{X f1}>"); }
	@Test public void test_1167() { checkNotSubtype("{{null f1} f1}","null|X<{X f2}>"); }
	@Test public void test_1168() { checkNotSubtype("{{null f1} f1}","null|{null f1}"); }
	@Test public void test_1169() { checkNotSubtype("{{null f1} f1}","null|{null f2}"); }
	@Test public void test_1170() { checkNotSubtype("{{null f1} f1}","null|(X<null|X>)"); }
	@Test public void test_1171() { checkNotSubtype("{{null f1} f1}","{null f1}|null"); }
	@Test public void test_1172() { checkNotSubtype("{{null f1} f1}","{null f2}|null"); }
	@Test public void test_1173() { checkNotSubtype("{{null f1} f1}","X<{X f1}>|null"); }
	@Test public void test_1174() { checkNotSubtype("{{null f1} f1}","X<{X f2}>|null"); }
	@Test public void test_1175() { checkNotSubtype("{{null f1} f1}","X<X|{null f1}>"); }
	@Test public void test_1176() { checkNotSubtype("{{null f1} f1}","X<X|{null f2}>"); }
	@Test public void test_1177() { checkIsSubtype("{{null f1} f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1178() { checkIsSubtype("{{null f1} f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1179() { checkIsSubtype("{{null f1} f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1180() { checkIsSubtype("{{null f1} f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1181() { checkIsSubtype("{{null f1} f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1182() { checkIsSubtype("{{null f1} f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1183() { checkIsSubtype("{{null f1} f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1184() { checkIsSubtype("{{null f1} f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1185() { checkIsSubtype("{{null f1} f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1186() { checkIsSubtype("{{null f1} f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1187() { checkIsSubtype("{{null f1} f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1188() { checkIsSubtype("{{null f1} f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1189() { checkIsSubtype("{{null f1} f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1190() { checkIsSubtype("{{null f1} f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1191() { checkIsSubtype("{{null f1} f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1192() { checkIsSubtype("{{null f1} f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1193() { checkIsSubtype("{{null f1} f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_1194() { checkIsSubtype("{{null f1} f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_1195() { checkIsSubtype("{{null f1} f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_1196() { checkIsSubtype("{{null f1} f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_1197() { checkIsSubtype("{{null f1} f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1198() { checkIsSubtype("{{null f1} f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1199() { checkIsSubtype("{{null f1} f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1200() { checkIsSubtype("{{null f1} f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1201() { checkNotSubtype("{{null f1} f1}","(X<X|null>)|null"); }
	@Test public void test_1202() { checkNotSubtype("{{null f1} f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_1203() { checkIsSubtype("{{null f1} f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1204() { checkIsSubtype("{{null f1} f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1205() { checkIsSubtype("{{null f1} f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1206() { checkIsSubtype("{{null f1} f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1207() { checkIsSubtype("{{null f1} f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1208() { checkIsSubtype("{{null f1} f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1209() { checkIsSubtype("{{null f1} f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1210() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test public void test_1211() { checkIsSubtype("{{null f2} f1}","X<{X f1}>"); }
	@Test public void test_1212() { checkIsSubtype("{{null f2} f1}","X<{X f2}>"); }
	@Test public void test_1213() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test public void test_1214() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test public void test_1215() { checkIsSubtype("{{null f2} f1}","{X<{X f1}> f1}"); }
	@Test public void test_1216() { checkIsSubtype("{{null f2} f1}","{X<{X f2}> f1}"); }
	@Test public void test_1217() { checkIsSubtype("{{null f2} f1}","{X<{X f1}> f2}"); }
	@Test public void test_1218() { checkIsSubtype("{{null f2} f1}","{X<{X f2}> f2}"); }
	@Test public void test_1219() { checkNotSubtype("{{null f2} f1}","X<X|null>"); }
	@Test public void test_1220() { checkIsSubtype("{{null f2} f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1221() { checkIsSubtype("{{null f2} f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1222() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test public void test_1223() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test public void test_1224() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test public void test_1225() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test public void test_1226() { checkIsSubtype("{{null f2} f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1227() { checkIsSubtype("{{null f2} f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1228() { checkIsSubtype("{{null f2} f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1229() { checkIsSubtype("{{null f2} f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1230() { checkIsSubtype("{{null f2} f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1231() { checkIsSubtype("{{null f2} f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1232() { checkIsSubtype("{{null f2} f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1233() { checkIsSubtype("{{null f2} f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1234() { checkIsSubtype("{{null f2} f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1235() { checkIsSubtype("{{null f2} f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1236() { checkIsSubtype("{{null f2} f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1237() { checkIsSubtype("{{null f2} f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1238() { checkIsSubtype("{{null f2} f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1239() { checkIsSubtype("{{null f2} f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1240() { checkIsSubtype("{{null f2} f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1241() { checkIsSubtype("{{null f2} f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1242() { checkIsSubtype("{{null f2} f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1243() { checkIsSubtype("{{null f2} f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1244() { checkIsSubtype("{{null f2} f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1245() { checkIsSubtype("{{null f2} f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1246() { checkNotSubtype("{{null f2} f1}","{X<X|null> f1}"); }
	@Test public void test_1247() { checkNotSubtype("{{null f2} f1}","{X<X|null> f2}"); }
	@Test public void test_1248() { checkIsSubtype("{{null f2} f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1249() { checkIsSubtype("{{null f2} f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1250() { checkIsSubtype("{{null f2} f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1251() { checkIsSubtype("{{null f2} f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1252() { checkIsSubtype("{{null f2} f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1253() { checkIsSubtype("{{null f2} f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1254() { checkIsSubtype("{{null f2} f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1255() { checkIsSubtype("{{null f2} f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1256() { checkIsSubtype("{{null f2} f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1257() { checkIsSubtype("{{null f2} f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1258() { checkNotSubtype("{{null f2} f1}","null|null"); }
	@Test public void test_1259() { checkNotSubtype("{{null f2} f1}","null|X<{X f1}>"); }
	@Test public void test_1260() { checkNotSubtype("{{null f2} f1}","null|X<{X f2}>"); }
	@Test public void test_1261() { checkNotSubtype("{{null f2} f1}","null|{null f1}"); }
	@Test public void test_1262() { checkNotSubtype("{{null f2} f1}","null|{null f2}"); }
	@Test public void test_1263() { checkNotSubtype("{{null f2} f1}","null|(X<null|X>)"); }
	@Test public void test_1264() { checkNotSubtype("{{null f2} f1}","{null f1}|null"); }
	@Test public void test_1265() { checkNotSubtype("{{null f2} f1}","{null f2}|null"); }
	@Test public void test_1266() { checkNotSubtype("{{null f2} f1}","X<{X f1}>|null"); }
	@Test public void test_1267() { checkNotSubtype("{{null f2} f1}","X<{X f2}>|null"); }
	@Test public void test_1268() { checkNotSubtype("{{null f2} f1}","X<X|{null f1}>"); }
	@Test public void test_1269() { checkNotSubtype("{{null f2} f1}","X<X|{null f2}>"); }
	@Test public void test_1270() { checkIsSubtype("{{null f2} f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1271() { checkIsSubtype("{{null f2} f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1272() { checkIsSubtype("{{null f2} f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1273() { checkIsSubtype("{{null f2} f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1274() { checkIsSubtype("{{null f2} f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1275() { checkIsSubtype("{{null f2} f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1276() { checkIsSubtype("{{null f2} f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1277() { checkIsSubtype("{{null f2} f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1278() { checkIsSubtype("{{null f2} f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1279() { checkIsSubtype("{{null f2} f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1280() { checkIsSubtype("{{null f2} f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1281() { checkIsSubtype("{{null f2} f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1282() { checkIsSubtype("{{null f2} f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1283() { checkIsSubtype("{{null f2} f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1284() { checkIsSubtype("{{null f2} f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1285() { checkIsSubtype("{{null f2} f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1286() { checkIsSubtype("{{null f2} f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_1287() { checkIsSubtype("{{null f2} f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_1288() { checkIsSubtype("{{null f2} f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_1289() { checkIsSubtype("{{null f2} f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_1290() { checkIsSubtype("{{null f2} f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1291() { checkIsSubtype("{{null f2} f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1292() { checkIsSubtype("{{null f2} f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1293() { checkIsSubtype("{{null f2} f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1294() { checkNotSubtype("{{null f2} f1}","(X<X|null>)|null"); }
	@Test public void test_1295() { checkNotSubtype("{{null f2} f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_1296() { checkIsSubtype("{{null f2} f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1297() { checkIsSubtype("{{null f2} f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1298() { checkIsSubtype("{{null f2} f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1299() { checkIsSubtype("{{null f2} f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1300() { checkIsSubtype("{{null f2} f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1301() { checkIsSubtype("{{null f2} f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1302() { checkIsSubtype("{{null f2} f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1303() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test public void test_1304() { checkIsSubtype("{{null f1} f2}","X<{X f1}>"); }
	@Test public void test_1305() { checkIsSubtype("{{null f1} f2}","X<{X f2}>"); }
	@Test public void test_1306() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test public void test_1307() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test public void test_1308() { checkIsSubtype("{{null f1} f2}","{X<{X f1}> f1}"); }
	@Test public void test_1309() { checkIsSubtype("{{null f1} f2}","{X<{X f2}> f1}"); }
	@Test public void test_1310() { checkIsSubtype("{{null f1} f2}","{X<{X f1}> f2}"); }
	@Test public void test_1311() { checkIsSubtype("{{null f1} f2}","{X<{X f2}> f2}"); }
	@Test public void test_1312() { checkNotSubtype("{{null f1} f2}","X<X|null>"); }
	@Test public void test_1313() { checkIsSubtype("{{null f1} f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1314() { checkIsSubtype("{{null f1} f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1315() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test public void test_1316() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test public void test_1317() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test public void test_1318() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test public void test_1319() { checkIsSubtype("{{null f1} f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1320() { checkIsSubtype("{{null f1} f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1321() { checkIsSubtype("{{null f1} f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1322() { checkIsSubtype("{{null f1} f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1323() { checkIsSubtype("{{null f1} f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1324() { checkIsSubtype("{{null f1} f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1325() { checkIsSubtype("{{null f1} f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1326() { checkIsSubtype("{{null f1} f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1327() { checkIsSubtype("{{null f1} f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1328() { checkIsSubtype("{{null f1} f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1329() { checkIsSubtype("{{null f1} f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1330() { checkIsSubtype("{{null f1} f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1331() { checkIsSubtype("{{null f1} f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1332() { checkIsSubtype("{{null f1} f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1333() { checkIsSubtype("{{null f1} f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1334() { checkIsSubtype("{{null f1} f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1335() { checkIsSubtype("{{null f1} f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1336() { checkIsSubtype("{{null f1} f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1337() { checkIsSubtype("{{null f1} f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1338() { checkIsSubtype("{{null f1} f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1339() { checkNotSubtype("{{null f1} f2}","{X<X|null> f1}"); }
	@Test public void test_1340() { checkNotSubtype("{{null f1} f2}","{X<X|null> f2}"); }
	@Test public void test_1341() { checkIsSubtype("{{null f1} f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1342() { checkIsSubtype("{{null f1} f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1343() { checkIsSubtype("{{null f1} f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1344() { checkIsSubtype("{{null f1} f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1345() { checkIsSubtype("{{null f1} f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1346() { checkIsSubtype("{{null f1} f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1347() { checkIsSubtype("{{null f1} f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1348() { checkIsSubtype("{{null f1} f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1349() { checkIsSubtype("{{null f1} f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1350() { checkIsSubtype("{{null f1} f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1351() { checkNotSubtype("{{null f1} f2}","null|null"); }
	@Test public void test_1352() { checkNotSubtype("{{null f1} f2}","null|X<{X f1}>"); }
	@Test public void test_1353() { checkNotSubtype("{{null f1} f2}","null|X<{X f2}>"); }
	@Test public void test_1354() { checkNotSubtype("{{null f1} f2}","null|{null f1}"); }
	@Test public void test_1355() { checkNotSubtype("{{null f1} f2}","null|{null f2}"); }
	@Test public void test_1356() { checkNotSubtype("{{null f1} f2}","null|(X<null|X>)"); }
	@Test public void test_1357() { checkNotSubtype("{{null f1} f2}","{null f1}|null"); }
	@Test public void test_1358() { checkNotSubtype("{{null f1} f2}","{null f2}|null"); }
	@Test public void test_1359() { checkNotSubtype("{{null f1} f2}","X<{X f1}>|null"); }
	@Test public void test_1360() { checkNotSubtype("{{null f1} f2}","X<{X f2}>|null"); }
	@Test public void test_1361() { checkNotSubtype("{{null f1} f2}","X<X|{null f1}>"); }
	@Test public void test_1362() { checkNotSubtype("{{null f1} f2}","X<X|{null f2}>"); }
	@Test public void test_1363() { checkIsSubtype("{{null f1} f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1364() { checkIsSubtype("{{null f1} f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1365() { checkIsSubtype("{{null f1} f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1366() { checkIsSubtype("{{null f1} f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1367() { checkIsSubtype("{{null f1} f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1368() { checkIsSubtype("{{null f1} f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1369() { checkIsSubtype("{{null f1} f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1370() { checkIsSubtype("{{null f1} f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1371() { checkIsSubtype("{{null f1} f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1372() { checkIsSubtype("{{null f1} f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1373() { checkIsSubtype("{{null f1} f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1374() { checkIsSubtype("{{null f1} f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1375() { checkIsSubtype("{{null f1} f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1376() { checkIsSubtype("{{null f1} f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1377() { checkIsSubtype("{{null f1} f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1378() { checkIsSubtype("{{null f1} f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1379() { checkIsSubtype("{{null f1} f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_1380() { checkIsSubtype("{{null f1} f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_1381() { checkIsSubtype("{{null f1} f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_1382() { checkIsSubtype("{{null f1} f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_1383() { checkIsSubtype("{{null f1} f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1384() { checkIsSubtype("{{null f1} f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1385() { checkIsSubtype("{{null f1} f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1386() { checkIsSubtype("{{null f1} f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1387() { checkNotSubtype("{{null f1} f2}","(X<X|null>)|null"); }
	@Test public void test_1388() { checkNotSubtype("{{null f1} f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_1389() { checkIsSubtype("{{null f1} f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1390() { checkIsSubtype("{{null f1} f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1391() { checkIsSubtype("{{null f1} f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1392() { checkIsSubtype("{{null f1} f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1393() { checkIsSubtype("{{null f1} f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1394() { checkIsSubtype("{{null f1} f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1395() { checkIsSubtype("{{null f1} f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1396() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test public void test_1397() { checkIsSubtype("{{null f2} f2}","X<{X f1}>"); }
	@Test public void test_1398() { checkIsSubtype("{{null f2} f2}","X<{X f2}>"); }
	@Test public void test_1399() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test public void test_1400() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test public void test_1401() { checkIsSubtype("{{null f2} f2}","{X<{X f1}> f1}"); }
	@Test public void test_1402() { checkIsSubtype("{{null f2} f2}","{X<{X f2}> f1}"); }
	@Test public void test_1403() { checkIsSubtype("{{null f2} f2}","{X<{X f1}> f2}"); }
	@Test public void test_1404() { checkIsSubtype("{{null f2} f2}","{X<{X f2}> f2}"); }
	@Test public void test_1405() { checkNotSubtype("{{null f2} f2}","X<X|null>"); }
	@Test public void test_1406() { checkIsSubtype("{{null f2} f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1407() { checkIsSubtype("{{null f2} f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1408() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test public void test_1409() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test public void test_1410() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test public void test_1411() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test public void test_1412() { checkIsSubtype("{{null f2} f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1413() { checkIsSubtype("{{null f2} f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1414() { checkIsSubtype("{{null f2} f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1415() { checkIsSubtype("{{null f2} f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1416() { checkIsSubtype("{{null f2} f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1417() { checkIsSubtype("{{null f2} f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1418() { checkIsSubtype("{{null f2} f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1419() { checkIsSubtype("{{null f2} f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1420() { checkIsSubtype("{{null f2} f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1421() { checkIsSubtype("{{null f2} f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1422() { checkIsSubtype("{{null f2} f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1423() { checkIsSubtype("{{null f2} f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1424() { checkIsSubtype("{{null f2} f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1425() { checkIsSubtype("{{null f2} f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1426() { checkIsSubtype("{{null f2} f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1427() { checkIsSubtype("{{null f2} f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1428() { checkIsSubtype("{{null f2} f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1429() { checkIsSubtype("{{null f2} f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1430() { checkIsSubtype("{{null f2} f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1431() { checkIsSubtype("{{null f2} f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1432() { checkNotSubtype("{{null f2} f2}","{X<X|null> f1}"); }
	@Test public void test_1433() { checkNotSubtype("{{null f2} f2}","{X<X|null> f2}"); }
	@Test public void test_1434() { checkIsSubtype("{{null f2} f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1435() { checkIsSubtype("{{null f2} f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1436() { checkIsSubtype("{{null f2} f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1437() { checkIsSubtype("{{null f2} f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1438() { checkIsSubtype("{{null f2} f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1439() { checkIsSubtype("{{null f2} f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1440() { checkIsSubtype("{{null f2} f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1441() { checkIsSubtype("{{null f2} f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1442() { checkIsSubtype("{{null f2} f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1443() { checkIsSubtype("{{null f2} f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1444() { checkNotSubtype("{{null f2} f2}","null|null"); }
	@Test public void test_1445() { checkNotSubtype("{{null f2} f2}","null|X<{X f1}>"); }
	@Test public void test_1446() { checkNotSubtype("{{null f2} f2}","null|X<{X f2}>"); }
	@Test public void test_1447() { checkNotSubtype("{{null f2} f2}","null|{null f1}"); }
	@Test public void test_1448() { checkNotSubtype("{{null f2} f2}","null|{null f2}"); }
	@Test public void test_1449() { checkNotSubtype("{{null f2} f2}","null|(X<null|X>)"); }
	@Test public void test_1450() { checkNotSubtype("{{null f2} f2}","{null f1}|null"); }
	@Test public void test_1451() { checkNotSubtype("{{null f2} f2}","{null f2}|null"); }
	@Test public void test_1452() { checkNotSubtype("{{null f2} f2}","X<{X f1}>|null"); }
	@Test public void test_1453() { checkNotSubtype("{{null f2} f2}","X<{X f2}>|null"); }
	@Test public void test_1454() { checkNotSubtype("{{null f2} f2}","X<X|{null f1}>"); }
	@Test public void test_1455() { checkNotSubtype("{{null f2} f2}","X<X|{null f2}>"); }
	@Test public void test_1456() { checkIsSubtype("{{null f2} f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1457() { checkIsSubtype("{{null f2} f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1458() { checkIsSubtype("{{null f2} f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1459() { checkIsSubtype("{{null f2} f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1460() { checkIsSubtype("{{null f2} f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1461() { checkIsSubtype("{{null f2} f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1462() { checkIsSubtype("{{null f2} f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1463() { checkIsSubtype("{{null f2} f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1464() { checkIsSubtype("{{null f2} f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1465() { checkIsSubtype("{{null f2} f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1466() { checkIsSubtype("{{null f2} f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1467() { checkIsSubtype("{{null f2} f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1468() { checkIsSubtype("{{null f2} f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1469() { checkIsSubtype("{{null f2} f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1470() { checkIsSubtype("{{null f2} f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1471() { checkIsSubtype("{{null f2} f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1472() { checkIsSubtype("{{null f2} f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_1473() { checkIsSubtype("{{null f2} f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_1474() { checkIsSubtype("{{null f2} f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_1475() { checkIsSubtype("{{null f2} f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_1476() { checkIsSubtype("{{null f2} f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1477() { checkIsSubtype("{{null f2} f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1478() { checkIsSubtype("{{null f2} f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1479() { checkIsSubtype("{{null f2} f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1480() { checkNotSubtype("{{null f2} f2}","(X<X|null>)|null"); }
	@Test public void test_1481() { checkNotSubtype("{{null f2} f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_1482() { checkIsSubtype("{{null f2} f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1483() { checkIsSubtype("{{null f2} f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1484() { checkIsSubtype("{{null f2} f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1485() { checkIsSubtype("{{null f2} f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1486() { checkIsSubtype("{{null f2} f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1487() { checkIsSubtype("{{null f2} f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1488() { checkIsSubtype("{{null f2} f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1489() { checkNotSubtype("{{X<{X f1}> f1} f1}","null"); }
	@Test public void test_1490() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f1}>"); }
	@Test public void test_1491() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f2}>"); }
	@Test public void test_1492() { checkNotSubtype("{{X<{X f1}> f1} f1}","{null f1}"); }
	@Test public void test_1493() { checkNotSubtype("{{X<{X f1}> f1} f1}","{null f2}"); }
	@Test public void test_1494() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<{X f1}> f1}"); }
	@Test public void test_1495() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<{X f2}> f1}"); }
	@Test public void test_1496() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<{X f1}> f2}"); }
	@Test public void test_1497() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<{X f2}> f2}"); }
	@Test public void test_1498() { checkNotSubtype("{{X<{X f1}> f1} f1}","X<X|null>"); }
	@Test public void test_1499() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1500() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1501() { checkNotSubtype("{{X<{X f1}> f1} f1}","{{null f1} f1}"); }
	@Test public void test_1502() { checkNotSubtype("{{X<{X f1}> f1} f1}","{{null f2} f1}"); }
	@Test public void test_1503() { checkNotSubtype("{{X<{X f1}> f1} f1}","{{null f1} f2}"); }
	@Test public void test_1504() { checkNotSubtype("{{X<{X f1}> f1} f1}","{{null f2} f2}"); }
	@Test public void test_1505() { checkIsSubtype("{{X<{X f1}> f1} f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1506() { checkIsSubtype("{{X<{X f1}> f1} f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1507() { checkIsSubtype("{{X<{X f1}> f1} f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1508() { checkIsSubtype("{{X<{X f1}> f1} f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1509() { checkIsSubtype("{{X<{X f1}> f1} f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1510() { checkIsSubtype("{{X<{X f1}> f1} f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1511() { checkIsSubtype("{{X<{X f1}> f1} f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1512() { checkIsSubtype("{{X<{X f1}> f1} f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1513() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1514() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1515() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1516() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1517() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1518() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1519() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1520() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1521() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1522() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1523() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1524() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1525() { checkNotSubtype("{{X<{X f1}> f1} f1}","{X<X|null> f1}"); }
	@Test public void test_1526() { checkNotSubtype("{{X<{X f1}> f1} f1}","{X<X|null> f2}"); }
	@Test public void test_1527() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1528() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1529() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1530() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1531() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1532() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1533() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1534() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1535() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1536() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1537() { checkNotSubtype("{{X<{X f1}> f1} f1}","null|null"); }
	@Test public void test_1538() { checkNotSubtype("{{X<{X f1}> f1} f1}","null|X<{X f1}>"); }
	@Test public void test_1539() { checkNotSubtype("{{X<{X f1}> f1} f1}","null|X<{X f2}>"); }
	@Test public void test_1540() { checkNotSubtype("{{X<{X f1}> f1} f1}","null|{null f1}"); }
	@Test public void test_1541() { checkNotSubtype("{{X<{X f1}> f1} f1}","null|{null f2}"); }
	@Test public void test_1542() { checkNotSubtype("{{X<{X f1}> f1} f1}","null|(X<null|X>)"); }
	@Test public void test_1543() { checkNotSubtype("{{X<{X f1}> f1} f1}","{null f1}|null"); }
	@Test public void test_1544() { checkNotSubtype("{{X<{X f1}> f1} f1}","{null f2}|null"); }
	@Test public void test_1545() { checkNotSubtype("{{X<{X f1}> f1} f1}","X<{X f1}>|null"); }
	@Test public void test_1546() { checkNotSubtype("{{X<{X f1}> f1} f1}","X<{X f2}>|null"); }
	@Test public void test_1547() { checkNotSubtype("{{X<{X f1}> f1} f1}","X<X|{null f1}>"); }
	@Test public void test_1548() { checkNotSubtype("{{X<{X f1}> f1} f1}","X<X|{null f2}>"); }
	@Test public void test_1549() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1550() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1551() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1552() { checkIsSubtype("{{X<{X f1}> f1} f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1553() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1554() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1555() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1556() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1557() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1558() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1559() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1560() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1561() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1562() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1563() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1564() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1565() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_1566() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_1567() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_1568() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_1569() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1570() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1571() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1572() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1573() { checkNotSubtype("{{X<{X f1}> f1} f1}","(X<X|null>)|null"); }
	@Test public void test_1574() { checkNotSubtype("{{X<{X f1}> f1} f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_1575() { checkIsSubtype("{{X<{X f1}> f1} f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1576() { checkIsSubtype("{{X<{X f1}> f1} f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1577() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1578() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1579() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1580() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1581() { checkIsSubtype("{{X<{X f1}> f1} f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1582() { checkNotSubtype("{{X<{X f2}> f1} f1}","null"); }
	@Test public void test_1583() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f1}>"); }
	@Test public void test_1584() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f2}>"); }
	@Test public void test_1585() { checkNotSubtype("{{X<{X f2}> f1} f1}","{null f1}"); }
	@Test public void test_1586() { checkNotSubtype("{{X<{X f2}> f1} f1}","{null f2}"); }
	@Test public void test_1587() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<{X f1}> f1}"); }
	@Test public void test_1588() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<{X f2}> f1}"); }
	@Test public void test_1589() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<{X f1}> f2}"); }
	@Test public void test_1590() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<{X f2}> f2}"); }
	@Test public void test_1591() { checkNotSubtype("{{X<{X f2}> f1} f1}","X<X|null>"); }
	@Test public void test_1592() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1593() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1594() { checkNotSubtype("{{X<{X f2}> f1} f1}","{{null f1} f1}"); }
	@Test public void test_1595() { checkNotSubtype("{{X<{X f2}> f1} f1}","{{null f2} f1}"); }
	@Test public void test_1596() { checkNotSubtype("{{X<{X f2}> f1} f1}","{{null f1} f2}"); }
	@Test public void test_1597() { checkNotSubtype("{{X<{X f2}> f1} f1}","{{null f2} f2}"); }
	@Test public void test_1598() { checkIsSubtype("{{X<{X f2}> f1} f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1599() { checkIsSubtype("{{X<{X f2}> f1} f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1600() { checkIsSubtype("{{X<{X f2}> f1} f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1601() { checkIsSubtype("{{X<{X f2}> f1} f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1602() { checkIsSubtype("{{X<{X f2}> f1} f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1603() { checkIsSubtype("{{X<{X f2}> f1} f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1604() { checkIsSubtype("{{X<{X f2}> f1} f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1605() { checkIsSubtype("{{X<{X f2}> f1} f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1606() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1607() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1608() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1609() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1610() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1611() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1612() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1613() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1614() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1615() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1616() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1617() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1618() { checkNotSubtype("{{X<{X f2}> f1} f1}","{X<X|null> f1}"); }
	@Test public void test_1619() { checkNotSubtype("{{X<{X f2}> f1} f1}","{X<X|null> f2}"); }
	@Test public void test_1620() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1621() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1622() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1623() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1624() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1625() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1626() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1627() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1628() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1629() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1630() { checkNotSubtype("{{X<{X f2}> f1} f1}","null|null"); }
	@Test public void test_1631() { checkNotSubtype("{{X<{X f2}> f1} f1}","null|X<{X f1}>"); }
	@Test public void test_1632() { checkNotSubtype("{{X<{X f2}> f1} f1}","null|X<{X f2}>"); }
	@Test public void test_1633() { checkNotSubtype("{{X<{X f2}> f1} f1}","null|{null f1}"); }
	@Test public void test_1634() { checkNotSubtype("{{X<{X f2}> f1} f1}","null|{null f2}"); }
	@Test public void test_1635() { checkNotSubtype("{{X<{X f2}> f1} f1}","null|(X<null|X>)"); }
	@Test public void test_1636() { checkNotSubtype("{{X<{X f2}> f1} f1}","{null f1}|null"); }
	@Test public void test_1637() { checkNotSubtype("{{X<{X f2}> f1} f1}","{null f2}|null"); }
	@Test public void test_1638() { checkNotSubtype("{{X<{X f2}> f1} f1}","X<{X f1}>|null"); }
	@Test public void test_1639() { checkNotSubtype("{{X<{X f2}> f1} f1}","X<{X f2}>|null"); }
	@Test public void test_1640() { checkNotSubtype("{{X<{X f2}> f1} f1}","X<X|{null f1}>"); }
	@Test public void test_1641() { checkNotSubtype("{{X<{X f2}> f1} f1}","X<X|{null f2}>"); }
	@Test public void test_1642() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1643() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1644() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1645() { checkIsSubtype("{{X<{X f2}> f1} f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1646() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1647() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1648() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1649() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1650() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1651() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1652() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1653() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1654() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1655() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1656() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1657() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1658() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_1659() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_1660() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_1661() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_1662() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1663() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1664() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1665() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1666() { checkNotSubtype("{{X<{X f2}> f1} f1}","(X<X|null>)|null"); }
	@Test public void test_1667() { checkNotSubtype("{{X<{X f2}> f1} f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_1668() { checkIsSubtype("{{X<{X f2}> f1} f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1669() { checkIsSubtype("{{X<{X f2}> f1} f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1670() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1671() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1672() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1673() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1674() { checkIsSubtype("{{X<{X f2}> f1} f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1675() { checkNotSubtype("{{X<{X f1}> f2} f1}","null"); }
	@Test public void test_1676() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f1}>"); }
	@Test public void test_1677() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f2}>"); }
	@Test public void test_1678() { checkNotSubtype("{{X<{X f1}> f2} f1}","{null f1}"); }
	@Test public void test_1679() { checkNotSubtype("{{X<{X f1}> f2} f1}","{null f2}"); }
	@Test public void test_1680() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<{X f1}> f1}"); }
	@Test public void test_1681() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<{X f2}> f1}"); }
	@Test public void test_1682() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<{X f1}> f2}"); }
	@Test public void test_1683() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<{X f2}> f2}"); }
	@Test public void test_1684() { checkNotSubtype("{{X<{X f1}> f2} f1}","X<X|null>"); }
	@Test public void test_1685() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1686() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1687() { checkNotSubtype("{{X<{X f1}> f2} f1}","{{null f1} f1}"); }
	@Test public void test_1688() { checkNotSubtype("{{X<{X f1}> f2} f1}","{{null f2} f1}"); }
	@Test public void test_1689() { checkNotSubtype("{{X<{X f1}> f2} f1}","{{null f1} f2}"); }
	@Test public void test_1690() { checkNotSubtype("{{X<{X f1}> f2} f1}","{{null f2} f2}"); }
	@Test public void test_1691() { checkIsSubtype("{{X<{X f1}> f2} f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1692() { checkIsSubtype("{{X<{X f1}> f2} f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1693() { checkIsSubtype("{{X<{X f1}> f2} f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1694() { checkIsSubtype("{{X<{X f1}> f2} f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1695() { checkIsSubtype("{{X<{X f1}> f2} f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1696() { checkIsSubtype("{{X<{X f1}> f2} f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1697() { checkIsSubtype("{{X<{X f1}> f2} f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1698() { checkIsSubtype("{{X<{X f1}> f2} f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1699() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1700() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1701() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1702() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1703() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1704() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1705() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1706() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1707() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1708() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1709() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1710() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1711() { checkNotSubtype("{{X<{X f1}> f2} f1}","{X<X|null> f1}"); }
	@Test public void test_1712() { checkNotSubtype("{{X<{X f1}> f2} f1}","{X<X|null> f2}"); }
	@Test public void test_1713() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1714() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1715() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1716() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1717() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1718() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1719() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1720() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1721() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1722() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1723() { checkNotSubtype("{{X<{X f1}> f2} f1}","null|null"); }
	@Test public void test_1724() { checkNotSubtype("{{X<{X f1}> f2} f1}","null|X<{X f1}>"); }
	@Test public void test_1725() { checkNotSubtype("{{X<{X f1}> f2} f1}","null|X<{X f2}>"); }
	@Test public void test_1726() { checkNotSubtype("{{X<{X f1}> f2} f1}","null|{null f1}"); }
	@Test public void test_1727() { checkNotSubtype("{{X<{X f1}> f2} f1}","null|{null f2}"); }
	@Test public void test_1728() { checkNotSubtype("{{X<{X f1}> f2} f1}","null|(X<null|X>)"); }
	@Test public void test_1729() { checkNotSubtype("{{X<{X f1}> f2} f1}","{null f1}|null"); }
	@Test public void test_1730() { checkNotSubtype("{{X<{X f1}> f2} f1}","{null f2}|null"); }
	@Test public void test_1731() { checkNotSubtype("{{X<{X f1}> f2} f1}","X<{X f1}>|null"); }
	@Test public void test_1732() { checkNotSubtype("{{X<{X f1}> f2} f1}","X<{X f2}>|null"); }
	@Test public void test_1733() { checkNotSubtype("{{X<{X f1}> f2} f1}","X<X|{null f1}>"); }
	@Test public void test_1734() { checkNotSubtype("{{X<{X f1}> f2} f1}","X<X|{null f2}>"); }
	@Test public void test_1735() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1736() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1737() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1738() { checkIsSubtype("{{X<{X f1}> f2} f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1739() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1740() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1741() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1742() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1743() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1744() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1745() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1746() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1747() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1748() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1749() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1750() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1751() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_1752() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_1753() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_1754() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_1755() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1756() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1757() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1758() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1759() { checkNotSubtype("{{X<{X f1}> f2} f1}","(X<X|null>)|null"); }
	@Test public void test_1760() { checkNotSubtype("{{X<{X f1}> f2} f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_1761() { checkIsSubtype("{{X<{X f1}> f2} f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1762() { checkIsSubtype("{{X<{X f1}> f2} f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1763() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1764() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1765() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1766() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1767() { checkIsSubtype("{{X<{X f1}> f2} f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1768() { checkNotSubtype("{{X<{X f2}> f2} f1}","null"); }
	@Test public void test_1769() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f1}>"); }
	@Test public void test_1770() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f2}>"); }
	@Test public void test_1771() { checkNotSubtype("{{X<{X f2}> f2} f1}","{null f1}"); }
	@Test public void test_1772() { checkNotSubtype("{{X<{X f2}> f2} f1}","{null f2}"); }
	@Test public void test_1773() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<{X f1}> f1}"); }
	@Test public void test_1774() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<{X f2}> f1}"); }
	@Test public void test_1775() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<{X f1}> f2}"); }
	@Test public void test_1776() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<{X f2}> f2}"); }
	@Test public void test_1777() { checkNotSubtype("{{X<{X f2}> f2} f1}","X<X|null>"); }
	@Test public void test_1778() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1779() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1780() { checkNotSubtype("{{X<{X f2}> f2} f1}","{{null f1} f1}"); }
	@Test public void test_1781() { checkNotSubtype("{{X<{X f2}> f2} f1}","{{null f2} f1}"); }
	@Test public void test_1782() { checkNotSubtype("{{X<{X f2}> f2} f1}","{{null f1} f2}"); }
	@Test public void test_1783() { checkNotSubtype("{{X<{X f2}> f2} f1}","{{null f2} f2}"); }
	@Test public void test_1784() { checkIsSubtype("{{X<{X f2}> f2} f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1785() { checkIsSubtype("{{X<{X f2}> f2} f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1786() { checkIsSubtype("{{X<{X f2}> f2} f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1787() { checkIsSubtype("{{X<{X f2}> f2} f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1788() { checkIsSubtype("{{X<{X f2}> f2} f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1789() { checkIsSubtype("{{X<{X f2}> f2} f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1790() { checkIsSubtype("{{X<{X f2}> f2} f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1791() { checkIsSubtype("{{X<{X f2}> f2} f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1792() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1793() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1794() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1795() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1796() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1797() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1798() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1799() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1800() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1801() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1802() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1803() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1804() { checkNotSubtype("{{X<{X f2}> f2} f1}","{X<X|null> f1}"); }
	@Test public void test_1805() { checkNotSubtype("{{X<{X f2}> f2} f1}","{X<X|null> f2}"); }
	@Test public void test_1806() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1807() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1808() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1809() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1810() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1811() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1812() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1813() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1814() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1815() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1816() { checkNotSubtype("{{X<{X f2}> f2} f1}","null|null"); }
	@Test public void test_1817() { checkNotSubtype("{{X<{X f2}> f2} f1}","null|X<{X f1}>"); }
	@Test public void test_1818() { checkNotSubtype("{{X<{X f2}> f2} f1}","null|X<{X f2}>"); }
	@Test public void test_1819() { checkNotSubtype("{{X<{X f2}> f2} f1}","null|{null f1}"); }
	@Test public void test_1820() { checkNotSubtype("{{X<{X f2}> f2} f1}","null|{null f2}"); }
	@Test public void test_1821() { checkNotSubtype("{{X<{X f2}> f2} f1}","null|(X<null|X>)"); }
	@Test public void test_1822() { checkNotSubtype("{{X<{X f2}> f2} f1}","{null f1}|null"); }
	@Test public void test_1823() { checkNotSubtype("{{X<{X f2}> f2} f1}","{null f2}|null"); }
	@Test public void test_1824() { checkNotSubtype("{{X<{X f2}> f2} f1}","X<{X f1}>|null"); }
	@Test public void test_1825() { checkNotSubtype("{{X<{X f2}> f2} f1}","X<{X f2}>|null"); }
	@Test public void test_1826() { checkNotSubtype("{{X<{X f2}> f2} f1}","X<X|{null f1}>"); }
	@Test public void test_1827() { checkNotSubtype("{{X<{X f2}> f2} f1}","X<X|{null f2}>"); }
	@Test public void test_1828() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1829() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1830() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1831() { checkIsSubtype("{{X<{X f2}> f2} f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1832() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1833() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1834() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1835() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1836() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1837() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1838() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1839() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1840() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1841() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1842() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1843() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1844() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_1845() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_1846() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_1847() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_1848() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1849() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1850() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1851() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1852() { checkNotSubtype("{{X<{X f2}> f2} f1}","(X<X|null>)|null"); }
	@Test public void test_1853() { checkNotSubtype("{{X<{X f2}> f2} f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_1854() { checkIsSubtype("{{X<{X f2}> f2} f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1855() { checkIsSubtype("{{X<{X f2}> f2} f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1856() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1857() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1858() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1859() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1860() { checkIsSubtype("{{X<{X f2}> f2} f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1861() { checkNotSubtype("{{X<{X f1}> f1} f2}","null"); }
	@Test public void test_1862() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f1}>"); }
	@Test public void test_1863() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f2}>"); }
	@Test public void test_1864() { checkNotSubtype("{{X<{X f1}> f1} f2}","{null f1}"); }
	@Test public void test_1865() { checkNotSubtype("{{X<{X f1}> f1} f2}","{null f2}"); }
	@Test public void test_1866() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<{X f1}> f1}"); }
	@Test public void test_1867() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<{X f2}> f1}"); }
	@Test public void test_1868() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<{X f1}> f2}"); }
	@Test public void test_1869() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<{X f2}> f2}"); }
	@Test public void test_1870() { checkNotSubtype("{{X<{X f1}> f1} f2}","X<X|null>"); }
	@Test public void test_1871() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1872() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1873() { checkNotSubtype("{{X<{X f1}> f1} f2}","{{null f1} f1}"); }
	@Test public void test_1874() { checkNotSubtype("{{X<{X f1}> f1} f2}","{{null f2} f1}"); }
	@Test public void test_1875() { checkNotSubtype("{{X<{X f1}> f1} f2}","{{null f1} f2}"); }
	@Test public void test_1876() { checkNotSubtype("{{X<{X f1}> f1} f2}","{{null f2} f2}"); }
	@Test public void test_1877() { checkIsSubtype("{{X<{X f1}> f1} f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1878() { checkIsSubtype("{{X<{X f1}> f1} f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1879() { checkIsSubtype("{{X<{X f1}> f1} f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1880() { checkIsSubtype("{{X<{X f1}> f1} f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1881() { checkIsSubtype("{{X<{X f1}> f1} f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1882() { checkIsSubtype("{{X<{X f1}> f1} f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1883() { checkIsSubtype("{{X<{X f1}> f1} f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1884() { checkIsSubtype("{{X<{X f1}> f1} f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1885() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1886() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1887() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1888() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1889() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1890() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1891() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1892() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1893() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1894() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1895() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1896() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1897() { checkNotSubtype("{{X<{X f1}> f1} f2}","{X<X|null> f1}"); }
	@Test public void test_1898() { checkNotSubtype("{{X<{X f1}> f1} f2}","{X<X|null> f2}"); }
	@Test public void test_1899() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1900() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1901() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1902() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1903() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1904() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1905() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1906() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_1907() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_1908() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_1909() { checkNotSubtype("{{X<{X f1}> f1} f2}","null|null"); }
	@Test public void test_1910() { checkNotSubtype("{{X<{X f1}> f1} f2}","null|X<{X f1}>"); }
	@Test public void test_1911() { checkNotSubtype("{{X<{X f1}> f1} f2}","null|X<{X f2}>"); }
	@Test public void test_1912() { checkNotSubtype("{{X<{X f1}> f1} f2}","null|{null f1}"); }
	@Test public void test_1913() { checkNotSubtype("{{X<{X f1}> f1} f2}","null|{null f2}"); }
	@Test public void test_1914() { checkNotSubtype("{{X<{X f1}> f1} f2}","null|(X<null|X>)"); }
	@Test public void test_1915() { checkNotSubtype("{{X<{X f1}> f1} f2}","{null f1}|null"); }
	@Test public void test_1916() { checkNotSubtype("{{X<{X f1}> f1} f2}","{null f2}|null"); }
	@Test public void test_1917() { checkNotSubtype("{{X<{X f1}> f1} f2}","X<{X f1}>|null"); }
	@Test public void test_1918() { checkNotSubtype("{{X<{X f1}> f1} f2}","X<{X f2}>|null"); }
	@Test public void test_1919() { checkNotSubtype("{{X<{X f1}> f1} f2}","X<X|{null f1}>"); }
	@Test public void test_1920() { checkNotSubtype("{{X<{X f1}> f1} f2}","X<X|{null f2}>"); }
	@Test public void test_1921() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_1922() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_1923() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_1924() { checkIsSubtype("{{X<{X f1}> f1} f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_1925() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_1926() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_1927() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_1928() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_1929() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_1930() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_1931() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_1932() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_1933() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_1934() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_1935() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_1936() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_1937() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_1938() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_1939() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_1940() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_1941() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_1942() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_1943() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_1944() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_1945() { checkNotSubtype("{{X<{X f1}> f1} f2}","(X<X|null>)|null"); }
	@Test public void test_1946() { checkNotSubtype("{{X<{X f1}> f1} f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_1947() { checkIsSubtype("{{X<{X f1}> f1} f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_1948() { checkIsSubtype("{{X<{X f1}> f1} f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_1949() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_1950() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_1951() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_1952() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_1953() { checkIsSubtype("{{X<{X f1}> f1} f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_1954() { checkNotSubtype("{{X<{X f2}> f1} f2}","null"); }
	@Test public void test_1955() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f1}>"); }
	@Test public void test_1956() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f2}>"); }
	@Test public void test_1957() { checkNotSubtype("{{X<{X f2}> f1} f2}","{null f1}"); }
	@Test public void test_1958() { checkNotSubtype("{{X<{X f2}> f1} f2}","{null f2}"); }
	@Test public void test_1959() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<{X f1}> f1}"); }
	@Test public void test_1960() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<{X f2}> f1}"); }
	@Test public void test_1961() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<{X f1}> f2}"); }
	@Test public void test_1962() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<{X f2}> f2}"); }
	@Test public void test_1963() { checkNotSubtype("{{X<{X f2}> f1} f2}","X<X|null>"); }
	@Test public void test_1964() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_1965() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_1966() { checkNotSubtype("{{X<{X f2}> f1} f2}","{{null f1} f1}"); }
	@Test public void test_1967() { checkNotSubtype("{{X<{X f2}> f1} f2}","{{null f2} f1}"); }
	@Test public void test_1968() { checkNotSubtype("{{X<{X f2}> f1} f2}","{{null f1} f2}"); }
	@Test public void test_1969() { checkNotSubtype("{{X<{X f2}> f1} f2}","{{null f2} f2}"); }
	@Test public void test_1970() { checkIsSubtype("{{X<{X f2}> f1} f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_1971() { checkIsSubtype("{{X<{X f2}> f1} f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_1972() { checkIsSubtype("{{X<{X f2}> f1} f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_1973() { checkIsSubtype("{{X<{X f2}> f1} f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_1974() { checkIsSubtype("{{X<{X f2}> f1} f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_1975() { checkIsSubtype("{{X<{X f2}> f1} f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_1976() { checkIsSubtype("{{X<{X f2}> f1} f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_1977() { checkIsSubtype("{{X<{X f2}> f1} f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_1978() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_1979() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_1980() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_1981() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_1982() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_1983() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_1984() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_1985() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_1986() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_1987() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_1988() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_1989() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_1990() { checkNotSubtype("{{X<{X f2}> f1} f2}","{X<X|null> f1}"); }
	@Test public void test_1991() { checkNotSubtype("{{X<{X f2}> f1} f2}","{X<X|null> f2}"); }
	@Test public void test_1992() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_1993() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_1994() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_1995() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_1996() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_1997() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_1998() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_1999() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2000() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2001() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2002() { checkNotSubtype("{{X<{X f2}> f1} f2}","null|null"); }
	@Test public void test_2003() { checkNotSubtype("{{X<{X f2}> f1} f2}","null|X<{X f1}>"); }
	@Test public void test_2004() { checkNotSubtype("{{X<{X f2}> f1} f2}","null|X<{X f2}>"); }
	@Test public void test_2005() { checkNotSubtype("{{X<{X f2}> f1} f2}","null|{null f1}"); }
	@Test public void test_2006() { checkNotSubtype("{{X<{X f2}> f1} f2}","null|{null f2}"); }
	@Test public void test_2007() { checkNotSubtype("{{X<{X f2}> f1} f2}","null|(X<null|X>)"); }
	@Test public void test_2008() { checkNotSubtype("{{X<{X f2}> f1} f2}","{null f1}|null"); }
	@Test public void test_2009() { checkNotSubtype("{{X<{X f2}> f1} f2}","{null f2}|null"); }
	@Test public void test_2010() { checkNotSubtype("{{X<{X f2}> f1} f2}","X<{X f1}>|null"); }
	@Test public void test_2011() { checkNotSubtype("{{X<{X f2}> f1} f2}","X<{X f2}>|null"); }
	@Test public void test_2012() { checkNotSubtype("{{X<{X f2}> f1} f2}","X<X|{null f1}>"); }
	@Test public void test_2013() { checkNotSubtype("{{X<{X f2}> f1} f2}","X<X|{null f2}>"); }
	@Test public void test_2014() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2015() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2016() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2017() { checkIsSubtype("{{X<{X f2}> f1} f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2018() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2019() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2020() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2021() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2022() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2023() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2024() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2025() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2026() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2027() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2028() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2029() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2030() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_2031() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_2032() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_2033() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_2034() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2035() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2036() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2037() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2038() { checkNotSubtype("{{X<{X f2}> f1} f2}","(X<X|null>)|null"); }
	@Test public void test_2039() { checkNotSubtype("{{X<{X f2}> f1} f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_2040() { checkIsSubtype("{{X<{X f2}> f1} f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2041() { checkIsSubtype("{{X<{X f2}> f1} f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2042() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2043() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2044() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2045() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2046() { checkIsSubtype("{{X<{X f2}> f1} f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2047() { checkNotSubtype("{{X<{X f1}> f2} f2}","null"); }
	@Test public void test_2048() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f1}>"); }
	@Test public void test_2049() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f2}>"); }
	@Test public void test_2050() { checkNotSubtype("{{X<{X f1}> f2} f2}","{null f1}"); }
	@Test public void test_2051() { checkNotSubtype("{{X<{X f1}> f2} f2}","{null f2}"); }
	@Test public void test_2052() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<{X f1}> f1}"); }
	@Test public void test_2053() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<{X f2}> f1}"); }
	@Test public void test_2054() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<{X f1}> f2}"); }
	@Test public void test_2055() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<{X f2}> f2}"); }
	@Test public void test_2056() { checkNotSubtype("{{X<{X f1}> f2} f2}","X<X|null>"); }
	@Test public void test_2057() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_2058() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_2059() { checkNotSubtype("{{X<{X f1}> f2} f2}","{{null f1} f1}"); }
	@Test public void test_2060() { checkNotSubtype("{{X<{X f1}> f2} f2}","{{null f2} f1}"); }
	@Test public void test_2061() { checkNotSubtype("{{X<{X f1}> f2} f2}","{{null f1} f2}"); }
	@Test public void test_2062() { checkNotSubtype("{{X<{X f1}> f2} f2}","{{null f2} f2}"); }
	@Test public void test_2063() { checkIsSubtype("{{X<{X f1}> f2} f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2064() { checkIsSubtype("{{X<{X f1}> f2} f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2065() { checkIsSubtype("{{X<{X f1}> f2} f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2066() { checkIsSubtype("{{X<{X f1}> f2} f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2067() { checkIsSubtype("{{X<{X f1}> f2} f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2068() { checkIsSubtype("{{X<{X f1}> f2} f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2069() { checkIsSubtype("{{X<{X f1}> f2} f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2070() { checkIsSubtype("{{X<{X f1}> f2} f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2071() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2072() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2073() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2074() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2075() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2076() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2077() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2078() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2079() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2080() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2081() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2082() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2083() { checkNotSubtype("{{X<{X f1}> f2} f2}","{X<X|null> f1}"); }
	@Test public void test_2084() { checkNotSubtype("{{X<{X f1}> f2} f2}","{X<X|null> f2}"); }
	@Test public void test_2085() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2086() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2087() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2088() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2089() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2090() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2091() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2092() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2093() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2094() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2095() { checkNotSubtype("{{X<{X f1}> f2} f2}","null|null"); }
	@Test public void test_2096() { checkNotSubtype("{{X<{X f1}> f2} f2}","null|X<{X f1}>"); }
	@Test public void test_2097() { checkNotSubtype("{{X<{X f1}> f2} f2}","null|X<{X f2}>"); }
	@Test public void test_2098() { checkNotSubtype("{{X<{X f1}> f2} f2}","null|{null f1}"); }
	@Test public void test_2099() { checkNotSubtype("{{X<{X f1}> f2} f2}","null|{null f2}"); }
	@Test public void test_2100() { checkNotSubtype("{{X<{X f1}> f2} f2}","null|(X<null|X>)"); }
	@Test public void test_2101() { checkNotSubtype("{{X<{X f1}> f2} f2}","{null f1}|null"); }
	@Test public void test_2102() { checkNotSubtype("{{X<{X f1}> f2} f2}","{null f2}|null"); }
	@Test public void test_2103() { checkNotSubtype("{{X<{X f1}> f2} f2}","X<{X f1}>|null"); }
	@Test public void test_2104() { checkNotSubtype("{{X<{X f1}> f2} f2}","X<{X f2}>|null"); }
	@Test public void test_2105() { checkNotSubtype("{{X<{X f1}> f2} f2}","X<X|{null f1}>"); }
	@Test public void test_2106() { checkNotSubtype("{{X<{X f1}> f2} f2}","X<X|{null f2}>"); }
	@Test public void test_2107() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2108() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2109() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2110() { checkIsSubtype("{{X<{X f1}> f2} f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2111() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2112() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2113() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2114() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2115() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2116() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2117() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2118() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2119() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2120() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2121() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2122() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2123() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_2124() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_2125() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_2126() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_2127() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2128() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2129() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2130() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2131() { checkNotSubtype("{{X<{X f1}> f2} f2}","(X<X|null>)|null"); }
	@Test public void test_2132() { checkNotSubtype("{{X<{X f1}> f2} f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_2133() { checkIsSubtype("{{X<{X f1}> f2} f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2134() { checkIsSubtype("{{X<{X f1}> f2} f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2135() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2136() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2137() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2138() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2139() { checkIsSubtype("{{X<{X f1}> f2} f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2140() { checkNotSubtype("{{X<{X f2}> f2} f2}","null"); }
	@Test public void test_2141() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f1}>"); }
	@Test public void test_2142() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f2}>"); }
	@Test public void test_2143() { checkNotSubtype("{{X<{X f2}> f2} f2}","{null f1}"); }
	@Test public void test_2144() { checkNotSubtype("{{X<{X f2}> f2} f2}","{null f2}"); }
	@Test public void test_2145() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<{X f1}> f1}"); }
	@Test public void test_2146() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<{X f2}> f1}"); }
	@Test public void test_2147() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<{X f1}> f2}"); }
	@Test public void test_2148() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<{X f2}> f2}"); }
	@Test public void test_2149() { checkNotSubtype("{{X<{X f2}> f2} f2}","X<X|null>"); }
	@Test public void test_2150() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_2151() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_2152() { checkNotSubtype("{{X<{X f2}> f2} f2}","{{null f1} f1}"); }
	@Test public void test_2153() { checkNotSubtype("{{X<{X f2}> f2} f2}","{{null f2} f1}"); }
	@Test public void test_2154() { checkNotSubtype("{{X<{X f2}> f2} f2}","{{null f1} f2}"); }
	@Test public void test_2155() { checkNotSubtype("{{X<{X f2}> f2} f2}","{{null f2} f2}"); }
	@Test public void test_2156() { checkIsSubtype("{{X<{X f2}> f2} f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2157() { checkIsSubtype("{{X<{X f2}> f2} f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2158() { checkIsSubtype("{{X<{X f2}> f2} f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2159() { checkIsSubtype("{{X<{X f2}> f2} f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2160() { checkIsSubtype("{{X<{X f2}> f2} f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2161() { checkIsSubtype("{{X<{X f2}> f2} f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2162() { checkIsSubtype("{{X<{X f2}> f2} f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2163() { checkIsSubtype("{{X<{X f2}> f2} f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2164() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2165() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2166() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2167() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2168() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2169() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2170() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2171() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2172() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2173() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2174() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2175() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2176() { checkNotSubtype("{{X<{X f2}> f2} f2}","{X<X|null> f1}"); }
	@Test public void test_2177() { checkNotSubtype("{{X<{X f2}> f2} f2}","{X<X|null> f2}"); }
	@Test public void test_2178() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2179() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2180() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2181() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2182() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2183() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2184() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2185() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2186() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2187() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2188() { checkNotSubtype("{{X<{X f2}> f2} f2}","null|null"); }
	@Test public void test_2189() { checkNotSubtype("{{X<{X f2}> f2} f2}","null|X<{X f1}>"); }
	@Test public void test_2190() { checkNotSubtype("{{X<{X f2}> f2} f2}","null|X<{X f2}>"); }
	@Test public void test_2191() { checkNotSubtype("{{X<{X f2}> f2} f2}","null|{null f1}"); }
	@Test public void test_2192() { checkNotSubtype("{{X<{X f2}> f2} f2}","null|{null f2}"); }
	@Test public void test_2193() { checkNotSubtype("{{X<{X f2}> f2} f2}","null|(X<null|X>)"); }
	@Test public void test_2194() { checkNotSubtype("{{X<{X f2}> f2} f2}","{null f1}|null"); }
	@Test public void test_2195() { checkNotSubtype("{{X<{X f2}> f2} f2}","{null f2}|null"); }
	@Test public void test_2196() { checkNotSubtype("{{X<{X f2}> f2} f2}","X<{X f1}>|null"); }
	@Test public void test_2197() { checkNotSubtype("{{X<{X f2}> f2} f2}","X<{X f2}>|null"); }
	@Test public void test_2198() { checkNotSubtype("{{X<{X f2}> f2} f2}","X<X|{null f1}>"); }
	@Test public void test_2199() { checkNotSubtype("{{X<{X f2}> f2} f2}","X<X|{null f2}>"); }
	@Test public void test_2200() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2201() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2202() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2203() { checkIsSubtype("{{X<{X f2}> f2} f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2204() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2205() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2206() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2207() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2208() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2209() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2210() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2211() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2212() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2213() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2214() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2215() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2216() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_2217() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_2218() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_2219() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_2220() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2221() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2222() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2223() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2224() { checkNotSubtype("{{X<{X f2}> f2} f2}","(X<X|null>)|null"); }
	@Test public void test_2225() { checkNotSubtype("{{X<{X f2}> f2} f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_2226() { checkIsSubtype("{{X<{X f2}> f2} f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2227() { checkIsSubtype("{{X<{X f2}> f2} f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2228() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2229() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2230() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2231() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2232() { checkIsSubtype("{{X<{X f2}> f2} f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2233() { checkNotSubtype("X<{{{X f1} f1} f1}>","null"); }
	@Test public void test_2234() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f1}>"); }
	@Test public void test_2235() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f2}>"); }
	@Test public void test_2236() { checkNotSubtype("X<{{{X f1} f1} f1}>","{null f1}"); }
	@Test public void test_2237() { checkNotSubtype("X<{{{X f1} f1} f1}>","{null f2}"); }
	@Test public void test_2238() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<{X f1}> f1}"); }
	@Test public void test_2239() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<{X f2}> f1}"); }
	@Test public void test_2240() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<{X f1}> f2}"); }
	@Test public void test_2241() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<{X f2}> f2}"); }
	@Test public void test_2242() { checkNotSubtype("X<{{{X f1} f1} f1}>","X<X|null>"); }
	@Test public void test_2243() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_2244() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_2245() { checkNotSubtype("X<{{{X f1} f1} f1}>","{{null f1} f1}"); }
	@Test public void test_2246() { checkNotSubtype("X<{{{X f1} f1} f1}>","{{null f2} f1}"); }
	@Test public void test_2247() { checkNotSubtype("X<{{{X f1} f1} f1}>","{{null f1} f2}"); }
	@Test public void test_2248() { checkNotSubtype("X<{{{X f1} f1} f1}>","{{null f2} f2}"); }
	@Test public void test_2249() { checkIsSubtype("X<{{{X f1} f1} f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2250() { checkIsSubtype("X<{{{X f1} f1} f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2251() { checkIsSubtype("X<{{{X f1} f1} f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2252() { checkIsSubtype("X<{{{X f1} f1} f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2253() { checkIsSubtype("X<{{{X f1} f1} f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2254() { checkIsSubtype("X<{{{X f1} f1} f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2255() { checkIsSubtype("X<{{{X f1} f1} f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2256() { checkIsSubtype("X<{{{X f1} f1} f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2257() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2258() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2259() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2260() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2261() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2262() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2263() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2264() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2265() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2266() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2267() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2268() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2269() { checkNotSubtype("X<{{{X f1} f1} f1}>","{X<X|null> f1}"); }
	@Test public void test_2270() { checkNotSubtype("X<{{{X f1} f1} f1}>","{X<X|null> f2}"); }
	@Test public void test_2271() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2272() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2273() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2274() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2275() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2276() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2277() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2278() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2279() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2280() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2281() { checkNotSubtype("X<{{{X f1} f1} f1}>","null|null"); }
	@Test public void test_2282() { checkNotSubtype("X<{{{X f1} f1} f1}>","null|X<{X f1}>"); }
	@Test public void test_2283() { checkNotSubtype("X<{{{X f1} f1} f1}>","null|X<{X f2}>"); }
	@Test public void test_2284() { checkNotSubtype("X<{{{X f1} f1} f1}>","null|{null f1}"); }
	@Test public void test_2285() { checkNotSubtype("X<{{{X f1} f1} f1}>","null|{null f2}"); }
	@Test public void test_2286() { checkNotSubtype("X<{{{X f1} f1} f1}>","null|(X<null|X>)"); }
	@Test public void test_2287() { checkNotSubtype("X<{{{X f1} f1} f1}>","{null f1}|null"); }
	@Test public void test_2288() { checkNotSubtype("X<{{{X f1} f1} f1}>","{null f2}|null"); }
	@Test public void test_2289() { checkNotSubtype("X<{{{X f1} f1} f1}>","X<{X f1}>|null"); }
	@Test public void test_2290() { checkNotSubtype("X<{{{X f1} f1} f1}>","X<{X f2}>|null"); }
	@Test public void test_2291() { checkNotSubtype("X<{{{X f1} f1} f1}>","X<X|{null f1}>"); }
	@Test public void test_2292() { checkNotSubtype("X<{{{X f1} f1} f1}>","X<X|{null f2}>"); }
	@Test public void test_2293() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2294() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2295() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2296() { checkIsSubtype("X<{{{X f1} f1} f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2297() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2298() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2299() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2300() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2301() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2302() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2303() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2304() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2305() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2306() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2307() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2308() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2309() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_2310() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_2311() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_2312() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_2313() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2314() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2315() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2316() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2317() { checkNotSubtype("X<{{{X f1} f1} f1}>","(X<X|null>)|null"); }
	@Test public void test_2318() { checkNotSubtype("X<{{{X f1} f1} f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_2319() { checkIsSubtype("X<{{{X f1} f1} f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2320() { checkIsSubtype("X<{{{X f1} f1} f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2321() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2322() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2323() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2324() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2325() { checkIsSubtype("X<{{{X f1} f1} f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2326() { checkNotSubtype("X<{{{X f2} f1} f1}>","null"); }
	@Test public void test_2327() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f1}>"); }
	@Test public void test_2328() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f2}>"); }
	@Test public void test_2329() { checkNotSubtype("X<{{{X f2} f1} f1}>","{null f1}"); }
	@Test public void test_2330() { checkNotSubtype("X<{{{X f2} f1} f1}>","{null f2}"); }
	@Test public void test_2331() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<{X f1}> f1}"); }
	@Test public void test_2332() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<{X f2}> f1}"); }
	@Test public void test_2333() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<{X f1}> f2}"); }
	@Test public void test_2334() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<{X f2}> f2}"); }
	@Test public void test_2335() { checkNotSubtype("X<{{{X f2} f1} f1}>","X<X|null>"); }
	@Test public void test_2336() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_2337() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_2338() { checkNotSubtype("X<{{{X f2} f1} f1}>","{{null f1} f1}"); }
	@Test public void test_2339() { checkNotSubtype("X<{{{X f2} f1} f1}>","{{null f2} f1}"); }
	@Test public void test_2340() { checkNotSubtype("X<{{{X f2} f1} f1}>","{{null f1} f2}"); }
	@Test public void test_2341() { checkNotSubtype("X<{{{X f2} f1} f1}>","{{null f2} f2}"); }
	@Test public void test_2342() { checkIsSubtype("X<{{{X f2} f1} f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2343() { checkIsSubtype("X<{{{X f2} f1} f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2344() { checkIsSubtype("X<{{{X f2} f1} f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2345() { checkIsSubtype("X<{{{X f2} f1} f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2346() { checkIsSubtype("X<{{{X f2} f1} f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2347() { checkIsSubtype("X<{{{X f2} f1} f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2348() { checkIsSubtype("X<{{{X f2} f1} f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2349() { checkIsSubtype("X<{{{X f2} f1} f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2350() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2351() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2352() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2353() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2354() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2355() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2356() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2357() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2358() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2359() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2360() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2361() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2362() { checkNotSubtype("X<{{{X f2} f1} f1}>","{X<X|null> f1}"); }
	@Test public void test_2363() { checkNotSubtype("X<{{{X f2} f1} f1}>","{X<X|null> f2}"); }
	@Test public void test_2364() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2365() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2366() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2367() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2368() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2369() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2370() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2371() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2372() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2373() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2374() { checkNotSubtype("X<{{{X f2} f1} f1}>","null|null"); }
	@Test public void test_2375() { checkNotSubtype("X<{{{X f2} f1} f1}>","null|X<{X f1}>"); }
	@Test public void test_2376() { checkNotSubtype("X<{{{X f2} f1} f1}>","null|X<{X f2}>"); }
	@Test public void test_2377() { checkNotSubtype("X<{{{X f2} f1} f1}>","null|{null f1}"); }
	@Test public void test_2378() { checkNotSubtype("X<{{{X f2} f1} f1}>","null|{null f2}"); }
	@Test public void test_2379() { checkNotSubtype("X<{{{X f2} f1} f1}>","null|(X<null|X>)"); }
	@Test public void test_2380() { checkNotSubtype("X<{{{X f2} f1} f1}>","{null f1}|null"); }
	@Test public void test_2381() { checkNotSubtype("X<{{{X f2} f1} f1}>","{null f2}|null"); }
	@Test public void test_2382() { checkNotSubtype("X<{{{X f2} f1} f1}>","X<{X f1}>|null"); }
	@Test public void test_2383() { checkNotSubtype("X<{{{X f2} f1} f1}>","X<{X f2}>|null"); }
	@Test public void test_2384() { checkNotSubtype("X<{{{X f2} f1} f1}>","X<X|{null f1}>"); }
	@Test public void test_2385() { checkNotSubtype("X<{{{X f2} f1} f1}>","X<X|{null f2}>"); }
	@Test public void test_2386() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2387() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2388() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2389() { checkIsSubtype("X<{{{X f2} f1} f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2390() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2391() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2392() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2393() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2394() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2395() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2396() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2397() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2398() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2399() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2400() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2401() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2402() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_2403() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_2404() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_2405() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_2406() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2407() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2408() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2409() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2410() { checkNotSubtype("X<{{{X f2} f1} f1}>","(X<X|null>)|null"); }
	@Test public void test_2411() { checkNotSubtype("X<{{{X f2} f1} f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_2412() { checkIsSubtype("X<{{{X f2} f1} f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2413() { checkIsSubtype("X<{{{X f2} f1} f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2414() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2415() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2416() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2417() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2418() { checkIsSubtype("X<{{{X f2} f1} f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2419() { checkNotSubtype("X<{{{X f1} f2} f1}>","null"); }
	@Test public void test_2420() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f1}>"); }
	@Test public void test_2421() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f2}>"); }
	@Test public void test_2422() { checkNotSubtype("X<{{{X f1} f2} f1}>","{null f1}"); }
	@Test public void test_2423() { checkNotSubtype("X<{{{X f1} f2} f1}>","{null f2}"); }
	@Test public void test_2424() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<{X f1}> f1}"); }
	@Test public void test_2425() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<{X f2}> f1}"); }
	@Test public void test_2426() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<{X f1}> f2}"); }
	@Test public void test_2427() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<{X f2}> f2}"); }
	@Test public void test_2428() { checkNotSubtype("X<{{{X f1} f2} f1}>","X<X|null>"); }
	@Test public void test_2429() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_2430() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_2431() { checkNotSubtype("X<{{{X f1} f2} f1}>","{{null f1} f1}"); }
	@Test public void test_2432() { checkNotSubtype("X<{{{X f1} f2} f1}>","{{null f2} f1}"); }
	@Test public void test_2433() { checkNotSubtype("X<{{{X f1} f2} f1}>","{{null f1} f2}"); }
	@Test public void test_2434() { checkNotSubtype("X<{{{X f1} f2} f1}>","{{null f2} f2}"); }
	@Test public void test_2435() { checkIsSubtype("X<{{{X f1} f2} f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2436() { checkIsSubtype("X<{{{X f1} f2} f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2437() { checkIsSubtype("X<{{{X f1} f2} f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2438() { checkIsSubtype("X<{{{X f1} f2} f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2439() { checkIsSubtype("X<{{{X f1} f2} f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2440() { checkIsSubtype("X<{{{X f1} f2} f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2441() { checkIsSubtype("X<{{{X f1} f2} f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2442() { checkIsSubtype("X<{{{X f1} f2} f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2443() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2444() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2445() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2446() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2447() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2448() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2449() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2450() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2451() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2452() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2453() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2454() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2455() { checkNotSubtype("X<{{{X f1} f2} f1}>","{X<X|null> f1}"); }
	@Test public void test_2456() { checkNotSubtype("X<{{{X f1} f2} f1}>","{X<X|null> f2}"); }
	@Test public void test_2457() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2458() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2459() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2460() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2461() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2462() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2463() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2464() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2465() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2466() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2467() { checkNotSubtype("X<{{{X f1} f2} f1}>","null|null"); }
	@Test public void test_2468() { checkNotSubtype("X<{{{X f1} f2} f1}>","null|X<{X f1}>"); }
	@Test public void test_2469() { checkNotSubtype("X<{{{X f1} f2} f1}>","null|X<{X f2}>"); }
	@Test public void test_2470() { checkNotSubtype("X<{{{X f1} f2} f1}>","null|{null f1}"); }
	@Test public void test_2471() { checkNotSubtype("X<{{{X f1} f2} f1}>","null|{null f2}"); }
	@Test public void test_2472() { checkNotSubtype("X<{{{X f1} f2} f1}>","null|(X<null|X>)"); }
	@Test public void test_2473() { checkNotSubtype("X<{{{X f1} f2} f1}>","{null f1}|null"); }
	@Test public void test_2474() { checkNotSubtype("X<{{{X f1} f2} f1}>","{null f2}|null"); }
	@Test public void test_2475() { checkNotSubtype("X<{{{X f1} f2} f1}>","X<{X f1}>|null"); }
	@Test public void test_2476() { checkNotSubtype("X<{{{X f1} f2} f1}>","X<{X f2}>|null"); }
	@Test public void test_2477() { checkNotSubtype("X<{{{X f1} f2} f1}>","X<X|{null f1}>"); }
	@Test public void test_2478() { checkNotSubtype("X<{{{X f1} f2} f1}>","X<X|{null f2}>"); }
	@Test public void test_2479() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2480() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2481() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2482() { checkIsSubtype("X<{{{X f1} f2} f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2483() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2484() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2485() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2486() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2487() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2488() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2489() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2490() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2491() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2492() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2493() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2494() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2495() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_2496() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_2497() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_2498() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_2499() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2500() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2501() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2502() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2503() { checkNotSubtype("X<{{{X f1} f2} f1}>","(X<X|null>)|null"); }
	@Test public void test_2504() { checkNotSubtype("X<{{{X f1} f2} f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_2505() { checkIsSubtype("X<{{{X f1} f2} f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2506() { checkIsSubtype("X<{{{X f1} f2} f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2507() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2508() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2509() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2510() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2511() { checkIsSubtype("X<{{{X f1} f2} f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2512() { checkNotSubtype("X<{{{X f2} f2} f1}>","null"); }
	@Test public void test_2513() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f1}>"); }
	@Test public void test_2514() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f2}>"); }
	@Test public void test_2515() { checkNotSubtype("X<{{{X f2} f2} f1}>","{null f1}"); }
	@Test public void test_2516() { checkNotSubtype("X<{{{X f2} f2} f1}>","{null f2}"); }
	@Test public void test_2517() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<{X f1}> f1}"); }
	@Test public void test_2518() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<{X f2}> f1}"); }
	@Test public void test_2519() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<{X f1}> f2}"); }
	@Test public void test_2520() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<{X f2}> f2}"); }
	@Test public void test_2521() { checkNotSubtype("X<{{{X f2} f2} f1}>","X<X|null>"); }
	@Test public void test_2522() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_2523() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_2524() { checkNotSubtype("X<{{{X f2} f2} f1}>","{{null f1} f1}"); }
	@Test public void test_2525() { checkNotSubtype("X<{{{X f2} f2} f1}>","{{null f2} f1}"); }
	@Test public void test_2526() { checkNotSubtype("X<{{{X f2} f2} f1}>","{{null f1} f2}"); }
	@Test public void test_2527() { checkNotSubtype("X<{{{X f2} f2} f1}>","{{null f2} f2}"); }
	@Test public void test_2528() { checkIsSubtype("X<{{{X f2} f2} f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2529() { checkIsSubtype("X<{{{X f2} f2} f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2530() { checkIsSubtype("X<{{{X f2} f2} f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2531() { checkIsSubtype("X<{{{X f2} f2} f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2532() { checkIsSubtype("X<{{{X f2} f2} f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2533() { checkIsSubtype("X<{{{X f2} f2} f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2534() { checkIsSubtype("X<{{{X f2} f2} f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2535() { checkIsSubtype("X<{{{X f2} f2} f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2536() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2537() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2538() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2539() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2540() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2541() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2542() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2543() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2544() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2545() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2546() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2547() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2548() { checkNotSubtype("X<{{{X f2} f2} f1}>","{X<X|null> f1}"); }
	@Test public void test_2549() { checkNotSubtype("X<{{{X f2} f2} f1}>","{X<X|null> f2}"); }
	@Test public void test_2550() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2551() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2552() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2553() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2554() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2555() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2556() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2557() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2558() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2559() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2560() { checkNotSubtype("X<{{{X f2} f2} f1}>","null|null"); }
	@Test public void test_2561() { checkNotSubtype("X<{{{X f2} f2} f1}>","null|X<{X f1}>"); }
	@Test public void test_2562() { checkNotSubtype("X<{{{X f2} f2} f1}>","null|X<{X f2}>"); }
	@Test public void test_2563() { checkNotSubtype("X<{{{X f2} f2} f1}>","null|{null f1}"); }
	@Test public void test_2564() { checkNotSubtype("X<{{{X f2} f2} f1}>","null|{null f2}"); }
	@Test public void test_2565() { checkNotSubtype("X<{{{X f2} f2} f1}>","null|(X<null|X>)"); }
	@Test public void test_2566() { checkNotSubtype("X<{{{X f2} f2} f1}>","{null f1}|null"); }
	@Test public void test_2567() { checkNotSubtype("X<{{{X f2} f2} f1}>","{null f2}|null"); }
	@Test public void test_2568() { checkNotSubtype("X<{{{X f2} f2} f1}>","X<{X f1}>|null"); }
	@Test public void test_2569() { checkNotSubtype("X<{{{X f2} f2} f1}>","X<{X f2}>|null"); }
	@Test public void test_2570() { checkNotSubtype("X<{{{X f2} f2} f1}>","X<X|{null f1}>"); }
	@Test public void test_2571() { checkNotSubtype("X<{{{X f2} f2} f1}>","X<X|{null f2}>"); }
	@Test public void test_2572() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2573() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2574() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2575() { checkIsSubtype("X<{{{X f2} f2} f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2576() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2577() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2578() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2579() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2580() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2581() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2582() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2583() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2584() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2585() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2586() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2587() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2588() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_2589() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_2590() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_2591() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_2592() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2593() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2594() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2595() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2596() { checkNotSubtype("X<{{{X f2} f2} f1}>","(X<X|null>)|null"); }
	@Test public void test_2597() { checkNotSubtype("X<{{{X f2} f2} f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_2598() { checkIsSubtype("X<{{{X f2} f2} f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2599() { checkIsSubtype("X<{{{X f2} f2} f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2600() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2601() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2602() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2603() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2604() { checkIsSubtype("X<{{{X f2} f2} f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2605() { checkNotSubtype("X<{{{X f1} f1} f2}>","null"); }
	@Test public void test_2606() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f1}>"); }
	@Test public void test_2607() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f2}>"); }
	@Test public void test_2608() { checkNotSubtype("X<{{{X f1} f1} f2}>","{null f1}"); }
	@Test public void test_2609() { checkNotSubtype("X<{{{X f1} f1} f2}>","{null f2}"); }
	@Test public void test_2610() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<{X f1}> f1}"); }
	@Test public void test_2611() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<{X f2}> f1}"); }
	@Test public void test_2612() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<{X f1}> f2}"); }
	@Test public void test_2613() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<{X f2}> f2}"); }
	@Test public void test_2614() { checkNotSubtype("X<{{{X f1} f1} f2}>","X<X|null>"); }
	@Test public void test_2615() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_2616() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_2617() { checkNotSubtype("X<{{{X f1} f1} f2}>","{{null f1} f1}"); }
	@Test public void test_2618() { checkNotSubtype("X<{{{X f1} f1} f2}>","{{null f2} f1}"); }
	@Test public void test_2619() { checkNotSubtype("X<{{{X f1} f1} f2}>","{{null f1} f2}"); }
	@Test public void test_2620() { checkNotSubtype("X<{{{X f1} f1} f2}>","{{null f2} f2}"); }
	@Test public void test_2621() { checkIsSubtype("X<{{{X f1} f1} f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2622() { checkIsSubtype("X<{{{X f1} f1} f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2623() { checkIsSubtype("X<{{{X f1} f1} f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2624() { checkIsSubtype("X<{{{X f1} f1} f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2625() { checkIsSubtype("X<{{{X f1} f1} f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2626() { checkIsSubtype("X<{{{X f1} f1} f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2627() { checkIsSubtype("X<{{{X f1} f1} f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2628() { checkIsSubtype("X<{{{X f1} f1} f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2629() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2630() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2631() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2632() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2633() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2634() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2635() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2636() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2637() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2638() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2639() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2640() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2641() { checkNotSubtype("X<{{{X f1} f1} f2}>","{X<X|null> f1}"); }
	@Test public void test_2642() { checkNotSubtype("X<{{{X f1} f1} f2}>","{X<X|null> f2}"); }
	@Test public void test_2643() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2644() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2645() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2646() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2647() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2648() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2649() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2650() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2651() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2652() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2653() { checkNotSubtype("X<{{{X f1} f1} f2}>","null|null"); }
	@Test public void test_2654() { checkNotSubtype("X<{{{X f1} f1} f2}>","null|X<{X f1}>"); }
	@Test public void test_2655() { checkNotSubtype("X<{{{X f1} f1} f2}>","null|X<{X f2}>"); }
	@Test public void test_2656() { checkNotSubtype("X<{{{X f1} f1} f2}>","null|{null f1}"); }
	@Test public void test_2657() { checkNotSubtype("X<{{{X f1} f1} f2}>","null|{null f2}"); }
	@Test public void test_2658() { checkNotSubtype("X<{{{X f1} f1} f2}>","null|(X<null|X>)"); }
	@Test public void test_2659() { checkNotSubtype("X<{{{X f1} f1} f2}>","{null f1}|null"); }
	@Test public void test_2660() { checkNotSubtype("X<{{{X f1} f1} f2}>","{null f2}|null"); }
	@Test public void test_2661() { checkNotSubtype("X<{{{X f1} f1} f2}>","X<{X f1}>|null"); }
	@Test public void test_2662() { checkNotSubtype("X<{{{X f1} f1} f2}>","X<{X f2}>|null"); }
	@Test public void test_2663() { checkNotSubtype("X<{{{X f1} f1} f2}>","X<X|{null f1}>"); }
	@Test public void test_2664() { checkNotSubtype("X<{{{X f1} f1} f2}>","X<X|{null f2}>"); }
	@Test public void test_2665() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2666() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2667() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2668() { checkIsSubtype("X<{{{X f1} f1} f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2669() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2670() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2671() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2672() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2673() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2674() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2675() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2676() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2677() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2678() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2679() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2680() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2681() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_2682() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_2683() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_2684() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_2685() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2686() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2687() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2688() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2689() { checkNotSubtype("X<{{{X f1} f1} f2}>","(X<X|null>)|null"); }
	@Test public void test_2690() { checkNotSubtype("X<{{{X f1} f1} f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_2691() { checkIsSubtype("X<{{{X f1} f1} f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2692() { checkIsSubtype("X<{{{X f1} f1} f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2693() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2694() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2695() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2696() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2697() { checkIsSubtype("X<{{{X f1} f1} f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2698() { checkNotSubtype("X<{{{X f2} f1} f2}>","null"); }
	@Test public void test_2699() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f1}>"); }
	@Test public void test_2700() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f2}>"); }
	@Test public void test_2701() { checkNotSubtype("X<{{{X f2} f1} f2}>","{null f1}"); }
	@Test public void test_2702() { checkNotSubtype("X<{{{X f2} f1} f2}>","{null f2}"); }
	@Test public void test_2703() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<{X f1}> f1}"); }
	@Test public void test_2704() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<{X f2}> f1}"); }
	@Test public void test_2705() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<{X f1}> f2}"); }
	@Test public void test_2706() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<{X f2}> f2}"); }
	@Test public void test_2707() { checkNotSubtype("X<{{{X f2} f1} f2}>","X<X|null>"); }
	@Test public void test_2708() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_2709() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_2710() { checkNotSubtype("X<{{{X f2} f1} f2}>","{{null f1} f1}"); }
	@Test public void test_2711() { checkNotSubtype("X<{{{X f2} f1} f2}>","{{null f2} f1}"); }
	@Test public void test_2712() { checkNotSubtype("X<{{{X f2} f1} f2}>","{{null f1} f2}"); }
	@Test public void test_2713() { checkNotSubtype("X<{{{X f2} f1} f2}>","{{null f2} f2}"); }
	@Test public void test_2714() { checkIsSubtype("X<{{{X f2} f1} f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2715() { checkIsSubtype("X<{{{X f2} f1} f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2716() { checkIsSubtype("X<{{{X f2} f1} f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2717() { checkIsSubtype("X<{{{X f2} f1} f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2718() { checkIsSubtype("X<{{{X f2} f1} f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2719() { checkIsSubtype("X<{{{X f2} f1} f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2720() { checkIsSubtype("X<{{{X f2} f1} f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2721() { checkIsSubtype("X<{{{X f2} f1} f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2722() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2723() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2724() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2725() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2726() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2727() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2728() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2729() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2730() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2731() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2732() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2733() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2734() { checkNotSubtype("X<{{{X f2} f1} f2}>","{X<X|null> f1}"); }
	@Test public void test_2735() { checkNotSubtype("X<{{{X f2} f1} f2}>","{X<X|null> f2}"); }
	@Test public void test_2736() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2737() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2738() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2739() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2740() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2741() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2742() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2743() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2744() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2745() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2746() { checkNotSubtype("X<{{{X f2} f1} f2}>","null|null"); }
	@Test public void test_2747() { checkNotSubtype("X<{{{X f2} f1} f2}>","null|X<{X f1}>"); }
	@Test public void test_2748() { checkNotSubtype("X<{{{X f2} f1} f2}>","null|X<{X f2}>"); }
	@Test public void test_2749() { checkNotSubtype("X<{{{X f2} f1} f2}>","null|{null f1}"); }
	@Test public void test_2750() { checkNotSubtype("X<{{{X f2} f1} f2}>","null|{null f2}"); }
	@Test public void test_2751() { checkNotSubtype("X<{{{X f2} f1} f2}>","null|(X<null|X>)"); }
	@Test public void test_2752() { checkNotSubtype("X<{{{X f2} f1} f2}>","{null f1}|null"); }
	@Test public void test_2753() { checkNotSubtype("X<{{{X f2} f1} f2}>","{null f2}|null"); }
	@Test public void test_2754() { checkNotSubtype("X<{{{X f2} f1} f2}>","X<{X f1}>|null"); }
	@Test public void test_2755() { checkNotSubtype("X<{{{X f2} f1} f2}>","X<{X f2}>|null"); }
	@Test public void test_2756() { checkNotSubtype("X<{{{X f2} f1} f2}>","X<X|{null f1}>"); }
	@Test public void test_2757() { checkNotSubtype("X<{{{X f2} f1} f2}>","X<X|{null f2}>"); }
	@Test public void test_2758() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2759() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2760() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2761() { checkIsSubtype("X<{{{X f2} f1} f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2762() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2763() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2764() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2765() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2766() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2767() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2768() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2769() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2770() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2771() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2772() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2773() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2774() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_2775() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_2776() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_2777() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_2778() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2779() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2780() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2781() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2782() { checkNotSubtype("X<{{{X f2} f1} f2}>","(X<X|null>)|null"); }
	@Test public void test_2783() { checkNotSubtype("X<{{{X f2} f1} f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_2784() { checkIsSubtype("X<{{{X f2} f1} f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2785() { checkIsSubtype("X<{{{X f2} f1} f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2786() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2787() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2788() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2789() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2790() { checkIsSubtype("X<{{{X f2} f1} f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2791() { checkNotSubtype("X<{{{X f1} f2} f2}>","null"); }
	@Test public void test_2792() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f1}>"); }
	@Test public void test_2793() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f2}>"); }
	@Test public void test_2794() { checkNotSubtype("X<{{{X f1} f2} f2}>","{null f1}"); }
	@Test public void test_2795() { checkNotSubtype("X<{{{X f1} f2} f2}>","{null f2}"); }
	@Test public void test_2796() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<{X f1}> f1}"); }
	@Test public void test_2797() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<{X f2}> f1}"); }
	@Test public void test_2798() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<{X f1}> f2}"); }
	@Test public void test_2799() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<{X f2}> f2}"); }
	@Test public void test_2800() { checkNotSubtype("X<{{{X f1} f2} f2}>","X<X|null>"); }
	@Test public void test_2801() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_2802() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_2803() { checkNotSubtype("X<{{{X f1} f2} f2}>","{{null f1} f1}"); }
	@Test public void test_2804() { checkNotSubtype("X<{{{X f1} f2} f2}>","{{null f2} f1}"); }
	@Test public void test_2805() { checkNotSubtype("X<{{{X f1} f2} f2}>","{{null f1} f2}"); }
	@Test public void test_2806() { checkNotSubtype("X<{{{X f1} f2} f2}>","{{null f2} f2}"); }
	@Test public void test_2807() { checkIsSubtype("X<{{{X f1} f2} f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2808() { checkIsSubtype("X<{{{X f1} f2} f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2809() { checkIsSubtype("X<{{{X f1} f2} f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2810() { checkIsSubtype("X<{{{X f1} f2} f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2811() { checkIsSubtype("X<{{{X f1} f2} f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2812() { checkIsSubtype("X<{{{X f1} f2} f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2813() { checkIsSubtype("X<{{{X f1} f2} f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2814() { checkIsSubtype("X<{{{X f1} f2} f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2815() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2816() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2817() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2818() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2819() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2820() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2821() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2822() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2823() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2824() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2825() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2826() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2827() { checkNotSubtype("X<{{{X f1} f2} f2}>","{X<X|null> f1}"); }
	@Test public void test_2828() { checkNotSubtype("X<{{{X f1} f2} f2}>","{X<X|null> f2}"); }
	@Test public void test_2829() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2830() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2831() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2832() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2833() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2834() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2835() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2836() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2837() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2838() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2839() { checkNotSubtype("X<{{{X f1} f2} f2}>","null|null"); }
	@Test public void test_2840() { checkNotSubtype("X<{{{X f1} f2} f2}>","null|X<{X f1}>"); }
	@Test public void test_2841() { checkNotSubtype("X<{{{X f1} f2} f2}>","null|X<{X f2}>"); }
	@Test public void test_2842() { checkNotSubtype("X<{{{X f1} f2} f2}>","null|{null f1}"); }
	@Test public void test_2843() { checkNotSubtype("X<{{{X f1} f2} f2}>","null|{null f2}"); }
	@Test public void test_2844() { checkNotSubtype("X<{{{X f1} f2} f2}>","null|(X<null|X>)"); }
	@Test public void test_2845() { checkNotSubtype("X<{{{X f1} f2} f2}>","{null f1}|null"); }
	@Test public void test_2846() { checkNotSubtype("X<{{{X f1} f2} f2}>","{null f2}|null"); }
	@Test public void test_2847() { checkNotSubtype("X<{{{X f1} f2} f2}>","X<{X f1}>|null"); }
	@Test public void test_2848() { checkNotSubtype("X<{{{X f1} f2} f2}>","X<{X f2}>|null"); }
	@Test public void test_2849() { checkNotSubtype("X<{{{X f1} f2} f2}>","X<X|{null f1}>"); }
	@Test public void test_2850() { checkNotSubtype("X<{{{X f1} f2} f2}>","X<X|{null f2}>"); }
	@Test public void test_2851() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2852() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2853() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2854() { checkIsSubtype("X<{{{X f1} f2} f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2855() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2856() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2857() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2858() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2859() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2860() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2861() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2862() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2863() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2864() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2865() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2866() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2867() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_2868() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_2869() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_2870() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_2871() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2872() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2873() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2874() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2875() { checkNotSubtype("X<{{{X f1} f2} f2}>","(X<X|null>)|null"); }
	@Test public void test_2876() { checkNotSubtype("X<{{{X f1} f2} f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_2877() { checkIsSubtype("X<{{{X f1} f2} f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2878() { checkIsSubtype("X<{{{X f1} f2} f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2879() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2880() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2881() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2882() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2883() { checkIsSubtype("X<{{{X f1} f2} f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2884() { checkNotSubtype("X<{{{X f2} f2} f2}>","null"); }
	@Test public void test_2885() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f1}>"); }
	@Test public void test_2886() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f2}>"); }
	@Test public void test_2887() { checkNotSubtype("X<{{{X f2} f2} f2}>","{null f1}"); }
	@Test public void test_2888() { checkNotSubtype("X<{{{X f2} f2} f2}>","{null f2}"); }
	@Test public void test_2889() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<{X f1}> f1}"); }
	@Test public void test_2890() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<{X f2}> f1}"); }
	@Test public void test_2891() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<{X f1}> f2}"); }
	@Test public void test_2892() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<{X f2}> f2}"); }
	@Test public void test_2893() { checkNotSubtype("X<{{{X f2} f2} f2}>","X<X|null>"); }
	@Test public void test_2894() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_2895() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_2896() { checkNotSubtype("X<{{{X f2} f2} f2}>","{{null f1} f1}"); }
	@Test public void test_2897() { checkNotSubtype("X<{{{X f2} f2} f2}>","{{null f2} f1}"); }
	@Test public void test_2898() { checkNotSubtype("X<{{{X f2} f2} f2}>","{{null f1} f2}"); }
	@Test public void test_2899() { checkNotSubtype("X<{{{X f2} f2} f2}>","{{null f2} f2}"); }
	@Test public void test_2900() { checkIsSubtype("X<{{{X f2} f2} f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2901() { checkIsSubtype("X<{{{X f2} f2} f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2902() { checkIsSubtype("X<{{{X f2} f2} f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2903() { checkIsSubtype("X<{{{X f2} f2} f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2904() { checkIsSubtype("X<{{{X f2} f2} f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2905() { checkIsSubtype("X<{{{X f2} f2} f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2906() { checkIsSubtype("X<{{{X f2} f2} f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_2907() { checkIsSubtype("X<{{{X f2} f2} f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_2908() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_2909() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_2910() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_2911() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_2912() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_2913() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_2914() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_2915() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_2916() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_2917() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_2918() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_2919() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_2920() { checkNotSubtype("X<{{{X f2} f2} f2}>","{X<X|null> f1}"); }
	@Test public void test_2921() { checkNotSubtype("X<{{{X f2} f2} f2}>","{X<X|null> f2}"); }
	@Test public void test_2922() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_2923() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_2924() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_2925() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_2926() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_2927() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_2928() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_2929() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_2930() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_2931() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_2932() { checkNotSubtype("X<{{{X f2} f2} f2}>","null|null"); }
	@Test public void test_2933() { checkNotSubtype("X<{{{X f2} f2} f2}>","null|X<{X f1}>"); }
	@Test public void test_2934() { checkNotSubtype("X<{{{X f2} f2} f2}>","null|X<{X f2}>"); }
	@Test public void test_2935() { checkNotSubtype("X<{{{X f2} f2} f2}>","null|{null f1}"); }
	@Test public void test_2936() { checkNotSubtype("X<{{{X f2} f2} f2}>","null|{null f2}"); }
	@Test public void test_2937() { checkNotSubtype("X<{{{X f2} f2} f2}>","null|(X<null|X>)"); }
	@Test public void test_2938() { checkNotSubtype("X<{{{X f2} f2} f2}>","{null f1}|null"); }
	@Test public void test_2939() { checkNotSubtype("X<{{{X f2} f2} f2}>","{null f2}|null"); }
	@Test public void test_2940() { checkNotSubtype("X<{{{X f2} f2} f2}>","X<{X f1}>|null"); }
	@Test public void test_2941() { checkNotSubtype("X<{{{X f2} f2} f2}>","X<{X f2}>|null"); }
	@Test public void test_2942() { checkNotSubtype("X<{{{X f2} f2} f2}>","X<X|{null f1}>"); }
	@Test public void test_2943() { checkNotSubtype("X<{{{X f2} f2} f2}>","X<X|{null f2}>"); }
	@Test public void test_2944() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_2945() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_2946() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_2947() { checkIsSubtype("X<{{{X f2} f2} f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_2948() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_2949() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_2950() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_2951() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_2952() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_2953() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_2954() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_2955() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_2956() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_2957() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_2958() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_2959() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_2960() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_2961() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_2962() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_2963() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_2964() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_2965() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_2966() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_2967() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_2968() { checkNotSubtype("X<{{{X f2} f2} f2}>","(X<X|null>)|null"); }
	@Test public void test_2969() { checkNotSubtype("X<{{{X f2} f2} f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_2970() { checkIsSubtype("X<{{{X f2} f2} f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_2971() { checkIsSubtype("X<{{{X f2} f2} f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_2972() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_2973() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_2974() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_2975() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_2976() { checkIsSubtype("X<{{{X f2} f2} f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_2977() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","null"); }
	@Test public void test_2978() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f1}>"); }
	@Test public void test_2979() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f2}>"); }
	@Test public void test_2980() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{null f1}"); }
	@Test public void test_2981() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{null f2}"); }
	@Test public void test_2982() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<{X f1}> f1}"); }
	@Test public void test_2983() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<{X f2}> f1}"); }
	@Test public void test_2984() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<{X f1}> f2}"); }
	@Test public void test_2985() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<{X f2}> f2}"); }
	@Test public void test_2986() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","X<X|null>"); }
	@Test public void test_2987() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_2988() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_2989() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{{null f1} f1}"); }
	@Test public void test_2990() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{{null f2} f1}"); }
	@Test public void test_2991() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{{null f1} f2}"); }
	@Test public void test_2992() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{{null f2} f2}"); }
	@Test public void test_2993() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_2994() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_2995() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_2996() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_2997() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_2998() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_2999() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3000() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3001() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3002() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3003() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3004() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3005() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3006() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3007() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3008() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3009() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3010() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3011() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3012() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3013() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{X<X|null> f1}"); }
	@Test public void test_3014() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{X<X|null> f2}"); }
	@Test public void test_3015() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3016() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3017() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3018() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3019() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3020() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3021() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3022() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3023() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3024() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3025() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","null|null"); }
	@Test public void test_3026() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","null|X<{X f1}>"); }
	@Test public void test_3027() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","null|X<{X f2}>"); }
	@Test public void test_3028() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","null|{null f1}"); }
	@Test public void test_3029() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","null|{null f2}"); }
	@Test public void test_3030() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","null|(X<null|X>)"); }
	@Test public void test_3031() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{null f1}|null"); }
	@Test public void test_3032() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","{null f2}|null"); }
	@Test public void test_3033() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f1}>|null"); }
	@Test public void test_3034() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f2}>|null"); }
	@Test public void test_3035() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{null f1}>"); }
	@Test public void test_3036() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{null f2}>"); }
	@Test public void test_3037() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3038() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3039() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3040() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3041() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3042() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3043() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3044() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3045() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3046() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3047() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3048() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3049() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3050() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3051() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3052() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3053() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_3054() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_3055() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_3056() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_3057() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3058() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3059() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3060() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3061() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","(X<X|null>)|null"); }
	@Test public void test_3062() { checkNotSubtype("X<{{Y<X|Y> f1} f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_3063() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3064() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3065() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3066() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3067() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3068() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3069() { checkIsSubtype("X<{{Y<X|Y> f1} f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3070() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","null"); }
	@Test public void test_3071() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f1}>"); }
	@Test public void test_3072() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f2}>"); }
	@Test public void test_3073() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{null f1}"); }
	@Test public void test_3074() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{null f2}"); }
	@Test public void test_3075() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<{X f1}> f1}"); }
	@Test public void test_3076() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<{X f2}> f1}"); }
	@Test public void test_3077() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<{X f1}> f2}"); }
	@Test public void test_3078() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<{X f2}> f2}"); }
	@Test public void test_3079() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","X<X|null>"); }
	@Test public void test_3080() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_3081() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_3082() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{{null f1} f1}"); }
	@Test public void test_3083() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{{null f2} f1}"); }
	@Test public void test_3084() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{{null f1} f2}"); }
	@Test public void test_3085() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{{null f2} f2}"); }
	@Test public void test_3086() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3087() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3088() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3089() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3090() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3091() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3092() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3093() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3094() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3095() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3096() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3097() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3098() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3099() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3100() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3101() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3102() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3103() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3104() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3105() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3106() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{X<X|null> f1}"); }
	@Test public void test_3107() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{X<X|null> f2}"); }
	@Test public void test_3108() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3109() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3110() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3111() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3112() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3113() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3114() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3115() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3116() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3117() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3118() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","null|null"); }
	@Test public void test_3119() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","null|X<{X f1}>"); }
	@Test public void test_3120() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","null|X<{X f2}>"); }
	@Test public void test_3121() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","null|{null f1}"); }
	@Test public void test_3122() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","null|{null f2}"); }
	@Test public void test_3123() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","null|(X<null|X>)"); }
	@Test public void test_3124() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{null f1}|null"); }
	@Test public void test_3125() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","{null f2}|null"); }
	@Test public void test_3126() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f1}>|null"); }
	@Test public void test_3127() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f2}>|null"); }
	@Test public void test_3128() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{null f1}>"); }
	@Test public void test_3129() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{null f2}>"); }
	@Test public void test_3130() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3131() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3132() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3133() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3134() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3135() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3136() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3137() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3138() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3139() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3140() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3141() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3142() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3143() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3144() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3145() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3146() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_3147() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_3148() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_3149() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_3150() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3151() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3152() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3153() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3154() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","(X<X|null>)|null"); }
	@Test public void test_3155() { checkNotSubtype("X<{{Y<X|Y> f2} f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_3156() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3157() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3158() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3159() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3160() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3161() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3162() { checkIsSubtype("X<{{Y<X|Y> f2} f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3163() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","null"); }
	@Test public void test_3164() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f1}>"); }
	@Test public void test_3165() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f2}>"); }
	@Test public void test_3166() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{null f1}"); }
	@Test public void test_3167() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{null f2}"); }
	@Test public void test_3168() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<{X f1}> f1}"); }
	@Test public void test_3169() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<{X f2}> f1}"); }
	@Test public void test_3170() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<{X f1}> f2}"); }
	@Test public void test_3171() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<{X f2}> f2}"); }
	@Test public void test_3172() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","X<X|null>"); }
	@Test public void test_3173() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_3174() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_3175() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{{null f1} f1}"); }
	@Test public void test_3176() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{{null f2} f1}"); }
	@Test public void test_3177() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{{null f1} f2}"); }
	@Test public void test_3178() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{{null f2} f2}"); }
	@Test public void test_3179() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3180() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3181() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3182() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3183() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3184() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3185() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3186() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3187() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3188() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3189() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3190() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3191() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3192() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3193() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3194() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3195() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3196() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3197() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3198() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3199() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{X<X|null> f1}"); }
	@Test public void test_3200() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{X<X|null> f2}"); }
	@Test public void test_3201() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3202() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3203() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3204() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3205() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3206() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3207() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3208() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3209() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3210() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3211() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","null|null"); }
	@Test public void test_3212() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","null|X<{X f1}>"); }
	@Test public void test_3213() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","null|X<{X f2}>"); }
	@Test public void test_3214() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","null|{null f1}"); }
	@Test public void test_3215() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","null|{null f2}"); }
	@Test public void test_3216() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","null|(X<null|X>)"); }
	@Test public void test_3217() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{null f1}|null"); }
	@Test public void test_3218() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","{null f2}|null"); }
	@Test public void test_3219() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f1}>|null"); }
	@Test public void test_3220() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f2}>|null"); }
	@Test public void test_3221() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{null f1}>"); }
	@Test public void test_3222() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{null f2}>"); }
	@Test public void test_3223() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3224() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3225() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3226() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3227() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3228() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3229() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3230() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3231() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3232() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3233() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3234() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3235() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3236() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3237() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3238() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3239() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_3240() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_3241() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_3242() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_3243() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3244() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3245() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3246() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3247() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","(X<X|null>)|null"); }
	@Test public void test_3248() { checkNotSubtype("X<{{Y<X|Y> f1} f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_3249() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3250() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3251() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3252() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3253() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3254() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3255() { checkIsSubtype("X<{{Y<X|Y> f1} f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3256() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","null"); }
	@Test public void test_3257() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f1}>"); }
	@Test public void test_3258() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f2}>"); }
	@Test public void test_3259() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{null f1}"); }
	@Test public void test_3260() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{null f2}"); }
	@Test public void test_3261() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<{X f1}> f1}"); }
	@Test public void test_3262() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<{X f2}> f1}"); }
	@Test public void test_3263() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<{X f1}> f2}"); }
	@Test public void test_3264() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<{X f2}> f2}"); }
	@Test public void test_3265() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","X<X|null>"); }
	@Test public void test_3266() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_3267() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_3268() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{{null f1} f1}"); }
	@Test public void test_3269() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{{null f2} f1}"); }
	@Test public void test_3270() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{{null f1} f2}"); }
	@Test public void test_3271() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{{null f2} f2}"); }
	@Test public void test_3272() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3273() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3274() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3275() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3276() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3277() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3278() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3279() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3280() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3281() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3282() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3283() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3284() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3285() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3286() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3287() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3288() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3289() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3290() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3291() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3292() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{X<X|null> f1}"); }
	@Test public void test_3293() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{X<X|null> f2}"); }
	@Test public void test_3294() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3295() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3296() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3297() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3298() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3299() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3300() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3301() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3302() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3303() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3304() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","null|null"); }
	@Test public void test_3305() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","null|X<{X f1}>"); }
	@Test public void test_3306() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","null|X<{X f2}>"); }
	@Test public void test_3307() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","null|{null f1}"); }
	@Test public void test_3308() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","null|{null f2}"); }
	@Test public void test_3309() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","null|(X<null|X>)"); }
	@Test public void test_3310() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{null f1}|null"); }
	@Test public void test_3311() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","{null f2}|null"); }
	@Test public void test_3312() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f1}>|null"); }
	@Test public void test_3313() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f2}>|null"); }
	@Test public void test_3314() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{null f1}>"); }
	@Test public void test_3315() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{null f2}>"); }
	@Test public void test_3316() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3317() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3318() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3319() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3320() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3321() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3322() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3323() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3324() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3325() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3326() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3327() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3328() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3329() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3330() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3331() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3332() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_3333() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_3334() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_3335() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_3336() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3337() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3338() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3339() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3340() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","(X<X|null>)|null"); }
	@Test public void test_3341() { checkNotSubtype("X<{{Y<X|Y> f2} f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_3342() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3343() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3344() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3345() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3346() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3347() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3348() { checkIsSubtype("X<{{Y<X|Y> f2} f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3349() { checkNotSubtype("{X<X|null> f1}","null"); }
	@Test public void test_3350() { checkIsSubtype("{X<X|null> f1}","X<{X f1}>"); }
	@Test public void test_3351() { checkIsSubtype("{X<X|null> f1}","X<{X f2}>"); }
	@Test public void test_3352() { checkIsSubtype("{X<X|null> f1}","{null f1}"); }
	@Test public void test_3353() { checkNotSubtype("{X<X|null> f1}","{null f2}"); }
	@Test public void test_3354() { checkIsSubtype("{X<X|null> f1}","{X<{X f1}> f1}"); }
	@Test public void test_3355() { checkIsSubtype("{X<X|null> f1}","{X<{X f2}> f1}"); }
	@Test public void test_3356() { checkIsSubtype("{X<X|null> f1}","{X<{X f1}> f2}"); }
	@Test public void test_3357() { checkIsSubtype("{X<X|null> f1}","{X<{X f2}> f2}"); }
	@Test public void test_3358() { checkNotSubtype("{X<X|null> f1}","X<X|null>"); }
	@Test public void test_3359() { checkIsSubtype("{X<X|null> f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_3360() { checkIsSubtype("{X<X|null> f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_3361() { checkNotSubtype("{X<X|null> f1}","{{null f1} f1}"); }
	@Test public void test_3362() { checkNotSubtype("{X<X|null> f1}","{{null f2} f1}"); }
	@Test public void test_3363() { checkNotSubtype("{X<X|null> f1}","{{null f1} f2}"); }
	@Test public void test_3364() { checkNotSubtype("{X<X|null> f1}","{{null f2} f2}"); }
	@Test public void test_3365() { checkIsSubtype("{X<X|null> f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3366() { checkIsSubtype("{X<X|null> f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3367() { checkIsSubtype("{X<X|null> f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3368() { checkIsSubtype("{X<X|null> f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3369() { checkIsSubtype("{X<X|null> f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3370() { checkIsSubtype("{X<X|null> f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3371() { checkIsSubtype("{X<X|null> f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3372() { checkIsSubtype("{X<X|null> f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3373() { checkIsSubtype("{X<X|null> f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3374() { checkIsSubtype("{X<X|null> f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3375() { checkIsSubtype("{X<X|null> f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3376() { checkIsSubtype("{X<X|null> f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3377() { checkIsSubtype("{X<X|null> f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3378() { checkIsSubtype("{X<X|null> f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3379() { checkIsSubtype("{X<X|null> f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3380() { checkIsSubtype("{X<X|null> f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3381() { checkIsSubtype("{X<X|null> f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3382() { checkIsSubtype("{X<X|null> f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3383() { checkIsSubtype("{X<X|null> f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3384() { checkIsSubtype("{X<X|null> f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3385() { checkIsSubtype("{X<X|null> f1}","{X<X|null> f1}"); }
	@Test public void test_3386() { checkNotSubtype("{X<X|null> f1}","{X<X|null> f2}"); }
	@Test public void test_3387() { checkIsSubtype("{X<X|null> f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3388() { checkIsSubtype("{X<X|null> f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3389() { checkIsSubtype("{X<X|null> f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3390() { checkIsSubtype("{X<X|null> f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3391() { checkIsSubtype("{X<X|null> f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3392() { checkIsSubtype("{X<X|null> f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3393() { checkIsSubtype("{X<X|null> f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3394() { checkIsSubtype("{X<X|null> f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3395() { checkIsSubtype("{X<X|null> f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3396() { checkIsSubtype("{X<X|null> f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3397() { checkNotSubtype("{X<X|null> f1}","null|null"); }
	@Test public void test_3398() { checkNotSubtype("{X<X|null> f1}","null|X<{X f1}>"); }
	@Test public void test_3399() { checkNotSubtype("{X<X|null> f1}","null|X<{X f2}>"); }
	@Test public void test_3400() { checkNotSubtype("{X<X|null> f1}","null|{null f1}"); }
	@Test public void test_3401() { checkNotSubtype("{X<X|null> f1}","null|{null f2}"); }
	@Test public void test_3402() { checkNotSubtype("{X<X|null> f1}","null|(X<null|X>)"); }
	@Test public void test_3403() { checkNotSubtype("{X<X|null> f1}","{null f1}|null"); }
	@Test public void test_3404() { checkNotSubtype("{X<X|null> f1}","{null f2}|null"); }
	@Test public void test_3405() { checkNotSubtype("{X<X|null> f1}","X<{X f1}>|null"); }
	@Test public void test_3406() { checkNotSubtype("{X<X|null> f1}","X<{X f2}>|null"); }
	@Test public void test_3407() { checkIsSubtype("{X<X|null> f1}","X<X|{null f1}>"); }
	@Test public void test_3408() { checkNotSubtype("{X<X|null> f1}","X<X|{null f2}>"); }
	@Test public void test_3409() { checkIsSubtype("{X<X|null> f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3410() { checkIsSubtype("{X<X|null> f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3411() { checkIsSubtype("{X<X|null> f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3412() { checkIsSubtype("{X<X|null> f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3413() { checkIsSubtype("{X<X|null> f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3414() { checkIsSubtype("{X<X|null> f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3415() { checkIsSubtype("{X<X|null> f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3416() { checkIsSubtype("{X<X|null> f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3417() { checkIsSubtype("{X<X|null> f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3418() { checkIsSubtype("{X<X|null> f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3419() { checkIsSubtype("{X<X|null> f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3420() { checkIsSubtype("{X<X|null> f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3421() { checkIsSubtype("{X<X|null> f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3422() { checkIsSubtype("{X<X|null> f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3423() { checkIsSubtype("{X<X|null> f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3424() { checkIsSubtype("{X<X|null> f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3425() { checkIsSubtype("{X<X|null> f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_3426() { checkIsSubtype("{X<X|null> f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_3427() { checkIsSubtype("{X<X|null> f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_3428() { checkIsSubtype("{X<X|null> f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_3429() { checkIsSubtype("{X<X|null> f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3430() { checkIsSubtype("{X<X|null> f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3431() { checkIsSubtype("{X<X|null> f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3432() { checkIsSubtype("{X<X|null> f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3433() { checkNotSubtype("{X<X|null> f1}","(X<X|null>)|null"); }
	@Test public void test_3434() { checkNotSubtype("{X<X|null> f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_3435() { checkIsSubtype("{X<X|null> f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3436() { checkIsSubtype("{X<X|null> f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3437() { checkIsSubtype("{X<X|null> f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3438() { checkIsSubtype("{X<X|null> f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3439() { checkIsSubtype("{X<X|null> f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3440() { checkIsSubtype("{X<X|null> f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3441() { checkIsSubtype("{X<X|null> f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3442() { checkNotSubtype("{X<X|null> f2}","null"); }
	@Test public void test_3443() { checkIsSubtype("{X<X|null> f2}","X<{X f1}>"); }
	@Test public void test_3444() { checkIsSubtype("{X<X|null> f2}","X<{X f2}>"); }
	@Test public void test_3445() { checkNotSubtype("{X<X|null> f2}","{null f1}"); }
	@Test public void test_3446() { checkIsSubtype("{X<X|null> f2}","{null f2}"); }
	@Test public void test_3447() { checkIsSubtype("{X<X|null> f2}","{X<{X f1}> f1}"); }
	@Test public void test_3448() { checkIsSubtype("{X<X|null> f2}","{X<{X f2}> f1}"); }
	@Test public void test_3449() { checkIsSubtype("{X<X|null> f2}","{X<{X f1}> f2}"); }
	@Test public void test_3450() { checkIsSubtype("{X<X|null> f2}","{X<{X f2}> f2}"); }
	@Test public void test_3451() { checkNotSubtype("{X<X|null> f2}","X<X|null>"); }
	@Test public void test_3452() { checkIsSubtype("{X<X|null> f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_3453() { checkIsSubtype("{X<X|null> f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_3454() { checkNotSubtype("{X<X|null> f2}","{{null f1} f1}"); }
	@Test public void test_3455() { checkNotSubtype("{X<X|null> f2}","{{null f2} f1}"); }
	@Test public void test_3456() { checkNotSubtype("{X<X|null> f2}","{{null f1} f2}"); }
	@Test public void test_3457() { checkNotSubtype("{X<X|null> f2}","{{null f2} f2}"); }
	@Test public void test_3458() { checkIsSubtype("{X<X|null> f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3459() { checkIsSubtype("{X<X|null> f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3460() { checkIsSubtype("{X<X|null> f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3461() { checkIsSubtype("{X<X|null> f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3462() { checkIsSubtype("{X<X|null> f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3463() { checkIsSubtype("{X<X|null> f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3464() { checkIsSubtype("{X<X|null> f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3465() { checkIsSubtype("{X<X|null> f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3466() { checkIsSubtype("{X<X|null> f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3467() { checkIsSubtype("{X<X|null> f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3468() { checkIsSubtype("{X<X|null> f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3469() { checkIsSubtype("{X<X|null> f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3470() { checkIsSubtype("{X<X|null> f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3471() { checkIsSubtype("{X<X|null> f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3472() { checkIsSubtype("{X<X|null> f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3473() { checkIsSubtype("{X<X|null> f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3474() { checkIsSubtype("{X<X|null> f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3475() { checkIsSubtype("{X<X|null> f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3476() { checkIsSubtype("{X<X|null> f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3477() { checkIsSubtype("{X<X|null> f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3478() { checkNotSubtype("{X<X|null> f2}","{X<X|null> f1}"); }
	@Test public void test_3479() { checkIsSubtype("{X<X|null> f2}","{X<X|null> f2}"); }
	@Test public void test_3480() { checkIsSubtype("{X<X|null> f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3481() { checkIsSubtype("{X<X|null> f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3482() { checkIsSubtype("{X<X|null> f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3483() { checkIsSubtype("{X<X|null> f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3484() { checkIsSubtype("{X<X|null> f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3485() { checkIsSubtype("{X<X|null> f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3486() { checkIsSubtype("{X<X|null> f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3487() { checkIsSubtype("{X<X|null> f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3488() { checkIsSubtype("{X<X|null> f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3489() { checkIsSubtype("{X<X|null> f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3490() { checkNotSubtype("{X<X|null> f2}","null|null"); }
	@Test public void test_3491() { checkNotSubtype("{X<X|null> f2}","null|X<{X f1}>"); }
	@Test public void test_3492() { checkNotSubtype("{X<X|null> f2}","null|X<{X f2}>"); }
	@Test public void test_3493() { checkNotSubtype("{X<X|null> f2}","null|{null f1}"); }
	@Test public void test_3494() { checkNotSubtype("{X<X|null> f2}","null|{null f2}"); }
	@Test public void test_3495() { checkNotSubtype("{X<X|null> f2}","null|(X<null|X>)"); }
	@Test public void test_3496() { checkNotSubtype("{X<X|null> f2}","{null f1}|null"); }
	@Test public void test_3497() { checkNotSubtype("{X<X|null> f2}","{null f2}|null"); }
	@Test public void test_3498() { checkNotSubtype("{X<X|null> f2}","X<{X f1}>|null"); }
	@Test public void test_3499() { checkNotSubtype("{X<X|null> f2}","X<{X f2}>|null"); }
	@Test public void test_3500() { checkNotSubtype("{X<X|null> f2}","X<X|{null f1}>"); }
	@Test public void test_3501() { checkIsSubtype("{X<X|null> f2}","X<X|{null f2}>"); }
	@Test public void test_3502() { checkIsSubtype("{X<X|null> f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3503() { checkIsSubtype("{X<X|null> f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3504() { checkIsSubtype("{X<X|null> f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3505() { checkIsSubtype("{X<X|null> f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3506() { checkIsSubtype("{X<X|null> f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3507() { checkIsSubtype("{X<X|null> f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3508() { checkIsSubtype("{X<X|null> f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3509() { checkIsSubtype("{X<X|null> f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3510() { checkIsSubtype("{X<X|null> f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3511() { checkIsSubtype("{X<X|null> f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3512() { checkIsSubtype("{X<X|null> f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3513() { checkIsSubtype("{X<X|null> f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3514() { checkIsSubtype("{X<X|null> f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3515() { checkIsSubtype("{X<X|null> f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3516() { checkIsSubtype("{X<X|null> f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3517() { checkIsSubtype("{X<X|null> f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3518() { checkIsSubtype("{X<X|null> f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_3519() { checkIsSubtype("{X<X|null> f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_3520() { checkIsSubtype("{X<X|null> f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_3521() { checkIsSubtype("{X<X|null> f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_3522() { checkIsSubtype("{X<X|null> f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3523() { checkIsSubtype("{X<X|null> f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3524() { checkIsSubtype("{X<X|null> f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3525() { checkIsSubtype("{X<X|null> f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3526() { checkNotSubtype("{X<X|null> f2}","(X<X|null>)|null"); }
	@Test public void test_3527() { checkNotSubtype("{X<X|null> f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_3528() { checkIsSubtype("{X<X|null> f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3529() { checkIsSubtype("{X<X|null> f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3530() { checkIsSubtype("{X<X|null> f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3531() { checkIsSubtype("{X<X|null> f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3532() { checkIsSubtype("{X<X|null> f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3533() { checkIsSubtype("{X<X|null> f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3534() { checkIsSubtype("{X<X|null> f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3535() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","null"); }
	@Test public void test_3536() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f1}>"); }
	@Test public void test_3537() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f2}>"); }
	@Test public void test_3538() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{null f1}"); }
	@Test public void test_3539() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{null f2}"); }
	@Test public void test_3540() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<{X f1}> f1}"); }
	@Test public void test_3541() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<{X f2}> f1}"); }
	@Test public void test_3542() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<{X f1}> f2}"); }
	@Test public void test_3543() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<{X f2}> f2}"); }
	@Test public void test_3544() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","X<X|null>"); }
	@Test public void test_3545() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_3546() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_3547() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{{null f1} f1}"); }
	@Test public void test_3548() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{{null f2} f1}"); }
	@Test public void test_3549() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{{null f1} f2}"); }
	@Test public void test_3550() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{{null f2} f2}"); }
	@Test public void test_3551() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3552() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3553() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3554() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3555() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3556() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3557() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3558() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3559() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3560() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3561() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3562() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3563() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3564() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3565() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3566() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3567() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3568() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3569() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3570() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3571() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{X<X|null> f1}"); }
	@Test public void test_3572() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{X<X|null> f2}"); }
	@Test public void test_3573() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3574() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3575() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3576() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3577() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3578() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3579() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3580() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3581() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3582() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3583() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","null|null"); }
	@Test public void test_3584() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","null|X<{X f1}>"); }
	@Test public void test_3585() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","null|X<{X f2}>"); }
	@Test public void test_3586() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","null|{null f1}"); }
	@Test public void test_3587() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","null|{null f2}"); }
	@Test public void test_3588() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","null|(X<null|X>)"); }
	@Test public void test_3589() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{null f1}|null"); }
	@Test public void test_3590() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","{null f2}|null"); }
	@Test public void test_3591() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f1}>|null"); }
	@Test public void test_3592() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f2}>|null"); }
	@Test public void test_3593() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{null f1}>"); }
	@Test public void test_3594() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{null f2}>"); }
	@Test public void test_3595() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3596() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3597() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3598() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3599() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3600() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3601() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3602() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3603() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3604() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3605() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3606() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3607() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3608() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3609() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3610() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3611() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_3612() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_3613() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_3614() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_3615() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3616() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3617() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3618() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3619() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","(X<X|null>)|null"); }
	@Test public void test_3620() { checkNotSubtype("{X<X|Y<{Y f1}>> f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_3621() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3622() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3623() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3624() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3625() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3626() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3627() { checkIsSubtype("{X<X|Y<{Y f1}>> f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3628() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","null"); }
	@Test public void test_3629() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f1}>"); }
	@Test public void test_3630() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f2}>"); }
	@Test public void test_3631() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{null f1}"); }
	@Test public void test_3632() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{null f2}"); }
	@Test public void test_3633() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<{X f1}> f1}"); }
	@Test public void test_3634() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<{X f2}> f1}"); }
	@Test public void test_3635() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<{X f1}> f2}"); }
	@Test public void test_3636() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<{X f2}> f2}"); }
	@Test public void test_3637() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","X<X|null>"); }
	@Test public void test_3638() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_3639() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_3640() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{{null f1} f1}"); }
	@Test public void test_3641() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{{null f2} f1}"); }
	@Test public void test_3642() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{{null f1} f2}"); }
	@Test public void test_3643() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{{null f2} f2}"); }
	@Test public void test_3644() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3645() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3646() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3647() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3648() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3649() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3650() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3651() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3652() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3653() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3654() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3655() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3656() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3657() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3658() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3659() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3660() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3661() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3662() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3663() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3664() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{X<X|null> f1}"); }
	@Test public void test_3665() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{X<X|null> f2}"); }
	@Test public void test_3666() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3667() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3668() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3669() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3670() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3671() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3672() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3673() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3674() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3675() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3676() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","null|null"); }
	@Test public void test_3677() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","null|X<{X f1}>"); }
	@Test public void test_3678() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","null|X<{X f2}>"); }
	@Test public void test_3679() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","null|{null f1}"); }
	@Test public void test_3680() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","null|{null f2}"); }
	@Test public void test_3681() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","null|(X<null|X>)"); }
	@Test public void test_3682() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{null f1}|null"); }
	@Test public void test_3683() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","{null f2}|null"); }
	@Test public void test_3684() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f1}>|null"); }
	@Test public void test_3685() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f2}>|null"); }
	@Test public void test_3686() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{null f1}>"); }
	@Test public void test_3687() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{null f2}>"); }
	@Test public void test_3688() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3689() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3690() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3691() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3692() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3693() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3694() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3695() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3696() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3697() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3698() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3699() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3700() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3701() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3702() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3703() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3704() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_3705() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_3706() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_3707() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_3708() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3709() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3710() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3711() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3712() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","(X<X|null>)|null"); }
	@Test public void test_3713() { checkNotSubtype("{X<X|Y<{Y f2}>> f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_3714() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3715() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3716() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3717() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3718() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3719() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3720() { checkIsSubtype("{X<X|Y<{Y f2}>> f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3721() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","null"); }
	@Test public void test_3722() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f1}>"); }
	@Test public void test_3723() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f2}>"); }
	@Test public void test_3724() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{null f1}"); }
	@Test public void test_3725() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{null f2}"); }
	@Test public void test_3726() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<{X f1}> f1}"); }
	@Test public void test_3727() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<{X f2}> f1}"); }
	@Test public void test_3728() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<{X f1}> f2}"); }
	@Test public void test_3729() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<{X f2}> f2}"); }
	@Test public void test_3730() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","X<X|null>"); }
	@Test public void test_3731() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_3732() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_3733() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{{null f1} f1}"); }
	@Test public void test_3734() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{{null f2} f1}"); }
	@Test public void test_3735() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{{null f1} f2}"); }
	@Test public void test_3736() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{{null f2} f2}"); }
	@Test public void test_3737() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3738() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3739() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3740() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3741() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3742() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3743() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3744() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3745() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3746() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3747() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3748() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3749() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3750() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3751() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3752() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3753() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3754() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3755() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3756() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3757() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{X<X|null> f1}"); }
	@Test public void test_3758() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{X<X|null> f2}"); }
	@Test public void test_3759() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3760() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3761() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3762() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3763() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3764() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3765() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3766() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3767() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3768() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3769() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","null|null"); }
	@Test public void test_3770() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","null|X<{X f1}>"); }
	@Test public void test_3771() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","null|X<{X f2}>"); }
	@Test public void test_3772() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","null|{null f1}"); }
	@Test public void test_3773() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","null|{null f2}"); }
	@Test public void test_3774() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","null|(X<null|X>)"); }
	@Test public void test_3775() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{null f1}|null"); }
	@Test public void test_3776() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","{null f2}|null"); }
	@Test public void test_3777() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f1}>|null"); }
	@Test public void test_3778() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f2}>|null"); }
	@Test public void test_3779() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{null f1}>"); }
	@Test public void test_3780() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{null f2}>"); }
	@Test public void test_3781() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3782() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3783() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3784() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3785() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3786() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3787() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3788() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3789() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3790() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3791() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3792() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3793() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3794() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3795() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3796() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3797() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_3798() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_3799() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_3800() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_3801() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3802() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3803() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3804() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3805() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","(X<X|null>)|null"); }
	@Test public void test_3806() { checkNotSubtype("{X<X|Y<{Y f1}>> f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_3807() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3808() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3809() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3810() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3811() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3812() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3813() { checkIsSubtype("{X<X|Y<{Y f1}>> f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3814() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","null"); }
	@Test public void test_3815() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f1}>"); }
	@Test public void test_3816() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f2}>"); }
	@Test public void test_3817() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{null f1}"); }
	@Test public void test_3818() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{null f2}"); }
	@Test public void test_3819() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<{X f1}> f1}"); }
	@Test public void test_3820() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<{X f2}> f1}"); }
	@Test public void test_3821() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<{X f1}> f2}"); }
	@Test public void test_3822() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<{X f2}> f2}"); }
	@Test public void test_3823() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","X<X|null>"); }
	@Test public void test_3824() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_3825() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_3826() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{{null f1} f1}"); }
	@Test public void test_3827() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{{null f2} f1}"); }
	@Test public void test_3828() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{{null f1} f2}"); }
	@Test public void test_3829() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{{null f2} f2}"); }
	@Test public void test_3830() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3831() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3832() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3833() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3834() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3835() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3836() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3837() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3838() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3839() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3840() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3841() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3842() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3843() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3844() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3845() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3846() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3847() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3848() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3849() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3850() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{X<X|null> f1}"); }
	@Test public void test_3851() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{X<X|null> f2}"); }
	@Test public void test_3852() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3853() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3854() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3855() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3856() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3857() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3858() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3859() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3860() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3861() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3862() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","null|null"); }
	@Test public void test_3863() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","null|X<{X f1}>"); }
	@Test public void test_3864() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","null|X<{X f2}>"); }
	@Test public void test_3865() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","null|{null f1}"); }
	@Test public void test_3866() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","null|{null f2}"); }
	@Test public void test_3867() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","null|(X<null|X>)"); }
	@Test public void test_3868() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{null f1}|null"); }
	@Test public void test_3869() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","{null f2}|null"); }
	@Test public void test_3870() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f1}>|null"); }
	@Test public void test_3871() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f2}>|null"); }
	@Test public void test_3872() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{null f1}>"); }
	@Test public void test_3873() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{null f2}>"); }
	@Test public void test_3874() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3875() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3876() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3877() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3878() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3879() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3880() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3881() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3882() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3883() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3884() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3885() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3886() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3887() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3888() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3889() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3890() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_3891() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_3892() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_3893() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_3894() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3895() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3896() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3897() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3898() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","(X<X|null>)|null"); }
	@Test public void test_3899() { checkNotSubtype("{X<X|Y<{Y f2}>> f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_3900() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3901() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3902() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3903() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3904() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3905() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3906() { checkIsSubtype("{X<X|Y<{Y f2}>> f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_3907() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","null"); }
	@Test public void test_3908() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f1}>"); }
	@Test public void test_3909() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f2}>"); }
	@Test public void test_3910() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{null f1}"); }
	@Test public void test_3911() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{null f2}"); }
	@Test public void test_3912() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<{X f1}> f1}"); }
	@Test public void test_3913() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<{X f2}> f1}"); }
	@Test public void test_3914() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<{X f1}> f2}"); }
	@Test public void test_3915() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<{X f2}> f2}"); }
	@Test public void test_3916() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","X<X|null>"); }
	@Test public void test_3917() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_3918() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_3919() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{{null f1} f1}"); }
	@Test public void test_3920() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{{null f2} f1}"); }
	@Test public void test_3921() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{{null f1} f2}"); }
	@Test public void test_3922() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{{null f2} f2}"); }
	@Test public void test_3923() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_3924() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_3925() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_3926() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_3927() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_3928() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_3929() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_3930() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_3931() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_3932() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_3933() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_3934() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_3935() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_3936() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_3937() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_3938() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_3939() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_3940() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_3941() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_3942() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_3943() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{X<X|null> f1}"); }
	@Test public void test_3944() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{X<X|null> f2}"); }
	@Test public void test_3945() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_3946() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_3947() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_3948() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_3949() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_3950() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_3951() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_3952() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_3953() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_3954() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_3955() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","null|null"); }
	@Test public void test_3956() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","null|X<{X f1}>"); }
	@Test public void test_3957() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","null|X<{X f2}>"); }
	@Test public void test_3958() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","null|{null f1}"); }
	@Test public void test_3959() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","null|{null f2}"); }
	@Test public void test_3960() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","null|(X<null|X>)"); }
	@Test public void test_3961() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{null f1}|null"); }
	@Test public void test_3962() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","{null f2}|null"); }
	@Test public void test_3963() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f1}>|null"); }
	@Test public void test_3964() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f2}>|null"); }
	@Test public void test_3965() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{null f1}>"); }
	@Test public void test_3966() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{null f2}>"); }
	@Test public void test_3967() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_3968() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_3969() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_3970() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_3971() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_3972() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_3973() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_3974() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_3975() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_3976() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_3977() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_3978() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_3979() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_3980() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_3981() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_3982() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_3983() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_3984() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_3985() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_3986() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_3987() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_3988() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_3989() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_3990() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_3991() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","(X<X|null>)|null"); }
	@Test public void test_3992() { checkNotSubtype("X<{Y<Y|{X f1}> f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_3993() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_3994() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_3995() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_3996() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_3997() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_3998() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_3999() { checkIsSubtype("X<{Y<Y|{X f1}> f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4000() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","null"); }
	@Test public void test_4001() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f1}>"); }
	@Test public void test_4002() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f2}>"); }
	@Test public void test_4003() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{null f1}"); }
	@Test public void test_4004() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{null f2}"); }
	@Test public void test_4005() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<{X f1}> f1}"); }
	@Test public void test_4006() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<{X f2}> f1}"); }
	@Test public void test_4007() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<{X f1}> f2}"); }
	@Test public void test_4008() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<{X f2}> f2}"); }
	@Test public void test_4009() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","X<X|null>"); }
	@Test public void test_4010() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_4011() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_4012() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{{null f1} f1}"); }
	@Test public void test_4013() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{{null f2} f1}"); }
	@Test public void test_4014() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{{null f1} f2}"); }
	@Test public void test_4015() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{{null f2} f2}"); }
	@Test public void test_4016() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4017() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4018() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4019() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4020() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4021() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4022() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4023() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4024() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4025() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4026() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4027() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4028() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4029() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4030() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4031() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4032() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4033() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4034() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4035() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4036() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{X<X|null> f1}"); }
	@Test public void test_4037() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{X<X|null> f2}"); }
	@Test public void test_4038() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4039() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4040() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4041() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4042() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4043() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4044() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4045() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4046() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4047() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4048() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","null|null"); }
	@Test public void test_4049() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","null|X<{X f1}>"); }
	@Test public void test_4050() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","null|X<{X f2}>"); }
	@Test public void test_4051() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","null|{null f1}"); }
	@Test public void test_4052() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","null|{null f2}"); }
	@Test public void test_4053() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","null|(X<null|X>)"); }
	@Test public void test_4054() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{null f1}|null"); }
	@Test public void test_4055() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","{null f2}|null"); }
	@Test public void test_4056() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f1}>|null"); }
	@Test public void test_4057() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f2}>|null"); }
	@Test public void test_4058() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{null f1}>"); }
	@Test public void test_4059() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{null f2}>"); }
	@Test public void test_4060() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4061() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4062() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4063() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4064() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4065() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4066() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4067() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4068() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4069() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4070() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4071() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4072() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4073() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4074() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4075() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4076() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_4077() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_4078() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_4079() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_4080() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4081() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4082() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4083() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4084() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","(X<X|null>)|null"); }
	@Test public void test_4085() { checkNotSubtype("X<{Y<Y|{X f2}> f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_4086() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4087() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4088() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4089() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4090() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4091() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4092() { checkIsSubtype("X<{Y<Y|{X f2}> f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4093() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","null"); }
	@Test public void test_4094() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f1}>"); }
	@Test public void test_4095() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f2}>"); }
	@Test public void test_4096() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{null f1}"); }
	@Test public void test_4097() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{null f2}"); }
	@Test public void test_4098() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<{X f1}> f1}"); }
	@Test public void test_4099() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<{X f2}> f1}"); }
	@Test public void test_4100() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<{X f1}> f2}"); }
	@Test public void test_4101() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<{X f2}> f2}"); }
	@Test public void test_4102() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","X<X|null>"); }
	@Test public void test_4103() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_4104() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_4105() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{{null f1} f1}"); }
	@Test public void test_4106() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{{null f2} f1}"); }
	@Test public void test_4107() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{{null f1} f2}"); }
	@Test public void test_4108() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{{null f2} f2}"); }
	@Test public void test_4109() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4110() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4111() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4112() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4113() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4114() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4115() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4116() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4117() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4118() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4119() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4120() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4121() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4122() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4123() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4124() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4125() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4126() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4127() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4128() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4129() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{X<X|null> f1}"); }
	@Test public void test_4130() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{X<X|null> f2}"); }
	@Test public void test_4131() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4132() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4133() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4134() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4135() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4136() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4137() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4138() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4139() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4140() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4141() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","null|null"); }
	@Test public void test_4142() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","null|X<{X f1}>"); }
	@Test public void test_4143() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","null|X<{X f2}>"); }
	@Test public void test_4144() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","null|{null f1}"); }
	@Test public void test_4145() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","null|{null f2}"); }
	@Test public void test_4146() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","null|(X<null|X>)"); }
	@Test public void test_4147() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{null f1}|null"); }
	@Test public void test_4148() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","{null f2}|null"); }
	@Test public void test_4149() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f1}>|null"); }
	@Test public void test_4150() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f2}>|null"); }
	@Test public void test_4151() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{null f1}>"); }
	@Test public void test_4152() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{null f2}>"); }
	@Test public void test_4153() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4154() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4155() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4156() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4157() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4158() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4159() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4160() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4161() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4162() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4163() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4164() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4165() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4166() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4167() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4168() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4169() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_4170() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_4171() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_4172() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_4173() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4174() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4175() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4176() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4177() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","(X<X|null>)|null"); }
	@Test public void test_4178() { checkNotSubtype("X<{Y<Y|{X f1}> f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_4179() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4180() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4181() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4182() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4183() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4184() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4185() { checkIsSubtype("X<{Y<Y|{X f1}> f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4186() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","null"); }
	@Test public void test_4187() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f1}>"); }
	@Test public void test_4188() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f2}>"); }
	@Test public void test_4189() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{null f1}"); }
	@Test public void test_4190() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{null f2}"); }
	@Test public void test_4191() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<{X f1}> f1}"); }
	@Test public void test_4192() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<{X f2}> f1}"); }
	@Test public void test_4193() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<{X f1}> f2}"); }
	@Test public void test_4194() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<{X f2}> f2}"); }
	@Test public void test_4195() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","X<X|null>"); }
	@Test public void test_4196() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_4197() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_4198() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{{null f1} f1}"); }
	@Test public void test_4199() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{{null f2} f1}"); }
	@Test public void test_4200() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{{null f1} f2}"); }
	@Test public void test_4201() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{{null f2} f2}"); }
	@Test public void test_4202() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4203() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4204() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4205() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4206() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4207() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4208() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4209() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4210() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4211() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4212() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4213() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4214() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4215() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4216() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4217() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4218() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4219() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4220() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4221() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4222() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{X<X|null> f1}"); }
	@Test public void test_4223() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{X<X|null> f2}"); }
	@Test public void test_4224() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4225() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4226() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4227() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4228() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4229() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4230() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4231() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4232() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4233() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4234() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","null|null"); }
	@Test public void test_4235() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","null|X<{X f1}>"); }
	@Test public void test_4236() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","null|X<{X f2}>"); }
	@Test public void test_4237() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","null|{null f1}"); }
	@Test public void test_4238() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","null|{null f2}"); }
	@Test public void test_4239() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","null|(X<null|X>)"); }
	@Test public void test_4240() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{null f1}|null"); }
	@Test public void test_4241() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","{null f2}|null"); }
	@Test public void test_4242() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f1}>|null"); }
	@Test public void test_4243() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f2}>|null"); }
	@Test public void test_4244() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{null f1}>"); }
	@Test public void test_4245() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{null f2}>"); }
	@Test public void test_4246() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4247() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4248() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4249() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4250() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4251() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4252() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4253() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4254() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4255() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4256() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4257() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4258() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4259() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4260() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4261() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4262() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_4263() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_4264() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_4265() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_4266() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4267() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4268() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4269() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4270() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","(X<X|null>)|null"); }
	@Test public void test_4271() { checkNotSubtype("X<{Y<Y|{X f2}> f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_4272() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4273() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4274() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4275() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4276() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4277() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4278() { checkIsSubtype("X<{Y<Y|{X f2}> f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4279() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","null"); }
	@Test public void test_4280() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f1}>"); }
	@Test public void test_4281() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f2}>"); }
	@Test public void test_4282() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{null f1}"); }
	@Test public void test_4283() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{null f2}"); }
	@Test public void test_4284() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<{X f1}> f1}"); }
	@Test public void test_4285() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<{X f2}> f1}"); }
	@Test public void test_4286() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<{X f1}> f2}"); }
	@Test public void test_4287() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<{X f2}> f2}"); }
	@Test public void test_4288() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|null>"); }
	@Test public void test_4289() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_4290() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_4291() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{null f1} f1}"); }
	@Test public void test_4292() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{null f2} f1}"); }
	@Test public void test_4293() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{null f1} f2}"); }
	@Test public void test_4294() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{null f2} f2}"); }
	@Test public void test_4295() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4296() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4297() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4298() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4299() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4300() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4301() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4302() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4303() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4304() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4305() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4306() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4307() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4308() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4309() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4310() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4311() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4312() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4313() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4314() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4315() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<X|null> f1}"); }
	@Test public void test_4316() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<X|null> f2}"); }
	@Test public void test_4317() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4318() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4319() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4320() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4321() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4322() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4323() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4324() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4325() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4326() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4327() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","null|null"); }
	@Test public void test_4328() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","null|X<{X f1}>"); }
	@Test public void test_4329() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","null|X<{X f2}>"); }
	@Test public void test_4330() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","null|{null f1}"); }
	@Test public void test_4331() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","null|{null f2}"); }
	@Test public void test_4332() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","null|(X<null|X>)"); }
	@Test public void test_4333() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{null f1}|null"); }
	@Test public void test_4334() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{null f2}|null"); }
	@Test public void test_4335() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f1}>|null"); }
	@Test public void test_4336() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f2}>|null"); }
	@Test public void test_4337() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{null f1}>"); }
	@Test public void test_4338() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{null f2}>"); }
	@Test public void test_4339() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4340() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4341() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4342() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4343() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4344() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4345() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4346() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4347() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4348() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4349() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4350() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4351() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4352() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4353() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4354() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4355() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_4356() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_4357() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_4358() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_4359() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4360() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4361() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4362() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4363() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","(X<X|null>)|null"); }
	@Test public void test_4364() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_4365() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4366() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4367() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4368() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4369() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4370() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4371() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4372() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","null"); }
	@Test public void test_4373() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f1}>"); }
	@Test public void test_4374() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f2}>"); }
	@Test public void test_4375() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{null f1}"); }
	@Test public void test_4376() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{null f2}"); }
	@Test public void test_4377() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<{X f1}> f1}"); }
	@Test public void test_4378() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<{X f2}> f1}"); }
	@Test public void test_4379() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<{X f1}> f2}"); }
	@Test public void test_4380() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<{X f2}> f2}"); }
	@Test public void test_4381() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|null>"); }
	@Test public void test_4382() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_4383() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_4384() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{null f1} f1}"); }
	@Test public void test_4385() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{null f2} f1}"); }
	@Test public void test_4386() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{null f1} f2}"); }
	@Test public void test_4387() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{null f2} f2}"); }
	@Test public void test_4388() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4389() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4390() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4391() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4392() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4393() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4394() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4395() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4396() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4397() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4398() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4399() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4400() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4401() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4402() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4403() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4404() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4405() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4406() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4407() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4408() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<X|null> f1}"); }
	@Test public void test_4409() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<X|null> f2}"); }
	@Test public void test_4410() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4411() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4412() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4413() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4414() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4415() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4416() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4417() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4418() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4419() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4420() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","null|null"); }
	@Test public void test_4421() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","null|X<{X f1}>"); }
	@Test public void test_4422() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","null|X<{X f2}>"); }
	@Test public void test_4423() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","null|{null f1}"); }
	@Test public void test_4424() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","null|{null f2}"); }
	@Test public void test_4425() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","null|(X<null|X>)"); }
	@Test public void test_4426() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{null f1}|null"); }
	@Test public void test_4427() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{null f2}|null"); }
	@Test public void test_4428() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f1}>|null"); }
	@Test public void test_4429() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f2}>|null"); }
	@Test public void test_4430() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{null f1}>"); }
	@Test public void test_4431() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{null f2}>"); }
	@Test public void test_4432() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4433() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4434() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4435() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4436() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4437() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4438() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4439() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4440() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4441() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4442() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4443() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4444() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4445() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4446() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4447() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4448() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_4449() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_4450() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_4451() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_4452() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4453() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4454() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4455() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4456() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","(X<X|null>)|null"); }
	@Test public void test_4457() { checkNotSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_4458() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4459() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4460() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4461() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4462() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4463() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4464() { checkIsSubtype("X<{Y<Y|(Z<X|Z>)> f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4465() { checkIsSubtype("null|null","null"); }
	@Test public void test_4466() { checkIsSubtype("null|null","X<{X f1}>"); }
	@Test public void test_4467() { checkIsSubtype("null|null","X<{X f2}>"); }
	@Test public void test_4468() { checkNotSubtype("null|null","{null f1}"); }
	@Test public void test_4469() { checkNotSubtype("null|null","{null f2}"); }
	@Test public void test_4470() { checkIsSubtype("null|null","{X<{X f1}> f1}"); }
	@Test public void test_4471() { checkIsSubtype("null|null","{X<{X f2}> f1}"); }
	@Test public void test_4472() { checkIsSubtype("null|null","{X<{X f1}> f2}"); }
	@Test public void test_4473() { checkIsSubtype("null|null","{X<{X f2}> f2}"); }
	@Test public void test_4474() { checkIsSubtype("null|null","X<X|null>"); }
	@Test public void test_4475() { checkIsSubtype("null|null","X<X|Y<{Y f1}>>"); }
	@Test public void test_4476() { checkIsSubtype("null|null","X<X|Y<{Y f2}>>"); }
	@Test public void test_4477() { checkNotSubtype("null|null","{{null f1} f1}"); }
	@Test public void test_4478() { checkNotSubtype("null|null","{{null f2} f1}"); }
	@Test public void test_4479() { checkNotSubtype("null|null","{{null f1} f2}"); }
	@Test public void test_4480() { checkNotSubtype("null|null","{{null f2} f2}"); }
	@Test public void test_4481() { checkIsSubtype("null|null","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4482() { checkIsSubtype("null|null","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4483() { checkIsSubtype("null|null","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4484() { checkIsSubtype("null|null","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4485() { checkIsSubtype("null|null","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4486() { checkIsSubtype("null|null","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4487() { checkIsSubtype("null|null","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4488() { checkIsSubtype("null|null","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4489() { checkIsSubtype("null|null","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4490() { checkIsSubtype("null|null","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4491() { checkIsSubtype("null|null","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4492() { checkIsSubtype("null|null","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4493() { checkIsSubtype("null|null","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4494() { checkIsSubtype("null|null","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4495() { checkIsSubtype("null|null","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4496() { checkIsSubtype("null|null","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4497() { checkIsSubtype("null|null","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4498() { checkIsSubtype("null|null","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4499() { checkIsSubtype("null|null","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4500() { checkIsSubtype("null|null","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4501() { checkNotSubtype("null|null","{X<X|null> f1}"); }
	@Test public void test_4502() { checkNotSubtype("null|null","{X<X|null> f2}"); }
	@Test public void test_4503() { checkIsSubtype("null|null","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4504() { checkIsSubtype("null|null","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4505() { checkIsSubtype("null|null","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4506() { checkIsSubtype("null|null","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4507() { checkIsSubtype("null|null","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4508() { checkIsSubtype("null|null","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4509() { checkIsSubtype("null|null","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4510() { checkIsSubtype("null|null","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4511() { checkIsSubtype("null|null","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4512() { checkIsSubtype("null|null","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4513() { checkIsSubtype("null|null","null|null"); }
	@Test public void test_4514() { checkIsSubtype("null|null","null|X<{X f1}>"); }
	@Test public void test_4515() { checkIsSubtype("null|null","null|X<{X f2}>"); }
	@Test public void test_4516() { checkNotSubtype("null|null","null|{null f1}"); }
	@Test public void test_4517() { checkNotSubtype("null|null","null|{null f2}"); }
	@Test public void test_4518() { checkIsSubtype("null|null","null|(X<null|X>)"); }
	@Test public void test_4519() { checkNotSubtype("null|null","{null f1}|null"); }
	@Test public void test_4520() { checkNotSubtype("null|null","{null f2}|null"); }
	@Test public void test_4521() { checkIsSubtype("null|null","X<{X f1}>|null"); }
	@Test public void test_4522() { checkIsSubtype("null|null","X<{X f2}>|null"); }
	@Test public void test_4523() { checkNotSubtype("null|null","X<X|{null f1}>"); }
	@Test public void test_4524() { checkNotSubtype("null|null","X<X|{null f2}>"); }
	@Test public void test_4525() { checkIsSubtype("null|null","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4526() { checkIsSubtype("null|null","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4527() { checkIsSubtype("null|null","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4528() { checkIsSubtype("null|null","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4529() { checkIsSubtype("null|null","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4530() { checkIsSubtype("null|null","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4531() { checkIsSubtype("null|null","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4532() { checkIsSubtype("null|null","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4533() { checkIsSubtype("null|null","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4534() { checkIsSubtype("null|null","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4535() { checkIsSubtype("null|null","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4536() { checkIsSubtype("null|null","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4537() { checkIsSubtype("null|null","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4538() { checkIsSubtype("null|null","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4539() { checkIsSubtype("null|null","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4540() { checkIsSubtype("null|null","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4541() { checkIsSubtype("null|null","X<X|{{X f1} f1}>"); }
	@Test public void test_4542() { checkIsSubtype("null|null","X<X|{{X f2} f1}>"); }
	@Test public void test_4543() { checkIsSubtype("null|null","X<X|{{X f1} f2}>"); }
	@Test public void test_4544() { checkIsSubtype("null|null","X<X|{{X f2} f2}>"); }
	@Test public void test_4545() { checkIsSubtype("null|null","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4546() { checkIsSubtype("null|null","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4547() { checkIsSubtype("null|null","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4548() { checkIsSubtype("null|null","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4549() { checkIsSubtype("null|null","(X<X|null>)|null"); }
	@Test public void test_4550() { checkIsSubtype("null|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_4551() { checkIsSubtype("null|null","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4552() { checkIsSubtype("null|null","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4553() { checkIsSubtype("null|null","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4554() { checkIsSubtype("null|null","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4555() { checkIsSubtype("null|null","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4556() { checkIsSubtype("null|null","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4557() { checkIsSubtype("null|null","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4558() { checkIsSubtype("null|X<{X f1}>","null"); }
	@Test public void test_4559() { checkIsSubtype("null|X<{X f1}>","X<{X f1}>"); }
	@Test public void test_4560() { checkIsSubtype("null|X<{X f1}>","X<{X f2}>"); }
	@Test public void test_4561() { checkNotSubtype("null|X<{X f1}>","{null f1}"); }
	@Test public void test_4562() { checkNotSubtype("null|X<{X f1}>","{null f2}"); }
	@Test public void test_4563() { checkIsSubtype("null|X<{X f1}>","{X<{X f1}> f1}"); }
	@Test public void test_4564() { checkIsSubtype("null|X<{X f1}>","{X<{X f2}> f1}"); }
	@Test public void test_4565() { checkIsSubtype("null|X<{X f1}>","{X<{X f1}> f2}"); }
	@Test public void test_4566() { checkIsSubtype("null|X<{X f1}>","{X<{X f2}> f2}"); }
	@Test public void test_4567() { checkIsSubtype("null|X<{X f1}>","X<X|null>"); }
	@Test public void test_4568() { checkIsSubtype("null|X<{X f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_4569() { checkIsSubtype("null|X<{X f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_4570() { checkNotSubtype("null|X<{X f1}>","{{null f1} f1}"); }
	@Test public void test_4571() { checkNotSubtype("null|X<{X f1}>","{{null f2} f1}"); }
	@Test public void test_4572() { checkNotSubtype("null|X<{X f1}>","{{null f1} f2}"); }
	@Test public void test_4573() { checkNotSubtype("null|X<{X f1}>","{{null f2} f2}"); }
	@Test public void test_4574() { checkIsSubtype("null|X<{X f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4575() { checkIsSubtype("null|X<{X f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4576() { checkIsSubtype("null|X<{X f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4577() { checkIsSubtype("null|X<{X f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4578() { checkIsSubtype("null|X<{X f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4579() { checkIsSubtype("null|X<{X f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4580() { checkIsSubtype("null|X<{X f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4581() { checkIsSubtype("null|X<{X f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4582() { checkIsSubtype("null|X<{X f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4583() { checkIsSubtype("null|X<{X f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4584() { checkIsSubtype("null|X<{X f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4585() { checkIsSubtype("null|X<{X f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4586() { checkIsSubtype("null|X<{X f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4587() { checkIsSubtype("null|X<{X f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4588() { checkIsSubtype("null|X<{X f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4589() { checkIsSubtype("null|X<{X f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4590() { checkIsSubtype("null|X<{X f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4591() { checkIsSubtype("null|X<{X f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4592() { checkIsSubtype("null|X<{X f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4593() { checkIsSubtype("null|X<{X f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4594() { checkNotSubtype("null|X<{X f1}>","{X<X|null> f1}"); }
	@Test public void test_4595() { checkNotSubtype("null|X<{X f1}>","{X<X|null> f2}"); }
	@Test public void test_4596() { checkIsSubtype("null|X<{X f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4597() { checkIsSubtype("null|X<{X f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4598() { checkIsSubtype("null|X<{X f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4599() { checkIsSubtype("null|X<{X f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4600() { checkIsSubtype("null|X<{X f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4601() { checkIsSubtype("null|X<{X f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4602() { checkIsSubtype("null|X<{X f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4603() { checkIsSubtype("null|X<{X f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4604() { checkIsSubtype("null|X<{X f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4605() { checkIsSubtype("null|X<{X f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4606() { checkIsSubtype("null|X<{X f1}>","null|null"); }
	@Test public void test_4607() { checkIsSubtype("null|X<{X f1}>","null|X<{X f1}>"); }
	@Test public void test_4608() { checkIsSubtype("null|X<{X f1}>","null|X<{X f2}>"); }
	@Test public void test_4609() { checkNotSubtype("null|X<{X f1}>","null|{null f1}"); }
	@Test public void test_4610() { checkNotSubtype("null|X<{X f1}>","null|{null f2}"); }
	@Test public void test_4611() { checkIsSubtype("null|X<{X f1}>","null|(X<null|X>)"); }
	@Test public void test_4612() { checkNotSubtype("null|X<{X f1}>","{null f1}|null"); }
	@Test public void test_4613() { checkNotSubtype("null|X<{X f1}>","{null f2}|null"); }
	@Test public void test_4614() { checkIsSubtype("null|X<{X f1}>","X<{X f1}>|null"); }
	@Test public void test_4615() { checkIsSubtype("null|X<{X f1}>","X<{X f2}>|null"); }
	@Test public void test_4616() { checkNotSubtype("null|X<{X f1}>","X<X|{null f1}>"); }
	@Test public void test_4617() { checkNotSubtype("null|X<{X f1}>","X<X|{null f2}>"); }
	@Test public void test_4618() { checkIsSubtype("null|X<{X f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4619() { checkIsSubtype("null|X<{X f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4620() { checkIsSubtype("null|X<{X f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4621() { checkIsSubtype("null|X<{X f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4622() { checkIsSubtype("null|X<{X f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4623() { checkIsSubtype("null|X<{X f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4624() { checkIsSubtype("null|X<{X f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4625() { checkIsSubtype("null|X<{X f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4626() { checkIsSubtype("null|X<{X f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4627() { checkIsSubtype("null|X<{X f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4628() { checkIsSubtype("null|X<{X f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4629() { checkIsSubtype("null|X<{X f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4630() { checkIsSubtype("null|X<{X f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4631() { checkIsSubtype("null|X<{X f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4632() { checkIsSubtype("null|X<{X f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4633() { checkIsSubtype("null|X<{X f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4634() { checkIsSubtype("null|X<{X f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_4635() { checkIsSubtype("null|X<{X f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_4636() { checkIsSubtype("null|X<{X f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_4637() { checkIsSubtype("null|X<{X f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_4638() { checkIsSubtype("null|X<{X f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4639() { checkIsSubtype("null|X<{X f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4640() { checkIsSubtype("null|X<{X f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4641() { checkIsSubtype("null|X<{X f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4642() { checkIsSubtype("null|X<{X f1}>","(X<X|null>)|null"); }
	@Test public void test_4643() { checkIsSubtype("null|X<{X f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_4644() { checkIsSubtype("null|X<{X f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4645() { checkIsSubtype("null|X<{X f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4646() { checkIsSubtype("null|X<{X f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4647() { checkIsSubtype("null|X<{X f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4648() { checkIsSubtype("null|X<{X f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4649() { checkIsSubtype("null|X<{X f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4650() { checkIsSubtype("null|X<{X f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4651() { checkIsSubtype("null|X<{X f2}>","null"); }
	@Test public void test_4652() { checkIsSubtype("null|X<{X f2}>","X<{X f1}>"); }
	@Test public void test_4653() { checkIsSubtype("null|X<{X f2}>","X<{X f2}>"); }
	@Test public void test_4654() { checkNotSubtype("null|X<{X f2}>","{null f1}"); }
	@Test public void test_4655() { checkNotSubtype("null|X<{X f2}>","{null f2}"); }
	@Test public void test_4656() { checkIsSubtype("null|X<{X f2}>","{X<{X f1}> f1}"); }
	@Test public void test_4657() { checkIsSubtype("null|X<{X f2}>","{X<{X f2}> f1}"); }
	@Test public void test_4658() { checkIsSubtype("null|X<{X f2}>","{X<{X f1}> f2}"); }
	@Test public void test_4659() { checkIsSubtype("null|X<{X f2}>","{X<{X f2}> f2}"); }
	@Test public void test_4660() { checkIsSubtype("null|X<{X f2}>","X<X|null>"); }
	@Test public void test_4661() { checkIsSubtype("null|X<{X f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_4662() { checkIsSubtype("null|X<{X f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_4663() { checkNotSubtype("null|X<{X f2}>","{{null f1} f1}"); }
	@Test public void test_4664() { checkNotSubtype("null|X<{X f2}>","{{null f2} f1}"); }
	@Test public void test_4665() { checkNotSubtype("null|X<{X f2}>","{{null f1} f2}"); }
	@Test public void test_4666() { checkNotSubtype("null|X<{X f2}>","{{null f2} f2}"); }
	@Test public void test_4667() { checkIsSubtype("null|X<{X f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4668() { checkIsSubtype("null|X<{X f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4669() { checkIsSubtype("null|X<{X f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4670() { checkIsSubtype("null|X<{X f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4671() { checkIsSubtype("null|X<{X f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4672() { checkIsSubtype("null|X<{X f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4673() { checkIsSubtype("null|X<{X f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4674() { checkIsSubtype("null|X<{X f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4675() { checkIsSubtype("null|X<{X f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4676() { checkIsSubtype("null|X<{X f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4677() { checkIsSubtype("null|X<{X f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4678() { checkIsSubtype("null|X<{X f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4679() { checkIsSubtype("null|X<{X f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4680() { checkIsSubtype("null|X<{X f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4681() { checkIsSubtype("null|X<{X f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4682() { checkIsSubtype("null|X<{X f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4683() { checkIsSubtype("null|X<{X f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4684() { checkIsSubtype("null|X<{X f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4685() { checkIsSubtype("null|X<{X f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4686() { checkIsSubtype("null|X<{X f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4687() { checkNotSubtype("null|X<{X f2}>","{X<X|null> f1}"); }
	@Test public void test_4688() { checkNotSubtype("null|X<{X f2}>","{X<X|null> f2}"); }
	@Test public void test_4689() { checkIsSubtype("null|X<{X f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4690() { checkIsSubtype("null|X<{X f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4691() { checkIsSubtype("null|X<{X f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4692() { checkIsSubtype("null|X<{X f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4693() { checkIsSubtype("null|X<{X f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4694() { checkIsSubtype("null|X<{X f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4695() { checkIsSubtype("null|X<{X f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4696() { checkIsSubtype("null|X<{X f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4697() { checkIsSubtype("null|X<{X f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4698() { checkIsSubtype("null|X<{X f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4699() { checkIsSubtype("null|X<{X f2}>","null|null"); }
	@Test public void test_4700() { checkIsSubtype("null|X<{X f2}>","null|X<{X f1}>"); }
	@Test public void test_4701() { checkIsSubtype("null|X<{X f2}>","null|X<{X f2}>"); }
	@Test public void test_4702() { checkNotSubtype("null|X<{X f2}>","null|{null f1}"); }
	@Test public void test_4703() { checkNotSubtype("null|X<{X f2}>","null|{null f2}"); }
	@Test public void test_4704() { checkIsSubtype("null|X<{X f2}>","null|(X<null|X>)"); }
	@Test public void test_4705() { checkNotSubtype("null|X<{X f2}>","{null f1}|null"); }
	@Test public void test_4706() { checkNotSubtype("null|X<{X f2}>","{null f2}|null"); }
	@Test public void test_4707() { checkIsSubtype("null|X<{X f2}>","X<{X f1}>|null"); }
	@Test public void test_4708() { checkIsSubtype("null|X<{X f2}>","X<{X f2}>|null"); }
	@Test public void test_4709() { checkNotSubtype("null|X<{X f2}>","X<X|{null f1}>"); }
	@Test public void test_4710() { checkNotSubtype("null|X<{X f2}>","X<X|{null f2}>"); }
	@Test public void test_4711() { checkIsSubtype("null|X<{X f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4712() { checkIsSubtype("null|X<{X f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4713() { checkIsSubtype("null|X<{X f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4714() { checkIsSubtype("null|X<{X f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4715() { checkIsSubtype("null|X<{X f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4716() { checkIsSubtype("null|X<{X f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4717() { checkIsSubtype("null|X<{X f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4718() { checkIsSubtype("null|X<{X f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4719() { checkIsSubtype("null|X<{X f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4720() { checkIsSubtype("null|X<{X f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4721() { checkIsSubtype("null|X<{X f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4722() { checkIsSubtype("null|X<{X f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4723() { checkIsSubtype("null|X<{X f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4724() { checkIsSubtype("null|X<{X f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4725() { checkIsSubtype("null|X<{X f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4726() { checkIsSubtype("null|X<{X f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4727() { checkIsSubtype("null|X<{X f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_4728() { checkIsSubtype("null|X<{X f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_4729() { checkIsSubtype("null|X<{X f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_4730() { checkIsSubtype("null|X<{X f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_4731() { checkIsSubtype("null|X<{X f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4732() { checkIsSubtype("null|X<{X f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4733() { checkIsSubtype("null|X<{X f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4734() { checkIsSubtype("null|X<{X f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4735() { checkIsSubtype("null|X<{X f2}>","(X<X|null>)|null"); }
	@Test public void test_4736() { checkIsSubtype("null|X<{X f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_4737() { checkIsSubtype("null|X<{X f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4738() { checkIsSubtype("null|X<{X f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4739() { checkIsSubtype("null|X<{X f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4740() { checkIsSubtype("null|X<{X f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4741() { checkIsSubtype("null|X<{X f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4742() { checkIsSubtype("null|X<{X f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4743() { checkIsSubtype("null|X<{X f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4744() { checkIsSubtype("null|{null f1}","null"); }
	@Test public void test_4745() { checkIsSubtype("null|{null f1}","X<{X f1}>"); }
	@Test public void test_4746() { checkIsSubtype("null|{null f1}","X<{X f2}>"); }
	@Test public void test_4747() { checkIsSubtype("null|{null f1}","{null f1}"); }
	@Test public void test_4748() { checkNotSubtype("null|{null f1}","{null f2}"); }
	@Test public void test_4749() { checkIsSubtype("null|{null f1}","{X<{X f1}> f1}"); }
	@Test public void test_4750() { checkIsSubtype("null|{null f1}","{X<{X f2}> f1}"); }
	@Test public void test_4751() { checkIsSubtype("null|{null f1}","{X<{X f1}> f2}"); }
	@Test public void test_4752() { checkIsSubtype("null|{null f1}","{X<{X f2}> f2}"); }
	@Test public void test_4753() { checkIsSubtype("null|{null f1}","X<X|null>"); }
	@Test public void test_4754() { checkIsSubtype("null|{null f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_4755() { checkIsSubtype("null|{null f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_4756() { checkNotSubtype("null|{null f1}","{{null f1} f1}"); }
	@Test public void test_4757() { checkNotSubtype("null|{null f1}","{{null f2} f1}"); }
	@Test public void test_4758() { checkNotSubtype("null|{null f1}","{{null f1} f2}"); }
	@Test public void test_4759() { checkNotSubtype("null|{null f1}","{{null f2} f2}"); }
	@Test public void test_4760() { checkIsSubtype("null|{null f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4761() { checkIsSubtype("null|{null f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4762() { checkIsSubtype("null|{null f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4763() { checkIsSubtype("null|{null f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4764() { checkIsSubtype("null|{null f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4765() { checkIsSubtype("null|{null f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4766() { checkIsSubtype("null|{null f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4767() { checkIsSubtype("null|{null f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4768() { checkIsSubtype("null|{null f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4769() { checkIsSubtype("null|{null f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4770() { checkIsSubtype("null|{null f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4771() { checkIsSubtype("null|{null f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4772() { checkIsSubtype("null|{null f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4773() { checkIsSubtype("null|{null f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4774() { checkIsSubtype("null|{null f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4775() { checkIsSubtype("null|{null f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4776() { checkIsSubtype("null|{null f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4777() { checkIsSubtype("null|{null f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4778() { checkIsSubtype("null|{null f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4779() { checkIsSubtype("null|{null f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4780() { checkIsSubtype("null|{null f1}","{X<X|null> f1}"); }
	@Test public void test_4781() { checkNotSubtype("null|{null f1}","{X<X|null> f2}"); }
	@Test public void test_4782() { checkIsSubtype("null|{null f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4783() { checkIsSubtype("null|{null f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4784() { checkIsSubtype("null|{null f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4785() { checkIsSubtype("null|{null f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4786() { checkIsSubtype("null|{null f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4787() { checkIsSubtype("null|{null f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4788() { checkIsSubtype("null|{null f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4789() { checkIsSubtype("null|{null f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4790() { checkIsSubtype("null|{null f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4791() { checkIsSubtype("null|{null f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4792() { checkIsSubtype("null|{null f1}","null|null"); }
	@Test public void test_4793() { checkIsSubtype("null|{null f1}","null|X<{X f1}>"); }
	@Test public void test_4794() { checkIsSubtype("null|{null f1}","null|X<{X f2}>"); }
	@Test public void test_4795() { checkIsSubtype("null|{null f1}","null|{null f1}"); }
	@Test public void test_4796() { checkNotSubtype("null|{null f1}","null|{null f2}"); }
	@Test public void test_4797() { checkIsSubtype("null|{null f1}","null|(X<null|X>)"); }
	@Test public void test_4798() { checkIsSubtype("null|{null f1}","{null f1}|null"); }
	@Test public void test_4799() { checkNotSubtype("null|{null f1}","{null f2}|null"); }
	@Test public void test_4800() { checkIsSubtype("null|{null f1}","X<{X f1}>|null"); }
	@Test public void test_4801() { checkIsSubtype("null|{null f1}","X<{X f2}>|null"); }
	@Test public void test_4802() { checkIsSubtype("null|{null f1}","X<X|{null f1}>"); }
	@Test public void test_4803() { checkNotSubtype("null|{null f1}","X<X|{null f2}>"); }
	@Test public void test_4804() { checkIsSubtype("null|{null f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4805() { checkIsSubtype("null|{null f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4806() { checkIsSubtype("null|{null f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4807() { checkIsSubtype("null|{null f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4808() { checkIsSubtype("null|{null f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4809() { checkIsSubtype("null|{null f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4810() { checkIsSubtype("null|{null f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4811() { checkIsSubtype("null|{null f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4812() { checkIsSubtype("null|{null f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4813() { checkIsSubtype("null|{null f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4814() { checkIsSubtype("null|{null f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4815() { checkIsSubtype("null|{null f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4816() { checkIsSubtype("null|{null f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4817() { checkIsSubtype("null|{null f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4818() { checkIsSubtype("null|{null f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4819() { checkIsSubtype("null|{null f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4820() { checkIsSubtype("null|{null f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_4821() { checkIsSubtype("null|{null f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_4822() { checkIsSubtype("null|{null f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_4823() { checkIsSubtype("null|{null f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_4824() { checkIsSubtype("null|{null f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4825() { checkIsSubtype("null|{null f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4826() { checkIsSubtype("null|{null f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4827() { checkIsSubtype("null|{null f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4828() { checkIsSubtype("null|{null f1}","(X<X|null>)|null"); }
	@Test public void test_4829() { checkIsSubtype("null|{null f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_4830() { checkIsSubtype("null|{null f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4831() { checkIsSubtype("null|{null f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4832() { checkIsSubtype("null|{null f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4833() { checkIsSubtype("null|{null f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4834() { checkIsSubtype("null|{null f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4835() { checkIsSubtype("null|{null f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4836() { checkIsSubtype("null|{null f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4837() { checkIsSubtype("null|{null f2}","null"); }
	@Test public void test_4838() { checkIsSubtype("null|{null f2}","X<{X f1}>"); }
	@Test public void test_4839() { checkIsSubtype("null|{null f2}","X<{X f2}>"); }
	@Test public void test_4840() { checkNotSubtype("null|{null f2}","{null f1}"); }
	@Test public void test_4841() { checkIsSubtype("null|{null f2}","{null f2}"); }
	@Test public void test_4842() { checkIsSubtype("null|{null f2}","{X<{X f1}> f1}"); }
	@Test public void test_4843() { checkIsSubtype("null|{null f2}","{X<{X f2}> f1}"); }
	@Test public void test_4844() { checkIsSubtype("null|{null f2}","{X<{X f1}> f2}"); }
	@Test public void test_4845() { checkIsSubtype("null|{null f2}","{X<{X f2}> f2}"); }
	@Test public void test_4846() { checkIsSubtype("null|{null f2}","X<X|null>"); }
	@Test public void test_4847() { checkIsSubtype("null|{null f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_4848() { checkIsSubtype("null|{null f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_4849() { checkNotSubtype("null|{null f2}","{{null f1} f1}"); }
	@Test public void test_4850() { checkNotSubtype("null|{null f2}","{{null f2} f1}"); }
	@Test public void test_4851() { checkNotSubtype("null|{null f2}","{{null f1} f2}"); }
	@Test public void test_4852() { checkNotSubtype("null|{null f2}","{{null f2} f2}"); }
	@Test public void test_4853() { checkIsSubtype("null|{null f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4854() { checkIsSubtype("null|{null f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4855() { checkIsSubtype("null|{null f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4856() { checkIsSubtype("null|{null f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4857() { checkIsSubtype("null|{null f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4858() { checkIsSubtype("null|{null f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4859() { checkIsSubtype("null|{null f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4860() { checkIsSubtype("null|{null f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4861() { checkIsSubtype("null|{null f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4862() { checkIsSubtype("null|{null f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4863() { checkIsSubtype("null|{null f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4864() { checkIsSubtype("null|{null f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4865() { checkIsSubtype("null|{null f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4866() { checkIsSubtype("null|{null f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4867() { checkIsSubtype("null|{null f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4868() { checkIsSubtype("null|{null f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4869() { checkIsSubtype("null|{null f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4870() { checkIsSubtype("null|{null f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4871() { checkIsSubtype("null|{null f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4872() { checkIsSubtype("null|{null f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4873() { checkNotSubtype("null|{null f2}","{X<X|null> f1}"); }
	@Test public void test_4874() { checkIsSubtype("null|{null f2}","{X<X|null> f2}"); }
	@Test public void test_4875() { checkIsSubtype("null|{null f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4876() { checkIsSubtype("null|{null f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4877() { checkIsSubtype("null|{null f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4878() { checkIsSubtype("null|{null f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4879() { checkIsSubtype("null|{null f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4880() { checkIsSubtype("null|{null f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4881() { checkIsSubtype("null|{null f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4882() { checkIsSubtype("null|{null f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4883() { checkIsSubtype("null|{null f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4884() { checkIsSubtype("null|{null f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4885() { checkIsSubtype("null|{null f2}","null|null"); }
	@Test public void test_4886() { checkIsSubtype("null|{null f2}","null|X<{X f1}>"); }
	@Test public void test_4887() { checkIsSubtype("null|{null f2}","null|X<{X f2}>"); }
	@Test public void test_4888() { checkNotSubtype("null|{null f2}","null|{null f1}"); }
	@Test public void test_4889() { checkIsSubtype("null|{null f2}","null|{null f2}"); }
	@Test public void test_4890() { checkIsSubtype("null|{null f2}","null|(X<null|X>)"); }
	@Test public void test_4891() { checkNotSubtype("null|{null f2}","{null f1}|null"); }
	@Test public void test_4892() { checkIsSubtype("null|{null f2}","{null f2}|null"); }
	@Test public void test_4893() { checkIsSubtype("null|{null f2}","X<{X f1}>|null"); }
	@Test public void test_4894() { checkIsSubtype("null|{null f2}","X<{X f2}>|null"); }
	@Test public void test_4895() { checkNotSubtype("null|{null f2}","X<X|{null f1}>"); }
	@Test public void test_4896() { checkIsSubtype("null|{null f2}","X<X|{null f2}>"); }
	@Test public void test_4897() { checkIsSubtype("null|{null f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4898() { checkIsSubtype("null|{null f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4899() { checkIsSubtype("null|{null f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4900() { checkIsSubtype("null|{null f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4901() { checkIsSubtype("null|{null f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4902() { checkIsSubtype("null|{null f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4903() { checkIsSubtype("null|{null f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4904() { checkIsSubtype("null|{null f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4905() { checkIsSubtype("null|{null f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4906() { checkIsSubtype("null|{null f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_4907() { checkIsSubtype("null|{null f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_4908() { checkIsSubtype("null|{null f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_4909() { checkIsSubtype("null|{null f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_4910() { checkIsSubtype("null|{null f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_4911() { checkIsSubtype("null|{null f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_4912() { checkIsSubtype("null|{null f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_4913() { checkIsSubtype("null|{null f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_4914() { checkIsSubtype("null|{null f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_4915() { checkIsSubtype("null|{null f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_4916() { checkIsSubtype("null|{null f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_4917() { checkIsSubtype("null|{null f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_4918() { checkIsSubtype("null|{null f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_4919() { checkIsSubtype("null|{null f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_4920() { checkIsSubtype("null|{null f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_4921() { checkIsSubtype("null|{null f2}","(X<X|null>)|null"); }
	@Test public void test_4922() { checkIsSubtype("null|{null f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_4923() { checkIsSubtype("null|{null f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_4924() { checkIsSubtype("null|{null f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_4925() { checkIsSubtype("null|{null f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_4926() { checkIsSubtype("null|{null f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_4927() { checkIsSubtype("null|{null f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_4928() { checkIsSubtype("null|{null f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_4929() { checkIsSubtype("null|{null f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_4930() { checkIsSubtype("null|(X<null|X>)","null"); }
	@Test public void test_4931() { checkIsSubtype("null|(X<null|X>)","X<{X f1}>"); }
	@Test public void test_4932() { checkIsSubtype("null|(X<null|X>)","X<{X f2}>"); }
	@Test public void test_4933() { checkNotSubtype("null|(X<null|X>)","{null f1}"); }
	@Test public void test_4934() { checkNotSubtype("null|(X<null|X>)","{null f2}"); }
	@Test public void test_4935() { checkIsSubtype("null|(X<null|X>)","{X<{X f1}> f1}"); }
	@Test public void test_4936() { checkIsSubtype("null|(X<null|X>)","{X<{X f2}> f1}"); }
	@Test public void test_4937() { checkIsSubtype("null|(X<null|X>)","{X<{X f1}> f2}"); }
	@Test public void test_4938() { checkIsSubtype("null|(X<null|X>)","{X<{X f2}> f2}"); }
	@Test public void test_4939() { checkIsSubtype("null|(X<null|X>)","X<X|null>"); }
	@Test public void test_4940() { checkIsSubtype("null|(X<null|X>)","X<X|Y<{Y f1}>>"); }
	@Test public void test_4941() { checkIsSubtype("null|(X<null|X>)","X<X|Y<{Y f2}>>"); }
	@Test public void test_4942() { checkNotSubtype("null|(X<null|X>)","{{null f1} f1}"); }
	@Test public void test_4943() { checkNotSubtype("null|(X<null|X>)","{{null f2} f1}"); }
	@Test public void test_4944() { checkNotSubtype("null|(X<null|X>)","{{null f1} f2}"); }
	@Test public void test_4945() { checkNotSubtype("null|(X<null|X>)","{{null f2} f2}"); }
	@Test public void test_4946() { checkIsSubtype("null|(X<null|X>)","{{X<{X f1}> f1} f1}"); }
	@Test public void test_4947() { checkIsSubtype("null|(X<null|X>)","{{X<{X f2}> f1} f1}"); }
	@Test public void test_4948() { checkIsSubtype("null|(X<null|X>)","{{X<{X f1}> f2} f1}"); }
	@Test public void test_4949() { checkIsSubtype("null|(X<null|X>)","{{X<{X f2}> f2} f1}"); }
	@Test public void test_4950() { checkIsSubtype("null|(X<null|X>)","{{X<{X f1}> f1} f2}"); }
	@Test public void test_4951() { checkIsSubtype("null|(X<null|X>)","{{X<{X f2}> f1} f2}"); }
	@Test public void test_4952() { checkIsSubtype("null|(X<null|X>)","{{X<{X f1}> f2} f2}"); }
	@Test public void test_4953() { checkIsSubtype("null|(X<null|X>)","{{X<{X f2}> f2} f2}"); }
	@Test public void test_4954() { checkIsSubtype("null|(X<null|X>)","X<{{{X f1} f1} f1}>"); }
	@Test public void test_4955() { checkIsSubtype("null|(X<null|X>)","X<{{{X f2} f1} f1}>"); }
	@Test public void test_4956() { checkIsSubtype("null|(X<null|X>)","X<{{{X f1} f2} f1}>"); }
	@Test public void test_4957() { checkIsSubtype("null|(X<null|X>)","X<{{{X f2} f2} f1}>"); }
	@Test public void test_4958() { checkIsSubtype("null|(X<null|X>)","X<{{{X f1} f1} f2}>"); }
	@Test public void test_4959() { checkIsSubtype("null|(X<null|X>)","X<{{{X f2} f1} f2}>"); }
	@Test public void test_4960() { checkIsSubtype("null|(X<null|X>)","X<{{{X f1} f2} f2}>"); }
	@Test public void test_4961() { checkIsSubtype("null|(X<null|X>)","X<{{{X f2} f2} f2}>"); }
	@Test public void test_4962() { checkIsSubtype("null|(X<null|X>)","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_4963() { checkIsSubtype("null|(X<null|X>)","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_4964() { checkIsSubtype("null|(X<null|X>)","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_4965() { checkIsSubtype("null|(X<null|X>)","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_4966() { checkNotSubtype("null|(X<null|X>)","{X<X|null> f1}"); }
	@Test public void test_4967() { checkNotSubtype("null|(X<null|X>)","{X<X|null> f2}"); }
	@Test public void test_4968() { checkIsSubtype("null|(X<null|X>)","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_4969() { checkIsSubtype("null|(X<null|X>)","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_4970() { checkIsSubtype("null|(X<null|X>)","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_4971() { checkIsSubtype("null|(X<null|X>)","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_4972() { checkIsSubtype("null|(X<null|X>)","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_4973() { checkIsSubtype("null|(X<null|X>)","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_4974() { checkIsSubtype("null|(X<null|X>)","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_4975() { checkIsSubtype("null|(X<null|X>)","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_4976() { checkIsSubtype("null|(X<null|X>)","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_4977() { checkIsSubtype("null|(X<null|X>)","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_4978() { checkIsSubtype("null|(X<null|X>)","null|null"); }
	@Test public void test_4979() { checkIsSubtype("null|(X<null|X>)","null|X<{X f1}>"); }
	@Test public void test_4980() { checkIsSubtype("null|(X<null|X>)","null|X<{X f2}>"); }
	@Test public void test_4981() { checkNotSubtype("null|(X<null|X>)","null|{null f1}"); }
	@Test public void test_4982() { checkNotSubtype("null|(X<null|X>)","null|{null f2}"); }
	@Test public void test_4983() { checkIsSubtype("null|(X<null|X>)","null|(X<null|X>)"); }
	@Test public void test_4984() { checkNotSubtype("null|(X<null|X>)","{null f1}|null"); }
	@Test public void test_4985() { checkNotSubtype("null|(X<null|X>)","{null f2}|null"); }
	@Test public void test_4986() { checkIsSubtype("null|(X<null|X>)","X<{X f1}>|null"); }
	@Test public void test_4987() { checkIsSubtype("null|(X<null|X>)","X<{X f2}>|null"); }
	@Test public void test_4988() { checkNotSubtype("null|(X<null|X>)","X<X|{null f1}>"); }
	@Test public void test_4989() { checkNotSubtype("null|(X<null|X>)","X<X|{null f2}>"); }
	@Test public void test_4990() { checkIsSubtype("null|(X<null|X>)","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_4991() { checkIsSubtype("null|(X<null|X>)","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_4992() { checkIsSubtype("null|(X<null|X>)","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_4993() { checkIsSubtype("null|(X<null|X>)","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_4994() { checkIsSubtype("null|(X<null|X>)","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_4995() { checkIsSubtype("null|(X<null|X>)","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_4996() { checkIsSubtype("null|(X<null|X>)","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_4997() { checkIsSubtype("null|(X<null|X>)","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_4998() { checkIsSubtype("null|(X<null|X>)","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_4999() { checkIsSubtype("null|(X<null|X>)","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5000() { checkIsSubtype("null|(X<null|X>)","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5001() { checkIsSubtype("null|(X<null|X>)","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5002() { checkIsSubtype("null|(X<null|X>)","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5003() { checkIsSubtype("null|(X<null|X>)","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5004() { checkIsSubtype("null|(X<null|X>)","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5005() { checkIsSubtype("null|(X<null|X>)","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5006() { checkIsSubtype("null|(X<null|X>)","X<X|{{X f1} f1}>"); }
	@Test public void test_5007() { checkIsSubtype("null|(X<null|X>)","X<X|{{X f2} f1}>"); }
	@Test public void test_5008() { checkIsSubtype("null|(X<null|X>)","X<X|{{X f1} f2}>"); }
	@Test public void test_5009() { checkIsSubtype("null|(X<null|X>)","X<X|{{X f2} f2}>"); }
	@Test public void test_5010() { checkIsSubtype("null|(X<null|X>)","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5011() { checkIsSubtype("null|(X<null|X>)","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5012() { checkIsSubtype("null|(X<null|X>)","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5013() { checkIsSubtype("null|(X<null|X>)","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5014() { checkIsSubtype("null|(X<null|X>)","(X<X|null>)|null"); }
	@Test public void test_5015() { checkIsSubtype("null|(X<null|X>)","X<X|(Y<Y|null>)>"); }
	@Test public void test_5016() { checkIsSubtype("null|(X<null|X>)","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5017() { checkIsSubtype("null|(X<null|X>)","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5018() { checkIsSubtype("null|(X<null|X>)","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5019() { checkIsSubtype("null|(X<null|X>)","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5020() { checkIsSubtype("null|(X<null|X>)","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5021() { checkIsSubtype("null|(X<null|X>)","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5022() { checkIsSubtype("null|(X<null|X>)","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5023() { checkIsSubtype("{null f1}|null","null"); }
	@Test public void test_5024() { checkIsSubtype("{null f1}|null","X<{X f1}>"); }
	@Test public void test_5025() { checkIsSubtype("{null f1}|null","X<{X f2}>"); }
	@Test public void test_5026() { checkIsSubtype("{null f1}|null","{null f1}"); }
	@Test public void test_5027() { checkNotSubtype("{null f1}|null","{null f2}"); }
	@Test public void test_5028() { checkIsSubtype("{null f1}|null","{X<{X f1}> f1}"); }
	@Test public void test_5029() { checkIsSubtype("{null f1}|null","{X<{X f2}> f1}"); }
	@Test public void test_5030() { checkIsSubtype("{null f1}|null","{X<{X f1}> f2}"); }
	@Test public void test_5031() { checkIsSubtype("{null f1}|null","{X<{X f2}> f2}"); }
	@Test public void test_5032() { checkIsSubtype("{null f1}|null","X<X|null>"); }
	@Test public void test_5033() { checkIsSubtype("{null f1}|null","X<X|Y<{Y f1}>>"); }
	@Test public void test_5034() { checkIsSubtype("{null f1}|null","X<X|Y<{Y f2}>>"); }
	@Test public void test_5035() { checkNotSubtype("{null f1}|null","{{null f1} f1}"); }
	@Test public void test_5036() { checkNotSubtype("{null f1}|null","{{null f2} f1}"); }
	@Test public void test_5037() { checkNotSubtype("{null f1}|null","{{null f1} f2}"); }
	@Test public void test_5038() { checkNotSubtype("{null f1}|null","{{null f2} f2}"); }
	@Test public void test_5039() { checkIsSubtype("{null f1}|null","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5040() { checkIsSubtype("{null f1}|null","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5041() { checkIsSubtype("{null f1}|null","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5042() { checkIsSubtype("{null f1}|null","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5043() { checkIsSubtype("{null f1}|null","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5044() { checkIsSubtype("{null f1}|null","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5045() { checkIsSubtype("{null f1}|null","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5046() { checkIsSubtype("{null f1}|null","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5047() { checkIsSubtype("{null f1}|null","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5048() { checkIsSubtype("{null f1}|null","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5049() { checkIsSubtype("{null f1}|null","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5050() { checkIsSubtype("{null f1}|null","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5051() { checkIsSubtype("{null f1}|null","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5052() { checkIsSubtype("{null f1}|null","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5053() { checkIsSubtype("{null f1}|null","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5054() { checkIsSubtype("{null f1}|null","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5055() { checkIsSubtype("{null f1}|null","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5056() { checkIsSubtype("{null f1}|null","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5057() { checkIsSubtype("{null f1}|null","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5058() { checkIsSubtype("{null f1}|null","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5059() { checkIsSubtype("{null f1}|null","{X<X|null> f1}"); }
	@Test public void test_5060() { checkNotSubtype("{null f1}|null","{X<X|null> f2}"); }
	@Test public void test_5061() { checkIsSubtype("{null f1}|null","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5062() { checkIsSubtype("{null f1}|null","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5063() { checkIsSubtype("{null f1}|null","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5064() { checkIsSubtype("{null f1}|null","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5065() { checkIsSubtype("{null f1}|null","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5066() { checkIsSubtype("{null f1}|null","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5067() { checkIsSubtype("{null f1}|null","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5068() { checkIsSubtype("{null f1}|null","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5069() { checkIsSubtype("{null f1}|null","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5070() { checkIsSubtype("{null f1}|null","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5071() { checkIsSubtype("{null f1}|null","null|null"); }
	@Test public void test_5072() { checkIsSubtype("{null f1}|null","null|X<{X f1}>"); }
	@Test public void test_5073() { checkIsSubtype("{null f1}|null","null|X<{X f2}>"); }
	@Test public void test_5074() { checkIsSubtype("{null f1}|null","null|{null f1}"); }
	@Test public void test_5075() { checkNotSubtype("{null f1}|null","null|{null f2}"); }
	@Test public void test_5076() { checkIsSubtype("{null f1}|null","null|(X<null|X>)"); }
	@Test public void test_5077() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test public void test_5078() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test public void test_5079() { checkIsSubtype("{null f1}|null","X<{X f1}>|null"); }
	@Test public void test_5080() { checkIsSubtype("{null f1}|null","X<{X f2}>|null"); }
	@Test public void test_5081() { checkIsSubtype("{null f1}|null","X<X|{null f1}>"); }
	@Test public void test_5082() { checkNotSubtype("{null f1}|null","X<X|{null f2}>"); }
	@Test public void test_5083() { checkIsSubtype("{null f1}|null","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5084() { checkIsSubtype("{null f1}|null","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5085() { checkIsSubtype("{null f1}|null","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5086() { checkIsSubtype("{null f1}|null","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5087() { checkIsSubtype("{null f1}|null","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5088() { checkIsSubtype("{null f1}|null","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5089() { checkIsSubtype("{null f1}|null","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5090() { checkIsSubtype("{null f1}|null","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5091() { checkIsSubtype("{null f1}|null","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5092() { checkIsSubtype("{null f1}|null","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5093() { checkIsSubtype("{null f1}|null","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5094() { checkIsSubtype("{null f1}|null","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5095() { checkIsSubtype("{null f1}|null","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5096() { checkIsSubtype("{null f1}|null","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5097() { checkIsSubtype("{null f1}|null","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5098() { checkIsSubtype("{null f1}|null","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5099() { checkIsSubtype("{null f1}|null","X<X|{{X f1} f1}>"); }
	@Test public void test_5100() { checkIsSubtype("{null f1}|null","X<X|{{X f2} f1}>"); }
	@Test public void test_5101() { checkIsSubtype("{null f1}|null","X<X|{{X f1} f2}>"); }
	@Test public void test_5102() { checkIsSubtype("{null f1}|null","X<X|{{X f2} f2}>"); }
	@Test public void test_5103() { checkIsSubtype("{null f1}|null","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5104() { checkIsSubtype("{null f1}|null","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5105() { checkIsSubtype("{null f1}|null","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5106() { checkIsSubtype("{null f1}|null","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5107() { checkIsSubtype("{null f1}|null","(X<X|null>)|null"); }
	@Test public void test_5108() { checkIsSubtype("{null f1}|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_5109() { checkIsSubtype("{null f1}|null","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5110() { checkIsSubtype("{null f1}|null","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5111() { checkIsSubtype("{null f1}|null","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5112() { checkIsSubtype("{null f1}|null","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5113() { checkIsSubtype("{null f1}|null","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5114() { checkIsSubtype("{null f1}|null","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5115() { checkIsSubtype("{null f1}|null","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5116() { checkIsSubtype("{null f2}|null","null"); }
	@Test public void test_5117() { checkIsSubtype("{null f2}|null","X<{X f1}>"); }
	@Test public void test_5118() { checkIsSubtype("{null f2}|null","X<{X f2}>"); }
	@Test public void test_5119() { checkNotSubtype("{null f2}|null","{null f1}"); }
	@Test public void test_5120() { checkIsSubtype("{null f2}|null","{null f2}"); }
	@Test public void test_5121() { checkIsSubtype("{null f2}|null","{X<{X f1}> f1}"); }
	@Test public void test_5122() { checkIsSubtype("{null f2}|null","{X<{X f2}> f1}"); }
	@Test public void test_5123() { checkIsSubtype("{null f2}|null","{X<{X f1}> f2}"); }
	@Test public void test_5124() { checkIsSubtype("{null f2}|null","{X<{X f2}> f2}"); }
	@Test public void test_5125() { checkIsSubtype("{null f2}|null","X<X|null>"); }
	@Test public void test_5126() { checkIsSubtype("{null f2}|null","X<X|Y<{Y f1}>>"); }
	@Test public void test_5127() { checkIsSubtype("{null f2}|null","X<X|Y<{Y f2}>>"); }
	@Test public void test_5128() { checkNotSubtype("{null f2}|null","{{null f1} f1}"); }
	@Test public void test_5129() { checkNotSubtype("{null f2}|null","{{null f2} f1}"); }
	@Test public void test_5130() { checkNotSubtype("{null f2}|null","{{null f1} f2}"); }
	@Test public void test_5131() { checkNotSubtype("{null f2}|null","{{null f2} f2}"); }
	@Test public void test_5132() { checkIsSubtype("{null f2}|null","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5133() { checkIsSubtype("{null f2}|null","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5134() { checkIsSubtype("{null f2}|null","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5135() { checkIsSubtype("{null f2}|null","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5136() { checkIsSubtype("{null f2}|null","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5137() { checkIsSubtype("{null f2}|null","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5138() { checkIsSubtype("{null f2}|null","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5139() { checkIsSubtype("{null f2}|null","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5140() { checkIsSubtype("{null f2}|null","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5141() { checkIsSubtype("{null f2}|null","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5142() { checkIsSubtype("{null f2}|null","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5143() { checkIsSubtype("{null f2}|null","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5144() { checkIsSubtype("{null f2}|null","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5145() { checkIsSubtype("{null f2}|null","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5146() { checkIsSubtype("{null f2}|null","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5147() { checkIsSubtype("{null f2}|null","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5148() { checkIsSubtype("{null f2}|null","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5149() { checkIsSubtype("{null f2}|null","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5150() { checkIsSubtype("{null f2}|null","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5151() { checkIsSubtype("{null f2}|null","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5152() { checkNotSubtype("{null f2}|null","{X<X|null> f1}"); }
	@Test public void test_5153() { checkIsSubtype("{null f2}|null","{X<X|null> f2}"); }
	@Test public void test_5154() { checkIsSubtype("{null f2}|null","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5155() { checkIsSubtype("{null f2}|null","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5156() { checkIsSubtype("{null f2}|null","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5157() { checkIsSubtype("{null f2}|null","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5158() { checkIsSubtype("{null f2}|null","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5159() { checkIsSubtype("{null f2}|null","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5160() { checkIsSubtype("{null f2}|null","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5161() { checkIsSubtype("{null f2}|null","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5162() { checkIsSubtype("{null f2}|null","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5163() { checkIsSubtype("{null f2}|null","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5164() { checkIsSubtype("{null f2}|null","null|null"); }
	@Test public void test_5165() { checkIsSubtype("{null f2}|null","null|X<{X f1}>"); }
	@Test public void test_5166() { checkIsSubtype("{null f2}|null","null|X<{X f2}>"); }
	@Test public void test_5167() { checkNotSubtype("{null f2}|null","null|{null f1}"); }
	@Test public void test_5168() { checkIsSubtype("{null f2}|null","null|{null f2}"); }
	@Test public void test_5169() { checkIsSubtype("{null f2}|null","null|(X<null|X>)"); }
	@Test public void test_5170() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test public void test_5171() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test public void test_5172() { checkIsSubtype("{null f2}|null","X<{X f1}>|null"); }
	@Test public void test_5173() { checkIsSubtype("{null f2}|null","X<{X f2}>|null"); }
	@Test public void test_5174() { checkNotSubtype("{null f2}|null","X<X|{null f1}>"); }
	@Test public void test_5175() { checkIsSubtype("{null f2}|null","X<X|{null f2}>"); }
	@Test public void test_5176() { checkIsSubtype("{null f2}|null","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5177() { checkIsSubtype("{null f2}|null","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5178() { checkIsSubtype("{null f2}|null","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5179() { checkIsSubtype("{null f2}|null","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5180() { checkIsSubtype("{null f2}|null","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5181() { checkIsSubtype("{null f2}|null","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5182() { checkIsSubtype("{null f2}|null","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5183() { checkIsSubtype("{null f2}|null","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5184() { checkIsSubtype("{null f2}|null","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5185() { checkIsSubtype("{null f2}|null","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5186() { checkIsSubtype("{null f2}|null","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5187() { checkIsSubtype("{null f2}|null","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5188() { checkIsSubtype("{null f2}|null","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5189() { checkIsSubtype("{null f2}|null","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5190() { checkIsSubtype("{null f2}|null","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5191() { checkIsSubtype("{null f2}|null","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5192() { checkIsSubtype("{null f2}|null","X<X|{{X f1} f1}>"); }
	@Test public void test_5193() { checkIsSubtype("{null f2}|null","X<X|{{X f2} f1}>"); }
	@Test public void test_5194() { checkIsSubtype("{null f2}|null","X<X|{{X f1} f2}>"); }
	@Test public void test_5195() { checkIsSubtype("{null f2}|null","X<X|{{X f2} f2}>"); }
	@Test public void test_5196() { checkIsSubtype("{null f2}|null","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5197() { checkIsSubtype("{null f2}|null","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5198() { checkIsSubtype("{null f2}|null","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5199() { checkIsSubtype("{null f2}|null","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5200() { checkIsSubtype("{null f2}|null","(X<X|null>)|null"); }
	@Test public void test_5201() { checkIsSubtype("{null f2}|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_5202() { checkIsSubtype("{null f2}|null","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5203() { checkIsSubtype("{null f2}|null","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5204() { checkIsSubtype("{null f2}|null","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5205() { checkIsSubtype("{null f2}|null","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5206() { checkIsSubtype("{null f2}|null","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5207() { checkIsSubtype("{null f2}|null","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5208() { checkIsSubtype("{null f2}|null","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5209() { checkIsSubtype("X<{X f1}>|null","null"); }
	@Test public void test_5210() { checkIsSubtype("X<{X f1}>|null","X<{X f1}>"); }
	@Test public void test_5211() { checkIsSubtype("X<{X f1}>|null","X<{X f2}>"); }
	@Test public void test_5212() { checkNotSubtype("X<{X f1}>|null","{null f1}"); }
	@Test public void test_5213() { checkNotSubtype("X<{X f1}>|null","{null f2}"); }
	@Test public void test_5214() { checkIsSubtype("X<{X f1}>|null","{X<{X f1}> f1}"); }
	@Test public void test_5215() { checkIsSubtype("X<{X f1}>|null","{X<{X f2}> f1}"); }
	@Test public void test_5216() { checkIsSubtype("X<{X f1}>|null","{X<{X f1}> f2}"); }
	@Test public void test_5217() { checkIsSubtype("X<{X f1}>|null","{X<{X f2}> f2}"); }
	@Test public void test_5218() { checkIsSubtype("X<{X f1}>|null","X<X|null>"); }
	@Test public void test_5219() { checkIsSubtype("X<{X f1}>|null","X<X|Y<{Y f1}>>"); }
	@Test public void test_5220() { checkIsSubtype("X<{X f1}>|null","X<X|Y<{Y f2}>>"); }
	@Test public void test_5221() { checkNotSubtype("X<{X f1}>|null","{{null f1} f1}"); }
	@Test public void test_5222() { checkNotSubtype("X<{X f1}>|null","{{null f2} f1}"); }
	@Test public void test_5223() { checkNotSubtype("X<{X f1}>|null","{{null f1} f2}"); }
	@Test public void test_5224() { checkNotSubtype("X<{X f1}>|null","{{null f2} f2}"); }
	@Test public void test_5225() { checkIsSubtype("X<{X f1}>|null","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5226() { checkIsSubtype("X<{X f1}>|null","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5227() { checkIsSubtype("X<{X f1}>|null","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5228() { checkIsSubtype("X<{X f1}>|null","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5229() { checkIsSubtype("X<{X f1}>|null","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5230() { checkIsSubtype("X<{X f1}>|null","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5231() { checkIsSubtype("X<{X f1}>|null","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5232() { checkIsSubtype("X<{X f1}>|null","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5233() { checkIsSubtype("X<{X f1}>|null","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5234() { checkIsSubtype("X<{X f1}>|null","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5235() { checkIsSubtype("X<{X f1}>|null","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5236() { checkIsSubtype("X<{X f1}>|null","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5237() { checkIsSubtype("X<{X f1}>|null","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5238() { checkIsSubtype("X<{X f1}>|null","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5239() { checkIsSubtype("X<{X f1}>|null","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5240() { checkIsSubtype("X<{X f1}>|null","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5241() { checkIsSubtype("X<{X f1}>|null","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5242() { checkIsSubtype("X<{X f1}>|null","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5243() { checkIsSubtype("X<{X f1}>|null","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5244() { checkIsSubtype("X<{X f1}>|null","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5245() { checkNotSubtype("X<{X f1}>|null","{X<X|null> f1}"); }
	@Test public void test_5246() { checkNotSubtype("X<{X f1}>|null","{X<X|null> f2}"); }
	@Test public void test_5247() { checkIsSubtype("X<{X f1}>|null","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5248() { checkIsSubtype("X<{X f1}>|null","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5249() { checkIsSubtype("X<{X f1}>|null","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5250() { checkIsSubtype("X<{X f1}>|null","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5251() { checkIsSubtype("X<{X f1}>|null","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5252() { checkIsSubtype("X<{X f1}>|null","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5253() { checkIsSubtype("X<{X f1}>|null","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5254() { checkIsSubtype("X<{X f1}>|null","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5255() { checkIsSubtype("X<{X f1}>|null","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5256() { checkIsSubtype("X<{X f1}>|null","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5257() { checkIsSubtype("X<{X f1}>|null","null|null"); }
	@Test public void test_5258() { checkIsSubtype("X<{X f1}>|null","null|X<{X f1}>"); }
	@Test public void test_5259() { checkIsSubtype("X<{X f1}>|null","null|X<{X f2}>"); }
	@Test public void test_5260() { checkNotSubtype("X<{X f1}>|null","null|{null f1}"); }
	@Test public void test_5261() { checkNotSubtype("X<{X f1}>|null","null|{null f2}"); }
	@Test public void test_5262() { checkIsSubtype("X<{X f1}>|null","null|(X<null|X>)"); }
	@Test public void test_5263() { checkNotSubtype("X<{X f1}>|null","{null f1}|null"); }
	@Test public void test_5264() { checkNotSubtype("X<{X f1}>|null","{null f2}|null"); }
	@Test public void test_5265() { checkIsSubtype("X<{X f1}>|null","X<{X f1}>|null"); }
	@Test public void test_5266() { checkIsSubtype("X<{X f1}>|null","X<{X f2}>|null"); }
	@Test public void test_5267() { checkNotSubtype("X<{X f1}>|null","X<X|{null f1}>"); }
	@Test public void test_5268() { checkNotSubtype("X<{X f1}>|null","X<X|{null f2}>"); }
	@Test public void test_5269() { checkIsSubtype("X<{X f1}>|null","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5270() { checkIsSubtype("X<{X f1}>|null","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5271() { checkIsSubtype("X<{X f1}>|null","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5272() { checkIsSubtype("X<{X f1}>|null","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5273() { checkIsSubtype("X<{X f1}>|null","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5274() { checkIsSubtype("X<{X f1}>|null","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5275() { checkIsSubtype("X<{X f1}>|null","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5276() { checkIsSubtype("X<{X f1}>|null","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5277() { checkIsSubtype("X<{X f1}>|null","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5278() { checkIsSubtype("X<{X f1}>|null","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5279() { checkIsSubtype("X<{X f1}>|null","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5280() { checkIsSubtype("X<{X f1}>|null","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5281() { checkIsSubtype("X<{X f1}>|null","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5282() { checkIsSubtype("X<{X f1}>|null","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5283() { checkIsSubtype("X<{X f1}>|null","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5284() { checkIsSubtype("X<{X f1}>|null","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5285() { checkIsSubtype("X<{X f1}>|null","X<X|{{X f1} f1}>"); }
	@Test public void test_5286() { checkIsSubtype("X<{X f1}>|null","X<X|{{X f2} f1}>"); }
	@Test public void test_5287() { checkIsSubtype("X<{X f1}>|null","X<X|{{X f1} f2}>"); }
	@Test public void test_5288() { checkIsSubtype("X<{X f1}>|null","X<X|{{X f2} f2}>"); }
	@Test public void test_5289() { checkIsSubtype("X<{X f1}>|null","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5290() { checkIsSubtype("X<{X f1}>|null","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5291() { checkIsSubtype("X<{X f1}>|null","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5292() { checkIsSubtype("X<{X f1}>|null","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5293() { checkIsSubtype("X<{X f1}>|null","(X<X|null>)|null"); }
	@Test public void test_5294() { checkIsSubtype("X<{X f1}>|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_5295() { checkIsSubtype("X<{X f1}>|null","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5296() { checkIsSubtype("X<{X f1}>|null","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5297() { checkIsSubtype("X<{X f1}>|null","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5298() { checkIsSubtype("X<{X f1}>|null","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5299() { checkIsSubtype("X<{X f1}>|null","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5300() { checkIsSubtype("X<{X f1}>|null","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5301() { checkIsSubtype("X<{X f1}>|null","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5302() { checkIsSubtype("X<{X f2}>|null","null"); }
	@Test public void test_5303() { checkIsSubtype("X<{X f2}>|null","X<{X f1}>"); }
	@Test public void test_5304() { checkIsSubtype("X<{X f2}>|null","X<{X f2}>"); }
	@Test public void test_5305() { checkNotSubtype("X<{X f2}>|null","{null f1}"); }
	@Test public void test_5306() { checkNotSubtype("X<{X f2}>|null","{null f2}"); }
	@Test public void test_5307() { checkIsSubtype("X<{X f2}>|null","{X<{X f1}> f1}"); }
	@Test public void test_5308() { checkIsSubtype("X<{X f2}>|null","{X<{X f2}> f1}"); }
	@Test public void test_5309() { checkIsSubtype("X<{X f2}>|null","{X<{X f1}> f2}"); }
	@Test public void test_5310() { checkIsSubtype("X<{X f2}>|null","{X<{X f2}> f2}"); }
	@Test public void test_5311() { checkIsSubtype("X<{X f2}>|null","X<X|null>"); }
	@Test public void test_5312() { checkIsSubtype("X<{X f2}>|null","X<X|Y<{Y f1}>>"); }
	@Test public void test_5313() { checkIsSubtype("X<{X f2}>|null","X<X|Y<{Y f2}>>"); }
	@Test public void test_5314() { checkNotSubtype("X<{X f2}>|null","{{null f1} f1}"); }
	@Test public void test_5315() { checkNotSubtype("X<{X f2}>|null","{{null f2} f1}"); }
	@Test public void test_5316() { checkNotSubtype("X<{X f2}>|null","{{null f1} f2}"); }
	@Test public void test_5317() { checkNotSubtype("X<{X f2}>|null","{{null f2} f2}"); }
	@Test public void test_5318() { checkIsSubtype("X<{X f2}>|null","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5319() { checkIsSubtype("X<{X f2}>|null","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5320() { checkIsSubtype("X<{X f2}>|null","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5321() { checkIsSubtype("X<{X f2}>|null","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5322() { checkIsSubtype("X<{X f2}>|null","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5323() { checkIsSubtype("X<{X f2}>|null","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5324() { checkIsSubtype("X<{X f2}>|null","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5325() { checkIsSubtype("X<{X f2}>|null","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5326() { checkIsSubtype("X<{X f2}>|null","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5327() { checkIsSubtype("X<{X f2}>|null","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5328() { checkIsSubtype("X<{X f2}>|null","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5329() { checkIsSubtype("X<{X f2}>|null","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5330() { checkIsSubtype("X<{X f2}>|null","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5331() { checkIsSubtype("X<{X f2}>|null","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5332() { checkIsSubtype("X<{X f2}>|null","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5333() { checkIsSubtype("X<{X f2}>|null","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5334() { checkIsSubtype("X<{X f2}>|null","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5335() { checkIsSubtype("X<{X f2}>|null","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5336() { checkIsSubtype("X<{X f2}>|null","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5337() { checkIsSubtype("X<{X f2}>|null","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5338() { checkNotSubtype("X<{X f2}>|null","{X<X|null> f1}"); }
	@Test public void test_5339() { checkNotSubtype("X<{X f2}>|null","{X<X|null> f2}"); }
	@Test public void test_5340() { checkIsSubtype("X<{X f2}>|null","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5341() { checkIsSubtype("X<{X f2}>|null","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5342() { checkIsSubtype("X<{X f2}>|null","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5343() { checkIsSubtype("X<{X f2}>|null","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5344() { checkIsSubtype("X<{X f2}>|null","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5345() { checkIsSubtype("X<{X f2}>|null","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5346() { checkIsSubtype("X<{X f2}>|null","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5347() { checkIsSubtype("X<{X f2}>|null","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5348() { checkIsSubtype("X<{X f2}>|null","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5349() { checkIsSubtype("X<{X f2}>|null","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5350() { checkIsSubtype("X<{X f2}>|null","null|null"); }
	@Test public void test_5351() { checkIsSubtype("X<{X f2}>|null","null|X<{X f1}>"); }
	@Test public void test_5352() { checkIsSubtype("X<{X f2}>|null","null|X<{X f2}>"); }
	@Test public void test_5353() { checkNotSubtype("X<{X f2}>|null","null|{null f1}"); }
	@Test public void test_5354() { checkNotSubtype("X<{X f2}>|null","null|{null f2}"); }
	@Test public void test_5355() { checkIsSubtype("X<{X f2}>|null","null|(X<null|X>)"); }
	@Test public void test_5356() { checkNotSubtype("X<{X f2}>|null","{null f1}|null"); }
	@Test public void test_5357() { checkNotSubtype("X<{X f2}>|null","{null f2}|null"); }
	@Test public void test_5358() { checkIsSubtype("X<{X f2}>|null","X<{X f1}>|null"); }
	@Test public void test_5359() { checkIsSubtype("X<{X f2}>|null","X<{X f2}>|null"); }
	@Test public void test_5360() { checkNotSubtype("X<{X f2}>|null","X<X|{null f1}>"); }
	@Test public void test_5361() { checkNotSubtype("X<{X f2}>|null","X<X|{null f2}>"); }
	@Test public void test_5362() { checkIsSubtype("X<{X f2}>|null","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5363() { checkIsSubtype("X<{X f2}>|null","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5364() { checkIsSubtype("X<{X f2}>|null","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5365() { checkIsSubtype("X<{X f2}>|null","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5366() { checkIsSubtype("X<{X f2}>|null","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5367() { checkIsSubtype("X<{X f2}>|null","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5368() { checkIsSubtype("X<{X f2}>|null","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5369() { checkIsSubtype("X<{X f2}>|null","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5370() { checkIsSubtype("X<{X f2}>|null","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5371() { checkIsSubtype("X<{X f2}>|null","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5372() { checkIsSubtype("X<{X f2}>|null","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5373() { checkIsSubtype("X<{X f2}>|null","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5374() { checkIsSubtype("X<{X f2}>|null","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5375() { checkIsSubtype("X<{X f2}>|null","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5376() { checkIsSubtype("X<{X f2}>|null","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5377() { checkIsSubtype("X<{X f2}>|null","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5378() { checkIsSubtype("X<{X f2}>|null","X<X|{{X f1} f1}>"); }
	@Test public void test_5379() { checkIsSubtype("X<{X f2}>|null","X<X|{{X f2} f1}>"); }
	@Test public void test_5380() { checkIsSubtype("X<{X f2}>|null","X<X|{{X f1} f2}>"); }
	@Test public void test_5381() { checkIsSubtype("X<{X f2}>|null","X<X|{{X f2} f2}>"); }
	@Test public void test_5382() { checkIsSubtype("X<{X f2}>|null","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5383() { checkIsSubtype("X<{X f2}>|null","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5384() { checkIsSubtype("X<{X f2}>|null","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5385() { checkIsSubtype("X<{X f2}>|null","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5386() { checkIsSubtype("X<{X f2}>|null","(X<X|null>)|null"); }
	@Test public void test_5387() { checkIsSubtype("X<{X f2}>|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_5388() { checkIsSubtype("X<{X f2}>|null","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5389() { checkIsSubtype("X<{X f2}>|null","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5390() { checkIsSubtype("X<{X f2}>|null","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5391() { checkIsSubtype("X<{X f2}>|null","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5392() { checkIsSubtype("X<{X f2}>|null","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5393() { checkIsSubtype("X<{X f2}>|null","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5394() { checkIsSubtype("X<{X f2}>|null","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5395() { checkNotSubtype("X<X|{null f1}>","null"); }
	@Test public void test_5396() { checkIsSubtype("X<X|{null f1}>","X<{X f1}>"); }
	@Test public void test_5397() { checkIsSubtype("X<X|{null f1}>","X<{X f2}>"); }
	@Test public void test_5398() { checkIsSubtype("X<X|{null f1}>","{null f1}"); }
	@Test public void test_5399() { checkNotSubtype("X<X|{null f1}>","{null f2}"); }
	@Test public void test_5400() { checkIsSubtype("X<X|{null f1}>","{X<{X f1}> f1}"); }
	@Test public void test_5401() { checkIsSubtype("X<X|{null f1}>","{X<{X f2}> f1}"); }
	@Test public void test_5402() { checkIsSubtype("X<X|{null f1}>","{X<{X f1}> f2}"); }
	@Test public void test_5403() { checkIsSubtype("X<X|{null f1}>","{X<{X f2}> f2}"); }
	@Test public void test_5404() { checkNotSubtype("X<X|{null f1}>","X<X|null>"); }
	@Test public void test_5405() { checkIsSubtype("X<X|{null f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_5406() { checkIsSubtype("X<X|{null f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_5407() { checkNotSubtype("X<X|{null f1}>","{{null f1} f1}"); }
	@Test public void test_5408() { checkNotSubtype("X<X|{null f1}>","{{null f2} f1}"); }
	@Test public void test_5409() { checkNotSubtype("X<X|{null f1}>","{{null f1} f2}"); }
	@Test public void test_5410() { checkNotSubtype("X<X|{null f1}>","{{null f2} f2}"); }
	@Test public void test_5411() { checkIsSubtype("X<X|{null f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5412() { checkIsSubtype("X<X|{null f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5413() { checkIsSubtype("X<X|{null f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5414() { checkIsSubtype("X<X|{null f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5415() { checkIsSubtype("X<X|{null f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5416() { checkIsSubtype("X<X|{null f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5417() { checkIsSubtype("X<X|{null f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5418() { checkIsSubtype("X<X|{null f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5419() { checkIsSubtype("X<X|{null f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5420() { checkIsSubtype("X<X|{null f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5421() { checkIsSubtype("X<X|{null f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5422() { checkIsSubtype("X<X|{null f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5423() { checkIsSubtype("X<X|{null f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5424() { checkIsSubtype("X<X|{null f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5425() { checkIsSubtype("X<X|{null f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5426() { checkIsSubtype("X<X|{null f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5427() { checkIsSubtype("X<X|{null f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5428() { checkIsSubtype("X<X|{null f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5429() { checkIsSubtype("X<X|{null f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5430() { checkIsSubtype("X<X|{null f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5431() { checkIsSubtype("X<X|{null f1}>","{X<X|null> f1}"); }
	@Test public void test_5432() { checkNotSubtype("X<X|{null f1}>","{X<X|null> f2}"); }
	@Test public void test_5433() { checkIsSubtype("X<X|{null f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5434() { checkIsSubtype("X<X|{null f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5435() { checkIsSubtype("X<X|{null f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5436() { checkIsSubtype("X<X|{null f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5437() { checkIsSubtype("X<X|{null f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5438() { checkIsSubtype("X<X|{null f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5439() { checkIsSubtype("X<X|{null f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5440() { checkIsSubtype("X<X|{null f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5441() { checkIsSubtype("X<X|{null f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5442() { checkIsSubtype("X<X|{null f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5443() { checkNotSubtype("X<X|{null f1}>","null|null"); }
	@Test public void test_5444() { checkNotSubtype("X<X|{null f1}>","null|X<{X f1}>"); }
	@Test public void test_5445() { checkNotSubtype("X<X|{null f1}>","null|X<{X f2}>"); }
	@Test public void test_5446() { checkNotSubtype("X<X|{null f1}>","null|{null f1}"); }
	@Test public void test_5447() { checkNotSubtype("X<X|{null f1}>","null|{null f2}"); }
	@Test public void test_5448() { checkNotSubtype("X<X|{null f1}>","null|(X<null|X>)"); }
	@Test public void test_5449() { checkNotSubtype("X<X|{null f1}>","{null f1}|null"); }
	@Test public void test_5450() { checkNotSubtype("X<X|{null f1}>","{null f2}|null"); }
	@Test public void test_5451() { checkNotSubtype("X<X|{null f1}>","X<{X f1}>|null"); }
	@Test public void test_5452() { checkNotSubtype("X<X|{null f1}>","X<{X f2}>|null"); }
	@Test public void test_5453() { checkIsSubtype("X<X|{null f1}>","X<X|{null f1}>"); }
	@Test public void test_5454() { checkNotSubtype("X<X|{null f1}>","X<X|{null f2}>"); }
	@Test public void test_5455() { checkIsSubtype("X<X|{null f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5456() { checkIsSubtype("X<X|{null f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5457() { checkIsSubtype("X<X|{null f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5458() { checkIsSubtype("X<X|{null f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5459() { checkIsSubtype("X<X|{null f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5460() { checkIsSubtype("X<X|{null f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5461() { checkIsSubtype("X<X|{null f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5462() { checkIsSubtype("X<X|{null f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5463() { checkIsSubtype("X<X|{null f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5464() { checkIsSubtype("X<X|{null f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5465() { checkIsSubtype("X<X|{null f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5466() { checkIsSubtype("X<X|{null f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5467() { checkIsSubtype("X<X|{null f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5468() { checkIsSubtype("X<X|{null f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5469() { checkIsSubtype("X<X|{null f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5470() { checkIsSubtype("X<X|{null f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5471() { checkIsSubtype("X<X|{null f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_5472() { checkIsSubtype("X<X|{null f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_5473() { checkIsSubtype("X<X|{null f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_5474() { checkIsSubtype("X<X|{null f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_5475() { checkIsSubtype("X<X|{null f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5476() { checkIsSubtype("X<X|{null f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5477() { checkIsSubtype("X<X|{null f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5478() { checkIsSubtype("X<X|{null f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5479() { checkNotSubtype("X<X|{null f1}>","(X<X|null>)|null"); }
	@Test public void test_5480() { checkNotSubtype("X<X|{null f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_5481() { checkIsSubtype("X<X|{null f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5482() { checkIsSubtype("X<X|{null f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5483() { checkIsSubtype("X<X|{null f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5484() { checkIsSubtype("X<X|{null f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5485() { checkIsSubtype("X<X|{null f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5486() { checkIsSubtype("X<X|{null f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5487() { checkIsSubtype("X<X|{null f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5488() { checkNotSubtype("X<X|{null f2}>","null"); }
	@Test public void test_5489() { checkIsSubtype("X<X|{null f2}>","X<{X f1}>"); }
	@Test public void test_5490() { checkIsSubtype("X<X|{null f2}>","X<{X f2}>"); }
	@Test public void test_5491() { checkNotSubtype("X<X|{null f2}>","{null f1}"); }
	@Test public void test_5492() { checkIsSubtype("X<X|{null f2}>","{null f2}"); }
	@Test public void test_5493() { checkIsSubtype("X<X|{null f2}>","{X<{X f1}> f1}"); }
	@Test public void test_5494() { checkIsSubtype("X<X|{null f2}>","{X<{X f2}> f1}"); }
	@Test public void test_5495() { checkIsSubtype("X<X|{null f2}>","{X<{X f1}> f2}"); }
	@Test public void test_5496() { checkIsSubtype("X<X|{null f2}>","{X<{X f2}> f2}"); }
	@Test public void test_5497() { checkNotSubtype("X<X|{null f2}>","X<X|null>"); }
	@Test public void test_5498() { checkIsSubtype("X<X|{null f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_5499() { checkIsSubtype("X<X|{null f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_5500() { checkNotSubtype("X<X|{null f2}>","{{null f1} f1}"); }
	@Test public void test_5501() { checkNotSubtype("X<X|{null f2}>","{{null f2} f1}"); }
	@Test public void test_5502() { checkNotSubtype("X<X|{null f2}>","{{null f1} f2}"); }
	@Test public void test_5503() { checkNotSubtype("X<X|{null f2}>","{{null f2} f2}"); }
	@Test public void test_5504() { checkIsSubtype("X<X|{null f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5505() { checkIsSubtype("X<X|{null f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5506() { checkIsSubtype("X<X|{null f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5507() { checkIsSubtype("X<X|{null f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5508() { checkIsSubtype("X<X|{null f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5509() { checkIsSubtype("X<X|{null f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5510() { checkIsSubtype("X<X|{null f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5511() { checkIsSubtype("X<X|{null f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5512() { checkIsSubtype("X<X|{null f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5513() { checkIsSubtype("X<X|{null f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5514() { checkIsSubtype("X<X|{null f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5515() { checkIsSubtype("X<X|{null f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5516() { checkIsSubtype("X<X|{null f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5517() { checkIsSubtype("X<X|{null f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5518() { checkIsSubtype("X<X|{null f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5519() { checkIsSubtype("X<X|{null f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5520() { checkIsSubtype("X<X|{null f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5521() { checkIsSubtype("X<X|{null f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5522() { checkIsSubtype("X<X|{null f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5523() { checkIsSubtype("X<X|{null f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5524() { checkNotSubtype("X<X|{null f2}>","{X<X|null> f1}"); }
	@Test public void test_5525() { checkIsSubtype("X<X|{null f2}>","{X<X|null> f2}"); }
	@Test public void test_5526() { checkIsSubtype("X<X|{null f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5527() { checkIsSubtype("X<X|{null f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5528() { checkIsSubtype("X<X|{null f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5529() { checkIsSubtype("X<X|{null f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5530() { checkIsSubtype("X<X|{null f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5531() { checkIsSubtype("X<X|{null f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5532() { checkIsSubtype("X<X|{null f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5533() { checkIsSubtype("X<X|{null f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5534() { checkIsSubtype("X<X|{null f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5535() { checkIsSubtype("X<X|{null f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5536() { checkNotSubtype("X<X|{null f2}>","null|null"); }
	@Test public void test_5537() { checkNotSubtype("X<X|{null f2}>","null|X<{X f1}>"); }
	@Test public void test_5538() { checkNotSubtype("X<X|{null f2}>","null|X<{X f2}>"); }
	@Test public void test_5539() { checkNotSubtype("X<X|{null f2}>","null|{null f1}"); }
	@Test public void test_5540() { checkNotSubtype("X<X|{null f2}>","null|{null f2}"); }
	@Test public void test_5541() { checkNotSubtype("X<X|{null f2}>","null|(X<null|X>)"); }
	@Test public void test_5542() { checkNotSubtype("X<X|{null f2}>","{null f1}|null"); }
	@Test public void test_5543() { checkNotSubtype("X<X|{null f2}>","{null f2}|null"); }
	@Test public void test_5544() { checkNotSubtype("X<X|{null f2}>","X<{X f1}>|null"); }
	@Test public void test_5545() { checkNotSubtype("X<X|{null f2}>","X<{X f2}>|null"); }
	@Test public void test_5546() { checkNotSubtype("X<X|{null f2}>","X<X|{null f1}>"); }
	@Test public void test_5547() { checkIsSubtype("X<X|{null f2}>","X<X|{null f2}>"); }
	@Test public void test_5548() { checkIsSubtype("X<X|{null f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5549() { checkIsSubtype("X<X|{null f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5550() { checkIsSubtype("X<X|{null f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5551() { checkIsSubtype("X<X|{null f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5552() { checkIsSubtype("X<X|{null f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5553() { checkIsSubtype("X<X|{null f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5554() { checkIsSubtype("X<X|{null f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5555() { checkIsSubtype("X<X|{null f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5556() { checkIsSubtype("X<X|{null f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5557() { checkIsSubtype("X<X|{null f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5558() { checkIsSubtype("X<X|{null f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5559() { checkIsSubtype("X<X|{null f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5560() { checkIsSubtype("X<X|{null f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5561() { checkIsSubtype("X<X|{null f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5562() { checkIsSubtype("X<X|{null f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5563() { checkIsSubtype("X<X|{null f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5564() { checkIsSubtype("X<X|{null f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_5565() { checkIsSubtype("X<X|{null f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_5566() { checkIsSubtype("X<X|{null f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_5567() { checkIsSubtype("X<X|{null f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_5568() { checkIsSubtype("X<X|{null f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5569() { checkIsSubtype("X<X|{null f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5570() { checkIsSubtype("X<X|{null f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5571() { checkIsSubtype("X<X|{null f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5572() { checkNotSubtype("X<X|{null f2}>","(X<X|null>)|null"); }
	@Test public void test_5573() { checkNotSubtype("X<X|{null f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_5574() { checkIsSubtype("X<X|{null f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5575() { checkIsSubtype("X<X|{null f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5576() { checkIsSubtype("X<X|{null f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5577() { checkIsSubtype("X<X|{null f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5578() { checkIsSubtype("X<X|{null f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5579() { checkIsSubtype("X<X|{null f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5580() { checkIsSubtype("X<X|{null f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5581() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","null"); }
	@Test public void test_5582() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f1}>"); }
	@Test public void test_5583() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f2}>"); }
	@Test public void test_5584() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{null f1}"); }
	@Test public void test_5585() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{null f2}"); }
	@Test public void test_5586() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<{X f1}> f1}"); }
	@Test public void test_5587() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<{X f2}> f1}"); }
	@Test public void test_5588() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<{X f1}> f2}"); }
	@Test public void test_5589() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<{X f2}> f2}"); }
	@Test public void test_5590() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|null>"); }
	@Test public void test_5591() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_5592() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_5593() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{{null f1} f1}"); }
	@Test public void test_5594() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{{null f2} f1}"); }
	@Test public void test_5595() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{{null f1} f2}"); }
	@Test public void test_5596() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{{null f2} f2}"); }
	@Test public void test_5597() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5598() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5599() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5600() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5601() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5602() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5603() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5604() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5605() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5606() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5607() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5608() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5609() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5610() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5611() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5612() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5613() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5614() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5615() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5616() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5617() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<X|null> f1}"); }
	@Test public void test_5618() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<X|null> f2}"); }
	@Test public void test_5619() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5620() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5621() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5622() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5623() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5624() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5625() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5626() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5627() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5628() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5629() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","null|null"); }
	@Test public void test_5630() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","null|X<{X f1}>"); }
	@Test public void test_5631() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","null|X<{X f2}>"); }
	@Test public void test_5632() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","null|{null f1}"); }
	@Test public void test_5633() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","null|{null f2}"); }
	@Test public void test_5634() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","null|(X<null|X>)"); }
	@Test public void test_5635() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{null f1}|null"); }
	@Test public void test_5636() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","{null f2}|null"); }
	@Test public void test_5637() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f1}>|null"); }
	@Test public void test_5638() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f2}>|null"); }
	@Test public void test_5639() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{null f1}>"); }
	@Test public void test_5640() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{null f2}>"); }
	@Test public void test_5641() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5642() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5643() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5644() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5645() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5646() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5647() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5648() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5649() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5650() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5651() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5652() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5653() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5654() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5655() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5656() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5657() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_5658() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_5659() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_5660() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_5661() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5662() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5663() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5664() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5665() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","(X<X|null>)|null"); }
	@Test public void test_5666() { checkNotSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_5667() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5668() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5669() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5670() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5671() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5672() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5673() { checkIsSubtype("{X<{X f1}> f1}|X<{X f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5674() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","null"); }
	@Test public void test_5675() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f1}>"); }
	@Test public void test_5676() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f2}>"); }
	@Test public void test_5677() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{null f1}"); }
	@Test public void test_5678() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{null f2}"); }
	@Test public void test_5679() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<{X f1}> f1}"); }
	@Test public void test_5680() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<{X f2}> f1}"); }
	@Test public void test_5681() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<{X f1}> f2}"); }
	@Test public void test_5682() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<{X f2}> f2}"); }
	@Test public void test_5683() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|null>"); }
	@Test public void test_5684() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_5685() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_5686() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{{null f1} f1}"); }
	@Test public void test_5687() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{{null f2} f1}"); }
	@Test public void test_5688() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{{null f1} f2}"); }
	@Test public void test_5689() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{{null f2} f2}"); }
	@Test public void test_5690() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5691() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5692() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5693() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5694() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5695() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5696() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5697() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5698() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5699() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5700() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5701() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5702() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5703() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5704() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5705() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5706() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5707() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5708() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5709() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5710() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<X|null> f1}"); }
	@Test public void test_5711() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<X|null> f2}"); }
	@Test public void test_5712() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5713() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5714() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5715() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5716() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5717() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5718() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5719() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5720() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5721() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5722() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","null|null"); }
	@Test public void test_5723() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","null|X<{X f1}>"); }
	@Test public void test_5724() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","null|X<{X f2}>"); }
	@Test public void test_5725() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","null|{null f1}"); }
	@Test public void test_5726() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","null|{null f2}"); }
	@Test public void test_5727() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","null|(X<null|X>)"); }
	@Test public void test_5728() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{null f1}|null"); }
	@Test public void test_5729() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","{null f2}|null"); }
	@Test public void test_5730() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f1}>|null"); }
	@Test public void test_5731() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f2}>|null"); }
	@Test public void test_5732() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{null f1}>"); }
	@Test public void test_5733() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{null f2}>"); }
	@Test public void test_5734() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5735() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5736() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5737() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5738() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5739() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5740() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5741() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5742() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5743() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5744() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5745() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5746() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5747() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5748() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5749() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5750() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_5751() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_5752() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_5753() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_5754() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5755() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5756() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5757() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5758() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","(X<X|null>)|null"); }
	@Test public void test_5759() { checkNotSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_5760() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5761() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5762() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5763() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5764() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5765() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5766() { checkIsSubtype("{X<{X f2}> f1}|X<{X f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5767() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","null"); }
	@Test public void test_5768() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f1}>"); }
	@Test public void test_5769() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f2}>"); }
	@Test public void test_5770() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{null f1}"); }
	@Test public void test_5771() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{null f2}"); }
	@Test public void test_5772() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<{X f1}> f1}"); }
	@Test public void test_5773() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<{X f2}> f1}"); }
	@Test public void test_5774() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<{X f1}> f2}"); }
	@Test public void test_5775() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<{X f2}> f2}"); }
	@Test public void test_5776() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|null>"); }
	@Test public void test_5777() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_5778() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_5779() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{{null f1} f1}"); }
	@Test public void test_5780() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{{null f2} f1}"); }
	@Test public void test_5781() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{{null f1} f2}"); }
	@Test public void test_5782() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{{null f2} f2}"); }
	@Test public void test_5783() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5784() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5785() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5786() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5787() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5788() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5789() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5790() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5791() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5792() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5793() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5794() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5795() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5796() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5797() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5798() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5799() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5800() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5801() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5802() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5803() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<X|null> f1}"); }
	@Test public void test_5804() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<X|null> f2}"); }
	@Test public void test_5805() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5806() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5807() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5808() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5809() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5810() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5811() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5812() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5813() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5814() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5815() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","null|null"); }
	@Test public void test_5816() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","null|X<{X f1}>"); }
	@Test public void test_5817() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","null|X<{X f2}>"); }
	@Test public void test_5818() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","null|{null f1}"); }
	@Test public void test_5819() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","null|{null f2}"); }
	@Test public void test_5820() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","null|(X<null|X>)"); }
	@Test public void test_5821() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{null f1}|null"); }
	@Test public void test_5822() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","{null f2}|null"); }
	@Test public void test_5823() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f1}>|null"); }
	@Test public void test_5824() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f2}>|null"); }
	@Test public void test_5825() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{null f1}>"); }
	@Test public void test_5826() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{null f2}>"); }
	@Test public void test_5827() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5828() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5829() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5830() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5831() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5832() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5833() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5834() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5835() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5836() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5837() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5838() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5839() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5840() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5841() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5842() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5843() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_5844() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_5845() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_5846() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_5847() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5848() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5849() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5850() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5851() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","(X<X|null>)|null"); }
	@Test public void test_5852() { checkNotSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_5853() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5854() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5855() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5856() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5857() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5858() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5859() { checkIsSubtype("{X<{X f1}> f2}|X<{X f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5860() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","null"); }
	@Test public void test_5861() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f1}>"); }
	@Test public void test_5862() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f2}>"); }
	@Test public void test_5863() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{null f1}"); }
	@Test public void test_5864() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{null f2}"); }
	@Test public void test_5865() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<{X f1}> f1}"); }
	@Test public void test_5866() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<{X f2}> f1}"); }
	@Test public void test_5867() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<{X f1}> f2}"); }
	@Test public void test_5868() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<{X f2}> f2}"); }
	@Test public void test_5869() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|null>"); }
	@Test public void test_5870() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_5871() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_5872() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{{null f1} f1}"); }
	@Test public void test_5873() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{{null f2} f1}"); }
	@Test public void test_5874() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{{null f1} f2}"); }
	@Test public void test_5875() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{{null f2} f2}"); }
	@Test public void test_5876() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5877() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5878() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5879() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5880() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5881() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5882() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5883() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5884() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5885() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5886() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5887() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5888() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5889() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5890() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5891() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5892() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5893() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5894() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5895() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5896() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<X|null> f1}"); }
	@Test public void test_5897() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<X|null> f2}"); }
	@Test public void test_5898() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5899() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5900() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5901() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5902() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5903() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5904() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5905() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5906() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_5907() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_5908() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","null|null"); }
	@Test public void test_5909() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","null|X<{X f1}>"); }
	@Test public void test_5910() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","null|X<{X f2}>"); }
	@Test public void test_5911() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","null|{null f1}"); }
	@Test public void test_5912() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","null|{null f2}"); }
	@Test public void test_5913() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","null|(X<null|X>)"); }
	@Test public void test_5914() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{null f1}|null"); }
	@Test public void test_5915() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","{null f2}|null"); }
	@Test public void test_5916() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f1}>|null"); }
	@Test public void test_5917() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f2}>|null"); }
	@Test public void test_5918() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{null f1}>"); }
	@Test public void test_5919() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{null f2}>"); }
	@Test public void test_5920() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_5921() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_5922() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_5923() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_5924() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_5925() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_5926() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_5927() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_5928() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_5929() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_5930() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_5931() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_5932() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_5933() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_5934() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_5935() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_5936() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_5937() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_5938() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_5939() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_5940() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_5941() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_5942() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_5943() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_5944() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","(X<X|null>)|null"); }
	@Test public void test_5945() { checkNotSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_5946() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_5947() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_5948() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_5949() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_5950() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_5951() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_5952() { checkIsSubtype("{X<{X f2}> f2}|X<{X f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_5953() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","null"); }
	@Test public void test_5954() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f1}>"); }
	@Test public void test_5955() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f2}>"); }
	@Test public void test_5956() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{null f1}"); }
	@Test public void test_5957() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{null f2}"); }
	@Test public void test_5958() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<{X f1}> f1}"); }
	@Test public void test_5959() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<{X f2}> f1}"); }
	@Test public void test_5960() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<{X f1}> f2}"); }
	@Test public void test_5961() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<{X f2}> f2}"); }
	@Test public void test_5962() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","X<X|null>"); }
	@Test public void test_5963() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_5964() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_5965() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{{null f1} f1}"); }
	@Test public void test_5966() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{{null f2} f1}"); }
	@Test public void test_5967() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{{null f1} f2}"); }
	@Test public void test_5968() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{{null f2} f2}"); }
	@Test public void test_5969() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_5970() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_5971() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_5972() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_5973() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_5974() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_5975() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_5976() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_5977() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_5978() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_5979() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_5980() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_5981() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_5982() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_5983() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_5984() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_5985() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_5986() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_5987() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_5988() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_5989() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{X<X|null> f1}"); }
	@Test public void test_5990() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{X<X|null> f2}"); }
	@Test public void test_5991() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_5992() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_5993() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_5994() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_5995() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_5996() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_5997() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_5998() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_5999() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6000() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6001() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","null|null"); }
	@Test public void test_6002() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","null|X<{X f1}>"); }
	@Test public void test_6003() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","null|X<{X f2}>"); }
	@Test public void test_6004() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","null|{null f1}"); }
	@Test public void test_6005() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","null|{null f2}"); }
	@Test public void test_6006() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","null|(X<null|X>)"); }
	@Test public void test_6007() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{null f1}|null"); }
	@Test public void test_6008() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","{null f2}|null"); }
	@Test public void test_6009() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f1}>|null"); }
	@Test public void test_6010() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f2}>|null"); }
	@Test public void test_6011() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{null f1}>"); }
	@Test public void test_6012() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{null f2}>"); }
	@Test public void test_6013() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6014() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6015() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6016() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6017() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6018() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6019() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6020() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6021() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6022() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6023() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6024() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6025() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6026() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6027() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6028() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6029() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_6030() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_6031() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_6032() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_6033() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6034() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6035() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6036() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6037() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","(X<X|null>)|null"); }
	@Test public void test_6038() { checkNotSubtype("X<{X f1}>|Y<{Y f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_6039() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6040() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6041() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6042() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6043() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6044() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6045() { checkIsSubtype("X<{X f1}>|Y<{Y f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6046() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","null"); }
	@Test public void test_6047() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f1}>"); }
	@Test public void test_6048() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f2}>"); }
	@Test public void test_6049() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{null f1}"); }
	@Test public void test_6050() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{null f2}"); }
	@Test public void test_6051() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<{X f1}> f1}"); }
	@Test public void test_6052() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<{X f2}> f1}"); }
	@Test public void test_6053() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<{X f1}> f2}"); }
	@Test public void test_6054() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<{X f2}> f2}"); }
	@Test public void test_6055() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","X<X|null>"); }
	@Test public void test_6056() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_6057() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_6058() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{{null f1} f1}"); }
	@Test public void test_6059() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{{null f2} f1}"); }
	@Test public void test_6060() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{{null f1} f2}"); }
	@Test public void test_6061() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{{null f2} f2}"); }
	@Test public void test_6062() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6063() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6064() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6065() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6066() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6067() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6068() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6069() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6070() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6071() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6072() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6073() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6074() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6075() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6076() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6077() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6078() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6079() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6080() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6081() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6082() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{X<X|null> f1}"); }
	@Test public void test_6083() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{X<X|null> f2}"); }
	@Test public void test_6084() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6085() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6086() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6087() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6088() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6089() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6090() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6091() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6092() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6093() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6094() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","null|null"); }
	@Test public void test_6095() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","null|X<{X f1}>"); }
	@Test public void test_6096() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","null|X<{X f2}>"); }
	@Test public void test_6097() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","null|{null f1}"); }
	@Test public void test_6098() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","null|{null f2}"); }
	@Test public void test_6099() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","null|(X<null|X>)"); }
	@Test public void test_6100() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{null f1}|null"); }
	@Test public void test_6101() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","{null f2}|null"); }
	@Test public void test_6102() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f1}>|null"); }
	@Test public void test_6103() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f2}>|null"); }
	@Test public void test_6104() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{null f1}>"); }
	@Test public void test_6105() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{null f2}>"); }
	@Test public void test_6106() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6107() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6108() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6109() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6110() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6111() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6112() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6113() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6114() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6115() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6116() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6117() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6118() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6119() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6120() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6121() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6122() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_6123() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_6124() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_6125() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_6126() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6127() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6128() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6129() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6130() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","(X<X|null>)|null"); }
	@Test public void test_6131() { checkNotSubtype("X<{X f1}>|Y<{Y f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_6132() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6133() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6134() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6135() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6136() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6137() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6138() { checkIsSubtype("X<{X f1}>|Y<{Y f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6139() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","null"); }
	@Test public void test_6140() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f1}>"); }
	@Test public void test_6141() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f2}>"); }
	@Test public void test_6142() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{null f1}"); }
	@Test public void test_6143() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{null f2}"); }
	@Test public void test_6144() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<{X f1}> f1}"); }
	@Test public void test_6145() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<{X f2}> f1}"); }
	@Test public void test_6146() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<{X f1}> f2}"); }
	@Test public void test_6147() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<{X f2}> f2}"); }
	@Test public void test_6148() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","X<X|null>"); }
	@Test public void test_6149() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_6150() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_6151() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{{null f1} f1}"); }
	@Test public void test_6152() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{{null f2} f1}"); }
	@Test public void test_6153() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{{null f1} f2}"); }
	@Test public void test_6154() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{{null f2} f2}"); }
	@Test public void test_6155() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6156() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6157() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6158() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6159() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6160() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6161() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6162() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6163() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6164() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6165() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6166() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6167() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6168() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6169() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6170() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6171() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6172() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6173() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6174() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6175() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{X<X|null> f1}"); }
	@Test public void test_6176() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{X<X|null> f2}"); }
	@Test public void test_6177() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6178() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6179() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6180() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6181() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6182() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6183() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6184() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6185() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6186() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6187() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","null|null"); }
	@Test public void test_6188() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","null|X<{X f1}>"); }
	@Test public void test_6189() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","null|X<{X f2}>"); }
	@Test public void test_6190() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","null|{null f1}"); }
	@Test public void test_6191() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","null|{null f2}"); }
	@Test public void test_6192() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","null|(X<null|X>)"); }
	@Test public void test_6193() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{null f1}|null"); }
	@Test public void test_6194() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","{null f2}|null"); }
	@Test public void test_6195() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f1}>|null"); }
	@Test public void test_6196() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f2}>|null"); }
	@Test public void test_6197() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{null f1}>"); }
	@Test public void test_6198() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{null f2}>"); }
	@Test public void test_6199() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6200() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6201() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6202() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6203() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6204() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6205() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6206() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6207() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6208() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6209() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6210() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6211() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6212() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6213() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6214() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6215() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_6216() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_6217() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_6218() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_6219() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6220() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6221() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6222() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6223() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","(X<X|null>)|null"); }
	@Test public void test_6224() { checkNotSubtype("X<{X f2}>|Y<{Y f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_6225() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6226() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6227() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6228() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6229() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6230() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6231() { checkIsSubtype("X<{X f2}>|Y<{Y f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6232() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","null"); }
	@Test public void test_6233() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f1}>"); }
	@Test public void test_6234() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f2}>"); }
	@Test public void test_6235() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{null f1}"); }
	@Test public void test_6236() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{null f2}"); }
	@Test public void test_6237() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<{X f1}> f1}"); }
	@Test public void test_6238() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<{X f2}> f1}"); }
	@Test public void test_6239() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<{X f1}> f2}"); }
	@Test public void test_6240() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<{X f2}> f2}"); }
	@Test public void test_6241() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","X<X|null>"); }
	@Test public void test_6242() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_6243() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_6244() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{{null f1} f1}"); }
	@Test public void test_6245() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{{null f2} f1}"); }
	@Test public void test_6246() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{{null f1} f2}"); }
	@Test public void test_6247() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{{null f2} f2}"); }
	@Test public void test_6248() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6249() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6250() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6251() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6252() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6253() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6254() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6255() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6256() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6257() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6258() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6259() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6260() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6261() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6262() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6263() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6264() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6265() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6266() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6267() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6268() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{X<X|null> f1}"); }
	@Test public void test_6269() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{X<X|null> f2}"); }
	@Test public void test_6270() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6271() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6272() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6273() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6274() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6275() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6276() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6277() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6278() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6279() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6280() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","null|null"); }
	@Test public void test_6281() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","null|X<{X f1}>"); }
	@Test public void test_6282() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","null|X<{X f2}>"); }
	@Test public void test_6283() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","null|{null f1}"); }
	@Test public void test_6284() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","null|{null f2}"); }
	@Test public void test_6285() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","null|(X<null|X>)"); }
	@Test public void test_6286() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{null f1}|null"); }
	@Test public void test_6287() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","{null f2}|null"); }
	@Test public void test_6288() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f1}>|null"); }
	@Test public void test_6289() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f2}>|null"); }
	@Test public void test_6290() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{null f1}>"); }
	@Test public void test_6291() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{null f2}>"); }
	@Test public void test_6292() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6293() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6294() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6295() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6296() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6297() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6298() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6299() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6300() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6301() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6302() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6303() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6304() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6305() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6306() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6307() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6308() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_6309() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_6310() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_6311() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_6312() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6313() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6314() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6315() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6316() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","(X<X|null>)|null"); }
	@Test public void test_6317() { checkNotSubtype("X<{X f2}>|Y<{Y f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_6318() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6319() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6320() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6321() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6322() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6323() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6324() { checkIsSubtype("X<{X f2}>|Y<{Y f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6325() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","null"); }
	@Test public void test_6326() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f1}>"); }
	@Test public void test_6327() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f2}>"); }
	@Test public void test_6328() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{null f1}"); }
	@Test public void test_6329() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{null f2}"); }
	@Test public void test_6330() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<{X f1}> f1}"); }
	@Test public void test_6331() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<{X f2}> f1}"); }
	@Test public void test_6332() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<{X f1}> f2}"); }
	@Test public void test_6333() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<{X f2}> f2}"); }
	@Test public void test_6334() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|null>"); }
	@Test public void test_6335() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_6336() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_6337() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{{null f1} f1}"); }
	@Test public void test_6338() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{{null f2} f1}"); }
	@Test public void test_6339() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{{null f1} f2}"); }
	@Test public void test_6340() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{{null f2} f2}"); }
	@Test public void test_6341() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6342() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6343() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6344() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6345() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6346() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6347() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6348() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6349() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6350() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6351() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6352() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6353() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6354() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6355() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6356() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6357() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6358() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6359() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6360() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6361() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<X|null> f1}"); }
	@Test public void test_6362() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<X|null> f2}"); }
	@Test public void test_6363() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6364() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6365() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6366() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6367() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6368() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6369() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6370() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6371() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6372() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6373() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","null|null"); }
	@Test public void test_6374() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","null|X<{X f1}>"); }
	@Test public void test_6375() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","null|X<{X f2}>"); }
	@Test public void test_6376() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","null|{null f1}"); }
	@Test public void test_6377() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","null|{null f2}"); }
	@Test public void test_6378() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","null|(X<null|X>)"); }
	@Test public void test_6379() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{null f1}|null"); }
	@Test public void test_6380() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","{null f2}|null"); }
	@Test public void test_6381() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f1}>|null"); }
	@Test public void test_6382() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f2}>|null"); }
	@Test public void test_6383() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{null f1}>"); }
	@Test public void test_6384() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{null f2}>"); }
	@Test public void test_6385() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6386() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6387() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6388() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6389() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6390() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6391() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6392() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6393() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6394() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6395() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6396() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6397() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6398() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6399() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6400() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6401() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_6402() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_6403() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_6404() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_6405() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6406() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6407() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6408() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6409() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","(X<X|null>)|null"); }
	@Test public void test_6410() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_6411() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6412() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6413() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6414() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6415() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6416() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6417() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6418() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","null"); }
	@Test public void test_6419() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f1}>"); }
	@Test public void test_6420() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f2}>"); }
	@Test public void test_6421() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{null f1}"); }
	@Test public void test_6422() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{null f2}"); }
	@Test public void test_6423() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<{X f1}> f1}"); }
	@Test public void test_6424() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<{X f2}> f1}"); }
	@Test public void test_6425() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<{X f1}> f2}"); }
	@Test public void test_6426() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<{X f2}> f2}"); }
	@Test public void test_6427() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|null>"); }
	@Test public void test_6428() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_6429() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_6430() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{{null f1} f1}"); }
	@Test public void test_6431() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{{null f2} f1}"); }
	@Test public void test_6432() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{{null f1} f2}"); }
	@Test public void test_6433() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{{null f2} f2}"); }
	@Test public void test_6434() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6435() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6436() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6437() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6438() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6439() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6440() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6441() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6442() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6443() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6444() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6445() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6446() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6447() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6448() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6449() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6450() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6451() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6452() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6453() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6454() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<X|null> f1}"); }
	@Test public void test_6455() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<X|null> f2}"); }
	@Test public void test_6456() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6457() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6458() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6459() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6460() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6461() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6462() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6463() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6464() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6465() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6466() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","null|null"); }
	@Test public void test_6467() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","null|X<{X f1}>"); }
	@Test public void test_6468() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","null|X<{X f2}>"); }
	@Test public void test_6469() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","null|{null f1}"); }
	@Test public void test_6470() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","null|{null f2}"); }
	@Test public void test_6471() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","null|(X<null|X>)"); }
	@Test public void test_6472() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{null f1}|null"); }
	@Test public void test_6473() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","{null f2}|null"); }
	@Test public void test_6474() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f1}>|null"); }
	@Test public void test_6475() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f2}>|null"); }
	@Test public void test_6476() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{null f1}>"); }
	@Test public void test_6477() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{null f2}>"); }
	@Test public void test_6478() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6479() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6480() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6481() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6482() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6483() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6484() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6485() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6486() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6487() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6488() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6489() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6490() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6491() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6492() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6493() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6494() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_6495() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_6496() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_6497() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_6498() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6499() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6500() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6501() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6502() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","(X<X|null>)|null"); }
	@Test public void test_6503() { checkNotSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_6504() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6505() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6506() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6507() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6508() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6509() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6510() { checkIsSubtype("X<{X f1}>|{X<{X f1}> f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6511() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","null"); }
	@Test public void test_6512() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f1}>"); }
	@Test public void test_6513() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f2}>"); }
	@Test public void test_6514() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{null f1}"); }
	@Test public void test_6515() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{null f2}"); }
	@Test public void test_6516() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<{X f1}> f1}"); }
	@Test public void test_6517() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<{X f2}> f1}"); }
	@Test public void test_6518() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<{X f1}> f2}"); }
	@Test public void test_6519() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<{X f2}> f2}"); }
	@Test public void test_6520() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|null>"); }
	@Test public void test_6521() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|Y<{Y f1}>>"); }
	@Test public void test_6522() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|Y<{Y f2}>>"); }
	@Test public void test_6523() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{{null f1} f1}"); }
	@Test public void test_6524() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{{null f2} f1}"); }
	@Test public void test_6525() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{{null f1} f2}"); }
	@Test public void test_6526() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{{null f2} f2}"); }
	@Test public void test_6527() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6528() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6529() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6530() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6531() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6532() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6533() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6534() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6535() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6536() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6537() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6538() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6539() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6540() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6541() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6542() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6543() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6544() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6545() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6546() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6547() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<X|null> f1}"); }
	@Test public void test_6548() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<X|null> f2}"); }
	@Test public void test_6549() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6550() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6551() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6552() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6553() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6554() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6555() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6556() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6557() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6558() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6559() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","null|null"); }
	@Test public void test_6560() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","null|X<{X f1}>"); }
	@Test public void test_6561() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","null|X<{X f2}>"); }
	@Test public void test_6562() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","null|{null f1}"); }
	@Test public void test_6563() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","null|{null f2}"); }
	@Test public void test_6564() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","null|(X<null|X>)"); }
	@Test public void test_6565() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{null f1}|null"); }
	@Test public void test_6566() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","{null f2}|null"); }
	@Test public void test_6567() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f1}>|null"); }
	@Test public void test_6568() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f2}>|null"); }
	@Test public void test_6569() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{null f1}>"); }
	@Test public void test_6570() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{null f2}>"); }
	@Test public void test_6571() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6572() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6573() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6574() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6575() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6576() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6577() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6578() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6579() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6580() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6581() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6582() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6583() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6584() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6585() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6586() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6587() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{{X f1} f1}>"); }
	@Test public void test_6588() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{{X f2} f1}>"); }
	@Test public void test_6589() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{{X f1} f2}>"); }
	@Test public void test_6590() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{{X f2} f2}>"); }
	@Test public void test_6591() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6592() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6593() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6594() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6595() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","(X<X|null>)|null"); }
	@Test public void test_6596() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|(Y<Y|null>)>"); }
	@Test public void test_6597() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6598() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6599() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6600() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6601() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6602() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6603() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f1}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6604() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","null"); }
	@Test public void test_6605() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f1}>"); }
	@Test public void test_6606() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f2}>"); }
	@Test public void test_6607() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{null f1}"); }
	@Test public void test_6608() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{null f2}"); }
	@Test public void test_6609() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<{X f1}> f1}"); }
	@Test public void test_6610() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<{X f2}> f1}"); }
	@Test public void test_6611() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<{X f1}> f2}"); }
	@Test public void test_6612() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<{X f2}> f2}"); }
	@Test public void test_6613() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|null>"); }
	@Test public void test_6614() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|Y<{Y f1}>>"); }
	@Test public void test_6615() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|Y<{Y f2}>>"); }
	@Test public void test_6616() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{{null f1} f1}"); }
	@Test public void test_6617() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{{null f2} f1}"); }
	@Test public void test_6618() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{{null f1} f2}"); }
	@Test public void test_6619() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{{null f2} f2}"); }
	@Test public void test_6620() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6621() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6622() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6623() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6624() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6625() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6626() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6627() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6628() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6629() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6630() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6631() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6632() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6633() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6634() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6635() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6636() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6637() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6638() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6639() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6640() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<X|null> f1}"); }
	@Test public void test_6641() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<X|null> f2}"); }
	@Test public void test_6642() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6643() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6644() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6645() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6646() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6647() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6648() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6649() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6650() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6651() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6652() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","null|null"); }
	@Test public void test_6653() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","null|X<{X f1}>"); }
	@Test public void test_6654() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","null|X<{X f2}>"); }
	@Test public void test_6655() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","null|{null f1}"); }
	@Test public void test_6656() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","null|{null f2}"); }
	@Test public void test_6657() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","null|(X<null|X>)"); }
	@Test public void test_6658() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{null f1}|null"); }
	@Test public void test_6659() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","{null f2}|null"); }
	@Test public void test_6660() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f1}>|null"); }
	@Test public void test_6661() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f2}>|null"); }
	@Test public void test_6662() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{null f1}>"); }
	@Test public void test_6663() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{null f2}>"); }
	@Test public void test_6664() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6665() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6666() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6667() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6668() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6669() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6670() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6671() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6672() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6673() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6674() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6675() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6676() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6677() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6678() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6679() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6680() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{{X f1} f1}>"); }
	@Test public void test_6681() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{{X f2} f1}>"); }
	@Test public void test_6682() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{{X f1} f2}>"); }
	@Test public void test_6683() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{{X f2} f2}>"); }
	@Test public void test_6684() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6685() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6686() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6687() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6688() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","(X<X|null>)|null"); }
	@Test public void test_6689() { checkNotSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|(Y<Y|null>)>"); }
	@Test public void test_6690() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6691() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6692() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6693() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6694() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6695() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6696() { checkIsSubtype("X<{X f2}>|{X<{X f2}> f2}","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6697() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","null"); }
	@Test public void test_6698() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f1}>"); }
	@Test public void test_6699() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f2}>"); }
	@Test public void test_6700() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{null f1}"); }
	@Test public void test_6701() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{null f2}"); }
	@Test public void test_6702() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<{X f1}> f1}"); }
	@Test public void test_6703() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<{X f2}> f1}"); }
	@Test public void test_6704() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<{X f1}> f2}"); }
	@Test public void test_6705() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<{X f2}> f2}"); }
	@Test public void test_6706() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","X<X|null>"); }
	@Test public void test_6707() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_6708() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_6709() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{{null f1} f1}"); }
	@Test public void test_6710() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{{null f2} f1}"); }
	@Test public void test_6711() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{{null f1} f2}"); }
	@Test public void test_6712() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{{null f2} f2}"); }
	@Test public void test_6713() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6714() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6715() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6716() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6717() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6718() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6719() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6720() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6721() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6722() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6723() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6724() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6725() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6726() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6727() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6728() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6729() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6730() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6731() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6732() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6733() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{X<X|null> f1}"); }
	@Test public void test_6734() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{X<X|null> f2}"); }
	@Test public void test_6735() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6736() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6737() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6738() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6739() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6740() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6741() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6742() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6743() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6744() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6745() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","null|null"); }
	@Test public void test_6746() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","null|X<{X f1}>"); }
	@Test public void test_6747() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","null|X<{X f2}>"); }
	@Test public void test_6748() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","null|{null f1}"); }
	@Test public void test_6749() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","null|{null f2}"); }
	@Test public void test_6750() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","null|(X<null|X>)"); }
	@Test public void test_6751() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{null f1}|null"); }
	@Test public void test_6752() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","{null f2}|null"); }
	@Test public void test_6753() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f1}>|null"); }
	@Test public void test_6754() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f2}>|null"); }
	@Test public void test_6755() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{null f1}>"); }
	@Test public void test_6756() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{null f2}>"); }
	@Test public void test_6757() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6758() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6759() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6760() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6761() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6762() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6763() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6764() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6765() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6766() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6767() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6768() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6769() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6770() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6771() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6772() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6773() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_6774() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_6775() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_6776() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_6777() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6778() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6779() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6780() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6781() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","(X<X|null>)|null"); }
	@Test public void test_6782() { checkNotSubtype("X<X|{Y<{Y f1}> f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_6783() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6784() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6785() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6786() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6787() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6788() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6789() { checkIsSubtype("X<X|{Y<{Y f1}> f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6790() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","null"); }
	@Test public void test_6791() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f1}>"); }
	@Test public void test_6792() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f2}>"); }
	@Test public void test_6793() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{null f1}"); }
	@Test public void test_6794() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{null f2}"); }
	@Test public void test_6795() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<{X f1}> f1}"); }
	@Test public void test_6796() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<{X f2}> f1}"); }
	@Test public void test_6797() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<{X f1}> f2}"); }
	@Test public void test_6798() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<{X f2}> f2}"); }
	@Test public void test_6799() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","X<X|null>"); }
	@Test public void test_6800() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_6801() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_6802() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{{null f1} f1}"); }
	@Test public void test_6803() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{{null f2} f1}"); }
	@Test public void test_6804() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{{null f1} f2}"); }
	@Test public void test_6805() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{{null f2} f2}"); }
	@Test public void test_6806() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6807() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6808() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6809() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6810() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6811() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6812() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6813() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6814() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6815() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6816() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6817() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6818() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6819() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6820() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6821() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6822() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6823() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6824() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6825() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6826() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{X<X|null> f1}"); }
	@Test public void test_6827() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{X<X|null> f2}"); }
	@Test public void test_6828() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6829() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6830() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6831() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6832() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6833() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6834() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6835() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6836() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6837() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6838() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","null|null"); }
	@Test public void test_6839() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","null|X<{X f1}>"); }
	@Test public void test_6840() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","null|X<{X f2}>"); }
	@Test public void test_6841() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","null|{null f1}"); }
	@Test public void test_6842() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","null|{null f2}"); }
	@Test public void test_6843() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","null|(X<null|X>)"); }
	@Test public void test_6844() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{null f1}|null"); }
	@Test public void test_6845() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","{null f2}|null"); }
	@Test public void test_6846() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f1}>|null"); }
	@Test public void test_6847() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f2}>|null"); }
	@Test public void test_6848() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{null f1}>"); }
	@Test public void test_6849() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{null f2}>"); }
	@Test public void test_6850() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6851() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6852() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6853() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6854() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6855() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6856() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6857() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6858() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6859() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6860() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6861() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6862() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6863() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6864() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6865() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6866() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_6867() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_6868() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_6869() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_6870() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6871() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6872() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6873() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6874() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","(X<X|null>)|null"); }
	@Test public void test_6875() { checkNotSubtype("X<X|{Y<{Y f2}> f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_6876() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6877() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6878() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6879() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6880() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6881() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6882() { checkIsSubtype("X<X|{Y<{Y f2}> f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6883() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","null"); }
	@Test public void test_6884() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f1}>"); }
	@Test public void test_6885() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f2}>"); }
	@Test public void test_6886() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{null f1}"); }
	@Test public void test_6887() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{null f2}"); }
	@Test public void test_6888() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<{X f1}> f1}"); }
	@Test public void test_6889() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<{X f2}> f1}"); }
	@Test public void test_6890() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<{X f1}> f2}"); }
	@Test public void test_6891() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<{X f2}> f2}"); }
	@Test public void test_6892() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","X<X|null>"); }
	@Test public void test_6893() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_6894() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_6895() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{{null f1} f1}"); }
	@Test public void test_6896() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{{null f2} f1}"); }
	@Test public void test_6897() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{{null f1} f2}"); }
	@Test public void test_6898() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{{null f2} f2}"); }
	@Test public void test_6899() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6900() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6901() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6902() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6903() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6904() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6905() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6906() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_6907() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_6908() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_6909() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_6910() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_6911() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_6912() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_6913() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_6914() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_6915() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_6916() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_6917() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_6918() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_6919() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{X<X|null> f1}"); }
	@Test public void test_6920() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{X<X|null> f2}"); }
	@Test public void test_6921() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_6922() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_6923() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_6924() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_6925() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_6926() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_6927() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_6928() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_6929() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_6930() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_6931() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","null|null"); }
	@Test public void test_6932() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","null|X<{X f1}>"); }
	@Test public void test_6933() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","null|X<{X f2}>"); }
	@Test public void test_6934() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","null|{null f1}"); }
	@Test public void test_6935() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","null|{null f2}"); }
	@Test public void test_6936() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","null|(X<null|X>)"); }
	@Test public void test_6937() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{null f1}|null"); }
	@Test public void test_6938() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","{null f2}|null"); }
	@Test public void test_6939() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f1}>|null"); }
	@Test public void test_6940() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f2}>|null"); }
	@Test public void test_6941() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{null f1}>"); }
	@Test public void test_6942() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{null f2}>"); }
	@Test public void test_6943() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_6944() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_6945() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_6946() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_6947() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_6948() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_6949() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_6950() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_6951() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_6952() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_6953() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_6954() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_6955() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_6956() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_6957() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_6958() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_6959() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_6960() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_6961() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_6962() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_6963() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_6964() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_6965() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_6966() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_6967() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","(X<X|null>)|null"); }
	@Test public void test_6968() { checkNotSubtype("X<X|{Y<{Y f1}> f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_6969() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_6970() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_6971() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_6972() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_6973() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_6974() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_6975() { checkIsSubtype("X<X|{Y<{Y f1}> f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_6976() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","null"); }
	@Test public void test_6977() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f1}>"); }
	@Test public void test_6978() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f2}>"); }
	@Test public void test_6979() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{null f1}"); }
	@Test public void test_6980() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{null f2}"); }
	@Test public void test_6981() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<{X f1}> f1}"); }
	@Test public void test_6982() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<{X f2}> f1}"); }
	@Test public void test_6983() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<{X f1}> f2}"); }
	@Test public void test_6984() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<{X f2}> f2}"); }
	@Test public void test_6985() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","X<X|null>"); }
	@Test public void test_6986() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_6987() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_6988() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{{null f1} f1}"); }
	@Test public void test_6989() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{{null f2} f1}"); }
	@Test public void test_6990() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{{null f1} f2}"); }
	@Test public void test_6991() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{{null f2} f2}"); }
	@Test public void test_6992() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_6993() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_6994() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_6995() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_6996() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_6997() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_6998() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_6999() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7000() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7001() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7002() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7003() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7004() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7005() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7006() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7007() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7008() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7009() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7010() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7011() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7012() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{X<X|null> f1}"); }
	@Test public void test_7013() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{X<X|null> f2}"); }
	@Test public void test_7014() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7015() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7016() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7017() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7018() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7019() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7020() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7021() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7022() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7023() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7024() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","null|null"); }
	@Test public void test_7025() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","null|X<{X f1}>"); }
	@Test public void test_7026() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","null|X<{X f2}>"); }
	@Test public void test_7027() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","null|{null f1}"); }
	@Test public void test_7028() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","null|{null f2}"); }
	@Test public void test_7029() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","null|(X<null|X>)"); }
	@Test public void test_7030() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{null f1}|null"); }
	@Test public void test_7031() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","{null f2}|null"); }
	@Test public void test_7032() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f1}>|null"); }
	@Test public void test_7033() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f2}>|null"); }
	@Test public void test_7034() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{null f1}>"); }
	@Test public void test_7035() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{null f2}>"); }
	@Test public void test_7036() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7037() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7038() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7039() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7040() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7041() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7042() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7043() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7044() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7045() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7046() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7047() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7048() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7049() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7050() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7051() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7052() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_7053() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_7054() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_7055() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_7056() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7057() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7058() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7059() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7060() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","(X<X|null>)|null"); }
	@Test public void test_7061() { checkNotSubtype("X<X|{Y<{Y f2}> f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_7062() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7063() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7064() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7065() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7066() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7067() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7068() { checkIsSubtype("X<X|{Y<{Y f2}> f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7069() { checkNotSubtype("X<X|{{X f1} f1}>","null"); }
	@Test public void test_7070() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f1}>"); }
	@Test public void test_7071() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f2}>"); }
	@Test public void test_7072() { checkNotSubtype("X<X|{{X f1} f1}>","{null f1}"); }
	@Test public void test_7073() { checkNotSubtype("X<X|{{X f1} f1}>","{null f2}"); }
	@Test public void test_7074() { checkIsSubtype("X<X|{{X f1} f1}>","{X<{X f1}> f1}"); }
	@Test public void test_7075() { checkIsSubtype("X<X|{{X f1} f1}>","{X<{X f2}> f1}"); }
	@Test public void test_7076() { checkIsSubtype("X<X|{{X f1} f1}>","{X<{X f1}> f2}"); }
	@Test public void test_7077() { checkIsSubtype("X<X|{{X f1} f1}>","{X<{X f2}> f2}"); }
	@Test public void test_7078() { checkNotSubtype("X<X|{{X f1} f1}>","X<X|null>"); }
	@Test public void test_7079() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_7080() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_7081() { checkNotSubtype("X<X|{{X f1} f1}>","{{null f1} f1}"); }
	@Test public void test_7082() { checkNotSubtype("X<X|{{X f1} f1}>","{{null f2} f1}"); }
	@Test public void test_7083() { checkNotSubtype("X<X|{{X f1} f1}>","{{null f1} f2}"); }
	@Test public void test_7084() { checkNotSubtype("X<X|{{X f1} f1}>","{{null f2} f2}"); }
	@Test public void test_7085() { checkIsSubtype("X<X|{{X f1} f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7086() { checkIsSubtype("X<X|{{X f1} f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7087() { checkIsSubtype("X<X|{{X f1} f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7088() { checkIsSubtype("X<X|{{X f1} f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7089() { checkIsSubtype("X<X|{{X f1} f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7090() { checkIsSubtype("X<X|{{X f1} f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7091() { checkIsSubtype("X<X|{{X f1} f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7092() { checkIsSubtype("X<X|{{X f1} f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7093() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7094() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7095() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7096() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7097() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7098() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7099() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7100() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7101() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7102() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7103() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7104() { checkIsSubtype("X<X|{{X f1} f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7105() { checkNotSubtype("X<X|{{X f1} f1}>","{X<X|null> f1}"); }
	@Test public void test_7106() { checkNotSubtype("X<X|{{X f1} f1}>","{X<X|null> f2}"); }
	@Test public void test_7107() { checkIsSubtype("X<X|{{X f1} f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7108() { checkIsSubtype("X<X|{{X f1} f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7109() { checkIsSubtype("X<X|{{X f1} f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7110() { checkIsSubtype("X<X|{{X f1} f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7111() { checkIsSubtype("X<X|{{X f1} f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7112() { checkIsSubtype("X<X|{{X f1} f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7113() { checkIsSubtype("X<X|{{X f1} f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7114() { checkIsSubtype("X<X|{{X f1} f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7115() { checkIsSubtype("X<X|{{X f1} f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7116() { checkIsSubtype("X<X|{{X f1} f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7117() { checkNotSubtype("X<X|{{X f1} f1}>","null|null"); }
	@Test public void test_7118() { checkNotSubtype("X<X|{{X f1} f1}>","null|X<{X f1}>"); }
	@Test public void test_7119() { checkNotSubtype("X<X|{{X f1} f1}>","null|X<{X f2}>"); }
	@Test public void test_7120() { checkNotSubtype("X<X|{{X f1} f1}>","null|{null f1}"); }
	@Test public void test_7121() { checkNotSubtype("X<X|{{X f1} f1}>","null|{null f2}"); }
	@Test public void test_7122() { checkNotSubtype("X<X|{{X f1} f1}>","null|(X<null|X>)"); }
	@Test public void test_7123() { checkNotSubtype("X<X|{{X f1} f1}>","{null f1}|null"); }
	@Test public void test_7124() { checkNotSubtype("X<X|{{X f1} f1}>","{null f2}|null"); }
	@Test public void test_7125() { checkNotSubtype("X<X|{{X f1} f1}>","X<{X f1}>|null"); }
	@Test public void test_7126() { checkNotSubtype("X<X|{{X f1} f1}>","X<{X f2}>|null"); }
	@Test public void test_7127() { checkNotSubtype("X<X|{{X f1} f1}>","X<X|{null f1}>"); }
	@Test public void test_7128() { checkNotSubtype("X<X|{{X f1} f1}>","X<X|{null f2}>"); }
	@Test public void test_7129() { checkIsSubtype("X<X|{{X f1} f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7130() { checkIsSubtype("X<X|{{X f1} f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7131() { checkIsSubtype("X<X|{{X f1} f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7132() { checkIsSubtype("X<X|{{X f1} f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7133() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7134() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7135() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7136() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7137() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7138() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7139() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7140() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7141() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7142() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7143() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7144() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7145() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_7146() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_7147() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_7148() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_7149() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7150() { checkIsSubtype("X<X|{{X f1} f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7151() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7152() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7153() { checkNotSubtype("X<X|{{X f1} f1}>","(X<X|null>)|null"); }
	@Test public void test_7154() { checkNotSubtype("X<X|{{X f1} f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_7155() { checkIsSubtype("X<X|{{X f1} f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7156() { checkIsSubtype("X<X|{{X f1} f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7157() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7158() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7159() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7160() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7161() { checkIsSubtype("X<X|{{X f1} f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7162() { checkNotSubtype("X<X|{{X f2} f1}>","null"); }
	@Test public void test_7163() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f1}>"); }
	@Test public void test_7164() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f2}>"); }
	@Test public void test_7165() { checkNotSubtype("X<X|{{X f2} f1}>","{null f1}"); }
	@Test public void test_7166() { checkNotSubtype("X<X|{{X f2} f1}>","{null f2}"); }
	@Test public void test_7167() { checkIsSubtype("X<X|{{X f2} f1}>","{X<{X f1}> f1}"); }
	@Test public void test_7168() { checkIsSubtype("X<X|{{X f2} f1}>","{X<{X f2}> f1}"); }
	@Test public void test_7169() { checkIsSubtype("X<X|{{X f2} f1}>","{X<{X f1}> f2}"); }
	@Test public void test_7170() { checkIsSubtype("X<X|{{X f2} f1}>","{X<{X f2}> f2}"); }
	@Test public void test_7171() { checkNotSubtype("X<X|{{X f2} f1}>","X<X|null>"); }
	@Test public void test_7172() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_7173() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_7174() { checkNotSubtype("X<X|{{X f2} f1}>","{{null f1} f1}"); }
	@Test public void test_7175() { checkNotSubtype("X<X|{{X f2} f1}>","{{null f2} f1}"); }
	@Test public void test_7176() { checkNotSubtype("X<X|{{X f2} f1}>","{{null f1} f2}"); }
	@Test public void test_7177() { checkNotSubtype("X<X|{{X f2} f1}>","{{null f2} f2}"); }
	@Test public void test_7178() { checkIsSubtype("X<X|{{X f2} f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7179() { checkIsSubtype("X<X|{{X f2} f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7180() { checkIsSubtype("X<X|{{X f2} f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7181() { checkIsSubtype("X<X|{{X f2} f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7182() { checkIsSubtype("X<X|{{X f2} f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7183() { checkIsSubtype("X<X|{{X f2} f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7184() { checkIsSubtype("X<X|{{X f2} f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7185() { checkIsSubtype("X<X|{{X f2} f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7186() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7187() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7188() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7189() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7190() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7191() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7192() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7193() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7194() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7195() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7196() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7197() { checkIsSubtype("X<X|{{X f2} f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7198() { checkNotSubtype("X<X|{{X f2} f1}>","{X<X|null> f1}"); }
	@Test public void test_7199() { checkNotSubtype("X<X|{{X f2} f1}>","{X<X|null> f2}"); }
	@Test public void test_7200() { checkIsSubtype("X<X|{{X f2} f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7201() { checkIsSubtype("X<X|{{X f2} f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7202() { checkIsSubtype("X<X|{{X f2} f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7203() { checkIsSubtype("X<X|{{X f2} f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7204() { checkIsSubtype("X<X|{{X f2} f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7205() { checkIsSubtype("X<X|{{X f2} f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7206() { checkIsSubtype("X<X|{{X f2} f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7207() { checkIsSubtype("X<X|{{X f2} f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7208() { checkIsSubtype("X<X|{{X f2} f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7209() { checkIsSubtype("X<X|{{X f2} f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7210() { checkNotSubtype("X<X|{{X f2} f1}>","null|null"); }
	@Test public void test_7211() { checkNotSubtype("X<X|{{X f2} f1}>","null|X<{X f1}>"); }
	@Test public void test_7212() { checkNotSubtype("X<X|{{X f2} f1}>","null|X<{X f2}>"); }
	@Test public void test_7213() { checkNotSubtype("X<X|{{X f2} f1}>","null|{null f1}"); }
	@Test public void test_7214() { checkNotSubtype("X<X|{{X f2} f1}>","null|{null f2}"); }
	@Test public void test_7215() { checkNotSubtype("X<X|{{X f2} f1}>","null|(X<null|X>)"); }
	@Test public void test_7216() { checkNotSubtype("X<X|{{X f2} f1}>","{null f1}|null"); }
	@Test public void test_7217() { checkNotSubtype("X<X|{{X f2} f1}>","{null f2}|null"); }
	@Test public void test_7218() { checkNotSubtype("X<X|{{X f2} f1}>","X<{X f1}>|null"); }
	@Test public void test_7219() { checkNotSubtype("X<X|{{X f2} f1}>","X<{X f2}>|null"); }
	@Test public void test_7220() { checkNotSubtype("X<X|{{X f2} f1}>","X<X|{null f1}>"); }
	@Test public void test_7221() { checkNotSubtype("X<X|{{X f2} f1}>","X<X|{null f2}>"); }
	@Test public void test_7222() { checkIsSubtype("X<X|{{X f2} f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7223() { checkIsSubtype("X<X|{{X f2} f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7224() { checkIsSubtype("X<X|{{X f2} f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7225() { checkIsSubtype("X<X|{{X f2} f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7226() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7227() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7228() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7229() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7230() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7231() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7232() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7233() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7234() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7235() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7236() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7237() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7238() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_7239() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_7240() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_7241() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_7242() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7243() { checkIsSubtype("X<X|{{X f2} f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7244() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7245() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7246() { checkNotSubtype("X<X|{{X f2} f1}>","(X<X|null>)|null"); }
	@Test public void test_7247() { checkNotSubtype("X<X|{{X f2} f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_7248() { checkIsSubtype("X<X|{{X f2} f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7249() { checkIsSubtype("X<X|{{X f2} f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7250() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7251() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7252() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7253() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7254() { checkIsSubtype("X<X|{{X f2} f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7255() { checkNotSubtype("X<X|{{X f1} f2}>","null"); }
	@Test public void test_7256() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f1}>"); }
	@Test public void test_7257() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f2}>"); }
	@Test public void test_7258() { checkNotSubtype("X<X|{{X f1} f2}>","{null f1}"); }
	@Test public void test_7259() { checkNotSubtype("X<X|{{X f1} f2}>","{null f2}"); }
	@Test public void test_7260() { checkIsSubtype("X<X|{{X f1} f2}>","{X<{X f1}> f1}"); }
	@Test public void test_7261() { checkIsSubtype("X<X|{{X f1} f2}>","{X<{X f2}> f1}"); }
	@Test public void test_7262() { checkIsSubtype("X<X|{{X f1} f2}>","{X<{X f1}> f2}"); }
	@Test public void test_7263() { checkIsSubtype("X<X|{{X f1} f2}>","{X<{X f2}> f2}"); }
	@Test public void test_7264() { checkNotSubtype("X<X|{{X f1} f2}>","X<X|null>"); }
	@Test public void test_7265() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_7266() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_7267() { checkNotSubtype("X<X|{{X f1} f2}>","{{null f1} f1}"); }
	@Test public void test_7268() { checkNotSubtype("X<X|{{X f1} f2}>","{{null f2} f1}"); }
	@Test public void test_7269() { checkNotSubtype("X<X|{{X f1} f2}>","{{null f1} f2}"); }
	@Test public void test_7270() { checkNotSubtype("X<X|{{X f1} f2}>","{{null f2} f2}"); }
	@Test public void test_7271() { checkIsSubtype("X<X|{{X f1} f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7272() { checkIsSubtype("X<X|{{X f1} f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7273() { checkIsSubtype("X<X|{{X f1} f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7274() { checkIsSubtype("X<X|{{X f1} f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7275() { checkIsSubtype("X<X|{{X f1} f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7276() { checkIsSubtype("X<X|{{X f1} f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7277() { checkIsSubtype("X<X|{{X f1} f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7278() { checkIsSubtype("X<X|{{X f1} f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7279() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7280() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7281() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7282() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7283() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7284() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7285() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7286() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7287() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7288() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7289() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7290() { checkIsSubtype("X<X|{{X f1} f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7291() { checkNotSubtype("X<X|{{X f1} f2}>","{X<X|null> f1}"); }
	@Test public void test_7292() { checkNotSubtype("X<X|{{X f1} f2}>","{X<X|null> f2}"); }
	@Test public void test_7293() { checkIsSubtype("X<X|{{X f1} f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7294() { checkIsSubtype("X<X|{{X f1} f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7295() { checkIsSubtype("X<X|{{X f1} f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7296() { checkIsSubtype("X<X|{{X f1} f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7297() { checkIsSubtype("X<X|{{X f1} f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7298() { checkIsSubtype("X<X|{{X f1} f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7299() { checkIsSubtype("X<X|{{X f1} f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7300() { checkIsSubtype("X<X|{{X f1} f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7301() { checkIsSubtype("X<X|{{X f1} f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7302() { checkIsSubtype("X<X|{{X f1} f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7303() { checkNotSubtype("X<X|{{X f1} f2}>","null|null"); }
	@Test public void test_7304() { checkNotSubtype("X<X|{{X f1} f2}>","null|X<{X f1}>"); }
	@Test public void test_7305() { checkNotSubtype("X<X|{{X f1} f2}>","null|X<{X f2}>"); }
	@Test public void test_7306() { checkNotSubtype("X<X|{{X f1} f2}>","null|{null f1}"); }
	@Test public void test_7307() { checkNotSubtype("X<X|{{X f1} f2}>","null|{null f2}"); }
	@Test public void test_7308() { checkNotSubtype("X<X|{{X f1} f2}>","null|(X<null|X>)"); }
	@Test public void test_7309() { checkNotSubtype("X<X|{{X f1} f2}>","{null f1}|null"); }
	@Test public void test_7310() { checkNotSubtype("X<X|{{X f1} f2}>","{null f2}|null"); }
	@Test public void test_7311() { checkNotSubtype("X<X|{{X f1} f2}>","X<{X f1}>|null"); }
	@Test public void test_7312() { checkNotSubtype("X<X|{{X f1} f2}>","X<{X f2}>|null"); }
	@Test public void test_7313() { checkNotSubtype("X<X|{{X f1} f2}>","X<X|{null f1}>"); }
	@Test public void test_7314() { checkNotSubtype("X<X|{{X f1} f2}>","X<X|{null f2}>"); }
	@Test public void test_7315() { checkIsSubtype("X<X|{{X f1} f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7316() { checkIsSubtype("X<X|{{X f1} f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7317() { checkIsSubtype("X<X|{{X f1} f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7318() { checkIsSubtype("X<X|{{X f1} f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7319() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7320() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7321() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7322() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7323() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7324() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7325() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7326() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7327() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7328() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7329() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7330() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7331() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_7332() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_7333() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_7334() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_7335() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7336() { checkIsSubtype("X<X|{{X f1} f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7337() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7338() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7339() { checkNotSubtype("X<X|{{X f1} f2}>","(X<X|null>)|null"); }
	@Test public void test_7340() { checkNotSubtype("X<X|{{X f1} f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_7341() { checkIsSubtype("X<X|{{X f1} f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7342() { checkIsSubtype("X<X|{{X f1} f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7343() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7344() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7345() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7346() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7347() { checkIsSubtype("X<X|{{X f1} f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7348() { checkNotSubtype("X<X|{{X f2} f2}>","null"); }
	@Test public void test_7349() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f1}>"); }
	@Test public void test_7350() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f2}>"); }
	@Test public void test_7351() { checkNotSubtype("X<X|{{X f2} f2}>","{null f1}"); }
	@Test public void test_7352() { checkNotSubtype("X<X|{{X f2} f2}>","{null f2}"); }
	@Test public void test_7353() { checkIsSubtype("X<X|{{X f2} f2}>","{X<{X f1}> f1}"); }
	@Test public void test_7354() { checkIsSubtype("X<X|{{X f2} f2}>","{X<{X f2}> f1}"); }
	@Test public void test_7355() { checkIsSubtype("X<X|{{X f2} f2}>","{X<{X f1}> f2}"); }
	@Test public void test_7356() { checkIsSubtype("X<X|{{X f2} f2}>","{X<{X f2}> f2}"); }
	@Test public void test_7357() { checkNotSubtype("X<X|{{X f2} f2}>","X<X|null>"); }
	@Test public void test_7358() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_7359() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_7360() { checkNotSubtype("X<X|{{X f2} f2}>","{{null f1} f1}"); }
	@Test public void test_7361() { checkNotSubtype("X<X|{{X f2} f2}>","{{null f2} f1}"); }
	@Test public void test_7362() { checkNotSubtype("X<X|{{X f2} f2}>","{{null f1} f2}"); }
	@Test public void test_7363() { checkNotSubtype("X<X|{{X f2} f2}>","{{null f2} f2}"); }
	@Test public void test_7364() { checkIsSubtype("X<X|{{X f2} f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7365() { checkIsSubtype("X<X|{{X f2} f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7366() { checkIsSubtype("X<X|{{X f2} f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7367() { checkIsSubtype("X<X|{{X f2} f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7368() { checkIsSubtype("X<X|{{X f2} f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7369() { checkIsSubtype("X<X|{{X f2} f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7370() { checkIsSubtype("X<X|{{X f2} f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7371() { checkIsSubtype("X<X|{{X f2} f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7372() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7373() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7374() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7375() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7376() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7377() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7378() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7379() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7380() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7381() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7382() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7383() { checkIsSubtype("X<X|{{X f2} f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7384() { checkNotSubtype("X<X|{{X f2} f2}>","{X<X|null> f1}"); }
	@Test public void test_7385() { checkNotSubtype("X<X|{{X f2} f2}>","{X<X|null> f2}"); }
	@Test public void test_7386() { checkIsSubtype("X<X|{{X f2} f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7387() { checkIsSubtype("X<X|{{X f2} f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7388() { checkIsSubtype("X<X|{{X f2} f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7389() { checkIsSubtype("X<X|{{X f2} f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7390() { checkIsSubtype("X<X|{{X f2} f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7391() { checkIsSubtype("X<X|{{X f2} f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7392() { checkIsSubtype("X<X|{{X f2} f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7393() { checkIsSubtype("X<X|{{X f2} f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7394() { checkIsSubtype("X<X|{{X f2} f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7395() { checkIsSubtype("X<X|{{X f2} f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7396() { checkNotSubtype("X<X|{{X f2} f2}>","null|null"); }
	@Test public void test_7397() { checkNotSubtype("X<X|{{X f2} f2}>","null|X<{X f1}>"); }
	@Test public void test_7398() { checkNotSubtype("X<X|{{X f2} f2}>","null|X<{X f2}>"); }
	@Test public void test_7399() { checkNotSubtype("X<X|{{X f2} f2}>","null|{null f1}"); }
	@Test public void test_7400() { checkNotSubtype("X<X|{{X f2} f2}>","null|{null f2}"); }
	@Test public void test_7401() { checkNotSubtype("X<X|{{X f2} f2}>","null|(X<null|X>)"); }
	@Test public void test_7402() { checkNotSubtype("X<X|{{X f2} f2}>","{null f1}|null"); }
	@Test public void test_7403() { checkNotSubtype("X<X|{{X f2} f2}>","{null f2}|null"); }
	@Test public void test_7404() { checkNotSubtype("X<X|{{X f2} f2}>","X<{X f1}>|null"); }
	@Test public void test_7405() { checkNotSubtype("X<X|{{X f2} f2}>","X<{X f2}>|null"); }
	@Test public void test_7406() { checkNotSubtype("X<X|{{X f2} f2}>","X<X|{null f1}>"); }
	@Test public void test_7407() { checkNotSubtype("X<X|{{X f2} f2}>","X<X|{null f2}>"); }
	@Test public void test_7408() { checkIsSubtype("X<X|{{X f2} f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7409() { checkIsSubtype("X<X|{{X f2} f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7410() { checkIsSubtype("X<X|{{X f2} f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7411() { checkIsSubtype("X<X|{{X f2} f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7412() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7413() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7414() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7415() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7416() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7417() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7418() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7419() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7420() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7421() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7422() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7423() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7424() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_7425() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_7426() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_7427() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_7428() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7429() { checkIsSubtype("X<X|{{X f2} f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7430() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7431() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7432() { checkNotSubtype("X<X|{{X f2} f2}>","(X<X|null>)|null"); }
	@Test public void test_7433() { checkNotSubtype("X<X|{{X f2} f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_7434() { checkIsSubtype("X<X|{{X f2} f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7435() { checkIsSubtype("X<X|{{X f2} f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7436() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7437() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7438() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7439() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7440() { checkIsSubtype("X<X|{{X f2} f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7441() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","null"); }
	@Test public void test_7442() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f1}>"); }
	@Test public void test_7443() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f2}>"); }
	@Test public void test_7444() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{null f1}"); }
	@Test public void test_7445() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{null f2}"); }
	@Test public void test_7446() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<{X f1}> f1}"); }
	@Test public void test_7447() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<{X f2}> f1}"); }
	@Test public void test_7448() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<{X f1}> f2}"); }
	@Test public void test_7449() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<{X f2}> f2}"); }
	@Test public void test_7450() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|null>"); }
	@Test public void test_7451() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|Y<{Y f1}>>"); }
	@Test public void test_7452() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|Y<{Y f2}>>"); }
	@Test public void test_7453() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{null f1} f1}"); }
	@Test public void test_7454() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{null f2} f1}"); }
	@Test public void test_7455() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{null f1} f2}"); }
	@Test public void test_7456() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{null f2} f2}"); }
	@Test public void test_7457() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7458() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7459() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7460() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7461() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7462() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7463() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7464() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7465() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7466() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7467() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7468() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7469() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7470() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7471() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7472() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7473() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7474() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7475() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7476() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7477() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<X|null> f1}"); }
	@Test public void test_7478() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<X|null> f2}"); }
	@Test public void test_7479() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7480() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7481() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7482() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7483() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7484() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7485() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7486() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7487() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7488() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7489() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","null|null"); }
	@Test public void test_7490() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","null|X<{X f1}>"); }
	@Test public void test_7491() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","null|X<{X f2}>"); }
	@Test public void test_7492() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","null|{null f1}"); }
	@Test public void test_7493() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","null|{null f2}"); }
	@Test public void test_7494() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","null|(X<null|X>)"); }
	@Test public void test_7495() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{null f1}|null"); }
	@Test public void test_7496() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{null f2}|null"); }
	@Test public void test_7497() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f1}>|null"); }
	@Test public void test_7498() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f2}>|null"); }
	@Test public void test_7499() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{null f1}>"); }
	@Test public void test_7500() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{null f2}>"); }
	@Test public void test_7501() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7502() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7503() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7504() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7505() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7506() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7507() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7508() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7509() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7510() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7511() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7512() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7513() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7514() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7515() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7516() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7517() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{{X f1} f1}>"); }
	@Test public void test_7518() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{{X f2} f1}>"); }
	@Test public void test_7519() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{{X f1} f2}>"); }
	@Test public void test_7520() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{{X f2} f2}>"); }
	@Test public void test_7521() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7522() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7523() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7524() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7525() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","(X<X|null>)|null"); }
	@Test public void test_7526() { checkNotSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|(Y<Y|null>)>"); }
	@Test public void test_7527() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7528() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7529() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7530() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7531() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7532() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7533() { checkIsSubtype("X<{X f1}>|(Y<X<{X f1}>|Y>)","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7534() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","null"); }
	@Test public void test_7535() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f1}>"); }
	@Test public void test_7536() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f2}>"); }
	@Test public void test_7537() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{null f1}"); }
	@Test public void test_7538() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{null f2}"); }
	@Test public void test_7539() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<{X f1}> f1}"); }
	@Test public void test_7540() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<{X f2}> f1}"); }
	@Test public void test_7541() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<{X f1}> f2}"); }
	@Test public void test_7542() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<{X f2}> f2}"); }
	@Test public void test_7543() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|null>"); }
	@Test public void test_7544() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|Y<{Y f1}>>"); }
	@Test public void test_7545() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|Y<{Y f2}>>"); }
	@Test public void test_7546() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{null f1} f1}"); }
	@Test public void test_7547() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{null f2} f1}"); }
	@Test public void test_7548() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{null f1} f2}"); }
	@Test public void test_7549() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{null f2} f2}"); }
	@Test public void test_7550() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7551() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7552() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7553() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7554() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7555() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7556() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7557() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7558() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7559() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7560() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7561() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7562() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7563() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7564() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7565() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7566() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7567() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7568() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7569() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7570() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<X|null> f1}"); }
	@Test public void test_7571() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<X|null> f2}"); }
	@Test public void test_7572() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7573() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7574() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7575() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7576() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7577() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7578() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7579() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7580() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7581() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7582() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","null|null"); }
	@Test public void test_7583() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","null|X<{X f1}>"); }
	@Test public void test_7584() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","null|X<{X f2}>"); }
	@Test public void test_7585() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","null|{null f1}"); }
	@Test public void test_7586() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","null|{null f2}"); }
	@Test public void test_7587() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","null|(X<null|X>)"); }
	@Test public void test_7588() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{null f1}|null"); }
	@Test public void test_7589() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{null f2}|null"); }
	@Test public void test_7590() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f1}>|null"); }
	@Test public void test_7591() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f2}>|null"); }
	@Test public void test_7592() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{null f1}>"); }
	@Test public void test_7593() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{null f2}>"); }
	@Test public void test_7594() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7595() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7596() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7597() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7598() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7599() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7600() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7601() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7602() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7603() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7604() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7605() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7606() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7607() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7608() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7609() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7610() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{{X f1} f1}>"); }
	@Test public void test_7611() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{{X f2} f1}>"); }
	@Test public void test_7612() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{{X f1} f2}>"); }
	@Test public void test_7613() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{{X f2} f2}>"); }
	@Test public void test_7614() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7615() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7616() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7617() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7618() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","(X<X|null>)|null"); }
	@Test public void test_7619() { checkNotSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|(Y<Y|null>)>"); }
	@Test public void test_7620() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7621() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7622() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7623() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7624() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7625() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7626() { checkIsSubtype("X<{X f2}>|(Y<X<{X f2}>|Y>)","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7627() { checkNotSubtype("X<X|{Y<X|Y> f1}>","null"); }
	@Test public void test_7628() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f1}>"); }
	@Test public void test_7629() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f2}>"); }
	@Test public void test_7630() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{null f1}"); }
	@Test public void test_7631() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{null f2}"); }
	@Test public void test_7632() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<{X f1}> f1}"); }
	@Test public void test_7633() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<{X f2}> f1}"); }
	@Test public void test_7634() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<{X f1}> f2}"); }
	@Test public void test_7635() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<{X f2}> f2}"); }
	@Test public void test_7636() { checkNotSubtype("X<X|{Y<X|Y> f1}>","X<X|null>"); }
	@Test public void test_7637() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_7638() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_7639() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{{null f1} f1}"); }
	@Test public void test_7640() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{{null f2} f1}"); }
	@Test public void test_7641() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{{null f1} f2}"); }
	@Test public void test_7642() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{{null f2} f2}"); }
	@Test public void test_7643() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7644() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7645() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7646() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7647() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7648() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7649() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7650() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7651() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7652() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7653() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7654() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7655() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7656() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7657() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7658() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7659() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7660() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7661() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7662() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7663() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{X<X|null> f1}"); }
	@Test public void test_7664() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{X<X|null> f2}"); }
	@Test public void test_7665() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7666() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7667() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7668() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7669() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7670() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7671() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7672() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7673() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7674() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7675() { checkNotSubtype("X<X|{Y<X|Y> f1}>","null|null"); }
	@Test public void test_7676() { checkNotSubtype("X<X|{Y<X|Y> f1}>","null|X<{X f1}>"); }
	@Test public void test_7677() { checkNotSubtype("X<X|{Y<X|Y> f1}>","null|X<{X f2}>"); }
	@Test public void test_7678() { checkNotSubtype("X<X|{Y<X|Y> f1}>","null|{null f1}"); }
	@Test public void test_7679() { checkNotSubtype("X<X|{Y<X|Y> f1}>","null|{null f2}"); }
	@Test public void test_7680() { checkNotSubtype("X<X|{Y<X|Y> f1}>","null|(X<null|X>)"); }
	@Test public void test_7681() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{null f1}|null"); }
	@Test public void test_7682() { checkNotSubtype("X<X|{Y<X|Y> f1}>","{null f2}|null"); }
	@Test public void test_7683() { checkNotSubtype("X<X|{Y<X|Y> f1}>","X<{X f1}>|null"); }
	@Test public void test_7684() { checkNotSubtype("X<X|{Y<X|Y> f1}>","X<{X f2}>|null"); }
	@Test public void test_7685() { checkNotSubtype("X<X|{Y<X|Y> f1}>","X<X|{null f1}>"); }
	@Test public void test_7686() { checkNotSubtype("X<X|{Y<X|Y> f1}>","X<X|{null f2}>"); }
	@Test public void test_7687() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7688() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7689() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7690() { checkIsSubtype("X<X|{Y<X|Y> f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7691() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7692() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7693() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7694() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7695() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7696() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7697() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7698() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7699() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7700() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7701() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7702() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7703() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_7704() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_7705() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_7706() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_7707() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7708() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7709() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7710() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7711() { checkNotSubtype("X<X|{Y<X|Y> f1}>","(X<X|null>)|null"); }
	@Test public void test_7712() { checkNotSubtype("X<X|{Y<X|Y> f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_7713() { checkIsSubtype("X<X|{Y<X|Y> f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7714() { checkIsSubtype("X<X|{Y<X|Y> f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7715() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7716() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7717() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7718() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7719() { checkIsSubtype("X<X|{Y<X|Y> f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7720() { checkNotSubtype("X<X|{Y<X|Y> f2}>","null"); }
	@Test public void test_7721() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f1}>"); }
	@Test public void test_7722() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f2}>"); }
	@Test public void test_7723() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{null f1}"); }
	@Test public void test_7724() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{null f2}"); }
	@Test public void test_7725() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<{X f1}> f1}"); }
	@Test public void test_7726() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<{X f2}> f1}"); }
	@Test public void test_7727() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<{X f1}> f2}"); }
	@Test public void test_7728() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<{X f2}> f2}"); }
	@Test public void test_7729() { checkNotSubtype("X<X|{Y<X|Y> f2}>","X<X|null>"); }
	@Test public void test_7730() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_7731() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_7732() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{{null f1} f1}"); }
	@Test public void test_7733() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{{null f2} f1}"); }
	@Test public void test_7734() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{{null f1} f2}"); }
	@Test public void test_7735() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{{null f2} f2}"); }
	@Test public void test_7736() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7737() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7738() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7739() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7740() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7741() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7742() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7743() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7744() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7745() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7746() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7747() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7748() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7749() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7750() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7751() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7752() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7753() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7754() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7755() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7756() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{X<X|null> f1}"); }
	@Test public void test_7757() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{X<X|null> f2}"); }
	@Test public void test_7758() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7759() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7760() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7761() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7762() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7763() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7764() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7765() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7766() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7767() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7768() { checkNotSubtype("X<X|{Y<X|Y> f2}>","null|null"); }
	@Test public void test_7769() { checkNotSubtype("X<X|{Y<X|Y> f2}>","null|X<{X f1}>"); }
	@Test public void test_7770() { checkNotSubtype("X<X|{Y<X|Y> f2}>","null|X<{X f2}>"); }
	@Test public void test_7771() { checkNotSubtype("X<X|{Y<X|Y> f2}>","null|{null f1}"); }
	@Test public void test_7772() { checkNotSubtype("X<X|{Y<X|Y> f2}>","null|{null f2}"); }
	@Test public void test_7773() { checkNotSubtype("X<X|{Y<X|Y> f2}>","null|(X<null|X>)"); }
	@Test public void test_7774() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{null f1}|null"); }
	@Test public void test_7775() { checkNotSubtype("X<X|{Y<X|Y> f2}>","{null f2}|null"); }
	@Test public void test_7776() { checkNotSubtype("X<X|{Y<X|Y> f2}>","X<{X f1}>|null"); }
	@Test public void test_7777() { checkNotSubtype("X<X|{Y<X|Y> f2}>","X<{X f2}>|null"); }
	@Test public void test_7778() { checkNotSubtype("X<X|{Y<X|Y> f2}>","X<X|{null f1}>"); }
	@Test public void test_7779() { checkNotSubtype("X<X|{Y<X|Y> f2}>","X<X|{null f2}>"); }
	@Test public void test_7780() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7781() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7782() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7783() { checkIsSubtype("X<X|{Y<X|Y> f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7784() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7785() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7786() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7787() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7788() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7789() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7790() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7791() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7792() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7793() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7794() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7795() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7796() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_7797() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_7798() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_7799() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_7800() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7801() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7802() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7803() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7804() { checkNotSubtype("X<X|{Y<X|Y> f2}>","(X<X|null>)|null"); }
	@Test public void test_7805() { checkNotSubtype("X<X|{Y<X|Y> f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_7806() { checkIsSubtype("X<X|{Y<X|Y> f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7807() { checkIsSubtype("X<X|{Y<X|Y> f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7808() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7809() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7810() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7811() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7812() { checkIsSubtype("X<X|{Y<X|Y> f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7813() { checkIsSubtype("(X<X|null>)|null","null"); }
	@Test public void test_7814() { checkIsSubtype("(X<X|null>)|null","X<{X f1}>"); }
	@Test public void test_7815() { checkIsSubtype("(X<X|null>)|null","X<{X f2}>"); }
	@Test public void test_7816() { checkNotSubtype("(X<X|null>)|null","{null f1}"); }
	@Test public void test_7817() { checkNotSubtype("(X<X|null>)|null","{null f2}"); }
	@Test public void test_7818() { checkIsSubtype("(X<X|null>)|null","{X<{X f1}> f1}"); }
	@Test public void test_7819() { checkIsSubtype("(X<X|null>)|null","{X<{X f2}> f1}"); }
	@Test public void test_7820() { checkIsSubtype("(X<X|null>)|null","{X<{X f1}> f2}"); }
	@Test public void test_7821() { checkIsSubtype("(X<X|null>)|null","{X<{X f2}> f2}"); }
	@Test public void test_7822() { checkIsSubtype("(X<X|null>)|null","X<X|null>"); }
	@Test public void test_7823() { checkIsSubtype("(X<X|null>)|null","X<X|Y<{Y f1}>>"); }
	@Test public void test_7824() { checkIsSubtype("(X<X|null>)|null","X<X|Y<{Y f2}>>"); }
	@Test public void test_7825() { checkNotSubtype("(X<X|null>)|null","{{null f1} f1}"); }
	@Test public void test_7826() { checkNotSubtype("(X<X|null>)|null","{{null f2} f1}"); }
	@Test public void test_7827() { checkNotSubtype("(X<X|null>)|null","{{null f1} f2}"); }
	@Test public void test_7828() { checkNotSubtype("(X<X|null>)|null","{{null f2} f2}"); }
	@Test public void test_7829() { checkIsSubtype("(X<X|null>)|null","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7830() { checkIsSubtype("(X<X|null>)|null","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7831() { checkIsSubtype("(X<X|null>)|null","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7832() { checkIsSubtype("(X<X|null>)|null","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7833() { checkIsSubtype("(X<X|null>)|null","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7834() { checkIsSubtype("(X<X|null>)|null","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7835() { checkIsSubtype("(X<X|null>)|null","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7836() { checkIsSubtype("(X<X|null>)|null","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7837() { checkIsSubtype("(X<X|null>)|null","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7838() { checkIsSubtype("(X<X|null>)|null","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7839() { checkIsSubtype("(X<X|null>)|null","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7840() { checkIsSubtype("(X<X|null>)|null","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7841() { checkIsSubtype("(X<X|null>)|null","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7842() { checkIsSubtype("(X<X|null>)|null","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7843() { checkIsSubtype("(X<X|null>)|null","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7844() { checkIsSubtype("(X<X|null>)|null","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7845() { checkIsSubtype("(X<X|null>)|null","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7846() { checkIsSubtype("(X<X|null>)|null","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7847() { checkIsSubtype("(X<X|null>)|null","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7848() { checkIsSubtype("(X<X|null>)|null","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7849() { checkNotSubtype("(X<X|null>)|null","{X<X|null> f1}"); }
	@Test public void test_7850() { checkNotSubtype("(X<X|null>)|null","{X<X|null> f2}"); }
	@Test public void test_7851() { checkIsSubtype("(X<X|null>)|null","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7852() { checkIsSubtype("(X<X|null>)|null","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7853() { checkIsSubtype("(X<X|null>)|null","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7854() { checkIsSubtype("(X<X|null>)|null","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7855() { checkIsSubtype("(X<X|null>)|null","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7856() { checkIsSubtype("(X<X|null>)|null","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7857() { checkIsSubtype("(X<X|null>)|null","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7858() { checkIsSubtype("(X<X|null>)|null","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7859() { checkIsSubtype("(X<X|null>)|null","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7860() { checkIsSubtype("(X<X|null>)|null","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7861() { checkIsSubtype("(X<X|null>)|null","null|null"); }
	@Test public void test_7862() { checkIsSubtype("(X<X|null>)|null","null|X<{X f1}>"); }
	@Test public void test_7863() { checkIsSubtype("(X<X|null>)|null","null|X<{X f2}>"); }
	@Test public void test_7864() { checkNotSubtype("(X<X|null>)|null","null|{null f1}"); }
	@Test public void test_7865() { checkNotSubtype("(X<X|null>)|null","null|{null f2}"); }
	@Test public void test_7866() { checkIsSubtype("(X<X|null>)|null","null|(X<null|X>)"); }
	@Test public void test_7867() { checkNotSubtype("(X<X|null>)|null","{null f1}|null"); }
	@Test public void test_7868() { checkNotSubtype("(X<X|null>)|null","{null f2}|null"); }
	@Test public void test_7869() { checkIsSubtype("(X<X|null>)|null","X<{X f1}>|null"); }
	@Test public void test_7870() { checkIsSubtype("(X<X|null>)|null","X<{X f2}>|null"); }
	@Test public void test_7871() { checkNotSubtype("(X<X|null>)|null","X<X|{null f1}>"); }
	@Test public void test_7872() { checkNotSubtype("(X<X|null>)|null","X<X|{null f2}>"); }
	@Test public void test_7873() { checkIsSubtype("(X<X|null>)|null","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7874() { checkIsSubtype("(X<X|null>)|null","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7875() { checkIsSubtype("(X<X|null>)|null","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7876() { checkIsSubtype("(X<X|null>)|null","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7877() { checkIsSubtype("(X<X|null>)|null","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7878() { checkIsSubtype("(X<X|null>)|null","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7879() { checkIsSubtype("(X<X|null>)|null","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7880() { checkIsSubtype("(X<X|null>)|null","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7881() { checkIsSubtype("(X<X|null>)|null","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7882() { checkIsSubtype("(X<X|null>)|null","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7883() { checkIsSubtype("(X<X|null>)|null","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7884() { checkIsSubtype("(X<X|null>)|null","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7885() { checkIsSubtype("(X<X|null>)|null","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7886() { checkIsSubtype("(X<X|null>)|null","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7887() { checkIsSubtype("(X<X|null>)|null","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7888() { checkIsSubtype("(X<X|null>)|null","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7889() { checkIsSubtype("(X<X|null>)|null","X<X|{{X f1} f1}>"); }
	@Test public void test_7890() { checkIsSubtype("(X<X|null>)|null","X<X|{{X f2} f1}>"); }
	@Test public void test_7891() { checkIsSubtype("(X<X|null>)|null","X<X|{{X f1} f2}>"); }
	@Test public void test_7892() { checkIsSubtype("(X<X|null>)|null","X<X|{{X f2} f2}>"); }
	@Test public void test_7893() { checkIsSubtype("(X<X|null>)|null","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7894() { checkIsSubtype("(X<X|null>)|null","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7895() { checkIsSubtype("(X<X|null>)|null","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7896() { checkIsSubtype("(X<X|null>)|null","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7897() { checkIsSubtype("(X<X|null>)|null","(X<X|null>)|null"); }
	@Test public void test_7898() { checkIsSubtype("(X<X|null>)|null","X<X|(Y<Y|null>)>"); }
	@Test public void test_7899() { checkIsSubtype("(X<X|null>)|null","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7900() { checkIsSubtype("(X<X|null>)|null","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7901() { checkIsSubtype("(X<X|null>)|null","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7902() { checkIsSubtype("(X<X|null>)|null","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7903() { checkIsSubtype("(X<X|null>)|null","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7904() { checkIsSubtype("(X<X|null>)|null","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7905() { checkIsSubtype("(X<X|null>)|null","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7906() { checkIsSubtype("X<X|(Y<Y|null>)>","null"); }
	@Test public void test_7907() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f1}>"); }
	@Test public void test_7908() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f2}>"); }
	@Test public void test_7909() { checkNotSubtype("X<X|(Y<Y|null>)>","{null f1}"); }
	@Test public void test_7910() { checkNotSubtype("X<X|(Y<Y|null>)>","{null f2}"); }
	@Test public void test_7911() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<{X f1}> f1}"); }
	@Test public void test_7912() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<{X f2}> f1}"); }
	@Test public void test_7913() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<{X f1}> f2}"); }
	@Test public void test_7914() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<{X f2}> f2}"); }
	@Test public void test_7915() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|null>"); }
	@Test public void test_7916() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|Y<{Y f1}>>"); }
	@Test public void test_7917() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|Y<{Y f2}>>"); }
	@Test public void test_7918() { checkNotSubtype("X<X|(Y<Y|null>)>","{{null f1} f1}"); }
	@Test public void test_7919() { checkNotSubtype("X<X|(Y<Y|null>)>","{{null f2} f1}"); }
	@Test public void test_7920() { checkNotSubtype("X<X|(Y<Y|null>)>","{{null f1} f2}"); }
	@Test public void test_7921() { checkNotSubtype("X<X|(Y<Y|null>)>","{{null f2} f2}"); }
	@Test public void test_7922() { checkIsSubtype("X<X|(Y<Y|null>)>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_7923() { checkIsSubtype("X<X|(Y<Y|null>)>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_7924() { checkIsSubtype("X<X|(Y<Y|null>)>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_7925() { checkIsSubtype("X<X|(Y<Y|null>)>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_7926() { checkIsSubtype("X<X|(Y<Y|null>)>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_7927() { checkIsSubtype("X<X|(Y<Y|null>)>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_7928() { checkIsSubtype("X<X|(Y<Y|null>)>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_7929() { checkIsSubtype("X<X|(Y<Y|null>)>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_7930() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_7931() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_7932() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_7933() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_7934() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_7935() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_7936() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_7937() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_7938() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_7939() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_7940() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_7941() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_7942() { checkNotSubtype("X<X|(Y<Y|null>)>","{X<X|null> f1}"); }
	@Test public void test_7943() { checkNotSubtype("X<X|(Y<Y|null>)>","{X<X|null> f2}"); }
	@Test public void test_7944() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_7945() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_7946() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_7947() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_7948() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_7949() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_7950() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_7951() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_7952() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_7953() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_7954() { checkIsSubtype("X<X|(Y<Y|null>)>","null|null"); }
	@Test public void test_7955() { checkIsSubtype("X<X|(Y<Y|null>)>","null|X<{X f1}>"); }
	@Test public void test_7956() { checkIsSubtype("X<X|(Y<Y|null>)>","null|X<{X f2}>"); }
	@Test public void test_7957() { checkNotSubtype("X<X|(Y<Y|null>)>","null|{null f1}"); }
	@Test public void test_7958() { checkNotSubtype("X<X|(Y<Y|null>)>","null|{null f2}"); }
	@Test public void test_7959() { checkIsSubtype("X<X|(Y<Y|null>)>","null|(X<null|X>)"); }
	@Test public void test_7960() { checkNotSubtype("X<X|(Y<Y|null>)>","{null f1}|null"); }
	@Test public void test_7961() { checkNotSubtype("X<X|(Y<Y|null>)>","{null f2}|null"); }
	@Test public void test_7962() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f1}>|null"); }
	@Test public void test_7963() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f2}>|null"); }
	@Test public void test_7964() { checkNotSubtype("X<X|(Y<Y|null>)>","X<X|{null f1}>"); }
	@Test public void test_7965() { checkNotSubtype("X<X|(Y<Y|null>)>","X<X|{null f2}>"); }
	@Test public void test_7966() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_7967() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_7968() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_7969() { checkIsSubtype("X<X|(Y<Y|null>)>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_7970() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_7971() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_7972() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_7973() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_7974() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_7975() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_7976() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_7977() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_7978() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_7979() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_7980() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_7981() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_7982() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{{X f1} f1}>"); }
	@Test public void test_7983() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{{X f2} f1}>"); }
	@Test public void test_7984() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{{X f1} f2}>"); }
	@Test public void test_7985() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{{X f2} f2}>"); }
	@Test public void test_7986() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_7987() { checkIsSubtype("X<X|(Y<Y|null>)>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_7988() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_7989() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_7990() { checkIsSubtype("X<X|(Y<Y|null>)>","(X<X|null>)|null"); }
	@Test public void test_7991() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|(Y<Y|null>)>"); }
	@Test public void test_7992() { checkIsSubtype("X<X|(Y<Y|null>)>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_7993() { checkIsSubtype("X<X|(Y<Y|null>)>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_7994() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_7995() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_7996() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_7997() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_7998() { checkIsSubtype("X<X|(Y<Y|null>)>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_7999() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","null"); }
	@Test public void test_8000() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f1}>"); }
	@Test public void test_8001() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f2}>"); }
	@Test public void test_8002() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{null f1}"); }
	@Test public void test_8003() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{null f2}"); }
	@Test public void test_8004() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<{X f1}> f1}"); }
	@Test public void test_8005() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<{X f2}> f1}"); }
	@Test public void test_8006() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<{X f1}> f2}"); }
	@Test public void test_8007() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<{X f2}> f2}"); }
	@Test public void test_8008() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|null>"); }
	@Test public void test_8009() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_8010() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_8011() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{null f1} f1}"); }
	@Test public void test_8012() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{null f2} f1}"); }
	@Test public void test_8013() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{null f1} f2}"); }
	@Test public void test_8014() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{null f2} f2}"); }
	@Test public void test_8015() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_8016() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_8017() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_8018() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_8019() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_8020() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_8021() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_8022() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_8023() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_8024() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_8025() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_8026() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_8027() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_8028() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_8029() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_8030() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_8031() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_8032() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_8033() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_8034() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_8035() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<X|null> f1}"); }
	@Test public void test_8036() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<X|null> f2}"); }
	@Test public void test_8037() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_8038() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_8039() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_8040() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_8041() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_8042() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_8043() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_8044() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_8045() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_8046() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_8047() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","null|null"); }
	@Test public void test_8048() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","null|X<{X f1}>"); }
	@Test public void test_8049() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","null|X<{X f2}>"); }
	@Test public void test_8050() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","null|{null f1}"); }
	@Test public void test_8051() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","null|{null f2}"); }
	@Test public void test_8052() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","null|(X<null|X>)"); }
	@Test public void test_8053() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{null f1}|null"); }
	@Test public void test_8054() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{null f2}|null"); }
	@Test public void test_8055() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f1}>|null"); }
	@Test public void test_8056() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f2}>|null"); }
	@Test public void test_8057() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{null f1}>"); }
	@Test public void test_8058() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{null f2}>"); }
	@Test public void test_8059() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_8060() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_8061() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_8062() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_8063() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_8064() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_8065() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_8066() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_8067() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_8068() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_8069() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_8070() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_8071() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_8072() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_8073() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_8074() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_8075() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{{X f1} f1}>"); }
	@Test public void test_8076() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{{X f2} f1}>"); }
	@Test public void test_8077() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{{X f1} f2}>"); }
	@Test public void test_8078() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{{X f2} f2}>"); }
	@Test public void test_8079() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_8080() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_8081() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_8082() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_8083() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","(X<X|null>)|null"); }
	@Test public void test_8084() { checkNotSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_8085() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_8086() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_8087() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_8088() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_8089() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_8090() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_8091() { checkIsSubtype("(X<X|Y<{Y f1}>>)|Y<{Y f1}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_8092() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","null"); }
	@Test public void test_8093() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f1}>"); }
	@Test public void test_8094() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f2}>"); }
	@Test public void test_8095() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{null f1}"); }
	@Test public void test_8096() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{null f2}"); }
	@Test public void test_8097() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<{X f1}> f1}"); }
	@Test public void test_8098() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<{X f2}> f1}"); }
	@Test public void test_8099() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<{X f1}> f2}"); }
	@Test public void test_8100() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<{X f2}> f2}"); }
	@Test public void test_8101() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|null>"); }
	@Test public void test_8102() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|Y<{Y f1}>>"); }
	@Test public void test_8103() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|Y<{Y f2}>>"); }
	@Test public void test_8104() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{null f1} f1}"); }
	@Test public void test_8105() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{null f2} f1}"); }
	@Test public void test_8106() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{null f1} f2}"); }
	@Test public void test_8107() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{null f2} f2}"); }
	@Test public void test_8108() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_8109() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_8110() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_8111() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_8112() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_8113() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_8114() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_8115() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_8116() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_8117() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_8118() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_8119() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_8120() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_8121() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_8122() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_8123() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_8124() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_8125() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_8126() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_8127() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_8128() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<X|null> f1}"); }
	@Test public void test_8129() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<X|null> f2}"); }
	@Test public void test_8130() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_8131() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_8132() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_8133() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_8134() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_8135() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_8136() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_8137() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_8138() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_8139() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_8140() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","null|null"); }
	@Test public void test_8141() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","null|X<{X f1}>"); }
	@Test public void test_8142() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","null|X<{X f2}>"); }
	@Test public void test_8143() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","null|{null f1}"); }
	@Test public void test_8144() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","null|{null f2}"); }
	@Test public void test_8145() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","null|(X<null|X>)"); }
	@Test public void test_8146() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{null f1}|null"); }
	@Test public void test_8147() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{null f2}|null"); }
	@Test public void test_8148() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f1}>|null"); }
	@Test public void test_8149() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f2}>|null"); }
	@Test public void test_8150() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{null f1}>"); }
	@Test public void test_8151() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{null f2}>"); }
	@Test public void test_8152() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_8153() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_8154() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_8155() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_8156() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_8157() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_8158() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_8159() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_8160() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_8161() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_8162() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_8163() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_8164() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_8165() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_8166() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_8167() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_8168() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{{X f1} f1}>"); }
	@Test public void test_8169() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{{X f2} f1}>"); }
	@Test public void test_8170() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{{X f1} f2}>"); }
	@Test public void test_8171() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{{X f2} f2}>"); }
	@Test public void test_8172() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_8173() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_8174() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_8175() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_8176() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","(X<X|null>)|null"); }
	@Test public void test_8177() { checkNotSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|(Y<Y|null>)>"); }
	@Test public void test_8178() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_8179() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_8180() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_8181() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_8182() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_8183() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_8184() { checkIsSubtype("(X<X|Y<{Y f2}>>)|Y<{Y f2}>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_8185() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","null"); }
	@Test public void test_8186() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f1}>"); }
	@Test public void test_8187() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f2}>"); }
	@Test public void test_8188() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{null f1}"); }
	@Test public void test_8189() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{null f2}"); }
	@Test public void test_8190() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<{X f1}> f1}"); }
	@Test public void test_8191() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<{X f2}> f1}"); }
	@Test public void test_8192() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<{X f1}> f2}"); }
	@Test public void test_8193() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<{X f2}> f2}"); }
	@Test public void test_8194() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|null>"); }
	@Test public void test_8195() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|Y<{Y f1}>>"); }
	@Test public void test_8196() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|Y<{Y f2}>>"); }
	@Test public void test_8197() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{null f1} f1}"); }
	@Test public void test_8198() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{null f2} f1}"); }
	@Test public void test_8199() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{null f1} f2}"); }
	@Test public void test_8200() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{null f2} f2}"); }
	@Test public void test_8201() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_8202() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_8203() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_8204() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_8205() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_8206() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_8207() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_8208() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_8209() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_8210() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_8211() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_8212() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_8213() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_8214() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_8215() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_8216() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_8217() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_8218() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_8219() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_8220() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_8221() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<X|null> f1}"); }
	@Test public void test_8222() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<X|null> f2}"); }
	@Test public void test_8223() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_8224() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_8225() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_8226() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_8227() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_8228() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_8229() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_8230() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_8231() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_8232() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_8233() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","null|null"); }
	@Test public void test_8234() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","null|X<{X f1}>"); }
	@Test public void test_8235() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","null|X<{X f2}>"); }
	@Test public void test_8236() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","null|{null f1}"); }
	@Test public void test_8237() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","null|{null f2}"); }
	@Test public void test_8238() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","null|(X<null|X>)"); }
	@Test public void test_8239() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{null f1}|null"); }
	@Test public void test_8240() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{null f2}|null"); }
	@Test public void test_8241() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f1}>|null"); }
	@Test public void test_8242() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f2}>|null"); }
	@Test public void test_8243() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{null f1}>"); }
	@Test public void test_8244() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{null f2}>"); }
	@Test public void test_8245() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_8246() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_8247() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_8248() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_8249() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_8250() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_8251() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_8252() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_8253() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_8254() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_8255() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_8256() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_8257() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_8258() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_8259() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_8260() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_8261() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{{X f1} f1}>"); }
	@Test public void test_8262() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{{X f2} f1}>"); }
	@Test public void test_8263() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{{X f1} f2}>"); }
	@Test public void test_8264() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{{X f2} f2}>"); }
	@Test public void test_8265() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_8266() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_8267() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_8268() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_8269() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","(X<X|null>)|null"); }
	@Test public void test_8270() { checkNotSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|(Y<Y|null>)>"); }
	@Test public void test_8271() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_8272() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_8273() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_8274() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_8275() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_8276() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_8277() { checkIsSubtype("X<X|(Y<Y|Z<{Z f1}>>)>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_8278() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","null"); }
	@Test public void test_8279() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f1}>"); }
	@Test public void test_8280() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f2}>"); }
	@Test public void test_8281() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{null f1}"); }
	@Test public void test_8282() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{null f2}"); }
	@Test public void test_8283() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<{X f1}> f1}"); }
	@Test public void test_8284() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<{X f2}> f1}"); }
	@Test public void test_8285() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<{X f1}> f2}"); }
	@Test public void test_8286() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<{X f2}> f2}"); }
	@Test public void test_8287() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|null>"); }
	@Test public void test_8288() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|Y<{Y f1}>>"); }
	@Test public void test_8289() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|Y<{Y f2}>>"); }
	@Test public void test_8290() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{null f1} f1}"); }
	@Test public void test_8291() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{null f2} f1}"); }
	@Test public void test_8292() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{null f1} f2}"); }
	@Test public void test_8293() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{null f2} f2}"); }
	@Test public void test_8294() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_8295() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_8296() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_8297() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_8298() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_8299() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_8300() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_8301() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_8302() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_8303() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_8304() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_8305() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_8306() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_8307() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_8308() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_8309() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_8310() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_8311() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_8312() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_8313() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_8314() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<X|null> f1}"); }
	@Test public void test_8315() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<X|null> f2}"); }
	@Test public void test_8316() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_8317() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_8318() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_8319() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_8320() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_8321() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_8322() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_8323() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_8324() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_8325() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_8326() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","null|null"); }
	@Test public void test_8327() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","null|X<{X f1}>"); }
	@Test public void test_8328() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","null|X<{X f2}>"); }
	@Test public void test_8329() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","null|{null f1}"); }
	@Test public void test_8330() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","null|{null f2}"); }
	@Test public void test_8331() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","null|(X<null|X>)"); }
	@Test public void test_8332() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{null f1}|null"); }
	@Test public void test_8333() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{null f2}|null"); }
	@Test public void test_8334() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f1}>|null"); }
	@Test public void test_8335() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f2}>|null"); }
	@Test public void test_8336() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{null f1}>"); }
	@Test public void test_8337() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{null f2}>"); }
	@Test public void test_8338() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_8339() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_8340() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_8341() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_8342() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_8343() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_8344() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_8345() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_8346() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_8347() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_8348() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_8349() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_8350() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_8351() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_8352() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_8353() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_8354() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{{X f1} f1}>"); }
	@Test public void test_8355() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{{X f2} f1}>"); }
	@Test public void test_8356() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{{X f1} f2}>"); }
	@Test public void test_8357() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{{X f2} f2}>"); }
	@Test public void test_8358() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_8359() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_8360() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_8361() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_8362() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","(X<X|null>)|null"); }
	@Test public void test_8363() { checkNotSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|(Y<Y|null>)>"); }
	@Test public void test_8364() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_8365() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_8366() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_8367() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_8368() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_8369() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_8370() { checkIsSubtype("X<X|(Y<Y|Z<{Z f2}>>)>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_8371() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","null"); }
	@Test public void test_8372() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f1}>"); }
	@Test public void test_8373() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f2}>"); }
	@Test public void test_8374() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{null f1}"); }
	@Test public void test_8375() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{null f2}"); }
	@Test public void test_8376() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<{X f1}> f1}"); }
	@Test public void test_8377() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<{X f2}> f1}"); }
	@Test public void test_8378() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<{X f1}> f2}"); }
	@Test public void test_8379() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<{X f2}> f2}"); }
	@Test public void test_8380() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","X<X|null>"); }
	@Test public void test_8381() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|Y<{Y f1}>>"); }
	@Test public void test_8382() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|Y<{Y f2}>>"); }
	@Test public void test_8383() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{{null f1} f1}"); }
	@Test public void test_8384() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{{null f2} f1}"); }
	@Test public void test_8385() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{{null f1} f2}"); }
	@Test public void test_8386() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{{null f2} f2}"); }
	@Test public void test_8387() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_8388() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_8389() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_8390() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_8391() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_8392() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_8393() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_8394() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_8395() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_8396() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_8397() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_8398() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_8399() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_8400() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_8401() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_8402() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_8403() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_8404() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_8405() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_8406() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_8407() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{X<X|null> f1}"); }
	@Test public void test_8408() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{X<X|null> f2}"); }
	@Test public void test_8409() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_8410() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_8411() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_8412() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_8413() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_8414() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_8415() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_8416() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_8417() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_8418() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_8419() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","null|null"); }
	@Test public void test_8420() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","null|X<{X f1}>"); }
	@Test public void test_8421() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","null|X<{X f2}>"); }
	@Test public void test_8422() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","null|{null f1}"); }
	@Test public void test_8423() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","null|{null f2}"); }
	@Test public void test_8424() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","null|(X<null|X>)"); }
	@Test public void test_8425() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{null f1}|null"); }
	@Test public void test_8426() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","{null f2}|null"); }
	@Test public void test_8427() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f1}>|null"); }
	@Test public void test_8428() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f2}>|null"); }
	@Test public void test_8429() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{null f1}>"); }
	@Test public void test_8430() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{null f2}>"); }
	@Test public void test_8431() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_8432() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_8433() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_8434() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_8435() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_8436() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_8437() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_8438() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_8439() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_8440() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_8441() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_8442() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_8443() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_8444() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_8445() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_8446() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_8447() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{{X f1} f1}>"); }
	@Test public void test_8448() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{{X f2} f1}>"); }
	@Test public void test_8449() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{{X f1} f2}>"); }
	@Test public void test_8450() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{{X f2} f2}>"); }
	@Test public void test_8451() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_8452() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_8453() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_8454() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_8455() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","(X<X|null>)|null"); }
	@Test public void test_8456() { checkNotSubtype("X<X|(Y<Y|{X f1}>)>","X<X|(Y<Y|null>)>"); }
	@Test public void test_8457() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_8458() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_8459() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_8460() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_8461() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_8462() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_8463() { checkIsSubtype("X<X|(Y<Y|{X f1}>)>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_8464() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","null"); }
	@Test public void test_8465() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f1}>"); }
	@Test public void test_8466() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f2}>"); }
	@Test public void test_8467() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{null f1}"); }
	@Test public void test_8468() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{null f2}"); }
	@Test public void test_8469() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<{X f1}> f1}"); }
	@Test public void test_8470() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<{X f2}> f1}"); }
	@Test public void test_8471() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<{X f1}> f2}"); }
	@Test public void test_8472() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<{X f2}> f2}"); }
	@Test public void test_8473() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","X<X|null>"); }
	@Test public void test_8474() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|Y<{Y f1}>>"); }
	@Test public void test_8475() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|Y<{Y f2}>>"); }
	@Test public void test_8476() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{{null f1} f1}"); }
	@Test public void test_8477() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{{null f2} f1}"); }
	@Test public void test_8478() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{{null f1} f2}"); }
	@Test public void test_8479() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{{null f2} f2}"); }
	@Test public void test_8480() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_8481() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_8482() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_8483() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_8484() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_8485() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_8486() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_8487() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_8488() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_8489() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_8490() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_8491() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_8492() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_8493() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_8494() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_8495() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_8496() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_8497() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_8498() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_8499() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_8500() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{X<X|null> f1}"); }
	@Test public void test_8501() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{X<X|null> f2}"); }
	@Test public void test_8502() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_8503() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_8504() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_8505() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_8506() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_8507() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_8508() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_8509() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_8510() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_8511() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_8512() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","null|null"); }
	@Test public void test_8513() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","null|X<{X f1}>"); }
	@Test public void test_8514() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","null|X<{X f2}>"); }
	@Test public void test_8515() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","null|{null f1}"); }
	@Test public void test_8516() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","null|{null f2}"); }
	@Test public void test_8517() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","null|(X<null|X>)"); }
	@Test public void test_8518() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{null f1}|null"); }
	@Test public void test_8519() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","{null f2}|null"); }
	@Test public void test_8520() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f1}>|null"); }
	@Test public void test_8521() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f2}>|null"); }
	@Test public void test_8522() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{null f1}>"); }
	@Test public void test_8523() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{null f2}>"); }
	@Test public void test_8524() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_8525() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_8526() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_8527() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_8528() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_8529() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_8530() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_8531() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_8532() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_8533() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_8534() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_8535() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_8536() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_8537() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_8538() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_8539() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_8540() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{{X f1} f1}>"); }
	@Test public void test_8541() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{{X f2} f1}>"); }
	@Test public void test_8542() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{{X f1} f2}>"); }
	@Test public void test_8543() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{{X f2} f2}>"); }
	@Test public void test_8544() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_8545() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_8546() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_8547() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_8548() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","(X<X|null>)|null"); }
	@Test public void test_8549() { checkNotSubtype("X<X|(Y<Y|{X f2}>)>","X<X|(Y<Y|null>)>"); }
	@Test public void test_8550() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_8551() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_8552() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_8553() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_8554() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_8555() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_8556() { checkIsSubtype("X<X|(Y<Y|{X f2}>)>","X<X|(Y<Y|(Z<X|Z>)>)>"); }
	@Test public void test_8557() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","null"); }
	@Test public void test_8558() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f1}>"); }
	@Test public void test_8559() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f2}>"); }
	@Test public void test_8560() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{null f1}"); }
	@Test public void test_8561() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{null f2}"); }
	@Test public void test_8562() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<{X f1}> f1}"); }
	@Test public void test_8563() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<{X f2}> f1}"); }
	@Test public void test_8564() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<{X f1}> f2}"); }
	@Test public void test_8565() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<{X f2}> f2}"); }
	@Test public void test_8566() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|null>"); }
	@Test public void test_8567() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|Y<{Y f1}>>"); }
	@Test public void test_8568() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|Y<{Y f2}>>"); }
	@Test public void test_8569() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{null f1} f1}"); }
	@Test public void test_8570() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{null f2} f1}"); }
	@Test public void test_8571() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{null f1} f2}"); }
	@Test public void test_8572() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{null f2} f2}"); }
	@Test public void test_8573() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{X<{X f1}> f1} f1}"); }
	@Test public void test_8574() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{X<{X f2}> f1} f1}"); }
	@Test public void test_8575() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{X<{X f1}> f2} f1}"); }
	@Test public void test_8576() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{X<{X f2}> f2} f1}"); }
	@Test public void test_8577() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{X<{X f1}> f1} f2}"); }
	@Test public void test_8578() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{X<{X f2}> f1} f2}"); }
	@Test public void test_8579() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{X<{X f1}> f2} f2}"); }
	@Test public void test_8580() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{{X<{X f2}> f2} f2}"); }
	@Test public void test_8581() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{{X f1} f1} f1}>"); }
	@Test public void test_8582() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{{X f2} f1} f1}>"); }
	@Test public void test_8583() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{{X f1} f2} f1}>"); }
	@Test public void test_8584() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{{X f2} f2} f1}>"); }
	@Test public void test_8585() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{{X f1} f1} f2}>"); }
	@Test public void test_8586() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{{X f2} f1} f2}>"); }
	@Test public void test_8587() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{{X f1} f2} f2}>"); }
	@Test public void test_8588() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{{X f2} f2} f2}>"); }
	@Test public void test_8589() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{Y<X|Y> f1} f1}>"); }
	@Test public void test_8590() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{Y<X|Y> f2} f1}>"); }
	@Test public void test_8591() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{Y<X|Y> f1} f2}>"); }
	@Test public void test_8592() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{{Y<X|Y> f2} f2}>"); }
	@Test public void test_8593() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<X|null> f1}"); }
	@Test public void test_8594() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<X|null> f2}"); }
	@Test public void test_8595() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<X|Y<{Y f1}>> f1}"); }
	@Test public void test_8596() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<X|Y<{Y f2}>> f1}"); }
	@Test public void test_8597() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<X|Y<{Y f1}>> f2}"); }
	@Test public void test_8598() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<X|Y<{Y f2}>> f2}"); }
	@Test public void test_8599() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{Y<Y|{X f1}> f1}>"); }
	@Test public void test_8600() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{Y<Y|{X f2}> f1}>"); }
	@Test public void test_8601() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{Y<Y|{X f1}> f2}>"); }
	@Test public void test_8602() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{Y<Y|{X f2}> f2}>"); }
	@Test public void test_8603() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{Y<Y|(Z<X|Z>)> f1}>"); }
	@Test public void test_8604() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{Y<Y|(Z<X|Z>)> f2}>"); }
	@Test public void test_8605() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","null|null"); }
	@Test public void test_8606() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","null|X<{X f1}>"); }
	@Test public void test_8607() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","null|X<{X f2}>"); }
	@Test public void test_8608() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","null|{null f1}"); }
	@Test public void test_8609() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","null|{null f2}"); }
	@Test public void test_8610() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","null|(X<null|X>)"); }
	@Test public void test_8611() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{null f1}|null"); }
	@Test public void test_8612() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{null f2}|null"); }
	@Test public void test_8613() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f1}>|null"); }
	@Test public void test_8614() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f2}>|null"); }
	@Test public void test_8615() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{null f1}>"); }
	@Test public void test_8616() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{null f2}>"); }
	@Test public void test_8617() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<{X f1}> f1}|X<{X f1}>"); }
	@Test public void test_8618() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<{X f2}> f1}|X<{X f2}>"); }
	@Test public void test_8619() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<{X f1}> f2}|X<{X f1}>"); }
	@Test public void test_8620() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","{X<{X f2}> f2}|X<{X f2}>"); }
	@Test public void test_8621() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f1}>|Y<{Y f1}>"); }
	@Test public void test_8622() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f1}>|Y<{Y f2}>"); }
	@Test public void test_8623() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f2}>|Y<{Y f1}>"); }
	@Test public void test_8624() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f2}>|Y<{Y f2}>"); }
	@Test public void test_8625() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f1}>|{X<{X f1}> f1}"); }
	@Test public void test_8626() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f1}>|{X<{X f1}> f2}"); }
	@Test public void test_8627() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f2}>|{X<{X f2}> f1}"); }
	@Test public void test_8628() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f2}>|{X<{X f2}> f2}"); }
	@Test public void test_8629() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{Y<{Y f1}> f1}>"); }
	@Test public void test_8630() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{Y<{Y f2}> f1}>"); }
	@Test public void test_8631() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{Y<{Y f1}> f2}>"); }
	@Test public void test_8632() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{Y<{Y f2}> f2}>"); }
	@Test public void test_8633() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{{X f1} f1}>"); }
	@Test public void test_8634() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{{X f2} f1}>"); }
	@Test public void test_8635() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{{X f1} f2}>"); }
	@Test public void test_8636() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{{X f2} f2}>"); }
	@Test public void test_8637() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f1}>|(Y<X<{X f1}>|Y>)"); }
	@Test public void test_8638() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<{X f2}>|(Y<X<{X f2}>|Y>)"); }
	@Test public void test_8639() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{Y<X|Y> f1}>"); }
	@Test public void test_8640() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|{Y<X|Y> f2}>"); }
	@Test public void test_8641() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","(X<X|null>)|null"); }
	@Test public void test_8642() { checkNotSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|(Y<Y|null>)>"); }
	@Test public void test_8643() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","(X<X|Y<{Y f1}>>)|Y<{Y f1}>"); }
	@Test public void test_8644() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","(X<X|Y<{Y f2}>>)|Y<{Y f2}>"); }
	@Test public void test_8645() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|(Y<Y|Z<{Z f1}>>)>"); }
	@Test public void test_8646() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|(Y<Y|Z<{Z f2}>>)>"); }
	@Test public void test_8647() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|(Y<Y|{X f1}>)>"); }
	@Test public void test_8648() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|(Y<Y|{X f2}>)>"); }
	@Test public void test_8649() { checkIsSubtype("X<X|(Y<Y|(Z<X|Z>)>)>","X<X|(Y<Y|(Z<X|Z>)>)>"); }

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
