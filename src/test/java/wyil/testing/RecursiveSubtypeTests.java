// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// This file was automatically generated.
package wyil.testing;
import org.junit.*;

import wybs.lang.NameResolver;
import wyc.lang.WhileyFile.Type;
import wyc.util.TestUtils;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.subtyping.RelaxedTypeEmptinessTest;
import wyil.type.subtyping.StrictTypeEmptinessTest;

import static org.junit.Assert.*;
import static wyc.lang.WhileyFile.Type;

// FIXME: this needs to be brough back online sometime.  #785

public class RecursiveSubtypeTests {
	@Test @Ignore public void test_1() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2() { checkNotSubtype("null","{null f1}"); }
	@Test @Ignore public void test_3() { checkNotSubtype("null","{null f2}"); }
	@Test @Ignore public void test_4() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_5() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_6() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_7() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_8() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_9() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_10() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_11() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_12() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_13() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_14() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_15() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_16() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_17() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_18() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_19() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_20() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_21() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_22() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test @Ignore public void test_23() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test @Ignore public void test_24() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test @Ignore public void test_25() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test @Ignore public void test_26() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test @Ignore public void test_27() { checkNotSubtype("null","X<{null|X f1}>"); }
	@Test @Ignore public void test_28() { checkNotSubtype("null","X<{null|X f2}>"); }
	@Test @Ignore public void test_29() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test @Ignore public void test_30() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_31() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_32() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_33() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_34() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_35() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_36() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_37() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_38() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_39() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_40() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_41() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_42() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_43() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_44() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_45() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_46() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_47() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_48() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_49() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_50() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_51() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_52() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_53() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_54() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_55() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_56() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_57() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_58() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_59() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_60() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_61() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_62() { checkNotSubtype("{null f1}","null"); }
	@Test @Ignore public void test_63() { checkIsSubtype("{null f1}","{null f1}"); }
	@Test @Ignore public void test_64() { checkNotSubtype("{null f1}","{null f2}"); }
	@Test @Ignore public void test_65() { checkNotSubtype("{null f1}","(null,null)"); }
	@Test @Ignore public void test_66() { checkNotSubtype("{null f1}","(null,null)"); }
	@Test @Ignore public void test_67() { checkNotSubtype("{null f1}","(null,{null f1})"); }
	@Test @Ignore public void test_68() { checkNotSubtype("{null f1}","(null,{null f2})"); }
	@Test @Ignore public void test_69() { checkNotSubtype("{null f1}","({null f1},null)"); }
	@Test @Ignore public void test_70() { checkNotSubtype("{null f1}","({null f2},null)"); }
	@Test @Ignore public void test_71() { checkNotSubtype("{null f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_72() { checkNotSubtype("{null f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_73() { checkNotSubtype("{null f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_74() { checkNotSubtype("{null f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_75() { checkNotSubtype("{null f1}","({null f1},null)"); }
	@Test @Ignore public void test_76() { checkNotSubtype("{null f1}","({null f2},null)"); }
	@Test @Ignore public void test_77() { checkNotSubtype("{null f1}","(null,{null f1})"); }
	@Test @Ignore public void test_78() { checkNotSubtype("{null f1}","(null,{null f2})"); }
	@Test @Ignore public void test_79() { checkNotSubtype("{null f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_80() { checkNotSubtype("{null f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_81() { checkNotSubtype("{null f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_82() { checkNotSubtype("{null f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_83() { checkNotSubtype("{null f1}","{{null f1} f1}"); }
	@Test @Ignore public void test_84() { checkNotSubtype("{null f1}","{{null f2} f1}"); }
	@Test @Ignore public void test_85() { checkNotSubtype("{null f1}","{{null f1} f2}"); }
	@Test @Ignore public void test_86() { checkNotSubtype("{null f1}","{{null f2} f2}"); }
	@Test @Ignore public void test_87() { checkNotSubtype("{null f1}","X<{X|null f1}>"); }
	@Test @Ignore public void test_88() { checkNotSubtype("{null f1}","X<{null|X f1}>"); }
	@Test @Ignore public void test_89() { checkNotSubtype("{null f1}","X<{null|X f2}>"); }
	@Test @Ignore public void test_90() { checkNotSubtype("{null f1}","X<{X|null f2}>"); }
	@Test @Ignore public void test_91() { checkNotSubtype("{null f1}","null"); }
	@Test @Ignore public void test_92() { checkNotSubtype("{null f1}","null"); }
	@Test @Ignore public void test_93() { checkNotSubtype("{null f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_94() { checkNotSubtype("{null f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_95() { checkNotSubtype("{null f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_96() { checkNotSubtype("{null f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_97() { checkNotSubtype("{null f1}","null|{null f1}"); }
	@Test @Ignore public void test_98() { checkNotSubtype("{null f1}","null|{null f2}"); }
	@Test @Ignore public void test_99() { checkNotSubtype("{null f1}","{null f1}|null"); }
	@Test @Ignore public void test_100() { checkNotSubtype("{null f1}","{null f2}|null"); }
	@Test @Ignore public void test_101() { checkNotSubtype("{null f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_102() { checkNotSubtype("{null f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_103() { checkNotSubtype("{null f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_104() { checkNotSubtype("{null f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_105() { checkNotSubtype("{null f1}","null"); }
	@Test @Ignore public void test_106() { checkNotSubtype("{null f1}","null"); }
	@Test @Ignore public void test_107() { checkNotSubtype("{null f1}","null"); }
	@Test @Ignore public void test_108() { checkNotSubtype("{null f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_109() { checkNotSubtype("{null f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_110() { checkNotSubtype("{null f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_111() { checkNotSubtype("{null f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_112() { checkNotSubtype("{null f1}","{null f1}|null"); }
	@Test @Ignore public void test_113() { checkNotSubtype("{null f1}","{null f2}|null"); }
	@Test @Ignore public void test_114() { checkNotSubtype("{null f1}","null|{null f1}"); }
	@Test @Ignore public void test_115() { checkNotSubtype("{null f1}","null|{null f2}"); }
	@Test @Ignore public void test_116() { checkNotSubtype("{null f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_117() { checkNotSubtype("{null f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_118() { checkNotSubtype("{null f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_119() { checkNotSubtype("{null f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_120() { checkNotSubtype("{null f1}","null"); }
	@Test @Ignore public void test_121() { checkNotSubtype("{null f1}","null"); }
	@Test @Ignore public void test_122() { checkNotSubtype("{null f1}","null"); }
	@Test @Ignore public void test_123() { checkNotSubtype("{null f2}","null"); }
	@Test @Ignore public void test_124() { checkNotSubtype("{null f2}","{null f1}"); }
	@Test @Ignore public void test_125() { checkIsSubtype("{null f2}","{null f2}"); }
	@Test @Ignore public void test_126() { checkNotSubtype("{null f2}","(null,null)"); }
	@Test @Ignore public void test_127() { checkNotSubtype("{null f2}","(null,null)"); }
	@Test @Ignore public void test_128() { checkNotSubtype("{null f2}","(null,{null f1})"); }
	@Test @Ignore public void test_129() { checkNotSubtype("{null f2}","(null,{null f2})"); }
	@Test @Ignore public void test_130() { checkNotSubtype("{null f2}","({null f1},null)"); }
	@Test @Ignore public void test_131() { checkNotSubtype("{null f2}","({null f2},null)"); }
	@Test @Ignore public void test_132() { checkNotSubtype("{null f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_133() { checkNotSubtype("{null f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_134() { checkNotSubtype("{null f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_135() { checkNotSubtype("{null f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_136() { checkNotSubtype("{null f2}","({null f1},null)"); }
	@Test @Ignore public void test_137() { checkNotSubtype("{null f2}","({null f2},null)"); }
	@Test @Ignore public void test_138() { checkNotSubtype("{null f2}","(null,{null f1})"); }
	@Test @Ignore public void test_139() { checkNotSubtype("{null f2}","(null,{null f2})"); }
	@Test @Ignore public void test_140() { checkNotSubtype("{null f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_141() { checkNotSubtype("{null f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_142() { checkNotSubtype("{null f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_143() { checkNotSubtype("{null f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_144() { checkNotSubtype("{null f2}","{{null f1} f1}"); }
	@Test @Ignore public void test_145() { checkNotSubtype("{null f2}","{{null f2} f1}"); }
	@Test @Ignore public void test_146() { checkNotSubtype("{null f2}","{{null f1} f2}"); }
	@Test @Ignore public void test_147() { checkNotSubtype("{null f2}","{{null f2} f2}"); }
	@Test @Ignore public void test_148() { checkNotSubtype("{null f2}","X<{X|null f1}>"); }
	@Test @Ignore public void test_149() { checkNotSubtype("{null f2}","X<{null|X f1}>"); }
	@Test @Ignore public void test_150() { checkNotSubtype("{null f2}","X<{null|X f2}>"); }
	@Test @Ignore public void test_151() { checkNotSubtype("{null f2}","X<{X|null f2}>"); }
	@Test @Ignore public void test_152() { checkNotSubtype("{null f2}","null"); }
	@Test @Ignore public void test_153() { checkNotSubtype("{null f2}","null"); }
	@Test @Ignore public void test_154() { checkNotSubtype("{null f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_155() { checkNotSubtype("{null f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_156() { checkNotSubtype("{null f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_157() { checkNotSubtype("{null f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_158() { checkNotSubtype("{null f2}","null|{null f1}"); }
	@Test @Ignore public void test_159() { checkNotSubtype("{null f2}","null|{null f2}"); }
	@Test @Ignore public void test_160() { checkNotSubtype("{null f2}","{null f1}|null"); }
	@Test @Ignore public void test_161() { checkNotSubtype("{null f2}","{null f2}|null"); }
	@Test @Ignore public void test_162() { checkNotSubtype("{null f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_163() { checkNotSubtype("{null f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_164() { checkNotSubtype("{null f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_165() { checkNotSubtype("{null f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_166() { checkNotSubtype("{null f2}","null"); }
	@Test @Ignore public void test_167() { checkNotSubtype("{null f2}","null"); }
	@Test @Ignore public void test_168() { checkNotSubtype("{null f2}","null"); }
	@Test @Ignore public void test_169() { checkNotSubtype("{null f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_170() { checkNotSubtype("{null f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_171() { checkNotSubtype("{null f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_172() { checkNotSubtype("{null f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_173() { checkNotSubtype("{null f2}","{null f1}|null"); }
	@Test @Ignore public void test_174() { checkNotSubtype("{null f2}","{null f2}|null"); }
	@Test @Ignore public void test_175() { checkNotSubtype("{null f2}","null|{null f1}"); }
	@Test @Ignore public void test_176() { checkNotSubtype("{null f2}","null|{null f2}"); }
	@Test @Ignore public void test_177() { checkNotSubtype("{null f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_178() { checkNotSubtype("{null f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_179() { checkNotSubtype("{null f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_180() { checkNotSubtype("{null f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_181() { checkNotSubtype("{null f2}","null"); }
	@Test @Ignore public void test_182() { checkNotSubtype("{null f2}","null"); }
	@Test @Ignore public void test_183() { checkNotSubtype("{null f2}","null"); }
	@Test @Ignore public void test_184() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_185() { checkNotSubtype("(null,null)","{null f1}"); }
	@Test @Ignore public void test_186() { checkNotSubtype("(null,null)","{null f2}"); }
	@Test @Ignore public void test_187() { checkIsSubtype("(null,null)","(null,null)"); }
	@Test @Ignore public void test_188() { checkIsSubtype("(null,null)","(null,null)"); }
	@Test @Ignore public void test_189() { checkNotSubtype("(null,null)","(null,{null f1})"); }
	@Test @Ignore public void test_190() { checkNotSubtype("(null,null)","(null,{null f2})"); }
	@Test @Ignore public void test_191() { checkNotSubtype("(null,null)","({null f1},null)"); }
	@Test @Ignore public void test_192() { checkNotSubtype("(null,null)","({null f2},null)"); }
	@Test @Ignore public void test_193() { checkNotSubtype("(null,null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_194() { checkNotSubtype("(null,null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_195() { checkNotSubtype("(null,null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_196() { checkNotSubtype("(null,null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_197() { checkNotSubtype("(null,null)","({null f1},null)"); }
	@Test @Ignore public void test_198() { checkNotSubtype("(null,null)","({null f2},null)"); }
	@Test @Ignore public void test_199() { checkNotSubtype("(null,null)","(null,{null f1})"); }
	@Test @Ignore public void test_200() { checkNotSubtype("(null,null)","(null,{null f2})"); }
	@Test @Ignore public void test_201() { checkNotSubtype("(null,null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_202() { checkNotSubtype("(null,null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_203() { checkNotSubtype("(null,null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_204() { checkNotSubtype("(null,null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_205() { checkNotSubtype("(null,null)","{{null f1} f1}"); }
	@Test @Ignore public void test_206() { checkNotSubtype("(null,null)","{{null f2} f1}"); }
	@Test @Ignore public void test_207() { checkNotSubtype("(null,null)","{{null f1} f2}"); }
	@Test @Ignore public void test_208() { checkNotSubtype("(null,null)","{{null f2} f2}"); }
	@Test @Ignore public void test_209() { checkNotSubtype("(null,null)","X<{X|null f1}>"); }
	@Test @Ignore public void test_210() { checkNotSubtype("(null,null)","X<{null|X f1}>"); }
	@Test @Ignore public void test_211() { checkNotSubtype("(null,null)","X<{null|X f2}>"); }
	@Test @Ignore public void test_212() { checkNotSubtype("(null,null)","X<{X|null f2}>"); }
	@Test @Ignore public void test_213() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_214() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_215() { checkNotSubtype("(null,null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_216() { checkNotSubtype("(null,null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_217() { checkNotSubtype("(null,null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_218() { checkNotSubtype("(null,null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_219() { checkNotSubtype("(null,null)","null|{null f1}"); }
	@Test @Ignore public void test_220() { checkNotSubtype("(null,null)","null|{null f2}"); }
	@Test @Ignore public void test_221() { checkNotSubtype("(null,null)","{null f1}|null"); }
	@Test @Ignore public void test_222() { checkNotSubtype("(null,null)","{null f2}|null"); }
	@Test @Ignore public void test_223() { checkNotSubtype("(null,null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_224() { checkNotSubtype("(null,null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_225() { checkNotSubtype("(null,null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_226() { checkNotSubtype("(null,null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_227() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_228() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_229() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_230() { checkNotSubtype("(null,null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_231() { checkNotSubtype("(null,null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_232() { checkNotSubtype("(null,null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_233() { checkNotSubtype("(null,null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_234() { checkNotSubtype("(null,null)","{null f1}|null"); }
	@Test @Ignore public void test_235() { checkNotSubtype("(null,null)","{null f2}|null"); }
	@Test @Ignore public void test_236() { checkNotSubtype("(null,null)","null|{null f1}"); }
	@Test @Ignore public void test_237() { checkNotSubtype("(null,null)","null|{null f2}"); }
	@Test @Ignore public void test_238() { checkNotSubtype("(null,null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_239() { checkNotSubtype("(null,null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_240() { checkNotSubtype("(null,null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_241() { checkNotSubtype("(null,null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_242() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_243() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_244() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_245() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_246() { checkNotSubtype("(null,null)","{null f1}"); }
	@Test @Ignore public void test_247() { checkNotSubtype("(null,null)","{null f2}"); }
	@Test @Ignore public void test_248() { checkIsSubtype("(null,null)","(null,null)"); }
	@Test @Ignore public void test_249() { checkIsSubtype("(null,null)","(null,null)"); }
	@Test @Ignore public void test_250() { checkNotSubtype("(null,null)","(null,{null f1})"); }
	@Test @Ignore public void test_251() { checkNotSubtype("(null,null)","(null,{null f2})"); }
	@Test @Ignore public void test_252() { checkNotSubtype("(null,null)","({null f1},null)"); }
	@Test @Ignore public void test_253() { checkNotSubtype("(null,null)","({null f2},null)"); }
	@Test @Ignore public void test_254() { checkNotSubtype("(null,null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_255() { checkNotSubtype("(null,null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_256() { checkNotSubtype("(null,null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_257() { checkNotSubtype("(null,null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_258() { checkNotSubtype("(null,null)","({null f1},null)"); }
	@Test @Ignore public void test_259() { checkNotSubtype("(null,null)","({null f2},null)"); }
	@Test @Ignore public void test_260() { checkNotSubtype("(null,null)","(null,{null f1})"); }
	@Test @Ignore public void test_261() { checkNotSubtype("(null,null)","(null,{null f2})"); }
	@Test @Ignore public void test_262() { checkNotSubtype("(null,null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_263() { checkNotSubtype("(null,null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_264() { checkNotSubtype("(null,null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_265() { checkNotSubtype("(null,null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_266() { checkNotSubtype("(null,null)","{{null f1} f1}"); }
	@Test @Ignore public void test_267() { checkNotSubtype("(null,null)","{{null f2} f1}"); }
	@Test @Ignore public void test_268() { checkNotSubtype("(null,null)","{{null f1} f2}"); }
	@Test @Ignore public void test_269() { checkNotSubtype("(null,null)","{{null f2} f2}"); }
	@Test @Ignore public void test_270() { checkNotSubtype("(null,null)","X<{X|null f1}>"); }
	@Test @Ignore public void test_271() { checkNotSubtype("(null,null)","X<{null|X f1}>"); }
	@Test @Ignore public void test_272() { checkNotSubtype("(null,null)","X<{null|X f2}>"); }
	@Test @Ignore public void test_273() { checkNotSubtype("(null,null)","X<{X|null f2}>"); }
	@Test @Ignore public void test_274() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_275() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_276() { checkNotSubtype("(null,null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_277() { checkNotSubtype("(null,null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_278() { checkNotSubtype("(null,null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_279() { checkNotSubtype("(null,null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_280() { checkNotSubtype("(null,null)","null|{null f1}"); }
	@Test @Ignore public void test_281() { checkNotSubtype("(null,null)","null|{null f2}"); }
	@Test @Ignore public void test_282() { checkNotSubtype("(null,null)","{null f1}|null"); }
	@Test @Ignore public void test_283() { checkNotSubtype("(null,null)","{null f2}|null"); }
	@Test @Ignore public void test_284() { checkNotSubtype("(null,null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_285() { checkNotSubtype("(null,null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_286() { checkNotSubtype("(null,null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_287() { checkNotSubtype("(null,null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_288() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_289() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_290() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_291() { checkNotSubtype("(null,null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_292() { checkNotSubtype("(null,null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_293() { checkNotSubtype("(null,null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_294() { checkNotSubtype("(null,null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_295() { checkNotSubtype("(null,null)","{null f1}|null"); }
	@Test @Ignore public void test_296() { checkNotSubtype("(null,null)","{null f2}|null"); }
	@Test @Ignore public void test_297() { checkNotSubtype("(null,null)","null|{null f1}"); }
	@Test @Ignore public void test_298() { checkNotSubtype("(null,null)","null|{null f2}"); }
	@Test @Ignore public void test_299() { checkNotSubtype("(null,null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_300() { checkNotSubtype("(null,null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_301() { checkNotSubtype("(null,null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_302() { checkNotSubtype("(null,null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_303() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_304() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_305() { checkNotSubtype("(null,null)","null"); }
	@Test @Ignore public void test_306() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_307() { checkNotSubtype("(null,{null f1})","{null f1}"); }
	@Test @Ignore public void test_308() { checkNotSubtype("(null,{null f1})","{null f2}"); }
	@Test @Ignore public void test_309() { checkNotSubtype("(null,{null f1})","(null,null)"); }
	@Test @Ignore public void test_310() { checkNotSubtype("(null,{null f1})","(null,null)"); }
	@Test @Ignore public void test_311() { checkIsSubtype("(null,{null f1})","(null,{null f1})"); }
	@Test @Ignore public void test_312() { checkNotSubtype("(null,{null f1})","(null,{null f2})"); }
	@Test @Ignore public void test_313() { checkNotSubtype("(null,{null f1})","({null f1},null)"); }
	@Test @Ignore public void test_314() { checkNotSubtype("(null,{null f1})","({null f2},null)"); }
	@Test @Ignore public void test_315() { checkNotSubtype("(null,{null f1})","X<(null,X|null)>"); }
	@Test @Ignore public void test_316() { checkNotSubtype("(null,{null f1})","X<(null,null|X)>"); }
	@Test @Ignore public void test_317() { checkNotSubtype("(null,{null f1})","X<(null|X,null)>"); }
	@Test @Ignore public void test_318() { checkNotSubtype("(null,{null f1})","X<(X|null,null)>"); }
	@Test @Ignore public void test_319() { checkNotSubtype("(null,{null f1})","({null f1},null)"); }
	@Test @Ignore public void test_320() { checkNotSubtype("(null,{null f1})","({null f2},null)"); }
	@Test @Ignore public void test_321() { checkIsSubtype("(null,{null f1})","(null,{null f1})"); }
	@Test @Ignore public void test_322() { checkNotSubtype("(null,{null f1})","(null,{null f2})"); }
	@Test @Ignore public void test_323() { checkNotSubtype("(null,{null f1})","X<(X|null,null)>"); }
	@Test @Ignore public void test_324() { checkNotSubtype("(null,{null f1})","X<(null|X,null)>"); }
	@Test @Ignore public void test_325() { checkNotSubtype("(null,{null f1})","X<(null,null|X)>"); }
	@Test @Ignore public void test_326() { checkNotSubtype("(null,{null f1})","X<(null,X|null)>"); }
	@Test @Ignore public void test_327() { checkNotSubtype("(null,{null f1})","{{null f1} f1}"); }
	@Test @Ignore public void test_328() { checkNotSubtype("(null,{null f1})","{{null f2} f1}"); }
	@Test @Ignore public void test_329() { checkNotSubtype("(null,{null f1})","{{null f1} f2}"); }
	@Test @Ignore public void test_330() { checkNotSubtype("(null,{null f1})","{{null f2} f2}"); }
	@Test @Ignore public void test_331() { checkNotSubtype("(null,{null f1})","X<{X|null f1}>"); }
	@Test @Ignore public void test_332() { checkNotSubtype("(null,{null f1})","X<{null|X f1}>"); }
	@Test @Ignore public void test_333() { checkNotSubtype("(null,{null f1})","X<{null|X f2}>"); }
	@Test @Ignore public void test_334() { checkNotSubtype("(null,{null f1})","X<{X|null f2}>"); }
	@Test @Ignore public void test_335() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_336() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_337() { checkNotSubtype("(null,{null f1})","X<null|(X,null)>"); }
	@Test @Ignore public void test_338() { checkNotSubtype("(null,{null f1})","X<null|(null,X)>"); }
	@Test @Ignore public void test_339() { checkNotSubtype("(null,{null f1})","X<(null,X)|null>"); }
	@Test @Ignore public void test_340() { checkNotSubtype("(null,{null f1})","X<(X,null)|null>"); }
	@Test @Ignore public void test_341() { checkNotSubtype("(null,{null f1})","null|{null f1}"); }
	@Test @Ignore public void test_342() { checkNotSubtype("(null,{null f1})","null|{null f2}"); }
	@Test @Ignore public void test_343() { checkNotSubtype("(null,{null f1})","{null f1}|null"); }
	@Test @Ignore public void test_344() { checkNotSubtype("(null,{null f1})","{null f2}|null"); }
	@Test @Ignore public void test_345() { checkNotSubtype("(null,{null f1})","X<null|{X f1}>"); }
	@Test @Ignore public void test_346() { checkNotSubtype("(null,{null f1})","X<null|{X f2}>"); }
	@Test @Ignore public void test_347() { checkNotSubtype("(null,{null f1})","X<{X f1}|null>"); }
	@Test @Ignore public void test_348() { checkNotSubtype("(null,{null f1})","X<{X f2}|null>"); }
	@Test @Ignore public void test_349() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_350() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_351() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_352() { checkNotSubtype("(null,{null f1})","X<(X,null)|null>"); }
	@Test @Ignore public void test_353() { checkNotSubtype("(null,{null f1})","X<(null,X)|null>"); }
	@Test @Ignore public void test_354() { checkNotSubtype("(null,{null f1})","X<null|(null,X)>"); }
	@Test @Ignore public void test_355() { checkNotSubtype("(null,{null f1})","X<null|(X,null)>"); }
	@Test @Ignore public void test_356() { checkNotSubtype("(null,{null f1})","{null f1}|null"); }
	@Test @Ignore public void test_357() { checkNotSubtype("(null,{null f1})","{null f2}|null"); }
	@Test @Ignore public void test_358() { checkNotSubtype("(null,{null f1})","null|{null f1}"); }
	@Test @Ignore public void test_359() { checkNotSubtype("(null,{null f1})","null|{null f2}"); }
	@Test @Ignore public void test_360() { checkNotSubtype("(null,{null f1})","X<{X f1}|null>"); }
	@Test @Ignore public void test_361() { checkNotSubtype("(null,{null f1})","X<{X f2}|null>"); }
	@Test @Ignore public void test_362() { checkNotSubtype("(null,{null f1})","X<null|{X f1}>"); }
	@Test @Ignore public void test_363() { checkNotSubtype("(null,{null f1})","X<null|{X f2}>"); }
	@Test @Ignore public void test_364() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_365() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_366() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_367() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_368() { checkNotSubtype("(null,{null f2})","{null f1}"); }
	@Test @Ignore public void test_369() { checkNotSubtype("(null,{null f2})","{null f2}"); }
	@Test @Ignore public void test_370() { checkNotSubtype("(null,{null f2})","(null,null)"); }
	@Test @Ignore public void test_371() { checkNotSubtype("(null,{null f2})","(null,null)"); }
	@Test @Ignore public void test_372() { checkNotSubtype("(null,{null f2})","(null,{null f1})"); }
	@Test @Ignore public void test_373() { checkIsSubtype("(null,{null f2})","(null,{null f2})"); }
	@Test @Ignore public void test_374() { checkNotSubtype("(null,{null f2})","({null f1},null)"); }
	@Test @Ignore public void test_375() { checkNotSubtype("(null,{null f2})","({null f2},null)"); }
	@Test @Ignore public void test_376() { checkNotSubtype("(null,{null f2})","X<(null,X|null)>"); }
	@Test @Ignore public void test_377() { checkNotSubtype("(null,{null f2})","X<(null,null|X)>"); }
	@Test @Ignore public void test_378() { checkNotSubtype("(null,{null f2})","X<(null|X,null)>"); }
	@Test @Ignore public void test_379() { checkNotSubtype("(null,{null f2})","X<(X|null,null)>"); }
	@Test @Ignore public void test_380() { checkNotSubtype("(null,{null f2})","({null f1},null)"); }
	@Test @Ignore public void test_381() { checkNotSubtype("(null,{null f2})","({null f2},null)"); }
	@Test @Ignore public void test_382() { checkNotSubtype("(null,{null f2})","(null,{null f1})"); }
	@Test @Ignore public void test_383() { checkIsSubtype("(null,{null f2})","(null,{null f2})"); }
	@Test @Ignore public void test_384() { checkNotSubtype("(null,{null f2})","X<(X|null,null)>"); }
	@Test @Ignore public void test_385() { checkNotSubtype("(null,{null f2})","X<(null|X,null)>"); }
	@Test @Ignore public void test_386() { checkNotSubtype("(null,{null f2})","X<(null,null|X)>"); }
	@Test @Ignore public void test_387() { checkNotSubtype("(null,{null f2})","X<(null,X|null)>"); }
	@Test @Ignore public void test_388() { checkNotSubtype("(null,{null f2})","{{null f1} f1}"); }
	@Test @Ignore public void test_389() { checkNotSubtype("(null,{null f2})","{{null f2} f1}"); }
	@Test @Ignore public void test_390() { checkNotSubtype("(null,{null f2})","{{null f1} f2}"); }
	@Test @Ignore public void test_391() { checkNotSubtype("(null,{null f2})","{{null f2} f2}"); }
	@Test @Ignore public void test_392() { checkNotSubtype("(null,{null f2})","X<{X|null f1}>"); }
	@Test @Ignore public void test_393() { checkNotSubtype("(null,{null f2})","X<{null|X f1}>"); }
	@Test @Ignore public void test_394() { checkNotSubtype("(null,{null f2})","X<{null|X f2}>"); }
	@Test @Ignore public void test_395() { checkNotSubtype("(null,{null f2})","X<{X|null f2}>"); }
	@Test @Ignore public void test_396() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_397() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_398() { checkNotSubtype("(null,{null f2})","X<null|(X,null)>"); }
	@Test @Ignore public void test_399() { checkNotSubtype("(null,{null f2})","X<null|(null,X)>"); }
	@Test @Ignore public void test_400() { checkNotSubtype("(null,{null f2})","X<(null,X)|null>"); }
	@Test @Ignore public void test_401() { checkNotSubtype("(null,{null f2})","X<(X,null)|null>"); }
	@Test @Ignore public void test_402() { checkNotSubtype("(null,{null f2})","null|{null f1}"); }
	@Test @Ignore public void test_403() { checkNotSubtype("(null,{null f2})","null|{null f2}"); }
	@Test @Ignore public void test_404() { checkNotSubtype("(null,{null f2})","{null f1}|null"); }
	@Test @Ignore public void test_405() { checkNotSubtype("(null,{null f2})","{null f2}|null"); }
	@Test @Ignore public void test_406() { checkNotSubtype("(null,{null f2})","X<null|{X f1}>"); }
	@Test @Ignore public void test_407() { checkNotSubtype("(null,{null f2})","X<null|{X f2}>"); }
	@Test @Ignore public void test_408() { checkNotSubtype("(null,{null f2})","X<{X f1}|null>"); }
	@Test @Ignore public void test_409() { checkNotSubtype("(null,{null f2})","X<{X f2}|null>"); }
	@Test @Ignore public void test_410() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_411() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_412() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_413() { checkNotSubtype("(null,{null f2})","X<(X,null)|null>"); }
	@Test @Ignore public void test_414() { checkNotSubtype("(null,{null f2})","X<(null,X)|null>"); }
	@Test @Ignore public void test_415() { checkNotSubtype("(null,{null f2})","X<null|(null,X)>"); }
	@Test @Ignore public void test_416() { checkNotSubtype("(null,{null f2})","X<null|(X,null)>"); }
	@Test @Ignore public void test_417() { checkNotSubtype("(null,{null f2})","{null f1}|null"); }
	@Test @Ignore public void test_418() { checkNotSubtype("(null,{null f2})","{null f2}|null"); }
	@Test @Ignore public void test_419() { checkNotSubtype("(null,{null f2})","null|{null f1}"); }
	@Test @Ignore public void test_420() { checkNotSubtype("(null,{null f2})","null|{null f2}"); }
	@Test @Ignore public void test_421() { checkNotSubtype("(null,{null f2})","X<{X f1}|null>"); }
	@Test @Ignore public void test_422() { checkNotSubtype("(null,{null f2})","X<{X f2}|null>"); }
	@Test @Ignore public void test_423() { checkNotSubtype("(null,{null f2})","X<null|{X f1}>"); }
	@Test @Ignore public void test_424() { checkNotSubtype("(null,{null f2})","X<null|{X f2}>"); }
	@Test @Ignore public void test_425() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_426() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_427() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_428() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_429() { checkNotSubtype("({null f1},null)","{null f1}"); }
	@Test @Ignore public void test_430() { checkNotSubtype("({null f1},null)","{null f2}"); }
	@Test @Ignore public void test_431() { checkNotSubtype("({null f1},null)","(null,null)"); }
	@Test @Ignore public void test_432() { checkNotSubtype("({null f1},null)","(null,null)"); }
	@Test @Ignore public void test_433() { checkNotSubtype("({null f1},null)","(null,{null f1})"); }
	@Test @Ignore public void test_434() { checkNotSubtype("({null f1},null)","(null,{null f2})"); }
	@Test @Ignore public void test_435() { checkIsSubtype("({null f1},null)","({null f1},null)"); }
	@Test @Ignore public void test_436() { checkNotSubtype("({null f1},null)","({null f2},null)"); }
	@Test @Ignore public void test_437() { checkNotSubtype("({null f1},null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_438() { checkNotSubtype("({null f1},null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_439() { checkNotSubtype("({null f1},null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_440() { checkNotSubtype("({null f1},null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_441() { checkIsSubtype("({null f1},null)","({null f1},null)"); }
	@Test @Ignore public void test_442() { checkNotSubtype("({null f1},null)","({null f2},null)"); }
	@Test @Ignore public void test_443() { checkNotSubtype("({null f1},null)","(null,{null f1})"); }
	@Test @Ignore public void test_444() { checkNotSubtype("({null f1},null)","(null,{null f2})"); }
	@Test @Ignore public void test_445() { checkNotSubtype("({null f1},null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_446() { checkNotSubtype("({null f1},null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_447() { checkNotSubtype("({null f1},null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_448() { checkNotSubtype("({null f1},null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_449() { checkNotSubtype("({null f1},null)","{{null f1} f1}"); }
	@Test @Ignore public void test_450() { checkNotSubtype("({null f1},null)","{{null f2} f1}"); }
	@Test @Ignore public void test_451() { checkNotSubtype("({null f1},null)","{{null f1} f2}"); }
	@Test @Ignore public void test_452() { checkNotSubtype("({null f1},null)","{{null f2} f2}"); }
	@Test @Ignore public void test_453() { checkNotSubtype("({null f1},null)","X<{X|null f1}>"); }
	@Test @Ignore public void test_454() { checkNotSubtype("({null f1},null)","X<{null|X f1}>"); }
	@Test @Ignore public void test_455() { checkNotSubtype("({null f1},null)","X<{null|X f2}>"); }
	@Test @Ignore public void test_456() { checkNotSubtype("({null f1},null)","X<{X|null f2}>"); }
	@Test @Ignore public void test_457() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_458() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_459() { checkNotSubtype("({null f1},null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_460() { checkNotSubtype("({null f1},null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_461() { checkNotSubtype("({null f1},null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_462() { checkNotSubtype("({null f1},null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_463() { checkNotSubtype("({null f1},null)","null|{null f1}"); }
	@Test @Ignore public void test_464() { checkNotSubtype("({null f1},null)","null|{null f2}"); }
	@Test @Ignore public void test_465() { checkNotSubtype("({null f1},null)","{null f1}|null"); }
	@Test @Ignore public void test_466() { checkNotSubtype("({null f1},null)","{null f2}|null"); }
	@Test @Ignore public void test_467() { checkNotSubtype("({null f1},null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_468() { checkNotSubtype("({null f1},null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_469() { checkNotSubtype("({null f1},null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_470() { checkNotSubtype("({null f1},null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_471() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_472() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_473() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_474() { checkNotSubtype("({null f1},null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_475() { checkNotSubtype("({null f1},null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_476() { checkNotSubtype("({null f1},null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_477() { checkNotSubtype("({null f1},null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_478() { checkNotSubtype("({null f1},null)","{null f1}|null"); }
	@Test @Ignore public void test_479() { checkNotSubtype("({null f1},null)","{null f2}|null"); }
	@Test @Ignore public void test_480() { checkNotSubtype("({null f1},null)","null|{null f1}"); }
	@Test @Ignore public void test_481() { checkNotSubtype("({null f1},null)","null|{null f2}"); }
	@Test @Ignore public void test_482() { checkNotSubtype("({null f1},null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_483() { checkNotSubtype("({null f1},null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_484() { checkNotSubtype("({null f1},null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_485() { checkNotSubtype("({null f1},null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_486() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_487() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_488() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_489() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_490() { checkNotSubtype("({null f2},null)","{null f1}"); }
	@Test @Ignore public void test_491() { checkNotSubtype("({null f2},null)","{null f2}"); }
	@Test @Ignore public void test_492() { checkNotSubtype("({null f2},null)","(null,null)"); }
	@Test @Ignore public void test_493() { checkNotSubtype("({null f2},null)","(null,null)"); }
	@Test @Ignore public void test_494() { checkNotSubtype("({null f2},null)","(null,{null f1})"); }
	@Test @Ignore public void test_495() { checkNotSubtype("({null f2},null)","(null,{null f2})"); }
	@Test @Ignore public void test_496() { checkNotSubtype("({null f2},null)","({null f1},null)"); }
	@Test @Ignore public void test_497() { checkIsSubtype("({null f2},null)","({null f2},null)"); }
	@Test @Ignore public void test_498() { checkNotSubtype("({null f2},null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_499() { checkNotSubtype("({null f2},null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_500() { checkNotSubtype("({null f2},null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_501() { checkNotSubtype("({null f2},null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_502() { checkNotSubtype("({null f2},null)","({null f1},null)"); }
	@Test @Ignore public void test_503() { checkIsSubtype("({null f2},null)","({null f2},null)"); }
	@Test @Ignore public void test_504() { checkNotSubtype("({null f2},null)","(null,{null f1})"); }
	@Test @Ignore public void test_505() { checkNotSubtype("({null f2},null)","(null,{null f2})"); }
	@Test @Ignore public void test_506() { checkNotSubtype("({null f2},null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_507() { checkNotSubtype("({null f2},null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_508() { checkNotSubtype("({null f2},null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_509() { checkNotSubtype("({null f2},null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_510() { checkNotSubtype("({null f2},null)","{{null f1} f1}"); }
	@Test @Ignore public void test_511() { checkNotSubtype("({null f2},null)","{{null f2} f1}"); }
	@Test @Ignore public void test_512() { checkNotSubtype("({null f2},null)","{{null f1} f2}"); }
	@Test @Ignore public void test_513() { checkNotSubtype("({null f2},null)","{{null f2} f2}"); }
	@Test @Ignore public void test_514() { checkNotSubtype("({null f2},null)","X<{X|null f1}>"); }
	@Test @Ignore public void test_515() { checkNotSubtype("({null f2},null)","X<{null|X f1}>"); }
	@Test @Ignore public void test_516() { checkNotSubtype("({null f2},null)","X<{null|X f2}>"); }
	@Test @Ignore public void test_517() { checkNotSubtype("({null f2},null)","X<{X|null f2}>"); }
	@Test @Ignore public void test_518() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_519() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_520() { checkNotSubtype("({null f2},null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_521() { checkNotSubtype("({null f2},null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_522() { checkNotSubtype("({null f2},null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_523() { checkNotSubtype("({null f2},null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_524() { checkNotSubtype("({null f2},null)","null|{null f1}"); }
	@Test @Ignore public void test_525() { checkNotSubtype("({null f2},null)","null|{null f2}"); }
	@Test @Ignore public void test_526() { checkNotSubtype("({null f2},null)","{null f1}|null"); }
	@Test @Ignore public void test_527() { checkNotSubtype("({null f2},null)","{null f2}|null"); }
	@Test @Ignore public void test_528() { checkNotSubtype("({null f2},null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_529() { checkNotSubtype("({null f2},null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_530() { checkNotSubtype("({null f2},null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_531() { checkNotSubtype("({null f2},null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_532() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_533() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_534() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_535() { checkNotSubtype("({null f2},null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_536() { checkNotSubtype("({null f2},null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_537() { checkNotSubtype("({null f2},null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_538() { checkNotSubtype("({null f2},null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_539() { checkNotSubtype("({null f2},null)","{null f1}|null"); }
	@Test @Ignore public void test_540() { checkNotSubtype("({null f2},null)","{null f2}|null"); }
	@Test @Ignore public void test_541() { checkNotSubtype("({null f2},null)","null|{null f1}"); }
	@Test @Ignore public void test_542() { checkNotSubtype("({null f2},null)","null|{null f2}"); }
	@Test @Ignore public void test_543() { checkNotSubtype("({null f2},null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_544() { checkNotSubtype("({null f2},null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_545() { checkNotSubtype("({null f2},null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_546() { checkNotSubtype("({null f2},null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_547() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_548() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_549() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_550() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_551() { checkNotSubtype("X<(null,X|null)>","{null f1}"); }
	@Test @Ignore public void test_552() { checkNotSubtype("X<(null,X|null)>","{null f2}"); }
	@Test @Ignore public void test_553() { checkIsSubtype("X<(null,X|null)>","(null,null)"); }
	@Test @Ignore public void test_554() { checkIsSubtype("X<(null,X|null)>","(null,null)"); }
	@Test @Ignore public void test_555() { checkNotSubtype("X<(null,X|null)>","(null,{null f1})"); }
	@Test @Ignore public void test_556() { checkNotSubtype("X<(null,X|null)>","(null,{null f2})"); }
	@Test @Ignore public void test_557() { checkNotSubtype("X<(null,X|null)>","({null f1},null)"); }
	@Test @Ignore public void test_558() { checkNotSubtype("X<(null,X|null)>","({null f2},null)"); }
	@Test @Ignore public void test_559() { checkIsSubtype("X<(null,X|null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_560() { checkIsSubtype("X<(null,X|null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_561() { checkNotSubtype("X<(null,X|null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_562() { checkNotSubtype("X<(null,X|null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_563() { checkNotSubtype("X<(null,X|null)>","({null f1},null)"); }
	@Test @Ignore public void test_564() { checkNotSubtype("X<(null,X|null)>","({null f2},null)"); }
	@Test @Ignore public void test_565() { checkNotSubtype("X<(null,X|null)>","(null,{null f1})"); }
	@Test @Ignore public void test_566() { checkNotSubtype("X<(null,X|null)>","(null,{null f2})"); }
	@Test @Ignore public void test_567() { checkNotSubtype("X<(null,X|null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_568() { checkNotSubtype("X<(null,X|null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_569() { checkIsSubtype("X<(null,X|null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_570() { checkIsSubtype("X<(null,X|null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_571() { checkNotSubtype("X<(null,X|null)>","{{null f1} f1}"); }
	@Test @Ignore public void test_572() { checkNotSubtype("X<(null,X|null)>","{{null f2} f1}"); }
	@Test @Ignore public void test_573() { checkNotSubtype("X<(null,X|null)>","{{null f1} f2}"); }
	@Test @Ignore public void test_574() { checkNotSubtype("X<(null,X|null)>","{{null f2} f2}"); }
	@Test @Ignore public void test_575() { checkNotSubtype("X<(null,X|null)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_576() { checkNotSubtype("X<(null,X|null)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_577() { checkNotSubtype("X<(null,X|null)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_578() { checkNotSubtype("X<(null,X|null)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_579() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_580() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_581() { checkNotSubtype("X<(null,X|null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_582() { checkNotSubtype("X<(null,X|null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_583() { checkNotSubtype("X<(null,X|null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_584() { checkNotSubtype("X<(null,X|null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_585() { checkNotSubtype("X<(null,X|null)>","null|{null f1}"); }
	@Test @Ignore public void test_586() { checkNotSubtype("X<(null,X|null)>","null|{null f2}"); }
	@Test @Ignore public void test_587() { checkNotSubtype("X<(null,X|null)>","{null f1}|null"); }
	@Test @Ignore public void test_588() { checkNotSubtype("X<(null,X|null)>","{null f2}|null"); }
	@Test @Ignore public void test_589() { checkNotSubtype("X<(null,X|null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_590() { checkNotSubtype("X<(null,X|null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_591() { checkNotSubtype("X<(null,X|null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_592() { checkNotSubtype("X<(null,X|null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_593() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_594() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_595() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_596() { checkNotSubtype("X<(null,X|null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_597() { checkNotSubtype("X<(null,X|null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_598() { checkNotSubtype("X<(null,X|null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_599() { checkNotSubtype("X<(null,X|null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_600() { checkNotSubtype("X<(null,X|null)>","{null f1}|null"); }
	@Test @Ignore public void test_601() { checkNotSubtype("X<(null,X|null)>","{null f2}|null"); }
	@Test @Ignore public void test_602() { checkNotSubtype("X<(null,X|null)>","null|{null f1}"); }
	@Test @Ignore public void test_603() { checkNotSubtype("X<(null,X|null)>","null|{null f2}"); }
	@Test @Ignore public void test_604() { checkNotSubtype("X<(null,X|null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_605() { checkNotSubtype("X<(null,X|null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_606() { checkNotSubtype("X<(null,X|null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_607() { checkNotSubtype("X<(null,X|null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_608() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_609() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_610() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_611() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_612() { checkNotSubtype("X<(null,null|X)>","{null f1}"); }
	@Test @Ignore public void test_613() { checkNotSubtype("X<(null,null|X)>","{null f2}"); }
	@Test @Ignore public void test_614() { checkIsSubtype("X<(null,null|X)>","(null,null)"); }
	@Test @Ignore public void test_615() { checkIsSubtype("X<(null,null|X)>","(null,null)"); }
	@Test @Ignore public void test_616() { checkNotSubtype("X<(null,null|X)>","(null,{null f1})"); }
	@Test @Ignore public void test_617() { checkNotSubtype("X<(null,null|X)>","(null,{null f2})"); }
	@Test @Ignore public void test_618() { checkNotSubtype("X<(null,null|X)>","({null f1},null)"); }
	@Test @Ignore public void test_619() { checkNotSubtype("X<(null,null|X)>","({null f2},null)"); }
	@Test @Ignore public void test_620() { checkIsSubtype("X<(null,null|X)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_621() { checkIsSubtype("X<(null,null|X)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_622() { checkNotSubtype("X<(null,null|X)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_623() { checkNotSubtype("X<(null,null|X)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_624() { checkNotSubtype("X<(null,null|X)>","({null f1},null)"); }
	@Test @Ignore public void test_625() { checkNotSubtype("X<(null,null|X)>","({null f2},null)"); }
	@Test @Ignore public void test_626() { checkNotSubtype("X<(null,null|X)>","(null,{null f1})"); }
	@Test @Ignore public void test_627() { checkNotSubtype("X<(null,null|X)>","(null,{null f2})"); }
	@Test @Ignore public void test_628() { checkNotSubtype("X<(null,null|X)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_629() { checkNotSubtype("X<(null,null|X)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_630() { checkIsSubtype("X<(null,null|X)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_631() { checkIsSubtype("X<(null,null|X)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_632() { checkNotSubtype("X<(null,null|X)>","{{null f1} f1}"); }
	@Test @Ignore public void test_633() { checkNotSubtype("X<(null,null|X)>","{{null f2} f1}"); }
	@Test @Ignore public void test_634() { checkNotSubtype("X<(null,null|X)>","{{null f1} f2}"); }
	@Test @Ignore public void test_635() { checkNotSubtype("X<(null,null|X)>","{{null f2} f2}"); }
	@Test @Ignore public void test_636() { checkNotSubtype("X<(null,null|X)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_637() { checkNotSubtype("X<(null,null|X)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_638() { checkNotSubtype("X<(null,null|X)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_639() { checkNotSubtype("X<(null,null|X)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_640() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_641() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_642() { checkNotSubtype("X<(null,null|X)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_643() { checkNotSubtype("X<(null,null|X)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_644() { checkNotSubtype("X<(null,null|X)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_645() { checkNotSubtype("X<(null,null|X)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_646() { checkNotSubtype("X<(null,null|X)>","null|{null f1}"); }
	@Test @Ignore public void test_647() { checkNotSubtype("X<(null,null|X)>","null|{null f2}"); }
	@Test @Ignore public void test_648() { checkNotSubtype("X<(null,null|X)>","{null f1}|null"); }
	@Test @Ignore public void test_649() { checkNotSubtype("X<(null,null|X)>","{null f2}|null"); }
	@Test @Ignore public void test_650() { checkNotSubtype("X<(null,null|X)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_651() { checkNotSubtype("X<(null,null|X)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_652() { checkNotSubtype("X<(null,null|X)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_653() { checkNotSubtype("X<(null,null|X)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_654() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_655() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_656() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_657() { checkNotSubtype("X<(null,null|X)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_658() { checkNotSubtype("X<(null,null|X)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_659() { checkNotSubtype("X<(null,null|X)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_660() { checkNotSubtype("X<(null,null|X)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_661() { checkNotSubtype("X<(null,null|X)>","{null f1}|null"); }
	@Test @Ignore public void test_662() { checkNotSubtype("X<(null,null|X)>","{null f2}|null"); }
	@Test @Ignore public void test_663() { checkNotSubtype("X<(null,null|X)>","null|{null f1}"); }
	@Test @Ignore public void test_664() { checkNotSubtype("X<(null,null|X)>","null|{null f2}"); }
	@Test @Ignore public void test_665() { checkNotSubtype("X<(null,null|X)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_666() { checkNotSubtype("X<(null,null|X)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_667() { checkNotSubtype("X<(null,null|X)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_668() { checkNotSubtype("X<(null,null|X)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_669() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_670() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_671() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_672() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_673() { checkNotSubtype("X<(null|X,null)>","{null f1}"); }
	@Test @Ignore public void test_674() { checkNotSubtype("X<(null|X,null)>","{null f2}"); }
	@Test @Ignore public void test_675() { checkIsSubtype("X<(null|X,null)>","(null,null)"); }
	@Test @Ignore public void test_676() { checkIsSubtype("X<(null|X,null)>","(null,null)"); }
	@Test @Ignore public void test_677() { checkNotSubtype("X<(null|X,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_678() { checkNotSubtype("X<(null|X,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_679() { checkNotSubtype("X<(null|X,null)>","({null f1},null)"); }
	@Test @Ignore public void test_680() { checkNotSubtype("X<(null|X,null)>","({null f2},null)"); }
	@Test @Ignore public void test_681() { checkNotSubtype("X<(null|X,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_682() { checkNotSubtype("X<(null|X,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_683() { checkIsSubtype("X<(null|X,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_684() { checkIsSubtype("X<(null|X,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_685() { checkNotSubtype("X<(null|X,null)>","({null f1},null)"); }
	@Test @Ignore public void test_686() { checkNotSubtype("X<(null|X,null)>","({null f2},null)"); }
	@Test @Ignore public void test_687() { checkNotSubtype("X<(null|X,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_688() { checkNotSubtype("X<(null|X,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_689() { checkIsSubtype("X<(null|X,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_690() { checkIsSubtype("X<(null|X,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_691() { checkNotSubtype("X<(null|X,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_692() { checkNotSubtype("X<(null|X,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_693() { checkNotSubtype("X<(null|X,null)>","{{null f1} f1}"); }
	@Test @Ignore public void test_694() { checkNotSubtype("X<(null|X,null)>","{{null f2} f1}"); }
	@Test @Ignore public void test_695() { checkNotSubtype("X<(null|X,null)>","{{null f1} f2}"); }
	@Test @Ignore public void test_696() { checkNotSubtype("X<(null|X,null)>","{{null f2} f2}"); }
	@Test @Ignore public void test_697() { checkNotSubtype("X<(null|X,null)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_698() { checkNotSubtype("X<(null|X,null)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_699() { checkNotSubtype("X<(null|X,null)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_700() { checkNotSubtype("X<(null|X,null)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_701() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_702() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_703() { checkNotSubtype("X<(null|X,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_704() { checkNotSubtype("X<(null|X,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_705() { checkNotSubtype("X<(null|X,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_706() { checkNotSubtype("X<(null|X,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_707() { checkNotSubtype("X<(null|X,null)>","null|{null f1}"); }
	@Test @Ignore public void test_708() { checkNotSubtype("X<(null|X,null)>","null|{null f2}"); }
	@Test @Ignore public void test_709() { checkNotSubtype("X<(null|X,null)>","{null f1}|null"); }
	@Test @Ignore public void test_710() { checkNotSubtype("X<(null|X,null)>","{null f2}|null"); }
	@Test @Ignore public void test_711() { checkNotSubtype("X<(null|X,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_712() { checkNotSubtype("X<(null|X,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_713() { checkNotSubtype("X<(null|X,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_714() { checkNotSubtype("X<(null|X,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_715() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_716() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_717() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_718() { checkNotSubtype("X<(null|X,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_719() { checkNotSubtype("X<(null|X,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_720() { checkNotSubtype("X<(null|X,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_721() { checkNotSubtype("X<(null|X,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_722() { checkNotSubtype("X<(null|X,null)>","{null f1}|null"); }
	@Test @Ignore public void test_723() { checkNotSubtype("X<(null|X,null)>","{null f2}|null"); }
	@Test @Ignore public void test_724() { checkNotSubtype("X<(null|X,null)>","null|{null f1}"); }
	@Test @Ignore public void test_725() { checkNotSubtype("X<(null|X,null)>","null|{null f2}"); }
	@Test @Ignore public void test_726() { checkNotSubtype("X<(null|X,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_727() { checkNotSubtype("X<(null|X,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_728() { checkNotSubtype("X<(null|X,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_729() { checkNotSubtype("X<(null|X,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_730() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_731() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_732() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_733() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_734() { checkNotSubtype("X<(X|null,null)>","{null f1}"); }
	@Test @Ignore public void test_735() { checkNotSubtype("X<(X|null,null)>","{null f2}"); }
	@Test @Ignore public void test_736() { checkIsSubtype("X<(X|null,null)>","(null,null)"); }
	@Test @Ignore public void test_737() { checkIsSubtype("X<(X|null,null)>","(null,null)"); }
	@Test @Ignore public void test_738() { checkNotSubtype("X<(X|null,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_739() { checkNotSubtype("X<(X|null,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_740() { checkNotSubtype("X<(X|null,null)>","({null f1},null)"); }
	@Test @Ignore public void test_741() { checkNotSubtype("X<(X|null,null)>","({null f2},null)"); }
	@Test @Ignore public void test_742() { checkNotSubtype("X<(X|null,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_743() { checkNotSubtype("X<(X|null,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_744() { checkIsSubtype("X<(X|null,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_745() { checkIsSubtype("X<(X|null,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_746() { checkNotSubtype("X<(X|null,null)>","({null f1},null)"); }
	@Test @Ignore public void test_747() { checkNotSubtype("X<(X|null,null)>","({null f2},null)"); }
	@Test @Ignore public void test_748() { checkNotSubtype("X<(X|null,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_749() { checkNotSubtype("X<(X|null,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_750() { checkIsSubtype("X<(X|null,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_751() { checkIsSubtype("X<(X|null,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_752() { checkNotSubtype("X<(X|null,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_753() { checkNotSubtype("X<(X|null,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_754() { checkNotSubtype("X<(X|null,null)>","{{null f1} f1}"); }
	@Test @Ignore public void test_755() { checkNotSubtype("X<(X|null,null)>","{{null f2} f1}"); }
	@Test @Ignore public void test_756() { checkNotSubtype("X<(X|null,null)>","{{null f1} f2}"); }
	@Test @Ignore public void test_757() { checkNotSubtype("X<(X|null,null)>","{{null f2} f2}"); }
	@Test @Ignore public void test_758() { checkNotSubtype("X<(X|null,null)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_759() { checkNotSubtype("X<(X|null,null)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_760() { checkNotSubtype("X<(X|null,null)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_761() { checkNotSubtype("X<(X|null,null)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_762() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_763() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_764() { checkNotSubtype("X<(X|null,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_765() { checkNotSubtype("X<(X|null,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_766() { checkNotSubtype("X<(X|null,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_767() { checkNotSubtype("X<(X|null,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_768() { checkNotSubtype("X<(X|null,null)>","null|{null f1}"); }
	@Test @Ignore public void test_769() { checkNotSubtype("X<(X|null,null)>","null|{null f2}"); }
	@Test @Ignore public void test_770() { checkNotSubtype("X<(X|null,null)>","{null f1}|null"); }
	@Test @Ignore public void test_771() { checkNotSubtype("X<(X|null,null)>","{null f2}|null"); }
	@Test @Ignore public void test_772() { checkNotSubtype("X<(X|null,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_773() { checkNotSubtype("X<(X|null,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_774() { checkNotSubtype("X<(X|null,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_775() { checkNotSubtype("X<(X|null,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_776() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_777() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_778() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_779() { checkNotSubtype("X<(X|null,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_780() { checkNotSubtype("X<(X|null,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_781() { checkNotSubtype("X<(X|null,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_782() { checkNotSubtype("X<(X|null,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_783() { checkNotSubtype("X<(X|null,null)>","{null f1}|null"); }
	@Test @Ignore public void test_784() { checkNotSubtype("X<(X|null,null)>","{null f2}|null"); }
	@Test @Ignore public void test_785() { checkNotSubtype("X<(X|null,null)>","null|{null f1}"); }
	@Test @Ignore public void test_786() { checkNotSubtype("X<(X|null,null)>","null|{null f2}"); }
	@Test @Ignore public void test_787() { checkNotSubtype("X<(X|null,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_788() { checkNotSubtype("X<(X|null,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_789() { checkNotSubtype("X<(X|null,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_790() { checkNotSubtype("X<(X|null,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_791() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_792() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_793() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_794() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_795() { checkNotSubtype("({null f1},null)","{null f1}"); }
	@Test @Ignore public void test_796() { checkNotSubtype("({null f1},null)","{null f2}"); }
	@Test @Ignore public void test_797() { checkNotSubtype("({null f1},null)","(null,null)"); }
	@Test @Ignore public void test_798() { checkNotSubtype("({null f1},null)","(null,null)"); }
	@Test @Ignore public void test_799() { checkNotSubtype("({null f1},null)","(null,{null f1})"); }
	@Test @Ignore public void test_800() { checkNotSubtype("({null f1},null)","(null,{null f2})"); }
	@Test @Ignore public void test_801() { checkIsSubtype("({null f1},null)","({null f1},null)"); }
	@Test @Ignore public void test_802() { checkNotSubtype("({null f1},null)","({null f2},null)"); }
	@Test @Ignore public void test_803() { checkNotSubtype("({null f1},null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_804() { checkNotSubtype("({null f1},null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_805() { checkNotSubtype("({null f1},null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_806() { checkNotSubtype("({null f1},null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_807() { checkIsSubtype("({null f1},null)","({null f1},null)"); }
	@Test @Ignore public void test_808() { checkNotSubtype("({null f1},null)","({null f2},null)"); }
	@Test @Ignore public void test_809() { checkNotSubtype("({null f1},null)","(null,{null f1})"); }
	@Test @Ignore public void test_810() { checkNotSubtype("({null f1},null)","(null,{null f2})"); }
	@Test @Ignore public void test_811() { checkNotSubtype("({null f1},null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_812() { checkNotSubtype("({null f1},null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_813() { checkNotSubtype("({null f1},null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_814() { checkNotSubtype("({null f1},null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_815() { checkNotSubtype("({null f1},null)","{{null f1} f1}"); }
	@Test @Ignore public void test_816() { checkNotSubtype("({null f1},null)","{{null f2} f1}"); }
	@Test @Ignore public void test_817() { checkNotSubtype("({null f1},null)","{{null f1} f2}"); }
	@Test @Ignore public void test_818() { checkNotSubtype("({null f1},null)","{{null f2} f2}"); }
	@Test @Ignore public void test_819() { checkNotSubtype("({null f1},null)","X<{X|null f1}>"); }
	@Test @Ignore public void test_820() { checkNotSubtype("({null f1},null)","X<{null|X f1}>"); }
	@Test @Ignore public void test_821() { checkNotSubtype("({null f1},null)","X<{null|X f2}>"); }
	@Test @Ignore public void test_822() { checkNotSubtype("({null f1},null)","X<{X|null f2}>"); }
	@Test @Ignore public void test_823() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_824() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_825() { checkNotSubtype("({null f1},null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_826() { checkNotSubtype("({null f1},null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_827() { checkNotSubtype("({null f1},null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_828() { checkNotSubtype("({null f1},null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_829() { checkNotSubtype("({null f1},null)","null|{null f1}"); }
	@Test @Ignore public void test_830() { checkNotSubtype("({null f1},null)","null|{null f2}"); }
	@Test @Ignore public void test_831() { checkNotSubtype("({null f1},null)","{null f1}|null"); }
	@Test @Ignore public void test_832() { checkNotSubtype("({null f1},null)","{null f2}|null"); }
	@Test @Ignore public void test_833() { checkNotSubtype("({null f1},null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_834() { checkNotSubtype("({null f1},null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_835() { checkNotSubtype("({null f1},null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_836() { checkNotSubtype("({null f1},null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_837() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_838() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_839() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_840() { checkNotSubtype("({null f1},null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_841() { checkNotSubtype("({null f1},null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_842() { checkNotSubtype("({null f1},null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_843() { checkNotSubtype("({null f1},null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_844() { checkNotSubtype("({null f1},null)","{null f1}|null"); }
	@Test @Ignore public void test_845() { checkNotSubtype("({null f1},null)","{null f2}|null"); }
	@Test @Ignore public void test_846() { checkNotSubtype("({null f1},null)","null|{null f1}"); }
	@Test @Ignore public void test_847() { checkNotSubtype("({null f1},null)","null|{null f2}"); }
	@Test @Ignore public void test_848() { checkNotSubtype("({null f1},null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_849() { checkNotSubtype("({null f1},null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_850() { checkNotSubtype("({null f1},null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_851() { checkNotSubtype("({null f1},null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_852() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_853() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_854() { checkNotSubtype("({null f1},null)","null"); }
	@Test @Ignore public void test_855() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_856() { checkNotSubtype("({null f2},null)","{null f1}"); }
	@Test @Ignore public void test_857() { checkNotSubtype("({null f2},null)","{null f2}"); }
	@Test @Ignore public void test_858() { checkNotSubtype("({null f2},null)","(null,null)"); }
	@Test @Ignore public void test_859() { checkNotSubtype("({null f2},null)","(null,null)"); }
	@Test @Ignore public void test_860() { checkNotSubtype("({null f2},null)","(null,{null f1})"); }
	@Test @Ignore public void test_861() { checkNotSubtype("({null f2},null)","(null,{null f2})"); }
	@Test @Ignore public void test_862() { checkNotSubtype("({null f2},null)","({null f1},null)"); }
	@Test @Ignore public void test_863() { checkIsSubtype("({null f2},null)","({null f2},null)"); }
	@Test @Ignore public void test_864() { checkNotSubtype("({null f2},null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_865() { checkNotSubtype("({null f2},null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_866() { checkNotSubtype("({null f2},null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_867() { checkNotSubtype("({null f2},null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_868() { checkNotSubtype("({null f2},null)","({null f1},null)"); }
	@Test @Ignore public void test_869() { checkIsSubtype("({null f2},null)","({null f2},null)"); }
	@Test @Ignore public void test_870() { checkNotSubtype("({null f2},null)","(null,{null f1})"); }
	@Test @Ignore public void test_871() { checkNotSubtype("({null f2},null)","(null,{null f2})"); }
	@Test @Ignore public void test_872() { checkNotSubtype("({null f2},null)","X<(X|null,null)>"); }
	@Test @Ignore public void test_873() { checkNotSubtype("({null f2},null)","X<(null|X,null)>"); }
	@Test @Ignore public void test_874() { checkNotSubtype("({null f2},null)","X<(null,null|X)>"); }
	@Test @Ignore public void test_875() { checkNotSubtype("({null f2},null)","X<(null,X|null)>"); }
	@Test @Ignore public void test_876() { checkNotSubtype("({null f2},null)","{{null f1} f1}"); }
	@Test @Ignore public void test_877() { checkNotSubtype("({null f2},null)","{{null f2} f1}"); }
	@Test @Ignore public void test_878() { checkNotSubtype("({null f2},null)","{{null f1} f2}"); }
	@Test @Ignore public void test_879() { checkNotSubtype("({null f2},null)","{{null f2} f2}"); }
	@Test @Ignore public void test_880() { checkNotSubtype("({null f2},null)","X<{X|null f1}>"); }
	@Test @Ignore public void test_881() { checkNotSubtype("({null f2},null)","X<{null|X f1}>"); }
	@Test @Ignore public void test_882() { checkNotSubtype("({null f2},null)","X<{null|X f2}>"); }
	@Test @Ignore public void test_883() { checkNotSubtype("({null f2},null)","X<{X|null f2}>"); }
	@Test @Ignore public void test_884() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_885() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_886() { checkNotSubtype("({null f2},null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_887() { checkNotSubtype("({null f2},null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_888() { checkNotSubtype("({null f2},null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_889() { checkNotSubtype("({null f2},null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_890() { checkNotSubtype("({null f2},null)","null|{null f1}"); }
	@Test @Ignore public void test_891() { checkNotSubtype("({null f2},null)","null|{null f2}"); }
	@Test @Ignore public void test_892() { checkNotSubtype("({null f2},null)","{null f1}|null"); }
	@Test @Ignore public void test_893() { checkNotSubtype("({null f2},null)","{null f2}|null"); }
	@Test @Ignore public void test_894() { checkNotSubtype("({null f2},null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_895() { checkNotSubtype("({null f2},null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_896() { checkNotSubtype("({null f2},null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_897() { checkNotSubtype("({null f2},null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_898() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_899() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_900() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_901() { checkNotSubtype("({null f2},null)","X<(X,null)|null>"); }
	@Test @Ignore public void test_902() { checkNotSubtype("({null f2},null)","X<(null,X)|null>"); }
	@Test @Ignore public void test_903() { checkNotSubtype("({null f2},null)","X<null|(null,X)>"); }
	@Test @Ignore public void test_904() { checkNotSubtype("({null f2},null)","X<null|(X,null)>"); }
	@Test @Ignore public void test_905() { checkNotSubtype("({null f2},null)","{null f1}|null"); }
	@Test @Ignore public void test_906() { checkNotSubtype("({null f2},null)","{null f2}|null"); }
	@Test @Ignore public void test_907() { checkNotSubtype("({null f2},null)","null|{null f1}"); }
	@Test @Ignore public void test_908() { checkNotSubtype("({null f2},null)","null|{null f2}"); }
	@Test @Ignore public void test_909() { checkNotSubtype("({null f2},null)","X<{X f1}|null>"); }
	@Test @Ignore public void test_910() { checkNotSubtype("({null f2},null)","X<{X f2}|null>"); }
	@Test @Ignore public void test_911() { checkNotSubtype("({null f2},null)","X<null|{X f1}>"); }
	@Test @Ignore public void test_912() { checkNotSubtype("({null f2},null)","X<null|{X f2}>"); }
	@Test @Ignore public void test_913() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_914() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_915() { checkNotSubtype("({null f2},null)","null"); }
	@Test @Ignore public void test_916() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_917() { checkNotSubtype("(null,{null f1})","{null f1}"); }
	@Test @Ignore public void test_918() { checkNotSubtype("(null,{null f1})","{null f2}"); }
	@Test @Ignore public void test_919() { checkNotSubtype("(null,{null f1})","(null,null)"); }
	@Test @Ignore public void test_920() { checkNotSubtype("(null,{null f1})","(null,null)"); }
	@Test @Ignore public void test_921() { checkIsSubtype("(null,{null f1})","(null,{null f1})"); }
	@Test @Ignore public void test_922() { checkNotSubtype("(null,{null f1})","(null,{null f2})"); }
	@Test @Ignore public void test_923() { checkNotSubtype("(null,{null f1})","({null f1},null)"); }
	@Test @Ignore public void test_924() { checkNotSubtype("(null,{null f1})","({null f2},null)"); }
	@Test @Ignore public void test_925() { checkNotSubtype("(null,{null f1})","X<(null,X|null)>"); }
	@Test @Ignore public void test_926() { checkNotSubtype("(null,{null f1})","X<(null,null|X)>"); }
	@Test @Ignore public void test_927() { checkNotSubtype("(null,{null f1})","X<(null|X,null)>"); }
	@Test @Ignore public void test_928() { checkNotSubtype("(null,{null f1})","X<(X|null,null)>"); }
	@Test @Ignore public void test_929() { checkNotSubtype("(null,{null f1})","({null f1},null)"); }
	@Test @Ignore public void test_930() { checkNotSubtype("(null,{null f1})","({null f2},null)"); }
	@Test @Ignore public void test_931() { checkIsSubtype("(null,{null f1})","(null,{null f1})"); }
	@Test @Ignore public void test_932() { checkNotSubtype("(null,{null f1})","(null,{null f2})"); }
	@Test @Ignore public void test_933() { checkNotSubtype("(null,{null f1})","X<(X|null,null)>"); }
	@Test @Ignore public void test_934() { checkNotSubtype("(null,{null f1})","X<(null|X,null)>"); }
	@Test @Ignore public void test_935() { checkNotSubtype("(null,{null f1})","X<(null,null|X)>"); }
	@Test @Ignore public void test_936() { checkNotSubtype("(null,{null f1})","X<(null,X|null)>"); }
	@Test @Ignore public void test_937() { checkNotSubtype("(null,{null f1})","{{null f1} f1}"); }
	@Test @Ignore public void test_938() { checkNotSubtype("(null,{null f1})","{{null f2} f1}"); }
	@Test @Ignore public void test_939() { checkNotSubtype("(null,{null f1})","{{null f1} f2}"); }
	@Test @Ignore public void test_940() { checkNotSubtype("(null,{null f1})","{{null f2} f2}"); }
	@Test @Ignore public void test_941() { checkNotSubtype("(null,{null f1})","X<{X|null f1}>"); }
	@Test @Ignore public void test_942() { checkNotSubtype("(null,{null f1})","X<{null|X f1}>"); }
	@Test @Ignore public void test_943() { checkNotSubtype("(null,{null f1})","X<{null|X f2}>"); }
	@Test @Ignore public void test_944() { checkNotSubtype("(null,{null f1})","X<{X|null f2}>"); }
	@Test @Ignore public void test_945() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_946() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_947() { checkNotSubtype("(null,{null f1})","X<null|(X,null)>"); }
	@Test @Ignore public void test_948() { checkNotSubtype("(null,{null f1})","X<null|(null,X)>"); }
	@Test @Ignore public void test_949() { checkNotSubtype("(null,{null f1})","X<(null,X)|null>"); }
	@Test @Ignore public void test_950() { checkNotSubtype("(null,{null f1})","X<(X,null)|null>"); }
	@Test @Ignore public void test_951() { checkNotSubtype("(null,{null f1})","null|{null f1}"); }
	@Test @Ignore public void test_952() { checkNotSubtype("(null,{null f1})","null|{null f2}"); }
	@Test @Ignore public void test_953() { checkNotSubtype("(null,{null f1})","{null f1}|null"); }
	@Test @Ignore public void test_954() { checkNotSubtype("(null,{null f1})","{null f2}|null"); }
	@Test @Ignore public void test_955() { checkNotSubtype("(null,{null f1})","X<null|{X f1}>"); }
	@Test @Ignore public void test_956() { checkNotSubtype("(null,{null f1})","X<null|{X f2}>"); }
	@Test @Ignore public void test_957() { checkNotSubtype("(null,{null f1})","X<{X f1}|null>"); }
	@Test @Ignore public void test_958() { checkNotSubtype("(null,{null f1})","X<{X f2}|null>"); }
	@Test @Ignore public void test_959() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_960() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_961() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_962() { checkNotSubtype("(null,{null f1})","X<(X,null)|null>"); }
	@Test @Ignore public void test_963() { checkNotSubtype("(null,{null f1})","X<(null,X)|null>"); }
	@Test @Ignore public void test_964() { checkNotSubtype("(null,{null f1})","X<null|(null,X)>"); }
	@Test @Ignore public void test_965() { checkNotSubtype("(null,{null f1})","X<null|(X,null)>"); }
	@Test @Ignore public void test_966() { checkNotSubtype("(null,{null f1})","{null f1}|null"); }
	@Test @Ignore public void test_967() { checkNotSubtype("(null,{null f1})","{null f2}|null"); }
	@Test @Ignore public void test_968() { checkNotSubtype("(null,{null f1})","null|{null f1}"); }
	@Test @Ignore public void test_969() { checkNotSubtype("(null,{null f1})","null|{null f2}"); }
	@Test @Ignore public void test_970() { checkNotSubtype("(null,{null f1})","X<{X f1}|null>"); }
	@Test @Ignore public void test_971() { checkNotSubtype("(null,{null f1})","X<{X f2}|null>"); }
	@Test @Ignore public void test_972() { checkNotSubtype("(null,{null f1})","X<null|{X f1}>"); }
	@Test @Ignore public void test_973() { checkNotSubtype("(null,{null f1})","X<null|{X f2}>"); }
	@Test @Ignore public void test_974() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_975() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_976() { checkNotSubtype("(null,{null f1})","null"); }
	@Test @Ignore public void test_977() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_978() { checkNotSubtype("(null,{null f2})","{null f1}"); }
	@Test @Ignore public void test_979() { checkNotSubtype("(null,{null f2})","{null f2}"); }
	@Test @Ignore public void test_980() { checkNotSubtype("(null,{null f2})","(null,null)"); }
	@Test @Ignore public void test_981() { checkNotSubtype("(null,{null f2})","(null,null)"); }
	@Test @Ignore public void test_982() { checkNotSubtype("(null,{null f2})","(null,{null f1})"); }
	@Test @Ignore public void test_983() { checkIsSubtype("(null,{null f2})","(null,{null f2})"); }
	@Test @Ignore public void test_984() { checkNotSubtype("(null,{null f2})","({null f1},null)"); }
	@Test @Ignore public void test_985() { checkNotSubtype("(null,{null f2})","({null f2},null)"); }
	@Test @Ignore public void test_986() { checkNotSubtype("(null,{null f2})","X<(null,X|null)>"); }
	@Test @Ignore public void test_987() { checkNotSubtype("(null,{null f2})","X<(null,null|X)>"); }
	@Test @Ignore public void test_988() { checkNotSubtype("(null,{null f2})","X<(null|X,null)>"); }
	@Test @Ignore public void test_989() { checkNotSubtype("(null,{null f2})","X<(X|null,null)>"); }
	@Test @Ignore public void test_990() { checkNotSubtype("(null,{null f2})","({null f1},null)"); }
	@Test @Ignore public void test_991() { checkNotSubtype("(null,{null f2})","({null f2},null)"); }
	@Test @Ignore public void test_992() { checkNotSubtype("(null,{null f2})","(null,{null f1})"); }
	@Test @Ignore public void test_993() { checkIsSubtype("(null,{null f2})","(null,{null f2})"); }
	@Test @Ignore public void test_994() { checkNotSubtype("(null,{null f2})","X<(X|null,null)>"); }
	@Test @Ignore public void test_995() { checkNotSubtype("(null,{null f2})","X<(null|X,null)>"); }
	@Test @Ignore public void test_996() { checkNotSubtype("(null,{null f2})","X<(null,null|X)>"); }
	@Test @Ignore public void test_997() { checkNotSubtype("(null,{null f2})","X<(null,X|null)>"); }
	@Test @Ignore public void test_998() { checkNotSubtype("(null,{null f2})","{{null f1} f1}"); }
	@Test @Ignore public void test_999() { checkNotSubtype("(null,{null f2})","{{null f2} f1}"); }
	@Test @Ignore public void test_1000() { checkNotSubtype("(null,{null f2})","{{null f1} f2}"); }
	@Test @Ignore public void test_1001() { checkNotSubtype("(null,{null f2})","{{null f2} f2}"); }
	@Test @Ignore public void test_1002() { checkNotSubtype("(null,{null f2})","X<{X|null f1}>"); }
	@Test @Ignore public void test_1003() { checkNotSubtype("(null,{null f2})","X<{null|X f1}>"); }
	@Test @Ignore public void test_1004() { checkNotSubtype("(null,{null f2})","X<{null|X f2}>"); }
	@Test @Ignore public void test_1005() { checkNotSubtype("(null,{null f2})","X<{X|null f2}>"); }
	@Test @Ignore public void test_1006() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_1007() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_1008() { checkNotSubtype("(null,{null f2})","X<null|(X,null)>"); }
	@Test @Ignore public void test_1009() { checkNotSubtype("(null,{null f2})","X<null|(null,X)>"); }
	@Test @Ignore public void test_1010() { checkNotSubtype("(null,{null f2})","X<(null,X)|null>"); }
	@Test @Ignore public void test_1011() { checkNotSubtype("(null,{null f2})","X<(X,null)|null>"); }
	@Test @Ignore public void test_1012() { checkNotSubtype("(null,{null f2})","null|{null f1}"); }
	@Test @Ignore public void test_1013() { checkNotSubtype("(null,{null f2})","null|{null f2}"); }
	@Test @Ignore public void test_1014() { checkNotSubtype("(null,{null f2})","{null f1}|null"); }
	@Test @Ignore public void test_1015() { checkNotSubtype("(null,{null f2})","{null f2}|null"); }
	@Test @Ignore public void test_1016() { checkNotSubtype("(null,{null f2})","X<null|{X f1}>"); }
	@Test @Ignore public void test_1017() { checkNotSubtype("(null,{null f2})","X<null|{X f2}>"); }
	@Test @Ignore public void test_1018() { checkNotSubtype("(null,{null f2})","X<{X f1}|null>"); }
	@Test @Ignore public void test_1019() { checkNotSubtype("(null,{null f2})","X<{X f2}|null>"); }
	@Test @Ignore public void test_1020() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_1021() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_1022() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_1023() { checkNotSubtype("(null,{null f2})","X<(X,null)|null>"); }
	@Test @Ignore public void test_1024() { checkNotSubtype("(null,{null f2})","X<(null,X)|null>"); }
	@Test @Ignore public void test_1025() { checkNotSubtype("(null,{null f2})","X<null|(null,X)>"); }
	@Test @Ignore public void test_1026() { checkNotSubtype("(null,{null f2})","X<null|(X,null)>"); }
	@Test @Ignore public void test_1027() { checkNotSubtype("(null,{null f2})","{null f1}|null"); }
	@Test @Ignore public void test_1028() { checkNotSubtype("(null,{null f2})","{null f2}|null"); }
	@Test @Ignore public void test_1029() { checkNotSubtype("(null,{null f2})","null|{null f1}"); }
	@Test @Ignore public void test_1030() { checkNotSubtype("(null,{null f2})","null|{null f2}"); }
	@Test @Ignore public void test_1031() { checkNotSubtype("(null,{null f2})","X<{X f1}|null>"); }
	@Test @Ignore public void test_1032() { checkNotSubtype("(null,{null f2})","X<{X f2}|null>"); }
	@Test @Ignore public void test_1033() { checkNotSubtype("(null,{null f2})","X<null|{X f1}>"); }
	@Test @Ignore public void test_1034() { checkNotSubtype("(null,{null f2})","X<null|{X f2}>"); }
	@Test @Ignore public void test_1035() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_1036() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_1037() { checkNotSubtype("(null,{null f2})","null"); }
	@Test @Ignore public void test_1038() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_1039() { checkNotSubtype("X<(X|null,null)>","{null f1}"); }
	@Test @Ignore public void test_1040() { checkNotSubtype("X<(X|null,null)>","{null f2}"); }
	@Test @Ignore public void test_1041() { checkIsSubtype("X<(X|null,null)>","(null,null)"); }
	@Test @Ignore public void test_1042() { checkIsSubtype("X<(X|null,null)>","(null,null)"); }
	@Test @Ignore public void test_1043() { checkNotSubtype("X<(X|null,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_1044() { checkNotSubtype("X<(X|null,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_1045() { checkNotSubtype("X<(X|null,null)>","({null f1},null)"); }
	@Test @Ignore public void test_1046() { checkNotSubtype("X<(X|null,null)>","({null f2},null)"); }
	@Test @Ignore public void test_1047() { checkNotSubtype("X<(X|null,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1048() { checkNotSubtype("X<(X|null,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1049() { checkIsSubtype("X<(X|null,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1050() { checkIsSubtype("X<(X|null,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1051() { checkNotSubtype("X<(X|null,null)>","({null f1},null)"); }
	@Test @Ignore public void test_1052() { checkNotSubtype("X<(X|null,null)>","({null f2},null)"); }
	@Test @Ignore public void test_1053() { checkNotSubtype("X<(X|null,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_1054() { checkNotSubtype("X<(X|null,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_1055() { checkIsSubtype("X<(X|null,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1056() { checkIsSubtype("X<(X|null,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1057() { checkNotSubtype("X<(X|null,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1058() { checkNotSubtype("X<(X|null,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1059() { checkNotSubtype("X<(X|null,null)>","{{null f1} f1}"); }
	@Test @Ignore public void test_1060() { checkNotSubtype("X<(X|null,null)>","{{null f2} f1}"); }
	@Test @Ignore public void test_1061() { checkNotSubtype("X<(X|null,null)>","{{null f1} f2}"); }
	@Test @Ignore public void test_1062() { checkNotSubtype("X<(X|null,null)>","{{null f2} f2}"); }
	@Test @Ignore public void test_1063() { checkNotSubtype("X<(X|null,null)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1064() { checkNotSubtype("X<(X|null,null)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1065() { checkNotSubtype("X<(X|null,null)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1066() { checkNotSubtype("X<(X|null,null)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1067() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_1068() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_1069() { checkNotSubtype("X<(X|null,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1070() { checkNotSubtype("X<(X|null,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1071() { checkNotSubtype("X<(X|null,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1072() { checkNotSubtype("X<(X|null,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1073() { checkNotSubtype("X<(X|null,null)>","null|{null f1}"); }
	@Test @Ignore public void test_1074() { checkNotSubtype("X<(X|null,null)>","null|{null f2}"); }
	@Test @Ignore public void test_1075() { checkNotSubtype("X<(X|null,null)>","{null f1}|null"); }
	@Test @Ignore public void test_1076() { checkNotSubtype("X<(X|null,null)>","{null f2}|null"); }
	@Test @Ignore public void test_1077() { checkNotSubtype("X<(X|null,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1078() { checkNotSubtype("X<(X|null,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1079() { checkNotSubtype("X<(X|null,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1080() { checkNotSubtype("X<(X|null,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1081() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_1082() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_1083() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_1084() { checkNotSubtype("X<(X|null,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1085() { checkNotSubtype("X<(X|null,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1086() { checkNotSubtype("X<(X|null,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1087() { checkNotSubtype("X<(X|null,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1088() { checkNotSubtype("X<(X|null,null)>","{null f1}|null"); }
	@Test @Ignore public void test_1089() { checkNotSubtype("X<(X|null,null)>","{null f2}|null"); }
	@Test @Ignore public void test_1090() { checkNotSubtype("X<(X|null,null)>","null|{null f1}"); }
	@Test @Ignore public void test_1091() { checkNotSubtype("X<(X|null,null)>","null|{null f2}"); }
	@Test @Ignore public void test_1092() { checkNotSubtype("X<(X|null,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1093() { checkNotSubtype("X<(X|null,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1094() { checkNotSubtype("X<(X|null,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1095() { checkNotSubtype("X<(X|null,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1096() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_1097() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_1098() { checkNotSubtype("X<(X|null,null)>","null"); }
	@Test @Ignore public void test_1099() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_1100() { checkNotSubtype("X<(null|X,null)>","{null f1}"); }
	@Test @Ignore public void test_1101() { checkNotSubtype("X<(null|X,null)>","{null f2}"); }
	@Test @Ignore public void test_1102() { checkIsSubtype("X<(null|X,null)>","(null,null)"); }
	@Test @Ignore public void test_1103() { checkIsSubtype("X<(null|X,null)>","(null,null)"); }
	@Test @Ignore public void test_1104() { checkNotSubtype("X<(null|X,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_1105() { checkNotSubtype("X<(null|X,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_1106() { checkNotSubtype("X<(null|X,null)>","({null f1},null)"); }
	@Test @Ignore public void test_1107() { checkNotSubtype("X<(null|X,null)>","({null f2},null)"); }
	@Test @Ignore public void test_1108() { checkNotSubtype("X<(null|X,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1109() { checkNotSubtype("X<(null|X,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1110() { checkIsSubtype("X<(null|X,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1111() { checkIsSubtype("X<(null|X,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1112() { checkNotSubtype("X<(null|X,null)>","({null f1},null)"); }
	@Test @Ignore public void test_1113() { checkNotSubtype("X<(null|X,null)>","({null f2},null)"); }
	@Test @Ignore public void test_1114() { checkNotSubtype("X<(null|X,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_1115() { checkNotSubtype("X<(null|X,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_1116() { checkIsSubtype("X<(null|X,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1117() { checkIsSubtype("X<(null|X,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1118() { checkNotSubtype("X<(null|X,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1119() { checkNotSubtype("X<(null|X,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1120() { checkNotSubtype("X<(null|X,null)>","{{null f1} f1}"); }
	@Test @Ignore public void test_1121() { checkNotSubtype("X<(null|X,null)>","{{null f2} f1}"); }
	@Test @Ignore public void test_1122() { checkNotSubtype("X<(null|X,null)>","{{null f1} f2}"); }
	@Test @Ignore public void test_1123() { checkNotSubtype("X<(null|X,null)>","{{null f2} f2}"); }
	@Test @Ignore public void test_1124() { checkNotSubtype("X<(null|X,null)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1125() { checkNotSubtype("X<(null|X,null)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1126() { checkNotSubtype("X<(null|X,null)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1127() { checkNotSubtype("X<(null|X,null)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1128() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_1129() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_1130() { checkNotSubtype("X<(null|X,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1131() { checkNotSubtype("X<(null|X,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1132() { checkNotSubtype("X<(null|X,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1133() { checkNotSubtype("X<(null|X,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1134() { checkNotSubtype("X<(null|X,null)>","null|{null f1}"); }
	@Test @Ignore public void test_1135() { checkNotSubtype("X<(null|X,null)>","null|{null f2}"); }
	@Test @Ignore public void test_1136() { checkNotSubtype("X<(null|X,null)>","{null f1}|null"); }
	@Test @Ignore public void test_1137() { checkNotSubtype("X<(null|X,null)>","{null f2}|null"); }
	@Test @Ignore public void test_1138() { checkNotSubtype("X<(null|X,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1139() { checkNotSubtype("X<(null|X,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1140() { checkNotSubtype("X<(null|X,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1141() { checkNotSubtype("X<(null|X,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1142() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_1143() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_1144() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_1145() { checkNotSubtype("X<(null|X,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1146() { checkNotSubtype("X<(null|X,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1147() { checkNotSubtype("X<(null|X,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1148() { checkNotSubtype("X<(null|X,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1149() { checkNotSubtype("X<(null|X,null)>","{null f1}|null"); }
	@Test @Ignore public void test_1150() { checkNotSubtype("X<(null|X,null)>","{null f2}|null"); }
	@Test @Ignore public void test_1151() { checkNotSubtype("X<(null|X,null)>","null|{null f1}"); }
	@Test @Ignore public void test_1152() { checkNotSubtype("X<(null|X,null)>","null|{null f2}"); }
	@Test @Ignore public void test_1153() { checkNotSubtype("X<(null|X,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1154() { checkNotSubtype("X<(null|X,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1155() { checkNotSubtype("X<(null|X,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1156() { checkNotSubtype("X<(null|X,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1157() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_1158() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_1159() { checkNotSubtype("X<(null|X,null)>","null"); }
	@Test @Ignore public void test_1160() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_1161() { checkNotSubtype("X<(null,null|X)>","{null f1}"); }
	@Test @Ignore public void test_1162() { checkNotSubtype("X<(null,null|X)>","{null f2}"); }
	@Test @Ignore public void test_1163() { checkIsSubtype("X<(null,null|X)>","(null,null)"); }
	@Test @Ignore public void test_1164() { checkIsSubtype("X<(null,null|X)>","(null,null)"); }
	@Test @Ignore public void test_1165() { checkNotSubtype("X<(null,null|X)>","(null,{null f1})"); }
	@Test @Ignore public void test_1166() { checkNotSubtype("X<(null,null|X)>","(null,{null f2})"); }
	@Test @Ignore public void test_1167() { checkNotSubtype("X<(null,null|X)>","({null f1},null)"); }
	@Test @Ignore public void test_1168() { checkNotSubtype("X<(null,null|X)>","({null f2},null)"); }
	@Test @Ignore public void test_1169() { checkIsSubtype("X<(null,null|X)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1170() { checkIsSubtype("X<(null,null|X)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1171() { checkNotSubtype("X<(null,null|X)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1172() { checkNotSubtype("X<(null,null|X)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1173() { checkNotSubtype("X<(null,null|X)>","({null f1},null)"); }
	@Test @Ignore public void test_1174() { checkNotSubtype("X<(null,null|X)>","({null f2},null)"); }
	@Test @Ignore public void test_1175() { checkNotSubtype("X<(null,null|X)>","(null,{null f1})"); }
	@Test @Ignore public void test_1176() { checkNotSubtype("X<(null,null|X)>","(null,{null f2})"); }
	@Test @Ignore public void test_1177() { checkNotSubtype("X<(null,null|X)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1178() { checkNotSubtype("X<(null,null|X)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1179() { checkIsSubtype("X<(null,null|X)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1180() { checkIsSubtype("X<(null,null|X)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1181() { checkNotSubtype("X<(null,null|X)>","{{null f1} f1}"); }
	@Test @Ignore public void test_1182() { checkNotSubtype("X<(null,null|X)>","{{null f2} f1}"); }
	@Test @Ignore public void test_1183() { checkNotSubtype("X<(null,null|X)>","{{null f1} f2}"); }
	@Test @Ignore public void test_1184() { checkNotSubtype("X<(null,null|X)>","{{null f2} f2}"); }
	@Test @Ignore public void test_1185() { checkNotSubtype("X<(null,null|X)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1186() { checkNotSubtype("X<(null,null|X)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1187() { checkNotSubtype("X<(null,null|X)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1188() { checkNotSubtype("X<(null,null|X)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1189() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_1190() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_1191() { checkNotSubtype("X<(null,null|X)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1192() { checkNotSubtype("X<(null,null|X)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1193() { checkNotSubtype("X<(null,null|X)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1194() { checkNotSubtype("X<(null,null|X)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1195() { checkNotSubtype("X<(null,null|X)>","null|{null f1}"); }
	@Test @Ignore public void test_1196() { checkNotSubtype("X<(null,null|X)>","null|{null f2}"); }
	@Test @Ignore public void test_1197() { checkNotSubtype("X<(null,null|X)>","{null f1}|null"); }
	@Test @Ignore public void test_1198() { checkNotSubtype("X<(null,null|X)>","{null f2}|null"); }
	@Test @Ignore public void test_1199() { checkNotSubtype("X<(null,null|X)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1200() { checkNotSubtype("X<(null,null|X)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1201() { checkNotSubtype("X<(null,null|X)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1202() { checkNotSubtype("X<(null,null|X)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1203() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_1204() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_1205() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_1206() { checkNotSubtype("X<(null,null|X)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1207() { checkNotSubtype("X<(null,null|X)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1208() { checkNotSubtype("X<(null,null|X)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1209() { checkNotSubtype("X<(null,null|X)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1210() { checkNotSubtype("X<(null,null|X)>","{null f1}|null"); }
	@Test @Ignore public void test_1211() { checkNotSubtype("X<(null,null|X)>","{null f2}|null"); }
	@Test @Ignore public void test_1212() { checkNotSubtype("X<(null,null|X)>","null|{null f1}"); }
	@Test @Ignore public void test_1213() { checkNotSubtype("X<(null,null|X)>","null|{null f2}"); }
	@Test @Ignore public void test_1214() { checkNotSubtype("X<(null,null|X)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1215() { checkNotSubtype("X<(null,null|X)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1216() { checkNotSubtype("X<(null,null|X)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1217() { checkNotSubtype("X<(null,null|X)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1218() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_1219() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_1220() { checkNotSubtype("X<(null,null|X)>","null"); }
	@Test @Ignore public void test_1221() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_1222() { checkNotSubtype("X<(null,X|null)>","{null f1}"); }
	@Test @Ignore public void test_1223() { checkNotSubtype("X<(null,X|null)>","{null f2}"); }
	@Test @Ignore public void test_1224() { checkIsSubtype("X<(null,X|null)>","(null,null)"); }
	@Test @Ignore public void test_1225() { checkIsSubtype("X<(null,X|null)>","(null,null)"); }
	@Test @Ignore public void test_1226() { checkNotSubtype("X<(null,X|null)>","(null,{null f1})"); }
	@Test @Ignore public void test_1227() { checkNotSubtype("X<(null,X|null)>","(null,{null f2})"); }
	@Test @Ignore public void test_1228() { checkNotSubtype("X<(null,X|null)>","({null f1},null)"); }
	@Test @Ignore public void test_1229() { checkNotSubtype("X<(null,X|null)>","({null f2},null)"); }
	@Test @Ignore public void test_1230() { checkIsSubtype("X<(null,X|null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1231() { checkIsSubtype("X<(null,X|null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1232() { checkNotSubtype("X<(null,X|null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1233() { checkNotSubtype("X<(null,X|null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1234() { checkNotSubtype("X<(null,X|null)>","({null f1},null)"); }
	@Test @Ignore public void test_1235() { checkNotSubtype("X<(null,X|null)>","({null f2},null)"); }
	@Test @Ignore public void test_1236() { checkNotSubtype("X<(null,X|null)>","(null,{null f1})"); }
	@Test @Ignore public void test_1237() { checkNotSubtype("X<(null,X|null)>","(null,{null f2})"); }
	@Test @Ignore public void test_1238() { checkNotSubtype("X<(null,X|null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1239() { checkNotSubtype("X<(null,X|null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1240() { checkIsSubtype("X<(null,X|null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1241() { checkIsSubtype("X<(null,X|null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1242() { checkNotSubtype("X<(null,X|null)>","{{null f1} f1}"); }
	@Test @Ignore public void test_1243() { checkNotSubtype("X<(null,X|null)>","{{null f2} f1}"); }
	@Test @Ignore public void test_1244() { checkNotSubtype("X<(null,X|null)>","{{null f1} f2}"); }
	@Test @Ignore public void test_1245() { checkNotSubtype("X<(null,X|null)>","{{null f2} f2}"); }
	@Test @Ignore public void test_1246() { checkNotSubtype("X<(null,X|null)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1247() { checkNotSubtype("X<(null,X|null)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1248() { checkNotSubtype("X<(null,X|null)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1249() { checkNotSubtype("X<(null,X|null)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1250() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_1251() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_1252() { checkNotSubtype("X<(null,X|null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1253() { checkNotSubtype("X<(null,X|null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1254() { checkNotSubtype("X<(null,X|null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1255() { checkNotSubtype("X<(null,X|null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1256() { checkNotSubtype("X<(null,X|null)>","null|{null f1}"); }
	@Test @Ignore public void test_1257() { checkNotSubtype("X<(null,X|null)>","null|{null f2}"); }
	@Test @Ignore public void test_1258() { checkNotSubtype("X<(null,X|null)>","{null f1}|null"); }
	@Test @Ignore public void test_1259() { checkNotSubtype("X<(null,X|null)>","{null f2}|null"); }
	@Test @Ignore public void test_1260() { checkNotSubtype("X<(null,X|null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1261() { checkNotSubtype("X<(null,X|null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1262() { checkNotSubtype("X<(null,X|null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1263() { checkNotSubtype("X<(null,X|null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1264() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_1265() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_1266() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_1267() { checkNotSubtype("X<(null,X|null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1268() { checkNotSubtype("X<(null,X|null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1269() { checkNotSubtype("X<(null,X|null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1270() { checkNotSubtype("X<(null,X|null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1271() { checkNotSubtype("X<(null,X|null)>","{null f1}|null"); }
	@Test @Ignore public void test_1272() { checkNotSubtype("X<(null,X|null)>","{null f2}|null"); }
	@Test @Ignore public void test_1273() { checkNotSubtype("X<(null,X|null)>","null|{null f1}"); }
	@Test @Ignore public void test_1274() { checkNotSubtype("X<(null,X|null)>","null|{null f2}"); }
	@Test @Ignore public void test_1275() { checkNotSubtype("X<(null,X|null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1276() { checkNotSubtype("X<(null,X|null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1277() { checkNotSubtype("X<(null,X|null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1278() { checkNotSubtype("X<(null,X|null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1279() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_1280() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_1281() { checkNotSubtype("X<(null,X|null)>","null"); }
	@Test @Ignore public void test_1282() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test @Ignore public void test_1283() { checkNotSubtype("{{null f1} f1}","{null f1}"); }
	@Test @Ignore public void test_1284() { checkNotSubtype("{{null f1} f1}","{null f2}"); }
	@Test @Ignore public void test_1285() { checkNotSubtype("{{null f1} f1}","(null,null)"); }
	@Test @Ignore public void test_1286() { checkNotSubtype("{{null f1} f1}","(null,null)"); }
	@Test @Ignore public void test_1287() { checkNotSubtype("{{null f1} f1}","(null,{null f1})"); }
	@Test @Ignore public void test_1288() { checkNotSubtype("{{null f1} f1}","(null,{null f2})"); }
	@Test @Ignore public void test_1289() { checkNotSubtype("{{null f1} f1}","({null f1},null)"); }
	@Test @Ignore public void test_1290() { checkNotSubtype("{{null f1} f1}","({null f2},null)"); }
	@Test @Ignore public void test_1291() { checkNotSubtype("{{null f1} f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_1292() { checkNotSubtype("{{null f1} f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_1293() { checkNotSubtype("{{null f1} f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_1294() { checkNotSubtype("{{null f1} f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_1295() { checkNotSubtype("{{null f1} f1}","({null f1},null)"); }
	@Test @Ignore public void test_1296() { checkNotSubtype("{{null f1} f1}","({null f2},null)"); }
	@Test @Ignore public void test_1297() { checkNotSubtype("{{null f1} f1}","(null,{null f1})"); }
	@Test @Ignore public void test_1298() { checkNotSubtype("{{null f1} f1}","(null,{null f2})"); }
	@Test @Ignore public void test_1299() { checkNotSubtype("{{null f1} f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_1300() { checkNotSubtype("{{null f1} f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_1301() { checkNotSubtype("{{null f1} f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_1302() { checkNotSubtype("{{null f1} f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_1303() { checkIsSubtype("{{null f1} f1}","{{null f1} f1}"); }
	@Test @Ignore public void test_1304() { checkNotSubtype("{{null f1} f1}","{{null f2} f1}"); }
	@Test @Ignore public void test_1305() { checkNotSubtype("{{null f1} f1}","{{null f1} f2}"); }
	@Test @Ignore public void test_1306() { checkNotSubtype("{{null f1} f1}","{{null f2} f2}"); }
	@Test @Ignore public void test_1307() { checkNotSubtype("{{null f1} f1}","X<{X|null f1}>"); }
	@Test @Ignore public void test_1308() { checkNotSubtype("{{null f1} f1}","X<{null|X f1}>"); }
	@Test @Ignore public void test_1309() { checkNotSubtype("{{null f1} f1}","X<{null|X f2}>"); }
	@Test @Ignore public void test_1310() { checkNotSubtype("{{null f1} f1}","X<{X|null f2}>"); }
	@Test @Ignore public void test_1311() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test @Ignore public void test_1312() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test @Ignore public void test_1313() { checkNotSubtype("{{null f1} f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_1314() { checkNotSubtype("{{null f1} f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_1315() { checkNotSubtype("{{null f1} f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_1316() { checkNotSubtype("{{null f1} f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_1317() { checkNotSubtype("{{null f1} f1}","null|{null f1}"); }
	@Test @Ignore public void test_1318() { checkNotSubtype("{{null f1} f1}","null|{null f2}"); }
	@Test @Ignore public void test_1319() { checkNotSubtype("{{null f1} f1}","{null f1}|null"); }
	@Test @Ignore public void test_1320() { checkNotSubtype("{{null f1} f1}","{null f2}|null"); }
	@Test @Ignore public void test_1321() { checkNotSubtype("{{null f1} f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_1322() { checkNotSubtype("{{null f1} f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_1323() { checkNotSubtype("{{null f1} f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_1324() { checkNotSubtype("{{null f1} f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_1325() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test @Ignore public void test_1326() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test @Ignore public void test_1327() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test @Ignore public void test_1328() { checkNotSubtype("{{null f1} f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_1329() { checkNotSubtype("{{null f1} f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_1330() { checkNotSubtype("{{null f1} f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_1331() { checkNotSubtype("{{null f1} f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_1332() { checkNotSubtype("{{null f1} f1}","{null f1}|null"); }
	@Test @Ignore public void test_1333() { checkNotSubtype("{{null f1} f1}","{null f2}|null"); }
	@Test @Ignore public void test_1334() { checkNotSubtype("{{null f1} f1}","null|{null f1}"); }
	@Test @Ignore public void test_1335() { checkNotSubtype("{{null f1} f1}","null|{null f2}"); }
	@Test @Ignore public void test_1336() { checkNotSubtype("{{null f1} f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_1337() { checkNotSubtype("{{null f1} f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_1338() { checkNotSubtype("{{null f1} f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_1339() { checkNotSubtype("{{null f1} f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_1340() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test @Ignore public void test_1341() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test @Ignore public void test_1342() { checkNotSubtype("{{null f1} f1}","null"); }
	@Test @Ignore public void test_1343() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test @Ignore public void test_1344() { checkNotSubtype("{{null f2} f1}","{null f1}"); }
	@Test @Ignore public void test_1345() { checkNotSubtype("{{null f2} f1}","{null f2}"); }
	@Test @Ignore public void test_1346() { checkNotSubtype("{{null f2} f1}","(null,null)"); }
	@Test @Ignore public void test_1347() { checkNotSubtype("{{null f2} f1}","(null,null)"); }
	@Test @Ignore public void test_1348() { checkNotSubtype("{{null f2} f1}","(null,{null f1})"); }
	@Test @Ignore public void test_1349() { checkNotSubtype("{{null f2} f1}","(null,{null f2})"); }
	@Test @Ignore public void test_1350() { checkNotSubtype("{{null f2} f1}","({null f1},null)"); }
	@Test @Ignore public void test_1351() { checkNotSubtype("{{null f2} f1}","({null f2},null)"); }
	@Test @Ignore public void test_1352() { checkNotSubtype("{{null f2} f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_1353() { checkNotSubtype("{{null f2} f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_1354() { checkNotSubtype("{{null f2} f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_1355() { checkNotSubtype("{{null f2} f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_1356() { checkNotSubtype("{{null f2} f1}","({null f1},null)"); }
	@Test @Ignore public void test_1357() { checkNotSubtype("{{null f2} f1}","({null f2},null)"); }
	@Test @Ignore public void test_1358() { checkNotSubtype("{{null f2} f1}","(null,{null f1})"); }
	@Test @Ignore public void test_1359() { checkNotSubtype("{{null f2} f1}","(null,{null f2})"); }
	@Test @Ignore public void test_1360() { checkNotSubtype("{{null f2} f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_1361() { checkNotSubtype("{{null f2} f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_1362() { checkNotSubtype("{{null f2} f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_1363() { checkNotSubtype("{{null f2} f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_1364() { checkNotSubtype("{{null f2} f1}","{{null f1} f1}"); }
	@Test @Ignore public void test_1365() { checkIsSubtype("{{null f2} f1}","{{null f2} f1}"); }
	@Test @Ignore public void test_1366() { checkNotSubtype("{{null f2} f1}","{{null f1} f2}"); }
	@Test @Ignore public void test_1367() { checkNotSubtype("{{null f2} f1}","{{null f2} f2}"); }
	@Test @Ignore public void test_1368() { checkNotSubtype("{{null f2} f1}","X<{X|null f1}>"); }
	@Test @Ignore public void test_1369() { checkNotSubtype("{{null f2} f1}","X<{null|X f1}>"); }
	@Test @Ignore public void test_1370() { checkNotSubtype("{{null f2} f1}","X<{null|X f2}>"); }
	@Test @Ignore public void test_1371() { checkNotSubtype("{{null f2} f1}","X<{X|null f2}>"); }
	@Test @Ignore public void test_1372() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test @Ignore public void test_1373() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test @Ignore public void test_1374() { checkNotSubtype("{{null f2} f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_1375() { checkNotSubtype("{{null f2} f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_1376() { checkNotSubtype("{{null f2} f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_1377() { checkNotSubtype("{{null f2} f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_1378() { checkNotSubtype("{{null f2} f1}","null|{null f1}"); }
	@Test @Ignore public void test_1379() { checkNotSubtype("{{null f2} f1}","null|{null f2}"); }
	@Test @Ignore public void test_1380() { checkNotSubtype("{{null f2} f1}","{null f1}|null"); }
	@Test @Ignore public void test_1381() { checkNotSubtype("{{null f2} f1}","{null f2}|null"); }
	@Test @Ignore public void test_1382() { checkNotSubtype("{{null f2} f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_1383() { checkNotSubtype("{{null f2} f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_1384() { checkNotSubtype("{{null f2} f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_1385() { checkNotSubtype("{{null f2} f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_1386() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test @Ignore public void test_1387() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test @Ignore public void test_1388() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test @Ignore public void test_1389() { checkNotSubtype("{{null f2} f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_1390() { checkNotSubtype("{{null f2} f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_1391() { checkNotSubtype("{{null f2} f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_1392() { checkNotSubtype("{{null f2} f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_1393() { checkNotSubtype("{{null f2} f1}","{null f1}|null"); }
	@Test @Ignore public void test_1394() { checkNotSubtype("{{null f2} f1}","{null f2}|null"); }
	@Test @Ignore public void test_1395() { checkNotSubtype("{{null f2} f1}","null|{null f1}"); }
	@Test @Ignore public void test_1396() { checkNotSubtype("{{null f2} f1}","null|{null f2}"); }
	@Test @Ignore public void test_1397() { checkNotSubtype("{{null f2} f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_1398() { checkNotSubtype("{{null f2} f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_1399() { checkNotSubtype("{{null f2} f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_1400() { checkNotSubtype("{{null f2} f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_1401() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test @Ignore public void test_1402() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test @Ignore public void test_1403() { checkNotSubtype("{{null f2} f1}","null"); }
	@Test @Ignore public void test_1404() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test @Ignore public void test_1405() { checkNotSubtype("{{null f1} f2}","{null f1}"); }
	@Test @Ignore public void test_1406() { checkNotSubtype("{{null f1} f2}","{null f2}"); }
	@Test @Ignore public void test_1407() { checkNotSubtype("{{null f1} f2}","(null,null)"); }
	@Test @Ignore public void test_1408() { checkNotSubtype("{{null f1} f2}","(null,null)"); }
	@Test @Ignore public void test_1409() { checkNotSubtype("{{null f1} f2}","(null,{null f1})"); }
	@Test @Ignore public void test_1410() { checkNotSubtype("{{null f1} f2}","(null,{null f2})"); }
	@Test @Ignore public void test_1411() { checkNotSubtype("{{null f1} f2}","({null f1},null)"); }
	@Test @Ignore public void test_1412() { checkNotSubtype("{{null f1} f2}","({null f2},null)"); }
	@Test @Ignore public void test_1413() { checkNotSubtype("{{null f1} f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_1414() { checkNotSubtype("{{null f1} f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_1415() { checkNotSubtype("{{null f1} f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_1416() { checkNotSubtype("{{null f1} f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_1417() { checkNotSubtype("{{null f1} f2}","({null f1},null)"); }
	@Test @Ignore public void test_1418() { checkNotSubtype("{{null f1} f2}","({null f2},null)"); }
	@Test @Ignore public void test_1419() { checkNotSubtype("{{null f1} f2}","(null,{null f1})"); }
	@Test @Ignore public void test_1420() { checkNotSubtype("{{null f1} f2}","(null,{null f2})"); }
	@Test @Ignore public void test_1421() { checkNotSubtype("{{null f1} f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_1422() { checkNotSubtype("{{null f1} f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_1423() { checkNotSubtype("{{null f1} f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_1424() { checkNotSubtype("{{null f1} f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_1425() { checkNotSubtype("{{null f1} f2}","{{null f1} f1}"); }
	@Test @Ignore public void test_1426() { checkNotSubtype("{{null f1} f2}","{{null f2} f1}"); }
	@Test @Ignore public void test_1427() { checkIsSubtype("{{null f1} f2}","{{null f1} f2}"); }
	@Test @Ignore public void test_1428() { checkNotSubtype("{{null f1} f2}","{{null f2} f2}"); }
	@Test @Ignore public void test_1429() { checkNotSubtype("{{null f1} f2}","X<{X|null f1}>"); }
	@Test @Ignore public void test_1430() { checkNotSubtype("{{null f1} f2}","X<{null|X f1}>"); }
	@Test @Ignore public void test_1431() { checkNotSubtype("{{null f1} f2}","X<{null|X f2}>"); }
	@Test @Ignore public void test_1432() { checkNotSubtype("{{null f1} f2}","X<{X|null f2}>"); }
	@Test @Ignore public void test_1433() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test @Ignore public void test_1434() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test @Ignore public void test_1435() { checkNotSubtype("{{null f1} f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_1436() { checkNotSubtype("{{null f1} f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_1437() { checkNotSubtype("{{null f1} f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_1438() { checkNotSubtype("{{null f1} f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_1439() { checkNotSubtype("{{null f1} f2}","null|{null f1}"); }
	@Test @Ignore public void test_1440() { checkNotSubtype("{{null f1} f2}","null|{null f2}"); }
	@Test @Ignore public void test_1441() { checkNotSubtype("{{null f1} f2}","{null f1}|null"); }
	@Test @Ignore public void test_1442() { checkNotSubtype("{{null f1} f2}","{null f2}|null"); }
	@Test @Ignore public void test_1443() { checkNotSubtype("{{null f1} f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_1444() { checkNotSubtype("{{null f1} f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_1445() { checkNotSubtype("{{null f1} f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_1446() { checkNotSubtype("{{null f1} f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_1447() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test @Ignore public void test_1448() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test @Ignore public void test_1449() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test @Ignore public void test_1450() { checkNotSubtype("{{null f1} f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_1451() { checkNotSubtype("{{null f1} f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_1452() { checkNotSubtype("{{null f1} f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_1453() { checkNotSubtype("{{null f1} f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_1454() { checkNotSubtype("{{null f1} f2}","{null f1}|null"); }
	@Test @Ignore public void test_1455() { checkNotSubtype("{{null f1} f2}","{null f2}|null"); }
	@Test @Ignore public void test_1456() { checkNotSubtype("{{null f1} f2}","null|{null f1}"); }
	@Test @Ignore public void test_1457() { checkNotSubtype("{{null f1} f2}","null|{null f2}"); }
	@Test @Ignore public void test_1458() { checkNotSubtype("{{null f1} f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_1459() { checkNotSubtype("{{null f1} f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_1460() { checkNotSubtype("{{null f1} f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_1461() { checkNotSubtype("{{null f1} f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_1462() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test @Ignore public void test_1463() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test @Ignore public void test_1464() { checkNotSubtype("{{null f1} f2}","null"); }
	@Test @Ignore public void test_1465() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test @Ignore public void test_1466() { checkNotSubtype("{{null f2} f2}","{null f1}"); }
	@Test @Ignore public void test_1467() { checkNotSubtype("{{null f2} f2}","{null f2}"); }
	@Test @Ignore public void test_1468() { checkNotSubtype("{{null f2} f2}","(null,null)"); }
	@Test @Ignore public void test_1469() { checkNotSubtype("{{null f2} f2}","(null,null)"); }
	@Test @Ignore public void test_1470() { checkNotSubtype("{{null f2} f2}","(null,{null f1})"); }
	@Test @Ignore public void test_1471() { checkNotSubtype("{{null f2} f2}","(null,{null f2})"); }
	@Test @Ignore public void test_1472() { checkNotSubtype("{{null f2} f2}","({null f1},null)"); }
	@Test @Ignore public void test_1473() { checkNotSubtype("{{null f2} f2}","({null f2},null)"); }
	@Test @Ignore public void test_1474() { checkNotSubtype("{{null f2} f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_1475() { checkNotSubtype("{{null f2} f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_1476() { checkNotSubtype("{{null f2} f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_1477() { checkNotSubtype("{{null f2} f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_1478() { checkNotSubtype("{{null f2} f2}","({null f1},null)"); }
	@Test @Ignore public void test_1479() { checkNotSubtype("{{null f2} f2}","({null f2},null)"); }
	@Test @Ignore public void test_1480() { checkNotSubtype("{{null f2} f2}","(null,{null f1})"); }
	@Test @Ignore public void test_1481() { checkNotSubtype("{{null f2} f2}","(null,{null f2})"); }
	@Test @Ignore public void test_1482() { checkNotSubtype("{{null f2} f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_1483() { checkNotSubtype("{{null f2} f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_1484() { checkNotSubtype("{{null f2} f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_1485() { checkNotSubtype("{{null f2} f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_1486() { checkNotSubtype("{{null f2} f2}","{{null f1} f1}"); }
	@Test @Ignore public void test_1487() { checkNotSubtype("{{null f2} f2}","{{null f2} f1}"); }
	@Test @Ignore public void test_1488() { checkNotSubtype("{{null f2} f2}","{{null f1} f2}"); }
	@Test @Ignore public void test_1489() { checkIsSubtype("{{null f2} f2}","{{null f2} f2}"); }
	@Test @Ignore public void test_1490() { checkNotSubtype("{{null f2} f2}","X<{X|null f1}>"); }
	@Test @Ignore public void test_1491() { checkNotSubtype("{{null f2} f2}","X<{null|X f1}>"); }
	@Test @Ignore public void test_1492() { checkNotSubtype("{{null f2} f2}","X<{null|X f2}>"); }
	@Test @Ignore public void test_1493() { checkNotSubtype("{{null f2} f2}","X<{X|null f2}>"); }
	@Test @Ignore public void test_1494() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test @Ignore public void test_1495() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test @Ignore public void test_1496() { checkNotSubtype("{{null f2} f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_1497() { checkNotSubtype("{{null f2} f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_1498() { checkNotSubtype("{{null f2} f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_1499() { checkNotSubtype("{{null f2} f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_1500() { checkNotSubtype("{{null f2} f2}","null|{null f1}"); }
	@Test @Ignore public void test_1501() { checkNotSubtype("{{null f2} f2}","null|{null f2}"); }
	@Test @Ignore public void test_1502() { checkNotSubtype("{{null f2} f2}","{null f1}|null"); }
	@Test @Ignore public void test_1503() { checkNotSubtype("{{null f2} f2}","{null f2}|null"); }
	@Test @Ignore public void test_1504() { checkNotSubtype("{{null f2} f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_1505() { checkNotSubtype("{{null f2} f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_1506() { checkNotSubtype("{{null f2} f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_1507() { checkNotSubtype("{{null f2} f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_1508() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test @Ignore public void test_1509() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test @Ignore public void test_1510() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test @Ignore public void test_1511() { checkNotSubtype("{{null f2} f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_1512() { checkNotSubtype("{{null f2} f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_1513() { checkNotSubtype("{{null f2} f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_1514() { checkNotSubtype("{{null f2} f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_1515() { checkNotSubtype("{{null f2} f2}","{null f1}|null"); }
	@Test @Ignore public void test_1516() { checkNotSubtype("{{null f2} f2}","{null f2}|null"); }
	@Test @Ignore public void test_1517() { checkNotSubtype("{{null f2} f2}","null|{null f1}"); }
	@Test @Ignore public void test_1518() { checkNotSubtype("{{null f2} f2}","null|{null f2}"); }
	@Test @Ignore public void test_1519() { checkNotSubtype("{{null f2} f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_1520() { checkNotSubtype("{{null f2} f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_1521() { checkNotSubtype("{{null f2} f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_1522() { checkNotSubtype("{{null f2} f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_1523() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test @Ignore public void test_1524() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test @Ignore public void test_1525() { checkNotSubtype("{{null f2} f2}","null"); }
	@Test @Ignore public void test_1526() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test @Ignore public void test_1527() { checkIsSubtype("X<{X|null f1}>","{null f1}"); }
	@Test @Ignore public void test_1528() { checkNotSubtype("X<{X|null f1}>","{null f2}"); }
	@Test @Ignore public void test_1529() { checkNotSubtype("X<{X|null f1}>","(null,null)"); }
	@Test @Ignore public void test_1530() { checkNotSubtype("X<{X|null f1}>","(null,null)"); }
	@Test @Ignore public void test_1531() { checkNotSubtype("X<{X|null f1}>","(null,{null f1})"); }
	@Test @Ignore public void test_1532() { checkNotSubtype("X<{X|null f1}>","(null,{null f2})"); }
	@Test @Ignore public void test_1533() { checkNotSubtype("X<{X|null f1}>","({null f1},null)"); }
	@Test @Ignore public void test_1534() { checkNotSubtype("X<{X|null f1}>","({null f2},null)"); }
	@Test @Ignore public void test_1535() { checkNotSubtype("X<{X|null f1}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1536() { checkNotSubtype("X<{X|null f1}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1537() { checkNotSubtype("X<{X|null f1}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1538() { checkNotSubtype("X<{X|null f1}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1539() { checkNotSubtype("X<{X|null f1}>","({null f1},null)"); }
	@Test @Ignore public void test_1540() { checkNotSubtype("X<{X|null f1}>","({null f2},null)"); }
	@Test @Ignore public void test_1541() { checkNotSubtype("X<{X|null f1}>","(null,{null f1})"); }
	@Test @Ignore public void test_1542() { checkNotSubtype("X<{X|null f1}>","(null,{null f2})"); }
	@Test @Ignore public void test_1543() { checkNotSubtype("X<{X|null f1}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1544() { checkNotSubtype("X<{X|null f1}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1545() { checkNotSubtype("X<{X|null f1}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1546() { checkNotSubtype("X<{X|null f1}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1547() { checkIsSubtype("X<{X|null f1}>","{{null f1} f1}"); }
	@Test @Ignore public void test_1548() { checkNotSubtype("X<{X|null f1}>","{{null f2} f1}"); }
	@Test @Ignore public void test_1549() { checkNotSubtype("X<{X|null f1}>","{{null f1} f2}"); }
	@Test @Ignore public void test_1550() { checkNotSubtype("X<{X|null f1}>","{{null f2} f2}"); }
	@Test @Ignore public void test_1551() { checkIsSubtype("X<{X|null f1}>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1552() { checkIsSubtype("X<{X|null f1}>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1553() { checkNotSubtype("X<{X|null f1}>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1554() { checkNotSubtype("X<{X|null f1}>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1555() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test @Ignore public void test_1556() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test @Ignore public void test_1557() { checkNotSubtype("X<{X|null f1}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1558() { checkNotSubtype("X<{X|null f1}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1559() { checkNotSubtype("X<{X|null f1}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1560() { checkNotSubtype("X<{X|null f1}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1561() { checkNotSubtype("X<{X|null f1}>","null|{null f1}"); }
	@Test @Ignore public void test_1562() { checkNotSubtype("X<{X|null f1}>","null|{null f2}"); }
	@Test @Ignore public void test_1563() { checkNotSubtype("X<{X|null f1}>","{null f1}|null"); }
	@Test @Ignore public void test_1564() { checkNotSubtype("X<{X|null f1}>","{null f2}|null"); }
	@Test @Ignore public void test_1565() { checkNotSubtype("X<{X|null f1}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1566() { checkNotSubtype("X<{X|null f1}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1567() { checkNotSubtype("X<{X|null f1}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1568() { checkNotSubtype("X<{X|null f1}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1569() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test @Ignore public void test_1570() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test @Ignore public void test_1571() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test @Ignore public void test_1572() { checkNotSubtype("X<{X|null f1}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1573() { checkNotSubtype("X<{X|null f1}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1574() { checkNotSubtype("X<{X|null f1}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1575() { checkNotSubtype("X<{X|null f1}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1576() { checkNotSubtype("X<{X|null f1}>","{null f1}|null"); }
	@Test @Ignore public void test_1577() { checkNotSubtype("X<{X|null f1}>","{null f2}|null"); }
	@Test @Ignore public void test_1578() { checkNotSubtype("X<{X|null f1}>","null|{null f1}"); }
	@Test @Ignore public void test_1579() { checkNotSubtype("X<{X|null f1}>","null|{null f2}"); }
	@Test @Ignore public void test_1580() { checkNotSubtype("X<{X|null f1}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1581() { checkNotSubtype("X<{X|null f1}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1582() { checkNotSubtype("X<{X|null f1}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1583() { checkNotSubtype("X<{X|null f1}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1584() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test @Ignore public void test_1585() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test @Ignore public void test_1586() { checkNotSubtype("X<{X|null f1}>","null"); }
	@Test @Ignore public void test_1587() { checkNotSubtype("X<{null|X f1}>","null"); }
	@Test @Ignore public void test_1588() { checkIsSubtype("X<{null|X f1}>","{null f1}"); }
	@Test @Ignore public void test_1589() { checkNotSubtype("X<{null|X f1}>","{null f2}"); }
	@Test @Ignore public void test_1590() { checkNotSubtype("X<{null|X f1}>","(null,null)"); }
	@Test @Ignore public void test_1591() { checkNotSubtype("X<{null|X f1}>","(null,null)"); }
	@Test @Ignore public void test_1592() { checkNotSubtype("X<{null|X f1}>","(null,{null f1})"); }
	@Test @Ignore public void test_1593() { checkNotSubtype("X<{null|X f1}>","(null,{null f2})"); }
	@Test @Ignore public void test_1594() { checkNotSubtype("X<{null|X f1}>","({null f1},null)"); }
	@Test @Ignore public void test_1595() { checkNotSubtype("X<{null|X f1}>","({null f2},null)"); }
	@Test @Ignore public void test_1596() { checkNotSubtype("X<{null|X f1}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1597() { checkNotSubtype("X<{null|X f1}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1598() { checkNotSubtype("X<{null|X f1}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1599() { checkNotSubtype("X<{null|X f1}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1600() { checkNotSubtype("X<{null|X f1}>","({null f1},null)"); }
	@Test @Ignore public void test_1601() { checkNotSubtype("X<{null|X f1}>","({null f2},null)"); }
	@Test @Ignore public void test_1602() { checkNotSubtype("X<{null|X f1}>","(null,{null f1})"); }
	@Test @Ignore public void test_1603() { checkNotSubtype("X<{null|X f1}>","(null,{null f2})"); }
	@Test @Ignore public void test_1604() { checkNotSubtype("X<{null|X f1}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1605() { checkNotSubtype("X<{null|X f1}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1606() { checkNotSubtype("X<{null|X f1}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1607() { checkNotSubtype("X<{null|X f1}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1608() { checkIsSubtype("X<{null|X f1}>","{{null f1} f1}"); }
	@Test @Ignore public void test_1609() { checkNotSubtype("X<{null|X f1}>","{{null f2} f1}"); }
	@Test @Ignore public void test_1610() { checkNotSubtype("X<{null|X f1}>","{{null f1} f2}"); }
	@Test @Ignore public void test_1611() { checkNotSubtype("X<{null|X f1}>","{{null f2} f2}"); }
	@Test @Ignore public void test_1612() { checkIsSubtype("X<{null|X f1}>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1613() { checkIsSubtype("X<{null|X f1}>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1614() { checkNotSubtype("X<{null|X f1}>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1615() { checkNotSubtype("X<{null|X f1}>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1616() { checkNotSubtype("X<{null|X f1}>","null"); }
	@Test @Ignore public void test_1617() { checkNotSubtype("X<{null|X f1}>","null"); }
	@Test @Ignore public void test_1618() { checkNotSubtype("X<{null|X f1}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1619() { checkNotSubtype("X<{null|X f1}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1620() { checkNotSubtype("X<{null|X f1}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1621() { checkNotSubtype("X<{null|X f1}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1622() { checkNotSubtype("X<{null|X f1}>","null|{null f1}"); }
	@Test @Ignore public void test_1623() { checkNotSubtype("X<{null|X f1}>","null|{null f2}"); }
	@Test @Ignore public void test_1624() { checkNotSubtype("X<{null|X f1}>","{null f1}|null"); }
	@Test @Ignore public void test_1625() { checkNotSubtype("X<{null|X f1}>","{null f2}|null"); }
	@Test @Ignore public void test_1626() { checkNotSubtype("X<{null|X f1}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1627() { checkNotSubtype("X<{null|X f1}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1628() { checkNotSubtype("X<{null|X f1}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1629() { checkNotSubtype("X<{null|X f1}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1630() { checkNotSubtype("X<{null|X f1}>","null"); }
	@Test @Ignore public void test_1631() { checkNotSubtype("X<{null|X f1}>","null"); }
	@Test @Ignore public void test_1632() { checkNotSubtype("X<{null|X f1}>","null"); }
	@Test @Ignore public void test_1633() { checkNotSubtype("X<{null|X f1}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1634() { checkNotSubtype("X<{null|X f1}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1635() { checkNotSubtype("X<{null|X f1}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1636() { checkNotSubtype("X<{null|X f1}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1637() { checkNotSubtype("X<{null|X f1}>","{null f1}|null"); }
	@Test @Ignore public void test_1638() { checkNotSubtype("X<{null|X f1}>","{null f2}|null"); }
	@Test @Ignore public void test_1639() { checkNotSubtype("X<{null|X f1}>","null|{null f1}"); }
	@Test @Ignore public void test_1640() { checkNotSubtype("X<{null|X f1}>","null|{null f2}"); }
	@Test @Ignore public void test_1641() { checkNotSubtype("X<{null|X f1}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1642() { checkNotSubtype("X<{null|X f1}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1643() { checkNotSubtype("X<{null|X f1}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1644() { checkNotSubtype("X<{null|X f1}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1645() { checkNotSubtype("X<{null|X f1}>","null"); }
	@Test @Ignore public void test_1646() { checkNotSubtype("X<{null|X f1}>","null"); }
	@Test @Ignore public void test_1647() { checkNotSubtype("X<{null|X f1}>","null"); }
	@Test @Ignore public void test_1648() { checkNotSubtype("X<{null|X f2}>","null"); }
	@Test @Ignore public void test_1649() { checkNotSubtype("X<{null|X f2}>","{null f1}"); }
	@Test @Ignore public void test_1650() { checkIsSubtype("X<{null|X f2}>","{null f2}"); }
	@Test @Ignore public void test_1651() { checkNotSubtype("X<{null|X f2}>","(null,null)"); }
	@Test @Ignore public void test_1652() { checkNotSubtype("X<{null|X f2}>","(null,null)"); }
	@Test @Ignore public void test_1653() { checkNotSubtype("X<{null|X f2}>","(null,{null f1})"); }
	@Test @Ignore public void test_1654() { checkNotSubtype("X<{null|X f2}>","(null,{null f2})"); }
	@Test @Ignore public void test_1655() { checkNotSubtype("X<{null|X f2}>","({null f1},null)"); }
	@Test @Ignore public void test_1656() { checkNotSubtype("X<{null|X f2}>","({null f2},null)"); }
	@Test @Ignore public void test_1657() { checkNotSubtype("X<{null|X f2}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1658() { checkNotSubtype("X<{null|X f2}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1659() { checkNotSubtype("X<{null|X f2}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1660() { checkNotSubtype("X<{null|X f2}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1661() { checkNotSubtype("X<{null|X f2}>","({null f1},null)"); }
	@Test @Ignore public void test_1662() { checkNotSubtype("X<{null|X f2}>","({null f2},null)"); }
	@Test @Ignore public void test_1663() { checkNotSubtype("X<{null|X f2}>","(null,{null f1})"); }
	@Test @Ignore public void test_1664() { checkNotSubtype("X<{null|X f2}>","(null,{null f2})"); }
	@Test @Ignore public void test_1665() { checkNotSubtype("X<{null|X f2}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1666() { checkNotSubtype("X<{null|X f2}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1667() { checkNotSubtype("X<{null|X f2}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1668() { checkNotSubtype("X<{null|X f2}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1669() { checkNotSubtype("X<{null|X f2}>","{{null f1} f1}"); }
	@Test @Ignore public void test_1670() { checkNotSubtype("X<{null|X f2}>","{{null f2} f1}"); }
	@Test @Ignore public void test_1671() { checkNotSubtype("X<{null|X f2}>","{{null f1} f2}"); }
	@Test @Ignore public void test_1672() { checkIsSubtype("X<{null|X f2}>","{{null f2} f2}"); }
	@Test @Ignore public void test_1673() { checkNotSubtype("X<{null|X f2}>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1674() { checkNotSubtype("X<{null|X f2}>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1675() { checkIsSubtype("X<{null|X f2}>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1676() { checkIsSubtype("X<{null|X f2}>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1677() { checkNotSubtype("X<{null|X f2}>","null"); }
	@Test @Ignore public void test_1678() { checkNotSubtype("X<{null|X f2}>","null"); }
	@Test @Ignore public void test_1679() { checkNotSubtype("X<{null|X f2}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1680() { checkNotSubtype("X<{null|X f2}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1681() { checkNotSubtype("X<{null|X f2}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1682() { checkNotSubtype("X<{null|X f2}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1683() { checkNotSubtype("X<{null|X f2}>","null|{null f1}"); }
	@Test @Ignore public void test_1684() { checkNotSubtype("X<{null|X f2}>","null|{null f2}"); }
	@Test @Ignore public void test_1685() { checkNotSubtype("X<{null|X f2}>","{null f1}|null"); }
	@Test @Ignore public void test_1686() { checkNotSubtype("X<{null|X f2}>","{null f2}|null"); }
	@Test @Ignore public void test_1687() { checkNotSubtype("X<{null|X f2}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1688() { checkNotSubtype("X<{null|X f2}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1689() { checkNotSubtype("X<{null|X f2}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1690() { checkNotSubtype("X<{null|X f2}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1691() { checkNotSubtype("X<{null|X f2}>","null"); }
	@Test @Ignore public void test_1692() { checkNotSubtype("X<{null|X f2}>","null"); }
	@Test @Ignore public void test_1693() { checkNotSubtype("X<{null|X f2}>","null"); }
	@Test @Ignore public void test_1694() { checkNotSubtype("X<{null|X f2}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1695() { checkNotSubtype("X<{null|X f2}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1696() { checkNotSubtype("X<{null|X f2}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1697() { checkNotSubtype("X<{null|X f2}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1698() { checkNotSubtype("X<{null|X f2}>","{null f1}|null"); }
	@Test @Ignore public void test_1699() { checkNotSubtype("X<{null|X f2}>","{null f2}|null"); }
	@Test @Ignore public void test_1700() { checkNotSubtype("X<{null|X f2}>","null|{null f1}"); }
	@Test @Ignore public void test_1701() { checkNotSubtype("X<{null|X f2}>","null|{null f2}"); }
	@Test @Ignore public void test_1702() { checkNotSubtype("X<{null|X f2}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1703() { checkNotSubtype("X<{null|X f2}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1704() { checkNotSubtype("X<{null|X f2}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1705() { checkNotSubtype("X<{null|X f2}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1706() { checkNotSubtype("X<{null|X f2}>","null"); }
	@Test @Ignore public void test_1707() { checkNotSubtype("X<{null|X f2}>","null"); }
	@Test @Ignore public void test_1708() { checkNotSubtype("X<{null|X f2}>","null"); }
	@Test @Ignore public void test_1709() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test @Ignore public void test_1710() { checkNotSubtype("X<{X|null f2}>","{null f1}"); }
	@Test @Ignore public void test_1711() { checkIsSubtype("X<{X|null f2}>","{null f2}"); }
	@Test @Ignore public void test_1712() { checkNotSubtype("X<{X|null f2}>","(null,null)"); }
	@Test @Ignore public void test_1713() { checkNotSubtype("X<{X|null f2}>","(null,null)"); }
	@Test @Ignore public void test_1714() { checkNotSubtype("X<{X|null f2}>","(null,{null f1})"); }
	@Test @Ignore public void test_1715() { checkNotSubtype("X<{X|null f2}>","(null,{null f2})"); }
	@Test @Ignore public void test_1716() { checkNotSubtype("X<{X|null f2}>","({null f1},null)"); }
	@Test @Ignore public void test_1717() { checkNotSubtype("X<{X|null f2}>","({null f2},null)"); }
	@Test @Ignore public void test_1718() { checkNotSubtype("X<{X|null f2}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1719() { checkNotSubtype("X<{X|null f2}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1720() { checkNotSubtype("X<{X|null f2}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1721() { checkNotSubtype("X<{X|null f2}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1722() { checkNotSubtype("X<{X|null f2}>","({null f1},null)"); }
	@Test @Ignore public void test_1723() { checkNotSubtype("X<{X|null f2}>","({null f2},null)"); }
	@Test @Ignore public void test_1724() { checkNotSubtype("X<{X|null f2}>","(null,{null f1})"); }
	@Test @Ignore public void test_1725() { checkNotSubtype("X<{X|null f2}>","(null,{null f2})"); }
	@Test @Ignore public void test_1726() { checkNotSubtype("X<{X|null f2}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1727() { checkNotSubtype("X<{X|null f2}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1728() { checkNotSubtype("X<{X|null f2}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1729() { checkNotSubtype("X<{X|null f2}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1730() { checkNotSubtype("X<{X|null f2}>","{{null f1} f1}"); }
	@Test @Ignore public void test_1731() { checkNotSubtype("X<{X|null f2}>","{{null f2} f1}"); }
	@Test @Ignore public void test_1732() { checkNotSubtype("X<{X|null f2}>","{{null f1} f2}"); }
	@Test @Ignore public void test_1733() { checkIsSubtype("X<{X|null f2}>","{{null f2} f2}"); }
	@Test @Ignore public void test_1734() { checkNotSubtype("X<{X|null f2}>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1735() { checkNotSubtype("X<{X|null f2}>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1736() { checkIsSubtype("X<{X|null f2}>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1737() { checkIsSubtype("X<{X|null f2}>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1738() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test @Ignore public void test_1739() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test @Ignore public void test_1740() { checkNotSubtype("X<{X|null f2}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1741() { checkNotSubtype("X<{X|null f2}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1742() { checkNotSubtype("X<{X|null f2}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1743() { checkNotSubtype("X<{X|null f2}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1744() { checkNotSubtype("X<{X|null f2}>","null|{null f1}"); }
	@Test @Ignore public void test_1745() { checkNotSubtype("X<{X|null f2}>","null|{null f2}"); }
	@Test @Ignore public void test_1746() { checkNotSubtype("X<{X|null f2}>","{null f1}|null"); }
	@Test @Ignore public void test_1747() { checkNotSubtype("X<{X|null f2}>","{null f2}|null"); }
	@Test @Ignore public void test_1748() { checkNotSubtype("X<{X|null f2}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1749() { checkNotSubtype("X<{X|null f2}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1750() { checkNotSubtype("X<{X|null f2}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1751() { checkNotSubtype("X<{X|null f2}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1752() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test @Ignore public void test_1753() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test @Ignore public void test_1754() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test @Ignore public void test_1755() { checkNotSubtype("X<{X|null f2}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1756() { checkNotSubtype("X<{X|null f2}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1757() { checkNotSubtype("X<{X|null f2}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1758() { checkNotSubtype("X<{X|null f2}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1759() { checkNotSubtype("X<{X|null f2}>","{null f1}|null"); }
	@Test @Ignore public void test_1760() { checkNotSubtype("X<{X|null f2}>","{null f2}|null"); }
	@Test @Ignore public void test_1761() { checkNotSubtype("X<{X|null f2}>","null|{null f1}"); }
	@Test @Ignore public void test_1762() { checkNotSubtype("X<{X|null f2}>","null|{null f2}"); }
	@Test @Ignore public void test_1763() { checkNotSubtype("X<{X|null f2}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1764() { checkNotSubtype("X<{X|null f2}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1765() { checkNotSubtype("X<{X|null f2}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1766() { checkNotSubtype("X<{X|null f2}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1767() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test @Ignore public void test_1768() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test @Ignore public void test_1769() { checkNotSubtype("X<{X|null f2}>","null"); }
	@Test @Ignore public void test_1770() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1771() { checkNotSubtype("null","{null f1}"); }
	@Test @Ignore public void test_1772() { checkNotSubtype("null","{null f2}"); }
	@Test @Ignore public void test_1773() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_1774() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_1775() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_1776() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_1777() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_1778() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_1779() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_1780() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_1781() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_1782() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_1783() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_1784() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_1785() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_1786() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_1787() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_1788() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_1789() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_1790() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_1791() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test @Ignore public void test_1792() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test @Ignore public void test_1793() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test @Ignore public void test_1794() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test @Ignore public void test_1795() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test @Ignore public void test_1796() { checkNotSubtype("null","X<{null|X f1}>"); }
	@Test @Ignore public void test_1797() { checkNotSubtype("null","X<{null|X f2}>"); }
	@Test @Ignore public void test_1798() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test @Ignore public void test_1799() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1800() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1801() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_1802() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_1803() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_1804() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_1805() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_1806() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_1807() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_1808() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_1809() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_1810() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_1811() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_1812() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_1813() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1814() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1815() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1816() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_1817() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_1818() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_1819() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_1820() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_1821() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_1822() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_1823() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_1824() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_1825() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_1826() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_1827() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_1828() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1829() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1830() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1831() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1832() { checkNotSubtype("null","{null f1}"); }
	@Test @Ignore public void test_1833() { checkNotSubtype("null","{null f2}"); }
	@Test @Ignore public void test_1834() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_1835() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_1836() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_1837() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_1838() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_1839() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_1840() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_1841() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_1842() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_1843() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_1844() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_1845() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_1846() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_1847() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_1848() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_1849() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_1850() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_1851() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_1852() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test @Ignore public void test_1853() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test @Ignore public void test_1854() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test @Ignore public void test_1855() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test @Ignore public void test_1856() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test @Ignore public void test_1857() { checkNotSubtype("null","X<{null|X f1}>"); }
	@Test @Ignore public void test_1858() { checkNotSubtype("null","X<{null|X f2}>"); }
	@Test @Ignore public void test_1859() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test @Ignore public void test_1860() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1861() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1862() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_1863() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_1864() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_1865() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_1866() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_1867() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_1868() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_1869() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_1870() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_1871() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_1872() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_1873() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_1874() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1875() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1876() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1877() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_1878() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_1879() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_1880() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_1881() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_1882() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_1883() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_1884() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_1885() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_1886() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_1887() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_1888() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_1889() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1890() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1891() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_1892() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_1893() { checkNotSubtype("X<null|(X,null)>","{null f1}"); }
	@Test @Ignore public void test_1894() { checkNotSubtype("X<null|(X,null)>","{null f2}"); }
	@Test @Ignore public void test_1895() { checkIsSubtype("X<null|(X,null)>","(null,null)"); }
	@Test @Ignore public void test_1896() { checkIsSubtype("X<null|(X,null)>","(null,null)"); }
	@Test @Ignore public void test_1897() { checkNotSubtype("X<null|(X,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_1898() { checkNotSubtype("X<null|(X,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_1899() { checkNotSubtype("X<null|(X,null)>","({null f1},null)"); }
	@Test @Ignore public void test_1900() { checkNotSubtype("X<null|(X,null)>","({null f2},null)"); }
	@Test @Ignore public void test_1901() { checkNotSubtype("X<null|(X,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1902() { checkNotSubtype("X<null|(X,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1903() { checkIsSubtype("X<null|(X,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1904() { checkIsSubtype("X<null|(X,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1905() { checkNotSubtype("X<null|(X,null)>","({null f1},null)"); }
	@Test @Ignore public void test_1906() { checkNotSubtype("X<null|(X,null)>","({null f2},null)"); }
	@Test @Ignore public void test_1907() { checkNotSubtype("X<null|(X,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_1908() { checkNotSubtype("X<null|(X,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_1909() { checkIsSubtype("X<null|(X,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1910() { checkIsSubtype("X<null|(X,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1911() { checkNotSubtype("X<null|(X,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1912() { checkNotSubtype("X<null|(X,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1913() { checkNotSubtype("X<null|(X,null)>","{{null f1} f1}"); }
	@Test @Ignore public void test_1914() { checkNotSubtype("X<null|(X,null)>","{{null f2} f1}"); }
	@Test @Ignore public void test_1915() { checkNotSubtype("X<null|(X,null)>","{{null f1} f2}"); }
	@Test @Ignore public void test_1916() { checkNotSubtype("X<null|(X,null)>","{{null f2} f2}"); }
	@Test @Ignore public void test_1917() { checkNotSubtype("X<null|(X,null)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1918() { checkNotSubtype("X<null|(X,null)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1919() { checkNotSubtype("X<null|(X,null)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1920() { checkNotSubtype("X<null|(X,null)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1921() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_1922() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_1923() { checkIsSubtype("X<null|(X,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1924() { checkNotSubtype("X<null|(X,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1925() { checkNotSubtype("X<null|(X,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1926() { checkIsSubtype("X<null|(X,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1927() { checkNotSubtype("X<null|(X,null)>","null|{null f1}"); }
	@Test @Ignore public void test_1928() { checkNotSubtype("X<null|(X,null)>","null|{null f2}"); }
	@Test @Ignore public void test_1929() { checkNotSubtype("X<null|(X,null)>","{null f1}|null"); }
	@Test @Ignore public void test_1930() { checkNotSubtype("X<null|(X,null)>","{null f2}|null"); }
	@Test @Ignore public void test_1931() { checkNotSubtype("X<null|(X,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1932() { checkNotSubtype("X<null|(X,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1933() { checkNotSubtype("X<null|(X,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1934() { checkNotSubtype("X<null|(X,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1935() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_1936() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_1937() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_1938() { checkIsSubtype("X<null|(X,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1939() { checkNotSubtype("X<null|(X,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1940() { checkNotSubtype("X<null|(X,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1941() { checkIsSubtype("X<null|(X,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1942() { checkNotSubtype("X<null|(X,null)>","{null f1}|null"); }
	@Test @Ignore public void test_1943() { checkNotSubtype("X<null|(X,null)>","{null f2}|null"); }
	@Test @Ignore public void test_1944() { checkNotSubtype("X<null|(X,null)>","null|{null f1}"); }
	@Test @Ignore public void test_1945() { checkNotSubtype("X<null|(X,null)>","null|{null f2}"); }
	@Test @Ignore public void test_1946() { checkNotSubtype("X<null|(X,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1947() { checkNotSubtype("X<null|(X,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1948() { checkNotSubtype("X<null|(X,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1949() { checkNotSubtype("X<null|(X,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1950() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_1951() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_1952() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_1953() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_1954() { checkNotSubtype("X<null|(null,X)>","{null f1}"); }
	@Test @Ignore public void test_1955() { checkNotSubtype("X<null|(null,X)>","{null f2}"); }
	@Test @Ignore public void test_1956() { checkIsSubtype("X<null|(null,X)>","(null,null)"); }
	@Test @Ignore public void test_1957() { checkIsSubtype("X<null|(null,X)>","(null,null)"); }
	@Test @Ignore public void test_1958() { checkNotSubtype("X<null|(null,X)>","(null,{null f1})"); }
	@Test @Ignore public void test_1959() { checkNotSubtype("X<null|(null,X)>","(null,{null f2})"); }
	@Test @Ignore public void test_1960() { checkNotSubtype("X<null|(null,X)>","({null f1},null)"); }
	@Test @Ignore public void test_1961() { checkNotSubtype("X<null|(null,X)>","({null f2},null)"); }
	@Test @Ignore public void test_1962() { checkIsSubtype("X<null|(null,X)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1963() { checkIsSubtype("X<null|(null,X)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1964() { checkNotSubtype("X<null|(null,X)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1965() { checkNotSubtype("X<null|(null,X)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1966() { checkNotSubtype("X<null|(null,X)>","({null f1},null)"); }
	@Test @Ignore public void test_1967() { checkNotSubtype("X<null|(null,X)>","({null f2},null)"); }
	@Test @Ignore public void test_1968() { checkNotSubtype("X<null|(null,X)>","(null,{null f1})"); }
	@Test @Ignore public void test_1969() { checkNotSubtype("X<null|(null,X)>","(null,{null f2})"); }
	@Test @Ignore public void test_1970() { checkNotSubtype("X<null|(null,X)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_1971() { checkNotSubtype("X<null|(null,X)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_1972() { checkIsSubtype("X<null|(null,X)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_1973() { checkIsSubtype("X<null|(null,X)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_1974() { checkNotSubtype("X<null|(null,X)>","{{null f1} f1}"); }
	@Test @Ignore public void test_1975() { checkNotSubtype("X<null|(null,X)>","{{null f2} f1}"); }
	@Test @Ignore public void test_1976() { checkNotSubtype("X<null|(null,X)>","{{null f1} f2}"); }
	@Test @Ignore public void test_1977() { checkNotSubtype("X<null|(null,X)>","{{null f2} f2}"); }
	@Test @Ignore public void test_1978() { checkNotSubtype("X<null|(null,X)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_1979() { checkNotSubtype("X<null|(null,X)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_1980() { checkNotSubtype("X<null|(null,X)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_1981() { checkNotSubtype("X<null|(null,X)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_1982() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_1983() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_1984() { checkNotSubtype("X<null|(null,X)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_1985() { checkIsSubtype("X<null|(null,X)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_1986() { checkIsSubtype("X<null|(null,X)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_1987() { checkNotSubtype("X<null|(null,X)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_1988() { checkNotSubtype("X<null|(null,X)>","null|{null f1}"); }
	@Test @Ignore public void test_1989() { checkNotSubtype("X<null|(null,X)>","null|{null f2}"); }
	@Test @Ignore public void test_1990() { checkNotSubtype("X<null|(null,X)>","{null f1}|null"); }
	@Test @Ignore public void test_1991() { checkNotSubtype("X<null|(null,X)>","{null f2}|null"); }
	@Test @Ignore public void test_1992() { checkNotSubtype("X<null|(null,X)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_1993() { checkNotSubtype("X<null|(null,X)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_1994() { checkNotSubtype("X<null|(null,X)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_1995() { checkNotSubtype("X<null|(null,X)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_1996() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_1997() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_1998() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_1999() { checkNotSubtype("X<null|(null,X)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2000() { checkIsSubtype("X<null|(null,X)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2001() { checkIsSubtype("X<null|(null,X)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2002() { checkNotSubtype("X<null|(null,X)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2003() { checkNotSubtype("X<null|(null,X)>","{null f1}|null"); }
	@Test @Ignore public void test_2004() { checkNotSubtype("X<null|(null,X)>","{null f2}|null"); }
	@Test @Ignore public void test_2005() { checkNotSubtype("X<null|(null,X)>","null|{null f1}"); }
	@Test @Ignore public void test_2006() { checkNotSubtype("X<null|(null,X)>","null|{null f2}"); }
	@Test @Ignore public void test_2007() { checkNotSubtype("X<null|(null,X)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2008() { checkNotSubtype("X<null|(null,X)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2009() { checkNotSubtype("X<null|(null,X)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2010() { checkNotSubtype("X<null|(null,X)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2011() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2012() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2013() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2014() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2015() { checkNotSubtype("X<(null,X)|null>","{null f1}"); }
	@Test @Ignore public void test_2016() { checkNotSubtype("X<(null,X)|null>","{null f2}"); }
	@Test @Ignore public void test_2017() { checkIsSubtype("X<(null,X)|null>","(null,null)"); }
	@Test @Ignore public void test_2018() { checkIsSubtype("X<(null,X)|null>","(null,null)"); }
	@Test @Ignore public void test_2019() { checkNotSubtype("X<(null,X)|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2020() { checkNotSubtype("X<(null,X)|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2021() { checkNotSubtype("X<(null,X)|null>","({null f1},null)"); }
	@Test @Ignore public void test_2022() { checkNotSubtype("X<(null,X)|null>","({null f2},null)"); }
	@Test @Ignore public void test_2023() { checkIsSubtype("X<(null,X)|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2024() { checkIsSubtype("X<(null,X)|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2025() { checkNotSubtype("X<(null,X)|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2026() { checkNotSubtype("X<(null,X)|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2027() { checkNotSubtype("X<(null,X)|null>","({null f1},null)"); }
	@Test @Ignore public void test_2028() { checkNotSubtype("X<(null,X)|null>","({null f2},null)"); }
	@Test @Ignore public void test_2029() { checkNotSubtype("X<(null,X)|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2030() { checkNotSubtype("X<(null,X)|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2031() { checkNotSubtype("X<(null,X)|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2032() { checkNotSubtype("X<(null,X)|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2033() { checkIsSubtype("X<(null,X)|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2034() { checkIsSubtype("X<(null,X)|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2035() { checkNotSubtype("X<(null,X)|null>","{{null f1} f1}"); }
	@Test @Ignore public void test_2036() { checkNotSubtype("X<(null,X)|null>","{{null f2} f1}"); }
	@Test @Ignore public void test_2037() { checkNotSubtype("X<(null,X)|null>","{{null f1} f2}"); }
	@Test @Ignore public void test_2038() { checkNotSubtype("X<(null,X)|null>","{{null f2} f2}"); }
	@Test @Ignore public void test_2039() { checkNotSubtype("X<(null,X)|null>","X<{X|null f1}>"); }
	@Test @Ignore public void test_2040() { checkNotSubtype("X<(null,X)|null>","X<{null|X f1}>"); }
	@Test @Ignore public void test_2041() { checkNotSubtype("X<(null,X)|null>","X<{null|X f2}>"); }
	@Test @Ignore public void test_2042() { checkNotSubtype("X<(null,X)|null>","X<{X|null f2}>"); }
	@Test @Ignore public void test_2043() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2044() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2045() { checkNotSubtype("X<(null,X)|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2046() { checkIsSubtype("X<(null,X)|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2047() { checkIsSubtype("X<(null,X)|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2048() { checkNotSubtype("X<(null,X)|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2049() { checkNotSubtype("X<(null,X)|null>","null|{null f1}"); }
	@Test @Ignore public void test_2050() { checkNotSubtype("X<(null,X)|null>","null|{null f2}"); }
	@Test @Ignore public void test_2051() { checkNotSubtype("X<(null,X)|null>","{null f1}|null"); }
	@Test @Ignore public void test_2052() { checkNotSubtype("X<(null,X)|null>","{null f2}|null"); }
	@Test @Ignore public void test_2053() { checkNotSubtype("X<(null,X)|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2054() { checkNotSubtype("X<(null,X)|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2055() { checkNotSubtype("X<(null,X)|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2056() { checkNotSubtype("X<(null,X)|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2057() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2058() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2059() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2060() { checkNotSubtype("X<(null,X)|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2061() { checkIsSubtype("X<(null,X)|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2062() { checkIsSubtype("X<(null,X)|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2063() { checkNotSubtype("X<(null,X)|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2064() { checkNotSubtype("X<(null,X)|null>","{null f1}|null"); }
	@Test @Ignore public void test_2065() { checkNotSubtype("X<(null,X)|null>","{null f2}|null"); }
	@Test @Ignore public void test_2066() { checkNotSubtype("X<(null,X)|null>","null|{null f1}"); }
	@Test @Ignore public void test_2067() { checkNotSubtype("X<(null,X)|null>","null|{null f2}"); }
	@Test @Ignore public void test_2068() { checkNotSubtype("X<(null,X)|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2069() { checkNotSubtype("X<(null,X)|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2070() { checkNotSubtype("X<(null,X)|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2071() { checkNotSubtype("X<(null,X)|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2072() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2073() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2074() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2075() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2076() { checkNotSubtype("X<(X,null)|null>","{null f1}"); }
	@Test @Ignore public void test_2077() { checkNotSubtype("X<(X,null)|null>","{null f2}"); }
	@Test @Ignore public void test_2078() { checkIsSubtype("X<(X,null)|null>","(null,null)"); }
	@Test @Ignore public void test_2079() { checkIsSubtype("X<(X,null)|null>","(null,null)"); }
	@Test @Ignore public void test_2080() { checkNotSubtype("X<(X,null)|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2081() { checkNotSubtype("X<(X,null)|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2082() { checkNotSubtype("X<(X,null)|null>","({null f1},null)"); }
	@Test @Ignore public void test_2083() { checkNotSubtype("X<(X,null)|null>","({null f2},null)"); }
	@Test @Ignore public void test_2084() { checkNotSubtype("X<(X,null)|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2085() { checkNotSubtype("X<(X,null)|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2086() { checkIsSubtype("X<(X,null)|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2087() { checkIsSubtype("X<(X,null)|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2088() { checkNotSubtype("X<(X,null)|null>","({null f1},null)"); }
	@Test @Ignore public void test_2089() { checkNotSubtype("X<(X,null)|null>","({null f2},null)"); }
	@Test @Ignore public void test_2090() { checkNotSubtype("X<(X,null)|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2091() { checkNotSubtype("X<(X,null)|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2092() { checkIsSubtype("X<(X,null)|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2093() { checkIsSubtype("X<(X,null)|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2094() { checkNotSubtype("X<(X,null)|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2095() { checkNotSubtype("X<(X,null)|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2096() { checkNotSubtype("X<(X,null)|null>","{{null f1} f1}"); }
	@Test @Ignore public void test_2097() { checkNotSubtype("X<(X,null)|null>","{{null f2} f1}"); }
	@Test @Ignore public void test_2098() { checkNotSubtype("X<(X,null)|null>","{{null f1} f2}"); }
	@Test @Ignore public void test_2099() { checkNotSubtype("X<(X,null)|null>","{{null f2} f2}"); }
	@Test @Ignore public void test_2100() { checkNotSubtype("X<(X,null)|null>","X<{X|null f1}>"); }
	@Test @Ignore public void test_2101() { checkNotSubtype("X<(X,null)|null>","X<{null|X f1}>"); }
	@Test @Ignore public void test_2102() { checkNotSubtype("X<(X,null)|null>","X<{null|X f2}>"); }
	@Test @Ignore public void test_2103() { checkNotSubtype("X<(X,null)|null>","X<{X|null f2}>"); }
	@Test @Ignore public void test_2104() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2105() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2106() { checkIsSubtype("X<(X,null)|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2107() { checkNotSubtype("X<(X,null)|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2108() { checkNotSubtype("X<(X,null)|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2109() { checkIsSubtype("X<(X,null)|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2110() { checkNotSubtype("X<(X,null)|null>","null|{null f1}"); }
	@Test @Ignore public void test_2111() { checkNotSubtype("X<(X,null)|null>","null|{null f2}"); }
	@Test @Ignore public void test_2112() { checkNotSubtype("X<(X,null)|null>","{null f1}|null"); }
	@Test @Ignore public void test_2113() { checkNotSubtype("X<(X,null)|null>","{null f2}|null"); }
	@Test @Ignore public void test_2114() { checkNotSubtype("X<(X,null)|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2115() { checkNotSubtype("X<(X,null)|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2116() { checkNotSubtype("X<(X,null)|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2117() { checkNotSubtype("X<(X,null)|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2118() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2119() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2120() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2121() { checkIsSubtype("X<(X,null)|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2122() { checkNotSubtype("X<(X,null)|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2123() { checkNotSubtype("X<(X,null)|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2124() { checkIsSubtype("X<(X,null)|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2125() { checkNotSubtype("X<(X,null)|null>","{null f1}|null"); }
	@Test @Ignore public void test_2126() { checkNotSubtype("X<(X,null)|null>","{null f2}|null"); }
	@Test @Ignore public void test_2127() { checkNotSubtype("X<(X,null)|null>","null|{null f1}"); }
	@Test @Ignore public void test_2128() { checkNotSubtype("X<(X,null)|null>","null|{null f2}"); }
	@Test @Ignore public void test_2129() { checkNotSubtype("X<(X,null)|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2130() { checkNotSubtype("X<(X,null)|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2131() { checkNotSubtype("X<(X,null)|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2132() { checkNotSubtype("X<(X,null)|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2133() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2134() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2135() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2136() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_2137() { checkIsSubtype("null|{null f1}","{null f1}"); }
	@Test @Ignore public void test_2138() { checkNotSubtype("null|{null f1}","{null f2}"); }
	@Test @Ignore public void test_2139() { checkNotSubtype("null|{null f1}","(null,null)"); }
	@Test @Ignore public void test_2140() { checkNotSubtype("null|{null f1}","(null,null)"); }
	@Test @Ignore public void test_2141() { checkNotSubtype("null|{null f1}","(null,{null f1})"); }
	@Test @Ignore public void test_2142() { checkNotSubtype("null|{null f1}","(null,{null f2})"); }
	@Test @Ignore public void test_2143() { checkNotSubtype("null|{null f1}","({null f1},null)"); }
	@Test @Ignore public void test_2144() { checkNotSubtype("null|{null f1}","({null f2},null)"); }
	@Test @Ignore public void test_2145() { checkNotSubtype("null|{null f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_2146() { checkNotSubtype("null|{null f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_2147() { checkNotSubtype("null|{null f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_2148() { checkNotSubtype("null|{null f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_2149() { checkNotSubtype("null|{null f1}","({null f1},null)"); }
	@Test @Ignore public void test_2150() { checkNotSubtype("null|{null f1}","({null f2},null)"); }
	@Test @Ignore public void test_2151() { checkNotSubtype("null|{null f1}","(null,{null f1})"); }
	@Test @Ignore public void test_2152() { checkNotSubtype("null|{null f1}","(null,{null f2})"); }
	@Test @Ignore public void test_2153() { checkNotSubtype("null|{null f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_2154() { checkNotSubtype("null|{null f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_2155() { checkNotSubtype("null|{null f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_2156() { checkNotSubtype("null|{null f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_2157() { checkNotSubtype("null|{null f1}","{{null f1} f1}"); }
	@Test @Ignore public void test_2158() { checkNotSubtype("null|{null f1}","{{null f2} f1}"); }
	@Test @Ignore public void test_2159() { checkNotSubtype("null|{null f1}","{{null f1} f2}"); }
	@Test @Ignore public void test_2160() { checkNotSubtype("null|{null f1}","{{null f2} f2}"); }
	@Test @Ignore public void test_2161() { checkNotSubtype("null|{null f1}","X<{X|null f1}>"); }
	@Test @Ignore public void test_2162() { checkNotSubtype("null|{null f1}","X<{null|X f1}>"); }
	@Test @Ignore public void test_2163() { checkNotSubtype("null|{null f1}","X<{null|X f2}>"); }
	@Test @Ignore public void test_2164() { checkNotSubtype("null|{null f1}","X<{X|null f2}>"); }
	@Test @Ignore public void test_2165() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_2166() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_2167() { checkNotSubtype("null|{null f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_2168() { checkNotSubtype("null|{null f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_2169() { checkNotSubtype("null|{null f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_2170() { checkNotSubtype("null|{null f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_2171() { checkIsSubtype("null|{null f1}","null|{null f1}"); }
	@Test @Ignore public void test_2172() { checkNotSubtype("null|{null f1}","null|{null f2}"); }
	@Test @Ignore public void test_2173() { checkIsSubtype("null|{null f1}","{null f1}|null"); }
	@Test @Ignore public void test_2174() { checkNotSubtype("null|{null f1}","{null f2}|null"); }
	@Test @Ignore public void test_2175() { checkNotSubtype("null|{null f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_2176() { checkNotSubtype("null|{null f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_2177() { checkNotSubtype("null|{null f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_2178() { checkNotSubtype("null|{null f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_2179() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_2180() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_2181() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_2182() { checkNotSubtype("null|{null f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_2183() { checkNotSubtype("null|{null f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_2184() { checkNotSubtype("null|{null f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_2185() { checkNotSubtype("null|{null f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_2186() { checkIsSubtype("null|{null f1}","{null f1}|null"); }
	@Test @Ignore public void test_2187() { checkNotSubtype("null|{null f1}","{null f2}|null"); }
	@Test @Ignore public void test_2188() { checkIsSubtype("null|{null f1}","null|{null f1}"); }
	@Test @Ignore public void test_2189() { checkNotSubtype("null|{null f1}","null|{null f2}"); }
	@Test @Ignore public void test_2190() { checkNotSubtype("null|{null f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_2191() { checkNotSubtype("null|{null f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_2192() { checkNotSubtype("null|{null f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_2193() { checkNotSubtype("null|{null f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_2194() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_2195() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_2196() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_2197() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_2198() { checkNotSubtype("null|{null f2}","{null f1}"); }
	@Test @Ignore public void test_2199() { checkIsSubtype("null|{null f2}","{null f2}"); }
	@Test @Ignore public void test_2200() { checkNotSubtype("null|{null f2}","(null,null)"); }
	@Test @Ignore public void test_2201() { checkNotSubtype("null|{null f2}","(null,null)"); }
	@Test @Ignore public void test_2202() { checkNotSubtype("null|{null f2}","(null,{null f1})"); }
	@Test @Ignore public void test_2203() { checkNotSubtype("null|{null f2}","(null,{null f2})"); }
	@Test @Ignore public void test_2204() { checkNotSubtype("null|{null f2}","({null f1},null)"); }
	@Test @Ignore public void test_2205() { checkNotSubtype("null|{null f2}","({null f2},null)"); }
	@Test @Ignore public void test_2206() { checkNotSubtype("null|{null f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_2207() { checkNotSubtype("null|{null f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_2208() { checkNotSubtype("null|{null f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_2209() { checkNotSubtype("null|{null f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_2210() { checkNotSubtype("null|{null f2}","({null f1},null)"); }
	@Test @Ignore public void test_2211() { checkNotSubtype("null|{null f2}","({null f2},null)"); }
	@Test @Ignore public void test_2212() { checkNotSubtype("null|{null f2}","(null,{null f1})"); }
	@Test @Ignore public void test_2213() { checkNotSubtype("null|{null f2}","(null,{null f2})"); }
	@Test @Ignore public void test_2214() { checkNotSubtype("null|{null f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_2215() { checkNotSubtype("null|{null f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_2216() { checkNotSubtype("null|{null f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_2217() { checkNotSubtype("null|{null f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_2218() { checkNotSubtype("null|{null f2}","{{null f1} f1}"); }
	@Test @Ignore public void test_2219() { checkNotSubtype("null|{null f2}","{{null f2} f1}"); }
	@Test @Ignore public void test_2220() { checkNotSubtype("null|{null f2}","{{null f1} f2}"); }
	@Test @Ignore public void test_2221() { checkNotSubtype("null|{null f2}","{{null f2} f2}"); }
	@Test @Ignore public void test_2222() { checkNotSubtype("null|{null f2}","X<{X|null f1}>"); }
	@Test @Ignore public void test_2223() { checkNotSubtype("null|{null f2}","X<{null|X f1}>"); }
	@Test @Ignore public void test_2224() { checkNotSubtype("null|{null f2}","X<{null|X f2}>"); }
	@Test @Ignore public void test_2225() { checkNotSubtype("null|{null f2}","X<{X|null f2}>"); }
	@Test @Ignore public void test_2226() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_2227() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_2228() { checkNotSubtype("null|{null f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_2229() { checkNotSubtype("null|{null f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_2230() { checkNotSubtype("null|{null f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_2231() { checkNotSubtype("null|{null f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_2232() { checkNotSubtype("null|{null f2}","null|{null f1}"); }
	@Test @Ignore public void test_2233() { checkIsSubtype("null|{null f2}","null|{null f2}"); }
	@Test @Ignore public void test_2234() { checkNotSubtype("null|{null f2}","{null f1}|null"); }
	@Test @Ignore public void test_2235() { checkIsSubtype("null|{null f2}","{null f2}|null"); }
	@Test @Ignore public void test_2236() { checkNotSubtype("null|{null f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_2237() { checkNotSubtype("null|{null f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_2238() { checkNotSubtype("null|{null f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_2239() { checkNotSubtype("null|{null f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_2240() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_2241() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_2242() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_2243() { checkNotSubtype("null|{null f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_2244() { checkNotSubtype("null|{null f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_2245() { checkNotSubtype("null|{null f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_2246() { checkNotSubtype("null|{null f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_2247() { checkNotSubtype("null|{null f2}","{null f1}|null"); }
	@Test @Ignore public void test_2248() { checkIsSubtype("null|{null f2}","{null f2}|null"); }
	@Test @Ignore public void test_2249() { checkNotSubtype("null|{null f2}","null|{null f1}"); }
	@Test @Ignore public void test_2250() { checkIsSubtype("null|{null f2}","null|{null f2}"); }
	@Test @Ignore public void test_2251() { checkNotSubtype("null|{null f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_2252() { checkNotSubtype("null|{null f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_2253() { checkNotSubtype("null|{null f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_2254() { checkNotSubtype("null|{null f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_2255() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_2256() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_2257() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_2258() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_2259() { checkIsSubtype("{null f1}|null","{null f1}"); }
	@Test @Ignore public void test_2260() { checkNotSubtype("{null f1}|null","{null f2}"); }
	@Test @Ignore public void test_2261() { checkNotSubtype("{null f1}|null","(null,null)"); }
	@Test @Ignore public void test_2262() { checkNotSubtype("{null f1}|null","(null,null)"); }
	@Test @Ignore public void test_2263() { checkNotSubtype("{null f1}|null","(null,{null f1})"); }
	@Test @Ignore public void test_2264() { checkNotSubtype("{null f1}|null","(null,{null f2})"); }
	@Test @Ignore public void test_2265() { checkNotSubtype("{null f1}|null","({null f1},null)"); }
	@Test @Ignore public void test_2266() { checkNotSubtype("{null f1}|null","({null f2},null)"); }
	@Test @Ignore public void test_2267() { checkNotSubtype("{null f1}|null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2268() { checkNotSubtype("{null f1}|null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2269() { checkNotSubtype("{null f1}|null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2270() { checkNotSubtype("{null f1}|null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2271() { checkNotSubtype("{null f1}|null","({null f1},null)"); }
	@Test @Ignore public void test_2272() { checkNotSubtype("{null f1}|null","({null f2},null)"); }
	@Test @Ignore public void test_2273() { checkNotSubtype("{null f1}|null","(null,{null f1})"); }
	@Test @Ignore public void test_2274() { checkNotSubtype("{null f1}|null","(null,{null f2})"); }
	@Test @Ignore public void test_2275() { checkNotSubtype("{null f1}|null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2276() { checkNotSubtype("{null f1}|null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2277() { checkNotSubtype("{null f1}|null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2278() { checkNotSubtype("{null f1}|null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2279() { checkNotSubtype("{null f1}|null","{{null f1} f1}"); }
	@Test @Ignore public void test_2280() { checkNotSubtype("{null f1}|null","{{null f2} f1}"); }
	@Test @Ignore public void test_2281() { checkNotSubtype("{null f1}|null","{{null f1} f2}"); }
	@Test @Ignore public void test_2282() { checkNotSubtype("{null f1}|null","{{null f2} f2}"); }
	@Test @Ignore public void test_2283() { checkNotSubtype("{null f1}|null","X<{X|null f1}>"); }
	@Test @Ignore public void test_2284() { checkNotSubtype("{null f1}|null","X<{null|X f1}>"); }
	@Test @Ignore public void test_2285() { checkNotSubtype("{null f1}|null","X<{null|X f2}>"); }
	@Test @Ignore public void test_2286() { checkNotSubtype("{null f1}|null","X<{X|null f2}>"); }
	@Test @Ignore public void test_2287() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_2288() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_2289() { checkNotSubtype("{null f1}|null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2290() { checkNotSubtype("{null f1}|null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2291() { checkNotSubtype("{null f1}|null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2292() { checkNotSubtype("{null f1}|null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2293() { checkIsSubtype("{null f1}|null","null|{null f1}"); }
	@Test @Ignore public void test_2294() { checkNotSubtype("{null f1}|null","null|{null f2}"); }
	@Test @Ignore public void test_2295() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test @Ignore public void test_2296() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test @Ignore public void test_2297() { checkNotSubtype("{null f1}|null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2298() { checkNotSubtype("{null f1}|null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2299() { checkNotSubtype("{null f1}|null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2300() { checkNotSubtype("{null f1}|null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2301() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_2302() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_2303() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_2304() { checkNotSubtype("{null f1}|null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2305() { checkNotSubtype("{null f1}|null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2306() { checkNotSubtype("{null f1}|null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2307() { checkNotSubtype("{null f1}|null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2308() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test @Ignore public void test_2309() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test @Ignore public void test_2310() { checkIsSubtype("{null f1}|null","null|{null f1}"); }
	@Test @Ignore public void test_2311() { checkNotSubtype("{null f1}|null","null|{null f2}"); }
	@Test @Ignore public void test_2312() { checkNotSubtype("{null f1}|null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2313() { checkNotSubtype("{null f1}|null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2314() { checkNotSubtype("{null f1}|null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2315() { checkNotSubtype("{null f1}|null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2316() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_2317() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_2318() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_2319() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_2320() { checkNotSubtype("{null f2}|null","{null f1}"); }
	@Test @Ignore public void test_2321() { checkIsSubtype("{null f2}|null","{null f2}"); }
	@Test @Ignore public void test_2322() { checkNotSubtype("{null f2}|null","(null,null)"); }
	@Test @Ignore public void test_2323() { checkNotSubtype("{null f2}|null","(null,null)"); }
	@Test @Ignore public void test_2324() { checkNotSubtype("{null f2}|null","(null,{null f1})"); }
	@Test @Ignore public void test_2325() { checkNotSubtype("{null f2}|null","(null,{null f2})"); }
	@Test @Ignore public void test_2326() { checkNotSubtype("{null f2}|null","({null f1},null)"); }
	@Test @Ignore public void test_2327() { checkNotSubtype("{null f2}|null","({null f2},null)"); }
	@Test @Ignore public void test_2328() { checkNotSubtype("{null f2}|null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2329() { checkNotSubtype("{null f2}|null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2330() { checkNotSubtype("{null f2}|null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2331() { checkNotSubtype("{null f2}|null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2332() { checkNotSubtype("{null f2}|null","({null f1},null)"); }
	@Test @Ignore public void test_2333() { checkNotSubtype("{null f2}|null","({null f2},null)"); }
	@Test @Ignore public void test_2334() { checkNotSubtype("{null f2}|null","(null,{null f1})"); }
	@Test @Ignore public void test_2335() { checkNotSubtype("{null f2}|null","(null,{null f2})"); }
	@Test @Ignore public void test_2336() { checkNotSubtype("{null f2}|null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2337() { checkNotSubtype("{null f2}|null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2338() { checkNotSubtype("{null f2}|null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2339() { checkNotSubtype("{null f2}|null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2340() { checkNotSubtype("{null f2}|null","{{null f1} f1}"); }
	@Test @Ignore public void test_2341() { checkNotSubtype("{null f2}|null","{{null f2} f1}"); }
	@Test @Ignore public void test_2342() { checkNotSubtype("{null f2}|null","{{null f1} f2}"); }
	@Test @Ignore public void test_2343() { checkNotSubtype("{null f2}|null","{{null f2} f2}"); }
	@Test @Ignore public void test_2344() { checkNotSubtype("{null f2}|null","X<{X|null f1}>"); }
	@Test @Ignore public void test_2345() { checkNotSubtype("{null f2}|null","X<{null|X f1}>"); }
	@Test @Ignore public void test_2346() { checkNotSubtype("{null f2}|null","X<{null|X f2}>"); }
	@Test @Ignore public void test_2347() { checkNotSubtype("{null f2}|null","X<{X|null f2}>"); }
	@Test @Ignore public void test_2348() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_2349() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_2350() { checkNotSubtype("{null f2}|null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2351() { checkNotSubtype("{null f2}|null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2352() { checkNotSubtype("{null f2}|null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2353() { checkNotSubtype("{null f2}|null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2354() { checkNotSubtype("{null f2}|null","null|{null f1}"); }
	@Test @Ignore public void test_2355() { checkIsSubtype("{null f2}|null","null|{null f2}"); }
	@Test @Ignore public void test_2356() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test @Ignore public void test_2357() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test @Ignore public void test_2358() { checkNotSubtype("{null f2}|null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2359() { checkNotSubtype("{null f2}|null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2360() { checkNotSubtype("{null f2}|null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2361() { checkNotSubtype("{null f2}|null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2362() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_2363() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_2364() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_2365() { checkNotSubtype("{null f2}|null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2366() { checkNotSubtype("{null f2}|null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2367() { checkNotSubtype("{null f2}|null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2368() { checkNotSubtype("{null f2}|null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2369() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test @Ignore public void test_2370() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test @Ignore public void test_2371() { checkNotSubtype("{null f2}|null","null|{null f1}"); }
	@Test @Ignore public void test_2372() { checkIsSubtype("{null f2}|null","null|{null f2}"); }
	@Test @Ignore public void test_2373() { checkNotSubtype("{null f2}|null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2374() { checkNotSubtype("{null f2}|null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2375() { checkNotSubtype("{null f2}|null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2376() { checkNotSubtype("{null f2}|null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2377() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_2378() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_2379() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_2380() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_2381() { checkIsSubtype("X<null|{X f1}>","{null f1}"); }
	@Test @Ignore public void test_2382() { checkNotSubtype("X<null|{X f1}>","{null f2}"); }
	@Test @Ignore public void test_2383() { checkNotSubtype("X<null|{X f1}>","(null,null)"); }
	@Test @Ignore public void test_2384() { checkNotSubtype("X<null|{X f1}>","(null,null)"); }
	@Test @Ignore public void test_2385() { checkNotSubtype("X<null|{X f1}>","(null,{null f1})"); }
	@Test @Ignore public void test_2386() { checkNotSubtype("X<null|{X f1}>","(null,{null f2})"); }
	@Test @Ignore public void test_2387() { checkNotSubtype("X<null|{X f1}>","({null f1},null)"); }
	@Test @Ignore public void test_2388() { checkNotSubtype("X<null|{X f1}>","({null f2},null)"); }
	@Test @Ignore public void test_2389() { checkNotSubtype("X<null|{X f1}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2390() { checkNotSubtype("X<null|{X f1}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2391() { checkNotSubtype("X<null|{X f1}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2392() { checkNotSubtype("X<null|{X f1}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2393() { checkNotSubtype("X<null|{X f1}>","({null f1},null)"); }
	@Test @Ignore public void test_2394() { checkNotSubtype("X<null|{X f1}>","({null f2},null)"); }
	@Test @Ignore public void test_2395() { checkNotSubtype("X<null|{X f1}>","(null,{null f1})"); }
	@Test @Ignore public void test_2396() { checkNotSubtype("X<null|{X f1}>","(null,{null f2})"); }
	@Test @Ignore public void test_2397() { checkNotSubtype("X<null|{X f1}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2398() { checkNotSubtype("X<null|{X f1}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2399() { checkNotSubtype("X<null|{X f1}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2400() { checkNotSubtype("X<null|{X f1}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2401() { checkIsSubtype("X<null|{X f1}>","{{null f1} f1}"); }
	@Test @Ignore public void test_2402() { checkNotSubtype("X<null|{X f1}>","{{null f2} f1}"); }
	@Test @Ignore public void test_2403() { checkNotSubtype("X<null|{X f1}>","{{null f1} f2}"); }
	@Test @Ignore public void test_2404() { checkNotSubtype("X<null|{X f1}>","{{null f2} f2}"); }
	@Test @Ignore public void test_2405() { checkIsSubtype("X<null|{X f1}>","X<{X|null f1}>"); }
	@Test @Ignore public void test_2406() { checkIsSubtype("X<null|{X f1}>","X<{null|X f1}>"); }
	@Test @Ignore public void test_2407() { checkNotSubtype("X<null|{X f1}>","X<{null|X f2}>"); }
	@Test @Ignore public void test_2408() { checkNotSubtype("X<null|{X f1}>","X<{X|null f2}>"); }
	@Test @Ignore public void test_2409() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_2410() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_2411() { checkNotSubtype("X<null|{X f1}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2412() { checkNotSubtype("X<null|{X f1}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2413() { checkNotSubtype("X<null|{X f1}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2414() { checkNotSubtype("X<null|{X f1}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2415() { checkIsSubtype("X<null|{X f1}>","null|{null f1}"); }
	@Test @Ignore public void test_2416() { checkNotSubtype("X<null|{X f1}>","null|{null f2}"); }
	@Test @Ignore public void test_2417() { checkIsSubtype("X<null|{X f1}>","{null f1}|null"); }
	@Test @Ignore public void test_2418() { checkNotSubtype("X<null|{X f1}>","{null f2}|null"); }
	@Test @Ignore public void test_2419() { checkIsSubtype("X<null|{X f1}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2420() { checkNotSubtype("X<null|{X f1}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2421() { checkIsSubtype("X<null|{X f1}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2422() { checkNotSubtype("X<null|{X f1}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2423() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_2424() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_2425() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_2426() { checkNotSubtype("X<null|{X f1}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2427() { checkNotSubtype("X<null|{X f1}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2428() { checkNotSubtype("X<null|{X f1}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2429() { checkNotSubtype("X<null|{X f1}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2430() { checkIsSubtype("X<null|{X f1}>","{null f1}|null"); }
	@Test @Ignore public void test_2431() { checkNotSubtype("X<null|{X f1}>","{null f2}|null"); }
	@Test @Ignore public void test_2432() { checkIsSubtype("X<null|{X f1}>","null|{null f1}"); }
	@Test @Ignore public void test_2433() { checkNotSubtype("X<null|{X f1}>","null|{null f2}"); }
	@Test @Ignore public void test_2434() { checkIsSubtype("X<null|{X f1}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2435() { checkNotSubtype("X<null|{X f1}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2436() { checkIsSubtype("X<null|{X f1}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2437() { checkNotSubtype("X<null|{X f1}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2438() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_2439() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_2440() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_2441() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_2442() { checkNotSubtype("X<null|{X f2}>","{null f1}"); }
	@Test @Ignore public void test_2443() { checkIsSubtype("X<null|{X f2}>","{null f2}"); }
	@Test @Ignore public void test_2444() { checkNotSubtype("X<null|{X f2}>","(null,null)"); }
	@Test @Ignore public void test_2445() { checkNotSubtype("X<null|{X f2}>","(null,null)"); }
	@Test @Ignore public void test_2446() { checkNotSubtype("X<null|{X f2}>","(null,{null f1})"); }
	@Test @Ignore public void test_2447() { checkNotSubtype("X<null|{X f2}>","(null,{null f2})"); }
	@Test @Ignore public void test_2448() { checkNotSubtype("X<null|{X f2}>","({null f1},null)"); }
	@Test @Ignore public void test_2449() { checkNotSubtype("X<null|{X f2}>","({null f2},null)"); }
	@Test @Ignore public void test_2450() { checkNotSubtype("X<null|{X f2}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2451() { checkNotSubtype("X<null|{X f2}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2452() { checkNotSubtype("X<null|{X f2}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2453() { checkNotSubtype("X<null|{X f2}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2454() { checkNotSubtype("X<null|{X f2}>","({null f1},null)"); }
	@Test @Ignore public void test_2455() { checkNotSubtype("X<null|{X f2}>","({null f2},null)"); }
	@Test @Ignore public void test_2456() { checkNotSubtype("X<null|{X f2}>","(null,{null f1})"); }
	@Test @Ignore public void test_2457() { checkNotSubtype("X<null|{X f2}>","(null,{null f2})"); }
	@Test @Ignore public void test_2458() { checkNotSubtype("X<null|{X f2}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2459() { checkNotSubtype("X<null|{X f2}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2460() { checkNotSubtype("X<null|{X f2}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2461() { checkNotSubtype("X<null|{X f2}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2462() { checkNotSubtype("X<null|{X f2}>","{{null f1} f1}"); }
	@Test @Ignore public void test_2463() { checkNotSubtype("X<null|{X f2}>","{{null f2} f1}"); }
	@Test @Ignore public void test_2464() { checkNotSubtype("X<null|{X f2}>","{{null f1} f2}"); }
	@Test @Ignore public void test_2465() { checkIsSubtype("X<null|{X f2}>","{{null f2} f2}"); }
	@Test @Ignore public void test_2466() { checkNotSubtype("X<null|{X f2}>","X<{X|null f1}>"); }
	@Test @Ignore public void test_2467() { checkNotSubtype("X<null|{X f2}>","X<{null|X f1}>"); }
	@Test @Ignore public void test_2468() { checkIsSubtype("X<null|{X f2}>","X<{null|X f2}>"); }
	@Test @Ignore public void test_2469() { checkIsSubtype("X<null|{X f2}>","X<{X|null f2}>"); }
	@Test @Ignore public void test_2470() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_2471() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_2472() { checkNotSubtype("X<null|{X f2}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2473() { checkNotSubtype("X<null|{X f2}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2474() { checkNotSubtype("X<null|{X f2}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2475() { checkNotSubtype("X<null|{X f2}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2476() { checkNotSubtype("X<null|{X f2}>","null|{null f1}"); }
	@Test @Ignore public void test_2477() { checkIsSubtype("X<null|{X f2}>","null|{null f2}"); }
	@Test @Ignore public void test_2478() { checkNotSubtype("X<null|{X f2}>","{null f1}|null"); }
	@Test @Ignore public void test_2479() { checkIsSubtype("X<null|{X f2}>","{null f2}|null"); }
	@Test @Ignore public void test_2480() { checkNotSubtype("X<null|{X f2}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2481() { checkIsSubtype("X<null|{X f2}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2482() { checkNotSubtype("X<null|{X f2}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2483() { checkIsSubtype("X<null|{X f2}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2484() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_2485() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_2486() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_2487() { checkNotSubtype("X<null|{X f2}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2488() { checkNotSubtype("X<null|{X f2}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2489() { checkNotSubtype("X<null|{X f2}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2490() { checkNotSubtype("X<null|{X f2}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2491() { checkNotSubtype("X<null|{X f2}>","{null f1}|null"); }
	@Test @Ignore public void test_2492() { checkIsSubtype("X<null|{X f2}>","{null f2}|null"); }
	@Test @Ignore public void test_2493() { checkNotSubtype("X<null|{X f2}>","null|{null f1}"); }
	@Test @Ignore public void test_2494() { checkIsSubtype("X<null|{X f2}>","null|{null f2}"); }
	@Test @Ignore public void test_2495() { checkNotSubtype("X<null|{X f2}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2496() { checkIsSubtype("X<null|{X f2}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2497() { checkNotSubtype("X<null|{X f2}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2498() { checkIsSubtype("X<null|{X f2}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2499() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_2500() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_2501() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_2502() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_2503() { checkIsSubtype("X<{X f1}|null>","{null f1}"); }
	@Test @Ignore public void test_2504() { checkNotSubtype("X<{X f1}|null>","{null f2}"); }
	@Test @Ignore public void test_2505() { checkNotSubtype("X<{X f1}|null>","(null,null)"); }
	@Test @Ignore public void test_2506() { checkNotSubtype("X<{X f1}|null>","(null,null)"); }
	@Test @Ignore public void test_2507() { checkNotSubtype("X<{X f1}|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2508() { checkNotSubtype("X<{X f1}|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2509() { checkNotSubtype("X<{X f1}|null>","({null f1},null)"); }
	@Test @Ignore public void test_2510() { checkNotSubtype("X<{X f1}|null>","({null f2},null)"); }
	@Test @Ignore public void test_2511() { checkNotSubtype("X<{X f1}|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2512() { checkNotSubtype("X<{X f1}|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2513() { checkNotSubtype("X<{X f1}|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2514() { checkNotSubtype("X<{X f1}|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2515() { checkNotSubtype("X<{X f1}|null>","({null f1},null)"); }
	@Test @Ignore public void test_2516() { checkNotSubtype("X<{X f1}|null>","({null f2},null)"); }
	@Test @Ignore public void test_2517() { checkNotSubtype("X<{X f1}|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2518() { checkNotSubtype("X<{X f1}|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2519() { checkNotSubtype("X<{X f1}|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2520() { checkNotSubtype("X<{X f1}|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2521() { checkNotSubtype("X<{X f1}|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2522() { checkNotSubtype("X<{X f1}|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2523() { checkIsSubtype("X<{X f1}|null>","{{null f1} f1}"); }
	@Test @Ignore public void test_2524() { checkNotSubtype("X<{X f1}|null>","{{null f2} f1}"); }
	@Test @Ignore public void test_2525() { checkNotSubtype("X<{X f1}|null>","{{null f1} f2}"); }
	@Test @Ignore public void test_2526() { checkNotSubtype("X<{X f1}|null>","{{null f2} f2}"); }
	@Test @Ignore public void test_2527() { checkIsSubtype("X<{X f1}|null>","X<{X|null f1}>"); }
	@Test @Ignore public void test_2528() { checkIsSubtype("X<{X f1}|null>","X<{null|X f1}>"); }
	@Test @Ignore public void test_2529() { checkNotSubtype("X<{X f1}|null>","X<{null|X f2}>"); }
	@Test @Ignore public void test_2530() { checkNotSubtype("X<{X f1}|null>","X<{X|null f2}>"); }
	@Test @Ignore public void test_2531() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_2532() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_2533() { checkNotSubtype("X<{X f1}|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2534() { checkNotSubtype("X<{X f1}|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2535() { checkNotSubtype("X<{X f1}|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2536() { checkNotSubtype("X<{X f1}|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2537() { checkIsSubtype("X<{X f1}|null>","null|{null f1}"); }
	@Test @Ignore public void test_2538() { checkNotSubtype("X<{X f1}|null>","null|{null f2}"); }
	@Test @Ignore public void test_2539() { checkIsSubtype("X<{X f1}|null>","{null f1}|null"); }
	@Test @Ignore public void test_2540() { checkNotSubtype("X<{X f1}|null>","{null f2}|null"); }
	@Test @Ignore public void test_2541() { checkIsSubtype("X<{X f1}|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2542() { checkNotSubtype("X<{X f1}|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2543() { checkIsSubtype("X<{X f1}|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2544() { checkNotSubtype("X<{X f1}|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2545() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_2546() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_2547() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_2548() { checkNotSubtype("X<{X f1}|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2549() { checkNotSubtype("X<{X f1}|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2550() { checkNotSubtype("X<{X f1}|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2551() { checkNotSubtype("X<{X f1}|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2552() { checkIsSubtype("X<{X f1}|null>","{null f1}|null"); }
	@Test @Ignore public void test_2553() { checkNotSubtype("X<{X f1}|null>","{null f2}|null"); }
	@Test @Ignore public void test_2554() { checkIsSubtype("X<{X f1}|null>","null|{null f1}"); }
	@Test @Ignore public void test_2555() { checkNotSubtype("X<{X f1}|null>","null|{null f2}"); }
	@Test @Ignore public void test_2556() { checkIsSubtype("X<{X f1}|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2557() { checkNotSubtype("X<{X f1}|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2558() { checkIsSubtype("X<{X f1}|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2559() { checkNotSubtype("X<{X f1}|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2560() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_2561() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_2562() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_2563() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_2564() { checkNotSubtype("X<{X f2}|null>","{null f1}"); }
	@Test @Ignore public void test_2565() { checkIsSubtype("X<{X f2}|null>","{null f2}"); }
	@Test @Ignore public void test_2566() { checkNotSubtype("X<{X f2}|null>","(null,null)"); }
	@Test @Ignore public void test_2567() { checkNotSubtype("X<{X f2}|null>","(null,null)"); }
	@Test @Ignore public void test_2568() { checkNotSubtype("X<{X f2}|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2569() { checkNotSubtype("X<{X f2}|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2570() { checkNotSubtype("X<{X f2}|null>","({null f1},null)"); }
	@Test @Ignore public void test_2571() { checkNotSubtype("X<{X f2}|null>","({null f2},null)"); }
	@Test @Ignore public void test_2572() { checkNotSubtype("X<{X f2}|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2573() { checkNotSubtype("X<{X f2}|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2574() { checkNotSubtype("X<{X f2}|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2575() { checkNotSubtype("X<{X f2}|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2576() { checkNotSubtype("X<{X f2}|null>","({null f1},null)"); }
	@Test @Ignore public void test_2577() { checkNotSubtype("X<{X f2}|null>","({null f2},null)"); }
	@Test @Ignore public void test_2578() { checkNotSubtype("X<{X f2}|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2579() { checkNotSubtype("X<{X f2}|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2580() { checkNotSubtype("X<{X f2}|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2581() { checkNotSubtype("X<{X f2}|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2582() { checkNotSubtype("X<{X f2}|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2583() { checkNotSubtype("X<{X f2}|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2584() { checkNotSubtype("X<{X f2}|null>","{{null f1} f1}"); }
	@Test @Ignore public void test_2585() { checkNotSubtype("X<{X f2}|null>","{{null f2} f1}"); }
	@Test @Ignore public void test_2586() { checkNotSubtype("X<{X f2}|null>","{{null f1} f2}"); }
	@Test @Ignore public void test_2587() { checkIsSubtype("X<{X f2}|null>","{{null f2} f2}"); }
	@Test @Ignore public void test_2588() { checkNotSubtype("X<{X f2}|null>","X<{X|null f1}>"); }
	@Test @Ignore public void test_2589() { checkNotSubtype("X<{X f2}|null>","X<{null|X f1}>"); }
	@Test @Ignore public void test_2590() { checkIsSubtype("X<{X f2}|null>","X<{null|X f2}>"); }
	@Test @Ignore public void test_2591() { checkIsSubtype("X<{X f2}|null>","X<{X|null f2}>"); }
	@Test @Ignore public void test_2592() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_2593() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_2594() { checkNotSubtype("X<{X f2}|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2595() { checkNotSubtype("X<{X f2}|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2596() { checkNotSubtype("X<{X f2}|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2597() { checkNotSubtype("X<{X f2}|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2598() { checkNotSubtype("X<{X f2}|null>","null|{null f1}"); }
	@Test @Ignore public void test_2599() { checkIsSubtype("X<{X f2}|null>","null|{null f2}"); }
	@Test @Ignore public void test_2600() { checkNotSubtype("X<{X f2}|null>","{null f1}|null"); }
	@Test @Ignore public void test_2601() { checkIsSubtype("X<{X f2}|null>","{null f2}|null"); }
	@Test @Ignore public void test_2602() { checkNotSubtype("X<{X f2}|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2603() { checkIsSubtype("X<{X f2}|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2604() { checkNotSubtype("X<{X f2}|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2605() { checkIsSubtype("X<{X f2}|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2606() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_2607() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_2608() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_2609() { checkNotSubtype("X<{X f2}|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2610() { checkNotSubtype("X<{X f2}|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2611() { checkNotSubtype("X<{X f2}|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2612() { checkNotSubtype("X<{X f2}|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2613() { checkNotSubtype("X<{X f2}|null>","{null f1}|null"); }
	@Test @Ignore public void test_2614() { checkIsSubtype("X<{X f2}|null>","{null f2}|null"); }
	@Test @Ignore public void test_2615() { checkNotSubtype("X<{X f2}|null>","null|{null f1}"); }
	@Test @Ignore public void test_2616() { checkIsSubtype("X<{X f2}|null>","null|{null f2}"); }
	@Test @Ignore public void test_2617() { checkNotSubtype("X<{X f2}|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2618() { checkIsSubtype("X<{X f2}|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2619() { checkNotSubtype("X<{X f2}|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2620() { checkIsSubtype("X<{X f2}|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2621() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_2622() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_2623() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_2624() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2625() { checkNotSubtype("null","{null f1}"); }
	@Test @Ignore public void test_2626() { checkNotSubtype("null","{null f2}"); }
	@Test @Ignore public void test_2627() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_2628() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_2629() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_2630() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_2631() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_2632() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_2633() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2634() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2635() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2636() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2637() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_2638() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_2639() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_2640() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_2641() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2642() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2643() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2644() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2645() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test @Ignore public void test_2646() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test @Ignore public void test_2647() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test @Ignore public void test_2648() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test @Ignore public void test_2649() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test @Ignore public void test_2650() { checkNotSubtype("null","X<{null|X f1}>"); }
	@Test @Ignore public void test_2651() { checkNotSubtype("null","X<{null|X f2}>"); }
	@Test @Ignore public void test_2652() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test @Ignore public void test_2653() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2654() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2655() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2656() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2657() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2658() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2659() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_2660() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_2661() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_2662() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_2663() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2664() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2665() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2666() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2667() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2668() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2669() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2670() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2671() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2672() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2673() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2674() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_2675() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_2676() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_2677() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_2678() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2679() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2680() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2681() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2682() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2683() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2684() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2685() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2686() { checkNotSubtype("null","{null f1}"); }
	@Test @Ignore public void test_2687() { checkNotSubtype("null","{null f2}"); }
	@Test @Ignore public void test_2688() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_2689() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_2690() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_2691() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_2692() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_2693() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_2694() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2695() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2696() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2697() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2698() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_2699() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_2700() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_2701() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_2702() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2703() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2704() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2705() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2706() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test @Ignore public void test_2707() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test @Ignore public void test_2708() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test @Ignore public void test_2709() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test @Ignore public void test_2710() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test @Ignore public void test_2711() { checkNotSubtype("null","X<{null|X f1}>"); }
	@Test @Ignore public void test_2712() { checkNotSubtype("null","X<{null|X f2}>"); }
	@Test @Ignore public void test_2713() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test @Ignore public void test_2714() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2715() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2716() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2717() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2718() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2719() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2720() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_2721() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_2722() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_2723() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_2724() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2725() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2726() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2727() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2728() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2729() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2730() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2731() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2732() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2733() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2734() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2735() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_2736() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_2737() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_2738() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_2739() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2740() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2741() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2742() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2743() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2744() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2745() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2746() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2747() { checkNotSubtype("null","{null f1}"); }
	@Test @Ignore public void test_2748() { checkNotSubtype("null","{null f2}"); }
	@Test @Ignore public void test_2749() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_2750() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_2751() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_2752() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_2753() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_2754() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_2755() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2756() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2757() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2758() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2759() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_2760() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_2761() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_2762() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_2763() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_2764() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_2765() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_2766() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_2767() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test @Ignore public void test_2768() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test @Ignore public void test_2769() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test @Ignore public void test_2770() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test @Ignore public void test_2771() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test @Ignore public void test_2772() { checkNotSubtype("null","X<{null|X f1}>"); }
	@Test @Ignore public void test_2773() { checkNotSubtype("null","X<{null|X f2}>"); }
	@Test @Ignore public void test_2774() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test @Ignore public void test_2775() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2776() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2777() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2778() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2779() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2780() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2781() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_2782() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_2783() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_2784() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_2785() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2786() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2787() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2788() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2789() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2790() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2791() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2792() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_2793() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_2794() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_2795() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_2796() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_2797() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_2798() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_2799() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_2800() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_2801() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_2802() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_2803() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_2804() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2805() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2806() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_2807() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2808() { checkNotSubtype("X<(X,null)|null>","{null f1}"); }
	@Test @Ignore public void test_2809() { checkNotSubtype("X<(X,null)|null>","{null f2}"); }
	@Test @Ignore public void test_2810() { checkIsSubtype("X<(X,null)|null>","(null,null)"); }
	@Test @Ignore public void test_2811() { checkIsSubtype("X<(X,null)|null>","(null,null)"); }
	@Test @Ignore public void test_2812() { checkNotSubtype("X<(X,null)|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2813() { checkNotSubtype("X<(X,null)|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2814() { checkNotSubtype("X<(X,null)|null>","({null f1},null)"); }
	@Test @Ignore public void test_2815() { checkNotSubtype("X<(X,null)|null>","({null f2},null)"); }
	@Test @Ignore public void test_2816() { checkNotSubtype("X<(X,null)|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2817() { checkNotSubtype("X<(X,null)|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2818() { checkIsSubtype("X<(X,null)|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2819() { checkIsSubtype("X<(X,null)|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2820() { checkNotSubtype("X<(X,null)|null>","({null f1},null)"); }
	@Test @Ignore public void test_2821() { checkNotSubtype("X<(X,null)|null>","({null f2},null)"); }
	@Test @Ignore public void test_2822() { checkNotSubtype("X<(X,null)|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2823() { checkNotSubtype("X<(X,null)|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2824() { checkIsSubtype("X<(X,null)|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2825() { checkIsSubtype("X<(X,null)|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2826() { checkNotSubtype("X<(X,null)|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2827() { checkNotSubtype("X<(X,null)|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2828() { checkNotSubtype("X<(X,null)|null>","{{null f1} f1}"); }
	@Test @Ignore public void test_2829() { checkNotSubtype("X<(X,null)|null>","{{null f2} f1}"); }
	@Test @Ignore public void test_2830() { checkNotSubtype("X<(X,null)|null>","{{null f1} f2}"); }
	@Test @Ignore public void test_2831() { checkNotSubtype("X<(X,null)|null>","{{null f2} f2}"); }
	@Test @Ignore public void test_2832() { checkNotSubtype("X<(X,null)|null>","X<{X|null f1}>"); }
	@Test @Ignore public void test_2833() { checkNotSubtype("X<(X,null)|null>","X<{null|X f1}>"); }
	@Test @Ignore public void test_2834() { checkNotSubtype("X<(X,null)|null>","X<{null|X f2}>"); }
	@Test @Ignore public void test_2835() { checkNotSubtype("X<(X,null)|null>","X<{X|null f2}>"); }
	@Test @Ignore public void test_2836() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2837() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2838() { checkIsSubtype("X<(X,null)|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2839() { checkNotSubtype("X<(X,null)|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2840() { checkNotSubtype("X<(X,null)|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2841() { checkIsSubtype("X<(X,null)|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2842() { checkNotSubtype("X<(X,null)|null>","null|{null f1}"); }
	@Test @Ignore public void test_2843() { checkNotSubtype("X<(X,null)|null>","null|{null f2}"); }
	@Test @Ignore public void test_2844() { checkNotSubtype("X<(X,null)|null>","{null f1}|null"); }
	@Test @Ignore public void test_2845() { checkNotSubtype("X<(X,null)|null>","{null f2}|null"); }
	@Test @Ignore public void test_2846() { checkNotSubtype("X<(X,null)|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2847() { checkNotSubtype("X<(X,null)|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2848() { checkNotSubtype("X<(X,null)|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2849() { checkNotSubtype("X<(X,null)|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2850() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2851() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2852() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2853() { checkIsSubtype("X<(X,null)|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2854() { checkNotSubtype("X<(X,null)|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2855() { checkNotSubtype("X<(X,null)|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2856() { checkIsSubtype("X<(X,null)|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2857() { checkNotSubtype("X<(X,null)|null>","{null f1}|null"); }
	@Test @Ignore public void test_2858() { checkNotSubtype("X<(X,null)|null>","{null f2}|null"); }
	@Test @Ignore public void test_2859() { checkNotSubtype("X<(X,null)|null>","null|{null f1}"); }
	@Test @Ignore public void test_2860() { checkNotSubtype("X<(X,null)|null>","null|{null f2}"); }
	@Test @Ignore public void test_2861() { checkNotSubtype("X<(X,null)|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2862() { checkNotSubtype("X<(X,null)|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2863() { checkNotSubtype("X<(X,null)|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2864() { checkNotSubtype("X<(X,null)|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2865() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2866() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2867() { checkIsSubtype("X<(X,null)|null>","null"); }
	@Test @Ignore public void test_2868() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2869() { checkNotSubtype("X<(null,X)|null>","{null f1}"); }
	@Test @Ignore public void test_2870() { checkNotSubtype("X<(null,X)|null>","{null f2}"); }
	@Test @Ignore public void test_2871() { checkIsSubtype("X<(null,X)|null>","(null,null)"); }
	@Test @Ignore public void test_2872() { checkIsSubtype("X<(null,X)|null>","(null,null)"); }
	@Test @Ignore public void test_2873() { checkNotSubtype("X<(null,X)|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2874() { checkNotSubtype("X<(null,X)|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2875() { checkNotSubtype("X<(null,X)|null>","({null f1},null)"); }
	@Test @Ignore public void test_2876() { checkNotSubtype("X<(null,X)|null>","({null f2},null)"); }
	@Test @Ignore public void test_2877() { checkIsSubtype("X<(null,X)|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2878() { checkIsSubtype("X<(null,X)|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2879() { checkNotSubtype("X<(null,X)|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2880() { checkNotSubtype("X<(null,X)|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2881() { checkNotSubtype("X<(null,X)|null>","({null f1},null)"); }
	@Test @Ignore public void test_2882() { checkNotSubtype("X<(null,X)|null>","({null f2},null)"); }
	@Test @Ignore public void test_2883() { checkNotSubtype("X<(null,X)|null>","(null,{null f1})"); }
	@Test @Ignore public void test_2884() { checkNotSubtype("X<(null,X)|null>","(null,{null f2})"); }
	@Test @Ignore public void test_2885() { checkNotSubtype("X<(null,X)|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2886() { checkNotSubtype("X<(null,X)|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2887() { checkIsSubtype("X<(null,X)|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2888() { checkIsSubtype("X<(null,X)|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2889() { checkNotSubtype("X<(null,X)|null>","{{null f1} f1}"); }
	@Test @Ignore public void test_2890() { checkNotSubtype("X<(null,X)|null>","{{null f2} f1}"); }
	@Test @Ignore public void test_2891() { checkNotSubtype("X<(null,X)|null>","{{null f1} f2}"); }
	@Test @Ignore public void test_2892() { checkNotSubtype("X<(null,X)|null>","{{null f2} f2}"); }
	@Test @Ignore public void test_2893() { checkNotSubtype("X<(null,X)|null>","X<{X|null f1}>"); }
	@Test @Ignore public void test_2894() { checkNotSubtype("X<(null,X)|null>","X<{null|X f1}>"); }
	@Test @Ignore public void test_2895() { checkNotSubtype("X<(null,X)|null>","X<{null|X f2}>"); }
	@Test @Ignore public void test_2896() { checkNotSubtype("X<(null,X)|null>","X<{X|null f2}>"); }
	@Test @Ignore public void test_2897() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2898() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2899() { checkNotSubtype("X<(null,X)|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2900() { checkIsSubtype("X<(null,X)|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2901() { checkIsSubtype("X<(null,X)|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2902() { checkNotSubtype("X<(null,X)|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2903() { checkNotSubtype("X<(null,X)|null>","null|{null f1}"); }
	@Test @Ignore public void test_2904() { checkNotSubtype("X<(null,X)|null>","null|{null f2}"); }
	@Test @Ignore public void test_2905() { checkNotSubtype("X<(null,X)|null>","{null f1}|null"); }
	@Test @Ignore public void test_2906() { checkNotSubtype("X<(null,X)|null>","{null f2}|null"); }
	@Test @Ignore public void test_2907() { checkNotSubtype("X<(null,X)|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2908() { checkNotSubtype("X<(null,X)|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2909() { checkNotSubtype("X<(null,X)|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2910() { checkNotSubtype("X<(null,X)|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2911() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2912() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2913() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2914() { checkNotSubtype("X<(null,X)|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2915() { checkIsSubtype("X<(null,X)|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2916() { checkIsSubtype("X<(null,X)|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2917() { checkNotSubtype("X<(null,X)|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2918() { checkNotSubtype("X<(null,X)|null>","{null f1}|null"); }
	@Test @Ignore public void test_2919() { checkNotSubtype("X<(null,X)|null>","{null f2}|null"); }
	@Test @Ignore public void test_2920() { checkNotSubtype("X<(null,X)|null>","null|{null f1}"); }
	@Test @Ignore public void test_2921() { checkNotSubtype("X<(null,X)|null>","null|{null f2}"); }
	@Test @Ignore public void test_2922() { checkNotSubtype("X<(null,X)|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2923() { checkNotSubtype("X<(null,X)|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2924() { checkNotSubtype("X<(null,X)|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2925() { checkNotSubtype("X<(null,X)|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2926() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2927() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2928() { checkIsSubtype("X<(null,X)|null>","null"); }
	@Test @Ignore public void test_2929() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2930() { checkNotSubtype("X<null|(null,X)>","{null f1}"); }
	@Test @Ignore public void test_2931() { checkNotSubtype("X<null|(null,X)>","{null f2}"); }
	@Test @Ignore public void test_2932() { checkIsSubtype("X<null|(null,X)>","(null,null)"); }
	@Test @Ignore public void test_2933() { checkIsSubtype("X<null|(null,X)>","(null,null)"); }
	@Test @Ignore public void test_2934() { checkNotSubtype("X<null|(null,X)>","(null,{null f1})"); }
	@Test @Ignore public void test_2935() { checkNotSubtype("X<null|(null,X)>","(null,{null f2})"); }
	@Test @Ignore public void test_2936() { checkNotSubtype("X<null|(null,X)>","({null f1},null)"); }
	@Test @Ignore public void test_2937() { checkNotSubtype("X<null|(null,X)>","({null f2},null)"); }
	@Test @Ignore public void test_2938() { checkIsSubtype("X<null|(null,X)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2939() { checkIsSubtype("X<null|(null,X)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2940() { checkNotSubtype("X<null|(null,X)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2941() { checkNotSubtype("X<null|(null,X)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2942() { checkNotSubtype("X<null|(null,X)>","({null f1},null)"); }
	@Test @Ignore public void test_2943() { checkNotSubtype("X<null|(null,X)>","({null f2},null)"); }
	@Test @Ignore public void test_2944() { checkNotSubtype("X<null|(null,X)>","(null,{null f1})"); }
	@Test @Ignore public void test_2945() { checkNotSubtype("X<null|(null,X)>","(null,{null f2})"); }
	@Test @Ignore public void test_2946() { checkNotSubtype("X<null|(null,X)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_2947() { checkNotSubtype("X<null|(null,X)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_2948() { checkIsSubtype("X<null|(null,X)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_2949() { checkIsSubtype("X<null|(null,X)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_2950() { checkNotSubtype("X<null|(null,X)>","{{null f1} f1}"); }
	@Test @Ignore public void test_2951() { checkNotSubtype("X<null|(null,X)>","{{null f2} f1}"); }
	@Test @Ignore public void test_2952() { checkNotSubtype("X<null|(null,X)>","{{null f1} f2}"); }
	@Test @Ignore public void test_2953() { checkNotSubtype("X<null|(null,X)>","{{null f2} f2}"); }
	@Test @Ignore public void test_2954() { checkNotSubtype("X<null|(null,X)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_2955() { checkNotSubtype("X<null|(null,X)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_2956() { checkNotSubtype("X<null|(null,X)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_2957() { checkNotSubtype("X<null|(null,X)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_2958() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2959() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2960() { checkNotSubtype("X<null|(null,X)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2961() { checkIsSubtype("X<null|(null,X)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2962() { checkIsSubtype("X<null|(null,X)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2963() { checkNotSubtype("X<null|(null,X)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2964() { checkNotSubtype("X<null|(null,X)>","null|{null f1}"); }
	@Test @Ignore public void test_2965() { checkNotSubtype("X<null|(null,X)>","null|{null f2}"); }
	@Test @Ignore public void test_2966() { checkNotSubtype("X<null|(null,X)>","{null f1}|null"); }
	@Test @Ignore public void test_2967() { checkNotSubtype("X<null|(null,X)>","{null f2}|null"); }
	@Test @Ignore public void test_2968() { checkNotSubtype("X<null|(null,X)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2969() { checkNotSubtype("X<null|(null,X)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2970() { checkNotSubtype("X<null|(null,X)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2971() { checkNotSubtype("X<null|(null,X)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2972() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2973() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2974() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2975() { checkNotSubtype("X<null|(null,X)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_2976() { checkIsSubtype("X<null|(null,X)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_2977() { checkIsSubtype("X<null|(null,X)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_2978() { checkNotSubtype("X<null|(null,X)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_2979() { checkNotSubtype("X<null|(null,X)>","{null f1}|null"); }
	@Test @Ignore public void test_2980() { checkNotSubtype("X<null|(null,X)>","{null f2}|null"); }
	@Test @Ignore public void test_2981() { checkNotSubtype("X<null|(null,X)>","null|{null f1}"); }
	@Test @Ignore public void test_2982() { checkNotSubtype("X<null|(null,X)>","null|{null f2}"); }
	@Test @Ignore public void test_2983() { checkNotSubtype("X<null|(null,X)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_2984() { checkNotSubtype("X<null|(null,X)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_2985() { checkNotSubtype("X<null|(null,X)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_2986() { checkNotSubtype("X<null|(null,X)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_2987() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2988() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2989() { checkIsSubtype("X<null|(null,X)>","null"); }
	@Test @Ignore public void test_2990() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_2991() { checkNotSubtype("X<null|(X,null)>","{null f1}"); }
	@Test @Ignore public void test_2992() { checkNotSubtype("X<null|(X,null)>","{null f2}"); }
	@Test @Ignore public void test_2993() { checkIsSubtype("X<null|(X,null)>","(null,null)"); }
	@Test @Ignore public void test_2994() { checkIsSubtype("X<null|(X,null)>","(null,null)"); }
	@Test @Ignore public void test_2995() { checkNotSubtype("X<null|(X,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_2996() { checkNotSubtype("X<null|(X,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_2997() { checkNotSubtype("X<null|(X,null)>","({null f1},null)"); }
	@Test @Ignore public void test_2998() { checkNotSubtype("X<null|(X,null)>","({null f2},null)"); }
	@Test @Ignore public void test_2999() { checkNotSubtype("X<null|(X,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3000() { checkNotSubtype("X<null|(X,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3001() { checkIsSubtype("X<null|(X,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3002() { checkIsSubtype("X<null|(X,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3003() { checkNotSubtype("X<null|(X,null)>","({null f1},null)"); }
	@Test @Ignore public void test_3004() { checkNotSubtype("X<null|(X,null)>","({null f2},null)"); }
	@Test @Ignore public void test_3005() { checkNotSubtype("X<null|(X,null)>","(null,{null f1})"); }
	@Test @Ignore public void test_3006() { checkNotSubtype("X<null|(X,null)>","(null,{null f2})"); }
	@Test @Ignore public void test_3007() { checkIsSubtype("X<null|(X,null)>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3008() { checkIsSubtype("X<null|(X,null)>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3009() { checkNotSubtype("X<null|(X,null)>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3010() { checkNotSubtype("X<null|(X,null)>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3011() { checkNotSubtype("X<null|(X,null)>","{{null f1} f1}"); }
	@Test @Ignore public void test_3012() { checkNotSubtype("X<null|(X,null)>","{{null f2} f1}"); }
	@Test @Ignore public void test_3013() { checkNotSubtype("X<null|(X,null)>","{{null f1} f2}"); }
	@Test @Ignore public void test_3014() { checkNotSubtype("X<null|(X,null)>","{{null f2} f2}"); }
	@Test @Ignore public void test_3015() { checkNotSubtype("X<null|(X,null)>","X<{X|null f1}>"); }
	@Test @Ignore public void test_3016() { checkNotSubtype("X<null|(X,null)>","X<{null|X f1}>"); }
	@Test @Ignore public void test_3017() { checkNotSubtype("X<null|(X,null)>","X<{null|X f2}>"); }
	@Test @Ignore public void test_3018() { checkNotSubtype("X<null|(X,null)>","X<{X|null f2}>"); }
	@Test @Ignore public void test_3019() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_3020() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_3021() { checkIsSubtype("X<null|(X,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3022() { checkNotSubtype("X<null|(X,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3023() { checkNotSubtype("X<null|(X,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3024() { checkIsSubtype("X<null|(X,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3025() { checkNotSubtype("X<null|(X,null)>","null|{null f1}"); }
	@Test @Ignore public void test_3026() { checkNotSubtype("X<null|(X,null)>","null|{null f2}"); }
	@Test @Ignore public void test_3027() { checkNotSubtype("X<null|(X,null)>","{null f1}|null"); }
	@Test @Ignore public void test_3028() { checkNotSubtype("X<null|(X,null)>","{null f2}|null"); }
	@Test @Ignore public void test_3029() { checkNotSubtype("X<null|(X,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3030() { checkNotSubtype("X<null|(X,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3031() { checkNotSubtype("X<null|(X,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3032() { checkNotSubtype("X<null|(X,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3033() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_3034() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_3035() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_3036() { checkIsSubtype("X<null|(X,null)>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3037() { checkNotSubtype("X<null|(X,null)>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3038() { checkNotSubtype("X<null|(X,null)>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3039() { checkIsSubtype("X<null|(X,null)>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3040() { checkNotSubtype("X<null|(X,null)>","{null f1}|null"); }
	@Test @Ignore public void test_3041() { checkNotSubtype("X<null|(X,null)>","{null f2}|null"); }
	@Test @Ignore public void test_3042() { checkNotSubtype("X<null|(X,null)>","null|{null f1}"); }
	@Test @Ignore public void test_3043() { checkNotSubtype("X<null|(X,null)>","null|{null f2}"); }
	@Test @Ignore public void test_3044() { checkNotSubtype("X<null|(X,null)>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3045() { checkNotSubtype("X<null|(X,null)>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3046() { checkNotSubtype("X<null|(X,null)>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3047() { checkNotSubtype("X<null|(X,null)>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3048() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_3049() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_3050() { checkIsSubtype("X<null|(X,null)>","null"); }
	@Test @Ignore public void test_3051() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_3052() { checkIsSubtype("{null f1}|null","{null f1}"); }
	@Test @Ignore public void test_3053() { checkNotSubtype("{null f1}|null","{null f2}"); }
	@Test @Ignore public void test_3054() { checkNotSubtype("{null f1}|null","(null,null)"); }
	@Test @Ignore public void test_3055() { checkNotSubtype("{null f1}|null","(null,null)"); }
	@Test @Ignore public void test_3056() { checkNotSubtype("{null f1}|null","(null,{null f1})"); }
	@Test @Ignore public void test_3057() { checkNotSubtype("{null f1}|null","(null,{null f2})"); }
	@Test @Ignore public void test_3058() { checkNotSubtype("{null f1}|null","({null f1},null)"); }
	@Test @Ignore public void test_3059() { checkNotSubtype("{null f1}|null","({null f2},null)"); }
	@Test @Ignore public void test_3060() { checkNotSubtype("{null f1}|null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3061() { checkNotSubtype("{null f1}|null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3062() { checkNotSubtype("{null f1}|null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3063() { checkNotSubtype("{null f1}|null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3064() { checkNotSubtype("{null f1}|null","({null f1},null)"); }
	@Test @Ignore public void test_3065() { checkNotSubtype("{null f1}|null","({null f2},null)"); }
	@Test @Ignore public void test_3066() { checkNotSubtype("{null f1}|null","(null,{null f1})"); }
	@Test @Ignore public void test_3067() { checkNotSubtype("{null f1}|null","(null,{null f2})"); }
	@Test @Ignore public void test_3068() { checkNotSubtype("{null f1}|null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3069() { checkNotSubtype("{null f1}|null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3070() { checkNotSubtype("{null f1}|null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3071() { checkNotSubtype("{null f1}|null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3072() { checkNotSubtype("{null f1}|null","{{null f1} f1}"); }
	@Test @Ignore public void test_3073() { checkNotSubtype("{null f1}|null","{{null f2} f1}"); }
	@Test @Ignore public void test_3074() { checkNotSubtype("{null f1}|null","{{null f1} f2}"); }
	@Test @Ignore public void test_3075() { checkNotSubtype("{null f1}|null","{{null f2} f2}"); }
	@Test @Ignore public void test_3076() { checkNotSubtype("{null f1}|null","X<{X|null f1}>"); }
	@Test @Ignore public void test_3077() { checkNotSubtype("{null f1}|null","X<{null|X f1}>"); }
	@Test @Ignore public void test_3078() { checkNotSubtype("{null f1}|null","X<{null|X f2}>"); }
	@Test @Ignore public void test_3079() { checkNotSubtype("{null f1}|null","X<{X|null f2}>"); }
	@Test @Ignore public void test_3080() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_3081() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_3082() { checkNotSubtype("{null f1}|null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3083() { checkNotSubtype("{null f1}|null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3084() { checkNotSubtype("{null f1}|null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3085() { checkNotSubtype("{null f1}|null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3086() { checkIsSubtype("{null f1}|null","null|{null f1}"); }
	@Test @Ignore public void test_3087() { checkNotSubtype("{null f1}|null","null|{null f2}"); }
	@Test @Ignore public void test_3088() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test @Ignore public void test_3089() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test @Ignore public void test_3090() { checkNotSubtype("{null f1}|null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3091() { checkNotSubtype("{null f1}|null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3092() { checkNotSubtype("{null f1}|null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3093() { checkNotSubtype("{null f1}|null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3094() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_3095() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_3096() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_3097() { checkNotSubtype("{null f1}|null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3098() { checkNotSubtype("{null f1}|null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3099() { checkNotSubtype("{null f1}|null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3100() { checkNotSubtype("{null f1}|null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3101() { checkIsSubtype("{null f1}|null","{null f1}|null"); }
	@Test @Ignore public void test_3102() { checkNotSubtype("{null f1}|null","{null f2}|null"); }
	@Test @Ignore public void test_3103() { checkIsSubtype("{null f1}|null","null|{null f1}"); }
	@Test @Ignore public void test_3104() { checkNotSubtype("{null f1}|null","null|{null f2}"); }
	@Test @Ignore public void test_3105() { checkNotSubtype("{null f1}|null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3106() { checkNotSubtype("{null f1}|null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3107() { checkNotSubtype("{null f1}|null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3108() { checkNotSubtype("{null f1}|null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3109() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_3110() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_3111() { checkIsSubtype("{null f1}|null","null"); }
	@Test @Ignore public void test_3112() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_3113() { checkNotSubtype("{null f2}|null","{null f1}"); }
	@Test @Ignore public void test_3114() { checkIsSubtype("{null f2}|null","{null f2}"); }
	@Test @Ignore public void test_3115() { checkNotSubtype("{null f2}|null","(null,null)"); }
	@Test @Ignore public void test_3116() { checkNotSubtype("{null f2}|null","(null,null)"); }
	@Test @Ignore public void test_3117() { checkNotSubtype("{null f2}|null","(null,{null f1})"); }
	@Test @Ignore public void test_3118() { checkNotSubtype("{null f2}|null","(null,{null f2})"); }
	@Test @Ignore public void test_3119() { checkNotSubtype("{null f2}|null","({null f1},null)"); }
	@Test @Ignore public void test_3120() { checkNotSubtype("{null f2}|null","({null f2},null)"); }
	@Test @Ignore public void test_3121() { checkNotSubtype("{null f2}|null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3122() { checkNotSubtype("{null f2}|null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3123() { checkNotSubtype("{null f2}|null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3124() { checkNotSubtype("{null f2}|null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3125() { checkNotSubtype("{null f2}|null","({null f1},null)"); }
	@Test @Ignore public void test_3126() { checkNotSubtype("{null f2}|null","({null f2},null)"); }
	@Test @Ignore public void test_3127() { checkNotSubtype("{null f2}|null","(null,{null f1})"); }
	@Test @Ignore public void test_3128() { checkNotSubtype("{null f2}|null","(null,{null f2})"); }
	@Test @Ignore public void test_3129() { checkNotSubtype("{null f2}|null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3130() { checkNotSubtype("{null f2}|null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3131() { checkNotSubtype("{null f2}|null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3132() { checkNotSubtype("{null f2}|null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3133() { checkNotSubtype("{null f2}|null","{{null f1} f1}"); }
	@Test @Ignore public void test_3134() { checkNotSubtype("{null f2}|null","{{null f2} f1}"); }
	@Test @Ignore public void test_3135() { checkNotSubtype("{null f2}|null","{{null f1} f2}"); }
	@Test @Ignore public void test_3136() { checkNotSubtype("{null f2}|null","{{null f2} f2}"); }
	@Test @Ignore public void test_3137() { checkNotSubtype("{null f2}|null","X<{X|null f1}>"); }
	@Test @Ignore public void test_3138() { checkNotSubtype("{null f2}|null","X<{null|X f1}>"); }
	@Test @Ignore public void test_3139() { checkNotSubtype("{null f2}|null","X<{null|X f2}>"); }
	@Test @Ignore public void test_3140() { checkNotSubtype("{null f2}|null","X<{X|null f2}>"); }
	@Test @Ignore public void test_3141() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_3142() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_3143() { checkNotSubtype("{null f2}|null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3144() { checkNotSubtype("{null f2}|null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3145() { checkNotSubtype("{null f2}|null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3146() { checkNotSubtype("{null f2}|null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3147() { checkNotSubtype("{null f2}|null","null|{null f1}"); }
	@Test @Ignore public void test_3148() { checkIsSubtype("{null f2}|null","null|{null f2}"); }
	@Test @Ignore public void test_3149() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test @Ignore public void test_3150() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test @Ignore public void test_3151() { checkNotSubtype("{null f2}|null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3152() { checkNotSubtype("{null f2}|null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3153() { checkNotSubtype("{null f2}|null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3154() { checkNotSubtype("{null f2}|null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3155() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_3156() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_3157() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_3158() { checkNotSubtype("{null f2}|null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3159() { checkNotSubtype("{null f2}|null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3160() { checkNotSubtype("{null f2}|null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3161() { checkNotSubtype("{null f2}|null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3162() { checkNotSubtype("{null f2}|null","{null f1}|null"); }
	@Test @Ignore public void test_3163() { checkIsSubtype("{null f2}|null","{null f2}|null"); }
	@Test @Ignore public void test_3164() { checkNotSubtype("{null f2}|null","null|{null f1}"); }
	@Test @Ignore public void test_3165() { checkIsSubtype("{null f2}|null","null|{null f2}"); }
	@Test @Ignore public void test_3166() { checkNotSubtype("{null f2}|null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3167() { checkNotSubtype("{null f2}|null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3168() { checkNotSubtype("{null f2}|null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3169() { checkNotSubtype("{null f2}|null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3170() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_3171() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_3172() { checkIsSubtype("{null f2}|null","null"); }
	@Test @Ignore public void test_3173() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_3174() { checkIsSubtype("null|{null f1}","{null f1}"); }
	@Test @Ignore public void test_3175() { checkNotSubtype("null|{null f1}","{null f2}"); }
	@Test @Ignore public void test_3176() { checkNotSubtype("null|{null f1}","(null,null)"); }
	@Test @Ignore public void test_3177() { checkNotSubtype("null|{null f1}","(null,null)"); }
	@Test @Ignore public void test_3178() { checkNotSubtype("null|{null f1}","(null,{null f1})"); }
	@Test @Ignore public void test_3179() { checkNotSubtype("null|{null f1}","(null,{null f2})"); }
	@Test @Ignore public void test_3180() { checkNotSubtype("null|{null f1}","({null f1},null)"); }
	@Test @Ignore public void test_3181() { checkNotSubtype("null|{null f1}","({null f2},null)"); }
	@Test @Ignore public void test_3182() { checkNotSubtype("null|{null f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_3183() { checkNotSubtype("null|{null f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_3184() { checkNotSubtype("null|{null f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_3185() { checkNotSubtype("null|{null f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_3186() { checkNotSubtype("null|{null f1}","({null f1},null)"); }
	@Test @Ignore public void test_3187() { checkNotSubtype("null|{null f1}","({null f2},null)"); }
	@Test @Ignore public void test_3188() { checkNotSubtype("null|{null f1}","(null,{null f1})"); }
	@Test @Ignore public void test_3189() { checkNotSubtype("null|{null f1}","(null,{null f2})"); }
	@Test @Ignore public void test_3190() { checkNotSubtype("null|{null f1}","X<(X|null,null)>"); }
	@Test @Ignore public void test_3191() { checkNotSubtype("null|{null f1}","X<(null|X,null)>"); }
	@Test @Ignore public void test_3192() { checkNotSubtype("null|{null f1}","X<(null,null|X)>"); }
	@Test @Ignore public void test_3193() { checkNotSubtype("null|{null f1}","X<(null,X|null)>"); }
	@Test @Ignore public void test_3194() { checkNotSubtype("null|{null f1}","{{null f1} f1}"); }
	@Test @Ignore public void test_3195() { checkNotSubtype("null|{null f1}","{{null f2} f1}"); }
	@Test @Ignore public void test_3196() { checkNotSubtype("null|{null f1}","{{null f1} f2}"); }
	@Test @Ignore public void test_3197() { checkNotSubtype("null|{null f1}","{{null f2} f2}"); }
	@Test @Ignore public void test_3198() { checkNotSubtype("null|{null f1}","X<{X|null f1}>"); }
	@Test @Ignore public void test_3199() { checkNotSubtype("null|{null f1}","X<{null|X f1}>"); }
	@Test @Ignore public void test_3200() { checkNotSubtype("null|{null f1}","X<{null|X f2}>"); }
	@Test @Ignore public void test_3201() { checkNotSubtype("null|{null f1}","X<{X|null f2}>"); }
	@Test @Ignore public void test_3202() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_3203() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_3204() { checkNotSubtype("null|{null f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_3205() { checkNotSubtype("null|{null f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_3206() { checkNotSubtype("null|{null f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_3207() { checkNotSubtype("null|{null f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_3208() { checkIsSubtype("null|{null f1}","null|{null f1}"); }
	@Test @Ignore public void test_3209() { checkNotSubtype("null|{null f1}","null|{null f2}"); }
	@Test @Ignore public void test_3210() { checkIsSubtype("null|{null f1}","{null f1}|null"); }
	@Test @Ignore public void test_3211() { checkNotSubtype("null|{null f1}","{null f2}|null"); }
	@Test @Ignore public void test_3212() { checkNotSubtype("null|{null f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_3213() { checkNotSubtype("null|{null f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_3214() { checkNotSubtype("null|{null f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_3215() { checkNotSubtype("null|{null f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_3216() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_3217() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_3218() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_3219() { checkNotSubtype("null|{null f1}","X<(X,null)|null>"); }
	@Test @Ignore public void test_3220() { checkNotSubtype("null|{null f1}","X<(null,X)|null>"); }
	@Test @Ignore public void test_3221() { checkNotSubtype("null|{null f1}","X<null|(null,X)>"); }
	@Test @Ignore public void test_3222() { checkNotSubtype("null|{null f1}","X<null|(X,null)>"); }
	@Test @Ignore public void test_3223() { checkIsSubtype("null|{null f1}","{null f1}|null"); }
	@Test @Ignore public void test_3224() { checkNotSubtype("null|{null f1}","{null f2}|null"); }
	@Test @Ignore public void test_3225() { checkIsSubtype("null|{null f1}","null|{null f1}"); }
	@Test @Ignore public void test_3226() { checkNotSubtype("null|{null f1}","null|{null f2}"); }
	@Test @Ignore public void test_3227() { checkNotSubtype("null|{null f1}","X<{X f1}|null>"); }
	@Test @Ignore public void test_3228() { checkNotSubtype("null|{null f1}","X<{X f2}|null>"); }
	@Test @Ignore public void test_3229() { checkNotSubtype("null|{null f1}","X<null|{X f1}>"); }
	@Test @Ignore public void test_3230() { checkNotSubtype("null|{null f1}","X<null|{X f2}>"); }
	@Test @Ignore public void test_3231() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_3232() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_3233() { checkIsSubtype("null|{null f1}","null"); }
	@Test @Ignore public void test_3234() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_3235() { checkNotSubtype("null|{null f2}","{null f1}"); }
	@Test @Ignore public void test_3236() { checkIsSubtype("null|{null f2}","{null f2}"); }
	@Test @Ignore public void test_3237() { checkNotSubtype("null|{null f2}","(null,null)"); }
	@Test @Ignore public void test_3238() { checkNotSubtype("null|{null f2}","(null,null)"); }
	@Test @Ignore public void test_3239() { checkNotSubtype("null|{null f2}","(null,{null f1})"); }
	@Test @Ignore public void test_3240() { checkNotSubtype("null|{null f2}","(null,{null f2})"); }
	@Test @Ignore public void test_3241() { checkNotSubtype("null|{null f2}","({null f1},null)"); }
	@Test @Ignore public void test_3242() { checkNotSubtype("null|{null f2}","({null f2},null)"); }
	@Test @Ignore public void test_3243() { checkNotSubtype("null|{null f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_3244() { checkNotSubtype("null|{null f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_3245() { checkNotSubtype("null|{null f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_3246() { checkNotSubtype("null|{null f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_3247() { checkNotSubtype("null|{null f2}","({null f1},null)"); }
	@Test @Ignore public void test_3248() { checkNotSubtype("null|{null f2}","({null f2},null)"); }
	@Test @Ignore public void test_3249() { checkNotSubtype("null|{null f2}","(null,{null f1})"); }
	@Test @Ignore public void test_3250() { checkNotSubtype("null|{null f2}","(null,{null f2})"); }
	@Test @Ignore public void test_3251() { checkNotSubtype("null|{null f2}","X<(X|null,null)>"); }
	@Test @Ignore public void test_3252() { checkNotSubtype("null|{null f2}","X<(null|X,null)>"); }
	@Test @Ignore public void test_3253() { checkNotSubtype("null|{null f2}","X<(null,null|X)>"); }
	@Test @Ignore public void test_3254() { checkNotSubtype("null|{null f2}","X<(null,X|null)>"); }
	@Test @Ignore public void test_3255() { checkNotSubtype("null|{null f2}","{{null f1} f1}"); }
	@Test @Ignore public void test_3256() { checkNotSubtype("null|{null f2}","{{null f2} f1}"); }
	@Test @Ignore public void test_3257() { checkNotSubtype("null|{null f2}","{{null f1} f2}"); }
	@Test @Ignore public void test_3258() { checkNotSubtype("null|{null f2}","{{null f2} f2}"); }
	@Test @Ignore public void test_3259() { checkNotSubtype("null|{null f2}","X<{X|null f1}>"); }
	@Test @Ignore public void test_3260() { checkNotSubtype("null|{null f2}","X<{null|X f1}>"); }
	@Test @Ignore public void test_3261() { checkNotSubtype("null|{null f2}","X<{null|X f2}>"); }
	@Test @Ignore public void test_3262() { checkNotSubtype("null|{null f2}","X<{X|null f2}>"); }
	@Test @Ignore public void test_3263() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_3264() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_3265() { checkNotSubtype("null|{null f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_3266() { checkNotSubtype("null|{null f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_3267() { checkNotSubtype("null|{null f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_3268() { checkNotSubtype("null|{null f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_3269() { checkNotSubtype("null|{null f2}","null|{null f1}"); }
	@Test @Ignore public void test_3270() { checkIsSubtype("null|{null f2}","null|{null f2}"); }
	@Test @Ignore public void test_3271() { checkNotSubtype("null|{null f2}","{null f1}|null"); }
	@Test @Ignore public void test_3272() { checkIsSubtype("null|{null f2}","{null f2}|null"); }
	@Test @Ignore public void test_3273() { checkNotSubtype("null|{null f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_3274() { checkNotSubtype("null|{null f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_3275() { checkNotSubtype("null|{null f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_3276() { checkNotSubtype("null|{null f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_3277() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_3278() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_3279() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_3280() { checkNotSubtype("null|{null f2}","X<(X,null)|null>"); }
	@Test @Ignore public void test_3281() { checkNotSubtype("null|{null f2}","X<(null,X)|null>"); }
	@Test @Ignore public void test_3282() { checkNotSubtype("null|{null f2}","X<null|(null,X)>"); }
	@Test @Ignore public void test_3283() { checkNotSubtype("null|{null f2}","X<null|(X,null)>"); }
	@Test @Ignore public void test_3284() { checkNotSubtype("null|{null f2}","{null f1}|null"); }
	@Test @Ignore public void test_3285() { checkIsSubtype("null|{null f2}","{null f2}|null"); }
	@Test @Ignore public void test_3286() { checkNotSubtype("null|{null f2}","null|{null f1}"); }
	@Test @Ignore public void test_3287() { checkIsSubtype("null|{null f2}","null|{null f2}"); }
	@Test @Ignore public void test_3288() { checkNotSubtype("null|{null f2}","X<{X f1}|null>"); }
	@Test @Ignore public void test_3289() { checkNotSubtype("null|{null f2}","X<{X f2}|null>"); }
	@Test @Ignore public void test_3290() { checkNotSubtype("null|{null f2}","X<null|{X f1}>"); }
	@Test @Ignore public void test_3291() { checkNotSubtype("null|{null f2}","X<null|{X f2}>"); }
	@Test @Ignore public void test_3292() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_3293() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_3294() { checkIsSubtype("null|{null f2}","null"); }
	@Test @Ignore public void test_3295() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_3296() { checkIsSubtype("X<{X f1}|null>","{null f1}"); }
	@Test @Ignore public void test_3297() { checkNotSubtype("X<{X f1}|null>","{null f2}"); }
	@Test @Ignore public void test_3298() { checkNotSubtype("X<{X f1}|null>","(null,null)"); }
	@Test @Ignore public void test_3299() { checkNotSubtype("X<{X f1}|null>","(null,null)"); }
	@Test @Ignore public void test_3300() { checkNotSubtype("X<{X f1}|null>","(null,{null f1})"); }
	@Test @Ignore public void test_3301() { checkNotSubtype("X<{X f1}|null>","(null,{null f2})"); }
	@Test @Ignore public void test_3302() { checkNotSubtype("X<{X f1}|null>","({null f1},null)"); }
	@Test @Ignore public void test_3303() { checkNotSubtype("X<{X f1}|null>","({null f2},null)"); }
	@Test @Ignore public void test_3304() { checkNotSubtype("X<{X f1}|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3305() { checkNotSubtype("X<{X f1}|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3306() { checkNotSubtype("X<{X f1}|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3307() { checkNotSubtype("X<{X f1}|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3308() { checkNotSubtype("X<{X f1}|null>","({null f1},null)"); }
	@Test @Ignore public void test_3309() { checkNotSubtype("X<{X f1}|null>","({null f2},null)"); }
	@Test @Ignore public void test_3310() { checkNotSubtype("X<{X f1}|null>","(null,{null f1})"); }
	@Test @Ignore public void test_3311() { checkNotSubtype("X<{X f1}|null>","(null,{null f2})"); }
	@Test @Ignore public void test_3312() { checkNotSubtype("X<{X f1}|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3313() { checkNotSubtype("X<{X f1}|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3314() { checkNotSubtype("X<{X f1}|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3315() { checkNotSubtype("X<{X f1}|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3316() { checkIsSubtype("X<{X f1}|null>","{{null f1} f1}"); }
	@Test @Ignore public void test_3317() { checkNotSubtype("X<{X f1}|null>","{{null f2} f1}"); }
	@Test @Ignore public void test_3318() { checkNotSubtype("X<{X f1}|null>","{{null f1} f2}"); }
	@Test @Ignore public void test_3319() { checkNotSubtype("X<{X f1}|null>","{{null f2} f2}"); }
	@Test @Ignore public void test_3320() { checkIsSubtype("X<{X f1}|null>","X<{X|null f1}>"); }
	@Test @Ignore public void test_3321() { checkIsSubtype("X<{X f1}|null>","X<{null|X f1}>"); }
	@Test @Ignore public void test_3322() { checkNotSubtype("X<{X f1}|null>","X<{null|X f2}>"); }
	@Test @Ignore public void test_3323() { checkNotSubtype("X<{X f1}|null>","X<{X|null f2}>"); }
	@Test @Ignore public void test_3324() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_3325() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_3326() { checkNotSubtype("X<{X f1}|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3327() { checkNotSubtype("X<{X f1}|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3328() { checkNotSubtype("X<{X f1}|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3329() { checkNotSubtype("X<{X f1}|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3330() { checkIsSubtype("X<{X f1}|null>","null|{null f1}"); }
	@Test @Ignore public void test_3331() { checkNotSubtype("X<{X f1}|null>","null|{null f2}"); }
	@Test @Ignore public void test_3332() { checkIsSubtype("X<{X f1}|null>","{null f1}|null"); }
	@Test @Ignore public void test_3333() { checkNotSubtype("X<{X f1}|null>","{null f2}|null"); }
	@Test @Ignore public void test_3334() { checkIsSubtype("X<{X f1}|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3335() { checkNotSubtype("X<{X f1}|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3336() { checkIsSubtype("X<{X f1}|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3337() { checkNotSubtype("X<{X f1}|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3338() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_3339() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_3340() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_3341() { checkNotSubtype("X<{X f1}|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3342() { checkNotSubtype("X<{X f1}|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3343() { checkNotSubtype("X<{X f1}|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3344() { checkNotSubtype("X<{X f1}|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3345() { checkIsSubtype("X<{X f1}|null>","{null f1}|null"); }
	@Test @Ignore public void test_3346() { checkNotSubtype("X<{X f1}|null>","{null f2}|null"); }
	@Test @Ignore public void test_3347() { checkIsSubtype("X<{X f1}|null>","null|{null f1}"); }
	@Test @Ignore public void test_3348() { checkNotSubtype("X<{X f1}|null>","null|{null f2}"); }
	@Test @Ignore public void test_3349() { checkIsSubtype("X<{X f1}|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3350() { checkNotSubtype("X<{X f1}|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3351() { checkIsSubtype("X<{X f1}|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3352() { checkNotSubtype("X<{X f1}|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3353() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_3354() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_3355() { checkIsSubtype("X<{X f1}|null>","null"); }
	@Test @Ignore public void test_3356() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_3357() { checkNotSubtype("X<{X f2}|null>","{null f1}"); }
	@Test @Ignore public void test_3358() { checkIsSubtype("X<{X f2}|null>","{null f2}"); }
	@Test @Ignore public void test_3359() { checkNotSubtype("X<{X f2}|null>","(null,null)"); }
	@Test @Ignore public void test_3360() { checkNotSubtype("X<{X f2}|null>","(null,null)"); }
	@Test @Ignore public void test_3361() { checkNotSubtype("X<{X f2}|null>","(null,{null f1})"); }
	@Test @Ignore public void test_3362() { checkNotSubtype("X<{X f2}|null>","(null,{null f2})"); }
	@Test @Ignore public void test_3363() { checkNotSubtype("X<{X f2}|null>","({null f1},null)"); }
	@Test @Ignore public void test_3364() { checkNotSubtype("X<{X f2}|null>","({null f2},null)"); }
	@Test @Ignore public void test_3365() { checkNotSubtype("X<{X f2}|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3366() { checkNotSubtype("X<{X f2}|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3367() { checkNotSubtype("X<{X f2}|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3368() { checkNotSubtype("X<{X f2}|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3369() { checkNotSubtype("X<{X f2}|null>","({null f1},null)"); }
	@Test @Ignore public void test_3370() { checkNotSubtype("X<{X f2}|null>","({null f2},null)"); }
	@Test @Ignore public void test_3371() { checkNotSubtype("X<{X f2}|null>","(null,{null f1})"); }
	@Test @Ignore public void test_3372() { checkNotSubtype("X<{X f2}|null>","(null,{null f2})"); }
	@Test @Ignore public void test_3373() { checkNotSubtype("X<{X f2}|null>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3374() { checkNotSubtype("X<{X f2}|null>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3375() { checkNotSubtype("X<{X f2}|null>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3376() { checkNotSubtype("X<{X f2}|null>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3377() { checkNotSubtype("X<{X f2}|null>","{{null f1} f1}"); }
	@Test @Ignore public void test_3378() { checkNotSubtype("X<{X f2}|null>","{{null f2} f1}"); }
	@Test @Ignore public void test_3379() { checkNotSubtype("X<{X f2}|null>","{{null f1} f2}"); }
	@Test @Ignore public void test_3380() { checkIsSubtype("X<{X f2}|null>","{{null f2} f2}"); }
	@Test @Ignore public void test_3381() { checkNotSubtype("X<{X f2}|null>","X<{X|null f1}>"); }
	@Test @Ignore public void test_3382() { checkNotSubtype("X<{X f2}|null>","X<{null|X f1}>"); }
	@Test @Ignore public void test_3383() { checkIsSubtype("X<{X f2}|null>","X<{null|X f2}>"); }
	@Test @Ignore public void test_3384() { checkIsSubtype("X<{X f2}|null>","X<{X|null f2}>"); }
	@Test @Ignore public void test_3385() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_3386() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_3387() { checkNotSubtype("X<{X f2}|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3388() { checkNotSubtype("X<{X f2}|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3389() { checkNotSubtype("X<{X f2}|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3390() { checkNotSubtype("X<{X f2}|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3391() { checkNotSubtype("X<{X f2}|null>","null|{null f1}"); }
	@Test @Ignore public void test_3392() { checkIsSubtype("X<{X f2}|null>","null|{null f2}"); }
	@Test @Ignore public void test_3393() { checkNotSubtype("X<{X f2}|null>","{null f1}|null"); }
	@Test @Ignore public void test_3394() { checkIsSubtype("X<{X f2}|null>","{null f2}|null"); }
	@Test @Ignore public void test_3395() { checkNotSubtype("X<{X f2}|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3396() { checkIsSubtype("X<{X f2}|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3397() { checkNotSubtype("X<{X f2}|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3398() { checkIsSubtype("X<{X f2}|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3399() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_3400() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_3401() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_3402() { checkNotSubtype("X<{X f2}|null>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3403() { checkNotSubtype("X<{X f2}|null>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3404() { checkNotSubtype("X<{X f2}|null>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3405() { checkNotSubtype("X<{X f2}|null>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3406() { checkNotSubtype("X<{X f2}|null>","{null f1}|null"); }
	@Test @Ignore public void test_3407() { checkIsSubtype("X<{X f2}|null>","{null f2}|null"); }
	@Test @Ignore public void test_3408() { checkNotSubtype("X<{X f2}|null>","null|{null f1}"); }
	@Test @Ignore public void test_3409() { checkIsSubtype("X<{X f2}|null>","null|{null f2}"); }
	@Test @Ignore public void test_3410() { checkNotSubtype("X<{X f2}|null>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3411() { checkIsSubtype("X<{X f2}|null>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3412() { checkNotSubtype("X<{X f2}|null>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3413() { checkIsSubtype("X<{X f2}|null>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3414() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_3415() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_3416() { checkIsSubtype("X<{X f2}|null>","null"); }
	@Test @Ignore public void test_3417() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_3418() { checkIsSubtype("X<null|{X f1}>","{null f1}"); }
	@Test @Ignore public void test_3419() { checkNotSubtype("X<null|{X f1}>","{null f2}"); }
	@Test @Ignore public void test_3420() { checkNotSubtype("X<null|{X f1}>","(null,null)"); }
	@Test @Ignore public void test_3421() { checkNotSubtype("X<null|{X f1}>","(null,null)"); }
	@Test @Ignore public void test_3422() { checkNotSubtype("X<null|{X f1}>","(null,{null f1})"); }
	@Test @Ignore public void test_3423() { checkNotSubtype("X<null|{X f1}>","(null,{null f2})"); }
	@Test @Ignore public void test_3424() { checkNotSubtype("X<null|{X f1}>","({null f1},null)"); }
	@Test @Ignore public void test_3425() { checkNotSubtype("X<null|{X f1}>","({null f2},null)"); }
	@Test @Ignore public void test_3426() { checkNotSubtype("X<null|{X f1}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3427() { checkNotSubtype("X<null|{X f1}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3428() { checkNotSubtype("X<null|{X f1}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3429() { checkNotSubtype("X<null|{X f1}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3430() { checkNotSubtype("X<null|{X f1}>","({null f1},null)"); }
	@Test @Ignore public void test_3431() { checkNotSubtype("X<null|{X f1}>","({null f2},null)"); }
	@Test @Ignore public void test_3432() { checkNotSubtype("X<null|{X f1}>","(null,{null f1})"); }
	@Test @Ignore public void test_3433() { checkNotSubtype("X<null|{X f1}>","(null,{null f2})"); }
	@Test @Ignore public void test_3434() { checkNotSubtype("X<null|{X f1}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3435() { checkNotSubtype("X<null|{X f1}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3436() { checkNotSubtype("X<null|{X f1}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3437() { checkNotSubtype("X<null|{X f1}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3438() { checkIsSubtype("X<null|{X f1}>","{{null f1} f1}"); }
	@Test @Ignore public void test_3439() { checkNotSubtype("X<null|{X f1}>","{{null f2} f1}"); }
	@Test @Ignore public void test_3440() { checkNotSubtype("X<null|{X f1}>","{{null f1} f2}"); }
	@Test @Ignore public void test_3441() { checkNotSubtype("X<null|{X f1}>","{{null f2} f2}"); }
	@Test @Ignore public void test_3442() { checkIsSubtype("X<null|{X f1}>","X<{X|null f1}>"); }
	@Test @Ignore public void test_3443() { checkIsSubtype("X<null|{X f1}>","X<{null|X f1}>"); }
	@Test @Ignore public void test_3444() { checkNotSubtype("X<null|{X f1}>","X<{null|X f2}>"); }
	@Test @Ignore public void test_3445() { checkNotSubtype("X<null|{X f1}>","X<{X|null f2}>"); }
	@Test @Ignore public void test_3446() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_3447() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_3448() { checkNotSubtype("X<null|{X f1}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3449() { checkNotSubtype("X<null|{X f1}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3450() { checkNotSubtype("X<null|{X f1}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3451() { checkNotSubtype("X<null|{X f1}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3452() { checkIsSubtype("X<null|{X f1}>","null|{null f1}"); }
	@Test @Ignore public void test_3453() { checkNotSubtype("X<null|{X f1}>","null|{null f2}"); }
	@Test @Ignore public void test_3454() { checkIsSubtype("X<null|{X f1}>","{null f1}|null"); }
	@Test @Ignore public void test_3455() { checkNotSubtype("X<null|{X f1}>","{null f2}|null"); }
	@Test @Ignore public void test_3456() { checkIsSubtype("X<null|{X f1}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3457() { checkNotSubtype("X<null|{X f1}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3458() { checkIsSubtype("X<null|{X f1}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3459() { checkNotSubtype("X<null|{X f1}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3460() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_3461() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_3462() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_3463() { checkNotSubtype("X<null|{X f1}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3464() { checkNotSubtype("X<null|{X f1}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3465() { checkNotSubtype("X<null|{X f1}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3466() { checkNotSubtype("X<null|{X f1}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3467() { checkIsSubtype("X<null|{X f1}>","{null f1}|null"); }
	@Test @Ignore public void test_3468() { checkNotSubtype("X<null|{X f1}>","{null f2}|null"); }
	@Test @Ignore public void test_3469() { checkIsSubtype("X<null|{X f1}>","null|{null f1}"); }
	@Test @Ignore public void test_3470() { checkNotSubtype("X<null|{X f1}>","null|{null f2}"); }
	@Test @Ignore public void test_3471() { checkIsSubtype("X<null|{X f1}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3472() { checkNotSubtype("X<null|{X f1}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3473() { checkIsSubtype("X<null|{X f1}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3474() { checkNotSubtype("X<null|{X f1}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3475() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_3476() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_3477() { checkIsSubtype("X<null|{X f1}>","null"); }
	@Test @Ignore public void test_3478() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_3479() { checkNotSubtype("X<null|{X f2}>","{null f1}"); }
	@Test @Ignore public void test_3480() { checkIsSubtype("X<null|{X f2}>","{null f2}"); }
	@Test @Ignore public void test_3481() { checkNotSubtype("X<null|{X f2}>","(null,null)"); }
	@Test @Ignore public void test_3482() { checkNotSubtype("X<null|{X f2}>","(null,null)"); }
	@Test @Ignore public void test_3483() { checkNotSubtype("X<null|{X f2}>","(null,{null f1})"); }
	@Test @Ignore public void test_3484() { checkNotSubtype("X<null|{X f2}>","(null,{null f2})"); }
	@Test @Ignore public void test_3485() { checkNotSubtype("X<null|{X f2}>","({null f1},null)"); }
	@Test @Ignore public void test_3486() { checkNotSubtype("X<null|{X f2}>","({null f2},null)"); }
	@Test @Ignore public void test_3487() { checkNotSubtype("X<null|{X f2}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3488() { checkNotSubtype("X<null|{X f2}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3489() { checkNotSubtype("X<null|{X f2}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3490() { checkNotSubtype("X<null|{X f2}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3491() { checkNotSubtype("X<null|{X f2}>","({null f1},null)"); }
	@Test @Ignore public void test_3492() { checkNotSubtype("X<null|{X f2}>","({null f2},null)"); }
	@Test @Ignore public void test_3493() { checkNotSubtype("X<null|{X f2}>","(null,{null f1})"); }
	@Test @Ignore public void test_3494() { checkNotSubtype("X<null|{X f2}>","(null,{null f2})"); }
	@Test @Ignore public void test_3495() { checkNotSubtype("X<null|{X f2}>","X<(X|null,null)>"); }
	@Test @Ignore public void test_3496() { checkNotSubtype("X<null|{X f2}>","X<(null|X,null)>"); }
	@Test @Ignore public void test_3497() { checkNotSubtype("X<null|{X f2}>","X<(null,null|X)>"); }
	@Test @Ignore public void test_3498() { checkNotSubtype("X<null|{X f2}>","X<(null,X|null)>"); }
	@Test @Ignore public void test_3499() { checkNotSubtype("X<null|{X f2}>","{{null f1} f1}"); }
	@Test @Ignore public void test_3500() { checkNotSubtype("X<null|{X f2}>","{{null f2} f1}"); }
	@Test @Ignore public void test_3501() { checkNotSubtype("X<null|{X f2}>","{{null f1} f2}"); }
	@Test @Ignore public void test_3502() { checkIsSubtype("X<null|{X f2}>","{{null f2} f2}"); }
	@Test @Ignore public void test_3503() { checkNotSubtype("X<null|{X f2}>","X<{X|null f1}>"); }
	@Test @Ignore public void test_3504() { checkNotSubtype("X<null|{X f2}>","X<{null|X f1}>"); }
	@Test @Ignore public void test_3505() { checkIsSubtype("X<null|{X f2}>","X<{null|X f2}>"); }
	@Test @Ignore public void test_3506() { checkIsSubtype("X<null|{X f2}>","X<{X|null f2}>"); }
	@Test @Ignore public void test_3507() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_3508() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_3509() { checkNotSubtype("X<null|{X f2}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3510() { checkNotSubtype("X<null|{X f2}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3511() { checkNotSubtype("X<null|{X f2}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3512() { checkNotSubtype("X<null|{X f2}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3513() { checkNotSubtype("X<null|{X f2}>","null|{null f1}"); }
	@Test @Ignore public void test_3514() { checkIsSubtype("X<null|{X f2}>","null|{null f2}"); }
	@Test @Ignore public void test_3515() { checkNotSubtype("X<null|{X f2}>","{null f1}|null"); }
	@Test @Ignore public void test_3516() { checkIsSubtype("X<null|{X f2}>","{null f2}|null"); }
	@Test @Ignore public void test_3517() { checkNotSubtype("X<null|{X f2}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3518() { checkIsSubtype("X<null|{X f2}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3519() { checkNotSubtype("X<null|{X f2}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3520() { checkIsSubtype("X<null|{X f2}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3521() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_3522() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_3523() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_3524() { checkNotSubtype("X<null|{X f2}>","X<(X,null)|null>"); }
	@Test @Ignore public void test_3525() { checkNotSubtype("X<null|{X f2}>","X<(null,X)|null>"); }
	@Test @Ignore public void test_3526() { checkNotSubtype("X<null|{X f2}>","X<null|(null,X)>"); }
	@Test @Ignore public void test_3527() { checkNotSubtype("X<null|{X f2}>","X<null|(X,null)>"); }
	@Test @Ignore public void test_3528() { checkNotSubtype("X<null|{X f2}>","{null f1}|null"); }
	@Test @Ignore public void test_3529() { checkIsSubtype("X<null|{X f2}>","{null f2}|null"); }
	@Test @Ignore public void test_3530() { checkNotSubtype("X<null|{X f2}>","null|{null f1}"); }
	@Test @Ignore public void test_3531() { checkIsSubtype("X<null|{X f2}>","null|{null f2}"); }
	@Test @Ignore public void test_3532() { checkNotSubtype("X<null|{X f2}>","X<{X f1}|null>"); }
	@Test @Ignore public void test_3533() { checkIsSubtype("X<null|{X f2}>","X<{X f2}|null>"); }
	@Test @Ignore public void test_3534() { checkNotSubtype("X<null|{X f2}>","X<null|{X f1}>"); }
	@Test @Ignore public void test_3535() { checkIsSubtype("X<null|{X f2}>","X<null|{X f2}>"); }
	@Test @Ignore public void test_3536() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_3537() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_3538() { checkIsSubtype("X<null|{X f2}>","null"); }
	@Test @Ignore public void test_3539() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3540() { checkNotSubtype("null","{null f1}"); }
	@Test @Ignore public void test_3541() { checkNotSubtype("null","{null f2}"); }
	@Test @Ignore public void test_3542() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_3543() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_3544() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_3545() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_3546() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_3547() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_3548() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3549() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3550() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3551() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3552() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_3553() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_3554() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_3555() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_3556() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3557() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3558() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3559() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3560() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test @Ignore public void test_3561() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test @Ignore public void test_3562() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test @Ignore public void test_3563() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test @Ignore public void test_3564() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test @Ignore public void test_3565() { checkNotSubtype("null","X<{null|X f1}>"); }
	@Test @Ignore public void test_3566() { checkNotSubtype("null","X<{null|X f2}>"); }
	@Test @Ignore public void test_3567() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test @Ignore public void test_3568() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3569() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3570() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3571() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3572() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3573() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3574() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_3575() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_3576() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_3577() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_3578() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3579() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3580() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3581() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3582() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3583() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3584() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3585() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3586() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3587() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3588() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3589() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_3590() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_3591() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_3592() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_3593() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3594() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3595() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3596() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3597() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3598() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3599() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3600() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3601() { checkNotSubtype("null","{null f1}"); }
	@Test @Ignore public void test_3602() { checkNotSubtype("null","{null f2}"); }
	@Test @Ignore public void test_3603() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_3604() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_3605() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_3606() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_3607() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_3608() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_3609() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3610() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3611() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3612() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3613() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_3614() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_3615() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_3616() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_3617() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3618() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3619() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3620() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3621() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test @Ignore public void test_3622() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test @Ignore public void test_3623() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test @Ignore public void test_3624() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test @Ignore public void test_3625() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test @Ignore public void test_3626() { checkNotSubtype("null","X<{null|X f1}>"); }
	@Test @Ignore public void test_3627() { checkNotSubtype("null","X<{null|X f2}>"); }
	@Test @Ignore public void test_3628() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test @Ignore public void test_3629() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3630() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3631() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3632() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3633() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3634() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3635() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_3636() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_3637() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_3638() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_3639() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3640() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3641() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3642() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3643() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3644() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3645() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3646() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3647() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3648() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3649() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3650() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_3651() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_3652() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_3653() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_3654() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3655() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3656() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3657() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3658() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3659() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3660() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3661() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3662() { checkNotSubtype("null","{null f1}"); }
	@Test @Ignore public void test_3663() { checkNotSubtype("null","{null f2}"); }
	@Test @Ignore public void test_3664() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_3665() { checkNotSubtype("null","(null,null)"); }
	@Test @Ignore public void test_3666() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_3667() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_3668() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_3669() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_3670() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3671() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3672() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3673() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3674() { checkNotSubtype("null","({null f1},null)"); }
	@Test @Ignore public void test_3675() { checkNotSubtype("null","({null f2},null)"); }
	@Test @Ignore public void test_3676() { checkNotSubtype("null","(null,{null f1})"); }
	@Test @Ignore public void test_3677() { checkNotSubtype("null","(null,{null f2})"); }
	@Test @Ignore public void test_3678() { checkNotSubtype("null","X<(X|null,null)>"); }
	@Test @Ignore public void test_3679() { checkNotSubtype("null","X<(null|X,null)>"); }
	@Test @Ignore public void test_3680() { checkNotSubtype("null","X<(null,null|X)>"); }
	@Test @Ignore public void test_3681() { checkNotSubtype("null","X<(null,X|null)>"); }
	@Test @Ignore public void test_3682() { checkNotSubtype("null","{{null f1} f1}"); }
	@Test @Ignore public void test_3683() { checkNotSubtype("null","{{null f2} f1}"); }
	@Test @Ignore public void test_3684() { checkNotSubtype("null","{{null f1} f2}"); }
	@Test @Ignore public void test_3685() { checkNotSubtype("null","{{null f2} f2}"); }
	@Test @Ignore public void test_3686() { checkNotSubtype("null","X<{X|null f1}>"); }
	@Test @Ignore public void test_3687() { checkNotSubtype("null","X<{null|X f1}>"); }
	@Test @Ignore public void test_3688() { checkNotSubtype("null","X<{null|X f2}>"); }
	@Test @Ignore public void test_3689() { checkNotSubtype("null","X<{X|null f2}>"); }
	@Test @Ignore public void test_3690() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3691() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3692() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3693() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3694() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3695() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3696() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_3697() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_3698() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_3699() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_3700() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3701() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3702() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3703() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3704() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3705() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3706() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3707() { checkNotSubtype("null","X<(X,null)|null>"); }
	@Test @Ignore public void test_3708() { checkNotSubtype("null","X<(null,X)|null>"); }
	@Test @Ignore public void test_3709() { checkNotSubtype("null","X<null|(null,X)>"); }
	@Test @Ignore public void test_3710() { checkNotSubtype("null","X<null|(X,null)>"); }
	@Test @Ignore public void test_3711() { checkNotSubtype("null","{null f1}|null"); }
	@Test @Ignore public void test_3712() { checkNotSubtype("null","{null f2}|null"); }
	@Test @Ignore public void test_3713() { checkNotSubtype("null","null|{null f1}"); }
	@Test @Ignore public void test_3714() { checkNotSubtype("null","null|{null f2}"); }
	@Test @Ignore public void test_3715() { checkNotSubtype("null","X<{X f1}|null>"); }
	@Test @Ignore public void test_3716() { checkNotSubtype("null","X<{X f2}|null>"); }
	@Test @Ignore public void test_3717() { checkNotSubtype("null","X<null|{X f1}>"); }
	@Test @Ignore public void test_3718() { checkNotSubtype("null","X<null|{X f2}>"); }
	@Test @Ignore public void test_3719() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3720() { checkIsSubtype("null","null"); }
	@Test @Ignore public void test_3721() { checkIsSubtype("null","null"); }

	private void checkIsSubtype(String from, String to) {
		NameResolver resolver = null;
		SubtypeOperator subtypeOperator = new SubtypeOperator(resolver,
				new RelaxedTypeEmptinessTest(resolver));
		Type ft = TestUtils.fromString(from);
		Type tt = TestUtils.fromString(to);
		try {
			assertTrue(subtypeOperator.isSubtype(ft,tt,null));
		} catch(NameResolver.ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	private void checkNotSubtype(String from, String to) {
		NameResolver resolver = null;
		SubtypeOperator subtypeOperator = new SubtypeOperator(resolver,
				new RelaxedTypeEmptinessTest(resolver));
		Type ft = TestUtils.fromString(from);
		Type tt = TestUtils.fromString(to);
		try {
			assertFalse(subtypeOperator.isSubtype(ft,tt,null));
		} catch(NameResolver.ResolutionError e) {
			throw new RuntimeException(e);
		}
	}
}
