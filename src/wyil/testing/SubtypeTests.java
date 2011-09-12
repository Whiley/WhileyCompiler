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
	@Test public void test_16() { checkNotSubtype("null","any"); }
	@Test public void test_17() { checkIsSubtype("null","null"); }
	@Test public void test_18() { checkNotSubtype("null","int"); }
	@Test public void test_19() { checkNotSubtype("null","[void]"); }
	@Test public void test_20() { checkNotSubtype("null","[any]"); }
	@Test public void test_21() { checkNotSubtype("null","[null]"); }
	@Test public void test_22() { checkNotSubtype("null","[int]"); }
	@Test public void test_23() { checkIsSubtype("null","{void f1}"); }
	@Test public void test_24() { checkIsSubtype("null","{void f2}"); }
	@Test public void test_25() { checkNotSubtype("null","{any f1}"); }
	@Test public void test_26() { checkNotSubtype("null","{any f2}"); }
	@Test public void test_27() { checkNotSubtype("null","{null f1}"); }
	@Test public void test_28() { checkNotSubtype("null","{null f2}"); }
	@Test public void test_29() { checkNotSubtype("null","{int f1}"); }
	@Test public void test_30() { checkNotSubtype("null","{int f2}"); }
	@Test public void test_31() { checkNotSubtype("int","any"); }
	@Test public void test_32() { checkNotSubtype("int","null"); }
	@Test public void test_33() { checkIsSubtype("int","int"); }
	@Test public void test_34() { checkNotSubtype("int","[void]"); }
	@Test public void test_35() { checkNotSubtype("int","[any]"); }
	@Test public void test_36() { checkNotSubtype("int","[null]"); }
	@Test public void test_37() { checkNotSubtype("int","[int]"); }
	@Test public void test_38() { checkIsSubtype("int","{void f1}"); }
	@Test public void test_39() { checkIsSubtype("int","{void f2}"); }
	@Test public void test_40() { checkNotSubtype("int","{any f1}"); }
	@Test public void test_41() { checkNotSubtype("int","{any f2}"); }
	@Test public void test_42() { checkNotSubtype("int","{null f1}"); }
	@Test public void test_43() { checkNotSubtype("int","{null f2}"); }
	@Test public void test_44() { checkNotSubtype("int","{int f1}"); }
	@Test public void test_45() { checkNotSubtype("int","{int f2}"); }
	@Test public void test_46() { checkNotSubtype("[void]","any"); }
	@Test public void test_47() { checkNotSubtype("[void]","null"); }
	@Test public void test_48() { checkNotSubtype("[void]","int"); }
	@Test public void test_49() { checkIsSubtype("[void]","[void]"); }
	@Test public void test_50() { checkNotSubtype("[void]","[any]"); }
	@Test public void test_51() { checkNotSubtype("[void]","[null]"); }
	@Test public void test_52() { checkNotSubtype("[void]","[int]"); }
	@Test public void test_53() { checkIsSubtype("[void]","{void f1}"); }
	@Test public void test_54() { checkIsSubtype("[void]","{void f2}"); }
	@Test public void test_55() { checkNotSubtype("[void]","{any f1}"); }
	@Test public void test_56() { checkNotSubtype("[void]","{any f2}"); }
	@Test public void test_57() { checkNotSubtype("[void]","{null f1}"); }
	@Test public void test_58() { checkNotSubtype("[void]","{null f2}"); }
	@Test public void test_59() { checkNotSubtype("[void]","{int f1}"); }
	@Test public void test_60() { checkNotSubtype("[void]","{int f2}"); }
	@Test public void test_61() { checkNotSubtype("[any]","any"); }
	@Test public void test_62() { checkNotSubtype("[any]","null"); }
	@Test public void test_63() { checkNotSubtype("[any]","int"); }
	@Test public void test_64() { checkIsSubtype("[any]","[void]"); }
	@Test public void test_65() { checkIsSubtype("[any]","[any]"); }
	@Test public void test_66() { checkIsSubtype("[any]","[null]"); }
	@Test public void test_67() { checkIsSubtype("[any]","[int]"); }
	@Test public void test_68() { checkIsSubtype("[any]","{void f1}"); }
	@Test public void test_69() { checkIsSubtype("[any]","{void f2}"); }
	@Test public void test_70() { checkNotSubtype("[any]","{any f1}"); }
	@Test public void test_71() { checkNotSubtype("[any]","{any f2}"); }
	@Test public void test_72() { checkNotSubtype("[any]","{null f1}"); }
	@Test public void test_73() { checkNotSubtype("[any]","{null f2}"); }
	@Test public void test_74() { checkNotSubtype("[any]","{int f1}"); }
	@Test public void test_75() { checkNotSubtype("[any]","{int f2}"); }
	@Test public void test_76() { checkNotSubtype("[null]","any"); }
	@Test public void test_77() { checkNotSubtype("[null]","null"); }
	@Test public void test_78() { checkNotSubtype("[null]","int"); }
	@Test public void test_79() { checkIsSubtype("[null]","[void]"); }
	@Test public void test_80() { checkNotSubtype("[null]","[any]"); }
	@Test public void test_81() { checkIsSubtype("[null]","[null]"); }
	@Test public void test_82() { checkNotSubtype("[null]","[int]"); }
	@Test public void test_83() { checkIsSubtype("[null]","{void f1}"); }
	@Test public void test_84() { checkIsSubtype("[null]","{void f2}"); }
	@Test public void test_85() { checkNotSubtype("[null]","{any f1}"); }
	@Test public void test_86() { checkNotSubtype("[null]","{any f2}"); }
	@Test public void test_87() { checkNotSubtype("[null]","{null f1}"); }
	@Test public void test_88() { checkNotSubtype("[null]","{null f2}"); }
	@Test public void test_89() { checkNotSubtype("[null]","{int f1}"); }
	@Test public void test_90() { checkNotSubtype("[null]","{int f2}"); }
	@Test public void test_91() { checkNotSubtype("[int]","any"); }
	@Test public void test_92() { checkNotSubtype("[int]","null"); }
	@Test public void test_93() { checkNotSubtype("[int]","int"); }
	@Test public void test_94() { checkIsSubtype("[int]","[void]"); }
	@Test public void test_95() { checkNotSubtype("[int]","[any]"); }
	@Test public void test_96() { checkNotSubtype("[int]","[null]"); }
	@Test public void test_97() { checkIsSubtype("[int]","[int]"); }
	@Test public void test_98() { checkIsSubtype("[int]","{void f1}"); }
	@Test public void test_99() { checkIsSubtype("[int]","{void f2}"); }
	@Test public void test_100() { checkNotSubtype("[int]","{any f1}"); }
	@Test public void test_101() { checkNotSubtype("[int]","{any f2}"); }
	@Test public void test_102() { checkNotSubtype("[int]","{null f1}"); }
	@Test public void test_103() { checkNotSubtype("[int]","{null f2}"); }
	@Test public void test_104() { checkNotSubtype("[int]","{int f1}"); }
	@Test public void test_105() { checkNotSubtype("[int]","{int f2}"); }
	@Test public void test_106() { checkNotSubtype("{void f1}","any"); }
	@Test public void test_107() { checkNotSubtype("{void f1}","null"); }
	@Test public void test_108() { checkNotSubtype("{void f1}","int"); }
	@Test public void test_109() { checkNotSubtype("{void f1}","[void]"); }
	@Test public void test_110() { checkNotSubtype("{void f1}","[any]"); }
	@Test public void test_111() { checkNotSubtype("{void f1}","[null]"); }
	@Test public void test_112() { checkNotSubtype("{void f1}","[int]"); }
	@Test public void test_113() { checkIsSubtype("{void f1}","{void f1}"); }
	@Test public void test_114() { checkIsSubtype("{void f1}","{void f2}"); }
	@Test public void test_115() { checkNotSubtype("{void f1}","{any f1}"); }
	@Test public void test_116() { checkNotSubtype("{void f1}","{any f2}"); }
	@Test public void test_117() { checkNotSubtype("{void f1}","{null f1}"); }
	@Test public void test_118() { checkNotSubtype("{void f1}","{null f2}"); }
	@Test public void test_119() { checkNotSubtype("{void f1}","{int f1}"); }
	@Test public void test_120() { checkNotSubtype("{void f1}","{int f2}"); }
	@Test public void test_121() { checkNotSubtype("{void f2}","any"); }
	@Test public void test_122() { checkNotSubtype("{void f2}","null"); }
	@Test public void test_123() { checkNotSubtype("{void f2}","int"); }
	@Test public void test_124() { checkNotSubtype("{void f2}","[void]"); }
	@Test public void test_125() { checkNotSubtype("{void f2}","[any]"); }
	@Test public void test_126() { checkNotSubtype("{void f2}","[null]"); }
	@Test public void test_127() { checkNotSubtype("{void f2}","[int]"); }
	@Test public void test_128() { checkIsSubtype("{void f2}","{void f1}"); }
	@Test public void test_129() { checkIsSubtype("{void f2}","{void f2}"); }
	@Test public void test_130() { checkNotSubtype("{void f2}","{any f1}"); }
	@Test public void test_131() { checkNotSubtype("{void f2}","{any f2}"); }
	@Test public void test_132() { checkNotSubtype("{void f2}","{null f1}"); }
	@Test public void test_133() { checkNotSubtype("{void f2}","{null f2}"); }
	@Test public void test_134() { checkNotSubtype("{void f2}","{int f1}"); }
	@Test public void test_135() { checkNotSubtype("{void f2}","{int f2}"); }
	@Test public void test_136() { checkNotSubtype("{any f1}","any"); }
	@Test public void test_137() { checkNotSubtype("{any f1}","null"); }
	@Test public void test_138() { checkNotSubtype("{any f1}","int"); }
	@Test public void test_139() { checkNotSubtype("{any f1}","[void]"); }
	@Test public void test_140() { checkNotSubtype("{any f1}","[any]"); }
	@Test public void test_141() { checkNotSubtype("{any f1}","[null]"); }
	@Test public void test_142() { checkNotSubtype("{any f1}","[int]"); }
	@Test public void test_143() { checkIsSubtype("{any f1}","{void f1}"); }
	@Test public void test_144() { checkIsSubtype("{any f1}","{void f2}"); }
	@Test public void test_145() { checkIsSubtype("{any f1}","{any f1}"); }
	@Test public void test_146() { checkNotSubtype("{any f1}","{any f2}"); }
	@Test public void test_147() { checkIsSubtype("{any f1}","{null f1}"); }
	@Test public void test_148() { checkNotSubtype("{any f1}","{null f2}"); }
	@Test public void test_149() { checkIsSubtype("{any f1}","{int f1}"); }
	@Test public void test_150() { checkNotSubtype("{any f1}","{int f2}"); }
	@Test public void test_151() { checkNotSubtype("{any f2}","any"); }
	@Test public void test_152() { checkNotSubtype("{any f2}","null"); }
	@Test public void test_153() { checkNotSubtype("{any f2}","int"); }
	@Test public void test_154() { checkNotSubtype("{any f2}","[void]"); }
	@Test public void test_155() { checkNotSubtype("{any f2}","[any]"); }
	@Test public void test_156() { checkNotSubtype("{any f2}","[null]"); }
	@Test public void test_157() { checkNotSubtype("{any f2}","[int]"); }
	@Test public void test_158() { checkIsSubtype("{any f2}","{void f1}"); }
	@Test public void test_159() { checkIsSubtype("{any f2}","{void f2}"); }
	@Test public void test_160() { checkNotSubtype("{any f2}","{any f1}"); }
	@Test public void test_161() { checkIsSubtype("{any f2}","{any f2}"); }
	@Test public void test_162() { checkNotSubtype("{any f2}","{null f1}"); }
	@Test public void test_163() { checkIsSubtype("{any f2}","{null f2}"); }
	@Test public void test_164() { checkNotSubtype("{any f2}","{int f1}"); }
	@Test public void test_165() { checkIsSubtype("{any f2}","{int f2}"); }
	@Test public void test_166() { checkNotSubtype("{null f1}","any"); }
	@Test public void test_167() { checkNotSubtype("{null f1}","null"); }
	@Test public void test_168() { checkNotSubtype("{null f1}","int"); }
	@Test public void test_169() { checkNotSubtype("{null f1}","[void]"); }
	@Test public void test_170() { checkNotSubtype("{null f1}","[any]"); }
	@Test public void test_171() { checkNotSubtype("{null f1}","[null]"); }
	@Test public void test_172() { checkNotSubtype("{null f1}","[int]"); }
	@Test public void test_173() { checkIsSubtype("{null f1}","{void f1}"); }
	@Test public void test_174() { checkIsSubtype("{null f1}","{void f2}"); }
	@Test public void test_175() { checkNotSubtype("{null f1}","{any f1}"); }
	@Test public void test_176() { checkNotSubtype("{null f1}","{any f2}"); }
	@Test public void test_177() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test public void test_178() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test public void test_179() { checkNotSubtype("{null f1}","{int f1}"); }
	@Test public void test_180() { checkNotSubtype("{null f1}","{int f2}"); }
	@Test public void test_181() { checkNotSubtype("{null f2}","any"); }
	@Test public void test_182() { checkNotSubtype("{null f2}","null"); }
	@Test public void test_183() { checkNotSubtype("{null f2}","int"); }
	@Test public void test_184() { checkNotSubtype("{null f2}","[void]"); }
	@Test public void test_185() { checkNotSubtype("{null f2}","[any]"); }
	@Test public void test_186() { checkNotSubtype("{null f2}","[null]"); }
	@Test public void test_187() { checkNotSubtype("{null f2}","[int]"); }
	@Test public void test_188() { checkIsSubtype("{null f2}","{void f1}"); }
	@Test public void test_189() { checkIsSubtype("{null f2}","{void f2}"); }
	@Test public void test_190() { checkNotSubtype("{null f2}","{any f1}"); }
	@Test public void test_191() { checkNotSubtype("{null f2}","{any f2}"); }
	@Test public void test_192() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test public void test_193() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test public void test_194() { checkNotSubtype("{null f2}","{int f1}"); }
	@Test public void test_195() { checkNotSubtype("{null f2}","{int f2}"); }
	@Test public void test_196() { checkNotSubtype("{int f1}","any"); }
	@Test public void test_197() { checkNotSubtype("{int f1}","null"); }
	@Test public void test_198() { checkNotSubtype("{int f1}","int"); }
	@Test public void test_199() { checkNotSubtype("{int f1}","[void]"); }
	@Test public void test_200() { checkNotSubtype("{int f1}","[any]"); }
	@Test public void test_201() { checkNotSubtype("{int f1}","[null]"); }
	@Test public void test_202() { checkNotSubtype("{int f1}","[int]"); }
	@Test public void test_203() { checkIsSubtype("{int f1}","{void f1}"); }
	@Test public void test_204() { checkIsSubtype("{int f1}","{void f2}"); }
	@Test public void test_205() { checkNotSubtype("{int f1}","{any f1}"); }
	@Test public void test_206() { checkNotSubtype("{int f1}","{any f2}"); }
	@Test public void test_207() { checkNotSubtype("{int f1}","{null f1}"); }
	@Test public void test_208() { checkNotSubtype("{int f1}","{null f2}"); }
	@Test public void test_209() { checkIsSubtype("{int f1}","{int f1}"); }
	@Test public void test_210() { checkNotSubtype("{int f1}","{int f2}"); }
	@Test public void test_211() { checkNotSubtype("{int f2}","any"); }
	@Test public void test_212() { checkNotSubtype("{int f2}","null"); }
	@Test public void test_213() { checkNotSubtype("{int f2}","int"); }
	@Test public void test_214() { checkNotSubtype("{int f2}","[void]"); }
	@Test public void test_215() { checkNotSubtype("{int f2}","[any]"); }
	@Test public void test_216() { checkNotSubtype("{int f2}","[null]"); }
	@Test public void test_217() { checkNotSubtype("{int f2}","[int]"); }
	@Test public void test_218() { checkIsSubtype("{int f2}","{void f1}"); }
	@Test public void test_219() { checkIsSubtype("{int f2}","{void f2}"); }
	@Test public void test_220() { checkNotSubtype("{int f2}","{any f1}"); }
	@Test public void test_221() { checkNotSubtype("{int f2}","{any f2}"); }
	@Test public void test_222() { checkNotSubtype("{int f2}","{null f1}"); }
	@Test public void test_223() { checkNotSubtype("{int f2}","{null f2}"); }
	@Test public void test_224() { checkNotSubtype("{int f2}","{int f1}"); }
	@Test public void test_225() { checkIsSubtype("{int f2}","{int f2}"); }

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
